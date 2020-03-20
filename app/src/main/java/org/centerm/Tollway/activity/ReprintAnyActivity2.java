package org.centerm.Tollway.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

import org.centerm.Tollway.BuildConfig;
import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.adapter.ReprintAnyAdapter;
import org.centerm.Tollway.adapter.ReprintAnyAdapter2;
import org.centerm.Tollway.alipay.AliConfig;
import org.centerm.Tollway.alipay.AliVoidActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.DBListTemp;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_APPROVAL_CODE;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;

public class ReprintAnyActivity2 extends SettingToolbarActivity {

    private final String TAG = "ReprintAnyActivity2";
    private RecyclerView recyclerViewReprintAny;
    private CardManager cardManager;
    private ReprintAnyAdapter reprintAnyAdapter;
    private ReprintAnyAdapter2 reprintAnyAdapter2;
    private ArrayList<TransTemp> transTempList;
    private ArrayList<DBListTemp> transTempList2;   // Paul_20181023
    private AidlPrinter printDev = null;
    private Realm realm;
    private EditText invoiceEt;
    private ImageView searchInvoiceImage;
    private String typeHost = null;
    private TransTemp transTemp = null;
    private DBListTemp transTemp2 = null;

    private Integer inSetPrintType;

    private View printLastView_customer;
    private View printLastView_merchant;

    private Button closeImage; //K.GAME 180828 change dialog UI
    private String typeClick;

    private View reportSettlementLast;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private Dialog dialogHostQr;
    private Button posQrBtn;
    private Button tmsQrBtn;
    private Button epsQrBtn;

    private Button ghcQrBtn;  ////20180708 SINN Add healthcare print.

    private Button qrBtn;

    private TextView taxIdLabel;
    private TextView taxAbbLabel;
    private TextView traceTaxLabel;
    private TextView batchTaxLabel;
    private TextView dateTaxLabel;
    private TextView timeTaxLabel;
    private TextView feeTaxLabel;
    private TextView copyLabel;
    private TextView nameEmvCardLabel;
    private LinearLayout taxLinearLayout;
    private TextView typeInputCardLabel;
    private TextView taxIdLabel_C;
    private TextView taxAbbLabel_C;
    private TextView traceTaxLabel_C;
    private TextView batchTaxLabel_C;
    private TextView dateTaxLabel_C;
    private TextView timeTaxLabel_C;
    private TextView feeTaxLabel_C;
    private TextView copyLabel_C;
    private TextView nameEmvCardLabel_C;
    private LinearLayout taxLinearLayout_C;
    private TextView typeInputCardLabel_C;

    private boolean isStatusPrintLastSlip = false;
    private Dialog dialogLoading;

    private TextView dialogLoading_msg;  ////SINN 20181107  message for dialog print waiting

    private FrameLayout comCodeFragment;
    private LinearLayout summaryLinearFeeLayout;
    private TextView merchantName1FeeLabel;
    private TextView merchantName2FeeLabel;
    private TextView merchantName3FeeLabel;
    private TextView dateFeeLabel;
    private TextView timeFeeLabel;
    private TextView batchFeeLabel;
    private TextView hostFeeLabel;
    private TextView saleCountFeeLabel;
    private TextView saleTotalFeeLabel;
    private TextView voidSaleCountFeeLabel;
    private TextView voidSaleAmountFeeLabel;
    private TextView cardCountFeeLabel;
    private TextView cardAmountFeeLabel;
    private TextView taxIdFeeLabel;

    private Dialog dialogHost;
    /**
     * Slip
     */
    private View hgcSaleView;
    private LinearLayout slip_sale_void_hgc_re; //(:
    private TextView DateTimePrn;   ////20180720 SINN  HGC slip fix
    private TextView dateHgcLabel;
    private TextView timeHgcLabel;
    private TextView midHgcLabel;
    private TextView tidHgcLabel;
    private TextView traceNoLabel;
    private TextView typeSaleLabel;
    private TextView nameEngLabel;
    private TextView batchHgcLabel;
    private TextView comCodeHgcLabel;  // Paul_20180714
    private TextView apprCodeHgcLabel; // Paul_20180712
    private TextView cardNoHgcLabelxx; // Paul_20180716
    private TextView amountLabel;
    private TextView merchantName1HgcLabel;
    private TextView merchantName2HgcLabel;
    private TextView merchantName3HgcLabel;
    private AidlPrinter printer;
    //end 20180708 SINN Add healthcare print.
    private String typeInterface;
    private String invoiceId;
    private String trace;
    private String approvalcode;
    private PosInterfaceActivity posInterfaceActivity;


    private FrameLayout fee_thb;
    private FrameLayout tot_thb;
    private FrameLayout fee_thb_C;
    private FrameLayout tot_thb_C;

    //K.GAME
    /***
     * SALE AND VOID
     */
    private ImageView bankImage = null;
    private ImageView bank1Image = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private TextView tidLabel = null;
    private TextView midLabel = null;
    private TextView traceLabel = null;
    private TextView systrcGHCLabel = null;
    private TextView systrcLabel = null;
    private TextView batchLabel = null;
    private TextView refNoLabel = null;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView typeLabel = null;
    private TextView typeCardLabel = null;
    private TextView cardNoLabel = null;
    private TextView apprCodeLabel = null;
    private TextView comCodeLabel = null;
    private TextView amtThbLabel = null;
    private TextView feeThbLabel = null;
    private TextView totThbLabel = null;
    private TextView ref1Label = null;
    private TextView ref2Label = null;
    private TextView ref3Label = null;
    private RelativeLayout ref1RelativeLayout = null;
    private RelativeLayout ref2RelativeLayout = null;
    private RelativeLayout ref3RelativeLayout = null;
    private LinearLayout slipLinearLayout = null;
    private TextView sigatureLabel = null;
    private TextView appLabel;
    private FrameLayout appFrameLabel;
    private TextView tcLabel;
    private FrameLayout tcFrameLayout;
    private TextView aidLabel;
    private FrameLayout aidFrameLayout;
    private TextView name_sw_version;  // Paul_20190125 software version print
    private TextView name_sw_version_C;  // Paul_20190125 software version print

    private TextView merchantName1Label_C = null;
    private TextView merchantName2Label_C = null;
    private TextView merchantName3Label_C = null;
    private TextView tidLabel_C = null;
    private TextView midLabel_C = null;
    private TextView traceLabel_C = null;
    private TextView systrcGHCLabel_C = null;
    //    private TextView systrcLabel_C = null;
    private TextView batchLabel_C = null;
    private TextView refNoLabel_C = null;
    private TextView dateLabel_C = null;
    private TextView timeLabel_C = null;
    private TextView typeLabel_C = null;
    private TextView typeCardLabel_C = null;
    private TextView cardNoLabel_C = null;
    private TextView apprCodeLabel_C = null;
    private TextView comCodeLabel_C = null;
    private TextView amtThbLabel_C = null;
    private TextView feeThbLabel_C = null;
    private TextView totThbLabel_C = null;
    private TextView ref1Label_C = null;
    private TextView ref2Label_C = null;
    private TextView ref3Label_C = null;
    private RelativeLayout ref1RelativeLayout_C = null;
    private RelativeLayout ref2RelativeLayout_C = null;
    private RelativeLayout ref3RelativeLayout_C = null;
    private LinearLayout slipLinearLayout_C = null;
    private FrameLayout comCodeFragment_C;

    DecimalFormat decFormatDisplay; //20180812 SINN BIG AMOUNT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.printf("utility:: %s onCreate \n", TAG);
        setContentView(R.layout.activity_reprint_any);
        cardManager = MainApplication.getCardManager();
        decFormatDisplay = new DecimalFormat("##,###,##0.00");      // Paul_20181028 Sinn merge version UAT6_0016
        printDev = cardManager.getInstancesPrint();
        posInterfaceActivity = MainApplication.getPosInterfaceActivity();

        if (realm == null) {                    // Paul_20181023
            realm = Realm.getDefaultInstance();
        }
        initData();
        initWidget();
        initBtnExit();
        setViewReprint();//K.GAME 180907
        setViewReprint_Customer();
        setViewReprint_Merchant();
        setDataBaseMerge();     // Paul_20181023
        customDialogLoading();
        customDialogOutOfPaper();

    }

    private void setViewReprint_Merchant() {

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printLastView_merchant = inflater.inflate(R.layout.view_reprint_merchant, null);
        merchantName1Label = printLastView_merchant.findViewById(R.id.merchantName1Label);
        merchantName2Label = printLastView_merchant.findViewById(R.id.merchantName2Label);
        merchantName3Label = printLastView_merchant.findViewById(R.id.merchantName3Label);
        slipLinearLayout = printLastView_merchant.findViewById(R.id.slipLinearLayout);
        tidLabel = printLastView_merchant.findViewById(R.id.tidLabel);
        midLabel = printLastView_merchant.findViewById(R.id.midLabel);
        traceLabel = printLastView_merchant.findViewById(R.id.traceLabel);
        systrcLabel = printLastView_merchant.findViewById(R.id.systrcLabel);
        batchLabel = printLastView_merchant.findViewById(R.id.batchLabel);
        refNoLabel = printLastView_merchant.findViewById(R.id.refNoLabel);
        dateLabel = printLastView_merchant.findViewById(R.id.dateLabel);
        timeLabel = printLastView_merchant.findViewById(R.id.timeLabel);
        typeLabel = printLastView_merchant.findViewById(R.id.typeLabel);
        typeCardLabel = printLastView_merchant.findViewById(R.id.typeCardLabel);
        cardNoLabel = printLastView_merchant.findViewById(R.id.cardNoLabel);
        apprCodeLabel = printLastView_merchant.findViewById(R.id.apprCodeLabel);
        comCodeLabel = printLastView_merchant.findViewById(R.id.comCodeLabel);
        amtThbLabel = printLastView_merchant.findViewById(R.id.amtThbLabel);
        feeThbLabel = printLastView_merchant.findViewById(R.id.feeThbLabel);
        totThbLabel = printLastView_merchant.findViewById(R.id.totThbLabel);
        ref1Label = printLastView_merchant.findViewById(R.id.ref1Label);
        ref2Label = printLastView_merchant.findViewById(R.id.ref2Label);
        ref3Label = printLastView_merchant.findViewById(R.id.ref3Label);
        ref1RelativeLayout = printLastView_merchant.findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayout = printLastView_merchant.findViewById(R.id.ref2RelativeLayout);
        ref3RelativeLayout = printLastView_merchant.findViewById(R.id.ref3RelativeLayout);

        taxIdLabel = printLastView_merchant.findViewById(R.id.taxIdLabel);
        taxAbbLabel = printLastView_merchant.findViewById(R.id.taxAbbLabel);
        traceTaxLabel = printLastView_merchant.findViewById(R.id.traceTaxLabel);
        batchTaxLabel = printLastView_merchant.findViewById(R.id.batchTaxLabel);
        dateTaxLabel = printLastView_merchant.findViewById(R.id.dateTaxLabel);
        timeTaxLabel = printLastView_merchant.findViewById(R.id.timeTaxLabel);
        feeTaxLabel = printLastView_merchant.findViewById(R.id.feeTaxLabel);
        copyLabel = printLastView_merchant.findViewById(R.id.copyLabel);
        nameEmvCardLabel = printLastView_merchant.findViewById(R.id.nameEmvCardLabel);
        taxLinearLayout = printLastView_merchant.findViewById(R.id.taxLinearLayout);
        sigatureLabel = printLastView_merchant.findViewById(R.id.sigatureLabel);
        typeInputCardLabel = printLastView_merchant.findViewById(R.id.typeInputCardLabel);
        comCodeFragment = printLastView_merchant.findViewById(R.id.comCodeFragment);

        appLabel = printLastView_merchant.findViewById(R.id.appLabel);
        appFrameLabel = printLastView_merchant.findViewById(R.id.appFrameLabel);
        tcLabel = printLastView_merchant.findViewById(R.id.tcLabel);
        tcFrameLayout = printLastView_merchant.findViewById(R.id.tcFrameLayout);
        aidLabel = printLastView_merchant.findViewById(R.id.aidLabel);
        aidFrameLayout = printLastView_merchant.findViewById(R.id.aidFrameLayout);

        fee_thb = printLastView_merchant.findViewById(R.id.fee_thb);    //SINN 20181031 merge KTBNORMAL again.
        tot_thb = printLastView_merchant.findViewById(R.id.tot_thb);     //SINN 20181031 merge KTBNORMAL again.

        name_sw_version = printLastView_merchant.findViewById(R.id.name_sw_version);
    }

    private void setViewReprint_Customer() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printLastView_customer = inflater.inflate(R.layout.view_reprint_customer, null);
        merchantName1Label_C = printLastView_customer.findViewById(R.id.merchantName1Label);
        merchantName2Label_C = printLastView_customer.findViewById(R.id.merchantName2Label);
        merchantName3Label_C = printLastView_customer.findViewById(R.id.merchantName3Label);
        slipLinearLayout_C = printLastView_customer.findViewById(R.id.slipLinearLayout);
        tidLabel_C = printLastView_customer.findViewById(R.id.tidLabel);
        midLabel_C = printLastView_customer.findViewById(R.id.midLabel);
        traceLabel_C = printLastView_customer.findViewById(R.id.traceLabel);
