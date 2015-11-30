package net.pubnative.library.request;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;
import java.util.HashMap;


public class PubNativeRequestTest {
    private PubNativeRequest mPubNativeRequest;
    private PubNativeRequest.PubNativeException mPubNativeException;
    private boolean mIsLogging;
    private HashMap<String, String> mRequestParameters;
    private PubNativeRequest.PubNativeRequestListener mRequestListener;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        mPubNativeRequest = new PubNativeRequest(RuntimeEnvironment.application);
        mPubNativeException = new PubNativeRequest.PubNativeException();

        Field field = PubNativeRequest.class.getDeclaredField("isLogging");
        field.setAccessible(true);
        mIsLogging = (Boolean) field.get(mPubNativeRequest);

        field = PubNativeRequest.class.getDeclaredField("requestParameters");
        field.setAccessible(true);
        mRequestParameters = (HashMap<String, String>) field.get(mPubNativeRequest);

        field = PubNativeRequest.class.getDeclaredField("listener");
        field.setAccessible(true);
        mRequestListener = (PubNativeRequest.PubNativeRequestListener) field.get(mPubNativeRequest);

    }

    @After
    public void tearDown(){
        mPubNativeRequest = null;
    }

    @Test
    public void testPubNativeRequestIsNotNull(){
        Assert.assertNotNull("Pub Native Request is not null",mPubNativeRequest);
    }

    @Test
    public void testPubNativeExceptionIsNotNull(){
        Assert.assertNotNull("Pub Native Exception is not null",mPubNativeException);
    }

    @Test
    public void testIsLoggingNotNull() {
        Assert.assertNotNull("IsLogging is not null", mIsLogging);
    }


    @Test
    public void tesSetAndGetIsLogging(){
        mPubNativeRequest.setIsLogging(true);
        boolean isLog = mPubNativeRequest.isLogging();
        Assert.assertSame("IsLogged true", isLog, true);
    }

    @Test
    public void testSetAndGetErrorMessage(){
        mPubNativeException.setErrMsg("Error Message");
        String msg = mPubNativeException.getErrMsg();

        Assert.assertSame("Error Message is same", msg, "Error Message");
    }

    @Test
    public void testSetAndGetStatus(){

    }

    @Test
    public void testSetParameters(){
        mPubNativeRequest.setParameter(PubNativeRequest.Parameters.AD_COUNT, "2");

        mPubNativeRequest.setParameter(PubNativeRequest.Parameters.ICON_SIZE, null);
    }

    @Test
    public void testStart(){
        PubNativeRequest.PubNativeRequestListener requestListener = Mockito.mock(PubNativeRequest.PubNativeRequestListener.class);
    }


}
