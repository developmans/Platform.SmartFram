package com.boxlab.bean;

import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-15 下午2:43:48 
 * 类说明 
 */

public class ZigBeeBean {

	public static final int DEF_C_NA = 0;
	public static final int DEF_P_NA = 0;
	public static final long DEF_C_IEEE = 0L;
	public static final long DEF_P_IEEE = 0L;
	public static final int DEF_PAN_ID = 0;
	public static final int DEF_PROFILE = 0;
	public static final int DEF_CVER = 0;

	public int id;
	
	public int iCna = DEF_C_NA;
	public int iPna = DEF_P_NA;
	
	public long lCieee = DEF_C_IEEE;
	public long lPieee = DEF_P_IEEE;

	public int iPanId = DEF_PAN_ID;
	
	public int iProfile = DEF_PROFILE;
	public int iCver = DEF_CVER;
	
	public int getiCna() {
		return iCna;
	}
	
	public void setiCna(int iCna) {
		this.iCna = iCna;
	}
	
	public int getiPna() {
		return iPna;
	}
	
	public void setiPna(int iPna) {
		this.iPna = iPna;
	}
	
	public long getlCieee() {
		return lCieee;
	}
	
	public void setlCieee(long lCieee) {
		this.lCieee = lCieee;
	}
	
	public long getlPieee() {
		return lPieee;
	}
	
	public void setlPieee(long lPieee) {
		this.lPieee = lPieee;
	}
	
	public int getiPanId() {
		return iPanId;
	}
	
	public void setiPanId(int iPanId) {
		this.iPanId = iPanId;
	}
	
	public int getiProfile() {
		return iProfile;
	}
	
	public void setiProfile(int iProfile) {
		this.iProfile = iProfile;
	}
	
	public int getiCver() {
		return iCver;
	}
	
	public void setiCver(int iCver) {
		this.iCver = iCver;
	}
	
	public static boolean isComplete(ZigBeeBean n) {
		return ((n.lCieee != DEF_C_IEEE) && (n.lPieee != DEF_P_IEEE));
	}

	@Override
	public String toString() {
		return "节点网络信息： " 
	            + "\n  iCna    : " + StringUtil.getHexStringFormatShort(this.iCna) 
				+ "\n  iPna    : " + StringUtil.getHexStringFormatShort(this.iPna) 
				+ "\n  lCieee  : " + StringUtil.getHexStringFormatLong(this.lCieee)
				+ "\n  lPieee  : " + StringUtil.getHexStringFormatLong(this.lPieee)
				+ "\n  iPanId  : " + StringUtil.getHexStringFormatShort(this.iPanId)
				+ "\n  iProfile: " + StringUtil.getHexStringFormatShort(this.iProfile)
				+ "\n  iCver   : " + StringUtil.getHexStringFormatShort(this.iCver)
				;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iCna;
		result = prime * result + iCver;
		result = prime * result + iPanId;
		result = prime * result + iPna;
		result = prime * result + iProfile;
		result = prime * result + (int) (lCieee ^ (lCieee >>> 32));
		result = prime * result + (int) (lPieee ^ (lPieee >>> 32));
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
		ZigBeeBean other = (ZigBeeBean) obj;
		if (iCna != other.iCna)
			return false;
		if (iCver != other.iCver)
			return false;
		if (iPanId != other.iPanId)
			return false;
		if (iPna != other.iPna)
			return false;
		if (iProfile != other.iProfile)
			return false;
		if (lCieee != other.lCieee)
			return false;
		if (lPieee != other.lPieee)
			return false;
		return true;
	}
	
	


}
