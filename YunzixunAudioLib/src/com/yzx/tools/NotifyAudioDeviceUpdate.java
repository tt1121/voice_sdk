package com.yzx.tools;

import com.yzx.listenerInterface.AudioDeviceUpdateListener;

public class NotifyAudioDeviceUpdate {

	public static AudioDeviceUpdateListener audioDeviceUpdateListener;
	
	public static void addAudioDeviceUpdateListener(AudioDeviceUpdateListener adul){
		audioDeviceUpdateListener = adul;
	}
	
	public static void notifyAudipDevicesUpdate(){
		if(audioDeviceUpdateListener != null){
			audioDeviceUpdateListener.onAudioDeviceUpdate();
		}
	}
	
}
