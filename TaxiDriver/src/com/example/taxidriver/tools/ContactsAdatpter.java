package com.example.taxidriver.tools;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.taxidriver.R;
import com.example.taxidriver.bean.ViewHolder;

public class ContactsAdatpter extends BaseAdapter {
	// ������ݵ�list
	private ArrayList<String> list;
	private ArrayList<String> nums;
	// ��������CheckBox��ѡ��״��
	public static HashMap<Integer, Boolean> isSelected;
	// ������
	private Context context;
	// �������벼��
	private LayoutInflater inflater = null;

	// ������
	public ContactsAdatpter(ArrayList<String> list, ArrayList<String> nums,
			Context context) {
		this.context = context;
		this.list = list;
		this.nums = nums;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		// ��ʼ������
		initDate();
	}

	// ��ʼ��isSelected������
	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = inflater.inflate(R.layout.contactsinfos_item, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.color_title);
			holder.text = (TextView) convertView.findViewById(R.id.color_text);
			holder.cb = (CheckBox) convertView.findViewById(R.id.color_image);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (ViewHolder) convertView.getTag();
		}
		String name = list.get(position);
		String number = nums.get(position);
		// ������ϵ������
		if (name != null)
			holder.title.setText(name);
		// //������ϵ�˺���
		if (number != null)
			holder.text.setText(number);
		// ����isSelected������checkbox��ѡ��״��
		holder.cb.setChecked(getIsSelected().get(position));
		return convertView;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		ContactsAdatpter.isSelected = isSelected;
	}
}