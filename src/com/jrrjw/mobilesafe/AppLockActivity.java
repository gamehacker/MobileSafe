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
 * ʵ�ֳ���������
 * @author JiangYueSong
 *
 */
public class AppLockActivity extends Activity {

	//���а�װ��app
	List<AppInfo> softInfos;
	//�б���ʾapp
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
		//������еİ�װ��app��Ϣ
		appLockInfos = sqliteUtils.getAll(this);
		//�жϳ��������������û�п���
		boolean isAppLockRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.LockAppService");
		//����������
		if (!isAppLockRunning) {
			startService(new Intent(this, LockAppService.class));
			Toast.makeText(this, "�������ѿ���", Toast.LENGTH_LONG).show();
		}

		layout = (LinearLayout) findViewById(R.id.ll_applock_load);
		lv_appName = (ListView) findViewById(R.id.lv_applock_appname);
		tv_applock_tip = (TextView) findViewById(R.id.tv_applock_tip);
		lockSqliteUtils = new AppLockSqliteUtils(this);
		fillData();
	}

	/**
	 * �������
	 */
	private void fillData() {
		//�����г������ڼ���ʱ��ֻ��ʾ���ȣ�������ʾ����������
		layout.setVisibility(View.VISIBLE);
		lv_appName.setVisibility(View.GONE);
		tv_applock_tip.setVisibility(View.GONE);
		new Thread(new Runnable() {
			public void run() {
				try {
					//���appinfo
					softInfos = SoftUtils.getAppInfo(AppLockActivity.this);
					//��������������ƣ�����...
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
					//�����߳�������
					runOnUiThread(new Runnable() {
						public void run() {
							// �ж�listVew�ǲ��ǿյ�
							if (listAdapter == null) {
								listAdapter = new MyAdapter();
								lv_appName.setAdapter(listAdapter);
							} else {
								listAdapter.notifyDataSetChanged();
							}
							//���ù�����ʱ���ʱ�����
							onScrollListener = new MyOnScrollListener();
							lv_appName.setOnScrollListener(onScrollListener);
							//���õ���item��������¼�
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
	 * item����¼�������
	 * @author JiangYueSong
	 *
	 */
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//�жϵ����λ��
			if (position == 0 || position == userApps.size() + 1) {
				return;
			}
			int newPosition = position - 1;
			//�ж�ѡ�е�״̬������Ҫ�����ĳ�����Ϣ�������ݿ�
			// userApp��ʾ�û����� sysApp��ʾϵͳ����
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
				//�����ϵͳӦ�ø���ô��
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
			//����list
			listAdapter.notifyDataSetChanged();

		}
	}

	/**
	 * listView������
	 * @author JiangYueSong
	 *
	 */
	private class MyAdapter extends BaseAdapter {
		public View getView(int position, View convertView, ViewGroup parent) {
			/*
			 * ����ͨ��ʹ��ViewHover����ڲ���ʵ���˻��������Ż���
			 * */
			View v = null;
			ViewHover hover = null;
			if (position == 0) {
				TextView tv_title = new TextView(AppLockActivity.this);
				tv_title.setText("�û�����(" + userApps.size() + ")");
				tv_title.setHeight(20);
				return tv_title;
			}
			if (position == userApps.size() + 1) {
				TextView tv_title = new TextView(AppLockActivity.this);
				tv_title.setText("ϵͳ����(" + sysApps.size() + ")");
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
					hover.tv_appLock_state.setText("������");
				} else {
					hover.iv_lockIcon.setImageResource(R.drawable.unlock);
					hover.tv_appLock_state.setText("δ����");
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
						hover.tv_appLock_state.setText("������");
					} else {
						hover.iv_lockIcon.setImageResource(R.drawable.unlock);
						hover.tv_appLock_state.setText("δ����");
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
	 * ���������¼�
	 * */
	private class MyOnScrollListener implements OnScrollListener {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			//��ʾ�������û����򣬻���ϵͳ�������
			if (firstVisibleItem <= (userApps.size() - 1)) {
				tv_applock_tip.setText("�û�����(" + userApps.size() + ")");
				return;
			}
			if (firstVisibleItem > (userApps.size() - 1)) {
				tv_applock_tip.setText("ϵͳ����(" + sysApps.size() + ")");
				return;
			}
		}
	}

}
