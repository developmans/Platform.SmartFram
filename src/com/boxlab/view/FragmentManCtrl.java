package com.boxlab.view;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boxlab.bean.Sensor;
import com.boxlab.platform.R;
import com.boxlab.utils.DataResolverUtil;
import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.PinIO;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.StringUtil;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-10-30 上午10:43:10 
 * 类说明 
 */

public class FragmentManCtrl extends FragmentBase implements android.view.View.OnClickListener,OnCheckedChangeListener{

	private static final String TAG = "FragmentManCtrl";
	
	private LinearLayout llCtrlHeater;
	private LinearLayout llCtrlSunlight;
	private LinearLayout llCtrlWater;
	private LinearLayout llCtrlFan;
	
	private TextView tvCtrlHeater;
	private TextView tvCtrlSunlight;
	private TextView tvCtrlWater;
	private TextView tvCtrlFan;
	
	private CheckBox cbCtrlHeater;
	private CheckBox cbCtrlSunlight;
	private CheckBox cbCtrlWater;
	private CheckBox cbCtrlFan;
	
	private LinearLayout llCtrlHeaterSlide;
	private LinearLayout llCtrlSunlightSlide;
	private LinearLayout llCtrlWaterSlide;
	private LinearLayout llCtrlFanSlide;

	private Animation slideDown;
	private Animation slideUp;
	private LinearLayout llCtrlSliding;
	
	private int iCnaHeater;
	private int iCnaLight;
	private int iCnaWaterPump;
	private int iCnaFan;
	
	private boolean bStateHeater;
	private boolean bStateLight;
	private boolean bStateWaterPump;
	private boolean bStateFan;
	private LinearLayout llCtrlRollBlind;
	private LinearLayout llCtrlRollBlindSlide;
	private TextView tvCtrlRollBlind;
	private ImageButton imCtrlRollBlindUp;
	private ImageButton imCtrlRollBlindStop;
	private ImageButton imCtrlRollBlindDown;
	private int iCnaRollBlind;
	private int iStateRollBlind;
	private PinIO ioStateRollBlind;

