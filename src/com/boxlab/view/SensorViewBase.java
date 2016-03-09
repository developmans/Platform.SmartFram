package com.boxlab.view;

import java.util.ArrayList;

import com.boxlab.bean.Sensor;
import com.boxlab.platform.BasicApp;
import com.boxlab.platform.ServiceProxy;
import com.boxlab.platform.R;
import com.boxlab.utils.DataResolverUtil;
import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.PinIO;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.SmartLogic;
import com.boxlab.utils.StringUtil;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-10-22 ����2:30:17 
 * ��˵�� 
 */

public class SensorViewBase extends LinearLayout {
	private static final String TAG = "SensorViewBase";
	
	public int mPlatformType = SharedPreferencesUtil.TYPE_SMART_COMMON;

	public String sTitle;
	private int iboundCna = -1;
	private int iboundSensorType = -1;
	private String sSensorData = "";
	private int rIcon = -1;

	private Sensor mBoundSensor = null;
	
	private LinearLayout mViewGroup;

	private TextView tvTitle;
	private ImageButton ibIcon;
	private TextView tvSensorData;
	
	private int colorSensorData;
	private int colorSensorDataAlarm;

	// TODO remove
//  private ServiceProxy mService = null;
//	private BasicApp mApp;
//	private SmartLogic smartLogic;
//	private SharedPreferences mPref;
	
