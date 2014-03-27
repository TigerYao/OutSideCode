package com.example.taxidriver.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.example.taxidriver.R;
import com.example.taxidriver.activity.LocationNetworkActivity.OnLocitonChangeListener;
import com.example.taxidriver.bean.ClientInfo;
import com.example.taxidriver.bean.DriverInfo;

//import com.amap.api.maps.LocationSource.OnLocationChangedListener;

public class ClientDetailActivity extends Activity implements LocationSource,
		OnLocitonChangeListener, OnRouteSearchListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;

	private ClientInfo info;
	private TextView informa;
	private DriverInfo driverinfo;
LocationNetworkActivity loc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		info = (ClientInfo) getIntent().getSerializableExtra("client");
		setContentView(R.layout.activity_dialog);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		driverinfo = DriverInfo.getInstance();
		init();
		LatLonPoint startPoint = new LatLonPoint(driverinfo.getLat(),
				driverinfo.getLon());
		LatLonPoint endPoint = new LatLonPoint(Double.parseDouble(info
				.getsLat()), Double.parseDouble(info.getsLon()));
		searchRouteResult(startPoint, endPoint);
		drawMarkers(driverinfo.getLat(), driverinfo.getLon());
		loc=LocationNetworkActivity.getInstance(this);
		loc.setLocationNetworkActivity(this);
		loc.setListener(this);
	}
	

	/**
	 * 閸掓繂顬婇崠鏈匨ap鐎电钖�
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		informa = (TextView) findViewById(R.id.informa);
		informa.setText("始点：" + info.getStart() + "\n" + "终点：" + info.getGoal());
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
				driverinfo.getLat(), driverinfo.getLon())));
		// menubar = (Button) findViewById(R.id.menubar);

	}

	/**
	 * 鐠佸墽鐤嗘稉锟界昂amap閻ㄥ嫬鐫橀幀锟�
	 */
	private void setUpMap() {
		// 閼奉亜鐣炬稊澶岄兇缂佺喎鐣炬担宥呯毈閽冩繄鍋�
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 鐠佸墽鐤嗙亸蹇氭憫閻愬湱娈戦崶鐐垼
		myLocationStyle.strokeColor(Color.BLACK);// 鐠佸墽鐤嗛崷鍡楄埌閻ㄥ嫯绔熷鍡涱杹閼癸拷
		myLocationStyle.strokeWidth(0.1f);// 鐠佸墽鐤嗛崷鍡楄埌閻ㄥ嫯绔熷鍡欑煐缂侊拷
		UiSettings us = aMap.getUiSettings();
		us.setZoomControlsEnabled(false);
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setMyLocationRotateAngle(180);
		aMap.setLocationSource(this);// 鐠佸墽鐤嗙�姘秴閻╂垵鎯�
		us.setMyLocationButtonEnabled(false);// 鐠佸墽鐤嗘妯款吇鐎规矮缍呴幐澶愭尦閺勵垰鎯侀弰鍓с仛
		aMap.setMyLocationEnabled(true);// 鐠佸墽鐤嗘稉绨峳ue鐞涖劎銇氶弰鍓с仛鐎规矮缍呯仦鍌氳嫙閸欘垵袝閸欐垵鐣炬担宥忕礉false鐞涖劎銇氶梾鎰鐎规矮缍呯仦鍌氳嫙娑撳秴褰茬憴锕�絺鐎规矮缍呴敍宀勭帛鐠併倖妲竑alse
	}

	/**
	 * 閺傝纭惰箛鍛淬�闁插秴鍟�
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		loc.onCreate();
	}

	/**
	 * 閺傝纭惰箛鍛淬�闁插秴鍟�
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 閺傝纭惰箛鍛淬�闁插秴鍟�
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 閺傝纭惰箛鍛淬�闁插秴鍟�
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 閺勫墽銇氱化鑽ょ埠鐏忓繗鎽戦悙锟�
			float bearing = aMap.getCameraPosition().bearing;
			
			aMap.setMyLocationRotateAngle(bearing);// 鐠佸墽鐤嗙亸蹇氭憫閻愯妫嗘潪顒冾瀾鎼达拷
			double lat=aLocation.getLatitude();
			double lon=aLocation.getLongitude();
//			drawMarkers(aLocation.getLatitude(), aLocation.getLongitude());
			driverinfo.setLat(lat);
			driverinfo.setLon(lon);
			aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lon)));

		}
	}

	/**
	 * 濠碉拷妞跨�姘秴
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		// if (mAMapLocationManager == null) {
		// mAMapLocationManager = LocationManagerProxy.getInstance(this);
		// mAMapLocationManager.requestLocationUpdates(
		// LocationProviderProxy.AMapNetwork, 5000, 10, this);
		// }
	}

	/**
	 * 閸嬫粍顒涚�姘秴
	 */
	@Override
	public void deactivate() {
		loc.stopLocation();
		 mListener = null;
		// if (mAMapLocationManager != null) {
		// mAMapLocationManager.removeUpdates(this);
		// mAMapLocationManager.destory();
		// }
		// mAMapLocationManager = null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.call:
			System.out.println(info.getMobile());
			Intent phoneIntent = new Intent(Intent.ACTION_CALL,
					Uri.parse("tel:" + info.getMobile()));
			startActivity(phoneIntent);
			break;
		// case R.id.play:
		// break;
		case R.id.confirm:
			startActivity(new Intent(this, GetNearCallMsgActivity.class));
			finish();
			break;
//		case R.id.fail:
//
//			new Thread() {
//				public void run() {
//					if (WebUtils.freeDriving(info.getId(),
//							driverinfo.getStrMobile(), driverinfo.getStrPwd())) {
//						startActivity(new Intent(ClientDetailActivity.this,
//								GetNearCallMsgActivity.class));
//						finish();
//					}
//				};
//			}.start();
//			break;
		default:
			break;
		}
	}

	// /**
	// * 閺傝纭惰箛鍛淬�闁插秴鍟�
	// */
	// @Override
	// protected void onResume() {

	// // drawMarkers();
	// super.onResume();
	// mapView.onResume();
	// }

	public void drawMarkers(double lat, double lon) {
		// Toast.makeText(this, "nihao", 3000).show();
		LatLng posi = new LatLng(lat, lon);
		Marker marker = aMap.addMarker(new MarkerOptions()
				.position(posi)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.perspective(true).draggable(true));
		// marker.setRotateAngle(90);// 鐠佸墽鐤唌arker閺冨娴�0鎼达拷
		marker.showInfoWindow();// 鐠佸墽鐤嗘妯款吇閺勫墽銇氭稉锟介嚋infowinfow

	}

	
	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	private DriveRouteResult driveRouteResult;// 妞规崘婧呭Ο鈥崇础閺屻儴顕楃紒鎾寸亯

	/**
	 * 妞规崘婧呯紒鎾寸亯閸ョ偠鐨�
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		// dissmissProgressDialog();
		System.out.println(rCode + "****rCode**");
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 濞撳懐鎮婇崷鏉挎禈娑撳﹦娈戦幍锟芥箒鐟曞棛娲婇悧锟�
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				Toast.makeText(ClientDetailActivity.this, "错误", 3000).show();
				// ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			// ToastUtil.show(RouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			// ToastUtil.show(RouteActivity.this, R.string.error_key);
		} else {
			// ToastUtil.show(RouteActivity.this, R.string.error_other);
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	private RouteSearch routeSearch;

	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		// showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		// 妞规崘婧呯捄顖氱窞鐟欏嫬鍨�
		DriveRouteQuery query = new DriveRouteQuery(fromAndTo,
				RouteSearch.DrivingDefault, null, null, "");// 缁楊兛绔存稉顏勫棘閺佹媽銆冪粈楦跨熅瀵板嫯顬冮崚鎺旀畱鐠ч鍋ｉ崪宀�矒閻愮櫢绱濈粭顑跨癌娑擃亜寮弫鎷屻�缁�椽鈹氭潪锔侥佸蹇ョ礉缁楊兛绗佹稉顏勫棘閺佹媽銆冪粈娲拷缂佸繒鍋ｉ敍宀�儑閸ユ稐閲滈崣鍌涙殶鐞涖劎銇氶柆鑳唨閸栧搫鐓欓敍宀�儑娴滄柧閲滈崣鍌涙殶鐞涖劎銇氶柆鑳唨闁捁鐭�
		routeSearch.calculateDriveRouteAsyn(query);// 瀵倹顒炵捄顖氱窞鐟欏嫬鍨濇す鎹愭簠濡�绱￠弻銉嚄
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean isClose() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void getClientInfoss(ArrayList<ClientInfo> infos) {
		// TODO Auto-generated method stub
		
	}
}
