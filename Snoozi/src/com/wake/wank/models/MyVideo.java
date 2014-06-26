package com.wake.wank.models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.snoozi.videoendpoint.Videoendpoint;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class MyVideo {

	private int id;
	private String url;
	private Long videoid;
	private String localurl;
	private String description;
	private int like;
	private int mylike;
	private int viewcount;
	private int myviewcount;
	private String status;
	private String filestatus;
	private int level;
	private Long timestamp;
	private Long downloadid;
	private Long userid;

	private int addedLike;
	private int addedViewcount;

	private boolean hasChanged;

	public MyVideo()
	{
		description = "";
		videoid = -1l;
		url = "";
		localurl = "";
		like = 0;
		mylike = 0;
		viewcount = 0;
		myviewcount = 0;
		status = "UNSET"; 
		timestamp = 0l;
		userid = 1l;
		level = 0;
		addedLike = 0;
		addedViewcount = 0;
		hasChanged = false;

	}

	/**
	 * @param cursor
	 */
	public  MyVideo(Cursor cursor) {
		this(); // default constructor

		try {

			this.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID)));
			this.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.URL)));
			this.setVideoid(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.VIDEOID)));
			this.setLocalurl(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.LOCALURL)));
			this.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.DESCRIPTION)));
			this.setLike(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.LIKE)));
			this.setMylike(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.MYLIKE)));
			this.setViewcount(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.VIEWCOUNT)));
			this.setMyviewcount(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.MYVIEWCOUNT)));
			this.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.STATUS)));
			this.setFilestatus(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.FILESTATUS)));
			this.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.LEVEL)));
			this.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.TIMESTAMP)));
			this.setDownloadid(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.DOWNLOADID)));
			this.setUserid(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.USERID)));

			hasChanged = false;

		} catch (Exception e) {
			// Error during creation of this MyVideoObject
		}
	}


	public void addViewcount(int videoViewCount) {

		this.setMyviewcount(this.getMyviewcount() + videoViewCount);
		this.addedViewcount = videoViewCount;
		// TODO : voir comment faire pour envoyer le viewcount au serveur
		hasChanged = true;
	}


	public void addLike(int i) 
	{
		//We remove the previous like value to know what to add
		this.addedLike = i - this.getMylike();

		this.setMylike(i);
		hasChanged = true;

		// TODO : voir comment faire pour envoyer le viewcount au serveur

	}


	/**
	 * Save this Objet onto local Database
	 * @param context
	 * @return
	 */
	public int save(Context context) {
		// TODO Auto-generated method stub
		int result = 1;

		if(this.getId() == 0 || hasChanged)
		{
			/* TODO : Doit  envoyer les message au serveur pour qu'il refresh ses datas concernant :
		 - viewcount
		 - dislike
		 - like
			 */
			try {

				//SAVE THE VIDEO IN THE LOCAL DATABASE
				ContentValues values = new ContentValues();
				values.put(SnooziContract.videos.Columns.LIKE,getLike() );
				values.put(SnooziContract.videos.Columns.MYLIKE,getMylike() );
				values.put(SnooziContract.videos.Columns.VIEWCOUNT,getViewcount() );
				values.put(SnooziContract.videos.Columns.MYVIEWCOUNT,getMyviewcount() );

				ContentResolver provider = context.getContentResolver();
				if(this.getId() == 0)
				{
					//INSERTING
					values.put(SnooziContract.videos.Columns.DESCRIPTION,getDescription() );
					values.put(SnooziContract.videos.Columns.STATUS,getStatus() );
					values.put(SnooziContract.videos.Columns.LEVEL,getLevel() );
					values.put(SnooziContract.videos.Columns.VIDEOID,getVideoid() );
					values.put(SnooziContract.videos.Columns.TIMESTAMP,getTimestamp() );
					values.put(SnooziContract.videos.Columns.USERID,getUserid() );
					values.put(SnooziContract.videos.Columns.URL,getUrl() );
					values.put(SnooziContract.videos.Columns.FILESTATUS,getFilestatus() );
					values.put(SnooziContract.videos.Columns.LOCALURL,getLocalurl());

					Uri videouri = provider.insert(SnooziContract.videos.CONTENT_URI, values);
					//Retrieving id from path
					result = Integer.parseInt(videouri.getLastPathSegment());
					this.setId(result);
				}else
				{

					result = provider.update(ContentUris.withAppendedId(SnooziContract.videos.CONTENT_URI, getId()), values,null,null);
				}

				hasChanged = false;

				SnooziUtility.trace(context,TRACETYPE.INFO,"Saved Video " + getId() + " " + getUrl() + " with myviewcount " + getMyviewcount());
			} catch (Exception e) {
				SnooziUtility.trace(context,TRACETYPE.ERROR,"Save Video ERROR" + e.toString());
				result = 0;
			}

		}
		return result;
	}



	/**
	 * Save to local Database and send to server modified data
	 * @param context
	 */
	public void saveAndSync(Context context) {

		if(this.save(context) > 0)
		{
			//UPDATE SUCCESS, THEN WE SEND NEW VALUE TO THE SERVER

			try {

				if(addedLike != 0 || addedViewcount != 0)
				{
					//On demande une synchro avec le server
					Bundle settingsBundle = new Bundle();
					settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
					settingsBundle.putString("action", SnooziUtility.SYNC_ACTION.SEND_RATING);
					settingsBundle.putInt("addedViewcount",addedViewcount);
					settingsBundle.putInt("addedLike", addedLike);
					settingsBundle.putLong("videoid", getVideoid());
					
					ContentResolver.requestSync(SyncAdapter.GetSyncAccount(context), SnooziContract.AUTHORITY, settingsBundle);


					addedLike = 0;
					addedViewcount = 0;
				}

			} catch (Exception e) {
				SnooziUtility.trace(context,TRACETYPE.ERROR,"saveAndSync ERROR" + e.toString());
			}



		}

	}



	/**
	 * Get the next less viewed, then older video
	 * @param context
	 * @return theVideoObject
	 */
	public static MyVideo getNextUnViewedVideo(Context context)
	{
		MyVideo videoObj = null;

		Cursor cursor = null;
		try{
			//Selecting the video by her downloadid
			ContentResolver provider = context.getContentResolver();
			cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.FILESTATUS + " LIKE ?", new String[]{"SUCCESSFUL"},  SnooziContract.videos.SORT_ORDER_UNVIEWED);
			if (cursor.moveToFirst()) 
			{
				//BUILDING THE VIDEO obj
				videoObj = new MyVideo(cursor);
			}else
			{
				//No video yet, getting one of the DUMMY Videos
				SnooziUtility.trace(context, TRACETYPE.INFO,"No video yet, getting one of the DUMMY Videos");
				cursor.close();
				cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.FILESTATUS + " LIKE ?", new String[]{"DUMMY"},  SnooziContract.videos.SORT_ORDER_UNVIEWED);
				if (cursor.moveToFirst()) 
				{
					//BUILDING THE VIDEO obj
					videoObj = new MyVideo(cursor);
				}else
				{
					// Very Wierd, no dummy video !!!
					SnooziUtility.trace(context, TRACETYPE.ERROR,"Video.getNextUnViewedVideo NO DUMMY VIDEO PRESENT !!! ");

				}
			}


		}
		catch (Exception e) {
			SnooziUtility.trace(context, TRACETYPE.ERROR,"Video.getNextUnViewedVideo Exception :  " +  e.toString());

		}finally{
			if(cursor != null)
				cursor.close();
		}

		if(videoObj == null)
		{	
			//In everyCase we return a video, even if it's a dummy one
			videoObj = new MyVideo();
			Random generator = new Random();
			int videoNumber = generator.nextInt(2);
			String path =  "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video" + videoNumber, "raw", context.getPackageName());
			videoObj.setLocalurl(path);

		}
		return videoObj;
	}





	@Override
	public String toString() {
		return "MyVideo [url=" + url + ", description=" + description + "]";
	}

	public String getPublishDate()
	{
		//Calendar calendar = Calendar.getInstance();
		//calendar.setTimeInMillis(timestamp);

		SimpleDateFormat df = new SimpleDateFormat("dd MMM yy");
		String formattedDate = df.format(new Date(timestamp));

		return formattedDate;

	}


	public int getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public Long getVideoid() {
		return videoid;
	}

	public String getLocalurl() {
		return localurl;
	}

	public String getDescription() {
		return description;
	}

	public int getLike() {
		return like;
	}

	public int getMylike() {
		return mylike;
	}


	public int getViewcount() {
		return viewcount;
	}

	public int getMyviewcount() {
		return myviewcount;
	}

	public String getStatus() {
		return status;
	}

	public String getFilestatus() {
		return filestatus;
	}

	public int getLevel() {
		return level;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Long getDownloadid() {
		return downloadid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setVideoid(Long videoid) {
		this.videoid = videoid;
	}

	public void setLocalurl(String localurl) {
		this.localurl = localurl;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public void setMylike(int mylike) {
		this.mylike = mylike;
	}


	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
	}

	public void setMyviewcount(int myviewcount) {
		this.myviewcount = myviewcount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFilestatus(String filestatus) {
		this.filestatus = filestatus;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public void setDownloadid(Long downloadid) {
		this.downloadid = downloadid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}












}
