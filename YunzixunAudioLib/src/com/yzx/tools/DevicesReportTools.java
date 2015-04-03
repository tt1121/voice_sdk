package com.yzx.tools;

import org.json.JSONException;
import org.json.JSONObject;

import com.yzx.http.HttpTools;
import com.yzx.listenerInterface.ReportListener;
import com.yzx.preference.UserData;
import com.yzx.tcp.packet.PacketDfineAction;

import android.content.Context;

public class DevicesReportTools {

	
	private final static String REPORT = "IS_REPORT_INFO";
	
	
	public static void reportDevicesInfo(final Context mContext ,final String clientId ,final ReportListener reportDevicesListener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = DefinitionAction.REPORT_URL+"/ulog/log?event=mobileLog";
				JSONObject json = new JSONObject();
				try {
					json.put("operator", DevicesTools.getSimOperator(mContext));
					json.put("imei", DevicesTools.getDevicesImei(mContext));
					json.put("brand", android.os.Build.BRAND);
					json.put("model", android.os.Build.MODEL.replaceAll(" ", ""));
					//json.put("width", value)
					//json.put("height", value)
					json.put("mac", DevicesTools.getDevicesMacAddress(mContext));
					//json.put("cpu", value)
					//json.put("cpu_rate", value)
					//json.put("memory", value)
					json.put("os", "android");
					json.put("version", android.os.Build.VERSION.SDK_INT);
					json.put("sdkVersion", Util.SDK_VERSION);
					json.put("demoVersion", UserData.getVersionName(mContext));
					json.put("packageName", UserData.getPackageName(mContext));
					json.put("clientNumber", clientId);
					json.put("logDate", DateTools.getDate());
					
					//CustomLog.v("REPORT_DEVICES_INFO:"+json.toString());
					//CustomLog.v("REPORT_DEVICES_RUL:"+url);
					JSONObject resultJson = HttpTools.doPostMethod(mContext, url, json.toString());
					
					if(resultJson != null && resultJson.has("code")){
						CustomLog.v("REPORT_DEVICES_RESPONSE_JSON:"+resultJson);
						reportDevicesListener.onReportResult(resultJson.getInt("code"), resultJson.has("result")?resultJson.getString("result"):"");
					}else{
						reportDevicesListener.onReportResult(-1, "response is null");
					}
				} catch (JSONException e) {
					reportDevicesListener.onReportResult(-2, e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 是否上报过设备日志
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-20 上午9:34:23
	 */
	public static boolean isReportDevicesInfo(Context mContext){
		return mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean(REPORT, true);
	}
	public static void saveReportDevicesInfo(Context mContext,boolean isReport){
		mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean(REPORT, isReport).commit();
	}
	
}
