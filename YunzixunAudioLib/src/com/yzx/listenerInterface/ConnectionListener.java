package com.yzx.listenerInterface;

/**
 * TCP���Ӽ�����
 * 
 * @author xiaozhenhua
 * 
 */
public interface ConnectionListener {

	/**
	 * TCP���ӳɹ�
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 ����4:01:07
	 */
	public void onConnectionSuccessful();

	/**
	 * TCP����ʧ��
	 * 
	 * @param e
	 * @author: xiaozhenhua
	 * @data:2014-4-15 ����4:01:41
	 */
	public void onConnectionFailed(UcsReason reason);

}
