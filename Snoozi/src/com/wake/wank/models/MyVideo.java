package com.wake.wank.models;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.snoozi.videoendpoint.Videoendpoint;
import com.snoozi.videoendpoint.model.CollectionResponseVideo;
import com.snoozi.videoendpoint.model.Video;
import com.wake.wank.MyApplication;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.receivers.VideoDownloadReceiver;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class MyVideo  implements Bundleable {

	private int id;
	private String url;
	private Long videoid;
	private String localurl;
	private String description;
	private String extlink;
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

	@Override
	public Bundle toBundle() {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();

		b.putInt("_id",id);
		b.putString("_url",url);
		b.putString("_localurl",localurl);
		b.putString("_description",description);
		b.putString("_extlink",extlink);
		b.putInt("_like",like);
		b.putInt("_mylike",mylike);
		b.putInt("_viewcount",viewcount);
		b.putInt("_myviewcount",myviewcount);
		b.putString("_status",status);
		b.putString("_filestatus",filestatus);
		b.putInt("_level",level);
		b.putLong("_timestamp",timestamp);
		b.putLong("_downloadid",downloadid);
		b.putInt("_addedlike",addedLike);
		b.putInt("_addedviewcount",addedViewcount);

		b.putLong("_videoid",videoid);
		b.putLong("_userid",userid);


		b.putBoolean("_haschanged", getHasChanged());

		return b;
	}

	@Override
	public void fromBundle(Bundle b) {
		setId(b.getInt("_id"));
		setUrl(b.getString("_url"));
		setLocalurl(b.getString("_localurl"));
		setDescription(b.getString("_description"));
		setExtlink(b.getString("_extlink"));
		setLike(b.getInt("_like"));
		setMylike(b.getInt("_mylike"));
		setViewcount(b.getInt("_viewcount"));
		setMyviewcount(b.getInt("_myviewcount"));
		setStatus (b.getString("_status"));
		setFilestatus ( b.getString("_filestatus"));
		setLevel(b.getInt("_level"));
		setTimestamp(b.getLong("_timestamp"));
		setDownloadid(b.getLong("_downloadid"));
		setAddedLike(b.getInt("_addedlike"));
		setAddedViewcount(b.getInt("_addedviewcount"));

		setVideoid(b.getLong("_videoid"));
		setUserid(b.getLong("_userid"));

		setHasChanged(b.getBoolean("_haschanged"));
	}

	public MyVideo(Bundle b) {
		fromBundle(b);
	}


	public MyVideo()
	{
		setDescription("");
		setExtlink("");
		setVideoid(-1l);
		setUrl("");
		setLocalurl("");
		setLike(0);
		setMylike(0);
		setViewcount(0);
		setMyviewcount(0);
		setStatus("UNSET"); 
		setTimestamp(0l);
		setUserid( 1l);
		setLevel( 0);
		setAddedLike( 0);
		setAddedViewcount( 0);
		setHasChanged(false);

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
			this.setExtlink(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.EXTLINK)));
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

			setHasChanged( false);

		} catch (Exception e) {
			// Error during creation of this MyVideoObject
		}
	}


	public void addViewcount(int i) {

		this.setMyviewcount(this.getMyviewcount() + i);
		this.setAddedViewcount( i);

	}


	public void addLike(int i) 
	{
		
		//We remove the previous like value to know what to add
		int myfinallike = (this.getMylike() + i) % 4;
		
		int difference = myfinallike - this.getMylike();
		this.setLike(getLike() + difference);
		this.setAddedLike( difference);
		
		this.setMylike(myfinallike);
		

	}


	/**
	 * Save this Objet onto local Database
	 * @return
	 */
	public int save() {
		int result = 0;

		if(this.getId() == 0 || hasChanged)
		{

			//on met a jour les likes
			try {

				//SAVE THE VIDEO IN THE LOCAL DATABASE
				ContentValues values = new ContentValues();
				values.put(SnooziContract.videos.Columns.LIKE,getLike() );
				values.put(SnooziContract.videos.Columns.MYLIKE,getMylike() );
				values.put(SnooziContract.videos.Columns.VIEWCOUNT,getViewcount() );
				values.put(SnooziContract.videos.Columns.MYVIEWCOUNT,getMyviewcount() );

				ContentResolver provider = MyApplication.getAppContext().getContentResolver();
				if(this.getId() == 0)
				{
					//INSERTING
					values.put(SnooziContract.videos.Columns.DESCRIPTION,getDescription() );
					values.put(SnooziContract.videos.Columns.EXTLINK,getExtlink() );
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

				
				setHasChanged(false);

				SnooziUtility.trace(TRACETYPE.INFO,"Saved Video " + getId() + " " + getUrl() + " with myviewcount " + getMyviewcount());
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR,"MyVideo.save Exception " + e.toString());
				result = -1;
			}

		}
		return result;
	}

	
	

	/**
	 * Get every alarm in the database
	 * @return
	 */
	public static List<MyVideo> getListFromSQL()
	{
		return getListFromSQL(null, null);
	}

	/**
	 * Get every Video that matche the condition
	 * @param WhereClause  ex : SnooziContract.videos.Columns.STATUS + " = ?"
	 * @param WhereValues  ex : new String[]{"OK"}
	 * @return
	 */
	public static List<MyVideo> getListFromSQL(String WhereClause, String[] WhereValues)
	{
		List<MyVideo> videoList = new ArrayList<MyVideo>();
		ContentResolver provider = MyApplication.getAppContext().getContentResolver();
		Cursor cursor = null;
		MyVideo video = null;
		try
		{


			cursor   = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, WhereClause,WhereValues, null);
			if (cursor.moveToFirst()) 
			{

				do {
					video = new MyVideo(cursor);
					videoList.add(video);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyVideo.getFromSQL Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return videoList;
	}




	/**
	 * Get the next less viewed, then older video
	 * @return theVideoObject
	 */
	public static MyVideo getNextUnViewedVideo()
	{
		MyVideo videoObj = null;
		Context context = null;
		Cursor cursor = null;
		try{
			context = MyApplication.getAppContext();
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
				SnooziUtility.trace(TRACETYPE.INFO, "No video yet, getting one of the DUMMY Videos");
				cursor.close();
				cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.FILESTATUS + " LIKE ?", new String[]{"DUMMY"},  SnooziContract.videos.SORT_ORDER_UNVIEWED);
				if (cursor.moveToFirst()) 
				{
					//BUILDING THE VIDEO obj
					videoObj = new MyVideo(cursor);
				}else
				{
					// Very Wierd, no dummy video !!!
					SnooziUtility.trace(TRACETYPE.ERROR, "MyVideo.getNextUnViewedVideo NO DUMMY VIDEO PRESENT !!! ");

				}
			}


		}
		catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyVideo.getNextUnViewedVideo Exception :  " +  e.toString());

		}finally{
			if(cursor != null)
				cursor.close();
		}

		if(videoObj == null && context != null)
		{	
			//In everyCase we return a video, even if it's a dummy one
			videoObj = new MyVideo();
			Random generator = new Random();
			int videoNumber = generator.nextInt(2);
			String path =  "android.resource://" + context.getPackageName() + "/" + context.getResources().getIdentifier("video" + videoNumber, "raw", context.getPackageName());
			videoObj.setLocalurl(path);
			videoObj.setHasChanged(false);

		}
		return videoObj;
	}







	//####### ASYNC FUNCTION




	/**
	 * Synchronize video info to the server with the specified action
	 * @param syncaction type of SnooziUtility.SYNC_ACTION
	 * @param theBundeledObj bundeled object or null
	 * @return
	 */
	public static void async(SnooziUtility.SYNC_ACTION syncaction, Bundle theBundeledObj)throws IOException
	{


		Videoendpoint.Builder endpointBuilder = new Videoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) { }
				});
		Videoendpoint videoEndpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		MyVideo theVideo = null;
		if(theBundeledObj != null)
			theVideo = new MyVideo( theBundeledObj);

		switch (syncaction) {

		case VIDEO_RATING:
			if(theVideo != null)
				theVideo.endPointRateVideo(videoEndpoint);
			break;

		case VIDEO_RETRIEVE:
			retrieveRecentVideo(videoEndpoint);
			break;
		case VIDEO_CLEANUP:
			cleanupOldVideo();
		default:
			break;
		}

	}

	/**
	 * @param videoEndpoint
	 * @throws IOException
	 */
	private  void endPointRateVideo(Videoendpoint videoEndpoint) throws IOException {
		if(getVideoid() > 0)
		{
			
			videoEndpoint.rateVideo(getVideoid(),getAddedLike(),getAddedViewcount() ).execute();
			setAddedLike(0);
			setAddedViewcount(0);
			SnooziUtility.trace(TRACETYPE.INFO,"OK Video rating sended to server : " + toString() + " with addedViewcount " + getAddedViewcount() + " with addedLike " + getAddedLike());
		}
	}





	/**
	 * Called async from a syncAdapter 
	 * Get if needed all recent video from the server
	 * @param provider
	 * @param videoEndpoint 
	 * @return
	 */
	private static boolean retrieveRecentVideo( Videoendpoint videoEndpoint)
	{
		boolean success = true;
		int maxDownloadedVideo = 5 ;
		ContentResolver provider = null;


		//We check how many video are still waiting not viewed
		Cursor playedcursor = null;
		try {
			provider = MyApplication.getAppContext().getContentResolver();

			playedcursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ID, SnooziContract.videos.Columns.MYVIEWCOUNT + " == 0 AND " + SnooziContract.videos.Columns.FILESTATUS + " != ? AND  " + SnooziContract.videos.Columns.FILESTATUS + " != ? ",new String[]{"ERROR","DUMMY"},  null);
			int notPlayedVideoCount = playedcursor.getCount();
			SnooziUtility.trace(TRACETYPE.DEBUG,"in stock : " +  notPlayedVideoCount + " - maxDownloadedVideo : " + maxDownloadedVideo);
			maxDownloadedVideo = maxDownloadedVideo - notPlayedVideoCount;
			// If We have enough video to play, we skip the retrieval of new video
			if(maxDownloadedVideo < 0)
				maxDownloadedVideo = 0;
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "retrieveRecentVideo Exception :  " +  e.toString());
			return false;
		}finally
		{
			if(playedcursor != null)
				playedcursor.close();
		}

		if(maxDownloadedVideo >0)
		{

			//getting the highest video STAMP
			Long fromstamp = 0l;
			try {
				playedcursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.FILESTATUS + " != ? ",new String[]{"UNSET"},  SnooziContract.videos.SORT_ORDER_TIMESTAMP);
				if (playedcursor.moveToFirst()) 
				{
					fromstamp = playedcursor.getLong(playedcursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.TIMESTAMP));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				SnooziUtility.trace(TRACETYPE.ERROR, "MyVideo.retrieveRecentVideo Exception getting the highest video STAMP :  " +  e.toString());

			}



			SnooziUtility.trace(TRACETYPE.INFO, "Getting from server " +  maxDownloadedVideo + " new video timestamp > " + fromstamp);
			CollectionResponseVideo videolist = null;
			//Getting a list of all recent video
			try {
				videolist = videoEndpoint.getVideosFromUser()
						.setFromstamp(fromstamp)
						.setUserid((long) 2)  /* we only take the wank user id*/
						.setLimit(maxDownloadedVideo)
						.execute();
			} catch (Exception e) {
				videolist = null;
				SnooziUtility.trace(TRACETYPE.ERROR, "MyVideo.retrieveRecentVideo Exception Getting a list of all recent video :  " +  e.toString());
				return false;
			}

			// for each video, we synchronise with values in local Database
			Cursor cursor = null;

			try {


				if(videolist != null)
				{
					List<Video> videoItemList = videolist.getItems();
					if(videoItemList != null)
					{
						String videoPath = "file://" + MyApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES ).getPath() +"/" ;
						for(Video video : videoItemList) 
						{
							SnooziUtility.trace(TRACETYPE.INFO,"looking in local BDD for video " + video.getUrl());
							//Preparing video data to insert or update ( both case)
							ContentValues values = new ContentValues();
							values.put(SnooziContract.videos.Columns.DESCRIPTION,video.getDescription() );
							values.put(SnooziContract.videos.Columns.EXTLINK,video.getExtlink() );
							values.put(SnooziContract.videos.Columns.LIKE,video.getLike() );
							values.put(SnooziContract.videos.Columns.VIEWCOUNT,video.getViewcount() );
							values.put(SnooziContract.videos.Columns.STATUS,video.getStatus() );
							values.put(SnooziContract.videos.Columns.LEVEL,video.getLevel() );
							try 
							{
								// We look in the content provider if we already have that video in stock
								cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.URL + " LIKE ?", new String[]{video.getUrl()},  null);

								if(cursor.getCount() == 0)
								{
									//INSERTION OF THE VIDEO
									values.put(SnooziContract.videos.Columns.VIDEOID,video.getId() );
									values.put(SnooziContract.videos.Columns.TIMESTAMP,video.getTimestamp() );
									values.put(SnooziContract.videos.Columns.USERID,video.getUserid() );
									values.put(SnooziContract.videos.Columns.URL,video.getUrl() );
									values.put(SnooziContract.videos.Columns.FILESTATUS,"PENDING" );
									String localUrl = videoPath + video.getId() + ".mp4";
									values.put(SnooziContract.videos.Columns.LOCALURL,localUrl);

									Uri videouri = provider.insert(SnooziContract.videos.CONTENT_URI, values);
									SnooziUtility.trace(TRACETYPE.INFO,"INSERTED Video " + video.getId() + " : " + videouri.toString());



								}else
								{
									if (cursor.moveToFirst()) 
									{
										//UPDATE OF THE VIDEO
										int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID));
										String url = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.URL));
										String fileStatus = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.FILESTATUS));
										if(!url.equals(video.getUrl()) || fileStatus.equals("ERROR"))
										{
											//URL has changedor is in error, we must redownload the file
											values.put(SnooziContract.videos.Columns.URL,video.getUrl() );
											String localUrl = videoPath +  video.getId() + ".mp4";
											values.put(SnooziContract.videos.Columns.LOCALURL,localUrl);
											values.put(SnooziContract.videos.Columns.FILESTATUS,"PENDING" );
										}

										//Already present in database, we update the info ( description, like, dislike, viewcount,level)
										int updatecount = provider.update(ContentUris.withAppendedId(SnooziContract.videos.CONTENT_URI, id), values,null,null);
										SnooziUtility.trace(TRACETYPE.INFO,"UPDATED Video " + video.getId() + " : " + updatecount);
									}
								}

							}
							catch (Exception e) {
								SnooziUtility.trace(TRACETYPE.ERROR, "MyVideo.retrieveRecentVideo Exception :  " +  e.toString());
								success = false;
							}finally{
								if(cursor != null)
									cursor.close();
							}
						}
					}
				}
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR, "MyVideo.retrieveRecentVideo Exception  :  " +  e.toString());

			}
		}


		//Launching Download Manager
		VideoDownloadReceiver.launchingPendingDownload();

		return success;
	}


	/**
	 * Called async from a syncAdapter 
	 * Remove old video from disk and database
	 * @return
	 */
	public static boolean cleanupOldVideo() {
		Cursor cursor = null;
		int maxKeptVideo = 4;
		boolean success = false;

		int cleanedCount = 0;
		int counter = 0;
		
		//android.os.Debug.waitForDebugger();
		
		try {
			//We check all tracking event in Contentproviders
			ContentResolver provider = MyApplication.getAppContext().getContentResolver();
			cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_URL, SnooziContract.videos.Columns.MYVIEWCOUNT + " > 0 AND " + SnooziContract.videos.Columns.FILESTATUS + " != ?",new String[]{"DUMMY"},  SnooziContract.videos.SORT_ORDER_TIMESTAMP);

			if (cursor.moveToFirst()) 
			{
				if(cursor.getCount() > maxKeptVideo)
				{
					do {
						counter++;
						
						//We dont delete the first maxKeptVideo videos
						if(counter <= maxKeptVideo)
							continue;
						
						int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID));
						String localurl = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.LOCALURL));

						Uri uri = Uri.parse(localurl);
						File file = new File( uri.getPath());
						Boolean isdeleted = false;
						if( file.exists() )
							isdeleted = file.delete();
						
						if(counter > 10)
						{
							cleanedCount++;
							//We delete the database entry
							// Deleting the old Video 
							provider.delete(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.Columns._ID + " = ? ", new String[]{String.valueOf(id)});
							SnooziUtility.trace(TRACETYPE.INFO, "cleanupOldVideo - Permanently Deleted video :  " +  id);
						}else if(isdeleted)
						{
							// We only place the video filestatus as "DELETED"
							ContentValues values = new ContentValues();
							values.put(SnooziContract.videos.Columns.FILESTATUS,"DELETED" );
							provider.update(ContentUris.withAppendedId(SnooziContract.videos.CONTENT_URI, id), values,null,null);

							SnooziUtility.trace(TRACETYPE.INFO, "cleanupOldVideo - Deleted video :  " +  id);
						}

					} while (cursor.moveToNext());
				}
			}
			success = true;

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "cleanupOldVideo Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		SnooziUtility.trace(TRACETYPE.INFO,"OK Video cleanedup count : " + counter);
		
		return success;

	}



	//###### GETTER & SETTERS #########

	@Override
	public String toString() {
		return  getId() + " Video [url=" + url + ", description=" + description + "]";
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

	public boolean getHasChanged() {
		// TODO Auto-generated method stub
		return hasChanged;
	}

	public Long getUserid() {
		return userid;
	}

	public int getAddedLike() {
		return addedLike;
	}

	public int getAddedViewcount() {
		return addedViewcount;

	}
	
	public String getExtlink() {
		return extlink;
	}
	
	public void setId(int id) {
		this.id = id;
		setHasChanged(true);
	}

	public void setUrl(String url) {
		this.url = url;
		setHasChanged(true);
	}

	public void setVideoid(Long videoid) {
		this.videoid = videoid;
		setHasChanged(true);
	}

	public void setLocalurl(String localurl) {
		this.localurl = localurl;
		setHasChanged(true);
	}

	public void setDescription(String description) {
		this.description = description;
		setHasChanged(true);
	}

	public void setLike(int like) {
		this.like = like;
		setHasChanged(true);
	}

	public void setMylike(int mylike) {
		this.mylike = mylike;
		setHasChanged(true);
	}


	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
		setHasChanged(true);
	}

	public void setMyviewcount(int myviewcount) {
		this.myviewcount = myviewcount;
		setHasChanged(true);
	}

	public void setStatus(String status) {
		this.status = status;
		setHasChanged(true);
	}

	public void setFilestatus(String filestatus) {
		this.filestatus = filestatus;
		setHasChanged(true);
	}

	public void setLevel(int level) {
		this.level = level;
		setHasChanged(true);
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		setHasChanged(true);
	}

	public void setDownloadid(Long downloadid) {
		this.downloadid = downloadid;
		setHasChanged(true);
	}

	public void setUserid(Long userid) {
		this.userid = userid;
		setHasChanged(true);
	}


	public void setAddedLike(int addedLike) {
		this.addedLike = addedLike;
		setHasChanged(true);
	}

	public void setAddedViewcount(int addedViewcount) {
		this.addedViewcount = addedViewcount;
		setHasChanged(true);
	}

	

	public void setExtlink(String extlink) {
		this.extlink = extlink;
		setHasChanged(true);
	}

	public void setHasChanged(boolean value) {
		// TODO Auto-generated method stub
		hasChanged = value;
	}










}
