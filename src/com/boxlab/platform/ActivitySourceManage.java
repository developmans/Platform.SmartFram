package com.boxlab.platform;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.opengl.ETC1;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.boxlab.adapter.MyListAdapter_Base;
import com.boxlab.adapter.MyListAdapter_Source;
import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.bean.SourceBean;
import com.boxlab.interfaces.IListenerSensor;
import com.boxlab.model.NodeEngineModel;
import com.boxlab.ndk.RfidCtrl;
import com.boxlab.utils.SharedPreferencesUtil;

;

/**
 * @author Next
 * @version 1.0 E-mail: caiwl2005@126.com 创建时间：2015-9-14 下午3:58:34 类说明
 */

public class ActivitySourceManage extends Activity implements OnClickListener {

	protected static final String TAG = "ActivitySourceManage";
	private static Context mContext;
	private LinearLayout layout1 = null;
	private LinearLayout layout2 = null;
	private static NodeEngineModel nodeEngineModel = null;
	private RfidCtrl rfidCtrl = null;
	private TimerTask task = null;
	private Timer timer = null;

	// 基本信息列表
	private ListView listview_base = null;
	private MyListAdapter_Base adapter_base = null;
	private List<SourceBean> list_base = null;
	public static List<Boolean> checkBoxs = null;
	private static Map<Integer, String> checkmap = null;
	// 溯源信息列表
	private ListView listview_source = null;
	private MyListAdapter_Source adapter_source = null;
	private List<Sensor> list_source = null;

	// 操作选择
	private RadioGroup radioGroup = null;
	// 信息控件
	private static EditText ed_rfid = null;
	private static EditText ed_varieties = null;
	private static Spinner sp_region = null;
	private static EditText ed_plant = null;
	private static EditText ed_harvest = null;
	private CheckBox checkboxall = null;

