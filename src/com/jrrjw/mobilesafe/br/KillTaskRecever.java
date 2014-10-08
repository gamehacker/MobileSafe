package com.jrrjw.mobilesafe.br;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * ��������Ĺ㲥������
 * @author Jiang
 *
 */
public class KillTaskRecever extends BroadcastReceiver {

	ActivityManager am;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("KillTaskRecever���������......");
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
