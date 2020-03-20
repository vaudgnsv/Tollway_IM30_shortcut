package org.centerm.Tollway.activity.qr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import org.centerm.Tollway.utility.BluetoothService;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.Response;

//import org.centerm.Tollway.activity.CalculatePriceActivity;
//import org.centerm.Tollway.activity.MenuServiceActivity;
//SINN  rs232 20180705 add interface
//END  SINN  rs232 20180705 add interface


public class GenerateQrActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "GenerateQrActivity";
    private LinearLayout ref1LinearLayout;
    private LinearLayout ref2LinearLayout;
    private LinearLayout ref3LinearLayout;
    private ImageView qrImage = null;
    private TextView amountBox = null; //K.GAME 18097 change Edittext > TextView
    private EditText ref1Box = null;
    private EditText ref2Box = null;
    private EditText ref3Box = null;
    private Button generatorBtn = null;
    private Button qrSuccessBtn = null;
    private String tagAll = "";
    private Realm realm = null;

    private String aid = "";
    private String billerId = "";
    private String qrTid = "";
    private String nameCompany = "";
    private int nextId;
    /***
     * DialogSlip
     */
    private Dialog dialogAlertPrint;
    private ImageView bankImage = null;
    private ImageView bank1Image = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private TextView tidLabel = null;
    private TextView billerIdLabel = null;
    private TextView traceNoLabel = null;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView comCodeLabel = null;
    private TextView amtThbLabel = null;
    private ImageView qrSilpImage = null;
    private LinearLayout ref1RelativeLayout = null;
    private TextView ref1Label = null;
    private LinearLayout ref2RelativeLayout = null;
    private TextView ref2Label = null;
    private LinearLayout slipLinearLayout = null;
    private Button printBtn = null;
    private AidlPrinter printDev = null;
    private QrCode qrCode;
    private CardManager cardManager = null;
    private String dateFormat;
    private String dateFormatDef;
    private String timeFormat;
    private ImageView thaiQrImage = null;
    private Dialog dialogQuestionPrint;

    private LinearLayout ref3RelativeLayout;
    private TextView ref3Label;


    private LinearLayout linearLayoutPrint = null;

    private int currentIdObl;
    private Dialog dialogAlertLoading;

    /***
     *
     *  slipSuccess
     */

    private ImageView bankSlipImage = null;
    private ImageView bank1SlipImage = null;
    private TextView merchantName1SlipLabel = null;
    private TextView merchantName2SlipLabel = null;
    private TextView merchantName3SlipLabel = null;
    private TextView qrTidSlipLabel = null;
    private TextView midSlipLabel = null;
    private TextView batchSlipLabel = null;
    private TextView billerSlipLabel = null;
    private TextView traceSlipLabel = null;
    private TextView inquiryLabel = null;
    private TextView dateSlipLabel = null;
    private TextView timeSlipLabel = null;
    private TextView comCodeSlipLabel = null;
    private TextView amtThbSlipLabel = null;
    private ImageView qrSilpSlipImage = null;
    private RelativeLayout ref1SlipRelativeLayout = null;
    private TextView ref1SlipLabel = null;
    private RelativeLayout ref2SlipRelativeLayout = null;
    private TextView ref2SlipLabel = null;
    private LinearLayout slipSuccessLinearLayout = null;
    private View tagView;
    private View tagViewQr;

    private int statusPrintFinish = 0;
    private int status_game = 0;//K.GAME 180919 แก้บัค ทำ UI reprint confirm
    private DecimalFormat decimalFormatShow;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private TextView apprCodeLabel;
    private Dialog dialogLoading;

    private TextView merchantNameThaiLabel = null;
    private TextView merchantNameLabel = null;

    private String szDateOrg;   //sinn rs232 20180705
    private String szTimeOrg;  //sinn rs232 20180705


    /**
     * Interface
     */
    private String typeInterface;
    private String ref1;
    private String ref2;
    private String ref3;
    private String amt;
    private PosInterfaceActivity posInterfaceActivity;
    //K.GAME 180917 new UI calculate
    private FrameLayout sevenClickFrameLayout = null;
    private FrameLayout eightClickFrameLayout = null;
    private FrameLayout nineClickFrameLayout = null;
    private FrameLayout fourClickFrameLayout = null;
    private FrameLayout fiveClickFrameLayout = null;
    private FrameLayout sixClickFrameLayout = null;
    private FrameLayout oneClickFrameLayout = null;
    private FrameLayout twoClickFrameLayout = null;
    private FrameLayout threeClickFrameLayout = null;
    private FrameLayout zeroClickFrameLayout = null;
    private FrameLayout dotClickFrameLayout = null;

    private FrameLayout exitClickFrameLayout = null;
    private FrameLayout deleteClickFrameLayout = null;
    private FrameLayout sureClickFrameLayout = null;
    private TextView tv_insertIdCard_02;

    private LinearLayout linear_InsertRef;
    private LinearLayout linear_amountBox;
    private LinearLayout linear123;
    private LinearLayout linear456;
    private LinearLayout linear789;
    private LinearLayout linear0_del;
    private LinearLayout viewPagerMenu;
    private LinearLayout linear_generate_Qr;
    private LinearLayout linear_show_qr;
    private LinearLayout linear_qrSuccessBtn_new;
    private LinearLayout linear_SuccessPrint;//K.GAME New Linear for button print qr

    private TextView tv_label_qr01;
    private TextView tv_label_qr02;
    private TextView tv_line;

    private TextView tv_qr_amount;
    private TextView tv_qr_merchant_th;
    private TextView tv_qr_merchant_en;
//    private View view;

    private String numberPrice = "";
    DecimalFormat decFormat; //20180812 SINN BIG AMOUNT
    String amountInterface = null;
    private Dialog dialogWaiting;
    private Dialog dialogSuccess_GotoMain;
    private Dialog dialog_reprint_confirm_qr;
    private Button btn_gotoMain;
    //END K.GAME 180917 new UI calculate
    int CancelFlg = 0;            // Paul_20181020

    // for railway by hong
    private BluetoothService btService = null;
    private BluetoothSocket blSocket;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    DataOutputStream data_out;
    DataInputStream data_in;
    private String Qr_send_filename;

    private TextView tv_insertIdcard_01;
    private TextView name_sw_version;  // Paul_20190125 software version print

    private int GenerateQRorInquireFlg = 0;   // Paul_20190216

    private RelativeLayout tagView_ref1RelativeLayout = null;
    private TextView tagView_ref1Label = null;
    private RelativeLayout tagView_ref2RelativeLayout = null;
    private TextView tagView_ref2Label = null;
//    private RelativeLayout tagView_ref3RelativeLayout = null;
//    private TextView tagView_ref3Label = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);  // Paul_20190206 xml change

        decFormat = new DecimalFormat("##,###,##0.00"); //K.GAME 180917 // Paul_20181019

        GenerateQRorInquireFlg = 0;   // Paul_20190216
        initData();  //SINN  rs232 20180705 add interface
        initWidget();
        //initBtnExit();
        final View view = this.getCurrentFocus();
        if (view != null)
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //K.GAME 180917 disable
                    keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);//K.GAME 180917 disable
                }
            }, 50);


        //SINN  rs232 20180705 add interface
//        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {//K.GAME 180917 New UI
//
//            amountInterface = amt;
//
//            amountBox.setText(amountInterface);
//            ref1Box.setText(ref1);
//            ref2Box.setText(ref2);
//
//            pos_generateQr();
//            //   final View view = this.getCurrentFocus();
////            if (view != null)
////                view.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        // TODO Auto-generated method stub
////                       InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //K.GAME 180917 disable
////                       keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);//K.GAME 180917 disable
////                    }
////                }, 50);
//        }
        //END  SINN  rs232 20180705 add interface
//
//        hidingKeyboard(ref1Box);
//        hidingKeyboard(ref2Box);
//        view.clearFocus();
    }

    //SINN  rs232 20180705 add interface
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            realm = Realm.getDefaultInstance();
            typeInterface = bundle.getString(MenuServiceListActivity.KEY_TYPE_INTERFACE);
            amt = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_AMOUNT);
            ref1 = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_REF1);
            ref2 = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_REF2);
            ref3 = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_REF3);

            if (typeInterface.equalsIgnoreCase("InterfaceTollway")) {
                amt.replace(",", "");
            }else {
                amt = amt.replaceFirst("^0*", "");
                if (amt.isEmpty()) amt = "0";

                System.out.printf("utility:: %s , initData 0001 amt = %s \n", TAG, amt);
                amt = decFormat.format(Double.valueOf(amt) / 100);              // Paul_20181019 Amount Display method 0.03
                System.out.printf("utility:: %s , initData 0002 amt = %s \n", TAG, amt);
            }


/*
            if (amt.length() == 2)
                amt = "0." + amt;
            else if (amt.length() > 2) {
                String Ten = new String();
                Ten = amt.substring(0, amt.length() - 2);
                String satang = new String();
                satang = amt.substring(amt.length() - 2, amt.length());
                amt = Ten + "." + satang;
            }
*/
            ref1 = ref1.trim();
            System.out.printf("utility:: %s initData ref1 = %s \n", TAG, ref1);
            ref2 = ref2.trim();
            ref3 = ref3.trim();

            System.out.printf("utility:: %s BBBB amountInterface = %s\n", TAG, amountInterface);
            System.out.printf("utility:: %s BBBB ref1 = %s\n", TAG, ref1);
            System.out.printf("utility:: %s BBBB ref2 = %s\n", TAG, ref2);
            System.out.printf("utility:: %s BBBB ref3 = %s\n", TAG, ref3);

        }

        posInterfaceActivity = MainApplication.getPosInterfaceActivity();

        if (Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_ID).equalsIgnoreCase("1")) {
            if (btService == null) {
                btService = new BluetoothService(this);
            }
        }
    }
