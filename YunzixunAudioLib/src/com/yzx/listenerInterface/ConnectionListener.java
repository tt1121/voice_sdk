package com.yzx.listenerInterface;

/**
 * TCP连接监听器
 * 
 * @author xiaozhenhua
 * 
 */
public interface ConnectionListener {

	/**
	 * TCP连接成功
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 下午4:01:07
	 */
	public void onConnectionSuccessful();

	/**
	 * TCP连接失败
	 * 
	 * @param e
	 * @author: xiaozhenhua
	 * @data:2014-4-15 下午4:01:41
	 */
	public void onConnectionFailed(UcsReason reason);

}