//        systrcLabel_C = printLastView_customer.findViewById(R.id.systrcLabel);
        batchLabel_C = printLastView_customer.findViewById(R.id.batchLabel);
        refNoLabel_C = printLastView_customer.findViewById(R.id.refNoLabel);
        dateLabel_C = printLastView_customer.findViewById(R.id.dateLabel);
        timeLabel_C = printLastView_customer.findViewById(R.id.timeLabel);
        typeLabel_C = printLastView_customer.findViewById(R.id.typeLabel);
        typeCardLabel_C = printLastView_customer.findViewById(R.id.typeCardLabel);
        cardNoLabel_C = printLastView_customer.findViewById(R.id.cardNoLabel);
        apprCodeLabel_C = printLastView_customer.findViewById(R.id.apprCodeLabel);
        comCodeLabel_C = printLastView_customer.findViewById(R.id.comCodeLabel);
        amtThbLabel_C = printLastView_customer.findViewById(R.id.amtThbLabel);
        feeThbLabel_C = printLastView_customer.findViewById(R.id.feeThbLabel);
        totThbLabel_C = printLastView_customer.findViewById(R.id.totThbLabel);
        ref1Label_C = printLastView_customer.findViewById(R.id.ref1Label);
        ref2Label_C = printLastView_customer.findViewById(R.id.ref2Label);
        ref3Label_C = printLastView_customer.findViewById(R.id.ref3Label);
        ref1RelativeLayout_C = printLastView_customer.findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayout_C = printLastView_customer.findViewById(R.id.ref2RelativeLayout);
        ref3RelativeLayout_C = printLastView_customer.findViewById(R.id.ref3RelativeLayout);

        taxIdLabel_C = printLastView_customer.findViewById(R.id.taxIdLabel);
        taxAbbLabel_C = printLastView_customer.findViewById(R.id.taxAbbLabel);
        traceTaxLabel_C = printLastView_customer.findViewById(R.id.traceTaxLabel);
        batchTaxLabel_C = printLastView_customer.findViewById(R.id.batchTaxLabel);
        dateTaxLabel_C = printLastView_customer.findViewById(R.id.dateTaxLabel);
        timeTaxLabel_C = printLastView_customer.findViewById(R.id.timeTaxLabel);
        feeTaxLabel_C = printLastView_customer.findViewById(R.id.feeTaxLabel);
        copyLabel_C = printLastView_customer.findViewById(R.id.copyLabel);
        nameEmvCardLabel_C = printLastView_customer.findViewById(R.id.nameEmvCardLabel);
        taxLinearLayout_C = printLastView_customer.findViewById(R.id.taxLinearLayout);
        typeInputCardLabel_C = printLastView_customer.findViewById(R.id.typeInputCardLabel);
        comCodeFragment_C = printLastView_customer.findViewById(R.id.comCodeFragment);

        fee_thb_C = printLastView_customer.findViewById(R.id.fee_thb);    //SINN 20181031 merge KTBNORMAL again.
        tot_thb_C = printLastView_customer.findViewById(R.id.tot_thb);     //SINN 20181031 merge KTBNORMAL again.

        name_sw_version_C = printLastView_customer.findViewById(R.id.name_sw_version);
    }

    private void setViewReprint() {//K.GAME 180907
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        hgcSaleView = inflater.inflate( R.layout.view_slip_sale_hgc_re, null);
        hgcSaleView = inflater.inflate(R.layout.view_slip_sale_hgc_re, null);
        slip_sale_void_hgc_re = hgcSaleView.findViewById(R.id.slip_sale_void_hgc_re_lay);   //(:
        dateHgcLabel = hgcSaleView.findViewById(R.id.dateLabel);
        DateTimePrn = hgcSaleView.findViewById(R.id.DateTimePrn); //20180720 SINN  HGC slip fix
        timeHgcLabel = hgcSaleView.findViewById(R.id.timeLabel);
        midHgcLabel = hgcSaleView.findViewById(R.id.midLabel);
        tidHgcLabel = hgcSaleView.findViewById(R.id.tidLabel);
        systrcGHCLabel = hgcSaleView.findViewById(R.id.systrcGHCLabel);
        traceNoLabel = hgcSaleView.findViewById(R.id.traceNoLabel);
        typeSaleLabel = hgcSaleView.findViewById(R.id.typeSaleLabel);
        cardNoHgcLabelxx = hgcSaleView.findViewById(R.id.cardNoLabelxx);//PAUL_20180716
        nameEngLabel = hgcSaleView.findViewById(R.id.nameEngLabel);
        apprCodeHgcLabel = hgcSaleView.findViewById(R.id.apprCodeLabel);        // Paul_20180712
        comCodeHgcLabel = hgcSaleView.findViewById(R.id.comCodeLabel);      // Paul_20180714
//        comCodeLabel = hgcSaleView.findViewById(R.id.comCodeLabel);
        batchHgcLabel = hgcSaleView.findViewById(R.id.batchLabel);
        amountLabel = hgcSaleView.findViewById(R.id.amountLabel);
        merchantName1HgcLabel = hgcSaleView.findViewById(R.id.merchantName1Label);
        merchantName2HgcLabel = hgcSaleView.findViewById(R.id.merchantName2Label);
        merchantName3HgcLabel = hgcSaleView.findViewById(R.id.merchantName3Label);
        //20180720 SINN  HGC slip fix
        dateHgcLabel.setText("");
        DateTimePrn.setText("");
        timeHgcLabel.setText("");
        midHgcLabel.setText("");
        tidHgcLabel.setText("");
        systrcGHCLabel.setText("");
        traceNoLabel.setText("");
        typeSaleLabel.setText("");
        cardNoHgcLabelxx.setText("");
        nameEngLabel.setText("");
        apprCodeHgcLabel.setText("");
        comCodeHgcLabel.setText("");
        batchHgcLabel.setText("");
        amountLabel.setText("");
        merchantName1HgcLabel.setText("");
        merchantName2HgcLabel.setText("");
        merchantName3HgcLabel.setText("");
//END 20180720 SINN  HGC slip fix
    }

    // Paul_20181023
    private void setDataBaseMerge() {
        int i;

        if (realm == null) {                    // Paul_20181023
            realm = Realm.getDefaultInstance();
        }


//        RealmResults<DBListTemp> dblistTemp1 = realm.where(DBListTemp.class).findAll();

        // Temp DataBase Clear
//        dblistTemp1.deleteAllFromRealm();
        deleteDBListTemp();

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        DBListTemp dblistTemp = realm.where(DBListTemp.class).findFirst();

        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).findAll();

        int nextId;
        int nextId2 = 1;
        int nextId3 = 1;

        for (i = 0; i < transTemp.size(); i++) {
            realm.beginTransaction();
            Number currentId = realm.where(DBListTemp.class).max("id");
            if (currentId == null) {
                nextId = 1;
            } else {
                nextId = currentId.intValue() + 1;
            }
            dblistTemp = realm.createObject(DBListTemp.class, nextId);

//            Number currentId1 = realm.where(TransTemp.class).max("id");
//            if (currentId1 == null) {
//                nextId2 = 1;
//            } else {
//                nextId2 = currentId1.intValue() + 1;
//            }
//            transTemp = realm.createObject(TransTemp.class, nextId2++);

//            dblistTemp.setAppid(transTemp.getAppid());
//            dblistTemp.setTid(transTemp.getTid());
//            dblistTemp.setMid(transTemp.getMid());
//            dblistTemp.setTraceNo(transTemp.getTransDate());
            dblistTemp.setTransDate(transTemp.get(i).getTransDate());
            dblistTemp.setTransTime(transTemp.get(i).getTransTime());
            System.out.printf("utility:: QQQQQQQQQQQQ transTemp.getTransDate = %s \n", transTemp.get(i).getTransDate());
            System.out.printf("utility:: QQQQQQQQQQQQ transTemp.getTransTime = %s \n", transTemp.get(i).getTransTime());

            dblistTemp.setAmount(transTemp.get(i).getAmount());
//            dblistTemp.setCardType(transTemp.getCardType());
            dblistTemp.setEcr(transTemp.get(i).getEcr());
            dblistTemp.setVoidFlag(transTemp.get(i).getVoidFlag());

            dblistTemp.setHostTypeCard(transTemp.get(i).getHostTypeCard());
            realm.commitTransaction();
        }

