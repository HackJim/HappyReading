package com.happyReading.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	private static NetUtil netUtil = null;
	private static boolean iswifi = false;
	private static boolean isNetAvailable = false;
	/**
	 * ��������������Ϊwifi��ʱ�򷵻�true
	 * @return
	 */
	public  boolean isWifi(){
		return iswifi;
	}
	/**
	 * �ж��Ƿ��������������������ʱ�򷵻�true
	 * @return
	 */
	public  boolean isNetAvailable(){
		return isNetAvailable;
	}
	private NetUtil(){
		
	}
	public static NetUtil getInstance(Context context){
		if(netUtil==null){
			netUtil = new NetUtil();
		}
		checkNet(context);
		return netUtil;
	}
	/**
	 * �����������ķ���,ÿ�λ����������Ƕ����������м��
	 * @param context
	 */
	private static void checkNet(Context context){
		
		try {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mobNetInfoActivity = connectivityManager
					.getActiveNetworkInfo();
			if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
				isNetAvailable = false;
			} else {
				isNetAvailable = true;
				String type = mobNetInfoActivity.getTypeName();
				if("WIFI".equals(type))
					iswifi = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}
	
}