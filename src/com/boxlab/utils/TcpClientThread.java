package com.boxlab.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** 
 * @author .Next E-mail: 
 * @version 创建时间：2015-5-28 下午5:36:31 
 * 类说明 
 */

public class TcpClientThread extends Thread {
	
	private static final String TAG = "ThreadClient";

	public static final int TCP_CLIENT_SHOW_MSG = 0x10;
	public static final int TCP_CLIENT_RECV_DATA = 0x11;
	public static final int TCP_CLIENT_SEND_DATA = 0x12;
	public static final int TCP_CLIENT_THREAD_FINISH = 0x13;
	
	public String socketAddr;
	public int socketPort;
	
	private Socket mSocketClient = null;
	private Handler mHandler = null;
	
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private PrintStream out = null;
	private BufferedReader in = null;

	private byte[] recvBuf;
	private int recvLen;
	
	private boolean isRunThread = false;

	public TcpClientThread(Handler pHandler, String pServerAddr, int pServerPort) {
		// TODO Auto-generated constructor stub
		this.mHandler = pHandler;
        this.recvBuf = new byte[64];
        this.recvLen = 0;
        
        this.socketAddr = pServerAddr;
        this.socketPort = pServerPort;
        
		//connectToServer(pServerAddr, pServerPort);
	}
	
	public void sendData(byte[] data,int len){
		
		if (this.mSocketClient != null && this.mOutputStream != null) {
			if (len > 0) {
				try {
					Log.i(TAG, "sendData()");
					this.mOutputStream.flush();
					this.mOutputStream.write(data, 0, len );
					this.mOutputStream.flush();
					showSendData(data, len);
				} catch (IOException e) {
					showMsg("[错误]：[Tcp Client]向服务端：" + socketAddr + "发送Tcp数据出现异常。");
					showMsg("原因：" + e.toString());
					finish();
				}
			}
		}
	}
	
    private void showSendData(byte[] data, int len) {
    	final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < len; i++) {
			byte byteChar = data[i];
			stringBuilder.append(String.format("[%02X] ",byteChar));
		}
		Log.i(TAG, "send Len: " + len);
		Log.i(TAG, stringBuilder.toString());
//		Message msg = mHandler.obtainMessage(TCP_CLIENT_SEND_DATA);
//		Bundle bundle = new Bundle();
//		bundle.putByteArray("SendData", data);
//		msg.setData(bundle);
//		mHandler.sendMessage(msg);
	}

	@Override
    public void run()
    {
        super.run();
        while(isRunThread)
        {
            if (this.mSocketClient != null && this.mInputStream != null)
            {
                try {
                	this.recvLen = this.mInputStream.read(this.recvBuf, 0, 64);
                    //this.recvLen = this.mInputStream.available();
                    if(this.recvLen > 0)
                    {
						final StringBuilder stringBuilder = new StringBuilder(recvLen);
						for (int i = 0; i < this.recvLen; i++) {
							byte byteChar = this.recvBuf[i];
							stringBuilder.append(String.format("[%02X] ",
									byteChar));
						}
						Log.i(TAG, "recvLen: " + this.recvLen);
						Log.i(TAG, "recvBuf: " + this.recvBuf);
						Log.i(TAG, stringBuilder.toString());
						Message msg = mHandler.obtainMessage(TCP_CLIENT_RECV_DATA);
						Bundle bundle = new Bundle();
						bundle.putString("Server", this.socketAddr);
						bundle.putInt("RecvLen", this.recvLen);
						bundle.putByteArray("RecvData", this.recvBuf);
						msg.setData(bundle);
						mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
					showMsg("[错误]：[Tcp Client]从服务端：" + socketAddr + "接收Tcp数据出现异常。");
                    showMsg("原因：" + e.toString());
                    finish();
                }
            }
        }
    }
    
    public boolean connectToServer(){
    	
//		socketAddr = pServerAddr;
//		socketPort = pServerPort;
		
		Log.e(TAG, "socketAddr:" + socketAddr);
		Log.e(TAG, "socketPort:" + socketPort);
		
		try {
			mSocketClient = new Socket(socketAddr,socketPort);
			isRunThread = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.e(TAG,"[Tcp Client:" + socketAddr + "-->Port:" + socketPort 
					+ "]Socket套接字构造异常\r\n原因：客户端无法连接到主机：" + socketAddr + "\r\n因为："+ e.toString());
			showMsg("[错误]：[Tcp Client:" + socketAddr + "-->Port:" + socketPort 
					+ "]Socket套接字构造异常\r\n原因：客户端无法连接到主机：" + socketAddr + "\r\n因为："+ e.toString());
			isRunThread = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG,"[Tcp Client:" + socketAddr + "-->Port:" + socketPort 
					+ "]Socket套接字构造异常:" + e.toString());
			showMsg("[错误]：[Tcp Client:" + socketAddr + "-->Port:" + socketPort 
					+ "]Socket套接字构造异常:" + e.toString());
			isRunThread = false;
			return false;
		}
		
		socketAddr = mSocketClient.getRemoteSocketAddress().toString();
		int index = socketAddr.indexOf('/');
		if (index >= 0){
            socketAddr = socketAddr.substring(index + 1);
        }else{
            socketAddr = "Unknown";
        }
		
		try {
			mOutputStream = mSocketClient.getOutputStream();
			mInputStream = mSocketClient.getInputStream();
			out = new PrintStream(mOutputStream);
			in = new BufferedReader(new InputStreamReader(mInputStream));
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG,"[Tcp Client]:获取套接字" + socketAddr + "输入输出流异常:" + e.toString());
			showMsg("[错误]：[Tcp Client]:获取套接字" + socketAddr + "输入输出流异常\r\n原因：" + e.toString());
			closeSocket();
			
			isRunThread = false;
			return false;
			
		}
		
		isRunThread = true;
		return isRunThread;
    	
    }

    /**
	 * 关闭socket
	 */
	public void closeSocket(){
		isRunThread = false;
		if(mSocketClient != null){
			try {
				if(out != null){
					out.close();
					out = null;
				}
				if(in != null){
					in.close();
					in = null;
				}
				if(mInputStream !=null){
					mInputStream.close();
					mInputStream = null;
				}
				if(mOutputStream !=null){
					mOutputStream.close();
					mOutputStream = null;
				}
				
				mSocketClient.close();
				mSocketClient = null;
				
				Log.i(TAG,"关闭客户端连接(" + socketAddr + ")");
				showMsg("[提示]：[Tcp Client:" + socketAddr + "]关闭客户端连接");
				
			} catch (IOException e) {
				Log.e(TAG,"closeSocket() throw IOException on (" + socketAddr + "):" + e.toString());
				showMsg("[提示]：[Tcp Client:" + socketAddr + "]关闭客户端连接异常\r\n原因：" + e.toString());
			}
		}
	}

    private void showMsg(String str)
    {
//        Message msgStr = mHandler.obtainMessage(TCP_CLIENT_SHOW_MSG);
//        Bundle bundle = new Bundle();
//        bundle.putString("Message", str);
//        msgStr.setData(bundle);
//        mHandler.sendMessage(msgStr);
    	Log.w(TAG, str);
    }
    
    private void finish()
    {
        Message msg = mHandler.obtainMessage(TCP_CLIENT_THREAD_FINISH);
        mHandler.sendMessage(msg);
    }
}