//        RealmResults<QrCode> QrTemp = realm.where(QrCode.class).findAll();
        RealmResults<QrCode> QrTemp = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();  //SINN 20181024 add qr success only

        for (i = 0; i < QrTemp.size(); i++) {
            realm.beginTransaction();
            Number currentId = realm.where(DBListTemp.class).max("id");
            if (currentId == null) {
                nextId = 1;
            } else {
                nextId = currentId.intValue() + 1;
            }
            dblistTemp = realm.createObject(DBListTemp.class, nextId);

//            Number currentId1 = realm.where(QrCode.class).max("id");
//            if (currentId1 == null) {
//                nextId3 = 1;
//            } else {
//                nextId3 = currentId1.intValue() + 1;
//            }
//            QrTemp = realm.createObject(QrCode.class, nextId3);

//            dblistTemp.setAppid(QrTemp.getAppid());
//            dblistTemp.setTid(QrTemp.getTid());
//            dblistTemp.setMid(QrTemp.getMid());
//            dblistTemp.setTraceNo(QrTemp.getDate());
            System.out.printf("utility:: QQQQQQQQQQQQ QrTemp.getDate() = %s \n", QrTemp.get(i).getDate());
            System.out.printf("utility:: QQQQQQQQQQQQ QrTemp.getTime() = %s \n", QrTemp.get(i).getTime());
//        String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
//        transTemp.setTransDate(fDate);
//        String tTime = new SimpleDateFormat("HH:mm:ss").format(cDate);
//        dateTimeOnline = new SimpleDateFormat("yyyyMMddHHmmss").format(cDate);
//        transTemp.setTransTime(tTime);
//        dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
//        timeFormat = new SimpleDateFormat("HHmmss").format(date);
            String date = QrTemp.get(i).getDate();
            dblistTemp.setTransDate(date.substring(6, 10) + date.substring(3, 5) + date.substring(0, 2));
//            dblistTemp.setTransDate(QrTemp.getDate());
            String time = QrTemp.get(i).getTime();
            dblistTemp.setTransTime(time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6));
//                dblistTemp.setTransTime(QrTemp.get(i).getTime());
            System.out.printf("utility:: QQQQQQQQQQQQ 0002 QrTemp.getTransDate = %s \n", dblistTemp.getTransDate());
            System.out.printf("utility:: QQQQQQQQQQQQ 0002 QrTemp.getTransTime = %s \n", dblistTemp.getTransTime());

            dblistTemp.setAmount(QrTemp.get(i).getAmount());
//            dblistTemp.setCardType(QrTemp.getCardType());
            dblistTemp.setEcr(QrTemp.get(i).getTrace());
//            dblistTemp.setVoidFlag(QrTemp.getVoidFlag());
            dblistTemp.setVoidFlag("N");

            dblistTemp.setHostTypeCard(QrTemp.get(i).getHostTypeCard());
            realm.commitTransaction();
        }

        RealmResults<QrCode> AliTemp = realm.where(QrCode.class).equalTo("hostTypeCard", "ALIPAY").equalTo("respcode", "0").findAll();  //20181119 Jeff

        for (i = 0; i < AliTemp.size(); i++) {
            realm.beginTransaction();
            Number currentId = realm.where(DBListTemp.class).max("id");
            if (currentId == null) {
                nextId = 1;
            } else {
                nextId = currentId.intValue() + 1;
            }
            dblistTemp = realm.createObject(DBListTemp.class, nextId);
            String date = AliTemp.get(i).getReqChannelDtm();
            dblistTemp.setTransDate(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10));
            String time = date.substring(11, 13) + ":" + date.substring(14, 16) + ":" + date.substring(17, 19);
            dblistTemp.setTransTime(time);

            if (AliTemp.get(i).getAmtplusfee().equals("null"))
                dblistTemp.setAmount(AliTemp.get(i).getAmt());
            else
                dblistTemp.setAmount(AliTemp.get(i).getAmtplusfee());

            dblistTemp.setEcr(AliTemp.get(i).getTrace());
            dblistTemp.setVoidFlag(AliTemp.get(i).getVoidFlag());
            dblistTemp.setHostTypeCard(AliTemp.get(i).getHostTypeCard());
            realm.commitTransaction();
        }

        RealmResults<QrCode> WecTemp = realm.where(QrCode.class).equalTo("hostTypeCard", "WECHAT").equalTo("respcode", "0").findAll();  //20181119 Jeff

        for (i = 0; i < WecTemp.size(); i++) {
            realm.beginTransaction();
            Number currentId = realm.where(DBListTemp.class).max("id");
            if (currentId == null) {
                nextId = 1;
            } else {
                nextId = currentId.intValue() + 1;
            }
            dblistTemp = realm.createObject(DBListTemp.class, nextId);
            String date = WecTemp.get(i).getReqChannelDtm();
            dblistTemp.setTransDate(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10));
            String time = date.substring(11, 13) + ":" + date.substring(14, 16) + ":" + date.substring(17, 19);
            dblistTemp.setTransTime(time);

            if (WecTemp.get(i).getAmtplusfee().equals("null"))
                dblistTemp.setAmount(WecTemp.get(i).getAmt());
            else
                dblistTemp.setAmount(WecTemp.get(i).getAmtplusfee());

            dblistTemp.setEcr(WecTemp.get(i).getTrace());
            dblistTemp.setVoidFlag(WecTemp.get(i).getVoidFlag());
            dblistTemp.setHostTypeCard(WecTemp.get(i).getHostTypeCard());
            realm.commitTransaction();
        }
    }

    public void deleteDBListTemp() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<DBListTemp> transTemps = realm.where(DBListTemp.class).findAll();
                transTemps.deleteAllFromRealm();
            }
        });
        realm.close();
        realm = null;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
            invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
            approvalcode = bundle.getString(KEY_INTERFACE_VOID_APPROVAL_CODE);
            System.out.printf("utility:: %s approvalcode 0004 = %s \n", TAG, approvalcode);
        }
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
                        public void onPrintFinish() throws RemoteException {
                            if (isStatusPrintLastSlip) {
                                isStatusPrintLastSlip = false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new CountDownTimer(6000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                Log.d(TAG, "onTick: " + millisUntilFinished);
                                            }

                                            @Override
                                            public void onFinish() {
                                                System.out.printf("utility:: %s doPrinting Befor 039 \n", TAG);
                                                //doPrinting(getBitmapFromView(slipLinearLayout));
                                            }
                                        }.start();
                                    }
                                });
                            } else {
                                dialogLoading.dismiss();
//                                Intent intent = new Intent(ReprintActivity.this, MenuServiceActivity.class);
//
                                ////20181022 SINN Reprint no need dialog no need go back
//                                Intent intent = new Intent(ReprintAnyActivity2.this, MenuServiceListActivity.class); // Paul_20180704
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
                        public void onPrintError(int i) throws RemoteException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    msgLabel.setText("เกิดข้อผิดพลาด เช่นแบตเตอรี่อ่อน");
                                    dialogOutOfPaper.show();
                                }
                            });
                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogOutOfPaper.show();
                                }
                            });

                        }
                    });
//                    int ret = printDev.printBarCodeSync("asdasd");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void initWidget() {
        // super.initWidget();

        invoiceEt = findViewById(R.id.invoiceEt);
        searchInvoiceImage = findViewById(R.id.searchInvoiceImage);
        ////game 20181019 reprint new UI
        invoiceEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchALLDB();
//                    searchDataTransTemp(invoiceEt.getText().toString());
                    return true;
                }
                return false;
            }
        });
        recyclerViewReprintAny = findViewById(R.id.recyclerViewReprintAny);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReprintAny.setLayoutManager(layoutManager);


        searchInvoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchALLDB();
