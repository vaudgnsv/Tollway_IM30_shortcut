package org.centerm.Tollway.activity;

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
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import org.centerm.Tollway.activity.qr.ReprintQrActivity;
import org.centerm.Tollway.adapter.ReprintAdapter;
import org.centerm.Tollway.alipay.AliConfig;
import org.centerm.Tollway.alipay.AliReprintActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
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
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

//import org.centerm.Tollway.healthcare.database.SaleOfflineHealthCare;        // Paul_20180724_OFF

public class ReprintActivity extends SettingToolbarActivity {


    private RecyclerView menuRecyclerView = null;
    private ReprintAdapter reprintAdapter = null;
    private List<String> nameList = null;

    /***
     * SALE AND VOID
     */

    private View printLastView_customer;
    private View printLastView_merchant;

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
    private TextView name_sw_version;

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
    private TextView sigatureLabel_C = null;
    private TextView appLabel_C;
    private FrameLayout appFrameLabel_C;
    private TextView tcLabel_C;
    private FrameLayout tcFrameLayout_C;
    private TextView aidLabel_C;
    private FrameLayout aidFrameLayout_C;
    private TextView name_sw_version_C;

    private FrameLayout fee_thb;
    private FrameLayout tot_thb;
    private FrameLayout fee_thb_C;
    private FrameLayout tot_thb_C;

    /**
     * SETTLEMENT
     */
    private NestedScrollView slipNestedScrollViewSettle = null;
    private LinearLayout settlementLinearLayoutSettle;
    private TextView dateLabelSettle = null;
    private TextView timeLabelSettle = null;
    private TextView midLabelSettle = null;
    private TextView tidLabelSettle = null;
    private TextView batchLabelSettle = null;
    private TextView hostLabelSettle = null;
    private TextView saleCountLabelSettle = null;
    private TextView saleTotalLabelSettle = null;
    private TextView voidSaleCountLabelSettle = null;
    private TextView voidSaleAmountLabelSettle = null;
    private TextView cardCountLabelSettle = null;
    private TextView cardAmountLabelSettle = null;
    private TextView merchantName1LabelSettle = null;
    private TextView merchantName2LabelSettle = null;
    private TextView merchantName3LabelSettle = null;
    private ImageView bank1ImageSettle = null;
    private ImageView bankImageSettle = null;

    private EditText invoiceEt = null;
    private ImageView searchInvoiceImage = null;

    private Realm realm = null;
//    private View printLastView;
    private CardManager cardManager = null;
    private AidlPrinter printDev = null;
//    private Dialog dialogSearch;
    private Dialog dialogHost;
    private Button posBtn;
    private Button epsBtn;
    private Button tmsBtn;

    private Button ghcBtn; ////SINN 20180706 Add void print
    private Button aliBtn; //20181116Jeff
    private Button wechatBtn; //20181116Jeff

    //    private ImageView closeImage; //K.GAME 180828 change dialog UI
    private Button closeImage; //K.GAME 180828 change dialog UI
    private String typeClick;

    private String typeHost;
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
    //    private ImageView closeQrImage;// K.GAME 180828 change dialog UI
    private Button closeQrImage;// K.GAME 180828 change dialog UI

    private TextView taxIdLabel;
    private TextView taxAbbLabel;
    private TextView traceTaxLabel;
    private TextView batchTaxLabel;
    private TextView dateTaxLabel;
    private TextView timeTaxLabel;
    private TextView feeTaxLabel;
    private TextView copyLabel;
//    private TextView typeCopyLabel;
    private TextView nameEmvCardLabel;
    private LinearLayout taxLinearLayout;
    private TextView taxIdLabel_C;
    private TextView taxAbbLabel_C;
    private TextView traceTaxLabel_C;
    private TextView batchTaxLabel_C;
    private TextView dateTaxLabel_C;
    private TextView timeTaxLabel_C;
    private TextView feeTaxLabel_C;
    private TextView copyLabel_C;
//    private TextView typeCopyLabel_C;
    private TextView nameEmvCardLabel_C;
    private LinearLayout taxLinearLayout_C;

    private boolean isStatusPrintLastSlip = false;
    private TextView typeInputCardLabel;
    private TextView typeInputCardLabel_C;
    private final String TAG = "ReprintActivity";
    private Dialog dialogLoading;
    private FrameLayout comCodeFragment;
    private FrameLayout comCodeFragment_C;
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

//20180708 SINN Add healthcare print.
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

    private Dialog dialogSuccess_GotoMain;  ////20181218  SINN Print slip enable/disable
    private Button btn_gotoMain;////20181218  SINN Print slip enable/disable


    private ImageView img_krungthai1;//K.GAME 181016
    private ImageView img_krungthai2;//K.GAME 181016
    private TextView app_title;//K.GAME 181016
    private String str_app_title = "พิมพ์สำเนาสลิป";//K.GAME 181016
    /**
     * Interface
     */
    public static final String KEY_INTERFACE_INV = ReprintActivity.class.getName() + "_key_invoice_number";  //SINN 20180706 Add QR print.
    public static final String KEY_INTERFACE_REPRINT_TYPE = ReprintActivity.class.getName() + "_key_reprint_type";
    String invoiceId = null;  //SINN 20180706 Add QR print.
    String inReprintType = null;
    String type;
    String amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.printf("utility:: %s onCreate \n",TAG);
        setContentView(R.layout.activity_reprint);
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        initWidget();
//        initBtnExit(); K.GAME 180824 change UI
        customDialogLoading();
        customDialogOutOfPaper();
        setViewReprint_Customer();
        setViewReprint_Merchant();
//        LayoutInflater inflater =
//                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        printLastView = inflater.inflate(R.layout.view_sale_void, null);
//        merchantName1Label = printLastView.findViewById(R.id.merchantName1Label);
//        merchantName2Label = printLastView.findViewById(R.id.merchantName2Label);
//        merchantName3Label = printLastView.findViewById(R.id.merchantName3Label);
//        slipLinearLayout = printLastView.findViewById(R.id.slipLinearLayout);
//        tidLabel = printLastView.findViewById(R.id.tidLabel);
//        midLabel = printLastView.findViewById(R.id.midLabel);
//        traceLabel = printLastView.findViewById(R.id.traceLabel);
//        systrcLabel = printLastView.findViewById(R.id.systrcLabel);
//        batchLabel = printLastView.findViewById(R.id.batchLabel);
//        refNoLabel = printLastView.findViewById(R.id.refNoLabel);
//        dateLabel = printLastView.findViewById(R.id.dateLabel);
//        timeLabel = printLastView.findViewById(R.id.timeLabel);
//        typeLabel = printLastView.findViewById(R.id.typeLabel);
//        typeCardLabel = printLastView.findViewById(R.id.typeCardLabel);
//        cardNoLabel = printLastView.findViewById(R.id.cardNoLabel);
//        apprCodeLabel = printLastView.findViewById(R.id.apprCodeLabel);
//        comCodeLabel = printLastView.findViewById(R.id.comCodeLabel);
//        amtThbLabel = printLastView.findViewById(R.id.amtThbLabel);
//        feeThbLabel = printLastView.findViewById(R.id.feeThbLabel);
//        totThbLabel = printLastView.findViewById(R.id.totThbLabel);
//        ref1Label = printLastView.findViewById(R.id.ref1Label);
//        ref2Label = printLastView.findViewById(R.id.ref2Label);
//        ref3Label = printLastView.findViewById(R.id.ref3Label);
//        ref1RelativeLayout = printLastView.findViewById(R.id.ref1RelativeLayout);
//        ref2RelativeLayout = printLastView.findViewById(R.id.ref2RelativeLayout);
//        ref3RelativeLayout = printLastView.findViewById(R.id.ref3RelativeLayout);
//
//        taxIdLabel = printLastView.findViewById(R.id.taxIdLabel);
//        taxAbbLabel = printLastView.findViewById(R.id.taxAbbLabel);
//        traceTaxLabel = printLastView.findViewById(R.id.traceTaxLabel);
//        batchTaxLabel = printLastView.findViewById(R.id.batchTaxLabel);
//        dateTaxLabel = printLastView.findViewById(R.id.dateTaxLabel);
//        timeTaxLabel = printLastView.findViewById(R.id.timeTaxLabel);
//        feeTaxLabel = printLastView.findViewById(R.id.feeTaxLabel);
//        copyLabel = printLastView.findViewById(R.id.copyLabel);
//        typeCopyLabel = printLastView.findViewById(R.id.typeCopyLabel);
//        nameEmvCardLabel = printLastView.findViewById(R.id.nameEmvCardLabel);
//        taxLinearLayout = printLastView.findViewById(R.id.taxLinearLayout);
//        sigatureLabel = printLastView.findViewById(R.id.sigatureLabel);
//        typeInputCardLabel = printLastView.findViewById(R.id.typeInputCardLabel);
//        comCodeFragment = printLastView.findViewById(R.id.comCodeFragment);
//
//        appLabel = printLastView.findViewById(R.id.appLabel);
//        appFrameLabel = printLastView.findViewById(R.id.appFrameLabel);
//        tcLabel = printLastView.findViewById(R.id.tcLabel);
//        tcFrameLayout = printLastView.findViewById(R.id.tcFrameLayout);
//        aidLabel = printLastView.findViewById(R.id.aidLabel);
//        aidFrameLayout = printLastView.findViewById(R.id.aidFrameLayout);
//
//
////        fee_thb = printLastView.findViewById(R.id.fee_thb_reprint);    //SINN 20181031 merge KTBNORMAL again.
////        tot_thb = printLastView.findViewById(R.id.tot_thb_reprint);;     //SINN 20181031 merge KTBNORMAL again.
//
//        name_sw_version = printLastView.findViewById(R.id.name_sw_version);   // Paul_20190125 software version print

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

