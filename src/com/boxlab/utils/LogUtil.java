package com.boxlab.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class LogUtil
{
	private static final String TAG = "LogUtil";
	private static final String DEF_LOG_DIR = "boxlab/log/";
	private String mPath;
	private Writer mWriter;

	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("[HH:mm:ss] ");

	/**
	 * LOG���ߣ�Ĭ�ϴ�����SD���ġ�boxlab/log/��Ŀ¼��
	 * @param logSession �ռ��ļ�������.			eg: "myLog"
	 * @throws IOException
	 */
	public LogUtil(String logSession)
	  throws IOException
	{
		File sdcard = Environment.getExternalStorageDirectory();
        File logDir = new File(sdcard, DEF_LOG_DIR);
        if (!logDir.exists()) {
        	logDir.mkdirs();
        	// do not allow media scan
            new File(logDir, ".nomedia").createNewFile();
        }        
		
		open(logDir.getAbsolutePath() + "/" + logSession);
	}

	/**
	 * LOG���ߣ�ָ��������SD����logDirPathĿ¼��
	 * @param logDirPath �ռ���SD���ϵĴ洢·��.	eg: "boxlab/log/"
	 * @param logSession �ռ��ļ�������.			eg: "myLog"
	 * @throws IOException
	 */
	public LogUtil(String logDirPath, String logSession)
	  throws IOException
	{
		File sdcard = Environment.getExternalStorageDirectory();
        File logDir = new File(sdcard, logDirPath);
        if (!logDir.exists()) {
        	logDir.mkdirs();
        	// do not allow media scan
            new File(logDir, ".nomedia").createNewFile();
        }        
		
		open(logDir.getAbsolutePath() + "/" + logSession);
	}

	protected void open(String logPath)
	  throws IOException
	{
		File f = new File(logPath + "-" + getTodayString());
		mPath = f.getAbsolutePath();
		mWriter = new BufferedWriter(new FileWriter(mPath), 2048);

		println("Opened log.");
	}

	private static String getTodayString()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-hhmmss");
		return df.format(new Date());
	}

	public String getPath()
	{
		return mPath;
	}

	public void println(String message)
	  throws IOException
	{
		mWriter.write(TIMESTAMP_FMT.format(new Date()));
		mWriter.write(message);
		mWriter.write('\n');
		mWriter.flush();
	}

	public void close()
	  throws IOException
	{
		mWriter.close();
	}
}
