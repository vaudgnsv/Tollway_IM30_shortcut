package org.centerm.land.activity.qr;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.manager.HttpManager;
import org.centerm.land.model.Check;
import org.centerm.land.utility.DecimalDigitsInputFilter;
import org.centerm.land.utility.MoneyValueFilter;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.Response;

public class GenerateQrActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "GenerateQrActivity";
    private LinearLayout ref2LinearLayout;
    private ImageView qrImage = null;
    private EditText amountBox = null;
    private EditText ref1Box = null;
    private EditText ref2Box = null;
    private Button generatorBtn = null;
    private Button qrSuccessBtn = null;
    private String tagAll = "";
    private Realm realm = null;

    private String aid = "";
    private String billerId = "";
    private String qrTid = "";
    private String nameCompany = "";
    private int nextId;
    /***
     * DialogSlip
     */
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
    private ImageView qrSilpImage = null;
    private RelativeLayout ref1RelativeLayout = null;
    private TextView ref1Label = null;
    private RelativeLayout ref2RelativeLayout = null;
    private TextView ref2Label = null;
    private LinearLayout slipLinearLayout = null;
    private Button printBtn = null;
    private AidlPrinter printDev = null;
    private QrCode qrCode;
    private CardManager cardManager = null;
    private String dateFormat;
    private String dateFormatDef;
    private String timeFormat;
    private Dialog dialogQuestionPrint;

    private LinearLayout linearLayoutPrint = null;

    private int currentIdObl;
    private Dialog dialogAlertLoading;

    /***
     *
     *  slipSuccess
     */

    private ImageView bankSlipImage = null;
    private ImageView bank1SlipImage = null;
    private TextView merchantName1SlipLabel = null;
    private TextView merchantName2SlipLabel = null;
    private TextView merchantName3SlipLabel = null;
    private TextView qrTidSlipLabel = null;
    private TextView billerSlipLabel = null;
    private TextView traceSlipLabel = null;
    private TextView dateSlipLabel = null;
    private TextView timeSlipLabel = null;
    private TextView comCodeSlipLabel = null;
    private TextView amtThbSlipLabel = null;
    private ImageView qrSilpSlipImage = null;
    private RelativeLayout ref1SlipRelativeLayout = null;
    private TextView ref1SlipLabel = null;
    private RelativeLayout ref2SlipRelativeLayout = null;
    private TextView ref2SlipLabel = null;
    private LinearLayout slipSuccessLinearLayout = null;
    private View tagView;
    private View tagViewQr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        initWidget();
//        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        qrImage = findViewById(R.id.qrImage);
        ref2LinearLayout = findViewById(R.id.ref2LinearLayout);
        amountBox = findViewById(R.id.amountBox);
        amountBox.setFilters(new InputFilter[]{new MoneyValueFilter()});
        ref1Box = findViewById(R.id.ref1Box);
        ref2Box = findViewById(R.id.ref2Box);
        linearLayoutPrint = findViewById(R.id.linearLayoutPrint);

        generatorBtn = findViewById(R.id.generatorBtn);
        qrSuccessBtn = findViewById(R.id.qrSuccessBtn);
        linearLayoutPrint.setOnClickListener(this);
        generatorBtn.setOnClickListener(this);
        qrSuccessBtn.setOnClickListener(this);
        if (Preference.getInstance(this).getValueBoolean(Preference.KEY_REF_2)) {
            ref2LinearLayout.setVisibility(View.VISIBLE);
        } else {
            ref2LinearLayout.setVisibility(View.GONE);
        }

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tagView = inflater.inflate(R.layout.view_slip_qr, null);
        bankSlipImage = tagView.findViewById(R.id.bankImage);
        bank1SlipImage = tagView.findViewById(R.id.bank1Image);
        merchantName1SlipLabel = tagView.findViewById(R.id.merchantName1Label);
        merchantName2SlipLabel = tagView.findViewById(R.id.merchantName2Label);
        merchantName3SlipLabel = tagView.findViewById(R.id.merchantName3Label);
        qrTidSlipLabel = tagView.findViewById(R.id.qrTidLabel);
        billerSlipLabel = tagView.findViewById(R.id.billerLabel);
        traceSlipLabel = tagView.findViewById(R.id.traceLabel);
        dateSlipLabel = tagView.findViewById(R.id.dateLabel);
        timeSlipLabel = tagView.findViewById(R.id.timeLabel);
        comCodeSlipLabel = tagView.findViewById(R.id.comCodeLabel);
        amtThbSlipLabel = tagView.findViewById(R.id.amtThbLabel);
        ref1SlipRelativeLayout = tagView.findViewById(R.id.ref1RelativeLayout);
        ref1SlipLabel = tagView.findViewById(R.id.ref1Label);
        ref2SlipRelativeLayout = tagView.findViewById(R.id.ref2RelativeLayout);
        ref2SlipLabel = tagView.findViewById(R.id.ref2Label);
        slipSuccessLinearLayout = tagView.findViewById(R.id.slipLinearLayout);
        setViewPrintQr();
        customDialogAlertLoading();

    }

    private void setMeasure() {
        tagView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tagView.layout(0, 0, tagView.getMeasuredWidth(), tagView.getMeasuredHeight());
    }
    private void setMeasureQr() {
        tagViewQr.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tagViewQr.layout(0, 0, tagView.getMeasuredWidth(), tagViewQr.getMeasuredHeight());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generatorBtn:
                Date date = new Date();
                dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(date);
                timeFormat = new SimpleDateFormat("hhMMss").format(date);
                dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);

                aid = Preference.getInstance(this).getValueString(Preference.KEY_QR_AID);
                billerId = "010352102131870";
                qrTid = Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID) +
                        CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_QR_TRACE_NO), 6) +
                        dateFormatDef; //"00025068000023180517";  //tid trace yymmdd
                Log.d(TAG, "onClick: " + qrTid);
                Log.d(TAG, "onClick: " + billerId);
                Log.d(TAG, "onClick: " + aid);
                nameCompany = "NAKHONRATCHASIMA PCG.";

                tagAll = Utility.idValue("", "00", "01");
                tagAll = Utility.idValue(tagAll, "01", "11");
                String tagIn30 = Utility.idValue("", "00", aid);
                tagIn30 = Utility.idValue(tagIn30, "01", billerId);
                tagIn30 = Utility.idValue(tagIn30, "02", ref1Box.getText().toString());
                if (!ref2Box.getText().toString().isEmpty()) {
                    tagIn30 = Utility.idValue(tagIn30, "03", ref2Box.getText().toString());
                }
                String tag30 = Utility.idValue("", "30", tagIn30);
                tagAll += tag30;
                tagAll = Utility.idValue(tagAll, "53", "764");
                tagAll = Utility.idValue(tagAll, "54", amountBox.getText().toString());
                tagAll = Utility.idValue(tagAll, "58", "TH");
                tagAll = Utility.idValue(tagAll, "59", nameCompany);
                String tagIn62 = Utility.idValue("", "07", qrTid);
                String tag62 = Utility.idValue("", "62", tagIn62);
                tagAll += tag62;
