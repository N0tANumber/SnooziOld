package com.snoozi.snoozi.receivers;

import com.snoozi.snoozi.database.SnooziContract;
import com.snoozi.snoozi.utils.SnooziUtility;
import com.snoozi.snoozi.utils.SnooziUtility.TRACETYPE;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

public class VideoDownloadReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) 
		{
			long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			Query query = new Query();
			//query.setFilterById(enqueue);

			DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			Cursor c = dm.query(query);
			Cursor cursor = null;
			if (c.moveToFirst()) 
			{
				int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

				try{
					//Selecting the video by her downloadid
					ContentResolver provider = context.getContentResolver();
					cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.DOWNLOADID + " LIKE ?", new String[]{downloadId+""},  null);
					if (cursor.moveToFirst()) 
					{
						//UPDATE FILESTATUS OF THE VIDEO
						int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID));
						ContentValues values = new ContentValues();
						boolean toUpdate = false;

						switch (c.getInt(columnIndex)) {
						case DownloadManager.STATUS_SUCCESSFUL:
							values.put(SnooziContract.videos.Columns.FILESTATUS,"SUCCESSFUL" );
							toUpdate = true;
							SnooziUtility.trace(context,TRACETYPE.DEBUG,"Video Downloaded : " + downloadId );

							break;
						case DownloadManager.STATUS_FAILED:
							SnooziUtility.trace(context,TRACETYPE.DEBUG,"Video Download Failed : " + downloadId );
							values.put(SnooziContract.videos.Columns.FILESTATUS,"PENDING" );
							toUpdate = true;
							break;
						case DownloadManager.STATUS_PAUSED:
							SnooziUtility.trace(context,TRACETYPE.DEBUG,"Video Download Paused : " + downloadId );
							break;
						case DownloadManager.STATUS_PENDING:
							SnooziUtility.trace(context,TRACETYPE.DEBUG,"Video Download Pending : " + downloadId );
							break;
						case DownloadManager.STATUS_RUNNING:
							SnooziUtility.trace(context,TRACETYPE.DEBUG,"Video Download Running : " + downloadId );
							break;

						default:
							break;
						}

						if(toUpdate)
						{
							//we update the info about the file
							provider.update(ContentUris.withAppendedId(SnooziContract.videos.CONTENT_URI, id), values,null,null);
						}
					}
				}
				catch (Exception e) {
					SnooziUtility.trace(context, TRACETYPE.ERROR,"VideoDownloadReceiver.onReceive Exception :  " +  e.toString());

				}finally{
					if(cursor != null)
						cursor.close();
				}

				//ImageView view = (ImageView) findViewById(R.id.imageView1);
				//String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				//view.setImageURI(Uri.parse(uriString));

			}
			
			c.close();
		}
	}

	/**
	 * Launch all pending/error download
	 * @param context
	 */
	@SuppressLint("NewApi")
	public static void launchingPendingDownload(Context context)
	{
		Cursor cursor = null;
		ContentResolver provider = context.getContentResolver();
		DownloadManager dm = (DownloadManager)  context.getSystemService(Context.DOWNLOAD_SERVICE);


		try 
		{
			cursor = provider.query(SnooziContract.videos.CONTENT_URI, SnooziContract.videos.PROJECTION_ALL, SnooziContract.videos.Columns.FILESTATUS + " LIKE ?", new String[]{"PENDING"},  null);
			if (cursor.moveToFirst()) 
			{
				//for each video with filestatus PENDING
				do{
					try 
					{
						long filedownloadid = 0l;


						int id = cursor.getInt(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns._ID));
						String url = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.URL));
						String localUrl = cursor.getString(cursor.getColumnIndexOrThrow(SnooziContract.videos.Columns.LOCALURL));

						ContentValues values = new ContentValues();
						values.put(SnooziContract.videos.Columns.FILESTATUS, "RUNNING" );
						//We launch a DownloadManager
						try{

							Request request = new Request(Uri.parse("https://storage.googleapis.com/wakeupvideos/"+url));
							request.setAllowedOverRoaming(false);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
								request.setAllowedOverMetered(false); //TODO : mettre un param de config qui permet de demander si la personne souhaite de download via son forfait
							//Uri dest = Uri.parse(localUrl);
							if(!SnooziUtility.DEV_MODE)
							{
								//If in dev mode, we want to see downloading file.
								request.setVisibleInDownloadsUi(false);
								request.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
							}
							request.setDestinationUri(Uri.parse(localUrl));
							//request.setDestinationUri(this.getContext(), Environment.DIRECTORY_MOVIES,video.getId() + ".mp4");
							filedownloadid = dm.enqueue(request);
							values.put(SnooziContract.videos.Columns.DOWNLOADID, filedownloadid );
							SnooziUtility.trace(context,TRACETYPE.DEBUG,"  Downloading Video " + id + " from " + url + " to " + localUrl);
						}catch (Exception e) {
							SnooziUtility.trace(context,TRACETYPE.ERROR,"launchingPendingDownload Error Video download :  " +  e.toString());
							values.put(SnooziContract.videos.Columns.FILESTATUS,"ERROR" );
						}
						//And update the line
						int updatecount = provider.update(ContentUris.withAppendedId(SnooziContract.videos.CONTENT_URI, id), values,null,null);
						if(updatecount == 0)
							SnooziUtility.trace(context,TRACETYPE.ERROR,"launchingPendingDownload Problem in updating FileStatus");
						

					}catch (Exception e) {
						SnooziUtility.trace(context,TRACETYPE.ERROR,"launchingPendingDownload cursor ERROR :  " +  e.toString());
					}

				} while (cursor.moveToNext());
			}


		}catch (Exception e) {
			SnooziUtility.trace(context,TRACETYPE.ERROR,"launchingPendingDownload ERROR :  " +  e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}





		//ContentValues values = new ContentValues();
		//values.put(SnooziContract.trackingevents.Columns.TYPE,this.m_type );
		//values.put(SnooziContract.trackingevents.Columns.DESCRIPTION,this.m_description );
		//values.put(SnooziContract.trackingevents.Columns.TIMESTAMP,this.m_timestamp );
		//values.put(SnooziContract.trackingevents.Columns.TIMESTRING,this.m_timestring );
		//values.put(SnooziContract.trackingevents.Columns.VIDEOID,this.m_videoid );
		/*
		Uri theResult = resolver.insert(SnooziContract.trackingevents.CONTENT_URI, values);


			Request request = new Request(Uri.parse("https://storage.googleapis.com/wakeupvideos/"+video.getUrl()));
			request.setAllowedOverRoaming(false);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			    request.setAllowedOverMetered(false); //TODO : mettre un param de config qui permet de demander si la personne souhaite de download via son forfait
			//request.setVisibleInDownloadsUi(false);
			//Uri dest = Uri.parse(localUrl);
			//request.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
			//request.setDestinationUri(dest);
			request.setDestinationInExternalFilesDir(this.getContext(), Environment.DIRECTORY_MOVIES,video.getId() + ".mp4");
			filedownloadid = dm.enqueue(request);
			SnooziUtility.trace(context,TRACETYPE.DEBUG,"  Downloading Video " + video.getId() + " from " + video.getUrl() + " to " + localUrl);

		}catch (Exception e) {
			SnooziUtility.trace(context,TRACETYPE.ERROR,"GetRecentVideo Error Video download :  " +  e.toString());
			values.put(SnooziContract.videos.Columns.FILESTATUS,"ERROR" );
			localUrl = "";
		}
		values.put(SnooziContract.videos.Columns.DOWNLOADID,filedownloadid );
		SnooziUtility.trace(context,TRACETYPE.INFO,"Video " + video.getId() + " inserted : " + theResult.toString() + " with download id : "+ filedownloadid);
		 */
	}

}
