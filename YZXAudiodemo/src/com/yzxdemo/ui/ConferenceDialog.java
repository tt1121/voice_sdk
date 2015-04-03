package com.yzxdemo.ui;

import com.yzxdemo.R;
import com.yzxdemo.activity.ConferenceActivity;
import com.yzxdemo.tools.DataTools;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class ConferenceDialog extends Dialog {
	
	private Context mcontext;
	private EditText dialog_input;
	public ConferenceDialog(Context context) {
		super(context);
		mcontext=context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_conference);
		setCanceledOnTouchOutside(false);
		dialog_input = (EditText) findViewById(R.id.dialog_input);
		findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				cancel();
			}
		});
		
		findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(DataTools.checkMobilePhoneNumber(dialog_input.getText().toString().trim())){
					Intent intent = new Intent(ConferenceActivity.ACTION_ADDPERSON);
					intent.putExtra("phone", dialog_input.getText().toString().trim());
					mcontext.sendBroadcast(intent);
				}
				cancel();
			}
		});
	}
}
