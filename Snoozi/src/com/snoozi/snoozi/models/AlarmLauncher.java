package com.snoozi.snoozi.models;

import java.util.Calendar;

import com.snoozi.snoozi.*;
import com.snoozi.snoozi.UI.AlarmReceiverActivity;
import com.snoozi.snoozi.receivers.WakeupBootReceiver;
import com.snoozi.snoozi.utils.TrackingEventType;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.TrackingSender;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

/**
 * Singleton class for planning a new alarm or for retrieving the next alarm launch
 * For now, work only with 1 alarm set
 * @author CtrlX
 *
 */
public class AlarmLauncher {

	/**
	 * Planify next launch of the alarm
	 * @param context
	 * @param secondFromNow 
	 * 		if <> 0 alarm will launch after specified second, not from parameters
	 * @return true
	 *   if planification succeed
	 */
	public static boolean planifyNextAlarm(Context context, int secondFromNow)
	{
		try {
			//Getting the next launch time with a calendar
			Calendar calendar = getNextLaunchCalendar(context,secondFromNow);
			
			//planning an alarm with a PendingIntent
			Intent intent = new Intent(context, AlarmReceiverActivity.class);
			//12345 is the alarm id. using the same alarm id overwritte previous planification of this alarm
			PendingIntent alarmIntent = PendingIntent.getActivity(context,12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmIntent);
			
			//Activating the bootreceiver for registering alarm if device is reboot
			ComponentName receiver = new ComponentName(context, WakeupBootReceiver.class);
			PackageManager pm = context.getPackageManager();
			pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
			
		} catch (Exception e) {
			TrackingSender sender = new TrackingSender(context);
			sender.sendUserEvent(TrackingEventType.ERROR_LOGGER,e.toString());
			return false;
		}
		return true;
	}
	
	/**
	 * Check and Planify next launch of the alarm (if activated)
	 * @param context
	 * @return true if alarm is planned,otherwise false
	 */
	public static boolean checkAndPlanifyNextAlarm(Context context)
	{
		//We check if alarm is still enabled
		SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		if(settings.getBoolean("activate", false))
			return planifyNextAlarm(context, 0);
		else
			return false;
	}
	
	/**
	 * Remove pending alarm planification
	 * @param context
	 */
	public static void CancelAlarm(Context context)
	{
		// If the alarm has been set, cancel it.
		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiverActivity.class);
		PendingIntent alarmIntent = PendingIntent.getActivity(context,12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmMgr.cancel(alarmIntent);
		
		//Disabling reboot alarm receiver
		ComponentName receiver = new ComponentName(context, WakeupBootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,  PackageManager.DONT_KILL_APP);
		
	}

	/**
	 * We get the next planned Calendar
	 * @param context
	 * @param secondFromNow 
	 * 			if 0, we take values from user pref. If >0 we get Now + specified seconds
	 * @return
	 */
	public static Calendar getNextLaunchCalendar(Context context, int secondFromNow) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if(secondFromNow > 0)
		{
			// Set the next alarm to start second from now
			calendar.add(Calendar.SECOND,secondFromNow);

		}else
		{
			//Calculating next alarm from the user pref 
			SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
			int hour = settings.getInt("hour", 7);
			int minute = settings.getInt("minute", 30);
			boolean[] checkedday = {false,false,false,false,false,false,false};
			checkedday[0] = settings.getBoolean("sunday", false);
			checkedday[1] = settings.getBoolean("monday", false);
			checkedday[2] = settings.getBoolean("tuesday", false);
			checkedday[3] = settings.getBoolean("wednesday", false);
			checkedday[4] = settings.getBoolean("thursday", false);
			checkedday[5] = settings.getBoolean("friday", false);
			checkedday[6] = settings.getBoolean("saturday", false);


			int minutetoday = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			int today = calendar.get(Calendar.DAY_OF_WEEK)-1;
			int dayadded = 0;

			if(!(settings.getBoolean("monday", false) || settings.getBoolean("tuesday", false) || settings.getBoolean("wednesday", false) ||	settings.getBoolean("thursday", false) || settings.getBoolean("friday", false) || settings.getBoolean("saturday", false) || settings.getBoolean("sunday", false)))
			{
				//REPEAT EVERYDAY -> We must check the hour
				if(minutetoday > hour*60 + minute )
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


		return calendar;
	}
	
	/**
	 * Return de timespan to the next alarm
	 * @param context
	 * @return 
	 * 		X days Y hours Z minutes
	 */
	public static String getNextAlarmAsString(Context context)
	{
		Calendar nextAlarm = AlarmLauncher.getNextLaunchCalendar(context, 0);
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		long nextmilli = nextAlarm.getTimeInMillis();
		long nowmilli = now.getTimeInMillis();
		long diffInSeconds = (nextmilli - nowmilli)/1000;
		long diff[] = new long[] { 0, 0, 0, 0 };
		/* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
	    /* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
	    /* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
	    /* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));
	    
	    StringBuilder theDelay = new StringBuilder();
	    if(diff[0] > 0)
	    {
	    	theDelay.append(diff[0]);
	    	theDelay.append(" ");
	    	if(diff[0] > 1)
	    		 theDelay.append(context.getResources().getString(R.string.days));
	    	 else
	    		 theDelay.append(context.getResources().getString(R.string.day));
	    	theDelay.append(" ");
	    }
	    if(diff[1] > 0)
	    {
	    	theDelay.append(diff[1]);
	    	theDelay.append(" ");
	    	if(diff[1] > 1)
	    		 theDelay.append(context.getResources().getString(R.string.hours));
	    	 else
	    		 theDelay.append(context.getResources().getString(R.string.hour));
	    	theDelay.append(" ");
	    }
	    if(diff[2] > 0)
	    {
	    	theDelay.append(diff[2]);
	    	theDelay.append(" ");
	    	if(diff[2] > 1)
	    		 theDelay.append(context.getResources().getString(R.string.minutes));
	    	 else
	    		 theDelay.append(context.getResources().getString(R.string.minute));
	    	theDelay.append(" ");
	    }
	    
	    if(theDelay.length() == 0 && diff[3] > 0)
	    {
	    	// We display second only if less than a minute
	    	theDelay.append(diff[3]);
	    	theDelay.append(" ");
	    	if(diff[3] > 1)
	    		 theDelay.append(context.getResources().getString(R.string.seconds));
	    	 else
	    		 theDelay.append(context.getResources().getString(R.string.second));
	    	theDelay.append(" ");
	    }
	   
	   
	    return theDelay.toString();
	}
}
