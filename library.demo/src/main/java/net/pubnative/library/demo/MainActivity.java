package net.pubnative.library.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onNativeClick(View view) {

        Log.v(TAG, "onNativeClick");
        // Launch native activity
        Intent intent = new Intent(this, NativeAdActivity.class);
        startActivity(intent);
    }

    public void onSettingsClick(View view) {

        Log.v(TAG, "onSettingsClick");
        // Launch native activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
