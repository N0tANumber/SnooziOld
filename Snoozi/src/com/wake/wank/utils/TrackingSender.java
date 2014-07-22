package com.wake.wank.utils;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.snoozi.trackingeventendpoint.Trackingeventendpoint;
import com.snoozi.trackingeventendpoint.model.TrackingEvent;
import com.wake.wank.MyApplication;
import com.wake.wank.MyApplication.TrackerName;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.CloudEndpointUtils;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Application;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;


/**
 * Class for saving tracking Event in the local database
 * The SyncAdapter look into the database and perform the server uploading
 * @author CtrlX
 *
 */
public class TrackingSender {
	
	
	private Application m_apps;
	//private TrackingEvent _trackingEvent;
	private Long m_videoid;
	private String m_category;
	private String m_action;
	private String m_description;
	private long m_timestamp;
	private String m_timestring;
	
	
	public TrackingSender(Application apps){
		
		this.m_apps = apps;
		//this._userAccount = null;
		
	}
	
	
	/**
	 * Send a Event made by the current User
	 * @param theAction
	 * 			SET_ALARM,UNSET_ALARM, SNOOZE, VIEW_VIDEO, LIKE_VIDEO, DISLIKE_VIDEO
	 * @param theDescription
	 * 			description of the event
	 */
	public void sendUserEvent(TrackingEventCategory theCat, TrackingEventAction theAction, String theDescription,Long videoid){
		//if(this._userAccount == null)
		//	this._userAccount = SnooziUtility.getAccountNames(this._appContext);
		
		Calendar cal = Calendar.getInstance();
		Date currentLocalTime = cal.getTime();
		
		this.m_description = theDescription;
		this.m_category = theCat.toString();
		this.m_action = theAction.toString();
		this.m_timestamp = System.currentTimeMillis();
		this.m_timestring = currentLocalTime.toString();
		this.m_videoid = videoid;
		
		try 
		{
			
			// Get tracker.
			Tracker t = ((MyApplication)this.m_apps).getTracker(
	            TrackerName.APP_TRACKER);
	        int theValue = 0;
	        if(this.m_action.equals(TrackingEventAction.SET.toString()))
	        	theValue = 1;
        	else if(this.m_action.equals(TrackingEventAction.UNSET.toString()))
        		theValue = -1;
        	
	        // Build and send an Event.
	        t.send(new HitBuilders.EventBuilder()
	            .setCategory(this.m_category)
	            .setAction(this.m_action)
	            .setLabel(this.m_description)
	            .setValue(theValue)
	            .build());
	        
	        
	        if(this.m_category.equals(TrackingEventCategory.ALARM.toString()))
	        {
	        	
				ContentValues values = new ContentValues();
				values.put(SnooziContract.trackingevents.Columns.TYPE,this.m_action );
				values.put(SnooziContract.trackingevents.Columns.DESCRIPTION,this.m_description );
				values.put(SnooziContract.trackingevents.Columns.TIMESTAMP,this.m_timestamp );
				values.put(SnooziContract.trackingevents.Columns.TIMESTRING,this.m_timestring );
				values.put(SnooziContract.trackingevents.Columns.VIDEOID,this.m_videoid );
	
				ContentResolver resolver = m_apps.getApplicationContext().getContentResolver();
				resolver.insert(SnooziContract.trackingevents.CONTENT_URI, values);
				//Log.i("CONTENTRESOLVER",theResult.toString());
				
				//On demande une synchro avec le server
				SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.TRACKING_SEND);
	        }
			
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.DEBUG,"TrackingSender.sendUserEvent Error " + e.toString());
			
		}finally{
		}
		
		
	}
	
	/**
	 * Send a Event made by the current User
	 * @param theType
	 * 			SET_ALARM,UNSET_ALARM, SNOOZE, VIEW_VIDEO, LIKE_VIDEO, DISLIKE_VIDEO
	 * @param theDescription
	 * 			description of the event
	 */
	public void sendUserEvent(TrackingEventCategory theCat,TrackingEventAction theType, String theDescription){
		sendUserEvent( theCat, theType, theDescription,0l);
	}
	
	
	
	public void sendUserEvent(TrackingEventCategory theCat,TrackingEventAction theType){
		sendUserEvent(theCat, theType, "");
	}
	
	
	
	
	//##### SERVER SIDE SYNC
	
	/**
	 * Send all pending trackingEvent to the server
	 * @param provider
	 * @throws Exception 
	 */
	public static boolean sendTrackingEvent() throws Exception {
		Cursor cursor = null;
		android.os.Debug.waitForDebugger();
		
		boolean success = false;
		try {
			
			//We check all tracking event in Contentproviders
			ContentResolver provider = MyApplication.getAppContext().getContentResolver();
			cursor = provider.query(SnooziContract.trackingevents.CONTENT_URI, SnooziContract.trackingevents.PROJECTION_ALL, null, null,  null);
			if (cursor.moveToFirst()) 
			{
				SnooziUtility.trace(TRACETYPE.INFO,"Sending "+cursor.getCount()+" TrackingEvent to server...");
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
				String userId = SnooziUtility.getAccountNames();
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
					
					versionName = MyApplication.getAppContext().getPackageManager().getPackageInfo(MyApplication.getAppContext().getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					SnooziUtility.trace(TRACETYPE.ERROR, "SyncAdapter NameNotFoundException :  " +  e.toString());
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
						SnooziUtility.trace(TRACETYPE.INFO, "## DEV MODE DUMMY## Tracking "+type+" synchronisation complete, id : " + id);
						answerEvent = new TrackingEvent();
					}else
					{
						//Sending the tracking Event
						answerEvent = endpoint.insertTrackingEvent(trackingEvent).execute();
						SnooziUtility.trace(TRACETYPE.INFO, "Tracking "+type+" synchronisation complete, id : " + id);
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
			throw e;
		} catch (Exception e) {
			throw e;
		}finally{
			if(cursor != null)
				cursor.close();
		}

		return success;
	}
	
}
