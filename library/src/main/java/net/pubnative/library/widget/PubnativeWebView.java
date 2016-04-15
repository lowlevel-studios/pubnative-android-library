package net.pubnative.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PubnativeWebView extends WebView {

    private static final String TAG = PubnativeWebView.class.getSimpleName();

    public PubnativeWebView(Context context) {
        super(context);
        init();
    }

    public PubnativeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PubnativeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        getSettings().setJavaScriptEnabled(true);
        post(new Runnable() {
            @Override
            public void run() {
                getLayoutParams().height = 1;
                getLayoutParams().width = 1;
            }
        });
    }

    public void loadBeacon(final String jsBeacon) {

        Log.v(TAG, "loadBeacon");
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PubnativeWebView.this.loadUrl("javascript:" + jsBeacon);
            }
        });
        loadUrl("file:///android_asset/pubnative_ad.html");
    }
}
