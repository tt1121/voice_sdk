package com.gl.softphone;


import android.content.Context;

/**
 * @author�� Rambo.fu
 * @date�� 2014-4-8
 * @description�� 
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
	 * ��ʼ����������
	 * @return 0 �ɹ� 1 ʧ��
	 */
	public synchronized int sm_voeInit()
	{
		return voeInit();
	}
	
	/**
	 * ж����������
	 */
	public synchronized void sm_voeDestroy()
	{
		voeDestroy();
	}
		

	/**
	 * ��ʼ����ͨ��
	 * @param payload  0 pcmu, 18 g729 
	 * @param remote_ip Զ��rtp��ַ
	 * @param remote_port Զ��rtp�˿�
	 set_audio_stream
	 */
	
	//public synchronized void sm_voeSetAudioStream(int payload,String remote_ip,int remote_port)
	//{
	//	SetAudioStream(payload, remote_ip, remote_port);
//	}
	
	
	/**
	 * ֹͣ��������
	 */
	public synchronized void sm_voeStopAuido()
	{
		voeStopAuido();
	}
	/**
	 *�����������浱ǰ״̬
	 * state value is  (1 �Ѽ��أ�2 ���У�3 ����������)
	 */
	public synchronized void sm_voeSetState(int state)
	{
		 voeSetState(state);
	}
	
	/**
	 * ��ȡ�������浱ǰ״̬
	 * @return 0 δ���أ�1 �Ѽ��أ�2 ���У�3 ����������
	 */
	public synchronized int sm_voeGetState()
	{
		return voeGetState();
	}
	
	/**
	 * ����dtmf
	 * @param dtmf dtmfֵ��0-9��*��#��
	 */
	public synchronized void sm_voeSendDTMF(char dtmf)
	{
		voeSendDTMF(dtmf);
	}
	
	/**
	 * �Ƿ���
	 * @param enable true ����ģʽ��false ����ģʽ
	 */
	public synchronized void sm_voeMuteMic(boolean enable)
	{
		voeMuteMic(enable);
	}
	
	
	/**
	 * �Ƿ�����
	 * @param enable true ����ģʽ��false ����ģʽ
	 */
	/*public synchronized void sm_voeEnableSpeaker(boolean enable)
	{
		voeEnableSpeaker(enable);
	}
	*/
	
	/**
	 * ����������Ϥ
	 * @param VqeConfig vqe����
	 * @param RtpConfig rtp��ز���
	 * ˵������������ο� VqeConfig��RtpConfig
	 */
	public synchronized void sm_voeSetConfig(int moduleID,Object obj,int version)
	{
		voeSetConfig(moduleID,obj,version);
	}

	
	/**
	 * ��ȡ���ò���
	 * @param VqeConfig vqe����
	 * @param RtpConfig rtp��ز���
	 * ˵������������ο� VqeConfig��RtpConfig���Ҳ���Ϊnull
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
	 * �Ƿ���������
	 * @return true ����������false δ������
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
	 * ����android ������
	 * @param context ȫ�ֵ�context
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
