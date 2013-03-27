package com.heart.beat.cn.app;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heart.beat.cn.util.ConstantsI;
import com.heart.beat.cn.widget.CommentsAdapter;
import com.heart.beat.cn.widget.ImageCarouseAdapter;
import com.slider.cn.app.bean.request.RequestComment;
import com.slider.cn.app.bean.resource.AccountResource;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.resource.CommentResource;
import com.slider.cn.app.bean.resource.UserResource;
import com.slider.cn.app.bean.response.ResponseBeat;
import com.slider.cn.app.bean.response.ResponseComment;
import com.slider.cn.app.bean.response.ResponseComments;
import com.slider.cn.app.bean.response.ResponseProfile;
import com.slider.cn.app.cache.AsyncImageLoader;
import com.slider.cn.app.cache.AsyncImageLoader.ImageCallback;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.CookieImpl;
/**
 * 这个类暂时不用。其所有功能都转移到info界面去了.
 * the detail information of beat
 * @author slider
 *
 */
public class BeatActivity extends Activity implements OnClickListener, OnItemClickListener {
	public static final String BEAT_ID = "beat_id";
	private static final String TAG = "BeatActivity";
	private static final String UserId = "user_id";
	private int beatId;

	private View back;
	private ListView commentList; /* comment-list */
	private View header;
	private ImageView avatar; // author of the beat
	private TextView author, time, title, description, price, applaud,
			location;
	private View layout_comments;

	private View viewBeat;
	private Gallery galleryFlow;
	private AsyncImageLoader imageLoader;

	/* comment view */
	private View commentSend;
	private EditText commentEdit;
	List<ResponseComment> comments;
	private CommentsAdapter commentsdapter;

	/* beat info */
	private boolean isHeart = false;
	private BeatResource beatApi;
	private ResponseBeat beat;
	private List<String> urls;
	private BaseAdapter imagesAdapter;

