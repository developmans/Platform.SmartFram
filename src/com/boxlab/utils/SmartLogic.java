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
 * 创建时间：2015-9-17 下午3:08:56 
 * 类说明 
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
	
	public static final String MSG_ACTION_HELP = "请按照如下格式回复：\n" +
			"格式1：获取#传感器编号或者传感器名称#信息\n" +
			"格式2：设置#传感器编号或者传感器名称#状态[开/关/停]\n" +
			"格式3：修改#传感器编号或者传感器名称#上下限[上限值&&下限值]\n" +
			"回复\"传感器列表\"获取传感器名称和编号";

	public static final String MSG_ACTION_GET_SENSOR_TITLE_LIST = "传感器列表";
	public static final String[] MSG_SENSOR_TITLE_LIST = new String[]{
		"无效传感器",
		"大棚温度加热器",
		"大棚温湿度检测",
		"植物生长灯",
		"环境光强检测",
		"灌溉水泵",
		"土壤温湿度检测",
		"大棚通风装置",
		"CO2温室气体浓度",
		"大棚卷帘装置",
		};

	public static final int MSG_ACTION_UNDIFIND = -1;
	public static final int MSG_ACTION_READ_SENSOR_STATE = 1;
	public static final int MSG_ACTION_SEND_CTRL_CMD = 2;
	public static final int MSG_ACTION_EDIT_PARM = 3;
	// 定义短信处理过滤器
	public static final String[] MSG_ACTION_FILTER_READ_SENSOR_STATE = new String[]{"获取", "信息"};
	public static final String[] MSG_ACTION_FILTER_SEND_CTRL_CMD = new String[]{"设置", "状态"};
	public static final String[] MSG_ACTION_FILTER_EDIT_PARM = new String[]{"修改", "上下限"};

}
