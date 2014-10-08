package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 当点击别的应用程序时，需要输入程序锁密码，这里密码被写死了为12 
 * @author Jiang
 *
 */
public class LockAppWPasswdActivity extends Activity {

	EditText editText;
	String packageName;
	String name;
	TextView tv_passwd_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_app_wpasswd);
		editText = (EditText) findViewById(R.id.et_passwd_input);
		tv_passwd_name = (TextView) findViewById(R.id.tv_passwd_name);
		packageName = (String) getIntent().getExtras().get("packageName");
		name = (String) getIntent().getExtras().get("name");
		tv_passwd_name.setText("当前应用程序已上锁(密码：12):" + name);
	}

	/**
	 * 点击确认
	 * @param v
	 */
	public void confirm(View v) {
		String passwd = editText.getText().toString().trim();
		if (TextUtils.isEmpty(passwd)) {
			Toast.makeText(this, "密码不能为空", 0).show();
			return;
		} else {
			//判断密码是否正确
			if ("12".equals(passwd)) {
				Intent intent = new Intent();
				intent.setAction("com.jrrjw.mobilesafe.removeApp");
				intent.putExtra("packageName", packageName);
				sendBroadcast(intent);

				finish();
			} else {
				Toast.makeText(this, "密码错误", 0).show();
			}
		}
	}

	/**
	 * 监听返回键的事件
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();  //这个不能删掉
		//直接回到luncher页面
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}

	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		finish();
	}

}
