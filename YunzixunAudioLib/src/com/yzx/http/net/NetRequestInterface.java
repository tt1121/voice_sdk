package com.yzx.http.net;

import android.content.Context;

public interface NetRequestInterface {
	
	public static String REQUESTYPE = "requestype";
	
	public static String REQUESTBYPOST = "post";
	
	public static String REQUESTBYGET = "get";

	void dorequest(final NetParameters params,
			final NetResponseListener responselistener,
			final Context context, final int tag);
}