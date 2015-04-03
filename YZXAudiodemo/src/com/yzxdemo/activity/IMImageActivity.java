package com.yzxdemo.activity;

import java.util.ArrayList;

import com.yzxdemo.R;
import com.yzx.api.UCSMessage;
import com.yzx.listenerInterface.MessageListener;
import com.yzx.listenerInterface.UcsReason;
import com.yzx.tcp.packet.UcsMessage;
import com.yzx.tools.FileTools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class IMImageActivity extends Activity implements MessageListener {

	private ImageView image;
	private String msgid;
	private String fileName;
	private TextView process;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_imimage);
		
		process = (TextView)findViewById(R.id.process);
		image = (ImageView)findViewById(R.id.image_view);
		msgid = getIntent().getStringExtra("msgid");
		fileName = getIntent().getStringExtra("fileName");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String path = getIntent().getStringExtra("path");
				String formuid = getIntent().getStringExtra("formuid");
				if(path.startsWith("http:")){
					String localPath = FileTools.createPicFilePath(formuid)+fileName;
					UCSMessage.downloadAttached(path, localPath, msgid, IMImageActivity.this);
				}else{
					//ͼƬ̫������ڴ����
					Bitmap bitmap;
					bitmap = compressImageFromFile(path);
					Drawable drawable = new BitmapDrawable(bitmap);
					
					Message msg = new Message();
					msg.what=1;
					msg.obj=drawable;
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}

	private Bitmap compressImageFromFile(String srcPath) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        newOpts.inJustDecodeBounds = true;//ֻ����,��������  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        float hh = 640f;//  
        float ww = 512f;//  
  
        int be = 1;  
        if (w > h && w > ww) {  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//���ò�����  
          
        newOpts.inPreferredConfig = Config.ARGB_8888;//��ģʽ��Ĭ�ϵ�,�ɲ���  
        newOpts.inPurgeable = true;// ͬʱ���òŻ���Ч  
        newOpts.inInputShareable = true;//����ϵͳ�ڴ治��ʱ��ͼƬ�Զ�������  
          
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return bitmap;  
    }  
	
	private Handler mHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			image.setBackgroundDrawable((Drawable) msg.obj);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UCSMessage.removeMessageListener(IMImageActivity.this);
	}

	@Override
	public void onReceiveUcsMessage(final UcsReason reason,UcsMessage message) {
		if(reason.getReason() != 0){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(IMImageActivity.this, "�����ļ�ʧ��:"+reason.getReason(), Toast.LENGTH_SHORT).show();
				}
			});
		}
		
	}

	@Override
	public void onSendUcsMessage(UcsReason reason, UcsMessage message) {
		
	}

	@Override
	public void onSendFileProgress(int progress) {
		
	}

	@Override
	public void onDownloadAttachedProgress(String msgId,final String filePaht, final int sizeProgrss, final int currentProgress) {
		if(msgid.equals(msgId) && currentProgress >= sizeProgrss){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = compressImageFromFile(filePaht);
					Drawable drawable = new BitmapDrawable(bitmap);	
					Message msg = new Message();
					msg.what=1;
					msg.obj=drawable;
					mHandler.sendMessage(msg);
				}
			});
		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					process.setText("��ǰ:"+currentProgress+"    �ܴ�С:"+sizeProgrss);
				}
			});
		}		
	}

	@Override
	public void onUserState(ArrayList arg0) {
		
	}
}
