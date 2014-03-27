package com.yt.yh.ma.taxidriver;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.taxidriver.R;
import com.example.taxidriver.activity.GetNearCallMsgActivity;
import com.example.taxidriver.activity.LogoInActivity;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.bean.UpdateManager;
import com.example.taxidriver.tools.HttpUtils;
import com.example.taxidriver.tools.TextUitls;
import com.example.taxidriver.tools.WebUtils;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechUser;

public class MainActivity extends NewActivity {
	SharedPreferences pre;
	DriverInfo info;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String url = (String) msg.obj;
			UpdateManager update = new UpdateManager(MainActivity.this);
			update.checkUpdateInfo(url);
		};
	};
	LocationManager alm;
	private Timer timer;
	private TimerTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pre = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_main);
		alm = (LocationManager) getSystemService(LOCATION_SERVICE);
		cancleTask();
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isUpdate();
			}
		};
	}

	/**
	 * 用户登录回调监听器.
	 */
	private SpeechListener listener = new SpeechListener() {

		@Override
		public void onData(byte[] arg0) {
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error != null) {
				Toast.makeText(MainActivity.this,
						getString(R.string.text_login_fail), Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onEvent(int arg0, Bundle arg1) {

		}
	};
	AlertDialog dialog;

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		switch (id) {
		case 0:
			builder.setTitle("网络提示")
					.setMessage("网络连接不可用,是否进行设置?")
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = null;
									// 判断手机系统的版本 即API大于10 就是3.0或以上版本
									if (android.os.Build.VERSION.SDK_INT > 10) {
										intent = new Intent(
												android.provider.Settings.ACTION_WIRELESS_SETTINGS);
									} else {
										intent = new Intent();
										ComponentName component = new ComponentName(
												"com.android.settings",
												"com.android.settings.WirelessSettings");
										intent.setComponent(component);
										intent.setAction("android.intent.action.VIEW");
									}
									startActivity(intent);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							});
			dialog = builder.create();
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub

					finish();
				}
			});
			// dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			//
			// @Override
			// public boolean onKey(DialogInterface dialog, int keyCode,
			// KeyEvent event) {
			// // TODO Auto-generated method stub
			// if(event.getAction()==KeyEvent.ACTION_DOWN){
			//
			// }
			// return false;
			// }
			// });
			break;
		case 1:
			builder.setTitle("GPS提示")
					.setMessage("GPS未开启,是否进行设置?")
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = null;
									// 判断手机系统的版本 即API大于10 就是3.0或以上版本
									// if (android.os.Build.VERSION.SDK_INT >
									// 10) {
									intent = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									// }
									startActivity(intent);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();

									// finish();
								}
							});
			dialog = builder.create();
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub

					try {
						timer.schedule(task, 10);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			break;
		default:
			break;
		}

		return dialog; // super.onCreateDialog(id);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		info = TextUitls.getDriverInfo(pre);
		cancleTask();
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isUpdate();
			}
		};
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtils.isNetworkConnected(MainActivity.this)
						&& alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
					try {
						timer.schedule(task, 10);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (!HttpUtils.isNetworkConnected(MainActivity.this)) {
					showDialog(0);
				} else if (!alm
						.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
					showDialog(1);
					// setNetworkMethod(getBaseContext());
				}
			}
		});
		super.onResume();
	}

	public void islogin() {
		SpeechUser.getUser().login(MainActivity.this, null, null,
				"appid=" + getString(R.string.app_id), listener);
		final String strMobile = pre.getString("strMobile", null);
		String strName = pre.getString("strName", null);
		final String strPwd = pre.getString("strPwd", null);
		String strCode = pre.getString("strCode", null);
		System.out.println(strMobile + "****" + strPwd);
		// if (info == null || info.getStrMobile() == null
		// || info.getStrMobile().length() <= 0) {

		new Thread() {
			public void run() {
				if (strMobile != null && strPwd != null
						&& WebUtils.Login(strMobile, strPwd)) {
					TextUitls.saveInfo(pre, DriverInfo.getInstance());
					startActivity(new Intent(MainActivity.this,
							GetNearCallMsgActivity.class));
					finish();
					// }
				} else {
					startActivity(new Intent(MainActivity.this,
							LogoInActivity.class));
					finish();
				}
			};
		}.start();
		// finish();
	}

	// }

	private void isUpdate() {
		String url = WebUtils.getUpdateUrl(this);
		if (url == null) {
			islogin();
		} else {
			Message msg = handler.obtainMessage();
			msg.obj = url;
			handler.sendMessage(msg);
		}
	}

	private void cancleTask() {
		if (task != null) {
			task.cancel();
		}
		if (timer != null) {
			timer.cancel();
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cancleTask();
		super.onDestroy();
	}
}
