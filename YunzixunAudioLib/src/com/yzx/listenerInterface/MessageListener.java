package com.yzx.listenerInterface;

import java.util.ArrayList;
import java.util.HashMap;

import com.yzx.tcp.packet.UcsMessage;
import com.yzx.tcp.packet.UcsStatus;

/**
 * IM��Ϣ�ص��ӿ�
 * 
 * @author xiaozhenhua
 * 
 */
public interface MessageListener {

	/**
	 * �յ���Ϣ
	 * 
	 * @param message
	 * @author: xiaozhenhua
	 * @data:2014-5-13 ����5:27:01
	 */
	public void onReceiveUcsMessage(UcsReason reason, UcsMessage message);

	/**
	 * ����IM��Ϣ�ɹ���ʧ�ܵĻص�
	 * 
	 * @param reason
	 *            :״̬��
	 * @param message
	 *            :��Ϣ��
	 * @author: xiaozhenhua
	 * @data:2014-5-13 ����5:27:18
	 */
	public void onSendUcsMessage(UcsReason reason, UcsMessage message);
	
	/**
	 * �ļ����ȷ��ͼ����ӿ�
	 * @param progress
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����11:39:02
	 */
	public void onSendFileProgress(int progress);
	
	
	/**
	 * �����ļ����ȼ����ӿ�(�����ļ�)
	 * @param msgId
	 * @param sizeProgrss:�ܼ��ܴ�С
	 * @param currentProgress����ǰ���ش�С
	 * @author: xiaozhenhua
	 * @data:2014-5-21 ����2:47:34
	 */
	public void onDownloadAttachedProgress(String msgId,String filePaht,int sizeProgrss,int currentProgress);

	/**
	 * ���ز�ѯ����״̬
	 * @param list
	 * @author: xiaozhenhua
	 * @data:2014-8-4 ����5:54:49
	 */
	public void onUserState(ArrayList<UcsStatus> list);
}
