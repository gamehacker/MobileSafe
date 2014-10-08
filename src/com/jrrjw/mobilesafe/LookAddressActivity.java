package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jrrjw.mobilesafe.utils.QueryAddressFromSql;

/**
 * 归属地查询，只能查询11位手机号码
 * @author Jiang
 *
 */
public class LookAddressActivity extends Activity {

	EditText etLookaddressPhone;
	TextView tvLookaddressInfo;
	Button btLookaddressSubmit;
	Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look_address);
		etLookaddressPhone = (EditText) findViewById(R.id.et_lookaddress_phone);
		btLookaddressSubmit = (Button) findViewById(R.id.bt_lookaddress_submit);
		tvLookaddressInfo = (TextView) findViewById(R.id.tv_lookaddress_info);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		btLookaddressSubmit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = etLookaddressPhone.getText().toString().trim();
				if (number.length() != 11) {
					Toast.makeText(LookAddressActivity.this, "号码不存在", 0).show();
					Animation shake = AnimationUtils.loadAnimation(
							LookAddressActivity.this, R.anim.shake);
					etLookaddressPhone.startAnimation(shake);
					return;
				}

				if (TextUtils.isEmpty(number)) {
					Toast.makeText(LookAddressActivity.this, "号码不能为空", 0)
							.show();
					Animation shake = AnimationUtils.loadAnimation(
							LookAddressActivity.this, R.anim.shake);
					etLookaddressPhone.startAnimation(shake);
					return;
				}

				getAddress(number);

			}
		});

	}

	public void getAddress(String number) {

		String value = QueryAddressFromSql.getAddress(LookAddressActivity.this,
				number);
		if (value == null) {
			Toast.makeText(LookAddressActivity.this, "号码不存在", 0).show();
			Animation shake = AnimationUtils.loadAnimation(
					LookAddressActivity.this, R.anim.shake);
			etLookaddressPhone.startAnimation(shake);
			long[] pattern = { 200, 200, 300, 300, 1000, 2000 };
			vibrator.vibrate(pattern, -1);
		}
		tvLookaddressInfo.setText(value);
	}

}