//                searchDataTransTemp(invoiceEt.getText().toString());
            }
        });


    }

    private void searchALLDB() {
        trace = invoiceEt.getText().toString();
        trace = checkLength(trace, 6);

        //DB DATA
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        transTemp = realm.where(TransTemp.class).equalTo("ecr", trace).findFirst();
        RealmResults<QrCode> qrTemp = realm.where(QrCode.class).equalTo("trace", trace).findAll();

        if (transTemp != null) {
            customDialogReprintConfirmSlip_sale(transTemp);
        } else if (qrTemp.size() > 0) {
            Intent service = new Intent(ReprintAnyActivity2.this, AliVoidActivity.class);
            service.putExtra("INVOICE", trace);
            service.putExtra("TYPE", AliConfig.Inquiry);
            startActivity(service);
            finish();
        } else {
            Utility.customDialogAlert(ReprintAnyActivity2.this, "ไม่พบรายการ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void searchTransDB() {


    }


    private void searchDataTransTemp(String traceNo) {
        String traceNoAddZero = "";//ถ้าพิมพ์น้อยกว่า 6 ตัวจะติด 0 ข้างหน้า
        if (!traceNo.isEmpty()) {
            if (traceNo.length() < 6) {
                for (int i = traceNo.length(); i < 6; i++) {
                    traceNoAddZero += "0";

                }

            }
            traceNoAddZero += traceNo;
//            traceBox_new.setText(traceNoAddZero);//K.GAME 180905 Add change EdidText dialog
            Log.d(TAG, "utility:: searchDataTransTemp: " + traceNoAddZero);

//            RealmResults<TransTemp> transTemp1;
//            transTemp1 = realm.where(TransTemp.class).equalTo("ecr", traceNoAddZero).findAll();

//
//            RealmResults<TransTemp> resultsearch = realm.where(TransTemp.class).equalTo("ecr", traceNoAddZero).findAll();
//            resultsearch = resultsearch.sort("ecr");
//
//            RealmResults<TransTemp> transTemp1;
//            transTemp1 = resultsearch;
            //--------------------------------------------------------------------------------------
            deleteDBListTemp();

            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            DBListTemp dblistTemp = realm.where(DBListTemp.class).findFirst();

            RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).findAll();

            int nextId;
            int nextId2 = 1;
            int nextId3 = 1;
            int i;

            for (i = 0; i < transTemp.size(); i++) {
                realm.beginTransaction();
                Number currentId = realm.where(DBListTemp.class).max("id");
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
                dblistTemp = realm.createObject(DBListTemp.class, nextId);

//            Number currentId1 = realm.where(TransTemp.class).max("id");
//            if (currentId1 == null) {
//                nextId2 = 1;
//            } else {
//                nextId2 = currentId1.intValue() + 1;
//            }
//            transTemp = realm.createObject(TransTemp.class, nextId2++);

//            dblistTemp.setAppid(transTemp.getAppid());
//            dblistTemp.setTid(transTemp.getTid());
//            dblistTemp.setMid(transTemp.getMid());
//            dblistTemp.setTraceNo(transTemp.getTransDate());
                dblistTemp.setTransDate(transTemp.get(i).getTransDate());
                dblistTemp.setTransTime(transTemp.get(i).getTransTime());
                System.out.printf("utility:: QQQQQQQQQQQQ transTemp.getTransDate = %s \n", dblistTemp.getTransDate());
                System.out.printf("utility:: QQQQQQQQQQQQ transTemp.getTransTime = %s \n", dblistTemp.getTransTime());

                dblistTemp.setAmount(transTemp.get(i).getAmount());
//            dblistTemp.setCardType(transTemp.getCardType());
                dblistTemp.setEcr(transTemp.get(i).getEcr());
                dblistTemp.setVoidFlag(transTemp.get(i).getVoidFlag());

                dblistTemp.setHostTypeCard(transTemp.get(i).getHostTypeCard());
                realm.commitTransaction();
            }

//        RealmResults<QrCode> QrTemp = realm.where(QrCode.class).findAll();
            RealmResults<QrCode> QrTemp = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("statusSuccess", "1").findAll();  //SINN 20181024 add qr success only

            for (i = 0; i < QrTemp.size(); i++) {
                realm.beginTransaction();
                Number currentId = realm.where(DBListTemp.class).max("id");
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
                dblistTemp = realm.createObject(DBListTemp.class, nextId);
                String date = QrTemp.get(i).getDate();
                dblistTemp.setTransDate(date.substring(6, 10) + date.substring(3, 5) + date.substring(0, 2));
//            dblistTemp.setTransDate(QrTemp.getDate());
                String time = QrTemp.get(i).getTime();
                dblistTemp.setTransTime(time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6));
//                dblistTemp.setTransTime(QrTemp.get(i).getTime());
                System.out.printf("utility:: QQQQQQQQQQQQ 0001 QrTemp.getTransDate = %s \n", dblistTemp.getTransDate());
                System.out.printf("utility:: QQQQQQQQQQQQ 0001 QrTemp.getTransTime = %s \n", dblistTemp.getTransTime());

                dblistTemp.setAmount(QrTemp.get(i).getAmount());
//            dblistTemp.setCardType(QrTemp.getCardType());
                dblistTemp.setEcr(QrTemp.get(i).getTrace());
//            dblistTemp.setVoidFlag(QrTemp.getVoidFlag());
                dblistTemp.setVoidFlag("N");

                dblistTemp.setHostTypeCard(QrTemp.get(i).getHostTypeCard());
                realm.commitTransaction();
            }

            RealmResults<QrCode> AliTemp = realm.where(QrCode.class).equalTo("hostTypeCard", "ALIPAY").equalTo("respcode", "0").findAll();  //20181119 Jeff

            for (i = 0; i < AliTemp.size(); i++) {
                realm.beginTransaction();
                Number currentId = realm.where(DBListTemp.class).max("id");
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
                dblistTemp = realm.createObject(DBListTemp.class, nextId);
                String date = AliTemp.get(i).getReqChannelDtm();
                dblistTemp.setTransDate(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10));
                String time = date.substring(11, 13) + ":" + date.substring(14, 16) + ":" + date.substring(17, 19);
                dblistTemp.setTransTime(time);

                if (AliTemp.get(i).getAmtplusfee().equals("null"))
                    dblistTemp.setAmount(AliTemp.get(i).getAmt());
                else
                    dblistTemp.setAmount(AliTemp.get(i).getAmtplusfee());

                dblistTemp.setEcr(AliTemp.get(i).getTrace());
                dblistTemp.setVoidFlag(AliTemp.get(i).getVoidFlag());
                dblistTemp.setHostTypeCard(AliTemp.get(i).getHostTypeCard());
                realm.commitTransaction();
            }

            RealmResults<QrCode> WecTemp = realm.where(QrCode.class).equalTo("hostTypeCard", "WECHAT").equalTo("respcode", "0").findAll();  //20181119 Jeff

            for (i = 0; i < WecTemp.size(); i++) {
                realm.beginTransaction();
                Number currentId = realm.where(DBListTemp.class).max("id");
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
                dblistTemp = realm.createObject(DBListTemp.class, nextId);
                String date = WecTemp.get(i).getReqChannelDtm();
                dblistTemp.setTransDate(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10));
                String time = date.substring(11, 13) + ":" + date.substring(14, 16) + ":" + date.substring(17, 19);
                dblistTemp.setTransTime(time);

                if (WecTemp.get(i).getAmtplusfee().equals("null"))
                    dblistTemp.setAmount(WecTemp.get(i).getAmt());
                else
                    dblistTemp.setAmount(WecTemp.get(i).getAmtplusfee());

                dblistTemp.setEcr(WecTemp.get(i).getTrace());
                dblistTemp.setVoidFlag(WecTemp.get(i).getVoidFlag());
                dblistTemp.setHostTypeCard(WecTemp.get(i).getHostTypeCard());
                realm.commitTransaction();
            }

            Log.d(TAG, "utility:: searchDataTransTemp: " + dblistTemp);
            if (realm.copyFromRealm(realm.where(DBListTemp.class).equalTo("ecr", traceNoAddZero).findAll()).size() > 0) {
                if (transTempList2 != null)
                    reprintAnyAdapter2.clear();

                if (transTempList2 == null) {
                    transTempList2 = new ArrayList<>();
                } else {
                    transTempList2.clear();
                }
//                transTempList2.addAll(realm.copyFromRealm(realm.where(DBListTemp.class).findAll().sort("ecr", Sort.DESCENDING)));
                transTempList2.addAll(realm.copyFromRealm(realm.where(DBListTemp.class).equalTo("ecr", traceNoAddZero).findAll()));
                reprintAnyAdapter2.setItem(transTempList2);
                reprintAnyAdapter2.notifyDataSetChanged();
            }

            //--------------------------------------------------------------------------------------
//
//            Log.d(TAG, "utility:: searchDataTransTemp: " + transTemp1);
//            if (transTemp1.size() > 0) {
//                if (transTempList != null)
//                reprintAnyAdapter.clear();
//
//                if (transTempList == null) {
//                    transTempList = new ArrayList<>();
//                } else {
//                    transTempList.clear();
//                }
//                transTempList.addAll(transTemp1);
//                reprintAnyAdapter.setItem(transTempList);
//                reprintAnyAdapter.notifyDataSetChanged();
//            }


        } else {
            setVoidList();
        }

    }


    private void setVoidList() {
        System.out.printf("utility:: %s  setVoidList \n", TAG);
        if (recyclerViewReprintAny.getAdapter() == null) {
            reprintAnyAdapter2 = new ReprintAnyAdapter2(this);
            recyclerViewReprintAny.setAdapter(reprintAnyAdapter2);
            reprintAnyAdapter2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();

                    if (realm == null) {
                        realm = Realm.getDefaultInstance();
                    }

                    RealmResults<DBListTemp> dbList = realm.where(DBListTemp.class).findAll();
//                    position = (dbList.size()) - position;   //Reverse
//                    String trace =  dbList.get(position).getEcr();
                    DBListTemp transTemp1 = reprintAnyAdapter2.getItem(position);
                    String trace = transTemp1.getEcr();

                    DBListTemp dbList2 = realm.where(DBListTemp.class).equalTo("ecr", trace).findFirst();
                    String type = dbList2.getHostTypeCard();

                    switch (type) {
                        case "POS":
                        case "TMS":
                        case "EPS":
                            transTemp = realm.where(TransTemp.class).equalTo("ecr", trace).findFirst();
                            customDialogReprintConfirmSlip_sale(transTemp);
                            break;
                        case "GHC":
                            transTemp = realm.where(TransTemp.class).equalTo("ecr", trace).findFirst();
                            customDialogReprintConfirmSlip(transTemp);
                            break;
                        case "QR":
//                            Intent intent = new Intent(ReprintAnyActivity2.this, ReprintQRCheckActivity.class);
//                            intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, trace);
//                            intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
//                            finish();
//                            break;
                        case "ALIPAY":
                        case "WECHAT":

                            Intent service = new Intent(ReprintAnyActivity2.this, AliVoidActivity.class);
                            service.putExtra("INVOICE", trace);
                            service.putExtra("TYPE", AliConfig.Inquiry);
                            //GAME test found activity close.
//                            service.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            service.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            service.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(service);
                            finish();
                            break;
                    }
                }
            });
        } else {
            reprintAnyAdapter2.clear();
        }
        if (transTempList2 == null) {
            transTempList2 = new ArrayList<>();
        } else {
            transTempList2.clear();
        }

//        transTempList.addAll(realm.copyFromRealm(realm.where(TransTemp.class).findAll()));  // Org
        transTempList2.addAll(realm.copyFromRealm(realm.where(DBListTemp.class).findAll().sort("ecr", Sort.DESCENDING))); // 20181022 List reverse sequence
//        transTempList2.addAll(realm.copyFromRealm(realm.where(TransTemp.class).findAll().sort("ecr", Sort.DESCENDING)));

        reprintAnyAdapter2.setItem(transTempList2);
        reprintAnyAdapter2.notifyDataSetChanged();

        if (typeInterface != null) {
            transTemp2 = reprintAnyAdapter2.getItemWithErcInvoid(invoiceId);
            if (transTemp != null) {
// Paul_20180704
//                System.out.printf("utility:: setVoidList 00003 = %s \n",transTemp.getVoidFlag());
                if (transTemp2.getVoidFlag().equalsIgnoreCase("N")) {
//                    System.out.printf("utility:: setVoidList 00004 \n");
                    typeHost = transTemp2.getHostTypeCard();
//                    customDialogInvoice(transTemp.getEcr(), transTemp.getAmount());
                } else {
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                        Utility.customDialogAlertAuto(VoidActivity.this, "รายการนี้ยกเลิกแล้ว");
//                        TerToPosNoMatching();
//                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                            @Override
//                            public void success() {
//                                Utility.customDialogAlertAutoClear();
//                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                                overridePendingTransition(0, 0);
//                            }
//                        });
                    } else
                        Utility.customDialogAlert(this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                }
            } else {
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                    Utility.customDialogAlertAuto(VoidActivity.this, "รายการนี้ยกเลิกแล้ว");
////                    TerToPosNoMatching();
//                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                        @Override
//                        public void success() {
////                            Utility.customDialogAlertAutoClear();
////                            Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                            startActivity(intent);
////                            finish();
////                            overridePendingTransition(0, 0);
//                        }
//                    });
                } else
                    Utility.customDialogAlert(this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            }
        }

    }

    private void setDataSlipOffline(TransTemp healthCareDB) {
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);
        ////20180720 SINN  HGC slip fix
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        DateTimePrn.setText("Date Time      " + dateFormat.format(date));
        //END 20180720 SINN  HGC slip fix
        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
        systrcGHCLabel.setText(healthCareDB.getTraceNo());
        Log.d("SINN:", "systrcLabel :" + systrcGHCLabel.getText());
        System.out.printf("utility:: systrcGHCLabel 003 = %s \n", systrcGHCLabel.getText());

        traceNoLabel.setText(healthCareDB.getEcr());
        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        System.out.printf("utility:: healthCareDB.getCardNo() = %s \n", healthCareDB.getCardNo());
        System.out.printf("utility:: healthCareDB.getIdCard() = %s \n", healthCareDB.getIdCard());
        String idCardCd = null;
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            cardNoHgcLabelxx.setText(healthCareDB.getCardNo()); //PAUL_20180716
            idCardCd = healthCareDB.getCardNo();
            System.out.printf("utility:: cardNoLabel 00000000XX001 = %s \n", healthCareDB.getCardNo());
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            cardNoHgcLabelxx.setText(healthCareDB.getIdCard());//PAUL_20180716
            idCardCd = healthCareDB.getIdCard();
            System.out.printf("utility:: cardNoLabel 00000000XX002 = %s \n", healthCareDB.getIdCard());
        } else {
            cardNoHgcLabelxx.setText(healthCareDB.getCardNo());//PAUL_20180716
            idCardCd = healthCareDB.getCardNo();
            System.out.printf("utility:: cardNoLabel 00000000XX003 = %s \n", healthCareDB.getCardNo());
        }
        System.out.printf("utility:: cardNoLabel 000001 = %s \n", cardNoLabel.getText());
        String szMSG = null;
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        cardNoHgcLabelxx.setText(szMSG);
//        nameEngLabel.setText(healthCareDB.getEngFName());
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
        apprCodeHgcLabel.setText(healthCareDB.getApprvCode());
        comCodeHgcLabel.setText(healthCareDB.getComCode()); //20180714_PAUL
        batchHgcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        amountLabel.setText("*" + healthCareDB.getAmount());

        setMeasureHGC();
    }

    private void setMeasureHGC() {
        hgcSaleView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleView.layout(0, 0, hgcSaleView.getMeasuredWidth(), hgcSaleView.getMeasuredHeight());
    }

    private void setPrint_off(TransTemp transTemp) {
        setDataSlipOffline(transTemp);
//        //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
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

    private void setDataSlipSale(TransTemp healthCareDB) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Paul_20180711_new
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);
        //20180720 SINN  HGC slip fix
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        DateTimePrn.setText("Date Time      " + dateFormat.format(date));
//END 20180720 SINN  HGC slip fix
        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
        systrcLabel.setText(healthCareDB.getTraceNo());
        systrcGHCLabel.setText(healthCareDB.getTraceNo());  //(:
