package com.yzx.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.media.ToneGenerator;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.EditText;

import com.gl.softphone.CallPushPara;
import com.gl.softphone.UGoManager;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.ForwardingListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;
import com.yzx.service.ConnectionControllerService;
import com.yzx.tcp.TcpTools;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tools.AudioManagerTools;
import com.yzx.tools.CustomLog;
import com.yzx.tools.ErrorCodeReportTools;
import com.yzx.tools.KeyboardTools;


/**
 * 电话管理类
 * @author xiaozhenhua
 *
 */
public class UCSCall {
	/**
	 * 对方或自己接听
	 */
	//public static final int CALL_VOIP_ANSWER = 0x000;
	/**
	 * 媒体协商失败
	 */
	//public static final int CALL_VOIP_MEDIA_CONSULT = 0x001;
	/**
	 * RTPP超时
	 */
	//public static final int CALL_VOIP_RTPP_TIMEOUT = 0x002;
	/**
	 * 余额不足
	 */
	public static final int CALL_VOIP_NOT_ENOUGH_BALANCE = 300211;
	/**
	 * 媒体更新失败
	 */
	//public static final int CALL_VOIP_MEDIA_UPDATE_FAILED = 0x004;
	/**
	 * 对方正忙
	 */
	public static final int CALL_VOIP_BUSY = 300212;
	/**
	 * 对方拒绝
	 */
	public static final int CALL_VOIP_REFUSAL = 300213;
	/**
	 * 用户不存在
	 */
	//public static final int CALL_VOIP_NOTFOUND = 0x007;
	/**
	 * 不在线或不存在
	 */
	public static final int CALL_VOIP_NUMBER_ERROR = 300214;
	/**
	 * 被叫号码错误
	 */
	public static final int CALL_VOIP_NUMBER_WRONG = 300215;
	/**
	 * 被号账户被冻结
	 */
	public static final int CALL_VOIP_REJECT_ACCOUNT_FROZEN = 300217;
	/**
	 * 主号账户被冻结
	 */
	public static final int CALL_VOIP_ACCOUNT_FROZEN = 300216;
	/**
	 * 主号账户过期
	 */
	public static final int CALL_VOIP_ACCOUNT_EXPIRED = 300218;
	/**
	 * 不能拨打自己绑定号码
	 */
	public static final int CALL_VOIP_CALLYOURSELF = 300219;
	/**
	 * 呼叫请求超时
	 */
	public static final int CALL_VOIP_NETWORK_TIMEOUT = 300220;
	/**
	 * 对方无人应答
	 */
	public static final int CALL_VOIP_NOT_ANSWER = 300221;
	/**
	 * 呼叫超时
	 */
	//public static final int CALL_VOIP_CALL_TIMEOUT = 0x020;
	/**
	 * 转直拨
	 */
	public static final int CALL_VOIP_TRYING_183 = 300222;
	/**
	 * 对方正在响铃
	 */
	public static final int CALL_VOIP_RINGING_180 = 300247;
	/**
	 * 鉴权失败(TCP未认证)
	 */
	public static final int CALL_VOIP_SESSION_EXPIRATION = 300223;
	/**
	 * 服务器错误
	 */
	public static final int CALL_VOIP_ERROR = 300210;
	
	/**
	 * 被叫方没有应答
	 */
	public static final int HUNGUP_NOT_ANSWER = CALL_VOIP_NOT_ANSWER;
	/**
	 * 连接失败
	 */
	//public static final int HUNGUP_CONNECT_FAILED = 0x029;
	/**
	 * 自己挂断电话
	 */
	public static final int HUNGUP_MYSELF = 300225;
	/**
	 * 对方挂断电话
	 */
	public static final int HUNGUP_OTHER = 300226;
	/**
	 * 2G时不允许拨打电话（回拨除外）
	 */
	public static final int HUNGUP_WHILE_2G = 300267;
	/**
	 * 对方拒绝接听
	 */
	public static final int HUNGUP_REFUSAL = CALL_VOIP_REFUSAL;
	/**
	 * 自己拒绝
	 */
	public static final int HUNGUP_MYSELF_REFUSAL = 300248;
	/**
	 * 余额不足
	 */
	public static final int HUNGUP_NOT_ENOUGH_BALANCE = CALL_VOIP_NOT_ENOUGH_BALANCE;
	
	/**
	 * 不支持视频电话
	 */
	public static final int CALL_VIDEO_DOES_NOT_SUPPORT = 300249;
	
	private static ArrayList<CallStateListener> callStateListenerList = new ArrayList<CallStateListener>();

	public static ArrayList<CallStateListener> getCallStateListener() {
		return callStateListenerList;
	}

	private static String currentCallId;

