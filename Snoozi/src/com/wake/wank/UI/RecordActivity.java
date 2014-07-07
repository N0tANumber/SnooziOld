package com.wake.wank.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.wake.wank.R;
import com.wake.wank.models.CameraPreview;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class RecordActivity extends Activity {
	private boolean isRecording = false;

	private Camera mCamera;
	private CameraPreview mPreview;
	private MediaRecorder mMediaRecorder;

	private File outputFile;
	private Button captureButton;

	private FileOutputStream paused_file;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recordold);

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_previewold);
	 	//preview.setClipBounds(new Rect(0, 0, preview.getWidth(), preview.getWidth())); 
		preview.addView(mPreview);


		outputFile = getOutputMediaFile(this);
		try {
			paused_file = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			paused_file = null;
			e.printStackTrace();
		}

		// Add a listener to the Capture button
		captureButton = (Button) findViewById(R.id.button_captureold);
		captureButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SwitchRecording();

					}
				}
				);

	}


	protected void SwitchRecording() {
		if (isRecording) {

			stopRecording();

		} else {
			startRecording();
		}
	}


	/**
	 * 
	 */
	public void startRecording() {

		prepareVideoRecorder();

		if (mMediaRecorder != null) {
			// Camera is available and unlocked, MediaRecorder is prepared,
			// now you can start recording

			mMediaRecorder.start();

			// inform the user that recording has started
			captureButton.setText(getResources().getString(R.string.stoprecord));



			isRecording = true;
		}
		/*else {
		    // prepare didn't work, release the camera
		    releaseMediaRecorder();
		    // inform user
		}
		 */
	}


	/**
	 * @throws IOException 
	 * 
	 */
	public void stopRecording() {
		if (isRecording) 
		{
			// inform the user that recording has stopped
			captureButton.setText(getResources().getString(R.string.startrecord));

			// stop recording and release camera
			mMediaRecorder.stop();  // stop the recording
			releaseMediaRecorder(); // release the MediaRecorder object
			//mCamera.lock();         // take camera access back from MediaRecorder



			isRecording = false;
		}
	}




	@Override
	protected void onPause() {
		super.onPause();
		//releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		//releaseCamera();              // release the camera immediately on pause event
		stopRecording();
	}




	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		SnooziUtility.trace(TRACETYPE.DEBUG, "RecordActivitySnap.onstart");

		try {
			// Create an instance of Camera
			mCamera = getCameraInstance();
			// get Camera parameters
			Camera.Parameters params = mCamera.getParameters();
			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// Autofocus mode is supported
				// set the focus mode
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				// set Camera parameters
				mCamera.setParameters(params);

			}
			mPreview.setCamera(mCamera);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		mPreview.setCamera(null);
		releaseCamera();              // release the camera immediately on pause event
		try {
			paused_file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){

		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
			c.setDisplayOrientation(90);
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}


	private boolean prepareVideoRecorder(){

		if(mMediaRecorder != null || paused_file == null)
			return true;

		try {
			mMediaRecorder = new MediaRecorder();

			// Step 1: Unlock and set camera to MediaRecorder
			//mCamera.stopPreview();
			mCamera.unlock();

			mMediaRecorder.setCamera(mCamera);


			//mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
			

			// Step 2: Set sources
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			
			//mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			//mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			//mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

			// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
			//mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			
			mMediaRecorder.setProfile(CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_BACK, CamcorderProfile.QUALITY_480P));
			
			mMediaRecorder.setOutputFile(paused_file.getFD());



			//String localUrl = "file://" + this.getExternalFilesDir(Environment.DIRECTORY_MOVIES ).getPath() +"/123.mp4";
			//mMediaRecorder.setOutputFile(localUrl);
			mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
			
			//Tags the video with a 90° angle in order to tell the phone how to display it
			mMediaRecorder.setOrientationHint(90);
			
			mMediaRecorder.setMaxDuration(30 * 1000);

			/*
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setOrientationHint(90);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(this, MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
			 */

			// Step 6: Prepare configured MediaRecorder
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	private void releaseMediaRecorder(){

		try {
			if (mMediaRecorder != null) {
				mMediaRecorder.reset();   // clear recorder configuration
				mMediaRecorder.release(); // release the recorder object
				mMediaRecorder = null;
				mCamera.lock();           // lock camera for later use
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void releaseCamera(){
		try {
			if (mCamera != null){
				mCamera.release();        // release the camera for other applications
				mCamera = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(Context context){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.


		File mediaStorageDir = new File(context.getExternalFilesDir(
				Environment.DIRECTORY_MOVIES),"snoozi");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				SnooziUtility.trace(TRACETYPE.DEBUG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator +
				"VID_"+ timeStamp + ".3gp");


		return mediaFile;

	}
}
