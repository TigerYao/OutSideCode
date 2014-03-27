package com.example.taxidriver.tools;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;

import com.example.taxidriver.bean.DriverInfo;

public class TextUitls {
	public static String getEditValue(EditText text) {
		if (text == null || text.getText() == null
				|| text.getText().toString().length() <= 0) {
			text.setError("不能为空");
			return null;
		} else
			return text.getText().toString().trim();
	}

	public static boolean putValues(SharedPreferences pre,
			Map<String, String> infos) {
		Editor edits = pre.edit();
		for (String key : infos.keySet()) {
			String value = infos.get(key);
			edits.putString(key, value);
		}
		edits.putBoolean("isRegister", true);
		return edits.commit();
	}

	public static DriverInfo getDriverInfo(SharedPreferences pre) {
		String strMobile = pre.getString("strMobile", null);
		String strName = pre.getString("strName", null);
		String strPwd = pre.getString("strPwd", null);
		String strCode = pre.getString("strCode", null);
		DriverInfo inf = DriverInfo.getInstance();
		inf.putValue(strMobile, strName, strPwd, "", strCode);
		System.out.println(inf);
		return inf;
	}

	public static void saveInfo(SharedPreferences pre, DriverInfo inf) {
		String strMobile = inf.getStrMobile();
		String strName = inf.getStrName();
		String strPwd = inf.getStrPwd();
		String strCode = inf.getStrCode();
		pre.edit().putString("strMobile", strMobile)
				.putString("strName", strName).putString("strPwd", strPwd)
				.putString("strCode", strCode).commit();
	}

	public static String getSoapValue(SoapObject objec, String key, int position) {
		String value = "";
		if (objec != null && key != null) {
			Object obj = objec.getProperty(key);
			if (obj != null) {
				value = obj.toString();
			}
		} else if (position != -1) {
			Object obj = objec.getProperty(key);
			if (obj != null) {
				value = obj.toString();
			}
		}
		return value;
	}

	public static String getDistatce(double lat11, double lat22, double lon11,
			double lon22) throws Exception {
		double R = 6371;
		// double distance = 0.0;
		System.out.println(lat11 + "*(***" + lat22 + "*****" + lon11 + "*****"
				+ lon22);
		double lat1 = (Math.PI / 180) * lat11;
		double lat2 = (Math.PI / 180) * lat22;

		double lon1 = (Math.PI / 180) * lon11;
		double lon2 = (Math.PI / 180) * lon22;
		double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(lon2 - lon1))
				* R;

		return "大约" + String.format("%.1f", d) + "公里";
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public static String isMobileNO(EditText edit) {
		String mobiles = getEditValue(edit);
		if (mobiles != null) {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			if (!m.matches()) {
				edit.setError("电话号码格式不正确");
				return null;
			}
		} else {
			edit.setError("电话号码格式不正确");
		}
		return mobiles;
	}
}
