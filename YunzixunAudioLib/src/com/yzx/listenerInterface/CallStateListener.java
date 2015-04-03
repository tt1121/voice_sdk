package com.yzx.listenerInterface;

import java.util.ArrayList;

import com.yzx.api.ConferenceInfo;

/**
 * 电话状态监听器
 * 
 * @author xiaozhenhua
 * 
 */
public interface CallStateListener {

	/**
	 * 回拨呼叫成功
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-23 下午4:59:50
	 */
	public void onCallBackSuccess();

	/**
	 * VOIP呼叫状态
	 * 
	 * @param reason
	 *            :参考VOIP呼叫状态码
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午12:50:37
	 */
	public void onDialFailed(String callId, UcsReason reason);

	/**
	 * 有新来电
	 * 
	 * @param phone
	 *            :来电的电话号号码或者cliend账户(voipaccount)
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午12:51:16
	 */
	public void onIncomingCall(String callId, String callType,
			String callerNumber, String nickName, String userdata);

	/**
	 * 挂断事件回调
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午2:36:03
	 */
	public void onHangUp(String callId, UcsReason reason);

	/**
	 * 呼叫振铃中
	 * 
	 * @param callId
	 * @author: xiaozhenhua
	 * @data:2014-4-29 下午2:30:08
	 */
	public void onAlerting(String callId);

	/**
	 * 接听事件回调
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午2:34:49
	 */
	public void onAnswer(String callId);

	/**
	 * 多方通话来电回调函数
	 * @param callId
	 * @param callType
	 * @param callerNumber
	 * @param nickName
	 * @param userdata
	 * @author: xiaozhenhua
	 * @data:2015-3-25 下午3:07:05
	 */
	public void onChatRoomIncomingCall(String callId, String callType,
			String callerNumber, String nickName, String userdata);
	/**
	 * 会议通知回调事件
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午2:47:28
	 */
	public void onChatRoomState(String callId, ArrayList<ConferenceInfo> states);
	@Deprecated
	public void onConferenceState(String callId, ArrayList<ConferenceInfo> states);
	/**
	 * 会议模式转换通知回调事件
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午2:47:28
	 */
	public void onChatRoomModeConvert(String callId);
	@Deprecated
	public void onConferenceModeConvert(String callId);
	/**
	 * DTMF回调
	 * 
	 * @param dtmf
	 * @author: xiaozhenhua
	 * @data:2015-3-16 上午11:33:44
	 */
	public void onDTMF(int dtmfCode);

	/**
	 * 单通回调事件
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午2:47:28
	 */
	// public void singlePass(int reason);

	/**
	 * 网络状态上报
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 下午2:48:59
	 */
	// public void onNetworkState(int reason);
}
