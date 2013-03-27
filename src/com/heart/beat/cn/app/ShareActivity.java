package com.heart.beat.cn.app;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.heart.beat.cn.util.ConstantsI;
import com.heart.beat.cn.util.LocationService;
import com.heart.beat.cn.util.MyLocation;
import com.heart.beat.cn.util.MyLocation.LocationResult;
import com.heart.beat.cn.widget.CropOption;
import com.heart.beat.cn.widget.CropOptionAdapter;
import com.heart.beat.cn.widget.ImageGalleryAdapter;
import com.heart.beat.cn.widget.PlaceAdapter;
import com.slider.cn.app.bean.request.RequestBeat;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.google.place.BeatPlace;
import com.slider.cn.app.google.place.Place;
import com.slider.cn.app.google.place.PlaceMeta;
import com.slider.cn.app.google.place.resource.PlaceResource;
import com.slider.cn.app.google.place.resource.PlaceService;
import com.slider.cn.app.httpurlconnectionnet.FileEntity;

public class ShareActivity extends Activity implements OnClickListener {
	private static final String TAG = "ShareActivity ...";
	
	private final int STATUS_SHARE = 0;//info界面(beat列表界面)
	private final int STATUS_MAP = 1;//beat 详细信息界面
	private int status = STATUS_SHARE;// 默认是info界面
	private View viewShare,viewMap;
	
	private View capture;
	private View back;
	private View complete;
	private Gallery galleryFlow;
	private EditText product_title;
	private EditText product_city;
	private EditText product_price;
	private EditText product_decription;
	private EditText product_location;
	private View layout_price;
	private TextView add_decription;
	private TextView add_price;
	private TextView lPlaceText;
	private ImageView placeImage;
	
	private ImageGalleryAdapter adapter;

	private List<String> paths; /* the filepath of images */
	private static final String FILEDIR = Environment.getExternalStorageDirectory()+"/heartbeat/temp/";
	private static final String FILEDIR_CAMERA = Environment.getExternalStorageDirectory()+"/heartbeat/temp/camera/";
	
