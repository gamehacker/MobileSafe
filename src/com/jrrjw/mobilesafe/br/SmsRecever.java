package com.jrrjw.mobilesafe.br;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.jrrjw.mobilesafe.R;
import com.jrrjw.mobilesafe.service.GpsService;

/**
 * ���ż���������
 * @author Jiang
 *
 */
public class SmsRecever extends BroadcastReceiver {
	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		// д���ն��ŵĴ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		for (Object b : objs) {
			// �����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			// ������
			String sender = sms.getOriginatingAddress();// 15555555556
			String safenumber = sp.getString("safenumber", "");// 5556
			String body = sms.getMessageBody();
			System.out.println(safenumber+"��ȫ����");

			// ȱ���жϺ����ҵ������
			if ("#*location*#".equals(body)) {
				// �õ��ֻ���GPS\
				System.out.println("getting location ...");
				Intent intent2 = new Intent(context, GpsService.class);
				context.startService(intent2);
				String location = sp.getString("location", "");
				if (TextUtils.isEmpty(location)) {
					System.out.println("getting location ...");
					SmsManager.getDefault().sendTextMessage(safenumber, null,
							"getting location ...", null, null);
				}
				while (true) {
					if (!TextUtils.isEmpty(location)) {
						System.out.println("location:" + location);
						SmsManager.getDefault().sendTextMessage(safenumber,
								null, "location:" + location, null, null);
						break;
					}
				}

				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				// ���ű���Ӱ��
				System.out.println(" alert");
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
				player.setLooping(false);//
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			} else if ("#*wipedata*#".equals(body)) {
				// Զ���������
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(body)) {
				// Զ������
				
				abortBroadcast();
			}
		}
	}
}
