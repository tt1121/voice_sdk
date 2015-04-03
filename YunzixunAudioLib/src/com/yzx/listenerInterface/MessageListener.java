package com.yzx.listenerInterface;

import java.util.ArrayList;
import java.util.HashMap;

import com.yzx.tcp.packet.UcsMessage;
import com.yzx.tcp.packet.UcsStatus;

/**
 * IM消息回调接口
 * 
 * @author xiaozhenhua
 * 
 */
public interface MessageListener {

	/**
	 * 收到消息
	 * 
	 * @param message
	 * @author: xiaozhenhua
	 * @data:2014-5-13 下午5:27:01
	 */
	public void onReceiveUcsMessage(UcsReason reason, UcsMessage message);

	/**
	 * 发送IM消息成功或失败的回调
	 * 
	 * @param reason
	 *            :状态码
	 * @param message
	 *            :消息包
	 * @author: xiaozhenhua
	 * @data:2014-5-13 下午5:27:18
	 */
	public void onSendUcsMessage(UcsReason reason, UcsMessage message);
	
	/**
	 * 文件进度发送监听接口
	 * @param progress
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午11:39:02
	 */
	public void onSendFileProgress(int progress);
	
	
	/**
	 * 下载文件进度监听接口(下载文件)
	 * @param msgId
	 * @param sizeProgrss:总件总大小
	 * @param currentProgress：当前下载大小
	 * @author: xiaozhenhua
	 * @data:2014-5-21 下午2:47:34
	 */
	public void onDownloadAttachedProgress(String msgId,String filePaht,int sizeProgrss,int currentProgress);

	/**
	 * 返回查询到的状态
	 * @param list
	 * @author: xiaozhenhua
	 * @data:2014-8-4 下午5:54:49
	 */
	public void onUserState(ArrayList<UcsStatus> list);
}
