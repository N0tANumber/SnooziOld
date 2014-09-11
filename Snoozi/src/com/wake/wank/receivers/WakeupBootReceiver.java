package com.wake.wank.receivers;

import java.util.List;

import com.wake.wank.MyApplication;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.AlarmPlanifier;
import com.wake.wank.models.MyAlarm;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class for re-planing alarm if device is rebooted
 * @author CtrlX
 *
 */
public class WakeupBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		//We need to planify every activated Alarm
		MyApplication.setAppContext(context);
		
		SnooziUtility.trace(TRACETYPE.INFO,"WakeupBootReceiver.onReceive ");
		
		List<MyAlarm> alrmList = MyAlarm.getListFromSQL(SnooziContract.alarms.Columns.ACTIVATE + " = ?", new String[]{"1"});
		
		if(alrmList != null)
		{
			
			for (MyAlarm myAlarm : alrmList) {
				{
					AlarmPlanifier.checkAndPlanifyNextAlarm(myAlarm,context);
					SnooziUtility.trace(TRACETYPE.INFO,"Alarm "+myAlarm.getId()+" planified : " + myAlarm.toStringDiff());
					
				}
				
			}
		}else
			SnooziUtility.trace(TRACETYPE.INFO,"No Alarm to planify ");
		
		
        
	}

}
