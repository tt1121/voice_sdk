package com.yzx.service;

import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.AlarmTools;
import com.yzx.tcp.TcpConnection;
import com.yzx.tcp.TcpTools;
import com.yzx.tools.CustomLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MsgBackReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		CustomLog.v( "–ƒÃ¯÷ÿ¡¨...");
		AlarmTools.stopAll();
		for(ConnectionListener cl:TcpConnection.getConnectionListener()){
			cl.onConnectionFailed(new UcsReason().setReason(300501));
		}
	}
}
