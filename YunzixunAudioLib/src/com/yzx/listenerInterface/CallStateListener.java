package com.yzx.listenerInterface;

import java.util.ArrayList;

import com.yzx.api.ConferenceInfo;

/**
 * �绰״̬������
 * 
 * @author xiaozhenhua
 * 
 */
public interface CallStateListener {

	/**
	 * �ز����гɹ�
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-23 ����4:59:50
	 */
	public void onCallBackSuccess();

	/**
	 * VOIP����״̬
	 * 
	 * @param reason
	 *            :�ο�VOIP����״̬��
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����12:50:37
	 */
	public void onDialFailed(String callId, UcsReason reason);

	/**
	 * ��������
	 * 
	 * @param phone
	 *            :����ĵ绰�ź������cliend�˻�(voipaccount)
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����12:51:16
	 */
	public void onIncomingCall(String callId, String callType,
			String callerNumber, String nickName, String userdata);

	/**
	 * �Ҷ��¼��ص�
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����2:36:03
	 */
	public void onHangUp(String callId, UcsReason reason);

	/**
	 * ����������
	 * 
	 * @param callId
	 * @author: xiaozhenhua
	 * @data:2014-4-29 ����2:30:08
	 */
	public void onAlerting(String callId);

	/**
	 * �����¼��ص�
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����2:34:49
	 */
	public void onAnswer(String callId);

	/**
	 * �෽ͨ������ص�����
	 * @param callId
	 * @param callType
	 * @param callerNumber
	 * @param nickName
	 * @param userdata
	 * @author: xiaozhenhua
	 * @data:2015-3-25 ����3:07:05
	 */
	public void onChatRoomIncomingCall(String callId, String callType,
			String callerNumber, String nickName, String userdata);
	/**
	 * ����֪ͨ�ص��¼�
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����2:47:28
	 */
	public void onChatRoomState(String callId, ArrayList<ConferenceInfo> states);
	@Deprecated
	public void onConferenceState(String callId, ArrayList<ConferenceInfo> states);
	/**
	 * ����ģʽת��֪ͨ�ص��¼�
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����2:47:28
	 */
	public void onChatRoomModeConvert(String callId);
	@Deprecated
	public void onConferenceModeConvert(String callId);
	/**
	 * DTMF�ص�
	 * 
	 * @param dtmf
	 * @author: xiaozhenhua
	 * @data:2015-3-16 ����11:33:44
	 */
	public void onDTMF(int dtmfCode);

	/**
	 * ��ͨ�ص��¼�
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����2:47:28
	 */
	// public void singlePass(int reason);

	/**
	 * ����״̬�ϱ�
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 ����2:48:59
	 */
	// public void onNetworkState(int reason);
}
