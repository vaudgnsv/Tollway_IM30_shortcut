package org.centerm.land.activity.qr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;
import com.google.gson.JsonElement;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.activity.MenuServiceActivity;
import org.centerm.land.adapter.MenuQrAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.QrCode;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.manager.HttpManager;
import org.centerm.land.model.Check;
import org.centerm.land.model.MenuQr;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;

public class MenuQrActivity extends SettingToolbarActivity {

    private RecyclerView recyclerViewMenuQr = null;
    private MenuQrAdapter menuQrAdapter = null;
    private List<MenuQr> menuList = null;

    private Realm realm = null;
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
    private View slipView;

    private int qrId = 0;
    private Check check;
    private final String TAG = "MenuQrActivity";
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private TextView midLabel;
    private TextView batchLabel;
    private TextView apprCodeLabel;
    private TextView inquiryLabel;
    private Dialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_qr);
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        recyclerViewMenuQr = findViewById(R.id.recyclerViewMenuQr);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMenuQr.setLayoutManager(layoutManager);
        customDialogOutOfPaper();
        customDialogLoading();
        setMenuQr();

        setViewPrintSlip();
    }

    private void setMenuQr() {
        if (recyclerViewMenuQr.getAdapter() == null) {
            menuQrAdapter = new MenuQrAdapter(this);
            recyclerViewMenuQr.setAdapter(menuQrAdapter);
            menuQrAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        Intent intent = new Intent(MenuQrActivity.this, GenerateQrActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (position == 1) {
                        RealmResults<QrCode> allTransactions = realm.where(QrCode.class).findAll();
                        Log.d(TAG, "onClick: allTransactions :" + allTransactions.size());
//If you have an incrementing id column, do this
                        if (allTransactions.size() > 0) {
                            qrCode = allTransactions.last();
                            if (qrCode != null)
                                qrId = qrCode.getId();
                            if (qrCode.getStatusSuccess().equalsIgnoreCase("0")) {
                                print();
                            } else {
                                Utility.customDialogAlertSuccess(MenuQrActivity.this, "รายการนี้ชำระเงินแล้ว", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            Utility.customDialogAlert(MenuQrActivity.this, "ไม่มีรายการ", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    } else if (position == 2) {
                        Intent intent = new Intent(MenuQrActivity.this, CheckQrActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }
            });
        } else {
            menuQrAdapter.clear();
        }
        if (menuList == null) {
            menuList = new ArrayList<>();
        } else {
            menuList.clear();
        }
        MenuQr menuQr1 = new MenuQr();
        menuQr1.setName("Generator QR ");
        menuQr1.setImage(R.drawable.qrscanner96x96);

        MenuQr menuQr2 = new MenuQr();
        menuQr2.setName("ตรวจสอบการชำระเงิน");
        menuQr2.setImage(R.drawable.qrscanner96x96);

        MenuQr menuQr3 = new MenuQr();
        menuQr3.setName("ตรวจสอบการชำระเงินรายการก่อนหน้า");
        menuQr3.setImage(R.drawable.qrscanner96x96);

        menuList.add(menuQr1);
        menuList.add(menuQr2);
        menuList.add(menuQr3);
        menuQrAdapter.addItem(menuList);
        menuQrAdapter.notifyDataSetChanged();

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
        midLabel = slipView.findViewById(R.id.midLabel);
        batchLabel = slipView.findViewById(R.id.batchLabel);
        apprCodeLabel = slipView.findViewById(R.id.apprCodeLabel);
        inquiryLabel = slipView.findViewById(R.id.inquiryLabel);
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

    private void print() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

                if (!Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                    merchantName1Label.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                if (!Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                    merchantName2Label.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                if (!Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                    merchantName3Label.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                qrCode = realm.where(QrCode.class).equalTo("id", qrId).findFirst();
                qrTidLabel.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                midLabel.setText(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
//                batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS),6));
                int batch = Integer.parseInt(Preference.getInstance(MenuQrActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                apprCodeLabel.setText("000000");
                inquiryLabel.setText(qrCode.getQrTid());
                billerLabel.setText(qrCode.getBillerId());
                traceLabel.setText(qrCode.getTrace());
                dateLabel.setText(qrCode.getDate());
                String timeHH = qrCode.getTime().substring(0, 2);
                String timeMM = qrCode.getTime().substring(2, 4);
                String timeSS = qrCode.getTime().substring(4, 6);
                timeLabel.setText(getString(R.string.time_qr, timeHH + ":" + timeMM + ":" + timeSS));
                qrId = qrCode.getId();
//                comCodeLabel.setText(qrCode.getComCode());
                amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(qrCode.getAmount()))));
                /*if (qrCode.getRef1() != null) {
                    ref1RelativeLayout.setVisibility(View.VISIBLE);
                    ref1Label.setText(qrCode.getRef1());
                }
                if (qrCode.getRef2() != null) {
                    ref2RelativeLayout.setVisibility(View.VISIBLE);
                    ref2Label.setText(qrCode.getRef2());
                }*/
//                qrImage.setImageBitmap(Utility.createQRImage(qrCode.getTextQrGenerateAll(), 300, 300));
                setMeasureSlip();

                check = new Check();
                check.setBillerId(qrCode.getBillerId());
                check.setTerminalId(qrCode.getQrTid());
                check.setRef1(qrCode.getRef1());
                check.setRef2(qrCode.getRef2());
                requestCheckSlip();
            }
        });
    }

    private void setMeasureSlip() {
        slipView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        slipView.layout(0, 0, slipView.getMeasuredWidth(), slipView.getMeasuredHeight());
    }


    private void requestCheckSlip() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dialogLoading.show();
            }
        });
        HttpManager.getInstance().getService().checkQr(check)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<JsonElement>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<JsonElement> jsonElementResponse) {
                        try {
                            if (jsonElementResponse.body() != null) {
                                JSONObject object = new JSONObject(jsonElementResponse.body().toString());
                                String code = object.getString("code");
                                if (code.equalsIgnoreCase("00000")) {
                                    dialogLoading.dismiss();
                                    updateQrSuccess();
                                    doPrinting(getBitmapFromView(slipLinearLayout));
                                } else {
                                    dialogLoading.dismiss();
                                    String dec = object.getString("desc");
                                    Utility.customDialogAlert(MenuQrActivity.this, dec, new Utility.OnClickCloseImage() {
                                        @Override
                                        public void onClickImage(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            } else {
                                dialogLoading.dismiss();
                                Utility.customDialogAlert(MenuQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialogLoading.dismiss();
                        Log.d(TAG, "onError: " + e.getMessage());
                        Utility.customDialogAlert(MenuQrActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        dialogLoading.dismiss();
                    }
                });
    }

    private void updateQrSuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            QrCode qrCodeSave = realm.where(QrCode.class).equalTo("id", qrId).findFirst();
                            if (qrCodeSave != null) {
                                qrCodeSave.setStatusSuccess("1");
                                realm.copyToRealmOrUpdate(qrCodeSave);
                            }
                        }
                    });
                } finally {
                    realm.close();
                }

            }
        }).start();


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
                    printDev.printBmpFast(slip, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Intent intent = new Intent(MenuQrActivity.this, MenuServiceActivity.class);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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