        fee_thb = printLastView_merchant.findViewById(R.id.fee_thb);
        tot_thb = printLastView_merchant.findViewById(R.id.tot_thb);

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

//    private void setMeasure() {
//        printLastView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        printLastView.layout(0, 0, printLastView.getMeasuredWidth(), printLastView.getMeasuredHeight());
//    }


    private void setMeasureSettle() {
        reportSettlementLast.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSettlementLast.layout(0, 0, reportSettlementLast.getMeasuredWidth(), reportSettlementLast.getMeasuredHeight());
    }

    @Override
    public void initWidget() {

        //K.GAME 181016 hard code
        app_title = findViewById(R.id.app_title);
        app_title.setText(str_app_title);//Title

        img_krungthai1 = findViewById(R.id.img_krungthai1);
        img_krungthai2 = findViewById(R.id.img_krungthai2);
        if (!Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {

            img_krungthai1.setVisibility(View.INVISIBLE);
            img_krungthai2.setVisibility(View.VISIBLE);
        }//END K.GAME 181016 hard code

//        super.initWidget();
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); //K.GAME 180824 change UI
        gridLayoutManager.setSpanCount(3);//K.GAME 180824 change UI
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);//K.GAME 180824 change UI
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);
        setMenuList();
//        customDialogSearch();
        customDialogHost();
        customDialogHostQr();
        setViewSettlementLast();

        setViewSaleHGC();  ////20180708 SINN Add healthcare print.///
        dialogSuccess_GotoMain();
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
                Intent intent = new Intent(ReprintActivity.this, MenuServiceListActivity.class);
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



    private void setMenuList() {
        if (menuRecyclerView.getAdapter() == null) {
            reprintAdapter = new ReprintAdapter(this);
            menuRecyclerView.setAdapter(reprintAdapter);
            reprintAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        typeClick = "slipLast";
//                        if (dialogHost != null)  // Paul_20180720
//                            dialogHost.show();

                        //SINN 20181119  AXA no need select host
//                        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1"))

                        if(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")||
                                ((Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)&&
                        (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)&&
                                        (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)&&
                                        (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)))

                        {
                            selectReportLast("EPS");
                        }else{
                            if (dialogHost != null)  // Paul_20180720
                            dialogHost.show();
                        }
                        //END SINN 20181119  AXA no need select host

                    } else if (position == 1) {
                        typeClick = "slipPrevious";
//                        if (dialogHost != null) { // Paul_20180720
//                            dialogHost.show();//K.GAME 180907 แก้บัค
//                        }
                        //
//20181022 SINN Reprint no need dialog
                            //game 20181019 reprint new UI
                            Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity2.class);
                            invoiceId = "";
                            inReprintType = "2";
                            if (typeClick.equals("slipLast")) {
                                inReprintType = "1";
                                invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);
                            }

                            intent.putExtra(KEY_INTERFACE_INV, invoiceId);
                            intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, inReprintType);
                            startActivity(intent);
                            finish();
                            //END K.GAME 181017


                        //---------------------------------------------------------
                    } else if (position == 2) {
                        typeClick = "slipSettle";
//                        if (dialogHostQr != null)  // Paul_20180720
//                            dialogHostQr.show();
                        //SINN 20181119  AXA no need select host
//                        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1"))

                        if(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")||
                                ((Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)&&
                                        (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)&&
                                        (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)&&
                                        (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)))

                        {
                            typeHost = "EPS";
                            selectReportSettleLast();
                        }else {
                           if (dialogHostQr != null)  // Paul_20180720
                            dialogHostQr.show();
                        }

                    }
                }
            });
        } else {
            reprintAdapter.clear();
        }
        if (nameList == null) {
            nameList = new ArrayList<>();
        } else {
            nameList.clear();
        }
        nameList.add("พิมพ์ใบเสร็จล่าสุด");
//        nameList.add("พิมพ์ใบเสร็จย้อนหลัง");//K.GAME 181016
        nameList.add("เลือกรายการ\nที่จะพิมพ์ซ้ำ");
//        nameList.add("พิมพ์ใบสรุปยอดล่าสุด");//K.GAME 181016
        nameList.add("พิมพ์ซ้ำ\nยอดโอนล่าสุด");
        reprintAdapter.setData(nameList);
        reprintAdapter.notifyDataSetChanged();
    }

    private void setViewSettlementLast() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSettlementLast = inflater.inflate(R.layout.view_settlement_last, null);
        slipNestedScrollViewSettle = reportSettlementLast.findViewById(R.id.slipNestedScrollView);
        settlementLinearLayoutSettle = reportSettlementLast.findViewById(R.id.settlementLinearLayout);
        dateLabelSettle = reportSettlementLast.findViewById(R.id.dateLabel);
        timeLabelSettle = reportSettlementLast.findViewById(R.id.timeLabel);
        midLabelSettle = reportSettlementLast.findViewById(R.id.midLabel);
        tidLabelSettle = reportSettlementLast.findViewById(R.id.tidLabel);
        batchLabelSettle = reportSettlementLast.findViewById(R.id.batchLabel);
        hostLabelSettle = reportSettlementLast.findViewById(R.id.hostLabel);
        saleCountLabelSettle = reportSettlementLast.findViewById(R.id.saleCountLabel);
        saleTotalLabelSettle = reportSettlementLast.findViewById(R.id.saleTotalLabel);
        voidSaleCountLabelSettle = reportSettlementLast.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabelSettle = reportSettlementLast.findViewById(R.id.voidSaleAmountLabel);
        cardCountLabelSettle = reportSettlementLast.findViewById(R.id.cardCountLabel);
        cardAmountLabelSettle = reportSettlementLast.findViewById(R.id.cardAmountLabel);
        bank1ImageSettle = reportSettlementLast.findViewById(R.id.bank1Image);
        bankImageSettle = reportSettlementLast.findViewById(R.id.bankImage);

//        setContentView(R.layout.view_settlement_last); //K.GAME 180831 big change UI test

        if (Preference.getInstance(this).getValueString(Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
        {
            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
            bankImageSettle.setImageResource(id);
        }


        summaryLinearFeeLayout = reportSettlementLast.findViewById(R.id.summaryLinearLayout);
        merchantName1FeeLabel = reportSettlementLast.findViewById(R.id.merchantName1TaxLabel);
        merchantName2FeeLabel = reportSettlementLast.findViewById(R.id.merchantName2TaxLabel);
        merchantName3FeeLabel = reportSettlementLast.findViewById(R.id.merchantName3TaxLabel);
        dateFeeLabel = reportSettlementLast.findViewById(R.id.dateTaxLabel);
        timeFeeLabel = reportSettlementLast.findViewById(R.id.timeTaxLabel);
        batchFeeLabel = reportSettlementLast.findViewById(R.id.batchTaxLabel);
        hostFeeLabel = reportSettlementLast.findViewById(R.id.hostTaxLabel);
        saleCountFeeLabel = reportSettlementLast.findViewById(R.id.saleCountTaxLabel);
        saleTotalFeeLabel = reportSettlementLast.findViewById(R.id.saleTotalTaxLabel);
        voidSaleCountFeeLabel = reportSettlementLast.findViewById(R.id.voidSaleCountTaxLabel);
        voidSaleAmountFeeLabel = reportSettlementLast.findViewById(R.id.voidSaleAmountTaxLabel);
        cardCountFeeLabel = reportSettlementLast.findViewById(R.id.cardCountTaxLabel);
        cardAmountFeeLabel = reportSettlementLast.findViewById(R.id.cardAmountTaxLabel);
        taxIdFeeLabel = reportSettlementLast.findViewById(R.id.taxIdLabel);

        merchantName1LabelSettle = reportSettlementLast.findViewById(R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty()) {
            merchantName1LabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
            merchantName1FeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        }

        merchantName2LabelSettle = reportSettlementLast.findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty()) {
            merchantName2LabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));
            merchantName2FeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));
        }

        merchantName3LabelSettle = reportSettlementLast.findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty()) {
            merchantName3LabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));
            merchantName3FeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));
        }
    }

    private void setDataViewSettle() {
        dialogLoading.show();
        if (typeHost.equalsIgnoreCase("POS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_POS).isEmpty()) {
            summaryLinearFeeLayout.setVisibility(View.VISIBLE);
            hostLabelSettle.setText("KTB OFFUS");
            hostFeeLabel.setText("KTB OFFUS");
            batchLabelSettle.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_POS));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_POS));
