package com.yzx.http.net;


public class InterfaceUrl {
	
	public static final String URL = "url";

	//cps.ucpaas.com:9997
	//���Ի�����ַ
	//public static String GET_PARAMETER = "http://113.31.89.144:9997/v2/get_audiodevice?";//��ȡ�������
	//public static String GET_PARAMETER_LIST = "http://113.31.89.144:9997/v2/get_adlist?";//��ȡ���������б�
	//public static String POST_PARAMETER_ADEXCEPTION = "http://113.31.89.144:9997/v2/post_adexception?";//�쳣��������ϱ�
	//public static String POST_PARAMETER_ADSUCCESS = "http://113.31.89.144:9997/v2/post_adsuccess?";//�ɹ���������ϱ�
	
	//���ϻ�����ַ
	public static String GET_PARAMETER = "http://cps.ucpaas.com:9997/v2/get_audiodevice?";//��ȡ�������
	public static String GET_PARAMETER_LIST = "http://cps.ucpaas.com:9997/v2/get_adlist?";//��ȡ���������б�
	public static String POST_PARAMETER_ADEXCEPTION = "http://cps.ucpaas.com:9997/v2/post_adexception?";//�쳣��������ϱ�
	public static String POST_PARAMETER_ADSUCCESS = "http://cps.ucpaas.com:9997/v2/post_adsuccess?";//�ɹ���������ϱ�
	
	/** 
	 * ����CPS Url�����Ի���
	 * 
	 */  
	public static void initUrlToTest(boolean istest){
		if(istest){
			GET_PARAMETER = "http://113.31.89.144:9997/v2/get_audiodevice?";//��ȡ�������
			GET_PARAMETER_LIST = "http://113.31.89.144:9997/v2/get_adlist?";//��ȡ���������б�
			POST_PARAMETER_ADEXCEPTION = "http://113.31.89.144:9997/v2/post_adexception?";//�쳣��������ϱ�
			POST_PARAMETER_ADSUCCESS = "http://113.31.89.144:9997/v2/post_adsuccess?";//�ɹ���������ϱ�
		}else {
			GET_PARAMETER = "http://cps.ucpaas.com:9997/v2/get_audiodevice?";//��ȡ�������
			GET_PARAMETER_LIST = "http://cps.ucpaas.com:9997/v2/get_adlist?";//��ȡ���������б�
			POST_PARAMETER_ADEXCEPTION = "http://cps.ucpaas.com:9997/v2/post_adexception?";//�쳣��������ϱ�
			POST_PARAMETER_ADSUCCESS = "http://cps.ucpaas.com:9997/v2/post_adsuccess?";//�ɹ���������ϱ�
		}
	}
}
