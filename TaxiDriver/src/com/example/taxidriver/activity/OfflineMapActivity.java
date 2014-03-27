package com.example.taxidriver.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.example.taxidriver.R;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.AMapUtil;
import com.example.taxidriver.tools.ToastUtil;

/**
 * AMapV2��ͼ�м򵥽������ߵ�ͼ����
 */
public class OfflineMapActivity extends Activity implements
		OfflineMapDownloadListener {
	private OfflineMapManager amapManager = null;
	private EditText cityName;
	private TextView statusText;
	/*
	 * private AMap aMap; private MapView mapView;
	 */
	private boolean update = false;
	private DriverInfo info;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����Ӧ�õ����ĵ�ͼ�洢Ŀ¼�����������ߵ�ͼ���ʼ����ͼʱ����
		MapsInitializer.sdcardDir = getSdCacheDir(this);
		setContentView(R.layout.offlinemap_activity);
		info=DriverInfo.getInstance();
		/*
		 * mapView = (MapView) findViewById(R.id.map);
		 * mapView.onCreate(savedInstanceState);// �˷���������д
		 */init();

	}

	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		// if (aMap == null) {
		// aMap = mapView.getMap();
		// }
		amapManager = new OfflineMapManager(this, this);
		cityName = (EditText) findViewById(R.id.cityname);
		statusText = (TextView) findViewById(R.id.status);
		String cct=info.getCity();
		if(cct!=null&&!cct.equals("")){
			cityName.setText(cct);
		}
	}

	/**
	 * �����ʼ��ť��Ӧ�¼��ص�
	 */
	public void startButton(View view) {
		String city = AMapUtil.checkEditText(cityName);
		if ("".equals(city)) {
			ToastUtil.show(OfflineMapActivity.this, "�������������");
			return;
		} else {
			downloadMap(city);
		}
	}

	/**
	 * ��ʼ�������ߵ�ͼ
	 */
	private void downloadMap(String city) {
		boolean start = false;
		try {
			start = amapManager.downloadByCityName(city);
			if (!start) {
				ToastUtil.show(this, "�������ߵ�ͼ����ʧ��");
			} else {
				ToastUtil.show(this, "��ʼ�������ߵ�ͼ����");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(this, "��������ʧ�ܣ����������Ƿ�����");
		}

	}

	/**
	 * �����ͣ��ť��Ӧ�¼��ص�
	 */
	public void pauseButton(View view) {
		amapManager.pause();
		ToastUtil.show(this, "��ͣ�������ߵ�ͼ����");
	}

	/**
	 * ���ֹͣ��ť��Ӧ�¼��ص�
	 */
	public void stopButton(View view) {
		amapManager.stop();
		ToastUtil.show(this, "ֹͣ�������ߵ�ͼ����");
	}

	/**
	 * ���ɾ����ť��Ӧ�¼��ص�
	 */
	public void deleteButton(View view) {
		String city = AMapUtil.checkEditText(cityName);
		if ("".equals(city)) {
			ToastUtil.show(OfflineMapActivity.this, "�������������");
			return;
		} else {
			amapManager.remove(city);
			ToastUtil.show(this, "ɾ�����ߵ�ͼ����");
		}

	}

	/**
	 * ������°�ť��Ӧ�¼��ص�
	 */
	public void updateButton(View view) {
		final String city = AMapUtil.checkEditText(cityName);
		if ("".equals(city)) {
			ToastUtil.show(OfflineMapActivity.this, "�������������");
			return;
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						update = amapManager.updateOfflineCityByName(city);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (update) {
									ToastUtil.show(OfflineMapActivity.this,
											city + "���������и���");
									downloadMap(city);
								} else {
									ToastUtil.show(OfflineMapActivity.this,
											city + "���������Ѿ������µ���");
								}

							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	/**
	 * ��ȡmap ����Ͷ�ȡĿ¼
	 * 
	 * @param context
	 * @return
	 */
	private String getSdCacheDir(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			java.io.File fExternalStorageDirectory = Environment
					.getExternalStorageDirectory();
			java.io.File autonaviDir = new java.io.File(
					fExternalStorageDirectory, "Company");
			boolean result = false;
			if (!autonaviDir.exists()) {
				result = autonaviDir.mkdir();
			}
			java.io.File minimapDir = new java.io.File(autonaviDir, "Name");
			if (!minimapDir.exists()) {
				result = minimapDir.mkdir();
			}
			return minimapDir.toString() + "/";
		} else {
			return null;
		}
	}

	@Override
	public void onDownload(int status, int completeCode, String arg2) {
		switch (status) {
		case OfflineMapStatus.SUCCESS:
			statusText.setText("���سɹ�");
			break;
		case OfflineMapStatus.LOADING:
			statusText.setText("��������," + "����ɣ�" + completeCode + "%" + arg2);
			if(completeCode==100){
				finish();
			}
			break;
		case OfflineMapStatus.UNZIP:
			statusText.setText("���ڽ�ѹ");
			break;
		case OfflineMapStatus.WAITING:
			statusText.setText("���ڵȴ�");
			break;
		case OfflineMapStatus.PAUSE:
			statusText.setText("��ͣ����");
			break;
		case OfflineMapStatus.STOP:
			statusText.setText("ֹͣ����");
			break;
		case OfflineMapStatus.ERROR:
			statusText.setText("���ش���");
			break;
		default:
			break;
		}
	}
}
