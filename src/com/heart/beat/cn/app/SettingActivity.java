package com.heart.beat.cn.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.heart.beat.cn.widget.CropOption;
import com.heart.beat.cn.widget.CropOptionAdapter;
import com.slider.cn.app.bean.request.RequestProfile;
import com.slider.cn.app.bean.resource.AccountResource;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.FileEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * it's for handling the process for profile-setting; not include profile-setting layout
 * @author slider
 *
 */
public class SettingActivity extends Activity implements OnClickListener {
	private int status = 0;
	private int userId = 0;
	public final static String STATUS = "status";
	public final static String USER_ID = "user_id";
	public final static int PROFILE_EDIT = 0; 
	
	private AccountResource accountApi;
	/* prifle mofify option */
	private EditText displayName,country,state,city,address,zipcode;
	
	/* avatar modify option */
	private ImageView avatar;
	private String avatarPath;
	
	private static final String FILEDIR = Environment.getExternalStorageDirectory()+"/heartbeat/temp/";
	private static final String FILEDIR_CAMERA = Environment.getExternalStorageDirectory()+"/heartbeat/temp/camera/";
	
	/* share image */
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		status = bundle.getInt(STATUS);
		userId = bundle.getInt(USER_ID);
		if(status==PROFILE_EDIT)
			loadProfileEdit();
		
		
	}	
	private void loadProfileEdit(){
		setContentView(R.layout.profile_edit);
		avatar = (ImageView)findViewById(R.id.avatar_edit);
		avatar.setOnClickListener(this);
		findViewById(R.id.avatar_post).setOnClickListener(this);
		
		displayName = (EditText)findViewById(R.id.display_name);
		country = (EditText)findViewById(R.id.country);
		state = (EditText)findViewById(R.id.state);
		city = (EditText)findViewById(R.id.city);
		address = (EditText)findViewById(R.id.address);
		zipcode = (EditText)findViewById(R.id.zipcode);
		findViewById(R.id.profile_post).setOnClickListener(this);
	}
	
	/**
	 * show the capture dialog when invoking the capture button
	 */
	private void showCaptureDialog() {
		final String[] items = getResources()
				.getStringArray(R.array.avatar_edit);// new String []
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
					return;
				} else if (item == 1) { // pick from file
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
				} else if (item ==2){ // it's meaning of cancel
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				}else{
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
		            avatarPath = saveImageFile(photo);
		            avatar.setImageBitmap(photo);
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
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.avatar_edit:
			showCaptureDialog();
			break;
		case R.id.avatar_post:
			if(avatarPath==null){
				Toast.makeText(this, R.string.avatar_null, Toast.LENGTH_SHORT).show();
				return;
			}
			FileEntity file = new FileEntity("avatar", new File(avatarPath));
			if(accountApi==null)
				accountApi = new AccountResource(SettingActivity.this);
			boolean result;
			try {
				result = accountApi.postAvatar(null, file);
				if(result)
					Toast.makeText(this, "post avatar success", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "post avatar failed", Toast.LENGTH_SHORT).show();
			} catch (NetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			break;
			
		case R.id.profile_post:
			RequestProfile profile  = new RequestProfile();
			profile.setAddress(address.getText().toString());
			profile.setCity(city.getText().toString());
			profile.setCountry(country.getText().toString());
			profile.setDisplay_name(displayName.getText().toString());
			profile.setState(state.getText().toString());
			profile.setZipcode(zipcode.getText().toString());
			if(accountApi==null)
				accountApi = new AccountResource(SettingActivity.this);
			try {
				accountApi.patchProfile(userId, profile);
				Toast.makeText(this, "Post new profile inforation success", Toast.LENGTH_SHORT).show();
			} catch (NetException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			
			break;

		default:
			break;
		}
		
	}
}
