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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.adapter.MenuReportAdapter;
import org.centerm.Tollway.adapter.ReportAliTaxDetailAdapter;
import org.centerm.Tollway.adapter.ReportTaxDetailAdapter;
import org.centerm.Tollway.adapter.SlipAlipayReportAdapter;
import org.centerm.Tollway.adapter.SlipQrReportAdapter;
import org.centerm.Tollway.adapter.SlipReportAdapter;
import org.centerm.Tollway.adapter.SlipReportCardAdapter;
import org.centerm.Tollway.adapter.SlipSummaryReportCardAdapter;
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

import io.realm.Realm;
import io.realm.RealmResults;

//import org.centerm.Tollway.healthcare.database.SaleOfflineHealthCare;    // Paul_20180724_OFF

public class MenuDetailReportActivity extends SettingToolbarActivity {

    private RecyclerView menuRecyclerView;
    private RecyclerView recyclerViewReportDetail;
    private RecyclerView recyclerViewCardReportDetail;      // Paul_20181202
    private RecyclerView recyclerViewCardReportSummary;      // Paul_20181203
    private LinearLayout reportDetailLinearLayout = null;
    private LinearLayout reportCardDetailLinearLayout = null;   // Paul_20181202
    private LinearLayout reportCardSummaryLinearLayout = null;   // Paul_20181203
    private MenuReportAdapter menuReportAdapter = null;
    private List<String> nameList;
    private Dialog dialogMenu;
    private Button posBtn;
    private Button epsBtn;
    private Button tmsBtn;
    private Button qrBtn;
    private Button aliBtn;
    private Button wechatBtn;
    private Button ghcBtn;
    //    private ImageView closeImage; //K.GAME 180828 change dialog UI
    private Button closeImage; //K.GAME 180828 change dialog UI

    private Realm realm;
    private View reportView;
    private View reportSummaryView;
    private View reportGHCSummaryView;      // Paul_20181202
    private CardManager cardManager = null;
    private AidlPrinter printDev = null;
    private final String TAG = "MenuDetailReport";
    private SlipReportAdapter slipReportAdapter;
    //    private SlipReportHcOffAdapter slipReportHcOffAdapter;
    private SlipReportAdapter slipReportHcOffAdapter;       // Paul_20180724_OFF
    private SlipReportCardAdapter slipReportCardAdapter;    // Paul_20181201 Card Type Add
    private SlipSummaryReportCardAdapter slipSummaryReportCardAdapter;    // Paul_20181203 Card Type Add

    private List<TransTemp> transTempList = null;
    private List<TransTemp> CardTypeDB = null;      // Paul_20181202

    private int summaryReportSize = 0;
    private Double totalAll = 0.0;
    private int countAll = 0;
    private Double totalSale = 0.0;
    private Double totalVoid = 0.0;
    private String cntSale;
    private String cntVoid;
    private TextView merchantName1Label;
    private TextView merchantName2Label;
    private TextView merchantName3Label;
// Paul_20181203
    private TextView summarymerchantName1Label;
    private TextView summarymerchantName2Label;
    private TextView summarymerchantName3Label;
    private TextView summarydateLabel;
    private TextView summarytimeLabel;
    private TextView summarymidLabel;
    private TextView summarytidLabel;
    private TextView summarybatchLabel;
    private TextView summaryhostLabel;

    private TextView summarysaleCountLabel;
    private TextView summarysaleTotalLabel;
    private TextView summaryvoidSaleCountLabel;
    private TextView summaryvoidSaleAmountLabel;

    private TextView summarycardCountLabel;
    private TextView summarycardAmountLabel;
    private TextView summarysubjectSlipLabel;


//    private Button AlipayBtn;
//    private Button WechatBtn;


    private TextView dateLabel;
    private TextView timeLabel;
    private TextView midLabel;
    private TextView tidLabel;
    private TextView batchLabel;
    private TextView hostLabel;
    private TextView saleCountLabel;
    private TextView saleTotalLabel;
    private TextView voidSaleCountLabel;
    private TextView voidSaleAmountLabel;
    private TextView cardCountLabel;
    private TextView cardAmountLabel;
    private TextView subjectSlipLabel;

    private LinearLayout summaryLinearLayout;
    private LinearLayout GHCsummaryLinearLayout;    // Paul_20181202
    private Dialog dialogHost;
    private String typeClick;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private Dialog dialogMenuHostTwo;

    private Dialog dialogMenuHostTaxType;

    /***
     * Report Tax Detail
     */

    private TextView detailLabel;
    private View reportTaxView;
    private TextView taxIdLabel;
    private TextView batchIdLabel;
    private TextView hostTaxLabel;
    private TextView dateTaxLabel;
    private TextView timeTaxLabel;
    private RecyclerView recyclerViewReportTaxDetail;
    private ReportTaxDetailAdapter reportTaxDetailAdapter;
    private ArrayList<TransTemp> taxList = null;
    private ArrayList<QrCode> taxAliList = null;        // Paul_20181218
    private LinearLayout reportTaxDetailLinearLayout;
    private TextView countFeeLabel;
    private TextView totalFeeLabel;
    private TextView countVoidFeeLabel;
    private TextView totalVoidFeeLabel;
    private TextView countGrandLabel;
    private TextView totalCountGrandLabel;

    private Dialog dialogHostSummary;
    private Button posSummaryBtn;
    private Button epsSummaryBtn;
    //    private ImageView closeSummaryImage;//K.GAME 180828 change dialog UI
    private Button closeSummaryImage;//K.GAME 180828 change dialog UI
    private Button taxKtbOffUsSummaryBtn;
    private Button taxBase24EpsSummaryBtn;
    private Button tmsSummaryBtn;
    private Dialog dialogLoading;
    private TextView countReportLabel;
    private TextView amountReportLabel;
    private Button qrSummaryBtn;
    private Button aliSummaryBtn;
    private Button wechatSummaryBtn;
    /**
     * Slip QR
     */
    private View reportViewQr;
    private RecyclerView recyclerViewReportDetailQr;
    private LinearLayout reportDetailLinearLayoutQr;
    private RecyclerView recyclerViewReportDetailAlipay;    // Paul_20181120
    private LinearLayout reportDetailLinearLayoutAlipay;    // Paul_20181120
    private TextView countReportLabelQr;
    private TextView amountReportLabelQr;
    private TextView countReportLabelAli;
    private TextView amountReportLabelAli;
    private TextView dateLabelQr;
    private TextView timeLabelQr;
    private TextView midLabelQr;
    private TextView tidLabelQr;
    private TextView batchLabelQr;
    private TextView hostLabelQr;
    private SlipQrReportAdapter slipQrReportAdapter;
    private SlipAlipayReportAdapter slipAlipayReportAdapter;
    private TextView dateLabelQQr;
    private TextView timeLabelQQr;
    private TextView midLabelQQr;
    private TextView tidLabelQQr;
    private TextView batchLabelQQr;
    private TextView hostLabelQQr;

    private ReportAliTaxDetailAdapter reportAlipayTaxDetailAdapter;   // Paul_20181219

    private ArrayList<QrCode> qrCodeList = null;
    private View reportSummaryViewQr;
    private LinearLayout summaryLinearLayoutSmQr;
    private TextView merchantName1LabelSmQr;
    private TextView merchantName2LabelSmQr;
    private TextView merchantName3LabelSmQr;
    private TextView dateLabelSmQr;
    private TextView timeLabelSmQr;
    private TextView midLabelSmQr;
    private TextView tidLabelSmQr;
    private TextView batchLabelSmQr;
    private TextView hostLabelSmQr;
    private TextView saleCountLabelSmQr;
    private TextView saleTotalLabelSmQr;
    private TextView voidSaleCountLabelSmQr;
    private TextView voidSaleAmountLabelSmQr;
    private TextView cardCountLabelSmQr;
    private TextView cardAmountLabelSmQr;

    private View reportViewAlipay;
    private View reportSummaryViewAlipay;
    private LinearLayout summaryLinearLayoutSmAlipay;
    private TextView merchantName1LabelSmAlipay;
    private TextView merchantName2LabelSmAlipay;
    private TextView merchantName3LabelSmAlipay;
    private TextView dateLabelSmAlipay;
    private TextView timeLabelSmAlipay;
    private TextView midLabelSmAlipay;
    private TextView tidLabelSmAlipay;
    private TextView batchLabelSmAlipay;
    private TextView hostLabelSmAlipay;
    private TextView saleCountLabelSmAlipay;
    private TextView saleTotalLabelSmAlipay;
    private TextView voidSaleCountLabelSmAlipay;
    private TextView voidSaleAmountLabelSmAlipay;
    private TextView cardCountLabelSmAlipay;
    private TextView cardAmountLabelSmAlipay;


    private NestedScrollView slipNestedScrollViewSmQr;
    private NestedScrollView slipNestedScrollViewSmAlipay;
    private TextView merchantName1ReportLabel;
    private TextView merchantName2ReportLabel;
    private TextView merchantName3ReportLabel;
    private TextView merchantName1TaxLabel;
    private TextView merchantName2TaxLabel;
    private TextView merchantName3TaxLabel;
    private TextView merchantName1QrLabel;
    private TextView merchantName2QrLabel;
    private TextView merchantName3QrLabel;
    private TextView merchantName1AliLabel;
    private TextView merchantName2AliLabel;
    private TextView merchantName3AliLabel;
    private TextView dateReportLabel;
    private TextView timeReportLabel;
    private TextView midReportLabel;
    private TextView tidReportLabel;
    private TextView batchReportLabel;
    private TextView hostReportLabel;

    private ImageView bankImage;  //20180810 SINN Add multilogo
    /**
     * FEE
     */
    private View reportSummaryFeeView;
    private LinearLayout summaryLinearFeeLayout;
    private TextView merchantName1FeeLabel;
    private TextView merchantName2FeeLabel;
    private TextView merchantName3FeeLabel;
    private TextView dateFeeLabel;
    private TextView timeFeeLabel;
    private TextView midFeeLabel;
    private TextView tidFeeLabel;
    private TextView batchFeeLabel;
    private TextView hostFeeLabel;
    private TextView saleCountFeeLabel;
    private TextView saleTotalFeeLabel;
    private TextView voidSaleCountFeeLabel;
    private TextView voidSaleAmountFeeLabel;
    private TextView cardCountFeeLabel;
    private TextView cardAmountFeeLabel;
    private TextView taxIdFeeLabel;
//    private List<SaleOfflineHealthCare> hcOfflineList;        // Paul_20180724
    //K.GAME
    private ImageView img_krungthai1;//K.GAME 181016
    private ImageView img_krungthai2;//K.GAME 181016
    private CardView cardViewDetailBtn;
    private CardView cardViewSumBtn;
    //END K.GAME

    private int checkOff = 0;
    private Button hcSummaryBtn;
    private int inSelect;

    private int OnlyTaxSelect = 0;

    int Count = 0;      // Paul_20180725_OFF
    boolean GhcOnlineFlg = false;   // Paul_20180725_OFF
    int VoidTotalCount = 0;       // Paul_20180725_OFF
    int SaleTotalCount = 0;       // Paul_20180725_OFF
    String gtypeHost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail_report);
        initWidget();
//        initBtnExit(); //K.GAME 180824 change UI
        customDialogMenu();
    }

    @Override
    public void initWidget() {

        //K.GAME 181016 hard code
        img_krungthai1 = findViewById(R.id.img_krungthai1);
        img_krungthai2 = findViewById(R.id.img_krungthai2);
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {

            img_krungthai1.setVisibility(View.INVISIBLE);
            img_krungthai2.setVisibility(View.VISIBLE);
        }//END K.GAME 181016 hard code


//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); //K.GAME 180824 change UI
        gridLayoutManager.setSpanCount(3);//K.GAME 180824 change UI
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);//K.GAME 180824 change UI
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this)
        menuRecyclerView.setLayoutManager(layoutManager);
        reportSummaryView();
        reportGHCSummaryView();     // Paul_20181202
        setMenuList();
        customDialogOutOfPaper();
        customDialogHost();
        customDialogTaxType();
        customDialogHostTwo();

        customDialogHostSummaryReport();
