package com.burstly.rewardssamplecl;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.burstly.conveniencelayer.*;
import com.burstly.conveniencelayer.events.AdDismissFullscreenEvent;
import com.burstly.conveniencelayer.events.AdFailEvent;
import com.burstly.conveniencelayer.events.AdPresentFullscreenEvent;
import com.burstly.lib.currency.BalanceUpdateInfo;
import com.burstly.lib.currency.CurrencyManager;
import com.burstly.lib.feature.currency.ICurrencyListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Burstly rewards shows how to display an offerwall and manage currency.
 */
public class MainActivity extends BurstlyActivity implements View.OnClickListener, ICurrencyListener{
    private static final String TAG = "RewardsSample";

    /**
     * The App ID used by the sample.  You will use your own App ID
     */
    private static final String APP_ID = "Js_mugok3kCBg8ABoJj_Cg";

    /**
     * The currency type that is configured for this specific app/zone
     */
    private static final String CURRENCY_TYPE = "coins";

    /**
     * The Zone ID for the OFFERWALL within this app.  You will use your own zone ID
     */
    private static final String OFFERWALL_ZONE_ID = "0954195379157264033";

	/**
     * Button used to trigger an interstitial
     */
    private Button mWallButton;

    /**
     * Button used to subtract currency
     */
    private Button mSubtractButton;

    /**
     * Text to display currency value
     */
    private TextView mCurrencyTextView;

    /**
     * The interstitial class which we initialize and use to interact with interstitials
     */
    private BurstlyInterstitial mWallInterstitial;

    /**
     * Currency manager
     */
    private CurrencyManager mCurrencyManager;

