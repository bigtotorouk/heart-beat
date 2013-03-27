package com.heart.beat.cn.app;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.heart.beat.cn.util.ConstantsI;
import com.slider.cn.app.bean.resource.UserResource;
import com.slider.cn.app.bean.response.ResponseProfile;
import com.slider.cn.app.exception.NetException;
/**
 * 因为登陆界面的放在profileactivity里了。所以这个界面主要处理注册、找回密码、用第三方网站授权等。
 * @author slider
 *
 */
public class LoginAliasActivity extends Activity implements OnClickListener {
	private static final String TAG = "LoginAliasActivity";
	
	private EditText usernameEdit,password1Edit,password2Edit;
	private ProgressDialog progressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		layoutPosition();
		setContentView(R.layout.login_alias);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(this.getResources().getString(R.string.network_wait));
		preLayout();
		findViewById(R.id.login_back).setOnClickListener(this);
	}

	private void preLayout(){
		TextView title = (TextView)findViewById(R.id.login_title);
		int target = this.getIntent().getIntExtra(ConstantsI.LOGIN_TARGET, -1);
		switch (target) {
		case ConstantsI.NOTHING:
			//error
			Log.d(TAG, "NOTHING , SOMETHING ERRORS");
			break;
		case ConstantsI.REGISTER:
			title.setText(R.string.l_register);
			findViewById(R.id.register_layout).setVisibility(View.VISIBLE);
			usernameEdit = (EditText)findViewById(R.id.register_username);
			password1Edit = (EditText)findViewById(R.id.register_password);
			password2Edit = (EditText)findViewById(R.id.register_confirm_password);
			findViewById(R.id.register_done).setOnClickListener(this);
			break;
		case ConstantsI.FORGOT_P0ASSWORD:
			title.setText(R.string.fd_password);
			findViewById(R.id.forgot_password_layout).setVisibility(View.VISIBLE);
			break;
		case ConstantsI.SIGN_FACEBOOK:
			title.setText(R.string.sign_facebook);
			findViewById(R.id.sign_other_layout).setVisibility(View.VISIBLE);
			break;
		case ConstantsI.SIGN_GOOGLE:
			title.setText(R.string.sign_google);
			findViewById(R.id.sign_other_layout).setVisibility(View.VISIBLE);
			break;
		case ConstantsI.SIGN_TWITTER:
			title.setText(R.string.sign_twitter);
			findViewById(R.id.sign_other_layout).setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	/**
	 * set the attri of dialog
	 */
	private void layoutPosition(){
        LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        //p.height =h;// 设置高度
        //p.width = w;//设置宽度
        p.alpha = 1.0f; // 设置透明度
        p.x=0;//设置距X距离
        p.y=-80;//设置距Y距离 ,这个是相对于中心
        getWindow().setAttributes(p); // 设置生效
	}
	
	private void register(){
		
		progressDialog.show();
		UserResource userApi = new UserResource();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(ConstantsI.USERNAME,usernameEdit.getText().toString());
		params.put(ConstantsI.PASSWORD1, password1Edit.getText().toString());
		params.put(ConstantsI.PASSWORD2, password2Edit.getText().toString());
		//params.put("email", "xiaowangzaixian@gmail.com");
		//params.put("avatar", "http://www.baidu.com/image.jpg");
		//params.put("country", "250");
		//params.put("state", "1986255");
		//params.put("city", "Shanghai");
		//params.put("address", "Shanghai Changning hongqiao road");
		//params.put("zipcode", "441000");
		ResponseProfile user = null;
		try {
			user = userApi.register(LoginAliasActivity.this,params);
		} catch (NetException e) {
			Toast.makeText(LoginAliasActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		progressDialog.dismiss();
		
		if(user!=null){
			//登陆成功，返回profile界面
			Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
			String url = ConstantsI.SERVER_HOST+user.getResource_uri()+ConstantsI.QUERY_FORMAT;
			Intent i = new Intent();  
	        Bundle b = new Bundle();  
	        b.putString(ProfileActivity.USER_URI, url);  
	        i.putExtras(b);  
	        this.setResult(RESULT_OK, i);  
	        this.finish();  
		}else{
			showMessageDialog(R.string.dg_title, R.string.register_failed);
		}
	}
	private void showMessageDialog(int title,int message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, null);
        builder.create().show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_back:
			this.finish();
			break;
		case R.id.register_done:
			register();
		default:
			break;
		}
	}
}
