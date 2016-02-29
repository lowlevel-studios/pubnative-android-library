package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.pubnative.library.demo.utils.Settings;

/**
 * Created by davidmartin on 25/02/16.
 */
public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getName();

    private TextView appToken;

    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appToken = (TextView) findViewById(R.id.activity_settings_text_app_token);
        appToken.setText(Settings.getAppToken());
    }

    public void onDoneClick(View view) {

        Log.v(TAG, "onDoneClick");
        Settings.setAppToken(appToken.getText().toString());
        finish();
    }
}
