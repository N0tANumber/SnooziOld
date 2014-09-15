package com.wake.wank.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.snoozi.alarmendpoint.Alarmendpoint;
import com.snoozi.alarmendpoint.model.Alarm;
import com.wake.wank.MyApplication;
import com.wake.wank.R;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.Toast;

public class MyAlarm  implements Bundleable {


	private int id;

	private int hour;
	private int minute;
	private int volume;

	private String dayString;
	private Boolean monday;
	private Boolean tuesday;
	private Boolean wednesday;
	private Boolean thursday;
	private Boolean friday;
	private Boolean saturday;
	private Boolean sunday;

	private Boolean activate;
	private Boolean vibrate;

	private Boolean hasChanged;

	private long alarmId;
	private long videoId;


	@Override
	public Bundle toBundle() {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		b.putInt("_id",getId());
		b.putBoolean("_activate",getActivate());
		b.putInt("_hour",getHour());
		b.putInt("_minute",getMinute());
		b.putInt("_volume",getVolume());
		b.putString("_daystring",getDayString());
		b.putBoolean("_monday",getMonday());
		b.putBoolean("_tuesday",getTuesday());
		b.putBoolean("_wednesday",getWednesday());
		b.putBoolean("_thursday",getThursday());
		b.putBoolean("_friday",getFriday());
		b.putBoolean("_saturday",getSaturday());
		b.putBoolean("_sunday",getSunday());
		b.putBoolean("_vibrate",getVibrate());

		b.putLong("_alarmid",getAlarmId());
		b.putLong("_videoid",getVideoId());

		b.putBoolean("_haschanged",getHasChanged());

		return b;
	}

	@Override
	public void fromBundle(Bundle b) {
		setId(b.getInt("_id"));
		setActivate(b.getBoolean("_activate"));
		setHour(b.getInt("_hour"));
		setMinute(b.getInt("_minute"));
		setVolume(b.getInt("_volume"));
		setDayString(b.getString("_daystring"));
		setMonday(b.getBoolean("_monday"));
		setTuesday(b.getBoolean("_tuesday"));
		setWednesday(b.getBoolean("_wednesday"));
		setThursday(b.getBoolean("_thursday"));
		setFriday(b.getBoolean("_friday"));
		setSaturday(b.getBoolean("_saturday"));
		setSunday(b.getBoolean("_sunday"));
		setVibrate(b.getBoolean("_vibrate"));

		setAlarmId(b.getLong("_alarmid"));
		setVideoId(b.getLong("_videoid"));

		setHasChanged(b.getBoolean("_haschanged"));
	}


	public MyAlarm(Bundle b) {
		fromBundle(b);
	}

	public MyAlarm() {
		setId(0);
		setActivate( true);
		setHour( 7);
		setMinute( 30);
		setVolume( -1);
		setDayString( "");
		setMonday( false);
		setTuesday( false);
		setWednesday( false);
		setThursday(false);
		setFriday( false);
		setSaturday( false);
		setSunday( false);
		setVibrate( true);

		setAlarmId( 0);
		setVideoId( 0);

		setHasChanged(false);
	}