//            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS)));
           //SINN 20181113 settlement amount sale no need *
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_POS));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_POS));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_POS));
            voidSaleAmountFeeLabel.setText(getString(R.string.slip_pattern_amount_void, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS)));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
//            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS)));

            batchFeeLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_POS));
            saleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_POS));
//            saleTotalFeeLabel.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            saleTotalFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS)));
            dateFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_POS));
            timeFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_POS));
            voidSaleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_POS));
            voidSaleAmountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_POS));
//            cardAmountFeeLabel.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS)));
            cardCountFeeLabel.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            taxIdFeeLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));

            //SINN 20181031 merge KTBNORMAL again.
            if(Preference.getInstance(this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))
            summaryLinearFeeLayout.setVisibility(View.GONE);

            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 027 \n",TAG);
//            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
            //doPrinting(Utility.SettlementReprintBitmapRead(typeHost));      // Paul_20181205 settlement reprint modify
        } else if (typeHost.equalsIgnoreCase("EPS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_EPS).isEmpty()) {
            summaryLinearFeeLayout.setVisibility(View.VISIBLE);
            hostLabelSettle.setText("WAY4");
            batchLabelSettle.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_EPS));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS));
//            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_EPS));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_EPS));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
//            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));

            batchFeeLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_EPS));
            saleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS));
//            saleTotalFeeLabel.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            saleTotalFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS)));
            dateFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_EPS));
            timeFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_EPS));
            voidSaleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS));
            voidSaleAmountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_EPS));
//            cardAmountFeeLabel.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS)));
            cardCountFeeLabel.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            taxIdFeeLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));

            //SINN 20181031 merge KTBNORMAL again.
            if(Preference.getInstance(this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))
                summaryLinearFeeLayout.setVisibility(View.GONE);

            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 028 \n",TAG);

//            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
            //doPrinting(Utility.SettlementReprintBitmapRead(typeHost));      // Paul_20181205 settlement reprint modify
        } else if (typeHost.equalsIgnoreCase("TMS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_TMS).isEmpty()) {
            hostLabelSettle.setText("KTB ONUS");
            batchLabelSettle.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_TMS));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS));
//            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_TMS));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_TMS));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
//            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS)));
            summaryLinearFeeLayout.setVisibility(View.GONE);
            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 029 \n",TAG);
//            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
            //doPrinting(Utility.SettlementReprintBitmapRead(typeHost));      // Paul_20181205 settlement reprint modify
            //20180720 SINN last reprint settlement GHC
        } else if (typeHost.equalsIgnoreCase("GHC") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_GHC).isEmpty()) {
            hostLabelSettle.setText("HEARTH CARE");
            batchLabelSettle.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_GHC));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_GHC));
//            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_GHC)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_GHC)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_GHC));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_GHC));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_GHC));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_GHC));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
//            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_GHC)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_GHC)));
            summaryLinearFeeLayout.setVisibility(View.GONE);
            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 030 \n",TAG);
            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
            //END 20180720 SINN last reprint settlement GHC
        } else if (typeHost.equalsIgnoreCase("QR") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_QR).isEmpty()) {
            hostLabelSettle.setText("KTB QR");
            batchLabelSettle.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_QR));      // Paul_20181120 please no remark last settlement reprint problem
//            batchLabelSettle.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID));  //SINN 20181119  TID QR
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_ID));  //SINN 20181119  TID QR
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_QR));
//            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_QR));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_QR));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_QR));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
//            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR)));
            summaryLinearFeeLayout.setVisibility(View.GONE);
            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 031 \n",TAG);
            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else {
            Utility.customDialogAlert(this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    dialogLoading.dismiss();
                }
            });
        }
    }

    private void setPrintLast(TransTemp transTemp) {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            Log.d("SINN", "setPrintLastSearch");
            setDataSlipSale(transTemp);
            System.out.printf("utility:: %s doPrinting Befor 032 \n", TAG);
            //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
            return;
        }

        //EPS  POS
        if (!typeHost.equalsIgnoreCase(transTemp.getHostTypeCard())) {
            new CountDownTimer(3000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            dialogLoading.dismiss();
                        }
                    });
                }
            }.start();


            return;
        }

        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("####0.00");
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
        refNoLabel.setText(transTemp.getRefNo());
        String date = transTemp.getTransDate().substring(6, 8);
        String mount = transTemp.getTransDate().substring(4, 6);
        String year = transTemp.getTransDate().substring(2, 4);
        dateLabel.setText(date + "/" + mount + "/" + year);
        timeLabel.setText(transTemp.getTransTime());

        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON  //  typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
        cardNoLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel.setText(transTemp.getApprvCode());

        String typeVoidOrSale = "";
        if (!Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3));

        if (typeHost.equalsIgnoreCase("POS")) {
            typeVoidOrSale = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            typeVoidOrSale = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_EPS);
        } else {
            typeVoidOrSale = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_TMS);
        }
        if (transTemp.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabel.setText("C");
        } else if (transTemp.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabel.setText("W");
            sigatureLabel.setVisibility(View.GONE);
        }  else {
            typeInputCardLabel.setText("S");
        }
        if (typeVoidOrSale.equals(CalculatePriceActivity.TypeSale)) {
            comCodeFragment.setVisibility(View.GONE);
            if (transTemp.getFee() != null) {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", "")))));
            } else {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
            }

            typeLabel.setText("SALE");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount().replaceAll(",", "")))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
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
//                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null){
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ","").equalsIgnoreCase("/")){
                        nameEmvCardLabel.setText("");
                    }else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",", ""))));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format( (amount + fee)))); // Paul_20190128 (float)
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

                if (transTemp.getTransType().equalsIgnoreCase("W")){
                    sigatureLabel.setVisibility(View.GONE);
                }else {
                    sigatureLabel.setVisibility(View.VISIBLE);
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
//                typeCopyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ","").equalsIgnoreCase("/")){
                    nameEmvCardLabel.setText("");
                }else {
                    nameEmvCardLabel.setText(__name.trim());
                }

                // appFrameLabel.setVisibility(View.GONE);
                tcFrameLayout.setVisibility(View.GONE);
                aidFrameLayout.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);

                taxLinearLayout.setVisibility(View.GONE);
                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",", ""))));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
            }
        } else {
//            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            // feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", "")))));
            try {
                feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", "")))));
            } catch (Exception e) {
                feeTaxLabel.setText("0.00");
            }


            typeLabel.setText("VOID");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount().replaceAll(",", "")))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
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
//                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null){
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                    String __name = transTemp.getEmvNameCardHolder().trim();
                    if (__name.replace(" ","").equalsIgnoreCase("/")){
                        nameEmvCardLabel.setText("");
                    }else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee()))));
                    Log.d(TAG, "setPrintLast(TransTemp transTemp) transTemp.getFee()=" + transTemp.getFee().replaceAll(",", ""));
