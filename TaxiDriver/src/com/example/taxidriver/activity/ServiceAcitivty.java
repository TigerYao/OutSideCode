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
	String types[] = { "��ʳ", "�в�", "���", "�ư�", "������", "����", "��ݾƵ�", "ATM",
			"����", "����", "����վ", "ͣ����", "����վ", "��վ", "����", "����", "ϴԡ", "KTV",
			"��ӰԺ" };
	private ListView lv;
	int oldPosition = -1;
	ImageButton olderView = null;
	private SlideMenu slideMenu;
	private AMap aMap;
	private MapView mapView;
	private LatLonPoint lp;
	private DriverInfo info;
	private String deepType = "";// poi��������
	private int searchType = 0;// ��������
	private int tsearchType = 0;// ��ǰѡ����������
	private PoiResult poiResult; // poi���صĽ��
	private int currentPage = 0;// ��ǰҳ�棬��0��ʼ����
	private PoiSearch.Query query;// Poi��ѯ������

	private PoiOverlay poiOverlay;// poiͼ��
	private List<PoiItem> poiItems;// poi����
	private Marker detailMarker;// ��ʾMarker������
	LinearLayout tool_dialog;
	private Marker locationMarker; // ѡ��ĵ�

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
		title_bar_name.setText("�ܱ߷���");
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
		mapView.onCreate(savedInstanceState);// �˷���������д
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
				.title("����λ�ã�\n" + info.getMyAddress()));
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
	 * ע�����
	 */
	private void registerListener() {
		aMap.setOnMarkerClickListener(this);// ��ӵ��marker�����¼�
		aMap.setInfoWindowAdapter(this);// �����ʾinfowindow�����¼�
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);
	}

	// public void searchPOI(int position) {
	// progDialog = new ProgressDialog(this);
	// progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// progDialog.setIndeterminate(false);
	// progDialog.setCancelable(true);
	// progDialog.setMessage("���ڻ�ȡ������Ϣ");
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
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("����������");
		progDialog.show();
	}

	/**
	 * ���ؽ��ȿ�
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * ��ʼ����poi����
	 */
	protected void doSearchQuery() {
		showProgressDialog();// ��ʾ���ȿ�
		currentPage = 0;
		slideMenu.closeMenu();
		query = new PoiSearch.Query("", deepType, null);// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query.setPageSize(10);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(currentPage);// ���ò��һҳ

		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 5000, true));//
			poiSearch.searchPOIAsyn();// �첽����
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
		dissmissProgressDialog();// ���ضԻ���
		if (rCode == 0) {
			if (result != null) {// ����poi�Ľ��
				if (detailMarker != null) {
					StringBuffer sb = new StringBuffer(result.getSnippet());
					if ((result.getGroupbuys() != null && result.getGroupbuys()
							.size() > 0)
							|| (result.getDiscounts() != null && result
									.getDiscounts().size() > 0)) {

						if (result.getGroupbuys() != null
								&& result.getGroupbuys().size() > 0) {// ȡ��һ���Ź���Ϣ
							sb.append("\n�Ź���"
									+ result.getGroupbuys().get(0).getDetail());
						}
						if (result.getDiscounts() != null
								&& result.getDiscounts().size() > 0) {// ȡ��һ���Ż���Ϣ
							sb.append("\n�Żݣ�"
									+ result.getDiscounts().get(0).getDetail());
						}
						int i = 0;
						int index = 0;
						while (index < sb.length()) {// ����̫�����ָ���ʾ
							if (sb.charAt(index) == '\n') {// �Ѿ��л��У����¼���
								i = 0;
							}

							index++;
							i++;

							if (i % 25 == 0) {
								sb.insert(index, '\n');
							}
						}

					} else {
						sb = new StringBuffer("��ַ��" + result.getSnippet()
								+ "\n�绰��" + result.getTel() + "\n���ͣ�"
								+ result.getTypeDes());
					}
					sb = getDeepInfo(result, sb);// ����poi���������Ϣ
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
	 * POI�����ص�
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// ���ضԻ���
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// ����poi�Ľ��
				if (result.getQuery().equals(query)) {// �Ƿ���ͬһ��
					poiResult = result;
					poiItems = poiResult.getPois();// ȡ�õ�һҳ��poiitem���ݣ�ҳ��������0��ʼ
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// ����������poiitem����ʱ���᷵�غ��������ؼ��ֵĳ�����Ϣ
					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();// ����֮ǰ��ͼ��
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
	 * poiû�����������ݣ�����һЩ�Ƽ����е���Ϣ
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "�Ƽ�����\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "��������:" + cities.get(i).getCityName() + "��������:"
					+ cities.get(i).getCityCode() + "���б���:"
					+ cities.get(i).getAdCode() + "\n";
		}
		// ToastUtil.show(PoiAroundSearchActivity.this, infomation);

	}

	/**
	 * POI�����Ϣ��ȡ
	 */
	private StringBuffer getDeepInfo(PoiItemDetail result,
			StringBuffer sbuBuffer) {
		switch (result.getDeepType()) {
		// ���������Ϣ
		case DINING:
			if (result.getDining() != null) {
				Dining dining = result.getDining();
				sbuBuffer
						.append("\n��ϵ��" + dining.getTag() + "\n��ɫ��"
								+ dining.getRecommend() + "\n��Դ��"
								+ dining.getDeepsrc());
			}
			break;
		// �Ƶ������Ϣ
		case HOTEL:
			if (result.getHotel() != null) {
				Hotel hotel = result.getHotel();
				sbuBuffer.append("\n��λ��" + hotel.getLowestPrice() + "\n������"
						+ hotel.getHealthRating() + "\n��Դ��"
						+ hotel.getDeepsrc());
			}
			break;
		// ���������Ϣ
		case SCENIC:
			if (result.getScenic() != null) {
				Scenic scenic = result.getScenic();
				sbuBuffer
						.append("\n��Ǯ��" + scenic.getPrice() + "\n�Ƽ���"
								+ scenic.getRecommend() + "\n��Դ��"
								+ scenic.getDeepsrc());
			}
			break;
		// ӰԺ�����Ϣ
		case CINEMA:
			if (result.getCinema() != null) {
				Cinema cinema = result.getCinema();
				sbuBuffer.append("\nͣ����" + cinema.getParking() + "\n��飺"
						+ cinema.getIntro() + "\n��Դ��" + cinema.getDeepsrc());
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
	private BusRouteResult busRouteResult;// ����ģʽ��ѯ���
	private DriveRouteResult driveRouteResult;// �ݳ�ģʽ��ѯ���
	private WalkRouteResult walkRouteResult;// ����ģʽ��ѯ���
	private int routeType = 2;// 1������ģʽ��2����ݳ�ģʽ��3������ģʽ
	private int busMode = RouteSearch.BusDefault;// ����Ĭ��ģʽ
	private int drivingMode = RouteSearch.DrivingDefault;// �ݳ�Ĭ��ģʽ
	private int walkMode = RouteSearch.WalkDefault;// ����Ĭ��ģʽ
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
	 * ����·�߲�ѯ�ص�
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);

				aMap.clear();// �����ͼ�ϵ����и�����
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
	 * �ݳ�����ص�
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// �����ͼ�ϵ����и�����
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
	 * ����·�߽���ص�
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// �����ͼ�ϵ����и�����
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
	// * ѡ�񹫽�ģʽ
	// */
	// private void busRoute() {
	// routeType = 1;// ��ʶΪ����ģʽ
	// busMode = RouteSearch.BusDefault;
	// }
	//
	// /**
	// * ѡ��ݳ�ģʽ
	// */
	// private void drivingRoute() {
	// routeType = 2;// ��ʶΪ�ݳ�ģʽ
	// drivingMode = RouteSearch.DrivingSaveMoney;
	// }
	//
	// /**
	// * ѡ����ģʽ
	// */
	// private void walkRoute() {
	// routeType = 3;// ��ʶΪ����ģʽ
	// walkMode = RouteSearch.WalkMultipath;
	//
	// }

	/**
	 * ��ʼ����·���滮����
	 */
	private void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// ����·���滮
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "����", 0);// ��һ��������ʾ·���滮�������յ㣬�ڶ���������ʾ������ѯģʽ��������������ʾ������ѯ�������ţ����ĸ�������ʾ�Ƿ����ҹ�೵��0��ʾ������
			routeSearch.calculateBusRouteAsyn(query);// �첽·���滮����ģʽ��ѯ
		} else if (routeType == 2) {// �ݳ�·���滮
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
					null, null, "");// ��һ��������ʾ·���滮�������յ㣬�ڶ���������ʾ�ݳ�ģʽ��������������ʾ;���㣬���ĸ�������ʾ�������򣬵����������ʾ���õ�·
			routeSearch.calculateDriveRouteAsyn(query);// �첽·���滮�ݳ�ģʽ��ѯ
		} else if (routeType == 3) {// ����·���滮
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// �첽·���滮����ģʽ��ѯ
		}
	}

}