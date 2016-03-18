package com.boxlab.bean;

import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: lixb@boxlab.cn
 * 创建时间：2016-3-14 下午2:43:48 
 * 类说明 
 */

public class SourceBean {

	public static final String RFID_ID = "";
	public static final String VARIETIES = "";
	public static final int REGION = 0;
	public static final String PLANTING_DATE = "";
	public static final String HARVEST_DATE = "";

	public int id;
	
	public String sRfid = RFID_ID;
	public String sVarieties = VARIETIES;
	
	public int iRegion = REGION;

	public String sPalant = PLANTING_DATE;
	public String sHarvest = HARVEST_DATE;
	
	
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getsRfid() {
		return sRfid;
	}



	public void setsRfid(String sRfid) {
		this.sRfid = sRfid;
	}



	public String getsVarieties() {
		return sVarieties;
	}



	public void setsVarieties(String sVarieties) {
		this.sVarieties = sVarieties;
	}



	public int getiRegion() {
		return iRegion;
	}



	public void setiRegion(int iRegion) {
		this.iRegion = iRegion;
	}



	public String getsPalant() {
		return sPalant;
	}



	public void setsPalant(String sPalant) {
		this.sPalant = sPalant;
	}



	public String getsHarvest() {
		return sHarvest;
	}



	public void setsHarvest(String sHarvest) {
		this.sHarvest = sHarvest;
	}



//	public static boolean isComplete(SourceBean n) {
//		return ((n.lCieee != DEF_C_IEEE) && (n.lPieee != DEF_P_IEEE));
//	}
	
	

//	@Override
//	public String toString() {
//		return "农作物信息： " 
//	            + "\n  iCna    : " + StringUtil.getHexStringFormatShort(this.iCna) 
//				+ "\n  iPna    : " + StringUtil.getHexStringFormatShort(this.iPna) 
//				+ "\n  lCieee  : " + StringUtil.getHexStringFormatLong(this.lCieee)
//				+ "\n  lPieee  : " + StringUtil.getHexStringFormatLong(this.lPieee)
//				+ "\n  iPanId  : " + StringUtil.getHexStringFormatShort(this.iPanId)
//				+ "\n  iProfile: " + StringUtil.getHexStringFormatShort(this.iProfile)
//				+ "\n  iCver   : " + StringUtil.getHexStringFormatShort(this.iCver)
//				;
//	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + iCna;
//		result = prime * result + iCver;
//		result = prime * result + iPanId;
//		result = prime * result + iPna;
//		result = prime * result + iProfile;
//		result = prime * result + (int) (lCieee ^ (lCieee >>> 32));
//		result = prime * result + (int) (lPieee ^ (lPieee >>> 32));
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		SourceBean other = (SourceBean) obj;
//		if (iCna != other.iCna)
//			return false;
//		if (iCver != other.iCver)
//			return false;
//		if (iPanId != other.iPanId)
//			return false;
//		if (iPna != other.iPna)
//			return false;
//		if (iProfile != other.iProfile)
//			return false;
//		if (lCieee != other.lCieee)
//			return false;
//		if (lPieee != other.lPieee)
//			return false;
//		return true;
//	}
	
	


}
