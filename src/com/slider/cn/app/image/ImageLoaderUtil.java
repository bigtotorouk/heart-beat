package com.slider.cn.app.image;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.heart.beat.cn.app.BeatActivity;
import com.heart.beat.cn.app.R;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.response.ResponseBeat;
import com.slider.cn.app.bean.response.ResponsePlace;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;
import com.slider.cn.app.exception.NetException;

public class ImageLoaderUtil {
	private AsyncImageLoader imageLoader;
	
	private BeatResource beatApi;
	public ImageLoaderUtil(){
		imageLoader = AsyncImageLoader.getInstance();
	}
	public void LoadBeatImage(int beatId,Context context, ImageView imageView,View parentView){
		String url = getImageUri(beatId);
		if(url!=null){
			bindImageViewWithImage(context, imageView, parentView, url);
		}else {
			imageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_icon));
		}
	}
	
	public void loadProfileIcon(int prfileId){
		
	}
	public void loadUserIcon(int userId){
		
	}
	
	public void loadPlaceIcon(int placeId){
		
	}
	public void loadPlaceIcon(ResponsePlace place, Context context, ImageView imageView, View parentView){
		if(place==null)	return;
		if(beatApi==null)
			beatApi = new BeatResource(context);
		List<String> beatIds = place.getBeats();
		if(beatIds!=null){
			if(beatIds.size()!=0){
				
				//String beatUri = beatApi.convertUrlFromId(beatIds.get(0)); 
				String url = getImageUri(Integer.valueOf(beatIds.get(0)));
				if(url!=null){
					bindImageViewWithImage(context, imageView, parentView, url);
				}else {
					imageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_icon));
				}
			}
		}
	}
	
	private String getImageUri(int beatId){
		ResponseBeat beat = null;
		try {
			beat = beatApi.getBeat(beatId, true);
		} catch (NetException e) {
			e.printStackTrace();
			return null;
		}
		if(beat!=null&&beat.getPhoto_urls()!=null&&beat.getPhoto_urls().size()!=0)
			return beat.getPhoto_urls().get(0);
		else {
			return null;
		}
	}
	private void bindImageViewWithImage(Context context, ImageView image,final View parentView, String url){
		image.setTag(url);
		Drawable drawable = imageLoader.loadDrawable(url, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				ImageView imageViewByTag = (ImageView) parentView.findViewWithTag(imageUrl);
				if(imageViewByTag!=null){
					//imageViewByTag.setImageDrawable(imageDrawable);
					imageViewByTag.setBackgroundDrawable(imageDrawable);
				}
			}
		});
		if(drawable!=null){
			//holder.image.setImageDrawable(drawable);
			image.setBackgroundDrawable(drawable);
		}else{
			image.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_icon));
		}
	}
}
