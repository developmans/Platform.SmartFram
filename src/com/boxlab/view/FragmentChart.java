package com.boxlab.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.boxlab.bean.Sensor;
import com.boxlab.bean.SensorBean;
import com.boxlab.bean.ZigBee;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.platform.ServiceProxy;
import com.boxlab.platform.R;
import com.boxlab.utils.FrameUtil;
import com.boxlab.utils.StringUtil;
import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-11-4 下午5:45:25 
 * 类说明 
 */

public class FragmentChart extends FragmentBase {

    private static final String TAG = "FragmentChart";

    private final String[] dummyLabels = {"", "10-15", "", "15-20", "", "20-25", "", "25-30", "", "30-35", ""};
    private final float[][] dummyValues = {
    		{23.5f, 24.7f, 24.3f, 28f, 26.5f, 10f,  17f, 18.3f, 17.0f, 17.3f, 15f  },
    		{100f, 62.5f, 62.5f, 64f, 63.5f, 65.5f, 65f, 65.3f, 54.8f, 55.3f, 53f },
            };
    
    private static final int DEF_DATA_SET_LENGTH = 11;
    
    private int mDataSetLength = DEF_DATA_SET_LENGTH;

	private List<Sensor> listSensorTobeDraw;
	private ArrayList<String> listLabels;
	private ArrayList<Float> listValues1;
	private ArrayList<Float> listValues2;
	private boolean listValues1Effectd = false;
	private boolean listValues2Effectd = false;

    private float[] floatArrValues1 = {23.5f, 24.7f, 24.3f, 28f, 26.5f, 10f,  17f, 18.3f, 17.0f, 17.3f, 15f  };
    private float[] floatArrValues2 = {100f, 62.5f, 62.5f, 64f, 63.5f, 65.5f, 65f, 65.3f, 54.8f, 55.3f, 53f };
    
    private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
    private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();

	private boolean itemSelectedChange = false;
    private int iItemSelected = -1;
	private Sensor mSensor;
    
	private LineChartView mChart;
	private Tooltip mTooltip;
	private LinearLayout mChartLegend;
	
	private Spinner spNodeSelect;
	private TextView tvStartTime;
	private Button btChangeTime;
	
	private ArrayList<String> listNodeCna;
	private ArrayAdapter<String> adapterNodeSelect;

	private Animation anim;

	
	public FragmentChart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
		
		spNodeSelect = (Spinner) rootView.findViewById(R.id.spNodeSelect);
		tvStartTime = (TextView) rootView.findViewById(R.id.tvStartTime);
		btChangeTime = (Button) rootView.findViewById(R.id.btChangeTime);
		
		initSpinner();
		
        mChart = (LineChartView) rootView.findViewById(R.id.linechart);
        
        initChartView();
        
        //updataChartView();

