package com.example.taxidriver.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyTrafficStyle;
import com.example.taxidriver.R;
import com.example.taxidriver.bean.DriverInfo;

/**
 * AMapV2地图中简单介绍矢量地图和卫星地图模式切换
 */
public class LayersActivity extends Activity implements OnItemSelectedListener,
		OnClickListener {
	private AMap aMap;
	private MapView mapView;
	DriverInfo info;
	private Marker locationMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layers_activity);
		info = DriverInfo.getInstance();
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	LatLng startPoint;

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			if (info.getLat() != 0) {
				startPoint = new LatLng(info.getLat(), info.getLon());
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
						15));
				drawMe();
			}
		}
		CheckBox traffic = (CheckBox) findViewById(R.id.traffic);
		aMap.setTrafficEnabled(traffic.isChecked());
		traffic.setOnClickListener(this);
		Spinner spinner = (Spinner) findViewById(R.id.layers_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.layers_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
		myTrafficStyle.setSeriousCongestedColor(0xff92000a);
		myTrafficStyle.setCongestedColor(0xffea0312);
		myTrafficStyle.setSlowColor(0xffff7508);
		myTrafficStyle.setSmoothColor(0xff00a209);
		aMap.setMyTrafficStyle(myTrafficStyle);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (aMap != null) {
			setLayer((String) parent.getItemAtPosition(position));
		}
	}

	/**
	 * 选择矢量地图和卫星地图事件的响应
	 */
	private void setLayer(String layerName) {
		if (layerName.equals("二维图")) {
			aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
		} else if (layerName.equals("卫星图")) {
			aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
		}
	}

	private void drawMe() {
		locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
				.position(startPoint).title("您的位置：\n" + info.getMyAddress()));
		locationMarker.showInfoWindow();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	/**
	 * 对选择是否显示交通状况事件的响应
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.traffic) {
			aMap.setTrafficEnabled(((CheckBox) v).isChecked());// 显示实时交通状况
		} else if (v.getId() == R.id.taxi_fanhui_xxl) {
			finish();
		}
	}
}
