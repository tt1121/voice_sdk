package com.yzx.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.packet.DataPacket;
import com.yzx.tcp.packet.MessageTools;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tools.CustomLog;
import com.yzx.tools.FileTools;
import com.yzx.tools.RC4Tools;

/**
 * 消息写入类
 * @author xiaozhenhua
 *
 */
public class PacketWriter {
	public static int pingCount = 0 ;
	
	private Queue<DataPacket> queue = new LinkedList<DataPacket>();
	private Thread writerThread;
	protected boolean done = false; // 客户端是否以经断线
	private OutputStream writer;
	//private TcpConnection connection;
	//private boolean isNotifyDisconnect = true;		//是否通知断线,默认为通知，更换账号重连的情况下不通知
	//private Context mContext = null;
	
	public PacketWriter(TcpConnection c) {
		//this.mContext = mContext;
		//connection = c;
		done = false;
		writer = c.writer;
		writerThread = new Thread() {
			@Override
			public void run() {
				writePackets(this);
			}
		};
		// 标识为守护线程
		writerThread.setDaemon(true);
	}

	/**
	 * 
	 * 发送数据包
	 * 
	 * @param thisThread
	 * @author: xiaozhenhua
	 * @version: 2012-6-21 下午02:48:40
	 */
	private void writePackets(Thread thisThread) {
		//isNotifyDisconnect = true;
		//CustomLog.v("--------------START WRITE THREAD-----------------");
		while (!done && thisThread == writerThread) {
			DataPacket packet = nextPacket();
			if (packet != null) {
				try {
					if (packet.mHeadDataPacket.getType() == PacketDfineAction.SOCKET_INTERNAL_TYPE
							&&  packet.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_PING_OP//心跳包
					) {
						// 无包体,报文头长度为0,报文体长度为0
						byte[] head = { 0, 0, 0, 0 };
						writer.write(head);
						if(packet.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_PING_OP){
							CustomLog.v( "REQUEST PING  ... ");
						}else{
							CustomLog.v( "客户端与服务端内部消息 ... ");
						}
					} else {
						if (packet.mHeadDataPacket.getType() == PacketDfineAction.SOCKET_INTERNAL_TYPE
								&& packet.mHeadDataPacket.getOp() == PacketDfineAction.SOCKET_VERSION) {
							byte[] sendByte = new byte[4];
							byte[] verByteArray = new byte[4];
							int ver = 0x20;
							verByteArray[0] = (byte) ((ver >> 24) & 0xff);
							verByteArray[1] = (byte) ((ver >> 16) & 0xff);
							verByteArray[2] = (byte) ((ver >> 8) & 0xff);
							verByteArray[3] = (byte) (ver & 0xff);
							System.arraycopy(verByteArray, 0, sendByte, 0, verByteArray.length);
							writer.write(sendByte);
							//CustomLog.v( "SEND_HEAD:"+sendByte.length);
						} else {
							String bodyString = packet.toJSON();
							byte[] body = null; // 内容
							if (packet.mHeadDataPacket.getEnc() == (byte) 0x01) {
								body = RC4Tools.encry_RC4_byte(bodyString);
							} else {
								body = bodyString.getBytes();
							}
							if (body == null){
								return;
							}
							byte[] head = packet.mHeadDataPacket.toBsonByte();// 协议头
							int headLength = head.length;
							int bodyLegth = body.length;
							int totalLength = headLength + bodyLegth + 4;
							byte[] sendByte = null;
							sendByte = new byte[totalLength];

							// 报文头跟报体的长度组装成包头
							byte[] header = new byte[4];
							header[0] = (byte) ((headLength >> 8) & 0xff);
							header[1] = (byte) (headLength & 0xff);
							header[2] = (byte) ((bodyLegth >> 8) & 0xff);
							header[3] = (byte) (bodyLegth & 0xff);

							System.arraycopy(header, 0, sendByte, 0, header.length);
							System.arraycopy(head, 0, sendByte, 4, headLength);
							System.arraycopy(body, 0, sendByte, headLength + 4, bodyLegth);
							
							// IM 相关业务 加入发送队列
							MessageTools.sendMessageIsTimeout(packet);
							
							if (packet.mHeadDataPacket.getType() == PacketDfineAction.SOCKET_CALL) {
								//CustomLog.v(PacketDfineAction.TAG, "tcp write body:" + bodyString);
							}
							//CustomLog.v( "SEND_BODY:" + bodyString);
							writer.write(sendByte);
							//CustomLog.v(PacketDfineAction.TAG, "SEND_BYTE:" + sendByte);
						}
					}
					writer.flush();
				} catch (IOException e) {
					CustomLog.v("WRITER CONDUIT EXCEPTION:" + e.toString());
					done = true;
					//CustomLog.v( "EXCEPTION_PACKET_WRITER_NOTIFY_DISCONNECT:" + isNotifyDisconnect);
					//if(isNotifyDisconnect){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							cl.onConnectionFailed(new UcsReason().setReason(300503).setMsg(e.toString()));
						}
						FileTools.saveExLog("写管道错误","YZX_trace_");
					//}
					e.printStackTrace();
				}
			}
		}
		CustomLog.v( "PacketWriter done : " + done);
		queue.clear();
		/*if(done){
			try {
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			CustomLog.v( "DOWN_PACKET_WRITER_NOTIFY_DISCONNECT:" + isNotifyDisconnect);
			if(isNotifyDisconnect){
				for(ConnectionListener cl:TcpConnection.getConnectionListener()){
					cl.onConnectionFailed(new UcsReason().setReason(300504));
				}
			}
			CustomLog.v( "PacketWriter close");
		}*/
	}

	public void sendPacket(DataPacket packet) {
		if (packet != null) {
			if (!done) {
				queue.add(packet);
				synchronized (queue) {
					queue.notifyAll();
				}
			}else{
				MessageTools.sendMessageIsTimeout(packet);
			}
		}
	}

	/**
	 * 
	 * 获取下一个要发送的数据包
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @version: 2012-6-27 下午05:23:10
	 */
	public DataPacket nextPacket() {
		DataPacket packet = null;
		try{
			while (!done && (packet = queue.poll()) == null) {
				synchronized (queue) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return packet;
	}

	public void startup() {
		writerThread.start();
	}

	public void shutdown() {
		done = true;
		//isNotifyDisconnect = isNotify;
		synchronized (queue) {
			queue.notifyAll();
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			CustomLog.v( "PACKET_WRITE : " + done);
		}
	}
}
