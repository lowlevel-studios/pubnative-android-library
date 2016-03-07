package net.pubnative.library.tracking;

import android.content.Context;

import junit.framework.Assert;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.tracking.model.PubnativeTrackingURLModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeTrackingManagerTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testWithNullContext() {

        PubnativeTrackingManager.track(null, "www.google.com");
    }

    @Test
    public void testWithEmptyUrl() {

        PubnativeTrackingManager.track(applicationContext, "");
        List<PubnativeTrackingURLModel> urlModelList = PubnativeTrackingManager.getList(applicationContext, "pending");

        Assert.assertTrue(urlModelList.size() == 0);
    }

    @Test
    public void checkItemEnqued() {

        PubnativeTrackingManager.setList(applicationContext, "pending", null);

        PubnativeTrackingURLModel model = new PubnativeTrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();

        PubnativeTrackingManager.enqueueItem(applicationContext, "pending", model);

        List<PubnativeTrackingURLModel> urlModelList = PubnativeTrackingManager.getList(applicationContext, "pending");

        Assert.assertTrue(urlModelList.size() > 0);
    }

    @Test
    public void checkItemdDequeued() {

        PubnativeTrackingManager.setList(applicationContext, "pending", null);

        PubnativeTrackingURLModel model = new PubnativeTrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();

        PubnativeTrackingManager.enqueueItem(applicationContext, "pending", model);

        PubnativeTrackingURLModel dequedItem = PubnativeTrackingManager.dequeueItem(applicationContext, "pending");

        Assert.assertTrue(model.url.equalsIgnoreCase(dequedItem.url) && model.startTimestamp == dequedItem.startTimestamp);
    }
}