//SINN  rs232 20180705 add interface


    @Override
    public void initWidget() {
//        super.initWidget();
        tv_qr_amount = findViewById(R.id.tv_qr_amount);
        tv_qr_merchant_th = findViewById(R.id.tv_qr_merchant_th);
        tv_qr_merchant_en = findViewById(R.id.tv_qr_merchant_en);
        //K.GAME 180917 New UI calculate
        oneClickFrameLayout = findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = findViewById(R.id.zeroClickFrameLayout);
        dotClickFrameLayout = findViewById(R.id.dotClickFrameLayout);

        deleteClickFrameLayout = findViewById(R.id.deleteClickFrameLayout);

        linear_InsertRef = findViewById(R.id.linear_InsertRef);
        linear_amountBox = findViewById(R.id.linear_amountBox);
        linear123 = findViewById(R.id.linear123);
        linear456 = findViewById(R.id.linear456);
        linear789 = findViewById(R.id.linear789);
        linear0_del = findViewById(R.id.linear0_del);
        viewPagerMenu = findViewById(R.id.viewPagerMenu);
        linear_generate_Qr = findViewById(R.id.linear_generate_Qr);
        linear_show_qr = findViewById(R.id.linear_show_qr);
        linear_qrSuccessBtn_new = findViewById(R.id.linear_qrSuccessBtn_new);
        linear_SuccessPrint = findViewById(R.id.linear_SuccessPrint);//K.GAME 181011 New linear

        tv_label_qr01 = findViewById(R.id.tv_label_qr01);
        tv_label_qr02 = findViewById(R.id.tv_label_qr02);
        tv_line = findViewById(R.id.tv_line);

        tv_insertIdcard_01 = findViewById(R.id.tv_insertIdCard_01);


        oneClickFrameLayout.setOnClickListener(this);
        twoClickFrameLayout.setOnClickListener(this);
        threeClickFrameLayout.setOnClickListener(this);
        fourClickFrameLayout.setOnClickListener(this);
        fiveClickFrameLayout.setOnClickListener(this);
        sixClickFrameLayout.setOnClickListener(this);
        sevenClickFrameLayout.setOnClickListener(this);
        eightClickFrameLayout.setOnClickListener(this);
        nineClickFrameLayout.setOnClickListener(this);
        zeroClickFrameLayout.setOnClickListener(this);

        deleteClickFrameLayout.setOnClickListener(this);
        linear_InsertRef.setOnClickListener(this);
        linear_generate_Qr.setOnClickListener(this);
        linear_qrSuccessBtn_new.setOnClickListener(this);
        //END K.GAME 180917 New UI calculate

        decimalFormatShow = new DecimalFormat("##,###,##0.00");
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        qrImage = findViewById(R.id.qrImage);
        ref1LinearLayout = findViewById(R.id.ref1LinearLayout);
        ref2LinearLayout = findViewById(R.id.ref2LinearLayout);
        ref3LinearLayout = findViewById(R.id.ref3LinearLayout);
        amountBox = findViewById(R.id.amountBox);
//        amountBox.setFilters(new InputFilter[]{new MoneyValueFilter()});  //
        ref1Box = findViewById(R.id.ref1Box);
        ref2Box = findViewById(R.id.ref2Box);
        ref3Box = findViewById(R.id.ref3Box);
        linearLayoutPrint = findViewById(R.id.linearLayoutPrint);
        thaiQrImage = findViewById(R.id.thaiQrImage);
//        merchantNameLabel = findViewById(R.id.merchantNameLabel);
//        merchantNameLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_MERCHANT_NAME));
        generatorBtn = findViewById(R.id.generatorBtn);
        qrSuccessBtn = findViewById(R.id.qrSuccessBtn);
        linearLayoutPrint.setOnClickListener(this);
        generatorBtn.setOnClickListener(this);
        qrSuccessBtn.setOnClickListener(this);
        /*if (Preference.getInstance(this).getValueBoolean(Preference.KEY_REF_2)) {
            ref2LinearLayout.setVisibility(View.VISIBLE);
        } else {
            ref2LinearLayout.setVisibility(View.GONE);
        }*/
        customDialogOutOfPaper();
        customDialogLoading();
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tagView = inflater.inflate(R.layout.view_slip_qr, null);
        bankSlipImage = tagView.findViewById(R.id.bankImage);
        bank1SlipImage = tagView.findViewById(R.id.bank1Image);
        merchantName1SlipLabel = tagView.findViewById(R.id.merchantName1Label);
        merchantName2SlipLabel = tagView.findViewById(R.id.merchantName2Label);
        merchantName3SlipLabel = tagView.findViewById(R.id.merchantName3Label);
        apprCodeLabel = tagView.findViewById(R.id.apprCodeLabel);
        midSlipLabel = tagView.findViewById(R.id.midLabel);
        batchSlipLabel = tagView.findViewById(R.id.batchLabel);
        qrTidSlipLabel = tagView.findViewById(R.id.qrTidLabel);
        billerSlipLabel = tagView.findViewById(R.id.billerLabel);
        traceSlipLabel = tagView.findViewById(R.id.traceLabel);
        inquiryLabel = tagView.findViewById(R.id.inquiryLabel);
        dateSlipLabel = tagView.findViewById(R.id.dateLabel);
        timeSlipLabel = tagView.findViewById(R.id.timeLabel);
        amtThbSlipLabel = tagView.findViewById(R.id.amtThbLabel);
        slipSuccessLinearLayout = tagView.findViewById(R.id.slipLinearLayout);
        name_sw_version = tagView.findViewById(R.id.name_sw_version);   // Paul_20190125 software version print

        tagView_ref1RelativeLayout = tagView.findViewById(R.id.ref1RelativeLayout);
        tagView_ref1Label = tagView.findViewById(R.id.ref1Label);
        tagView_ref2RelativeLayout = tagView.findViewById(R.id.ref2RelativeLayout);
        tagView_ref2Label = tagView.findViewById(R.id.ref2Label);
//        tagView_ref3RelativeLayout = tagView.findViewById(R.id.ref3RelativeLayout);
//        tagView_ref3Label = tagView.findViewById(R.id.ref3Label);

        setViewPrintQr();
        customDialogAlertLoading();
        dialogSuccess_GotoMain();
        dialog_reprint_confirm_qr();

//20181213  SINN  KTB CR request set default ref text.
        ref1Box.setHint(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QrRef1text_ID));
        ref2Box.setHint(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QrRef2text_ID));
        ref3Box.setHint(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QrRef3text_ID));


        String valueParameterEnable = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1000);   //para enable


////SINN 20181129 Railway project QR ref1
        if (Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_ID).equalsIgnoreCase("1")) {
            if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));  //ref1 hide   //ref1  view only
                ref1Box.setEnabled(false);
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));  //ref1 hide
                ref1Box.setEnabled(true);
//                ref1Box.requestFocus(); //K.GAME 180907 Disabel

                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {   //SINN  rs232 20180705 add interface
                    ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));
                    hidingKeyboard(ref1Box);
                }

            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
                ref1LinearLayout.setVisibility(View.GONE);
                ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));  //ref1 hide
                ref1Box.setEnabled(false);
            }

        } else if (!valueParameterEnable.isEmpty()) {
////END SINN 20181129 Railway project QR ref1
//        if (!valueParameterEnable.isEmpty()) {
            if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
                System.out.printf("utility:: %s POSLINK TEST 0000001 \n", TAG);
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));    //ref1  view only
                ref1Box.setEnabled(false);
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                System.out.printf("utility:: %s POSLINK TEST 0000002 \n", TAG);
                ref1LinearLayout.setVisibility(View.VISIBLE);
                System.out.printf("utility:: %s POSLINK TEST 0000003 \n", TAG);
                ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));   //ref1 editable
                ref1Box.setEnabled(true);
//                ref1Box.requestFocus(); //K.GAME 180907 Disabel
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {   //SINN  rs232 20180705 add interface
                    System.out.printf("utility:: %s POSLINK TEST AA0000002 \n", TAG);
                    ref1Box.setText(ref1);
                    hidingKeyboard(ref1Box);
                }

            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
                System.out.printf("utility:: %s POSLINK TEST 0000004 \n", TAG);
                ref1LinearLayout.setVisibility(View.GONE);
                ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));  //ref1 hide
                ref1Box.setEnabled(false);
            }

            if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
                System.out.printf("utility:: %s POSLINK TEST 0000005 \n", TAG);
                ref2LinearLayout.setVisibility(View.VISIBLE);
                ref2Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1003));   //ref2
                ref2Box.setEnabled(false);
            } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
                System.out.printf("utility:: %s POSLINK TEST 0000006 \n", TAG);
                ref2LinearLayout.setVisibility(View.VISIBLE);
                ref2Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1003));  //ref2
                ref2Box.setEnabled(true);
//                ref2Box.requestFocus();//K.GAME 180907 Disabel


                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {   //SINN  rs232 20180705 add interface
                    ref2Box.setText(ref2);
                    hidingKeyboard(ref2Box);
                }

            } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
                System.out.printf("utility:: %s POSLINK TEST 0000007 \n", TAG);
                ref2LinearLayout.setVisibility(View.GONE);
                ref2Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1003));  //ref2
                ref2Box.setEnabled(false);
            }


            if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
                System.out.printf("utility:: %s POSLINK TEST 0000001 \n", TAG);
                ref3LinearLayout.setVisibility(View.VISIBLE);
                ref3Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1004));    //ref3  view only
                ref3Box.setEnabled(false);
            } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
                System.out.printf("utility:: %s POSLINK TEST 0000002 \n", TAG);
                ref3LinearLayout.setVisibility(View.VISIBLE);
                System.out.printf("utility:: %s POSLINK TEST 0000003 \n", TAG);
                ref3Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1004));   //ref3 editable
                ref3Box.setEnabled(true);
//                ref1Box.requestFocus(); //K.GAME 180907 Disabel
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {   //SINN  rs232 20180705 add interface
                    System.out.printf("utility:: %s POSLINK TEST AA0000002 \n", TAG);
                    ref3Box.setText(ref3);
                    hidingKeyboard(ref3Box);
                }

            } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
                System.out.printf("utility:: %s POSLINK TEST 0000004 \n", TAG);
                ref3LinearLayout.setVisibility(View.GONE);
                ref3Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1004));  //ref3 hide
                ref3Box.setEnabled(false);
            }

        } else {
            Utility.customDialogAlert(GenerateQrActivity.this, "กรุณา First Settlement ก่อนทำรายการ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    finish();
                    cardManager.abortPBOCProcess();
                }
            });
        }

        //SINN RS232
        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
            amountBox.setText(amt);

//            hidingKeyboard(amountBox);//K.GAME 180917 change Edittext > TextView

            amountBox.setEnabled(false);

            // if(ref1Box.getText().length()>0)   ////SINN 20180722 check ref1box
            // {
            ref1Box.setEnabled(false);
            //     generatorBtn.setEnabled( false );   // SINN_20180718
            // }

            //if(ref1Box.getText().length()>0)   //SINN 20180722 check ref1box
            //{
            ref2Box.setEnabled(false);
            ref3Box.setEnabled(false);
            generatorBtn.setEnabled(false);   // SINN_20180718
            // }


        }
        //END SINN RS232
//        if (typeInterface == null)
//            amountBox.requestFocus();//K.GAME 180907 Disabel


