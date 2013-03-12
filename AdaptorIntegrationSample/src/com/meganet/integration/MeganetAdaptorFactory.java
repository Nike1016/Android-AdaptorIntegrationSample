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

import java.util.Map;

import android.content.Context;

import com.meganet.utils.Logger;

import com.burstly.lib.component.IBurstlyAdaptor;
import com.burstly.lib.feature.networks.IAdaptorFactory;

/**
 * Factory class for {@link MeganetAdaptor}. It produces Meganet adaptor implementations.
 */
public class MeganetAdaptorFactory implements IAdaptorFactory {

    /**
     * A key for context object being passed in parameters.
     */
    private static final String CONTEXT = "context";

    /**
     * A key for current BurstlyView id object being passed in parameters.
     */
    private static final String VIEW_ID = "viewId";

    /**
     * A key for adaptor name being passed in parameters.
     */
    private static final String ADAPTOR_NAME = "adaptorName";

    @Override
    public void initialize(final Map<String, ?> params) throws IllegalArgumentException {
        Logger.logInfo(this, "Initialization called");
    }

    @Override
    public IBurstlyAdaptor createAdaptor(final Map<String, ?> params) {
        if (params == null) {
            Logger.logError(this, "Adaptor creation parameters cannot be null");
            return null;
        }
        // Three parameters are available.
        final Object context = params.get(CONTEXT);
        final Object viewId = params.get(VIEW_ID);
        final Object adaptorName = params.get(ADAPTOR_NAME);
        return new MeganetAdaptor((Context)context, (String)viewId, (String)adaptorName);
    }

    @Override
    public void destroy() {
        // implement any logic to clear factory instance
        Logger.logInfo(this, "Factory destroyed.");
    }

}