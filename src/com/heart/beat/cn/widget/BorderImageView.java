package com.heart.beat.cn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 带边框的ImageView
 * @author ken
 *
 */
public class BorderImageView extends ImageView {
	private Rect rec;
	private Paint paint;
	private final int STROKEWIDTH = 5;
	public BorderImageView(Context context) {
		super(context);
		previewPaint(); 
	}

	public BorderImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		previewPaint(); 
	}

	public BorderImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		previewPaint();
	}
	
	private void previewPaint(){
		paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKEWIDTH); 
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		// 画边框  如果没有if:这个在list滚动的时候，list的上下会出现白色线条
		
			rec = canvas.getClipBounds();
			//canvas.getClipBounds(rec);
			rec.bottom--;
			rec.right--;
			canvas.drawRect(rec, paint);
		
	}
}