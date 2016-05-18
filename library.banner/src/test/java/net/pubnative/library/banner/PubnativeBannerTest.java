package net.pubnative.library.banner;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = 16)
@RunWith(RobolectricGradleTestRunner.class)
public class PubnativeBannerTest {

    public static final String FAKE_APP_TOKEN = "1234567890";

    @Mock Context                      mMockContext;

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

        PubnativeBanner banner = spy(PubnativeBanner.class);

        final Exception ex = mock(Exception.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(banner).invokeLoadFail(any(Exception.class));
        banner.load(null, FAKE_APP_TOKEN, null, null);

        verify(banner).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadBanner_withNotActivityContext_returnsException() {

        PubnativeBanner banner = spy(PubnativeBanner.class);

        final Exception ex = mock(Exception.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(banner).invokeLoadFail(any(Exception.class));
        banner.load(mMockContext, FAKE_APP_TOKEN, null, null);

        verify(banner).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadBanner_withEmptyAppToken_returnsException() {

        PubnativeBanner banner = spy(PubnativeBanner.class);

        final Exception ex = mock(Exception.class);
        Context context = mock(Activity.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(banner).invokeLoadFail(any(Exception.class));
        banner.load(context, "", null, null);

        verify(banner).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadBanner_whenBannerReady_returnsException() {

        PubnativeBanner banner = spy(PubnativeBanner.class);

        Handler handler = new Handler();

        banner.mHandler = handler;
        Context context = mock(Activity.class);
        when(banner.isReady()).thenReturn(true);
        banner.load(context, FAKE_APP_TOKEN, null, null);

        verify(banner).invokeLoadFinish();
    }

}