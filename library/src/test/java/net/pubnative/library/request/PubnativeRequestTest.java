package net.pubnative.library.request;

import android.content.Context;

import junit.framework.Assert;
import net.pubnative.library.BuildConfig;
import net.pubnative.library.MyRobolectricTestRunner;
import net.pubnative.library.models.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MyRobolectricTestRunner.class)
@Config(constants = BuildConfig.class,sdk = 19)
public class PubnativeRequestTest {

    @Test
    public void testPunnativeRequestIsNotNull() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Assert.assertNotNull("Pubnative request is not null",pubnativeRequest);
    }

    @Test
    public void testContextIsNotNull() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        Assert.assertNotNull("Pubnative Request context is not null", pubnativeRequest.context);
    }

    @Test
    public void testRequestParametersIsNotNull() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Map<String, String> requestParametrs = spy(Map.class);
        pubnativeRequest.requestParameters = requestParametrs;
        Assert.assertNotNull("Pubnative Request requestParameters is not null", pubnativeRequest.requestParameters);
    }

    @Test
    public void testListenerIsNotNull() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        pubnativeRequest.listener = listener;
        Assert.assertNotNull("Pubnative Request listener is not null", pubnativeRequest.listener);
    }

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
        assertThat(!pubnativeRequest.requestParameters.containsKey("banner_size")).isTrue();
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

        pubnativeRequest.start(context, PubnativeRequest.EndPoint.NATIVE, listener);

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

        pubnativeRequest.endpoint = PubnativeRequest.EndPoint.NATIVE;
        pubnativeRequest.setParameter("ad_count", "1");
        pubnativeRequest.context = context;

        when(pubnativeRequest.createNativeRequest()).thenReturn("Network request is created");
        String url = pubnativeRequest.createNetworkRequest();

        verify(pubnativeRequest, times(1)).createNativeRequest();
        assertThat(url.equals("Network request is created")).isTrue();
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

        pubnativeRequest.onAndroidAdIdTaskFinished("");
        assertThat(pubnativeRequest.requestParameters.containsKey("no_user_id")).isTrue();
    }

    @Test
    public void testOnResponse(){
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        String response = loadJsonResponse("responseSuccess.json", pubnativeRequest.context);
        pubnativeRequest.onResponse(response);

        verify(pubnativeRequest, times(1)).parseResponse(response);
    }

    @Test
    public void testParseResponse() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        String response = loadJsonResponse("responseSuccess.json", pubnativeRequest.context);

        List<PubnativeAdModel> ads = pubnativeRequest.parseResponse(response);
        assertThat(ads.size() == 1).isTrue();
    }

    @Test
    public void testPrepareExceptionFromErrorJson() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;

        String response = loadJsonResponse("responseFailure.json", pubnativeRequest.context);
        Exception exception = pubnativeRequest.prepareExceptionFromErrorJson(response, 100);
        assertThat(exception.getMessage().equals("error Invalid app token100")).isTrue();

        exception = pubnativeRequest.prepareExceptionFromErrorJson("test", 100);
        assertThat(exception.getMessage().equals("Value test of type java.lang.String cannot be converted to JSONObject")).isTrue();

        exception = pubnativeRequest.prepareExceptionFromErrorJson(null, 100);
        assertThat(exception.getMessage().equals("Data is null")).isTrue();
    }

    private String loadJsonResponse(String path, Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
