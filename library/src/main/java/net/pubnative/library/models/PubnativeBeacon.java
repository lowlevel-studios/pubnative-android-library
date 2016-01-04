package net.pubnative.library.models;

public class PubnativeBeacon {

    public String type;
    public String url;

    interface BeaconType {

        String IMPRESSION = "impression";
    }
}
