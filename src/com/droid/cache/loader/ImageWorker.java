package com.droid.cache.loader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.ImageView;
import com.droid.cache.ImageCache;
import com.droid.cache.util.LogUtil;

import java.lang.ref.WeakReference;


public abstract class ImageWorker
{
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;
    private ImageCache mImageCache;
    private boolean mExitTasksEarly = false;
    protected Context mContext;
    private SparseArray<Bitmap> loadingImageMap;


    protected ImageWorker(Context context)
    {
        mContext = context;
        loadingImageMap = new SparseArray<Bitmap>();
    }


    public void loadImage(String url, ImageView imageView, int loadingImageId)
    {
        if(TextUtils.isEmpty(url))
        {
            return;
        }
        Bitmap bitmap = null;
        if (mImageCache != null)
        {
            bitmap = mImageCache.getBitmapFromMemCache(url);
        }
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }
        else if (cancelPotentialWork(url, imageView))
        {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), getLoadingImage(loadingImageId), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(url);
        }
    }


    private Bitmap getLoadingImage(int resId)
    {
        Bitmap loadingBitmap = null;
        loadingBitmap = loadingImageMap.get(resId);
        if (loadingBitmap == null)
        {
            loadingBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
            loadingImageMap.put(resId, loadingBitmap);
        }
        return loadingBitmap;
    }



    public void setImageCache(ImageCache imageCache)
    {
        mImageCache = imageCache;
    }


    public ImageCache getImageCache()
    {
        return mImageCache;
    }


    public void setExitTasksEarly(boolean exitTasksEarly)
    {
        mExitTasksEarly = exitTasksEarly;
    }


    protected abstract Bitmap processBitmap(String url);


    public static void cancelWork(ImageView imageView)
    {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null)
        {
            bitmapWorkerTask.cancel(true);
            LogUtil.d(TAG, "cancel load : " + bitmapWorkerTask.url);
        }
    }


    public static boolean cancelPotentialWork(String url, ImageView imageView)
    {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null)
        {
            final String taskUrl = bitmapWorkerTask.url;
            if (TextUtils.isEmpty(taskUrl) || !taskUrl.equals(url))
            {
                bitmapWorkerTask.cancel(true);
                LogUtil.d(TAG, "cancel load : " + taskUrl);
            }
            else
            {
                return false;
            }
        }
        return true;
    }


    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView)
    {
        if (imageView != null)
        {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable)
            {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;

                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }


    private class BitmapWorkerTask extends AsyncTask<String, String, Bitmap>
    {
        private String url;
        private final WeakReference<ImageView> imageViewReference;


        public BitmapWorkerTask(ImageView imageView)
        {
            //进行弱引用
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            url = params[0];
            Bitmap bitmap = null;

            // If the image cache is not null.
            // 2. If the current thread has not been canceled.
            // 3. If the weakly referenced ImageView is not destroyed.
            // If the loader does not need to exit.
            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null && !mExitTasksEarly)
            {
                bitmap = mImageCache.getBitmapFromDiskCache(url);
            }

            // 1. If the picture taken from the hard disk cache is Null.
            // 2. If the current thread has not been canceled.
            // 3. If the weakly referenced ImageView is not destroyed.
            // If the loader does not need to exit.
            if (bitmap == null && !isCancelled() && getAttachedImageView() != null && !mExitTasksEarly)
            {
                bitmap = processBitmap(params[0]);
            }

            // 1. If the image after loading is not null.
            // 2. If the image cache is not null.
            if (bitmap != null && mImageCache != null)
            {
                mImageCache.addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            // 1. If the current thread is <canceled>.
            // 2. If the loader <was ordered to exit>.
            if (isCancelled() || mExitTasksEarly)
            {
                result = null;
            }
            final ImageView imageView = getAttachedImageView();
            if (result != null && imageView != null)
            {
                setImageBitmap(imageView, result);
            }
        };


        private ImageView getAttachedImageView()
        {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask)
            {
                return imageView;
            }
            return null;
        }
    }


    private class AsyncDrawable extends BitmapDrawable
    {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;


        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask)
        {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }


        public BitmapWorkerTask getBitmapWorkerTask()
        {
            return bitmapWorkerTaskReference.get();
        }
    }


    private void setImageBitmap(ImageView imageView, Bitmap bitmap)
    {
        final Drawable[] layers = new Drawable[] {
                new ColorDrawable(android.R.color.transparent),
                new BitmapDrawable(mContext.getResources(), bitmap) };

        final TransitionDrawable transDrawable = new TransitionDrawable(layers);

        imageView.setImageDrawable(transDrawable);
        transDrawable.startTransition(FADE_IN_TIME);
    }
}
