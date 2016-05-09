package ru.technotrack.music.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

public class ImageManager {
    private static ImageManager ourInstance = new ImageManager();
    private static LruCache<String, Bitmap> mMemoryCache;
    public static String BITMAP_POST_PICTURE_PORTRAIT = "POST_PICTURE_PORTRAIT";
    public static String BITMAP_POST_PICTURE_LANDSCAPE = "POST_PICTURE_LANDSCAPE";

    public static ImageManager getInstance() {
        return ourInstance;
    }

    private ImageManager() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    private int getInSampleSize(int width, int height,
                                int availableWidth, int availableHeight) {
        int inSampleSize = 1;
        width /= 2;
        height /= 2;
        while (width > availableWidth && height > availableHeight) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    private void addToCache(String key, String type, Bitmap bitmap) {
        mMemoryCache.put(key + type, bitmap);
    }

    private Bitmap getFromCache(String key, String type) {
        return mMemoryCache.get(key + type);
    }

    private class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> mWeakImageView;
        private final WeakReference<Context> mContext;
        private final String mPathToPicture;
        private final int mWidth;
        private final int mHeight;
        private final String mType;

        public LoadImageTask(Context context, ImageView imageView, String path, String type,
                             int width, int height) {
            super();
            mWeakImageView = new WeakReference<>(imageView);
            mContext = new WeakReference<>(context);
            mPathToPicture = path;
            mWidth = width;
            mHeight = height;
            mType = type;
        }

        protected Bitmap decodeFile(File file) {
            try {
                InputStream is = new FileInputStream(file);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);
                options.inSampleSize =
                        getInSampleSize(options.outWidth, options.outHeight, mWidth, mHeight);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file),
                        null, options);

                if (bitmap != null) {
                    Log.d("LOAD_IMAGE", " name = " + mPathToPicture +
                            " w = " + bitmap.getWidth() + " h = " + bitmap.getHeight());
                }
                return bitmap;
            } catch (IOException e) {
                Log.i("LoadImageTask", "LoadImageTask.LoadBitmap IOException " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            InputStream is = null;
            OutputStream os = null;
            try {
                Context context = mContext.get();
                Bitmap bitmap;
                File file;
                if (context != null) {
                    file = new File(context.getCacheDir(), mPathToPicture.replace("/", "") + mType);
                    bitmap = decodeFile(file);
                    if (null == bitmap) {
                        URL url = new URL(mPathToPicture);
                        is = url.openConnection().getInputStream();
                        os = new FileOutputStream(file);
                        Utils.copyStream(is, os);
                        os.close();
                        bitmap = decodeFile(file);
                    }
                    return bitmap;
                }
            } catch (IOException e) {
                Log.e("LoadImageTask", "LoadImageTask.LoadBitmap IOException " + e.getMessage(), e);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            Bitmap cachedBitmap = getFromCache(mPathToPicture, mType);
            if (cachedBitmap == null && bitmap != null) {
                addToCache(mPathToPicture, mType, bitmap);
                cachedBitmap = bitmap;
            }

            ImageView imageView = mWeakImageView.get();
            if (imageView != null && this == getBitmapDownloaderTask(imageView)) {

                imageView.setImageBitmap(cachedBitmap);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                params.width = mWidth;
                params.height = mHeight;
                params.gravity = Gravity.CENTER_HORIZONTAL;
                imageView.setLayoutParams(params);
            }
        }
    }

    private static class DownloadDrawable extends ColorDrawable {
        private final WeakReference<LoadImageTask> mLoadTaskWeak;

        private DownloadDrawable(LoadImageTask loadTask) {
            super(Color.GRAY);
            mLoadTaskWeak = new WeakReference<>(loadTask);
        }

        public LoadImageTask getTask() {
            return mLoadTaskWeak.get();
        }
    }

    public void loadBitmap(Context context, String pathToPicture, String type,
                            ImageView imageView, int width, int height) {

        final Bitmap cachedBitmap = getFromCache(pathToPicture, type);
        if (cachedBitmap != null) {
            cancelDownload(pathToPicture, type, imageView);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            params.width = width;
            params.height = height;
            params.gravity = Gravity.CENTER_HORIZONTAL;
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(cachedBitmap);
        } else {
            LoadImageTask lt = new LoadImageTask(context, imageView, pathToPicture, type,
                    width, height);

            DownloadDrawable dd = new DownloadDrawable(lt);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            imageView.setImageDrawable(dd);

            lt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void cancelDownload(String pathToPicture, String type, ImageView imageView) {
        LoadImageTask task = getBitmapDownloaderTask(imageView);
        if (task != null) {
            String key = pathToPicture + type;
            String bitKey = task.mPathToPicture + task.mType;
            if (!bitKey.equals(key)) {
                task.cancel(true);
            }
        }
    }

    private LoadImageTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadDrawable) {
                DownloadDrawable dd = (DownloadDrawable) drawable;
                return dd.getTask();
            }
        }

        return null;
    }
}
