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
 * AMapV2��ͼ�м򵥽���ʸ����ͼ�����ǵ�ͼģʽ�л�
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
		mapView.onCreate(savedInstanceState);// �˷���������д
		init();
	}

	/**
	 * ��ʼ��AMap����
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
	 * ����������д
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();

	}

	/**
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
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
	 * ѡ��ʸ����ͼ�����ǵ�ͼ�¼�����Ӧ
	 */
	private void setLayer(String layerName) {
		if (layerName.equals("��άͼ")) {
			aMap.setMapType(AMap.MAP_TYPE_NORMAL);// ʸ����ͼģʽ
		} else if (layerName.equals("����ͼ")) {
			aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// ���ǵ�ͼģʽ
		}
	}

	private void drawMe() {
		locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
				.position(startPoint).title("����λ�ã�\n" + info.getMyAddress()));
		locationMarker.showInfoWindow();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	/**
	 * ��ѡ���Ƿ���ʾ��ͨ״���¼�����Ӧ
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.traffic) {
			aMap.setTrafficEnabled(((CheckBox) v).isChecked());// ��ʾʵʱ��ͨ״��
		} else if (v.getId() == R.id.taxi_fanhui_xxl) {
			finish();
		}
	}
}
