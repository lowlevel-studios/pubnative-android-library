package net.pubnative.library.model;

import java.util.Map;

/**
 * Created by davidmartin on 30/11/15.
 */
public class APIV3VideoAd {

    public String revenue_model;
    public int    points;
    public String vast_xml;
    public Map    preview_banner;

    public interface RevenueModel {

        String CPM = "cpm";
        String CPC = "cpc";
        String CPA = "cpa";
    }

    public interface PreviewBanner {

        String id = "id";
        String url = "url";
    }

    public String getPreviewBannerURL () {

        String result = null;

        if(preview_banner != null) {

            result = (String) preview_banner.get(PreviewBanner.url);
        }

        return result;
    }
}
