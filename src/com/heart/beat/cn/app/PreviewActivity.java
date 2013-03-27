package com.heart.beat.cn.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class PreviewActivity extends Activity {
	public static final String BEAT_IDS = "beat_ids";
	private List<String> beatIds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_layout);
		beatIds = getIntent().getStringArrayListExtra(BEAT_IDS);
		
	}
}
