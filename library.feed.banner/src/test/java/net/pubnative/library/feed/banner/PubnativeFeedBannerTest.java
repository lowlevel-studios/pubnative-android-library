package net.pubnative.library.feed.banner;

import net.pubnative.library.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = net.pubnative.library.BuildConfig.class,
        sdk = 21)
public class PubnativeFeedBannerTest {
    @Test
    public void load_withNullContext_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.load(null, "app_token", listener);
    }

    @Test
    public void load_withInvalidAppToken_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.load(RuntimeEnvironment.application.getApplicationContext(), "", listener);
    }

    @Test
    public void load_withoutListener_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", null);
    }
}