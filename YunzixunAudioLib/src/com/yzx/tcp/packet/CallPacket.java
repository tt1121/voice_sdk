package com.yzx.tcp.packet;

/**
 * �绰ҵ��
 * @author xiaozhenhua
 *
 */
public class CallPacket extends DataPacket {


	private String json;

	public void setJson(String j) {
		this.json = j;
	}

	@Override
	public String toJSON() {
		return json;
	}

}
