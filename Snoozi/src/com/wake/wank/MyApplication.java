package com.wake.wank;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.wake.wank.models.MyAlarm;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	
	private static Context context = null;

	public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public MyApplication() {
        super();
        
    }

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    
    
    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) 
        {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.myanalytics) : null;
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public static Context getAppContext() {
    	return MyApplication.context;
    }
	public static void setAppContext(Context context) {
		if(MyApplication.context == null)
			MyApplication.context = context;
	}
}
