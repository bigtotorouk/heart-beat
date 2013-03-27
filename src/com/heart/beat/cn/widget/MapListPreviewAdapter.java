package com.heart.beat.cn.widget;

import java.util.List;

import com.heart.beat.cn.app.R;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.response.ResponseBeat;
import com.slider.cn.app.bean.response.ResponsePlace;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;
import com.slider.cn.app.exception.NetException;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * grid for preview grid-item
 * @author slider
 *
 */
public class MapListPreviewAdapter extends ArrayAdapter<ResponseBeat> {
	private static final String TAG = "MapListAdapter";

	private final int STATUS_GRID = 0;
	private final int STATUS_LIST = 1;
	private int status = STATUS_GRID;
	
	
	
	private Activity context;
	private List<ResponseBeat> list;
	private View listView;
	private BeatResource beatApi;
	private AsyncImageLoader imageLoader;
	
	public MapListPreviewAdapter(Activity context, List<ResponseBeat> list,View listView) {
		super(context, R.layout.map_lsit_item,list);
		
		this.context = context;
		this.list = list;
		this.listView = listView;
		beatApi = new BeatResource(context);
		imageLoader = AsyncImageLoader.getInstance();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if(convertView==null){
			holder = new ViewHolder();
			LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(status==STATUS_LIST){
				
				convertView = inflate.inflate(R.layout.map_lsit_item , null);
				holder.placeLocation = (TextView)convertView.findViewById(R.id.place_location);
				holder.placeName = (TextView)convertView.findViewById(R.id.place_name);
				holder.placeImage = (ImageView)convertView.findViewById(R.id.place_image);
				holder.placeBeats = (TextView)convertView.findViewById(R.id.place_beats);
			}else {
				convertView = inflate.inflate(R.layout.map_grid_item, null);
				holder.placeImage = (ImageView)convertView.findViewById(R.id.grid_item_image);
				holder.placeBeats = (TextView)convertView.findViewById(R.id.grid_item_counter);
			}
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		//holder.placeImage.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth-GALLERY_MARGIN,screenWidth-GALLERY_MARGIN));
		// bind data
		
		ResponseBeat beat = list.get(position);
		holder.placeBeats.setVisibility(View.INVISIBLE);
		String url = null;
		if(beat!=null&&beat.getPhoto_urls()!=null&&beat.getPhoto_urls().size()!=0)
			url = beat.getPhoto_urls().get(0);
		if (url != null) {
			bindImageViewWithImage(holder.placeImage, url);
		} else {
			holder.placeImage.setBackgroundDrawable(context.getResources()
					.getDrawable(R.drawable.default_icon));
		}
		
		return convertView;
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
	
	private void bindImageViewWithImage(ImageView image,String url){
		image.setTag(url);
		Drawable drawable = imageLoader.loadDrawable(url, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
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
	
	static class ViewHolder{
		TextView placeLocation;
		TextView placeName;
		ImageView placeImage;
		TextView placeBeats;
	}
	static class GridViewHolder{
		ImageView imageView;
		TextView counter;
	}

}
