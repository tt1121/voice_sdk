package com.yzx.tcp.packet;

public class PacketDfineAction {

	public final static String PREFERENCE_NAME = "yunzhixun_preference";
	
	//--------------------------消息type 类型------------------------------
	public static final byte SOCKET_INTERNAL_TYPE = 0x00;  // 客户端与服务端内部消息
	public static final byte SOCKET_REALTIME_TYPE = 0xB;  //实时消息
	public static final byte SOCKET_STORAGE_TYPE = 0xC;  //存储转发消息
	public static final byte SOCKET_USERSTATUS_TYPE = 0x05; // 状态查询
	public static final byte SOCKET_SEVERPUSH_TYPE = 0xE; // 服务器推送业务
	public static final byte SOCKET_BOROADCAST  = 0xD; //广播消息
	public static final byte SOCKET_CALL  = 0xA;//电话
	//--------------------------消息OP 子功能码 类型------------------------------
	public static final byte SOCKET_LOGIN_STATUS_OP = 0x01;  // 登录
	public static final byte SOCKET_PING_OP = 0x02;  // 心跳,状态查询
	public static final byte SOCKET_INPUT_OP = 0x03;// 正在输入
	public static final byte SOCKET_LOGINCOT_OFF_LINE_OP = 0x04; //登出消息、存储消息
	public static final byte SOCKET_CONNET_OP = 0x05;//连接建立
	public static final byte SOCKET_COMPULSORY_OP = 0x06; //强制下线消息
	public static final byte SOCKET_SERVICE_STOP_OP = 0x07; // 服务器停机
	public static final byte SOCKET_REEIVER_OP = 0x79;//推送应答
	public static final byte SOCKET_VERSION = 0x78;//版本号发送
	public static short SN = 0;  						//流水号
	
	
	public static final String RESULT = "result";
	public static final String TYPE = "type";
	public static final String STATUS = "status";
	public static final String STATE = "state";
	
	public static final String CALLID = "callid";
	public static final String REASON = "reason";
	
	public static final String PATH = "path";
	public static final String MSG_LIST = "msglist";
	public static final String UID = "uid";
	public static final String FROM = "from";
	public static final String FROMUID = "fromuid";
	public static final String TO = "to";
	public static final String TOUID = "touid";
	public static final String MSG = "msg";
	public static final String TIME = "time";
	public static final String ACTIONS = "actions";
	public static final String FLAG = "flag";
	public static final String FROM_NAME = "fromname";
	public static final String FROM_HEAD = "fromhead";
	public static final String DIRECT_JUMP = "directjump";
	public static final String REDIRECT = "redirect";
	public static final String MASTER_BUSINESS = "masterBusiness";
	public static final String SLAVE_BUSINESS = "slaveBusiness";
	public static final String STATUS_SERVER_ID = "id";
	public static final String RANDCODE_ID = "randcode";
	public static final String FILENAME = "filename";
	public static final String FILESIZE = "filesize";
	
	public static final String FROM_SER_NUM = "fromSerNum";
	public static final String TO_SER_NUM = "toSerNum";
	
	public static final String KEY_INTENT_CONFIG_MODEL = "MODEL";
	public static final String KEY_INTENT_CONFIG_CALLUID = "calledUid";
	public static final String KEY_INTENT_CONFIG_CALLPHONE = "calledPhone";
	public static final String KEY_INTENT_CONFIG_CALLUSERDATA = "calledUserdata";
	
	public static final String KEY_INTENT_CONFIG_MEMBERSTR = "memberStr";
	
	public static String INTENT_ACTION_LOGIN = "com.yunzhixun.action.login";
	public static String INTENT_ACTION_CS = "com.yunzhixun.action.getcs";
	public static String INTENT_ACTION_SETCONFIG = "com.yunzhixun.action.setconfig";
	public static String INTENT_ACTION_INIT = "com.yunzhixun.action.init";
	public static String INTENT_ACTION_CONFERENCEDIAL = "com.yunzhixun.action.conferencedial";
	public static String INTENT_ACTION_DIAL = "com.yunzhixun.action.dial";
	public static String INTENT_ACTION_HANDUP = "com.yunzhixun.action.handup";
	public static String INTENT_ACTION_ANSWER = "com.yunzhixun.action.answer";
	public static String INTENT_ACTION_DTMF = "com.yunzhixun.action.dtmf";
	public static String INTENT_ACTION_MIC_MUTE = "com.yunzhixun.action.mic";
	public static String INTENT_ACTION_CONNECT = "com.yunzhixun.action.connect";
	public static String INTENT_ACTION_SEND_MESSAGE = "com.yunzhixun.action.sendmessage";
	public static String INTENT_ACTION_QUERY_STATE = "com.yunzhixun.action.query";
	public static String INTENT_ACTION_FORWARDING = "com.yunzhixun.action.forwarding";
	public static String INTENT_ACTION_LOG = "com.yunzhixun.action.savelog";