    /**
     * Our listener which receives all event callbacks related to the interstitial
     */
    BurstlyListenerAdapter mBurstlyListener = new BurstlyListenerAdapter() {
        /**
         * Timer used to wait before checking for balance
         */
        private Timer timer = new Timer();

        /**
         * In the case that our attempt to show an offerwall fails we need to dismiss the progress dialog
         *
         * @param ad The ad which failed to display a creative when an attempt to show or precache an ad was made
         * @param event fail event data
         */
        @Override
        public void onFail(final BurstlyBaseAd ad, final AdFailEvent event) {
            dismissProgressDialog();
        }

        /**
         * Dismiss the progress dialog is there is one
         *
         * @param ad    The ad which took over the screen
         * @param event present fullscreen event data
         */
        @Override
        public void onPresentFullscreen(final BurstlyBaseAd ad, final AdPresentFullscreenEvent event){
            dismissProgressDialog();
        }

        /**
         * Check for currency updates when full screen is dismissed
         *
         * @param ad    The ad which took over the screen
         * @param event dismiss fullscreen event data
         */
        @Override
        public void onDismissFullscreen(final BurstlyBaseAd ad, final AdDismissFullscreenEvent event){
            //Often times it will take a few seconds for the reward to be processed, so we will wait 5 seconds then check for an update
            int seconds = 5;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkForUpdatedBalance();
                }
            }, seconds*1000);
        }
    };

    /**
     * Progress dialog that is shown while offerwall is loaded
     */
    private ProgressDialog mProgressDialog;

    /**
     * In our main activity we need to call Burstly.init which we do here.  In this example all ads are added via code without
     * a layout file.
     */
    @Override
    public void onCreate(Bundle savedData) {
        super.onCreate(savedData);
        Burstly.init(this, APP_ID);

        setContentView(R.layout.main);

		//Banner added to the layout via the main.xml layout file.
		final BurstlyBanner banner = new BurstlyBanner(this, R.id.bannerview);
        banner.addBurstlyListener(mBurstlyListener);
		banner.showAd();

        mCurrencyTextView = (TextView)findViewById(R.id.currencyText);

        //get instance to currency manager
        mCurrencyManager = Burstly.getCurrencyManager();

        initWallButton(this);
        initSubButton();
        initAddButton();
        initRefreshButton();

        //set balance after everything has been initialized
        setBalance(mCurrencyManager.getBalance(CURRENCY_TYPE));

        mWallInterstitial = new BurstlyInterstitial(this, OFFERWALL_ZONE_ID, "Interstitial", false);
        mWallInterstitial.addBurstlyListener(mBurstlyListener);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mCurrencyManager.addCurrencyListener(this);
        checkForUpdatedBalance();
        //dismiss progress dialog if one exists on app resume
        dismissProgressDialog();
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        mCurrencyManager.removeCurrencyListener(this);
    }

    /**
     * Our add button calls CurrencyManager.increaseBalance
     */
    private void initWallButton(final Context context) {
        mWallButton = (Button)findViewById(R.id.wallButton);
        mWallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Show progress dialog to indicate to the user that offer wall is being loaded
                // Offer walls can take some time to load, and caching is not fully supported.
                mProgressDialog = ProgressDialog.show(context, "Working..", "Finding Offers");
                mWallInterstitial.showAd();
            }
        });
    }

    /**
     * Our add button calls CurrencyManager.increaseBalance
     */
    private void initAddButton() {
        Button button = (Button)findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCurrencyManager.increaseBalance(5, CURRENCY_TYPE);
            }
        });
    }

    /**
     * Our subtract button calls CurrencyManager.decreaseBalance
     */
    private void initSubButton() {
        mSubtractButton = (Button)findViewById(R.id.subtractButton);
        mSubtractButton.setEnabled(mCurrencyManager.getBalance(CURRENCY_TYPE) >= 10);
        mSubtractButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCurrencyManager.decreaseBalance(10, CURRENCY_TYPE);
            }
        });
    }

    /**
     * Our refresh button calls checkForUpdatedBalance which calls CurrencyManager.checkForUpdate and logs errors
     */
    private void initRefreshButton() {
        Button button = (Button)findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                checkForUpdatedBalance();
            }
        });
    }

    /**
     * Check for updated balance when application is resumed
     */
    private void checkForUpdatedBalance() {
        try {
            mCurrencyManager.checkForUpdate();
        } catch (Exception e) {
            Log.e(TAG, "Exception while checking for updated balance" + e.toString());
        }
    }

    /**
     * Click event to launch offerwall. Show a progress dialog while offerwall is loading.
     * @param view
     */
    @Override
    public void onClick(View view) {
        // Show progress dialog to indicate to the user that offer wall is being loaded
        mProgressDialog = ProgressDialog.show(this, "Working..", "Finding Offers");
        mWallInterstitial.showAd();
    }

    /**
     * Dismiss the Progress Dialog (if one exists) on the UI thread
     */
    private void dismissProgressDialog(){
        if(mProgressDialog != null){
            runOnUiThread(new Runnable() {
                public void run() {
                    mProgressDialog.dismiss();
                }
            });
        }
    }


    /**
     * Received update balance event (This will happen on a background thread)
     *
     * @param balanceUpdateMap contains a map keyed on currency type. Use get(CURRENCY_TYPE) to access balanceUpdateInfo
     *                         for your currency type
     */
    @Override
    public void didUpdateBalance(final Map<String, BalanceUpdateInfo> balanceUpdateMap) {
        // balanceUpdateMap will be empty if no currency has been awarded, so best to check that key exists
        String message = "didUpdateBalance. Currency change: " + (balanceUpdateMap.containsKey(CURRENCY_TYPE) ? balanceUpdateMap.get(CURRENCY_TYPE).getChange() : 0)  + " " + CURRENCY_TYPE;
        Log.d(TAG, message);
        showMessage(message);
        // Set new balance.
        setBalance(mCurrencyManager.getBalance(CURRENCY_TYPE));
    }

    /**
     * Failed to update balance (This will happen on a background thread)
     *
     * @param balanceUpdateMap contains a map keyed on currency type. Use get(CURRENCY_TYPE) to access balanceUpdateInfo
     *                         for your currency type
     */
    @Override
    public void didFailToUpdateBalance(final Map<String, BalanceUpdateInfo> balanceUpdateMap) {
        showMessage("didFailToUpdateBalance. Click refresh to check balance again.");
    }

    public void showMessage(final String message){
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Set balance text. This must be done from the UI thread
     *
     * @param currentBalance
     */
    private void setBalance(final int currentBalance) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Updating balance to: " + currentBalance);
                mCurrencyTextView.setText(CURRENCY_TYPE + ": " + currentBalance);
                mSubtractButton.setEnabled(currentBalance >= 10);
            }
        });
    }
}
