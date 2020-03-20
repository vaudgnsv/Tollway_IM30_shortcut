package org.centerm.Tollway.activity.qr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;
import com.google.gson.JsonElement;

import org.centerm.Tollway.BuildConfig;
import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.adapter.MenuQrAdapter;
import org.centerm.Tollway.alipay.AliConfig;
import org.centerm.Tollway.alipay.AliPriceActivity;
import org.centerm.Tollway.alipay.AliReprintActivity;
import org.centerm.Tollway.alipay.AliServiceActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.manager.HttpManager;
import org.centerm.Tollway.model.Check;
import org.centerm.Tollway.model.MenuQr;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;

//import org.centerm.Tollway.activity.MenuServiceActivity;

public class MenuQrActivity extends SettingToolbarActivity {

    private RecyclerView recyclerViewMenuQr = null;
    private MenuQrAdapter menuQrAdapter = null;
    private List<MenuQr> menuList = null;

    private Realm realm = null;
    private ImageView bankImage = null;
    private ImageView bank1Image = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private TextView qrTidLabel = null;
    private TextView billerLabel = null;
    private TextView traceLabel = null;
//    private TextView dateLabel = null;
//    private TextView timeLabel = null;
    private TextView dateSlipLabel = null;
    private TextView timeSlipLabel = null;
    private TextView comCodeLabel = null;
    private TextView amtThbLabel = null;
    private ImageView qrImage = null;
    private RelativeLayout ref1RelativeLayout = null;
    private TextView ref1Label = null;
    private RelativeLayout ref2RelativeLayout = null;
    private TextView ref2Label = null;
//    private RelativeLayout ref3RelativeLayout = null;
//    private TextView ref3Label = null;
    private LinearLayout slipLinearLayout = null;
    private Button printBtn = null;
    private AidlPrinter printDev = null;
    private QrCode qrCode;
    private CardManager cardManager = null;
    private View slipView;

    private int qrId = 0;
    private String trace;
    private String voidFlag;
    private String respcode;
    private String amount;
    private String fee;
    private Check check;
    private final String TAG = "MenuQrActivity";
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private TextView midLabel;
    private TextView batchLabel;
    private TextView apprCodeLabel;
    private TextView inquiryLabel;
    private Dialog dialogLoading;

    private ImageView img_krungthai1;//K.GAME 181016
    private ImageView img_krungthai2;//K.GAME 181016
    private TextView app_title;//K.GAME 181016
    private String str_app_title = "คิวอาร์โค้ด";//K.GAME 181016

    private TextView name_sw_version;  // Paul_20190125 software version print

