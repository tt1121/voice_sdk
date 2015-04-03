package com.gl.softphone;

/**
 * @author£ºRambo.Fu
 * @date£º 2014-4-3
 * @description£ºall api params/macro/enum definitions
 */
public class UGoAPIParam {
	/**
     * *UGo module state****
     */
    public static final int eUGo_STATE_WAIT = 0;        //wait
    public static final int eUGo_STATE_INVITE = 1;        //invite
    public static final int eUGo_STATE_RINGING = 2;     //ringing
    public static final int eUGo_STATE_CONNECT = 3;        //connect
    public static final int eUGo_STATE_PUSHACTIVE = 4;    //ios push call active
    public static final int eUGo_STATE_SYSACTIVE = 5;    //system call active

    /**
     * *UGo event type******
     */
    public static final int eUGo_CALLDIALING_EV = 0;    //call dialing event
    public static final int eUGo_CALLINCOMING_EV = 1;    //call incoming event
    public static final int eUGo_CALLANSWER_EV = 2;        //call answer event
    public static final int eUGo_CALLHUNGUP_EV = 3;        //call hungup event
    public static final int eUGo_NETWORK_EV = 4;        //network state event
    public static final int eUGo_UPSINGLEPASS_EV = 5;    //UP RTP single pass
    public static final int eUGo_DNSINGLEPASS_EV = 6;    //DN RTP single pass
    public static final int eUGo_TCPTRANSPORT_EV = 7;   //tcptransport event only use for test
    public static final int eUGo_CONFERENCE_EV = 8;   //conference call event  add by wuzhaoyang20140605
    public static final int eUGo_GETDTMF_EV = 9;      //get dtmf event
    /**
     * *network state reason******
     */
    public static final int eUGo_NETWORK_GENERAL = 0;        //general
    public static final int eUGo_NETWORK_NICE = 1;           //good
    public static final int eUGo_NETWORK_BAD = 2;             //bad

    /**
     * *the reason for singlepass event**
     */
    public static final int eUGo_NETWORK_ERROR = 0;                //network problem
    public static final int eUGo_AUDIO_DEVICE_INIT = 1;            //local device init failed
    public static final int eUGo_START_SEND = 2;                //start send failed
    public static final int eUGo_START_RECEIVE_FAIL = 3;        //start receive failed
    public static final int eUGo_SET_LOCAL_RECEIVER_FAIL = 4;    //receive failed


    /****UGo event reason: begin*****/
    /**
     * success reason 0*
     */
    public static final int eUGo_Reason_Success = 0;    //success

    /*public reason 1~29*/
    public static final int eUGo_Reason_NotAccept = 1;    //not accept
    public static final int eUGo_Reason_RtppTimeOut = 2;    //RTPP RTP timeout
    public static final int eUGo_Reason_NoBalance = 3;    //nobalance
    public static final int eUGo_Reason_UpdateMediaFail = 4;    //update media fail
    public static final int eUGo_Reason_Busy = 5;    //busy
    public static final int eUGo_Reason_Reject = 6;    //reject
    public static final int eUGo_Reason_NotFind = 7;    //not find
    public static final int eUGo_Reason_TooShort = 8;    //Forbidden(callee too short)
    public static final int eUGo_Reason_CalleeFrozen = 9;    //callee have been frozen
    public static final int eUGo_Reason_Freeze = 10;    //caller have been frozen
    public static final int eUGo_Reason_Expired = 11;    //caller expired
    public static final int eUGo_Reason_Cancel = 12;    //Terminater for Cancel
    public static final int eUGo_Reason_Forbidden = 13;    //Forbidden(caller binding number)
    public static final int eUGo_Reason_NoResponse = 14;    //no response timeout
    public static final int eUGo_Reason_NetworkDisable = 15;   //The network is not supported
    public static final int eUGo_Reason_UnReachable = 16;   //Signaling inaccessible(NACK)
    public static final int eUGo_Reason_UnableToPush = 17;   //ios unable to push
    public static final int eUGo_Reason_CallidNotExist = 18;   //callid is not exist
    public static final int eUGo_Reason_NoAnswer = 19;    //callee have no answer
    public static final int eUGo_Reason_ConnectFaild = 20;    //connect faild

