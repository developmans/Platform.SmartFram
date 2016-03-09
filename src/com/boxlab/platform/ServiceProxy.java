package com.boxlab.platform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.android.serialport.ShellInterface;
import com.boxlab.bean.AdminEntity;
import com.boxlab.bean.MsgEntity;
import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.interfaces.ISmartLogicView;
import com.boxlab.presenter.NodePresenter;
import com.boxlab.utils.DataResolverUtil;
import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.PinIO;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.SmartLogic;
import com.boxlab.utils.TcpClientThread;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Next
 * @version 1.0 E-mail: caiwl2005@126.com ����ʱ�䣺2015-9-17 ����2:13:24 ��˵��
 */

public class ServiceProxy extends Service implements ISmartLogicView{

	private static final String TAG = "ServiceProxy";

	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	private NodePresenter mPresenter;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			MsgEntity sms;
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String notifyMsgContent = (String) msg.obj;
				notifyAdminTask(notifyMsgContent);
				break;
				
//			case 2:
//				sms = (MsgEntity) msg.obj;
//				boolean isInUiThread = (msg.arg1 == 1) ? true : false;
//				mMsgList.add(sms);
//				msgCallback.onReciverMsg(sms, isInUiThread);
//				break;

			default:
				break;
			}
		}

	};

	public class LocalBinder extends Binder {

		/**
		 * ��ȡ��ǰService��ʵ��
		 * 
		 * @return ServiceProxy
		 */
		public ServiceProxy getService() {
			Log.i(TAG, "getService()");
			return ServiceProxy.this;
		}
	}

	private final IBinder binder = new LocalBinder();

	/**
	 * ����һ��Binder����
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mPresenter = new NodePresenter(getApplicationContext(), mHandler);
		mPresenter.registerSmartLogicView(this);
		registerBrocastReceiver();
		initAdminList();
		initMsgList();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	public ArrayList<Sensor> loadSensors(Sensor sensor, int n) {
		if (sensor == null || n < 0) {
			return new ArrayList<Sensor>(0);
		}

		return mPresenter.loadSensors(sensor, n);
	}

	public void sendCmd(final byte[] cmd) {
		if (cmd == null) {
			return;
		}
		mPresenter.sendCmd(cmd);
	}

	public void deleteNode(int index, int nodeaddr) {
		if (index == 0 || nodeaddr == 0) {
			return;
		}
		mPresenter.deleteNode(index, nodeaddr);
	}

	public void registerView(IListenerSensor view) {
		Log.e(TAG, "registerView");
		mPresenter.registerView(view);
	}

	public void unregisterView(IListenerSensor view) {
		Log.e(TAG, "unregisterView");
		mPresenter.unregisterView(view);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPresenter.unregisterAllView();
		mPresenter.stopUartThread();
		unRegisterBrocastReceiver();
		SharedPreferencesUtil.saveAdminList(mAdminList);
		mPresenter.unregisterSmartLogicView();
	}

	private SmsReceiver smsReceiver;

	private final ArrayList<MsgEntity> mMsgList = new ArrayList<MsgEntity>();
	private final ArrayList<AdminEntity> mAdminList = new ArrayList<AdminEntity>();
	private ArrayList<AdminEntity> notifyAdminList = new ArrayList<AdminEntity>();

	private void initMsgList() {

	}

	public ArrayList<MsgEntity> getMsgList() {
		return mMsgList;
	}

	private void initAdminList() {
		SharedPreferencesUtil.loadAdminList(mAdminList);
		updataNotifyAdminList();
	}

	private void updataNotifyAdminList() {
		if(notifyAdminList.size() > 0){
			notifyAdminList = null;
			notifyAdminList = new ArrayList<AdminEntity>();
		}
		
		for(AdminEntity admin : mAdminList){
			if((admin.permission & AdminEntity.PERMSSION_RECV_NOTIFY) > 0){
				notifyAdminList.add(admin);
			}
		}
	}

	public ArrayList<AdminEntity> getAdminList() {
		return mAdminList;
	}

	public ArrayList<AdminEntity> addAdmin(AdminEntity admin) {
		mAdminList.add(admin);
		SharedPreferencesUtil.saveAdminList(mAdminList);
		updataNotifyAdminList();
		return mAdminList;
	}

	public ArrayList<AdminEntity> removeAdmin(int position) {
		mAdminList.remove(position);
		SharedPreferencesUtil.saveAdminList(mAdminList);
		updataNotifyAdminList();
		return mAdminList;
	}

	public interface MsgCallback {

		public void onReciverMsg(MsgEntity msg, boolean isUiThread);

	}

	private final MsgCallback dummyCallback = new MsgCallback() {
		@Override
		public void onReciverMsg(MsgEntity msg, boolean isUiThread) {

//			Message handlerMsg = mHandler.obtainMessage(1);
//			handlerMsg.obj = msg;
//			mHandler.sendMessage(handlerMsg);

			Toast.makeText(ServiceProxy.this, (msg.iMsgType == MsgEntity.MSG_TYPE_SEND ? "����" : "�յ�" ) 
					+ "���ţ�" + msg.admin.name + ",����:" + msg.sContent + "\nʱ��:"
							+ msg.sMsgTime, Toast.LENGTH_LONG).show();
		}

	};
	
	private MsgCallback msgCallback = dummyCallback;

	public void registMsgCallback(MsgCallback callback) {
		msgCallback = callback;
	}

	public void unregistMsgCallback() {
		msgCallback = dummyCallback;
	}

	/**
	 * ע��㲥������
	 */
	public void registerBrocastReceiver() {
		smsReceiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction(SMS_RECEIVED);
		registerReceiver(smsReceiver, filter);
	}

	/**
	 * ȡ���㲥���ջ�
	 */
	public void unRegisterBrocastReceiver() {
		unregisterReceiver(smsReceiver);
	}

	// ��������BroadcastReceiver
	public class SmsReceiver extends BroadcastReceiver {

		private String TAG = "SmsReceiver";
		
		// �㲥��Ϣ����
		public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
		
		public final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		@Override
		public void onReceive(Context context, Intent intent) {
	    	Log.e(TAG, "onReceive");
			// ���жϹ㲥��Ϣ
			String action = intent.getAction();
			if (SMS_RECEIVED_ACTION.equals(action)) {
				// ��ȡintent����
				Bundle bundle = intent.getExtras();
				// �ж�bundle����
				if (bundle != null) {
					// ȡpdus����,ת��ΪObject[]
					Object[] pdus = (Object[]) bundle.get("pdus");
					// ��������
					SmsMessage[] messages = new SmsMessage[pdus.length];
					for (int i = 0; i < messages.length; i++) {
						byte[] pdu = (byte[]) pdus[i];
						messages[i] = SmsMessage.createFromPdu(pdu);
					}

					String sender = messages[0].getOriginatingAddress();
					String msgTime = simpleDataFormat.format(new Date(messages[0].getTimestampMillis()));

					StringBuilder msgBody = new StringBuilder("");//��������
					// ���������ݺ�����������
					for (SmsMessage msg : messages) {
						// ��ȡ��������
						msgBody.append(msg.getMessageBody());
					}
					String msgContent = msgBody.toString();

					Log.w(TAG, "����" + sender + "�Ķ���:" +  msgContent);
					AdminEntity admin = getAdminByPhone(sender);
					if(admin != null){
						// �����ض��ĺ���,ȡ���㲥
						abortBroadcast();
						Log.w(TAG, "��������" + admin.name + "(" + admin.phone + ")�Ķ���" );
						
						MsgEntity msg = new MsgEntity(admin);
						msg.iMsgType = MsgEntity.MSG_TYPE_RECV;
						msg.sContent = msgContent;
						msg.sMsgTime = msgTime.toString();
						
						mMsgList.add(msg);
						msgCallback.onReciverMsg(msg, true);
						
						processIncomingMsg(msg);
						
					}
					
				}
			}
		}

		private AdminEntity getAdminByPhone(String phone) {
			for (AdminEntity admin : mAdminList) {
				if (admin.phone != null	&& 
						(admin.phone.contains(phone) || 
						 phone.contains(admin.phone))) {
					return admin;
				}
			}
			return null;
		}
		
		private void processIncomingMsg(MsgEntity msg) {

			MsgEntity msgResp = new MsgEntity(msg.admin);
			msgResp.iMsgType = MsgEntity.MSG_TYPE_SEND;
			
			int msg_action = SmartLogic.MSG_ACTION_UNDIFIND;
			int iSensor = -1;
			String sSensorTitle = SmartLogic.MSG_SENSOR_TITLE_LIST[0];
			
			StringBuilder sb = new StringBuilder();
			
			int length = SmartLogic.MSG_SENSOR_TITLE_LIST.length;
			
			if(msg.sContent.contains(SmartLogic.MSG_ACTION_GET_SENSOR_TITLE_LIST)){
				//���ش���������б�
				for(int i = 0; i < SmartLogic.MSG_SENSOR_TITLE_LIST.length; i++){
					sb.append(String.valueOf(i))
					  .append(":")
					  .append(SmartLogic.MSG_SENSOR_TITLE_LIST[i])
					  .append("\n");
				}
				msgResp.sContent = sb.toString();
				sendSMS(msgResp, true);
				return;
			}
			
			if(!msg.sContent.contains("#")){
				//������#��
				msg_action = SmartLogic.MSG_ACTION_UNDIFIND;
				msgResp.sContent = SmartLogic.MSG_ACTION_HELP;
				sendSMS(msgResp, true);
				return;
			}
			
			//�ж��Ƿ�������������
			for(int i = 1; i < length; i++){
				if(msg.sContent.contains("#" + SmartLogic.MSG_SENSOR_TITLE_LIST[i] + "#") || msg.sContent.contains("#" + i + "#")){
					iSensor = i;
					break;
				}
			}
			if(iSensor < 0){
				//���������������
				msg_action = SmartLogic.MSG_ACTION_UNDIFIND;
				msgResp.sContent = SmartLogic.MSG_ACTION_HELP;
				//"���������ƻ��߱�Ŵ���\n" + 
				//"�ظ�\"�������б�\"��ȡ���������ƺͱ��"
				sendSMS(msgResp, true);
				return;

			}else{
				
				sSensorTitle = SmartLogic.MSG_SENSOR_TITLE_LIST[iSensor];
				
				String para = null;
				int start,middle,end;
				
				//��һ���ж�ִ������ACTION
				for(String s : SmartLogic.MSG_ACTION_FILTER_READ_SENSOR_STATE){
					if(msg.sContent.contains(s)){
						msg_action = SmartLogic.MSG_ACTION_READ_SENSOR_STATE;
						Log.e(TAG, "msg_action = SmartLogic.MSG_ACTION_READ_SENSOR_STATE");
						Log.e(TAG, "s:" + s );
						break;
					}
				}
				for(String s : SmartLogic.MSG_ACTION_FILTER_SEND_CTRL_CMD){
					if(msg.sContent.contains(s) && msg.sContent.contains("[") && msg.sContent.contains("]") ){
						start = msg.sContent.indexOf("[");
						end = msg.sContent.indexOf("]");
						
						if(start > 0 && end > 0){
							msg_action = SmartLogic.MSG_ACTION_SEND_CTRL_CMD;
							Log.e(TAG, "msg_action = SmartLogic.MSG_ACTION_SEND_CTRL_CMD");
							para = msg.sContent.substring(start + 1, end);
							Log.e(TAG, "s:" + s + ", para: " + para);
							break;
						}
					}
				}
				for(String s : SmartLogic.MSG_ACTION_FILTER_EDIT_PARM){
					if(msg.sContent.contains(s) && msg.sContent.contains("[") &&  msg.sContent.contains("&&") && msg.sContent.contains("]") ){
						
						start = msg.sContent.indexOf("[");
						middle = msg.sContent.indexOf("&&");
						end = msg.sContent.indexOf("]");
						
						if(start > 0 && middle > 0 && end > 0 && start < middle && middle > end){
							msg_action = SmartLogic.MSG_ACTION_EDIT_PARM;
							Log.e(TAG, "msg_action = SmartLogic.MSG_ACTION_EDIT_PARM");
							para = msg.sContent.substring(start + 1, end);
							Log.e(TAG, "s:" + s + ", para: " + para);
							break;
						}
					}
				}
				
				int iCna = SharedPreferencesUtil.getCnaBySensorTitle(sSensorTitle);
				if(iCna < 0){
					msgResp.sContent = "δ�ҵ���Ӧ������\n" + 
					                   "�ظ�\"�������б�\"��ȡ���������ƺͱ��";
					sendSMS(msgResp, true);
					return;
				}

				int index = mPresenter.getNodeIndex(iCna);
				if (index < 0) {
					msgResp.sContent = "��ǰ" + sSensorTitle +  
							           "δ��������\n" + 
				                       "�������ݿ�...";
					sendSMS(msgResp, true);
					return;
				}
				
				Sensor sensor = listSensor.get(index);
				
				switch (msg_action) {
				case SmartLogic.MSG_ACTION_READ_SENSOR_STATE:
					if((msgResp.admin.permission & AdminEntity.PERMSSION_READ_SENSOR) == 0){
						sb.append("��û�и�Ȩ��(��ȡ����״̬)������ϵ��������Ա...");
						break;
					}
						
					sb.append("��ǰ")
					  .append(sSensorTitle)
					  .append("��Ϣ��")
					  .append(sensor.toString());
					
					break;

				case SmartLogic.MSG_ACTION_SEND_CTRL_CMD:
					if((msgResp.admin.permission & AdminEntity.PERMSSION_SEND_CMD) == 0){
						sb.append("��û�и�Ȩ��(���Ϳ�������)������ϵ��������Ա...");
						break;
					}

					sb.append(sSensorTitle)
					  .append("����ǰ״̬��");
					
					int cache = -1;
					PinIO ioState;
					if(iSensor == 1 || iSensor == 3 || iSensor == 5 || iSensor == 7){
						
						if(para != null && para.contains("��")){
							cache = 1;
							sb.append(sensor.sSensorData)
							  .append("\n����֮��״̬��")
							  .append("��");
						}else if(para != null && para.equals("��")){
							cache = 0;
							sb.append(sensor.sSensorData)
							  .append("\n����֮��״̬��")
							  .append("��");
						}else{
							sb.append(sensor.sSensorData).append("\nҪ���õ�״̬��").append(para).append("\n��֧�ֵ�״̬���ͣ�����...");
							break;
						}
						
						if((cache == 1 && !sensor.Bool) || (cache == 0 && sensor.Bool)){
							byte[] cmd = DataResolverUtil.EecodeData(sensor,DataResolverUtil.NODE_SET_RELAY, cache);
							mPresenter.sendCmd(cmd);
						}
						
						
					}else if(iSensor == 9){

						ioState = new PinIO(sensor.SensorData);

						if(PinIO.get(ioState, PinIO.ROLL_BLIND_TYPE) == PinIO.ROLL_BLIND_STATE_POSITIVE){
							sb.append("��������...");
						}else if(PinIO.get(ioState, PinIO.ROLL_BLIND_TYPE) == PinIO.ROLL_BLIND_STATE_NEGATIVE){
							sb.append("�����ر���...");
						}else{
							sb.append("����ֹͣ");
						}
						
						if(para != null && para.equals("��")){
							PinIO.set(ioState, 0, PinIO.ROLL_BLIND_STATE_POSITIVE);
						}else if(para != null && para.equals("��")){
							PinIO.set(ioState, 0, PinIO.ROLL_BLIND_STATE_NEGATIVE);
						}else if(para != null && para.equals("ͣ")){
							PinIO.set(ioState, 0, PinIO.ROLL_BLIND_STATE_STOP);
						}else{
							sb.append(sensor.sSensorData).append("\n��֧�ֵ�״̬���ͣ�����...");
							break;
						}

						cache = ioState.getSendCmdCache();
						byte[] cmd = DataResolverUtil.EecodeData(iCna, DataResolverUtil.NODE_SET_IO, cache);
						mPresenter.sendCmd(cmd);
						
					}else{

						sb.append(sensor.sSensorData)
						  .append("���������ʹ���,�ô����������ڿ��ƴ�����...");
						
					}
					
					break;

				case SmartLogic.MSG_ACTION_EDIT_PARM:
					
					break;
					
				case SmartLogic.MSG_ACTION_UNDIFIND:
				default:
					
					sb.append("����״̬������...\n")
					  .append("��ǰ")
					  .append(sSensorTitle)
					  .append("��Ϣ��")
					  .append(sensor.toString())
					  .append(sensor.sSensorData);
					break;
				}
				
				msgResp.sContent = sb.toString();
				sendSMS(msgResp, true);
			}
			
		}
		
	}
	
