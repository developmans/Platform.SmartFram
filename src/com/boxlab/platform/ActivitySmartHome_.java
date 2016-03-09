package com.boxlab.platform;

import java.util.ArrayList;
import java.util.Iterator;

import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.platform.R;
import com.boxlab.utils.DataResolverUtil;
import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.StringUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-18 下午2:37:44 
 * 类说明 
 */

public class ActivitySmartHome_ extends ActivityBase implements IListenerSensor {

	protected static final String TAG = "ActivitySmartHome";
	
	private TextView tvAvAnnuciatorSensorData;

	private TextView tvGasDetectorSensorData;

	private TextView tvIrDetectorSensorData;

	private TextView tvIrFenceData;

	private TextView tvDoorSensorData;

	private LinearLayout llDoor;

	private LinearLayout llAvAnnuciator;

	private TextView tvThTransmitterRS232SensorData;

	private TextView tvFanSensorData;

	private TextView tvLightSensorData;

	private TextView tvWinCurtainSensorData;

	private TextView tvBH1750FVISensorData;

	private LinearLayout llFan;

	private LinearLayout llLight;

	private LinearLayout llWinCurtain;

	private int colorSensorData;

	private int colorSensorDataAlarm;

	private TextView tvLfRfidData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_home);
		
		//
		tvAvAnnuciatorSensorData = (TextView) findViewById(R.id.tvAvAnnuciatorSensorData);
		tvGasDetectorSensorData = (TextView) findViewById(R.id.tvGasDetectorSensorData);
		tvIrDetectorSensorData = (TextView) findViewById(R.id.tvIrDetectorSensorData);
		tvIrFenceData = (TextView) findViewById(R.id.tvIrFenceData);
		tvDoorSensorData = (TextView) findViewById(R.id.tvDoorSensorData);
		
		llDoor = (LinearLayout) findViewById(R.id.llDoor);
		llAvAnnuciator = (LinearLayout) findViewById(R.id.llAvAnnuciator);
		
		tvAvAnnuciatorSensorData.setOnClickListener(mOnClickListener);
		tvDoorSensorData.setOnClickListener(mOnClickListener);
		
		//
		tvThTransmitterRS232SensorData = (TextView) findViewById(R.id.tvThTransmitterRS232SensorData);
		tvFanSensorData = (TextView) findViewById(R.id.tvFanSensorData);
		tvLightSensorData = (TextView) findViewById(R.id.tvLightSensorData);
		tvWinCurtainSensorData = (TextView) findViewById(R.id.tvWinCurtainSensorData);
		tvBH1750FVISensorData = (TextView) findViewById(R.id.tvBH1750FVISensorData);
		
		llFan = (LinearLayout) findViewById(R.id.llFan);
		llLight = (LinearLayout) findViewById(R.id.llLight);
		llWinCurtain = (LinearLayout) findViewById(R.id.llWinCurtain);

		tvFanSensorData.setOnClickListener(mOnClickListener);
		tvLightSensorData.setOnClickListener(mOnClickListener);
		tvWinCurtainSensorData.setOnClickListener(mOnClickListener);
		
		tvLfRfidData = (TextView) findViewById(R.id.tvLfRfidData);
		
		initLoginingDlg();
		
		colorSensorData = getResources().getColor(R.color.sensor_data_text);
		colorSensorDataAlarm = getResources().getColor(R.color.sensor_data_text_alarm);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		restoreState();
	}

	private void restoreState() {
		// TODO 恢复之前状态
//		tvAvAnnuciatorSensorData.setText(SharedPreferencesUtil.restoreAvAnnuciator());
//		tvGasDetectorSensorData.setText(SharedPreferencesUtil.restoreGasDetector());
//		tvIrDetectorSensorData.setText(SharedPreferencesUtil.restoreIrDetector());
//		tvIrFenceData.setText(SharedPreferencesUtil.restoreIrFence());
//		tvDoorSensorData.setText(SharedPreferencesUtil.restoreDoor());
//		tvThTransmitterRS232SensorData.setText(SharedPreferencesUtil.restoreThTransmitterRS232());
//		tvFanSensorData.setText(SharedPreferencesUtil.restoreFan());
//		tvLightSensorData.setText(SharedPreferencesUtil.restoreLight());
//		tvWinCurtainSensorData.setText(SharedPreferencesUtil.restoreWinCurtain());
//		tvBH1750FVISensorData.setText(SharedPreferencesUtil.restoreBH1750FVI());
//		tvLfRfidData.setText(SharedPreferencesUtil.restoreLfRfid());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		saveState();
	}

	private void saveState() {
		// TODO 保存传感器状态
//		SharedPreferencesUtil.saveAvAnnuciator(tvAvAnnuciatorSensorData.getText().toString());
//		SharedPreferencesUtil.saveGasDetector(tvGasDetectorSensorData.getText().toString());
//		SharedPreferencesUtil.saveIrDetector(tvIrDetectorSensorData.getText().toString());
//		SharedPreferencesUtil.saveIrFence(tvIrFenceData.getText().toString());
//		SharedPreferencesUtil.saveDoor(tvDoorSensorData.getText().toString());
//		SharedPreferencesUtil.saveThTransmitterRS232(tvThTransmitterRS232SensorData.getText().toString());
//		SharedPreferencesUtil.saveFan(tvFanSensorData.getText().toString());
//		SharedPreferencesUtil.saveLight(tvLightSensorData.getText().toString());
//		SharedPreferencesUtil.saveWinCurtain(tvWinCurtainSensorData.getText().toString());
//		SharedPreferencesUtil.saveBH1750FVI(tvBH1750FVISensorData.getText().toString());
//		SharedPreferencesUtil.saveLfRfid(tvLfRfidData.getText().toString());
	}
	
	@Override
	public void onInitSensors(ArrayList<Sensor> sensors) {
		
		super.onInitSensors(sensors);
		
		dataBind(listSensor);
	}
	
	private void dataBind(ArrayList<Sensor> arrSensor) {
		// TODO Auto-generated method stub

		final ArrayList<Sensor> sensorConflict ;
		
		int index = -1;
		
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_AV_ANNUNCIATOR);
		if(index >= 0){
			tvAvAnnuciatorSensorData.setTag(arrSensor.get(index));
		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_GAS_DETECTOR);
		if(index >= 0){
			tvGasDetectorSensorData.setTag(arrSensor.get(index));
		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_IR_DETECTOR);
		if(index >= 0){
			tvIrDetectorSensorData.setTag(arrSensor.get(index));
		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_IR_FENCE);
		if(index >= 0){
			tvIrFenceData.setTag(arrSensor.get(index));
		}
