package com.example.taxidriver.bean;

/* <param name="strMobile">���⳵�豸�ֻ���</param>
 /// <param name="strName">˾���������û�����</param>
 /// <param name="strPwd">����</param>
 /// <param name="strEmail">����</param>
 /// <param name="strCode">���ƺ�</param>*/
public class DriverInfo {
	private static DriverInfo instance = null;
	private String strMobile, strName, strPwd, strEmail, strCode, uid,
			myAddress, city;
	private double lat, lon;

	public void setMyAddress(String myAddress) {
		if (myAddress.length() > 10)
			myAddress = myAddress.subSequence(0, 10) + "\n"
					+ myAddress.subSequence(10, myAddress.length());
		this.myAddress = myAddress;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public String getMyAddress() {
		if (myAddress == null) {
			myAddress = "δ֪";
		}
		return myAddress;
	}

	public double getLat() {
		return lat;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public static void setInstance(DriverInfo instance) {
		DriverInfo.instance = instance;
	}

	private DriverInfo() {
		// TODO Auto-generated constructor stub
	}

	public void putValue(String strMobile, String strName, String strPwd,
			String strEmail, String strCode) {
		this.setStrMobile(strMobile);
		this.setStrName(strName);
		this.setStrPwd(strPwd);
		this.setStrEmail(strEmail);
		this.setStrCode(strCode);

	}

	public static DriverInfo getInstance() {
		if (instance == null) {
			instance = new DriverInfo();
		}
		System.out.println(instance.toString());
		return instance;
	}

	public String getStrMobile() {
		return strMobile;
	}

	public void setStrMobile(String strMobile) {
		this.strMobile = strMobile;
	}

	private DriverInfo(String strMobile, String strName, String strPwd,
			String strEmail, String strCode) {
		this();

	}

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	public String getStrPwd() {
		return strPwd;
	}

	public void setStrPwd(String strPwd) {
		this.strPwd = strPwd;
	}

	public String getStrEmail() {
		return strEmail;
	}

	public void setStrEmail(String strEmail) {
		this.strEmail = strEmail;
	}

	public String getStrCode() {
		return strCode;
	}

	public void setStrCode(String strCode) {
		this.strCode = strCode;
	}

	@Override
	public String toString() {
		return "DriverInfo [strMobile=" + strMobile + ", strName=" + strName
				+ ", strPwd=" + strPwd + ", strEmail=" + strEmail
				+ ", strCode=" + strCode + ", lat=" + lat + ", lon=" + lon
				+ "]";
	}

	public void clear() {
		this.strMobile = null;
		this.strName = null;
		this.strPwd = null;
		this.strEmail = null;
		this.strCode = null;
	};
}
