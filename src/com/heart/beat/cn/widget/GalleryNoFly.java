package com.heart.beat.cn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class GalleryNoFly extends Gallery {

	public GalleryNoFly(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
		//return super.onFling(e1, e2, velocityX, velocityY);
	}

}
