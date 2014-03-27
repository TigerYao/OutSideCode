package com.example.taxidriver.tools;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxidriver.R;
import com.example.taxidriver.activity.GetNearCallMsgActivity;
import com.example.taxidriver.bean.ClientInfo;
import com.example.taxidriver.bean.DriverInfo;

public class ClientAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<ClientInfo> infos;
	private DriverInfo driverInfo;
	private GetNearCallMsgActivity context;

	public ClientAdapter(GetNearCallMsgActivity context,
			ArrayList<ClientInfo> infos) {
		// TODO Auto-generated constructor stub
		this.context = context;
		changeDatas(infos);
		driverInfo = DriverInfo.getInstance();
		inflater = LayoutInflater.from(context);
	}

	public void changeDatas(ArrayList<ClientInfo> infos) {
		if (infos != null) {
			this.infos = infos;
		} else {
			this.infos = new ArrayList<ClientInfo>();
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infos.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return infos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final ClientInfo info = (ClientInfo) getItem(position);
	
	
		if (contentView == null) {
			contentView = inflater.inflate(R.layout.client_info, null);
		}
		final ImageView imageView = (ImageView) contentView
				.findViewById(R.id.talk);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				context.recoginice(info, imageView,true);
				imageView.setImageResource(R.drawable.play_seekbar_play);
			}
		});
		if (position == 0) {
			context.recoginice(info, imageView,false);
		}
		TextView tv = (TextView) contentView.findViewById(R.id.clientinfo);

		tv.setText("¾àÀë³Ë¿Í£º  " + info.getDistance() + "\n´Ó  " + info.getStart()
				+ "\nµ½  " + info.getGoal());

		return contentView;
	}

}
