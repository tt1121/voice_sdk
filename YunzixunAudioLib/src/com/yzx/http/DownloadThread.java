package com.yzx.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.yzx.listenerInterface.MessageListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.packet.MessageTools;
import com.yzx.tools.CustomLog;

public class DownloadThread extends Thread {

	private String down_url;
	private String down_filepath;
	private String down_msgid;
	private MessageListener down_listener;
	private boolean isStop = false;
	
	@Override
	public void run() {
		int filelength = 0;
		int length = 0;
		boolean isException = false;
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			// 构造URL
			URL url = new URL(down_url);
			CustomLog.v("DOWN_LOAD_URL:"+url);
			// 打开连接
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			length = connection.getContentLength() / 1024;
			// 输入流
			is = connection.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len = 0;
			// 输出的文件流
			os = new FileOutputStream(down_filepath);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
				filelength += len;
				int size = (filelength/1024);
				if(down_listener != null){
					down_listener.onDownloadAttachedProgress(down_url, down_filepath,length, size > 0 ? size : 1);
				}
				for(MessageListener list:MessageTools.getMessageListenerList()){
					list.onDownloadAttachedProgress(down_msgid, down_filepath ,length, size > 0 ? size : 1);
				}
				if(isStop){
					break;
				}
			}
		} catch (Exception e) {
			filelength = 0;
			e.printStackTrace();
			isException = true;
			if(down_listener != null){
				down_listener.onDownloadAttachedProgress(down_msgid, down_filepath,0, 0);
				down_listener.onReceiveUcsMessage(new UcsReason(300230).setMsg(e.toString()),null);
			}
			for(MessageListener list:MessageTools.getMessageListenerList()){
				list.onDownloadAttachedProgress(down_msgid, down_filepath ,0, 0);
				//list.onReceiveUcsMessage();
			}
			MessageTools.notifyMessages(new UcsReason(300230).setMsg(e.toString()),null);
		} finally {
			// 完毕，关闭所有链接
			try {
				if (os != null) {
					os.close();
					os = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
			int size = (filelength/1024);
			if(!isStop && !isException){
				if(down_listener != null){
					down_listener.onDownloadAttachedProgress(down_msgid, down_filepath ,length, size > 0 ? size : 1);
				}
				for(MessageListener list:MessageTools.getMessageListenerList()){
					list.onDownloadAttachedProgress(down_msgid, down_filepath ,length, size > 0 ? size : 1);
				}
			}
		}
	}

	public DownloadThread(String fileUrl, String filePaht,String msgId,MessageListener fileListener){
		isStop = false;
		down_url = fileUrl;
		down_filepath = filePaht;
		down_msgid = msgId;
		down_listener = fileListener;
	}
	
	public void stopDownload(){
		isStop = true;
	}
	
	public void startDownload(){
		this.start();
	}
	
}
