package com.boxlab.utils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;

import com.android.serialport.ShellInterface;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetDeviceInterface {
	private static final String TAG = "NetDeviceInterface";
	
	private static String byteArray2HexStr(byte[] bytes, boolean isSplitStr, String splitStr) 
	{
	    StringBuilder buf = new StringBuilder();
	    for(int i = 0; i < bytes.length; i++) 
		{
	        int val = bytes[i] & 0xFF;
	        if (val < 0x10)
	        {
	        	buf.append("0");
	        }
	        buf.append(Integer.toHexString(val).toUpperCase());
	        if(i != (bytes.length - 1))
	        {
		        if(isSplitStr)
		        {
		        	buf.append(splitStr);
		        }
	        }
	    }
	    return buf.toString();
	}
	
	private static long HexStr2Long(String hexStr)
	{
		long rtn = 0;
		
		if(hexStr.length() > 0)
		{
			rtn = Integer.parseInt(hexStr, 16);
		}
		return rtn;
	}
	
    //��ʮ����������ʽת����1.0.0.127��ʽ��IP��ַ   
	private static String Long2IpStr(long ipVal)
    {   
        StringBuffer ipStr = new StringBuffer("");   
        //����24λ��0   
        ipStr.append(String.valueOf((ipVal & 0x000000FF)));  
        ipStr.append(".");    
        //����16λ��0��Ȼ������8λ   
        ipStr.append(String.valueOf((ipVal & 0x0000FFFF) >>> 8));  
        ipStr.append(".");  
        //����8λ��0��Ȼ������16λ   
        ipStr.append(String.valueOf((ipVal & 0x00FFFFFF) >>> 16));  
        ipStr.append(".");   
        //ֱ������24λ   
        ipStr.append(String.valueOf((ipVal >>> 24))); 
        return ipStr.toString();   
    }
	
	/**
	 * ��ȡ����ӿ�����(���磺eth0)
	 * @return
	 */
	public static String[] getDeviceNames()
	{
		ArrayList<String> netInterfaceList = new ArrayList<String>();
		String[] netInterfaces;
		int index = 0;
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			// �������õ�����ӿ�
			while (en.hasMoreElements()) {
				// �õ�ÿһ������ӿڰ󶨵�����IP
				NetworkInterface netDB = en.nextElement();
				Enumeration<InetAddress> netAddr = netDB.getInetAddresses();
				// ����ÿһ���ӿڰ󶨵�����IP
				while (netAddr.hasMoreElements()) {
					InetAddress ipAddr = netAddr.nextElement();
					if (!ipAddr.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ipAddr
									.getHostAddress())) {
						netInterfaceList.add(netDB.getDisplayName().toString());
						// netInterfaces[index++] =
						// netDB.getDisplayName().toString();
					}
				}
			}
		} catch (SocketException e) {
			Log.d(TAG, "getNetName() SocketException:" + e.toString());
		}
		netInterfaces = new String[netInterfaceList.size()];
		netInterfaces = netInterfaceList.toArray(netInterfaces);
		return netInterfaces;
	}
	
	//��ȡ����ӿ�Ĭ������
	public static String getDeviceGateway(String deviceName)	
	{
		String netGateway = "";
		
		if(deviceName.isEmpty())
		{
			return netGateway;
		}		
		if(ShellInterface.isSuAvailable())
		{					
			String routeList = "";
			String routeFlag = deviceName + "\t00000000\t";
			
			routeList = ShellInterface.getProcessOutput("cat /proc/net/route");				
			int id = routeList.indexOf(routeFlag);
			if(id >= 0)
			{
				netGateway = routeList.substring(id + routeFlag.length(), id + routeFlag.length() + 8);
				netGateway = Long2IpStr(HexStr2Long(netGateway));
			}
		}
		return netGateway;
	}	
	
	//��ȡ����ӿ�DNS
	public static String getDeviceDNS(String deviceName)	
	{
		String netDNS = "";
		
		if(deviceName.isEmpty())
		{
			return netDNS;
		}
		if(ShellInterface.isSuAvailable())
		{					
			netDNS = ShellInterface.getProcessOutput("getprop net.dns1");				
			if(!netDNS.isEmpty())
			{
				netDNS += '\t';
			}
			netDNS += ShellInterface.getProcessOutput("getprop net.dns2");
		}
		return netDNS;
	}
	
	//��ȡ����ӿ�IP
	public static String getDeviceIP(String deviceName)
	{
		String netIP = ""; 
		
		if(deviceName.isEmpty())
		{
			return netIP;
		}
		try
		{
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			//�������õ�����ӿ�
			while (en.hasMoreElements())
			{
		    	//�õ�ÿһ������ӿڰ󶨵�����IP
		        NetworkInterface netDB = en.nextElement(); 
		        Enumeration<InetAddress> netAddr = netDB.getInetAddresses();
		        //����ÿһ���ӿڰ󶨵�����IP
		        while (netAddr.hasMoreElements())
		        {
		        	InetAddress ipAddr = netAddr.nextElement();
		        	if (!ipAddr.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipAddr.getHostAddress()))
		        	{
		        		if(deviceName.equals(netDB.getDisplayName().toString()))
		        		{
		        			netIP = ipAddr.getHostAddress().toString();
		        		}		        		
		        	}		        	
		        }
			}
		}
		catch (SocketException e){
			Log.d(TAG, "getNetIP() SocketException:" + e.toString());
		}
		return netIP;
	}
	
	//��ȡ����ӿ�MAC
	public static String getDeviceMAC(String deviceName)
	{
		String netMAC = ""; 
		
		if(deviceName.isEmpty())
		{
			return netMAC;
		}
		try
		{
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			//�������õ�����ӿ�
			while (en.hasMoreElements())
			{
		    	//�õ�ÿһ������ӿڰ󶨵�����IP
		        NetworkInterface netDB = en.nextElement(); 
		        Enumeration<InetAddress> netAddr = netDB.getInetAddresses();
		        //����ÿһ���ӿڰ󶨵�����IP
		        while (netAddr.hasMoreElements())
		        {
		        	InetAddress ipAddr = netAddr.nextElement();
		        	if (!ipAddr.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipAddr.getHostAddress()))
		        	{
		        		if(deviceName.equals(netDB.getDisplayName().toString()))
		        		{
			        		byte[] macAddr = netDB.getHardwareAddress();
			        		
			        		netMAC = byteArray2HexStr(macAddr, true, "-");
		        		}
		        	}		        	
		        }
			}
		}
		catch (SocketException e){
			Log.d(TAG, "getNetMAC() SocketException:" + e.toString());
		}
		return netMAC;
	}
	
	//��ȡ����ӿ���������
	public static String getDeviceMask(String deviceName)
	{
		String netMASK = ""; 
		
		if(deviceName.isEmpty())
		{
			return netMASK;
		}
		try
		{
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			//�������õ�����ӿ�
			while (en.hasMoreElements())
			{
		    	//�õ�ÿһ������ӿڰ󶨵�����IP
		        NetworkInterface netDB = en.nextElement(); 
		        Enumeration<InetAddress> netAddr = netDB.getInetAddresses();
		        //����ÿһ���ӿڰ󶨵�����IP
		        while (netAddr.hasMoreElements())
		        {
		        	InetAddress ipAddr = netAddr.nextElement();
		        	if (!ipAddr.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipAddr.getHostAddress()))
		        	{
		        		if(deviceName.equals(netDB.getDisplayName().toString()))
		        		{
			        		List<InterfaceAddress> netlist = netDB.getInterfaceAddresses();
					        //��������Ķ�����1�ĸ���
					        int mask = netlist.get(0).getNetworkPrefixLength();				        
				            StringBuilder maskStr = new StringBuilder();
				            int[] maskIp = new int[4];
				            for (int i = 0; i < maskIp.length; i++) 
				            {
				                maskIp[i] = (mask >= 8) ? 255 : (mask > 0 ? (mask & 0xFF) : 0);
				                mask -= 8;
				                maskStr.append(maskIp[i]);
				                if (i < maskIp.length-1) 
				                {
				                	maskStr.append(".");
				                }
				            }		        		
			        		netMASK = maskStr.toString();
		        		}
		        	}		        	
		        }
			}
		}
		catch (SocketException e){
			Log.d(TAG, "getNetDeviceMask() SocketException:" + e.toString());
		}
		return netMASK;
	}
	
	public  static String getWiFiAddress(Activity pActivity) {
		WifiManager wifiManager = (WifiManager) pActivity.getSystemService(Context.WIFI_SERVICE);
		// �ж�wifi�Ƿ���
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		DhcpInfo dhcoInfo = wifiManager.getDhcpInfo();
		int ipAddress = wifiInfo.getIpAddress();
		
		Log.d(TAG, "getWiFiAddress() Ip Addr:" + Long2IpStr(ipAddress) 
				+ "\r\nDhcp-> addr:" + Long2IpStr(dhcoInfo.ipAddress)
				+ "\r\nDhcp-> gateway:" + Long2IpStr(dhcoInfo.gateway)
				+ "\r\nDhcp-> netmask:" + Long2IpStr(dhcoInfo.netmask)
				+ "\r\nDhcp-> dns1:" + Long2IpStr(dhcoInfo.dns1)
				+ "\r\nDhcp-> dns2:" + Long2IpStr(dhcoInfo.dns2));
		return Long2IpStr(ipAddress);
	}
	
	public static String getWiFiNetMask(Activity pActivity) {
		DhcpInfo dhcoInfo = getDhcpInfo(pActivity);
		return Long2IpStr(dhcoInfo.netmask);
	}
	
	public static String getWiFiGateway(Activity pActivity) {
		DhcpInfo dhcoInfo = getDhcpInfo(pActivity);
		return Long2IpStr(dhcoInfo.gateway);
	}
	
	public static String getWiFiDns1(Activity pActivity) {
		DhcpInfo dhcoInfo = getDhcpInfo(pActivity);
		return Long2IpStr(dhcoInfo.dns1);
	}
	
	public static String getWiFiDns2(Activity pActivity) {
		DhcpInfo dhcoInfo = getDhcpInfo(pActivity);
		return Long2IpStr(dhcoInfo.dns2);
	}
	
	private static DhcpInfo getDhcpInfo(Activity pActivity){

		WifiManager wifiManager = (WifiManager) pActivity.getSystemService(Context.WIFI_SERVICE);
		// �ж�wifi�Ƿ���
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		DhcpInfo getDhcpInfo = wifiManager.getDhcpInfo();
		return getDhcpInfo;
	}
}
