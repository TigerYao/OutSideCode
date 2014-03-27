package com.example.taxidriver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.taxidriver.R;
import com.example.taxidriver.bean.UpdateManager;
import com.example.taxidriver.tools.ToastUtil;
import com.example.taxidriver.tools.WebUtils;
import com.yt.yh.ma.taxidriver.MainActivity;
import com.yt.yh.ma.taxidriver.NewActivity;

public class SettingsActivity extends NewActivity implements
		OnItemClickListener {
	ListView list;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String url = (String) msg.obj;
			UpdateManager update = new UpdateManager(SettingsActivity.this);
			update.checkUpdateInfo(url);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.fragment_5);

		list = (ListView) findViewById(R.id.fragment5_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.illnessitem, R.id.tv, getResources().getStringArray(
						R.array.settings_items));
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		switch (position) {
		case 3:
			PreferenceManager.getDefaultSharedPreferences(this).edit().clear()
					.commit();
			startActivity(new Intent(this, MainActivity.class));

			break;
		case 1:
			new Thread() {
				public void run() {
					isUpdate();
				};
			}.start();
			break;
		default:
			break;
		}

	}

	private void isUpdate() {
		String url = WebUtils.getUpdateUrl(this);
		if (url == null) {
			ToastUtil.show(this, "已经是最新版本");
		} else {
			Message msg = handler.obtainMessage();
			msg.obj = url;
			handler.sendMessage(msg);
		}
	}

	public void islogin() {

	}
}