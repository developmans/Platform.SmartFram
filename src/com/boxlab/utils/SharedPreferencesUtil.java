package com.boxlab.utils;

import java.util.ArrayList;

import com.boxlab.bean.AdminEntity;
import com.boxlab.bean.Sensor;
import com.boxlab.view.SensorViewBase;

import android.content.SharedPreferences;
import android.content.ContextWrapper;
import android.content.Context;
import android.util.Log;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-14 下午3:10:12 
 * 类说明 
 */

public class SharedPreferencesUtil {

	public static final int TYPE_SMART_COMMON = 0;
	public static final int TYPE_SMART_HOME = 1;
	public static final int TYPE_SMART_FRAM = 2;
	public static final int TYPE_FRAGMENT = 3;
	public static final int TYPE_SERVICE_PROXY = 4;
	
	private static SharedPreferences mSharedPreferences;
	
	private static final String SHARE_PRE_NAME = "SharedPreferencesUtil";

	private static final String PREF_SERVICE_KEEPALIVE = "PREF_SERVICE_KEEPALIVE";
	
	private static final String PREF_SERIAL_PORT = "PREF_SERIAL_PORT";
	private static final String PREF_SERIAL_PORT_SEL = "PREF_SERIAL_PORT_SEL";
	private static final String PREF_SERIAL_RATE = "PREF_SERIAL_RATE";
	private static final String PREF_SERIAL_RATE_SEL = "PREF_SERIAL_RATE_SEL";
	private static final String PREF_SERIAL_STATE = "PREF_SERIAL_STATE";
	private static final String PREF_SERVER_ADDR = "PREF_SERVER_ADDR";
	private static final String PREF_SERVER_PORT = "PREF_SERVER_PORT";
	
	private static final String PREF_TemperatureUp = "PREF_TemperatureUp";
	private static final String PREF_TemperatureDown = "PREF_TemperatureDown";

	private static final String PREF_HumidityUp = "PREF_HumidityUp";
	private static final String PREF_HumidityDown = "PREF_HumidityDown";

	private static final String PREF_CO2Up = "PREF_CO2Up";
	private static final String PREF_CO2Down = "PREF_CO2Down";

	private static final String PREF_LightUp = "PREF_LightUp";
	private static final String PREF_LightDown = "PREF_LightDown";

	private static final String PREF_ADMIN_COUNT = "PREF_ADMIN_COUNT";

	private static final String PREF_ADMIN = "PREF_ADMIN";
	
	/**
	 * 得到SharedPreferences对象
	 * @return SharedPreferences
	 */
	public static SharedPreferences getSharedPreferencesInstance(ContextWrapper contextWrapper) {
		if(mSharedPreferences == null){
			mSharedPreferences = contextWrapper.getSharedPreferences(SHARE_PRE_NAME, Context.MODE_PRIVATE);
		}
		return mSharedPreferences;
	}

	public static boolean setServiceKeepAlive(boolean state) {
		return mSharedPreferences.edit().putBoolean(PREF_SERVICE_KEEPALIVE, state).commit();
	}
	
	public static boolean getServiceKeepAlive() {
		return mSharedPreferences.getBoolean(PREF_SERVICE_KEEPALIVE, false);
	}
	
	
	public static boolean setSerialPort(String s) {
		return mSharedPreferences.edit().putString(PREF_SERIAL_PORT, s).commit();
	}
	
	public static String getSerialPort() {
		return mSharedPreferences.getString(PREF_SERIAL_PORT, "/dev/ttySAC1");
	}
	
	public static boolean setSerialPortSelection(int selection) {
		return mSharedPreferences.edit().putInt(PREF_SERIAL_PORT_SEL, selection).commit();
	}
	
	public static int getSerialPortSelection() {
		return mSharedPreferences.getInt(PREF_SERIAL_PORT_SEL, 2);
	}
	
	public static boolean setSerialRate(int rate) {
		return mSharedPreferences.edit().putInt(PREF_SERIAL_RATE, rate).commit();
	}

	public static int getSerialRate() {
		return mSharedPreferences.getInt(PREF_SERIAL_RATE, 57600);
	}
	
	public static boolean setSerialRateSelection(int selection) {
		return mSharedPreferences.edit().putInt(PREF_SERIAL_RATE_SEL, selection).commit();
	}
	
	public static int getSerialRateSelection() {
		return mSharedPreferences.getInt(PREF_SERIAL_RATE_SEL, 0);
	}
	
	public static boolean setSerialState(boolean state) {
		return mSharedPreferences.edit().putBoolean(PREF_SERIAL_STATE, state).commit();
	}
	
	public static boolean getSerialState() {
		return mSharedPreferences.getBoolean(PREF_SERIAL_STATE, false);
	}

	public static String getServerAddr() {
		return mSharedPreferences.getString(PREF_SERVER_ADDR, "192.168.10.59");
	}

	public static int getServerPort() {
		return mSharedPreferences.getInt(PREF_SERVER_PORT, 4004);
	}
	
	public static int getTemperatureUp() {
		return mSharedPreferences.getInt(PREF_TemperatureUp, 250);
	}
	public static int getTemperatureDown() {
		return mSharedPreferences.getInt(PREF_TemperatureDown, 200);
	}
	public static int getHumidityUp() {
		return mSharedPreferences.getInt(PREF_HumidityUp, 650);
	}
	public static int getHumidityDown() {
		return mSharedPreferences.getInt(PREF_HumidityDown, 400);
	}
	public static int getCO2Up() {
		return mSharedPreferences.getInt(PREF_CO2Up, 1000);
	}
	public static int getCO2Down() {
		return mSharedPreferences.getInt(PREF_CO2Down, 500);
	}
	public static int getLightUp() {
		return mSharedPreferences.getInt(PREF_LightUp, 600);
	}
	public static int getLightDown() {
		return mSharedPreferences.getInt(PREF_LightDown, 300);
	}

