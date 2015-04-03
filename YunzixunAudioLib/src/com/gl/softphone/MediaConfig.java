package com.gl.softphone;

public class MediaConfig extends Object {
	public int ucRealTimeType; // Real time protocol type, 0: RTP 1: PRTP
	// public int ucPhoneProtocol; //Phone protocol, 0: disable 1: enable
	public int ucVideoEnable; // Video module, 0: disable 1: enable
	public int ucEmodelEnable; // Emodel module, 0: disable 1: enable, default 1
	public int ucFecEnable; // Fec module, 0: disable 1: enable, default 0
}
