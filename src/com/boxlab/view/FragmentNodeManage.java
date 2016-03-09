package com.boxlab.view;

import java.util.ArrayList;

import com.boxlab.bean.Node;
import com.boxlab.bean.Sensor;
import com.boxlab.bean.ZigBee;
import com.boxlab.platform.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.AlteredCharSequence;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-10-28 下午2:52:48 
 * 类说明 
 */

public class FragmentNodeManage extends FragmentBase {

	private ListView lvNodeInfo;
	private AdapterNode mAdapter;
	
	private ArrayList<Node> listNode = new ArrayList<Node>();

	public FragmentNodeManage() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);

		mAdapter = new AdapterNode(mActivity, listNode);
		
		View rootView = inflater.inflate(R.layout.fragment_node_manage,container, false);
		
		lvNodeInfo = (ListView) rootView.findViewById(R.id.lvNodeInfo);
		lvNodeInfo.setAdapter(mAdapter);
		lvNodeInfo.setOnItemLongClickListener(mOnItemLongClickListener);
		return rootView;
	}

	private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			
			showDeleteDlg(view, position, id);
			
			return false;
		}
	};


	private DlgFragment dlg;
	
	public void showDeleteDlg(View view, int position, long id){
		
		dlg = new DlgFragment();
		
		Bundle args = new Bundle();
		args.putInt("NOED_INDEX", position);
		dlg.setArguments(args);
		dlg.show(getFragmentManager(), "DlgFragment");
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		updataListNode();
	}


	@Override
	public void notifyReciveSensor(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		super.notifyReciveSensor(index, sensor);
		Message msg;
		msg = handler.obtainMessage(1);
		msg.arg1 = index;
		msg.obj = sensor;
		handler.sendMessage(msg);
		msg = handler.obtainMessage(2);
		msg.arg1 = index;
		msg.obj = sensor;
		handler.sendMessageDelayed(msg, 500);
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			View childView;
			switch (msg.what) {
			case 1:
				childView = lvNodeInfo.getChildAt(msg.arg1);
				if(childView == null){
					updataListNode();
				}else{
					childView.setBackgroundColor(0xFF94CE1D);
				}
				break;
				
			case 2:
				childView = lvNodeInfo.getChildAt(msg.arg1);
				if(childView == null) 
					return;
		        if(msg.arg1 % 2 != 0){
		        	childView.setBackgroundColor(0xFF80C8FE);
		        }else{
		        	childView.setBackgroundColor(0xFFFFFFFF);
		        }
				break;
				
			default:
				
				break;
			}
		}
	};
	
	private void updataListNode() {
		if(listZigBee != null && listSensor != null 
				&& ( listZigBee.size() == listSensor.size())){
			int size = listZigBee.size();
			listNode = new ArrayList<Node>(size);
			for(int i = 0; i < size; i++){
				Node node = new Node(listZigBee.get(i), listSensor.get(i));
				listNode.add(node);
			}
			
			mAdapter.setList(listNode);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public class DlgFragment extends DialogFragment {
		
		int index = 0;
		View v = null;
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	
	    	index = getArguments().getInt("NOED_INDEX", 0);
	    	v = lvNodeInfo.getChildAt(index);
	    	
	    	Node node = listNode.get(index);
	    	AlertDialog.Builder dlg = new AlertDialog.Builder(mActivity);
	    	
	    	dlg.setTitle("您确定要删除该节点？")
	    	   .setMessage("节点地址：" + node.mZigBee.sC_NA + "\n传感器类型：" + node.mSensor.sSensorType)
	    	   .setPositiveButton("删除", mDlgOnClickListener)
	    	   .setNegativeButton("取消", mDlgOnClickListener);
	    	
	        return dlg.create();
	    }
	    
	    public void set(View v, int id) {
			this.v = v;
			this.index = id;
		}
	}
	
	DialogInterface.OnClickListener mDlgOnClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which == DialogInterface.BUTTON_POSITIVE){
		    	Node node = listNode.get(dlg.index);
				mServiceInFragment.deleteNode(dlg.index, node.mZigBee.nBean.iCna);
				mAdapter.notifyDataSetChanged();
			}
			
		}
	};
}
