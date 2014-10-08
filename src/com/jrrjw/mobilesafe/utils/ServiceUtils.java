package com.jrrjw.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.widget.Toast;

public class ServiceUtils {

	public static boolean isServicRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> infos = am.getRunningServices(100);

		for (RunningServiceInfo runningServiceInfo : infos) {
			String serviceN = runningServiceInfo.service.getClassName();
			if (serviceName.equals(serviceN)) {
				return true;
			}
		}

		return false;

	}

}
