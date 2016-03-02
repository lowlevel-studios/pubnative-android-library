// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.tracking.model;

import android.util.Log;

import java.util.Date;

public class TrackingURLModel {

    private static final    String TAG                     = TrackingURLModel.class.getSimpleName();

    protected               String url;
    protected               long trackingStartTime;

    /**
     * Constructor
     * This constructor calls another constructor to initialise startTime for tracking url
     *
     * @param url Ad impression tracking url
     */

    public TrackingURLModel(String url) {
        this(url, new Date().getTime());

        Log.v(TAG, "TrackingURLModel(String url)");
    }

    /**
     * Constructor
     *
     * @param url           Ad impression tracking url
     * @param startTime     Impression url tracking startTime
     */

    public TrackingURLModel(String url, long startTime) {

        Log.v(TAG, "TrackingURLModel(String url, long startTime): ");

        this.url = url;
        this.trackingStartTime = startTime;
    }

    //==============================================================================================
    // GETTER
    //==============================================================================================

    /**
     * Getter for tracking start time
     *
     * @return long tracking start time
     */
    public long getTrackingStartTime() {

        Log.v(TAG, "getTrackingStartTime");

        return trackingStartTime;
    }

    /**
     * Getter for tracking url
     *
     * @return String Ad impression tracking url
     */

    public String getUrl() {

        Log.v(TAG, "getUrl");

        return url;
    }

}
