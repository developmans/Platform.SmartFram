package com.boxlab.platform;

import com.boxlab.bean.Sensor;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2016-2-16 ����4:33:39 
 * ��˵�� 
 */

public class ActivityZigBeeManage extends ActivityBase {

	public ActivityZigBeeManage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyReciveSensor(int index, Sensor sensor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorSettingReportCallback(int index, Sensor sensor) {
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
