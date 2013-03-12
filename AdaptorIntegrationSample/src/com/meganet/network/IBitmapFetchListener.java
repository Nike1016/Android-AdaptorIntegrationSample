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
package com.meganet.network;

import android.graphics.Bitmap;

/**
 * Listener used to receive callbacks from {@link BitmapFetchTask}.
 */
public interface IBitmapFetchListener {

    /**
     * Called when the bitmap was successfully fetched.
     */
    public void imageLoaded(Bitmap fetchedBitmap);

    /**
     * Called when the bitmap was not fetched due to some errors.
     */
    public void imageFailedToLoad();

}
