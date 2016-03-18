package com.boxlab.model;

import java.util.ArrayList;
import java.util.Map;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;

import com.boxlab.bean.SourceBean;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.SensorBean;
import com.boxlab.database.SQLiteHelper;
import com.boxlab.database.SQLiteNetInfoDAL;
import com.boxlab.database.SQLiteSensorInfoDal;
import com.boxlab.database.SQLiteSourceInfoDAL;

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
	private SQLiteSourceInfoDAL mSourceInfoDAL;

	public NodeEngineModel(Context pContext) {
		this.mNetInfoDAL = SQLiteNetInfoDAL.getInstance(pContext);
		this.mSensorInfoDAL = SQLiteSensorInfoDal.getInstance(pContext);
		this.mSourceInfoDAL = SQLiteSourceInfoDAL.getInstance(pContext);
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
	 * �����ݿ��м������д���������
	 */
	@Override
	public ArrayList<SensorBean> loadSensorBeans(String startTime,String stopTime) {
		return mSensorInfoDAL.load(startTime,stopTime);
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

	@Override
	public void saveSourceBean(SourceBean s) {
		mSourceInfoDAL.saveCompleteSourceInfoBean(s);
	}

	@Override
	public ArrayList<SourceBean> loadSourceBeans() {
		return mSourceInfoDAL.load();
	}

	@Override
	public void updateSourceBean(SourceBean s) {
		mSourceInfoDAL.update(s);
	}

	@Override
	public void deleteSourceBeans(Map<Integer, String> sourceBeans) {
		mSourceInfoDAL.delete(sourceBeans);
	}

	@Override
	public SourceBean load(String rfid) {
		return mSourceInfoDAL.load(rfid);
	}

}
