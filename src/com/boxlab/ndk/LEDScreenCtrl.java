package com.boxlab.ndk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.serialport.SerialPortFinder;
import com.android.serialport.ShellInterface;
import com.android.serialport.UartThread;
import com.boxlab.utils.EncodingConversionUtil;

public class LEDScreenCtrl {

	protected static final String TAG = "LEDSvreen";
	boolean isInRunMode;
	boolean isRunThread;
	boolean isUartReady;
	boolean isInRecvMode;
	int times = 0;
	private UartThread uartThread = null;
	private boolean isExist;
	private String[] uartPortNames;
	private SerialPortFinder mSerialPortFinder;
	private String dataStr=null;//��ʾ����
	private String colorStr=null;//��ʾЧ���ֽ�
	private String address_485Str=null;//485��ַ�ֽ�
	private String superpositionStr=null;//�ۼ��ֽ�
	// ************485�ӿ�************//
	private	RS485Ctrl rs485Ctrl;
	static final int RS485_RECV_MODE = 0;
	static final int RS485_SEND_MODE = 1;
	//��ʾЧ��
	public static final int RED=0;//�����ɫ
	public static final int GREEN=1;//������ɫ
	public static final int ORANGE=2;//�����ɫ
	public static final int REW_WITH_TWINKLE=3;//�����ɫ����˸
	public static final int GREEN_WITH_TWINKLE=4;//������ɫ����˸
	public static final int ORANGE_WITH_TWINKLE=5;//�����ɫ����˸

	public LEDScreenCtrl() {
		isInRunMode = false;
		isRunThread = false;
		isUartReady = false;
		isInRecvMode = false;
		rs485Ctrl = new RS485Ctrl();
		address_485Str="03";
		superpositionStr="00";
	}

