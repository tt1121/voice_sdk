package com.yzxdemo.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yzxdemo.R;
import com.yzx.api.CallType;
import com.yzx.api.ConferenceMemberInfo;
import com.yzx.tools.CustomLog;
import com.yzxdemo.tools.Config;
import com.yzxdemo.tools.DfineAction;
import com.yzxdemo.tools.LoginConfig;
import com.yzxdemo.ui.ConferenceDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConferenceActivity extends Activity{
	public static final String ACTION_ADDPERSON = "com.yzxproject.conference.addperson";
	private ListView conference_list;
	private conferenceAdapter adapter;
	private TextView conference_start;
	private TextView confercnce_custom_client;
	private RelativeLayout rl_back;
	private RelativeLayout mFooterView;
	private ConferenceDialog mConferenceDialog;
	private int num;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conference);
		initviews();
		IntentFilter ift = new IntentFilter(ACTION_ADDPERSON);
		registerReceiver(br, ift);
	}

	private void initviews() {
		conference_list = (ListView)findViewById(R.id.conference_list);
		conference_start = (TextView) findViewById(R.id.conference_start);
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		adapter = new conferenceAdapter(this);
		addFooterView();
		conference_list.setAdapter(adapter);
		confercnce_custom_client = (TextView) findViewById(R.id.confercnce_custom_client);
		rl_back.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	finish();
            }  
        });
		
		//对被选中的对象发起会议呼叫
		conference_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(adapter!=null){
					ArrayList<ChildClient> clients = adapter.getClients();
					ArrayList<ConferenceMemberInfo> list = new ArrayList<ConferenceMemberInfo>();
					int conferencenum = 0;//会议最多支持6人，选中超过5人显示满员
					for(int i=0;i<clients.size();i++){
						if(clients.get(i).getCheck()){
							conferencenum++;
							if(conferencenum > 5){
								Toast.makeText(ConferenceActivity.this, "会议满员", Toast.LENGTH_SHORT).show();
								return;
							}
							ConferenceMemberInfo conference = new ConferenceMemberInfo();
							//因为默认测试账户为6个，除掉本身是5个，所以大于5的是设置的手机号码
							if(i<6){
								conference.setUid(clients.get(i).getClientid());
								conference.setPhone(Config.getMobile(clients.get(i).getClientid()));
							}else {
								conference.setUid("");
								conference.setPhone(clients.get(i).getClientid());
							}
							list.add(conference);
						}
					}
					Intent intent = new Intent(ConferenceActivity.this, ConferenceConverseActivity.class); 
					intent.putExtra("conference", (Serializable)list);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			}
		});
		
		//弹出会议增加成员对话框，增加手机号码到成员列表，发起会议时被选择的如果是手机号码则使用直拨
		confercnce_custom_client.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	if(mConferenceDialog==null){
            		mConferenceDialog = new ConferenceDialog(ConferenceActivity.this);
            	}
            	mConferenceDialog.show();
            }  
        });
	}
	
	private void addFooterView(){
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFooterView = (RelativeLayout) inflater.inflate(R.layout.confercence_add, null);
		conference_list.addFooterView(mFooterView);
	}
	
	//接收会议增加成员对话框发送的广播后增加成员到列表中
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_ADDPERSON)){
				adapter.addClient(intent.getStringExtra("phone"));
			}
		}
	};
	
	class conferenceAdapter extends BaseAdapter{
		private ArrayList<String> clientList = new ArrayList<String>();
		private ArrayList<String> phoneList = new ArrayList<String>();
		private LayoutInflater mInflater;
		private ArrayList<ChildClient> clients = new ArrayList<ChildClient>();
		
		public conferenceAdapter(Context context) {
			clientList.addAll(Arrays.asList(Config.getClient_id().split(",")));
			phoneList.addAll(Arrays.asList(Config.getMobile().split(",")));
			mInflater = LayoutInflater.from(context);
			CustomLog.i(DfineAction.TAG_TCP, LoginConfig.getCurrentClientId(context));
			int len = clientList.size();
			for(int i=0;i<len;i++){
				if(clientList.get(i).equals(LoginConfig.getCurrentClientId(context))){
					clientList.remove(i);
					num=i;
					break;
				}
			}
			for(int i=0;i<clientList.size();i++){
				ChildClient child = new ChildClient();
				child.setClientid(clientList.get(i));
				child.setPhone(Config.getMobile(clientList.get(i)));
				clients.add(child);
			}
			if(len==0){
				Toast.makeText(context, "您已掉线，请重新连接", Toast.LENGTH_LONG).show();
			}
		}
		
		public void addClient(String phone){
			for(int i=0;i<clients.size();i++){
				if(phone.equals(clients.get(i).getClientid())){
					return;
				}
			}
			ChildClient child = new ChildClient();
			child.setClientid(phone);
			clients.add(child);
			notifyDataSetChanged();
		}
		
		public ArrayList<ChildClient> getClients(){
			return clients;
		}
		
		@Override
		public int getCount() {
			return clients.size();
		}


		@Override
		public Object getItem(int position) {
			return clients.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
//			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_login, null);
				viewHolder.conference_list_iv = (ImageView) convertView.findViewById(R.id.login_list_iv);
				viewHolder.conference_list_cb = (CheckBox) convertView.findViewById(R.id.login_list_cb);
				viewHolder.conference_list_tv_client = (TextView) convertView.findViewById(R.id.login_list_tv_client);
				viewHolder.conference_list_tv_phone = (TextView) convertView.findViewById(R.id.login_list_tv_phone);
				convertView.setTag(viewHolder);
//			} else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
			int pos=position;
			if(num<position || num==position){
				pos++;
			}
			switch(pos){
			case 0:
				viewHolder.conference_list_iv.setBackgroundResource(R.drawable.head1_small);
				break;
			case 1:
				viewHolder.conference_list_iv.setBackgroundResource(R.drawable.head2_small);
				break;
			case 2:
				viewHolder.conference_list_iv.setBackgroundResource(R.drawable.head3_small);
				break;
			case 3:
				viewHolder.conference_list_iv.setBackgroundResource(R.drawable.head4_small);
				break;
			case 4:
				viewHolder.conference_list_iv.setBackgroundResource(R.drawable.head5_small);
				break;
			case 5:
				viewHolder.conference_list_iv.setBackgroundResource(R.drawable.head6_small);
				break;
			default:
				viewHolder.conference_list_iv.setBackgroundResource(R.drawable.conference_custom);
				break;
			}
			viewHolder.conference_list_tv_client.setText(clients.get(position).getClientid());
			viewHolder.conference_list_tv_phone.setText(clients.get(position).getPhone());
			
			clients.get(position).setCheck(viewHolder.conference_list_cb.isChecked());
			viewHolder.conference_list_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						clients.get(position).setCheck(true);
					}else {
						clients.get(position).setCheck(false);
					}
				}
			});
			return convertView;
		}
		
		class ViewHolder{
			public ImageView conference_list_iv;
			public CheckBox conference_list_cb;
			public TextView conference_list_tv_client;
			public TextView conference_list_tv_phone;
		}	
	}
	
	class ChildClient{
		private String clientid="";
		private String phone="";
		private Boolean check=false;
		
		public String getClientid() {
			return clientid;
		}
		
		public void setClientid(String clientid) {
			this.clientid = clientid;
		}
		
		public String getPhone() {
			return phone;
		}
		
		public void setPhone(String phone) {
			this.phone = phone;
		}
		
		public Boolean getCheck() {
			return check;
		}
		
		public void setCheck(Boolean check) {
			this.check = check;
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(br);
		super.onDestroy();
	}
}
