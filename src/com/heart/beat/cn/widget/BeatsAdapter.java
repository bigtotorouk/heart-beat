package com.heart.beat.cn.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.heart.beat.cn.app.R;
import com.slider.cn.app.bean.resource.UserResource;
import com.slider.cn.app.bean.response.ResponseBeat;
import com.slider.cn.app.bean.response.ResponseProfile;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;
import com.slider.cn.app.exception.NetException;

public class BeatsAdapter extends BaseAdapter {
	private static final String TAG = "BeatsAdapter";
	
	private int status = 0;
	public static final int STATUS_INFO = 0; // 表示属于info界面中beat列表的适配器
	public static final int STATUS_PROFILE = 1; // 表示属于profile界面中beat列表的适配器

	private Context mContext;
	private UserResource userApi;
	private List<ResponseBeat> objects;
	private AsyncImageLoader imageLoader;
	private ListView listview;
	
	private boolean isDebug = false; 
	
	
	public BeatsAdapter(int status,Context context,ListView listview,List<ResponseBeat> objects){
		this.status = status;
		mContext = context;
		this.listview = listview;
		userApi = new UserResource();
		imageLoader = AsyncImageLoader.getInstance();
		if(objects==null){
			this.objects = new ArrayList<ResponseBeat>(10);
			for(int i = 0;i<10;i++){
				this.objects.add(new ResponseBeat());
			}
			
		}else{
			this.objects = objects;
		}
		
	}
	
	
	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int arg0) {
		return objects.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	
	public boolean reflesh(List<ResponseBeat> objects){
		
		if(objects==null)
			return false;
		this.objects = objects;
		notifyDataSetChanged();
		return true;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder ;
		if(convertView==null){
			holder = new ViewHolder();
			LayoutInflater inflate = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(status == STATUS_INFO){
				convertView = inflate.inflate(R.layout.beat_item , null);
				holder.icon = (ImageView)convertView.findViewById(R.id.item_icon);
				holder.gridView = (GridView)convertView.findViewById(R.id.item_images);
				holder.image1 = (ImageView)convertView.findViewById(R.id.item_img1);
				holder.image2 = (ImageView)convertView.findViewById(R.id.item_img2);
				holder.image3 = (ImageView)convertView.findViewById(R.id.item_img3);
			}else if(status == STATUS_PROFILE){
				convertView = inflate.inflate(R.layout.beat_list_item , null);
				holder.image = (ImageView)convertView.findViewById(R.id.item_image);
			}
			
			
			holder.author = (TextView)convertView.findViewById(R.id.item_author);
			holder.time = (TextView)convertView.findViewById(R.id.item_time);
			holder.title = (TextView)convertView.findViewById(R.id.item_title);
			holder.price = (TextView)convertView.findViewById(R.id.item_price);
			holder.applaud = (TextView)convertView.findViewById(R.id.item_applaud);
			holder.comment = (TextView)convertView.findViewById(R.id.item_comment);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		/*
		 * set you view binding data
		 * */
		
		ResponseBeat beat = objects.get(position);
		if(status == STATUS_INFO){
			
			String avatarUrl = getUserIcon(beat.getCreator().getId());
			Log.d(TAG, "avatarUrl "+ avatarUrl);
			if(avatarUrl!=null&&!avatarUrl.equals(""))
				bindImageViewWithImage(holder.icon, avatarUrl);
			else {
				holder.icon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.avatar));
			}
			bindViewWithImage(holder,beat);
		} else if(status == STATUS_PROFILE){
			List<String> urls =  beat.getPhoto_urls();
			if(urls!=null&&urls.size()>0)
				bindImageViewWithImage(holder.image,beat.getPhoto_urls().get(0));
			else {
				holder.image.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.default_icon));
			}
		}
		//holder.gridView.setAdapter(new ImagesAdapter(mContext, beat.getPhoto_urls(), imageLoader, listview));
		
		holder.title.setText(beat.getTitle());
		holder.author.setText(beat.getCreator().getUsername());
		if(beat.getPrice()!=0) {
			holder.price.setVisibility(View.VISIBLE);
			holder.price.setText(beat.getPrice()+"");
		}else{
			holder.price.setText("");
			holder.price.setVisibility(View.GONE);
		}
		holder.comment.setText(beat.getComments().size()+"");
		holder.applaud.setText(beat.getHearts().size()+"");
		holder.time.setText(beat.getUpload_time().substring(0, 10));
		
		return convertView;
	}
	
	private String getUserIcon(String userId){
		ResponseProfile profile = null;
		try {
			String url = userApi.createProfileFromId(userId);
			profile = userApi.profile(url, mContext);
		} catch (NetException e) {
			e.printStackTrace();
			
		}
		if(profile!=null)
			return profile.getAvatar();
		return null;
	}
	
	private void bindViewWithImage(ViewHolder holder,ResponseBeat beat){
		List<String> urls = beat.getPhoto_urls();
		
		if(urls!=null&&urls.size()>0){
			int count = 0;
			for(String url:urls){
				if(count==0)
					bindImageViewWithImage(holder.image1, url);
				else if(count==1)
					bindImageViewWithImage(holder.image2, url);
				else if(count==2)
					bindImageViewWithImage(holder.image3, url);
				count++;
			}
			
			holder.image1.setVisibility(View.VISIBLE);
			holder.image2.setVisibility(View.VISIBLE);
			holder.image3.setVisibility(View.VISIBLE);
			
			if(count==1){
				holder.image2.setVisibility(View.INVISIBLE);
				holder.image3.setVisibility(View.INVISIBLE);
			}
			if(count==2){
				holder.image3.setVisibility(View.INVISIBLE);
			}
			
		}else
			holder.image1.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.default_icon));
		
	}
	private void bindImageViewWithImage(ImageView image,String url){
		image.setTag(url);
		Drawable drawable = imageLoader.loadDrawable(url, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				ImageView imageViewByTag = (ImageView) listview.findViewWithTag(imageUrl);
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
			image.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.default_icon));
		}
	}
	
	static class ViewHolder{
		ImageView icon;	//author of the beat
		TextView author;
		TextView time;
		TextView title;
		GridView gridView;
		ImageView image;//image of beat-list xml
		ImageView image1; // image of beat, image-info: 300x300
		ImageView image2;
		ImageView image3;
		TextView price;
		TextView applaud; // praise-data  of the beat
		TextView comment; // comment-data  of the beat
	}

	
}
