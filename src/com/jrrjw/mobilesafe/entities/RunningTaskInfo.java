package com.jrrjw.mobilesafe.entities;

import android.graphics.drawable.Drawable;

public class RunningTaskInfo {

	public Drawable icon;
	public String title;
	public String packageName;
	public boolean userApp; // 用户应用为1
	public long taskSize;
	public boolean isChecked;

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

	public long getTaskSize() {
		return taskSize;
	}

	public void setTaskSize(long taskSize) {
		this.taskSize = taskSize;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String size;
}
