package com.jrrjw.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jrrjw.mobilesafe.R;
import com.jrrjw.mobilesafe.SettingActivity;
import com.jrrjw.mobilesafe.utils.QueryAddressFromSql;
import com.jrrjw.mobilesafe.utils.ServiceUtils;

/**
 * 来去点的实现显示归属地信息服务
 * @author JIang
 *
 */
public class TelAddressService extends Service {
	TelephonyManager tm;
	SharedPreferences sp;
	boolean state;
	MyComingPhone comingPhone;
	private boolean isServiceRunning;
	private View view = null;
	WindowManager wm;
	private WindowManager.LayoutParams params;
	private TelOutAddressRecever receiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 启动服务，然后启动广播接收者 1. 启动来电监听的服务
	 */
	public void onCreate() {
		super.onCreate();

		sp = getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// 注册去电的时候
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		receiver = new TelOutAddressRecever();
		registerReceiver(receiver, filter);

		isServiceRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.TelAddressService");
		comingPhone = new MyComingPhone();
		tm.listen(comingPhone, PhoneStateListener.LISTEN_CALL_STATE);

	}

	public void toast(String phone) {
		view = View.inflate(this, R.layout.toastview, null);
		view.setOnTouchListener(new OnTouchListener() {

			private float startX;
			private float startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getRawX();
					startY = event.getRawY();

					break;

				case MotionEvent.ACTION_MOVE:
					float moveX = event.getRawX();
					float moveY = event.getRawY();

					int dx = (int) (moveX - startX);
					int dy = (int) (moveY - startY);

					params.x += dx;
					params.y += dy;

					// if (params.x < 0) {
					// params.x = 0;
					// }
					// if (params.y < 0) {
					// params.y = 0;
					// }
					// if (params.x > (wm.getDefaultDisplay().getWidth())
					// - view.getWidth()) {
					// params.x = (wm.getDefaultDisplay().getWidth())
					// - view.getWidth();
					//
					// }
					// if (params.y > (wm.getDefaultDisplay().getHeight())
					// - view.getHeight()) {
					//
					// params.y = (wm.getDefaultDisplay().getHeight())
					// - view.getHeight();
					//
					// }

					wm.updateViewLayout(view, params);
					startX = event.getRawX();
					startY = event.getRawY();
					break;

				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("tel_lastx", params.x);
					editor.putInt("tel_lasty", params.y);
					editor.commit();
					break;

				default:
					break;
				}

				return false;
			}
		});

		TextView tv = (TextView) view.findViewById(R.id.tv_tel_address_show);
		tv.setText(QueryAddressFromSql.getAddress(this, phone));

		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
		params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

		int x = sp.getInt("tel_lastx", 0);
		int y = sp.getInt("tel_lasty", 0);
		params.x = x;
		params.y = y;

		wm.addView(view, params);
		return;
	}

	class TelOutAddressRecever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String number = getResultData();
			toast(number);

		}

	}

	private class MyComingPhone extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			System.out.println(state + incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// 响铃时候 , 显示来电归属地信息
				toast(incomingNumber);

				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null) {
					wm.removeView(view);
					view = null;
				}

				break;

			default:
				break;
			}

		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (isServiceRunning) {
			tm.listen(comingPhone, PhoneStateListener.LISTEN_NONE);
			comingPhone = null;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}
}
