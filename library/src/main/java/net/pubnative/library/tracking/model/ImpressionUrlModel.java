package net.pubnative.library.tracking.model;

public class ImpressionUrlModel {

    protected String URL;
    protected long  impressionTime;

    public long getImpressionTime() {
        return impressionTime;
    }

    public void setImpressionTime(long impressionTime) {
        this.impressionTime = impressionTime;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public ImpressionUrlModel(String url, long time) {
        this.URL = url;
        this.impressionTime = time;
    }
}
