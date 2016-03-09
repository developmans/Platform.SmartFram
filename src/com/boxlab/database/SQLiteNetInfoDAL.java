package com.boxlab.database;

import java.util.ArrayList;

import com.boxlab.bean.ZigBeeBean;
import com.boxlab.utils.StringUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-15 下午7:28:44 
 * 类说明 
 */

public class SQLiteNetInfoDAL {

	private static final String TAG = "SQLiteNetInfoDAL";
	
	private static final String TABLE_NAME = SQLiteDataBaseConfig.TABLE_NAME_NET_INFO;

	private static SQLiteNetInfoDAL INSTANCE;
	
	private SQLiteHelper mSQLiteHelper;

	private SQLiteNetInfoDAL(Context pContext) {
		mSQLiteHelper = SQLiteHelper.getInstance(pContext);
	}
	
	/**
	 * 单例模式，得到SQLiteNetInfoDAL对象
	 * @param pContext
	 * @return
	 */
	public static SQLiteNetInfoDAL getInstance(Context pContext) {
		if (INSTANCE == null) {
			Log.w(TAG, "getInstance() new SQLiteNetInfoDAL(pContext)");
			INSTANCE = new SQLiteNetInfoDAL(pContext);
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
	 * 保存单个不完整的NetInfoBean实例
	 *     包括 C_NA, P_NA, C_PANID, C_VER, PROFILE
	 */
	public void saveIncompleteNetInfoBean(ZigBeeBean n){
		Log.w(TAG, "saveIncompleteNetInfoBean() n = " + n);
		
		this.mSQLiteHelper.getWritableDatabase().
		            execSQL("insert into " + TABLE_NAME + 
		            		" (C_NA, P_NA, C_PANID, C_VER, PROFILE) " + 
		            	    " values(?, ?, ?, ?, ?) ", 
		            	    new Object[]{n.iCna, n.iPna, n.iPanId, n.iCver, n.iProfile});
	}
	
	/**
	 * 保存一个完整的NetInfoBean实例
	 *     包括 C_NA, C_IEEE, P_NA, P_IEEE, C_PANID, C_VER, PROFILE
	 */
	public void saveCompleteNetInfoBean(ZigBeeBean n){
		Log.w(TAG, "saveCompleteNetInfoBean() n = " + n);
		checkCieee(n.lCieee);
		this.mSQLiteHelper.getWritableDatabase().
		            execSQL("insert into " + TABLE_NAME + 
		            		" (C_NA, C_IEEE, P_NA, P_IEEE, C_PANID, C_VER, PROFILE) " + 
		            	    " values(?, ?, ?, ?, ?, ?, ?) ", 
		            	    new Object[]{n.iCna, n.lCieee, n.iPna, n.lPieee, n.iPanId, n.iCver, n.iProfile});
	}
	/**
	 * 保存所有NetInfoBean实例到数据库
	 *     
	 */
	public void save(ArrayList<ZigBeeBean> arrs){
		
		int size = arrs.size();
		
		for(int i = 0; i < size; i++){
			update(arrs.get(i));
		}
	}
	
	/**
	 * 更新NetInfoBean实例
	 *     包括 C_NA, C_IEEE, P_NA, P_IEEE, C_PANID, C_VER, PROFILE
	 * @throws Throwable 
	 */
	public void update(ZigBeeBean n){
		Log.w(TAG, "update(NetInfoBean n)" + n);
		
		SQLiteDatabase db =  this.mSQLiteHelper.getWritableDatabase();
		
		// 检查该节点(iCna)是否存在
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where C_NA=? ", new String[]{String.valueOf(n.iCna)});

		if(cursor.moveToFirst()){
			
			// 检查C_IEEE字段是否冲突
			int index = cursor.getColumnIndex("C_IEEE");
			
				if(cursor.isNull(index)){
					if(n.lCieee != 0){
						// 更新前检测该C_IEEE是否与表中其他条目冲突
						checkCieee(n.lCieee);
						// C_IEEE字段为NULL,说明尚未更新过,直接更新覆盖
						db.execSQL("update " + TABLE_NAME + 
								" set C_IEEE=?, P_NA=?, P_IEEE=? where C_NA=? ",   
							     new Object[]{n.lCieee, n.iPna, n.lPieee, n.iCna}); 
					}else{
						Log.e(TAG, "C_IEEE字段为NULL且n.lCieee为0");
					}
				}
				else
				{
					if(n.lCieee != 0){
						long c_ieee = cursor.getLong(index);
						if(c_ieee == n.lCieee){
							// C_IEEE字段与C_NA符合,直接更新覆盖
							Log.e(TAG, "C_IEEE字段与C_NA符合,直接更新覆盖");
							db.execSQL("update " + TABLE_NAME + 
									" set C_IEEE=?, P_NA=?, P_IEEE=? where C_NA=? ",   
								     new Object[]{n.lCieee, n.iPna, n.lPieee, n.iCna}); 
						}else{
							// C_IEEE字段与C_NA冲突,删除该C_IEEE所在列
							//(同时触发器会删除该节点在表SensorInfo里的所有的数据) 
							Log.e(TAG, "C_IEEE字段与C_NA冲突,删除该C_IEEE所在列");
							Log.e(TAG, "old node c_ieee = " + StringUtil.getHexStringFormatLong(c_ieee));
							db.execSQL("delete from " + TABLE_NAME + " where C_IEEE=? ", new Object[]{n.lCieee});
							
							// 重新添加该节点
							Log.e(TAG, "重新添加该节点");
							Log.e(TAG, "new node = " + n);
							db.execSQL("insert into " + TABLE_NAME + 
				            		" (C_NA, C_IEEE, P_NA, P_IEEE, C_PANID, C_VER, PROFILE) " + 
				            	    " values(?, ?, ?, ?, ?, ?, ?) ", 
				            	    new Object[]{n.iCna, n.lCieee, n.iPna, n.lPieee, n.iPanId, n.iCver, n.iProfile});
						}
					}else{
						Log.e(TAG, "C_IEEE字段不为NULL但是n.lCieee == 0");
					}
				}
			
		}else{
			
			if(n.lCieee == 0){
				saveIncompleteNetInfoBean(n);
			}else{
				saveCompleteNetInfoBean(n);
			}
		}
	}
	/**
	 * 检查该Ciee是否存在，如果存在，直接删除
	 * @param lCieee
	 */
	private void checkCieee(long lCieee) {

		SQLiteDatabase db =  this.mSQLiteHelper.getWritableDatabase();
		
		// 检查该节点是否存在
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where C_IEEE=? ", new String[]{String.valueOf(lCieee)});
		if(cursor.moveToFirst()){
			// C_IEEE字段存在,删除该C_IEEE所在列
			//(同时触发器会删除该节点在表SensorInfo里的所有的数据)
			Log.e(TAG, "C_IEEE字段存在,删除该C_IEEE所在列");
			Log.e(TAG, "old node c_ieee = " + StringUtil.getHexStringFormatLong(lCieee));
			db.execSQL("delete from " + TABLE_NAME + " where C_IEEE=? ", new Object[]{lCieee});
		}
	}

	public ArrayList<ZigBeeBean> load(){
		ArrayList<ZigBeeBean> arr = new ArrayList<ZigBeeBean>();
		
		Cursor cursor = this.mSQLiteHelper.getWritableDatabase().
				rawQuery("select * from " + TABLE_NAME + " ", null);  
		
		while (cursor.moveToNext()){  
			ZigBeeBean n = new ZigBeeBean();

			int indexID = cursor.getColumnIndex("ID");
			int indexC_NA = cursor.getColumnIndex("C_NA");
			int indexC_IEEE = cursor.getColumnIndex("C_IEEE");
			int indexP_NA = cursor.getColumnIndex("P_NA");
			int indexP_IEEE = cursor.getColumnIndex("P_IEEE");
			int indexC_PANID = cursor.getColumnIndex("C_PANID");
			int indexC_VER = cursor.getColumnIndex("C_VER");
			int indexPROFILE = cursor.getColumnIndex("PROFILE");
			
			n.id = cursor.getInt(indexID);
			n.iCna = cursor.getInt(indexC_NA);
			if(!cursor.isNull(indexC_IEEE))
				n.lCieee = cursor.getLong(indexC_IEEE);
			n.iPna = cursor.getInt(indexP_NA);
			if(!cursor.isNull(indexP_IEEE))
				n.lPieee = cursor.getLong(indexP_IEEE);
			n.iPanId = cursor.getInt(indexC_PANID);
			n.iCver = cursor.getInt(indexC_VER);
			n.iProfile = cursor.getInt(indexPROFILE);
			
			arr.add(n);
		}
		cursor.close();
		return arr;
	}
	
	public int getCount() {
		Cursor cursor = this.mSQLiteHelper.getWritableDatabase()
				.rawQuery("select count(*) from " + TABLE_NAME , null); 
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public void delete(int iCna) {
		
		SQLiteDatabase db =  this.mSQLiteHelper.getWritableDatabase();
		
		db.execSQL("delete from " + TABLE_NAME + " where C_NA=? ", new Object[]{iCna});
		
	}
}
