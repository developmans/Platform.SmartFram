package com.boxlab.view;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boxlab.bean.MsgEntity;
import com.boxlab.platform.R;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-11-9 下午6:44:26 
 * 类说明 
 */

public class AdapterMsg extends AdapterBase<MsgEntity> {

	//private static final String TAG = "AdapterMsg";

	public AdapterMsg(Context pContext, List<MsgEntity> pList) {
		super(pContext, pList);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		MsgEntity entity = mList.get(position);
		boolean isComMsg = (entity.iMsgType == MsgEntity.MSG_TYPE_RECV);

		//Log.e(TAG, "position : " + position + ", isComMsg : " + isComMsg +", entity" + entity.sContent);
		ViewHolder viewHolder = null;
		
		if (convertView == null) {
			if (isComMsg) {
				convertView = mLayoutInflater.inflate(R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = mLayoutInflater.inflate(R.layout.chatting_item_msg_text_right, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.tvMsgTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvAdminName = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.isComMsg = isComMsg;
			convertView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvMsgTime.setText(entity.sMsgTime);
		if (isComMsg) {
			viewHolder.tvAdminName.setText(entity.admin.name);
		}else{
			viewHolder.tvAdminName.setText(MsgEntity.MSG_MY_NAME);
		}
		viewHolder.tvContent.setText(entity.sContent);
		
		return convertView;
	}

	/**
	 * 得到Item的类型，是对方发过来的消息，还是自己发送出去的
	 */
	public int getItemViewType(int position) {
		MsgEntity entity = mList.get(position);
		return entity.iMsgType;
	}
	
	static class ViewHolder {
		public TextView tvMsgTime;
		public TextView tvAdminName;
		public TextView tvContent;
		public boolean isComMsg;
	}
}