//        customDialogLoading();//K.GAME 180912 change dialog Loading
        customDialogWaiting("กำลังพิมพ์"); //K.GAME 180912 change dialog Loading
        reportView();
        reportViewQr();
        reportViewAlipay();
        reportSummaryViewQr();
        reportSummaryViewAlipay();
        setViewReportTaxDetail();
        reportSummaryFeeView();
    }

    private void reportView() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        reportView = inflater.inflate(R.layout.view_slip_report_detail, null);        // Paul_20181202
        reportView = inflater.inflate(R.layout.view_slip_card_report_detail, null);     // Paul_20181202
        recyclerViewReportDetail = reportView.findViewById(R.id.recyclerViewReportDetail);
        recyclerViewCardReportDetail = reportView.findViewById(R.id.recyclerViewCardReportDetail);  // Paul_20181202
        reportDetailLinearLayout = reportView.findViewById(R.id.reportDetailLinearLayout);

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        recyclerViewReportDetail.setLayoutManager(layoutManager1);

        reportCardDetailLinearLayout = reportView.findViewById(R.id.reportDetailLinearLayout);      // Paul_20181202
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);          // Paul_20181202
        recyclerViewCardReportDetail.setLayoutManager(layoutManager2);                              // Paul_20181202

//        countReportLabel = reportView.findViewById(R.id.countReportLabel);
//        amountReportLabel = reportView.findViewById(R.id.amountReportLabel);
        detailLabel = reportView.findViewById(R.id.detailLabel);
        merchantName1ReportLabel = reportView.findViewById(R.id.merchantName1Label);
        merchantName2ReportLabel = reportView.findViewById(R.id.merchantName2Label);
        merchantName3ReportLabel = reportView.findViewById(R.id.merchantName3Label);
        dateReportLabel = reportView.findViewById(R.id.dateLabel);
        timeReportLabel = reportView.findViewById(R.id.timeLabel);
        midReportLabel = reportView.findViewById(R.id.midLabel);
        tidReportLabel = reportView.findViewById(R.id.tidLabel);
        batchReportLabel = reportView.findViewById(R.id.batchLabel);
        hostReportLabel = reportView.findViewById(R.id.hostLabel);
//
//        bankImage= reportView.findViewById( R.id.hostLabel);  ////20180810 SINN Add multilogo
//        if (Preference.getInstance(this).getValueString( Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
//        {
//            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
//            bankImage.setImageResource(id);
//        }

    }

    private void reportViewQr() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportViewQr = inflater.inflate(R.layout.view_slip_qr_report_detail, null);
        recyclerViewReportDetailQr = reportViewQr.findViewById(R.id.recyclerViewReportDetail);
        reportDetailLinearLayoutQr = reportViewQr.findViewById(R.id.reportDetailLinearLayout);
        slipQrReportAdapter = new SlipQrReportAdapter(this);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        recyclerViewReportDetailQr.setLayoutManager(layoutManager1);
        recyclerViewReportDetailQr.setAdapter(slipQrReportAdapter);
        countReportLabelQr = reportViewQr.findViewById(R.id.countReportLabel);
        amountReportLabelQr = reportViewQr.findViewById(R.id.amountReportLabel);
        dateLabelQQr = reportViewQr.findViewById(R.id.dateLabel);
        timeLabelQQr = reportViewQr.findViewById(R.id.timeLabel);
        midLabelQQr = reportViewQr.findViewById(R.id.midLabel);
        tidLabelQQr = reportViewQr.findViewById(R.id.tidLabel);
        batchLabelQQr = reportViewQr.findViewById(R.id.batchLabel);
        hostLabelQQr = reportViewQr.findViewById(R.id.hostLabel);
        merchantName1QrLabel = reportViewQr.findViewById(R.id.merchantName1Label);
        merchantName2QrLabel = reportViewQr.findViewById(R.id.merchantName2Label);
        merchantName3QrLabel = reportViewQr.findViewById(R.id.merchantName3Label);

//        bankImage= reportView.findViewById( R.id.hostLabel);  ////20180810 SINN Add multilogo
//        if (Preference.getInstance(this).getValueString( Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
//        {
//            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
//            bankImage.setImageResource(id);
//        }

    }

    private void reportViewAlipay() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportViewAlipay = inflater.inflate(R.layout.view_slip_alipay_report_detail, null);
        recyclerViewReportDetailAlipay = reportViewAlipay.findViewById(R.id.recyclerViewReportDetail);  // Paul_20181120
        reportDetailLinearLayoutAlipay = reportViewAlipay.findViewById(R.id.reportDetailLinearLayout);  // Paul_20181120
        slipAlipayReportAdapter = new SlipAlipayReportAdapter(this);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        recyclerViewReportDetailAlipay.setLayoutManager(layoutManager1);                    // Paul_20181120
        recyclerViewReportDetailAlipay.setAdapter(slipAlipayReportAdapter);                 // Paul_20181120
        countReportLabelAli = reportViewAlipay.findViewById(R.id.countReportLabel);
        amountReportLabelAli = reportViewAlipay.findViewById(R.id.amountReportLabel);
        dateLabelQr = reportViewAlipay.findViewById(R.id.dateLabel);
        timeLabelQr = reportViewAlipay.findViewById(R.id.timeLabel);
        midLabelQr = reportViewAlipay.findViewById(R.id.midLabel);
        tidLabelQr = reportViewAlipay.findViewById(R.id.tidLabel);
        batchLabelQr = reportViewAlipay.findViewById(R.id.batchLabel);
        hostLabelQr = reportViewAlipay.findViewById(R.id.hostLabel);
        merchantName1AliLabel = reportViewAlipay.findViewById(R.id.merchantName1Label);
        merchantName2AliLabel = reportViewAlipay.findViewById(R.id.merchantName2Label);
        merchantName3AliLabel = reportViewAlipay.findViewById(R.id.merchantName3Label);
    }

    private void reportSummaryView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSummaryView = inflater.inflate(R.layout.view_silp_report_card_summary, null);      // Paul_20181203

        recyclerViewCardReportSummary = reportSummaryView.findViewById(R.id.recyclerViewCardReportSummary);  // Paul_20181203

        reportCardSummaryLinearLayout = reportSummaryView.findViewById(R.id.summaryLinearLayout);  // Paul_20181203
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);          // Paul_20181202
        recyclerViewCardReportSummary.setLayoutManager(layoutManager2);                              // Paul_20181202

        summaryLinearLayout = reportSummaryView.findViewById(R.id.summaryLinearLayout);
        summarymerchantName1Label = reportSummaryView.findViewById(R.id.merchantName1Label);
        summarymerchantName2Label = reportSummaryView.findViewById(R.id.merchantName2Label);
        summarymerchantName3Label = reportSummaryView.findViewById(R.id.merchantName3Label);
        summarydateLabel = reportSummaryView.findViewById(R.id.dateLabel);
        summarytimeLabel = reportSummaryView.findViewById(R.id.timeLabel);
        summarymidLabel = reportSummaryView.findViewById(R.id.midLabel);
        summarytidLabel = reportSummaryView.findViewById(R.id.tidLabel);
        summarybatchLabel = reportSummaryView.findViewById(R.id.batchLabel);
        summaryhostLabel = reportSummaryView.findViewById(R.id.hostLabel);
        summarycardCountLabel = reportSummaryView.findViewById(R.id.cardCountLabel);
        summarycardAmountLabel = reportSummaryView.findViewById(R.id.cardAmountLabel);
        summarysubjectSlipLabel = reportSummaryView.findViewById(R.id.subjectSlipLabel);
    }

    // Paul_20181202
    private void reportGHCSummaryView() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportGHCSummaryView = inflater.inflate(R.layout.view_silp_report_summary, null);
        GHCsummaryLinearLayout = reportGHCSummaryView.findViewById(R.id.summaryLinearLayout);
        merchantName1Label = reportGHCSummaryView.findViewById(R.id.merchantName1Label);
        merchantName2Label = reportGHCSummaryView.findViewById(R.id.merchantName2Label);
        merchantName3Label = reportGHCSummaryView.findViewById(R.id.merchantName3Label);
        dateLabel = reportGHCSummaryView.findViewById(R.id.dateLabel);
        timeLabel = reportGHCSummaryView.findViewById(R.id.timeLabel);
        midLabel = reportGHCSummaryView.findViewById(R.id.midLabel);
        tidLabel = reportGHCSummaryView.findViewById(R.id.tidLabel);
        batchLabel = reportGHCSummaryView.findViewById(R.id.batchLabel);
        hostLabel = reportGHCSummaryView.findViewById(R.id.hostLabel);
        saleCountLabel = reportGHCSummaryView.findViewById(R.id.saleCountLabel);
        saleTotalLabel = reportGHCSummaryView.findViewById(R.id.saleTotalLabel);
        voidSaleCountLabel = reportGHCSummaryView.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabel = reportGHCSummaryView.findViewById(R.id.voidSaleAmountLabel);
        cardCountLabel = reportGHCSummaryView.findViewById(R.id.cardCountLabel);
        cardAmountLabel = reportGHCSummaryView.findViewById(R.id.cardAmountLabel);
        subjectSlipLabel = reportGHCSummaryView.findViewById(R.id.subjectSlipLabel);

//        bankImage= reportView.findViewById( R.id.bankImage);  ////20180810 SINN Add multilogo
//        if (Preference.getInstance(this).getValueString( Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
//        {
//            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
//            bankImage.setImageResource(id);
//        }

    }

    private void reportSummaryFeeView() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSummaryFeeView = inflater.inflate(R.layout.view_silp_report_fee_summary, null);
        summaryLinearFeeLayout = reportSummaryFeeView.findViewById(R.id.summaryLinearLayout);
        merchantName1FeeLabel = reportSummaryFeeView.findViewById(R.id.merchantName1Label);
        merchantName2FeeLabel = reportSummaryFeeView.findViewById(R.id.merchantName2Label);
        merchantName3FeeLabel = reportSummaryFeeView.findViewById(R.id.merchantName3Label);
        dateFeeLabel = reportSummaryFeeView.findViewById(R.id.dateLabel);
        timeFeeLabel = reportSummaryFeeView.findViewById(R.id.timeLabel);
        midFeeLabel = reportSummaryFeeView.findViewById(R.id.midLabel);
        tidFeeLabel = reportSummaryFeeView.findViewById(R.id.tidLabel);
        batchFeeLabel = reportSummaryFeeView.findViewById(R.id.batchLabel);
        hostFeeLabel = reportSummaryFeeView.findViewById(R.id.hostLabel);
        saleCountFeeLabel = reportSummaryFeeView.findViewById(R.id.saleCountLabel);
        saleTotalFeeLabel = reportSummaryFeeView.findViewById(R.id.saleTotalLabel);
        voidSaleCountFeeLabel = reportSummaryFeeView.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountFeeLabel = reportSummaryFeeView.findViewById(R.id.voidSaleAmountLabel);
        cardCountFeeLabel = reportSummaryFeeView.findViewById(R.id.cardCountLabel);
        cardAmountFeeLabel = reportSummaryFeeView.findViewById(R.id.cardAmountLabel);
        taxIdFeeLabel = reportSummaryFeeView.findViewById(R.id.taxIdLabel);

