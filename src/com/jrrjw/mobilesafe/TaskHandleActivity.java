package com.jrrjw.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrrjw.mobilesafe.entities.TaskInfo;
import com.jrrjw.mobilesafe.utils.TaskUtils;

/**
 * 进程清理
 * @author Jiang
 *
 */
public class TaskHandleActivity extends Activity {

	TextView tv_runningTask;
	TextView tv_memory;
	ListView lv_taskName;
	List<TaskInfo> taskInfos;
	LinearLayout ll_load;
	MyTaskAdapter myTaskAdapter = null;
	List<TaskInfo> userTasks;
	List<TaskInfo> sysTasks;
	MyTaskOnItemClick myTaskOnItemClick;
	int count = 0;
	long memory = 0L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_handle);
		tv_runningTask = (TextView) findViewById(R.id.tv_task_runningTask);
		tv_memory = (TextView) findViewById(R.id.tv_task_memory);
		ll_load = (LinearLayout) findViewById(R.id.ll_task_load);
		lv_taskName = (ListView) findViewById(R.id.lv_task_body);
		count = (int) TaskUtils.getRunningTaskCount(TaskHandleActivity.this);
		fillTop(null, null);
		taskInfos = TaskUtils.getTaskInfos(TaskHandleActivity.this);
		fillData();

		myTaskOnItemClick = new MyTaskOnItemClick();
		lv_taskName.setOnItemClickListener(myTaskOnItemClick);

	}

	/**
	 * 顶部的textbox
	 * @param first  
	 * @param second 
	 */
	private void fillTop(String first, String second) {
		if (first == null) {
			tv_runningTask.setText("运行:"
					+ TaskUtils.getRunningTaskCount(TaskHandleActivity.this)
					+ "个      ");
		} else {
			tv_runningTask.setText("运行:" + first + "个      ");
		}
		if (second == null) {
			try {
				String value = Formatter.formatFileSize(this,
						TaskUtils.getLeftMemory(TaskHandleActivity.this));
				tv_memory.setText("可用内存:" + value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			tv_memory.setText("可用内存:" + second);
		}
	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		ll_load.setVisibility(View.VISIBLE);
		lv_taskName.setVisibility(View.GONE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				userTasks = new ArrayList<TaskInfo>();
				sysTasks = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : taskInfos) {

					if (taskInfo.isUserTask()) {
						userTasks.add(taskInfo);
					} else {
						sysTasks.add(taskInfo);
					}

				}
				runOnUiThread(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						ll_load.setVisibility(View.GONE);
						lv_taskName.setVisibility(View.VISIBLE);
						if (myTaskAdapter == null) {
							myTaskAdapter = new MyTaskAdapter();
							lv_taskName.setAdapter(myTaskAdapter);
						} else {
							myTaskAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
	}

	private class MyTaskOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0 || position == userTasks.size() + 1) {
				return;
			}
			int newPosition = position - 1;
			if (newPosition < userTasks.size()) {
				if (userTasks.get(newPosition).isChecked()) {
					userTasks.get(newPosition).setChecked(false);
					changeTaskInfosByPN(userTasks.get(newPosition)
							.getPackname(), false);
				} else {
					userTasks.get(newPosition).setChecked(true);
					changeTaskInfosByPN(userTasks.get(newPosition)
							.getPackname(), true);
				}
			} else {
				int new2Position = newPosition - userTasks.size() - 1;
				if (sysTasks.get(new2Position).isChecked()) {
					sysTasks.get(new2Position).setChecked(false);
					changeTaskInfosByPN(sysTasks.get(new2Position)
							.getPackname(), false);
				} else {
					sysTasks.get(new2Position).setChecked(true);
					changeTaskInfosByPN(sysTasks.get(new2Position)
							.getPackname(), true);
				}
			}
			myTaskAdapter.notifyDataSetChanged();

		}

		public void changeTaskInfosByPN(String packageName, boolean isCheck) {
			for (TaskInfo taskInfo : taskInfos) {
				if (packageName.equals(taskInfo.getPackname())) {
					taskInfo.setChecked(isCheck);
				}

			}

		}

	}

	private class MyTaskAdapter extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			ViewHover hover = null;

			if (position == 0) {
				TextView tv_title = new TextView(TaskHandleActivity.this);
				tv_title.setText("用户程序(" + userTasks.size() + ")");
				tv_title.setHeight(20);
				return tv_title;
			}

			if (position == userTasks.size() + 1) {
				TextView tv_title = new TextView(TaskHandleActivity.this);
				tv_title.setText("系统程序(" + sysTasks.size() + ")");
				tv_title.setHeight(20);
				return tv_title;
			}
			if (convertView != null && convertView instanceof TextView) {
				v = View.inflate(TaskHandleActivity.this,
						R.layout.tasknameitem, null);
				hover = new ViewHover();
				hover.iv_task_icon = (ImageView) v
						.findViewById(R.id.iv_task_icon);
				hover.tv_task_title = (TextView) v
						.findViewById(R.id.tv_task_name);
				hover.tv_task_memory = (TextView) v
						.findViewById(R.id.tv_task_memory);
				hover.cb_isCheck = (CheckBox) v
						.findViewById(R.id.cb_task_isCheck);
				v.setTag(hover);
			} else if (convertView != null
					&& (convertView instanceof RelativeLayout)) {
				v = convertView;
				hover = (ViewHover) v.getTag();
			}
			if (position <= userTasks.size()) {
				int fposition = position - 1;
				hover.iv_task_icon.setImageDrawable(userTasks.get(fposition)
						.getIcon());
				String mem = Formatter.formatFileSize(TaskHandleActivity.this,
						userTasks.get(fposition).getMemsize());
				hover.tv_task_title.setText(userTasks.get(fposition).getName());
				hover.tv_task_memory.setText(mem);
				hover.cb_isCheck.setChecked(userTasks.get(fposition)
						.isChecked());
			} else {
				int newPosition = position - userTasks.size() - 2;
				hover.iv_task_icon.setImageDrawable(sysTasks.get(newPosition)
						.getIcon());
				hover.tv_task_title
						.setText(sysTasks.get(newPosition).getName());
				String mem = Formatter.formatFileSize(TaskHandleActivity.this,
						sysTasks.get(newPosition).getMemsize());
				hover.tv_task_memory.setText(mem);
				hover.cb_isCheck.setChecked(sysTasks.get(newPosition)
						.isChecked());
			}
			return v;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return taskInfos.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	public void checkAll(View v) {
		for (TaskInfo taskInfo : taskInfos) {
			taskInfo.setChecked(true);

		}
		fillData();

	}

	public void checkRev(View v) {

		for (TaskInfo taskInfo : taskInfos) {
			if (taskInfo.isChecked()) {
				taskInfo.setChecked(false);
			} else {
				taskInfo.setChecked(true);
			}

		}
		fillData();

	}

	/**
	 * 清理进程
	 * @param v
	 */
	public void killTask(View v) {
		//通过管理器可实现正在运行的进程的管理
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<TaskInfo> taskInfosBak = new ArrayList<TaskInfo>();
		for (TaskInfo taskInfo : taskInfos) {
			taskInfosBak.add(taskInfo);
		}

		for (TaskInfo taskInfo : taskInfosBak) {
			if (taskInfo.isChecked()) {
				am.killBackgroundProcesses(taskInfo.getPackname());
				if (taskInfo.getPackname().equals("com.jrrjw.mobilesafe") == false) {
					taskInfos.remove(taskInfo);
					count--;
					memory += taskInfo.getMemsize();
				}
			}
		}

		String first = String.valueOf(count);
		String second = Formatter.formatFileSize(this,
				TaskUtils.getLeftMemory(TaskHandleActivity.this) + memory);
		fillTop(first, second);
		// 显示listView
		fillData();

	}

	/**
	 * 设置页面
	 * @param v
	 */
	public void setSet(View v) {
		Intent intent = new Intent(this, TaskSettingActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}

	public void setBack(View v) {
		finish();
	}

	class ViewHover {
		ImageView iv_task_icon = null;
		TextView tv_task_title = null;
		TextView tv_task_memory = null;
		CheckBox cb_isCheck = null;
	}
}
