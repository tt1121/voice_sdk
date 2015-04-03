package com.yzx.tools;

import com.yzx.tcp.packet.PacketDfineAction;

import android.content.Context;
import android.media.AudioManager;

/**
 * 该类用于保存系统Media配置与还原
 * 
 * @author xiaozhenhua
 * 
 */
public class SystemMediaConfig {

	/**
	 * 初始系统Media配置
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-2 上午9:57:01
	 */
	public static void initMediaConfig(AudioManager am, Context mContext) {
		if (am == null) {
			am = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
		}
		mContext.getApplicationContext()
				.getSharedPreferences(PacketDfineAction.PREFERENCE_NAME, 0)
				.edit().putInt("CALL_MODE_TYPE", am.getMode())
				.putBoolean("CALL_MODE_SPEAKERPHONEON", am.isSpeakerphoneOn())
				/* .putInt("RINGER_MODE", am.getRingerMode()) */.commit();

	}

	/**
	 * 还原Media配置
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-2 上午10:13:47
	 */
	public static void restoreMediaConfig(AudioManager am, Context mContext) {
		if (am == null) {
			am = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
		}
		int mode = mContext.getSharedPreferences(
				PacketDfineAction.PREFERENCE_NAME, 0).getInt("CALL_MODE_TYPE",
				AudioManager.MODE_NORMAL);
		int ringerMode = mContext.getSharedPreferences(
				PacketDfineAction.PREFERENCE_NAME, 0).getInt("RINGER_MODE",
				AudioManager.MODE_IN_CALL);
		boolean isSpeakerphoneOn = mContext.getSharedPreferences(
				PacketDfineAction.PREFERENCE_NAME, 0).getBoolean(
				"CALL_MODE_SPEAKERPHONEON", false);
		//CustomLog.v("CURRENT_SPEAKERPHONE_MODE:"+am.getMode());
		am.setSpeakerphoneOn(isSpeakerphoneOn);
		am.setMode(mode);
		//CustomLog.v("SET_CURRENT_SPEAKERPHONE_MODE:"+am.getMode());
	}
}
