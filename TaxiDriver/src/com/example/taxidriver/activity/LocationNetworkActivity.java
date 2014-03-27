package com.example.taxidriver.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.example.taxidriver.bean.ClientInfo;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.TextUitls;
import com.example.taxidriver.tools.WebUtils;

/**
 * AMapV2地图中简单介绍混合定位
 */
public class LocationNetworkActivity implements AMapLocationListener {
	private LocationManagerProxy mAMapLocManager = null;
	private static LocationNetworkActivity instance = null;
	static DriverInfo info;
	private Handler handler;
public LocationNetworkActivity(Context context) {
	// TODO Auto-generated constructor stub
	this.context=context;
}
	public void setLocationNetworkActivity(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	Context context;

	public static LocationNetworkActivity getInstance(Context context) {
		if (instance == null) {
			instance = new LocationNetworkActivity(context);
			info = DriverInfo.getInstance();
		}

		return instance;
	}

	Timer timer;
	TimerTask task;

	public void openTask() {
		if (task != null) {
			task.cancel();
		}
		if (timer != null) {
			timer.cancel();
		}

		this.timer = new Timer();
		this.task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (requestInfo != null && requestInfo.size() > 0) {
					if (!isCheck){
						getClientInfos(requestInfo);
					WebUtils.screenOn(context);
					}
				}
			}
		};
		timer.schedule(task, 1000, 4000);
	}

	// private TextView myLocation;
	// private AMapLocation aMapLocation;// 用于判断定位超时
	// private Handler handler = new Handler();

	public void onCreate() {
		mAMapLocManager = LocationManagerProxy.getInstance(context
				.getApplicationContext());
		mAMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 10, this);
		requestInfo = new HashMap<String, Object>();

	}

	/**
	 * 销毁定位
	 */
	public void stopLocation() {
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
		stopTask();
	}

	public void stopTask() {
		if (task != null) {
			task.cancel();
		}
		if (timer != null) {
			timer.cancel();
		}
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * 混合定位回调函数
	 */
	Map<String, Object> requestInfo;

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			double geoLat = location.getLatitude();
			double geoLng = location.getLongitude();
			requestInfo.put(keys[0], geoLng + "");
			requestInfo.put(keys[1], geoLat + "");
			requestInfo.put(keys[2], "10000");
			if (info == null)
				info = DriverInfo.getInstance();
			info.setLat(geoLat);
			info.setLon(geoLng);
			listener.onLocationChanged(location);
		}
	}

	public void setListener(OnLocitonChangeListener listener) {
		this.listener = listener;
	}

	OnLocitonChangeListener listener;

	interface OnLocitonChangeListener {
		void onLocationChanged(AMapLocation location);

		void getClientInfoss(ArrayList<ClientInfo> infos);

		boolean isClose();
	}

	boolean isCheck = false;
	String[] keys = { "dLon", "dLat", "iDis" };

	private void getClientInfos(Map<String, Object> infoss) {
		isCheck = true;
		String method = "GetNearCalling1";
		ArrayList<ClientInfo> infos = new ArrayList<ClientInfo>();
		SoapObject array = WebUtils.getObject(keys, method, infoss);
		if (array != null) {
			int count = array.getPropertyCount();
			for (int i = 0; i < count; i++) {
				SoapObject objec = (SoapObject) array.getProperty(i);
				String id = TextUitls.getSoapValue(objec, "Id", -1);
				ClientInfo info = new ClientInfo();
				info.setCalledState(TextUitls.getSoapValue(objec,
						"CalledState", -1));
				info.setCalledTime(TextUitls.getSoapValue(objec, "CalledTime",
						-1));
				info.setgLat(TextUitls.getSoapValue(objec, "GLat", -1));
				info.setgLon(TextUitls.getSoapValue(objec, "GLon", -1));
				info.setGoal(TextUitls.getSoapValue(objec, "Goal", -1));
				info.setId(id);
				info.setIntro(TextUitls.getSoapValue(objec, "Intro", -1));
				int position = -1;
				info.setMobile(TextUitls
						.getSoapValue(objec, "Mobile", position));
				info.setsLat(TextUitls.getSoapValue(objec, "SLat", position));
				info.setsLon(TextUitls.getSoapValue(objec, "SLon", position));
				info.setStart(TextUitls.getSoapValue(objec, "Start", position));
				info.setTaxiMobile(TextUitls.getSoapValue(objec, "TaxiMobile",
						position));
				info.setX(TextUitls.getSoapValue(objec, "X", position));
				info.setY(TextUitls.getSoapValue(objec, "Y", position));
				infos.add(0, info);
			}
			
		}
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.obj = infos;
			handler.sendMessage(msg);
		}
		listener.getClientInfoss(infos);
		isCheck = false;
	}

}
