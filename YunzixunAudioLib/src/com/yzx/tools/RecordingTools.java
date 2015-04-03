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
 * ¼��������
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
	
	private MediaRecorder mRecorder = null; // ¼��
	private boolean isStop = false;
	private Object obj = new Object();
	private MediaPlayer mPlayer = null; // ����
	private MediaPlayer mGetDurationPlay;//��ȡ��Ƶ�ļ�ʱ��
	
	
	/**
	 * ��ȡ¼���ļ���ʱ��
	 * @param filePath
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����10:01:17
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
	 * ��ʼ¼��
	 * @param toUid:�����ߵ�UID
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����10:08:14
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
							CustomLog.v("�Զ�ֹͣ¼��  ... ");
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
	 * ͣ��¼��
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����10:05:16
	 */
	public void stopVoiceRecord(){
		if(mRecorder != null){
			synchronized (obj) {
				isStop = false;
				obj.notifyAll();
			}
			CustomLog.v("�ֶ�ֹͣ¼��  ... ");
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
	
	/**
	 * ��ǰ�Ƿ����ڲ���
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����10:17:45
	 */
	public boolean isPlaying() {
		return (mPlayer != null) ? mPlayer.isPlaying() : false;
	}
	
	/**
	 * ����¼��
	 * @param filePath:��Ҫ���ŵ��ļ�·��
	 * @param playerCompletionListener:������ɵļ�����(����Ҫ�ص�ʱ����Ϊ��)
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����10:34:42
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
	 * ֹͣ����
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����10:23:20
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
