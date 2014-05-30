package com.snoozi.snoozi.services;


import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.snoozi.snoozi.UI.WakeupActivity;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.SnooziUtility.TRACETYPE;

public class WakeupLaunchService extends WakefulIntentService {

	public static boolean isrunning = false;
	
	
	public WakeupLaunchService() {
		super("WakeupLaunchService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		isrunning = true;
		SnooziUtility.trace(this,TRACETYPE.DEBUG,"WakeupLaunchService.doWakefulWork");
		
		Intent launchintent = new Intent(this, WakeupActivity.class);
		launchintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(launchintent);
		
		
		do {
			
			try {
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (isrunning);
		
	}

}
