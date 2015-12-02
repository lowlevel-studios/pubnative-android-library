package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.pubnative.library.model.PubnativeAdModel;
import net.pubnative.library.request.PubnativeRequest;

import java.util.List;

public class Main2Activity extends Activity implements PubnativeRequest.Listener {
    private final String APP_TOKEN = "6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onNativeClicked(View v){
        PubnativeRequest request = new PubnativeRequest();
        request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, APP_TOKEN);
        request.setParameter(PubnativeRequest.Parameters.AD_COUNT, "2");
        request.setParameter(PubnativeRequest.Parameters.ICON_SIZE, "200x200");
        request.setParameter(PubnativeRequest.Parameters.BANNER_SIZE,"1200x627");
        request.start(getApplicationContext(), PubnativeRequest.EndPoint.NATIVE, this);
    }

    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {
        Toast.makeText(getApplication().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPubnativeRequestFail(PubnativeRequest request, Exception ex) {
        Toast.makeText(getApplication().getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
    }
}

