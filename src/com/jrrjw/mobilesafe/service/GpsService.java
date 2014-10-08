package com.jrrjw.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 获得用户的位置服务
 * @author Jiang
 *
 */
public class GpsService extends Service {
	private LocationManager lm;
	private SharedPreferences sp;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);

		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		String provider = lm.getBestProvider(criteria, true);

		listener = new MyListener();
		lm.requestLocationUpdates(provider, 6000, 0, listener);
	}

	private class MyListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			String longitude = "经度：" + location.getLongitude();
			String latitude = "纬度：" + location.getLatitude();
			String accuracy = "精确度：" + location.getAccuracy();
			StringBuffer sb = new StringBuffer();
			sb.append(longitude);
			sb.append(latitude);
			sb.append(accuracy);
			Editor editor = sp.edit();
			editor.putString("location", sb.toString());
			editor.commit();

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;

	}
}
