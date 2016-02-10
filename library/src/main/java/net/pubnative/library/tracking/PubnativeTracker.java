package net.pubnative.library.tracking;

import android.view.View;
import android.view.ViewTreeObserver;

import net.pubnative.library.models.PubnativeAdModel;
import net.pubnative.library.utils.SystemUtils;

public class PubnativeTracker {

    private PubnativeAdModel.Listener       mListener;
    private View                            mView;
    private View                            mClickableView;
    private float                           VISIBILTY_THRESHOLD   =   0.50f;
    private ViewTreeObserver                viewTreeObserver;

    public PubnativeTracker(View view, View clickableView, PubnativeAdModel.Listener listener) {

        mListener = listener;

        mView = view;
        mClickableView = clickableView;
        viewTreeObserver = mView.getViewTreeObserver();

        startTracking();
    }

    private void startTracking() {

        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
        viewTreeObserver.addOnScrollChangedListener(onScrollChangedListener);

        mClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeOnClicked();
            }
        });
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (SystemUtils.isVisibleOnScreen(mView, VISIBILTY_THRESHOLD)) {
                invokeOnImpressionConfirmed();
                viewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
                viewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener);
            }
        }
    };

    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            if (SystemUtils.isVisibleOnScreen(mView, VISIBILTY_THRESHOLD)) {
                invokeOnImpressionConfirmed();
                viewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
                viewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener);
            }
        }
    };

    private void inovkeOnImpressionFalied(Exception exception) {

        if(mListener != null) {

            mListener.onPubnativeAdModelImpressionFailed(exception);
        }
    }

    private void invokeOnImpressionConfirmed() {
        if(mListener != null) {

            mListener.onPubnativeAdModelImpressionConfirmed();
        }
    }

    private void invokeOnClicked() {
        if(mListener != null) {

            mListener.onPubnativeAdModelImpressionConfirmed();
        }
    }

    private void invokeOnClickFailed(Exception exception) {
        if(mListener != null) {

            mListener.onPubnativeAdModelImpressionConfirmed();
        }
    }
}
