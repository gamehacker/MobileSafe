package com.jrrjw.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jrrjw.mobilesafe.dao.BlackNameInfo;
import com.jrrjw.mobilesafe.dao.CallSmsSqliteUtils;
import com.jrrjw.mobilesafe.service.LockAppService;
import com.jrrjw.mobilesafe.service.TelAddressService;
import com.jrrjw.mobilesafe.utils.ServiceUtils;

/**
 * ����������
 * @author JiangYueSong
 *
 */
public class CallSmsActivity extends Activity {

	public static final String TAG = null;
	ListView lv_blackname;
	CallSmsAdapter adapter;
	List<BlackNameInfo> infos = null;
	CallSmsSqliteUtils callSmsSqliteUtils;
	private View v_add;
	LinearLayout ll_load;
	Button btn_add;
	int start = 0;
	int Max = 0;
	int count = 10;
	MyOnSrollListener onSrollListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms);
		lv_blackname = (ListView) findViewById(R.id.lv_callsms_blackname);
		ll_load = (LinearLayout) findViewById(R.id.ll_callsms_load);
		btn_add = (Button) findViewById(R.id.bt_callsms_add);
		callSmsSqliteUtils = new CallSmsSqliteUtils(this);
		adapter = new CallSmsAdapter();

		//�жϷ�����û������
		boolean isAppLockRunning = ServiceUtils.isServicRunning(this,
				"com.jrrjw.mobilesafe.service.CallSmsService");

		if (!isAppLockRunning) {
			Toast.makeText(this, "������δ���������������п���", Toast.LENGTH_LONG).show();
		}
		//��ʾ���ڼ��صĽ�����
		ll_load.setVisibility(View.VISIBLE);
		lv_blackname.setVisibility(View.GONE);
		btn_add.setVisibility(View.GONE);
		if (infos == null) {
			new Thread(new Runnable() {
				public void run() {
					Max = callSmsSqliteUtils.getCount();
					infos = new ArrayList<BlackNameInfo>();
					//���list����
					if (Max != 0) {
						infos.addAll(callSmsSqliteUtils.getAllInfoPart(start,
								count));
						runOnUiThread(new Runnable() {
							public void run() {
								btn_add.setVisibility(View.VISIBLE);
								ll_load.setVisibility(View.GONE);
								lv_blackname.setVisibility(View.VISIBLE);
								lv_blackname.setAdapter(adapter);
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								btn_add.setVisibility(View.VISIBLE);
								ll_load.setVisibility(View.GONE);
								lv_blackname.setVisibility(View.VISIBLE);
								Toast.makeText(CallSmsActivity.this,
										"��ǰû�к�������������", 0).show();
								lv_blackname.setAdapter(adapter);
							}
						});
					}
				}
			}).start();

		}
		// lv_blackname.setAdapter(adapter);
		onSrollListener = new MyOnSrollListener();
		lv_blackname.setOnScrollListener(onSrollListener);
	}

	/**
	 * 
	 * ��������
	 * @author Jiang
	 *
	 */
	class MyOnSrollListener implements OnScrollListener {

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (lv_blackname.getLastVisiblePosition() < (Max - 1)
						&& lv_blackname.getLastVisiblePosition() == (infos
								.size() - 1)) {
					lv_blackname.setVisibility(View.GONE);
					ll_load.setVisibility(View.VISIBLE);
					btn_add.setVisibility(View.GONE);
					new Thread(new Runnable() {
						public void run() {
							start += count;
							infos.addAll(callSmsSqliteUtils.getAllInfoPart(
									start, count));
							runOnUiThread(new Runnable() {
								public void run() {
									btn_add.setVisibility(View.VISIBLE);
									ll_load.setVisibility(View.GONE);
									lv_blackname.setVisibility(View.VISIBLE);
									adapter.notifyDataSetChanged();
								}
							});
						}
					}).start();

				}

				break;

			default:
				break;
			}

		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

	}

	public void hisSms(View v) {

	}

	public void hisCall(View v) {

	}

	public void setBack(View v) {
		finish();
	}

	/**
	 * ������ʱ���Ķ���
	 * @param view
	 */
	public void add(View view) {
		//�����Զ���Ի���
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("��Ӻ�����");
		v_add = View.inflate(this, R.layout.setaddblackname, null);
		builder.setView(v_add);
		//����ȷ����ť��Ҫ������
		builder.setPositiveButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText phone = (EditText) v_add
						.findViewById(R.id.et_callsms_phone);
				EditText name = (EditText) v_add
						.findViewById(R.id.et_callsms_name);
				CheckBox cb_call = (CheckBox) v_add
						.findViewById(R.id.cb_callsms_call);
				CheckBox cb_sms = (CheckBox) v_add
						.findViewById(R.id.cb_callsms_sms);

				String number = phone.getText().toString().trim();
				String name1 = name.getText().toString().trim();
				boolean call = cb_call.isChecked();
				boolean sms = cb_sms.isChecked();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(CallSmsActivity.this, "���벻��Ϊ��", 0).show();
					return;
				}
				if (callSmsSqliteUtils.getDataByPhone(CallSmsActivity.this,
						number)) {
					Toast.makeText(CallSmsActivity.this, "�����Ѵ���", 0).show();
					return;
				}
				if (call == false && sms == false) {
					Toast.makeText(CallSmsActivity.this, "������������ѡ��һ��", 0)
							.show();
					return;
				}

				// �������ݿ�
				BlackNameInfo info = new BlackNameInfo();
				info.setPhone(number);
				info.setName(name1);
				if (call == true && sms == true) {
					info.setMode("3");
				} else if (sms == true) {
					info.setMode("2");
				} else if (call == true) {
					info.setMode("1");
				}
				callSmsSqliteUtils.insertInfo(info);
				Max = callSmsSqliteUtils.getCount();
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				System.out.println(infos.size() + ".....");
				Toast.makeText(CallSmsActivity.this, "��ӳɹ�", 0).show();
			}
		});
		//����ȡ��������
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		builder.show();
	}

	/**
	 * ������
	 * @author Jiang
	 *
	 */
	private class CallSmsAdapter extends BaseAdapter {
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewInfo holder;
			// 1.�����ڴ���view���󴴽��ĸ���
			if (convertView == null) {
				Log.i(TAG, "�����µ�view����" + position);
				// ��һ�������ļ�ת���� view����
				view = View.inflate(getApplicationContext(),
						R.layout.callsmsitem, null);
				// 2.�����Ӻ��Ӳ�ѯ�Ĵ��� �ڴ��ж���ĵ�ַ��
				holder = new ViewInfo();
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_callsms_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_callsms_mode);
				holder.iv_del = (ImageView) view
						.findViewById(R.id.iv_callsms_del);
				// ��������������ʱ���ҵ����ǵ����ã�����ڼ��±������ڸ��׵Ŀڴ�
				view.setTag(holder);
			} else {
				Log.i(TAG, "��������ʷ��view���󣬸�����ʷ�����view����" + position);
				view = convertView;
				holder = (ViewInfo) view.getTag();// 5%
			}
			holder.tv_phone.setText(infos.get(position).getPhone());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("�绰����");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("��������");
			} else {
				holder.tv_mode.setText("ȫ������");
			}
			holder.iv_del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(
							CallSmsActivity.this);
					builder.setTitle("����");
					builder.setMessage("ȷ��Ҫɾ��������¼ô��");
					builder.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// ɾ�����ݿ������
									callSmsSqliteUtils.deleteInfo(infos.get(
											position).getPhone());
									// ���½��档
									infos.remove(position);
									Max = callSmsSqliteUtils.getCount();
									// ֪ͨlistview��������������
									adapter.notifyDataSetChanged();
								}
							});
					builder.setNegativeButton("ȡ��", null);
					builder.show();
				}
			});

			return view;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

	}

	class ViewInfo {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_del;
	}

}
