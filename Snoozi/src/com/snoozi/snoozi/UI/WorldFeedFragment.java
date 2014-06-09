package com.snoozi.snoozi.UI;


import com.snoozi.snoozi.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WorldFeedFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_worldfeed, container, false);
		
		return rootView;
	}

}
