package com.jrrjw.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 手机防盗的第三个向导页面，输入安全号码
 * @author Administrator
 *
 */
public class LockSet3Activity extends BaseTouchActivity {

	private EditText et_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_set3);
		et_phone = (EditText) findViewById(R.id.et_phone);
		String preNumber = sp.getString("safenumber", "");
		if (!TextUtils.isEmpty(preNumber)) {
			et_phone.setText(preNumber);
		}

	}

	public void selectContact(View v) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}

	/*
	 *这个方法是获取用户点击下一个页面返回的值，获得手机号码
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null)
			return;
		String phone = data.getStringExtra("phone");
		et_phone.setText(phone);
	}

	public void showPre() {
		Intent intent = new Intent(this, LockSet2Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		finish();
	}

	public void showNext() {
		// 强制用户输入手机号
		if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
			Editor editor = sp.edit();
			editor.putString("safenumber", et_phone.getText().toString().trim());
			editor.commit();
			Intent intent = new Intent(this, LockSet4Activity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			finish();
		} else {
			Toast.makeText(LockSet3Activity.this, "请先设置安全号码", 0).show();
		}

	}
}
