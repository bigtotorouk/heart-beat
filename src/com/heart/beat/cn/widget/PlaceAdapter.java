package com.heart.beat.cn.widget;

import java.util.List;
import com.heart.beat.cn.app.R;
import com.slider.cn.app.google.place.PlaceMeta;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceAdapter extends ArrayAdapter<PlaceMeta> {
	private Context mContext;
	private List<PlaceMeta> mList;
	private Drawable defaultIcon;
	public PlaceAdapter(Context context, List<PlaceMeta> list) {
		super(context, R.layout.map_select_header,list);
		mList = list;
		mContext = context;
		defaultIcon = mContext.getResources().getDrawable(R.drawable.share_location);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.map_select_header, null);
			
			holder.icon = (ImageView)convertView.findViewById(R.id.item_icon);
			holder.title = (TextView)convertView.findViewById(R.id.item_title);
			holder.description = (TextView)convertView.findViewById(R.id.item_description);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.icon.setImageDrawable(defaultIcon);
		holder.title.setText(mList.get(position).getName());
		holder.description.setText(mList.get(position).getVicinity());
		
		return convertView;
	}
	class ViewHolder{
		ImageView icon;
		TextView title;
		TextView description;
	}
	
}
