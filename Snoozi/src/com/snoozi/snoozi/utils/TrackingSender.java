package com.snoozi.snoozi.utils;


import java.util.Calendar;
import java.util.Date;

import com.snoozi.snoozi.UI.MainActivity;
import com.snoozi.snoozi.database.SnooziContract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;



public class TrackingSender extends AsyncTask<Context, Integer, Long> {
	
	
	private Context _appContext;
	//private TrackingEvent _trackingEvent;
	private int _videoid;
	private String _type;
	private String _description;
	private long _timestamp;
	private String _timestring;
	
	
	public TrackingSender(Context thecontext){
		
		this._appContext = thecontext;
		//this._userAccount = null;
		
	}
	
	
	/**
	 * Send a Event made by the current User
	 * @param theType
	 * 			SET_ALARM,UNSET_ALARM, SNOOZE, VIEW_VIDEO, LIKE_VIDEO, DISLIKE_VIDEO
	 * @param theDescription
	 * 			description of the event
	 */
	public void sendUserEvent(EventType theType, String theDescription,int videoid){
		//if(this._userAccount == null)
		//	this._userAccount = SnooziUtility.getAccountNames(this._appContext);
		
		Calendar cal = Calendar.getInstance();
		Date currentLocalTime = cal.getTime();
		
		this._description = theDescription;
		this._type = theType.toString();
		this._timestamp = System.currentTimeMillis();
		this._timestring = currentLocalTime.toString();
		this._videoid = videoid;
		
		
		this.execute(_appContext);
	}
	
	/**
	 * Send a Event made by the current User
	 * @param theType
	 * 			SET_ALARM,UNSET_ALARM, SNOOZE, VIEW_VIDEO, LIKE_VIDEO, DISLIKE_VIDEO
	 * @param theDescription
	 * 			description of the event
	 */
	public void sendUserEvent(EventType theType, String theDescription){
		sendUserEvent( theType, theDescription,0);
	}
	
	
	
	public void sendUserEvent(EventType theType){
		sendUserEvent( theType, "");
	}
	
	
	
	
	protected Long doInBackground(Context... contexts) 
	{
		try 
		{
			ContentValues values = new ContentValues();
			values.put(SnooziContract.trackingevents.Columns.TYPE,this._type );
			values.put(SnooziContract.trackingevents.Columns.DESCRIPTION,this._description );
			values.put(SnooziContract.trackingevents.Columns.TIMESTAMP,this._timestamp );
			values.put(SnooziContract.trackingevents.Columns.TIMESTRING,this._timestring );
			values.put(SnooziContract.trackingevents.Columns.VIDEOID,this._videoid );

			ContentResolver resolver = this._appContext.getContentResolver();
			Uri theResult = resolver.insert(SnooziContract.trackingevents.CONTENT_URI, values);
			//Log.i("CONTENTRESOLVER",theResult.toString());
			
			//On demande une synchro avec le server
			Bundle settingsBundle = new Bundle();
	        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
	        settingsBundle.putBoolean( ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
	        ContentResolver.requestSync(MainActivity.CreateSyncAccount(this._appContext), MainActivity.AUTHORITY, settingsBundle);
			
			
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
