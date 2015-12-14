package org.nexage.sourcekit.vast.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.widget.CountDownView;

import org.nexage.sourcekit.util.VASTLog;
import org.nexage.sourcekit.vast.model.VASTModel;

public class VASTPlayerView extends RelativeLayout implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {

    private static String TAG = "VASTPlayerView";

    // EXTRAS
    private VASTModel mVastModel = null;

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

    public VASTPlayerView(Context context) {

        super(context);
    }

    public void startViedeo(VASTModel model) {

        mVastModel = model;
    }

    private void createSurface() {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mSurfaceView = new SurfaceView(getContext());
        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        addView(mSurfaceView);
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

    //=============================================
    // MediaPlayer.OnCompletionListener
    //=============================================
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        VASTLog.d(TAG, "entered onCOMPLETION -- (MediaPlayer callback)");
//        stopVideoProgressTimer();
//
//        if (!mIsPlayBackError && !mIsCompleted) {
//
//            mIsCompleted = true;
//            processEvent(TRACKING_EVENTS_TYPE.complete);
//
//            if (VASTPlayer.listener != null) {
//
//                VASTPlayer.listener.vastComplete();
//            }
//
//            this.onBackPressed();
//        }
    }

    //=============================================
    // MediaPlayer.OnErrorListener
    //=============================================
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        VASTLog.e(TAG, "entered onError -- (MediaPlayer callback)");
//        mIsPlayBackError = true;
//        VASTLog.e(TAG, "Shutting down Activity due to Media Player errors: WHAT:" + what + ": EXTRA:" + extra + ":");
//
//        processErrorEvent();
//        onBackPressed();

        return true;
    }

    private void processErrorEvent() {

        VASTLog.d(TAG, "entered processErrorEvent");

//        List<String> errorUrls = mVastModel.getErrorUrl();
//        fireUrls(errorUrls);
    }

    //=============================================
    // MediaPlayer.OnPreparedListener
    //=============================================
    @Override
    public void onPrepared(MediaPlayer mp) {

        VASTLog.d(TAG, "entered onPrepared called --(MediaPlayer callback) ....about to play");

//        calculateAspectRatio();
//        mMediaPlayer.start();
//
//        mPlayerLayout.setVisibility(View.VISIBLE);
//        mPlayerLoadingBar.setVisibility(View.GONE);
//        mPlayerCountDown.setProgress(0, mMediaPlayer.getDuration());
//
//        startLayoutTimer();
//        startQuartileTimer();
//        startVideoProgressTimer();
//
//        if (!mIsProcessedImpressions) {
//            processImpressions();
//        }
    }

    //=============================================
    // MediaPlayer.OnVideoSizeChangedListener
    //=============================================
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        VASTLog.d(TAG, "entered onVideoSizeChanged -- (MediaPlayer callback)");
//        mVideoWidth = width;
//        mVideoHeight = height;
//        VASTLog.d(TAG, "video size: " + mVideoWidth + "x" + mVideoHeight);
    }
}
