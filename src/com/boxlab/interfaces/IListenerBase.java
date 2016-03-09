package com.boxlab.interfaces;

import java.util.ArrayList;

import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2016-2-16 ����10:02:55 
 * ��˵�� 
 */

public interface IListenerBase {
	
	public static final int ERR_UART_OPEN_FAILED = -1;

	public int getListenerType();
	
	public void onInitZigBeeBeans(ArrayList<ZigBeeBean> zigbeeBeans);
	public void onInitSensorBeans(ArrayList<SensorBean> sensorBeans);
	
	public void onInitSensors(ArrayList<Sensor> sensors);
	public void onInitZigBees(ArrayList<ZigBee> zigbees);
	
	public void onErr(int errReason, String errTips);
	
}
