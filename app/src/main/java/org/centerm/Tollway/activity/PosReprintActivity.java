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
import org.centerm.Tollway.activity.qr.CheckQrActivity;
import org.centerm.Tollway.adapter.ReprintAdapter;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;
import java.util.List;

import io.realm.Realm;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_F1_POS_MSG;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_APPROVAL_CODE;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER;

//import org.centerm.Tollway.healthcare.database.SaleOfflineHealthCare;        // Paul_20180724_OFF

public class PosReprintActivity extends SettingToolbarActivity {

    private RecyclerView menuRecyclerView = null;
    private ReprintAdapter reprintAdapter = null;
    private List<String> nameList = null;

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
    private TextView systrcLabel = null;
    private TextView systrcGHCLabel = null;
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

    private TextView name_sw_version;       // Paul_20190125 software version print

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
    private View printLastView;
    private CardManager cardManager = null;
    private AidlPrinter printDev = null;
    private Dialog dialogSearch;
    private Dialog dialogHost;
    private Button posBtn;
    private Button epsBtn;
    private Button tmsBtn;

    private Button ghcBtn; ////SINN 20180706 Add void print

    private ImageView closeImage;
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
    //    private ImageView closeQrImage; //K.GAME 180926 ImageView > Button
    private Button closeQrImage; //K.GAME 180926 ImageView > Button

    private TextView taxIdLabel;
    private TextView taxAbbLabel;
    private TextView traceTaxLabel;
    private TextView batchTaxLabel;
    private TextView dateTaxLabel;
    private TextView timeTaxLabel;
    private TextView feeTaxLabel;
    private TextView copyLabel;
    private TextView typeCopyLabel;
    private TextView nameEmvCardLabel;
    private LinearLayout taxLinearLayout;

    private boolean isStatusPrintLastSlip = false;
    private TextView typeInputCardLabel;
    private final String TAG = "PosReprintActivity";
    private Dialog dialogLoading;
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

//20180708 SINN Add healthcare print.
    /**
     * Slip
     */
    private View hgcSaleView;
    private LinearLayout slip_sale_void_hgc_re;
    private TextView dateHgcLabel;
    private TextView timeHgcLabel;
    private TextView midHgcLabel;
    private TextView tidHgcLabel;
    private TextView traceNoLabel;
    private TextView typeSaleLabel;
    private TextView nameEngLabel;
    private TextView batchHgcLabel;
    private TextView apprCodeHgcLabelPosinterface; // Paul_20180713
    private TextView comCodeHgcLabel;               // Paul_20180715
    private TextView cardNoHgcLabel; //PAUL_20180716
    private TextView amountLabel;
    private TextView merchantName1HgcLabel;
    private TextView merchantName2HgcLabel;
    private TextView merchantName3HgcLabel;
    private AidlPrinter printer;
//end 20180708 SINN Add healthcare print.

    /**
     * Interface
     */
    private PosInterfaceActivity posInterfaceActivity;
    public static final String KEY_INTERFACE_INV = PosReprintActivity.class.getName() + "_key_invoice_number";  //SINN 20180706 Add QR print.
    public static final String KEY_INTERFACE_REPRINT_TYPE = PosReprintActivity.class.getName() + "_key_reprint_type";
    String invoiceId = null;  //SINN 20180706 Add QR print.
    String approvalcode = null; // Paul_20181024 Add to Approval Code
    String inReprintType = null;
    String szkey_interface_f1_pos = " ";


    private String szDateOrg = null;
    private String szTimeOrg = null;

    TransTemp gtransTemp;    // Paul_20180717
//    SaleOfflineHealthCare gOfftransTemp;    // Paul_20180724_OFF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reprint);
        setContentView(R.layout.activity_print_previous);

        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        posInterfaceActivity = MainApplication.getPosInterfaceActivity();

        initData();
        initWidget();
        initBtnExit();
        customDialogLoading();
        customDialogOutOfPaper();

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printLastView = inflater.inflate(R.layout.view_sale_void, null);
        merchantName1Label = printLastView.findViewById(R.id.merchantName1Label);
        merchantName2Label = printLastView.findViewById(R.id.merchantName2Label);
        merchantName3Label = printLastView.findViewById(R.id.merchantName3Label);
        slipLinearLayout = printLastView.findViewById(R.id.slipLinearLayout);
        tidLabel = printLastView.findViewById(R.id.tidLabel);
        midLabel = printLastView.findViewById(R.id.midLabel);
        traceLabel = printLastView.findViewById(R.id.traceLabel);
        systrcLabel = printLastView.findViewById(R.id.systrcLabel);
        batchLabel = printLastView.findViewById(R.id.batchLabel);
        refNoLabel = printLastView.findViewById(R.id.refNoLabel);
        dateLabel = printLastView.findViewById(R.id.dateLabel);
        timeLabel = printLastView.findViewById(R.id.timeLabel);
        typeLabel = printLastView.findViewById(R.id.typeLabel);
        typeCardLabel = printLastView.findViewById(R.id.typeCardLabel);
        cardNoLabel = printLastView.findViewById(R.id.cardNoLabel);
        apprCodeLabel = printLastView.findViewById(R.id.apprCodeLabel);
        comCodeLabel = printLastView.findViewById(R.id.comCodeLabel);
        amtThbLabel = printLastView.findViewById(R.id.amtThbLabel);
        feeThbLabel = printLastView.findViewById(R.id.feeThbLabel);
        totThbLabel = printLastView.findViewById(R.id.totThbLabel);
        ref1Label = printLastView.findViewById(R.id.ref1Label);
        ref2Label = printLastView.findViewById(R.id.ref2Label);
        ref3Label = printLastView.findViewById(R.id.ref3Label);
        ref1RelativeLayout = printLastView.findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayout = printLastView.findViewById(R.id.ref2RelativeLayout);
        ref3RelativeLayout = printLastView.findViewById(R.id.ref3RelativeLayout);

