package com.wake.wank.models;

import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.snoozi.userendpoint.Userendpoint;
import com.snoozi.userendpoint.model.User;
import com.wake.wank.MyApplication;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;


public class MyUser implements Bundleable{

	private static Long MyUserId = 0l;

	
	

	private int id;
	private Long userid;

	private String pseudo;
	private String city;
	private String country;

	private int videocount; // number of video created by the user
	private int viewcount; // number of time user has seen a video
	private int likecount; // number of time user has liked a video
	private Long signupstamp; // times of the user Signup


	private boolean hasChanged;


	@Override
	public Bundle toBundle() {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();

		b.putInt("_id",getId());
		b.putString("_pseudo",getPseudo());
		b.putString("_city",getCity());
		b.putString("_country",getCountry());
		b.putInt("_videocount",getVideocount());
		b.putInt("_viewcount",getViewcount());
		b.putInt("_likecount",getLikecount());
		b.putLong("_userid",getUserid());
		b.putLong("_signupstamp",getSignupstamp());
		b.putBoolean("_haschanged", getHasChanged());

		return b;
	}

	@Override
	public void fromBundle(Bundle b) {
		setId(b.getInt("_id"));
		setPseudo(b.getString("_pseudo"));
		setCity(b.getString("_city"));
		setCountry(b.getString("_country"));
		setVideocount(b.getInt("_videocount"));
		setViewcount( b.getInt("_viewcount"));
		setLikecount( b.getInt("_likecount"));


		setUserid(b.getLong("_userid"));
		setSignupstamp(b.getLong("_signupstamp"));
		setHasChanged(b.getBoolean("_haschanged"));
	}


	public MyUser(Bundle b) {
		fromBundle(b);
	}

	public MyUser()
	{
		setId(0);
		setPseudo("");
		setCity("??");
		setCountry("??");
		setVideocount(0);
		setViewcount(0);
		setLikecount(0);
		setUserid(0l);
		setSignupstamp(0l);

		setHasChanged(false);

	}

