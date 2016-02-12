package net.pubnative.library.tracking;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import net.pubnative.library.models.PubnativeAdModel;
import net.pubnative.library.models.PubnativeBeacon;
import net.pubnative.library.network.PubnativeAPIRequest;
import net.pubnative.library.utils.SystemUtils;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for tracking an ad. This ensures two things:
 * <ul>
 *     <li>impression tracking</li>
 *     <li>click tracking</li>
 * </ul>
 */
public class PubnativeAdTracker implements PubnativeAPIRequest.Listener {

    private static String                   TAG                 = PubnativeAdTracker.class.getSimpleName();

    private PubnativeAdModel.Listener       mListener;
    private View                            mView;
    private View                            mClickableView;
    private float                           VISIBILITY_THRESHOLD = 0.50f;
    private ViewTreeObserver                mViewTreeObserver;
    private PubnativeAdModel                mPubnativeAdModel;
    private final ScheduledExecutorService  mExecutor;
    private boolean                         isTracked           = false;
    private boolean                         isTrackingStopped   = false;
    private Handler                         mHandler;

    /**
     * Constructor
     * @param view ad view
     * @param clickableView clickable view
     * @param listener listener for callbacks
     * @param adModel adModel
     */
    public PubnativeAdTracker(View view, View clickableView, PubnativeAdModel.Listener listener, PubnativeAdModel adModel) {

        mListener = listener;

        mView = view;
        mClickableView = clickableView;
        mPubnativeAdModel = adModel;
        mViewTreeObserver = mView.getViewTreeObserver();

        mExecutor = Executors.newScheduledThreadPool(1);

        mHandler = new Handler();

        startTracking();
    }

    public void stopTracking() {

        Log.v(TAG, "stopTracking()");

        mExecutor.shutdownNow();
        mListener = null;
        isTrackingStopped = true;
        mClickableView.setOnClickListener(null);
    }

    private void startTracking() {

        Log.v(TAG, "startTracking()");

        mViewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
        mViewTreeObserver.addOnScrollChangedListener(onScrollChangedListener);

        mClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleClickEvent();
            }
        });
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            checkImpression();
        }
    };

    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            checkImpression();
        }
    };

    private void checkImpression() {
        if (SystemUtils.isVisibleOnScreen(mView, VISIBILITY_THRESHOLD)) {

            if(isTracked || isTrackingStopped || mExecutor.isShutdown()) {
                return;
            }

            mExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    long firstVisibleTime = System.currentTimeMillis() - 100;
                    while(true) {
                        if(isTracked) {
                            return;
                        }
                        if (SystemUtils.isVisibleOnScreen(mView, VISIBILITY_THRESHOLD)) {
                            if(System.currentTimeMillis() - firstVisibleTime >= 1000) {

                                isTracked = true;
                                startImpressionRequest();
                                mViewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
                                mViewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener);

                                return;
                            }
                        } else {
                            return;
                        }
                    }

                }
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    protected void startImpressionRequest() {

        Log.v(TAG, "startImpressionRequest()");

        if(isTrackingStopped) {
            return;
        }

        String impressionUrl = mPubnativeAdModel.getBeacon(PubnativeBeacon.BeaconType.IMPRESSION);

        if (TextUtils.isEmpty(impressionUrl)) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    invokeOnImpressionFailed(new MalformedURLException("Can not confirm impression, no Beacon URL found"));
                }
            });
        } else {

            PubnativeAPIRequest.send(PubnativeAPIRequest.Method.GET, impressionUrl, this);
        }
    }

    private void handleClickEvent() {

        Log.v(TAG, "handleClickEvent()");

        if (!TextUtils.isEmpty(mPubnativeAdModel.getClickUrl())) {

            URLOpener urlOpener = new URLOpener(mView.getContext());
            urlOpener.openInBackground(mPubnativeAdModel.getClickUrl(), true, urlOpenerListener);

        } else {

            invokeOnClickFailed(new MalformedURLException("Can not open ad, no click_url found"));
        }
    }

    protected URLOpener.Listener urlOpenerListener = new URLOpener.Listener() {
        @Override
        public void onURLOpenerStart(String url) {

        }

        @Override
        public void onURLOpenerRedirect(String url) {

        }

        @Override
        public void onURLOpenerFinish(String url) {
            invokeOnClicked();
        }

        @Override
        public void onURLOpenerFailed(String url, Exception exception) {
            invokeOnClickFailed(exception);
        }
    };

    private void invokeOnImpressionFailed(Exception exception) {

        if(mListener != null) {

            mListener.onPubnativeAdModelImpressionFailed(exception);
        }
    }

    private void invokeOnImpressionConfirmed() {

        if(mListener != null) {

            mListener.onPubnativeAdModelImpressionConfirmed(mView);
        }
    }

    private void invokeOnClicked() {
        if(mListener != null) {

            mListener.onPubnativeAdModelClicked(mClickableView);
        }
    }

    private void invokeOnClickFailed(Exception exception) {
        if(mListener != null) {

            mListener.onPubnativeAdModelClickFailed(exception);
        }
    }

    @Override
    public void invokeOnResponse(String response) {
        invokeOnImpressionConfirmed();
    }

    @Override
    public void invokeOnErrorResponse(Exception error) {
        invokeOnImpressionFailed(error);
    }
}
