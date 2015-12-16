//
//  VastActivity.java
//

//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.vast.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import org.nexage.sourcekit.util.VASTLog;
import org.nexage.sourcekit.vast.R;
import org.nexage.sourcekit.vast.VASTPlayer;
import org.nexage.sourcekit.vast.model.VASTModel;
import org.nexage.sourcekit.vast.view.VASTPlayerView;

public class VASTActivity extends Activity {

    public static final  String EXTRA_MODEL     = "com.nexage.android.vast.player.vastModel";
    public static final  String EXTRA_SKIP_NAME = "com.nexage.android.vast.player.skipName";
    public static final  String EXTRA_SKIP_TIME = "com.nexage.android.vast.player.skipTime";
    private static final String TAG             = "VASTActivity";

    private VASTPlayerView mVASTPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        VASTLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Intent    intent   = getIntent();
        VASTModel model    = (VASTModel) intent.getSerializableExtra(EXTRA_MODEL);
        String    skipName = intent.getStringExtra(EXTRA_SKIP_NAME);
        int       skipTime = intent.getIntExtra(EXTRA_SKIP_TIME, 0);

        if (model == null) {

            VASTLog.e(TAG, "vastModel is null. Stopping activity.");
            VASTPlayer.listener.vastDismiss();

        } else {

            hideTitleStatusBars();
            initializeView(model, skipName, skipTime);
        }
    }

    private void initializeView(VASTModel model, String skipName, int skipTime) {

        VASTLog.d(TAG, "initializeView");

        RelativeLayout rootView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.pubnative_player_fullscreen, null);

        mVASTPlayerView = (VASTPlayerView) rootView.findViewById(R.id.player);
        mVASTPlayerView.startVideo(model, skipName, skipTime);

        setContentView(rootView);
    }

    private void hideTitleStatusBars() {

        VASTLog.d(TAG, "hideTitleStatusBars");

        // hide title bar of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // hide status bar and soft keys.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
