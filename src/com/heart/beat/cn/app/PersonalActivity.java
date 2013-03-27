package com.heart.beat.cn.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.heart.beat.cn.util.ConstantsI;
import com.heart.beat.cn.widget.BeatsAdapter;
import com.slider.cn.app.bean.resource.AccountResource;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.resource.UserResource;
import com.slider.cn.app.bean.response.ResponseBeats;
import com.slider.cn.app.bean.response.ResponseProfile;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.CookieImpl;
/**
 * show the profile information of the user(including anyone)
 * 注意，这里浏览别人的信息资料，这里的setting改为添加与取消'关注'了
 * @author slider
 *
 */
public class PersonalActivity extends Activity implements OnClickListener, OnCheckedChangeListener, OnItemClickListener {
	private final String TAG = "PersonalActivity";
	private static final String USER_URL = "user_id";
	private static final int MSG_LOAD_USER = 1;
	private static final int MSG_ADD_FOLLOW = 2;
	private String userUrl;
	private String userId;
	private ResponseProfile user;
	private ImageView profileAvatar;
	private RadioGroup showtab;
	private RadioButton showFriends,showShares,showAttentions;
	private ListView beatList;
	private ResponseBeats profileBeats;
	private TextView nickerText,cityText,emailText,follow,setting;
	private ProgressDialog progress_dialog; 
	
	private AccountResource accoutApi;
	/* profile info */
	private ResponseProfile profile;
	private CookieImpl cookieManager;
	private AsyncImageLoader imageLoader;
	
	private boolean isAttented = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userUrl = getIntent().getStringExtra(USER_URL);
		
		setContentView(R.layout.profile_layout);
		showtab = (RadioGroup)findViewById(R.id.tab_show);
		showtab.setOnCheckedChangeListener(this);
		showFriends = (RadioButton)findViewById(R.id.show_friends);
		showShares = (RadioButton)findViewById(R.id.show_shares);
		showAttentions = (RadioButton)findViewById(R.id.show_attention);
		beatList = (ListView)findViewById(R.id.profile_beat_list);
		nickerText =(TextView)findViewById(R.id.nicker);
		cityText =(TextView)findViewById(R.id.city);
		emailText =(TextView)findViewById(R.id.email);
		follow = (TextView)findViewById(R.id.follow);
		profileAvatar = (ImageView)findViewById(R.id.profile_photo);
		follow.setOnClickListener(this);
		setting = (TextView)findViewById(R.id.setting);
		setting.setBackgroundResource(R.drawable.btn_gray_unselected);
		setting.setOnClickListener(this);
		
		progress_dialog = new ProgressDialog(this);
		progress_dialog.setMessage(this.getResources().getString(R.string.network_wait));
		
		//profile = ((MyApplication)getApplicationContext()).getProfile();
		cookieManager = new CookieImpl();
		loadProfile();
	}
	
	private void loadProfile(){
		if(accoutApi==null)
			accoutApi = new AccountResource(this);
		String profileId = cookieManager.getValue(this, CookieImpl.PROFILE_ID);

		Log.d(TAG, "profileId " + profileId);
		
		try {
			user = accoutApi.getProfile(userUrl);
		}catch (Exception e1) {
			
			e1.printStackTrace();
			Toast.makeText(PersonalActivity.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
		}
		try {
			profile = accoutApi.getProfile(profileId);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(PersonalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		nickerText.setText(getResources().getString(R.string.l_nicker)+" "+user.getUser().getUsername());
		cityText.setText(getResources().getString(R.string.l_city)+" "+user.getCity());
		emailText.setText(getResources().getString(R.string.l_email)+" "+user.getUser().getEmail());
		if(profileId.equals(user.getId())){
			//浏览自己的信息的时候，就不需要这个功能
			setting.setVisibility(View.INVISIBLE);
		}else {
			if(profile!=null&&profile.getFriends().contains(user.getId())){
				isAttented = true;
				setting.setText(R.string.cancle_attention);
				//follow.setText(R.string.attentioned);
			}else {
				isAttented = false;
				setting.setText(R.string.add_attention);
				//follow.setText(R.string.attention);
			}
		}
		
		//load the profile avatar
		String url = user.getAvatar();
		if(url!=null&&!url.equals("")){
			if(imageLoader==null)
				imageLoader = AsyncImageLoader.getInstance();
			profileAvatar.setTag(url);
			Drawable drawable = imageLoader.loadDrawable(url, new ImageCallback() {
				
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					// TODO Auto-generated method stub
					ImageView imageViewByTag = (ImageView) profileAvatar.findViewWithTag(imageUrl);
					if(imageViewByTag!=null){
						//imageViewByTag.setImageDrawable(imageDrawable);
						imageViewByTag.setBackgroundDrawable(imageDrawable);
					}
				}
			});
			if(drawable!=null)
				profileAvatar.setBackgroundDrawable(drawable);
			else
				profileAvatar.setBackgroundDrawable(PersonalActivity.this.getResources().getDrawable(R.drawable.photo));
		}
		
		beatList.post(new Runnable() {
			
			@Override
			public void run() {
				userId = user.getId();
				BeatResource beatApi = new BeatResource(PersonalActivity.this);
				try {
					profileBeats = beatApi.getBeats(PersonalActivity.this, userId);
				} catch (NetException e) {
					Toast.makeText(PersonalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return;
				}
				beatList.setAdapter(new BeatsAdapter(BeatsAdapter.STATUS_PROFILE,PersonalActivity.this, beatList, profileBeats.getObjects()));
				
			}
		});
		beatList.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.follow:
			String statue = follow.getText().toString();
			if(statue.equals(getResources().getString(R.string.attention))){
				addFollow();
			}else {
				removeFollow();
			}
			
			break;
		case R.id.setting:
			/* 添加关注或者取消关注 */
			if(!isAttented){
				addFollow();
			}else {
				removeFollow();
			}
			break;
		default:
			break;
		}
		
	}
	/**
	 * 添加对象为follow
	 */
	private void addFollow(){
		if(accoutApi==null)
			accoutApi = new AccountResource(this);
		try {
			accoutApi.addFriend(userUrl);
			isAttented = true;
			setting.setText(R.string.cancle_attention);
		} catch (NetException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		//follow.setText(R.string.attentioned);
	}
	private void removeFollow(){
		if(accoutApi==null)
			accoutApi = new AccountResource(this);
		try {
			accoutApi.removeFriend(userUrl);
			isAttented = false;
			setting.setText(R.string.add_attention);
		} catch (NetException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		//follow.setText(R.string.attention);
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_LOAD_USER:
				
				break;
			case MSG_ADD_FOLLOW:
				
				break;
			default:
				break;
			}
			
		}
	};

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.show_shares:
			beatList.setVisibility(View.VISIBLE);
			
			break;
		case R.id.show_friends:
			beatList.setVisibility(View.INVISIBLE);
			break;
		case R.id.show_attention:
			beatList.setVisibility(View.INVISIBLE);
			break;

		default:
			break;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int beatId = Integer.valueOf(profileBeats.getObjects().get(arg2).getId());
		Intent intent = new Intent(this, BeatActivity.class);
		intent.putExtra(BeatActivity.BEAT_ID, beatId);
		startActivity(intent);
		
	}
}
