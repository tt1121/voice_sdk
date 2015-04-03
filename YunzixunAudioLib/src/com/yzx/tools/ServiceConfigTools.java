package com.yzx.tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gl.softphone.RtppSrvConfig;
import com.yzx.http.HttpTools;
import com.yzx.preference.UserData;

public class ServiceConfigTools {

	
	/**
	 * 获取RTPP List 列表
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void getRtppAndStunList(){
		String url = UserData.getHost()+"/static/address.txt";
		try {
			//CustomLog.v("RTPP_REPUEST_URL:"+url);
			JSONObject json = HttpTools.doGetMethod(url, UserData.getAc());
			if(json != null){
				//CustomLog.v("RTPP_RESPONSE:"+json);
				if(json.has("rtpp")){
					UserData.saveRtppAddressList(json.getJSONArray("rtpp").toString());
				}
				if(json.has("stun")){
					UserData.saveStunAddressList(json.getJSONArray("stun").toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			CustomLog.v("RTPP_RESPONSE:"+e.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			CustomLog.v("RTPP_RESPONSE:"+e.toString());
		}
	}


	/**
	 * 计算RTPP地址的丢包率与延迟时间
	 * @param list:需要ping的IP地址列表
	 * @param totalTime：每一个IP地址ping的时间
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午11:39:48
	 */
	public static void pingRtpp(RtppSrvConfig astRTPSrvCfg,String hostList,final long totalTime){
		final JSONArray rtppArray = new JSONArray();
		
		HashMap<Integer,JSONObject> jsonMap = new HashMap<Integer,JSONObject>();
		ArrayList<Integer> arrlyList = new ArrayList<Integer>();
		
		if(hostList.length() > 0){
			try {
				JSONArray array = new JSONArray(hostList);
				for (int i = 0; i < array.length(); i++) {
					String ipAndPort = array.getString(i);
					if(ipAndPort.indexOf(":") > 0){
						ArrayList<Boolean> lostSize = new ArrayList<Boolean>();
						ipAndPort = ipAndPort.replace("http://", "");
						String ip = ipAndPort.split(":")[0];
						String port = ipAndPort.split(":")[1];
						int timeOut = 1000;
						long startTime = System.currentTimeMillis();	// 起始时间
						double averageTime = 1999.99;					// 平均时间
						int lost = 0;			// 丢包率
						int packageSize = 5;	// 发包个数
						for(int j = 0 ; j < packageSize ; j ++){
							String updSend = "";
							String updReveiver = "";
							DatagramSocket socket = new DatagramSocket();
							try {
								String ping = "pong "+j;
								byte[] udp = ping.getBytes();
								
								socket.setSoTimeout(timeOut);
								InetAddress address = InetAddress.getByName(ip);
								DatagramPacket sendPacket = new DatagramPacket(udp, udp.length, address,Integer.parseInt(port));
								DatagramPacket receivePacket = new DatagramPacket(new byte[udp.length], udp.length);

								socket.send(sendPacket);
								socket.receive(receivePacket);
								String receiverUdp = new String(receivePacket.getData()).replace(" ", " ");
								//CustomLog.v("UDP_SEND:"+ping);
								//CustomLog.v("UDP_RECEIVER:"+receiverUdp);
								//CustomLog.v("UDP_CASE:"+receiverUdp.replace(" ", "").equals(ping.replace(" ", "")));
								
								updSend = ping;
								updReveiver = receiverUdp;
							} catch (SocketException e) {
								e.printStackTrace();
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								socket.close();
								if(updReveiver.length() > 0 && 
										updSend.length() > 0 &&
										updReveiver.replace(" ", "").equals(updSend.replace(" ", ""))){
									lostSize.add(true);
								}
							}
						}
						long endTime = System.currentTimeMillis();	// 结速时间
						lost = Integer.parseInt(new DecimalFormat("00").format(((double)(packageSize-lostSize.size())/packageSize)*100)+"");
						if(lost >= 100){
							averageTime = 1000;
						}else{
							averageTime = (double)(endTime - startTime) / packageSize;
						}
						//CustomLog.v("1-AVERAGE_TIME:" + averageTime);
						//CustomLog.v(i+"-1-PING_COUNT:" + packageSize);
						//CustomLog.v(i+"-1-LOST_COUNT:" + (packageSize-lostSize.size()));
						//CustomLog.v(i+"-1-ADDRESS:" + ip);
						//CustomLog.v(i+"-1-丢包率:" + lost + "%");
						//CustomLog.v(i+"-1-延迟:" + (int) averageTime);
						JSONObject rtppCfg = new JSONObject();
						try {
							rtppCfg.put("delay", (int) averageTime);
							rtppCfg.put("lost", lost);
							rtppCfg.put("ip", ip);
							//rtppArray.put(rtppCfg);
							jsonMap.put((int) averageTime, rtppCfg);
							arrlyList.add((int) averageTime);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						lostSize.clear();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < arrlyList.size(); ++i) {
			for (int j = 0; j < arrlyList.size() - i - 1; ++j) {
				if(arrlyList.get(j) > arrlyList.get(j + 1)){
					int v = arrlyList.get(j +1);
					arrlyList.set(j+1, arrlyList.get(j));
					arrlyList.set(j, v);
				}
			}
		}
		for(int i = 0 ; i < arrlyList.size() && i < 5 ; i ++){
			rtppArray.put(jsonMap.get(arrlyList.get(i)));
		}
		astRTPSrvCfg.rtp_list_length = rtppArray.toString().length();
		astRTPSrvCfg.rtppcfg = rtppArray.toString();
	}
}


