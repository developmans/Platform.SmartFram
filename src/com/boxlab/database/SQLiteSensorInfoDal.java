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
 * 创建时间：2015-9-15 下午7:29:08 
 * 类说明 
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
	 * 单例模式，得到SQLiteSensorInfoDal对象
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
	 * 开始事务
	 */
	public void beginTransaction() {
		this.mSQLiteHelper.getWritableDatabase().beginTransaction();
	}
	
	/**
	 * 设置事务成功
	 */
	public void setTransactionSuccessful() {
		this.mSQLiteHelper.getWritableDatabase().setTransactionSuccessful();
	}
	
	/**
	 * 设置事务结束
	 */
	public void endTransaction() {
		this.mSQLiteHelper.getWritableDatabase().endTransaction();
	}
	
	/**
	 * 保存一个完整的SensorInfoBean
	 */
	public void save(SensorBean sBean){  
		this.mSQLiteHelper.getWritableDatabase().
             execSQL("insert into " + TABLE_NAME + 
        		     " (C_NA, SENSOR_TYPE, SENSOR_DATA, POWER) values(?,?,?,?)", 
        		     new Object[]{sBean.iCna, sBean.iSensorType, sBean.aSensorData,sBean.iPower});
    }
	
//	/**
//	 * 更新一个完整的SensorInfoBean
//	 */ 
//	public void update(SensorInfoBean sBean){  
//		this.mSQLiteHelper.getWritableDatabase().
//		     execSQL("update " + TABLE_NAME + " set C_NA=? where C_NA=?",   
//				     new Object[]{sBean.iCna, sBean.iCna}); 
//	}
	
	/**
	 * 删除所有C_NA为iCna的SensorInfoBean
	 */
	public void delete(int iCna){  
		this.mSQLiteHelper.getWritableDatabase().
        execSQL("delete from " + TABLE_NAME + " where C_NA=?", new Object[]{iCna});  
	}
	
	/**
	 * 查找所有C_NA为iCna的SensorInfoBean
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
	 * 查找所有C_NA为iCna的SensorInfoBean
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
	 * 根据节点地址iCna加载指定数量的的SensorInfoBean
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
				// 填充空数据
				SensorBean s = new SensorBean();
				s.iCna = iCna;
				s.id = ((id - i) < 0) ? 0 : (id - i);
				arr.add(s);
			}
		}
		// 倒序排列
		Collections.reverse(arr);
		return arr;  
	}
	/**
	 * 加载C_NA为iCna的SensorInfoBean的最新数据
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
	 * 统计C_NA为iCna的SensorInfoBean的数据数量
	 */
	public long getCount(int iCna) {
		Cursor cursor = this.mSQLiteHelper.getReadableDatabase()
				.rawQuery("select count(*) from " + TABLE_NAME + " where C_NA=? " , new String[]{String.valueOf(iCna)}); 
		int count = cursor.getCount();
		cursor.close();
		return count;
	}
	
//	//分页的操作  
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
//	//对数据库表数据的统计操作  
//	public long getCount() {  
//		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();  
//		Cursor cursor = db.rawQuery("select count(*) from person", null); 
//		cursor.moveToFirst();  
//		return cursor.getLong(0);  
//	}

}
