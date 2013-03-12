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
package com.meganet.integration;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.meganet.network.BitmapFetchTask;
import com.meganet.network.IBitmapFetchListener;
import com.meganet.utils.Logger;

import com.burstly.lib.component.IBurstlyAdaptor;
import com.burstly.lib.component.IBurstlyAdaptorListener;
import com.burstly.lib.ui.BurstlyView;

/**
 * Integration example of a custom ad network.
 * <p>
 * Simple image loading for the specified url is used as an example here. The image url is one of
 * the parameters that comes from server. Then the image will be fetched with the help of
 * {@link BitmapFetchTask} and displayed as a banner.
 */
public class MeganetAdaptor implements IBurstlyAdaptor {

    /**
     * Interstitial image view.
     */
    static ImageView sInterstitialView;

    /**
     * Flag that shiws
     */
    static boolean sIsShowingNow;

    /**
     * Currents context. Strong reference is safe, because adaptors are being destroyed on
     * {@link BurstlyView#destroy()}.
     */
    Context mContext;

    /**
     * Adaptor name received from server.
     */
    private String mAdaptorName;

    /**
     * Listener used to inform BurstlySDK about the state of the integrated library.
     */
    IBurstlyAdaptorListener mAdaptorListener;

    /**
     * Specifies whether the {@link MeganetAdaptor#destroy()} method was called.
     */
    private boolean mIsDestroyed;

    /**
     * Specifies whether current ad is an interstitial ad.
     */
    boolean mIsInterstitial;

    /**
     * Specifies whether current ad is in precache mode. We should care only about interstitial
     * precaching, because banner precaching is implemented in Burstly SDK out-of-the-box.
     */
    boolean mIsPrecacheInterstitial;

    /**
     * URL of the image that should be fetched and displayed.
     */
    private String mImageUrl;

    /**
     * Image view that will display the fetched image.
     */
    ImageView mImageView;

    /**
     * Used to fetch the bitmap.
     */
    private BitmapFetchTask mTask;

    /**
     * Handles callbacks from the {@link BitmapFetchTask}.
     * <p>
     * {@link IBurstlyAdaptorListener#didLoad(String, boolean)} and
     * {@link IBurstlyAdaptorListener#failedToLoad(String, boolean, String)} should be called when
     * the ad loading process successed/failed.
     */
    private static class ImageFetchListener implements IBitmapFetchListener {

        /**
         * Weak reference to the {@link MeganetAdaptor}.
         */
        private final Reference<MeganetAdaptor> mAdaptor;

        /**
         * Flag marks this callback as interstitial.
         */
        private final boolean mIsInterstitial;

        /**
         * Constructs new instance.
         * 
         * @param isInterstitial boolean interstitial flag
         */
        ImageFetchListener(final MeganetAdaptor adaptor, final boolean isInterstitial) {
            mAdaptor = new WeakReference<MeganetAdaptor>(adaptor);
            mIsInterstitial = isInterstitial;
        }

        @Override
        public void imageLoaded(final Bitmap fetchedBitmap) {
            final MeganetAdaptor adaptor = mAdaptor.get();
            if (adaptor == null || adaptor.isDestroyed()) {
                return;
            }

            final ImageView image = adaptor.mImageView;
            image.setAdjustViewBounds(true);
            image.setImageBitmap(fetchedBitmap);

            if (mIsInterstitial && !adaptor.mIsPrecacheInterstitial) {
                startInterstitial(adaptor);
            } else {
                Logger.logInfo(this, "Loaded ad.");
                // notify Burstly sdk about a successfully loaded ad
                adaptor.mAdaptorListener.didLoad(adaptor.getNetworkName(), mIsInterstitial);
            }

        }

        @Override
        public void imageFailedToLoad() {
            final MeganetAdaptor adaptor = mAdaptor.get();
            if (adaptor == null || adaptor.isDestroyed()) {
                return;
            }

            Logger.logInfo(this, "Image was not fetched. See log for details.");
            // notify Burstly sdk that ad has been failed to load
            adaptor.mAdaptorListener.failedToLoad(adaptor.getNetworkName(), false, "");
        }

    }

    /**
     * Handles click callback from the ad ImageView.
     * <p>
     * {@link IBurstlyAdaptorListener#adWasClicked(String, boolean)} should be called when the ad
     * clicked.
     */
    private static class ImageClickListener implements View.OnClickListener {

        /**
         * Weak reference to the {@link MeganetAdaptor}.
         */
        private final Reference<MeganetAdaptor> mAdaptor;

        /**
         * Flag marks this callback as interstitial.
         */
        private final boolean mIsInterstitial;

        /**
         * Constructs new instance.
         */
        ImageClickListener(final MeganetAdaptor adaptor, final boolean isInterstitial) {
            mAdaptor = new WeakReference<MeganetAdaptor>(adaptor);
            mIsInterstitial = isInterstitial;
        }

        @Override
        public void onClick(final View arg0) {
            final MeganetAdaptor adaptor = mAdaptor.get();
            if (adaptor == null || adaptor.isDestroyed()) {
                return;
            }

            // handle a click event. For example open a site.
            adaptor.mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://burstly.com")));

            Logger.logInfo(this, "Banner was clicked.");
            // notify Burstly sdk about 'ad click' event
            adaptor.mAdaptorListener.adWasClicked(adaptor.mAdaptorName, mIsInterstitial);
        }

    }

    /**
     * Constructs a new {@link MeganetAdaptor} instance.
     */
    public MeganetAdaptor(final Context context, final String viewId, final String adaptorName) {
        mContext = context;
        mAdaptorName = adaptorName;
        Logger.logDebug(this, "Burstly view id: " + viewId);
    }

