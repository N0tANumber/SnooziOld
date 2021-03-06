package com.wake.wank.models;


import java.util.Timer;
import java.util.TimerTask;

import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * Handle the music playback while waking up ( without the video )
 *  It has fadeIn capability
 * @author CtrlX
 *
 */
public class AlarmSound 
{
	private MediaPlayer m_mediaPlayer;
	private Context m_context;
	
	//private int m_mainStreamVolume;
	
	private int m_currentVolume;
	private AudioManager m_audioManager;
	private Uri m_uri;
	
	private final static int INT_VOLUME_MAX = 100;
	private final static int INT_VOLUME_MIN = 0;
	private final static float FLOAT_VOLUME_MAX = 1;
	private final static float FLOAT_VOLUME_MIN = 0;
	

	public AlarmSound(Context context, MyAlarm alrm)
	{
		m_context = context;
		m_audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		//m_mainStreamVolume = m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		//SharedPreferences prefs = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		
		//m_alarmVolume =  prefs.getInt("volume", m_mainStreamVolume);
		if(alrm !=null)
			m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alrm.getVolume(), 0);
		//m_audioManager.setStreamVolume(AudioManager.STREAM_, m_alarmVolume, 0);
		
	}



	/**
	 * Load a music from it's URI 
	 * @param uri
	 * @param looping indicate if we are looping the sound
	 */
	public void load(Uri uri, boolean looping)
	{
		release();
		m_uri = uri;
		
		m_mediaPlayer = MediaPlayer.create(m_context, m_uri);
		m_mediaPlayer.setLooping(looping);
		SnooziUtility.trace(TRACETYPE.INFO, "ALARM LOADED : " + uri.toString());
		m_currentVolume = 0;
	}

	/**
	 * Play the loaded music with fadeIn
	 * @param fadeDuration
	 */
	public void play( int fadeDuration)
	{
		SnooziUtility.trace(TRACETYPE.INFO, "ALARM START PLAYING");
		//Play music
		if(m_mediaPlayer ==null || m_mediaPlayer.isPlaying())
			return;
		
		m_mediaPlayer.start();
		//Set current volume, depending on fade or not
		if (fadeDuration == 0) 
			m_currentVolume = INT_VOLUME_MAX;
		 
		updateVolume(0);

		//Start increasing volume in increments
		if(fadeDuration > 0 && m_currentVolume < INT_VOLUME_MAX)
		{
			int timerPeriod = 100; // each 100millis. the volume timer fire
			int volumeStep = (int) ((float)(INT_VOLUME_MAX * timerPeriod) / fadeDuration );
			if(volumeStep == 0)
				volumeStep = 2;
			final int finalvolumeStep = volumeStep;
			
			final Timer timer = new Timer(true);
			
			TimerTask timerTask = new TimerTask() 
			{
				
				@Override
				public void run() 
				{
					try {
						if(m_mediaPlayer == null)
						{
							//no media player anymore, we stop the timer
							timer.cancel();
							timer.purge();
							
						}else if(m_mediaPlayer.isPlaying())
						{
							updateVolume(finalvolumeStep);
							if (m_currentVolume == INT_VOLUME_MAX)
							{
								timer.cancel();
								timer.purge();
							}
						}
						
					} catch (Exception e) {
						SnooziUtility.trace(TRACETYPE.ERROR, "ALARM timerTask error : " + e.toString());
						timer.cancel();
						timer.purge();
					}
				}
			};

			timer.schedule(timerTask, timerPeriod, timerPeriod);
		}
	}

	public void pause()
	{
		SnooziUtility.trace(TRACETYPE.INFO, "ALARM PAUSED");
		try{
		if (m_mediaPlayer !=null && m_mediaPlayer.isPlaying()) 
			m_mediaPlayer.pause();
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "ALARM pause error : " + e.toString());
		}
	}
	
	public void stop()
	{
		SnooziUtility.trace(TRACETYPE.INFO, "ALARM STOPPED");
		try {
			if (m_mediaPlayer !=null && m_mediaPlayer.isPlaying()) 
				m_mediaPlayer.stop();
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "ALARM stop error : " + e.toString());
		}
		m_currentVolume = 0;
	}
	
	public void release()
	{
		
		
		// we replace the old stream volume
		//m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, m_mainStreamVolume, 0);
		
		if(m_mediaPlayer != null)
		{
			m_mediaPlayer.reset();
			m_mediaPlayer.release();
		}
		m_mediaPlayer = null;
	}
	
	/**
	 * Used by the TimerTask  to fade the volume
	 * @param change
	 */
	private void updateVolume(int change)
	{
		//increment or decrement depending on type of fade
		m_currentVolume = m_currentVolume + change;

		//ensure iVolume within boundaries
		if (m_currentVolume < INT_VOLUME_MIN)
			m_currentVolume = INT_VOLUME_MIN;
		else if (m_currentVolume > INT_VOLUME_MAX)
			m_currentVolume = INT_VOLUME_MAX;

		//convert to float value
		float fVolume = ((float)m_currentVolume) / ((float)INT_VOLUME_MAX);
		//float fVolume = 1 - ((float) Math.log(INT_VOLUME_MAX - m_currentVolume) / (float) Math.log(INT_VOLUME_MAX));
			
		Log.i("VOLUME","volume : " + fVolume );

		//ensure fVolume within boundaries
		if (fVolume < FLOAT_VOLUME_MIN)
			fVolume = FLOAT_VOLUME_MIN;
		else if (fVolume > FLOAT_VOLUME_MAX)
			fVolume = FLOAT_VOLUME_MAX;     

		m_mediaPlayer.setVolume(fVolume, fVolume);
	}
}