		return rootView;
	}

	private void initSpinner() {
		if(listSensor == null) 
			return;
		
		listNodeCna = new ArrayList<String>();
		for(Sensor sensor : listSensor){
			String sCna = StringUtil.getHexStringFormatShort(sensor.sBean.iCna);
			listNodeCna.add(sCna);
		}
		adapterNodeSelect = new ArrayAdapter<String>(getActivity(), R.layout.select_spinner_item, listNodeCna);
		adapterNodeSelect.setDropDownViewResource(R.layout.list_spinner_item);
		spNodeSelect.setAdapter(adapterNodeSelect);
		spNodeSelect.setOnItemSelectedListener(new OnItemSelectedListener() {


			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(listSensor == null)
					return;
				
				if(position != iItemSelected && position < listSensor.size()){
					
					itemSelectedChange = true;
					iItemSelected = position;
					mSensor = listSensor.get(iItemSelected);
					
					loadInitSensorsData(mSensor,DEF_DATA_SET_LENGTH);
					
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}

	
	@Override
	public void onInitZigBeeBeans(ArrayList<ZigBeeBean> zigbeeBeans) {
		// TODO Auto-generated method stub
		super.onInitZigBeeBeans(zigbeeBeans);
	}

	@Override
	public void onInitSensorBeans(ArrayList<SensorBean> sensorBeans) {
		// TODO Auto-generated method stub
		super.onInitSensorBeans(sensorBeans);
	}

	@Override
	public void onInitZigBees(ArrayList<ZigBee> zigbees) {
		// TODO Auto-generated method stub
		super.onInitZigBees(zigbees);
	}

	@Override
	public void onInitSensors(ArrayList<Sensor> sensors) {
		// TODO Auto-generated method stub
		super.onInitSensors(sensors);
		
		initSpinner();
		
	}

	@Override
	public boolean filterInterested(Sensor sensor) {
		// TODO Auto-generated method stub
		if(listSensor == null || mSensor == null)
			return false;
		
		return (mSensor.sBean.iCna == sensor.sBean.iCna );
	}

	@Override
	public void notifyReciveSensor(int index, Sensor sensor) {
		// TODO Auto-generated method stub
		listSensorTobeDraw.remove(0);
		listSensorTobeDraw.add(sensor);
//		listSensorTobeDraw = listSensorTobeDraw.subList(1, DEF_DATA_SET_LENGTH);
		listLabels.remove(0);
		listLabels.add(sensor.sBean.sTimeStamp);
//		listLabels = (ArrayList<String>) listLabels.subList(1, DEF_DATA_SET_LENGTH);
		
		mHandler.sendMessage(mHandler.obtainMessage(1));
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:

				processList();
				updataChartView();
				
				break;

			default:
				break;
			}
		}
		
	};
	
	/**
	 * 初始化图表类
	 * @param none
	 * 
	 */
	private void initChartView() {

		// Dummy Data
		LineSet dataset = new LineSet(dummyLabels, dummyValues[0]);
		dataset.setColor(Color.parseColor("#97b867"))
				.setThickness(Tools.fromDpToPx(3))
				.setDashed(new float[] { 10, 10 })
				.setDotsStrokeThickness(Tools.fromDpToPx(2))
				.setDotsStrokeColor(Color.parseColor("#FF365EAF"))
				.setDotsColor(Color.parseColor("#eef1f6")).setSmooth(true);
		mChart.addData(dataset);

		dataset = new LineSet(dummyLabels, dummyValues[1]);
		dataset.setColor(Color.parseColor("#FF365EAF"))
				.setDotsStrokeThickness(Tools.fromDpToPx(2))
				.setDotsStrokeColor(Color.parseColor("#FF365EAF"))
				.setDotsColor(Color.parseColor("#eef1f6"))
                .setSmooth(true);
        mChart.addData(dataset);
        
        //
        mChartLegend = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.chart_legend, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        		RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.rightMargin = (int) Tools.fromDpToPx(3);
        layoutParams.topMargin = (int) Tools.fromDpToPx(3);
        
        mChart.addView(mChartLegend, layoutParams);
        
        //
        mTooltip = new Tooltip(getActivity(), R.layout.tooltip_linechart, R.id.value);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            mTooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

            mTooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X,0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y,0f));
        }

        mChart.setTooltips(mTooltip);
        
        mChart.setOnClickListener(mOnClickListener);
        mChart.setOnEntryClickListener(mOnEntryClickListener);
        
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#7F97B867"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        
		mChart.setBorderSpacing(1)
		        .setAxisBorderValues(0, 100, 20)
				.setXLabels(AxisController.LabelPosition.OUTSIDE)
				.setYLabels(AxisController.LabelPosition.OUTSIDE)
				.setLabelsColor(Color.parseColor("#FF8E9196"))
				.setXAxis(false)
				.setYAxis(false)
				.setBorderSpacing(Tools.fromDpToPx(5))
				.setGrid(ChartView.GridType.HORIZONTAL, gridPaint);
