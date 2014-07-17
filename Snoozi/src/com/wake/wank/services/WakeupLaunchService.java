package com.wake.wank.services;


import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.wake.wank.UI.WakeupActivity;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

public class WakeupLaunchService extends WakefulIntentService {

	public static boolean isrunning = false;
	
	
	public WakeupLaunchService() {
		super("WakeupLaunchService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		isrunning = true;
		int alarmid = intent.getIntExtra("alarmid", 0);
		SnooziUtility.trace(TRACETYPE.DEBUG,"WakeupLaunchService.doWakefulWork alarmid + " + alarmid);
		
		Intent launchintent = new Intent(this, WakeupActivity.class);
		launchintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		launchintent.putExtra("alarmid", alarmid);
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
