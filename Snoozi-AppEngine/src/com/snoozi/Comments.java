package com.snoozi;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Comments {
	
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY )
	private Long id;
	
	@Persistent
	private String description;
	@Persistent
	private int like;
	@Persistent
	private Long userid;
	@Persistent
	private String userpseudo;
	@Persistent
	private Long videoid;
	@Persistent
	private long timestamp;
	
	
	public Comments(){
		like = 0;
		description = "";
		userpseudo = "";
		userid = 0l;
		videoid = 0l;
		timestamp = System.currentTimeMillis();
	}


	public Long getId() {
		return id;
	}


	public String getDescription() {
		return description;
	}


	public int getLike() {
		return like;
	}


	public Long getUserid() {
		return userid;
	}


	public Long getVideoid() {
		return videoid;
	}


	public long getTimestamp() {
		return timestamp;
	}
	
	public String getUserpseudo() {
		return userpseudo;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public void setLike(int like) {
		this.like = like;
	}


	public void setUserid(Long userid) {
		this.userid = userid;
	}


	public void setVideoid(Long videoid) {
		this.videoid = videoid;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	


	public void setUserpseudo(String userpseudo) {
		this.userpseudo = userpseudo;
	}

}
