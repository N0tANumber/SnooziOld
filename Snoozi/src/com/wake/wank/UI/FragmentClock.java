package com.wake.wank.UI;



import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.wake.wank.R;
import com.wake.wank.models.AlarmPlanifier;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FragmentClock extends Fragment {

	
	private CheckBox chkactivate;
	private OnCheckedChangeListener activateListener = null;
	private ViewGroup rootView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		 rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_clock, container, false);
		
		 LinearLayout setAlarmBtn = (LinearLayout) rootView.findViewById(R.id.BtnSetAlarm);
		 setAlarmBtn.setOnTouchListener(new OnTouchListener() {
			
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
        
        
     // Recherchez AdView comme ressource et chargez une demande.
     		Bundle bundle = new Bundle();
     		bundle.putString("color_bg", "641213");
     		bundle.putString("color_bg_top", "641213");
     		bundle.putString("color_border", "641213");
     		bundle.putString("color_link", "FFFFFF");
     		bundle.putString("color_text", "FFFFFF");
     		bundle.putString("color_url", "cc9933");

     		AdMobExtras extras = new AdMobExtras(bundle);
     		AdView adView = (AdView)rootView.findViewById(R.id.adView);
     	    AdRequest adRequest = new AdRequest.Builder()
     			    .addNetworkExtras(extras)
     			    .tagForChildDirectedTreatment(false)
     	    		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Émulateur
     	    	    .addTestDevice("88D53265ED709666EA324C27AAE13FC1")
     	    	    .addTestDevice("BFCEAEB7FADA53A2A79A6F7C4DD211AB")
     	    	    .build();
     	    adView.loadAd(adRequest);
     	    
     	    
     	   SharedPreferences settings = rootView.getContext().getSharedPreferences(SnooziUtility.PREFS_NAME, 0);
     	   chkactivate = (CheckBox) rootView.findViewById(R.id.checkBoxActiv);
   		//Update Button state from Pref
   		boolean isActivated = settings.getBoolean("activate", false);
   		if(isActivated)
   		{
   			//alarm is set, so we disable all other controls
   			chkactivate.setChecked(isActivated);
   		}
   		
		String theDayString = settings.getString("dayString",getResources().getString(R.string.Everyday));
		int thehour = settings.getInt("hour",7);
		int themin = settings.getInt("minute",30);

		buildTime(thehour, themin);
		TextView txtday = (TextView)rootView.findViewById(R.id.Txtdays);
		txtday.setText(theDayString);

		/*Activate setup*/
		activateListener = new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				SetAlarm(isChecked);


			}

		};
		
   		chkactivate.setOnCheckedChangeListener(activateListener);    
        
		return rootView;
	}

	
	private void launchSettingActivity()
	{
		 Intent intent = new Intent(this.getActivity(), AlarmSettingActivity.class);
		startActivityForResult(intent, 1);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) 
		{
			SnooziUtility.trace(getActivity(), TRACETYPE.INFO,".....onAlarmSettingResult RESULT OK");
			Boolean activ = data.getBooleanExtra("activated", false);
			
			int thehour = data.getIntExtra("hour",7);
			int themin = data.getIntExtra("minute",30);
			String theDayString = data.getStringExtra("dayString");
			
			
			buildTime(thehour, themin);
			TextView txtday = (TextView)rootView.findViewById(R.id.Txtdays);
			txtday.setText(theDayString);
			
			if(activ == chkactivate.isChecked())
				SetAlarm(activ);
			else
				chkactivate.setChecked(activ);
		}
			  
		
	}


	/**
	 * @param thehour
	 * @param themin
	 */
	public void buildTime(int thehour, int themin) {
		String theTime = "";
		if(thehour < 10)
			theTime = "0";
		theTime += thehour + ":";
		if(themin < 10)
			theTime += "0";
		theTime += themin;
		
		TextView txtTime = (TextView)rootView.findViewById(R.id.TxtTime);
		txtTime.setText(theTime);
	}


	
	private void SetAlarm(Boolean isActivated)
	{

		SharedPreferences prefs = getActivity().getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("activate", isActivated);
		editor.apply();
		editor.commit();
		int thehour = prefs.getInt("hour",7);
		int themin = prefs.getInt("minute",30);
		
		//Build the event for the server
		TrackingSender sender = new TrackingSender(rootView.getContext(),getActivity().getApplication());
		StringBuilder evtDescr = new StringBuilder();
		evtDescr.append(" at ");
		evtDescr.append(thehour);
		evtDescr.append(":");
		evtDescr.append(themin);
		evtDescr.append(" on ");
		String dayString = "";
		ArrayList<String> theDayList = new ArrayList<String>();
		if(prefs.getBoolean("monday",false))
			theDayList.add("monday");
		if(prefs.getBoolean("tuesday",false))
			theDayList.add("tuesday");
		if(prefs.getBoolean("wednesday",false))
			theDayList.add("wednesday");
		if(prefs.getBoolean("thursday",false))
			theDayList.add("thursday");
		if(prefs.getBoolean("friday",false))
			theDayList.add("friday");
		if(prefs.getBoolean("saturday",false))
			theDayList.add("saturday");
		if(prefs.getBoolean("sunday",false))
			theDayList.add("sunday");
		
		if(theDayList.size() == 0 || theDayList.size() == 7)
			dayString = "EveryDay";
		else
			dayString = theDayList.toString();
		evtDescr.append(dayString);

		//Dispatch event to the server
		if(isActivated)
		{
			AlarmPlanifier.checkAndPlanifyNextAlarm(getActivity());
			sender.sendUserEvent(TrackingEventCategory.ALARM,TrackingEventAction.SET,"set" + evtDescr.toString());
			
			//We display a toast message
			String nextString = AlarmPlanifier.getNextAlarmAsString(rootView.getContext());
			if(nextString.length() > 0)
			{
				nextString = getResources().getString(R.string.alarnIsSet) + " " + nextString;
				Toast.makeText(rootView.getContext(),nextString , Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			sender.sendUserEvent(TrackingEventCategory.ALARM,TrackingEventAction.UNSET,"unset" + evtDescr.toString());
			AlarmPlanifier.CancelAlarm(getActivity());
		}
	}
	
	
	
}
