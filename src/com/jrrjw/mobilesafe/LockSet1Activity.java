package com.jrrjw.mobilesafe;

import android.content.Intent;
import android.os.Bundle;

/**
 * 手机防盗的第一个向导页面
 * @author Administrator
 *
 */
public class LockSet1Activity extends BaseTouchActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_set1);
	}

	public void showNext() {
		Intent intent = new Intent(this, LockSet2Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		finish();
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		finish();

	}

}
