package com.snoozi;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User {
	

	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY )
	private Long id;
	
	@Persistent
	private String pseudo;
	@Persistent
	private String email;
	@Persistent
	private String city;
	@Persistent
	private String country;
	
	@Persistent
	private int wakeupcount; // number of time user has been woke up
	@Persistent
	private int videocount; // number of video created by the user
	@Persistent
	private int viewcount; // number of time user has seen a video
	@Persistent
	private int likecount; // number of time user has liked a video
	@Persistent
	private long lastwakeup; // last wokeup timestamp
	@Persistent
	private long signupstamp; //  signup timestamp


	public User(){
		pseudo = null;
		email = "";
		wakeupcount = 0;
		videocount = 0;
		viewcount = 0;
		likecount = 0;
		city = null;
		country = null;
		lastwakeup = 0l;
		signupstamp = 0l;
		
	}


	public Long getId() {
		return id;
	}


	public String getPseudo() {
		return pseudo;
	}


	public String getEmail() {
		return email;
	}




	public String getCity() {
		return city;
	}


	public String getCountry() {
		return country;
	}


	public int getWakeupcount() {
		return wakeupcount;
	}


	public int getVideocount() {
		return videocount;
	}


	public int getViewcount() {
		return viewcount;
	}


	public int getLikecount() {
		return likecount;
	}

	public long getLastwakeup() {
		return lastwakeup;
	}


	public long getSignupstamp() {
		return signupstamp;
	}
	
	
	public void setId(Long id) {
		this.id = id;
	}


	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setCity(String city) {
		this.city = city;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public void setWakeupcount(int wakeupcount) {
		this.wakeupcount = wakeupcount;
	}


	public void setVideocount(int myvideocount) {
		this.videocount = myvideocount;
	}

	public void setViewcount(int myviewcount) {
		this.viewcount = myviewcount;
	}


	public void setLikecount(int mylikecount) {
		this.likecount = mylikecount;
	}

	public void setLastwakeup(long lastwakeup) {
		this.lastwakeup = lastwakeup;
	}




	public void setSignupstamp(long signupstamp) {
		this.signupstamp = signupstamp;
	}




	
}
