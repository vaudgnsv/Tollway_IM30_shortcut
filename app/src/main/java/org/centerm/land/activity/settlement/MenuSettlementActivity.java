package org.centerm.land.activity.settlement;

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
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.constant.Constant;
import com.google.zxing.qrcode.encoder.QRCode;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.adapter.MenuSettlementAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.QrCode;
import org.centerm.land.database.TransTemp;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MenuSettlementActivity extends SettingToolbarActivity {

    private final String TAG = "MenuSettlementActivity";

    public static final String KEY_TYPE_HOST = MenuSettlementActivity.class.getName() + "key_type_host";

    private RecyclerView menuSettleRecyclerView;
    private MenuSettlementAdapter menuSettlementAdapter = null;
    private ArrayList<String> menuList = null;
    private CardManager cardManager = null;
    private Realm realm = null;
    private ArrayList<TransTemp> transTemp = null;
    private ArrayList<TransTemp> transTempVoidFlag = null;
    private LinearLayout settlementLinearLayout;
    private RelativeLayout settlementRelativeLayout;
    private AidlPrinter printDev;
    private Dialog dialogWaiting;

    private String typeHost;

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
    private View qrView;
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settlement);
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        menuSettleRecyclerView = findViewById(R.id.menuSettleRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menuSettleRecyclerView.setLayoutManager(layoutManager);
        setMenuList();
        customDialogWaiting();

        cardManager.setSettlementHelperLister(new CardManager.SettlementHelperLister() {
            @Override
            public void onSettlementSuccess() {
                dialogWaiting.dismiss();
                Intent intent = new Intent(MenuSettlementActivity.this, SlipSettlementActivity.class);
                intent.putExtra(KEY_TYPE_HOST, typeHost);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

            @Override
            public void onConnectTimeOut() {

            }

            @Override
            public void onTransactionTimeOut() {

            }
        });

        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(final String response) {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlert(MenuSettlementActivity.this, response, new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onResponseCodeSuccess() {

            }

            @Override
            public void onConnectTimeOut() {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlert(MenuSettlementActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onTransactionTimeOut() {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlert(MenuSettlementActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        qrView = inflater.inflate(R.layout.view_slip_settlement, null);
        settlementLinearLayout = qrView.findViewById(R.id.settlementLinearLayout);
        dateLabel = qrView.findViewById(R.id.dateLabel);
        timeLabel = qrView.findViewById(R.id.timeLabel);
        midLabel = qrView.findViewById(R.id.midLabel);
        tidLabel = qrView.findViewById(R.id.tidLabel);
        batchLabel = qrView.findViewById(R.id.batchLabel);
        hostLabel = qrView.findViewById(R.id.hostLabel);
        saleCountLabel = qrView.findViewById(R.id.saleCountLabel);
        saleTotalLabel = qrView.findViewById(R.id.saleTotalLabel);
        voidSaleCountLabel = qrView.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabel = qrView.findViewById(R.id.voidSaleAmountLabel);
        cardCountLabel = qrView.findViewById(R.id.cardCountLabel);
        cardAmountLabel = qrView.findViewById(R.id.cardAmountLabel);
    }

    private void setViewSlip() {
        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll();
        float amountSale = 0;
        float amountVoid = 0;
        for (int i = 0; i < transTemp.size(); i++) {
            amountSale += Float.valueOf(transTemp.get(i).getAmount());
        }
        RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("hostTypeCard", typeHost).findAll();
        for (int i = 0; i < transTempVoid.size(); i++) {
            amountVoid += Float.valueOf(transTempVoid.get(i).getAmount());
        }
        Date date = new Date();

        voidSaleCountLabel.setText(transTempVoid.size() + "");
        voidSaleAmountLabel.setText(String.format("%.2f", amountVoid));
        cardCountLabel.setText((transTemp.size() + transTempVoid.size()) + "");
        cardAmountLabel.setText(String.format("%.2f", amountVoid + amountSale));
        dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        dateLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
        if (typeHost.equalsIgnoreCase("POS")) {
            hostLabel.setText("OFFUS POS");
            batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
            tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            hostLabel.setText("OFFUS EPS");
            batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
            tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
        } else {
            hostLabel.setText("KTB ONUS");
            batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS));
            tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
        }
        doPrinting(getBitmapFromView(settlementLinearLayout));
        cardManager.deleteTransTemp();
    }

    private void setMenuList() {
        if (menuSettleRecyclerView.getAdapter() == null) {
            menuSettlementAdapter = new MenuSettlementAdapter(this);
            menuSettleRecyclerView.setAdapter(menuSettlementAdapter);
            menuSettlementAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardManager.setDataDefault();
                    int position = (int) v.getTag();
                    if (position == 0) {
                        typeHost = "POS";
                        selectDataTransTemp("POS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
//                                cardManager.setDataSettlementAndSend("POS");
                                cardManager.setCheckTCUpload("POS", true);
                            } else {
//                                cardManager.setDataSettlementAndSend("POS");
                                cardManager.setCheckTCUpload("POS", true);
                            }
                            dialogWaiting.show();
                        } else {
                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    } else if (position == 1) {
                        typeHost = "EPS";
                        selectDataTransTemp("EPS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
//                                cardManager.setDataSettlementAndSendEPS();
                                cardManager.setCheckTCUpload("EPS", true);
                            } else {
//                                cardManager.setDataSettlementAndSendEPS();
                                cardManager.setCheckTCUpload("EPS", true);
                            }
                            dialogWaiting.show();
                        } else {
                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    } else if (position == 2) {
                        typeHost = "TMS";
                        selectDataTransTemp("TMS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
                                cardManager.setDataSettlementAndSendTMS();
                            } else {
                                cardManager.setDataSettlementAndSendTMS();
                            }
                            dialogWaiting.show();
                        } else {
                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    } else if (position == 3) {
                        selectSettlementQR();
                    }
                }
            });
        } else {
            menuSettlementAdapter.clear();
        }
        if (menuList == null) {
            menuList = new ArrayList<>();
        } else {
            menuList.clear();
        }
        menuList.add("KTB Off us");
        menuList.add("BASE24 EPS");
        menuList.add("KTB On Us");
        menuList.add("QR");
        menuSettlementAdapter.setItem(menuList);
        menuSettlementAdapter.notifyDataSetChanged();

    }

    private void selectDataTransTemp(String typeHost) {
        /*if (realm == null) {
            realm = Realm.getDefaultInstance();
        }*/

        if (transTemp == null) {
            transTemp = new ArrayList<>();
        } else {
            transTemp.clear();
        }
        if (transTempVoidFlag == null) {
            transTempVoidFlag = new ArrayList<>();
        } else {
            transTempVoidFlag.clear();
        }
        transTemp.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
        Log.d(TAG, "selectDataTransTemp: " + transTemp.size());
        transTempVoidFlag.addAll(realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll());
        Log.d(TAG, "transTempVoidFlag: " + transTempVoidFlag.size());
        /*if (realm != null) {
            realm.close();
            realm = null;
        }*/
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

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setCancelable(false);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
    }

    private void selectSettlementQR() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();
                Double amountSaleQr = 0.0;
                float amountVoidQr = 0;
                if (qrCode.size() > 0 ) {
                    status = 0;
                    for (int i = 0; i < qrCode.size(); i++) {
                        amountSaleQr += Double.valueOf(qrCode.get(i).getAmount());
                    }

                    Date date = new Date();
                    voidSaleCountLabel.setText("0");
                    voidSaleAmountLabel.setText(String.format("%.2f", 0.0));
                    saleCountLabel.setText(qrCode.size() + "");
                    saleTotalLabel.setText(String.format("%.2f", amountSaleQr));
                    cardCountLabel.setText(qrCode.size() + "");
                    cardAmountLabel.setText(String.format("%.2f", amountSaleQr));
                    dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                    hostLabel.setText("OFFUS POS");
                    batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
                    tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                    midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_QR,dateLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_QR,timeLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR,saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_QR,saleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_QR,voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR,voidSaleAmountLabel.getText().toString());
                    setMeasureQr();
                } else {
                    status = 1;
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (status == 0) {
                    deleteQrAll();
                    doPrinting(getBitmapFromView(settlementLinearLayout));
                } else {
                    Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    private void deleteQrAll() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<QrCode> qrCode = realm.where(QrCode.class).findAll();
                qrCode.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }
        });


    }

    private void setMeasureQr() {
        qrView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        qrView.layout(0, 0, qrView.getMeasuredWidth(), qrView.getMeasuredHeight());
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
    protected void onDestroy() {
        super.onDestroy();
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
