package com.gl.softphone;

public class RtpConfig extends Object {
	public int uiRTPTimeout; // rtp超时时间 5 --60s
	public boolean uiFixLowPayload; // 低码流标志，打开 ：true， 关闭:
									// false,仅在使用私有协议时有效，当网络较差(2g)时打开（g729
									// 60ms，silk 5k 60ms）
									// 低码流标志打开时，动态负载默认关闭
}