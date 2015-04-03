package com.yzx.http.net;


import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONException;

import com.yzx.tools.CustomLog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class NetAndroidClient {
	private Context context;
	private static NetAndroidClient to8ToAndroidClient;
	private int connectTimeout = 10000;// 10
	private int readTimeout = 30000;// 30

	public static NetAndroidClient getNetAndroidClient(Context context) {
		if (to8ToAndroidClient == null) {
			init(context);
		}
		return to8ToAndroidClient;
	}

	private static void init(Context context) {
		to8ToAndroidClient = new NetAndroidClient();
		to8ToAndroidClient.setContext(context);
	}

	private NetAndroidClient() {
	}

	public void api(final NetParameters params, final String hosturl, final int tag,
			final boolean ispostmethod,final NetResponseListener responselistener) {
		if (params == null) {
			throw new IllegalArgumentException("params must not null.");
		}

		new Thread() {
			@Override
			public void run() {
				invokeApi(params,tag,hosturl,ispostmethod,responselistener);
			}
		}.start();

	}

	private void invokeApi(NetParameters params, int tag,
			String hosturl, boolean ispostmethod,final NetResponseListener responselistener) {
		try {
			if (ispostmethod) {
				//CustomLog.v("###WebUtils.doPost###");
				String jsonStr = WebUtils.doPost(context, hosturl,
						generateApiParams(params),
						params.getAttachments(), connectTimeout, readTimeout); 
				//handleApiResponse(handler, jsonStr);
				if(responselistener != null){
					responselistener.onComplete(jsonStr, tag);
				}
			} else {
				//CustomLog.v("###WebUtils.doGet### tag="+tag);
				String jsonStr = WebUtils.doGet(context, hosturl, generateApiParams(params));
				if(responselistener != null){
					responselistener.onComplete(jsonStr, tag);
				}
				//handleApiResponse(handler, jsonStr);
			}
		} catch (Exception e) {
			if(responselistener != null){
				//CustomLog.v("###responselistener.onException###"+e.toString());
				responselistener.onException(e, tag);
			} 
		}
	}

	public static Map<String, String> generateApiParams(
			NetParameters topParameters) throws IOException {
		TreeMap<String, String> params = new TreeMap<String, String>();
		Map<String, String> map = topParameters.getParams();
		if (map != null) {
			Set<Entry<String, String>> set = map.entrySet();
			for (Entry<String, String> entry : set) {
				params.put(entry.getKey(), entry.getValue());
			}
		}

		return params;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

}
