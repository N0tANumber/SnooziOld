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

public class MyAlarmAdapter extends ArrayAdapter<MyAlarm> {

	private final Activity mactivity;
	private  List<MyAlarm> alarmlist;
	private OnCheckedChangeListener activateListener;
	private OnTouchListener touchlistener;
	//private int viewwidth;

	
	static class ViewHolder {
		//public LinearLayout mainLayout;
		public TextView txtTime;
		public TextView txtDay;
		public CheckBox chkActiv;
	}

	public MyAlarmAdapter( Activity activity, List<MyAlarm> names) {
		super(activity, R.layout.row_alarm, names);
		this.mactivity = activity;
		this.alarmlist = names;

		
		this.activateListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				try {
					MyAlarm alrm = alarmlist.get((Integer) buttonView.getTag());
					if(alrm.getActivate() != isChecked)
					{
						alrm.setActivate(isChecked);
						alrm.saveAndSync();
						alrm.checkAndPlanify(mactivity);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};


	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = mactivity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.row_alarm, null);
			//rowView.setLayoutParams(new ListView.LayoutParams(viewwidth*2, rowView.getHeight())); // We must use the parent LayoutParams object type ( rowView has ListView has parent)

			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			//viewHolder.mainLayout = (LinearLayout) rowView.findViewById(R.id.linearbloc);

			viewHolder.txtTime = (TextView) rowView.findViewById(R.id.TxtTime);
			viewHolder.txtDay = (TextView) rowView.findViewById(R.id.Txtdays);
			viewHolder.chkActiv = (CheckBox) rowView.findViewById(R.id.checkBoxActiv);
			viewHolder.chkActiv.setOnCheckedChangeListener(activateListener);
			//viewHolder.delButton.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));

			//viewHolder.mainLayout.setLayoutParams(new LinearLayout.LayoutParams(viewwidth,viewHolder.mainLayout.getHeight()));
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();

		if(this.touchlistener != null)
			rowView.setOnTouchListener(this.touchlistener);

		MyAlarm alrm = alarmlist.get(position);
		holder.txtTime.setText(alrm.toTime());
		holder.txtDay.setText(alrm.getDayString());
		holder.chkActiv.setTag(position);
		holder.chkActiv.setChecked(alrm.getActivate());

		return rowView;
	}

} 