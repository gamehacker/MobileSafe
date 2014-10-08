package com.jrrjw.mobilesafe.br;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * ����sim����ʵ�ַ�����ʾ
 * @author Jiang
 *
 */
public class SimRecever extends BroadcastReceiver {
	private SharedPreferences sp;
	private TelephonyManager manager;

	@Override
	public void onReceive(Context context, Intent intent) {

		// ���ԭ��sp�洢����Ϣ
		sp = context.getSharedPreferences("config", 0);

		boolean isOpenLock = sp.getBoolean("lock", false);
		boolean isProtect = sp.getBoolean("protect", false);
		String safenumber = sp.getString("safenumber", "");
		String pre_sim = sp.getString("sim", null);

		if (isOpenLock) {
			if (isProtect) {
				manager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);

				// ������µ�sim����Ϣ  
				String new_sim = manager.getSimSerialNumber();

				if (!TextUtils.isEmpty(safenumber)) {
					if (pre_sim.equals(new_sim) == false) {
						SmsManager.getDefault().sendTextMessage(safenumber,
								null, "SIM changed ......", null, null);
					}
				}
			}

		}

	}
}
