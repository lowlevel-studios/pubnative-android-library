package net.pubnative.library.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    private final String APP_TOKEN = "6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }
}