//	private void send(String number, String message){
//	    String SENT = "sms_sent";
//	    String DELIVERED = "sms_delivered";
//	   
//	    PendingIntent sentPI = PendingIntent.getService(this, 0, new Intent(SENT), 0);
//	    PendingIntent deliveredPI = PendingIntent.getService(this, 0, new Intent(DELIVERED), 0);
//	   
//	    registerReceiver(new BroadcastReceiver(){
//
//	            @Override
//	            public void onReceive(Context context, Intent intent) {
//	                switch(getResultCode())
//	                {
//	                    case Activity.RESULT_OK:
//	                        Log.i("====>", "Activity.RESULT_OK");
//	                        break;
//	                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//	                        Log.i("====>", "RESULT_ERROR_GENERIC_FAILURE");
//	                        break;
//	                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//	                        Log.i("====>", "RESULT_ERROR_NO_SERVICE");
//	                        break;
//	                    case SmsManager.RESULT_ERROR_NULL_PDU:
//	                        Log.i("====>", "RESULT_ERROR_NULL_PDU");
//	                        break;
//	                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//	                        Log.i("====>", "RESULT_ERROR_RADIO_OFF");
//	                        break;
//	                }
//	            }
//	    }, new IntentFilter(SENT));
//	   
//	    registerReceiver(new BroadcastReceiver(){
//	        @Override
//	        public void onReceive(Context context, Intent intent){
//	            switch(getResultCode())
//	            {
//	                case Activity.RESULT_OK:
//	                    Log.i("====>", "RESULT_OK");
//	                    break;
//	                case Activity.RESULT_CANCELED:
//	                    Log.i("=====>", "RESULT_CANCELED");
//	                    break;
//	            }
//	        }
//	    }, new IntentFilter(DELIVERED));
//	   
//	        SmsManager smsm = SmsManager.getDefault();
//	        smsm.sendTextMessage(number, null, message, sentPI, deliveredPI);
//	}
	
	private void sendSMS(MsgEntity msgResp, boolean isUiThread) {
		msgResp.iMsgType = MsgEntity.MSG_TYPE_SEND;
		String content = msgResp.sContent;
		
		SmsManager smsManager = SmsManager.getDefault();
		List<String> divideContents = smsManager.divideMessage(content);
		for (String text : divideContents) {
			smsManager.sendTextMessage(msgResp.admin.phone, null, text,
					null, null);
		}
		
		if (isUiThread) {
			mMsgList.add(msgResp);
			msgCallback.onReciverMsg(msgResp, isUiThread);
		}
		
//		else{
//			Message msg = mHandler.obtainMessage(2);
//			msg.arg1 = isUiThread ? 1:0; 
//			msg.obj = msgResp;
//			mHandler.sendMessage(msg);
//		}
		
		
	}
	
	private ArrayList<ZigBeeBean> listZigBeeBean;
	private ArrayList<SensorBean> listSensorBean;
	private ArrayList<ZigBee> listZigBee;
	private ArrayList<Sensor> listSensor;

	@Override
	public void notifyReciveSensor(int index, Sensor sensor) {
		String notifyMsgContent = null;
		Sensor ctrlSensor;
		int ctrState = -1;
		switch (sensor.sBean.iSensorType) {
			
		case FrameUtil.SENSOR_TYPE_ID_BH1750FVI:
			ctrlSensor = SmartLogic.sensorLight;
			ctrState = -1;
		    if(((int) sensor.Light) > SmartLogic.iLightup ){
				if(ctrlSensor != null && ctrlSensor.Bool == true){
			    	Log.e(TAG, "��ǿ�������ޣ��ر�ֲ��ƣ������ͱ�������");
			    	notifyMsgContent = "��ǿ�������ޣ��ر�ֲ���\n" + sensor;
					ctrState = 0;
				}
			}
		    if(((int) sensor.Light) < SmartLogic.iLightdown ){
				if(ctrlSensor != null && ctrlSensor.Bool == false){
			    	Log.e(TAG, "��ǿ�������ޣ���ֲ��ƣ������ͱ�������");
			    	notifyMsgContent = "��ǿ�������ޣ���ֲ���\n" + sensor;
					ctrState = 1;
				}
		    }
			if (ctrState != -1) {
				byte[] cmd = DataResolverUtil.EecodeData(ctrlSensor,DataResolverUtil.NODE_SET_RELAY, ctrState);
				sendCmd(cmd);
			}
			
			break;

		case FrameUtil.SENSOR_TYPE_ID_CO2:
			ctrlSensor = SmartLogic.sensorFan;
			ctrState = -1;
			if (((int) sensor.Concentration) > SmartLogic.iCup) {
				if (ctrlSensor != null && ctrlSensor.Bool == false) {
					Log.e(TAG, "CO2Ũ�ȳ������ޣ��򿪷��ȣ������ͱ�������");
			    	notifyMsgContent = "CO2Ũ�ȳ������ޣ��򿪷���\n" + sensor;
					ctrState = 1;
				}
			}
			if (((int) sensor.Concentration) < SmartLogic.iCdown) {
				if (ctrlSensor != null && ctrlSensor.Bool == true) {
					Log.e(TAG, "CO2Ũ�ȵ������ޣ��رշ��ȣ������ͱ�������");
			    	notifyMsgContent = "CO2Ũ�ȵ������ޣ��رշ���\n" + sensor;
					ctrState = 0;
				}
			}
			if (ctrState != -1) {
				byte[] cmd = DataResolverUtil.EecodeData(ctrlSensor,
						DataResolverUtil.NODE_SET_RELAY, ctrState);
				sendCmd(cmd);
			}

			break;
			
		case FrameUtil.SENSOR_TYPE_ID_SOIL:
			ctrlSensor = SmartLogic.sensorWaterPump;
			ctrState = -1;

			int iHumi = (int) (sensor.Humidity * 10);

			if (iHumi > SmartLogic.iHup) {
				if (ctrlSensor != null && ctrlSensor.Bool == true) {
					Log.e(TAG, "����ʪ�ȳ������ޣ��ر�ˮ�ã������ͱ�������");
			    	notifyMsgContent = "����ʪ�ȳ������ޣ��ر�ˮ��\n" + sensor;
					ctrState = 0;
				}
			}
			if (iHumi < SmartLogic.iHdown) {
				if (ctrlSensor != null && ctrlSensor.Bool == false) {
					Log.e(TAG, "����ʪ�ȵ������ޣ���ˮ�ã������ͱ�������");
			    	notifyMsgContent = "����ʪ�ȵ������ޣ���ˮ��\n" + sensor;
					ctrState = 1;
				}
			}
			if (ctrState != -1) {
				byte[] cmd = DataResolverUtil.EecodeData(ctrlSensor,
						DataResolverUtil.NODE_SET_RELAY, ctrState);
				sendCmd(cmd);
			}
			break;

		case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485:
			ctrlSensor = SmartLogic.sensorHeater;
			ctrState = -1;
			
			int iTemp = (int) (sensor.Temperature * 10);

			ctrState = -1;
			if (iTemp > SmartLogic.iTup) {
				if (ctrlSensor != null && ctrlSensor.Bool == true) {
					Log.e(TAG, "�����¶ȳ������ޣ��رռ������������ͱ�������");
			    	notifyMsgContent = "�����¶ȳ������ޣ��رռ�����\n" + sensor;
					ctrState = 0;
				}
			}
			if (iTemp < SmartLogic.iTdown) {
				if (ctrlSensor != null && ctrlSensor.Bool == false) {
					Log.e(TAG, "�����¶ȵ������ޣ��򿪼������������ͱ�������");
			    	notifyMsgContent = "�����¶ȵ������ޣ��򿪼�����\n" + sensor;
					ctrState = 1;
				}
			}
			if (ctrState != -1) {
				byte[] cmd = DataResolverUtil.EecodeData(ctrlSensor,
						DataResolverUtil.NODE_SET_RELAY, ctrState);
				sendCmd(cmd);
			}
			break;
			
		default:
			break;
		}
		if (ctrState != -1 && notifyMsgContent != null ) {
			Message msg = mHandler.obtainMessage(1);
			msg.obj = notifyMsgContent;
			mHandler.sendMessage(msg);
			//notifyAdminTask(notifyMsgContent);
		}
	}

	private void notifyAdminTask(String notifyMsgContent) {
		if(notifyMsgContent == null || notifyAdminList == null)
			return;
		
		if(notifyAdminList.size() == 0)
			return;
		
		for(AdminEntity admin : notifyAdminList){
			final MsgEntity notifyMsg = new MsgEntity(admin);
			notifyMsg.iMsgType = MsgEntity.MSG_TYPE_SEND;
			notifyMsg.sContent = admin.name + "��ע�⣬����һ����֪ͨ��" + notifyMsgContent + "";
			sendSMS(notifyMsg, true);
		}
		
	}

	@Override
	public void onSensorSettingReportCallback(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitZigBeeBeans(ArrayList<ZigBeeBean> zigbeeBeans) {
		// TODO Auto-generated method stub
		listZigBeeBean = zigbeeBeans;
	}

	@Override
	public void onInitSensorBeans(ArrayList<SensorBean> sensorBeans) {
		// TODO Auto-generated method stub
		listSensorBean = sensorBeans;
	}

	@Override
	public void onInitZigBees(ArrayList<ZigBee> zigbees) {
		// TODO Auto-generated method stub
		listZigBee = zigbees;
	}
	
	@Override
	public void onInitSensors(ArrayList<Sensor> sensors) {
		// TODO Auto-generated method stub
		listSensor = sensors;
	}

	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO Auto-generated method stub
		int iSensorType = sensor.sBean.iSensorType;
		if (iSensorType == FrameUtil.SENSOR_TYPE_ID_HEATER
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_WATER_PUMP
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_SOIL
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_FAN
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_CO2
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_LIGHT
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_BH1750FVI
				|| iSensorType == FrameUtil.SENSOR_TYPE_ID_PIN_IO
				) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return SharedPreferencesUtil.TYPE_SERVICE_PROXY;
	}

	@Override
	public void onErr(int errReason, String errTips) {
		// TODO Auto-generated method stub
		
	}

}