//        Log.d("SINN:", "systrcLabel :" + systrcLabel.getText());
        System.out.printf("utility:: systrcLabel 002 = %s \n", healthCareDB.getTraceNo());

        traceNoLabel.setText(healthCareDB.getEcr());
        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        System.out.printf("utility:: healthCareDB.getCardNo() = %s \n", healthCareDB.getCardNo());
        System.out.printf("utility:: healthCareDB.getIdCard() = %s \n", healthCareDB.getIdCard());

        // Paul_20180720 Start
        String szMSG = null;
        String CardNo = null;
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            CardNo = healthCareDB.getCardNo();
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            CardNo = healthCareDB.getIdCard();
        } else {
            CardNo = healthCareDB.getCardNo();      // Paul_20180720              CardNo = healthCareDB.getIdCard();
        }
        szMSG = CardNo.substring(0, 1) + " " + CardNo.substring(1, 4) + "X" + " " + "XXXX" + CardNo.substring(9, 10) + " " + CardNo.substring(10, 12) + " " + CardNo.substring(12, 13);
        cardNoHgcLabelxx.setText(szMSG);       // Paul_20180720
        // Paul_20180720 End
//        nameEngLabel.setText(healthCareDB.getEngFName());     // Paul_20180720
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
        apprCodeHgcLabel.setText(healthCareDB.getApprvCode()); // Paul_20180712
        System.out.printf("utility:: HHHHHHHHHHHHHH 0004 apprCodeLabel = %s \n", apprCodeLabel);
//        comCodeLabel.setText("HCG13814");
        comCodeHgcLabel.setText(healthCareDB.getComCode()); // Paul_20180714
        batchHgcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));
        //amountLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(priceLabel.getText().toString()))));
//        getString(healthCareDB.getAmount(), decimalFormat.format(Double.valueOf(healthCareDB.getAmount())));
//        amountLabel.setText( healthCareDB.getAmount());

        if (healthCareDB.getVoidFlag().equals("N")) {
            amountLabel.setText("*" + healthCareDB.getAmount());
        } else {
            amountLabel.setText("-" + healthCareDB.getAmount());
        }

        setMeasureHGC();
    }

    private void setPrintLastSearch(TransTemp transTemp) {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            Log.d("SINN", "setPrintLastSearch");
            setDataSlipSale(transTemp);
            return;
        }


        dialogLoading_msg.setText("กำลังพิมพ์สำเนาสำหรับร้านค้า");   ////20181107 SINN Dialog print
        dialogLoading.show();
        DecimalFormat decimalFormatShow = new DecimalFormat("#,###,##0.00");    // Paul_20190118
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText("TID:" + transTemp.getTid());
        midLabel.setText("MID:" + transTemp.getMid());
        traceLabel.setText("TRACE:" + transTemp.getEcr());
        systrcLabel.setText(transTemp.getTraceNo());
        if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            //20180708 SINN Add healthcare print.
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC"))
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        //20180708 SINN Add healthcare print.
        refNoLabel.setText(transTemp.getRefNo());
        String date = transTemp.getTransDate().substring(6, 8);
        String mount = transTemp.getTransDate().substring(4, 6);
        String year = transTemp.getTransDate().substring(2, 4);
        dateLabel.setText(date + "/" + mount + "/" + year);
        timeLabel.setText(transTemp.getTransTime());

        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        typeCardLabel.setText(CardPrefix.getTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        String cutCardStart = transTemp.getCardNo().substring(0, 6);
//        String cutCardEnd = transTemp.getCardNo().substring(12, transTemp.getCardNo().length());
//        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
//        cardNoLabel.setText(cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16));
        cardNoLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel.setText(transTemp.getApprvCode());
//        System.out.printf("utility:: HHHHHHHHHHHHHH 0003 apprCodeLabel = %s \n",apprCodeLabel );  //SINN 20181024 found wrong debug command
        System.out.printf("utility:: HHHHHHHHHHHHHH 0003 apprCodeLabel = %s \n", apprCodeLabel.getText());
//        comCodeLabel.setText(transTemp.getComCode());
        String typeVoidOrSale = "";
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        if (typeHost.equalsIgnoreCase("POS")) {
            typeVoidOrSale = Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TYPE_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            typeVoidOrSale = Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TYPE_EPS);
        } else {
            typeVoidOrSale = Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TYPE_TMS);
        }
        if (transTemp.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabel.setText("C");
        }else if (transTemp.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabel.setText("W");
            sigatureLabel.setVisibility(View.GONE);
        } else {
            typeInputCardLabel.setText("S");
        }


        if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
//            feeTaxLabel.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
            if (transTemp.getFee() != null) {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
            } else {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
            }
            typeLabel.setText("SALE");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel.setText(transTemp.getTaxAbb());
                traceTaxLabel.setText(transTemp.getEcr());

                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));

                /*String date = transTemp.getTransDate().substring(6,8);
                String mount = transTemp.getTransDate().substring(4,6);
                String year = transTemp.getTransDate().substring(0,4);
                dateLabel.setText(date +"/" +mount + "/" + year);*/
                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);

                timeTaxLabel.setText(transTemp.getTransTime());
                copyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null) {
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                        nameEmvCardLabel.setText("");
                    } else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee))));    // Paul_20190128
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
                if (transTemp.getEmvAppLabel() != null) {
                    if (!transTemp.getEmvAppLabel().isEmpty()) {
                        appLabel.setText(transTemp.getEmvAppLabel());
                    } else {
                        // appFrameLabel.setVisibility(View.GONE);
                    }
                } else {
                    // appFrameLabel.setVisibility(View.GONE);
                }

                if (transTemp.getEmvTc() != null) {
                    if (!transTemp.getEmvTc().isEmpty()) {
                        tcLabel.setText(transTemp.getEmvTc());
                    } else {
                        tcFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    tcFrameLayout.setVisibility(View.GONE);
                }
                if (transTemp.getEmvAid() != null) {
                    if (!transTemp.getEmvAid().isEmpty()) {
                        aidLabel.setText(transTemp.getEmvAid());
                    } else {
                        aidFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    aidFrameLayout.setVisibility(View.GONE);
                }
            } else {
                comCodeFragment.setVisibility(View.VISIBLE);
                copyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                    nameEmvCardLabel.setText("");
                } else {
                    nameEmvCardLabel.setText(__name.trim());
                }

//                // appFrameLabel.setVisibility(View.GONE);
//                tcFrameLayout.setVisibility(View.GONE);
//                aidFrameLayout.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);

                taxLinearLayout.setVisibility(View.GONE);
                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
            }
        } else {
//            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            if (transTemp.getFee() != null) {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
            } else {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            }
            typeLabel.setText("VOID");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel.setText(transTemp.getTaxAbb());
                traceTaxLabel.setText(transTemp.getEcr());
                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);
//                dateTaxLabel.setText(transTemp.getTransDate());
                timeTaxLabel.setText(transTemp.getTransTime());
//                feeTaxLabel.setText(transTemp.getFee());
                copyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null) {
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                        nameEmvCardLabel.setText("");
                    } else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));    // Paul_20190128 (float) delete
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
//                if (transTemp.getEmvAppLabel() != null) {
//                    if (!transTemp.getEmvAppLabel().isEmpty()) {
//                        appLabel.setText(transTemp.getEmvAppLabel());
//                    } else {
//                        // appFrameLabel.setVisibility(View.GONE);
//                    }
//                } else {
//                    // appFrameLabel.setVisibility(View.GONE);
//                }
//
//                if (transTemp.getEmvTc() != null) {
//                    if (!transTemp.getEmvTc().isEmpty()) {
//                        tcLabel.setText(transTemp.getEmvTc());
//                    } else {
//                        tcFrameLayout.setVisibility(View.GONE);
//                    }
//                } else {
//                    tcFrameLayout.setVisibility(View.GONE);
//                }
//                if (transTemp.getEmvAid() != null) {
//                    if (!transTemp.getEmvAid().isEmpty()) {
//                        aidLabel.setText(transTemp.getEmvAid());
//                    } else {
//                        aidFrameLayout.setVisibility(View.GONE);
//                    }
//                } else {
//                    aidFrameLayout.setVisibility(View.GONE);
//                }
            } else {
                comCodeFragment.setVisibility(View.VISIBLE);
                taxLinearLayout.setVisibility(View.GONE);

//                // appFrameLabel.setVisibility(View.GONE);
//                tcFrameLayout.setVisibility(View.GONE);
//                aidFrameLayout.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);

                copyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                    nameEmvCardLabel.setText("");
                } else {
                    nameEmvCardLabel.setText(__name.trim());
                }
                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }


        //SINN 20181031 merge KTBNORMAL again.
