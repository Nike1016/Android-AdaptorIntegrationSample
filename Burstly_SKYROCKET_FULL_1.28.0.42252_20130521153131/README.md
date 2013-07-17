Android Guide
====================

<!--
The latest version of this README can be viewed online at http://quickstart.skyrocketapp.com/android-guide
-->

##Introduction

Burstly allows you to integrate a single SDK into your app, and serve ads from many of the top 3rd party ad providers as well as traffic cross promotional and directly sold campaigns. The following will take through the steps to integrate the SDK into your app. 

##SDK Integration

Integrating the Burstly SDK into your application is as easy as adding the Burstly_SkyRocket_SDK_XXXXX.jar, and android-support-v4.jar to you projects libs directory.  Once the jar files have been added you may need to add a dependency to your project depending on your build environment. 

##The Manifest

Before any ad activity can be performed it's important to setup your AndroidManifest.xml file with the necessary activities and permissions. Failure to add the required entries will result in the Burstly SDK throwing a RuntimeException when you try to initialize the system.

**Note: You must set targetSdkVersion to 16 or less for ads that have javascript calling native methods to work on 4.2+ devices.** This issue is as a result of Android's *@JavascriptInterface* annotation requirements, as described in the android release notes [here](http://developer.android.com/about/versions/android-4.2.html). We are working on a fix for this in our next release.

    android:targetSdkVersion="16"

###Required Activities

The following manifest activity entries are required if you are using the Burstly SDK with 3rd party SDKs. These activity entries allow interstitials to be launched in their own activity and are required. Please copy the section below and paste it into your project's AndroidManifest.xml file within just before the closing application tag. If you are using the Burstly Only SDK, only the Burstly Activity is required. 

	<!-- Begin Burstly Required Activities -->
	
	<!-- Burstly ================================================= -->
	<activity android:name="com.burstly.lib.component.networkcomponent.burstly.BurstlyFullscreenActivity"
	        android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
	        android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
	
	<!-- Admob =================================================== -->
	<activity android:name="com.google.ads.AdActivity"
	        android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize" />
	
	<!-- Greystripe ============================================== -->
	<activity android:name="com.greystripe.sdk.GSFullscreenActivity"
	        android:configChanges="keyboard|keyboardHidden|orientation"  />
	
	<!-- Inmobi ================================================== -->
	<activity android:name="com.inmobi.androidsdk.IMBrowserActivity"
	        android:configChanges="keyboardHidden|orientation|keyboard|screenSize|smallestScreenSize" />
	
	<!-- Millennial =============================================== -->
	<activity android:name="com.millennialmedia.android.MMActivity"
	        android:configChanges="keyboardHidden|orientation|keyboard"
	        android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
	<activity android:name="com.millennialmedia.android.VideoPlayer"
	        android:configChanges="keyboardHidden|orientation|keyboard"
	        android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
  
	<!-- Jumptap ================================================== -->
    <activity
            android:name="com.burstly.lib.component.networkcomponent.jumptap.JumptapActivity"
            android:configChanges="keyboard|keyboardHidden|orientation"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen"/>
            
	<!-- End Burstly Required Activities -->

###Required Permissions

Burstly requires that all Android applications on the platform supply the *INTERNET, ACCESS_NETWORK_STATE, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE*, and *ACCESS_WIFI_STATE* permissions. These permissions are used by each of the integrated SDKs to retrieve and target ads.

Please copy the permission entries below and paste them into your AndroidManifest.xml file just before the closing manifest tag.

	<!-- Burstly Required Permissions -->
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

###Optional Permissions

These optional permissions, if supplied, will allow ad networks to retrieve more accurate geo targeting info. These two permissions may affect your applications battery consumption and you should refer to the Android documentation when deciding which, if any, of these optional parameters you want to require.

If you wish to add either of these permissions please copy and paste the corresponding manifest permission entries from below, into your AndroidManifest.xml file just after the required permissions.

	<!-- Burstly Optional permissions -->
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

A sample manifest with the required permissions can be viewed in any of our samples.

##Integration Mode