//        amountBox.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
//        ref2LinearLayout.setVisibility(View.VISIBLE);

        decFormat = new DecimalFormat("##,###,##0.00"); //K.GAME 180917
        // Paul_20181106 Android Thread ?
        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {//K.GAME 180917 New UI

            amountInterface = amt;
            amountBox.setText(amountInterface);
            ref1Box.setText(ref1);
            ref2Box.setText(ref2);
            ref3Box.setText(ref3);
            System.out.printf("utility:: %s AAAA amountInterface = %s\n", TAG, amountInterface);
            System.out.printf("utility:: %s AAAA ref1 = %s\n", TAG, ref1);
            System.out.printf("utility:: %s AAAA ref2 = %s\n", TAG, ref2);
            System.out.printf("utility:: %s AAAA ref2 = %s\n", TAG, ref3);

            pos_generateQr();
            //   final View view = this.getCurrentFocus();
//            if (view != null)
//                view.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                       InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //K.GAME 180917 disable
//                       keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);//K.GAME 180917 disable
//                    }
//                }, 50);
        }


        if (typeInterface.equals("InterfaceTollway")){

            linear_InsertRef.setVisibility(View.GONE);
            linear_amountBox.setVisibility(View.GONE);
            linear123.setVisibility(View.GONE);
            linear456.setVisibility(View.GONE);
            linear789.setVisibility(View.GONE);
            linear0_del.setVisibility(View.GONE);
            tv_label_qr01.setVisibility(View.GONE);
            ////////////////////////////////////
            linear_show_qr.setVisibility(View.VISIBLE);
            linear_qrSuccessBtn_new.setVisibility(View.VISIBLE);
            linear_SuccessPrint.setVisibility(View.VISIBLE);//K.GAME 181011 for New Buttom print slip qr
            tv_insertIdcard_01.setText("สแกนเพื่อชำระเงิน");  // Paul_20190123 K.sinn support


            amountBox.setText(amt);
            ref1Box.setText(ref1);
            ref2Box.setText(ref2);
            ref3Box.setText(ref3);

            System.out.printf("utility:: %s KKKKKKKK0002 \n", TAG);
//                tv_insertIdcard_01.setText( "กรุณาสแกนเพื่อชําระเงิน" );  // Paul_20181206
//                tv_qr_amount.setText("AMT THB : * " + amountBox.getText().toString());
            tv_qr_amount.setText("*  " + amountBox.getText().toString() + "  BAHT");
            tv_qr_merchant_th.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME_THAI));
            tv_qr_merchant_en.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME));
            Log.d("1919ref1Box", ref1Box.getText().toString());

            generatorQr();
        }
    }

    private void dialog_reprint_confirm_qr() {
        dialog_reprint_confirm_qr = new Dialog(this);
        dialog_reprint_confirm_qr.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_reprint_confirm_qr.setContentView(R.layout.dialog_custom_reprint_confirm_qr);
        dialog_reprint_confirm_qr.setCancelable(false);
        dialog_reprint_confirm_qr.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_reprint_confirm_qr.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        dialog_reprint_confirm_qr.setCancelable(true);
        dialog_reprint_confirm_qr.setCanceledOnTouchOutside(true);

        Button okBtn = dialog_reprint_confirm_qr.findViewById(R.id.okBtn);
        Button CancelBtn = dialog_reprint_confirm_qr.findViewById(R.id.CancelBtn);
        Utility.animation_Waiting_new(dialog_reprint_confirm_qr);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_reprint_confirm_qr.dismiss();
                // K.GAME เก็บไว้ใช้ง
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new CountDownTimer(1500, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับร้านค้า");
                                dialogWaiting.show();

                            }

                            @Override
                            public void onFinish() {
                                dialogWaiting.dismiss();
                            }
                        }.start();
                    }
                });
                //END K.GAME เก็บไว้ใช้
                doPrinting(getBitmap(slipSuccessLinearLayout));

            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.printf("utility:: GenerateQrActivity CancelBtn.setOnClickListener \n");
                dialog_reprint_confirm_qr.dismiss();
                dialogSuccess_GotoMain.show();
            }
        });
//        dialog_reprint_confirm_qr.show();
    }

    private void setMeasure() {
        tagView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tagView.layout(0, 0, tagView.getMeasuredWidth(), tagView.getMeasuredHeight());
    }

    private void setMeasureQr() {
        tagViewQr.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tagViewQr.layout(0, 0, tagViewQr.getMeasuredWidth(), tagViewQr.getMeasuredHeight());
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        //K.GAME 180917 New Calculate UI //////////////////////////////////////////////////////////////
        Log.d(TAG, "Click number: " + numberPrice);
        String[] splitter = null;
        Log.d(TAG, "onClick: " + numberPrice.contains("."));
//        if (numberPrice.length() < 10) {//K.GAME 180919 แก้บัค ตัวเลขกดเกิน8 10ตัวเลยกดอะไรไม่ได้
        if (!numberPrice.contains(".")) {
            Log.d(TAG, "if Main : ");
            if (!numberPrice.isEmpty())
                if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                    numberPrice = "";
            clickCal(v);
        } else {
            Log.d(TAG, "onClick: ");
            Log.d(TAG, "else Main : ");
            splitter = numberPrice.split("\\.");
            if (splitter.length > 1) {
                Log.d(TAG, "if Sub : ");
                if (splitter[1].length() > 1) {
                    Log.d(TAG, "splitter[1].length() > 1: ");
                    if (v == exitClickFrameLayout) {

                    } else if (v == deleteClickFrameLayout) {
                        if (!numberPrice.equalsIgnoreCase("0.00")) {
                            Log.d(TAG, "onClick: numberPrice.equalsIgnoreCase(\"0.00\") ");
                            if (numberPrice.length() == 0) {
                                Log.d(TAG, "onClick: numberPrice.length() If == 0 ");
                                numberPrice = "0.00";
                            } else {
                                numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                                if (numberPrice.length() == 0) {
                                    Log.d(TAG, "onClick: numberPrice.length() Else == 0 ");
                                    numberPrice = "0.00";
                                }
                            }
                        } else {
                            if (!numberPrice.isEmpty()) {
                                numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                            }
                        }
                    } else if (v == sureClickFrameLayout) {

                    }
                } else {
                    if (!numberPrice.isEmpty())
                            /*if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                                numberPrice = "";*/
                        clickCal(v);
                }
            } else {

                Log.d(TAG, "splitter[1].length() > 1 Else: ");

                if (!numberPrice.isEmpty())
                        /*if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                            numberPrice = "";*/
                    Log.d(TAG, "else Sub : " + splitter.length);
                Log.d(TAG, "else Sub : " + splitter[splitter.length - 1]);
                clickCal(v);
            }
        }
//        }//K.GAME 180919 แก้บัค ตัวเลขกดเกิน8 10ตัวเลยกดอะไรไม่ได้

        if (!numberPrice.isEmpty()) {
            Log.d("1919_3", numberPrice);
//            amountBox.setText(numberPrice);
//            amountBox.setText(amountInterface);//K.GAME 180924 ทดลอง POS Simulator //180924
            amountBox.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
            Log.d("1919_3test", decFormat.format(Double.valueOf(numberPrice) / 100));
            Log.d("1919_3", amountBox.getText().toString());
        }
        //END K.GAME 180917 New Calculate UI /////////////////////////////////////////////////////////////

        switch (v.getId()) {
            case R.id.linear_InsertRef://K.GAME 180917 new ui

                System.out.printf("utility:: %s KKKKKKKK0001 \n", TAG);
                // Paul_20190205 x,xxx,xxx,xxx.xx
                if (!Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MAX_AMT).equals("0") && (Double.valueOf(amountBox.getText().toString().replaceAll(",", "")) > Double.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MAX_AMT)))) {
                    Utility.customDialogAlert(GenerateQrActivity.this, "จำนวนเงินเกินกำหนด", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else if (amountBox.getText().toString().trim().isEmpty()) {
                    Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else if ((amountBox.getText().toString().equals("0.00")) || (amountBox.getText().toString().equals("0"))) {
                    Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    linear_InsertRef.setVisibility(View.GONE);
                    linear_amountBox.setVisibility(View.GONE);
                    linear123.setVisibility(View.GONE);
                    linear456.setVisibility(View.GONE);
                    linear789.setVisibility(View.GONE);
                    linear0_del.setVisibility(View.GONE);
                    tv_label_qr01.setVisibility(View.GONE);
                    ////////////////////////////////////////
                    linear_generate_Qr.setVisibility(View.VISIBLE);
                    tv_label_qr02.setVisibility(View.VISIBLE);
                    viewPagerMenu.setVisibility(View.VISIBLE);
//                    ref1LinearLayout.setVisibility(View.VISIBLE);//K.GAME 181029 แก้บัค ต้องเชค key_tag_1000
                    String valueParameterEnable = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1000);   //para enable
//Railway start
                    ///SINN 20181129 Railway project QR ref1
                    if (Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_ID).equalsIgnoreCase("1")) {
                        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {    //ref1
                            ref1LinearLayout.setVisibility(View.VISIBLE);
                            ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));  //ref1 hide   //ref1  view only
                            ref1Box.setEnabled(false);
                        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                            ref1LinearLayout.setVisibility(View.VISIBLE);
                            ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));  //ref1 hide
                            ref1Box.setEnabled(true);

                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {   //SINN  rs232 20180705 add interface
                                ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));
                                hidingKeyboard(ref1Box);
                            }

                        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
                            ref1LinearLayout.setVisibility(View.GONE);
                            ref1Box.setText(CardPrefix.calLen(String.valueOf(Integer.valueOf(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID)) + 1), 6));  //ref1 hide
                            ref1Box.setEnabled(false);
                        }

                    } else if (!valueParameterEnable.isEmpty()) {
//Railway end
//                    if (!valueParameterEnable.isEmpty()) {
                        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
                            ref1LinearLayout.setVisibility(View.VISIBLE);
                            ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));    //ref1  view only
                            ref1Box.setEnabled(false);
                        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                            ref1LinearLayout.setVisibility(View.VISIBLE);
                            ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));   //ref1 editable
                            ref1Box.setEnabled(true);
                        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
                            ref1LinearLayout.setVisibility(View.GONE);
                            ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));  //ref1 hide
                            ref1Box.setEnabled(false);
                        }
                    }
//                    ref1Box.setEnabled(true);//K.GAME 181029 ต้องเชคจากพารามิเตอร์ key_tag_1000


                    //Sinn 20181115  jump when parameter disable 1111+2222
                    String valueParameterEnable1 = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1000);
//                    if ((valueParameterEnable1.substring(0, 4).equalsIgnoreCase("2222") || valueParameterEnable1.substring(0, 4).equalsIgnoreCase("1111"))) {
//                        linear_generate_Qr.performClick();
//                    } NAMTAN_20190712

                    if (!(valueParameterEnable1.substring(0, 4).contains("3")) && !(valueParameterEnable1.substring(0, 4).contains("4"))) {
                        linear_generate_Qr.performClick();
                    }

                }
//                customDialog_insert_ref();
                break;
            case R.id.linear_generate_Qr://K.GAME 180917 new ui
                System.out.printf("utility:: %s KKKKKKKK0002 \n", TAG);
