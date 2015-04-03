package com.yzx.api;

import java.io.Serializable;

public class ConferenceInfo implements Serializable {

	private static final long serialVersionUID = 2296256151959306861L;
	ConferenceState conferenceState;
	String joinConferenceNumber;

	public ConferenceState getConferenceState() {
		return conferenceState != null ? conferenceState : ConferenceState.UNKNOWN;
	}

	public void setConferenceState(ConferenceState conferenceState) {
		this.conferenceState = conferenceState;
	}

	public String getJoinConferenceNumber() {
		return joinConferenceNumber != null && joinConferenceNumber.length() > 0 ? joinConferenceNumber : "";
	}

	public void setJoinConferenceNumber(String joinConferenceNumber) {
		this.joinConferenceNumber = joinConferenceNumber;
	}

}
