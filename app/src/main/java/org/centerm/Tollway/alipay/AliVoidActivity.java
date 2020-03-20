package org.centerm.Tollway.alipay;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.ReprintActivity;
import org.centerm.Tollway.activity.qr.ReprintQRCheckActivity;
import org.centerm.Tollway.database.QrCode;

import java.text.DecimalFormat;

import io.realm.Realm;

//import org.centerm.Tollway.alipay.database.AliTemp;

public class AliVoidActivity extends AppCompatActivity implements View.OnClickListener {

    private Realm realm = null;
    private String TAG = "AliVoidActivity";
    private LinearLayout linearLayoutvoid;
    private LinearLayout linearLayoutsuccess;
    private LinearLayout linearLayoutcheck;
    private LinearLayout linearLayoutnext;

    private ImageView img_type;
    private TextView txt_type;
    private TextView txt_amt;
    private TextView txt_date;
    private TextView txt_time;
    private TextView txt_trace;
    private TextView txt_status;
    private TextView txt_reference;
    private TextView txt_ref1;
    private TextView txt_ref2;
    private TextView txt_buller;

    private Button btn_reprint1; //VOID
    private Button btn_reprint2; //SUCCESS
    private Button btn_cancel;
    private Button btn_check;
    private Button btn_next;

    private Intent intent;

    private Drawable icon;
    private String function;
    private String type;
    private String amt;
    private String fee;
    private String amtplusfee;
    private String date;
    private String time;
    private String trace;

    private String qrTid = "";

    private String status;
    private String reference;
    private String ref1;
    private String ref2;
    private String buller;
    private String StatusSuccess;

    private String respcode;

    public static final String KEY_INTERFACE_INV = ReprintActivity.class.getName() + "_key_invoice_number";  //SINN 20180706 Add QR print.
    public static final String KEY_INTERFACE_REPRINT_TYPE = ReprintActivity.class.getName() + "_key_reprint_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_void);

        initIntent();
        initWidget();

