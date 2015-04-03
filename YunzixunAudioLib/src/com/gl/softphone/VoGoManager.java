package com.gl.softphone;


import android.content.Context;

/**
 * @author： Rambo.fu
 * @date： 2014-4-8
 * @description： 
 */
public class VoGoManager 
{
	private native int voeLoadMediaEngine();
	private native void voeSetAndroidContext(Context context);
	//private native int setAndroidApi(int level);
	
	private native int voeInit();
	private native void voeDestroy();

	private native int voeCreateAudioStream();
	private native void voeDeleteAudioStream();
	private native void voeSetAudioStream(AudioInfo audioInfo);
	

	private native void voeEnableAudioPlayload(boolean enable);//
	private native void voeEnableAudioSend(boolean enable);//
	native void voeEnableAudioReceive(boolean enable);//
	private native int voeSetState(int state);
	private native int voeGetState();
	private native int voeSetMicMute(boolean enable);//
	private native int voeSetMicVoulme(int volume);
	private native int voeSetSpeakerMute(boolean enable);//
	private native int voeSetSpeakerVolume(int volume);
	
	
	private native void voeSendDTMF(char dtmf);
	private native void voeMuteMic(boolean enable);//
	
	private native int voeSetLogCfg(LogCfg cfg);
	private native boolean voeSoundStarted();
	
	private native int voePlayFile(MediaFilePlayPara param);
	private native int voeStopFile();
	
	private native void voeStopAuido();

	private native String voeGetVersion();
	private native void voeSetConfig(int moduleID,Object object,int version);
	private native int voeGetConfig(int moduleID, Object onject,int version);
	private native int voeSetAndroidApi(int level);
	
	private native void Callbacks(IVoGoCallbacks cb);
	
	IVoGoCallbacks VoGoCallbacks;
	public static VoGoManager Single = null;

	public static VoGoManager getInstance(IVoGoCallbacks cb) {
		if (Single == null) {
			Single = new VoGoManager();
			Single.VoGoCallbacks = cb;
		}
		return Single;
	}

	static{
		if(android.os.Build.VERSION.SDK_INT < 9)
		{
			System.loadLibrary("OpenSLES");
		}
		System.loadLibrary("VoGo");
	}
	
	private VoGoManager()
	{
		
	}
	
	/**
	 * 初始化语音引擎
	 * @return 0 成功 1 失败
	 */
	public synchronized int sm_voeInit()
	{
		return voeInit();
	}
	
	/**
	 * 卸载语音引擎
	 */
	public synchronized void sm_voeDestroy()
	{
		voeDestroy();
	}
		

	/**
	 * 开始语音通话
	 * @param payload  0 pcmu, 18 g729 
	 * @param remote_ip 远程rtp地址
	 * @param remote_port 远程rtp端口
	 set_audio_stream
	 */
	
	//public synchronized void sm_voeSetAudioStream(int payload,String remote_ip,int remote_port)
	//{
	//	SetAudioStream(payload, remote_ip, remote_port);
//	}
	
	
	/**
	 * 停止语音传输
	 */
	public synchronized void sm_voeStopAuido()
	{
		voeStopAuido();
	}
	/**
	 *设置语音引擎当前状态
	 * state value is  (1 已加载，2 空闲，3 语音传输中)
	 */
	public synchronized void sm_voeSetState(int state)
	{
		 voeSetState(state);
	}
	
	/**
	 * 获取语音引擎当前状态
	 * @return 0 未加载，1 已加载，2 空闲，3 语音传输中
	 */
	public synchronized int sm_voeGetState()
	{
		return voeGetState();
	}
	
	/**
	 * 发送dtmf
	 * @param dtmf dtmf值（0-9，*，#）
	 */
	public synchronized void sm_voeSendDTMF(char dtmf)
	{
		voeSendDTMF(dtmf);
	}
	
	/**
	 * 是否静音
	 * @param enable true 静音模式，false 正常模式
	 */
	public synchronized void sm_voeMuteMic(boolean enable)
	{
		voeMuteMic(enable);
	}
	
	
	/**
	 * 是否免提
	 * @param enable true 免提模式，false 正常模式
	 */
	/*public synchronized void sm_voeEnableSpeaker(boolean enable)
	{
		voeEnableSpeaker(enable);
	}
	*/
	
