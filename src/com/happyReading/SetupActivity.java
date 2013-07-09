package com.happyReading;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

public class SetupActivity extends Activity implements OnClickListener {
	private SharedPreferences sp;
	private int[] topBgResource = { R.drawable.toolbar_bg,
			R.drawable.skin_blue, R.drawable.skin_purple, R.drawable.skin_red };
	// 1表示已经关闭，可以打开
	private int layout1Flag = 1;
	private int layout2Flag = 1;
	private TableLayout read_bg;
	private TableLayout read_style;
	private RelativeLayout layout1;
	private RelativeLayout layout2;
	private RelativeLayout layout3;
	private Button bt_bg1;
	private Button bt_bg2;
	private Button bt_bg3;
	private Button bt_bg4;
	private Button bt_bg5;
	private Button bt_bg6;
	private Button bt_bg7;
	private Button bt_bg8;
	private Button bt_style1;
	private Button bt_style2;
	private Button bt_style3;
	private Button bt_style4;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setup);
		super.onCreate(savedInstanceState);
		findView();
		init();
		setListener();
	}

	private void findView() {
		layout1 = (RelativeLayout) findViewById(R.id.layout1);
		layout2 = (RelativeLayout) findViewById(R.id.layout2);
		layout3 = (RelativeLayout) findViewById(R.id.layout3);
		bt_bg1 = (Button) findViewById(R.id.bt_bg1);
		bt_bg2 = (Button) findViewById(R.id.bt_bg2);
		bt_bg3 = (Button) findViewById(R.id.bt_bg3);
		bt_bg4 = (Button) findViewById(R.id.bt_bg4);
		bt_bg5 = (Button) findViewById(R.id.bt_bg5);
		bt_bg6 = (Button) findViewById(R.id.bt_bg6);
		bt_bg7 = (Button) findViewById(R.id.bt_bg7);
		bt_bg8 = (Button) findViewById(R.id.bt_bg8);
		bt_style1 = (Button) findViewById(R.id.bt_style1);
		bt_style2 = (Button) findViewById(R.id.bt_style2);
		bt_style3 = (Button) findViewById(R.id.bt_style3);
		bt_style4 = (Button) findViewById(R.id.bt_style4);
		read_bg = (TableLayout) findViewById(R.id.read_bg);
		read_style = (TableLayout) findViewById(R.id.read_style);

	}

	private void init() {
		HomeTabActivity.top_title_name.setText("设置");
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[top_bg_style]);
		editor= sp.edit();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[top_bg_style]);
		HomeTabActivity.group.setBackgroundResource(topBgResource[top_bg_style]);
		HomeTabActivity.top_title_name.setText("设置");
		editor= sp.edit();
	}

	private void setListener() {
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
		bt_bg1.setOnClickListener(this);
		bt_bg2.setOnClickListener(this);
		bt_bg3.setOnClickListener(this);
		bt_bg4.setOnClickListener(this);
		bt_bg5.setOnClickListener(this);
		bt_bg6.setOnClickListener(this);
		bt_bg7.setOnClickListener(this);
		bt_bg8.setOnClickListener(this);
		bt_style1.setOnClickListener(this);
		bt_style2.setOnClickListener(this);
		bt_style3.setOnClickListener(this);
		bt_style4.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout1:
			if (layout1Flag == 1) {
				read_bg.setVisibility(View.VISIBLE);

				layout1Flag = 2;
			} else {
				read_bg.setVisibility(View.GONE);
				layout1Flag = 1;
			}
			break;
		case R.id.layout2:
			if (layout2Flag == 1) {
				read_style.setVisibility(View.VISIBLE);

				layout2Flag = 2;
			} else {
				read_style.setVisibility(View.GONE);
				layout2Flag = 1;
			}
			break;
		case R.id.layout3:
			//TODO 检查更新
			Toast.makeText(SetupActivity.this, "尚无无广告版本", 0).show();
			break;
		case R.id.bt_bg1:
			
			editor.putInt("bg_style", 0);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_bg2:
			editor.putInt("bg_style", 1);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_bg3:
			editor.putInt("bg_style", 2);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_bg4:
			editor.putInt("bg_style", 3);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_bg5:
			editor.putInt("bg_style", 4);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_bg6:
			editor.putInt("bg_style", 5);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_bg7:
			editor.putInt("bg_style", 6);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_bg8:
			editor.putInt("bg_style", 7);
			editor.commit();
			read_bg.setVisibility(View.GONE);
			layout1Flag = 1;
			break;
		case R.id.bt_style1:
			editor.putInt("top_bg", 0);
			editor.commit();
			read_style.setVisibility(View.GONE);
			layout2Flag = 1;
			onResume();
			
			break;
		case R.id.bt_style2:
			editor.putInt("top_bg", 1);
			editor.commit();
			read_style.setVisibility(View.GONE);
			layout2Flag = 1;
			onResume();
			break;
		case R.id.bt_style3:
			editor.putInt("top_bg", 2);
			editor.commit();
			read_style.setVisibility(View.GONE);
			layout2Flag = 1;
			onResume();
			break;
		case R.id.bt_style4:
			editor.putInt("top_bg", 3);
			editor.commit();
			read_style.setVisibility(View.GONE);
			layout2Flag = 1;
			onResume();
			break;
		default:
			break;
		}
	}
}
