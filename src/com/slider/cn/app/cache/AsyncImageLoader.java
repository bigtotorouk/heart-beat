package com.slider.cn.app.cache;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

/**
 * imnage loading from url
 * 
 * @author ken
 * 
 */
public class AsyncImageLoader {

	private HashMap<String, SoftReference<Drawable>> imageCache;

	private ImageCacheUtil diskImageCache;

	private static AsyncImageLoader loader;

	private AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
		diskImageCache = ImageCacheUtil.getInstance();
	}

	public static synchronized AsyncImageLoader getInstance() {
		if (loader == null)
			loader = new AsyncImageLoader();
		return loader;
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		} else {
			Bitmap bitmap = diskImageCache.getImageUrl(imageUrl);
			if (bitmap != null) {
				BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
				return bitmapDrawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				// save to disk too
				if (drawable != null) {
					diskImageCache.saveFile(imageUrl,
							((BitmapDrawable) drawable).getBitmap());
				}
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(i, "src");
		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

}