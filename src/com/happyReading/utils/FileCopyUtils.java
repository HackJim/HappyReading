package com.happyReading.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class FileCopyUtils {
	private static final int FILE_COPY_ERROR = 30;
	private static final int FILE_COPY_SUCCESS = 31;
	private static final int FILE_ALREADY_HAVE = 32;

	/**
	 * �����ļ�
	 * 
	 * @param is
	 *            Դ�ļ���������
	 * @param path
	 *            �������ļ���·��
	 * @return ���ؿմ�����ʧ��
	 */
	public static File copyFile(InputStream is, String path) {
		try {
			File file = new File(path);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ��assets�п����ļ���Ӧ�����ֻ���filesĿ¼�£����Ʊ���һ��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param fileName
	 *            �ļ�����
	 * @param handler
	 *            ������Ϣ��handler
	 */
	public static void copyFileFromAssetsToAppFiles(final Context context,
			final String fileName, final Handler handler) {
		new Thread() {
			public void run() {
				File destFile = new File(context.getFilesDir(), fileName);
				Message msg = Message.obtain();
				if (destFile.exists() && destFile.length() > 0) {
					msg = Message.obtain();
					// �ļ��Ѿ�����
					msg.what = FILE_ALREADY_HAVE;
					handler.sendMessage(msg);
					return;
				}
				try {
					InputStream is = context.getAssets().open(fileName);
					File file = FileCopyUtils.copyFile(is,
							destFile.getAbsolutePath());
					if (file == null) {
						// ����ʧ��
						msg.what = FILE_COPY_ERROR;
					} else {
						// �����ɹ�
						msg.what = FILE_COPY_SUCCESS;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					msg.what = FILE_COPY_ERROR;
					e.printStackTrace();
					// ����ʧ��
				} finally {
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * ��Assets�µ��ļ�ȫ�����Ƶ�sd��
	 * 
	 * @param context
	 *            �����Ķ���
	 */
	public static void assetsCopyAll(final Context context) {
		new Thread() {
			public void run() {
				CopyAssets(context, "", Environment
						.getExternalStorageDirectory().getAbsolutePath() + "/");
			}
		}.start();
	}

	/**
	 * ��Assets�µ�ĳ���ļ���ȫ�����Ƶ�sd��
	 * 
	 * @param name
	 *            Assets���ļ��е�����
	 */
	public static void assetsCopyByName(final Context context, final String name) {
		new Thread() {
			public void run() {
				CopyAssets(context, name, Environment
						.getExternalStorageDirectory().getAbsolutePath() + "/");
			}
		}.start();
	}
	/**
	 * ������Assets���ļ��п�����SD���ϵİ�������
	 * @param context
	 * @param assetDir
	 * @param dir
	 */
	private static void CopyAssets(Context context, String assetDir, String dir) {
		String[] files;
		try {
			files = context.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath.mkdirs()) {

			}
		}
		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(context, fileName, dir + fileName + "/");
					} else {
						CopyAssets(context, assetDir + "/" + fileName, dir
								+ fileName + "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists())
					outFile.delete();
				InputStream in = null;
				if (0 != assetDir.length())
					in = context.getAssets().open(assetDir + "/" + fileName);
				else
					in = context.getAssets().open(fileName);
				copyFile(in, outFile.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
