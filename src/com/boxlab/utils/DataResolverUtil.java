package com.boxlab.utils;

import java.io.IOException;

import com.boxlab.bean.Sensor;

import android.util.Log;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-16 下午6:08:05 
 * 类说明 
 * TODO 可以进一步修改为状态机
 */

public class DataResolverUtil {

	private static final String TAG = "DataResolverUtil";
	
	//private static final boolean DEBUG = true;
	private static final boolean DEBUG = false;
	
	private static final int mixDataCacheSize = 1024;                      // 最小缓冲区大小
	
	public  int mDataCacheSize = mixDataCacheSize;                         // 内部缓冲区大小
	private int DataCache_Start = 0, DataCache_End = 0;                    // 内部缓冲区索引
	private byte[] DataCache = null;                                       // 内部缓冲区
	
	/** 帧类型 */
	public  byte FrameType = 0;                                            
	
	/** 数据帧 -- 译码出来的数据包 */
	public  byte[] FrameData = null;
	public  int FramePacketLen = 0;              // 译码出的数据包长度
	

	public StringBuilder stringBuilder = new StringBuilder("");     // 日记信息
	public int FrameResolveNum = 0;
	public int FrameErrorNum = 0;
	private LogUtil mLog;
	
	
	public DataResolverUtil(int iDataCacheSize) {
		if(iDataCacheSize < mixDataCacheSize){
			mDataCacheSize = mixDataCacheSize;
		}
		else{
			mDataCacheSize = iDataCacheSize;
		}
		
		DataCache = new byte[mDataCacheSize];         // 分配内部缓冲区
		FrameData = new byte[256];                    // 数据帧---译码出来的数据包(最大数据包限制)
		FrameData[0] = (byte) 0xFF;
		FrameData[1] = (byte) 0xFF;
		
		try {
			mLog = new LogUtil("DataResolverUtil");
			showMsg("Opened log at " + mLog.getPath());
		} catch (IOException e) {
			showMsg("Failed to open log", e);
		}
	}
	
