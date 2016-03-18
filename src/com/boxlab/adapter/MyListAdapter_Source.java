package com.boxlab.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boxlab.bean.Sensor;
import com.boxlab.bean.SourceBean;
import com.boxlab.platform.R;
import com.boxlab.utils.StringUtil;

// ◊‘∂®“ÂListView  ≈‰∆˜
public class MyListAdapter_Source extends ArrayAdapter<Sensor> {
	private LayoutInflater mInflater;
	int mResourceId;

	public MyListAdapter_Source(Context context, int textViewResourceId,List<Sensor> sensors) {
		super(context, textViewResourceId,sensors);
		mResourceId = textViewResourceId;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Sensor sensor=getItem(position);
		ViewHolder holder = null;
		LinearLayout userListItem = new LinearLayout(getContext());
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.activity_source_item,
					userListItem, true);
			holder.id = (TextView) convertView.findViewById(R.id.textView1);
			holder.C_NA = (TextView) convertView.findViewById(R.id.textView2);
			holder.SENSOR_TYPE = (TextView) convertView
					.findViewById(R.id.textView3);
			holder.SENSOR_DATA = (TextView) convertView
					.findViewById(R.id.textView4);
			holder.TIMESTAMP = (TextView) convertView
					.findViewById(R.id.textView5);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.id.setText(1 + position + "");
		holder.C_NA.setText(StringUtil.getHexStringFormatShort(sensor.sBean.iCna) );
		holder.SENSOR_TYPE.setText(sensor.sSensorType);
		holder.SENSOR_DATA.setText(sensor.sSensorData.replaceFirst("\n", ""));
		holder.TIMESTAMP.setText(sensor.sBean.getsTimeStamp());
		return convertView;
	}

	public final class ViewHolder {
		public TextView id;
		public TextView C_NA;
		public TextView SENSOR_TYPE;
		public TextView SENSOR_DATA;
		public TextView TIMESTAMP;
	}
}