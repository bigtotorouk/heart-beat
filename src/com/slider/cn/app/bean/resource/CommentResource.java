package com.slider.cn.app.bean.resource;

import java.util.List;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.heart.beat.cn.util.ConstantsI;
import com.slider.cn.app.bean.request.RequestComment;
import com.slider.cn.app.bean.response.ResponseComment;
import com.slider.cn.app.bean.response.ResponseComments;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.HttpJsonUtil;
import com.slider.cn.app.httpurlconnectionnet.HttpResult;

public class CommentResource {
	private static final String TAG = "CommentResource";
	private static final String POST_COMMENT = ConstantsI.SERVER_HOST+"/api/v1/beatcomment/?format=json";
	private static final String GET_COMMENT = "http://192.168.1.66/beat/1/comment/?format=json";
	private static final String GET_COMMENTS = "http://192.168.1.66/api/v1/beatcomment/set/12,13,14/?format=json";
	private Gson gson;
	private String status = "";
	private Context mContext;
	public CommentResource(Context context){
		mContext = context;
		gson = new Gson();
	}
	
	public String getStatus(){
		return this.status;
	}
	
	/**
	 * test pass 2012-7-20
	 * @return
	 * @throws NetException 
	 */
	public String postComment(RequestComment comment) throws NetException{
		String json = gson.toJson(comment);
		HttpJsonUtil http = HttpJsonUtil.getInstance();
		HttpResult response = http.connectPost(POST_COMMENT,mContext, json);
		if(response!=null&&response.isCodeOk()){
			Log.d(TAG, "postComment HttpResult"+response);
			return response.getResult();
		}else {
			return null;
		}
	}
	/**
	 * test pass
	 * @throws NetException 
	 */
	public ResponseComment getComment(String url,boolean requireLogin) throws NetException{
		
		HttpResult response = HttpJsonUtil.getInstance().connectGet(url,mContext,requireLogin);
		ResponseComment comment = null;
		if(response!=null&&response.isCodeOk()){
			System.out.println("getComment: resulst "+response);
			try {
				comment = gson.fromJson(response.getResult(), ResponseComment.class);
			} catch (JsonSyntaxException e) {
				// TODO: handle exception
			}
			
		}
		return comment;
		
	}
	
	/**
	 * test pass 2012-7-20
	 * @param ids : the ids of comments
	 * @return ResponseComments
	 * @throws NetException 
	 */
	public ResponseComments getComments(List<String> ids,boolean requireLogin) throws NetException{
		if(ids==null||ids.size()==0)
			return null;
		StringBuilder sb = new StringBuilder(ConstantsI.SERVER_HOST+"/api/v1/beatcomment/set/");
		for(String id : ids){
			sb.append(id+",");
		}
		sb.deleteCharAt(sb.length()-1).append("/?format=json");	
		String url = sb.toString();
	
		HttpResult response = HttpJsonUtil.getInstance().connectGet(url,mContext,requireLogin);
		ResponseComments comments = null;
		if(response!=null&&response.isCodeOk()){
			System.out.println("getComments: "+response);
			comments = gson.fromJson(response.getResult(), ResponseComments.class);
		}
		return comments;
	}

}
