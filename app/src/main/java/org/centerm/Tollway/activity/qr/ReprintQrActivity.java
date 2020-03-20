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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import org.centerm.Tollway.activity.ReprintActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.manager.HttpManager;
import org.centerm.Tollway.model.Check;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;

import static org.centerm.Tollway.activity.ReprintActivity.KEY_INTERFACE_INV;
import static org.centerm.Tollway.activity.ReprintActivity.KEY_INTERFACE_REPRINT_TYPE;

public class ReprintQrActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "ReprintQrActivity";

    private EditText traceBox = null;
    private Button checkBtn = null;
    private Realm realm = null;
    private Dialog dialogAlertPrint;

    private ImageView bankImage = null;
    private ImageView bank1Image = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;

    private TextView duplicateLabel = null; ////20180706 Add QR print.

    private TextView qrTidLabel = null;
    private TextView billerLabel = null;
    private TextView traceLabel = null;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
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
    private int id;
    private View slipView;
    private Check check;

    private String statusSuccess = "";
    private DecimalFormat decimalFormatShow;
    private int qrCodeId = 0;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private TextView midLabel;
    private TextView batchLabel;
    private TextView apprCodeLabel;
    private TextView inquiryLabel;
    private Dialog dialogLoading;

    /**
     * Interface
     */
    private String invoiceId=null;
    private String inReprintType;



    private String szDateOrg=null;
    private String szTimeOrg=null;

    private TextView name_sw_version;  // Paul_20190125 software version print

    private Dialog dialogWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_check_qr_no);

        initWidget();
        initData();
        initBtnExit();

        ////--------------------------------------------------------------------------
// Paul_20190214
//        if(invoiceId != null)
//        {
//            if (inReprintType.equals("1"))
//                checkBtn.performClick();
//        }
        traceBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (inReprintType.equals("1"))
                    checkBtn.performClick();
            }
        });


////-------------------------------------------------------------------------
    }


    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            invoiceId = bundle.getString( ReprintActivity.KEY_INTERFACE_INV);
            inReprintType=bundle.getString( ReprintActivity.KEY_INTERFACE_REPRINT_TYPE);  // type 1 : auto  | "" : normal
            dialogLoading.show();
        }
        Log.d(TAG, "invoiceId: " + invoiceId);
        Log.d(TAG, "inReprintType: " + inReprintType);
    }


    @Override
    public void initWidget() {
//        super.initWidget();
        decimalFormatShow = new DecimalFormat("#,##0.00");
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        traceBox = findViewById( R.id.traceBox);
        checkBtn = findViewById( R.id.checkBtn);
        setViewPrintSlip();
        customDialogOutOfPaper();
        customDialogLoading();
        checkBtn.setOnClickListener(this);

    }

    private void setDefaultTrace() {
        RealmResults<QrCode> allTransactions = realm.where(QrCode.class).findAll();

//If you have an incrementing id column, do this
//        if (allTransactions.size() > 0) {
//            qrCode = allTransactions.last();
//            if (qrCode != null) {
//                traceBox.setText(qrCode.getTrace());
//            }
//    }

              traceBox.setText(invoiceId);
              if(inReprintType.equals("1"))
                  traceBox.setEnabled(false);



    }

    private void setMeasureSlip() {
        slipView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        slipView.layout(0, 0, slipView.getMeasuredWidth(), slipView.getMeasuredHeight());
    }

    private void selectQr(final String traceId) {

        System.out.printf("utility:: traceId = %s\n",traceId);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                qrCode = realm.where(QrCode.class).equalTo("trace", traceId).findFirst();
                if (qrCode != null) {

                    if (!Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText( Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText( Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText( Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_MERCHANT_3));

                    qrTidLabel.setText( Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_QR_TERMINAL_ID));
//                    midLabel.setText( Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_QR_BILLER_ID));
                    midLabel.setText( Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_QR_MERCHANT_ID));  //20180814 SINN  use QR Merchant ID instead biller id.


//                    batchLabel.setText(CardPrefix.calLen(Preference.getInstance(CheckQrActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS),6));
                    int batch = Integer.parseInt( Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_QR_BATCH_NUMBER));
                    batchLabel.setText( CardPrefix.calLen(String.valueOf(batch), 6));
                    apprCodeLabel.setText("000000");
                    inquiryLabel.setText(qrCode.getQrTid());
                    billerLabel.setText(qrCode.getBillerId());
                    traceLabel.setText(qrCode.getTrace());
                    dateLabel.setText(qrCode.getDate());

                    szDateOrg =qrCode.getDate();
                    szTimeOrg =qrCode.getTime();

                    String timeHH = qrCode.getTime().substring(0, 2);
                    String timeMM = qrCode.getTime().substring(2, 4);
                    String timeSS = qrCode.getTime().substring(4, 6);

                    timeLabel.setText(getString( R.string.time_qr, timeHH + ":" + timeMM + ":" + timeSS));
//                    comCodeLabel.setText(qrCode.getComCode());
                    amtThbLabel.setText(getString( R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(qrCode.getAmount().replaceAll(",","")))));
                    /*if (qrCode.getRef1() != null) {
                        ref1RelativeLayout.setVisibility(View.VISIBLE);
                        ref1Label.setText(qrCode.getRef1());
                    }
                    if (qrCode.getRef2() != null) {
                        ref2RelativeLayout.setVisibility(View.VISIBLE);
                        ref2Label.setText(qrCode.getRef2());
                    }*/
