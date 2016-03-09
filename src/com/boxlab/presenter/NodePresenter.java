package com.boxlab.presenter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.R.fraction;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.WebIconDatabase.IconListener;

import com.android.serialport.SerialPort;
import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.interfaces.IListenerBase;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.interfaces.ISmartLogicView;
import com.boxlab.model.INodeEngineModel;
import com.boxlab.model.NodeEngineModel;
import com.boxlab.utils.DataResolverUtil;
import com.boxlab.utils.DataResolverUtil.EnumState;
import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.LogUtil;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.SmartLogic;
import com.boxlab.utils.StringUtil;
import com.boxlab.utils.TcpClientThread;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-9-16 ����1:38:36 
 * ��˵�� 
 */

public class NodePresenter {
	
	private static final String TAG = "NodePresenter";
	
	private INodeEngineModel mNodeEngine;
	private IListenerSensor mSmartHomeView;
	private IListenerSensor mSmartFramView;
	private IListenerSensor mFragmentView;
	private boolean isSmartHomeViewReg;
	private boolean isSmartFramViewReg;
	private boolean isFragmentViewReg;

	private final ArrayList<ZigBeeBean> mZigBeeBeans;
	private final ArrayList<SensorBean> mSensorBeans;
	private final ArrayList<ZigBee> mZigBees;
	private final ArrayList<Sensor> mSensors;
	
	private Handler mHandler;
	private UartThread uartThread;

	//private TcpClientThread tcpClientThread;
	
	private static final long NET_RECONNECT_INTVERVAL = 60000;
	
	
	public NodePresenter(Context pContext, Handler h) {

		SharedPreferencesUtil.getSharedPreferencesInstance((ContextWrapper) pContext);
		
		this.mHandler = h;
		
		// �����ڵ�����
		mNodeEngine = new NodeEngineModel(pContext);
		
		// �������нڵ��ZigBee������Ϣ
		mZigBeeBeans = mNodeEngine.loadNetworkBeans();
		int nodeCount = mZigBeeBeans.size();
		Log.w(TAG, "��ʼ���ڵ����:" + nodeCount);
		
		// ����ÿ���ڵ��Ӧ��ZigBee�б�
		mZigBees = new ArrayList<ZigBee>(nodeCount);
		
		// ����ÿ���ڵ��Ӧ��SensorBean�б�
		mSensorBeans = new ArrayList<SensorBean>(nodeCount);
		// ����ÿ���ڵ��Ӧ��Sensor�б�
		mSensors = new ArrayList<Sensor>(nodeCount);
		
		for(int i = 0; i < nodeCount; i++){
			// ��ȡ�ڵ��Cna��ַ
			ZigBeeBean nbean = mZigBeeBeans.get(i);
			
			// ��ʼ����Ӧ��ZigBee
			ZigBee zigBee = new ZigBee(nbean);
			mZigBees.add(zigBee);
			
			// ���ض�Ӧ��SensorBean
			SensorBean sBean = mNodeEngine.loadSensorBean(nbean.iCna);
			mSensorBeans.add(sBean);
			
			// ��ʼ����Ӧ��Sensor
			Sensor sensor = new Sensor(sBean, true);
			mSensors.add(sensor);
		}
	}

	public void registerView(IListenerSensor view){
		boolean init = false;
		
		if(view.getListenerType() == SharedPreferencesUtil.TYPE_SMART_HOME){
			this.mSmartHomeView = view;
			this.isSmartHomeViewReg = true;
			init = true;
		}else if(view.getListenerType() == SharedPreferencesUtil.TYPE_SMART_FRAM){
			this.mSmartFramView = view;
			this.isSmartFramViewReg = true;
			init = true;
		}else if(view.getListenerType() == SharedPreferencesUtil.TYPE_FRAGMENT){
			this.mFragmentView = view;
			this.isFragmentViewReg = true;
		}
		
		view.onInitZigBeeBeans(mZigBeeBeans);
		view.onInitSensorBeans(mSensorBeans);
		view.onInitZigBees(mZigBees);
		view.onInitSensors(mSensors);

		if(init){
			startUartThread();
		}
		
	}
	
