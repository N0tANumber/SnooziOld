package com.snoozi.snoozi.UI;


import com.snoozi.snoozi.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class FragmentGallery extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_gallery, container, false);
		
		Button testbtn = (Button) rootView.findViewById(R.id.btnRecord);
        testbtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					// TODO Auto-generated method stub
					//((HomeActivity)getActivity()).showDetailView();
					launchRecordActivity();
				}
				return false;
			}
		});
		return rootView;
	}
	
	private void launchRecordActivity()
	{
		if(checkCameraHardware(this.getActivity()))
		{
			
		 Intent intent = new Intent(this.getActivity(), RecordActivitySnap.class);
		startActivity(intent);
		}
		
	}
	
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
}