//                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee().replaceAll(",",""))).replaceAll(",",""));
//                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",",""))).replaceAll(",",""));

                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",", ""))));

                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format( (amount + fee))));   // Paul_20190128 (float)
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
//                typeCopyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ","").equalsIgnoreCase("/")){
                    nameEmvCardLabel.setText("");
                }else {
                    nameEmvCardLabel.setText(__name.trim());
                }


                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getEmciFree().replaceAll(",", "")))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree().replaceAll(",", ""))));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",", ""))));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }

        String valueParameterEnable = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAG_1000);
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
        name_sw_version.setText( BuildConfig.VERSION_NAME);      // Paul_20190125 software version print

        //SINN 20181031 merge KTBNORMAL again.
        if (Preference.getInstance(this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")) {
            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);

        }

        // Paul_20190312 VOID No TAX Slip
        if (!typeVoidOrSale.equalsIgnoreCase(CalculatePriceActivity.TypeSale))
        {
            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);
        }

        isStatusPrintLastSlip = true;
        System.out.printf("utility:: %s doPrinting Befor 033 \n", TAG);

        if (Preference.getInstance(this).getValueString(Preference.KEY_PrintSlip_ID).equalsIgnoreCase("11")){
            setDataMerchant(transTemp);
            setDataCustomer(transTemp);
            //doPrinting(getBitmapFromView(slipLinearLayout));
        } else if (Preference.getInstance(this).getValueString(Preference.KEY_PrintSlip_ID).substring(0, 1).equalsIgnoreCase("1")){
            setDataMerchant(transTemp);
            doPrinting_slipdisable(getBitmapFromView(slipLinearLayout));
        } else if (Preference.getInstance(this).getValueString(Preference.KEY_PrintSlip_ID).substring(1, 2).equalsIgnoreCase("1")){
            setDataCustomer(transTemp);
            doPrinting_slipdisable(getBitmapFromView(slipLinearLayout_C));
        } else{
            dialogSuccess_GotoMain.show();
        }
    }

    private void setDataMerchant(TransTemp transTemp) {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            setDataSlipSale(transTemp);
            return;
        }
        DecimalFormat decimalFormatShow = new DecimalFormat("#,###,##0.00");    // Paul_20190118
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText(transTemp.getTid());
        midLabel.setText(transTemp.getMid());
        traceLabel.setText(transTemp.getEcr());
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
        }  else {
            typeInputCardLabel.setText("S");
        }
        if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
            if (transTemp.getFee() != null) {
                feeTaxLabel.setText( getString( R.string.slip_pattern_amount, decimalFormatShow.format( Double.valueOf( transTemp.getFee().replaceAll( ",", "" ) ) ) ) ); // Paul_20190118
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
                    if (__name.replace(" ","").equalsIgnoreCase("/")){
                        nameEmvCardLabel.setText("");
                    }else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format( (amount + fee))));    // Paul_20190128
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

                if (transTemp.getTransType().equalsIgnoreCase("W")){
                    sigatureLabel.setVisibility(View.GONE);
                }else {
                    sigatureLabel.setVisibility(View.VISIBLE);
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
                if (__name.replace(" ","").equalsIgnoreCase("/")){
                    nameEmvCardLabel.setText("");
                }else {
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
                feeTaxLabel.setText( getString( R.string.slip_pattern_amount_void, decimalFormatShow.format( Double.valueOf( transTemp.getFee().replaceAll( ",", "" ) ) ) ) ); // Paul_20190118
            } else
            {
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
                if (transTemp.getEmvNameCardHolder() != null){
                    String __name = transTemp.getEmvNameCardHolder();
                    if (__name.replace(" ","").equalsIgnoreCase("/")){
                        nameEmvCardLabel.setText("");
                    }else {
                        nameEmvCardLabel.setText(__name.trim());
                    }
                }

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",", ""))))); // Paul_20190118
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format( (amount + fee))));    // Paul_20190128 (float) delete
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

                String __name = transTemp.getEmvNameCardHolder().trim();
                if (__name.replace(" ","").equalsIgnoreCase("/")){
                    nameEmvCardLabel.setText("");
                }else {
                    nameEmvCardLabel.setText(__name.trim());
                }

