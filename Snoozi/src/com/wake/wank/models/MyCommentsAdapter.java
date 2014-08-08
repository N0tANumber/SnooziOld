package com.wake.wank.models;

import java.util.List;

import com.wake.wank.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MyCommentsAdapter extends ArrayAdapter<MyComments> {

	private final Activity mactivity;
	private  List<MyComments> commentlist;
	private OnTouchListener touchlistener;
	//private int viewwidth;

	
	static class ViewHolder {
		//public LinearLayout mainLayout;
		public TextView txtPseudo;
		public TextView txtComment;
	}

	public MyCommentsAdapter( Activity activity, List<MyComments> names) {
		super(activity, R.layout.row_comment, names);
		this.mactivity = activity;
		this.commentlist = names;
		


	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = mactivity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.row_comment, null);
			
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.txtPseudo = (TextView) rowView.findViewById(R.id.TxtPseudo);
			viewHolder.txtComment = (TextView) rowView.findViewById(R.id.TxtComments);
			
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();

		
		MyComments comm = commentlist.get(position);
		holder.txtPseudo.setText(comm.getUserpseudo() + " :");
		holder.txtComment.setText(comm.getDescription());
		
		return rowView;
	}

} 