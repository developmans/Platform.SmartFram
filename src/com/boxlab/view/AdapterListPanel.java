package com.boxlab.view;

import java.util.List;

import com.boxlab.platform.R;
import com.boxlab.view.PanelContent;
import com.boxlab.view.PanelContent.PanelItem;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * @author .Next E-mail: 
 * @version ����ʱ�䣺2015-4-23 ����4:40:41 
 * ��˵�� 
 */

public class AdapterListPanel extends AdapterBase<PanelItem> {
	
	//private final static String TAG = "AdapterListPanel";
	
	public AdapterListPanel(Context pContext, List<PanelItem> pList) {
		super(pContext, pList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = getLayoutInflater().inflate(R.layout.lv_mune_panel_item, null);
		}
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.ivPanel_item_icon);        //����ÿ����Ŀ��ͼ�� 
		icon.setBackgroundResource(((PanelItem)mList.get(position)).drawableResourceID); 
		
		TextView text = (TextView) convertView.findViewById(R.id.tvPanel_item_text);          //������Ŀ������˵��  
        text.setText(((PanelItem)mList.get(position)).toString());  
        
        return convertView;
	}

}
