package com.boxlab.model;

import java.util.ArrayList;
import java.util.Map;

import com.boxlab.bean.SourceBean;
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
	public ArrayList<SensorBean> loadSensorBeans(String startTime,String stopTime);
	
	public void deleteNode(int nodeaddr);
	
	public void saveSourceBean(SourceBean s);
	public ArrayList<SourceBean> loadSourceBeans();
	public SourceBean load(String rfid);
	public void updateSourceBean(SourceBean s);
	public void deleteSourceBeans(Map<Integer, String> sourceBeans);
	
	
}
