package com.snoozi.snoozi.models;


import java.util.Timer;
import java.util.TimerTask;

import com.snoozi.snoozi.utils.SnooziUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

/**
 * Handle the music playback while waking up ( without the video )
 *  It has fadeIn capability
 * @author CtrlX
 *
 */
public class MusicHandler 
{
	private MediaPlayer m_mediaPlayer;
	private Context m_context;
	private int m_oldVolume;
	private int m_currentVolume;
	private AudioManager m_audioManager;
	
	private final static int INT_VOLUME_MAX = 100;
	private final static int INT_VOLUME_MIN = 0;
	private final static float FLOAT_VOLUME_MAX = 1;
	private final static float FLOAT_VOLUME_MIN = 0;
	

	public MusicHandler(Context context)
	{
		m_context = context;
		m_audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		m_oldVolume = m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		SharedPreferences prefs = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
		int alarmVol =  prefs.getInt("volume", m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmVol, 0);
		
	}



	/**
	 * Load a music from it's URI 
	 * @param uri
	 * @param looping indicate if we are looping the sound
	 */
	public void load(Uri uri, boolean looping)
	{
		m_mediaPlayer = MediaPlayer.create(m_context, uri);
		m_mediaPlayer.setLooping(looping);
		
	}

	/**
	 * Play the loaded music with fadeIn
	 * @param fadeDuration
	 */
	public void play(int fadeDuration)
	{
		//Set current volume, depending on fade or not
		if (fadeDuration > 0) 
			m_currentVolume = INT_VOLUME_MIN;
		else 
			m_currentVolume = INT_VOLUME_MAX;
		 
		 
		 
		updateVolume(0);

		//Play music
		if(!m_mediaPlayer.isPlaying()) m_mediaPlayer.start();

		//Start increasing volume in increments
		if(fadeDuration > 0)
		{
			final Timer timer = new Timer(true);
			TimerTask timerTask = new TimerTask() 
			{
				@Override
				public void run() 
				{
					updateVolume(1);
					if (m_currentVolume == INT_VOLUME_MAX)
					{
						timer.cancel();
						timer.purge();
					}
				}
			};

			// calculate delay, cannot be zero, set to 1 if zero
			int delay = fadeDuration/INT_VOLUME_MAX;
			if (delay == 0) delay = 1;

			timer.schedule(timerTask, delay, delay);
		}
	}

	public void pause(int fadeDuration)
	{
		//Set current volume, depending on fade or not
		if (fadeDuration > 0) 
			m_currentVolume = INT_VOLUME_MAX;
		else 
			m_currentVolume = INT_VOLUME_MIN;

		updateVolume(0);

		//Start increasing volume in increments
		if(fadeDuration > 0)
		{
			final Timer timer = new Timer(true);
			TimerTask timerTask = new TimerTask() 
			{
				@Override
				public void run() 
				{   
					updateVolume(-1);
					if (m_currentVolume == INT_VOLUME_MIN)
					{
						//Pause music
						if (m_mediaPlayer.isPlaying()) m_mediaPlayer.pause();
						timer.cancel();
						timer.purge();
					}
				}
			};

			// calculate delay, cannot be zero, set to 1 if zero
			int delay = fadeDuration/INT_VOLUME_MAX;
			if (delay == 0) delay = 1;

			timer.schedule(timerTask, delay, delay);
		}           
	}
	public void stop()
	{
		if (m_mediaPlayer.isPlaying()) 
			m_mediaPlayer.stop();
		m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, m_oldVolume, 0);
		
		m_mediaPlayer.reset();
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
		float fVolume = 1 - ((float) Math.log(INT_VOLUME_MAX - m_currentVolume) / (float) Math.log(INT_VOLUME_MAX));

		//ensure fVolume within boundaries
		if (fVolume < FLOAT_VOLUME_MIN)
			fVolume = FLOAT_VOLUME_MIN;
		else if (fVolume > FLOAT_VOLUME_MAX)
			fVolume = FLOAT_VOLUME_MAX;     

		m_mediaPlayer.setVolume(fVolume, fVolume);
	}
}