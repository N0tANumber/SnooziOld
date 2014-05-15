package com.snoozi.snoozi.utils;

public enum EventType {
	DEBUGGING,
	ERROR_LOGGER,
	APP_FIRSTLAUNCH,
	APP_LAUNCH,
	APP_RATING,
	APP_COMMENT,
	ALARM_SET, /* User setting Alarm ON */
	ALARM_UNSET,/* User setting Alarm OFF */
	ALARM_LAUNCH, /* Alarm is ringing */
	ALARM_KILLED, /* Alarm is killed via android home button */
	ALARM_WAKEUP, /* user slid to play the video */
	ALARM_SNOOZE,/* user slid to repeat in 5 minutes*/
	VIDEO_VIEWED,
	VIDEO_CANCELED,
	VIDEO_RATING
}
