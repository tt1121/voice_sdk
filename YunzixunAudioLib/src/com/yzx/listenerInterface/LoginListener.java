package com.yzx.listenerInterface;

import org.json.JSONObject;

/**
 * ��¼�ص��ӿ�
 * @author xiaozhenhua
 *
 */
public interface LoginListener {

	/**
	 * 
	 * @param result����¼״̬��  0:��ʾ�ɹ�        ����������Ϊ6λ�ο���֮Ѷandroid������     3λ�ο�httpͨ�ô�����
	 * @param error��������Ϣ
	 * @param resultMap:���resultΪ0,��mapΪ�ɹ������ݼ�ֵ��,resultΪ��0ʱ��mapΪ�� 
	 * @author: xiaozhenhua
	 * @data:2014-4-10 ����10:55:45
	 */
	public void onLoginStateResponse(JSONObject resultJson,Exception e);
	
	/**
	 * ��ȡCS��ַ�Ƿ�ɹ�
	 * @param result:200 ����������������ʾ������
	 * @author: xiaozhenhua
	 * @data:2014-4-15 ����2:29:32
	 */
	public void onCSAddressResponse(JSONObject csAddressJson,Exception e);
	
	
	/**
	 * ��¼�ɹ�
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 ����9:46:10
	 */
	public void onLoginSuccess();
}
