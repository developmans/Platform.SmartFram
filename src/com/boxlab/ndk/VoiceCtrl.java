package com.boxlab.ndk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

public class VoiceCtrl {
	private static String TAG = "VoiceCtrl"; 	
	// �����ϳɶ���
	private SpeechSynthesizer mTts;
	// Ĭ�Ϸ�����
	private String voicer = "xiaoyan";
	// �������
	private int mPercentForBuffering = 0;
	// ���Ž���
	private int mPercentForPlaying = 0;
	
	private SharedPreferences mSharedPreferences;
	
	public boolean init(Context context){
		// ��ʼ���ϳɶ���
		SpeechUtility.createUtility(context, "appid=" + "56e127f3");
		mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
		mSharedPreferences = context.getSharedPreferences("com.iflytek.setting", Context.MODE_PRIVATE);
		return true;
	}
	
	public boolean display(String info){
		setParam();
		int code = mTts.startSpeaking(info, mTtsListener);
//		/** 
//		 * ֻ������Ƶ�����в��Žӿ�,���ô˽ӿ���ע��startSpeaking�ӿ�
//		 * text:Ҫ�ϳɵ��ı���uri:��Ҫ�������Ƶȫ·����listener:�ص��ӿ�
//		*/
//		String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//		int code = mTts.synthesizeToUri(text, path, mTtsListener);
		if (code != ErrorCode.SUCCESS) {
			if(code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED){
				Log.i(TAG,"������δ��װ: " + code);	
				return false;
			}else {
				Log.i(TAG,"�����ϳ�ʧ��,������: " + code);	
				return false;
			}
		}
		return true;
	}
	
	public void stop(){
		mTts.stopSpeaking();
	}
	
	/**
	 * ��ʼ��������
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		Log.i(TAG,"��ʼ��ʧ��,�����룺"+code);
        	} else {
				// ��ʼ���ɹ���֮����Ե���startSpeaking����
        		// ע���еĿ�������onCreate�����д�����ϳɶ���֮�����Ͼ͵���startSpeaking���кϳɣ�
        		// ��ȷ�������ǽ�onCreate�е�startSpeaking������������
			}		
		}
	};
	
	/**
	 * �ϳɻص�������
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		
		@Override
		public void onSpeakBegin() {
			Log.i(TAG,"��ʼ����");
		}

		@Override
		public void onSpeakPaused() {
			Log.i(TAG,"��ͣ����");
		}

		@Override
		public void onSpeakResumed() {
			Log.i(TAG,"��������");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// �ϳɽ���
			mPercentForBuffering = percent;
			Log.i(TAG,mPercentForBuffering+"");
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// ���Ž���
			mPercentForPlaying = percent;
			Log.i(TAG, mPercentForPlaying+"");
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				Log.i(TAG,"�������");
			} else if (error != null) {
				Log.i(TAG,error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// ���´������ڻ�ȡ���ƶ˵ĻỰid����ҵ�����ʱ���Ựid�ṩ������֧����Ա�������ڲ�ѯ�Ự��־����λ����ԭ��
			// ��ʹ�ñ����������ỰidΪnull
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};
	
	public void close(){
		mTts.stopSpeaking();
		// �˳�ʱ�ͷ�����
		mTts.destroy();
	}
	
	/**
	 * ��������
	 * @param param
	 * @return 
	 */
	private void setParam(){
		// ��ղ���
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// ���ݺϳ�����������Ӧ����
//		if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
//			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//			// �������ߺϳɷ�����
//			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
//			//���úϳ�����
//			mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
//			//���úϳ�����
//			mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
//			//���úϳ�����
//			mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
//		}else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			// ���ñ��غϳɷ����� voicerΪ�գ�Ĭ��ͨ����ǽ���ָ�������ˡ�
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
			/**
			 * TODO ���غϳɲ��������١�������������Ĭ��ʹ���������
			 * �����������Զ����������ο����ߺϳɲ�������
			 */
//		}
		//���ò�������Ƶ������
		mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
		// ���ò��źϳ���Ƶ������ֲ��ţ�Ĭ��Ϊtrue
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		
		// ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
		// ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
	}
	
}
