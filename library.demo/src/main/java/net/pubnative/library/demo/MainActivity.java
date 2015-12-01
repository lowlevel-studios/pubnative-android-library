package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.pubnative.library.model.PubNativeAdModel;
import net.pubnative.library.request.PubNativeRequest;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, PubNativeRequest.PubNativeRequestListener {
    private Button mBtnNative;

    private final String APP_TOKEN = "6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mBtnNative = (Button) findViewById(R.id.btn_native);
        mBtnNative.setOnClickListener(this);
    }

//    public void onNativeClicked(View v){
//
//        Log.d("PubnativeLibrary", "onNativeClicked");
//
//        SimpleClass simpleClass = new SimpleClass();
//        if(simpleClass.isTest()){
//            Log.d("PubnativeLibrary", "test");
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_native:
                PubNativeRequest request = new PubNativeRequest(getApplicationContext());
                request.setIsLogging(true);
                request.setParameter(PubNativeRequest.Parameters.APP_TOKEN, APP_TOKEN);
                request.setParameter(PubNativeRequest.Parameters.AD_COUNT, "2");
                request.setParameter(PubNativeRequest.Parameters.ICON_SIZE, "200x200");
                request.setParameter(PubNativeRequest.Parameters.BANNER_SIZE,"1200x627");
                request.start(PubNativeRequest.AdType.VIDEO, this);
                break;
        }
    }


    @Override
    public void onRequestSuccess(PubNativeRequest request, ArrayList<PubNativeAdModel> ads) {
        Toast.makeText(getApplication().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestFailed(PubNativeRequest request, PubNativeRequest.PubNativeException ex) {
        Toast.makeText(getApplication().getApplicationContext(), ex.getErrMsg(), Toast.LENGTH_SHORT).show();
    }

}