        taxIdLabel = printLastView.findViewById(R.id.taxIdLabel);
        taxAbbLabel = printLastView.findViewById(R.id.taxAbbLabel);
        traceTaxLabel = printLastView.findViewById(R.id.traceTaxLabel);
        batchTaxLabel = printLastView.findViewById(R.id.batchTaxLabel);
        dateTaxLabel = printLastView.findViewById(R.id.dateTaxLabel);
        timeTaxLabel = printLastView.findViewById(R.id.timeTaxLabel);
        feeTaxLabel = printLastView.findViewById(R.id.feeTaxLabel);
        copyLabel = printLastView.findViewById(R.id.copyLabel);
        typeCopyLabel = printLastView.findViewById(R.id.typeCopyLabel);
        nameEmvCardLabel = printLastView.findViewById(R.id.nameEmvCardLabel);
        taxLinearLayout = printLastView.findViewById(R.id.taxLinearLayout);
        sigatureLabel = printLastView.findViewById(R.id.sigatureLabel);
        typeInputCardLabel = printLastView.findViewById(R.id.typeInputCardLabel);
        comCodeFragment = printLastView.findViewById(R.id.comCodeFragment);

        appLabel = printLastView.findViewById(R.id.appLabel);
        appFrameLabel = printLastView.findViewById(R.id.appFrameLabel);
        tcLabel = printLastView.findViewById(R.id.tcLabel);
        tcFrameLayout = printLastView.findViewById(R.id.tcFrameLayout);
        aidLabel = printLastView.findViewById(R.id.aidLabel);
        aidFrameLayout = printLastView.findViewById(R.id.aidFrameLayout);

        name_sw_version = printLastView.findViewById(R.id.name_sw_version);   // Paul_20190125 software version print


        typeClick = "slipPrevious";
//                typeHost = "POS";
        //               typeHost = "EPS";
//                typeHost = "TMS";
//                typeHost = "GHC";


        if (szkey_interface_f1_pos.equals("ONUS"))
            typeHost = "TMS";
        else if (szkey_interface_f1_pos.equals("OFFUS"))
            typeHost = "POS";
        else if (szkey_interface_f1_pos.equals("QR"))
            typeHost = "QR";
        else
            typeHost = "GHC";


        invoiceEt = dialogSearch.findViewById(R.id.invoiceEt);
        //  invoiceEt.setVisibility(View.GONE);
        // invoiceEt.setText("1732");

        // invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
        // szkey_interface_f1_pos =bundle.getString(KEY_INTERFACE_F1_POS_MSG);

        Log.d(TAG, "invoiceId:" + invoiceId);
        Log.d(TAG, "f1_pos_msg:" + szkey_interface_f1_pos);
        Log.d(TAG, "typeHost:" + typeHost);

        invoiceEt.setText(invoiceId);
        invoiceEt.setEnabled(false);


        invoiceEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (invoiceEt.getText().length() > 0) {
                    searchInvoiceImage.setEnabled(false); // Paul_20180719 Search push can not
                    searchInvoiceImage.performClick();
                }
            }
        });


        selectReportPrevious(typeHost);
        //  selectReportSettleLast();


        searchInvoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String traceNoAddZero = "";
                if (!invoiceEt.getText().toString().isEmpty()) {
                    if (invoiceEt.getText().toString().length() < 6) {
                        for (int i = invoiceEt.getText().toString().length(); i < 6; i++) {
                            traceNoAddZero += "0";
                        }
                    }
                    System.out.printf("utility:: %s searchInvoiceImage 0000000000002 \n", TAG);
                    Log.d(TAG, "hostTypeCard: setOnClickListener " + typeHost);
                    if (typeHost.equals("ghc"))      // Paul_20180709 typeHost = "ghc";
                        typeHost = typeHost.toUpperCase();

                    typeHost = "POS";
                    TransTemp transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
                    if (transTemp != null) {
//                        setPrintLast(transTemp);
                        setPrintLastSearch(transTemp);
//                        if(posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                            TellToPosMatching();
//                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                @Override
//                                public void success() {
//                                }
//                            });
//                        }

                    } else {
                        typeHost = "EPS";
                        transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
                        if (transTemp != null) {
                            setPrintLastSearch(transTemp);
//                                if(posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                                    TellToPosMatching();
//                                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                        @Override
//                                        public void success() {
//                                        }
//                                    });
//                                }
                        } else {
                            typeHost = "TMS";
//                            transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).equalTo("apprvCode" ,approvalcode ).findFirst();
                            transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
                            if (transTemp != null) {
                                setPrintLastSearch(transTemp);
//                                    if(posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                                        TellToPosMatching();
//                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                            @Override
//                                            public void success() {
//                                            }
//                                        });
//                                    }
                            } else {
                                typeHost = "GHC";
                                //PAUL_20180714_START
//                                    SaleOfflineHealthCare offlineTemp = null; // Paul_20180724_OFF
//                                    offlineTemp = realm.where(SaleOfflineHealthCare.class).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
                                transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
                                if (transTemp != null) {
                                    if (transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
                                        setPrint_off(transTemp);
                                    }
                                    if (transTemp != null) {
                                        setPrintLastSearch(transTemp);
//                                            if(posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                                                TellToPosMatching();
//                                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                                    @Override
//                                                    public void success() {
//                                                    }
//                                                });
//                                            }
                                    }

                                } else {
//                                        typeHost = "QR";
//
//                                        if (transTemp != null) {
//                                            setPrintLastSearch(transTemp);
//                                            TellToPosMatching();
//                                        }else
//                                        TellToPosNoMatching("12");
//                                        if(posInterfaceActivity != null)

                                    Intent intent = new Intent(PosReprintActivity.this, CheckQrActivity.class);
                                    intent.putExtra(MenuServiceListActivity.KEY_TYPE_INTERFACE, "Interface");
                                    intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                                    intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);


//                                        if(posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                                            Utility.customDialogAlertAuto( PosReprintActivity.this, "ไม่มีข้อมูล" );
//                                            TellToPosNoMatching( "12" );
//                                                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                                @Override
//                                                public void success() {
//                                                    Utility.customDialogAlertAutoClear();
////                                                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
//                                                    Intent intent = new Intent( PosReprintActivity.this, MenuServiceListActivity.class );
//                                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                                                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
//                                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                                                    startActivity( intent );
//                                                    overridePendingTransition( 0, 0 );
//                                                    finish();
//                                                }
//                                            });
//                                        }
//                                        else
//                                            Utility.customDialogAlert(PosReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                                @Override
//                                                public void onClickImage(Dialog dialog) {
//                                                    dialog.dismiss();
//                                                    dialogLoading.dismiss();
//                                                    finish();
//                                                }
//                                            });

                                }
                            }
                        }


                    }
                } else {
                    invoiceEt.setError("กรุณาใส่ตัวเลข");
                }
            }
        });


    }


    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // typeInterface = bundle.getString(KEY_TYPE_INTERFACE);  all file for POS reprint
            invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
            approvalcode = bundle.getString(KEY_INTERFACE_VOID_APPROVAL_CODE);
            szkey_interface_f1_pos = bundle.getString(KEY_INTERFACE_F1_POS_MSG);
            System.out.printf("utility::  %s initData approvalcode = %s \n", TAG, approvalcode);
        }
    }


    private void setMeasure() {
        printLastView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printLastView.layout(0, 0, printLastView.getMeasuredWidth(), printLastView.getMeasuredHeight());
    }


    private void setMeasureSettle() {
        reportSettlementLast.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSettlementLast.layout(0, 0, reportSettlementLast.getMeasuredWidth(), reportSettlementLast.getMeasuredHeight());
    }

    @Override
    public void initWidget() {

        customDialogSearch();
        customDialogHostQr();
        setViewSettlementLast();

        setViewSaleHGC();  ////20180708 SINN Add healthcare print.///

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
            batchLabelSettle.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_POS));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_POS));
//            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
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

            batchFeeLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_POS));
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
            taxIdFeeLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 013 \n", TAG);
            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else if (typeHost.equalsIgnoreCase("EPS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_EPS).isEmpty()) {
            summaryLinearFeeLayout.setVisibility(View.VISIBLE);
            hostLabelSettle.setText("WAY4");
            batchLabelSettle.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_EPS));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS));
//            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            // //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_EPS));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_EPS));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS));
            voidSaleAmountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
//            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));

            batchFeeLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_EPS));
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
            taxIdFeeLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAX_ID));

            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 014 \n", TAG);
            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else if (typeHost.equalsIgnoreCase("TMS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_TMS).isEmpty()) {
            hostLabelSettle.setText("KTB ONUS");
            batchLabelSettle.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_TMS));
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
            // //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_normal, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS)));
            summaryLinearFeeLayout.setVisibility(View.GONE);
            setMeasureSettle();
            System.out.printf("utility:: %s doPrinting Befor 015 \n", TAG);
            //doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else if (typeHost.equalsIgnoreCase("QR") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_QR).isEmpty()) {
            hostLabelSettle.setText("KTB QR");
            batchLabelSettle.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_QR));   // Paul_20181120 please no remark last settlement reprint problem
