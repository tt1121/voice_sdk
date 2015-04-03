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
	 * 添加自定义类型
	 * @param msgType
	 * @author: xiaozhenhua
	 * @data:2014-6-4 上午11:03:39
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
	 * 设置IM监听器
	 * @param imListener
	 * @author: xiaozhenhua
	 * @data:2014-5-14 下午3:31:38
	 */
	public static void addMessageListener(MessageListener messageListener) {
		MessageTools.addIMListener(messageListener);
	}
	public static void removeMessageListener(MessageListener messageListener){
		MessageTools.removeMessageListener(messageListener);
	}
	
	/**
	 * 
	 * @param receiverId:接收者ID
	 * @param text：消息内容
	 * @param filePath：文件路径
	 * @param expandData：自定义数据
	 * @param extra_mime：消息类型   1:文字   	2:图片	3:音频	4:视频
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-14 下午3:00:53
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
	 * 开始录音
	 * @param toUid:接收者ID
	 * @param recordListener：录音监听器
	 * @author: xiaozhenhua
	 * @data:2014-5-29 下午6:17:18
	 */
	public static boolean startVoiceRecord(String filePath,RecordListener recordListener){
		return RecordingTools.getInstance().startVoiceRecord(filePath,recordListener);
	}
	
	/**
	 * 停止录音
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-29 下午6:20:27
	 */
	public static void stopVoiceRecord(){
		RecordingTools.getInstance().stopVoiceRecord();
	}
	
	/**
	 * 播放录音文件
	 * @param filePath
	 * @param recordListener
	 * @author: xiaozhenhua
	 * @data:2014-5-29 下午6:49:52
	 */
	public static void startPlayerVoice(String filePath,RecordListener recordListener){
		RecordingTools.getInstance().startPlayerVoice(filePath, recordListener);
	}
	
	/**
	 * 停止播放录音
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-29 下午6:50:36
	 */
	public static void stopPlayerVoice(){
		RecordingTools.getInstance().stopPlayerVoice();
	}
	
	/**
	 * 获取语音文件时长
	 * @param filePath
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-29 下午6:54:37
	 */
	public static int getVoiceDuration(String filePath){
		return RecordingTools.getInstance().getDuration(filePath);
	}
	
	/**
	 * 当前是否正在播放
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-18 下午2:50:39
	 */
	public static boolean isPlaying(){
		return RecordingTools.getInstance().isPlaying();
	}
	
	/**
	 * 下载附件
	 * @param fileUrl:远程文件URL
	 * @param filePaht：本地文件路径
	 * @param msgId:消息ID
	 * @param fileListener：下载监听器
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-29 下午6:57:53
	 */
	public static DownloadThread downloadAttached(String fileUrl, String filePaht,String msgId,MessageListener fileListener){
		return HttpTools.downloadFile(fileUrl, filePaht,msgId,fileListener);
	}
	
	/**
	 * 查询多个client/phone的状态
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-8-4 下午5:53:20
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
	 * 查询单个client/phone的状态
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-8-4 下午5:53:29
	 */
	public static void queryUserState(ClientType emun,String clientId){
		ArrayList<String> list = new ArrayList<String>();
		list.add(clientId);
		queryUserState(emun,list);
	}		
	
}
