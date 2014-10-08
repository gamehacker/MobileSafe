package com.jrrjw.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
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

import com.jrrjw.mobilesafe.entities.SoftInfo;
import com.jrrjw.mobilesafe.utils.SoftUtils;

/**
 * 软件管理
 * @author Jiang
 *
 */
public class SoftManagerActivity extends Activity {
	TextView tv_soft_ownMemory;
	TextView tv_soft_sdMemory;
	TextView tv_soft_tip;
	ListView listView;
	List<SoftInfo> softInfos;
	LinearLayout ll_load;
	List<SoftInfo> userApps;
	List<SoftInfo> sysApps;
	MyAdapter listAdapter;
	MyOnScrollListener onScrollListener;
	MyOnItemClickListener onItemClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soft_manager);
		tv_soft_ownMemory = (TextView) findViewById(R.id.tv_soft_ownmemory);
		tv_soft_sdMemory = (TextView) findViewById(R.id.tv_soft_sdmemory);
		tv_soft_tip = (TextView) findViewById(R.id.tv_soft_tip);
		ll_load = (LinearLayout) findViewById(R.id.ll_soft_load);
		listView = (ListView) findViewById(R.id.lv_soft_body);
		fillData();

		tv_soft_ownMemory.setText("手机可用："
				+ String.valueOf(Formatter.formatFileSize(this,
						SoftUtils.getOwnMemory(this))));
		tv_soft_sdMemory.setText("SD可用："
				+ String.valueOf(Formatter.formatFileSize(this,
						SoftUtils.getSDMemory(this))));

	}

	private void fillData() {
		//填充数据，显示进度
		ll_load.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			public void run() {
				try {
					softInfos = SoftUtils
							.getApplicationInfo(SoftManagerActivity.this);
					System.out.println(softInfos.size());
					userApps = new ArrayList<SoftInfo>();
					sysApps = new ArrayList<SoftInfo>();

					for (SoftInfo si : softInfos) {
						if (si.getUserApp()) {
							userApps.add(si);
						} else {
							sysApps.add(si);
						}
					}
					runOnUiThread(new Runnable() {

						public void run() {
							if (listAdapter == null) {
								listAdapter = new MyAdapter();
								listView.setAdapter(listAdapter);
							} else {
								listAdapter.notifyDataSetChanged();
							}
							onScrollListener = new MyOnScrollListener();
							listView.setOnScrollListener(onScrollListener);
							onItemClickListener = new MyOnItemClickListener();
							listView.setOnItemClickListener(onItemClickListener);
							ll_load.setVisibility(View.GONE);
							listView.setVisibility(View.VISIBLE);
							tv_soft_tip.setVisibility(View.VISIBLE);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	/**
	 * 设置监听器
	 * @author Jiang
	 *
	 */
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0 || position == userApps.size()) {
				System.out.println("click");
				return;
			}
			int newPosition = 0;
			if (position < userApps.size()) {
				newPosition = position - 1;

			} else if (position > userApps.size()) {
				newPosition = position - userApps.size();
			}
			final int new2Position = newPosition;

			AlertDialog.Builder builder = new Builder(SoftManagerActivity.this);
			if (newPosition < userApps.size()) {
				builder.setTitle(userApps.get(newPosition).getTitle());
			} else {
				builder.setTitle(sysApps.get(newPosition).getTitle());
			}
			builder.setItems(new String[] { "启动程序", "卸载程序", "分享软件" },
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String packageName = "";
							String appName = "";
							int new3position = new2Position;
							if (new2Position < userApps.size()) {
								packageName = userApps.get(new2Position)
										.getPackageName();
								appName = userApps.get(new2Position).getTitle();
							} else {
								new3position = new2Position - userApps.size();
								packageName = sysApps.get(new3position)
										.getPackageName();
								appName = sysApps.get(new3position).getTitle();
							}
							if (which == 0) {

								startOtherApplication(packageName);
								return;
							}
							if (which == 1) {
								unInstallApplication(packageName);
								return;
							}
							if (which == 2) {
								sharedApplication(appName);
								return;
							}

						}
					});
			builder.show();
		}
	}

	/**
	 * 卸载应用程序
	 * @param packageName 包名  只能通过报名唯一确定这个app
	 */
	public void unInstallApplication(String packageName) {

		// <action android:name="android.intent.action.VIEW" />
		// <action android:name="android.intent.action.DELETE" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:scheme="package" />
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.setAction("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + packageName));
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			Toast.makeText(SoftManagerActivity.this, "该应用无法卸载", 0).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		fillData();

	}

	/**
	 * 分享app
	 * @param appName
	 */
	protected void sharedApplication(String appName) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "请下载一下应用 " + appName);
		startActivity(intent);

	}

	/**
	 * 开启app
	 * @param packageName
	 */
	protected void startOtherApplication(String packageName) {
		// TODO Auto-generated method stub
		PackageManager pm = SoftManagerActivity.this.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(packageName);
		if (intent == null) {
			Toast.makeText(SoftManagerActivity.this, "该应用无法启动", 0).show();
		} else {
			startActivity(intent);
			overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
		}

	}

	/**
	 * 滚动事件
	 * @author Jiang
	 *
	 */
	private class MyOnScrollListener implements OnScrollListener {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem <= (userApps.size() - 1)) {
				tv_soft_tip.setText("用户程序(" + userApps.size() + ")");
				return;
			}
			if (firstVisibleItem > (userApps.size() - 1)) {
				tv_soft_tip.setText("系统程序(" + sysApps.size() + ")");
				return;
			}
		}
	}

	private class MyAdapter extends BaseAdapter {
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			ViewHover hover = null;
			if (position == 0) {
				TextView tv_title = new TextView(SoftManagerActivity.this);
				tv_title.setText("用户程序(" + userApps.size() + ")");
				tv_title.setHeight(20);
				return tv_title;
			}
			if (position == userApps.size() + 1) {
				TextView tv_title = new TextView(SoftManagerActivity.this);
				tv_title.setText("系统程序(" + sysApps.size() + ")");
				tv_title.setHeight(20);
				return tv_title;
			}

			if ((convertView instanceof RelativeLayout) == false) {
				v = View.inflate(SoftManagerActivity.this,
						R.layout.softnameitem, null);
				hover = new ViewHover();
				hover.iv_soft_icon = (ImageView) v
						.findViewById(R.id.iv_soft_icon);
				hover.tv_soft_title = (TextView) v
						.findViewById(R.id.tv_soft_name);
				hover.tv_soft_state = (TextView) v
						.findViewById(R.id.tv_soft_state);
				System.out.println(hover.tv_soft_title);
				v.setTag(hover);
			} else {
				v = convertView;
				hover = (ViewHover) v.getTag();
			}
			if (position <= userApps.size()) {
				int fposition = position - 1;
				hover.iv_soft_icon.setImageDrawable(userApps.get(fposition)
						.getIcon());
				hover.tv_soft_title.setText(userApps.get(fposition).getTitle());
				if (userApps.get(fposition).getState() == 1) {
					hover.tv_soft_state.setText("安装在SD卡");
				} else {
					hover.tv_soft_state.setText("安装在手机");
				}
			} else {
				int newPosition = position - userApps.size() - 2;
				 if (newPosition < sysApps.size()) {
				hover.iv_soft_icon.setImageDrawable(sysApps.get(newPosition)
						.getIcon());
				hover.tv_soft_title
						.setText(sysApps.get(newPosition).getTitle());
				if (sysApps.get(newPosition).getState() == 1) {
					hover.tv_soft_state.setText("安装在SD卡");
				} else {
					hover.tv_soft_state.setText("安装在手机");
				}
			}
			 }
			return v;
		}

		public int getCount() {
			return softInfos.size()+2;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}

	class ViewHover {
		ImageView iv_soft_icon = null;
		TextView tv_soft_title = null;
		TextView tv_soft_state = null;
	}

	public void setBack(View v) {
		finish();
	}
}
