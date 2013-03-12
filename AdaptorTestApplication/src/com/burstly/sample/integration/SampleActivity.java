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
package com.burstly.sample.integration;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.burstly.conveniencelayer.Burstly;
import com.burstly.conveniencelayer.BurstlyActivity;
import com.burstly.conveniencelayer.BurstlyBanner;

/**
 * Activity with the demonstration of intagrated library.
 * <p>
 * You just need to add some custom library integration to classpath. Here, as an example, we use
 * 'meganet.jar' from IntegrationSample.
 */
public class SampleActivity extends BurstlyActivity {

    /**
     * Reference to {@link BurstlyBanner} instance.
     */
    private BurstlyBanner mBurstlyBanner;

    /**
     * Used to send requests manually.
     */
    private Button mButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Burstly.init(this, "S86n-cfJlUa-O5pwTE_lfA");

        // This pubId and zoneId will provide the next parameters from server:
        // meganet?{"factoryClass":"com.meganet.integration.MeganetAdaptorFactory",
        // "imageUrl":"http://img263.imageshack.us/img263/6085/samplen.png"}
        //Note that the zone id is added in the layout xml file
        mBurstlyBanner = new BurstlyBanner (this, R.id.bannerview);

        mButton = (Button)findViewById(R.id.request_button);
        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                mBurstlyBanner.showAd();
            }

        });
    }

}