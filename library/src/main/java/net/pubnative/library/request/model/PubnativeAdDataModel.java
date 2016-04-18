package net.pubnative.library.request.model;

import java.util.HashMap;
import java.util.List;

public interface PubnativeAdDataModel {

    String getClickUrl();

    String getTitle();

    String getDescription();

    String getCta();

    String getRating();

    PubnativeImage getIcon();

    PubnativeImage getBanner();

    HashMap<String, String> getMetaField(String metaType);

    List<String> getAllBeacons();

    List<String> getJsBeacons();

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
}
