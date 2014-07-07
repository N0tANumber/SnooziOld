package com.snoozi;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Alarm {
	
	public enum ALARM_STATUS{
		UNASSIGNED,
		ASSIGNED_FRIEND,
		ASSIGNED_RANDOM
	}
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY )
	private Long id;
	@Persistent
	private int hour;
	@Persistent
	private int minute;
	@Persistent
	private int volume;
	
	@Persistent
	private String daystring;
	
	@Persistent
	private Boolean monday;
	@Persistent
	private Boolean tuesday;
	@Persistent
	private Boolean wednesday;
	@Persistent
	private Boolean thursday;
	@Persistent
	private Boolean friday;
	@Persistent
	private Boolean saturday;
	@Persistent
	private Boolean sunday;
	
	@Persistent
	private Boolean activate;
	@Persistent
	private Boolean vibrate;
	

	
	@Persistent
	private Long userid;
	@Persistent
	private Long videoid;
	@Persistent
	private ALARM_STATUS status;
	
	/*info :
	 * Quand on regenere, ca fait sauter le correctif
	 * si on a un JDOFatalException c'est a cause de la Key qui est null et que la method containsTrackingEvent a un probleme avec le null
	 * il faut la transformer ainsi en mettant au debut:
	 * if(video.getId() ==null)
			return false;
	 */

	public Alarm(){
		hour = 0;
		minute = 0;
		volume = 0;
		daystring = "";
		monday = false;
		tuesday = false;
		wednesday = false;
		thursday = false;
		friday = false;
		saturday = false;
		sunday = false;
		activate = false;
		vibrate = false;
		userid = 0l;
		videoid = 0l;
		status= ALARM_STATUS.UNASSIGNED;
	}

	public Long getId() {
		return id;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getVolume() {
		return volume;
	}

	public String getDaystring() {
		return daystring;
	}

	public Boolean getMonday() {
		return monday;
	}

	public Boolean getTuesday() {
		return tuesday;
	}

	public Boolean getWednesday() {
		return wednesday;
	}

	public Boolean getThursday() {
		return thursday;
	}

	public Boolean getFriday() {
		return friday;
	}

	public Boolean getSaturday() {
		return saturday;
	}

	public Boolean getSunday() {
		return sunday;
	}

	public Boolean getActivate() {
		return activate;
	}

	public Boolean getVibrate() {
		return vibrate;
	}

	public Long getUserid() {
		return userid;
	}

	public Long getVideoid() {
		return videoid;
	}

	public ALARM_STATUS getStatus() {
		return status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public void setDaystring(String daystring) {
		this.daystring = daystring;
	}

	public void setMonday(Boolean monday) {
		this.monday = monday;
	}

	public void setTuesday(Boolean tuesday) {
		this.tuesday = tuesday;
	}

	public void setWednesday(Boolean wednesday) {
		this.wednesday = wednesday;
	}

	public void setThursday(Boolean thursday) {
		this.thursday = thursday;
	}

	public void setFriday(Boolean friday) {
		this.friday = friday;
	}

	public void setSaturday(Boolean saturday) {
		this.saturday = saturday;
	}

	public void setSunday(Boolean sunday) {
		this.sunday = sunday;
	}

	public void setActivate(Boolean activate) {
		this.activate = activate;
	}

	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public void setVideoid(Long videoid) {
		this.videoid = videoid;
	}

	public void setStatus(ALARM_STATUS status) {
		this.status = status;
	}

}
