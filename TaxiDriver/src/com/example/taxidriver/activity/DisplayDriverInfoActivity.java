package com.example.taxidriver.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.taxidriver.R;

public class DisplayDriverInfoActivity extends Activity implements
		OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.taxi_siji_wodedidi_xxl);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.taxi_fanhui_xxl:
			finish();
			break;
		default:
			break;
		}
	}
}
