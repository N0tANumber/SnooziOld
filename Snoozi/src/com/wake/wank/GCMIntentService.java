package com.wake.wank;

import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.snoozi.deviceinfoendpoint.Deviceinfoendpoint;
import com.snoozi.deviceinfoendpoint.model.DeviceInfo;
import com.wake.wank.UI.RegisterActivity;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.models.CloudEndpointUtils;
import com.wake.wank.models.SyncAdapter;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

/**
 * This class is started up as a service of the Android application. It listens
 * for Google Cloud Messaging (GCM) messages directed to this device.
 * 
 * When the device is successfully registered for GCM, a message is sent to the
 * App Engine backend via Cloud Endpoints, indicating that it wants to receive
 * broadcast messages from the it.
 * 
 * Before registering for GCM, you have to create a project in Google's Cloud
 * Console (https://code.google.com/apis/console). In this project, you'll have
 * to enable the "Google Cloud Messaging for Android" Service.
 * 
 * Once you have set up a project and enabled GCM, you'll have to set the
 * PROJECT_NUMBER field to the project number mentioned in the "Overview" page.
 * 
 * See the documentation at
 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class GCMIntentService extends GCMBaseIntentService {
  private final Deviceinfoendpoint endpoint;

  /*
   * TODO: Set this to a valid project number. See
   * http://developers.google.com/eclipse/docs/cloud_endpoints for more
   * information.
   */
  public static final String PROJECT_NUMBER = "791538234183";

  
  
  /**
   * Register the device for GCM.
   * 
   * @param mContext
   *            the activity's context.
   */
  public static void register(Context mContext) {
    GCMRegistrar.checkDevice(mContext);
    GCMRegistrar.checkManifest(mContext);
    GCMRegistrar.register(mContext, PROJECT_NUMBER);
    SyncAdapter.isRegistationPending = true;
  }

  /**
   * Unregister the device from the GCM service.
   * 
   * @param mContext
   *            the activity's context.
   */
  public static void unregister(Context mContext) {
    GCMRegistrar.unregister(mContext);
  }

  public GCMIntentService() {
    super(PROJECT_NUMBER);
    
    Deviceinfoendpoint.Builder endpointBuilder = new Deviceinfoendpoint.Builder(
        AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
        new HttpRequestInitializer() {
          public void initialize(HttpRequest httpRequest) {
          }
        });
    endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
  }

  /**
   * Called on registration error. This is called in the context of a Service
   * - no dialog or UI.
   * 
   * @param context
   *            the Context
   * @param errorId
   *            an error message
   */
  @Override
  public void onError(Context context, String errorId) {
	  SnooziUtility.trace(TRACETYPE.ERROR, "Registration with Google Cloud Messaging FAILED : " + errorId);
	  SyncAdapter.isRegistationPending = false;
  }

  /**
   * Called when a cloud message has been received.
   */
  @Override
  public void onMessage(Context context, Intent intent) {
	  	// Get a GCM object instance
      GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
      // Get the type of GCM message
      String messageType = gcm.getMessageType(intent);
      /*
       * Test the message type and examine the message contents.
       * Since GCM is a general-purpose messaging system, you
       * may receive normal messages that don't require a sync
       * adapter run.
       */
      if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
      {
    	  Bundle extras = intent.getExtras();
          String message = extras.getString("message");
          if (message.equals("VIDEO_RETRIEVE"))
          {
        	  SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.VIDEO_RETRIEVE);
        	
          }
      }
      
      /*
	  sendNotificationIntent(
        context,
        "Message received via Google Cloud Messaging:\n\n"
            + intent.getStringExtra("message"), true, false);*/
      
  }

  /**
   * Called back when a registration token has been received from the Google
   * Cloud Messaging service.
   * 
   * @param context
   *            the Context
   */
  @Override
  public void onRegistered(Context context, String registration) {
    boolean alreadyRegisteredWithEndpointServer = false;
    
    try {

      /*
       * Using cloud endpoints, see if the device has already been
       * registered with the backend
       */
      DeviceInfo existingInfo = endpoint.getDeviceInfo(registration)
          .execute();

      if (existingInfo != null && registration.equals(existingInfo.getDeviceRegistrationID())) {
        alreadyRegisteredWithEndpointServer = true;
      }
    } catch (IOException e) {
      // Ignore
    }

    try {
      if (!alreadyRegisteredWithEndpointServer) {
        /*
         * We are not registered as yet. Send an endpoint message
         * containing the GCM registration id and some of the device's
         * product information over to the backend. Then, we'll be
         * registered.
         */
        DeviceInfo deviceInfo = new DeviceInfo();
        endpoint.insertDeviceInfo(
            deviceInfo
                .setDeviceRegistrationID(registration)
                .setTimestamp(System.currentTimeMillis())
                .setUserid(SnooziUtility.getAccountNames())
                .setDeviceInformation(android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE)).execute();
      }
      
      SnooziUtility.trace(TRACETYPE.DEBUG, "GCM registered  with id :  " +  registration);
     
    //Saving registration State
    SyncAdapter.requestSync(SnooziUtility.SYNC_ACTION.GCM_REGISTERED);
	  
	   
    } catch (IOException e) {
    	String msg = "Exception received when attempting to register with server at "
                + endpoint.getRootUrl() + " : " + e.toString();
    	SnooziUtility.trace(TRACETYPE.ERROR, msg);
      
      return;
    }

    
  }

  /**
   * Called back when the Google Cloud Messaging service has unregistered the
   * device.
   * 
   * @param context
   *            the Context
   */
  @Override
  protected void onUnregistered(Context context, String registrationId) {

    if (registrationId != null && registrationId.length() > 0) {

      try {
        endpoint.removeDeviceInfo(registrationId).execute();
      } catch (IOException e) 
      {
    	  String msg = "Exception received when attempting to unregister with server at "
                  + endpoint.getRootUrl() + " : " + e.toString();
      	SnooziUtility.trace(TRACETYPE.ERROR, msg);
        

        return;
      }
    }
  }

  /**
   * Generate a notification intent and dispatch it to the RegisterActivity.
   * This is how we get information from this service (non-UI) back to the
   * activity.
   * 
   * For this to work, the 'android:launchMode="singleTop"' attribute needs to
   * be set for the RegisterActivity in AndroidManifest.xml.
   * 
   * @param context
   *            the application context
   * @param message
   *            the message to send
   * @param isError
   *            true if the message is an error-related message; false
   *            otherwise
   * @param isRegistrationMessage
   *            true if this message is related to registration/unregistration
   */
  private void sendNotificationIntent(Context context, String message,
      boolean isError, boolean isRegistrationMessage) {
    Intent notificationIntent = new Intent(context, RegisterActivity.class);
    notificationIntent.putExtra("gcmIntentServiceMessage", true);
    notificationIntent.putExtra("registrationMessage",
        isRegistrationMessage);
    notificationIntent.putExtra("error", isError);
    notificationIntent.putExtra("message", message);
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(notificationIntent);
  }

  
}
