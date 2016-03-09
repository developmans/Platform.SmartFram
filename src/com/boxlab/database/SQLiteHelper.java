package com.boxlab.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库辅助类（辅助类作用：创建数据库表和数据库版本升级）
 * 
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private final static String TAG = "SQLiteHelper";
	private static SQLiteHelper INSTANCE;
	private static Context mContext;
		
	private SQLiteHelper(Context pContext)
	{
		/**
		 * 创建数据库
		 */
		super(pContext, SQLiteDataBaseConfig.DATABASE_NAME, null, SQLiteDataBaseConfig.VERSION);
		mContext = pContext;
	}
	
	/**
	 * 单例模式，得到SQLiteHelper对象
	 * @param pContext
	 * @return
	 */
	public static SQLiteHelper getInstance(Context pContext)
	{
		if (mContext != pContext || INSTANCE == null) {
			Log.w(TAG, "getInstance() new SQLiteHelper(pContext)");
			INSTANCE = new SQLiteHelper(pContext);
		}
		return INSTANCE;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "onCreate()创建数据库表");
		db.execSQL(SQLiteDataBaseConfig.TABLE_DDL_NET_INFO);
		db.execSQL(SQLiteDataBaseConfig.TABLE_DDL_SENSOR_INFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SQLiteDataBaseConfig.TABLE_NAME_NET_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + SQLiteDataBaseConfig.TABLE_NAME_SENSOR_INFO);
        onCreate(db);
	}
	

}
