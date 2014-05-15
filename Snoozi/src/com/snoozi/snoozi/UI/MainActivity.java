package com.snoozi.snoozi.UI;


import com.snoozi.snoozi.*;

import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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
	// The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.snoozi.provider";
    // An account type, in the form of a domain name
    private static final String ACCOUNT_TYPE = "com.snoozi";
    // The account name
    private static final String ACCOUNT = "default_account";
   




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
        
		
		// Start up RegisterActivity right away
		//Intent intent = new Intent(this, RegisterActivity.class);
		//startActivity(intent);
		
		try {
			//sleep for the splash screen
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Simulate a long loading process on app startup.
        Intent intent = new Intent(this, AlarmSettingActivity.class);
		startActivity(intent);
		// Since this is just a wrapper to start the main activity,
		// finish it after launching RegisterActivity
		finish();
	}
	
	
	/**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
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
        return newAccount;
    }
}