//        if(Preference.getInstance(this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))//SINN 20181122  Merchant support rate KEY_MerchantSupportRate_ID
        if (Preference.getInstance(this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1")) {
            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);

        }
        // Paul_20190312 VOID No TAX Slip
        if (transTemp.getVoidFlag().equalsIgnoreCase("Y")) {
            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);
        }

        String valueParameterEnable = Preference.getInstance(this).getValueString(Preference.KEY_TAG_1000);
        if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
            comCodeLabel.setVisibility(View.GONE);
            comCodeLabel.setText(transTemp.getComCode());
        }
        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
            ref1RelativeLayout.setVisibility(View.GONE);
            ref1Label.setText(transTemp.getRef1());
        }
        if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
            ref2RelativeLayout.setVisibility(View.GONE);
            ref2Label.setText(transTemp.getRef2());
        }
        if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(transTemp.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(transTemp.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
            ref3RelativeLayout.setVisibility(View.GONE);
            ref3Label.setText(transTemp.getRef3());
        }
        name_sw_version.setVisibility(View.VISIBLE);             // Paul_20190125 software version print
        name_sw_version.setText(BuildConfig.VERSION_NAME);      // Paul_20190125 software version print

        if (transTemp.getTransType().equalsIgnoreCase("W")) {
            sigatureLabel.setVisibility(View.GONE);
        } else {
            sigatureLabel.setVisibility(View.VISIBLE);
        }


//        sigatureLabel.layout(0,0,0,40);
//        nameEmvCardLabel.layout(0,40,0,0);

        setMeasure();

        //game 20181019 reprint new UI
//        isStatusPrintLastSlip = true;
        System.out.printf("utility:: %s doPrinting Befor 040 \n", TAG);

        if (inSetPrintType == 1) {
            //doPrinting(getBitmapFromView(slipLinearLayout));
        } else
            rePrintLast(transTemp);
    }

    private void setMeasure() {
        printLastView_customer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printLastView_customer.layout(0, 0, printLastView_customer.getMeasuredWidth(), printLastView_customer.getMeasuredHeight());
    }

    private void rePrintLast(TransTemp transTemp) {

        dialogLoading_msg.setText("กำลังพิมพ์สำเนาสำหรับลูกค้า");   ////20181104 SINN Dialog print   // dialogLoading_msg.setText("กำลังพิมพ์สำเนาสำหรับร้านค้า");   ////20181107 SINN Dialog print

        sigatureLabel.setVisibility(View.GONE);
        if (transTemp.getEmvNameCardHolder() != null) {
//            nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
            String __name = transTemp.getEmvNameCardHolder().trim();
            if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                nameEmvCardLabel.setText("");
            } else {
                nameEmvCardLabel.setText(__name.trim());
            }
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, 0, 0, 0);
        lp.setMargins(0, 20, 0, 0); //SINN 20181217 Fix receipt copy
        nameEmvCardLabel.setLayoutParams(lp);
        nameEmvCardLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        copyLabel.setText("***** MERCHANT COPY *****");

        setMeasure();

    }

    private void customDialogLoading() {
        //K.GAME 180926
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
        //END K.GAME 180926
        dialogLoading.setCancelable(false);   // Paul_20181015 Printing Can not cancel button

        dialogLoading_msg = dialogLoading.findViewById(R.id.msgLabel);

//        dialogLoading = new Dialog(this);
//        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogLoading.setContentView(R.layout.dialog_custom_alert_loading);
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180919
        View view = dialogOutOfPaper.getLayoutInflater().inflate(R.layout.dialog_custom_printer, null);//K.GAME 180919
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180919
        dialogOutOfPaper.setContentView(view);//K.GAME 180919
        dialogOutOfPaper.setCancelable(false);//K.GAME 180919

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
                System.out.printf("utility:: %s doPrinting Befor 036 \n", TAG);
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    private void customDialogReprintConfirmSlip_sale(final TransTemp TransTempDB) { //K.GAME 1801024
        final Dialog dialogReprintConfirmSlip_sale = new Dialog(this);
        dialogReprintConfirmSlip_sale.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReprintConfirmSlip_sale.setContentView(R.layout.dialog_custom_reprint_confirm2_sale);
        dialogReprintConfirmSlip_sale.setCancelable(false);
        dialogReprintConfirmSlip_sale.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReprintConfirmSlip_sale.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ////
        Button btn_confirm_reprint = dialogReprintConfirmSlip_sale.findViewById(R.id.btn_confirm_reprint);
        Button btn_confirm_reprint2 = dialogReprintConfirmSlip_sale.findViewById(R.id.btn_confirm_reprint2);
        TextView msgLabel = dialogReprintConfirmSlip_sale.findViewById(R.id.msgLabel);
        TextView tvLabelName = dialogReprintConfirmSlip_sale.findViewById(R.id.tvLabelName);
        TextView tv_confirm_numberPrice = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_numberPrice);//ยืนยันราคา
        TextView tv_confirm_idcard = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_idcard);//ยืนยัน id card
        TextView tv_confirm_username = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_username);//usrname
        TextView tv_confirm_date = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_date);//date
        TextView tv_confirm_time = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_time);//time
        TextView tv_confirm_traceNo = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_traceNo);//trace no.
        TextView tv_confirm_status = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_status);//status เช่น Success
        TextView tv_confirm_terminalId = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_terminalId);//Terminal ID
        TextView tv_confirm_apprCode = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_apprCode);//appr code
        TextView tv_confirm_merchantId = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_merchantId);//merchant Id
        TextView tv_confirm_BatchNo = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_BatchNo);//Batch No
        TextView tv_confirm_comCode = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_comCode);//com Code
        TextView tv_confirm_referenceNo = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_referenceNo);//referenceNo

        LinearLayout linear_comCode = dialogReprintConfirmSlip_sale.findViewById(R.id.linear_comCode);//ถ้าไม่มี จะให้ INVISIBLE

        ////
        //SINN 20180911 reprint any set void font
        //tv_confirm_numberPrice.setText(TransTempDB.getAmount());
        TextView tv_confirm_unit = dialogReprintConfirmSlip_sale.findViewById(R.id.tv_confirm_unit);
        if (TransTempDB.getVoidFlag().equals("N")) {
//            tv_confirm_numberPrice.setText(TransTempDB.getAmount());
            tv_confirm_numberPrice.setText(decFormatDisplay.format(Double.valueOf(TransTempDB.getAmount().replaceAll(",", ""))));     //GAME 20181026

            tv_confirm_numberPrice.setTextColor(Color.GREEN);
            tv_confirm_unit.setTextColor(Color.GREEN);
        } else {
//            tv_confirm_numberPrice.setText("-" + TransTempDB.getAmount());
            tv_confirm_numberPrice.setText("-" + decFormatDisplay.format(Double.valueOf(TransTempDB.getAmount().replaceAll(",", ""))));   ////GAME 20181026            tv_confirm_numberPrice.setTextColor(Color.RED);
            tv_confirm_numberPrice.setTextColor(Color.RED);
            tv_confirm_unit.setTextColor(Color.RED);
        }

        if (TransTempDB.getHostTypeCard().equals("TMS") || TransTempDB.getHostTypeCard().equals("EPS") || TransTempDB.getHostTypeCard().equals("POS"))
            msgLabel.setText("บัตรเครดิต/เดบิต");       // Paul_20190205
        tv_confirm_idcard.setText(CardPrefix.maskviewcard(" ", TransTempDB.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        tv_confirm_username.setText(TransTempDB.getEmvNameCardHolder());
        //K.GAME 181022 Date
        String date1 = TransTempDB.getTransDate().replaceAll("/", "");
        String date2 = date1.substring(6, 8)
                + "/"
                + date1.substring(4, 6)
                + "/"
                + date1.substring(0, 4);
        tv_confirm_date.setText(date2);
        //END K.GAME 181022 Date
        tv_confirm_time.setText(TransTempDB.getTransTime());
//        tv_confirm_traceNo.setText(TransTempDB.getTraceNo());
        tv_confirm_traceNo.setText(TransTempDB.getEcr());//K.GAME 181024

        //K.GAME 181022
        if (TransTempDB.getVoidFlag().equals("Y")) {
            tv_confirm_status.setText("VOID");
        } else {
            tv_confirm_status.setText("SUCCESS");
        }//K.GAME 181022

        tv_confirm_terminalId.setText(TransTempDB.getTid());
        tv_confirm_apprCode.setText(TransTempDB.getApprvCode());
        tv_confirm_merchantId.setText(TransTempDB.getMid());
//        tv_confirm_BatchNo.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_BATCH_GHC));

//        batchLabelSettle.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_TMS));

        Log.d(TAG, "getHostTypeCard :" + TransTempDB.getHostTypeCard());
        Log.d(TAG, "KEY_SETTLE_BATCH_POS :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        Log.d(TAG, "KEY_SETTLE_BATCH_EPS :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        Log.d(TAG, "KEY_SETTLE_BATCH_TMS :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
        Log.d(TAG, "KEY_SETTLE_BATCH_GHC :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (TransTempDB.getHostTypeCard().equalsIgnoreCase("POS"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        else if (TransTempDB.getHostTypeCard().equalsIgnoreCase("EPS"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        else if (TransTempDB.getHostTypeCard().equalsIgnoreCase("TMS"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            //20180708 SINN Add healthcare print.
        else if (TransTempDB.getHostTypeCard().equalsIgnoreCase("GHC"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        //end //20180708 SINN Add healthcare print.


        //SINN
        if (TransTempDB.getComCode().equals("")) {
            linear_comCode.setVisibility(View.INVISIBLE);
        } else {
            tv_confirm_comCode.setText(TransTempDB.getComCode());
        }
        tv_confirm_referenceNo.setText(TransTempDB.getRefNo());

        typeHost = TransTempDB.getHostTypeCard();

        final TransTemp TransTempDB1 = TransTempDB;
        inSetPrintType = 1;


        if (Preference.getInstance(this).getValueString(Preference.KEY_PrintSlip_ID).substring(0, 1).equalsIgnoreCase("0")) {
            btn_confirm_reprint2.setEnabled(false); //ร้านค้า
            btn_confirm_reprint2.setBackgroundColor(getResources().getColor(R.color.color_gray));
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_PrintSlip_ID).substring(1, 2).equalsIgnoreCase("0")) {
            btn_confirm_reprint.setEnabled(false); //ลูกค้า
            btn_confirm_reprint.setBackgroundColor(getResources().getColor(R.color.color_gray));
        }


        //ลูกค้า
        btn_confirm_reprint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//           typeHost = TransTempDB1.getHostTypeCard();
                isStatusPrintLastSlip = false;
                if (typeHost.equals("GHC") && TransTempDB1.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(TransTempDB1);

                } else {
                    System.out.printf("utility:: QQQQQQQQQQQQQQQQQ customDialogReprintConfirmSlip 000000000001 \n");
                    inSetPrintType = 2;
                    setDataCustomer();
                    //doPrinting(getBitmapFromView(slipLinearLayout_C));
                }
            }
        });


        //K.GAME 181022 back button
        dialogReprintConfirmSlip_sale.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                finish();
                dialogReprintConfirmSlip_sale.dismiss();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 181022 back button

        //ร้านค้า
        btn_confirm_reprint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeHost.equals("GHC") && TransTempDB1.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(TransTempDB1);

                } else {
                    inSetPrintType = 1;
                    setDataMerchant();
                    //doPrinting(getBitmapFromView(slipLinearLayout));
                }
            }
        });

        dialogReprintConfirmSlip_sale.show();
    }

    private void setDataMerchant() {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            setDataSlipSale(transTemp);
            return;
        }

        dialogLoading_msg.setText("กำลังพิมพ์สำเนาสำหรับร้านค้า");   ////20181107 SINN Dialog print
        dialogLoading.show();
        DecimalFormat decimalFormatShow = new DecimalFormat("#,###,##0.00");    // Paul_20190118
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText(transTemp.getTid());
        midLabel.setText(transTemp.getMid());
        traceLabel.setText(transTemp.getEcr());
        systrcLabel.setText(transTemp.getTraceNo());
        if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        refNoLabel.setText(transTemp.getRefNo());
        String date = transTemp.getTransDate().substring(6, 8);
        String mount = transTemp.getTransDate().substring(4, 6);
        String year = transTemp.getTransDate().substring(2, 4);
        dateLabel.setText(date + "/" + mount + "/" + year);
        timeLabel.setText(transTemp.getTransTime());

        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
        cardNoLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel.setText(transTemp.getApprvCode());
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        if (transTemp.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabel.setText("C");
        } else if (transTemp.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabel.setText("W");
            sigatureLabel.setVisibility(View.GONE);
        } else {
            typeInputCardLabel.setText("S");
        }
        if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
            if (transTemp.getFee() != null) {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
            } else {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
            }
            typeLabel.setText("SALE");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel.setText(transTemp.getTaxAbb());
                traceTaxLabel.setText(transTemp.getEcr());

                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));

                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);

                timeTaxLabel.setText(transTemp.getTransTime());
                copyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null) {
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                        nameEmvCardLabel.setText("");
                    } else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee))));    // Paul_20190128
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
                if (transTemp.getEmvAppLabel() != null) {
                    if (!transTemp.getEmvAppLabel().isEmpty()) {
                        appLabel.setText(transTemp.getEmvAppLabel());
                    } else {
                        // appFrameLabel.setVisibility(View.GONE);
                    }
                } else {
                    // appFrameLabel.setVisibility(View.GONE);
                }

                if (transTemp.getEmvTc() != null) {
                    if (!transTemp.getEmvTc().isEmpty()) {
                        tcLabel.setText(transTemp.getEmvTc());
                    } else {
                        tcFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    tcFrameLayout.setVisibility(View.GONE);
                }
                if (transTemp.getEmvAid() != null) {
                    if (!transTemp.getEmvAid().isEmpty()) {
                        aidLabel.setText(transTemp.getEmvAid());
                    } else {
                        aidFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    aidFrameLayout.setVisibility(View.GONE);
                }

//                if (transTemp.getTransType().equalsIgnoreCase("W")){
//                    sigatureLabel.setVisibility(View.GONE);
//                }else {
//                    sigatureLabel.setVisibility(View.VISIBLE);
//                }
            } else {
                comCodeFragment.setVisibility(View.VISIBLE);
                copyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                    nameEmvCardLabel.setText("");
                } else {
                    nameEmvCardLabel.setText(__name.trim());
                }

                // appFrameLabel.setVisibility(View.GONE);
                tcFrameLayout.setVisibility(View.GONE);
                aidFrameLayout.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);

                taxLinearLayout.setVisibility(View.GONE);
                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
            }
        } else {
//            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            if (transTemp.getFee() != null) {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
            } else {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            }
            typeLabel.setText("VOID");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel.setText(transTemp.getTaxAbb());
                traceTaxLabel.setText(transTemp.getEcr());
                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);