//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
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
        if(Preference.getInstance(this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1"))
        {
            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);

        }
        // Paul_20190312 VOID No TAX Slip
        if (transTemp.getVoidFlag().equalsIgnoreCase("Y"))
        {
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

        name_sw_version.setText( BuildConfig.VERSION_NAME);      // Paul_20190125 software version print
//        sigatureLabel.setVisibility(View.VISIBLE);  //SINN 20181213 fixed swaping print customer /merchant

        if (transTemp.getTransType().equalsIgnoreCase("W")){
            sigatureLabel.setVisibility(View.GONE);
        }else {
            sigatureLabel.setVisibility(View.VISIBLE);
        }

        printLastView_merchant.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printLastView_merchant.layout(0, 0, printLastView_merchant.getMeasuredWidth(), printLastView_merchant.getMeasuredHeight());
    }

    private void setDataCustomer(TransTemp transTemp) {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            Log.d("SINN", "setPrintLastSearch");
            setDataSlipSale(transTemp);
            return;
        }
        DecimalFormat decimalFormatShow = new DecimalFormat("#,###,##0.00");    // Paul_20190118
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel_C.setText("TID:"+transTemp.getTid());
        midLabel_C.setText("MID:"+transTemp.getMid());
        traceLabel_C.setText("TRACE:"+transTemp.getEcr());
        if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
            batchLabel_C.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
            batchLabel_C.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
            batchLabel_C.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC"))
            batchLabel_C.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        refNoLabel_C.setText("REF NO:"+transTemp.getRefNo());
        String date = transTemp.getTransDate().substring(6, 8);
        String mount = transTemp.getTransDate().substring(4, 6);
        String year = transTemp.getTransDate().substring(2, 4);
        dateLabel_C.setText(date + "/" + mount + "/" + year);
        timeLabel_C.setText(transTemp.getTransTime());
        typeCardLabel_C.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
        cardNoLabel_C.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel_C.setText("APPR CODE:"+transTemp.getApprvCode());
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
                feeTaxLabel_C.setText( getString( R.string.slip_pattern_amount, decimalFormatShow.format( Double.valueOf( transTemp.getFee().replaceAll( ",", "" ) ) ) ) ); // Paul_20190118
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
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format( (amount + fee))));    // Paul_20190128
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
                feeTaxLabel_C.setText( getString( R.string.slip_pattern_amount_void, decimalFormatShow.format( Double.valueOf( transTemp.getFee().replaceAll( ",", "" ) ) ) ) ); // Paul_20190118
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
                    totThbLabel_C.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format( (amount + fee))));    // Paul_20190128 (float) delete
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

        if(Preference.getInstance(this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1"))
        {
            fee_thb_C.setVisibility(View.GONE);
            tot_thb_C.setVisibility(View.GONE);
            taxLinearLayout_C.setVisibility(View.GONE);

        }
        // Paul_20190312 VOID No TAX Slip
        if (transTemp.getVoidFlag().equalsIgnoreCase("Y"))
        {
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
        name_sw_version_C.setText( BuildConfig.VERSION_NAME);

        printLastView_customer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printLastView_customer.layout(0, 0, printLastView_customer.getMeasuredWidth(), printLastView_customer.getMeasuredHeight());
    }

    //PAUL_20180714
    private void setPrint_off(TransTemp transTemp) {
        setDataSlipOffline(transTemp);
        System.out.printf("utility:: %s doPrinting Befor 031 \n",TAG);
        //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
    }

//    private void setPrintLastSearch(TransTemp transTemp) {
//        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
//        {
//            Log.d("SINN", "setPrintLastSearch");
//            setDataSlipSale(transTemp);
//            System.out.printf("utility:: %s doPrinting Befor 034 \n",TAG);
//            //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
//            return;
//        }
//
//        dialogLoading.show();
////        DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");
////        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
//
//////20180904 SINN Fixed duplicate  amount format.
//        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
//        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
//        tidLabel.setText(transTemp.getTid());
//        midLabel.setText(transTemp.getMid());
//        traceLabel.setText(transTemp.getEcr());
//        systrcLabel.setText(transTemp.getTraceNo());
//        if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
//            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
//        else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
//            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
//        else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
//            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
//            //20180708 SINN Add healthcare print.
//        else if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC"))
//            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
//        //20180708 SINN Add healthcare print.
//        refNoLabel.setText(transTemp.getRefNo());
//        String date = transTemp.getTransDate().substring(6, 8);
//        String mount = transTemp.getTransDate().substring(4, 6);
//        String year = transTemp.getTransDate().substring(2, 4);
//        dateLabel.setText(date + "/" + mount + "/" + year);
//        timeLabel.setText(transTemp.getTransTime());
//
//        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
////        typeCardLabel.setText(CardPrefix.getTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
////        String cutCardStart = transTemp.getCardNo().substring(0, 6);
////        String cutCardEnd = transTemp.getCardNo().substring(12, transTemp.getCardNo().length());
////        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
////        cardNoLabel.setText(cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16));
//        cardNoLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
//        apprCodeLabel.setText(transTemp.getApprvCode());
//        System.out.printf("utility:: HHHHHHHHHHHHHH 0003 apprCodeLabel = %s \n", apprCodeLabel);
////        comCodeLabel.setText(transTemp.getComCode());
//        String typeVoidOrSale = "";
//        if (!Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
//            merchantName1Label.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1));
//        if (!Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
//            merchantName2Label.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2));
//        if (!Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
//            merchantName3Label.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3));
//
//        if (typeHost.equalsIgnoreCase("POS")) {
//            typeVoidOrSale = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_POS);
//        } else if (typeHost.equalsIgnoreCase("EPS")) {
//            typeVoidOrSale = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_EPS);
//        } else {
//            typeVoidOrSale = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_TMS);
//        }
//        if (transTemp.getTransType().equals("I")) {
//            typeInputCardLabel.setText("C");
//        } else {
//            typeInputCardLabel.setText("S");
//        }
//        if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
////            feeTaxLabel.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
//            feeTaxLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",","")))));
//            typeLabel.setText("SALE");
//            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount().replaceAll(",","")))));
//            if (!transTemp.getHostTypeCard().equals("TMS")) {
//                comCodeFragment.setVisibility(View.GONE);
//                taxIdLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
//                taxAbbLabel.setText(transTemp.getTaxAbb());
//                traceTaxLabel.setText(transTemp.getEcr());
//
//                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
//                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
//                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
//                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
//                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
//                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
//
//                /*String date = transTemp.getTransDate().substring(6,8);
//                String mount = transTemp.getTransDate().substring(4,6);
//                String year = transTemp.getTransDate().substring(0,4);
//                dateLabel.setText(date +"/" +mount + "/" + year);*/
//                String dateTax = transTemp.getTransDate().substring(6, 8);
//                String mountTax = transTemp.getTransDate().substring(4, 6);
//                String yearTax = transTemp.getTransDate().substring(2, 4);
//                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);
//
//                timeTaxLabel.setText(transTemp.getTransTime());
//                copyLabel.setText("***** MERCHANT COPY *****");
//                if (transTemp.getEmvNameCardHolder() != null)
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
//
//                if (transTemp.getFee() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",","")))));
//                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee().replaceAll(",",""))));
//                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",",""))));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format( (amount + fee)))); // Paul_20190128 (float)
//                } else {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
//                }
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
//            } else {
//                comCodeFragment.setVisibility(View.VISIBLE);
//                copyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
//
//                // appFrameLabel.setVisibility(View.GONE);
//                tcFrameLayout.setVisibility(View.GONE);
//                aidFrameLayout.setVisibility(View.GONE);
//                taxLinearLayout.setVisibility(View.GONE);
//
//                taxLinearLayout.setVisibility(View.GONE);
//                if (transTemp.getEmciFree() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getEmciFree().replaceAll(",","")))));
//                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree().replaceAll(",",""))));
//                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",",""))));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((double) (amount + fee))));
//                } else {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
//                }
//            }
//        } else {
////            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
//            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",","")))));
//            typeLabel.setText("VOID");
//            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount().replaceAll(",","")))));
//            if (!transTemp.getHostTypeCard().equals("TMS")) {
//                comCodeFragment.setVisibility(View.GONE);
//                taxIdLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
//                taxAbbLabel.setText(transTemp.getTaxAbb());
//                traceTaxLabel.setText(transTemp.getEcr());
//                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
//                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
//                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
//                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
//                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
//                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
//                String dateTax = transTemp.getTransDate().substring(6, 8);
//                String mountTax = transTemp.getTransDate().substring(4, 6);
//                String yearTax = transTemp.getTransDate().substring(2, 4);
//                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);
////                dateTaxLabel.setText(transTemp.getTransDate());
//                timeTaxLabel.setText(transTemp.getTransTime());
////                feeTaxLabel.setText(transTemp.getFee());
//                copyLabel.setText("***** MERCHANT COPY *****");
//                if (transTemp.getEmvNameCardHolder() != null)
//                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
//
//                if (transTemp.getFee() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getFee().replaceAll(",","")))));
//                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee().replaceAll(",",""))));
//                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",",""))));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format( (amount + fee))));    // Paul_20190128 (float)
//                } else {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
//                }
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
//            } else {
//                comCodeFragment.setVisibility(View.VISIBLE);
//                taxLinearLayout.setVisibility(View.GONE);
//
//                // appFrameLabel.setVisibility(View.GONE);
//                tcFrameLayout.setVisibility(View.GONE);
//                aidFrameLayout.setVisibility(View.GONE);
//                taxLinearLayout.setVisibility(View.GONE);
//
//                copyLabel.setText("***** MERCHANT COPY *****");
//                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
//                if (transTemp.getEmciFree() != null) {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getEmciFree().replaceAll(",","")))));
//                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree().replaceAll(",",""))));
//                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount().replaceAll(",",""))));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((double) (amount + fee))));
//                } else {
//                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
//                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
//                }
//            }
//        }
//
//        String valueParameterEnable = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAG_1000);
//        if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
//            comCodeLabel.setVisibility(View.VISIBLE);
//            comCodeLabel.setText(transTemp.getComCode());
//        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
//            comCodeLabel.setVisibility(View.VISIBLE);
//            comCodeLabel.setText(transTemp.getComCode());
//        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
//            comCodeLabel.setVisibility(View.GONE);
//            comCodeLabel.setText(transTemp.getComCode());
//        }
//        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
//            ref1RelativeLayout.setVisibility(View.VISIBLE);
//            ref1Label.setText(transTemp.getRef1());
//        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
//            ref1RelativeLayout.setVisibility(View.VISIBLE);
//            ref1Label.setText(transTemp.getRef1());
//        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
//            ref1RelativeLayout.setVisibility(View.GONE);
//            ref1Label.setText(transTemp.getRef1());
//        }
//        if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
//            ref2RelativeLayout.setVisibility(View.VISIBLE);
//            ref2Label.setText(transTemp.getRef2());
//        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
//            ref2RelativeLayout.setVisibility(View.VISIBLE);
//            ref2Label.setText(transTemp.getRef2());
//        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
//            ref2RelativeLayout.setVisibility(View.GONE);
//            ref2Label.setText(transTemp.getRef2());
//        }
//        if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
//            ref3RelativeLayout.setVisibility(View.VISIBLE);
//            ref3Label.setText(transTemp.getRef3());
//        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
//            ref3RelativeLayout.setVisibility(View.VISIBLE);
//            ref3Label.setText(transTemp.getRef3());
//        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
//            ref3RelativeLayout.setVisibility(View.GONE);
//            ref3Label.setText(transTemp.getRef3());
//        }
//        name_sw_version.setVisibility(View.VISIBLE);             // Paul_20190125 software version print
//        name_sw_version.setText( BuildConfig.VERSION_NAME);      // Paul_20190125 software version print
//        /*if (!transTemp.getRef1().isEmpty()) {
//            ref1RelativeLayout.setVisibility(View.VISIBLE);
//            ref1Label.setText(transTemp.getRef1());
//        }
//        if (!transTemp.getRef2().isEmpty()) {
//            ref2RelativeLayout.setVisibility(View.VISIBLE);
//            ref2Label.setText(transTemp.getRef2());
//        }
//        if (!transTemp.getRef3().isEmpty()) {
//            ref3RelativeLayout.setVisibility(View.VISIBLE);
//            ref3Label.setText(transTemp.getRef3());
//        }*/
//        setMeasure();
//        isStatusPrintLastSlip = true;
//        System.out.printf("utility:: %s doPrinting Befor 035 \n",TAG);
//        //doPrinting(getBitmapFromView(slipLinearLayout));
//
//        rePrintLast(transTemp);
//    }

//    private void rePrintLast(TransTemp transTemp) {
//        sigatureLabel.setVisibility(View.GONE);
//        if (transTemp.getEmvNameCardHolder() != null)
//            nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, 20, 0, 0);
//        nameEmvCardLabel.setLayoutParams(lp);
//        nameEmvCardLabel.setGravity(Gravity.CENTER_HORIZONTAL);
//        copyLabel.setText("**** ต้นฉบับ ****");
//
//        setMeasure();
//    }

//    private void customDialogSearch() {
//        dialogSearch = new Dialog(this);
//        dialogSearch.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogSearch.setContentView(R.layout.dialog_search);
//        dialogSearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogSearch.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        invoiceEt = dialogSearch.findViewById(R.id.invoiceEt);
//        searchInvoiceImage = dialogSearch.findViewById(R.id.searchInvoiceImage);
//        invoiceEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    String traceNoAddZero = "";
//                    if (!invoiceEt.getText().toString().isEmpty()) {
//                        if (invoiceEt.getText().toString().length() < 6) {
//                            for (int i = invoiceEt.getText().toString().length(); i < 6; i++) {
//                                traceNoAddZero += "0";
//                            }
//                        }
//                        //20180708 SINN Add healthcare print.
//                        Log.d(TAG, "hostTypeCard customDialogSearch: " + typeHost);
//                        if (typeHost.equals("GHC"))      // Paul_20180714 typeHost = "ghc";
//                            typeHost = typeHost.toUpperCase();
//
//                        //PAUL_20180714_START
//                        TransTemp transTemp;
////                        SaleOfflineHealthCare offlineTemp = null;     // Paul_20180724_OFF
//
////                        if(typeHost.equals("GHC")){
////                            offlineTemp = realm.where(SaleOfflineHealthCare.class).equalTo("ecr", traceNoAddZero+invoiceEt.getText().toString()).findFirst();
////                            transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero+invoiceEt.getText().toString()).findFirst();
////                        }else
////                            transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero+invoiceEt.getText().toString()).findFirst();
////                        if (transTemp != null || offlineTemp != null) {
////                            if(offlineTemp != null)
////                                setPrint_off(offlineTemp);
////                            else
////                                setPrintLastSearch(transTemp);
////                            //PAUL_20180714_END
////                        }
//                        // Paul_20180724_OFF
//                        transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
//                        if (transTemp != null) {
//                            if (typeHost.equals("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
//                                setPrint_off(transTemp);
//                            } else {
//                                setPrintLastSearch(transTemp);
//                            }
//                        } else {
//                            Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                @Override
//                                public void onClickImage(Dialog dialog) {
//                                    dialog.dismiss();
//                                    dialogLoading.dismiss();
//                                }
//                            });
//                        }
//                    } else {
//                        invoiceEt.setError("กรุณาใส่ตัวเลข");
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        searchInvoiceImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String traceNoAddZero = "";
//                if (!invoiceEt.getText().toString().isEmpty()) {
//                    if (invoiceEt.getText().toString().length() < 6) {
//                        for (int i = invoiceEt.getText().toString().length(); i < 6; i++) {
//                            traceNoAddZero += "0";
//                        }
//                    }
//////20180708 SINN Add healthcare print.
//                    Log.d(TAG, "hostTypeCard: setOnClickListener " + typeHost);
//                    if (typeHost.equals("GHC"))      // Paul_20180714 typeHost = "ghc";
//                        typeHost = typeHost.toUpperCase();
//
//                    //PAUL_20180714_START
//                    TransTemp transTemp;
////                    SaleOfflineHealthCare offlineTemp = null;     // Paul_20180724_OFF
//
////                    if(typeHost.equals("GHC")){
////                        offlineTemp = realm.where(SaleOfflineHealthCare.class).equalTo("ecr", traceNoAddZero+invoiceEt.getText().toString()).findFirst();
////                        transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero+invoiceEt.getText().toString()).findFirst();
////                    }else
////                        transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero+invoiceEt.getText().toString()).findFirst();
////                    if (transTemp != null || offlineTemp != null) {
////                        if(offlineTemp != null)
////                            setPrint_off(offlineTemp);
////                        else
////                            setPrintLastSearch(transTemp);
////                        //PAUL_20180714_END
////                    }
//                    // Paul_20180724_OFF
//                    transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
//                    if (transTemp != null) {
//                        if (typeHost.equals("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
//                            setPrint_off(transTemp);
//                        } else {
//                            setPrintLastSearch(transTemp);
//                        }
//                    } else {
//                        Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                            @Override
//                            public void onClickImage(Dialog dialog) {
//                                dialog.dismiss();
//                                dialogLoading.dismiss();
//                            }
//                        });
//                    }
//                } else {
//                    invoiceEt.setError("กรุณาใส่ตัวเลข");
//                }
//            }
//        });
//    }


    private void customDialogHost() {
        dialogHost = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180828 change UI
        View view = dialogHost.getLayoutInflater().inflate(R.layout.dialog_custom_host, null);//K.GAME 180828 change UI
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change UI
        dialogHost.setContentView(view);//K.GAME 180828 change UI
        dialogHost.setCancelable(false);//K.GAME 180828 change UI

//        dialogHost = new Dialog(this);
//        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogHost.setCancelable(false);
//        dialogHost.setContentView(R.layout.dialog_custom_host);
//        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
//20180708 SINN Add healthcare print.
        ghcBtn = dialogHost.findViewById(R.id.ghcBtn);
//END //20180708 SINN Add healthcare print.
//20180706 Add QR print.
        qrBtn = dialogHost.findViewById(R.id.qrBtn);
        aliBtn = dialogHost.findViewById(R.id.aliBtn);
        wechatBtn = dialogHost.findViewById(R.id.wechatBtn);
        qrBtn.setVisibility(View.VISIBLE);

        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)
            tmsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
            posBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length()<8)
            epsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)
            ghcBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)
            qrBtn.setVisibility(View.GONE);

        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
            aliBtn.setVisibility(View.VISIBLE);
        }

        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
            wechatBtn.setVisibility(View.VISIBLE);
        }

        closeImage = dialogHost.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.dismiss();
            }
        });
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("slipLast")) {
                    selectReportLast("POS");
                } else if (typeClick.equals("slipPrevious")) {
//                    typeHost = "POS";
//                    selectReportPrevious("POS");
                    //K.GAME 181017 ย้ายเข้ามาใส่ไว้ก่อน Reprint อาจจะต้องมีการคัดการในอนาคต
//                    Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity.class);
                    //game 20181019 reprint new UI
                    Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity2.class);
                    invoiceId = "";
                    inReprintType = "2";
                    if (typeClick.equals("slipLast")) {
                        inReprintType = "1";
                        invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);
                    }

                    intent.putExtra(KEY_INTERFACE_INV, invoiceId);
                    intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, inReprintType);
                    startActivity(intent);
                    finish();
                    //END K.GAME 181017
                } else {
                    typeHost = "POS";
                    selectReportSettleLast();
                }
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("slipLast")) {
                    selectReportLast("EPS");
                } else if (typeClick.equals("slipPrevious")) {
//                    typeHost = "EPS";
//                    selectReportPrevious("EPS");
                    //K.GAME 181017 ย้ายเข้ามาใส่ไว้ก่อน Reprint อาจจะต้องมีการคัดการในอนาคต
                    //                    Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity.class);
                    //game 20181019 reprint new UI
                    Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity2.class);
                    invoiceId = "";
                    inReprintType = "2";
                    if (typeClick.equals("slipLast")) {
                        inReprintType = "1";
                        invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);
                    }

                    intent.putExtra(KEY_INTERFACE_INV, invoiceId);
                    intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, inReprintType);
                    startActivity(intent);
                    finish();
                    //END K.GAME 181017
                } else {
                    typeHost = "EPS";
                    selectReportSettleLast();

                }
            }
        });

        tmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("slipLast")) {
                    selectReportLast("TMS");
                } else if (typeClick.equals("slipPrevious")) {
//                    typeHost = "TMS";
//                    selectReportPrevious("TMS");
                    //K.GAME 181017 ย้ายเข้ามาใส่ไว้ก่อน Reprint อาจจะต้องมีการคัดการในอนาคต
                    //                    Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity.class);
                    //game 20181019 reprint new UI
                    Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity2.class);
                    invoiceId = "";
                    inReprintType = "2";
                    if (typeClick.equals("slipLast")) {
                        inReprintType = "1";
                        invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);
                    }

                    intent.putExtra(KEY_INTERFACE_INV, invoiceId);
                    intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, inReprintType);
                    startActivity(intent);
                    finish();
                    //END K.GAME 181017
                } else {
                    typeHost = "TMS";
                    selectReportSettleLast();
                }
            }
        });