	public SensorViewBase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context, null, 0);
	}

	public SensorViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context, attrs, 0);
	}

	public SensorViewBase(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context, attrs, defStyle);
	}

	private void initView(Context context, AttributeSet attrs, int defStyle) {
		// TODO Auto-generated method stub
		
		mViewGroup = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.sensor_view_base, SensorViewBase.this);
		
		tvTitle = (TextView) mViewGroup.findViewById(R.id.tvTitle);
		ibIcon = (ImageButton) mViewGroup.findViewById(R.id.ibIcon);
		tvSensorData = (TextView) mViewGroup.findViewById(R.id.tvSensorData);
		
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.SensorView);

		sTitle = typeArray.getString(R.styleable.SensorView_title);
		rIcon = typeArray.getResourceId(R.styleable.SensorView_sensorIcon, R.drawable.ic_launcher2);
		Drawable dw = typeArray.getDrawable(R.styleable.SensorView_sensorIcon);
		
		mPlatformType = typeArray.getInt(R.styleable.SensorView_platformType, 0);
		
		typeArray.recycle();
		
		if(sTitle != null)
			tvTitle.setText(sTitle);
		
		if(dw != null)
			ibIcon.setBackgroundDrawable(dw);
		
		colorSensorData = context.getResources().getColor(R.color.sensor_data_text);
		colorSensorDataAlarm = context.getResources().getColor(R.color.sensor_data_text_alarm);

		//smartLogic = SmartLogic.getSmartLogicInstance();
		
	}
	
	public void onNotifySensor(Sensor sensor){
		
		if(mBoundSensor == null){
			// �����Ȱ�
			setBindSensor(sensor);
		}else if((iboundCna != -1 && iboundCna != sensor.sBean.iCna) ||
				 (iboundSensorType != -1 && iboundSensorType != sensor.sBean.iSensorType)){
			// ��ͬ�İ󶨽ڵ�
			return;
		}

		tvSensorData.setText(mBoundSensor.sSensorData);
		
		switch (mBoundSensor.sBean.iSensorType) {
		
		case FrameUtil.SENSOR_TYPE_ID_LM35DZ:
			break;

		case FrameUtil.SENSOR_TYPE_ID_LS_RESISTANCE: // �������贫����
			break;

		case FrameUtil.SENSOR_TYPE_ID_LS_DIODE: // ���������ܴ�����
			break;

		case (byte) 0x04: // MQ-3�ƾ�������
			break;

		case (byte) 0x05: // MQ-135��������������(����/����/��ϵ����)
			break;

		case (byte) 0x06: // MQ-2������(���Һ����/����/����)
			break;

		case (byte) 0x07: // HC-SR501������⴫����
			break;

		case (byte) 0x08: // ֱ�����ģ��
			break;

		case (byte) 0x09: // ��ʪ�ȴ�����(�;���)
			break;

		case (byte) 0x0a: // ADXL345�������ּ��ٶȴ�����
			break;

		case (byte) 0x0b: // SHT10��ʪ�ȴ�����(�߾���)
			break;

		case (byte) 0x0c: // L3G4200D �������������Ǵ�����
			break;
			
		case (byte) 0x0d: // �ڵ�IO״̬
			if(mPlatformType == SharedPreferencesUtil.TYPE_SMART_HOME){
				
			}else if(mPlatformType == SharedPreferencesUtil.TYPE_SMART_FRAM){
				
				PinIO ioState = new PinIO(mBoundSensor.SensorData);

				if(PinIO.get(ioState, PinIO.ROLL_BLIND_TYPE) == PinIO.ROLL_BLIND_STATE_POSITIVE){
					tvSensorData.setText("��������...");
					tvSensorData.setTextColor(colorSensorDataAlarm);
				}else if(PinIO.get(ioState, PinIO.ROLL_BLIND_TYPE) == PinIO.ROLL_BLIND_STATE_NEGATIVE){
					tvSensorData.setText("�����ر���...");
					tvSensorData.setTextColor(colorSensorDataAlarm);
				}else{
					tvSensorData.setText("����ֹͣ");
					tvSensorData.setTextColor(colorSensorData);
				}
				
			}
			break;
			
		case (byte) 0x0e: // ���ܵ��
			break;
			
		case (byte) 0x0f: // Զ��ң���豸
			break;
		
		case (byte) 0x11: // �񶯴�����
			break;
			
		case (byte) 0x13: // BH1750FVI���մ�����
			if(mPlatformType == SharedPreferencesUtil.TYPE_SMART_HOME){

			} else if (mPlatformType == SharedPreferencesUtil.TYPE_SMART_FRAM) {
				int ctrState = -1;
				if ((((int) mBoundSensor.Light) > SmartLogic.iLightup)
						&& SmartLogic.sensorLight != null
						&& SmartLogic.sensorLight.Bool == true) {
					Log.e(TAG, "��ǿ�������ޣ��ر�ֲ���");
					ctrState = 0;
				}
				if ((((int) mBoundSensor.Light) < SmartLogic.iLightdown)
						&& SmartLogic.sensorLight != null
						&& SmartLogic.sensorLight.Bool == false) {
					Log.e(TAG, "��ǿ�������ޣ���ֲ���");
					ctrState = 1;
				}
				if (ctrState != -1) {
					tvSensorData.setTextColor(colorSensorDataAlarm);
				} else {
					tvSensorData.setTextColor(colorSensorData);
				}
			}
		    break;
		
		case (byte) 0x14: // ����������
			break;
		
		case (byte) 0x15: // �����ഫ����
			break;
		
		case (byte) 0x17: // �̵����豸
			break;

		case (byte) 0x18: // ��ѹ������
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IO_LED:
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_INNER_TS:
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_LIGHT:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
				ibIcon.setBackgroundResource(R.drawable.lamp_bulb_on);
				rIcon = R.drawable.lamp_bulb_on;
			}else{
				tvSensorData.setTextColor(colorSensorData);
				ibIcon.setBackgroundResource(R.drawable.lamp_bulb_off);
				rIcon = R.drawable.lamp_bulb_off;
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_FAN:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WATER_PUMP:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_HEATER:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_AV_ANNUNCIATOR:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SOCKET:
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_RFID_LF:
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_CO2:
			if(mPlatformType == SharedPreferencesUtil.TYPE_SMART_HOME){
				//do nothing
			} else if (mPlatformType == SharedPreferencesUtil.TYPE_SMART_FRAM) {
				int ctrState = -1;
				if ((((int) mBoundSensor.Concentration) > SmartLogic.iCup)
						&& SmartLogic.sensorFan != null
						&& SmartLogic.sensorFan.Bool == true) {
					Log.e(TAG, "CO2Ũ�ȳ�������");
					ctrState = 1;
				}
				if ((((int) mBoundSensor.Concentration) < SmartLogic.iCdown)
						&& SmartLogic.sensorFan != null
						&& SmartLogic.sensorFan.Bool == false) {
					Log.e(TAG, "CO2Ũ�ȵ�������");
					ctrState = 0;
				}
				if (ctrState != -1) {
					tvSensorData.setTextColor(colorSensorDataAlarm);
				} else {
					tvSensorData.setTextColor(colorSensorData);
				}
			}
			break;

		case FrameUtil.SENSOR_TYPE_ID_SOIL:
			if(mPlatformType == SharedPreferencesUtil.TYPE_SMART_HOME){
				//do nothing
			}else if(mPlatformType == SharedPreferencesUtil.TYPE_SMART_FRAM){

				int iHumi = (int) (mBoundSensor.Humidity * 10);

				int ctrState = -1;
				if (iHumi > SmartLogic.iHup
						&& SmartLogic.sensorWaterPump != null
						&& SmartLogic.sensorWaterPump.Bool == true) {
					Log.e(TAG, "����ʪ�ȳ������ޣ��ر�ˮ��");
					ctrState = 0;
				}
				if (iHumi < SmartLogic.iHdown
						&& SmartLogic.sensorWaterPump != null
						&& SmartLogic.sensorWaterPump.Bool == false) {

					Log.e(TAG, "����ʪ�ȵ������ޣ���ˮ��");
					ctrState = 1;
				}
				if (ctrState != -1) {
					tvSensorData.setTextColor(colorSensorDataAlarm);
				} else {
					tvSensorData.setTextColor(colorSensorData);
				}
			}
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IR_DETECTOR:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_GAS_DETECTOR:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IR_FENCE:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SMOKE_ALARM:
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS232:
			//ibIcon.setBackgroundResource(R.drawable.temperature);
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485:
			if (mPlatformType == SharedPreferencesUtil.TYPE_SMART_HOME) {
				// do nothing
			} else if (mPlatformType == SharedPreferencesUtil.TYPE_SMART_FRAM) {

				int iTemp = (int) (mBoundSensor.Temperature * 10);

				int ctrState = -1;
				if (iTemp > SmartLogic.iTup && SmartLogic.sensorHeater != null
						&& SmartLogic.sensorHeater.Bool == true) {
					Log.e(TAG, "�����¶ȳ������ޣ��رռ�����");
					ctrState = 0;
				}
				if (iTemp < SmartLogic.iTdown
						&& SmartLogic.sensorHeater != null
						&& SmartLogic.sensorHeater.Bool == false) {
					Log.e(TAG, "�����¶ȵ������ޣ��򿪼�����");
					ctrState = 1;
				}
				if (ctrState != -1) {
					tvSensorData.setTextColor(colorSensorDataAlarm);
				} else {
					tvSensorData.setTextColor(colorSensorData);
				}
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN:
			if(mBoundSensor.Bool){
				tvSensorData.setTextColor(colorSensorDataAlarm);
			}else{
				tvSensorData.setTextColor(colorSensorData);
			}
			break;
			
		case (byte) 0xfe: // ��Ч������
			break;
			
		default:
			break;
		}
	}
	
	// ����״̬
	public void setBoundiCna(int icna) {
		iboundCna = icna;
	}
	public void setBoundiSensorType(int s_tp) {
		iboundSensorType = s_tp;
	}
	public void setSaveSensorData(String s_da) {
		sSensorData = s_da;
		tvSensorData.setText(sSensorData);
	}
	
	public void setPlatformType(int platform){
		if(platform < SharedPreferencesUtil.TYPE_SMART_COMMON || platform > SharedPreferencesUtil.TYPE_SMART_FRAM)
			return;
		mPlatformType = platform;
	}
	public int getPlatformType(){
		return mPlatformType;
	}
	
	public void setIconResId(int resId){
		rIcon =  resId;
		ibIcon.setBackgroundResource(resId);
	}
	public int getIconResId(int resId){
		return rIcon;
	}
	
	/**
	 *  ���ð󶨽ڵ�
	 * @param sensor
	 */
	public void setBindSensor(Sensor sensor){
		
		mBoundSensor = sensor;
		
		setBoundiCna(sensor.sBean.iCna);
		setBoundiSensorType(sensor.sBean.iSensorType);
		
		onNotifySensor(sensor);
		
		//if(smartLogic != null){
			SmartLogic.putSensorRecord(sTitle, sensor);
		//}
		
	}
	/** 
	 * ��ȡ�󶨽ڵ�
	 * @return
	 */
	public Sensor getBoundSensor() {
		return mBoundSensor;
	}

//	/**
//	 * ���ӵ��������ͬʱ��ȡBasicApp
//	 * @param service
//	 */
//	public void connectSevice(ServiceProxy service){
//		mService = service;
//		mApp = (BasicApp)(mService.getApplication());
//	}
//	/**
//	 * �Ͽ��������
//	 * @param service
//	 */
//	public void disconnectSevice(){
//		mService = null;
//		mApp = null;
//	}

	/** 
	 * �ָ�SensorView��Ϣ�����󶨽ڵ�
	 * @param sensorList
	 */
	public void onSensorViewRestore(ArrayList<Sensor> sensorList) {
		SharedPreferencesUtil.restoreSensorView(this);
		Log.e(TAG, "onSensorViewRestore: iboundCna = " + 
		            StringUtil.getHexStringFormatShort(iboundCna));
		
		if(iboundCna != -1){
			//check if there is node which iCna is match
			int length = sensorList.size();
			for(int i = 0; i < length; i++){
				Sensor sensor = sensorList.get(i);
				if(iboundCna == sensor.sBean.iCna){
					setBindSensor(sensor);
				}
			}
		}
	}
	
	/**
	 * ����SensorView��Ϣ
	 * 
	 */
	public void onSensorViewDestory() {
		Log.e(TAG, "onSensorViewDestory:" + this.sTitle);
		SharedPreferencesUtil.saveSensorView(this);
	}

}
