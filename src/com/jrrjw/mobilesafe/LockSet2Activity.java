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
 * �ֻ������ĵڶ�����ҳ�棬��sim��
 * 
 * @author Administrator
 * 
 */
public class LockSet2Activity extends BaseTouchActivity {

	private SettingItem si_sim; // ������Զ���Ŀؼ�
	private String pre_sim;
	private TelephonyManager manager; // �绰������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_set2);
		manager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE); // ��õ绰������
		si_sim = (SettingItem) findViewById(R.id.si_sim);
		tv_next = (TextView) findViewById(R.id.bt_set2_next);
		pre_sim = sp.getString("sim", null);
		// �ж�sim������Ϣ��û�б��浽sp�ʵ�ֻ���
		if (pre_sim != null) {
			si_sim.setCheck(true);
		}
		si_sim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean state = si_sim.getCheckedState(); // ���ѡ���״̬
				if (state) {
					si_sim.setCheck(false);
				} else {
					si_sim.setCheck(true);
					// ���sim�������кţ���Ϊ�������е�sim�����ܻ�ȡ���ֻ��ŵģ������кſ��Ի�ȡ��������Ψһ�ģ����������ȡ���к�
					String number = manager.getSimSerialNumber();
					if (!TextUtils.isEmpty(number)) {
						Toast.makeText(LockSet2Activity.this,
								"SIM����Ϣ" + number, 0).show();
					} else {
						Toast.makeText(LockSet2Activity.this,
								"�޷���ȡSIM����Ϣ�������˳�", 0).show();
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
			Toast.makeText(LockSet2Activity.this, "SIM��δ�󶨣��޷�����", 0).show();
			return;
		}
		// ������ǿ���û�ѡ���sim����Ϣ
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
