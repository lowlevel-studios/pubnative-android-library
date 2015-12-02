package net.pubnative.library.request;

import android.content.Context;

import com.android.volley.VolleyError;

import junit.framework.Assert;
import net.pubnative.library.BuildConfig;
import net.pubnative.library.model.PubnativeAdModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,sdk = 19)
public class PubnativeRequestTest {
    private PubnativeRequest mPubnativeRequest;
    private Context mContext;
    private Map<String, String> mRequestParameters;
    private PubnativeRequest.Listener mListener;
    private String nativeSuccessUrl = "http://api.applift.com/api/partner/v2/promotions/native?app_token=bed5dfea7feb694967a8755bfa7f67fdf1ceb9c291ddd7b8825983a103c8b266&icon_size=150x150&country=DE&os=android&device_model=iphone&os_version=3&ad_count=1";
    private String failureUrl = "http://api.applift.com/api/partner/v2/promotions/native?icon_size=150x150&country=DE&os=android&device_model=iphone&os_version=3&ad_count=1";


    @Before
    public void setUp() {
        mPubnativeRequest = spy(PubnativeRequest.class);
        mContext = RuntimeEnvironment.application;
        mRequestParameters = spy(Map.class);
        mListener = spy(PubnativeRequest.Listener.class);
    }

    @After
    public void tearDown() {
        mPubnativeRequest = null;
        mContext = null;
        mRequestParameters = null;
        mListener = null;
    }

    @Test
    public void testPunnativeRequestIsNotNull() {
        Assert.assertNotNull("Pubnative request is not null",mPubnativeRequest);
    }

    @Test
    public void testContextIsNotNull() {
        mPubnativeRequest.context = mContext;
        Assert.assertNotNull("Pubnative Request context is not null", mPubnativeRequest.context);
    }

    @Test
    public void testRequestParametersIsNotNull() {
        mPubnativeRequest.requestParameters = mRequestParameters;
        Assert.assertNotNull("Pubnative Request requestParameters is not null", mPubnativeRequest.requestParameters);
    }

    @Test
    public void testListenerIsNotNull() {
        mPubnativeRequest.listener = mListener;
        Assert.assertNotNull("Pubnative Request listener is not null", mPubnativeRequest.listener);
    }

    @Test
    public void testSetParameter() {
        mPubnativeRequest.setParameter("ad_count","1");
        mPubnativeRequest.setParameter("icon_size", "10x10");
        assertThat(mPubnativeRequest.requestParameters.size() == 2);

        mPubnativeRequest.setParameter("ad_count", "1");
        assertThat(mPubnativeRequest.requestParameters.size() == 2);

        mPubnativeRequest.setParameter("banner_size", null);
        assertThat(!mPubnativeRequest.requestParameters.containsKey("banner_size"));
    }

    @Test
    public void testStart() {
        mPubnativeRequest.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "7e746627-ebae-4c87-aea6-e7554798f0fe");

        when(mPubnativeRequest.createNativeRequest()).thenReturn("Request is started");

        mPubnativeRequest.start(mContext, PubnativeRequest.EndPoint.NATIVE, mListener);

        verify(mPubnativeRequest, times(1)).setOptionalParameters();
        verify(mPubnativeRequest, times(1)).createNetworkRequest();
        verify(mPubnativeRequest, times(1)).sendNetworkRequest("Request is started");
    }

    @Test
    public void testSetOptionalParameters() {
        mPubnativeRequest.setParameter("ad_count", "1");
        mPubnativeRequest.context = mContext;
        mPubnativeRequest.setOptionalParameters();
        assertThat(mPubnativeRequest.requestParameters.size() == 8);
    }

    @Test
    public void testCreateNetworkRequest() {
        mPubnativeRequest.endpoint = PubnativeRequest.EndPoint.NATIVE;
        mPubnativeRequest.setParameter("ad_count", "1");
        mPubnativeRequest.context = mContext;

        when(mPubnativeRequest.createNativeRequest()).thenReturn("Network request is created");
        doNothing().when(mPubnativeRequest).sendNetworkRequest(nativeSuccessUrl);
        mPubnativeRequest.createNetworkRequest();

        verify(mPubnativeRequest, times(1)).createNativeRequest();
        verify(mPubnativeRequest, times(1)).sendNetworkRequest("Network request is created");
    }

    @Test
    public void testCreateNativeRequest() {
        mPubnativeRequest.setParameter("ad_count", "1");
        String url = mPubnativeRequest.createNativeRequest();

        assertThat(url.equals("http://api.pubnative.net/api/partner/v2/promotions/native?ad_count=1"));
    }

    @Test
    public void testSendNetworkRequestSuccessResponse() {
        mPubnativeRequest.context = mContext;

        //String response = loadJsonResponse("responseSuccess.json");
        doNothing().when(mPubnativeRequest).onResponse("response");
        mPubnativeRequest.sendNetworkRequest(nativeSuccessUrl);

        //verify(mPubnativeRequest).onResponse("response");
    }

    @Test
    public void testSendNetworkRequestErrorResponse() {
        mPubnativeRequest.context = mContext;
         VolleyError error = new VolleyError();
        doNothing().when(mPubnativeRequest).onErrorResponse(error);
        mPubnativeRequest.sendNetworkRequest(failureUrl);

       // verify(mPubnativeRequest, times(1)).onErrorResponse(error);
    }

    @Test
    public void testParseResponse() {
        String response = loadJsonResponse("responseSuccess.json");
        List<PubnativeAdModel> ads = mPubnativeRequest.parseResponse(response);

        assertThat(ads.size() == 1);
    }

    @Test
    public void testPrepareExceptionFromErrorJson() {
        String response = loadJsonResponse("responseFailure.json");
        Exception exception = mPubnativeRequest.prepareExceptionFromErrorJson(response, 100);
        assertThat(exception.getMessage().equals("error Invalid app token100"));

        exception = mPubnativeRequest.prepareExceptionFromErrorJson("test", 100);
        assertThat(exception.getMessage().equals("Value test of type java.lang.String cannot be converted to JSONObject"));

        exception = mPubnativeRequest.prepareExceptionFromErrorJson(null, 100);
        assertThat(exception.getMessage().equals("Data is null"));
    }

    private String loadJsonResponse(String path) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(path);
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
