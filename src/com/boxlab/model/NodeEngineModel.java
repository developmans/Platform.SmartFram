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
 * ����ʱ�䣺2015-9-15 ����2:43:25 
 * ��˵��  
 * NodeEngineModel �ڵ�����
 * ������ײ����ݿ⽻��
 */

public class NodeEngineModel implements INodeEngineModel {
	
	private SQLiteNetInfoDAL mNetInfoDAL;
	private SQLiteSensorInfoDal mSensorInfoDAL;

	public NodeEngineModel(Context pContext) {
		this.mNetInfoDAL = SQLiteNetInfoDAL.getInstance(pContext);
		this.mSensorInfoDAL = SQLiteSensorInfoDal.getInstance(pContext);
	}

	/**
	 * �����ݿ��м�������NetInfoBeanʵ�����ڴ���
	 */
	@Override
	public ArrayList<ZigBeeBean> loadNetworkBeans() {
		
		return this.mNetInfoDAL.load();
	}
	
	/**
	 * ���ڴ��е�����NetInfoBeanʵ��ͬ�������ݿ���
	 */
	@Override
	public void saveNetworkBeans(ArrayList<ZigBeeBean> zigBeeBeans) {
		
		if(zigBeeBeans == null || zigBeeBeans.isEmpty())
			return;
		
		this.mNetInfoDAL.save(zigBeeBeans);
	}

	/**
	 * �����ݿ��м��ؽڵ�iCna�����д���������
	 */
	@Override
	public SensorBean loadSensorBean(int iCna) {
		
		return mSensorInfoDAL.find(iCna);
	}
	
	/**
	 * �����ݿ��м��ؽڵ�iCna��n������������
	 */
	@Override
	public ArrayList<SensorBean> loadSensorBeans(SensorBean s, int n) {
		return mSensorInfoDAL.load(s.iCna, n);
	}

	/**
	 * ��iCna�ڵ�Ĵ��������ݱ��浽���ݿ���
	 */
	@Override
	public void saveSensorBean(SensorBean s) {
		mSensorInfoDAL.save(s);
	}

	/**
	 * �����ݿ���ɾ���ڵ��ַΪiCna�Ľڵ���Ϣ������
	 */
	@Override
	public void deleteNode(int iCna) {
		mSensorInfoDAL.delete(iCna);
		mNetInfoDAL.delete(iCna);
	}

	
}
