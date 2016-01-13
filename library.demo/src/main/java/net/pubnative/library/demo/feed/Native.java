package net.pubnative.library.demo.feed;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.pubnative.library.PubnativeContract;
import net.pubnative.library.demo.R;
import net.pubnative.library.demo.RequestData;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.AdRequestListener;

import java.util.ArrayList;

public class Native extends RelativeLayout implements AdRequestListener {

    private static final String TAG = Native.class.getName();

    private View        container;
    private ProgressBar progressBar;
    private ImageView   banner;
    private ImageView   icon;
    private TextView    title;
    private Button      cta;
    private Button      request;

    private NativeAdModel model;

    public Native(Context context, AttributeSet attrs) {

        super(context, attrs);

        inflate(context, R.layout.feed_native, this);

        container = findViewById(R.id.native_content);

        progressBar = (ProgressBar) findViewById(R.id.native_progressBar);
        banner = (ImageView) findViewById(R.id.native_banner);
        icon = (ImageView) findViewById(R.id.native_icon);
        title = (TextView) findViewById(R.id.native_title);
        cta = (Button) findViewById(R.id.native_cta);
        cta.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Native.this.onNativeClick(v);
            }
        });

        request = (Button) findViewById(R.id.native_request);
        request.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Native.this.onRequestClick(v);
            }
        });
    }

    public void onNativeClick(View view) {

        Log.d(TAG, "onNativeClick");

        model.open(this.getContext());
    }

    public void onRequestClick(View view) {

        Log.d(TAG, "onRequestClick");

        model = null;

        container.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        AdRequest request = new AdRequest(getContext());
        request.setParameter(PubnativeContract.Request.APP_TOKEN, RequestData.APP_TOKEN);
        request.start(AdRequest.Endpoint.NATIVE, this);
    }

    public void configureView(NativeAdModel ad) {

        Picasso.with(getContext()).load(ad.bannerUrl).into(banner);
        Picasso.with(getContext()).load(ad.iconUrl).into(icon);
        title.setText(ad.title);
        cta.setText(ad.ctaText);

        container.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    //=========================
    // Callbacks
    //=========================

    // AdRequestListener
    //-------------------------

    @Override
    public void onAdRequestStarted(AdRequest request) {

        Log.d(TAG, "onAdRequestStarted");
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {

        Log.d(TAG, "onAdRequestFinished");

        if (ads.size() > 0) {

            model = ads.get(0);
            configureView(model);

        } else {

            Toast.makeText(getContext(), "Native - No fill", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception ex) {

        Log.d(TAG, "onAdRequestFailed: " + ex);
        Toast.makeText(getContext(), "Video - Request error", Toast.LENGTH_SHORT);
    }
}
