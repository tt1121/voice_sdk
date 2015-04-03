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
 * 消息分发器
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
	 * 消息分发
	 * @param message
	 * @author: xiaozhenhua
	 * @data:2014-4-18 下午5:27:48
	 */
	public synchronized void messageAllocationAction(byte[] message,TcpConnection connection) {
		this.mJsonBodyModel.ResponseBody(jsonBody);// 解析报体
		if (this.mJsonBodyModel.getResult() == 10) {
			// TCP未认证
			//CustomLog.v( "收到TCP未认证消息 ...");
			if (UserData.getAc().length() > 0) {
				PacketTools.tcpLogin();
			}
		}else {
			switch (this.mHeadDataPacket.getType()) {
			case PacketDfineAction.SOCKET_INTERNAL_TYPE:// 客户端服务器内部消息
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
						CustomLog.v("服务器超出负载,连接其它机器 ...");
						AlarmTools.stopAll();
						TcpTools.tcpDisconnection();
						if(ConnectionControllerService.getInstance() != null){
							ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_CS));
						}
						//connection.shutdown();
						//服务器超出负载，连接其它机器
						//TcpUtil.Disconnect();
						//CsaddrUtil.getCsaddr(mContext);
					} else {
						CustomLog.v("JSON:"+toJSON());
						CustomLog.v("SESSION 过期 ...");
						AlarmTools.stopAll();
						TcpTools.tcpDisconnection();
						UserData.saveAc("");
						if(ConnectionControllerService.getInstance() != null){
							ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_LOGIN));
						}
						//TcpUtil.sessionTimeOut(mResponse);
					}
					break;
				case PacketDfineAction.SOCKET_SERVICE_STOP_OP: // 服务器停机消息
					CustomLog.v( "收到服务器停机消息 ...");
					AlarmTools.stopAll();
					TcpTools.tcpDisconnection();
					UserData.saveAc("");
					for(ConnectionListener cl:TcpConnection.getConnectionListener()){
						cl.onConnectionFailed(new UcsReason().setReason(300505).setMsg("the server is down"));
					}
					break;
				case PacketDfineAction.SOCKET_COMPULSORY_OP: // 强制下线
					// 判断是否在通话，有通话侧断开通话
					CustomLog.v( "收到服务器强制下线消息 ...");
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
					//CustomLog.v( "收到TCP建立连接,开始TCP认证 ...");
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
						CustomLog.v( "AC错误 ... ");
					}
					break;
				}
				break;
			case PacketDfineAction.SOCKET_BOROADCAST:// 广播消息
			case PacketDfineAction.SOCKET_STORAGE_TYPE:// 存储转发消息
				if (this.mHeadDataPacket.getAck() >= 0) { // 发送IM 应答
					//CustomLog.v( "收到IM应答:"+this.mHeadDataPacket.getTime());
					MessageTools.sendMessageSuccess(this);
				} else if (this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP) {
					// 离线消息
					//CustomLog.v( "收到IM离线消息:"+this.mHeadDataPacket.getTime());
					MessageTools.parseOfflineReceiverMessagePacket(this);
				} else { // 服务器端推送IM消息
					//CustomLog.v( "收到IM消息:"+this.mHeadDataPacket.getTime());
					MessageTools.parseReceiverMessagePacket(this,true);
				}
				break;
			case PacketDfineAction.SOCKET_REALTIME_TYPE:// 实时消息、状态查询消息 应答
				CustomLog.v( "实时消息 ...");
				if (this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_LOGIN_STATUS_OP) {// 状态查询
					if (this.mHeadDataPacket.getAck() > 0) {// 查询状态回应
						// CallUtil.queryStatus(mContext, this);
					} else {// 向我来查询状态
						// CallUtil.responseStatus(mContext,this.mHeadDataPacket.getFuid(),this.mHeadDataPacket.getSn());
					}
				} else if (this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_INPUT_OP) {// 正在输入
					// BroadcastUtil.receiverInputStatus(mContext,this.mJsonBodyModel.getFromuid(),this.mJsonBodyModel.getType());
				}
				break;
			case PacketDfineAction.SOCKET_SEVERPUSH_TYPE:// 服务器推送业务
				CustomLog.v( "服务器推送业务 ...");
				if(this.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_LOGIN_STATUS_OP){
					CallBackServerPushTools.parseCallBackRequest(this);
				}
				// ServerPushMessageResponse.serverMessageResponse(this);
				break;
			case PacketDfineAction.SOCKET_CALL:// 电话话相关
				if (message != null) {
					int headLength = (short) ((short) (message[0] << 8) + (short) (message[1] & 0xff));
					int length = (short) ((short) (message[2] << 8) + (short) (message[3] & 0xff));
					byte[] header = new byte[headLength];
					int j = 0;
					for (int i = 4; i < (4 + headLength); i++) {
						header[j] = message[i];
						j++;
					}
					// 解报文头
					String head = String.valueOf(BSON.decode(header));
					CustomLog.v( "RECEIVER CONVERT_HEAD:" + head);
					// 数据区
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
				CustomLog.v( "状态实时消息 ...");
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