package com.boxlab.platform;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.boxlab.bean.Sensor;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.platform.R;
import com.boxlab.utils.DataResolverUtil;
import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.StringUtil;
import com.boxlab.view.FragmentAutoCtrl;
import com.boxlab.view.FragmentChart;
import com.boxlab.view.FragmentManCtrl;
import com.boxlab.view.FragmentNodeManage;
import com.boxlab.view.PanelContent;
import com.boxlab.view.SensorViewBase;
import com.boxlab.view.AdapterListPanel;
import com.boxlab.view.FragmentBase.FragmentBaseCallbacks;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-18 下午7:46:30 
 * 类说明 
 */

public class ActivitySmartFram extends ActivityBase implements IListenerSensor, FragmentBaseCallbacks {

	private static final String TAG = "ActivitySmartFram";
	
	private ImageButton ibFramSetting;
	
	private SensorViewBase svHeater;
	private SensorViewBase svThTransmitterRS485;
	private SensorViewBase svLight;
	private SensorViewBase svBH1750FVI;
	private SensorViewBase svWaterPump;
	private SensorViewBase svSoil;
	private SensorViewBase svCO2;
	private SensorViewBase svFan;
	private SensorViewBase svRollBlind;
	
	private SensorViewBase[] listSensorView;
	
	private ListView lvSettingMenu;
	private AdapterListPanel mAdapterListPanel;
	private FragmentManager fragmentManager;
	private FrameLayout flContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_fram_9in);
		
		svHeater = (SensorViewBase) findViewById(R.id.svHeater);
		svThTransmitterRS485 = (SensorViewBase) findViewById(R.id.svThTransmitterRS485);
		
		svLight = (SensorViewBase) findViewById(R.id.svLight);
		svBH1750FVI = (SensorViewBase) findViewById(R.id.svBH1750FVI);
		
		svWaterPump = (SensorViewBase) findViewById(R.id.svWaterPump);
		svSoil = (SensorViewBase) findViewById(R.id.svSoil);

		svCO2 = (SensorViewBase) findViewById(R.id.svCO2);
		svFan = (SensorViewBase) findViewById(R.id.svFan);

		svRollBlind = (SensorViewBase) findViewById(R.id.svRollBlind);
		
		listSensorView = new SensorViewBase[]{
					svHeater, svThTransmitterRS485, 
					svLight, svBH1750FVI,
		            svWaterPump, svSoil, 
		            svCO2, svFan,
		            svRollBlind };
		
		svHeater.setOnClickListener(mOnClickListener);
		svLight.setOnClickListener(mOnClickListener);
		svWaterPump.setOnClickListener(mOnClickListener);
		svFan.setOnClickListener(mOnClickListener);

		ibFramSetting = (ImageButton) findViewById(R.id.ibFramSetting);
		ibFramSetting.setOnClickListener(mOnClickListener);
		
		initDlg();
		
		lvSettingMenu = (ListView) findViewById(R.id.lvSettingMenu);
		mAdapterListPanel = new AdapterListPanel(this, PanelContent.PANEL_ITEMS);
		lvSettingMenu.setAdapter(mAdapterListPanel);
		lvSettingMenu.setOnItemClickListener(mOnItemClickListener);
		lvSettingMenu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		fragmentManager = getFragmentManager();
		flContainer = (FrameLayout) findViewById(R.id.node_detail_container);
		
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Sensor sensor = ((SensorViewBase) v).getBoundSensor();
			if(sensor == null)
				return;
			
			ViewGroup parent = null;
			switch (v.getId()) {
			case R.id.svHeater:
				parent = svHeater;
				break;
				
			case R.id.svLight:
				parent = svLight;
				break;
				
			case R.id.svFan:
				parent = svFan;
				break;

			case R.id.svWaterPump:
				parent = svWaterPump;
				break;

			default:
				break;
			}
			if(parent == null)
				return;
			
			if(mDlg != null && mDlg.isShowing()){
				closeDlg();
			}else {
				showDlg(sensor, parent);
			}
			
