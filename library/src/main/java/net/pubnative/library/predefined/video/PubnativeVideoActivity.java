package net.pubnative.library.predefined.video;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.RelativeLayout;

import net.pubnative.library.R;
import net.pubnative.player.VASTPlayer;
import net.pubnative.player.model.VASTModel;

public class PubnativeVideoActivity extends Activity implements VASTPlayer.InteractionListener, VASTPlayer.PlaybackListener, VASTPlayer.CachingListener {

    private static final String TAG = PubnativeVideoActivity.class.getName();

    public static final String EXTRA_MODEL     = "com.nexage.android.vast.player.vastModel";
    public static final String EXTRA_SKIP_NAME = "com.nexage.android.vast.player.skipName";
    public static final String EXTRA_SKIP_TIME = "com.nexage.android.vast.player.skipTime";

    private VASTPlayer mVASTPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Intent    intent   = getIntent();
        VASTModel model    = (VASTModel) intent.getSerializableExtra(EXTRA_MODEL);
        String    skipName = intent.getStringExtra(EXTRA_SKIP_NAME);
        int       skipTime = intent.getIntExtra(EXTRA_SKIP_TIME, 0);

        if (model == null) {

            Log.e(TAG, "vastModel is null. Stopping activity.");
            finish();

        } else {

            hideTitleStatusBars();
            initializeView(model, skipName, skipTime);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);

        mVASTPlayer.rotate();
    }

    private void initializeView(VASTModel model, String skipName, int skipTime) {

        Log.d(TAG, "initializeView");

        RelativeLayout rootView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.pubnative_video, null);
        setContentView(rootView);

        mVASTPlayer = (VASTPlayer) rootView.findViewById(R.id.player);
        mVASTPlayer.setInteractionListener(this);
        mVASTPlayer.setCachingListener(this);
        mVASTPlayer.setPlaybackListener(this);
        mVASTPlayer.load(model, skipName, skipTime);
    }

    private void hideTitleStatusBars() {

        Log.d(TAG, "hideTitleStatusBars");

        // hide title bar of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onVASTPlayerMute() {

        Log.v(TAG, "onVASTPlayerMute");
    }

    @Override
    public void onVASTPlayerUnMute() {

        Log.v(TAG, "onVASTPlayerUnMute");
    }

    @Override
    public void onVASTPlayerClick() {

        Log.v(TAG, "onVASTPlayerClick");

        finish();
    }

    @Override
    public void onVASTPlayerDismiss() {

        Log.v(TAG, "onVASTPlayerDismiss");

        finish();
    }

    @Override
    public void onVASTPlayerStart() {

        Log.v(TAG, "onVASTPlayerStart");
    }

    @Override
    public void onVASTPlayerFirstQuartile() {

        Log.v(TAG, "onVASTPlayerFirstQuartile");
    }

    @Override
    public void onVASTPlayerMidpoint() {

        Log.v(TAG, "onVASTPlayerMidpoint");
    }

    @Override
    public void onVASTPlayerThirdQuartile() {

        Log.v(TAG, "onVASTPlayerThirdQuartile");
    }

    @Override
    public void onVASTPlayerCompleted() {

        Log.v(TAG, "onVASTPlayerCompleted");
    }

    @Override
    public void onVASTPlayerCachingStart() {

        Log.v(TAG, "onVASTPlayerCachingStart");
    }

    @Override
    public void onVASTPlayerCachingFinish() {

        Log.v(TAG, "onVASTPlayerCachingFinish");

        mVASTPlayer.play();
    }

    @Override
    public void onVASTPlayerCachingFail() {

        Log.v(TAG, "onVASTPlayerCachingFail");

        finish();
    }
}