	public static void setCurrentCallId(String cid) {
		currentCallId = cid;
	}

	public static String getCurrentCallId() {
		return currentCallId != null && currentCallId.length() > 0 ? currentCallId : "";
	}
	
	
	/**
	 * 添加电话监听器
	 * @param csl
	 * @author: xiaozhenhua
	 * @data:2014-4-29 下午5:50:23
	 */
	public static void addCallStateListener(CallStateListener csl){
		callStateListenerList.add(csl);
	}

	/* BEGIN: Added by gonghuojin, 2014/10/17   DS:conference api */
	
	
	/*public static void setAc(String token){
		UserData.saveAc(token);
	}
	public static void setClientId(String clientId){
		UserData.saveClientId(clientId);
	}
	public static void tcpConnection(){
		TcpTools.tcpConnection();
	}*/
	
	@Deprecated
	public static void dialConference(Context mContext,ArrayList<ConferenceMemberInfo> memberStr){
		startChatRoom(mContext,memberStr);
	}
	/**
	 * 发起一个会议呼叫
	 * @param callType:0:VOIP    1:DIRECT
	 * @param memberStr:被邀请成员clientid字符串，成员之间以","分隔  
	 * @author: gonghuojin
	 * @data:2014-10-17 下午12:29:40
	 */
	public static void startChatRoom(Context mContext,ArrayList<ConferenceMemberInfo> memberStr){
		if(mContext != null && memberStr != null && memberStr.size() > 0){
			if(memberStr.size() > 5){
				for(CallStateListener csl:getCallStateListener()){
					csl.onDialFailed("", new UcsReason(300308));
				}
			}else{
				Intent dial = new Intent(PacketDfineAction.INTENT_ACTION_CONFERENCEDIAL);
				JSONArray jsonArray = new JSONArray();
		        JSONObject conferenceCallParam[] = new JSONObject[memberStr.size()];
		        for (int i = 0; i < memberStr.size(); i++) {
		        	conferenceCallParam[i] = new JSONObject();
		            try {
		            	//conferenceCallParam[i].put("mode", 5);
		                if(memberStr.get(i).getCallType() == CallType.VOIP){
		                	conferenceCallParam[i].put("mode", 6);
		                }else if(memberStr.get(i).getCallType() == CallType.DIRECT){
		                	conferenceCallParam[i].put("mode", 4);
		                }else{
		                	conferenceCallParam[i].put("mode", 5);
		                }
		                conferenceCallParam[i].put("uid", memberStr.get(i).getUid());
		                conferenceCallParam[i].put("phone", memberStr.get(i).getPhone());
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
		            jsonArray.put(conferenceCallParam[i]);
		        }
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MEMBERSTR, jsonArray.toString());
				CustomLog.v("MEETING_PARAM:"+jsonArray.toString());
				mContext.sendBroadcast(dial);
			}
		}else{
			for(CallStateListener csl:getCallStateListener()){
				csl.onDialFailed("", new UcsReason(300006).setMsg("memberStr is null"));
			}
		}
	}
	
	/**
	 * 退出多方通话
	 * 
	 * @author: xiaozhenhua
	 * @data:2015-3-25 下午3:04:56
	 */
	public static void stopChatRoom(){
		hangUp("");
	}


