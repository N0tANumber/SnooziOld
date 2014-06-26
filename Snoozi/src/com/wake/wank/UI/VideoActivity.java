package com.wake.wank.UI;


import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.wake.wank.*;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.MyVideo;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class VideoActivity extends Activity {


	public enum VIDEO_STATE{
		PLAYING,
		PAUSED,
		STOPPED
	}


	private MyVideo currentVideo;
	private VideoView mVideoView;
	private ProgressBar mProgressBar;

	private VIDEO_STATE mVideoPlayback = VIDEO_STATE.STOPPED;
	private int duration = 0;
	public int current = 0;
	private myAsync _progresstask = null;
	//private AudioManager audioManager;
	//private int _musicVol;
	//private int _oldmusicVol;
	public int _videoViewCount = 0;

	private boolean _isActivityPaused = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_video);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

		mVideoView = (VideoView)findViewById(R.id.myvideoView);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		mVideoView.setLayoutParams(new LinearLayout.LayoutParams(width,height));

		currentVideo = SnooziUtility.getCurrentAlarmVideo(this);

		TextView videoTitle = (TextView) findViewById(R.id.txtinfo);
		videoTitle.setText(currentVideo.getDescription());


		// Recherchez AdView comme ressource et chargez une demande.
		Bundle bundle = new Bundle();
		bundle.putString("color_bg", "641213");
		bundle.putString("color_bg_top", "641213");
		bundle.putString("color_border", "333333");
		bundle.putString("color_link", "FFFFFF");
		bundle.putString("color_text", "cccccc");
		bundle.putString("color_url", "cc9933");

		AdMobExtras extras = new AdMobExtras(bundle);
		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		.addNetworkExtras(extras)
		.tagForChildDirectedTreatment(false)
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Émulateur
		.addTestDevice("88D53265ED709666EA324C27AAE13FC1")
		.addTestDevice("BFCEAEB7FADA53A2A79A6F7C4DD211AB")
		.build();
		adView.loadAd(adRequest);

		
		
		try {
			mVideoView.setVideoURI(Uri.parse(currentVideo.getLocalurl()));
			mVideoView.setOnPreparedListener(new OnPreparedListener() {


				public void onPrepared(MediaPlayer mp) {
					try {
						duration = mVideoView.getDuration();
						
						startPlaying();
						
					} catch (Exception e) {
						// TODO: handle exception
						SnooziUtility.trace(null, TRACETYPE.ERROR,"VideoActivity.onCreate Error : "+ e.toString());

						finish();
					}
				}
			});
			mVideoView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent ev) {
					if(ev.getAction() == MotionEvent.ACTION_DOWN){
						if(mVideoView.isPlaying())
						{
							mVideoView.pause();
						} else 
						{
							mVideoView.start();
						}
						return true;		
					} else {
						return false;
					}
				}
			});
			mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					_videoViewCount++;
					mp.start();
				}
			});
		} catch (Exception e) {
			SnooziUtility.trace(this, TRACETYPE.ERROR,"VideoActivity.onCreate Error : "+ e.toString());

		}
		
		

		//audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		//_oldmusicVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		//SharedPreferences prefs = this.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		//_musicVol =  prefs.getInt("volume", _oldmusicVol);
		//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _musicVol, 0);

		mProgressBar = (ProgressBar) findViewById(R.id.Progressbar);
		mProgressBar.setProgress(0);
		mProgressBar.setMax(100);



		


		// Like / Dislike
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);        
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected
				switch (checkedId) {
				case R.id.radioLike:
					currentVideo.addLike(1);
					break;
				case R.id.radioDislike:
					currentVideo.addLike(-1);
					break;

				default:
					break;
				}
				refreshInfotext();

			}
		});

		refreshInfotext();


		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		try {
			EasyTracker.getInstance().activityStart(this);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 */
	private void refreshInfotext() {
		String info = "";
		info = (currentVideo.getViewcount() + currentVideo.getMyviewcount()) + " wakeup - ";
		info += (currentVideo.getLike() + currentVideo.getMylike()) + " like(s)";

		TextView txtinfo = (TextView) findViewById(R.id.txtinfo);
		txtinfo.setText(info);
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		_isActivityPaused =true;

	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		_isActivityPaused = false;
	}


	@Override
	protected void onStop() {
		super.onStop();
		// TODO Auto-generated method stub
		try {

			//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _oldmusicVol, 0);
			//mVideoView.getCurrentPosition();
			TrackingSender sender = new TrackingSender(getApplicationContext(),getApplication());

			if(_videoViewCount >0)
			{
				currentVideo.addViewcount(_videoViewCount);
				currentVideo.setMyviewcount(currentVideo.getMyviewcount() + _videoViewCount);
				currentVideo.saveAndSync(this);

				sender.sendUserEvent(TrackingEventCategory.VIDEO,TrackingEventAction.VIEWED,_videoViewCount + " time(s)", currentVideo.getVideoid());
			}
			else
			{
				currentVideo.addViewcount(1);
				currentVideo.setMyviewcount(currentVideo.getMyviewcount() + 1);
				currentVideo.saveAndSync(this);

				sender.sendUserEvent(TrackingEventCategory.VIDEO,TrackingEventAction.CANCELED,"canceled at " + Math.round(current/1000.0f) +"s./" + Math.round(duration/1000.0f) + "s.", currentVideo.getVideoid()  );
			}

			TrackingSender likesender = new TrackingSender(getApplicationContext(),getApplication());

			if(currentVideo.getMylike() != 0)
				likesender.sendUserEvent(TrackingEventCategory.VIDEO,TrackingEventAction.RATING, currentVideo.getMylike() +"", currentVideo.getVideoid());



			/*
		RadioButton radioLike = (RadioButton) findViewById(R.id.radioLike);
		RadioButton radioDislike = (RadioButton) findViewById(R.id.radioDislike);
		sender = new TrackingSender(getApplicationContext());
		if(radioLike.isChecked())
		{
			sender.sendUserEvent(TrackingEventType.VIDEO_RATING, "1", currentVideo.getVideoid());
		}
		else if(radioDislike.isChecked())
		{
			sender.sendUserEvent(TrackingEventType.VIDEO_RATING, "-1", currentVideo.getVideoid());
		}	
			 */		

			//prepare next video
			SnooziUtility.unsetVideo( );
			currentVideo = null;

			EasyTracker.getInstance().activityStop(this);

			stopPlaying();

			Intent returnIntent = new Intent();
			setResult(RESULT_OK, returnIntent);  

		} catch (Exception e) {
			SnooziUtility.trace(this, TRACETYPE.ERROR,"VideoActivity.onStop Error : "+ e.toString());

		}


		finish();
	}



	public void startPlaying()
	{
		mVideoView.start();
		mVideoPlayback = VIDEO_STATE.PLAYING;
		if(_progresstask == null)
			_progresstask = new myAsync();

		_progresstask.execute();
	}	




	public void stopPlaying()
	{
		mVideoPlayback = VIDEO_STATE.STOPPED;
		mVideoView.stopPlayback();
		mProgressBar.setProgress(0);
		_progresstask = null;
	}

	private class myAsync extends AsyncTask<Void, Integer, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			int theprecent = 0;

			do {
				if(!_isActivityPaused)
				{
					current  = mVideoView.getCurrentPosition();
					//System.out.println("duration - " + duration + " current- "
					//        + current);
					try {
						if(duration > 0)
						{
							int currentPrecent = (int) (current * 100 / duration);
							if(theprecent != currentPrecent)
							{
								publishProgress(currentPrecent);
								theprecent = currentPrecent ;
							}

						}
						Thread.sleep(200);

					} catch (Exception e) {
					}
				}
			} while (mVideoPlayback != VIDEO_STATE.STOPPED);

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// System.out.println(values[0]);
			mProgressBar.setProgress(values[0]);
		}
	}
}
