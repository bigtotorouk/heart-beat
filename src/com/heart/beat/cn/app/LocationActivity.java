package com.heart.beat.cn.app;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.wifi.WifiConfiguration.Status;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.google.map.test.xml.PointInf;
import com.google.map.test.xml.PullParseService;
import com.heart.beat.cn.util.ImageUtil;
import com.heart.beat.cn.util.MyLocation;
import com.heart.beat.cn.util.MyLocation.LocationResult;
import com.heart.beat.cn.widget.BeatsAdapter;
import com.heart.beat.cn.widget.CustomOverlay;
import com.heart.beat.cn.widget.MapListAdapter;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.response.ResponsePlace;
import com.slider.cn.app.bean.response.ResponsePlaces;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.google.place.resource.PlaceResource;
import com.slider.cn.app.google.place.resource.PlaceService;
import com.slider.cn.app.image.ImageLoaderUtil;

public class LocationActivity extends MapActivity implements OnClickListener {
	private static final String TAG = "Map_testActivity-->";
	private int status = 0;
	private final int STATUS_MAP = 0;
	private final int STATUS_LIST = 1;
	private LayoutInflater inflater;
	private View viewMap,viewList;
	
	private MapView mapView;
	private MapController mapController;
	private View popView;
	
	private List<PointInf> pointInfs = null;
	private PlaceResource placeApi;
	private ResponsePlaces  responsePlaces;
	private List<ResponsePlace> places;
	
	private Location currentLoaction; 
	private GeoPoint centerGeo;

	
	//menu 
	private final int menuMode = Menu.FIRST;
	private final int menuExit = Menu.FIRST+1;
	//signal id
	private final int LOCATION_UPDATE = 0;
	private final static int PLACES_LOADING =1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        viewMap = inflater.inflate(R.layout.map_layout, null);
        status = STATUS_MAP;
        setContentView(viewMap);
        
        mapView = (MapView) findViewById(R.id.map); 
        
        // 设置显示模式
        //mapView.setTraffic(true);//是否开发交通流量图层
        mapView.setSatellite(false);//是否打开卫星图
        //mapView.setStreetView(true);//设置是否街道模式
        mapView.setBuiltInZoomControls(true);// 设置可以缩放
        Projection projection = mapView.getProjection();
        
        //定义bubble窗口
        popView = View.inflate(this, R.layout.overlay_pop, null);
        mapView.addView(popView,new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
        popView.setVisibility(View.GONE);

        //获得中点 GeoPoint
        //currentLoaction = new LocationService(this).getLocation();
        updateLocation2();
        if(currentLoaction==null){
        	Toast.makeText(this, "Failure to locate", Toast.LENGTH_SHORT).show();
        	return;
        }
        centerGeo = new GeoPoint((int)(currentLoaction.getLatitude()*1E6), (int)(currentLoaction.getLongitude()*1E6));

        //调用mapview的getOverLays()方法，用户得到所有的图层对象
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.icon_class_bank);        
        CustomOverlay customOverlay = new CustomOverlay(drawable, this,centerGeo);
        
        mapController=mapView.getController();
        mapController.setCenter(getCenterPoint());
        mapController.setZoom(15);//设置缩放级别 看的范围就越广
        
        /* load places */
        placeApi = new PlaceResource(LocationActivity.this);
        try {
			responsePlaces = placeApi.getPlaces(currentLoaction.getLatitude(), currentLoaction.getLongitude(),PlaceService.RADIOUS_DEFAULT);
		} catch (NetException e) {
			Toast.makeText(LocationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
        /* get places from server failed */
        if(responsePlaces==null){
			responsePlaces = new ResponsePlaces();
		}
        places = responsePlaces.getObjects();

        customOverlay.addOverlay(getOverlays());
        customOverlay.setOnFocusChangeListener(onFocusChangeListener);
        //将overlay对象添加到mapview的getOverelays()里去
        mapOverlays.add(customOverlay);
        
        findViewById(R.id.switch_tolist).setOnClickListener(this);
        
        
        
    }
    /**
	 * add by slider at 2012-8-24
	 */
	private void updateLocation2(){
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location location){
		        currentLoaction = location;
		    }
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}
    
  
    /**
     * 表示我们是否显示一些路线的信息，这个通常用在地图的飞行模式时才使用，
     * 这里我们不提供任何信息仅是返回false即可。
     */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private ArrayList<OverlayItem> getOverlays(){
		ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
		OverlayItem overlayItem = null;
		//List<PointInf> pointInfs = getPointInfos();
		if(responsePlaces==null){
			responsePlaces = new ResponsePlaces();
		}else{
			if(places != null){
				//List<Drawable> drawables = getDrawablesFromPlaces();
				for (int i = 0; i < places.size(); i++) {
					ResponsePlace place = places.get(i);
					overlayItem = new OverlayItem(new GeoPoint((int)(Double.parseDouble(place.getLat())*1E6), (int)(Double.parseDouble(place.getLng())*1E6)), place.getName(), place.getLocation());
					overlayItem.setMarker(getResources().getDrawable(R.drawable.pin_green));
					overlayItems.add(overlayItem);
				}
				
			}
		}
		return overlayItems;
	}
	