	public boolean init(String port, int baud) {
		
		// ************���������߳�************//
		isExist = false;
		mSerialPortFinder = new SerialPortFinder();
		uartPortNames = mSerialPortFinder.getAllDevicesPath();
		for (int i = 0; i < uartPortNames.length; i++) {
			if (uartPortNames[i].contains(port)) {
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			Log.i(TAG, "[����]: ����(" + port + ")�Ѷ�ʧ����ˢ�º�����ѡ�񴮿ڡ�\n");
			isInRunMode = false;
			return false;
		}
		if ((!port.isEmpty()) && (!port.equals(""))) {
			isInRunMode = true;
			uartThread = new UartThread(dataHandler);
			uartThread.openUart(port, baud);
			// �����߳�
			if (!uartThread.isAlive()) {
				uartThread.start();
			}
			if (ShellInterface.isSuAvailable()) {
				ShellInterface
						.getProcessOutput("chmod 777 /dev/max485_ctl_pin");
				if (isInRecvMode) {
					rs485Ctrl.open();
					rs485Ctrl.setMode(RS485_RECV_MODE);
					isInRecvMode=!isInRecvMode;
					Log.i(TAG, "[��ʾ]: RS485�ӿڹ����ڽ���ģʽ��\n");
				} else {
					rs485Ctrl.open();
					rs485Ctrl.setMode(RS485_SEND_MODE);
					isInRecvMode=!isInRecvMode;
					Log.i(TAG, "[��ʾ]: RS485�ӿڹ����ڷ���ģʽ��\n");
				}
			}
			Log.i(TAG, "[��ʾ]: ����ͨѶ�߳̽����ɹ���\n");
		} else {
			isInRunMode = false;
			return false;
		}
		return true;
	}
	
	public boolean display(String info,int color,boolean superposition){
		superpositionStr=superposition?"01":"00";
		return display(info, color);
	}

	public boolean display(String info, int color) {
		if(!isInRecvMode){
			rs485Ctrl.setMode(RS485_SEND_MODE);
			isInRecvMode=!isInRecvMode;
		}
		dataStr=info;
		switch (color) {
		case RED:
			colorStr="01";
			break;
		case GREEN:
			colorStr="02";
			break;
		case ORANGE:
			colorStr="03";
			break;
		case REW_WITH_TWINKLE:
			colorStr="04";
			break;
		case GREEN_WITH_TWINKLE:
			colorStr="05";
			break;
		case ORANGE_WITH_TWINKLE:
			colorStr="06";
			break;
		default:
			break;
		}
		String dataTohex = EncodingConversionUtil.str2HexStr(info);
		String datalenthhex = Integer.toHexString(dataTohex.length() / 2 + 5)
				.toUpperCase();
		String StrOfCom = "01"+address_485Str + (datalenthhex.length() > 1 ? "" : "0")+ datalenthhex + superpositionStr + colorStr+ dataTohex;
		StrOfCom = StrOfCom + EncodingConversionUtil.makeChecksum(StrOfCom);
		byte[] cmd = EncodingConversionUtil.HexString2Bytes(StrOfCom.toUpperCase());
		if (uartThread != null) {
//			Timer timer = new Timer();
//			TimerTask task = new TimerTask(){   
//			    public void run(){   
//			    	rs485Ctrl.setMode(RS485_RECV_MODE);
//			    	isInRecvMode=!isInRecvMode;
//			    	Log.i(TAG, "[��ʾ]: RS485�ӿڹ����ڽ���ģʽ��\n");
//			    }   
//			};   
			uartThread.sendData(cmd);
//			rs485Ctrl.setMode(RS485_RECV_MODE);
//			timer.schedule(task, 7); 
			return true;
		}
		return false;
	}
	
	public void clean(){
		byte[] cmd = EncodingConversionUtil.HexString2Bytes("0103050001000A");
		if (uartThread != null) {
			uartThread.sendData(cmd);
		}
	}

	// ���ݴ�������ʾ
	private final Handler dataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle;
			byte[] dataBuf;

			super.handleMessage(msg);
			switch (msg.what) {
			// ��ʾ��Ϣ
			case 0:
//				handlerMode = 0;
				bundle = msg.getData();
				String str = bundle.getString("Message");
				Log.i(TAG, str);
				break;

			// ���ڽ��յ�����
			case 1:
				bundle = msg.getData();
				dataBuf = bundle.getByteArray("RecvData");
//				if (handlerMode == 1) {
//					if (dataBuf.length > 0) {
//						Log.i(TAG, new String(dataBuf));
//					}
//				} else {
					if (dataBuf.length > 0) {
						Log.i(TAG, "[����]: \n");
						Log.i(TAG, ""+new String(dataBuf));
//					}
				}
//				handlerMode = 1;
				break;

			// ���ڷ��͵�����
			case 2:
				bundle = msg.getData();
				dataBuf = bundle.getByteArray("SendData");
//				if (handlerMode == 2) {
//					if (dataBuf.length > 0) {
//						Log.i(TAG, new String(dataBuf) + "\n");
//					}
//				} else {
					if (dataBuf.length > 0) {
						Log.i(TAG, "[����]: \n");
						Log.i(TAG, dataStr + "\n");
//					}
				}
//				handlerMode = 2;
				break;

			// �ر��߳�
			case 3:
//				handlerMode = 3;
				isRunThread = false;
				isUartReady = false;
				if (uartThread != null) {
					uartThread.closeUart();
					uartThread.interrupt();
				}
				isInRunMode = false;
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void close() {
		// ************�رմ����߳�************//
		isInRunMode=false;
		if (uartThread != null) {
			uartThread.closeUart();
			uartThread.interrupt();
			uartThread = null;
		}
		if (ShellInterface.isSuAvailable()) {
			ShellInterface.getProcessOutput("chmod 777 /dev/max485_ctl_pin");
			rs485Ctrl.close();
		}
		times = 0;
		Log.i(TAG, "[��ʾ]: �رմ���ͨѶ�̳߳ɹ���\n");
	}

	public String getAddress_485Str() {
		return address_485Str;
	}

	public void setAddress_485Str(String address_485Str) {
		this.address_485Str = address_485Str;
	}
	
}
