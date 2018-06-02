package org.centerm.land.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.TransTemp;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.utility.Preference;

import java.text.DecimalFormat;

import io.realm.Realm;

public class SlipTemplateActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "SlipTemplateActivity";

    private Realm realm = null;

    private CountDownTimer timer = null;

    /**
     * Slip
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
    /**
     * Slip Auto
     */
    private ImageView bankImageAuto = null;
    private ImageView bank1ImageAuto = null;
    private TextView merchantName1LabelAuto = null;
    private TextView merchantName2LabelAuto = null;
    private TextView merchantName3LabelAuto = null;
    private TextView tidLabelAuto = null;
    private TextView midLabelAuto = null;
    private TextView traceLabelAuto = null;
    private TextView systrcLabelAuto = null;
    private TextView batchLabelAuto = null;
    private TextView refNoLabelAuto = null;
    private TextView dateLabelAuto = null;
    private TextView timeLabelAuto = null;
    private TextView typeLabelAuto = null;
    private TextView typeCardLabelAuto = null;
    private TextView cardNoLabelAuto = null;
    private TextView apprCodeLabelAuto = null;
    private TextView comCodeLabelAuto = null;
    private TextView amtThbLabelAuto = null;
    private TextView feeThbLabelAuto = null;
    private TextView totThbLabelAuto = null;
    private TextView ref1LabelAuto = null;
    private TextView ref2LabelAuto = null;
    private TextView ref3LabelAuto = null;
    private RelativeLayout ref1RelativeLayoutAuto = null;
    private RelativeLayout ref2RelativeLayoutAuto = null;
    private RelativeLayout ref3RelativeLayoutAuto = null;

    private Button printBtn;
    private AidlPrinter printDev = null;
    private AidlDeviceManager manager = null;
    private NestedScrollView slipNestedScrollView;
    private NestedScrollView slipNestedScrollViewAuto;
    private LinearLayout slipLinearLayout;
    private LinearLayout slipLinearLayoutAuto;
    private CardManager cardManager = null;

    private int saleId;
    private String typeSlip;
    private View printFirst;

    private boolean statusOutScress = false;

    private AidlPrinterStateChangeListener.Stub callBackPrint = null;
    private Dialog dialogOutOfPaper;
    private Button okBtn;

    private Bitmap bitmapOld = null;
    private TextView msgLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip_template);
        initData();
        initWidget();
