package com.yzx.tcp.packet;

/**
 * �汾�Ű�(�ް���)
 * @author xiaozhenhua
 *
 */
public class VersionPacket extends DataPacket {
	public VersionPacket() {
		this.mHeadDataPacket.setType(PacketDfineAction.SOCKET_INTERNAL_TYPE);
		this.mHeadDataPacket.setOp(PacketDfineAction.SOCKET_VERSION);
	}
	
	@Override
	public String toJSON() {
		return null;
	}

}
