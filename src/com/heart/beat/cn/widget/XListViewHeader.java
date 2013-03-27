package com.heart.beat.cn.widget;

import java.util.Date;

import com.heart.beat.cn.app.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 高度变化的是主有由于contentLayout，该header有三种状态：normal，ready，refreshing。
 * 他们的流程为： 
 * [ refreshing --> ] normal <--> ready --> refreshing --> normal(下一个流程) ...
 * @author slider
 *
 */
public class XListViewHeader extends LinearLayout{

	// state value
	private int mState = STATE_NORMAL;
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	
	private LinearLayout contentLayout;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView,mTimeTextView;
	
	private long lastTime = -1;
	
	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	
	private final int ROTATE_ANIM_DURATION = 180;
	
	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}
	private void initView(Context context){
		contentLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
		//初始情况，设置下拉刷新view的height为0
		addView(contentLayout,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0));
		setGravity(Gravity.BOTTOM);
		
		mArrowImageView = (ImageView)contentLayout.findViewById(R.id.xlistview_header_arrow);
		mProgressBar = (ProgressBar)contentLayout.findViewById(R.id.xlistview_header_progressbar);
		mHintTextView = (TextView)contentLayout.findViewById(R.id.xlistview_header_hint_textview);
		mTimeTextView = (TextView) contentLayout.findViewById(R.id.xlistview_header_time);
		
		// 两个对应的动画效果
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillEnabled(true);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillEnabled(true);
		mRotateDownAnim.setFillAfter(true);
		
		//更新并替换时间
		lastTime = new Date().getTime();
	}
	
	public void setVisiableHeight(int height){
		if(height<0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) contentLayout.getLayoutParams();
		lp.height = height;
		contentLayout.setLayoutParams(lp);
	}
	public int getVisableHeight(){
		return contentLayout.getHeight();
	}
	
	public void setRefreshTime(String time){
		mTimeTextView.setText(time);
	}
	public void updateTime(boolean replace){
		String text = "";
		long currentTime = new Date().getTime();
		if(lastTime!=-1){
			long delta = currentTime - lastTime;
			if(delta<(60*1000)){
				text = "just now";
			}else if(delta<(60*60*1000)){
				text = delta/(60*1000) + " minute ago";
			}else if(delta<(24*60*60*1000)){
				text = delta/(60*60*1000) + " hour ago";
			}else {
				text = delta/(24*60*60*1000) + " day ago";
			}
			mTimeTextView.setText(text);
		}
		
		if(replace)
			lastTime = currentTime;
	}
	
	public void setState(int state){
		//更新显示时间
		updateTime(false);
		if(mState==state)	return;
		
		switch (state) {
		case STATE_NORMAL:
			// 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			if(mState == STATE_READY){
				mArrowImageView.startAnimation(mRotateDownAnim);
			}else if (mState == STATE_REFRESHING) {
				//清除动画
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText(R.string.xlistview_header_hint_normal);
			break;
		case STATE_READY:
			// 显示箭头图片
			mArrowImageView.startAnimation(mRotateUpAnim);
			mHintTextView.setText(R.string.xlistview_header_hint_ready);
			break;
		case STATE_REFRESHING:
			// 显示进度
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			
			mArrowImageView.clearAnimation();//这个不清楚，这个动画图像会现实出来，尽管你做了mArrowImageView.setVisibility(View.INVISIBLE)处理
			mHintTextView.setText(R.string.xlistview_header_hint_loading);
			break;
		default:
			break;
		}
		mState = state;
	}
		
}
