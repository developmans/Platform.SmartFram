package com.boxlab.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boxlab.bean.SourceBean;
import com.boxlab.platform.ActivitySourceManage;
import com.boxlab.platform.R;

// ◊‘∂®“ÂListView  ≈‰∆˜
public class MyListAdapter_Base extends ArrayAdapter<SourceBean>{
	private LayoutInflater mInflater;
	int mResourceId;
	private String[] region=null;

	public MyListAdapter_Base(Context context,int textViewResourceId,List<SourceBean> sourceBeans) {
		super(context, textViewResourceId,sourceBeans);
		this.mResourceId = textViewResourceId;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.region=context.getResources().getStringArray(R.array.region);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SourceBean sourceBean=getItem(position);
		ViewHolder holder = null;
		LinearLayout userListItem = new LinearLayout(getContext());
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.activity_base_item,
					userListItem, true);
			holder.select = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                    	ActivitySourceManage.checkBoxs.get(position);
                    }
                    else
                    {
                    	
                    }
                }
            });
			holder.tv_id = (TextView) convertView.findViewById(R.id.textView1);
			holder.tv_rfid = (TextView) convertView.findViewById(R.id.textView2);
			holder.tv_varieties = (TextView) convertView
					.findViewById(R.id.textView3);
			holder.tv_region = (TextView) convertView
					.findViewById(R.id.textView4);
			holder.tv_plant = (TextView) convertView
					.findViewById(R.id.textView5);
			holder.tv_harvest = (TextView) convertView
					.findViewById(R.id.textView6);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.select.setChecked(ActivitySourceManage.checkBoxs.get(position));
		holder.tv_id.setText(1 + position + "");
		holder.tv_rfid.setText(sourceBean.getsRfid());
		holder.tv_varieties.setText(sourceBean.getsVarieties());
		holder.tv_region.setText(region[sourceBean.getiRegion()]);
		holder.tv_plant.setText(sourceBean.getsPalant());
		holder.tv_harvest.setText(sourceBean.getsHarvest());
		return convertView;
	}

	public final class ViewHolder {
		public CheckBox select;
		public TextView tv_id;
		public TextView tv_rfid;
		public TextView tv_varieties;
		public TextView tv_region;
		public TextView tv_plant;
		public TextView tv_harvest;
	}

}