	public void unregisterView(IListenerSensor view){
		
		boolean isKeepAlive = SharedPreferencesUtil.getServiceKeepAlive();
		boolean isFragment = false;
		
		if(view.getListenerType() == SharedPreferencesUtil.TYPE_SMART_HOME){
			this.mSmartHomeView = null;
			this.isSmartHomeViewReg = false;
		}else if(view.getListenerType() == SharedPreferencesUtil.TYPE_SMART_FRAM){
			this.mSmartFramView = null;
			this.isSmartFramViewReg = false;
		}else if(view.getListenerType() == SharedPreferencesUtil.TYPE_FRAGMENT){
			this.mFragmentView = null;
			this.isFragmentViewReg = false;
			isFragment = true;
		}
		
		if(!isFragment && !isKeepAlive){
			stopUartThread();
		}
	}
	
	public void unregisterAllView() {
		this.mSmartFramView = null;
		this.isSmartFramViewReg = false;
		
		this.mSmartHomeView = null;
		this.isSmartHomeViewReg = false;

		this.mFragmentView = null;
		this.isFragmentViewReg = false;
		
		stopUartThread();
		
	}

	public void processErr(int errReason, String errTips) {
		// TODO Auto-generated method stub
		if(isSmartHomeViewReg && mSmartHomeView != null){
			mSmartFramView.onErr(errReason, errTips);
		}else if(isSmartFramViewReg && mSmartFramView != null){
			mSmartFramView.onErr(errReason, errTips);
		}
		if(isFragmentViewReg && mFragmentView != null){
			mFragmentView.onErr(errReason, errTips);
		}
		if(isSmartLogicViewReg && smartLogicView != null){
			smartLogicView.onErr(errReason, errTips);
		}
	}

	public void startUartThread() {
        Log.e(TAG, "startUartThread()");
        
		if(SharedPreferencesUtil.getServiceKeepAlive()){
			if(uartThread != null && uartThread.isAlive()){
		        Log.e(TAG, "uartThread is still Alive,return");
				return;
			}
		}
		
		if(uartThread != null && uartThread.isAlive()){
	        Log.e(TAG, "uartThread is still Alive,close");
			stopUartThread();
		}

//		if(tcpClientThread != null && tcpClientThread.isAlive()){
//	        Log.e(TAG, "tcpClientThread is still Alive,close it again");
//			stopUartThread();
//		}
		
        String port = SharedPreferencesUtil.getSerialPort();
		int rate = SharedPreferencesUtil.getSerialRate();
		
//		String serverAddr = SharedPreferencesUtil.getServerAddr();
//		int serverPort = SharedPreferencesUtil.getServerPort();

//		tcpClientThread = new TcpClientThread(tcpHandler, serverAddr, serverPort);
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				startTcpClientThread();
//			}
//		}).start();
		
		uartThread = new UartThread(this.mHandler);
		if(uartThread.openUart(port, rate)){
	        Log.w(TAG, "openUart() start new UartThread");
			uartThread.start();
		}else{
			
		}
	}
//
//	private boolean startTcpClientThread() {
//		
//		if(tcpClientThread.connectToServer()){
//	        Log.e(TAG, "startTcpClientThread() connect to Server successful, Thread started! ");
//			tcpClientThread.start();
//			return true;
//		} else {
//			mHandler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							startTcpClientThread();
//						}
//					}).start();
//				}
//			}, NET_RECONNECT_INTVERVAL);
//		}
//		return false;
//	};

//	private Handler tcpHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//
//			String serverAddr;
//			int netRecDataLen = 0;
//			byte[] netRecvData;
//			
//			switch (msg.what) {
//
//			case TcpClientThread.TCP_CLIENT_SHOW_MSG:
//				
//				break;
//			case TcpClientThread.TCP_CLIENT_SEND_DATA:
//				
//				break;
//			case TcpClientThread.TCP_CLIENT_RECV_DATA:
//				Bundle bundle = msg.getData();
////				bundle.putString("Server", this.socketAddr);
////				bundle.putInt("RecvLen", this.recvLen);
////				bundle.putByteArray("RecvData", this.recvBuf);
//				serverAddr = bundle.getString("Server");
//				netRecDataLen = bundle.getInt("RecvLen");
//				netRecvData = bundle.getByteArray("RecvData");
//				if(netRecDataLen > 0){
//					Log.w(TAG, "ת��������������");
//					sendCmd(netRecvData);
//				}
//				break;
//				
//			case TcpClientThread.TCP_CLIENT_THREAD_FINISH:
//				
//				mHandler.postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						new Thread(new Runnable() {
//							
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								startTcpClientThread();
//							}
//						}).start();
//					}
//				}, NET_RECONNECT_INTVERVAL);
//				break;
//				
//
//			default:
//				break;
//			}
//		}
//		
//	};
//	
	public void stopUartThread() {
        Log.e(TAG, "stopUartThread()");
        if(uartThread != null){
    		uartThread.closeUart();
    		uartThread.interrupt();
    		uartThread = null;
        }
//        if(tcpClientThread != null){
//        	tcpClientThread.closeSocket();
//        	tcpClientThread.interrupt();
//        	tcpClientThread = null;
//        }
	}
	
