package com.wake.wank.UI;



import java.util.ArrayList;
import java.util.List;


//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.wake.wank.R;
import com.wake.wank.models.MyAlarm;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FragmentClock extends Fragment {


	private static final int REQUEST_SETTING = 1;
	
	private ViewGroup rootView;

	private List<MyAlarm> alarmList ;
	//private CheckBox chkactivate;
	//private TextView txtday;
	//private TextView txtTime;
	private OnCheckedChangeListener activateListener = null;

	private MyAlarmAdapter mAdapter;
	private ListView listView;

	private Button addButton;

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_clock, container, false);

		listView = (ListView) rootView.findViewById(R.id.listviewalarm);
		alarmList = new ArrayList<MyAlarm>();
		mAdapter = new MyAlarmAdapter(this.getActivity(), alarmList);
		listView.setAdapter(mAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				  // We must launch the setup
					  try {
						  launchSettingActivity(alarmList.get(position));
						
					} catch (Exception e) {
						// nothing at that position, need to refresh the list
						mAdapter.notifyDataSetChanged();
					}
			    
			  }
			});
		
		addButton = (Button) rootView.findViewById(R.id.addButton);
		addButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				MyAlarm newalrm = new MyAlarm();
				launchSettingActivity(newalrm);
				return false;
			}
		});
		
/*
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
		*/
		

		// Build button list
		//txtTime = (TextView)rootView.findViewById(R.id.TxtTime);
		//txtday = (TextView)rootView.findViewById(R.id.Txtdays);
		//chkactivate = (CheckBox) rootView.findViewById(R.id.checkBoxActiv);



		/*Activate setup*/
		/*
		activateListener = new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SetAlarm(isChecked);
			}

		};*/


		// Recherchez AdView comme ressource et chargez une demande.
		/*		Bundle bundle = new Bundle();
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
		 */



		return rootView;
	}



	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//chkactivate.setOnCheckedChangeListener(null);    
		MyAlarm alrm = null;
		// recuperation depuis la base de données des alarmes configurée
		alarmList.clear();
		alarmList.addAll(MyAlarm.getFromSQL(rootView.getContext()));
		
		if(alarmList.isEmpty())
		{

			//Pas d'alarm, on set une alarme par default
			//LEGACY : Si il y a des anciennes alarmes en pref, on les recupere

			SharedPreferences settings = rootView.getContext().getSharedPreferences(SnooziUtility.PREFS_NAME, 0);

			alrm = new MyAlarm();
			alrm.setActivate(settings.getBoolean("activate", false));
			alrm.setDayString(settings.getString("dayString",""));
			alrm.setHour(settings.getInt("hour",7));
			alrm.setMinute(settings.getInt("minute",30));
			alrm.setMonday(settings.getBoolean("monday", false));
			alrm.setTuesday(settings.getBoolean("tuesday", false));
			alrm.setWednesday(settings.getBoolean("wednesday", false));
			alrm.setThursday(settings.getBoolean("thursday", false));
			alrm.setFriday(settings.getBoolean("friday", false));
			alrm.setSaturday(settings.getBoolean("saturday", false));
			alrm.setSunday(settings.getBoolean("sunday", false));
			alrm.setVibrate(settings.getBoolean("vibrate", true));
			alrm.setVolume(settings.getInt("volume", -1));

			// We delete old sharedPref
			Editor editor = settings.edit();
			editor.remove("activate");
			editor.remove("dayString");
			editor.remove("hour");
			editor.remove("minute");
			editor.remove("monday");
			editor.remove("tuesday");
			editor.remove("wednesday");
			editor.remove("thursday");
			editor.remove("friday");
			editor.remove("saturday");
			editor.remove("sunday");
			editor.remove("vibrate");
			editor.remove("volume");
			editor.commit();
			if(alrm.getVolume() != -1)
				alrm.save(rootView.getContext()); // alarm was activatged, so we save it
			alarmList.add(alrm);
		}


		mAdapter.notifyDataSetChanged();
		
		//txtTime.setText(currentAlarm.toTime());
		//txtday.setText(currentAlarm.getDayString());
		//chkactivate.setChecked(currentAlarm.getActivate());

		//chkactivate.setOnCheckedChangeListener(activateListener);    

	}


	private void launchSettingActivity(MyAlarm alrm)
	{
		Intent intent = new Intent(this.getActivity(), AlarmSettingActivity.class);
		intent.putExtra("alarm", alrm);
		startActivityForResult(intent, REQUEST_SETTING);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_SETTING)
		{
		if (resultCode == Activity.RESULT_OK) 
		{

			SnooziUtility.trace(getActivity(), TRACETYPE.INFO,".....onAlarmSettingResult RESULT OK");
			
			MyAlarm currentAlarm = (MyAlarm) data.getParcelableExtra("alarm");

			currentAlarm.save(rootView.getContext());
			currentAlarm.checkAndPlanify(getActivity(),rootView.getContext());
			
			//if(position == -1)
			//	alarmList.add(currentAlarm);
			//else
			//	alarmList.set(position, currentAlarm);
			
			//mAdapter.notifyDataSetChanged();
			
			//Boolean activ = currentAlarm.getActivate();

			//txtTime.setText(currentAlarm.toTime());
			//txtday.setText(currentAlarm.getDayString());

			//if(activ == chkactivate.isChecked())
			//	SetAlarm(activ);
			//else
			//	chkactivate.setChecked(activ);
		}
		}

	}



/*
	private void SetAlarm(Boolean isActivated)
	{

		//We save the current Alarm
		currentAlarm.save(rootView.getContext());




		//Build the event for the server
		TrackingSender sender = new TrackingSender(rootView.getContext(),getActivity().getApplication());
		StringBuilder evtDescr = new StringBuilder();
		evtDescr.append(" at ");
		evtDescr.append(currentAlarm.getHour());
		evtDescr.append(":");
		evtDescr.append(currentAlarm.getMinute());
		evtDescr.append(" on ");
		String dayString = "";
		ArrayList<String> theDayList = new ArrayList<String>();
		if(currentAlarm.getMonday())
			theDayList.add("monday");
		if(currentAlarm.getTuesday())
			theDayList.add("tuesday");
		if(currentAlarm.getWednesday())
			theDayList.add("wednesday");
		if(currentAlarm.getThursday())
			theDayList.add("thursday");
		if(currentAlarm.getFriday())
			theDayList.add("friday");
		if(currentAlarm.getSaturday())
			theDayList.add("saturday");
		if(currentAlarm.getSunday())
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
*/


}
