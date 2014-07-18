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
import com.wake.wank.models.MyUser;
import com.wake.wank.models.MyVideo;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.TrackingEventAction;
import com.wake.wank.utils.TrackingEventCategory;
import com.wake.wank.utils.TrackingSender;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.os.Bundle;
import android.os.Environment;
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
				// FirstLaunch tracking
				SharedPreferences settings = getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				
				boolean isFirstLaunch = settings.getBoolean("firstLaunch", true);
				
				TrackingSender sender = new TrackingSender(getApplication());
				if(isFirstLaunch)
				{
					SnooziUtility.trace(TRACETYPE.INFO,"First APP_LAUNCH ");
					
					sender.sendUserEvent(TrackingEventCategory.APP,TrackingEventAction.FIRSTLAUNCH);
					//Save firstlaunch state
					editor.putBoolean("firstLaunch", false);
					
					//We insert the 3 dummy video into BDD
					createDummyVideo();
					
					//During that time We ask for next video to be downloaded
					SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.VIDEO_RETRIEVE);
				}
				else
					sender.sendUserEvent(TrackingEventCategory.APP,TrackingEventAction.LAUNCH);

				
				
				//User Profil information
				int userid = settings.getInt("localuid", 0);
				MyUser me = null;
				if(userid != 0)
					 me = MyUser.getFromSQL(userid);
				if(me == null)
				{
					// We need to save userinfo and get the userid from the server
					me = new MyUser();
					me.setPseudo("Wankster");
					me.save();
					userid = me.getId();
					editor.putInt("localuid", userid);
				}
				
				// If we still don't have my serverside userId, i ask for a sync with the server
				if(MyUser.getMyUserId() == 0l)
					SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.USER_UPDATE,me); 
					
				
				editor.commit();
				
				//We call a cleanup for old Video
				MyVideo.cleanupOldVideo();
				
				
				
				//We copy the local BDD / Only on DEV MODE
				if(SnooziUtility.DEV_MODE)
				{
					exportLocalDatabase();
				}
				
				
				launchHomeActivity();
			}
		};

		timer.schedule(timerTask, 300);
		
		
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
		
		try {
			
			if(!SnooziUtility.DEV_MODE)
				EasyTracker.getInstance().activityStart(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
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
		videoObj.save();
		videoObj.setId(0);

		//SECOND VIDEO : Party at my house last night!
		videoNumber = 1;
		path =  "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video" + videoNumber, "raw", context.getPackageName());
		videoObj.setLocalurl(path);
		videoObj.setUrl("GirlsGoneWild.m4v");
		videoObj.setVideoid(6750768661004288l); // video id on the Cloud Storage for tracking like/view and others...
		videoObj.setDescription("Party at my house last night!");
		videoObj.setTimestamp(1401291282525l);
		videoObj.save();
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
	

	
	/**
	 * For accessing data in the database without rooting the device, we copy the database on the sd card ( no access restriction)
	 * used only in dev mode
	 */
	private void exportLocalDatabase() {
		
			
			try {
				
				File file = getDatabasePath("WankDB.db");
				InputStream in = new FileInputStream(file);
		        OutputStream out = null;
		        
		        File outFile = new File(Environment.getExternalStorageDirectory().getPath() + "/backupWank.db");
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
		        
		        // We do the same for userpref
		         in = new FileInputStream("/data/data/com.wake.wank/shared_prefs/com.wake.wank.xml");
		         out = null;
		        
		        outFile = new File(Environment.getExternalStorageDirectory().getPath() + "/com.wake.wank.sharedPref.xml");
				out = new FileOutputStream(outFile);
		       
		        buffer = new byte[1024];
		        while((read = in.read(buffer)) != -1){
		          out.write(buffer, 0, read);
		        }
		        in.close();
		        in = null;
		        out.flush();
		        out.close();
		    	
		    	
		        
				SnooziUtility.trace(TRACETYPE.INFO, "Database exported : " + outFile.getAbsolutePath());
			} catch (Exception e) {
				// TODO: handle exception
				SnooziUtility.trace(TRACETYPE.ERROR, "Database export error : "+ e.toString());
			}
		
	}
	
}


