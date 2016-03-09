package com.boxlab.model;

import java.util.ArrayList;

import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.SensorBean;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-15 下午5:25:25 
 * 接口说明 
 * INodeEngineModel 节点引擎接口
 * 定义了与底层数据库交互的引擎接口
 */

public interface INodeEngineModel {

	public ArrayList<ZigBeeBean> loadNetworkBeans();
	public void saveNetworkBeans(ArrayList<ZigBeeBean> zigBeeBeans);
	
	public SensorBean loadSensorBean(int iCna);
	public void saveSensorBean(SensorBean s);
	
	public ArrayList<SensorBean> loadSensorBeans(SensorBean s, int n);
	
	public void deleteNode(int nodeaddr);
	
}