//20180708 SINNN Add healthcare print.
//        ghcBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (typeClick.equals("slipLast")) {
//                    selectReportLast("GHC");
//                } else if (typeClick.equals("slipPrevious")) {
//                    typeHost = "GHC";
//                    selectReportPrevious("GHC");
//                } else {
//                    typeHost = "GHC";
//                    selectReportSettleLast();
//                }
//            }
//        });

        ghcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogLoading.dismiss();
//                dialogHost.dismiss();  //20180907 try fix
//                Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity.class);
//                invoiceId = "";
//                inReprintType = "2";
//                if (typeClick.equals("slipLast")) {
//                    inReprintType = "1";
//                    invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);
//                }
//
//                intent.putExtra(KEY_INTERFACE_INV, invoiceId);
//                intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, inReprintType);
//                startActivity(intent);
//                finish();

                dialogLoading.dismiss();
                dialogHost.dismiss();  //20180907 try fix

                if (typeClick.equals("slipLast")) {
                    selectReportLast("GHC");
                }
                else
                {

//                Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity.class);
                    //game 20181019 reprint new UI
                Intent intent = new Intent(ReprintActivity.this, ReprintAnyActivity2.class);
                invoiceId = "";
                inReprintType = "2";
                if (typeClick.equals("slipLast")) {
                    inReprintType = "1";
                    invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);
                }

                intent.putExtra(KEY_INTERFACE_INV, invoiceId);
                intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, inReprintType);
                startActivity(intent);
                finish();
                }


            }
        });
//END 20180708 SINN Add healthcare print.


        //20180706 SINN Add QR print.
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoading.dismiss();
                checkQRfuncton();
            }
        });
        //end //20180706 SINN Add QR print.

        //20181116 JEFF Add ALIPAY print.
        aliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoading.dismiss();
                checkALIfuncton();

            }
        });
        //end 20181116 JEFF Add ALIPAY print.

        //20181116 JEFF Add WECHAT print.
        wechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoading.dismiss();
                checkWECfuncton();
            }
        });
        //end 20181116 JEFF Add WECHAT print.
    }

    /**
     * View Slip
     */
    //sinn 20180706 SINN Add QR print.
