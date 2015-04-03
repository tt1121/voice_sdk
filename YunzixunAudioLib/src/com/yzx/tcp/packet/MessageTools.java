package com.yzx.tcp.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bson.BSON;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.yzx.api.UCSMessage;
import com.yzx.listenerInterface.MessageListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tcp.TcpTools;
import com.yzx.tools.CustomLog;
import com.yzx.tools.ErrorCodeReportTools;
import com.yzx.tools.RC4Tools;

/**
 * ��Ϣ������
 * @author xiaozhenhua
 *
 */
public class MessageTools {

	public static ArrayList<MessageListener> imListenerList = new ArrayList<MessageListener>();

	public static void addIMListener(MessageListener imListener) {
		imListenerList.add(imListener);
	}

	public static ArrayList<MessageListener> getMessageListenerList(){
		return imListenerList;
	}
	public static void removeMessageListener(MessageListener msgListener){
		imListenerList.remove(msgListener);
	}
	
	//------------------IM���Ͷ��кͽ��ն���
	public static HashMap<Timer,DataPacket> sendMessageMap = new HashMap<Timer,DataPacket>(5);
	public static HashMap<Timer,Boolean> sendMessageMapState = new HashMap<Timer,Boolean>(5);
	public static HashMap<String,String> MessageMapFilePaht = new HashMap<String,String>(5);
	
	/**
	 * ��Ӧ�������յ��µ�IM��Ϣ
	 * @param resultPacket
	 * @param result
	 * @author: xiaozhenhua
	 * @data:2014-5-14 ����4:41:39
	 */
	private static void receiverMessageResponse(MessageAllocation resultPacket,int result) {
		if(result == 1){//����Ӧ��
			ResponseReceiverMessagePacket packet = (ResponseReceiverMessagePacket) DataPacket.CreateDataPack(PacketDfineAction.SOCKET_STORAGE_TYPE, PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP);
			packet.setServer(resultPacket.headJson);
			packet.mHeadDataPacket.setOp(PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP);
			packet.mHeadDataPacket.setType(resultPacket.mHeadDataPacket.getType());
			packet.mHeadDataPacket.setSn(resultPacket.mHeadDataPacket.getSn());
			packet.mHeadDataPacket.setTuid(resultPacket.mHeadDataPacket.getFuid());
			packet.mHeadDataPacket.setAck(1);
			CustomLog.v( "�ն�Ӧ��������Ϣ ... ");
			TcpTools.sendPacket(packet);
		}else{//IM Ӧ��
			ResponseReceiverMessagePacket packet = (ResponseReceiverMessagePacket) DataPacket.CreateDataPack(PacketDfineAction.SOCKET_STORAGE_TYPE, PacketDfineAction.SOCKET_REEIVER_OP);
			packet.setServer(resultPacket.headJson);
			packet.mHeadDataPacket.setType(resultPacket.mHeadDataPacket.getType());
			packet.mHeadDataPacket.setSn(resultPacket.mHeadDataPacket.getSn());
			packet.mHeadDataPacket.setTuid(resultPacket.mHeadDataPacket.getFuid());
			packet.mHeadDataPacket.setAck(1);
			//CustomLog.v( "�ն�Ӧ����Ϣ ... ");
			TcpTools.sendPacket(packet);
		}
		
	}
	
