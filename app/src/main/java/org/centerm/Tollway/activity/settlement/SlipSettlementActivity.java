package org.centerm.Tollway.activity.settlement;

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

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.adapter.SlipSummaryReportCardAdapter;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
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

public class SlipSettlementActivity extends SettingToolbarActivity {

    private final String TAG = "SlipSettlementActivity";
    private NestedScrollView slipNestedScrollView = null;
    private LinearLayout settlementLinearLayout;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView midLabel = null;
    private TextView tidLabel = null;
    private TextView batchLabel = null;
    private TextView hostLabel = null;
    private TextView saleCountLabel = null;
    private TextView saleTotalLabel = null;
    private TextView voidSaleCountLabel = null;
    private TextView voidSaleAmountLabel = null;
    private TextView cardCountLabel = null;
    private TextView cardAmountLabel = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private ImageView bank1Image = null;
    private ImageView bankImage = null;

    private Realm realm = null;

    private String typeHost = null;
    private CardManager cardManager = null;
    private AidlPrinter printDev;

    private Dialog dialogAlertLoading;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld = null;

    /**
     * TAX FEE
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
    private Double totalSale = 0.0;
    private Double totalVoid = 0.0;
    private int countAll;

    private boolean isPrintSettlement = false;

    private RecyclerView recyclerViewCardReportSummary;      // Paul_20181203
    private LinearLayout reportCardSummaryLinearLayout = null;   // Paul_20181203
    private List<TransTemp> CardTypeDB = null;      // Paul_20181202
    private SlipSummaryReportCardAdapter slipSummaryReportCardAdapter;    // Paul_20181203 Card Type Add
//    private SlipReportCardSettlementAdapter slipSummaryReportCardAdapter;    // Paul_20181203 Card Type Add
    private TextView DuplicatelLabel;       // Paul_20181205

//    private List<summarySettlementDB> summaryTempList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.view_slip_settlement);        // Paul_20181203
        setContentView(R.layout.view_slip_card_settlement);     // Paul_20181203
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        initData();
        initWidget();
        initBtnExit();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeHost = bundle.getString(MenuSettlementActivity.KEY_TYPE_HOST);
        }
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        recyclerViewCardReportSummary = findViewById(R.id.recyclerViewReportSettlement);  // Paul_20181203

        reportCardSummaryLinearLayout = findViewById(R.id.summaryLinearLayout);  // Paul_20181203
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);          // Paul_20181202
        recyclerViewCardReportSummary.setLayoutManager(layoutManager2);                              // Paul_20181202

        slipNestedScrollView = findViewById(R.id.slipNestedScrollView);
        settlementLinearLayout = findViewById(R.id.settlementLinearLayout);
        dateLabel = findViewById(R.id.dateLabel);
        timeLabel = findViewById(R.id.timeLabel);
        midLabel = findViewById(R.id.midLabel);
        tidLabel = findViewById(R.id.tidLabel);
        batchLabel = findViewById(R.id.batchLabel);
        hostLabel = findViewById(R.id.hostLabel);

        DuplicatelLabel = findViewById(R.id.duplicateLabel);
/*
        saleCountLabel = findViewById(R.id.saleCountLabel);
        saleTotalLabel = findViewById(R.id.saleTotalLabel);
        voidSaleCountLabel = findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabel = findViewById(R.id.voidSaleAmountLabel);
        cardCountLabel = findViewById(R.id.cardCountLabel);
        cardAmountLabel = findViewById(R.id.cardAmountLabel);
*/
//        bank1Image = findViewById(R.id.bank1Image);
//        bankImage = findViewById(R.id.bankImage);
        reportSummaryFeeView();

