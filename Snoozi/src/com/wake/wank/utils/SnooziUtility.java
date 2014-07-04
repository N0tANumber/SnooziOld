package com.wake.wank.utils;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.wake.wank.models.MyVideo;

public class SnooziUtility {
	public static final String PREFS_NAME = "com.wake.wank";
	private static final String SNOOZI_TRACE = "Snoozi_TRACE";
	public static final boolean DEV_MODE = true;

	 
	
	private static String m_username = "";
	
	private static MyVideo m_video = null;
	
	
	public class SYNC_ACTION{
		public static final String NEW_VIDEO_AVAILABLE = "NEW_VIDEO_AVAILABLE";
		public static final String GCM_REGISTERED = "GCM_REGISTERED";
		public static final String SEND_RATING = "SEND_RATING";
		public static final String SEND_DATA = "SEND_DATA";
		public static final String UPDATE_ALARM = "UPDATE_ALARM";
		}
	
	public static enum TRACETYPE{
		DEBUG,
		INFO,
		ERROR
	}
	
	
	public static void trace(Context context, TRACETYPE traceType, String message)
	{
		String stackinfo = "";
		try {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			StackTraceElement laststack = stack[stack.length-1];
			if(stack.length > 1)
				laststack = stack[stack.length-2];
			
			stackinfo =  laststack.getMethodName() + " : " + message + "    (" +laststack.getClassName()+" at line "+ laststack.getLineNumber() + ")";
		} catch (Exception e) {
			stackinfo = message;
		}
		
		switch (traceType) {
		case DEBUG :
			Log.d(SNOOZI_TRACE,stackinfo);
			break;
		case INFO :
			Log.i(SNOOZI_TRACE,stackinfo);
			break;
		case ERROR :
			
			Log.e(SNOOZI_TRACE,stackinfo);
			if(!DEV_MODE) {
				if (context != null) {
					// Logging to the server
					//TrackingSender sender = new TrackingSender(context);
					//sender.sendUserEvent(TrackingEventType.ERROR_LOGGER,stackinfo );
				}
			}
		break;
		default:
			break;
		}
	}
	
	
	/**
	 * Return the user account email
	 * @param context
	 * @return email like foo@gmail.com
	 */
	public static String getAccountNames(Context context) {
		if(m_username == "")
		{
			AccountManager mAccountManager = AccountManager.get(context);
		    Account[] accounts = mAccountManager.getAccountsByType(
		    		GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		    String[] names = new String[accounts.length];
		    for (int i = 0; i < names.length; i++) {
		        names[i] = accounts[i].name;
		    }
		    if(names.length > 0)
		    	m_username = names[0];
		    else 
		    	m_username = "unknown";
		}
		return m_username;
	    
	}
	
	public static MyVideo getCurrentAlarmVideo(Context context)
	{
		
		if(m_video == null)
		{
			m_video = MyVideo.getNextUnViewedVideo(context);
			
			//SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
			//m_video = settings.getInt("videoNumber", 1);
			
		}
		
		return m_video; 
	}
	
	/**
	 * Unset the video so there can be another one selected with getCurrentAlarmVideo
	 * @param context
	 */
	public static void unsetVideo()
	{
		
		//SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		//SharedPreferences.Editor editor = settings.edit();
		//editor.putInt("videoNumber", newNumber);
		//editor.commit();
		
		m_video = null;
	}
	
	
	public static Uri getVideoUri(Context context)
	{
		
		MyVideo video = getCurrentAlarmVideo(context);
		//String path = "";
		/*if(video == null)
		{
			//TODO : if no video, playing embedded video
			Random generator = new Random();
			int videoNumber = generator.nextInt(2);
			SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("videoNumber", videoNumber);
			editor.commit();
			path =  "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video" + videoNumber, "raw", context.getPackageName());
		}else
			path = video.getLocalurl();
		*/
		return Uri.parse(video.getLocalurl());
	}
	
	
	
}
