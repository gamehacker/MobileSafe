package com.jrrjw.mobilesafe.entities;

import android.graphics.drawable.Drawable;

public class SoftInfo {

	public Drawable icon;
	public String title;
	public String packageName;
	public boolean userApp; // �û�Ӧ��Ϊ1
	public int state; // sd�����Ϊ1

	public boolean getUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String size;
}
