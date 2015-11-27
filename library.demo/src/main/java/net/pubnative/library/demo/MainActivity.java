package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.pubnative.library.SimpleClass;
import net.pubnative.library.demo.model.PubNativeAdModel;
import net.pubnative.library.demo.request.PubNativeRequest;

public class MainActivity extends Activity implements View.OnClickListener, PubNativeRequest.PubNativeRequestListener {
    private Button mbtnNative;

    private final String APP_TOKEN = "6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mbtnNative = (Button) findViewById(R.id.btn_native);
        mbtnNative.setOnClickListener(this);
    }

    public void onNativeClicked(View v){

        Log.d("PubnativeLibrary", "onNativeClicked");

        SimpleClass simpleClass = new SimpleClass();
        if(simpleClass.isTest()){
            Log.d("PubnativeLibrary", "test");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_native:
                PubNativeRequest request = new PubNativeRequest(getApplicationContext());
                request.setParameter(PubNativeRequest.Parameters.APP_TOKEN, APP_TOKEN);
                request.setParameter(PubNativeRequest.Parameters.AD_COUNT, "2");
                request.setParameter(PubNativeRequest.Parameters.ICON_SIZE, "200x200");
                request.setParameter(PubNativeRequest.Parameters.BANNER_SIZE,"1200x627");
                request.start(PubNativeRequest.Type.NATIVE, this);
                break;
        }
    }

    @Override
    public void onRequestSuccess(PubNativeRequest request, PubNativeAdModel ads) {

    }

    @Override
    public void onRequestFailed(PubNativeRequest request, PubNativeRequest.PubNativeException ex) {

    }

}
