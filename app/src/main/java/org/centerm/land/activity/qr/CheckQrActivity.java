package org.centerm.land.activity.qr;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.constant.Constant;
import com.google.gson.JsonElement;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.QrCode;
import org.centerm.land.manager.HttpManager;
import org.centerm.land.model.Check;
import org.centerm.land.utility.DecimalDigitsInputFilter;
import org.centerm.land.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;

public class CheckQrActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "CheckQrActivity";

    private EditText traceBox = null;
    private Button checkBtn = null;
    private Realm realm = null;
    private Dialog dialogAlertPrint;

    private ImageView bankImage = null;
    private ImageView bank1Image = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private TextView qrTidLabel = null;
    private TextView billerLabel = null;
    private TextView traceLabel = null;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView comCodeLabel = null;
    private TextView amtThbLabel = null;
    private ImageView qrImage = null;
    private RelativeLayout ref1RelativeLayout = null;
    private TextView ref1Label = null;
    private RelativeLayout ref2RelativeLayout = null;
    private TextView ref2Label = null;
    private LinearLayout slipLinearLayout = null;
    private Button printBtn = null;
    private AidlPrinter printDev = null;
    private QrCode qrCode;
    private CardManager cardManager = null;
    private int id;
    private View slipView;
    private Check check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_qr);
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        traceBox = findViewById(R.id.traceBox);
        checkBtn = findViewById(R.id.checkBtn);
        setViewPrintSlip();
        setDefaultTrace();
        checkBtn.setOnClickListener(this);
    }

    private void setDefaultTrace() {
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            RealmResults<QrCode> allTransactions = realm.where(QrCode.class).findAll();

//If you have an incrementing id column, do this
            qrCode = allTransactions.last();
            if (qrCode != null) {
                traceBox.setText(qrCode.getTrace());
            }
        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }
    private void setMeasureSlip() {
        slipView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        slipView.layout(0, 0, slipView.getMeasuredWidth(), slipView.getMeasuredHeight());
    }

    private void selectQr(final String traceId) {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                qrCode = realm.where(QrCode.class).equalTo("trace", traceId).findFirst();
                Log.d(TAG, "execute: " + qrCode.toString());
                qrTidLabel.setText(qrCode.getQrTid());
                billerLabel.setText(qrCode.getBillerId());
                traceLabel.setText(qrCode.getTrace());
                dateLabel.setText(qrCode.getDate());
                timeLabel.setText(qrCode.getTime());
                comCodeLabel.setText(qrCode.getComCode());
                amtThbLabel.setText(getString(R.string.slip_pattern_amount, qrCode.getAmount()));
                if (qrCode.getRef1() != null) {
                    ref1RelativeLayout.setVisibility(View.VISIBLE);
                    ref1Label.setText(qrCode.getRef1());
                }
                if (qrCode.getRef2() != null) {
                    ref2RelativeLayout.setVisibility(View.VISIBLE);
                    ref2Label.setText(qrCode.getRef2());
                }
//                qrImage.setImageBitmap(Utility.createQRImage(qrCode.getTextQrGenerateAll(), 300, 300));
                setMeasureSlip();

                check = new Check();
                check.setBillerId(qrCode.getBillerId());
                check.setTerminalId(qrCode.getQrTid());
                check.setRef1(qrCode.getRef1());
                check.setRef2(qrCode.getRef2());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                requestCheckSlip();
                if (realm != null) {
                    realm.close();
                    realm = null;
                }
            }
        });
    }

    public void setViewPrintSlip() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        slipView = inflater.inflate(R.layout.view_slip_qr, null);

        bankImage = slipView.findViewById(R.id.bankImage);
        bank1Image = slipView.findViewById(R.id.bank1Image);

        merchantName1Label = slipView.findViewById(R.id.merchantName1Label);
        merchantName2Label = slipView.findViewById(R.id.merchantName2Label);
        merchantName3Label = slipView.findViewById(R.id.merchantName3Label);
        qrTidLabel = slipView.findViewById(R.id.qrTidLabel);
        billerLabel = slipView.findViewById(R.id.billerLabel);
        traceLabel = slipView.findViewById(R.id.traceLabel);
        dateLabel = slipView.findViewById(R.id.dateLabel);
        timeLabel = slipView.findViewById(R.id.timeLabel);
        comCodeLabel = slipView.findViewById(R.id.comCodeLabel);
        amtThbLabel = slipView.findViewById(R.id.amtThbLabel);
//        qrImage = slipView.findViewById(R.id.qrImage);
        ref1RelativeLayout = slipView.findViewById(R.id.ref1RelativeLayout);
        ref1Label = slipView.findViewById(R.id.ref1Label);
        ref2RelativeLayout = slipView.findViewById(R.id.ref2RelativeLayout);
        ref2Label = slipView.findViewById(R.id.ref2Label);
        slipLinearLayout = slipView.findViewById(R.id.slipLinearLayout);
        printBtn = slipView.findViewById(R.id.printBtn);
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

    private void requestCheckSlip() {
        HttpManager.getInstance().getService().checkQr(check)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<JsonElement>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<JsonElement> jsonElementResponse) {
                        Log.d(TAG, "onNext: " + jsonElementResponse);
                        Log.d(TAG, "onNext: " + jsonElementResponse.body());
                        Toast.makeText(CheckQrActivity.this, "" + jsonElementResponse.body(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(CheckQrActivity.this, "" + jsonElementResponse, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject object = new JSONObject(jsonElementResponse.body().toString());
                            String code = object.getString("code");
                            if (code.equalsIgnoreCase("00000")) {
//                                        slipSuccessLinearLayout.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                                doPrinting(getBitmapFromView(slipSuccessLinearLayout));
//                                            }
//                                        });
                                doPrinting(getBitmapFromView(slipLinearLayout));
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        Toast.makeText(CheckQrActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBtn :
                if (traceBox.getText().length() < 6) {
                    StringBuilder num = new StringBuilder(traceBox.getText().toString());
                    for (int i = traceBox.getText().toString().length(); i < 6 ; i++) {
                        num.insert(0, "0");
                    }
                    traceBox.setText(num.toString());
                    selectQr(traceBox.getText().toString());
                } else {
                    selectQr(traceBox.getText().toString());
                }
                break;
        }
    }
}
