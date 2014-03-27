package com.example.taxidriver.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taxidriver.R;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.TextUitls;
import com.example.taxidriver.tools.WebUtils;

public class RegisterActivity extends Activity implements OnClickListener {
	EditText username, mobile, ccode, pwd, company, checknum;
	String mobilenum = null;
	Button getCheckNum;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1001:
				Toast.makeText(RegisterActivity.this, "注册失败", 3000).show();
				if (prd != null) {
					prd.dismiss();
				}
				break;
			case 1002:
				Toast.makeText(RegisterActivity.this, "账号已存在", 3000).show();
				if (prd != null) {
					prd.dismiss();
				}
				break;
			case 1009:
				getCheckNum.setEnabled(true);
				break;
			default:
				break;
			}
		};
	};
	SharedPreferences pre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.taxi_zhuche_xxl);
		super.onCreate(savedInstanceState);
		initView();

	}

	private void initView() {
		pre = PreferenceManager.getDefaultSharedPreferences(this);
		// TODO Auto-generated method stub
		username = (EditText) findViewById(R.id.register_username);// _username_xxl);
		mobile = (EditText) findViewById(R.id.register_mobile);
		ccode = (EditText) findViewById(R.id.register_taxi_ccode);
		pwd = (EditText) findViewById(R.id.register_taxi_password);
		company = (EditText) findViewById(R.id.register_taxi_company);
		checknum = (EditText) findViewById(R.id.register_taxi_checknum);
		getCheckNum = (Button) findViewById(R.id.getCheckNum);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		prd = new ProgressDialog(this);
		prd.setMessage("正在注册请稍等。。。");
	}

	boolean isRegisting = false;
	ProgressDialog prd;

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		synchronized (this) {

			switch (view.getId()) {
			case R.id.register:

				register();

				break;
			case R.id.taxi_zhuce_fanhui_xxl:
				startActivity(new Intent(this, LogoInActivity.class));
				finish();
				break;
			case R.id.getCheckNum:
				if (mobile.getText() == null) {
					mobile.setError("号码不能为空");
					return;
				}
				if (mobile.getText() != null) {
					mobilenum = TextUitls.getEditValue(mobile);
					if (mobilenum == null || !TextUitls.isMobileNO(mobilenum)) {
						mobilenum = null;
						Toast.makeText(this, "电话号码格式不正确", 3000).show();
						return;
					}
				}
				Toast.makeText(this, "25秒之后再重新获取", 3000).show();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getCheckNum.setClickable(true);
					}
				}, 25000);
				getCheckNum.setClickable(false);
				new Thread() {
					public void run() {
						String che = WebUtils.getCheckNum(mobilenum).trim();
						if (che != null || che.equals("anyType{}")) {
							handler.sendEmptyMessage(1009);
						}
					};
				}.start();
				break;
			default:
				break;
			}
		}
	}

	DriverInfo info;
	List<String> keys = new ArrayList<String>();
	Map<String, String> infos = new HashMap<String, String>();

	// <returns>0:注册失败 1:注册成功 2:已经存在注册帐号</returns>
	private void register() {
		System.out.println("&&&&&&&&&&&&&&&&");
		isRegisting = true;
		String name = TextUitls.getEditValue(username);

		String code = TextUitls.getEditValue(ccode);
		String pwds = TextUitls.getEditValue(pwd);
		String com = TextUitls.getEditValue(company);
		String checknums = TextUitls.getEditValue(checknum);
		if (mobilenum == null) {
			mobilenum = TextUitls.getEditValue(mobile);
			if (mobilenum == null || !TextUitls.isMobileNO(mobilenum)) {
				Toast.makeText(this, "电话号码不正确", 3000).show();
				return;
			}
		}
		if (name == null || code == null || com == null || checknums == null
				|| pwds == null) {
			Toast.makeText(this, "信息不完整", 3000).show();
			return;
		}
		if (!prd.isShowing()) {
			prd.show();
		}
		// else {
		keys.clear();
		infos.clear();
		infos.put("strMobile", mobilenum);
		keys.add("strMobile");
		infos.put("strName", name);
		keys.add("strName");
		infos.put("strPwd", pwds);
		keys.add("strPwd");
		infos.put("strEmail", com);
		keys.add("strEmail");
		infos.put("strCode", code);
		keys.add("strCode");
		infos.put("iCheckCode", checknums);
		keys.add("iCheckCode");
		info = DriverInfo.getInstance();
		info.putValue(mobilenum, name, pwds, com, code);
		new Thread() {

			public void run() {
				String result = WebUtils.getString(keys, "RegUserDriver1",
						infos);
				System.out.println("register******" + result);
				TextUitls.putValues(pre, infos);
				if (result.equals("0")) {
					keys.clear();
					infos.clear();
					handler.sendEmptyMessage(1001);
					pre.edit().clear().commit();
				} else if (result.equals("1")) {
					// System.out.println(info.toString());
					startActivity(new Intent(getBaseContext(),
							GetNearCallMsgActivity.class));
					finish();
				} else if (result.equals("2")) {
					handler.sendEmptyMessage(1002);
					keys.clear();
					infos.clear();
					pre.edit().clear().commit();
				}
				isRegisting = false;
			};
		}.start();

		// }

	}
}
