package com.boxlab.bean;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-11-9 下午4:43:22 
 * 类说明 
 */

public class AdminEntity {
	
	public static final String[] PERMISSIONS = {"接收通知/","获取数据状态/","发送控制命令/","修改参数"};

	public static final int PERMSSION_RECV_NOTIFY = 0x01; 
	public static final int PERMSSION_READ_SENSOR = 0x02; 
	public static final int PERMSSION_SEND_CMD = 0x04; 
	public static final int PERMSSION_EDIT_PARM = 0x08; 
	
	public int id = -1;
	public String name = "";
	public String phone = "";
	public int permission = -1;
	
}
