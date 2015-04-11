package com.yzx.tcp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.yzx.service.AlarmReceiver;
import com.yzx.service.ConnectionControllerService;
import com.yzx.service.MsgBackReceiver;
import com.yzx.tcp.packet.PacketDfineAction;

/**
 * ����������
 * @author xiaozhenhua
 *
 */
public class AlarmTools {
	
	private static final int PING_TIME = 3 * 60 * 1000;		//3����һ��������
	public static final long PING_BACK = 20 * 1000;
	
	/**
	 * ��������
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-16 ����10:08:32
	 */
	public static void startAlarm() {
		//CustomLog.v( "��ʼ����  ... ");
		PacketDfineAction.PING_COUNT = 0;
		//pinng = true;
		if(ConnectionControllerService.getInstance() != null){
			//CustomLog.v( "2-��ʼ����  ... ");
			Context mContext = ConnectionControllerService.getInstance().getApplicationContext();
			Intent intent = new Intent(mContext, AlarmReceiver.class);
			PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE))
					.setRepeating(AlarmManager.RTC_WAKEUP, PING_TIME, PING_TIME, pendIntent);
		}
	}

	/**
	 * ֹͣ����
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-16 ����10:08:43
	 */
	public static void stopAlarm() {
		//CustomLog.v( "1-ֹͣ����  ... ");
		if(ConnectionControllerService.getInstance() != null){
			//CustomLog.v( "2-ֹͣ����  ... ");
			Context mContext = ConnectionControllerService.getInstance().getApplicationContext();
			Intent intent = new Intent(mContext, AlarmReceiver.class);
			PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pendIntent);
		}
	}
	
	public static void startBackTcpPing(){
		//CustomLog.v( "1-������������ ...");
		if(ConnectionControllerService.getInstance() != null){
			//CustomLog.v( "2-������������ ...");
			Context mContext = ConnectionControllerService.getInstance().getApplicationContext();
			Intent intent = new Intent(mContext, MsgBackReceiver.class);
			PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE))
					.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+20000, PING_BACK, pendIntent);
		}
		
	}
	
	public static void stopBackTcpPing(){
		//CustomLog.v( "1-ֹͣ�������� ... ");
		if(ConnectionControllerService.getInstance() != null){
			//CustomLog.v( "2-ֹͣ�������� ... ");
			Context mContext = ConnectionControllerService.getInstance().getApplicationContext();
			Intent intent = new Intent(mContext, MsgBackReceiver.class);
			PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pendIntent);
		}
	}
	
	public static void stopAll(){
		stopAlarm();
		stopBackTcpPing();
	}
}
