package com.yzx.tcp.packet;


import org.bson.BSON;

import android.content.Intent;

import com.gl.softphone.UGoManager;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.service.ServiceLoginTools;
import com.yzx.tcp.AlarmTools;
import com.yzx.tcp.PacketTools;
import com.yzx.tcp.TcpConnection;
import com.yzx.tcp.TcpTools;
import com.yzx.tools.CustomLog;

/**
 * ��Ϣ�ַ���
 * 
 * @author xiaozhenhua
 * 
 */
public class MessageAllocation extends DataPacket {
	public String jsonBody = "";
	public JsonBodyModel mJsonBodyModel;

	public MessageAllocation() {
		mJsonBodyModel = new JsonBodyModel();
	}

	@Override
	public String toJSON() {
		return jsonBody.toString();
	}

	/**
	 * ��Ϣ�ַ�
	 * @param message
	 * @author: xiaozhenhua
	 * @data:2014-4-18 ����5:27:48
	 */
	public synchronized void messageAllocationAction(byte[] message,TcpConnection connection) {
		this.mJsonBodyModel.ResponseBody(jsonBody);// ��������
		if (this.mJsonBodyModel.getResult() == 10) {
			// TCPδ��֤
			//CustomLog.v( "�յ�TCPδ��֤��Ϣ ...");
			if (UserData.getAc().length() > 0) {
				PacketTools.tcpLogin();
			}
		}else {
			switch (this.mHeadDataPacket.getType()) {
			case PacketDfineAction.SOCKET_INTERNAL_TYPE:// �ͻ��˷������ڲ���Ϣ
				switch (this.mHeadDataPacket.getOp()) {
				case PacketDfineAction.SOCKET_LOGIN_STATUS_OP:
					if (this.mJsonBodyModel.getResult() == 0) {
						CustomLog.v( "SOCKET CONNECTION SUCCESS ... ");
						AlarmTools.startAlarm();
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							cl.onConnectionSuccessful();
						}
						if(TcpConnection.getConfigListner() != null){
							TcpConnection.getConfigListner().onConnectionSuccessful();
						}
					} else if (this.mJsonBodyModel.getResult() == 5 || this.mJsonBodyModel.getResult() == 6 ||  this.mJsonBodyModel.getResult() == 7) {
						CustomLog.v("��������������,������������ ...");
						AlarmTools.stopAll();
						TcpTools.tcpDisconnection();
						if(ConnectionControllerService.getInstance() != null){
							ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_CS));
						}
						//connection.shutdown();
						//�������������أ�������������
						//TcpUtil.Disconnect();
						//CsaddrUtil.getCsaddr(mContext);
					} else {
						CustomLog.v("JSON:"+toJSON());
						CustomLog.v("SESSION ���� ...");
						AlarmTools.stopAll();
						TcpTools.tcpDisconnection();
						UserData.saveAc("");
						if(ConnectionControllerService.getInstance() != null){
							ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_LOGIN));
						}
						//TcpUtil.sessionTimeOut(mResponse);
					}
					break;
				case PacketDfineAction.SOCKET_SERVICE_STOP_OP: // ������ͣ����Ϣ
					CustomLog.v( "�յ�������ͣ����Ϣ ...");
					AlarmTools.stopAll();
					TcpTools.tcpDisconnection();
					UserData.saveAc("");
					for(ConnectionListener cl:TcpConnection.getConnectionListener()){
						cl.onConnectionFailed(new UcsReason().setReason(300505).setMsg("the server is down"));
					}
					break;
				case PacketDfineAction.SOCKET_COMPULSORY_OP: // ǿ������
					// �ж��Ƿ���ͨ������ͨ����Ͽ�ͨ��
					CustomLog.v( "�յ�������ǿ��������Ϣ ...");
					AlarmTools.stopAll();
					/*if(connection != null){
						connection.shutdown(false);
					}*/
					UserData.saveAc("");
					ConnectionControllerService.stopCurrentService();
					for(ConnectionListener cl:TcpConnection.getConnectionListener()){
						cl.onConnectionFailed(new UcsReason().setReason(300207).setMsg("forced offline server"));
					}
					break;
				case PacketDfineAction.SOCKET_CONNET_OP:
					LoginPacket.randCode = this.mJsonBodyModel.getRandcode();
					//CustomLog.v( "�յ�TCP��������,��ʼTCP��֤ ...");
					if (UserData.getAc().length() > 0) {
						PacketTools.tcpLogin();
					}else{
						if(UserData.getLoginType() == 0){
							CustomLog.v( "ACCOUNT_SID");
							ServiceLoginTools.loginAction(UserData.getAccountSid(), UserData.getAccountToken(), UserData.getClientId(), UserData.getClientPwd(),null);
						}else{
							CustomLog.v( "ACCOUNT_TOKEN");
							ServiceLoginTools.loginAction(UserData.getAccountToken(), null);
						}
						CustomLog.v( "AC���� ... ");
					}
					break;
				}
				break;
			case PacketDfineAction.SOCKET_BOROADCAST:// �㲥��Ϣ
			case PacketDfineAction.SOCKET_STORAGE_TYPE:// �洢ת����Ϣ
				if (this.mHeadDataPacket.getAck() >= 0) { // ����IM Ӧ��
					//CustomLog.v( "�յ�IMӦ��:"+this.mHeadDataPacket.getTime());
					MessageTools.sendMessageSuccess(this);
				} else if (this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP) {
					// ������Ϣ
					//CustomLog.v( "�յ�IM������Ϣ:"+this.mHeadDataPacket.getTime());
					MessageTools.parseOfflineReceiverMessagePacket(this);
				} else { // ������������IM��Ϣ
					//CustomLog.v( "�յ�IM��Ϣ:"+this.mHeadDataPacket.getTime());
					MessageTools.parseReceiverMessagePacket(this,true);
				}
				break;
			case PacketDfineAction.SOCKET_REALTIME_TYPE:// ʵʱ��Ϣ��״̬��ѯ��Ϣ Ӧ��
				CustomLog.v( "ʵʱ��Ϣ ...");
				if (this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_LOGIN_STATUS_OP) {// ״̬��ѯ
					if (this.mHeadDataPacket.getAck() > 0) {// ��ѯ״̬��Ӧ
						// CallUtil.queryStatus(mContext, this);
					} else {// ��������ѯ״̬
						// CallUtil.responseStatus(mContext,this.mHeadDataPacket.getFuid(),this.mHeadDataPacket.getSn());
					}
				} else if (this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_INPUT_OP) {// ��������
					// BroadcastUtil.receiverInputStatus(mContext,this.mJsonBodyModel.getFromuid(),this.mJsonBodyModel.getType());
				}
				break;
			case PacketDfineAction.SOCKET_SEVERPUSH_TYPE:// ����������ҵ��
				CustomLog.v( "����������ҵ�� ...");
				if(this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_LOGIN_STATUS_OP){
					CallBackServerPushTools.parseCallBackRequest(this);
				}
				// ServerPushMessageResponse.serverMessageResponse(this);
				break;
			case PacketDfineAction.SOCKET_CALL:// �绰�����
				if (message != null) {
					int headLength = (short) ((short) (message[0] << 8) + (short) (message[1] & 0xff));
					int length = (short) ((short) (message[2] << 8) + (short) (message[3] & 0xff));
					byte[] header = new byte[headLength];
					int j = 0;
					for (int i = 4; i < (4 + headLength); i++) {
						header[j] = message[i];
						j++;
					}
					// �ⱨ��ͷ
					String head = String.valueOf(BSON.decode(header));
					CustomLog.v( "RECEIVER CONVERT_HEAD:" + head);
					// ������
					byte[] data = new byte[length];
					j = 0;
					for (int i = (4 + headLength); i < message.length; i++) {
						data[j] = message[i];
						j++;
					}
					CustomLog.v( "RECEIVER CONVERT_PACKAGE:" + new String(data));
					CustomLog.v( "UPDATE TCP MSG ... ");
					UGoManager.getInstance().pub_UGoTcpRecvMsg(message.length, message);
				}
				break;
			case PacketDfineAction.SOCKET_USERSTATUS_TYPE:
				CustomLog.v( "״̬ʵʱ��Ϣ ...");
				if(this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_PING_OP
					|| this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_INPUT_OP){
					MessageTools.parseStatusMessage(this);
				}
			default:
				break;
			}
		}
	}

}