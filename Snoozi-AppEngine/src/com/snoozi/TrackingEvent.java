package com.snoozi;

import java.util.Calendar;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable
public class TrackingEvent {

	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long key;
	  
	@Persistent
	private String type;
	
	@Persistent
	private String userid;
	
	@Persistent
	private long timestamp;
	
	@Persistent
	private String timeString;
	
	@Persistent
	private String description;
	
	@Persistent
	private String deviceInformation;
	
	@Persistent
	private String apkVersion = "—";
	
	@Persistent
	private int androidVersion;
	
	@Persistent
	private Long videoid;

	/*info :
	 * Quand on regenere, ca fait sauter le correctif
	 * si on a un JDOFatalException c'est a cause de la Key qui est null et que la method containsTrackingEvent a un probleme avec le null
	 * il faut la transformer ainsi en mettant au debut:
	 * if(trackingevent.getKey() == null)
			return false;
	 */
	
	/* Via query function
	query = mgr.newQuery(TrackingEvent.class);
	query.setFilter("type == tracktype");
	query.setRange(0, 200);
	query.setOrdering("timestamp desc");*/
	
	/* Via query filter 
	 Query.Builder query = Query.newBuilder();
	query.addKindBuilder().setName("com.snoozi.TrackingEvent");
	query.setFilter(makeFilter(
	    makeFilter("type", PropertyFilter.Operator.EQUAL, makeValue("ERROR_LOGGER")).build(),
	    makeFilter("otherfilter", PropertyFilter.Operator.LESS_THAN, makeValue(63)).build()));
	query.addOrder(makeOrder("timestamp", PropertyOrder.Direction.DESCENDING));
	query.execute()... */
	
	/* Via query string 
	 query = mgr.newQuery("select from com.snoozi.TrackingEvent " +
            " where type == tracktype " +
			 " RANGE 0,200");
			 
			 */
	
	public TrackingEvent() {
		
		Calendar cal = Calendar.getInstance();
		Date currentLocalTime = cal.getTime();
		timestamp = currentLocalTime.getTime();
		timeString = currentLocalTime.toString();
	}
	
	 public Long getKey() {
		 return key;
	  }
	
	public String getType(){
		return type;
	}
	
	public String getUserid(){
		return userid;
	}
	public Long getVideoid(){
		return videoid;
	}
	
	public long getTimestamp(){
		return timestamp;
		
	}
	public String getTimeString(){
		return timeString;
		
	}
	
	public String getDescription(){
		return description;
	}
	public String getApkVersion(){
		return apkVersion;
	}
	
	public String getDeviceInformation() {
	    return this.deviceInformation;
	}
	public int getAndroidVersion() {
	    return this.androidVersion;
	}
	
	public void setUserid(String theuserid){
		this.userid = theuserid;
	}
	public void setVideoid(Long thevideoid){
		this.videoid = thevideoid;
	}
	
	public void setTimestamp(long thetimestamp){
		this.timestamp = thetimestamp;
	}
	
	public void setString(String thetimestring){
		this.timeString = thetimestring;
	}
	
	public void setDescription(String thedescription){
		this.description = thedescription;
	}
	public void setApkVersion(String theapk){
		this.apkVersion = theapk;
	}
	
	public void setDeviceInformation(String deviceInformation) {
	    this.deviceInformation = deviceInformation;
	  }
	
	public void setAndroidVersion(int androidVersion) {
	    this.androidVersion = androidVersion;
	  }
	
}
