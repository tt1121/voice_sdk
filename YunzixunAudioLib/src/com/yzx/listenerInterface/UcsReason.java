package com.yzx.listenerInterface;

public class UcsReason {

	private int reason;
	private String msg;

	public int getReason() {
		return reason;
	}

	public UcsReason(){
		
	}
	public UcsReason(int reason){
		this.reason = reason;
	}
	
	public UcsReason setReason(int reason) {
		this.reason = reason;
		return this;
	}

	public String getMsg() {
		return (msg != null && msg.length() > 0) ? msg : "";
	}

	public UcsReason setMsg(String msg) {
		this.msg = msg;
		return this;
	}

}