        functon();
    }

    private void functon() {

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            QrCode aliTemp;
            if(qrTid!=null)
                aliTemp = realm.where(QrCode.class).equalTo("trace", trace).equalTo("qrTid",qrTid).findFirst();
            else
                aliTemp = realm.where(QrCode.class).equalTo("trace", trace).findFirst();

            String tmp = "";
            if (aliTemp != null) {
                type = aliTemp.getHostTypeCard();

                switch (type) {
                    case "QR":
                        date = aliTemp.getDate();
                        tmp = aliTemp.getTime();
                        time = tmp.substring(0, 2) + ":" + tmp.substring(2, 4) + ":" + tmp.substring(4, 6);
                        status = "N"; //20181015 Now Qr not supp void
                        respcode = aliTemp.getStatusSuccess();
                        amt = aliTemp.getAmount();
                        reference = aliTemp.getQrTid();
                        ref1 = aliTemp.getRef1();
                        ref2 = aliTemp.getRef2();
                        buller = aliTemp.getBillerId();
                        StatusSuccess = aliTemp.getStatusSuccess();

                        if (status.equals("N")) {
                            if (respcode.equals("1")) {
                                status = "Success";
                                txt_amt.setTextColor(Color.parseColor("#1DDB16")); //green
                                if (function.equals(AliConfig.Void))
                                    linearLayoutnext.setVisibility(View.VISIBLE);
                                else
                                    linearLayoutsuccess.setVisibility(View.VISIBLE);
                            } else {
                                status = "waiting";
                                txt_amt.setTextColor(Color.parseColor("#F9C629")); //yellow
                                if (function.equals(AliConfig.Void))
                                    linearLayoutnext.setVisibility(View.VISIBLE);
                                else
                                    linearLayoutcheck.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (respcode.equals("1")) {
                                status = "Void";
                                txt_amt.setTextColor(Color.parseColor("#CC3333")); //red
                                if (function.equals(AliConfig.Void))
                                    linearLayoutnext.setVisibility(View.VISIBLE);
                                else
                                    linearLayoutvoid.setVisibility(View.VISIBLE);
                            } else {
                                status = "Time Out";
                                txt_amt.setTextColor(Color.parseColor("#CC3333")); //red
                                linearLayoutcheck.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case "ALIPAY":
                    case "WECHAT":
                        tmp = aliTemp.getReqChannelDtm();
                        date = tmp.substring(8, 10) + "/" + tmp.substring(5, 7) + "/" + tmp.substring(0, 4);
                        time = tmp.substring(11, tmp.length() - 4);
                        status = aliTemp.getVoidFlag();
                        amt = aliTemp.getAmt();
                        fee = aliTemp.getFee();
                        if (!fee.equals("null")) //20181115Jeff
                            amt = aliTemp.getAmtplusfee();
                        respcode = aliTemp.getRespcode();

                        if (status.equals("N")) {
                            if (respcode.equals("0")) {
                                status = "Success";
                                txt_amt.setTextColor(Color.parseColor("#1DDB16")); //green
                                if (function.equals(AliConfig.Void))
                                    linearLayoutnext.setVisibility(View.VISIBLE);
                                else
                                    linearLayoutsuccess.setVisibility(View.VISIBLE);
                            } else {
                                status = "waiting";
                                txt_amt.setTextColor(Color.parseColor("#F9C629")); //yellow
                                if (function.equals(AliConfig.Void))
                                    linearLayoutnext.setVisibility(View.VISIBLE);
                                else
                                    linearLayoutcheck.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (respcode.equals("0")) {
                                status = "Void";
                                txt_amt.setTextColor(Color.parseColor("#CC3333")); //red
                                if (function.equals(AliConfig.Void))
                                    linearLayoutnext.setVisibility(View.VISIBLE);
                                else
                                    linearLayoutvoid.setVisibility(View.VISIBLE);
                            } else {
                                status = "Time Out";
                                txt_amt.setTextColor(Color.parseColor("#CC3333")); //red
                                linearLayoutcheck.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                }

                //Set data
//                if (type.equals("ALIPAY")) {
//                    img_type.setBackgroundResource(R.drawable.ic_alipay);
//                    txt_type.setText("Alipay");
//                } else if (type.equals("WECHATPAY")) {
//                    img_type.setBackgroundResource(R.drawable.ic_wechat);
//                    txt_type.setText("Wechat");
//                } else {
//                    img_type.setBackgroundResource(R.drawable.ic_qr);
//                    txt_type.setText("QR Payment");
//                }
                //K.GAME 181022
                if (type.equals("ALIPAY")) {
                    img_type.setBackgroundResource( R.drawable.ic_alipay);
                    txt_type.setText("Alipay");
                    txt_status.setText(status.toUpperCase());
                    if(status.equals("Void"))
                        txt_amt.setText("-"+decimalFormat.format((Double.valueOf(amt.replaceAll(",", "")))) + " บาท");  // Paul_20190205 space 1
                    else
                        txt_amt.setText(decimalFormat.format((Double.valueOf(amt.replaceAll(",", "")))) + " บาท");  // Paul_20190205 space 1

                } else if (type.equals("WECHAT")) {
                    img_type.setBackgroundResource( R.drawable.ic_wechat);
//                    txt_type.setText("Wechat");
                    txt_type.setText("WeChat Pay");     // Paul_20190323
                    txt_status.setText(status.toUpperCase());
                    if(status.equals("Void"))
                        txt_amt.setText("-"+decimalFormat.format((Double.valueOf(amt.replaceAll(",", "")))) + " บาท");  // Paul_20190205 space 1
                    else
                        txt_amt.setText(decimalFormat.format((Double.valueOf(amt.replaceAll(",", "")))) + " บาท");  // Paul_20190205 space 1
                } else {
                    img_type.setBackgroundResource( R.drawable.ic_qr);
                    txt_type.setText("QR Payment");
                    if (StatusSuccess.equals("1")) {
                        txt_amt.setTextColor(Color.parseColor("#1DDB16")); //green
                        txt_amt.setText(decimalFormat.format((Double.valueOf(amt.replaceAll(",", "")))) + " บาท");  // Paul_20190205 space 1 //K.GAME 181024 replaceAll
                        txt_status.setText("SUCCESS");

                        linearLayoutcheck.setVisibility(View.GONE);//K.GAME 181024
                        linearLayoutsuccess.setVisibility(View.VISIBLE);//K.GAME 181024
                        linearLayoutnext.setVisibility(View.GONE);//K.GAME 181024


                    } else if (StatusSuccess.equals("0")) {
                        txt_amt.setTextColor(Color.parseColor("#F9C629")); //yellow
                        txt_amt.setText(decimalFormat.format((Double.valueOf(amt.replaceAll(",", "")))) + " บาท");  // Paul_20190205 space 1 //K.GAME 181024 replaceAll
                        txt_status.setText("PENDING");
                        linearLayoutcheck.setVisibility(View.VISIBLE);//K.GAME 181024
                        linearLayoutsuccess.setVisibility(View.GONE);//K.GAME 181024
                    } else {
                        txt_amt.setTextColor(Color.parseColor("#CC3333")); //red
                        txt_amt.setText("-" + decimalFormat.format((Double.valueOf(amt.replaceAll(",", "")))) + " บาท");    // Paul_20190205 space 1 //K.GAME 181024 replaceAll
                        txt_status.setText("VOID");
                    }
                }

                txt_date.setText(date);
                txt_time.setText(time);
                txt_trace.setText(trace);
                txt_reference.setText(reference);
                txt_ref1.setText(ref1);
                txt_ref2.setText(ref2);
                txt_buller.setText(buller);
            } else {
                Toast.makeText(AliVoidActivity.this, "No Transaction . . .", Toast.LENGTH_SHORT).show();
                finish();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }
    }

    private void initIntent() {
        intent = getIntent();
        trace = intent.getExtras().getString("INVOICE");
        function = intent.getExtras().getString("TYPE");
        qrTid = intent.getExtras().getString("QrTid");
        Log.d(TAG, "trace = " + trace);
    }

    private void initWidget() {
        linearLayoutvoid = findViewById( R.id.linearLayoutvoid);
        linearLayoutsuccess = findViewById( R.id.linearLayoutsuccess);
        linearLayoutcheck = findViewById( R.id.linearLayoutcheck);
        linearLayoutnext = findViewById( R.id.linearLayoutnext);

        img_type = findViewById( R.id.img_type);
        txt_type = findViewById( R.id.txt_type);
        txt_amt = findViewById( R.id.txt_amt);
        txt_date = findViewById( R.id.txt_date);
        txt_time = findViewById( R.id.txt_time);
        txt_trace = findViewById( R.id.txt_trace);
        txt_status = findViewById( R.id.txt_status);
        txt_reference = findViewById( R.id.txt_reference);
        txt_ref1 = findViewById( R.id.txt_ref1);
        txt_ref2 = findViewById( R.id.txt_ref2);
        txt_buller = findViewById( R.id.txt_buillerid);

        btn_reprint1 = findViewById( R.id.btn_reprint1);
        btn_reprint2 = findViewById( R.id.btn_reprint2);
        btn_cancel = findViewById( R.id.btn_cancel);
        btn_check = findViewById( R.id.btn_check);
        btn_next = findViewById( R.id.btn_next);

        btn_reprint1.setOnClickListener(this);
        btn_reprint2.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_check.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (status.equals("Success")) {
                    Intent sale = new Intent(AliVoidActivity.this, AliServiceActivity.class);
                    sale.putExtra("TYPE", AliConfig.Void);
                    sale.putExtra("WALLET_CODE", type);
                    sale.putExtra("INVOICE", trace);
                    startActivity(sale);
                    finish();
                } else
                    Toast.makeText(AliVoidActivity.this, "Status :: Can't  VOID Transaction", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_reprint1:  //full
////                Intent void_reprint = new Intent(AliVoidActivity.this, AliSlipActivity.class);
////                void_reprint.putExtra("STATUS", AliConfig.Success);
////                void_reprint.putExtra("TYPE", AliConfig.Void);
////                void_reprint.putExtra("INVOICE", trace);
////                void_reprint.putExtra("INVOICE", trace);
////                void_reprint.putExtra("AMOUNT", "-" + amt);
////                startActivity(void_reprint);
////                finish();
                if (type.equals("QR")) {
                    Intent intent = new Intent(AliVoidActivity.this, ReprintQRCheckActivity.class);
                    intent.putExtra( MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, trace);
                    intent.putExtra( MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else{
                    Intent void_reprint = new Intent(AliVoidActivity.this, AliReprintActivity.class);
                    void_reprint.putExtra("STATUS", AliConfig.Success);
                    void_reprint.putExtra("TYPE", AliConfig.Void);
                    void_reprint.putExtra("INVOICE", trace);
                    void_reprint.putExtra("AMOUNT", "-" + amt);
                    startActivity(void_reprint);
                    finish();
                }


                break;
            case R.id.btn_reprint2:   //ui show
//                Intent sale_reprint = new Intent(AliVoidActivity.this, AliSlipActivity.class);
//                sale_reprint.putExtra("STATUS", AliConfig.Success);
//                sale_reprint.putExtra("TYPE", AliConfig.Sale);
//                sale_reprint.putExtra("INVOICE", trace);
//                sale_reprint.putExtra("AMOUNT", "-" + amt);
//                startActivity(sale_reprint);
//                finish();

                if (type.equals("QR")) {
                    Intent intent = new Intent(AliVoidActivity.this, ReprintQRCheckActivity.class);
                    intent.putExtra( MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, trace);
                    intent.putExtra( MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else{
                    Intent sale_reprint = new Intent(AliVoidActivity.this, AliReprintActivity.class);
                    sale_reprint.putExtra("STATUS", AliConfig.Success);
                    sale_reprint.putExtra("TYPE", AliConfig.Sale);
                    sale_reprint.putExtra("INVOICE", trace);
                    sale_reprint.putExtra("AMOUNT", amt);
                    startActivity(sale_reprint);
                    finish();
                }

                break;
            case R.id.btn_check:

                if (type.equals("QR")) {
//                    Intent intent = new Intent(AliVoidActivity.this, ReprintQrActivity.class);
//                    Intent intent = new Intent(AliVoidActivity.this, CheckQrActivity.class);
//                    Intent intent = new Intent(AliVoidActivity.this, ReprintQRCheckActivity.class);
//                    intent.putExtra(KEY_INTERFACE_INV, trace);
//                    intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, "1");
//                    startActivity(intent);
//                    finish();

                    Intent intent = new Intent(AliVoidActivity.this, ReprintQRCheckActivity.class);
                    intent.putExtra( MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, trace);
                    intent.putExtra( MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else {
                    Intent inquiry = new Intent(AliVoidActivity.this, AliServiceActivity.class);
                    inquiry.putExtra("TYPE", AliConfig.Inquiry);
                    inquiry.putExtra("WALLET_CODE", type);
                    inquiry.putExtra("INVOICE", trace);
                    startActivity(inquiry);
                    finish();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