	public static void parseOfflineReceiverMessagePacket(MessageAllocation resultPacket){
		receiverMessageResponse(resultPacket, 1);
		if (resultPacket.mJsonBodyModel.getMsgList().length() == 0) {
			return;
		} else {
			try {
				JSONArray array = new JSONArray(resultPacket.mJsonBodyModel.getMsgList());
				if (array.length() == 0)
					return;		
				for (int i = 0; i < array.length(); i++) {
					MessageAllocation  cbPack = new MessageAllocation();
					String indexJson = array.getString(i);
					byte[] mbyte = Base64.decode(indexJson, Base64.DEFAULT);

					cbPack.headLength = (short) ((short) (mbyte[0] << 8) + (short) (mbyte[1] & 0xff));
					cbPack.length = (short) ((short) (mbyte[2] << 8) + (short) (mbyte[3] & 0xff));

					if (cbPack.headLength > 0) {// ������ͷ
						byte[] head = new byte[cbPack.headLength];
						for (int j = 0; j < cbPack.headLength; j++) {
							if (mbyte.length > j + 4) {
								head[j] = mbyte[j + 4];
							}
						}
						try {
							cbPack.setHead(BSON.decode(head));
							//CustomLog.v( "buf1==" + cbPack.mHeadDataPacket.toString());
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						head = null;
					}
					if (cbPack.length > 0) {
						byte[] body = new byte[cbPack.length];
						for (int j = 0; j < cbPack.length; j++) {
							if (mbyte.length > (j + cbPack.headLength + 4)) {
								body[j] = mbyte[j + cbPack.headLength + 4];
							}
						}
						try {
							if (cbPack.mHeadDataPacket.enc == (byte) 0x01) { // �Ƿ����
								cbPack.jsonBody = RC4Tools.decry_RC4(body);
							} else {
								cbPack.jsonBody = new String(body);
							}
						} catch (Exception e) {
							e.printStackTrace();
							CustomLog.v( "PacketReader RC4 ����:" + e.toString());
						}
						//body = null;
					}
					if (resultPacket.mHeadDataPacket.getResend() > 0) {// ������߰�Ϊ�ط����Ͱ��������Ϣ�����Ϊ�ط���Ϣ
						cbPack.mHeadDataPacket.setResend(1);
					}
					parseReceiverMessagePacket(cbPack, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * �����յ�����Ϣ
	 * @param resultPacket
	 * @author: xiaozhenhua
	 * @data:2014-5-14 ����5:03:17
	 */
	public static void parseReceiverMessagePacket(MessageAllocation resultPacket,boolean isOnLine){
		if(isOnLine){
			receiverMessageResponse(resultPacket, 0);
		}
		try {
			JSONObject mJsonBject = new JSONObject(resultPacket.jsonBody);
			long fuid = mJsonBject.getLong("fromuid");
			ReceiverMessagePacket packet = new ReceiverMessagePacket();
			packet.setJson(mJsonBject);
			packet.setTime(resultPacket.mHeadDataPacket.getTime());
			packet.setFromuid(String.valueOf(resultPacket.mHeadDataPacket.getuid(fuid)));			
			if (!UCSMessage.MSG_TYPE.keySet().contains(packet.getType())){
				return;
			}
			if(resultPacket.mHeadDataPacket.getResend() > 0){ //�Ƿ��ط���Ϣ
				packet.setHaveFlag(true); // �ظ�
			}else{
				packet.setHaveFlag(false); //���ظ�
			}
			//CustomLog.v("�յ���Ϣ:"+packet.toJson());
			for(MessageListener messageListener:MessageTools.getMessageListenerList()){
				UcsMessage message = new UcsMessage(packet.toJson());
				message.setCurrentTime((resultPacket.mHeadDataPacket.getTime()*1000)+"");
				messageListener.onReceiveUcsMessage(new UcsReason(0),message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * ��Ϣ��ʱ����������Ϣ�Ƿ�ʧ��
	 * @param packet
	 * @author: xiaozhenhua
	 * @data:2014-5-14 ����6:55:13
	 */
	public synchronized static void sendMessageIsTimeout(DataPacket packet){
		if(isMessage(packet)){
			// IM ���ҵ�� ���뷢�Ͷ���
			final Timer callMessageTimer = new Timer();
			callMessageTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					//�����Ϣ��ʱ��30��֮���ȡ������Ϣ�����ڣ�����Ϊ��Ϣ����ʧ��
					DataPacket dataPack = MessageTools.sendMessageMap.get(callMessageTimer);
					if(dataPack != null){
						for(MessageListener messageListener:MessageTools.getMessageListenerList()){
							UcsMessage sendMessage = (UcsMessage)dataPack;
							sendMessage.setType(1);
							sendMessage.setSendSuccess(2);
							messageListener.onSendUcsMessage(null, sendMessage);
						}
					}
					CustomLog.v("��ʱ���Զ��Ƴ���Ϣ ... ");
					MessageTools.sendMessageMap.remove(callMessageTimer);
					MessageTools.sendMessageMapState.remove(callMessageTimer);
				}
			}, PacketDfineAction.IM_TEXT_TIME);
			MessageTools.sendMessageMap.put(callMessageTimer, packet);
			MessageTools.sendMessageMapState.put(callMessageTimer,false);
		}
		returnMessage(packet);
	}
	
	/**
	 * �Ƿ���ͨIM��Ϣ
	 * @param packet
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-13 ����9:57:27
	 */
	private static boolean isMessage(DataPacket packet){
		return packet.mHeadDataPacket.getType() == PacketDfineAction.SOCKET_STORAGE_TYPE && packet.mHeadDataPacket.getAck() == -1;
	}
	
	/**
	 * ����Ҫ���͵���Ϣ����
	 * @param packet
	 * @author: xiaozhenhua
	 * @data:2014-6-13 ����9:59:08
	 */
	private static void returnMessage(DataPacket packet){
		if(isMessage(packet)){
			for(MessageListener messageListener:MessageTools.getMessageListenerList()){
				UcsMessage sendMessage = (UcsMessage)packet;
				sendMessage.setType(1);
				sendMessage.setSendSuccess(0);
				if(MessageMapFilePaht.keySet().contains(sendMessage.getMsgId())){
					String filePaht = MessageMapFilePaht.get(sendMessage.getMsgId());
					sendMessage.setMsg(filePaht);
					MessageMapFilePaht.remove(sendMessage.getMsgId());
				}
				messageListener.onSendUcsMessage(null, sendMessage);
			}
		}
	}
	
	/**
	 * ���ͳɹ�
	 * @param packet
	 * @author: xiaozhenhua
	 * @data:2014-5-20 ����12:05:12
	 */
	public synchronized static void sendMessageSuccess(MessageAllocation packet){
		Timer currentTime = null;
		for (Timer timer : MessageTools.sendMessageMapState.keySet()) {
			int sn = MessageTools.sendMessageMap.get(timer).mHeadDataPacket.getSn();
			if (sn == packet.mHeadDataPacket.getSn()) {
				MessageTools.sendMessageMapState.put(timer, true);
				for(Timer msgTimer : MessageTools.sendMessageMap.keySet()){
					if(timer == msgTimer){
						for(MessageListener messageListener:MessageTools.getMessageListenerList()){
							UcsMessage sendMessage = (UcsMessage)MessageTools.sendMessageMap.get(timer);
							sendMessage.setType(1);
							sendMessage.setSendSuccess(1);
							messageListener.onSendUcsMessage(null, sendMessage);
						}
						//CustomLog.v("�յ���Ӧ,ֹͣ��Ϣ��ʱ�� ... ");
						msgTimer.cancel();
					}
				}
				currentTime = timer;
				break;
			}
		}
		if(currentTime != null){
			//CustomLog.v("�յ���������Ӧ�Ƴ���Ϣ ... ");
			MessageTools.sendMessageMap.remove(currentTime);
			MessageTools.sendMessageMapState.remove(currentTime);
			currentTime.cancel();
		}
	}
	
	
	public synchronized static void parseStatusMessage(MessageAllocation resultPacket){
		try {
			JSONObject mJsonBject = new JSONObject(resultPacket.jsonBody);
			//{"data":[{"uid":"61327000021294","csid":1,"pv":4,"state":1,"netmode":1,"phone":"13570867320","timestamp":1408449316,"appid":"f2dde200a91c4b3fae57954d325da5c3","version":"1.0.11"}],"retcode":0}
			if(mJsonBject.has("retcode") && mJsonBject.getInt("retcode") == 0 && mJsonBject.has("data")){
				JSONArray array = mJsonBject.getJSONArray("data");
				ArrayList<UcsStatus> list = new ArrayList<UcsStatus>();
				for(int i = 0 ; i < array.length() ; i ++){
					UcsStatus status = new UcsStatus();
					JSONObject item = (JSONObject)array.get(i);
					if(item.has("uid")){
						status.setUid(item.getString("uid"));
					}
					if(item.has("state")){
						status.setOnline(item.getInt("state") == 1);
					}
					if(item.has("timestamp")){
						status.setTimestamp(item.getString("timestamp"));
					}
					if(item.has("pv")){
						status.setPv(item.getInt("pv"));
					}
					if(item.has("netmode")){
						status.setNetmode(item.getInt("netmode"));
					}
					list.add(status);
				}
				for(MessageListener messageListener:MessageTools.getMessageListenerList()){
					messageListener.onUserState(list);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * msg֪ͨ
	 * @param msgReason
	 * @author: xiaozhenhua
	 * @data:2014-10-21 ����10:10:43
	 */
	public static void notifyMessages(UcsReason msgReason,UcsMessage message){
		for(MessageListener messageListener:MessageTools.getMessageListenerList()){
			messageListener.onSendUcsMessage(msgReason, message);
		}
		if(ConnectionControllerService.getInstance() != null
				&& msgReason != null){
			ErrorCodeReportTools.collectionErrorCode(ConnectionControllerService.getInstance(),UserData.getClientId(),"",msgReason.getReason(),msgReason.getMsg());
		}
	}
	
	
}
