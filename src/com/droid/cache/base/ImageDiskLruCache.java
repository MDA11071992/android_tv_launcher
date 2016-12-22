package com.droid.cache.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import com.droid.cache.util.CacheConfig;
import com.droid.cache.util.CacheUtils;
import com.droid.cache.util.LogUtil;

import java.io.*;
import java.util.Locale;


public final class ImageDiskLruCache extends DiskLruCache {

    private final static String TAG = "ImageDiskLruCache";


    private int mCompressQuality = 100;


    protected ImageDiskLruCache(File cacheDir, long maxByteSize) {
        super(cacheDir, maxByteSize);
    }


    public final static ImageDiskLruCache openImageCache(Context context, String cacheName, long maxByteSize) {
        File cacheDir = CacheUtils.getEnabledCacheDir(context, cacheName);
        if (cacheDir.isDirectory() && cacheDir.canWrite() && CacheUtils.getUsableSpace(cacheDir) > maxByteSize) {
            return new ImageDiskLruCache(cacheDir, maxByteSize);
        }
        return null;
    }

    public final void putImage(String url, Bitmap bitmap) {
        synchronized (mLinkedHashMap) {
            if (mLinkedHashMap.get(url) == null) {
                final String filePath = createFilePath(url);

                if (writeBitmapToFile(bitmap, filePath, url)) {
                    onPutSuccess(url, filePath);
                    flushCache();
                }
            }
        }
    }


    public final Bitmap getImage(String url) {
        synchronized (mLinkedHashMap) {
            final String filePath = mLinkedHashMap.get(url);
            if (!TextUtils.isEmpty(filePath)) {
                LogUtil.d(TAG, "cache hit : " + url);
                return BitmapFactory.decodeFile(filePath);
            } else {
                final String existFilePath = createFilePath(url);
                if (new File(existFilePath).exists()) {
                    onPutSuccess(url, existFilePath);
                    LogUtil.d(TAG, "cache hit : " + url);
                    return BitmapFactory.decodeFile(existFilePath);
                }
            }
            return null;
        }
    }


    private boolean writeBitmapToFile(Bitmap bitmap, String filePath, String url) {
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(filePath), CacheConfig.IO_BUFFER_SIZE);
            return bitmap.compress(getCompressFormat(url), mCompressQuality, outputStream);
        } catch (FileNotFoundException e) {
            LogUtil.d(TAG, "bitmap compress fail : " + filePath, e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                LogUtil.d(TAG, "close outputStream error : " + e.getMessage());
            }
        }
        return false;
    }


    private CompressFormat getCompressFormat(String url) {
        String lowerUrl = url.toLowerCase(Locale.ENGLISH);
        if (lowerUrl.endsWith(".jpg")) {
            return CompressFormat.JPEG;
        } else if (lowerUrl.endsWith(".png")) {
            return CompressFormat.PNG;
        }
        return CompressFormat.JPEG;
    }
}
