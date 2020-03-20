package org.centerm.Tollway.alipay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.QrCodeAli;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

public class AliQrActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "GenerateQrActivity";
    private String tagAll = "";

    private Realm realm = null;
    private String aid = "";
    private String billerId = "";
    private String qrTid = "";
    private String nameCompany = "";
    private int nextId;
    private int currentIdObl;
    /***
     * DialogSlip
     */
    private String dateFormatDef;
    private String timeFormat;
    private String dateFormat;

    private String szDateOrg;   //sinn rs232 20180705
    private String szTimeOrg;  //sinn rs232 20180705


    private ImageView qrImage = null;
    private Button qrSuccessBtn = null;

    private Intent intent;
    private String amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_qr);

        qrImage = findViewById( R.id.qrImage);
        qrSuccessBtn = findViewById( R.id.qrSuccessBtn);
        qrSuccessBtn.setOnClickListener(this);

        intent = getIntent();
        amount = intent.getExtras().getString("AMOUNT");
        generatorQr();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrSuccessBtn:
                Intent inquiry = new Intent(AliQrActivity.this, AliServiceActivity.class);
                inquiry.putExtra("TYPE", AliConfig.Inquiry);
                inquiry.putExtra("AMOUNT", amount);
                startActivity(inquiry);
                finish();
                overridePendingTransition(0, 0);
                break;
    }
}

    private void generatorQr() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Date date = new Date();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
        timeFormat = new SimpleDateFormat("HHmmss").format(date);
        dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);

        //SINN copy  QR data & time 20180706
        szDateOrg=dateFormatDef;
        szTimeOrg=timeFormat;
        //END SINN copy  QR data & time 20180706

        aid = Preference.getInstance(this).getValueString( Preference.KEY_QR_AID);
        billerId = Preference.getInstance(this).getValueString( Preference.KEY_QR_BILLER_ID);

        qrTid = Preference.getInstance(this).getValueString( Preference.KEY_QR_TERMINAL_ID) +
                dateFormatDef+timeFormat;  //TID +DATE+TIME

        Log.d(TAG, "onClick: " + qrTid);
        Log.d(TAG, "onClick: " + billerId);
        Log.d(TAG, "onClick: " + aid);
        nameCompany = /*"NAKHONRATCHASIMA PCG."+ */Preference.getInstance(AliQrActivity.this).getValueString( Preference.KEY_QR_MERCHANT_NAME);

        tagAll = Utility.idValue("", "00", "01");
        tagAll = Utility.idValue(tagAll, "01", "11");
        String tagIn30 = Utility.idValue("", "00", aid);
        tagIn30 = Utility.idValue(tagIn30, "01", billerId);
        String tag30 = Utility.idValue("", "30", tagIn30);
        tagAll += tag30;
        tagAll = Utility.idValue(tagAll, "53", "764");
        tagAll = Utility.idValue(tagAll, "54", decimalFormat.format(Double.valueOf(amount)));
        tagAll = Utility.idValue(tagAll, "58", "TH");
        tagAll = Utility.idValue(tagAll, "59", nameCompany);
        String tagIn62 = Utility.idValue("", "07", qrTid);
        String tag62 = Utility.idValue("", "62", tagIn62);
        tagAll += tag62;
//                tagAll = Utility.idValue(tagAll, "63", "");
        tagAll += "6304";
        Log.d(TAG, "initWidget: B " + tagAll);
        tagAll += Utility.CheckSumCrcCCITT(tagAll);
        Log.d(TAG, "initWidget: A " + tagAll);

        //QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(tagAll, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qr_code = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(qr_code);
        } catch (WriterException e) {
            e.printStackTrace();
        }
//        insertGenerateQr();
    }

    private void insertGenerateQr() {
        Number currentId = realm.where(QrCodeAli.class).max("id");
        if (currentId == null) {
            nextId = 1;
        } else {
            currentIdObl = currentId.intValue();
            nextId = currentId.intValue() + 1;
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String traceId = Preference.getInstance(AliQrActivity.this).getValueString( Preference.KEY_INVOICE_NUMBER_ALL); ////SINN 20180713 QR share invoice number
                QrCodeAli qrCodeAli = realm.createObject(QrCodeAli.class, nextId);
                qrCodeAli.setAid(aid);
                qrCodeAli.setQrTid(qrTid);
                qrCodeAli.setBillerId(billerId);
                qrCodeAli.setTrace( CardPrefix.calLen(traceId, 6));
                qrCodeAli.setDate(dateFormat);
                qrCodeAli.setTime(timeFormat);
                qrCodeAli.setNameCompany(nameCompany);
                qrCodeAli.setTextQrGenerateAll(tagAll);
                qrCodeAli.setAmount(amount);
                qrCodeAli.setStatusPrint("0");
                qrCodeAli.setStatusSuccess("0");
                realm.copyFromRealm(qrCodeAli);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                String traceIdOld = Preference.getInstance(AliQrActivity.this).getValueString( Preference.KEY_INVOICE_NUMBER_ALL);
                Preference.getInstance(AliQrActivity.this).setValueString( Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(traceIdOld) + 1));
                Log.d(TAG, "onSuccess: ");
            }
        });
    }
}
