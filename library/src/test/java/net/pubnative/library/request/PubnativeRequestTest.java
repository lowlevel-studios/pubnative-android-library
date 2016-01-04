package net.pubnative.library.request;

import android.content.Context;

import com.android.volley.VolleyError;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.PubnativeTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
public class PubnativeRequestTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testParameterIsSet() {

        String           testKey   = "testKey";
        String           testValue = "testValue";
        PubnativeRequest request   = spy(PubnativeRequest.class);

        request.setParameter(testKey, testValue);

        assertThat(request.requestParameters.get(testKey)).isEqualTo(testValue);
    }

    @Test
    public void testWithNullParameters() {

        PubnativeRequest request = spy(PubnativeRequest.class);
        String           testKey = "testKey";

        request.setParameter(testKey, null);

        assertThat(request.requestParameters.containsKey(testKey)).isFalse();
    }

    @Test
    public void testParameterSize() {

        PubnativeRequest request = spy(PubnativeRequest.class);

        request.setParameter("test1", "1");
        request.setParameter("test2", "2");

        assertThat(request.requestParameters.size() == 2).isTrue();
    }

    @Test
    public void testDuplicateParametersOverridesValue() {

        String           testKey    = "testKey";
        String           testValue1 = "value1";
        String           testValue2 = "value2";
        PubnativeRequest request    = spy(PubnativeRequest.class);

        request.setParameter(testKey, testValue1);
        request.setParameter(testKey, testValue2);

        assertThat(request.requestParameters.size()).isEqualTo(1);
        assertThat(request.requestParameters.get(testKey)).isEqualTo(testValue2);
    }

    @Test
    public void testNetworkRequestInitiatedOnStart() {

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.listener = listener;
        request.endpoint = PubnativeRequest.Endpoint.NATIVE;
        request.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "test");

        request.start(this.applicationContext, PubnativeRequest.Endpoint.NATIVE, listener);

        verify(request, times(1)).setDefaultParameters();
        verify(request, times(1)).sendNetworkRequest();
    }

    @Test
    public void testStartWithNullEndpointFails() {

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "test");

        request.start(this.applicationContext, null, listener);

        verify(request, times(1)).invokeOnPubnativeRequestFailure(any(Exception.class));
    }

    @Test
    public void testStartWithNullContextFails() {

        PubnativeRequest.Listener listener = mock(PubnativeRequest.Listener.class);

        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "test");

        pubnativeRequest.start(null, PubnativeRequest.Endpoint.NATIVE, listener);

        verify(pubnativeRequest, times(1)).invokeOnPubnativeRequestFailure(any(Exception.class));
    }

    @Test
    public void testSetsUpDefaultParametersAutomatically() {

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.context = this.applicationContext;
        request.setDefaultParameters();

        assertThat(request.requestParameters.containsKey(PubnativeRequest.Parameters.BUNDLE_ID)).isTrue();
        assertThat(request.requestParameters.containsKey(PubnativeRequest.Parameters.OS)).isTrue();
        assertThat(request.requestParameters.containsKey(PubnativeRequest.Parameters.OS_VERSION)).isTrue();
        assertThat(request.requestParameters.containsKey(PubnativeRequest.Parameters.DEVICE_MODEL)).isTrue();
        assertThat(request.requestParameters.containsKey(PubnativeRequest.Parameters.DEVICE_RESOLUTION)).isTrue();
        assertThat(request.requestParameters.containsKey(PubnativeRequest.Parameters.DEVICE_TYPE)).isTrue();
        assertThat(request.requestParameters.containsKey(PubnativeRequest.Parameters.LOCALE)).isTrue();
    }

    @Test
    public void testRequestUrlValidity() {

        String           testKey   = "testKey";
        String           testValue = "testValue";
        PubnativeRequest request   = spy(PubnativeRequest.class);
        request.endpoint = PubnativeRequest.Endpoint.NATIVE;
        request.setParameter(testKey, testValue);

        String url = request.getRequestURL();
        assertThat(url).isNotNull();
        assertThat(url).isNotEmpty();
        assertThat(url).startsWith(PubnativeRequest.BASE_URL);
        assertThat(url).contains(testKey);
    }

    @Test
    public void testInvalidEndpointReturnsNullURL() {

        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.endpoint = null;

        String url = pubnativeRequest.getRequestURL();

        assertThat(url).isNull();
    }

    @Test
    public void testOnResponseSuccess() {

        String response = PubnativeTestUtils.getResponseJSON("success.json");

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.listener = listener;

        request.onResponse(response);

        verify(listener, times(1)).onPubnativeRequestSuccess(eq(request), any(List.class));
    }

    @Test
    public void testOnResponseWithInvalidData() {

        String response = PubnativeTestUtils.getResponseJSON("failure.json");

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.listener = listener;

        request.onResponse(response);

        verify(listener, times(1)).onPubnativeRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void testOnResponseWithNullData() {

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.listener = listener;

        request.onResponse(null);

        verify(listener, times(1)).onPubnativeRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void testOnErrorResponseFromVolley() {

        VolleyError               error    = mock(VolleyError.class);
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.listener = listener;

        request.onErrorResponse(error);
        verify(listener, times(1)).onPubnativeRequestFail(eq(request), eq(error));
    }
}
