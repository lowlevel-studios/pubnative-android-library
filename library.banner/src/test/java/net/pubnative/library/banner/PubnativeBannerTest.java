package net.pubnative.library.banner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = 16)
@RunWith(RobolectricGradleTestRunner.class)
public class PubnativeBannerTest {

    @Test
    public void invokeLoadFail_shouldCallOnLoadFail() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;

        Handler handler = new Handler();

        banner.mHandler = handler;
        Exception ex = mock(Exception.class);
        banner.invokeLoadFail(ex);

        verify(listener).onPubnativeBannerLoadFail(eq(banner), eq(ex));
    }

    @Test
    public void invokeLoadFinish_shouldCallOnLoadFinish() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;

        Handler handler = new Handler();

        banner.mHandler = handler;
        banner.invokeLoadFinish();

        verify(listener).onPubnativeBannerLoadFinish(eq(banner));
    }

    @Test
    public void invokeShow_shouldCallOnShow() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;

        Handler handler = new Handler();

        banner.mHandler = handler;
        banner.invokeShow();

        verify(listener).onPubnativeBannerShow(eq(banner));
    }

    @Test
    public void invokeImpressionConfirmed_shouldCallOnImpressionConfirmed() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;

        Handler handler = new Handler();

        banner.mHandler = handler;
        banner.invokeImpressionConfirmed();

        verify(listener).onPubnativeBannerImpressionConfirmed(eq(banner));
    }

    @Test
    public void invokeClick_shouldCallOnClick() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;

        Handler handler = new Handler();

        banner.mHandler = handler;
        banner.invokeClick();

        verify(listener).onPubnativeBannerClick(eq(banner));
    }

    @Test
    public void invokeHide_shouldCallOnHide() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;

        Handler handler = new Handler();

        banner.mHandler = handler;
        banner.invokeHide();

        verify(listener).onPubnativeBannerHide(eq(banner));
    }

    @Test
    public void loadBanner_withNullContext_returnsException() {

        String appToken = "123456";

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.load(null, appToken, null, null);
        verify(banner).invokeLoadFail(any(Exception.class));

    }

    @Test
    public void loadBanner_withNotActivityContext_returnsException() {

        String appToken = "123456";
        Context context = mock(Application.class);

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.load(context, appToken, null, null);
        verify(banner).invokeLoadFail(any(Exception.class));

    }

    @Test
    public void loadBanner_withEmptyAppToken_returnsException() {

        String appToken = "";
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.load(activity, appToken, null, null);
        verify(banner).invokeLoadFail(any(Exception.class));

    }

    @Test
    public void loadBanner_whenBannerReady_returnsException() {

        String appToken = "123456";
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeBanner banner = spy(PubnativeBanner.class);
        when(banner.isReady()).thenReturn(true);
        banner.load(activity, appToken, null, null);

        verify(banner).invokeLoadFinish();
    }

}