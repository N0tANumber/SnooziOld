package com.wake.wank.UI;


import com.google.analytics.tracking.android.EasyTracker;
import com.wake.wank.R;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class HomeActivity extends FragmentActivity {
	
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
       // requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_home);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);
        
		
	    
	    
	    
	    
        
        
	    
	    
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
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try {
			EasyTracker.getInstance().activityStop(this);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SnooziUtility.trace(this, TRACETYPE.INFO,"HomeActivity.onActivityResult");
		if (resultCode == RESULT_OK) 
		{
			SnooziUtility.trace(this, TRACETYPE.INFO,".....onActivityResult RESULT OK");
			
		 }
	}
    
}
