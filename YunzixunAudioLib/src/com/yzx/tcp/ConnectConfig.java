package com.yzx.tcp;

/**
 * 连接配置类
 * 
 * @author xiaozhenhua
 * 
 */
public class ConnectConfig {

	private String accountSid;
	private String accountToken;
	private String clientId;
	private String clientPwd;

	private String host;
	private String port;

	public String getHost() {
		return host != null && host.length() > 0 ? host : "";
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port != null && port.length() > 0 ? port : "";
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAccountSid() {
		return accountSid != null && accountSid.length() > 0 ? accountSid : "";
	}

	/**
	 * 开发者账号
	 * 
	 * @param accountSid
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午3:45:02
	 */
	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	public String getAccountToken() {
		return accountToken != null && accountToken.length() > 0 ? accountToken
				: "";
	}

	/**
	 * 开发者密码
	 * 
	 * @param accountToken
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午3:45:29
	 */
	public void setAccountToken(String accountToken) {
		this.accountToken = accountToken;
	}

	public String getClientId() {
		return clientId != null && clientId.length() > 0 ? clientId : "";
	}

	/**
	 * 用户账号
	 * 
	 * @param clientId
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午3:45:51
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientPwd() {
		return clientPwd != null && clientPwd.length() > 0 ? clientPwd : "";
	}

	/**
	 * 用户密码
	 * 
	 * @param clientPwd
	 * @author: xiaozhenhua
	 * @data:2014-4-23 下午3:46:09
	 */
	public void setClientPwd(String clientPwd) {
		this.clientPwd = clientPwd;
	}

}
