package com.wake.wank.models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.snoozi.commentsendpoint.Commentsendpoint;
import com.snoozi.commentsendpoint.model.CollectionResponseComments;
import com.snoozi.commentsendpoint.model.Comments;
import com.wake.wank.MyApplication;
import com.wake.wank.database.SnooziContract;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class MyComments  implements Bundleable {

	private int id;
	private Long commentid;
	private String description;
	private int like;
	private int mylike;
	private Long timestamp;
	private Long userid;
	private String userpseudo;
	private Long videoid;


	private boolean hasChanged;
	private int addedLike;


	@Override
	public Bundle toBundle() {
		Bundle b = new Bundle();

		b.putInt("_id",id);
		b.putLong("_commentid",commentid);
		b.putString("_description",description);
		b.putInt("_like",like);
		b.putInt("_mylike",mylike);
		b.putLong("_timestamp",timestamp);
		b.putString("_userpseudo",userpseudo);

		b.putLong("_videoid",videoid);
		b.putLong("_userid",userid);


		b.putBoolean("_haschanged", getHasChanged());

		return b;
	}

	@Override
	public void fromBundle(Bundle b) {
		setId(b.getInt("_id"));
		setCommentid(b.getLong("_commentid"));
		setDescription(b.getString("_description"));
		setLike(b.getInt("_like"));
		setMylike(b.getInt("_mylike"));
		setTimestamp(b.getLong("_timestamp"));
		setUserpseudo(b.getString("_userpseudo"));

		setVideoid(b.getLong("_videoid"));
		setUserid(b.getLong("_userid"));

		setHasChanged(b.getBoolean("_haschanged"));
	}

	public MyComments(Bundle b) {
		fromBundle(b);
	}


	public MyComments()
	{
		setDescription("");
		setLike(0);
		setMylike(0);
		setUserpseudo(""); 
		setTimestamp(0l);
		setCommentid( 0l);
		setUserid( 0l);
		setVideoid( 0l);
		setHasChanged(false);

	}

	/**
	 * @param cursor
	 */
	public  MyComments(Cursor cursor) {
		this(); // default constructor

		try {
			this.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns._ID)));
			this.setCommentid(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.COMMENTID)));
			this.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.DESCRIPTION)));
			this.setLike(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.LIKE)));
			this.setMylike(cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.MYLIKE)));
			this.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.TIMESTAMP)));
			this.setVideoid(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.VIDEOID)));
			this.setUserid(cursor.getLong(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.USERID)));
			this.setUserpseudo(cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.USERPSEUDO)));

			setHasChanged( false);

		} catch (Exception e) {
			// Error during creation of this MyComments Object
		}
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

				//SAVE THE COMMENT IN THE LOCAL DATABASE
				ContentValues values = new ContentValues();
				values.put(SnooziContract.comments.Columns.LIKE,getLike() );
				values.put(SnooziContract.comments.Columns.MYLIKE,getMylike() );

				ContentResolver provider = MyApplication.getAppContext().getContentResolver();
				if(this.getId() == 0)
				{
					//INSERTING
					values.put(SnooziContract.comments.Columns.DESCRIPTION,getDescription() );
					values.put(SnooziContract.comments.Columns.COMMENTID,getCommentid() );
					values.put(SnooziContract.comments.Columns.TIMESTAMP,getTimestamp() );
					values.put(SnooziContract.comments.Columns.USERID,getUserid() );
					values.put(SnooziContract.comments.Columns.USERPSEUDO,getUserpseudo() );
					values.put(SnooziContract.comments.Columns.VIDEOID,getVideoid() );

					Uri videouri = provider.insert(SnooziContract.comments.CONTENT_URI, values);
					//Retrieving id from path
					result = Integer.parseInt(videouri.getLastPathSegment());
					this.setId(result);
				}else
				{

					result = provider.update(ContentUris.withAppendedId(SnooziContract.comments.CONTENT_URI, getId()), values,null,null);
				}


				setHasChanged(false);

				SnooziUtility.trace(TRACETYPE.INFO,"Saved Comments " + getId() + " " + getDescription() + " with userid " + getUserid());
			} catch (Exception e) {
				SnooziUtility.trace(TRACETYPE.ERROR,"MyComments.save Exception " + e.toString());
				result = -1;
			}

		}
		return result;
	}




	/**
	 * Get every comments in the database
	 * @return
	 */
	public static List<MyComments> getListFromSQL()
	{
		return getListFromSQL(null, null);
	}

	/**
	 * Get every Comments that match the condition
	 * @param WhereClause  ex : SnooziContract.comments.Columns.STATUS + " = ?"
	 * @param WhereValues  ex : new String[]{"OK"}
	 * @return
	 */
	public static List<MyComments> getListFromSQL(String WhereClause, String[] WhereValues)
	{
		List<MyComments> commentList = new ArrayList<MyComments>();
		ContentResolver provider = MyApplication.getAppContext().getContentResolver();
		Cursor cursor = null;
		MyComments comment = null;
		try
		{


			cursor   = provider.query(SnooziContract.comments.CONTENT_URI, SnooziContract.comments.PROJECTION_ALL, WhereClause,WhereValues, null);
			if (cursor.moveToFirst()) 
			{

				do {
					comment = new MyComments(cursor);
					commentList.add(comment);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyComments.getFromSQL Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return commentList;
	}






	//####### ASYNC FUNCTION




	/**
	 * Synchronize comment info to the server with the specified action
	 * @param syncaction type of SnooziUtility.SYNC_ACTION
	 * @param theBundeledObj bundeled object or null
	 * @return
	 */
	public static void async(SnooziUtility.SYNC_ACTION syncaction, Bundle theBundeledObj)throws IOException
	{


		Commentsendpoint.Builder endpointBuilder = new Commentsendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) { }
				});
		Commentsendpoint commentEndpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();

		switch (syncaction) {

		case COMMENT_SEND:
			MyComments theComment = null;
			if(theBundeledObj != null)
				theComment = new MyComments( theBundeledObj);

			if(theComment != null)
				theComment.endPointSendComment(commentEndpoint);
			break;

		case COMMENT_RATING:
			//TODO : rating sending
			break;
		case COMMENT_RETRIEVE:
			if(theBundeledObj != null)
			{
				//We get the id of the video associated
				Long videoid = theBundeledObj.getLong("videoid");
				retrieveVideoComment(commentEndpoint,videoid);
			}
			break;
		case COMMENT_CLEANUP:
			if(theBundeledObj != null)
			{
				//We get the id of the video associated
				Long videoid = theBundeledObj.getLong("videoid");
				cleanupOldComment(videoid);
			}
			
		default:
			break;
		}

	}

	/**
	 * @param commentEndpoint
	 * @throws IOException
	 */
	private  void endPointSendComment(Commentsendpoint commentEndpoint) throws IOException {
		if(getVideoid() > 0)
		{
			Comments comment = new Comments();
			comment.setDescription(this.getDescription());
			comment.setUserid(MyUser.getMyUserId());
			comment.setVideoid(this.getVideoid());
			comment.setUserpseudo(MyUser.getMyUserPseudo());

			comment = commentEndpoint.insertComments(comment).execute();
			if(comment != null)
			{
				setTimestamp(comment.getTimestamp());
				setCommentid(comment.getId());
				setAddedLike(0);
				save();
				
			}
			
			SnooziUtility.trace(TRACETYPE.INFO,"OK Comment sended to server : " + toString());
		}
	}





	/**
	 * Called async from a syncAdapter 
	 * Get if needed all recent video from the server
	 * @param provider
	 * @param commentEndpoint 
	 * @param videoid 
	 * @return
	 */
	private static boolean retrieveVideoComment( Commentsendpoint commentEndpoint, Long videoid)
	{
		boolean success = true;
		int maxDownloadedComments = 30;
		ContentResolver provider = null;

		// for now we limit the comments to the first 50
		//we should get the comment by block from the last timestamp comment




		//getting the highest comment STAMP for this video
		Cursor downloadedcursor = null;
		Long fromstamp = 0l;
		try {
			provider = MyApplication.getAppContext().getContentResolver();

			downloadedcursor = provider.query(SnooziContract.comments.CONTENT_URI, SnooziContract.comments.PROJECTION_ALL, SnooziContract.comments.Columns.VIDEOID + " = "  + videoid , null ,  SnooziContract.comments.SORT_ORDER_REVERSE);
			if (downloadedcursor.moveToFirst()) 
			{
				fromstamp = downloadedcursor.getLong(downloadedcursor.getColumnIndexOrThrow(SnooziContract.comments.Columns.TIMESTAMP));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SnooziUtility.trace(TRACETYPE.ERROR, "MyComments.retrieveVideoComment Exception getting the highest comment STAMP :  " +  e.toString());

		}



		SnooziUtility.trace(TRACETYPE.INFO, "Getting from server " +  maxDownloadedComments + " new comments timestamp > " + fromstamp);
		CollectionResponseComments commentlist = null;
		//Getting a list of all recent video
		try {
			commentlist = commentEndpoint.getCommentsFromVideo()
					.setFromstamp(fromstamp)
					.setVideoid(videoid)  /* we only take the wank user id*/
					.setLimit(maxDownloadedComments)
					.execute();
		} catch (Exception e) {
			commentlist = null;
			SnooziUtility.trace(TRACETYPE.ERROR, "MyComments.retrieveVideoComment Exception Getting a list of all recent comment for video "+videoid+" :  " +  e.toString());
			return false;
		}

		// for each video, we synchronise with values in local Database
		Cursor cursor = null;

		try {


			if(commentlist != null)
			{
				List<Comments> commentsItemList = commentlist.getItems();
				if(commentsItemList != null)
				{
					for(Comments comment : commentsItemList) 
					{
						SnooziUtility.trace(TRACETYPE.INFO,"looking in local BDD for comment " + comment.getId());
						//Preparing comment data to insert or update ( both case)
						ContentValues values = new ContentValues();
						values.put(SnooziContract.comments.Columns.DESCRIPTION,comment.getDescription() );
						values.put(SnooziContract.comments.Columns.LIKE,comment.getLike() );
						try 
						{
							// We look in the content provider if we already have that comment in stock
							cursor = provider.query(SnooziContract.comments.CONTENT_URI, SnooziContract.comments.PROJECTION_ALL, SnooziContract.comments.Columns.COMMENTID + " = " + comment.getId(), null,  null);
							
							
							if(cursor.getCount() == 0)
							{
								//INSERTION OF THE COMMENT
								values.put(SnooziContract.comments.Columns.COMMENTID,comment.getId() );
								values.put(SnooziContract.comments.Columns.TIMESTAMP,comment.getTimestamp() );
								values.put(SnooziContract.comments.Columns.USERID,comment.getUserid() );
								values.put(SnooziContract.comments.Columns.USERPSEUDO,comment.getUserpseudo() );
								values.put(SnooziContract.comments.Columns.VIDEOID,comment.getVideoid() );
								
								Uri commenturi = provider.insert(SnooziContract.comments.CONTENT_URI, values);
								SnooziUtility.trace(TRACETYPE.INFO,"INSERTED Comment " + comment.getId() + " : " + commenturi.toString());



							}else
							{
								if (cursor.moveToFirst()) 
								{
									//UPDATE OF THE COMMENT
									int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.comments.Columns._ID));
									
									//Already present in database, we update the info ( description, like)
									int updatecount = provider.update(ContentUris.withAppendedId(SnooziContract.comments.CONTENT_URI, id), values,null,null);
									SnooziUtility.trace(TRACETYPE.INFO,"UPDATED Comment " + comment.getId() + " : " + updatecount);
								}
							}

						}
						catch (Exception e) {
							SnooziUtility.trace(TRACETYPE.ERROR, "MyComments.retrieveVideoComment Exception :  " +  e.toString());
							success = false;
						}finally{
							if(cursor != null)
								cursor.close();
						}
					}
				}
			}
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyComments.retrieveVideoComment Exception  :  " +  e.toString());

		}

		return success;
	}


	/**
	 * Called async from a syncAdapter 
	 * Remove old comment from database
	 * @return
	 */
	public static boolean cleanupOldComment(Long videoid) {
		Cursor cursor = null;
		boolean success = false;

		
		//android.os.Debug.waitForDebugger();

		try {
			//We delete all comments  in Contentproviders
			ContentResolver provider = MyApplication.getAppContext().getContentResolver();
			cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.comments.PROJECTION_ID, SnooziContract.comments.Columns.VIDEOID + " = " + videoid ,null, null);

			if (cursor.moveToFirst()) 
			{
				
					do {
						
						int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID));
						
						
						
							//We delete the database entry
							provider.delete(SnooziContract.comments.CONTENT_URI, SnooziContract.comments.Columns._ID + " = ? ", new String[]{String.valueOf(id)});
							SnooziUtility.trace(TRACETYPE.INFO, "cleanupOldComment - Permanently Deleted comment :  " +  id);
						

					} while (cursor.moveToNext());
				
			}
			success = true;

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "cleanupOldComment Exception :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		
		return success;

	}



	//###### GETTER & SETTERS #########

	@Override
	public String toString() {
		return  getId() + " Comment [user=" + userid + ", description=" + description + "]";
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

	
	public Long getVideoid() {
		return videoid;
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



	public Long getTimestamp() {
		return timestamp;
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

	

	public Long getCommentid() {
		return commentid;
	}
	public String getUserpseudo() {
		return userpseudo;
	}

	public void setId(int id) {
		this.id = id;
		setHasChanged(true);
	}


	public void setVideoid(Long videoid) {
		this.videoid = videoid;
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


	

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
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

	
	public void setHasChanged(boolean value) {
		// TODO Auto-generated method stub
		hasChanged = value;
	}



	public void setCommentid(Long commentid) {
		this.commentid = commentid;
	}


	public void setUserpseudo(String userpseudo) {
		this.userpseudo = userpseudo;
	}










}