//            batchLabelSettle.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));  ////SINN 20181025  Check settlement for GHC & welfare only
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
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
            System.out.printf("utility:: %s doPrinting Befor 016 \n", TAG);
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

    //PAUL_20180714
    private void setPrint_off(TransTemp transTemp) {
        gtransTemp = transTemp;
        setDataSlipOffline(transTemp);
        //PAUL_20180717
        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
            TellToOffLinePosMatching();
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    System.out.printf("utility:: %s doPrinting Befor 017 \n", TAG);
                    //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
                }
            });
        } else {
            System.out.printf("utility:: %s doPrinting Befor 018 \n", TAG);
            //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
        }
    }

    //PAUL_20180714
    private void setDataSlipOffline(TransTemp healthCareDB) {
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);
        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
        systrcGHCLabel.setText(healthCareDB.getTraceNo());
        Log.d("SINN:", "systrcLabel :" + systrcLabel.getText());
        System.out.printf("utility:: systrcLabel 004 = %s \n", healthCareDB.getTraceNo());

        traceNoLabel.setText(healthCareDB.getEcr());

        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        String idCardCd = null;
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
//            cardNoHgcLabel.setText(healthCareDB.getCardNo());
            idCardCd = healthCareDB.getCardNo();
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
//            cardNoHgcLabel.setText(healthCareDB.getIdCard());
            idCardCd = healthCareDB.getIdCard();
        } else {
//            cardNoHgcLabel.setText(healthCareDB.getCardNo());
            idCardCd = healthCareDB.getCardNo();
        }

        String szMSG = null;
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        cardNoHgcLabel.setText(szMSG);
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
        apprCodeHgcLabelPosinterface.setText(healthCareDB.getApprvCode());
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

    private void setPrintLast(TransTemp transTemp) {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            Log.d("SINN", "setPrintLastSearch");
            setDataSlipSale(transTemp);
            System.out.printf("utility:: %s doPrinting Befor 019 \n", TAG);
            //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));
            return;
        }

        DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText(transTemp.getTid());
        midLabel.setText(transTemp.getMid());
        traceLabel.setText(transTemp.getEcr());
        systrcLabel.setText(transTemp.getTraceNo());
        System.out.printf("SINN:: systrcLabel = %s \n", systrcLabel);

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

        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo()));   //20180815 SINN JSON  // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        typeCardLabel.setText(CardPrefix.getTypeCardName(transTemp.getCardNo()));   //20180815 SINN JSON  // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        String cutCardStart = transTemp.getCardNo().substring(0, 6);
//        String cutCardEnd = transTemp.getCardNo().substring(12, transTemp.getCardNo().length());
//        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
//        cardNoLabel.setText(cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16));
        cardNoLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel.setText(transTemp.getApprvCode());
//        comCodeLabel.setText(transTemp.getComCode());
        String typeVoidOrSale = "";
        if (!Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3));

        if (typeHost.equalsIgnoreCase("POS")) {
            typeVoidOrSale = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            typeVoidOrSale = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_EPS);
        } else {
            typeVoidOrSale = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_TMS);
        }
        if (transTemp.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabel.setText("C");
        } else if (transTemp.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabel.setText("W");
        } else {
            typeInputCardLabel.setText("S");
        }


        if (typeVoidOrSale.equals(CalculatePriceActivity.TypeSale)) {
            comCodeFragment.setVisibility(View.GONE);
            feeTaxLabel.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
            typeLabel.setText("SALE");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null)
                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee)))); // Paul_20190128 (float)
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

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
            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            typeLabel.setText("VOID");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null)
                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));    // Paul_20190128 (float)
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
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

        String valueParameterEnable = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAG_1000);
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

        /*if (!transTemp.getRef1().isEmpty()) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        }
        if (!transTemp.getRef2().isEmpty()) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        }
        if (!transTemp.getRef3().isEmpty()) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(transTemp.getRef3());
        }*/
        setMeasure();
        isStatusPrintLastSlip = true;
        System.out.printf("utility:: %s doPrinting Befor 020 \n", TAG);
        //doPrinting(getBitmapFromView(slipLinearLayout));

        rePrintLast(transTemp);
    }

    private void setPrintLastSearch(TransTemp transTemp) {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            Log.d("SINN", "setPrintLastSearch");
            setDataSlipSale(transTemp);
// Paul_20180719
//            try {
//                Thread.sleep(400);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                gtransTemp = transTemp;     // Paul_20180719
                TellToPosMatching();
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        System.out.printf("utility:: %s doPrinting Befor 021 \n", TAG);
                        //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));
                    }
                });
            } else {
                System.out.printf("utility:: %s doPrinting Befor 022 \n", TAG);
                //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));
            }
            return;
        }

        dialogLoading.show();
        DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");
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
            //20180708 SINN Add healthcare print.
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        //20180708 SINN Add healthcare print.
        refNoLabel.setText(transTemp.getRefNo());
        String date = transTemp.getTransDate().substring(6, 8);
        String mount = transTemp.getTransDate().substring(4, 6);
        String year = transTemp.getTransDate().substring(2, 4);
        dateLabel.setText(date + "/" + mount + "/" + year);
        timeLabel.setText(transTemp.getTransTime());

        szDateOrg = transTemp.getTransDate();
        szTimeOrg = transTemp.getTransTime();


        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON //typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        typeCardLabel.setText(CardPrefix.getTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON //typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        String cutCardStart = transTemp.getCardNo().substring(0, 6);
//        String cutCardEnd = transTemp.getCardNo().substring(12, transTemp.getCardNo().length());
//        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
//        cardNoLabel.setText(cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16));
        cardNoLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel.setText(transTemp.getApprvCode());
//        comCodeLabel.setText(transTemp.getComCode());
        String typeVoidOrSale = "";
        if (!Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_MERCHANT_3));

        if (typeHost.equalsIgnoreCase("POS")) {
            typeVoidOrSale = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            typeVoidOrSale = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_EPS);
        } else {
            typeVoidOrSale = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_TMS);
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
            feeTaxLabel.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
            typeLabel.setText("SALE");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null)
                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee)))); // Paul_20190128 (float)
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

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
            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            typeLabel.setText("VOID");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAX_ID));
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null)
                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));    // Paul_20190128 (float)
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
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
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

        String valueParameterEnable = Preference.getInstance(PosReprintActivity.this).getValueString(Preference.KEY_TAG_1000);
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

        setMeasure();
        isStatusPrintLastSlip = true;


        System.out.printf("utility:: setPrintLastSearch KKKKKKKKKKKKKKKKKKKKKKKK 002 \n");

        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
            gtransTemp = transTemp;


