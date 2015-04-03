package com.yzx.tcp.packet;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseCallBackPacket  extends DataPacket {
	public ResponseCallBackPacket() {
		this.mHeadDataPacket.setType(PacketDfineAction.SOCKET_SEVERPUSH_TYPE);
		this.mHeadDataPacket.setOp(PacketDfineAction.SOCKET_LOGIN_STATUS_OP);
		this.mHeadDataPacket.setAck(1);
	}

	@Override
	public String toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("masterBusiness", 1);
			json.put("slaveBusiness", 1);
			json.put("result", 0);
			json.put("description", "success");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
}
