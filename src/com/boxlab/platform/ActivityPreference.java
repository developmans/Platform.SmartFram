package com.boxlab.platform;


import com.boxlab.bean.Sensor;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.platform.R;
import com.boxlab.utils.SharedPreferencesUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-14 下午3:58:34 
 * 类说明 
 */

public class ActivityPreference extends ActivityBase implements OnClickListener, OnItemSelectedListener, IListenerSensor{

	protected static final String TAG = "ActivityPreference";
	private Button btServiceStart;
	private Button btServiceStop;
	private Spinner spPortZigBee;
	private Spinner spBaudZigBee;
	private EditText etLog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pref);
		
		btServiceStart = (Button) findViewById(R.id.btServiceStart);
		btServiceStop = (Button) findViewById(R.id.btServiceStop);
		
		spPortZigBee = (Spinner) findViewById(R.id.spPortZigBee);
		spBaudZigBee = (Spinner) findViewById(R.id.spBaudZigBee);

		btServiceStart.setOnClickListener(this);
		btServiceStop.setOnClickListener(this);
		
		spPortZigBee.setOnItemSelectedListener(this);
		spBaudZigBee.setOnItemSelectedListener(this);
		
		etLog = (EditText) findViewById(R.id.etServiceLog);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		updateViewState();
	}

	private void updateViewState() {
		// TODO Auto-generated method stub
		
		boolean isSrvState = SharedPreferencesUtil.getSerialState();
		btServiceStart.setEnabled(!isSrvState);
		btServiceStop.setEnabled(isSrvState);
		
		spPortZigBee.setSelection(SharedPreferencesUtil.getSerialPortSelection());
		spBaudZigBee.setSelection(SharedPreferencesUtil.getSerialRateSelection());
		
		spPortZigBee.setEnabled(!isSrvState);
		spBaudZigBee.setEnabled(!isSrvState);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btServiceStart:
			SharedPreferencesUtil.setSerialState(true);
			updateViewState();
			
			break;
			
		case R.id.btServiceStop: 
			SharedPreferencesUtil.setSerialState(false);
			updateViewState();
			
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		if(parent == spPortZigBee){
			SharedPreferencesUtil.setSerialPortSelection(position);
			SharedPreferencesUtil.setSerialPort(((TextView) view).getText().toString());
		}
		
		if(parent == spBaudZigBee){
			SharedPreferencesUtil.setSerialRateSelection(position);
			SharedPreferencesUtil.setSerialRate(Integer.valueOf(((TextView) view).getText().toString()));
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 1:
				Sensor sensor = (Sensor) msg.obj;
				showServiceLog(sensor);
				break;

			default:
				break;
			}
		}
	};

	private void showServiceLog(Sensor sensor) {
		// TODO Auto-generated method stub
		etLog.setSelection(etLog.getText().length(), etLog.getText().length());
		etLog.getText().append("\n\n" + sensor.sBean);
		etLog.getText().append("\n" + sensor);
//		etLog.getText().append("\n\t\t传感器类型:" + sensor.sSensorType);
//		etLog.getText().append("\n\t\t传感器数据:" + sensor.sSensorData);
	}
	
	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO Auto-generated method stub
		return true;
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

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return SharedPreferencesUtil.TYPE_SMART_FRAM;
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