//        bankImage= reportView.findViewById( R.id.hostLabel);  ////20180810 SINN Add multilogo
//        if (Preference.getInstance(this).getValueString( Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
//        {
//            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
//            bankImage.setImageResource(id);
//        }

    }

    private void reportSummaryViewQr() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSummaryViewQr = inflater.inflate(R.layout.view_silp_qr_report_summary, null);
        slipNestedScrollViewSmQr = reportSummaryViewQr.findViewById(R.id.slipNestedScrollView);
        summaryLinearLayoutSmQr = reportSummaryViewQr.findViewById(R.id.summaryLinearLayout);
        merchantName1LabelSmQr = reportSummaryViewQr.findViewById(R.id.merchantName1Label);
        merchantName2LabelSmQr = reportSummaryViewQr.findViewById(R.id.merchantName2Label);
        merchantName3LabelSmQr = reportSummaryViewQr.findViewById(R.id.merchantName3Label);
        dateLabelSmQr = reportSummaryViewQr.findViewById(R.id.dateLabel);
        timeLabelSmQr = reportSummaryViewQr.findViewById(R.id.timeLabel);
        midLabelSmQr = reportSummaryViewQr.findViewById(R.id.midLabel);
        tidLabelSmQr = reportSummaryViewQr.findViewById(R.id.tidLabel);
        batchLabelSmQr = reportSummaryViewQr.findViewById(R.id.batchLabel);
        hostLabelSmQr = reportSummaryViewQr.findViewById(R.id.hostLabel);
        saleCountLabelSmQr = reportSummaryViewQr.findViewById(R.id.saleCountLabel);
        saleTotalLabelSmQr = reportSummaryViewQr.findViewById(R.id.saleTotalLabel);
        voidSaleCountLabelSmQr = reportSummaryViewQr.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabelSmQr = reportSummaryViewQr.findViewById(R.id.voidSaleAmountLabel);
        cardCountLabelSmQr = reportSummaryViewQr.findViewById(R.id.cardCountLabel);
        cardAmountLabelSmQr = reportSummaryViewQr.findViewById(R.id.cardAmountLabel);

//        bankImage= reportView.findViewById( R.id.hostLabel);  ////20180810 SINN Add multilogo

