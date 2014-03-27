package com.example.taxidriver.activity;

import java.util.List;

import net.ting.sliding.SlideMenu;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.BusRouteOverlay;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.Cinema;
import com.amap.api.services.poisearch.Dining;
import com.amap.api.services.poisearch.Hotel;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.api.services.poisearch.Scenic;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.taxidriver.R;
import com.example.taxidriver.bean.DriverInfo;
import com.example.taxidriver.tools.ToastUtil;

public class ServiceAcitivty extends Activity implements OnClickListener,
		OnPoiSearchListener, OnMarkerClickListener, InfoWindowAdapter,
		OnInfoWindowClickListener, OnRouteSearchListener {

	int drawables[] = { R.drawable.fuwu1, R.drawable.fuwu2, R.drawable.fuwu3,
			R.drawable.fuwu4, R.drawable.fuwu5, R.drawable.fuwu6,
			R.drawable.fuwu7, R.drawable.fuwu8, R.drawable.fuwu9,
			R.drawable.wufu10, R.drawable.wufu11, R.drawable.fuwu12,
			R.drawable.fuwu13, R.drawable.wufu14, R.drawable.fuwu15,
			R.drawable.fuwu16, R.drawable.wufu17, R.drawable.wufu18,
			R.drawable.wufu19 };
	int drawabless[] = { R.drawable.p05, R.drawable.p56, R.drawable.p07,
			R.drawable.p09, R.drawable.p11, R.drawable.p22, R.drawable.p20,
			R.drawable.p23, R.drawable.p24, R.drawable.p54, R.drawable.p36,
			R.drawable.p33, R.drawable.p34, R.drawable.p36, R.drawable.p44,
			R.drawable.p46, R.drawable.p57, R.drawable.p47, R.drawable.p48 };
	String types[] = { "美食", "中餐", "快餐", "酒吧", "咖啡厅", "宾馆", "快捷酒店", "ATM",
			"银行", "公厕", "加油站", "停车场", "公交站", "火车站", "超市", "网吧", "洗浴", "KTV",
			"电影院" };
	private ListView lv;
	int oldPosition = -1;
	ImageButton olderView = null;
	private SlideMenu slideMenu;
	private AMap aMap;
	private MapView mapView;
	private LatLonPoint lp;
	private DriverInfo info;
	private String deepType = "";// poi搜索类型
	private int searchType = 0;// 搜索类型
	private int tsearchType = 0;// 当前选择搜索类型
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类

	private PoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据
	private Marker detailMarker;// 显示Marker的详情
	LinearLayout tool_dialog;
	private Marker locationMarker; // 选择的点

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.service);
		initView(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	/**
	 * @param savedInstanceState
	 */
	private void initView(Bundle savedInstanceState) {
		lv = (ListView) findViewById(R.id.lv);
		TextView title_bar_name = (TextView) findViewById(R.id.title_bar_name);
		title_bar_name.setText("周边服务");
		tool_dialog = (LinearLayout) findViewById(R.id.tool_dialog);
		tool_dialog.setVisibility(View.GONE);
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);

		// ImageView menuImg = (ImageView)
		// findViewById(R.id.title_bar_menu_btn);
		lv.setAdapter(new BaseAdapter() {

			@Override
			public View getView(final int position, View v, ViewGroup arg2) {
				// TODO Auto-generated method stub
				v = getLayoutInflater().inflate(R.layout.layout10item, null);
				ImageButton imga = (ImageButton) v
						.findViewById(R.id.itemText_ten);
				imga.setImageResource(drawables[position]);
				imga.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (oldPosition != position) {
							if (oldPosition != -1) {
								olderView
										.setImageResource(drawables[oldPosition]);
							}
							olderView = ((ImageButton) v);
							oldPosition = position;
							olderView.setImageResource(drawabless[position]);
						}
						deepType = types[position];
						tsearchType = position;
						doSearchQuery();
					}
				});
				return v;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return drawables.length;
			}
		});
		setMapInit(savedInstanceState);
		initRadioGroup();
	}

	/**
	 * @param savedInstanceState
	 */
	private void setMapInit(Bundle savedInstanceState) {
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		info = DriverInfo.getInstance();
		if (info.getLat() != -1) {
			lp = new LatLonPoint(info.getLat(), info.getLon());
		}
		if (aMap == null) {
			aMap = mapView.getMap();
			registerListener();
			LatLng startPoint = new LatLng(info.getLat(), info.getLon());
			if (startPoint != null)
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
						15));
			drawMe();
		}
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
	}

	/**
	 * 
	 */
	private void drawMe() {
		locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
				.position(new LatLng(lp.getLatitude(), lp.getLongitude()))
				.title("您的位置：\n" + info.getMyAddress()));
		locationMarker.showInfoWindow();
	}

	protected void onResume() {

		mapView.onResume();
		if (slideMenu.isMainScreenShowing()) {
			slideMenu.openMenu();
		} else {
			slideMenu.closeMenu();
		}
		super.onResume();
	};

	protected void onPause() {
		mapView.onPause();
		super.onPause();
	};

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	};

	ProgressDialog progDialog;

	private PoiSearch poiSearch;

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
		aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);
	}

	// public void searchPOI(int position) {
	// progDialog = new ProgressDialog(this);
	// progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// progDialog.setIndeterminate(false);
	// progDialog.setCancelable(true);
	// progDialog.setMessage("正在获取服务信息");
	// progDialog.show();
	// new Thread(new MyRungnable(position)).start();
	// }

	class MyRungnable implements Runnable {
		int postion;

		public MyRungnable(int position) {
			// TODO Auto-generated constructor stub
			this.postion = position;
		}

		@Override
		public void run() {
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_bar_menu_btn:
			if (slideMenu.isMainScreenShowing()) {
				slideMenu.openMenu();
			} else {
				slideMenu.closeMenu();
			}
			break;
		case R.id.search_way:
			tool_dialog.setVisibility(View.GONE);
			searchRouteResult(startPoint, endPoint);
			break;
		case R.id.title_bar_home:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索中");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		showProgressDialog();// 显示进度框
		currentPage = 0;
		slideMenu.closeMenu();
		query = new PoiSearch.Query("", deepType, null);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 5000, true));//
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0) {
			if (result != null) {// 搜索poi的结果
				if (detailMarker != null) {
					StringBuffer sb = new StringBuffer(result.getSnippet());
					if ((result.getGroupbuys() != null && result.getGroupbuys()
							.size() > 0)
							|| (result.getDiscounts() != null && result
									.getDiscounts().size() > 0)) {

						if (result.getGroupbuys() != null
								&& result.getGroupbuys().size() > 0) {// 取第一条团购信息
							sb.append("\n团购："
									+ result.getGroupbuys().get(0).getDetail());
						}
						if (result.getDiscounts() != null
								&& result.getDiscounts().size() > 0) {// 取第一条优惠信息
							sb.append("\n优惠："
									+ result.getDiscounts().get(0).getDetail());
						}
						int i = 0;
						int index = 0;
						while (index < sb.length()) {// 描述太长，分割显示
							if (sb.charAt(index) == '\n') {// 已经有换行，重新计算
								i = 0;
							}

							index++;
							i++;

							if (i % 25 == 0) {
								sb.insert(index, '\n');
							}
						}

					} else {
						sb = new StringBuffer("地址：" + result.getSnippet()
								+ "\n电话：" + result.getTel() + "\n类型："
								+ result.getTypeDes());
					}
					sb = getDeepInfo(result, sb);// 加入poi搜索深度信息
					detailMarker.setSnippet(sb.toString());
				}

			} else {
				Toast.makeText(ServiceAcitivty.this, R.string.no_result, 3000)
						.show();
			}
		} else if (rCode == 27) {
			Toast.makeText(ServiceAcitivty.this, R.string.error_network, 3000);
		} else if (rCode == 32) {
			Toast.makeText(ServiceAcitivty.this, R.string.error_key, 3000);
		} else {
			Toast.makeText(ServiceAcitivty.this, R.string.error_other, 300);
		}
	}

	/**
	 * POI搜索回调
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();// 清理之前的图标
						drawMe();
						poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						Toast.makeText(ServiceAcitivty.this,
								R.string.no_result, 3000).show();
					}
				}
			} else {
				Toast.makeText(ServiceAcitivty.this, R.string.no_result, 3000)
						.show();
			}
		} else if (rCode == 27) {
			Toast.makeText(ServiceAcitivty.this, R.string.error_network, 3000)
					.show();
		} else if (rCode == 32) {
			Toast.makeText(ServiceAcitivty.this, R.string.error_key, 3000)
					.show();
		} else {
			Toast.makeText(ServiceAcitivty.this, R.string.error_other, 3000)
					.show();
		}
	}

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		// ToastUtil.show(PoiAroundSearchActivity.this, infomation);

	}

	/**
	 * POI深度信息获取
	 */
	private StringBuffer getDeepInfo(PoiItemDetail result,
			StringBuffer sbuBuffer) {
		switch (result.getDeepType()) {
		// 餐饮深度信息
		case DINING:
			if (result.getDining() != null) {
				Dining dining = result.getDining();
				sbuBuffer
						.append("\n菜系：" + dining.getTag() + "\n特色："
								+ dining.getRecommend() + "\n来源："
								+ dining.getDeepsrc());
			}
			break;
		// 酒店深度信息
		case HOTEL:
			if (result.getHotel() != null) {
				Hotel hotel = result.getHotel();
				sbuBuffer.append("\n价位：" + hotel.getLowestPrice() + "\n卫生："
						+ hotel.getHealthRating() + "\n来源："
						+ hotel.getDeepsrc());
			}
			break;
		// 景区深度信息
		case SCENIC:
			if (result.getScenic() != null) {
				Scenic scenic = result.getScenic();
				sbuBuffer
						.append("\n价钱：" + scenic.getPrice() + "\n推荐："
								+ scenic.getRecommend() + "\n来源："
								+ scenic.getDeepsrc());
			}
			break;
		// 影院深度信息
		case CINEMA:
			if (result.getCinema() != null) {
				Cinema cinema = result.getCinema();
				sbuBuffer.append("\n停车：" + cinema.getParking() + "\n简介："
						+ cinema.getIntro() + "\n来源：" + cinema.getDeepsrc());
			}
			break;
		default:
			break;
		}
		return sbuBuffer;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (poiOverlay != null && poiItems != null && poiItems.size() > 0) {
			detailMarker = marker;
			tool_dialog.setVisibility(View.VISIBLE);
			endPointname.setText(marker.getSnippet());
			LatLng ll = marker.getPosition();
			endPoint = new LatLonPoint(ll.latitude, ll.longitude);
		}
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		System.out.println(marker.getTitle() + "*****" + marker.getSnippet());
	}

	private RadioGroup radiog;
	private AutoCompleteTextView startPointname, endPointname;
	private LatLonPoint startPoint, endPoint;
	private BusRouteResult busRouteResult;// 公交模式查询结果
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private int routeType = 2;// 1代表公交模式，2代表驾车模式，3代表步行模式
	private int busMode = RouteSearch.BusDefault;// 公交默认模式
	private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private RouteSearch routeSearch;

	private void initRadioGroup() {
		startPointname = (AutoCompleteTextView) findViewById(R.id.startpoint);
		if (info.getMyAddress() != null) {
			startPointname.setText(info.getMyAddress());
			startPoint = new LatLonPoint(info.getLat(), info.getLon());
		}
		endPointname = (AutoCompleteTextView) findViewById(R.id.endpoint);
		radiog = (RadioGroup) findViewById(R.id.lay13_rg);
		radiog.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int index) {
				// TODO Auto-generated method stub
				routeType = index + 1;
			}
		});
	}

	/**
	 * 公交路线查询回调
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);

				aMap.clear();// 清理地图上的所有覆盖物
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
						busPath, busRouteResult.getStartPos(),
						busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
			} else {
				ToastUtil.show(ServiceAcitivty.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_key);
		} else {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_other);
		}
	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				ToastUtil.show(ServiceAcitivty.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_key);
		} else {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_other);
		}
	}

	/**
	 * 步行路线结果回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
						aMap, walkPath, walkRouteResult.getStartPos(),
						walkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
			} else {
				ToastUtil.show(ServiceAcitivty.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_key);
		} else {
			ToastUtil.show(ServiceAcitivty.this, R.string.error_other);
		}
	}

	// /**
	// * 选择公交模式
	// */
	// private void busRoute() {
	// routeType = 1;// 标识为公交模式
	// busMode = RouteSearch.BusDefault;
	// }
	//
	// /**
	// * 选择驾车模式
	// */
	// private void drivingRoute() {
	// routeType = 2;// 标识为驾车模式
	// drivingMode = RouteSearch.DrivingSaveMoney;
	// }
	//
	// /**
	// * 选择步行模式
	// */
	// private void walkRoute() {
	// routeType = 3;// 标识为步行模式
	// walkMode = RouteSearch.WalkMultipath;
	//
	// }

	/**
	 * 开始搜索路径规划方案
	 */
	private void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "北京", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == 2) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
					null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == 3) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

}