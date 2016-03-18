package com.boxlab.platform;

import android.app.Application;
import java.util.LinkedList;
import java.util.List;

import com.boxlab.ndk.LEDScreenCtrl;
import com.boxlab.ndk.VoiceCtrl;
import com.boxlab.utils.SharedPreferencesUtil;
import com.boxlab.utils.SmartLogic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 13-10-12.
 */
public class BasicApp extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static BasicApp instance;

	private SharedPreferences mPref;

	private static SmartLogic smartLogic;

	// ************LED屏************//
	private static LEDScreenCtrl ledscreen = null;
	// ************语音报警************//
	private static VoiceCtrl voiceCtrl = null;

	public static BasicApp getInstance() {
		if (null == instance) {
			instance = new BasicApp();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);

	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mPref = SharedPreferencesUtil.getSharedPreferencesInstance(this);
		smartLogic = SmartLogic.getSmartLogicInstance(this);
		// 实例化
		ledscreen = new LEDScreenCtrl();
		voiceCtrl = new VoiceCtrl();

		// 初始化
		ledscreen.init("/dev/ttySAC1", 9600);
		voiceCtrl.init(this);

	}

	public SharedPreferences getSharedPreferencesInstance() {
		return mPref;
	}

	public SmartLogic getSmartLogicInstance() {
		return smartLogic;
	}

	public static LEDScreenCtrl getLedscreen() {
		return ledscreen;
	}

	public static VoiceCtrl getVoiceCtrl() {
		return voiceCtrl;
	}
	
}