//                tv_insertIdcard_01.setText( "กรุณาสแกนเพื่อชําระเงิน" );  // Paul_20181206
//                tv_qr_amount.setText("AMT THB : * " + amountBox.getText().toString());
                tv_qr_amount.setText("*  " + amountBox.getText().toString() + "  BAHT");
                tv_qr_merchant_th.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME_THAI));
                tv_qr_merchant_en.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME));
                Log.d("1919ref1Box", ref1Box.getText().toString());

                String valueParameterEnable1 = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1000);
                if ((valueParameterEnable1.substring(1, 2).contains("3")) && (valueParameterEnable1.substring(1, 2).contains("4"))) {
//                    ref1Box.setText("");
//                }
//
//                if ((ref1Box.getText().toString().isEmpty())) {
                    Log.d("1919", ref1Box.getText().toString());
                    // Paul_20181019 Error Send to POSLINK
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        System.out.printf("utility:: %s UUU0001 \n", TAG);
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref1");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else {
                        System.out.printf("utility:: %s UUU0002 \n", TAG);
                        Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref1", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }


                } /*else if (ref2LinearLayout.getVisibility() == View.VISIBLE && (ref2Box.getText().toString().isEmpty() || ref2Box.getText().toString().equals(" "))) // Paul_20190123 k.sinn modify Ref2
                {
                    // Paul_20181019 Error Send to POSLINK
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref2");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else {
                        Utility.customDialogAlert( GenerateQrActivity.this, "กรุณากรอก Ref2", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        } );
                    }
                } */ else {
                    Log.d("1919", ref1Box.getText().toString());
                    tv_line.setVisibility(View.GONE);
                    linear_generate_Qr.setVisibility(View.GONE);
                    tv_label_qr02.setVisibility(View.GONE);
                    viewPagerMenu.setVisibility(View.GONE);
                    ref1LinearLayout.setVisibility(View.GONE);
                    ////////////////////////////////////
                    linear_show_qr.setVisibility(View.VISIBLE);
                    linear_qrSuccessBtn_new.setVisibility(View.VISIBLE);
                    linear_SuccessPrint.setVisibility(View.VISIBLE);//K.GAME 181011 for New Buttom print slip qr
                    tv_insertIdcard_01.setText("สแกนเพื่อชำระเงิน");  // Paul_20190123 K.sinn support
                    generatorQr();
//                    customDialog_insert_ref();
                }
                break;
            case R.id.linear_qrSuccessBtn_new://K.GAME 180917 disable
                System.out.printf("utility:: %s KKKKKKKK0003 \n", TAG);
                if (!tagAll.isEmpty()) {
                    Check check = new Check();
                    check.setBillerId(billerId);
                    check.setRef1(ref1Box.getText().toString());
                    if (ref2Box.getText().toString().isEmpty()) {
                        check.setRef2("");
                    } else {
                        check.setRef2(ref2Box.getText().toString());
                    }
                    if (ref3Box.getText().toString().isEmpty()) {
                        check.setRef3("");
                    } else {
                        check.setRef3(ref3Box.getText().toString());
                    }

                    check.setTerminalId(qrTid);
                    Log.d(TAG, "billerId: " + billerId + " ref1Box : " + ref1Box.getText().toString()
                            + " ref2Box :" + ref2Box.getText().toString()
                            + " ref3Box :" + ref3Box.getText().toString() +
                            " qrTid : " + qrTid);
                    dialogLoading.show();

//                    Log.d(TAG,"utility:: " + check.toString());

                    HttpManager.getInstance().getService().checkQr(check)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Response<JsonElement>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    Log.d(TAG, "billerId: " + billerId + " ref1Box : " + ref1Box.getText().toString()
                                            + " ref2Box :" + ref2Box.getText().toString() +
                                            " qrTid : " + qrTid);
                                }

                                @Override
                                public void onNext(Response<JsonElement> jsonElementResponse) {
                                    try {
                                        if (jsonElementResponse.body() != null) {
                                            JSONObject object = new JSONObject(jsonElementResponse.body().toString());
                                            String code = object.getString("code");
                                            if (code.equalsIgnoreCase("00000")) {
                                                dialogLoading.dismiss();
                                                selectQrSlip();

                                                //SINN //20180706 Add QR print.
                                                String szMSG = new String();
//                                                szMSG = traceSlipLabel.getText().toString();
                                                szMSG = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
                                                Log.d(TAG, "KEY_QR_LAST_TRACE :" + szMSG);
                                                szMSG = String.valueOf(Integer.valueOf(szMSG) - 1);
                                                System.out.printf("utility:: %s R.id.linear_qrSuccessBtn_new traceSlipLabel.getText().toString() = %s \n", TAG, traceSlipLabel.getText().toString());
//                                                Preference.getInstance(GenerateQrActivity.this).setValueString(Preference.KEY_QR_LAST_TRACE, szMSG);
                                                Preference.getInstance(GenerateQrActivity.this).setValueString(Preference.KEY_QR_LAST_TRACE, CardPrefix.calLen(szMSG, 6));  // Paul_20181028 Sinn merge version UAT6_0016
                                            } else {
//                                                if (typeInterface != null)  //sinn rs232 20180705
//                                                    TellToPosError("12");
                                                //END sinn rs232 20180705
                                                dialogLoading.dismiss();
                                                String dec = object.getString("desc");
                                                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                                    Utility.customDialogAlertAuto(GenerateQrActivity.this, dec);
                                                    TellToPosError("ND");
                                                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                        @Override
                                                        public void success() {
                                                            Utility.customDialogAlertAutoClear();
                                                            Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                            overridePendingTransition(0, 0);
                                                        }
                                                    });
                                                } else
//                                                    Utility.customDialogAlert(GenerateQrActivity.this, dec, new Utility.OnClickCloseImage() {
//                                                        @Override
//                                                        public void onClickImage(Dialog dialog) {
//                                                            dialog.dismiss();
//                                                        }
//                                                    });

                                                Utility.customDialogAlert(GenerateQrActivity.this, "ไม่พบรายการ", new Utility.OnClickCloseImage() {
                                                    @Override
                                                    public void onClickImage(Dialog dialog) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        } else {
                                            dialogLoading.dismiss();
//                                            if (typeInterface != null)  //sinn rs232 20180705
//                                                TellToPosError("EN");
                                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                                System.out.printf("utility:: GenerateQrActivity AAAABBBBBB \n");
                                                Utility.customDialogAlertAuto(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้");
                                                TellToPosError("21");
                                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                    @Override
                                                    public void success() {
                                                        Utility.customDialogAlertAutoClear();
                                                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                        overridePendingTransition(0, 0);
                                                    }
                                                });
                                            } else
                                                Utility.customDialogAlert(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
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
                                    Log.d(TAG, "onError: " + e.getMessage());
                                    dialogLoading.dismiss();
//                                    if (typeInterface != null)  //sinn rs232 20180705
//                                        TellToPosError("EN");
                                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                        System.out.printf("utility:: GenerateQrActivity onError \n");
                                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้");
                                        TellToPosError("21");
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
                                                Utility.customDialogAlertAutoClear();
                                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(0, 0);
                                            }
                                        });
                                    } else
                                        Utility.customDialogAlert(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                                            @Override
                                            public void onClickImage(Dialog dialog) {
                                                dialog.dismiss();
                                            }
                                        });
                                }

                                @Override
                                public void onComplete() {
                                    dialogLoading.dismiss();
                                }
                            });
                } else {
//                    if (typeInterface != null)  //sinn rs232 20180705
//                        TellToPosError("EN");
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณา Generator QR ก่อน");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
                        Utility.customDialogAlert(GenerateQrActivity.this, "กรุณา Generator QR ก่อน", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                }


                break;
            case R.id.generatorBtn://K.GAME 180917 disable
                System.out.printf("utility:: %s KKKKKKKK0004 \n", TAG);
//                View view = this.getCurrentFocus();
//                if (view != null) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                if (amountBox.getText().toString().trim().isEmpty()) {
//                    Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                        }
//                    });
//                } else if (ref1Box.getText().toString().isEmpty()) {
//                    Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref1", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                        }
//                    });
//                } else if (ref2LinearLayout.getVisibility() == View.VISIBLE && ref2Box.getText().toString().isEmpty()) {
//                    Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref2", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    generatorQr();
//                }
                break;
            case R.id.qrSuccessBtn:
                System.out.printf("utility:: %s KKKKKKKK0005 \n", TAG);
                if (!tagAll.isEmpty()) {
                    Check check = new Check();
                    check.setBillerId(billerId);
                    check.setRef1(ref1Box.getText().toString());
                    if (ref2Box.getText().toString().isEmpty()) {
                        check.setRef2("");
                    } else {
                        check.setRef2(ref2Box.getText().toString());
                    }
                    if (ref3Box.getText().toString().isEmpty()) {
                        check.setRef3("");
                    } else {
                        check.setRef3(ref3Box.getText().toString());
                    }
                    check.setTerminalId(qrTid);
                    Log.d(TAG, "billerId: " + billerId + " ref1Box : " + ref1Box.getText().toString()
                            + " ref2Box :" + ref2Box.getText().toString()
                            + " ref3Box :" + ref3Box.getText().toString() +
                            " qrTid : " + qrTid);
                    dialogLoading.show();
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
                                                selectQrSlip();

                                                //SINN //20180706 Add QR print.
                                                String szMSG = new String();
//                                                szMSG = traceSlipLabel.getText().toString();
                                                szMSG = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
                                                Log.d(TAG, "KEY_QR_LAST_TRACE2 :" + szMSG);
                                                szMSG = String.valueOf(Integer.valueOf(szMSG) - 1);
                                                Preference.getInstance(GenerateQrActivity.this).setValueString(Preference.KEY_QR_LAST_TRACE, CardPrefix.calLen(szMSG, 6));
                                            } else {
//                                                if (typeInterface != null)  //sinn rs232 20180705
//                                                    TellToPosError("12");
                                                //END sinn rs232 20180705
                                                dialogLoading.dismiss();
                                                String dec = object.getString("desc");
                                                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                                    Utility.customDialogAlertAuto(GenerateQrActivity.this, dec);
                                                    TellToPosError("ND");
                                                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                        @Override
                                                        public void success() {
                                                            Utility.customDialogAlertAutoClear();
                                                            Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                            overridePendingTransition(0, 0);
                                                        }
                                                    });
                                                } else
                                                    Utility.customDialogAlert(GenerateQrActivity.this, dec, new Utility.OnClickCloseImage() {
                                                        @Override
                                                        public void onClickImage(Dialog dialog) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            }
                                        } else {
                                            dialogLoading.dismiss();
//                                            if (typeInterface != null)  //sinn rs232 20180705
//                                                TellToPosError("EN");
                                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                                System.out.printf("utility:: GenerateQrActivity AAAABBBBBB \n");
                                                Utility.customDialogAlertAuto(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้");
                                                TellToPosError("21");
                                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                    @Override
                                                    public void success() {
                                                        Utility.customDialogAlertAutoClear();
                                                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                        overridePendingTransition(0, 0);
                                                    }
                                                });
                                            } else
                                                Utility.customDialogAlert(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
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
                                    Log.d(TAG, "onError: " + e.getMessage());
                                    dialogLoading.dismiss();
//                                    if (typeInterface != null)  //sinn rs232 20180705
//                                        TellToPosError("EN");
                                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                        System.out.printf("utility:: GenerateQrActivity onError \n");
                                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้");
                                        TellToPosError("21");
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
                                                Utility.customDialogAlertAutoClear();
                                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(0, 0);
                                            }
                                        });
                                    } else
                                        Utility.customDialogAlert(GenerateQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                                            @Override
                                            public void onClickImage(Dialog dialog) {
                                                dialog.dismiss();
                                            }
                                        });
                                }

                                @Override
                                public void onComplete() {
                                    dialogLoading.dismiss();
                                }
                            });
                } else {
//                    if (typeInterface != null)  //sinn rs232 20180705
//                        TellToPosError("EN");
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณา Generator QR ก่อน");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
                        Utility.customDialogAlert(GenerateQrActivity.this, "กรุณา Generator QR ก่อน", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                }
                break;
            case R.id.linearLayoutPrint:
                System.out.printf("utility:: %s KKKKKKKK0006 \n", TAG);
                String valueParameterEnable2 = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1000);
                if (amountBox.getText().toString().isEmpty()) {

//                    if (typeInterface != null)  //sinn rs232 20180705
//                        TellToPosError("EN");
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอกจำนวนเงิน");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
                        Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                } else if ((valueParameterEnable2.substring(1, 2).contains("3")) && (valueParameterEnable2.substring(1, 2).contains("4"))) {
//                    if (ref1Box.getText().toString().isEmpty() ) {
//                    if (typeInterface != null)  //sinn rs232 20180705
//                        TellToPosError("EN");
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        System.out.printf("utility:: %s UUU0003 \n", TAG);
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref1");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else {
                        System.out.printf("utility:: %s UUU0004 \n", TAG);
                        Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref1", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }
                }/* else if (ref2LinearLayout.getVisibility() == View.VISIBLE && ref2Box.getText().toString().isEmpty()) {
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref2");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
                        Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref2", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                } */ else if (tagAll.isEmpty()) {
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                        Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณา Generator QR ก่อน");
                        TellToPosError("EN");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
                        Utility.customDialogAlert(GenerateQrActivity.this, "กรุณา Generator QR ก่อน", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                } else {
//                    linearLayoutPrint.setVisibility(View.INVISIBLE);//K.GAME 181011 New button print slip qr
//                    linearLayoutPrint.setClickable(false);//K.GAME กดPrint QR แล้วปิดปุ่ม
                    selectQr();
                }
                break;
        }
    }

    private void pos_generateQr() {

        linear_InsertRef.setVisibility(View.GONE);
        linear_amountBox.setVisibility(View.GONE);
        linear123.setVisibility(View.GONE);
        linear456.setVisibility(View.GONE);
        linear789.setVisibility(View.GONE);
        linear0_del.setVisibility(View.GONE);
        tv_label_qr01.setVisibility(View.GONE);
        ////////////////////////////////////////
        linear_generate_Qr.setVisibility(View.VISIBLE);
        tv_label_qr02.setVisibility(View.VISIBLE);
        viewPagerMenu.setVisibility(View.VISIBLE);
//        ref1LinearLayout.setVisibility(View.VISIBLE);//K.GAME 181029 แก้บัค ต้องเชค key_tag_1000
        String valueParameterEnable = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1000);   //para enable
        if (!valueParameterEnable.isEmpty()) {
            if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));    //ref1  view only
                ref1Box.setEnabled(false);
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));   //ref1 editable
                ref1Box.setEnabled(true);
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
                ref1LinearLayout.setVisibility(View.GONE);
                ref1Box.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1002));  //ref1 hide
                ref1Box.setEnabled(false);
            }
        }
