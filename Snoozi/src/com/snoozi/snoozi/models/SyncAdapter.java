package com.snoozi.snoozi.models;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.snoozi.snoozi.GCMIntentService;
import com.snoozi.snoozi.database.SnooziContract;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.SnooziUtility.TRACETYPE;
import com.snoozi.trackingeventendpoint.Trackingeventendpoint;
import com.snoozi.trackingeventendpoint.model.TrackingEvent;


/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	// Global variables
	// Define a variable to contain a content resolver instance
	ContentResolver m_ContentResolver;


	// An account type, in the form of a domain name
	private static final String ACCOUNT_TYPE = "com.snoozi";
	// The account name
	private static final String ACCOUNT = "default_account";


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
		
		if(!isRegistered)
		{
			SnooziUtility.trace(this.getContext(),TRACETYPE.INFO,"isRegistered : " + isRegistered);
			//Not registered yet so Registering Google cloud messaging
			try {
				GCMIntentService.register(this.getContext());
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
			// we must call the serveur to get a list of all new available video
			SnooziUtility.trace(this.getContext(), TRACETYPE.INFO, "gogo list");
			
		}else
		{
			// Otherwise : SnooziUtility.SYNC_ACTION.SEND_DATA 
			// we send all data to the server if any

			Cursor cursor = null;
			try {
				//We check all tracking event in Contentproviders
				cursor = provider.query(SnooziContract.trackingevents.CONTENT_URI, SnooziContract.trackingevents.PROJECTION_ALL, null, null,  null);
				//TODO : selection de la liste des tracking Event
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
					Trackingeventendpoint endpoint = CloudEndpointUtils.updateBuilder(
							endpointBuilder).build();
					String userId = SnooziUtility.getAccountNames(this.getContext());
					String versionName;
					try {
						versionName = this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0).versionName;
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						versionName = "—";
					}
					do {
						// getting the data from the cursor
						int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns._ID));
						long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TIMESTAMP));
						String timestring = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TIMESTRING));
						String description = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.DESCRIPTION));
						String type = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TYPE));
						int videoid = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.VIDEOID));

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
						TrackingEvent result;
						if(SnooziUtility.DEV_MODE)
						{
							SnooziUtility.trace(this.getContext(), TRACETYPE.INFO,"## DEV MODE DUMMY## Tracking "+type+" synchronisation complete, id : " + id);
							result = new TrackingEvent();
						}else
						{
							//Sending the tracking Event
							result = endpoint.insertTrackingEvent(trackingEvent).execute();
							SnooziUtility.trace(this.getContext(), TRACETYPE.INFO,"Tracking "+type+" synchronisation complete, id : " + id);
						}
						if(result != null)
						{
							// Deleting the Tracking event
							provider.delete(SnooziContract.trackingevents.CONTENT_URI, 
											SnooziContract.trackingevents.Columns._ID + " = ? ", 
											new String[]{String.valueOf(id)});
						}
					} while (cursor.moveToNext());

				}


			} catch (IOException e) {
				//ne pas mettre TYPE"ERROR car sinon il va spammer le server de log error
				SnooziUtility.trace(this.getContext(), TRACETYPE.DEBUG,"SyncAdapter IOException :  " +  e.toString());
			} catch (RemoteException e) {
				SnooziUtility.trace(this.getContext(), TRACETYPE.DEBUG,"SyncAdapter RemoteException :  " +  e.toString());
			} catch (IllegalArgumentException e) {
				SnooziUtility.trace(this.getContext(), TRACETYPE.DEBUG,"SyncAdapter IllegalArgumentException :  " +  e.toString());
			} catch (Exception e) {
				SnooziUtility.trace(this.getContext(), TRACETYPE.DEBUG,"SyncAdapter IllegalArgumentException :  " +  e.toString());
			}finally{
				if(cursor != null)
					cursor.close();
			}
		}

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
