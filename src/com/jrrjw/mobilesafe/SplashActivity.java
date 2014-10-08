package com.jrrjw.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������ҳ
 * @author Jiang
 * 
 */
public class SplashActivity extends Activity {
	private TextView tv_splash_version, tv_splash_progressbar = null;
	private String update_des = null;
	private String apkurl = null;
	private SharedPreferences sp = null;

	private static final int ENTER_HOME = 1;
	private static final int UPDATE = 2;

	protected static final int URLERROR = 3;

	protected static final int IOERROR = 4;

	protected static final int JSONERROR = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// Intent intent2 = new Intent(this, SmsService.class);
		// startService(intent2);
		// �����ļ���Ŀ¼��
		copyDbtoFile();
		installShortCut();
		// ���а汾�ŵ���ʾ��app�ĸ���
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_progressbar = (TextView) findViewById(R.id.tv_splash_progressbar);
		tv_splash_version.setText("�汾��" + getVersionInfo());
		sp = getSharedPreferences("config", 0);
		int checked = sp.getInt("update", 0);
		if (checked == 1) {
			// ���汾��Ϣ
			checkVersion();
		} else {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					enterHome();
				}
			}, 2000);
		}
	}

	private void installShortCut() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean shortCut = sp.getBoolean("shortcut", false);
		if (shortCut == true) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// �������ʽҪ����һ���������� ͼ�� ���� ������ʽ
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ���ȫ��ʿ");
		Intent intent2 = new Intent();
		// <action android:name="android.intent.action.MAIN" />
		// <category android:name="android.intent.category.LAUNCHER" />
		intent2.setAction("android.intent.action.MAIN");
		intent2.addCategory("android.intent.category.LAUNCHER");
		intent2.setClassName(getPackageName(),
				"com.jrrjw.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
		sendBroadcast(intent);
		Editor editor = sp.edit();
		editor.putBoolean("shortcut", true);
		editor.commit();

	}

	/**
	 * ��assert�ļ������ݿ⿽�������Ŀ¼��
	 */
	private void copyDbtoFile() {
		// TODO Auto-generated method stub
		try {
			File file = new File(getFilesDir(), "address.db");
			if (!file.exists()) {
				InputStream is = getAssets().open("address.db");
				FileOutputStream fos = new FileOutputStream(file);
				byte[] bs = new byte[1024];
				int len = -1;
				while ((len = is.read(bs)) != -1) {
					fos.write(bs);
				}
				fos.close();
				is.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:
				enterHome();
				break;
			case UPDATE:
				update_des = msg.getData().getString("des");
				apkurl = msg.getData().getString("apkurl");

				showDialogUpdate();
				break;
			case URLERROR:
				Toast.makeText(SplashActivity.this, "URL����", 0).show();
				enterHome();
				break;
			case JSONERROR:
				Toast.makeText(SplashActivity.this, "JSON����", 0).show();
				enterHome();
				break;
			case IOERROR:
				Toast.makeText(SplashActivity.this, "IO����", 0).show();
				enterHome();
				break;
			default:
				break;
			}

		};
	};

	/**
	 * ��ʾ�汾���¶Ի���
	 */
	public void showDialogUpdate() {
		AlertDialog.Builder dialog = new Builder(SplashActivity.this);
		dialog.setTitle("����и�����");
		dialog.setMessage(update_des);
		dialog.setNegativeButton("�´���˵", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});
		dialog.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					//ͨ������������ʵ��http�ļ�����
					FinalHttp conn = new FinalHttp();
					conn.download(apkurl, Environment
							.getDownloadCacheDirectory().getAbsolutePath()
							+ "aa.apk", new AjaxCallBack<File>() {
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							Toast.makeText(SplashActivity.this,
									"����ʧ��" + errorNo, 0).show();
							enterHome();
						};

						public void onSuccess(File t) {
							Toast.makeText(SplashActivity.this, "���سɹ�", 0)
									.show();
							enterHome();
						};

						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							int c = (int) (current * 100 / count);
							tv_splash_progressbar.setText("������" + c + "%");

						};

					});
				}
			}
		});
		dialog.show();

	}

	/**
	 * ���汾�����嵥�ļ��л�ð汾���������磬���汾��Ϣ
	 */
	private void checkVersion() {

		new Thread() {
			public void run() {
				// ���http����
				long start = System.currentTimeMillis();
				URL url = null;
				HttpURLConnection conn = null;
				Message msg = new Message();
				try {
					url = new URL(getString(R.string.updateurl));
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(4000);
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						// String response_json = InputStreamToString
						// .getString(is);
						String response_json = null;
						JSONObject json = new JSONObject(response_json);
						String version = (String) json.get("version");
						String des = (String) json.get("description");
						String apkurl = (String) json.get("apkurl");
						double version_new = Double.parseDouble(version);
						double version_old = Double
								.parseDouble(getVersionInfo());
						if (version_new > version_old) {
							msg.what = UPDATE;
							Bundle data = new Bundle();
							data.putString("des", des);
							data.putString("apkurl", apkurl);
							msg.setData(data);
						} else {
							msg.what = ENTER_HOME;
						}

					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					msg.what = URLERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					msg.what = IOERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what = JSONERROR;
					e.printStackTrace();
				} finally {
					long end = System.currentTimeMillis();
					if ((end - start) < 2000) {
						try {
							Thread.sleep(2000 - (end - start));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					handler.sendMessage(msg);
				}

			};
		}.start();

	}

	/*
	 * ������ҳ
	 */
	private void enterHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
		finish();
	}

	/**
	 * ��ð汾��Ϣ����ӵ���ҳ
	 * 
	 * @return
	 */
	public String getVersionInfo() {
		String versionName = null;
		try {
			// ����嵥�ļ��е���Ϣ
			versionName = getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

}
