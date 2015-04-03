package com.yzx.api;

import java.io.Serializable;

public class ConferenceMemberInfo implements Serializable{

	private static final long serialVersionUID = 7765517956193409421L;
	String uid;
	String phone;
	CallType callType;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public CallType getCallType() {
		return callType;
	}

	public void setCallType(CallType callType) {
		this.callType = callType;
	}
}