	public void sendCmd(byte[] cmd) {
        Log.e(TAG, "sendCmd()");

		if(uartThread != null){
    		uartThread.sendData(cmd);
    		return;
        }
	}

	public ArrayList<Sensor> loadSensors(Sensor sensor, int n) {
        Log.e(TAG, "loadSensors() " + sensor);
        
        ArrayList<Sensor> listSensor = new ArrayList<Sensor>(n);
        
        ArrayList<SensorBean> listsBean = mNodeEngine.loadSensorBeans(sensor.sBean, n);

        for(SensorBean s : listsBean){
			// ��ʼ����Ӧ��Sensor
			listSensor.add(new Sensor(s, true));
		}
        return listSensor;
	}
	
	public void deleteNode(int index,int nodeaddr) {
        Log.e(TAG, "deleteNode() " + nodeaddr);
		mNodeEngine.deleteNode(nodeaddr);
		mZigBeeBeans.remove(index);
		mZigBees.remove(index);
		mSensorBeans.remove(index);
		mSensors.remove(index);
	}
	
	public class UartThread extends Thread{
	    private static final String TAG = "UartThread";
		private Handler mDataHandler = null;
	    private SerialPort mSerialPort = null;
	    private InputStream mInStream = null;
	    private OutputStream mOutStream = null;
	    private byte[] recvBuf = null;
	    private int recvLen;
	    private boolean isRunThread;
	    
		private LogUtil mLog;

