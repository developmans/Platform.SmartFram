package com.boxlab.bean;

import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-11-9 下午5:44:13 
 * 类说明 
 */

public class MsgEntity {
	public static final String MSG_MY_NAME = "智慧农业实训系统";
	
	public static final int MSG_TYPE_RECV = 0x01;
	public static final int MSG_TYPE_SEND = 0x02;

	public final AdminEntity admin;
	
	public int iMsgType;
	public String sContent;
	public String sMsgTime = StringUtil.getTimeStamp();
	
	public MsgEntity(AdminEntity admin){
		this.admin = admin;
	}

}
