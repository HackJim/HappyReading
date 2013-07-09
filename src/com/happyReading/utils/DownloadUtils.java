package com.happyReading.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.ProgressDialog;

/**
 * 下载工具类
 * 
 * @author pc
 * 
 */
public class DownloadUtils {
	/**
	 * 下载文件的操作
	 * 
	 * @param serverPath
	 *            服务器上文件的路径
	 * @param savePath
	 *            保存在本地的路径
	 * @param pd
	 *            下载进度的对话框
	 * @return 下载完成返回文件对象，下载失败返回null
	 */
	public static File downloadFile(String serverPath, String savedPath,
			ProgressDialog pd) {
		try {
			URL url = new URL(serverPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				int max = conn.getContentLength();;
				pd.setMax(max);
				File file = new File(savedPath);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				int total = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					total+=len;
					pd.setProgress(total);
				}
				fos.flush();
				fos.close();
				return file;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 获取服务器上APK的名称
	 * @param serverPath 服务器路径
	 * @return APK的名称
	 */
	public static String getFileName(String serverPath)
	{
		return serverPath.substring(serverPath.lastIndexOf("/")+1);
	}
}