//                qrImage.setImageBitmap(Utility.createQRImage(qrCode.getTextQrGenerateAll(), 300, 300));
                    setMeasureSlip();

                    check = new Check();
                    check.setBillerId(qrCode.getBillerId());
                    check.setTerminalId(qrCode.getQrTid());
                    check.setRef1(qrCode.getRef1());
                    check.setRef2(qrCode.getRef2());
                    statusSuccess = qrCode.getStatusSuccess();
                    qrCodeId = qrCode.getId();

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

//                    if(!qrCode.getRef3().isEmpty()){
//                        ref3Label.setText(qrCode.getRef3());
//                        ref3RelativeLayout.setVisibility(View.VISIBLE);
//                    }else {
//                        ref3RelativeLayout.setVisibility(View.GONE);
//                    }

                } else {
                    statusSuccess = "";
                    check = null;
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                if (!statusSuccess.isEmpty()) {
                    if (statusSuccess.equalsIgnoreCase("0")) {
                        requestCheckSlip();
                    } else {


//
//                        Utility.customDialogAlertSuccess(ReprintQrActivity.this, "รายการนี้ชำระเงินแล้ว", new Utility.OnClickCloseImage() {
//                            @Override
//                            public void onClickImage(Dialog dialog) {
//                                statusSuccess = "";
//                                dialog.dismiss();
//                            }
//                        });


                        //SINN printslip 20180705
                        setViewPrintSlip_Dup();
//                        dialogLoading.dismiss();
                        //doPrinting(getBitmapFromView(slipLinearLayout));



                    }
                } else {
                    String msgShow = "ไม่มีหมายเลขนี้ในรายการ";
                    if (Preference.getInstance(ReprintQrActivity.this).getValueString( Preference.KEY_QR_LAST_TRACE).equals("0"))
                        msgShow="ไม่มีข้อมูล";
//                    Utility.customDialogAlert(ReprintQrActivity.this, "ไม่มีหมายเลขนี้ในรายการ", new Utility.OnClickCloseImage() {
                    Utility.customDialogAlert(ReprintQrActivity.this, msgShow, new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            statusSuccess = "";
                            dialog.dismiss();
                        }
                    });

                }
            }
        });
    }

    public void setViewPrintSlip_Dup() {
        duplicateLabel.setVisibility(View.VISIBLE);
        setMeasureSlip();

    }
    //END SINN RS232 20180705


    public void setViewPrintSlip() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        slipView = inflater.inflate( R.layout.view_slip_qr, null);

        bankImage = slipView.findViewById( R.id.bankImage);
        bank1Image = slipView.findViewById( R.id.bank1Image);

        merchantName1Label = slipView.findViewById( R.id.merchantName1Label);
        merchantName2Label = slipView.findViewById( R.id.merchantName2Label);
        merchantName3Label = slipView.findViewById( R.id.merchantName3Label);

        duplicateLabel = slipView.findViewById( R.id.duplicateLabel);  ////20180706 Add QR print.
        duplicateLabel.setVisibility(View.GONE);///20180706 Add QR print.

        midLabel = slipView.findViewById( R.id.midLabel);
        batchLabel = slipView.findViewById( R.id.batchLabel);
        apprCodeLabel = slipView.findViewById( R.id.apprCodeLabel);
        inquiryLabel = slipView.findViewById( R.id.inquiryLabel);
        qrTidLabel = slipView.findViewById( R.id.qrTidLabel);
        billerLabel = slipView.findViewById( R.id.billerLabel);
        traceLabel = slipView.findViewById( R.id.traceLabel);
        dateLabel = slipView.findViewById( R.id.dateLabel);
        timeLabel = slipView.findViewById( R.id.timeLabel);
        comCodeLabel = slipView.findViewById( R.id.comCodeLabel);
        amtThbLabel = slipView.findViewById( R.id.amtThbLabel);
//        qrImage = slipView.findViewById(R.id.qrImage);
        ref1RelativeLayout = slipView.findViewById( R.id.ref1RelativeLayout);
        ref1Label = slipView.findViewById( R.id.ref1Label);
        ref2RelativeLayout = slipView.findViewById( R.id.ref2RelativeLayout);
        ref2Label = slipView.findViewById( R.id.ref2Label);
