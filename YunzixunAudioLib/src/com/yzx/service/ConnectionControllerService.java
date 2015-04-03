package com.yzx.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bson.BSON;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.api.ConferenceInfo;
import com.yzx.api.ConferenceState;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSMessage;
import com.yzx.api.UCSService;
import com.yzx.http.HttpTools;
import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.listenerInterface.AudioDeviceUpdateListener;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.LoginListener;
import com.yzx.listenerInterface.MessageListener;
import com.yzx.listenerInterface.ReportListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.listenerInterface.UploadProgressListener;
import com.yzx.preference.UserData;
import com.yzx.tcp.AlarmTools;
import com.yzx.tcp.ConnectConfig;
import com.yzx.tcp.SaveConfig;
import com.yzx.tcp.TcpConnection;
import com.yzx.tcp.TcpTools;
import com.yzx.tcp.packet.CallPacket;
import com.yzx.tcp.packet.DataPacket;
import com.yzx.tcp.packet.MessageTools;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tcp.packet.UcsMessage;
import com.yzx.tools.CustomLog;
import com.yzx.tools.FileTools;
import com.yzx.tools.NetWorkTools;
import com.yzx.tcp.PacketTools;
import com.yzx.tools.CpsTools;
import com.yzx.tools.CrashHandler;
import com.yzx.tools.DevicesReportTools;
import com.yzx.tools.DevicesTools;
import com.yzx.tools.EmodelTools;
import com.yzx.tools.ErrorCodeReportTools;
import com.yzx.tools.ForwardingTools;
import com.yzx.tools.NotifyAudioDeviceUpdate;
import com.yzx.tools.PhoneNumberTools;
import com.yzx.tools.ServiceConfigTools;
import com.yzx.tools.SignTools;
import com.yzx.tools.SystemMediaConfig;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;

/**
 * SDK核心控制服务
 * @author xiaozhenhua
 *
 */
public class ConnectionControllerService extends Service implements ConnectionListener,UGoManager.IUGoCallbacks,AudioDeviceUpdateListener{

	
	public static Context mContext;
	public static ConnectionControllerService ccs;
	public static Context getInstance(){
		return mContext != null ? mContext : ccs;
	}
	
	public interface StartSetviceListener {
		public void onStartServiceSuccess();
	}
	
	//public static ConnectionControllerService.StartSetviceListener startServiceListsner;
	//public static void addStartServiceListener(ConnectionControllerService.StartSetviceListener ssl){
	//	startServiceListsner = ssl;
	//}
	//private HashMap<String,String> meeting = new HashMap<String,String>();
	public interface sendMessageListener{
		public void onSendMessage(String msgId);
	}
	public static ConnectionControllerService.sendMessageListener sendListener;
	public static void addSendMessageListener(ConnectionControllerService.sendMessageListener snl){
		sendListener = snl;
	}
	
	private Object obj = new Object();
	
	public static void startCurrentService(Context mC,boolean isSwitch){
		if(mC != null){
			CustomLog.v("the start of dormancy, waiting for the service to started   ... ");
			mContext = mC;
			UserData.saveLogSwitch(mC,isSwitch);
			initPackage(mC);
			mC.startService(new Intent(mC,ConnectionControllerService.class));
		}
	}
	
	public static void stopCurrentService(){
		TcpTools.tcpDisconnection();
		UserData.cleanPreference();
	}
	
