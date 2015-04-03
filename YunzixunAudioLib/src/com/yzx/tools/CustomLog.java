package com.yzx.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yzx.preference.UserData;

/**
 * 日志工具类
 * @author xiaozhenhua
 *
 */
public class CustomLog {

	public final static String LOGTAG = "yunzhixun";
	public synchronized static void v(String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			v(LOGTAG, logMe);
		}
		if(UserData.isAllLogToSdcard()){
			FileTools.saveSdkLog(logMe, "YZX_SDK_");
		}
	}

	public static void d(String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			d(LOGTAG, logMe);
		}
		if(UserData.isAllLogToSdcard()){
			FileTools.saveSdkLog(logMe, "YZX_SDK_");
		}
	}

	public static void i(String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			i(LOGTAG, logMe);
		}
		if(UserData.isAllLogToSdcard()){
			FileTools.saveSdkLog(logMe, "YZX_SDK_");
		}
	}

	public static void e(String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			e(LOGTAG, logMe);
		}
		if(UserData.isAllLogToSdcard()){
			FileTools.saveSdkLog(logMe, "YZX_SDK_");
		}
	}

	//-----------------------------------------------
	
	public static void v(String tag, String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.v(tag, logMe);
		}
		if(UserData.isSaveLogToSD()){
			writeLogFile(tag, logMe);
		}
	}
	
	public static void d(String tag, String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.d(tag, logMe);
		}
	}
	
	public static void i(String tag, String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.i(tag, logMe);
			//writeLogFile(tag, logMe);
		}
	}
	
	public static void e(String tag, String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.e(tag, logMe);
			//writeLogFile(tag, logMe);
		}
	}
	
	public static void e(String logMe, Exception ex) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.e(LOGTAG, logMe, ex);
		}
	}
	public static void e(String tag, String logMe, Exception ex) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.e(tag, logMe, ex);
		}
	}
	
	//----------------------------------------
	
	private static SimpleDateFormat sDateFormatYMD = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
	public synchronized static void writeLogFile(String fileName, String data) {
		String path = FileTools.getSdCardFilePath();
		try {
			File filePath = new File(path);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			File file = new File(filePath + "/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			StringBuffer buffer = new StringBuffer();
			String dateString = sDateFormatYMD.format(new Date(System.currentTimeMillis()));
			buffer.append(dateString).append("   ").append(data).append("\r\n");

			RandomAccessFile raf = new RandomAccessFile(file, "rw");// "rw" 读写权限
			raf.seek(file.length());
			raf.write(buffer.toString().getBytes());
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}