package com.heart.beat.cn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class EquilateralImageView extends ImageView {
	private static final String TAG = "EquilateralImageView";
	private int sideLen = 0;
	private final int spacing = 10;
	
	public EquilateralImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		//measure(MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(sideLen!=0){
			setMeasuredDimension(sideLen, sideLen);
		}
		Log.d(TAG,"onMeasure " + this.getHeight()+ " " + this.getWidth());
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.d(TAG, "onLayout changed = "+changed+" left = "+left+" top = "+top+" right = "+right+" botom = "+bottom);
		if(sideLen==0){
			int screenwidth = right-left;
			sideLen =screenwidth-2*spacing;
		}
		super.onLayout(changed, left, top, right, bottom);
	}


}
