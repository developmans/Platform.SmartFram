package com.boxlab.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ���ݿ⸨���ࣨ���������ã��������ݿ������ݿ�汾������
 * 
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private final static String TAG = "SQLiteHelper";
	private static SQLiteHelper INSTANCE;
	private static Context mContext;
		
	private SQLiteHelper(Context pContext)
	{
		/**
		 * �������ݿ�
		 */
		super(pContext, SQLiteDataBaseConfig.DATABASE_NAME, null, SQLiteDataBaseConfig.VERSION);
		mContext = pContext;
	}
	
	/**
	 * ����ģʽ���õ�SQLiteHelper����
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
		Log.i(TAG, "onCreate()�������ݿ��");
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
