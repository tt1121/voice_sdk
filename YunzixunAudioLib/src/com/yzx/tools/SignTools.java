package com.yzx.tools;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SignTools {

	public static enum Encryption{
		RC4,SHA1,MD5
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getSign(String url,SignTools.Encryption encry) {
		String encryption = "";
		String query = url.contains("?") ? url.substring(url.indexOf("?") + 1) : url;
		String []b = query.split("&");
		if (b == null)
			return "";
		ArrayList<String> array = new ArrayList<String>();
		for (int i = 0; i < b.length; i++) {
			array.add(b[i]);
		}
		Collections.sort(array, new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				return s1.compareTo(s2);
			}
		});
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < array.size(); i++) {
			String pointString = array.get(i);
			int point = pointString.indexOf("=");
			if (point >= 0) {
				pointString = pointString.substring(point + 1);
			}
			try {
				buf.append(URLDecoder.decode(pointString));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				buf.append(pointString);
			}
		}
		if(encry == SignTools.Encryption.RC4){
			//CustomLog.v("--RC4:"+buf.toString());
			encryption = RC4Tools.encry_RC4_string(buf.toString());
		}else if(encry == SignTools.Encryption.SHA1){
			//CustomLog.v("--SHA1:"+buf.toString());
			buf.append(SHA1Tools.ASE_KEY);
			encryption = SHA1Tools.getDigestOfString(buf.toString().getBytes());
		}
		else if(encry == SignTools.Encryption.MD5) {
			//CustomLog.v("--MD5:"+buf.toString());
			encryption = MD5Tools.getMD5Str(buf.toString());
		}
			
		return encryption;
	}

	public static String getSign2(String sign,SignTools.Encryption encry) {
		String encryption = "";

		if(sign == null)
			return "";
		
		if(encry == SignTools.Encryption.RC4){
			//CustomLog.v("--RC4:"+buf.toString());
			encryption = RC4Tools.encry_RC4_string(sign);
		}else if(encry == SignTools.Encryption.SHA1){
			//CustomLog.v("--SHA1:"+buf.toString());
			sign = sign + SHA1Tools.ASE_KEY;
			encryption = SHA1Tools.getDigestOfString(sign.getBytes());
		}
		else if(encry == SignTools.Encryption.MD5) {
			//CustomLog.v("--MD5:"+buf.toString());
			encryption = MD5Tools.getMD5Str(sign);
		}
			
		return encryption;
	}
	public static String getSn() {
		long time = System.currentTimeMillis();
		int randomSix = 1 + (int) (Math.random() * 6);
		return String.valueOf(time + randomSix);
	}

	public static String getSnFromUrl(String url) {
		long time = System.currentTimeMillis();
		int randomSix = 1 + (int) (Math.random() * 6);
		return String.valueOf(time + randomSix);
	}
}
