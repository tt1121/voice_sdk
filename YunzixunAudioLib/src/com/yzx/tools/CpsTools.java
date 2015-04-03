package com.yzx.tools;

import org.json.JSONException;
import org.json.JSONObject;

import org.webrtc.voiceengine.AudioDeviceUtil;

import android.content.Context;

import com.yzx.http.net.InterfaceConst;
import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.service.ConnectionControllerService;


public class CpsTools {
	
	/**
	 * Ĭ�����ò���Ȩ����Ϣ
	 * 
	 * @author: lion
	 * @throws JSONException 
	 * @data:2014-6-16 ����11:30:36
	 */
	public static void setDynamicPolicyEnable(boolean enable){
		AudioDeviceUtil.getInstance().setDynamicPolicyEnable(enable);
	}
	
	/**
	 * Ĭ�����ò���Ȩ����Ϣ
	 * 
	 * @author: lion
	 * @throws JSONException 
	 * @data:2014-6-16 ����11:30:36
	 */
	public static void setCpsDefPermission(Context mContext){
		String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
		if(permission.length() <= 0){
			JSONObject jsObj;
			try {
				jsObj = new JSONObject();
				jsObj.put("iceenable", 0);
				jsObj.put("audiofec", 0);
				jsObj.put("logreport", 0);
				jsObj.put("vqmenable", 0);
				jsObj.put("autoadapter", 1);
				jsObj.put("prtpenable", 0);
				//CustomLog.v("setCpsDefPermission:"+jsObj.toString());
				setDynamicPolicyEnable(true);//default DynamicPolicy disabled
				AudioDeviceUtil.getInstance().setPermissionParam(ConnectionControllerService.getInstance(), jsObj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ����android����������Ϣ
	 * 
	 * @author: lion
	 * @data:2014-6-16 ����11:30:36
	 */
	public static void setCpsAudioAdapterParam(){
	
		AudioDeviceUtil.getInstance().setAudioDeviceParam(ConnectionControllerService.getInstance());
	}
	
	/**
	 * ��ȡ����Ȩ����Ϣ
	 * 
	 * @author: lion
	 * @data:2014-6-16 ����11:30:36
	 */
	public static void getCpsParam(boolean isFastGet){
	
		AudioDeviceUtil.getInstance().AakTaskload(ConnectionControllerService.getInstance(), InterfaceConst.getParameter, isFastGet);
	}

	/**
	 * ��ȡ��Ƶ��������������Ϣ
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 ����11:30:36
	 */
	public static void getCpsAdListParam(){
		AudioDeviceUtil.getInstance().AakTaskload(ConnectionControllerService.getInstance(), InterfaceConst.getParameter_list, false);
	}

	/**
	 * �ϱ���Ƶ��������ɹ���Ϣ
	 * 
	 * @author: lion
	 * @data:2014-6-16 ����11:30:36
	 */
	public static void postCpsAndroidDeviceParam(){
		AudioDeviceUtil.getInstance().AakTaskload(ConnectionControllerService.getInstance(), InterfaceConst.postadsuccess, false);
	}

	/**
	 * �ϱ���Ƶ���������쳣��Ϣ
	 * 
	 * @author: lion
	 * @data:2014-6-16 ����11:30:36
	 */
	public static void postCpsAdExceptionParam(){
		AudioDeviceUtil.getInstance().AakTaskload(ConnectionControllerService.getInstance(), InterfaceConst.postadexception, false);
	}

}

