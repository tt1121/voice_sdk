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
	 * TCP�Ƿ�����
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-15 ����11:20:25
	 */
	public static boolean isConnected() {
		return TcpConnection.getConnectionExample().isConnection();
		//return (connection != null) ? connection.isConnection() : false;
	}
	
	/**
	 * �Ͽ�TCP����
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 ����11:22:31
	 */
	public static void tcpDisconnection(){
		AlarmTools.stopAll();
		//UGoManager.getInstance().pub_UGoTcpUpdateState(UGoAPIParam.eUGo_TCP_DISCONNECTED);����������������йҶϵ绰����
		TcpConnection.getConnectionExample().shutdown();
	}
	
	/**
	 * TCP�������
	 * @param host:TCP��ַ
	 * @param port��TCP�˿�
	 * @param cl:�ص�������
	 * @author: xiaozhenhua
	 * @data:2014-4-18 ����2:11:48
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
	 * ����
	 * @param index
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����7:34:58
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
	 * TCP����
	 * @param index
	 * @author: xiaozhenhua
	 * @data:2014-5-19 ����12:29:23
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
	 * ��ȡ��һ�����ӵ�����
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-19 ����11:40:47
	 */
	public static int getNextIndex(){
		return ((UserData.getCurrentTcpIndex() + 1) < UserData
				.getImServiceAddress().size()) ? (UserData.getCurrentTcpIndex() + 1) : 0 ;
	}
	
	
	/**
	 * �������ݰ�(��������/Э��汾/TCP��֤)
	 * @param packet
	 * @author: xiaozhenhua
	 * @data:2014-4-23 ����2:48:06
	 */
	public static void sendPacket(DataPacket packet){
		if(isConnected()){
			TcpConnection.getConnectionExample().sendPacket(packet);
		}else{
			MessageTools.sendMessageIsTimeout(packet);
		}
	}
	
}
