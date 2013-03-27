package com.heart.beat.cn.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.heart.beat.cn.util.ConstantsI;
import com.heart.beat.cn.widget.BeatsAdapter;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.resource.UserResource;
import com.slider.cn.app.bean.response.ResponseBeats;
import com.slider.cn.app.bean.response.ResponseProfile;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.CookieImpl;
/**
 * personal profile and login  interface:
 * it will show personal profile layout when user logined ,otherwise login layout
 * @author ken
 *
 */
public class ProfileActivity extends Activity implements OnClickListener, OnCheckedChangeListener, OnItemClickListener {
	private static final String  TAG = "PersonalActivity  ";
	
	private static final int MESSAGE_LOGIN = 0;
	private static final int MESSAGE_REGISTER = 1;
	private static final int MESSAGE_DIALOG = 2;
	
	/* signal activity intent */
	private static final int REGISTER = 1;
	
	private String userUri = "";
	private String userId = "-1";
	public static final String USER_URI = "uri";
	
	
	private boolean isUpdateUser = true;
	private ResponseProfile user;
	
	/* login layout */
	private EditText usernameEdit,passwordEdit;
	private ProgressDialog progressDialog = null;
	/* profile layout */
	private View profileView;
	private Resources resourses;
	private RadioButton showFriends,showShares,show_attention;
	private ImageView profileAvatar;
	private TextView setting;
	
	private ListView beatList;
	private ResponseBeats profileBeats;
	
	private TextView nickerText,cityText,emailText,follow;
	private AsyncImageLoader imageLoader;
	private CookieImpl cookieManager;
	
	private int status = 0;/* 0 not login-layout ; 1 profile-layout ; 2 profile-setting */
	private final int STATUS_LOGIN = 0;
	private final int STATUS_PROFILE = 1;
	private final int STATUS_SETTING = 2;
	private LayoutInflater inflater;
	private View viewLogin,ViewProfile,viewSetting;
	//private boolean isLogineded = false; // whether the user logged in
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate isLogineded "+status);
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(this.getResources().getString(R.string.network_wait));
		progressDialog.setCancelable(true);
		resourses = getResources();
		
