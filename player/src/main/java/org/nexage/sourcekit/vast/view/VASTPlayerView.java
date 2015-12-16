package org.nexage.sourcekit.vast.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class VASTPlayerView extends RelativeLayout implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, View.OnClickListener {

    interface Listener {

        void onFinished(VASTPlayerView player);
    }

    private static String TAG = "VASTPlayerView";

    // timer delays
    private static final long QUARTILE_TIMER_INTERVAL       = 250;
    private static final long VIDEO_PROGRESS_TIMER_INTERVAL = 250;
    private static final long LAYOUT_TIMER                  = 50;

    // LISTENER
    private Listener mListener;

    // timers
    private Timer mLayoutTimer;
    private Timer mTrackingEventTimer;
    private Timer mStartVideoProgressTimer;

    // Tacker
    private       LinkedList<Integer> mVideoProgressTracker      = null;
    private final int                 mMaxProgressTrackingPoints = 20;
    private HashMap<TRACKING_EVENTS_TYPE, List<String>> mTrackingEventMap;

    // DATA
    private VASTModel mVastModel = null;
    private String    mSkipName  = null;
    private int mSkipTime;

    // PLAYER
    private MediaPlayer   mMediaPlayer;
    private SurfaceView   mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    // LAYOUT
    private RelativeLayout mPlayerRoot;
    private RelativeLayout mPlayerContainer;
    private RelativeLayout mPlayerLayout;
    private RelativeLayout mPlayerLoader;
    private CountDownView  mPlayerCountDown;
    private TextView       mPlayerSkip;
    private ImageView      mPlayerMute;

    private int mVideoHeight;
    private int mVideoWidth;

    private boolean mIsSkipHidden           = true;
    private boolean mIsVideoMute            = false;
    private boolean mIsPlayBackError        = false;
    private boolean mIsProcessedImpressions = false;
    private boolean mIsCompleted            = false;
    private int     mQuartile               = 0;

    public VASTPlayerView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public void setListener(Listener listener) {

        mListener = listener;
    }

    public void startVideo(VASTModel model, String skipName, int skipTime) {

        mVastModel = model;
        mSkipName = skipName;
        mSkipTime = skipTime;

        mTrackingEventMap = mVastModel.getTrackingUrls();

        createLayout();
        createSurface();
        createMediaPlayer();
    }

    public void stopVideo() {

        cleanUpMediaPlayer();
        stopQuartileTimer();
        stopLayoutTimer();
        stopVideoProgressTimer();

        // Process close event when leaving
        if (!mIsPlayBackError) {

            processEvent(TRACKING_EVENTS_TYPE.close);
        }

        if(VASTPlayer.listener != null) {
            VASTPlayer.listener.vastDismiss();
        }

        invokeOnFinished();
    }

    private void invokeOnFinished() {

        if (mListener == null) {
            mListener.onFinished(this);
        }
    }

    private void createLayout() {

        mPlayerRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_player, null);
        mPlayerContainer = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_container);
        mPlayerContainer.setOnClickListener(this);
        mPlayerLayout = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_layout);
        mPlayerLoader = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_loader);
        mPlayerCountDown = (CountDownView) mPlayerRoot.findViewById(R.id.player_count_down);
        mPlayerSkip = (TextView) mPlayerRoot.findViewById(R.id.player_skip_text);
        mPlayerSkip.setText(mSkipName);
        mPlayerSkip.setOnClickListener(this);
        mPlayerMute = (ImageView) mPlayerRoot.findViewById(R.id.player_button_mute);
        mPlayerMute.setOnClickListener(this);

        addView(mPlayerRoot);
    }

    private void createSurface() {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mSurfaceView = new SurfaceView(getContext());
        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mPlayerContainer.addView(mSurfaceView);
    }

    private void createMediaPlayer() {

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void cleanUpMediaPlayer() {

        VASTLog.d(TAG, "cleanUpMediaPlayer");

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

    private void calculateAspectRatio() {

        VASTLog.d(TAG, "calculateAspectRatio");

        if (mVideoWidth == 0 || mVideoHeight == 0) {

            VASTLog.w(TAG, "mVideoWidth or mVideoHeight is 0, skipping calculateAspectRatio");
            return;
        }

        VASTLog.d(TAG, "calculating aspect ratio");
        double widthRatio  = 1.0 * getWidth() / mVideoWidth;
        double heightRatio = 1.0 * getHeight() / mVideoHeight;

        double scale = Math.min(widthRatio, heightRatio);

        int surfaceWidth  = (int) (scale * mVideoWidth);
        int surfaceHeight = (int) (scale * mVideoHeight);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(surfaceWidth, surfaceHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder.setFixedSize(surfaceWidth, surfaceHeight);

        VASTLog.d(TAG, " screen size:   " + getWidth() + "x" + getHeight());
        VASTLog.d(TAG, " video size:    " + mVideoWidth + "x" + mVideoHeight);
        VASTLog.d(TAG, " widthRatio:    " + widthRatio);
        VASTLog.d(TAG, " heightRatio:   " + heightRatio);
        VASTLog.d(TAG, " surface size:  " + surfaceWidth + "x" + surfaceHeight);
    }

    //=============================================
    // Layout buttons handle
    //=============================================

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

        stopVideo();
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
            ResolveInfo resolvable = getContext().getPackageManager().resolveActivity(intent, PackageManager.GET_INTENT_FILTERS);

            if (resolvable == null) {

                VASTLog.e(TAG, "Clickthrough error occured, uri unresolvable");
                return;

            } else {

                getContext().startActivity(intent);
                stopVideo();
            }

        } catch (NullPointerException e) {
            VASTLog.e(TAG, e.getMessage(), e);
        }
    }

    //=============================================
    // Event processing
    //=============================================

    private void processEvent(TRACKING_EVENTS_TYPE eventName) {

        VASTLog.i(TAG, "processEvent: " + eventName);

        if (mTrackingEventMap != null) {

            List<String> urls = (List<String>) mTrackingEventMap.get(eventName);
            fireUrls(urls);
        }
    }

    private void processImpressions() {

        VASTLog.d(TAG, "entered processImpressions");

        mIsProcessedImpressions = true;
        List<String> impressions = mVastModel.getImpressions();
        fireUrls(impressions);
    }

    private void processErrorEvent() {

        VASTLog.d(TAG, "processErrorEvent");

        List<String> errorUrls = mVastModel.getErrorUrl();
        fireUrls(errorUrls);
    }

    private void fireUrls(List<String> urls) {

        VASTLog.d(TAG, "fireUrls");

        if (urls != null) {

            for (String url : urls) {

                VASTLog.v(TAG, "\tfiring url:" + url);
                HttpTools.httpGetURL(url);
            }

        } else {

            VASTLog.d(TAG, "\turl list is null");
        }
    }

    //=============================================
    // Timers
    //=============================================

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

                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(getContext().getMainLooper());

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            if (mMediaPlayer != null) {

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
                        VASTPlayerView.this.stopVideo();
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

    //=============================================
    // SurfaceHolder.Callback
    //=============================================

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        VASTLog.d(TAG, "surfaceCreated -- (SurfaceHolder callback)");
        try {

            if (mMediaPlayer == null) {

                createMediaPlayer();
            }

            mPlayerLoader.setVisibility(View.VISIBLE);
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

        VASTLog.d(TAG, "surfaceChanged -- (SurfaceHolder callback)");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        VASTLog.d(TAG, "surfaceDestroyed -- (SurfaceHolder callback)");
        cleanUpMediaPlayer();

    }

    //=============================================
    // MediaPlayer.OnCompletionListener
    //=============================================
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        VASTLog.d(TAG, "onCompletion -- (MediaPlayer callback)");

        stopVideoProgressTimer();

        if (!mIsPlayBackError && !mIsCompleted) {

            mIsCompleted = true;
            processEvent(TRACKING_EVENTS_TYPE.complete);

            if (VASTPlayer.listener != null) {

                VASTPlayer.listener.vastComplete();
            }

            stopVideo();
        }
    }

    //=============================================
    // MediaPlayer.OnErrorListener
    //=============================================
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        VASTLog.e(TAG, "onError -- (MediaPlayer callback)");
        VASTLog.e(TAG, "Shutting down Activity due to Media Player errors: WHAT:" + what + ": EXTRA:" + extra + ":");

        mIsPlayBackError = true;

        processErrorEvent();
        stopVideo();

        return true;
    }

    //=============================================
    // MediaPlayer.OnPreparedListener
    //=============================================
    @Override
    public void onPrepared(MediaPlayer mp) {

        VASTLog.d(TAG, "onPrepared --(MediaPlayer callback) ....about to play");

        calculateAspectRatio();
        mMediaPlayer.start();

        mPlayerLayout.setVisibility(View.VISIBLE);
        mPlayerLoader.setVisibility(View.GONE);
        mPlayerCountDown.setProgress(0, mMediaPlayer.getDuration());

        startLayoutTimer();
        startQuartileTimer();
        startVideoProgressTimer();

        if (!mIsProcessedImpressions) {
            processImpressions();
        }
    }

    //=============================================
    // MediaPlayer.OnVideoSizeChangedListener
    //=============================================
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        VASTLog.d(TAG, "onVideoSizeChanged -- (MediaPlayer callback)");
        mVideoWidth = width;
        mVideoHeight = height;
    }

    //

    //=============================================
    // View.OnClickListener
    //=============================================
    public void onClick(View v) {

        if (mPlayerContainer == v) {

            onPlayerClicked(v);

        } else if (mPlayerSkip == v) {

            onSkipClicked(v);

        } else if (mPlayerMute == v) {

            onMuteClicked(v);
        }
    }
}
