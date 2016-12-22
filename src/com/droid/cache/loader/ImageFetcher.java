package com.droid.cache.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import com.droid.cache.base.DiskLruCache;
import com.droid.cache.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageFetcher extends ImageWorker
{

    private static final String TAG = "ImageFetcher";


    public ImageFetcher(Context context)
    {
        super(context);
    }

    @Override
    protected Bitmap processBitmap(String url)
    {
        final File file = downloadBitmap(url);
        if (file != null)
        {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }


    private File downloadBitmap(String netUrl)
    {
        DiskLruCache diskCache = getImageCache().getDiskCache();
        LogUtil.d(TAG, "load : " + netUrl);
        HttpURLConnection urlConnection = null;
        try
        {
            final URL url = new URL(netUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            String filePath = diskCache.put(netUrl, urlConnection.getInputStream());
            if(!TextUtils.isEmpty(filePath))
            {
                return new File(filePath);
            }
        }
        catch (IOException e)
        {
            LogUtil.e(TAG, "load error :" + netUrl, e);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