//        if (Preference.getInstance(this).getValueString( Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
//        {
//            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
//            bankImage.setImageResource(id);
//        }
    }

    private void reportSummaryViewAlipay() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSummaryViewAlipay = inflater.inflate(R.layout.view_silp_alipay_report_summary, null);
        slipNestedScrollViewSmAlipay = reportSummaryViewAlipay.findViewById(R.id.slipNestedScrollView);
        summaryLinearLayoutSmAlipay = reportSummaryViewAlipay.findViewById(R.id.summaryLinearLayout);
        merchantName1LabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.merchantName1Label);
        merchantName2LabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.merchantName2Label);
        merchantName3LabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.merchantName3Label);
        dateLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.dateLabel);
        timeLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.timeLabel);
        midLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.midLabel);
        tidLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.tidLabel);
        batchLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.batchLabel);
        hostLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.hostLabel);
        saleCountLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.saleCountLabel);
        saleTotalLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.saleTotalLabel);
        voidSaleCountLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.voidSaleAmountLabel);
        cardCountLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.cardCountLabel);
        cardAmountLabelSmAlipay = reportSummaryViewAlipay.findViewById(R.id.cardAmountLabel);
    }

    private void setMeasure() {
        reportView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportView.layout(0, 0, reportView.getMeasuredWidth(), reportView.getMeasuredHeight());
    }

    private void setMeasureQr() {
        reportViewQr.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportViewQr.layout(0, 0, reportViewQr.getMeasuredWidth(), reportViewQr.getMeasuredHeight());
    }

    private void setMeasureAlipay() {
        reportViewAlipay.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportViewAlipay.layout(0, 0, reportViewAlipay.getMeasuredWidth(), reportViewAlipay.getMeasuredHeight());
    }

    private void setMeasureSummary() {
        reportSummaryView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSummaryView.layout(0, 0, reportSummaryView.getMeasuredWidth(), reportSummaryView.getMeasuredHeight());
    }

    // Paul_20181202
    private void setMeasureGHCSummary() {
        reportGHCSummaryView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportGHCSummaryView.layout(0, 0, reportGHCSummaryView.getMeasuredWidth(), reportGHCSummaryView.getMeasuredHeight());
    }

    private void setMeasureFeeSummary() {
        reportSummaryFeeView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSummaryFeeView.layout(0, 0, reportSummaryFeeView.getMeasuredWidth(), reportSummaryFeeView.getMeasuredHeight());
    }

    private void setMeasureSummaryQr() {
        reportSummaryViewQr.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSummaryViewQr.layout(0, 0, reportSummaryViewQr.getMeasuredWidth(), reportSummaryViewQr.getMeasuredHeight());
    }

    private void setMeasureSummaryAlipay() {
        reportSummaryViewAlipay.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSummaryViewAlipay.layout(0, 0, reportSummaryViewAlipay.getMeasuredWidth(), reportSummaryViewAlipay.getMeasuredHeight());
    }

    private void setMenuList() {
        if (menuRecyclerView.getAdapter() == null) {
            menuReportAdapter = new MenuReportAdapter(this);
            menuRecyclerView.setAdapter(menuReportAdapter);
            menuReportAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        typeClick = "SummaryReport";
//                        dialogHostSummary.show();
                        //SINN 20181119  AXA no need select host
//                        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1"))
                        if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")||
                                ((Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)&&
                                        (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)&&
                                        (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)&&
                                        (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)))
                            selectSummaryReport("EPS");
                         else
                            dialogHostSummary.show();
                        //END SINN 20181119  AXA no need select host
                    } else if (position == 1) {
                        typeClick = "DetailReport";
//                        dialogHost.show();
                        //SINN 20181119  AXA no need select host
//                        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1"))

                        if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")||
                                ((Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)
                                        &&(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)&&
                                        (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)&&
                                        (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)))
                        selectDetailReport("EPS");
                        else
                            dialogHost.show();
                        //END SINN 20181119  AXA no need select host
                    } else if (position == 2) {
                        typeClick = "TAX";
                        // dialogMenuHostTwo.show();
                        dialogMenuHostTaxType.show();  ////SINN 20180717 Rearrange TAX munu.
                    }
                }
            });
        } else {
            menuReportAdapter.clear();
        }
        if (nameList == null) {
            nameList = new ArrayList<>();
        } else {
            nameList.clear();
        }
        nameList.add("พิมพ์\nยอดรวม");
        nameList.add("พิมพ์\nรายละเอียด");
        //SINN 20181212 TAX report enable by set TAX
        if (Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID).length()>=10) {
            nameList.add("พิมพ์\nรายงานภาษี");
//            nameList.add("TAX");
        }


        menuReportAdapter.setItem(nameList);
        menuReportAdapter.notifyDataSetChanged();
    }

    private void dismissAllDialog() {
        if (dialogMenu != null) {
            if (dialogMenu.isShowing()) {
                dialogMenu.dismiss();
            }
        }
        if (dialogHostSummary != null) {
            if (dialogHostSummary.isShowing()) {
                dialogHostSummary.dismiss();
            }
        }
        if (dialogHost != null) {
            if (dialogHost.isShowing()) {
                dialogHost.dismiss();
            }
        }
        if (dialogMenuHostTwo != null) {
            if (dialogMenuHostTwo.isShowing()) {
                dialogMenuHostTwo.dismiss();
            }
        }
        if (dialogMenuHostTaxType != null) {
            if (dialogMenuHostTaxType.isShowing()) {
                dialogMenuHostTaxType.dismiss();
            }
        }

        if (dialogOutOfPaper != null) {
            if (dialogOutOfPaper.isShowing()) {
                dialogOutOfPaper.dismiss();
            }
        }
        if (dialogLoading != null) {
            if (dialogLoading.isShowing()) {
                dialogLoading.dismiss();
            }
        }
    }

    private void customDialogMenu() {
        dialogMenu = new Dialog(this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogMenu.getLayoutInflater().inflate(R.layout.dialog_custom_menu, null);//K.GAME 180828 change dialog UI
        dialogMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogMenu.setContentView(view);//K.GAME 180828 change dialog UI
        dialogMenu.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogMenu = new Dialog(this);
//        dialogMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogMenu.setCancelable(false);
//        dialogMenu.setContentView(R.layout.dialog_custom_menu);
//        dialogMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogMenu.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogMenu.findViewById(R.id.posBtn);
        epsBtn = dialogMenu.findViewById(R.id.epsBtn);
        tmsBtn = dialogMenu.findViewById(R.id.tmsBtn);
        closeImage = dialogMenu.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenu.dismiss();
            }
        });
    }

    private void selectDetailReport(String typeHost) {
        gtypeHost = typeHost;
        System.out.printf("utility:: %s selectDetailReport = %s \n", TAG, typeHost);
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        if (transTempList == null) {
            transTempList = new ArrayList<>();
        } else {
            transTempList.clear();
        }
        // Paul_20181202
        if (CardTypeDB == null) {
            CardTypeDB = new ArrayList<>();
        } else {
            CardTypeDB.clear();
        }


        // setContentView(R.layout.view_slip_report_detail); //SINN 20181026 no display on screen

        detailLabel.setText("DETAIL REPORT");
        recyclerViewReportDetail.setAdapter(null);
        slipReportAdapter = new SlipReportAdapter(this);
        recyclerViewReportDetail.setAdapter(slipReportAdapter);
        transTempList.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
        slipReportAdapter.setItem(transTempList);
        slipReportAdapter.notifyDataSetChanged();

        Double amountAll = 0.0;
        for (int i = 0; i < transTempList.size(); i++) {
            if (transTempList.get(i).getVoidFlag().equals("N")) {
                amountAll += Double.valueOf(transTempList.get(i).getAmount().replaceAll(",",""));
            }
        }
        Date date = new Date();
        dateReportLabel.setText(new SimpleDateFormat("dd/MM/yy").format(date));
        timeReportLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
        if (typeHost.equalsIgnoreCase("TMS")) {
            midReportLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
            tidReportLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            batchReportLabel.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            hostReportLabel.setText("HOST:KTB ONUS");
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            midReportLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
            tidReportLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            batchReportLabel.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
            hostReportLabel.setText("HOST:WAY4");
        } else if (typeHost.equalsIgnoreCase("GHC")) {
            midReportLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
            tidReportLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
            batchReportLabel.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
            hostReportLabel.setText("HOST:HEALTH CARE");
        } else {
            midReportLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
            tidReportLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            batchReportLabel.setText("BATCH:"+CardPrefix.calLen(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
            hostReportLabel.setText("HOST:KTB OFFUS");
        }

        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

// Paul_20181202 Start
        recyclerViewCardReportDetail.setAdapter(null);
        slipReportCardAdapter = new SlipReportCardAdapter(this);
        recyclerViewCardReportDetail.setAdapter(slipReportCardAdapter);

        if(realm == null)
            realm = Realm.getDefaultInstance();
        RealmResults<TransTemp> transTemp10 = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
        for(int i=0;i<transTemp10.size();i++) {
            String CardTypeHolder =   transTemp10.get(i).getCardTypeHolder();
            System.out.printf("utility:: %s CardTypeHolder = %s \n",TAG,CardTypeHolder);
            String Amount = decimalFormat.format(Double.valueOf(transTemp10.get(i).getAmount()));
            System.out.printf("utility:: %s Amount = %s \n",TAG,Amount);
        }
        String[] CardTypeTempHolder = new String[100];
//        CardTypeTempHolder = null;
        int CardTypeTempCnt=0;

        for(int k=0;k<100;k++)
        {
            CardTypeTempHolder[k] = null;
        }
        for(int i=0;i<transTemp10.size();i++) {
            String CardTypeHolder =   transTemp10.get(i).getCardTypeHolder();
            System.out.printf("utility:: %s CardTypeHolder 99999999 = %s \n",TAG,CardTypeHolder);

            int CheckFlg = 0;
            for(int j=0;(j<CardTypeTempCnt) && (j<100);j++) {
                if(CardTypeTempHolder[j] == null)
                    break;
                if(CardTypeHolder.equalsIgnoreCase(CardTypeTempHolder[j]))
                {
                    CheckFlg = 1;
                    break;
                }
            }
            if(CheckFlg == 0)
            {
                CardTypeTempHolder[CardTypeTempCnt] = CardTypeHolder;
                CardTypeTempCnt++;
//            CardTypeDB.clear();
                CardTypeDB.addAll( realm.where( TransTemp.class ).equalTo( "hostTypeCard", typeHost ).equalTo("CardTypeHolder",CardTypeHolder).findAll() );
                System.out.printf("utility:: %s CardTypeDB.size() = %d \n",TAG,CardTypeDB.size());
//                i = i + CardTypeDB.size() - 1;
                slipReportCardAdapter.setItem( CardTypeDB );
                //            slipReportCardAdapter.notifyDataSetChanged();
            }
//            System.out.printf("utility:: iiiiiiiiiiiii = %d \n",i);
        }
        slipReportCardAdapter.notifyDataSetChanged();
// Paul_20181202 End

        setMeasure();
        dialogLoading.show();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (transTempList.size() > 0) {
// Paul_20180731
                    if (gtypeHost == "TMS") {
                        checkOff = 0;
                    } else {
                        checkOff = 1;
                    }
//                    checkOff = 1;
                    //SINN 20181031 merge KTBNORMAL again.
//                    if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))//SINN 20181122  Merchant support rate KEY_MerchantSupportRate_ID
                    if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1"))
                        checkOff = 0;


                    System.out.printf("utility:: %s doPrinting Befor 005 \n", TAG);
                    //doPrinting(getBitmapFromView(reportDetailLinearLayout));
                } else {
                    checkOff = 0;
                    switch (gtypeHost) {
                        case "GHC":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    selectSummaryReportHc();
                                }
                            });
                            break;
                        default:
                            Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    dialogLoading.dismiss();
                                }
                            });
                            break;
                    }
                }
            }
        }.start();

    }

    private void selectDetailReportHC(String typeHost) {
        System.out.printf("utility:: %s selectDetailReportHC \n", TAG);
        gtypeHost = typeHost;
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        if (transTempList == null) {
            transTempList = new ArrayList<>();
        } else {
            transTempList.clear();
        }
        System.out.printf("utility:: %s selectDetailReportHC 0001 \n", TAG);
        recyclerViewReportDetail.setAdapter(null);
        slipReportAdapter = new SlipReportAdapter(this);
        recyclerViewReportDetail.setAdapter(slipReportAdapter);
        System.out.printf("utility:: %s selectDetailReportHC 0002 \n", TAG);
        if (typeHost.equalsIgnoreCase("GHC")) {
            System.out.printf("utility:: %s selectDetailReportHC 0003 \n", TAG);
            transTempList.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ghcoffFlg", "N").findAll());
        } else {
            transTempList.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
        }
        System.out.printf("utility:: %s selectDetailReportHC 0004 \n", TAG);
        slipReportAdapter.setItem(transTempList);
        slipReportAdapter.notifyDataSetChanged();
        System.out.printf("utility:: %s selectDetailReportHC 0005 \n", TAG);

        detailLabel.setText("DETAIL REPORT");
        Count = 0;
        Double amountAll = 0.0;
        for (int i = 0; i < transTempList.size(); i++) {
            Count++;
            if (transTempList.get(i).getVoidFlag().equals("N")) {
                amountAll += Double.valueOf(transTempList.get(i).getAmount().replaceAll(",",""));
            }
        }
        System.out.printf("utility:: %s selectDetailReportHC 0006 Count = %d \n", TAG, Count);
        Date date = new Date();
        dateReportLabel.setText(new SimpleDateFormat("dd/MM/yy").format(date));
        timeReportLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
        midReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        tidReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        batchReportLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        hostReportLabel.setText("HEALTH CARE");
        System.out.printf("utility:: %s selectDetailReportHC 0007 \n", TAG);


        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

//        countReportLabel.setText(transTempList.size() + "");
        countReportLabel.setText(Count + "");        // Paul_20180725_OFF
        amountReportLabel.setText(decimalFormat.format(amountAll));
        System.out.printf("utility:: %s selectDetailReportHC 0008 \n", TAG);

        setMeasure();
        dialogLoading.show();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
//                if (transTempList.size() > 0)
                if (Count > 0)       // Paul_20180725_OFF
                {
                    System.out.printf("utility:: %s selectDetailReportHC 0009 \n", TAG);
                    GhcOnlineFlg = true;
                    checkOff = 1;
                    System.out.printf("utility:: %s doPrinting Befor 006 \n", TAG);
                    //doPrinting(getBitmapFromView(reportDetailLinearLayout));
                } else {
                    System.out.printf("utility:: %s selectDetailReportHC 0010 \n", TAG);
                    GhcOnlineFlg = false;   // Paul_20180725_OFF
                    checkOff = 0;
                    checkSaleOffGHC();      // Paul_20180726

/*
                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            dialogLoading.dismiss();
                        }
                    });
*/
                }
                System.out.printf("utility:: %s selectDetailReportHC 0011 \n", TAG);
            }
        }.start();
    }

    private void selectSummaryReport(String typeHost) {
        System.out.printf("utility:: %s selectSummaryReport = %s \n", TAG, typeHost);
        gtypeHost = typeHost;

        // Paul_20181202
        if (CardTypeDB == null) {
            CardTypeDB = new ArrayList<>();
        } else {
            CardTypeDB.clear();
        }

        dialogLoading.show();
        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "N").findAll();

        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "Y").findAll();
        totalSale = 0.0;
        totalVoid = 0.0;
        SaleTotalCount = 0;      // Paul_20180724_OFF
        VoidTotalCount = 0;      // Paul_20180724_OFF
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            if (typeHost.equalsIgnoreCase("GHC")) {
                if (transTempSale.get(i).getGhcoffFlg().equalsIgnoreCase("N")) {
                    totalSale += Double.valueOf(transTempSale.get(i).getAmount().replaceAll(",",""));
                    SaleTotalCount++;
                }
            } else {
                totalSale += Double.valueOf(transTempSale.get(i).getAmount().replaceAll(",",""));
                SaleTotalCount++;
            }
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            if (typeHost.equalsIgnoreCase("GHC")) {
                if (transTempSale.get(i).getGhcoffFlg().equalsIgnoreCase("N")) {
                    totalVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",",""));
                    VoidTotalCount++;
                }
            } else {
                totalVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",",""));
                VoidTotalCount++;
            }
        }
        System.out.printf("utility:: SaleTotalCount = %d \n", SaleTotalCount);
        System.out.printf("utility:: VoidTotalCount = %d \n", VoidTotalCount);

        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty()) {
            System.out.printf("utility::AAAAAAAAAAAAAAAAAAAAAAAAa 0000000000000 %s \n",Preference.getInstance( MenuDetailReportActivity.this ).getValueString( Preference.KEY_MERCHANT_1 ));
            summarymerchantName1Label.setText( Preference.getInstance( MenuDetailReportActivity.this ).getValueString( Preference.KEY_MERCHANT_1 ) );
        }
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            summarymerchantName2Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            summarymerchantName3Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        summarydateLabel.setText(dateFormat.format(date));
        summarytimeLabel.setText(timeFormat.format(date));
        System.out.printf("utility:: %s YYYYYYYYYYYYY00001 typeHost = %s \n",TAG,typeHost);
        switch (typeHost) {
            case "POS":
                System.out.printf("utility:: %s YYYYYYYYYYYYY00002 typeHost = %s \n",TAG,typeHost);
                summarymidLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                summarytidLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                summarybatchLabel.setText("BATCH:"+CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)), 6));
                summaryhostLabel.setText("HOST:KTB OFFUS");     // Paul_20181028 Sinn merge version UAT6_0016
                break;
            case "EPS":
                System.out.printf("utility:: %s YYYYYYYYYYYYY00003 typeHost = %s \n",TAG,typeHost);
                summarymidLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                summarytidLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                summarybatchLabel.setText("BATCH:"+CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)), 6));
                summaryhostLabel.setText("HOST:WAY4");//K.GAME UAT4
                break;
            case "GHC":
                System.out.printf("utility:: %s YYYYYYYYYYYYY00004 typeHost = %s \n",TAG,typeHost);
                summarymidLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
                summarytidLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
                summarybatchLabel.setText("BATCH:"+CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC)), 6));
                summaryhostLabel.setText("HOST:HEALTH CARE");
                break;
            default:
                System.out.printf("utility:: %s YYYYYYYYYYYYY00005 typeHost = %s \n",TAG,typeHost);
                summarymidLabel.setText("MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                summarytidLabel.setText("TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                summarybatchLabel.setText("BATCH:"+CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
                summaryhostLabel.setText("HOST:KTB ONUS");
                break;
        }

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

        summarysubjectSlipLabel.setText("SUMMARY REPORT");

        countAll = transTempSale.size() + transTempVoid.size();
        summarycardCountLabel.setText(countAll + "");
        summarycardAmountLabel.setText(decimalFormat.format(totalSale));

        recyclerViewCardReportSummary.setAdapter(null);
        slipSummaryReportCardAdapter = new SlipSummaryReportCardAdapter(this);
        recyclerViewCardReportSummary.setAdapter(slipSummaryReportCardAdapter);

        if(realm == null)
            realm = Realm.getDefaultInstance();
        RealmResults<TransTemp> transTemp10 = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
        for(int i=0;i<transTemp10.size();i++) {
            String CardTypeHolder =   transTemp10.get(i).getCardTypeHolder();
            System.out.printf("utility:: %s CardTypeHolder = %s \n",TAG,CardTypeHolder);
            String Amount = decimalFormat.format(Double.valueOf(transTemp10.get(i).getAmount()));
            System.out.printf("utility:: %s Amount = %s \n",TAG,Amount);
        }
        String[] CardTypeTempHolder = new String[100];
//        CardTypeTempHolder = null;
        int CardTypeTempCnt=0;

        for(int k=0;k<100;k++)
        {
            CardTypeTempHolder[k] = null;
        }
        for(int i=0;i<transTemp10.size();i++) {
            String CardTypeHolder =   transTemp10.get(i).getCardTypeHolder();
            System.out.printf("utility:: %s CardTypeHolder 99999999 = %s \n",TAG,CardTypeHolder);

            int CheckFlg = 0;
            for(int j=0;(j<CardTypeTempCnt) && (j<100);j++) {
                if(CardTypeTempHolder[j] == null)
                    break;
                if(CardTypeHolder.equalsIgnoreCase(CardTypeTempHolder[j]))
                {
                    CheckFlg = 1;
                    break;
                }
            }
            if(CheckFlg == 0)
            {
                CardTypeTempHolder[CardTypeTempCnt] = CardTypeHolder;
                CardTypeTempCnt++;
                CardTypeDB.addAll( realm.where( TransTemp.class ).equalTo( "hostTypeCard", typeHost ).equalTo("CardTypeHolder",CardTypeHolder).findAll() );
                System.out.printf("utility:: %s CardTypeDB.size() = %d \n",TAG,CardTypeDB.size());
                slipSummaryReportCardAdapter.setItem( CardTypeDB );
            }
//            for(int j=0;(j<CardTypeTempCnt) && (j<100);j++) {
//                if(CardTypeTempHolder[j] == null)
//                    break;
//                if(CardTypeHolder.equalsIgnoreCase(CardTypeTempHolder[j]))
//                {
//                    CheckFlg = 1;
//                    break;
//                }
//            }
//            if(CheckFlg == 0)
//            {
//                CardTypeTempHolder[CardTypeTempCnt] = CardTypeHolder;
//                CardTypeTempCnt++;
//                CardTypeDB.addAll( realm.where( TransTemp.class ).equalTo( "hostTypeCard", typeHost ).equalTo("CardTypeHolder",CardTypeHolder).findAll() );
//                System.out.printf("utility:: %s CardTypeDB.size() = %d \n",TAG,CardTypeDB.size());
//                slipSummaryReportCardAdapter.setItem( CardTypeDB );
//            }
        }
        slipSummaryReportCardAdapter.notifyDataSetChanged();
// Paul_20181202 End

        setMeasureSummary();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (SaleTotalCount > 0 || VoidTotalCount > 0) {
                    GhcOnlineFlg = true;
// Paul_20180731
                    if (gtypeHost == "TMS") {
                        checkOff = 0;
                    } else {
                        checkOff = 2;
                    }


//                    if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))//SINN 20181122  Merchant support rate KEY_MerchantSupportRate_ID
                    if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1"))
                    checkOff = 0;


//                    customDialogSelect_report_summary();//K.GAME 180912 dialog confirm Print report
                    System.out.printf("utility:: %s doPrinting Befor 008 \n", TAG);
                    //doPrinting(getBitmapFromView(summaryLinearLayout));
                    //2018 SINN reset summary offline amount
                    totalSale = 0.0;
                    totalVoid = 0.0;
//                    if(typeHost.equalsIgnoreCase("GHC"))
//                        subjectSlipLabel.setText("SUMMARY OFFLINE REPORT");

                } else {
                    GhcOnlineFlg = false;
                    checkOff = 0;
                    switch (gtypeHost) {
                        case "GHC":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    selectSummaryReportHc();
                                }
                            });
                            break;
                        default:
                            Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    dialogLoading.dismiss();
                                }
                            });
                            break;
                    }
