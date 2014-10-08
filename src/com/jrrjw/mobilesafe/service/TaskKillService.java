package com.jrrjw.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;

/*
 * �����������
 * @author Jiang
 *
 */
public class TaskKillService extends Service {

	MyTaskKillRecever killRecever;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// �����㲥�����ߣ����������㲥��Ȼ���������
		killRecever = new MyTaskKillRecever();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(killRecever, filter);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (killRecever != null) {
			unregisterReceiver(killRecever);
			killRecever = null;
		}

	}

	private class MyTaskKillRecever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// �������
			System.out.println("TaskKillService���������......");
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);

			List<RunningAppProcessInfo> appProcessInfos = activityManager
					.getRunningAppProcesses();
			for (RunningAppProcessInfo runningAppProcessInfo : appProcessInfos) {
				activityManager
						.killBackgroundProcesses(runningAppProcessInfo.processName);
			}

		}
	}

}
