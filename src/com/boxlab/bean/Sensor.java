package com.boxlab.bean;

import java.io.Serializable;
import java.text.DecimalFormat;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-17 下午3:22:13 
 * 类说明 
 */

public class Sensor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8509320338751971606L;

	public static DecimalFormat floatNum = new DecimalFormat("###0.00"); // 最多保留几位小数,就用几个#,最少位就用0来确定

	public final SensorBean sBean;
	
	public String sSensorType;   // final

	public int SensorData;       // 用来缓存计算结果
	public int SensorData2;      // 用来缓存计算结果
	public int SensorData3;      // 用来缓存计算结果
	public String sSensorData;   // 传感器数据字符串
	
	public float fPower;
	public String sPower;
	
	public float Volatage;
	public float Temperature;
	public float Humidity;
	
	public boolean Bool = false;

	public float xVal;

	public float yVal;

	public float zVal;

	public float kWh;

	public float Light;

	public float Distance;

	public float Pressure;

	public float Concentration;

	private float DewPoint;

	public interface SensorObserver{

		public void onSensorDataUpdata(Sensor s);
		public void onSensorDataTrigger(Sensor s);
		public void onBoolValueOn(Sensor s);
		public void onBoolValueOff(Sensor s);
		
	}
	
	public Sensor(SensorBean sbean, boolean isUpdata) {
		this.sBean = sbean;
		if(isUpdata){
			this.sSensorType = StringUtil.getStringFormatS_TP(this.sBean.iSensorType);
			updataSensorData(this);
		}
	}

	public static void updataSensorType(Sensor sensor, SensorBean sbean) {
		sensor.sSensorType = StringUtil.getStringFormatS_TP(sbean.iSensorType);
	}
	
	public static boolean updataSensorData(Sensor sensor) {
		
		if(sensor == null || sensor.sBean == null || sensor.sBean.aSensorData == null)
			return false;

		// 传感器的数据
		final byte[] DecPacket = sensor.sBean.aSensorData;
		int i = 0;
		boolean isUpdate = true;
		switch (sensor.sBean.iSensorType) {
		
		case FrameUtil.SENSOR_TYPE_ID_LM35DZ:
			sensor.SensorData = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
			sensor.Temperature = (float) (((float) sensor.SensorData * 3.3 / 2048) / 0.01);
			sensor.sSensorData = "温度:" + floatNum.format(sensor.Temperature) + "℃";
			break;

		case FrameUtil.SENSOR_TYPE_ID_LS_RESISTANCE: // 光敏电阻传感器
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
			sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
			sensor.sSensorData = "光强:" + floatNum.format(sensor.Volatage) + "L";
			break;

		case FrameUtil.SENSOR_TYPE_ID_LS_DIODE: // 光敏二极管传感器
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
			sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
			sensor.sSensorData = "光强:" + floatNum.format(sensor.Volatage) + "L";
			break;

		case (byte) 0x04: // MQ-3酒精传感器
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.sSensorData = "浓度:" + floatNum.format(sensor.Volatage) + "%";
			break;

		case (byte) 0x05: // MQ-135空气质量传感器(氨气/硫化物/苯系蒸汽)
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.sSensorData = "质量:" + floatNum.format(sensor.Volatage) + "%";
			break;

		case (byte) 0x06: // MQ-2传感器(检测液化气/丙烷/氢气)
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.sSensorData = "浓度:" + floatNum.format(sensor.Volatage) + "%";
			break;

		case (byte) 0x07: // HC-SR501人体红外传感器
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if(DecPacket[i]==(byte)0x01){
				sensor.sSensorData  = "有人";
				if(sensor.Bool != true){
					sensor.Bool = true;           // TODO 触发二值信号
				}else{
					isUpdate = false;
				}
			}else if(DecPacket[i]==(byte)0x00){
				sensor.sSensorData  = "无人";
				if(sensor.Bool != false){
					sensor.Bool = false;           // TODO 触发二值信号
				}else{
					isUpdate = false;
				}
			}
			break;

		case (byte) 0x08: // 直流马达模块
			sensor.SensorData = (int) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.SensorData2 = (short) (DecPacket[i] & 0xff);
		    sensor.SensorData3 = (short) (DecPacket[i + 1] & 0xff);
		    sensor.sSensorData =  "信号1占空比:" + sensor.SensorData2 + "%\n"
					            + "信号2占空比:" + sensor.SensorData3 + "%\n"
					            + "PWM信号频率:" + sensor.SensorData + "Hz";
			break;

		case (byte) 0x09: // 温湿度传感器(低精度)
			sensor.SensorData = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.Temperature = (float) ((DecPacket[i + 2] & 0xff) + (float) (DecPacket[i + 3] & 0xff) / 10.0);
			
		    sensor.SensorData2 = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Humidity = (float) ((DecPacket[i] & 0xff) + (float) (DecPacket[i + 1] & 0xff) / 10.0);
			
		    sensor.sSensorData = "温度:" + floatNum.format(sensor.Temperature) + "℃"
					+ "湿度:" + floatNum.format(sensor.Humidity) + "%";
			break;

		case (byte) 0x0a: // ADXL345三轴数字加速度传感器
			sensor.SensorData = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.xVal = (float) (sensor.SensorData * 3.9 / 1000.0);
		    sensor.SensorData2 = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.yVal = (float) (sensor.SensorData2 * 3.9 / 1000.0);
		    sensor.SensorData3 = (short) (((DecPacket[i + 4] & 0xff) << 8) + ((DecPacket[i + 5]) & 0xff));
		    sensor.zVal = (float) (sensor.SensorData3 * 3.9 / 1000.0);
		    
		    sensor.sSensorData = "X轴:" + floatNum.format(sensor.xVal) + "g" + "\n" + 
		                         "Y轴:" 	+ floatNum.format(sensor.yVal) + "g" + "\n" + 
		    		             "Z轴:" +  floatNum.format(sensor.zVal) + "g";
			break;

		case (byte) 0x0b: // SHT10温湿度传感器(高精度)
			sensor.SensorData = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.Temperature = (float) (-39.70 + 0.01 * sensor.SensorData);
			
		    sensor.SensorData2 = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Humidity = (float) (-2.0468 + 0.0367 * sensor.SensorData2 + (-1.5955E-6) * sensor.SensorData2 * sensor.SensorData2);
		    sensor.Humidity = (float) ((sensor.Temperature - 25) * (0.01 - 0.00008 * sensor.Humidity) + sensor.Humidity);
		    sensor.sSensorData = "温度:" + floatNum.format(sensor.Temperature) + "℃"
					+ "湿度:" + floatNum.format(sensor.Humidity) + "%";
			break;

		case (byte) 0x0c: // L3G4200D 三轴数字陀螺仪传感器
			sensor.SensorData = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.xVal = (float) (sensor.SensorData * 8.75 / 1000.0);
		    sensor.SensorData2 = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.yVal = (float) (sensor.SensorData2 * 8.75 / 1000.0);
		    sensor.SensorData3 = (short) (((DecPacket[i + 4] & 0xff) << 8) + ((DecPacket[i + 5]) & 0xff));
		    sensor.zVal = (float) (sensor.SensorData3 * 8.75 / 1000.0);
			
		    sensor.sSensorData = "X轴:" + floatNum.format(sensor.xVal) + "dps" + "\n"
					           + "Y轴:" + floatNum.format(sensor.yVal) + "dps" + "\n" 
		    		           + "Z轴:" + floatNum.format(sensor.zVal) + "dps";
			break;
			
		case (byte) 0x0d:// 节点IO状态
			sensor.SensorData = (int) ((DecPacket[i + 1] & 0xff) << 8) + (DecPacket[i] & 0xff);
		    if(sensor.SensorData2 != sensor.SensorData){
			    sensor.SensorData2 = sensor.SensorData;              // TODO 触发开关信号
			    
				//sensor.sSensorData = ioState(DecPacket, i);
			    sensor.sSensorData = ioState(DecPacket[i] & 0xFF, DecPacket[i + 1] & 0xFF);
		    }else{
		    	isUpdate = false;
		    }
			break;
			
		case (byte) 0x0e:// 智能电表
			sensor.sSensorData = Integer.toHexString(((DecPacket[i] >> 4) & 0x0f) * 100000 + (DecPacket[i] & 0x0f) * 10000 + 
				 ((DecPacket[i + 1] >> 4) & 0x0f) * 1000 + (DecPacket[i + 1] & 0x0f) * 100 + 
		         ((DecPacket[i + 2] >> 4) & 0x0f) * 10 + (DecPacket[i + 2] & 0x0f) * 1) + "." + 			         
		         Integer.toHexString((DecPacket[i + 3] >> 4) & 0x0f) + Integer.toHexString(DecPacket[i + 3] & 0x0f) +  "kWh";
		    
		    sensor.kWh = (float) ((((DecPacket[i] >> 4) & 0x0f) * 100000 + (DecPacket[i] & 0x0f) * 10000 + 
					 ((DecPacket[i + 1] >> 4) & 0x0f) * 1000 + (DecPacket[i + 1] & 0x0f) * 100 + 
			         ((DecPacket[i + 2] >> 4) & 0x0f) * 10 + (DecPacket[i + 2] & 0x0f) * 1 ) + 
			         ((DecPacket[i + 3] >> 4) & 0x0f) * 0.1 + (DecPacket[i + 3] & 0x0f) * 0.01);
		
			break;
			
		case (byte) 0x0f:// 远程遥控设备
			sensor.SensorData = (int)(DecPacket[i] & 0xff);
			sensor.sSensorData = remoteControl(DecPacket, i);
			break;
		
		case (byte) 0x11: // 振动传感器
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "检测到振动";;
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "未检测到振动";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case (byte) 0x13: // BH1750FVI光照传感器
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) <<8)+((DecPacket[i+1]) & 0xff));
		    sensor.Light = (float) (sensor.SensorData);
		    sensor.sSensorData = "光强:" + sensor.SensorData + "L";
		    
		break;
		
		case (byte) 0x14: // 声音传感器
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "检测到声音";;
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "未检测到声音";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
		
		case (byte) 0x15: // 红外测距传感器
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.Distance = (float) (26.757 * Math.pow(sensor.Volatage, -1.236));
		    sensor.sSensorData = "距离:" + floatNum.format(sensor.Distance) + "cm";
			break;
		
		case (byte) 0x17: // 继电器设备

			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "高电平";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "低电平";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;

		case (byte) 0x18://气压传感器
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) <<8)+((DecPacket[i+1]) & 0xff));
		    sensor.Temperature = (float) (sensor.SensorData /10.0);
		    sensor.SensorData = (int) (((DecPacket[i+2] & 0xff) << 24) + ((DecPacket[i + 3] & 0xff) <<16) + 
					((DecPacket[i + 4] & 0xff) << 8) + (DecPacket[i + 5] & 0xff));
		    sensor.Pressure = (float) (sensor.SensorData /100.0);
		    sensor.sSensorData = "压强:" + floatNum.format(sensor.Pressure) + "hPa" +
		             "温度:" + floatNum.format(sensor.Temperature) + "℃";
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IO_LED:
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_INNER_TS:

			break;
			
		case FrameUtil.SENSOR_TYPE_ID_LIGHT:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "亮";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "灭";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_FAN:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "开";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "关";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "开锁";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "关锁";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WATER_PUMP:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "开始抽水";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "停止抽水";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_HEATER:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "开始加热";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "停止加热";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_AV_ANNUNCIATOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "开始报警";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "停止报警";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SOCKET:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "通电";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "断电";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_RFID_LF:
			if((DecPacket[0] == (byte)0xFE) && (DecPacket[1] == (byte)0xFE)){
				sensor.sSensorData = "未识别到RFID卡";
				return true;
			}
			sensor.SensorData = FrameUtil.buildInt(DecPacket, i);
			sensor.SensorData2 = FrameUtil.buildInt(DecPacket, i + 4);
			sensor.SensorData3 = FrameUtil.buildShort(DecPacket, i + 8);
			sensor.sSensorData = "RFID卡：" + new String(DecPacket);
