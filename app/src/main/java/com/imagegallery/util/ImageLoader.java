package com.imagegallery.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;


import com.imagegallery.data.PhotoToLoad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private static volatile ImageLoader singleton = null;
    private final Context context;
    private final MemCache cache;
    private final FileCache fileCache;
    private final ExecutorService service;

    private ImageLoader(Context context, MemCache cache, ExecutorService service, FileCache fileCache) {
        this.context = context;
        this.cache = cache;
        this.service = service;
        this.fileCache = fileCache;
    }

    public static ImageLoader getInstance(Context context) {
        if (singleton == null) {
            synchronized (ImageLoader.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public void loadImage(String url, ImageView imageView, int placeholder) {

        Bitmap bitmap = cache.getBitmapFromMemCache(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(placeholder);
            queuePhoto(url, imageView);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        service.submit(new PhotosLoader(p));
    }

    public static class Builder {
        private final Context context;
        private ExecutorService service;
        private FileCache fileCache;
        private MemCache cache;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        public ImageLoader build() {
            Context context = this.context;

            if (cache == null) {
                cache = new MemCache();
            }
            if (fileCache == null) {
                fileCache = new FileCache(context);
            }
            if (service == null) {
                service = Executors.newFixedThreadPool(4);
            }
            return new ImageLoader(context, cache, service, fileCache);
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {

            Bitmap bmp=ImageLoaderHelper.getBitmap(photoToLoad.url, fileCache);
            cache.addBitmapToMemoryCache(photoToLoad.url, bmp);

            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
//            if(imageViewReused(photoToLoad))
//                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
//            else
//                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

}
