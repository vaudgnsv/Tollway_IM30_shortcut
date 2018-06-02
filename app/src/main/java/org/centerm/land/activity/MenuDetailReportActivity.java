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
import org.centerm.land.adapter.SlipReportAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.TransTemp;
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
        setMenuList();
        customDialogOutOfPaper();
        customDialogHost();
        customDialogHostTwo();
        reportView();
        reportSummaryView();
        setViewReportTaxDetail();
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

    private void setMeasure() {
        reportView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportView.layout(0, 0, reportView.getMeasuredWidth(), reportView.getMeasuredHeight());
    }

    private void setMeasureSummary() {
        summaryLinearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        summaryLinearLayout.layout(0, 0, summaryLinearLayout.getMeasuredWidth(), summaryLinearLayout.getMeasuredHeight());
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
                        dialogHost.show();
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
        if (transTempList == null) {
            transTempList = new ArrayList<>();
        } else {
            transTempList.clear();
        }
        transTempList.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
        slipReportAdapter.setItem(transTempList);
        slipReportAdapter.notifyDataSetChanged();

        setMeasure();
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
                        }
                    });
                }
            }
        }.start();

    }

    private void selectSummaryReport(String typeHost) {
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

        saleCountLabel.setText(String.valueOf(transTempSale.size()));
        saleTotalLabel.setText(totalSale + "");
        voidSaleCountLabel.setText(transTempVoid.size() + "");
        voidSaleAmountLabel.setText(totalVoid + "");
        countAll = transTempSale.size() + transTempVoid.size();
        cardCountLabel.setText(countAll + "");
        totalAll = totalSale - totalVoid;
        cardAmountLabel.setText(totalAll + "");

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
                if (typeClick.equals("DetailReport")) {
                    selectDetailReport("POS");
                } else if (typeClick.equals("SummaryReport")) {
                    selectSummaryReport("POS");
                } else {

                }
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("DetailReport")) {
                    selectDetailReport("EPS");
                } else if (typeClick.equals("SummaryReport")) {
                    selectSummaryReport("EPS");
                } else {
                    dialogMenuHostTwo.show();
                }
            }
        });

        tmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("DetailReport")) {
                    selectDetailReport("TMS");
                } else if (typeClick.equals("SummaryReport")) {
                    selectSummaryReport("TMS");
                } else {

                }
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReportTaxDetail.setLayoutManager(layoutManager);
    }

    private void getDatabaseTax(String typeHost) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:MM:ss");
        taxIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID));
        if (typeHost.equalsIgnoreCase("EPS")) {
            batchIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
            hostTaxLabel.setText("BASE24 EPS");
        } else {
            batchIdLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
            hostTaxLabel.setText("KTB OFFUS");
        }
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
                            dialogOutOfPaper.show();
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
