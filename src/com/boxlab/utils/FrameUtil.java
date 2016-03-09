package com.boxlab.utils;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-16 下午3:25:44 
 * 类说明 
 * 
 *  0x01LM35DZ线性模拟温度传感器：
 *  S.DA[0]，S.DA[1]
 *  
 * 	0x02光敏电阻传感器：
 * 	S.DA[0]，S.DA[1]
 * 
 * 	0x03光敏二极管传感器：
 * 	S.DA[0]，S.DA[1]
 * 
 * 	0x04MQ-3酒精传感器：
 * 	S.DA[0]，S.DA[1]
 * 
 * 	0x05MQ-135空气质量传感器：
 * 	S.DA[0]，S.DA[1]
 * 		0x06MQ-2可燃性气体传感器：
 * 	S.DA[0]，S.DA[1]
 * 
 * 	0x07HC-SR501人体红外感应传感器：
 * 	S.DA[0]0x00：未检测到人，0x01：检测到人
 * 
 * 	0x08直流马达模块
 * 	S.DA[0]PWM通道A占空比(逆时针)
 * 	S.DA[1]PWM通道B占空比(顺时针)
 * 	S.DA[2]，S.DA[3] PWM信号频率
 * 
 *  0x09DHT11温湿度传感器(低精度) ：
 *  S.DA[0]，S.DA[1]湿度数据
 *  S.DA[2]，S.DA[3]温度数据
 *  
 *  0x0AADXL345三轴数字加速度传感器
 *  S.DA[0]，S.DA[1]X轴加速度数据
 *  S.DA[2]，S.DA[3]Y轴加速度数据
 *  S.DA[4]，S.DA[5]Z轴加速度数据
 *  
 *  0x0BSHT10温湿度传感器(高精度)
 *  S.DA[0]，S.DA[1]湿度数据
 *  S.DA[2]，S.DA[3]温度数据
 *  
 *  0x0CL3G4200D三轴数字陀螺仪传感器
 *  S.DA[0]，S.DA[1]X轴角速度数据
 *  S.DA[2]，S.DA[3]Y轴角速度数据
 *  S.DA[4]，S.DA[5]Z轴角速度数据
 *  
 *  0x0D节点IO状态
 *  S.DA[0]：IO方向(0输入，1输出)
 *  S.DA[0].[7..4]：P1DIR.[7..4]
 *  S.DA[0].[3..0]：P0DIR.[7..4]
 *  S.DA[1]：IO状态(0低电平，1高电平)
 *  S.DA[1].[7..4]：P1.[7..4]
 *  S.DA[1].[3..0]：P0.[7..4]
 *  
 *  0x0E智能电表电量
 *  S.DA[0..4](单位：kwh)
 *  
 *  0x0F远程遥控设备
 *  S.DA[0]
 *  
 *  0x10MQ-7一氧化碳传感器
 *  S.DA[0]，S.DA[1]
 *  
 *  0x11振动传感器
 *  S.DA[0]0x00：未检测到振动，0x01：检测到振动
 *  
 *  0x12火焰传感器
 *  S.DA[0]0x00：未检测到火焰，0x01：检测到火焰
 *  
 *  0x13 BH1750FVI光照传感器
 *  S.DA[0]，S.DA[1]
 *  
 *  0x14声音传感器
 *  S.DA[0]0x00：未检测到声音，0x01：检测到声音
 *  
 *  0x15红外测距传感器
 *  S.DA[0]，S.DA[1]
 *  
 *  0x16DSM501A灰尘传感器
 *  S.DA[0]，S.DA[1]，S.DA[2]，S.DA[3]
 *  
 *  0x17继电器设备
 *  S.DA[0].0
 *  
 *  0x18气压传感器
 *  S.DA[0]，S.DA[1]温度数据
 *  S.DA[2]，S.DA[3], S.DA[4]，S.DA[5]气压数据
 *  
 *  0x19LED传感器
 *  S.DA[0].[3..0] LED的IO方向(1:输出，0:输入)
 *  S.DA[1].[3..0] LED的显示状态(1:熄灭，0:点亮)
 *  
 *  0x1A片内模拟温度传感器
 *  S.DA[0]，S.DA[1]温度数据
 *  
 *  0x1B电灯
 *  S.DA[0].[0]
 *  
 *  0x1C散热器风扇
 *  S.DA[0].[0]
 *  
 *  0x1D电子门锁
 *  S.DA[0].[0]
 *  
 *  0x1E微型水泵
 *  S.DA[0].[0]
 *  
 *  0x1F陶瓷加热器
 *  S.DA[0].[0]
 *  
 *  0x20声光报警器
 *  S.DA[0].[0]
 *  
 *  0x21遥控插座
 *  S.DA[0].[0]
 *  
 *  0x22低频RFID
 *  S.DA[0]，S.DA[9]RFID卡号(ASCII码)
 *  
 *  0x23二氧化碳传感器
 *  S.DA[0]，S.DA[1] 传感器数据
 *  
 *  0x24土壤温湿度传感器(SHT10)
 *  S.DA[0]，S.DA[1]湿度数据
 *  S.DA[2]，S.DA[3]温度数据
 *  
 *  0x25门磁检测器(1:检测到门被打开，0:未检测到门打开)
 *  S.DA[0].[0]
 *  
 *  0x26人体红外探测器(1:检测到人体，0:未检测到人体)
 *  S.DA[0].[0]
 *  
 *  0x27可燃气体检测器(1:检测到可燃气体，0:未检测到可燃气体)
 *  S.DA[0].[0]
 *  
 *  0x28红外电子栅栏(1:检测到红外线被遮断，0:检测到红外线未被遮断)
 *  S.DA[0].[0]
 *  
 *  0x29烟雾报警器(1:检测到烟雾，0:未检测到烟雾)
 *  S.DA[0].[0]
 *  
 *  0x2A串口温湿度变送器
 *  S.DA[0]，S.DA[1] 温度数据(16位有符号数除以10)
 *  S.DA[2]，S.DA[3] 湿度数据(16位有符号数除以10)
 *  S.DA[4]，S.DA[5] 露点数据(16位有符号数除以10)
 *  
 *  0x2BRS485温湿度变送器
 *  S.DA[0]，S.DA[1] 湿度数据(16位有符号数除以10)
 *  S.DA[2]，S.DA[3] 温度数据(16位有符号数除以10)
 *  
 *  0x2C电动窗帘(1:打开窗帘，0:闭合窗帘)
 *  S.DA[0].[0]
 *  
 *  0xFE无效传感器
 * 
 * 
