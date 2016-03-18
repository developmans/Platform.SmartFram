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
 * ����ʱ�䣺2015-9-17 ����3:22:13 
 * ��˵�� 
 */

public class Sensor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8509320338751971606L;

	public static DecimalFormat floatNum = new DecimalFormat("###0.00"); // ��ౣ����λС��,���ü���#,����λ����0��ȷ��

	public final SensorBean sBean;
	
	public String sSensorType;   // final

	public int SensorData;       // �������������
	public int SensorData2;      // �������������
	public int SensorData3;      // �������������
	public String sSensorData;   // �����������ַ���
	
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

		// ������������
		final byte[] DecPacket = sensor.sBean.aSensorData;
		int i = 0;
		boolean isUpdate = true;
		switch (sensor.sBean.iSensorType) {
		
		case FrameUtil.SENSOR_TYPE_ID_LM35DZ:
			sensor.SensorData = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
			sensor.Temperature = (float) (((float) sensor.SensorData * 3.3 / 2048) / 0.01);
			sensor.sSensorData = "�¶�:" + floatNum.format(sensor.Temperature) + "��";
			break;

		case FrameUtil.SENSOR_TYPE_ID_LS_RESISTANCE: // �������贫����
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
			sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
			sensor.sSensorData = "��ǿ:" + floatNum.format(sensor.Volatage) + "L";
			break;

		case FrameUtil.SENSOR_TYPE_ID_LS_DIODE: // ���������ܴ�����
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
			sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
			sensor.sSensorData = "��ǿ:" + floatNum.format(sensor.Volatage) + "L";
			break;

		case (byte) 0x04: // MQ-3�ƾ�������
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.sSensorData = "Ũ��:" + floatNum.format(sensor.Volatage) + "%";
			break;

		case (byte) 0x05: // MQ-135��������������(����/����/��ϵ����)
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.sSensorData = "����:" + floatNum.format(sensor.Volatage) + "%";
			break;

		case (byte) 0x06: // MQ-2������(���Һ����/����/����)
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.sSensorData = "Ũ��:" + floatNum.format(sensor.Volatage) + "%";
			break;

		case (byte) 0x07: // HC-SR501������⴫����
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if(DecPacket[i]==(byte)0x01){
				sensor.sSensorData  = "����";
				if(sensor.Bool != true){
					sensor.Bool = true;           // TODO ������ֵ�ź�
				}else{
					isUpdate = false;
				}
			}else if(DecPacket[i]==(byte)0x00){
				sensor.sSensorData  = "����";
				if(sensor.Bool != false){
					sensor.Bool = false;           // TODO ������ֵ�ź�
				}else{
					isUpdate = false;
				}
			}
			break;

		case (byte) 0x08: // ֱ�����ģ��
			sensor.SensorData = (int) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.SensorData2 = (short) (DecPacket[i] & 0xff);
		    sensor.SensorData3 = (short) (DecPacket[i + 1] & 0xff);
		    sensor.sSensorData =  "�ź�1ռ�ձ�:" + sensor.SensorData2 + "%\n"
					            + "�ź�2ռ�ձ�:" + sensor.SensorData3 + "%\n"
					            + "PWM�ź�Ƶ��:" + sensor.SensorData + "Hz";
			break;

		case (byte) 0x09: // ��ʪ�ȴ�����(�;���)
			sensor.SensorData = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.Temperature = (float) ((DecPacket[i + 2] & 0xff) + (float) (DecPacket[i + 3] & 0xff) / 10.0);
			
		    sensor.SensorData2 = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Humidity = (float) ((DecPacket[i] & 0xff) + (float) (DecPacket[i + 1] & 0xff) / 10.0);
			
		    sensor.sSensorData = "�¶�:" + floatNum.format(sensor.Temperature) + "��"
					+ "ʪ��:" + floatNum.format(sensor.Humidity) + "%";
			break;

		case (byte) 0x0a: // ADXL345�������ּ��ٶȴ�����
			sensor.SensorData = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.xVal = (float) (sensor.SensorData * 3.9 / 1000.0);
		    sensor.SensorData2 = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.yVal = (float) (sensor.SensorData2 * 3.9 / 1000.0);
		    sensor.SensorData3 = (short) (((DecPacket[i + 4] & 0xff) << 8) + ((DecPacket[i + 5]) & 0xff));
		    sensor.zVal = (float) (sensor.SensorData3 * 3.9 / 1000.0);
		    
		    sensor.sSensorData = "X��:" + floatNum.format(sensor.xVal) + "g" + "\n" + 
		                         "Y��:" 	+ floatNum.format(sensor.yVal) + "g" + "\n" + 
		    		             "Z��:" +  floatNum.format(sensor.zVal) + "g";
			break;

		case (byte) 0x0b: // SHT10��ʪ�ȴ�����(�߾���)
			sensor.SensorData = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.Temperature = (float) (-39.70 + 0.01 * sensor.SensorData);
			
		    sensor.SensorData2 = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Humidity = (float) (-2.0468 + 0.0367 * sensor.SensorData2 + (-1.5955E-6) * sensor.SensorData2 * sensor.SensorData2);
		    sensor.Humidity = (float) ((sensor.Temperature - 25) * (0.01 - 0.00008 * sensor.Humidity) + sensor.Humidity);
		    sensor.sSensorData = "�¶�:" + floatNum.format(sensor.Temperature) + "��"
					+ "ʪ��:" + floatNum.format(sensor.Humidity) + "%";
			break;

		case (byte) 0x0c: // L3G4200D �������������Ǵ�����
			sensor.SensorData = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.xVal = (float) (sensor.SensorData * 8.75 / 1000.0);
		    sensor.SensorData2 = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.yVal = (float) (sensor.SensorData2 * 8.75 / 1000.0);
		    sensor.SensorData3 = (short) (((DecPacket[i + 4] & 0xff) << 8) + ((DecPacket[i + 5]) & 0xff));
		    sensor.zVal = (float) (sensor.SensorData3 * 8.75 / 1000.0);
			
		    sensor.sSensorData = "X��:" + floatNum.format(sensor.xVal) + "dps" + "\n"
					           + "Y��:" + floatNum.format(sensor.yVal) + "dps" + "\n" 
		    		           + "Z��:" + floatNum.format(sensor.zVal) + "dps";
			break;
			
		case (byte) 0x0d:// �ڵ�IO״̬
			sensor.SensorData = (int) ((DecPacket[i + 1] & 0xff) << 8) + (DecPacket[i] & 0xff);
		    if(sensor.SensorData2 != sensor.SensorData){
			    sensor.SensorData2 = sensor.SensorData;              // TODO ���������ź�
			    
				//sensor.sSensorData = ioState(DecPacket, i);
			    sensor.sSensorData = ioState(DecPacket[i] & 0xFF, DecPacket[i + 1] & 0xFF);
		    }else{
		    	isUpdate = false;
		    }
			break;
			
		case (byte) 0x0e:// ���ܵ��
			sensor.sSensorData = Integer.toHexString(((DecPacket[i] >> 4) & 0x0f) * 100000 + (DecPacket[i] & 0x0f) * 10000 + 
				 ((DecPacket[i + 1] >> 4) & 0x0f) * 1000 + (DecPacket[i + 1] & 0x0f) * 100 + 
		         ((DecPacket[i + 2] >> 4) & 0x0f) * 10 + (DecPacket[i + 2] & 0x0f) * 1) + "." + 			         
		         Integer.toHexString((DecPacket[i + 3] >> 4) & 0x0f) + Integer.toHexString(DecPacket[i + 3] & 0x0f) +  "kWh";
		    
		    sensor.kWh = (float) ((((DecPacket[i] >> 4) & 0x0f) * 100000 + (DecPacket[i] & 0x0f) * 10000 + 
					 ((DecPacket[i + 1] >> 4) & 0x0f) * 1000 + (DecPacket[i + 1] & 0x0f) * 100 + 
			         ((DecPacket[i + 2] >> 4) & 0x0f) * 10 + (DecPacket[i + 2] & 0x0f) * 1 ) + 
			         ((DecPacket[i + 3] >> 4) & 0x0f) * 0.1 + (DecPacket[i + 3] & 0x0f) * 0.01);
		
			break;
			
		case (byte) 0x0f:// Զ��ң���豸
			sensor.SensorData = (int)(DecPacket[i] & 0xff);
			sensor.sSensorData = remoteControl(DecPacket, i);
			break;
		
		case (byte) 0x11: // �񶯴�����
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��⵽��";;
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "δ��⵽��";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case (byte) 0x13: // BH1750FVI���մ�����
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) <<8)+((DecPacket[i+1]) & 0xff));
		    sensor.Light = (float) (sensor.SensorData);
		    sensor.sSensorData = "��ǿ:" + sensor.SensorData + "L";
		    
		break;
		
		case (byte) 0x14: // ����������
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��⵽����";;
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "δ��⵽����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
		
		case (byte) 0x15: // �����ഫ����
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.Distance = (float) (26.757 * Math.pow(sensor.Volatage, -1.236));
		    sensor.sSensorData = "����:" + floatNum.format(sensor.Distance) + "cm";
			break;
		
		case (byte) 0x17: // �̵����豸

			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "�ߵ�ƽ";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "�͵�ƽ";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;

		case (byte) 0x18://��ѹ������
			sensor.SensorData = (int) (((DecPacket[i] & 0xff) <<8)+((DecPacket[i+1]) & 0xff));
		    sensor.Temperature = (float) (sensor.SensorData /10.0);
		    sensor.SensorData = (int) (((DecPacket[i+2] & 0xff) << 24) + ((DecPacket[i + 3] & 0xff) <<16) + 
					((DecPacket[i + 4] & 0xff) << 8) + (DecPacket[i + 5] & 0xff));
		    sensor.Pressure = (float) (sensor.SensorData /100.0);
		    sensor.sSensorData = "ѹǿ:" + floatNum.format(sensor.Pressure) + "hPa" +
		             "�¶�:" + floatNum.format(sensor.Temperature) + "��";
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IO_LED:
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_INNER_TS:

			break;
			
		case FrameUtil.SENSOR_TYPE_ID_LIGHT:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "��";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_FAN:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "��";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "����";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WATER_PUMP:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��ʼ��ˮ";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "ֹͣ��ˮ";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_HEATER:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��ʼ����";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "ֹͣ����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_AV_ANNUNCIATOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��ʼ����";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "ֹͣ����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SOCKET:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "ͨ��";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "�ϵ�";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_RFID_LF:
			if((DecPacket[0] == (byte)0xFE) && (DecPacket[1] == (byte)0xFE)){
				sensor.sSensorData = "δʶ��RFID��";
				return true;
			}
			sensor.SensorData = FrameUtil.buildInt(DecPacket, i);
			sensor.SensorData2 = FrameUtil.buildInt(DecPacket, i + 4);
			sensor.SensorData3 = FrameUtil.buildShort(DecPacket, i + 8);
			sensor.sSensorData = "RFID����" + new String(DecPacket);
