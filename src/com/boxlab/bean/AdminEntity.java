package com.boxlab.bean;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-11-9 ����4:43:22 
 * ��˵�� 
 */

public class AdminEntity {
	
	public static final String[] PERMISSIONS = {"����֪ͨ/","��ȡ����״̬/","���Ϳ�������/","�޸Ĳ���"};

	public static final int PERMSSION_RECV_NOTIFY = 0x01; 
	public static final int PERMSSION_READ_SENSOR = 0x02; 
	public static final int PERMSSION_SEND_CMD = 0x04; 
	public static final int PERMSSION_EDIT_PARM = 0x08; 
	
	public int id = -1;
	public String name = "";
	public String phone = "";
	public int permission = -1;
	
}