	private GeoPoint getCenterPoint(){
		GeoPoint goal = null;
		int lat=0;
		int lon=0;
		if(places!=null){
			for(ResponsePlace place:places){
				lat+=(int)(Double.parseDouble(place.getLat())*1E6);
				lon+=(int)(Double.parseDouble(place.getLng())*1E6);
			}
			lat=lat/places.size();
			lon=lon/places.size();
		}else{
			return centerGeo;
		}
		
		Log.d(TAG, "lat: "+lat+" lon: "+lon);
		goal = new GeoPoint(lat, lon);
		return goal;
	} 
	private GeoPoint getCenterPoint(List<GeoPoint> points){
		GeoPoint goal = null;
		int lat=0;
		int lon=0;
		for(GeoPoint point:points){
			lat+=point.getLatitudeE6();
			lon+=point.getLongitudeE6();
		}
		lat=lat/points.size();
		lon=lon/points.size();
		goal = new GeoPoint(lat, lon);
		return goal;
	}
	
	//window listener of bubble 
	private ItemizedOverlay.OnFocusChangeListener onFocusChangeListener = new ItemizedOverlay.OnFocusChangeListener() {

		@Override
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
			//创建气泡
			if(popView!=null){
				popView.setVisibility(View.GONE);
			}
			if(newFocus!=null){
				MapView.LayoutParams params = (MapView.LayoutParams)popView.getLayoutParams();
				params.point = newFocus.getPoint();//这用于popView的定位
				((TextView)popView.findViewById(R.id.map_bubbleTitle)).setText(newFocus.getTitle());
				((TextView)popView.findViewById(R.id.map_bubbleText)).setText(newFocus.getSnippet());
				mapView.updateViewLayout(popView, params);
				popView.setVisibility(View.VISIBLE);
			}
		}
		
	};

	@Override
	public void onClick(View v) {
		if(status==STATUS_MAP){
			switch (v.getId()) {
			case R.id.switch_tolist:
				//loadMapList();
				startActivity(new Intent(this, LocationListActivity.class));
				break;
	
			default:
				break;
			}
		}else if (status==STATUS_LIST) {
			switch (v.getId()) {
			case R.id.back:
				status=STATUS_MAP;
				//viewMap.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
				setContentView(viewMap);
				break;

			default:
				break;
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		if(status==STATUS_MAP){
		this.getParent().onBackPressed();
		}else if (status==STATUS_LIST) {
			status=STATUS_MAP;
			//viewMap.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
			setContentView(viewMap);
		}
	}
	
	private List<Drawable> getDrawablesFromPlaces(){
		List<Drawable> drawables = new ArrayList<Drawable>();
		View layout = LayoutInflater.from(this).inflate(R.layout.map_grid_item, null);
		ImageView imageView = (ImageView)layout.findViewById(R.id.grid_item_image);
		TextView textView = (TextView)layout.findViewById(R.id.grid_item_counter);
		
		if(places!=null&&places.size()!=0){
			for(ResponsePlace place:places){
				ImageLoaderUtil imageLoaderUtil = new ImageLoaderUtil();
				imageLoaderUtil.loadPlaceIcon(place, this, imageView, layout);
				textView.setText(String.valueOf(place.getBeats().size()));
				layout.setDrawingCacheEnabled(true);
				layout.buildDrawingCache();
				Bitmap bitmap = layout.getDrawingCache();
				Drawable drawable = new BitmapDrawable(bitmap);
				drawables.add(drawable);
			}
		}
		
		
		return drawables;
	}
	
	//sync handler
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PLACES_LOADING:
				
				break;

			default:
					break;
			}
		};
	};
	
	/**
	 * STATUS_LIST LAYOUT
	 */
	private ListView listView;
	private void loadMapList(){
		status = STATUS_LIST;
		viewList = inflater.inflate(R.layout.map_lsit, null);
		//add animation for view when invoke setcontentview
		viewList.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		setContentView(viewList);
		findViewById(R.id.back).setOnClickListener(this);
		listView = (ListView)findViewById(R.id.list);
		if(places!=null)
			listView.setAdapter(new MapListAdapter(LocationActivity.this, places, listView));
		
	}
		
}