//                tagAll = Utility.idValue(tagAll, "63", "");
                tagAll += "6304";
                tagAll += Utility.CheckSumCrcCCITT(tagAll);
                Log.d(TAG, "initWidget: " + tagAll);
                qrImage.setImageBitmap(Utility.createQRImage(tagAll, 300, 300));
                insertGenerateQr();
                break;
            case R.id.qrSuccessBtn:
                Check check = new Check();
                check.setBillerId(billerId);
                check.setRef1(ref1Box.getText().toString());
                if (ref2Box.getText().toString().isEmpty()) {
                    check.setRef2("");
                } else {
                    check.setRef2(ref2Box.getText().toString());
                }
                check.setTerminalId(qrTid);
                Log.d(TAG, "billerId: " + billerId + " ref1Box : " + ref1Box.getText().toString()
                        + " ref2Box :" + ref2Box.getText().toString() +
                " qrTid : " + qrTid);
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
                                Toast.makeText(GenerateQrActivity.this, "" + jsonElementResponse.body(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(GenerateQrActivity.this, "" + jsonElementResponse, Toast.LENGTH_SHORT).show();
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
                                        selectQrSlip();
                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                                Toast.makeText(GenerateQrActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
            case R.id.linearLayoutPrint:
                selectQr();
                break;
        }
    }


    public void setViewPrintQr() {
        LayoutInflater inflaterQr =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tagViewQr = inflaterQr.inflate(R.layout.dialog_print_qr, null);

        bankImage = tagViewQr.findViewById(R.id.bankImage);
        bank1Image = tagViewQr.findViewById(R.id.bank1Image);

        merchantName1Label = tagViewQr.findViewById(R.id.merchantName1Label);
        merchantName2Label = tagViewQr.findViewById(R.id.merchantName2Label);
        merchantName3Label = tagViewQr.findViewById(R.id.merchantName3Label);
        qrTidLabel = tagViewQr.findViewById(R.id.qrTidLabel);
        billerLabel = tagViewQr.findViewById(R.id.billerLabel);
        traceLabel = tagViewQr.findViewById(R.id.traceLabel);
        dateLabel = tagViewQr.findViewById(R.id.dateLabel);
        timeLabel = tagViewQr.findViewById(R.id.timeLabel);
        comCodeLabel = tagViewQr.findViewById(R.id.comCodeLabel);
        amtThbLabel = tagViewQr.findViewById(R.id.amtThbLabel);
        qrSilpImage = tagViewQr.findViewById(R.id.qrImage);
        ref1RelativeLayout = tagViewQr.findViewById(R.id.ref1RelativeLayout);
        ref1Label = tagViewQr.findViewById(R.id.ref1Label);
        ref2RelativeLayout = tagViewQr.findViewById(R.id.ref2RelativeLayout);
        ref2Label = tagViewQr.findViewById(R.id.ref2Label);
        slipLinearLayout = tagViewQr.findViewById(R.id.slipLinearLayout);
        printBtn = tagViewQr.findViewById(R.id.printBtn);
    }

    private void insertGenerateQr() {
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            Number currentId = realm.where(QrCode.class).max("id");
            if (currentId == null) {
                nextId = 1;
            } else {
                currentIdObl = currentId.intValue();
                nextId = currentId.intValue() + 1;
            }
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    String traceId = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_TRACE_NO);
                    QrCode qrCode = realm.createObject(QrCode.class, nextId);
                    qrCode.setAid(aid);
                    qrCode.setQrTid(qrTid);
                    qrCode.setBillerId(billerId);
                    qrCode.setTrace(CardPrefix.calLen(traceId, 6));
                    qrCode.setDate(dateFormat);
                    qrCode.setTime(timeFormat);
                    qrCode.setComCode(Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_TAG_1001));
                    qrCode.setRef1(ref1Box.getText().toString());
                    qrCode.setRef2(ref2Box.getText().toString());
                    qrCode.setNameCompany(nameCompany);
                    qrCode.setTextQrGenerateAll(tagAll);
                    qrCode.setAmount(amountBox.getText().toString());
                    realm.copyFromRealm(qrCode);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    String traceIdOld = Preference.getInstance(GenerateQrActivity.this).getValueString(Preference.KEY_QR_TRACE_NO);
                    Preference.getInstance(GenerateQrActivity.this).setValueString(Preference.KEY_QR_TRACE_NO, String.valueOf(Integer.valueOf(traceIdOld) + 1));
                    Log.d(TAG, "onSuccess: ");

                }
            });

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    private void selectQr() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                qrCode = realm.where(QrCode.class).equalTo("id", nextId).findFirst();
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
                qrSilpImage.setImageBitmap(Utility.createQRImage(qrCode.getTextQrGenerateAll(), 300, 300));

                qrTidSlipLabel.setText(qrCode.getQrTid());
                billerSlipLabel.setText(qrCode.getBillerId());
                traceSlipLabel.setText(qrCode.getTrace());
                dateSlipLabel.setText(qrCode.getDate());
                timeSlipLabel.setText(qrCode.getTime());
                comCodeSlipLabel.setText(qrCode.getComCode());
                amtThbSlipLabel.setText(getString(R.string.slip_pattern_amount, qrCode.getAmount()));
                if (qrCode.getRef1() != null) {
                    ref1SlipRelativeLayout.setVisibility(View.VISIBLE);
                    ref1SlipLabel.setText(qrCode.getRef1());
                }
                if (qrCode.getRef2() != null) {
                    ref2SlipRelativeLayout.setVisibility(View.VISIBLE);
                    ref2SlipLabel.setText(qrCode.getRef2());
                }
                setMeasureQr();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");

                if (realm != null) {
                    realm.close();
                    realm = null;
                }
                dialogAlertLoading.show();
                doPrinting(getBitmap(slipLinearLayout));
            }
        });
    }
    private void selectQrSlip() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                qrCode = realm.where(QrCode.class).equalTo("id", nextId).findFirst();
                Log.d(TAG, "execute: " + qrCode.toString());

                qrTidSlipLabel.setText(qrCode.getQrTid());
                billerSlipLabel.setText(qrCode.getBillerId());
                traceSlipLabel.setText(qrCode.getTrace());
                dateSlipLabel.setText(qrCode.getDate());
                timeSlipLabel.setText(qrCode.getTime());
                comCodeSlipLabel.setText(qrCode.getComCode());
                amtThbSlipLabel.setText(getString(R.string.slip_pattern_amount, qrCode.getAmount()));
                if (qrCode.getRef1() != null) {
                    ref1SlipRelativeLayout.setVisibility(View.VISIBLE);
                    ref1SlipLabel.setText(qrCode.getRef1());
                }
                if (qrCode.getRef2() != null) {
                    ref2SlipRelativeLayout.setVisibility(View.VISIBLE);
                    ref2SlipLabel.setText(qrCode.getRef2());
                }

                setMeasure();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");

                if (realm != null) {
                    realm.close();
                    realm = null;
                }
                dialogAlertLoading.show();
                doPrinting(getBitmap(slipSuccessLinearLayout));
            }
        });
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

    public Bitmap getBitmap(View v ) {
        return getBitmapFromView(v);
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

                    if (dialogAlertLoading != null) {
                        dialogAlertLoading.dismiss();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void customDialogAlertLoading() {
        dialogAlertLoading = new Dialog(this);
        dialogAlertLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlertLoading.setContentView(R.layout.dialog_custom_alert_loading);
        dialogAlertLoading.setCancelable(false);
        dialogAlertLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlertLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