***************************************************/
public class FrameUtil {
	
	public static final int SENSOR_TYPE_ID_UNDEFINE = 0xFE;
	public static final String SENSOR_TYPE_UNDEFINE = "无效传感器";
	
	public static final int SENSOR_TYPE_ID_LM35DZ = 0x01;
	public static final String SENSOR_TYPE_LM35DZ = "LM35DZ线性模拟温度传感器";

	public static final int SENSOR_TYPE_ID_LS_RESISTANCE = 0x02;
	public static final String SENSOR_TYPE_LS_RESISTANCE = "光敏电阻传感器";

	public static final int SENSOR_TYPE_ID_LS_DIODE = 0x03;
	public static final String SENSOR_TYPE_LS_DIODE = "光敏二极管传感器";
	
	public static final int SENSOR_TYPE_ID_MQ3 = 0x04;
	public static final String SENSOR_TYPE_MQ3 = "MQ-3酒精传感器";
	
	public static final int SENSOR_TYPE_ID_MQ5 = 0x05;
	public static final String SENSOR_TYPE_MQ5 = "MQ-135空气质量传感器";
	
	public static final int SENSOR_TYPE_ID_MQ2 = 0x06;//
	public static final String SENSOR_TYPE_MQ2 = "MQ-2可燃性气体传感器";

	public static final int SENSOR_TYPE_ID_HC_SR501 = 0x07;
	public static final String SENSOR_TYPE_HC_SR501 = "HC-SR501人体红外传感器";
	
	public static final int SENSOR_TYPE_ID_DC_MOTOR = 0x08;//
	public static final String SENSOR_TYPE_DC_MOTOR = "直流马达模块";

	public static final int SENSOR_TYPE_ID_DHT11 = 0x09;
	public static final String SENSOR_TYPE_DHT11 = "DHT11温湿度传感器(低精度) ";

	public static final int SENSOR_TYPE_ID_ADXL345 = 0x0A;
	public static final String SENSOR_TYPE_ADXL345 = "ADXL345三轴数字加速度传感器";

	public static final int SENSOR_TYPE_ID_SHT10 = 0x0B;
	public static final String SENSOR_TYPE_SHT10 = "SHT10温湿度传感器(高精度)";
	
