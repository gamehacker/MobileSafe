package com.jrrjw.mobilesafe.utils;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryAddressFromSql {
	private static SQLiteDatabase database;

	private static String path = "/data/data/com.jrrjw.mobilesafe/files/address.db";

	public static String getAddress(Context context, String number) {

		String value = null;
		if (number.matches("^1[34568]\\d{9}$")) {
			String cut_number = number.substring(0, 7);
			database = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);

			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?) ",
							new String[] { cut_number });
			while (cursor.moveToNext()) {
				value = cursor.getString(0);
			}
			cursor.close();
			database.close();

		}

		return value;

	}
}
