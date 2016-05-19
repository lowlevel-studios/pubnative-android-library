package net.pubnative.library.interstitial;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = 16)
@RunWith(RobolectricGradleTestRunner.class)
public class PubnativeInterstitialTest {

    @Test
    public void loadInterstitial_withNullContext_invokeLoadFail() {

        String appToken = "123456";

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.mListener = spy(PubnativeInterstitial.Listener.class);
        interstitial.load(null, appToken);
        verify(interstitial).invokeLoadFail(any(Exception.class));

    }

    @Test
    public void loadInterstitial_withEmptyAppToken_invokeLoadFail() {

        String appToken = "";
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.mListener = spy(PubnativeInterstitial.Listener.class);
        interstitial.load(activity, appToken);
        verify(interstitial).invokeLoadFail(any(Exception.class));

    }

    @Test
    public void loadIterstitial_whenReady_invokeLoadFinish() {

        String appToken = "123456";
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.mListener = spy(PubnativeInterstitial.Listener.class);

        when(interstitial.isReady()).thenReturn(true);

        interstitial.load(activity, appToken);
        verify(interstitial).invokeLoadFinish();

    }

}
