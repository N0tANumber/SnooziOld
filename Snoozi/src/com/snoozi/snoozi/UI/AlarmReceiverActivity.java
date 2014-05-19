package com.snoozi.snoozi.UI;


import com.fima.glowpadview.GlowPadView;
import com.fima.glowpadview.GlowPadView.OnTriggerListener;
import com.snoozi.snoozi.*;
import com.snoozi.snoozi.models.AlarmLauncher;
import com.snoozi.snoozi.models.MusicHandler;
import com.snoozi.snoozi.utils.TrackingEventType;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.TrackingSender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Vibrator;

public class AlarmReceiverActivity extends Activity  implements OnTriggerListener{


	protected static final String TAG = "ALARMRECEIVERACTIVITY";
	private MusicHandler mMediaPlayer = null;
	private GlowPadView mGlowPadView;
	
	private TrackingEventType _alarmEvent = TrackingEventType.ALARM_KILLED;
	private Vibrator _vibrator = null;
	private long _launchtime = 0;
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	
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
		Log.i(TAG,"oncreateAlarm");
		
		
		TrackingSender sender = new TrackingSender(getApplicationContext());
		sender.sendUserEvent(TrackingEventType.ALARM_LAUNCH,"", SnooziUtility.getVideoNumber(this));
		
		//If alarm is launched only one time, we disable it
		SharedPreferences settings = getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		/* we dont disable alarm anymore (never -> everyday)
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
		
		
		
		//Play sound
		mMediaPlayer = new MusicHandler(this);
		mMediaPlayer.load(SnooziUtility.getVideoUri(this), true);
		mMediaPlayer.play(5000);
		
		boolean isVibrate = settings.getBoolean("vibrate", false);
		if(isVibrate)
			_vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		// Start without a delay
		// Vibrate for 100 milliseconds
		// Sleep for 1000 milliseconds
		if (_vibrator !=null && _vibrator.hasVibrator())
		{
			long[] pattern = {0, 1000, 200};
			_vibrator.vibrate(pattern, 0);
		}
		
		 
		mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);
		mGlowPadView.setOnTriggerListener(this);
		
		// uncomment this to make sure the glowpad doesn't vibrate on touch
		// mGlowPadView.setVibrateEnabled(false);
		// uncomment this to hide targets
		mGlowPadView.setShowTargetsOnIdle(true);
		_launchtime = System.currentTimeMillis();
		
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG,"onStartAlarm");
		
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG,"onPauseAlarm");
		
		long currenttime = System.currentTimeMillis();
		if(currenttime - _launchtime > 1000)
		{
		
			Log.i(TAG,"Stop Alarm");
			stopRinginAlarm();
			
			if (_alarmEvent == TrackingEventType.ALARM_KILLED) 
			{
				Log.i(TAG,"alarm killed");
				//Reporting alarm killed
				TrackingSender sender = new TrackingSender(getApplicationContext());
				sender.sendUserEvent(_alarmEvent,"",SnooziUtility.getVideoNumber(this));
				AlarmLauncher.checkAndPlanifyNextAlarm(this);
			    
			}
			finish();
		}
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG,"onConfigurationChanged");
		
    }

	@Override
	public void onBackPressed() {
		//cancel backbutton event
		
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG,"onStop");
		
	}

	@Override
	public void onGrabbed(View v, int handle) {
		// TODO Auto-generated method stub
		Log.i(TAG,"onGrabbed");
		
	}

	@Override
	public void onReleased(View v, int handle) {
		mGlowPadView.ping();
		
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG,"onDestroyAlarm");
		
		
		if(isFinishing())
		{
			Log.i(TAG,"onDestroyAlarm Finishing");
			stopRinginAlarm();
			
		}
	}

	private void stopRinginAlarm(){
		if(mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
		if (_vibrator!=null && _vibrator.hasVibrator())
		{
			_vibrator.cancel();
			_vibrator = null;
		}
	}

	
	@Override
	public void onTrigger(View v, int target) {
		final int resId = mGlowPadView.getResourceIdForTarget(target);
		TrackingSender sender = new TrackingSender(getApplicationContext());
		switch (resId) {
		case R.drawable.ic_item_snooze:
			_alarmEvent = TrackingEventType.ALARM_SNOOZE;
			sender.sendUserEvent(_alarmEvent,"",  SnooziUtility.getVideoNumber(this));
			AlarmLauncher.planifyNextAlarm(this,5*60);
			Toast.makeText(this,getResources().getString(R.string.snoozeinfivemin) , Toast.LENGTH_LONG).show();
			finish();
			break;

		case R.drawable.ic_item_wakeup:
			_alarmEvent = TrackingEventType.ALARM_WAKEUP;
			sender.sendUserEvent(_alarmEvent,"",  SnooziUtility.getVideoNumber(this));
			stopRinginAlarm();
			AlarmLauncher.checkAndPlanifyNextAlarm(this);
			
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
		Log.i(TAG,"onActivityResult");
		if (requestCode == 1) {
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
