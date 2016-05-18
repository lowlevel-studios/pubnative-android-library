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

        PubnativeInterstitial interstitial = new PubnativeInterstitial(null, null);

        Assertions.assertThat(interstitial).isNotNull();

    }

    @Test
    public void loadInterstitial_withNullContext_invokeLoadFail() {

        PubnativeInterstitial interstitial = new PubnativeInterstitial(null, FAKE_APP_TOKEN);
        PubnativeInterstitial spyInterstitial = spy(interstitial);

        final Exception ex = mock(Exception.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(spyInterstitial).invokeLoadFail(any(Exception.class));
        spyInterstitial.load();

        verify(spyInterstitial).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadInterstitial_withEmptyAppToken_invokeLoadFail() {

        //when(TextUtils.isEmpty(any(CharSequence.class))).thenReturn(true);
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = new PubnativeInterstitial(activity, "");
        PubnativeInterstitial spyInterstitial = spy(interstitial);

        final Exception ex = mock(Exception.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                args[0] = ex;
                return null;
            }
        }).when(spyInterstitial).invokeLoadFail(any(Exception.class));

        spyInterstitial.load();

        verify(spyInterstitial).invokeLoadFail(eq(ex));

    }

    @Test
    public void loadIterstitial_whenReady_invokeLoadFinish() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = new PubnativeInterstitial(activity, FAKE_APP_TOKEN);
        PubnativeInterstitial spyInterstitial = spy(interstitial);
        when(spyInterstitial.isReady()).thenReturn(true);

        spyInterstitial.load();

        verify(spyInterstitial).invokeLoadFinish();

    }

}
