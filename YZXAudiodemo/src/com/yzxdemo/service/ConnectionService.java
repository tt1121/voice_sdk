package com.yzxdemo.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.yzx.api.CallType;
import com.yzx.api.ConferenceInfo;
import com.yzx.api.ConferenceMemberInfo;
import com.yzx.api.ConferenceState;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSMessage;
import com.yzx.api.UCSService;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.MessageListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.packet.UcsMessage;
import com.yzx.tcp.packet.UcsStatus;
import com.yzx.tools.CustomLog;
import com.yzxdemo.MainApplication;
import com.yzxdemo.action.UIDfineAction;
import com.yzxdemo.activity.AudioConverseActivity;
import com.yzxdemo.activity.ConferenceConverseActivity;
import com.yzxdemo.activity.IMMessageActivity;
import com.yzxdemo.activity.TerminalLoginDialogActivity;
import com.yzxdemo.tools.DataTools;
import com.yzxdemo.tools.DfineAction;
import com.yzxdemo.tools.LoginConfig;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;


/**
 * 后台服务/连接控制器
 *
 */
public class ConnectionService extends Service implements ConnectionListener,CallStateListener,MessageListener {

	
	public static HashMap<String ,UcsStatus> mapstatus = new HashMap<String ,UcsStatus>();
	
	
	private String cliend_id;
	private String cliend_pwd;
	private String sid;
	private String sid_pwd;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("NewApi") @Override
	public void onCreate() {
		super.onCreate();
		mapstatus.clear();
		CustomLog.i(DfineAction.TAG_TCP,"ConnectionService onCreate ... ");
		
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
		}
		//添加连接监听器
		UCSService.addConnectionListener(ConnectionService.this);
		//添加电话监听器
		UCSCall.addCallStateListener(ConnectionService.this);
		//添加消息监听器
		UCSMessage.addMessageListener(ConnectionService.this);
		//初始化SDK
		UCSService.init(this,true);
		//添加自定义消息类型  地理位置与附件
		UCSMessage.addMessageType(UIDfineAction.LOCATION);
		UCSMessage.addMessageType(UIDfineAction.FILE);
        
