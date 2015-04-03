package com.yzxdemo.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.yzx.api.CallType;
import com.yzx.api.ConferenceInfo;
import com.yzx.api.ConferenceMemberInfo;
import com.yzx.tools.CustomLog;
import com.yzxdemo.R;
import com.yzxdemo.activity.ConferenceActivity;
import com.yzxdemo.activity.ConferenceConverseActivity;
import com.yzxdemo.tools.Config;
import com.yzxdemo.tools.DfineAction;
import com.yzxdemo.tools.LoginConfig;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

public class ConferenceAddDialog extends Dialog{

	private ListView conference_list;
	private conferenceAdapter adapter;
	private TextView conference_start_again;
	private Context mcontext;
	private int num;
	public ConferenceAddDialog(Context context, ArrayList<ConferenceInfo> conferencestates) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_conferenceadd);
		mcontext = context;
		conference_list = (ListView)findViewById(R.id.conference_list);
		conference_start_again = (TextView) findViewById(R.id.conference_start_again);
		adapter = new conferenceAdapter(mcontext);
		conference_list.setAdapter(adapter);
		
		//对被选中的对象发起会议呼叫
		conference_start_again.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(adapter!=null){
					ArrayList<ChildClient> clients = adapter.getClients();
					ArrayList<ConferenceMemberInfo> list = new ArrayList<ConferenceMemberInfo>();
					for(int i=0;i<clients.size();i++){
						if(clients.get(i).getCheck()){
							ConferenceMemberInfo conference = new ConferenceMemberInfo();
							CustomLog.v(DfineAction.TAG_TCP, "选中client"+clients.get(i).getClientid());
							//因为默认测试账户为6个，除掉本身是5个，所以大于5的是设置的手机号码，手机号码则使用直拨
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
					Intent intent = new Intent(ConferenceConverseActivity.ACTION_MEETING_ADDPERSONS); 
					intent.putExtra("newpersons", (Serializable)list);
					mcontext.sendBroadcast(intent);
				}
			}
		});
	}
	

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
			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_login, null);
				viewHolder.conference_list_iv = (ImageView) convertView.findViewById(R.id.login_list_iv);
				viewHolder.conference_list_cb = (CheckBox) convertView.findViewById(R.id.login_list_cb);
				viewHolder.conference_list_tv_client = (TextView) convertView.findViewById(R.id.login_list_tv_client);
				viewHolder.conference_list_tv_phone = (TextView) convertView.findViewById(R.id.login_list_tv_phone);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
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
}