//			                     + StringUtil.getHexStringFormatInt(sensor.SensorData)
//			                     + StringUtil.getHexStringFormatInt(sensor.SensorData2)
//			                     + StringUtil.getHexStringFormatShort(sensor.SensorData3);
					                                           // TODO 触发二值信号
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_CO2:

			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.Concentration = (float) ((sensor.Volatage / 2.5) * 5000.0);
		    sensor.sSensorData = "浓度:\n" + floatNum.format(sensor.Concentration) + "ppm";
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SOIL:

			sensor.SensorData = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.Temperature = (float) (-39.70 + 0.01 * sensor.SensorData);
			
		    sensor.SensorData2 = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Humidity = (float) (-2.0468 + 0.0367 * sensor.SensorData2 + (-1.5955E-6) * sensor.SensorData2 * sensor.SensorData2);
		    sensor.Humidity = (float) ((sensor.Temperature - 25) * (0.01 - 0.00008 * sensor.Humidity) + sensor.Humidity);
		    sensor.sSensorData = "温度:" + floatNum.format(sensor.Temperature) + "℃"
					+ "湿度:" + floatNum.format(sensor.Humidity) + "%";
			break;
			
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "门被打开";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "门已关上";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IR_DETECTOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "探测到人体";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "未探测到人体";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_GAS_DETECTOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "检测到可燃气体";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "未检测到可燃气体";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IR_FENCE:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "检测到红外线被阻断";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "红外线未被阻断";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SMOKE_ALARM:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "检测到烟雾";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "未检测到烟雾";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS232:
			if((DecPacket[0] == (byte)0xFE) && (DecPacket[1] == (byte)0xFE)){
				return false;
			}
			sensor.SensorData = (short)((DecPacket[i] & 0xFF) << 8) + (DecPacket[i + 1] & 0xFF);
			sensor.Temperature = (float) (sensor.SensorData / 10.0);
			sensor.SensorData2 = (short)((DecPacket[i + 2] & 0xFF) << 8) + (DecPacket[i + 3] & 0xFF);
			sensor.Humidity = (float) (sensor.SensorData2 / 10.0);
			sensor.SensorData3 = (short)((DecPacket[i + 4] & 0xFF) << 8) + (DecPacket[i + 5] & 0xFF);
			sensor.DewPoint = (float) (sensor.SensorData3 / 10.0);
			sensor.sSensorData =  "温度:" + floatNum.format(sensor.Temperature) + "℃"
					+ " 湿度:" + floatNum.format(sensor.Humidity) + "%" 
					+ " 露点:" + floatNum.format(sensor.DewPoint) + "℃" ;
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485:
			if((DecPacket[0] == (byte)0xFE) && (DecPacket[1] == (byte)0xFE)){
				return false;
			}
			sensor.SensorData = (short)((DecPacket[i + 2] & 0xFF) << 8) + (DecPacket[i + 3] & 0xFF);
			sensor.Temperature = (float) (sensor.SensorData / 10.0);
			sensor.SensorData2 = (short)((DecPacket[i] & 0xFF) << 8) + (DecPacket[i + 1] & 0xFF);
			sensor.Humidity = (float) (sensor.SensorData2 / 10.0);
			sensor.sSensorData =  "温度:" + floatNum.format(sensor.Temperature) + "℃"
					+ " 湿度:" + floatNum.format(sensor.Humidity) + "%";
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "打开窗帘";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "关闭窗帘";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO 触发二值信号
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case (byte) 0xfe: // 无效传感器
			//sensor.sSensorType = "无效传感器";
			sensor.sSensorData = "无效传感器数据";
		    isUpdate = false;
			break;
			
		default:
			//sensor.sSensorType = "##Error##传感器类型未定义，请校验";
			sensor.sSensorData = "无效传感器数据";
		    isUpdate = false;
			break;
		}
		return isUpdate;
	}
	
	/**
	 * 判断节点IO状态：输入还是输出、高电平还是低电平
	 * 
	 * @param DecPacket
	 * @param i
	 */
