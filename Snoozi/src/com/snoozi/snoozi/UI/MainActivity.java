package com.snoozi.snoozi.UI;


import java.util.Timer;
import java.util.TimerTask;

import com.snoozi.deviceinfoendpoint.Deviceinfoendpoint;
import com.snoozi.snoozi.*;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.SnooziUtility.TRACETYPE;

import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * The Main Activity.
 * 
 * This activity starts up the RegisterActivity immediately, which communicates
 * with your App Engine backend using Cloud Endpoints. It also receives push
 * notifications from backend via Google Cloud Messaging (GCM).
 * 
 * Check out RegisterActivity.java for more details.
 */
public class MainActivity extends Activity {




	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(Deviceinfoendpoint.DEFAULT_ROOT_URL == "https://evident-quasar-565.appspot.com/_ah/api/")
		{
			SnooziUtility.trace(this, TRACETYPE.ERROR, "You need to change all endpoint DEFAULT_ROOT_URL to match your current api version : ex : https://evident... ==> https://2-dot-evident");
			finish();
			return;
		}
		
		
		//TEST : Start up RegisterActivity right away
		//Intent intent = new Intent(this, RegisterActivity.class);
		//startActivity(intent);
		
		
		final Timer timer = new Timer(true);
		TimerTask timerTask = new TimerTask() 
		{
			@Override
			public void run() 
			{   
				//after 1s, launch the setting activity
				launchSettingActivity();
			}
		};

		timer.schedule(timerTask, 1000);
		
		
	}
	
	private void launchSettingActivity()
	{
		 Intent intent = new Intent(this, AlarmSettingActivity.class);
		startActivity(intent);
		//for now finish main activity after launching the AlarmSettingActivity
		finish();
	}
	
	
}


