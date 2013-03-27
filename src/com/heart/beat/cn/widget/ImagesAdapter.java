package com.heart.beat.cn.widget;

import java.util.ArrayList;
import java.util.List;

import com.heart.beat.cn.app.R;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * adapter for GridView. It showing photos of beat in a line.but it is up one line and 
 * the line up to three
 * @author slider
 *
 */
public class ImagesAdapter extends BaseAdapter {
	private List<String> mUrls;
	private final int MAX_LENGTH=3;
	private Context mContext;
	private AsyncImageLoader imageLoader;
	private View parent;
	
	public ImagesAdapter(Context context,List<String> urls,AsyncImageLoader imageLoader,View parent){
		mContext = context;
		this.imageLoader = imageLoader;
		this.parent = parent;
		
		if(urls==null){
			mUrls = new ArrayList<String>();
			return;
		}
		if(urls.size()<=MAX_LENGTH)
			mUrls = urls;
		else{
			mUrls = new ArrayList<String>(MAX_LENGTH);
			for(int i=0;i<MAX_LENGTH;i++){
				mUrls.add(urls.get(i));
			}
				
		}
	}
	
	@Override
	public int getCount() {
		return mUrls.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view = new ImageView(mContext);
		view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.border_2bg));
		bindImageViewWithImage(view,mUrls.get(position));
		return view;
	}
	
	private void bindImageViewWithImage(ImageView image,String url){
		image.setTag(url);
		Drawable drawable = imageLoader.loadDrawable(url, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				ImageView imageViewByTag = (ImageView) parent.findViewWithTag(imageUrl);
				if(imageViewByTag!=null){
					//imageViewByTag.setImageDrawable(imageDrawable);
					imageViewByTag.setImageDrawable(imageDrawable);
				}
			}
		});
		if(drawable!=null){
			//holder.image.setImageDrawable(drawable);
			image.setBackgroundDrawable(drawable);
		}else{
			image.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.default_icon));
		}
	}

}
