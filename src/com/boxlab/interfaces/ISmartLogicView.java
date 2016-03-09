package com.boxlab.interfaces;

import com.boxlab.bean.Sensor;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-11-10 ����8:08:55 
 * ��˵�� 
 */

public interface ISmartLogicView extends IListenerBase {

	public boolean filterInterested(Sensor sensor);

	public void notifyReciveSensor(int index, Sensor sensor);

	public void onSensorSettingReportCallback(int index, Sensor sensor);
}
