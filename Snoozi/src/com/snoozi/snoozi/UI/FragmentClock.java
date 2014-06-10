package com.snoozi.snoozi.UI;


import com.snoozi.snoozi.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class FragmentClock extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_clock, container, false);
		
		Button testbtn = (Button) rootView.findViewById(R.id.btnsetAlarm);
        testbtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					// TODO Auto-generated method stub
					launchSettingActivity();
					
				}
				return false;
			}
		});
        
		return rootView;
	}

	
	private void launchSettingActivity()
	{
		 Intent intent = new Intent(this.getActivity(), AlarmSettingActivity.class);
		startActivity(intent);
		
	}
	
	
}
