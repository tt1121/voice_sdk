package com.yzx.tcp.packet;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 收到新的IM消息
 * @author xiaozhenhua
 *
 */
public class ReceiverMessagePacket {
	private String jsonBody = null;
	private String fromuid = null;
	private String msg = null;
	private String actions = null;
	private String fromname = null;
	private String fromhead = null;
	private int directjump = 0;
	public String getFromname() {
		return fromname;
	}

	public void setFromname(String fromname) {
		this.fromname = fromname;
	}

	public String getFromhead() {
		if (fromhead == null)
			return "";
		return fromhead;
	}

	public void setFromhead(String fromhead) {
		this.fromhead = fromhead;
	}

	public int getDirectjump() {
		return directjump;
	}

	public void setDirectjump(int directjump) {
		this.directjump = directjump;
	}

	private int type = 0;
	private long time = 0;
	
	private boolean haveFlag = false;
	
	public void setHaveFlag(boolean haveFlag) {
		this.haveFlag = haveFlag;
	}
	
	public boolean getHaveFlag() {
		return this.haveFlag;
	}
	
	public String getFromuid() {
		if (fromuid == null)
			return "";
		return fromuid;
	}

	public void setFromuid(String fromuid) {
		this.fromuid = fromuid;
	}

	public void setActions(String mActions) {
		this.actions = mActions;
	}
	
	public String getActions() {
		return (actions == null) ? "" : actions;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return (msg == null) ? "" : msg;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public long getTime() {
		return time;
	}

	public void setJson(String json) {
		if(json != null && json.length() > 0){
			try {
				setJson(new JSONObject(json));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void setJson(JSONObject json) {
		if (json != null) {
			this.jsonBody = json.toString();
			try {
				if (json.has(PacketDfineAction.FROMUID)) {
					this.setFromuid(json.getString(PacketDfineAction.FROMUID));
				}
				if (json.has(PacketDfineAction.MSG)) {
					this.setMsg(json.getString(PacketDfineAction.MSG));
				}
				if (json.has(PacketDfineAction.TYPE)) {
					this.setType(json.getInt(PacketDfineAction.TYPE));
				}
				if (json.has(PacketDfineAction.TIME)) {
					this.setTime(json.getLong(PacketDfineAction.TIME));
				}
				if (json.has(PacketDfineAction.FLAG)) {
					if (json.getInt(PacketDfineAction.FLAG) == 1) {
						this.setHaveFlag(true);
					} else {
						this.setHaveFlag(false);
					}
				} else {
					this.setHaveFlag(false);
				}
				if (json.has(PacketDfineAction.ACTIONS)) {
					this.setActions(json.getString(PacketDfineAction.ACTIONS));
				}
				if (json.has(PacketDfineAction.FROM_NAME)) {
					this.setFromname(json.getString(PacketDfineAction.FROM_NAME));
				}
				if (json.has(PacketDfineAction.FROM_HEAD)) {
					this.setFromhead(json.getString(PacketDfineAction.FROM_HEAD));
				}
				if (json.has(PacketDfineAction.DIRECT_JUMP)) {
					this.setDirectjump(json.getInt(PacketDfineAction.DIRECT_JUMP));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public String toJson() {
		if (jsonBody == null)
			return "";
		return jsonBody;
	}
}