//                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                            dialogLoading.dismiss();
//                        }
//                    });
                }
            }
        }.start();

    }

    // Paul_20181202
    private void selectGHCSummaryReport() {
        System.out.printf("utility:: %s selectSummaryReport = %s \n", TAG, "GHC");
        gtypeHost = "GHC";

        dialogLoading.show();
        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", gtypeHost).equalTo("voidFlag", "N").findAll();

        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", gtypeHost).equalTo("voidFlag", "Y").findAll();
        totalSale = 0.0;
        totalVoid = 0.0;
        SaleTotalCount = 0;      // Paul_20180724_OFF
        VoidTotalCount = 0;      // Paul_20180724_OFF
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            if (gtypeHost.equalsIgnoreCase("GHC")) {
                if (transTempSale.get(i).getGhcoffFlg().equalsIgnoreCase("N")) {
                    totalSale += Double.valueOf(transTempSale.get(i).getAmount().replaceAll(",",""));
                    SaleTotalCount++;
                }
            } else {
                totalSale += Double.valueOf(transTempSale.get(i).getAmount().replaceAll(",",""));
                SaleTotalCount++;
            }
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            if (gtypeHost.equalsIgnoreCase("GHC")) {
                if (transTempSale.get(i).getGhcoffFlg().equalsIgnoreCase("N")) {
                    totalVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",",""));
                    VoidTotalCount++;
                }
            } else {
                totalVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",",""));
                VoidTotalCount++;
            }
        }
        System.out.printf("utility:: SaleTotalCount = %d \n", SaleTotalCount);
        System.out.printf("utility:: VoidTotalCount = %d \n", VoidTotalCount);

        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateLabel.setText(dateFormat.format(date));
        timeLabel.setText(timeFormat.format(date));
        switch (gtypeHost) {
            case "POS":
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)), 6));
//                hostLabel.setText("KTB Off US");//K.GAME 181012
                hostLabel.setText("KTB OFFUS");     // Paul_20181028 Sinn merge version UAT6_0016
                break;
            case "EPS":
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)), 6));
                hostLabel.setText("WAY4");//K.GAME UAT4
                break;
            case "GHC":
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC)), 6));
                hostLabel.setText("HEALTH CARE");
                break;
            default:
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
//                hostLabel.setText("KTB ON US");//K.GAME 181012
                hostLabel.setText("KTB ONUS");
                break;
        }

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

//        detailLabel.setText("SUMMARY REPORT");
        subjectSlipLabel.setText("SUMMARY REPORT");

        saleCountLabel.setText(String.valueOf(SaleTotalCount));   // Paul_20180724_OFF
        saleTotalLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountLabel.setText(VoidTotalCount + "");
        voidSaleAmountLabel.setText(decimalFormat.format(totalVoid));
        countAll = SaleTotalCount + VoidTotalCount;     // Paul_20180724_OFF
        cardCountLabel.setText(countAll + "");
        cardAmountLabel.setText(decimalFormat.format(totalSale));

        setMeasureGHCSummary();     // Paul_20181202
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (SaleTotalCount > 0 || VoidTotalCount > 0) {
                    GhcOnlineFlg = true;
// Paul_20180731
                    if (gtypeHost == "TMS") {
                        checkOff = 0;
                    } else {
                        checkOff = 2;
                    }


//                    if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))//SINN 20181122  Merchant support rate KEY_MerchantSupportRate_ID
                    if(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1"))
                        checkOff = 0;


//                    customDialogSelect_report_summary();//K.GAME 180912 dialog confirm Print report
                    System.out.printf("utility:: %s doPrinting Befor 008 \n", TAG);
                    //doPrinting(getBitmapFromView(GHCsummaryLinearLayout));      // Paul_20181202
                    //2018 SINN reset summary offline amount
                    totalSale = 0.0;
                    totalVoid = 0.0;
//                    if(typeHost.equalsIgnoreCase("GHC"))
//                        subjectSlipLabel.setText("SUMMARY OFFLINE REPORT");

                } else {
                    GhcOnlineFlg = false;
                    checkOff = 0;
                    switch (gtypeHost) {
                        case "GHC":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    selectSummaryReportHc();
                                }
                            });
                            break;
                        default:
                            Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    dialogLoading.dismiss();
                                }
                            });
                            break;
                    }
//                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                            dialogLoading.dismiss();
//                        }
//                    });
                }
            }
        }.start();

    }

    public void customDialogSelect_report_summary() {
        final Dialog dialogAlert = new Dialog(this);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(R.layout.dialog_custom_select_report_summary);
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        dialogAlert.setCancelable(true);
        dialogAlert.setCanceledOnTouchOutside(true);


//        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button okBtn = dialogAlert.findViewById(R.id.okBtn);
        Button cancelBtn = dialogAlert.findViewById(R.id.CancelBtn);
        //okBtn.setText("*");

//        if (msg != null) {
//            msgLabel.setText(msg);
//        }

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuDetailReportActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuDetailReportActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
        dialogAlert.show();//ใส่เพิ่ม
    }

    private void selectSummaryReportHc() {
        gtypeHost = "GHC";
        System.out.printf("utility:: %s selectSummaryReportHc \n", TAG);
        dialogLoading.show();
        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").equalTo("ghcoffFlg", "Y").equalTo("voidFlag", "N").findAll();
        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").equalTo("ghcoffFlg", "Y").equalTo("voidFlag", "Y").findAll();

        totalSale = 0.0;
        totalVoid = 0.0;
        SaleTotalCount = 0;      // Paul_20180724_OFF
        VoidTotalCount = 0;      // Paul_20180724_OFF
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
//            if (transTempSale.get( i ).getGhcoffFlg().equalsIgnoreCase( "Y" )) {
            totalSale += Double.valueOf(transTempSale.get(i).getAmount().replaceAll(",",""));
            SaleTotalCount++;
//            }
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
//            if(transTempVoid.get( i ).getGhcoffFlg().equalsIgnoreCase( "Y" ))
//            {
            totalVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",",""));
            VoidTotalCount++;
//            }
        }

        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateLabel.setText(dateFormat.format(date));
        timeLabel.setText(timeFormat.format(date));
        midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC)), 6));
        hostLabel.setText("HEALTH CARE");

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

//        detailLabel.setText("SUMMARY REPORT OFFLINE");
        subjectSlipLabel.setText("SUMMARY OFFLINE REPORT");

        saleCountLabel.setText(String.valueOf(SaleTotalCount));   // Paul_20180724_OFF
        saleTotalLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountLabel.setText(VoidTotalCount + "");
        voidSaleAmountLabel.setText(decimalFormat.format(totalVoid));
        countAll = SaleTotalCount + VoidTotalCount;     // Paul_20180724_OFF
        cardCountLabel.setText(countAll + "");
        cardAmountLabel.setText(decimalFormat.format(totalSale));

        setMeasureGHCSummary();     // Paul_20181202
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (SaleTotalCount > 0 || VoidTotalCount > 0) {
                    checkOff = 0;
//                    customDialogSelect_report_summary();//K.GAME 180912 dialog confirm Print report
                    System.out.printf("utility:: %s doPrinting Befor 009 \n", TAG);
                    //doPrinting(getBitmapFromView(GHCsummaryLinearLayout));      // Paul_20181202
                    //2018 SINN reset summary offline amount
                    totalSale = 0.0;
                    totalVoid = 0.0;

                } else {
//                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                            dialogLoading.dismiss();
//                        }
//                    });
                    dialogLoading.dismiss();//K.GAME 180912 แก้ไข ปิดการแจ้งเตือน ไม่มีข้อมูล ใน ออฟไลน์
                }
            }
        }.start();

    }

    private void selectSummaryTAXReport(String typeHost) {
        gtypeHost = typeHost;
        System.out.printf("utility:: %s selectSummaryTAXReport = %s \n", TAG, typeHost);
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "N").findAll();

        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "Y").findAll();
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            totalSale += Double.valueOf(transTempSale.get(i).getFee());
        }

        if (typeHost.equalsIgnoreCase("POS")) {
            hostFeeLabel.setText("KTB OFFUS");
        } else {
            hostFeeLabel.setText("WAY4");
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            totalVoid += Double.valueOf(transTempVoid.get(i).getFee());
        }
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1FeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2FeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3FeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFeeLabel.setText(dateFormat.format(date));
        timeFeeLabel.setText(timeFormat.format(date));
