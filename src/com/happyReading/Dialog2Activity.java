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

public class Dialog2Activity extends Activity implements OnClickListener{
	private TextView tv_dialog_title;
	private TextView tv_dialog_message;
	private Button button1;
	private Button button2;
	private String novelName;
	private String novelMessage;
	public static final int BUTTON1=1;
	public static final int BUTTON2=2;
	private Intent intent;
	private int style;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.dialog2_show);
		intent = getIntent();
		novelName = getIntent().getStringExtra("novelName");
		style = getIntent().getIntExtra("style", 1);
		novelMessage = "        "+getIntent().getStringExtra("novelMessage");
		init();
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
	}
	
	
	private void init() {
		tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
		tv_dialog_message = (TextView) findViewById(R.id.tv_dialog_message);
		button1 = (Button) findViewById(R.id.Dialog2_Button1);
		button2 = (Button) findViewById(R.id.Dialog2_Button2);
		tv_dialog_title.setText(novelName);
		tv_dialog_message.setText(novelMessage);
		if(style==1){
			button1.setText("确认");
			button2.setText("取消");
		}else{
			button1.setText("在线阅读");
			button2.setText("移除书架");
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Dialog2_Button1:
			this.setResult(BUTTON1,intent);
			finish();
			break;
		case R.id.Dialog2_Button2:
			this.setResult(BUTTON2,intent);
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