	private static final int MSG_SERVER_ERROR = 0;
	private static final int MESSAGE_LOADING = 1;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadBeatLayout();
	}
	
	/**
	 * load the data from server when init activity
	 */
	private void loadBeatLayout(){
		viewBeat = LayoutInflater.from(this).inflate(R.layout.beat, null);
		setContentView(viewBeat);
		/* load infoActivity layout */
		beatId = getIntent().getIntExtra(BEAT_ID, -1);
		
		//初始化参数，防止界面返回切换出现bug
		comments = null;
		
		
		back = findViewById(R.id.back);
		back.setOnClickListener(this);
		commentList = (ListView)findViewById(R.id.comments);
		// add header layout
		header = LayoutInflater.from(this).inflate(R.layout.beat_header, null);
		avatar = (ImageView)header.findViewById(R.id.user_icon);
		author = (TextView)header.findViewById(R.id.beat_author);
		time = (TextView)header.findViewById(R.id.beat_time);
		title = (TextView)header.findViewById(R.id.beat_title);
		description = (TextView)header.findViewById(R.id.beat_description);
		price = (TextView)header.findViewById(R.id.beat_price);
		applaud = (TextView)header.findViewById(R.id.beat_applaud);
		location = (TextView)header.findViewById(R.id.beat_location);
		layout_comments = this.findViewById(R.id.comments_layout);
		
		header.findViewById(R.id.beat_comment).setOnClickListener(this);
		avatar.setOnClickListener(this);
		applaud.setOnClickListener(this);
		commentList.addHeaderView(header);
		
		imageLoader = AsyncImageLoader.getInstance();
		galleryFlow = (Gallery)header.findViewById(R.id.gallery);
		
		commentEdit = (EditText)findViewById(R.id.editcomment);
		commentSend = findViewById(R.id.comment_submit);
		commentSend.setOnClickListener(this);

		//urls = new ArrayList<String>();
		//System.out.println("Beatactivity urls is  "+urls);
		//imagesAdapter = new ImageCarouseAdapter(this,galleryFlow,urls);
		galleryFlow.setFadingEdgeLength(0);     
        galleryFlow.setOnItemClickListener(new OnItemClickListener() {     
            public void onItemClick(AdapterView<?> parent, View view,     
                    int position, long id) {     
            	//
            }     
                 
        });   
		
        commentList.setOnItemClickListener(this);
        
        loadBeatData();
	}
	private void loadBeatData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					Log.d(TAG, "beat_id "+beatId);
					beatApi = new BeatResource(BeatActivity.this);
					beat = beatApi.getBeat(Integer.valueOf(beatId),true);
					
					urls = beat.getPhoto_urls();
					
					ResponseComments responseContent = new CommentResource(BeatActivity.this).getComments(beat.getComments(),true);
					if(responseContent!=null){
						comments = responseContent.getObjects();
						Log.d(TAG, "comments "+comments.toString());
					}
					
					Message msg = mHandler.obtainMessage();
					msg.what = MESSAGE_LOADING;
					mHandler.sendMessage(msg);
				}catch (NetException e) {
					Message msg = mHandler.obtainMessage();
					Bundle data = new Bundle();
					data.putString("data", e.getMessage());
					msg.what = MSG_SERVER_ERROR;
					msg.setData(data);
					mHandler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void updateViewContent(ResponseBeat beat){
		String avatarUrl = getUserIcon(beat.getCreator().getId());
		Log.d(TAG, "avatarUrl "+ avatarUrl);
		if(avatarUrl!=null&&!avatarUrl.equals("")){
			bindImageViewWithImage(avatar, avatarUrl);
		}
		author.setText(beat.getCreator().getUsername());
		title.setText(beat.getTitle());
		time.setText(beat.getUpload_time().substring(0, 10));
		
		if(beat.getPrice()!=0.0d){
			price.setVisibility(View.VISIBLE);
			price.setText(beat.getPrice()+"");
		}else {
			price.setVisibility(View.GONE);
		}
		//set content of applaud view
		CookieImpl cookieManager = new CookieImpl();
		String profileId = cookieManager.getValue(this, CookieImpl.PROFILE_ID);
		ResponseProfile profileMy =null;
		try {
			profileMy = new AccountResource(this).getProfile(profileId);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		Log.d(TAG, "My userId "+profileMy.getUser().getId());
		if(beat.getHearts().contains(profileMy.getUser().getId())){
			applaud.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unapplaud_normal), null, null, null);
			
			isHeart = true;
		}else {
			applaud.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.applaud_normal), null, null, null);
			isHeart = false;
		}
		applaud.setText(beat.getHearts().size()+"");
		
		if(beat.getDescription()!=null&&!beat.getDescription().equals("")){
			description.setVisibility(View.VISIBLE);
			description.setText(beat.getDescription());
		}else {
			description.setVisibility(View.GONE);
		}
		
		location.setText(beat.getPlace().getLocation());
		
	}
	private String getUserIcon(String id){
		ResponseProfile profile = null;
		try {
			UserResource userApi = new UserResource();
			String url = userApi.createProfileFromId(id);
			profile = userApi.profile(url, this);
		} catch (NetException e) {
			e.printStackTrace();
			
		}
		if(profile!=null)
			return profile.getAvatar();
		return null;
	}
	
	private void bindImageViewWithImage(ImageView image,String url){
		image.setTag(url);
		Drawable drawable = imageLoader.loadDrawable(url, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				ImageView imageViewByTag = (ImageView) viewBeat.findViewWithTag(imageUrl);
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
			image.setBackgroundDrawable(getResources().getDrawable(R.drawable.default_icon));
		}
	}

	/**
	 * send a comment to the server
	 * @param reply_to
	 */
	private void sendComment(String reply_to){
		String body = commentEdit.getText().toString();
		if(body==null||body.trim().equals("")){
			Toast.makeText(this, R.string.co_body_null, Toast.LENGTH_SHORT).show();
			return;
		}
		RequestComment comment = new RequestComment();
		comment.setBeat(ConstantsI.BEAT_RESOURCE+beatId+"/");
		comment.setData(body);
		if(reply_to!=null)
			comment.setReply_to(reply_to);
		CommentResource commentApi = new CommentResource(BeatActivity.this);
		String commentUrl;
		try {
			commentUrl = commentApi.postComment(comment);
			if(commentUrl!=null){
				Log.d(TAG, "post comment success");
				Toast.makeText(this, "post a comment success", Toast.LENGTH_SHORT).show();
				commentEdit.setText("");
				layout_comments.setVisibility(View.GONE);
				//notify the comments list 
				ResponseComment com = new CommentResource(BeatActivity.this).getComment(commentUrl,true);
				if(com!=null){
					//com.setUpload_time(getResources().getString(R.string.now_time));
					commentsdapter.addItem(com);
				}
			}else {
				Toast.makeText(BeatActivity.this, R.string.l_post_success, Toast.LENGTH_SHORT).show();
			}
		} catch (NetException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
				
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			this.finish();
			break;
		case R.id.comment_submit:
			sendComment(null);
			break;
		case R.id.user_icon:
			/* get profile when user_avatar invoked */
			String id = String.valueOf(beat.getCreator().getId());
			Intent intent = new Intent(this, PersonalActivity.class);
			intent.putExtra(UserId, id);
			startActivity(intent);
			break;
		case R.id.beat_comment:
			if(layout_comments.getVisibility()==View.GONE){
				layout_comments.setVisibility(View.VISIBLE);
			}else {
				layout_comments.setVisibility(View.GONE);
			}
			break;
		case R.id.beat_applaud:
			if(!isHeart){
				addHeart();
			}else {
				removeHeart();
			}
			
			//removeHeart();
			break;
		default:
			break;
		}
		
	}
	private void addHeart(){
		if(beat!=null){
			try {
				boolean result = beatApi.addHeart(beat.getId());
				if(result)
					Toast.makeText(this, R.string.post_heart_success, Toast.LENGTH_SHORT).show();
				else {
					Toast.makeText(this, R.string.post_heart_failed, Toast.LENGTH_SHORT).show();
				}
			} catch (NetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			applaud.setText(((Integer.valueOf(applaud.getText().toString())+1)+""));
			applaud.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unapplaud_normal), null, null, null);
			isHeart = true;
		}
	}
	private void removeHeart(){
		if(beat!=null){
			try {
				boolean result = beatApi.removeHeart(beat.getId());
				if(result)
					Toast.makeText(this, R.string.remove_heart_success, Toast.LENGTH_SHORT).show();
				else {
					Toast.makeText(this, R.string.remove_heart_failed, Toast.LENGTH_SHORT).show();
				}
			} catch (NetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			applaud.setText(((Integer.valueOf(applaud.getText().toString())-1)+""));
			applaud.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.applaud_normal), null, null, null);
			isHeart = false;
		}
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MESSAGE_LOADING:
				updateViewContent(beat);
				imagesAdapter = new ImageCarouseAdapter(BeatActivity.this,
						galleryFlow, urls);
				galleryFlow.setAdapter(imagesAdapter);
				if (comments == null)
					comments = new ArrayList<ResponseComment>();
				commentsdapter = new CommentsAdapter(BeatActivity.this,
						comments);
				commentList.setAdapter(commentsdapter);

				imagesAdapter.notifyDataSetChanged();
				commentsdapter.notifyDataSetChanged();
				break;
			case MSG_SERVER_ERROR:
				Bundle data = msg.getData();
				Toast.makeText(BeatActivity.this, data.getString("data"),
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	};
}