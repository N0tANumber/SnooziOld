package com.wake.wank.database;


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
	 * The authority of the provider. "com.wake.wankprovider"
	 */
	public static final String AUTHORITY = "com.wake.wankprovider";

	/**
	 * Top level URI "content://com.wake.wankprovider"
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
		
		public static final String CONTENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		public static final String CONTENT_MIME_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		
		public static final String TABLE = "trackingevent";
		
		public static final String[] PROJECTION_ID = {Columns._ID};
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
		
		public static final String CONTENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		public static final String CONTENT_MIME_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		
		public static final String TABLE = "video";
		
		public static final String[] PROJECTION_ID = {Columns._ID};
		public static final String[] PROJECTION_URL = {Columns._ID,Columns.LOCALURL};
		public static final String[] PROJECTION_ALL = {Columns._ID,
								Columns.URL,
								Columns.VIDEOID,
								Columns.LOCALURL,
								Columns.DESCRIPTION,
								Columns.LIKE,
								Columns.MYLIKE,
								Columns.VIEWCOUNT,
								Columns.MYVIEWCOUNT,
								Columns.STATUS,
								Columns.FILESTATUS,
								Columns.LEVEL,
								Columns.TIMESTAMP,
								Columns.USERID,
								Columns.DOWNLOADID
								};
		
		public static final String SORT_ORDER_DEFAULT = Columns.TIMESTAMP + " DESC";
		public static final String SORT_ORDER_TIMESTAMP = Columns.TIMESTAMP + " DESC";
		public static final String SORT_ORDER_UNVIEWED = Columns.MYVIEWCOUNT + " ASC, " + Columns.MYLIKE + " DESC";
		
		public final static class Columns implements BaseColumns{
			private Columns(){}
			
			public static final String URL = "url";
			public static final String VIDEOID = "videoid";
			public static final String LOCALURL = "localurl";
			public static final String DESCRIPTION = "description";
			public static final String LIKE = "like";
			public static final String MYLIKE = "mylike";
			public static final String VIEWCOUNT = "viewcount";
			public static final String MYVIEWCOUNT = "myviewcount";
			public static final String STATUS = "status";
			public static final String FILESTATUS = "filestatus";
			public static final String LEVEL = "level";
			public static final String TIMESTAMP = "timestamp";
			public static final String USERID = "userid";
			public static final String DOWNLOADID = "downloadid";
			
		}
		
	}
	
	


	public static final class alarms
	{
		public static final String CONTENT_PATH = "alarms";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(SnooziContract.CONTENT_URI,CONTENT_PATH);
		
		public static final String CONTENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		public static final String CONTENT_MIME_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		
		public static final String TABLE = "alarm";
		
		public static final String[] PROJECTION_ID = {Columns._ID};
		public static final String[] PROJECTION_ALL = {Columns._ID,
								Columns.ACTIVATE,
								Columns.ALARMID,
								Columns.HOUR,
								Columns.MINUTE,
								Columns.DAYSTRING,
								Columns.MONDAY,
								Columns.TUESDAY,
								Columns.WEDNESDAY,
								Columns.THURSDAY,
								Columns.FRIDAY,
								Columns.SATURDAY,
								Columns.SUNDAY,
								Columns.VIBRATE,
								Columns.VOLUME,
								Columns.VIDEOID
								};
		
		public static final String SORT_ORDER_DEFAULT = Columns._ID + " ASC";
				
		public final static class Columns implements BaseColumns{
			private Columns(){}
			
			public static final String ACTIVATE = "activate";
			public static final String ALARMID = "alarmid";
			public static final String HOUR = "hour";
			public static final String MINUTE = "minute";
			public static final String DAYSTRING = "daystring";
			public static final String MONDAY = "monday";
			public static final String TUESDAY = "tuesday";
			public static final String WEDNESDAY = "wednesday";
			public static final String THURSDAY = "thursday";
			public static final String FRIDAY = "friday";
			public static final String SATURDAY = "saturday";
			public static final String SUNDAY = "sunday";
			public static final String VIBRATE = "vibrate";
			public static final String VOLUME = "volume";
			public static final String VIDEOID = "videoid";
			
		}
		
	}

	public static final class users
	{
		public static final String CONTENT_PATH = "users";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(SnooziContract.CONTENT_URI,CONTENT_PATH);
		
		public static final String CONTENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		public static final String CONTENT_MIME_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "_" + CONTENT_PATH;
		
		public static final String TABLE = "user";
		
		public static final String[] PROJECTION_ID = {Columns._ID};
		public static final String[] PROJECTION_ALL = {Columns._ID,
								Columns.PSEUDO,
								Columns.CITY,
								Columns.COUNTRY,
								Columns.VIDEOCOUNT,
								Columns.VIEWCOUNT,
								Columns.LIKECOUNT,
								Columns.USERID,
								Columns.SIGNUPSTAMP
								};
		
		public static final String SORT_ORDER_DEFAULT = Columns._ID + " ASC";
				
		public final static class Columns implements BaseColumns{
			private Columns(){}
			
			public static final String PSEUDO = "pseudo";
			public static final String CITY = "city";
			public static final String COUNTRY = "country";
			public static final String VIDEOCOUNT = "videocount";
			public static final String VIEWCOUNT = "viewcount";
			public static final String LIKECOUNT = "likecount";
			public static final String USERID = "userid";
			public static final String SIGNUPSTAMP = "signupstamp";
			
		}
		
		
	}
	
	
	
	
}
