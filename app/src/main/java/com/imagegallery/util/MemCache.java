package com.imagegallery.util;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemCache {

    private LruCache<String, Bitmap> cache;

    MemCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        cache = new LruCache<String, Bitmap>(cacheSize);
    }

    public Bitmap getBitmapFromMemCache(String key){
        return cache.get(key);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if (getBitmapFromMemCache(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public void clear() {
        cache.evictAll();
    }
}
