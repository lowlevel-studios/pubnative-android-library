package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.library.banner.PubnativeBanner;
import net.pubnative.library.demo.utils.Settings;

public class BannerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("Test Application Banner", "Banner Activity got click action");
            }
        });

    }

    public void onRequestClick(View v) {
        PubnativeBanner banner = new PubnativeBanner(
                this,
                Settings.getAppToken(),
                PubnativeBanner.Size.BANNER_50,
                PubnativeBanner.Position.TOP
        );
        //banner.setListener(this);
        banner.load();
        banner.show();
    }
}