	public static final int SENSOR_TYPE_ID_L3G4200D = 0x0C;
	public static final String SENSOR_TYPE_L3G4200D = "L3G4200D三轴数字陀螺仪传感器";
	
	public static final int SENSOR_TYPE_ID_PIN_IO = 0x0D;
	public static final String SENSOR_TYPE_PIN_IO = "节点IO状态";

	public static final int SENSOR_TYPE_ID_SMART_AMMETER = 0x0E;
	public static final String SENSOR_TYPE_SMART_AMMETER = "智能电表电量";

	public static final int SENSOR_TYPE_ID_REMOTE_CONTROL = 0x0F;
	public static final String SENSOR_TYPE_REMOTE_CONTROL = "远程遥控设备";

	public static final int SENSOR_TYPE_ID_MQ7 = 0x10;
	public static final String SENSOR_TYPE_MQ7 = "MQ-7一氧化碳传感器";
	
	public static final int SENSOR_TYPE_ID_VIBRATION = 0x11;
	public static final String SENSOR_TYPE_VIBRATION = "振动传感器";
	
	public static final int SENSOR_TYPE_ID_FLAME = 0x12;
	public static final String SENSOR_TYPE_FLAME = "火焰传感器";

	public static final int SENSOR_TYPE_ID_BH1750FVI = 0x13;
	public static final String SENSOR_TYPE_BH1750FVI = "BH1750FVI光照传感器";

	public static final int SENSOR_TYPE_ID_SOUND = 0x14;
	public static final String SENSOR_TYPE_SOUND = "声音传感器";

	public static final int SENSOR_TYPE_ID_IRDMS = 0x15;
	public static final String SENSOR_TYPE_IRDMS = "红外测距传感器";
	
	public static final int SENSOR_TYPE_ID_DSM501A = 0x16;
	public static final String SENSOR_TYPE_DSM501A = "DSM501A灰尘传感器";

	public static final int SENSOR_TYPE_ID_RELAY = 0x17;
	public static final String SENSOR_TYPE_RELAY = "继电器设备";
	
	public static final int SENSOR_TYPE_ID_BMP085 = 0x18;
	public static final String SENSOR_TYPE_BMP085 = "气压传感器";

	public static final int SENSOR_TYPE_ID_IO_LED = 0x19;
	public static final String SENSOR_TYPE_IO_LED = "LED传感器";

	public static final int SENSOR_TYPE_ID_INNER_TS = 0x1A;
	public static final String SENSOR_TYPE_INNER_TS = "片内模拟温度传感器";

	public static final int SENSOR_TYPE_ID_LIGHT = 0x1B;
	public static final String SENSOR_TYPE_LIGHT = "电灯";

	public static final int SENSOR_TYPE_ID_FAN = 0x1C;
	public static final String SENSOR_TYPE_FAN = "散热器风扇";

	/**
	 * 0x25电子门锁 (1:打开门锁，0:关闭门锁) 
	 * S.DA[0].[0]
	 */
	public static final int SENSOR_TYPE_ID_DOOR_LOCK = 0x1D;
	public static final String SENSOR_TYPE_DOOR_LOCK = "电子门锁";

	public static final int SENSOR_TYPE_ID_WATER_PUMP = 0x1E;
	public static final String SENSOR_TYPE_WATER_PUMP = "微型水泵";

	public static final int SENSOR_TYPE_ID_HEATER = 0x1F;
	public static final String SENSOR_TYPE_HEATER = "陶瓷加热器";

	public static final int SENSOR_TYPE_ID_AV_ANNUNCIATOR = 0x20;
	public static final String SENSOR_TYPE_AV_ANNUNCIATOR = "声光报警器";
	
	/***************************************
	 * 0x21遥控插座 
	 * S.DA[0].[0]
	 **************************************/
	public static final int SENSOR_TYPE_ID_SOCKET = 0x21;
	public static final String SENSOR_TYPE_SOCKET = "遥控插座";
	
	/**
	 * 0x22低频RFID 
	 * S.DA[0]，S.DA[9]RFID卡号(ASCII码)
	 */
	public static final int SENSOR_TYPE_ID_RFID_LF = 0x22;
	public static final String SENSOR_TYPE_RFID_LF = "低频RFID";
	
