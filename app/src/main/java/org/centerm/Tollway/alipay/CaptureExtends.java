package org.centerm.Tollway.alipay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.journeyapps.barcodescanner.CaptureActivity;

import org.centerm.Tollway.R;

public class CaptureExtends extends CaptureActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        imageView.setBackgroundResource(R.drawable.camera_rotate2);
        this.addContentView(imageView, layoutParams);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ScanQrActivity.flag == 0){
                    /*ScanQrActivity.qrScan.setCameraId(1);
                    ScanQrActivity.qrScan.initiateScan();*/

                    ScanQrActivity.flag = 1;
                    Intent resultIntent = new Intent();
                    setResult(3, resultIntent);
                    finish();
                }else{
                    /*
                    ScanQrActivity.qrScan.setCameraId(0);
                    ScanQrActivity.qrScan.initiateScan();*/
                    ScanQrActivity.flag = 0;
                    Intent resultIntent = new Intent();
                    setResult(4, resultIntent);
                    finish();
                }
            }
        });
    }
}
