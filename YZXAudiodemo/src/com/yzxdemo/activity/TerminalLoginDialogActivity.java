package com.yzxdemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;

public class TerminalLoginDialogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int reson = getIntent().getIntExtra("reason", 0);
		new AlertDialog.Builder(this)
		.setTitle("��ʾ")
		.setMessage("�����˺��������ط���¼,�򱻷�����ǿ������:"+reson)
		.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						TerminalLoginDialogActivity.this.finish();
					}
				})
		.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					TerminalLoginDialogActivity.this.finish();
				}
				return false;
			}
		}).setCancelable(false).create().show();
		
	}

	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
