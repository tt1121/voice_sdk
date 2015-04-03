package com.yzx.tools;

public class Util {

	
	public static final String SDK_VERSION = "voice_1.2.14";
	/**
	 * 获取URL内指定字段值
	 * @param url,optionName
	 * @return value
	 * @author: xiaozhenhua
	 * @data:2014-4-10 上午11:15:30
	 */
	public static String getUrlOptionValue(String url,String optionName) {
		
		String ovalue = "";

		if(url == null || optionName == null)
			return "";

		int bf = url.indexOf(optionName);
		if (bf < 0)
			return "";

		String tmpStr = url.substring(bf);
		bf = tmpStr.indexOf('&');
		if (bf < 0) {
			ovalue = tmpStr.substring(optionName.length());
		}else{
			ovalue = tmpStr.substring(optionName.length(),bf);
		}
		
		return ovalue;
	}
	
	
	/**
	 * 生成SecurityFlag
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-9 下午4:10:31
	 */
	public static String setSecurityFlag() {
		char[] map = { 'd', 'e', 'y', 'b', 'i', 'p', 'v', 'k', 'z', 'o' };
		String addLen = "";
		int x = 100 + (int) (Math.random() * 100);
		int y = 1 + (int) (Math.random() * 9999);
		int z = y * 256 + x;
		String strc = String.valueOf(z);
		for (int j = 0; j < strc.length(); j++) {
			char index = strc.charAt(j);
			int conent = Integer.parseInt("" + index);
			addLen += map[conent];
		}
		return addLen;
	}

}