//
//        switch (typeHost) {
//            case "POS":
//                batchFeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_POS));
//                break;
//            case "EPS":
//                batchFeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_EPS));
//                break;
//            default:
//                batchFeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_TMS));
//
//        }

        switch (typeHost) {
            case "POS":
                batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)), 6));
                break;
            case "EPS":
                batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)), 6));
                break;
            case "GHC":
                batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC)), 6));
                break;
            default:
                batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
                break;
        }

        /*switch (typeHost) {
            case "POS":
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)), 6));
                hostLabel.setText("KTB Off US");
                break;
            case "EPS":
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)), 6));
                hostLabel.setText("WAY4");
                break;
            default:
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
                hostLabel.setText("KTB ONUS");
                break;
        }*/
        taxIdFeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TAX_ID));
        saleCountFeeLabel.setText(String.valueOf(transTempSale.size()));
        saleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountFeeLabel.setText(transTempVoid.size() + "");
        voidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        cardCountFeeLabel.setText(countAll + "");
        cardAmountFeeLabel.setText(decimalFormat.format(totalSale));

        setMeasureFeeSummary();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (transTempSale.size() > 0 || transTempVoid.size() > 0) {
                    checkOff = 0;
//                    customDialogSelect_report_summary();//K.GAME 180912 dialog confirm Print report
                    System.out.printf("utility:: %s doPrinting Befor 010 \n",TAG);
                    //doPrinting(getBitmapFromView(summaryLinearFeeLayout));
                } else {
                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            dialogLoading.dismiss();
                        }
                    });
                }
            }
        }.start();

    }


    private void selectSummaryQrReport() {
        gtypeHost = "QR";
        System.out.printf("utility:: %s selectSummaryQrReport \n", TAG);
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        final RealmResults<QrCode> qrCodes = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();

        Log.d(TAG, "selectSummaryReport: " + qrCodes.size());
        for (int i = 0; i < qrCodes.size(); i++) {
            totalSale += Double.valueOf(qrCodes.get(i).getAmount().replaceAll(",",""));
        }

        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1LabelSmQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2LabelSmQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3LabelSmQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateLabelSmQr.setText(dateFormat.format(date));
        timeLabelSmQr.setText(timeFormat.format(date));

        String qr_mid = "MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_MERCHANT_ID);
        String qr_tid = "TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID);
        String qr_batch = "BATCH:"+CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER)), 6);

        midLabelSmQr.setText(qr_mid);
        tidLabelSmQr.setText(qr_tid);
        batchLabelSmQr.setText(qr_batch);
        hostLabelSmQr.setText("HOST: QR");

        saleCountLabelSmQr.setText(qrCodes.size() + "");
        saleTotalLabelSmQr.setText(decimalFormat.format(totalSale));
        voidSaleCountLabelSmQr.setText("0");
        voidSaleAmountLabelSmQr.setText(decimalFormat.format(totalVoid));
        cardCountLabelSmQr.setText(qrCodes.size() + "");
        cardAmountLabelSmQr.setText(decimalFormat.format(totalSale));

        setMeasureSummaryQr();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (qrCodes.size() > 0) {
                    checkOff = 0;
//                    customDialogSelect_report_summary();//K.GAME 180912 dialog confirm Print report
                    System.out.printf("utility:: %s doPrinting Befor 007 \n", TAG);
                    //doPrinting(getBitmapFromView(summaryLinearLayoutSmQr));
                } else {
                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            dialogLoading.dismiss();
                        }
                    });
                }
            }
        }.start();

    }

    private void selectSummaryAliReport(String type) {
        gtypeHost = type;
        System.out.printf("utility:: %s selectSummaryAliReport \n", TAG);
        dialogLoading.show();

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

        totalSale = 0.00;       // Paul_20181218
        totalVoid = 0.00;       // Paul_20181218
        RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
        for (int i = 0; i < saleTemp.size(); i++) {
            totalSale += Double.valueOf(saleTemp.get(i).getAmt().replaceAll(",",""));
/*
            if(saleTemp.get(i).getAmtplusfee().equals("null"))
                totalSale += Double.valueOf(saleTemp.get(i).getAmt().replaceAll(",",""));
            else
                totalSale += Double.valueOf(saleTemp.get(i).getAmtplusfee().replaceAll(",",""));
*/
        }

        RealmResults<QrCode> voidTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
        for (int i = 0; i < voidTemp.size(); i++) {
            totalVoid += Double.valueOf(voidTemp.get(i).getAmt().replaceAll(",",""));
/*
            if(voidTemp.get(i).getAmtplusfee().equals("null"))
                totalVoid += Double.valueOf(voidTemp.get(i).getAmt().replaceAll(",",""));
            else
                totalVoid += Double.valueOf(voidTemp.get(i).getAmtplusfee().replaceAll(",",""));
*/
        }

        checkOff = 2;       // Paul_20181218 summary report

        if(saleTemp == null){
            cntSale = "0";
            totalSale = 0.00;
        }else
            cntSale = String.valueOf(saleTemp.size());

        if(voidTemp == null){
            cntVoid = "0";
            totalVoid = 0.00;
        }else
            cntVoid = String.valueOf(voidTemp.size());

        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1LabelSmAlipay.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2LabelSmAlipay.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3LabelSmAlipay.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateLabelSmAlipay.setText(dateFormat.format(date));
        timeLabelSmAlipay.setText(timeFormat.format(date));

        if(type.equals("ALIPAY")){
            midLabelSmAlipay.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
            tidLabelSmAlipay.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
            batchLabelSmAlipay.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER)), 6));
            hostLabelSmAlipay.setText("ALIPAY");
        }else{
            midLabelSmAlipay.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
            tidLabelSmAlipay.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
            batchLabelSmAlipay.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER)), 6));
            hostLabelSmAlipay.setText("WECHAT PAY");    // Paul_20190324
        }

        saleCountLabelSmAlipay.setText(cntSale + "");
        voidSaleCountLabelSmAlipay.setText(cntVoid + "");
        saleTotalLabelSmAlipay.setText(decimalFormat.format(totalSale));
        voidSaleAmountLabelSmAlipay.setText(decimalFormat.format(totalVoid));
        cardCountLabelSmAlipay.setText(String.valueOf(Integer.parseInt(cntSale) + Integer.parseInt(cntVoid)) + "");
        cardAmountLabelSmAlipay.setText(decimalFormat.format(totalSale));

        setMeasureSummaryAlipay();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if ((Integer.parseInt(cntSale) + Integer.parseInt(cntVoid))> 0) {
//                    checkOff = 0;
//                    customDialogSelect_report_summary();//K.GAME 180912 dialog confirm Print report
//                    System.out.printf("utility:: %s doPrinting Befor 007 \n", TAG);
                    //doPrinting(getBitmapFromView(summaryLinearLayoutSmAlipay));
                } else {
                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            dialogLoading.dismiss();
                        }
                    });
                }
            }
        }.start();

    }

    private void selectSummaryAliTAXReport(String typeHost) {
        gtypeHost = typeHost;
        System.out.printf("utility:: %s selectSummaryAliTAXReport typeHost = %s\n", TAG,typeHost);
        dialogLoading.show();

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

        totalSale = 0.00;
        totalVoid = 0.00;
        Double TotalAmount = 0.0;
        RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("hostTypeCard", gtypeHost).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
        for (int i = 0; i < saleTemp.size(); i++) {
            if(!saleTemp.get(i).getFee().equals("null"))
                totalSale += Double.valueOf(saleTemp.get(i).getFee().replaceAll(",",""));
/*
            if(saleTemp.get(i).getAmtplusfee().equals("null"))
                totalSale += Double.valueOf(saleTemp.get(i).getAmt().replaceAll(",",""));
            else
                totalSale += Double.valueOf(saleTemp.get(i).getAmtplusfee().replaceAll(",",""));
*/
            if(!saleTemp.get(i).getAmt().equals("null"))                      // Paul_20190201
                TotalAmount += Double.valueOf(saleTemp.get(i).getAmt());
        }

        RealmResults<QrCode> voidTemp = realm.where(QrCode.class).equalTo("hostTypeCard", gtypeHost).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
        for (int i = 0; i < voidTemp.size(); i++) {
            if(!voidTemp.get(i).getFee().equals("null"))
                totalVoid += Double.valueOf(voidTemp.get(i).getFee().replaceAll(",",""));
/*
            if(voidTemp.get(i).getAmtplusfee().equals("null"))
                totalVoid += Double.valueOf(voidTemp.get(i).getAmt().replaceAll(",",""));
            else
                totalVoid += Double.valueOf(voidTemp.get(i).getAmtplusfee().replaceAll(",",""));
*/
            if(!voidTemp.get(i).getAmt().equals("null"))                      // Paul_20190201
                TotalAmount += Double.valueOf(voidTemp.get(i).getAmt());
        }

        if(saleTemp == null){
            cntSale = "0";
            totalSale = 0.00;
        }else
            cntSale = String.valueOf(saleTemp.size());

        if(voidTemp == null){
            cntVoid = "0";
            totalVoid = 0.00;
        }else
            cntVoid = String.valueOf(voidTemp.size());
        if((totalSale + totalVoid) == 0)        // Paul_20190201
        {
            if(TotalAmount == 0 || OnlyTaxSelect == 1) {
                Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                        dialogLoading.dismiss();
                    }
                });
            }
            else {
                // Paul_20190215
//                dialogLoading.dismiss();
                dismissAllDialog();
                Intent intent = new Intent(MenuDetailReportActivity.this, MenuServiceListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
            return;
        }

        if (gtypeHost.equalsIgnoreCase("ALIPAY")) {
            hostFeeLabel.setText("ALIPAY");
        } else {
            hostFeeLabel.setText("WECHAT PAY");     // Paul_20190324
        }
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1FeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2FeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3FeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFeeLabel.setText(dateFormat.format(date));
        timeFeeLabel.setText(timeFormat.format(date));

        if(gtypeHost.equals("ALIPAY")){
            midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
            tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
            batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER)), 6));
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER)), 6));
            hostLabel.setText("ALIPAY");
        }else{
            midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
            tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
            batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER)), 6));
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER)), 6));
            hostLabel.setText("WECHAT PAY");    // Paul_20190324
        }

        taxIdFeeLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TAX_ID));
        saleCountFeeLabel.setText(String.valueOf(saleTemp.size()));
        saleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountFeeLabel.setText(voidTemp.size() + "");
        voidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = saleTemp.size() + voidTemp.size();
        cardCountFeeLabel.setText(countAll + "");
        cardAmountFeeLabel.setText(decimalFormat.format(totalSale));

        setMeasureFeeSummary();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.printf("utility:: %s selectSummaryAliTAXReport onTick \n",TAG);
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if ((Integer.parseInt(cntSale) + Integer.parseInt(cntVoid))> 0) {
                    System.out.printf("utility:: %s selectSummaryAliTAXReport onFinish \n",TAG);
                    checkOff = 0;       // Paul_20190215
//                    customDialogSelect_report_summary();//K.GAME 180912 dialog confirm Print report
//                    System.out.printf("utility:: %s doPrinting Befor 007 \n", TAG);
                    //doPrinting(getBitmapFromView(summaryLinearFeeLayout));
                } else {
                    Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            dialogLoading.dismiss();
                        }
                    });
                }
            }
        }.start();

    }

    private void customDialogHost() {
        dialogHost = new Dialog(this, R.style.ThemeWithCorners);
        View view = dialogHost.getLayoutInflater().inflate(R.layout.dialog_custom_host_qr, null);
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHost.setContentView(view);
        dialogHost.setCancelable(false);

        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
        qrBtn = dialogHost.findViewById(R.id.qrBtn);
        ghcBtn = dialogHost.findViewById(R.id.ghcBtn);
        aliBtn = dialogHost.findViewById(R.id.aliBtn);
        wechatBtn = dialogHost.findViewById(R.id.wechatBtn);
        closeImage = dialogHost.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.dismiss();
            }
        });

//SINN 20181119  AXA no need select host
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)
            tmsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
            posBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length()<8)
            epsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)
            ghcBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)
            qrBtn.setVisibility(View.GONE);
//END SINN 20181119  AXA no need select host

        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
            aliBtn.setVisibility(View.VISIBLE);
        }

        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
            wechatBtn.setVisibility(View.VISIBLE);
        }

        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (typeClick) {
                    case "DetailReport":
                        selectDetailReport("POS");
                        break;
                    case "SummaryReport":
                        selectSummaryReport("POS");
                        break;
                    default:

                        break;
                }
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (typeClick) {
                    case "DetailReport":
                        selectDetailReport("EPS");
                        break;
                    case "SummaryReport":
                        selectSummaryReport("EPS");
                        break;
                    default:
                        dialogMenuHostTwo.show();
                        break;
                }
            }
        });

        tmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (typeClick) {
                    case "DetailReport":
                        selectDetailReport("TMS");
                        break;
                    case "SummaryReport":
                        selectSummaryReport("TMS");
                        break;
                    default:

                        break;
                }
            }
        });
        ghcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (typeClick) {
                    case "DetailReport":
                        checkOff = 1;
                        selectDetailReportHC("GHC");
                        break;
                    case "SummaryReport":
                        checkOff = 1;
//                        selectSummaryReport("GHC");
                        selectGHCSummaryReport();       // Paul_20181202
                        break;
                    default:

                        break;
                }
            }
        });
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQrReport();
            }
        });

        aliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gtypeHost = "ALIPAY";
                getAliReport(gtypeHost);
            }
        });

        wechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gtypeHost = "WECHAT";
                getAliReport(gtypeHost);
            }
        });
    }

    private void customDialogTaxType() {
//        dialogMenuHostTaxType = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180828 change dialog UI
//        View view = dialogMenuHostTaxType.getLayoutInflater().inflate(R.layout.dialog_custom_tax_select, null);//K.GAME 180828 change dialog UI
//        dialogMenuHostTaxType.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
//        dialogMenuHostTaxType.setContentView(view);//K.GAME 180828 change dialog UI
//        dialogMenuHostTaxType.setCancelable(false);//K.GAME 180828 change dialog UI

        dialogMenuHostTaxType = new Dialog(this);
        dialogMenuHostTaxType.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMenuHostTaxType.setContentView(R.layout.dialog_custom_tax_select);
        dialogMenuHostTaxType.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMenuHostTaxType.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        cardViewDetailBtn = dialogMenuHostTaxType.findViewById(R.id.DetailBtn);
        cardViewSumBtn = dialogMenuHostTaxType.findViewById(R.id.SumBtn);

        //K.GAME 181018 hard code
        ImageView img_krungthai1 = dialogMenuHostTaxType.findViewById(R.id.img_krungthai1);
        ImageView img_krungthai2 = dialogMenuHostTaxType.findViewById(R.id.img_krungthai2);
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {

            img_krungthai1.setVisibility(View.INVISIBLE);
            img_krungthai2.setVisibility(View.VISIBLE);
        }//END K.GAME 181018 hard code

        cardViewDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inSelect = 0;
//                dialogMenuHostTwo.show();
                //SINN 20181213 by pass  TAX select when only EPS
               // if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
         if ((Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("0")&&
              Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("0")&&
              Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
                        )
                    getDatabaseTax("EPS");
                 else
                dialogMenuHostTwo.show();
            }
        });
        cardViewSumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inSelect = 1;
