package com.snoozi.snoozi.utils;


import java.util.Calendar;
import java.util.Date;

import com.snoozi.snoozi.UI.MainActivity;
import com.snoozi.snoozi.database.SnooziContract;
import com.snoozi.snoozi.models.SyncAdapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


/**
 * Class for saving tracking Event in the local database
 * The SyncAdapter look into the database and perform the server uploading
 * @author CtrlX
 *
 */
public class TrackingSender extends AsyncTask<Context, Integer, Long> {
	
	
	private Context m_appContext;
	//private TrackingEvent _trackingEvent;
	private int m_videoid;
	private String m_type;
	private String m_description;
	private long m_timestamp;
	private String m_timestring;
	
	
	public TrackingSender(Context thecontext){
		
		this.m_appContext = thecontext;
		//this._userAccount = null;
		
	}
	
	
	/**
	 * Send a Event made by the current User
	 * @param theType
	 * 			SET_ALARM,UNSET_ALARM, SNOOZE, VIEW_VIDEO, LIKE_VIDEO, DISLIKE_VIDEO
	 * @param theDescription
	 * 			description of the event
	 */
	public void sendUserEvent(TrackingEventType theType, String theDescription,int videoid){
		//if(this._userAccount == null)
		//	this._userAccount = SnooziUtility.getAccountNames(this._appContext);
		
		Calendar cal = Calendar.getInstance();
		Date currentLocalTime = cal.getTime();
		
		this.m_description = theDescription;
		this.m_type = theType.toString();
		this.m_timestamp = System.currentTimeMillis();
		this.m_timestring = currentLocalTime.toString();
		this.m_videoid = videoid;
		
		
		this.execute(m_appContext);
	}
	
	/**
	 * Send a Event made by the current User
	 * @param theType
	 * 			SET_ALARM,UNSET_ALARM, SNOOZE, VIEW_VIDEO, LIKE_VIDEO, DISLIKE_VIDEO
	 * @param theDescription
	 * 			description of the event
	 */
	public void sendUserEvent(TrackingEventType theType, String theDescription){
		sendUserEvent( theType, theDescription,0);
	}
	
	
	
	public void sendUserEvent(TrackingEventType theType){
		sendUserEvent( theType, "");
	}
	
	
	
	
	protected Long doInBackground(Context... contexts) 
	{
		try 
		{
			ContentValues values = new ContentValues();
			values.put(SnooziContract.trackingevents.Columns.TYPE,this.m_type );
			values.put(SnooziContract.trackingevents.Columns.DESCRIPTION,this.m_description );
			values.put(SnooziContract.trackingevents.Columns.TIMESTAMP,this.m_timestamp );
			values.put(SnooziContract.trackingevents.Columns.TIMESTRING,this.m_timestring );
			values.put(SnooziContract.trackingevents.Columns.VIDEOID,this.m_videoid );

			ContentResolver resolver = this.m_appContext.getContentResolver();
			Uri theResult = resolver.insert(SnooziContract.trackingevents.CONTENT_URI, values);
			//Log.i("CONTENTRESOLVER",theResult.toString());
			
			//On demande une synchro avec le server
			Bundle settingsBundle = new Bundle();
	        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
	        settingsBundle.putBoolean( ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
	        ContentResolver.requestSync(SyncAdapter.GetSyncAccount(this.m_appContext), SnooziContract.AUTHORITY, settingsBundle);
			
			
		} catch (Exception e) {
			Log.e("CONTENTRESOLVER",e.toString());
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
