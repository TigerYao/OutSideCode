package com.example.taxidriver.bean;

import java.io.Serializable;

import com.example.taxidriver.tools.TextUitls;

/**
 * <CalledObject> <Id>int</Id> <Mobile>string</Mobile> <Start>string</Start>
 * <Goal>string</Goal> <X>double</X> <Y>double</Y> <SLon>double</SLon>
 * <SLat>double</SLat> <GLon>double</GLon> <GLat>double</GLat>
 * <TaxiMobile>string</TaxiMobile> <CalledTime>string</CalledTime>
 * <CalledState>int</CalledState> <Intro>string</Intro>
 * 
 * @author yaohu
 * 
 */
public class ClientInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "ClientInfo [id=" + id + ", mobile=" + mobile + ", start="
				+ start + ", goal=" + goal + ", x=" + x + ", y=" + y
				+ ", sLon=" + sLon + ", sLat=" + sLat + ", gLon=" + gLon
				+ ", gLat=" + gLat + ", taxiMobile=" + taxiMobile
				+ ", calledTime=" + calledTime + ", calledState=" + calledState
				+ ", intro=" + intro + "]";
	}

	String id, mobile, start, goal, x, y, sLon, sLat, gLon, gLat, taxiMobile,
			calledTime, calledState, intro,distance;

	public ClientInfo() {
		// TODO Auto-generated constructor stub
	}
public void setDistance(String distance) {
	this.distance = distance;
}
public String getDistance() {
	if(distance==null)
		try {
			distance = TextUitls.getDistatce(DriverInfo.getInstance().getLat(),
					Double.parseDouble(getsLat()), DriverInfo.getInstance().getLon(),
					Double.parseDouble(getsLon()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return distance;
}
	public ClientInfo(String id, String mobile, String start, String goal,
			String x, String y, String sLon, String sLat, String gLon,
			String gLat, String taxiMobile, String calledTime,
			String calledState, String intro) {
		super();
		this.id = id;
		this.mobile = mobile;
		this.start = start;
		this.goal = goal;
		this.x = x;
		this.y = y;
		this.sLon = sLon;
		this.sLat = sLat;
		this.gLon = gLon;
		this.gLat = gLat;
		this.taxiMobile = taxiMobile;
		this.calledTime = calledTime;
		this.calledState = calledState;
		this.intro = intro;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getsLon() {
		return sLon;
	}

	public void setsLon(String sLon) {
		this.sLon = sLon;
	}

	public String getsLat() {
		return sLat;
	}

	public void setsLat(String sLat) {
		this.sLat = sLat;
	}

	public String getgLon() {
		return gLon;
	}

	public void setgLon(String gLon) {
		this.gLon = gLon;
	}

	public String getgLat() {
		return gLat;
	}

	public void setgLat(String gLat) {
		this.gLat = gLat;
	}

	public String getTaxiMobile() {
		return taxiMobile;
	}

	public void setTaxiMobile(String taxiMobile) {
		this.taxiMobile = taxiMobile;
	}

	public String getCalledTime() {
		return calledTime;
	}

	public void setCalledTime(String calledTime) {
		this.calledTime = calledTime;
	}

	public String getCalledState() {
		return calledState;
	}

	public void setCalledState(String calledState) {
		this.calledState = calledState;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

}