//                dialogMenuHostTwo.show();
                //SINN 20181213 by pass  TAX select when only EPS
                //if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
                if ((Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("0")&&
                        Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("0")&&
                        Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
                        )
                    selectSummaryTAXReport("EPS");
                else
                    dialogMenuHostTwo.show();
            }
        });
    }


    private void customDialogHostTwo() {
        dialogMenuHostTwo = new Dialog(this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogMenuHostTwo.getLayoutInflater().inflate(R.layout.dialog_custom_host_2, null);//K.GAME 180828 change dialog UI
        dialogMenuHostTwo.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogMenuHostTwo.setContentView(view);//K.GAME 180828 change dialog UI
        dialogMenuHostTwo.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogMenuHostTwo = new Dialog(this);
//        dialogMenuHostTwo.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogMenuHostTwo.setCancelable(false);
//        dialogMenuHostTwo.setContentView(R.layout.dialog_custom_host_2);
//        dialogMenuHostTwo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogMenuHostTwo.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogMenuHostTwo.findViewById(R.id.posBtn);
        epsBtn = dialogMenuHostTwo.findViewById(R.id.epsBtn);
        closeImage = dialogMenuHostTwo.findViewById(R.id.closeImage);

        aliBtn  =dialogMenuHostTwo.findViewById(R.id.AlipayBtn);
        wechatBtn =dialogMenuHostTwo.findViewById(R.id.WechatBtn);

        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("0")) {
            aliBtn.setVisibility(View.GONE);
        }

        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("0")) {
            wechatBtn.setVisibility(View.GONE);
        }
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
            posBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length()<8)
            epsBtn.setVisibility(View.GONE);

        // Paul_20181219
        aliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlyTaxSelect = 1;      // Paul_20190202
                gtypeHost = "ALIPAY";
                if (inSelect == 0)
                    getDatabaseAliTax("ALIPAY");
                else
                    selectSummaryAliTAXReport("ALIPAY");
            }
        });

        // Paul_20181219
        wechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlyTaxSelect = 1;      // Paul_20190202
                gtypeHost = "WECHAT";
                if (inSelect == 0)
                    getDatabaseAliTax("WECHAT");    // Paul_20190324
                else
                    selectSummaryAliTAXReport("WECHAT");    // Paul_20190324
            }
        });

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenuHostTwo.dismiss();
            }
        });

        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inSelect == 0)
                    getDatabaseTax("POS");
                else
                    selectSummaryTAXReport("POS");


            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inSelect == 0)
                    getDatabaseTax("EPS");
                else
                    selectSummaryTAXReport("EPS");
            }
        });
    }


    private void customDialogHostSummaryReport() {
        dialogHostSummary = new Dialog(this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogHostSummary.getLayoutInflater().inflate(R.layout.dialog_custom_host_summary, null);//K.GAME 180828 change dialog UI
        dialogHostSummary.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogHostSummary.setContentView(view);//K.GAME 180828 change dialog UI
        dialogHostSummary.setCancelable(false);//K.GAME 180828 change dialog UI

        posSummaryBtn = dialogHostSummary.findViewById(R.id.posBtn);
        epsSummaryBtn = dialogHostSummary.findViewById(R.id.epsBtn);
        tmsSummaryBtn = dialogHostSummary.findViewById(R.id.tmsBtn);
        qrSummaryBtn = dialogHostSummary.findViewById(R.id.qrSummaryBtn);
        hcSummaryBtn = dialogHostSummary.findViewById(R.id.hcSummaryBtn);
        aliSummaryBtn = dialogHostSummary.findViewById(R.id.aliSummaryBtn);
        wechatSummaryBtn = dialogHostSummary.findViewById(R.id.wechatSummaryBtn);

        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)
            tmsSummaryBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
            posSummaryBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length()<8)
            epsSummaryBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)
            hcSummaryBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID).length()<8)
            qrSummaryBtn.setVisibility(View.GONE);
//END SINN 20181119  AXA no need select host



        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
            aliSummaryBtn.setVisibility(View.VISIBLE);
        }

        if (Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
            wechatSummaryBtn.setVisibility(View.VISIBLE);
        }

        closeSummaryImage = dialogHostSummary.findViewById(R.id.closeImage);
        taxKtbOffUsSummaryBtn = dialogHostSummary.findViewById(R.id.taxKtbOffUsBtn);
        taxBase24EpsSummaryBtn = dialogHostSummary.findViewById(R.id.taxBase24EpsBtn);


        closeSummaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHostSummary.dismiss();
            }
        });
        posSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryReport("POS");

            }
        });
        epsSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryReport("EPS");
            }
        });

        tmsSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryReport("TMS");
            }
        });
        taxKtbOffUsSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryTAXReport("POS");
            }
        });
        taxBase24EpsSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryTAXReport("EPS");
            }
        });
        qrSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryQrReport();
            }
        });
        hcSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOff = 2;
//                selectSummaryReport("GHC");
                selectGHCSummaryReport();       // Paul_20181202
            }
        });
        aliSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryAliReport("ALIPAY");
            }
        });
        wechatSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryAliReport("WECHAT");
            }
        });
    }

    private void setViewReportTaxDetail() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportTaxView = inflater.inflate(R.layout.view_slip_report_tax_detail, null);
        reportTaxDetailLinearLayout = reportTaxView.findViewById(R.id.reportTaxDetailLinearLayout);
        taxIdLabel = reportTaxView.findViewById(R.id.taxIdLabel);
        batchIdLabel = reportTaxView.findViewById(R.id.batchIdLabel);
        hostTaxLabel = reportTaxView.findViewById(R.id.hostLabel);
        dateTaxLabel = reportTaxView.findViewById(R.id.dateLabel);
        timeTaxLabel = reportTaxView.findViewById(R.id.timeLabel);
        countFeeLabel = reportTaxView.findViewById(R.id.countFeeLabel);
        totalFeeLabel = reportTaxView.findViewById(R.id.totalFeeLabel);
        countVoidFeeLabel = reportTaxView.findViewById(R.id.countVoidFeeLabel);
        totalVoidFeeLabel = reportTaxView.findViewById(R.id.totalVoidFeeLabel);
        countGrandLabel = reportTaxView.findViewById(R.id.countGrandLabel);
        totalCountGrandLabel = reportTaxView.findViewById(R.id.totalCountGrandLabel);
        recyclerViewReportTaxDetail = reportTaxView.findViewById(R.id.recyclerViewReportTaxDetail);
        merchantName1TaxLabel = reportTaxView.findViewById(R.id.merchantName1Label);
        merchantName2TaxLabel = reportTaxView.findViewById(R.id.merchantName2Label);
        merchantName3TaxLabel = reportTaxView.findViewById(R.id.merchantName3Label);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReportTaxDetail.setLayoutManager(layoutManager);

        //     bankImage =reportTaxView.findViewById( R.id.bank1Image); //20180810 SINN Add multilogo

//        if (Preference.getInstance(this).getValueString( Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
//        {
//            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
//            bankImage.setImageResource(id);
//        }

    }

    private void getDatabaseTax(String typeHost) {
        gtypeHost = typeHost;
        System.out.printf("utility:: %s getDatabaseTax = %s \n", TAG, typeHost);
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
        if (typeHost.equalsIgnoreCase("EPS")) {
            batchIdLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
            hostTaxLabel.setText("WAY4");
        } else {
            batchIdLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
            hostTaxLabel.setText("KTB OFFUS");
        }
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1TaxLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2TaxLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3TaxLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

        dateTaxLabel.setText(dateFormat.format(date));
        timeTaxLabel.setText(timeFormat.format(date));
        RealmResults<TransTemp> tax = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
        if (tax.size() > 0) {
            Double amountVoid = 0.0;
            Double amount = 0.0;
            int feeSize = 0;
            int feeVoidSize = 0;
            RealmResults<TransTemp> taxVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "Y").findAll();
            for (int i = 0; i < taxVoid.size(); i++) {
                amountVoid += Double.valueOf(taxVoid.get(i).getFee());
            }
            feeVoidSize = taxVoid.size();
            RealmResults<TransTemp> taxFee = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "N").findAll();
            for (int i = 0; i < taxFee.size(); i++) {
                amount += Double.valueOf(taxFee.get(i).getFee());
            }
            feeSize = taxFee.size();
            countFeeLabel.setText(feeSize + "");
            totalFeeLabel.setText(decimalFormat.format(amount));
            countVoidFeeLabel.setText(feeVoidSize + "");
            totalVoidFeeLabel.setText(decimalFormat.format(amountVoid));
            countGrandLabel.setText(feeSize + feeVoidSize + "");
            totalCountGrandLabel.setText(decimalFormat.format(amount));
            if (taxList == null) {
                taxList = new ArrayList<>();
            } else {
                taxList.clear();
            }
            taxList.addAll(tax);
            if (recyclerViewReportTaxDetail.getAdapter() == null) {
                reportTaxDetailAdapter = new ReportTaxDetailAdapter(this);
                recyclerViewReportTaxDetail.setAdapter(reportTaxDetailAdapter);
            } else {
                reportTaxDetailAdapter.clear();
            }
            reportTaxDetailAdapter.setItem(taxList);
            reportTaxDetailAdapter.notifyDataSetChanged();

            setMeasureTaxDetail();

            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    checkOff = 0;
                    System.out.printf("utility:: %s doPrinting Befor 003 \n", TAG);
                    //doPrinting(getBitmapFromView(reportTaxDetailLinearLayout));
                }
            }.start();
        } else {
            Utility.customDialogAlert(MenuDetailReportActivity.this, typeHost + " ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    dialogLoading.dismiss();
                }
            });
        }

    }

    private void getQrReport() {
        System.out.printf("utility:: %s getQrReport \n", TAG);
        gtypeHost = "QR";
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        RealmResults<QrCode> qrCodes = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();
        if (qrCodes.size() > 0) {
            Double amount = 0.0;

            for (int i = 0; i < qrCodes.size(); i++) {
                amount += Double.valueOf(qrCodes.get(i).getAmount().replaceAll(",",""));
            }

            Date date = new Date();
            String dateFormat = new SimpleDateFormat("dd/MM/yy").format(date);
            String timeFormat = new SimpleDateFormat("HH:mm:ss").format(date);
            dateLabelQQr.setText(dateFormat);
            timeLabelQQr.setText(timeFormat);
            String qr_mid = "MID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_MERCHANT_ID);
            String qr_tid = "TID:"+Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID);
            String qr_batch = "BATCH:"+CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER)), 6);

            midLabelQQr.setText(qr_mid);
            tidLabelQQr.setText(qr_tid);
            batchLabelQQr.setText(qr_batch);
            hostLabelQQr.setText("HOST: QR");

            taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));

            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                merchantName1QrLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                merchantName2QrLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                merchantName3QrLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

            amountReportLabelQr.setText(decimalFormat.format(amount));
            countReportLabelQr.setText(qrCodes.size() + "");

            if (qrCodeList == null) {
                qrCodeList = new ArrayList<>();
            } else {
                qrCodeList.clear();
            }
            qrCodeList.addAll(qrCodes);
            slipQrReportAdapter.setItem(qrCodeList);
            slipQrReportAdapter.notifyDataSetChanged();

            setMeasureQr();

            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    checkOff = 0;
                    System.out.printf("utility:: %s doPrinting Befor 004 \n", TAG);
                    //doPrinting(getBitmapFromView(reportDetailLinearLayoutQr));
                }
            }.start();
        } else {
            Utility.customDialogAlert(MenuDetailReportActivity.this, " ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    dialogLoading.dismiss();
                }
            });
        }
    }

    private void getAliReport(String type) {
        System.out.printf("utility:: %s getQrReport \n", TAG);
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        RealmResults<QrCode> qrCodes = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("respcode", "0").findAll();
        if (qrCodes.size() > 0) {
            Double amount = 0.0;
            for (int i = 0; i < qrCodes.size(); i++) {
                // Paul_20181218
                if(qrCodes.get(i).getVoidFlag().equals("N"))
                    amount += Double.valueOf(qrCodes.get(i).getAmt().replaceAll(",", ""));
/*
                if(qrCodes.get(i).getAmtplusfee().equals("null")) {
                    if(qrCodes.get(i).getVoidFlag().equals("N"))
                        amount += Double.valueOf(qrCodes.get(i).getAmt().replaceAll(",", ""));
                }else{
                    if(qrCodes.get(i).getVoidFlag().equals("N"))
                        amount += Double.valueOf(qrCodes.get(i).getAmtplusfee().replaceAll(",",""));
                }
*/
            }

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
            dateLabelQr.setText(dateFormat.format(date));
            timeLabelQr.setText(timeFormat.format(date));

            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                merchantName1AliLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                merchantName2AliLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                merchantName3AliLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

            if(type.equals("ALIPAY")){
                batchLabelQr.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER)), 6));
            }else{
                batchLabelQr.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER)), 6));
            }

            midLabelQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
            tidLabelQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
            if(type.equals("ALIPAY")) {
                hostLabelQr.setText( type );
            }
            if(type.equals("WECHAT")) {     // Paul_20190325
                hostLabelQr.setText( type + " PAY");
            }
            amountReportLabelAli.setText(decimalFormat.format(amount));
            countReportLabelAli.setText(qrCodes.size() + "");
            if (qrCodeList == null) {
                qrCodeList = new ArrayList<>();
            } else {
                qrCodeList.clear();
            }
            qrCodeList.addAll(qrCodes);
            slipAlipayReportAdapter.setItem(qrCodeList);
            slipAlipayReportAdapter.notifyDataSetChanged();

            setMeasureAlipay();


            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
