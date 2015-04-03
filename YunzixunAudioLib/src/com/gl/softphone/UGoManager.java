package com.gl.softphone;

import com.yzx.tools.CustomLog;

import android.content.Context;

/**
 * @author£º ÌÆµ¤Ñô
 * @date£º 2013-1-16
 * @description£º 
 */
public class UGoManager
{
	native int UGoDebugEnabled(boolean enable);
	native int UGoInit();
	native int UGoDestroy();
	native int UGoRigister(String uid,int mode);
	native int UGoUnRigister();
	native int UGoCallPush(CallPushPara param);
	native int UGoDial(CallDialPara para);
	native int UGoConferenceDial(Object para);//add by wuzhaoyang20140721
	native int UGoAnswer();
	native int UGoHangup(int reason);
	native String UGoGetVersion();
	native int UGoTcpRecvMsg(int recvlen, byte[] recvbuf);
	//native int UGoTcpRecvMsg(TcpRecvPara tcprecv);
	native int UGoTcpUpdateState(int state);
	native int UGoUpdateSystemState(int state);
	native int UGoSetMicMute(boolean enable);
    native boolean UGoGetMicMute();
	native int UGoSetLoudSpeakerStatus(boolean enable);
    native boolean UGoGetLoudSpeakerStatus();
	native int UGoSendDTMF(char dtmf);
	native void Callbacks(IUGoCallbacks cb);
	native void setAndroidContext(Context context);
	native int UGoLoadMediaEngine(int mode);//0 vogo,1 vigo
	native int UGoSetConfig(int mothed,Object config,int version);
	native int UGoGetConfig(int mothed,Object config,int version);
	native int UGoGetState();
	native int UGoSetApi(int level);
	native int UGoSetLogFile(LogTracePara para, int version);
	/*add by Rookie 20130806 for emodel interface*/
	native int UGoGetEmodelValue(EmodelInfo mos, EmodelInfo tr, EmodelInfo ppl, EmodelInfo burstr, EmodelInfo ie);
	native int UGoStartRecord(MediaFileRecordPara para);
	native int UGoStopRecord();
	native int UGoPlayFile(MediaFilePlayPara para);
	native int UGoStopFile();
	native int UGoVideoSetCamera(VideoCameraPara para);
	native int UGoStartVideo(int sendReceive);
	native int UGoStopVideo(int sendReceive);
	native int UGoVideoGetCameraState(VideoCameraPara info);

	

	//all callback function address
	private IUGoCallbacks UGoCallbacks;

	static {
		if (android.os.Build.VERSION.SDK_INT < 9) {
			System.loadLibrary("OpenSLES");
		}
		System.loadLibrary("VoGo");
		System.loadLibrary("UGo");
	}
	
	public static UGoManager Single = null;

	public static UGoManager getInstance() {
		if (Single == null) {
			Single = new UGoManager();
			//Single.UGoCallbacks = cb;
		}
		return Single;
	}

	/*public static UGoManager getUGoManager() {
		return Single;
	}*/

	private UGoManager() {

	}
	
	public synchronized int pub_UGoDebugEnabled(boolean enable)
	{
		return UGoDebugEnabled(enable);
	}
	
	public synchronized int pub_UGoSetLogFile(LogTracePara para, int version)
	{
		return UGoSetLogFile(para, version);
	}
	
	public synchronized int pub_UGoGetState()
	{
		return UGoGetState();
	}
	public synchronized int pub_UGoSetApi(int level)
	{
		return UGoSetApi(level);
	}	
	public synchronized int pub_UGoSetConfig(int methodID,Object config, int version)
	{
		return UGoSetConfig(methodID, config,version);
	}

	public synchronized int pub_UGoGetConfig(int method,Object config, int version)
	{
		return UGoGetConfig(method,config,version);
	}
	
	public synchronized int pub_UGoSendDTMF(char dtmf)
	{
		return UGoSendDTMF(dtmf);
	}
	
	public synchronized int pub_UGoSetMicMute(boolean enable)
	{
		return UGoSetMicMute(enable);
	}

    public synchronized boolean pub_UGoGetMicMute() {
        return UGoGetMicMute();
    }

	public synchronized int pub_UGoSetLoudSpeakerStatus(boolean enable)
	{
		return UGoSetLoudSpeakerStatus(enable);
	}

    public synchronized boolean pub_UGoGetLoudSpeakerStatus() {
        return UGoGetLoudSpeakerStatus();
    }

	/*add by Rookie for emodel interface 20130807*/
	public synchronized int pub_UGoGetEmodelValue(EmodelValue ev)
	{
		return UGoGetEmodelValue(ev.emodel_mos, ev.emodel_tr, ev.emodel_ppl, ev.emodel_burstr, ev.emodel_ie);
	}
	
