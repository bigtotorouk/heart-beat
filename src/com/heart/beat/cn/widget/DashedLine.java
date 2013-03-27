package com.heart.beat.cn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * dashed line
 * @author slider
 *
 */
public class DashedLine extends TextView {
	private Paint paint;
	private Path path;

	public DashedLine(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getTextColors().getDefaultColor());

		path = new Path();
		path.moveTo(0, getHeight() / 2);
		path.lineTo(getWidth(), getHeight() / 2);

		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
		paint.setPathEffect(effects);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		/*
		 * Paint paint = new Paint(); paint.setStyle(Paint.Style.STROKE);
		 * paint.setColor(getTextColors().getDefaultColor());
		 */
		/*
		 * Path path = new Path(); path.moveTo(0, getHeight()/2);
		 * path.lineTo(getWidth(), getHeight()/2);
		 */

		canvas.drawPath(path, paint);
	}
}
