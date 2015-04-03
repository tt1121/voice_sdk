package com.yzx.tcp;

import com.yzx.tcp.packet.DataPacket;
import com.yzx.tcp.packet.LoginPacket;
import com.yzx.tcp.packet.LogoutPacket;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tcp.packet.QueryStatus;
import com.yzx.tcp.packet.VersionPacket;
import com.yzx.tools.CustomLog;

/**
 * 
 * @author xiaozhenhua
 * 
 */
public class PacketTools {

	/**
	 * TCP认证(session认证)
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:30:49
	 */
	public static void tcpLogin() {
		if (TcpTools.isConnected()) {
			LoginPacket loginPacket = (LoginPacket) DataPacket.CreateDataPack(PacketDfineAction.SOCKET_INTERNAL_TYPE, PacketDfineAction.SOCKET_LOGIN_STATUS_OP);
			//CustomLog.v("LOGIN_JSON:"+loginPacket.toJSON());
			TcpTools.sendPacket(loginPacket);
		}else{
			CustomLog.v("TCP认证没有检测到连接 ... ");
		}
	}

	/**
	 * TCP退出
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-18 上午10:06:07
	 */
	public static void tcpLogout(){
		if (TcpTools.isConnected()) {
			LogoutPacket logoutPacket = (LogoutPacket) DataPacket.CreateDataPack(PacketDfineAction.SOCKET_INTERNAL_TYPE, PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP);
			TcpTools.sendPacket(logoutPacket);
		}
	}
	
	/**
	 * 终端版本认证
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:28:45
	 */
	public static void sendVersion() {
		if (TcpTools.isConnected()) {
			VersionPacket versionPacket = (VersionPacket) DataPacket.CreateDataPack(PacketDfineAction.SOCKET_INTERNAL_TYPE,PacketDfineAction.SOCKET_VERSION);
			TcpTools.sendPacket(versionPacket);
		}
	}
	
	public static void queryStatus(int type,String uid){
		QueryStatus status = (QueryStatus)DataPacket.CreateDataPack(PacketDfineAction.SOCKET_USERSTATUS_TYPE, PacketDfineAction.SOCKET_PING_OP);
		status.setType(type);
		String[] uids = uid.split(",");
		for(String u:uids){
			status.add(u);
		}
		//CustomLog.v("QUERY:"+status.toJSON());
		TcpTools.sendPacket(status);
	}
}