//			                     + StringUtil.getHexStringFormatInt(sensor.SensorData)
//			                     + StringUtil.getHexStringFormatInt(sensor.SensorData2)
//			                     + StringUtil.getHexStringFormatShort(sensor.SensorData3);
					                                           // TODO ������ֵ�ź�
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_CO2:

			sensor.SensorData = (int) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Volatage = (float) ((float) sensor.SensorData * 3.3 / 2048);
		    sensor.Concentration = (float) ((sensor.Volatage / 2.5) * 5000.0);
		    sensor.sSensorData = "Ũ��:\n" + floatNum.format(sensor.Concentration) + "ppm";
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SOIL:

			sensor.SensorData = (short) (((DecPacket[i + 2] & 0xff) << 8) + ((DecPacket[i + 3]) & 0xff));
		    sensor.Temperature = (float) (-39.70 + 0.01 * sensor.SensorData);
			
		    sensor.SensorData2 = (short) (((DecPacket[i] & 0xff) << 8) + ((DecPacket[i + 1]) & 0xff));
		    sensor.Humidity = (float) (-2.0468 + 0.0367 * sensor.SensorData2 + (-1.5955E-6) * sensor.SensorData2 * sensor.SensorData2);
		    sensor.Humidity = (float) ((sensor.Temperature - 25) * (0.01 - 0.00008 * sensor.Humidity) + sensor.Humidity);
		    sensor.sSensorData = "�¶�:" + floatNum.format(sensor.Temperature) + "��"
					+ "ʪ��:" + floatNum.format(sensor.Humidity) + "%";
			break;
			
			
		case FrameUtil.SENSOR_TYPE_ID_DOOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "�ű���";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "���ѹ���";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IR_DETECTOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "̽�⵽����";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "δ̽�⵽����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_GAS_DETECTOR:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��⵽��ȼ����";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "δ��⵽��ȼ����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_IR_FENCE:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��⵽�����߱����";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "������δ�����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SMOKE_ALARM:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "��⵽����";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "δ��⵽����";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
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
			sensor.sSensorData =  "�¶�:" + floatNum.format(sensor.Temperature) + "��"
					+ " ʪ��:" + floatNum.format(sensor.Humidity) + "%" 
					+ " ¶��:" + floatNum.format(sensor.DewPoint) + "��" ;
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485:
			if((DecPacket[0] == (byte)0xFE) && (DecPacket[1] == (byte)0xFE)){
				return false;
			}
			sensor.SensorData = (short)((DecPacket[i + 2] & 0xFF) << 8) + (DecPacket[i + 3] & 0xFF);
			sensor.Temperature = (float) (sensor.SensorData / 10.0);
			sensor.SensorData2 = (short)((DecPacket[i] & 0xFF) << 8) + (DecPacket[i + 1] & 0xFF);
			sensor.Humidity = (float) (sensor.SensorData2 / 10.0);
			sensor.sSensorData =  "�¶�:" + floatNum.format(sensor.Temperature) + "��"
					+ " ʪ��:" + floatNum.format(sensor.Humidity) + "%";
			break;
			
		case FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN:
			sensor.SensorData = (int) (DecPacket[i] & 0xFF);
			if (DecPacket[i] == (byte) 0x01) {
				sensor.sSensorData = "�򿪴���";
				if (sensor.Bool != true) {
					sensor.Bool = true;                         // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			} else if (DecPacket[i] == (byte) 0x00) {
				sensor.sSensorData = "�رմ���";
				if (sensor.Bool != false) {
					sensor.Bool = false;                       // TODO ������ֵ�ź�
				}else{
			    	isUpdate = false;
			    }
			}
			break;
			
		case (byte) 0xfe: // ��Ч������
			//sensor.sSensorType = "��Ч������";
			sensor.sSensorData = "��Ч����������";
		    isUpdate = false;
			break;
			
		default:
			//sensor.sSensorType = "##Error##����������δ���壬��У��";
			sensor.sSensorData = "��Ч����������";
		    isUpdate = false;
			break;
		}
		return isUpdate;
	}
	
	/**
	 * �жϽڵ�IO״̬�����뻹��������ߵ�ƽ���ǵ͵�ƽ
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
					stringBuilder.append(StrPIN_NUM + (n + 4) + "�����:");
					if ((ioLevel & 0x01) != 0) {
						stringBuilder.append("�͵�ƽ;");
					} else {
						stringBuilder.append("�ߵ�ƽ;");
					}
				} else {
					stringBuilder.append(StrPIN_NUM + (n + 4) + "�����:");
					if ((ioLevel & 0x01) != 0) {
						stringBuilder.append("�͵�ƽ;");
					} else {
						stringBuilder.append("�ߵ�ƽ;");
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
	 * �ж�Զ��ң���豸ң������
	 * @param DecPacket
	 * @param i
	 * @return
	 */
	public static String remoteControl(byte[] DecPacket, int i){
		String str = "0x"+StringUtil.getHexStringFormatByte(DecPacket[i]);
		String deviceName = "";
		if("0x00".equals(str)){
			deviceName = "���ӻ�+������";
		}else if("0x01".equals(str)){
			deviceName = "�յ�";
		}else if("0x02".equals(str)){
			deviceName = "DVD����岥����";
		}else if("0x03".equals(str)){
			deviceName = "���";
		}else if("0x04".equals(str)){
			deviceName = "�����";
		}else if("0xfe".equals(str)){
			deviceName = "��Чң���豸";
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
		return "-------------------------��������������-------------------------" 
				+ "\n  �ڵ��ַ     : " + StringUtil.getHexStringFormatShort(this.sBean.iCna) 
				+ "\n  ����������  : " + this.sSensorType
				+ "\n  ����������  : " + this.sSensorData
//				+ "\n  Power Level:" + this.sPower
				;
	}
	

//	switch (mSensor.sBean.iSensorType) {
//	
//	case FrameUtil.SENSOR_TYPE_ID_LM35DZ:
//		break;
//
//	case FrameUtil.SENSOR_TYPE_ID_LS_RESISTANCE: // �������贫����
//		break;
//
//	case FrameUtil.SENSOR_TYPE_ID_LS_DIODE: // ���������ܴ�����
//		break;
//
//	case (byte) 0x04: // MQ-3�ƾ�������
//		break;
//
//	case (byte) 0x05: // MQ-135��������������(����/����/��ϵ����)
//		break;
//
//	case (byte) 0x06: // MQ-2������(���Һ����/����/����)
//		break;
//
//	case (byte) 0x07: // HC-SR501������⴫����
//		break;
//
//	case (byte) 0x08: // ֱ�����ģ��
//		break;
//
//	case (byte) 0x09: // ��ʪ�ȴ�����(�;���)
//		break;
//
//	case (byte) 0x0a: // ADXL345�������ּ��ٶȴ�����
//		break;
//
//	case (byte) 0x0b: // SHT10��ʪ�ȴ�����(�߾���)
//		break;
//
//	case (byte) 0x0c: // L3G4200D �������������Ǵ�����
//		break;
//		
//	case (byte) 0x0d:// �ڵ�IO״̬
//		break;
//		
//	case (byte) 0x0e:// ���ܵ��
//		break;
//		
//	case (byte) 0x0f:// Զ��ң���豸
//		break;
//	
//	case (byte) 0x11: // �񶯴�����
//		break;
//		
//	case (byte) 0x13: // BH1750FVI���մ�����		    
//	    break;
//	
//	case (byte) 0x14: // ����������
//		break;
//	
//	case (byte) 0x15: // �����ഫ����
//		break;
//	
//	case (byte) 0x17: // �̵����豸
//		break;
//
//	case (byte) 0x18://��ѹ������
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
//	case (byte) 0xfe: // ��Ч������
//		break;
//		
//	default:
//		break;
//		
//	}
	
}