////////////
            TellToPosMatching();

            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    System.out.printf("utility:: %s doPrinting Befor 023 \n", TAG);
                    //doPrinting(getBitmapFromView(slipLinearLayout));
                    rePrintLast(gtransTemp);
                }
            });
        } else {
            System.out.printf("utility:: %s doPrinting Befor 024 \n", TAG);
            //doPrinting(getBitmapFromView(slipLinearLayout));
            rePrintLast(transTemp);
        }
    }

    public void TellToPosError(String szErr) {
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, szErr);
    }

    public void TellToPosMatching() {
        System.out.printf("utility:: %s TellToPosMatching 000001 \n", TAG);
        if (typeHost.equals("GHC")) {
            posInterfaceActivity.PosInterfaceWriteField("01", gtransTemp.getApprvCode());   // Paul_20180719
//            posInterfaceActivity.PosInterfaceWriteField( "02", posInterfaceActivity.ResponseMsgPosInterface( "00" ) );
            posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));
            posInterfaceActivity.PosInterfaceWriteField("65", gtransTemp.getTraceNo());   // Invoice Number
            if (!gtransTemp.getHostTypeCard().equalsIgnoreCase("GHC"))     // Paul_20180719
                posInterfaceActivity.PosInterfaceWriteField("D3", gtransTemp.getRefNo());  //Reference No
            posInterfaceActivity.PosInterfaceWriteField("16", gtransTemp.getTid());   //tid
            posInterfaceActivity.PosInterfaceWriteField("D1", gtransTemp.getMid());//mid

            posInterfaceActivity.PosInterfaceWriteField("03", gtransTemp.getTransDate().substring(2, 8));  //yymmdd

            posInterfaceActivity.PosInterfaceWriteField("04", gtransTemp.getTransTime());


            if (!gtransTemp.getHostTypeCard().equalsIgnoreCase("GHC"))     // Paul_20180719
            {
                posInterfaceActivity.PosInterfaceWriteField("F1", szkey_interface_f1_pos);
                if (!gtransTemp.getHostTypeCard().equalsIgnoreCase("QR"))     // Paul_20180731
                {
//                    String cutCardStart = gtransTemp.getCardNo().substring(0, 6);
//                    String cutCardEnd = gtransTemp.getCardNo().substring(12, gtransTemp.getCardNo().length());
//                    //                String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
//                    //                String szMSG = cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16);
//                    String cardNo = cutCardStart + "xxxxxx" + cutCardEnd;   //20180725 SINN reprint pos card number format
                    String cardNo = CardPrefix.maskviewcard(" ", gtransTemp.getCardNo());  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
                    String szMSG = cardNo;
                    posInterfaceActivity.PosInterfaceWriteField("30", szMSG);
                }
            } else {
                String CardNo;
                if (gtransTemp.getTypeSale().substring(1).equalsIgnoreCase("2")) {
                    CardNo = gtransTemp.getCardNo();
                } else if (gtransTemp.getTypeSale().substring(1).equalsIgnoreCase("1")) {
                    CardNo = gtransTemp.getIdCard();
                } else {
                    CardNo = gtransTemp.getCardNo();
                }
                String szMSG = CardNo.substring(0, 1) + " " + CardNo.substring(1, 4) + "X" + " " + "XXXX" + CardNo.substring(9, 10) + " " + CardNo.substring(10, 12) + " " + CardNo.substring(12, 13);

                posInterfaceActivity.PosInterfaceWriteField("30", szMSG);
            }
            posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, "00");
        } else {
            String szMSG = new String();
            String YY = "";
            String MM = "";
            String DD = "";

            posInterfaceActivity.PosInterfaceWriteField("01", apprCodeLabel.getText().toString());   // Approval Code
            posInterfaceActivity.PosInterfaceWriteField("02", posInterfaceActivity.ResponseMsgPosInterface("00"));   // Response Message TX NOT FOUND

            szMSG = traceLabel.getText().toString();
            posInterfaceActivity.PosInterfaceWriteField("65", szMSG);   // Invoice Number
            posInterfaceActivity.PosInterfaceWriteField("D3", refNoLabel.getText().toString());  //Reference No
//        tidLabel.setText(transTemp.getTid());
//        midLabel.setText(transTemp.getMid());
            posInterfaceActivity.PosInterfaceWriteField("16", tidLabel.getText().toString());   //tid
            posInterfaceActivity.PosInterfaceWriteField("D1", midLabel.getText().toString());//mid


            Log.d(TAG, "TellToPosMatching typeHost :" + typeHost);
            Log.d(TAG, "TellToPosMatching szDateOrg :" + szDateOrg);
            Log.d(TAG, "TellToPosMatching szDateOrg :" + szTimeOrg);

        /*
        typeHost :EPS    typeHost :POS   typeHost :GHC
        20180709         20180710         20180709
        19:56:57         10:31:46        194858
        */

            szMSG = szDateOrg;
            DD = szMSG.substring(6, 8);
            MM = szMSG.substring(4, 6);
            YY = szMSG.substring(2, 4);

            posInterfaceActivity.PosInterfaceWriteField("03", YY + MM + DD);  //yymmdd

            if (typeHost.equals("GHC"))
                posInterfaceActivity.PosInterfaceWriteField("04", szTimeOrg);  //hhmmss
            else {
                szMSG = szTimeOrg;
                DD = szMSG.substring(0, 2);
                MM = szMSG.substring(3, 5);
                YY = szMSG.substring(6, 8);
                posInterfaceActivity.PosInterfaceWriteField("04", DD + MM + YY);
            }
// Paul_20181024 Include 30 field
            System.out.printf("utility:: %s TellToPosMatching gtransTemp.getCardNo() = %s \n", TAG, gtransTemp.getCardNo());
//            String cutCardStart = gtransTemp.getCardNo().substring(0, 6);
//            String cutCardEnd = gtransTemp.getCardNo().substring(12, gtransTemp.getCardNo().length());
//            String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
            String cardNo = CardPrefix.maskviewcard(" ", gtransTemp.getCardNo());  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
//
//            szMSG = cutCardStart + "xxxxxx" + cutCardEnd;
            szMSG = CardPrefix.maskviewcard(" ", gtransTemp.getCardNo());  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
            posInterfaceActivity.PosInterfaceWriteField("30", szMSG);


//20180719 SINN Reprint send POS TAG "F1" with Host batch.
            if (typeHost.equals("TMS") || typeHost.equals("GHC"))
                posInterfaceActivity.PosInterfaceWriteField("F1", "ONUS ");
            else
                posInterfaceActivity.PosInterfaceWriteField("F1", "OFFUS");
//END 20180719 SINN Reprint send POS TAG "F1" with Host batch.

            posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, "00");

        }

