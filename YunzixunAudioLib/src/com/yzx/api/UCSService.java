package com.yzx.api;

import java.util.ArrayList;

import com.yzx.http.net.InterfaceUrl;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tcp.TcpConnection;
import com.yzx.tcp.TcpTools;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tools.Util;

import android.content.Context;
import android.content.Intent;

public class UCSService {

	protected static ArrayList<ConnectionListener> connectionListenerList = new ArrayList<ConnectionListener>();//联接状态监听器
	
	public static void addConnectionListener(ConnectionListener cl){
		connectionListenerList.add(cl);
	}
	
	public static ArrayList<ConnectionListener> getConnectionListener(){
		return connectionListenerList;
	}
	
	public static String ACTION_INIT_SUCCESS = "com.yunzhixin.sdk_init_success";
	
	public static void initAction(Context mC){
		if(!ACTION_INIT_SUCCESS.startsWith(mC.getPackageName())){
			ACTION_INIT_SUCCESS = mC.getPackageName() + "_" + ACTION_INIT_SUCCESS;
		}
	}
	
	/**
	 * 初始化SDK
	 * @param mC
	 * @author: xiaozhenhua
	 * @data:2014-4-15 下午2:21:38
	 */
	public static void init(final Context mC,final boolean isSwitch){
		ConnectionControllerService.startCurrentService(mC,isSwitch);
	}
	
	
	/**
	 * 返初始化
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-29 上午11:06:48
	 */
	public static void uninit(){
		ConnectionControllerService.stopCurrentService();
	}

	/**
	 * 通过token连接去服务器
	 * @param token
	 * @author: xiaozhenhua
	 * @data:2014-6-6 下午6:30:14
	 */
	public static void connect(String token){
		if(ConnectionControllerService.getInstance() != null){
			Intent connect = new Intent(PacketDfineAction.INTENT_ACTION_CONNECT);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID_PWD, token);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_TYPE,1);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CHECK_CLIENT,true);
			addHost(connect);
			ConnectionControllerService.getInstance().sendBroadcast(connect);
		}else{
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				//标识init() 没有初始化
				cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
			}
		}
	}
	
	private static  void addHost(Intent intent){
		intent.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_HOST, "https://im.ucpaas.com");
		intent.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_PORT, "8887");
	}
	
	/**
	 * 打开关闭sdk日志
	 * @param isOpenSdkLog
	 */
	public static void openSdkLog(boolean isOpenSdkLog){
		UserData.saveAllLogToSdcard(isOpenSdkLog);
	}
	
	/**
	 * 通过token连接去服务器
	 * @param token
	 * @author: xiaozhenhua
	 * @data:2014-6-6 下午6:30:14
	 */
	public static void connect(String host,String port,String token){
		if(ConnectionControllerService.getInstance() != null){
			Intent connect = new Intent(PacketDfineAction.INTENT_ACTION_CONNECT);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID_PWD, token);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_TYPE,1);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CHECK_CLIENT,true);
			if(host != null && host.length() > 0 && port != null && port.length() > 0){
				connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_HOST, host);
				connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_PORT, port);
			}else{
				addHost(connect);
			}
			ConnectionControllerService.getInstance().sendBroadcast(connect);
		}else{
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				//标识init() 没有初始化
				cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
			}
		}
	}
	
	/**
	 * 连接云服务器
	 * @param accountSid
	 * @param accountToken
	 * @param clientId
	 * @param clientPwd
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午4:41:40
	 */
	public static void connect(String sid,String sidPwd,String clientId,String clientPwd){
		if(ConnectionControllerService.getInstance() != null){
			InterfaceUrl.initUrlToTest(false);
			Intent connect = new Intent(PacketDfineAction.INTENT_ACTION_CONNECT);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID, sid);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID_PWD, sidPwd);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CLIEND, clientId);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CLIEND_PWD, clientPwd);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_TYPE,0);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CHECK_CLIENT,true);
			addHost(connect);
			ConnectionControllerService.getInstance().sendBroadcast(connect);
		}else{
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				//标识init() 没有初始化
				cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
			}
		}
	}
	
	/**
	 * 连接云服务器
	 * @param accountSid
	 * @param accountToken
	 * @param clientId
	 * @param clientPwd
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午4:41:40
	 */
	public static void connect(String host,String port,String sid,String sidPwd,String clientId,String clientPwd){
		if(ConnectionControllerService.getInstance() != null){
			if("http://113.31.89.144".equals(host)){
				InterfaceUrl.initUrlToTest(true);
			}else{
				InterfaceUrl.initUrlToTest(false);
			}
			Intent connect = new Intent(PacketDfineAction.INTENT_ACTION_CONNECT);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID, sid);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID_PWD, sidPwd);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CLIEND, clientId);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CLIEND_PWD, clientPwd);
			if(host != null && host.length() > 0 && port != null && port.length() > 0){
				connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_HOST, host);
				connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_PORT, port);
			}else{
				addHost(connect);
			}
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_TYPE,0);
			connect.putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CHECK_CLIENT,true);
			ConnectionControllerService.getInstance().sendBroadcast(connect);
		}else{
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				//标识init() 没有初始化
				cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
			}
		}
	}
	
	/**
	 * TCP是否连接
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-19 下午4:32:27
	 */
	public static boolean isConnected() {
		return TcpTools.isConnected();
	}
	
	public static String getSDKVersion(){
		return Util.SDK_VERSION.substring(0, Util.SDK_VERSION.length() - 1);
	}
}
