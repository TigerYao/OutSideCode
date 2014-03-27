package com.example.taxidriver.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.services.core.LatLonPoint;
import com.example.taxidriver.activity.LocationNetworkActivity.OnLocitonChangeListener;
import com.example.taxidriver.bean.ClientInfo;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.DealAddress;
import com.example.taxidriver.tools.SocketHandler;
import com.example.taxidriver.tools.WebUtils;

public class MyLocalService extends Service implements OnLocitonChangeListener {
	LocationNetworkActivity loc;
	DealAddress addressRec;
	SharedPreferences pre;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	TimerTask sendTask, openTask;
	Timer socketTimer;
	private AMapLocation aMapLocation;
	SocketHandler sockethandler;
	KeyguardManager keyManger;
	PowerManager pm;
	WakeLock wakeLock;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		addressRec = new DealAddress(this);
		keyManger = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		pre = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate();
	}

	private void locationLL() {
		loc = LocationNetworkActivity.getInstance(this);
		loc.setLocationNetworkActivity(this);
		loc.setListener(this);
		loc.onCreate();
	}

	KeyguardLock mKeyguardLock;

	private void dealHandler(TimerTask sendTask, TimerTask openTask,
			Timer socketTimer, final SocketHandler sockethandler) {
		sendTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (aMapLocation != null)
					sockethandler.sendMessage(aMapLocation, pre);
				// 点亮亮屏
				wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
				wakeLock.acquire();
				Log.i("Log : ", "------>mKeyguardLock");
				// 初始化键盘锁，可以锁定或解开键盘锁
				mKeyguardLock = keyManger.newKeyguardLock("");
				// 禁用显示键盘锁定
				mKeyguardLock.disableKeyguard();
			}
		};
		openTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sockethandler.open();
				this.cancel();
			}
		};
		socketTimer.schedule(openTask, 2000);
		socketTimer.schedule(sendTask, 1000);
	}

	// @Override
	// public void onLocationChanged(Location arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onProviderDisabled(String arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onProviderEnabled(String arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	// // TODO Auto-generated method stub
	//
	// }

	DriverInfo info;
	private LocationManagerProxy mAMapLocManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		info = DriverInfo.getInstance();
		if (sockethandler != null) {
			sockethandler.close();
		}
		if (socketTimer != null) {
			socketTimer.cancel();
		}
		locationLL();
		sockethandler = SocketHandler.getInstance();
		socketTimer = new Timer();
		dealHandler(sendTask, openTask, socketTimer, sockethandler);
		// mAMapLocManager = LocationManagerProxy.getInstance(this);
		// mAMapLocManager.requestLocationUpdates(
		// LocationProviderProxy.AMapNetwork, 5000, 10, this);
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onLocationChanged(final AMapLocation location) {
		// TODO Auto-generated method stub
		synchronized (location) {

			if (location != null) {
				this.aMapLocation = location;
				double geoLat = location.getLatitude();
				double geoLng = location.getLongitude();
				info.setLat(geoLat);
				info.setLon(geoLng);
				addressRec.getAddress(new LatLonPoint(geoLat, geoLng));
				String city = location.getCity();
				if (city != null) {
					info.setCity(city);
				}
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (aMapLocation != null)
							sockethandler.sendMessage(location, pre);
					}
				}.start();

				wakeLock.acquire();
				KeyguardLock keyLock = keyManger.newKeyguardLock("");
				keyLock.disableKeyguard();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						wakeLock.release();
					}
				}, 300000);

				// System.out.println("**************\n" + geoLat + "******"
				// + geoLng + "\n**************");
				// Toast.makeText(this, , duration).show();
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (sockethandler != null) {
			sockethandler.close();
		}
		if (socketTimer != null) {
			socketTimer.cancel();
		}
		loc.stopLocation();
		// startService(new Intent(this, MyLocalService.class));
		super.onDestroy();
	}

	@Override
	public boolean isClose() {
		// TODO Auto-generated method stub
		return false;
	}

	Handler handler = new Handler();

	@Override
	public void getClientInfoss(ArrayList<ClientInfo> infos) {
		// TODO Auto-generated method stub
		System.out.println("???????????????????");
		if (infos != null && infos.size() > 0) {
			wakeLock.acquire();
			KeyguardLock keyLock = keyManger.newKeyguardLock("");
			keyLock.disableKeyguard();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					wakeLock.release();
				}
			}, 30000);
		}

	}

}