    private String dateFormat = null;
    private String timeFormat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_qr);
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        initWidget();
        // initBtnExit();
    }

    @Override
    public void initWidget() {
        //K.GAME 181016 hard code
        app_title = findViewById(R.id.app_title);
        app_title.setText(str_app_title);//Title

        img_krungthai1 = findViewById(R.id.img_krungthai1);
        img_krungthai2 = findViewById(R.id.img_krungthai2);
        if (!Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {

            img_krungthai1.setVisibility(View.INVISIBLE);
            img_krungthai2.setVisibility(View.VISIBLE);
        }//END K.GAME 181016 hard code

//        super.initWidget();
        recyclerViewMenuQr = findViewById(R.id.recyclerViewMenuQr);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); //K.GAME 180823 change UI
        gridLayoutManager.setSpanCount(3);//K.GAME 180823 change UI
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);//K.GAME 180823 change UI
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMenuQr.setLayoutManager(layoutManager);
        customDialogOutOfPaper();
        customDialogLoading();
        setMenuQr();

        setViewPrintSlip();
    }

    private void setMenuQr() {
        if (recyclerViewMenuQr.getAdapter() == null) {
            menuQrAdapter = new MenuQrAdapter(this);
            recyclerViewMenuQr.setAdapter(menuQrAdapter);
            menuQrAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int position = (int) v.getTag();

                    // Paul_20181023 Start : Selete Enable
//                    System.out.printf("utility:: AAAAAAAAAAAAAAAAAAAAAAAA menuList.get(position).getName() = %s \n",menuList.get(position).getName());
                    switch (menuList.get(position).getName())
                    {
                        case "สร้าง\nคิวอาร์โค้ด":
                            Intent intent = new Intent(MenuQrActivity.this, GenerateQrActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;
                        case "Alipay ":
                            Intent ali = new Intent(MenuQrActivity.this, AliPriceActivity.class);
                            ali.putExtra("TYPE", "ALI_SALE");
                            startActivity(ali);
                            break;
                        case "WeChat Pay":
                            Intent wechat = new Intent(MenuQrActivity.this, AliPriceActivity.class);
                            wechat.putExtra("TYPE", "WECHAT_SALE");
                            startActivity(wechat);
                            break;
                        case "ตรวจสอบ\nรายการก่อนหน้า":
                            int cnt;
                            String type;

                            if (realm == null) {
                                realm = Realm.getDefaultInstance();
                            }
                            Number currentId = realm.where(QrCode.class).max("id");

                            if(currentId != null)
                                cnt = currentId.intValue() - 1;
                            else
                                cnt = -1;
                            RealmResults<QrCode> qrCode = realm.where(QrCode.class).findAll();
                            if (cnt >= 0) {
                                type = qrCode.get(cnt).getHostTypeCard();

                                switch(type){
                                    case "QR" :
                                        if (qrCode != null)
                                            qrId = qrCode.get(cnt).getId();
                                            trace = qrCode.get(cnt).getTrace();

                                        if (qrCode.get(cnt).getStatusSuccess().equalsIgnoreCase("0")) { //20181130Jeff
                                            print();
                                        } else {
                                            Intent qr_reprint = new Intent(MenuQrActivity.this, ReprintQRCheckActivity.class);
                                            qr_reprint.putExtra( MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, trace);
                                            qr_reprint.putExtra( MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
                                            startActivity(qr_reprint);
                                            overridePendingTransition(0, 0);
                                            finish();
                                        }
                                        break;
                                    case "ALIPAY" :
                                    case "WECHAT" :
                                        if (qrCode != null) {
                                            trace = qrCode.get(cnt).getTrace();
                                            respcode = qrCode.get(cnt).getRespcode();
                                            amount = qrCode.get(cnt).getAmt();
                                            voidFlag = qrCode.get(cnt).getVoidFlag();
                                            fee = qrCode.get(cnt).getFee();
                                            if(!fee.equals("null")) //20181115Jeff
                                                amount = qrCode.get(cnt).getAmtplusfee();
                                        }

                                        if(respcode.equals("1")){
                                            Intent service = new Intent(MenuQrActivity.this, AliServiceActivity.class);
                                            service.putExtra("INVOICE", trace);
                                            service.putExtra("TYPE", AliConfig.Inquiry);
                                            service.putExtra("WALLET_CODE", type);
                                            startActivity(service);
                                            finish();
                                        }else{
                                            //Reprint
                                            if(voidFlag.equals("N")){
                                                Intent service = new Intent(MenuQrActivity.this, AliReprintActivity.class);
                                                service.putExtra("STATUS", AliConfig.Success);
                                                service.putExtra("TYPE", AliConfig.Sale);
                                                service.putExtra("INVOICE", trace);
                                                service.putExtra("AMOUNT", amount);
                                                startActivity(service);
                                                finish();
                                            }else{
                                                Intent void_reprint = new Intent(MenuQrActivity.this, AliReprintActivity.class);
                                                void_reprint.putExtra("STATUS", AliConfig.Success);
                                                void_reprint.putExtra("TYPE", AliConfig.Void);
                                                void_reprint.putExtra("INVOICE", trace);
                                                void_reprint.putExtra("AMOUNT", "-" + amount);
                                                startActivity(void_reprint);
                                                finish();
                                            }
                                        }
                                }
                            }else {
                                Utility.customDialogAlert(MenuQrActivity.this, "ไม่มีรายการ", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                            break;
                        case "ตรวจสอบ\nรายการ":
                            Intent inquiry = new Intent( MenuQrActivity.this, InquiryQrActivity.class ); //CheckQrActivity
                            startActivity(inquiry);
                            finish();
                            overridePendingTransition( 0, 0 );
                            break;
                    }
                    // Paul_20181023 End : Selete Enable

//                    switch(position) {
//                        case 0:
//                            Intent intent = new Intent(MenuQrActivity.this, GenerateQrActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
//                            break;
//                        // Paul_20181022 x alipay , wechat
//                        case 1:
//                            Intent ali = new Intent(MenuQrActivity.this, AliPriceActivity.class);
//                            ali.putExtra("TYPE", "ALI_SALE");
//                            startActivity(ali);
//                            break;
//                        case 2:
//                            Intent wechat = new Intent(MenuQrActivity.this, AliPriceActivity.class);
//                            wechat.putExtra("TYPE", "WECHAT_SALE");
//                            startActivity(wechat);
//                            break;
//                        case 3:
//                            int cnt;
//                            String type;
//
//                            if (realm == null) {
//                                realm = Realm.getDefaultInstance();
//                            }
//                            Number currentId = realm.where(QrCode.class).max("id");
//
//                            if(currentId != null)
//                                cnt = currentId.intValue() - 1;
//                            else
//                                cnt = -1;
//                            RealmResults<QrCode> qrCode = realm.where(QrCode.class).findAll();
//                            if (cnt >= 0) {
//                                type = qrCode.get(cnt).getHostTypeCard();
//
//                                switch(type){
//                                    case "QR" :
//                                        if (qrCode != null)
//                                            qrId = qrCode.get(cnt).getId();
//                                        if (qrCode.get(cnt).getStatusSuccess().equalsIgnoreCase("0")) {
//                                            print();
//                                        } else {
//                                            Utility.customDialogAlertSuccess(MenuQrActivity.this, "รายการนี้ชำระเงินแล้ว", new Utility.OnClickCloseImage() {
//                                                @Override
//                                                public void onClickImage(Dialog dialog) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                        }
//                                        break;
//                                    case "ALIPAY" :
//                                    case "WECHATPAY" :
//                                        if (qrCode != null)
//                                            trace = qrCode.get(cnt).getTrace();
//
//                                        Intent service = new Intent(MenuQrActivity.this, AliServiceActivity.class);
//                                        service.putExtra("INVOICE", trace);
//                                        service.putExtra("TYPE", AliConfig.Inquiry);
//                                        service.putExtra("WALLET_CODE", type);
//                                        startActivity(service);
//                                        finish();
//                                }
//                            }else {
//                                Utility.customDialogAlert(MenuQrActivity.this, "ไม่มีรายการ", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                            }
//                            break;
//                        case 4:
//                            Intent inquiry = new Intent( MenuQrActivity.this, InquiryQrActivity.class ); //CheckQrActivity
//                            startActivity(inquiry);
//                            finish();
//                            overridePendingTransition( 0, 0 );
//                            break;
//                    }
                }
            });
        } else {
            menuQrAdapter.clear();
        }
        if (menuList == null) {
            menuList = new ArrayList<>();
        } else {
            menuList.clear();
        }
//        MenuQr menuQr1 = new MenuQr();
////        menuQr1.setName("Generator QR ");
//        menuQr1.setName("สร้าง\nคิวอาร์โค้ด");//K.GAME 181016
//        menuQr1.setImage(R.drawable.ic_qr);

// Paul_20181023 Start : Selete Enable
        MenuQr menuQr2 = new MenuQr();
        if (Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
            menuQr2.setName("Alipay ");
            menuQr2.setImage(R.drawable.ic_alipay);
        }
        MenuQr menuQr3 = new MenuQr();
        if (Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
            menuQr3.setName("WeChat Pay");
            menuQr3.setImage(R.drawable.ic_wechat);
        }
// Paul_20181023 End : Selete Enable

//        MenuQr menuQr4 = new MenuQr();
//        menuQr4.setName("ตรวจสอบ\nรายการก่อนหน้า");
//        menuQr4.setImage(R.drawable.ic_inquiry);

        MenuQr menuQr5 = new MenuQr();
        menuQr5.setName("ตรวจสอบ\nรายการ");
        menuQr5.setImage(R.drawable.ic_inquiry);

//        menuList.add(menuQr1);

// Paul_20181023 Start : Selete Enable
        if (Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
            menuList.add(menuQr2);
        }
        if (Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
            menuList.add(menuQr3);
        }
// Paul_20181023 End : Selete Enable
//        menuList.add(menuQr4);
        menuList.add(menuQr5);
        menuQrAdapter.addItem(menuList);
        menuQrAdapter.notifyDataSetChanged();

    }


    public void setViewPrintSlip() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        slipView = inflater.inflate(R.layout.view_slip_qr, null);

        bank1Image = slipView.findViewById(R.id.bank1Image);

        merchantName1Label = slipView.findViewById(R.id.merchantName1Label);
        merchantName2Label = slipView.findViewById(R.id.merchantName2Label);
        merchantName3Label = slipView.findViewById(R.id.merchantName3Label);
        midLabel = slipView.findViewById(R.id.midLabel);
        batchLabel = slipView.findViewById(R.id.batchLabel);
        apprCodeLabel = slipView.findViewById(R.id.apprCodeLabel);
        inquiryLabel = slipView.findViewById(R.id.inquiryLabel);
        qrTidLabel = slipView.findViewById(R.id.qrTidLabel);
        billerLabel = slipView.findViewById(R.id.billerLabel);
        traceLabel = slipView.findViewById(R.id.traceLabel);
//        dateLabel = slipView.findViewById(R.id.dateLabel);
//        timeLabel = slipView.findViewById(R.id.timeLabel);
        dateSlipLabel = slipView.findViewById(R.id.dateLabel);
        timeSlipLabel = slipView.findViewById(R.id.timeLabel);
        comCodeLabel = slipView.findViewById(R.id.comCodeLabel);
        amtThbLabel = slipView.findViewById(R.id.amtThbLabel);
//        qrImage = slipView.findViewById(R.id.qrImage);
        ref1RelativeLayout = slipView.findViewById(R.id.ref1RelativeLayout);
        ref1Label = slipView.findViewById(R.id.ref1Label);
        ref2RelativeLayout = slipView.findViewById(R.id.ref2RelativeLayout);
        ref2Label = slipView.findViewById(R.id.ref2Label);
//        ref3RelativeLayout = slipView.findViewById(R.id.ref3RelativeLayout);
//        ref3Label = slipView.findViewById(R.id.ref3Label);
        slipLinearLayout = slipView.findViewById(R.id.slipLinearLayout);
        printBtn = slipView.findViewById(R.id.printBtn);
        name_sw_version = slipView.findViewById(R.id.name_sw_version);   // Paul_20190125 software version print
    }

    private void print() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

                if (!Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                    merchantName1Label.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                if (!Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                    merchantName2Label.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                if (!Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                    merchantName3Label.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                if (realm == null) {
                    realm = Realm.getDefaultInstance();
                }
                qrCode = realm.where(QrCode.class).equalTo("id", qrId).findFirst();

                String __tid ;
//                        = Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS);
                String __mid ;
//                        = Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS);
                __tid = Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID);
                __mid = Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_QR_MERCHANT_ID);

                qrTidLabel.setText(__tid);
                midLabel.setText(__mid);

//                qrTidLabel.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
//                midLabel.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
//                batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS),6));
                int batch = Integer.parseInt(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                apprCodeLabel.setText("000000");
                inquiryLabel.setText(qrCode.getQrTid());
                billerLabel.setText(qrCode.getBillerId());
                traceLabel.setText(qrCode.getTrace());
                Preference.getInstance(MenuQrActivity.this).setValueString(Preference.KEY_QR_LAST_TRACE, qrCode.getTrace());   //SINN 20181121 sometime call check last tr with this
// Paul_20190131
                Date date = new Date();
                if(qrCode.getStatusSuccess().equalsIgnoreCase( "1" )) {
                    dateFormat = qrCode.getDate();
                    timeFormat = qrCode.getTime();
                } else
                {
                    dateFormat = new SimpleDateFormat( "dd/MM/yyyy" ).format( date );
                    timeFormat = new SimpleDateFormat( "HHmmss" ).format( date );
                }
                dateSlipLabel.setText(dateFormat);
                String timeHH = timeFormat.substring(0, 2);
                String timeMM = timeFormat.substring(2, 4);
                String timeSS = timeFormat.substring(4, 6);
                timeSlipLabel.setText(timeHH + ":" + timeMM + ":" + timeSS);
                qrId = qrCode.getId();
//                comCodeLabel.setText(qrCode.getComCode());
                amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(qrCode.getAmount().replaceAll(",","")))));
                /*if (qrCode.getRef1() != null) {
                    ref1RelativeLayout.setVisibility(View.VISIBLE);
                    ref1Label.setText(qrCode.getRef1());
                }
                if (qrCode.getRef2() != null) {
                    ref2RelativeLayout.setVisibility(View.VISIBLE);
                    ref2Label.setText(qrCode.getRef2());
                }*/
