package net.pubnative.library.predefined.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.RelativeLayout;

import net.pubnative.library.R;
import net.pubnative.library.model.APIV3VideoAd;
import net.pubnative.player.VASTPlayer;
import net.pubnative.player.model.VASTModel;

public class PubnativeVideoActivity extends Activity implements VASTPlayer.Listener {

    private static final String TAG = PubnativeVideoActivity.class.getName();

    public static final String EXTRA_MODEL         = "net.pubnative.library.predefined.video.vastModel";
    public static final String EXTRA_SKIP_NAME     = "net.pubnative.library.predefined.video.skipName";
    public static final String EXTRA_SKIP_TIME     = "net.pubnative.library.predefined.video.skipTime";
    public static final String EXTRA_REVENUE_MODEL = "net.pubnative.library.predefined.video.revenue_model";

    private VASTPlayer mVASTPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Intent    intent       = getIntent();
        VASTModel model        = (VASTModel) intent.getSerializableExtra(EXTRA_MODEL);
        String    skipName     = intent.getStringExtra(EXTRA_SKIP_NAME);
        int       skipTime     = intent.getIntExtra(EXTRA_SKIP_TIME, 0);
        String    revenueModel = intent.getStringExtra(EXTRA_REVENUE_MODEL);

        if (model == null) {

            Log.e(TAG, "vastModel is null. Stopping activity.");
            finish();

        } else {


            // Read campaign type from revenue model
            VASTPlayer.CampaignType campaignType = VASTPlayer.CampaignType.CPC;

            if(APIV3VideoAd.RevenueModel.CPM.equals(revenueModel)) {

                campaignType = VASTPlayer.CampaignType.CPM;
            }

            hideTitleStatusBars();
            initializeView(model, skipName, skipTime, campaignType);
        }
    }

    private void initializeView(VASTModel model, String skipName, int skipTime, VASTPlayer.CampaignType campaignType) {

        Log.d(TAG, "initializeView");

        RelativeLayout rootView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.pubnative_video, null);
        setContentView(rootView);

        mVASTPlayer = (VASTPlayer) rootView.findViewById(R.id.player);
        mVASTPlayer.setCampaignType(campaignType);
        mVASTPlayer.setListener(this);
        mVASTPlayer.setSkipName(skipName);
        mVASTPlayer.setSkipTime(skipTime);
        mVASTPlayer.load(model);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        mVASTPlayer.clean();
    }

    private void hideTitleStatusBars() {

        Log.d(TAG, "hideTitleStatusBars");

        // hide title bar of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    // VASTPlayer.Listener
    //------------------------------

    @Override
    public void onVASTPlayerLoadFinish() {

        Log.v(TAG, "onVASTPlayerLoadFinish");
        mVASTPlayer.play();
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {

        Log.v(TAG, "onVASTPlayerFail: " + exception);
        finish();
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

        Log.v(TAG, "onVASTPlayerPlaybackStart");
    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

        Log.v(TAG, "onVASTPlayerPlaybackFinish");
    }

    @Override
    public void onVASTPlayerClick() {

        Log.v(TAG, "onVASTPlayerClick");
        finish();
    }
}
