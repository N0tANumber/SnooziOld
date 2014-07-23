package com.snoozi;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class UserVideo {
	

	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY )
	private Long id;
	
	@Persistent
	private Boolean isFavorite;
	
	@Persistent
	private Long userid;
	
	@Persistent
	private Long videoid;
	
	@Persistent
	private int viewcount; // number of time user has seen a video
	@Persistent
	private int likecount; // number of time user has liked a video
	

	public UserVideo(){
		isFavorite = false;
		viewcount = 0;
		likecount = 0;
		userid = 0l;
		videoid = 0l;
		
	}


	public Long getId() {
		return id;
	}


	public Boolean getIsFavorite() {
		return isFavorite;
	}


	public Long getUserid() {
		return userid;
	}


	public Long getVideoid() {
		return videoid;
	}


	public int getViewcount() {
		return viewcount;
	}


	public int getLikecount() {
		return likecount;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}


	public void setUserid(Long userid) {
		this.userid = userid;
	}


	public void setVideoid(Long videoid) {
		this.videoid = videoid;
	}


	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
	}


	public void setLikecount(int likecount) {
		this.likecount = likecount;
	}




	
}
