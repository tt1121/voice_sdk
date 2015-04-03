package com.yzx.api;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;

import com.yzx.http.DownloadThread;
import com.yzx.http.HttpTools;
import com.yzx.listenerInterface.MessageListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.listenerInterface.RecordListener;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tcp.packet.MessageTools;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tools.PhoneNumberTools;
import com.yzx.tools.RecordingTools;

public class UCSMessage {
	
	
	
	public static final int SEND = 1;
	
	public static final int TEXT = 1;
	public static final int PIC = 2;
	public static final int VOICE = 3;
	public static final int VIDEO = 4;

	public static HashMap<Integer,Integer> MSG_TYPE = new HashMap<Integer,Integer>();
	static{
		MSG_TYPE.put(TEXT,null);
		MSG_TYPE.put(PIC,null);
		MSG_TYPE.put(VOICE,null);
		MSG_TYPE.put(VIDEO,null);
	}

	/**
	 * ����Զ�������
	 * @param msgType
	 * @author: xiaozhenhua
	 * @data:2014-6-4 ����11:03:39
	 */
	public static void addMessageType(int msgType){
		if(!MSG_TYPE.keySet().contains(msgType) && isType(msgType)){
			MSG_TYPE.put(msgType, null);
		}else{
			MessageTools.notifyMessages(new UcsReason(300244).setMsg(msgType+""), null);
		}
	}
	
	private static boolean isType(int msgType){
		return msgType > 0 && msgType != TEXT && msgType != PIC && msgType != VOICE && msgType != VIDEO && msgType >= 10 && msgType <= 29;
	}
	
	/**
	 * ����IM������
	 * @param imListener
	 * @author: xiaozhenhua
	 * @data:2014-5-14 ����3:31:38
	 */
	public static void addMessageListener(MessageListener messageListener) {
		MessageTools.addIMListener(messageListener);
	}
	public static void removeMessageListener(MessageListener messageListener){
		MessageTools.removeMessageListener(messageListener);
	}
	
	/**
	 * 
	 * @param receiverId:������ID
	 * @param text����Ϣ����
	 * @param filePath���ļ�·��
	 * @param expandData���Զ�������
	 * @param extra_mime����Ϣ����   1:����   	2:ͼƬ	3:��Ƶ	4:��Ƶ
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-14 ����3:00:53
	 */
	public static void sendUcsMessage(String receiverId, String text, String filePath,int extra_mime) {
		if(MSG_TYPE.keySet().contains(extra_mime)){
			if(receiverId != null && receiverId.length() > 0 && receiverId.length() <= 15){
				if(PhoneNumberTools.isNumber(receiverId)){
					Intent sendMsg = new Intent(PacketDfineAction.INTENT_ACTION_SEND_MESSAGE);
					sendMsg.putExtra(PacketDfineAction.TYPE, extra_mime);
					sendMsg.putExtra(PacketDfineAction.TOUID, receiverId);
					sendMsg.putExtra(PacketDfineAction.MSG, text == null || text.length() <= 0?"":text);
					sendMsg.putExtra(PacketDfineAction.PATH, filePath == null || filePath.length() <= 0?"":filePath);
					if(ConnectionControllerService.getInstance() != null){
						ConnectionControllerService.getInstance().sendBroadcast(sendMsg);
					}else{
						notifyListener(300228);
					}
				}else{
					notifyListener(300232);
				}
			}else{
				notifyListener(300231);
			}
		}else{
			notifyListener(300244);
		}
	}

	private static void notifyListener(int reason){
		notifyListener(new UcsReason(reason).setMsg(""));
	}
	
	private static void notifyListener(UcsReason reason){
		MessageTools.notifyMessages(reason, null);
	}
	
	/**
	 * ��ʼ¼��
	 * @param toUid:������ID
	 * @param recordListener��¼��������
	 * @author: xiaozhenhua
	 * @data:2014-5-29 ����6:17:18
	 */
	public static boolean startVoiceRecord(String filePath,RecordListener recordListener){
		return RecordingTools.getInstance().startVoiceRecord(filePath,recordListener);
	}
	
	/**
	 * ֹͣ¼��
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-29 ����6:20:27
	 */
	public static void stopVoiceRecord(){
		RecordingTools.getInstance().stopVoiceRecord();
	}
	
	/**
	 * ����¼���ļ�
	 * @param filePath
	 * @param recordListener
	 * @author: xiaozhenhua
	 * @data:2014-5-29 ����6:49:52
	 */
	public static void startPlayerVoice(String filePath,RecordListener recordListener){
		RecordingTools.getInstance().startPlayerVoice(filePath, recordListener);
	}
	
	/**
	 * ֹͣ����¼��
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-29 ����6:50:36
	 */
	public static void stopPlayerVoice(){
		RecordingTools.getInstance().stopPlayerVoice();
	}
	
	/**
	 * ��ȡ�����ļ�ʱ��
	 * @param filePath
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-29 ����6:54:37
	 */
	public static int getVoiceDuration(String filePath){
		return RecordingTools.getInstance().getDuration(filePath);
	}
	
	/**
	 * ��ǰ�Ƿ����ڲ���
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-18 ����2:50:39
	 */
	public static boolean isPlaying(){
		return RecordingTools.getInstance().isPlaying();
	}
	
	/**
	 * ���ظ���
	 * @param fileUrl:Զ���ļ�URL
	 * @param filePaht�������ļ�·��
	 * @param msgId:��ϢID
	 * @param fileListener�����ؼ�����
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-29 ����6:57:53
	 */
	public static DownloadThread downloadAttached(String fileUrl, String filePaht,String msgId,MessageListener fileListener){
		return HttpTools.downloadFile(fileUrl, filePaht,msgId,fileListener);
	}
	
	/**
	 * ��ѯ���client/phone��״̬
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-8-4 ����5:53:20
	 */
	public static void queryUserState(ClientType emun,ArrayList<String> clientList){
		StringBuffer clientBuffer = new StringBuffer();
		for(String str:clientList){
			clientBuffer.append(str+",");
		}
		if(ConnectionControllerService.getInstance() != null){
			Intent intent = new Intent(PacketDfineAction.INTENT_ACTION_QUERY_STATE);
			intent.putExtra(PacketDfineAction.TYPE, emun == ClientType.PHONE ? 0:1);
			intent.putExtra(PacketDfineAction.UID, clientBuffer.toString().substring(0, clientBuffer.length() - 1));
			ConnectionControllerService.getInstance().sendBroadcast(intent);
		}else{
			notifyListener(300228);
		}
	}
	/**
	 * ��ѯ����client/phone��״̬
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-8-4 ����5:53:29
	 */
	public static void queryUserState(ClientType emun,String clientId){
		ArrayList<String> list = new ArrayList<String>();
		list.add(clientId);
		queryUserState(emun,list);
	}		
	
}
