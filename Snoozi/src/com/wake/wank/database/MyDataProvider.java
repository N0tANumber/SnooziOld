package com.wake.wank.database;


import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class MyDataProvider extends ContentProvider {
	/*
     * Defines a handle to the database helper object. The MainDatabaseHelper class is defined
     * in a following snippet.
     */
	private MainDatabaseHelper m_OpenHelper;
    private SQLiteDatabase db;

    private static ContentProviderClient provider = null;
    
	/**
	 * Constant to match URI with the pattern
	 * @see http://developer.android.com/reference/android/content/UriMatcher.html
	 */
    private static final int TRACKING = 1;
    private static final int TRACKING_ID = 2;
    
    private static final int VIDEO = 3;
    private static final int VIDEO_ID = 4;
	
    private static final int ALARM = 5;
    private static final int ALARM_ID = 6;
    
    private static final int USER = 7;
    private static final int USER_ID = 8;
    
    private static final int COMMENT = 9;
    private static final int COMMENT_ID = 10;
	
	
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
	static
	{
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.trackingevents.CONTENT_PATH, TRACKING);
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.trackingevents.CONTENT_PATH + "/#", TRACKING_ID);	
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.videos.CONTENT_PATH, VIDEO);
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.videos.CONTENT_PATH + "/#", VIDEO_ID);	
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.alarms.CONTENT_PATH, ALARM);
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.alarms.CONTENT_PATH + "/#", ALARM_ID);	
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.users.CONTENT_PATH, USER);
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.users.CONTENT_PATH + "/#", USER_ID);	
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.comments.CONTENT_PATH, COMMENT);
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.comments.CONTENT_PATH + "/#", COMMENT_ID);	
	}
	
	@Override
	public boolean onCreate() {
		m_OpenHelper = new MainDatabaseHelper(getContext());
		db = m_OpenHelper.getWritableDatabase();
		return true;
	}
	
	
	
	
	
	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		switch (match)
		{
		case TRACKING:
			return SnooziContract.trackingevents.CONTENT_MIME_TYPE;
		case TRACKING_ID:
			return SnooziContract.trackingevents.CONTENT_MIME_ITEM_TYPE;
		case VIDEO:
			return SnooziContract.videos.CONTENT_MIME_TYPE;
		case VIDEO_ID:
			return SnooziContract.videos.CONTENT_MIME_ITEM_TYPE;
		case ALARM:
			return SnooziContract.alarms.CONTENT_MIME_TYPE;
		case ALARM_ID:
			return SnooziContract.alarms.CONTENT_MIME_ITEM_TYPE;
		case USER:
			return SnooziContract.users.CONTENT_MIME_TYPE;
		case USER_ID:
			return SnooziContract.users.CONTENT_MIME_ITEM_TYPE;
		case COMMENT:
			return SnooziContract.comments.CONTENT_MIME_TYPE;
		case COMMENT_ID:
			return SnooziContract.comments.CONTENT_MIME_ITEM_TYPE;
		default:
			SnooziUtility.trace(TRACETYPE.ERROR, "MyDataProvider.getType  Unsupported URI : " + uri);
			return null;
		}
	}

	private Uri getUriForId(long id, Uri uri) {
		Uri result = null;
		try {

			if (id > 0) {
				Uri itemUri = ContentUris.withAppendedId(uri, id);
				if (!isInBatchMode()) {
					// notify all listeners of changes:
					getContext().
					getContentResolver().
					notifyChange(itemUri, null);
				}
				result= itemUri;
			}
			if(result == null)
				throw new Exception("Problem while inserting into uri: " + uri);

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyDataProvider : " + e.toString());
		}
		return result;
	}
	
	private boolean isInBatchMode()
	{
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		boolean useAuthorityUri = false;
		try {
			switch (sUriMatcher.match(uri)) {
			case TRACKING:
				//SELECT ALL THE TRACKING EVENT
				builder.setTables(SnooziContract.trackingevents.TABLE);
				if (TextUtils.isEmpty(sortOrder)) 
					sortOrder = SnooziContract.trackingevents.SORT_ORDER_DEFAULT;
				break;
				
			case TRACKING_ID :
				// SELECT ONE TRACKING EVENT
				builder.setTables(SnooziContract.trackingevents.TABLE);
				builder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
				break;
			case VIDEO:
				//SELECT ALL THE VIDEOS
				builder.setTables(SnooziContract.videos.TABLE);
				if (TextUtils.isEmpty(sortOrder)) 
					sortOrder = SnooziContract.videos.SORT_ORDER_DEFAULT;
				break;
				
			case VIDEO_ID :
				// SELECT ONE VIDEO
				builder.setTables(SnooziContract.videos.TABLE);
				builder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
				break;
			case ALARM:
				//SELECT ALL THE ALARMS
				builder.setTables(SnooziContract.alarms.TABLE);
				if (TextUtils.isEmpty(sortOrder)) 
					sortOrder = SnooziContract.alarms.SORT_ORDER_DEFAULT;
				break;
				
			case ALARM_ID :
				// SELECT ONE ALARM
				builder.setTables(SnooziContract.alarms.TABLE);
				builder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
				break;
			case USER_ID :
				// SELECT ONE USER
				builder.setTables(SnooziContract.users.TABLE);
				builder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
				break;
			case COMMENT:
				//SELECT ALL THE COMMENTS
				builder.setTables(SnooziContract.comments.TABLE);
				if (TextUtils.isEmpty(sortOrder)) 
					sortOrder = SnooziContract.comments.SORT_ORDER_DEFAULT;
				break;
				
			case COMMENT_ID :
				// SELECT ONE COMMENTS
				builder.setTables(SnooziContract.comments.TABLE);
				builder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
			}
			Cursor cursor = builder.query(
			         db, 
			         projection, 
			         selection, 
			         selectionArgs,
			         null, 
			         null, 
			         sortOrder);
			   // if we want to be notified of any changes:
			   if (useAuthorityUri) {
			      cursor.setNotificationUri(
			            getContext().getContentResolver(), 
			            SnooziContract.trackingevents.CONTENT_URI);
			   }
			   else {
			      cursor.setNotificationUri(
			            getContext().getContentResolver(), 
			            uri);
			   }
			   return cursor;
			   
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyDataProvider.query : " + e.toString());		
		}
		return null;
	}
	
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = sUriMatcher.match(uri);
		Uri result = null;
		try 
		{
			if (match == TRACKING)
			{
				long id = db.insert( SnooziContract.trackingevents.TABLE,null, values);
				result = getUriForId(id, uri);
			}else if (match == VIDEO)
			{
				long id = db.insert( SnooziContract.videos.TABLE,null, values);
				result = getUriForId(id, uri);
			}else if (match == ALARM)
			{
				long id = db.insert( SnooziContract.alarms.TABLE,null, values);
				result = getUriForId(id, uri);
			}else if (match == USER)
			{
				long id = db.insert( SnooziContract.users.TABLE,null, values);
				result = getUriForId(id, uri);
			}else if (match == COMMENT)
			{
				long id = db.insert( SnooziContract.comments.TABLE,null, values);
				result = getUriForId(id, uri);
			}else
				throw new Exception("Unsupported URI for insertion : " + uri);

		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyDataProvider.insert : " + e.toString());

		}
		return result;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int updateCount = 0;
		String idStr = "";
		String where = "";
		String table = "";
		try {

			switch (sUriMatcher.match(uri)) {
			case TRACKING:
				table = SnooziContract.trackingevents.TABLE;
				where = selection;
				break;
			case TRACKING_ID:
				table = SnooziContract.trackingevents.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case VIDEO:
				table = SnooziContract.videos.TABLE;
				where = selection;
				break;
			case VIDEO_ID:
				table = SnooziContract.videos.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case ALARM:
				table = SnooziContract.alarms.TABLE;
				where = selection;
				break;
			case ALARM_ID:
				table = SnooziContract.alarms.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case USER:
				table = SnooziContract.users.TABLE;
				where = selection;
				break;
			case USER_ID:
				table = SnooziContract.users.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case COMMENT:
				table = SnooziContract.comments.TABLE;
				where = selection;
				break;
			case COMMENT_ID:
				table = SnooziContract.comments.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			default:
				// no support for updating photos or entities!
				throw new IllegalArgumentException("Unsupported URI: " + uri);
			}
			
			
			if(!table.equals(""))
			{
				updateCount = db.update(
						table, 
						values, 
						where,
						selectionArgs);
			}
			// notify all listeners of changes:
			if (updateCount > 0 && !isInBatchMode()) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyDataProvider.update : " + e.toString());

		}
		return updateCount;
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int delCount = 0;
		String table = "";
		String idStr = "";
		String where = "";
		
		try {

			switch (sUriMatcher.match(uri)) {
			case TRACKING:
				table = SnooziContract.trackingevents.TABLE;
				where = selection;
				break;
			case TRACKING_ID:
				table = SnooziContract.trackingevents.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case VIDEO:
				table = SnooziContract.videos.TABLE;
				where = selection;
				break;
			case VIDEO_ID:
				table = SnooziContract.videos.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case ALARM:
				table = SnooziContract.alarms.TABLE;
				where = selection;
				break;
			case ALARM_ID:
				table = SnooziContract.alarms.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case USER:
				table = SnooziContract.users.TABLE;
				where = selection;
				break;
			case USER_ID:
				table = SnooziContract.users.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			case COMMENT:
				table = SnooziContract.comments.TABLE;
				where = selection;
				break;
			case COMMENT_ID:
				table = SnooziContract.comments.TABLE;
				idStr = uri.getLastPathSegment();
				where = BaseColumns._ID + " = " + idStr;
				if (!TextUtils.isEmpty(selection)) {
					where += " AND " + selection;
				}
				break;
			default:
				// no support for updating photos or entities!
				throw new IllegalArgumentException("Unsupported URI: " + uri);
			}
			
			if(!table.equals(""))
			{
				delCount = db.delete(
						table, 
						where,
						selectionArgs);
			}
			// notify all listeners of changes:
			if (delCount > 0 && !isInBatchMode()) {
				getContext().getContentResolver().notifyChange(uri, null);
			}
		} catch (Exception e) {
			SnooziUtility.trace(TRACETYPE.ERROR, "MyDataProvider.delete : " + e.toString());

		}
		return delCount;
	}


	
	/**
	 * Helper class that actually creates and manages the provider's underlying data repository.
	 */
	protected static final class MainDatabaseHelper extends SQLiteOpenHelper {

		// A string that defines the SQL statement for creating a table
		private static final String DBNAME = "WankDB.db";
		private static final int DB_VERSION = 6;
		 
		private static final String SQL_CREATE_TRACKINGEVENT = "CREATE TABLE IF NOT EXISTS " +
        		SnooziContract.trackingevents.TABLE +  // Table's name
		    "(" +                           // The columns in the table
		     SnooziContract.trackingevents.Columns._ID + "  INTEGER PRIMARY KEY, " +
		     SnooziContract.trackingevents.Columns.TYPE + " TEXT, " +
		     SnooziContract.trackingevents.Columns.TIMESTAMP + " LONG default 0, " +
		     SnooziContract.trackingevents.Columns.TIMESTRING + " TEXT, " +
		     SnooziContract.trackingevents.Columns.VIDEOID + " LONG default 0, " +
		     SnooziContract.trackingevents.Columns.DESCRIPTION + " TEXT )";
		
		private static final String SQL_CREATE_VIDEO = "CREATE TABLE IF NOT EXISTS " +
        		SnooziContract.videos.TABLE +  // Table's name
		    "(" +                           // The columns in the table
		     SnooziContract.videos.Columns._ID + "  INTEGER PRIMARY KEY, " +
		     SnooziContract.videos.Columns.URL + " TEXT, " +
		     SnooziContract.videos.Columns.VIDEOID + " LONG default 0, " +
		     SnooziContract.videos.Columns.LOCALURL + " TEXT, " +
		     SnooziContract.videos.Columns.DESCRIPTION + " TEXT, " +
		     SnooziContract.videos.Columns.EXTLINK + " TEXT, " +
		     SnooziContract.videos.Columns.LIKE + " INTEGER default 0, " +
		     SnooziContract.videos.Columns.MYLIKE + " INTEGER default 0, " +
		     SnooziContract.videos.Columns.VIEWCOUNT + " INTEGER default 0, " +
		     SnooziContract.videos.Columns.MYVIEWCOUNT + " INTEGER default 0, " +
		     SnooziContract.videos.Columns.STATUS + " TEXT, " +
		     SnooziContract.videos.Columns.FILESTATUS + " TEXT, " +
		     SnooziContract.videos.Columns.LEVEL + " INTEGER default 0, " +
		     SnooziContract.videos.Columns.TIMESTAMP + " LONG default 0, " +
		     SnooziContract.videos.Columns.DOWNLOADID + " LONG default 0, " +
		     SnooziContract.videos.Columns.USERID + " LONG default 0 )";
		
		private static final String SQL_CREATE_ALARM = "CREATE TABLE IF NOT EXISTS " +
        		SnooziContract.alarms.TABLE +  // Table's name
		    "(" +                           // The columns in the table
		     SnooziContract.alarms.Columns._ID + "  INTEGER PRIMARY KEY, " +
		     SnooziContract.alarms.Columns.ACTIVATE + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.ALARMID + " LONG default 0, " +
		     SnooziContract.alarms.Columns.HOUR + " INTEGER default 7, " +
		     SnooziContract.alarms.Columns.MINUTE + " INTEGER default 30, " +
		     SnooziContract.alarms.Columns.DAYSTRING + " TEXT, " +
		     SnooziContract.alarms.Columns.MONDAY + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.TUESDAY + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.WEDNESDAY + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.THURSDAY + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.FRIDAY + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.SATURDAY + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.SUNDAY + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.VIBRATE + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.VOLUME + " INTEGER default 0, " +
		     SnooziContract.alarms.Columns.VIDEOID + " LONG default 0 )";
		
		
		private static final String SQL_CREATE_USER = "CREATE TABLE IF NOT EXISTS " +
				SnooziContract.users.TABLE +  // Table's name
				"(" +                           // The columns in the table
				SnooziContract.users.Columns._ID + "  INTEGER PRIMARY KEY, " +
				SnooziContract.users.Columns.PSEUDO + " TEXT, " +
				SnooziContract.users.Columns.CITY + " TEXT, " +
				SnooziContract.users.Columns.COUNTRY + " TEXT, " +
				SnooziContract.users.Columns.VIDEOCOUNT + " INTEGER default 0, " +
				SnooziContract.users.Columns.VIEWCOUNT + " INTEGER default 0, " +
				SnooziContract.users.Columns.LIKECOUNT + " INTEGER default 0, " +
				SnooziContract.users.Columns.USERID + " LONG default 0, " +
				SnooziContract.users.Columns.SIGNUPSTAMP + " LONG default 0 )";
		
		
		private static final String SQL_CREATE_COMMENT = "CREATE TABLE IF NOT EXISTS " +
        		SnooziContract.comments.TABLE +  // Table's name
		    "(" +                           // The columns in the table
		     SnooziContract.comments.Columns._ID + "  INTEGER PRIMARY KEY, " +
		     SnooziContract.comments.Columns.COMMENTID + " LONG default 0, " +
		     SnooziContract.comments.Columns.DESCRIPTION + " TEXT, " +
		     SnooziContract.comments.Columns.LIKE + " INTEGER default 0, " +
		     SnooziContract.comments.Columns.MYLIKE + " INTEGER default 0, " +
		     SnooziContract.comments.Columns.TIMESTAMP + " LONG default 0, " +
		     SnooziContract.comments.Columns.VIDEOID + " LONG default 0, " +
		     SnooziContract.comments.Columns.USERPSEUDO + " TEXT, " +
		     SnooziContract.comments.Columns.USERID + " LONG default 0 )";
		
	    
	    /*
	     * Instantiates an open helper for the provider's SQLite data repository
	     * Do not do database creation and upgrade here.
	     */
	    MainDatabaseHelper(Context context) {
	        super(context, DBNAME, null,DB_VERSION);
	        
	    }

	    /*
	     * Creates the data repository. This is called when the provider attempts to open the
	     * repository and SQLite reports that it doesn't exist.
	     */
	    @Override
		public void onCreate(SQLiteDatabase db) {
	        // Creates the main table
	    	db.execSQL(SQL_CREATE_TRACKINGEVENT);
	    	db.execSQL(SQL_CREATE_VIDEO);
	    	db.execSQL(SQL_CREATE_ALARM);
	    	db.execSQL(SQL_CREATE_USER);
	    	db.execSQL(SQL_CREATE_COMMENT);
	    	
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.beginTransaction();
			
			// This allows you to upgrade from any version to the next most 
            // recent one in multiple steps as you don't know if the user has
            // skipped any of the previous updates
            
	        try {
	            if(oldVersion<2){
	                // Upgrade database structure from Version 1 to 2
	            	db.execSQL("DROP TABLE " + SnooziContract.trackingevents.TABLE + " ;");
	            	db.execSQL(SQL_CREATE_TRACKINGEVENT);
	    	        db.execSQL(SQL_CREATE_VIDEO);
	    	        SnooziUtility.trace(TRACETYPE.INFO, "Successfully upgraded to Version 2");
					
	            }
	            
	            if(oldVersion<3){
	                // Upgrade database structure from Version 2 to 3
	            	db.execSQL(SQL_CREATE_ALARM);
	            	SnooziUtility.trace(TRACETYPE.INFO, "Successfully upgraded to Version 3");
	            }
	            
	            if(oldVersion<4){
	                // Upgrade database structure from Version 3 to 4
	            	db.execSQL(SQL_CREATE_USER);
	            	SnooziUtility.trace(TRACETYPE.INFO, "Successfully upgraded to Version 4");
	            }
	            
	            
	            if(oldVersion<5){
	            	// Upgrade database structure from Version 4 to 5
	            	String alterTable = "ALTER TABLE " + SnooziContract.videos.TABLE + 
	            			" ADD "+ SnooziContract.videos.Columns.EXTLINK + " TEXT " ;
	           		db.execSQL(alterTable);
	            	SnooziUtility.trace(TRACETYPE.INFO, "Successfully upgraded to Version 5");
	            }
	            
	            if(oldVersion<6){
	            	// Upgrade database structure from Version 5 to 6
	            	db.execSQL(SQL_CREATE_COMMENT);
	            	SnooziUtility.trace(TRACETYPE.INFO, "Successfully upgraded to Version 6");
	            }
	            
	            /*
	            if(oldVersion<7){
	            	// Upgrade database structure from Version 6 to 7
	            	String alterTable = "ALTER ....";
	            	
	            	db.execSQL(alterTable);
	            	SnooziUtility.trace(TRACETYPE.INFO, "Successfully upgraded to Version 7");
	            }*/

	            // Only when this code is executed, the changes will be applied 
	            // to the database
	            db.setTransactionSuccessful();
	        } catch(Exception ex){
	            ex.printStackTrace();
	        } finally {
	            // Ends transaction
	            // If there was an error, the database won't be altered
	            db.endTransaction();
	        }
		}
	}
}
