package com.jrrjw.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.jrrjw.mobilesafe.R;
import com.jrrjw.mobilesafe.br.MyWidget;
import com.jrrjw.mobilesafe.utils.ServiceUtils;
import com.jrrjw.mobilesafe.utils.TaskUtils;

/**
 * 小控件的一键清理监听服务
 * @author Jiang
 *
 */
public class TaskKillWidgetService extends Service {
	Timer timer;
	TimerTask task;
	TaskKillWidgetService killWidgetService;

	AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		awm = AppWidgetManager.getInstance(getApplicationContext());
		// TODO Auto-generated method stub
		super.onCreate();
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// 执行widget的动作
				long runCount = TaskUtils
						.getRunningTaskCount(getApplicationContext());
				long leftMemory = TaskUtils
						.getLeftMemory(getApplicationContext());

				ComponentName provider = new ComponentName(
						TaskKillWidgetService.this, MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				views.setTextViewText(R.id.process_count, "正在运行" + runCount
						+ "个");
				views.setTextViewText(
						R.id.process_memory,
						"剩余内存："
								+ Formatter.formatFileSize(
										getApplicationContext(), leftMemory));

				Intent intent = new Intent();
				intent.setAction("com.jrrjw.killTask");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);

			}
		};
		timer.schedule(task, 0, 1000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

}