	/***************************************
	 * 0x23二氧化碳传感器 
	 * S.DA[0]，S.DA[1] 传感器数据
	 **************************************/
	public static final int SENSOR_TYPE_ID_CO2 = 0x23;
	public static final String SENSOR_TYPE_CO2 = "二氧化碳传感器 ";

	/**
	 * 0x24土壤温湿度传感器(SHT10)
	 * S.DA[0]，S.DA[1]湿度数据
	 * S.DA[2]，S.DA[3]温度数据
	 */
	public static final int SENSOR_TYPE_ID_SOIL = 0x24;
	public static final String SENSOR_TYPE_SOIL = "土壤温湿度传感器(SHT10)";
	
	/**
	 * 0x25门磁检测器 (1:检测到门被打开，0:未检测到门打开) 
	 * S.DA[0].[0]
	 */
	public static final int SENSOR_TYPE_ID_DOOR = 0x25;
	public static final String SENSOR_TYPE_DOOR = "门磁检测器";
	
	/**
	 * 0x26人体红外探测器(1:检测到人体，0:未检测到人体) 
	 * S.DA[0].[0]
	 */
	public static final int SENSOR_TYPE_ID_IR_DETECTOR = 0x26;
	public static final String SENSOR_TYPE_IR_DETECTOR = "人体红外探测器";

	/**
	 * 0x27可燃气体检测器(1:检测到可燃气体，0:未检测到可燃气体)
	 * S.DA[0].[0]
	 */
	public static final int SENSOR_TYPE_ID_GAS_DETECTOR = 0x27;
	public static final String SENSOR_TYPE_GAS_DETECTOR = "可燃气体检测器";
	
	/**
	 * 0x28红外电子栅栏(1:检测到红外线被遮断，0:检测到红外线未被遮断) 
	 * S.DA[0].[0]
	 */
	public static final int SENSOR_TYPE_ID_IR_FENCE = 0x28;
	public static final String SENSOR_TYPE_IR_FENCE = "红外电子栅栏";
	
	/**
	 * 0x29烟雾报警器(1:检测到烟雾，0:未检测到烟雾)
	 * S.DA[0].[0]
	 */
	public static final int SENSOR_TYPE_ID_SMOKE_ALARM = 0x29;
	public static final String SENSOR_TYPE_SMOKE_ALARM = "烟雾报警器";

	/**
	 * 0x2A串口温湿度变送器 transmitter 
	 * S.DA[0]，S.DA[1] 温度数据(16位有符号数除以10) 
	 * S.DA[2]，S.DA[3] 湿度数据(16位有符号数除以10) 
	 * S.DA[4]，S.DA[5] 露点数据(16位有符号数除以10)
	 */
	public static final int SENSOR_TYPE_ID_TH_TRANSMITTER_RS232 = 0x2A;
	public static final String SENSOR_TYPE_TH_TRANSMITTER_RS232 = "RS232温湿度变送器";
	
	/**
	 * 0x2BRS485温湿度变送器 
	 * S.DA[0]，S.DA[1] 湿度数据(16位有符号数除以10) 
	 * S.DA[2]，S.DA[3] 温度数据(16位有符号数除以10)
	 */
	public static final int SENSOR_TYPE_ID_TH_TRANSMITTER_RS485 = 0x2B;
	public static final String SENSOR_TYPE_TH_TRANSMITTER_RS485 = "RS485温湿度变送器";

	/**
	 * 0x2C电动窗帘(1:打开窗帘，0:闭合窗帘) 
	 * S.DA[0].[0]
	 */
	public static final int SENSOR_TYPE_ID_WIN_CURTAIN = 0x2C;
	public static final String SENSOR_TYPE_WIN_CURTAIN = "电动窗帘";
	
	public static final int SENSOR_TYPES = SENSOR_TYPE_ID_TH_TRANSMITTER_RS485 + 1;
	
