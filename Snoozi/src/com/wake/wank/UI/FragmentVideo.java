package com.wake.wank.UI;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wake.wank.MyApplication;
import com.wake.wank.R;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.MyAlarm;
import com.wake.wank.models.MyComments;
import com.wake.wank.models.MyCommentsAdapter;
import com.wake.wank.models.MyVideo;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
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
	private ProgessUpdater _progresstask = null;
	private CommentSender _commsendtask = null;
	private CommentRetriever _commretrievetask = null;
	//private AudioManager audioManager;
	//private int _musicVol;
	//private int _oldmusicVol;
	public int _videoViewCount = 0;

	private boolean _isActivityPaused = false;

	private ScrollView mScrollViewVideo;
	private TextView mVideoTitle;
	private ViewGroup rootView;
	private TextView mTxtwakeup;
	private TextView mTxtlike;
	private ImageView mBtnLike;
	private EditText mEditComm;
	private Button mBtnComm;


	//for comments
	private List<MyComments> commentList ;
	private MyCommentsAdapter mAdapter;
	private ListView commentlistView;




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_video, container, false);



		mScrollViewVideo = (ScrollView)rootView.findViewById(R.id.scrollViewVideo);
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


		commentlistView = (ListView) rootView.findViewById(R.id.listviewcomment);
		commentList = new ArrayList<MyComments>();
		mAdapter = new MyCommentsAdapter(this.getActivity(), commentList);
		commentlistView.setAdapter(mAdapter);





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
			@Override
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
			@Override
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
					try {
						if(_commsendtask != null && _commsendtask.getStatus() == Status.RUNNING)
						{
							_commsendtask.cancel(true);
						}


						_commsendtask = new CommentSender(currentVideo);

						String comment= mEditComm.getText().toString();
						comment = comment.trim();
						if(comment.length() > 0)
						{
							// We need to send the message

							mBtnComm.setEnabled(false);
							mBtnComm.setText(getResources().getString(R.string.sending));
							mEditComm.setEnabled(false);


							if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
								_commsendtask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,comment);
							else
								_commsendtask.execute(comment);



						}
					} catch (Exception e) {
						SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.mBtnComm.setOnTouchListener :  " + e.toString());

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
				refreshComment(true);
			}

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.openVideo Error : "+ e.toString());
			stopPlaying();

			return false;

		}
		return true;

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

			if(_commsendtask != null)
			{
				if(_commsendtask.getStatus() == Status.RUNNING)
					_commsendtask.cancel(true);
				_commsendtask = null;
			}
			if(_commretrievetask != null)
			{
				if(_commretrievetask.getStatus() == Status.RUNNING)
					_commretrievetask.cancel(true);
				_commretrievetask = null;
			}


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


	public void refreshComment(boolean isgetFromServer)
	{
		SnooziUtility.trace(TRACETYPE.INFO, "FragmentVideo.refreshComment");

		commentList.clear();
		if(currentVideo != null)
		{

			String whereClause = SnooziContract.comments.Columns.VIDEOID + " =  " + currentVideo.getVideoid();
			String[] whereValue = null;
			commentList.addAll(MyComments.getListFromSQL(whereClause,whereValue));
		}

		if(mAdapter != null)
		{
			mAdapter.notifyDataSetChanged();
			setListViewHeightBasedOnChildren(commentlistView);

		}

		if(isgetFromServer)
		{
			// We need to refresh the comments from the server
			if(_commretrievetask != null && _commretrievetask.getStatus() == Status.RUNNING)
			{
				_commretrievetask.cancel(true);
			}
			_commretrievetask = new CommentRetriever(currentVideo);
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
				_commretrievetask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				_commretrievetask.execute();


		}
	}

	@SuppressWarnings("unchecked")
	public void setListViewHeightBasedOnChildren(ListView listView) {
		try {

			ArrayAdapter<MyComments> listAdapter = (ArrayAdapter<MyComments>) listView.getAdapter(); 
			if (listAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				listItem.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				//listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
			listView.requestLayout();
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "FragmentVideo.setListViewHeightBasedOnChildren error :" + e.toString());

		}
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		_videoViewCount = 0;
		if(currentVideo != null)
			openVideo(currentVideo);

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
			_progresstask = new ProgessUpdater();

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

	private class ProgessUpdater extends AsyncTask<Void, Integer, Void>
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
							int currentPrecent = current * 100 / duration;
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

	private class CommentRetriever extends AsyncTask<Void, Void, String> {

		private MyVideo video;

		public CommentRetriever(MyVideo theVideo) {
			super();
			video = theVideo;
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			try {
				//  We need to refresh the comment list from the server too
				video.getServerComments();video.getServerComments();

			} catch (IOException e) {
				SnooziUtility.trace(TRACETYPE.INFO, "CommentRetriever interrupted : "+e.toString());
				return "IO";
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.INFO, "CommentRetriever interrupted : "+e.toString());
				return e.toString();
			}

			return "OK";
		}

		@Override
		protected void onPostExecute(String result) {
			if(result.equals("OK"))
				refreshComment(false);
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}




	
	
	private class CommentSender extends AsyncTask<String, Void, String> {

		private MyVideo video;

		public CommentSender(MyVideo theVideo) {
			super();
			video = theVideo;
		}

		@Override
		protected String doInBackground(String... params) 
		{

			try {
				if(params.length > 0)
					video.sendComment(params[0]);
				else
					throw new Exception("no comments to add");


			} catch (IOException e) {
				SnooziUtility.trace(TRACETYPE.INFO, "CommentSender interrupted : "+e.toString());
				return "IO";
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.INFO, "CommentSender interrupted : "+e.toString());
				return e.toString();
			}

			return "OK";
		}

		@Override
		protected void onPostExecute(String result) {
			mBtnComm.setEnabled(true);
			mEditComm.setEnabled(true);
			mBtnComm.setText(getResources().getString(R.string.send));

			if(result.equals("OK"))
			{
				mEditComm.setText("");
				refreshComment(true);
				//scroll to last comment
				mScrollViewVideo.fullScroll(View.FOCUS_DOWN);
				
			}else if(result.equals("IO"))
			{
				// display a toast message explaining no network
				Toast.makeText(MyApplication.getAppContext(),getResources().getString(R.string.checknetwork) , Toast.LENGTH_LONG).show();
			}else 

			{
				// display a toast message explaining the result
				Toast.makeText(MyApplication.getAppContext(),result, Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
}
