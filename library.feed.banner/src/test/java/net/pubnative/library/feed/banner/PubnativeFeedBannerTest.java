package net.pubnative.library.feed.banner;

import android.os.Handler;

import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = net.pubnative.library.BuildConfig.class,
        sdk = 21)
public class PubnativeFeedBannerTest {
    @Test
    public void load_withNullContext_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.load(null, "app_token");

        verify(listener, times(1)).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void load_withInvalidAppToken_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.load(RuntimeEnvironment.application.getApplicationContext(), "");

        verify(listener, times(1)).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void load_withNullAppToken_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.load(RuntimeEnvironment.application.getApplicationContext(), null);

        verify(listener, times(1)).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestSuccess_withoutAds_invokesLoadFail() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        banner.onPubnativeRequestSuccess(request, null);
        verify(listener, times(1)).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestSuccess_withAds_invokesLoadFinish() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        banner.onPubnativeRequestSuccess(request, Arrays.asList(mock(PubnativeAdModel.class)));
        verify(listener, times(1)).onPubnativeFeedBannerLoadFinish(any(PubnativeFeedBanner.class));
    }

    @Test
    public void onPubnativeRequestFailed_invokesLoadFailed() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        banner.onPubnativeRequestFailed(request, mock(Exception.class));
        verify(listener, times(1)).onPubnativeFeedBannerLoadFailed(any(PubnativeFeedBanner.class), any(Exception.class));
    }

    @Test
    public void invokeLoadFail_withNullListener_shouldPass() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeLoadFail(mock(Exception.class));
    }

    @Test
    public void invokeLoadFail_withValidListener_invokesLoadFailed() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        Exception exception = mock(Exception.class);
        banner.invokeLoadFail(exception);

        verify(listener, times(1)).onPubnativeFeedBannerLoadFailed(eq(banner), eq(exception));
    }

    @Test
    public void invokeLoadFinish_withNullListener_shouldPass() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFinish_withValidListener_invokesLoadFinish() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.invokeLoadFinish();

        verify(listener, times(1)).onPubnativeFeedBannerLoadFinish(eq(banner));
    }

    @Test
    public void invokeShow_withNullListener_shouldPass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeShow();
    }

    @Test
    public void invokeShow_withValidListener_invokesShow() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeShow();

        verify(listener).onPubnativeFeedBannerShow(eq(banner));
    }

    @Test
    public void invokeImpressionConfirmed_withNullListener_shouldPass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeImpressionConfirmed();
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_invokesImpressionConfirmed() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeImpressionConfirmed();

        verify(listener).onPubnativeFeedBannerImpressionConfirmed(eq(banner));
    }

    @Test
    public void invokeClick_withNullListener_shouldPass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeClick();
    }

    @Test
    public void invokeClick_withValidListener_invokesClick() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeClick();

        verify(listener).onPubnativeFeedBannerClick(eq(banner));
    }
}