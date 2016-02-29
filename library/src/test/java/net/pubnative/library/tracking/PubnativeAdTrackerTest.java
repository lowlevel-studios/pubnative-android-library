package net.pubnative.library.tracking;

import android.content.Context;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdTrackerTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }


    @Test
    public void testImpressionFailureForNullListener() {

        View                        adView      = spy(new View(applicationContext));
        PubnativeAdTracker          tracker     = spy(new PubnativeAdTracker(adView, adView, "", "", null));

        tracker.startImpressionRequest();
    }

    @Test
    public void testImpressionSuccessForNullListener() {

        View                        adView      = spy(new View(applicationContext));
        PubnativeAdTracker          tracker     = spy(new PubnativeAdTracker(adView, adView, "", "", null));

        tracker.invokeOnTrackerImpression();
    }

    @Test
    public void testOnImpressionConfirmed() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));
        PubnativeAdTracker.Listener listener        = mock(PubnativeAdTracker.Listener.class);

        PubnativeAdTracker          tracker         = spy(new PubnativeAdTracker(adView, clickableView, "", "", listener));

        tracker.invokeOnTrackerImpression();
        verify(listener, times(1)).onTrackerImpression(eq(adView));
    }

    @Test
    public void testInvalidClickUrl() {

        View                        adView      = spy(new View(applicationContext));
        PubnativeAdTracker.Listener listener    = mock(PubnativeAdTracker.Listener.class);

        PubnativeAdTracker          tracker     = spy(new PubnativeAdTracker(adView, adView, "", "", listener));

        tracker.handleClickEvent();
    }

    @Test
    public void testValidClickListener() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));

        PubnativeAdTracker          tracker         = spy(new PubnativeAdTracker(adView, clickableView, "", "", null));

        verify(clickableView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testInValidClickListener() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));

        PubnativeAdTracker          tracker         = spy(new PubnativeAdTracker(adView, clickableView, "", "", null));

        verify(adView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testOnClickConfirmed() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));
        PubnativeAdTracker.Listener listener        = mock(PubnativeAdTracker.Listener.class);

        PubnativeAdTracker          tracker         = spy(new PubnativeAdTracker(adView, clickableView, "", "", listener));

        tracker.invokeOnTrackerClick();
        verify(listener, times(1)).onTrackerClick(eq(clickableView));
    }
}
