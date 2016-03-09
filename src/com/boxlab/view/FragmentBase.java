package com.boxlab.view;

import java.util.ArrayList;

import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.platform.BasicApp;
import com.boxlab.platform.ServiceProxy;
import com.boxlab.utils.SharedPreferencesUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-10-28 上午10:49:15 
 * 类说明 
 */

public class FragmentBase extends Fragment implements IListenerSensor{
		
    private static final String TAG = "FragmentBase";

	protected ServiceProxy mServiceInFragment;
    
    protected ArrayList<ZigBeeBean> listZigBeeBean;
    protected ArrayList<SensorBean> listSensorBean;
    protected ArrayList<Sensor> listSensor;
    protected ArrayList<ZigBee> listZigBee;
    
    protected boolean isActive = false;

    protected Activity mActivity;

	private FragmentBaseCallbacks mCallbacks;

	protected BasicApp mApp;

	protected SharedPreferences mPref;
    
    public interface FragmentBaseCallbacks {
    	
    	public static final int FRAGMENT_STATE_ON_ATTACH = 0;
    	public static final int FRAGMENT_STATE_ON_CREATE = 1;
    	public static final int FRAGMENT_STATE_ON_CREATE_VIEW = 2;
    	public static final int FRAGMENT_STATE_ON_ACTIVITY_CREATED = 3;
    	public static final int FRAGMENT_STATE_ON_START = 4;
    	public static final int FRAGMENT_STATE_ON_RESUME = 5;
    	public static final int FRAGMENT_STATE_ON_PAUSE = 6;
    	public static final int FRAGMENT_STATE_ON_STOP = 7;
    	public static final int FRAGMENT_STATE_ON_DESTORY_VIEW = 8;
    	public static final int FRAGMENT_STATE_ON_DESTORY = 9;
		public static final int FRAGMENT_STATE_ON_DETACH = 10;

		public void onFragmentBaseStateChange(Fragment fragment, int fragmentState);
		
    }

	private static FragmentBaseCallbacks sDummyCallbacks = new FragmentBaseCallbacks() {
		
		@Override
		public void onFragmentBaseStateChange(Fragment fragment,int fragmentState){
			Log.i(TAG,"Thi is sDummyCallbacks, " +
					"Fragment:" + fragment + "; " +
					"State:" + fragmentState);
		}
		
	};
	
	public FragmentBase() {
		// TODO Auto-generated constructor stub
	}

	// 按照Fragment生命周期
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		if(mActivity == null || mActivity != activity){
			Log.w(TAG,"onAttach new activity");
			mActivity = activity;
		}

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof FragmentBaseCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement FragmentBase's callbacks.");
		}

		mCallbacks = (FragmentBaseCallbacks) activity;
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_ATTACH);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_CREATE);
		
		mApp = (BasicApp) mActivity.getApplication();
		mPref = mApp.getSharedPreferencesInstance();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_CREATE_VIEW);
		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_ACTIVITY_CREATED);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_START);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		isActive = true;
		if(mServiceInFragment != null)
			mServiceInFragment.registerView(this);
		
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_RESUME);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		isActive = false;
		if(mServiceInFragment != null)
			mServiceInFragment.unregisterView(this);
		
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_PAUSE);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_STOP);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_DESTORY_VIEW);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(mServiceInFragment != null)
			mServiceInFragment = null;

		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_DESTORY);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		
		mCallbacks.onFragmentBaseStateChange(this, FragmentBaseCallbacks.FRAGMENT_STATE_ON_DETACH);
		mCallbacks = sDummyCallbacks;
		
		if(mActivity != null)
			mActivity = null;
		
		if(mServiceInFragment != null)
			mServiceInFragment = null;

	}
	
	public void proxyServiceConnect(ServiceProxy service) {
		if(service != null){
			mServiceInFragment = service;
			//mServiceInFragment.registerFragmentView(this);
		}
	}
	
	public void proxyServiceDisconnect(){
		if(mServiceInFragment != null)
			mServiceInFragment = null;
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
		return true;
	}

	@Override
	public void notifyReciveSensor(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		Log.w(TAG, sensor.toString());
	}

	@Override
	public void onSensorSettingReportCallback(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		Log.w(TAG, sensor.toString());
		//Toast.makeText(mActivity, "节点参数设置成功！" + sensor, Toast.LENGTH_SHORT).show();
	}

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return SharedPreferencesUtil.TYPE_FRAGMENT;
	}

	@Override
	public void onErr(int errReason, String errTips) {
		// TODO Auto-generated method stub
		
	}
	

}