//        initBtnExit();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            saleId = bundle.getInt(CalculatePriceActivity.KEY_CALCUATE_ID);
            typeSlip = bundle.getString(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID);
        }
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        cardManager.abortPBOCProcess();
        printDev = cardManager.getInstancesPrint();

        setViewPrintFirst();
        customDialogOutOfPaper();

        printBtn = findViewById(R.id.printBtn);
        slipNestedScrollView = findViewById(R.id.slipNestedScrollView);
        slipLinearLayout = findViewById(R.id.slipLinearLayout);
        bankImage = findViewById(R.id.bankImage);
        bank1Image = findViewById(R.id.bank1Image);
        merchantName1Label = findViewById(R.id.merchantName1Label);
        merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2Label = findViewById(R.id.merchantName2Label);
        merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3Label = findViewById(R.id.merchantName3Label);
        merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        tidLabel = findViewById(R.id.tidLabel);
        midLabel = findViewById(R.id.midLabel);
        traceLabel = findViewById(R.id.traceLabel);
        systrcLabel = findViewById(R.id.systrcLabel);
        batchLabel = findViewById(R.id.batchLabel);
        refNoLabel = findViewById(R.id.refNoLabel);
        dateLabel = findViewById(R.id.dateLabel);
        timeLabel = findViewById(R.id.timeLabel);
        typeLabel = findViewById(R.id.typeLabel);
        typeCardLabel = findViewById(R.id.typeCardLabel);
        cardNoLabel = findViewById(R.id.cardNoLabel);
        apprCodeLabel = findViewById(R.id.apprCodeLabel);
        comCodeLabel = findViewById(R.id.comCodeLabel);
        amtThbLabel = findViewById(R.id.amtThbLabel);
        feeThbLabel = findViewById(R.id.feeThbLabel);
        totThbLabel = findViewById(R.id.totThbLabel);
        ref1Label = findViewById(R.id.ref1Label);
        ref2Label = findViewById(R.id.ref2Label);
        ref3Label = findViewById(R.id.ref3Label);
        ref1RelativeLayout = findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayout = findViewById(R.id.ref2RelativeLayout);
        ref3RelativeLayout = findViewById(R.id.ref3RelativeLayout);
        printBtn.setOnClickListener(this);
        selectSALE();
    }

    private void setViewPrintFirst() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printFirst = inflater.inflate(R.layout.view_report_sale, null);

        slipNestedScrollViewAuto = printFirst.findViewById(R.id.slipNestedScrollView);
        slipLinearLayoutAuto = printFirst.findViewById(R.id.slipLinearLayout);
        bankImageAuto = printFirst.findViewById(R.id.bankImage);
        bank1ImageAuto = printFirst.findViewById(R.id.bank1Image);
        merchantName1LabelAuto = printFirst.findViewById(R.id.merchantName1Label);
        merchantName1LabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2LabelAuto = printFirst.findViewById(R.id.merchantName2Label);
        merchantName2LabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3LabelAuto = printFirst.findViewById(R.id.merchantName3Label);
        merchantName3LabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        tidLabelAuto = printFirst.findViewById(R.id.tidLabel);
        midLabelAuto = printFirst.findViewById(R.id.midLabel);
        traceLabelAuto = printFirst.findViewById(R.id.traceLabel);
        systrcLabelAuto = printFirst.findViewById(R.id.systrcLabel);
        batchLabelAuto = printFirst.findViewById(R.id.batchLabel);
        refNoLabelAuto = printFirst.findViewById(R.id.refNoLabel);
        dateLabelAuto = printFirst.findViewById(R.id.dateLabel);
        timeLabelAuto = printFirst.findViewById(R.id.timeLabel);
        typeLabelAuto = printFirst.findViewById(R.id.typeLabel);
        typeCardLabelAuto = printFirst.findViewById(R.id.typeCardLabel);
        cardNoLabelAuto = printFirst.findViewById(R.id.cardNoLabel);
        apprCodeLabelAuto = printFirst.findViewById(R.id.apprCodeLabel);
        comCodeLabelAuto = printFirst.findViewById(R.id.comCodeLabel);
        amtThbLabelAuto = printFirst.findViewById(R.id.amtThbLabel);
        feeThbLabelAuto = printFirst.findViewById(R.id.feeThbLabel);
        totThbLabelAuto = printFirst.findViewById(R.id.totThbLabel);
        ref1LabelAuto = printFirst.findViewById(R.id.ref1Label);
        ref2LabelAuto = printFirst.findViewById(R.id.ref2Label);
        ref3LabelAuto = printFirst.findViewById(R.id.ref3Label);
        ref1RelativeLayoutAuto = printFirst.findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayoutAuto = printFirst.findViewById(R.id.ref2RelativeLayout);
        ref3RelativeLayoutAuto = printFirst.findViewById(R.id.ref3RelativeLayout);


    }

    private void selectSALE() {
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            TransTemp transTemp = realm.where(TransTemp.class).equalTo("id", saleId).findFirst();
            Log.d(TAG, "selectSALE: " + transTemp.getCardNo());
            setDataView(transTemp);
            setDataViewAuto(transTemp);
        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }

    }

    private void setDataView(TransTemp item) {
        if (item.getHostTypeCard().equalsIgnoreCase("POS")) {
            Preference.getInstance(SlipTemplateActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_POS, item.getId());
        } else if (item.getHostTypeCard().equalsIgnoreCase("EPS")) {
            Preference.getInstance(SlipTemplateActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_EPS, item.getId());
        } else {
            Preference.getInstance(SlipTemplateActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_TMS, item.getId());
        }
        DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText(item.getTid());
        midLabel.setText(item.getMid());
        traceLabel.setText(item.getEcr());
        systrcLabel.setText(item.getTraceNo());
        if (CardPrefix.getTypeCard(item.getCardNo()).equalsIgnoreCase("POS"))
            batchLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
        else if (CardPrefix.getTypeCard(item.getCardNo()).equalsIgnoreCase("EPS"))
            batchLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
        else if (CardPrefix.getTypeCard(item.getCardNo()).equalsIgnoreCase("TMS"))
            batchLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS));
        refNoLabel.setText(item.getRefNo());
        dateLabel.setText(item.getTransDate());
        timeLabel.setText(item.getTransTime());
        typeLabel.setText(item.getTransStat());
        typeCardLabel.setText(CardPrefix.getTypeCardName(item.getCardNo()));
        String cutCardStart = item.getCardNo().substring(0, 6);
        String cutCardEnd = item.getCardNo().substring(12, item.getCardNo().length());
        cardNoLabel.setText(cutCardStart + "XXXXXX" + cutCardEnd);
        apprCodeLabel.setText(item.getApprvCode());
        comCodeLabel.setText(item.getComCode());
        if (typeSlip.equalsIgnoreCase(CalculatePriceActivity.TypeSale)) {
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getAmount()))));
            Log.d(TAG, "setDataView if : " + item.getAmount() + " Fee : " + item.getFee());
            if (item.getFee() != null) {
                feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(item.getFee()))));
                double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getFee())));
                double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getAmount())));
                totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((float) (amount + fee))));
            } else {
                feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
            }
        } else {
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Float.valueOf(item.getAmount()))));

            if (item.getHostTypeCard().equals("TMS")) {
                if (!item.getEmciFree().isEmpty()) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Float.valueOf(item.getEmciFree()))));
                    float fee = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getEmciFree())));
                    float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((float) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
                Log.d(TAG, "setDataView Else : " + item.getAmount() + " Fee : " + item.getEmciFree());
            } else {
                if (item.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Float.valueOf(item.getFee()))));
                    float fee = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getFee())));
                    float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((float) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }
        if (!item.getRef1().isEmpty()) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(item.getRef1());
        }
        if (!item.getRef2().isEmpty()) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(item.getRef2());
        }
        if (!item.getRef3().isEmpty()) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(item.getRef3());
        }
    }

    private void setDataViewAuto(TransTemp item) {

        DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabelAuto.setText(item.getTid());
        midLabelAuto.setText(item.getMid());
        traceLabelAuto.setText(item.getEcr());
        systrcLabelAuto.setText(item.getTraceNo());
        if (CardPrefix.getTypeCard(item.getCardNo()).equalsIgnoreCase("POS"))
            batchLabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
        else if (CardPrefix.getTypeCard(item.getCardNo()).equalsIgnoreCase("EPS"))
            batchLabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
        else if (CardPrefix.getTypeCard(item.getCardNo()).equalsIgnoreCase("TMS"))
            batchLabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS));
        refNoLabelAuto.setText(item.getRefNo());
        dateLabelAuto.setText(item.getTransDate());
        timeLabelAuto.setText(item.getTransTime());
        typeLabelAuto.setText(item.getTransStat());
        typeCardLabelAuto.setText(CardPrefix.getTypeCardName(item.getCardNo()));
        String cutCardStart = item.getCardNo().substring(0, 6);
        String cutCardEnd = item.getCardNo().substring(12, item.getCardNo().length());
        cardNoLabelAuto.setText(cutCardStart + "XXXXXX" + cutCardEnd);
        apprCodeLabelAuto.setText(item.getApprvCode());
        comCodeLabelAuto.setText(item.getComCode());
        if (typeSlip.equalsIgnoreCase(CalculatePriceActivity.TypeSale)) {
            amtThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getAmount()))));
            Log.d(TAG, "setDataView if : " + item.getAmount() + " Fee : " + item.getFee());
            if (item.getFee() != null) {
                feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(item.getFee()))));
                double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getFee())));
                double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getAmount())));
                totThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((float) (amount + fee))));
            } else {
                feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount, "0.00"));
                totThbLabelAuto.setText(getString(R.string.slip_pattern_amount, "0.00"));
            }
        } else {
            amtThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Float.valueOf(item.getAmount()))));
            Log.d(TAG, "setDataView Else : " + item.getAmount() + " Fee : " + item.getFee());
            if (item.getHostTypeCard().equals("TMS")) {
                if (!item.getEmciFree().isEmpty()) {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Float.valueOf(item.getEmciFree()))));
                    float fee = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getEmciFree())));
                    float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount())));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((float) (amount + fee))));
                } else {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            } else {
                if (item.getFee() != null) {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Float.valueOf(item.getFee()))));
                    float fee = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getFee())));
                    float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount())));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((float) (amount + fee))));
                } else {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }
        if (!item.getRef1().isEmpty()) {
            ref1RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref1LabelAuto.setText(item.getRef1());
        }
        if (!item.getRef2().isEmpty()) {
            ref2RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref2LabelAuto.setText(item.getRef2());
        }
        if (!item.getRef3().isEmpty()) {
            ref3RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref3LabelAuto.setText(item.getRef3());
        }

        setMeasure();

        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                doPrinting(getBitmapFromView(slipLinearLayoutAuto));
                autoPrint();
            }
        }.start();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void autoPrint() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                statusOutScress = true;
                doPrinting(getBitmapFromView(slipLinearLayout));
            }
        };
        timer.start();
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
                            Log.d(TAG, "onPrintFinish: ");
                            if (statusOutScress) {
                                Intent intent = new Intent(SlipTemplateActivity.this, MenuServiceActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            } else {
                                if (timer != null) {
                                    timer.cancel();
                                    timer.start();
                                }
                            }
                        }

                        @Override
                        public void onPrintError(int i) throws RemoteException {
                            Log.d(TAG, "onPrintError: ");
                            msgLabel.setText("เกิดข้อผิดพลาด");
                            dialogOutOfPaper.show();
                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {
                            Log.d(TAG, "onPrintOutOfPaper: ");
                            if (!statusOutScress) {
                                timer.cancel();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    dialogOutOfPaper.show();
                                }
                            });
                        }
                    });
//                    int ret = printDev.printBarCodeSync("asdasd");
//                    Log.d(TAG, "after call printData ret = " + ret);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setMeasure() {
        printFirst.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printFirst.layout(0, 0, printFirst.getMeasuredWidth(), printFirst.getMeasuredHeight());
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

    @Override
    public void onClick(View v) {
        if (v == printBtn) {
            statusOutScress = true;
            doPrinting(getBitmapFromView(slipLinearLayout));
            if (timer != null) {
                timer.cancel();
            }
        }
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
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