	public static void setTemperatureUp(int progress) {
		mSharedPreferences.edit().putInt(PREF_TemperatureUp, progress).commit();
	}
	public static void setTemperatureDown(int progress) {
		mSharedPreferences.edit().putInt(PREF_TemperatureDown, progress).commit();
	}
	public static void setHumidityUp(int progress) {
		mSharedPreferences.edit().putInt(PREF_HumidityUp, progress).commit();
	}
	public static void setHumidityDown(int progress) {
		mSharedPreferences.edit().putInt(PREF_HumidityDown, progress).commit();
	}
	public static void setCO2Up(int progress) {
		mSharedPreferences.edit().putInt(PREF_CO2Up, progress).commit();
	}
	public static void setCO2Down(int progress) {
		mSharedPreferences.edit().putInt(PREF_CO2Down, progress).commit();
	}
	public static void setLightUp(int progress) {
		mSharedPreferences.edit().putInt(PREF_LightUp, progress).commit();
	}
	public static void setLightDown(int progress) {
		mSharedPreferences.edit().putInt(PREF_LightDown, progress).commit();
	}

	public static void saveSensorView(SensorViewBase sv) {
		Sensor sensor = sv.getBoundSensor();
		if(sensor == null){
			Log.e("SharedPreferencesUtil", "SharedPreferencesUtil sensor == null" );
			return;
		}else{
			Log.e("SharedPreferencesUtil", "SharedPreferencesUtil sensor:" + sensor );
		}
		mSharedPreferences.edit().putInt(sv.sTitle + "iCna", sv.getBoundSensor().sBean.iCna).commit();
		mSharedPreferences.edit().putInt(sv.sTitle + "iSensorType", sv.getBoundSensor().sBean.iSensorType).commit();
		mSharedPreferences.edit().putInt(sv.sTitle + "iPlatformType", sv.getPlatformType()).commit();
		mSharedPreferences.edit().putString(sv.sTitle + "sSensorData", sv.getBoundSensor().sSensorData).commit();

		if(sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_DOOR ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_FAN ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_HEATER ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_LIGHT ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_RELAY ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_SOCKET ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_WATER_PUMP ||
				sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN)
			mSharedPreferences.edit().putBoolean(sv.sTitle + "bSensorData", sv.getBoundSensor().Bool).commit();
		
		if(sensor.sBean.iSensorType == FrameUtil.SENSOR_TYPE_ID_PIN_IO)
			mSharedPreferences.edit().putInt(sv.sTitle + "iPinIOState", sv.getBoundSensor().SensorData).commit();
		
	}

	public static void restoreSensorView(SensorViewBase sv) {
		sv.setBoundiCna(mSharedPreferences.getInt(sv.sTitle + "iCna", -1));
		sv.setBoundiSensorType(mSharedPreferences.getInt(sv.sTitle + "iSensorType", -1));
		sv.setPlatformType(mSharedPreferences.getInt(sv.sTitle + "iPlatformType", TYPE_SMART_COMMON));
		sv.setSaveSensorData(mSharedPreferences.getString(sv.sTitle + "sSensorData", "尚未加入网络，暂无数据"));
	}

	public static int getAdminCount() {
		return mSharedPreferences.getInt(PREF_ADMIN_COUNT, 0);
	}
	
	public static void setAdminCount(int cnt) {
		mSharedPreferences.edit().putInt(PREF_ADMIN_COUNT, cnt).commit();
	}
	
	public static String getAdminName(int id) {
		return mSharedPreferences.getString(PREF_ADMIN + "NAME" + id, "未知名称");
	}
	public static String getAdminPhone(int id) {
		return mSharedPreferences.getString(PREF_ADMIN + "PHONE" + id, "未知号码");
	}
	public static int getAdminPermission(int id) {
		return mSharedPreferences.getInt(PREF_ADMIN + "PERMISSION" + id, -1);
	}
	public static void setAdminName(int id, String name) {
		mSharedPreferences.edit().putString(PREF_ADMIN + "NAME" + id, name).commit();
	}
	public static void setAdminPhone(int id, String phone) {
		mSharedPreferences.edit().putString(PREF_ADMIN + "PHONE" + id, phone).commit();
	}
	public static void setAdminPermission(int id, int permission) {
		mSharedPreferences.edit().putInt(PREF_ADMIN + "PERMISSION" + id, permission).commit();
	}
	
	public static void loadAdminList(ArrayList<AdminEntity> mAdminList) {
		int cnt = getAdminCount();
		AdminEntity ae;
		for(int i = 0; i < cnt; i++){
			ae = new AdminEntity();
			ae.id = i;
			ae.name = getAdminName(i);
			ae.phone = getAdminPhone(i);
			ae.permission = getAdminPermission(i);
			mAdminList.add(ae);
		}
	}
	public static void saveAdminList(ArrayList<AdminEntity> mAdminList) {
		int cnt = mAdminList.size();
		setAdminCount(cnt);
		AdminEntity ae;
		for(int i = 0; i < cnt; i++){
			ae = mAdminList.get(i);
			setAdminName(i, ae.name);
			setAdminPhone(i, ae.phone);
			setAdminPermission(i, ae.permission);
		}
	}

	public static int getCnaBySensorTitle(String sSensorTitle) {
		return mSharedPreferences.getInt(sSensorTitle + "iCna", -1);
	}

}
