package com.yzx.listenerInterface;

/**
 * ������ɼ�����
 * @author xiaozhenhua
 *
 */
public interface RecordListener {
	
	/**
	 * �������
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����11:16:01
	 */
	public void onFinishedPlayingVoice();
	
	/**
	 * ¼�����(���60s)
	 * @param duration:¼���ļ�ʱ��
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����11:24:46
	 */
	public void onFinishedRecordingVoice(int duration);
	
}
