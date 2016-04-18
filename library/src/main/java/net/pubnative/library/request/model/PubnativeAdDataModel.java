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

package net.pubnative.library.request.model;

import java.util.List;
import java.util.Map;

public interface PubnativeAdDataModel {

    String getClickUrl();

    String getTitle();

    String getDescription();

    String getCta();

    String getRating();

    PubnativeImage getIcon();

    PubnativeImage getBanner();

    Map getMetaField(String metaType);

    List getBeacons(String type);

    List getAssetTrackingUrls();

    interface AssetType {

        String TITLE        = "title";
        String DESCRIPTION  = "description";
        String CTA          = "cta";
        String RATING       = "rating";
        String ICON         = "icon";
        String BANNER       = "banner";
    }

    interface MetaType {

        String POINTS       = "points";
        String REVENU_MODEL = "revenuemodel";
        String CAMPAIGN_ID  = "campaignid";
        String CREATIVE_ID  = "creativeid";
    }

    interface BeaconType {
        String URL = "url";
        String JS = "js";
    }
}
