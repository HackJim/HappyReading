package com.happyReading.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	private static NetUtil netUtil = null;
	private static boolean iswifi = false;
	private static boolean isNetAvailable = false;
	/**
	 * 如果有网络服务，且为wifi的时候返回true
	 * @return
	 */
	public  boolean isWifi(){
		return iswifi;
	}
	/**
	 * 判断是否有网络服务，有网络服务的时候返回true
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
	 * 用来检查网络的方法,每次获得这个单例是都会对网络进行检查
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