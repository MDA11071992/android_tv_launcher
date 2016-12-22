package com.droid.cache.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public final class CacheUtils
{

    public static int getBitmapSize(Bitmap bitmap)
    {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    public static boolean isExternalStorageRWable()
    {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    public static File getEnabledCacheDir(Context context, String cacheName)
    {
        String cachePath;
        if(isExternalStorageRWable())
        {
            cachePath = Environment.getExternalStorageDirectory().getPath();
        }
        else
        {
            cachePath = context.getCacheDir().getPath();
        }
        File cacheFile = new File(cachePath + CacheConfig.DISK_CACHE_NAME + cacheName);
        if (!cacheFile.exists())
        {
            cacheFile.mkdirs();
        }
        return cacheFile;
    }


    public static long getUsableSpace(File path)
    {
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Return the approximate per-application memory class of the current device. 
     * This gives you an idea of how hard a memory limit you should impose on your 
     * application to let the overall system work best. The returned value is in megabytes; 
     * the baseline Android memory class is 16 (which happens to be the Java heap limit of those devices); 
     * some device with more memory may return 24 or even higher numbers.
     */
    private static int getMemoryClass(Context context)
    {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }


    public static int getMemorySize(Context context, int shrinkFactor)
    {
        int totalSize = getMemoryClass(context)*1024*1024;

        return totalSize / shrinkFactor;
    }
}
