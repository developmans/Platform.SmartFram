package com.boxlab.bean;

import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-10-28 下午3:40:36 
 * 类说明 
 */

public class ZigBee {
	
	public final ZigBeeBean nBean;
	
	public String sID = "-";
	public String sC_NA = "无效地址";
	public String sC_IEEE = "xx-xx-xx-xx-xx-xx-xx-xx";
	public String sP_NA = "-";
	public String sP_IEEE = "xx-xx-xx-xx-xx-xx-xx-xx";
	public String sC_PANID = "-";

	public String sProfile = "-";
	public String sVer = "-";

	public ZigBee(ZigBeeBean nbean) {
		this.nBean = nbean;
		updataNetInfo();
	}

	public void updataNetInfo() {
		this.sID = String.valueOf(nBean.id);
		this.sC_NA = StringUtil.getHexStringFormatShort(nBean.iCna);
		this.sC_IEEE = StringUtil.getHexStringFormatIEEE(nBean.lCieee);
		this.sP_NA = StringUtil.getHexStringFormatShort(nBean.iPna);
		this.sP_IEEE = StringUtil.getHexStringFormatIEEE(nBean.lPieee);
		this.sC_PANID = StringUtil.getHexStringFormatShort(nBean.iPanId);
		this.sProfile = StringUtil.getHexStringFormatByte((byte)nBean.iProfile);
		this.sVer = StringUtil.getHexStringFormatByte((byte)nBean.iProfile);
	}
}
