package com.yzx.http.net;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yzx.api.UCSService;
import com.yzx.tools.CustomLog;

public class NetRequestInterfaceImp implements NetRequestInterface {

	int repetcount = 0;
	boolean ispost = false;

	public void dorequest(final NetParameters params,
			final NetResponseListener responselistener, final Context context,
			final int tag) {
		final String url = params.getParam("url");
		//CustomLog.v("«Î«Ûµÿ÷∑”Ú√˚£∫tag="+tag+" url="+url);
		if (params.getParam(NetRequestInterface.REQUESTYPE).equals("post")) {
			ispost = true;
		}
		params.removeParam(NetRequestInterface.REQUESTYPE);
		params.removeParam("url");
		NetAndroidClient.getNetAndroidClient(context).api(params, url, tag,
				ispost,responselistener);

	}
}