    /*client reason 30~49*/
    public static final int eUGo_Reason_HungupMyself = 30;   //call hungup by myself
    public static final int eUGo_Reason_HungupPeer = 31;   //call hungup by peer
    public static final int eUGo_Reason_HungupTCPDisconnected = 32;   //"Tcp event:Server connect failed!!!"
    public static final int eUGo_Reason_HungupRTPTimeout = 33;   //"medie engine: RTP time out!!!"

    /**
     * vps sever reason 50~79*
     */
    public static final int eUGo_Reason_ProxyAuth = 50;    //proxy auth
    public static final int eUGo_Reason_MsgHeadError = 51;    //message head error
    public static final int eUGo_Reason_MsgBodyError = 52;    //message body error
    public static final int eUGo_Reason_CallIDExist = 53;    //callid exist
    public static final int eUGo_Reason_MsgTimeOut = 54;    //message timeout


    /* conference reason  add by wuzhaoyang20140621 */
    public static final int eUGo_Reason_CONF_NO_EXIST = 60;  //find the conference fail
    public static final int eUGo_Reason_CONF_STATE_ERR = 61;  //conference state error
    public static final int eUGo_Reason_CONF_FULL = 62;  //conference full
    public static final int eUGo_Reason_CONF_CREATE_ERR = 63;  //create conference fail
    public static final int eUGo_Reason_CONF_CALL_FAILED = 64;  //call procedure fail
    public static final int eUGo_Reason_CONF_MEDIA_FAILED = 65;  //apply media resource fail
    public static final int eUGo_Reason_CONF_TER_UNSUPPORT = 66;  //the peer don't support

    /* conference reason  add by wuzhaoyang20140605 */
    public static final int eUGo_Reason_StateNotify = 70;   //conference state notify
    public static final int eUGo_Reason_ActiveModeConvert = 71;   //active change to conference mode
    public static final int eUGo_Reason_PassiveModeConvert = 72;   //passive change to conference mode

    /*temporary reason 80~98(notify:80~89,other:90~98)*/
    public static final int eUGo_Reason_NotifyPeerNotFind = 80;   //call notify peer uid not find
    public static final int eUGo_Reason_NotifyPeerOffLine = 81;   //call notify peer offline
    public static final int eUGo_Reason_NotifyPeerTimeout = 82;   //call notify free call timeout

    public static final int eUGo_Reason_Connecting = 97;    //connecting between send invite and don't receive response
    public static final int eUGo_Reason_Ringing = 98;   //Ringing response

    /**
     * video reason *
     */
    public static final int eUGo_Reason_VideoPreview = 150;
    public static final int eUGo_Reason_VideoStartSendRecive = 151;
    public static final int eUGo_Reason_VideoStartSend = 152;
    public static final int eUGo_Reason_VideoStartRecive = 153;
    public static final int eUGo_Reason_VideoStopSendRecive = 154;
    public static final int eUGo_Reason_VideoStopSend = 155;
    public static final int eUGo_Reason_VideoStopRecive = 156;
    public static final int eUGo_Reason_VideoRelease = 157;

    /**
     * unkown error 99*
     */
    public static final int eUGo_Reason_UnkownError = 99;    //unkown error
    /****UGo event reason: end*****/


    /**
     * *tcptransport state***
     */
    public static final int eUGo_TCP_DISCONNECTED = 0;
    public static final int eUGo_TCP_CONNECTED = 1;
    public static final int eUGo_TCP_RECONNECTED = 2;


    /**
     * system call state*
     */
    public static final int eUGo_SYSCALL_IDLE = 0;
    public static final int eUGo_SYSCALL_ACTIVE = 1;