//		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_DOOR);// 门磁检测
//		if(index >= 0){
//			//tvDoorSensorData.setText(arrSensor.get(index).sSensorData);
//			//tvDoorSensorData.setTag(arrSensor.get(index));
//		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_DOOR_LOCK); // 电子门锁
		if(index >= 0){
			tvDoorSensorData.setTag(arrSensor.get(index));
		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_TH_TRANSMITTER_RS232);
		if(index >= 0){
			tvThTransmitterRS232SensorData.setTag(arrSensor.get(index));
		}
		sensorConflict = getSensorsOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_FAN);
		if(sensorConflict.size() > 1){
			Log.e(TAG, "找到多个传感器，类型：" + FrameUtil.SENSOR_TYPE_FAN);
//			mHandler.postDelayed(new Runnable() {
//				
//				@Override
//				public void run() {
//					showSensorConflict(FrameUtil.SENSOR_TYPE_FAN,sensorConflict);
//				}
//			}, 1000);
			tvFanSensorData.setTag(sensorConflict);
		}else{
			index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_FAN);
			if(index >= 0){
				tvFanSensorData.setTag(arrSensor.get(index));
			}
		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_LIGHT);
		if(index >= 0){
			tvLightSensorData.setTag(arrSensor.get(index));
		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_WIN_CURTAIN);
		if(index >= 0){
			tvWinCurtainSensorData.setTag(arrSensor.get(index));
		}
		index = getIndexOnArrSensor(arrSensor,FrameUtil.SENSOR_TYPE_BH1750FVI);
		if(index >= 0){
			tvBH1750FVISensorData.setTag(arrSensor.get(index));
		}
		
	}

	private ArrayList<Sensor> getSensorsOnArrSensor(ArrayList<Sensor> arrSensor, String sensorType) {
		ArrayList<Sensor> sensorSpec = new ArrayList<Sensor>();
		Sensor sensor;
    	for(int i = 0; i < arrSensor.size(); i++){
			synchronized (sensor = (Sensor)arrSensor.get(i)) {
        		if(sensor.sSensorType.equals(sensorType))
        			sensorSpec.add(sensor);
			}
    	}
    	
        return sensorSpec;
	}

	private int getIndexOnArrSensor(ArrayList<Sensor> arrSensor, String sensorType) {
    	Sensor sensor;
    	for(int i = 0; i < arrSensor.size(); i++){
			synchronized (sensor = (Sensor)arrSensor.get(i)) {
        		if(sensor.sSensorType.equals(sensorType))
        			return i;
			}
    	}
        return -1;
	}

	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO 定义过滤器
		int sensorType = sensor.sBean.iSensorType;
		if( sensorType == FrameUtil.SENSOR_TYPE_ID_AV_ANNUNCIATOR || 
				sensorType == FrameUtil.SENSOR_TYPE_ID_GAS_DETECTOR ||
				sensorType == FrameUtil.SENSOR_TYPE_ID_IR_DETECTOR ||
				sensorType == FrameUtil.SENSOR_TYPE_ID_IR_FENCE || 
				sensorType == FrameUtil.SENSOR_TYPE_ID_DOOR || 
				sensorType == FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK ||
				sensorType == FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS232 ||
				sensorType == FrameUtil.SENSOR_TYPE_ID_FAN ||
				sensorType == FrameUtil.SENSOR_TYPE_ID_LIGHT ||
				sensorType == FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN ||
				sensorType == FrameUtil.SENSOR_TYPE_ID_BH1750FVI || 
				sensorType == FrameUtil.SENSOR_TYPE_ID_RFID_LF){
			return true;
		}else{
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
				processServiceLog(sensor);
				break;

			default:
				break;
			}
		}

	};

	protected void processServiceLog(Sensor sensor) {
		// TODO Auto-generated method stub
		switch (sensor.sBean.iSensorType) {
		case FrameUtil.SENSOR_TYPE_ID_AV_ANNUNCIATOR:
			tvAvAnnuciatorSensorData.setText(sensor.sSensorData);
			if(sensor.Bool){
				tvAvAnnuciatorSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvAvAnnuciatorSensorData.setTextColor(colorSensorData);
			}
			
			break;

		case FrameUtil.SENSOR_TYPE_ID_GAS_DETECTOR:
			tvGasDetectorSensorData.setText(sensor.sSensorData);
			
			break;

		case FrameUtil.SENSOR_TYPE_ID_IR_DETECTOR:
			tvIrDetectorSensorData.setText(sensor.sSensorData);
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IR_FENCE:
			tvIrFenceData.setText(sensor.sSensorData);
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR:
			tvDoorSensorData.setText(sensor.sSensorData);
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK:
			tvDoorSensorData.setTag(sensor);
			
			break;

		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS232:
			tvThTransmitterRS232SensorData.setText(sensor.sSensorData);
			
			break;

		case FrameUtil.SENSOR_TYPE_ID_FAN:
			if(tvFanSensorData.getTag() instanceof Sensor){
				Sensor fan = (Sensor) tvFanSensorData.getTag();
				if(fan.sBean.iCna == sensor.sBean.iCna){
					tvFanSensorData.setText(sensor.sSensorData);
				}
			}else{
				tvFanSensorData.setText("节点"+StringUtil.getHexStringFormatShort(sensor.sBean.iCna) + sensor.sSensorData);
			}
			
			break;

		case FrameUtil.SENSOR_TYPE_ID_LIGHT:
			tvLightSensorData.setText(sensor.sSensorData);
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN:
			tvWinCurtainSensorData.setText(sensor.sSensorData);
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_BH1750FVI:
			tvBH1750FVISensorData.setText(sensor.sSensorData);
						
			break;
		case FrameUtil.SENSOR_TYPE_ID_RFID_LF:
			tvLfRfidData.setText(sensor.sSensorData);
			
			break;
			
		default:
			break;
		}
		
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.e(TAG, "onClick" + v);
			Sensor sensor = null;
			ArrayList<Sensor> arr = null;
			if(v.getTag() instanceof Sensor){
				sensor = (Sensor) v.getTag();
			}else if(v.getTag() instanceof ArrayList<?>){
				Log.e(TAG, "onClick v.getTag() instanceof ArrayList<?>" + v);
				arr = (ArrayList<Sensor>) v.getTag();
			}
			
			switch (v.getId()) {
			case R.id.tvAvAnnuciatorSensorData:
				if (mLoginingDlg != null && mLoginingDlg.isShowing()){
					closeLoginingDlg();
				}else {
					showLoginingDlg(sensor, llAvAnnuciator);
				}
				break;

			case R.id.tvDoorSensorData:
				if (mLoginingDlg != null && mLoginingDlg.isShowing()){
					closeLoginingDlg();
				}else {
					showLoginingDlg(sensor, llDoor);
				}
				break;
				
			case R.id.tvFanSensorData:
				if(arr != null){
					showSensorConflict(v, FrameUtil.SENSOR_TYPE_FAN, arr);
					return;
				}
				
				if (mLoginingDlg != null && mLoginingDlg.isShowing()){
					closeLoginingDlg();
				}else {
					showLoginingDlg(sensor, llFan);
				}
				break;

			case R.id.tvLightSensorData:
				if (mLoginingDlg != null && mLoginingDlg.isShowing()){
					closeLoginingDlg();
				}else {
					showLoginingDlg(sensor, llLight);
				}
				break;
				
			case R.id.tvWinCurtainSensorData:
				if (mLoginingDlg != null && mLoginingDlg.isShowing()){
					closeLoginingDlg();
				}else {
					showLoginingDlg(sensor, llWinCurtain);
				}
				break;
				
			default:
				break;
			}
			
		}
	};

	private Dialog mLoginingDlg;

	
	/* 初始化正在登录对话框 */
	private void initLoginingDlg() {

		mLoginingDlg = new Dialog(this, R.style.settingDlg);
		mLoginingDlg.setContentView(R.layout.dlg_setting);

		mLoginingDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog

//		mConflictDlg = new Dialog(this, R.style.settingDlg);
//		mConflictDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
	}

	/* 显示正在登录对话框 */
	private void showLoginingDlg(Sensor sensor, ViewGroup parent) {
		if (mLoginingDlg != null){
			
			// 获取屏幕的高宽
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int cxScreen = dm.widthPixels;
			int cyScreen = dm.heightPixels;
			
			Rect rect = new Rect();
			parent.getGlobalVisibleRect(rect);
			Log.w(TAG, rect.toString());

			// 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置
			Window window = mLoginingDlg.getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			/* 对话框默认位置在屏幕中心,这里设置为左上角，因此x,y表示此控件到"屏幕左上角"的偏移量 */
			window.setGravity(Gravity.LEFT | Gravity.TOP);
			// TODO 检测边界
			LinearLayout bg = (LinearLayout) (mLoginingDlg.findViewById(R.id.llDialog));
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
			
			TextView tvSensorAddr = (TextView) (mLoginingDlg.findViewById(R.id.tvSensorAddr));
			TextView tvSensorType = (TextView) (mLoginingDlg.findViewById(R.id.tvSensorType));
			TextView tvSensorPower = (TextView) (mLoginingDlg.findViewById(R.id.tvSensorPower));
			Switch sw = (Switch) (mLoginingDlg.findViewById(R.id.sw));
			
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
				sw.setOnCheckedChangeListener(listener );
				
			}else{
				sw.setOnCheckedChangeListener(null);
				sw.setTag(null);
				tvSensorAddr.setText("未知地址");
				tvSensorType.setText("未知类型");
				tvSensorPower.setText("3.2V");
			}
			
			mLoginingDlg.show();
		}
	}

	/* 关闭正在登录对话框 */
	private void closeLoginingDlg() {
		if (mLoginingDlg != null && mLoginingDlg.isShowing())
			mLoginingDlg.dismiss();
	}

	synchronized private void showSensorConflict(final View v, String sensorType, final ArrayList<Sensor> conflict) {

		AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySmartHome_.this);
		ArrayList<String> list = new ArrayList<String>(conflict.size());
		String[] stringlist = new String[list.size()];
		for(int i = 0; i < conflict.size(); i++){
			Sensor sensor = conflict.get(i);
			list.add(StringUtil.getHexStringFormatShort(sensor.sBean.iCna));
			Log.e(TAG, StringUtil.getHexStringFormatShort(sensor.sBean.iCna));
		}
		stringlist = list.toArray(stringlist);
		
		builder.setTitle("发现多个" + sensorType + "传感器,请选择");
		builder.setItems(stringlist, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				v.setTag(conflict.get(which));
				
			}
			
		});

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
	}

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return SharedPreferencesUtil.TYPE_SMART_HOME;
	}

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onErr(int errReason, String errTips) {
		// TODO Auto-generated method stub
		
	}

}
