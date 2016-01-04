package net.pubnative.library.models;

import android.text.TextUtils;

import java.util.List;

public class PubnativeAdModel {

    public String                title;
    public String                description;
    public String                cta_text;
    public String                icon_url;
    public String                banner_url;
    public String                click_url;
    public String                revenue_model;
    public int                   points;
    public List<PubnativeBeacon> beacons;
    public String                type;
    public String                portrait_banner_url;

    /**
     * This function will return the Beacon URL on the bases of beacon type.
     * It will traverse all beacons and search for <code><beaconType<code/> beaconType.
     *
     * @param beaconType
     *
     * @return return Beacon URL or null otherwise.
     */
    public String getBeacon(String beaconType) {

        String beaconUrl = null;

        if (!TextUtils.isEmpty(beaconType)) {

            for (PubnativeBeacon beacon : beacons) {

                if (beaconType.equalsIgnoreCase(beacon.type)) {

                    beaconUrl = beacon.url;
                    break;
                }
            }
        }

        return beaconUrl;
    }
}
