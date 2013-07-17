package com.burstly.samplecl;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.burstly.lib.conveniencelayer.Burstly;
import com.burstly.lib.conveniencelayer.BurstlyActivity;
import com.burstly.lib.conveniencelayer.BurstlyBanner;
import com.burstly.lib.conveniencelayer.BurstlyBaseAd;
import com.burstly.lib.conveniencelayer.BurstlyInterstitial;
import com.burstly.lib.conveniencelayer.IBurstlyListener;
import com.burstly.lib.conveniencelayer.events.AdCacheEvent;
import com.burstly.lib.conveniencelayer.events.AdClickEvent;
import com.burstly.lib.conveniencelayer.events.AdDismissFullscreenEvent;
import com.burstly.lib.conveniencelayer.events.AdFailEvent;
import com.burstly.lib.conveniencelayer.events.AdHideEvent;
import com.burstly.lib.conveniencelayer.events.AdPresentFullscreenEvent;
import com.burstly.lib.conveniencelayer.events.AdShowEvent;

/**
 * This basic sample shows how to add a simple banner and interstitial. The banner is anchored to the top of the screen
 * and the will auto rotate every 30 seconds. The interstitial is triggered when the button at the bottom of the screen
 * is clicked
 */
public class MainActivity extends BurstlyActivity implements View.OnClickListener {
    /**
     * The button used to trigger our interstitial
     */
    private Button mButton;

    /**
     * The BurstlyInterstitial instance used to show interstitials
     */
    private BurstlyInterstitial mInterstitial;

    /**
     * The BurstlyBanner instance used to show banners
     */
    private BurstlyBanner mBanner;

    /**
     * In our main activity we need to call Burstly.init which we do here (This call would not be made in sub
     * activities). In all activities where we are showing ads we will initialize the ad objects after the layout is
     * inflated (done via setContentView in this case).
     */
    @Override
    public void onCreate(final Bundle savedInstanceData) {
        super.onCreate(savedInstanceData);

        // Initialize Burstly with your app ID
        Burstly.init(this, "Js_mugok3kCBg8ABoJj_Cg");

        setContentView(R.layout.main);

        // Banner added to the layout via the main.xml layout file.
        mBanner = new BurstlyBanner(this, R.id.bannerview);
        // Attach a listener to receive ad callbacks
        mBanner.addBurstlyListener(new MyBurstlyListener());
        // Start showing the banner. The banner will automatically refresh at the interval you
        // specified in code or on Burstly.com (interval set on Burstly.com will override code)
        mBanner.showAd();

        // Create an interstitial with your zone ID and a view name pass in true or false for auto-caching
        mInterstitial = new BurstlyInterstitial(this, "0656195979157244033", "BurstlyInterstitial", true);
        // Attach a listener to receive ad callbacks
        mInterstitial.addBurstlyListener(new MyBurstlyListener());

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableButton();
    }

    @Override
    public void onClick(final View view) {
        // disable the button while ad is being shown
        mButton.setEnabled(false);
        mButton.setText(getString(R.string.retrieving_ad));
        // Show the ad
        mInterstitial.showAd();
    }

    private void showToast(final String message) {
        final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create a listener to do various things such as pause game play when ads are presented. This listener can be
     * shared by multiple ads (banners/interstitials), or you can create different listeners for each.
     */
    private class MyBurstlyListener implements IBurstlyListener {

        /**
         * When an interstitial fails to display, enable button again and show message
         *
         * @param ad    The ad which failed to display a creative when an attempt to show or cache an ad was made
         * @param event fail event data
         */
        @Override
        public void onFail(final BurstlyBaseAd ad, final AdFailEvent event) {
            if(ad == mInterstitial){
                if (event.wasRequestThrottled()) {
                    showToast("Request throttled. " + event.getMinTimeUntilNextRequest()
                            + " ms until next request can be made.");
                } else {
                    showToast("onFail. " + ad.getName() + " failed with the following networks "
                            + event.getFailedCreativesNetworks().toString());
                }
                enableButton();
            }
        }

        /**
         * Called by an interstitial being presented or a clicked banner that will present fullscreen. You should pause
         * your app. Note that some interstitials use views rather than activities, thus it is not safe to rely on 
         * onPause to be called
         * 
         * @param ad    The ad which took over the screen
         * @param event present fullscreen event data
         */
        @Override
        public void onPresentFullscreen(final BurstlyBaseAd ad, final AdPresentFullscreenEvent event) {
            //Pause your app!
        }

        /**
         * Unpause your app. When an interstitial is dismissed enable the show ad button. 
         *
         * @param ad    The ad which took over the screen
         * @param event dismiss fullscreen event data
         */
        @Override
        public void onDismissFullscreen(final BurstlyBaseAd ad, final AdDismissFullscreenEvent event) {
            //Unpause your app!
            enableButton();
            
            //You may want to request a new banner after it has been clicked
            if(ad == mBanner) mBanner.showAd();
        }

        @Override
        public void onCache(final BurstlyBaseAd ad, final AdCacheEvent event) {
            // show when ads are cached using auto-caching
            if(ad == mInterstitial) showToast("ad " + ad.getName() + " cached.");
        }

        // ------------------ unimplemented callbacks ------------------------
        @Override
        public void onHide(final BurstlyBaseAd ad, final AdHideEvent event) {
        }

        @Override
        public void onShow(final BurstlyBaseAd ad, final AdShowEvent event) {
        }

        @Override
        public void onClick(final BurstlyBaseAd ad, final AdClickEvent event) {
        }

    }

    private void enableButton() {
        mButton.setEnabled(true);
        mButton.setText(getString(R.string.show_ad));
    }
}
