package com.jrrjw.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockSqliteUtils {
	AppLockOpneHelper opneHelper;
	Context context;

	public AppLockSqliteUtils(Context context) {
		opneHelper = new AppLockOpneHelper(context);
		this.context = context;
	}

	public List<AppLockInfo> getAll(Context context) {
		List<AppLockInfo> appLockInfos = new ArrayList<AppLockInfo>();
		SQLiteDatabase database = opneHelper.getReadableDatabase();
		Cursor cursor = database.query("applock", new String[] { "name",
				"packagename" }, null, null, null, null, null);
		while (cursor.moveToNext()) {
			AppLockInfo appLockInfo = new AppLockInfo();
			appLockInfo.setName(cursor.getString(0).toString());
			appLockInfo.setPackageName(cursor.getString(1).toString());
			appLockInfos.add(appLockInfo);
		}
		database.close();
		cursor = null;
		return appLockInfos;

	}

	public void sendIntent() {
		Intent intent = new Intent();
		intent.setAction("com.jrrjw.mobilesafe.databasechange");
		context.sendBroadcast(intent);
	}

	public void deleteInfo(String packageName) {
		SQLiteDatabase database = opneHelper.getWritableDatabase();
		database.delete("applock", "packagename=?",
				new String[] { packageName });
		database.close();
		sendIntent();

	}

	public void insertInfo(AppLockInfo info) {
		String packageName = info.getPackageName();
		String name = info.getName();

		SQLiteDatabase database = opneHelper.getWritableDatabase();

		database.execSQL("insert into applock(name,packagename) values(?,?);",
				new String[] { name, packageName });
		database.close();
		sendIntent();

	}

}
