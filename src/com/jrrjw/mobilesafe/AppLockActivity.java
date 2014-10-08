package com.jrrjw.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jrrjw.mobilesafe.dao.AppLockInfo;
import com.jrrjw.mobilesafe.dao.AppLockSqliteUtils;
import com.jrrjw.mobilesafe.entities.AppInfo;
import com.jrrjw.mobilesafe.service.LockAppService;
import com.jrrjw.mobilesafe.utils.ServiceUtils;
import com.jrrjw.mobilesafe.utils.SoftUtils;

/**
 * 实现程序锁功能
 * @author JiangYueSong
 *
 */
public class AppLockActivity extends Activity {

	//所有安装的app
	List<AppInfo> softInfos;
	//列表显示app
	ListView lv_appName;
	LinearLayout layout;
	protected ArrayList<AppInfo> sysApps;
	MyAdapter listAdapter;
	TextView tv_applock_tip;
	MyOnItemClickListener onItemClickListener;

	private ArrayList<AppInfo> userApps;
	MyOnScrollListener onScrollListener;
	AppLockSqliteUtils lockSqliteUtils;
	List<AppLockInfo> appLockInfos;
	AppLockSqliteUtils sqliteUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		sqliteUtils = new AppLockSqliteUtils(this);
		//获得所有的安装的app信息
		appLockInfos = sqliteUtils.getAll(this);
		//判断程序锁这个服务有没有开启
		boolean isAppLockRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.LockAppService");
		//启动程序锁
		if (!isAppLockRunning) {
			startService(new Intent(this, LockAppService.class));
			Toast.makeText(this, "程序锁已开启", Toast.LENGTH_LONG).show();
		}