Burstly integration mode allows you to test your Burstly integration without configuring and managing zones through SkyRocketApp.com. It also allows you to verify that all 3rd party networks which are included are working properly. Keep in mind that enabling integration mode for an ad will disregard the app/zone IDs that you have specified in code.

1. In order to use integration mode you will need to call

		Burstly.enableIntegrationMode(deviceIDs);
passing it the list of deviceIDs which should be placed in integration mode.  Alternatively you can pass in *null* which places all devices in integration mode.  During integration mode you will receive a warning to notify you that you are using integration mode.  **You MUST disable integration mode before going live otherwise you will need to resubmit your application in order to serve live ads.**

2. Once you have enabled integration mode you will also need to set which network you want to test by calling

		Burstly.setIntegrationNetwork(BurstlyIntegrationModeAdNetworks.ADMOB);
The network which is set will be used by all subsequent ad placements until the value is changed.  The available values are: *DISABLED, HOUSE, MILLENNIAL, ADMOB, GREYSTRIPE, INMOBI, JUMPTAP, RICHMEDIA, REWARDS*.

##Initializing the System

Before using Burstly the system needs to be initialized. This should occur before:
	
- Any layouts containing a Banner are inflated
- Any Burstly classes are instantiated
- Any other Burstly static functions are called

We recommend that you put the initialization code immediately following the call to super.onCreate() in your application class (if your app has one) or your default activity (this call should not appear in subsequent activities launched by your application). You will need to pass your App ID into the init call.

	Burstly.init(this, YOUR_APP_ID);

##Lifecycle Events
All activities or fragments which show Burstly ads need to notify Burstly of events related to their lifecycle. If the Activity or Fragment is a direct Subclass of Android's android.app.Activity or android.support.v4.app.Fragment classes then this can be done by changing your activity's parent class to BurstlyActivity or your fragments parent class to BurstlyFragment.

If it is not possible to subclass BurstlyActivity / BurstlyFragment then you will need to add calls to Burstly when onPause, onResume, and onDestroy events occur.

