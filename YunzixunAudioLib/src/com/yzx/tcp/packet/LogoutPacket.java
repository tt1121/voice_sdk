package com.yzx.tcp.packet;

/**
 * TCPÍË³ö°ü
 * @author xiaozhenhua
 *
 */
public class LogoutPacket extends DataPacket{
	protected LogoutPacket() {
		this.mHeadDataPacket.setType(PacketDfineAction.SOCKET_INTERNAL_TYPE);
		this.mHeadDataPacket.setOp(PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP);
	}

	@Override
	public String toJSON() {
		return "";
	}
}
