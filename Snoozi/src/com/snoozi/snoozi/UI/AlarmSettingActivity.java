package com.snoozi.snoozi.UI;

import java.util.ArrayList;
import java.util.List;

import com.snoozi.snoozi.*;
import com.snoozi.snoozi.models.AlarmPlanifier;
import com.snoozi.snoozi.utils.TrackingEventType;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.TrackingSender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Class to manage the Alarm panel setting and save it to the userpref. 
 * Calls the class Alarm to set an alarm with a broadcastreceiver
 * 
 * @author CtrlX
 *
 */
public class AlarmSettingActivity extends Activity {
	private static final int TIME_PICKER_INTERVAL=5;
	private boolean mIgnoreEvent=false;

	private ToggleButton chkactivate;
	private OnCheckedChangeListener activateListener = null;

	private TimePicker picker;
	private TextView txtdays;
	private ArrayList<Integer> currentDays;

	private CheckedTextView chkvibrate;
	private OnClickListener vibrateListener = null;

	private OnClickListener repeatListener = null;
	private AlertDialog repeatdialog;
	private SeekBar volumebar;
	private AudioManager audioManager; 

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// FirstLaunch tracking
		SharedPreferences settings = getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		boolean isFirstLaunch = settings.getBoolean("firstLaunch", true);
		TrackingSender sender = new TrackingSender(getApplicationContext());
		if(isFirstLaunch)
		{
			sender.sendUserEvent(TrackingEventType.APP_FIRSTLAUNCH);
			//Save firstlaunch state
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("firstLaunch", false);
			editor.commit();
		}
		else
			sender.sendUserEvent(TrackingEventType.APP_LAUNCH);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_alarmsetting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

		SharedPreferences settings = getSharedPreferences(SnooziUtility.PREFS_NAME, 0);


		/*Activate setup*/
		activateListener = new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				SavePref();

				//Build the event for the server
				TrackingSender sender = new TrackingSender(getApplicationContext());
				StringBuilder evtDescr = new StringBuilder();
				evtDescr.append(" at ");
				evtDescr.append(picker.getCurrentHour());
				evtDescr.append(":");
				evtDescr.append(picker.getCurrentMinute());
				evtDescr.append(" on ");
				evtDescr.append(txtdays.getText().toString());

				//Dispatch event to the server
				if(isChecked)
				{
					AlarmPlanifier.checkAndPlanifyNextAlarm(getApplicationContext());
					sender.sendUserEvent(TrackingEventType.ALARM_SET,"set" + evtDescr.toString());
					
					
					//We display a toast message
					String nextString = AlarmPlanifier.getNextAlarmAsString(getApplicationContext());
					if(nextString.length() > 0)
					{
						nextString = getResources().getString(R.string.alarnIsSet) + " " + nextString;
						Toast.makeText(getApplicationContext(),nextString , Toast.LENGTH_LONG).show();
					}
					
					
				}
				else
				{
					sender.sendUserEvent(TrackingEventType.ALARM_UNSET,"unset" + evtDescr.toString());
					AlarmPlanifier.CancelAlarm(getApplicationContext());
				}


