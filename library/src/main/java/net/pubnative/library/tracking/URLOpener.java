package net.pubnative.library.tracking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class URLOpener {

    private static final String TAG = URLOpener.class.getName();

    public interface Listener {

        void onURLOpenerStart(String url);
        void onURLOpenerRedirect(String url);
        void onURLOpenerFinish(String url);
        void onURLOpenerFailed(String url, Exception exception);
    }

    protected Context  context;
    protected Listener listener;
    protected Handler  handler;

    public URLOpener(Context context) {

        handler = new Handler();
        this.context = context;
    }

    public void openBrowser(String url, Listener listener) {

        Log.v(TAG, "openBrowser(url, listener)");

        // We cant open if any of the parameter is null
        if (listener != null && !TextUtils.isEmpty(url)) {

            this.listener = listener;
            invokeStart(url);
            openBrowser(url);
        }
    }

    protected void openBrowser(String url) {

        Log.v(TAG, "openBrowser(url): " + url);

        if (context != null && !TextUtils.isEmpty(url)) {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            this.context.startActivity(intent);
            invokeFinish(url);
        }
    }

    public void openInBackground(final String url, final boolean canFail, Listener listener) {

        Log.v(TAG, "openInBackground" + (canFail ? " - canFail" : "") + ": " + url);

        this.listener = listener;
        openProcess(url, canFail);
    }

    protected void openProcess(final String url, final boolean canFail) {

        Log.v(TAG, "openProcess" + (canFail ? " - canFail" : "") + ":" + url);

        new Thread(new Runnable() {

            @Override
            public void run() {

                invokeStart(url);
                URLOpener.this.openProcess(url, canFail, false);
            }

        }).start();
    }

    protected void openProcess(String url, boolean canFail, boolean isRedirect) {

        Log.v(TAG, "openProcess" + (canFail ? " - canFail" : "") + (isRedirect ? " - isRedirect" : "") + ":" + url);

        if (isRedirect) {

            invokeRedirect(url);
        }

        try {

            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            Log.v(TAG, " - Status: " + status);

            switch (status) {

                case HttpURLConnection.HTTP_OK: {

                    Log.v(TAG, " - Done");
                    this.openBrowser(url);
                }
                break;

                case HttpURLConnection.HTTP_MOVED_TEMP:
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_SEE_OTHER: {

                    String newUrl = conn.getHeaderField("Location");
                    openProcess(newUrl, canFail);
                }
                break;

                default: {

                    if (canFail) {

                        invokeFail(url, new Exception("Invalid URL, Status: " + status));
                    } else {

                        openBrowser(url);
                    }

                    Log.e(TAG, " - Status error: " + status);
                }
                break;
            }

        } catch (Exception exception) {

            // Do nothing
            Log.e(TAG, " - Error: " + exception);

            if (canFail) {

                invokeFail(url, exception);

            } else {

                openBrowser(url);
            }
        }
    }

    //==================================================
    // Listener helpers
    //==================================================

    protected void invokeStart(final String url) {

        Log.v(TAG, "invokeStart");

        handler.post(new Runnable() {

            @Override
            public void run() {

                if (listener != null) {

                    listener.onURLOpenerStart(url);
                }
            }
        });
    }

    protected void invokeRedirect(final String url) {

        Log.v(TAG, "invokeRedirect");

        handler.post(new Runnable() {

            @Override
            public void run() {

                if (listener != null) {

                    listener.onURLOpenerRedirect(url);
                }
            }
        });
    }

    protected void invokeFinish(final String url) {

        Log.v(TAG, "invokeFinish");

        handler.post(new Runnable() {

            @Override
            public void run() {

                if (listener != null) {

                    listener.onURLOpenerFinish(url);
                }
            }
        });
    }

    protected void invokeFail(final String url, final Exception exception) {

        Log.v(TAG, "invokeFail: " + exception);

        handler.post(new Runnable() {

            @Override
            public void run() {

                if (listener != null) {

                    listener.onURLOpenerFailed(url, exception);
                }
            }
        });
    }
}
