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

    protected Handler                  mServiceHandler;
    protected Looper                   mServiceLooper;
    protected PubnativeBanner          mBanner;
    protected PubnativeBanner.Listener mListener;

    @Before
    public void setUp() {
        mBanner = spy(PubnativeBanner.class);
        mListener = mock(PubnativeBanner.Listener.class);
        mBanner.mListener = mListener;
    }

    @Test
    public void invokeLoadFail_shouldCallOnLoadFail() {

        HandlerThread serviceThread = new HandlerThread("[" + PubnativeBanner.class.getSimpleName() + "Thread]");
        serviceThread.start();
        mServiceLooper = serviceThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        mBanner.mHandler = mServiceHandler;
        Exception ex = new Exception();
        mBanner.invokeLoadFail(ex);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(serviceThread.getLooper());
        shadowLooper.idle();

        verify(mListener).onPubnativeBannerLoadFail(eq(mBanner), eq(ex));
    }

    @Test
    public void invokeLoadFinish_shouldCallOnLoadFinish() {

        HandlerThread serviceThread = new HandlerThread("[" + PubnativeBanner.class.getSimpleName() + "Thread]");
        serviceThread.start();
        mServiceLooper = serviceThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        mBanner.mHandler = mServiceHandler;
        mBanner.invokeLoadFinish();
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(serviceThread.getLooper());
        shadowLooper.idle();

        verify(mListener).onPubnativeBannerLoadFinish(eq(mBanner));
    }

    @Test
    public void invokeShow_shouldCallOnShow() {

        HandlerThread serviceThread = new HandlerThread("[" + PubnativeBanner.class.getSimpleName() + "Thread]");
        serviceThread.start();
        mServiceLooper = serviceThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        mBanner.mHandler = mServiceHandler;
        mBanner.invokeShow();
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(serviceThread.getLooper());
        shadowLooper.idle();

        verify(mListener).onPubnativeBannerShow(eq(mBanner));
    }

    @Test
    public void invokeImpressionConfirmed_shouldCallOnImpressionConfirmed() {

        HandlerThread serviceThread = new HandlerThread("[" + PubnativeBanner.class.getSimpleName() + "Thread]");
        serviceThread.start();
        mServiceLooper = serviceThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        mBanner.mHandler = mServiceHandler;
        mBanner.invokeImpressionConfirmed();
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(serviceThread.getLooper());
        shadowLooper.idle();

        verify(mListener).onPubnativeBannerImpressionConfirmed(eq(mBanner));
    }

    @Test
    public void invokeClick_shouldCallOnClick() {

        HandlerThread serviceThread = new HandlerThread("[" + PubnativeBanner.class.getSimpleName() + "Thread]");
        serviceThread.start();
        mServiceLooper = serviceThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        mBanner.mHandler = mServiceHandler;
        mBanner.invokeClick();
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(serviceThread.getLooper());
        shadowLooper.idle();

        verify(mListener).onPubnativeBannerClick(eq(mBanner));
    }

    @Test
    public void invokeHide_shouldCallOnHide() {

        HandlerThread serviceThread = new HandlerThread("[" + PubnativeBanner.class.getSimpleName() + "Thread]");
        serviceThread.start();
        mServiceLooper = serviceThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        mBanner.mHandler = mServiceHandler;
        mBanner.invokeHide();
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(serviceThread.getLooper());
        shadowLooper.idle();

        verify(mListener).onPubnativeBannerHide(eq(mBanner));
    }

    @Test
    public void loadBanner_withNullContext_returnsException() {

        final Exception ex = new Exception();
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(mBanner).invokeLoadFail(any(Exception.class));
        mBanner.load(null, FAKE_APP_TOKEN, null, null);

        verify(mBanner).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadBanner_withNotActivityContext_returnsException() {

        final Exception ex = new Exception();
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(mBanner).invokeLoadFail(any(Exception.class));
        mBanner.load(mMockContext, FAKE_APP_TOKEN, null, null);

        verify(mBanner).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadBanner_withEmptyAppToken_returnsException() {

        final Exception ex = new Exception();
        Context context = mock(Activity.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(mBanner).invokeLoadFail(any(Exception.class));
        mBanner.load(context, "", null, null);

        verify(mBanner).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadBanner_whenBannerReady_returnsException() {
        HandlerThread serviceThread = new HandlerThread("[" + PubnativeBanner.class.getSimpleName() + "Thread]");
        serviceThread.start();
        mServiceLooper = serviceThread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        mBanner.mHandler = mServiceHandler;
        Context context = mock(Activity.class);
        when(mBanner.isReady()).thenReturn(true);
        mBanner.load(context, FAKE_APP_TOKEN, null, null);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(serviceThread.getLooper());
        shadowLooper.idle();

        verify(mBanner).invokeLoadFinish();
    }

}