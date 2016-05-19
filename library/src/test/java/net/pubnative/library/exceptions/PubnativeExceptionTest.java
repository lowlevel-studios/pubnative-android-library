package net.pubnative.library.exceptions;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class PubnativeExceptionTest {

    @Test
    public void testGetErrorCode() throws Exception {

        int exceptionCode = 1001;

        PubnativeException exception = new PubnativeException(exceptionCode, "Test message");
        assertThat(exception.getErrorCode()).isEqualTo(exceptionCode);

    }
}