//        String szMSG = new String();
//        String YY ="";
//        String MM="";
//        String DD="";
//        posInterfaceActivity.PosInterfaceWriteField("01",apprCodeLabel.getText().toString());   // Approval Code
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("00"));   // Response Message TX NOT FOUND
//
//        szMSG = traceLabel.getText().toString();
//        posInterfaceActivity.PosInterfaceWriteField("65",szMSG);   // Invoice Number
//        if(!typeHost.equals("GHC"))     // Paul_20180719
//            posInterfaceActivity.PosInterfaceWriteField("D3",refNoLabel.getText().toString());  //Reference No
////        tidLabel.setText(transTemp.getTid());
////        midLabel.setText(transTemp.getMid());
//        posInterfaceActivity.PosInterfaceWriteField("16",tidLabel.getText().toString() );   //tid
//        posInterfaceActivity.PosInterfaceWriteField("D1",midLabel.getText().toString() );//mid
//
//
//
//
//        Log.d(TAG, "TellToPosMatching typeHost :"+typeHost);
//        Log.d(TAG, "TellToPosMatching szDateOrg :"+szDateOrg);
//        Log.d(TAG, "TellToPosMatching szDateOrg :"+szTimeOrg);
//
//        /*
//        typeHost :EPS    typeHost :POS   typeHost :GHC
//        20180709         20180710         20180709
//        19:56:57         10:31:46        194858
//        */
//
//        szMSG=szDateOrg;
//        DD=szMSG.substring(6,8);
//        MM=szMSG.substring(4,6 );
//        YY=szMSG.substring(2,4 );
//
//        posInterfaceActivity.PosInterfaceWriteField("03",YY+MM+DD);  //yymmdd
//
//        if(typeHost.equals("GHC"))
//            posInterfaceActivity.PosInterfaceWriteField("04",szTimeOrg);  //hhmmss
//        else {
//            szMSG=szTimeOrg;
//            DD=szMSG.substring(0,2);
//            MM=szMSG.substring(3,5 );
//            YY=szMSG.substring(6,8 );
//            posInterfaceActivity.PosInterfaceWriteField("04", DD+MM+YY);
//        }
//
//        if(!typeHost.equals("GHC"))     // Paul_20180719
//        {
//            posInterfaceActivity.PosInterfaceWriteField( "F1", szkey_interface_f1_pos );
//        }
//        else
//        {
//            posInterfaceActivity.PosInterfaceWriteField( "30", cardNoLabel.getText().toString() );
//        }
//        posInterfaceActivity.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,"00");


    }

    // Paul_20180720
    public void TellToOffLinePosMatching() {
        System.out.printf("utility:: %s TellToOffLinePosMatching 000002 \n", TAG);
        posInterfaceActivity.PosInterfaceWriteField("01", "000xxxxxx");   // Paul_20180719
//        posInterfaceActivity.PosInterfaceWriteField( "02", posInterfaceActivity.ResponseMsgPosInterface( "00" ) );
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));
        posInterfaceActivity.PosInterfaceWriteField("65", gtransTemp.getTraceNo());   // Invoice Number
        posInterfaceActivity.PosInterfaceWriteField("16", gtransTemp.getTid());   //tid
        posInterfaceActivity.PosInterfaceWriteField("D1", gtransTemp.getMid());//mid

        posInterfaceActivity.PosInterfaceWriteField("03", gtransTemp.getTransDate().substring(2, 8));  //yymmdd

        posInterfaceActivity.PosInterfaceWriteField("04", gtransTemp.getTransTime());

        String CardNo;
        if (gtransTemp.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            CardNo = gtransTemp.getCardNo();
        } else if (gtransTemp.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            CardNo = gtransTemp.getIdCard();
        } else {
            CardNo = gtransTemp.getCardNo();
        }
        posInterfaceActivity.PosInterfaceWriteField("30", CardNo);
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, "00");
    }

    private void rePrintLast(TransTemp transTemp) {
        sigatureLabel.setVisibility(View.GONE);
        if (transTemp.getEmvNameCardHolder() != null)
            nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        nameEmvCardLabel.setLayoutParams(lp);
        nameEmvCardLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        copyLabel.setText("**** ต้นฉบับ ****");
        typeCopyLabel.setText("***** CUSTOMER COPY *****");

        setMeasure();
    }


    private void customDialogSearch() {
        dialogSearch = new Dialog(this);
        dialogSearch.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSearch.setContentView(R.layout.dialog_search);
        dialogSearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSearch.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        invoiceEt = dialogSearch.findViewById(R.id.invoiceEt);
        searchInvoiceImage = dialogSearch.findViewById(R.id.searchInvoiceImage);
        invoiceEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.printf("utility:: %s customDialogSearch 0000000000001 \n", TAG);

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String traceNoAddZero = "";
                    if (!invoiceEt.getText().toString().isEmpty()) {
                        if (invoiceEt.getText().toString().length() < 6) {
                            for (int i = invoiceEt.getText().toString().length(); i < 6; i++) {
                                traceNoAddZero += "0";
                            }
                        }
                        //20180708 SINN Add healthcare print.
                        Log.d(TAG, "hostTypeCard customDialogSearch: " + typeHost);
                        if (typeHost.equals("ghc"))      // Paul_20180709
                            typeHost = typeHost.toUpperCase();

                        TransTemp transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
                        if (transTemp != null) {
                            dialogLoading.show();
                            setPrintLastSearch(transTemp);
                        } else {
                            Utility.customDialogAlert(PosReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    dialogLoading.dismiss();
                                }
                            });
                        }
                    } else {
                        invoiceEt.setError("กรุณาใส่ตัวเลข");
                    }
                    return true;
                }
                return false;
            }
        });

        searchInvoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String traceNoAddZero = "";
                if (!invoiceEt.getText().toString().isEmpty()) {
                    if (invoiceEt.getText().toString().length() < 6) {
                        for (int i = invoiceEt.getText().toString().length(); i < 6; i++) {
                            traceNoAddZero += "0";
                        }
                    }
////20180708 SINN Add healthcare print.
                    Log.d(TAG, "hostTypeCard: setOnClickListener " + typeHost);
                    System.out.printf("utility:: %s customDialogSearch 0000000000002 \n", TAG);
                    TransTemp transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero + invoiceEt.getText().toString()).findFirst();
                    if (transTemp != null) {
//                        setPrintLast(transTemp);
                        setPrintLastSearch(transTemp);
                    } else {
                        Utility.customDialogAlert(PosReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                dialogLoading.dismiss();
                            }
                        });
                    }
                } else {
                    invoiceEt.setError("กรุณาใส่ตัวเลข");
                }
            }
        });
    }


    private void setDataSlipSale(TransTemp healthCareDB) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Paul_20180711_new
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);

        szDateOrg = healthCareDB.getTransDate();
        szTimeOrg = healthCareDB.getTransTime();

        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
