package com.jrrjw.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 选择联系人的页面，可能会出现问题，未解决
 * 
 * @author Administrator
 * 
 */
public class SelectContactActivity extends Activity {
	private ListView lv_contact;
	private List<Map<String, String>> data;
	SimpleAdapter simpleAdapter;
	ContentResolver cr;
	MyAdapter myAdapter;
	List<Long> ids;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		cr = this.getContentResolver();
		ids = loadAllContactIds();
		myAdapter = new MyAdapter(ids);

		lv_contact.setAdapter(myAdapter);

		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = loadAllContactInfo(ids.get(position));
				String[] NPs = phone.split("\\|");
				Intent intent = new Intent(SelectContactActivity.this,
						LockSet3Activity.class);
				intent.putExtra("phone", NPs[0]);
				setResult(0, intent);
				finish();
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		private List<Long> ids;
		private Map<Long, String> maps;

		public MyAdapter(List<Long> ids) {
			this.ids = ids;
			maps = new HashMap<Long, String>();
		}

		@Override
		public int getCount() {
			return this.ids.size();
		}

		@Override
		public Object getItem(int position) {
			return this.ids.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			if (null == convertView) {

				v = View.inflate(SelectContactActivity.this,
						R.layout.listcontact, null);
			} else {
				v = convertView;
			}

			TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) v.findViewById(R.id.tv_phone);

			long id = ids.get(position);
			String data = loadAllContactInfo(id);

			String[] NPs = data.split("\\|");

			tv_name.setText(NPs[1]);

			tv_phone.setText(NPs[0]);

			return v;

		}
	}

	private List<Long> loadAllContactIds() {
		List<Long> arr = new ArrayList<Long>();
		Cursor cursor = cr
				.query(ContactsContract.RawContacts.CONTENT_URI, null,
						ContactsContract.RawContacts.DELETED + " = 0", null,
						null);
		if (null != cursor && cursor.moveToFirst()) {
			do {
				long id = cursor
						.getLong(cursor
								.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
				arr.add(id);
			} while (cursor.moveToNext());
			cursor.close();
		}

		return arr;
	}

	private String loadAllContactInfo(long id) {
		Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null,
				ContactsContract.Data.RAW_CONTACT_ID + " = " + id, null, null);
		StringBuffer sb = new StringBuffer();
		if (null != cursor && cursor.moveToFirst()) {
			do {
				String type = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Data.MIMETYPE));

//				if (type.equals("vnd.android.cursor.item/phone_v2")) {
//					sb.append(
//							cursor.getString(cursor.getColumnIndex(Event.DATA1)))
//							.append("|");
//				} else if (type.equals("vnd.android.cursor.item/name")) {
//					sb.append(
//							cursor.getString(cursor.getColumnIndex(Event.DATA1)))
//							.append("|");
//				}

				 if (type.equals(StructuredName.CONTENT_ITEM_TYPE)) {
				 sb.append(
				 cursor.getString(cursor
				 .getColumnIndex(StructuredName.DISPLAY_NAME)))
				 .append("|");
				 } else if (type.equals(Nickname.CONTENT_ITEM_TYPE)) {
				 sb.append(
				 cursor.getString(cursor
				 .getColumnIndex(Nickname.NAME)))
				 .append("|");
				 } else if (type.equals(Phone.CONTENT_ITEM_TYPE)) {
				 sb.append(
				 cursor.getString(cursor
				 .getColumnIndex(Phone.NUMBER))).append("|");
				 } else if (type.equals(Event.CONTENT_ITEM_TYPE)
				 && Event.TYPE.equals(Event.TYPE_BIRTHDAY)) {
				 sb.append(
				 cursor.getString(cursor.getColumnIndex(Event.DATA1)))
				 .append("|");
				 }
			} while (cursor.moveToNext());
			cursor.close();
		}
		return sb.toString();
	}

}
