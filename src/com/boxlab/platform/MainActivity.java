package com.boxlab.platform;


import com.boxlab.platform.R;

import android.os.Bundle;
import android.os.Messenger;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btSmartHome;
	private Button btSmartFarm;
	private Button btSmartSetting;
	private Button btSmartSms;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		btSmartHome = (Button) findViewById(R.id.btSmartHome);
		btSmartFarm = (Button) findViewById(R.id.btSmartFarm);
		btSmartSetting = (Button) findViewById(R.id.btSmartSetting);
		btSmartSms = (Button) findViewById(R.id.btSmartSms);
		
		btSmartHome.setOnClickListener(mOnClickListener);
		btSmartFarm.setOnClickListener(mOnClickListener);
		btSmartSetting.setOnClickListener(mOnClickListener);
		btSmartSms.setOnClickListener(mOnClickListener);
		
		Intent intent = new Intent(MainActivity.this, ServiceProxy.class);
		startService(intent);
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
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
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent(MainActivity.this, ServiceProxy.class);
		stopService(intent);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
        	Intent intent; 
        	
			switch (v.getId()) {
			case R.id.btSmartHome:
				intent = new Intent(MainActivity.this, ActivitySmartHome_.class);
        		startActivity(intent);
				
				break;
				
			case R.id.btSmartFarm:
				intent = new Intent(MainActivity.this, ActivitySmartFram.class);
        		startActivity(intent);
        		
				break;
				
			case R.id.btSmartSms:
				intent = new Intent(MainActivity.this, ActivitySMS.class);
        		startActivity(intent);
        		
				break;
			case R.id.btSmartSetting:
				intent = new Intent(MainActivity.this, ActivityPreference.class);
        		startActivity(intent);
        		//Messenger rMessenger = new Messenger(service);  
				break;

			default:
				break;
			}
			
			
		}
	};

}
