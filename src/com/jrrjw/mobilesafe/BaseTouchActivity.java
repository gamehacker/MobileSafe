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
 * ����ʵ�������ƴ����Ĺ���
 * ���ֻ���������ҳ��ʹ��
 * @author Administrator
 *
 */
public abstract class BaseTouchActivity extends Activity {
	public GestureDetector detector;  //��������̽����
	protected SharedPreferences sp; //����SP
	protected TextView tv_next; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);  //���sp

		// ʵ��������̽���������ü���
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			/*
			 * �������ƻ���ʱ�������÷���
			 * */
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
					//	�ж��Ƿ�Ϊ����������
				if (e1.getRawX() - e2.getRawX() > 150) {
					// �����ƶ�
					showNext();
					return true;
				} else if (e2.getRawX() - e1.getRawX() > 200) {
					// �����ƶ�
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
