package com.jrrjw.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrrjw.mobilesafe.R;

public class SettingItem extends RelativeLayout {
	TextView tv_name, tv_info;
	CheckBox cb_check;
	private String des_on;
	private String des_off;
	private String title;

	public SettingItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		View v = View.inflate(context, R.layout.settingitem, SettingItem.this);
		tv_name = (TextView) v.findViewById(R.id.tv_name);
		tv_info = (TextView) v.findViewById(R.id.tv_tip);
		cb_check = (CheckBox) v.findViewById(R.id.cb_check);

	}

	public boolean getCheckedState() {
		return cb_check.isChecked();
	}

	public void setCheck(boolean check) {
		if (check) {
			tv_info.setText(des_on);
		} else {
			tv_info.setText(des_off);
		}
		cb_check.setChecked(check);
	}

	public void setDesc(String value) {
		tv_info.setText(value);
	}

	public SettingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
		title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.jrrjw.mobilesafe",
				"ttitle");
		des_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.jrrjw.mobilesafe",
				"des_on");
		des_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.jrrjw.mobilesafe",
				"des_off");

		tv_name.setText(title);
		tv_info.setText(des_off);
	}

	public SettingItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

}
