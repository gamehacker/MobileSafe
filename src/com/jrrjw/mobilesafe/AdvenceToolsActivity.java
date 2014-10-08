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
 * ����ʵ���˸߼�����ҳ�����ʾ
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
	 * ����������
	 * @param v
	 */
	public void llAppLock(View v) {
		startActivity(new Intent(AdvenceToolsActivity.this,
				AppLockActivity.class));
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}

	/**
	 * ��������������
	 * @param v
	 */
	public void llLookAddress(View v) {
		Intent intent = new Intent(this, LookAddressActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.top_pre_in, R.anim.top_pre_out);
	}

	/**
	 * �ָ����Ź���
	 * �ڻָ�����ʱ���п��ܸ��ǵ�ԭ�����ŵ����ݣ�����ֻ����ʾ��δ������
	 * @param v
	 */
	public void llSmsRecover(View v) {

		//����������Ի���
		final ProgressDialog dialog = new ProgressDialog(this);  
		//���ý������Ի�����ʽ
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//���ñ���
		dialog.setTitle("�ָ���");
		dialog.show();

		new Thread(new Runnable() {
			public void run() {
				// �������߳̽��ж��ű���
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
							Toast.makeText(AdvenceToolsActivity.this, "�ָ��ɹ�", 0)
									.show();
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvenceToolsActivity.this, "�ָ�ʧ��", 0)
									.show();
						}
					});
				}

			}
		}).start();

	}

	/**
	 * ���ݶ���
	 * @param v
	 */
	public void llSmsBackup(View v) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("������");
		dialog.show();
		new Thread(new Runnable() {
			public void run() {
				// �������߳̽��ж��ű���
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
							Toast.makeText(AdvenceToolsActivity.this, "���ݳɹ�", 0)
									.show();
							dialog.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvenceToolsActivity.this, "ʧ��", 0)
									.show();
						}
					});
				}
			}
		}).start();

	}
}
