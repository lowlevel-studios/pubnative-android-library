package net.pubnative.library.models;

import android.app.Activity;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdModelTest {

    @Test
    public void testGetBeaconWithNullValueReturnsNull() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);

        String url = model.getBeacon(null);

        assertThat(url).isNull();
    }

    @Test
    public void testGetBeaconWithEmptyValueReturnsNull() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);

        String url = model.getBeacon("");

        assertThat(url).isNull();
    }

    @Test
    public void testGetBeaconWithNotContainedValueReturnsNull() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.setBeacons(mock(List.class));

        String url = model.getBeacon("");

        assertThat(url).isNull();
    }

    @Test
    public void testGetBeaconWitContainedValueReturnsBeacon() {

        String testType = "type";
        String testValue = "value";

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        PubnativeBeacon beacon = spy(PubnativeBeacon.class);
        beacon.type = testType;
        beacon.url = testValue;

        List<PubnativeBeacon> beacons = new ArrayList<PubnativeBeacon>();
        beacons.add(beacon);
        model.setBeacons(beacons);

        String url = model.getBeacon(testType);

        assertThat(url).isEqualTo(testValue);
    }

    @Test
    public void testStartTrackingWithValidListener() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = spy(PubnativeAdModel.Listener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        model.startTracking(adView, listener);
    }

    @Test
    public void testStartTrackingWithNullListener() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        model.startTracking(adView, null);
    }

    @Test
    public void testStartTrackingWithClickableView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = spy(PubnativeAdModel.Listener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);
        View clickableView = new View(activity);

        model.startTracking(adView, clickableView, listener);
    }

    @Test
    public void testStartTrackingWithoutClickableView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = spy(PubnativeAdModel.Listener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        model.startTracking(adView, listener);
    }

    @Test
    public void testStartTrackingWithNullView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = spy(PubnativeAdModel.Listener.class);

        try {
            model.startTracking(null, listener);
        } catch (Exception ex) {
            assertThat(ex.getClass()).isEqualTo(NullPointerException.class);
        }
    }

    @Test
    public void testStartTrackingWithNullClickableView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = spy(PubnativeAdModel.Listener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View adView = new View(activity);

        try {
            model.startTracking(adView, null, listener);
        } catch (Exception ex) {
            assertThat(ex.getClass()).isEqualTo(NullPointerException.class);
        }
    }
}
