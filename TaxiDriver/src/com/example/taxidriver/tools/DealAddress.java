package com.example.taxidriver.tools;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.taxidriver.bean.DriverInfo;

public class DealAddress implements OnGeocodeSearchListener {
	GeocodeSearch geocoderSearch;
	DriverInfo info;

	public DealAddress(Context context) {
		// TODO Auto-generated constructor stub
		geocoderSearch = new GeocodeSearch(context);
		geocoderSearch.setOnGeocodeSearchListener(this);
		info = DriverInfo.getInstance();
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				RegeocodeAddress regResutl=result.getRegeocodeAddress();
				String myAddress =regResutl.getDistrict()+regResutl.getStreetNumber().getStreet()+regResutl.getBuilding()+"附近";
							info.setMyAddress(myAddress);
				// addressName = result.getRegeocodeAddress().getFormatAddress()
				// + "附近";
//				 aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				// AMapUtil.convertToLatLng(latLonPoint), 15));
				// regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
				// ToastUtil.show(GeocoderActivity.this, addressName);
			} else {
				// ToastUtil.show(GeocoderActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			// ToastUtil.show(GeocoderActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			// ToastUtil.show(GeocoderActivity.this, R.string.error_key);
		} else {
			// ToastUtil.show(GeocoderActivity.this, R.string.error_other);
		}
	}

}
