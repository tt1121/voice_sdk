package com.yzxdemo.activity;

import com.yzxdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AudioCallActivity extends Activity{
	
	private ImageView audio_call_head;
	private ImageView audio_call_iv2;
	private ImageView audio_call_iv3;
	private ImageView audio_call_iv4;
	private TextView audio_call_admin;
	private TextView audio_call_phone;
	private TextView audio_call_tv2_1;
	private TextView audio_call_tv2_2;
	private TextView audio_call_tv3_1;
	private TextView audio_call_tv3_2;
	private TextView audio_call_tv4_1;
	private TextView audio_call_tv4_2;
	private RelativeLayout audio_call_back;
	private RelativeLayout audio_call_intelligence;
	private RelativeLayout audio_call_free;
	private RelativeLayout audio_call_direct;
	private RelativeLayout audio_call_backcall;
	private String call_phone="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_call);
		initviews();
	}

	private void initviews() {
		audio_call_head = (ImageView) findViewById(R.id.audio_call_head);
		audio_call_iv2 = (ImageView) findViewById(R.id.audio_call_iv2);
		audio_call_iv3 = (ImageView) findViewById(R.id.audio_call_iv3);
		audio_call_iv4 = (ImageView) findViewById(R.id.audio_call_iv4);
		audio_call_admin = (TextView) findViewById(R.id.audio_call_admin);
		audio_call_phone = (TextView) findViewById(R.id.audio_call_phone);
		audio_call_tv2_1 = (TextView) findViewById(R.id.audio_call_tv2_1);
		audio_call_tv2_2 = (TextView) findViewById(R.id.audio_call_tv2_2);
		audio_call_tv3_1 = (TextView) findViewById(R.id.audio_call_tv3_1);
		audio_call_tv3_2 = (TextView) findViewById(R.id.audio_call_tv3_2);
		audio_call_tv4_1 = (TextView) findViewById(R.id.audio_call_tv4_1);
		audio_call_tv4_2 = (TextView) findViewById(R.id.audio_call_tv4_2);
		audio_call_back = (RelativeLayout) findViewById(R.id.audio_call_back);
		audio_call_intelligence = (RelativeLayout) findViewById(R.id.audio_call_intelligence);
		audio_call_free = (RelativeLayout) findViewById(R.id.audio_call_free);
		audio_call_direct = (RelativeLayout) findViewById(R.id.audio_call_direct);
		audio_call_backcall = (RelativeLayout) findViewById(R.id.audio_call_backcall);
		if(null != getIntent().getStringExtra("call_phone")){
			call_phone = getIntent().getStringExtra("call_phone");
		}
		getCallHead();
		audio_call_admin.setText(getIntent().getStringExtra("call_client"));
		
		//�ر�Activity�������ϸ�����
		audio_call_back.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	finish();
            }  
        });  
		
		//VOIP��ѵ绰������ClientID��Ϣ�����н��棬���н���ͨ��ClientID������ѵ绰����
		audio_call_free.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	Intent intent = new Intent(AudioCallActivity.this, AudioConverseActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("call_client", getIntent().getStringExtra("call_client"));
				intent.putExtra("call_type", 1);
				startActivity(intent);
            }  
        }); 
		
		//����а󶨵绰���룬����Խ���ֱ�����ز������ܲ���
		if(!"".equals(call_phone)){
			//���ܲ��򣬴���ClientID��Ϣ�����н��棬���н���ͨ��ClientID�������ܲ���
			audio_call_intelligence.setOnClickListener(new View.OnClickListener() {  
	            public void onClick(View v) {  
	            	Intent intent = new Intent(AudioCallActivity.this, AudioConverseActivity.class);
	            	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("call_phone", getIntent().getStringExtra("call_phone"));
					intent.putExtra("call_type", 5);
					startActivity(intent);
	            }  
	        }); 
			//ֱ��������ClientID�󶨵��ֻ�������Ϣ�����н��棬���н���ͨ��ClientID�󶨵��ֻ��������ֱ��
			audio_call_direct.setOnClickListener(new View.OnClickListener() {  
	            public void onClick(View v) {  
	            	Intent intent = new Intent(AudioCallActivity.this, AudioConverseActivity.class);
	            	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("call_phone", getIntent().getStringExtra("call_phone"));
					intent.putExtra("call_type", 0);
					startActivity(intent);
	            }  
	        });  
			//�ز�������ClientID�󶨵��ֻ�������Ϣ�����н��棬���н���ͨ��ClientID�󶨵��ֻ�������лز�
			audio_call_backcall.setOnClickListener(new View.OnClickListener() {  
	            public void onClick(View v) {  
	            	Intent intent = new Intent(AudioCallActivity.this, AudioConverseActivity.class); 
	            	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("call_phone", getIntent().getStringExtra("call_phone"));
					startActivity(intent);
	            }  
	        });  
		}
	}

	private void getCallHead() {
		switch(getIntent().getIntExtra("call_position", 0)){
		case 0:
			audio_call_head.setBackgroundResource(R.drawable.head1_big);
			break;
		case 1:
			audio_call_head.setBackgroundResource(R.drawable.head2_big);
			break;
		case 2:
			audio_call_head.setBackgroundResource(R.drawable.head3_big);
			break;
		case 3:
			audio_call_head.setBackgroundResource(R.drawable.head4_big);
			break;
		case 4:
			audio_call_head.setBackgroundResource(R.drawable.head5_big);
			break;
		case 5:
			audio_call_head.setBackgroundResource(R.drawable.head6_big);
			break;
		}
		if(!"".equals(call_phone)){
			audio_call_phone.setText("�ֻ���:"+call_phone);
		}else{
			audio_call_iv2.setBackgroundResource(R.drawable.phone_not);
			audio_call_iv3.setBackgroundResource(R.drawable.phone_not);
			audio_call_iv4.setBackgroundResource(R.drawable.phone_not);
			audio_call_tv2_1.setTextColor(Color.parseColor("#ACACAC"));
			audio_call_tv2_2.setTextColor(Color.parseColor("#ACACAC"));
			audio_call_tv3_1.setTextColor(Color.parseColor("#ACACAC"));
			audio_call_tv3_2.setTextColor(Color.parseColor("#ACACAC"));
			audio_call_tv4_1.setTextColor(Color.parseColor("#ACACAC"));
			audio_call_tv4_2.setTextColor(Color.parseColor("#ACACAC"));
		}
	}
}