	public FragmentManCtrl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.fragment_man_ctrl,container, false);
		
		llCtrlHeater = (LinearLayout) rootView.findViewById(R.id.llCtrlHeater);
		llCtrlSunlight = (LinearLayout) rootView.findViewById(R.id.llCtrlSunlight);
		llCtrlWater = (LinearLayout) rootView.findViewById(R.id.llCtrlWater);
		llCtrlFan = (LinearLayout) rootView.findViewById(R.id.llCtrlFan);
		llCtrlRollBlind = (LinearLayout) rootView.findViewById(R.id.llCtrlRollBlind);
		
		llCtrlHeaterSlide = (LinearLayout) rootView.findViewById(R.id.llCtrlHeaterSlide);
		llCtrlSunlightSlide = (LinearLayout) rootView.findViewById(R.id.llCtrlSunlightSlide);
		llCtrlWaterSlide = (LinearLayout) rootView.findViewById(R.id.llCtrlWaterSlide);
		llCtrlFanSlide = (LinearLayout) rootView.findViewById(R.id.llCtrlFanSlide);
		llCtrlRollBlindSlide = (LinearLayout) rootView.findViewById(R.id.llCtrlRollBlindSlide);
		
		tvCtrlHeater = (TextView) rootView.findViewById(R.id.tvCtrlHeater);
		tvCtrlSunlight = (TextView) rootView.findViewById(R.id.tvCtrlSunlight);
		tvCtrlWater = (TextView) rootView.findViewById(R.id.tvCtrlWater);
		tvCtrlFan = (TextView) rootView.findViewById(R.id.tvCtrlFan);
		tvCtrlRollBlind = (TextView) rootView.findViewById(R.id.tvCtrlRollBlind);
		
		tvCtrlHeater.setOnClickListener(this);
		tvCtrlSunlight.setOnClickListener(this);
		tvCtrlWater.setOnClickListener(this);
		tvCtrlFan.setOnClickListener(this);
		tvCtrlRollBlind.setOnClickListener(this);
		
		cbCtrlHeater = (CheckBox) rootView.findViewById(R.id.cbCtrlHeater);
		cbCtrlSunlight = (CheckBox) rootView.findViewById(R.id.cbCtrlSunlight);
		cbCtrlWater = (CheckBox) rootView.findViewById(R.id.cbCtrlWater);
		cbCtrlFan = (CheckBox) rootView.findViewById(R.id.cbCtrlFan);
		
		imCtrlRollBlindUp = (ImageButton) rootView.findViewById(R.id.imCtrlRollBlindUp);
		imCtrlRollBlindStop = (ImageButton) rootView.findViewById(R.id.imCtrlRollBlindStop);
		imCtrlRollBlindDown = (ImageButton) rootView.findViewById(R.id.imCtrlRollBlindDown);
		
		//mPref = SharedPreferencesUtil.getSharedPreferencesInstance(mActivity);

		iCnaHeater = mPref.getInt(getResources().getString(R.string.SvbTvTitleHeater) + "iCna",-1);
		bStateHeater = mPref.getBoolean(getResources().getString(R.string.SvbTvTitleHeater) + "bSensorData", false);
		cbCtrlHeater.setChecked(bStateHeater);
		
		iCnaLight = mPref.getInt(getResources().getString(R.string.SvbTvTitleLight)	+ "iCna", -1);
		bStateLight = mPref.getBoolean(getResources().getString(R.string.SvbTvTitleLight) + "bSensorData", false);
		cbCtrlSunlight.setChecked(bStateLight);
		
		iCnaWaterPump = mPref.getInt(getResources().getString(R.string.SvbTvTitleWaterPump) + "iCna", -1);
		bStateWaterPump = mPref.getBoolean(getResources().getString(R.string.SvbTvTitleWaterPump) + "bSensorData", false);
		cbCtrlWater.setChecked(bStateWaterPump);
		
		iCnaFan = mPref.getInt(getResources().getString(R.string.SvbTvTitleFan) + "iCna", -1);
		bStateFan = mPref.getBoolean(getResources().getString(R.string.SvbTvTitleFan) + "bSensorData", false);
		cbCtrlFan.setChecked(bStateFan);
		
		iCnaRollBlind = mPref.getInt(getResources().getString(R.string.SvbTvTitleRollBlind) + "iCna", -1);
		iStateRollBlind = mPref.getInt(getResources().getString(R.string.SvbTvTitleRollBlind) + "iPinIOState", 0x00ff);
		ioStateRollBlind = new PinIO(iStateRollBlind);

		if(PinIO.get(ioStateRollBlind, PinIO.ROLL_BLIND_TYPE) == PinIO.ROLL_BLIND_STATE_POSITIVE){
			imCtrlRollBlindDown.setEnabled(true);
			imCtrlRollBlindUp.setEnabled(false);
		}else if(PinIO.get(ioStateRollBlind, PinIO.ROLL_BLIND_TYPE) == PinIO.ROLL_BLIND_STATE_NEGATIVE){
			imCtrlRollBlindDown.setEnabled(false);
			imCtrlRollBlindUp.setEnabled(true);
		}else{
			imCtrlRollBlindDown.setEnabled(true);
			imCtrlRollBlindUp.setEnabled(true);
		}

		imCtrlRollBlindDown.setOnClickListener(this);
		imCtrlRollBlindStop.setOnClickListener(this);
		imCtrlRollBlindUp.setOnClickListener(this);
		
		cbCtrlHeater.setOnCheckedChangeListener(this);
		cbCtrlSunlight.setOnCheckedChangeListener(this);
		cbCtrlWater.setOnCheckedChangeListener(this);
		cbCtrlFan.setOnCheckedChangeListener(this);

		slideDown = AnimationUtils.loadAnimation(mActivity, R.anim.slide_dowm);
		slideUp = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up);
		//slideDown.setAnimationListener(animationListener);
		slideUp.setAnimationListener(animationListener);
		
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO Auto-generated method stub
		int iCna = sensor.sBean.iCna;
		int iSensorType = sensor.sBean.iSensorType;
		if ((iSensorType == FrameUtil.SENSOR_TYPE_ID_HEATER && iCna == iCnaHeater)
				|| (iSensorType == FrameUtil.SENSOR_TYPE_ID_LIGHT && iCna == iCnaLight)
				|| (iSensorType == FrameUtil.SENSOR_TYPE_ID_WATER_PUMP && iCna == iCnaWaterPump)
				|| (iSensorType == FrameUtil.SENSOR_TYPE_ID_FAN && iCna == iCnaFan)
				|| (iSensorType == FrameUtil.SENSOR_TYPE_ID_PIN_IO && iCna == iCnaRollBlind)
				) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void notifyReciveSensor(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		//super.notifyReciveSensor(index, sensor);
		
//		switch (sensor.sBean.iSensorType) {
//		case FrameUtil.SENSOR_TYPE_ID_HEATER:
//			
//			break;
//
//		default:
//			break;
//		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tvCtrlHeater:
			if(llCtrlHeaterSlide.getVisibility() == View.INVISIBLE){
				llCtrlHeaterSlide.startAnimation(slideDown);
				llCtrlHeaterSlide.setVisibility(View.VISIBLE);
			}else{
				llCtrlSliding = llCtrlHeaterSlide;
				llCtrlHeaterSlide.startAnimation(slideUp);
			}
			break;
			
		case R.id.tvCtrlSunlight:
			if(llCtrlSunlightSlide.getVisibility() == View.INVISIBLE){
				llCtrlSunlightSlide.startAnimation(slideDown);
				llCtrlSunlightSlide.setVisibility(View.VISIBLE);
			}else{
				llCtrlSliding = llCtrlSunlightSlide;
				llCtrlSunlightSlide.startAnimation(slideUp);
			}
			break;
			
		case R.id.tvCtrlWater:
			if(llCtrlWaterSlide.getVisibility() == View.INVISIBLE){
				llCtrlWaterSlide.startAnimation(slideDown);
				llCtrlWaterSlide.setVisibility(View.VISIBLE);
			}else{
				llCtrlSliding = llCtrlWaterSlide;
				llCtrlWaterSlide.startAnimation(slideUp);
			}
			break;
			
		case R.id.tvCtrlFan:
			if(llCtrlFanSlide.getVisibility() == View.INVISIBLE){
				llCtrlFanSlide.startAnimation(slideDown);
				llCtrlFanSlide.setVisibility(View.VISIBLE);
			}else{
				llCtrlSliding = llCtrlFanSlide;
				llCtrlFanSlide.startAnimation(slideUp);
			}			
			break;

		case R.id.imCtrlRollBlindDown:
			setRollBlind(PinIO.ROLL_BLIND_STATE_POSITIVE);
			imCtrlRollBlindDown.setEnabled(false);
			imCtrlRollBlindUp.setEnabled(true);
			break;
			
		case R.id.imCtrlRollBlindStop:
			setRollBlind(PinIO.ROLL_BLIND_STATE_STOP);
			imCtrlRollBlindDown.setEnabled(true);
			imCtrlRollBlindUp.setEnabled(true);
			break;
			
		case R.id.imCtrlRollBlindUp:
			setRollBlind(PinIO.ROLL_BLIND_STATE_NEGATIVE);
			imCtrlRollBlindDown.setEnabled(true);
			imCtrlRollBlindUp.setEnabled(false);
			imCtrlRollBlindUp.startAnimation(slideUp);
			break;

		default:
			break;
		}
	}
	
	public void setRollBlind(int state) {
		// TODO Auto-generated method stub
		Log.e(TAG, "设置卷帘\n  setRollBlind = " + state );
		
		if(mServiceInFragment == null)
			return;
		
		int iCna = -1;
		iCna = mPref.getInt(getResources().getString(R.string.SvbTvTitleRollBlind) + "iCna", -1);

		if(state == PinIO.ROLL_BLIND_STATE_POSITIVE){
			PinIO.set(ioStateRollBlind, 0, PinIO.ROLL_BLIND_STATE_POSITIVE);
		}else if(state == PinIO.ROLL_BLIND_STATE_NEGATIVE){
			PinIO.set(ioStateRollBlind, 0, PinIO.ROLL_BLIND_STATE_NEGATIVE);
		}else{
			PinIO.set(ioStateRollBlind, 0, PinIO.ROLL_BLIND_STATE_STOP);
		}

		int cache = ioStateRollBlind.getSendCmdCache();
		
		byte[] cmd = null;
		
		Log.e(TAG, "  iCna = " + StringUtil.getHexStringFormatShort(iCna) + "; cache = " + StringUtil.getHexStringFormatShort(cache));

		if(iCna != -1){
			cmd = DataResolverUtil.EecodeData(iCna, DataResolverUtil.NODE_SET_IO, cache);
			showProcessBarDlg();
			mServiceInFragment.sendCmd(cmd);
		}
	}
	
	private AnimationListener animationListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if(llCtrlSliding != null && llCtrlSliding.getVisibility() == View.VISIBLE){
				llCtrlSliding.setVisibility(View.INVISIBLE);
				llCtrlSliding = null;
			}
		}
	};

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		Log.e(TAG, "isChecked = " + isChecked );
		
		if(mServiceInFragment == null)
			return;
		
		int iCna = -1;
		byte[] cmd = null;
		int cache = 0;
		
		if(isChecked)
			cache = 1;
		else
			cache = 0;
		
		switch (buttonView.getId()) {
		case R.id.cbCtrlHeater:
			iCna = mPref.getInt(getResources().getString(R.string.SvbTvTitleHeater) + "iCna", -1);
			break;
		case R.id.cbCtrlSunlight:
			iCna = mPref.getInt(getResources().getString(R.string.SvbTvTitleLight) + "iCna", -1);
			break;
		case R.id.cbCtrlWater:
			iCna = mPref.getInt(getResources().getString(R.string.SvbTvTitleWaterPump) + "iCna", -1);
			break;
		case R.id.cbCtrlFan:
			iCna = mPref.getInt(getResources().getString(R.string.SvbTvTitleFan) + "iCna", -1);
			break;

		default:
			break;
		}
		
		Log.e(TAG, "  iCna = " + StringUtil.getHexStringFormatShort(iCna) + "; cache = " + StringUtil.getHexStringFormatShort(cache));

		if(iCna != -1){
			cmd = DataResolverUtil.EecodeData(iCna, DataResolverUtil.NODE_SET_RELAY, cache);
			showProcessBarDlg();
			mServiceInFragment.sendCmd(cmd);
		}
	}
	
	DlgFragmentProcessBar dlg;
	
	private void showProcessBarDlg() {
		// TODO Auto-generated method stub
		dlg = new DlgFragmentProcessBar();
		dlg.setStyle(DialogFragment.STYLE_NO_INPUT, 0);
		dlg.show(getFragmentManager(), "DlgFragmentProcessBar");

		mHandler.sendEmptyMessageDelayed(2, 8000);
	}
	
	public class DlgFragmentProcessBar extends DialogFragment {
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View v = inflater.inflate(R.layout.dlg_processbar, container, false);
	        return v;
	    }
	}
	

	@Override
	public void onSensorSettingReportCallback(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		super.onSensorSettingReportCallback(index, sensor);
		
		Message msg = mHandler.obtainMessage(1);
		msg.arg1 = index;
		msg.obj = sensor;
		mHandler.sendMessage(msg);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if(dlg.isVisible())
					dlg.dismiss();
				
				Sensor sensor = (Sensor) msg.obj;
				Toast.makeText(mActivity, "设置节点参数成功!!!\n\r" + sensor, Toast.LENGTH_SHORT).show();
				
				break;

			case 2:
				if(dlg.isVisible()){
					dlg.dismiss();
					Toast.makeText(mActivity, "设置节点参数超时...\n\r请检查该节点状态..." , Toast.LENGTH_SHORT).show();
				}
				
				break;
			default:
				break;
			}
		}
		
	};
}
