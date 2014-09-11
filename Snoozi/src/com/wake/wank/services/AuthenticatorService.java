package com.wake.wank.services;

import com.wake.wank.models.Authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class AuthenticatorService extends Service {

	// Instance field that stores the authenticator object
    private Authenticator mAuthenticator;
	@Override
	public void onCreate(){
		// Create a new authenticator object
        mAuthenticator = new Authenticator(this);
	}
	
	/*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mAuthenticator.getIBinder();
	}

}
