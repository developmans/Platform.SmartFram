package com.boxlab.interfaces;

import com.boxlab.bean.Sensor;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-9-16 ����1:40:05 
 * ��˵�� 
 */

public interface IListenerSensor extends IListenerBase{
	
	public boolean filterInterested(Sensor sensor);

	public void notifyReciveSensor(int index, Sensor sensor);

	public void onSensorSettingReportCallback(int index, Sensor sensor);
}
