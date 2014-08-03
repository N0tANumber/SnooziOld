package com.wake.wank.UI;


import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.ads.*;
//import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.wake.wank.*;
import com.wake.wank.models.MyVideo;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class VideoActivity extends FragmentActivity {



	private FragmentVideo videofragment;
	private MyVideo currentVideo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_video);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);



		Intent data = getIntent();
		Bundle videoBundleled =  data.getBundleExtra("video");
		currentVideo = new MyVideo(videoBundleled);

		videofragment = (FragmentVideo) getSupportFragmentManager().findFragmentById(R.id.fragmentVideo);
		videofragment.openVideo(currentVideo);
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		try {
			if(!SnooziUtility.DEV_MODE)
				GoogleAnalytics.getInstance(this).reportActivityStart(this);
			//EasyTracker.getInstance().activityStart(this);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}



	@Override
	public void onStop() {
		super.onStop();


		videofragment.closeVideo();
		try{
			//prepare next video
			SnooziUtility.unsetVideo( );
			currentVideo = null;

			//We call a cleanup for old Video
			MyVideo.cleanupOldVideo(); 


			if(!SnooziUtility.DEV_MODE)
				GoogleAnalytics.getInstance(this).reportActivityStop(this);
			//EasyTracker.getInstance().activityStop(this);


			Intent returnIntent = new Intent();
			setResult(Activity.RESULT_OK, returnIntent);  

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "VideoActivity.onStop Error : "+ e.toString());

		}


		finish();
	}


}
