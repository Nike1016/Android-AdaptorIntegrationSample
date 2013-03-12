package com.meganet.integration;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class MeganetInterstitialActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ImageView mImageView = MeganetAdaptor.sInterstitialView;
        MeganetAdaptor.sInterstitialView = null;
        addContentView(mImageView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    @Override
    protected void onDestroy() {
        MeganetAdaptor.sIsShowingNow = false;
        super.onDestroy();
    }

}
