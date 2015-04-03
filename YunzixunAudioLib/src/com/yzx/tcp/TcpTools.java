package com.yzx.tcp;

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tcp.packet.DataPacket;
import com.yzx.tcp.packet.MessageTools;
import com.yzx.tools.CustomLog;

public class TcpTools {
	
	//private static TcpConnection connection;
	
	
	/**
	 * TCP是否连接
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:20:25
	 */
	public static boolean isConnected() {
		return TcpConnection.getConnectionExample().isConnection();
		//return (connection != null) ? connection.isConnection() : false;
	}
	
	/**
	 * 断开TCP连接
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:22:31
	 */
	public static void tcpDisconnection(){
		AlarmTools.stopAll();
		//UGoManager.getInstance().pub_UGoTcpUpdateState(UGoAPIParam.eUGo_TCP_DISCONNECTED);传给组件后，组件会进行挂断电话操作
		TcpConnection.getConnectionExample().shutdown();
	}
	
	/**
	 * TCP连接入口
	 * @param host:TCP地址
	 * @param port：TCP端口
	 * @param cl:回调监听器
	 * @author: xiaozhenhua
	 * @data:2014-4-18 下午2:11:48
	 */
	public static void tcpConnection(){
		//CustomLog.v("TCP NETWORK CONNECT:"+UserData.isCurrentConnect(ConnectionControllerService.getInstance()));
		//if(UserData.isCurrentConnect(ConnectionControllerService.getInstance())){
			//UserData.saveCurrentConnect(ConnectionControllerService.getInstance(), false);
			if(isConnected()){
				tcpDisconnection();
			}
			connection(UserData.getCurrentTcpIndex());
		//}
	}

	
	/**
	 * 重联
	 * @param index
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午7:34:58
	 */
	public static void reTcpConnection(){
		//CustomLog.v("RE TCP NETWORK CONNECT:"+UserData.isCurrentConnect(ConnectionControllerService.getInstance()));
		//if(UserData.isCurrentConnect(ConnectionControllerService.getInstance())){
		//	UserData.saveCurrentConnect(ConnectionControllerService.getInstance(), false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					CustomLog.v("TCP RECONNECTION");
					tcpDisconnection();
					connection(TcpTools.getNextIndex());
				}
			}).start();
		//}
	}
	
	
	/**
	 * TCP联接
	 * @param index
	 * @author: xiaozhenhua
	 * @data:2014-5-19 下午12:29:23
	 */
	private static void connection(int index){
		//CustomLog.v("CURRENT_SIZE:"+UserData.getImServiceAddress().size()+"      CURRENT_INDEX:"+index);
		if(UserData.getImServiceAddress().size() > index){
			//CustomLog.v("CURRENT_ADDRESS:"+UserData.getImServiceAddress().get(index));
			String[] address = UserData.getImServiceAddress().get(index).split(":");
			UserData.saveCurrentTcpIndex(index);
			if(address != null && address.length == 2 && address[0].length() > 0 && address[1].length() > 0){
				TcpConnection.getConnectionExample().connection(address[0], Integer.parseInt(address[1]));
				if(isConnected()){
					UGoManager.getInstance().pub_UGoTcpUpdateState(UGoAPIParam.eUGo_TCP_CONNECTED);
					PacketTools.sendVersion();
				}
			}else{
				for(ConnectionListener notifyCl:TcpConnection.getConnectionListener()){
					notifyCl.onConnectionFailed(new UcsReason().setReason(300002).setMsg("service address or port length is 0"));
				}
			}
		}else{
			for(ConnectionListener notifyCl:TcpConnection.getConnectionListener()){
				notifyCl.onConnectionFailed(new UcsReason().setReason(300002).setMsg("service address can not empty"));
			}
		}
	}
	
	/**
	 * 获取下一个联接的索引
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午11:40:47
	 */
	public static int getNextIndex(){
		return ((UserData.getCurrentTcpIndex() + 1) < UserData
				.getImServiceAddress().size()) ? (UserData.getCurrentTcpIndex() + 1) : 0 ;
	}
	
	
	/**
	 * 发送数据包(发送心跳/协议版本/TCP认证)
	 * @param packet
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午2:48:06
	 */
	public static void sendPacket(DataPacket packet){
		if(isConnected()){
			TcpConnection.getConnectionExample().sendPacket(packet);
		}else{
			MessageTools.sendMessageIsTimeout(packet);
		}
	}
	
}
