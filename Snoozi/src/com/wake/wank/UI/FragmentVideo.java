package com.wake.wank.UI;



import java.io.File;

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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

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
	private TextView mTxtwakeup;
	private TextView mTxtlike;
	private ImageView mBtnLike;
	private EditText mEditComm;
	private Button mBtnComm;




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_video, container, false);



		mVideoView = (VideoView)rootView.findViewById(R.id.myvideoView);
		mVideoTitle = (TextView) rootView.findViewById(R.id.txtinfo);
		mVideoTitle.setClickable(true);
		mVideoTitle.setMovementMethod(LinkMovementMethod.getInstance());

		mProgressBar = (ProgressBar)rootView.findViewById(R.id.Progressbar);
		mTxtwakeup = (TextView) rootView.findViewById(R.id.txtViewCount);
		mTxtlike = (TextView) rootView.findViewById(R.id.txtLikeCount);
		mBtnLike = (ImageView) rootView.findViewById(R.id.imgBtnLike);

		mEditComm = (EditText) rootView.findViewById(R.id.editComm);
		mBtnComm =  (Button) rootView.findViewById(R.id.btnSendComm);






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
					SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.prepareVideo Error : "+ e.toString());
					stopPlaying();

				}
			}
		});
		mVideoView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if(ev.getAction() == MotionEvent.ACTION_UP){
					SnooziUtility.trace(TRACETYPE.INFO, "ACTION UP ");
					if(mVideoView.isPlaying())
					{
						mVideoView.pause();
					} else 
					{
						mVideoView.start();
					}
					return true;		
				} else if(ev.getAction() == MotionEvent.ACTION_DOWN)
				{
					SnooziUtility.trace(TRACETYPE.INFO, "ACTION DOWN ");

					return true;
				}else
					return false;
			}
		});
		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				try {
					_videoViewCount++;
					mp.start();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});



		mBtnLike.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(currentVideo != null)
				{
					currentVideo.addLike(1);
					refreshInfo();

				}
				return false;
			}
		});

		mBtnComm.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(currentVideo != null)
				{
					String comment= mEditComm.getText().toString();
					comment = comment.trim();
					if(comment.length() > 0)
					{
						// We need to send the message
						mBtnComm.setActivated(false);
						mBtnComm.setText(getResources().getString(R.string.sending));
						mEditComm.setActivated(false);
						
						new CommentSender().execute(comment);
			            
					}
					
					
				}
				return false;
			}
		});



		return rootView;

	}



	/**
	 * @param currentVideo 
	 * 
	 */
	public Boolean openVideo(MyVideo video) {
		if(video == null)
		{
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.openVideo :  video is NULL");
			return false; // no video
		}


		try {
			setCurrentVideo(video);
			if(mVideoView != null)
			{
				Uri theUri = Uri.parse(currentVideo.getLocalurl());
				File file = new File(theUri.getPath());
				if(file.exists() || currentVideo.getFilestatus().equals("DUMMY"))
					mVideoView.setVideoURI(theUri);
				else
				{
					mVideoView.setVideoURI(null);
					return false;
				}
				refreshInfo();
			}

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.openVideo Error : "+ e.toString());
			stopPlaying();

			return false;

		}
		return true;

	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		_videoViewCount = 0;
		if(currentVideo != null)
			openVideo(currentVideo);

	}



	public void closeVideo() {
		SnooziUtility.trace(TRACETYPE.INFO, "FragmentVideo.closeVideo begin");
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


		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.closeVideo Error : "+ e.toString());

		}finally{
			stopPlaying();

		}
	}






	/**
	 * 
	 */
	public void refreshInfo() {

		if(currentVideo == null)
			return;

		mVideoTitle.setText(Html.fromHtml(currentVideo.getDescription()));
		//mVideoTitle.setText(currentVideo.getDescription());


		//String info = "";
		//info = (currentVideo.getViewcount() + currentVideo.getMyviewcount()) + " wakeup - ";
		//info += (currentVideo.getLike() + currentVideo.getMylike()) + " like(s)";
		int myLike = currentVideo.getMylike();

		mTxtwakeup.setText("" + currentVideo.getViewcount());
		mTxtlike.setText("" + (currentVideo.getLike()));

		switch (myLike) {
		case 1:
			mBtnLike.setImageResource(R.drawable.btnlike_1);

			break;
		case 2:
			mBtnLike.setImageResource(R.drawable.btnlike_2);
			break;
		case 3:
			mBtnLike.setImageResource(R.drawable.btnlike_3);
			break;

		default:
			mBtnLike.setImageResource(R.drawable.btnlike_0);
			break;
		}





	}




	@Override
	public void onPause() {
		super.onPause();
		_isActivityPaused =true;
		if(currentVideo !=null)
			currentVideo.save();

	}


	@Override
	public void onResume() {
		super.onResume();
		_isActivityPaused = false;
	}







	@Override
	public void onStop() {
		super.onStop();
		SnooziUtility.trace(TRACETYPE.INFO, "FragmentVideo.onStop : ");

		closeVideo();



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
					try {
						current  = mVideoView.getCurrentPosition();
						//System.out.println("duration - " + duration + " current- "
						//        + current);
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


	
	private class CommentSender extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) 
        {
        	// TODO : We need to call the Comment endpoint to add
        	
        	android.os.Debug.waitForDebugger();

        	
        	
        	
        	
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                    
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
           
        	android.os.Debug.waitForDebugger();
mBtnComm.setActivated(true);
			mEditComm.setActivated(true);
			mEditComm.setText("");
			mBtnComm.setText(getResources().getString(R.string.send));
			
			//TODO :  We need to refresh the comment list
			
        	//TextView txt = (TextView) findViewById(R.id.output);
            //txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
