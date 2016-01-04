package net.pubnative.library.models;

import net.pubnative.library.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdModelTest
{
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
        model.beacons = mock(List.class);

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
        model.beacons = beacons;

        String url = model.getBeacon(testType);

        assertThat(url).isEqualTo(testValue);
    }
}
