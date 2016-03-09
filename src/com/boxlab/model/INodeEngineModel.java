package com.boxlab.model;

import java.util.ArrayList;

import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.SensorBean;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-9-15 ����5:25:25 
 * �ӿ�˵�� 
 * INodeEngineModel �ڵ�����ӿ�
 * ��������ײ����ݿ⽻��������ӿ�
 */

public interface INodeEngineModel {

	public ArrayList<ZigBeeBean> loadNetworkBeans();
	public void saveNetworkBeans(ArrayList<ZigBeeBean> zigBeeBeans);
	
	public SensorBean loadSensorBean(int iCna);
	public void saveSensorBean(SensorBean s);
	
	public ArrayList<SensorBean> loadSensorBeans(SensorBean s, int n);
	
	public void deleteNode(int nodeaddr);
	
}