//        ref1Box.setEnabled(true);//K.GAME 181029 ต้องเชคจากพารามิเตอร์ key_tag_1000

        tv_qr_amount.setText("AMT : " + amountBox.getText().toString());
        tv_qr_merchant_th.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME_THAI));
        tv_qr_merchant_en.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME));
        Log.d("1919ref1Box", ref1Box.getText().toString());
        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {   // Paul_20181106
            amountInterface = amt;
            amountBox.setText(amountInterface);
            ref1Box.setText(ref1);
            ref2Box.setText(ref2);
            ref3Box.setText(ref3);
        }
        if ((ref1Box.getText().toString().isEmpty()) || (ref1Box.getText().toString().equals(" "))) {
            Log.d("1919", ref1Box.getText().toString());
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {       // Paul_1021
                System.out.printf("utility:: %s UUU0005 \n", TAG);
                Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref1");
                TellToPosError("EN");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else {
                System.out.printf("utility:: %s UUU0006 \n", TAG);
                Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref1", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }/* else if (ref2LinearLayout.getVisibility() == View.VISIBLE && (ref2Box.getText().toString().isEmpty() || ref2Box.getText().toString().equals(" "))) {
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {       // Paul_20181021
                Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref2");
                TellToPosError("EN");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else
                Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref2", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
        } */ else {
            Log.d("1919", ref1Box.getText().toString());
            tv_line.setVisibility(View.GONE);
            linear_generate_Qr.setVisibility(View.GONE);
            tv_label_qr02.setVisibility(View.GONE);
            viewPagerMenu.setVisibility(View.GONE);
            ref1LinearLayout.setVisibility(View.GONE);
            ////////////////////////////////////
            linear_show_qr.setVisibility(View.VISIBLE);
            linear_qrSuccessBtn_new.setVisibility(View.VISIBLE);
            ///SINN 20181014 SINN POS QR fixed
            linear_show_qr.setVisibility(View.VISIBLE);
            linear_qrSuccessBtn_new.setVisibility(View.VISIBLE);
            linear_SuccessPrint.setVisibility(View.VISIBLE);//K.GAME 181011 for New Buttom print slip qr
            generatorQr();
        }
    }

    private void customDialog_insert_ref() {

        Dialog dialogInsertRef = new Dialog(this);
        dialogInsertRef.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInsertRef.setContentView(R.layout.dialog_custom_show_qr);
        dialogInsertRef.setCancelable(false);
        dialogInsertRef.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInsertRef.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button cancelBtn = dialogInsertRef.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Button btn_ok = dialogInsertRef.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialogInsertRef.show();

    }


    //SINN RS232 error
    public void TellToPosMatching() {
        String szMSG = new String();

        posInterfaceActivity.PosInterfaceWriteField("01", "000000000");   // Approval Code
        //posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("00"));   // Response Message TX NOT FOUND

        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));   // Response Message TX NOT FOUND

        szMSG = traceSlipLabel.getText().toString();
        posInterfaceActivity.PosInterfaceWriteField("65", szMSG);   // Invoice Number
        posInterfaceActivity.PosInterfaceWriteField("D3", "000000000000");  //Reference No
        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID));
//        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_ID)); //20180814 SINN  use QR Merchant ID instead biller id.

        posInterfaceActivity.PosInterfaceWriteField("03", szDateOrg);  //yymmdd


        posInterfaceActivity.PosInterfaceWriteField("04", szTimeOrg);  //hhmmss

        posInterfaceActivity.PosInterfaceWriteField("F1", "QR   ");

        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, "00");


    }

    public void TellToPosError(String szErr) {
//        posInterfaceActivity.PosInterfaceWriteField("01","000xxxxxx");   // Approval Code
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(szErr));
//
//        posInterfaceActivity.PosInterfaceWriteField("65","000000");   // Invoice Number
//        posInterfaceActivity.PosInterfaceWriteField("D3","xxxxxxxxxxxx");
//        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_QR_TERMINAL_ID));
//        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
//        Date date = new Date();
//        String dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);
//        posInterfaceActivity.PosInterfaceWriteField("03",dateFormatDef);  //yymmdd
//
//        String timeFormat = new SimpleDateFormat("HHmmss").format(date);
//        posInterfaceActivity.PosInterfaceWriteField("04",timeFormat);  //hhmmss
//
//        posInterfaceActivity.PosInterfaceWriteField("F1","QR");

        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, szErr);
    }

    //END SINN RS232 20180705


//    private void generatorQr() {
//
//       /* if (ref1Box.getText().toString().trim().isEmpty()) {
//            Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref1 ", new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            });
//        } else if (ref2Box.getText().toString().isEmpty()) {
//            Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref2 ", new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            });
//        } else {*/
//
//        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
//        Date date = new Date();
//        dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
//        timeFormat = new SimpleDateFormat("HHmmss").format(date);
//        dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);
//
//        //SINN copy  QR data & time 20180706
//        szDateOrg = dateFormatDef;
//        szTimeOrg = timeFormat;
//        //END SINN copy  QR data & time 20180706
//
//        aid = Preference.getInstance(this).getValueString(Preference.KEY_QR_AID);
//        billerId = Preference.getInstance(this).getValueString(Preference.KEY_QR_BILLER_ID);
//
//        //KTB request change
////        qrTid = Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID) +
////                CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_QR_TRACE_NO), 6) +
////                dateFormatDef; //"00025068000023180517";  //tid trace yymmdd
//
//        //QR เดิม REF3 จะเป็น TID+INVOICE+DATE เปลี่ยนเป็น TID[8]+DATE[YYMMDD]+[hhmmss]
//        qrTid = Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID) +
//                dateFormatDef + timeFormat;  //TID +DATE+TIME
//        //KTB Request change
//
//        Log.d(TAG, "onClick: " + qrTid);
//        Log.d(TAG, "onClick: " + billerId);
//        Log.d(TAG, "onClick: " + aid);
//        nameCompany = /*"NAKHONRATCHASIMA PCG."+ */Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_MERCHANT_NAME);
//
//        tagAll = Utility.idValue("", "00", "01");
//        tagAll = Utility.idValue(tagAll, "01", "11");
//        String tagIn30 = Utility.idValue("", "00", aid);
//        tagIn30 = Utility.idValue(tagIn30, "01", billerId);
//        tagIn30 = Utility.idValue(tagIn30, "02", ref1Box.getText().toString());
//        if (!ref2Box.getText().toString().isEmpty()) {
//            tagIn30 = Utility.idValue(tagIn30, "03", ref2Box.getText().toString());
//        }
//        String tag30 = Utility.idValue("", "30", tagIn30);
//        tagAll += tag30;
//        tagAll = Utility.idValue(tagAll, "53", "764");
////        tagAll = Utility.idValue(tagAll, "54", decimalFormat.format((amountBox.getText().toString().replaceAll(",",""))));//K.GAME 180926 เจอบัค
//        String szmsg;
//        szmsg = amountBox.getText().toString().replaceAll(",", "");
//        Log.d("szmsg = ", szmsg);
//        tagAll = Utility.idValue(tagAll, "54", szmsg);//K.GAME 180926 เจอบัค
////        tagAll = Utility.idValue(tagAll, "54", decimalFormat.format(Double.valueOf(amountBox.getText().toString())));//K.GAME 180926 เจอบัค
////        tagAll = Utility.idValue(tagAll, "54", decimalFormat.format(Double.valueOf(numberPrice)));//K.GAME 180926 เจอบัค
//        tagAll = Utility.idValue(tagAll, "58", "TH");
//        tagAll = Utility.idValue(tagAll, "59", nameCompany);
//        String tagIn62 = Utility.idValue("", "07", qrTid);
//        String tag62 = Utility.idValue("", "62", tagIn62);
//        tagAll += tag62;
////                tagAll = Utility.idValue(tagAll, "63", "");
//        tagAll += "6304";
//        Log.d(TAG, "initWidget: B " + tagAll);
//        tagAll += Utility.CheckSumCrcCCITT(tagAll);
//        Log.d(TAG, "initWidget: A " + tagAll);
//
//        System.out.printf("utility:: %s tagAll = %s\n",TAG,tagAll);
//        qrImage.setImageBitmap(Utility.createQRImage(tagAll, 300, 300, GenerateQrActivity.this));//K.GAME 180919 แก้ ขนาด 300 > 150
//
////        qrImage.setImageBitmap(Utility.createQRImage(tagAll, 150, 150, GenerateQrActivity.this));//K.GAME 180919 แก้ ขนาด 300 > 150
////            thaiQrImage.setVisibility(View.VISIBLE);
//        insertGenerateQr();
//        /*
//        }*/
//
//    }

    private void generatorQr() {

       /* if (ref1Box.getText().toString().trim().isEmpty()) {
            Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref1 ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else if (ref2Box.getText().toString().isEmpty()) {
            Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref2 ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else {*/

        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Date date = new Date();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
        timeFormat = new SimpleDateFormat("HHmmss").format(date);
        dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);

        //SINN copy  QR data & time 20180706
        szDateOrg = dateFormatDef;
        szTimeOrg = timeFormat;
        //END SINN copy  QR data & time 20180706

        aid = Preference.getInstance(this).getValueString(Preference.KEY_QR_AID);
        billerId = Preference.getInstance(this).getValueString(Preference.KEY_QR_BILLER_ID);

        //KTB request change
