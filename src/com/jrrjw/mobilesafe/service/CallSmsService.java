package com.jrrjw.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.jrrjw.mobilesafe.dao.CallSmsSqliteUtils;

/**
 * ����������
 * @author jiANG
 *
 */
public class CallSmsService extends Service {

	CallSmsSqliteUtils callSmsSqliteUtils;
	SmsRecever smsRecever;
	TelephonyManager tm;
	MyListener phoneListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		callSmsSqliteUtils = new CallSmsSqliteUtils(CallSmsService.this);
		smsRecever = new SmsRecever();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		registerReceiver(smsRecever, filter);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		phoneListener = new MyListener();
		tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	private class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:

				String mode = callSmsSqliteUtils.getData(CallSmsService.this,
						incomingNumber);
				if (mode != null) {
					if ("1".equals(mode) || "3".equals(mode)) {
						Uri uri = Uri.parse("content://call_log/calls");
						MyObserver observer = new MyObserver(incomingNumber,
								new Handler());
						getContentResolver().registerContentObserver(uri, true,
								observer);
						endCall();
						Toast.makeText(CallSmsService.this, "����������", 0).show();
					}
				}
				break;

			default:
				break;
			}

		}

		class MyObserver extends ContentObserver {
			String incomingNumber;

			public MyObserver(String incomingNumber, Handler handler) {
				super(handler);
				this.incomingNumber = incomingNumber;

			}

			@Override
			public void onChange(boolean selfChange) {
				delCallLog(incomingNumber);
				super.onChange(selfChange);

				getContentResolver().unregisterContentObserver(this);
			}

		}

		//ɾ������������ĵ绰��¼
		public void delCallLog(String number) {
			Uri uri = Uri.parse("content://call_log/calls");
			ContentResolver resolver = getContentResolver();
			resolver.delete(uri, "number=?", new String[] { number });
		}

		//ǿ�ƽ���ͨ��������android��׼�ã�ֻ��ͨ����������
		public void endCall() {
			try {
				// ����servicemanager���ֽ���
				Class clazz = CallSmsService.class.getClassLoader().loadClass(
						"android.os.ServiceManager");
				Method method = clazz.getDeclaredMethod("getService",
						String.class);
				IBinder ibinder = (IBinder) method.invoke(null,
						TELEPHONY_SERVICE);
				ITelephony.Stub.asInterface(ibinder).endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsRecever);
		tm.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
		phoneListener = null;

	}

	private class SmsRecever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objs) {
				SmsMessage smsMessage = SmsMessage
						.createFromPdu((byte[]) object);
				String phone = smsMessage.getOriginatingAddress();
				String mode = callSmsSqliteUtils.getData(CallSmsService.this,
						phone);
				if (mode != null) {
					if ("2".equals(mode) || "3".equals(mode)) {
						Toast.makeText(CallSmsService.this, "�����Ѿ�����", 1).show();
						abortBroadcast();
					}
				}

			}

		}
	}
}
