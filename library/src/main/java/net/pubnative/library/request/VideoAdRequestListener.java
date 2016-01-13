package net.pubnative.library.request;

import net.pubnative.library.model.APIV3VideoAd;

import java.util.List;

/**
 * Created by davidmartin on 30/11/15.
 */
public interface VideoAdRequestListener extends  AdRequestListener{

    void onVideoAdRequestFinished(VideoAdRequest request, List<APIV3VideoAd> ads);
}
