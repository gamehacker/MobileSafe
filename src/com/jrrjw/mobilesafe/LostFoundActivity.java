package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �ֻ�������ҳ��
 * 
 * @author Administrator
 * 
 */
public class LostFoundActivity extends Activity {

	private SharedPreferences sp;
	private TextView tv_reset, tv_lostfound_safenumber;
	private ImageView iv_lostfound_islock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȡ���ͱ�����ص�����
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean islock = sp.getBoolean("lock", false);
		if (!islock) {
			Intent intent = new Intent(this, LockSet1Activity.class);
			startActivity(intent);
			finish();
		}
		setContentView(R.layout.activity_lost_found);
		tv_reset = (TextView) findViewById(R.id.tv_reset);
		tv_lostfound_safenumber = (TextView) findViewById(R.id.tv_lostfound_safenumber);
		iv_lostfound_islock = (ImageView) findViewById(R.id.iv_lostfound_islock);
		boolean isProtect = sp.getBoolean("protect", false);
		if (isProtect) {
			iv_lostfound_islock.setImageResource(R.drawable.lock);
		} else {
			iv_lostfound_islock.setImageResource(R.drawable.unlock);
		}
		String safenumber = sp.getString("safenumber", "");
		if (!TextUtils.isEmpty(safenumber)) {
			tv_lostfound_safenumber.setText(safenumber);
		}
		// ���½��з���������
		tv_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp = getSharedPreferences("config", MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putBoolean("lock", false);
				editor.putBoolean("protect", false);
				editor.commit();
				Intent intent = new Intent(LostFoundActivity.this,
						LockSet1Activity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.top_in, R.anim.top_out);
				finish();
			}
		});

	}
}
