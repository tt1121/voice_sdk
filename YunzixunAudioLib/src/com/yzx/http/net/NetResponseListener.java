package com.yzx.http.net;


public interface NetResponseListener {

	void onComplete(Object object, int tag);

	
	void onException(Exception e, int tag);
}