//			Sensor sensor = null;
//			ArrayList<Sensor> arr = null;
//			if(v.getTag() instanceof Sensor){
//				sensor = (Sensor) v.getTag();
//			}else if(v.getTag() instanceof ArrayList<?>){
//				arr = (ArrayList<Sensor>) v.getTag();
//			}
//			switch (v.getId()) {
//			case R.id.svHeater:
//				if (mDlg != null && mDlg.isShowing()){
//					closeDlg();
//				}else {
//					showDlg(sensor, svHeater);
//				}
//				break;
//				
//			case R.id.svLight:
//				if(arr != null){
//					showSensorConflict((SensorViewBase) v, FrameUtil.SENSOR_TYPE_LIGHT, arr);
//					return;
//				}
//				if (mDlg != null && mDlg.isShowing()){
//					closeDlg();
//				}else {
//					showDlg(sensor, svLight);
//				}
//				break;
//				
//			case R.id.svFan:
//				if(arr != null){
//					showSensorConflict((SensorViewBase) v, FrameUtil.SENSOR_TYPE_FAN, arr);
//					return;
//				}
//				
//				if (mDlg != null && mDlg.isShowing()){
//					closeDlg();
//				}else {
//					showDlg(sensor, svFan);
//				}
//				break;
//
//			case R.id.svWaterPump:
//				if (mDlg != null && mDlg.isShowing()){
//					closeDlg();
//				}else {
//					showDlg(sensor, svWaterPump);
//				}
//				break;
//				
//			case R.id.ibFramSetting:
//				
//				break;
//
//			default:
//				break;
//			}
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		for (int i = 0; i < listSensorView.length; i++) {
			if (listSensorView[i] != null) {
				listSensorView[i].onSensorViewDestory();
			}
		}
	}

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stu
		for (int i = 0; i < listSensorView.length; i++) {
			if (listSensorView[i] != null) {
				listSensorView[i].setPlatformType(SharedPreferencesUtil.TYPE_SMART_FRAM);
			}
		}
	}
	
	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub
		
	}
    
	@Override
	public void onInitSensors(ArrayList<Sensor> sensors) {
		super.onInitSensors(sensors);
		
		//sensorBindSensorView(listSensor);
		
		restoreState(listSensor);
	}

	
	private void restoreState(ArrayList<Sensor> sensorList) {
		// TODO Auto-generated method stub
		for(int i = 0; i < listSensorView.length; i++){
			if(listSensorView[i] != null)
				listSensorView[i].onSensorViewRestore(sensorList);
		}
	}
