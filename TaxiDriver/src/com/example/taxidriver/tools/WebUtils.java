package com.example.taxidriver.tools;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.example.taxidriver.bean.DriverInfo;

/**
 * 
 * @author yaohu
 * 
 */
public class WebUtils {
	private static final String NAMESPACE = "http://tempuri.org/";

	// WebService地址
	private static String URL = "http://210.51.1.22/zkweb/CallService.asmx";

	public static SoapObject getObject(String[] keys, String method,
			Map<String, ? extends Object> info) {
		SoapObject request = new SoapObject(NAMESPACE, method);
		for (String key : keys) {

			String name = key;
			Object value = info.get(key);

			request.addProperty(name, value);
		}
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		HttpTransportSE htse = new HttpTransportSE(URL);
		envelope.setOutputSoapObject(request);
		try {

			htse.call(NAMESPACE + method, envelope);
			SoapObject result = (SoapObject) envelope.getResponse();
			System.out.println("***&&&&&&&&&&&&&&&***");
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static SoapObject getObject(List<String> keys, String method,
			Map<String, ? extends Object> info) {
		SoapObject request = new SoapObject(NAMESPACE, method);
		for (String key : keys) {
			String name = key;
			String value = (String) info.get(key);
			request.addProperty(name, value);
		}
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		HttpTransportSE htse = new HttpTransportSE(URL);
		envelope.setOutputSoapObject(request);
		try {
			htse.call(NAMESPACE + method, envelope);
			SoapObject result = (SoapObject) envelope.getResponse();
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getString(List<String> keys, String method,
			Map<String, ? extends Object> info) {
		SoapObject request = new SoapObject(NAMESPACE, method);
		for (String key : keys) {
			String name = key;
			String value = (String) info.get(key);
			request.addProperty(name, value);
		}
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		HttpTransportSE htse = new HttpTransportSE(URL);
		envelope.setOutputSoapObject(request);
		try {
			htse.call(NAMESPACE + method, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			if (result == null) {
				System.out.println("nnnnnnuuuulllll");
			}
			return result.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean taxiAccept(String iCallId) {
		boolean isOk = false;
		DriverInfo info = DriverInfo.getInstance();
		SoapObject request = new SoapObject(NAMESPACE, "TaxiAccept");
		request.addProperty("iCallId", iCallId);
		request.addProperty("strTaxiMobile", info.getStrMobile());
		String strPwd = info.getStrPwd();
		request.addProperty("strPwd", strPwd);
		System.out.println(iCallId + "****" + info.getStrMobile() + "****"
				+ info.getStrPwd());
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		HttpTransportSE htse = new HttpTransportSE(URL);
		envelope.setOutputSoapObject(request);
		try {
			htse.call(NAMESPACE + "TaxiAccept", envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			// SoapObject re=(SoapObject) envelope.bodyIn;
			System.out.println(result + "***");
			isOk = Boolean.parseBoolean(result.toString());
			// System.out.println(re+"***");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isOk;
	}

	public static boolean Login(String strSIM, String strPwd) {
		boolean isOk = false;
		// String keys[] = { "strSIM", "strPwd", "userType" };
		SoapObject request = new SoapObject(NAMESPACE, "LoginDriver");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		request.addProperty("strSIM", strSIM);
		request.addProperty("strPwd", strPwd);
		request.addProperty("userType", "1");
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				"http://210.51.1.22/zkweb/Service.asmx");
		envelope.setOutputSoapObject(request);
		try {
			androidHttpTransport.call("http://tempuri.org/LoginDriver",
					envelope);

			SoapObject resultString = (SoapObject) envelope.bodyIn;
			String result = resultString.getProperty("LoginDriverResult")
					.toString();

			if (result != null && !result.equals("anyType{}")) {
				JSONObject obje = new JSONObject(result);
				String uid = obje.getString("uid");
				String name = obje.getString("name");
				String ccode = obje.getString("ccode");
				String mobile = obje.getString("mobile");
				String company = obje.getString("company");
				DriverInfo info = DriverInfo.getInstance();
				info.setStrPwd(strPwd);
				if (uid != null && !uid.equals("")) {
					info.setUid(uid);
				}
				if (name != null && !name.equals("")) {
					info.setStrName(name);
				}
				if (ccode != null && !ccode.equals("")) {
					info.setStrCode(ccode);
				}
				if (mobile != null && !mobile.equals("")) {
					info.setStrMobile(mobile);
				}
				if (company != null) {
					info.setStrEmail(company);
				}
				isOk = true;
				System.out.println(info.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOk;
	}

	public static boolean freeDriving(String iCallId, String taxiMobile,
			String pwd) {
		SoapObject request = new SoapObject(NAMESPACE, "FreeDriving");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		/**
		 * <strTaxiMobile>string</strTaxiMobile> <strPwd>string</strPwd>
		 */
		boolean isOk = false;
		envelope.dotNet = true;
		request.addProperty("iCallId", iCallId);
		request.addProperty("strTaxiMobile", taxiMobile);
		request.addProperty("strPwd", pwd);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				"http://210.51.1.22/zkweb/Service.asmx");
		envelope.setOutputSoapObject(request);
		try {
			androidHttpTransport.call("http://tempuri.org/FreeDriving",
					envelope);

			// SoapObject resultString = (SoapObject) envelope.bodyIn;
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			System.out.println(result + "***");
			isOk = Boolean.parseBoolean(result.toString());
		} catch (Exception e) {

		}
		return isOk;
	}

	public static String getUpdate(int version) {

		String isOk = null;
		// String keys[] = { "strSIM", "strPwd", "userType" };
		SoapObject request = new SoapObject(NAMESPACE, "CheckNewVersion");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		request.addProperty("iVersion", version);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				"http://210.51.1.22/zkweb/Service.asmx");
		envelope.setOutputSoapObject(request);
		try {
			androidHttpTransport.call("http://tempuri.org/CheckNewVersion",
					envelope);

			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			if (resultString != null) {
				isOk = resultString.toString();
			}
			System.out.println(isOk + "*****");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOk;

	}

	public static String getUpdateUrl(Context context) {
		String updateJson = getUpdate(getVersionName(context));
		if (updateJson != null) {
			try {
				JSONObject object = new JSONObject(updateJson);
				String url = object.getString("url1");
				return url;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}

	private static int getVersionName(Context context) {
		// 获取packagemanager的实例
		String version = "";
		int ccode = 0;
		try {
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			version = packInfo.versionName;
			ccode = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ccode;
	}

	public static String getCheckNum(String phoneNum) {
		String isOk = null;
		// String keys[] = { "strSIM", "strPwd", "userType" };
		SoapObject request = new SoapObject(NAMESPACE, "GetCheckNum");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		request.addProperty("strGoalSIM", phoneNum);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				"http://210.51.1.22/zkweb/Service.asmx");
		envelope.setOutputSoapObject(request);
		try {
			androidHttpTransport.call("http://tempuri.org/GetCheckNum",
					envelope);

			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			if (resultString != null) {
				isOk = resultString.toString();
			}
			System.out.println(isOk + "*****");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOk;
	}

	public static String getPwd(String phoneNum) {
		String isOk = null;
		// String keys[] = { "strSIM", "strPwd", "userType" };
		SoapObject request = new SoapObject(NAMESPACE, "GetPwd");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		request.addProperty("strMobile", phoneNum);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				"http://210.51.1.22/zkweb/Service.asmx");
		envelope.setOutputSoapObject(request);
		try {
			androidHttpTransport.call("http://tempuri.org/GetPwd", envelope);

			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			if (resultString != null) {
				isOk = resultString.toString();
			}
			System.out.println(isOk + "*****");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOk;
	}

	public static String changePwd(String phoneNum, String iCode, String pwd) {
		String isOk = null;
		// String keys[] = { "strSIM", "strPwd", "userType" };
		SoapObject request = new SoapObject(NAMESPACE, "ChangePwd ");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		request.addProperty("strMobile", phoneNum);
		request.addProperty("iCheckCode", iCode);
		request.addProperty("strNewPwd", pwd);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				"http://210.51.1.22/zkweb/CallService.asmx");
		envelope.setOutputSoapObject(request);
		try {
			androidHttpTransport
					.call("http://tempuri.org/ChangePwd ", envelope);

			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			if (resultString != null) {
				isOk = resultString.toString();
			}
			System.out.println(isOk + "*****");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOk;
	}

	public static WakeLock screenOn(Context context) {
		KeyguardManager keyManger = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		wakeLock.acquire();
		KeyguardLock keyLock = keyManger.newKeyguardLock("");
		keyLock.disableKeyguard();
		System.out.println("ddddsssssddd");
		return wakeLock;
	}
}