//	public static String ioState(final byte[] DecPacket, int i) {
//		int ioDirec = DecPacket[i] & 0xFF;
//		int ioLevel = DecPacket[i + 1] & 0xFF;

	public static String ioState(int iDir, int iLev) {
		int ioDirec = iDir & 0xFF;
		int ioLevel = iLev & 0xFF;
		final StringBuilder stringBuilder = new StringBuilder();
		String StrPIN_NUM = "";

		for (int j = 0; j < 2; j++) {
			if (j == 0) {
				StrPIN_NUM = "\nP0.";
			} else {
				StrPIN_NUM = "\nP1.";
			}
			for (int n = 0; n < 4; n++) {
				if ((ioDirec & 0x01) != 0) {
					stringBuilder.append(StrPIN_NUM + (n + 4) + "输出口:");
					if ((ioLevel & 0x01) != 0) {
						stringBuilder.append("低电平;");
					} else {
						stringBuilder.append("高电平;");
					}
				} else {
					stringBuilder.append(StrPIN_NUM + (n + 4) + "输入口:");
					if ((ioLevel & 0x01) != 0) {
						stringBuilder.append("低电平;");
					} else {
						stringBuilder.append("高电平;");
					}
				}
				if (n == 3) {
				}
				ioDirec = (ioDirec & 0xFF) >> 1;
				ioLevel = (ioLevel & 0xFF) >> 1;
			}
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * 判断远程遥控设备遥控类型
	 * @param DecPacket
	 * @param i
	 * @return
	 */
	public static String remoteControl(byte[] DecPacket, int i){
		String str = "0x"+StringUtil.getHexStringFormatByte(DecPacket[i]);
		String deviceName = "";
		if("0x00".equals(str)){
			deviceName = "电视机+机顶盒";
		}else if("0x01".equals(str)){
			deviceName = "空调";
		}else if("0x02".equals(str)){
			deviceName = "DVD或高清播放器";
		}else if("0x03".equals(str)){
			deviceName = "电灯";
		}else if("0x04".equals(str)){
			deviceName = "电风扇";
		}else if("0xfe".equals(str)){
			deviceName = "无效遥控设备";
		}
		return deviceName;
	}
	
	public static void updataPower(Sensor sensor) {
		sensor.fPower = (float) ((1.15 * sensor.sBean.iPower) * 3.0 / 2048.0);
		sensor.sPower = floatNum.format(sensor.fPower);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "-------------------------传感器解析数据-------------------------" 
				+ "\n  节点地址     : " + StringUtil.getHexStringFormatShort(this.sBean.iCna) 
				+ "\n  传感器类型  : " + this.sSensorType
				+ "\n  传感器数据  : " + this.sSensorData
//				+ "\n  Power Level:" + this.sPower
				;
	}
	

//	switch (mSensor.sBean.iSensorType) {
//	
//	case FrameUtil.SENSOR_TYPE_ID_LM35DZ:
//		break;
//
//	case FrameUtil.SENSOR_TYPE_ID_LS_RESISTANCE: // 光敏电阻传感器
//		break;
//
//	case FrameUtil.SENSOR_TYPE_ID_LS_DIODE: // 光敏二极管传感器
//		break;
//
//	case (byte) 0x04: // MQ-3酒精传感器
//		break;
//
//	case (byte) 0x05: // MQ-135空气质量传感器(氨气/硫化物/苯系蒸汽)
//		break;
//
//	case (byte) 0x06: // MQ-2传感器(检测液化气/丙烷/氢气)
//		break;
//
//	case (byte) 0x07: // HC-SR501人体红外传感器
//		break;
//
//	case (byte) 0x08: // 直流马达模块
//		break;
//
//	case (byte) 0x09: // 温湿度传感器(低精度)
//		break;
//
//	case (byte) 0x0a: // ADXL345三轴数字加速度传感器
//		break;
//
//	case (byte) 0x0b: // SHT10温湿度传感器(高精度)
//		break;
//
//	case (byte) 0x0c: // L3G4200D 三轴数字陀螺仪传感器
//		break;
//		
//	case (byte) 0x0d:// 节点IO状态
//		break;
//		
//	case (byte) 0x0e:// 智能电表
//		break;
//		
//	case (byte) 0x0f:// 远程遥控设备
//		break;
//	
//	case (byte) 0x11: // 振动传感器
//		break;
//		
//	case (byte) 0x13: // BH1750FVI光照传感器		    
//	    break;
//	
//	case (byte) 0x14: // 声音传感器
//		break;
//	
//	case (byte) 0x15: // 红外测距传感器
//		break;
//	
//	case (byte) 0x17: // 继电器设备
//		break;
//
//	case (byte) 0x18://气压传感器
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_IO_LED:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_INNER_TS:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_LIGHT:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_FAN:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_WATER_PUMP:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_HEATER:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_AV_ANNUNCIATOR:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_SOCKET:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_RFID_LF:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_CO2:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_SOIL:
//		break;
//		
//		
//	case FrameUtil.SENSOR_TYPE_ID_DOOR:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_IR_DETECTOR:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_GAS_DETECTOR:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_IR_FENCE:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_SMOKE_ALARM:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS232:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485:
//		break;
//		
//	case FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN:
//		break;
//		
//	case (byte) 0xfe: // 无效传感器
//		break;
//		
//	default:
//		break;
//		
//	}
	
}


