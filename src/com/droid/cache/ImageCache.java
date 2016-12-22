package com.droid.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.droid.cache.base.DiskLruCache;
import com.droid.cache.base.ImageDiskLruCache;
import com.droid.cache.util.CacheConfig;
import com.droid.cache.util.CacheUtils;
import com.droid.cache.util.LogUtil;


public class ImageCache
{
    private static final String TAG = "ImageCache";

    private ImageDiskLruCache mImageDiskCache;

    private LruCache<String, Bitmap> mMemoryCache;

    private static ImageCache mInstance;


    public static ImageCache getInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new ImageCache(context);
        }
        return mInstance;
    }


    private ImageCache(Context context)
    {
        init(context);
    }


    private void init(Context context)
    {
        mImageDiskCache =
                ImageDiskLruCache.openImageCache(context, CacheConfig.Image.DISK_CACHE_NAME, CacheConfig.Image.DISK_CACHE_MAX_SIZE);

        final int imageMemorySize = CacheUtils.getMemorySize(context, CacheConfig.Image.MEMORY_SHRINK_FACTOR);
        LogUtil.d(TAG, "memory size : " + imageMemorySize);
        mMemoryCache = new LruCache<String, Bitmap>(imageMemorySize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                return CacheUtils.getBitmapSize(bitmap);
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue)
            {
            }
        };
    }


    public void addBitmapToCache(String key, Bitmap bitmap)
    {
        if (key == null || bitmap == null)
        {
            return;
        }
        if (mMemoryCache != null && mMemoryCache.get(key) == null)
        {
            mMemoryCache.put(key, bitmap);
        }
        if (mImageDiskCache != null && !mImageDiskCache.containsKey(key))
        {
            mImageDiskCache.putImage(key, bitmap);
        }
    }


    public boolean exists(String key)
    {
        if (mImageDiskCache != null && mImageDiskCache.containsKey(key))
        {
            return true;
        }
        return false;
    }


    public Bitmap getBitmapFromMemCache(String key)
    {
        if (mMemoryCache != null)
        {
            final Bitmap bitmap = mMemoryCache.get(key);
            if (bitmap != null)
            {
                LogUtil.d(TAG, "memory cache hit : " + key);
                return bitmap;
            }
        }
        return null;
    }


    public Bitmap getBitmapFromDiskCache(String key)
    {
        if (mImageDiskCache != null)
        {
            return mImageDiskCache.getImage(key);
        }
        return null;
    }


    public void clearMemoryCache()
    {
        mMemoryCache.evictAll();
    }


    public DiskLruCache getDiskCache()
    {
        return mImageDiskCache;
    }


    private void test() {

    }


}
