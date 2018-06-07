package org.centerm.land.activity.settlement;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.util.Log;
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
import org.centerm.land.activity.MenuServiceActivity;
import org.centerm.land.activity.SlipTemplateActivity;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.TransTemp;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.utility.Preference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_slip_settlement);
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
        slipNestedScrollView = findViewById(R.id.slipNestedScrollView);
        settlementLinearLayout = findViewById(R.id.settlementLinearLayout);
        dateLabel = findViewById(R.id.dateLabel);
        timeLabel = findViewById(R.id.timeLabel);
        midLabel = findViewById(R.id.midLabel);
        tidLabel = findViewById(R.id.tidLabel);
        batchLabel = findViewById(R.id.batchLabel);
        hostLabel = findViewById(R.id.hostLabel);
        saleCountLabel = findViewById(R.id.saleCountLabel);
        saleTotalLabel = findViewById(R.id.saleTotalLabel);
        voidSaleCountLabel = findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabel = findViewById(R.id.voidSaleAmountLabel);
        cardCountLabel = findViewById(R.id.cardCountLabel);
        cardAmountLabel = findViewById(R.id.cardAmountLabel);
        bank1Image = findViewById(R.id.bank1Image);
        bankImage = findViewById(R.id.bankImage);

        merchantName1Label = findViewById(R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2Label = findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3Label = findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        customDialogAlertLoading();
        customDialogOutOfPaper();

        setViewSlip();
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                dialogAlertLoading.dismiss();
                doPrinting(getBitmapFromView(settlementLinearLayout));
                cardManager.deleteTransTemp();
            }
        }.start();

    }

    public void customDialogAlertLoading() {
        dialogAlertLoading = new Dialog(this);
        dialogAlertLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlertLoading.setContentView(R.layout.dialog_custom_alert_loading);
        dialogAlertLoading.setCancelable(false);
        dialogAlertLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlertLoading.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogAlertLoading.show();
    }


    private void setViewSlip() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll();
        double amountSale = 0;
        double amountVoid = 0;
        for (int i = 0; i < transTemp.size(); i++) {
            amountSale += Double.valueOf(transTemp.get(i).getAmount());
        }
        RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("hostTypeCard", typeHost).findAll();
        for (int i = 0; i < transTempVoid.size(); i++) {
            amountVoid += Double.valueOf(transTempVoid.get(i).getAmount());
        }
        Date date = new Date();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        voidSaleCountLabel.setText(transTempVoid.size() + "");
        voidSaleAmountLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(amountVoid)));
        saleCountLabel.setText(transTemp.size() + "");
        saleTotalLabel.setText(decimalFormat.format(amountSale));
        cardCountLabel.setText((transTemp.size() + transTempVoid.size()) + "");
        cardAmountLabel.setText(decimalFormat.format(amountSale));
        dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));


        if (typeHost.equalsIgnoreCase("POS")) {
            hostLabel.setText("KTB Off us");
            int batch = Integer.parseInt(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
            batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
            tidLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            midLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_DATE_POS, dateLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_TIME_POS, timeLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS, saleTotalLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_POS, saleCountLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_POS, voidSaleCountLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS, voidSaleAmountLabel.getText().toString());
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            hostLabel.setText("BASE24 EPS");
            int batch = Integer.parseInt(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
            batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
            tidLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            midLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_DATE_EPS, dateLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_TIME_EPS, timeLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS, saleTotalLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS, saleCountLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS, voidSaleCountLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS, voidSaleAmountLabel.getText().toString());
        } else {
            hostLabel.setText("KTB On Us");

            int batch = Integer.parseInt(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)) - 1;
            batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
            tidLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_DATE_TMS, dateLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_TIME_TMS, timeLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS, saleTotalLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS, saleCountLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS, voidSaleCountLabel.getText().toString());
            Preference.getInstance(this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS, voidSaleAmountLabel.getText().toString());
        }
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
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Intent intent = new Intent(SlipSettlementActivity.this, MenuServiceActivity.class);
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
}
