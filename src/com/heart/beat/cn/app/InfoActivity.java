package com.heart.beat.cn.app;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.heart.beat.cn.widget.BeatsAdapter;
import com.heart.beat.cn.widget.XListView;
import com.heart.beat.cn.widget.XListView.IXListViewListener;
import com.slider.cn.app.bean.resource.BeatResource;
import com.slider.cn.app.bean.response.ResponseBeats;
import com.slider.cn.app.cache.TxtCacheUtil;
import com.slider.cn.app.exception.NetException;

public class InfoActivity extends Activity implements OnItemClickListener, OnCheckedChangeListener, OnClickListener {
	private static final String TAG = "InfoActivity";
	private static final String MESSAGE_DATA = "message_data";
	
	private final int STATUS_INFO = 0;//info界面(beat列表界面)
	private final int STATUS_BEAT = 1;//beat 详细信息界面
	private int status = 0;// 默认是info界面
	private View viewInfo;//分别代表info、beat界面
	private LayoutInflater inflater;
	
	private ImageView reflesh;
	private boolean isNeedFlesh = false;
	
	// ViewPager是google SDk中自带的一个附加包的一个类，可以用来实现屏幕间的切换。
	// android-support-v4.jar
	private ViewPager mPager;//页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private RadioGroup infoTab;
	private RadioButton t1, t2, t3;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 1;// 当前页卡编号
	private final int PAGE_FRIENDS = 0;
	private final int PAGE_HOTS = 1;
	private final int PAGE_NEWS = 2;
	private int bmpW;// 动画图片宽度
	
	private static final int OFFSET_WIDTH = 110;
	int one,two,three;//卡片在三个不容位置的位置

	private XListView infoFriends,infoHots,infoNews;
	
	private BeatResource beatApi;
	private ResponseBeats hotBeats,friendsBeats,newsBeats;
	private BeatsAdapter hotsAdapter,friendsAdapter,newsAdapter;
	private TxtCacheUtil hotsCache,friendsCache,newsCache;
	
	private ProgressDialog progress_dialog; 
	
