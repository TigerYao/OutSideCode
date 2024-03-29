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
	// 填充数据的list
	private ArrayList<String> list;
	private ArrayList<String> nums;
	// 用来控制CheckBox的选中状况
	public static HashMap<Integer, Boolean> isSelected;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;

	// 构造器
	public ContactsAdatpter(ArrayList<String> list, ArrayList<String> nums,
			Context context) {
		this.context = context;
		this.list = list;
		this.nums = nums;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initDate();
	}

	// 初始化isSelected的数据
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
			// 获得ViewHolder对象
			holder = new ViewHolder();
			// 导入布局并赋值给convertview
			convertView = inflater.inflate(R.layout.contactsinfos_item, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.color_title);
			holder.text = (TextView) convertView.findViewById(R.id.color_text);
			holder.cb = (CheckBox) convertView.findViewById(R.id.color_image);
			// 为view设置标签
			convertView.setTag(holder);
		} else {
			// 取出holder
			holder = (ViewHolder) convertView.getTag();
		}
		String name = list.get(position);
		String number = nums.get(position);
		// 绘制联系人名称
		if (name != null)
			holder.title.setText(name);
		// //绘制联系人号码
		if (number != null)
			holder.text.setText(number);
		// 根据isSelected来设置checkbox的选中状况
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