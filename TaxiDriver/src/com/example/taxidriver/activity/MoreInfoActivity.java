package com.example.taxidriver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.taxidriver.R;

public class MoreInfoActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxi_gengduo_xxl);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.taxi_fanhui_xxl:
			finish();
			break;
		case R.id.taxi_tuijian_xxl:
			startActivity(new Intent(this, CopyOfContactsActivity.class));
			break;
		case R.id.taxi_lukuang_xxl:
			startActivity(new Intent(this, LayersActivity.class));
			break;
		case R.id.taxi_fuwu_xxl:
			startActivity(new Intent(this, ServiceAcitivty.class));
			break;
		case R.id.taxi_didishezhi_xxl:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.download_map:
			startActivity(new Intent(this, OfflineMapActivity.class));
			break;
		default:
			break;
		}
	}
}