	/**
	 * 缓存接收到的数据，并查找缓存中包含的数据帧
	 * 
	 * @param SrcData
	 * @param size
	 * @return String
	 */
	public EnumState processSerialPortData(final byte[] SrcData, int size) {
		int i = 0, j = 0;
		boolean IsFoundFullFrame = false;    // 是否找到一个完整的数据帧(false:否, true:是)
		boolean IsValidFrame = false;        // 数据帧CRC校验是否通过(false:否, true:是)
		int FreeCache = 0;                   // 缓冲区剩余空间
		//int FramePacketLen = 0;              // 译码出的数据包长度
		byte FrameDataLen = 0;               // 帧有效负载数据部分译码后的长度
		int FrameStartOnCache = -1;                 // 帧起始位置
		
		// 判断缓存是否溢出
		FreeCache = mDataCacheSize - DataCache_End;
		if (FreeCache < size) {
			//拷贝未处理缓冲数据
			int temp = DataCache_End - DataCache_Start;
			for (i = 0; i < temp; i++) {
				DataCache[i] = DataCache[DataCache_Start + i];
			}
			DataCache_End = i;
			DataCache_Start = 0;
		}
		
		// 缓存数据
		for (i = 0; i < size; i++) {
			DataCache[DataCache_End++] = SrcData[i];
		}
		
		if (DataCache_End - DataCache_Start >= FrameUtil.FRAME_MIN_LEN) {        // 最小的数据帧长度限制
			for (i = DataCache_Start; i < (DataCache_End - 4); i++) {
				IsFoundFullFrame = false;                                        // 清除完整数据帧标志位
				IsValidFrame = false;                                            // 清除CRC校验标志位
				FrameStartOnCache = -1;                                          // 清除帧起始位置

				// 查找起始帧标志
				if (IsFrameFront(DataCache, i)) {
					FrameStartOnCache = i;
					FramePacketLen = 2;
					FrameType = DataCache[FrameStartOnCache + 2];
					FrameDataLen = DataCache[FrameStartOnCache + 3];
					FrameData[FramePacketLen++] = DataCache[FrameStartOnCache + 2];
					FrameData[FramePacketLen++] = DataCache[FrameStartOnCache + 3];
					
					// 处理帧有效负载数据
					// 查找完整的数据帧
					if ((FrameStartOnCache + FrameUtil.FRAME_DATA_OFFSET) + FrameDataLen < DataCache_End) {
						IsFoundFullFrame = true;
						// 找到完整的有效帧负载数据
						for (j = FrameStartOnCache + 4; j < FrameStartOnCache + 4 + FrameDataLen; j++) {
							FrameData[FramePacketLen++] = DataCache[j];
						}
						
						// 帧CRC校验
						byte CheckCRC = DataCache[j];;                           // 待检验参考CRC
						byte CalCRC = (byte) 0x00;                               // 计算CRC
						for (j = 0; j < FramePacketLen; j++) {
							CalCRC = (byte) (CalCRC ^ FrameData[j]);
						}
						if (CalCRC == CheckCRC) {
							IsValidFrame = true;
							DataCache_Start = DataCache_Start + FrameUtil.FRAME_DATA_OFFSET + FrameDataLen + 1;
						} else if (IsFoundFullFrame) {
							DataCache_Start = DataCache_Start + 2;
						}
						
						// 完整的数据帧
						if (IsFoundFullFrame) {

							if(DEBUG){
								stringBuilder.delete(0, stringBuilder.length());
							}
							
							if (!IsValidFrame) {
								if(DEBUG){
									FrameErrorNum++;
									stringBuilder.append("\n-----------错帧: " + FrameErrorNum + " 帧-------------\n");
									stringBuilder.append("\n-----------------------------------------------------------\n");
									stringBuilder.append("数据帧CRC校验失败：\n");
									stringBuilder.append("接收CRC：0x"
											+ StringUtil.getHexStringFormatByte(CheckCRC) + "\n");
									stringBuilder.append("计算CRC：0x"
											+ StringUtil.getHexStringFormatByte(CalCRC) + "\n");
									stringBuilder.append("数据帧：\n");
									stringBuilder.append(StringUtil.arrByteFormat(FrameData, FramePacketLen));
									stringBuilder.append("\n");
									
									showMsg(stringBuilder.toString());
								}

								return EnumState.FailedFrameFCS;
								
							} else {
								if(DEBUG){
									
									// 对译码后的数据包进行解析
									FrameResolveNum++;
									stringBuilder.append("\n-----------解析: " + FrameResolveNum + " 帧-------------\n");
									stringBuilder.append("数据帧：\n");
									stringBuilder.append(StringUtil.arrByteFormat(FrameData, FramePacketLen));
									stringBuilder.append("\n");
									//ParseDataFrame(FrameType, DecPacket, FramePacketLen, stringBuilder);

									showMsg(stringBuilder.toString());
								}

								return EnumState.PassFrameFCS;
								
							}
						}
						return EnumState.CheckFrameFCS;
					}
					return EnumState.FindingFullFrame;
				}
			}
			return EnumState.FindindFrameFront;
		}
		return EnumState.FindindFrameFront;
	}

	/**
	 * 查找起始帧标志
	 * 
	 * @param CheckData
	 * @param p
	 * @return
	 */
	public boolean IsFrameFront(final byte[] CheckData, int p) {
		if (CheckData[p] == (byte) 0xff
				&& CheckData[p + 1] == (byte) 0xff
				&& ((CheckData[p + 2] == (byte) 0x01)
						|| (CheckData[p + 2] == (byte) 0x02)
						|| (CheckData[p + 2] == (byte) 0x03)
						|| (CheckData[p + 2] == (byte) 0x04)
						|| (CheckData[p + 2] == (byte) 0x11) 
						|| (CheckData[p + 2] == (byte) 0x13))) {
			return true;
		}
		return false;
	}

	// log helper function
	private void showMsg(String message) {
		showMsg(message, null);
	}
	
	private void showMsg(String message, Throwable e) {
		if (e != null){
			Log.e(TAG, message, e);
		} else {
			Log.i(TAG, message);			
		}
		
		if (mLog != null){
			try {
				if(e != null){
					mLog.println(message + "\n" + Log.getStackTraceString(e));
					mLog.println(e.toString());
				}else{
					mLog.println(message);
				}
			} catch (IOException ex) {}
		}
	}

	public enum EnumState{

		/**
		 *  查找起始帧标志
		 */
		FindindFrameFront,

