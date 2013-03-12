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
package com.meganet.utils;

import android.util.Log;

/**
 * Class for logging messages in the common format.
 */
public class Logger {

    /**
     * Non constructable.
     */
    private Logger() {
    }

    /**
     * Log tag for integartion example.
     */
    private static final String LOG_TAG = "Meganet Adaptor Integration";

    /**
     * Logs error message with classname specified.
     */
    public static void logError(final Object logCaller, final String message) {
        Log.e(LOG_TAG, format(logCaller, message));
    }

    /**
     * Logs warning message with classname specified.
     */
    public static void logWarning(final Object logCaller, final String message) {
        Log.w(LOG_TAG, format(logCaller, message));
    }

    /**
     * Logs info message with classname specified.
     */
    public static void logInfo(final Object logCaller, final String message) {
        Log.i(LOG_TAG, format(logCaller, message));
    }

    /**
     * Logs debug message with classname specified.
     */
    public static void logDebug(final Object logCaller, final String message) {
        Log.d(LOG_TAG, format(logCaller, message));
    }

    /**
     * Common format for logging.
     */
    private static String format(final Object logCaller, final String message) {
        return logCaller.getClass().getName() + " says: " + message;
    }
}
