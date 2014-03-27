package com.example.taxidriver.tools;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

public class SocketServer extends Service implements AMapLocationListener{
   
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
	// TODO Auto-generated method stub
	return super.onStartCommand(intent, flags, startId);
}
@Override
public void onCreate() {
	// TODO Auto-generated method stub
	super.onCreate();
}
@Override
public void onLocationChanged(Location arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onProviderDisabled(String arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onProviderEnabled(String arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	// TODO Auto-generated method stub
	
}
@Override
public void onLocationChanged(AMapLocation arg0) {
	// TODO Auto-generated method stub
	
}
}
