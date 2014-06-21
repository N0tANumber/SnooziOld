package com.wake.wank.models;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Authenticator permit the registration of the user Account
 * @author CtrlX
 *
 */
public class Authenticator extends AbstractAccountAuthenticator {

	// Simple constructor
    public Authenticator(Context context) {
		super(context);
		
	}

	// Don't add additional accounts
    @Override
	public Bundle addAccount(AccountAuthenticatorResponse arg0, String arg1,
			String arg2, String[] arg3, Bundle arg4)
			throws NetworkErrorException {
		return null;
	}

	// Ignore attempts to confirm credentials
    @Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, Bundle arg2) throws NetworkErrorException {
		return null;
	}

 // Editing properties is not supported
    @Override
	public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	// Getting an authentication token is not supported
    @Override
	public Bundle getAuthToken(AccountAuthenticatorResponse arg0, Account arg1,
			String arg2, Bundle arg3) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}

	// Getting a label for the auth token is not supported
    @Override
	public String getAuthTokenLabel(String arg0) {
		throw new UnsupportedOperationException();
	}

	// Checking features for the account is not supported
    @Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}

 // Updating user credentials is not supported
    @Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}

}
