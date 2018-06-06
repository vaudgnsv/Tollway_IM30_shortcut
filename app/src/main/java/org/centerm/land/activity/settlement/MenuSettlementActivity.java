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
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.adapter.MenuSettlementAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.QrCode;
import org.centerm.land.database.TransTemp;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.text.DecimalFormat;
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

    private boolean isSettlementAll = false;
    private int settlementPosition = 0;
    private Dialog dialogSettlement;
    private ProgressBar progressBarStatus;
    private TextView statusLabel;
    private Button okBtn;
    private Bitmap oldBitmap;
    private Dialog dialogOutOfPaper;
    private Button okPaperBtn;
    private TextView msgLabel;
    private TextView merchantName1Label;
    private TextView merchantName2Label;
    private TextView merchantName3Label;
    private ImageView closeImage;

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
        customDialogSettlement();
        customDialogOutOfPaper();
        cardManager.setSettlementHelperLister(new CardManager.SettlementHelperLister() {
            @Override
            public void onSettlementSuccess() {
                if (!isSettlementAll) {
                    dialogWaiting.dismiss();
                    Intent intent = new Intent(MenuSettlementActivity.this, SlipSettlementActivity.class);
                    intent.putExtra(KEY_TYPE_HOST, typeHost);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else {
                    setViewSlip();
                }
            }

            @Override
            public void onCloseSettlementFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.customDialogAlert(MenuSettlementActivity.this, "ทำรายการไม่สำเร็จ 95", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                dialogWaiting.dismiss();
                            }
                        });
                    }
                });
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
                            if (dialogSettlement != null) {
                                dialogSettlement.dismiss();
                            }
                            Utility.customDialogAlert(MenuSettlementActivity.this, response, new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    settlementPosition++;
                                    okBtn.setVisibility(View.VISIBLE);
                                    if (settlementPosition == 0) {
                                        statusLabel.setText("KTB offus ไม่มีข้อมูล");
                                    } else if (settlementPosition == 1) {
                                        statusLabel.setText("BASE24 EPS ไม่มีข้อมูล");
                                    } else if (settlementPosition == 2) {
                                        statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
                                    } else if (settlementPosition == 3) {
                                        statusLabel.setText("QR ไม่มีข้อมูล");
                                    } else {
                                        statusLabel.setText("Settlement สำเร็จ");
                                    }
                                    if (settlementPosition == 1) {
                                        selectDataTransTempAll("EPS");
                                    } else if (settlementPosition == 2) {
                                        selectDataTransTempAll("TMS");
                                    } else if (settlementPosition == 3) {
                                        selectSettlementQR();
                                    } else {
                                        dialogSettlement.dismiss();
                                        Log.d(TAG, "success: " + settlementPosition);
                                    }
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
                            if (dialogSettlement != null) {
                                dialogSettlement.dismiss();
                            }
                            if (dialogSettlement != null) {
                                dialogSettlement.dismiss();
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
                            if (dialogSettlement != null) {
                                dialogSettlement.dismiss();
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
        merchantName1Label = qrView.findViewById(R.id.merchantName1Label);
        merchantName2Label = qrView.findViewById(R.id.merchantName2Label);
        merchantName3Label = qrView.findViewById(R.id.merchantName3Label);
    }

    private void setViewSlip() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                Realm realm = Realm.getDefaultInstance();
                try {
                    RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll();
                    Double amountSale = 0.0;
                    Double amountVoid = 0.0;
                    for (int i = 0; i < transTemp.size(); i++) {
                        amountSale += Float.valueOf(transTemp.get(i).getAmount());
                    }
                    RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("hostTypeCard", typeHost).findAll();
                    for (int i = 0; i < transTempVoid.size(); i++) {
                        amountVoid += Float.valueOf(transTempVoid.get(i).getAmount());
                    }
                    Date date = new Date();

                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                    voidSaleCountLabel.setText(transTempVoid.size() + "");
                    voidSaleAmountLabel.setText(decimalFormat.format(amountVoid));
                    saleTotalLabel.setText(decimalFormat.format(amountSale));
                    saleCountLabel.setText(transTemp.size()+"");
                    cardCountLabel.setText((transTemp.size() + transTempVoid.size()) + "");
                    cardAmountLabel.setText(decimalFormat.format(amountSale));
                    dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                    /*if (typeHost.equalsIgnoreCase("POS")) {
                        hostLabel.setText("KTB OFFUS");
                        batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                    } else if (typeHost.equalsIgnoreCase("EPS")) {
                        hostLabel.setText("BASE24 EPS");
                        batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                    } else {
                        hostLabel.setText("KTB ONUS");
                        batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                    }*/

                    if (typeHost.equalsIgnoreCase("POS")) {
                        hostLabel.setText("KTB Off us");
                        batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS),6));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_POS, dateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_POS, timeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS, saleTotalLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_POS, saleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_POS, voidSaleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS, voidSaleAmountLabel.getText().toString());
                    } else if (typeHost.equalsIgnoreCase("EPS")) {
                        hostLabel.setText("BASE24 EPS");
                        batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS),6));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_EPS, dateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_EPS, timeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS, saleTotalLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS, saleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS, voidSaleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS, voidSaleAmountLabel.getText().toString());
                    } else {
                        hostLabel.setText("KTB On Us");
                        batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS),6));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_TMS, dateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_TMS, timeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS, saleTotalLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS, saleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS, voidSaleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS, voidSaleAmountLabel.getText().toString());
                    }
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
                            transTemp.deleteAllFromRealm();
                        }
                    });
                    setMeasureQr();
                    doPrinting(getBitmapFromView(settlementLinearLayout));

                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }
            }

        }).start();

    }

    private void setMenuList() {
        if (menuSettleRecyclerView.getAdapter() == null) {
            menuSettlementAdapter = new MenuSettlementAdapter(this);
            menuSettleRecyclerView.setAdapter(menuSettlementAdapter);
            menuSettlementAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSettlementAll = false;
                    cardManager.setDataDefaultBatchUpload();
                    int position = (int) v.getTag();
                    if (position == 2) {
                        typeHost = "POS";
                        selectDataTransTemp("POS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
//                                cardManager.setDataSettlementAndSend("POS");
                                cardManager.setDataDefaultUploadCradit();
                                cardManager.setCheckTCUpload("POS", true);
                            } else {
//                                cardManager.setDataSettlementAndSend("POS");
                                cardManager.setDataDefaultUploadCradit();
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
                    } else if (position == 3) {
                        typeHost = "EPS";
                        selectDataTransTemp("EPS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
//                                cardManager.setDataSettlementAndSendEPS();
                                cardManager.setDataDefaultUploadCradit();
                                cardManager.setCheckTCUpload("EPS", true);
                            } else {
//                                cardManager.setDataSettlementAndSendEPS();
                                cardManager.setDataDefaultUploadCradit();
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
                    } else if (position == 1) {
                        typeHost = "TMS";
                        selectDataTransTemp("TMS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
                                cardManager.setDataDefaultUploadCradit();
                                cardManager.setDataSettlementAndSendTMS();
                            } else {
                                cardManager.setDataDefaultUploadCradit();
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
                    } else if (position == 4) {
                        selectSettlementQR();
                    } else if (position == 0) {
                        settlementPosition = 0;
                        isSettlementAll = true;
                        typeHost = "POS";
                        dialogSettlement.show();
                        progressBarStatus.setVisibility(View.VISIBLE);
                        selectDataTransTempAll("POS");
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
        menuList.add("Settlement All"); // 4 0
        menuList.add("KTB On Us"); // 2 1
        menuList.add("KTB Off us"); // 0 2
        menuList.add("BASE24 EPS"); // 1 3
        menuList.add("QR"); // 3 4
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

    private void selectDataTransTempAll(final String typeHost) {
        /*if (realm == null) {
            realm = Realm.getDefaultInstance();
        }*/

        cardManager.setDataDefaultBatchUpload();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    if (transTemp == null) {
                        transTemp = new ArrayList<>();
                    } else {
                        transTemp.clear();
                    }
                    transTemp.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
                    Log.d(TAG, "selectDataTransTemp: " + transTemp.size());

                    if (transTemp.size() > 0) {
                        if (!typeHost.equals("TMS")) {
                            cardManager.setDataDefaultUploadCradit();
                            cardManager.setCheckTCUpload(typeHost, true);
//                            dialogWaiting.show();
                        } else {
                            cardManager.setDataDefaultUploadCradit();
                            cardManager.setDataSettlementAndSendTMS();
//                            dialogWaiting.show();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                okBtn.setVisibility(View.VISIBLE);
                                progressBarStatus.setVisibility(View.GONE);
                                if (settlementPosition == 0) {
                                    statusLabel.setText("KTB offus ไม่มีข้อมูล");
                                } else if (settlementPosition == 1) {
                                    statusLabel.setText("BASE24 EPS ไม่มีข้อมูล");
                                } else if (settlementPosition == 2) {
                                    statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
                                } else if (settlementPosition == 3) {
                                    statusLabel.setText("QR ไม่มีข้อมูล");
                                }else {
                                    statusLabel.setText("Settlement สำเร็จ");
                                }
                            }
                        });

                    }
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }
            }
        }).start();
    }

    public void doPrinting(Bitmap slip) {
        oldBitmap = slip;
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    printDev.printBmpFast(oldBitmap, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    settlementPosition++;
                                    progressBarStatus.setVisibility(View.VISIBLE);
                                    if (settlementPosition == 0) {
                                        statusLabel.setText("Settlement KTB OFFUS");
                                    } else if (settlementPosition == 1) {
                                        statusLabel.setText("Settlement BASE24 EPS");
                                    } else if (settlementPosition == 2) {
                                        statusLabel.setText("Settlement KTB ONUS");
                                    } else if (settlementPosition == 3) {
                                        statusLabel.setText("Settlement QR");
                                    }else {
                                        statusLabel.setText("Settlement สำเร็จ");
                                    }
                                    if (settlementPosition == 1) {
                                        typeHost = "EPS";
                                        selectDataTransTempAll("EPS");
                                    } else if (settlementPosition == 2) {
                                        typeHost = "TMS";
                                        selectDataTransTempAll("TMS");
                                    } else if (settlementPosition == 3) {
                                        selectSettlementQRAll();
                                    } else {
                                        dialogSettlement.dismiss();
                                        Log.d(TAG, "success: " + settlementPosition);
                                    }

                                    Log.d(TAG, "onSettlementSuccess: " + settlementPosition);
                                }
                            });
                        }

                        @Override
                        public void onPrintError(int i) throws RemoteException {

                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {

                        }
                    });
