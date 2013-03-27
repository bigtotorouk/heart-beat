package com.heart.beat.cn.widget;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.heart.beat.cn.app.R;
import com.slider.cn.app.bean.response.ResponseComment;

public class CommentsAdapter extends ArrayAdapter<ResponseComment> {
	private static final String TAG = "CommentsAdapter";
	private Context mContext;
	private List<ResponseComment> mObjects;
	public CommentsAdapter(Context context,List<ResponseComment> objects) {
		super(context, com.heart.beat.cn.app.R.layout.comment_item, objects);
		mContext = context;
		mObjects = objects;
	}
	
	public void addItem(ResponseComment comment){
		mObjects.add(comment);
		notifyDataSetChanged();
	}
	public void setDatas(List<ResponseComment> objects){
		mObjects = objects;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if(convertView==null){
			LayoutInflater inflate = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflate.inflate(R.layout.comment_item , null);
			holder = new ViewHolder();
			holder.icon = (ImageView)convertView.findViewById(R.id.comment_icon);
			holder.author = (TextView)convertView.findViewById(R.id.comment_author);
			holder.time = (TextView)convertView.findViewById(R.id.comment_time);
			holder.content = (TextView)convertView.findViewById(R.id.comment_content);
			holder.comment = (ImageView)convertView.findViewById(R.id.comment_comment);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		/*
		 * set you view binding data
		 * */
		ResponseComment comment = mObjects.get(position);
		if(comment!=null){
			Log.d(TAG, comment.toString());
			holder.author.setText(comment.getCreator().getUsername());
			holder.content.setText(comment.getData());
			holder.time.setText(comment.getUpload_time().substring(0, 10));
			//holder.time.setText(comment.getUpload_time());
		}
		
		return convertView;
	}
	
	
	static class ViewHolder{
		ImageView icon;
		TextView author;
		TextView content;
		TextView time;
		ImageView comment;
	}

}
