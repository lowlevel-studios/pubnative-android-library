package net.pubnative.library.request.model;

import java.util.HashMap;

public class PubnativeAPIV3DataModel {

    protected String                    type;
    protected HashMap<String, String>   data;

    public String getType() {
        return type;
    }

    public HashMap<String, String> getData() {
        return data;
    }
}
