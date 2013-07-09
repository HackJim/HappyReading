package com.happyReading;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.youmi.android.AdManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.happyReading.bean.UpdateInfo;
import com.happyReading.domain.Shelf;
import com.happyReading.engine.UpdateInfoProvider;
import com.happyReading.net.NetUtil;
import com.happyReading.utils.ActivityUtils;
import com.happyReading.utils.DownloadUtils;
import com.happyReading.utils.FileCopyUtils;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends Activity {
	private static final int COPY_DB_ERROR = 30;
	private static final int COPY_DB_SUCCESS = 31;
	private static final int COPY_DB_ALREADY_HAVE = 32;
	public static final int PARSE_SUCCESS = 10;
	public static final int PARSE_ERROR = 11;
	public static final int SERVER_ERROR = 12;
	public static final int URL_ERROR = 13;
	public static final int URL_CANT_FIND = 14;
	public static final int NETWORK_ERROR = 15;
	private static final int DOWNLOAD_SUCCESS = 16;
	private static final int DOWNLOAD_ERROR = 17;
	private static final int UPDATA = 20;
	private UpdateInfo updateInfo;
	private ProgressDialog pd;// ���ؽ��ȵĶԻ���
	SharedPreferences sp;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case COPY_DB_ERROR:
				// ���ݿ⿽��ʧ��
				Toast.makeText(SplashActivity.this, "���ݿ⿽��ʧ��", 0).show();
				break;
			case COPY_DB_SUCCESS:
				Toast.makeText(SplashActivity.this, "���ݿ⿽���ɹ�", 0).show();
				showTips();
				break;
			case COPY_DB_ALREADY_HAVE:
				Toast.makeText(SplashActivity.this, "���ݿ��Ѿ�����", 0).show();
				showTips();
				break;
			case PARSE_SUCCESS:
				// �жϱ��صİ汾�źͷ������汾���Ƿ���ͬ
				if (getVersion().equals(updateInfo.getVersion())) {
					// �汾����ͬ��������
					createDir();
					Shelf.getInstance();
					showHomeTabActivity();
				} else {
					showUpdateDialog();
				}

				break;
			case PARSE_ERROR:
				Toast.makeText(getApplicationContext(), "����xmlʧ��", 0).show();
				createDir();
				Shelf.getInstance();
				showHomeTabActivity();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "����������", 0).show();
				createDir();
				Shelf.getInstance();
				showHomeTabActivity();
				break;
			case URL_ERROR:
				Toast.makeText(getApplicationContext(), "url����", 0).show();
				createDir();
				Shelf.getInstance();
				showHomeTabActivity();
				break;
			case URL_CANT_FIND:
				Toast.makeText(getApplicationContext(), "urlû�б��ҵ�", 0).show();
				createDir();
				Shelf.getInstance();
				showHomeTabActivity();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "�������Ӵ���", 0).show();
				createDir();
				Shelf.getInstance();
				showHomeTabActivity();
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "����APK����", 0).show();
				createDir();
				Shelf.getInstance();
				showHomeTabActivity();
				break;
			case DOWNLOAD_SUCCESS:
				Toast.makeText(getApplicationContext(), "��װ�µ�APK", 0).show();
				File file = (File) msg.obj;
				installApk(file);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		AdManager.getInstance(SplashActivity.this).init(
				"0e86b1cc6faaae0d", "c01de7a3a75e8b7a", false);
		copyDB();
		super.onCreate(savedInstanceState);
	}

	protected void installApk(File file) {
		// TODO ��װ���غõ���APK��
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.fromFile(file));
		intent.setType("application/vnd.android.package-archive");
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	protected void showUpdateDialog() {
		// TODO �����Ƿ�װ���°��Ľ��棬���û�ѡ��
		String message = "�Ѽ�⵽���³����Ƿ����Ӧ�ó���";
		String name = "���³���";
		Intent intent = new Intent(SplashActivity.this, Dialog2Activity.class);
		intent.putExtra("novelName", name);
		intent.putExtra("novelMessage", message);
		intent.putExtra("style", 1);
		startActivityForResult(intent, UPDATA);
	}

	// �����ݿ��ļ�������Ӧ�õ�files�ļ�����
	private void copyDB() {
		FileCopyUtils.copyFileFromAssetsToAppFiles(this, "happyReading.db",
				handler);
	}

	private void showHomeTabActivity() {
		ActivityUtils.startActivityAndFinish(this, HomeTabActivity.class);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == UPDATA) {
			if (resultCode == Dialog2Activity.BUTTON1) {
				// ���ظ��º��APK
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// �����µ�APK���ֻ�
					pd = new ProgressDialog(SplashActivity.this);
					pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					pd.setTitle("���ѣ�");
					pd.setMessage("�������ذ�װ��");
					pd.show();
					new Thread() {
						public void run() {
							File file = new File(
									Environment.getExternalStorageDirectory(),
									DownloadUtils.getFileName(updateInfo
											.getPath()));
							File downloadFile = DownloadUtils.downloadFile(
									updateInfo.getPath(),
									file.getAbsolutePath(), pd);
							Message msg = new Message();
							if (downloadFile != null) {
								// ���سɹ�
								msg.what = DOWNLOAD_SUCCESS;
								msg.obj = downloadFile;
							} else {
								// ����ʧ��
								msg.what = DOWNLOAD_ERROR;
							}
							pd.dismiss();
							handler.sendMessage(msg);
						}
					}.start();
				} else {
					Toast.makeText(getApplicationContext(), "SD��������", 0).show();
				}
			}
			if (resultCode == Dialog2Activity.BUTTON2) {
				createDir();
				Shelf.getInstance();
				showHomeTabActivity();
			}
		}
	}

	// ��splashActivity����ʾlogo��
	private void showTips() {
		//�ж��Ƿ��а汾����һ��һ��
		sp = getSharedPreferences("config", MODE_PRIVATE);
		long lastUpdateTime = sp.getLong("lastXMLUpdateTime", 0);
		long currentTimeMillis = System.currentTimeMillis();
		if (NetUtil.getInstance(SplashActivity.this).isNetAvailable()&&(currentTimeMillis-lastUpdateTime)>24 * 60 * 60 * 1000) {
			// �����������½��и��³���
			updataApk();
		} else {
			AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
			aa.setDuration(3000);
			ImageView img = (ImageView) findViewById(R.id.splash_screen);
			img.startAnimation(aa);
			aa.setAnimationListener(new AnimationListener() {

				/**
				 * ������ʼʱ
				 */
				@Override
				public void onAnimationStart(Animation animation) {
				}

				/**
				 * �ظ�����ʱ
				 */
				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				/**
				 * ��������ʱ
				 */
				@Override
				public void onAnimationEnd(Animation animation) {
					// ������̼���
					createDir();
					Shelf.getInstance();
					showHomeTabActivity();
				}
			});
		}
	}

	protected void updataApk() {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		final Message msg = new Message();
		try {
			URL url = new URL(getResources().getString(R.string.updateurl));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// �������ӷ���
			conn.setRequestMethod("GET");
			// �������ӳ�ʱʱ��
			conn.setConnectTimeout(3000);
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				updateInfo = UpdateInfoProvider.getUpdateInfo(is);
				if (updateInfo != null) {
					// �����ɹ�
					msg.what = PARSE_SUCCESS;
				} else {
					// ����ʧ��
					msg.what = PARSE_ERROR;
				}
			} else {
				msg.what = SERVER_ERROR;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = URL_ERROR;
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = URL_CANT_FIND;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = NETWORK_ERROR;
		}
		long endTime = System.currentTimeMillis();
		Editor editor = sp.edit();
		editor.putLong("lastXMLUpdateTime", endTime);
		editor.commit();
		long dTime = endTime - startTime;
		if (dTime < 3000) {
			AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
			aa.setDuration(3000 - dTime);
			ImageView img = (ImageView) findViewById(R.id.splash_screen);
			img.startAnimation(aa);
			aa.setAnimationListener(new AnimationListener() {

				/**
				 * ������ʼʱ
				 */
				@Override
				public void onAnimationStart(Animation animation) {
				}

				/**
				 * �ظ�����ʱ
				 */
				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				/**
				 * ��������ʱ
				 */
				@Override
				public void onAnimationEnd(Animation animation) {
					// ������̼���
					handler.sendMessage(msg);
					return;
				}
			});
		} else {
			handler.sendMessage(msg);
		}
	}

	// �����ļ���
	private void createDir() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/happyReading";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public void test(View view) {
		Intent intent = new Intent(this, HomeTabActivity.class);
		startActivity(intent);
	}

	/**
	 * ��ȡ��ǰӦ�ó���İ汾��
	 * 
	 * @return ��ǰ����汾��
	 */
	private String getVersion() {
		// �õ�ϵͳ�İ�������
		PackageManager pm = getPackageManager();
		// ��ȡ��Ӧ��������ذ���Ϣ
		// pm.getPackageInfo("my.safe.mobilesafe", 0);
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			return packinfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// ���������ܷ�����can't reach
			return "";
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
