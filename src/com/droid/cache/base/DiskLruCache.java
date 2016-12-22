package com.droid.cache.base;

import android.content.Context;
import com.droid.cache.util.CacheConfig;
import com.droid.cache.util.CacheUtils;
import com.droid.cache.util.LogUtil;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class DiskLruCache {

    private static final String TAG = "DiskLruCache";

    private static final String CACHE_FILENAME_PREFIX = "cache_";

    private static final int MAX_REMOVALS = 4;

    private final File mCacheDir;

    private int cacheNumSize = 0;

    private int cacheByteSize = 0;

    private static final int maxCacheNumSize = 512;

    private long maxCacheByteSize = 1024 * 1024 * 10;

    protected final Map<String, String> mLinkedHashMap =
            Collections.synchronizedMap(new LinkedHashMap<String, String>(32, 0.75f, true));


    private static final FilenameFilter CAHE_FILE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            //The file name that begins with our <cache file naming start> is considered to be a cache file.
            return filename.startsWith(CACHE_FILENAME_PREFIX);
        }
    };


    public final static DiskLruCache openCache(Context context, String cacheName, long maxByteSize) {
        File cacheDir = CacheUtils.getEnabledCacheDir(context, cacheName);
        if (cacheDir.isDirectory() && cacheDir.canWrite() && CacheUtils.getUsableSpace(cacheDir) > maxByteSize) {
            return new DiskLruCache(cacheDir, maxByteSize);
        }
        return null;
    }


    protected DiskLruCache(File cacheDir, long maxByteSize) {
        mCacheDir = cacheDir;
        maxCacheByteSize = maxByteSize;
    }


    public final String put(String key, InputStream inputStream) {
        BufferedInputStream bufferedInputStream = null;
        OutputStream bufferOps = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            String filePath = createFilePath(key);
            bufferOps = new BufferedOutputStream(new FileOutputStream(filePath));

            byte[] b = new byte[CacheConfig.IO_BUFFER_SIZE];
            int count;
            while ((count = bufferedInputStream.read(b)) > 0) {
                bufferOps.write(b, 0, count);
            }
            bufferOps.flush();
            LogUtil.d(TAG, "put success : " + key);
            onPutSuccess(key, filePath);
            flushCache();
            return filePath;
        } catch (IOException e) {
            LogUtil.d(TAG, "store failed to store: " + key, e);
        } finally {
            try {
                if (bufferOps != null) {
                    bufferOps.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                LogUtil.d(TAG, "close stream error : " + e.getMessage());
            }
        }
        return "";
    }


    public final String put(String key, byte[] data) {
        if (data != null) {
            OutputStream bufferOps = null;
            try {
                String filePath = createFilePath(key);
                bufferOps = new BufferedOutputStream(new FileOutputStream(filePath));
                bufferOps.write(data, 0, data.length);
                bufferOps.flush();
                LogUtil.d(TAG, "put success : " + key);
                onPutSuccess(key, filePath);
                flushCache();
                return filePath;
            } catch (IOException e) {
                LogUtil.d(TAG, "put fail : " + key, e);
            } finally {
                try {
                    if (bufferOps != null) {
                        bufferOps.close();
                    }
                } catch (IOException e) {
                    LogUtil.d(TAG, "close outputStream error : " + e.getMessage());
                }
            }
        }
        return "";
    }


    protected final void onPutSuccess(String key, String filePath) {
        mLinkedHashMap.put(key, filePath);
        cacheNumSize = mLinkedHashMap.size();
        cacheByteSize += new File(filePath).length();
    }



    protected final void flushCache() {
        Entry<String, String> eldestEntry;
        File eldestFile;
        long eldestFileSize;
        int count = 0;
        while (count < MAX_REMOVALS && (cacheNumSize > maxCacheNumSize || cacheByteSize > maxCacheByteSize)) {
            eldestEntry = mLinkedHashMap.entrySet().iterator().next();
            eldestFile = new File(eldestEntry.getValue());
            eldestFileSize = eldestFile.length();
            mLinkedHashMap.remove(eldestEntry.getKey());
            eldestFile.delete();
            cacheNumSize = mLinkedHashMap.size();
            cacheByteSize -= eldestFileSize;
            count++;
            LogUtil.d(TAG, "flushCache - Removed :" + eldestFile.getAbsolutePath() + ", " + eldestFileSize);
        }
    }


    public final File get(String key) {
        if (containsKey(key)) {
            final File file = new File(createFilePath(key));
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }


    public final boolean containsKey(String key) {
        //Whether in the map.
        if (mLinkedHashMap.containsKey(key)) {
            return true;
        }
        //If it is not in the map, see Yes or No in the folder.
        final String existingFile = createFilePath(key);
        if (new File(existingFile).exists()) {
            //If there is, into the map directly after the late extraction.
            onPutSuccess(key, existingFile);
            return true;
        }
        return false;
    }


    public final void clearCache() {
        final File[] files = mCacheDir.listFiles(CAHE_FILE_FILTER);
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }


    public final String createFilePath(String key) {
        return new StringBuffer().append(mCacheDir.getAbsolutePath())
                .append(File.separator).append(CACHE_FILENAME_PREFIX)
                .append(key.hashCode()).toString();
    }
}
