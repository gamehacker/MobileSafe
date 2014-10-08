package com.jrrjw.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.jrrjw.mobilesafe.entities.SmsInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	public interface Progress {

		public void OnBackUp(int progress);

		public void beforeBackUp(int Max);

	}

	public static List<SmsInfo> smsRecover(Context context, Progress p)
			throws Exception {
		Uri uri = Uri.parse("content://sms");
		List<SmsInfo> infos = null;
		SmsInfo info = null;
		File file = new File(Environment.getExternalStorageDirectory(),
				"back.xml");
		InputStream is = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");
		while (true) {
			switch (parser.next()) {
			case XmlPullParser.START_DOCUMENT:
				infos = new ArrayList<SmsInfo>();
				break;
			case XmlPullParser.START_TAG:
				if ("sms".equals(parser.getName())) {
					info = new SmsInfo();
				} else if ("address".equals(parser.getName())) {
					info.setAddress(parser.getText());

				} else if ("date".equals(parser.getName())) {
					info.setDate(parser.getText());

				} else if ("type".equals(parser.getName())) {
					info.setType(parser.getText());

				} else if ("body".equals(parser.getName())) {
					info.setBody(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("sms".equals(parser.getName())) {
					infos.add(info);
				}
				break;
			case XmlPullParser.END_DOCUMENT:

				break;

			default:
				break;
			}
			return infos;
		}

	}

	// ±¸·Ý¶ÌÐÅ
	public static void smsBackUp(Context context, Progress p) throws Exception {
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "address", "date", "type", "body" }, null, null,
				null);

		File file = new File(Environment.getExternalStorageDirectory(),
				"back.xml");
		OutputStream os = new FileOutputStream(file);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(os, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		int Max = cursor.getColumnCount();
		p.beforeBackUp(Max);
		while (cursor.moveToNext()) {
			serializer.startTag(null, "sms");

			serializer.startTag(null, "address");
			serializer.text(cursor.getString(0));
			serializer.endTag(null, "address");

			serializer.startTag(null, "date");
			serializer.text(cursor.getString(1));
			serializer.endTag(null, "date");

			serializer.startTag(null, "type");
			serializer.text(cursor.getString(2));
			serializer.endTag(null, "type");

			serializer.startTag(null, "body");
			serializer.text(cursor.getString(3));
			serializer.endTag(null, "body");

			serializer.endTag(null, "sms");
			p.OnBackUp(cursor.getPosition());
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();

	}

}