	public synchronized int pub_UGoLoadMediaEngine(int mode)
	{
		return UGoLoadMediaEngine(mode);
	}
	
	public synchronized int pub_UGoInit()
	{
		//set callback function
		if (UGoCallbacks != null)
		{
			Callbacks(UGoCallbacks);
		}

		return UGoInit();
	}

    public synchronized void pub_UGoCallbacks(IUGoCallbacks cb) {
        UGoCallbacks = cb;
        Callbacks(UGoCallbacks);
    }
	
	public synchronized int pub_UGoDestroy()
	{
		return UGoDestroy();
	}
	
	public synchronized int pub_UGoRigister(String uid, int mode)
	{
		CustomLog.v("java pub_UGoRigister uid= "+uid+ "  mode="+mode);
		return UGoRigister(uid,mode);
	}
	
	public synchronized int pub_UGoUnRigister()
	{
		return UGoUnRigister();
	}
	
	public synchronized int pub_UGoCallPush(CallPushPara param) {
		return UGoCallPush(param);
	}
	
	public synchronized int pub_UGoDial(CallDialPara para, int version) {
		if(para!=null){
			CustomLog.v("java pub_UGoDial uid= "+para.uid+ "  phone="+para.phone+ "  mode="+para.mode);
		}
		return UGoDial(para);
	}
	
	public synchronized int pub_UGoConferenceDial(Object para, int version)//add by wuzhaoyang20140721
	{
		if(para!=null){
			CustomLog.v("java pub_UGoConferenceDial start ");
		}
		return UGoConferenceDial(para);
	}
	
	public synchronized int pub_UGoAnswer()
	{
		return UGoAnswer();
	}
	
	public synchronized int pub_UGoHangup(int reason)
	{
		//CustomLog.v("java pub_UGoHangup ");
		return UGoHangup(reason);
	}

	public synchronized String pub_UGoGetVersion()
	{
		return UGoGetVersion();
	}
	

	public synchronized  int pub_UGoTcpRecvMsg(int recvlen, byte[] recvbuf)
	{
		return UGoTcpRecvMsg(recvlen,recvbuf);
	}
	
	public synchronized int pub_UGoTcpUpdateState(int state)
	{
		return UGoTcpUpdateState(state);
	}
	
	public synchronized int pub_UGoUpdateSystemState(int state)
	{
		return UGoUpdateSystemState(state);
	}
	
	public synchronized void pub_setAndroidContext(Context context)
	{
		setAndroidContext(context);
	}

	public synchronized int pub_UGoStartRecord(MediaFileRecordPara para)
	{
		return UGoStartRecord(para);
	}

	public synchronized int pub_UGoStopRecord()
	{
		return UGoStopRecord();
	}

	public synchronized int pub_UGoPlayFile(MediaFilePlayPara para)
	{
		return UGoPlayFile(para);
	}

	public synchronized int pub_UGoStopFile()
	{
		return UGoStopFile();
	}
	
	public synchronized int pub_UGoSetVideoDevice(VideoCameraPara para)
	{
		return UGoVideoSetCamera(para);
	}
	
	public synchronized int pub_UGoStartVideo(int sendReceive)
	{
		return UGoStartVideo(sendReceive);
	}
	
	public synchronized int pub_UGoStopVideo(int sendReceive)
	{
		return UGoStopVideo(sendReceive);
	}
	
	public  synchronized int  pub_UGoGetCurrentUsedCamerainfo(VideoCameraPara info)
	{
		return UGoVideoGetCameraState(info);
	}

	public static abstract interface IUGoCallbacks {
		public abstract void eventCallback(int ev_type,int ev_reason,String message,String param);
		public abstract void sendCallback(byte[] message, int len);
		public abstract void traceCallback(String summary, String detail, int level);

        /**
         * rtp/rtcp packets encrypt callback
         * @param inMsg the original packet
         * @param outMsg the encrypted packet for to send
         * @param inLen the length of inMsg
         * @param outLen the length of outMsg
         */
        public abstract void encryptCallback(byte[] inMsg, byte[] outMsg, int inLen, int[] outLen);

        /**
         * rtp/rtcp packets decrypt callback
         * @param inMsg the packet have been encrypted
         * @param outMsg the packet decrypt from inMsg
         * @param inLen the length of inMsg
         * @param outLen the length of outMsg
         */
        public abstract void decryptCallback(byte[] inMsg, byte[] outMsg, int inLen, int[] outLen);
	}

}
