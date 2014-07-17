package com.wake.wank.UI;



import com.wake.wank.R;
import com.wake.wank.models.MyVideo;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FragmentVideo extends Fragment {

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
	private TextView mVideoTitle;
	private ViewGroup rootView;




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_video, container, false);



		mVideoView = (VideoView)rootView.findViewById(R.id.myvideoView);
		mVideoTitle = (TextView) rootView.findViewById(R.id.txtinfo);
		mProgressBar = (ProgressBar)rootView.findViewById(R.id.Progressbar);
		RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup1);        





		// Recherchez AdView comme ressource et chargez une demande.
		/*		Bundle bundle = new Bundle();
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
		 */


		mProgressBar.setProgress(0);
		mProgressBar.setMax(100);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		mVideoView.setLayoutParams(new LinearLayout.LayoutParams(width,height));


		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				try {
					duration = mVideoView.getDuration();

					startPlaying();

				} catch (Exception e) {
					// TODO: handle exception
					SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.prepareVideo Error : "+ e.toString());

					getActivity().finish();
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




		// Like / Dislike
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected
				if(currentVideo != null)
				{

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
					refreshInfo();
				}

			}
		});
		if(currentVideo != null)
			openVideo(currentVideo);

		return rootView;

	}



	/**
	 * @param currentVideo 
	 * 
	 */
	public void openVideo(MyVideo video) {
		if(video == null)
		{
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.openVideo :  video is NULL");
			return; // no video
		}


		try {
			setCurrentVideo(video);
			if(mVideoView != null)
			{
				mVideoView.setVideoURI(Uri.parse(currentVideo.getLocalurl()));
				//refreshInfo();
			}

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.openVideo Error : "+ e.toString());

		}

	}

	public void closeVideo() {
		if(currentVideo == null)
		{
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.closeVideo :  video is NULL");
			return; // no video
		}
		try {

			//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _oldmusicVol, 0);
			//mVideoView.getCurrentPosition();
			TrackingSender sender = new TrackingSender(getActivity().getApplication());

			if(_videoViewCount >0)
			{
				currentVideo.addViewcount(_videoViewCount);
				if(currentVideo.save() > 0)
				{
					SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.VIDEO_RATING,currentVideo);
					SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.USER_VIEW,currentVideo);
				}


				sender.sendUserEvent(TrackingEventCategory.VIDEO,TrackingEventAction.VIEWED,_videoViewCount + " time(s)", currentVideo.getVideoid());
			}
			else
			{
				currentVideo.addViewcount(1);
				if(currentVideo.save() > 0)
				{
					SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.VIDEO_RATING,currentVideo);
					SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.USER_VIEW,currentVideo);

				}

				sender.sendUserEvent(TrackingEventCategory.VIDEO,TrackingEventAction.CANCELED,"canceled at " + Math.round(current/1000.0f) +"s./" + Math.round(duration/1000.0f) + "s.", currentVideo.getVideoid()  );
			}

			TrackingSender likesender = new TrackingSender(getActivity().getApplication());

			if(currentVideo.getMylike() != 0)
				likesender.sendUserEvent(TrackingEventCategory.VIDEO,TrackingEventAction.RATING, currentVideo.getMylike() +"", currentVideo.getVideoid());

			stopPlaying();

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.closeVideo Error : "+ e.toString());

		}
	}






	/**
	 * 
	 */
	public void refreshInfo() {

		
		mVideoTitle.setText(currentVideo.getDescription());


		String info = "";
		info = (currentVideo.getViewcount() + currentVideo.getMyviewcount()) + " wakeup - ";
		info += (currentVideo.getLike() + currentVideo.getMylike()) + " like(s)";

		TextView txtinfo = (TextView) rootView.findViewById(R.id.txtinfo);
		txtinfo.setText(info);
	}



	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		_isActivityPaused =true;

	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		_isActivityPaused = false;
	}







	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		SnooziUtility.trace(TRACETYPE.INFO, "FragmentVideo.onStop : ");

		stopPlaying();
		
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

	public MyVideo getCurrentVideo() {
		return currentVideo;
	}

	public void setCurrentVideo(MyVideo currentVideo) {
		this.currentVideo = currentVideo;
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