//        qrTid = Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID) +
//                CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_QR_TRACE_NO), 6) +
//                dateFormatDef; //"00025068000023180517";  //tid trace yymmdd

        //QR เดิม REF3 จะเป็น TID+INVOICE+DATE เปลี่ยนเป็น TID[8]+DATE[YYMMDD]+[hhmmss]
        qrTid = Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID) +
                dateFormatDef + timeFormat;  //TID +DATE+TIME
        //KTB Request change

        Log.d(TAG, "onClick: " + qrTid);
        Log.d(TAG, "onClick: " + billerId);
        Log.d(TAG, "onClick: " + aid);
        nameCompany = /*"NAKHONRATCHASIMA PCG."+ */Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_MERCHANT_NAME);

        tagAll = Utility.idValue("", "00", "01");
        tagAll = Utility.idValue(tagAll, "01", "11");
        String tagIn30 = Utility.idValue("", "00", aid);
        tagIn30 = Utility.idValue(tagIn30, "01", billerId);
        String ChangeToUpCase;
        ChangeToUpCase = ref1Box.getText().toString();
        ChangeToUpCase = ChangeToUpCase.toUpperCase();        // Paul_20190617

//        tagIn30 = Utility.idValue(tagIn30, "02", ref1Box.getText().toString());
        tagIn30 = Utility.idValue(tagIn30, "02", ChangeToUpCase);
        if (!ref2Box.getText().toString().isEmpty()) {
            ChangeToUpCase = ref2Box.getText().toString();
            ChangeToUpCase = ChangeToUpCase.toUpperCase();        // Paul_20190617
//            tagIn30 = Utility.idValue(tagIn30, "03", ref2Box.getText().toString());
            tagIn30 = Utility.idValue(tagIn30, "03", ChangeToUpCase);
        }
        String tag30 = Utility.idValue("", "30", tagIn30);
        tagAll += tag30;
        tagAll = Utility.idValue(tagAll, "53", "764");
//        tagAll = Utility.idValue(tagAll, "54", decimalFormat.format((amountBox.getText().toString().replaceAll(",",""))));//K.GAME 180926 เจอบัค
        String szmsg;
        szmsg = amountBox.getText().toString().replaceAll(",", "");
        Log.d("szmsg = ", szmsg);
        tagAll = Utility.idValue(tagAll, "54", szmsg);//K.GAME 180926 เจอบัค
//        tagAll = Utility.idValue(tagAll, "54", decimalFormat.format(Double.valueOf(amountBox.getText().toString())));//K.GAME 180926 เจอบัค
//        tagAll = Utility.idValue(tagAll, "54", decimalFormat.format(Double.valueOf(numberPrice)));//K.GAME 180926 เจอบัค
        tagAll = Utility.idValue(tagAll, "58", "TH");
        ChangeToUpCase = nameCompany;
        ChangeToUpCase = ChangeToUpCase.toUpperCase();        // Paul_20190617
//        tagAll = Utility.idValue(tagAll, "59", nameCompany);
        tagAll = Utility.idValue(tagAll, "59", ChangeToUpCase);

        String tagIn62 = Utility.idValue("", "07", qrTid);
        String tag62 = Utility.idValue("", "62", tagIn62);
        tagAll += tag62;
//                tagAll = Utility.idValue(tagAll, "63", "");
        tagAll += "6304";
        Log.d(TAG, "initWidget: B " + tagAll);
        tagAll += Utility.CheckSumCrcCCITT(tagAll);
        Log.d(TAG, "initWidget: A " + tagAll);

        System.out.printf("utility:: %s tagAll = %s\n", TAG, tagAll);
        qrImage.setImageBitmap(Utility.createQRImage(tagAll, 300, 300, GenerateQrActivity.this));//K.GAME 180919 แก้ ขนาด 300 > 150

//        qrImage.setImageBitmap(Utility.createQRImage(tagAll, 150, 150, GenerateQrActivity.this));//K.GAME 180919 แก้ ขนาด 300 > 150
//            thaiQrImage.setVisibility(View.VISIBLE);
        insertGenerateQr();
        /*
        }*/

    }


    // //sinn 20180927 autoprint qr code
    private void printbarcode() {
        System.out.printf("utility:: %s printbarcode 00000001 \n", TAG);
        if (amountBox.getText().toString().isEmpty()) {

//                    if (typeInterface != null)  //sinn rs232 20180705
//                        TellToPosError("EN");
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอกจำนวนเงิน");
                TellToPosError("EN");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else
                Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
        } else if (ref1Box.getText().toString().isEmpty()) {
//                    if (typeInterface != null)  //sinn rs232 20180705
//                        TellToPosError("EN");
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                System.out.printf("utility:: %s UUU0007 \n", TAG);
                Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref1");
                TellToPosError("EN");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else {
                System.out.printf("utility:: %s UUU0008 \n", TAG);
                Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref1", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        } /*else if (ref2LinearLayout.getVisibility() == View.VISIBLE && ref2Box.getText().toString().isEmpty()) {
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณากรอก Ref2");
                TellToPosError("EN");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else
                Utility.customDialogAlert(GenerateQrActivity.this, "กรุณากรอก Ref2", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
        } */ else if (tagAll.isEmpty()) {
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                Utility.customDialogAlertAuto(GenerateQrActivity.this, "กรุณา Generator QR ก่อน");
                TellToPosError("EN");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else
                Utility.customDialogAlert(GenerateQrActivity.this, "กรุณา Generator QR ก่อน", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
        } else {
//            linearLayoutPrint.setVisibility(View.INVISIBLE);
//            linearLayoutPrint.setClickable(false);
            selectQr();
        }

    }
//END //sinn 20180927 autoprint qr code

    public void setViewPrintQr() {
        LayoutInflater inflaterQr =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tagViewQr = inflaterQr.inflate(R.layout.dialog_print_qr, null);

//        setContentView(R.layout.dialog_print_qr);

        bankImage = tagViewQr.findViewById(R.id.bankImage);
        bank1Image = tagViewQr.findViewById(R.id.bank1Image);

        merchantName1Label = tagViewQr.findViewById(R.id.merchantName1Label);
        merchantName2Label = tagViewQr.findViewById(R.id.merchantName2Label);
        merchantName3Label = tagViewQr.findViewById(R.id.merchantName3Label);
        merchantNameThaiLabel = tagViewQr.findViewById(R.id.merchantNameThaiLabel);
        merchantNameThaiLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_MERCHANT_NAME_THAI));
        merchantNameLabel = tagViewQr.findViewById(R.id.merchantNameLabel);
        merchantNameLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_MERCHANT_NAME));
        tidLabel = tagViewQr.findViewById(R.id.tidLabel);
        billerIdLabel = tagViewQr.findViewById(R.id.billerIdLabel);
        traceNoLabel = tagViewQr.findViewById(R.id.traceNoLabel);
        dateLabel = tagViewQr.findViewById(R.id.dateLabel);
        timeLabel = tagViewQr.findViewById(R.id.timeLabel);
        comCodeLabel = tagViewQr.findViewById(R.id.comCodeLabel);
        amtThbLabel = tagViewQr.findViewById(R.id.amtThbLabel);
        qrSilpImage = tagViewQr.findViewById(R.id.qrImage);
        ref1RelativeLayout = tagViewQr.findViewById(R.id.ref1RelativeLayout);
        ref1Label = tagViewQr.findViewById(R.id.ref1Label);
        ref2RelativeLayout = tagViewQr.findViewById(R.id.ref2RelativeLayout);
        ref2Label = tagViewQr.findViewById(R.id.ref2Label);
        slipLinearLayout = tagViewQr.findViewById(R.id.slipLinearLayout);
        printBtn = tagViewQr.findViewById(R.id.printBtn);
        ref3RelativeLayout = tagViewQr.findViewById(R.id.ref3RelativeLayout);
        ref3Label = tagViewQr.findViewById(R.id.ref3Label);
    }

    private void insertGenerateQr() {
        Number currentId = realm.where(QrCode.class).max("id");
        if (currentId == null) {
            nextId = 1;
        } else {
            currentIdObl = currentId.intValue();
            nextId = currentId.intValue() + 1;
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                String traceId = Preference.getInstance(GenerateQrActivity.this).getValueString( Preference.KEY_QR_TRACE_NO);
                String traceId = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL); ////SINN 20180713 QR share invoice number
                QrCode qrCode = realm.createObject(QrCode.class, nextId);
                qrCode.setAid(aid);
                qrCode.setQrTid(qrTid);
                qrCode.setBillerId(billerId);
                qrCode.setTrace(CardPrefix.calLen(traceId, 6));
                qrCode.setDate(dateFormat);
                qrCode.setTime(timeFormat);
                qrCode.setComCode(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1001));
                qrCode.setRef1(ref1Box.getText().toString());
                qrCode.setRef2(ref2Box.getText().toString());
//                qrCode.setRef3(ref3Box.getText().toString());
                qrCode.setNameCompany(nameCompany);
                qrCode.setTextQrGenerateAll(tagAll);
                qrCode.setAmount(amountBox.getText().toString());
                qrCode.setStatusPrint("0");
                qrCode.setStatusSuccess("0");
                qrCode.setVoidFlag("N");
                qrCode.setHostTypeCard("QR");     // Paul_20181020
                realm.copyFromRealm(qrCode);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                String traceIdOld = Preference.getInstance(GenerateQrActivity.this).getValueString( Preference.KEY_QR_TRACE_NO);
//                Preference.getInstance(GenerateQrActivity.this).setValueString( Preference.KEY_QR_TRACE_NO, String.valueOf(Integer.valueOf(traceIdOld) + 1));
//SINN 20180713 QR share invoice number
                String traceIdOld = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
                Preference.getInstance(GenerateQrActivity.this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(traceIdOld) + 1));
