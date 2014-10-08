package com.jrrjw.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import com.jrrjw.mobilesafe.LockAppWPasswdActivity;
import com.jrrjw.mobilesafe.dao.AppLockInfo;
import com.jrrjw.mobilesafe.dao.AppLockSqliteUtils;

/**
 * ³ÌÐòËø·þÎñ
 * @author jiang
 *
 */
public class LockAppService extends Service {

	ActivityManager am;
	private boolean flag;
	List<AppLockInfo> appLockInfos;
	AppLockSqliteUtils sqliteUtils;
	String removedPackageName = null;
	MyBroadCast broadCast;
	MyRecever recever;
	DataBaseBroadCastRecever dataBaseBroadCastRecever;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dataBaseBroadCastRecever = new DataBaseBroadCastRecever();
		registerReceiver(dataBaseBroadCastRecever, new IntentFilter(
				"com.jrrjw.mobilesafe.databasechange"));
		recever = new MyRecever();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(recever, filter);

		broadCast = new MyBroadCast();
		registerReceiver(broadCast, new IntentFilter(
				"com.jrrjw.mobilesafe.removeApp"));

		sqliteUtils = new AppLockSqliteUtils(this);
		appLockInfos = sqliteUtils.getAll(LockAppService.this);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		flag = true;
		new Thread(new Runnable() {
			public void run() {

				while (flag) {
					List<RunningTaskInfo> runningTaskInfos = am
							.getRunningTasks(1);
					ComponentName name = runningTaskInfos.get(0).topActivity;
					String packageName = name.getPackageName();
					for (AppLockInfo a : appLockInfos) {
						if (a.getPackageName().equals(packageName)) {
							if (removedPackageName == null
									|| removedPackageName.equals(packageName) == false) {
								Intent intent = new Intent(LockAppService.this,
										LockAppWPasswdActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("packageName", packageName);
								intent.putExtra("name", a.getName());
								System.out.println("contains....."
										+ removedPackageName);
								startActivity(intent);
							} else {
								System.out.println("not contain"
										+ removedPackageName);
							}

						}

					}
					SystemClock.sleep(100);
				}

			}
		}).start();

	}

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			removedPackageName = (String) intent.getExtras().get("packageName");
		}

	}

	private class DataBaseBroadCastRecever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			appLockInfos = sqliteUtils.getAll(LockAppService.this);
		}

	}

	private class MyRecever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			removedPackageName = null;
		}

	}

	public void onDestroy() {
		flag = false;
		removedPackageName = null;
		unregisterReceiver(broadCast);
		broadCast = null;
	};
}
