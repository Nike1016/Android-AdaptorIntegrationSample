package com.burstly.samplecl;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.burstly.conveniencelayer.*;
import com.burstly.conveniencelayer.events.*;

/**
 * This basic sample shows how to add a simple banner and interstitial.
 *
 * The banner is anchored to the top of the screen and the will auto rotate every 30 seconds.
 * The interstitial is triggered when the button at the bottom of the screen is clicked
 */
public class MainActivity extends BurstlyActivity implements View.OnClickListener{
    /**
     * The button used to trigger our interstitial
     */
    private Button mButton;

    /**
     * The BurstlyInterstitial instance used to show interstitials
     */
    private BurstlyInterstitial mInterstitial;

    /**
     * In our main activity we need to call Burstly.init which we do here (This call would not be made in sub activities).
     *
     * In all activities where we are showing ads we will initialize the ad objects after the layout is inflated (done via
     * setContentView in this case).
     */
    @Override
    public void onCreate(Bundle savedInstanceData) {
        super.onCreate(savedInstanceData);

        //Initialize Burstly with your app ID
        Burstly.init(this, "Js_mugok3kCBg8ABoJj_Cg");

        setContentView(R.layout.main);

        //Banner added to the layout via the main.xml layout file.
        final BurstlyBanner banner = new BurstlyBanner(this, R.id.bannerview);
        //Start showing the banner. The banner will automatically refresh at the interval you specified in code or
        //on Burstly.com (interval set on Burstly.com will override code)
        banner.showAd();

        //Create an interstitial with your zone ID and a view name pass in true or false for auto-caching
        mInterstitial = new BurstlyInterstitial(this, "0656195979157244033", "MainMenuInterstitial", true);
        //Attach a listener to receive ad callbacks
        mInterstitial.addBurstlyListener(new MyBurstlyInterstitialListener());

        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableButton();
    }

    @Override
    public void onClick(View view) {
        //disable the button while ad is being shown
        mButton.setEnabled(false);
        mButton.setText(getString(R.string.retrieving_ad));
        //Show the ad
        mInterstitial.showAd();
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create a listener for the interstitial to enable launch button when onFail and onDismissFullscreen callbacks
     * are called
     */
    private class MyBurstlyInterstitialListener implements IBurstlyListener {

        /**
         * When an interstitial fails to display, enable button again and show message
         *
         * @param ad    The ad which failed to display a creative when an attempt to show or cache an ad was made
         * @param event fail event data
         */
        @Override
        public void onFail(BurstlyBaseAd ad, AdFailEvent event) {
            if(event.wasRequestThrottled()){
                showToast("Request throttled. " + event.getMinTimeUntilNextRequest() + " ms until next request can be made.");
            } else {
                showToast("onFail. " + ad.getName() + " failed with the following networks " + event.getFailedCreativesNetworks().toString());
            }
            enableButton();
        }

        /**
         * When an interstitial is dismissed enable the show ad button
         *
         * @param ad    The ad which took over the screen
         * @param event dismiss fullscreen event data
         */
        @Override
        public void onDismissFullscreen(BurstlyBaseAd ad, AdDismissFullscreenEvent event) {
            enableButton();
        }

        @Override
        public void onCache(BurstlyBaseAd ad, AdCacheEvent event) {
            //show when ads are cached using auto-caching
            showToast("ad " + ad.getName() + " cached.");
        }

        // ------------------ unimplemented callbacks ------------------------
        @Override
        public void onHide(BurstlyBaseAd ad, AdHideEvent event) {}

        @Override
        public void onShow(BurstlyBaseAd ad, AdShowEvent event) {}

        @Override
        public void onClick(BurstlyBaseAd ad, AdClickEvent event) {}

        @Override
        public void onPresentFullscreen(BurstlyBaseAd ad, AdPresentFullscreenEvent event) {}


    }

    private void enableButton() {
        mButton.setEnabled(true);
        mButton.setText(getString(R.string.show_ad));
    }
}
