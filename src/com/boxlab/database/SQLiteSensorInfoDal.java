package com.boxlab.database;

import java.util.ArrayList;
import java.util.Collections;

import com.boxlab.bean.SensorBean;
import com.boxlab.utils.StringUtil;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-9-15 ����7:29:08 
 * ��˵�� 
 */

public class SQLiteSensorInfoDal {
	
	private static final String TAG = "SQLiteSensorInfoDal";
	
	private static final String TABLE_NAME = SQLiteDataBaseConfig.TABLE_NAME_SENSOR_INFO;

	private static SQLiteSensorInfoDal INSTANCE;
	
	private SQLiteHelper mSQLiteHelper;

	private SQLiteSensorInfoDal(Context pContext) {
		mSQLiteHelper = SQLiteHelper.getInstance(pContext);
	}
	
	/**
	 * ����ģʽ���õ�SQLiteSensorInfoDal����
	 * @param pContext
	 * @return
	 */
	public static SQLiteSensorInfoDal getInstance(Context pContext) {
		if (INSTANCE == null) {
			Log.w(TAG, "getInstance() new SQLiteSensorInfoDal(pContext)");
			INSTANCE = new SQLiteSensorInfoDal(pContext);
		}
		return INSTANCE;
	}
	
	/**
	 * ��ʼ����
	 */
	public void beginTransaction() {
		this.mSQLiteHelper.getWritableDatabase().beginTransaction();
	}
	
	/**
	 * ��������ɹ�
	 */
	public void setTransactionSuccessful() {
		this.mSQLiteHelper.getWritableDatabase().setTransactionSuccessful();
	}
	
	/**
	 * �����������
	 */
	public void endTransaction() {
		this.mSQLiteHelper.getWritableDatabase().endTransaction();
	}
	
	/**
	 * ����һ��������SensorInfoBean
	 */
	public void save(SensorBean sBean){  
		this.mSQLiteHelper.getWritableDatabase().
             execSQL("insert into " + TABLE_NAME + 
        		     " (C_NA, SENSOR_TYPE, SENSOR_DATA, POWER) values(?,?,?,?)", 
        		     new Object[]{sBean.iCna, sBean.iSensorType, sBean.aSensorData,sBean.iPower});
    }
	
//	/**
//	 * ����һ��������SensorInfoBean
//	 */ 
//	public void update(SensorInfoBean sBean){  
//		this.mSQLiteHelper.getWritableDatabase().
//		     execSQL("update " + TABLE_NAME + " set C_NA=? where C_NA=?",   
//				     new Object[]{sBean.iCna, sBean.iCna}); 
//	}
	
	/**
	 * ɾ������C_NAΪiCna��SensorInfoBean
	 */
	public void delete(int iCna){  
		this.mSQLiteHelper.getWritableDatabase().
        execSQL("delete from " + TABLE_NAME + " where C_NA=?", new Object[]{iCna});  
	}
	
	/**
	 * ��������C_NAΪiCna��SensorInfoBean
	 */
	public ArrayList<SensorBean> load(int iCna){ 
		ArrayList<SensorBean> arr = new ArrayList<SensorBean>();
		Cursor cursor = this.mSQLiteHelper.getReadableDatabase().
				rawQuery("select * from " + TABLE_NAME + " where C_NA=? ", new String[]{String.valueOf(iCna)});  
		
		while (cursor.moveToNext()){  
			SensorBean s = new SensorBean();
			s.id = cursor.getInt(cursor.getColumnIndex("ID"));
			s.iCna = cursor.getInt(cursor.getColumnIndex("C_NA"));
			s.iSensorType = cursor.getInt(cursor.getColumnIndex("SENSOR_TYPE"));
			s.aSensorData = cursor.getBlob(cursor.getColumnIndex("SENSOR_DATA"));
			s.iPower = cursor.getInt(cursor.getColumnIndex("POWER"));
			arr.add(s);
		}
		cursor.close();
		return arr;  
	}
	
	/**
	 * ��������C_NAΪiCna��SensorInfoBean
	 */
	public ArrayList<SensorBean> load(String startTime, String stopTime){ 
		ArrayList<SensorBean> arr = new ArrayList<SensorBean>();
		Cursor cursor=null;
		if(!"".equals(stopTime)){
			cursor= this.mSQLiteHelper.getReadableDatabase().
				rawQuery("select * from " + TABLE_NAME + " where TIMESTAMP Between ? and  ? ",new String[]{startTime,stopTime});  
		}else{
			cursor= this.mSQLiteHelper.getReadableDatabase().
					rawQuery("select * from " + TABLE_NAME + " where TIMESTAMP >= ? ",new String[]{startTime});  
		}
		while (cursor.moveToNext()){  
			SensorBean s = new SensorBean();
			s.id = cursor.getInt(cursor.getColumnIndex("ID"));
			s.iCna = cursor.getInt(cursor.getColumnIndex("C_NA"));
			s.iSensorType = cursor.getInt(cursor.getColumnIndex("SENSOR_TYPE"));
			s.aSensorData = cursor.getBlob(cursor.getColumnIndex("SENSOR_DATA"));
			s.iPower = cursor.getInt(cursor.getColumnIndex("POWER"));
			s.sTimeStamp=cursor.getString(cursor.getColumnIndex("TIMESTAMP"));
			arr.add(s);
		}
		cursor.close();
		return arr;  
	}
	
