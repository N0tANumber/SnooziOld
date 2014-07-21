package com.wake.wank.models;

import java.util.List;

import com.wake.wank.R;
import com.wake.wank.utils.SnooziUtility;
import com.wake.wank.utils.SnooziUtility.TRACETYPE;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyVideoAdapter extends ArrayAdapter<MyVideo> {

	private final Activity mactivity;
	private  List<MyVideo> videolist;
	private OnTouchListener touchlistener;
	//private int viewwidth;

	
	static class ViewHolder {
		//public LinearLayout mainLayout;
		public TextView txtLike;
		public TextView txtView;
		public ImageView imgThumb;
		public Bitmap preview;
	}

	public MyVideoAdapter( Activity activity, List<MyVideo> names) {
		super(activity, R.layout.row_video, names);
		this.mactivity = activity;
		this.videolist = names;
		this.setNotifyOnChange(false);
		
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		MyVideo alrm = videolist.get(position);
		
		
		
		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = mactivity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.row_video, null);
			//rowView.setLayoutParams(new ListView.LayoutParams(viewwidth*2, rowView.getHeight())); // We must use the parent LayoutParams object type ( rowView has ListView has parent)

			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			//viewHolder.mainLayout = (LinearLayout) rowView.findViewById(R.id.linearbloc);

			viewHolder.txtLike = (TextView) rowView.findViewById(R.id.txtLikeCount);
			viewHolder.txtView = (TextView) rowView.findViewById(R.id.txtViewCount);
			viewHolder.imgThumb = (ImageView) rowView.findViewById(R.id.thumbView);
			//viewHolder.delButton.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));

			//viewHolder.mainLayout.setLayoutParams(new LinearLayout.LayoutParams(viewwidth,viewHolder.mainLayout.getHeight()));
			rowView.setTag(viewHolder);
			
			
					
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();

		if(this.touchlistener != null)
			rowView.setOnTouchListener(this.touchlistener);

		holder.txtLike.setText("" + alrm.getLike());
		holder.txtView.setText("" + alrm.getViewcount());
		Bitmap bMap = null;
		//SnooziUtility.trace(TRACETYPE.INFO, "1 ");
		try {
			Uri uri = Uri.parse(alrm.getLocalurl());
			 bMap = ThumbnailUtils.createVideoThumbnail(uri.getPath() , MediaStore.Video.Thumbnails.MINI_KIND);
			if(bMap != null)
				holder.preview = bMap;
		} catch (Exception e) {
			bMap = null;
		}
		//SnooziUtility.trace(TRACETYPE.INFO, "2 ");
		
		if(holder.preview != null)
			holder.imgThumb.setImageBitmap(holder.preview);

		return rowView;
	}
	
	

} 