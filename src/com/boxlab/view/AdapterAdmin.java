package com.boxlab.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boxlab.bean.AdminEntity;
import com.boxlab.platform.R;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-11-9 下午4:42:41 
 * 类说明 
 */

public class AdapterAdmin extends AdapterBase<AdminEntity> {
	
	class ViewHolder{
        TextView No;  
        TextView tvName;
        TextView tvPhone;
        TextView tvPermission;
	}

	public AdapterAdmin(Context pContext, List<AdminEntity> pList) {
		super(pContext, pList);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.lv_admin_item, null);  
            holder = new ViewHolder();
            holder.No = (TextView) convertView.findViewById(R.id.tvNoItem);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvNameItem);
            holder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhoneItem);
            holder.tvPermission = (TextView) convertView.findViewById(R.id.tvPermissionItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();  
        }
        
        AdminEntity a = mList.get(position);
        
        if(a != null){
            holder.No.setText(String.valueOf(a.id));
            holder.tvName.setText(a.name);
            holder.tvPhone.setText(a.phone);
            if(a.permission > 0){
                holder.tvPermission.setText("");
                String perm = "";
            	if((a.permission & AdminEntity.PERMSSION_RECV_NOTIFY) > 0)
            		perm += AdminEntity.PERMISSIONS[0];
            	if((a.permission & AdminEntity.PERMSSION_READ_SENSOR) > 0)
            		perm += AdminEntity.PERMISSIONS[1];
            	if((a.permission & AdminEntity.PERMSSION_SEND_CMD) > 0)
            		perm += AdminEntity.PERMISSIONS[2];
            	if((a.permission & AdminEntity.PERMSSION_EDIT_PARM) > 0)
            		perm += AdminEntity.PERMISSIONS[3];
                holder.tvPermission.setText(perm);
            }else{
                holder.tvPermission.setText("无");
            }
        }

        if(position % 2 != 0){
        	convertView.setBackgroundColor(0xFF80C8FE);
        }else{
        	convertView.setBackgroundColor(0xFFFFFFFF);
        }
        
		return convertView;
	}

}
