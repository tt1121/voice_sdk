package com.yzx.tcp.packet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 报文体数据模型
 * 
 * @author xiaozhenhua
 * 
 */
public class JsonBodyModel {
	private int result = -10;
	private int reason;
	private String msgList = null;
	private String callid = null;
	private String from = null;
	private String fromuid = null;
	private String touid = null;
	private String uid = null;
	private String to = null;
	private int status = 0;
	private int state = 0;
	private int type = 0;
	private int masterBusiness = 0;
	private int slaveBusiness = 0;
	private int id = 0;

	private int randcode = 0; // 随机码

	public void ResponseBody(String jsonBody) {
		if (jsonBody == null || jsonBody.length() == 0)
			return;
		try {
			JSONObject object = new JSONObject(jsonBody);
			if (object.has(PacketDfineAction.RESULT)) {
				this.setResult(object.getInt(PacketDfineAction.RESULT));
			}
			if (object.has(PacketDfineAction.FROM)) {
				this.setFrom(object.getString(PacketDfineAction.FROM));
			}
			if (object.has(PacketDfineAction.FROMUID)) {
				this.setFromuid(object.getString(PacketDfineAction.FROMUID));
			}
			if (object.has(PacketDfineAction.CALLID)) {
				this.setCallid(object.getString(PacketDfineAction.CALLID));
			}
			if (object.has(PacketDfineAction.TO)) {
				this.setTo(object.getString(PacketDfineAction.TO));
			}
			if (object.has(PacketDfineAction.TOUID)) {
				this.setTouid(object.getString(PacketDfineAction.TOUID));
			}
			if (object.has(PacketDfineAction.REASON)) {
				this.setReason(object.getInt(PacketDfineAction.REASON));
			}
			if (object.has(PacketDfineAction.MSG_LIST)) {
				this.setMsgList(object.getString(PacketDfineAction.MSG_LIST));
			}
			if (object.has(PacketDfineAction.UID)) {
				this.setUid(object.getString(PacketDfineAction.UID));
			}
			if (object.has(PacketDfineAction.STATUS)) {
				this.setStatus(object.getInt(PacketDfineAction.STATUS));
			}
			if (object.has(PacketDfineAction.STATE)) {
				this.setState(object.getInt(PacketDfineAction.STATE));
			}
			if (object.has(PacketDfineAction.TYPE)) {
				this.setType(object.getInt(PacketDfineAction.TYPE));
			}
			if (object.has(PacketDfineAction.MASTER_BUSINESS)) {
				this.setMasterBusiness(object
						.getInt(PacketDfineAction.MASTER_BUSINESS));
			}
			if (object.has(PacketDfineAction.SLAVE_BUSINESS)) {
				this.setSlaveBusiness(object
						.getInt(PacketDfineAction.SLAVE_BUSINESS));
			}
			if (object.has(PacketDfineAction.STATUS_SERVER_ID)) {
				this.setId(object.getInt(PacketDfineAction.STATUS_SERVER_ID));
			}

			if (object.has(PacketDfineAction.RANDCODE_ID)) {
				this.setRandcode(object.getInt(PacketDfineAction.RANDCODE_ID));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getMasterBusiness() {
		return masterBusiness;
	}

	public void setMasterBusiness(int masterBusiness) {
		this.masterBusiness = masterBusiness;
	}

	public int getSlaveBusiness() {
		return slaveBusiness;
	}

	public void setSlaveBusiness(int slaveBusiness) {
		this.slaveBusiness = slaveBusiness;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFrom() {
		return from == null?"":from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to == null?"":to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getCallid() {
		return callid == null?"":callid;
	}

	public void setCallid(String callid) {
		this.callid = callid;
	}

	public int getReason() {
		return reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}

	public void setMsgList(String msgList) {
		this.msgList = msgList;
	}

	public String getMsgList() {
		return msgList == null ? "" : msgList;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid == null ? "" : uid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFromuid() {
		return fromuid == null ? "" : fromuid;
	}

	public void setFromuid(String fromuid) {
		this.fromuid = fromuid;
	}

	public String getTouid() {
		return touid == null ? "" : touid;
	}

	public void setTouid(String touid) {
		this.touid = touid;
	}

	public int getRandcode() {
		return randcode;
	}

	public void setRandcode(int randcode) {
		this.randcode = randcode;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
