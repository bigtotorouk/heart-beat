package com.heart.beat.cn.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.heart.beat.cn.app.R;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;
/**
 * view adapter of BeatActivity
 * @author slider
 *
 */
public class ImageCarouseAdapter extends ArrayAdapter<String> {

	private final static int GALLERY_MARGIN = 10;
	private int screenWidth,screenHeight;
	private Context mContext;
	private List<String> values;
	private Gallery mListview;
	private AsyncImageLoader imageLoader;

	/**
	 * note: (width,height) must be with property of image the same.
	 * @param context
	 * @param imageIds 
	 * @param width the with-params of the imageview
	 * @param height the height-params of the imageview
	 */
	/*public ImageCarouseAdapter(Context context,Gallery listview,List<String> urls,final int width, final int height){
		mContext = context;
		this.mListview = listview;
		imageLoader = new AsyncImageLoader();
		this.width = width;
		this.height = height;
		mFilepaths = urls;     
	}*/
	
	public ImageCarouseAdapter(Activity activity,Gallery listview, List<String> values) {
		super(activity, R.layout.gallery_item, values);
		mContext = activity;
		imageLoader = AsyncImageLoader.getInstance();
		this.mListview = listview;
		this.values = values;
		
		// 获取屏幕密度（方法1）  
		screenWidth  = activity.getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）  
		screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("ImageCarouseAdapter getView position is  "+ position+"mFilepaths.size is "+ values.size());
		
		ViewHolder holder ;
		if(convertView==null){
			LayoutInflater inflate = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflate.inflate(R.layout.gallery_item ,parent, false);
			//convertView.setLayoutParams(new Gallery.LayoutParams(screenWidth-GALLERY_MARGIN,screenWidth-GALLERY_MARGIN));
			
			holder = new ViewHolder();
			holder.imageView = (ImageView)convertView.findViewById(R.id.gallery_item_image);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		//设置imageview的大小 必须每次显示的时候调用该方法，否则某些时候会失效.
		holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth-GALLERY_MARGIN,screenWidth-GALLERY_MARGIN));
		
		String url = values.get(position);
		holder.imageView.setTag(url);
		Drawable drawable = imageLoader.loadDrawable(url, new ImageCallback() {		
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				/* you can do nothing for job */
				if(imageDrawable!=null){
					ImageView imageview = (ImageView) mListview.findViewWithTag(imageUrl);
					if(imageview!=null){
						imageview.setBackgroundDrawable(imageDrawable);
					}
				}
				
			}
		});
		if(drawable!=null){
			holder.imageView.setBackgroundDrawable(drawable);
		}else{
			holder.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.default_icon));
		}
		return convertView;
	}
	
	
	static class ViewHolder{
		ImageView imageView;
	}

}
