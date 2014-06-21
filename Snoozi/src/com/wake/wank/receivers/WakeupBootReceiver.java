package com.wake.wank.receivers;

import com.wake.wank.models.AlarmPlanifier;
import com.wake.wank.utils.SnooziUtility;

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
		
		AlarmPlanifier.checkAndPlanifyNextAlarm(context);
        
	}

}
