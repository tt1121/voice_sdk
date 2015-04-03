package com.yzx.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ���븨����
 * @author xiaozhenhua
 *
 */
public class PhoneNumberTools {
	
	
	/**
	 * ��֤�Ƿ�绰����
	 * @param phone
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-8-6 ����5:43:42
	 */
	public static boolean checkTelphoneNumber(String phone) {
		Matcher m = Pattern.compile("^0(([1-9]\\d)|([3-9]\\d{2}))\\d{8}$").matcher(phone.replace(" ", ""));
		return m.find() || phone.startsWith("400");
	}
	
	/**
	 * ��֤�Ƿ��ֻ�����
	 * @param phone
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-8-6 ����5:43:38
	 */
	public static boolean checkMobilePhoneNumber(String phone){
		return phone == null ? false:Pattern.compile("^1[3,4,5,7,8]\\d{9}$").matcher(phone).matches();
	}

	/**
	 * ��֤�Ƿ�����
	 * @param num
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-26 ����11:27:00
	 */
	public static boolean isNumber(String num){
		Pattern p = Pattern.compile("\\d+");
		return p.matcher(num).matches();
	}
	
	/**
	 * ��֤�Ƿ�����
	 * @param num1 num2
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-26 ����11:27:00
	 */
	public static boolean isNumber(String uid, String phone){
		Pattern p = Pattern.compile("\\d+");
		if(uid==null && phone==null){
			return false;
		}
		if((uid != null && p.matcher(uid).matches())
				|| (phone != null && p.matcher(phone).matches())){
			return true;
		}
		return false;
	}
}
