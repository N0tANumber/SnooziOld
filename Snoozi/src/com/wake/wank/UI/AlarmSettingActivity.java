package com.wake.wank.UI;

import java.util.ArrayList;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.wake.wank.*;
import com.wake.wank.models.MyAlarm;
import com.wake.wank.utils.SnooziUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Class to manage the Alarm panel setting and save it to the userpref. 
 * Calls the class Alarm to set an alarm with a broadcastreceiver
 * 
 * @author CtrlX
 *
 */
public class AlarmSettingActivity extends Activity {
	
	private CheckedTextView chkactivate;

	private TimePicker picker;
	private TextView txtdays;
	private ArrayList<Integer> currentDays;

	private CheckedTextView chkvibrate;
	private OnClickListener vibrateListener = null;

	private OnClickListener repeatListener = null;
	private AlertDialog repeatdialog;
	private SeekBar volumebar;
	private AudioManager audioManager;
	private OnClickListener activateListener;
	private TextView BtnDone;
	private TextView BtnCancel;
	
	private MyAlarm currentAlarm; 
	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		try {
			if(!SnooziUtility.DEV_MODE)
				EasyTracker.getInstance().activityStart(this);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try {
			if(!SnooziUtility.DEV_MODE)
				EasyTracker.getInstance().activityStop(this);

		} catch (Exception e) {
			// TODO: handle exception
		}
		finish();

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_alarmsetting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

		Intent data = getIntent();
		currentAlarm = (MyAlarm) data.getParcelableExtra("alarm");
		


		chkactivate = (CheckedTextView) findViewById(R.id.chk_activate);
		chkactivate.setChecked(currentAlarm.getActivate());
		activateListener	 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				chkactivate.toggle();
				//SavePref();

			}
		};
		chkactivate.setOnClickListener(activateListener);

		/* Time picker setup */
		picker = (TimePicker) findViewById(R.id.timePicker);
		//Update Picker state from Pref
		picker.setCurrentHour(currentAlarm.getHour());
		picker.setCurrentMinute(currentAlarm.getMinute());



		/*Day setup */
		txtdays = (TextView) findViewById(R.id.txtdays);
		//Update text from Pref
		
		String theDays = currentAlarm.getDayString();
		if(theDays.equals(""))
			theDays =  getResources().getString(R.string.Everyday);
		txtdays.setText(theDays);
		repeatListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				repeatdialog.show();
				//SavePref();
			}
		};

		// Day DislogBox Declaration
		// arraylist to keep the selected items
		currentDays=new ArrayList<Integer>();
		final ArrayList<Integer> seletedDays=new ArrayList<Integer>();
		boolean[] checkedday = {false,false,false,false,false,false,false};
		//Update Days state from Pref
		if(currentAlarm.getMonday())
		{
			currentDays.add(0);
			seletedDays.add(0);
			checkedday[0] = true;
		}
		if(currentAlarm.getTuesday())
		{
			currentDays.add(1);
			seletedDays.add(1);
			checkedday[1] = true;
		}
		if(currentAlarm.getWednesday())
		{
			currentDays.add(2);
			seletedDays.add(2);
			checkedday[2] = true;

		}
		if(currentAlarm.getThursday())
		{
			currentDays.add(3);
			seletedDays.add(3);
			checkedday[3] = true;

		}
		if(currentAlarm.getFriday())
		{
			currentDays.add(4);
			seletedDays.add(4);
			checkedday[4] = true;

		}
		if(currentAlarm.getSaturday())
		{
			currentDays.add(5);
			seletedDays.add(5);
			checkedday[5] = true;

		}
		if(currentAlarm.getSunday())
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

				//SavePref();

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
		vibrateListener	 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				chkvibrate.toggle();
				//SavePref();

			}
		};
		chkvibrate = (CheckedTextView) findViewById(R.id.chk_vibrate);
		//Update Button state from Pref
		chkvibrate.setChecked(currentAlarm.getVibrate());
		chkvibrate.setOnClickListener(vibrateListener);

		/* Alarm Volume setup */
		volumebar = (SeekBar) findViewById(R.id.volumeseekBar);
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volumebar.setMax(maxVol);
		if(currentAlarm.getVolume() == -1)
			volumebar.setProgress((int)Math.floor(maxVol*0.50));
		else
			volumebar.setProgress(currentAlarm.getVolume());

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
				//SavePref();
			}
		});
		//audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager
				//        .getStreamMaxVolume(AudioManager.STREAM_ALARM),  AudioManager.FLAG_PLAY_SOUND);

		BtnDone = (TextView)findViewById(R.id.BtnDone);
		BtnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SavePref();
				Intent returnIntent = new Intent();
				returnIntent.putExtra("alarm",currentAlarm);
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});
		BtnCancel = (TextView)findViewById(R.id.BtnCancel);
		BtnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);  
				finish();
			}
		});

	}


	/**
	 * Save Current Alarm setting to User Pref file
	 */
	private void SavePref()
	{
		//this.getApplicationContext();
		currentAlarm.setActivate( chkactivate.isChecked());
		currentAlarm.setHour( picker.getCurrentHour());
		currentAlarm.setMinute( picker.getCurrentMinute());
		currentAlarm.setDayString(txtdays.getText().toString() );

		currentAlarm.setMonday( currentDays.contains(0));
		currentAlarm.setTuesday( currentDays.contains(1));
		currentAlarm.setWednesday( currentDays.contains(2));
		currentAlarm.setThursday( currentDays.contains(3));
		currentAlarm.setFriday( currentDays.contains(4));
		currentAlarm.setSaturday( currentDays.contains(5));
		currentAlarm.setSunday( currentDays.contains(6));

		currentAlarm.setVibrate( chkvibrate.isChecked());
		currentAlarm.setVolume( volumebar.getProgress());

	}

}