	/**
	 * 设置配置熟悉
	 * @param VqeConfig vqe参数
	 * @param RtpConfig rtp相关参数
	 * 说明：参数具体参考 VqeConfig，RtpConfig
	 */
	public synchronized void sm_voeSetConfig(int moduleID,Object obj,int version)
	{
		voeSetConfig(moduleID,obj,version);
	}

	
	/**
	 * 获取配置参数
	 * @param VqeConfig vqe参数
	 * @param RtpConfig rtp相关参数
	 * 说明：参数具体参考 VqeConfig，RtpConfig，且不能为null
	 */
	public synchronized int sm_voeGetConfig(int moduleID,Object object,int version)
	{
		return voeGetConfig(moduleID ,object, version);
	}

	/*add by Rookie 2013.08.07*/
	/*public synchronized int sm_voeGetEmodelValue(EmodelValue ev)
	{
		return voeGetEmodelValue(ev.emodel_mos,ev.emodel_tr, ev.emodel_ppl, ev.emodel_burstr, ev.emodel_ie);
	}*/
	/*add by charlie yuan 2014.06.10 */
	public synchronized int sm_voeSetLogCfg(LogCfg cfg)
	{
		return voeSetLogCfg(cfg);
	}
	
	/**
	 * 是否正在语音
	 * @return true 正在语音，false 未在语音
	 */
	public synchronized boolean sm_voeSoundStarted()
	{
		return voeSoundStarted();
	}
	
	

		
	public synchronized String sm_voeGetVersion()
	{
		return voeGetVersion();
	}
	//add by charlie yuan 2014.06.09 

	public synchronized int sm_voeLoadMediaEngine()
	{
		return voeLoadMediaEngine();
	}
	
	/**
	 * 设置android 上下文
	 * @param context 全局的context
	 */
	public synchronized void sm_voeSetAndroidContext(Context context)
	{
		voeSetAndroidContext(context);
	}
	
	public synchronized int sm_voeSetAndroidApi(int level)
	{
		return voeSetAndroidApi(level);
	}

	public synchronized int sm_voeCreateAudioStream()
	{
		return voeCreateAudioStream();
	}
	
	public synchronized void sm_voeDeleteAudioStream()
	{
		 voeDeleteAudioStream();
	}
	public synchronized void sm_voeSetAudioStream(AudioInfo audioInfo )
	{
		voeSetAudioStream(audioInfo);
	}
	public synchronized void sm_voeEnableAudioPlayod(boolean enable)
	{
		voeEnableAudioPlayload(enable);
	}
	
	public synchronized void sm_voeEnableAudioSend(boolean enable)
	{
		voeEnableAudioSend(enable);
	}
	
	public synchronized void sm_voeEnableAudioReceive(boolean enable)
	{
		voeEnableAudioReceive(enable);
	}

	public synchronized void sm_voeSetMicMute(boolean enable)
	{
		voeSetMicMute(enable);
	}
	public synchronized int sm_voeSetMicVoulme(int volume)
	{
		return voeSetMicVoulme(volume);
	}
	public synchronized void sm_voeSetSpeakerMute(boolean enable)
	{
		voeSetSpeakerMute(enable);
	}
	public synchronized int sm_voeSetSpeakerVolume(int volume)
	{
		return voeSetSpeakerVolume(volume);
	}
	
	public synchronized int sm_voePlayFile(MediaFilePlayPara param)
	{
		return voePlayFile(param);
	}
	
	public synchronized int sm_voeStopFile()
	{
		return voeStopFile();
	}
	//end add by charlie yuan 2014.06.09

	
	public static abstract interface IVoGoCallbacks
	{
		public abstract void eventCallback(int ev_type, int ev_reason, String something, String param);
		public abstract void sendCallback(int media_type, int data_type, byte[] message, int len);
		public abstract void traceCallback(String summary, String detail, int level);
	}
	
}
