package com.happyReading.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {
	/**
	 * �����µ�Activity���ҹرյ�ǰActivity
	 * @param activity ��ǰActivity
	 * @param cls Ҫת���Activity��class�ļ�
	 */
	public static void startActivityAndFinish(Activity activity,Class<?> cls){
		//���������򵼽���
		Intent intent = new Intent(activity,cls);
		activity.startActivity(intent);
		activity.finish();
	}
	/**
	 * �����µ�Activity
	 * @param activity ��ǰActivity
	 * @param cls Ҫת���Activity��class�ļ�
	 */
	public static void startActivity(Activity activity,Class<?> cls){
		//���������򵼽���
		Intent intent = new Intent(activity,cls);
		activity.startActivity(intent);
	}
}
