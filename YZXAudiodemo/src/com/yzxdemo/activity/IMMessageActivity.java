package com.yzxdemo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.yzxdemo.R;
import com.yzx.api.ClientType;
import com.yzx.api.UCSMessage;
import com.yzx.api.UCSService;
import com.yzx.listenerInterface.RecordListener;
import com.yzx.tcp.packet.UcsMessage;
import com.yzx.tcp.packet.UcsStatus;
import com.yzx.tools.CustomLog;
import com.yzx.tools.FileTools;
import com.yzxdemo.action.UIDfineAction;
import com.yzxdemo.service.ConnectionService;
import com.yzxdemo.tools.DfineAction;
import com.yzxdemo.tools.LoginConfig;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IMMessageActivity extends Activity{

	public static HashMap<String,ArrayList<UcsMessage>> msgList = new HashMap<String,ArrayList<UcsMessage>>();
	public static final String ACTION = "com.yzxproject.resetList";
	public static final String ACTION_MSG = "com.yzxproject.end_failed";
	public static final String ACTION_STATUS = "com.yzxproject.status";
	private Boolean send_record=true;
	private TextView im_client;
	private TextView im_tv_record;
	private TextView im_tv_bottom;
	private TextView im_send_btn;
	private EditText im_send_text;
	private MessageAdapter adapter;
	private String phoneNumber = "";
	private ListView messagelist;
	private LinearLayout im_ll_more;
	private LinearLayout im_ll_file;
	private LinearLayout im_ll_record;
	private RelativeLayout im_more;
	private RelativeLayout im_audio;
	private RelativeLayout im_key;
	private ImageView im_iv_record;
	private Timer timer = null;
	private int num = 0;
	private Handler mHandler; 
	private long uptime=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_immessage);
	
		initviews();
		
		UCSMessage.queryUserState(ClientType.CLIENT, phoneNumber);
		
		IntentFilter ift = new IntentFilter(ACTION);
		ift.addAction(UIDfineAction.ACTION_SEND_FILE_PROGRESS);
		ift.addAction(IMMessageActivity.ACTION_MSG);
		ift.addAction(IMMessageActivity.ACTION_STATUS);
		registerReceiver(br, ift);
	}
	
	private void initviews() {
		im_client = (TextView) findViewById(R.id.im_client);
		im_tv_record = (TextView) findViewById(R.id.im_tv_record);
		im_tv_bottom = (TextView) findViewById(R.id.im_tv_bottom);
		phoneNumber = getIntent().getStringExtra("im_client");
		
		im_client.setText(phoneNumber);
		messagelist = (ListView) findViewById(R.id.im_list);

		messagelist.setDivider(null);
		adapter = new MessageAdapter();
		messagelist.setAdapter(adapter);
		adapter.notifyDataSetChanged(msgList.get(phoneNumber), getIntent().getIntExtra("im_position", 0), getIntent().getIntExtra("im_localposition", 0));
		im_tv_bottom.setText(UCSService.isConnected()?"����:"+LoginConfig.getCurrentClientId(IMMessageActivity.this):"����");
		im_tv_record.setText("��ס˵��");
		
		im_send_text = (EditText) findViewById(R.id.im_send_text);
		im_send_btn = (TextView)findViewById(R.id.im_send_btn);
		im_key = (RelativeLayout) findViewById(R.id.im_key);
		im_audio = (RelativeLayout) findViewById(R.id.im_audio);
		im_more = (RelativeLayout) findViewById(R.id.im_more);
		im_ll_file = (LinearLayout)findViewById(R.id.im_ll_file);
		im_ll_more = (LinearLayout)findViewById(R.id.im_ll_more);
		im_ll_record = (LinearLayout)findViewById(R.id.im_ll_record);
		im_iv_record = (ImageView)findViewById(R.id.im_iv_record);
		
		findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		//������Ϣ�����,�������ݱ仯
		im_send_text.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {	
			}		
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				if("".equals(im_send_text.getText().toString().trim())){
					im_send_btn.setVisibility(View.GONE);
					im_audio.setVisibility(View.VISIBLE);
					im_key.setVisibility(View.GONE);
				}else{
					im_send_btn.setVisibility(View.VISIBLE);
					im_audio.setVisibility(View.GONE);
					im_key.setVisibility(View.GONE);
				}
			}
		});
		
		//������Ϣ�����
		im_send_text.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if("".equals(im_send_text.getText().toString().trim())){
					im_send_btn.setVisibility(View.GONE);
					im_audio.setVisibility(View.VISIBLE);
				}else{
					im_send_btn.setVisibility(View.VISIBLE);
					im_audio.setVisibility(View.GONE);
				}
				im_ll_record.setVisibility(View.GONE);
				im_ll_more.setVisibility(View.GONE);
			}
		});
		
		//����������Ϣ��ť
		im_send_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String text = im_send_text.getText().toString();
				new Thread(new Runnable() {
					@Override
					public void run() {
						UCSMessage.sendUcsMessage(phoneNumber, text, null, UCSMessage.TEXT);
					}
				}).start();
				im_send_text.setText("");
			}
		});
		
		//���̰�ť
		im_key.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				im_key.setVisibility(View.GONE);
				im_audio.setVisibility(View.VISIBLE);
				im_ll_record.setVisibility(View.GONE);
				im_ll_more.setVisibility(View.GONE);
				View view = getWindow().peekDecorView();
		        if (view != null) {
		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            inputmanger.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		        }
			}
		});
		
		//¼����ť
		im_audio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				im_audio.setVisibility(View.GONE);
				im_key.setVisibility(View.VISIBLE);
				im_ll_record.setVisibility(View.VISIBLE);
				im_ll_more.setVisibility(View.GONE);
				View view = getWindow().peekDecorView();
		        if (view != null) {
		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		        }
			}
		});
		
		//���ఴť
		im_more.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(im_ll_more.getVisibility() == View.VISIBLE){
					im_ll_more.setVisibility(View.GONE);
				}else{
					im_ll_more.setVisibility(View.VISIBLE);
				}
				im_ll_record.setVisibility(View.GONE);
				View view = getWindow().peekDecorView();
		        if (view != null) {
		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		        }
			}
		});
		
		//���ݸ�����ť	
		im_ll_file.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openFile();
			}
		});
		
		//��ʼ¼����ť
		im_iv_record.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					if((System.currentTimeMillis()-uptime)<2000){
						return true;
					}
					im_tv_record.setText("�ɿ�����");
					startRecordTimer();
					String path = FileTools.createAudioFileName(phoneNumber);
					send_record=true;
					UCSMessage.startVoiceRecord(path, new RecordListener() {
						@Override
						public void onFinishedRecordingVoice(int arg0) {
							if(send_record){
								CustomLog.v(DfineAction.TAG_TCP,"¼�����,ʱ��Ϊ:"+arg0+phoneNumber);
								CustomLog.v(DfineAction.TAG_TCP,"¼�����,¼���ļ�Ϊ:"+im_iv_record.getTag().toString());
								UCSMessage.sendUcsMessage(phoneNumber, null, im_iv_record.getTag().toString(), UCSMessage.VOICE);
							}
						}
						@Override
						public void onFinishedPlayingVoice() {							
						}
					});
					im_iv_record.setTag(path);
					break;
				case MotionEvent.ACTION_UP:
					if((System.currentTimeMillis()-uptime)<2000){
						Toast.makeText(IMMessageActivity.this, "���б�Ҫ�����ô���������㣡", Toast.LENGTH_SHORT).show();
						return true;
					}else{
						uptime = System.currentTimeMillis();
					}
					im_tv_record.setText("��ס˵��");
					im_iv_record.setBackgroundResource(R.drawable.im_record1);
					stopTimer();
					UCSMessage.stopVoiceRecord();
					break;
				case MotionEvent.ACTION_MOVE:
					float y = event.getY();
					if(y<0){
						im_tv_record.setText("ȡ������");
						send_record=false;
					}else {
						im_tv_record.setText("�ɿ�����");
						send_record=true;
					}
					break;
				}
				return true;
			}
		});
		
		mHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				IMMessageActivity.this.handleMessage(msg);
				return true;
			}
		});
	}

	
	protected void handleMessage(Message msg) {
		int num;
		if(send_record){
			num = msg.what;
		}else {
			num = msg.what+3;
		}
		switch(num){
			case 1:
				im_iv_record.setBackgroundResource(R.drawable.im_record1);
				break;
			case 2:
				im_iv_record.setBackgroundResource(R.drawable.im_record2);
				break;
			case 3:
				im_iv_record.setBackgroundResource(R.drawable.im_record3);
				break;
			case 4:
				im_iv_record.setBackgroundResource(R.drawable.im_cancel1);
				break;
			case 5:
				im_iv_record.setBackgroundResource(R.drawable.im_cancel2);
				break;
			case 6:
				im_iv_record.setBackgroundResource(R.drawable.im_cancel3);
				break;
		}
	}

	private void startRecordTimer(){
		stopTimer();
		if(timer == null){
			timer = new Timer();
		}
		num = 0; 
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				num++;
				mHandler.obtainMessage((num%3+1)).sendToTarget();
			}
		}, 0, 600);
	}
	
	private void stopTimer(){
		if (timer != null){
			timer.cancel();
			timer=null;
		}
	}
	
	private void openFile(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "��ѡ��һ��Ҫ�ϴ����ļ�"),100);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(this, "�����豸��û���ļ��������",Toast.LENGTH_SHORT).show();
		} 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == 100) {
			Uri uri = data.getData();
			CustomLog.i(DfineAction.TAG_TCP,"RESULT_PATH:"+uri.toString());
			String path = getFilePathFromUri(uri);
			CustomLog.i(DfineAction.TAG_TCP,"RESULT_PATH:"+path);
			File file = new File(path);
			CustomLog.i(DfineAction.TAG_TCP,"RESULT_PATH_EXISTS:"+file.exists());
			if(file.exists()){
				final String filePath = path;
				new Thread(new Runnable() {
					@Override
					public void run() {
						UCSMessage.sendUcsMessage(phoneNumber, null, filePath, FileTools.isPic(filePath)?UCSMessage.PIC:UIDfineAction.FILE);
					}
				}).start();
			}else{
				Toast.makeText(IMMessageActivity.this, "�ļ�·���쳣�����ļ������ڻ����ļ�����Ϊ����", Toast.LENGTH_SHORT).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * ����Uri ��ȡ�ļ�ʵ��·��
	 * @param fileUrl
	 * @return
	 */
	public String getFilePathFromUri(Uri fileUrl) {
		String fileName = "";
		if (fileUrl != null) {
			if (fileUrl.getScheme().toString().compareTo("content") == 0) {
				Cursor cursor = getContentResolver().query(fileUrl, null, null,null, null);
				if (cursor != null && cursor.moveToFirst()) {
					if(cursor.getColumnIndex(MediaStore.Images.Media.DATA) > -1){
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						fileName = cursor.getString(column_index);
						if (!fileName.startsWith("/data") && !fileName.startsWith("/storage") && !fileName.startsWith("/mnt")) {
							fileName = "/mnt" + fileName;
						}
					}
					cursor.close();
				}
			} else if (fileUrl.getScheme().compareTo("file") == 0){
				fileName = fileUrl.toString();
				fileName = fileUrl.toString().replace("file://", "");
				if(!fileName.startsWith("/storage")){
					int index = fileName.indexOf("/sdcard");
					fileName = index == -1 ? fileName : fileName.substring(index);
					if (!fileName.startsWith("/mnt")) {
						fileName = "/mnt" + fileName;
					}
				}
			}
		}
		return fileName;
	}
	
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			if(intent.getAction().equals(ACTION)){
				if(adapter != null){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged(msgList.get(phoneNumber), getIntent().getIntExtra("im_toposition", 0)
									, getIntent().getIntExtra("im_localposition", 0));
							messagelist.setSelection(adapter.getCount());
						}
					});
				}
			}else if(intent.getAction().equals(UIDfineAction.ACTION_SEND_FILE_PROGRESS)){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int progress = intent.getIntExtra(UIDfineAction.RESULT_KEY, 0);
						if(progress < 100){
							im_send_text.setText("����:"+progress+"%");
						}else{
							im_send_text.setText("");
						}
					}
				});
			}else if(intent.getAction().equals(IMMessageActivity.ACTION_MSG)){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int reason = intent.getIntExtra(UIDfineAction.REASON_KEY, 0);
						String msg = "";
						switch(reason){
						case 300227:
							msg = "�ļ����ܴ���100M";
							break;
						case 300228:
							msg = "�����ļ���ʱ";
							break;
						case 300229:
							msg = "�����ļ��ɹ�������JSON����";
							break;
						case 300231:
							msg = "��Ϣ�����߻�����Ϣ���Ͳ���Ϊ�ջ������ID����";
							break;
						case 300232:
							msg = "��Ϣ������ֻ��Ϊ����";
							break;
						case 300244:
							msg = "��Ϣ���ͳ�ͻ�򲻴���(�Զ���������10-29֮��)";
							break;
						case 300245:
							msg = "�����ļ������ڻ����ļ�����Ϊ����";
							break;
						case 300246:
							msg = "������Ϣ�ı�����,���ܴ���500";
							break;
						case 300013:
							msg = "��֧�ָ��ļ���ʽ����";
							break;
						default:
							msg = "δ֪����";
							break;
						}
						Toast.makeText(IMMessageActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});
			}else if(intent.getAction().equals(IMMessageActivity.ACTION_STATUS)){
				UcsStatus status = ConnectionService.mapstatus.get(phoneNumber);
				if(status != null){
					Toast.makeText(IMMessageActivity.this, status.isOnline()?"����":"������", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	class MessageAdapter extends BaseAdapter{

		private ArrayList<UcsMessage> currentMsgList = new ArrayList<UcsMessage>();
		private int toClientPosition;
		private int localClientPosition;
		public void notifyDataSetChanged(ArrayList<UcsMessage> list, int toPhoneNumber,int localPhoneNumber) {
			currentMsgList.clear();
			if(list != null && list.size() > 0){
				currentMsgList.addAll(list);
			}
			
			toClientPosition=toPhoneNumber;
			localClientPosition=localPhoneNumber;
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return currentMsgList.size();
		}

		@Override
		public Object getItem(int position) {
			return currentMsgList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			final UcsMessage messageItem = (UcsMessage)getItem(position);
			final HolderView hv ;

//			if(view == null){
				hv = new HolderView();
				if (messageItem.getType() == UCSMessage.SEND){
					view = LayoutInflater.from(IMMessageActivity.this).inflate(R.layout.listitem_message_right, null);
	            }else{
	            	view = LayoutInflater.from(IMMessageActivity.this).inflate(R.layout.listitem_message_left, null);
	            }
				hv.msgitem = (RelativeLayout)view.findViewById(R.id.message_list);
				hv.msgimage = (ImageView)view.findViewById(R.id.message_list_iv);
				hv.msgtext = (TextView)view.findViewById(R.id.message_list_tv);
//				view.setTag(hv);
//			}else{
//				hv = (HolderView)view.getTag();
//			}
			
			if(messageItem.getType() == UCSMessage.SEND){
				hv.msgimage.setBackgroundResource(getIMHead(localClientPosition));
				switch(messageItem.getSendSuccess()){
				case 0:
					//����
					break;
				case 1:
					//���ͳɹ�
					break;
				case 2:
					//����ʧ��
					break;
				}
			}else{
				//����
				hv.msgimage.setBackgroundResource(getIMHead(toClientPosition));
			}
			
			if(messageItem.getExtra_mime() == UCSMessage.PIC){
				hv.msgitem.setClickable(true);
				hv.msgtext.setText("ͼƬ    "+messageItem.getFileName()+"    "+messageItem.getFileSize()+"K");
				hv.msgitem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(IMMessageActivity.this,IMImageActivity.class);
						intent.putExtra("msgid", messageItem.getMsgId());
						intent.putExtra("path", messageItem.getMsg());
						intent.putExtra("formuid", messageItem.getFormuid());
						intent.putExtra("fileName", messageItem.getFileName());
						intent.putExtra("fileSize", Integer.parseInt(messageItem.getFileSize()));
						startActivity(intent);
					}
				});
			}else if(messageItem.getExtra_mime() == UCSMessage.VOICE){
				hv.msgitem.setClickable(true);
				hv.msgtext.setText("����");
				hv.msgitem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(IMMessageActivity.this,IMAudioActivity.class);
						intent.putExtra("msgid", messageItem.getMsgId());
						intent.putExtra("path", messageItem.getMsg());
						intent.putExtra("formuid", messageItem.getFormuid());
						startActivity(intent);
					}
				});
			}else if(messageItem.getExtra_mime() == UCSMessage.VIDEO){
				hv.msgitem.setClickable(true);
				hv.msgtext.setText("��Ƶ");
				hv.msgitem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			}else if(messageItem.getExtra_mime() == UIDfineAction.LOCATION){
				hv.msgitem.setClickable(true);
				hv.msgtext.setText("λ��");
				hv.msgitem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			}else if(messageItem.getExtra_mime() == UCSMessage.TEXT){
				//hv.message_item.setClickable(false);
				hv.msgtext.setText(messageItem.getMsg());
			}else if(messageItem.getExtra_mime() == UIDfineAction.FILE){
				hv.msgitem.setClickable(true);
				hv.msgtext.setText("����    "+messageItem.getFileName()+"    "+messageItem.getFileSize()+"K");
				hv.msgitem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(IMMessageActivity.this,IMFileActivity.class);
						intent.putExtra("msgid", messageItem.getMsgId());
						intent.putExtra("path", messageItem.getMsg());
						intent.putExtra("formuid", messageItem.getFormuid());
						intent.putExtra("fileName", messageItem.getFileName());
						startActivity(intent);
					}
				});
			}
			return view;
		}
		
		class HolderView{
			public RelativeLayout msgitem;
			public ImageView msgimage;
			public TextView msgtext;
		}
		
		private int getIMHead(int position) {
			switch(position){
			case 0:
				return R.drawable.head1_small;
			case 1:
				return R.drawable.head2_small;
			case 2:
				return R.drawable.head3_small;
			case 3:
				return R.drawable.head4_small;
			case 4:
				return R.drawable.head5_small;
			case 5:
				return R.drawable.head6_small;
			default:
				return R.drawable.head1_small;
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(br);
		super.onDestroy();
	}
	
}