//        systrcLabel.setText(healthCareDB.getTraceNo());
        systrcGHCLabel.setText(healthCareDB.getTraceNo()); //(:
        System.out.printf("SINN:: systrcGHCLabel = %s \n", systrcGHCLabel.getText());
        Log.d("SINN:", "systrcGHCLabel :" + systrcGHCLabel.getText());

        traceNoLabel.setText(healthCareDB.getEcr());
        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        // Paul_20180720 Start
        String szMSG = null;
        String CardNo = null;
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            CardNo = healthCareDB.getCardNo();
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            CardNo = healthCareDB.getIdCard();
        } else {
            CardNo = healthCareDB.getCardNo();      // Paul_20180720              //CardNo = healthCareDB.getIdCard();
        }
        szMSG = CardNo.substring(0, 1) + " " + CardNo.substring(1, 4) + "X" + " " + "XXXX" + CardNo.substring(9, 10) + " " + CardNo.substring(10, 12) + " " + CardNo.substring(12, 13);
        cardNoHgcLabel.setText(szMSG);       // Paul_20180720
        // Paul_20180720 End
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
//        apprCodeLabel.setText(healthCareDB.getApprvCode());
        apprCodeHgcLabelPosinterface.setText(healthCareDB.getApprvCode()); // Paul_20180712
        //comCodeLabel.setText("HCG13814");
        comCodeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAG_1001_HC));
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


    private void setMeasureHGC() {
        hgcSaleView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleView.layout(0, 0, hgcSaleView.getMeasuredWidth(), hgcSaleView.getMeasuredHeight());
    }

    private void setViewSaleHGC() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hgcSaleView = inflater.inflate(R.layout.view_slip_sale_hgc_re, null);
        slip_sale_void_hgc_re = hgcSaleView.findViewById(R.id.slip_sale_void_hgc_re_lay);
        dateHgcLabel = hgcSaleView.findViewById(R.id.dateLabel);
        timeHgcLabel = hgcSaleView.findViewById(R.id.timeLabel);
        midHgcLabel = hgcSaleView.findViewById(R.id.midLabel);
        tidHgcLabel = hgcSaleView.findViewById(R.id.tidLabel);