	    public UartThread(Handler handler)
	    {
	        this.mDataHandler = handler;
	        this.recvBuf = new byte[64];
	        this.recvLen = 0;
	        this.mSerialPort = null;
	        this.mInStream = null;
	        this.mOutStream = null;
	        
			try {
				mLog = new LogUtil("UartThread");
				showMsg("Opened log at " + mLog.getPath());
			} catch (IOException e) {
				showMsg("Failed to open log", e);
			}
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

	    public boolean openUart(String uartPort, int buadrate)
	    {
	        this.mSerialPort = openSerialPort(uartPort, buadrate);
	        if(this.mSerialPort == null){
	        	String tips = "[��ʾ]���򿪴���ʧ�ܣ�����˿ڣ�" + uartPort + "�Ƿ���ȷ...";
		        showMsg(tips);
		        processErr(IListenerBase.ERR_UART_OPEN_FAILED, tips);
		        return false;
	        }
	        
	        this.mInStream = this.mSerialPort.getInputStream();
	        this.mOutStream = this.mSerialPort.getOutputStream();
	        isRunThread = true;
	        showMsg("[��ʾ]���򿪴��ڳɹ���");
	        return true;
	    }

	    private SerialPort openSerialPort(String uartPort, int buadrate)
	    {
	        SerialPort nSerialPort = null;
	        try {
	            nSerialPort = new SerialPort(new File(uartPort), buadrate);
	        } catch (InvalidParameterException e) {
	            showMsg("[����]���򿪴��ڳ����쳣��");
	            showMsg("ԭ��", e);
	        } catch (SecurityException e) {
	        	showMsg("[����]���򿪴��ڳ����쳣��");
	            showMsg("ԭ��", e);
	        } catch (IOException e) {
	            showMsg("[����]���򿪴��ڳ����쳣��");
	            showMsg("ԭ��", e);
	        }
	        return nSerialPort;
	    }

	    public void closeUart()
	    {
	        isRunThread = false;
	        if (this.mInStream != null)
	        {
	            try {
	                this.mInStream.close();
	            } catch (IOException e) {
	            	 showMsg("[����]���رմ����������쳣��");
	                 showMsg("ԭ��", e);
	            }
	            this.mInStream = null;
	        }
	        if (this.mOutStream != null)
	        {
	            try {
	                this.mOutStream.close();
	            } catch (IOException e) {
	                showMsg("[����]���رմ���������쳣��");
	                showMsg("ԭ��", e);
	            }
	            this.mOutStream = null;
	        }
	        if (this.mSerialPort != null)
	        {
	            this.mSerialPort.close();
	            this.mSerialPort = null;
	        }
	        showMsg("[��ʾ]���رմ��ڳɹ���");
	    }
	    
	    public void sendData(byte[] sendBuf)
	    {
	        if(this.mSerialPort != null && this.mOutStream != null)
	        {
	            if(sendBuf.length > 0)
	            {
	                try
	                {
	                	showMsg("[��ʾ]��sendData()");
	                	showMsg(StringUtil.arrByteFormat(sendBuf, sendBuf.length ));
	                	this.mOutStream.flush();
	                    this.mOutStream.write(sendBuf);
	                    this.mOutStream.flush();
	                    Message msg = new Message();
	                    Bundle bundle = new Bundle();
	                    bundle.putByteArray("SendData", sendBuf);
	                    msg.what = 3;
	                    msg.setData(bundle);
	                    mDataHandler.sendMessage(msg);
	                }
	                catch (IOException e)
	                {
	                	showMsg("[����]�����ʹ������ݳ����쳣��");
	                    showMsg("ԭ��" + e.toString());
	                }
	            }
	            
	        }
	    }
	    
	    @Override
	    public void run()
	    {
			DataResolverUtil mFrameContent = new DataResolverUtil(1024);
			EnumState eState = EnumState.FindindFrameFront; 
	        while(isRunThread)
	        {
	            if (this.mSerialPort != null && this.mInStream != null)
	            {
	                try {
	                	this.recvLen = this.mInStream.read(this.recvBuf, 0, 64);
	                } catch (IOException e) {
	                	showMsg("[����]�����մ������ݳ����쳣��");
	                    showMsg("ԭ��" + e.toString());
	                    if (Thread.currentThread().isAlive()) {
							Thread.interrupted();
						}
	                    mFrameContent = null;
	                    return;
	                }
	                
					if (this.recvLen > 0) {
						eState = mFrameContent.processSerialPortData(this.recvBuf, this.recvLen);
						if(eState == EnumState.PassFrameFCS){
							
		                	//showMsg("[��ʾ]������֡�������");
							ParseDataFrame(mFrameContent.FrameType, mFrameContent.FrameData);
							
//							//��������ת��
//							if(tcpClientThread != null && tcpClientThread.isAlive()){
//								tcpClientThread.sendData(mFrameContent.FrameData, mFrameContent.FramePacketLen);
//							}
							
						}
					}
	            }else{
	            	if (Thread.currentThread().isAlive()) {
						Thread.interrupted();
					}
                    mFrameContent = null;
	            	return;
	            }
	        }
	        if (Thread.currentThread().isAlive()) {
				Thread.interrupted();
			}
            mFrameContent = null;
	        return;
	    }
	    
	}

