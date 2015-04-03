package com.yzx.service;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.yzx.http.LoginTools;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.LoginListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;
import com.yzx.tcp.TcpConnection;
import com.yzx.tcp.TcpTools;
import com.yzx.tcp.packet.MessageTools;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tools.CpsTools;
import com.yzx.tools.CustomLog;
import com.yzx.tools.ServiceConfigTools;

public class ServiceLoginTools {

	/**
	 * AMS认证
	 * @param accountSid
	 * @param accountToken
	 * @param clientId
	 * @param clientPwd
	 * @author: xiaozhenhua
	 * @data:2014-5-19 下午2:41:22
	 */
	public static void loginAction(String accountSid, String accountToken,String clientId,String clientPwd,final LoginListener loginSuccess){
		LoginTools.loginAction(accountSid, accountToken,clientId,clientPwd,new LoginListener() {
			@Override
			public void onLoginStateResponse(JSONObject resultJson,Exception e) {
				parseResult(resultJson,e,loginSuccess);
			}
			
			@Override
			public void onCSAddressResponse(JSONObject csAddressJson,Exception e) {
			}

			@Override
			public void onLoginSuccess() {
				
			}
		});
	}
	
	/**
	 * AMS认证(token)
	 * @param token
	 * @param loginSuccess
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午3:37:17
	 */
	public static void loginAction(String token,final LoginListener loginSuccess){
		LoginTools.loginAction(token, new LoginListener() {
			@Override
			public void onLoginSuccess() {
				
			}
			
			@Override
			public void onLoginStateResponse(JSONObject resultJson, Exception e) {
				parseResult(resultJson,e,loginSuccess);
			}
			
			@Override
			public void onCSAddressResponse(JSONObject csAddressJson, Exception e) {
				
			}
		});
	}
	
	
	/**
	 * 解析登录反回信息
	 * @param resultJson
	 * @param e
	 * @param loginSuccess
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午3:35:40
	 */
	private static void parseResult(JSONObject resultJson,Exception e,LoginListener loginSuccess){
		if(e == null){
			if(resultJson != null){
				//CustomLog.v("CONTROLL_LOGIN_RESPONSE:"+resultJson.toString());
				try {
					if(resultJson.has(PacketDfineAction.RESULT) && resultJson.getInt(PacketDfineAction.RESULT) == 0){
						CustomLog.v("LOGIN_SUCCESS ... ");
						if(resultJson.has("phone")){
							UserData.savePhoneNumber(resultJson.getString("phone").replace(" ", ""));
						}
						if(resultJson.has("ssid")){
							UserData.saveSsid(resultJson.getString("ssid").replace(" ", ""));
						}
						if(resultJson.has("uid")){
							UserData.saveClientId(resultJson.getString("uid").replace(" ", ""));
						}
						//UserData.saveAc("0001%asX88NjrYq1QbM9H3B2T8Ep8PX/JGDyT+9TDr49wgJQitJZPWwWcE+TkjB60gKY97n3frsOctJuEUoULEAmXI/HxJsyFhHP4UL/tf5u/y6s=");
						UserData.saveAc(resultJson.get("im_ssid").toString().replace(" ", ""));
						if(resultJson.has("name")){
							UserData.saveNickName(resultJson.getString("name"));
						}
						if(resultJson.has("forwardnumber")){
							UserData.saveForwardNumber(resultJson.getString("forwardnumber"));
						}
						getCsAddress();
						if(loginSuccess != null){
							loginSuccess.onLoginSuccess();
						}
						//CustomLog.v("UID:"+resultJson.getString("uid").replace(" ", "")+"     IM_SSID:"+resultJson.get("im_ssid").toString().replace(" ", ""));
						getRtppAndStunAddress();
						//get cps param---ghj20141021
						//CustomLog.v("#####getCpsParam()#####");
						getCpsParam(false);
					}else{
						CustomLog.v("LOGIN_FAILED ... ");
						int result = resultJson.getInt(PacketDfineAction.RESULT);
						resultSwitch(result);
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					for(ConnectionListener cl:TcpConnection.getConnectionListener()){
						cl.onConnectionFailed(new UcsReason(300003).setMsg(e1.toString()));
					}
				}
			}else{
				for(ConnectionListener cl:TcpConnection.getConnectionListener()){
					cl.onConnectionFailed(new UcsReason(300004).setMsg("login time out"));
				}
			}
		}else{
			e.printStackTrace();
			if(e instanceof IOException){
				for(ConnectionListener cl:TcpConnection.getConnectionListener()){
					cl.onConnectionFailed(new UcsReason(300001).setMsg(e.toString()));
				}
			}else if(e instanceof JSONException){
				for(ConnectionListener cl:TcpConnection.getConnectionListener()){
					cl.onConnectionFailed(new UcsReason(300003).setMsg(e.toString()));
				}
			}
		}
	}
	
	
		/**
	 * 获取Cps策略参数
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午2:46:15
	 */
	public static void getCpsParam(boolean isFastGet){
		//CustomLog.v("---getCpsParam:----");
		CpsTools.getCpsParam(isFastGet);
	}
	
	/**
	 * 获取RTPP地址
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午2:46:15
	 */
	public static void getRtppAndStunAddress(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				ServiceConfigTools.getRtppAndStunList();
			}
		}).start();
	}
	

	/**
	 * 获取CS地址
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午11:39:01
	 */
	public static void getCsAddress(){
		//获取CS
		LoginTools.getCsAddress(UserData.getAccountSid(),new LoginListener() {
			@Override
			public void onLoginStateResponse(JSONObject resultJson, Exception e) {}
			
			@Override
			public void onCSAddressResponse(JSONObject csAddressJson, Exception e) {
				if(e == null){
					if(csAddressJson != null){
						//CustomLog.v("CS_RESPONSE:"+csAddressJson.toString());
						try {
							if(csAddressJson.getInt(PacketDfineAction.RESULT) == 200){
								CustomLog.v("CS_SUCCESS ... ");
								if (csAddressJson.has("data")) {
									JSONObject jObject = csAddressJson.getJSONObject("data");
									//CustomLog.v("CS_ADDRESS:"+jObject.toString());
									if (jObject.has("csaddr")) {
										UserData.saveImServicesAddress(jObject.getString("csaddr"));
									}
								}
								new Thread(new Runnable() {
									@Override
									public void run() {
										TcpTools.tcpConnection();
									}
								}).start();
							}else{
								CustomLog.v("CS_FAILED ... ");
								resultSwitch(csAddressJson.getInt(PacketDfineAction.RESULT));
							}
						} catch (JSONException e1) {
							for(ConnectionListener cl:TcpConnection.getConnectionListener()){
								cl.onConnectionFailed(new UcsReason(300002).setMsg(e1.toString()));
							}
							e1.printStackTrace();
						}
					}else{
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							cl.onConnectionFailed(new UcsReason(300004).setMsg("request csm time out"));
						}
					}
				}else{
					e.printStackTrace();
					if(e instanceof IOException){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							cl.onConnectionFailed(new UcsReason(300001).setMsg(e.toString()));
						}
					}else if(e instanceof JSONException){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							cl.onConnectionFailed(new UcsReason(300003).setMsg(e.toString()));
						}
					}
				}
			}

			@Override
			public void onLoginSuccess() {
				
			}
		});
	}
	
	
	/**
	 * 消息分发
	 * @param result
	 * @author: xiaozhenhua
	 * @data:2014-6-7 下午5:48:11
	 */
	public static void resultSwitch(int result){
		switch(result){
		case -3:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300005).setMsg(""));
			}
			break;
		case 501001:
		case 1:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300006).setMsg("service return parameters is null"));
			}
			break;
		case 2:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300007).setMsg(""));
			}
			break;
		case 3:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300008).setMsg(""));
			}
			break;
		case 501002:
		case 4:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300009).setMsg(""));
			}
			break;
		case 10:
			if(ConnectionControllerService.getInstance() != null){
				ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_LOGIN));
			}
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300010).setMsg(""));
			}
			
			break;
		case 11:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300011).setMsg(""));
			}
			break;
		case 501005:
		case 501006:
		case 501009:
		case 12:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300012).setMsg(""));
			}
			break;
		case 23:
			MessageTools.notifyMessages(new UcsReason(300013).setMsg("send file failed"), null);
			break;
		case 501003:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300014).setMsg(""));
			}
			break;
		case 501004:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300015).setMsg(""));
			}
			break;
		case 501007:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300016).setMsg(""));
			}
			break;
		case 501008:
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason(300017).setMsg(""));
			}
			break;
		}
	}
}
