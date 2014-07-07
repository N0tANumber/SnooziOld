package com.wake.wank.UI;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.snoozi.deviceinfoendpoint.Deviceinfoendpoint;
import com.wake.wank.*;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.MyAlarm;
import com.wake.wank.models.MyUser;
import com.wake.wank.models.MyVideo;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * The Main Activity.
 * 
 * This activity starts up the RegisterActivity immediately, which communicates
 * with your App Engine backend using Cloud Endpoints. It also receives push
 * notifications from backend via Google Cloud Messaging (GCM).
 * 
 * Check out RegisterActivity.java for more details.
 */
public class MainActivity extends Activity {



	





	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(Deviceinfoendpoint.DEFAULT_ROOT_URL == "https://evident-quasar-565.appspot.com/_ah/api/")
		{
			SnooziUtility.trace(TRACETYPE.ERROR, "You need to change all endpoint DEFAULT_ROOT_URL to match your current api version : ex : https://evident... ==> https://2-dot-evident");
			finish();
			return;
		}
		
		//TEST : Start up RegisterActivity right away
		//Intent intent = new Intent(this, RegisterActivity.class);
		//startActivity(intent);
		
		
		final Timer timer = new Timer(true);
		TimerTask timerTask = new TimerTask() 
		{
			@Override
			public void run() 
			{   
				//after 100ms, we do init work
				launchHomeActivity();
				//launchSettingActivity();
			}
		};

		timer.schedule(timerTask, 100);
		
		
	}

	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try {
			if(!SnooziUtility.DEV_MODE)
				EasyTracker.getInstance().activityStop(this);
			
		} catch (Exception e) {
			// TODO: handle exception
		} 
	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// FirstLaunch tracking
		SharedPreferences settings = getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		boolean isFirstLaunch = settings.getBoolean("firstLaunch", true);
		
		TrackingSender sender = new TrackingSender(getApplicationContext(),getApplication());
		if(isFirstLaunch)
		{
			SnooziUtility.trace(TRACETYPE.INFO,"First APP_LAUNCH ");
			
			sender.sendUserEvent(TrackingEventCategory.APP,TrackingEventAction.FIRSTLAUNCH);
			//Save firstlaunch state
			editor.putBoolean("firstLaunch", false);
			
			//We insert the 3 dummy video into BDD
			createDummyVideo();
			
			//During that time We ask for next video to be downloaded
			Bundle settingsBundle = new Bundle();
			settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			//settingsBundle.putBoolean( ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			settingsBundle.putString("action", "NEW_VIDEO_AVAILABLE");
			ContentResolver.requestSync(SyncAdapter.GetSyncAccount(this), SnooziContract.AUTHORITY, settingsBundle);
		}
		else
			sender.sendUserEvent(TrackingEventCategory.APP,TrackingEventAction.LAUNCH);

		//User Profil information
		int userid = settings.getInt("userid", 0);
		if(userid == 0)
		{
			// We need to save userinfo and get the userid from the server
			userid = createMyInfo();
			//editor.putInt("userid", userid);
			
		}
		
		
		editor.commit();
		try {
			
			if(!SnooziUtility.DEV_MODE)
				EasyTracker.getInstance().activityStart(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
		//We copy the local BDD / Only on DEV MODE
		exportLocalDatabase();
	}
	
	
	



	private void launchHomeActivity()
	{
		 Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		//for now finish main activity after launching the AlarmSettingActivity
		finish();
	}
	
	
	private void createDummyVideo()
	{
		Context context = this;
		
		MyVideo videoObj = new MyVideo();
		videoObj.setStatus("OK");
		videoObj.setFilestatus("DUMMY");
		videoObj.setId(0);
		int videoNumber = 0;
		String path = "";

		//FIRST VIDEO :Still Dreaming?
		videoNumber = 0;
		path =  "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video" + videoNumber, "raw", context.getPackageName());
		videoObj.setLocalurl(path);
		videoObj.setUrl("Chicks.mp4");
		videoObj.setVideoid(6421335542595584l); // video id on the Cloud Storage for tracking like/view and others...
		videoObj.setDescription("Still Dreaming?");
		videoObj.setTimestamp(1401291149156l);
		videoObj.save(this);
		videoObj.setId(0);

		//SECOND VIDEO : Party at my house last night!
		videoNumber = 1;
		path =  "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video" + videoNumber, "raw", context.getPackageName());
		videoObj.setLocalurl(path);
		videoObj.setUrl("GirlsGoneWild.m4v");
		videoObj.setVideoid(6750768661004288l); // video id on the Cloud Storage for tracking like/view and others...
		videoObj.setDescription("Party at my house last night!");
		videoObj.setTimestamp(1401291282525l);
		videoObj.save(this);
		videoObj.setId(0);

		//THIRD VIDEO : Elephant piano
		/*videoNumber = 2;
		path =  "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video" + videoNumber, "raw", context.getPackageName());
		videoObj.setLocalurl(path);
		videoObj.setUrl("ElephantPiano.m4v");
		videoObj.setVideoid(5207439808921600l); // video id on the Cloud Storage for tracking like/view and others...
		videoObj.setDescription("Have fun with your morning video!");
		videoObj.save(this);
*/
	}
	
	private int createMyInfo() {
		MyUser me = new MyUser();
		me.setPseudo("WankUser");
		me.saveAndSync();
		
		//TODO : get country and city in an async task
		
		return me.getId();
	}

	
	/**
	 * For accessing data in the database without rooting the device, we copy the database on the sd card ( no access restriction)
	 * used only in dev mode
	 */
	private void exportLocalDatabase() {
		if(SnooziUtility.DEV_MODE)
		{
			
			try {
				
				File file = getDatabasePath("WankDB.db");
				InputStream in = new FileInputStream(file);
		        OutputStream out = null;
		        
		        File outFile = new File("/mnt/sdcard/Android/backupWank.db");
				out = new FileOutputStream(outFile);
		       
		        byte[] buffer = new byte[1024];
		        int read;
		        while((read = in.read(buffer)) != -1){
		          out.write(buffer, 0, read);
		        }
		        in.close();
		        in = null;
		        out.flush();
		        out.close();
		        
		        
		        out = null;
		        
				SnooziUtility.trace(TRACETYPE.DEBUG, "Database exported : " + outFile.getAbsolutePath());
			} catch (Exception e) {
				// TODO: handle exception
				SnooziUtility.trace(TRACETYPE.DEBUG, "Database export error : "+ e.toString());
			}
		}
	}
	
}


