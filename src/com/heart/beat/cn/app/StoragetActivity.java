package com.heart.beat.cn.app;

import android.app.Activity;
import android.os.Bundle;

public class StoragetActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_layout);
	}
	
	@Override
	public void onBackPressed() {
		this.getParent().onBackPressed();
	}
}
