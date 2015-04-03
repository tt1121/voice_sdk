package com.yzxdemo.action;

import java.util.HashMap;

import com.yzx.api.UCSCall;

public class UIDfineAction {

	

	public static final HashMap<Integer,String> dialState = new HashMap<Integer,String>();
	static{
		//dialState.put(UCSCall.CALL_VOIP_ANSWER, "�����绰");
		dialState.put(UCSCall.CALL_VOIP_NOT_ENOUGH_BALANCE, "����");
		dialState.put(UCSCall.CALL_VOIP_BUSY, "�Է���æ");
		dialState.put(UCSCall.CALL_VOIP_REFUSAL, "�Է��ܾ�");
		dialState.put(UCSCall.CALL_VOIP_NUMBER_ERROR, "���к��벻����");
		dialState.put(UCSCall.CALL_VOIP_REJECT_ACCOUNT_FROZEN, "�����˻�������");
		dialState.put(UCSCall.CALL_VOIP_ACCOUNT_FROZEN, "�˻�������");
		dialState.put(UCSCall.CALL_VOIP_ACCOUNT_EXPIRED, "�����˻�����");
		dialState.put(UCSCall.CALL_VOIP_CALLYOURSELF, "���ܲ����Լ��󶨺���");
		dialState.put(UCSCall.CALL_VOIP_NETWORK_TIMEOUT, "��������ʱ");
		dialState.put(UCSCall.CALL_VOIP_NOT_ANSWER, "�Է�����Ӧ��");
		dialState.put(UCSCall.CALL_VOIP_TRYING_183, "���в�����,תֱ��");
		dialState.put(UCSCall.CALL_VOIP_RINGING_180, "�Է���������");
		dialState.put(UCSCall.CALL_VOIP_SESSION_EXPIRATION, "��Ȩʧ��(TCPδ��֤)");
		dialState.put(UCSCall.CALL_VOIP_ERROR, "����������");
		dialState.put(UCSCall.HUNGUP_NOT_ANSWER, "���з�û��Ӧ��");
		dialState.put(UCSCall.HUNGUP_MYSELF, "�Լ��Ҷϵ绰");
		dialState.put(UCSCall.HUNGUP_OTHER, "�Է��Ҷϵ绰");
		dialState.put(UCSCall.HUNGUP_WHILE_2G, "2G���ܲ�����ѡ�ֱ������Ƶ������ͨ��");
		dialState.put(UCSCall.HUNGUP_REFUSAL, "�Է��ܾ�����");
		dialState.put(UCSCall.HUNGUP_NOT_ENOUGH_BALANCE, "����");
		dialState.put(UCSCall.HUNGUP_MYSELF_REFUSAL, "�Լ��ܾ�����");
		dialState.put(UCSCall.CALL_VIDEO_DOES_NOT_SUPPORT, "���豸��֧����Ƶͨ��");
	}
	
	
	public static final String FROM_NUM_KEY = "fromsernum";
	public static final String TO_NUM_KEY = "tosernum";
	public static final String CALL_UID= "call_uid";
	public static final String CALL_PHONE= "call_phone";
	public static final String RESULT_KEY = "result";
	public static final String REASON_KEY = "reason";
	public static final String ACTION_LOGIN = "com.yzx.login";
	public static final String ACTION_LOGOUT = "com.yzx.logout";
	public static final String ACTION_TCP_LOGIN_RESPONSE = "com.yzx.tcp_login_response";
	public static final String ACTION_TCP_LOGIN_CLIENT_RESPONSE = "com.yzx.tcp_login_client_response";
	public static final String ACTION_SEND_FILE_PROGRESS = "com.yzx.send_file";
	
	public static final String ACTION_DIAL = "com.yzx.dial";
	public static final String ACTION_DIAL_STATE = "com.yzx.dial.state";
	public static final String ACTION_DIAL_HANGUP = "com.yzx.dial.hangup";
	public static final String ACTION_NETWORK_STATE = "com.yzx.network.state";
	public static final String ACTION_ANSWER = "com.yzx.answer";
	public static final String ACTION_CALL_BACK = "com.yzx.callback";
	public static final String ACTION_CALL_TIME = "com.yzx.call_time";
	public static final String ACTION_START_TIME = "com.yzx.start_time";
	
	public static final int LOCATION = 10;		//�Զ�����Ϣ����  λ��
	public static final int FILE = 20;		//�Զ�����Ϣ����  ����
	
}
