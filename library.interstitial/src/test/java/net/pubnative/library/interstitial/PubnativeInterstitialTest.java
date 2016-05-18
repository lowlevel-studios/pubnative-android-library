package net.pubnative.library.interstitial;

import android.app.Activity;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = 16)
@RunWith(RobolectricGradleTestRunner.class)
public class PubnativeInterstitialTest {

    public static final String FAKE_APP_TOKEN = "1234567890";


    @Test
    public void createInterstitial_withNullContext_returnsObject() {

        PubnativeInterstitial interstitial = new PubnativeInterstitial();

        Assertions.assertThat(interstitial).isNotNull();

    }

    @Test
    public void loadInterstitial_withNullContext_invokeLoadFail() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);

        final Exception ex = mock(Exception.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(interstitial).invokeLoadFail(any(Exception.class));
        interstitial.load(null, FAKE_APP_TOKEN);

        verify(interstitial).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadInterstitial_withEmptyAppToken_invokeLoadFail() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);

        final Exception ex = mock(Exception.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(interstitial).invokeLoadFail(any(Exception.class));

        interstitial.load(activity, "");

        verify(interstitial).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadIterstitial_whenReady_invokeLoadFinish() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        when(interstitial.isReady()).thenReturn(true);

        interstitial.load(activity, FAKE_APP_TOKEN);

        verify(interstitial).invokeLoadFinish();

    }

}
