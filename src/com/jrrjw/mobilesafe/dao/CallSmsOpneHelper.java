package com.jrrjw.mobilesafe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CallSmsOpneHelper extends SQLiteOpenHelper {

	public CallSmsOpneHelper(Context context) {
		super(context, "blackname.db", null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blackname (id Integer primary key autoincrement,   phone varchar(20), name varchar(20), mode char(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
