package com.yzx.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.packet.DataPacket;
import com.yzx.tools.CustomLog;

public class TcpConnection {
	
	private Socket socket;
	protected PacketReader packetReader;
	protected PacketWriter packetWriter;
	protected DataInputStream reader;
	protected OutputStream writer;
	protected int port ;
	protected String host;
	protected static ArrayList<ConnectionListener> connectionListenerList = new ArrayList<ConnectionListener>();//联接状态监听器
	
	public static void addConnectionListener(ConnectionListener cl){
		connectionListenerList.add(cl);
	}
	public static void removeConnectionListenerList(ConnectionListener cl){
		connectionListenerList.remove(cl);
	}
	public static void removeAllConnectionListenerList(){
		connectionListenerList.clear();
	}
	public static ArrayList<ConnectionListener> getConnectionListener(){
		return connectionListenerList;
	}
	
	protected static ConnectionListener CofnigListenerList;
	public static void addConfigListener(ConnectionListener cl){
		CofnigListenerList = cl;
	}
	public static ConnectionListener getConfigListner(){
		return CofnigListenerList;
	}
	
	private static TcpConnection tcpConnectionExample;
	public static TcpConnection getConnectionExample(){
		if(tcpConnectionExample == null){
			tcpConnectionExample = new TcpConnection();
		}
		return tcpConnectionExample;
	}
	
	/**
	 * 建立SOCKET连接
	 * @param host:SOCKET连接地址
	 * @param port：SOCKET端口
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:12:22
	 */
	public synchronized void connection(String host, int port) {
		try {
			//CustomLog.v("socket host:"+host+"     port:"+port);
			this.socket = new Socket(host, port);
			initReaderAndWriter();
			packetWriter = new PacketWriter(TcpConnection.this);
			packetReader = new PacketReader(TcpConnection.this);
			this.port = port;
			this.host = host;
		} catch (UnknownHostException e) {
			shutdown();
			for(ConnectionListener cl:connectionListenerList){
				cl.onConnectionFailed(new UcsReason().setReason(300506).setMsg(e.toString()));
			}
			e.printStackTrace();
			CustomLog.v( "TcpConnection UnknownHostException:" + e.toString());
		} catch (IOException e) {
			shutdown();
			for(ConnectionListener cl:connectionListenerList){
				cl.onConnectionFailed(new UcsReason().setReason(300507).setMsg(e.toString()));
			}
			e.printStackTrace();
			CustomLog.v( "TcpConnection IOException:" + e.toString());
		} catch (Exception e) {
			shutdown();
			for(ConnectionListener cl:connectionListenerList){
				cl.onConnectionFailed(new UcsReason().setReason(300508).setMsg(e.toString()));
			}
			e.printStackTrace();
			CustomLog.v( "TcpConnection Exception:" + e.toString());
		} finally {
			if (isConnection()) {
				startup();
			}
		}
	}

	public int getPort() {
		return this.port;
	}

	public String getHost() {
		return (this.host != null && this.host.length() > 0) ? this.host : "";
	}
	
	public boolean isConnection() {
		return socket != null ? socket.isConnected() : false;
	}

	/**
	 * 初始化网络数据读写对像
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:11:33
	 */
	private void initReaderAndWriter() {
		try {
			reader = new DataInputStream(socket.getInputStream());
			writer = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送数据包
	 * @param packet
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:11:20
	 */
	public void sendPacket(DataPacket packet) {
		if (packetWriter != null) {
			packetWriter.sendPacket(packet);
		}
	}

	/**
	 * 启动网络套接字读写流
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:11:41
	 */
	private void startup() {
		if (packetReader != null) {
			packetReader.startup();
		}
		if (packetWriter != null) {
			packetWriter.startup();
		}
	}

	/**
	 * 网络连接关闭时，回收所有网络对像
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 上午11:12:08
	 */
	public void shutdown() {
		if (isConnection()) {
			PacketTools.tcpLogout();
			//CustomLog.v("TCP Close:"+isNotify);
		}
		if (packetReader != null) {
			packetReader.shutdown();
			packetReader = null;
		}
		if (packetWriter != null) {
			packetWriter.shutdown();
			packetWriter = null;
		}
		try {
			if (socket != null) {
				socket.close();
				CustomLog.v("SOCKET CLOSE ... ");
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			AlarmTools.stopAlarm();
			System.gc();
		}
	}
}
