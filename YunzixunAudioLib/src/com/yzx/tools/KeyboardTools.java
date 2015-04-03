package com.yzx.tools;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class KeyboardTools {
	
	private static KeyboardTools keyBoard;
	private Context mContext;
	
	public static KeyboardTools getInstance(Context activityContext){
		if(keyBoard == null){
			keyBoard = new KeyboardTools(activityContext);
		}
		return keyBoard;
	}
	
	
	private Object mToneGeneratorLock = new Object();		// ∞¥º¸…˘“Ù
	private ToneGenerator mToneGenerator;
	private AudioManager audioManager;
	
	public KeyboardTools(Context mC){
		mContext = mC;
		initTonePlayer();
	}
	private void initTonePlayer() {
		/*int j;
		int i = 1;
		android.content.ContentResolver contentresolver = mContext.getContentResolver();
		String obj = "dtmf_tone";
		j = android.provider.Settings.System.getInt(contentresolver,obj, i);
		if (j == i){
			j = i;
		}else{
			j = 0;
		}*/
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				try {
					mToneGenerator = new ToneGenerator(3, 60);
					((Activity)mContext).setVolumeControlStream(3);
				} catch (RuntimeException e) {
					mToneGenerator = null;
				}
			}
		}
	}
	
	public void playKeyBoardVoice(int tone) {
		new ThreadPlayTone(tone).start();
	}
	class ThreadPlayTone extends Thread{
		int tone = -1;
		public ThreadPlayTone(int t){
			tone = t;
		}
		@Override
		public void run() {
			if(tone > 0){
				if (audioManager == null) {
					audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
				}
				int ringerMode = audioManager.getRingerMode();
				if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
					return;
				}
				if (mToneGenerator == null) {
					return;
				}
				// …Ë÷√≤¶∫≈…˘“ÙŒ™100∫¡√Î
				mToneGenerator.startTone(tone, 100);
			}
		}
	}
}
