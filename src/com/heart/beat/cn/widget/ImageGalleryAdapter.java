package com.heart.beat.cn.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageGalleryAdapter extends BaseAdapter {
	private int width,height;
	private Context mContext;
	private List<String> mFilepaths;
	private List<ImageView> mImages;     
	/**
	 * note: (width,height) must be with property of image the same.
	 * @param context
	 * @param imageIds 
	 * @param width the with-params of the imageview
	 * @param height the height-params of the imageview
	 */
	public ImageGalleryAdapter(Context context,List<String> paths,int width, int height){
		mContext = context;
		this.width = width;
		this.height = height;
		mFilepaths = paths; 
		mImages = new ArrayList<ImageView>();    
    	int index = 0;
    	if(paths==null||paths.size()==0)
    		return;
    	for (String filepath : paths){       
    		Bitmap originalImage = BitmapFactory.decodeFile(filepath);
    		//originalImage = ImageUtil.toRoundCorner(originalImage, 30);  不去圆角
    		ImageView imageView = new BorderImageView(mContext);     
    		imageView.setImageBitmap(originalImage);      
    		imageView.setLayoutParams(new Gallery.LayoutParams(width, height));
    		mImages.add(index++, imageView);    
    	}     
	}
	
	public void add(String path){
		//originalImage = ImageUtil.toRoundCorner(originalImage, 30);  不去圆角
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		ImageView imageView = new BorderImageView(mContext);     
		imageView.setImageBitmap(bitmap);      
		imageView.setLayoutParams(new Gallery.LayoutParams(width, height));
		mImages.add(imageView);
		mFilepaths.add(path);
		notifyDataSetChanged();
	}
	
	public void remove(int position){
		mFilepaths.remove(position);
		mImages.remove(position);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImages.size();
		//return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getCount()==0) {
			return null;
		}
		return mImages.get(position);
		//return mImages.get(position%mImages.size());
	}

}
