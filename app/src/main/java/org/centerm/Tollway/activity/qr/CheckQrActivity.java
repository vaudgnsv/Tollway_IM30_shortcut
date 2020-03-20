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
import android.view.KeyEvent;
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
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.manager.HttpManager;
import org.centerm.Tollway.model.Check;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_REF1;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;

public class CheckQrActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "CheckQrActivity";

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
    private RelativeLayout ref3RelativeLayout = null;
    private TextView ref3Label = null;
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

    private TextView name_sw_version;  // Paul_20190125 software version print

    /**
     * Interface
     */
    private String typeInterface;
    private String invoiceId;
    private PosInterfaceActivity posInterfaceActivity;
    private String szREF1;  //20180719 SINN reprint qr call only requestCheckSlip_RS232
    private String szDateOrg=null;
    private String szTimeOrg=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_check_qr);

        initData();
        initWidget();
        initBtnExit();
////--------------------------------------------------------------------------

        traceBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (typeInterface != null) {
                    checkBtn.performClick();
                }
            }
        });


////-------------------------------------------------------------------------
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
            invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
            szREF1 = bundle.getString(KEY_INTERFACE_REF1);
        }

        posInterfaceActivity = MainApplication.getPosInterfaceActivity();
    }


    @Override
    public void initWidget() {
//        super.initWidget();
        decimalFormatShow = new DecimalFormat("##,###,##0.00");
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        traceBox = findViewById( R.id.traceBox);
        checkBtn = findViewById( R.id.checkBtn);
        setViewPrintSlip();
        customDialogOutOfPaper();
        customDialogLoading();
        checkBtn.setOnClickListener(this);
       //SINN RS232
        if (typeInterface != null) {
           // traceBox.setText("1");
            traceBox.setText(invoiceId);
            traceBox.setEnabled(false);
            checkBtn.setEnabled(false);
        }
     //END SINN RS232

    }

    private void setDefaultTrace() {
        RealmResults<QrCode> allTransactions = realm.where(QrCode.class).findAll();

//If you have an incrementing id column, do this
        if (allTransactions.size() > 0) {
            qrCode = allTransactions.last();
            if (qrCode != null) {
                traceBox.setText(qrCode.getTrace());
            }

            //sinn rs232 20180705
            if (typeInterface != null) {
                // traceBox.setText("1");
                traceBox.setText(invoiceId);
            }
            //sinn rs232 20180705


        }
    }

    private void setMeasureSlip() {
        slipView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        slipView.layout(0, 0, slipView.getMeasuredWidth(), slipView.getMeasuredHeight());
    }

    private void selectQr(final String traceId) {

           if(dialogLoading!=null)
               dialogLoading.dismiss();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                qrCode = realm.where(QrCode.class).equalTo( "hostTypeCard","QR" ).equalTo("trace", traceId).findFirst();    // Paul_20181020
                if (qrCode != null) {

                    if (!Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText( Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText( Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText( Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_MERCHANT_3));

                    qrTidLabel.setText( Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_QR_TERMINAL_ID));
//                    midLabel.setText( Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_QR_BILLER_ID));
                    midLabel.setText( Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_QR_MERCHANT_ID));  //20180814 SINN  use QR Merchant ID instead biller id.


//                    batchLabel.setText(CardPrefix.calLen(Preference.getInstance(CheckQrActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS),6));
                    int batch = Integer.parseInt( Preference.getInstance(CheckQrActivity.this).getValueString( Preference.KEY_QR_BATCH_NUMBER));
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

                        if(typeInterface!=null&&szREF1!=null)    ////20180719 SINN reprint qr call only requestCheckSlip_RS232
                            requestCheckSlip_RS232();
                        else
                            requestCheckSlip();

                    } else {

//                        if (typeInterface != null)  //sinn rs232 20180705
//                        TellToPosMatching();
                                    if (typeInterface != null) {
//                                        setViewPrintSlip_Dup();
                                        //dialogLoading.dismiss();  //20180724  SINN show dialog for POS repornt

                                        if(szREF1==null)
                                        Utility.customDialogAlertAuto( CheckQrActivity.this, "รายการนี้ชำระเงินแล้ว" );
                                        TellToPosMatching();
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
                                                setViewPrintSlip_Dup();
                                                //doPrinting(getBitmapFromView(slipLinearLayout));
                                                if(szREF1==null)
                                                Utility.customDialogAlertAutoClear();
                                                Intent intent = new Intent( CheckQrActivity.this, MenuServiceListActivity.class );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                                startActivity( intent );
                                                finish();
                                                overridePendingTransition( 0, 0 );
                                            }
                                        });
                                    }
                                else {
                                    Utility.customDialogAlertSuccess(CheckQrActivity.this, "รายการนี้ชำระเงินแล้ว", new Utility.OnClickCloseImage() {
                                        @Override
                                        public void onClickImage(Dialog dialog) {
                                            statusSuccess = "";
                                            dialog.dismiss();
                                        }
                                    });

                                    //SINN printslip 20180705
                                    setViewPrintSlip_Dup();
                                    dialogLoading.dismiss();
                                    //doPrinting(getBitmapFromView(slipLinearLayout));
                                }
                    }
                } else {
                    if (typeInterface != null) {
//                        TellToPosNoMatching("12");    //sinn rs232
//                        new CountDownTimer(1500, 1000) {
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//                                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                finish();
//                                }
//
//                        }.start();
                        Utility.customDialogAlertAuto( CheckQrActivity.this, "ไม่มีหมายเลขนี้ในรายการ" );
                        TellToPosNoMatching("12");    //sinn rs232
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                System.out.printf("utility:: CheckQrActivity connectTimeOut 0000001 \n");
                                Intent intent = new Intent( CheckQrActivity.this, MenuServiceListActivity.class );
                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity( intent );
                                finish();
                                overridePendingTransition( 0, 0 );
                            }
                        });
                    }
                    else
                    Utility.customDialogAlert(CheckQrActivity.this, "ไม่มีหมายเลขนี้ในรายการ", new Utility.OnClickCloseImage() {
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

    //SINN RS232 IQ found //sinn 20180705
    public void TellToPosMatching()
    {
        String szMSG = new String();

//        posInterfaceActivity.PosInterfaceWriteField("01","000000000");   // Approval Code
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("00"));   // Response Message TX NOT FOUND
//
//        szMSG = traceLabel.getText().toString();
//        posInterfaceActivity.PosInterfaceWriteField("65",szMSG);   // Invoice Number
//        posInterfaceActivity.PosInterfaceWriteField("D3","000000000000");  //Reference No
//        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_QR_TERMINAL_ID));
//        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
//
//        szMSG=szDateOrg;
//        szMSG=szTimeOrg;
//
//        posInterfaceActivity.PosInterfaceWriteField("03",szDateOrg.substring(8, 10)+szDateOrg.substring(3, 5)+szDateOrg.substring(0, 2));  //yymmdd
//
//        posInterfaceActivity.PosInterfaceWriteField("04",szTimeOrg);  //hhmmss
//
//        posInterfaceActivity.PosInterfaceWriteField("F1","QR");
//
//        posInterfaceActivity.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,"00");

        posInterfaceActivity.PosInterfaceWriteField("01","000000000");   // Approval Code
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));   // Response Message TX NOT FOUND

        szMSG = traceLabel.getText().toString();
        posInterfaceActivity.PosInterfaceWriteField("65",szMSG);   // Invoice Number
        posInterfaceActivity.PosInterfaceWriteField("D3","000000000000");  //Reference No
        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_QR_TERMINAL_ID));
        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
