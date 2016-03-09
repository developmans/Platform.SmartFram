package com.boxlab.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-15 下午2:59:48 
 * 类说明 
 */

public class StringUtil {

	public static String getStringFormatS_TP(int iSensorType) {
		if(iSensorType <= 0 || iSensorType >= FrameUtil.SENSOR_TYPES
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_UNDEFINE )
			return FrameUtil.SENSOR_TYPE_ARR[0];
		
		return FrameUtil.SENSOR_TYPE_ARR[iSensorType];
	}

	public static String getStringFormatS_DA(int iSensorType, byte[] aSensorData) {
		// TODO Auto-generated method stub
		return "";
	}

	public static String getStringFormatPowerLevel(int iPower) {
		// TODO Auto-generated method stub
		float f = (float) ((1.15 * iPower) * 3.0 / 2048.0);
		return String.valueOf(f);
	}
	
	public static String getHexStringFormatByte(byte b) {
		return String.format("0x%02X", (int)(b & 0xFF));
	}
	
	public static String getHexStringFormatShort(int i) {
		return String.format("0x%04X", (long)(i & 0xFFFFFFFFFFFFFFFFL));
	}
	
	public static String getHexStringFormatInt(int i) {
		return String.format("0x%08X", (long)(i & 0xFFFFFFFFFFFFFFFFL));
	}
	
	public static String getHexStringFormatLong(long l) {
		return String.format("0x%016X", (long)(l & 0xFFFFFFFFFFFFFFFFL));
	}
	
	public static String getHexStringFormatIEEE(long l) {
		final StringBuilder stringBuilder = new StringBuilder(8*3);
		for(int i = 7; i >= 0; i--){
			if (i > 0)
				stringBuilder.append(String.format("%02X-",
						(int) ((l >>> (8 * i)) & 0xFF)));
			else
				stringBuilder.append(String.format("%02X",
						(int) ((l >>> (8 * i)) & 0xFF)));
		}
		return stringBuilder.toString();
	}
	
    public static String arrByteFormat(final byte[] arrByte, int length) {
    	
    	int len;
    	
    	if(arrByte == null)
    		return "null";
    	
    	if(length < 0){
    		len = arrByte.length;
    	}else{
    		len = length;
    	}
    	
		final StringBuilder stringBuilder = new StringBuilder(len);
		for(int i = 0; i < len; i++){
			byte byteChar = arrByte[i];
			stringBuilder.append(String.format("[%02X] ", byteChar));
		}
		return stringBuilder.toString();
	}
    
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
    
    public static String getTimeStamp(){
        Date data = new Date(System.currentTimeMillis());  
        String timestamp = simpleDateFormat.format(data);  
        return timestamp;  
    }
}