    @Override
    public void startTransaction(final Map<String, ?> paramsFromServer) throws IllegalArgumentException {
        if (paramsFromServer == null) {
            throw new IllegalArgumentException("Parameters from server cannot be null.");
        }
        checkParameters(paramsFromServer);
        Logger.logDebug(this, "Transaction started with parameters from server: " + paramsFromServer.toString());
    }

    /**
     * Start showing interstitial ad.
     * 
     * @param adaptor {@link MeganetAdaptor} current adaptor instance
     */
    static void startInterstitial(final MeganetAdaptor adaptor) {
        String cause = "Interstitial could not be shown because one is showing now.";
        if (!sIsShowingNow) {
            try {
                final Context context = adaptor.mContext;
                context.startActivity(new Intent(context, MeganetInterstitialActivity.class));
                Logger.logInfo(adaptor, "Started interstitial ad.");
                sInterstitialView = adaptor.mImageView;
                sIsShowingNow = true;
                // notify Burstly sdk about a successfully loaded ad
                adaptor.mAdaptorListener.didLoad(adaptor.getNetworkName(), true);
                return;
            }
            catch (final ActivityNotFoundException anfe) {
                sInterstitialView = null;
                sIsShowingNow = false;
                cause = anfe.getMessage();
                Logger
                    .logWarning(adaptor,
                        "Failed to start interstitial ad. Have you add com.meganet.integration.MeganetInterstitialActivity in manifest ?");
            }
        }
        // notify Burstly sdk about fail
        adaptor.mAdaptorListener.failedToLoad(adaptor.getNetworkName(), true, cause);
    }

    @Override
    public void endTransaction(final TransactionCode endCode) {
        Logger.logDebug(this, "Transaction ended with code: " + endCode.name());
    }

    @Override
    public void startViewSession() {
        Logger.logDebug(this, "View session started.");
    }

    @Override
    public void endViewSession() {
        Logger.logDebug(this, "View session ended.");
    }

    @Override
    public void destroy() {
        sInterstitialView = null;
        mIsDestroyed = true;
        mIsInterstitial = false;
        mContext = null;
        mImageView = null;
        Logger.logDebug(this, "Adaptor destroyed.");
    }

    /**
     * Specifies whether the adaptor is marked as destroyed.
     */
    boolean isDestroyed() {
        return mIsDestroyed;
    }

    @Override
    public View getNewAd() {
        // not in precache mode
        mIsPrecacheInterstitial = false;
        // create a new ad impl and request for image asynchronously
        mImageView = new ImageView(mContext);
        mImageView.setOnClickListener(new ImageClickListener(this, mIsInterstitial));
        mTask = new BitmapFetchTask(new ImageFetchListener(this, mIsInterstitial));
        mTask.setScale(mContext.getResources().getDisplayMetrics().density);
        mTask.execute(mImageUrl);

        if (!mIsInterstitial) {
            return mImageView;
        }
        return null;
    }

    @Override
    public View precacheAd() {
        // default implementation for banner is the same as for getNewAd, except the fact
        // that after success callback banner will not be shown until next BurstlyView
        // sendRequestForAd(). This behaviour is offered out-of-the box and you should not bother
        // about it.
        return getNewAd();
    }

    /**
     * Is set by Burstly sdk.
     */
    @Override
    public void setAdaptorListener(final IBurstlyAdaptorListener listener) {
        if (listener == null) {
            Logger.logWarning(this, "IBurstlyAdaptorListener should not be null.");
        }
        mAdaptorListener = listener;
    }

    @Override
    public String getNetworkName() {
        return mAdaptorName;
    }

    @Override
    public BurstlyAdType getAdType() {
        // if your implementation supports interstitials you could use construction like that
        return mIsInterstitial ? BurstlyAdType.INTERSTITIAL_AD_TYPE : BurstlyAdType.BANNER_AD_TYPE;
    }

    @Override
    public boolean supports(final String action) {
        // If you want to support interstitial ad precaching, implement precacheInterstitialAd()
        // and showPrecachedInterstitialAd() methods.
        // here we return true because based on getAdType() method we always get
        // BurstlyAdType.BANNER_AD_TYPE and because we support any type of action with banner ads
        // (precache or not) we can safely return true always.
        // if implementation supports interstitials and DOES NOT support interstitial precaching you
        // could implement this method as follows:
        // return !(action.equals(AdaptorAction.PRECACHE_INTERSTITIAL.getCode()) &&
        // mIsInterstitial); where mIsInterstitial would be a flag that means that current ad is
        // interstitial ad
        return true;
    }

    @Override
    public void precacheInterstitialAd() {
        // call for getNewAd(); because in this adaptor implementation the logic of getting banner
        // ads and interstitilial ads is the same.
        getNewAd();
        // in precache mode
        mIsPrecacheInterstitial = true;
    }

    @Override
    public void showPrecachedInterstitialAd() {
        if (mImageView != null) {
            startInterstitial(this);
        } else {
            mAdaptorListener.failedToLoad(mAdaptorName, true, "No precached ad.");
        }
    }

    @Override
    public void pause() {
        // is called from BurstlyView onHideActivity
        // nothing to do here in this example
    }

    protected void checkParameters(final Map<String, ?> paramsFromServer) throws IllegalArgumentException {
        final String interstitial = (String)paramsFromServer.get("isInterstitial");
        mIsInterstitial = interstitial != null && interstitial.equalsIgnoreCase("YES");

        mImageUrl = (String)paramsFromServer.get("imageUrl");
        if (mImageUrl == null) {
            throw new IllegalArgumentException("Parameter 'imageUrl' can not be null.");
        }

    }

    @Override
    public void resume() {
        // is called from BurstlyView onShowActivity
        // nothing to do here in this example
    }

    @Override
    public void stop() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

}