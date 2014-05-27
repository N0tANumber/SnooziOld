package com.snoozi.snoozi.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * SnooziContract give the constant to accessing the ContentProvider of the Snoozi app.
 * constant are sorted like this :
 *    - Provider > table > Columns
 * @author CtrlX
 *
 */
public class SnooziContract {
	
	private SnooziContract(){}

	/**
	 * The authority of the provider. "com.snoozi.provider"
	 */
	public static final String AUTHORITY = "com.snoozi.provider";

	/**
	 * Top level URI "content://com.snoozi.provider"
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);


	
	/**
	 * Gere les definitions de colonnes et des differents points d'entree pour le provider
	 * @see https://thenewcircle.com/s/post/1375/android_content_provider_tutorial
	 * @see http://developer.android.com/guide/topics/providers/content-provider-creating.html
	 */
	public static final class trackingevents
	{
		public static final String CONTENT_PATH = "trackingevents";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(SnooziContract.CONTENT_URI,CONTENT_PATH);
		
		public static final String CONTENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.snoozi.provider_trackingevents";
		public static final String CONTENT_MIME_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.snoozi.provider_trackingevents";
		
		public static final String TABLE = "trackingevent";
		
		public static final String[] PROJECTION_ALL = {Columns._ID,Columns.TYPE,Columns.DESCRIPTION,Columns.TIMESTAMP,Columns.TIMESTRING,Columns.VIDEOID};
		
		public static final String SORT_ORDER_DEFAULT = Columns.TIMESTAMP + " ASC";
		
		public final static class Columns implements BaseColumns{
			private Columns(){}
			
			public static final String TYPE = "type";
			public static final String TIMESTAMP = "timestamp";
			public static final String TIMESTRING = "timestring";
			public static final String DESCRIPTION = "description";
			public static final String VIDEOID = "videoid";
			
		}
	}
	
	public static final class videos
	{
		public static final String CONTENT_PATH = "videos";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(SnooziContract.CONTENT_URI,CONTENT_PATH);
		
		public static final String CONTENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.snoozi.provider_videos";
		public static final String CONTENT_MIME_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.snoozi.provider_videos";
		
		public static final String TABLE = "video";
		
		public static final String[] PROJECTION_ALL = {Columns._ID,
								Columns.URL,
								Columns.LOCALURL,
								Columns.DESCRIPTION,
								Columns.LIKE,
								Columns.DISLIKE,
								Columns.VIEWCOUNT,
								Columns.STATUS,
								Columns.FILESTATUS,
								Columns.LEVEL,
								Columns.TIMESTAMP,
								Columns.USERID
								};
		
		public static final String SORT_ORDER_DEFAULT = Columns.TIMESTAMP + " DESC";
		
		public final static class Columns implements BaseColumns{
			private Columns(){}
			
			public static final String URL = "url";
			public static final String LOCALURL = "localurl";
			public static final String DESCRIPTION = "description";
			public static final String LIKE = "like";
			public static final String DISLIKE = "dislike";
			public static final String VIEWCOUNT = "viewcount";
			public static final String STATUS = "status";
			public static final String FILESTATUS = "filestatus";
			public static final String LEVEL = "level";
			public static final String TIMESTAMP = "timestamp";
			public static final String USERID = "userid";
			
		}
	}

}