				//when alarm is set, we disable all other controls
				LinearLayout layout = (LinearLayout) findViewById(R.id.inputlayout);
				for (int i = 0; i < layout.getChildCount(); i++) {
					View child = layout.getChildAt(i);
					child.setEnabled(!isChecked);
				}
			}

		};
		chkactivate = (ToggleButton) findViewById(R.id.chk_activate);
		//Update Button state from Pref
		boolean isActivated = settings.getBoolean("activate", false);
		if(isActivated)
		{
			//alarm is set, so we disable all other controls
			chkactivate.setChecked(isActivated);
			LinearLayout layout = (LinearLayout) findViewById(R.id.inputlayout);
			for (int i = 0; i < layout.getChildCount(); i++) {
				View child = layout.getChildAt(i);
				child.setEnabled(!isActivated);
			}
		}
		chkactivate.setOnCheckedChangeListener(activateListener);



		/* Time picker setup */
		picker = (TimePicker) findViewById(R.id.timePicker);
		//Update Picker state from Pref
		picker.setCurrentHour(settings.getInt("hour", 7));
		picker.setCurrentMinute(settings.getInt("minute", 30));
		TimePicker.OnTimeChangedListener mTimePickerListener=new TimePicker.OnTimeChangedListener(){
			public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute){
				if (mIgnoreEvent)
					return;
				if (minute%TIME_PICKER_INTERVAL!=0){
					int minuteFloor=minute-(minute%TIME_PICKER_INTERVAL);
					minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
					if (minute==60)
						minute=0;
					mIgnoreEvent=true;
					timePicker.setCurrentMinute(minute);
					mIgnoreEvent=false;
				}

				if(! mIgnoreEvent)
				{
					//Saving the current hour / minute
					SavePref();
				}
			}
		};
		picker.setOnTimeChangedListener(mTimePickerListener);


		/*Day setup */
		txtdays = (TextView) findViewById(R.id.txtdays);
		//Update text from Pref
		txtdays.setText(settings.getString("dayString", getResources().getString(R.string.Everyday)));
		repeatListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				repeatdialog.show();
				SavePref();
			}
		};

		// Day DislogBox Declaration
		// arraylist to keep the selected items
		currentDays=new ArrayList<Integer>();
		final ArrayList<Integer> seletedDays=new ArrayList<Integer>();
		boolean[] checkedday = {false,false,false,false,false,false,false};
		//Update Days state from Pref
		if(settings.getBoolean("monday", false))
		{
			currentDays.add(0);
			seletedDays.add(0);
			checkedday[0] = true;
		}
		if(settings.getBoolean("tuesday", false))
		{
			currentDays.add(1);
			seletedDays.add(1);
			checkedday[1] = true;
		}
		if(settings.getBoolean("wednesday", false))
		{
			currentDays.add(2);
			seletedDays.add(2);
			checkedday[2] = true;

		}
		if(settings.getBoolean("thursday", false))
		{
			currentDays.add(3);
			seletedDays.add(3);
			checkedday[3] = true;

		}
		if(settings.getBoolean("friday", false))
		{
			currentDays.add(4);
			seletedDays.add(4);
			checkedday[4] = true;

		}
		if(settings.getBoolean("saturday", false))
		{
			currentDays.add(5);
			seletedDays.add(5);
			checkedday[5] = true;

		}
		if(settings.getBoolean("sunday", false))
		{
			currentDays.add(6);
			seletedDays.add(6);
			checkedday[6] = true;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.Repeat));
		builder.setMultiChoiceItems(getResources().getTextArray(R.array.dayofweek), checkedday,
				new DialogInterface.OnMultiChoiceClickListener() {
			// indexSelected contains the index of item (of which checkbox checked)
			@Override
			public void onClick(DialogInterface dialog, int indexSelected,
					boolean isChecked) {
				if (isChecked) {
					// If the user checked the item, add it to the selected items
					// write your code when user checked the checkbox 
					seletedDays.add(indexSelected);
				} else if (seletedDays.contains(indexSelected)) {
					// Else, if the item is already in the array, remove it 
					// write your code when user Uchecked the checkbox 
					seletedDays.remove(Integer.valueOf(indexSelected));
				}
			}
		})
		// Set the action buttons
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Update the Day Label
				String dayString = "";
				int daycount = 0;
				List<String> slist = new ArrayList<String> ();
				currentDays.clear();

				// We Use a iday array for sorting the day correctly ( by index)
				boolean[] iday = {false,false,false,false,false,false,false};
				for (Integer day : seletedDays) {
					currentDays.add(day);
					iday[day] = true;
				}
				
				for (int i = 0; i < iday.length; i++) {
					boolean isChecked = iday[i];
					if(isChecked)
					{
						daycount++;
						slist.add(getResources().getStringArray(R.array.dayofweekshort)[i]);
					}
				}

				if(daycount == 0 || daycount == 7)
					dayString = getResources().getString(R.string.Everyday);
				else
					dayString = android.text.TextUtils.join(",",slist.toArray());
				txtdays.setText(dayString);

				SavePref();

			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				seletedDays.clear();
				for (Integer day : currentDays) {
					seletedDays.add(day);
				}
			}
		});
		repeatdialog = builder.create();//AlertDialog dialog; create like this outside onClick

		LinearLayout btnRepeat = (LinearLayout) findViewById(R.id.BtnRepeat);
		btnRepeat.setOnClickListener(repeatListener);


		/*Vibrate setup*/
		vibrateListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				chkvibrate.toggle();
				SavePref();

			}
		};
		chkvibrate = (CheckedTextView) findViewById(R.id.chk_vibrate);
		//Update Button state from Pref
		chkvibrate.setChecked(settings.getBoolean("vibrate", true));
		chkvibrate.setOnClickListener(vibrateListener);

		/* Alarm Volume setup */
		volumebar = (SeekBar) findViewById(R.id.volumeseekBar);
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volumebar.setMax(maxVol);
		volumebar.setProgress(settings.getInt("volume", (int)Math.floor(maxVol*0.60)));
		
		volumebar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) 
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) 
            {
            } 

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) 
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
                SavePref();
            }
        });
		//audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager
        //        .getStreamMaxVolume(AudioManager.STREAM_ALARM),  AudioManager.FLAG_PLAY_SOUND);

		
		


	}


	/**
	 * Save Current Alarm setting to User Pref file
	 */
	private void SavePref()
	{
		this.getApplicationContext();
		SharedPreferences prefs = this.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("activate", chkactivate.isChecked());
		editor.putInt("hour", picker.getCurrentHour());
		editor.putInt("minute", picker.getCurrentMinute());
		editor.putString("dayString", txtdays.getText().toString() );

		editor.putBoolean("monday",currentDays.contains(0));
		editor.putBoolean("tuesday",currentDays.contains(1));
		editor.putBoolean("wednesday",currentDays.contains(2));
		editor.putBoolean("thursday",currentDays.contains(3));
		editor.putBoolean("friday",currentDays.contains(4));
		editor.putBoolean("saturday",currentDays.contains(5));
		editor.putBoolean("sunday",currentDays.contains(6));

		editor.putBoolean("vibrate", chkvibrate.isChecked());
		editor.putInt("volume", volumebar.getProgress());
		
		editor.commit();


	}

}