	/**
	 * @param frameType
	 * @param decPacket
	 * @param decPacketLen
	 * @param stringBuilder
	 */
	public void ParseDataFrame(byte frameType, byte[] frameData) {
		
		int iCna = FrameUtil.buildShort(frameData,FrameUtil.FRAME_POS_NMIR_C_NA);
		
		int index = -1;
		// �����������ݰ����н���
		switch (frameType) {
		
		case (byte) 0x01: // ��ѯ������Ϣ��Ӧ/������Ϣ����(10) �����ͽڵ��ַ�����ڵ��ַ��Э��汾
			
		    index = getNodeIndex(iCna);
		    if(index < 0){
		    	
		    }
			break;

		case (byte) 0x02: // ��ѯ�ڵ��ۺ���Ϣ��Ӧ/�ڵ��ۺ���Ϣ����(26) ���ڵ��ַ�����ڵ��ַ����������ţ�Э��汾�����������ͣ����������ݣ��ڵ��ѹ���̼��汾
		
		    index = getNodeIndex(iCna); 
		    if(index < 0){
		    	processNodeMainInfoRepoNewNode(frameData);
		    }else{
		    	processNodeMainInfoRepo(index, frameData);
		    }
		    
			break;

		case (byte) 0x03: // ��ѯ������������Ӧ/���������ݱ���(18) ���ڵ��ַ�����������ͣ�����������

			index = getNodeIndex(iCna);
		    //Log.e(TAG, "���������ݱ���,index=" + index);
			if (index >= 0) {
				processNodeSensorInfoRepo(index, frameData, true);
			}

			break;
		case (byte) 0x04: // �ڵ�������籨��(25) ���ڵ��ַ���ڵ�IEEE��ַ�����ڵ��ַ�����ڵ�IEEE��ַ

			index = getNodeIndex(iCna);
			if (index < 0) {
				processNodeJoinRepoNewNode(frameData);
			} else {
				processNodeJoinRepo(index, frameData);
			}
			break;

		case (byte) 0x11: // ��ѯ�ڵ���Ϣ��Ӧ(18) ���ڵ��ַ����Ӧ��Ϣ���ͣ���Ӧ����

			break;

		case (byte) 0x13: // ���ýڵ������Ӧ(18) : �ڵ��ַ�� ���ò��������ͣ����ò���������
			
			index = getNodeIndex(iCna);
		    processSensorSettingReport(index, frameData);
		    
			break;

		default:
			Log.e("NodePresenter","\n-----------------------------------------------------------\nZigBeeδ֪����֡:\n" + 
		            "FrameType = " + frameType + "\n");
			break;
		}
	}
	