//20180731 Off Tag30 QR
//        posInterfaceActivity.PosInterfaceWriteField("30","0000000000000000");

        posInterfaceActivity.PosInterfaceWriteField("03",szDateOrg.substring(8, 10)+szDateOrg.substring(3, 5)+szDateOrg.substring(0, 2));  //yymmdd

        posInterfaceActivity.PosInterfaceWriteField("04",szTimeOrg);  //hhmmss

        posInterfaceActivity.PosInterfaceWriteField("F1","QR   ");

        posInterfaceActivity.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,"00");

    }


    //SINN RS232 not found

    public void TellToPosNoMatching(String szErr)
    {
        /*
        posInterfaceActivity.PosInterfaceWriteField("01","000xxxxxx");   // Approval Code
        //posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("12"));   // Response Message TX NOT FOUND
        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(szErr));

        posInterfaceActivity.PosInterfaceWriteField("65","000000");   // Invoice Number
        posInterfaceActivity.PosInterfaceWriteField("D3","xxxxxxxxxxxx");
        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_QR_TERMINAL_ID));
        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
        Date date = new Date();
        String dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);
        posInterfaceActivity.PosInterfaceWriteField("03",dateFormatDef);  //yymmdd

        String timeFormat = new SimpleDateFormat("HHmmss").format(date);
        posInterfaceActivity.PosInterfaceWriteField("04",timeFormat);  //hhmmss

        posInterfaceActivity.PosInterfaceWriteField("F1","QR");

        //posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode,"12");
        posInterfaceActivity.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,szErr);*/

        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posInterfaceActivity.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,szErr);
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
        ref3RelativeLayout = slipView.findViewById( R.id.ref3RelativeLayout);
        ref3Label = slipView.findViewById( R.id.ref3Label);
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
                           // Intent intent = new Intent(CheckQrActivity.this, MenuServiceActivity.class);  //SINN change main menu
                            Intent intent = new Intent(CheckQrActivity.this, MenuServiceListActivity.class);
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


    private void requestCheckSlip_RS232() {

        Log.d(TAG, "Call requestCheckSlip_RS232");
        HttpManager.getInstance().getService().checkQr(check)/* .subscribeOn(Schedulers.io())*/.observeOn( AndroidSchedulers.mainThread()).subscribe(new Observer<Response<JsonElement>>() {
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

                                    setDataSuccess();
                                    // Paul_20190131 Start
                                    dateLabel.setText(qrCode.getDate());
                                    String timeHH = qrCode.getTime().substring(0, 2);
                                    String timeMM = qrCode.getTime().substring(2, 4);
                                    String timeSS = qrCode.getTime().substring(4, 6);
                                    timeLabel.setText(getString(R.string.time_qr, timeHH + ":" + timeMM + ":" + timeSS));
                                    szDateOrg =qrCode.getDate();
                                    szTimeOrg =qrCode.getTime();
                                    // Paul_20190131 End

                                    if (typeInterface != null) //SINN 20180717 QR ALL ASK 3 time
                                    {
                                        TellToPosMatching();
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
                                                //doPrinting(getBitmapFromView(slipLinearLayout));
                                                Intent intent = new Intent(CheckQrActivity.this, MenuServiceListActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(0, 0);
                                            }
                                        });
                                    }

                                    else {
                                        //doPrinting(getBitmapFromView(slipLinearLayout));
                                    }

                                } else {

//                                    String dec = object.getString("desc");
//                                    Utility.customDialogAlert(CheckQrActivity.this, dec, new Utility.OnClickCloseImage() {
//                                        @Override
//                                        public void onClickImage(Dialog dialog) {
//                                            dialog.dismiss();
//                                        }
//                                    });
                                    Utility.customDialogAlertAuto( CheckQrActivity.this, "ไม่มีข้อมูล" );
                                    if (typeInterface != null)
                                    {
                                        TellToPosNoMatching("12");      // Paul_20180731 12 21
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
// Paul_20180718
                                                Utility.customDialogAlertAutoClear();
                                                System.out.printf("utility:: CheckQrActivity 0000001 \n");
                                                Intent intent = new Intent( CheckQrActivity.this, MenuServiceListActivity.class );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                                startActivity( intent );
                                                finish();
                                                overridePendingTransition( 0, 0 );
                                            }
                                        });
                                    }

                                }
                            } else {
                                Log.d("SINN", "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้" );

                                Utility.customDialogAlertAuto( CheckQrActivity.this, "ไม่มีข้อมูล" );
                                if (typeInterface != null)
                                {
//                                    TellToPosNoMatching("21");
                                    TellToPosNoMatching("12");
                                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                        @Override
                                        public void success() {
// Paul_20180718
                                            Utility.customDialogAlertAutoClear();
                                            System.out.printf("utility:: CheckQrActivity connectTimeOut 0000001 \n");
                                            Intent intent = new Intent( CheckQrActivity.this, MenuServiceListActivity.class );
                                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                            intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                            startActivity( intent );
                                            finish();
                                            overridePendingTransition( 0, 0 );
                                        }
                                    });
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("SINN", "JSONException e: " );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d(TAG, "onError: " + e.getMessage());
                        Utility.customDialogAlertAuto( CheckQrActivity.this, "ไม่มีข้อมูล" );
                        if (typeInterface != null)
                        {
                            TellToPosNoMatching("21");      // Paul_20180731
                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                @Override
                                public void success() {
// Paul_20180718
                                    Utility.customDialogAlertAutoClear();
                                    System.out.printf("utility:: CheckQrActivity onError 0000001 \n");
                                    Intent intent = new Intent( CheckQrActivity.this, MenuServiceListActivity.class );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                    startActivity( intent );
                                    finish();
                                    overridePendingTransition( 0, 0 );
                                }
                            });
                        }
                        Log.d("SINN", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }



    private void requestCheckSlip() {
        dialogLoading.show();
        HttpManager.getInstance().getService().checkQr(check)
                .subscribeOn(Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<JsonElement>>() {
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
                                    // Paul_20190131 Start
                                    dateLabel.setText(qrCode.getDate());
                                    String timeHH = qrCode.getTime().substring(0, 2);
                                    String timeMM = qrCode.getTime().substring(2, 4);
                                    String timeSS = qrCode.getTime().substring(4, 6);
                                    timeLabel.setText(getString(R.string.time_qr, timeHH + ":" + timeMM + ":" + timeSS));
                                    szDateOrg =qrCode.getDate();
                                    szTimeOrg =qrCode.getTime();
                                    // Paul_20190131 End

                                    if (typeInterface != null) //SINN 20180717 QR ALL ASK 3 time
                                    {
                                        TellToPosMatching();
                                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                @Override
                                                public void success() {
                                                    //doPrinting(getBitmapFromView(slipLinearLayout));
                                                    Intent intent = new Intent(CheckQrActivity.this, MenuServiceListActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                }
                                            });
                                        }

                                    else {
                                        //doPrinting(getBitmapFromView(slipLinearLayout));
                                    }

                                } else {
                                    dialogLoading.dismiss();
//                                    String dec = object.getString("desc");
//                                    Utility.customDialogAlert(CheckQrActivity.this, dec, new Utility.OnClickCloseImage() {
//                                        @Override
//                                        public void onClickImage(Dialog dialog) {
//                                            dialog.dismiss();
//                                        }
//                                    });
                                        if (typeInterface != null) {
                                                String dec = object.getString("desc");
                                                Utility.customDialogAlertAuto(CheckQrActivity.this, dec);
                                                TellToPosNoMatching("ND");
                                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                    @Override
                                                    public void success() {
                                                        Utility.customDialogAlertAutoClear();
                                                        Intent intent = new Intent(CheckQrActivity.this, MenuServiceListActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                        overridePendingTransition(0, 0);
                                                    }
                                                });

                                            }
                                        else
                                            {
                                            String dec = object.getString("desc");
                                            Utility.customDialogAlert(CheckQrActivity.this, dec, new Utility.OnClickCloseImage() {
                                                @Override
                                                public void onClickImage(Dialog dialog) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            }
                                }
                            } else {
                                Log.d("SINN", "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้" );
                                dialogLoading.dismiss();
//                                Utility.customDialogAlert(CheckQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });

                                    if (typeInterface != null) {
                                        Utility.customDialogAlertAuto(CheckQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้");
                                        System.out.printf("utility:: CheckQrActivity requestCheckSlip \n");
                                        TellToPosNoMatching("21");
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
                                                Utility.customDialogAlertAutoClear();
                                                Intent intent = new Intent(CheckQrActivity.this, MenuServiceListActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(0, 0);
                                            }
                                        });

                                    }else{
                                            Utility.customDialogAlert(CheckQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                                            @Override
                                            public void onClickImage(Dialog dialog) {
                                                dialog.dismiss();
                                            }
                                        });
                                        }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("SINN", "JSONException e: " );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialogLoading.dismiss();