	public static final String[] SENSOR_TYPE_ARR = new String[]{
		SENSOR_TYPE_UNDEFINE,
		SENSOR_TYPE_LM35DZ,
		SENSOR_TYPE_LS_RESISTANCE,
		SENSOR_TYPE_LS_DIODE,
		SENSOR_TYPE_MQ3,
		SENSOR_TYPE_MQ5,
		SENSOR_TYPE_MQ2,
		SENSOR_TYPE_HC_SR501,
		SENSOR_TYPE_DC_MOTOR,
		SENSOR_TYPE_DHT11,
		SENSOR_TYPE_ADXL345,
		SENSOR_TYPE_SHT10,
		SENSOR_TYPE_L3G4200D,
		SENSOR_TYPE_PIN_IO,
		SENSOR_TYPE_SMART_AMMETER,
		SENSOR_TYPE_REMOTE_CONTROL,
		SENSOR_TYPE_MQ7,
		SENSOR_TYPE_VIBRATION,
		SENSOR_TYPE_FLAME,
		SENSOR_TYPE_BH1750FVI,
		SENSOR_TYPE_SOUND,
		SENSOR_TYPE_IRDMS,
		SENSOR_TYPE_DSM501A,
		SENSOR_TYPE_RELAY,
		SENSOR_TYPE_BMP085,
		SENSOR_TYPE_IO_LED,
		SENSOR_TYPE_INNER_TS,
		SENSOR_TYPE_LIGHT,
		SENSOR_TYPE_FAN,
		SENSOR_TYPE_DOOR_LOCK,
		SENSOR_TYPE_WATER_PUMP,
		SENSOR_TYPE_HEATER,
		SENSOR_TYPE_AV_ANNUNCIATOR,
		SENSOR_TYPE_SOCKET,
		SENSOR_TYPE_RFID_LF,
		SENSOR_TYPE_CO2,
		SENSOR_TYPE_SOIL,
		SENSOR_TYPE_DOOR,
		SENSOR_TYPE_IR_DETECTOR,
		SENSOR_TYPE_GAS_DETECTOR,
		SENSOR_TYPE_IR_FENCE,
		SENSOR_TYPE_SMOKE_ALARM,
		SENSOR_TYPE_TH_TRANSMITTER_RS232,
		SENSOR_TYPE_TH_TRANSMITTER_RS485,
		SENSOR_TYPE_WIN_CURTAIN
		
	};
	
	public static final int SENSOR_DATA_ARR_LENGTH = 10;
	