//                    checkOff = 0;
                    checkOff = 1;       // Paul_20181218 detail report
                    System.out.printf("utility:: %s doPrinting Befor 004 \n", TAG);
                    //doPrinting(getBitmapFromView(reportDetailLinearLayoutAlipay));      // Paul_20181120
                }
            }.start();
        } else {
            Utility.customDialogAlert(MenuDetailReportActivity.this, " ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    dialogLoading.dismiss();
                }
            });
        }
    }

    private void getDatabaseAliTax(String typeHost) {
        gtypeHost = typeHost;
        System.out.printf("utility:: %s getDatabaseAliTax = %s \n", TAG, typeHost);
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
        if (typeHost.equalsIgnoreCase("ALIPAY")) {
            batchIdLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_ALI_BATCH_NUMBER), 6));
            hostTaxLabel.setText("ALIPAY");
        } else {
            batchIdLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_WEC_BATCH_NUMBER), 6));
            hostTaxLabel.setText("WECHAT PAY");     // Paul_20190324
        }
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1TaxLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2TaxLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3TaxLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

        dateTaxLabel.setText(dateFormat.format(date));
        timeTaxLabel.setText(timeFormat.format(date));
        RealmResults<QrCode> taxAli = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("respcode", "0").findAll();
        if (taxAli.size() > 0) {
            Double amountVoid = 0.0;
            Double amount = 0.0;
            Double TotalAmount = 0.0;
            int feeSize = 0;
            int feeVoidSize = 0;
            RealmResults<QrCode> taxVoid = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("respcode", "0").equalTo("voidflag", "Y").findAll();
            for (int i = 0; i < taxVoid.size(); i++) {
                if(!taxVoid.get(i).getFee().equals("null"))
                    amountVoid += Double.valueOf(taxVoid.get(i).getFee());
                if(!taxVoid.get(i).getAmt().equals("null"))                     // Paul_20190201
                    TotalAmount += Double.valueOf(taxVoid.get(i).getAmt());
            }
            feeVoidSize = taxVoid.size();
            RealmResults<QrCode> taxFee = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("respcode", "0").equalTo("voidflag", "N").findAll();
            for (int i = 0; i < taxFee.size(); i++) {
                if(!taxFee.get(i).getFee().equals("null"))
                    amount += Double.valueOf(taxFee.get(i).getFee());
                if(!taxFee.get(i).getAmt().equals("null"))                      // Paul_20190201
                    TotalAmount += Double.valueOf(taxFee.get(i).getAmt());
            }
            feeSize = taxFee.size();
            if((amountVoid + amount) == 0)      // Paul_20190201
            {
                if(TotalAmount == 0 || OnlyTaxSelect == 1) {
                    Utility.customDialogAlert(MenuDetailReportActivity.this, typeHost + " ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            dialogLoading.dismiss();
                        }
                    });
                }
                else {
                    // Paul_20190215
//                    dialogLoading.dismiss();
                    dismissAllDialog();
                    Intent intent = new Intent(MenuDetailReportActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
                return;
            }
            countFeeLabel.setText(feeSize + "");
            totalFeeLabel.setText(decimalFormat.format(amount));
            countVoidFeeLabel.setText(feeVoidSize + "");
            totalVoidFeeLabel.setText(decimalFormat.format(amountVoid));
            countGrandLabel.setText(feeSize + feeVoidSize + "");
            totalCountGrandLabel.setText(decimalFormat.format(amount));
            if (taxAliList == null) {
                taxAliList = new ArrayList<>();
            } else {
                taxAliList.clear();
            }
            taxAliList.addAll(taxAli);

            if (recyclerViewReportTaxDetail.getAdapter() == null) {
                reportAlipayTaxDetailAdapter = new ReportAliTaxDetailAdapter(this);
                recyclerViewReportTaxDetail.setAdapter(reportAlipayTaxDetailAdapter);
            } else {
                reportAlipayTaxDetailAdapter.clear();
            }
            reportAlipayTaxDetailAdapter.setItem(taxAliList);
            reportAlipayTaxDetailAdapter.notifyDataSetChanged();

            setMeasureTaxDetail();

            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    checkOff = 0;
                    System.out.printf("utility:: %s doPrinting Befor 003 \n", TAG);
                    //doPrinting(getBitmapFromView(reportTaxDetailLinearLayout));
                }
            }.start();
        } else {
            Utility.customDialogAlert(MenuDetailReportActivity.this, typeHost + " ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    dialogLoading.dismiss();
                }
            });
        }

    }

    private void setMeasureTaxDetail() {
        reportTaxView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportTaxView.layout(0, 0, reportTaxView.getMeasuredWidth(), reportTaxView.getMeasuredHeight());
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

    public void doPrinting(Bitmap slip) { // ตรวจสอบค่า ว่าต้องปริ้น offline ไหม
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
                            System.out.printf("utility:: onPrintFinish 0000000 checkOff = %d , gtypeHost = %s \n", checkOff, gtypeHost);
                            if (checkOff == 0) {
                                dismissAllDialog();
                                Intent intent = new Intent(MenuDetailReportActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            } else if (checkOff == 1) {
                                checkOff = 0;
                                OnlyTaxSelect = 0;      // Paul_20190202
                                switch (gtypeHost) {
                                    case "GHC":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                checkSaleOffGHC();
                                            }
                                        });
                                        break;
                                    case "POS":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getDatabaseTax("POS");
                                            }
                                        });
                                        break;
                                    case "EPS":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getDatabaseTax("EPS");
                                            }
                                        });
                                        break;
                                    // Paul_20181218
                                    case "ALIPAY":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getDatabaseAliTax("ALIPAY");
                                            }
                                        });
                                        break;
                                    case "WECHAT":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getDatabaseAliTax("WECHAT");        // Paul_20190324
                                            }
                                        });
                                        break;
                                }
                            } else {
                                checkOff = 0;
                                OnlyTaxSelect = 0;      // Paul_20190202
                                switch (gtypeHost) {
                                    case "GHC":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                selectSummaryReportHc();
                                            }
                                        });
                                        break;
                                    case "POS":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                selectSummaryTAXReport("POS");
                                            }
                                        });
                                        break;
                                    case "EPS":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                selectSummaryTAXReport("EPS");
                                            }
                                        });
                                        break;
                                        // Paul_20181218
                                    case "ALIPAY":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                selectSummaryAliTAXReport("ALIPAY");
                                            }
                                        });
                                        break;
                                    case "WECHAT":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                selectSummaryAliTAXReport("WECHAT");    // Paul_20190324
                                            }
                                        });
                                        break;
                                }
                            }
                        }
//                        if(inSelect==0)
//                        getDatabaseTax("EPS");
//                        else
//                        selectSummaryTAXReport("EPS");

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
                            dialogLoading.dismiss();
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


    private void checkSaleOffGHC() {
        gtypeHost = "GHC";
        System.out.printf("utility:: %s checkSaleOffGHC \n", TAG);
// Paul_20180731
        RealmResults<TransTemp> saleOffline = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").equalTo("ghcoffFlg", "Y").findAll();
        System.out.printf("utility:: checkSaleOffGHC 00001 \n");

//        RealmResults<TransTemp> saleOffline = realm.where(TransTemp.class).findAll();
        if (saleOffline.size() > 0) {
            System.out.printf("utility:: checkSaleOffGHC 00002 \n");
            DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
            if (transTempList == null) {
                transTempList = new ArrayList<>();
            } else {
                transTempList.clear();
            }
            recyclerViewReportDetail.setAdapter(null);
            slipReportHcOffAdapter = new SlipReportAdapter(this);
            recyclerViewReportDetail.setAdapter(slipReportHcOffAdapter);
            transTempList.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").equalTo("ghcoffFlg", "Y").findAll());
            slipReportHcOffAdapter.setItem(transTempList);
            slipReportHcOffAdapter.notifyDataSetChanged();

            detailLabel.setText("DETAIL OFFLINE REPORT");
            Count = 0;
            Double amountAll = 0.0;
            for (int i = 0; i < transTempList.size(); i++) {
                Count++;
                if (transTempList.get(i).getVoidFlag().equals("N")) {
                    amountAll += Double.valueOf(transTempList.get(i).getAmount().replaceAll(",",""));
                }
            }
            Date date = new Date();
            dateReportLabel.setText(new SimpleDateFormat("dd/MM/yy").format(date));
            timeReportLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));

            midReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
            tidReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
            batchReportLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
            hostReportLabel.setText("HEARTH CARE");


            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                merchantName1ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                merchantName2ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                merchantName3ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

            countReportLabel.setText(Count + "");   // Paul_20180725_OFF
            amountReportLabel.setText(decimalFormat.format(amountAll));

            setMeasure();
            dialogLoading.show();
            new CountDownTimer(1500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
                }

                @Override
                public void onFinish() {
//                    if (transTempList.size() > 0)
                    if (Count > 0)       // Paul_20180725_OFF
                    {
                        checkOff = 0;
                        System.out.printf("utility:: %s doPrinting Befor 001 \n", TAG);
                        //doPrinting(getBitmapFromView(reportDetailLinearLayout));
                    } else {
                        if (GhcOnlineFlg == false) {     // Paul_20180725_OFF
                            Utility.customDialogAlert(MenuDetailReportActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    dialogLoading.dismiss();
                                }
                            });
                        }
                    }
                }
            }.start();
        } else {
            System.out.printf("utility:: checkSaleOffGHC 00005 \n");
            Intent intent = new Intent(MenuDetailReportActivity.this, MenuServiceListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
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
//        dialogOutOfPaper.setContentView(R.layout.dialog_custom_printer);
//        dialogOutOfPaper.setCancelable(false);
//        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.printf("utility:: %s doPrinting Befor 002 \n", TAG);
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
                dialogLoading.show();
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
//        dialogLoading.setContentView(R.layout.dialog_custom_alert_loading);
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void customDialogWaiting(String msg) {
        dialogLoading = new Dialog(this);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(R.layout.dialog_custom_load_process);
        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogLoading.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        msgLabel = dialogLoading.findViewById(R.id.msgLabel); //K.GAME 180831 chang waitting UI
//        msgLabel.setText("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");//K.GAME 180831 chang waitting UI
        if (msg != null) {
            msgLabel.setText(msg);
        }
        Utility.animation_Waiting_new(dialogLoading);//K.GAME 180831 chang waitting UI
        //END K.GAME 180831 chang waitting UI
    }


    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }

    private class selectOffline extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}
