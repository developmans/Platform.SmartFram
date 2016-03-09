package com.boxlab.bean;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-10-28 下午3:33:23 
 * 类说明 
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
