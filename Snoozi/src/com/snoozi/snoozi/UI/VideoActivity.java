package com.snoozi.snoozi.UI;


import com.snoozi.snoozi.*;
import com.snoozi.snoozi.models.MyVideo;
import com.snoozi.snoozi.utils.TrackingEventType;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.TrackingSender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
	private AudioManager audioManager;
	private int _musicVol;
	private int _oldmusicVol;
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
		
		TextView videoTitle = (TextView) findViewById(R.id.videoTitle);
		videoTitle.setText(currentVideo.getDescription());
		
		mVideoView.setVideoURI(Uri.parse(currentVideo.getLocalurl()));
		
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		_oldmusicVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		SharedPreferences prefs = this.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		_musicVol =  prefs.getInt("volume", _oldmusicVol);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _musicVol, 0);
		
		mProgressBar = (ProgressBar) findViewById(R.id.Progressbar);
		mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        

        
        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            
			public void onPrepared(MediaPlayer mp) {
                duration = mVideoView.getDuration();
                
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

        startPlaying();
        
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
		// TODO Auto-generated method stub
		stopPlaying();
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _oldmusicVol, 0);
		mVideoView.getCurrentPosition();
		
		TrackingSender sender = new TrackingSender(getApplicationContext());
		if(_videoViewCount >0)
		{
			currentVideo.addViewcount(_videoViewCount);
			currentVideo.setMyviewcount(currentVideo.getMyviewcount() + _videoViewCount);
			
			sender.sendUserEvent(TrackingEventType.VIDEO_VIEWED,_videoViewCount + " time(s)", currentVideo.getVideoid());
		}
		else
		{
			currentVideo.addViewcount(1);
			currentVideo.setMyviewcount(currentVideo.getMyviewcount() + 1);
			
			sender.sendUserEvent(TrackingEventType.VIDEO_CANCELED,"canceled at " + Math.round(current/1000.0f) +"s./" + Math.round(duration/1000.0f) + "s.", currentVideo.getVideoid()  );
		}
		
		RadioButton radioLike = (RadioButton) findViewById(R.id.radioLike);
		RadioButton radioDislike = (RadioButton) findViewById(R.id.radioDislike);
		sender = new TrackingSender(getApplicationContext());
		if(radioLike.isChecked())
		{
			currentVideo.addLike(1);
			sender.sendUserEvent(TrackingEventType.VIDEO_RATING, "1", currentVideo.getVideoid());
		}
		else if(radioDislike.isChecked())
		{
			currentVideo.addDislike(1);
			sender.sendUserEvent(TrackingEventType.VIDEO_RATING, "-1", currentVideo.getVideoid());
		}			
		
		//prepare next video
		currentVideo.saveAndSync(this);
		SnooziUtility.unsetVideo( );
		currentVideo = null;
		
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);  
		
		super.onStop();
		
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
