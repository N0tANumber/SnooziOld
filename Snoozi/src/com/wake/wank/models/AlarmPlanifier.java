package com.wake.wank.models;

import java.util.Calendar;

import com.wake.wank.R;
import com.wake.wank.receivers.OnAlarmReceiver;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Singleton class for planning a new alarm or for retrieving the next alarm launch
 * For now, work only with 1 alarm set
 * @author CtrlX
 *
 */
public class AlarmPlanifier  {

	/**
	 * Planify next launch of the alarm
	 * @param context
	 * @param currentAlarm 
	 * @param secondFromNow 
	 * 		if <> 0 alarm will launch after specified second, not from parameters
	 * @return true
	 *   if planification succeed
	 */
	public static boolean planifyNextAlarm(Context context, MyAlarm currentAlarm, int secondFromNow)
	{
		
		try {
			//Getting the next launch time with a calendar
			Calendar calendar = getNextLaunchCalendar(context,currentAlarm, secondFromNow);

			//planning an alarm with a PendingIntent
			Intent intent = new Intent(context, OnAlarmReceiver.class);
			intent.putExtra("alarmid", currentAlarm.getId());
			//the alarm id is multi by *76543 to avoid error. using the same alarm id overwritte previous planification of this alarm
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context, currentAlarm.getId()*76543, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmIntent);

			//Activating the bootreceiver for registering alarm if device is reboot
			//ComponentName receiver = new ComponentName(context, WakeupBootReceiver.class);
			//PackageManager pm = context.getPackageManager();
			//pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, e.toString());
			return false;
		}
		return true;
	}

	/**
	 * Check and Planify next launch of the alarm (if activated)
	 * @param currentAlarm 
	 * @param context
	 * @return true if alarm is planned,otherwise false
	 */
	public static boolean checkAndPlanifyNextAlarm(MyAlarm currentAlarm, Context context)
	{
		//We check if alarm is still enabled
		if(currentAlarm == null)
			return false;
		SnooziUtility.trace(TRACETYPE.INFO, "checkAndPlanifyNextAlarm ");
		if(currentAlarm.getActivate())
			return planifyNextAlarm(context,currentAlarm, 0);
		else
			return false;
	}

	/**
	 * Remove pending alarm planification
	 * @param myAlarm 
	 * @param context
	 */
	public static void CancelAlarm(MyAlarm myAlarm, Context context)
	{
		if(myAlarm == null)
			return;
		
		try {
			SnooziUtility.trace(TRACETYPE.INFO, "Cancelling Alarm");
			
			// If the alarm has been set, cancel it.
			/*AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, OnAlarmReceiver.class);
			PendingIntent alarmIntent = PendingIntent.getActivity(context,12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarmMgr.cancel(alarmIntent);*/

			
			Intent intent = new Intent(context, OnAlarmReceiver.class);
			//12345 is the alarm id. using the same alarm id overwritte previous planification of this alarm
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context,myAlarm.getId()*76543, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			alarmMgr.cancel(alarmIntent);

			
			
			//Disabling reboot alarm receiver
			//ComponentName receiver = new ComponentName(context, WakeupBootReceiver.class);
			//PackageManager pm = context.getPackageManager();

			//pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,  PackageManager.DONT_KILL_APP);
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, e.toString());
		}

	}

	/**
	 * We get the next planned Calendar
	 * @param context
	 * @param currentAlarm 
	 * @param secondFromNow 
	 * 			if 0, we take values from user pref. If >0 we get Now + specified seconds
	 * @return
	 */
	public static Calendar getNextLaunchCalendar(Context context, MyAlarm currentAlarm, int secondFromNow) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		try {

			if(secondFromNow > 0)
			{
				// Set the next alarm to start second from now
				calendar.add(Calendar.SECOND,secondFromNow);

			}else
			{
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				
				//Calculating next alarm from the user pref 
				int hour = currentAlarm.getHour();
				int minute = currentAlarm.getMinute();
				boolean[] checkedday = {false,false,false,false,false,false,false};
				checkedday[0] = currentAlarm.getSunday();
				checkedday[1] = currentAlarm.getMonday();
				checkedday[2] = currentAlarm.getTuesday();
				checkedday[3] = currentAlarm.getWednesday();
				checkedday[4] = currentAlarm.getThursday();
				checkedday[5] = currentAlarm.getFriday();
				checkedday[6] = currentAlarm.getSaturday();


				int minutetoday = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
				int today = calendar.get(Calendar.DAY_OF_WEEK)-1;
				int dayadded = 0;
				
				int daychecked = 0;
				for (int i = 0; i < 7; i++) {
					if(checkedday[i])
						daychecked++;
				}
				if(daychecked == 0 || daychecked == 7)
				{
					//REPEAT EVERYDAY -> We must check the hour
					if(minutetoday >= hour*60 + minute )
						dayadded++; // not today, so tomorrow -> add a day
				}else
				{
					//REPEAT NEXT CHECKED DAY
					boolean isFound = false;
					for (int i = today; !isFound; i++) {
						boolean dayisChecked = checkedday[i%7];
						if(dayisChecked)
							if(i == today)
							{
								//We have to checked hour for today
								if( hour*60 + minute > minutetoday  )
									isFound = true;
							}else
							{
								isFound = true;
							}

						if(!isFound)
							dayadded++; // not found so we add a day
					}
				}
				calendar.add(Calendar.DATE, dayadded); 
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);

			}
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, e.toString());
		}


		return calendar;
	}

	
}
