package com.boxlab.view;

import java.util.List;

import com.boxlab.bean.Node;
import com.boxlab.platform.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-10-28 下午6:23:44 
 * 类说明 
 */

public class AdapterNode extends AdapterBase<Node> {
	
	class ViewHolder{
        TextView No;  
        TextView tvSensorType;
        TextView tvCna;
        TextView tvCIEEE;
        TextView tvPna;
        TextView tvPIEEE;
        //TextView tvTimeStamp;
	}

	public AdapterNode(Context pContext, List<Node> pList) {
		super(pContext, pList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.lv_node_item, null);  
            holder = new ViewHolder();
            holder.No = (TextView) convertView.findViewById(R.id.tvNoItem);
            holder.tvSensorType = (TextView) convertView.findViewById(R.id.tvSensorTypeItem);
            holder.tvCna = (TextView) convertView.findViewById(R.id.tvCnaItem);
            holder.tvCIEEE = (TextView) convertView.findViewById(R.id.tvCieeeItem);
            holder.tvPna = (TextView) convertView.findViewById(R.id.tvPnaItem);
            holder.tvPIEEE = (TextView) convertView.findViewById(R.id.tvPieeeItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();  
        }
        
        Node node = mList.get(position);
        
        if(node != null){
            holder.No.setText(node.mZigBee.sID);
            holder.tvSensorType.setText(node.mSensor.sSensorType);
            holder.tvCna.setText(node.mZigBee.sC_NA);
            holder.tvCIEEE.setText(node.mZigBee.sC_IEEE);
            holder.tvPna.setText(node.mZigBee.sP_NA);
            holder.tvPIEEE.setText(node.mZigBee.sP_IEEE);
        }

        if(position % 2 != 0){
        	convertView.setBackgroundColor(0xFF80C8FE);
        }else{
        	convertView.setBackgroundColor(0xFFFFFFFF);
        }
        
		return convertView;
	}
    
}