//                dateTaxLabel.setText(transTemp.getTransDate());
                timeTaxLabel.setText(transTemp.getTransTime());
//                feeTaxLabel.setText(transTemp.getFee());
                copyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null) {
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                        nameEmvCardLabel.setText("");
                    } else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));    // Paul_20190128 (float) delete
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
                if (transTemp.getEmvAppLabel() != null) {
                    if (!transTemp.getEmvAppLabel().isEmpty()) {
                        appLabel.setText(transTemp.getEmvAppLabel());
                    } else {
                        // appFrameLabel.setVisibility(View.GONE);
                    }
                } else {
                    // appFrameLabel.setVisibility(View.GONE);
                }

                if (transTemp.getEmvTc() != null) {
                    if (!transTemp.getEmvTc().isEmpty()) {
                        tcLabel.setText(transTemp.getEmvTc());
                    } else {
                        tcFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    tcFrameLayout.setVisibility(View.GONE);
                }
                if (transTemp.getEmvAid() != null) {
                    if (!transTemp.getEmvAid().isEmpty()) {
                        aidLabel.setText(transTemp.getEmvAid());
                    } else {
                        aidFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    aidFrameLayout.setVisibility(View.GONE);
                }
            } else {
                comCodeFragment.setVisibility(View.VISIBLE);
                taxLinearLayout.setVisibility(View.GONE);

                // appFrameLabel.setVisibility(View.GONE);
                tcFrameLayout.setVisibility(View.GONE);
                aidFrameLayout.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);

                copyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                    nameEmvCardLabel.setText("");
                } else {
                    nameEmvCardLabel.setText(__name.trim());
                }

                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }

        //SINN 20181031 merge KTBNORMAL again.
        if (Preference.getInstance(this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1")) {
            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);

        }
        // Paul_20190312 VOID No TAX Slip
        if (transTemp.getVoidFlag().equalsIgnoreCase("Y")) {
            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);
        }

        String valueParameterEnable = Preference.getInstance(this).getValueString(Preference.KEY_TAG_1000);
        if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
            comCodeLabel.setVisibility(View.GONE);
            comCodeLabel.setText(transTemp.getComCode());
        }
        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
            ref1RelativeLayout.setVisibility(View.GONE);
            ref1Label.setText(transTemp.getRef1());
        }
        if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
            ref2RelativeLayout.setVisibility(View.GONE);
            ref2Label.setText(transTemp.getRef2());
        }
        if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(transTemp.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(transTemp.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
            ref3RelativeLayout.setVisibility(View.GONE);
            ref3Label.setText(transTemp.getRef3());
        }

        name_sw_version.setText(BuildConfig.VERSION_NAME);      // Paul_20190125 software version print
//        sigatureLabel.setVisibility(View.VISIBLE);  //SINN 20181213 fixed swaping print customer /merchant

//        if (transTemp.getTransType().equalsIgnoreCase("W")) {
//            sigatureLabel.setVisibility(View.GONE);
//        }else {
//            sigatureLabel.setVisibility(View.VISIBLE);
//        }

        printLastView_merchant.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printLastView_merchant.layout(0, 0, printLastView_merchant.getMeasuredWidth(), printLastView_merchant.getMeasuredHeight());
    }

    private void setDataCustomer() {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            Log.d("SINN", "setPrintLastSearch");
            setDataSlipSale(transTemp);
            return;
        }

        dialogLoading_msg.setText("กำลังพิมพ์สำเนาสำหรับร้านค้า");   ////20181107 SINN Dialog print
        dialogLoading.show();
        DecimalFormat decimalFormatShow = new DecimalFormat("#,###,##0.00");    // Paul_20190118
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel_C.setText("TID:" + transTemp.getTid());
        midLabel_C.setText("MID:" + transTemp.getMid());
        traceLabel_C.setText("TRACE:" + transTemp.getEcr());
        if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
            batchLabel_C.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
            batchLabel_C.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
            batchLabel_C.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC"))
            batchLabel_C.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        refNoLabel_C.setText("REF NO:" + transTemp.getRefNo());
        String date = transTemp.getTransDate().substring(6, 8);
        String mount = transTemp.getTransDate().substring(4, 6);
        String year = transTemp.getTransDate().substring(2, 4);
        dateLabel_C.setText(date + "/" + mount + "/" + year);
        timeLabel_C.setText(transTemp.getTransTime());
        typeCardLabel_C.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
        cardNoLabel_C.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel_C.setText("APPR CODE:" + transTemp.getApprvCode());
        System.out.printf("utility:: HHHHHHHHHHHHHH 0003 apprCodeLabel = %s \n", apprCodeLabel.getText());
        String typeVoidOrSale = "";
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label_C.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label_C.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label_C.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        if (typeHost.equalsIgnoreCase("POS")) {
            typeVoidOrSale = Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TYPE_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            typeVoidOrSale = Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TYPE_EPS);
        } else {
            typeVoidOrSale = Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TYPE_TMS);
        }
        if (transTemp.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabel_C.setText("C");
        } else if (transTemp.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabel_C.setText("W");
            sigatureLabel.setVisibility(View.GONE);
        } else {
            typeInputCardLabel_C.setText("S");
        }
        if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
            if (transTemp.getFee() != null) {
                feeTaxLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
            } else {
                feeTaxLabel_C.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
            }
            typeLabel_C.setText("SALE");
            amtThbLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel_C.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel_C.setText(transTemp.getTaxAbb());
                traceTaxLabel_C.setText(transTemp.getEcr());

                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel_C.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel_C.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel_C.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));

                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel_C.setText(dateTax + "/" + mountTax + "/" + yearTax);

                timeTaxLabel_C.setText(transTemp.getTransTime());
                copyLabel_C.setText("***** CUSTOMER COPY *****");
                if (transTemp.getEmvNameCardHolder() != null) {
//                    nameEmvCardLabel_C.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                        nameEmvCardLabel_C.setText("");
                    } else {
                        nameEmvCardLabel_C.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
//                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee))));    // Paul_20190128
                } else {
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
            } else {
                comCodeFragment_C.setVisibility(View.VISIBLE);
                copyLabel_C.setText("***** CUSTOMER COPY *****");
//                nameEmvCardLabel_C.setText(transTemp.getEmvNameCardHolder().trim());
                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                    nameEmvCardLabel_C.setText("");
                } else {
                    nameEmvCardLabel_C.setText(__name.trim());
                }
                taxLinearLayout_C.setVisibility(View.GONE);

                taxLinearLayout_C.setVisibility(View.GONE);
                if (transTemp.getEmciFree() != null) {
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
            }
        } else {
            if (transTemp.getFee() != null)
                feeTaxLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
            else
                feeTaxLabel_C.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));

            typeLabel_C.setText("VOID");
            amtThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment_C.setVisibility(View.GONE);
                taxIdLabel_C.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel_C.setText(transTemp.getTaxAbb());
                traceTaxLabel_C.setText(transTemp.getEcr());
                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel_C.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel_C.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel_C.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel_C.setText(dateTax + "/" + mountTax + "/" + yearTax);
                timeTaxLabel_C.setText(transTemp.getTransTime());
                copyLabel_C.setText("***** CUSTOMER COPY *****");
                if (transTemp.getEmvNameCardHolder() != null) {
//                    nameEmvCardLabel_C.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                        nameEmvCardLabel_C.setText("");
                    } else {
                        nameEmvCardLabel_C.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
//                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));    // Paul_20190128 (float) delete
                } else {
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            } else {
                comCodeFragment_C.setVisibility(View.VISIBLE);
                taxLinearLayout_C.setVisibility(View.GONE);
                taxLinearLayout_C.setVisibility(View.GONE);

                copyLabel_C.setText("***** CUSTOMER COPY *****");
//                nameEmvCardLabel_C.setText(transTemp.getEmvNameCardHolder().trim());
                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                    nameEmvCardLabel_C.setText("");
                } else {
                    nameEmvCardLabel_C.setText(__name.trim());
                }

                if (transTemp.getEmciFree() != null) {
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }

        if (Preference.getInstance(this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1")) {
            fee_thb_C.setVisibility(View.GONE);
            tot_thb_C.setVisibility(View.GONE);
            taxLinearLayout_C.setVisibility(View.GONE);

        }
        // Paul_20190312 VOID No TAX Slip
        if (transTemp.getVoidFlag().equalsIgnoreCase("Y")) {
            fee_thb_C.setVisibility(View.GONE);
            tot_thb_C.setVisibility(View.GONE);
            taxLinearLayout_C.setVisibility(View.GONE);
        }

        String valueParameterEnable = Preference.getInstance(this).getValueString(Preference.KEY_TAG_1000);
        if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
            comCodeLabel_C.setVisibility(View.VISIBLE);
            comCodeLabel_C.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
            comCodeLabel_C.setVisibility(View.VISIBLE);
            comCodeLabel_C.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
            comCodeLabel_C.setVisibility(View.GONE);
            comCodeLabel_C.setText(transTemp.getComCode());
        }
        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
            ref1RelativeLayout_C.setVisibility(View.VISIBLE);
            ref1Label_C.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
            ref1RelativeLayout_C.setVisibility(View.VISIBLE);
            ref1Label_C.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
            ref1RelativeLayout_C.setVisibility(View.GONE);
            ref1Label_C.setText(transTemp.getRef1());
        }
        if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
            ref2RelativeLayout_C.setVisibility(View.VISIBLE);
            ref2Label_C.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
            ref2RelativeLayout_C.setVisibility(View.VISIBLE);
            ref2Label_C.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
            ref2RelativeLayout_C.setVisibility(View.GONE);
            ref2Label_C.setText(transTemp.getRef2());
        }
        if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
            ref3RelativeLayout_C.setVisibility(View.VISIBLE);
            ref3Label_C.setText(transTemp.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
            ref3RelativeLayout_C.setVisibility(View.VISIBLE);
            ref3Label_C.setText(transTemp.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
            ref3RelativeLayout_C.setVisibility(View.GONE);
            ref3Label_C.setText(transTemp.getRef3());
        }
        name_sw_version_C.setText(BuildConfig.VERSION_NAME);

        printLastView_customer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printLastView_customer.layout(0, 0, printLastView_customer.getMeasuredWidth(), printLastView_customer.getMeasuredHeight());
    }

    private void customDialogReprintConfirmSlip(TransTemp TransTempDB) { //K.GAME 180903 new dialog
        final Dialog dialogReprintConfirmSlip = new Dialog(this);
        dialogReprintConfirmSlip.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReprintConfirmSlip.setContentView(R.layout.dialog_custom_reprint_confirm2);
        dialogReprintConfirmSlip.setCancelable(false);
        dialogReprintConfirmSlip.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReprintConfirmSlip.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ////
        Button btn_confirm_reprint = dialogReprintConfirmSlip.findViewById(R.id.btn_confirm_reprint);
        Button btn_confirm_reprint2 = dialogReprintConfirmSlip.findViewById(R.id.btn_confirm_reprint2);
        TextView msgLabel = dialogReprintConfirmSlip.findViewById(R.id.msgLabel);
        TextView tvLabelName = dialogReprintConfirmSlip.findViewById(R.id.tvLabelName);
        TextView tv_confirm_numberPrice = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_numberPrice);//ยืนยันราคา
        TextView tv_confirm_idcard = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_idcard);//ยืนยัน id card
        TextView tv_confirm_username = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_username);//usrname
        TextView tv_confirm_date = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_date);//date
        TextView tv_confirm_time = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_time);//time
        TextView tv_confirm_traceNo = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_traceNo);//trace no.
        TextView tv_confirm_status = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_status);//status เช่น Success
        TextView tv_confirm_terminalId = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_terminalId);//Terminal ID
        TextView tv_confirm_apprCode = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_apprCode);//appr code
        TextView tv_confirm_merchantId = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_merchantId);//merchant Id
        TextView tv_confirm_BatchNo = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_BatchNo);//Batch No
        TextView tv_confirm_comCode = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_comCode);//com Code

        LinearLayout linear_comCode = dialogReprintConfirmSlip.findViewById(R.id.linear_comCode);

        ////
        //SINN 20180911 reprint any set void font
        //tv_confirm_numberPrice.setText(TransTempDB.getAmount());
        TextView tv_confirm_unit = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_unit);
        if (TransTempDB.getVoidFlag().equals("N")) {
            tv_confirm_numberPrice.setText(TransTempDB.getAmount());
            tv_confirm_numberPrice.setTextColor(Color.GREEN);
            tv_confirm_unit.setTextColor(Color.GREEN);
        } else {
            tv_confirm_numberPrice.setText("-" + TransTempDB.getAmount());
            tv_confirm_numberPrice.setTextColor(Color.RED);
            tv_confirm_unit.setTextColor(Color.RED);
        }

        if (TransTempDB.getHostTypeCard().equals("TMS") || TransTempDB.getHostTypeCard().equals("EPS") || TransTempDB.getHostTypeCard().equals("POS"))
            msgLabel.setText("บัตรเครดิต/เดบิต");       // Paul_20190205
