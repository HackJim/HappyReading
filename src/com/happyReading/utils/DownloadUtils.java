package com.happyReading.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.ProgressDialog;

/**
 * ���ع�����
 * 
 * @author pc
 * 
 */
public class DownloadUtils {
	/**
	 * �����ļ��Ĳ���
	 * 
	 * @param serverPath
	 *            ���������ļ���·��
	 * @param savePath
	 *            �����ڱ��ص�·��
	 * @param pd
	 *            ���ؽ��ȵĶԻ���
	 * @return ������ɷ����ļ���������ʧ�ܷ���null
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
	 * ��ȡ��������APK������
	 * @param serverPath ������·��
	 * @return APK������
	 */
	public static String getFileName(String serverPath)
	{
		return serverPath.substring(serverPath.lastIndexOf("/")+1);
	}
}
