package net.pubnative.library;

import net.pubnative.library.models.PubnativeAdModel;

import java.util.List;

/**
 * Created by GauravMehta on 09/12/15.
 */
public class APIRequestResponseModel {

    public String status;
    public List<PubnativeAdModel> ads;
    public String error_message;
}
