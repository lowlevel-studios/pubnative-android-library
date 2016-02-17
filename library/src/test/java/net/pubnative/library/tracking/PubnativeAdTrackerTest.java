package net.pubnative.library.tracking;

import android.app.Activity;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdTrackerTest {

    @Test
    public void testImpressionFailureForInvalidImpressionURL() {

        PubnativeAdTracker.Listener listener    = spy(PubnativeAdTracker.Listener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, "", "", listener));

        tracker.startImpressionRequest();

        verify(listener, times(1)).onImpressionFailed(any(Exception.class));
    }

    @Test
    public void testImpressionSuccess() {

        PubnativeAdTracker.Listener listener    = spy(PubnativeAdTracker.Listener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, "", "", listener));

        tracker.invokeOnResponse("");

        verify(listener, times(1)).onImpressionConfirmed();
    }

    @Test
    public void testImpressionFailureForNullListener() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, "", "", null));

        tracker.startImpressionRequest();
    }

    @Test
    public void testImpressionSuccessForNullListener() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, "", "", null));

        tracker.invokeOnResponse("");
    }
}
