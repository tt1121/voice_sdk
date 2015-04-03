package com.yzx.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * ���繤����
 * @author xiaozhenhua
 *
 */
public class NetWorkTools {

	/**
	 * ����������
	 */
	public static final int NETWORK_ON = 0;
	/**
	 * WIFI����
	 */
	public static final int NETWORK_WIFI = 1;
	/**
	 * 2G����(����:2.75G  2.5G 2G)
	 */
	public static final int NETWORK_EDGE = 2;
	/**
	 * 3G����(����:3G  3.5G  3.75G)
	 */
	public static final int NETWORK_3G = 3;
	
	/**
	 * ��ȡ��ǰ����������
	 * @param mActivityContext
	 * @author: xiaozhenhua
	 * @data:2014-4-9 ����3:15:07
	 */
	public static int getCurrentNetWorkType(Context mContext){
		int currentNetWorkType = NETWORK_ON;
		NetworkInfo activeNetInfo =  getNetworkInfo(mContext);
		int netSubtype = -1;
		if (activeNetInfo != null) {
			netSubtype = activeNetInfo.getSubtype();
		}
		if (activeNetInfo != null && activeNetInfo.isConnected()) {
			if ("WIFI".equalsIgnoreCase(activeNetInfo.getTypeName())) {
				currentNetWorkType = NETWORK_WIFI;
			} else if (activeNetInfo.getTypeName() != null 
					&& activeNetInfo.getTypeName().toLowerCase().contains("mobile")) {// 3g,˫���ֻ���ʱΪmobile2
				if (netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
						|| netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_A
						|| netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_B
						|| netSubtype == TelephonyManager.NETWORK_TYPE_EHRPD
						|| netSubtype == TelephonyManager.NETWORK_TYPE_HSDPA
						|| netSubtype == TelephonyManager.NETWORK_TYPE_HSUPA
						|| netSubtype == TelephonyManager.NETWORK_TYPE_HSPA
						|| netSubtype == TelephonyManager.NETWORK_TYPE_LTE
						// 4.0ϵͳ H+����Ϊ15 TelephonyManager.NETWORK_TYPE_HSPAP
						|| netSubtype == 15) {
					currentNetWorkType = NETWORK_3G;
				}  else {
					currentNetWorkType = NETWORK_EDGE;
				} 
			}
		}
		return currentNetWorkType;
	}
	
	private static NetworkInfo getNetworkInfo(Context mContext){
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo();
	}
	
	public static boolean isNetWorkConnect(Context mContext){
		NetworkInfo activeNetInfo =  getNetworkInfo(mContext);
		return (activeNetInfo != null && activeNetInfo.isConnected());
	}
	
	
	/**
	 * ���˱�����IP��ַ
	 * @param useIPv4
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-10 ����9:57:19
	 */
	public static String getIPAddress(boolean useIPv4) {
	       try {
	           List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
	           for (NetworkInterface intf : interfaces) {
	               List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
	               for (InetAddress addr : addrs) {
	                   if (!addr.isLoopbackAddress()) {
	                       String sAddr = addr.getHostAddress().toUpperCase();
	                       boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
	                       if (useIPv4) {
	                           if (isIPv4) 
	                               return sAddr;
	                       } else {
	                           if (!isIPv4) {
	                               int delim = sAddr.indexOf('%');
	                               return delim<0 ? sAddr : sAddr.substring(0, delim);
	                           }
	                       }
	                   }
	               }
	           }
	       } catch (Exception ex) { 
	    	   ex.printStackTrace();
	       }
	       return "";
	   }
	
	/**
	 * ��ǰ�Ƿ�wifi
	 * @param mcontext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 ����5:12:17
	 */
	public static boolean isConnectWifi(Context mcontext) {
		ConnectivityManager connMng = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInf = connMng.getActiveNetworkInfo();
		return netInf != null && "WIFI".equalsIgnoreCase(netInf.getTypeName());
	}
}
