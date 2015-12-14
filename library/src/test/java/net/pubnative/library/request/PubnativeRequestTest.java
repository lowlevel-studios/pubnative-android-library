package net.pubnative.library.request;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.PubnativeTestUtils;
import net.pubnative.library.models.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class PubnativeRequestTest {

    @Test
    public void testParameterIsSet() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.setParameter("ad_count", "1");
        assertThat(pubnativeRequest.requestParameters.get("ad_count")).isEqualTo("1");
    }

    @Test
    public void testWithNullParameters() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.setParameter("banner_size", null);
        assertThat(pubnativeRequest.requestParameters.containsKey("banner_size")).isFalse();
    }

    @Test
    public void testParameterSize() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.setParameter("ad_count", "1");
        pubnativeRequest.setParameter("icon_size", "10x10");
        assertThat(pubnativeRequest.requestParameters.size() == 2).isTrue();
    }

    @Test
    public void testDuplicateParameters() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.setParameter("ad_count", "4");
        assertThat(pubnativeRequest.requestParameters.containsKey("ad_count")).isTrue();
        pubnativeRequest.setParameter("ad_count", "5");
        assertThat(pubnativeRequest.requestParameters.get("ad_count")).isEqualTo("5");
    }

    @Test
    public void testNetworkRequestInitiatedOnStart() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        pubnativeRequest.listener = listener;
        pubnativeRequest.endpoint = PubnativeRequest.Endpoint.NATIVE;
        pubnativeRequest.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "7e746627-ebae-4c87-aea6-e7554798f0fe");
        pubnativeRequest.start(context, PubnativeRequest.Endpoint.NATIVE, listener);
        verify(pubnativeRequest, times(1)).setOptionalParameters();
        verify(pubnativeRequest, times(1)).sendNetworkRequest();
    }


    @Test
    public void testStartWithNullEndpoint()
    {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        pubnativeRequest.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "7e746627-ebae-4c87-aea6-e7554798f0fe");
        pubnativeRequest.start(context, null, listener);
        verify(pubnativeRequest, times(1)).invokeOnPubnativeRequestFailure(any(Exception.class));
    }


    @Test
    public void testStartWithNullContext()
    {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        PubnativeRequest.Listener listener = Mockito.mock(PubnativeRequest.Listener.class);
        pubnativeRequest.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "7e746627-ebae-4c87-aea6-e7554798f0fe");
        pubnativeRequest.start(null, PubnativeRequest.Endpoint.NATIVE, listener);
        verify(pubnativeRequest, times(1)).invokeOnPubnativeRequestFailure(any(Exception.class));
    }


    @Test
    public void testOptionalParameters() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.setParameter("ad_count", "1");
        pubnativeRequest.context = context;
        pubnativeRequest.setOptionalParameters();
        assertThat(pubnativeRequest.requestParameters.containsKey("ad_count")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("os")).isTrue();
    }


    @Test
    public void testResquestUrl() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.endpoint = PubnativeRequest.Endpoint.NATIVE;
        pubnativeRequest.setParameter("ad_count", "1");
        pubnativeRequest.context = context;
        String url = pubnativeRequest.getRequestURL();
        assertThat(url).isNotNull();
        assertThat(url).isNotEmpty();
        assertThat(url).startsWith(PubnativeRequest.BASE_URL);
        assertThat(url).contains("ad_count");
    }


    @Test
    public void testInvalidEndpoint()
    {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.endpoint = null;       // Invalid end point
        pubnativeRequest.listener = Mockito.mock(PubnativeRequest.Listener.class);
        pubnativeRequest.context = context;
        String url = pubnativeRequest.getRequestURL();
        verify(pubnativeRequest, times(1)).invokeOnPubnativeRequestFailure(any(Exception.class));
    }

    @Test
    public void testOnAndroidIdTaskFinished() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.requestParameters = spy(HashMap.class);
        pubnativeRequest.onAndroidAdIdTaskFinished("1234");
        assertThat(pubnativeRequest.requestParameters.containsValue("1234")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("android_advertiser_id")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("android_advertiser_id_sha1")).isTrue();
        assertThat(pubnativeRequest.requestParameters.containsKey("android_advertiser_id_md5")).isTrue();
        verify(pubnativeRequest, times(1)).sendNetworkRequest();
    }


    @Test
    public void testAdvertisingIdResponseWithNullAdId() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.requestParameters = spy(HashMap.class);
        pubnativeRequest.onAndroidAdIdTaskFinished(null);
        assertThat(pubnativeRequest.requestParameters.containsKey("no_user_id")).isTrue();
        verify(pubnativeRequest, times(1)).sendNetworkRequest();
    }

    @Test
    public void testOnResponseSuccess() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        pubnativeRequest.listener = Mockito.mock(PubnativeRequest.Listener.class);
        String response = PubnativeTestUtils.getResponseJSON("success.json");
        pubnativeRequest.onResponse(response);
        verify(pubnativeRequest.listener, times(1)).onPubnativeRequestSuccess(any(PubnativeRequest.class), any(List.class));
    }

    @Test
    public void testOnResponseWithInvalidData()
    {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        pubnativeRequest.listener = Mockito.mock(PubnativeRequest.Listener.class);
        String response = PubnativeTestUtils.getResponseJSON("failure.json");
        pubnativeRequest.onResponse(response);
        verify(pubnativeRequest.listener, times(1)).onPubnativeRequestFail(any(PubnativeRequest.class), any(Exception.class));
    }

    @Test
    public void testOnResponseWithNullData()
    {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;
        pubnativeRequest.listener = Mockito.mock(PubnativeRequest.Listener.class);
        pubnativeRequest.onResponse(null);
        verify(pubnativeRequest.listener, times(1)).onPubnativeRequestFail(any(PubnativeRequest.class), any(Exception.class));
    }

    @Test
    public void testValidResponseParsing() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        String response = PubnativeTestUtils.getResponseJSON("success.json");
        List<PubnativeAdModel> ads = pubnativeRequest.parseResponse(response);
        assertThat(ads).isNotEmpty();
    }

    @Test
    public void testInvalidResponseParsing() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        String response = PubnativeTestUtils.getResponseJSON("failure.json");
        List<PubnativeAdModel> ads = pubnativeRequest.parseResponse(response);
        assertThat(ads).isNull();

    }

    @Test
    public void testNullResponseParsing() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        List<PubnativeAdModel> ads = pubnativeRequest.parseResponse(null);
        assertThat(ads).isNull();
    }

    @Test
    public void testPrepareExceptionFromErrorJson() {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        Context context = RuntimeEnvironment.application;
        pubnativeRequest.context = context;

        String response = PubnativeTestUtils.getResponseJSON("failure.json");
        Exception exception = pubnativeRequest.getResponseException(response, 100);
        assertThat(exception.getMessage()).isEqualTo("error Invalid app token100");

        exception = pubnativeRequest.getResponseException("test", 100);
        assertThat(exception.getMessage()).isEqualTo("Value test of type java.lang.String cannot be converted to JSONObject");

        exception = pubnativeRequest.getResponseException(null, 100);
        assertThat(exception.getMessage()).isEqualTo("Data is null");
    }

    @Test
    public void testErrorResponseFromVolley()
    {
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        VolleyError error = new VolleyError(new NetworkResponse(400,new byte[2], new HashMap<String, String>(),false));
        pubnativeRequest.onErrorResponse(error);
        verify(pubnativeRequest).invokeOnPubnativeRequestFailure(any(Exception.class));
    }
}