//            msgLabel.setText("บัตรเดบิต/เครดิต");       // Paul_20190201
//          msgLabel.setText("Sale");       //K.GAME 181024

//        tv_confirm_idcard.setText(TransTempDB.getCardNo());
//        tv_confirm_idcard.setText(CardPrefix.maskcard(Preference.getInstance(this).getValueString(Preference.KEY_CARDMASK_ID).toString(), TransTempDB.getCardNo()));
        tv_confirm_idcard.setText(CardPrefix.maskviewcard(" ", TransTempDB.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555

//        tv_confirm_username.setText(TransTempDB.getThName());
        tv_confirm_username.setText(TransTempDB.getEmvNameCardHolder());
        //K.GAME 181022 Date
        String date1 = TransTempDB.getTransDate().replaceAll("/", "");
        String date2 = date1.substring(6, 8)
                + "/"
                + date1.substring(4, 6)
                + "/"
                + date1.substring(0, 4);
        tv_confirm_date.setText(date2);
        //END K.GAME 181022 Date
        tv_confirm_time.setText(TransTempDB.getTransTime());
        tv_confirm_traceNo.setText(TransTempDB.getTraceNo());

        //K.GAME 181022
        if (TransTempDB.getVoidFlag().equals("Y")) {
            tv_confirm_status.setText("Void");
        } else {
            tv_confirm_status.setText("Success");
        }//K.GAME 181022

        tv_confirm_terminalId.setText(TransTempDB.getTid());
        tv_confirm_apprCode.setText(TransTempDB.getApprvCode());
        tv_confirm_merchantId.setText(TransTempDB.getMid());
//        tv_confirm_BatchNo.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_BATCH_GHC));

//        batchLabelSettle.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_TMS));

        Log.d(TAG, "getHostTypeCard :" + TransTempDB.getHostTypeCard());
        Log.d(TAG, "KEY_SETTLE_BATCH_POS :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        Log.d(TAG, "KEY_SETTLE_BATCH_EPS :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        Log.d(TAG, "KEY_SETTLE_BATCH_TMS :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
        Log.d(TAG, "KEY_SETTLE_BATCH_GHC :" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (TransTempDB.getHostTypeCard().equalsIgnoreCase("POS"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        else if (TransTempDB.getHostTypeCard().equalsIgnoreCase("EPS"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        else if (TransTempDB.getHostTypeCard().equalsIgnoreCase("TMS"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            //20180708 SINN Add healthcare print.
        else if (TransTempDB.getHostTypeCard().equalsIgnoreCase("GHC"))
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        //end //20180708 SINN Add healthcare print.


        //SINN
        if (TransTempDB.getComCode().equals("")) {
            linear_comCode.setVisibility(View.INVISIBLE);
        } else {
            tv_confirm_comCode.setText(TransTempDB.getComCode());
        }

        typeHost = TransTempDB.getHostTypeCard();

        final TransTemp TransTempDB1 = TransTempDB;
        inSetPrintType = 1;

        //ลูกค้า
        btn_confirm_reprint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//           typeHost = TransTempDB1.getHostTypeCard();
                isStatusPrintLastSlip = false;
                if (typeHost.equals("GHC") && TransTempDB1.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(TransTempDB1);

                } else {
//            inSetPrintType=1;
                    System.out.printf("utility:: QQQQQQQQQQQQQQQQQ customDialogReprintConfirmSlip 000000000001 \n");
                    inSetPrintType = 2;
                    setPrintLastSearch(TransTempDB1);
                    //doPrinting(getBitmapFromView(slipLinearLayout));
                }


            }
        });


        //K.GAME 181022 back button
        dialogReprintConfirmSlip.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                finish();
                dialogReprintConfirmSlip.dismiss();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 181022 back button

        //ร้านค้า
        btn_confirm_reprint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ReprintAnyActivity.this, "พิมพ์ซ้ำ", Toast.LENGTH_SHORT).show();
                //K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้ สั่งปริ้น ต้องหาค่า trace มาหยอด
//                Toast.makeText(ReprintAnyActivity.this, " Trace = " + transTemp.getTraceNo(), Toast.LENGTH_SHORT).show();
                // transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", transTemp.getTraceNo() + invoiceEt.getText().toString()).findFirst();
//
//                if (typeHost.equals("GHC")) {
//                    System.out.printf( "utility:: %s doPrinting Befor 037 \n", TAG );
//                    doPrinting( getBitmapFromView( slip_sale_void_hgc_re ) );
//                }
//                else {
////                    if (isStatusPrintLastSlip) {
//                        isStatusPrintLastSlip = true;
//                        System.out.printf("utility:: %s doPrinting Befor 038 \n",TAG);
//
//                       // //doPrinting(getBitmapFromView(slipLinearLayout));
////                    }
//                }

                //END K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้

                if (typeHost.equals("GHC") && TransTempDB1.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(TransTempDB1);

                } else {
//                    inSetPrintType=2;
                    inSetPrintType = 1;
                    setPrintLastSearch(TransTempDB1);
//                    //doPrinting(getBitmapFromView(slipLinearLayout));
                }


            }
        });


//
//        btn_confirm_reprint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ReprintAnyActivity.this, "พิมพ์ซ้ำ", Toast.LENGTH_SHORT).show();
//                //K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้ สั่งปริ้น ต้องหาค่า trace มาหยอด
////                Toast.makeText(ReprintAnyActivity.this, " Trace = " + transTemp.getTraceNo(), Toast.LENGTH_SHORT).show();
//                // transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", transTemp.getTraceNo() + invoiceEt.getText().toString()).findFirst();
//                if (TransTempDB != null) {
//                    if (typeHost.equals("GHC") && TransTempDB.getGhcoffFlg().equalsIgnoreCase("Y")) {
//                        setPrint_off(TransTempDB);
//                        dialogReprintConfirmSlip.dismiss();
//                    } else {
//                        setPrintLastSearch(TransTempDB);
//                        dialogReprintConfirmSlip.dismiss();
//                    }
//                } else {
//                    Utility.customDialogAlert(ReprintAnyActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                            dialogReprintConfirmSlip.dismiss();
//                        }
//                    });
//                }
//                //END K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้
//            }
//        });
        dialogReprintConfirmSlip.show();
    }

//    private void searchQrDB() {
//        trace = invoiceEt.getText().toString();
//        trace = checkLength(trace, 6);
//
//        //DB DATA
//        if (realm == null) {
//            realm = Realm.getDefaultInstance();
//        }
//        RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("trace", trace).findAll();
//
//        if(saleTemp.size() > 0){
//            Intent service = new Intent(ReprintAnyActivity2.this, AliVoidActivity.class);
//            service.putExtra("INVOICE", trace);
//            service.putExtra("TYPE", AliConfig.Inquiry);
//            startActivity(service);
//            finish();
//        }else{
//            Utility.customDialogAlert(ReprintAnyActivity2.this, "ไม่พบรายการ", new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            });
//        }
//    }

    private String checkLength(String trace, int i) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for (int j = 0; j < (i - tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVoidList();
        if (realm == null) {
            realm = Realm.getDefaultInstance();       // Paul_20180809
        }
    }


    @Override
    public void initBtnExit() {
        super.initBtnExit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (realm != null) {
            realm.close();
            realm = null;   // Paul_20181026 Some time DB Read error solved
        }
    }
}