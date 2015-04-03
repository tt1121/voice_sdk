package com.yzx.listenerInterface;

import org.json.JSONObject;

/**
 * 登录回调接口
 * @author xiaozhenhua
 *
 */
public interface LoginListener {

	/**
	 * 
	 * @param result：登录状态：  0:表示成功        其它：长度为6位参考云之讯android错误吗     3位参考http通用错误吗
	 * @param error：错误消息
	 * @param resultMap:如果result为0,该map为成功的数据键值对,result为非0时该map为空 
	 * @author: xiaozhenhua
	 * @data:2014-4-10 上午10:55:45
	 */
	public void onLoginStateResponse(JSONObject resultJson,Exception e);
	
	/**
	 * 获取CS地址是否成功
	 * @param result:200 表是正常，其它表示不正常
	 * @author: xiaozhenhua
	 * @data:2014-4-15 下午2:29:32
	 */
	public void onCSAddressResponse(JSONObject csAddressJson,Exception e);
	
	
	/**
	 * 登录成功
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午9:46:10
	 */
	public void onLoginSuccess();
}
