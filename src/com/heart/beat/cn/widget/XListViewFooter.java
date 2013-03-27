package com.heart.beat.cn.widget;

import com.heart.beat.cn.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
/**
 * 加载更过layout.
 * 该类向listview提供一下接口：改变该layout的state
 * @author slider
 *
 */
public class XListViewFooter extends LinearLayout {
	private static final String TAG = "XListViewFooter";
	
	private int mState = STATE_NORMAL;
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;
	
	private RelativeLayout contentLayout;
	private ProgressBar mProgressBar;
	private TextView mHintView;

	public XListViewFooter(Context context) {
		super(context);
		initView(context);
	}
	private void initView(Context context){
		contentLayout = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.xlistview_footer, null);
		//这个参数必须加上，要不然默认都是wrap_content的
		addView(contentLayout,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		mProgressBar = (ProgressBar)contentLayout.findViewById(R.id.xlistview_footer_progressbar);
		mHintView = (TextView)contentLayout.findViewById(R.id.xlistview_footer_hint_textview);
	}
	
	public void setState(int state){
		if(mState==state)	return;
		switch (state) {
		case STATE_NORMAL:
			mHintView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			break;
		case STATE_READY:
			
			break;
		case STATE_LOADING:
			mHintView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		}
		mState = state;
	}
}
