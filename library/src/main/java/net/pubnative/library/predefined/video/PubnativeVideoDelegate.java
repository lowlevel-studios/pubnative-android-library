package net.pubnative.library.predefined.video;

import android.content.Context;
import android.content.Intent;

import net.pubnative.library.PubnativeContract;
import net.pubnative.library.model.APIV3VideoAd;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivityDelegate;
import net.pubnative.library.predefined.PubnativeActivityDelegateManager;
import net.pubnative.library.predefined.PubnativeActivityListener;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.VideoAdRequest;
import net.pubnative.library.request.VideoAdRequestListener;
import net.pubnative.player.VASTParser;
import net.pubnative.player.model.VASTModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidmartin on 20/11/15.
 */

public class PubnativeVideoDelegate extends PubnativeActivityDelegate implements VideoAdRequestListener {

    private VideoAdRequest request;

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
        requestAd();
    }

    private void requestAd(){



        this.request = new VideoAdRequest(this.context);
        this.request.setParameter(PubnativeContract.Request.APP_TOKEN, this.app_token);
        this.request.setParameter(PubnativeContract.Request.AD_COUNT, "1");
        this.request.setParameter(PubnativeContract.Request.ICON_SIZE, "200x200");
        this.request.setParameter(PubnativeContract.Request.BANNER_SIZE, "1200x627");
        this.request.start(this);
    }

    private void createPlayer(VASTModel model, String revenueModel){

        Intent vastPlayerIntent = new Intent(context, PubnativeVideoActivity.class);
        vastPlayerIntent.putExtra(PubnativeVideoActivity.EXTRA_MODEL, model);
        vastPlayerIntent.putExtra(PubnativeVideoActivity.EXTRA_REVENUE_MODEL, revenueModel);
        context.startActivity(vastPlayerIntent);
    }

    //========================================
    // AdRequestListener
    //========================================

    @Override
    public void onAdRequestStarted(AdRequest request) {

        // Do nothing
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {

        // Do nothing
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception exception) {

        this.invokeListenerFailed(exception);
    }

    //========================================
    // VideoAdRequestListener
    //========================================

    @Override
    public void onVideoAdRequestFinished(final VideoAdRequest request, List<APIV3VideoAd> ads) {

        if (ads.size() > 0) {

            final APIV3VideoAd ad = ads.get(0);

            new VASTParser(this.context).setListener(new VASTParser.Listener() {

                @Override
                public void onVASTParserError(int error) {

                    if(PubnativeVideoDelegate.this.request == request) {

                        PubnativeVideoDelegate.this.invokeListenerFailed(new Exception("VAST Parser error: " + error));
                    }
                }

                @Override
                public void onVASTParserFinished(VASTModel model) {

                    if (PubnativeVideoDelegate.this.request == request) {

                        createPlayer(model, ad.revenue_model);
                    }
                }

            }).execute(ad.vast_xml);

            }else{

                this.invokeListenerFailed(new Exception("Pubnative - NO FILL ERROR"));
        }
    }
}