        IntentFilter ift = new IntentFilter();
        ift.addAction(UIDfineAction.ACTION_LOGIN);
        ift.addAction(UIDfineAction.ACTION_DIAL);
        ift.addAction(UIDfineAction.ACTION_START_TIME);
        ift.addAction(UCSService.ACTION_INIT_SUCCESS);
        registerReceiver(br, ift);
        UCSService.openSdkLog(true);//输出sdk日志到sd卡中 yunzhixun/log/YZX_SDK_日期.txt
	}
	
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(UIDfineAction.ACTION_LOGIN)){
				//重新登录时，如果有通话侧挂断
				UCSCall.hangUp("");
				//进行Client账号登陆
				CustomLog.i(DfineAction.TAG_TCP, "开始进行子账号登入");
				if(intent.hasExtra("cliend_id") && intent.hasExtra("cliend_pwd")){
					cliend_id = intent.getStringExtra("cliend_id");
					cliend_pwd = intent.getStringExtra("cliend_pwd");
					sid = intent.getStringExtra("sid");
					sid_pwd = intent.getStringExtra("sid_pwd");
					CustomLog.i(DfineAction.TAG_TCP, "CLIENT_ID:"+cliend_id+"   CLIENT_PWD:"+cliend_pwd);
					if(cliend_id != null && cliend_id.length() > 0
							&& cliend_pwd != null && cliend_pwd.length() > 0){
						if(DataTools.istest){
							//公司测试专用
							connectionService("http://113.31.89.144","8887",sid,sid_pwd,cliend_id,cliend_pwd);
						}else {
							//开发者使用
							connectionService(null,null,sid,sid_pwd,cliend_id,cliend_pwd);
						}
					}
				}else if(intent.hasExtra("sid_pwd")){
					cliend_id = null;
					cliend_pwd = null;
					sid = null;
					sid_pwd = null;
					String token = intent.getStringExtra("sid_pwd");
					CustomLog.i(DfineAction.TAG_TCP, "TOKEN:"+token);
					//==================token====================
					connectionService(null,null,token);
				}
			}else if(intent.getAction().equals(UIDfineAction.ACTION_DIAL)){
				int type = intent.getIntExtra("type", 1);
				String uid = intent.getStringExtra(UIDfineAction.CALL_UID);
				String phone = intent.getStringExtra(UIDfineAction.CALL_PHONE);
				//FROM_NUM_KEY（主叫显号）和TO_NUM_KEY（被叫显号），仅仅作为回拨的时候显示用：可以为空
				String fromSerNum= "";
				String toSerNum = "";
				if(intent.hasExtra(UIDfineAction.FROM_NUM_KEY)){
					fromSerNum = intent.getStringExtra(UIDfineAction.FROM_NUM_KEY);
				}
				if(intent.hasExtra(UIDfineAction.TO_NUM_KEY)){
					toSerNum = intent.getStringExtra(UIDfineAction.TO_NUM_KEY);
				}
				//type:
				//		0：直拨
				//		1：免费
				//		2:回拨
				//		3:视频点对点
				//		4:会议
				//      5:智能拨打
				switch(type){
				case 0:
					UCSCall.dial(ConnectionService.this,CallType.DIRECT, phone);
					break;
				case 1:
					//UCSCall.dial(ConnectionService.this,CallType.VOIP, uid);
					//可以用下面函数透传内容
					UCSCall.dial(ConnectionService.this,CallType.VOIP, uid, "欢迎加入云之讯");
					break;
				case 2:
					UCSCall.callBack(ConnectionService.this, phone, fromSerNum, toSerNum);
					break;
				case 3:
					//UCSCall.dial(ConnectionService.this,CallType.VIDEO, uid);
					break;
				case 4:
					ArrayList<ConferenceMemberInfo> list = new ArrayList<ConferenceMemberInfo>();
					list = (ArrayList<ConferenceMemberInfo>) intent.getSerializableExtra("conference");
					UCSCall.dialConference(ConnectionService.this,list);
					CustomLog.v(DfineAction.TAG_TCP, "调用会议sdk");
					break;
				case 5:
					UCSCall.dial(ConnectionService.this, CallType.CALL_AUTO, phone);
					break;
				}
			}else if(intent.getAction().equals(UIDfineAction.ACTION_LOGOUT)){
				//UCSService.uninit(ConnectionService.this);
			}else if(intent.getAction().equals(UCSService.ACTION_INIT_SUCCESS)){
				CustomLog.i(DfineAction.TAG_TCP, "启动成功  ... ");
			}else if(intent.getAction().equals(UIDfineAction.ACTION_START_TIME)){
				startCallTimer();
			}
		}
	};
	
	
	

	
	@Override
	public void onDestroy() {
		CustomLog.i(DfineAction.TAG_TCP,"onDestroy ... ");
		unregisterReceiver(br);
		//断开云联接
		UCSService.uninit();
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

	
	//连接失败或断线回调
	@Override
	public void onConnectionFailed(UcsReason reason) {
		CustomLog.i(DfineAction.TAG_TCP,"CONNECTION_FAILED:"+reason.getReason());
		if(reason.getMsg().length() > 0){
			CustomLog.i(DfineAction.TAG_TCP,"CONNECTION_FAILED:"+reason.getMsg());
		}
		sendBroadcast(new Intent(UIDfineAction.ACTION_TCP_LOGIN_CLIENT_RESPONSE).putExtra(UIDfineAction.RESULT_KEY, 1).putExtra(UIDfineAction.REASON_KEY, reason.getReason()));
		if(reason.getReason() == 300505 || reason.getReason() == 300207){
			sendBroadcast(new Intent(UIDfineAction.ACTION_LOGOUT).putExtra(UIDfineAction.REASON_KEY, reason.getReason()));
			Intent intent = new Intent();
			intent.setClass(ConnectionService.this,TerminalLoginDialogActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("reason", reason.getReason());
			startActivity(intent);
		}
		//无网络状态
		if(reason.getReason() == 300318){
			sendBroadcast(new Intent(UIDfineAction.ACTION_NETWORK_STATE).putExtra("state", 100));
		}
	}

	//连接成功回调
	@Override
	public void onConnectionSuccessful() {
		IMMessageActivity.msgList.clear();
		sendBroadcast(new Intent(UIDfineAction.ACTION_TCP_LOGIN_CLIENT_RESPONSE).putExtra(UIDfineAction.RESULT_KEY, 0));
		CustomLog.i(DfineAction.TAG_TCP,"CONNECTION_SUCCESS ... ");
		if(cliend_id != null && cliend_id.length() > 0){
			LoginConfig.saveCurrentClientId(ConnectionService.this, cliend_id);
		}
	}
	
	public void connectionService(final String host,final String port,final String sid ,final String sidPwd ,final String cliend_id,final String cliend_pwd){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(host != null && host.length() > 0){
					UCSService.connect(host,port,sid,sidPwd,cliend_id,cliend_pwd);
				}else{
					UCSService.connect(sid,sidPwd,cliend_id,cliend_pwd);
				}
			}
		}).start();
	}
	
	public void connectionService(final String host,final String port,final String token){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(host != null && host.length() > 0){
					UCSService.connect(host,port,token);
				}else{
					UCSService.connect(token);
				}
			}
		}).start();
	}

	
	
	
	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	private Timer timer = null;
	/**
	 * 通话走时
	 * @author: xiaozhenhua
	 * @data:2014-6-24 上午10:19:56
	 */
	public void startCallTimer(){
		stopCallTimer();
		if(timer == null){
			timer = new Timer();
		}
		second = 0; //秒
		minute = 0; //分
		hour = 0;   //时
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				StringBuffer timer = new StringBuffer();
				second++;
				if (second >= 60) {
					minute++;
					second = 0;
				}
				if (minute >= 60) {
					hour++;
					minute = 0;
				}
				if (hour != 0) {
					if (hour < 10) {
						timer.append(0);
					}
					timer.append(hour);
					timer.append(":");
				}
				if (minute < 10) {
					timer.append(0);
				}
				timer.append(minute);
				timer.append(":");
				if (second < 10) {
					timer.append(0);
				}
				timer.append(second);
				sendBroadcast(new Intent(UIDfineAction.ACTION_CALL_TIME).putExtra("callduration", hour * 3600 + minute * 60 + second).putExtra("timer", timer.toString()));
			}
		}, 0, 1000);
	}
	
	public void stopCallTimer(){
		if (timer != null){
			timer.cancel();
			timer=null;
		}
	}
	
	
	//对方正在响铃回调
	@Override
	public void onAlerting(String arg0) {
		CustomLog.i(DfineAction.TAG_TCP,"onAlerting CURRENT_ID:"+arg0);
		sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_RINGING_180));
	}

	//对方接通回调
	@Override
	public void onAnswer(String arg0) {
		CustomLog.i(DfineAction.TAG_TCP,"onAnswer CURRENT_ID:"+arg0);
		sendBroadcast(new Intent(UIDfineAction.ACTION_ANSWER));
	}

	//拨打失败回调，请打印出错误码reason.getReason()，官网查询错误码含义
	@Override
	public void onDialFailed(String arg0, UcsReason reason) {
		CustomLog.i(DfineAction.TAG_TCP,"onDialFailed CURRENT_ID:"+arg0+"          SERVICE:"+reason.getReason()+"   MSG:"+reason.getMsg());
		voipSwitch(reason);
	}

	private void voipSwitch(UcsReason reason){
		switch(reason.getReason()){
		case 300210:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_ERROR));
			break;
		case 300211:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NOT_ENOUGH_BALANCE));
			break;
		case 300212:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_BUSY));
			break;
		case 300213:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_REFUSAL));
			break;
		case 300214:
		case 300215:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NUMBER_ERROR));
			break;
		case 300216:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_ACCOUNT_FROZEN));
			break;
		case 300217:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_REJECT_ACCOUNT_FROZEN));
			break;
		case 300218:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_ACCOUNT_EXPIRED));
			break;
		case 300219:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_CALLYOURSELF));
			break;
		case 300220:
		case 300224:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NETWORK_TIMEOUT));
			break;
		case 300221:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_NOT_ANSWER));
			break;
		case 300222:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_TRYING_183));
			break;
		case 300223:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_SESSION_EXPIRATION));
			break;
		case 300225:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_MYSELF));
			break;
		case 300226:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_OTHER));
			break;
		case 300267:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_WHILE_2G));
			break;
		case 300248:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_MYSELF_REFUSAL));
			break;
		case 300249:
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VIDEO_DOES_NOT_SUPPORT));
			break;
		default:
			if(reason.getReason() >= 10000 && reason.getReason() <= 20000){//透传错误码
				CustomLog.i(DfineAction.TAG_TCP,"KC_REASON:"+reason.getReason());
			}else if(reason.getReason() >= 300233 && reason.getReason() <= 300243){//回拨
				sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", reason.getReason()));
			}else if(reason.getReason() >= 300301 && reason.getReason() <= 300308){//会议
				sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", reason.getReason()));
			}
			break;
		}
	}
	
	
	@Override
	public void onHangUp(String arg0, UcsReason reason) {
		UCSCall.stopCallRinging();
		sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_HANGUP).putExtra("state", reason.getReason()));
		CustomLog.i(DfineAction.TAG_TCP,"onHangUp CURRENT_ID:"+arg0+"          SERVICE:"+reason.getReason());
		voipSwitch(reason);
		stopCallTimer();
	}

	/**
	 * 接收新消息
	 * nickName 显示主叫昵称
	 * userdata 暂时没用到
	 */
	@Override
	public void onIncomingCall(String callId, String callType, String callerNumber ,String nickName, String userdata) {
		CustomLog.v(DfineAction.TAG_TCP, "收到新的来电 callType="+callType);
		CustomLog.v(DfineAction.TAG_TCP, "收到透传信息："+userdata);
		Intent intent = new Intent();
		if(callType.equals("0")){
			intent.setClass(ConnectionService.this,AudioConverseActivity.class);
		}else if(callType.equals("2")){
			//会议
			intent.setClass(ConnectionService.this,ConferenceConverseActivity.class);
		}else{
			//视频电话
			//intent.setClass(ConnectionService.this,VideoCallActivity.class);
		}
		intent.putExtra("phoneNumber", callerNumber).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("inCall", true);
		intent.putExtra("nickName", nickName);
		startActivity(intent);
	}

	
	/**
	 * 接收新消息
	 */
	@Override
	public void onReceiveUcsMessage(UcsReason reason ,UcsMessage message) {
		if(reason.getReason() == 0){
			ArrayList<UcsMessage> arrayList = IMMessageActivity.msgList.get(message.getFormuid());
			if(arrayList == null){
				arrayList = new ArrayList<UcsMessage>();
				IMMessageActivity.msgList.put(message.getFormuid(), arrayList);
			}
			if(!arrayList.contains(message)){
				arrayList.add(message);
			}
			CustomLog.v(DfineAction.TAG_TCP,"收消息:"+message.toJSON());
			sendBroadcast(new Intent(IMMessageActivity.ACTION));
		}else{
			CustomLog.v(DfineAction.TAG_TCP,"下载文件失败:"+reason.getReason());
		}
		
	}

	/**
	 * 消息是发送成功
	 */
	@Override
	public void onSendUcsMessage(UcsReason reason, UcsMessage message) {
		if(message != null){
			ArrayList<UcsMessage> arrayList = IMMessageActivity.msgList.get(message.getTouid());
			if(arrayList == null){
				arrayList = new ArrayList<UcsMessage>();
				IMMessageActivity.msgList.put(message.getTouid(), arrayList);
			}
			if(!arrayList.contains(message)){
				arrayList.add(message);
			}
			CustomLog.v(DfineAction.TAG_TCP,"发消息:"+message.toJSON());
			sendBroadcast(new Intent(IMMessageActivity.ACTION));
		}else{
			if(reason != null){
				CustomLog.v(DfineAction.TAG_TCP,"消息发送:"+reason.getReason());
				sendBroadcast(new Intent(IMMessageActivity.ACTION_MSG).putExtra(UIDfineAction.REASON_KEY, reason.getReason()));
			}
		}
	}

	//发送附件进度回调
	@Override
	public void onSendFileProgress(int progress) {
		CustomLog.d(DfineAction.TAG_TCP,"发送文件进度:"+progress);
		sendBroadcast(new Intent(UIDfineAction.ACTION_SEND_FILE_PROGRESS).putExtra(UIDfineAction.RESULT_KEY, progress));
	}

	//回拨请求成功回调
	@Override
	public void onCallBackSuccess() {
		CustomLog.d(DfineAction.TAG_TCP,"回拨请求成功 ... ");
		sendBroadcast(new Intent(UIDfineAction.ACTION_CALL_BACK));
	}

	//文件下载完成回调
	@Override
	public void onDownloadAttachedProgress(String msgId, String filePaht,int sizeProgrss, int currentProgress) {
		CustomLog.d(DfineAction.TAG_TCP,"下载文件进度:"+sizeProgrss+"    "+currentProgress);
		if(currentProgress >= sizeProgrss){
			CustomLog.d(DfineAction.TAG_TCP,"下载文件完成:"+filePaht);
			for(String key:IMMessageActivity.msgList.keySet()){
				ArrayList<UcsMessage> arrayList = IMMessageActivity.msgList.get(key);
				for(UcsMessage msg :arrayList){
					if(msg.getMsgId().equals(msgId) && msg.getType() != UCSMessage.SEND){
						msg.setMsg(filePaht);
						break;
					}
				}
			}
			sendBroadcast(new Intent(IMMessageActivity.ACTION));
		}
	}

	//获得用户状态回调
	@Override
	public void onUserState(ArrayList list) {
		for(int i = 0 ; i < list.size() ; i ++){
			UcsStatus status = (UcsStatus)list.get(i);
			mapstatus.put(status.getUid(), status);
		}
		sendBroadcast(new Intent(IMMessageActivity.ACTION_STATUS));
	}

	//会议切换通知 普通通话主动或被动切换到会议模式
	@Override
	public void onConferenceModeConvert(String callId) {
		CustomLog.d(DfineAction.TAG_TCP,"会议切换通知  ... ");
	}


	//会议状态回调 包含两个变量：会议状态conferenceState和加入的成员joinConferenceNumber，这个回调函数中可以获得每个成员的状态
	@Override
	public void onConferenceState(String callId, ArrayList conferencestates) {
		ArrayList<ConferenceInfo> states = conferencestates;
		for(int i = 0 ; i < states.size() ; i ++){
			ConferenceState conference = states.get(i).getConferenceState();
			switch(conference){
			case UNKNOWN:
				CustomLog.d(DfineAction.TAG_TCP,states.get(i).getJoinConferenceNumber()+"处于未知状态 ...");
				break;
			case CALLING:
				CustomLog.d(DfineAction.TAG_TCP,"正在呼叫"+states.get(i).getJoinConferenceNumber());
				break;
			case RINGING:
				CustomLog.d(DfineAction.TAG_TCP,states.get(i).getJoinConferenceNumber()+"正在向铃 ... ");
				break;
			case JOINCONFERENCE:
				CustomLog.d(DfineAction.TAG_TCP,states.get(i).getJoinConferenceNumber()+"加入会议  ... ");
				break;
			case EXITCONFERENCE:
				CustomLog.d(DfineAction.TAG_TCP,states.get(i).getJoinConferenceNumber()+"退出会议  ... ");
				break;
			case OFFLINE:
				CustomLog.d(DfineAction.TAG_TCP,states.get(i).getJoinConferenceNumber()+"不在线  ... ");
				break;
			}
		}
		sendBroadcast(new Intent(ConferenceConverseActivity.ACTION_MEETING_STATES).putExtra("states", states));
	}
}
