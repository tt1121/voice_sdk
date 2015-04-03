package com.yzx.tcp.packet;

public class UcsStatus {

	private String uid;
	private boolean isOnline;
	private String timestamp;// 下线时间戳
	private int netmode;// 网络类型 0x01(wifi),0x02(2G),0x04(3G)
	private int pv;// 0x01(PC),0x02(ios),0x04(android),0x00(all）

	public String getUid() {
		return uid;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getNetmode() {
		return netmode;
	}

	public void setNetmode(int netmode) {
		this.netmode = netmode;
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}
}
