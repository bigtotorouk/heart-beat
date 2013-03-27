package com.heart.beat.cn.app;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
/**
 * application main INTERFACE
 * @author ken
 *
 */
public class HeartBeatActivity extends TabActivity implements OnCheckedChangeListener {
	private static final String TAG	= "HeartBeatActivity";
	
	private RadioGroup mainTab;
	private TabHost mTabHost;
	
	//内容Intent
	private Intent mHomeIntent;
	private Intent mNewsIntent;
	private Intent mInfoIntent;
	private Intent mSearchIntent;
	private Intent mMoreIntent;
	
	private boolean isLogin = false;
	
	private final static String TAB_TAG_INFO="tab_tag_info";
	private final static String TAB_TAG_STORAGE="tab_tag_storage";
	private final static String TAB_TAG_SHARE="tab_tag_share";
	private final static String TAB_TAG_MAP="tab_tag_map";
	private final static String TAB_TAG_PERSONAL="tab_tag_personal";
	//share image
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heart_beat);  
        mainTab=(RadioGroup)findViewById(R.id.main_tab);
        Log.d(TAG, "onCreate...");
        /* default show the first layout,so make it button selected */
        RadioButton button0 = (RadioButton)findViewById(R.id.radio_button0);
        button0.setChecked(true);
        
        mainTab.setOnCheckedChangeListener(this);
        prepareIntent();
        setupIntent();
    }
    /**
     * 准备tab的内容Intent
     */
	private void prepareIntent() {
		mHomeIntent=new Intent(this, InfoActivity.class);
		mNewsIntent=new Intent(this, StoragetActivity.class);
		mInfoIntent=new Intent(this, ShareActivity.class);
		mSearchIntent=new Intent(this,LocationActivity.class);
		mMoreIntent=new Intent(this, ProfileActivity.class);
	}
	/**
	 * 
	 */
	private void setupIntent() {
		this.mTabHost=getTabHost();
		TabHost localTabHost=this.mTabHost;
		localTabHost.addTab(buildTabSpec(TAB_TAG_INFO, R.string.main_home, R.drawable.info_unselected, mHomeIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_STORAGE, R.string.main_news, R.drawable.storage_unselected, mNewsIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_SHARE, R.string.main_my_info, R.drawable.share_unselected, mInfoIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_MAP, R.string.menu_search, R.drawable.map_unselected, mSearchIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_PERSONAL, R.string.more, R.drawable.profile_unselected, mMoreIntent));
	}
	/**
	 * 构建TabHost的Tab页
	 * @param tag 标记
	 * @param resLabel 标签
	 * @param resIcon 图标
	 * @param content 该tab展示的内容
	 * @return 一个tab
	 */
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	} 
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(mTabHost==null){
			//首次进入软件时候。出现
			return;
		}
			
		switch(checkedId){
		case R.id.radio_button0:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_INFO);
			break;
		case R.id.radio_button1:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_STORAGE);
			break;
		case R.id.radio_button2:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_SHARE);
			break;
		case R.id.radio_button3:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_MAP);
			break;
		case R.id.radio_button4:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_PERSONAL);
			break;
		}
	}
    
	@Override
	public void onBackPressed() {
		Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(R.string.l_exist);
		dialog.setPositiveButton("Yes", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				HeartBeatActivity.super.onBackPressed();
			}
		});
		dialog.setNegativeButton("No", null);
		dialog.show();
	}
	
	/**
	 * no usefull
	 */
	private void captureImageDialog(){
		final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);	
		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
										   "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
					try {
						intent.putExtra("return-data", true);
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent = new Intent();
		            intent.setType("image/*");
		            intent.setAction(Intent.ACTION_GET_CONTENT);    
		            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		});
			
		final AlertDialog dialog = builder.create();
		dialog.show();
	}
}