	public Timer timer ;		//来电或去电超时定时器
	public Timer answerTimer ;		//通话计时器
	//private int notifyConferenceSize = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	public static void initPackage(Context mContext){
		try {
			UserData.saveVersionName(mContext,mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
			UserData.savePackageName(mContext,mContext.getPackageName());
			PacketDfineAction.initAction(UserData.getPackageName(mContext));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void init(){
		TcpConnection.addConnectionListener(ConnectionControllerService.this);

		FileTools.createFolder();
		DevicesTools.initIsDoubleTelephone(ConnectionControllerService.this);
		CpsTools.setCpsAudioAdapterParam();
		CpsTools.setCpsDefPermission(ConnectionControllerService.this);

		ForwardingTools.getInstance().initForwarding(ConnectionControllerService.this);
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		CustomLog.v("onCreate ... ");
		CrashHandler.getInstance().init();
		ccs = this;
		//meeting.clear();
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			/*StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());*/
		}
		init();
		//UserData.saveCurrentConnect(ConnectionControllerService.this, true);
		SystemMediaConfig.initMediaConfig(null, this);
		
		NotifyAudioDeviceUpdate.addAudioDeviceUpdateListener(this);
		UGoManager.getInstance().pub_UGoCallbacks(this);
		
		IntentFilter ift = new IntentFilter();
		ift.addAction(PacketDfineAction.INTENT_ACTION_LOGIN);
		ift.addAction(PacketDfineAction.INTENT_ACTION_CS);
		ift.addAction(PacketDfineAction.INTENT_ACTION_SETCONFIG);
		ift.addAction(PacketDfineAction.INTENT_ACTION_DIAL);
		ift.addAction(PacketDfineAction.INTENT_ACTION_HANDUP);
		ift.addAction(PacketDfineAction.INTENT_ACTION_ANSWER);
		ift.addAction(PacketDfineAction.INTENT_ACTION_DTMF);
		ift.addAction(PacketDfineAction.INTENT_ACTION_CONNECT);
		ift.addAction(PacketDfineAction.INTENT_ACTION_SEND_MESSAGE);
		ift.addAction(PacketDfineAction.INTENT_ACTION_MIC_MUTE);
		ift.addAction(PacketDfineAction.INTENT_ACTION_INIT);
		ift.addAction(PacketDfineAction.INTENT_ACTION_QUERY_STATE);
		ift.addAction(PacketDfineAction.INTENT_ACTION_CONFERENCEDIAL);
		ift.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		ift.addAction(PacketDfineAction.INTENT_ACTION_LOG);
		ift.addAction(PacketDfineAction.INTENT_ACTION_FORWARDING);

		ift.addAction(PacketDfineAction.INTENT_ACTION_AD_ADAPTER);
		ift.addAction(PacketDfineAction.INTENT_ACTION_ADAPTER_OK);
		
		registerReceiver(br, ift);
		
		UGo_device_init();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		CustomLog.v("service started successfully ...");
		sendBroadcast(new Intent(UCSService.ACTION_INIT_SUCCESS));
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		TcpTools.tcpDisconnection();
		unregisterReceiver(br);
		UCSCall.getCallStateListener().clear();
		ForwardingTools.getInstance().cancalForwardListener();
		UGo_destory();
		CustomLog.v("onDestroy ... ");
		super.onDestroy();
	}

	private synchronized void checkNetWork(){
		if(NetWorkTools.getCurrentNetWorkType(ConnectionControllerService.this) == NetWorkTools.NETWORK_ON){
			CustomLog.v("NETWORK DISCONNECT  ... ");
			TcpTools.tcpDisconnection();
			//报告无网络连接
			for(ConnectionListener cl:TcpConnection.getConnectionListener()){
				cl.onConnectionFailed(new UcsReason().setReason(300318).setMsg(""));
			}
		}else{
			CustomLog.v("HAS MESSAGES:"+(!mHandler.hasMessages(1)));
			if(!mHandler.hasMessages(1)){
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(UserData.getAccountSid() != null && UserData.getAccountSid().length() > 0
								&& UserData.getClientPwd() != null && UserData.getClientPwd().length() > 0){
							CustomLog.v("NETWORK CONNECT LOGIN ... ");
							ConnectConfig config = new ConnectConfig();
							config.setHost(UserData.getHost());
							config.setPort(UserData.getPort());
							config.setAccountSid(UserData.getAccountSid());
							config.setAccountToken(UserData.getAccountToken());
							config.setClientId(UserData.getClientId());
							config.setClientPwd(UserData.getClientPwd());
							connect(config,false);
						}else if(UserData.getAccountToken() != null && UserData.getAccountToken().length() > 0){
							CustomLog.v("TOKEN CONNECT ... ");
							connect(UserData.getAccountToken(), UserData.getHost(), UserData.getPort());
						}else{
							CustomLog.v("no account information  ... ");
						}
					}
				}, 1000);
			}
		}
	}
	
	public BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			if(TextUtils.equals(intent.getAction(),ConnectivityManager.CONNECTIVITY_ACTION)){
				checkNetWork();
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_LOGIN)){
				if(UserData.getLoginType() == 0){
					ServiceLoginTools.loginAction(UserData.getAccountSid(), UserData.getAccountToken(), UserData.getClientId(), UserData.getClientPwd(),null);
				}else{
					ServiceLoginTools.loginAction(UserData.getAccountToken(),null);
				}
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_CS)){
				ServiceLoginTools.getCsAddress();
			}/*else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_SETCONFIG)){
				UGo_SetConfig();
			}*/else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_CONFERENCEDIAL)){
				dialConference(intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_MEMBERSTR));
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_DIAL)){
				UserData.saveMySelfTimeOutHangup(ConnectionControllerService.this, false);
				dial(intent.hasExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL)?intent.getIntExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, 1):1
						, intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUID) 
						, intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE) ,intent);
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_HANDUP)){
				UserData.saveMySelfTimeOutHangup(ConnectionControllerService.this, false);
				hangUp(intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL));
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_ANSWER)){
				answer(intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL));
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_DTMF)){
				if(intent.hasExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL)){
					sendDTMF(intent.getCharExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, "0".toCharArray()[0]));
				}
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_CONNECT)){
				int type = intent.getIntExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_TYPE, 0);
				UserData.saveLoginType(type);
				//UserData.saveCurrentConnect(ConnectionControllerService.getInstance(), true);
				//CustomLog.v("CONTROLLER_SERVICE:"+type);
				switch(type){
				case 0:
					parseIntent(intent);
					break;
				case 1:
					String token = intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID_PWD);
					String host = null;
					String port = null;
					if(intent.hasExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_HOST) && intent.hasExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_PORT)){
						host = intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_HOST);
						port = intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_PORT); 
					}
					connect(token, host, port);
					break;
				}
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_SEND_MESSAGE)){
				new Thread(new Runnable() {
					@Override
					public void run() {
						int type = intent.getIntExtra(PacketDfineAction.TYPE, 0);
						if(type > 0){
							String touid = intent.getStringExtra(PacketDfineAction.TOUID);
							String msg = "";
							String filePaht = "";
							if(intent.hasExtra(PacketDfineAction.MSG)){
								msg = intent.getStringExtra(PacketDfineAction.MSG);
							}
							if(intent.hasExtra(PacketDfineAction.PATH)){
								filePaht = intent.getStringExtra(PacketDfineAction.PATH);
							}
							if(type == UCSMessage.TEXT || (type >= 10 && type <= 19)){
								if(msg != null && msg.length() <= 500){
									String msgId = sendUcsMessage(touid,msg,type,null,null);
									//CustomLog.v("MSG_SERVICE_MSG_ID:"+msgId);
									if(sendListener != null){
										sendListener.onSendMessage(msgId);
									}
								}else{
									MessageTools.notifyMessages(new UcsReason(300246).setMsg(""),null);
								}
							}else{
								if(filePaht.length() > 0){
									sendUcsFile(touid, filePaht, type);
								}
							}
						}else{
							if(sendListener != null){
								sendListener.onSendMessage("");
							}
						}
					}
				}).start();
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_MIC_MUTE)){
				UGoManager.getInstance().pub_UGoSetMicMute(intent.getBooleanExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_MIC_MUTE,false));
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_QUERY_STATE)){
				String uids = intent.getStringExtra(PacketDfineAction.UID);
				int type = intent.getIntExtra(PacketDfineAction.TYPE,0);
				PacketTools.queryStatus(type,uids);
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_LOG)){
				CustomLog.v("LOG SWITCH INTNET:"+intent.getStringExtra("tag").equals(UserData.getPackageName(ConnectionControllerService.this)));
				if(intent.getStringExtra("tag").equals(UserData.getPackageName(ConnectionControllerService.this))){
					UserData.saveLogToSD(!UserData.isSaveLogToSD());
				}
				CustomLog.v("SWITCH:"+UserData.isSaveLogToSD());
				sendBroadcast(new Intent("com.yunzhixun.action.savelog.response").putExtra("switch", UserData.isSaveLogToSD()).putExtra("tag", UserData.getPackageName(ConnectionControllerService.this)));
			}
			else if (intent.getAction().equals(PacketDfineAction.INTENT_ACTION_AD_ADAPTER)){
				CpsTools.getCpsAdListParam();
			}else if (intent.getAction().equals(PacketDfineAction.INTENT_ACTION_ADAPTER_OK)){
				if (intent.getBooleanExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_ADAPTER_OK,false)){
					CpsTools.postCpsAndroidDeviceParam();
				}else{
					CpsTools.postCpsAdExceptionParam();
				}
			}else if(intent.getAction().equals(PacketDfineAction.INTENT_ACTION_FORWARDING)){
				if(intent.getBooleanExtra(PacketDfineAction.INTENT_ACTION_KEY_FORWARDING,false)){
					CustomLog.v("开启呼转 ... ");
					ForwardingTools.getInstance().openCallForwardingOption(ConnectionControllerService.this);
				}else{
					CustomLog.v("关闭呼转 ... ");
					ForwardingTools.getInstance().closeCallForwardingOption(ConnectionControllerService.this);
				}
			}
		}
	};
	
	
	private void parseIntent(Intent intent){
		String sid = intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID);
		String sidpwd = intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_SID_PWD);
		String clientid = intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CLIEND);
		String cliendpwd = intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CLIEND_PWD);
		ConnectConfig config = new ConnectConfig();
		config.setAccountSid((sid != null && sid.length() > 0 ) ? sid.replace(" ", "") : sid);
		config.setAccountToken((sidpwd != null && sidpwd.length() > 0 ) ? sidpwd.replace(" ", "") : sidpwd);
		config.setClientId(clientid);
		config.setClientPwd(cliendpwd);
		if(intent.hasExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_HOST) && intent.hasExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_PORT)){
			config.setHost(intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_HOST));
			config.setPort(intent.getStringExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_PORT));
		}
		boolean checkClient = intent.getBooleanExtra(PacketDfineAction.INTENT_ACTION_CONNECT_KEY_CHECK_CLIENT, false);
		connect(config,checkClient);
	}
	
	@Override
	public void onConnectionSuccessful() {
		//该处统一向开发者报告连接成功
		mHandler.removeMessages(1);
		//CustomLog.v("开发者报告连接成功  ... ");
		//获取策略参数
		//get cps param---ghj20141021
		ServiceLoginTools.getCpsParam(false);
		initPackage(mContext);//防止切换账号时，获取版本号为空
		
		UGo_SetConfig();
		new Thread(new Runnable() {
			@Override
			public void run() {
				ServiceConfigTools.pingRtpp(UGoAPIParam.getInstance().astRTPSrvCfg, UserData.getRtppAddressList(), 1000);
			}
		}).start();
		
		for(ConnectionListener cl:UCSService.getConnectionListener()){
			cl.onConnectionSuccessful();
		}
		//如果在打电话时检测到socket断连，重连上后会从该处继续发起呼叫
		if(UserData.isCurrentCall()){
			String callmode = UserData.getCurrentCallMode();
			if(callmode.length() > 0 && Integer.parseInt(callmode) > -1){
				if(Integer.parseInt(callmode) == 0){//会议电话
					Intent dial = new Intent(PacketDfineAction.INTENT_ACTION_CONFERENCEDIAL);
					dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MEMBERSTR, UserData.getCurrentCallConference());
					sendBroadcast(dial);
				}else{
					Intent dial = new Intent(PacketDfineAction.INTENT_ACTION_DIAL);
					dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_MODEL, Integer.parseInt(callmode));
					switch (Integer.parseInt(callmode)) {
					//视频点对点 免费通话
					case 3:
					case 6:
						dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUID,UserData.getCurrentCallUid());
						break;
					//直拨
					case 4:
						dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,UserData.getCurrentCallPhone());
						break;
					// 智能拨打
					case 5:
						dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUID,UserData.getCurrentCallUid());
						dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,UserData.getCurrentCallPhone());
						break;
					//回拨
					default:
						dial.putExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLPHONE,UserData.getCurrentCallPhone());
						break;
					}
					sendBroadcast(dial);
				}
			}
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				//上报设备信息
				if(DevicesReportTools.isReportDevicesInfo(ConnectionControllerService.this)){
					DevicesReportTools.reportDevicesInfo(ConnectionControllerService.this, UserData.getClientId(), new ReportListener() {
						@Override
						public void onReportResult(int code, String result) {
							if(code== 0){
								DevicesReportTools.saveReportDevicesInfo(ConnectionControllerService.this,false);
								CustomLog.v("REPORT DEVICES SUCCESS ... ");
							}else{
								CustomLog.v("REPORT DEVICES FAILUER ... ");
								CustomLog.v(code+":"+result);
							}
						}
					});
				}
				
				//上报错误码
				if(ErrorCodeReportTools.isReportErrorCode(ConnectionControllerService.this)){
					if(SharedPreferencesUtils.isLogReportEnable(ConnectionControllerService.this)){
						ErrorCodeReportTools.reportErrorCode(ConnectionControllerService.this, new ReportListener() {
							@Override
							public void onReportResult(int code, String result) {
								if(code == 0){
									ErrorCodeReportTools.cleanErrorCode(ConnectionControllerService.this);
									ErrorCodeReportTools.saveReportErrorCode(ConnectionControllerService.this);
									CustomLog.v("REPORT ERROR_CODE SUCCESS ... ");
								}else{
									CustomLog.v("REPORT ERROR_CODE FAILUER ... ");
									CustomLog.v(code+":"+result);
								}
							}
						});
					}						
					
					//every day fust to get cps param
					//获取策略参数
					//get cps param---ghj20141021
					//CustomLog.v("#####fast to getCpsParam()#####");
					ServiceLoginTools.getCpsParam(true);
					
				}else{
					CustomLog.v("DID NOT REPORT THE ERROR CODE ... ");
				}
				//上报错误日志
				FileTools.searchAndUploadFiles(ConnectionControllerService.this);
			}
		}).start();
		//UserData.saveCurrentConnect(ConnectionControllerService.this, true);
	}


	@Override
	public void onConnectionFailed(UcsReason reason) {
		//该处统一向开发者报告连接失败
		//CustomLog.v("开发者报告连接失败  ...");
		if(UCSCall.getCurrentCallId().length() > 0){
			//断线时自动挂断电话
			//UserData.saveMySelfTimeOutHangup(ConnectionControllerService.this, false);
			//CustomLog.v("CALL END HANGUP ...");
			//UserData.saveNotNetworkHangup(ConnectionControllerService.this,true);
			//hangUp("");
		}
		CustomLog.v("CONNECTION FAILED:"+reason.getReason());
		switch(reason.getReason()){//此五个错误码不在对外公开,为SDK内部重连错误码,由管道损坏或心跳失败发起该错误码
		case 300501:
		case 300503:
		case 300506:
		case 300507:
		case 300508:
			CustomLog.v("1-RE CONNECTION:"+(!mHandler.hasMessages(1)));
			if(!mHandler.hasMessages(1)){
				mHandler.sendEmptyMessageDelayed(1, 4000);
			}
			break;
		default:
			//UserData.saveCurrentConnect(ConnectionControllerService.this, true);
			CustomLog.v("REPORT CONNECT FAILED...");
			for(ConnectionListener cl:UCSService.getConnectionListener()){
				cl.onConnectionFailed(reason);
			}
			break;
		}
		ErrorCodeReportTools.collectionErrorCode(mContext, UserData.getClientId(), "", reason.getReason(), reason.getMsg());
	}
	
	
	private Handler mHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			if(NetWorkTools.isNetWorkConnect(ConnectionControllerService.this)){
				if(msg.what == 1){
					if ((UserData.getAccountSid() != null && UserData.getAccountSid().length() > 0
							&& UserData.getClientPwd() != null && UserData.getClientPwd().length() > 0)
							|| UserData.getAccountToken() != null && UserData.getAccountToken().length() > 0) {
						CustomLog.v("2-RE CONNECTION...");
						TcpTools.reTcpConnection();
					}
				}
			}else{
				CustomLog.v("2-RE DISCONNECTION...");
			}
		}
	};
	
	
	/**
	 * 连接云服务器(token)
	 * @param token
	 * @param host
	 * @param port
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午4:14:30
	 */
	public void connect(final String token ,final String host ,final String port){
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (obj) {
					if(token != null && token.length() > 0){
						ConnectConfig config = new ConnectConfig();
						if(host != null && host.length() > 0 && port != null && port.length() > 0){
							UserData.saveHost(host);
							UserData.savePort(port);
						}else{
							UserData.saveHost("");
							UserData.savePort("");
						}
						config.setAccountToken(token);
						TcpConnection.addConfigListener(SaveConfig.getInstance().setConfig(config));
						long currentLong = System.currentTimeMillis() - UserData.getLoginLastTime();
						currentLong /= 23 * 60 * 60;
						if(!config.getAccountToken().equals(UserData.getAccountToken())){
							CustomLog.v("TOKEN USER LOGIN ... ");
							DevicesReportTools.saveReportDevicesInfo(ConnectionControllerService.this, true);
							ServiceLoginTools.loginAction(token, null);
						}else if(UserData.getImServiceAddress().size() <= 0){
							CustomLog.v("TOKEN GET CS ... ");
							ServiceLoginTools.getCsAddress();
						}else if(currentLong >= 1000){
							CustomLog.v("TOKEN TIMEOUT LOGIN ... ");
							ServiceLoginTools.loginAction(UserData.getAccountToken(), null);
						}else{
							CustomLog.v("TOKEN TCP CONNECT ... ");
							TcpTools.tcpConnection();
						}
					}else{
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							//标识getAccountToken不能为空
							cl.onConnectionFailed(new UcsReason().setReason(300203).setMsg("token can not be empty"));
						}
					}
				}
			}
		}).start();
	}
	
	/**
	 * 连接云服务器
	 * @param accountSid
	 * @param accountToken
	 * @param clientId
	 * @param clientPwd
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午4:41:40
	 */
	public void connect(final ConnectConfig config,final boolean checkClient){
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (obj) {
					if(config.getAccountSid().length() <= 0){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							//标识getAccountSid不能为空
							cl.onConnectionFailed(new UcsReason().setReason(300202).setMsg("accountSid can not be empty"));
						}
					}else if(config.getAccountToken().length() <= 0){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							//标识getAccountToken不能为空
							cl.onConnectionFailed(new UcsReason().setReason(300203).setMsg("accountToken can not be empty"));
						}
					}else if(config.getClientId().length() <= 0){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							//标识getClientId不能为空
							cl.onConnectionFailed(new UcsReason().setReason(300204).setMsg("clientId can not be empty"));
						}
					}else if(config.getClientPwd().length() <= 0){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							//标识getClientPwd不能为空
							cl.onConnectionFailed(new UcsReason().setReason(300205).setMsg("clientPwd can not be empty"));
						}
					}else{
						AlarmTools.stopAll();
						TcpConnection.addConfigListener(SaveConfig.getInstance().setConfig(config));
			
						if(TcpTools.isConnected()){
							TcpTools.tcpDisconnection();
						}
						if(config.getHost().length() > 0 && config.getPort().length() > 0){
							UserData.saveHost(config.getHost());
							UserData.savePort(config.getPort());
						}else{
							UserData.saveHost("");
							UserData.savePort("");
						}
			
						long currentLong = System.currentTimeMillis() - UserData.getLoginLastTime();
						currentLong /= 23 * 60 * 60;
						if(checkClient ||  (!UserData.getAccountSid().equals(config.getAccountSid())
								|| !UserData.getClientId().equals(config.getClientId()))){
							CustomLog.v("USER LOGIN ... ");
							if(!UserData.getClientId().equals(config.getClientId())){
								DevicesReportTools.saveReportDevicesInfo(ConnectionControllerService.this, true);
							}
							ServiceLoginTools.loginAction(config.getAccountSid(), config.getAccountToken(), config.getClientId(), config.getClientPwd(),null);
						}else if(currentLong >= 1000){
							CustomLog.v("TIMEOUT LOGIN ... ");
							ServiceLoginTools.loginAction(UserData.getAccountSid(), UserData.getAccountToken(), UserData.getClientId(), UserData.getClientPwd(),null);
						}else if(UserData.getImServiceAddress().size() <= 0){
							CustomLog.v("GET CS ... ");
							ServiceLoginTools.getCsAddress();
						}else{
							CustomLog.v("TCP CONNECT ... ");
							TcpTools.tcpConnection();
						}
					}
				}
			}
		}).start();
	}
	
	////////////////////////////////////////IM相关////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param receiverId:接收者ID
	 * @param text：消息内容
	 * @param filePath：文件路径
	 * @param expandData：自定义数据
	 * @param extra_mime：消息类型   1:文字   	2:图片	3:音频	
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-14 下午3:00:53
	 */
	public String sendUcsMessage(String toUid, String text, int extra_mime , String fileName,String fileSize) {
		
		UcsMessage pack = (UcsMessage) DataPacket.CreateDataPack(PacketDfineAction.SOCKET_STORAGE_TYPE, PacketDfineAction.SOCKET_STORAGE_TYPE);
		//int uid = Integer.parseInt(to);
		byte resources = 0x00;
		/*if(uid>=8000 && uid <=10000){
			resources = (byte)0x01;
		}*/
		pack.mHeadDataPacket.setTuid(pack.mHeadDataPacket.getLong((short)0x00, (byte)0x00, resources, Long.parseLong(toUid)));
		pack.mHeadDataPacket.setMtp(extra_mime);
		pack.setTouid(toUid);
		pack.setMsg(text);
		pack.setExtra_mime(extra_mime);
		if(extra_mime == UCSMessage.PIC || (extra_mime >= 20 && extra_mime <= 29)){
			pack.setFileName(fileName);
			pack.setFileSize(fileSize);
		}
		//CustomLog.v("SEND_JSON:"+pack.toJSON());
		TcpTools.sendPacket(pack);
		/*for(IMListener messageListener:MessageUtil.getImListenerList()){
			pack.setType(1);
			pack.setSendSuccess(false);
			messageListener.onSendUcsMessage(new Reason(-1), pack);
		}*/
		return pack.getMsgId();
	}
	
	/**
	 * 发送附件
	 * @param toUid
	 * @param filePath
	 * @param extra_mime
	 * @author: xiaozhenhua
	 * @data:2014-5-20 下午4:33:59
	 */
	private void sendUcsFile(final String toUid,final String filePath,final int extra_mime){
		File file = new File(filePath);
		if(file.exists()){
			if(FileTools.isFileSize(filePath)){
				int sn = (int) (Math.round(Math.random() * 1000));
				StringBuffer buffer = new StringBuffer();
				buffer.append(UserData.getHttpUploadFileUrl());
				buffer.append("/v2/upload_multimedia?");
				buffer.append("p=UCS");
				buffer.append("&");
				buffer.append("pv=android");
				buffer.append("&");
				buffer.append("securityver=1");//0:不加密   1:加密
				buffer.append("&");
				buffer.append("sn="+sn);
				buffer.append("&");
				buffer.append("type="+extra_mime);
				buffer.append("&");
				buffer.append("u=5");
				buffer.append("&");
				buffer.append("uid="+UserData.getClientId());
				buffer.append("&");
				buffer.append("v="+UserData.getVersionName(ConnectionControllerService.this));
				buffer.append("&");
				String sign = SignTools.getSign(buffer.toString(),SignTools.Encryption.SHA1);
				buffer.append("sign="+sign);
				
				HashMap<String, ArrayList<File>> fileMap = new HashMap<String, ArrayList<File>>();
				ArrayList<File> fileArray = new ArrayList<File>();
				if (filePath != null && filePath.length() > 0) {
					fileArray.add(new File(filePath));
				}
				fileMap.put("mediafile", fileArray);
				String serverResult = HttpTools.postFile(buffer.toString(), fileMap, new UploadProgressListener() {
					int pr = -1;
					@Override
					public void uploadProgress(int progress) {
						if(pr != progress){
							//CustomLog.v("SERVICE_CURRENT_UPLOAD:"+progress);
							for(MessageListener listener:MessageTools.getMessageListenerList()){
								listener.onSendFileProgress(progress);
							}
						}
					}
				});
				if(serverResult != null){
					try {
						CustomLog.v("UPLOAD_RESPONSE_JSON:"+serverResult);
						JSONObject jsonOuter = new JSONObject(serverResult);
						if(jsonOuter.has(PacketDfineAction.RESULT) && jsonOuter.getInt(PacketDfineAction.RESULT) == 0){
							String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
							String fileSize = FileTools.getFileSize(filePath);
							CustomLog.v("FILE_NAME:"+fileName);
							CustomLog.v("FILE_SIZE:"+fileSize);
							String msgId = sendUcsMessage(toUid,jsonOuter.getString("url"),extra_mime,fileName,fileSize);
							MessageTools.MessageMapFilePaht.put(msgId, filePath);
							CustomLog.v("FILE_SERVICE_MSG_ID:"+msgId);
							if(sendListener != null){
								sendListener.onSendMessage(msgId);
							}
						}else{
							if(jsonOuter.getInt(PacketDfineAction.RESULT) == 10){//如果session过期
								if(UserData.getLoginType() == 0){
									ServiceLoginTools.loginAction(UserData.getAccountSid(), UserData.getAccountToken(), UserData.getClientId(), UserData.getClientPwd(), new LoginListener() {
										@Override
										public void onLoginSuccess() {
											//重发
											sendUcsFile(toUid, filePath, extra_mime);
										}
										@Override
										public void onLoginStateResponse(JSONObject resultJson, Exception e) {
											
										}
										
										@Override
										public void onCSAddressResponse(JSONObject csAddressJson, Exception e) {
											
										}
									});
								}else{
									ServiceLoginTools.loginAction(UserData.getAccountToken(), new LoginListener() {
										@Override
										public void onLoginSuccess() {
											//重发
											sendUcsFile(toUid, filePath, extra_mime);
										}
										
										@Override
										public void onLoginStateResponse(JSONObject resultJson, Exception e) {
											
										}
										
										@Override
										public void onCSAddressResponse(JSONObject csAddressJson, Exception e) {
											
										}
									});
								}
							}else{
								ServiceLoginTools.resultSwitch(jsonOuter.getInt(PacketDfineAction.RESULT));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						MessageTools.notifyMessages(new UcsReason(300229).setMsg(e.toString()),null);
					}
				}else{
					MessageTools.notifyMessages(new UcsReason(300228).setMsg("send file time out"),null);
				}
			}else{
				//文件过大
				MessageTools.notifyMessages(new UcsReason(300227).setMsg("send file failed"),null);
			}
		}else{
			//文件不存在或文件存在中文
			MessageTools.notifyMessages(new UcsReason(300245).setMsg("send file failed"),null);
		}
	}
	
	
	////////////////////////////////////////电话相关////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 拨打电话会议
	 * @param callType
	 * @param mNumner 邀请成员数
	  * @param memberStr 邀请成员uid串，成员之间以","隔开
	 * @author: gonghuojin
	 * @data:2014-10-17 上午10:58:57
	 */
	public void dialConference(String memberStr){
		if(TcpTools.isConnected()){
			CustomLog.v("conference memberstr:"+memberStr);
			UserData.saveCurrentCall(false);
			UserData.saveCurrentCallConference("");
			UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_RTPP_CFG_MODULE_ID,UGoAPIParam.getInstance().astRTPSrvCfg,0);
            UGoAPIParam.getInstance().stConferenceCallDialPara.user_attr = memberStr;
            UGoAPIParam.getInstance().stConferenceCallDialPara.user_num = memberStr.length();
            UGoAPIParam.getInstance().stConferenceCallDialPara.roomname = "";
            UGoAPIParam.getInstance().stConferenceCallDialPara.roompwd = "";
            UGoAPIParam.getInstance().stConferenceCallDialPara.remark = "";
            UGoManager.getInstance().pub_UGoConferenceDial(UGoAPIParam.getInstance().stConferenceCallDialPara, 0);
            CustomLog.v("免费电话会议 ... ");
            /*try {
				JSONArray array = new JSONArray(memberStr);
				//[{"phone":"18688753176","mode":5},{"uid":"63244003000016","mode":5}]
				//[{"phone":"18688753176","mode":5},{"uid":"63244003000016","mode":5}]
				for(int i = 0 ;i < array.length() ; i ++){
					JSONObject json = (JSONObject)array.get(i);
					if(json.has("phone")){
						meeting.put(json.getString("phone"), "");
						CustomLog.v("参会人员:"+json.getString("phone"));
					}else if(json.has("uid")){
						meeting.put(json.getString("uid"), "");
						CustomLog.v("参会人员:"+json.getString("uid"));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
		}else{
			CustomLog.v("免费电话会议:"+memberStr);
			UserData.saveCurrentCall(true);
			UserData.saveCurrentCallMode(0);
			UserData.saveCurrentCallConference(memberStr);
			checkNetWork();
		}
	}

	
	/**
	 * 拨打电话
	 * @param callType
	 * @param calledUid
	 * @param calledPhone
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午10:58:57
	 */
	public void dial(int callType,String calledUid,String calledPhone, Intent intent){
		if(TcpTools.isConnected()){
			if(PhoneNumberTools.isNumber(calledUid, calledPhone)){
				CustomLog.v("CURRENT_CALL_Uid:"+calledUid);
				CustomLog.v("CURRENT_CALL_PHONE:"+calledPhone);
				UserData.saveCurrentCall(false);
				UserData.saveCurrentCallUid(""); 
				UserData.saveCurrentCallPhone("");
				if(callType == 6){
					//UGo_SetConfig();
					if(!calledUid.equals(UserData.getClientId())){
						if(NetWorkTools.getCurrentNetWorkType(ConnectionControllerService.getInstance().getApplicationContext()) == 2){
							notifyDialFailed(new UcsReason(300267).setMsg(""));
							return;
						}
						UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_RTPP_CFG_MODULE_ID,UGoAPIParam.getInstance().astRTPSrvCfg,0);
						CustomLog.v("免费电话 ... ");
						UGoAPIParam.getInstance().stCallDialPara.mode = callType;
						UGoAPIParam.getInstance().stCallDialPara.phone = "";
						UGoAPIParam.getInstance().stCallDialPara.uid = calledUid;
						UGoAPIParam.getInstance().stCallDialPara.video_enable = 0;
						UGoAPIParam.getInstance().stCallDialPara.ucalltype = 0;
						UGoAPIParam.getInstance().stCallDialPara.userdata = intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUSERDATA);
						UGoAPIParam.getInstance().stCallDialPara.fnickname = UserData.getNickName();
						UGoManager.getInstance().pub_UGoDial(UGoAPIParam.getInstance().stCallDialPara, 0);
						UserData.saveCallTimeoutTime(ConnectionControllerService.this,115000);
					}else{
						notifyHangUp(new UcsReason(300219).setMsg(""));
					}
				}else if(callType == 5){
					if(NetWorkTools.getCurrentNetWorkType(ConnectionControllerService.getInstance().getApplicationContext()) == 2){
						notifyDialFailed(new UcsReason(300267).setMsg(""));
						return;
					}
					UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_RTPP_CFG_MODULE_ID,UGoAPIParam.getInstance().astRTPSrvCfg,0);
					CustomLog.v("智能呼叫 ... ");
					UGoAPIParam.getInstance().stCallDialPara.mode = callType;
					UGoAPIParam.getInstance().stCallDialPara.phone = calledPhone;
					UGoAPIParam.getInstance().stCallDialPara.uid = calledUid;
					UGoAPIParam.getInstance().stCallDialPara.video_enable = 0;
					UGoAPIParam.getInstance().stCallDialPara.fnickname = UserData.getNickName();
					UGoAPIParam.getInstance().stCallDialPara.userdata = intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUSERDATA);
					UGoManager.getInstance().pub_UGoDial(UGoAPIParam.getInstance().stCallDialPara, 0);
					UserData.saveCallTimeoutTime(ConnectionControllerService.this,95000);
				}else if(callType == 4){
					if(NetWorkTools.getCurrentNetWorkType(ConnectionControllerService.getInstance().getApplicationContext()) == 2){
						notifyDialFailed(new UcsReason(300267).setMsg(""));
						return;
					}
					UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_RTPP_CFG_MODULE_ID,UGoAPIParam.getInstance().astRTPSrvCfg,0);
					CustomLog.v("直拨电话 ... ");
					UGoAPIParam.getInstance().stCallDialPara.mode = callType;
					UGoAPIParam.getInstance().stCallDialPara.phone = calledPhone;
					UGoAPIParam.getInstance().stCallDialPara.uid = "";
					UGoAPIParam.getInstance().stCallDialPara.video_enable = 0;
					UGoAPIParam.getInstance().stCallDialPara.fnickname = UserData.getNickName();
					UGoAPIParam.getInstance().stCallDialPara.userdata = intent.getStringExtra(PacketDfineAction.KEY_INTENT_CONFIG_CALLUSERDATA);
					UGoManager.getInstance().pub_UGoDial(UGoAPIParam.getInstance().stCallDialPara, 0);
					UserData.saveCallTimeoutTime(ConnectionControllerService.this,95000);
				}/*else if(mode == 3){
					if(UserData.getCpuType() >= 7){
						//UGo_SetConfig();
						UGoManager.getInstance(ConnectionControllerService.this).pub_UGoSetConfig(UGoAPIParam.UGO_RTPP_CFG_MODULE_ID,UGoAPIParam.getInstance().astRTPSrvCfg,0);
						CustomLog.v("视频电话 ... ");
						UGoAPIParam.getInstance().stCallDialPara.mode = 6;
						UGoAPIParam.getInstance().stCallDialPara.phone = "";
						UGoAPIParam.getInstance().stCallDialPara.uid = calledUid;
						UGoAPIParam.getInstance().stCallDialPara.video_enable = 1;
						UGoManager.getInstance(ConnectionControllerService.this).pub_UGoDial(UGoAPIParam.getInstance().stCallDialPara, 0);
						UserData.saveCallMode(callType);
					}else{
						notifyDialFailed(new UcsReason(300249).setMsg("the device does not support video calls"));
					}
				}*/else{
					//回拨
					if(UserData.getAc().length() > 0){
						if(UserData.getPhoneNumber().length() > 0){
							//CustomLog.v("CURRENT_CALL_BACK_PHONE:"+UserData.getPhoneNumber());
							if(PhoneNumberTools.checkMobilePhoneNumber(UserData.getPhoneNumber())){
								String fromSerNum = "";
								String toSerNum = "";
								if(intent.hasExtra(PacketDfineAction.FROM_SER_NUM)){
									fromSerNum = intent.getStringExtra(PacketDfineAction.FROM_SER_NUM);
								}
								if(intent.hasExtra(PacketDfineAction.TO_SER_NUM)){
									toSerNum = intent.getStringExtra(PacketDfineAction.TO_SER_NUM);
								}
								callBack(calledPhone,fromSerNum,toSerNum);
								CustomLog.v("回拨电话 ... ");
							}else{
								notifyDialFailed(new UcsReason(300234).setMsg(""));
							}
						}else{
							notifyDialFailed(new UcsReason(300233).setMsg(""));
						}
					}else{
						notifyDialFailed(new UcsReason(300235).setMsg(""));
					}
				}
			}else{
				//被叫号码错误
				switchEvent(UGoAPIParam.eUGo_CALLDIALING_EV,UGoAPIParam.eUGo_Reason_TooShort,"");
			}
		}else{
			UserData.saveCurrentCall(true);
			UserData.saveCurrentCallMode(callType);
			UserData.saveCurrentCallUid(calledUid);
			UserData.saveCurrentCallPhone(calledPhone);
			checkNetWork();
		}
	}
	
	
	/**
	 * 回拨
	 * @param calledNumner
	 * @author: xiaozhenhua
	 * @data:2014-5-23 下午5:38:46
	 */
	public void callBack(String calledPhone,String fromSerNum,String toSerMum){
		StringBuilder builder = new StringBuilder();
		builder.append(UserData.getHttpCallBackUrl());
		builder.append("v2/callback?").append("brandid=yzx").append("&sn=").append(SignTools.getSn())
				.append("&called=").append(calledPhone).append("&securityver=1")
				.append("&uid=").append(UserData.getClientId())
				.append("&p=").append(UserData.getPackageName(ConnectionControllerService.this))
				.append("&pv=").append("android")
				.append("&v=").append(UserData.getVersionName(ConnectionControllerService.this))
				.append("&u=").append("5");
		if(fromSerNum != null && fromSerNum.length() > 0){
			builder.append("&fromsernum=").append(fromSerNum);
		}
		if(toSerMum != null && toSerMum.length() > 0){
			builder.append("&tosernum=").append(toSerMum);
		}
		String sign = SignTools.getSign(builder.toString(), SignTools.Encryption.SHA1);
		builder.append("&sign=").append(sign);
		
		CustomLog.v("CALL_BACK_URL:"+builder.toString());
		final String url = builder.toString();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject json = HttpTools.doGetMethod(url,UserData.getAc());
					if(json != null){
						//CustomLog.v("CALL_BACK_RESPONSE_JSON:"+json.toString());
						switch(json.getInt(PacketDfineAction.RESULT)){
						case 0:
							for (CallStateListener csl : UCSCall.getCallStateListener()) {
								csl.onCallBackSuccess();
							}
							break;
						case -1:
						case 1:
						case 10:
							UserData.saveAc("");
							sendBroadcast(new Intent(PacketDfineAction.INTENT_ACTION_LOGIN));
							notifyDialFailed(new UcsReason(300010).setMsg("session timo out"));
							break;
						case 2:
							notifyDialFailed(new UcsReason(300239).setMsg(""));
							break;
						case 3:
							notifyDialFailed(new UcsReason(300240).setMsg(""));
							break;
						case 4:
							notifyDialFailed(new UcsReason(300241).setMsg(""));
							break;
						case 5:
							notifyDialFailed(new UcsReason(300233).setMsg(""));
							break;
						case 6:
							notifyDialFailed(new UcsReason(300211).setMsg(""));
							break;
						case 11:
							notifyDialFailed(new UcsReason(300242).setMsg(""));
							break;
						case 107:
							notifyDialFailed(new UcsReason(300219).setMsg(""));
							break;
						case 999:
							notifyDialFailed(new UcsReason(300243).setMsg(""));
							break;
						}
					}else{
						notifyDialFailed(new UcsReason(300238).setMsg(""));
					}
				} catch (IOException e) {
					e.printStackTrace();
					notifyDialFailed(new UcsReason(300236).setMsg(""));
				} catch (JSONException e) {
					e.printStackTrace();
					notifyDialFailed(new UcsReason(300237).setMsg(""));
				}
			}
		}).start();
	}

	
	/**
	 * 挂断电话
	 * @param callId
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午11:01:42
	 */
	public void hangUp(String callId){
		UGoManager.getInstance().pub_UGoHangup(UGoAPIParam.eUGo_Reason_HungupMyself);
	}
	
	/**
	 * 接听电话
	 * @param callId
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午11:04:54
	 */
	public void answer(String callId){
		//notifyConferenceSize = 0 ;
		UGoManager.getInstance().pub_UGoAnswer();
	}
	
	/**
	 * 发送DTMF
	 * @param dtmf
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午11:08:34
	 */
	public void sendDTMF(char dtmf){
		UGoManager.getInstance().pub_UGoSendDTMF(dtmf);
	}
	
	/**
	 * 初始化电话组件
	 * @param mContext
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午10:34:47
	 */
	public void UGo_device_init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// UGoManager.getInstance().pub_UGoSetLogFile(0xffff,"/storage/sdcard0/yunzhixun/lingphone_log.txt");
				int media_result = UGoManager.getInstance().pub_UGoLoadMediaEngine(0);
				// UGoManager.getInstance().pub_UGoSetApi(Integer.parseInt(android.os.Build.VERSION.SDK));
				CustomLog.v((media_result == 0 ? "媒体组件初始化成功:" : "媒体组件初始化失败:") + media_result);
				UGoManager.getInstance().pub_setAndroidContext(getApplicationContext());
				int init_result = UGoManager.getInstance().pub_UGoInit();
				CustomLog.v((init_result == 0 ? "UGo组件初始化成功:" : "UGo组件初始化失败:") + init_result);
				
				//UGo_SetConfig();
			}
		}).start();
	}
	
	public void UGo_SetConfig() {
		
		String stunAdd = UserData.getStunAddressList(ConnectionControllerService.this);
		//CustomLog.v("STUN_ADD:"+stunAdd);
		UGoAPIParam.getInstance().stIceCfg.ice_enabled = UserData.isIceEnable(ConnectionControllerService.this);
		UGoAPIParam.getInstance().stIceCfg.stun_server = stunAdd.length() > 0 ? stunAdd: "stun.softjoys.com:3478";
		int ice_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_CFG_ICE_MODULE_ID,UGoAPIParam.getInstance().stIceCfg,0);
		CustomLog.v((ice_config_result == 0 ? "ICE配置成功:" : "ICE配置失败:") + ice_config_result);
	    
		boolean autoadapter = UserData.isAudioAutoAdapter(ConnectionControllerService.this);
		int audiofec = UserData.getAudioFec(ConnectionControllerService.this);
	    int vqmenable = UserData.getVpmEnable(ConnectionControllerService.this);
	    int prtpenable = UserData.getPrtpEnable(ConnectionControllerService.this);
		
		CpsTools.setDynamicPolicyEnable(autoadapter);
		UGoAPIParam.getInstance().stMediaCfg.ucEmodelEnable = vqmenable;
		UGoAPIParam.getInstance().stMediaCfg.ucFecEnable = audiofec;
		UGoAPIParam.getInstance().stMediaCfg.ucRealTimeType = prtpenable;
		int media_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_CTRL_CFG_MODULE_ID,UGoAPIParam.getInstance().stMediaCfg,0);
		CustomLog.v((media_config_result == 0 ? "MediaCfg配置成功:" : "Media配置失败:") + media_config_result);
		CustomLog.v("iceenable="+UserData.isIceEnable(ConnectionControllerService.this)+"  autoadapter="+autoadapter+"  audiofec="+audiofec+"  vqmenable="+vqmenable+"  prtpenable="+prtpenable);

		UGoAPIParam.getInstance().stRTPCfg.uiRTPTimeout =20;
		UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_RTP_CFG_MODULE_ID,UGoAPIParam.getInstance().stRTPCfg,0);
		
		UGoAPIParam.getInstance().stTcpCfg.tcp_enabled = false;
		if (UserData.getImServiceAddress().size() > 0) {
			UGoAPIParam.getInstance().stTcpCfg.tcp_srvaddr = UserData.getImServiceAddress().get(0);
		}
	
		UGoAPIParam.getInstance().stUGoCfg.rc4_enabled = false;
		UGoAPIParam.getInstance().stUGoCfg.video_enabled = false;
		UGoAPIParam.getInstance().stUGoCfg.platform = 0x04;
		UGoAPIParam.getInstance().stUGoCfg.brand = "yzx_"+UserData.getPackageName(ConnectionControllerService.this);
		UGoAPIParam.getInstance().stUGoCfg.phone = UserData.getPhoneNumber();
		UGoAPIParam.getInstance().stUGoCfg.uid = UserData.getClientId();
		CustomLog.v("CURRENT_LOGIN_CLIENTID:"+UGoAPIParam.getInstance().stUGoCfg.uid);
		CustomLog.v("CURRENT_LOGIN_PHONE:"+UGoAPIParam.getInstance().stUGoCfg.phone);
		
		UGoManager.getInstance().pub_UGoDebugEnabled(false);
		int ugo_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_CFG_PARAM_MODULE_ID,UGoAPIParam.getInstance().stUGoCfg,0);
		CustomLog.v((ugo_config_result == 0 ? "UGO配置成功:" : "UGO配置失败:") + ugo_config_result);
		//int tcp_config_result = UGoManager.getInstance(ConnectionControllerService.this).pub_UGoSetConfig(UGoAPIParam.UGO_CFG_TCP_MODULE_ID,UGoAPIParam.getInstance().stTcpCfg,0);
		//CustomLog.v((tcp_config_result == 0 ? "TCP配置成功:" : "TCP配置失败:") + tcp_config_result);
		
		//UGoAPIParam.getInstance().stLogTracePara.level = 0x0004+0x08;
		//UGoAPIParam.getInstance().stLogTracePara.path = FileTools.getSdCardFilePath()+"/webrtc_log_info.txt";
		//int log_config = UGoManager.getInstance(ConnectionControllerService.this).pub_UGoSetLogFile(UGoAPIParam.getInstance().stLogTracePara, 0);
		//CustomLog.v((log_config == 0 ? "LOG配置成功:" : "LOG配置失败:") + log_config);
		CustomLog.v("PHONE_VERSION:"+UGoManager.getInstance().pub_UGoGetVersion());
	}
	
	public void UGo_destory() {
		int destroy = UGoManager.getInstance().pub_UGoDestroy();
		CustomLog.v((destroy == 0 ? "卸载UGo成功:" : "卸载UGo失败:") + destroy);
	}
	

	@Override
	public void traceCallback(String summary, String detail, int level) {
		CustomLog.v( "TRACE_CALL_BACK:" + "  summary:" + summary + "   detail:" + detail + "   level:" + level);
		FileTools.saveExLog(" summary:" + summary + "   detail:" + detail + "   level:" + level,"YZX_trace_");
	}


	@Override
	public void sendCallback(byte[] message, int len) {
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
		CustomLog.v( "UGO SEND CONVERT_HEAD:" + head);
		// 数据区
		byte[] data = new byte[length];
		j = 0;
		for (int i = (4 + headLength); i < len; i++) {
			data[j] = message[i];
			j++;
		}
		CustomLog.v( "UGO SEND CONVERT_DATA:" + new String(data));
		CallPacket callDataPack = new CallPacket();
		callDataPack.setHead(BSON.decode(header));
		callDataPack.setServer(head);
		callDataPack.setJson(new String(data));
		TcpTools.sendPacket(callDataPack);
	}

	@Override
	public void eventCallback(int ev_type, int ev_reason, String message, String param) {
		switchEvent(ev_type, ev_reason, param);
	}
	
	private void switchEvent(int event, int reason, String param) {
		CustomLog.v( "switchEvent event = " + event + ",  reason = " + reason + ",  param = " + param);
		switch (event) {
		case UGoAPIParam.eUGo_CONFERENCE_EV:// eUGo_CONFERENCE_EV(会议呼叫事件)
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Success://会议呼叫成功
				CustomLog.v("会议接通  ... ");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onAnswer(UCSCall.getCurrentCallId());
				}
				UserData.saveAnswer(ConnectionControllerService.this, true);
				break;
			case UGoAPIParam.eUGo_Reason_Connecting:
				CustomLog.v("正在接通对方 ... ");
				if(param != null && param.length() > 0){
					try {
						JSONObject json = new JSONObject(param);
						if (json.has("callid")) {
							UCSCall.setCurrentCallId(json.getString("callid"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case UGoAPIParam.eUGo_Reason_StateNotify://会议状态通知
				CustomLog.v("会议通知 ... ");
				/*if(mHandler.hasMessages(2)){
					CustomLog.v("转换会议... ");
					mHandler.removeMessages(2);
					notifyConferenceModeConvert();
				}*/
				
				try {
					JSONObject object = new JSONObject(param);
					JSONArray objArr = object.getJSONArray("parties");
					ArrayList<ConferenceInfo> list=new ArrayList<ConferenceInfo>();
					for(int i = 0; i < objArr.length(); i++) {
						JSONObject obj = objArr.getJSONObject(i);
						String uid = obj.getString("uid");
						String phone = obj.getString("phone");
						
						ConferenceInfo cstate = new ConferenceInfo();
						switch(obj.getInt("state")){
						case 0:
							cstate.setConferenceState(ConferenceState.UNKNOWN);
							break;
						case 1:
							cstate.setConferenceState(ConferenceState.CALLING);
							break;
						case 2:
							cstate.setConferenceState(ConferenceState.RINGING);
							break;
						case 3:
							cstate.setConferenceState(ConferenceState.JOINCONFERENCE);
							break;
						case 4:
							cstate.setConferenceState(ConferenceState.EXITCONFERENCE);
							break;
						}
						if(uid != null && uid.length() > 0 
								&& !uid.equals(UserData.getClientId())){//过滤掉自己的状态
							cstate.setJoinConferenceNumber(uid);
							/*if(notifyConferenceSize == 0){
								meeting.put(uid, "");
							}else if(objArr.length() > meeting.size()){
								meeting.put(uid, "");
								CustomLog.v("会议中加人UID:"+uid);
							}*/
							list.add(cstate);
						}else if(phone != null && phone.length() > 0
								&& !phone.equals(UserData.getPhoneNumber())){//过滤掉自己的状态
							cstate.setJoinConferenceNumber(phone);
							/*if(notifyConferenceSize == 0){
								meeting.put(phone, "");
							}else if(objArr.length() > meeting.size()){
								meeting.put(phone, "");
								CustomLog.v("会议中加人PHONE:"+phone);
							}*/
							list.add(cstate);
						}
					}
					/*notifyConferenceSize = notifyConferenceSize + 1;
					HashMap<String,String> notMeeting = new HashMap<String, String>();
					if(list.size() != meeting.size()){
						for(String key:meeting.keySet()){
							boolean isExitMeeting = true;
							for(ConferenceInfo meet:list){
								if(meet.getJoinConferenceNumber().equals(key)){
									isExitMeeting = false;
									break;
								}
							}
							if(isExitMeeting){
								notMeeting.put(key, null);
							}
						}
					}
					for(String key:notMeeting.keySet()){
						ConferenceInfo cstate = new ConferenceInfo();
						cstate.setJoinConferenceNumber(key);
						cstate.setConferenceState(ConferenceState.EXITCONFERENCE);
						list.add(cstate);
						meeting.remove(key);
					}
					notMeeting.clear();*/
					for (int i = 0; i < list.size() - 1; i++) {
						for (int j = 0; j < list.size() - i - 1; j++) {
							if (Long.parseLong(list.get(j).getJoinConferenceNumber()) > Long.parseLong(list.get(j+1).getJoinConferenceNumber())) {
								ConferenceInfo temp = list.get(j);
								list.set(j,list.get(j + 1));
								list.set(j+1,temp);
							}
						}
					}
					CustomLog.v("参会人数："+list.size());
					for (CallStateListener csl : UCSCall.getCallStateListener()) {
						csl.onChatRoomState(UCSCall.getCurrentCallId(),list);
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			 	break;
			case UGoAPIParam.eUGo_Reason_ActiveModeConvert:// 普通通话主动切换到会议模式
				CustomLog.v("主动切换为会议模式 ... ");
				notifyConferenceModeConvert();
				break;
			case UGoAPIParam.eUGo_Reason_PassiveModeConvert:// 普通通话被动切换到会议模式
				CustomLog.v("被动切换为会议模式 ... ");
				notifyConferenceModeConvert();
				break;
			case UGoAPIParam.eUGo_Reason_CONF_NO_EXIST:
				CustomLog.v( "会议不存在 ... ");
				stopCallTimer();
				notifyDialFailed(new UcsReason(300301).setMsg("conference is not exit"));
				break;
			case UGoAPIParam.eUGo_Reason_CONF_STATE_ERR:
				CustomLog.v( "会议状态异常 ... ");
				notifyDialFailed(new UcsReason(300302).setMsg("conference state error"));
				break;
			case UGoAPIParam.eUGo_Reason_CONF_FULL:
				CustomLog.v( "会议容量满 ... ");
				notifyDialFailed(new UcsReason(300303).setMsg("conference is full"));
				break;
			case UGoAPIParam.eUGo_Reason_CONF_CREATE_ERR:
				CustomLog.v( "创建会议失败 ... ");
				notifyDialFailed(new UcsReason(300304).setMsg("create conference error"));
				break;
			case UGoAPIParam.eUGo_Reason_CONF_CALL_FAILED:
				CustomLog.v( "呼叫流程中失败 ... ");
				notifyDialFailed(new UcsReason(300305).setMsg("call conference processs faild"));
				break;
			case UGoAPIParam.eUGo_Reason_CONF_MEDIA_FAILED:
				CustomLog.v( "申请媒体资源失败 ... ");
				notifyDialFailed(new UcsReason(300306).setMsg("media port apply faild"));
				break;
			case UGoAPIParam.eUGo_Reason_CONF_TER_UNSUPPORT:
				CustomLog.v( "对端不支持 ... ");
				notifyDialFailed(new UcsReason(300307).setMsg("peer not support"));
				break;
			}
			break;
			
		case UGoAPIParam.eUGo_CALLDIALING_EV:// eUGo_CALLDIALING_EV(呼叫事件)
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Success:// 接听电话
				CustomLog.v( "接听电话  ... ");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onAnswer(UCSCall.getCurrentCallId());
				}
				UserData.saveMySelfRefusal(false);
				startAnswerTimer();
				stopCallTimer();
				break;
			case UGoAPIParam.eUGo_Reason_NotAccept:// 媒体协商失败
			case UGoAPIParam.eUGo_Reason_RtppTimeOut:// RTPP 超时
			case UGoAPIParam.eUGo_Reason_UpdateMediaFail:// 媒体更新失败
			case 700:// 服务器错误
				CustomLog.v( reason + ":媒体协商失败  ... ");
				notifyDialFailed(new UcsReason(300210).setMsg("media negotiation failure"));
				break;
			case UGoAPIParam.eUGo_Reason_NoBalance:// 余额不足
				CustomLog.v( "余额不足  ... ");
				notifyDialFailed(new UcsReason(300211).setMsg("sorry, your credit is running low"));
				break;
			case UGoAPIParam.eUGo_Reason_Busy:// 对方正忙
				CustomLog.v( "对方正忙  ... ");
				notifyDialFailed(new UcsReason(300212).setMsg("the other side is busy"));
				break;
			case 480:// 标准的SIP反回对方拒绝
			case UGoAPIParam.eUGo_Reason_Reject:// 对方拒绝接听
				CustomLog.v( "对方拒绝接听 ... ");
				stopCallTimer();
				notifyDialFailed(new UcsReason(300213).setMsg("the other side refuse to answer"));
				break;
			case UGoAPIParam.eUGo_Reason_NotFind:// 该用户不在线 (不存在)
				CustomLog.v( "该用户不在线 ... ");
				notifyDialFailed(new UcsReason(300214).setMsg("the cliendid not find"));
				break;
			case UGoAPIParam.eUGo_Reason_TooShort:// 被叫号码错误
				CustomLog.v( "被叫号码错误 ... ");
				notifyDialFailed(new UcsReason(300215).setMsg("the cliendid error"));
				break;
			case UGoAPIParam.eUGo_Reason_CalleeFrozen:// 被叫号码冻结
				CustomLog.v( "被叫号码冻结 ... ");
				notifyDialFailed(new UcsReason(300216).setMsg("the other side frozen"));
				break;
			case UGoAPIParam.eUGo_Reason_Freeze:// 主叫帐号冻结
				CustomLog.v( "主叫号码冻结 ... ");
				notifyDialFailed(new UcsReason(300217).setMsg("the cliendid frozen"));
				break;
			case UGoAPIParam.eUGo_Reason_Expired:// 主叫帐号过期
				CustomLog.v( "主叫帐号过期 ... ");
				notifyDialFailed(new UcsReason(300218).setMsg("the cliendid be overdue"));
				break;
			case UGoAPIParam.eUGo_Reason_Cancel:// 主叫取消
				CustomLog.v( "主叫取消 ... ");
				break;
			case UGoAPIParam.eUGo_Reason_Forbidden:// 不能拨打自己绑定号码
				CustomLog.v( "不能拨打自己绑定号码 ... ");
				notifyDialFailed(new UcsReason(300219).setMsg(""));
				break;
			case UGoAPIParam.eUGo_Reason_NoResponse:// 请求超时
				CustomLog.v( "请求超时 ... ");
				notifyDialFailed(new UcsReason(300220).setMsg("repuest time out"));//
				break;
			case UGoAPIParam.eUGo_Reason_UnableToPush:// iOS
				break;
			case UGoAPIParam.eUGo_Reason_NoAnswer:// 对方无人应答
				CustomLog.v( "对方无人应答 ... ");
				notifyDialFailed(new UcsReason(300221).setMsg("the other side not answer"));
				break;
			case UGoAPIParam.eUGo_Reason_ConnectFaild:// 呼叫超时
				CustomLog.v( "呼叫超时 ... ");
				notifyDialFailed(new UcsReason(300220).setMsg("repuest time out"));
				break;
			case 32:// 转直拨
			case UGoAPIParam.eUGo_Reason_NotifyPeerTimeout://转直拨呼叫超时
			case UGoAPIParam.eUGo_Reason_NotifyPeerNotFind:
			case UGoAPIParam.eUGo_Reason_NotifyPeerOffLine:
				CustomLog.v( "转直拨 ... ");
				notifyDialFailed(new UcsReason(300222).setMsg("firect call"));
				break;
			case 47:
			case UGoAPIParam.eUGo_Reason_Connecting:
				CustomLog.v( "正在接通对方 ... ");
				if(param != null && param.length() > 0){
					try {
						JSONObject json = new JSONObject(param);
						if (json.has("callid")) {
							UCSCall.setCurrentCallId(json.getString("callid"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				//AudioManagerTools.getInstance().enableOtherMediaPlay(false);
				startCallTimer();
				break;
			case 38:// 对方正在响铃
			case 48:
			case UGoAPIParam.eUGo_Reason_Ringing:
				CustomLog.v( "对方正在响铃 ... ");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onAlerting(UCSCall.getCurrentCallId());
				}
				startCallTimer();
				break;
			case UGoAPIParam.eUGo_Reason_ProxyAuth:// (未登录)Proxy鉴权失败
				CustomLog.v( "Proxy鉴权失败 ... ");
				notifyDialFailed(new UcsReason(300223).setMsg("session expiration"));
				break;
			default:// 拨打失败
				if(reason >=10000 && reason <= 20000){
					//KC透传
					CustomLog.v( "透传:"+reason);
					notifyDialFailed(new UcsReason(reason).setMsg(""));
				}else{
					CustomLog.v( "拨打失败 ... ");
					notifyDialFailed(new UcsReason(300224).setMsg("other error："+reason));
				}
				break;
			}
			break;
		case UGoAPIParam.eUGo_CALLINCOMING_EV:// eUGo_CALLINCOMING_EV(电话呼入事件)
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Success:
				//AudioManagerTools.getInstance().enableOtherMediaPlay(false);
				String phone = "";
				int type = 0;
				String nickName = "";
				String userdata = "";
				
				if(param != null && param.length() > 0){
					try {
						JSONObject json = new JSONObject(param);
						if (json.has("callid")) {
							UCSCall.setCurrentCallId(json.getString("callid"));
						}
						if(json.has("fphone")){
							phone = json.getString("fphone");
						}
						if(json.has("fuid") && phone.length() <= 0){
							phone = json.getString("fuid");
						}
						if(json.has("meetingflag")){
							int meetingflag = json.getInt("meetingflag");
							if(meetingflag == 1){
								type = 2;		//2:会议来电
							}
						}
						if(type == 0 && json.has("videoflag")){    //0:音频    1：视频
							type = json.getInt("videoflag");
						}
						if(json.has("fnickname")){
							nickName = json.getString("fnickname");
						}
						if(json.has("user_data")){
							userdata = json.getString("user_data");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				CustomLog.v( "新的来电:"+param);
				if(type == 2){
					CustomLog.v( "会议来电 ... ");
				}else if(type == 1){
					CustomLog.v( "视频来电 ... ");
				}else{
					CustomLog.v( "音频来电 ... ");
				}
				//{"videoflag":0,"audiofec":0,"directfec":0,"calltype":0,"meetingflag":1,"ucalltype":0,"callid":"nullgXbFk12321744033","fuid":"63244003000015","fphone":"","fnickname":"","tuid":"63244003000017","tphone":""}

				UserData.saveCallTimeoutTime(ConnectionControllerService.this,115000);
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					if(type == 2){
						csl.onChatRoomIncomingCall(UCSCall.getCurrentCallId(), type+"", phone, nickName, userdata);
					}else{
						csl.onIncomingCall(UCSCall.getCurrentCallId(), type+"", phone, nickName, userdata);
					}
				}
				UserData.saveMySelfRefusal(true);
				startCallTimer();
				break;
			default:
				break;
			}
			break;
		case UGoAPIParam.eUGo_CALLANSWER_EV:// eUGo_CALLANSWER_EV(接听事件)
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Success:// 接听、
				CustomLog.v( "接听 ... ");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onAnswer(UCSCall.getCurrentCallId());
				}
				startAnswerTimer();
				UserData.saveMySelfRefusal(false);
				stopCallTimer();
				break;
			default:
				break;
			}
			break;
		case UGoAPIParam.eUGo_CALLHUNGUP_EV:// eUGo_CALLHUNGUP_EV(挂断事件)
			stopCallTimer();
			//AudioManagerTools.getInstance().enableOtherMediaPlay(true);
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Forbidden:// 不能拨打自己绑定号码
				CustomLog.v( "不能拨打自己绑定号码 ... ");
				notifyHangUp(new UcsReason(300219).setMsg(""));
			case UGoAPIParam.eUGo_Reason_NoAnswer:// called have no answer(被叫方没有应答)
				CustomLog.v( "被叫方没有应答 ... ");
				notifyHangUp(new UcsReason(300221).setMsg("the other side not answer"));
				break;
			case UGoAPIParam.eUGo_Reason_ConnectFaild:// connect failed(联接失败)
				CustomLog.v( "联接失败 ... ");
				notifyHangUp(new UcsReason(300220).setMsg("repuest time out"));
				break;
			case UGoAPIParam.eUGo_Reason_HungupMyself:// 自己挂断电话
				if(UserData.isMySelfTimeOutHangup(ConnectionControllerService.this)){
					CustomLog.v("根据最大超时时间请求超时 ...");
					UserData.saveMySelfTimeOutHangup(ConnectionControllerService.this, false);
					notifyDialFailed(new UcsReason(300220).setMsg("time repuest time out"));//
				}else if(UserData.isNotNetworkHangup(ConnectionControllerService.this)){
					CustomLog.v("网络中断挂断电话 ...");
					UserData.saveNotNetworkHangup(ConnectionControllerService.this, false);
					notifyHangUp(new UcsReason(300318).setMsg(""));
				}else{
					CustomLog.v("自己挂断电话 ...");
					notifyHangUp(new UcsReason(300225).setMsg("myself hangup"));
				}
				break;
			case UGoAPIParam.eUGo_Reason_HungupPeer:// 对方挂断电话
				CustomLog.v( reason+"对方挂断电话 ... ");
				notifyHangUp(new UcsReason(300226).setMsg("the other side hangup"));
				break;
			case UGoAPIParam.eUGo_Reason_Reject:// 对方拒绝接听
				if (UserData.isMySelfRefusal()) {
					UserData.saveMySelfRefusal(false);
					CustomLog.v( "自己拒绝接听 ... ");
					notifyHangUp(new UcsReason(300248).setMsg(""));
				}else{
					CustomLog.v( "对方拒绝接听 ... ");
					notifyHangUp(new UcsReason(300213).setMsg("the other side refuse to answer"));
				}
				break;
			case UGoAPIParam.eUGo_Reason_NoBalance:// 余额不足
				CustomLog.v( "余额不足 ... ");
				notifyHangUp(new UcsReason(300211).setMsg("Sorry, your credit is running low"));
				break;
			case UGoAPIParam.eUGo_Reason_RtppTimeOut:// RTPP 超时
				CustomLog.v( "RTPP 超时 ... ");
				notifyHangUp(new UcsReason(300210).setMsg("RTPP time out"));
				break;
			case UGoAPIParam.eUGo_Reason_NetworkDisable:// 网络类型不支持
				CustomLog.v( "网络类型不支持 ... ");
				notifyHangUp(new UcsReason(300210).setMsg("network does not support"));
				break;
			case UGoAPIParam.eUGo_Reason_MsgTimeOut:// message timeout
				CustomLog.v( "message timeout ... ");
				notifyHangUp(new UcsReason(300210).setMsg("repuest time out"));
				break;
			case UGoAPIParam.eUGo_Reason_TooShort:// 被叫号码错误
				CustomLog.v( "被叫号码错误 ... ");
				notifyDialFailed(new UcsReason(300215).setMsg("the cliendid error"));
				break;
			case UGoAPIParam.eUGo_Reason_Success://2014-8-29(兼容组件的一个信令错误)
				CustomLog.v( reason+"对方挂断电话 ... ");
				notifyHangUp(new UcsReason(300226).setMsg("the other side hangup"));
				break;
			default:
				CustomLog.v( reason+"其他原因挂断电话 ... ");
				notifyHangUp(new UcsReason(300226).setMsg("the other side hangup"));
				//eUGo_Reason_UnkownError
				break;
			}
			// CallConfig.setCallHangup(false);
			break;
		case UGoAPIParam.eUGo_NETWORK_EV:// eUGo_NETWORK_EV(网络状态上报)
			CustomLog.v( "网络状态上报 ... ");
			break;
		case UGoAPIParam.eUGo_UPSINGLEPASS_EV:
			CustomLog.v( " ......... ");
			break;
		case UGoAPIParam.eUGo_DNSINGLEPASS_EV:// UP RTP single pass
		case UGoAPIParam.eUGo_TCPTRANSPORT_EV:// DN RTP single pass
			break;
		case UGoAPIParam.eUGo_GETDTMF_EV:
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.onDTMF(reason);
			}
			break;
		}
	}

	/**
	 * 通知转换成会议模式
	 * 
	 * @author: xiaozhenhua
	 * @data:2015-1-8 下午4:34:49
	 */
	private void notifyConferenceModeConvert(){
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.onChatRoomModeConvert(UCSCall.getCurrentCallId());
		}
	}
	
	/**
	 * 通知呼叫失败
	 * @param dial
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午10:45:34
	 */
	private void notifyDialFailed(UcsReason dial) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.onDialFailed(UCSCall.getCurrentCallId(),dial);
		}
		UCSCall.setCurrentCallId("");
		ErrorCodeReportTools.collectionErrorCode(ConnectionControllerService.this, UserData.getClientId(), "", dial.getReason(), dial.getMsg());
	}

	/**
	 * 通知挂机
	 * @param hangup
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午10:45:27
	 */
	private void notifyHangUp(UcsReason hangup) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.onHangUp(UCSCall.getCurrentCallId(), hangup);
		}
		//stopAnswerTimer();
		
		CustomLog.v("EMODEL:"+UserData.isAnswer(ConnectionControllerService.this));
		if(UserData.isAnswer(ConnectionControllerService.this)
				&& SharedPreferencesUtils.isVpmEnable(ConnectionControllerService.this)){
			//获取eModel值
			EmodelTools.getEmodelValue(ConnectionControllerService.this);
			UserData.saveAnswer(ConnectionControllerService.this, false);
		}
		
		//SystemMediaConfig.restoreMediaConfig(null, ConnectionControllerService.this);
		UCSCall.setCurrentCallId("");
		ErrorCodeReportTools.collectionErrorCode(ConnectionControllerService.this, UserData.getClientId(), "", hangup.getReason(), hangup.getMsg());
	}
	
	
	public void startAnswerTimer(){
		stopAnswerTimer();
		if(answerTimer == null){
			answerTimer = new Timer();
		}
		answerTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				CustomLog.v("TIME TASK ANSWER ... ");
				UserData.saveAnswer(ConnectionControllerService.this, true);
			}
		}, 30000);
	}
	
	public void stopAnswerTimer(){
		if (answerTimer != null){
			answerTimer.cancel();
			answerTimer=null;
			CustomLog.v("STOP TIME TASK ANSWER ... ");
		}
	}
	
	
	/**
	 * 来电/去电超时定时器
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-8-9 上午11:47:34
	 */
	public void startCallTimer(){
		stopCallTimer();
		if(timer == null){
			timer = new Timer();
		}
		CustomLog.v("TIME TASK:"+UserData.getCallTimeoutTime(ConnectionControllerService.this));
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				CustomLog.v("TIME TASK HUNGUP ...");
				UserData.saveMySelfTimeOutHangup(ConnectionControllerService.this, true);
				hangUp("");
			}
		}, UserData.getCallTimeoutTime(ConnectionControllerService.this));
	}
	
	public void stopCallTimer(){
		if (timer != null){
			timer.cancel();
			timer=null;
			CustomLog.v("STOP TIME TASK ... ");
		}
	}

	@Override
	public void onAudioDeviceUpdate() {
		CustomLog.v("onDevicesUpdate ... ");
		UGo_SetConfig();
	}

	@Override
	public void encryptCallback(byte[] inMsg, byte[] outMsg, int inLen,
			int[] outLen) {
		
	}

	@Override
	public void decryptCallback(byte[] inMsg, byte[] outMsg, int inLen,
			int[] outLen) {
		
	}
}

