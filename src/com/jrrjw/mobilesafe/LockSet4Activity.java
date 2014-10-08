package com.jrrjw.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jrrjw.mobilesafe.ui.SettingItem;

/**
 * 手机防盗的第四个向导页面 设置是否开启防盗保护
 * @author Administrator
 *
 */
public class LockSet4Activity extends BaseTouchActivity {

	SettingItem si_openlock;   //自定义控件，显示一个单选框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_set4);
		si_openlock = (SettingItem) findViewById(R.id.si_openlock);
		boolean preCheck = sp.getBoolean("protect", false);
		// 设置单选框的回显
		if (preCheck) {
			si_openlock.setCheck(true);
		} else {
			si_openlock.setCheck(false);
		}

		si_openlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isChecked = si_openlock.getCheckedState();
				Editor editor = sp.edit();
				if (isChecked) {
					si_openlock.setCheck(false);
					editor.putBoolean("protect", false);
				} else {
					si_openlock.setCheck(true);
					editor.putBoolean("protect", true);
				}
				editor.commit();

			}
		});

	}

	public void showPre() {
		Intent intent = new Intent(this, LockSet3Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		finish();
	}

	public void showNext() {
		// 这里不强制用户勾选
		sp = getSharedPreferences("config", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("lock", true);
		editor.commit();
		Intent intent = new Intent(this, LostFoundActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		finish();
	}
}