/*
    private void setDataSlipSaleOffline(TransTemp healthCareDB) {
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);
//20180720 SINN  HGC slip fix
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        DateTimePrn.setText("Date Time      "+dateFormat.format(date));
//END 20180720 SINN  HGC slip fix
        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
        systrcGHCLabel.setText(healthCareDB.getTraceNo());  //(:
        Log.d("SINN:", "systrcLabel :"+systrcLabel.getText());
        System.out.printf("utility:: systrcLabel 001 = %s \n",healthCareDB.getTraceNo());

        DateTimePrn.setText("Date Time      "+dateFormat.format(date));

        traceNoLabel.setText(healthCareDB.getEcr());
        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            cardNoLabel.setText(healthCareDB.getCardNo());
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            cardNoLabel.setText(healthCareDB.getIdCard());
        } else {
            cardNoLabel.setText(healthCareDB.getCardNo());
        }
//        nameEngLabel.setText(healthCareDB.getEngFName());
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText( healthCareDB.getEngFName() );
        }
        else {
            nameEngLabel.setText(null);
        }
        apprCodeHgcLabel.setText(healthCareDB.getApprvCode()); // Paul_20180712
        System.out.printf("utility:: HHHHHHHHHHHHHH 0004 apprCodeLabel = %s \n",apprCodeLabel);
        comCodeHgcLabel.setText(healthCareDB.getComCode()); // Paul_20180714
        batchHgcLabel.setText( CardPrefix.calLen( Preference.getInstance(this).getValueString( Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3));

        amountLabel.setText( "*"+healthCareDB.getAmount());

        setMeasureHGC();
    }
*/
    private void setDataSlipSale(TransTemp healthCareDB) {
//        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Paul_20180711_new

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00"); ////20180904 SINN Fixed duplicate  amount format.
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
//        systrcLabel.setText(healthCareDB.getTraceNo());
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
//            amountLabel.setText( "*"+healthCareDB.getAmount());
            amountLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(healthCareDB.getAmount().replaceAll(",","")))));       ////20180904 SINN Fixed duplicate  amount format.
        } else {
//            amountLabel.setText( "-"+healthCareDB.getAmount());
            amountLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(healthCareDB.getAmount().replaceAll(",","")))));     ////20180904 SINN Fixed duplicate  amount format.
        }

        setMeasureHGC();
    }

    //PAUL_20180714
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
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00"); ////20180904 SINN Fixed duplicate  amount format.

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

//        amountLabel.setText( "*"+healthCareDB.getAmount());
        if (healthCareDB.getVoidFlag().equals("N"))
            amountLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(healthCareDB.getAmount().replaceAll(",","")))));      ////20180904 SINN Fixed duplicate  amount format.
        else
            amountLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(healthCareDB.getAmount().replaceAll(",","")))));     ////20180904 SINN Fixed duplicate  amount format.

        setMeasureHGC();
    }
    // Paul_20190214
    private void checkQRfuncton() {

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);  // Paul_20190214
        invoiceId = checkLength(invoiceId, 6);
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            QrCode qrTemp;
            qrTemp= realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("trace", invoiceId).equalTo("statusSuccess","1").findFirst();

            if(qrTemp != null){
                Intent intent = new Intent(ReprintActivity.this, ReprintQrActivity.class);
                invoiceId = "";
                inReprintType = "2";
                if (typeClick.equals("slipLast")) {
                    inReprintType = "1";
                    invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_LAST_TRACE);
                }

                intent.putExtra(KEY_INTERFACE_INV, invoiceId);
                intent.putExtra(KEY_INTERFACE_REPRINT_TYPE, inReprintType);
                startActivity(intent);
                finish();
/*

                if(aliTemp.getVoidFlag().equals("N")) {
                    type = AliConfig.Sale;

                    if(aliTemp.getAmtplusfee().equals("null"))
                        amt = aliTemp.getAmt();
                    else
                        amt = aliTemp.getAmtplusfee();

                }else {
                    type = AliConfig.Void;

                    if(aliTemp.getAmtplusfee().equals("null"))
                        amt = "-" + aliTemp.getAmt();
                    else
                        amt = "-" + aliTemp.getAmtplusfee();
                }

                Intent intent = new Intent(ReprintActivity.this, AliReprintActivity.class);
                intent.putExtra("STATUS", AliConfig.Success);
                intent.putExtra("TYPE", type);
                intent.putExtra("INVOICE", invoiceId);
                intent.putExtra("AMOUNT", amt);
                startActivity(intent);

                finish();
*/
            }else{
                Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                        if(dialogLoading != null)
                            dialogLoading.dismiss();
                    }
                });
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }
    }

    private void checkALIfuncton() {

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_ALIPAY_LAST_TRACE);
        invoiceId = checkLength(invoiceId, 6);
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            QrCode aliTemp;
            aliTemp= realm.where(QrCode.class).equalTo("trace", invoiceId).findFirst();

            if(aliTemp != null){
                if(aliTemp.getVoidFlag().equals("N")) {
                    type = AliConfig.Sale;

                    if(aliTemp.getAmtplusfee().equals("null"))
                        amt = aliTemp.getAmt();
                    else
                        amt = aliTemp.getAmtplusfee();

                }else {
                    type = AliConfig.Void;

                    if(aliTemp.getAmtplusfee().equals("null"))
                        amt = "-" + aliTemp.getAmt();
                    else
                        amt = "-" + aliTemp.getAmtplusfee();
                }

                Intent intent = new Intent(ReprintActivity.this, AliReprintActivity.class);
                intent.putExtra("STATUS", AliConfig.Success);
                intent.putExtra("TYPE", type);
                intent.putExtra("INVOICE", invoiceId);
                intent.putExtra("AMOUNT", amt);
                startActivity(intent);

                finish();

            }else{
                Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                        dialogLoading.dismiss();
                    }
                });
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }
    }

    private void checkWECfuncton() {

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        invoiceId = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_WECHAT_LAST_TRACE);
        invoiceId = checkLength(invoiceId, 6);
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            QrCode aliTemp;
            aliTemp= realm.where(QrCode.class).equalTo("trace", invoiceId).findFirst();

            if(aliTemp != null){
                if(aliTemp.getVoidFlag().equals("N")) {
                    type = AliConfig.Sale;

                    if(aliTemp.getAmtplusfee().equals("null"))
                        amt = aliTemp.getAmt();
                    else
                        amt = aliTemp.getAmtplusfee();

                }else {
                    type = AliConfig.Void;

                    if(aliTemp.getAmtplusfee().equals("null"))
                        amt = "-" + aliTemp.getAmt();
                    else
                        amt = "-" + aliTemp.getAmtplusfee();
                }

                Intent intent = new Intent(ReprintActivity.this, AliReprintActivity.class);
                intent.putExtra("STATUS", AliConfig.Success);
                intent.putExtra("TYPE", type);
                intent.putExtra("INVOICE", invoiceId);
                intent.putExtra("AMOUNT", amt);
                startActivity(intent);

                finish();

            }else{
                Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                        dialogLoading.dismiss();
                    }
                });
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }
    }

    private String checkLength(String trace, int i ) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for(int j = 0; j<(i-tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }

    private void setMeasureHGC() {
        hgcSaleView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleView.layout(0, 0, hgcSaleView.getMeasuredWidth(), hgcSaleView.getMeasuredHeight());
    }

    private void setViewSaleHGC() {
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

//end 20180706 SINN Add QR print.

    private void customDialogHostQr() {
        dialogHostQr = new Dialog(this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogHostQr.getLayoutInflater().inflate(R.layout.dialog_custom_host_qr, null);//K.GAME 180828 change dialog UI
        dialogHostQr.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogHostQr.setContentView(view);//K.GAME 180828 change dialog UI
        dialogHostQr.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogHostQr = new Dialog(this);
//        dialogHostQr.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogHostQr.setCancelable(false);
//        dialogHostQr.setContentView(R.layout.dialog_custom_host_qr);
//        dialogHostQr.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogHostQr.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posQrBtn = dialogHostQr.findViewById(R.id.posBtn);
        epsQrBtn = dialogHostQr.findViewById(R.id.epsBtn);
        tmsQrBtn = dialogHostQr.findViewById(R.id.tmsBtn);
        qrBtn = dialogHostQr.findViewById(R.id.qrBtn);
        aliBtn = dialogHostQr.findViewById(R.id.aliBtn);
        wechatBtn = dialogHostQr.findViewById(R.id.wechatBtn);
        ghcQrBtn = dialogHostQr.findViewById(R.id.ghcBtn);  ////20180708 SINN Add healthcare print.
//
//        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("111")) {
//            posQrBtn.setVisibility(View.VISIBLE);
//            epsQrBtn.setVisibility(View.VISIBLE);
//            tmsQrBtn.setVisibility(View.VISIBLE);
//            qrBtn.setVisibility(View.VISIBLE);
//            ghcQrBtn.setVisibility(View.VISIBLE);
//        } else if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("011")) {
//            posQrBtn.setVisibility(View.GONE);
//            epsQrBtn.setVisibility(View.GONE);
//            tmsQrBtn.setVisibility(View.GONE);
//            qrBtn.setVisibility(View.VISIBLE);
//            ghcQrBtn.setVisibility(View.VISIBLE);
//        } else if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("101")) {
//            posQrBtn.setVisibility(View.VISIBLE);
//            epsQrBtn.setVisibility(View.VISIBLE);
//            tmsQrBtn.setVisibility(View.VISIBLE);
//            qrBtn.setVisibility(View.GONE);
//            ghcQrBtn.setVisibility(View.VISIBLE);
//        } else if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("100")) {
//            posQrBtn.setVisibility(View.VISIBLE);
//            epsQrBtn.setVisibility(View.VISIBLE);
//            tmsQrBtn.setVisibility(View.VISIBLE);
//            qrBtn.setVisibility(View.GONE);
//            ghcQrBtn.setVisibility(View.GONE);
//        } else if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("110")) {
//            posQrBtn.setVisibility(View.VISIBLE);
//            epsQrBtn.setVisibility(View.VISIBLE);
//            tmsQrBtn.setVisibility(View.VISIBLE);
//            qrBtn.setVisibility(View.VISIBLE);
//            ghcQrBtn.setVisibility(View.GONE);
//        } else if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("001")) {
//            posQrBtn.setVisibility(View.GONE);
//            epsQrBtn.setVisibility(View.GONE);
//            tmsQrBtn.setVisibility(View.GONE);
//            qrBtn.setVisibility(View.GONE);
//            ghcQrBtn.setVisibility(View.VISIBLE);
//        } else if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("010")) {
//            posQrBtn.setVisibility(View.GONE);
//            epsQrBtn.setVisibility(View.GONE);
//            tmsQrBtn.setVisibility(View.GONE);
//            qrBtn.setVisibility(View.VISIBLE);
//            ghcQrBtn.setVisibility(View.GONE);
//        }


//SINN 20181119  AXA no need select host
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)
            tmsQrBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
            posQrBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length()<8)
            epsQrBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)
            ghcQrBtn.setVisibility(View.GONE);
        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)
            qrBtn.setVisibility(View.GONE);
