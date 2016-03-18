package com.android.serialport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.android.serialport.SerialPort;

//����ͨ���߳�
public class UartThread extends Thread{
    private Handler mHandler = null;
    private SerialPort mSerialPort = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    private byte[] recvBuf;
    private int recvLen;
    private boolean isRunThread;

    public UartThread(Handler handler)
    {
        this.mHandler = handler;
        this.recvLen = 0;
        this.mSerialPort = null;
        this.mInStream = null;
        this.mOutStream = null;
    }

    private void showMsg(String str)
    {
        Message msgStr = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("Message", str);
        msgStr.what = 0;
        msgStr.setData(bundle);
        mHandler.sendMessage(msgStr);
    }

    private void finish()
    {
        Message msg = new Message();
        msg.what = 3;
        mHandler.sendMessage(msg);
        isRunThread = false;
    }

    public void openUart(String uartPort, int buadrate)
    {
        this.mSerialPort = openSerialPort(uartPort, buadrate);
        this.mInStream = this.mSerialPort.getInputStream();
        this.mOutStream = this.mSerialPort.getOutputStream();
        isRunThread = true;
        showMsg("[��ʾ]: �򿪴��ڳɹ���\n");
    }

    private SerialPort openSerialPort(String uartPort, int buadrate)
    {
        SerialPort nSerialPort = null;
        try {
            nSerialPort = new SerialPort(new File(uartPort), buadrate);
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            showMsg("[����]: �򿪴��ڳ����쳣��");
            showMsg("ԭ��: " + e.toString() + "\n");
            finish();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            showMsg("[����]: �򿪴��ڳ����쳣��");
            showMsg("ԭ��: " + e.toString() + "\n");
            finish();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            showMsg("[����]: �򿪴��ڳ����쳣��");
            showMsg("ԭ��: " + e.toString() + "\n");
            finish();
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
                showMsg("[����]: �رմ����������쳣��");
                showMsg("ԭ��: " + e.toString() + "\n");
            }
            this.mInStream = null;
        }
        if (this.mOutStream != null)
        {
            try {
                this.mOutStream.close();
            } catch (IOException e) {
                showMsg("[����]: �رմ���������쳣��");
                showMsg("ԭ��: " + e.toString() + "\n");
            }
            this.mOutStream = null;
        }
        if (this.mSerialPort != null)
        {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
        showMsg("[��ʾ]: �رմ��ڳɹ���\n");
    }

    //�򴮿ڷ�������
    public void sendData(byte[] sendBuf)
    {
        if(this.mSerialPort != null && this.mOutStream != null)
        {
            if(sendBuf.length > 0)
            {
                try
                {
                    this.mOutStream.flush();
                    this.mOutStream.write(sendBuf);
                    this.mOutStream.flush();
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("SendData", sendBuf);
                    msg.what = 2;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
                catch (IOException e)
                {
                    showMsg("[����]: ���ʹ������ݳ����쳣��");
                    showMsg("ԭ��: " + e.toString() + "\n");
                    finish();
                }
            }
        }
    }
        
    @Override
    public void run()
    {
        super.run();
        while(isRunThread)
        {
            if (this.mSerialPort != null && this.mInStream != null)
            {
            	
                try {                    
                    if(this.mInStream.available() > 0)
                    {
                    	this.recvLen = this.mInStream.available();
                        this.recvBuf = new byte[this.recvLen];
                        this.mInStream.read(this.recvBuf, 0, this.recvLen);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("RecvData", this.recvBuf);
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                    else
                    {
                    	try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
		                    // TODO Auto-generated catch block
		                    showMsg("[����]: ���մ������ݳ����쳣��");
		                    showMsg("ԭ��: " + e.toString() + "\n");
		                    finish();
						}
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    showMsg("[����]: ���մ������ݳ����쳣��");
                    showMsg("ԭ��: " + e.toString() + "\n");
                    finish();
                }
            }
        }
    }
}
