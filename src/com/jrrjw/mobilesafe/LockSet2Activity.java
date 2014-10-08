package com.jrrjw.mobilesafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jrrjw.mobilesafe.ui.SettingItem;

/**
 * 手机防盗的第二个向导页面，绑定sim卡
 * 
 * @author Administrator
 * 
 */
public class LockSet2Activity extends BaseTouchActivity {

	private SettingItem si_sim; // 这个是自定义的控件
	private String pre_sim;
	private TelephonyManager manager; // 电话管理器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_set2);
		manager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE); // 获得电话管理器
		si_sim = (SettingItem) findViewById(R.id.si_sim);
		tv_next = (TextView) findViewById(R.id.bt_set2_next);
		pre_sim = sp.getString("sim", null);
		// 判断sim卡的信息有没有保存到sp里，实现回显
		if (pre_sim != null) {
			si_sim.setCheck(true);
		}
		si_sim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean state = si_sim.getCheckedState(); // 获得选项的状态
				if (state) {
					si_sim.setCheck(false);
				} else {
					si_sim.setCheck(true);
					// 获得sim卡的序列号，因为不是所有的sim卡都能获取到手机号的，而序列号可以获取，并且是唯一的，所以这里获取序列号
					String number = manager.getSimSerialNumber();
					if (!TextUtils.isEmpty(number)) {
						Toast.makeText(LockSet2Activity.this,
								"SIM卡信息" + number, 0).show();
					} else {
						Toast.makeText(LockSet2Activity.this,
								"无法获取SIM卡信息，建议退出", 0).show();
					}
				}
			}
		});
	}

	public void showPre() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, LockSet1Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		finish();
	}

	public void showNext() {
		// TODO Auto-generated method stub
		if (!si_sim.getCheckedState()) {
			Toast.makeText(LockSet2Activity.this, "SIM卡未绑定，无法继续", 0).show();
			return;
		}
		// 这里是强制用户选择绑定sim卡信息
		String number = manager.getSimSerialNumber();
		Editor editor = sp.edit();
		editor.putString("sim", number);
		editor.commit();

		Intent intent = new Intent(this, LockSet3Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		finish();
	}

}
