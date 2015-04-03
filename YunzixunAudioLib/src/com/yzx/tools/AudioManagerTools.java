package com.yzx.tools;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.service.ConnectionControllerService;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import android.os.Vibrator;

/**
 * 音频管理与播放提示音
 * @author xiaozhenhua
 *
 */
public class AudioManagerTools {

	private static String TAG = "AudioPlayer";
	
	private static AudioManagerTools audioPlayer ;
	public static AudioManagerTools getInstance(){
		if(audioPlayer == null){
			audioPlayer = new AudioManagerTools();
		}
		return audioPlayer;
	}
	
	//private AudioManager mAudioManager;
	private Vibrator mVibrator;
	private MediaPlayer mRingerPlayer;
	private OnAudioFocusChangeListener audioFocusChangedListener = null;
	//public static byte[] dialing_tone = null;
	//private int SDKVERSION;
	public AudioManagerTools(){
		//SDKVERSION = Integer.parseInt(android.os.Build.VERSION.SDK);
		//mAudioManager = ((AudioManager) ConnectionControllerService.getInstance().getSystemService(Context.AUDIO_SERVICE));
		if(ConnectionControllerService.getInstance() != null){
			mVibrator = (Vibrator) ConnectionControllerService.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
		}
	}
	
	/**
	 * 播放来电音
	 * @param RingtoneManagerType
	 * @param vibrator
	 * @author: xiaozhenhua
	 * @data:2013-2-19 下午4:39:04
	 */
	public synchronized void startRinging(boolean isVibrator) {
		/*if(mAudioManager != null){
			mAudioManager.setMode(AudioManager.MODE_RINGTONE);
		}*/
		try {
			if(ConnectionControllerService.getInstance() != null && mVibrator != null){
				if (isVibrator) {
	        		long[] patern = { 0, 1000, 1000 };
					mVibrator.vibrate(patern, 1);
	        	}
				if (mRingerPlayer == null) {
					mRingerPlayer = new MediaPlayer();
				}
				mRingerPlayer.setDataSource(ConnectionControllerService.getInstance(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
				mRingerPlayer.setAudioStreamType(AudioManager.STREAM_RING);
				mRingerPlayer.prepare();
				mRingerPlayer.setLooping(true);
				mRingerPlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭来电响铃
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:21:49
	 */
	public synchronized void stopRinging() {
		try {
			if (mRingerPlayer != null) {
				mRingerPlayer.stop();
				mRingerPlayer.release();
				mRingerPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mVibrator!=null) {
			mVibrator.cancel();
		}
		if(android.os.Build.BRAND != null && (android.os.Build.BRAND.toString().contains("Xiaomi") 
				|| android.os.Build.BRAND.toString().equals("Xiaomi"))){
			if(ConnectionControllerService.getInstance() != null){
				SystemMediaConfig.restoreMediaConfig(null,ConnectionControllerService.getInstance());
			}
		}
	}
	
	/*public int getCurrentAudioMode() {
		return mAudioManager.getMode();
	}*/
	/**
	 * 播放去电提示音
	 * @param mContext
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:56:28
	 */
	public void startCallRinging(final String fileName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(ConnectionControllerService.getInstance() != null){
					byte[] dial = play_dialing_tone(fileName,ConnectionControllerService.getInstance());
					if(dial != null){
						UGoAPIParam.getInstance().stMediaFilePlayPara.audioData = dial;
						UGoAPIParam.getInstance().stMediaFilePlayPara.iFileFormat = UGoAPIParam.kME_FileFormatPcm16kHzFile;
						UGoAPIParam.getInstance().stMediaFilePlayPara.iDirect = 0;
						UGoAPIParam.getInstance().stMediaFilePlayPara.iLoop = 1;
						UGoAPIParam.getInstance().stMediaFilePlayPara.mode = 1;
						UGoAPIParam.getInstance().stMediaFilePlayPara.data_size = UGoAPIParam.getInstance().stMediaFilePlayPara.audioData.length;
						int result = UGoManager.getInstance().pub_UGoPlayFile(UGoAPIParam.getInstance().stMediaFilePlayPara);
						CustomLog.v("CURRENT_PLAYER:"+result);
					}else{
						CustomLog.v("CURRENT_FILE is null");
					}
				}else{
					CustomLog.v("CURRENT_PLAYER:ConnectionControllerService is null");
				}
			}
		}).start();
	}
	
	/**
	 * 停止去电提示音
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:56:12
	 */
	public void stopCallRinging() {
		CustomLog.v("CURRENT_STOP_PLAYER ... ");
		UGoManager.getInstance().pub_UGoStopFile();
	}
	
	
	/**
	 * 是否终止外部媒体播放
	 * @param enable:false 抢占媒体资源,停卡其它媒体播放   true:恢复其它媒体播放
	 * @author: xiaozhenhua
	 * @data:2014-7-23 下午12:17:39
	 */
	/*public synchronized void enableOtherMediaPlay(boolean enable) {
		if (Build.VERSION.SDK_INT < 8 || mAudioManager == null) {
			return;
		}
		CustomLog.v(enable+"   ON OTHER MEDIAPLAYER ... ");
		
		if(audioFocusChangedListener == null){
			audioFocusChangedListener = createOnAudioFocusChangeListener();
		}
		if(enable){
			mAudioManager.abandonAudioFocus(audioFocusChangedListener);
		}else{
			mAudioManager.requestAudioFocus(audioFocusChangedListener, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		}
		CustomLog.v("MUSIC_ACTIVITY:"+mAudioManager.isMusicActive());
	}*/
	
	private OnAudioFocusChangeListener createOnAudioFocusChangeListener() {
		return new OnAudioFocusChangeListener() {
			@Override
			public void onAudioFocusChange(int focusChange) {
				if(AudioManager.AUDIOFOCUS_REQUEST_GRANTED == focusChange){
					CustomLog.v("音频资源审请成功");
				}else if(AudioManager.AUDIOFOCUS_REQUEST_FAILED == focusChange){
					CustomLog.v("音频资源审请失败");
				}else if( AudioManager.AUDIOFOCUS_GAIN == focusChange){
					CustomLog.v("获取音频资源");
				}else if( AudioManager. AUDIOFOCUS_LOSS == focusChange){
					CustomLog.v("失去音频资源");
				}
				CustomLog.v("ON_AUDIO_FOCUS_CHANGE:" + focusChange);
			}
		};
	}
	
	public static byte[] play_dialing_tone(String fileName,Context mContext) {
		//if (dialing_tone == null) {
			//dialing_tone = convertStream2byteArrry(fileName,mContext);
		//}
		//return dialing_tone;
		return convertStream2byteArrry(fileName,mContext);
	}
	
	/**
	 * 将音频文件转换成byteArray
	 * @param filepath
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-7-23 下午12:37:50
	 */
	public static byte[] convertStream2byteArrry(String filepath, Context mContext) {
		InputStream inStream = null;
		try {
			inStream = mContext.getResources().getAssets().open(filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		byte[] mybyte = null;
		try {
			if(inStream != null){
				inStream.available();
				while ((i = inStream.read()) != -1) {
					baos.write(i);
				}
				inStream.close();
				mybyte = baos.toByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mybyte;
	}
	
	/**
	 * 通话时用于设置免提或内放
	 * @param loudspeakerOn true:外放   false:内放
	 * @author: xiaozhenhua
	 * @data:2013-2-19 下午4:27:47
	 */
	public synchronized void setSpeakerPhoneOn(boolean loudspeakerOn) {
		UGoManager.getInstance().pub_UGoSetLoudSpeakerStatus(loudspeakerOn);
		/*CustomLog.v(TAG,"SET_PLAYOUT_SPEAKER:"+loudspeakerOn);
        if ((3 == SDKVERSION) || (4 == SDKVERSION)) {
            // 1.5 and 1.6 devices
            if (loudspeakerOn) {
                // route audio to back speaker
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                CustomLog.v(TAG,"3_SET_MODE:AudioManager.MODE_NORMAL");
            } else {
                // route audio to earpiece
            	CustomLog.v(TAG,"SDK 1.5 and 1.6 devices:route audio to earpiece success");
            	mAudioManager.setMode(AudioManager.MODE_IN_CALL);
            	CustomLog.v(TAG,"4_SET_MODE:AudioManager.MODE_IN_CALL");
            }
        } else {
            // 2.x devices
            if ((android.os.Build.BRAND.equals("Samsung") ||
                            android.os.Build.BRAND.equals("samsung")) &&
                            ((5 == SDKVERSION) || (6 == SDKVERSION) ||
                            (7 == SDKVERSION))) {
                // Samsung 2.0, 2.0.1 and 2.1 devices
                if (loudspeakerOn) {
                    // route audio to back speaker
                	mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                	CustomLog.v(TAG,"5_SET_MODE:AudioManager.MODE_IN_CALL");
                	mAudioManager.setSpeakerphoneOn(loudspeakerOn);
                    CustomLog.v(TAG,"Samsung and Samsung 2.1 and down devices:route audio to  back speaker success");
                } else {
                    // route audio to earpiece
                	mAudioManager.setSpeakerphoneOn(loudspeakerOn);
                	mAudioManager.setMode(AudioManager.MODE_NORMAL);
                	CustomLog.v(TAG,"6_SET_MODE:AudioManager.MODE_NORMAL");
                	CustomLog.v(TAG,"Samsung and Samsung 2.1 and down devices:route audio to  earpiece success");
                }
            } else {
				if (isMode()) {
					String modelString = Build.MODEL.replaceAll(" ", "");
					if (loudspeakerOn && (modelString.equalsIgnoreCase("HUAWEIY300-0000") 
							|| modelString.equalsIgnoreCase("HUAWEIC8813"))) {
						mAudioManager.setMode(AudioManager.MODE_NORMAL);
					}
					mAudioManager.setMode(AudioManager.MODE_IN_CALL);
					CustomLog.v(TAG, "7_SET_MODE:AudioManager.MODE_IN_CALL");
				} else {
					mAudioManager.setMode(AudioManager.MODE_NORMAL);
					CustomLog.v(TAG, "8_SET_MODE:AudioManager.MODE_NORMAL");
				}
				mAudioManager.setSpeakerphoneOn(loudspeakerOn);
            }
        }
        currentPlayerDevices();*/
    }
	
	public boolean isSpeakerphoneOn(){
		return UGoManager.getInstance().pub_UGoGetLoudSpeakerStatus();
	}
	
	/*public boolean getPlayoutSpeaker() {
		return mAudioManager != null ? mAudioManager.isSpeakerphoneOn() : false;
	}*/
	/**
	 * 测式函数
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-4-10 上午11:14:23
	 */
	/*public void currentPlayerDevices(){
		CustomLog.v(TAG,"CURRENT_PALYER_DEVICES:");
		if(mAudioManager.isBluetoothA2dpOn()){
			CustomLog.v(TAG,"BLUETOOTAH");
		}else if(mAudioManager.isSpeakerphoneOn()){
			CustomLog.v(TAG,"外放");
		}else if(mAudioManager.isWiredHeadsetOn()){
			CustomLog.v(TAG,"有线耳机");
		}else if(!mAudioManager.isSpeakerphoneOn()){
			CustomLog.v(TAG,"内放");
		}else{
			CustomLog.v(TAG,"其它设备");
		}
		CustomLog.v(TAG,"CURRENT_PLAYER_MODE:"+mAudioManager.getMode());
		switch(mAudioManager.getMode()){
		case AudioManager.MODE_INVALID:
			CustomLog.v(TAG,"CURRENT_PLAYER_MODE:AudioManager.MODE_INVALID");
			break;
		case AudioManager.MODE_CURRENT:
			CustomLog.v(TAG,"CURRENT_PLAYER_MODE:AudioManager.MODE_CURRENT");
			break;
		case AudioManager.MODE_NORMAL:
			CustomLog.v(TAG,"CURRENT_PLAYER_MODE:AudioManager.MODE_NORMAL");
			break;
		case AudioManager.MODE_RINGTONE:
			CustomLog.v(TAG,"CURRENT_PLAYER_MODE:AudioManager.MODE_RINGTONE");
			break;
		case AudioManager.MODE_IN_CALL:
			CustomLog.v(TAG,"CURRENT_PLAYER_MODE:AudioManager.MODE_IN_CALL");
			break;
		}
		CustomLog.v(TAG,"------------------------------");
	}*/
	
	/*public void autoSetAudioMode(){
		CustomLog.v(TAG,
				"10_SET_MODE:"
						+ (getCurrentAudioMode() == AudioManager.MODE_IN_CALL ? "AudioManager.MODE_NORMAL"
								: "AudioManager.MODE_IN_CALL"));
		int mode = getCurrentAudioMode() == AudioManager.MODE_IN_CALL ? AudioManager.MODE_NORMAL
				: AudioManager.MODE_IN_CALL;
		mAudioManager.setMode(mode);
		AudioConfig.saveConfigAudioMode(true,mode);
		currentPlayerDevices();
	}*/
	
	/**
	 * 判断品牌或机型是否需要设置AudioManager的Mode
	 * @return
	 * @author: xiaozhenhua
	 * @data:2013-4-10 上午11:56:11
	 */
	public boolean isMode(){
		String brandString = android.os.Build.BRAND;
    	String modelString = "";
		if (Build.MODEL != null)
			modelString = Build.MODEL.replaceAll(" ", "");
		CustomLog.v(TAG,"phone band ="+brandString);
		CustomLog.v(TAG,"phone modelString = " + modelString);
		return brandString != null 
            			&&(brandString.equalsIgnoreCase("yusu")
                    	|| brandString.equalsIgnoreCase("yusuH701")
                    	|| brandString.equalsIgnoreCase("yusuA2")
                    	|| brandString.equalsIgnoreCase("qcom")
                    	|| brandString.equalsIgnoreCase("motoME525")
                    	|| (brandString.equalsIgnoreCase("Huawei") 
                    			&& !modelString.equalsIgnoreCase("HUAWEIY220T")
                    			&& !modelString.equalsIgnoreCase("HUAWEIT8600")
                    			&& !modelString.equalsIgnoreCase("HUAWEIY310-T10")
                    			&& !modelString.equalsIgnoreCase("HUAWEIT8951"))
                    	|| brandString.equalsIgnoreCase("lge")
                    	|| brandString.equalsIgnoreCase("SEMC")
                    	|| (brandString.equalsIgnoreCase("ZTE")
                    			&& !modelString.equalsIgnoreCase("ZTEU880E")
                    			&& !modelString.equalsIgnoreCase("ZTEV985")
                    			&& !modelString.equalsIgnoreCase("ZTE-TU880")
                    			&& !modelString.equalsIgnoreCase("ZTE-TU960s")
                    			&& !modelString.equalsIgnoreCase("ZTEU793"))
                    	|| modelString.equalsIgnoreCase("LenovoS850e")
                    	|| modelString.equalsIgnoreCase("LenovoA60")
                    	|| modelString.equalsIgnoreCase("HTCA510e")
                    	|| (brandString.equalsIgnoreCase("Coolpad")
                    			&& modelString.equalsIgnoreCase("7260"))
                    	|| modelString.equalsIgnoreCase("Coolpad5890")
                    	|| brandString.equalsIgnoreCase("ChanghongV10")
                    	|| modelString.equalsIgnoreCase("MI2")
                    	|| modelString.equalsIgnoreCase("MI2S")
                    	|| modelString.equalsIgnoreCase("MT788")
                    	|| modelString.equalsIgnoreCase("MI-ONEPlus")
                    	|| modelString.equalsIgnoreCase("HUAWEIP6")
                    	|| modelString.equalsIgnoreCase("LenovoA780"));
	}
}
