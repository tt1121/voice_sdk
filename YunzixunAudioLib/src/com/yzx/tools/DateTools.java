package com.yzx.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTools {

	public static String getDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(new Date());
	}
	
}