		/**
		 *  找到起始帧标志，查找完整的数据帧
		 */
		FindingFullFrame,
		
		/**
		 *  找到完整的数据数据，进行帧CRC校验
		 */
		CheckFrameFCS,
		
		/**
		 * 帧校验通过
		 */
		PassFrameFCS,
		
		/**
		 * 帧校验失败
		 */
		FailedFrameFCS,
	};
	
	
	
	public static final byte GFRM_RESERVED_DATA               =(byte) 0xFE;
	//General Report Format
	public static final short GFRM_SOP                        =(short) 0xFFFF;
	public static final byte GFRM_SOP_OFFSET                  = 0;
	public static final byte GFRM_TYPE_OFFSET                 = 2;
	public static final byte GFRM_DLEN_OFFSET                 = 3;
	public static final byte GFRM_DATA_OFFSET                 = 4;
	
	public static final byte GFRM_TYPE_SET                    = 0x12;
	public static final byte GFRM_TYPE_SET_RESP               = 0x13;
	
	//Set Node Parameters Response Format
	//节点配置参数
	public static final byte NODE_SET_RESTORE                  = 0x01;  //还原出厂设置
	public static final byte NODE_SET_RPTTIME                  = 0x02;  //设置定时报告时间
	public static final byte NODE_SET_BUADRATE                 = 0x03;  //设置串口通讯波特率
	public static final byte NODE_SET_PWM                      = 0x04;  //设置PWM控制参数
	public static final byte NODE_SET_IO                       = 0x05;  //设置IO参数
	public static final byte NODE_SEND_REMOTE_CMD              = 0x06;  //设置远程遥控指令代码
	public static final byte NODE_SET_NETINFO                  = 0x07;  //设置网络参数
	public static final byte NODE_SET_RELAY                    = 0x08;  //设置继电器参数
	public static final byte NODE_SET_4LED                      = 0x09;

	public static final byte GFRM_SNPR_LEN                     = 18;
	public static final byte GFRM_SNPR_TYPE                    = GFRM_TYPE_SET_RESP;
	public static final byte GFRM_SNPR_DLEN                    = 13;
	public static final byte SNPR_ADDR_OFFSET                  = 0;
	public static final byte SNPR_TYPE_OFFSET                  = 2;
	public static final byte SNPR_DATA_OFFSET                  = 3;
	public static final byte SNPR_FCS_OFFSET                   = 13;
	
	public static final byte HAL_UART_BR_9600   = 0x00;
	public static final byte HAL_UART_BR_19200  = 0x01;
	public static final byte HAL_UART_BR_38400  = 0x02;
	public static final byte HAL_UART_BR_57600  = 0x03;
	public static final byte HAL_UART_BR_115200 = 0x04;

