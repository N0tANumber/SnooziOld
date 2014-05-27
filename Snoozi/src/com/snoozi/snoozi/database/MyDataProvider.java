package com.snoozi.snoozi.database;


import com.snoozi.snoozi.database.SnooziContract.videos.Columns;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.SnooziUtility.TRACETYPE;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MyDataProvider extends ContentProvider {
	/*
     * Defines a handle to the database helper object. The MainDatabaseHelper class is defined
     * in a following snippet.
     */
    private MainDatabaseHelper m_OpenHelper;
    private SQLiteDatabase db;

	/**
	 * Constant to match URI with the pattern
	 * @see http://developer.android.com/reference/android/content/UriMatcher.html
	 */
    private static final int TRACKING = 1;
    private static final int TRACKING_ID = 2;
    
    private static final int VIDEO = 3;
    private static final int VIDEO_ID = 4;
	
	
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
	static
	{
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.trackingevents.CONTENT_PATH, TRACKING);
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.trackingevents.CONTENT_PATH + "/#", TRACKING_ID);	
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.videos.CONTENT_PATH, VIDEO);
		sUriMatcher.addURI(SnooziContract.AUTHORITY, SnooziContract.videos.CONTENT_PATH + "/#", VIDEO_ID);	
	}
	
	@Override
	public boolean onCreate() {
		m_OpenHelper = new MainDatabaseHelper(getContext());
		db = m_OpenHelper.getWritableDatabase();
		return true;
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
			}else
				throw new Exception("Unsupported URI for insertion : " + uri);

		} catch (Exception e) {
			SnooziUtility.trace(getContext(), TRACETYPE.ERROR, "MyDataProvider.insert : " + e.toString());

		}
		return result;
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
		default:
			SnooziUtility.trace(getContext(), TRACETYPE.ERROR, "MyDataProvider.getType  Unsupported URI : " + uri);
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
			SnooziUtility.trace(getContext(), TRACETYPE.ERROR, "MyDataProvider : " + e.toString());
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
				builder.appendWhere(SnooziContract.trackingevents.Columns._ID + " = " + uri.getLastPathSegment());
				
			case VIDEO:
				//SELECT ALL THE VIDEOS
				builder.setTables(SnooziContract.videos.TABLE);
				if (TextUtils.isEmpty(sortOrder)) 
					sortOrder = SnooziContract.videos.SORT_ORDER_DEFAULT;
				break;
				
			case VIDEO_ID :
				// SELECT ONE VIDEO
				builder.setTables(SnooziContract.videos.TABLE);
				builder.appendWhere(SnooziContract.videos.Columns._ID + " = " + uri.getLastPathSegment());
				
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
			SnooziUtility.trace(getContext(), TRACETYPE.ERROR, "MyDataProvider.query : " + e.toString());		
		}
		return null;
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
				where = SnooziContract.trackingevents.Columns._ID + " = " + idStr;
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
				where = SnooziContract.videos.Columns._ID + " = " + idStr;
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
			SnooziUtility.trace(getContext(), TRACETYPE.ERROR, "MyDataProvider.update : " + e.toString());

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
				where = SnooziContract.trackingevents.Columns._ID + " = " + idStr;
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
				where = SnooziContract.videos.Columns._ID + " = " + idStr;
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
			SnooziUtility.trace(getContext(), TRACETYPE.ERROR, "MyDataProvider.delete : " + e.toString());

		}
		return delCount;
	}


	
	/**
	 * Helper class that actually creates and manages the provider's underlying data repository.
	 */
	protected static final class MainDatabaseHelper extends SQLiteOpenHelper {

		// A string that defines the SQL statement for creating a table
		private static final String DBNAME = "SnooziDB.db";
		private static final int DB_VERSION = 2;
		 
		private static final String SQL_CREATE_TRACKINGEVENT = "CREATE TABLE IF NOT EXISTS " +
        		SnooziContract.trackingevents.TABLE +  // Table's name
		    "(" +                           // The columns in the table
		     SnooziContract.trackingevents.Columns._ID + "  INTEGER PRIMARY KEY, " +
		     SnooziContract.trackingevents.Columns.TYPE + " TEXT, " +
		     SnooziContract.trackingevents.Columns.TIMESTAMP + " LONG, " +
		     SnooziContract.trackingevents.Columns.TIMESTRING + " TEXT, " +
		     SnooziContract.trackingevents.Columns.VIDEOID + " INTEGER, " +
		     SnooziContract.trackingevents.Columns.DESCRIPTION + " TEXT )";
		
		private static final String SQL_CREATE_VIDEO = "CREATE TABLE IF NOT EXISTS " +
        		SnooziContract.videos.TABLE +  // Table's name
		    "(" +                           // The columns in the table
		     SnooziContract.videos.Columns._ID + "  INTEGER PRIMARY KEY, " +
		     SnooziContract.videos.Columns.URL + " TEXT, " +
		     SnooziContract.videos.Columns.LOCALURL + " TEXT, " +
		     SnooziContract.videos.Columns.DESCRIPTION + " TEXT, " +
		     SnooziContract.videos.Columns.LIKE + " INTEGER, " +
		     SnooziContract.videos.Columns.DISLIKE + " INTEGER, " +
		     SnooziContract.videos.Columns.VIEWCOUNT + " INTEGER, " +
		     SnooziContract.videos.Columns.STATUS + " TEXT, " +
		     SnooziContract.videos.Columns.FILESTATUS + " TEXT, " +
		     SnooziContract.videos.Columns.LEVEL + " INTEGER, " +
		     SnooziContract.videos.Columns.TIMESTAMP + " LONG, " +
		     SnooziContract.videos.Columns.USERID + " LONG )";
		
	
	    
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
	    public void onCreate(SQLiteDatabase db) {
	        // Creates the main table
	        db.execSQL(SQL_CREATE_TRACKINGEVENT);
	        db.execSQL(SQL_CREATE_VIDEO);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO : Make it better ( not by deleting table )
			db.beginTransaction();
			
			// This allows you to upgrade from any version to the next most 
            // recent one in multiple steps as you don't know if the user has
            // skipped any of the previous updates
            
	        try {
	            if(oldVersion<2){
	                // Upgrade database structure from Version 1 to 2
	                db.execSQL(SQL_CREATE_VIDEO);
	                SnooziUtility.trace(null, TRACETYPE.INFO,"Successfully upgraded to Version 2");
					
	                Log.i("DATABASE","Successfully upgraded to Version 2");
	            }
	            /*
	            if(oldVersion<3){
	                // Upgrade database structure from Version 2 to 3
	                String alterTable = "ALTER ....";

	                db.execSQL(alterTable);
	                Log.i("DATABASE","Successfully upgraded to Version 3");
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
