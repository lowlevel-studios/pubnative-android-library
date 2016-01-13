package net.pubnative.library.demo.feed;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.pubnative.library.demo.R;
import net.pubnative.library.demo.RequestData;
import net.pubnative.library.model.APIV3VideoAd;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.VideoAdRequest;
import net.pubnative.library.request.VideoAdRequestListener;
import net.pubnative.player.VASTParser;
import net.pubnative.player.VASTPlayer;
import net.pubnative.player.model.VASTModel;

import java.util.ArrayList;
import java.util.List;

public class Video extends RelativeLayout implements VideoAdRequestListener,
                                                     VASTPlayer.Listener {

    private static final String TAG = Video.class.getName();

    private View       progressBar;
    private VASTPlayer player;
    private Button     request;

    public Video(Context context, AttributeSet attrs) {

        super(context, attrs);

        inflate(context, R.layout.feed_video, this);

        progressBar = findViewById(R.id.video_progressBar);

        player = (VASTPlayer) findViewById(R.id.video_player);
        player.setListener(this);

        request = (Button) findViewById(R.id.video_request);
        request.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Video.this.onRequestClick(view);
            }
        });
    }

    public void onRequestClick(View view) {

        Log.v(TAG, "onRequestClick");

        progressBar.setVisibility(VISIBLE);
        parseAd(RequestData.VIDEO_STATIC);

        // TODO: Uncomment when server bet works
//        VideoAdRequest request = new VideoAdRequest(getContext());
//        request.setParameter(PubnativeContract.Request.APP_TOKEN, RequestData.APP_TOKEN);
//        request.start(AdRequest.Endpoint.VIDEO, this);
    }

    public void parseAd(String adString) {

        Log.v(TAG, "parseAd: " + adString);

        new VASTParser(getContext()).setListener(new VASTParser.Listener() {

            @Override
            public void onVASTParserError(int error) {

                Log.d(TAG, "onVASTParserError: " + error);

                Toast.makeText(getContext(), "Video - Error parsing the video", Toast.LENGTH_SHORT);
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {

                Log.d(TAG, "onVASTParserFinished");

                progressBar.setVisibility(GONE);
                player.load(model);
            }

        }).execute(adString);
    }

    //===========================
    // Callbacks
    //===========================

    // VideoAdRequestListener
    //---------------------------

    @Override
    public void onAdRequestStarted(AdRequest request) {

        Log.v(TAG, "onAdRequestStarted");
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {

        Log.v(TAG, "onAdRequestFinished");
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception ex) {

        Log.v(TAG, "onAdRequestFailed: " + ex);

        Toast.makeText(getContext(), "Video - Request error", Toast.LENGTH_SHORT);

        progressBar.setVisibility(GONE);
    }

    @Override
    public void onVideoAdRequestFinished(VideoAdRequest request, List<APIV3VideoAd> ads) {

        Log.v(TAG, "onVideoAdRequestFinished");

        if (ads.size() > 0) {

            APIV3VideoAd ad = ads.get(0);
            Log.v(TAG, "onVideoAdRequestFinished - JSON banner: " + ad.getPreviewBannerURL());
            parseAd(ad.vast_xml);

        } else {

            Toast.makeText(getContext(), "Video - No fill", Toast.LENGTH_SHORT);
        }

        progressBar.setVisibility(GONE);
    }

    // VASTPlayer.Listener
    //---------------------------------



    @Override
    public void onVASTPlayerLoadFinish() {

        Log.v(TAG, "onVASTPlayerLoadFinish");
        player.play();
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {

        Log.v(TAG, "onVASTPlayerFail: " + exception);
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

        Log.v(TAG, "onVASTPlayerPlaybackStart");
    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

        Log.v(TAG, "onVASTPlayerPlaybackFinish");
    }

    @Override
    public void onVASTPlayerClick() {

        Log.v(TAG, "onVASTPlayerClick");
    }
}
