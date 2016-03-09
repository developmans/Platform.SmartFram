package com.boxlab.platform;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.boxlab.bean.Sensor;
import com.boxlab.utils.FrameUtil;
import com.boxlab.view.AdapterListPanel;
import com.boxlab.view.PanelContent;
import com.boxlab.view.SensorViewBase;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2016-2-16 下午2:59:50 
 * 类说明 
 */

public class ActivitySmartHome extends ActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_fram_9in);
		
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
	public void notifyReciveSensor(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage(1);
		msg.obj = sensor;
		mHandler.sendMessage(msg);
		
	}

	@Override
	public void onSensorSettingReportCallback(int index, Sensor sensor) {
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

			default:
				break;
			}
		}

	};

	private void processSensor(Sensor sensor) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onErr(int errReason, String errTips) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub

	}

}
