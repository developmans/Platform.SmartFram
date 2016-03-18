package com.boxlab.bean;

import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-15 下午2:44:11 
 * 类说明 
 */

public class SensorBean {
	
	public static final int DEF_C_NA = 0;
	public static final int DEF_S_TP = FrameUtil.SENSOR_TYPE_ID_UNDEFINE;
	public static final byte[] DEF_S_DA = FrameUtil.SENSOR_DATA_ARR;
	public static final int DEF_C_POWER = 0;

	public int id;
	
	public int iCna = DEF_C_NA;
	
	public int iSensorType = DEF_S_TP;
	
	public byte[] aSensorData = DEF_S_DA;
	
	public int iPower = DEF_C_POWER;
	
	public String sTimeStamp = "";

	public int getiCna() {
		return iCna;
	}

	public void setiCna(int iCna) {
		this.iCna = iCna;
	}

	public int getiSensorType() {
		return iSensorType;
	}

	public void setiSensorType(int iSensorType) {
		this.iSensorType = iSensorType;
	}

	public byte[] getaSensorData() {
		return aSensorData;
	}

	public void setaSensorData(byte[] aSensorData) {
		this.aSensorData = aSensorData;
	}

	public int getPower() {
		return iPower;
	}

	public void setPower(int power) {
		iPower = power;
	}

	public String getsTimeStamp() {
		return sTimeStamp;
	}

	public void setsTimeStamp(String sTimeStamp) {
		this.sTimeStamp = sTimeStamp;
	}

	@Override
	public String toString() {
		return "-----------------------节点传感器原始数据-----------------------"
				+ "\n  Cna       : " + StringUtil.getHexStringFormatShort(this.iCna) 
				+ "\n  SensorType: " + StringUtil.getHexStringFormatByte((byte)(this.iSensorType & 0xFF))
				+ "\n  SensorData: " + StringUtil.arrByteFormat( this.aSensorData, 10)
//				+ "\n  SensorData: " + StringUtil.getStringFormatS_DA(this.iSensorType, this.aSensorData)
//				+ "\n  Power Level:" + StringUtil.getStringFormatPowerLevel(this.iPower) 
				;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iCna;
		result = prime * result + iSensorType;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorBean other = (SensorBean) obj;
		if (iCna != other.iCna)
			return false;
		if (iSensorType != other.iSensorType)
			return false;
		return true;
	}
	
}
