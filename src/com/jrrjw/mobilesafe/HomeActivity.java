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
 * ��ҳ��
 * 
 * @author Jiang
 * 
 */
public class HomeActivity extends Activity {

	ListView lv_function = null;
	SharedPreferences sp;
	//�ֻ�ɱ��������ͳ��δʵ��
	private static String[] names = { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���", "����ͳ��(δʵ��)",
			"�ֻ�ɱ��(δʵ��)", "��������", "�߼�����", "��������"

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
		// ����listview������
		lv_function.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 8) {
					Intent intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					//����
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
 * ���뻺������ҳ��
 */
	protected void enterCacheClean() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, CleanActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * ���뻺������ҳ��
	 */
	protected void enterCacheClean1() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, CleanCacheActivity1.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * �����������ҳ��
	 */
	protected void enterTaskHandleActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, TaskHandleActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);

	}
	/**
	 * �����������ҳ��
	 */
	protected void enterSoftManager() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SoftManagerActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * ���������ҳ��
	 */
	protected void enterCallSms() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, CallSmsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}
	/**
	 * ����߼�����ҳ��
	 */
	protected void enterAdvenceTools() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AdvenceToolsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}

	/**
	 * �����Ƿ��·��ؼ���ť
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// ���÷��ض�����
			overridePendingTransition(R.anim.top_in, R.anim.top_out);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �����ֻ�������Ҫ����������
	 */
	private void showPasswdDialog() {
		// �ж��Ƿ��Ѿ����ù�����
		if (getIsSetPasswd()) {// �������ù�������
			showEnterPasswdDialog();
			bt_confirm.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String passwd = et_passwd.getText().toString().trim();
					if (!TextUtils.isEmpty(passwd)) {
						// ������� �ж��Ƿ�һ��
						String pass = sp.getString("passwd", "");
						if (pass.equals(passwd)) {
							dialog.dismiss();
							Toast.makeText(HomeActivity.this, "�����ֻ���ҳ��", 0)
									.show();
							enterLock();
						} else {
							Toast.makeText(HomeActivity.this, "�������", 0).show();
						}

					} else {
						Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
					}
				}
			});
			// �����ȡ��ʱ�Ķ���
			bt_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// �رնԻ���
					dialog.dismiss();
				}
			});
		} else { // ����û�����ù�����
			showSetPasswdDialog();
			// Ȼ���ж����������
			bt_confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String passwd = et_passwd.getText().toString().trim();
					String passwd_confirm = et_passwd_confirm.getText()
							.toString().trim();
					// �ж���������������Ƿ�һ��
					if (!TextUtils.isEmpty(passwd)
							&& !TextUtils.isEmpty(passwd_confirm)) {
						if (passwd.equals(passwd_confirm)) {
							// д��sp �رնԻ��� ����ҳ��
							Editor editor = sp.edit();
							editor.putString("passwd", passwd);
							editor.commit();
							dialog.dismiss();
							Toast.makeText(HomeActivity.this, "�����ֻ���ҳ��", 0)
									.show();
							enterLock();
						} else {
							Toast.makeText(HomeActivity.this, "�����������벻һ��", 0)
									.show();
						}
					} else {
						Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
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
	 * �������ҳ��
	 */
	protected void enterLock() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(HomeActivity.this, LostFoundActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);

	}

	/**
	 * ��ʾ��������ĶԻ���
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
	 * ��ʾ�����������ĶԻ���
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
	 * �ж��Ƿ����ù�����
	 * 
	 * @return
	 */
	private boolean getIsSetPasswd() {
		// TODO Auto-generated method stub
		String passwd = sp.getString("passwd", null);
		return !TextUtils.isEmpty(passwd);

	}

	/**
	 * �ڲ��࣬ ʵ������������̬�����ݰ󶨵�ListView��
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
			// �ѵ���ListView�Ĳ����ļ�ѹ��һ��view�����ݹ���
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