		if(cookieManager==null)
			cookieManager = new CookieImpl();
		userUri = cookieManager.isOutdate(ProfileActivity.this);
		if(userUri!=null){
			status = STATUS_PROFILE;
			userUri = ConstantsI.SERVER_HOST+userUri;
		}
			
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume isLogineded "+status);
		if(status == STATUS_PROFILE)
			loadPersonalLayout();
		else if(status == STATUS_LOGIN)
			loadLoginLayout();
		else if(status == STATUS_SETTING)
			loadSettingLayout();
	}
	
	private void loadPersonalLayout(){
		ViewProfile = inflater.inflate(R.layout.profile_layout, null);
		
		setContentView(ViewProfile);
		setting = (TextView)findViewById(R.id.setting);
		profileView = findViewById(R.id.profile_layout);
		profileAvatar = (ImageView)profileView.findViewById(R.id.profile_photo);
		
		
		showFriends = (RadioButton)findViewById(R.id.show_friends);
		showShares = (RadioButton)findViewById(R.id.show_shares);
		show_attention = (RadioButton)findViewById(R.id.show_attention);
		((RadioGroup)findViewById(R.id.tab_show)).setOnCheckedChangeListener(this);
		
		nickerText =(TextView)findViewById(R.id.nicker);
		cityText =(TextView)findViewById(R.id.city);
		emailText =(TextView)findViewById(R.id.email);
		follow = (TextView)findViewById(R.id.follow);
		beatList = (ListView)findViewById(R.id.profile_beat_list);
		
		setting.setOnClickListener(this);
		showFriends.setOnClickListener(this);
		showShares.setOnClickListener(this);
		follow.setOnClickListener(this);
		//showFriends.setPressed(true);//default 
		
		
		UserResource userApi = new UserResource();
		if(user==null){
			try {
				user = userApi.profile(userUri, ProfileActivity.this);
			} catch (NetException e) {
				Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
				return;
			}
		}
		
		/* update application variable */
		if(((MyApplication)getApplicationContext()).getProfile()==null)
			((MyApplication)getApplicationContext()).setProfile(user);
		
		nickerText.setText(resourses.getString(R.string.l_nicker)+" "+user.getUser().getUsername());
		cityText.setText(resourses.getString(R.string.l_city)+" "+user.getCity());
		emailText.setText(resourses.getString(R.string.l_email)+" "+user.getUser().getEmail());
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
				profileAvatar.setBackgroundDrawable(ProfileActivity.this.getResources().getDrawable(R.drawable.photo));
		}
		
		beatList.post(new Runnable() {
			
			@Override
			public void run() {
				userId = user.getUser().getId();
				BeatResource beatApi = new BeatResource(ProfileActivity.this);
				try {
					profileBeats = beatApi.getBeats(ProfileActivity.this, userId);
				} catch (NetException e) {
					Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return;
				}
				if(profileBeats!=null)
					beatList.setAdapter(new BeatsAdapter(BeatsAdapter.STATUS_PROFILE,ProfileActivity.this, beatList, profileBeats.getObjects()));
				
			}
		});
		beatList.setOnItemClickListener(this);
		
	}
	
	private void loadLoginLayout(){
		viewLogin = inflater.inflate(R.layout.login_layout, null);
		setContentView(viewLogin);
		usernameEdit = (EditText)findViewById(R.id.login_username);
		passwordEdit = (EditText)findViewById(R.id.login_password);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.register).setOnClickListener(this);
		findViewById(R.id.forgot_password).setOnClickListener(this);
		findViewById(R.id.login_facebook).setOnClickListener(this);
		findViewById(R.id.login_google).setOnClickListener(this);
		findViewById(R.id.login_twitter).setOnClickListener(this);
	}
	private void loadSettingLayout(){
		viewSetting = inflater.inflate(R.layout.profile_setting, null);
		setContentView(viewSetting);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.setting_sync).setOnClickListener(this);
		findViewById(R.id.setting_profile).setOnClickListener(this);
		findViewById(R.id.setting_logout).setOnClickListener(this);
	
	}
	@Override
	public void onClick(View v) {
		if(status==STATUS_PROFILE){
			switch (v.getId()) {
			case R.id.setting:
				status = STATUS_SETTING;
				loadSettingLayout();
				break;
			/*case R.id.show_friends:
				showFriends.setPressed(true);
				showShares.setPressed(false);
				break;
			case R.id.show_shares:
				showShares.setPressed(true);
				showFriends.setPressed(false);
				break;*/
			case R.id.follow:
				if(follow.getText().equals(this.getResources().getString(R.string.attentioned)))
					follow.setText(this.getResources().getString(R.string.attention));
				else
					follow.setText(this.getResources().getString(R.string.attentioned));
				break;
			default:
				break;
			}
		}else if(status == STATUS_LOGIN){
			Intent i = new Intent(this, LoginAliasActivity.class);
			switch (v.getId()) {
			case R.id.login:
				progressDialog.show();
				boolean resulst = login(usernameEdit.getText().toString().trim(),passwordEdit.getText().toString().trim());
				if(resulst){
					status = STATUS_PROFILE;//resume login success every time
					loadPersonalLayout();
				}
				Message msg = mHandler.obtainMessage();
				msg.what = MESSAGE_DIALOG;
				mHandler.sendMessage(msg);
				break;
			case R.id.register:
				i.putExtra(ConstantsI.LOGIN_TARGET, ConstantsI.REGISTER);
				startActivityForResult(i, REGISTER);
				break;
			case R.id.forgot_password:
				i.putExtra(ConstantsI.LOGIN_TARGET, ConstantsI.FORGOT_P0ASSWORD);
				startActivity(i);
				break;
			case R.id.login_facebook:
				i.putExtra(ConstantsI.LOGIN_TARGET, ConstantsI.SIGN_FACEBOOK);
				startActivity(i);
				break;
			case R.id.login_google:
				i.putExtra(ConstantsI.LOGIN_TARGET, ConstantsI.SIGN_GOOGLE);
				startActivity(i);
				break;
			case R.id.login_twitter:
				i.putExtra(ConstantsI.LOGIN_TARGET, ConstantsI.SIGN_TWITTER);
				startActivity(i);
				break;

			default:
				break;
			}
		}else if(status ==STATUS_SETTING ){
			Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
			switch (v.getId()) {
			case R.id.back:
				status = STATUS_PROFILE;
				setContentView(ViewProfile);
				break;
			case R.id.setting_sync:
				//intent.putExtra(SettingActivity.STATUS, 1);
				break;
			case R.id.setting_profile:
				Bundle bundle = new Bundle();
				bundle.putInt(SettingActivity.USER_ID, Integer.valueOf(user.getId()));
				bundle.putInt(SettingActivity.STATUS, SettingActivity.PROFILE_EDIT);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.setting_logout:
				Builder b = new AlertDialog.Builder(this).setMessage(R.string.q_logout);
				b.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								cookieManager.clearCookie(ProfileActivity.this);
								status = STATUS_LOGIN;
								loadLoginLayout();
						}
					})
				.setNeutralButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int whichButton) {
							}
					}).create();
				b.show();
				
				break;
			default:
				break;
			}
		}
		
	}
	
	private boolean login(String username,String password){
		System.out.println("login .... "+username+password);
		if(username.equals("")){
			showMessageDialog(R.string.dg_title, R.string.dg_fg_username);
			return false;
		}
		if(password.equals("")){
			showMessageDialog(R.string.dg_title, R.string.dg_fg_password);
			return false;
		}
		UserResource userApi = new UserResource();
		try {
			user = userApi.login(ProfileActivity.this,username,password);
			if(user==null){
				showMessageDialog(R.string.dg_title, R.string.sign_failed);
				return false;
			}
		} catch (NetException e) {
			Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		}
		// 保存用户的uri
		if(cookieManager==null)
			cookieManager = new CookieImpl();
		cookieManager.save(ProfileActivity.this, CookieImpl.USER_URI, user.getResource_uri());
		Log.d(TAG, "cookieManager.av profile_id "+user.getId());
		cookieManager.save(ProfileActivity.this, CookieImpl.PROFILE_ID, user.getId());
		Log.d(TAG, "login success and save user uri "+ user.getResource_uri());
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode){
		case REGISTER:
			if(resultCode == RESULT_OK){
				System.out.println("ProfileActivity onActivitiyResult register occur");
				Bundle b=data.getExtras();  //data为B中回传的Intent
				String url=b.getString(USER_URI);//str即为回传的值
				try {
					user = new UserResource().profile(url,ProfileActivity.this);
					status = STATUS_PROFILE;
				} catch (NetException e) {
					Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		if(status==STATUS_PROFILE||status==STATUS_LOGIN){
			this.getParent().onBackPressed();
		}else if(status==STATUS_SETTING){
			status = STATUS_PROFILE;
			setContentView(ViewProfile);
		}
		
	}
	
	//sync handler
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case MESSAGE_LOGIN:
				
				break;
			case MESSAGE_REGISTER:
				
				break;
			case MESSAGE_DIALOG:
				progressDialog.dismiss();
				break;
			default:
				break;
			}
		};
	};
	
	private void showMessageDialog(int title,int message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, null);
        builder.create().show();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
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
