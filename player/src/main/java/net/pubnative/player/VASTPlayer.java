package net.pubnative.player;

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

import net.pubnative.player.model.TRACKING_EVENTS_TYPE;
import net.pubnative.player.model.VASTModel;
import net.pubnative.player.util.HttpTools;
import net.pubnative.player.util.VASTLog;
import net.pubnative.player.widget.CountDownView;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VASTPlayer extends RelativeLayout implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, View.OnClickListener {

    private static final String TAG = VASTPlayer.class.getName();

    /**
     * Caching callbacks for caching and streaming events
     */
    public interface CachingListener {

        void onVASTPlayerCachingStart();
        void onVASTPlayerCachingFinish();
        void onVASTPlayerCachingFail();
    }

    /**
     * Interaction callbacks for user events on the video
     */
    public interface InteractionListener {

        void onVASTPlayerMute();
        void onVASTPlayerUnMute();
        void onVASTPlayerClick();
        void onVASTPlayerDismiss();
    }

    /**
     * Playback callbacks for events happened while playing the video
     */
    public interface PlaybackListener {

        void onVASTPlayerStart();
        void onVASTPlayerFirstQuartile();
        void onVASTPlayerMidpoint();
        void onVASTPlayerThirdQuartile();
        void onVASTPlayerCompleted();
    }

    // LISTENERS
    private CachingListener     mCachingListener     = null;
    private PlaybackListener    mPlaybackListener    = null;
    private InteractionListener mInteractionListener = null;

    // TIMERS
    private Timer mLayoutTimer;
    private Timer mTrackingEventsTimer;

    private static final long TIMER_TRACKING_INTERVAL = 250;
    private static final long TIMER_LAYOUT_INTERVAL   = 50;

    // TRACKING
    private HashMap<TRACKING_EVENTS_TYPE, List<String>> mTrackingEventMap;

    // DATA
    private VASTModel mVastModel;
    private String    mSkipName;
    private int       mSkipTime;

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

    // OTHERS
    private int     mVideoHeight            = 0;
    private int     mVideoWidth             = 0;
    private boolean mIsSkipHidden           = true;
    private boolean mIsVideoMute            = false;
    private boolean mIsPlayBackError        = false;
    private boolean mIsProcessedImpressions = false;
    private boolean mIsCompleted            = false;
    private int     mQuartile               = 0;
    private boolean mIsAutoPlay             = false;
    private boolean mIsReady                = false;
    private boolean mIsLayoutLoaded         = false;
    private boolean mIsSurfaceLoaded        = false;

    //=======================================================
    // Public
    //=======================================================

    /**
     * Constructor, generally used automatically by a layout inflater
     *
     * @param context
     * @param attrs
     */
    public VASTPlayer(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    /**
     * Sets caching listener for callbacks related to status of video streaming
     *
     * @param listener CachingListener
     */
    public void setCachingListener(CachingListener listener) {

        mCachingListener = listener;
    }

    /**
     * Sets interaction listener for callbacks related to user interaction over the playing video
     *
     * @param listener InteractionListener
     */
    public void setInteractionListener(InteractionListener listener) {

        mInteractionListener = listener;
    }

    /**
     * Sets playback listener for callbacks related to playback events on the video
     *
     * @param listener PlaybackListener
     */
    public void setPlaybackListener(PlaybackListener listener) {

        mPlaybackListener = listener;
    }

    /**
     * Starts reproduction inmediately after the video is loaded
     *
     * @param autoPlay sets the value of the autoplay option
     */
    public void setAutoPlay(boolean autoPlay) {

        mIsAutoPlay = autoPlay;
    }

    /**
     * Starts loading a video VASTModel in the player, it will notify when it's ready with
     * CachingListener.onVASTPlayerCachingFinish(), so you can start video reproduction.
     *
     * @param model    model containing the parsed VAST XML
     * @param skipName name for the skip button
     * @param skipTime time for the skip buttom to appear, < 0 value means
     */
    public void load(VASTModel model, String skipName, int skipTime) {

        VASTLog.v(TAG, "load");

        mVastModel = model;
        mSkipName = skipName;
        mSkipTime = skipTime;

        mIsReady = false;

        mTrackingEventMap = mVastModel.getTrackingUrls();

        if (!mIsLayoutLoaded) {

            mIsLayoutLoaded = true;
            createLayout();
        }

        if (!mIsSurfaceLoaded) {

            mIsSurfaceLoaded = true;
            createSurface();

        } else {

            loadVideoURL(mVastModel.getPickedMediaFileURL());
        }
    }

    /**
     * Starts video playback
     */
    public void play() {

        VASTLog.v(TAG, "play");

        if (mIsReady) {

            calculateAspectRatio();
            mMediaPlayer.start();

            if (mMediaPlayer.getCurrentPosition() == 0) {

                mPlayerLayout.setVisibility(View.VISIBLE);
                mPlayerLoader.setVisibility(View.GONE);
                mPlayerCountDown.setProgress(0, mMediaPlayer.getDuration());

                startLayoutTimer();
                startQuartileTimer();
            }

        } else {

            VASTLog.d(TAG, "play cannot be called before the video is ready");
        }
    }

    /**
     * Pauses video playback
     */
    public void pause() {

        VASTLog.v(TAG, "pause");

        mMediaPlayer.pause();

        // TODO: Add pause layout
    }

    /**
     * Stops video playback
     */
    public void stop() {

        VASTLog.v(TAG, "stop");

        cleanUpMediaPlayer();
        stopQuartileTimer();
        stopLayoutTimer();

        // Process close event when leaving
        if (!mIsPlayBackError) {

            processEvent(TRACKING_EVENTS_TYPE.close);
        }
    }

    public void rotate() {

        calculateAspectRatio();
    }

    //=======================================================
    // Private
    //=======================================================

    // User Interaction
    //-------------------------------------------------------

    public void onMuteClicked(View v) {

        VASTLog.v(TAG, "onMuteClicked");

        ImageView muteView = (ImageView) v;

        if (mMediaPlayer != null) {

            if (mIsVideoMute) {

                mMediaPlayer.setVolume(1.0f, 1.0f);
                muteView.setImageResource(R.drawable.pubnative_btn_mute);
                invokeOnUnMute();

            } else {

                mMediaPlayer.setVolume(0.0f, 0.0f);
                muteView.setImageResource(R.drawable.pubnative_btn_unmute);
                invokeOnMute();
            }

            mIsVideoMute = !mIsVideoMute;
        }
    }

    public void onSkipClicked(View v) {

        VASTLog.v(TAG, "onSkipClicked");

        invokeOnDismiss();
        stop();
    }

    public void onPlayerClicked(View v) {

        VASTLog.v(TAG, "onPlayerClicked");

        invokeOnClick();

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
                stop();
            }

        } catch (NullPointerException e) {
            VASTLog.e(TAG, e.getMessage(), e);
        }
    }

    // Layout
    //-------------------------------------------------------

    private void createSurface() {

        VASTLog.v(TAG, "createSurface");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mSurfaceView = new SurfaceView(getContext());
        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mPlayerContainer.addView(mSurfaceView);
    }

    private void createLayout() {

        VASTLog.v(TAG, "createLayout");

        mPlayerRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_player_layout, null);
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

    // Media player
    //-------------------------------------------------------

    private void createMediaPlayer() {

        VASTLog.v(TAG, "createMediaPlayer");

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void cleanUpMediaPlayer() {

        VASTLog.v(TAG, "cleanUpMediaPlayer");

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

    public void calculateAspectRatio() {

        VASTLog.v(TAG, "calculateAspectRatio");

        if (mVideoWidth == 0 || mVideoHeight == 0) {

            VASTLog.w(TAG, "mVideoWidth or mVideoHeight is 0, skipping calculateAspectRatio");
            return;
        }

        double widthRatio  = 1.0 * getWidth() / mVideoWidth;
        double heightRatio = 1.0 * getHeight() / mVideoHeight;

        VASTLog.v(TAG, "calculating aspect ratio");

        double scale = Math.min(widthRatio, heightRatio);

        int surfaceWidth  = (int) (scale * mVideoWidth);
        int surfaceHeight = (int) (scale * mVideoHeight);

        VASTLog.i(TAG, " screen size:   " + getWidth() + "x" + getHeight());
        VASTLog.i(TAG, " video size:    " + mVideoWidth + "x" + mVideoHeight);
        VASTLog.i(TAG, " widthRatio:    " + widthRatio);
        VASTLog.i(TAG, " heightRatio:   " + heightRatio);
        VASTLog.i(TAG, " surface size:  " + surfaceWidth + "x" + surfaceHeight);

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(surfaceWidth, surfaceHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceView.setLayoutParams(params);
        mSurfaceHolder.setFixedSize(surfaceWidth, surfaceHeight);

//        } else {
//
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(surfaceHeight, surfaceWidth);
//            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            mSurfaceView.setLayoutParams(params);
//            mSurfaceHolder.setFixedSize(surfaceHeight, surfaceWidth);
//        }
    }

    // Event processing
    //-------------------------------------------------------

    private void processEvent(TRACKING_EVENTS_TYPE eventName) {

        VASTLog.v(TAG, "processEvent: " + eventName);

        if (mTrackingEventMap != null) {

            List<String> urls = mTrackingEventMap.get(eventName);
            fireUrls(urls);
        }
    }

    private void processImpressions() {

        VASTLog.v(TAG, "processImpressions");

        if (!mIsProcessedImpressions) {
            mIsProcessedImpressions = true;
            List<String> impressions = mVastModel.getImpressions();
            fireUrls(impressions);
        }
    }

    private void processErrorEvent() {

        VASTLog.v(TAG, "processErrorEvent");

        List<String> errorUrls = mVastModel.getErrorUrl();
        fireUrls(errorUrls);
    }

    private void fireUrls(List<String> urls) {

        VASTLog.v(TAG, "fireUrls");

        if (urls != null) {

            for (String url : urls) {

                VASTLog.v(TAG, "\tfiring url:" + url);
                HttpTools.httpGetURL(url);
            }

        } else {

            VASTLog.d(TAG, "\turl list is null");
        }
    }

    //=======================================================
    // Timers
    //=======================================================

    // Quartile timer
    //-------------------------------------------------------
    private void startQuartileTimer() {

        VASTLog.v(TAG, "startQuartileTimer");

        stopQuartileTimer();

        if (mIsCompleted) {

            VASTLog.d(TAG, "ending quartileTimer becuase the video has been replayed");
            return;
        }

        final int videoDuration = mMediaPlayer.getDuration();

        mTrackingEventsTimer = new Timer();
        mTrackingEventsTimer.scheduleAtFixedRate(new TimerTask() {

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
                        processImpressions();
                        processEvent(TRACKING_EVENTS_TYPE.start);
                        invokeOnStart();

                    } else if (mQuartile == 1) {

                        VASTLog.i(TAG, "Video at first quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.firstQuartile);
                        invokeOnFirstQuartile();

                    } else if (mQuartile == 2) {

                        VASTLog.i(TAG, "Video at midpoint: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.midpoint);
                        invokeOnMidpoint();

                    } else if (mQuartile == 3) {

                        VASTLog.i(TAG, "Video at third quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.thirdQuartile);
                        stopQuartileTimer();
                        invokeOnThirdQuartile();
                    }

                    mQuartile++;
                }
            }

        }, 0, TIMER_TRACKING_INTERVAL);
    }

    private void stopQuartileTimer() {

        VASTLog.v(TAG, "stopQuartileTimer");
        if (mTrackingEventsTimer != null) {

            mTrackingEventsTimer.cancel();
            mTrackingEventsTimer = null;
        }
    }

    // Layout timer
    //-------------------------------------------------------
    private void startLayoutTimer() {

        VASTLog.v(TAG, "startLayoutTimer");

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
        }, 0, TIMER_LAYOUT_INTERVAL);
    }

    private void stopLayoutTimer() {

        VASTLog.d(TAG, "stopLayoutTimer");

        if (mLayoutTimer != null) {

            mLayoutTimer.cancel();
        }
    }

    // Caching Listener
    //-------------------------------------------------------

    private void invokeOnCachingStart() {

        VASTLog.v(TAG, "invokeOnCachingStart");

        if (mCachingListener != null) {

            mCachingListener.onVASTPlayerCachingStart();
        }
    }

    private void invokeOnCachingFail() {

        VASTLog.v(TAG, "invokeOnCachingFail");

        if (mCachingListener != null) {

            mCachingListener.onVASTPlayerCachingFail();
        }
    }

    private void invokeOnCachingFinish() {

        VASTLog.v(TAG, "invokeOnCachingFinish");

        if (mCachingListener != null) {

            mCachingListener.onVASTPlayerCachingFinish();
        }
    }

    // Playback Listener
    //-------------------------------------------------------

    private void invokeOnStart() {

        VASTLog.v(TAG, "invokeOnStart");

        if (mPlaybackListener != null) {

            mPlaybackListener.onVASTPlayerStart();
        }
    }

    private void invokeOnFirstQuartile() {

        VASTLog.v(TAG, "invokeOnFirstQuartile");

        if (mPlaybackListener != null) {

            mPlaybackListener.onVASTPlayerFirstQuartile();
        }
    }

    private void invokeOnMidpoint() {

        VASTLog.v(TAG, "invokeOnMidpoint");

        if (mPlaybackListener != null) {

            mPlaybackListener.onVASTPlayerMidpoint();
        }
    }

    private void invokeOnThirdQuartile() {

        VASTLog.v(TAG, "invokeOnThirdQuartile");

        if (mPlaybackListener != null) {

            mPlaybackListener.onVASTPlayerThirdQuartile();
        }
    }

    private void invokeOnCompleted() {

        VASTLog.v(TAG, "invokeOnCompleted");

        if (mPlaybackListener != null) {

            mPlaybackListener.onVASTPlayerCompleted();
        }
    }

    // Interaction Listener
    //-------------------------------------------------------

    private void invokeOnMute() {

        VASTLog.v(TAG, "invokeOnMute");

        if (mInteractionListener != null) {

            mInteractionListener.onVASTPlayerMute();
        }
    }

    private void invokeOnUnMute() {

        VASTLog.v(TAG, "invokeOnUnMute");

        if (mInteractionListener != null) {

            mInteractionListener.onVASTPlayerUnMute();
        }
    }

    private void invokeOnClick() {

        VASTLog.v(TAG, "invokeOnClick");

        if (mInteractionListener != null) {

            mInteractionListener.onVASTPlayerClick();
        }
    }

    private void invokeOnDismiss() {

        VASTLog.v(TAG, "invokeOnDismiss");

        if (mInteractionListener != null) {

            mInteractionListener.onVASTPlayerDismiss();
        }
    }

    //=============================================
    // CALLBACKS
    //=============================================

    // SurfaceHolder.Callback
    //---------------------------------------------

    public void loadVideoURL(String url) {

        VASTLog.v(TAG, "loadVideoURL: " + url);

        if (mMediaPlayer == null) {

            createMediaPlayer();
        }

        try {

            mMediaPlayer.setDisplay(mSurfaceHolder);
            invokeOnCachingStart();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();

        } catch (Exception e) {

            VASTLog.e(TAG, e.getMessage(), e);
            invokeOnCachingFail();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        VASTLog.v(TAG, "surfaceCreated -- (SurfaceHolder callback)");

        try {

            mPlayerLoader.setVisibility(View.VISIBLE);

            loadVideoURL(mVastModel.getPickedMediaFileURL());

        } catch (Exception e) {

            VASTLog.e(TAG, e.getMessage(), e);
            invokeOnCachingFail();
            stop();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int arg1, int arg2, int arg3) {

        VASTLog.v(TAG, "surfaceChanged -- (SurfaceHolder callback)");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        VASTLog.v(TAG, "surfaceDestroyed -- (SurfaceHolder callback)");
        cleanUpMediaPlayer();

    }

    // MediaPlayer.OnCompletionListener
    //---------------------------------------------
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        VASTLog.v(TAG, "onCompletion -- (MediaPlayer callback)");

        if (!mIsPlayBackError && !mIsCompleted) {

            mIsCompleted = true;
            processEvent(TRACKING_EVENTS_TYPE.complete);

            invokeOnCompleted();

            stop();
        }
    }

    // MediaPlayer.OnErrorListener
    //---------------------------------------------
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        VASTLog.v(TAG, "onError -- (MediaPlayer callback)");
        VASTLog.v(TAG, "Shutting down Activity due to Media Player errors: WHAT:" + what + ": EXTRA:" + extra + ":");

        mIsPlayBackError = true;

        processErrorEvent();

        stop();

        return true;
    }

    // MediaPlayer.OnPreparedListener
    //---------------------------------------------
    @Override
    public void onPrepared(MediaPlayer mp) {

        VASTLog.v(TAG, "onPrepared --(MediaPlayer callback) ....about to play");

        mIsReady = true;

        invokeOnCachingFinish();

        if (mIsAutoPlay) {
            play();
        }
    }

    // MediaPlayer.OnVideoSizeChangedListener
    //---------------------------------------------
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        VASTLog.v(TAG, "onVideoSizeChanged -- " + width + " x " + height);

        mVideoWidth = width;
        mVideoHeight = height;

        calculateAspectRatio();
    }

    // View.OnClickListener
    //---------------------------------------------
    public void onClick(View v) {

        VASTLog.v(TAG, "onClick -- (View.OnClickListener callback)");

        if (mPlayerContainer == v) {

            onPlayerClicked(v);

        } else if (mPlayerSkip == v) {

            onSkipClicked(v);

        } else if (mPlayerMute == v) {

            onMuteClicked(v);
        }
    }
}