//	
//	private void sensorBindSensorView(ArrayList<Sensor> sensorList) {
//		
//		ArrayList<Sensor> sensorConflict ;
//		Sensor sensor;
//		int index = -1;
//		
//		index = getIndexOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_SOIL);
//		if(index >= 0){
//			sensor = sensorList.get(index);
//			svSoil.setBindSensor(sensor);
//			svSoil.setTag(sensor);
//		}
//		index = getIndexOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_HEATER);
//		if(index >= 0){
//			sensor = sensorList.get(index);
//			svHeater.setBindSensor(sensor);
//			svHeater.setTag(sensor);
//		}
//		// 冲突
//		sensorConflict = getSensorsOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_FAN);
//		if(sensorConflict.size() > 1){
//			svFan.setTag(sensorConflict);
//			//svFan.setSaveSensorData(SharedPreferencesUtil.restoreFanAir());
//		}else{
//			index = getIndexOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_FAN);
//			if(index >= 0){
//				sensor = sensorList.get(index);
//				svFan.setBindSensor(sensor);
//				svFan.setTag(sensor);
//			}
//		}
//		index = getIndexOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_WATER_PUMP);
//		if(index >= 0){
//			sensor = sensorList.get(index);
//			svWaterPump.setBindSensor(sensor);
//			svWaterPump.setTag(sensorList.get(index));
//		}
//		// 冲突
//		sensorConflict = getSensorsOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_BH1750FVI);
//		if(sensorConflict.size() > 1){
//			svBH1750FVI.setTag(sensorConflict);
//			//svBH1750FVI.setSaveSensorData(SharedPreferencesUtil.restoreLightFram());
//		}else{
//			index = getIndexOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_BH1750FVI);
//			if(index >= 0){
//				sensor = sensorList.get(index);
//				svBH1750FVI.setBindSensor(sensor);
//				svBH1750FVI.setTag(sensorList.get(index));
//			}
//		}
//		// 冲突
//		sensorConflict = getSensorsOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_LIGHT);
//		if(sensorConflict.size() > 1){
//			svLight.setTag(sensorConflict);
//		}else{
//			index = getIndexOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_LIGHT);
//			if(index >= 0){
//				sensor = sensorList.get(index);
//				svLight.setBindSensor(sensor);
//				svLight.setTag(sensorList.get(index));
//			}
//		}
//		index = getIndexOnArrSensor(sensorList,FrameUtil.SENSOR_TYPE_PIN_IO);
//		if(index >= 0){
//			sensor = sensorList.get(index);
//			svRollBlind.setBindSensor(sensor);
//			svRollBlind.setTag(sensorList.get(index));
//		}
//	}
//
//	private ArrayList<Sensor> getSensorsOnArrSensor(ArrayList<Sensor> sensorList, String sensorType) {
//		ArrayList<Sensor> sensorSpec = new ArrayList<Sensor>();
//		Sensor sensor;
//    	for(int i = 0; i < sensorList.size(); i++){
//			synchronized (sensor = (Sensor)sensorList.get(i)) {
//        		if(sensor.sSensorType.equals(sensorType))
//        			sensorSpec.add(sensor);
//			}
//    	}
//    	
//        return sensorSpec;
//	}
//
//	private int getIndexOnArrSensor(ArrayList<Sensor> sensorList,String sensorType) {
//    	Sensor sensor;
//    	for(int i = 0; i < sensorList.size(); i++){
//			synchronized (sensor = (Sensor)sensorList.get(i)) {
//        		if(sensor.sSensorType.equals(sensorType))
//        			return i;
//			}
//    	}
//        return -1;
//	}
	
	@Override
	public boolean filterInterested(Sensor sensor) {
		int iSensorType = sensor.sBean.iSensorType;
		if (iSensorType == FrameUtil.SENSOR_TYPE_ID_HEATER
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_WATER_PUMP
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_SOIL
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_FAN
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_CO2
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_LIGHT
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_BH1750FVI
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_PIN_IO
				) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onSensorSettingReportCallback(int index, Sensor sensor) {
		// TODO Auto-generated method stub

		Message msg = mHandler.obtainMessage(1);
		msg.obj = sensor;
		mHandler.sendMessage(msg);
		
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
				processSensor(sensor);
				break;
				
			case -1:
				String tips = (String) msg.obj;
				showToast("Err:" + msg.arg1 + "\nTips:" + tips);
				break;
				
			default:
				break;
			}
		}
	};

	private void processSensor(Sensor sensor) {
		// TODO Auto-generated method stub
		switch (sensor.sBean.iSensorType) {
		case FrameUtil.SENSOR_TYPE_ID_SOIL:
			svSoil.onNotifySensor(sensor);
			break;

		case FrameUtil.SENSOR_TYPE_ID_HEATER:
			svHeater.onNotifySensor(sensor);
			break;

		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485:
			svThTransmitterRS485.onNotifySensor(sensor);
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WATER_PUMP:
			svWaterPump.onNotifySensor(sensor);
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_BH1750FVI:
			svBH1750FVI.onNotifySensor(sensor);
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_LIGHT:
			svLight.onNotifySensor(sensor);
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_CO2:
			svCO2.onNotifySensor(sensor);
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_FAN:
			svFan.onNotifySensor(sensor);			
			break;
		case FrameUtil.SENSOR_TYPE_ID_PIN_IO:
			svRollBlind.onNotifySensor(sensor);
			break;
			
		default:
			break;
		}
	}
	
	private Dialog mDlg;
	
	/* 初始化对话框 */
	private void initDlg() {
		mDlg = new Dialog(this, R.style.settingDlg);
		mDlg.setContentView(R.layout.dlg_setting);
		mDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
		
	}
	
	/* 显示对话框 */
	private void showDlg(Sensor sensor, ViewGroup parent) {
		if (mDlg != null){
			
			// 获取屏幕的高宽
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int cxScreen = dm.widthPixels;
			int cyScreen = dm.heightPixels;
			
			Rect rect = new Rect();
			parent.getGlobalVisibleRect(rect);
			Log.w(TAG, rect.toString());

			// 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置
			Window window = mDlg.getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			/* 对话框默认位置在屏幕中心,这里设置为左上角，因此x,y表示此控件到"屏幕左上角"的偏移量 */
			window.setGravity(Gravity.LEFT | Gravity.TOP);
			// TODO 检测边界
			LinearLayout bg = (LinearLayout) (mDlg.findViewById(R.id.llDialog));
			if(rect.top > cyScreen/2){
				params.x = rect.left - 10;
				params.y = rect.top - (rect.bottom - rect.top) - 15;
				bg.setBackgroundResource(R.drawable.bg_dialog_up);
			}else{
				params.x = rect.left - 10;
				params.y = rect.bottom - 10; // -199
				bg.setBackgroundResource(R.drawable.bg_dialog_down);
			}

			params.width = rect.right - rect.left + 10 ;//cxScreen;
			params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			// width,height表示mLoginingDlg的实际大小
			
			TextView tvSensorAddr = (TextView) (mDlg.findViewById(R.id.tvSensorAddr));
			TextView tvSensorType = (TextView) (mDlg.findViewById(R.id.tvSensorType));
			TextView tvSensorPower = (TextView) (mDlg.findViewById(R.id.tvSensorPower));
			Switch sw = (Switch) (mDlg.findViewById(R.id.sw));
			
			if(sensor != null){
				sw.setTag(sensor);
				tvSensorAddr.setText(StringUtil.getHexStringFormatShort(sensor.sBean.iCna));
				Sensor.updataSensorType(sensor, sensor.sBean);
				tvSensorType.setText(sensor.sSensorType );
				Sensor.updataPower(sensor);
				tvSensorPower.setText(sensor.sPower);

				sw.setChecked(sensor.Bool);

				OnCheckedChangeListener listener = new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						Sensor sensor = (Sensor) buttonView.getTag();
						final byte[] cmd;
						if(mBound){
							if(isChecked){
								cmd = DataResolverUtil.EecodeData(sensor, DataResolverUtil.NODE_SET_RELAY, 1);
							}else{
								cmd = DataResolverUtil.EecodeData(sensor, DataResolverUtil.NODE_SET_RELAY, 0);
							}
							mService.sendCmd(cmd);
						}
					}
				};
				sw.setOnCheckedChangeListener(listener);

			}else{
				sw.setOnCheckedChangeListener(null);
				sw.setTag(null);
				tvSensorAddr.setText("未知地址");
				tvSensorType.setText("未知类型");
				tvSensorPower.setText("3.2V");
			}
			
			mDlg.show();
		}
	}

	/* 关闭对话框 */
	private void closeDlg() {
		if (mDlg != null && mDlg.isShowing())
			mDlg.dismiss();
	}
	
	synchronized private void showSensorConflict(final SensorViewBase sv, String sensorType, final ArrayList<Sensor> conflict) {

		AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySmartFram.this);
		ArrayList<String> list = new ArrayList<String>(conflict.size());
		String[] stringlist = new String[list.size()];
		list.toArray(stringlist);
		for(int i = 0; i < conflict.size(); i++){
			Sensor sensor = conflict.get(i);
			list.add(StringUtil.getHexStringFormatShort(sensor.sBean.iCna));
		}
		
		stringlist = list.toArray(stringlist);
		
		builder.setTitle("发现多个" + sensorType + "传感器,请选择正确的节点");
		builder.setItems(stringlist, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Sensor sensor = conflict.get(which);
				sv.setTag(sensor);
				sv.setBindSensor(sensor);
			}
		});

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
		
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (parent.getId()) {
			case R.id.lvSettingMenu:
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				switch (position) {
				case 0:
					FragmentManCtrl fragment0 = new FragmentManCtrl();
					transaction.replace(R.id.node_detail_container, fragment0);
					transaction.commit();
					break;
					
				case 1:
					//flContainer.setBackgroundResource(R.drawable.group_message_member_list_bg);
					FragmentAutoCtrl fragment1 = new FragmentAutoCtrl();
					transaction.replace(R.id.node_detail_container, fragment1);
					transaction.commit();
					break;
					
				case 2:
					//flContainer.setBackgroundResource(R.drawable.head_photo_bg_list);
					FragmentNodeManage fragment2 = new FragmentNodeManage();
					transaction.replace(R.id.node_detail_container, fragment2);
					transaction.commit();
					break;

				case 3:
					//flContainer.setBackgroundResource(R.drawable.head_photo_bg_list);
					FragmentChart fragment3 = new FragmentChart();
					transaction.replace(R.id.node_detail_container, fragment3);
					transaction.commit();
					break;
				default:
					break;
				}
				
				
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onFragmentBaseStateChange(Fragment fragment, int fragmentState) {
		// TODO Auto-generated method stub
		
		if(fragment instanceof FragmentNodeManage){
			switch (fragmentState) {
			case FRAGMENT_STATE_ON_ACTIVITY_CREATED:
				((FragmentNodeManage) fragment).proxyServiceConnect(mService);
				
				break;
				
			case FRAGMENT_STATE_ON_DESTORY_VIEW:
				((FragmentNodeManage) fragment).proxyServiceDisconnect();

			default:
				break;
			}
		}
		
		if(fragment instanceof FragmentManCtrl){
			switch (fragmentState) {
			case FRAGMENT_STATE_ON_ACTIVITY_CREATED:
				((FragmentManCtrl) fragment).proxyServiceConnect(mService);
				
				break;
				
			case FRAGMENT_STATE_ON_DESTORY_VIEW:
				((FragmentManCtrl) fragment).proxyServiceDisconnect();

			default:
				break;
			}
		}

		if(fragment instanceof FragmentChart){
			switch (fragmentState) {
			case FRAGMENT_STATE_ON_ACTIVITY_CREATED:
				((FragmentChart) fragment).proxyServiceConnect(mService);
				
				break;
				
			case FRAGMENT_STATE_ON_DESTORY_VIEW:
				((FragmentChart) fragment).proxyServiceDisconnect();

			default:
				break;
			}
		}
	}

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return SharedPreferencesUtil.TYPE_SMART_FRAM;
	}

	@Override
	public void onErr(int errReason, String errTips) {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage(-1);
		msg.arg1 = errReason;
		msg.obj = errTips;
		mHandler.sendMessage(msg);
	}

}
