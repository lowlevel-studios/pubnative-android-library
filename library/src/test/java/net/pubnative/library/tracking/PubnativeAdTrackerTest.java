package net.pubnative.library.tracking;

import android.app.Activity;
import android.view.View;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.models.PubnativeAdModel;
import net.pubnative.library.models.PubnativeBeacon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdTrackerTest {

    @Test
    public void testImpressionFailureForInvalidImpressionURL() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = spy(PubnativeAdModel.Listener.class);

        PubnativeBeacon             beacon      = new PubnativeBeacon();
        beacon.type = "impression";
        beacon.url = "";

        List<PubnativeBeacon> beacons = new ArrayList<PubnativeBeacon>();
        beacons.add(beacon);

        model.setBeacons(beacons);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, listener, model));

        tracker.startImpressionRequest();

        verify(listener, times(1)).onPubnativeAdModelImpressionFailed(eq(model), any(IllegalArgumentException.class));
    }

    @Test
    public void testImpressionSuccess() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = spy(PubnativeAdModel.Listener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, listener, model));

        tracker.invokeOnResponse("");

        verify(listener, times(1)).onPubnativeAdModelImpressionConfirmed(eq(model), eq(adView));
    }

    @Test
    public void testImpressionFailureForNullListener() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);

        PubnativeBeacon             beacon      = new PubnativeBeacon();
        beacon.type = "impression";
        beacon.url = "";

        List<PubnativeBeacon> beacons = new ArrayList<PubnativeBeacon>();
        beacons.add(beacon);

        model.setBeacons(beacons);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, null, model));

        tracker.startImpressionRequest();
    }

    @Test
    public void testImpressionSuccessForNullListener() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        PubnativeAdTracker tracker = spy(new PubnativeAdTracker(adView, adView, null, model));

        tracker.invokeOnResponse("");
    }
}
