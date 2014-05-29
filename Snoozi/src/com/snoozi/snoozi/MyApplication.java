package com.snoozi.snoozi;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

public class MyApplication extends Application {
	
    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public MyApplication() {
        super();
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
}
