package com.boxlab.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.boxlab.bean.Sensor;
import com.boxlab.platform.ActivitySmartFram;
import com.boxlab.platform.R;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.SmartLogic;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-10-29 上午11:25:24 
 * 类说明 
 */

public class FragmentAutoCtrl extends FragmentBase {
	
	protected static final String TAG = "FragmentAutoCtrl";
	private TextView tvTup;
	private TextView tvTdown;
	private TextView tvHup;
	private TextView tvHdown;
	private TextView tvCup;
	private TextView tvCdown;
	private VerticalSeekBar vsbTup;
	private VerticalSeekBar vsbTdown;
	private VerticalSeekBar vsbHup;
	private VerticalSeekBar vsbHdown;
	private VerticalSeekBar vsbCup;
	private VerticalSeekBar vsbCdown;

//	private int iTup;
//	private int iTdown;
//	private int iHup;
//	private int iHdown;
//	private int iCup;
//	private int iCdown;
//	private int iLup;
//	private int iLdown;
	
	private SmartLogic smartLogic;
	
	private TextView tvLup;
	private TextView tvLdown;
	private VerticalSeekBar vsbLup;
	private VerticalSeekBar vsbLdown;
	
	public FragmentAutoCtrl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		
		smartLogic = mApp.getSmartLogicInstance();
		
		//updateLogic();
		
		View rootView = inflater.inflate(R.layout.fragment_auto_ctrl,container, false);

		tvTup = (TextView) (rootView.findViewById(R.id.tvTup));
		tvTdown = (TextView) (rootView.findViewById(R.id.tvTdown));
		tvHup = (TextView) (rootView.findViewById(R.id.tvHup));
		tvHdown = (TextView) (rootView.findViewById(R.id.tvHdown));
		tvCup = (TextView) (rootView.findViewById(R.id.tvCup));
		tvCdown = (TextView) (rootView.findViewById(R.id.tvCdown));
		tvLup = (TextView) (rootView.findViewById(R.id.tvLup));
		tvLdown = (TextView) (rootView.findViewById(R.id.tvLdown));
		
		tvTup.setText(Float.toString((float)(SmartLogic.iTup / 10.0)));
		tvTdown.setText(Float.toString((float)(SmartLogic.iTdown / 10.0)));
		tvHup.setText(Float.toString((float)(SmartLogic.iHup / 10.0)));
		tvHdown.setText(Float.toString((float)(SmartLogic.iHdown / 10.0)));
		tvCup.setText(Integer.toString(SmartLogic.iCup));
		tvCdown.setText(Integer.toString(SmartLogic.iCdown));
		tvLup.setText(Integer.toString(SmartLogic.iLightup));
		tvLdown.setText(Integer.toString(SmartLogic.iLightdown));
		
		vsbTup = (VerticalSeekBar) (rootView.findViewById(R.id.vsbTup));
		vsbTdown = (VerticalSeekBar) (rootView.findViewById(R.id.vsbTdown));
		vsbHup = (VerticalSeekBar) (rootView.findViewById(R.id.vsbHup));
		vsbHdown = (VerticalSeekBar) (rootView.findViewById(R.id.vsbHdown));
		vsbCup = (VerticalSeekBar) (rootView.findViewById(R.id.vsbCup));
		vsbCdown = (VerticalSeekBar) (rootView.findViewById(R.id.vsbCdown));
		vsbLup = (VerticalSeekBar) (rootView.findViewById(R.id.vsbLup));
		vsbLdown = (VerticalSeekBar) (rootView.findViewById(R.id.vsbLdown));
		
		vsbTup.setProgress(SmartLogic.iTup);
		vsbTdown.setProgress(SmartLogic.iTdown);
		vsbHup.setProgress(SmartLogic.iHup);
		vsbHdown.setProgress(SmartLogic.iHdown);
		vsbCup.setProgress(SmartLogic.iCup);
		vsbCdown.setProgress(SmartLogic.iCdown);
		vsbLup.setProgress(SmartLogic.iLightup);
		vsbLdown.setProgress(SmartLogic.iLightdown);
		
