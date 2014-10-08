package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jrrjw.mobilesafe.service.CallSmsService;
import com.jrrjw.mobilesafe.service.LockAppService;
import com.jrrjw.mobilesafe.service.TelAddressService;
import com.jrrjw.mobilesafe.ui.SettingItem;
import com.jrrjw.mobilesafe.utils.ServiceUtils;

/**
 * 设置页面，进行通用功能的设置
 * @author Administrator
 *
 */
/**
 * @author Administrator
 * 
 */
public class SettingActivity extends Activity {
	SettingItem si_item = null;
	ImageView iv_back = null;
	private SharedPreferences sp = null;
	LinearLayout ll_tel_address;
	SettingItem si_tel_address, si_callsms_isopen, si_applock_isopen;
	private boolean isServiceRunning, isCallSmsServiceRunning,
			isAppLockRunning;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		si_item = (SettingItem) findViewById(R.id.si_item);
		iv_back = (ImageView) findViewById(R.id.iv_setting_back);

		si_tel_address = (SettingItem) findViewById(R.id.si_tel_address);
		si_callsms_isopen = (SettingItem) findViewById(R.id.si_callsms_isopen);
		si_applock_isopen = (SettingItem) findViewById(R.id.si_applock_isopen);

		//设置回显
		
		/**
		 * 判断一个服务是否开启不能通过sp保存数据来确定，要通过这个服务在系统中是否真的开启来判断
		 * 
		 */
		isAppLockRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.LockAppService");

		if (isAppLockRunning) {
			si_applock_isopen.setCheck(true);
		} else {
			si_applock_isopen.setCheck(false);
		}
		si_applock_isopen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isAppLockRunning = ServiceUtils.isServicRunning(
						SettingActivity.this,
						"com.jrrjw.mobilesafe.service.LockAppService");
				Intent intent = new Intent(SettingActivity.this,
						LockAppService.class);
				if (si_applock_isopen.getCheckedState()) {
					si_applock_isopen.setCheck(false);

					if (isAppLockRunning) {
						stopService(intent);
						System.out.println("stop service");
					}
				} else {
					si_applock_isopen.setCheck(true);
					if (isAppLockRunning == false) {
						startService(intent);
						System.out.println("start service");
					}
				}

			}
		});

		isCallSmsServiceRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.CallSmsService");

		if (isCallSmsServiceRunning) {
			si_callsms_isopen.setCheck(true);
		} else {
			si_callsms_isopen.setCheck(false);
		}

		si_callsms_isopen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isCallSmsServiceRunning = ServiceUtils.isServicRunning(
						SettingActivity.this,
						"com.jrrjw.mobilesafe.service.CallSmsService");
				Intent intent = new Intent(SettingActivity.this,
						CallSmsService.class);
				if (si_callsms_isopen.getCheckedState()) {
					si_callsms_isopen.setCheck(false);

					if (isCallSmsServiceRunning) {
						stopService(intent);
					}
				} else {
					si_callsms_isopen.setCheck(true);
					if (isCallSmsServiceRunning == false) {
						startService(intent);
					}
				}

			}
		});

		isServiceRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.TelAddressService");
		if (isServiceRunning) {
			si_tel_address.setCheck(true);
		} else {
			si_tel_address.setCheck(false);
		}
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				overridePendingTransition(R.anim.top_in, R.anim.top_out);
				finish();
			}
		});
		if (sp.getInt("update", 0) == 1) {
			si_item.setCheck(true);
		} else {
			si_item.setCheck(false);

		}

		si_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean ischecked = si_item.getCheckedState();
				Editor et = sp.edit();
				if (ischecked) {
					si_item.setCheck(false);
					et.putInt("update", 0);

				} else {
					si_item.setCheck(true);
					et.putInt("update", 1);
				}
				et.commit();

			}
		});

	}

	public void setBack(View v) {

		finish();
	}

	public void telClick(View v) {
		isServiceRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.TelAddressService");
		Intent intent = new Intent(SettingActivity.this,
				TelAddressService.class);
		if (si_tel_address.getCheckedState()) {
			si_tel_address.setCheck(false);

			if (isServiceRunning) {
				stopService(intent);
			}
		} else {
			si_tel_address.setCheck(true);
			if (isServiceRunning == false) {
				startService(intent);
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		isServiceRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.TelAddressService");
		if (isServiceRunning) {
			si_tel_address.setCheck(true);
		} else {
			si_tel_address.setCheck(false);
		}

	}

}
