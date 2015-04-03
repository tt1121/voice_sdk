package com.yzx.tcp.packet;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yzx.tools.CustomLog;

public class QueryStatus extends DataPacket {

	
	public QueryStatus(){
		this.mHeadDataPacket.setType(PacketDfineAction.SOCKET_USERSTATUS_TYPE);
	}
	
	
	private ArrayList<String> uids;

	public ArrayList<String> getUids() {
		return uids;
	}
	public void setUids(ArrayList<String> uids) {
		if(this.uids == null){
			this.uids = new ArrayList<String>();
		}else{
			this.uids.clear();
		}
		this.uids.addAll(uids);
	}
	
	public void add(String uid){
		if(this.uids == null){
			this.uids = new ArrayList<String>();
		}
		this.uids.add(uid);
	}

	public int type;
	
	public void setType(int type) {
		this.type = type;
		this.mHeadDataPacket.setOp(this.type == 0?PacketDfineAction.SOCKET_INPUT_OP:PacketDfineAction.SOCKET_PING_OP);
	}
	@Override
	public String toJSON() {
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		CustomLog.v("TYPE:"+this.type);
		try {
			for(String uid:this.uids){
				JSONObject json = new JSONObject();
				json.put(this.type == 0 ? "phone" : "uid", uid);
				json.put("pv", 0);
				array.put(json);
			}
			object.put("user", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}

}