	/**
	 * 对发送数据进行编码
	 * 
	 * @param Sensor  sensor 节点地址
	 * @param int     setType 设置类型
	 * @param int     cache 设置数据(数据方向低位[7]--高位[10])
	 * @return byte[]
	 */
	public static byte[] EecodeData(Sensor sensor, int setType, int cache) {

		int iCna = sensor.sBean.iCna;
		return EecodeData(iCna, setType, cache);
	}
	/**
	 * 对发送数据进行编码
	 * 
	 * @param int iCna     节点地址
	 * @param int setType  设置类型
	 * @param int cache    设置数据(数据方向低位[7]--高位[10])
	 * @return byte[]
	 */
	public static byte[] EecodeData(int iCna, int setType, int cache) {
		
		int i;
		
        //申请空间
        byte[] pData = new byte[GFRM_SNPR_LEN];
        //帧标识
        pData[GFRM_SOP_OFFSET] = (byte) ((GFRM_SOP>>8) & 0xFF);
        pData[GFRM_SOP_OFFSET + 1] = (byte) (GFRM_SOP & 0xFF);
        //帧类型
        pData[GFRM_TYPE_OFFSET] = GFRM_TYPE_SET;
        //帧数据部分长度
        pData[GFRM_DLEN_OFFSET] = GFRM_SNPR_DLEN;
        //节点地址
        pData[GFRM_DATA_OFFSET + SNPR_ADDR_OFFSET] = (byte) ((iCna>>8) & 0xFF);
        pData[GFRM_DATA_OFFSET + SNPR_ADDR_OFFSET + 1] = (byte) (iCna & 0xFF);
        
        switch(setType)
        {
          case NODE_SET_RESTORE:
          {
            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_RESTORE;
            //数据信息                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte) cache;
            //填充数据
            for(i = 1; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //校验位
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);                 
            break;
          }
          
          case NODE_SET_RPTTIME:
          {
        	if (cache > 65000) cache = 65000;
            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_RPTTIME;
            //数据信息                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte) ((cache>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte) (cache & 0xFF);
            //填充数据
            for(i = 2; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //校验位
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);
                 
            break;
          }
          
          case NODE_SET_BUADRATE:
          {
        	byte uartBuadRate = (byte)((cache>>8) & 0xff);//[7]
        	byte remoteUartBuadRate = (byte)(cache & 0xff);//[8]
        	if (uartBuadRate > HAL_UART_BR_115200 || uartBuadRate < HAL_UART_BR_9600) 
        		uartBuadRate = HAL_UART_BR_57600;
        	if (remoteUartBuadRate > HAL_UART_BR_115200 || remoteUartBuadRate < HAL_UART_BR_9600) 
        		remoteUartBuadRate = HAL_UART_BR_9600;
            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_BUADRATE;
            //数据信息                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = uartBuadRate;
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = remoteUartBuadRate;
            //填充数据
            for(i = 2; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }             
            break;
          }
          
          case NODE_SET_PWM:
          {
            byte cacheCcwPer = (byte)((cache>>8) & 0xFF);
            byte cacheCwPer = (byte)(cache & 0xFF);
            short value = (short)((cache>>16) & 0xFFFF);
            if(cacheCcwPer > 100 || cacheCcwPer < 0 ) cacheCcwPer = 50;
            if(cacheCwPer > 100 || cacheCwPer < 0 ) cacheCwPer = 50;
            if(value < 5 || value > 2500 ) value = 100;

            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_PWM;
            //数据信息                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = cacheCcwPer;
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = cacheCwPer;
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 2] = (byte)((value>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 3] = (byte)(value & 0xFF);
            //填充数据
            for(i = 4; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //校验位
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);                
            break;
          }
          
          case NODE_SET_IO:  
          {
            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_IO;
            //数据信息
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)((cache>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte)(cache & 0xFF);
            //填充数据
            for(i = 2; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //校验位
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1); 
            break;
          }
          
          case NODE_SET_NETINFO://设置网络参数
          {
          	short cachePanid =  (short) (cache & 0xFFFF);
          	cache = ((cache >> 16) & 0xFF); 
            if(cache < 0x0B || cache > 0x1A)
            {
              cache = 0x0B;
            }
            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_NETINFO;
            //数据信息
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)((cachePanid>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte)(cachePanid & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 2] = (byte)(cache & 0xFF);
            //填充数据
            for(i = 3; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //校验位
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);

            break;
          }
          
          case NODE_SET_RELAY:  
          {

          	Log.w(TAG,"NODE_SET_RELAY");
          	Log.w(TAG,"Cna = " + StringUtil.getHexStringFormatShort(iCna));
            Log.w(TAG,"cache = "+ Integer.toHexString(cache));
            
            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_RELAY;
            //数据信息
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)(cache & 0xFF);
            //填充数据
            for(i = 1; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //校验位
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);
                  
            break;
          }
          
          case NODE_SET_4LED:  
          {
        	Log.w(TAG,"NODE_SET_4LED");
            Log.w(TAG,"cache="+Integer.toHexString(cache));
            
            //信息类型
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_4LED;
            //数据信息
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)(0x0F);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte)((cache>>8) & 0x0F);
            //填充数据
            for(i = 2; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //校验位
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);
                  
            break;
          }
          
        }
		
		return pData;
	}
	
	/**
	 * public static byte calcFCS(byte[] pData, int datalen)
	 * 对指定长度的byte数组进行FCS校验。
	 * 
	 * @param pData
	 *            byte[] 要校验的数据
	 * @param datalen
	 *            int 长度
	 * @return FCS校验值 
	 */
    public static byte calcFCS(byte[] pData, int datalen){
		byte CalCRC=0;
    	// CRC校验
		for (int j = 0; j < datalen; j++) {
			CalCRC = (byte) (CalCRC ^ pData[j]);
		}
		return CalCRC;
    }

}