        merchantName1Label = findViewById(R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2Label = findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3Label = findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

//       DuplicatelLabel = findViewById(R.id.duplicateLabel);

        DuplicatelLabel.setVisibility( View.VISIBLE );

        customDialogAlertLoading();
        customDialogOutOfPaper();



        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {


                dialogAlertLoading.dismiss();

//                DuplicatelLabel.setVisibility( View.VISIBLE );
//                Utility.SettlementReprintBmpWrite(typeHost,getBitmapFromView(settlementLinearLayout));    // Paul_20181205 settlement reprint modify
//                DuplicatelLabel.setVisibility( View.GONE );

                //doPrinting(getBitmapFromView(settlementLinearLayout));
                cardManager.deleteTransTemp();
            }
        }.start();

    }

    private void reportSummaryFeeView() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSummaryFeeView = inflater.inflate(R.layout.view_silp_report_fee_settlement, null);
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

    private void setMeasureFeeSummary() {
        reportSummaryFeeView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSummaryFeeView.layout(0, 0, reportSummaryFeeView.getMeasuredWidth(), reportSummaryFeeView.getMeasuredHeight());
    }

    public void customDialogAlertLoading() {
        dialogAlertLoading = new Dialog(this);
        dialogAlertLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlertLoading.setContentView(R.layout.dialog_custom_load_process);
        dialogAlertLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlertLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogAlertLoading.findViewById(R.id.waitingImage);
        TextView msgLabel = dialogAlertLoading.findViewById(R.id.msgLabel);
        msgLabel.setText("กรุณารอสักครู่...");
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
//        dialogAlertLoading.show();
    }


    private void setViewSlip() {

        System.out.printf("utility:: %s setViewSlip 00000001 \n",TAG);

        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll();
        double amountSale = 0;
        double amountVoid = 0;
        for (int i = 0; i < transTemp.size(); i++) {
            amountSale += Double.valueOf(transTemp.get(i).getAmount().replaceAll(",",""));
        }
        RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("hostTypeCard", typeHost).findAll();
        for (int i = 0; i < transTempVoid.size(); i++) {
            amountVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",",""));
        }
        Date date = new Date();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
/*
        voidSaleCountLabel.setText(transTempVoid.size() + "");
//        voidSaleAmountLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(amountVoid)));
        voidSaleAmountLabel.setText(getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));  //SINN 20181113 last requirement no need symbol minus
        saleCountLabel.setText(transTemp.size() + "");
        saleTotalLabel.setText(decimalFormat.format(amountSale));
        cardCountLabel.setText((transTemp.size() + transTempVoid.size()) + "");
        cardAmountLabel.setText(decimalFormat.format(amountSale));
  */
        dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));


        if (typeHost.equalsIgnoreCase("POS")) {
            hostLabel.setText("KTB OFFUS");     // Paul_20181028 Sinn merge version UAT6_0016
            int batch = Integer.parseInt(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
            batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
            tidLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            midLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_DATE_POS, dateLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_TIME_POS, timeLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS, saleTotalLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_POS, saleCountLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_POS, voidSaleCountLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS, voidSaleAmountLabel.getText().toString());

            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS, decimalFormat.format(amountSale));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_POS, transTemp.size() + "");
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_POS, transTempVoid.size() + "");
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));

            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_BATCH_POS, CardPrefix.calLen(String.valueOf(batch), 6));
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            hostLabel.setText("BASE24 EPS");
            int batch = Integer.parseInt(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
            batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
            tidLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            midLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_DATE_EPS, dateLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_TIME_EPS, timeLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS, saleTotalLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS, saleCountLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS, voidSaleCountLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS, voidSaleAmountLabel.getText().toString());


            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS, decimalFormat.format(amountSale));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS, transTemp.size() + "");
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS, transTempVoid.size() + "");
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));

            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_BATCH_EPS, CardPrefix.calLen(String.valueOf(batch), 6));
        } else {
            hostLabel.setText("KTB ONUS");      // Paul_20181028 Sinn merge version UAT6_0016

            int batch = Integer.parseInt(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)) - 1;
            batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
            tidLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_DATE_TMS, dateLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_TIME_TMS, timeLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS, saleTotalLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS, saleCountLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS, voidSaleCountLabel.getText().toString());
//            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS, voidSaleAmountLabel.getText().toString());

            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS, decimalFormat.format(amountSale));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS, transTemp.size() + "");
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS, transTempVoid.size() + "");
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));

            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_BATCH_TMS, CardPrefix.calLen(String.valueOf(batch), 6));
        }

        // Paul_20181202
        if (CardTypeDB == null) {
            CardTypeDB = new ArrayList<>();
        } else {
            CardTypeDB.clear();
        }


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

                //            CardTypeDB.clear();
                CardTypeDB.addAll( realm.where( TransTemp.class ).equalTo( "hostTypeCard", typeHost ).equalTo("CardTypeHolder",CardTypeHolder).findAll() );
                System.out.printf("utility:: %s CardTypeDB.size() = %d \n",TAG,CardTypeDB.size());
                slipSummaryReportCardAdapter.setItem( CardTypeDB );
            }
        }

        slipSummaryReportCardAdapter.notifyDataSetChanged();
