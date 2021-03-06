package com.wake.wank.UI;

import com.wake.wank.*;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class SurveyActivity extends Activity {

	private TrackingSender sender;
	private EditText _commenttxt;
	private RatingBar _ratingBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_survey);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

		 _commenttxt = (EditText) findViewById(R.id.editTextComment);
		 _ratingBar = (RatingBar) findViewById(R.id.ratingExperience);
		
		Button btnSend = (Button) findViewById(R.id.btnSendComment);
		btnSend.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				String comm = _commenttxt.getText().toString();
				sender = new TrackingSender(getApplication());
				sender.sendUserEvent(TrackingEventCategory.APP,TrackingEventAction.COMMENT, comm);
				
				String nextString = getResources().getString(R.string.sendthanks);
				Toast.makeText(getApplicationContext(),nextString , Toast.LENGTH_LONG).show();
			
				SharedPreferences prefs = getApplicationContext().getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("isSurvey", true);
				editor.commit();
				finish();
				return false;
			}
		});
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		int rating = _ratingBar.getProgress();
		if(rating > 0)
		{	
			sender = new TrackingSender(getApplication());
			sender.sendUserEvent(TrackingEventCategory.APP,TrackingEventAction.RATING, rating + "");
		}
		finish();
	}
	
	
	
	
	
}
