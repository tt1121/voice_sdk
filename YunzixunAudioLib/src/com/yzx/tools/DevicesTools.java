package com.yzx.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.yzx.tcp.packet.PacketDfineAction;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class DevicesTools {
	
	
	private static final String SIMCARD = "select_sim_card";
	private static final String SIMCARD_1 = "simcard_1";
	private static final String SIMCARD_2 = "simcard_2";
	private static final String ISDOUBLE = "isSingle";
	
	
	/**
	 * 获取本机的mac地址
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午3:36:36
	 */
	public static String getDevicesMacAddress(Context mContext){
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi != null ? wifi.getConnectionInfo() : null;
		if (info != null) {
			String macString = info.getMacAddress();
			return macString != null && macString.length() > 0 ? macString :"";
		} else {
			return "";
		}
	}
	
	/**
	 * 获取设备IMEI
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:29:31
	 */
	public static String getDevicesImei(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei != null && imei.length() > 0 ? imei : "";
	}

	/**
	 * 获取设备IMSI
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:29:44
	 */
	public static String getDevicesImsi(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi != null && imsi.length() > 0 ? imsi : "";
	}
	
	/**
	 * 初始化是否双卡
	 * @param mContext
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:45:43
	 */
	public static void initIsDoubleTelephone(Context mContext){
		boolean isDouble = true;
		Method method = null;
		Object result_0 = null;
		Object result_1 = null;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			method = TelephonyManager.class.getMethod("getSimStateGemini",new Class[] { int.class });
			result_0 = method.invoke(tm, new Object[] { new Integer(0) });
			result_1 = method.invoke(tm, new Object[] { new Integer(1) });
		} catch (SecurityException e) {
			isDouble = false;
			//e.printStackTrace();
		} catch (NoSuchMethodException e) {
			isDouble = false;
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			isDouble = false;
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			isDouble = false;
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			isDouble = false;
			//e.printStackTrace();
		} catch (Exception e){
			isDouble = false;
			//e.printStackTrace();
		}
		SharedPreferences sp = mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1); 
		Editor editor = sp.edit();
		if(isDouble){
			//保存为双卡手机
			editor.putBoolean(ISDOUBLE, true);
			//保存双卡是否可用
			if(result_0.toString().equals("5") && result_1.toString().equals("5")){
				if(!sp.getString(SIMCARD, "2").equals("0") && !sp.getString(SIMCARD, "2").equals("1")){
					editor.putString(SIMCARD, "0");
				}
				editor.putBoolean(SIMCARD_1, true);
				editor.putBoolean(SIMCARD_2, true);
			} else if(!result_0.toString().equals("5") && result_1.toString().equals("5")){
				if(!sp.getString(SIMCARD, "2").equals("0") && !sp.getString(SIMCARD, "2").equals("1")){
					editor.putString(SIMCARD, "1");
				}
				editor.putBoolean(SIMCARD_1, false);
				editor.putBoolean(SIMCARD_2, true);
			} else if(result_0.toString().equals("5") && !result_1.toString().equals("5")){
				if(!sp.getString(SIMCARD, "2").equals("0") && !sp.getString(SIMCARD, "2").equals("1")){
					editor.putString(SIMCARD, "0");
				}
				editor.putBoolean(SIMCARD_1, true);
				editor.putBoolean(SIMCARD_2, false);
			} else {
				editor.putBoolean(SIMCARD_1, false);
				editor.putBoolean(SIMCARD_2, false);
			}
			CustomLog.v("CURRENT_SIM_CARD_SIZE:"+2);
		}else{
			//保存为单卡手机
			editor.putString(SIMCARD, "0");
			editor.putBoolean(ISDOUBLE, false);
			CustomLog.v("CURRENT_SIM_CARD_SIZE:"+1);
		}
		editor.commit();
	}
	
	/**
	 * 获取运营商
	 * @param context
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:45:59
	 */
	public static String getSimOperator(Context mContext){
		SharedPreferences sp = mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1);
		if(!sp.getBoolean(ISDOUBLE, false)){
			return getSingleSimOperator(mContext);
		}else{
			return getDoubleSimOperator(mContext, Integer.parseInt(sp.getString(SIMCARD, "0")));
		}
	}
	
	/**
	 * 获取单卡手机的运营商
	 * @param context
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:46:13
	 */
	private static String getSingleSimOperator(Context mContext){
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = "000000";
		if(tm.getSimState() == 5){
			operator = tm.getSimOperator();
		}
		return operator;
	}
	
	/**
	 * 获取双卡或多卡手机的运营商
	 * @param mContext
	 * @param simCardIndex
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:47:23
	 */
	private static String getDoubleSimOperator(Context mContext , int simCardIndex){
		String operator = "000000";
		Method method = null;
		Object result = null;
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			method = TelephonyManager.class.getMethod("getSimStateGemini",new Class[] { int.class });
			result = method.invoke(tm, new Object[] { new Integer(simCardIndex) });
			String sim0State = result.toString();
			if(sim0State.equals("5")){
				method = TelephonyManager.class.getMethod("getSimOperatorGemini",new Class[] { int.class });
				operator = method.invoke(tm, new Object[] { new Integer(simCardIndex) }).toString();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return operator;
	}
}
