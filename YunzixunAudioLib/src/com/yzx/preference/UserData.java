package com.yzx.preference;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.voiceengine.AudioDeviceUtil;


import android.content.Context;

import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tools.RC4Tools;

public class UserData {
	
	
	private static ArrayList<String> csServiceAddress = new ArrayList<String>();	//CS服务器列表

	public static void saveImServicesAddress(String json) {
		if (csServiceAddress.size() > 0) {
			csServiceAddress.clear();
		}
		if(json != null && json.length() > 0){
			try {
				JSONArray array = new JSONArray(json);
				if (array != null && array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						csServiceAddress.add(array.getString(i));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally{
				if(ConnectionControllerService.getInstance() != null){
					ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("CS_ADDRESS_LIST", RC4Tools.encry_RC4_string(json)).commit();
				}
			}
		}
	}
	
	public static ArrayList<String> getImServiceAddress(){
		String json = ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("CS_ADDRESS_LIST", ""):"";
		if(json != null && json.length() > 0){
			if(isEncryption(json)){
				json = RC4Tools.decry_RC4(json);
			}
		}
		if (csServiceAddress.size() > 0) {
			csServiceAddress.clear();
		}
		saveImServicesAddress(json);
		//csServiceAddress.add("172.16.10.34:7301");
		return csServiceAddress;
	}
	
	public static String getAc(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_AC", ""):"";
	}
	public static void saveAc(String ac){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_AC", ac).commit();
		}
	}
	
	/**
	 * 昵称 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-25 上午11:50:44
	 */
	public static String getNickName(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_NICK_NAME", ""):"";
	}
	public static void saveNickName(String nickName){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_NICK_NAME", nickName).commit();
		}
	}
	
