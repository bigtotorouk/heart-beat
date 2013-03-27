package com.heart.beat.cn.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class CustomOverlay extends ItemizedOverlay<OverlayItem> {
	private final static String TAG = "CustomOverlay";
	
	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
			| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
	
	//创建一个list对象，用来存放该图层中所有的标记对象
	private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	private Context mContext;
	private GeoPoint centerGeo;
	private Drawable marker; 
	
	/**
	 * @param arg0 指定标记所使用的默认图片
	 * @param context
	 */
	public CustomOverlay(Drawable arg0,Context context,GeoPoint centerGeo) {
		super(arg0);
		this.centerGeo = centerGeo;
		this.marker = arg0;
		mContext = context;
	}
	
	public void addOverlay(OverlayItem object) {
		overlayItems.add(object);
		populate();
	}
	public void addOverlay(ArrayList<OverlayItem> objects){
		for(OverlayItem object:objects)
			overlayItems.add(object);
		populate();
	}
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		// TODO Auto-generated method stub
		return super.draw(canvas, mapView, shadow, when);
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Log.d(TAG, "shadow is "+shadow);
		if (!shadow) {
			boundCenterBottom(this.marker);
			canvas.save(LAYER_FLAGS);

			Projection projection = mapView.getProjection();
			int size = overlayItems.size();
			Point point = new Point();
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			OverlayItem overLayItem;

			for (int i = 0; i < size; i++) {
				overLayItem = overlayItems.get(i);

				Drawable marker = overLayItem.getMarker(0);
				// marker.getBounds()
				/* 象素点取得转换 */
				projection.toPixels(overLayItem.getPoint(), point);

				if (marker != null) {
					boundCenterBottom(marker);
				}
				
				/* 圆圈 
				paint.setColor(Color.RED);
				canvas.drawCircle(point.x, point.y, 5, paint);

				 标题 
				String title = overLayItem.getTitle();

				if (title != null && title.length() > 0) {
					paint.setColor(Color.BLACK);
					paint.setTextSize(15);
					canvas.drawText(title, point.x, point.y, paint);
				}
				*/
			}
			/* 标明手机位置点 */
			projection.toPixels(centerGeo, point);
			paint.setColor(Color.RED);
			canvas.drawCircle(point.x, point.y, 5, paint);

			canvas.restore();
			super.draw(canvas, mapView, false);
		}
		//super.draw(canvas, mapView, shadow);
	}
	/*@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		//boundCenterBottom(marker);
		if(!shadow){
			
		}
		super.draw(canvas, mapView, shadow);
	}*/
	
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return overlayItems.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return overlayItems.size();
	}
	//当用户点击的时候，所调用的函数
	@Override
	protected boolean onTap(int index) {
		/*Log.d(TAG, "onTap .... : index is "+index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		OverlayItem object = overlayItems.get(index);
		dialog.setTitle(object.getTitle());
		dialog.setMessage(object.getSnippet());
		dialog.show();
		return true;*/
		return super.onTap(index);
	}
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		// TODO Auto-generated method stub
		return super.onTap(p, mapView);
	}
}
