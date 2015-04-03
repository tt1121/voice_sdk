package com.yzx.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.bson.BSON;

import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.packet.PacketDfineAction;
import com.yzx.tcp.packet.MessageAllocation;
import com.yzx.tools.CustomLog;
import com.yzx.tools.FileTools;
import com.yzx.tools.RC4Tools;


/**
 * 
 * 消息读取类
 * 
 * @author: xiaozhenhua
 * @version: 2012-6-19 下午02:28:02
 */
public class PacketReader {
	private DataInputStream reader;
	//private Context mContext = null;
	private Thread readerThread;
	private TcpConnection connection;
	protected boolean done = false; // 客户端是否以经断线
	//private boolean isNotifyDisconnect = true;		//是否通知断线,默认为通知，更换账号重连的情况下不通知

	public PacketReader(TcpConnection c) {
		connection = c;
		reader = c.reader;
		//this.mContext = mContext;
		readerThread = new Thread(mRunnable);
		// 标识为守护线程
		readerThread.setDaemon(true);
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			//isNotifyDisconnect = true;
			//CustomLog.v("--------------START READER THREAD-----------------");
			while (!done) {
				try {
					byte[] readByteArray = new byte[2];
					int length = reader.read(readByteArray);
					//CustomLog.v(PacketDfineAction.TAG, "read length = " + length);
                    if (length == -1) break;
                    MessageAllocation  cbPack = new MessageAllocation();
                    cbPack.headLength = (short)((short) (readByteArray[0] << 8) + (short) (readByteArray[1] & 0xff));
                    byte[]  readByteArray1 = new byte[2];
                    length = reader.read(readByteArray1);
                    if (length == -1) break;
					cbPack.length  = (short) ((short) (readByteArray1[0] << 8) + (short) (readByteArray1[1] & 0xff));
					//CustomLog.v( "cbPack.headLength = " + cbPack.headLength+" ,cbPack.length="+cbPack.length);
					byte[] head = null;
					if (cbPack.headLength > 0) {//读报文头
						head = new byte[cbPack.headLength];
						byte[] buf = new byte[1024];
						int resdLength = 0;
						int rl = 0;
						while(resdLength < cbPack.headLength) {
							int lastLength = cbPack.headLength - resdLength;
							if (lastLength > 1024) {
								rl = reader.read(buf);
							} else {
								rl = reader.read(buf, 0, lastLength);
							}
							//CustomLog.v(PacketDfineAction.TAG, "read reader.read() return length = " + rl);
							if(rl>=0){
							  System.arraycopy(buf, 0, head, resdLength, rl);
							  resdLength += rl;
							}
							rl = 0;
						}
						try{
							cbPack.setHead(BSON.decode(head));
							//CustomLog.v(PacketDfineAction.TAG,"cbPack.headLength:"+BSON.decode(head).toString().length());
						} catch (Exception e) {
							e.printStackTrace();
							CustomLog.v("cbPack.headLength = login  ERROR:"+e.toString());
							reader.skip(reader.available());
							continue;
						}
					}
					byte[] body = null;
					if(cbPack.length > 0){//读报文体
						body = new byte[cbPack.length];
						byte[] buf = new byte[1024];
						int resdLength = 0;
						int rl = 0;
						while(resdLength < cbPack.length) {
							int lastLength = cbPack.length - resdLength;
							if (lastLength > 1024) {
								rl = reader.read(buf);
							} else {
								rl = reader.read(buf, 0, lastLength);
							}
							if(rl>=0){
							   System.arraycopy(buf, 0, body, resdLength, rl);
							   resdLength += rl;
							}
							rl = 0;
						}
						if(cbPack.mHeadDataPacket.getCpstp() == 0x01){
							body = unGZip(body);
						}
						try {
							if(cbPack.mHeadDataPacket.enc == (byte)0x01){		//是否加密
								//CustomLog.v( "RC4 解析 ... ");
								cbPack.jsonBody = RC4Tools.decry_RC4(body);
							} else {
								cbPack.jsonBody = new String (body);
								//CustomLog.v( "不解密  ... ");
							}
						} catch (Exception e) {
							e.printStackTrace();
							CustomLog.v( "PacketReader RC4 解析:" + e.toString());
						}
					}
					byte[] packet = null;
					if (cbPack.mHeadDataPacket.getType() == PacketDfineAction.SOCKET_CALL) {
						packet = new byte[readByteArray.length + readByteArray1.length + cbPack.headLength + cbPack.length];
						System.arraycopy(readByteArray, 0, packet, 0, readByteArray.length);
						System.arraycopy(readByteArray1, 0, packet, readByteArray.length, readByteArray1.length);
						System.arraycopy(head, 0, packet, readByteArray.length + readByteArray1.length, cbPack.headLength);
						if (body != null) {
							System.arraycopy(body, 0, packet, readByteArray.length + readByteArray1.length + cbPack.headLength, cbPack.length);
						}
					}
					//CustomLog.v("READ_HEAD:" + cbPack.headJson);
					//CustomLog.v("READ_BODY:" + cbPack.jsonBody);
					if (!done) {
						if(cbPack.headLength == 0 && cbPack.length == 0){//心跳包返回
							CustomLog.v( "RESPONSE PING  ... ");
							AlarmTools.stopBackTcpPing();
						}else{
							//调用公共接口
							cbPack.messageAllocationAction(packet,connection);
						}
					}
				} catch (Exception e) {
					CustomLog.v("READER CONDUIT EXCEPTION:" + e.toString());
					done = true;
					//CustomLog.v( "EXCEPTION_PACKET_READER_NOTIFY_DISCONNECT:" + isNotifyDisconnect);
					//if(isNotifyDisconnect){
						for(ConnectionListener cl:TcpConnection.getConnectionListener()){
							cl.onConnectionFailed(new UcsReason().setReason(300501).setMsg(e.toString()));
						}
						FileTools.saveExLog("读管道错误","YZX_trace_");
					//}
					e.printStackTrace();
				}
			}
			CustomLog.v( "PacketReader done : " + done);
			/*if (done) {
				try {
					if(reader != null){
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				CustomLog.v( "DOWN_PACKET_READER_NOTIFY_DISCONNECT:" + isNotifyDisconnect);
				if(isNotifyDisconnect){
					for(ConnectionListener cl:TcpConnection.getConnectionListener()){
						cl.onConnectionFailed(new UcsReason().setReason(300502));
					}
				}
				CustomLog.v( "PacketReader close");
			}*/
		}
	};

	public void startup() {
		readerThread.start();
	}

	public void shutdown() {
		//isNotifyDisconnect = isNotify;
		done = true;
		try {
			reader.close();
			reader = null;
			//readerThread.interrupt();
			//readerThread = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			CustomLog.v( "PACKET_READER : " + done);
		}
	}
	
	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
}
