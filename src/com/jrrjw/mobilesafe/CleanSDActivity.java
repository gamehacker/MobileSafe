package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CleanSDActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("该方法未实现......");
		tv.setHorizontalScrollBarEnabled(true);
		setContentView(tv);
		// File file = Environment.getExternalStorageDirectory();
		// File[] files = file.listFiles();
		// for (File f : files) {
		// String name = f.getName();
		// if (f.isDirectory()) {
		// System.out.println(name);
		// // 查询这个文件夹名称是否在数据库里面存在 如果存在 提示用户把文件夹删除了。
		// }
		// }
	}
}
