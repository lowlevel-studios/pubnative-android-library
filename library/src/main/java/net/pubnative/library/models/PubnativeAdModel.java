package net.pubnative.library.models;

import android.text.TextUtils;

import java.util.List;

public class PubnativeAdModel {
    public String                 title;
    public String                 description;
    public String                 cta_text;
    public String                 icon_url;
    public String                 banner_url;
    public String                 click_url;
    public String                 revenue_model;
    public int                    points;
    public List<PubnativeBeacon>  beacons;
    public String                 type;
    public String                 portrait_banner_url;

    public String getBeacon(String beaconType) {

        if (beaconType == null || TextUtils.isEmpty(beaconType) || beacons == null || beacons.isEmpty()) {
            return null;
        }

        for (PubnativeBeacon beacon : beacons) {
            if (beaconType.equalsIgnoreCase(beacon.type)) {
                return beacon.url;
            }
        }
        return null;
    }



}
