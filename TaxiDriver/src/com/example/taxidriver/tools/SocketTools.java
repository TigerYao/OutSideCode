package com.example.taxidriver.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.location.Location;

@SuppressLint("SimpleDateFormat")
public class SocketTools {
	public String createPower(String phonenum) {
		String phones = "0" + phonenum;

		String phone = "";
		for (int i = 0; i < 12; i += 2) {
			phone = phone + phones.substring(i, i + 2) + " ";
		}
		System.out.println(phone);
		String sum = jianquan(phone);
		System.out.println(sum);
		return sum;
	}

	public String createSend(Location location, String phonenum) {
		String nums = "0" + phonenum;
		String phone = "";
		if (nums.length() >11)
			for (int i = 0; i < 12; i += 2) {
				phone = phone + nums.substring(i, i + 2) + " ";
			}
		String messageHead = "02 04 00 1c " + phone + "00 03 "
				+ createBody(location);
		int jiaoyan = getCheckNum(messageHead);
		System.out.println(messageHead + "***jiaoyan****" + jiaoyan);
		String checkCode = Integer.toHexString(jiaoyan);
		if (checkCode.length() < 2) {
			checkCode = "0" + checkCode;
		}
		String body = messageHead + checkCode;
		// System.out.println("body****"+body);
		String bos[] = body.split(" ");
		String newBody = " ";
		for (int i = 0; i < bos.length; i++) {
			String currentS = bos[i];
			if (i == 0) {
				newBody = "";
			}
			if ("7e".equals(currentS)) {
				currentS = "7d 02";
			} else if ("7d".equals(currentS)) {
				currentS = "7d 01";
			}
			newBody = newBody + currentS + " ";
		}
		System.out.println(newBody);
		return "7e " + newBody + "7e";
	}

	private String createB(String lon, String lat, String speed, String higter,
			String direct, String time) {
		Long ll = Long.parseLong(lon);
		Long lla = Long.parseLong(lat);

		String lati = Long.toHexString(lla);
		String ln = Long.toHexString(ll);

		String information = dealNum(lati, 8) + dealNum(ln, 8)
				+ dealNum(speed, 4) + dealNum(higter, 4) + dealNum(direct, 4)
				+ time;
		String body = "00 00 00 00 00 00 00 03 " + information;
		return body;
	}

	private String dealNum(String nums, int count) {
		int len = nums.length();
		if (len < count) {
			int c = count - len;
			for (int i = 0; i < c; i++) {
				nums = "0" + nums;
			}
		}
		String phone = "";
		for (int i = 0; i < count; i += 2) {
			phone = phone + nums.substring(i, i + 2) + " ";
		}
		return phone;
	}

	private String createBody(Location location) {
		int bear = (int) location.getBearing();
		double lat = location.getLatitude();
		Double lon = location.getLongitude();
		int speed = (int) location.getSpeed();
		int higter = (int) location.getAltitude();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yy MM dd HH mm ss ");
		String time = format.format(date);

		System.out.println(dealNums(lat) + "*****" + dealNums(lon) + "****"
				+ lon);

		return createB(dealNums(lon), dealNums(lat),
				Integer.toHexString(speed), Integer.toHexString(higter),
				Integer.toHexString(bear), time);

	}

	private String dealNums(double d) {
		String current = d + "";
		int index = current.indexOf(".");
		String start = current.substring(0, index);
		String end = current.substring(index + 1, current.length());
		if (end.length() > 6) {
			end = end.substring(0, 6);
		}
		if (end.length() < 6) {
			for (int i = 0; i < 6 - end.length(); i++) {
				end = end + "0";
			}
		}
		return start + end;
	}

	public byte[] toByteArray(String send) {
		byte[] b = new byte[send.split(" ").length];
		String ss[] = send.split(" ");
		for (int i = 0; i < b.length; i++) {
			// sb.append((0xff & Integer.parseInt(ss, 16)));
			b[i] = (byte) (0xff & Integer.parseInt(ss[i], 16));
		}
		return b;
	}

	private String jianquan(String phonenum) {
		String s = "01 02 00 06 " + phonenum;
		phonenum = phonenum.replace(" ", "");
		String jianqu = str2HexStr((String) phonenum.subSequence(6,
				phonenum.length()));

		s = s + "00 03 " + jianqu;

		String nums[] = s.split(" ");
		int sum = Integer.parseInt(nums[0], 16);
		for (int i = 1; i < nums.length; i++) {
			try {
				if (!"".equals(s)) {
					sum = sum ^ Integer.parseInt(nums[i], 16);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// System.out.println(s+"****");
		s = "7e " + s + " " + Integer.toHexString(sum) + " 7e";
		return s;
	}

	public int getCheckNum(String s) {
		System.out.println(s);
		String nums[] = s.split(" ");
		int sum = Integer.parseInt(nums[0], 16);
		for (int i = 1; i < nums.length; i++) {
			try {
				if (!"".equals(s)) {
					sum = sum ^ Integer.parseInt(nums[i], 16);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(sum + "****");
		return sum;
	}

	public String str2HexStr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}
}
