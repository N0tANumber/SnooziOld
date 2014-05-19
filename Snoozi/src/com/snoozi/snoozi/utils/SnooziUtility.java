package com.snoozi.snoozi.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.gms.auth.GoogleAuthUtil;

public class SnooziUtility {
	public static final String PREFS_NAME = "com.snoozi.app";
	private static String m_username = "";
	
	private static int m_videoNumber = 0;
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
	
	public static int getVideoNumber(Context context)
	{
		if(m_videoNumber == 0)
		{
			SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
			m_videoNumber = settings.getInt("videoNumber", 1);
		}
		
		return m_videoNumber; 
	}
	
	public static void setVideoNumber(Context context,int newNumber)
	{
		
		SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("videoNumber", newNumber);
		editor.commit();
		m_videoNumber = newNumber;
	}
	
	
	public static Uri getVideoUri(Context context)
	{
		String path = "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video"+getVideoNumber(context), "raw", context.getPackageName());
		
		return Uri.parse(path);
	}
}
