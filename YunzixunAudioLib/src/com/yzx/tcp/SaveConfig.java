package com.yzx.tcp;

import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tools.CustomLog;

public class SaveConfig implements ConnectionListener {
	
	private static SaveConfig sc;
	public static SaveConfig getInstance(){
		if(sc == null){
			sc = new SaveConfig();
		}
		return sc;
	}
	
	private ConnectConfig cfg;
	public SaveConfig setConfig(ConnectConfig connectCfg){
		cfg = connectCfg;
		return getInstance();
	}

	@Override
	public void onConnectionSuccessful() {
		if( cfg != null){
			if(cfg.getAccountToken().length() > 0){
				UserData.saveAccountToken(cfg.getAccountToken());
			}
			if(cfg.getAccountSid().length() > 0){
				UserData.saveAccountSid(cfg.getAccountSid());
			}
			if(cfg.getClientId().length() > 0){
				UserData.saveClientId(cfg.getClientId());
			}
			if(cfg.getClientPwd().length() > 0){
				UserData.saveClientPwd(cfg.getClientPwd());
			}
			UserData.saveLoginLastTime(System.currentTimeMillis());
			//CustomLog.v("SAVE CURRENT_PREFERENCE:"+UserData.getClientId());
		}
	}

	@Override
	public void onConnectionFailed(UcsReason reason) {

	}

}