	/* share image */
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	
	/* handler what */
	private static final String MESSAGE_DATA = "message_data";
	private static final int MESSAGE_LOCATION = 0;
	private static final int MSG_LOCATION_ERROR = 1;
	private static final int MSG_SERVER_ERROR = 2;
	/* google map var */
	private LocationManager location;
    private String provider;
    private Location loc;
    private String city;
    private String address;
    private String countrycode;
    /* google palce */
    private Place places;
    private String[] placelabels = new String[0];
    private int selectedIndex = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadShareView();
	}
	private void loadShareView(){
		status = STATUS_SHARE;
		if(viewShare!=null){
			setContentView(viewShare);
			if(selectedIndex!=-1&&placeMetas!=null){ //当从map-select界面返回时候，显示选择place的name
				lPlaceText.setText(placeMetas.get(selectedIndex).getName());
				placeImage.setImageDrawable(getResources().getDrawable(R.drawable.share_location));
			}
			return;
		}
		viewShare = LayoutInflater.from(this).inflate(R.layout.share_detail, null);
		setContentView(viewShare);
		back = (View)viewShare.findViewById(R.id.detail_back);
		complete = (View)viewShare.findViewById(R.id.detail_complete);
		capture = (View)viewShare.findViewById(R.id.detail_capture);
		galleryFlow = (Gallery)viewShare.findViewById(R.id.gallery);
		product_title = (EditText)viewShare.findViewById(R.id.product_title);
		product_city = (EditText)viewShare.findViewById(R.id.city);
		product_price = (EditText)viewShare.findViewById(R.id.product_price);
		product_decription = (EditText)viewShare.findViewById(R.id.product_decription);
		product_location = (EditText)viewShare.findViewById(R.id.product_location);
		layout_price = viewShare.findViewById(R.id.layout_price);
		add_decription = (TextView)viewShare.findViewById(R.id.add_decription);
		placeImage = (ImageView)viewShare.findViewById(R.id.add_location);
		lPlaceText = (TextView)viewShare.findViewById(R.id.place_lable);
		add_price = (TextView)viewShare.findViewById(R.id.add_price);
		viewShare.findViewById(R.id.add_place).setOnClickListener(this);
		product_title.setOnClickListener(this);
		product_price.setOnClickListener(this);
		product_decription.setOnClickListener(this);
		product_location.setOnClickListener(this);
		add_decription.setOnClickListener(this);
		add_price.setOnClickListener(this);
		back.setOnClickListener(this);
		complete.setOnClickListener(this);
		capture.setOnClickListener(this);
		
		
		paths = new ArrayList<String>();
		adapter = new ImageGalleryAdapter(this,paths,200,200);
		galleryFlow.setFadingEdgeLength(0);     
        //galleryFlow.setSpacing(-100); //图片之间的间距     moni
        galleryFlow.setAdapter(adapter);     
             
        galleryFlow.setOnItemClickListener(new OnItemClickListener() {     
            public void onItemClick(AdapterView<?> parent, View view,     
                    int position, long id) {     
                Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();    
                showItemDialog(position);
            }     
                 
        });     
        galleryFlow.setSelection(paths.size()/2);  
		
        
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    LocationListener ll = new mylocationlistener();
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	    if(loc==null)
			updateLocation2();
	    Log.d(TAG, "loc "+loc);
	    //Toast.makeText(this, loc+"", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LocationService locationService = new LocationService(ShareActivity.this);
				
				locationService.reverseGeocode(loc);
				city = locationService.getCity();
				address = locationService.getAddress();
				countrycode = locationService.getCountryCode();
				//loc = locationService.getLocation();
				if(loc==null){
					Message message = mHandler.obtainMessage();
					message.what = MSG_LOCATION_ERROR;
					mHandler.sendMessage(message);
					return;
				}
					
				Message msg = mHandler.obtainMessage();
				msg.what = MESSAGE_LOCATION;
				mHandler.sendMessage(msg);
	
				System.out.println("city: "+city+ " address: "+address);
			}
		}).start();
        
	}
	
	private void showItemDialog(final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.l_note);
        builder.setMessage(R.string.note_image);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	adapter.remove(position);
            	galleryFlow.setSelection(paths.size()/2);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
       
        builder.create().show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.product_title:
			
			break;
		case R.id.product_price:
			
			break;
		case R.id.product_decription:
	
			break;
		case R.id.product_location:
	
			break;
		case R.id.add_decription:
			if(product_decription.getVisibility()==View.GONE){
				product_decription.setVisibility(View.VISIBLE);
			}else{
				product_decription.setVisibility(View.GONE);
			}
			
			break;
		case R.id.add_price:
			if(layout_price.getVisibility()==View.GONE){
				layout_price.setVisibility(View.VISIBLE);
				
			}else{
				layout_price.setVisibility(View.GONE);
			}
			break;
		case R.id.add_place:
			loadMapSelected();
			break;
		case R.id.detail_back:
			this.finish();
			
			break;
		case R.id.back:
			loadShareView();
			break;
		case R.id.detail_complete:
			int beatId=-1;
			if(paths.size()==0){
				Toast.makeText(this, R.string.beat_image_null, Toast.LENGTH_SHORT).show();
				return;
			}
			if(selectedIndex==-1){
				Toast.makeText(this, R.string.place_unselected, Toast.LENGTH_SHORT).show();
			}

			PlaceMeta placeMeta = places.getResults().get(selectedIndex);
			BeatPlace beatPlace = new BeatPlace();
			beatPlace.setPlaces_id(placeMeta.getId());
			beatPlace.setPlaces_ref(placeMeta.getReference());
			beatPlace.setCity(product_city.getText().toString());
			beatPlace.setCountry(countrycode);
			beatPlace.setLat(loc.getLatitude());
			beatPlace.setLng(loc.getLongitude());
			beatPlace.setCoupons("there has some coupons -- 64ert  test");
			beatPlace.setLocation(placeMeta.getVicinity());
			beatPlace.setName(placeMeta.getName());

			PlaceResource placeApi = new PlaceResource(ShareActivity.this);
			String placeid;
			try {
				placeid = placeApi.postPlace(beatPlace);
				RequestBeat beat = new RequestBeat();
				String price = product_price.getText().toString();
				if (price != null && !price.equals(""))
					beat.setPrice(Double.valueOf(price));
				beat.setDescription(product_decription.getText().toString());
				beat.setTitle(product_title.getText().toString());
				// beat.setPrice(12);
				// beat.setCurrency("USD");
				// beat.setDescription("this are shoes");
				if (placeid == null) {
					Toast.makeText(this, "post beat failed", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				placeid = placeid.substring(placeid
						.indexOf(ConstantsI.SERVER_HOST)
						+ ConstantsI.SERVER_HOST.length());// delete host of
															// beatid;
				beat.setPlace(placeid);
				BeatResource beatApi = new BeatResource(ShareActivity.this);
				String beatUrl = beatApi.postBeat(beat);
				String[] strings = beatUrl.split("/");
				beatId = Integer.valueOf(strings[strings.length-2]);
				// upload image of beat
				System.out.println("uplaod image of beat " + paths);
				System.out.println("uplaod image of beatUrl " + beatUrl);
				List<FileEntity> files = new ArrayList<FileEntity>();
				for (String e : paths) {
					FileEntity file = new FileEntity("image", new File(e));
					files.add(file);
				}
				boolean result = beatApi.postImage(beatId, files);
				if (result) {
					Toast.makeText(this, R.string.post_beat_success,
							Toast.LENGTH_SHORT).show();
					clearUIData();
				}
				System.out.println("upload image of beat : resulst " + result);
			} catch (NetException e1) {
				Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			if(beatId==-1)	return;
			Intent intent = new Intent(this, BeatActivity.class);
			intent.putExtra(BeatActivity.BEAT_ID, beatId);
			startActivity(intent);
			break;
		case R.id.detail_capture:
			showCaptureDialog();
			break;
		default:
			break;
		}
		
	}
	/**
	 * add by slider at 2012-8-24
	 */
	private void updateLocation2(){
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location location){
		        loc = location;
		    }
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}
	/* 获取经纬度信息 */
	private void updateLocation(){
		location = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria crit = new Criteria();
        provider = location.getBestProvider(crit, false);
        loc = location.getLastKnownLocation(provider);
        if(loc != null){
            Log.d(TAG, "Success Get GPS.");
            product_location.setText("lat: "+loc.getLatitude()+" ,lon: "+loc.getLongitude());
        }else{
            Log.v(TAG, "Failed Get GPS.");
            product_location.setText("failed get gps information");
        }  
	}
	/**
	 * 清空发布beat的数据，为发布下一个beat准备
	 */
	private void clearUIData(){
		product_title.setText("");
		product_price.setText("");
		//product_price.setVisibility(View.GONE);
		layout_price.setVisibility(View.GONE);
		product_decription.setText("");
		product_decription.setVisibility(View.GONE);
		
	}
	
	/**
	 * show the capture dialog when invoking the capture button
	 */
	private void showCaptureDialog() {
		final String[] items = getResources()
				.getStringArray(R.array.pick_image);// new String []
													// {"Take from camera",
													// "Select from gallery","Cancle"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					File dir = new File(FILEDIR_CAMERA);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					mImageCaptureUri = Uri.fromFile(new File(
							FILEDIR_CAMERA,
							"tmp_avatar_"
									+ String.valueOf(System.currentTimeMillis())
									+ ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else if (item == 1) { // pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				} else { // it's meaning of cancel
					return;
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) return;
		   
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	doCrop();
		    	
		    	break;
		    	
		    case PICK_FROM_FILE: 
		    	mImageCaptureUri = data.getData();
		    	
		    	doCrop();
	    
		    	break;	    	
	    
		    case CROP_FROM_CAMERA:	
		    	//data.setClass(this, ShareActivity.class);
		    	//startActivity(data);
		    	
		        Bundle extras = data.getExtras();
		        if (extras != null) {	        	
		            Bitmap photo = extras.getParcelable("data");
		            String path = saveImageFile(photo);
		            adapter.add(path);
		        }
	
		        /*File f = new File(mImageCaptureUri.getPath());            
		        
		        if (f.exists()) f.delete();*/
	
		        break;

	    }
	}
	/**
	 * simple crop image
	 */
	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        
        if (size == 0) {	        
        	Toast.makeText(this, R.string.crop_app_null, Toast.LENGTH_SHORT).show();
        	
            return;
        } else {
        	intent.setData(mImageCaptureUri);
            
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (mImageCaptureUri != null ) {
		                    getContentResolver().delete(mImageCaptureUri, null, null );
		                    mImageCaptureUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}
	
	@Override
	public void onBackPressed() {
		if(status==STATUS_MAP){
			loadShareView();
		}else if(status==STATUS_SHARE){
			this.getParent().onBackPressed();
		}
	}
	//sync handler
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_LOCATION:
				if(city.equals("not found"))
					product_city.setHint(city);
				else
					product_city.setText(city);
				product_location.setText(address);
				break;
			case MSG_LOCATION_ERROR:
				Toast.makeText(ShareActivity.this, R.string.location_error, Toast.LENGTH_LONG).show();
				break;
			case MSG_SERVER_ERROR:
				Toast.makeText(ShareActivity.this, R.string.server_failed, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};
	/**
	 * save bitmap in 'Environment.getExternalStorageDirectory()+"/heartbeat/temp/"'
	 * @param bitmap
	 * @return full filepath of bitmap
	 */
	private String saveImageFile(Bitmap bitmap){
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, R.string.sdcard_null, Toast.LENGTH_SHORT).show();
			return null;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String filename = FILEDIR+format.format(date)+".jpg";
		File dir = new File(FILEDIR);
		if(!dir.exists()){
			dir.mkdirs();
		}
		File file = new File(filename);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
		
		return filename;
	}
	
	private class mylocationlistener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				loc = location;
				System.out.println("onLocationChanged "+location.getLatitude() + "" + location.getLongitude());
			}else
				updateLocation();
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	/**
	 * some function for map-select-layout
	 */
	
	private ListView placeListView;
	private List<PlaceMeta> placeMetas;
	private PlaceAdapter placeAdapter;
	
	private void loadMapSelected(){
		status = STATUS_MAP;
		if(viewMap!=null){
			setContentView(viewMap);
			return;
		}
		viewMap = LayoutInflater.from(this).inflate(R.layout.map_select, null);
		setContentView(viewMap);
		placeListView = (ListView)viewMap.findViewById(R.id.place_list);
		viewMap.findViewById(R.id.back).setOnClickListener(this);
		View placeHeader =  LayoutInflater.from(this).inflate(R.layout.map_select_header, null);
		placeListView.addHeaderView(placeHeader);
		loadPlaces();
		if(placeMetas==null){
			placeMetas = new ArrayList<PlaceMeta>();
		}
		placeAdapter = new PlaceAdapter(this, placeMetas);
		placeListView.setAdapter(placeAdapter);
		placeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(ShareActivity.this, arg2+"", Toast.LENGTH_SHORT).show();
				selectedIndex = arg2-1;
				loadShareView();
			}
		});
		
	}
	/* 网络加载 places */
	private void loadPlaces(){
		if(loc==null){
			Toast.makeText(this, R.string.load_location_error, Toast.LENGTH_SHORT).show();
			return;
		}
		PlaceService placeService = new PlaceService();
		try {
			places = placeService.reversePlaceSearch(loc.getLatitude(), loc.getLongitude());
		} catch (NetException e) {
			Toast.makeText(this, R.string.load_place_error, Toast.LENGTH_SHORT).show();
		}
		
		if(places!=null){
			placeMetas = places.getResults();
			if(placeMetas==null){
				placeMetas = new ArrayList<PlaceMeta>();
			}
		}
	}
}
