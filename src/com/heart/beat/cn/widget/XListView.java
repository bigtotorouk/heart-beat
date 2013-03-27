package com.heart.beat.cn.widget;

import com.heart.beat.cn.app.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.Scroller;

public class XListView extends ListView {
	private static final String TAG = "XListView";
	
	private float mLastY = -1;
	/*support iOS like pull feature.这个参数随便你设置，作用就是降低滑动的敏感度*/
	private final static float OFFSET_RADIO = 1.8f; // 
	
	private IXListViewListener mListener;
	private Scroller mScroller; // used for scroll back
	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLL_DURATION = 400; // scroll back duration
	
	// -- HEADER VIEW --
	private boolean mEnablePullRefresh = true;
	private XListViewHeader viewHeader;
	private int mHeaderViewHeight = -1; //header-layout 加载完后是一个固定的值
	
	private boolean mPullRefreshing = false;
	
	// -- FOOTER VIEW --
	private XListViewFooter viewFooter;
	
	// three constructor 
	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initListView(context);
	}
	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initListView(context);
	}
	public XListView(Context context) {
		super(context);
		initListView(context);
	}
	
	private void initListView(Context context){
		//init header view
		mScroller = new Scroller(context, new DecelerateInterpolator());
		viewHeader = new XListViewHeader(context);
		addHeaderView(viewHeader);
		viewFooter = new XListViewFooter(context);
		addFooterView(viewFooter);
		viewHeader.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				//这个方法在viewheader加载完成后调用
				mHeaderViewHeight = viewHeader.findViewById(R.id.xlistview_header_content).getHeight();
				Log.d(TAG, "onGlobalLayout mHeaderViewHeight"+mHeaderViewHeight);
				viewHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		viewFooter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewFooter.setState(XListViewFooter.STATE_LOADING);
				mListener.onLoadMore();
			}
		});

		//default listener;
		mListener = new IXListViewListener(){
			@Override
			public void onRefresh() {}
			@Override
			public void onLoadMore() {}
		};
	}
	
	public void setXListViewListener(IXListViewListener listener){
		if(listener!=null){
			mListener = listener;
		}
	}
	
	public void setPullRefreshEnable(boolean enable){
		mEnablePullRefresh = enable;
		if(!mEnablePullRefresh){
			viewHeader.setVisibility(View.GONE);
		}else {
			viewHeader.setVisibility(View.VISIBLE);
		}
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			float deltaY = ev.getRawY() - mLastY;
			if(mEnablePullRefresh&&getFirstVisiblePosition()==0 && deltaY>0){
				updateHeaderHeight(deltaY/OFFSET_RADIO);
			}
			
			
			mLastY = ev.getRawY();
			break;

		case MotionEvent.ACTION_UP:
			
			if(mEnablePullRefresh&&getFirstVisiblePosition()==0&&mEnablePullRefresh){
				
				if(viewHeader.getVisableHeight()>mHeaderViewHeight){
					mPullRefreshing = true;
					viewHeader.setState(XListViewHeader.STATE_REFRESHING);
					
					mListener.onRefresh();
					
				}
				// headerview height复原
				resetHeaderView(false);
				
			}
			/* getAdapter is null if data is null */
			Log.d(TAG, "getAdapter() "+getAdapter());
			if(getAdapter()!=null&&getLastVisiblePosition() == (getAdapter().getCount()-1)){
				Log.d(TAG, "onTouchEvent getLastVisiblePosition() "+getLastVisiblePosition()+" (getAdapter().getCount()-1) "+(getAdapter().getCount()-1));
				
			}
			mLastY = -1;
			break;
		}
		
		
		return super.onTouchEvent(ev);
	}

	private void updateHeaderHeight(float delta){
		viewHeader.setVisiableHeight((int)delta + viewHeader.getVisableHeight());
		if(mEnablePullRefresh&&!mPullRefreshing){ // 未处于刷新状态，更新箭头
			//通过判断
			int currentHeight = viewHeader.getVisableHeight();
			if(currentHeight>mHeaderViewHeight){
				viewHeader.setState(XListViewHeader.STATE_READY);
			}else {
				viewHeader.setState(XListViewHeader.STATE_NORMAL);
			}
		}
	}
	/**
	 * 设置更新时间，最好在viewHeader显示前设置
	 * @param time
	 */
	public void setRefreshTime(String time){
		viewHeader.setRefreshTime(time);
	}
	/**
	 * 当onRefresh()完成后，用户应该主动调用它
	 */
	public void stopRefresh(){
		if(mPullRefreshing == true) { //最后将高度调整为0
			mPullRefreshing = false;
			resetHeaderView(true);
			viewHeader.updateTime(true);
		}
	}
	
	/**
	 * 重新调整viewHeader的高度。任何时候松手都应该调用该方法。
	 */
	private void resetHeaderView(boolean stop){
		int height = viewHeader.getVisableHeight();
		int finalHeight = 0;
		if(!stop&&height>mHeaderViewHeight)
			finalHeight = mHeaderViewHeight;
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
		
	}
	@Override
	public void computeScroll() {
		//先判断mScroller滚动是否完成  
		if(mScroller.computeScrollOffset()) {  
        	if (mScrollBack==SCROLLBACK_HEADER) {
        		Log.d(TAG, "computeScroll: mScroller.getCurrY() "+mScroller.getCurrY());
        		viewHeader.setVisiableHeight(mScroller.getCurrY());
        		//scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			}
			
            //必须调用该方法，否则不一定能看到滚动效果  
            postInvalidate();  
        }
		super.computeScroll();
	}
	
	/**
	 * 当onLoadMore() 完成后，用户应该主动调用
	 */
	public void stopLoadMore(){
		viewFooter.setState(XListViewFooter.STATE_NORMAL);
	}
	/**
	 * 当onLoadMore没有数据的时候，表示服务器上没有数据了，那么应该关闭这个功能
	 */
	public void shutdownLoadMore(){
		viewFooter.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

}
