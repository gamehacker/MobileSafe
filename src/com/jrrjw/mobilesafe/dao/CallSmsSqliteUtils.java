package com.jrrjw.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

public class CallSmsSqliteUtils {
	CallSmsOpneHelper opneHelper;

	public CallSmsSqliteUtils(Context context) {
		opneHelper = new CallSmsOpneHelper(context);
	}

	public boolean getDataByPhone(Context context, String phone) {
		boolean isTrue = false;
		SQLiteDatabase database = opneHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery(
				"select name from blackname where phone = ? ",
				new String[] { phone });
		if (cursor.moveToNext()) {
			isTrue = true;
		}
		database.close();
		cursor = null;
		return isTrue;

	}

	public String getData(Context context, String phone) {
		SQLiteDatabase database = opneHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery(
				"select mode from blackname where phone = ? ",
				new String[] { phone });
		if (cursor.moveToNext()) {
			return cursor.getString(0).toString();
		}
		database.close();
		cursor = null;
		return null;

	}

	public int getCount() {
		SQLiteDatabase database = opneHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select count(*) from blackname",
				null);
		while (cursor.moveToNext()) {
			return cursor.getInt(0);

		}
		return 0;
	}

	public List<BlackNameInfo> getAllInfoPart(int start, int count) {
		SystemClock.sleep(1000);
		SQLiteDatabase database = opneHelper.getReadableDatabase();
		Cursor cursor = database
				.rawQuery(
						"select phone,name,mode from blackname order by id desc  limit ? offset ?",
						new String[] { String.valueOf(count),
								String.valueOf(start) });
		List<BlackNameInfo> infos = new ArrayList<BlackNameInfo>();
		while (cursor.moveToNext()) {
			BlackNameInfo info = new BlackNameInfo();
			info.setPhone(cursor.getString(0));
			info.setName(cursor.getString(1));
			info.setMode(cursor.getString(2));
			infos.add(info);
		}
		cursor = null;
		database.close();
		return infos;

	}

	public List<BlackNameInfo> getAllInfo() {
		SQLiteDatabase database = opneHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery(
				"select phone,name,mode from blackname order by id desc", null);
		List<BlackNameInfo> infos = new ArrayList<BlackNameInfo>();
		while (cursor.moveToNext()) {
			BlackNameInfo info = new BlackNameInfo();
			info.setPhone(cursor.getString(0));
			info.setName(cursor.getString(1));
			info.setMode(cursor.getString(2));
			infos.add(info);
		}
		cursor = null;
		database.close();
		return infos;

	}

	public void deleteInfo(String phone) {
		SQLiteDatabase database = opneHelper.getWritableDatabase();
		database.delete("blackname", "phone=?", new String[] { phone });
		database.close();

	}

	public void insertInfo(BlackNameInfo info) {
		String phone = info.getPhone();
		String name = info.getName();
		String mode = info.getMode();

		SQLiteDatabase database = opneHelper.getWritableDatabase();

		database.execSQL(
				"insert into blackname(phone,name,mode) values(?,?,?);",
				new String[] { phone, name, mode });

		database.close();

	}

}
