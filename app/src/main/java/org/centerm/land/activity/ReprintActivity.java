package org.centerm.land.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.activity.settlement.SlipSettlementActivity;
import org.centerm.land.adapter.ReprintAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.TransTemp;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ReprintActivity extends SettingToolbarActivity {

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
    private Button qrBtn;
    private ImageView closeQrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprint);
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        initWidget();
        initBtnExit();

        customDialogOutOfPaper();

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printLastView = inflater.inflate(R.layout.view_sale_void, null);
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
//        super.initWidget();
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);
        setMenuList();
        customDialogSearch();
        customDialogHost();
        customDialogHostQr();
        setViewSettlementLast();
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
                        dialogHost.show();
                    } else if (position == 1) {
                        typeClick = "slipPrevious";
                        dialogHost.show();
                    } else if (position == 2) {
                        typeClick = "slipSettle";
                        dialogHostQr.show();
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
        nameList.add("พิมพ์ใบเสร็จย้อนหลัง");
        nameList.add("พิมพ์ใบสรุปยอดล่าสุด");
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

        merchantName1LabelSettle = reportSettlementLast.findViewById(R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1LabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2LabelSettle = reportSettlementLast.findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2LabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3LabelSettle = reportSettlementLast.findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3LabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));
    }

    private void setDataViewSettle() {
        if (typeHost.equalsIgnoreCase("POS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_POS).isEmpty()) {
            hostLabelSettle.setText("KTB OFFUS");
            batchLabelSettle.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_POS));
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_POS));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_POS));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_POS));
            voidSaleAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_void, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS)));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS)));
            setMeasureSettle();
            doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else if (typeHost.equalsIgnoreCase("EPS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_EPS).isEmpty()) {
            hostLabelSettle.setText("BASE24 EPS");
            batchLabelSettle.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS));
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_EPS));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_EPS));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS));
            voidSaleAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_void, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS)));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS)));
            setMeasureSettle();
            doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else if (typeHost.equalsIgnoreCase("TMS") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_TMS).isEmpty()) {
            hostLabelSettle.setText("KTB ONUS");
            batchLabelSettle.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS));
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_TMS));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_TMS));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS));
            voidSaleAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_void, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS)));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS)));
            setMeasureSettle();
            doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else if (typeHost.equalsIgnoreCase("QR") && !Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_QR).isEmpty()) {
            hostLabelSettle.setText("KTB ONUS");
            batchLabelSettle.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            tidLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
            saleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_COUNT_QR));
            saleTotalLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR)));
            dateLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_DATE_QR));
            timeLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_TIME_QR));
            voidSaleCountLabelSettle.setText(Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_COUNT_QR));
            voidSaleAmountLabelSettle.setText(getString(R.string.slip_pattern_amount_void, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR)));
            cardCountLabelSettle.setText(Integer.valueOf(saleCountLabelSettle.getText().toString()) + Integer.valueOf(voidSaleCountLabelSettle.getText().toString()) + "");
            cardAmountLabelSettle.setText(getString(R.string.slip_pattern_amount, Preference.getInstance(this).getValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR)));
            setMeasureSettle();
            doPrinting(getBitmapFromView(settlementLinearLayoutSettle));
        } else {
            Utility.customDialogAlert(this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void setPrintLast(TransTemp transTemp) {
        DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText(transTemp.getTid());
        midLabel.setText(transTemp.getMid());
        traceLabel.setText(transTemp.getEcr());
        systrcLabel.setText(transTemp.getTraceNo());
        if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
            batchLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
            batchLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
            batchLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS));
        refNoLabel.setText(transTemp.getRefNo());
        dateLabel.setText(transTemp.getTransDate());
        timeLabel.setText(transTemp.getTransTime());
        typeLabel.setText(transTemp.getTransStat());
        typeCardLabel.setText(CardPrefix.getTypeCardName(transTemp.getCardNo()));
        String cutCardStart = transTemp.getCardNo().substring(0, 6);
        String cutCardEnd = transTemp.getCardNo().substring(12, transTemp.getCardNo().length());
        cardNoLabel.setText(cutCardStart + "XXXXXX" + cutCardEnd);
        apprCodeLabel.setText(transTemp.getApprvCode());
        comCodeLabel.setText(transTemp.getComCode());
        amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
        if (transTemp.getFee() != null) {
            feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
            double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
            double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
            totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((float) (amount + fee))));
        } else {
            feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
            totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
        }
        if (!transTemp.getRef1().isEmpty()) {
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
        }
        setMeasure();

        doPrinting(getBitmapFromView(slipLinearLayout));
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

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String traceNoAddZero = "";
                    if (!invoiceEt.getText().toString().isEmpty()) {
                        if (invoiceEt.getText().toString().length() < 6) {
                            for (int i = invoiceEt.getText().toString().length(); i < 6; i++) {
                                traceNoAddZero += "0";
                            }
                        } else {
                            traceNoAddZero = invoiceEt.getText().toString();
                        }
                        TransTemp transTemp = realm.where(TransTemp.class).equalTo("ecr", traceNoAddZero).findFirst();
                        if (transTemp != null) {
                            setPrintLast(transTemp);
                        } else {
                            Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
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
                    } else {
                        traceNoAddZero = invoiceEt.getText().toString();
                    }
                    TransTemp transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", traceNoAddZero).findFirst();
                    if (transTemp != null) {
                        setPrintLast(transTemp);
                    } else {
                        Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }
                } else {
                    invoiceEt.setError("กรุณาใส่ตัวเลข");
                }
            }
        });
    }


    private void customDialogHost() {
        dialogHost = new Dialog(this);
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHost.setCancelable(false);
        dialogHost.setContentView(R.layout.dialog_custom_host);
        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
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
                    typeHost = "POS";
                    selectReportPrevious("POS");
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
                    typeHost = "EPS";
                    selectReportPrevious("EPS");
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
                    typeHost = "TMS";
                    selectReportPrevious("TMS");
                } else {
                    typeHost = "TMS";
                    selectReportSettleLast();
                }
            }
        });
    }

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
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeHost = "QR";
                selectReportSettleLast();
            }
        });
    }

    private void selectReportPrevious(String typeHost) {
        dialogSearch.show();
    }

    private void selectReportSettleLast() {
        setDataViewSettle();
    }

    private void selectReportLast(String typeHost) {
        int voidSaleId = 0;
        if (typeHost.equalsIgnoreCase("POS")) {
            voidSaleId = Preference.getInstance(ReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            voidSaleId = Preference.getInstance(ReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_EPS);
        } else {
            voidSaleId = Preference.getInstance(ReprintActivity.this).getValueInt(Preference.KEY_SALE_VOID_PRINT_ID_TMS);
        }
        TransTemp transTemp = realm.where(TransTemp.class).equalTo("id", voidSaleId).findFirst();
        if (transTemp != null) {
            setPrintLast(transTemp);
        } else {
            Utility.customDialogAlert(ReprintActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
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
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Intent intent = new Intent(ReprintActivity.this, MenuServiceActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }

                        @Override
                        public void onPrintError(int i) throws RemoteException {

                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {
                            dialogOutOfPaper.show();
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
        dialogOutOfPaper = new Dialog(this);
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOutOfPaper.setContentView(R.layout.dialog_custom_printer);
        dialogOutOfPaper.setCancelable(false);
        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

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
    }
}