	/**
	 * @param cursor
	 */
	public  MyUser(Cursor cursor) {
		this(); // default constructor

		try {

			this.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns._ID)));
			this.setPseudo(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.PSEUDO)));
			this.setCity(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.CITY)));
			this.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.COUNTRY)));
			this.setVideocount(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.VIDEOCOUNT)));
			this.setViewcount(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.VIEWCOUNT)));
			this.setLikecount(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.LIKECOUNT)));
			this.setUserid(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.USERID)));
			this.setSignupstamp(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.users.Columns.SIGNUPSTAMP)));

			setHasChanged( false);

		} catch (Exception e) {
			// Error during creation of this MyVideoObject
		}
	}



	


	public void addVideocount() {

		this.setVideocount(this.getVideocount() + 1);
		setHasChanged(true);
	}


	public void addViewcount() {

		this.setViewcount(this.getViewcount() + 1);
		setHasChanged(true);
	}


	public void addLikecount() 
	{
		this.setLikecount(this.getViewcount() + 1);
		setHasChanged(true);
	}

	/**
	 * Get a specific user from the Id
	 * @param Id
	 * @return
	 */
	public static MyUser getFromSQL(int Id)
	{
		Cursor cursor = null;
		MyUser object = null;
		try
		{
			ContentResolver provider = MyApplication.getAppContext().getContentResolver();
			cursor   = provider.query(ContentUris.withAppendedId(SnooziContract.users.CONTENT_URI,Id), SnooziContract.users.PROJECTION_ALL, null,null, null);
			if (cursor.moveToFirst()) 
			{
				object = new MyUser(cursor);

			}
		}
		catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyUser.getFromSQL("+Id+") Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return object;
	}





	/**
	 * Save this User if data has changed
	 * @param provider TODO
	 * @return
	 */
	public int save() {
		int result = 0;

		if(this.getId() == 0 || hasChanged)
		{	
			try {
				ContentResolver provider = MyApplication.getAppContext().getContentResolver();

				//SAVE THE USER IN THE LOCAL DATABASE
				ContentValues values = new ContentValues();

				values.put(SnooziContract.users.Columns.PSEUDO,getPseudo() );
				values.put(SnooziContract.users.Columns.CITY,getCity() );
				values.put(SnooziContract.users.Columns.COUNTRY,getCountry() );
				values.put(SnooziContract.users.Columns.LIKECOUNT,getLikecount() );
				values.put(SnooziContract.users.Columns.VIDEOCOUNT,getVideocount() );
				values.put(SnooziContract.users.Columns.VIEWCOUNT,getViewcount() );
				values.put(SnooziContract.users.Columns.USERID,getUserid() );
				values.put(SnooziContract.users.Columns.SIGNUPSTAMP,getSignupstamp() );

				if(this.getId() == 0)
				{
					//INSERTING
					Uri videouri = provider.insert(SnooziContract.users.CONTENT_URI, values);
					//Retrieving id from path
					result = Integer.parseInt(videouri.getLastPathSegment());
					this.setId(result);
				}else
				{
					result = provider.update(ContentUris.withAppendedId(SnooziContract.users.CONTENT_URI, getId()), values,null,null);
				}


				setHasChanged(false);

				SnooziUtility.trace(TRACETYPE.INFO,"Saved User " + toString());
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR,"MyUser.save2 ERROR" + e.toString());
				result = -1;
			}
		}
		return result;
	}






	/**
	 * Delete this User in database  ( we should never delete a user )
	 * @return
	 */
	@Deprecated
	public int delete() {
		int result = 1;

		if(this.getId() > 0 )
		{

			try {
				//DELETING THE USER INTO THE DATABASE
				ContentResolver provider = MyApplication.getAppContext().getContentResolver();

				result = provider.delete(ContentUris.withAppendedId(SnooziContract.users.CONTENT_URI, getId()),null,null);


				setHasChanged(false);

				SnooziUtility.trace(TRACETYPE.INFO,"Deleted User " + getId() + " : " + toString());
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR,"MyUser.delete ERROR" + e.toString());
				result = 0;
			}

		}
		return result;
	}







	//####### ASYNC FUNCTION
	/**
	 * Synchronize user info to the server with the specified action
	 * @param syncaction type of SnooziUtility.SYNC_ACTION
	 * @param theBundeledObj bundeled object or null
	 * @return
	 */
	public static void async(SnooziUtility.SYNC_ACTION syncaction,Bundle theBundeledObj)throws IOException
	{


		Userendpoint.Builder endpointBuilder = new Userendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) { }
				});
		Userendpoint userEndpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();

		switch (syncaction) {

		case USER_UPDATE:
			if(theBundeledObj != null)
			{
				//We have a user bundled
				MyUser theUser = new MyUser(theBundeledObj);
				theUser.endPointUpdate(userEndpoint);
			}
			break;
		case USER_WAKEUP:
			endPointWakeup(userEndpoint);
			break;
		case USER_VIEW:
			if(theBundeledObj != null)
			{
				// We have a video bundled
				MyVideo theVideo = new MyVideo(theBundeledObj);
				endPointVideoViewed(userEndpoint,theVideo);
			}
			break;

		default:
			break;
		}


	

	}

	/**
	 * 
	 * @param userEndpoint
	 * @param theVideo
	 * @throws IOException
	 */
	private static void endPointVideoViewed(Userendpoint userEndpoint, MyVideo theVideo) throws IOException {
		// TODO Auto-generated method stub
		if(MyUser.getMyUserId() != 0l && theVideo.getAddedViewcount() != 0)
			userEndpoint.addUserview(MyUser.getMyUserId(),theVideo.getAddedViewcount()).execute();
		SnooziUtility.trace(TRACETYPE.INFO,"OK user addedView sended to server : ");
		
	}

	/**
	 * 
	 * @param userEndpoint
	 * @throws IOException
	 */
	private static void endPointWakeup(Userendpoint userEndpoint) throws IOException {
		// TODO Auto-generated method stub
		if(MyUser.getMyUserId() != 0l)
			userEndpoint.wakeupUser(MyUser.getMyUserId()).execute();
		SnooziUtility.trace(TRACETYPE.INFO,"OK user wakeup sended  to server : ");
		
	}

	/**
	 * @param userEndpoint
	 * @throws IOException
	 */
	private void endPointUpdate(Userendpoint userEndpoint) throws IOException {
		User usr = new User();
		if(getUserid() != 0l)
			usr.setId(getUserid());
		usr.setCity(getCity());
		usr.setCountry(getCountry());
		usr.setPseudo(getPseudo());
		usr.setLikecount(getLikecount());
		usr.setVideocount(getVideocount());
		usr.setViewcount(getViewcount());
		usr.setEmail(SnooziUtility.getAccountNames());
		if(getSignupstamp() != 0l)
			usr.setSignupstamp(getSignupstamp());
		usr = userEndpoint.updateUser(usr).execute();

		if(getUserid() == 0l)
		{
			Long userid = usr.getId();
			setUserid(userid); //We take the id from the server
			setSignupstamp(usr.getSignupstamp());
			MyUser.setMyUserId(userid);
			SnooziUtility.trace(TRACETYPE.INFO,"new  Profil server id : " + userid);

			// We save it locally
			save();  


			//We save it in SharedPref
			SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong("USERID", userid);
			editor.commit();


		}
		SnooziUtility.trace(TRACETYPE.INFO,"OK Profil Sended to server : " + toString());
		
	}





	@Override
	public String toString() {
		return getId() + " " + getPseudo() + " from " + getCountry();
	}

	//###### GETTER & SETTERS #########


	public static Long getMyUserId() {
		if(MyUserId == 0l)
		{
			Context context = MyApplication.getAppContext();
			if(context != null)
			{	
				SharedPreferences settings = context.getSharedPreferences(SnooziUtility.PREFS_NAME, Context.MODE_PRIVATE);
				MyUserId = settings.getLong("USERID", 0l);
			}
		}
		return MyUserId;
	}

	public static void setMyUserId(Long userId) {
		MyUserId = userId;

	}

	public int getId() {
		return id;
	}

	public Long getUserid() {
		return userid;
	}

	public String getPseudo() {
		return pseudo;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
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

	public boolean getHasChanged() {
		return this.hasChanged ;
	}

	public Long getSignupstamp() {
		return signupstamp;
	}


	public void setId(int id) {
		this.id = id;
		setHasChanged(true);
	}

	public void setUserid(Long userid) {
		this.userid = userid;
		setHasChanged(true);
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
		setHasChanged(true);
	}

	public void setCity(String city) {
		this.city = city;
		setHasChanged(true);
	}

	public void setCountry(String country) {
		this.country = country;
		setHasChanged(true);
	}


	public void setVideocount(int videocount) {
		this.videocount = videocount;
		setHasChanged(true);
	}

	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
		setHasChanged(true);
	}

	public void setLikecount(int likecount) {
		this.likecount = likecount;
		setHasChanged(true);
	}


	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}



	public void setSignupstamp(Long signupstamp) {
		this.signupstamp = signupstamp;
		setHasChanged(true);
	}









}
