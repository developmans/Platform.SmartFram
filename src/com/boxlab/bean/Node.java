package com.boxlab.bean;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-10-28 ����3:33:23 
 * ��˵�� 
 */

public class Node {
	
	public ZigBee mZigBee;
	public Sensor mSensor;

	public Node(ZigBee zigbee, Sensor sensor) {
		// TODO Auto-generated constructor stub
		this.mZigBee = zigbee;
		this.mSensor = sensor;
	}
}
