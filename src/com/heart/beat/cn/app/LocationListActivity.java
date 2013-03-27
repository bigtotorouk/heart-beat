package com.heart.beat.cn.app;

import java.util.List;
import com.heart.beat.cn.util.MyLocation;
import com.heart.beat.cn.util.MyLocation.LocationResult;
import com.heart.beat.cn.widget.MapListAdapter;
import com.heart.beat.cn.widget.MapListPreviewAdapter;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.response.ResponseBeat;
import com.slider.cn.app.bean.response.ResponseBeats;
import com.slider.cn.app.bean.response.ResponsePlace;
import com.slider.cn.app.bean.response.ResponsePlaces;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.google.place.resource.PlaceResource;
import com.slider.cn.app.google.place.resource.PlaceService;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
/**
 * 这个类主要是用于以list(或者grid)的形式展示places及某个place的preview(预览)
 * @author slider
 *
 */
public class LocationListActivity extends Activity implements OnClickListener, OnItemClickListener {
	private static final String TAG = "LocationListActivity";
	
	private final int STATUS_MAP_LIST = 0;
	private final int STATUS_LIST_PREVIEW = 1;
	private int status = STATUS_MAP_LIST;
	
	
	private ResponsePlaces  responsePlaces;
	private List<ResponsePlace> places;
	private ResponseBeats priviewBeats;
	private Location currentLoaction; 
	private PlaceResource placeApi;
	private BeatResource beatApi;
	private GridView gridView;
	private ProgressBar bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map_lsit);
		findViewById(R.id.back).setOnClickListener(this);
		gridView = (GridView)findViewById(R.id.gridView1);
		bar = (ProgressBar)findViewById(R.id.bar1);
		
		loadPlacesLayout();
		
	}
	private void loadPlacesLayout(){
		status = STATUS_MAP_LIST;
		if(places!=null){ // back from status-preview layout
			gridView.setAdapter(new MapListAdapter(this, places, gridView));
			return;
		}
		bar.setVisibility(View.VISIBLE);
		updateLocation();
		 /* load places */
        placeApi = new PlaceResource(this);
        try {
			responsePlaces = placeApi.getPlaces(currentLoaction.getLatitude(), currentLoaction.getLongitude(),PlaceService.RADIOUS_DEFAULT);
		} catch (NetException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
        /* get places from server failed */
        if(responsePlaces==null){
			responsePlaces = new ResponsePlaces();
		}
        places = responsePlaces.getObjects();
        Log.d(TAG, "places "+places);
        if(places!=null){
			gridView.setAdapter(new MapListAdapter(this, places, gridView));
			gridView.setOnItemClickListener(this);
        }
		bar.setVisibility(View.GONE);
	}
	private void loadBeatsLayout(List<String> beatIds){
		status = STATUS_LIST_PREVIEW;
		bar.setVisibility(View.VISIBLE);
		if(beatApi==null)
			beatApi = new BeatResource(this);
		try {
			priviewBeats = beatApi.getBeats(beatIds, true);
			if(priviewBeats==null)
				return;
			List<ResponseBeat> list = priviewBeats.getObjects();
			gridView.setAdapter(new MapListPreviewAdapter(this, list, gridView));
			gridView.setOnItemClickListener(this);
		} catch (NetException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		bar.setVisibility(View.GONE);
	}

	private void updateLocation(){
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location location){
		        currentLoaction = location;
		    }
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if(status==STATUS_MAP_LIST){
				finish();
			}else if (status == STATUS_LIST_PREVIEW) {
				loadPlacesLayout();
			}
			break;
		default:
			break;
		}
		
	}
	@Override
	public void onBackPressed() {
		if(status==STATUS_MAP_LIST){
			finish();
		}else if (status == STATUS_LIST_PREVIEW) {
			loadPlacesLayout();
		}
	}

	/**
	 * listener for grid when clicked
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(status == STATUS_MAP_LIST){
			List<String> beatIds = places.get(position).getBeats();
			if (beatIds!=null&&beatIds.size()!=0) {
				loadBeatsLayout(beatIds);
				status = STATUS_LIST_PREVIEW;
			}
		}else if (status == STATUS_LIST_PREVIEW) {
			int beatId = Integer.valueOf(priviewBeats.getObjects().get(position).getId());
			Intent  intent = new Intent(this, BeatActivity.class);
			intent.putExtra(BeatActivity.BEAT_ID, beatId);
			startActivity(intent);
			
 		}
		
	}
	
	
	
}