//                qrImage.setImageBitmap(Utility.createQRImage(qrCode.getTextQrGenerateAll(), 300, 300));
                name_sw_version.setVisibility(View.VISIBLE);             // Paul_20190125 software version print
                name_sw_version.setText( BuildConfig.VERSION_NAME);      // Paul_20190125 software version print
                setMeasureSlip();

                check = new Check();
                check.setBillerId(qrCode.getBillerId());
                check.setTerminalId(qrCode.getQrTid());
                check.setRef1(qrCode.getRef1());
                check.setRef2(qrCode.getRef2());

                if(!qrCode.getRef1().isEmpty()){
                    ref1Label.setText(qrCode.getRef1());
                    ref1RelativeLayout.setVisibility(View.VISIBLE);
                }else {
                    ref1RelativeLayout.setVisibility(View.GONE);
                }

                if(!qrCode.getRef2().isEmpty()){
                    ref2Label.setText(qrCode.getRef2());
                    ref2RelativeLayout.setVisibility(View.VISIBLE);
                }else {
                    ref2RelativeLayout.setVisibility(View.GONE);
                }

//                if(!qrCode.getRef3().isEmpty()){
//                    ref3Label.setText(qrCode.getRef3());
//                    ref3RelativeLayout.setVisibility(View.VISIBLE);
//                }else {
//                    ref3RelativeLayout.setVisibility(View.GONE);
//                }

                requestCheckSlip();
            }
        });
    }

    private void setMeasureSlip() {
        slipView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        slipView.layout(0, 0, slipView.getMeasuredWidth(), slipView.getMeasuredHeight());
    }


    private void requestCheckSlip() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dialogLoading.show();
            }
        });
        HttpManager.getInstance().getService().checkQr(check)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<JsonElement>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<JsonElement> jsonElementResponse) {
                        try {
                            if (jsonElementResponse.body() != null) {
                                JSONObject object = new JSONObject(jsonElementResponse.body().toString());
                                String code = object.getString("code");
                                if (code.equalsIgnoreCase("00000")) {
                                    dialogLoading.dismiss();
                                    updateQrSuccess();
                                    //doPrinting(getBitmapFromView(slipLinearLayout));
                                } else {
                                    dialogLoading.dismiss();
                                    String dec = object.getString("desc");
                                    Utility.customDialogAlert(MenuQrActivity.this, dec, new Utility.OnClickCloseImage() {
                                        @Override
                                        public void onClickImage(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            } else {
                                dialogLoading.dismiss();
                                Utility.customDialogAlert(MenuQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialogLoading.dismiss();
                        Utility.customDialogAlert(MenuQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        dialogLoading.dismiss();
                    }
                });
    }

    private void updateQrSuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();       // Paul_20181026 Some time DB Read error solved
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // Paul_20190131 Time Date update
                            System.out.printf("utility:: %s setDataSuccess\n",TAG);
                            if (realm == null) {
                                realm = Realm.getDefaultInstance();
                            }
                            QrCode qrCodeSave = realm.where(QrCode.class).equalTo("id", qrId).findFirst();
                            if (qrCodeSave != null) {
                                qrCodeSave.setStatusSuccess("1");
                                qrCodeSave.setDate(dateFormat);    // Paul_20190131 Time Date update
                                qrCodeSave.setTime(timeFormat);    // Paul_20190131 Time Date update
                                realm.copyToRealmOrUpdate(qrCodeSave);
                            }
                        }
                    });
                } finally {
                    realm.close();
                    realm = null;   // Paul_20181026 Some time DB Read error solved
                }

            }
        }).start();


    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public void doPrinting(final Bitmap slip) {
        bitmapOld = slip;
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    //This interface is used for set the gray level of the printing
                    //MIN is 0,Max is 4
                    //The level bigger, the speed of print is smaller
                    printDev.setPrinterGray(2);
                    printDev.printBmpFast(slip, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() {
//                            Intent intent = new Intent(MenuQrActivity.this, MenuServiceActivity.class);
                            Intent intent = new Intent(MenuQrActivity.this, MenuServiceListActivity.class);  //20180720 SINN  print finish CallBack mainmenu
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }

                        @Override
                        public void onPrintError(int i) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    msgLabel.setText("เกิดข้อผิดพลาด เช่นแบตเตอรี่อ่อน");
                                    dialogOutOfPaper.show();
                                }
                            });
                        }

                        @Override
                        public void onPrintOutOfPaper() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogOutOfPaper.show();
                                }
                            });
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogOutOfPaper.getLayoutInflater().inflate(R.layout.dialog_custom_printer, null);//K.GAME 180821
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogOutOfPaper.setContentView(view);//K.GAME 180821
        dialogOutOfPaper.setCancelable(false);//K.GAME 180821

//        dialogOutOfPaper = new Dialog(this);
//        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogOutOfPaper.setContentView(R.layout.dialog_custom_printer);
//        dialogOutOfPaper.setCancelable(false);
//        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    private void customDialogLoading() {
        dialogLoading = new Dialog(this);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(R.layout.dialog_custom_load_process);
        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogLoading.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogLoading);
        //END K.GAME 180831 chang waitting UI

//        dialogLoading = new Dialog(this);
//        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        dialogLoading.setContentView(R.layout.dialog_custom_alert_loading);//K.GAME 180916 change UI XML
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();

    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }
}
