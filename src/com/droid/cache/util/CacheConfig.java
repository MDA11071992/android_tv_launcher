package com.droid.cache.util;


public interface CacheConfig
{
    int IO_BUFFER_SIZE = 8 * 1024;

    String DISK_CACHE_NAME = "/CacheDir";


    interface Image
    {
        String DISK_CACHE_NAME = "/images";

        int DISK_CACHE_MAX_SIZE = 1024 * 1024 * 20;

        int MEMORY_SHRINK_FACTOR = 8;
    }
}
