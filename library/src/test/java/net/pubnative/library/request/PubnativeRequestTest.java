package net.pubnative.library.request;

import android.content.Context;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.PubnativeTestUtils;
import net.pubnative.library.models.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,sdk = 19)
public class PubnativeRequestTest {


    @Test
    public void testSetParameter() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.setParameter("ad_count","1");
        pubnativeRequest.setParameter("icon_size", "10x10");
        assertThat(pubnativeRequest.requestParameters.size() == 2).isTrue();

        pubnativeRequest.setParameter("ad_count", "1");
        assertThat(pubnativeRequest.requestParameters.size() == 2).isTrue();

        pubnativeRequest.setParameter("ad_count", "4");
        assertThat(pubnativeRequest.requestParameters.containsKey("ad_count")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsValue("4")).isTrue();

        pubnativeRequest.setParameter("banner_size", null);
        assertThat(pubnativeRequest.requestParameters.containsKey("banner_size")).isFalse();
    }

    @Test
    public void testStart() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        pubnativeRequest.listener = listener;

        pubnativeRequest.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "7e746627-ebae-4c87-aea6-e7554798f0fe");
        when(pubnativeRequest.createNativeRequest()).thenReturn("Request is started");
        when(pubnativeRequest.createNetworkRequest()).thenReturn("Request is started");

        pubnativeRequest.start(context, PubnativeRequest.Endpoint.NATIVE, listener);

        verify(pubnativeRequest, times(1)).setOptionalParameters();
        verify(pubnativeRequest, times(1)).createNetworkRequest();
        verify(pubnativeRequest, times(1)).sendNetworkRequest("Request is started");
    }

    @Test
    public void testSetOptionalParameters() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.setParameter("ad_count", "1");
        pubnativeRequest.context = context;
        pubnativeRequest.setOptionalParameters();

        assertThat(pubnativeRequest.requestParameters.containsKey("ad_count")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("os")).isTrue();
    }

    @Test
    public void testCreateNetworkRequest() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;

        pubnativeRequest.endpoint = PubnativeRequest.Endpoint.NATIVE;
        pubnativeRequest.setParameter("ad_count", "1");
        pubnativeRequest.context = context;

        String url = pubnativeRequest.createNetworkRequest();

        verify(pubnativeRequest, times(1)).createNativeRequest();
        assertThat(url).isNotNull();
        assertThat(url).isNotEmpty();
    }

    @Test
    public void testCreateNativeRequest() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);

        pubnativeRequest.setParameter("ad_count", "1");
        String url = pubnativeRequest.createNativeRequest();

        assertThat(url.equals("http://api.pubnative.net/api/partner/v2/promotions/native?ad_count=1")).isTrue();
    }

    @Test
    public void testOnAndroidIdTaskFinished(){
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;

        pubnativeRequest.setParameter("ad_count", "1");
        pubnativeRequest.onAndroidAdIdTaskFinished("1234");

        assertThat(pubnativeRequest.requestParameters.containsValue("1234")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("android_advertiser_id")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("android_advertiser_id_sha1")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("android_advertiser_id_md5")).isTrue();
        verify(pubnativeRequest, times(1)).sendNetworkRequest(anyString());

        pubnativeRequest.onAndroidAdIdTaskFinished("");
        assertThat(pubnativeRequest.requestParameters.containsKey("no_user_id")).isTrue();

    }

    @Test
    public void testOnResponse(){
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        String response = PubnativeTestUtils.getResponseJSON("success.json");
        pubnativeRequest.listener = Mockito.mock(PubnativeRequest.Listener.class);

        pubnativeRequest.onResponse(response);

        verify(pubnativeRequest, times(1)).parseResponse(response);

    }

    @Test
    public void testParseResponse() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        String response = PubnativeTestUtils.getResponseJSON("success.json");

        List<PubnativeAdModel> ads = pubnativeRequest.parseResponse(response);
        assertThat(ads.size()).isEqualTo(1);
    }

    @Test
    public void testPrepareExceptionFromErrorJson() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;

        String response = PubnativeTestUtils.getResponseJSON("failure.json");
        Exception exception = pubnativeRequest.prepareExceptionFromErrorJson(response, 100);
        assertThat(exception.getMessage().equals("error Invalid app token100")).isTrue();

        exception = pubnativeRequest.prepareExceptionFromErrorJson("test", 100);
        assertThat(exception.getMessage().equals("Value test of type java.lang.String cannot be converted to JSONObject")).isTrue();

        exception = pubnativeRequest.prepareExceptionFromErrorJson(null, 100);
        assertThat(exception.getMessage().equals("Data is null")).isTrue();
    }
}
