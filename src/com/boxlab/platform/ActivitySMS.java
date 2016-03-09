package com.boxlab.platform;

import java.util.ArrayList;

import com.boxlab.bean.AdminEntity;
import com.boxlab.bean.MsgEntity;
import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.platform.ServiceProxy.MsgCallback;
import com.boxlab.presenter.NodePresenter;
import com.boxlab.platform.R;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.view.AdapterAdmin;
import com.boxlab.view.AdapterMsg;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-9-14 ����3:58:34 
 * ��˵�� 
 */

public class ActivitySMS extends Activity implements OnClickListener, OnItemLongClickListener,
                                               IListenerSensor, MsgCallback{

	protected static final String TAG = "ActivitySMS";
	
	NodePresenter mPresenter;

	private ImageButton imAddAdmin;
	
	private ListView lvAdmin;
	private ListView lvMsgDialog;

	private AdapterAdmin adapterAdmin;
	private ArrayList<AdminEntity> mAdminList;

	private AdapterMsg adapterMsg;
	private ArrayList<MsgEntity> mMsgList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		
		imAddAdmin = (ImageButton) findViewById(R.id.imAddAdmin);
		lvAdmin = (ListView) findViewById(R.id.lvAdmin);
		lvMsgDialog = (ListView) findViewById(R.id.lvSmsDialog);
		
		imAddAdmin.setOnClickListener(this);
		
		lvAdmin.setOnItemLongClickListener(this);

		initDlg();
	}

	private void initAdminListView() {
        mAdminList = mService.getAdminList();
		adapterAdmin = new AdapterAdmin(ActivitySMS.this, mAdminList);
		lvAdmin.setAdapter(adapterAdmin);
	}

	private void initMsgListView() {
		mMsgList = mService.getMsgList();
		adapterMsg = new AdapterMsg(ActivitySMS.this, mMsgList);
		lvMsgDialog.setAdapter(adapterMsg);
	}

	@Override
	public void onReciverMsg(MsgEntity msg, boolean isUiThread) {
		// TODO Auto-generated method stub
		Message msgHandler = mHandler.obtainMessage(2);
		msgHandler.obj = msg;
		mHandler.sendMessage(msgHandler);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// �󶨷���
		if(!mBound){
			Intent intent = new Intent(ActivitySMS.this, ServiceProxy.class);
			bindService(intent, mConnection, BIND_AUTO_CREATE);
    	}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mBound){
	    	// ȡ����
	    	mService.unregisterView(ActivitySMS.this);
	    	mService.unregistMsgCallback();
	    	unbindService(mConnection);
    	}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.imAddAdmin:
			showAddAdminDlg();
			break;

		case R.id.ibAdd:
			addAdmin();
			break;
		default:
			break;
		}
	}

	private Dialog mDlg;
	private EditText etName;
	private EditText etPhone;
	private CheckBox cbPermission1;
	private CheckBox cbPermission2;
	private CheckBox cbPermission3;
	private CheckBox cbPermission4;
	private ImageButton ibAdd;
	
	/* ��ʼ���Ի��� */
	private void initDlg() {
		mDlg = new Dialog(this, R.style.settingDlg);
		mDlg.setContentView(R.layout.dlg_add_admin);
		mDlg.setCanceledOnTouchOutside(true); // ���õ��Dialog�ⲿ��������ر�Dialog	
		
		etName = (EditText) (mDlg.findViewById(R.id.etName));
		etPhone = (EditText) (mDlg.findViewById(R.id.etPhone));
		cbPermission1 = (CheckBox) (mDlg.findViewById(R.id.cbPermission1));
		cbPermission2 = (CheckBox) (mDlg.findViewById(R.id.cbPermission2));
		cbPermission3 = (CheckBox) (mDlg.findViewById(R.id.cbPermission3));
		cbPermission4 = (CheckBox) (mDlg.findViewById(R.id.cbPermission4));
		ibAdd = (ImageButton) (mDlg.findViewById(R.id.ibAdd));
		ibAdd.setOnClickListener(this);
	}
	
    private void showAddAdminDlg() {
    	
    	if (mDlg != null){
			
			// ��ȡ��Ļ�ĸ߿�
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int cxScreen = dm.widthPixels;
			int cyScreen = dm.heightPixels;
			
			Rect rect = new Rect();
			imAddAdmin.getGlobalVisibleRect(rect);
			Log.w(TAG, rect.toString());

			// ��ȡ��mDlg�����ĵ�ǰ���ڵ����ԣ��Ӷ�����������Ļ����ʾ��λ��
			Window window = mDlg.getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			/* �Ի���Ĭ��λ������Ļ����,��������Ϊ���Ͻǣ����x,y��ʾ�˿ؼ���"��Ļ���Ͻ�"��ƫ���� */
			window.setGravity(Gravity.RIGHT | Gravity.TOP);
			params.x = (rect.right - rect.left) / 2 - 5;
			params.y = rect.bottom;
			// width,height��ʾmLoginingDlg��ʵ�ʴ�С			
			params.width = cxScreen / 2;
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			
			mDlg.show();
		}
	}

	private void addAdmin() {
		AdminEntity admin = new AdminEntity();
		admin.id = mAdminList.size();
		admin.name = etName.getText().toString().trim();
		admin.phone = etPhone.getText().toString().trim();
		admin.permission = 0;
		if(cbPermission1.isChecked())
			admin.permission |= AdminEntity.PERMSSION_RECV_NOTIFY;
		if(cbPermission2.isChecked())
			admin.permission |= AdminEntity.PERMSSION_READ_SENSOR;
		if(cbPermission3.isChecked())
			admin.permission |= AdminEntity.PERMSSION_SEND_CMD;
		if(cbPermission4.isChecked())
			admin.permission |= AdminEntity.PERMSSION_EDIT_PARM;
		
		if(mBound && mService != null){
			mService.addAdmin(admin);
			adapterAdmin.notifyDataSetChanged();
		}

    	if (mDlg != null && mDlg.isShowing()){
    		mDlg.dismiss();
    	}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if(parent == lvAdmin){
			showDeleteDlg(position);
		}
		return false;
	}

	private void showDeleteDlg(final int position) {
		// TODO Auto-generated method stub 
		AdminEntity admin = mAdminList.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ȷ��Ҫɾ���ù���Ա��");
		builder.setMessage("���ƣ�" + admin.name + "\n�ֻ����룺" + admin.phone);
		builder.setCancelable(false);
		builder.setNegativeButton("ȡ��", null);
		builder.setNeutralButton("ɾ��", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mBound && mService != null){
					mService.removeAdmin(position);
					adapterAdmin.notifyDataSetChanged();
				}
			}
			
		});
        AlertDialog deleteDlg = builder.create();
        deleteDlg.show();
	}

	private ServiceProxy mService;
	protected boolean mBound;
	
	protected ServiceConnection mConnection = new ServiceConnection() {

		@Override  
        public void onServiceDisconnected(ComponentName name) {  
	    	Log.i(TAG, "onServiceDisconnected()");	
			mBound = false;  
			
			mAdminList = null;
			mMsgList = null;
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {
	    	Log.i(TAG, "onServiceConnected()");	
            
        	// ����һ��Service����  
            mService = ((ServiceProxy.LocalBinder)service).getService();  
              
            // ע��ص��ӿ�
            mService.registerView(ActivitySMS.this);

            // ��ʼ������Ա�б�
            initAdminListView();
            
    		// ��ʼ�����ŶԻ���
            initMsgListView();
			
            // ע����Żص�
            mService.registMsgCallback(ActivitySMS.this);
            
            mBound = true;
        }

    };

	
	@Override
	public void onInitZigBeeBeans(ArrayList<ZigBeeBean> arrNetInfo) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onInitNetInfos()");
	}

	@Override
	public void onInitSensorBeans(ArrayList<SensorBean> arrSensorInfo) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onInitSensorInfos()");
	}

	@Override
	public void onInitSensors(ArrayList<Sensor> mArrSensor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitZigBees(ArrayList<ZigBee> zigbees) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSensorSettingReportCallback(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void notifyReciveSensor(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		
		Message msg = mHandler.obtainMessage(1);
		msg.obj = sensor;
		mHandler.sendMessage(msg);
		
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 1:
				Sensor sensor = (Sensor) msg.obj;
				showServiceLog(sensor);
				break;

			case 2:
				mMsgList.add((MsgEntity)msg.obj);
				adapterMsg.notifyDataSetChanged();
				break;
				
			default:
				break;
			}
		}
	};

	private void showServiceLog(Sensor sensor) {
	}

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onErr(int errReason, String errTips) {
		// TODO Auto-generated method stub
		
	}
}
