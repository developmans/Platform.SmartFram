/**
 * 
 */
package com.boxlab.utils;

import com.android.serialport.ShellInterface;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** 
 * @author .Next E-mail: 
 * @version ����ʱ�䣺2015-6-1 ����10:28:45 
 * ��˵�� 
 */

/**
 * @author .Next
 *
 */
public class NetDriverUtility {

	private static final String TAG = "NetUtility";

	private static NetDriverUtility INSTANCE;

	private String mNetName;
	private String mIp;
	private int mPort;
	private String mMask;
	private String mGateway;
	private String mDNS1;
	private String mDNS2;
	
	/**
	 * ����ģʽ
	 * @param pHandler 
	 * @param pContext 
	 */
	public static NetDriverUtility getNetUtility(Context pContext, Handler pHandler) {
		if(INSTANCE == null){
			INSTANCE = new NetDriverUtility();
		}
		return INSTANCE;
	}
	
	public String getNetDeviceName(){
		return mNetName;
	}	
	
	public String getLocalIp(){
		return mIp;
	}
	
	public int getLocalPort( ){
		return mPort;
	}
	
	public String getLocalMask(){
		return mMask;
	}
	
	public String getLocalGateway(){
		return mGateway;
	}
	
	public NetDriverUtility setNetDeviceName(String pNetName){
		mNetName = pNetName;
		return NetDriverUtility.this;
	}	
	
	public NetDriverUtility setLocalIp(String pIp){
		mIp = pIp;
		return NetDriverUtility.this;
	}
	
	public NetDriverUtility setLocalPort(int pPort){
		mPort = pPort;
		return NetDriverUtility.this;
	}
	
	public NetDriverUtility setLocalMask(String pMask){
		mMask = pMask;
		return NetDriverUtility.this;
	}
	
	public NetDriverUtility setLocalGateway(String pGateway){
		mGateway = pGateway;
		return NetDriverUtility.this;
	}
	
	public NetDriverUtility setLocalDNS(String pDNS1,String pDNS2){
		mDNS1 = pDNS1;
		mDNS2 = pDNS2;
		return NetDriverUtility.this;
	}
	
	public boolean initNetParam(){
		
		boolean ret = false;
		boolean isFoundNetName = false;
		
		if (ShellInterface.isSuAvailable()) {
			String selectNetName = mNetName;
			String _strIP = mIp;
			String _strMask = mMask;
			String _strGateWay = mGateway;
			String _strDns1 = mDNS1;
			String _strDns2 = mDNS2;

			String cmd;
			
			// ��������Ƿ���ȷ
			String[] netNames = NetDeviceInterface.getDeviceNames();
			for (int i = 0; i < netNames.length; i++) {
				if (netNames[i].compareToIgnoreCase(selectNetName) == 0) {
					isFoundNetName = true;
					break;
				}
			}
			
			if(isFoundNetName == false){
				showMsg("[��ʾ]��[NetUtility]δ�ҵ�����" + selectNetName);
				showMsg("[��ʾ]��[NetUtility]��������" + selectNetName);
			}
			
			if ((!selectNetName.isEmpty()) && (!selectNetName.equals(""))) {
				// ��������ӿ�
				cmd = "setprop dhcp." + selectNetName + ".reason BOUND";
				ShellInterface.runCommand(cmd);
				cmd = "setprop dhcp." + selectNetName + ".result ok";
				ShellInterface.runCommand(cmd);
				
				if (!(_strIP.isEmpty()) && !(_strIP.equals("")) && !(_strMask.isEmpty()) && !(_strMask.equals(""))) {
					// ����IP��ַ����������
					cmd = "ifconfig " + selectNetName + " "	+ _strIP + " " + "netmask " + _strMask;
					ShellInterface.runCommand(cmd);
				}
				if (!(_strGateWay.isEmpty()) && !(_strGateWay.equals(""))) {
					// ����Ĭ������
					cmd = "route add default gw " + _strGateWay	+ " dev " + selectNetName;
					ShellInterface.runCommand(cmd);
				}
				if (!(_strDns1.isEmpty()) && !(_strDns1.equals(""))) {
					// ����DNS1
					cmd = "setprop net." + selectNetName + ".dns1 "	+ _strDns1;
					ShellInterface.runCommand(cmd);
				}
				if (!(_strDns2.isEmpty()) && !(_strDns2.equals(""))) {
					// ����DNS2
					cmd = "setprop net." + selectNetName + ".dns2 "	+ _strDns2;
					ShellInterface.runCommand(cmd);
				}
				
				// ʹ����ʹ��
				ShellInterface.runCommand("ifconfig " + selectNetName + " up");
				
				if(isFoundNetName == false){
					showMsg("[��ʾ]��[NetUtility]��������" + selectNetName + "���");
				}
				
				// �������ӿڲ����Ƿ��жϳɹ�
				String checkIp = NetDeviceInterface.getDeviceIP(selectNetName);
				//String checkIp = NetDeviceInterface.getWiFiAddress((Activity) mContext);
				Log.i(TAG,"checkIp = " + checkIp);
				if ((!checkIp.isEmpty()) && (!checkIp.equals(""))) {
					showMsg("[��ʾ]��[NetUtility]����������óɹ�.\r\n���ڣ�" + selectNetName 
							+ "\r\nIP��ַ:" + _strIP 
							+ "\r\n�������룺" +  _strMask
							+ "\r\n���أ�" +  _strGateWay 
							+ "\r\nDns1��" + _strDns1
							+ "\r\nDns2��" + _strDns2);
					ret = true;
				} else {
					showMsg("[��ʾ]��[NetUtility]�����������\r\n���ڣ�" + selectNetName 
							+ "\r\nIP��ַ:" + _strIP 
							+ "\r\n�������룺" +  _strMask
							+ "\r\n���أ�" +  _strGateWay 
							+ "\r\nDns1��" + _strDns1
							+ "\r\nDns2��" + _strDns2
							+ "\r\n�����¼�����");
					ret = false;
				}
			}else{
				showMsg("[��ʾ]������ӿ����ƴ���");
			}
		}else{
			showMsg("[��ʾ]��Ȩ�޴���");
		}
		return ret;
	}
	
    private void showMsg(String str)
    {
    	Log.w(TAG, str);
//        Message msgStr = new Message();
//        Bundle bundle = new Bundle();
//        bundle.putString("Message", str);
//        msgStr.what = 0;
//        msgStr.setData(bundle);
//    	mHandler .sendMessage(msgStr);
    }
    
}