		vsbTup.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		vsbTdown.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		vsbHup.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		vsbHdown.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		vsbCup.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		vsbCdown.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		vsbLup.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		vsbLdown.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		
		return rootView;
	}

	OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
		int start;
		int stop;
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			stop = seekBar.getProgress();
			Log.w(TAG, "onStopTrackingTouch [" + seekBar + ", stop=" + stop );
			updateLogic();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			start = seekBar.getProgress();
			Log.w(TAG, "onStartTrackingTouch [" + seekBar + ", start=" + start );
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			Log.w(TAG, "onProgressChanged [" + seekBar + ", progress=" + progress + ", fromUser=" + fromUser);
			if(fromUser == false)
			{
				if(seekBar == vsbTup){
					if(progress < vsbTdown.getProgress()){
						showMsg("温度上限：" + Float.toString((float)(progress / 10.0))+ "不能小于温度下限：" + tvTdown.getText());
						seekBar.setProgress(vsbTdown.getProgress() + 1);
						return;
					}
					tvTup.setText(Float.toString((float)(progress / 10.0)));
					SharedPreferencesUtil.setTemperatureUp(progress);
				}
				if(seekBar == vsbTdown){
					if(progress > vsbTup.getProgress()){
						showMsg("温度下限：" + Float.toString((float)(progress / 10.0))+ "不能大于温度上限：" + tvTup.getText());
						seekBar.setProgress(vsbTup.getProgress() - 1);
						return;
					}
					tvTdown.setText(Float.toString((float)(progress / 10.0)));
					SharedPreferencesUtil.setTemperatureDown(progress);
				}
				if(seekBar == vsbHup){
					if(progress < vsbHdown.getProgress()){
						showMsg("湿度上限：" + Float.toString((float)(progress / 10.0))+ "不能小于湿度下限：" + tvHdown.getText());
						seekBar.setProgress(vsbHdown.getProgress() + 1);
						return;
					}
					tvHup.setText(Float.toString((float)(progress / 10.0)));
					SharedPreferencesUtil.setHumidityUp(progress);
				}
				if(seekBar == vsbHdown){
					if(progress > vsbHup.getProgress()){
						showMsg("湿度下限：" + Float.toString((float)(progress / 10.0))+ "不能大于湿度上限：" + tvHup.getText());
						seekBar.setProgress(vsbHup.getProgress() - 1);
						return;
					}
					tvHdown.setText(Float.toString((float)(progress / 10.0)));
					SharedPreferencesUtil.setHumidityDown(progress);
				}
				if(seekBar == vsbCup){
					if(progress < vsbCdown.getProgress()){
						showMsg("浓度上限：" + progress + "不能小于浓度下限：" + tvCdown.getText());
						seekBar.setProgress(vsbCdown.getProgress() + 1);
						return;
					}
					tvCup.setText(Integer.toString(progress));
					SharedPreferencesUtil.setCO2Up(progress);
				}
				if(seekBar == vsbCdown){
					if(progress > vsbCup.getProgress()){
						showMsg("浓度下限：" + progress + "不能大于浓度上限：" + tvCup.getText());
						seekBar.setProgress(vsbCup.getProgress() - 1);
						return;
					}
					tvCdown.setText(Integer.toString(progress));
					SharedPreferencesUtil.setCO2Down(progress);
				}
				if(seekBar == vsbLup){
					if(progress < vsbLdown.getProgress()){
						showMsg("光强上限：" + progress + "不能小于光强下限：" + tvLdown.getText());
						seekBar.setProgress(vsbLdown.getProgress() + 1);
						return;
					}
					tvLup.setText(Integer.toString(progress));
					SharedPreferencesUtil.setLightUp(progress);
				}
				if(seekBar == vsbLdown){
					if(progress > vsbLup.getProgress()){
						showMsg("光强下限：" + progress + "不能大于光强上限：" + tvLup.getText());
						seekBar.setProgress(vsbLup.getProgress() - 1);
						return;
					}
					tvLdown.setText(Integer.toString(progress));
					SharedPreferencesUtil.setLightDown(progress);
				}
			}
			
		}
	};
	
	private void updateLogic() {
//		if(mActivity != null) 
//			SharedPreferencesUtil.getSharedPreferencesInstance(mActivity);
		
//		iTup = SharedPreferencesUtil.getTemperatureUp();
//		iTdown = SharedPreferencesUtil.getTemperatureDown();
//
//		iHup = SharedPreferencesUtil.getHumidityUp();
//		iHdown = SharedPreferencesUtil.getHumidityDown();
//
//		iCup = SharedPreferencesUtil.getCO2Up();
//		iCdown = SharedPreferencesUtil.getCO2Down();
//
//		iLightup = SharedPreferencesUtil.getLightUp();
//		iLightdown = SharedPreferencesUtil.getLightDown();
//		
//		SensorViewBase.updateLogic();
		
		SmartLogic.updateLogic();
		
	}
	
	private Toast mToast;
	
	protected void showMsg(String pMsg) {
		if(mActivity != null){
			if(mToast!=null)
				mToast.setText(pMsg);
			else
				mToast = Toast.makeText(mActivity, pMsg, Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
}