	/**
	 * ����iCna��ȡ�ڵ���mZigBeeBeans������
	 * @param iCna
	 * @return �ڵ��Ӧ������,-1��ʾmZigBeeBeans�б����ڸýڵ�
	 *          
	 */
	public int getNodeIndex(int iCna) {
		int nodeCount = mZigBeeBeans.size();
		for(int i = 0; i < nodeCount; i++ ){
			if(mZigBeeBeans.get(i).iCna == iCna){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * ����<b>�ڵ��ۺ���Ϣ����֡</b>
	 * <i>�������½ڵ�</i>
	 * @param arrByte
	 */
	public void processNodeMainInfoRepoNewNode(final byte[] arrByte){
		
		// �½ڵ㽫�����ĩβ
		int index = mZigBeeBeans.size();
		
		// �����½ڵ�
		Log.e(TAG, "processNodeMainInfoRepoNewNode() �����½ڵ�ZigBeeBean");
		ZigBeeBean zBean = new ZigBeeBean();
		zBean.iCna = FrameUtil.buildShort(arrByte, FrameUtil.FRAME_POS_NMIR_C_NA);
		zBean.iPna = FrameUtil.buildShort(arrByte, FrameUtil.FRAME_POS_NMIR_P_NA);
		zBean.iPanId = FrameUtil.buildShort(arrByte, FrameUtil.FRAME_POS_NMIR_PAN_ID);
		zBean.iProfile = FrameUtil.buildShort(arrByte, FrameUtil.FRAME_POS_NMIR_PROFILE);
		zBean.iCver = FrameUtil.buildShort(arrByte, FrameUtil.FRAME_POS_NMIR_C_VER);
		mZigBeeBeans.add(zBean);
		
		// ���½ڵ�ͬ�������ݿ���
		mNodeEngine.saveNetworkBeans(mZigBeeBeans);
		
		// Ϊ�½ڵ㴴����Ӧ��ZigBee
		ZigBee zigBee = new ZigBee(zBean);
		mZigBees.add(zigBee);
		
		// Ϊ�½ڵ㴴����Ӧ��SensorBean
		Log.e(TAG, "processNodeMainInfoRepoNewNode() Ϊ�½ڵ㴴����Ӧ��SensorBean");
		SensorBean s = new SensorBean();
		s.iCna = zBean.iCna;
		mSensorBeans.add(s);
		
		// Ϊ�½ڵ㴴����Ӧ��Sensor
		Log.e(TAG, "processNodeMainInfoRepoNewNode() Ϊ�½ڵ㴴����Ӧ��Sensor");
		Sensor sensor = new Sensor(s,false);
		mSensors.add(sensor);
		
		// ����������Ϣ
		processNodeMainInfoRepoSensorInfo(index, arrByte, true);
	}
	
	/**
	 * ����<b>�ڵ��ۺ���Ϣ����֡</b></br>
	 * <i>�����½ڵ�������籨����ӵĽڵ����Ϣ</i>
	 * @param index
	 * @param frameData
	 */
	private void processNodeMainInfoRepo(int index, byte[] frameData) {
		
		ZigBeeBean z = mZigBeeBeans.get(index);
		
		if(z.iProfile == 0){
			Log.e(TAG, "processNodeMainInfoRepo() ���ýڵ��Ƿ��ǽڵ�������籨����ӵĽڵ� " + z);
			// ���ýڵ��Ƿ��ǽڵ�������籨����ӵĽڵ�
			z.iPanId = FrameUtil.buildShort(frameData, FrameUtil.FRAME_POS_NMIR_PAN_ID);
			z.iProfile = FrameUtil.buildShort(frameData, FrameUtil.FRAME_POS_NMIR_PROFILE);
			z.iCver = FrameUtil.buildByte(frameData, FrameUtil.FRAME_POS_NMIR_C_VER);
			// ͬ�������ݿ���
			mNodeEngine.saveNetworkBeans(mZigBeeBeans);
			// ���¶�Ӧ��ZigBee
			mZigBees.get(index).updataNetInfo();
			
			// ����������Ϣ
	    	processNodeMainInfoRepoSensorInfo(index, frameData, true);
		}else{
			// ����������Ϣ
	    	processNodeMainInfoRepoSensorInfo(index, frameData, false);
		}

	}
	
	/**
	 * ����<b>�ڵ��ۺ���Ϣ����֡</b>
	 * ��������Ϣ���ֵ�����
	 * @param index       �ڵ�����
	 * @param arrByte     ֡����
	 * @param updataSensorType ���´��������ͱ�ʶ
	 */
	public void processNodeMainInfoRepoSensorInfo(int index, final byte[] arrByte, boolean updataSensorType) {
		
		SensorBean sBean = mSensorBeans.get(index);

		sBean.iSensorType = (int)(arrByte[FrameUtil.FRAME_POS_NMIR_S_TP] & 0xFF);
		
		System.arraycopy(arrByte, FrameUtil.FRAME_POS_NMIR_S_DA,
				         sBean.aSensorData, 0, FrameUtil.SENSOR_DATA_ARR_LENGTH);
		
		sBean.iPower = FrameUtil.buildShort(arrByte, FrameUtil.FRAME_POS_NMIR_POWER);

		Sensor sensor = mSensors.get(index);
		
		if(updataSensorType){
			Sensor.updataSensorType(sensor, sBean);
		}

		if((sBean.aSensorData[0] != (byte)0xFE) && (sBean.aSensorData[1] != (byte)0xFE)){
			mNodeEngine.saveSensorBean(sBean);
		}
		
		notifyReciveSensorInfo(index, sensor,updataSensorType);
		
	}

	/**
	 * ����<b>�ڵ�������籨��֡</b>
	 * <i>�Ѿ����ڵĽڵ�</i>
	 * @param index      �ڵ�����
	 * @param frameData  ֡����
	 */
	private void processNodeJoinRepo(int index, byte[] frameData) {

		ZigBeeBean zBean = mZigBeeBeans.get(index);
		zBean.iCna = FrameUtil.buildShort(frameData, FrameUtil.FRAME_POS_NJR_C_NA);
		zBean.iPna = FrameUtil.buildShort(frameData, FrameUtil.FRAME_POS_NJR_P_NA);
		zBean.lCieee = FrameUtil.buildLong(frameData, FrameUtil.FRAME_POS_NJR_C_IEEE);
		zBean.lPieee = FrameUtil.buildLong(frameData, FrameUtil.FRAME_POS_NJR_P_IEEE);
		Log.e(TAG, "processNodeJoinRepo() �ڵ�������籨�棬�ڵ��Ѿ����ڣ�����IEEE����Ϣ" + zBean);

		// ���½ڵ�ͬ�������ݿ���
		mNodeEngine.saveNetworkBeans(mZigBeeBeans);
		// ���¶�Ӧ��ZigBee
		mZigBees.get(index).updataNetInfo();
		
		// ���ýڵ���������ʶ
		zBean.iProfile = 0;
	}


	/**
	 * ����<b>�ڵ�������籨��֡</b>
	 * <i>�������½ڵ�</i>
	 * @param frameData
	 */
	private void processNodeJoinRepoNewNode(byte[] frameData) {
		
		// �����½ڵ�
		Log.e(TAG, "processNodeJoinRepoNewNode() �ڵ�������籨�棬�����½ڵ�");
		ZigBeeBean zBean = new ZigBeeBean();
		zBean.iCna = FrameUtil.buildShort(frameData, FrameUtil.FRAME_POS_NJR_C_NA);
		zBean.iPna = FrameUtil.buildShort(frameData, FrameUtil.FRAME_POS_NJR_P_NA);
		zBean.lCieee = FrameUtil.buildLong(frameData, FrameUtil.FRAME_POS_NJR_C_IEEE);
		zBean.lPieee = FrameUtil.buildLong(frameData, FrameUtil.FRAME_POS_NJR_P_IEEE);
		mZigBeeBeans.add(zBean);
		ZigBee z = new ZigBee(zBean);
		mZigBees.add(z);

		// Ϊ�½ڵ㴴����Ӧ��SensorBean
		Log.e(TAG, "processNodeJoinRepoNewNode() �ڵ�������籨�棬Ϊ�½ڵ㴴����Ӧ��SensorBean");
		SensorBean sBean = new SensorBean();
		sBean.iCna = zBean.iCna;
		mSensorBeans.add(sBean);
		
		// Ϊ�½ڵ㴴����Ӧ��Sensor
		Log.e(TAG, "processNodeJoinRepoNewNode() �ڵ�������籨�棬Ϊ�½ڵ㴴����Ӧ��Sensor");
		Sensor sensor = new Sensor(sBean, false);
		mSensors.add(sensor);
	}

	/**
	 * ����<b>���������ݱ���֡</b>
	 * @param index
	 * @param arrByte
	 * @param notify �Ƿ���Ҫ����Sensor�Ĵ���������
	 */
	public void processNodeSensorInfoRepo(int index, final byte[] arrByte, boolean notify) {
		
		SensorBean s = mSensorBeans.get(index);

		s.iSensorType = (int)(arrByte[FrameUtil.FRAME_POS_NSR_S_TP] & 0xFF);
		
		System.arraycopy(arrByte, FrameUtil.FRAME_POS_NSR_S_DA,
				         s.aSensorData, 0, FrameUtil.SENSOR_DATA_ARR_LENGTH);
		
		s.iPower = FrameUtil.buildShort(arrByte, FrameUtil.FRAME_POS_NMIR_POWER);

		if((s.aSensorData[0] == (byte)0xFE) && (s.aSensorData[1] == (byte)0xFE) &&
				(s.aSensorData[2] == (byte)0xFE) && (s.iSensorType != FrameUtil.SENSOR_TYPE_ID_PIN_IO)){
			return;
		}
		
		mNodeEngine.saveSensorBean(s);
		
		Sensor sensor = mSensors.get(index);
		
		if(notify){
			Sensor.updataSensorType(sensor, s);
		}
		
		notifyReciveSensorInfo(index, sensor,notify);
	}

	private void notifyReciveSensorInfo(int index, Sensor sensor, boolean notify) {
		boolean isupdata = false;
		boolean isnotify = false;
		
		if(isSmartHomeViewReg && mSmartHomeView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify || notify) {
				mSmartHomeView.notifyReciveSensor(index, sensor);
			}
		}
		
		if(isSmartFramViewReg && mSmartFramView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify || notify) {
				mSmartFramView.notifyReciveSensor(index, sensor);
			}
		}

		if(isFragmentViewReg && mFragmentView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify || notify) {
				mFragmentView.notifyReciveSensor(index, sensor);
			}
		}
		
		if(isSmartLogicViewReg && smartLogicView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify || notify) {
				smartLogicView.notifyReciveSensor(index, sensor);
			}
		}
	}

//	/**
//	 * ����<b>���ýڵ������Ӧ֡</b>
//	 * @param frameData
//	 */
//	private void processSensorSettingReport(byte[] frameData) {
//		// TODO Auto-generated method stub
//		
//	}

