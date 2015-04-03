package com.yzx.http.net;


public class InterfaceUrl {
	
	public static final String URL = "url";

	//cps.ucpaas.com:9997
	//测试环境地址
	//public static String GET_PARAMETER = "http://113.31.89.144:9997/v2/get_audiodevice?";//获取适配参数
	//public static String GET_PARAMETER_LIST = "http://113.31.89.144:9997/v2/get_adlist?";//获取智能适配列表
	//public static String POST_PARAMETER_ADEXCEPTION = "http://113.31.89.144:9997/v2/post_adexception?";//异常适配参数上报
	//public static String POST_PARAMETER_ADSUCCESS = "http://113.31.89.144:9997/v2/post_adsuccess?";//成功适配参数上报
	
	//线上环境地址
	public static String GET_PARAMETER = "http://cps.ucpaas.com:9997/v2/get_audiodevice?";//获取适配参数
	public static String GET_PARAMETER_LIST = "http://cps.ucpaas.com:9997/v2/get_adlist?";//获取智能适配列表
	public static String POST_PARAMETER_ADEXCEPTION = "http://cps.ucpaas.com:9997/v2/post_adexception?";//异常适配参数上报
	public static String POST_PARAMETER_ADSUCCESS = "http://cps.ucpaas.com:9997/v2/post_adsuccess?";//成功适配参数上报
	
	/** 
	 * 配置CPS Url到测试环境
	 * 
	 */  
	public static void initUrlToTest(boolean istest){
		if(istest){
			GET_PARAMETER = "http://113.31.89.144:9997/v2/get_audiodevice?";//获取适配参数
			GET_PARAMETER_LIST = "http://113.31.89.144:9997/v2/get_adlist?";//获取智能适配列表
			POST_PARAMETER_ADEXCEPTION = "http://113.31.89.144:9997/v2/post_adexception?";//异常适配参数上报
			POST_PARAMETER_ADSUCCESS = "http://113.31.89.144:9997/v2/post_adsuccess?";//成功适配参数上报
		}else {
			GET_PARAMETER = "http://cps.ucpaas.com:9997/v2/get_audiodevice?";//获取适配参数
			GET_PARAMETER_LIST = "http://cps.ucpaas.com:9997/v2/get_adlist?";//获取智能适配列表
			POST_PARAMETER_ADEXCEPTION = "http://cps.ucpaas.com:9997/v2/post_adexception?";//异常适配参数上报
			POST_PARAMETER_ADSUCCESS = "http://cps.ucpaas.com:9997/v2/post_adsuccess?";//成功适配参数上报
		}
	}
}