	/**
	 * 发起一个呼叫
	 * @param calledNumner:用户clientId或者手机号码
	 * @param type： DIRECT ：直拨	VOIP：免费	    VIDEO:视频
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午12:29:40
	 */
	public static void dial(Context mContext,CallType callType,String calledNumner){
		if (mContext != null 
				&& callType != null 
				&& calledNumner != null
				&& calledNumner.length() > 0) {
			Intent dial = new Intent(PacketDfineAction.INTENT_ACTION_DIAL);
			if (callType == CallType.VOIP) {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 6);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUID,calledNumner);
			} else if (callType == CallType.CALL_AUTO) {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 5);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,calledNumner);
			} else if (callType == CallType.DIRECT) {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 4);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,calledNumner);
			}/* else if (callType == CallType.VIDEO) {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 3);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUID,calledNumner);
			} */else {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 2);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,calledNumner);
			}
			mContext.sendBroadcast(dial);
		} else {
			notifyDialFailed(new UcsReason(300006).setMsg("calledNumner is null "));
		}
	}
	/**
	 * 发起一个VOIP呼叫
	 * @param callType 只能是VOIP拨号
	 * @param userdata 透传信息
	 */
	public static void dial(Context mContext,CallType callType,String calledNumner,String userdata){
		if (mContext != null 
				&& callType != null 
				&& calledNumner != null
				&& calledNumner.length() > 0) {
			Intent dial = new Intent(PacketDfineAction.INTENT_ACTION_DIAL);
			if (callType == CallType.VOIP) {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUSERDATA, userdata);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 6);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUID,calledNumner);
			} else if (callType == CallType.CALL_AUTO) {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUSERDATA, userdata);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 5);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,calledNumner);
			} else if (callType == CallType.DIRECT) {
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUSERDATA, userdata);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 4);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,calledNumner);
			}
			mContext.sendBroadcast(dial);
		} else {
			notifyDialFailed(new UcsReason(300006).setMsg("calledNumner is null "));
		}
	}
	
	
	/**
	 * 回拨  
	 * @param mContext
	 * @param calledNumner：被叫号码
	 * @param fromSerNum：呼叫主叫所要显示的号码
	 * @param toSerMum：呼叫被叫所要显示的号码
	 * @author: xiaozhenhua
	 * @data:2014-9-4 下午5:33:24
	 */
	public static void callBack(Context mContext,String calledPhone,String fromSerNum,String toSerMum){
		if(!UCSCall.isCallForwarding()){
			if (mContext != null 
					&& calledPhone != null
					&& calledPhone.length() > 0) {
				Intent dial = new Intent(PacketDfineAction.INTENT_ACTION_DIAL);
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 2);
				if(fromSerNum != null && fromSerNum.length() > 0){
					dial.putExtra(PacketDfineAction.FROM_SER_NUM,fromSerNum);
				}
				if(toSerMum != null && toSerMum.length() > 0){
					dial.putExtra(PacketDfineAction.TO_SER_NUM,toSerMum);
				}
				dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,calledPhone);
				mContext.sendBroadcast(dial);
			}else {
				notifyDialFailed(new UcsReason(300006).setMsg("callback calledNumner is null "));
			}
		}else{
			notifyDialFailed(new UcsReason(300403).setMsg("callback calledNumner is null "));
		}
	}
	

	/**
	 * 结束通话(挂断)
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-21 上午10:36:58
	 */
	public static void hangUp(String callId){
		if(ConnectionControllerService.getInstance() != null){
			UserData.saveCurrentCall(false);
			ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_HANDUP).putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, (callId != null && callId.length() > 0) ? callId:""));
		}
	}

	/**
	 * 接听电话
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-21 上午10:37:34
	 */
	public static void answer(String callId){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_ANSWER).putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, (callId != null && callId.length() > 0) ? callId:""));
		}
	}
	
	
	/**
	 * 发送DTMF音
	 * @param dtmf
	 * @author: xiaozhenhua
	 * @data:2014-4-21 上午10:40:41
	 */
	public static void sendDTMF(Context mContext,int keyCode,EditText call_dtmf){
		String text = "";
		switch(keyCode){
		case KeyEvent.KEYCODE_0:
			text = "0";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_2);
			break;
		case KeyEvent.KEYCODE_1:
			text = "1";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_1);
			break;
		case KeyEvent.KEYCODE_2:
			text = "2";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_2);
			break;
		case KeyEvent.KEYCODE_3:
			text = "3";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_3);
			break;
		case KeyEvent.KEYCODE_4:
			text = "4";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_4);
			break;
		case KeyEvent.KEYCODE_5:
			text = "5";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_5);
			break;
		case KeyEvent.KEYCODE_6:
			text = "6";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_6);
			break;
		case KeyEvent.KEYCODE_7:
			text = "7";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_7);
			break;
		case KeyEvent.KEYCODE_8:
			text = "8";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_8);
			break;
		case KeyEvent.KEYCODE_9:
			text = "9";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_9);
			break;
		case KeyEvent.KEYCODE_POUND:
			text = "#";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_9);
			break;
		case KeyEvent.KEYCODE_STAR:
			text = "*";
			KeyboardTools.getInstance(mContext).playKeyBoardVoice(ToneGenerator.TONE_DTMF_7);
			break;
		default:
			//playKeyBoardVoice(ToneGenerator.TONE_DTMF_D);
			break;
		}
		if(text != null && text.length() > 0){
			if(call_dtmf != null){
				call_dtmf.getEditableText().insert(call_dtmf.getText().length(), text);
			}
			if(ConnectionControllerService.getInstance() != null){
				ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_DTMF).putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, text.charAt(0)));
			}
		}
	}
	
	/**
	 * 设备是否免提
	 * @param isSpeakerphoneOn  true:免提   false:内放
	 * @author: xiaozhenhua
	 * @data:2014-4-21 上午10:42:18
	 */
	public static void setSpeakerphone(boolean isSpeakerphoneOn){
		setSpeaker(isSpeakerphoneOn);
	}
	
	private static void setSpeaker(final boolean isSpeakerphoneOn){
		//CustomLog.v("1-SET_SPEAKER_PHONE_ON:"+isSpeakerphoneOn);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//CustomLog.v("2-SET_SPEAKER_PHONE_ON:"+isSpeakerphoneOn);
				AudioManagerTools.getInstance().setSpeakerPhoneOn(isSpeakerphoneOn);
			}
		}, 500);
	}
	
	
	/**
	 * 获取扬声器状态
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-21 上午10:43:49
	 */
	public static boolean isSpeakerphoneOn(){
		return AudioManagerTools.getInstance().isSpeakerphoneOn();
		//return AudioManagerTools.getInstance().getPlayoutSpeaker();
	}
	
	/**
	 * 设备静音
	 * @param isMicMute true:静音   false:正常
	 * @author: xiaozhenhua
	 * @data:2014-4-21 上午10:45:06
	 */
	public static void setMicMute(boolean isMicMute){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_MIC_MUTE).putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_MIC_MUTE, isMicMute));
		}
	}
	
	/**
	 * 获得静音状态
	 * @author: xiongjijian
	 * @data:2014-10-31 下午17:45:06
	 */
	public static boolean isMicMute(){
		return UGoManager.getInstance().pub_UGoGetMicMute();
	}
	
	private static boolean isAudioDeviceAdapter = false;
	
	/**
	 * 设备是否驱动智能适配
	 * @param isAudioDeviceDapter  true:启动智能适配   false:不启动
	 * @author: lion
	 * @data:2014-4-21 上午10:42:18
	 */
	public static void setAudioDeviceAdapter(boolean isAudioDeviceAdapterOn){
		isAudioDeviceAdapter = isAudioDeviceAdapterOn;
		ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_AD_ADAPTER).putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_AD_ADAPTER, isAudioDeviceAdapterOn));
	}

	/**
	 * 设备驱动智能适配是否成功
	 * @param isAudioDeviceDapter  true:智能适配成功   false:不成功
	 * @author: lion
	 * @data:2014-4-21 上午10:42:18
	 */
	public static void setADAdapterSuccess(boolean isADAdapterSuccess){
		if(ConnectionControllerService.getInstance() != null){
			ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_ADAPTER_OK).putExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_ADAPTER_OK, isADAdapterSuccess));
		}
	}
	
	/**
	 * 第三方通知激活接口
	 * @param param
	 * @return
	 * @author: xiaozhenhua
	 * @data:2015-3-24 下午2:54:29
	 */
	public static int callPush(CallPushPara param){
		return UGoManager.getInstance().pub_UGoCallPush(param);
	}
	
	/**
	 * 获取驱动智能适配状态
	 * @return
	 * @author: lion
	 * @data:2014-4-21 上午10:43:49
	 */
	public static boolean isAudioDeviceAdapterOn(){
		return isAudioDeviceAdapter;
	}
	
	/**
	 * 播放来电音
	 * @param RingtoneManagerType
	 * @param vibrator
	 * @author: xiaozhenhua
	 * @data:2013-2-19 下午4:39:04
	 */
	public static void startRinging(boolean isVibrator){
		AudioManagerTools.getInstance().startRinging(isVibrator);
	}
	
	/**
	 * 停止来电音
	 * @author: xiaozhenhua
	 * @data:2014-6-3 上午11:26:20
	 */
	public static void stopRinging(){
		AudioManagerTools.getInstance().stopRinging();
	}
	
	/**
	 * 
	 * 播放去电音
	 * @author: xiaozhenhua
	 * @data:2014-6-3 上午11:28:11
	 */
	public static void startCallRinging(String fileName){
		AudioManagerTools.getInstance().startCallRinging(fileName);
	}
	
	/**
	 * 停止播放去电音
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-3 上午11:35:59
	 */
	public static void stopCallRinging(){
		CustomLog.v("STOP CALL RINGING");
		AudioManagerTools.getInstance().stopCallRinging();
	}

	private static void notifyDialFailed(UcsReason dial) {
		for (CallStateListener csl : getCallStateListener()) {
			csl.onDialFailed(UCSCall.getCurrentCallId(),dial);
		}
		if(ConnectionControllerService.getInstance() != null){
			ErrorCodeReportTools.collectionErrorCode(ConnectionControllerService.getInstance(), UserData.getClientId(), "onDialFailed", dial.getReason(), dial.getMsg());
		}
	}
	
	private static ForwardingListener forListener;
	
	public static ForwardingListener getForListener() {
		return forListener;
	}

	public static void setCallForwardingOption(boolean forwarding,ForwardingListener forwardingListener){
		forListener = forwardingListener;
		ConnectionControllerService.getInstance().sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_FORWARDING).putExtra(PacketDfineAction.INTENT_ACTION_KEY_FORWARDING, forwarding));
	}
	
	public static boolean isCallForwarding(){
		return UserData.isForwarding();
	}
	
}