	/**
	 * @param cursor
	 */
	private  MyAlarm(Cursor cursor) {
		this(); // default constructor

		try {

			this.setId(cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)));
			this.setActivate((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.ACTIVATE)) == 1)? true : false);
			this.setHour(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.HOUR)));
			this.setMinute(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.MINUTE)));
			this.setDayString(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.DAYSTRING)));
			this.setMonday((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.MONDAY)) == 1)? true : false);
			this.setTuesday((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.TUESDAY)) == 1)? true : false);
			this.setWednesday((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.WEDNESDAY)) == 1)? true : false);
			this.setThursday((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.THURSDAY)) == 1)? true : false);
			this.setFriday((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.FRIDAY)) == 1)? true : false);
			this.setSaturday((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.SATURDAY)) == 1)? true : false);
			this.setSunday((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.SUNDAY)) == 1)? true : false);
			this.setVibrate((cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.VIBRATE)) == 1)? true : false);
			this.setVolume(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.VOLUME)));
			this.setAlarmId(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.ALARMID)));
			this.setVideoId(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.alarms.Columns.VIDEOID)));

			setHasChanged(false);
		} catch (Exception e) {
			// Error during creation of this MyVideoObject
		}
	}

	/**
	 * Get every alarm in the database
	 * @return
	 */
	public static List<MyAlarm> getListFromSQL()
	{
		return getListFromSQL(null, null);
	}

	/**
	 * Get every Alarm that matche the condition
	 * @param WhereClause  ex : SnooziContract.alarms.Columns.ACTIVATE + " = ?"
	 * @param WhereValues  ex : new String[]{"1"}
	 * @return
	 */
	public static List<MyAlarm> getListFromSQL(String WhereClause, String[] WhereValues)
	{
		List<MyAlarm> alarmList = new ArrayList<MyAlarm>();
		ContentResolver provider = MyApplication.getAppContext().getContentResolver();
		Cursor cursor = null;
		MyAlarm alrm = null;
		try
		{


			cursor   = provider.query(SnooziContract.alarms.CONTENT_URI, SnooziContract.alarms.PROJECTION_ALL, WhereClause,WhereValues, null);
			if (cursor.moveToFirst()) 
			{

				do {
					alrm = new MyAlarm(cursor);
					alarmList.add(alrm);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyAlarm.getFromSQL Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return alarmList;
	}


	/**
	 * Get a specific alarm from the Id
	 * @param alarmId
	 * @return
	 */
	public static MyAlarm getFromSQL(int alarmId)
	{
		Cursor cursor = null;
		MyAlarm alrm = null;
		try
		{
			ContentResolver provider = MyApplication.getAppContext().getContentResolver();
			cursor   = provider.query(ContentUris.withAppendedId(SnooziContract.alarms.CONTENT_URI,alarmId), SnooziContract.alarms.PROJECTION_ALL, null,null, null);
			if (cursor.moveToFirst()) 
			{
				alrm = new MyAlarm(cursor);

			}
		}
		catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyAlarm.getFromSQL("+alarmId+") Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return alrm;
	}



	public int saveAndSync() {
		// TODO Auto-generated method stub
		int theResult =  save();
		if(theResult > 0)
		{	//UPDATE SUCCESS, THEN WE SEND NEW VALUE TO THE SERVER
			SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.ALARM_UPDATE,this);
		}

		return theResult;
	}

	/**
	 * Save this Alarm if data has changed
	 * @return
	 */
	public int save() {
		int result = 0;

		if(this.getId() == 0 || hasChanged)
		{

			try {
				ContentResolver provider = MyApplication.getAppContext().getContentResolver();


				//SAVE THE ALARM IN THE LOCAL DATABASE
				ContentValues values = new ContentValues();
				values.put(SnooziContract.alarms.Columns.ACTIVATE,getActivate() );
				values.put(SnooziContract.alarms.Columns.HOUR,getHour() );
				values.put(SnooziContract.alarms.Columns.MINUTE,getMinute() );
				values.put(SnooziContract.alarms.Columns.DAYSTRING,getDayString() );
				values.put(SnooziContract.alarms.Columns.MONDAY,getMonday() );
				values.put(SnooziContract.alarms.Columns.TUESDAY,getTuesday() );
				values.put(SnooziContract.alarms.Columns.WEDNESDAY,getWednesday() );
				values.put(SnooziContract.alarms.Columns.THURSDAY,getThursday() );
				values.put(SnooziContract.alarms.Columns.FRIDAY,getFriday() );
				values.put(SnooziContract.alarms.Columns.SATURDAY,getSaturday() );
				values.put(SnooziContract.alarms.Columns.SUNDAY,getSunday() );
				values.put(SnooziContract.alarms.Columns.VIBRATE,getVibrate() );
				values.put(SnooziContract.alarms.Columns.VOLUME,getVolume() );

				values.put(SnooziContract.alarms.Columns.ALARMID,getAlarmId() );
				values.put(SnooziContract.alarms.Columns.VIDEOID,getVideoId() );

				if(this.getId() == 0)
				{
					//INSERTING

					Uri videouri = provider.insert(SnooziContract.alarms.CONTENT_URI, values);
					//Retrieving id from path
					result = Integer.parseInt(videouri.getLastPathSegment());
					this.setId(result);
				}else
				{
					result = provider.update(ContentUris.withAppendedId(SnooziContract.alarms.CONTENT_URI, getId()), values,null,null);
				}


				setHasChanged(false);

				SnooziUtility.trace(TRACETYPE.INFO,"Saved Alarm " + getId() + " : " + toString());
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR,"MyAlarm.save2 ERROR" + e.toString());
				result = -1;
			}

		}
		return result;
	}






	/**
	 * Delete this Alarm 
	 * @return
	 */
	public int delete() {
		int result = 1;

		
		if(this.getId() > 0 )
		{

			try {
				// First if activated, we desactivate the alarm
				if(this.getActivate())
					AlarmPlanifier.CancelAlarm(this,MyApplication.getAppContext());
				
				//DELETING THE ALARM INTO THE DATABASE
				ContentResolver provider = MyApplication.getAppContext().getContentResolver();

				result = provider.delete(ContentUris.withAppendedId(SnooziContract.alarms.CONTENT_URI, getId()),null,null);


				SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.ALARM_DELETE,this);

				setHasChanged(false);

				SnooziUtility.trace(TRACETYPE.INFO,"Deleted Alarm " + getId() + " : " + toString());
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR,"Deleted Alarm ERROR" + e.toString());
				result = 0;
			}

		}
		return result;
	}


	public void checkAndPlanify(Activity mactivity) {
		//Build the event for the server
		TrackingSender sender = new TrackingSender(mactivity.getApplication());
		StringBuilder evtDescr = new StringBuilder();
		evtDescr.append(" at ");
		evtDescr.append(this.getHour());
		evtDescr.append(":");
		evtDescr.append(this.getMinute());
		evtDescr.append(" on ");
		String dayString = "";
		ArrayList<String> theDayList = new ArrayList<String>();
		if(this.getMonday())
			theDayList.add("monday");
		if(this.getTuesday())
			theDayList.add("tuesday");
		if(this.getWednesday())
			theDayList.add("wednesday");
		if(this.getThursday())
			theDayList.add("thursday");
		if(this.getFriday())
			theDayList.add("friday");
		if(this.getSaturday())
			theDayList.add("saturday");
		if(this.getSunday())
			theDayList.add("sunday");

		if(theDayList.size() == 0 || theDayList.size() == 7)
			dayString = "EveryDay";
		else
			dayString = theDayList.toString();
		evtDescr.append(dayString);

		//Dispatch event to the server
		if(this.getActivate())
		{
			AlarmPlanifier.checkAndPlanifyNextAlarm(this,mactivity);
			sender.sendUserEvent(TrackingEventCategory.ALARM,TrackingEventAction.SET,"set" + evtDescr.toString());

			//We display a toast message
			String nextString = this.toStringDiff();
			if(nextString.length() > 0)
			{
				nextString = mactivity.getResources().getString(R.string.alarnIsSet) + " " + nextString;
				Toast.makeText(MyApplication.getAppContext(),nextString , Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			sender.sendUserEvent(TrackingEventCategory.ALARM,TrackingEventAction.UNSET,"unset" + evtDescr.toString());
			AlarmPlanifier.CancelAlarm(this,mactivity);
		}
	}






	//####### ASYNC FUNCTION
	/**
	 * Synchronize user info to the server with the specified action
	 * @param syncaction type of SnooziUtility.SYNC_ACTION
	 * @param provider 
	 * @param theBundeledObj bundeled object or null
	 * @return
	 */
	public static void async(SnooziUtility.SYNC_ACTION syncaction,Bundle theBundeledObj) throws IOException
	{


		Alarmendpoint.Builder endpointBuilder = new Alarmendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest httpRequest) { }
				});
		Alarmendpoint alarmEndpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		MyAlarm theAlarm = null;
		if(theBundeledObj != null)
			theAlarm = new MyAlarm(theBundeledObj);

		switch (syncaction) {

		case ALARM_UPDATE:
			if(theAlarm != null)
				theAlarm.endPointUpdate( alarmEndpoint);
			break;
		case ALARM_DELETE:
			if(theAlarm != null)
				theAlarm.endPointDelete(alarmEndpoint);
			break;

		default:
			break;
		}
	}

	/**
	 * @param alarmEndpoint
	 * @throws IOException
	 */
	private  void endPointDelete(Alarmendpoint alarmEndpoint) throws IOException {
		if(getAlarmId() != 0l)
			alarmEndpoint.removeAlarm(getAlarmId()).execute();
		SnooziUtility.trace(TRACETYPE.INFO,"OK Alarm deleted to server : " + toString());
		
	}

	/**
	 * @param alarmEndpoint
	 * @throws IOException
	 */
	private  void endPointUpdate(Alarmendpoint alarmEndpoint) throws IOException {
		if(MyUser.getMyUserId() != 0l)
		{
			// We send alarm only if this user has an id
			Alarm alrm = new Alarm();
			if(getAlarmId() != 0l)
				alrm.setId(getAlarmId());
			alrm.setActivate(getActivate());
			alrm.setHour(getHour());
			alrm.setMinute(getMinute());
			alrm.setVolume(getVolume());
			alrm.setDaystring(getDayString());
			alrm.setMonday(getMonday());
			alrm.setTuesday(getTuesday());
			alrm.setWednesday(getWednesday());
			alrm.setThursday(getThursday());
			alrm.setFriday(getFriday());
			alrm.setSaturday(getSaturday());
			alrm.setSunday(getSunday());
			alrm.setVibrate(getVibrate());

			if(MyUser.getMyUserId() != 0l)
				alrm.setUserid(MyUser.getMyUserId());
			if(getVideoId() != 0l)
				alrm.setVideoid(getVideoId());

			alrm = alarmEndpoint.updateAlarm(alrm).execute();
			if(getAlarmId() == 0l)
			{
				SnooziUtility.trace(TRACETYPE.INFO,"new Alarm server id : " + alrm.getId());
				setAlarmId(alrm.getId()); //We take the id from the server
				save();  // to save it locally
			}
			SnooziUtility.trace(TRACETYPE.INFO,"OK Alarm sended to server : " + toString());
			
		}
	}









	/**
	 * get the time of the Alarm
	 * @return
	 */
	public String toTime() {
		String theTime = "";
		if(hour < 10)
			theTime = "0";
		theTime += hour + ":";
		if(minute < 10)
			theTime += "0";
		theTime += minute;

		return theTime;
	}
	/**
	 * get the time of the Alarm
	 * @return
	 */
	@Override
	public String toString() {

		return (activate ? "ON" : "OFF") + " - " + toTime() + " - " + dayString;
	}


	/**
	 * Return de timespan to the next alarm
	 * @return 
	 * 		X days Y hours Z minutes
	 */
	public String toStringDiff()
	{
		StringBuilder result = new StringBuilder();
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		try {
			Calendar nextAlarm = AlarmPlanifier.getNextLaunchCalendar(MyApplication.getAppContext(),this, 0);

			long nextmilli = nextAlarm.getTimeInMillis();
			long nowmilli = now.getTimeInMillis();
			long diffInSeconds = (nextmilli - nowmilli)/1000;
			long diff[] = new long[] { 0, 0, 0, 0 };
			/* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
			/* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
			/* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
			/* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));

			if(diff[0] > 0)
			{
				/* days string building*/
				result.append(diff[0]);
				result.append(" ");
				if(diff[0] > 1)
					result.append(MyApplication.getAppContext().getResources().getString(R.string.days));
				else
					result.append(MyApplication.getAppContext().getResources().getString(R.string.day));
				result.append(" ");
			}
			if(diff[1] > 0)
			{
				/* hours string building*/
				result.append(diff[1]);
				result.append(" ");
				if(diff[1] > 1)
					result.append(MyApplication.getAppContext().getResources().getString(R.string.hours));
				else
					result.append(MyApplication.getAppContext().getResources().getString(R.string.hour));
				result.append(" ");
			}
			if(diff[2] > 0)
			{
				/* minute string building */
				result.append(diff[2]);
				result.append(" ");
				if(diff[2] > 1)
					result.append(MyApplication.getAppContext().getResources().getString(R.string.minutes));
				else
					result.append(MyApplication.getAppContext().getResources().getString(R.string.minute));
				result.append(" ");
			}

			if(result.length() == 0 && diff[3] > 0)
			{
				// We display second only if less than a minute
				result.append(diff[3]);
				result.append(" ");
				if(diff[3] > 1)
					result.append(MyApplication.getAppContext().getResources().getString(R.string.seconds));
				else
					result.append(MyApplication.getAppContext().getResources().getString(R.string.second));
				result.append(" ");
			}

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, e.toString());
		}

		return result.toString();
	}






	public int getId() {
		return id;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getVolume() {
		return volume;
	}

	public String getDayString() {
		return dayString;
	}

	public Boolean getMonday() {
		return monday;
	}

	public Boolean getTuesday() {
		return tuesday;
	}

	public Boolean getWednesday() {
		return wednesday;
	}

	public Boolean getThursday() {
		return thursday;
	}

	public Boolean getFriday() {
		return friday;
	}

	public Boolean getSaturday() {
		return saturday;
	}

	public Boolean getSunday() {
		return sunday;
	}

	public Boolean getActivate() {
		return activate;
	}

	public Boolean getVibrate() {
		return vibrate;
	}

	public long getAlarmId() {
		return alarmId;
	}

	public long getVideoId() {
		return videoId;
	}


	public Boolean getHasChanged() {
		return hasChanged;
	}

	public void setHour(int hour) {
		this.hour = hour;
		setHasChanged(true);		
	}

	public void setMinute(int minute) {
		this.minute = minute;
		setHasChanged(true);
	}

	public void setVolume(int volume) {
		this.volume = volume;
		setHasChanged(true);
	}

	public void setDayString(String dayString) {
		this.dayString = dayString;
		setHasChanged(true);
	}

	public void setMonday(Boolean monday) {
		this.monday = monday;
		setHasChanged(true);
	}

	public void setTuesday(Boolean tuesday) {
		this.tuesday = tuesday;
		setHasChanged(true);
	}

	public void setWednesday(Boolean wednesday) {
		this.wednesday = wednesday;
		setHasChanged(true);
	}

	public void setThursday(Boolean thursday) {
		this.thursday = thursday;
		setHasChanged(true);
	}

	public void setFriday(Boolean friday) {
		this.friday = friday;
		setHasChanged(true);
	}

	public void setSaturday(Boolean saturday) {
		this.saturday = saturday;
		setHasChanged(true);
	}

	public void setSunday(Boolean sunday) {
		this.sunday = sunday;
		setHasChanged(true);
	}

	public void setActivate(Boolean activate) {
		this.activate = activate;
		setHasChanged(true);
	}

	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
		setHasChanged(true);
	}


	public void setId(int id) {
		this.id = id;
		setHasChanged(true);
	}


	public void setVideoId(long l) {
		this.videoId = l;
		setHasChanged(true);
	}


	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
		setHasChanged(true);
	}



	public void setHasChanged(Boolean hasChanged) {
		this.hasChanged = hasChanged;
	}





}