//        setBorderSpacing(Tools.fromDpToPx(10))
//                .setXLabels(AxisController.LabelPosition.INSIDE)
//                .setLabelsColor(Color.parseColor("#e08b36"))
//                .setXAxis(true)
//                .setYAxis(false)
//                .setGrid(ChartView.GridType.HORIZONTAL, gridPaint);;

        anim = new Animation().setStartPoint(-1, 1);

        mChart.show(anim);
        
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
            //mChart.dismissAllTooltips();  
		}

	};
	
	private OnEntryClickListener mOnEntryClickListener = new OnEntryClickListener() {
		
		@Override
		public void onClick(int setIndex, int entryIndex, Rect rect) {
			float[] values;
			
			if(!listValues1Effectd && !listValues2Effectd)
				values = dummyValues[setIndex];
			else if(setIndex == 0 && listValues1Effectd){
				values = floatArrValues1;
			}else if(setIndex == 1 && listValues2Effectd){
				values = floatArrValues2;
			}else{
				values = dummyValues[setIndex];
			}
				
			mTooltip.prepare(rect, values[entryIndex]);

			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
				mTooltip.setPivotX(0);
				mTooltip.setAlpha(0);
				mTooltip.setScaleX(0);
				mTooltip.animate().setDuration(200).alpha(1).scaleX(1)
						.setInterpolator(enterInterpolator);
			}
		}
	};

	/**
	 * 更新图表类
	 * @param none
	 * 
	 */
	private void updataChartView() {

		mChart.dismissAllTooltips();
		if(listValues1Effectd){
//			float fmax = Collections.max(listValues1);
//			float fmin = Collections.min(listValues1);
//
//			int imax = ((int) fmax);
//			int imin = ((int) fmin);
//			
//			int step = (imax - imin) / 5;
//
//			imax = ((int) fmax) / step * step + step;
//			imin = ((int) fmin) / step * step;
//
//			imin = (step > imin)?step:imin;
//			
//			Log.w(TAG, "imax = " + imax + "; imix = " + imin + "; step = " + step);
//			
//			mChart.setAxisBorderValues(imin, imax, step);
			mChart.updateValues(0, floatArrValues1);
			
		}

		if(listValues2Effectd){
//			float fmax = Collections.max(listValues2);
//			float fmin = Collections.min(listValues2);
//
//			int imax = ((int) fmax);
//			int imin = ((int) fmin);
//			
//			int step = (imax - imin) / 5;
//
//			imax = ((int) fmax) / step * step+ step;
//			imin = ((int) fmin) / step * step;
//			
//			imin = (step > imin)?step:imin;
//			
//			Log.w(TAG, "imax = " + imax + "; imin = " + imin + "; step = " + step);
//			
//			mChart.setAxisBorderValues(imin, imax, step);
			
			mChart.updateValues(1, floatArrValues2);
		}
		
		//mChart.setWillNotDraw(false);
		
		mChart.notifyDataUpdate();
		
	}
	
	@Override
	public void proxyServiceConnect(ServiceProxy service) {
		// TODO Auto-generated method stub
		super.proxyServiceConnect(service);
		loadInitSensorsData(mSensor, DEF_DATA_SET_LENGTH);
		
	}

	private void loadInitSensorsData(Sensor s, int n) {
		
		Log.w(TAG, "loadInitSensorsData()");
		
		if(mServiceInFragment == null || s == null || n < 0)
			return;
		
		mDataSetLength = n;
		
		listSensorTobeDraw = mServiceInFragment.loadSensors(s, mDataSetLength);
		
		listLabels = new ArrayList<String>(mDataSetLength);
		for (Sensor sensor : listSensorTobeDraw) {
			listLabels.add(sensor.sBean.sTimeStamp);
		}
		floatArrValues1 = new float[mDataSetLength];
		floatArrValues2 = new float[mDataSetLength];
		
		listValues1 = new ArrayList<Float>(mDataSetLength);
		listValues2 = new ArrayList<Float>(mDataSetLength);
		
		processList();
		updataChartView();
	}

	private void processList() {
		Sensor sensor;
		
		for(int i = 0; i <  mDataSetLength; i++){
			sensor = listSensorTobeDraw.get(i);
			
			float f1 = 0.0f;
			float f2 = 0.0f;
			switch (sensor.sBean.iSensorType) {

			case FrameUtil.SENSOR_TYPE_ID_LM35DZ:
				break;

			case FrameUtil.SENSOR_TYPE_ID_LS_RESISTANCE: // 光敏电阻传感器
				break;

			case FrameUtil.SENSOR_TYPE_ID_LS_DIODE: // 光敏二极管传感器
				break;

			case (byte) 0x04: // MQ-3酒精传感器
				break;

			case (byte) 0x05: // MQ-135空气质量传感器(氨气/硫化物/苯系蒸汽)
				break;

			case (byte) 0x06: // MQ-2传感器(检测液化气/丙烷/氢气)
				break;

			case (byte) 0x07: // HC-SR501人体红外传感器
				break;

			case (byte) 0x08: // 直流马达模块
				break;

			case (byte) 0x09: // 温湿度传感器(低精度)
				break;

			case (byte) 0x0a: // ADXL345三轴数字加速度传感器
				break;

			case (byte) 0x0b: // SHT10温湿度传感器(高精度)
				f1 = sensor.Temperature;
			    floatArrValues1[i] = f1;
			    listValues1.add(f1);
				listValues1Effectd = true;
				f2 = sensor.Humidity;
			    floatArrValues2[i] = f2;
			    listValues2.add(f2);
				listValues2Effectd = true;
				break;

			case (byte) 0x0c: // L3G4200D 三轴数字陀螺仪传感器
				break;

			case (byte) 0x0d:// 节点IO状态
				break;

			case (byte) 0x0e:// 智能电表
				break;

			case (byte) 0x0f:// 远程遥控设备
				break;

			case (byte) 0x11: // 振动传感器
				break;

			case (byte) 0x13: // BH1750FVI光照传感器
				f1 = sensor.Light;
		        floatArrValues1[i] = f1;
			    listValues1.add(f1);
		        listValues1Effectd = true;
				listValues2Effectd = false;
				break;

			case (byte) 0x14: // 声音传感器
				break;

			case (byte) 0x15: // 红外测距传感器
				break;

			case (byte) 0x17: // 继电器设备
				break;

			case (byte) 0x18:// 气压传感器
				break;

			case FrameUtil.SENSOR_TYPE_ID_IO_LED:
				break;

			case FrameUtil.SENSOR_TYPE_ID_INNER_TS:
				break;

			case FrameUtil.SENSOR_TYPE_ID_LIGHT:
				break;

			case FrameUtil.SENSOR_TYPE_ID_FAN:
				break;

			case FrameUtil.SENSOR_TYPE_ID_DOOR_LOCK:
				break;

			case FrameUtil.SENSOR_TYPE_ID_WATER_PUMP:
				break;

			case FrameUtil.SENSOR_TYPE_ID_HEATER:
				break;

			case FrameUtil.SENSOR_TYPE_ID_AV_ANNUNCIATOR:
				break;

			case FrameUtil.SENSOR_TYPE_ID_SOCKET:
				break;

			case FrameUtil.SENSOR_TYPE_ID_RFID_LF:
				break;

			case FrameUtil.SENSOR_TYPE_ID_CO2:
				f1 = sensor.Concentration;
			    floatArrValues1[i] = f1;
			    listValues1.add(f1);
				listValues1Effectd = true;
				listValues2Effectd = false;
				break;

			case FrameUtil.SENSOR_TYPE_ID_SOIL:
				f1 = sensor.Temperature;
			    floatArrValues1[i] = f1;
			    listValues1.add(f1);
				listValues1Effectd = true;
				f2 = sensor.Humidity;
			    floatArrValues2[i] = f2;
			    listValues2.add(f2);
				listValues2Effectd = true;

			case FrameUtil.SENSOR_TYPE_ID_DOOR:
				break;

			case FrameUtil.SENSOR_TYPE_ID_IR_DETECTOR:
				break;

			case FrameUtil.SENSOR_TYPE_ID_GAS_DETECTOR:
				break;

			case FrameUtil.SENSOR_TYPE_ID_IR_FENCE:
				break;

			case FrameUtil.SENSOR_TYPE_ID_SMOKE_ALARM:
				break;

			case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS232:
				f1 = sensor.Temperature;
			    floatArrValues1[i] = f1;
			    listValues1.add(f1);
				listValues1Effectd = true;
				f2 = sensor.Humidity;
			    floatArrValues2[i] = f2;
			    listValues2.add(f2);
				listValues2Effectd = true;
				break;
				
			case FrameUtil.SENSOR_TYPE_ID_TH_TRANSMITTER_RS485:
				f1 = sensor.Temperature;
			    floatArrValues1[i] = f1;
			    listValues1.add(f1);
				listValues1Effectd = true;
				f2 = sensor.Humidity;
			    floatArrValues2[i] = f2;
			    listValues2.add(f2);
				listValues2Effectd = true;
				break;

			case FrameUtil.SENSOR_TYPE_ID_WIN_CURTAIN:
				break;

			case (byte) 0xfe: // 无效传感器
				break;

			default:
				break;

			}
			
		}
	}
}
