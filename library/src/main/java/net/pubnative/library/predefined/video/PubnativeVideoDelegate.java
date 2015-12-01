package net.pubnative.library.predefined.video;

import android.content.Context;

import net.pubnative.library.PubnativeContract;
import net.pubnative.library.model.APIV3VideoAd;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivityDelegate;
import net.pubnative.library.predefined.PubnativeActivityDelegateManager;
import net.pubnative.library.predefined.PubnativeActivityListener;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.VideoAdRequest;
import net.pubnative.library.request.VideoAdRequestListener;

import org.nexage.sourcekit.vast.VASTPlayer;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by davidmartin on 20/11/15.
 */

public class PubnativeVideoDelegate extends PubnativeActivityDelegate implements VideoAdRequestListener {

    private int LAUNCH_FLAGS = FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
    private VideoAdRequest request;
    private VASTPlayer player;

    /**
     * Creates enables a new interstitial delegate for showing ad
     *
     * @param context   Context object
     * @param app_token App token provided by Pubnative
     * @param listener  Listener to track ad display events
     */
    public static void Create(Context context, String app_token, PubnativeActivityListener listener) {

        PubnativeVideoDelegate delegate = new PubnativeVideoDelegate(context, app_token, listener);
        PubnativeActivityDelegateManager.addDelegate(delegate);
    }

    public PubnativeVideoDelegate(Context context, String app_token, PubnativeActivityListener listener) {

        super(context, app_token, listener);
        this.request = new VideoAdRequest(this.context);
        this.request.setParameter(PubnativeContract.Request.APP_TOKEN, this.app_token);
        this.request.setParameter(PubnativeContract.Request.AD_COUNT, "1");
        this.request.setParameter(PubnativeContract.Request.ICON_SIZE, "200x200");
        this.request.setParameter(PubnativeContract.Request.BANNER_SIZE, "1200x627");
        this.request.start(this);
    }

    @Override
    public void onAdRequestStarted(AdRequest request) {
        // Do nothing
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {
        // Start the activity

    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception exception) {

        this.invokeListenerFailed(exception);
    }

    @Override
    public void onVideoAdRequestFinished(VideoAdRequest request, List<APIV3VideoAd> ads) {

        if (ads.size() > 0) {
            APIV3VideoAd ad = (APIV3VideoAd) ads.get(0);

            player = new VASTPlayer(this.context, new VASTPlayer.VASTPlayerListener() {

                @Override
                public void vastReady() {
                    PubnativeVideoDelegate.this.invokeListenerStart();
                    PubnativeVideoDelegate.this.invokeListenerOpened();
                    PubnativeVideoDelegate.this.player.play();
                }

                @Override
                public void vastError(int error) {
                    PubnativeVideoDelegate.this.invokeListenerFailed(new Exception("VASTPlayer error: " + error));
                }

                @Override
                public void vastClick() {

                }

                @Override
                public void vastComplete() {
                    PubnativeVideoDelegate.this.invokeListenerClosed();
                }

                @Override
                public void vastDismiss() {
                    PubnativeVideoDelegate.this.invokeListenerClosed();
                }
            });

            player.loadVideoWithData(ad.vast_xml);

        } else {
            this.invokeListenerFailed(new Exception("Pubnative - NO FILL ERROR"));
        }
    }
}