package com.wake.wank.UI;


import com.fima.glowpadview.GlowPadView;
import com.fima.glowpadview.GlowPadView.OnTriggerListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.wake.wank.*;
import com.wake.wank.models.AlarmPlanifier;
import com.wake.wank.models.AlarmSound;
import com.wake.wank.models.MyVideo;
import com.wake.wank.services.WakeupLaunchService;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Vibrator;

public class WakeupActivity extends Activity  implements OnTriggerListener{


	protected static final String TAG = "ALARMRECEIVERACTIVITY";
	private AlarmSound mMediaPlayer = null;
	private GlowPadView mGlowPadView;
	
	private TrackingEventAction _alarmEvent = TrackingEventAction.KILLED;
	private Vibrator _vibrator = null;
	//private long _launchtime = 0;
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	
	private MyVideo currentVideo = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | 
			    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
			    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
			    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
	            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
			    WindowManager.LayoutParams.FLAG_FULLSCREEN | 
			    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
			    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
			    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
	            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       	
		setContentView(R.layout.activity_wakescreen);

		
		currentVideo = SnooziUtility.getCurrentAlarmVideo(this);
		SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.oncreateAlarm with video " +  currentVideo.getLocalurl());
		
		TrackingSender sender = new TrackingSender(getApplicationContext(),getApplication());
		sender.sendUserEvent(TrackingEventCategory.ALARM,TrackingEventAction.LAUNCH,"",currentVideo.getVideoid());
		
		SharedPreferences settings = getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		
		AlarmPlanifier.checkAndPlanifyNextAlarm(this);
		
		
		/* we dont disable alarm anymore (never -> everyday)
		//If alarm is launched only one time, we disable it
		if(!(settings.getBoolean("monday", false) ||
				settings.getBoolean("tuesday", false) ||
				settings.getBoolean("wednesday", false) ||
				settings.getBoolean("thursday", false) ||
				settings.getBoolean("friday", false) ||
				settings.getBoolean("saturday", false) ||
				settings.getBoolean("sunday", false)))
		{
			//Disabling the alarm
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("activate", false);
			editor.commit();
		}*/
		
		
		
		
		// Start without a delay
		// Vibrate for 100 milliseconds
		// Sleep for 1000 milliseconds
		boolean isVibrate = settings.getBoolean("vibrate", false);
		if(isVibrate)
			_vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		 
		mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);
		mGlowPadView.setOnTriggerListener(this);
		
		// uncomment this to make sure the glowpad doesn't vibrate on touch
		// mGlowPadView.setVibrateEnabled(false);
		// uncomment this to hide targets
		mGlowPadView.setShowTargetsOnIdle(true);
		//_launchtime = System.currentTimeMillis();
	}
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onStartAlarm");
		
		try {
			if(!SnooziUtility.DEV_MODE)
				EasyTracker.getInstance().activityStart(this);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onResumeAlarm");
		startRingingAlarm();
		
		try {
			
			com.facebook.AppEventsLogger.activateApp(this, "250270258502553");
		} catch (Exception e) {
			SnooziUtility.trace(this, TRACETYPE.ERROR, "Facebook registration error : " + e.toString());
			
		}
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onPauseAlarm");
		stopRingingAlarm();
		/*
		long currenttime = System.currentTimeMillis();
		if(currenttime - _launchtime > 1000)
		{
		
			SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity Stop Alarm");
			stopRinginAlarm();
			
			if (_alarmEvent == TrackingEventType.ALARM_KILLED) 
			{
				SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity Alarm Killed");
				//Reporting alarm killed
				TrackingSender sender = new TrackingSender(getApplicationContext());
				sender.sendUserEvent(_alarmEvent,"",currentVideo.getVideoid());
				
			}
			finish();
		}
		*/
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
        SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onConfigurationChanged");
		
		
    }

	@Override
	public void onBackPressed() {
		//cancel backbutton event
		
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onStop");
		try {
			if(!SnooziUtility.DEV_MODE)
				EasyTracker.getInstance().activityStop(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onGrabbed(View v, int handle) {
		// TODO Auto-generated method stub
		SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onGrabbed");
		
	}

	@Override
	public void onReleased(View v, int handle) {
		mGlowPadView.ping();
		
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		
		if(isFinishing())
		{
			SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onDestroy with Finishing");
			stopRingingAlarm();
			if(mMediaPlayer != null)
			{
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			_vibrator = null;
			//SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity Stop Alarm");
			//stopRinginAlarm();
			
			if (_alarmEvent == TrackingEventAction.KILLED) 
			{
				SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity Alarm Killed");
				//Reporting alarm killed
				TrackingSender sender = new TrackingSender(getApplicationContext(),getApplication());
				sender.sendUserEvent(TrackingEventCategory.ALARM, _alarmEvent,"",currentVideo.getVideoid());
				
			}
			WakeupLaunchService.isrunning = false;
		}else
			SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onDestroy");
	}
	
	
	
	private void startRingingAlarm()
	{
		if(mMediaPlayer == null)
		{
			//Play sound
			mMediaPlayer = new AlarmSound(this);
			try {
				mMediaPlayer.load(SnooziUtility.getVideoUri(this), true);
				
			} catch (Exception e) {
				//TODO : Error while loading video, playing fallback sound
				SnooziUtility.trace(this, TRACETYPE.ERROR,"AlarmReceiverActivity.startRingingAlarm Exception :  " +  e.toString());
			}
			
		}

		if(mMediaPlayer != null)
			mMediaPlayer.play(3000);
		
		if (_vibrator !=null && _vibrator.hasVibrator())
		{
			long[] pattern = {0, 1000, 200};
			_vibrator.vibrate(pattern, 0);
		}
	}

	private void stopRingingAlarm(){
		if(mMediaPlayer != null)
			mMediaPlayer.pause();
			
		if (_vibrator!=null && _vibrator.hasVibrator())
		{
			_vibrator.cancel();
			
		}
	}

	
	@Override
	public void onTrigger(View v, int target) {
		final int resId = mGlowPadView.getResourceIdForTarget(target);
		TrackingSender sender = new TrackingSender(getApplicationContext(),getApplication());
		switch (resId) {
		case R.drawable.ic_item_snooze:
			_alarmEvent = TrackingEventAction.SNOOZE;
			sender.sendUserEvent(TrackingEventCategory.ALARM, _alarmEvent,"",  currentVideo.getVideoid());
			AlarmPlanifier.planifyNextAlarm(this,5*60);
			Toast.makeText(this,getResources().getString(R.string.snoozeinfivemin) , Toast.LENGTH_LONG).show();
			finish();
			break;

		case R.drawable.ic_item_wakeup:
			_alarmEvent = TrackingEventAction.WAKEUP;
			sender.sendUserEvent(TrackingEventCategory.ALARM,_alarmEvent,"",  currentVideo.getVideoid());
			stopRingingAlarm();
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			_vibrator = null;
			Intent intent = new Intent(this, VideoActivity.class);
			startActivityForResult(intent, 1);
			
			
			break;
		default:
			// Code should never reach here.
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SnooziUtility.trace(this, TRACETYPE.INFO,"AlarmReceiverActivity.onActivityResult");
		if (resultCode == RESULT_OK) 
		{
			SnooziUtility.trace(this, TRACETYPE.INFO,".....onActivityResult RESULT OK");
				
			finish();
		  }
	}


	@Override
	public void onGrabbedStateChange(View v, int handle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinishFinalAnimation() {
		// TODO Auto-generated method stub

	}
	


}
