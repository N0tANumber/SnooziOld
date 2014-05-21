package com.snoozi.snoozi.receivers;

import com.snoozi.snoozi.models.AlarmPlanifier;
import com.snoozi.snoozi.utils.SnooziUtility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Class for re-planing alarm if device is rebooted
 * @author CtrlX
 *
 */
public class WakeupBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) 
		{
			SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, 0);
			boolean isActivated = settings.getBoolean("activate", false);
			if(isActivated)
			{
				AlarmPlanifier.checkAndPlanifyNextAlarm(context);
			}
        }
	}

}
