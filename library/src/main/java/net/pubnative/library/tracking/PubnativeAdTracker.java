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

    private static String                   TAG                             = PubnativeAdTracker.class.getSimpleName();

    private PubnativeAdModel.Listener       mListener;
    private View                            mView;
    private View                            mClickableView;
    private ViewTreeObserver                mViewTreeObserver;
    private PubnativeAdModel                mPubnativeAdModel;
    private final ScheduledExecutorService  mExecutor;
    private boolean                         mIsTracked                      = false;
    private boolean                         mIsTrackingStopped              = false;
    private Handler                         mHandler;

    private static final float              VISIBILITY_PERCENTAGE_THRESHOLD = 0.50f;
    private static final long               VISIBILITY_TIME_THRESHOLD       = 1000;
    private static final long               VISIBILITY_CHECK_INTERVAL       = 200;

    /**
     * Constructor
     * @param view ad view
     * @param clickableView clickable view
     * @param listener listener for callbacks
     * @param adModel adModel
     */
    public PubnativeAdTracker(View view, View clickableView, PubnativeAdModel.Listener listener, PubnativeAdModel adModel) {

        mExecutor = Executors.newScheduledThreadPool(1);
        mHandler = new Handler();

        mListener = listener;
        mPubnativeAdModel = adModel;

        if(view == null) {

            Log.e(TAG, "PubnativeAdTracker(): view is null");
            invokeOnImpressionFailed(new NullPointerException("view can't be null"));
            return;
        }

        if(clickableView == null) {

            Log.e(TAG, "PubnativeAdTracker(): clickable view is null");
            invokeOnClickFailed(new NullPointerException("clickable view can't be null"));
            return;
        }

        mView = view;
        mClickableView = clickableView;
        mViewTreeObserver = mView.getViewTreeObserver();

        startTracking();
    }

    public void stopTracking() {

        Log.v(TAG, "stopTracking()");

        mExecutor.shutdownNow();
        mListener = null;
        mIsTrackingStopped = true;
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
        if (SystemUtils.isVisibleOnScreen(mView, VISIBILITY_PERCENTAGE_THRESHOLD)) {

            if (mIsTracked || mIsTrackingStopped || mExecutor.isShutdown()) {
                return;
            }

            mExecutor.schedule(new Runnable() {

                @Override
                // After VISIBILITY_CHECK_INTERVAL (i.e. 200ms) of view visible on screen (first time)
                // it would be invoked. It regularly checks for visibility of view on screen on interval
                // of 200ms (VISIBILITY_CHECK_INTERVAL) to ensure that view is visible on the screen at least for 1 sec.
                public void run() {

                    // note first visible time
                    long firstVisibleTime = System.currentTimeMillis() - VISIBILITY_CHECK_INTERVAL;

                    Log.v(TAG, "checkImpression(), first visible at: " + firstVisibleTime);

                    // loop to make sure view is visible on screen for at least 1sec
                    while (System.currentTimeMillis() - firstVisibleTime < VISIBILITY_TIME_THRESHOLD + VISIBILITY_CHECK_INTERVAL) {

                        Log.v(TAG, "checkImpression(), within time threshold checking. Current time is: " + System.currentTimeMillis());

                        // If view is already tracked or not visible it returns from the loop without confirming impression
                        if (mIsTracked || !SystemUtils.isVisibleOnScreen(mView, VISIBILITY_PERCENTAGE_THRESHOLD)) {

                            Log.v(TAG, "checkImpression(), either already tracked or not visible anymore. Already tracked is: " + mIsTracked + " & Current time is: " + System.currentTimeMillis());
                            return;
                        }

                        if (System.currentTimeMillis() - firstVisibleTime >= VISIBILITY_TIME_THRESHOLD) {

                            Log.v(TAG, "checkImpression(), it's visible more than " + VISIBILITY_TIME_THRESHOLD + "ms Current time is: " + System.currentTimeMillis());

                            mIsTracked = true;
                            startImpressionRequest();
                            mViewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
                            mViewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener);

                            return;
                        }

                        try {
                            Log.v(TAG, "checkImpression(), thread is sleeping for " + VISIBILITY_CHECK_INTERVAL + "ms Current time is: " + System.currentTimeMillis());
                            // pausing thread for 200ms (VISIBILITY_CHECK_INTERVAL)
                            Thread.sleep(VISIBILITY_CHECK_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, VISIBILITY_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    protected void startImpressionRequest() {

        Log.v(TAG, "startImpressionRequest()");

        if(mIsTrackingStopped) {
            return;
        }

        String impressionUrl = mPubnativeAdModel.getBeacon(PubnativeBeacon.BeaconType.IMPRESSION);

        if (TextUtils.isEmpty(impressionUrl)) {

            invokeOnImpressionFailed(new MalformedURLException("Can not confirm impression, no Beacon URL found"));
        } else {

            PubnativeAPIRequest.send(PubnativeAPIRequest.Method.GET, impressionUrl, this);
        }
    }

    private void handleClickEvent() {

        Log.v(TAG, "handleClickEvent()");

        if (!TextUtils.isEmpty(mPubnativeAdModel.getClickUrl())) {

            URLOpener urlOpener = new URLOpener(mView.getContext());
            urlOpener.openInBackground(mPubnativeAdModel.getClickUrl(), true, new URLOpener.Listener() {

                @Override
                public void onURLOpenerStart(String url) {
                    // Do nothing
                }

                @Override
                public void onURLOpenerRedirect(String url) {
                    // Do nothing
                }

                @Override
                public void onURLOpenerFinish(String url) {
                    invokeOnClicked();
                }

                @Override
                public void onURLOpenerFailed(String url, Exception exception) {
                    invokeOnClickFailed(exception);
                }
            });

        } else {

            invokeOnClickFailed(new MalformedURLException("Can not open ad, no click_url found"));
        }
    }

    private void invokeOnImpressionFailed(final Exception exception) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if(mListener != null) {

                    mListener.onPubnativeAdModelImpressionFailed(mPubnativeAdModel, exception);
                }
            }
        });
    }

    private void invokeOnImpressionConfirmed() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if(mListener != null) {

                    mListener.onPubnativeAdModelImpressionConfirmed(mPubnativeAdModel, mView);
                }
            }
        });
    }

    private void invokeOnClicked() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if(mListener != null) {

                    mListener.onPubnativeAdModelClicked(mPubnativeAdModel, mClickableView);
                }
            }
        });
    }

    private void invokeOnClickFailed(final Exception exception) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if(mListener != null) {

                    mListener.onPubnativeAdModelClickFailed(mPubnativeAdModel, exception);
                }
            }
        });
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
