package com.yzx.tools;

import com.yzx.tcp.packet.PacketDfineAction;

import android.content.Context;
import android.media.AudioManager;

/**
 * �������ڱ���ϵͳMedia�����뻹ԭ
 * 
 * @author xiaozhenhua
 * 
 */
public class SystemMediaConfig {

	/**
	 * ��ʼϵͳMedia����
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-2 ����9:57:01
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
	 * ��ԭMedia����
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-2 ����10:13:47
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