	public static final byte[] SENSOR_DATA_ARR = new byte[]{
		(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, 
		(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, 
	};
	

	public static final int FRAME_MIN_LEN                     = 9;
	public static final int FRAME_DATA_OFFSET                 = 4;
	
	/**
	 * 帧结构
	 */
	public static final int FRAME_POS_HEAD = 0;
	public static final int FRAME_LEN_HEAD = 2;
	
	public static final int FRAME_POS_TYPE = FRAME_POS_HEAD + FRAME_LEN_HEAD; //2
	public static final int FRAME_LEN_TYPE = 1;
	
	public static final int FRAME_POS_LENG = FRAME_POS_TYPE + FRAME_LEN_TYPE; //3
	public static final int FRAME_LEN_LENG = 1;
	
	/*************************************************
	 * 1、查询拓扑信息响应/拓扑信息报告(10)
	 *************************************************/
	
	/*************************************************
	 * 2、查询节点综合信息响应/节点综合信息报告(26)
	 *************************************************/
	public static final int FRAME_POS_NMIR_DATA = FRAME_POS_LENG + FRAME_LEN_LENG;//4
	public static final int FRAME_LEN_NMIR_DATA = 21;
	
	public static final int FRAME_POS_NMIR_C_NA = 4;
	public static final int FRAME_LEN_NMIR_C_NA = 2;
	
	public static final int FRAME_POS_NMIR_P_NA = 6;
	public static final int FRAME_LEN_NMIR_P_NA = 2;
	
	public static final int FRAME_POS_NMIR_PAN_ID = 8;
	public static final int FRAME_LEN_NMIR_PAN_ID = 2;

	public static final int FRAME_POS_NMIR_PROFILE = 10;
	public static final int FRAME_LEN_NMIR_PROFILE = 1;

	public static final int FRAME_POS_NMIR_S_TP = 11;
	public static final int FRAME_LEN_NMIR_S_TP = 1;
	
	public static final int FRAME_POS_NMIR_S_DA = 12;
	public static final int FRAME_LEN_NMIR_S_DA = 10;

	public static final int FRAME_POS_NMIR_POWER = 22;
	public static final int FRAME_LEN_NMIR_POWER = 1;

	public static final int FRAME_POS_NMIR_C_VER = 23;
	public static final int FRAME_LEN_NMIR_C_VER = 1;

	public static final int FRAME_POS_NMIR_FCS = 25;
	public static final int FRAME_LEN_NMIR_FCS = 1;
	
	/*************************************************
	 * 3、查询传感器数据响应/传感器数据报告(18)
	 *************************************************/

	public static final int FRAME_POS_NSR_C_NA = 4;
	
	public static final int FRAME_POS_NSR_S_TP = 6;
	public static final int FRAME_LEN_NSR_S_TP = 1;
	
	public static final int FRAME_POS_NSR_S_DA = 7;
	public static final int FRAME_LEN_NSR_S_DA = 10;
	
	public static final int FRAME_POS_NSR_FCS = 17;
	public static final int FRAME_LEN_NSR_FCS = 1;
	
	/*************************************************
	 * 4、节点加入网络报告(25)
	 *************************************************/

	public static final int FRAME_POS_NJR_C_NA = 4;
	public static final int FRAME_POS_NJR_C_IEEE = 6;
	
	public static final int FRAME_POS_NJR_P_NA = 14;
	public static final int FRAME_POS_NJR_P_IEEE = 16;
	
	public static final int FRAME_POS_NJR_FCS = 24;
	public static final int FRAME_LEN_NJR_FCS = 1;
	
	/*************************************************
	 * 5、查询节点信息(8)
	 *************************************************/
	
	/*************************************************
	 * 6、查询节点信息响应(18)
	 *************************************************/
	
	/*************************************************
	 * 7、配置节点参数(18)
	 *************************************************/
	public static final int FRAME_POS_NSP_C_NA = 4;
	public static final int FRAME_POS_NSP_SET_TYPE = 6;
	
	public static final int FRAME_POS_NSP_SET_DATA = 7;
	public static final int FRAME_LEN_NSP_SET_DATA = 10;
	
	public static final int FRAME_POS_NSP_FCS = 17;
	public static final int FRAME_LEN_NSP_FCS = 1;
	
	/*************************************************
	 * 8、配置节点参数响应(18)
	 *************************************************/
	
	public static final int FRAME_POS_NSPR_C_NA = 4;
	public static final int FRAME_POS_NSPR_SET_TYPE = 6;
	
	public static final int FRAME_POS_NSPR_SET_DATA = 7;
	public static final int FRAME_LEN_NSPR_SET_DATA = 10;
	
	public static final int FRAME_POS_NSPR_FCS = 17;
	public static final int FRAME_LEN_NSPR_FCS = 1;
	
	/**
	 * 将arrByte[p]开始的1字节转换为整形数据
	 * @param arrByte
	 * @param p
	 * @return
	 */
	public static int buildByte(final byte[] arrByte, int p) {
		int ret = (int)(arrByte[p] & 0xFF);
		return ret;
	}	
	
	/**
	 * 将arrByte[p]开始的2字节转换为整形数据
	 * @param arrByte
	 * @param p
	 * @return
	 */
	public static int buildShort(final byte[] arrByte, int p) {
		int ret = (int)((arrByte[p] & 0xFF) << 8) | (arrByte[p + 1] & 0xFF);
		return ret;
	}
	
	/**
	 * 将arrByte[p]开始的4字节转换为整形数据
	 * @param arrByte
	 * @param p
	 * @return
	 */
	public static int buildInt(final byte[] arrByte, int p) {
		int ret = (int) (
				(((int)(arrByte[p + 0] & 0xFF)) << 24) | 
				(((int)(arrByte[p + 1] & 0xFF)) << 16) | 
				(((int)(arrByte[p + 2] & 0xFF)) << 8) | 
				(((int)(arrByte[p + 3] & 0xFF)) ));
		return ret;
	}
	/**
	 * 将arrByte[p]开始的8字节转换为整形数据
	 * @param arrByte
	 * @param p
	 * @return
	 */
	public static long buildLong(final byte[] arrByte, int p) {
		long ret = (long) (
				(((long)(arrByte[p + 0] & 0xFF)) << 56) | 
				(((long)(arrByte[p + 1] & 0xFF)) << 48) | 
				(((long)(arrByte[p + 2] & 0xFF)) << 40) | 
				(((long)(arrByte[p + 3] & 0xFF)) << 32) | 
				(((long)(arrByte[p + 4] & 0xFF)) << 24) | 
				(((long)(arrByte[p + 5] & 0xFF)) << 16) | 
				(((long)(arrByte[p + 6] & 0xFF)) << 8) | 
				(((long)(arrByte[p + 7] & 0xFF)) ));
		return ret;
	}
}
