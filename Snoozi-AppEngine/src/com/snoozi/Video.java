package com.snoozi;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Video {
	
	@Id
	private String id;
	
	private String url;
	private String description;
	private int viewcount;
	private int like;
	private int dislike;
	private int level;
	
	public Video(){
		like = 0;
		dislike = 0;
		level = 0;
		viewcount = 0;
		description = "";
		url = "";
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}

	public int getViewcount() {
		return viewcount;
	}

	public int getLike() {
		return like;
	}

	public int getDislike() {
		return dislike;
	}

	public int getLevel() {
		return level;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public void setDislike(int dislike) {
		this.dislike = dislike;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
