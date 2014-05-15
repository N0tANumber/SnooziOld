package com.snoozi.snoozi.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.gms.auth.GoogleAuthUtil;

public class SnooziUtility {
	public static final String PREFS_NAME = "com.snoozi.app";
	private static String _username = "";
	
	private static int _videoNumber = 0;
	/**
	 * Return the user account email
	 * @param context
	 * @return email like foo@gmail.com
	 */
	public static String getAccountNames(Context context) {
		if(_username == "")
		{
			AccountManager mAccountManager = AccountManager.get(context);
		    Account[] accounts = mAccountManager.getAccountsByType(
		    		GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		    String[] names = new String[accounts.length];
		    for (int i = 0; i < names.length; i++) {
		        names[i] = accounts[i].name;
		    }
		    if(names.length > 0)
		    	_username = names[0];
		    else 
		    	_username = "unknown";
		}
		return _username;
	    
	}
	
	public static int getVideoNumber(Context context)
	{
		if(_videoNumber == 0)
		{
			SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
			_videoNumber = settings.getInt("videoNumber", 1);
		}
		
		return _videoNumber; 
	}
	
	public static void setVideoNumber(Context context,int newNumber)
	{
		
		SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("videoNumber", newNumber);
		editor.commit();
		_videoNumber = newNumber;
	}
	
	
	public static Uri getVideoUri(Context context)
	{
		String path = "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video"+getVideoNumber(context), "raw", context.getPackageName());
		
		return Uri.parse(path);
	}
}
