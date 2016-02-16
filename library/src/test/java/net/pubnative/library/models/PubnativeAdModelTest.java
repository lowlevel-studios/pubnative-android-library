package net.pubnative.library.models;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowView;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdModelTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

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

        View view = new View(new Activity());
        ShadowView adView = Shadows.shadowOf(view);

        model.startTracking(view, null);
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

        model.startTracking(null, listener);
        verify(listener, times(1)).onPubnativeAdModelImpressionFailed(eq(model), any(Exception.class));
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
