package com.wake.wank.models;

import java.io.IOException;

import org.json.JSONException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.wake.wank.GCMIntentService;
import com.wake.wank.MyApplication;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;
import com.wake.wank.utils.TrackingSender;


/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	// Global variables
	// Define a variable to contain a content resolver instance
	ContentResolver m_ContentResolver;

	public static boolean isRegistationPending = false;

	// An account type, in the form of a domain name
	private static final String ACCOUNT_TYPE = "com.wake.wank";
	// The account name
	private static final String ACCOUNT = "Wakeaccount";



	/**
	 * Set up the sync adapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		m_ContentResolver = context.getContentResolver();
	}


	/**
	 * Set up the sync adapter. This form of the
	 * constructor maintains compatibility with Android 3.0
	 * and later platform versions
	 */
	public SyncAdapter(
			Context context,
			boolean autoInitialize,
			boolean allowParallelSyncs) 
	{
		super(context, autoInitialize);
		/*
		 * If your app uses a content resolver, get an instance of it
		 * from the incoming Context
		 */
		m_ContentResolver = context.getContentResolver();

	}


	public static void requestSync(SnooziUtility.SYNC_ACTION syncaction) {
		requestSync( syncaction, null );
	}

	/**
	 * Send a request to the sync service
	 * @param action SnooziUtility.SYNC_ACTION
	 * @param theUser MyUser object or null
	 */
	public static void requestSync(SnooziUtility.SYNC_ACTION syncaction, Bundleable data) {

		try {
			//On demande de faire une tache async
			Bundle settingsBundle = null;
			if(data != null)
				settingsBundle = data.toBundle();
			else
				settingsBundle = new Bundle();
			settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			settingsBundle.putInt("action", syncaction.ordinal()); // We change the enum to it's int value to be Bundled

			//	settingsBundle.putBundle("data", data.toBundle());

			ContentResolver.requestSync(SyncAdapter.GetSyncAccount(), SnooziContract.AUTHORITY, settingsBundle);


		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR,"SyncAdapter.requestSync Exception " + e.toString());
		}
	}



	@Override
	public void onPerformSync(Account account, final Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) 
	{
		//android.os.Debug.waitForDebugger();
		
		//Getting the extras and looking for action extra to know what to do next
		int actionordinal=extras.getInt("action",SnooziUtility.SYNC_ACTION.TRACKING_SEND.ordinal());
		SnooziUtility.SYNC_ACTION action = SnooziUtility.SYNC_ACTION.values()[actionordinal];

		SnooziUtility.trace(TRACETYPE.INFO, "SyncAdapter.onPerformSync : "+action);

		SharedPreferences settings =this.getContext().getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);


		if( action == SnooziUtility.SYNC_ACTION.GCM_REGISTERED)
		{
			//Saving registration State
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("isRegistered", true);
			editor.commit();
			isRegistationPending = false;

			//	SnooziUtility.trace(context, TRACETYPE.ERROR,"isRegistered not commiting " );
			return;
		}else
		{
			boolean isRegistered = settings.getBoolean("isRegistered", false);
			if(!isRegistered && !isRegistationPending)
			{
				SnooziUtility.trace(TRACETYPE.INFO,"isRegistered : " + isRegistered);
				//Not registered yet so Registering Google cloud messaging
				try {
					GCMIntentService.register(this.getContext());
					//isRegistationPending = true;
				} catch (Exception e) 
				{
					//probleme during GCM registration
					SnooziUtility.trace(TRACETYPE.ERROR, "GCM Registration error :  " +  e.toString());
				}
			}
		}
		
		
		try {
			switch (action) {

			case TRACKING_SEND:
				// we send all data to the server if any
				TrackingSender.sendTrackingEvent();
				/*
				new Handler(Looper.getMainLooper()).post(new Runnable() {             
				    @Override
				    public void run() { 
				    	try {
							TrackingSender.sendTrackingEvent();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});*/
				break;
			case USER_UPDATE :
			case USER_WAKEUP :
			case USER_VIEW :
				//We Need to sync user infos
				MyUser.async(action,extras);
				break;
			case ALARM_UPDATE :
			case ALARM_DELETE :
				//We Need to sync alarm infos
				MyAlarm.async(action,extras);
				break;
			case VIDEO_RETRIEVE:
			case VIDEO_RATING :
			case VIDEO_CLEANUP:
				//We need to sync video infos
				MyVideo.async(action ,extras);
				//After each rating, we clean & refresh current videos
				//MyVideo.cleanupOldVideo(provider);
				//MyVideo.retrieveRecentVideo(provider);

				break;
			default:
				break;
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			android.os.Debug.waitForDebugger();
			
			SnooziUtility.trace(TRACETYPE.ERROR, "SyncAdapter IOException :  " +  e.toString());
			syncResult.stats.numIoExceptions++;
			
		} catch (final ParseException e) {
            syncResult.stats.numParseExceptions++;
            SnooziUtility.trace(TRACETYPE.ERROR, "SyncAdapter ParseException :  " +  e.toString());
	    } catch (final Exception e) {
            syncResult.stats.numParseExceptions++;
            SnooziUtility.trace(TRACETYPE.ERROR, "SyncAdapter Exception :  " +  e.toString());
	    }
		
	
		
		/*
		Handler h = new Handler(Looper.getMainLooper());
		h.post(new Runnable() {
			
			public void run() {
					
				new SyncOperation().execute(extras);
				SnooziUtility.trace(TRACETYPE.INFO,"SyncAdapter here3 " );

			}
		});
		*/
		
	}











	/**
	 * Get or Create a new dummy account for the sync adapter
	 */
	public static Account GetSyncAccount() {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		try {


			// Get an instance of the Android account manager
			AccountManager accountManager = (AccountManager) MyApplication.getAppContext().getSystemService(android.content.Context.ACCOUNT_SERVICE);
			/*
			 * Add the account and account type, no password or user data
			 * If successful, return the Account object, otherwise report an error.
			 */
			if (accountManager.addAccountExplicitly(newAccount, null, null)) {
				/*
				 * If you don't set android:syncable="true" in
				 * in your <provider> element in the manifest,
				 * then call context.setIsSyncable(account, AUTHORITY, 1)
				 * here.
				 */

			} else {
				Account[] accountlist = accountManager.getAccountsByType(ACCOUNT_TYPE);
				for (int i = 0; i < accountlist.length; i++) {
					Account account = accountlist[i];
					if(account.name.equals(ACCOUNT))
						newAccount = account;
				}
				/*
				 * The account exists or some other error occurred. Log this, report it,
				 * or handle it internally.
				 */

			}
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR,"SyncAdapter.GetSyncAccount Exception " + e.toString());

		}
		return newAccount;
	}
	
	/*
	private class SyncOperation extends AsyncTask<Bundle, Void, String> {

        @Override
        protected String doInBackground(Bundle... extrasList) {
        	try {
        		
        		Bundle extra = extrasList[0];
        		
        		int actionordinal=extra.getInt("action",SnooziUtility.SYNC_ACTION.TRACKING_SEND.ordinal());
        		SnooziUtility.SYNC_ACTION action = SnooziUtility.SYNC_ACTION.values()[actionordinal];

        		SnooziUtility.trace(TRACETYPE.INFO,"SyncAdapter here  " );
				switch (action) {

				case TRACKING_SEND:
					// we send all data to the server if any
					TrackingSender.sendTrackingEvent();
					break;
				case USER_UPDATE :
					//We Need to sync user infos
					MyUser.async(action,extra);
					break;
				case ALARM_UPDATE :
				case ALARM_DELETE :
					//We Need to sync alarm infos
					MyAlarm.async(action,extra);
					break;
				case VIDEO_RETRIEVE:
				case VIDEO_RATING :
				case VIDEO_CLEANUP:
					//We need to sync video infos
					MyVideo.async(action ,extra);

					//After each rating, we clean & refresh current videos
					//MyVideo.cleanupOldVideo(provider);
					//MyVideo.retrieveRecentVideo(provider);

					break;
				default:
					break;
				}


				SnooziUtility.trace(TRACETYPE.INFO,"SyncAdapter here 2  " );
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR, "Sync Action error :  " +  e.toString());

			}
            return "Executed";
        }
       

		@Override
        protected void onPostExecute(String result) {
           
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }*/
}
