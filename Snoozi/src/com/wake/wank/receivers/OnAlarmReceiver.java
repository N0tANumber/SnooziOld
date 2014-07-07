package com.wake.wank.receivers;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.wake.wank.MyApplication;
import com.wake.wank.UI.WakeupActivity;
import com.wake.wank.services.WakeupLaunchService;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		MyApplication.setAppContext(context);
		int alarmid = intent.getIntExtra("alarmid", 0);
		SnooziUtility.trace(TRACETYPE.DEBUG,"OnAlarmReceiver.onReceive alarmid = " + alarmid);
		
		Intent launchintent = new Intent(context, WakeupLaunchService.class);
		launchintent.putExtra("alarmid", alarmid);
		WakefulIntentService.sendWakefulWork(context, launchintent);
		
		//WakefulIntentService.sendWakefulWork(context, WakeupLaunchService.class);
	}

}
