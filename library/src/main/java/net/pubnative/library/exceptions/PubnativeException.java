package net.pubnative.library.exceptions;

public class PubnativeException extends Exception {

    public static final String TAG = PubnativeException.class.getSimpleName();
    //==============================================================================================
    // Private fields
    //==============================================================================================
    protected int mErrorCode;
    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final PubnativeException REQUEST_NO_INTERNET             = new PubnativeException(1000, "Internet connection is not available");
    public static final PubnativeException REQUEST_PARAMETERS_INVALID      = new PubnativeException(1001, "Invalid execute parameters");
    public static final PubnativeException REQUEST_NO_FILL                 = new PubnativeException(1002, "No fill");
    public static final PubnativeException CONTEXT_IS_NULL                 = new PubnativeException(1003, "context is null or empty");
    public static final PubnativeException APPTOKEN_IS_NULL_OR_EMPTY       = new PubnativeException(1004, "app token is null or empty");
    public static final PubnativeException WRONG_TYPE_CONTEXT              = new PubnativeException(1005, "wrong context type, must be Activity context");
    //==============================================================================================
    // Interstitial Exceptions
    //==============================================================================================
    public static final PubnativeException INTERSTITIAL_PARAMETERS_INVALID = new PubnativeException(3000, "parameters configuring the interstitial are invalid");
    public static final PubnativeException INTERSTITIAL_LOADING            = new PubnativeException(3001, "interstitial is currently loading");
    public static final PubnativeException INTERSTITIAL_SHOWN              = new PubnativeException(3002, "interstitial is already shown");

    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param message   Error message
     */
    public PubnativeException(int errorCode, String message) {

        super(message);
        mErrorCode = errorCode;
    }

    /**
     * This will return this exception error code number
     *
     * @return valid int representing the error code
     */
    public int getErrorCode() {

        return mErrorCode;
    }

    @Override
    public String getMessage() {

        return String.valueOf("PubnativeException (" + getErrorCode() + "): " + super.getMessage());
    }
}