//        ref3RelativeLayout = slipView.findViewById( R.id.ref3RelativeLayout);
//        ref3Label = slipView.findViewById( R.id.ref3Label);
        slipLinearLayout = slipView.findViewById( R.id.slipLinearLayout);
        printBtn = slipView.findViewById( R.id.printBtn);
        name_sw_version = slipView.findViewById(R.id.name_sw_version);   // Paul_20190125 software version print
        name_sw_version.setText( BuildConfig.VERSION_NAME);
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

    public void doPrinting(Bitmap slip) {
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
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() {
//                            dialogLoading.dismiss();
                           // Intent intent = new Intent(CheckQrActivity.this, MenuServiceActivity.class);  //SINN change main menu
                            Intent intent = new Intent(ReprintQrActivity.this, MenuServiceListActivity.class);
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

    private void requestCheckSlip() {
//        dialogLoading.show();
//        HttpManager.getInstance().getService().checkQr(check)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Response<JsonElement>>() {


                HttpManager.getInstance().getService().checkQr(check)/*.subscribeOn(Schedulers.io())*/.observeOn( AndroidSchedulers.mainThread()).subscribe(new Observer<Response<JsonElement>>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<JsonElement> jsonElementResponse) {
                        try {
                            if (jsonElementResponse.body() != null) {
                                Log.d("SINN", "jsonElementResponse.body()" );
                                JSONObject object = new JSONObject(jsonElementResponse.body().toString());
                                String code = object.getString("code");
                                if (code.equalsIgnoreCase("00000")) {
                                    dialogLoading.dismiss();

                                    setDataSuccess();
                                    //doPrinting(getBitmapFromView(slipLinearLayout));

                                } else {
                                    dialogLoading.dismiss();
                                    String dec = object.getString("desc");
                                    Utility.customDialogAlert(ReprintQrActivity.this, dec, new Utility.OnClickCloseImage() {
                                        @Override
                                        public void onClickImage(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    });

                                }
                            } else {
                                Log.d("SINN", "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้" );
                                dialogLoading.dismiss();
                            //    Utility.customDialogAlert(ReprintQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                                     Utility.customDialogAlert(ReprintQrActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("SINN", "JSONException e: " );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialogLoading.dismiss();
                        Log.d(TAG, "onError: " + e.getMessage());
//                        Utility.customDialogAlert(ReprintQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                        Utility.customDialogAlert(ReprintQrActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        Log.d("SINN", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dialogLoading.dismiss();
                    }
                });
    }

    private void setDataSuccess() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Paul_20190131 Time Date update
                System.out.printf("utility:: %s setDataSuccess\n",TAG);
                QrCode qrCode1 = realm.where(QrCode.class).equalTo("id", qrCodeId).findFirst();
                qrCode1.setStatusSuccess("1");
//                qrCode1.setDate(dateFormat);    // Paul_20190131 Time Date update
//                qrCode1.setTime(timeFormat);    // Paul_20190131 Time Date update
                realm.copyToRealmOrUpdate(qrCode1);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBtn:
//                if (!invoiceId.isEmpty()) {
//                    if (invoiceId.length() < 6) {
//                        StringBuilder num = new StringBuilder(invoiceId);
//                        for (int i = invoiceId.length(); i < 6; i++) {
//                            num.insert(0, "0");
//                        }
//                        invoiceId = num.toString();
////                        traceBox.setText(num.toString());
//                        selectQr(invoiceId);
//                    } else {
//                        selectQr(invoiceId);
//                    }
//                } else {
//                    Utility.customDialogAlert(ReprintQrActivity.this, "กรุณากรอกหมายเลข", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                        }
//                    });
//                }
                if (!traceBox.getText().toString().isEmpty()) {
                    if (traceBox.getText().length() < 6) {
                        StringBuilder num = new StringBuilder(traceBox.getText().toString());
                        for (int i = traceBox.getText().toString().length(); i < 6; i++) {
                            num.insert(0, "0");
                        }
                        traceBox.setText(num.toString());
                        selectQr(traceBox.getText().toString());
                    } else {
                        selectQr(traceBox.getText().toString());
                    }
                } else {
                    Utility.customDialogAlert(ReprintQrActivity.this, "กรุณากรอกหมายเลข", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                }
                break;
        }
    }

    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180919
        View view = dialogOutOfPaper.getLayoutInflater().inflate(R.layout.dialog_custom_printer, null);//K.GAME 180919
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180919
        dialogOutOfPaper.setContentView(view);//K.GAME 180919
        dialogOutOfPaper.setCancelable(false);//K.GAME 180919

//        dialogOutOfPaper = new Dialog(this);
//        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogOutOfPaper.setContentView( R.layout.dialog_custom_printer);
//        dialogOutOfPaper.setCancelable(false);
//        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById( R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById( R.id.msgLabel);
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
        TextView msgLabel = dialogLoading.findViewById(R.id.msgLabel);
        msgLabel.setText("กรุณารอสักครู่...");
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogLoading);
        //END K.GAME 180831 chang waitting UI

//        dialogLoading = new Dialog(this);
//        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogLoading.setContentView( R.layout.dialog_custom_alert_loading);
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(realm == null)   // Paul_20181026
            realm = Realm.getDefaultInstance();

        setDefaultTrace();

    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }
}