	/**
	 * 转呼号码
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-25 上午11:53:51
	 */
	public static String getForwardNumber(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_FORWARD_NUMBER", ""):"";
	}
	public static void saveForwardNumber(String forwardNumber){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_FORWARD_NUMBER", forwardNumber).commit();
		}
	}
	
	
	public static String getVersionName(Context mContext){
		return mContext != null ? (mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_VERSION_NAME", "")) : (ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_VERSION_NAME", ""):"");
	}
	public static void saveVersionName(Context mContext,String versionName){
		mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_VERSION_NAME", versionName).commit();
	}
	
	public static String getPackageName(Context mContext){
		return mContext != null ? (mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_PACKAGE_NAME", "")):(ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_PACKAGE_NAME", ""):"");
	}
	public static void savePackageName(Context mContext,String packageName){
		mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_PACKAGE_NAME", packageName).commit();
	}
	
	/**
	 * 当前联接TCP的索引
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午7:36:54
	 */
	public static int getCurrentTcpIndex(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getInt("YZX_TCP_INDEX", 0):0;
	}
	/**
	 * 保存当前连接TCP的索引
	 * @param index
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午7:38:22
	 */
	public static void saveCurrentTcpIndex(int index){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putInt("YZX_TCP_INDEX", index).commit();
		}
	}
	
	public static String getAccountSid(){
		String accountSid = ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_ACCOUNT_SID", ""):"";
		if(accountSid != null && accountSid.length() > 0){
			if(isEncryption(accountSid)){
				accountSid = RC4Tools.decry_RC4(accountSid);
			}
			//CustomLog.v("解密："+RC4Tools.decry_RC4(accountSid));
		}
		return accountSid;
	}
	public static void saveAccountSid(String accountSid){
		if(ConnectionControllerService.getInstance() != null){
			//CustomLog.v("加密："+RC4Tools.encry_RC4_string(accountSid));
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_ACCOUNT_SID", RC4Tools.encry_RC4_string(accountSid)).commit();
		}
	}
	
	public static String getAccountToken(){
		String accountPwd = ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_ACCOUNT_PWD", ""):""; 
		if(accountPwd != null && accountPwd.length() > 0){
			if(isEncryption(accountPwd)){
				accountPwd = RC4Tools.decry_RC4(accountPwd);
			}
		}
		return accountPwd;
	}
	public static void saveAccountToken(String accountPwd){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_ACCOUNT_PWD", RC4Tools.encry_RC4_string(accountPwd)).commit();
		}
	}
	public static String getClientId(){
		String clientId = ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CLIENT_ID", ""):"";
		if(clientId != null && clientId.length() > 0){
			if(isEncryption(clientId)){
				clientId = RC4Tools.decry_RC4(clientId);
			}
		}
		return clientId;
	}
	public static void saveClientId(String clientId){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CLIENT_ID", RC4Tools.encry_RC4_string(clientId)).commit();
		}
	}
	
	public static String getClientPwd(){
		String clientpwd = ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CLIENT_PWD", ""):"";
		if(clientpwd != null && clientpwd.length() > 0){
			if(isEncryption(clientpwd)){
				clientpwd = RC4Tools.decry_RC4(clientpwd);
			}
		}
		return clientpwd;
	}
	public static void saveClientPwd(String clientPwd){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CLIENT_PWD", RC4Tools.encry_RC4_string(clientPwd)).commit();
		}
	}
	
	public static String getPhoneNumber(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CLIENT_PHONE", ""):"";
	}
	public static void savePhoneNumber(String phone){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CLIENT_PHONE", phone).commit();
		}
	}
	
	public static String getSsid(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CLIENT_SSID", ""):"";
	}
	public static void saveSsid(String ssid){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CLIENT_SSID", ssid).commit();
		}
	}
	/**
	 * 保存上一次登录时间
	 * @param time
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午10:45:04
	 */
	public static void saveLoginLastTime(long time){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putLong("YZX_LAST_TIME_LOGIN", time).commit();
		}
	}
	public static long getLoginLastTime(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getLong("YZX_LAST_TIME_LOGIN", 0):0;
	}
	
	private static boolean logSwitch = true;
	
	public static void saveLogSwitch(Context mContext,boolean isSwitch){
		logSwitch = isSwitch;
		if(mContext != null){
			mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_CLIENT_LOG_SWITCH", isSwitch).commit();
		}else if (ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_CLIENT_LOG_SWITCH", isSwitch).commit();
		}
	}
	public static boolean isLogSwitch(){
		return ConnectionControllerService.getInstance() != null ? (ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_CLIENT_LOG_SWITCH", logSwitch)):logSwitch;
	}
	
	
	//
	public static void saveHost(String host){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_HOST", host).commit();
		}
	}
	public static String getHost(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_HOST", ""):"";
	}
	public static void savePort(String port){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_PORT", port).commit();
		}
	}
	public static String getPort(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_PORT", ""):"";
	}

	//ams接口
	public static String getHttpLoginUrl(){
		return getHost().length() > 0 ? (getHost().startsWith("https")?(getHost()+"/ams2/login.act"):(getHost()+":"+getPort()+"/ams2/login.act")) :RC4Tools.decry_RC4("387d536c4d28f80c59ac0914d5345927900f271884088ee91f2f05845ef46caa6feb4a");
	}
	private static String getHttpUrl(){
		return getHost().length() > 0 ? (getHost().startsWith("https")?getHost().replace("https", "http"):getHost())+":" :RC4Tools.decry_RC4("387d536c043df8125fa9150bd22c46268e122016");
	}
	//csm地址
	public static  String getHttpCsmUrl(){
		return getHttpUrl()+(getPort().length() > 0 ? getPort() : "7301")+"/getcsaddr";
	}
	//获取上传附件地址
	public static String getHttpUploadFileUrl(){
		return getHttpUrl()+"8010";
	}
	//获取回拨地址
	public static String getHttpCallBackUrl(){
		return getHttpUrl()+(getPort().length() > 0 ? getPort()+"/" : "8020/");
	}

	public static String getCps(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CPS_PARAM", ""):"";
	}
	public static void saveCps(String cps){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CPS_PARAM", cps).commit();
		}
	}
	
	/**
	 * 当前来电号码
	 * @param phone
	 * @author: xiaozhenhua
	 * @data:2014-6-12 下午3:44:58
	 */
	/*public static void saveCurrentInCallNumber(String phone){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CURRENT_IN_CALL_PHONE", phone).commit();
		}
	}
	
	public static String getCurrnetInCallNumber(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CURRENT_IN_CALL_PHONE", ""):""; 
	}*/
	
	
	/**
	 * 保存RTPP List
	 * @param rtpp
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午11:29:20
	 */
	public static void saveRtppAddressList(String rtpp){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_RTPP_ADDRESS_LIST", RC4Tools.encry_RC4_string(rtpp)).commit();
		}
	}
	
	public static String getRtppAddressList(){
		String rtpp = ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_RTPP_ADDRESS_LIST", ""):"";
		if(rtpp != null && rtpp.length() > 0){
			if(isEncryption(rtpp)){
				rtpp = RC4Tools.decry_RC4(rtpp);
			}
		}
		return rtpp;
	}
	
	/**
	 * 
	 * @param rtpp
	 * @author: xiaozhenhua
	 * @data:2014-10-23 下午12:24:39
	 */
	public static void saveStunAddressList(String stun){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_STUN_ADDRESS_LIST", RC4Tools.encry_RC4_string(stun)).commit();
		}
	}
	
	public static String getStunAddressList(Context mContext){
		String stun = mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_STUN_ADDRESS_LIST", "");
		String stunAdd = "";
		try {
			if(stun != null && stun.length() > 0){
				if(isEncryption(stun)){
					stun = RC4Tools.decry_RC4(stun);
				}
			}
			JSONArray array = new JSONArray(stun);
			stunAdd = array.get(0).toString(); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return stunAdd;
		//return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_STUN_ADDRESS_LIST", ""):"";
	}
	
	
	/**
	 * 获取登录类型     0：明文 (有主账号与子账号信息)    1：密文(token)
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午3:40:21
	 */
	public static int getLoginType(){
		return ConnectionControllerService.getInstance() != null ? ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getInt("YZX_LOGIN_TYPE", 0):0;
	}
	
	public static void saveLoginType(int type){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putInt("YZX_LOGIN_TYPE", type).commit();
		}
	}

	/**
	 * 判断是否使用了RC4加密
	 * @param input
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-12-29 下午4:18:38
	 */
	private static boolean isEncryption(String input)
    {
		boolean isEquals = true;
		char[] str = "ghijklmnopqrstuvwxyz!@#$%^&*()~,.<>?/:;{}[]|+=-_*".toCharArray();
		for(char value:str){
			isEquals = input.toLowerCase().contains(value+"");
			if(isEquals){
				break;
			}
		}
		return !isEquals;
    }
	
	/*public static int getCallMode(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getInt("YZX_CALL_MODE", 6);
		}else{
			return 6;
		}
	}
	public static void saveCallMode(int mode){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putInt("YZX_CALL_MODE", mode).commit();
		}
	}*/
	
	public static int getCallTimeoutTime(Context mContext){
		return mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getInt("YZX_CALL_TIMEOUT_TIME", 95000);
	}
	public static void saveCallTimeoutTime(Context mContext,int time){
		mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putInt("YZX_CALL_TIMEOUT_TIME", time).commit();
	}
	
	public static boolean isCurrentCall(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_CURRENT_CALL", false);
		}else{
			return false;
		}
	}
	
	public static void saveCurrentCall(boolean connect){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_CURRENT_CALL", connect).commit();
		}
	}
	
	public static String getCurrentCallUid(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CURRENT_CALL_UID", "");
		}else{
			return "";
		}
	}
	
	public static void saveCurrentCallUid(String uid){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CURRENT_CALL_UID", uid).commit();
		}
	}
	
	public static String getCurrentCallPhone(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CURRENT_CALL_PHONE", "");
		}else{
			return "";
		}
	}
	
	public static void saveCurrentCallPhone(String phone){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CURRENT_CALL_PHONE", phone).commit();
		}
	}
	
	public static String getCurrentCallConference(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CURRENT_CALL_CONFERENCE", "");
		}else{
			return "";
		}
	}
	
	public static void saveCurrentCallConference(String conference){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CURRENT_CALL_CONFERENCE", conference).commit();
		}
	}
	
	public static String getCurrentCallMode(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getString("YZX_CURRENT_CALL_TYPE", "");
		}else{
			return "";
		}
	}
	
	public static void saveCurrentCallMode(int type){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putString("YZX_CURRENT_CALL_TYPE", type+"").commit();
		}
	}
	
	public static boolean isMySelfRefusal(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_MYSELF_REFUSAL", false);
		}else{
			return false;
		}
	}
	public static void saveMySelfRefusal(boolean refusal){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_MYSELF_REFUSAL", refusal).commit();
		}
	}
	
	
	public static void cleanPreference(){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().clear().commit();
		}
	}
	
	/**
	 * 是否接听电话
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-31 下午4:05:09
	 */
	public static boolean isAnswer(Context mContext){
		return mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_ANSWER", false);
	}
	public static void saveAnswer(Context mContext,boolean refusal){
		mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_ANSWER", refusal).commit();
	}
	
	
	//----------------记录SD卡日志后门开关--------------------
	
	public static void saveLogToSD(boolean isPringLog){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_DEVICE_LOG_TYPE", isPringLog).commit();
		}
	}
	
	public static boolean isSaveLogToSD(){
		//return true;
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_DEVICE_LOG_TYPE", false);
		}else{
			return false;
		}
	}
	//-----------------------策略控制-------------------------
	public static boolean isIceEnable(Context mContext){
		return parseBooleanJson(mContext,BooleanType.ICE);
	}
	public static boolean isAudioAutoAdapter(Context mContext){
		return parseBooleanJson(mContext,BooleanType.ADAPTER);
	}
	public static int getAudioFec(Context mContext){
		return parseIntJson(mContext, IntType.FEC);
	}
	public static int getVpmEnable(Context mContext){
		return parseIntJson(mContext, IntType.VPM);
	}
	public static int getPrtpEnable(Context mContext){
		return parseIntJson(mContext, IntType.PRTP);
	}
	enum BooleanType{
		ICE,ADAPTER
	}
	enum IntType{
		FEC,VPM,PRTP
	}
	
	private static boolean parseBooleanJson(Context mContext,BooleanType type){
		boolean booleanType = false;
		String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
		//CustomLog.v("permission:"+permission);
		if(permission.length() > 0){
			try {
				JSONObject jsObj = new JSONObject(permission);
				if(type == BooleanType.ICE){
					booleanType = jsObj.getInt("iceenable") != 0 ? true : false;
				}else if(type == BooleanType.ADAPTER){
					booleanType = jsObj.getInt("autoadapter") != 0 ? true : false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return booleanType;
	}
	
	private static int parseIntJson(Context mContext,IntType type){
		int intType = 0;
		String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
		//CustomLog.v("permission:"+permission);
		if(permission.length() > 0){
			try {
				JSONObject jsObj = new JSONObject(permission);
				if(type == IntType.FEC){
					intType = jsObj.getInt("audiofec");
				}else if(type == IntType.VPM){
					intType = jsObj.getInt("vqmenable");
				}else if(type == IntType.PRTP){
					intType = jsObj.getInt("prtpenable");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return intType;
	}
	
	
	/**
	 * 是否呼叫转移
	 * @param isForwarding
	 * @author: xiaozhenhua
	 * @data:2014-11-25 下午5:19:01
	 */
	public static void saveForwarding(boolean isForwarding){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_FORWARDING", isForwarding).commit();
		}
	}
	public static boolean isForwarding(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_FORWARDING", false);
		}else{
			return false;
		}
	}
	public static void saveCurrentForwarding(boolean isForwarding){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_CURRENT_FORWARDING", isForwarding).commit();
		}
	}
	public static boolean isCurrentForwarding(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_CURRENT_FORWARDING", false);
		}else{
			return false;
		}
	}
	
	//----------------------是否自己设置的超时时间挂断------------------------
	public static void saveMySelfTimeOutHangup(Context mContext,boolean isMySelf){
		if(mContext != null){
			mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_MYSELF_CALL_TIMEOUT", isMySelf).commit();
		}
	}
	public static boolean isMySelfTimeOutHangup(Context mContext){
		if(mContext != null){
			return mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_MYSELF_CALL_TIMEOUT", false);
		}else{
			return false;
		}
	}
	
	//--------------------------没有网络或网络中断挂断电话--------------------------
	public static void saveNotNetworkHangup(Context mContext,boolean isNetWork){
		if(mContext != null){
			mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_NOT_NETWORK_HANGUP", isNetWork).commit();
		}
	}
	public static boolean isNotNetworkHangup(Context mContext){
		if(mContext != null){
			return mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_NOT_NETWORK_HANGUP", false);
		}else{
			return false;
		}
	}
	
	/**
	 * 测试环境下打开sdk日志保存到sd卡中
	 * 
	 */
	public static void saveAllLogToSdcard(boolean isToSdcard){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_CURRENT_TOSDCARD", isToSdcard).commit();
		}
	}
	public static boolean isAllLogToSdcard(){
		if(ConnectionControllerService.getInstance() != null){
			return ConnectionControllerService.getInstance().getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_CURRENT_TOSDCARD", false);
		}else{
			return false;
		}
	}
	/**
	 * 当前是否正在连接(处理系统有时会同时报两次连网的问题)
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-12-31 下午2:37:03
	 */
	/*public static void saveCurrentConnect(Context mContext,boolean isCurrentConnect){
		if(mContext != null){
			CustomLog.v("SAVE_CURRENT_CONNTCT:"+isCurrentConnect);
			mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).edit().putBoolean("YZX_CURRENT_CONNECT", isCurrentConnect).commit();
		}
	}
	public static boolean isCurrentConnect(Context mContext){
		if(mContext != null){
			return mContext.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 1).getBoolean("YZX_CURRENT_CONNECT", true);
		}else{
			return true;
		}
	}*/
}
