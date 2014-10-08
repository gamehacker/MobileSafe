package com.jrrjw.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jrrjw.mobilesafe.entities.SmsInfo;
import com.jrrjw.mobilesafe.utils.SmsUtils;
import com.jrrjw.mobilesafe.utils.SmsUtils.Progress;

/**
 * 该类实现了高级功能页面的显示
 * @author JiangYueSong
 *
 */
public class AdvenceToolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advence_tools);
	}

	/**
	 * 程序锁设置
	 * @param v
	 */
	public void llAppLock(View v) {
		startActivity(new Intent(AdvenceToolsActivity.this,
				AppLockActivity.class));
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}

	/**
	 * 开启归属地设置
	 * @param v
	 */
	public void llLookAddress(View v) {
		Intent intent = new Intent(this, LookAddressActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}

	/**
	 * 恢复短信功能
	 * 在恢复短信时会有可能覆盖掉原来短信的内容，这里只是演示，未做处理
	 * @param v
	 */
	public void llSmsRecover(View v) {

		//定义进度条对话框
		final ProgressDialog dialog = new ProgressDialog(this);  
		//设置进度条对话框样式
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//设置标题
		dialog.setTitle("恢复中");
		dialog.show();

		new Thread(new Runnable() {
			public void run() {
				// 开启新线程进行短信备份
				try {
					List<SmsInfo> infos = SmsUtils.smsRecover(
							AdvenceToolsActivity.this, new Progress() {
								@Override
								public void beforeBackUp(int Max) {
									dialog.setMax(Max);
								}
								@Override
								public void OnBackUp(int progress) {
									dialog.setProgress(progress);
								}
							});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvenceToolsActivity.this, "恢复成功", 0)
									.show();
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvenceToolsActivity.this, "恢复失败", 0)
									.show();
						}
					});
				}

			}
		}).start();

	}

	/**
	 * 备份短信
	 * @param v
	 */
	public void llSmsBackup(View v) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("备份中");
		dialog.show();
		new Thread(new Runnable() {
			public void run() {
				// 开启新线程进行短信备份
				try {
					SmsUtils.smsBackUp(AdvenceToolsActivity.this,
							new Progress() {
								@Override
								public void beforeBackUp(int Max) {
									dialog.setMax(Max);
								}
								@Override
								public void OnBackUp(int progress) {
									dialog.setProgress(progress);
								}
							});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvenceToolsActivity.this, "备份成功", 0)
									.show();
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvenceToolsActivity.this, "失败", 0)
									.show();
						}
					});
				}
			}
		}).start();

	}
}
