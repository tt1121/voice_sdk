package com.yzx.service;

import com.yzx.tcp.AlarmTools;
import com.yzx.tcp.TcpTools;
import com.yzx.tcp.packet.DataPacket;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tcp.packet.PingPacket;
import com.yzx.tools.CustomLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		sendPacket();
	}
	
	
	/**
	 * ·¢ËÍÐÄÌø
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-18 ÏÂÎç5:31:51
	 */
	private static void sendPacket(){
		CustomLog.v("SEND PING");
		PingPacket ping = (PingPacket) DataPacket.CreateDataPack(PacketDfineAction.SOCKET_INTERNAL_TYPE,PacketDfineAction.SOCKET_PING_OP);
		AlarmTools.startBackTcpPing();
		TcpTools.sendPacket(ping);
	}

}
