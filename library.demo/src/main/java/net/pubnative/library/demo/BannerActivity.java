package net.pubnative.library.demo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.library.banner.PubnativeBanner;
import net.pubnative.library.demo.utils.Settings;

public class BannerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

    }

    public void onRequestClick(View v) {
        PubnativeBanner banner = new PubnativeBanner(this, Settings.getAppToken(), PubnativeBanner.Size.BANNER_50);
        //banner.setListener(this);
        banner.load();
        banner.show();
    }
}
