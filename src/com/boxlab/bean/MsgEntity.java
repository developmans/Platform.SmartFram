package com.boxlab.bean;

import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-11-9 ����5:44:13 
 * ��˵�� 
 */

public class MsgEntity {
	public static final String MSG_MY_NAME = "�ǻ�ũҵʵѵϵͳ";
	
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