	private static final int MSG_SERVER_ERROR = 0;
	private static final int MSG_LOADING_FRIENDBEATS_SUCCESSED = 1;
	private static final int MSG_LOADING_HOTBEATS_SUCCESSED = 2;
	private static final int MSG_LOADING_RECENTBEATS_SUCCESSED = 3;
	private static final int MSG_COOKIE_ERROR = 4;
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		loadInfoLayout();
		
	}
	
	private void loadInfoLayout(){
		viewInfo = inflater.inflate(R.layout.info_layout, null);
		setContentView(viewInfo);
		reflesh = (ImageView)findViewById(R.id.refresh);
		reflesh.setOnClickListener(this);
		friendsCache = new TxtCacheUtil(InfoActivity.this,TxtCacheUtil.CACHE_FRIENDBEATS);
		hotsCache = new TxtCacheUtil(InfoActivity.this,TxtCacheUtil.CACHE_HOTBEATS);
		newsCache = new TxtCacheUtil(InfoActivity.this,TxtCacheUtil.CACHE_NEWBEATS);
		
		InitImageView();
		InitTextView();
		InitViewPager();
		
		progress_dialog = new ProgressDialog(this);
    	//progress_dialog.setTitle("正在加载数据");
    	//progress_dialog.setIcon(R.drawable.icon);
    	progress_dialog.setMessage(this.getResources().getString(R.string.network_wait));
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(status==STATUS_INFO)
			loadBeats(currIndex);
		else if(status==STATUS_BEAT) {
			
		}

	}
	
	private boolean loadBeats(final int currIndex){
		/*if(!checkNetwork()){
			return false;
		}*/
		
    	progress_dialog.show();
    	new Thread(new Runnable() {	
			@Override
			public void run() {
				try {
					beatApi = new BeatResource(InfoActivity.this);
					switch (currIndex) {
					case 0:
						if(friendsBeats==null||isNeedFlesh){
							friendsBeats = beatApi.getFriends(true);
						}
						/*if(isNeedFlesh){
							friendsBeats = beatApi.getBeats();
							friendsAdapter.reflesh(friendsBeats.getObjects());
							
						}*/
						Message msg = mHandler.obtainMessage();
						msg.what = MSG_LOADING_FRIENDBEATS_SUCCESSED;
						mHandler.sendMessage(msg);
						break;
					case 1:
						if(hotBeats==null||isNeedFlesh){
							hotBeats = beatApi.getHot(false);
						}
						Message msg1 = mHandler.obtainMessage();
						msg1.what = MSG_LOADING_HOTBEATS_SUCCESSED;
						mHandler.sendMessage(msg1);
						break;
					case 2:
						if(newsBeats==null||isNeedFlesh){
							newsBeats = beatApi.getRecent(false);
						}
						Message msg2 = mHandler.obtainMessage();
						msg2.what = MSG_LOADING_RECENTBEATS_SUCCESSED;
						mHandler.sendMessage(msg2);
						break;
	
					default:
						Message msg3 = mHandler.obtainMessage();
						msg3.what = MSG_LOADING_RECENTBEATS_SUCCESSED;
						mHandler.sendMessage(msg3);
						break;
					}
				}catch(NetException e){
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_SERVER_ERROR;
					Bundle data = new Bundle();
					data.putString(MESSAGE_DATA, e.getMessage());
					msg.setData(data);
					mHandler.sendMessage(msg);
				}
			}
		}).start();
    	
		
		return true;
	}
	private boolean checkNetwork() {
        boolean flag = false;
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        if (!flag) {
            Builder b = new AlertDialog.Builder(this).setTitle(R.string.network_no_title).setMessage(R.string.network_no_message);
            b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	/* nothing to do */
                }
            }).create();
            b.show();
        }
 
        return flag;
    }

	/**
	 * 初始化头标
	*/
	private void InitTextView() {
		t1 = (RadioButton) findViewById(R.id.nav_friend);
		t2 = (RadioButton) findViewById(R.id.nav_hot);
		t3 = (RadioButton) findViewById(R.id.nav_news);
		
		infoTab = (RadioGroup)findViewById(R.id.info_tab);
		infoTab.setOnCheckedChangeListener(this);
		if(currIndex==PAGE_FRIENDS)
			t1.setChecked(true);
		else if (currIndex==PAGE_HOTS)
			t2.setChecked(true);	/*default hot-layout checked*/
		else if (currIndex==PAGE_NEWS)
			t3.setChecked(true);
	}

	/**
	 * 初始化ViewPager
	*/
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
			
		View info_hot = mInflater.inflate(R.layout.info_hot, null);
		infoHots = (XListView)info_hot.findViewById(R.id.info_hot_list);
		View info_friend = mInflater.inflate(R.layout.info_friends, null);
		infoFriends = (XListView)info_friend.findViewById(R.id.info_friend_list);
		View info_new = mInflater.inflate(R.layout.info_news, null);
		infoNews = (XListView)info_new.findViewById(R.id.info_news_list);
		
		listViews.add(info_friend);
		listViews.add(info_hot);
		listViews.add(info_new);
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(currIndex);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		infoFriends.setOnItemClickListener(InfoActivity.this);
		infoHots.setOnItemClickListener(InfoActivity.this);
		infoNews.setOnItemClickListener(InfoActivity.this);
		//add scroll listener
		infoHots.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						try {
							hotBeats = new BeatResource(InfoActivity.this).getHot(false);
						} catch (NetException e) {
							Toast.makeText(InfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						if(hotBeats!=null){
							hotsAdapter = new BeatsAdapter(BeatsAdapter.STATUS_INFO,InfoActivity.this, infoHots, hotBeats.getObjects());
							infoHots.setAdapter(hotsAdapter);
							isNeedFlesh = false;
						}
						infoHots.stopRefresh();
					}
				});
				
			}
			
			@Override
			public void onLoadMore() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(InfoActivity.this, "There is nothing more", Toast.LENGTH_SHORT).show();
						infoHots.stopLoadMore();
						infoHots.shutdownLoadMore();
					}
				}, 2000);
				
			}
		});
		
		infoNews.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						try {
							newsBeats = new BeatResource(InfoActivity.this).getRecent(false);
						} catch (NetException e) {
							Toast.makeText(InfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							infoNews.stopRefresh();
							return;
						}
						if(newsBeats!=null){
							newsAdapter = new BeatsAdapter(BeatsAdapter.STATUS_INFO,InfoActivity.this, infoNews, newsBeats.getObjects());
							infoNews.setAdapter(newsAdapter);
							isNeedFlesh = false;
						}
						infoNews.stopRefresh();
					}
				});
				
			}
			
			@Override
			public void onLoadMore() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(InfoActivity.this, "There is nothing more", Toast.LENGTH_SHORT).show();
						infoNews.stopLoadMore();
						infoNews.shutdownLoadMore();
					}
				}, 2000);
				
			}
		});
		
		infoFriends.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						try {
							friendsBeats = new BeatResource(InfoActivity.this).getFriends(true);
						} catch (NetException e) {
							Toast.makeText(InfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							infoFriends.stopRefresh();
							return;
						}
						if(friendsBeats!=null){
							friendsAdapter = new BeatsAdapter(BeatsAdapter.STATUS_INFO,InfoActivity.this, infoFriends,friendsBeats.getObjects());			
							infoFriends.setAdapter(friendsAdapter);
							isNeedFlesh = false;
						}
						infoFriends.stopRefresh();
					}
				});
				
			}
			
			@Override
			public void onLoadMore() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(InfoActivity.this, "There is nothing more", Toast.LENGTH_SHORT).show();
						infoFriends.stopLoadMore();
						infoFriends.shutdownLoadMore();
					}
				}, 2000);
				
			}
		});
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		int initOffset = 0;
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();// 获取图片宽度
		int barWidth = (findViewById(R.id.nav_friend).getWidth());
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = barWidth;// 计算偏移量
		Matrix matrix = new Matrix();
		initOffset = screenW/2 -45;
		matrix.postTranslate(initOffset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
		barWidth = OFFSET_WIDTH;
		one = initOffset -barWidth;
		two = initOffset;
		three = initOffset +barWidth;
		Log.d("Info ....", " one:"+one+" two:"+two+" three:"+three);
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		//int one = //int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		//int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			Log.d("onPageSelected...", "arg0 "+arg0+" currIndex "+currIndex);
			switch (arg0) {
			case 0:
				t1.setChecked(true);
				if (currIndex == 1) {
					animation = new TranslateAnimation(0, -OFFSET_WIDTH, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(OFFSET_WIDTH, -OFFSET_WIDTH, 0, 0);
				} else if (currIndex == 0) {
					return;
				}
				if(friendsBeats==null||isNeedFlesh){
					loadBeats(arg0);
				}
				break;
			case 1:
				t2.setChecked(true);
				if (currIndex == 0) {
					animation = new TranslateAnimation(-OFFSET_WIDTH, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(OFFSET_WIDTH, 0, 0, 0);
				} else if (currIndex == 1) {
					return;
				}
				if(hotBeats==null||isNeedFlesh){
					loadBeats(arg0);
				}
				break;
			case 2:
				t3.setChecked(true);
				if (currIndex == 0) {
					animation = new TranslateAnimation(-OFFSET_WIDTH, OFFSET_WIDTH, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(0, OFFSET_WIDTH, 0, 0);
				} else if (currIndex == 2) {
					return;
				}
				if(newsBeats==null||isNeedFlesh){
					loadBeats(arg0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置  //注释  2012-04-01
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Intent intent  = new Intent(this,BeatActivity.class);
			int beatId = -1;
			if(arg0==infoHots){
				System.out.println("infoHots onitemclick"+" position is "+arg2);
				arg2 -=1;// MyListView的index为0的item显示加载view了
				beatId = Integer.valueOf(hotBeats.getObjects().get(arg2).getId());
			}
			if(arg0==infoFriends){
				System.out.println("infoFriends onitemclick"+" position is "+arg2);
				arg2 -=1;// MyListView的index为0的item显示加载view了
				if(friendsBeats==null)	return;
				beatId = Integer.valueOf(friendsBeats.getObjects().get(arg2).getId());
			}
			if(arg0==infoNews){
				System.out.println("infoNews onitemclick"+" position is "+arg2+" newsBeats.getObjects().size() is"+newsBeats.getObjects().size());
				arg2 -=1;// MyListView的index为0的item显示加载view了
				beatId = Integer.valueOf(newsBeats.getObjects().get(arg2).getId());
			}
			
			//update by slider 2012-09-18
			//System.out.println("onItemClick beatUrl "+beatUrl);
			intent.putExtra(BeatActivity.BEAT_ID, beatId);
			startActivity(intent);
		
	}
	
	@Override
	public void onBackPressed() {
		this.getParent().onBackPressed();
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (mPager==null) {
			Log.e(TAG, "onCheckedChanged mPager null");
			return;
		}
		switch (checkedId) {
		case R.id.nav_friend:
			mPager.setCurrentItem(0);
			break;
		case R.id.nav_hot:
			mPager.setCurrentItem(1);
			break;
		case R.id.nav_news:
			mPager.setCurrentItem(2);
			break;

		default:
			break;
		}
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			System.out.println("InfoActivity refresh onclick is occur ");
			isNeedFlesh = true;
			loadBeats(currIndex);
			break;
		default:
			break;
		}

	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			progress_dialog.dismiss();
			switch (msg.what) {
			case MSG_COOKIE_ERROR:
				Toast.makeText(InfoActivity.this, R.string.cookie_null,
						Toast.LENGTH_LONG).show();
				break;
			case MSG_SERVER_ERROR:
				Toast.makeText(InfoActivity.this, R.string.server_failed,
						Toast.LENGTH_LONG).show();
				updateListView();
				break;
			case MSG_LOADING_FRIENDBEATS_SUCCESSED:
				updateListView();
				break;
			case MSG_LOADING_HOTBEATS_SUCCESSED:
				updateListView();
				break;
			case MSG_LOADING_RECENTBEATS_SUCCESSED:
				updateListView();
				break;
			}
		}
	};
	/**
	 * update beat-list in info-activity
	 */
	private void updateListView(){
		Log.d(TAG, "updateListView() currIndex is "+currIndex);
		switch (currIndex) {
		case 0:
			if(friendsBeats==null){
				
				friendsBeats = friendsCache.getObject();
				if(friendsBeats==null)
					break;
			}else {
				friendsCache.setObject(friendsBeats);
				friendsCache.saveObject(InfoActivity.this);//有新的beats数据，跟新保存到本地的文件
				friendsBeats = friendsCache.getObject();
			}
			if(friendsAdapter==null){
				
				friendsAdapter = new BeatsAdapter(BeatsAdapter.STATUS_INFO,InfoActivity.this, infoFriends,friendsBeats.getObjects());			
				infoFriends.setAdapter(friendsAdapter);
				
				
			}else{
				if(friendsAdapter!=null)
					friendsAdapter.reflesh(friendsBeats.getObjects());
				isNeedFlesh = false;
			}
			break;
		case 1:
			if(hotBeats==null){
				hotBeats = hotsCache.getObject();
				if(hotBeats==null)
					break;
			}else{
				hotsCache.setObject(hotBeats);
				hotsCache.saveObject(InfoActivity.this);
			}
				
			if(hotsAdapter==null){
				hotsAdapter = new BeatsAdapter(BeatsAdapter.STATUS_INFO,InfoActivity.this, infoHots, hotBeats.getObjects());
				infoHots.setAdapter(hotsAdapter);
				
			}else{
				
				hotsAdapter.reflesh(hotBeats.getObjects());
				
				isNeedFlesh = false;
			}
			break;
		case 2:
			
			if(newsBeats==null){
				newsBeats = newsCache.getObject();
				if(newsBeats==null)
					break;
			}else{
				newsCache.setObject(newsBeats);
				newsCache.saveObject(InfoActivity.this);
			}
			
			if(newsAdapter==null){
			//if(true){	
				newsAdapter = new BeatsAdapter(BeatsAdapter.STATUS_INFO,InfoActivity.this, infoNews, newsBeats.getObjects());
				infoNews.setAdapter(newsAdapter);

			}else{
				
				newsAdapter.reflesh(newsBeats.getObjects());
				isNeedFlesh = false;
			}
			break;
		default:
			break;
		}
	}
	
}
