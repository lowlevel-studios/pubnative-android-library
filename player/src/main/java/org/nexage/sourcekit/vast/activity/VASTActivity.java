//
//  VastActivity.java
//

//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.vast.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import net.pubnative.widget.CountDownView;

import org.nexage.sourcekit.util.HttpTools;
import org.nexage.sourcekit.util.VASTLog;
import org.nexage.sourcekit.vast.R;
import org.nexage.sourcekit.vast.VASTPlayer;
import org.nexage.sourcekit.vast.model.TRACKING_EVENTS_TYPE;
import org.nexage.sourcekit.vast.model.VASTModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VASTActivity extends Activity implements OnCompletionListener, OnErrorListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

    public static final String EXTRA_MODEL     = "com.nexage.android.vast.player.vastModel";
    public static final String EXTRA_SKIP_NAME = "com.nexage.android.vast.player.skipName";
    public static final String EXTRA_SKIP_TIME = "com.nexage.android.vast.player.skipTime";

    private static String TAG = "VASTActivity";

    // timer delays
    private static final long QUARTILE_TIMER_INTERVAL       = 250;
    private static final long VIDEO_PROGRESS_TIMER_INTERVAL = 250;
    private static final long LAYOUT_TIMER                  = 50;

    // timers
    private Timer mLayoutTimer;
    private Timer mTrackingEventTimer;
    private Timer mStartVideoProgressTimer;

    // Tacker
    private       LinkedList<Integer> mVideoProgressTracker      = null;
    private final int                 mMaxProgressTrackingPoints = 20;
    private HashMap<TRACKING_EVENTS_TYPE, List<String>> mTrackingEventMap;

    // EXTRAS
    private VASTModel mVastModel = null;
    private String    mSkipName  = null;
    private int mSkipTime;

    // PLAYER
    private MediaPlayer   mMediaPlayer;
    private SurfaceView   mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    // LAYOUT
    private RelativeLayout mPlayerRoot;
    private RelativeLayout mPlayerHolder;
    private RelativeLayout mPlayerLayout;
    private ProgressBar    mPlayerLoadingBar;
    private CountDownView  mPlayerCountDown;
    private TextView       mPlayerSkip;

    private int mVideoHeight;
    private int mVideoWidth;
    private int mScreenWidth;
    private int mScreenHeight;

    private boolean mIsSkipHidden           = true;
    private boolean mIsVideoMute            = false;
    private boolean mIsPlayBackError        = false;
    private boolean mIsProcessedImpressions = false;
    private boolean mIsCompleted            = false;
    private int     mQuartile               = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        VASTLog.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);

        int currentOrientation = getResources().getConfiguration().orientation;
        VASTLog.d(TAG, "currentOrientation:" + currentOrientation);

        if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {

            VASTLog.d(TAG, "Orientation is not landscape.....forcing landscape");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else {

            VASTLog.d(TAG, "orientation is landscape");
            Intent i = getIntent();
            mVastModel = (VASTModel) i.getSerializableExtra(EXTRA_MODEL);
            mSkipName = i.getStringExtra(EXTRA_SKIP_NAME);
            mSkipTime = i.getIntExtra(EXTRA_SKIP_TIME, 0);

            if (mVastModel == null) {

                VASTLog.e(TAG, "vastModel is null. Stopping activity.");
                finishVAST();

            } else {

                hideTitleStatusBars();

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                mScreenWidth = displayMetrics.widthPixels;
                mScreenHeight = displayMetrics.heightPixels;
                mTrackingEventMap = mVastModel.getTrackingUrls();

                createUIComponents();
            }
        }
    }

    @Override
    protected void onStart() {

        VASTLog.d(TAG, "entered onStart --(life cycle event)");
        super.onStart();
    }

    @Override
    protected void onStop() {

        VASTLog.d(TAG, "entered on onStop --(life cycle event)");
        super.onStop();
    }

    @Override
    protected void onPause() {

        VASTLog.d(TAG, "entered on onPause --(life cycle event)");
        super.onPause();

        this.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        VASTLog.d(TAG, "entered on onDestroy --(life cycle event)");
        super.onDestroy();
    }

    private void hideTitleStatusBars() {

        // hide title bar of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide status bar and soft keys.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    private void createUIComponents() {

        mPlayerRoot = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.pubnative_player, null);
        mPlayerHolder = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_holder);
        mPlayerLayout = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_layout);
        mPlayerLoadingBar = (ProgressBar) mPlayerRoot.findViewById(R.id.player_loading_bar);
        mPlayerCountDown = (CountDownView) mPlayerRoot.findViewById(R.id.player_count_down);
        mPlayerSkip = (TextView) mPlayerRoot.findViewById(R.id.player_skip_text);
        mPlayerSkip.setText(mSkipName);

        createSurface();
        createMediaPlayer();

        setContentView(mPlayerRoot);
    }

    @SuppressWarnings("deprecation")
    private void createSurface() {

        LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mSurfaceView = new SurfaceView(this);
        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mPlayerHolder.addView(mSurfaceView);
    }

    private void createMediaPlayer() {

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void onMuteClicked(View v) {

        ImageView muteView = (ImageView) v;
        if (mIsVideoMute) {

            mMediaPlayer.setVolume(1.0f, 1.0f);
            muteView.setImageResource(R.drawable.pubnative_btn_mute);

        } else {

            mMediaPlayer.setVolume(0.0f, 0.0f);
            muteView.setImageResource(R.drawable.pubnative_btn_unmute);
        }

        mIsVideoMute = !mIsVideoMute;
    }

    public void onSkipClicked(View v) {

        this.onBackPressed();
    }

    public void onPlayerClicked(View v) {

        VASTLog.d(TAG, "entered processClickThroughEvent:");

        if (VASTPlayer.listener != null) {

            VASTPlayer.listener.vastClick();
        }

        String clickThroughUrl = mVastModel.getVideoClicks().getClickThrough();
        VASTLog.d(TAG, "clickThrough url: " + clickThroughUrl);

        // Before we send the app to the click through url, we will process ClickTracking URL's.
        List<String> urls = mVastModel.getVideoClicks().getClickTracking();
        fireUrls(urls);

        // Navigate to the click through url
        try {
            Uri uri = Uri.parse(clickThroughUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ResolveInfo resolvable = getPackageManager().resolveActivity(intent, PackageManager.GET_INTENT_FILTERS);

            if (resolvable == null) {

                VASTLog.e(TAG, "Clickthrough error occured, uri unresolvable");
                return;

            } else {

                startActivity(intent);
                onBackPressed();
            }

        } catch (NullPointerException e) {
            VASTLog.e(TAG, e.getMessage(), e);
        }
    }

    public void close() {

    }

    @Override
    public void onBackPressed() {

        cleanActivityUp();

        // Process close event when leaving
        if (!mIsPlayBackError) {

            processEvent(TRACKING_EVENTS_TYPE.close);
        }

        finishVAST();
    }

    public void surfaceCreated(SurfaceHolder holder) {

        VASTLog.d(TAG, "surfaceCreated -- (SurfaceHolder callback)");
        try {

            if (mMediaPlayer == null) {

                createMediaPlayer();
            }

            mPlayerLoadingBar.setVisibility(View.VISIBLE);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            String url = mVastModel.getPickedMediaFileURL();

            VASTLog.d(TAG, "URL for media file:" + url);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            VASTLog.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int arg1, int arg2, int arg3) {

        VASTLog.d(TAG, "entered surfaceChanged -- (SurfaceHolder callback)");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        VASTLog.d(TAG, "entered surfaceDestroyed -- (SurfaceHolder callback)");
        cleanUpMediaPlayer();

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        VASTLog.d(TAG, "entered onVideoSizeChanged -- (MediaPlayer callback)");
        mVideoWidth = width;
        mVideoHeight = height;
        VASTLog.d(TAG, "video size: " + mVideoWidth + "x" + mVideoHeight);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        VASTLog.d(TAG, "entered onPrepared called --(MediaPlayer callback) ....about to play");

        calculateAspectRatio();
        mMediaPlayer.start();

        mPlayerLayout.setVisibility(View.VISIBLE);
        mPlayerLoadingBar.setVisibility(View.GONE);
        mPlayerCountDown.setProgress(0, mMediaPlayer.getDuration());

        startLayoutTimer();
        startQuartileTimer();
        startVideoProgressTimer();

        if (!mIsProcessedImpressions) {
            processImpressions();
        }
    }

    private void calculateAspectRatio() {

        VASTLog.d(TAG, "entered calculateAspectRatio");

        if (mVideoWidth == 0 || mVideoHeight == 0) {

            VASTLog.w(TAG, "mVideoWidth or mVideoHeight is 0, skipping calculateAspectRatio");
            return;
        }

        VASTLog.d(TAG, "calculating aspect ratio");
        double widthRatio  = 1.0 * mScreenWidth / mVideoWidth;
        double heightRatio = 1.0 * mScreenHeight / mVideoHeight;

        double scale = Math.min(widthRatio, heightRatio);

        int surfaceWidth  = (int) (scale * mVideoWidth);
        int surfaceHeight = (int) (scale * mVideoHeight);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(surfaceWidth, surfaceHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder.setFixedSize(surfaceWidth, surfaceHeight);

        VASTLog.d(TAG, " screen size:   " + mScreenWidth + "x" + mScreenHeight);
        VASTLog.d(TAG, " video size:    " + mVideoWidth + "x" + mVideoHeight);
        VASTLog.d(TAG, " widthRatio:    " + widthRatio);
        VASTLog.d(TAG, " heightRatio:   " + heightRatio);
        VASTLog.d(TAG, " surface size:  " + surfaceWidth + "x" + surfaceHeight);

    }

    private void cleanActivityUp() {

        cleanUpMediaPlayer();
        stopQuartileTimer();
        stopLayoutTimer();
        stopVideoProgressTimer();
    }

    private void cleanUpMediaPlayer() {

        VASTLog.d(TAG, "entered cleanUpMediaPlayer ");

        if (mMediaPlayer != null) {

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }

            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);

            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        VASTLog.e(TAG, "entered onError -- (MediaPlayer callback)");
        mIsPlayBackError = true;
        VASTLog.e(TAG, "Shutting down Activity due to Media Player errors: WHAT:" + what + ": EXTRA:" + extra + ":");

        processErrorEvent();
        onBackPressed();

        return true;
    }

    private void processErrorEvent() {

        VASTLog.d(TAG, "entered processErrorEvent");

        List<String> errorUrls = mVastModel.getErrorUrl();
        fireUrls(errorUrls);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        VASTLog.d(TAG, "entered onCOMPLETION -- (MediaPlayer callback)");
        stopVideoProgressTimer();

        if (!mIsPlayBackError && !mIsCompleted) {

            mIsCompleted = true;
            processEvent(TRACKING_EVENTS_TYPE.complete);

            if (VASTPlayer.listener != null) {

                VASTPlayer.listener.vastComplete();
            }

            this.onBackPressed();
        }
    }

    private void processImpressions() {

        VASTLog.d(TAG, "entered processImpressions");

        mIsProcessedImpressions = true;
        List<String> impressions = mVastModel.getImpressions();
        fireUrls(impressions);
    }

    private void fireUrls(List<String> urls) {

        VASTLog.d(TAG, "entered fireUrls");

        if (urls != null) {

            for (String url : urls) {

                VASTLog.v(TAG, "\tfiring url:" + url);
                HttpTools.httpGetURL(url);
            }

        } else {

            VASTLog.d(TAG, "\turl list is null");
        }
    }

    // Timers
    private void startQuartileTimer() {

        VASTLog.d(TAG, "entered startQuartileTimer");
        stopQuartileTimer();

        if (mIsCompleted) {

            VASTLog.d(TAG, "ending quartileTimer becuase the video has been replayed");
            return;
        }

        final int videoDuration = mMediaPlayer.getDuration();

        mTrackingEventTimer = new Timer();
        mTrackingEventTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                int percentage = 0;
                try {

                    int curPos = mMediaPlayer.getCurrentPosition();

                    // wait for the video to really start
                    if (curPos == 0) {
                        return;
                    }

                    percentage = 100 * curPos / videoDuration;

                } catch (Exception e) {

                    VASTLog.w(TAG, "mediaPlayer.getCurrentPosition exception: " + e.getMessage());
                    cancel();
                    return;
                }

                if (percentage >= 25 * mQuartile) {

                    if (mQuartile == 0) {

                        VASTLog.i(TAG, "Video at start: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.start);

                    } else if (mQuartile == 1) {

                        VASTLog.i(TAG, "Video at first quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.firstQuartile);

                    } else if (mQuartile == 2) {

                        VASTLog.i(TAG, "Video at midpoint: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.midpoint);

                    } else if (mQuartile == 3) {

                        VASTLog.i(TAG, "Video at third quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.thirdQuartile);
                        stopQuartileTimer();

                    }

                    mQuartile++;
                }
            }

        }, 0, QUARTILE_TIMER_INTERVAL);
    }

    private void stopQuartileTimer() {

        if (mTrackingEventTimer != null) {

            mTrackingEventTimer.cancel();
            mTrackingEventTimer = null;
        }
    }

    private void startLayoutTimer() {

        VASTLog.d(TAG, "entered startLayoutTimer");

        mLayoutTimer = new Timer();
        mLayoutTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (mMediaPlayer == null) {
                    return;
                }

                try {

                    final int currentPosition = mMediaPlayer.getCurrentPosition();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            if(mMediaPlayer != null) {

                                mPlayerCountDown.setProgress(currentPosition, mMediaPlayer.getDuration());

                                if (mSkipTime >= 0 &&
                                    mSkipTime * 1000 < currentPosition &&
                                    mIsSkipHidden) {

                                    mIsSkipHidden = false;
                                    mPlayerSkip.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

                } catch (Exception e) {

                }
            }
        }, 0, LAYOUT_TIMER);
    }

    private void stopLayoutTimer() {

        VASTLog.d(TAG, "entered stopVideoProgressTimer");

        if (mLayoutTimer != null) {

            mLayoutTimer.cancel();
        }
    }

    private void startVideoProgressTimer() {

        VASTLog.d(TAG, "entered startVideoProgressTimer");

        mStartVideoProgressTimer = new Timer();
        mVideoProgressTracker = new LinkedList<Integer>();

        mStartVideoProgressTimer.schedule(new TimerTask() {

            int maxAmountInList = mMaxProgressTrackingPoints - 1;

            @Override
            public void run() {

                if (mMediaPlayer == null) {

                    return;
                }

                if (mVideoProgressTracker.size() == maxAmountInList) {

                    int firstPosition = mVideoProgressTracker.getFirst();
                    int lastPosition = mVideoProgressTracker.getLast();

                    if (lastPosition >= firstPosition) {

                        VASTLog.v(TAG, "video progressing (position:" + lastPosition + ")");
                        mVideoProgressTracker.removeFirst();

                    } else {

                        VASTLog.e(TAG, "detected video hang");
                        mIsPlayBackError = true;
                        stopVideoProgressTimer();
                        processErrorEvent();
                        VASTActivity.this.onBackPressed();
                    }
                }

                try {

                    int curPos = mMediaPlayer.getCurrentPosition();
                    mVideoProgressTracker.addLast(curPos);

                } catch (Exception e) {
                    // occasionally the timer is in the middle of processing and
                    // the media player is cleaned up
                }
            }

        }, 0, VIDEO_PROGRESS_TIMER_INTERVAL);

    }

    private void stopVideoProgressTimer() {

        VASTLog.d(TAG, "entered stopVideoProgressTimer");

        if (mStartVideoProgressTimer != null) {

            mStartVideoProgressTimer.cancel();
        }
    }

    private void processEvent(TRACKING_EVENTS_TYPE eventName) {

        VASTLog.i(TAG, "entered Processing Event: " + eventName);

        if(mTrackingEventMap != null) {

            List<String> urls = (List<String>) mTrackingEventMap.get(eventName);
            fireUrls(urls);
        }
    }

    private void finishVAST() {

        VASTPlayer.listener.vastDismiss();
        finish();
    }
}
