package org.centerm.Tollway.alipay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.centerm.Tollway.R;

public class ScanQrActivity extends AppCompatActivity {

    public static IntentIntegrator qrScan;
    public static int flag = 0;
    private Intent intent;
    private Intent service;

    private String token="";
    private String type="";
    private String amount;
    private String walletcode;
    private String transid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_scan_qr);

        qrScan = new IntentIntegrator(ScanQrActivity.this);

        qrScan.setCaptureActivity(CaptureExtends.class);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("Scanning...");

        qrScan.setCameraId(0);
        qrScan.initiateScan();
        intent = getIntent();

        type = intent.getExtras().getString("TYPE");
        amount = intent.getExtras().getString("AMOUNT");
        walletcode = intent.getExtras().getString("WALLET_CODE");
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        Log.d("SJM requestCode :: ",  String.valueOf(requestCode));
        Log.d("SJM resultCode :: ", String.valueOf(resultCode));
        Log.d("SJM data :: ", String.valueOf(data));
        Log.d("SJM result :: ", String.valueOf(result));

        if (result != null) {
            if( resultCode == 3){
                qrScan.setCaptureActivity(CaptureExtends.class);
                qrScan.setOrientationLocked(false);
                qrScan.setPrompt("Scanning...");

                qrScan.setCameraId(1);
                qrScan.initiateScan();
            }else if (resultCode == 4 ){
                qrScan.setCaptureActivity(CaptureExtends.class);
                qrScan.setOrientationLocked(false);
                qrScan.setPrompt("Scanning...");

                qrScan.setCameraId(0);
                qrScan.initiateScan();
            }else {
                if (result.getContents() == null) {
                    Log.d("hhg :: 1", "aa " + resultCode);
                    if (type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)) {
                        service = new Intent(ScanQrActivity.this, AliPriceActivity.class);
                        service.putExtra("WALLET_CODE", walletcode);
                        service.putExtra("TYPE", type);
                        service.putExtra("AMOUNT", amount);
                        startActivity(service);
                        finish();
                    } else { //void
                        service = new Intent(ScanQrActivity.this, AliPriceActivity.class );
                        service.putExtra("WALLET_CODE", walletcode);
                        service.putExtra("TYPE", type);
                        startActivity(service);
                        finish();
                    }
                } else {
                    Log.d("hhg :: 2", "bb " + resultCode);
                    token = result.getContents();
                    service = new Intent(ScanQrActivity.this, AliServiceActivity.class);
                    service.putExtra("WALLET_CODE", walletcode);
                    service.putExtra("TYPE", type);
                    service.putExtra("TOKEN", token);
                    service.putExtra("AMOUNT", amount);
                    startActivity(service);
                    finish();
                    Toast.makeText(ScanQrActivity.this, "TOKEN : " + token, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.d("hhg :: 3","cc " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
