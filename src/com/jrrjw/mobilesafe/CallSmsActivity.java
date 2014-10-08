package com.jrrjw.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jrrjw.mobilesafe.dao.BlackNameInfo;
import com.jrrjw.mobilesafe.dao.CallSmsSqliteUtils;
import com.jrrjw.mobilesafe.service.LockAppService;
import com.jrrjw.mobilesafe.service.TelAddressService;
import com.jrrjw.mobilesafe.utils.ServiceUtils;

/**
 * 黑名单设置
 * @author JiangYueSong
 *
 */
public class CallSmsActivity extends Activity {

	public static final String TAG = null;
	ListView lv_blackname;
	CallSmsAdapter adapter;
	List<BlackNameInfo> infos = null;
	CallSmsSqliteUtils callSmsSqliteUtils;
	private View v_add;
	LinearLayout ll_load;
	Button btn_add;
	int start = 0;
	int Max = 0;
	int count = 10;
	MyOnSrollListener onSrollListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms);
		lv_blackname = (ListView) findViewById(R.id.lv_callsms_blackname);
		ll_load = (LinearLayout) findViewById(R.id.ll_callsms_load);
		btn_add = (Button) findViewById(R.id.bt_callsms_add);
		callSmsSqliteUtils = new CallSmsSqliteUtils(this);
		adapter = new CallSmsAdapter();

		//判断服务有没有运行
		boolean isAppLockRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.CallSmsService");

		if (!isAppLockRunning) {
			Toast.makeText(this, "黑名单未开启，请在设置中开启", Toast.LENGTH_LONG).show();
		}
		//显示正在加载的进度条
		ll_load.setVisibility(View.VISIBLE);
		lv_blackname.setVisibility(View.GONE);
		btn_add.setVisibility(View.GONE);
		if (infos == null) {
			new Thread(new Runnable() {
				public void run() {
					Max = callSmsSqliteUtils.getCount();
					infos = new ArrayList<BlackNameInfo>();
					//填充list数据
					if (Max != 0) {
						infos.addAll(callSmsSqliteUtils.getAllInfoPart(start,
								count));
						runOnUiThread(new Runnable() {
							public void run() {
								btn_add.setVisibility(View.VISIBLE);
								ll_load.setVisibility(View.GONE);
								lv_blackname.setVisibility(View.VISIBLE);
								lv_blackname.setAdapter(adapter);
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								btn_add.setVisibility(View.VISIBLE);
								ll_load.setVisibility(View.GONE);
								lv_blackname.setVisibility(View.VISIBLE);
								Toast.makeText(CallSmsActivity.this,
										"当前没有黑名单，点击添加", 0).show();
								lv_blackname.setAdapter(adapter);
							}
						});
					}
				}
			}).start();

		}
		// lv_blackname.setAdapter(adapter);
		onSrollListener = new MyOnSrollListener();
		lv_blackname.setOnScrollListener(onSrollListener);
	}

	/**
	 * 
	 * 滚动监听
	 * @author Jiang
	 *
	 */
	class MyOnSrollListener implements OnScrollListener {

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (lv_blackname.getLastVisiblePosition() < (Max - 1)
						&& lv_blackname.getLastVisiblePosition() == (infos
								.size() - 1)) {
					lv_blackname.setVisibility(View.GONE);
					ll_load.setVisibility(View.VISIBLE);
					btn_add.setVisibility(View.GONE);
					new Thread(new Runnable() {
						public void run() {
							start += count;
							infos.addAll(callSmsSqliteUtils.getAllInfoPart(
									start, count));
							runOnUiThread(new Runnable() {
								public void run() {
									btn_add.setVisibility(View.VISIBLE);
									ll_load.setVisibility(View.GONE);
									lv_blackname.setVisibility(View.VISIBLE);
									adapter.notifyDataSetChanged();
								}
							});
						}
					}).start();

				}

				break;

			default:
				break;
			}

		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

	}

	public void hisSms(View v) {

	}

	public void hisCall(View v) {

	}

	public void setBack(View v) {
		finish();
	}

	/**
	 * 点击添加时做的动作
	 * @param view
	 */
	public void add(View view) {
		//弹出自定义对话框
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("添加黑名单");
		v_add = View.inflate(this, R.layout.setaddblackname, null);
		builder.setView(v_add);
		//设置确定按钮需要做的事
		builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText phone = (EditText) v_add
						.findViewById(R.id.et_callsms_phone);
				EditText name = (EditText) v_add
						.findViewById(R.id.et_callsms_name);
				CheckBox cb_call = (CheckBox) v_add
						.findViewById(R.id.cb_callsms_call);
				CheckBox cb_sms = (CheckBox) v_add
						.findViewById(R.id.cb_callsms_sms);

				String number = phone.getText().toString().trim();
				String name1 = name.getText().toString().trim();
				boolean call = cb_call.isChecked();
				boolean sms = cb_sms.isChecked();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(CallSmsActivity.this, "号码不能为空", 0).show();
					return;
				}
				if (callSmsSqliteUtils.getDataByPhone(CallSmsActivity.this,
						number)) {
					Toast.makeText(CallSmsActivity.this, "号码已存在", 0).show();
					return;
				}
				if (call == false && sms == false) {
					Toast.makeText(CallSmsActivity.this, "拦截内容至少选择一个", 0)
							.show();
					return;
				}

				// 插入数据库
				BlackNameInfo info = new BlackNameInfo();
				info.setPhone(number);
				info.setName(name1);
				if (call == true && sms == true) {
					info.setMode("3");
				} else if (sms == true) {
					info.setMode("2");
				} else if (call == true) {
					info.setMode("1");
				}
				callSmsSqliteUtils.insertInfo(info);
				Max = callSmsSqliteUtils.getCount();
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				System.out.println(infos.size() + ".....");
				Toast.makeText(CallSmsActivity.this, "添加成功", 0).show();
			}
		});
		//设置取消做的事
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		builder.show();
	}

	/**
	 * 适配器
	 * @author Jiang
	 *
	 */
	private class CallSmsAdapter extends BaseAdapter {
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewInfo holder;
			// 1.减少内存中view对象创建的个数
			if (convertView == null) {
				Log.i(TAG, "创建新的view对象：" + position);
				// 把一个布局文件转化成 view对象。
				view = View.inflate(getApplicationContext(),
						R.layout.callsmsitem, null);
				// 2.减少子孩子查询的次数 内存中对象的地址。
				holder = new ViewInfo();
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_callsms_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_callsms_mode);
				holder.iv_del = (ImageView) view
						.findViewById(R.id.iv_callsms_del);
				// 当孩子生出来的时候找到他们的引用，存放在记事本，放在父亲的口袋
				view.setTag(holder);
			} else {
				Log.i(TAG, "厨房有历史的view对象，复用历史缓存的view对象：" + position);
				view = convertView;
				holder = (ViewInfo) view.getTag();// 5%
			}
			holder.tv_phone.setText(infos.get(position).getPhone());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else {
				holder.tv_mode.setText("全部拦截");
			}
			holder.iv_del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(
							CallSmsActivity.this);
					builder.setTitle("警告");
					builder.setMessage("确定要删除这条记录么？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 删除数据库的内容
									callSmsSqliteUtils.deleteInfo(infos.get(
											position).getPhone());
									// 更新界面。
									infos.remove(position);
									Max = callSmsSqliteUtils.getCount();
									// 通知listview数据适配器更新
									adapter.notifyDataSetChanged();
								}
							});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});

			return view;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

	}

	class ViewInfo {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_del;
	}

}
