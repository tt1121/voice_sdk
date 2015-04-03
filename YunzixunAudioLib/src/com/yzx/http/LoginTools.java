package com.yzx.http;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.yzx.listenerInterface.LoginListener;
import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tools.CustomLog;
import com.yzx.tools.MD5Tools;
import com.yzx.tools.NetWorkTools;

/**
 * 
 * @author xiaozhenhua
 *
 */
public class LoginTools {
	
	/**
	 * 验证账号(登录)
	 * @param amsAddress:登录地址
	 * @param mainaccount：主账号(开发者账号)
	 * @param mainaccountpwd:主账号密码(开发者账号密码)
	 * @param voipaccount:子账号(用户账号)
	 * @param voipaccountpwd:子账号密码(用户密码)
	 * @param imei:设备IMEI
	 * @author: xiaozhenhua
	 * @data:2014-4-9 下午3:06:04
	 */
	public static void loginAction(final String accountSid,final String accoundPwd,final String clientId,final String cliendPwd,final LoginListener LoginStateListenerBack){
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sbf = new StringBuffer();
				sbf.append(UserData.getHttpLoginUrl());
				sbf.append("?account="+clientId);
				sbf.append("&");
				sbf.append("accountpwd="+MD5Tools.getMD5Str(cliendPwd+"~U!X@I#N$"));
				sbf.append("&");
				sbf.append("accounttype="+URLEncoder.encode("mobile"));
				sbf.append("&");
				sbf.append("ip="+URLEncoder.encode(NetWorkTools.getIPAddress(true)));
				sbf.append("&");
				sbf.append("pv="+URLEncoder.encode("android"));
				//sbf.append("&");
				//sbf.append("imei="+URLEncoder.encode(imei));
				sbf.append("&");
				sbf.append("securityver=0");//0:不加密   1:加密
				sbf.append("&");
				sbf.append("mainaccount="+MD5Tools.getMD5Str(accountSid+"~U!X@I#N$"));
				sbf.append("&");
				sbf.append("mainaccountpwd="+MD5Tools.getMD5Str(accoundPwd+"~U!X@I#N$"));
				//CustomLog.v("LOGIN_URL:"+sbf.toString());
				parseJson(sbf,LoginStateListenerBack);
			}
		}).start();
	}
	
	
	private static void parseJson(StringBuffer sbf,LoginListener LoginStateListenerBack){
		try {
			JSONObject resultJson = HttpTools.loginToAms(sbf);
			LoginStateListenerBack.onLoginStateResponse(resultJson,null);
			//CustomLog.v("LOGIN_RESPONSE:"+((resultJson != null)?resultJson:"is null"));
		} catch (IOException e) {
			e.printStackTrace();
			LoginStateListenerBack.onLoginStateResponse(null,e);
		} catch (JSONException e) {
			e.printStackTrace();
			LoginStateListenerBack.onLoginStateResponse(null,e);
		}
	}
	
	
	/**
	 * token登录
	 * @param token
	 * @param LoginStateListenerBack
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午3:32:40
	 */
	public static void loginAction(final String token,final LoginListener LoginStateListenerBack){
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sbf = new StringBuffer();
				sbf.append(UserData.getHttpLoginUrl());
				sbf.append("?accountpwd="+URLEncoder.encode(token));
				sbf.append("&");
				sbf.append("accounttype="+URLEncoder.encode("mobile"));
				sbf.append("&");
				sbf.append("ip="+URLEncoder.encode(NetWorkTools.getIPAddress(true)));
				sbf.append("&");
				sbf.append("pv="+URLEncoder.encode("android"));
				sbf.append("&");
				sbf.append("securityver=1");//0:不加密   1:加密
				//CustomLog.v("TOKEN_LOGIN_URL:"+sbf.toString());
				parseJson(sbf,LoginStateListenerBack);
			}
		}).start();
	}
	
	
	/**
	 * 获取CS地址
	 * @param uid:用户ID
	 * @param version:软件版本
	 * @author: xiaozhenhua
	 * @data:2014-4-11 下午3:09:35
	 */
	public static void getCsAddress(final String cliendId,final LoginListener LoginStateListenerBack){
		//uxin_getCaAddress(cliendId, LoginStateListenerBack);
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer csmAddress = new StringBuffer();
				//CustomLog.v("CS_REPUSET_URL:"+UserData.getHttpCsmUrl());
				csmAddress.append(UserData.getHttpCsmUrl());
				//csmAddress.append("http://172.16.10.34:7601/getcsaddr");
				csmAddress.append("?");
				csmAddress.append("uid="+URLEncoder.encode(cliendId));
				csmAddress.append("&");
				csmAddress.append("version="+URLEncoder.encode(UserData.getVersionName(ConnectionControllerService.getInstance())));
				csmAddress.append("&");
				csmAddress.append("pv="+URLEncoder.encode("android"));
				//CustomLog.v("CS_REPUSET:"+csmAddress.toString());
				try {
					LoginStateListenerBack.onCSAddressResponse(HttpTools.getCsAddress(csmAddress), null);
				} catch (IOException e) {
					CustomLog.v("IO:"+e.toString());
					LoginStateListenerBack.onCSAddressResponse(null, e);
					e.printStackTrace();
				} catch (JSONException e) {
					CustomLog.v("JSON:"+e.toString());
					LoginStateListenerBack.onCSAddressResponse(null, e);
					e.printStackTrace();
				}
			}
		}).start();
	}
}