//END SINN 20180713 QR share invoice number

                ////SINN 20181129 Railway project QR ref1
                if (Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_ID).equalsIgnoreCase("1")) {
                    String szrailway_ref1 = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_REF1_ID);
                    Preference.getInstance(GenerateQrActivity.this).setValueString(Preference.KEY_RAILWAY_REF1_ID, String.valueOf(Integer.valueOf(szrailway_ref1) + 1));
                }
                ////END SINN 20181129 Railway project QR ref1

                Log.d(TAG, "onSuccess: ");
                if (Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_PRINTQR_ID).equalsIgnoreCase("1"))
                    printbarcode();   //sinn 20180927 autoprint qr code

                if (Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_RAILWAY_ID).equalsIgnoreCase("1")) {

                    btService.enableBluetooth();
                }
            }
        });
    }

    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        } else if (numberPrice.equalsIgnoreCase(null)) {
            numberPrice = "";
        }
    }

    private void clickCal(View v) {
        int maxlen = 12;                // Paul_20190131
        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < maxlen)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190131
                numberPrice += "0";
        } else if (v == dotClickFrameLayout) {
            checkNumberPrice();
            if (!numberPrice.isEmpty()) {
                if (!numberPrice.contains(".")) {
                    numberPrice += ".";
                }
            } else {
                numberPrice += "0.";
            }
        } else if (v == exitClickFrameLayout) {
            Log.d(TAG, "clickCal exitClickFrameLayout");
            cardManager.abortPBOCProcess();
//            finish();
            ////20180724 SINN cannot cancel by manual
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                System.out.printf("utility:: clickCal \n");
                TellToPosError("ND");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
//                        dialogCardError.dismiss();//ไม่มีอันนี้
                        Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class); // Paul_20180704
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else {
                finish();
            }


        } else if (v == deleteClickFrameLayout) {
            if (!amountBox.getText().toString().equalsIgnoreCase("0.00")) {
                Log.d(TAG, "clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + numberPrice);
                    numberPrice = "";
                    amountBox.setText("0.00");
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
//                        priceLabel.setText(amountInterface);
                        amountBox.setText(decFormat.format(Double.valueOf(amountInterface) / 100));
                    else            // Paul_20181106
                        amountBox.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
                    Log.d("1919_1", amountBox.getText().toString());

                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        if (numberPrice.equalsIgnoreCase("") || numberPrice == null) {
                            Log.d(TAG, "clickCal: if");
                            numberPrice = "";
                            amountBox.setText("0.00");
                            if (posInterfaceActivity.PosInterfaceExistFlg == 1)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
//                                priceLabel.setText(amountInterface);
                                amountBox.setText(decFormat.format(Double.valueOf(amountInterface) / 100));
                            else    // Paul_20181106
                                amountBox.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
                            Log.d("1919_2", amountBox.getText().toString());
                        } else {
                            Log.d(TAG, "clickCal: else");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } else if (v == sureClickFrameLayout) {
            //20180723 SINNN fixed double click.
            if (!numberPrice.isEmpty())
                sureClickFrameLayout.setEnabled(false);
//            submitAmount();//ไม่มีในฟังก์ชันนี้

        }

    }

    private void dialogSuccess_GotoMain() {
        dialogSuccess_GotoMain = new Dialog(this);
        dialogSuccess_GotoMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess_GotoMain.setContentView(R.layout.dialog_custom_success_gotomain);
        dialogSuccess_GotoMain.setCancelable(false);
        dialogSuccess_GotoMain.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSuccess_GotoMain.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogSuccess_GotoMain.findViewById(R.id.msgLabel);
//        Button btn_gotoMain = dialogSuccess_GotoMain.findViewById(R.id.btn_gotoMain);
        btn_gotoMain = dialogSuccess_GotoMain.findViewById(R.id.btn_gotoMain);
        btn_gotoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class); //SINN 20180705
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
//        dialogSuccess_GotoMain.show();
    }

    private void selectQr() {
        System.out.printf("utility:: %s selectQr\n", TAG);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("id", nextId).findFirst();    // Paul_20181020
                Log.d(TAG, "execute: " + qrCode.toString());
                tidLabel.setText("TID : " + Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID));//K.GAME 180926
                traceNoLabel.setText("TRACE : " + qrCode.getTrace());
                billerIdLabel.setText(qrCode.getBillerId());
                dateLabel.setText(getString(R.string.date_qr, qrCode.getDate()));
                String timeHH = qrCode.getTime().substring(0, 2);
                String timeMM = qrCode.getTime().substring(2, 4);
                String timeSS = qrCode.getTime().substring(4, 6);
                timeLabel.setText(getString(R.string.time_qr, timeHH + ":" + timeMM + ":" + timeSS));
//                comCodeLabel.setText(qrCode.getComCode());
                String getAmountmsg = qrCode.getAmount();
                Log.d("getAmountmsg = ", getAmountmsg);
//                amtThbLabel.setText(getString(R.string.slip_pattern_amount, getAmountmsg));//K.GAME 180926 แก้บัค โชว์ฟอร์เมต
                amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(qrCode.getAmount().replaceAll(",", "")))));
// Paul_20190123 Start
                String valueParameterEnable = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1000);

                if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {      //ref1
                    ref1RelativeLayout.setVisibility(View.VISIBLE);
                    ref1Label.setText(qrCode.getRef1());
                } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                    ref1RelativeLayout.setVisibility(View.VISIBLE);
                    ref1Label.setText(qrCode.getRef1());
                } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
//                    ref1RelativeLayout.setVisibility(View.GONE);
                    ref1Label.setText(qrCode.getRef1());
                }
                if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {    //ref2
                    ref2RelativeLayout.setVisibility(View.VISIBLE);
                    ref2Label.setText(qrCode.getRef2());
                } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
                    ref2RelativeLayout.setVisibility(View.VISIBLE);
                    ref2Label.setText(qrCode.getRef2());
                } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
//                    ref2RelativeLayout.setVisibility(View.GONE);
                    ref2Label.setText(qrCode.getRef2());
                }

                if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {    //ref3
                    ref3RelativeLayout.setVisibility(View.VISIBLE);
//                    ref3Label.setText(qrCode.getRef3());
                } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
                    ref3RelativeLayout.setVisibility(View.VISIBLE);
//                    ref3Label.setText(qrCode.getRef3());
                } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
                    ref3RelativeLayout.setVisibility(View.GONE);
//                    ref3Label.setText(qrCode.getRef3());
                }
// Paul_20190123 End

                qrSilpImage.setImageBitmap(Utility.createQRImage(qrCode.getTextQrGenerateAll(), 300, 300, GenerateQrActivity.this));

//                qrTidSlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                qrTidSlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID));
                billerSlipLabel.setText(qrCode.getBillerId());
                traceSlipLabel.setText(qrCode.getTrace());
                dateSlipLabel.setText(qrCode.getDate());
                String timeHHSlip = qrCode.getTime().substring(0, 2);
                String timeMMSlip = qrCode.getTime().substring(2, 4);
                String timeSSSlip = qrCode.getTime().substring(4, 6);
                timeSlipLabel.setText(timeHHSlip + ":" + timeMMSlip + ":" + timeSSSlip);
//                comCodeSlipLabel.setText(qrCode.getComCode());
                amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(qrCode.getAmount().replaceAll(",", "")))));
                QrCode qrCode = realm.where(QrCode.class).equalTo("id", nextId).findFirst();
                if (qrCode != null) {
                    qrCode.setStatusPrint("1");
                    realm.copyToRealmOrUpdate(qrCode);
                }
                setMeasureQr();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                msgLabel.setText("กำลังพิมพ์");//K.GAME 180925 test หลังจาก generate จะให้ขึ้นว่า กำลังพิมพ์
                dialogAlertLoading.show();
                doPrinting_generate_qr(getBitmap(slipLinearLayout));//K.GAME 180925 New print slip QR
            }
        });
    }

    private void selectQrSlip() {
        System.out.printf("utility:: %s selectQrSlip\n", TAG);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
                qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("id", nextId).findFirst();    // Paul_20181020
                Log.d(TAG, "execute: " + qrCode.toString());

                if (!Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                    merchantName1SlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                if (!Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                    merchantName2SlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                if (!Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                    merchantName3SlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_MERCHANT_3));

//                qrTidSlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                qrTidSlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID));//K.GAME 180926//K.GAME 180926
                billerSlipLabel.setText(qrCode.getBillerId());
//                midSlipLabel.setText( Preference.getInstance(GenerateQrActivity.this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
                midSlipLabel.setText(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_MERCHANT_ID));  //20180814 SINN  use QR Merchant ID instead biller id.

                apprCodeLabel.setText("000000");
                int batch = Integer.parseInt(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
                batchSlipLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
//                batchSlipLabel.setText(CardPrefix.calLen(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS),6));
                System.out.printf("utility:: %s qrCode.getTrace() = %s \n", TAG, qrCode.getTrace());
                traceSlipLabel.setText(qrCode.getTrace());
//                Preference.getInstance(GenerateQrActivity.this).setValueString(Preference.KEY_QR_LAST_TRACE, traceSlipLabel.getText().toString());
                System.out.printf("utility:: %s traceSlipLabel.getText().toString() = %s \n", TAG, traceSlipLabel.getText().toString());

                // Paul_20190131 Time Date update
// Paul_20190131
                Date date = new Date();
                if (qrCode.getStatusSuccess().equalsIgnoreCase("1")) {
                    dateFormat = qrCode.getDate();
                    timeFormat = qrCode.getTime();
                } else {
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
                    timeFormat = new SimpleDateFormat("HHmmss").format(date);
                }
//
//                String dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
//                String timeFormat = new SimpleDateFormat("HHmmss").format(date);
                System.out.printf("utility:: %s dateFormat = %s, timeFormat = %s\n", TAG, dateFormat, timeFormat);
                dateSlipLabel.setText(dateFormat);
                String timeHH = timeFormat.substring(0, 2);
                String timeMM = timeFormat.substring(2, 4);
                String timeSS = timeFormat.substring(4, 6);

//                dateSlipLabel.setText(qrCode.getDate());
//                String timeHH = qrCode.getTime().substring(0, 2);
//                String timeMM = qrCode.getTime().substring(2, 4);
//                String timeSS = qrCode.getTime().substring(4, 6);
                timeLabel.setText(getString(R.string.time_qr, timeHH + ":" + timeMM + ":" + timeSS));
                timeSlipLabel.setText(timeHH + ":" + timeMM + ":" + timeSS);

                name_sw_version.setVisibility(View.VISIBLE);             // Paul_20190125 software version print
                name_sw_version.setText(BuildConfig.VERSION_NAME);      // Paul_20190125 software version print

                inquiryLabel.setText(qrCode.getQrTid());
//                comCodeSlipLabel.setText(qrCode.getComCode());
                amtThbSlipLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(qrCode.getAmount().replaceAll(",", "")))));
                /*if (qrCode.getRef1() != null) {
                    ref1SlipRelativeLayout.setVisibility(View.VISIBLE);
                    ref1SlipLabel.setText(qrCode.getRef1());
                }
                if (qrCode.getRef2() != null) {
                    ref2SlipRelativeLayout.setVisibility(View.VISIBLE);
                    ref2SlipLabel.setText(qrCode.getRef2());
                }*/

                QrCode qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("id", nextId).findFirst(); // Paul_20181020
                if (qrCode != null) {
                    System.out.printf("utility:: %s setDataSuccess\n", TAG);
                    qrCode.setStatusSuccess("1");
                    qrCode.setDate(dateFormat);    // Paul_20190131 Time Date update
                    qrCode.setTime(timeFormat);    // Paul_20190131 Time Date update
                    realm.copyToRealmOrUpdate(qrCode);

                    if (!qrCode.getRef1().isEmpty()) {
                        tagView_ref1Label.setText(qrCode.getRef1());
                        tagView_ref1RelativeLayout.setVisibility(View.VISIBLE);
                    } else {
                        tagView_ref1RelativeLayout.setVisibility(View.GONE);
                    }

                    if (!qrCode.getRef2().isEmpty()) {
                        tagView_ref2Label.setText(qrCode.getRef2());
                        tagView_ref2RelativeLayout.setVisibility(View.VISIBLE);
                    } else {
                        tagView_ref2RelativeLayout.setVisibility(View.GONE);
                    }

//                    if(!qrCode.getRef3().isEmpty()){
//                        tagView_ref3Label.setText(qrCode.getRef3());
//                        tagView_ref3RelativeLayout.setVisibility(View.VISIBLE);
//                    }else {
//                    tagView_ref3RelativeLayout.setVisibility(View.GONE);
//                    }
                }

                setMeasure();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                dialogAlertLoading.show();
                // sinn rs232 20180705
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                    TellToPosMatching();
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            doPrinting(getBitmap(slipSuccessLinearLayout));
                            statusPrintFinish = 1;
                        }
                    });
                } else {
                    //K.GAME 180919
                    if (dialogAlertLoading != null) {
                        dialogAlertLoading.dismiss();
                    }
                    // K.GAME เก็บไว้ใช้ง
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new CountDownTimer(1500, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
//                                    customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");//K.GAME 181026 ปิดไป เนื่องจาก เมื่อก่อนมีให้พิมพ์ใบเสร็จ 2 ใบ แต่เปลี่ยนเหลือ 1 ใบ
                                    customDialogWaiting("กำลังพิมพ์ใบเสร็จ");//K.GAME 181026 พิมพ์ใบเสร็จ เหลือ 1 ใบ ต้องแสดงข้อความว่า กำลังพิมพ์ใบเสร็จ
                                    dialogWaiting.show();

                                }

                                @Override
                                public void onFinish() {
                                    // dialogWaiting.dismiss();
                                }
                            }.start();
                        }
                    });
                    //END K.GAME เก็บไว้ใช้
