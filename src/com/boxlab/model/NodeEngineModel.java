package com.boxlab.model;

import java.util.ArrayList;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;

import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.SensorBean;
import com.boxlab.database.SQLiteHelper;
import com.boxlab.database.SQLiteNetInfoDAL;
import com.boxlab.database.SQLiteSensorInfoDal;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-15 下午2:43:25 
 * 类说明  
 * NodeEngineModel 节点引擎
 * 负责与底层数据库交互
 */

public class NodeEngineModel implements INodeEngineModel {
	
	private SQLiteNetInfoDAL mNetInfoDAL;
	private SQLiteSensorInfoDal mSensorInfoDAL;

	public NodeEngineModel(Context pContext) {
		this.mNetInfoDAL = SQLiteNetInfoDAL.getInstance(pContext);
		this.mSensorInfoDAL = SQLiteSensorInfoDal.getInstance(pContext);
	}

	/**
	 * 从数据库中加载所有NetInfoBean实例到内存中
	 */
	@Override
	public ArrayList<ZigBeeBean> loadNetworkBeans() {
		
		return this.mNetInfoDAL.load();
	}
	
	/**
	 * 将内存中的所有NetInfoBean实例同步到数据库中
	 */
	@Override
	public void saveNetworkBeans(ArrayList<ZigBeeBean> zigBeeBeans) {
		
		if(zigBeeBeans == null || zigBeeBeans.isEmpty())
			return;
		
		this.mNetInfoDAL.save(zigBeeBeans);
	}

	/**
	 * 从数据库中加载节点iCna的所有传感器数据
	 */
	@Override
	public SensorBean loadSensorBean(int iCna) {
		
		return mSensorInfoDAL.find(iCna);
	}
	
	/**
	 * 从数据库中加载节点iCna的n个传感器数据
	 */
	@Override
	public ArrayList<SensorBean> loadSensorBeans(SensorBean s, int n) {
		return mSensorInfoDAL.load(s.iCna, n);
	}

	/**
	 * 将iCna节点的传感器数据保存到数据库中
	 */
	@Override
	public void saveSensorBean(SensorBean s) {
		mSensorInfoDAL.save(s);
	}

	/**
	 * 从数据库中删除节点地址为iCna的节点信息和数据
	 */
	@Override
	public void deleteNode(int iCna) {
		mSensorInfoDAL.delete(iCna);
		mNetInfoDAL.delete(iCna);
	}

	
}
