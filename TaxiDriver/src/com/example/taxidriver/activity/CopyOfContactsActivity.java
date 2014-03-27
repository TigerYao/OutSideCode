package com.example.taxidriver.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.taxidriver.R;
import com.example.taxidriver.bean.ViewHolder;
import com.example.taxidriver.tools.ContactsAdatpter;
import com.example.taxidriver.tools.TextUitls;

@SuppressLint("NewApi")
public class CopyOfContactsActivity extends Activity {

	Context mContext = null;

	/** ��ȡ��Phon���ֶ� **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** ��ϵ����ʾ���� **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** �绰���� **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** ͷ��ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** ��ϵ�˵�ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	/** ��ϵ������ **/
	private ArrayList<String> mContactsName = new ArrayList<String>();

	/** ��ϵ��ͷ�� **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	// /**��ϵ��ͷ��**/
	private ArrayList<String> mCheckedNums = new ArrayList<String>();

	ListView mListView = null;
	ContactsAdatpter myAdapter = null;
	String mobile = null;
	SmsManager sms = SmsManager.getDefault();
	EditText input_num;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.contactsinfos);
		mContext = this;
		mListView = (ListView) findViewById(R.id.contactssid);
		input_num = (EditText) findViewById(R.id.input_num);
		/** �õ��ֻ�ͨѶ¼��ϵ����Ϣ **/
		getPhoneContacts();

		myAdapter = new ContactsAdatpter(mContactsName, mContactsNumber, this);
		mListView.setAdapter(myAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// ����ϵͳ��������绰
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.cb.toggle();// ��ÿ�λ�ȡ�����itemʱ�ı�checkbox��״̬
				ContactsAdatpter.isSelected.put(position, holder.cb.isChecked()); // ͬʱ�޸�map��ֵ����״̬
				String num = mContactsNumber.get(position);
				if (holder.cb.isChecked() && !mCheckedNums.contains(num)) {
					mCheckedNums.add(num);
				} else {
					mCheckedNums.remove(num);
				}
			}
		});

		super.onCreate(savedInstanceState);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bk:
			finish();
			break;
		case R.id.fine:
			Toast.makeText(mContext, mCheckedNums.toString(), 3000).show();
			sendSMS("�ð�������õ�һ���С�۷�Ĵ����������: http://210.51.1.22/download/TaxiDriver.apk ��������");

			break;
		case R.id.send_input:
			String inputnum = TextUitls.getEditValue(input_num);
			if (inputnum == null) {
				break;
			} else if (!TextUitls.isMobileNO(inputnum)) {
				input_num.setError("�绰��ʽ����");
				break;
			}
			mCheckedNums.add(inputnum);
			sendSMS("�ð�������õ�һ���С�۷�Ĵ����������: http://210.51.1.22/download/TaxiDriver.apk ��������");

			break;
		default:
			break;
		}
	}

	/** �õ��ֻ�ͨѶ¼��ϵ����Ϣ **/
	private void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		// ��ȡ�ֻ���ϵ��
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// �õ���ϵ������
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// �õ���ϵ��ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
				// mContactsPhonto.add(contactPhoto);
			}

			phoneCursor.close();
		}
	}

	/** �õ��ֻ�SIM����ϵ������Ϣ **/
	private void getSIMContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		// ��ȡSims����ϵ��
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// �õ���ϵ������
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim����û����ϵ��ͷ��

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
			}

			phoneCursor.close();
		}
	}

	ProgressDialog pd;

	private void sendSMS(String message) {
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		pd = new ProgressDialog(this);
		pd.setMessage("���ڷ��Ͷ���...");
		pd.show();
		for (String number : mCheckedNums) {
			Intent sentIntent = new Intent(SENT_SMS_ACTION);
			PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
					sentIntent, 0);
			// register the Broadcast Receivers
			registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent _intent) {
					if(pd!=null)
					pd.dismiss();
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(context, "���ŷ��ͳɹ�", Toast.LENGTH_SHORT)
								.show();
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(context, "���ŷ���ʧ��", Toast.LENGTH_SHORT)
								.show();
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Toast.makeText(context, "���ŷ���ʧ��", Toast.LENGTH_SHORT)
								.show();
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Toast.makeText(context, "���ŷ���ʧ��", Toast.LENGTH_SHORT)
								.show();
						break;
					}
					finish();
				}
			}, new IntentFilter(SENT_SMS_ACTION));
			// List<String> divideContents = smsManager.divideMessage(message);
			// for (String text : divideContents) {
			// smsManager.sendTextMessage(phoneNumber, null, text, sentPI,
			// deliverPI);
			// }
			sms.sendTextMessage(number, null, message, sentPI, null);
		}
	}
}