	private int chooseType = 0;// 1添加、2修改、3删除、4查询

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_source);
		mContext=this;
		init();
	}

	private void init() {

		layout1 = (LinearLayout) findViewById(R.id.l1);
		layout2 = (LinearLayout) findViewById(R.id.l2);
		nodeEngineModel = new NodeEngineModel(this);
		rfidCtrl = new RfidCtrl();
		// 基本信息列表
		list_base = new ArrayList<SourceBean>();
		checkBoxs = new ArrayList<Boolean>();
		checkmap = new HashMap<Integer, String>();
		list_base.addAll(nodeEngineModel.loadSourceBeans());
		initCheckBox(false);
		listview_base = (ListView) findViewById(R.id.listView_base);
		adapter_base = new MyListAdapter_Base(this,
				R.layout.activity_base_item, list_base);
		listview_base.setAdapter(adapter_base);
		listview_base.setFocusable(true);
		listview_base.setOnItemClickListener(listOnItemClickListener);
		// 溯源信息列表
		list_source = new ArrayList<Sensor>();
		listview_source = (ListView) findViewById(R.id.listView_source);
		adapter_source = new MyListAdapter_Source(this,
				R.layout.activity_source_item, list_source);
		listview_source.setAdapter(adapter_source);
		checkboxall = (CheckBox) findViewById(R.id.checkBoxAll);
		checkboxall.setOnClickListener(this);
		// radioGroup选择
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(rgOnCheckedChangeListener);
		// 信息显示初始
		ed_rfid = (EditText) findViewById(R.id.ed_rfid);
		ed_varieties = (EditText) findViewById(R.id.ed_varieties);
		ed_plant = (EditText) findViewById(R.id.ed_plant);
		ed_harvest = (EditText) findViewById(R.id.ed_harvest);
		ed_plant.setOnClickListener(this);
		ed_harvest.setOnClickListener(this);
		sp_region = (Spinner) findViewById(R.id.sp_region);

		rfidCtrl.init();
		task = new TimerTask() {
			public void run() {
				rfidCtrl.findRfid();
			}
		};

		new Handler().postDelayed(new Runnable() {

			public void run() {
				rfidCtrl.initReader();
			}
		}, 500);

		timer = new Timer();
		timer.schedule(task, 500, 500);

	}

	private void initCheckBox(Boolean bool) {
		checkBoxs.clear();
		for (int i = 0; i < list_base.size(); i++) {
			checkBoxs.add(bool);
		}
	}

	private OnItemClickListener listOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (checkBoxs.get(position)) {
				checkmap.remove(position);
				checkBoxs.set(position, false);
				checkboxall.setChecked(false);
				adapter_base.notifyDataSetChanged();
			} else {
				checkmap.put(position, list_base.get(position).getsRfid());
				checkBoxs.set(position, true);
				adapter_base.notifyDataSetChanged();
			}
		}
	};

	// RadioGroup点击事件
	private RadioGroup.OnCheckedChangeListener rgOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			switch (arg1) {
			case R.id.radioAdd:
				chooseType = 1;
				cleanEdit();
				layout1.setVisibility(LinearLayout.VISIBLE);
				layout2.setVisibility(LinearLayout.GONE);
				ed_rfid.setEnabled(true);
				ed_varieties.setEnabled(true);
				sp_region.setClickable(true);
				ed_plant.setEnabled(true);
				ed_harvest.setEnabled(false);
				break;
			case R.id.radioUpdate:
				chooseType = 2;
				cleanEdit();
				layout1.setVisibility(LinearLayout.VISIBLE);
				layout2.setVisibility(LinearLayout.GONE);
				ed_rfid.setEnabled(true);
				ed_varieties.setEnabled(true);
				sp_region.setClickable(true);
				ed_plant.setEnabled(true);
				ed_harvest.setEnabled(true);
				break;
			case R.id.radioDelete:
				chooseType = 3;
				cleanEdit();
				layout1.setVisibility(LinearLayout.VISIBLE);
				layout2.setVisibility(LinearLayout.GONE);
				ed_rfid.setEnabled(false);
				ed_varieties.setEnabled(false);
				sp_region.setClickable(false);
				ed_plant.setEnabled(false);
				ed_harvest.setEnabled(false);
				break;
			case R.id.radioSelect:
				chooseType = 4;
				cleanEdit();
				layout1.setVisibility(LinearLayout.GONE);
				layout2.setVisibility(LinearLayout.VISIBLE);
				ed_rfid.setEnabled(true);
				ed_varieties.setEnabled(true);
				sp_region.setClickable(false);
				ed_plant.setEnabled(true);
				ed_harvest.setEnabled(true);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateViewState();
	}

	private void updateViewState() {
		// TODO Auto-generated method stub
		if (chooseType != 4) {
			list_base.clear();
			list_base.addAll(nodeEngineModel.loadSourceBeans());
			adapter_base.notifyDataSetChanged();
		} else {
			list_source.clear();
			ArrayList<SensorBean> sensorBeans = nodeEngineModel
					.loadSensorBeans(ed_plant.getText().toString(), ed_harvest
							.getText().toString());
			for (int i = 0; i < sensorBeans.size(); i++) {
				list_source.add(new Sensor(sensorBeans.get(i), true));
			}
			adapter_source.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btServiceStart:
			SharedPreferencesUtil.setSerialState(true);
			updateViewState();
			break;

		case R.id.btServiceStop:
			SharedPreferencesUtil.setSerialState(false);
			updateViewState();
			break;

		case R.id.ed_plant:
			setDate(ed_plant);
			break;
		case R.id.ed_harvest:
			setDate(ed_harvest);
			break;
		case R.id.checkBoxAll:
			if (!checkboxall.isChecked()) {
				checkboxall.setChecked(false);
				for (int i = 0; i < checkBoxs.size(); i++) {
					checkmap.remove(i);
				}
				initCheckBox(false);
			} else {
				checkboxall.setChecked(true);
				for (int i = 0; i < checkBoxs.size(); i++) {
					checkmap.put(i, list_base.get(i).getsRfid());
				}
				initCheckBox(true);
			}
			adapter_base.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	// 确认按钮点击事件
	public void submit(View view) {
		switch (chooseType) {
		case 1:
			nodeEngineModel.saveSourceBean(getSourceBean());
			updateViewState();
			checkBoxs.add(false);
			break;
		case 2:
			nodeEngineModel.updateSourceBean(getSourceBean());
			updateViewState();
			break;
		case 3:
			nodeEngineModel.deleteSourceBeans(checkmap);
			updateViewState();
			initCheckBox(false);
			break;
		case 4:
			if (!"".equals(ed_rfid.getText().toString())) {
				SourceBean sourceBean = nodeEngineModel.load(ed_rfid.getText().toString());
				if(sourceBean!=null){
				setSourceBean(sourceBean);
				updateViewState();
				}else{
					cleanEdit();
					Toast.makeText(this, "无此卡号", Toast.LENGTH_SHORT).show();
				}
			}else{
				cleanEdit();
				Toast.makeText(this, "请输入卡号", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	private static void cleanEdit(){
		ed_rfid.setText("");
		ed_plant.setText("");
		ed_harvest.setText("");
		ed_varieties.setText("");
		sp_region.setSelection(0);
	}

	private SourceBean getSourceBean() {
		SourceBean sourceBean = new SourceBean();
		sourceBean.setsRfid(ed_rfid.getText().toString());
		sourceBean.setsVarieties(ed_varieties.getText().toString());
		sourceBean.setiRegion(sp_region.getSelectedItemPosition());
		sourceBean.setsPalant(ed_plant.getText().toString());
		sourceBean.setsHarvest(ed_harvest.getText().toString());
		return sourceBean;
	}

	private static void setSourceBean(SourceBean s) {
		ed_rfid.setText(s.getsRfid());
		ed_varieties.setText(s.getsVarieties());
		ed_plant.setText(s.getsPalant());
		ed_harvest.setText(s.getsHarvest());
		sp_region.setSelection(s.getiRegion());
	}

	private void setDate(final EditText e) {
		Calendar c = Calendar.getInstance();
		Dialog dialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker dp, int year, int month,
							int dayOfMonth) {
						e.setText(year + "-" + String.format("%02d", month + 1)
								+ "-" + String.format("%02d", dayOfMonth));
					}
				}, c.get(Calendar.YEAR), // 传入年份
				c.get(Calendar.MONTH), // 传入月份
				c.get(Calendar.DAY_OF_MONTH) // 传入天数
		);
		dialog.show();
	}

	public static void setReaderId(String id) {
		ed_rfid.setText(id);
		if (!"".equals(ed_rfid.getText().toString())) {
			SourceBean sourceBean = nodeEngineModel.load(ed_rfid.getText().toString());
			if(sourceBean!=null){
			setSourceBean(sourceBean);
			}else{
				cleanEdit();
				Toast.makeText(mContext, "无此卡号", Toast.LENGTH_SHORT).show();
			}
		}else{
			cleanEdit();
			Toast.makeText(mContext, "请输入卡号", Toast.LENGTH_SHORT).show();
		}
	}
}
