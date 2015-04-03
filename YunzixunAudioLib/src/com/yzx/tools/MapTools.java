package com.yzx.tools;

import org.json.JSONException;
import org.json.JSONObject;

public class MapTools {

	
	/**
	 * ��װ��ַλ��JSON
	 * @param description:��ַ
	 * @param longitude:����
	 * @param latitude��γ��
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-22 ����5:09:52
	 */
	public static String combinationLocationJson(String description,String longitude,String latitude){
		JSONObject json = new JSONObject();
		try {
			json.put("description", description);
			json.put("longitude", longitude);
			json.put("latitude", latitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject location = new JSONObject();
		try {
			location.put("location", json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return location.toString();
	}
}
