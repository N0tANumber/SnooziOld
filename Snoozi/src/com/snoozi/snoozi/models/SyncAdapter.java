package com.snoozi.snoozi.models;

import java.io.IOException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.snoozi.snoozi.database.SnooziContract;
import com.snoozi.snoozi.services.GCMIntentService;
import com.snoozi.snoozi.utils.EventType;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.trackingeventendpoint.Trackingeventendpoint;
import com.snoozi.trackingeventendpoint.model.TrackingEvent;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	// Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    /**
     * Set up the sync adapter
     */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContentResolver = context.getContentResolver();
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
       mContentResolver = context.getContentResolver();
      
   }
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) 
	{
		
		SharedPreferences settings =this.getContext().getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		boolean isRegistered = settings.getBoolean("deviceRegistered", false);
		if(!isRegistered)
		{
			//Registering Google cloud messaging
			try {
	              SharedPreferences.Editor editor = settings.edit();
		  		  editor.putBoolean("deviceRegistered", true);
		  		  editor.commit();
		  		  GCMIntentService.register(this.getContext());
	            } catch (Exception e) {
	            	//probleme during GCM registration
	            	try {
	    				ContentValues values = new ContentValues();
	    				values.put(SnooziContract.trackingevents.Columns.TYPE,EventType.ERROR_LOGGER.toString() );
	    				values.put(SnooziContract.trackingevents.Columns.DESCRIPTION,"GCM Registration error :  " + e.toString() );
	    				values.put(SnooziContract.trackingevents.Columns.TIMESTAMP,System.currentTimeMillis() );
						provider.insert(SnooziContract.trackingevents.CONTENT_URI, values);
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
		}

		//We check all tracking event in Contentproviders
		Cursor cursor = null;
		try {
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
				Trackingeventendpoint endpoint = CloudEndpointUtils.updateBuilder(
						endpointBuilder).build();
				//Building a tracking end for all message
				String userId = SnooziUtility.getAccountNames(this.getContext());
				do {
					int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns._ID));
					long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TIMESTAMP));
					String timestring = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TIMESTRING));
					String description = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.DESCRIPTION));
					String type = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.TYPE));
					int videoid = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.trackingevents.Columns.VIDEOID));

					//Building the trackingEvent
					TrackingEvent trackingEvent = new TrackingEvent();
					trackingEvent.setUserid(userId);
					trackingEvent.setAndroidVersion(android.os.Build.VERSION.SDK_INT);
					trackingEvent.setDeviceInformation(android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE);
					trackingEvent.setDescription(description);
					trackingEvent.setType(type);
					trackingEvent.setTimestamp(timestamp);
					trackingEvent.setTimeString(timestring);
					trackingEvent.setVideoid(videoid);
					//Sending the tracking Event
					TrackingEvent result = endpoint.insertTrackingEvent(trackingEvent).execute();
					if(result != null)
					{
						// Deleting the Tracking event
						long noDeleted = provider.delete
							      (SnooziContract.trackingevents.CONTENT_URI, 
							    		  SnooziContract.trackingevents.Columns._ID + " = ? ", 
							      new String[]{String.valueOf(id)});
						Log.i("SNOOZI","Tracking "+type+" synchronisation complete, id : " + id);
					}
				} while (cursor.moveToNext());
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(cursor != null)
				cursor.close();
		}
		
		
	}
	
	

}
