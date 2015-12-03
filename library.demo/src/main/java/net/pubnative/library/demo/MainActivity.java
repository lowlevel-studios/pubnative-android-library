package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import net.pubnative.library.SimpleClass;

public class MainActivity extends Activity {
    private final String APP_TOKEN = "6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onNativeClicked(View v){
      Log.d("PubnativeLibrary", "onNativeClicked");

        SimpleClass simpleClass = new SimpleClass();
        if(simpleClass.isTest()){
            Log.d("PubnativeLibrary", "test");
        }
    }
}

