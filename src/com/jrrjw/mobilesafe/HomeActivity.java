package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主页面
 * 
 * @author Jiang
 * 
 */
public class HomeActivity extends Activity {

	ListView lv_function = null;
	SharedPreferences sp;
	//手机杀毒与流量统计未实现
	private static String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计(未实现)",
			"手机杀毒(未实现)", "缓存清理", "高级工具", "设置中心"

	};
	private static int[] icons = { R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.a

	};
	private Dialog dialog;
	private EditText et_passwd;
	private EditText et_passwd_confirm;
	private Button bt_cancel;
	private Button bt_confirm;
	private TextView tv_grid_score;
	Button button_grid_clean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		lv_function = (ListView) findViewById(R.id.lv_function);
		tv_grid_score = (TextView) findViewById(R.id.tv_grid_score);
		button_grid_clean = (Button) findViewById(R.id.btn_grid_clean);
		button_grid_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						CleanCacheActivity1.class);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);

			}
		});

		lv_function.setDivider(null);
		lv_function.setAdapter(new LFunction());
		// 设置listview监听器
		lv_function.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 8) {
					Intent intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					//动画
					overridePendingTransition(R.anim.top_pre_in,
							R.anim.top_pre_out);

				}
				if (position == 0) {
					showPasswdDialog();
				}
				if (position == 1) {
					enterCallSms();
				}
				if (position == 2) {
					enterSoftManager();
				}
				if (position == 3) {
					enterTaskHandleActivity();
				}
				if (position == 6) {
					enterCacheClean();
				}
				if (position == 7) {
					enterAdvenceTools();
				}

			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
			String score = (String) data.getExtras().get("score");
			String btn_value = (String) data.getExtras().get("value");
			tv_grid_score.setText(score);
			button_grid_clean.setText(btn_value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
/**
 * 进入缓存清理页面
 */
	protected void enterCacheClean() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, CleanActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * 进入缓存清理页面
	 */
	protected void enterCacheClean1() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, CleanCacheActivity1.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * 进入进程清理页面
	 */
	protected void enterTaskHandleActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, TaskHandleActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);

	}
	/**
	 * 进入软件管理页面
	 */
	protected void enterSoftManager() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SoftManagerActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * 进入黑名单页面
	 */
	protected void enterCallSms() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, CallSmsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * 进入高级工具页面
	 */
	protected void enterAdvenceTools() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AdvenceToolsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}

	/**
	 * 监听是否按下返回键按钮
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 设置返回动画，
			overridePendingTransition(R.anim.top_in, R.anim.top_out);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 进入手机防盗需要先输入密码
	 */
	private void showPasswdDialog() {
		// 判断是否已经设置过密码
		if (getIsSetPasswd()) {// 代表设置过了密码
			showEnterPasswdDialog();
			bt_confirm.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String passwd = et_passwd.getText().toString().trim();
					if (!TextUtils.isEmpty(passwd)) {
						// 获得密码 判断是否一致
						String pass = sp.getString("passwd", "");
						if (pass.equals(passwd)) {
							dialog.dismiss();
							Toast.makeText(HomeActivity.this, "进入手机锁页面", 0)
									.show();
							enterLock();
						} else {
							Toast.makeText(HomeActivity.this, "密码错误", 0).show();
						}

					} else {
						Toast.makeText(HomeActivity.this, "密码不能为空", 0).show();
					}
				}
			});
			// 当点击取消时的动作
			bt_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// 关闭对话框
					dialog.dismiss();
				}
			});
		} else { // 代表没有设置过密码
			showSetPasswdDialog();
			// 然后判断输入的密码
			bt_confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String passwd = et_passwd.getText().toString().trim();
					String passwd_confirm = et_passwd_confirm.getText()
							.toString().trim();
					// 判断两次输入的密码是否一致
					if (!TextUtils.isEmpty(passwd)
							&& !TextUtils.isEmpty(passwd_confirm)) {
						if (passwd.equals(passwd_confirm)) {
							// 写入sp 关闭对话框 进入页面
							Editor editor = sp.edit();
							editor.putString("passwd", passwd);
							editor.commit();
							dialog.dismiss();
							Toast.makeText(HomeActivity.this, "进入手机锁页面", 0)
									.show();
							enterLock();
						} else {
							Toast.makeText(HomeActivity.this, "两次输入密码不一致", 0)
									.show();
						}
					} else {
						Toast.makeText(HomeActivity.this, "密码不能为空", 0).show();
					}
				}
			});

			bt_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
	}

	/**
	 * 进入防盗页面
	 */
	protected void enterLock() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(HomeActivity.this, LostFoundActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);

	}

	/**
	 * 显示设置密码的对话框
	 */
	private void showSetPasswdDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(HomeActivity.this, R.layout.setpasswd, null);
		et_passwd = (EditText) view.findViewById(R.id.et_passwd);
		et_passwd_confirm = (EditText) view
				.findViewById(R.id.et_passwd_confirm);
		bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		builder.setView(view);
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * 显示输入密码进入的对话框
	 */
	private void showEnterPasswdDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(HomeActivity.this, R.layout.enterpasswd, null);
		et_passwd = (EditText) view.findViewById(R.id.et_passwd);
		bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		builder.setView(view);
		dialog = builder.create();
		dialog.show();

	}

	/**
	 * 判断是否设置过密码
	 * 
	 * @return
	 */
	private boolean getIsSetPasswd() {
		// TODO Auto-generated method stub
		String passwd = sp.getString("passwd", null);
		return !TextUtils.isEmpty(passwd);

	}

	/**
	 * 内部类， 实现适配器，动态把数据绑定到ListView上
	 * 
	 * @author Administrator
	 */
	private class LFunction extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// 把单个ListView的布局文件压成一个view并传递过来
			View v = View.inflate(HomeActivity.this, R.layout.itemfunction,
					null);
			ImageView iv = (ImageView) v.findViewById(R.id.iv_function_icon);
			TextView tv = (TextView) v.findViewById(R.id.tv_name);
			tv.setTextSize(15f);
			iv.setImageResource(icons[position]);
			tv.setText(names[position]);

			return v;
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

}