//        systrcLabel = hgcSaleView.findViewById( R.id.systrcLabel);
        systrcGHCLabel = hgcSaleView.findViewById(R.id.systrcGHCLabel);
        traceNoLabel = hgcSaleView.findViewById(R.id.traceNoLabel);
        typeSaleLabel = hgcSaleView.findViewById(R.id.typeSaleLabel);
        cardNoHgcLabel = hgcSaleView.findViewById(R.id.cardNoLabelxx); ////20180720 SINN  HGC slip fix
        nameEngLabel = hgcSaleView.findViewById(R.id.nameEngLabel);
//        apprCodeLabel = hgcSaleView.findViewById( R.id.apprCodeLabel);
        apprCodeHgcLabelPosinterface = hgcSaleView.findViewById(R.id.apprCodeLabel);    // Paul_20140713
        comCodeHgcLabel = hgcSaleView.findViewById(R.id.comCodeLabel); //Paul_20180714
//        comCodeLabel = hgcSaleView.findViewById( R.id.comCodeLabel);
        batchHgcLabel = hgcSaleView.findViewById(R.id.batchLabel);
        amountLabel = hgcSaleView.findViewById(R.id.amountLabel);
        merchantName1HgcLabel = hgcSaleView.findViewById(R.id.merchantName1Label);
        merchantName2HgcLabel = hgcSaleView.findViewById(R.id.merchantName2Label);
        merchantName3HgcLabel = hgcSaleView.findViewById(R.id.merchantName3Label);
    }
//end 20180706 SINN Add QR print.

    private void customDialogHostQr() {
        dialogHostQr = new Dialog(this);
        dialogHostQr.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHostQr.setCancelable(false);
        dialogHostQr.setContentView(R.layout.dialog_custom_host_qr);
        dialogHostQr.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogHostQr.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posQrBtn = dialogHostQr.findViewById(R.id.posBtn);
        epsQrBtn = dialogHostQr.findViewById(R.id.epsBtn);
        tmsQrBtn = dialogHostQr.findViewById(R.id.tmsBtn);
        qrBtn = dialogHostQr.findViewById(R.id.qrBtn);

        ghcQrBtn = dialogHostQr.findViewById(R.id.ghcBtn);  ////20180708 SINN Add healthcare print.

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
    }

    private void selectReportPrevious(String typeHost) {
        this.typeHost = typeHost;       // Paul_20180720
//        dialogSearch.show();
        if (dialogSearch != null && !dialogSearch.isShowing()) // Paul_20181024
        {
            dialogSearch.show();
//            try {//20180724 SINN  Activity has leaked.
//                dialogSearch.show();
//            } catch (Exception e) {
//                dialogSearch.dismiss();
//            }
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

        if (typeHost.equalsIgnoreCase("POS")) {
            this.typeHost = typeHost;
            voidSaleId = Preference.getInstance(PosReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            this.typeHost = typeHost;
            voidSaleId = Preference.getInstance(PosReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_EPS);
        } else if (typeHost.equalsIgnoreCase("TMS")) {      //20180708 SINN Add healthcare print.
            this.typeHost = typeHost;
            voidSaleId = Preference.getInstance(PosReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_TMS);
        }
        //20180708 SINN Add healthcare print.
        else {
            this.typeHost = typeHost;

//            szMSG = Preference.getInstance(ReprintActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
//            voidSaleId=Integer.parseInt(szMSG);


            szMSG = Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
            voidSaleId = Integer.valueOf(szMSG);
            voidSaleId = voidSaleId - 1;
            Log.d(TAG, "selectReportLast :voidSaleId " + String.valueOf(voidSaleId));

            szMSG = String.valueOf(voidSaleId);
            traceNoAddZero = "000000";
            traceNoAddZero = traceNoAddZero.substring(szMSG.length());
            traceNoAddZero = traceNoAddZero + szMSG;
        }
        //20180708 END SINN Add healthcare print.
        TransTemp transTemp;

        if (typeHost.equals("GHC"))
            transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero).findFirst();
        else
            transTemp = realm.where(TransTemp.class).equalTo("id", voidSaleId).findFirst();
        //end //20180708 SINN Add healthcare print.

//        TransTemp transTemp = realm.where(TransTemp.class).equalTo("id", voidSaleId).findFirst();
        if (transTemp != null) {
            setPrintLast(transTemp);
        } else {
            Utility.customDialogAlert(PosReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    dialogLoading.dismiss();
                }
            });
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
                                                System.out.printf("utility:: %s doPrinting Befor 012 \n", TAG);
                                                //doPrinting(getBitmapFromView(slipLinearLayout));
                                            }
                                        }.start();
                                    }
                                });
                            } else {
                                dialogLoading.dismiss();
//                                Intent intent = new Intent(ReprintActivity.this, MenuServiceActivity.class);
                                Intent intent = new Intent(PosReprintActivity.this, MenuServiceListActivity.class); // Paul_20180704
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
//        dialogOutOfPaper.setContentView( R.layout.dialog_custom_printer);
//        dialogOutOfPaper.setCancelable(false);
//        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.printf("utility:: %s doPrinting Befor 011 \n", TAG);
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
        dialogLoading.setCancelable(false);   // Paul_20181015 Printing Can not cancel button

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
        if (realm == null)       // Paul_20181026
            realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }


}