    /**
     * **debug level **
     */
    public static final int eME_TraceNone = 0x0000;  // no trace
    public static final int eME_TraceStateInfo = 0x0001;
    public static final int eME_TraceWarning = 0x0002;
    public static final int eME_TraceError = 0x0004;
    public static final int eME_TraceDebug = 0x0800;    // debug
    public static final int eME_TraceInfo = 0x1000;  // debug info
    public static final int eME_TraceReport = 0x8000;  //calling report
    public static final int eME_TraceAll = 0xffff;
    public static final int kME_TraceReport = 0x8000;  // trace something important log

    /**
     * * call mode **
     */
    public static final int eUGo_CM_DIRECT = 4;
    public static final int eUGo_CM_AUTO = 5;
    public static final int eUGo_CM_FREE = 6;

    /**
     * * file play mode **
     */
    public static final int kME_FILE_HANDLE = 0;
    public static final int kME_FILE_STREAM = 1;
    public static final int kME_FILE_PATHNAME = 2;

    /**
     * **file format **
     */
    public static final int kME_FileFormatWavFile = 1;
    public static final int kME_FileFormatCompressedFile = 2;
    public static final int kME_FileFormatAviFile = 3;
    public static final int kME_FileFormatPreencodedFile = 4;
    public static final int kME_FileFormatPcm16kHzFile = 7;
    public static final int kME_FileFormatPcm8kHzFile = 8;
    public static final int kME_FileFormatPcm32kHzFile = 9;

    /**
     * ** method id ***
     */
    public static final int UGO_CFG_PARAM_MODULE_ID = 0;        // Corresponding  UGo module parameter configuration structure
    public static final int UGO_CFG_TCP_MODULE_ID = 1;            // corresponding  UGo module TCP configuration structure
    public static final int UGO_CFG_ICE_MODULE_ID = 2;            // Corresponding UGo module ICE configuration structure
    public static final int UGO_RTPP_CFG_MODULE_ID = 3;            //Corresponding  UGo module RTPP configuration structure
    public static final int UGO_EMODEL_MODULE_ID = 4;            //corresponds EMODEL module configuration structure (reservation)
   

    public static final int ME_CTRL_CFG_MODULE_ID = 100;            //Engine control module configuration corresponding media structure
    public static final int ME_VQE_CFG_MODULE_ID = 101;            //module configuration structure corresponding vqe
    public static final int ME_RTP_CFG_MODULE_ID = 102;            // rtp module configuration corresponding structure
    public static final int ME_ENV_CFG_MODULE_ID = 103;            //corresponding environment variables module configuration structure
    public static final int ME_VIDEO_ENC_CFG_MODULE_ID = 104; /* Encoder parameters included */
    public static final int ME_VIDEO_DEC_CFG_MODULE_ID = 105; /* Decoder parameters included */
    public static final int ME_VIDEO_RENDER_CFG_MODULE_ID = 106; /* Render parameters included */
    public static final int ME_VIDEO_PROCES_CFG_MODULE_ID = 107;  /* Image process parameters included */
    public static final int ME_CODECS_CFG_MODULE_ID       = 108;  // corresponding codec module configuration structure

    /*define struction*/
    public MediaConfig stMediaCfg = null;
    public RtpConfig stRTPCfg = null;


    //modity by charlie  yuan 2014.04.30
    //public static RtppSrvConfig[] astRTPSrvCfg = null;
    public RtppSrvConfig astRTPSrvCfg = null;

    public EnvConfig envConfig = null;
    public DecodeConfig decodeConfig = null;
    public UGoConfig stUGoCfg = null;
    //add by charlie yuan 2014.04.29
    public TcpConfig stTcpCfg = null;
    public IceConfig stIceCfg = null;
    //end add by charlie
    public VqeConfig stVQECfg = null;
    public VideoEncParam stVideoEncCfg = null;
    public VideoDecParam stVideodecCfg = null;

