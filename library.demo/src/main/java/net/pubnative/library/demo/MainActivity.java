package net.pubnative.library.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.pubnative.library.Pubnative;
import net.pubnative.library.predefined.PubnativeActivityListener;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements PubnativeActivityListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override

    protected void onPause() {

        super.onPause();
        Pubnative.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        Pubnative.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Pubnative.onDestroy();
    }


    public void onFeedClick(View view) {

        Intent feedIntent = new Intent(this, FeedActivity.class);
        startActivity(feedIntent);
    }

    public void onVideoInterstitialClick(View view) {

        Pubnative.show(this, Pubnative.FullScreen.VIDEO, RequestData.APP_TOKEN, this);
    }

    public void onInterstitialClick(View view) {

        Pubnative.show(this, Pubnative.FullScreen.INTERSTITIAL, RequestData.APP_TOKEN, this);
    }

    public void onGameListClick(View view) {

        Pubnative.show(this, Pubnative.FullScreen.GAME_LIST, RequestData.APP_TOKEN, this);
    }

    public void onSettingsClick(View view) {

        Toast.makeText(this, "Under development", Toast.LENGTH_SHORT).show();
    }

    //PubnativeActivityListener
    @Override
    public void onPubnativeActivityStarted(String identifier) {

        Log.v("pubnative-library-demo", "onPubnativeActivityStarted: " + identifier);
    }

    @Override
    public void onPubnativeActivityFailed(String identifier, Exception exception) {

        Log.v("pubnative-library-demo", "onPubnativeActivityFailed: " + identifier + " - Exception: " + exception);
    }

    @Override
    public void onPubnativeActivityOpened(String identifier) {

        Log.v("pubnative-library-demo", "onPubnativeActivityOpened: " + identifier);
    }

    @Override
    public void onPubnativeActivityClosed(String identifier) {

        Log.v("pubnative-library-demo", "onPubnativeActivityClosed: " + identifier);
    }
}
