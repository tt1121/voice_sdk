package com.yzx.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.yzx.listenerInterface.RecordListener;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;

/**
 * 录音工具类
 * @author xiaozhenhua
 *
 */
public class RecordingTools {

	
	private static RecordingTools recordTools;
	
	public static RecordingTools getInstance(){
		if(recordTools == null){
			recordTools = new RecordingTools();
		}
		return recordTools;
	}
	
	private MediaRecorder mRecorder = null; // 录音
	private boolean isStop = false;
	private Object obj = new Object();
	private MediaPlayer mPlayer = null; // 播放
	private MediaPlayer mGetDurationPlay;//获取音频文件时长
	
	
	/**
	 * 获取录音文件的时长
	 * @param filePath
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午10:01:17
	 */
	public int getDuration(String filePath){
		int duration = 0;
		if(mGetDurationPlay == null){
			mGetDurationPlay = new MediaPlayer();
		}
		try {
			FileInputStream fis = new FileInputStream(filePath);
			if (fis != null){
				mGetDurationPlay.setDataSource(fis.getFD());
				mGetDurationPlay.prepare();
				duration = mGetDurationPlay.getDuration();
				fis.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (mGetDurationPlay != null) {
				mGetDurationPlay.reset();
				mGetDurationPlay.release();
				mGetDurationPlay = null;
			}
		}
		return duration;
	}

	/**
	 * 开始录音
	 * @param toUid:接收者的UID
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午10:08:14
	 */
	public boolean startVoiceRecord(final String recordPath,final RecordListener recordListener){
		boolean isRecord = true;
		stopVoiceRecord();
		if(mRecorder == null){
			mRecorder = new MediaRecorder();
			mRecorder.setOnInfoListener(new OnInfoListener() {
				public void onInfo(MediaRecorder mr, int what, int extra) {
					CustomLog.v( "recorder Info: what=" + what + " extra=" + extra);
				}
			});
			mRecorder.setOnErrorListener(new OnErrorListener() {
				public void onError(MediaRecorder mr, int what, int extra) {
					CustomLog.e( "recorder onError: what=" + what + " extra=" + extra);
				}
			});
		}
		//final String recordPath = FileWRTools.createAudioFileName(toUid);
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		mRecorder.setOutputFile(recordPath);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
			mRecorder.start();
			isStop = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						synchronized (obj) {
							obj.wait(59*1000);
						}
						if(isStop){
							CustomLog.v("自动停止录音  ... ");
							stopVoiceRecord();
						}
						int duration = getDuration(recordPath);
						recordListener.onFinishedRecordingVoice((duration/1000)+(duration%1000>500?1:0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (IllegalStateException e) {
			isRecord = false;
			e.printStackTrace();
		} catch (IOException e) {
			isRecord = false;
			e.printStackTrace();
		}
		return isRecord;
		//return recordPath;
	}
	
	/**
	 * 停卡录音
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午10:05:16
	 */
	public void stopVoiceRecord(){
		if(mRecorder != null){
			synchronized (obj) {
				isStop = false;
				obj.notifyAll();
			}
			CustomLog.v("手动停止录音  ... ");
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
	
	/**
	 * 当前是否正在播放
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午10:17:45
	 */
	public boolean isPlaying() {
		return (mPlayer != null) ? mPlayer.isPlaying() : false;
	}
	
	/**
	 * 播放录音
	 * @param filePath:需要播放的文件路径
	 * @param playerCompletionListener:播放完成的监听器(不需要回调时可以为空)
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午10:34:42
	 */
	public void startPlayerVoice(String filePath,final RecordListener recordListener){
		stopPlayerVoice();
		if(mPlayer == null){
			mPlayer = new MediaPlayer();
		}
		try {
			FileInputStream fis = new FileInputStream(filePath);
			mPlayer.setDataSource(fis.getFD());
			mPlayer.prepare();
			mPlayer.start();
			fis.close();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if(recordListener != null){
						recordListener.onFinishedPlayingVoice();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			CustomLog.v( "startPlay IOException : " + e.toString());
		} catch(Exception e){
			e.printStackTrace();
			CustomLog.v( "startPlay Exception : " + e.toString());
		}
	}
	/**
	 * 停止播放
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午10:23:20
	 */
	public void stopPlayerVoice(){
		if(mPlayer != null){
			CustomLog.v( "stop play");
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
		}
	}
}
