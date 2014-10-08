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
 * 程序首页
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
		// 拷贝文件到目录下
		copyDbtoFile();
		installShortCut();
		// 进行版本号的显示和app的更新
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_progressbar = (TextView) findViewById(R.id.tv_splash_progressbar);
		tv_splash_version.setText("版本：" + getVersionInfo());
		sp = getSharedPreferences("config", 0);
		int checked = sp.getInt("update", 0);
		if (checked == 1) {
			// 检查版本信息
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
		// 会计诶方式要包含一下三个内容 图标 标题 启动方式
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机安全卫士");
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
	 * 把assert文件夹数据库拷贝到相关目录下
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
				Toast.makeText(SplashActivity.this, "URL错误", 0).show();
				enterHome();
				break;
			case JSONERROR:
				Toast.makeText(SplashActivity.this, "JSON错误", 0).show();
				enterHome();
				break;
			case IOERROR:
				Toast.makeText(SplashActivity.this, "IO错误", 0).show();
				enterHome();
				break;
			default:
				break;
			}

		};
	};

	/**
	 * 显示版本更新对话框
	 */
	public void showDialogUpdate() {
		AlertDialog.Builder dialog = new Builder(SplashActivity.this);
		dialog.setTitle("软件有更新啦");
		dialog.setMessage(update_des);
		dialog.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});
		dialog.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					//通过第三方工具实现http文件下载
					FinalHttp conn = new FinalHttp();
					conn.download(apkurl, Environment
							.getDownloadCacheDirectory().getAbsolutePath()
							+ "aa.apk", new AjaxCallBack<File>() {
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							Toast.makeText(SplashActivity.this,
									"下载失败" + errorNo, 0).show();
							enterHome();
						};

						public void onSuccess(File t) {
							Toast.makeText(SplashActivity.this, "下载成功", 0)
									.show();
							enterHome();
						};

						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							int c = (int) (current * 100 / count);
							tv_splash_progressbar.setText("下载了" + c + "%");

						};

					});
				}
			}
		});
		dialog.show();

	}

	/**
	 * 检查版本，在清单文件中获得版本，链接网络，检查版本信息
	 */
	private void checkVersion() {

		new Thread() {
			public void run() {
				// 获得http链接
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
	 * 进入主页
	 */
	private void enterHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
		finish();
	}

	/**
	 * 获得版本信息，添加到主页
	 * 
	 * @return
	 */
	public String getVersionInfo() {
		String versionName = null;
		try {
			// 获得清单文件中的信息
			versionName = getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

}