    public VideoRenderParam stVideoRenderCfg = null;
    public VideoProcess stVideoProcessCfg = null;
    public EmodelInfo stEmlInfoCfg = null;
    public EmodelValue stEmlValueCfg = null;

    public VideoCameraPara stVideoDevicePara = null;
    public LogTracePara stLogTracePara = null;
    public CallDialPara stCallDialPara = null;
	public CallPushPara stCallPushPara = null;
    public MediaFileRecordPara MediaFileRecordPara = null;
    public TcpRecvPara stTcpRecvPara = null;
    public MediaFilePlayPara stMediaFilePlayPara = null;
    public AudioInfo stAudioInfo = null;
    public VideoInfo stVideoInfo = null;
    public LogCfg logCfg = null;

    public ConferenceCallDialPara stConferenceCallDialPara = null;//add by wuzhaoyang20140721
    public ConferenceTestDialPara stConferenceTestDialPara = null;
    public NoramlCallTestDialPara stNmlCallTestDialPara = null;
	public static final int MAX_VIDEO_CODEC_LIST_NUM = 10;
	public static UGoAPIParam ugoApiParam;

	public static UGoAPIParam getInstance() {
		if (ugoApiParam == null) {
			ugoApiParam = new UGoAPIParam();
		}
		return ugoApiParam;
	}

    public UGoAPIParam() {
        stMediaCfg = new MediaConfig();
        stRTPCfg = new RtpConfig();
        //modity by charlie yuan 2014.04.30
        //astRTPSrvCfg = new RtppSrvConfig[2];

        //astRTPSrvCfg[0] = new RtppSrvConfig();
        //astRTPSrvCfg[1] = new RtppSrvConfig();
        astRTPSrvCfg = new RtppSrvConfig();

        envConfig = new EnvConfig();
        decodeConfig = new DecodeConfig();
        stUGoCfg = new UGoConfig();
        stTcpCfg = new TcpConfig();
        stIceCfg = new IceConfig();
        //astRTPSrvCfg = new RtppSrvConfig();

        stVQECfg = new VqeConfig();
        stVideoEncCfg = new VideoEncParam();
        stVideoEncCfg.ucPlName = new String[MAX_VIDEO_CODEC_LIST_NUM];
        stVideoEncCfg.enable = new int[MAX_VIDEO_CODEC_LIST_NUM];
        stVideoEncCfg.ucPlType = new int[MAX_VIDEO_CODEC_LIST_NUM];
      
        stVideoRenderCfg = new VideoRenderParam();
        stVideodecCfg = new VideoDecParam();
        stVideoProcessCfg = new VideoProcess();
        stEmlInfoCfg = new EmodelInfo();
        stEmlValueCfg = new EmodelValue();
        stEmlValueCfg.emodel_mos = new EmodelInfo();
        stEmlValueCfg.emodel_ie = new EmodelInfo();
        stEmlValueCfg.emodel_ppl = new EmodelInfo();
        stEmlValueCfg.emodel_burstr = new EmodelInfo();
        stEmlValueCfg.emodel_tr = new EmodelInfo();
        stVideoDevicePara = new VideoCameraPara();
        stLogTracePara = new LogTracePara();
        stCallDialPara = new CallDialPara();
		stCallPushPara = new CallPushPara();
        MediaFileRecordPara = new MediaFileRecordPara();
        stTcpRecvPara = new TcpRecvPara();
        stMediaFilePlayPara = new MediaFilePlayPara();
        stAudioInfo = new AudioInfo();
        stVideoInfo = new VideoInfo();
        logCfg = new LogCfg();
        stConferenceCallDialPara = new ConferenceCallDialPara();//add by wuzhaoyang20140721
        /* add by VintonLiu on 20141114 for conference auto test */
        stConferenceTestDialPara = new ConferenceTestDialPara();
        stNmlCallTestDialPara = new NoramlCallTestDialPara();

    }
}
