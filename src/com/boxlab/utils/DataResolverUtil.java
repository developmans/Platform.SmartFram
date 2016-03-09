package com.boxlab.utils;

import java.io.IOException;

import com.boxlab.bean.Sensor;

import android.util.Log;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-9-16 ����6:08:05 
 * ��˵�� 
 * TODO ���Խ�һ���޸�Ϊ״̬��
 */

public class DataResolverUtil {

	private static final String TAG = "DataResolverUtil";
	
	//private static final boolean DEBUG = true;
	private static final boolean DEBUG = false;
	
	private static final int mixDataCacheSize = 1024;                      // ��С��������С
	
	public  int mDataCacheSize = mixDataCacheSize;                         // �ڲ���������С
	private int DataCache_Start = 0, DataCache_End = 0;                    // �ڲ�����������
	private byte[] DataCache = null;                                       // �ڲ�������
	
	/** ֡���� */
	public  byte FrameType = 0;                                            
	
	/** ����֡ -- ������������ݰ� */
	public  byte[] FrameData = null;
	public  int FramePacketLen = 0;              // ����������ݰ�����
	

	public StringBuilder stringBuilder = new StringBuilder("");     // �ռ���Ϣ
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
		
		DataCache = new byte[mDataCacheSize];         // �����ڲ�������
		FrameData = new byte[256];                    // ����֡---������������ݰ�(������ݰ�����)
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
	 * ������յ������ݣ������һ����а���������֡
	 * 
	 * @param SrcData
	 * @param size
	 * @return String
	 */
	public EnumState processSerialPortData(final byte[] SrcData, int size) {
		int i = 0, j = 0;
		boolean IsFoundFullFrame = false;    // �Ƿ��ҵ�һ������������֡(false:��, true:��)
		boolean IsValidFrame = false;        // ����֡CRCУ���Ƿ�ͨ��(false:��, true:��)
		int FreeCache = 0;                   // ������ʣ��ռ�
		//int FramePacketLen = 0;              // ����������ݰ�����
		byte FrameDataLen = 0;               // ֡��Ч�������ݲ��������ĳ���
		int FrameStartOnCache = -1;                 // ֡��ʼλ��
		
		// �жϻ����Ƿ����
		FreeCache = mDataCacheSize - DataCache_End;
		if (FreeCache < size) {
			//����δ����������
			int temp = DataCache_End - DataCache_Start;
			for (i = 0; i < temp; i++) {
				DataCache[i] = DataCache[DataCache_Start + i];
			}
			DataCache_End = i;
			DataCache_Start = 0;
		}
		
		// ��������
		for (i = 0; i < size; i++) {
			DataCache[DataCache_End++] = SrcData[i];
		}
		
		if (DataCache_End - DataCache_Start >= FrameUtil.FRAME_MIN_LEN) {        // ��С������֡��������
			for (i = DataCache_Start; i < (DataCache_End - 4); i++) {
				IsFoundFullFrame = false;                                        // �����������֡��־λ
				IsValidFrame = false;                                            // ���CRCУ���־λ
				FrameStartOnCache = -1;                                          // ���֡��ʼλ��

				// ������ʼ֡��־
				if (IsFrameFront(DataCache, i)) {
					FrameStartOnCache = i;
					FramePacketLen = 2;
					FrameType = DataCache[FrameStartOnCache + 2];
					FrameDataLen = DataCache[FrameStartOnCache + 3];
					FrameData[FramePacketLen++] = DataCache[FrameStartOnCache + 2];
					FrameData[FramePacketLen++] = DataCache[FrameStartOnCache + 3];
					
					// ����֡��Ч��������
					// ��������������֡
					if ((FrameStartOnCache + FrameUtil.FRAME_DATA_OFFSET) + FrameDataLen < DataCache_End) {
						IsFoundFullFrame = true;
						// �ҵ���������Ч֡��������
						for (j = FrameStartOnCache + 4; j < FrameStartOnCache + 4 + FrameDataLen; j++) {
							FrameData[FramePacketLen++] = DataCache[j];
						}
						
						// ֡CRCУ��
						byte CheckCRC = DataCache[j];;                           // ������ο�CRC
						byte CalCRC = (byte) 0x00;                               // ����CRC
						for (j = 0; j < FramePacketLen; j++) {
							CalCRC = (byte) (CalCRC ^ FrameData[j]);
						}
						if (CalCRC == CheckCRC) {
							IsValidFrame = true;
							DataCache_Start = DataCache_Start + FrameUtil.FRAME_DATA_OFFSET + FrameDataLen + 1;
						} else if (IsFoundFullFrame) {
							DataCache_Start = DataCache_Start + 2;
						}
						
						// ����������֡
						if (IsFoundFullFrame) {

							if(DEBUG){
								stringBuilder.delete(0, stringBuilder.length());
							}
							
							if (!IsValidFrame) {
								if(DEBUG){
									FrameErrorNum++;
									stringBuilder.append("\n-----------��֡: " + FrameErrorNum + " ֡-------------\n");
									stringBuilder.append("\n-----------------------------------------------------------\n");
									stringBuilder.append("����֡CRCУ��ʧ�ܣ�\n");
									stringBuilder.append("����CRC��0x"
											+ StringUtil.getHexStringFormatByte(CheckCRC) + "\n");
									stringBuilder.append("����CRC��0x"
											+ StringUtil.getHexStringFormatByte(CalCRC) + "\n");
									stringBuilder.append("����֡��\n");
									stringBuilder.append(StringUtil.arrByteFormat(FrameData, FramePacketLen));
									stringBuilder.append("\n");
									
									showMsg(stringBuilder.toString());
								}

								return EnumState.FailedFrameFCS;
								
							} else {
								if(DEBUG){
									
									// �����������ݰ����н���
									FrameResolveNum++;
									stringBuilder.append("\n-----------����: " + FrameResolveNum + " ֡-------------\n");
									stringBuilder.append("����֡��\n");
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
	 * ������ʼ֡��־
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
		 *  ������ʼ֡��־
		 */
		FindindFrameFront,

		/**
		 *  �ҵ���ʼ֡��־����������������֡
		 */
		FindingFullFrame,
		
		/**
		 *  �ҵ��������������ݣ�����֡CRCУ��
		 */
		CheckFrameFCS,
		
		/**
		 * ֡У��ͨ��
		 */
		PassFrameFCS,
		
		/**
		 * ֡У��ʧ��
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
	//�ڵ����ò���
	public static final byte NODE_SET_RESTORE                  = 0x01;  //��ԭ��������
	public static final byte NODE_SET_RPTTIME                  = 0x02;  //���ö�ʱ����ʱ��
	public static final byte NODE_SET_BUADRATE                 = 0x03;  //���ô���ͨѶ������
	public static final byte NODE_SET_PWM                      = 0x04;  //����PWM���Ʋ���
	public static final byte NODE_SET_IO                       = 0x05;  //����IO����
	public static final byte NODE_SEND_REMOTE_CMD              = 0x06;  //����Զ��ң��ָ�����
	public static final byte NODE_SET_NETINFO                  = 0x07;  //�����������
	public static final byte NODE_SET_RELAY                    = 0x08;  //���ü̵�������
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
	 * �Է������ݽ��б���
	 * 
	 * @param Sensor  sensor �ڵ��ַ
	 * @param int     setType ��������
	 * @param int     cache ��������(���ݷ����λ[7]--��λ[10])
	 * @return byte[]
	 */
	public static byte[] EecodeData(Sensor sensor, int setType, int cache) {

		int iCna = sensor.sBean.iCna;
		return EecodeData(iCna, setType, cache);
	}
	/**
	 * �Է������ݽ��б���
	 * 
	 * @param int iCna     �ڵ��ַ
	 * @param int setType  ��������
	 * @param int cache    ��������(���ݷ����λ[7]--��λ[10])
	 * @return byte[]
	 */
	public static byte[] EecodeData(int iCna, int setType, int cache) {
		
		int i;
		
        //����ռ�
        byte[] pData = new byte[GFRM_SNPR_LEN];
        //֡��ʶ
        pData[GFRM_SOP_OFFSET] = (byte) ((GFRM_SOP>>8) & 0xFF);
        pData[GFRM_SOP_OFFSET + 1] = (byte) (GFRM_SOP & 0xFF);
        //֡����
        pData[GFRM_TYPE_OFFSET] = GFRM_TYPE_SET;
        //֡���ݲ��ֳ���
        pData[GFRM_DLEN_OFFSET] = GFRM_SNPR_DLEN;
        //�ڵ��ַ
        pData[GFRM_DATA_OFFSET + SNPR_ADDR_OFFSET] = (byte) ((iCna>>8) & 0xFF);
        pData[GFRM_DATA_OFFSET + SNPR_ADDR_OFFSET + 1] = (byte) (iCna & 0xFF);
        
        switch(setType)
        {
          case NODE_SET_RESTORE:
          {
            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_RESTORE;
            //������Ϣ                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte) cache;
            //�������
            for(i = 1; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //У��λ
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);                 
            break;
          }
          
          case NODE_SET_RPTTIME:
          {
        	if (cache > 65000) cache = 65000;
            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_RPTTIME;
            //������Ϣ                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte) ((cache>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte) (cache & 0xFF);
            //�������
            for(i = 2; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //У��λ
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
            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_BUADRATE;
            //������Ϣ                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = uartBuadRate;
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = remoteUartBuadRate;
            //�������
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

            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_PWM;
            //������Ϣ                    
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = cacheCcwPer;
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = cacheCwPer;
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 2] = (byte)((value>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 3] = (byte)(value & 0xFF);
            //�������
            for(i = 4; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //У��λ
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);                
            break;
          }
          
          case NODE_SET_IO:  
          {
            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_IO;
            //������Ϣ
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)((cache>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte)(cache & 0xFF);
            //�������
            for(i = 2; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //У��λ
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1); 
            break;
          }
          
          case NODE_SET_NETINFO://�����������
          {
          	short cachePanid =  (short) (cache & 0xFFFF);
          	cache = ((cache >> 16) & 0xFF); 
            if(cache < 0x0B || cache > 0x1A)
            {
              cache = 0x0B;
            }
            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_NETINFO;
            //������Ϣ
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)((cachePanid>>8) & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte)(cachePanid & 0xFF);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 2] = (byte)(cache & 0xFF);
            //�������
            for(i = 3; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //У��λ
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);

            break;
          }
          
          case NODE_SET_RELAY:  
          {

          	Log.w(TAG,"NODE_SET_RELAY");
          	Log.w(TAG,"Cna = " + StringUtil.getHexStringFormatShort(iCna));
            Log.w(TAG,"cache = "+ Integer.toHexString(cache));
            
            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_RELAY;
            //������Ϣ
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)(cache & 0xFF);
            //�������
            for(i = 1; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //У��λ
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);
                  
            break;
          }
          
          case NODE_SET_4LED:  
          {
        	Log.w(TAG,"NODE_SET_4LED");
            Log.w(TAG,"cache="+Integer.toHexString(cache));
            
            //��Ϣ����
            pData[GFRM_DATA_OFFSET + SNPR_TYPE_OFFSET] = NODE_SET_4LED;
            //������Ϣ
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET] = (byte)(0x0F);
            pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + 1] = (byte)((cache>>8) & 0x0F);
            //�������
            for(i = 2; i < 10; i++)
            {
              pData[GFRM_DATA_OFFSET + SNPR_DATA_OFFSET + i] = GFRM_RESERVED_DATA;
            }
            //У��λ
            pData[GFRM_DATA_OFFSET + SNPR_FCS_OFFSET] = calcFCS(pData, GFRM_SNPR_LEN - 1);
                  
            break;
          }
          
        }
		
		return pData;
	}
	
	/**
	 * public static byte calcFCS(byte[] pData, int datalen)
	 * ��ָ�����ȵ�byte�������FCSУ�顣
	 * 
	 * @param pData
	 *            byte[] ҪУ�������
	 * @param datalen
	 *            int ����
	 * @return FCSУ��ֵ 
	 */
    public static byte calcFCS(byte[] pData, int datalen){
		byte CalCRC=0;
    	// CRCУ��
		for (int j = 0; j < datalen; j++) {
			CalCRC = (byte) (CalCRC ^ pData[j]);
		}
		return CalCRC;
    }

}
