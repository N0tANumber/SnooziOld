package com.wake.wank.utils;


import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wake.wank.MyApplication;
import com.wake.wank.MyApplication.TrackerName;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;


/**
 * Class for saving tracking Event in the local database
 * The SyncAdapter look into the database and perform the server uploading
 * @author CtrlX
 *
 */
public class TrackingSender extends AsyncTask<Context, Integer, Long> {
	
	
	private Context m_appContext;
	private Application m_apps;
	//private TrackingEvent _trackingEvent;
	private Long m_videoid;
	private String m_category;
	private String m_action;
	private String m_description;
	private long m_timestamp;
	private String m_timestring;
	
	private boolean isRunning;
	
	public TrackingSender(Context thecontext, Application apps){
		
		this.m_appContext = thecontext;
		this.m_apps = apps;
		//this._userAccount = null;
		isRunning = false;
		
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
		
		if(!isRunning)
			this.execute(m_appContext);
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
	
	
	
	
	protected Long doInBackground(Context... contexts) 
	{
		try 
		{
			// Get tracker.
			isRunning = true;
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
	
				ContentResolver resolver = this.m_appContext.getContentResolver();
				resolver.insert(SnooziContract.trackingevents.CONTENT_URI, values);
				//Log.i("CONTENTRESOLVER",theResult.toString());
				
				//On demande une synchro avec le server
				Bundle settingsBundle = new Bundle();
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		        //settingsBundle.putBoolean( ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        ContentResolver.requestSync(SyncAdapter.GetSyncAccount(this.m_appContext), SnooziContract.AUTHORITY, settingsBundle);
			
	        }
			
		} catch (Exception e) {
			SnooziUtility.trace(this.m_appContext,TRACETYPE.ERROR,"CONTENTRESOLVER Error " + e.toString());
			
		}finally{
			isRunning = false;
		}
		
		/*
		
		
		Trackingeventendpoint.Builder endpointBuilder = new Trackingeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) { }
				});
		Trackingeventendpoint endpoint = CloudEndpointUtils.updateBuilder(
				endpointBuilder).build();
		try {
			
			// TODO : Ne pas envoyer l'event tout de suite, mais le sauvegarder et le faire dans un background service qui check si on a internet
			TrackingEvent result = endpoint.insertTrackingEvent(this._trackingEvent).execute();
			this._trackingEvent = result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		return (long) 0;
	}
	
}
