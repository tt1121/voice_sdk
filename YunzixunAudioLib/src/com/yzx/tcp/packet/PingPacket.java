package com.yzx.tcp.packet;

/**
 * ������(�ް���)
 * @author xiaozhenhua
 *
 */
public class PingPacket extends DataPacket {
	public PingPacket() {
		this.mHeadDataPacket.setType(PacketDfineAction.SOCKET_INTERNAL_TYPE);
		this.mHeadDataPacket.setOp(PacketDfineAction.SOCKET_PING_OP);
	}
	
	@Override
	public String toJSON() {
		return null;
	}

}
