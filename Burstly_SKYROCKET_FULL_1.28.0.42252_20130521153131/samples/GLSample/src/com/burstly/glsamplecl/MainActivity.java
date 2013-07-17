package com.burstly.glsamplecl;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.burstly.lib.conveniencelayer.Burstly;
import com.burstly.lib.conveniencelayer.BurstlyActivity;
import com.burstly.lib.conveniencelayer.BurstlyAnimatedBanner;
import com.burstly.lib.conveniencelayer.BurstlyBaseAd;
import com.burstly.lib.conveniencelayer.BurstlyInterstitial;
import com.burstly.lib.conveniencelayer.BurstlyListenerAdapter;
import com.burstly.lib.conveniencelayer.IBurstlyListener;
import com.burstly.lib.conveniencelayer.events.AdDismissFullscreenEvent;
import com.burstly.lib.conveniencelayer.events.AdFailEvent;
import com.burstly.lib.conveniencelayer.events.AdPresentFullscreenEvent;
import com.burstly.ponglib.BurstlyPong;
import com.burstly.ponglib.IPongListener;

/**
 * This advanced Burstly sample shows some more advanced concepts including: working with a
 * multi-threaded Open GL based game,
 * animated banners, epandables, and additional listener methods
 */
public class MainActivity extends BurstlyActivity implements IPongListener {
    /**
     * The applications id
     */
    private static final String APP_ID = "Js_mugok3kCBg8ABoJj_Cg";
    
    /**
     * The GLSurfaceView and GLGame controlling the game's logic and rendering
     */
    protected BurstlyPong mBurstlyPong;

    /**
     * BurstlyAnimatedBanner instance used for showing banners
     */
    protected BurstlyAnimatedBanner mBanner;

    /**
     * BurstlyInterstitial instance used for showing interstitials
     */
    protected BurstlyInterstitial mInterstitial;

    /**
     * Total points scored
     */
    protected int mPointsScored;
   
    protected IBurstlyListener mListener = new BurstlyListenerAdapter() {
    	
        /**
         * In the case of an expandable being expanded to fullscreen then pause the game.
         */
        @Override
        public void onPresentFullscreen(final BurstlyBaseAd ad, final AdPresentFullscreenEvent event) {
        	mBurstlyPong.setPaused(true);
        }

        /**
         * In the case of an expandable being collapsed then unpause the game
         */
        @Override
        public void onDismissFullscreen(final BurstlyBaseAd ad, final AdDismissFullscreenEvent event) {
			mBurstlyPong.setPaused(false);
			mBurstlyPong.nextRound();
        }

        /**
         * In the case of an interstitial failing to load, then unpause gameplay otherwise the game
         * will hang.
         */
        @Override
        public void onFail(final BurstlyBaseAd ad, final AdFailEvent event) {
        	if (ad != mBanner && !event.wasFailureResultOfCachingAttempt()) {
            	mBurstlyPong.setPaused(false);
            }
        }
    };

    /**
     * In our main activity we need to call Burstly.init which we do here. In this example all ads
     * are added via code without
     * a layout file.
     */
    @Override
    public void onCreate(final Bundle oSavedInstanceState) {
        super.onCreate(oSavedInstanceState);

        Burstly.init(this, APP_ID);
        
        // turn off logging so it doesn't slow our frame rate
        Burstly.setLoggingEnabled(false);

        // Remove the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create the game logic
        mBurstlyPong = new BurstlyPong(this, this);
        
        // Create the layout and the
        final RelativeLayout layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        setContentView(layout);
        layout.addView(mBurstlyPong);

        // load and init animations
        final Animation inAnim = AnimationUtils.loadAnimation(this, R.anim.show_ad);
        inAnim.setFillEnabled(true);

        final Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.hide_ad);
        outAnim.setFillEnabled(true);

        mBanner = new BurstlyAnimatedBanner(this, layout, layoutParams, "0959195979157244033", "InGameBanner", 30,
                false);
        
        mBanner.addBurstlyListener(mListener);
        mBanner.setAnims(inAnim, outAnim);

        mInterstitial = new BurstlyInterstitial(this, "0656195979157244033", "InGameInterstitial", true);
        mInterstitial.addBurstlyListener(mListener);
    }

    /**
     * When the activity is paused pause the game loop
     */
    @Override
    public void onPause() {
        super.onPause();
        mBurstlyPong.onPause();
    }

    /**
     * When the activity is resumed resume the game loop
     */
    @Override
    public void onResume() {
        super.onResume();
        mBurstlyPong.onResume();
    }

    /**
     * called from the GL thread when a point is scored
     * 
     * @return true if an gameplay should be paused
     */
    @Override
	public boolean pointScored(final int winner, final int hits) {
    	mPointsScored++;
	
    	if (mPointsScored % 20 == 0) {
			hideBanner();

			if (mInterstitial.hasCachedAd()) {
				showInterstitial();
				return true;
			}
		} else if (mPointsScored % 10 == 0) {
			showBanner();
		}
		return false;
	}

    /**
     * Make call to show banner from the ui thread
     */
    private void showBanner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBanner.showAd();
            }
        });
    }

    /**
     * Make call to hide the banner from the ui thread
     */
    private void hideBanner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBanner.hideAd();
            }
        });
    }

    /**
     * Make a call to show an interstitial from the ui thread
     */
    private void showInterstitial() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInterstitial.showAd();
            }
        });
    }
}