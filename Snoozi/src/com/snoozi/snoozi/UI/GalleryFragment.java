package com.snoozi.snoozi.UI;


import com.snoozi.snoozi.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class GalleryFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_gallery, container, false);
		
		Button testbtn = (Button) rootView.findViewById(R.id.btntestVideo);
        testbtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					// TODO Auto-generated method stub
					((HomeActivity)getActivity()).showVideo();
					
				}
				return false;
			}
		});
		return rootView;
	}

}