Example of Subclassing BurstlyActivity:

	public class MyActivity extends BurstlyActivity {

Example of Subclassing BurstlyFragment:

	public class MyFragment extends BurstlyFragment {

Example of manually passing Activity events from Activity methods:

	@Override
	public void onResume() {
	    Burstly.onResumeActivity(this);
	    super.onResume();
	    ...
	}

	@Override
	public void onPause() {
	    Burstly.onPauseActivity(this);
	    super.onPause();
	    ...
	}

	@Override
	public void onDestroy() {
	    Burstly.onDestroyActivity(this);
	    super.onDestroy();
	    ...
	}

Example of manually passing Fragment events from Fragment methods:

	@Override
	public void onResume() {
	    Burstly.onResumeFragment(this);
	    super.onResume();
	    ...
	}

	@Override
	public void onPause() {
	    Burstly.onPauseFragment(this);
	    super.onPause();
	    ...
	}

	@Override
	public void onDestroyView() {
	    Burstly.onDestroyFragment(this);
	    super.onDestroyView();
	    ...
	}

##Display a Banner

Banners can be added and positioned using a layout file, or in code. 

###Adding the Banner Using a Layout File

In order to make full use of the layout file you will need to add the Burstly schema (xmlns:burstly="http://burstly.com/lib/ui/schema") to your root ViewGroup.  For example:

	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    	            xmlns:burstly="http://burstly.com/lib/ui/schema"
        	        android:layout_width="fill_parent"
            	    android:layout_height="fill_parent">

To add a banner add add the following tag to your layout file. Place it within a parent ViewGroup and position it as you would any other component being added using a layout file. The Zone Id, and View Name need to be filled in with the details from the zones you create in the SkyRocketApp.com UI.

	<com.burstly.lib.ui.BurstlyView
	    android:layout_width="wrap_content"
	    android:layout_height="wrap_content"
	    burstly:zoneId=YOUR_ZONE_ID
        burstly:burstlyViewId=YOUR_VIEW_NAME
        burstly:defaultSessionLife=REFRESH_RATE_SECONDS
	    android:id="@+id/bannerview"/>

In order to initialize this banner with the behavior of a static, autorefreshing banner, you must create a new BurstlyBanner passing it the id used in the layout file and then call showAd on the new instance.

This code should be added in your Activity's onCreate method after the call to setContentView:

	final BurstlyBanner banner = new BurstlyBanner(this, R.id.bannerview);
	banner.showAd();

###Adding the Banner in Code

To create a new BurstlyBanner and add it to a ViewGroup in code you must first have a reference to the ViewGroup you are adding to and a reference to a ViewGroup.LayoutParameters which will be used to place the banner inside the ViewGroup (null can be used for the layoutParameters). 

The Zone Id, and View name need to be filled in with the details from the zones you create in the SkyRocketApp.com UI. Once you have an instance of a BurstlyBanner call showAd in order to retrieve the first ad and start the refresh timer.

	final BurstlyBanner banner = new BurstlyBanner( this, viewGroup, layoutParams, YOUR_ZONE_ID, YOUR_VIEW_NAME, REFRESH_RATE_SECONDS);
	banner.showAd();

Once you call showAd to display a banner it will automatically refresh at the rate specified on the server or in your code, and you **do not need to call showAd to refresh the banner.**

###Toggling Banner Visibility

BurstlyAnimatedBanner is used to handle the use case of a banner which is not always visible. This type of banner can be hidden and shown. When hideAd and showAd are called they will use the animations provided in setAnims. If no animations are ever set then the banner will just pop on and off when hidden and shown.

	final BurstlyAnimatedBanner banner = new BurstlyAnimatedBanner( this, viewGroup, layoutParams, YOUR_ZONE_ID, YOUR_VIEW_NAME, REFRESH_RATE_SECONDS, AUTO_CACHE_BOOLEAN);
	banner.setAnims(inAnim, outAnim);
	banner.showAd();
	banner.hideAd();

##Interstitials

Interstitial ad placements differ from their banner counterparts in that they typically provide a full screen interactive experience. These are usually presented modally and take over the app experience while providing a way to return to the application. You have the capability of running static ads, videos and rich media creatives in your application by following the steps detailed below.

To show an interstitial you will need to create a new BurstlyInterstitial in the onCreate method of the Activity or Fragment where you are trying to show the interstitial. You will want to store a reference to the BurstlyInterstitial object that you instantiate in order to show the ad.

###Displaying an Interstitial

1. The BurstlyInterstitial constructor takes a reference to the Fragment or Activity that it's associated with, the Zone Id, and View name need to be filled in with the details from the zones you create in the SkyRocketApp.com UI, and finally a boolean value which tells whether automatic caching should be handled by the BurstlyInterstitial.
	
        mInterstitial = new BurstlyInterstitial(this, YOUR_ZONE_ID, YOUR_VIEW_NAME, USE_AUTOMATIC_CACHING);
	
2. Now that you have a reference to a BurstlyInterstitial you can use the showAd method to load and display an interstitial.
	
		mInterstitial.showAd();

##Caching Interstitials

Caching of interstitials before they are needed allows for much shorter load times, and a much better user experience. However one downside of caching results when you pass custom targeting parameters using setTargetingParameters. The values used passed in the request may not be known beforehand. For example if your app was a level based game, and you were passing the score back for targeting purposes, caching an ad at the start of the level would prevent sending the level score because the players score will not be known at the time that the ad is being cached.

The BurstlyInterstitial class provides an option to automatically manage the caching of interstitials for you. Whenever the Activity or Fragment it is associated with is shown the BurstlyInterstitial will cache an ad if an ad is not already cached and ready to show. To use automatic caching just set the *USE_AUTOMATIC_CACHING* argument in the constructor to *true*.

You can use the hasCachedAd and showAd methods of the AutomaticCacheManager in order to verify that an ad has been cached, and show it.

	if(mInterstitialMgr.hasCachedAd())
        mInterstitialMgr.showAd();

If you want a different caching behavior you can manually cache your ads using the cacheAd method.

    mInterstital.cacheAd();        

##Event Listeners

The IBurstlyListener is the interface used to receive Burstly related events (Additionally the BurstlyListenerAdapter is provided for convenience).  The supported events are:

	//Called when a banner or interstitial ad is hidden from the screen
    public void onHide(BurstlyBaseAd ad, AdHideEvent event)
    
    //Called when a banner or interstitial ad is shown
    public void onShow(BurstlyBaseAd ad, AdShowEvent event)
    
    //Called when all creatives in a given zone fail to display or an ad is throttled
    //Determine whether failure was a result of throttling using event.wasRequestThrottled()
    public void onFail(final BurstlyBaseAd ad, final AdFailEvent event)
    
    //Called when a banner or interstitial ad is cached
    public void onCache(final BurstlyBaseAd ad, final AdCacheEvent event)
    
    //Called when a banner or interstitial as is clicked
    public void onClick(final BurstlyBaseAd ad, final AdClickEvent event)
    
    //Called when an interstitial or a banner that is clicked presents in a modal view or activity
    //You should pause your app at this point
    public void onPresentFullscreen(final BurstlyBaseAd ad, final AdPresentFullscreenEvent event)
    
    //Called when a modal view or activity is dismissed
    //You should un-pause your app here
    public void onDismissFullscreen(final BurstlyBaseAd ad, final AdDismissFullscreenEvent event)

In order to associate/disassociate your listener with a banner or an interstitial use the addBurstlyListener and removeBurstlyListener methods.

    mInterstitial.addBurstlyListener(this);

##Custom Targeting

SkyRocket supports custom targeting. This allows you to pass a comma-separated string of parameters with each ad request. These custom parameters can be used to target specific ads into your app. Below is an example of passing age and gender information about your users:

    // You must set custom targeting before calling showAd or cacheAd.
    mInterstitial.setTargetingParameters("gender='male',age=24");
    mBanner.setTargetingParameters("gender='male',age=24");

Notice that string values should be enclosed in quotes and numerical values should not. Keep in mind that these parameters are arbitrary and you can pass anything you want.

For these targeting parameters to have any bearing on your ads you'll need to also configure specific creatives in the Burstly Dashboard. You can learn how to do this [here](/configuring-and-managing#Custom-Targeting).

##Rewards Currency Management

Initialization and display of rewards zones is handled in the same way that a banner or interstitial is displayed (for example, a zone containing an offer wall is instantiated as a BurstlyInterstitial), however you also need to manage the currency in your app to check the server for updates and add/subtract currency as it is awarded/used in app. To do this you will use the Burstly CurrencyManager. For a thorough example of rewards view the RewardsSample that is packaged with the SDK. 

###Burstly CurrencyManager

The Burstly CurrencyManager is automatically initialized using your app ID in the Burstly.init method. To get a reference to the CurrencyManager call getCurrencyManager. It is recommended that you store a reference to this in the onCreate method after you have called Burstly.init.

    mCurrencyManager = Burstly.getCurrencyManager();

Optionally, if you wish to pass a *USER_ID* param you can initialize the CurrencyManager manually using the following method:

    initManager(Context context, String publisherId, String userId);
    
To get the balance use CurrencyManager.getBalance(String currency). Replace CURRENCY_TYPE with your app's [Currency Key](/configuring-and-managing#Currency-Key). 

    mCurrencyManager.getBalance(CURRENCY_TYPE);
    
To manually add or remove currency, use CurrencyManager.increaseBalance(int amount, String currency) and CurrencyManager.decreaseBalance(int amount, String currency).  **Note that as of 1.18 the single currency methods for increaseBalance(int amount), decreaseBalance(int amount), and getBalance() have been removed.**

    mCurrencyManager.increaseBalance(increaseAmount, CURRENCY_TYPE);
    mCurrencyManager.decreaseBalance(decreaseAmount, CURRENCY_TYPE);

Both methods return the account balance as a result of the requested transaction. These methods are designed to work offline. If you never call these two methods, then CurrencyManager will always simply provide the total amount of currency awarded through Burstly Rewards. Note that decreaseBalance will allow the account balance to drop below 0. You must use your own logic to prevent users from buying items they can’t afford.

###ICurrencyListener

**Note that currency callbacks have changed as of 1.18 to support multiple currency.**

To receive notifications of Currency Manager events we will use the ICurrencyListener interface. This interface has two callbacks, didUpdateBalance and didFailToUpdateBalance. Each contain a map containing BalanceUpdateInfo for each of your currencies. The BalanceUpdateInfo provides the methods getChange, getNewTotal, and getOldTotal. If a user has not been awarded currency of type "CURRENCY_TYPE", balanceUpdateMap.get(CURRENCY_TYPE) will return null.

The following is a suggestion of how to implement these callbacks:
    
 	/**
     * Received update balance event (This will happen on a background thread)
     *
     * @param balanceUpdateMap contains a map keyed on currency type. Use get(CURRENCY_TYPE) to access balanceUpdateInfo
     *                         for your currency type
     */
    @Override
    public void didUpdateBalance(final Map<String, BalanceUpdateInfo> balanceUpdateMap) {
        // balanceUpdateMap will be empty if no currency has been awarded, so best to check that key exists
        String message = "didUpdateBalance: Awarded " + (balanceUpdateMap.containsKey(CURRENCY_TYPE) ? balanceUpdateMap.get(CURRENCY_TYPE).getChange() : 0)  + " " + CURRENCY_TYPE;
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
        // optionally log failure or try checking for update again
    }
    
We will add this listener in the activity's onResume method with a matching call to remove the ICurrencyListener in the onPause method (Note: You may choose to never remove this listener, and if that is the decision you make you will want to add this listener in the onCreate method). 

    @Override
    public void onPause() {
        mCurrencyManager.removeCurrencyListener(this); 
        ...
        super.onPause();
    }
    @Override
    public void onResume() {
        mCurrencyManager.addCurrencyListener(this); 
        checkForUpdatedBalance();
        ...
        super.onResume(); 
    }

We will need to check for balance updates at various points throughout the game using the CurrencyManager's checkForUpdate method. Note that currency updates do not happen instantly, with some 3rd party offers taking up to 24 hours to reward.

	private void checkForUpdatedBalance() { 
	    try {
	        mCurrencyManager.checkForUpdate(); 
	    	...
	}
	
This method will check for an updated balance asynchronously and you will receive notification of success or failure in the ICurrencyListener methods which you must implement. didUpdateBalance and didFailToUpdateBalance are called on the completion of an attempt to update your balance. This may be triggered by a call to CurrencyManager.checkForUpdate or potentially triggered internally by the system (So you may receive update or failure to update events without having called checkForUpdate). The currency manager callbacks will be made on an internal thread not the thread that requested the update. CurrencyManager.checkForUpdate will cache the latest known balance from the server, so this method even works offline.

It is recommended that you check in the Activity's onResume method as well as in the onDismissFullScreen method from the [IBurstlyListener](/android-guide#Event-Listeners), after and offer has been viewed. 

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

###Testing Rewards

To test rewards you can use the *REWARDS_SAMPLE* enum in BurstlyIntegrationModeAdNetworks. The featured offer in this enum will continue to serve even after you have converted the offer to allow for testing. Note that **you must use the correct app ID when you call Burstly.init** in order for your app to reward correctly.

    Burstly.init(this, BurstlyIntegrationModeAdNetworks.getAppId());
    Burstly.setIntegrationNetwork(BurstlyIntegrationModeAdNetworks.REWARDS_SAMPLE);
    Burstly.enableIntegrationMode(deviceIdArray);

##Open GL Based Applications

The threading model for Open GL based Applications is slightly different from traditional View based applications. View based applications use a thread referred to as the main or UI thread which processes the message queue and must be used to interact with Android View objects (This is an Android requirement). Open GL based applications create a thread (Often referred to as the GL thread) for handling the application's update and render loop. As a result most of your app's logic will be run on the GL thread, but all calls to interact with your Burstly objects must be run from the UI thread. This can be accomplished by having a reference to your Android Activity and calling it's runOnUiThread method to interact with View objects on the UI thread. 

##Decorating Your Interstitials

The BurstlyFullscreenActivity.IDecorator interface gives you full control of the look and feel of your Burstly interstitials (NOT interstitials from 3rd party SDKs). A fully documented sample on how to use this interface is available here:

- [Custom Decorator Sample](https://github.com/burstly/Android-CustomDecoratorSample)