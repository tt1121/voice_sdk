package com.yzx.listenerInterface;

/**
 * 播放完成监听器
 * @author xiaozhenhua
 *
 */
public interface RecordListener {
	
	/**
	 * 播放完成
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午11:16:01
	 */
	public void onFinishedPlayingVoice();
	
	/**
	 * 录音完成(最大60s)
	 * @param duration:录音文件时长
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午11:24:46
	 */
	public void onFinishedRecordingVoice(int duration);
	
}
