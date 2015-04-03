package com.yzx.tcp.packet;

import org.json.JSONException;
import org.json.JSONObject;

import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tools.NetWorkTools;


/**
 * TCP认证包(登录包)
 * @author xiaozhenhua
 *
 */
public class LoginPacket extends DataPacket {
	public static int randCode =  0;
	public LoginPacket() {
		this.mHeadDataPacket.setType(PacketDfineAction.SOCKET_INTERNAL_TYPE);
		this.mHeadDataPacket.setOp(PacketDfineAction.SOCKET_LOGIN_STATUS_OP);
		this.mHeadDataPacket.setEnc(0x01);
	}
   
	@Override
	public String toJSON() {
		//int netmodel = 1;//CpuUtil.isCpuFreq()?NetUtil.EDGE_NETWORK:NetUtil.getSelfNetworkType(MainApplocation.getInstance().getApplicationContext());
		int netmodel = 2;
		if(ConnectionControllerService.getInstance() != null){
			netmodel = NetWorkTools.getCurrentNetWorkType(ConnectionControllerService.getInstance().getApplicationContext());
		}
		//CustomLog.v("netmodel=="+netmodel);
		int mNetModel = 0;
		switch (netmodel) {
		case 1:
			mNetModel = 1;
			break;
		case 2:
			mNetModel = 2;
			break;
		case 3:
			mNetModel = 4;
			break;
		default:
			break;
		}
		JSONObject json = new JSONObject();
		try {
			json.put("im_ssid", UserData.getAc());
			json.put("version", UserData.getVersionName(ConnectionControllerService.getInstance()));
			json.put("netmode", mNetModel);
			json.put("randcode", randCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

   
}
