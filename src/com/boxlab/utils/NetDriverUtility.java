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
 * @version 创建时间：2015-6-1 上午10:28:45 
 * 类说明 
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
	 * 单例模式
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
			
			// 检测网口是否正确
			String[] netNames = NetDeviceInterface.getDeviceNames();
			for (int i = 0; i < netNames.length; i++) {
				if (netNames[i].compareToIgnoreCase(selectNetName) == 0) {
					isFoundNetName = true;
					break;
				}
			}
			
			if(isFoundNetName == false){
				showMsg("[提示]：[NetUtility]未找到网口" + selectNetName);
				showMsg("[提示]：[NetUtility]启用网口" + selectNetName);
			}
			
			if ((!selectNetName.isEmpty()) && (!selectNetName.equals(""))) {
				// 设置网络接口
				cmd = "setprop dhcp." + selectNetName + ".reason BOUND";
				ShellInterface.runCommand(cmd);
				cmd = "setprop dhcp." + selectNetName + ".result ok";
				ShellInterface.runCommand(cmd);
				
				if (!(_strIP.isEmpty()) && !(_strIP.equals("")) && !(_strMask.isEmpty()) && !(_strMask.equals(""))) {
					// 设置IP地址和子网掩码
					cmd = "ifconfig " + selectNetName + " "	+ _strIP + " " + "netmask " + _strMask;
					ShellInterface.runCommand(cmd);
				}
				if (!(_strGateWay.isEmpty()) && !(_strGateWay.equals(""))) {
					// 设置默认网关
					cmd = "route add default gw " + _strGateWay	+ " dev " + selectNetName;
					ShellInterface.runCommand(cmd);
				}
				if (!(_strDns1.isEmpty()) && !(_strDns1.equals(""))) {
					// 设置DNS1
					cmd = "setprop net." + selectNetName + ".dns1 "	+ _strDns1;
					ShellInterface.runCommand(cmd);
				}
				if (!(_strDns2.isEmpty()) && !(_strDns2.equals(""))) {
					// 设置DNS2
					cmd = "setprop net." + selectNetName + ".dns2 "	+ _strDns2;
					ShellInterface.runCommand(cmd);
				}
				
				// 使网口使能
				ShellInterface.runCommand("ifconfig " + selectNetName + " up");
				
				if(isFoundNetName == false){
					showMsg("[提示]：[NetUtility]启用网口" + selectNetName + "完成");
				}
				
				// 检测网络接口参数是否判断成功
				String checkIp = NetDeviceInterface.getDeviceIP(selectNetName);
				//String checkIp = NetDeviceInterface.getWiFiAddress((Activity) mContext);
				Log.i(TAG,"checkIp = " + checkIp);
				if ((!checkIp.isEmpty()) && (!checkIp.equals(""))) {
					showMsg("[提示]：[NetUtility]网络参数配置成功.\r\n网口：" + selectNetName 
							+ "\r\nIP地址:" + _strIP 
							+ "\r\n子网掩码：" +  _strMask
							+ "\r\n网关：" +  _strGateWay 
							+ "\r\nDns1：" + _strDns1
							+ "\r\nDns2：" + _strDns2);
					ret = true;
				} else {
					showMsg("[提示]：[NetUtility]网络参数错误\r\n网口：" + selectNetName 
							+ "\r\nIP地址:" + _strIP 
							+ "\r\n子网掩码：" +  _strMask
							+ "\r\n网关：" +  _strGateWay 
							+ "\r\nDns1：" + _strDns1
							+ "\r\nDns2：" + _strDns2
							+ "\r\n请重新检查参数");
					ret = false;
				}
			}else{
				showMsg("[提示]：网络接口名称错误");
			}
		}else{
			showMsg("[提示]：权限错误");
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
