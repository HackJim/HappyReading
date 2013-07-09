package com.happyReading;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends Activity implements OnClickListener{
	private TextView tv_dialog_title;
	private TextView tv_dialog_message;
	private Button sureButton;
	private Button cancelButton;
	private String novelName;
	private String novelMessage;
	public static final int SURE=1;
	public static final int CANCLE=0;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.dialog_show);
		intent = getIntent();
		novelName = getIntent().getStringExtra("novelName");
		novelMessage = "        "+getIntent().getStringExtra("novelMessage");
		init();
		sureButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}
	
	
	private void init() {
		tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
		tv_dialog_message = (TextView) findViewById(R.id.tv_dialog_message);
		sureButton = (Button) findViewById(R.id.sureButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		tv_dialog_title.setText(novelName);
		tv_dialog_message.setText(novelMessage);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sureButton:
			this.setResult(SURE,intent);
			finish();
			break;
		case R.id.cancelButton:
			this.setResult(CANCLE,intent);
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
