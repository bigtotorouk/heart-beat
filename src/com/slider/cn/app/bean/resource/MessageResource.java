package com.slider.cn.app.bean.resource;

import android.content.Context;
import com.google.gson.Gson;
import com.heart.beat.cn.util.ConstantsI;
import com.slider.cn.app.bean.request.RequestMessage;

public class MessageResource {
	private static final String MESSAGE_URL = ConstantsI.SERVER_HOST+"/api/v1/message/";

	private Gson gson;
	private Context mContext;
	public MessageResource(Context context){
		this.mContext = context;
		gson = new Gson();
	}
	public String getMessages(){
		
		return null;
	}
	public String postMessage(RequestMessage message){
		
		return null;
	}
	public String deleteMessage(){
		return null;
	}
}