//END SINN 20181119  AXA no need select host

        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
            aliBtn.setVisibility(View.VISIBLE);
        }

        if (Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
            wechatBtn.setVisibility(View.VISIBLE);
        }

        closeQrImage = dialogHostQr.findViewById(R.id.closeImage);
        closeQrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHostQr.dismiss();
            }
        });
        posQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "POS";
                selectReportSettleLast();
            }
        });
        epsQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "EPS";
                selectReportSettleLast();
            }
        });

        tmsQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "TMS";
                selectReportSettleLast();
            }
        });
        //20180708 SINN Add healthcare print.
        ghcQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "GHC";
                selectReportSettleLast();
            }
        });
        //end  //20180708 SINN Add healthcare print.
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "QR";
                selectReportSettleLast();
            }
        });
        aliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "ALIPAY";
                selectAliSettleLast(typeHost);
            }
        });
        wechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "WECHAT";
                selectAliSettleLast(typeHost);
            }
        });
    }

    private void selectAliSettleLast(String typeHost) {
        dialogLoading.show();
        if (typeHost.equalsIgnoreCase("ALIPAY")  && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_ALI).isEmpty()) {
            hostLabelSettle.setText("ALIPAY");
            String batch = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER_LAST); //20181120
            batch = checkLength(batch, 6);
            batchLabelSettle.setText(batch);
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_ALI));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_ALI));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_ALI));
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_ALI)));

            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_ALI));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_ALI));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_ALI)));

//            summaryLinearFeeLayout.setVisibility(View.GONE);
            hostFeeLabel.setText("ALIPAY");

            batchFeeLabel.setText(batch);
            saleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_ALI));
            dateFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_ALI));
            timeFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_ALI));
            saleTotalFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_ALI)));
            voidSaleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_ALI));
            voidSaleAmountFeeLabel.setText(getString(R.string.slip_pattern_amount_void, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_ALI)));
            cardAmountFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_ALI)));
            cardCountFeeLabel.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            taxIdFeeLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));

// Paul_20190202
            Double salefee = 0.0;
            Double voidfee = 0.0;
            salefee = Double.valueOf( Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_ALI).replaceAll( "," ,"") );
            voidfee = Double.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_ALI).replaceAll( "," ,""));
            System.out.printf("utility:: %s salefee = %f , voidfee = %f \n",TAG,salefee,voidfee);
            if((salefee + voidfee) == 0)
                summaryLinearFeeLayout.setVisibility(View.GONE);

            setMeasureSettle();
            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else if (typeHost.equalsIgnoreCase("WECHAT")  && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_WEC).isEmpty()) {
            hostLabelSettle.setText("WECHAT PAY");      // Paul_20190324
            String batch = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER_LAST); //20181120
            batch = checkLength(batch, 6);
            batchLabelSettle.setText(batch);
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_WEC));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_WEC));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_WEC));
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_WEC)));

            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_WEC));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_WEC));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_WEC)));

//            summaryLinearFeeLayout.setVisibility(View.GONE);
            hostFeeLabel.setText("WECHAT PAY");     // Paul_20190324
            batchFeeLabel.setText(batch);
            saleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_WEC));
            dateFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_WEC));
            timeFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_WEC));
            saleTotalFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_WEC)));
            voidSaleCountFeeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_WEC));
            voidSaleAmountFeeLabel.setText(getString(R.string.slip_pattern_amount_void, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_WEC)));
            cardAmountFeeLabel.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_WEC)));
            cardCountFeeLabel.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            taxIdFeeLabel.setText(Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
// Paul_20190202
            Double salefee = 0.0;
            Double voidfee = 0.0;
            salefee = Double.valueOf( Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_WEC).replaceAll( "," ,"") );
            voidfee = Double.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_WEC).replaceAll( "," ,""));
            System.out.printf("utility:: %s salefee = %f , voidfee = %f \n",TAG,salefee,voidfee);
            if((salefee + voidfee) == 0)
                summaryLinearFeeLayout.setVisibility(View.GONE);

            setMeasureSettle();
            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else {
            Utility.customDialogAlert(this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    if(dialogLoading != null)
                        dialogLoading.dismiss();
                }
            });
        }
    }

    private void selectReportSettleLast() {
        setDataViewSettle();
    }

    private void selectReportLast(String typeHost) {
        dialogLoading.show();
        int voidSaleId = 0;
        String traceNoAddZero = "";
        String szMSG = "";

        // Paul_20190214
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        if (typeHost.equalsIgnoreCase("POS")) {
            this.typeHost = typeHost;
            voidSaleId = Preference.getInstance(ReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            this.typeHost = typeHost;
            voidSaleId = Preference.getInstance(ReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_EPS);
            Log.d(TAG,"selectReportLast voidSaleId:"+String.valueOf(voidSaleId));
        } else if (typeHost.equalsIgnoreCase("TMS")) {      //20180708 SINN Add healthcare print.
            this.typeHost = typeHost;
            voidSaleId = Preference.getInstance(ReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_TMS);
        }
        //20180708 SINN Add healthcare print.
        else {
            typeHost = "GHC";
            this.typeHost = "GHC";
            voidSaleId = Preference.getInstance(ReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_GHC);
            szMSG = "000000";
            szMSG = szMSG.substring(String.valueOf(voidSaleId).length()) + String.valueOf(voidSaleId);
        }
        //END 20180708 SINN Add healthcare print.

        Log.d("Reprint", "typeHost:" + typeHost);
        Log.d("Reprint", "voidSaleId:" + String.valueOf(voidSaleId));
        Log.d("Reprint", "voidSaleId:" + szMSG);

        //PAUL_20180714_START
        TransTemp transTemp;
        String tmp = "0";
        String tmp2 = "0";
        if (typeHost.equals("GHC")) {
//            offlineTemp = realm.where(SaleOfflineHealthCare.class).equalTo("ecr", szMSG).findFirst();     // Paul_20180724_OFF
            transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", szMSG).findFirst();
        } else {
            transTemp = realm.where( TransTemp.class ).equalTo( "id", voidSaleId ).findFirst();
        }
        //end //20180708 SINN Add healthcare print.

//        TransTemp transTemp = realm.where(TransTemp.class).equalTo("id", voidSaleId).findFirst();
        if (transTemp != null) {
            if (typeHost.equals("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y"))
                setPrint_off(transTemp);
            else
                setPrintLast(transTemp);
            //PAUL_20180714_END
        } else {
            Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    if(dialogLoading != null)
                        dialogLoading.dismiss();
                }
            });
        }
    }


    public void doPrinting_slipdisable(Bitmap slip) {
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

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new CountDownTimer(2000, 1000) {

                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            dialogSuccess_GotoMain.show();
                                        }

                                        @Override
                                        public void onFinish() {

                                        }
                                    }.start();
                                }
                            });

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
                                                System.out.printf("utility:: %s doPrinting Befor 026 \n",TAG);
                                                //doPrinting(getBitmapFromView(slipLinearLayout_C));
                                            }
                                        }.start();
                                    }
                                });
                            } else {
                                dialogLoading.dismiss();
//                                Intent intent = new Intent(ReprintActivity.this, MenuServiceActivity.class);
                                Intent intent = new Intent(ReprintActivity.this, MenuServiceListActivity.class); // Paul_20180704
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
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
                System.out.printf("utility:: %s doPrinting Befor 025 \n",TAG);
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

        dialogLoading.setCancelable( false );   // Paul_20181015 Printing Can not cancel button

//        dialogLoading = new Dialog(this);
//        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogLoading.setContentView(R.layout.dialog_custom_alert_loading);
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(realm == null)       // Paul_20181026
            realm = Realm.getDefaultInstance();
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
