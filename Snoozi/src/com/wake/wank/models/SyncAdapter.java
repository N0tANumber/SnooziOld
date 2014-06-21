package com.wake.wank.models;

import java.io.File;
import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.snoozi.trackingeventendpoint.Trackingeventendpoint;
import com.snoozi.trackingeventendpoint.model.TrackingEvent;
import com.snoozi.videoendpoint.Videoendpoint;
import com.snoozi.videoendpoint.model.CollectionResponseVideo;
import com.snoozi.videoendpoint.model.Video;
import com.wake.wank.GCMIntentService;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.receivers.VideoDownloadReceiver;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;


/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	// Global variables
	// Define a variable to contain a content resolver instance
	ContentResolver m_ContentResolver;

	private static boolean isRegistationPending = false;

	// An account type, in the form of a domain name
	private static final String ACCOUNT_TYPE = "com.wake";
	// The account name
	private static final String ACCOUNT = "Wakeaccount";

	

	/**
	 * Set up the sync adapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		m_ContentResolver = context.getContentResolver();
	}


	/**
	 * Set up the sync adapter. This form of the
	 * constructor maintains compatibility with Android 3.0
	 * and later platform versions
	 */
	public SyncAdapter(
			Context context,
			boolean autoInitialize,
			boolean allowParallelSyncs) 
	{
		super(context, autoInitialize);
		/*
		 * If your app uses a content resolver, get an instance of it
		 * from the incoming Context
		 */
		m_ContentResolver = context.getContentResolver();

	}
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) 
	{
		
		SharedPreferences settings =this.getContext().getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		boolean isRegistered = settings.getBoolean("isRegistered", false);
		
		if(!isRegistered && !isRegistationPending)
		{
			SnooziUtility.trace(this.getContext(),TRACETYPE.INFO,"isRegistered : " + isRegistered);
			//Not registered yet so Registering Google cloud messaging
			try {
				GCMIntentService.register(this.getContext());
				isRegistationPending = true;
			} catch (Exception e) 
			{
				//probleme during GCM registration
				SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"GCM Registration error :  " +  e.toString());
			}
		}
		
		//Getting the extras and looking for message extra to know what to do next
		String action=null;
		if(extras != null)
			action = extras.getString("action");
		
		if(action==null)
			action = SnooziUtility.SYNC_ACTION.SEND_DATA;
		
		
		SnooziUtility.trace(this.getContext(), TRACETYPE.DEBUG, action);
		
		if( action.equals(SnooziUtility.SYNC_ACTION.NEW_VIDEO_AVAILABLE))
		{
			//We get all recent video from the server
			this.retrieveRecentVideo(provider);
			
		}else if( action.equals(SnooziUtility.SYNC_ACTION.SEND_DATA))
		{
			// we send all data to the server if any
			this.sendTrackingEvent(provider);
		}else if( action.equals(SnooziUtility.SYNC_ACTION.SEND_RATING))
		{
			//We send a user rating
			int addedViewcount = extras.getInt("addedViewcount");
			int addedLike = extras.getInt("addedLike");
			long videoid = extras.getLong("videoid");
			
			this.sendRatingData(videoid, addedViewcount, addedLike);
			
			this.cleanupOldVideo(provider);
		}
	}


	
	private boolean cleanupOldVideo(ContentProviderClient provider) {
		Cursor cursor = null;
		int maxKeptVideo = 5;
		boolean success = false;
		try {
			//We check all tracking event in Contentproviders
			cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ID, SnooziContract.videos.Columns.MYVIEWCOUNT + " > 0 AND " + SnooziContract.videos.Columns.FILESTATUS + " != ?",new String[]{"DUMMY"},  SnooziContract.videos.SORT_ORDER_DEFAULT);

			int counter = 0;
			if (cursor.moveToFirst()) 
			{
				counter++;
				if(counter > maxKeptVideo)
				{
					//We dont delete the first maxKeptVideo videos
					do {
						int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID));
						String localurl = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.LOCALURL));
					
						File file = new File(localurl);
						file.delete(); 
						
						if(counter > 50)
						{
							//We delete the database entry
							// Deleting the old Video 
							provider.delete(SnooziContract.videos.CONTENT_URI, SnooziContract.trackingevents.Columns._ID + " = ? ", new String[]{String.valueOf(id)});
							SnooziUtility.trace(this.getContext(), TRACETYPE.DEBUG,"cleanupOldVideo - Permanently Deleted video :  " +  id);
						}else
						{
							// We only place the video filestatus as "DELETED"
							ContentValues values = new ContentValues();
							values.put(SnooziContract.videos.Columns.FILESTATUS,"DELETED" );
							provider.update(ContentUris.withAppendedId(SnooziContract.videos.CONTENT_URI, id), values,null,null);
							
							SnooziUtility.trace(this.getContext(), TRACETYPE.DEBUG,"cleanupOldVideo - Deleted video :  " +  id);
						}

					} while (cursor.moveToNext());
				}
			}
			success = true;

		} catch (Exception e) {
			SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"SyncAdapter Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}

		return success;
		
	}


	/**
	 * Send all pending trackingEvent to the server
	 * @param provider
	 */
	private boolean sendTrackingEvent(ContentProviderClient provider) {
		Cursor cursor = null;
		
		boolean success = false;
		try {
			//We check all tracking event in Contentproviders
			cursor = provider.query(SnooziContract.trackingevents.CONTENT_URI, SnooziContract.trackingevents.PROJECTION_ALL, null, null,  null);
			if (cursor.moveToFirst()) 
			{
				// We got some tracking to send
				//Preparing the endpoint for sending all tracking event
				Trackingeventendpoint.Builder endpointBuilder = new Trackingeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(),
						new HttpRequestInitializer() {
							public void initialize(HttpRequest httpRequest) { }
						});
				
				//Building a tracking end for all message
				Trackingeventendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
				String userId = SnooziUtility.getAccountNames(this.getContext());
				String versionName;
				
				/*
				try {
					RemoveErrorLoggerEvent removeresult = endpoint.removeErrorLoggerEvent();
					removeresult.execute();
					SnooziUtility.trace(getContext(), TRACETYPE.INFO,"deletion" + removeresult.toString());
				} catch (Exception e) {
					SnooziUtility.trace(getContext(), TRACETYPE.ERROR, e.toString());
				}*/
				
				try {
					versionName = this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"SyncAdapter NameNotFoundException :  " +  e.toString());
					versionName = "—";
				}
				do {
					// getting the data from the cursor
					int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns._ID));
					long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TIMESTAMP));
					String timestring = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TIMESTRING));
					String description = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.DESCRIPTION));
					String type = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TYPE));
					long videoid = cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.VIDEOID));

					if(description.length() > 500)
						description = description.substring(0, 500);
					//Building the trackingEvent
					TrackingEvent trackingEvent = new TrackingEvent();
					trackingEvent.setUserid(userId)
							.setAndroidVersion(android.os.Build.VERSION.SDK_INT)
							.setDeviceInformation(android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE)
							.setDescription(description)
							.setType(type)
							.setTimestamp(timestamp)
							.setTimeString(timestring)
							.setVideoid(videoid)
							.setApkVersion(versionName);
					TrackingEvent answerEvent;
					if(SnooziUtility.DEV_MODE)
					{
						SnooziUtility.trace(this.getContext(), TRACETYPE.INFO,"## DEV MODE DUMMY## Tracking "+type+" synchronisation complete, id : " + id);
						answerEvent = new TrackingEvent();
					}else
					{
						//Sending the tracking Event
						answerEvent = endpoint.insertTrackingEvent(trackingEvent).execute();
						SnooziUtility.trace(this.getContext(), TRACETYPE.INFO,"Tracking "+type+" synchronisation complete, id : " + id);
					}
					if(answerEvent != null)
					{
						
						// Deleting the Tracking event
						provider.delete(SnooziContract.trackingevents.CONTENT_URI, 
										SnooziContract.trackingevents.Columns._ID + " = ? ", 
										new String[]{String.valueOf(id)});
					}
				} while (cursor.moveToNext());

			}
			success = true;

		} catch (IOException e) {
			//ne pas mettre TYPE"ERROR car sinon il va spammer le server de log error
			SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"SyncAdapter IOException :  " +  e.toString());
		} catch (RemoteException e) {
			SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"SyncAdapter RemoteException :  " +  e.toString());
		} catch (IllegalArgumentException e) {
			SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"SyncAdapter IllegalArgumentException :  " +  e.toString());
		} catch (Exception e) {
			SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"SyncAdapter Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		
		return success;
	}

	
	
	/**
	 * Get if needed all recent video from the server
	 * @param provider
	 * @return
	 */
	private boolean retrieveRecentVideo(ContentProviderClient provider)
	{
		boolean success = true;
		int maxDownloadedVideo = 7 ;
		
		//We check how many video are still waiting not viewed
		Cursor playedcursor = null;
		try {
			playedcursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ID, SnooziContract.videos.Columns.MYVIEWCOUNT + " == 0 AND " + SnooziContract.videos.Columns.FILESTATUS + " != ? AND  " + SnooziContract.videos.Columns.FILESTATUS + " != ? ",new String[]{"ERROR","DUMMY"},  null);
			int notPlayedVideoCount = playedcursor.getCount();
			SnooziUtility.trace(getContext(),TRACETYPE.DEBUG,"in stock : " +  notPlayedVideoCount + " - maxDownloadedVideo : " + maxDownloadedVideo);
			maxDownloadedVideo = maxDownloadedVideo - notPlayedVideoCount;
			// If We have enough video to play, we skill the retrieval of new video
			if(maxDownloadedVideo < 0)
				maxDownloadedVideo = 0;
		} catch (Exception e) {
			SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"retrieveRecentVideo Exception :  " +  e.toString());
			
		}finally
		{
			if(playedcursor != null)
				playedcursor.close();
		}
		
		if(maxDownloadedVideo >0)
		{
			SnooziUtility.trace(this.getContext(), TRACETYPE.INFO,"Getting from server " +  maxDownloadedVideo + " new video");
			// we must call the serveur to get a list of all recent  video
			//Preparing the endpoint for receiving all video
			Videoendpoint.Builder endpointBuilder = new Videoendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(),
					new HttpRequestInitializer() {
						public void initialize(HttpRequest httpRequest) { }
					});
			Videoendpoint videoEndpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
			CollectionResponseVideo videolist = null;
			//Getting a list of all recent video
			try {
				videolist = videoEndpoint.listRecentVideo().setLimit(maxDownloadedVideo).execute();
			} catch (IOException e) {
				SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"GetRecentVideo IOException :  " +  e.toString());
				return false;
			}
			
			// for each video, we synchronise with values in local Database
			Cursor cursor = null;
			for(Video video : videolist.getItems()) 
			{
				SnooziUtility.trace(this.getContext(),TRACETYPE.INFO,"looking in local BDD for video " + video.getUrl());
				//Preparing video data to insert or update ( both case)
				ContentValues values = new ContentValues();
				values.put(SnooziContract.videos.Columns.DESCRIPTION,video.getDescription() );
				values.put(SnooziContract.videos.Columns.LIKE,video.getLike() );
				values.put(SnooziContract.videos.Columns.VIEWCOUNT,video.getViewcount() );
				values.put(SnooziContract.videos.Columns.STATUS,video.getStatus() );
				values.put(SnooziContract.videos.Columns.LEVEL,video.getLevel() );
				
				try 
				{
					// We look in the content provider if we already have that video in stock
					cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.URL + " LIKE ?", new String[]{video.getUrl()},  null);
					
					if(cursor.getCount() == 0)
					{
						//INSERTION OF THE VIDEO
						values.put(SnooziContract.videos.Columns.VIDEOID,video.getId() );
						values.put(SnooziContract.videos.Columns.TIMESTAMP,video.getTimestamp() );
						values.put(SnooziContract.videos.Columns.USERID,video.getUserid() );
						values.put(SnooziContract.videos.Columns.URL,video.getUrl() );
						values.put(SnooziContract.videos.Columns.FILESTATUS,"PENDING" );
						String localUrl = "file://" + this.getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES ).getPath() +"/" +  video.getId() + ".mp4";
						values.put(SnooziContract.videos.Columns.LOCALURL,localUrl);
	
						Uri videouri = provider.insert(SnooziContract.videos.CONTENT_URI, values);
						SnooziUtility.trace(this.getContext(),TRACETYPE.INFO,"INSERTED Video " + video.getId() + " : " + videouri.toString());
					}else
					{
						if (cursor.moveToFirst()) 
						{
							//UPDATE OF THE VIDEO
							int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID));
							String url = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.URL));
							String fileStatus = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.FILESTATUS));
							if(!url.equals(video.getUrl()) || fileStatus.equals("ERROR"))
							{
								//URL has changedor is in error, we must redownload the file
								values.put(SnooziContract.videos.Columns.URL,video.getUrl() );
								String localUrl = "file://" + this.getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES ).getPath() +"/" +  video.getId() + ".mp4";
								values.put(SnooziContract.videos.Columns.LOCALURL,localUrl);
								values.put(SnooziContract.videos.Columns.FILESTATUS,"PENDING" );
							}
						
							//Already present in database, we update the info ( description, like, dislike, viewcount,level)
							int updatecount = provider.update(ContentUris.withAppendedId(SnooziContract.videos.CONTENT_URI, id), values,null,null);
							SnooziUtility.trace(this.getContext(),TRACETYPE.INFO,"UPDATED Video " + video.getId() + " : " + updatecount);
						}
					}
					
				}
				catch (Exception e) {
					SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"retrieveRecentVideo Exception :  " +  e.toString());
					success = false;
				}finally{
					if(cursor != null)
						cursor.close();
				}
			}
		}
		
		
		//Launching Download Manager
		VideoDownloadReceiver.launchingPendingDownload(getContext());
		
		return success;
	}

	/**
	 * Send user Rating for a video
	 * @param videoid
	 * @param addedViewcount
	 * @param addedLike
	 * @return
	 */
	private boolean sendRatingData(long videoid, int addedViewcount, int addedLike)
	{

		SnooziUtility.trace(getContext(),TRACETYPE.INFO,"Sending data to server : Video " + videoid + " with addedViewcount " + addedViewcount + " with addedLike " + addedLike);
		try {
			Videoendpoint.Builder endpointBuilder = new Videoendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(),
					new HttpRequestInitializer() {
						public void initialize(HttpRequest httpRequest) { }
					});
			Videoendpoint videoEndpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
			videoEndpoint.rateVideo(videoid,addedLike,addedViewcount ).execute();
		} catch (IOException e) {
			SnooziUtility.trace(this.getContext(), TRACETYPE.ERROR,"sendRatingData Exception :  " +  e.toString());
			return false;
		}
		return true;
	}
	
	
	/**
	 * Get or Create a new dummy account for the sync adapter
	 *
	 * @param context The application context
	 */
	public static Account GetSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context.getSystemService(android.content.Context.ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data
		 * If successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in
			 * in your <provider> element in the manifest,
			 * then call context.setIsSyncable(account, AUTHORITY, 1)
			 * here.
			 */

		} else {
			Account[] accountlist = accountManager.getAccountsByType(ACCOUNT_TYPE);
			for (int i = 0; i < accountlist.length; i++) {
				Account account = accountlist[i];
				if(account.name.equals(ACCOUNT))
					newAccount = account;
			}
			/*
			 * The account exists or some other error occurred. Log this, report it,
			 * or handle it internally.
			 */

		}
		return newAccount;
	}
}
