package com.boxlab.platform;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.interfaces.IListenerBase;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.ndk.LEDScreenCtrl;
import com.boxlab.ndk.RS485Ctrl;
import com.boxlab.ndk.VoiceCtrl;
import com.boxlab.utils.SharedPreferencesUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * 基本的Activiry类
 * 
 */
public abstract class ActivityBase extends Activity implements IListenerSensor {

	private static final String TAG = "ActivityBase";
	protected BasicApp mApp;
	protected SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mApp = (BasicApp) getApplication();
		mPref = mApp.getSharedPreferencesInstance();
		
	}

	protected static final int SHOW_TIME = Toast.LENGTH_SHORT;
	private Toast mToast;

	protected ArrayList<ZigBeeBean> listZigBeeBean;
	protected ArrayList<SensorBean> listSensorBean;
	protected ArrayList<ZigBee> listZigBee;
	protected ArrayList<Sensor> listSensor;

	/**
	 * 信息显示（Toast）
	 * 
	 * @param pMsg
	 *            String
	 */
	protected void showToast(String pMsg) {

		if (mToast != null)
			mToast.setText(pMsg);
		else
			mToast = Toast.makeText(this, pMsg, SHOW_TIME);

		mToast.show();
	}

	/**
	 * 打开Activity
	 * 
	 * @param pClass
	 *            Class<?>
	 */
	protected void openActivity(Class<?> pClass) {
		Intent intent = new Intent();
		intent.setClass(this, pClass);
		startActivity(intent);
	}

	/**
	 * 获取LayoutInflater对象
	 */
	public LayoutInflater getLayoutInflater() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		return layoutInflater;
	}

	/**
	 * 获取服务器本地IP地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {

		}
		return null;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Log.i(TAG, "call bindService()");
		// 绑定服务
		Intent intent = new Intent(this, ServiceProxy.class);
		bindService(intent, mConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if (mBound) {
			Log.i(TAG, "call unbindService()");
			// 取消注册
			mService.unregisterView(ActivityBase.this);
			unbindService(mConnection);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	protected ServiceProxy mService;
	protected boolean mBound;

	protected ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected()");
			mBound = false;

			ActivityBase.this.onServiceDisconnected();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected()");

			// 返回一个Service对象
			mService = ((ServiceProxy.LocalBinder) service).getService();

			// 注册回调接口
			mService.registerView(ActivityBase.this);

			mBound = true;

			ActivityBase.this.onServiceConnected();
		}
	};

	protected abstract void onServiceConnected();

	protected abstract void onServiceDisconnected();

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return 0;
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
}
