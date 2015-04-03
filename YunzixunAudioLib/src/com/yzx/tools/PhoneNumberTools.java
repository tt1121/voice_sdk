package com.yzx.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 号码辅助类
 * @author xiaozhenhua
 *
 */
public class PhoneNumberTools {
	
	
	/**
	 * 验证是否电话号码
	 * @param phone
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-8-6 下午5:43:42
	 */
	public static boolean checkTelphoneNumber(String phone) {
		Matcher m = Pattern.compile("^0(([1-9]\\d)|([3-9]\\d{2}))\\d{8}$").matcher(phone.replace(" ", ""));
		return m.find() || phone.startsWith("400");
	}
	
	/**
	 * 验证是否手机号码
	 * @param phone
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-8-6 下午5:43:38
	 */
	public static boolean checkMobilePhoneNumber(String phone){
		return phone == null ? false:Pattern.compile("^1[3,4,5,7,8]\\d{9}$").matcher(phone).matches();
	}

	/**
	 * 验证是否数字
	 * @param num
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-26 上午11:27:00
	 */
	public static boolean isNumber(String num){
		Pattern p = Pattern.compile("\\d+");
		return p.matcher(num).matches();
	}
	
	/**
	 * 验证是否数字
	 * @param num1 num2
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-26 上午11:27:00
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
