package com.yzx.http.net;


import org.json.JSONException;
import org.json.JSONObject;


public class ParseUtil {

	public static ParseUtil utils;

	public static ParseUtil getInstance() {
		if (utils == null) {
			utils = new ParseUtil();
		}
		return utils;
	}
	
	public boolean isParseJson(JSONObject oJsonObject){
		try {
			return 0==oJsonObject.getInt("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
