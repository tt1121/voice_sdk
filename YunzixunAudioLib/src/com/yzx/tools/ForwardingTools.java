package com.yzx.tools;

import com.yzx.api.UCSCall;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.preference.UserData;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * ����ת�����ù�����
 * @author xiaozhenhua
 *
 */
public class ForwardingTools {
	
	
	private TelephonyManager callForwardingManager;
	private CallForwardingListener callForwardingListener;
	
	static ForwardingTools forwardTools = null;
	public static ForwardingTools getInstance(){
		if(forwardTools == null){
			forwardTools = new ForwardingTools();
		}
		return forwardTools;
	}
	
	public ForwardingTools(){
		
	}
	
	public void initForwarding(Context mContext){
		if(callForwardingManager == null){
			callForwardingManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			callForwardingListener = new CallForwardingListener();
			callForwardingManager.listen(callForwardingListener,PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
		}
	}

	public void cancalForwardListener() {
		if (callForwardingManager != null && callForwardingListener != null) {
			callForwardingManager.listen(callForwardingListener, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
		}
	}
	
	class CallForwardingListener extends PhoneStateListener {
		@Override
		public void onCallForwardingIndicatorChanged(boolean cfi) {
			onCallForwardingChanged(cfi);
		}
	}
	
	private void onCallForwardingChanged(boolean cfi){
		CustomLog.v("FORWARDING:"+cfi);
		UserData.saveForwarding(cfi);
		if(UserData.isCurrentForwarding()){
			UserData.saveCurrentForwarding(false);
			if(cfi){
				notifyCallForwarding(new UcsReason(300400));
			}else{
				notifyCallForwarding(new UcsReason(300401));
			}
		}
	}
	
	/**
	 * ��������ת��
	 * @param mContext
	 * @author: xiaozhenhua
	 * @data:2014-11-25 ����6:03:33
	 */
	public void openCallForwardingOption(Context mContext){
		CustomLog.v("FORWARDING_NUMBER:"+UserData.getForwardNumber());
		if(UserData.getForwardNumber() != null && UserData.getForwardNumber().length() > 0){
			UserData.saveCurrentForwarding(true);
			Intent forwarding = new Intent(Intent.ACTION_CALL);
			forwarding.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String operator = DevicesTools.getSimOperator(mContext);
			if(operator.equals("46001")){
				CustomLog.v("OPERATOR:��ͨ");
				forwarding.setData(Uri.parse("tel:**21*"+UserData.getForwardNumber()+"%23"));
			}else if(operator.equals("46000")
					|| operator.equals("46002")){
				CustomLog.v("OPERATOR:�ƶ�");
				forwarding.setData(Uri.parse("tel:*21*"+UserData.getForwardNumber()+"%23"));
			}else if(operator.equals("46003")){
				CustomLog.v("OPERATOR:����");
				forwarding.setData(Uri.parse("tel:*72"+UserData.getForwardNumber()));
				//���ڵ�����Ӫ�̻�ȡ��������ת��״̬,���Գ�����Ϊֻ�ڿ����˺���ת��Ĭ�Ͼ�Ϊ�ɹ�
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						onCallForwardingChanged(true);
//					}
//				}, 1000);
			}
			if(operator.startsWith("4600")){
				//Ĭ�����ÿ�����ת�ɹ�
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						onCallForwardingChanged(true);
					}
				}, 1000);
				mContext.startActivity(forwarding);
				CustomLog.v("SEND OPEN ACTION_CALL");
			}else {
				notifyCallForwarding(new UcsReason(300402));
			}
		}else{
			notifyCallForwarding(new UcsReason(300402));
		}
	}
	/**
	 * �رպ���ת��
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-11-25 ����6:03:47
	 */
	public void closeCallForwardingOption(Context mContext){
		UserData.saveCurrentForwarding(true);
		Intent forwarding = new Intent(Intent.ACTION_CALL);
		forwarding.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String operator = DevicesTools.getSimOperator(mContext);
		if(operator.equals("46001")){
			forwarding.setData(Uri.parse("tel:%23%2321%23"));
		}else if(operator.equals("46000")
				|| operator.equals("46002")){
			forwarding.setData(Uri.parse("tel:%2321%23"));
		}else if(operator.equals("46003")){
			forwarding.setData(Uri.parse("tel:*720"));
			//���ڵ�����Ӫ�̻�ȡ��������ת��״̬,���Գ�����Ϊֻ�ڿ����˺���ת��Ĭ�Ͼ�Ϊ�ɹ�
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					onCallForwardingChanged(false);
//				}
//			}, 1000);
		} 
		if(operator.startsWith("4600")){
			//Ĭ�����ÿ�����ת�ɹ�
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					onCallForwardingChanged(false);
				}
			}, 1000);
			mContext.startActivity(forwarding);
			CustomLog.v("SEND OPEN ACTION_CALL");
		}else {
			notifyCallForwarding(new UcsReason(300402));
		}
	}
	
	private void notifyCallForwarding(UcsReason reason){
		if(UCSCall.getForListener() != null){
			UCSCall.getForListener().onCallForwardingIndicatorChanged(reason);
		}
	}
}