//                    int ret = printDev.printBarCodeSync("asdasd");
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
                if (qrCode.size() > 0) {
                    status = 0;
                    for (int i = 0; i < qrCode.size(); i++) {
                        amountSaleQr += Double.valueOf(qrCode.get(i).getAmount());
                    }

                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                    Date date = new Date();
                    voidSaleCountLabel.setText("0");
                    voidSaleAmountLabel.setText(String.format("%.2f", 0.0));
                    saleCountLabel.setText(qrCode.size() + "");
                    saleTotalLabel.setText(String.format("%.2f", amountSaleQr));
                    cardCountLabel.setText(qrCode.size() + "");
                    cardAmountLabel.setText(String.format("%.2f", amountSaleQr));
                    dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                    hostLabel.setText("KTB QR");
                    batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
                    tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                    midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_QR, dateLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_QR, timeLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR, saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_QR, saleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_QR, voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR, voidSaleAmountLabel.getText().toString());
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
                    okBtn.setVisibility(View.VISIBLE);
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

    private void selectSettlementQRAll() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();
                Double amountSaleQr = 0.0;
                float amountVoidQr = 0;
                if (qrCode.size() > 0) {
                    status = 0;
                    for (int i = 0; i < qrCode.size(); i++) {
                        amountSaleQr += Double.valueOf(qrCode.get(i).getAmount());
                    }


                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));


                    Date date = new Date();
                    voidSaleCountLabel.setText("0");
                    voidSaleAmountLabel.setText(String.format("%.2f", 0.0));
                    saleCountLabel.setText(qrCode.size() + "");
                    saleTotalLabel.setText(String.format("%.2f", amountSaleQr));
                    cardCountLabel.setText(qrCode.size() + "");
                    cardAmountLabel.setText(String.format("%.2f", amountSaleQr));
                    dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                    hostLabel.setText("KTB QR");
                    batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
                    tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                    midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_QR, dateLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_QR, timeLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR, saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_QR, saleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_QR, voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR, voidSaleAmountLabel.getText().toString());
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
                    progressBarStatus.setVisibility(View.GONE);
                    statusLabel.setText("QR ไม่มีข้อมูล");
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
    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this);
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOutOfPaper.setContentView(R.layout.dialog_custom_printer);
        dialogOutOfPaper.setCancelable(false);
        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okPaperBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okPaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPrinting(oldBitmap);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    private void customDialogSettlement() {
        dialogSettlement = new Dialog(this);
        dialogSettlement.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSettlement.setCancelable(false);
        dialogSettlement.setContentView(R.layout.dialog_custom_settlement);
        dialogSettlement.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSettlement.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        progressBarStatus = dialogSettlement.findViewById(R.id.progressBarStatus);
        statusLabel = dialogSettlement.findViewById(R.id.statusLabel);
        okBtn = dialogSettlement.findViewById(R.id.okBtn);
        closeImage = dialogSettlement.findViewById(R.id.closeImage);
        okBtn.setVisibility(View.GONE);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "settlementPosition : " + settlementPosition);
                progressBarStatus.setVisibility(View.VISIBLE);
                settlementPosition++;
                if (settlementPosition == 1) {
                    statusLabel.setText("Settlement BASE24 EPS");
                    typeHost = "EPS";
                    selectDataTransTempAll("EPS");
                } else if (settlementPosition == 2) {
                    statusLabel.setText("Settlement KTB ONUS");
                    typeHost = "TMS";
                    selectDataTransTempAll("TMS");
                } else if (settlementPosition == 3) {
                    selectSettlementQRAll();
                } else {
                    dialogSettlement.dismiss();
                    Log.d(TAG, "success: " + settlementPosition);
                }
            }
        });
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSettlement.dismiss();
            }
        });
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
