package com.happyReading;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.happyReading.application.MyApplication;
import com.umeng.analytics.MobclickAgent;

public class HomeTabActivity extends TabActivity {
	public static RadioGroup group;
	public static TextView top_title_name;
	private RadioButton radio_button_shelf;
	private SharedPreferences sp;
	private int[] topBgResource = { R.drawable.toolbar_bg,
			R.drawable.skin_blue, R.drawable.skin_purple, R.drawable.skin_red };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_tab);
		top_title_name = (TextView) findViewById(R.id.top_title_name);
		group = (RadioGroup) findViewById(R.id.radio_group);
		radio_button_shelf = (RadioButton) findViewById(R.id.radio_button_shelf);
		final TabHost tabHost = getTabHost();
		TabSpec tab1 = tabHost.newTabSpec("书架");
		TabSpec tab2 = tabHost.newTabSpec("搜书");
		TabSpec tab3 = tabHost.newTabSpec("下载");
		TabSpec tab4 = tabHost.newTabSpec("设置");
		tab1.setIndicator("");
		tab2.setIndicator("");
		tab3.setIndicator("");
		tab4.setIndicator("");
		tab1.setContent(new Intent(this, ShelfActivity.class));
		tab2.setContent(new Intent(this, SearchActivity.class));
		tab3.setContent(new Intent(this, DownloadActivity.class));
		tab4.setContent(new Intent(this, SetupActivity.class));
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);
		tabHost.addTab(tab4);
//		// 实例化广告条
//		AdView adView = new AdView(this, AdSize.SIZE_320x50);
//		// 获取要广告条的布局
//		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
//		// 将广告条加入到布局中
//		adLayout.addView(adView);
//		adView.setAdListener(new AdViewLinstener() {
//			@Override
//			public void onSwitchedAd(AdView adView) {
//				// 切换广告并展示
//				top_title_name.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onReceivedAd(AdView adView) {
//				// 请求广告成功
//			}
//
//			@Override
//			public void onFailedToReceivedAd(AdView adView) {
//				// 请求广告失败
//			}
//		});
		radio_button_shelf.setChecked(true);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_button_shelf:
					tabHost.setCurrentTab(0);
					break;
				case R.id.radio_button_search:
					tabHost.setCurrentTab(1);
					break;
				case R.id.radio_button_download:
					tabHost.setCurrentTab(2);
					break;
				case R.id.radio_button_setup:
					tabHost.setCurrentTab(3);
					break;

				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		group.setBackgroundResource(topBgResource[top_bg_style]);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putLong("lastReadOrderNum", MyApplication.lastReadOrderNum);
		editor.commit();
		finish();
	}

}
