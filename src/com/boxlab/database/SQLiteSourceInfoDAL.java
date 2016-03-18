package com.boxlab.database;

import java.util.ArrayList;
import java.util.Map;

import com.boxlab.bean.SourceBean;
import com.boxlab.bean.ZigBeeBean;
import com.boxlab.utils.StringUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Next
 * @version 1.0 E-mail: lixb@boxlab.cn 创建时间：2016-3-14 下午2:43:48 类说明
 */

public class SQLiteSourceInfoDAL {

	private static final String TAG = "SQLiteSourceInfoDAL";

	private static final String TABLE_NAME = SQLiteDataBaseConfig.TABLE_NAME_SOURCE_INFO;

	private static SQLiteSourceInfoDAL INSTANCE;

	private SQLiteHelper mSQLiteHelper;

	private SQLiteSourceInfoDAL(Context pContext) {
		mSQLiteHelper = SQLiteHelper.getInstance(pContext);
	}

	/**
	 * 单例模式，得到SQLiteSourceInfoDAL对象
	 * 
	 * @param pContext
	 * @return
	 */
	public static SQLiteSourceInfoDAL getInstance(Context pContext) {
		if (INSTANCE == null) {
			Log.w(TAG, "getInstance() new SQLiteSourceInfoDAL(pContext)");
			INSTANCE = new SQLiteSourceInfoDAL(pContext);
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
	 * 保存单个不完整的SourceBean实例 包括 RFID_ID, VARIETIES, REGION
	 */
	public void saveIncompleteSourceInfoBean(SourceBean s) {
		Log.w(TAG, "saveIncompleteSourceInfoBean() n = " + s);

		this.mSQLiteHelper.getWritableDatabase().execSQL(
				"insert into " + TABLE_NAME + " (RFID_ID, VARIETIES, REGION) "
						+ " values(?, ?, ?) ",
				new Object[] { s.sRfid, s.sVarieties, s.iRegion });
	}

	/**
	 * 保存一个完整的SourceBean实例 包括 RFID_ID, VARIETIES, REGION, PLANTING_DATE
	 * 
	 */
	public void saveCompleteSourceInfoBean(SourceBean s) {
		Log.w(TAG, "saveCompleteSourceInfoBean() n = " + s);
		checkCieee(s.sRfid);
		this.mSQLiteHelper.getWritableDatabase().execSQL(
				"insert into " + TABLE_NAME
						+ " (RFID_ID, VARIETIES, REGION, PLANTING_DATE) "
						+ " values(?, ?, ?, ?) ",
				new Object[] { s.sRfid, s.sVarieties, s.iRegion, s.sPalant });
	}

	/**
	 * 更新SourceBean实例 包括 VARIETIES, REGION, PLANTING_DATE, HARVEST_DATE
	 * 
	 * @throws Throwable
	 */
	public void update(SourceBean s) {
		Log.w(TAG, "update(SourceInfoBean n)" + s);

		SQLiteDatabase db = this.mSQLiteHelper.getWritableDatabase();

		// 检查该节点(sRfid)是否存在
		Cursor cursor = db
				.rawQuery("select * from " + TABLE_NAME + " where RFID_ID=? ",
						new String[] { String.valueOf(s.sRfid) });

		if (cursor.moveToFirst()) {
			db.execSQL(
					"update "
							+ TABLE_NAME
							+ " set  VARIETIES=?, REGION=?, PLANTING_DATE=?, HARVEST_DATE=? where RFID_ID=? ",
					new Object[] { s.sVarieties, s.iRegion, s.sPalant,
							s.sHarvest, s.sRfid });
		} else {
			Log.w(TAG, "update(SourceInfo n)" + s);
		}
	}

	/**
	 * 检查该sRfid是否存在，如果存在，直接删除
	 * 
	 * @param sRfid
	 */
	private void checkCieee(String sRfid) {

		SQLiteDatabase db = this.mSQLiteHelper.getWritableDatabase();

		// 检查该节点是否存在
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
				+ " where RFID_ID=? ", new String[] { String.valueOf(sRfid) });
		if (cursor.moveToFirst()) {
			// C_IEEE字段存在,删除该C_IEEE所在列
			// (同时触发器会删除该节点在表SourceInfo里的所有的数据)
			Log.e(TAG, "RFID_ID字段存在,删除该RFID_ID所在列");
			Log.e(TAG, "old node RFID_ID = " + sRfid);
			db.execSQL("delete from " + TABLE_NAME + " where RFID_ID=? ",
					new Object[] { sRfid });
		}
	}

	public ArrayList<SourceBean> load() {
		ArrayList<SourceBean> arr = new ArrayList<SourceBean>();

		Cursor cursor = this.mSQLiteHelper.getWritableDatabase().rawQuery(
				"select * from " + TABLE_NAME + " ", null);

		while (cursor.moveToNext()) {
			SourceBean s = new SourceBean();

			int indexID = cursor.getColumnIndex("ID");
			int indexRFID_ID = cursor.getColumnIndex("RFID_ID");
			int indexVARIETIES = cursor.getColumnIndex("VARIETIES");
			int indexREGION = cursor.getColumnIndex("REGION");
			int indexPLANTING_DATE = cursor.getColumnIndex("PLANTING_DATE");
			int indexHARVEST_DATE = cursor.getColumnIndex("HARVEST_DATE");

			s.id = cursor.getInt(indexID);
			s.sRfid = cursor.getString(indexRFID_ID);
			s.sVarieties = cursor.getString(indexVARIETIES);
			s.iRegion = cursor.getInt(indexREGION);
			s.sPalant = cursor.getString(indexPLANTING_DATE);
			s.sHarvest = cursor.getString(indexHARVEST_DATE);
			arr.add(s);
		}
		cursor.close();
		return arr;
	}

	public SourceBean load(String rfid) {

		Cursor cursor = this.mSQLiteHelper.getWritableDatabase().rawQuery(
				"select * from " + TABLE_NAME + " where RFID_ID=?",
				new String[] { rfid });
		SourceBean s = null;
		if (cursor.moveToNext()) {
			s = new SourceBean();
			int indexID = cursor.getColumnIndex("ID");
			int indexRFID_ID = cursor.getColumnIndex("RFID_ID");
			int indexVARIETIES = cursor.getColumnIndex("VARIETIES");
			int indexREGION = cursor.getColumnIndex("REGION");
			int indexPLANTING_DATE = cursor.getColumnIndex("PLANTING_DATE");
			int indexHARVEST_DATE = cursor.getColumnIndex("HARVEST_DATE");

			s.id = cursor.getInt(indexID);
			s.sRfid = cursor.getString(indexRFID_ID);
			s.sVarieties = cursor.getString(indexVARIETIES);
			s.iRegion = cursor.getInt(indexREGION);
			s.sPalant = cursor.getString(indexPLANTING_DATE);
			s.sHarvest = cursor.getString(indexHARVEST_DATE);
		}
		cursor.close();
		return s;
	}

	public int getCount() {
		Cursor cursor = this.mSQLiteHelper.getWritableDatabase().rawQuery(
				"select count(*) from " + TABLE_NAME, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public void delete(Map<Integer, String> sourceBeans) {
		for (Integer key : sourceBeans.keySet()) {
			delete(sourceBeans.get(key));
		}
	}

	public void delete(String sRfid) {

		SQLiteDatabase db = this.mSQLiteHelper.getWritableDatabase();

		db.execSQL("delete from " + TABLE_NAME + " where RFID_ID=? ",
				new Object[] { sRfid });

	}
}
