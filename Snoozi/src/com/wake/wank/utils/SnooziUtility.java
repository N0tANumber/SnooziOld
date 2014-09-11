package com.wake.wank.utils;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.wake.wank.MyApplication;
import com.wake.wank.models.MyVideo;

public class SnooziUtility {
	public static final String PREFS_NAME = "com.wake.wank";
	private static final String SNOOZI_TRACE = "Snoozi_TRACE";
	public static final boolean DEV_MODE = false;

	 
	
	private static String m_username = "";
	
	private static MyVideo m_video = null;
	
	
	public static enum SYNC_ACTION{
		GCM_REGISTERED ,
		TRACKING_SEND,
		ALARM_UPDATE,
		ALARM_DELETE,
		USER_UPDATE,
		USER_VIEW,
		USER_WAKEUP,
		VIDEO_RETRIEVE,
		VIDEO_RATING,
		VIDEO_CLEANUP
		}
	
	public static enum TRACETYPE{
		DEBUG,
		INFO,
		ERROR
	}
	
	
	public static void trace(TRACETYPE traceType, String message)
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
				if (MyApplication.getAppContext() != null) {
					// Logging to the server
					//TrackingSender sender = new TrackingSender(MyApplication.);
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
	 * @return email like foo@gmail.com
	 */
	public static String getAccountNames() {
		if(m_username == "")
		{
			String[] names = new String[0];
			try {
				
			
				AccountManager mAccountManager = AccountManager.get(MyApplication.getAppContext());
			    Account[] accounts = mAccountManager.getAccountsByType(
			    		GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
			    names = new String[accounts.length];
			    for (int i = 0; i < names.length; i++) {
			        names[i] = accounts[i].name;
			    }
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR,"SnooziUtility.getAccountNames Exception " + e.toString());
				
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
			m_video = MyVideo.getNextUnViewedVideo();
			
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
