package com.example.taxidriver.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taxidriver.R;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.TextUitls;
import com.example.taxidriver.tools.WebUtils;

public class FindPasswordActivity extends Activity implements OnClickListener {
	EditText tel, username, psw;
	Button register_button;
	SharedPreferences preferen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findpassword);
		initview();
		preferen = PreferenceManager.getDefaultSharedPreferences(this);
		iniPrd();
	}

	private void initview() {
		// TODO Auto-generated method stub
		tel = (EditText) findViewById(R.id.tel_edit);
		username = (EditText) findViewById(R.id.register_checknum);
		psw = (EditText) findViewById(R.id.password_edit);
		register_button = (Button) findViewById(R.id.register_button);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	ProgressDialog prd;

	private void iniPrd() {
		// TODO Auto-generated method stub
		prd = new ProgressDialog(this);
		prd.setMessage("正在修改，请稍后。。。");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.register_go_login:
			startActivity(new Intent(this, LogoInActivity.class));
			finish();
			break;

		case R.id.register_button:
			register();
			break;
		case R.id.register_getchecknum:
			final String tels = TextUitls.getEditValue(tel);
			if (TextUitls.isMobileNO(tels)) {
				Toast.makeText(this, "25秒之后再重新获取", 3000).show();
				arg0.setEnabled(false);
				new Thread() {
					public void run() {
						WebUtils.getPwd(tels);
					};
				}.start();
			} else {
				Toast.makeText(this, "电话号码格式不正确！", 3000).show();
			}
			break;
		case R.id.register_go_register:
			startActivity(new Intent(this, RegisterActivity.class));
			finish();
			break;
		default:
			break;
		}
		;

	}

	public void register() {
		final Map<String, Object> infos = new HashMap<String, Object>();

		final String tels = TextUitls.getEditValue(tel);// tel.getText().toString();
		if (!TextUitls.isMobileNO(tels)) {
			Toast.makeText(this, "手机号码格式不正确", 3000).show();
			return;
		}
		infos.put("strMobile", tels);
		final String usernames = TextUitls.getEditValue(username); // username.getText().toString();
		infos.put("strName", usernames);
		final String password = TextUitls.getEditValue(psw); // psw.getText().toString();
		if (password.trim().length() < 6) {
			Toast.makeText(this, "密码设置太短，至少6位", 3000).show();
			return;
		}
		infos.put("strPwd", password);
		AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				if (prd != null)
					prd.show();
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				return WebUtils.changePwd(tels, usernames, password);
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				if (prd != null) {
					prd.dismiss();
				}
				if ("1".equals(result)) {
					Toast.makeText(FindPasswordActivity.this, "修改成功", 2000)
							.show();
					preferen.edit().putString("pwd", password).commit();

					DriverInfo info = DriverInfo.getInstance();
					info.setStrMobile(tels);
					info.setStrPwd(password);
					startActivity(new Intent(FindPasswordActivity.this,
							LogoInActivity.class));
					finish();
				} else if ("0".equals(result)) {
					Toast.makeText(FindPasswordActivity.this, "修改失败！", 2000)
							.show();
				} else {
					Toast.makeText(FindPasswordActivity.this, "账号已存在！", 2000)
							.show();
				}
				super.onPostExecute(result);
			}

		};
		task.execute();
	}
}
