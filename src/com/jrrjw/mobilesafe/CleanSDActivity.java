package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CleanSDActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("�÷���δʵ��......");
		tv.setHorizontalScrollBarEnabled(true);
		setContentView(tv);
		// File file = Environment.getExternalStorageDirectory();
		// File[] files = file.listFiles();
		// for (File f : files) {
		// String name = f.getName();
		// if (f.isDirectory()) {
		// System.out.println(name);
		// // ��ѯ����ļ��������Ƿ������ݿ�������� ������� ��ʾ�û����ļ���ɾ���ˡ�
		// }
		// }
	}
}
