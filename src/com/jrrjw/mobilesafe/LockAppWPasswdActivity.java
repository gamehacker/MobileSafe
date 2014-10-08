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
 * ��������Ӧ�ó���ʱ����Ҫ������������룬�������뱻д����Ϊ12 
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
		tv_passwd_name.setText("��ǰӦ�ó���������(���룺12):" + name);
	}

	/**
	 * ���ȷ��
	 * @param v
	 */
	public void confirm(View v) {
		String passwd = editText.getText().toString().trim();
		if (TextUtils.isEmpty(passwd)) {
			Toast.makeText(this, "���벻��Ϊ��", 0).show();
			return;
		} else {
			//�ж������Ƿ���ȷ
			if ("12".equals(passwd)) {
				Intent intent = new Intent();
				intent.setAction("com.jrrjw.mobilesafe.removeApp");
				intent.putExtra("packageName", packageName);
				sendBroadcast(intent);

				finish();
			} else {
				Toast.makeText(this, "�������", 0).show();
			}
		}
	}

	/**
	 * �������ؼ����¼�
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();  //�������ɾ��
		//ֱ�ӻص�luncherҳ��
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
