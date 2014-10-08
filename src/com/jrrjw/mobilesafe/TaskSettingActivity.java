package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jrrjw.mobilesafe.service.TaskKillService;
import com.jrrjw.mobilesafe.ui.SettingItem;
import com.jrrjw.mobilesafe.utils.ServiceUtils;

/**
 * 设置页面，设置锁屏清理
 * @author Jiang
 *
 */
public class TaskSettingActivity extends Activity {

	SettingItem si_taskset_item;
	boolean isStart = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		si_taskset_item = (SettingItem) findViewById(R.id.si_taskset_item);
		isStart = ServiceUtils.isServicRunning(TaskSettingActivity.this,
				"com.jrrjw.mobilesafe.service.TaskKillService");
		if (isStart) {
			si_taskset_item.setCheck(true);
		} else {
			si_taskset_item.setCheck(false);
		}
		si_taskset_item.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (si_taskset_item.getCheckedState()) {

					si_taskset_item.setCheck(false);
					stopService();
				} else {
					si_taskset_item.setCheck(true);
					startService();
				}
			}
		});
	}

	public void setBack(View v) {
		finish();
	}

	protected void startService() {
		isStart = ServiceUtils.isServicRunning(TaskSettingActivity.this,
				"com.jrrjw.mobilesafe.service.TaskKillService");
		Intent intent = new Intent(TaskSettingActivity.this,
				TaskKillService.class);
		if (isStart == false) {
			startService(intent);
		}
	}

	protected void stopService() {
		isStart = ServiceUtils.isServicRunning(TaskSettingActivity.this,
				"com.jrrjw.mobilesafe.service.TaskKillService");
		Intent intent = new Intent(TaskSettingActivity.this,
				TaskKillService.class);
		if (isStart) {
			stopService(intent);
		}

	}
}
