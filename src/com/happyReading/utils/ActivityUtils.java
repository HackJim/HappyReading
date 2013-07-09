package com.happyReading.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {
	/**
	 * 开启新的Activity并且关闭当前Activity
	 * @param activity 当前Activity
	 * @param cls 要转向的Activity的class文件
	 */
	public static void startActivityAndFinish(Activity activity,Class<?> cls){
		//定向到设置向导界面
		Intent intent = new Intent(activity,cls);
		activity.startActivity(intent);
		activity.finish();
	}
	/**
	 * 开启新的Activity
	 * @param activity 当前Activity
	 * @param cls 要转向的Activity的class文件
	 */
	public static void startActivity(Activity activity,Class<?> cls){
		//定向到设置向导界面
		Intent intent = new Intent(activity,cls);
		activity.startActivity(intent);
	}
}
