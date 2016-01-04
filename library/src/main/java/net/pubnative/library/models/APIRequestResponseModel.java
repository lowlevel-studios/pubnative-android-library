package net.pubnative.library.models;

import java.util.List;

/**
 * Created by GauravMehta on 09/12/15.
 */
public class APIRequestResponseModel {

    public String                 status;
    public String                 error_message;
    public List<PubnativeAdModel> ads;

    public interface Status {

        String ERROR = "error";
        String OK    = "ok";
    }
}
