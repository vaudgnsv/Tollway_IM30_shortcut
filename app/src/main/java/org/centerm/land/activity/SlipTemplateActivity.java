package org.centerm.land.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
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

    private Button printBtn;
    private AidlPrinter printDev = null;
    private AidlDeviceManager manager = null;
    private NestedScrollView slipNestedScrollView;
    private LinearLayout slipLinearLayout;
    private CardManager cardManager = null;

    private int saleId;
    private String typeSlip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip_template);
        initData();
        initWidget();
        initBtnExit();
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
        super.initWidget();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        printBtn = findViewById(R.id.printBtn);
        slipNestedScrollView = findViewById(R.id.slipNestedScrollView);
        slipLinearLayout = findViewById(R.id.slipLinearLayout);
        bankImage = findViewById(R.id.bankImage);
        bank1Image = findViewById(R.id.bank1Image);
        merchantName1Label = findViewById(R.id.merchantName1Label);
        merchantName2Label = findViewById(R.id.merchantName2Label);
        merchantName3Label = findViewById(R.id.merchantName3Label);
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

    private void selectSALE() {
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            TransTemp transTemp = realm.where(TransTemp.class).equalTo("id", saleId).findFirst();
            Log.d(TAG, "selectSALE: " + transTemp.getCardNo());
            setDataView(transTemp);
        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }

    }

    private void setDataView(TransTemp item) {

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
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
        typeCardLabel.setText(item.getCardType());
        cardNoLabel.setText(item.getCardNo());
        apprCodeLabel.setText(item.getApprvCode());
        comCodeLabel.setText(item.getComCode());
        if (typeSlip.equalsIgnoreCase(CalculatePriceActivity.TypeSale)) {
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Float.valueOf(item.getAmount()))));
            if (item.getEmciFree() != null) {
                feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Integer.valueOf(item.getEmciFree()))));
                float fee = Float.parseFloat(decimalFormat.format(Integer.valueOf(item.getEmciFree())));
                float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount())));
                totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format((float) (amount + fee))));
            } else {
                feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
            }
        } else {
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Float.valueOf(item.getAmount()))));
            if (item.getEmciFree() != null) {
                feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Integer.valueOf(item.getEmciFree()))));
                float fee = Float.parseFloat(decimalFormat.format(Integer.valueOf(item.getEmciFree())));
                float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount())));
                totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format((float) (amount + fee))));
            } else {
                feeThbLabel.setText(getString(R.string.slip_pattern_amount_void,"0.00"));
                totThbLabel.setText(getString(R.string.slip_pattern_amount_void,"0.00"));
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


    @Override
    protected void onResume() {
        super.onResume();

    }

    public void doPrinting(final Bitmap slip) {
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    int ret = printDev.printBmpFastSync(slip, Constant.ALIGN.CENTER);
//                    int ret = printDev.printBarCodeSync("asdasd");
                    Log.d(TAG, "after call printData ret = " + ret);
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

    @Override
    public void onClick(View v) {
        if (v == printBtn) {
            doPrinting(getBitmapFromView(slipLinearLayout));
        }
    }
}
