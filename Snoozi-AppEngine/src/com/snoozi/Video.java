package com.snoozi;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Video {
	
	public enum VIDEO_STATUS{
		DUMMY,
		PENDING,
		OK,
		REPORT,
		DELETE
	}
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY )
	private Long id;
	
	@Persistent
	private String url;
	@Persistent
	private String description;
	@Persistent
	private String extlink;
	@Persistent
	private int viewcount;
	@Persistent
	private int like;
	@Persistent
	private int level;
	@Persistent
	private Long userid;
	@Persistent
	private long timestamp;
	@Persistent
	private VIDEO_STATUS status;
	
	/*info :
	 * Quand on regenere, ca fait sauter le correctif
	 * si on a un JDOFatalException c'est a cause de la Key qui est null et que la method containsTrackingEvent a un probleme avec le null
	 * il faut la transformer ainsi en mettant au debut:
	 * if(video.getId() ==null)
			return false;
	 */

	public Video(){
		like = 0;
		level = 0;
		viewcount = 0;
		description = "";
		extlink = "";
		url = "";
		userid = 0l;
		timestamp = System.currentTimeMillis();
		status = VIDEO_STATUS.PENDING;
	}

	public Long getId() {
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

	

	public int getLevel() {
		return level;
	}
	

	public String getExtlink() {
		return extlink;
	}


	public void setId(Long id) {
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

	

	public void setLevel(int level) {
		this.level = level;
	}
	
	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public VIDEO_STATUS getStatus() {
		return status;
	}

	public void setStatus(VIDEO_STATUS status) {
		this.status = status;
	}

	public void setExtlink(String extlink) {
		this.extlink = extlink;
	}
}