	/**
	 * ���ݽڵ��ַiCna����ָ�������ĵ�SensorInfoBean
	 */
	public ArrayList<SensorBean> load(int iCna, int n){ 
		ArrayList<SensorBean> arr = new ArrayList<SensorBean>();
		Cursor cursor = this.mSQLiteHelper.getReadableDatabase().
				rawQuery("select * from " + TABLE_NAME + " where C_NA=? order by ID desc limit ? ", 
						new String[]{String.valueOf(iCna), String.valueOf(n)});  
		
		while (cursor.moveToNext()){  
			SensorBean s = new SensorBean();
			s.id = cursor.getInt(cursor.getColumnIndex("ID"));
			s.iCna = cursor.getInt(cursor.getColumnIndex("C_NA"));
			s.iSensorType = cursor.getInt(cursor.getColumnIndex("SENSOR_TYPE"));
			s.aSensorData = cursor.getBlob(cursor.getColumnIndex("SENSOR_DATA"));
			s.iPower = cursor.getInt(cursor.getColumnIndex("POWER"));
			s.sTimeStamp = cursor.getString(cursor.getColumnIndex("TIMESTAMP"));
			arr.add(s);
		}
		cursor.close();
		if(arr.size() < n){
			int id = n;
			if(arr.size() == 0){
				id = n;
			}else{
				id = arr.get(arr.size() - 1).id;
			}
			for(int i = arr.size(); i < n; i++){
				// ��������
				SensorBean s = new SensorBean();
				s.iCna = iCna;
				s.id = ((id - i) < 0) ? 0 : (id - i);
				arr.add(s);
			}
		}
		// ��������
		Collections.reverse(arr);
		return arr;  
	}
	/**
	 * ����C_NAΪiCna��SensorInfoBean����������
	 */
	public SensorBean find(int iCna) {
		SensorBean s = new SensorBean();
		Cursor cursor = this.mSQLiteHelper.getReadableDatabase().
				rawQuery("select * from " + TABLE_NAME + " where C_NA=? order by ID desc limit 1", new String[]{String.valueOf(iCna)});
		
		if(cursor.getCount() == 1){
			cursor.moveToFirst();
			s.id = cursor.getInt(cursor.getColumnIndex("ID"));
			s.iCna = cursor.getInt(cursor.getColumnIndex("C_NA"));
			s.iSensorType = cursor.getInt(cursor.getColumnIndex("SENSOR_TYPE"));
			s.aSensorData = cursor.getBlob(cursor.getColumnIndex("SENSOR_DATA"));
			s.iPower = cursor.getInt(cursor.getColumnIndex("POWER"));
		}else{
			s.iCna = iCna;
			Log.e(TAG, "load err. iCna = " + StringUtil.getHexStringFormatShort(iCna) + 
					"return cursor.getCount() = " + cursor.getCount() );
		}
		cursor.close();
		return s;
	}
	
	/**
	 * ͳ��C_NAΪiCna��SensorInfoBean����������
	 */
	public long getCount(int iCna) {
		Cursor cursor = this.mSQLiteHelper.getReadableDatabase()
				.rawQuery("select count(*) from " + TABLE_NAME + " where C_NA=? " , new String[]{String.valueOf(iCna)}); 
		int count = cursor.getCount();
		cursor.close();
		return count;
	}
	
//	//��ҳ�Ĳ���  
//	public List<Person> getScrollData(Integer offset, Integer maxResult){  
//		List<Person> persons = new ArrayList<Person>();  
//		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();  
//		Cursor cursor = db.rawQuery("select * from person limit ?,?", new String[]{offset.toString(), maxResult.toString()});  
//		while(cursor.moveToNext()){  
//			int personid = cursor.getInt(cursor.getColumnIndex("personid"));  
//			String name = cursor.getString(cursor.getColumnIndex("name"));  
//			int amount = cursor.getInt(cursor.getColumnIndex("amount"));  
//			Person person = new Person(personid, name);  
//			person.setAmount(amount);  
//			persons.add(person);  
//		}  
//		cursor.close();  
//		return persons;  
//	}
//	//�����ݿ�����ݵ�ͳ�Ʋ���  
//	public long getCount() {  
//		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();  
//		Cursor cursor = db.rawQuery("select count(*) from person", null); 
//		cursor.moveToFirst();  
//		return cursor.getLong(0);  
//	}

}
