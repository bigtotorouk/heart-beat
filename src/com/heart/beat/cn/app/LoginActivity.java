package com.heart.beat.cn.app;

import com.heart.beat.cn.util.ConstantsI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class LoginActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.register).setOnClickListener(this);
		findViewById(R.id.forgot_password).setOnClickListener(this);
		findViewById(R.id.login_facebook).setOnClickListener(this);
		findViewById(R.id.login_google).setOnClickListener(this);
		findViewById(R.id.login_twitter).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent i = new Intent(this, LoginAliasActivity.class);
		switch (v.getId()) {
		case R.id.login:
			
			Intent intent = new Intent(LoginActivity.this,HeartBeatActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.register:
			i.putExtra(ConstantsI.LOGIN_TARGET, ConstantsI.REGISTER);
			startActivity(i);
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
		
	}
}
