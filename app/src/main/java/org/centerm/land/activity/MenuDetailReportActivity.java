package org.centerm.land.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
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

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.adapter.MenuReportAdapter;
import org.centerm.land.adapter.ReportTaxDetailAdapter;
import org.centerm.land.adapter.SlipQrReportAdapter;
import org.centerm.land.adapter.SlipReportAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.QrCode;
import org.centerm.land.database.TransTemp;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MenuDetailReportActivity extends SettingToolbarActivity {

    private RecyclerView menuRecyclerView;
    private RecyclerView recyclerViewReportDetail;
    private LinearLayout reportDetailLinearLayout = null;
    private MenuReportAdapter menuReportAdapter = null;
    private List<String> nameList;
    private Dialog dialogMenu;
    private Button posBtn;
    private Button epsBtn;
    private Button tmsBtn;
    private Button qrBtn;
    private ImageView closeImage;

    private Realm realm;
    private View reportView;
    private View reportSummaryView;
    private CardManager cardManager = null;
    private AidlPrinter printDev = null;
    private final String TAG = "MenuDetailReport";
    private SlipReportAdapter slipReportAdapter;

    private List<TransTemp> transTempList;

    private int summaryReportSize = 0;
    private Double totalAll = 0.0;
    private int countAll = 0;
    private Double totalSale = 0.0;
    private Double totalVoid = 0.0;
    private TextView merchantName1Label;
    private TextView merchantName2Label;
    private TextView merchantName3Label;
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
    private LinearLayout summaryLinearLayout;
    private Dialog dialogHost;
    private String typeClick;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private Dialog dialogMenuHostTwo;

    /***
     * Report Tax Detail
     */
    private View reportTaxView;
    private TextView taxIdLabel;
    private TextView batchIdLabel;
    private TextView hostTaxLabel;
    private TextView dateTaxLabel;
    private TextView timeTaxLabel;
    private RecyclerView recyclerViewReportTaxDetail;
    private ReportTaxDetailAdapter reportTaxDetailAdapter;
    private ArrayList<TransTemp> taxList = null;
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
    private ImageView closeSummaryImage;
    private Button taxKtbOffUsSummaryBtn;
    private Button taxBase24EpsSummaryBtn;
    private Button tmsSummaryBtn;
    private Dialog dialogLoading;
    private TextView countReportLabel;
    private TextView amountReportLabel;
    private Button qrSummaryBtn;
    /**
     * Slip QR
     */
    private View reportViewQr;
    private RecyclerView recyclerViewReportDetailQr;
    private LinearLayout reportDetailLinearLayoutQr;
    private TextView countReportLabelQr;
    private TextView amountReportLabelQr;
    private TextView dateLabelQr;
    private TextView timeLabelQr;
    private TextView midLabelQr;
    private TextView tidLabelQr;
    private TextView batchLabelQr;
    private TextView hostLabelQr;
    private SlipQrReportAdapter slipQrReportAdapter;

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
    private NestedScrollView slipNestedScrollViewSmQr;
    private TextView merchantName1ReportLabel;
    private TextView merchantName2ReportLabel;
    private TextView merchantName3ReportLabel;
    private TextView merchantName1TaxLabel;
    private TextView merchantName2TaxLabel;
    private TextView merchantName3TaxLabel;
    private TextView merchantName1QrLabel;
    private TextView merchantName2QrLabel;
    private TextView merchantName3QrLabel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail_report);
        initWidget();
        initBtnExit();
        customDialogMenu();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);
        reportSummaryView();
        setMenuList();
        customDialogOutOfPaper();
        customDialogHost();
        customDialogHostTwo();
        customDialogHostSummaryReport();
        customDialogLoading();
        reportView();
        reportViewQr();
        reportSummaryViewQr();
        setViewReportTaxDetail();
        reportSummaryFeeView();
    }

    private void reportView() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportView = inflater.inflate(R.layout.view_slip_report_detail, null);
        recyclerViewReportDetail = reportView.findViewById(R.id.recyclerViewReportDetail);
        reportDetailLinearLayout = reportView.findViewById(R.id.reportDetailLinearLayout);
        slipReportAdapter = new SlipReportAdapter(this);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        recyclerViewReportDetail.setLayoutManager(layoutManager1);
        recyclerViewReportDetail.setAdapter(slipReportAdapter);
        countReportLabel = reportView.findViewById(R.id.countReportLabel);
        amountReportLabel = reportView.findViewById(R.id.amountReportLabel);
        merchantName1ReportLabel = reportView.findViewById(R.id.merchantName1Label);
        merchantName2ReportLabel = reportView.findViewById(R.id.merchantName2Label);
        merchantName3ReportLabel = reportView.findViewById(R.id.merchantName3Label);
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
        dateLabelQr = reportViewQr.findViewById(R.id.dateLabel);
        timeLabelQr = reportViewQr.findViewById(R.id.timeLabel);
        midLabelQr = reportViewQr.findViewById(R.id.midLabel);
        tidLabelQr = reportViewQr.findViewById(R.id.tidLabel);
        batchLabelQr = reportViewQr.findViewById(R.id.batchLabel);
        hostLabelQr = reportViewQr.findViewById(R.id.hostLabel);
        merchantName1QrLabel = reportViewQr.findViewById(R.id.merchantName1Label);
        merchantName2QrLabel = reportViewQr.findViewById(R.id.merchantName2Label);
        merchantName3QrLabel = reportViewQr.findViewById(R.id.merchantName3Label);
    }

    private void reportSummaryView() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSummaryView = inflater.inflate(R.layout.view_silp_report_summary, null);
        summaryLinearLayout = reportSummaryView.findViewById(R.id.summaryLinearLayout);
        merchantName1Label = reportSummaryView.findViewById(R.id.merchantName1Label);
        merchantName2Label = reportSummaryView.findViewById(R.id.merchantName2Label);
        merchantName3Label = reportSummaryView.findViewById(R.id.merchantName3Label);
        dateLabel = reportSummaryView.findViewById(R.id.dateLabel);
        timeLabel = reportSummaryView.findViewById(R.id.timeLabel);
        midLabel = reportSummaryView.findViewById(R.id.midLabel);
        tidLabel = reportSummaryView.findViewById(R.id.tidLabel);
        batchLabel = reportSummaryView.findViewById(R.id.batchLabel);
        hostLabel = reportSummaryView.findViewById(R.id.hostLabel);
        saleCountLabel = reportSummaryView.findViewById(R.id.saleCountLabel);
        saleTotalLabel = reportSummaryView.findViewById(R.id.saleTotalLabel);
        voidSaleCountLabel = reportSummaryView.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabel = reportSummaryView.findViewById(R.id.voidSaleAmountLabel);
        cardCountLabel = reportSummaryView.findViewById(R.id.cardCountLabel);
        cardAmountLabel = reportSummaryView.findViewById(R.id.cardAmountLabel);

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

    private void setMeasureSummary() {
        reportSummaryView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSummaryView.layout(0, 0, reportSummaryView.getMeasuredWidth(), reportSummaryView.getMeasuredHeight());
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

    private void setMenuList() {
        if (menuRecyclerView.getAdapter() == null) {
            menuReportAdapter = new MenuReportAdapter(this);
            menuRecyclerView.setAdapter(menuReportAdapter);
            menuReportAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        typeClick = "DetailReport";
                        dialogHost.show();
                    } else if (position == 1) {
                        typeClick = "SummaryReport";
                        dialogHostSummary.show();
                    } else if (position == 2) {
                        typeClick = "TAX";
                        dialogMenuHostTwo.show();

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

        nameList.add("Detail Report");
        nameList.add("Summary Report");
        nameList.add("TAX");

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
        dialogMenu = new Dialog(this);
        dialogMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMenu.setCancelable(false);
        dialogMenu.setContentView(R.layout.dialog_custom_menu);
        dialogMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMenu.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
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
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        if (transTempList == null) {
            transTempList = new ArrayList<>();
        } else {
            transTempList.clear();
        }
        transTempList.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
        slipReportAdapter.setItem(transTempList);
        slipReportAdapter.notifyDataSetChanged();

        Double amountAll = 0.0;
        for (int i = 0; i < transTempList.size(); i++) {
            if (transTempList.get(i).getVoidFlag().equals("N")) {
                amountAll += Double.valueOf(transTempList.get(i).getAmount());
            }
        }

        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3ReportLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));

        countReportLabel.setText(transTempList.size() + "");
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
                if (transTempList.size() > 0) {
                    doPrinting(getBitmapFromView(reportDetailLinearLayout));
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

    private void selectSummaryReport(String typeHost) {
        dialogLoading.show();
        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "N").findAll();

        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "Y").findAll();
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            totalSale += Double.valueOf(transTempSale.get(i).getAmount());
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            totalVoid += Double.valueOf(transTempVoid.get(i).getAmount());
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
        switch (typeHost) {
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
                hostLabel.setText("BASE24 EPS");
                break;
            default:
                midLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                tidLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
                hostLabel.setText("KTB ON US");
                break;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        saleCountLabel.setText(String.valueOf(transTempSale.size()));
        saleTotalLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountLabel.setText(transTempVoid.size() + "");
        voidSaleAmountLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        cardCountLabel.setText(countAll + "");
        cardAmountLabel.setText(decimalFormat.format(totalSale));

        setMeasureSummary();
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (transTempSale.size() > 0 || transTempVoid.size() > 0) {
                    doPrinting(getBitmapFromView(summaryLinearLayout));
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

    private void selectSummaryTAXReport(String typeHost) {
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
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
            hostFeeLabel.setText("BASE24 EPS");
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
                hostLabel.setText("BASE24 EPS");
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
                    doPrinting(getBitmapFromView(summaryLinearFeeLayout));
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
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        final RealmResults<QrCode> qrCodes = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();

        Log.d(TAG, "selectSummaryReport: " + qrCodes.size());
        for (int i = 0; i < qrCodes.size(); i++) {
            totalSale += Double.valueOf(qrCodes.get(i).getAmount());
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

        midLabelSmQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
        tidLabelSmQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
        batchLabelSmQr.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
        hostLabelSmQr.setText("KTB QR");

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
                    doPrinting(getBitmapFromView(summaryLinearLayoutSmQr));
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
        dialogHost = new Dialog(this);
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHost.setCancelable(false);
        dialogHost.setContentView(R.layout.dialog_custom_host_qr);
        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
        qrBtn = dialogHost.findViewById(R.id.qrBtn);
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
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQrReport();
            }
        });
    }


    private void customDialogHostTwo() {
        dialogMenuHostTwo = new Dialog(this);
        dialogMenuHostTwo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMenuHostTwo.setCancelable(false);
        dialogMenuHostTwo.setContentView(R.layout.dialog_custom_host_2);
        dialogMenuHostTwo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMenuHostTwo.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogMenuHostTwo.findViewById(R.id.posBtn);
        epsBtn = dialogMenuHostTwo.findViewById(R.id.epsBtn);
        closeImage = dialogMenuHostTwo.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenuHostTwo.dismiss();
            }
        });
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatabaseTax("POS");
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatabaseTax("EPS");
            }
        });
    }

    private void customDialogHostSummaryReport() {
        dialogHostSummary = new Dialog(this);
        dialogHostSummary.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHostSummary.setCancelable(false);
        dialogHostSummary.setContentView(R.layout.dialog_custom_host_summary);
        dialogHostSummary.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogHostSummary.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posSummaryBtn = dialogHostSummary.findViewById(R.id.posBtn);
        epsSummaryBtn = dialogHostSummary.findViewById(R.id.epsBtn);
        tmsSummaryBtn = dialogHostSummary.findViewById(R.id.tmsBtn);
        qrSummaryBtn = dialogHostSummary.findViewById(R.id.qrSummaryBtn);
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
    }

    private void getDatabaseTax(String typeHost) {
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
        if (typeHost.equalsIgnoreCase("EPS")) {
            batchIdLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
            hostTaxLabel.setText("BASE24 EPS");
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
                    doPrinting(getBitmapFromView(reportTaxDetailLinearLayout));
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
        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        RealmResults<QrCode> qrCodes = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();
        if (qrCodes.size() > 0) {
            Double amount = 0.0;

            for (int i = 0; i < qrCodes.size(); i++) {
                amount += Double.valueOf(qrCodes.get(i).getAmount());
            }

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
            dateLabelQr.setText(dateFormat.format(date));
            timeLabelQr.setText(timeFormat.format(date));

            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                merchantName1QrLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_1));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                merchantName2QrLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_2));
            if (!Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                merchantName3QrLabel.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_3));


            midLabelQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
            tidLabelQr.setText(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            batchLabelQr.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuDetailReportActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
            hostLabelQr.setText("KTB QR");
            amountReportLabelQr.setText(decimalFormat.format(amount));
            countReportLabelQr.setText(qrCodes.size() + "");
            /*totalFeeLabel.setText(decimalFormat.format(amount));
            countVoidFeeLabel.setText(feeVoidSize + "");
            totalVoidFeeLabel.setText(decimalFormat.format(amountVoid));
            countGrandLabel.setText(feeSize + feeVoidSize + "");
            totalCountGrandLabel.setText(decimalFormat.format(amount));*/
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
                    doPrinting(getBitmapFromView(reportDetailLinearLayoutQr));
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
                            dismissAllDialog();
                            Intent intent = new Intent(MenuDetailReportActivity.this, MenuServiceActivity.class);
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
                dialogLoading.show();
            }
        });

    }

    private void customDialogLoading() {
        dialogLoading = new Dialog(this);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(R.layout.dialog_custom_alert_loading);
        dialogLoading.setCancelable(false);
        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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
