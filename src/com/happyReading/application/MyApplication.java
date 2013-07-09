package com.happyReading.application;

import java.util.HashMap;
import java.util.Map;

import com.happyReading.download.NovelDownload;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {
	public static long lastReadOrderNum;
	public static Map<Integer,NovelDownload> loadMap = new HashMap<Integer, NovelDownload>();
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		lastReadOrderNum = sp.getLong("lastReadOrderNum", 0);
		
	}
	
}
