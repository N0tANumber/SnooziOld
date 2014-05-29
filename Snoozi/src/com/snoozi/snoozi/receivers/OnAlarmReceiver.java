package com.snoozi.snoozi.receivers;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.snoozi.snoozi.services.WakeupLaunchService;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.SnooziUtility.TRACETYPE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SnooziUtility.trace(context,TRACETYPE.DEBUG,"OnAlarmReceiver.onReceive");
		WakefulIntentService.sendWakefulWork(context, WakeupLaunchService.class);
	}

}
