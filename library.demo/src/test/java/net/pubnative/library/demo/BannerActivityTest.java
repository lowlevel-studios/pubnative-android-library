package net.pubnative.library.demo;

import android.widget.Button;

import net.pubnative.library.banner.PubnativeBanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BannerActivityTest {

    private BannerActivity                           bannerActivity;
    private ExecutorService                          executor;

    @Before
    public void setup()  {
        executor = mock(ExecutorService.class);
        implementAsDirectExecutor(executor);
        bannerActivity = Robolectric.buildActivity(BannerActivity.class)
                                    .create().get();
    }

    protected void implementAsDirectExecutor(ExecutorService executor) {
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Exception {
                ((Runnable) invocation.getArguments()[0]).run();
                return null;
            }
        }).when(executor).submit(any(Runnable.class));
    }

    @Test
    public void checkActivity_shouldNotNull() throws Exception {
        assertNotNull(bannerActivity);
    }

}