	public static String INTENT_ACTION_AD_ADAPTER = "com.yunzhixun.action.adapter";
	public static String INTENT_ACTION_ADAPTER_OK = "com.yunzhixun.action.adaptersuccess";
	
	public static void initAction(String packageName) {
		if(!INTENT_ACTION_LOGIN.startsWith(packageName)){
			INTENT_ACTION_LOGIN = packageName + "_" + INTENT_ACTION_LOGIN;
			INTENT_ACTION_CS = packageName + "_" + INTENT_ACTION_CS;
			INTENT_ACTION_SETCONFIG = packageName + "_" + INTENT_ACTION_SETCONFIG;
			INTENT_ACTION_INIT = packageName + "_" + INTENT_ACTION_INIT;
			INTENT_ACTION_DIAL = packageName + "_" + INTENT_ACTION_DIAL;
			INTENT_ACTION_HANDUP = packageName + "_" + INTENT_ACTION_HANDUP;
			INTENT_ACTION_ANSWER = packageName + "_" + INTENT_ACTION_ANSWER;
			INTENT_ACTION_DTMF = packageName + "_" + INTENT_ACTION_DTMF;
			INTENT_ACTION_MIC_MUTE = packageName + "_" + INTENT_ACTION_MIC_MUTE;
			INTENT_ACTION_CONNECT = packageName + "_" + INTENT_ACTION_CONNECT;
			INTENT_ACTION_SEND_MESSAGE = packageName + "_" + INTENT_ACTION_SEND_MESSAGE;
			INTENT_ACTION_CONFERENCEDIAL  = packageName + "_" + INTENT_ACTION_CONFERENCEDIAL; 
			INTENT_ACTION_QUERY_STATE = packageName + "_" + INTENT_ACTION_QUERY_STATE;
			INTENT_ACTION_AD_ADAPTER  = packageName + "_" + INTENT_ACTION_AD_ADAPTER; 
			INTENT_ACTION_FORWARDING  = packageName + "_" + INTENT_ACTION_FORWARDING; 
			INTENT_ACTION_ADAPTER_OK  = packageName + "_" + INTENT_ACTION_ADAPTER_OK;
		}
	}
	
	
	public static final String INTENT_ACTION_CONNECT_KEY_SID = "sid";
	public static final String INTENT_ACTION_CONNECT_KEY_SID_PWD = "sid_pwd";
	public static final String INTENT_ACTION_CONNECT_KEY_CLIEND = "cliend";
	public static final String INTENT_ACTION_CONNECT_KEY_CLIEND_PWD = "cliend_pwd";
	public static final String INTENT_ACTION_CONNECT_KEY_HOST = "host";
	public static final String INTENT_ACTION_CONNECT_KEY_PORT = "port";
	public static final String INTENT_ACTION_CONNECT_KEY_TYPE = "type";
	public static final String INTENT_ACTION_CONNECT_KEY_CHECK_CLIENT = "check_client";
	public static final String INTENT_ACTION_CONNECT_KEY_MIC_MUTE = "mic";
	public static final String INTENT_ACTION_KEY_FORWARDING = "forward";

	public static final String INTENT_ACTION_CONNECT_KEY_AD_ADAPTER = "adapter";
	public static final String INTENT_ACTION_CONNECT_KEY_ADAPTER_OK = "adaptersuccess";
	
	public static final long IM_TEXT_TIME = 30 * 1000; 				//IM文字信息应答超时时长
	public static final long PING_TIME = 100 * 1000; 				//心跳间隔时间
	public static long PING_COUNT = 0;  					//测试用 心跳计数
	
}
