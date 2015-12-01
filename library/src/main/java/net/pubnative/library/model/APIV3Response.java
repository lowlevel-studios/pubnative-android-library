package net.pubnative.library.model;

import java.util.List;

/**
 * Created by davidmartin on 30/11/15.
 */
public class APIV3Response {

    public String             status;
    public List<APIV3VideoAd> ads;
    public String             error_message;

    public interface Status {

        String OK    = "ok";
        String ERROR = "error";
    }
}
