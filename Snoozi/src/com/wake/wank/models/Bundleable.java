package com.wake.wank.models;

import android.os.Bundle;

/**
 * interface for passing object in intent
 * @author CtrlX
 * @see http://stackoverflow.com/questions/13363046/passing-custom-parcelable-object-extra-or-in-arraylist-to-remoteviewsservice-bre
 */
public interface Bundleable {
	
		public Bundle toBundle();

		public void fromBundle(Bundle b);
		
	}
