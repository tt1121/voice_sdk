package com.yzx.tcp.packet;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tools.DevicesTools;

/**
 * IM消息
 * 
 * @author xiaozhenhua
 * 
 */
public class UcsMessage extends DataPacket {
	protected UcsMessage() {
		this.mHeadDataPacket.setType(PacketDfineAction.SOCKET_STORAGE_TYPE);
		this.mHeadDataPacket.setSn(PacketDfineAction.SN);
		setMsgId(nextID());
		setFormuid(UserData.getClientId());
		setCurrentTime(System.currentTimeMillis()+"");
	}

	public UcsMessage(String json) {
		try {
			JSONObject jsonObj = new JSONObject(json);
			/*if (jsonObj.has(PacketDfineAction.STATUS_SERVER_ID)) {
				setMsgId(jsonObj.getString(PacketDfineAction.STATUS_SERVER_ID));
			}*/
			setMsgId(nextID());
			if (jsonObj.has(PacketDfineAction.FROMUID)) {
				setFormuid(jsonObj.getString(PacketDfineAction.FROMUID));
			}
			setType(0);
			if (jsonObj.has(PacketDfineAction.TYPE)) {
				setExtra_mime(jsonObj.getInt(PacketDfineAction.TYPE));
			}
			if (jsonObj.has(PacketDfineAction.MSG)) {
				setMsg(jsonObj.getString(PacketDfineAction.MSG));
			}
			if(jsonObj.has(PacketDfineAction.FILENAME)){
				setFileName(jsonObj.getString(PacketDfineAction.FILENAME).replace(":", ""));
			}
			if(jsonObj.has(PacketDfineAction.FILESIZE)){
				setFileSize(jsonObj.getString(PacketDfineAction.FILESIZE));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String nextID() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String temp = formatter.format(new Date()).toString();
		return temp.substring(temp.length() - 8, temp.length())
				+ DevicesTools.getDevicesImei(ConnectionControllerService.getInstance())
				+ Math.round(Math.random() * 10000);
	}

	private String touid = "";

	public String getTouid() {
		return touid;
	}

	public void setTouid(String touid) {
		this.touid = touid;
	}

	private String msg = null;

	public String getMsg() {
		return (msg == null) ? "" : msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	private String msgId;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int extra_mime;// 消息类型 Text 1/image 2/audio 3/video 4

	public int getExtra_mime() {
		return extra_mime;
	}

	public void setExtra_mime(int extra_mime) {
		this.extra_mime = extra_mime;
	}

	private int type = 0;// 1:发送 0：接收

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private String formuid;

	public String getFormuid() {
		return formuid;
	}

	public void setFormuid(String formuid) {
		this.formuid = formuid;
	}

	private int isSendSuccess = 0;

	
	private String fileName;
	private String fileSize;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * 0:表示正常      1：表示发送成功     2：表示发送失败
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-13 上午10:10:21
	 */
	public int getSendSuccess() {
		return isSendSuccess;
	}

	public void setSendSuccess(int isSendSuccess) {
		this.isSendSuccess = isSendSuccess;
	}

	private String time;
	
	public String getCurrentTime() {
		return time;
	}
	
	public void setCurrentTime(String time) {
		this.time = time;
	}

	@Override
	public String toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put(PacketDfineAction.FROMUID, getFormuid());
			json.put(PacketDfineAction.TOUID, getTouid());
			json.put(PacketDfineAction.MSG, getMsg());
			json.put(PacketDfineAction.TYPE, getExtra_mime());
			//json.put(PacketDfineAction.STATUS_SERVER_ID, getMsgId());
			//json.put(PacketDfineAction.TIME, getCurrentTime());
			if(getFileName()!= null && getFileName().length() > 0){
				json.put(PacketDfineAction.FILENAME, getFileName());
			}
			if(getFileSize()!= null && getFileSize().length() > 0){
				json.put(PacketDfineAction.FILESIZE, getFileSize());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

}
