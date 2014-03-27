package com.example.taxidriver.activity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.example.taxidriver.R;
import com.example.taxidriver.activity.LocationNetworkActivity.OnLocitonChangeListener;
import com.example.taxidriver.bean.ClientInfo;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.ClientAdapter;
import com.example.taxidriver.tools.SocketHandler;
import com.example.taxidriver.tools.WebUtils;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SynthesizerListener;

public class GetNearCallMsgActivity extends Activity implements
		OnClickListener, OnLocitonChangeListener, OnItemClickListener,
		SynthesizerListener {
	Button me, more, shouche, changeType;
	ListView informations;

	// private int state = 0;
	private SpeechSynthesizer mSpeechSynthesizer;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ArrayList<ClientInfo> infos = (ArrayList<ClientInfo>) msg.obj;
			adapter.changeDatas(infos);
		};
	};
	// private LocationManagerProxy mAMapLocManager = null;

	ClientAdapter adapter;
	Timer socketTimer;
	private DriverInfo info;
	TimerTask sendTask, openTask;
	LocationNetworkActivity loc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxi_siji_sou_xxl);
		initView();
		info = DriverInfo.getInstance();
		socketTimer = new Timer();
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this);

		// synthetizeInSilence("欢迎使用智慧交通！");
		// state = 1;

	}

	private void locationLL() {
		loc = LocationNetworkActivity.getInstance(this);
		loc.setLocationNetworkActivity(this);
		loc.setListener(this);
		// loc.onCreate();
		loc.setHandler(handler);
		loc.openTask();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationLL();
		startService(new Intent(this, MyLocalService.class));
	}

	private void initView() {
		// TODO Auto-generated method stub
		me = (Button) findViewById(R.id.taxi_me_xxl);
		more = (Button) findViewById(R.id.taxi_more_xxl);
		shouche = (Button) findViewById(R.id.taxi_shouche_xxl);
		informations = (ListView) findViewById(R.id.infomation);
		adapter = new ClientAdapter(this, null);
		informations.setAdapter(adapter);
		informations.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.taxi_me_xxl:
			intent.setClass(this, DisplayDriverInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.taxi_more_xxl:
			intent.setClass(this, MoreInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.taxi_shouche_xxl:
			try {
				loc.stopLocation();
				socketTimer.cancel();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				SocketHandler.getInstance().close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			finish();
			break;
		default:
			break;
		}

	}

	protected void onStop() {
		if (null != mSpeechSynthesizer) {
			mSpeechSynthesizer.stopSpeaking();
			// state = 0;
		}
		super.onStop();
	};

	String oderid = "", startPoint = "";

	// public void recoginice(ClientInfo info, boolean istrue) {
	// // System.out.println(oderid + "*******" + info.getId());
	// // if (state == 0) {
	// if (!oderid.equals(info.getId()) || istrue) {
	// oderid = info.getId();
	// String rec = "距离乘客" + info.getDistance()+"。从" + info.getStart() + "到" +
	// info.getGoal() ;
	// if(state!=1){
	// mSpeechSynthesizer.startSpeaking(rec, this);
	// state = 1;
	// }
	// //}
	// }
	// }

	ImageView iamge;
	ProgressDialog prd;

	private void initPrd() {
		// TODO Auto-generated method stub
		if (prd == null) {
			prd = new ProgressDialog(this);
			prd.setMessage("正在识别。。。");
			prd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					prd = null;
				}
			});
			prd.show();
		}
	}

	public void recoginice(ClientInfo info, ImageView view, boolean isTrue) {
		if (isTrue)
			initPrd();
		this.iamge = view;

		String rec = "距离乘客" + info.getDistance() + "。。从 " + info.getStart()
				+ "到 " + info.getGoal();
		if (!oderid.equals(info.getGoal()) || isTrue
				|| !startPoint.equals(info.getStart())) {
			// oderid = info.getId();
			if (state != 1) {
				view.setImageResource(R.drawable.play_seekbar_play);
				synthetizeInSilence(rec);
			}
		}
		// System.out.println(state+"??????????????"+(!oderid.equals(info.getGoal()))
		// );
		// state = 1;
		oderid = info.getGoal();
		startPoint = info.getStart();
	}

	/**
	 * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
	 * 
	 * @param
	 */
	private void synthetizeInSilence(String source) {
		System.out.println(source + "&&&&&&&&&&&&&&");
		if (null == mSpeechSynthesizer) {
			// 创建合成对象.
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this);
		}
		// 设置发音人
		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		// 获取语速
		int speed = 50;
		// 设置语速
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "" + speed);
		// 获取音量.
		int volume = 100;
		// 设置音量
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "" + volume);
		// 进行语音合成.
		System.out.println(source + ":::::::::::::???????????");
		mSpeechSynthesizer.startSpeaking(source, this);
	}

	/**
	 * <dLon>double</dLon> <dLat>double</dLat> <iDis>int</iDis>
	 */
	int olderid = -1;

	Map<String, Object> requestInfo;
	boolean isCheck = false;

	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		super.onPause();
		loc.setHandler(null);
		loc.stopTask();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// stopLocation();
		if (openTask != null) {
			openTask.cancel();
		}
		if (sendTask != null) {
			sendTask.cancel();
		}
		if (socketTimer != null) {
			socketTimer.cancel();
		}
		super.onDestroy();
	}

	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// synchronized (this) {
	// // if(state==0)
	//
	// getClientInfos(requestInfo);
	// }
	//
	// }

	boolean isRunning = false;
	private int state;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		final ClientInfo clientinfo = (ClientInfo) adapter.getItem(position);
		if (!isRunning)
			new Thread() {
				public void run() {
					isRunning = true;
					Intent intent = new Intent(GetNearCallMsgActivity.this,
							ClientDetailActivity.class);
					boolean isOk = WebUtils.taxiAccept(clientinfo.getId());
					intent.putExtra("client", clientinfo);
					isRunning = false;
					Log.i("life", isFinishing() + "****tacc***");
					if (isOk) {
						try {
							startActivity(intent);
							finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
			}.start();

	}

	@Override
	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompleted(SpeechError arg0) {
		// TODO Auto-generated method stub
		state = 0;
		if (iamge != null)
			iamge.setImageResource(R.drawable.play_seekbar_pause);
		// mSpeechSynthesizer.stopSpeaking();
		mSpeechSynthesizer.cancel();
	}

	/*
	 * TTS状态。 0->初始状态， 1->已经合成，播放状态， 2->暂停播放状态 初始状态为0
	 */
	@Override
	public void onSpeakBegin() {
		// TODO Auto-generated method stub
		if (prd != null && prd.isShowing()) {
			prd.dismiss();
			prd = null;
		}
		state = 1;
		// if (iamge != null)
		// iamge.setImageResource(R.drawable.play_seekbar_play);
	}

	@Override
	public void onSpeakPaused() {
		// TODO Auto-generated method stub
		state = 2;
	}

	@Override
	public void onSpeakProgress(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		state = 1;
		System.out.println(arg1 + "*****" + arg2);
	}

	@Override
	public void onSpeakResumed() {
		// TODO Auto-generated method stub
		state = 1;
	}

	@Override
	public boolean isClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getClientInfoss(ArrayList<ClientInfo> infos) {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();
		msg.obj = infos;
		handler.sendMessage(msg);
	}
}
