package com.yzx.tcp.packet;

import org.json.JSONException;
import org.json.JSONObject;

import com.yzx.api.UCSCall;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.TcpTools;
import com.yzx.tools.CustomLog;



public class CallBackServerPushTools {

	private static final int CTGW_HANG_NORM = 0;
	private static final int CTGW_HANG_NOMONEY = 1;
	private static final int CTGW_HANG_MEDIA_TIMEOUT = 2;
	private static final int CTGW_HANG_NO_ROUTE = 3;
	private static final int CTGW_HANG_USER_REJECT = 4;
	private static final int CTGW_HANG_CALL_TIMEOUT = 5;
	private static final int CTGW_HANG_REJECT_OR_TIMEOUT = 6;
	private static final int CTGW_HANG_NET_ERROR = 7;
	private static final int CTGW_HANG_API = 8;
	private static final int CTGW_HANG_AS_REJECT = 9;
	private static final int CTGW_HANG_OTHER = 255;
	
	/**
	 * �����������ز�״̬����
	 * @param resultPacket
	 * @author: �ܻ���
	 */
	public static void parseCallBackRequest(MessageAllocation resultPacket) {
		CustomLog.v( "�յ��ز�״̬���� ...");
		receiverCallBackResponse(resultPacket);
		try {
			JSONObject mJson = new JSONObject(resultPacket.jsonBody);
			if (mJson.length() == 0)
				return;		
			switch (mJson.getInt("status")) {
			case 0://��ͨ�ɹ�
				CustomLog.v( "�����ɹ� ...");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onAnswer(UCSCall.getCurrentCallId());
				}
				break;
			case 1://��ͨʧ��
				CustomLog.v( "��ͨʧ��...");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onDialFailed(UCSCall.getCurrentCallId(), new UcsReason(300243).setMsg("callback failed"));
				}
				break;
			case 2://�Է�����
				CustomLog.v( "�Է�����...");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onAlerting(UCSCall.getCurrentCallId());
				}
				break;
			case 3://�Է��ԹҶ�.
				UcsReason reason= new UcsReason();
				CustomLog.v( "CALLBACK_STATUS:"+mJson);
				switch (mJson.getInt("reason")) {
				case CTGW_HANG_NORM:
					reason= new UcsReason(300213).setMsg("the other party refused");
					break;				
				case CTGW_HANG_NOMONEY:
					reason= new UcsReason(300211).setMsg("no money");
					break;
				case CTGW_HANG_MEDIA_TIMEOUT:
					reason= new UcsReason(300238).setMsg("connect timeout");
					break;
				case CTGW_HANG_NO_ROUTE:
					reason= new UcsReason(300260).setMsg("no route");
					break;
				case CTGW_HANG_USER_REJECT:
					reason= new UcsReason(300261).setMsg("user reject");
					break;
				case CTGW_HANG_CALL_TIMEOUT:
					reason= new UcsReason(300262).setMsg("call timeout");
					break;
				case CTGW_HANG_REJECT_OR_TIMEOUT:
					reason= new UcsReason(300263).setMsg("reject or timeout");
					break;
				case CTGW_HANG_NET_ERROR:
					reason= new UcsReason(300264).setMsg("net error");
					break;
				case CTGW_HANG_API:
					reason= new UcsReason(300265).setMsg("api request");
				case CTGW_HANG_AS_REJECT:
					reason= new UcsReason(300266).setMsg("AS error");
					break;
				case CTGW_HANG_OTHER://δ֪����
					reason= new UcsReason(300243).setMsg("");
					break;
				}
				if(mJson.getInt("reason")>10000){
					reason= new UcsReason(mJson.getInt("reason")).setMsg("");
				}
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onHangUp((UCSCall.getCurrentCallId()), reason);
				}
				CustomLog.v( "CALLBACK_REASON:"+reason.getReason());
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��Ӧ�������յ��µĻز�������Ϣ
	 * @param resultPacket
	 * @author: �ܻ���
	 */
	private static void receiverCallBackResponse(
			MessageAllocation resultPacket) {
		ResponseCallBackPacket packet = (ResponseCallBackPacket)DataPacket.CreateDataPack(PacketDfineAction.SOCKET_SEVERPUSH_TYPE, PacketDfineAction.SOCKET_LOGIN_STATUS_OP);
		TcpTools.sendPacket(packet);
	}

}
