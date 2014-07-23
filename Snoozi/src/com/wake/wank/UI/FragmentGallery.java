package com.wake.wank.UI;


import java.util.ArrayList;
import java.util.List;

import com.wake.wank.R;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.MyVideo;
import com.wake.wank.models.MyVideoAdapter;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentGallery extends Fragment {

	private ViewGroup rootView;


	private List<MyVideo> videoList ;
	private MyVideoAdapter mAdapter;
	private ListView listView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_gallery, container, false);


		listView = (ListView) rootView.findViewById(R.id.listviewvideo);
		videoList = new ArrayList<MyVideo>();
		mAdapter = new MyVideoAdapter(this.getActivity(), videoList);
		listView.setAdapter(mAdapter);


		// Setting this scroll listener is required to ensure that during ListView scrolling,
		// we don't look for swipes.
		//		listView.setOnScrollListener(touchListener.makeScrollListener());

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// We must launch the setup
				try {
					launchVideoActivity(videoList.get(position));

				} catch (Exception e) {
					refreshGalleryList();
				}

			}
		});





		return rootView;
	}



	@Override
	public void onStart() {
		super.onStart();
		SnooziUtility.trace(TRACETYPE.INFO, "FragmentGallery.onStart");
		//chkactivate.setOnCheckedChangeListener(null);    
		//MyVideo video= null;
		// recuperation depuis la base de données des alarmes configurée
		

		//List<MyAlarm> alrmList = MyAlarm.getListFromSQL(SnooziContract.alarms.Columns.ACTIVATE + " = ?", new String[]{"1"});


		
		//txtLike.setText(currentAlarm.toTime());
		//txtday.setText(currentAlarm.getDayString());
		//chkactivate.setChecked(currentAlarm.getActivate());

		//chkactivate.setOnCheckedChangeListener(activateListener);    

	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SnooziUtility.trace(TRACETYPE.INFO, "FragmentGallery.onResume");
		//refreshGalleryList();

	}






	/**
	 * 
	 */
	public void refreshGalleryList() {
		if(videoList != null)
		{
			SnooziUtility.trace(TRACETYPE.INFO, "FragmentGallery.refreshGalleryList");
			
			videoList.clear();
			String whereClause = SnooziContract.videos.Columns.MYVIEWCOUNT + " > 0 AND " + SnooziContract.videos.Columns.FILESTATUS + " LIKE ?";
			String[] whereValue = new String[]{"SUCCESSFUL"};
			
			videoList.addAll(MyVideo.getListFromSQL(whereClause,whereValue));

		}
		if(mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}



	





	protected void launchVideoActivity(MyVideo myVideo) {

		((HomeActivity)getActivity()).showVideoView(myVideo);



	}











	private void launchRecordActivity()
	{

		if(checkCameraHardware(this.getActivity()))
		{

			Intent intent = new Intent(this.getActivity(), RecordActivity.class);
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