	/**
	 * ����<b>���ýڵ������Ӧ֡</b>
	 * @param index
	 * @param frameData
	 */
	private void processSensorSettingReport(int index, byte[] frameData) {
		
//		int setType = FrameUtil.buildByte(frameData, FrameUtil.FRAME_POS_NSPR_SET_TYPE);
//		if(setType != (DataResolverUtil.NODE_SET_RELAY & 0xff) || 
//				setType != (DataResolverUtil.NODE_SET_IO & 0xff))
//			return;
		
		SensorBean sBean = mSensorBeans.get(index);
		
		System.arraycopy(frameData, FrameUtil.FRAME_POS_NSPR_SET_DATA,
				         sBean.aSensorData, 0, FrameUtil.FRAME_LEN_NSPR_SET_DATA);
		
		mNodeEngine.saveSensorBean(sBean);
		
		Sensor sensor = mSensors.get(index);
		Sensor.updataSensorType(sensor, sBean);
		
		onSensorSettingReportCallback(index, sensor);
	}

	private void onSensorSettingReportCallback(int index, Sensor sensor) {
		boolean isupdata = false;
		boolean isnotify = false;

		if(isFragmentViewReg && mFragmentView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify) {
				mFragmentView.onSensorSettingReportCallback(index, sensor);
			}
		}
		