// Paul_20181202 End

//        DuplicatelLabel.setVisibility( View.VISIBLE );
//        Utility.SettlementReprintBmpWrite(typeHost,getBitmapFromView(settlementLinearLayout));    // Paul_20181205 settlement reprint modify
//        DuplicatelLabel.setVisibility( View.GONE );

/*
        DuplicatelLabel.setVisibility( View.VISIBLE );

        // Paul_20181205 Start reprint bitmap write
        Bitmap bitmap_file =  getBitmapFromView(settlementLinearLayout);
        String bmp_file = "/sdcard/oversea_ct/gtms/aaa.bmp";
        File file = new File(bmp_file);
        try {

            OutputStream outStream = new FileOutputStream(file);
            bitmap_file.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
            outStream.flush();
            outStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        DuplicatelLabel.setVisibility( View.GONE );

        System.out.printf("utility:: %s  bitmap write success \n",TAG);
        // Paul_20181205 End reprint bitmap write
*/
    }

    private void selectSummaryTAXReport(String typeHost) {
//        dialogLoading.show();
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "N").findAll();

        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "Y").findAll();
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            totalSale += Double.valueOf(transTempSale.get(i).getFee());
        }
        Log.d(TAG, "selectSummaryTAXReport typeHost:" + typeHost);

        if (typeHost.equalsIgnoreCase("POS")) {
            hostFeeLabel.setText("KTB OFFUS");
        } else {
            hostFeeLabel.setText("WAY4");
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            totalVoid += Double.valueOf(transTempVoid.get(i).getFee());
        }

        if (typeHost.equalsIgnoreCase("POS")) {
            Preference.getInstance(SlipSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS, String.valueOf(totalSale));
            Preference.getInstance(SlipSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_POS, String.valueOf(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        } else {
            Preference.getInstance(SlipSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS, String.valueOf(totalSale));
            Preference.getInstance(SlipSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_EPS, String.valueOf(totalVoid));

            int batch = Integer.parseInt(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        }


        if (!Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1FeeLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2FeeLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3FeeLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFeeLabel.setText(dateFormat.format(date));
        timeFeeLabel.setText(timeFormat.format(date));
        /*switch (typeHost) {
            case "POS":
                midLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                tidLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)), 6));
                hostLabel.setText("KTB Off US");
                break;
            case "EPS":
                midLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                tidLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)), 6));
                hostLabel.setText("BASE24 EPS");
                break;
            default:
                midLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                tidLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
                hostLabel.setText("KTB ONUS");
                break;
        }*/
        taxIdFeeLabel.setText(Preference.getInstance(SlipSettlementActivity.this).getValueString(Preference.KEY_TAX_ID));
        saleCountFeeLabel.setText(String.valueOf(transTempSale.size()));
        saleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountFeeLabel.setText(transTempVoid.size() + "");
        voidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        cardCountFeeLabel.setText(countAll + "");
        cardAmountFeeLabel.setText(decimalFormat.format(totalSale));

        setMeasureFeeSummary();
        /*new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "millisUntilFinished : " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (transTempSale.size() > 0 || transTempVoid.size() > 0) {

                } else {
                    Utility.customDialogAlert(SlipSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
//                            dialogLoading.dismiss();
                        }
                    });
                }
            }
        }.start();*/
        realm.close();
        realm = null;
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

    public void doPrinting(final Bitmap slip) {
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
                            if (isPrintSettlement) {
//                                Intent intent = new Intent(SlipSettlementActivity.this, MenuServiceActivity.class);
                                Intent intent = new Intent(SlipSettlementActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            } else {
                                isPrintSettlement = true;
                                //doPrinting(getBitmapFromView(summaryLinearFeeLayout));
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
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        setViewSlip();
        Log.d(TAG, "typeHost:" + typeHost.toString());
/*
*/

//        if (!typeHost.equalsIgnoreCase("TMS")) {
//        if ((!typeHost.equalsIgnoreCase("TMS")) && (!typeHost.equalsIgnoreCase("QR"))) {//20180720 SINN NO NEED QR TAX
        if ((!typeHost.equalsIgnoreCase("TMS")) && (!typeHost.equalsIgnoreCase("QR"))&&Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID).length()>=10) {//20180720 SINN NO NEED QR TAX
            selectSummaryTAXReport(typeHost);
        } else {
            isPrintSettlement = true;
        }
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
