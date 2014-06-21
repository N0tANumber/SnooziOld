package com.wake.wank.utils;


/**
 * Each type of TRACKING EVENT
 * @author CtrlX
 *
 */
public enum TrackingEventAction {
	/*DEBUGGING EVENT */
	DEBUGGING,
	LOGGER,
	
	
	/* APPLICATION EVENT */
	FIRSTLAUNCH,
	RATING,
	COMMENT,
	
	/*ALARM EVENT */
	SET, /* User setting Alarm ON */
	UNSET,/* User setting Alarm OFF */
	LAUNCH, /* Alarm is ringing */
	KILLED, /* Alarm is killed via android home button */
	WAKEUP, /* user slid to play the video */
	SNOOZE,/* user slid to repeat in 5 minutes*/
	
	/* VIDEO EVENT */
	VIEWED,
	CANCELED,
}
