package com.gl.softphone;

public class RtpConfig extends Object {
	public int uiRTPTimeout; // rtp��ʱʱ�� 5 --60s
	public boolean uiFixLowPayload; // ��������־���� ��true�� �ر�:
									// false,����ʹ��˽��Э��ʱ��Ч��������ϲ�(2g)ʱ�򿪣�g729
									// 60ms��silk 5k 60ms��
									// ��������־��ʱ����̬����Ĭ�Ϲر�
}