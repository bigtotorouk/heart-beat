package com.heart.beat.cn.net;

import java.util.HashMap;
import java.util.List;

public class AsyncRunner {
	
	public void run(final String url,final HashMap<String, String> params,final String method,final List<FileEntity> file,final RequestListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = null;
				result = HttpUtil.getInstance().openUrl(url,params,method,file,false);
				listener.onComplete(result);
			}
		}).start();
	}
	
	public static interface RequestListener{
		void onComplete(String result);
		void onError();
	}
}