		if(isSmartLogicViewReg && smartLogicView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify) {
				smartLogicView.onSensorSettingReportCallback(index, sensor);
			}
		}

		if(isSmartHomeViewReg && mSmartHomeView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify) {
				mSmartHomeView.notifyReciveSensor(index, sensor);
			}
		}
		
		if(isSmartFramViewReg && mSmartFramView.filterInterested(sensor)){
			if(isupdata == false){
				isnotify = Sensor.updataSensorData(sensor);
				isupdata = true;
			}
			if (isnotify) {
				mSmartFramView.notifyReciveSensor(index, sensor);
			}
		}
	}
	
	private ISmartLogicView smartLogicView;
	private boolean isSmartLogicViewReg;

	public void registerSmartLogicView(ISmartLogicView smartLogicView) {
		this.smartLogicView = smartLogicView;
		this.isSmartLogicViewReg = true;
		this.smartLogicView.onInitZigBeeBeans(mZigBeeBeans);
		this.smartLogicView.onInitSensorBeans(mSensorBeans);
		this.smartLogicView.onInitZigBees(mZigBees);
		this.smartLogicView.onInitSensors(mSensors);
	}

	public void unregisterSmartLogicView() {
		this.smartLogicView = null;
		this.isSmartLogicViewReg = false;
	}
}
