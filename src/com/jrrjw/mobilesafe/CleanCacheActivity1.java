package com.jrrjw.mobilesafe;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity1 extends Activity {
	protected static final int SCANING = 1;
	public static final int SHOW_CACHE_INFO = 2;
	protected static final int SCAN_FINISH = 3;
	private static final int SCAN_FINISH1 = 4;
	private ProgressBar progressBar1;
	private LinearLayout ll_container;
	private TextView tv_status;
	private PackageManager pm;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				String text = (String) msg.obj;
				tv_status.setText("正在扫描：" + text);
				break;
			case SHOW_CACHE_INFO:
				View view = View.inflate(getApplicationContext(),
						R.layout.list_appcache_item, null);
				ImageView iv = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_cache = (TextView) view.findViewById(R.id.tv_cache);
				final CacheInfo info = (CacheInfo) msg.obj;
				iv.setImageDrawable(info.icon);
				tv_name.setText(info.name);
				tv_cache.setText("缓存大小："
						+ Formatter.formatFileSize(getApplicationContext(),
								info.size));
				ImageView iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				iv_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Method[] methods = PackageManager.class.getMethods();
						for (Method method : methods) {
							try {
								if ("deleteApplicationCacheFiles".equals(method
										.getName())) {
									method.invoke(pm, info.packname,
											new IPackageDataObserver.Stub() {
												@Override
												public void onRemoveCompleted(
														String packageName,
														boolean succeeded)
														throws RemoteException {

												}
											});
								}
							} catch (Exception e) {
								Intent intent = new Intent();
								intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								intent.setData(Uri.parse("package:"
										+ info.packname));
								startActivity(intent);
								e.printStackTrace();
							}
						}
					}
				});
				ll_container.addView(view, 0);
				break;
			case SCAN_FINISH:
				tv_status.setText("扫描完毕,正在清理");
				break;
			case SCAN_FINISH1:
				tv_status.setText("清理完成");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		Button btn_finish = (Button) findViewById(R.id.btn_finish);
		Button btn_clean = (Button) findViewById(R.id.btn_clean);
		tv_status = (TextView) findViewById(R.id.tv_status);
		btn_clean.setVisibility(View.GONE);
		btn_finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("清理完成".equals(tv_status.getText().toString().trim()) == false) {
					finish();
					return;
				}

				String score = "100分";
				String btn_value = "清理完成";
				Intent intent = new Intent(CleanCacheActivity1.this,
						HomeActivity.class);

				intent.putExtra("score", score);
				intent.putExtra("value", btn_value);
				setResult(0, intent);
				finish();
			}
		});

		new Thread() {
			public void run() {
				pm = getPackageManager();
				List<PackageInfo> packageInfos = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				progressBar1.setMax(packageInfos.size());
				int total = 0;
				for (PackageInfo packinfo : packageInfos) {
					try {
						String packname = packinfo.packageName;
						Method method = PackageManager.class.getMethod(
								"getPackageSizeInfo", String.class,
								IPackageStatsObserver.class);
						method.invoke(pm, packname, new MyObserver());
						Message msg = Message.obtain();
						// msg.obj = packinfo.applicationInfo.name;
						msg.what = SCANING;
						System.out.println(packinfo.applicationInfo.name
								+ "sao miao");
						msg.obj = packinfo.applicationInfo.loadLabel(pm)
								.toString();
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("抛异常了");
					}
					total++;
					progressBar1.setProgress(total);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
				cleanAll();
				SystemClock.sleep(2000);
				Message msg1 = Message.obtain();
				msg1.what = SCAN_FINISH1;
				handler.sendMessage(msg1);
			};
		}.start();

	}

	private class MyObserver extends IPackageStatsObserver.Stub {
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cache = pStats.cacheSize;
			long codeSize = pStats.codeSize;
			if (cache > 0) {
				try {
					Message msg = Message.obtain();
					msg.what = SHOW_CACHE_INFO;
					CacheInfo cacheInfo = new CacheInfo();
					cacheInfo.packname = pStats.packageName;
					cacheInfo.icon = pm.getApplicationInfo(pStats.packageName,
							0).loadIcon(pm);
					cacheInfo.name = pm
							.getApplicationInfo(pStats.packageName, 0)
							.loadLabel(pm).toString();
					cacheInfo.size = cache;
					msg.obj = cacheInfo;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CacheInfo {
		Drawable icon;
		String name;
		long size;
		String packname;
	}

	public void cleanAll() {

		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE,
							new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									System.out.println(succeeded);
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}


}
