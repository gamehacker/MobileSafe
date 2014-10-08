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
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 缓存清理标签页
 * @author Jiang
 *
 */
public class CleanCacheActivity extends Activity {
	protected static final int SCANING = 1;
	public static final int SHOW_CACHE_INFO = 2;
	protected static final int SCAN_FINISH = 3;
	private ProgressBar progressBar1;
	private LinearLayout ll_container;
	private TextView tv_status;
	private PackageManager pm;
	//处理Message
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:  //正在扫描
				String text = (String) msg.obj;
				tv_status.setText("正在扫描：" + text);
				break;
			case SHOW_CACHE_INFO:  //显示缓存信息
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
				//为删除按钮设置事件
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
				tv_status.setText("扫描完毕");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		Button btn_finish = (Button) findViewById(R.id.btn_finish);
		btn_finish.setVisibility(View.GONE);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		tv_status = (TextView) findViewById(R.id.tv_status);

		//实现获得app的大小
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
						msg.what = SCANING;
						msg.obj = packinfo.applicationInfo.loadLabel(pm)
								.toString();
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
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
			};
		}.start();

	}

	/**
	 * 观察者模式，调用远程的观察者
	 * @author Jiang
	 *
	 */
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
	/**
	 * 全部清理缓存原理
	 * 像系统申请大量空间，这是操作系统发现空间不够，就会自定清理缓存释放空间
	 * 
	 * 单个缓存清理系统未提供api只能手动清理，当前未实现
	 * 
	 * 这里通过反射原理
	 */
	public void cleanAll(View view) {
		// /freeStorageAndNotify
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
