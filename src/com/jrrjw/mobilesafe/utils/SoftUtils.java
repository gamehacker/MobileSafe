package com.jrrjw.mobilesafe.utils;

import java.util.ArrayList;
import java.util.List;

import com.jrrjw.mobilesafe.entities.AppInfo;
import com.jrrjw.mobilesafe.entities.SoftInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;

public class SoftUtils {

	public static List<SoftInfo> getApplicationInfo(Context context)
			throws Exception {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> infos = manager.getInstalledPackages(0);
		List<SoftInfo> softs = new ArrayList<SoftInfo>();

		for (PackageInfo pi : infos) {
			SoftInfo info = new SoftInfo();

			String packageName = pi.packageName;
			String title = pi.applicationInfo.loadLabel(manager).toString();
			Drawable drawable = pi.applicationInfo.loadIcon(manager);
			int flag = pi.applicationInfo.flags;
			if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户应用
				info.setUserApp(true);
			} else {
				// 系统应用
				info.setUserApp(false);
			}
			if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// 在手机上
				info.setState(0);
			} else {
				// 在sd卡中
				info.setState(1);
			}

			info.setIcon(drawable);
			info.setPackageName(packageName);
			info.setTitle(title);
			softs.add(info);
		}

		return softs;

	}

	public static List<AppInfo> getAppInfo(Context context) throws Exception {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> infos = manager.getInstalledPackages(0);
		List<AppInfo> softs = new ArrayList<AppInfo>();

		for (PackageInfo pi : infos) {
			AppInfo info = new AppInfo();

			String packageName = pi.packageName;
			String title = pi.applicationInfo.loadLabel(manager).toString();
			Drawable drawable = pi.applicationInfo.loadIcon(manager);
			int flag = pi.applicationInfo.flags;
			if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户应用
				info.setUserApp(true);
			} else {
				// 系统应用
				info.setUserApp(false);
			}
			info.setState(false);
			info.setIcon(drawable);
			info.setPackageName(packageName);
			info.setTitle(title);
			softs.add(info);
		}

		return softs;

	}

	public static long getOwnMemory(Context context) {
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
				.getAbsolutePath());

		long size = statFs.getBlockSize();
		long avail = statFs.getAvailableBlocks();
		return size * avail;
	}

	public static long getSDMemory(Context context) {
		StatFs statFs = new StatFs(Environment.getDataDirectory()
				.getAbsolutePath());

		long size = statFs.getBlockSize();
		long avail = statFs.getAvailableBlocks();
		return size * avail;
	}

}
