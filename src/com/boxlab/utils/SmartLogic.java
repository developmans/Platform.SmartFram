package com.boxlab.utils;

import java.util.HashMap;

import android.app.Application;

import com.boxlab.bean.Sensor;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.platform.R;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-9-17 ����3:08:56 
 * ��˵�� 
 */

public class SmartLogic {
	
    private static SmartLogic instance;

	public static int iTup;
	public static int iTdown;
	public static int iHup;
	public static int iHdown;
	public static int iCup;
	public static int iCdown;
	public static int iLightup;
	public static int iLightdown;

	public static String sTitleHeater;
	public static String sTitleThRS485;
	public static String sTitleLight;
	public static String sTitleBH1750FVI;
	public static String sTitleWaterPump;
	public static String sTitleSoil;
	public static String sTitleFan;
	public static String sTitleCO2;
	public static String sTitleRollBlind;
	
	public static Sensor sensorHeater;
	public static Sensor sensorThRS485;
	public static Sensor sensorLight;
	public static Sensor sensorBH1750FVI;
	public static Sensor sensorWaterPump;
	public static Sensor sensorSoil;
	public static Sensor sensorFan;
	public static Sensor sensorCO2;
	public static Sensor sensorRollBlind;
	
	public static HashMap<String, Sensor> mapSensorRecord = new HashMap<String, Sensor>();

	private SmartLogic(Application app) {
		
		sTitleHeater = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleHeater);
		sTitleThRS485 = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleThTransmitterRS485);
		sTitleLight = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleLight);
		sTitleBH1750FVI = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleBH1750FVI);
		sTitleWaterPump = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleWaterPump);
		sTitleSoil = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleSoil);
		sTitleFan = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleFan);
		sTitleCO2 = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleCO2);
		sTitleRollBlind = app.getApplicationContext().getResources().getString(R.string.SvbTvTitleRollBlind);
		
		updateLogic();
	}
	
	public static SmartLogic getSmartLogicInstance(Application app) {
        if (null == instance) {
            instance = new SmartLogic(app);
        }
        return instance;
	}

	public static void updateLogic() {
		
		iTup = SharedPreferencesUtil.getTemperatureUp();
		iTdown = SharedPreferencesUtil.getTemperatureDown();

		iHup = SharedPreferencesUtil.getHumidityUp();
		iHdown = SharedPreferencesUtil.getHumidityDown();

		iCup = SharedPreferencesUtil.getCO2Up();
		iCdown = SharedPreferencesUtil.getCO2Down();

		iLightup = SharedPreferencesUtil.getLightUp();
		iLightdown = SharedPreferencesUtil.getLightDown();
		
	}

	public static void putSensorRecord(String title, Sensor sensor ) {
		
		mapSensorRecord.put(title, sensor);
		
		if(title.equals(sTitleHeater)){
			sensorHeater = sensor;
		}else if(title.equals(sTitleThRS485)){
			sensorThRS485 = sensor;
		}else if(title.equals(sTitleLight)){
			sensorLight = sensor;
		}else if(title.equals(sTitleBH1750FVI)){
			sensorBH1750FVI = sensor;
		}else if(title.equals(sTitleWaterPump)){
			sensorWaterPump = sensor;
		}else if(title.equals(sTitleSoil)){
			sensorSoil = sensor;
		}else if(title.equals(sTitleFan)){
			sensorFan = sensor;
		}else if(title.equals(sTitleCO2)){
			sensorCO2 = sensor;
		}else if(title.equals(sTitleRollBlind)){
			sensorRollBlind = sensor;
		}
		
	}
	
	public static Sensor getSensorRecord(String title) {
		
		return mapSensorRecord.get(title);
		
	}
	
	public static final String MSG_ACTION_HELP = "�밴�����¸�ʽ�ظ���\n" +
			"��ʽ1����ȡ#��������Ż��ߴ���������#��Ϣ\n" +
			"��ʽ2������#��������Ż��ߴ���������#״̬[��/��/ͣ]\n" +
			"��ʽ3���޸�#��������Ż��ߴ���������#������[����ֵ&&����ֵ]\n" +
			"�ظ�\"�������б�\"��ȡ���������ƺͱ��";

	public static final String MSG_ACTION_GET_SENSOR_TITLE_LIST = "�������б�";
	public static final String[] MSG_SENSOR_TITLE_LIST = new String[]{
		"��Ч������",
		"�����¶ȼ�����",
		"������ʪ�ȼ��",
		"ֲ��������",
		"������ǿ���",
		"���ˮ��",
		"������ʪ�ȼ��",
		"����ͨ��װ��",
		"CO2��������Ũ��",
		"�������װ��",
		};

	public static final int MSG_ACTION_UNDIFIND = -1;
	public static final int MSG_ACTION_READ_SENSOR_STATE = 1;
	public static final int MSG_ACTION_SEND_CTRL_CMD = 2;
	public static final int MSG_ACTION_EDIT_PARM = 3;
	// ������Ŵ��������
	public static final String[] MSG_ACTION_FILTER_READ_SENSOR_STATE = new String[]{"��ȡ", "��Ϣ"};
	public static final String[] MSG_ACTION_FILTER_SEND_CTRL_CMD = new String[]{"����", "״̬"};
	public static final String[] MSG_ACTION_FILTER_EDIT_PARM = new String[]{"�޸�", "������"};

}