//                        Log.d(TAG, "onError: " + e.getMessage());
//                        Utility.customDialogAlert(CheckQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
//                            @Override
//                            public void onClickImage(Dialog dialog) {
//                                dialog.dismiss();
//                            }
//                        });
                        Log.d("SINN", "onError: " + e.getMessage());
                        if(typeInterface!=null){
                            Utility.customDialogAlertAuto(CheckQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้");
                            System.out.printf("utility:: CheckQrActivity onError \n");
                            TellToPosNoMatching("21");
                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                @Override
                                public void success() {
                                    Utility.customDialogAlertAutoClear();
                                    Intent intent = new Intent(CheckQrActivity.this, MenuServiceListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            });

                        }else{
                        Log.d(TAG, "onError: " + e.getMessage());
                        Utility.customDialogAlert(CheckQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        }


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
                Date date = new Date();
                String dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
                String timeFormat = new SimpleDateFormat("HHmmss").format(date);
                QrCode qrCode1 = realm.where(QrCode.class).equalTo( "hostTypeCard","QR" ).equalTo("id", qrCodeId).findFirst(); // Paul_20181020
                qrCode1.setStatusSuccess("1");
                qrCode1.setDate(dateFormat);    // Paul_20190131 Time Date update
                qrCode1.setTime(timeFormat);    // Paul_20190131 Time Date update
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

              //  if(typeInterface!=null)
              //  dialogLoading.show();   //20180724  SINN show dialog for POS repornt
                try{
                    dialogLoading.show();
                }catch(Exception e){
                    dialogLoading.dismiss();
                }

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
                    Utility.customDialogAlert(CheckQrActivity.this, "กรุณากรอกหมายเลข", new Utility.OnClickCloseImage() {
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
        realm = Realm.getDefaultInstance();

        setDefaultTrace();

    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {    //SINN 20180716 cancel return to main menu
        return super.onKeyDown(keyCode, event);
    }


}
