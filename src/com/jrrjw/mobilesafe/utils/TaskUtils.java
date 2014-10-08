package com.jrrjw.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.text.format.Formatter;

import com.jrrjw.mobilesafe.R;
import com.jrrjw.mobilesafe.entities.TaskInfo;

public class TaskUtils {

	public static List<TaskInfo> getTaskInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();
			String packname = processInfo.processName;
			taskInfo.setPackname(packname);
			MemoryInfo[] momoryInfos = am
					.getProcessMemoryInfo(new int[] { processInfo.pid });
			long memsize = momoryInfos[0].getTotalPrivateDirty() * 1024l;// 得到某个进程总的内存大小
			taskInfo.setMemsize(memsize);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packname, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// 用户进程
					taskInfo.setUserTask(true);
				} else {// 系统进程
					taskInfo.setUserTask(false);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(
						R.drawable.a));
				taskInfo.setName(packname);
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}

	public static long getRunningTaskCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		int count = am.getRunningAppProcesses().size();
		return count;
	}

	public static long getAllMemory(Context context) {
		StringBuffer allMemoryBuffer;
		try {
			File file = new File("/proc/meminfo");
			InputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String allMemory = reader.readLine();
			allMemoryBuffer = new StringBuffer();
			for (char c : allMemory.toCharArray()) {
				if (c >= '0' && c <= '9') {
					allMemoryBuffer.append(c);
				}
			}
			return (Long.parseLong(allMemoryBuffer.toString()) * 1024);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static long getLeftMemory(Context context) {
		try {
			File file = new File("/proc/meminfo");
			InputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			reader.readLine();
			String freeMemory = reader.readLine();
			StringBuffer freeMemoryBuffer = new StringBuffer();
			for (char c : freeMemory.toCharArray()) {
				if (c >= '0' && c <= '9') {
					freeMemoryBuffer.append(c);
				}
			}
			return (Long.parseLong(freeMemoryBuffer.toString()) * 1024);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
