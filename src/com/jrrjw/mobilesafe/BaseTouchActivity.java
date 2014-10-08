package com.jrrjw.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 该类实现了手势触摸的功能
 * 在手机防盗介绍页面使用
 * @author Administrator
 *
 */
public abstract class BaseTouchActivity extends Activity {
	public GestureDetector detector;  //定义手势探测器
	protected SharedPreferences sp; //定义SP
	protected TextView tv_next; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);  //获得sp

		// 实例化手势探测器并设置监听
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			/*
			 * 当有手势华东时，触发该方法
			 * */
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
					//	判断是否为横向触摸手势
				if (e1.getRawX() - e2.getRawX() > 150) {
					// 向右移动
					showNext();
					return true;
				} else if (e2.getRawX() - e1.getRawX() > 200) {
					// 向左移动
					showPre();
					return true;
				}

				return true;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void next(View v) {
		showNext();
	};

	public void pre(View v) {
		showPre();
	};

	public abstract void showPre();

	public abstract void showNext();

}