//                    doPrinting(getBitmap(slipSuccessLinearLayout));
                    doPrinting_game(getBitmap(slipSuccessLinearLayout));
                    //END K.GAME 180919
                    statusPrintFinish = 1;
                    //autoPrint();
                }
            }
        });
    }

    private void customDialogWaiting(String msg) {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        msgLabel = dialogWaiting.findViewById(R.id.msgLabel); //K.GAME 180831 chang waitting UI
//        msgLabel.setText("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");//K.GAME 180831 chang waitting UI
        if (msg != null) {
            msgLabel.setText(msg);
        }
        Utility.animation_Waiting_new(dialogWaiting);//K.GAME 180831 chang waitting UI
        //END K.GAME 180831 chang waitting UI
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

    public Bitmap getBitmap(View v) {
        return getBitmapFromView(v);
    }

    public void doPrinting(final Bitmap slip) {
        GenerateQRorInquireFlg = 0;
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
                            if (dialogAlertLoading != null) {
                                dialogAlertLoading.dismiss();
                            }
                            if (statusPrintFinish == 1) {
//                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceActivity.class);
//                                dialogSuccess_GotoMain.show();//K.GAME 180919 New dialog  อันเดิมคือด้านล่าง
                                if (posInterfaceActivity.PosInterfaceExistFlg == 1)     // Paul_20181017 goto main page no need
                                {
                                    Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class); //SINN 20180705
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            }
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
        dialogSuccess_GotoMain.show();//K.GAME 180919
    }

    public void doPrinting_generate_qr(final Bitmap slip) {//K.GAME 180925 New Function
        GenerateQRorInquireFlg = 1;
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
                            if (dialogAlertLoading != null) {
                                dialogAlertLoading.dismiss();
                            }
                            if (statusPrintFinish == 1) {
//                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceActivity.class);
//                                dialogSuccess_GotoMain.show();//K.GAME 180919 New dialog  อันเดิมคือด้านล่าง

//                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class); //SINN 20180705
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                                overridePendingTransition(0, 0);
                            }
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

    public void doPrinting_game(final Bitmap slip) {
        GenerateQRorInquireFlg = 2;
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
                            if (dialogAlertLoading != null) {
                                dialogAlertLoading.dismiss();
                            }
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            if (statusPrintFinish == 1) {
//                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceActivity.class);
//                                dialog_reprint_confirm_qr();
//                                dialog_reprint_confirm_qr.show();
//                                Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class); //SINN 20180705
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                                overridePendingTransition(0, 0);
                            }
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
//        dialog_reprint_confirm_qr.show();//K.GAME 180919 comment close dialog slip 2 // เมื่อก่อนมีให้แสดงคอมเฟิร์มเพื่อพิมพ์ใบที่ 2 แต่เปลี่ยนเหลือ1ใบ เลยให้ไปdialog goto main เลย
        dialogSuccess_GotoMain.show();//K.GAME 181026 เมื่อก่อนมีให้แสดงคอมเฟิร์มเพื่อพิมพ์ใบที่ 2 แต่เปลี่ยนเหลือ1ใบ เลยให้ไปdialog goto main เลย
    }


    public void customDialogAlertLoading() {
        dialogAlertLoading = new Dialog(this);
        dialogAlertLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlertLoading.setContentView(R.layout.dialog_custom_load_process);
        dialogAlertLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlertLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogAlertLoading.findViewById(R.id.waitingImage);
        msgLabel = dialogAlertLoading.findViewById(R.id.msgLabel);//K.GAME 180925 อยากให้มีคำว่า กำลังพิมพ์
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogAlertLoading);
        //END K.GAME 180831 chang waitting UI

//        dialogAlertLoading = new Dialog(this);
//        dialogAlertLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlertLoading.setContentView(R.layout.dialog_custom_alert_loading);
//        dialogAlertLoading.setCancelable(false);
//        dialogAlertLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlertLoading.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
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
                switch (GenerateQRorInquireFlg)      // Paul_20190216
                {
                    case 1:
                        doPrinting_generate_qr(bitmapOld);
                        break;
                    case 2:
                        doPrinting_game(bitmapOld);
                        break;
                    case 0:
                    default:
                        doPrinting(bitmapOld);
                        break;
                }
//                doPrinting(bitmapOld);
//                doPrinting_generate_qr(bitmapOld);
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
//        dialogLoading.setContentView(R.layout.dialog_custom_alert_loading);
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void hidingKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService( //K.GAME 180917 Disable
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
        //SINN 20180713 QR Pos autogenerate QR
        // if((amountBox.getText().length()>0)&&(typeInterface != null)&&(ref1Box.getText().length()>0))   //SINN 20180713 QR Pos autogenerate QR  //SINN 20180722 check ref1box

//        if ((amountBox.getText().length() > 0) && (typeInterface != null)) {
//            generatorBtn.performClick();
//
//        }

        //END SINN 20180713 QR Pos autogenerate QR
    }

    @Override
    protected void onStop() {
        byte[] EOT = new byte[]{0x04, 0x04, 0x04};
        try {
            if (data_out != null && blSocket.isConnected())
                data_out.write(EOT);

            if (data_in != null && blSocket.isConnected())
                data_in.close();
            if (data_out != null && blSocket.isConnected())
                data_out.close();
            if (blSocket != null && blSocket.isConnected())
                blSocket.close();

            data_in = null;
            data_out = null;
            blSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                System.out.printf("utility:: GenerateQrActivity onKeyDown \n");
                if (CancelFlg == 0) {
                    CancelFlg = 1;
                    Utility.customDialogAlertAuto(GenerateQrActivity.this, "Cancel");   // Paul_20181017 Can
                    TellToPosError("ND");
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            typeInterface = null;
                            Intent intent = new Intent(GenerateQrActivity.this, MenuServiceListActivity.class);
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
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            /** 추가된 부분 시작 **/
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    //btService.getDeviceInfo(data);
                    blSocket = btService.getDeviceSocket(data);
                    //BluetoothHandler.sendEmptyMessage(0);
                    new Thread() {
                        public void run() {
                            if (blSocket != null) {

                                try {
                                    data_out = new DataOutputStream(blSocket.getOutputStream());
                                    data_in = new DataInputStream(blSocket.getInputStream());

                                    byte[] temp_send = Make_send_msg();
                                    if (temp_send == null) {
                                        Log.d(TAG, "exit Tran ");
                                    }
                                    Log.d(TAG, "buffer length : " + temp_send.length);
                                    data_out.write(temp_send, 0, temp_send.length);
                                    //.write(Make_send_msg());
                                    data_out.flush();
                                    byte[] temp_recv = new byte[7];
                                    int read_rtn = data_in.read(temp_recv);

                                    Log.d(TAG, "exit Tran " + read_rtn + " " + new String(temp_recv));
                                    /*
                                    if (blSocket.isConnected()) {
                                        data_out.close();
                                        data_in.close();
                                        blSocket.close();
                                    }
                                    */
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
                break;
            /** 추가된 부분 끝 **/
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == RESULT_OK) {
                    // Next Step
                    btService.scanDevice();
                } else {

                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }

    private byte[] Make_send_msg() {
        byte[] msg = null;
        byte type;

        //Log.d(TAG, "aaada h " + linear_show_qr.getHeight() + " w " + linear_show_qr.getWidth() );

        Bitmap ForSendRailway = getBitmapFromView(linear_show_qr);

        try {
            String filepath1 = getFilesDir().getPath();
            Qr_send_filename = "qr_send" + ref1Box.getText().toString() + ".png";
            File file1 = new File(filepath1, Qr_send_filename);
            OutputStream outStream1 = null;
            outStream1 = new FileOutputStream(file1);
            ForSendRailway.compress(Bitmap.CompressFormat.PNG, 90, outStream1);
            outStream1.flush();
            outStream1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        type = 'I';
        String filepath = getFilesDir().getPath();
        File imgFile = new File(filepath, Qr_send_filename);
        Log.d(TAG, "file :" + imgFile.getPath() + " " + imgFile.getName());
        int file_len = (int) imgFile.length();
        msg = new byte[file_len];
        try {
            FileInputStream fis = new FileInputStream(imgFile);
            fis.read(msg, 0, file_len);
            fis.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }


        long send_size = msg.length + 8 + 1;
        byte[] temp = new byte[8];
        temp = longToBytes(send_size);

        byte[] sendByte = new byte[(int) send_size + 8];

        System.arraycopy(temp, 0, sendByte, 0, 8);
        sendByte[8] = type;
        System.arraycopy(msg, 0, sendByte, 9, msg.length);
        CRC32 checksum = new CRC32();
        checksum.update(msg);
        long lck = checksum.getValue();

        temp = longToBytes(lck);
        System.arraycopy(temp, 0, sendByte, (int) send_size, 8);

        imgFile.delete();
        return sendByte;
    }

    private static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }
}
