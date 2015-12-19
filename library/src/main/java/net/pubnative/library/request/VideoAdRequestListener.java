package net.pubnative.library.request;

import net.pubnative.player.model.VASTModel;

import java.util.List;

/**
 * Created by davidmartin on 30/11/15.
 */
public interface VideoAdRequestListener extends  AdRequestListener{

    void onVideoAdRequestFinished(VideoAdRequest request, List<VASTModel> ads);
}
