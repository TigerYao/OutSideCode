package com.example.taxidriver.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taxidriver.R;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.TextUitls;
import com.example.taxidriver.tools.WebUtils;

@SuppressLint("NewApi")
public class LogoInActivity extends Activity implements OnClickListener {
	SharedPreferences pre;
	DriverInfo info;
	EditText tel, pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		pre = PreferenceManager.getDefaultSharedPreferences(this);
		tel = (EditText) findViewById(R.id.tel_login);
		pwd = (EditText) findViewById(R.id.password_login);
	}

	ProgressDialog prd;

	private void initProgressD() {
		// TODO Auto-generated method stub
		if (prd == null) {
			prd = new ProgressDialog(this);
			prd.setTitle("登陆提示");
			prd.setMessage("正在登陆中。。。");
			prd.show();
			prd.setCanceledOnTouchOutside(false);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login_button:

			final String tels = TextUitls.isMobileNO(tel);
			final String pwds = TextUitls.getEditValue(pwd);
			if (tels != null && pwds != null) {
				initProgressD();
				new Thread() {
					public void run() {
						if (WebUtils.Login(tels, pwds)) {
							TextUitls.saveInfo(pre, DriverInfo.getInstance());
							// if(!LogoInActivity.this.isDestroyed()){
							if (prd != null && prd.isShowing())
								prd.dismiss();
							startActivity(new Intent(LogoInActivity.this,
									GetNearCallMsgActivity.class));
							finish();
							// }
						}
					};
				}.start();
			} else {
				Toast.makeText(this, "信息不全", 3000).show();
			}
			break;
		case R.id.login_go_register:
			startActivity(new Intent(this, RegisterActivity.class));
			finish();
			break;
		case R.id.login_go_getPwd:
			startActivity(new Intent(this,FindPasswordActivity.class));
			finish();
			break;
		default:
			break;
		}

	}
}
