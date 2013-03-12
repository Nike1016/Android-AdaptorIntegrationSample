/*******************************************************************************
 * Copyright 2011 App Media Group LLC.
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.meganet.network;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.meganet.utils.Logger;

/**
 * Asynchronous task for fetching a bitmap image from the specified url.
 */
public class BitmapFetchTask extends AsyncTask<String, Void, Boolean> {

    /**
     * Reference to {@link IBitmapFetchListener}.
     */
    private final IBitmapFetchListener mListener;

    /**
     * Url of the bitmap to be fetched.
     */
    private String mBitmapUrl;

    /**
     * Reference to the fetched bitmap.
     */
    private Bitmap mFetchedBitmap;

    /**
     * Scale factor.
     */
    private float mScale = 1f;

    /**
     * Constructs a new {@link BitmapFetchTask} instance.
     * 
     * @param listener
     *            will return the task results (including the bitmap in a case of success).
     */
    public BitmapFetchTask(final IBitmapFetchListener listener) {
        if (listener == null) {
            Logger.logWarning(this, "IBitmapFetchListener should not be null");
        }
        mListener = listener;
    }

    @Override
    protected Boolean doInBackground(final String... bitmapUrl) {
        if (bitmapUrl.length < 1 || bitmapUrl[0] == null) {
            return false;
        }
        mBitmapUrl = bitmapUrl[0];
        mFetchedBitmap = scaleBitmap(fetchBitmap(mBitmapUrl));
        if (mFetchedBitmap == null) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (mListener == null) {
            return;
        }
        if (result) {
            mListener.imageLoaded(mFetchedBitmap);
        } else {
            mListener.imageFailedToLoad();
        }
    }

    /**
     * Sets bitmap scale factor.
     */
    public void setScale(final float scale) {
        mScale = scale;
    }

    /**
     * Scales bitmap if nesseccary.
     */
    private Bitmap scaleBitmap(final Bitmap bitmap) {
        if (bitmap == null || mScale == 1f) {
            return bitmap;
        }
        final int scaledWidth = (int)(mScale * bitmap.getWidth());
        final int scaledHeight = (int)(mScale * bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }

    /**
     * Fetches bitmap from the specified url.
     */
    private Bitmap fetchBitmap(final String bitmapUrl) {
        try {
            final URL url = new URL(bitmapUrl.replace(" ", "%20"));
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            connection.connect();
            final FlushedInputStream fis = new FlushedInputStream(connection.getInputStream());
            final Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            return bitmap;
        }
        catch (MalformedURLException e) {
            Logger.logError(this, "URL is malformed: " + e.getMessage());
        }
        catch (IOException e) {
            Logger.logError(this, "IO exception during fetching bitmap: " + e.getMessage());
        }
        return null;
    }

    /**
     * Class with improvement of slow connection bug for {@link FilterInputStream} (<a
     * href="http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html"
     * >read here</a>).
     */
    private static class FlushedInputStream extends FilterInputStream {

        /**
         * Constructor.
         * 
         * @param inputStream {@link InputStream} input
         */
        FlushedInputStream(final InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(final long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int byteRed = read();
                    if (byteRed < 0) {
                        break; // we reached EOF
                    }
                    bytesSkipped = 1; // we read one byte
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
