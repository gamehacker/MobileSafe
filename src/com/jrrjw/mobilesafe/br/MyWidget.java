package com.jrrjw.mobilesafe.br;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.jrrjw.mobilesafe.service.TaskKillWidgetService;
import com.jrrjw.mobilesafe.utils.ServiceUtils;

/**
 * 桌面小控件
 * @author Jiang
 *
 */
public class MyWidget extends AppWidgetProvider {

	public void onReceive(Context context, Intent intent) {
		System.out.println("onrecever ....");
		super.onReceive(context, intent);
	}

	public void onEnabled(Context context) {
		System.out.println("onenable....");
		super.onEnabled(context);
		// 启动服务
		boolean isRunning = ServiceUtils.isServicRunning(context,
				"com.jrrjw.mobilesafe.service.TaskKillWidgetService");
		if (isRunning == false) {
			Intent intent = new Intent(context, TaskKillWidgetService.class);
			context.startService(intent);
		}
	}

	public void onDisabled(Context context) {
		System.out.println("ondisable....");
		super.onDisabled(context);

		boolean isRunning = ServiceUtils.isServicRunning(context,
				"com.jrrjw.mobilesafe.service.TaskKillWidgetService");
		if (isRunning) {
			Intent intent = new Intent(context, TaskKillWidgetService.class);
			context.stopService(intent);
		}
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		System.out.println("onupdate....");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
