package com.yzxdemo.activity;

import java.util.ArrayList;

import com.yzxdemo.R;
import com.yzx.api.CallType;
import com.yzx.api.ConferenceInfo;
import com.yzx.api.ConferenceMemberInfo;
import com.yzx.api.ConferenceState;
import com.yzx.api.UCSCall;
import com.yzx.tools.CustomLog;
import com.yzxdemo.action.UIDfineAction;
import com.yzxdemo.tools.DfineAction;
import com.yzxdemo.ui.ConferenceAddDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConferenceConverseActivity extends ConverseActivity{

	public static final String ACTION_MEETING_STATES = "com.yzx.meeting.states";
	public static final String ACTION_MEETING_ADDPERSONS = "com.yzx.meeting.addpersons";
	private boolean inCall = false;
	private boolean isHasTimer = false;
	private int persons = 0;
	private TextView converse_client;
	private TextView converse_information;
	private TextView dial_close;
	private TextView tv_conference1;
	private TextView tv_conference2;
	private TextView tv_conference3;
	private TextView tv_conference4;
	private TextView tv_conference5;
	private EditText dial_number;
	private ImageButton converse_call_mute;
	private ImageButton converse_call_dial;
	private ImageButton converse_call_speaker;
	private ImageButton converse_call_hangup;
	private ImageButton converse_call_answer;
	private ImageButton converse_call_endcall;
	private ImageButton dial_endcall;
	private LinearLayout key_layout;
	private LinearLayout converse_main;
	private LinearLayout ll_conference1;
	private LinearLayout ll_conference2;
	private LinearLayout ll_conference3;
	private LinearLayout ll_conference4;
	private LinearLayout ll_conference5;
	private RelativeLayout conference_add;
	private ArrayList<ConferenceMemberInfo> conferencelist = new ArrayList<ConferenceMemberInfo>();
	private ArrayList<ConferenceInfo> conferencestates = new ArrayList<ConferenceInfo>();
	private ConferenceAddDialog mConferenceAddDialog;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conference_converse);
		
		IntentFilter ift = new IntentFilter();
		ift.addAction(UIDfineAction.ACTION_DIAL_STATE);
		ift.addAction(UIDfineAction.ACTION_ANSWER);
		ift.addAction(UIDfineAction.ACTION_CALL_TIME);
		ift.addAction(ACTION_MEETING_STATES);
		ift.addAction(ACTION_MEETING_ADDPERSONS);
		registerReceiver(br, ift);
		
		conferencelist = (ArrayList<ConferenceMemberInfo>) getIntent().getSerializableExtra("conference");
		
		initviews();
		if(getIntent().hasExtra("inCall")){
			inCall = getIntent().getBooleanExtra("inCall", false);
		}
		
		//会议来电和发起会议
		if(inCall){
			converse_call_answer.setVisibility(View.VISIBLE);
			converse_call_hangup.setVisibility(View.VISIBLE);
			converse_call_endcall.setVisibility(View.GONE);
			converse_information.setText("会议来电");
			UCSCall.setSpeakerphone(true);
			UCSCall.startRinging(true);
		}else{
			converse_call_answer.setVisibility(View.GONE);
			converse_call_hangup.setVisibility(View.GONE);
			converse_call_endcall.setVisibility(View.VISIBLE);
			dial(conferencelist);
		}
	}
	private void initviews() {		
		converse_client = (TextView) findViewById(R.id.converse_client);
		converse_information = (TextView) findViewById(R.id.conference_time);
		converse_call_mute = (ImageButton) findViewById(R.id.converse_call_mute);
		converse_main = (LinearLayout) findViewById(R.id.converse_main);
		converse_call_answer = (ImageButton)findViewById(R.id.converse_call_answer);
		converse_call_hangup = (ImageButton)findViewById(R.id.converse_call_hangup);
		converse_call_endcall  = (ImageButton)findViewById(R.id.converse_call_endcall);
		key_layout = (LinearLayout) findViewById(R.id.key_layout);
		dial_endcall = (ImageButton) findViewById(R.id.dial_endcall);
		dial_number = (EditText) findViewById(R.id.text_dtmf_number);
		tv_conference1 = (TextView) findViewById(R.id.tv_conference1);
		tv_conference2 = (TextView) findViewById(R.id.tv_conference2);
		tv_conference3 = (TextView) findViewById(R.id.tv_conference3);
		tv_conference4 = (TextView) findViewById(R.id.tv_conference4);
		tv_conference5 = (TextView) findViewById(R.id.tv_conference5);
		ll_conference1 = (LinearLayout) findViewById(R.id.ll_conference1);
		ll_conference2 = (LinearLayout) findViewById(R.id.ll_conference2);
		ll_conference3 = (LinearLayout) findViewById(R.id.ll_conference3);
		ll_conference4 = (LinearLayout) findViewById(R.id.ll_conference4);
		ll_conference5 = (LinearLayout) findViewById(R.id.ll_conference5);
		conference_add = (RelativeLayout) findViewById(R.id.conference_add);
		setPersons();
		
		//增加新成员
		conference_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=mConferenceAddDialog){
					mConferenceAddDialog=null;
				}
				mConferenceAddDialog = new ConferenceAddDialog(ConferenceConverseActivity.this, conferencestates);
				mConferenceAddDialog.show();
			}
		});
		
		//静音
		converse_call_mute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(UCSCall.isMicMute()){
					converse_call_mute.setBackgroundResource(R.drawable.converse_mute);
				}else{
					converse_call_mute.setBackgroundResource(R.drawable.converse_mute_down);
				}
				UCSCall.setMicMute(!UCSCall.isMicMute());
			}
		});
		
		//扬声器
		converse_call_speaker = (ImageButton)findViewById(R.id.converse_call_speaker);
		converse_call_speaker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(UCSCall.isSpeakerphoneOn()){
					converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
				}else{
					converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
				}
				UCSCall.setSpeakerphone(!UCSCall.isSpeakerphoneOn());
			}
		});
		
		//接通
		converse_call_answer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomLog.v(DfineAction.TAG_TCP,"接通电话");
				UCSCall.stopRinging();
				UCSCall.answer("");
				UCSCall.setSpeakerphone(false);
				converse_call_answer.setVisibility(View.GONE);
				converse_call_hangup.setVisibility(View.GONE);
				converse_call_endcall.setVisibility(View.VISIBLE);
			}
		});
				
		//挂断
		converse_call_hangup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomLog.v(DfineAction.TAG_TCP,"挂断电话");
				UCSCall.stopRinging();
				UCSCall.hangUp("");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						ConferenceConverseActivity.this.finish();
					}
				}, 1500);
			}
		});
		
		//结束通话
		converse_call_endcall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomLog.v(DfineAction.TAG_TCP,"结束电话");
				UCSCall.stopRinging();
				UCSCall.setSpeakerphone(false);
				UCSCall.hangUp("");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						ConferenceConverseActivity.this.finish();
					}
				}, 1500);
			}
		});
		
		//结束通话(键盘界面中)
		dial_endcall  = (ImageButton)findViewById(R.id.dial_endcall);
		dial_endcall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomLog.v(DfineAction.TAG_TCP,"结束电话");
				UCSCall.stopRinging();
				UCSCall.setSpeakerphone(false);
				UCSCall.hangUp("");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						ConferenceConverseActivity.this.finish();
					}
				}, 1500);
			}
		});
		//打开键盘
		converse_call_dial  = (ImageButton)findViewById(R.id.converse_call_dial);
		converse_call_dial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomLog.v(DfineAction.TAG_TCP,"打开键盘");
				key_layout.setVisibility(View.VISIBLE);
				converse_main.setVisibility(View.GONE);
			}
		});
		
		//关闭键盘
		dial_close = (TextView) findViewById(R.id.dial_close);
		dial_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomLog.v(DfineAction.TAG_TCP,"关闭键盘");
				key_layout.setVisibility(View.GONE);
				converse_main.setVisibility(View.VISIBLE);
			}
		});
		findViewById(R.id.digit0).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_0,dial_number);
			}
		});
		findViewById(R.id.digit1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_1,dial_number);
			}
		});
		findViewById(R.id.digit2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_2,dial_number);
			}
		});
		findViewById(R.id.digit3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_3,dial_number);
			}
		});
		findViewById(R.id.digit4).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_4,dial_number);
			}
		});
		findViewById(R.id.digit5).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_5,dial_number);
			}
		});
		findViewById(R.id.digit6).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_6,dial_number);
			}
		});
		findViewById(R.id.digit7).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_7,dial_number);
			}
		});
		findViewById(R.id.digit8).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_8,dial_number);
			}
		});
		findViewById(R.id.digit9).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_9,dial_number);
			}
		});
		findViewById(R.id.digit_star).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_STAR,dial_number);
			}
		});
		findViewById(R.id.digit_husa).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UCSCall.sendDTMF(ConferenceConverseActivity.this, KeyEvent.KEYCODE_POUND,dial_number);
			}
		});
	}
	
	//发起会议呼叫，conferencelist为会议成员列表，ConferenceMemberInfo包含ClientID，ClientPhone,callType(ClientID使用Voip，ClientPhone使用直拨)
	public void dial(ArrayList<ConferenceMemberInfo> list){
		Intent intent = new Intent(UIDfineAction.ACTION_DIAL);
		intent.putExtra("conference", list);
		intent.putExtra("type", 4);
		sendBroadcast(intent);
	}
	
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_MEETING_STATES)){
				conferencestates = (ArrayList<ConferenceInfo>) intent.getSerializableExtra("states");
				persons = conferencestates.size();
				for(int i = 0;i<conferencestates.size();i++){
					if("EXITCONFERENCE".equals(conferencestates.get(i).getConferenceState())){
						persons--;
					}
				}
				CustomLog.v("会议中的人数："+persons);
				setPersons();
			} else if(intent.getAction().equals(ACTION_MEETING_ADDPERSONS)){
				if(mConferenceAddDialog!=null)
					mConferenceAddDialog.dismiss();
				conferencelist = null;
				ArrayList<ConferenceMemberInfo> list = (ArrayList<ConferenceMemberInfo>) intent.getSerializableExtra("newpersons");		
				
				//邀请新成员加入
				dial(list);
				
				//移除会议状态中已经推出的成员
				ArrayList<ConferenceInfo> infos = new ArrayList<ConferenceInfo>();
			    for(ConferenceInfo info:conferencestates){
			    	if(info.getConferenceState() == ConferenceState.EXITCONFERENCE){
			    		infos.add(info);
			    	}
			    }
			    conferencestates.removeAll(infos);
			    
			    //显示会议状态中已经存在的成员和正要加入的成员
				for(int i=0;i<list.size();i++){
					ConferenceInfo info = new ConferenceInfo();
					info.setJoinConferenceNumber(list.get(i).getUid());
					info.setConferenceState(ConferenceState.CALLING);
					conferencestates.add(info);
				}
				setPersons();
			}else if(intent.getAction().equals(UIDfineAction.ACTION_DIAL_STATE)){
				int state = intent.getIntExtra("state", 0);
				UCSCall.startCallRinging("dialling_tone.pcm");
				CustomLog.i("AUDIO_CALL_STATE:"+state);
				if(UIDfineAction.dialState.keySet().contains(state)){
					converse_information.setText(UIDfineAction.dialState.get(state));
				}else{
					switch(state){
					case 300301:
						converse_information.setText("会议不存在");
						break;
					case 300302:
						converse_information.setText("会议状态异常");
						break;
					case 300303:
						converse_information.setText("会议室满员");
						Toast.makeText(ConferenceConverseActivity.this, "会议室满员", Toast.LENGTH_SHORT).show();
						break;
					case 300304:
						converse_information.setText("创建会议失败");
						break;
					case 300305:
						converse_information.setText("会议呼叫处理失败");
						break;
					case 300306:
						converse_information.setText("会议媒体资源申请失败");
						break;
					case 300307:
						converse_information.setText("对端不支持会议模式切换");
						break;
					case 300308:
						converse_information.setText("邀请人数过多,只能同时邀请5个");
						break;
					default:
						converse_information.setText("服务器错误");
						break;
					}
				}
				if(state == UCSCall.HUNGUP_REFUSAL 
						|| state == UCSCall.HUNGUP_MYSELF 
						|| state == UCSCall.HUNGUP_OTHER
						|| state == UCSCall.HUNGUP_MYSELF_REFUSAL){
					UCSCall.stopRinging();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							ConferenceConverseActivity.this.finish();
						}
					}, 1500);
				}
			}else if(intent.getAction().equals(UIDfineAction.ACTION_ANSWER)){
				//接通后通知服务开启计时器
				if(!isHasTimer){
					isHasTimer=true;
					sendBroadcast(new Intent(UIDfineAction.ACTION_START_TIME));
				}
				converse_information.setText("会议通话中");
				UCSCall.stopCallRinging();
			}else if(intent.getAction().equals(UIDfineAction.ACTION_CALL_TIME)){
				String timer = intent.getStringExtra("timer");
				if(converse_information != null){
					converse_information.setText(timer);
				}
			}
		}
	};
	
	private void setPersons() {
		if(conferencelist==null){
			//会议接收方 conferencelist为空 根据回调的会议状态信息显示界面
			switch (conferencestates.size()) {
			case 1:	
				setPerson(ll_conference1, tv_conference1, conferencestates.get(0));
				cleanPerson(ll_conference2);
				cleanPerson(ll_conference3);
				cleanPerson(ll_conference4);
				cleanPerson(ll_conference5);
				break;
			case 2:
				setPerson(ll_conference1, tv_conference1, conferencestates.get(0));
				setPerson(ll_conference2, tv_conference2, conferencestates.get(1));
				cleanPerson(ll_conference3);
				cleanPerson(ll_conference4);
				cleanPerson(ll_conference5);
				break;
			case 3:
				setPerson(ll_conference1, tv_conference1, conferencestates.get(0));
				setPerson(ll_conference2, tv_conference2, conferencestates.get(1));
				setPerson(ll_conference3, tv_conference3, conferencestates.get(2));
				cleanPerson(ll_conference4);
				cleanPerson(ll_conference5);
				break;
			case 4:
				setPerson(ll_conference1, tv_conference1, conferencestates.get(0));
				setPerson(ll_conference2, tv_conference2, conferencestates.get(1));
				setPerson(ll_conference3, tv_conference3, conferencestates.get(2));
				setPerson(ll_conference4, tv_conference4, conferencestates.get(3));
				cleanPerson(ll_conference5);
				break;
			case 5:
				setPerson(ll_conference1, tv_conference1, conferencestates.get(0));
				setPerson(ll_conference2, tv_conference2, conferencestates.get(1));
				setPerson(ll_conference3, tv_conference3, conferencestates.get(2));
				setPerson(ll_conference4, tv_conference4, conferencestates.get(3));
				setPerson(ll_conference5, tv_conference5, conferencestates.get(4));
				break;
			}
		}else {
			//会议发起方 conferencelist不为空 根据回调的会议状态信息刷新界面
			switch (conferencelist.size()) {
			case 1:	
				setPerson(ll_conference1, tv_conference1, conferencelist.get(0));
				break;
			case 2:
				setPerson(ll_conference1, tv_conference1, conferencelist.get(0));
				setPerson(ll_conference2, tv_conference2, conferencelist.get(1));
				break;
			case 3:
				setPerson(ll_conference1, tv_conference1, conferencelist.get(0));
				setPerson(ll_conference2, tv_conference2, conferencelist.get(1));
				setPerson(ll_conference3, tv_conference3, conferencelist.get(2));
				break;
			case 4:
				setPerson(ll_conference1, tv_conference1, conferencelist.get(0));
				setPerson(ll_conference2, tv_conference2, conferencelist.get(1));
				setPerson(ll_conference3, tv_conference3, conferencelist.get(2));
				setPerson(ll_conference4, tv_conference4, conferencelist.get(3));
				break;
			case 5:
				setPerson(ll_conference1, tv_conference1, conferencelist.get(0));
				setPerson(ll_conference2, tv_conference2, conferencelist.get(1));
				setPerson(ll_conference3, tv_conference3, conferencelist.get(2));
				setPerson(ll_conference4, tv_conference4, conferencelist.get(3));
				setPerson(ll_conference5, tv_conference5, conferencelist.get(4));
				break;
			}
		}
	}
	
	private void cleanPerson(LinearLayout ll_conference) {
		ll_conference.setVisibility(View.GONE);	
	}
	
	//作为会议被叫方使用这个函数刷新UI
	private void setPerson(LinearLayout ll_conference,
			TextView tv_conference, ConferenceInfo info){
		if("Disconnected".equals(getState(info.getConferenceState()))){
			ll_conference.setVisibility(View.GONE);
		}else {
			ll_conference.setVisibility(View.VISIBLE);
			tv_conference.setText(info.getJoinConferenceNumber()+" "+getState(info.getConferenceState()));
		}
	}
	
	//作为会议发起方使用这个函数刷新UI
	private void setPerson(LinearLayout ll_conference,
			TextView tv_conference, ConferenceMemberInfo conferenceMemberInfo) {
		ll_conference.setVisibility(View.VISIBLE);
		if(!"".equals(conferenceMemberInfo.getUid())){
			if(conferencestates!=null && conferencestates.size()>0){
				for (int i = 0; i < conferencestates.size(); i++) {
					if(conferencestates.get(i).getJoinConferenceNumber().equals(conferenceMemberInfo.getUid())){
						tv_conference.setText(conferenceMemberInfo.getUid()+" "+getState(conferencestates.get(i).getConferenceState()));
						break;
					}
				}
			}else {
				tv_conference.setText(conferenceMemberInfo.getUid()+" Calling");
			}
		}else {
			if(conferencestates!=null && conferencestates.size()>0){
				for (int i = 0; i < conferencestates.size(); i++) {
					if(conferencestates.get(i).getJoinConferenceNumber().equals(conferenceMemberInfo.getPhone())){
						tv_conference.setText(conferenceMemberInfo.getPhone()+" "+getState(conferencestates.get(i).getConferenceState()));
						break;
					}
				}
			}else {
				tv_conference.setText(conferenceMemberInfo.getPhone()+" Calling");
			}
		}	
		if("".equals(tv_conference.getText().toString())){
			tv_conference.setText(conferenceMemberInfo.getUid()+" Calling");
		}
		
		if(tv_conference.getText().toString().contains("Disconnected")){
			ll_conference.setVisibility(View.GONE);
		}
	}
	
	private String getState(ConferenceState state){
		String strstate="";
		switch (state) {
		case UNKNOWN:
			strstate="idle";
			break;
		case CALLING:
			strstate="Calling";
			break;
		case RINGING:
			strstate="Ringing";
			break;
		case JOINCONFERENCE:
			strstate="Connected";
			break;
		case EXITCONFERENCE:
			strstate="Disconnected";
			break;
		case OFFLINE:
			strstate="Disconnected";
			break;
		default:
			break;
		}
		return strstate;
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(br);
		super.onDestroy();
	}
	
}