		layout = (LinearLayout) findViewById(R.id.ll_applock_load);
		lv_appName = (ListView) findViewById(R.id.lv_applock_appname);
		tv_applock_tip = (TextView) findViewById(R.id.tv_applock_tip);
		lockSqliteUtils = new AppLockSqliteUtils(this);
		fillData();
	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		//当所有程序正在加载时，只显示进度，而不显示其他的内容
		layout.setVisibility(View.VISIBLE);
		lv_appName.setVisibility(View.GONE);
		tv_applock_tip.setVisibility(View.GONE);
		new Thread(new Runnable() {
			public void run() {
				try {
					//获得appinfo
					softInfos = SoftUtils.getAppInfo(AppLockActivity.this);
					//遍历他并获得名称，类型...
					for (AppInfo info : softInfos) {
						String packageName = info.getPackageName();
						for (AppLockInfo appLockInfo : appLockInfos) {
							String packageName1 = appLockInfo.getPackageName();
							if (packageName.equals(packageName1)) {
								info.setState(true);
							}
						}
					}

					userApps = new ArrayList<AppInfo>();
					sysApps = new ArrayList<AppInfo>();

					for (AppInfo si : softInfos) {
						if (si.getUserApp()) {
							userApps.add(si);
						} else {
							sysApps.add(si);
						}
					}
					//在主线程中运行
					runOnUiThread(new Runnable() {
						public void run() {
							// 判断listVew是不是空的
							if (listAdapter == null) {
								listAdapter = new MyAdapter();
								lv_appName.setAdapter(listAdapter);
							} else {
								listAdapter.notifyDataSetChanged();
							}
							//设置滚动的时候的时间监听
							onScrollListener = new MyOnScrollListener();
							lv_appName.setOnScrollListener(onScrollListener);
							//设置单个item被点击的事件
							onItemClickListener = new MyOnItemClickListener();
							lv_appName
									.setOnItemClickListener(onItemClickListener);
							layout.setVisibility(View.GONE);
							lv_appName.setVisibility(View.VISIBLE);
							tv_applock_tip.setVisibility(View.VISIBLE);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

	/**
	 * item点击事件监听器
	 * @author JiangYueSong
	 *
	 */
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//判断点击的位置
			if (position == 0 || position == userApps.size() + 1) {
				return;
			}
			int newPosition = position - 1;
			//判断选中的状态，并把要上锁的程序信息插入数据库
			// userApp表示用户程序， sysApp表示系统程序
			if (newPosition < userApps.size()) {
				String packageName = userApps.get(newPosition).getPackageName();
				if (userApps.get(newPosition).isState()) {
					userApps.get(newPosition).setState(false);
					lockSqliteUtils.deleteInfo(packageName);
					for (AppInfo appInfo : softInfos) {
						if (appInfo.getPackageName().equals(packageName)) {
							appInfo.setState(false);
						}
					}
				} else {
					AppLockInfo appLockInfo = new AppLockInfo();
					appLockInfo.setPackageName(packageName);
					userApps.get(newPosition).setState(true);
					for (AppInfo appInfo : softInfos) {
						if (appInfo.getPackageName().equals(packageName)) {
							appInfo.setState(true);
							appLockInfo.setName(appInfo.getTitle());
						}
					}
					lockSqliteUtils.insertInfo(appLockInfo);
				}
			} else {
				//如果是系统应用该怎么做
				int new2Position = position - userApps.size() - 2;
				String packageName = sysApps.get(new2Position).getPackageName();
				if (sysApps.get(new2Position).isState()) {
					lockSqliteUtils.deleteInfo(packageName);
					sysApps.get(new2Position).setState(false);
					for (AppInfo appInfo : softInfos) {
						if (appInfo.getPackageName().equals(packageName)) {
							appInfo.setState(false);
						}
					}
				} else {
					AppLockInfo appLockInfo = new AppLockInfo();
					appLockInfo.setPackageName(packageName);
					sysApps.get(new2Position).setState(true);
					for (AppInfo appInfo : softInfos) {
						if (appInfo.getPackageName().equals(packageName)) {
							appInfo.setState(true);
							appLockInfo.setName(appInfo.getTitle());
						}
					}
					lockSqliteUtils.insertInfo(appLockInfo);
				}
			}
			//更新list
			listAdapter.notifyDataSetChanged();

		}
	}

	/**
	 * listView适配器
	 * @author JiangYueSong
	 *
	 */
	private class MyAdapter extends BaseAdapter {
		public View getView(int position, View convertView, ViewGroup parent) {
			/*
			 * 这里通过使用ViewHover这个内部类实现了缓存的相关优化。
			 * */
			View v = null;
			ViewHover hover = null;
			if (position == 0) {
				TextView tv_title = new TextView(AppLockActivity.this);
				tv_title.setText("用户程序(" + userApps.size() + ")");
				tv_title.setHeight(20);
				return tv_title;
			}
			if (position == userApps.size() + 1) {
				TextView tv_title = new TextView(AppLockActivity.this);
				tv_title.setText("系统程序(" + sysApps.size() + ")");
				tv_title.setHeight(20);
				return tv_title;
			}

			if ((convertView instanceof RelativeLayout) == false) {
				v = View.inflate(AppLockActivity.this, R.layout.applockitem,
						null);
				hover = new ViewHover();
				hover.iv_appLock_icon = (ImageView) v
						.findViewById(R.id.iv_applock_icon);
				hover.tv_appLock_title = (TextView) v
						.findViewById(R.id.tv_applock_name);
				hover.tv_appLock_state = (TextView) v
						.findViewById(R.id.tv_applock_state);
				hover.iv_lockIcon = (ImageView) v
						.findViewById(R.id.iv_applock_Lock);
				v.setTag(hover);
			} else {
				v = convertView;
				hover = (ViewHover) v.getTag();
			}
			if (position <= userApps.size()) {
				int fposition = position - 1;
				hover.iv_appLock_icon.setImageDrawable(userApps.get(fposition)
						.getIcon());
				hover.tv_appLock_title.setText(userApps.get(fposition)
						.getTitle());

				if (userApps.get(fposition).isState()) {
					hover.iv_lockIcon.setImageResource(R.drawable.applock);
					hover.tv_appLock_state.setText("已上锁");
				} else {
					hover.iv_lockIcon.setImageResource(R.drawable.unlock);
					hover.tv_appLock_state.setText("未上锁");
				}
			} else {
				int newPosition = position - userApps.size() - 2;

				if (newPosition < sysApps.size()) {
					hover.iv_appLock_icon.setImageDrawable(sysApps.get(
							newPosition).getIcon());
					hover.tv_appLock_title.setText(sysApps.get(newPosition)
							.getTitle());
					if (sysApps.get(newPosition).isState()) {
						hover.iv_lockIcon.setImageResource(R.drawable.applock);
						hover.tv_appLock_state.setText("已上锁");
					} else {
						hover.iv_lockIcon.setImageResource(R.drawable.unlock);
						hover.tv_appLock_state.setText("未上锁");
					}
				}
			}
			return v;
		}

		public int getCount() {
			return softInfos.size() + 2;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}

	class ViewHover {
		ImageView iv_appLock_icon = null;
		TextView tv_appLock_title = null;
		TextView tv_appLock_state = null;
		ImageView iv_lockIcon = null;
	}

	public void setBack(View v) {
		finish();
	}

	/*
	 * 滚动监听事件
	 * */
	private class MyOnScrollListener implements OnScrollListener {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			//显示顶部的用户程序，或者系统程序个数
			if (firstVisibleItem <= (userApps.size() - 1)) {
				tv_applock_tip.setText("用户程序(" + userApps.size() + ")");
				return;
			}
			if (firstVisibleItem > (userApps.size() - 1)) {
				tv_applock_tip.setText("系统程序(" + sysApps.size() + ")");
				return;
			}
		}
	}

}
