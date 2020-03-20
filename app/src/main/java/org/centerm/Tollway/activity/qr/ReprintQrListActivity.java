package org.centerm.Tollway.activity.qr;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.BuildConfig;
import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.adapter.ReprintQrListAdapter;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_APPROVAL_CODE;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;

public class ReprintQrListActivity extends SettingToolbarActivity {

    private final String TAG = "ReprintQrListActivity";
    private RecyclerView recyclerViewReprintQrList;
    private CardManager cardManager;
    private ReprintQrListAdapter reprintQrListAdapter;
    private ArrayList<QrCode> transTempList;
    private AidlPrinter printDev = null;
    private Realm realm;
    private EditText invoiceEt;
    private ImageView searchInvoiceImage;
    private String typeHost = null;
    //    private TransTemp transTemp = null;
    private QrCode transTemp = null;


    /**
     * Interface
     *///20180708 SINN Add healthcare print.
    private View printLastView;

    private Button closeImage; //K.GAME 180828 change dialog UI
    private String typeClick;

    private View reportSettlementLast;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private Dialog dialogHostQr;
    private Button posQrBtn;
    private Button tmsQrBtn;
    private Button epsQrBtn;

    private Button ghcQrBtn;  ////20180708 SINN Add healthcare print.

    private Button qrBtn;

    private TextView taxIdLabel;
    private TextView taxAbbLabel;
    private TextView traceTaxLabel;
    private TextView batchTaxLabel;
    private TextView dateTaxLabel;
    private TextView timeTaxLabel;
    private TextView feeTaxLabel;
    private TextView copyLabel;
    private TextView typeCopyLabel;
    private TextView nameEmvCardLabel;
    private LinearLayout taxLinearLayout;

    private boolean isStatusPrintLastSlip = false;
    private TextView typeInputCardLabel;
    private Dialog dialogLoading;
    private FrameLayout comCodeFragment;
    private LinearLayout summaryLinearFeeLayout;
    private TextView merchantName1FeeLabel;
    private TextView merchantName2FeeLabel;
    private TextView merchantName3FeeLabel;
    private TextView dateFeeLabel;
    private TextView timeFeeLabel;
    private TextView batchFeeLabel;
    private TextView hostFeeLabel;
    private TextView saleCountFeeLabel;
    private TextView saleTotalFeeLabel;
    private TextView voidSaleCountFeeLabel;
    private TextView voidSaleAmountFeeLabel;
    private TextView cardCountFeeLabel;
    private TextView cardAmountFeeLabel;
    private TextView taxIdFeeLabel;

    private Dialog dialogHost;
    /**
     * Slip
     */
    private View hgcSaleView;
    private LinearLayout slip_sale_void_hgc_re; //(:
    private TextView DateTimePrn;   ////20180720 SINN  HGC slip fix
    private TextView dateHgcLabel;
    private TextView timeHgcLabel;
    private TextView midHgcLabel;
    private TextView tidHgcLabel;
    private TextView traceNoLabel;
    private TextView typeSaleLabel;
    private TextView nameEngLabel;
    private TextView batchHgcLabel;
    private TextView comCodeHgcLabel;  // Paul_20180714
    private TextView apprCodeHgcLabel; // Paul_20180712
    private TextView cardNoHgcLabelxx; // Paul_20180716
    private TextView amountLabel;
    private TextView merchantName1HgcLabel;
    private TextView merchantName2HgcLabel;
    private TextView merchantName3HgcLabel;
    private AidlPrinter printer;
    //end 20180708 SINN Add healthcare print.
    private String typeInterface;
    private String invoiceId;
    private String approvalcode;
    private PosInterfaceActivity posInterfaceActivity;
    //K.GAME
    /***
     * SALE AND VOID
     */
    private ImageView bankImage = null;
    private ImageView bank1Image = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private TextView tidLabel = null;
    private TextView midLabel = null;
    private TextView traceLabel = null;
    private TextView systrcGHCLabel = null;
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
//    private TextView ref3Label = null;
    private RelativeLayout ref1RelativeLayout = null;
    private RelativeLayout ref2RelativeLayout = null;
//    private RelativeLayout ref3RelativeLayout = null;
    private LinearLayout slipLinearLayout = null;
    private TextView sigatureLabel = null;
    private TextView appLabel;
    private FrameLayout appFrameLabel;
    private TextView tcLabel;
    private FrameLayout tcFrameLayout;
    private TextView aidLabel;
    private FrameLayout aidFrameLayout;
    private TextView name_sw_version;  // Paul_20190125 software version print

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprint_qr_list);
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        realm = Realm.getDefaultInstance();

        initData();
        initWidget();
        initBtnExit();
        setViewReprint();//K.GAME 180907
        customDialogLoading();
        customDialogOutOfPaper();

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printLastView = inflater.inflate(R.layout.view_sale_void, null);
        merchantName1Label = printLastView.findViewById(R.id.merchantName1Label);
        merchantName2Label = printLastView.findViewById(R.id.merchantName2Label);
        merchantName3Label = printLastView.findViewById(R.id.merchantName3Label);
        slipLinearLayout = printLastView.findViewById(R.id.slipLinearLayout);
        tidLabel = printLastView.findViewById(R.id.tidLabel);
        midLabel = printLastView.findViewById(R.id.midLabel);
        traceLabel = printLastView.findViewById(R.id.traceLabel);
        systrcLabel = printLastView.findViewById(R.id.systrcLabel);
        batchLabel = printLastView.findViewById(R.id.batchLabel);
        refNoLabel = printLastView.findViewById(R.id.refNoLabel);
        dateLabel = printLastView.findViewById(R.id.dateLabel);
        timeLabel = printLastView.findViewById(R.id.timeLabel);
        typeLabel = printLastView.findViewById(R.id.typeLabel);
        typeCardLabel = printLastView.findViewById(R.id.typeCardLabel);
        cardNoLabel = printLastView.findViewById(R.id.cardNoLabel);
        apprCodeLabel = printLastView.findViewById(R.id.apprCodeLabel);
        comCodeLabel = printLastView.findViewById(R.id.comCodeLabel);
        amtThbLabel = printLastView.findViewById(R.id.amtThbLabel);
        feeThbLabel = printLastView.findViewById(R.id.feeThbLabel);
        totThbLabel = printLastView.findViewById(R.id.totThbLabel);
        ref1Label = printLastView.findViewById(R.id.ref1Label);
        ref2Label = printLastView.findViewById(R.id.ref2Label);
//        ref3Label = printLastView.findViewById(R.id.ref3Label);
        ref1RelativeLayout = printLastView.findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayout = printLastView.findViewById(R.id.ref2RelativeLayout);
//        ref3RelativeLayout = printLastView.findViewById(R.id.ref3RelativeLayout);

        taxIdLabel = printLastView.findViewById(R.id.taxIdLabel);
        taxAbbLabel = printLastView.findViewById(R.id.taxAbbLabel);
        traceTaxLabel = printLastView.findViewById(R.id.traceTaxLabel);
        batchTaxLabel = printLastView.findViewById(R.id.batchTaxLabel);
        dateTaxLabel = printLastView.findViewById(R.id.dateTaxLabel);
        timeTaxLabel = printLastView.findViewById(R.id.timeTaxLabel);
        feeTaxLabel = printLastView.findViewById(R.id.feeTaxLabel);
        copyLabel = printLastView.findViewById(R.id.copyLabel);
        typeCopyLabel = printLastView.findViewById(R.id.typeCopyLabel);
        nameEmvCardLabel = printLastView.findViewById(R.id.nameEmvCardLabel);
        taxLinearLayout = printLastView.findViewById(R.id.taxLinearLayout);
        sigatureLabel = printLastView.findViewById(R.id.sigatureLabel);
        typeInputCardLabel = printLastView.findViewById(R.id.typeInputCardLabel);
        comCodeFragment = printLastView.findViewById(R.id.comCodeFragment);

        appLabel = printLastView.findViewById(R.id.appLabel);
        appFrameLabel = printLastView.findViewById(R.id.appFrameLabel);
        tcLabel = printLastView.findViewById(R.id.tcLabel);
        tcFrameLayout = printLastView.findViewById(R.id.tcFrameLayout);
        aidLabel = printLastView.findViewById(R.id.aidLabel);
        aidFrameLayout = printLastView.findViewById(R.id.aidFrameLayout);

        name_sw_version = printLastView.findViewById(R.id.name_sw_version);   // Paul_20190125 software version print

    }

    private void setViewReprint() {//K.GAME 180907
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        hgcSaleView = inflater.inflate( R.layout.view_slip_sale_hgc_re, null);
        hgcSaleView = inflater.inflate(R.layout.view_slip_sale_hgc_re, null);
        slip_sale_void_hgc_re = hgcSaleView.findViewById(R.id.slip_sale_void_hgc_re_lay);   //(:
        dateHgcLabel = hgcSaleView.findViewById(R.id.dateLabel);
        DateTimePrn = hgcSaleView.findViewById(R.id.DateTimePrn); //20180720 SINN  HGC slip fix
        timeHgcLabel = hgcSaleView.findViewById(R.id.timeLabel);
        midHgcLabel = hgcSaleView.findViewById(R.id.midLabel);
        tidHgcLabel = hgcSaleView.findViewById(R.id.tidLabel);
        systrcGHCLabel = hgcSaleView.findViewById(R.id.systrcGHCLabel);
        traceNoLabel = hgcSaleView.findViewById(R.id.traceNoLabel);
        typeSaleLabel = hgcSaleView.findViewById(R.id.typeSaleLabel);
        cardNoHgcLabelxx = hgcSaleView.findViewById(R.id.cardNoLabelxx);//PAUL_20180716
        nameEngLabel = hgcSaleView.findViewById(R.id.nameEngLabel);
        apprCodeHgcLabel = hgcSaleView.findViewById(R.id.apprCodeLabel);        // Paul_20180712
        comCodeHgcLabel = hgcSaleView.findViewById(R.id.comCodeLabel);      // Paul_20180714
//        comCodeLabel = hgcSaleView.findViewById(R.id.comCodeLabel);
        batchHgcLabel = hgcSaleView.findViewById(R.id.batchLabel);
        amountLabel = hgcSaleView.findViewById(R.id.amountLabel);
        merchantName1HgcLabel = hgcSaleView.findViewById(R.id.merchantName1Label);
        merchantName2HgcLabel = hgcSaleView.findViewById(R.id.merchantName2Label);
        merchantName3HgcLabel = hgcSaleView.findViewById(R.id.merchantName3Label);
        //20180720 SINN  HGC slip fix
        dateHgcLabel.setText("");
        DateTimePrn.setText("");
        timeHgcLabel.setText("");
        midHgcLabel.setText("");
        tidHgcLabel.setText("");
        systrcGHCLabel.setText("");
        traceNoLabel.setText("");
        typeSaleLabel.setText("");
        cardNoHgcLabelxx.setText("");
        nameEngLabel.setText("");
        apprCodeHgcLabel.setText("");
        comCodeHgcLabel.setText("");
        batchHgcLabel.setText("");
        amountLabel.setText("");
        merchantName1HgcLabel.setText("");
        merchantName2HgcLabel.setText("");
        merchantName3HgcLabel.setText("");
//END 20180720 SINN  HGC slip fix
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
            invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
            approvalcode = bundle.getString(KEY_INTERFACE_VOID_APPROVAL_CODE);
            System.out.printf("utility:: %s approvalcode 0004 = %s \n", TAG, approvalcode);
        }

        posInterfaceActivity = MainApplication.getPosInterfaceActivity();
    }

    public void doPrinting(Bitmap slip) {
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
                            if (isStatusPrintLastSlip) {
                                isStatusPrintLastSlip = false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new CountDownTimer(6000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                Log.d(TAG, "onTick: " + millisUntilFinished);
                                            }

                                            @Override
                                            public void onFinish() {
                                                System.out.printf("utility:: %s doPrinting Befor 039 \n", TAG);
                                                //doPrinting(getBitmapFromView(slipLinearLayout));
                                            }
                                        }.start();
                                    }
                                });
                            } else {
                                dialogLoading.dismiss();
//                                Intent intent = new Intent(ReprintActivity.this, MenuServiceActivity.class);
                                Intent intent = new Intent(ReprintQrListActivity.this, MenuServiceListActivity.class); // Paul_20180704
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
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
//                    int ret = printDev.printBarCodeSync("asdasd");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void initWidget() {
        // super.initWidget();

        invoiceEt = findViewById(R.id.invoiceEt);
        searchInvoiceImage = findViewById(R.id.searchInvoiceImage);
        invoiceEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchDataTransTemp(invoiceEt.getText().toString());
                    return true;
                }
                return false;
            }
        });
        recyclerViewReprintQrList = findViewById(R.id.recyclerViewReprintQrList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReprintQrList.setLayoutManager(layoutManager);


        searchInvoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 돗보기 눌렀을때
                searchDataTransTemp(invoiceEt.getText().toString());
            }
        });


    }


    private void searchDataTransTemp(String traceNo) {
        String traceNoAddZero = "";//ถ้าพิมพ์น้อยกว่า 6 ตัวจะติด 0 ข้างหน้า
        if (!traceNo.isEmpty()) {
            if (traceNo.length() < 6) {
                for (int i = traceNo.length(); i < 6; i++) {
                    traceNoAddZero += "0";

                }

            }
            traceNoAddZero += traceNo;
//            traceBox_new.setText(traceNoAddZero);//K.GAME 180905 Add change EdidText dialog
            Log.d(TAG, "utility:: searchDataTransTemp: " + traceNoAddZero);

//            RealmResults<TransTemp> transTemp1;
//            transTemp1 = realm.where(TransTemp.class).equalTo("ecr", traceNoAddZero).findAll();
            RealmResults<QrCode> qrCode;
            qrCode = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();
//            qrCode = realm.where(QrCode.class).findAll();

//            Log.d(TAG, "utility:: searchDataTransTemp: " + qrCode);
            if (qrCode.size() > 0) {
                reprintQrListAdapter.clear();
                if (transTempList == null) {
                    transTempList = new ArrayList<>();
                } else {
                    transTempList.clear();
                }
                transTempList.addAll(qrCode);
                reprintQrListAdapter.setItem(transTempList);
                reprintQrListAdapter.notifyDataSetChanged();
            }


        } else {
            setVoidList();
        }

    }


    private void setVoidList() {
        if (recyclerViewReprintQrList.getAdapter() == null) {
            reprintQrListAdapter = new ReprintQrListAdapter(this);
            recyclerViewReprintQrList.setAdapter(reprintQrListAdapter);
            reprintQrListAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    // 그냥 눌렀을때

                    System.out.printf("utility:: VoidActivity LLLLLLLLLLLLLL position = %d \n", position);
                    transTemp = reprintQrListAdapter.getItem(position);
                    System.out.printf("utility:: VoidActivity LLLLLLLLLLLLLL transTemp.getTrace() = %s \n", transTemp.getTrace());
                    if (transTemp != null) {
                        //กดที่ Item แล้วจะให้ทำไรต่อ..
//                        customDialogReprintConfirmSlip(transTemp);
                        customDialogReprintConfirmSlip_qr(transTemp);//K.GAME 20181019 New DB
                        Toast.makeText(ReprintQrListActivity.this, transTemp.getTrace(), Toast.LENGTH_SHORT).show();

                    } else {
                        Utility.customDialogAlert(ReprintQrListActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }

                    //                    if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
//                        typeHost = transTemp.getHostTypeCard();
//                        Toast.makeText(ReprintAnyActivity.this, "Position = " + position + " Trace = " + transTemp.getTraceNo(), Toast.LENGTH_SHORT).show();
//                        // customDialogPin(transTemp.getEcr(), transTemp.getAmount(), transTemp);
//                        //K.GAME 180906 ต้องใส่ฟังค์ชันพิมพ์ซ้ำ Dialog Reprint
//                        customDialogReprintConfirmSlip(transTemp);
//
//                    } else {
////                        Utility.customDialogAlert(ReprintAnyActivity.this, "", new Utility.OnClickCloseImage() {
////                            @Override
////                            public void onClickImage(Dialog dialog) {
////                                dialog.dismiss();
////                            }
////                        });
                    //                   }
                }
            });
        } else {
            reprintQrListAdapter.clear();
        }
        if (transTempList == null) {
            transTempList = new ArrayList<>();
        } else {
            transTempList.clear();
        }
//        transTempList.addAll(realm.copyFromRealm(realm.where(TransTemp.class).findAll()));
        RealmResults<QrCode> qrCode;
//        qrCode = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();
        qrCode = realm.where(QrCode.class).findAll();
        transTempList.addAll(qrCode);
        reprintQrListAdapter.setItem(transTempList);
        reprintQrListAdapter.notifyDataSetChanged();


    }

    private void setDataSlipOffline(TransTemp healthCareDB) {
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);
        ////20180720 SINN  HGC slip fix
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        DateTimePrn.setText("Date Time      " + dateFormat.format(date));
        //END 20180720 SINN  HGC slip fix
        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
        systrcGHCLabel.setText(healthCareDB.getTraceNo());
        Log.d("SINN:", "systrcLabel :" + systrcGHCLabel.getText());
        System.out.printf("utility:: systrcGHCLabel 003 = %s \n", systrcGHCLabel.getText());

        traceNoLabel.setText(healthCareDB.getEcr());
        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        System.out.printf("utility:: healthCareDB.getCardNo() = %s \n", healthCareDB.getCardNo());
        System.out.printf("utility:: healthCareDB.getIdCard() = %s \n", healthCareDB.getIdCard());
        String idCardCd = null;
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            cardNoHgcLabelxx.setText(healthCareDB.getCardNo()); //PAUL_20180716
            idCardCd = healthCareDB.getCardNo();
            System.out.printf("utility:: cardNoLabel 00000000XX001 = %s \n", healthCareDB.getCardNo());
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            cardNoHgcLabelxx.setText(healthCareDB.getIdCard());//PAUL_20180716
            idCardCd = healthCareDB.getIdCard();
            System.out.printf("utility:: cardNoLabel 00000000XX002 = %s \n", healthCareDB.getIdCard());
        } else {
            cardNoHgcLabelxx.setText(healthCareDB.getCardNo());//PAUL_20180716
            idCardCd = healthCareDB.getCardNo();
            System.out.printf("utility:: cardNoLabel 00000000XX003 = %s \n", healthCareDB.getCardNo());
        }
        System.out.printf("utility:: cardNoLabel 000001 = %s \n", cardNoLabel.getText());
        String szMSG = null;
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        cardNoHgcLabelxx.setText(szMSG);
//        nameEngLabel.setText(healthCareDB.getEngFName());
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
        apprCodeHgcLabel.setText(healthCareDB.getApprvCode());
        comCodeHgcLabel.setText(healthCareDB.getComCode()); //20180714_PAUL
        batchHgcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        amountLabel.setText("*" + healthCareDB.getAmount());

        setMeasureHGC();
    }

    private void setMeasureHGC() {
        hgcSaleView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleView.layout(0, 0, hgcSaleView.getMeasuredWidth(), hgcSaleView.getMeasuredHeight());
    }

    private void setPrint_off(TransTemp transTemp) {
        setDataSlipOffline(transTemp);
//        //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
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

    private void setDataSlipSale(TransTemp healthCareDB) {
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00"); // Paul_20180711_new
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);
        //20180720 SINN  HGC slip fix
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        DateTimePrn.setText("Date Time      " + dateFormat.format(date));
//END 20180720 SINN  HGC slip fix
        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
//        systrcLabel.setText(healthCareDB.getTraceNo());
        systrcGHCLabel.setText(healthCareDB.getTraceNo());  //(:
        Log.d("SINN:", "systrcLabel :" + systrcLabel.getText());
        System.out.printf("utility:: systrcLabel 002 = %s \n", healthCareDB.getTraceNo());

        traceNoLabel.setText(healthCareDB.getEcr());
        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        System.out.printf("utility:: healthCareDB.getCardNo() = %s \n", healthCareDB.getCardNo());
        System.out.printf("utility:: healthCareDB.getIdCard() = %s \n", healthCareDB.getIdCard());

        // Paul_20180720 Start
        String szMSG = null;
        String CardNo = null;
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            CardNo = healthCareDB.getCardNo();
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            CardNo = healthCareDB.getIdCard();
        } else {
            CardNo = healthCareDB.getCardNo();      // Paul_20180720              CardNo = healthCareDB.getIdCard();
        }
        szMSG = CardNo.substring(0, 1) + " " + CardNo.substring(1, 4) + "X" + " " + "XXXX" + CardNo.substring(9, 10) + " " + CardNo.substring(10, 12) + " " + CardNo.substring(12, 13);
        cardNoHgcLabelxx.setText(szMSG);       // Paul_20180720
        // Paul_20180720 End
//        nameEngLabel.setText(healthCareDB.getEngFName());     // Paul_20180720
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
        apprCodeHgcLabel.setText(healthCareDB.getApprvCode()); // Paul_20180712
        System.out.printf("utility:: HHHHHHHHHHHHHH 0004 apprCodeLabel = %s \n", apprCodeLabel);
//        comCodeLabel.setText("HCG13814");
        comCodeHgcLabel.setText(healthCareDB.getComCode()); // Paul_20180714
        batchHgcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));
        //amountLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(priceLabel.getText().toString()))));
//        getString(healthCareDB.getAmount(), decimalFormat.format(Double.valueOf(healthCareDB.getAmount())));
//        amountLabel.setText( healthCareDB.getAmount());

        if (healthCareDB.getVoidFlag().equals("N")) {
            amountLabel.setText("*" + healthCareDB.getAmount());
        } else {
            amountLabel.setText("-" + healthCareDB.getAmount());
        }

        setMeasureHGC();
    }

    private void setPrintLastSearch(TransTemp transTemp) {
        if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) //20180708 SINN Add healthcare print
        {
            Log.d("SINN", "setPrintLastSearch");
            setDataSlipSale(transTemp);
//            //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));  //(:
            return;
        }

        dialogLoading.show();
        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText(transTemp.getTid());
        midLabel.setText(transTemp.getMid());
        traceLabel.setText(transTemp.getEcr());
        systrcLabel.setText(transTemp.getTraceNo());
        if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            //20180708 SINN Add healthcare print.
        else if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC"))
            batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        //20180708 SINN Add healthcare print.
        refNoLabel.setText(transTemp.getRefNo());
        String date = transTemp.getTransDate().substring(6, 8);
        String mount = transTemp.getTransDate().substring(4, 6);
        String year = transTemp.getTransDate().substring(2, 4);
        dateLabel.setText(date + "/" + mount + "/" + year);
        timeLabel.setText(transTemp.getTransTime());

        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        typeCardLabel.setText(CardPrefix.getTypeCardName(transTemp.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(transTemp.getCardNo()));
//        String cutCardStart = transTemp.getCardNo().substring(0, 6);
//        String cutCardEnd = transTemp.getCardNo().substring(12, transTemp.getCardNo().length());
//        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
//        cardNoLabel.setText(cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16));
        cardNoLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555

        apprCodeLabel.setText(transTemp.getApprvCode());
        System.out.printf("utility:: HHHHHHHHHHHHHH 0003 apprCodeLabel = %s \n", apprCodeLabel);
//        comCodeLabel.setText(transTemp.getComCode());
        String typeVoidOrSale = "";
        if (!Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_MERCHANT_3));

        if (typeHost.equalsIgnoreCase("POS")) {
            typeVoidOrSale = Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_POS);
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            typeVoidOrSale = Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_EPS);
        } else {
            typeVoidOrSale = Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_SETTLE_TYPE_TMS);
        }

        if (transTemp.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabel.setText("C");
        } else if (transTemp.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabel.setText("W");
        } else {
            typeInputCardLabel.setText("S");
        }


        if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
            feeTaxLabel.setText(getString(R.string.slip_pattern_amount, transTemp.getFee()));
            typeLabel.setText("SALE");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel.setText(transTemp.getTaxAbb());
                traceTaxLabel.setText(transTemp.getEcr());

                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));

                /*String date = transTemp.getTransDate().substring(6,8);
                String mount = transTemp.getTransDate().substring(4,6);
                String year = transTemp.getTransDate().substring(0,4);
                dateLabel.setText(date +"/" +mount + "/" + year);*/
                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);

                timeTaxLabel.setText(transTemp.getTransTime());
                copyLabel.setText("***** MERCHANT COPY *****");
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null)
                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format( (amount + fee)))); // Paul_20190128 (float)
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
                if (transTemp.getEmvAppLabel() != null) {
                    if (!transTemp.getEmvAppLabel().isEmpty()) {
                        appLabel.setText(transTemp.getEmvAppLabel());
                    } else {
                        // appFrameLabel.setVisibility(View.GONE);
                    }
                } else {
                    // appFrameLabel.setVisibility(View.GONE);
                }

                if (transTemp.getEmvTc() != null) {
                    if (!transTemp.getEmvTc().isEmpty()) {
                        tcLabel.setText(transTemp.getEmvTc());
                    } else {
                        tcFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    tcFrameLayout.setVisibility(View.GONE);
                }
                if (transTemp.getEmvAid() != null) {
                    if (!transTemp.getEmvAid().isEmpty()) {
                        aidLabel.setText(transTemp.getEmvAid());
                    } else {
                        aidFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    aidFrameLayout.setVisibility(View.GONE);
                }
            } else {
                comCodeFragment.setVisibility(View.VISIBLE);
                copyLabel.setText("***** MERCHANT COPY *****");
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                // appFrameLabel.setVisibility(View.GONE);
                tcFrameLayout.setVisibility(View.GONE);
                aidFrameLayout.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);

                taxLinearLayout.setVisibility(View.GONE);
                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
            }
        } else {
            feeTaxLabel.setText(getString(R.string.slip_pattern_amount_void, transTemp.getFee()));
            typeLabel.setText("VOID");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(transTemp.getAmount()))));
            if (!transTemp.getHostTypeCard().equals("TMS")) {
                comCodeFragment.setVisibility(View.GONE);
                taxIdLabel.setText(Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_TAX_ID));
                taxAbbLabel.setText(transTemp.getTaxAbb());
                traceTaxLabel.setText(transTemp.getEcr());
                if (transTemp.getHostTypeCard().equalsIgnoreCase("POS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("EPS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
                else if (transTemp.getHostTypeCard().equalsIgnoreCase("TMS"))
                    batchTaxLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
                String dateTax = transTemp.getTransDate().substring(6, 8);
                String mountTax = transTemp.getTransDate().substring(4, 6);
                String yearTax = transTemp.getTransDate().substring(2, 4);
                dateTaxLabel.setText(dateTax + "/" + mountTax + "/" + yearTax);
//                dateTaxLabel.setText(transTemp.getTransDate());
                timeTaxLabel.setText(transTemp.getTransTime());
//                feeTaxLabel.setText(transTemp.getFee());
                copyLabel.setText("***** MERCHANT COPY *****");
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                if (transTemp.getEmvNameCardHolder() != null)
                    nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());

                if (transTemp.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getFee()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getFee())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));    // Paul_20190128 (float)
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
                if (transTemp.getEmvAppLabel() != null) {
                    if (!transTemp.getEmvAppLabel().isEmpty()) {
                        appLabel.setText(transTemp.getEmvAppLabel());
                    } else {
                        // appFrameLabel.setVisibility(View.GONE);
                    }
                } else {
                    // appFrameLabel.setVisibility(View.GONE);
                }

                if (transTemp.getEmvTc() != null) {
                    if (!transTemp.getEmvTc().isEmpty()) {
                        tcLabel.setText(transTemp.getEmvTc());
                    } else {
                        tcFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    tcFrameLayout.setVisibility(View.GONE);
                }
                if (transTemp.getEmvAid() != null) {
                    if (!transTemp.getEmvAid().isEmpty()) {
                        aidLabel.setText(transTemp.getEmvAid());
                    } else {
                        aidFrameLayout.setVisibility(View.GONE);
                    }
                } else {
                    aidFrameLayout.setVisibility(View.GONE);
                }
            } else {
                comCodeFragment.setVisibility(View.VISIBLE);
                taxLinearLayout.setVisibility(View.GONE);

                // appFrameLabel.setVisibility(View.GONE);
                tcFrameLayout.setVisibility(View.GONE);
                aidFrameLayout.setVisibility(View.GONE);
                taxLinearLayout.setVisibility(View.GONE);

                copyLabel.setText("***** MERCHANT COPY *****");
                typeCopyLabel.setText("***** MERCHANT COPY *****");
                nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
                if (transTemp.getEmciFree() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Double.valueOf(transTemp.getEmciFree()))));
                    double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getEmciFree())));
                    double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(transTemp.getAmount())));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((double) (amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }

        String valueParameterEnable = Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_TAG_1000);
        if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(transTemp.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
            comCodeLabel.setVisibility(View.GONE);
            comCodeLabel.setText(transTemp.getComCode());
        }
        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
            ref1RelativeLayout.setVisibility(View.GONE);
            ref1Label.setText(transTemp.getRef1());
        }
        if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
            ref2RelativeLayout.setVisibility(View.GONE);
            ref2Label.setText(transTemp.getRef2());
        }
//        if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
//            ref3RelativeLayout.setVisibility(View.VISIBLE);
//            ref3Label.setText(transTemp.getRef3());
//        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
//            ref3RelativeLayout.setVisibility(View.VISIBLE);
//            ref3Label.setText(transTemp.getRef3());
//        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
//            ref3RelativeLayout.setVisibility(View.GONE);
//            ref3Label.setText(transTemp.getRef3());
//        }
        name_sw_version.setVisibility(View.VISIBLE);             // Paul_20190125 software version print
        name_sw_version.setText( BuildConfig.VERSION_NAME);      // Paul_20190125 software version print
        /*if (!transTemp.getRef1().isEmpty()) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(transTemp.getRef1());
        }
        if (!transTemp.getRef2().isEmpty()) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(transTemp.getRef2());
        }
        if (!transTemp.getRef3().isEmpty()) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(transTemp.getRef3());
        }*/
        setMeasure();

        isStatusPrintLastSlip = true;
        System.out.printf("utility:: %s doPrinting Befor 040 \n", TAG);
        //doPrinting(getBitmapFromView(slipLinearLayout));

        rePrintLast(transTemp);
    }

    private void setMeasure() {
        printLastView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printLastView.layout(0, 0, printLastView.getMeasuredWidth(), printLastView.getMeasuredHeight());
    }

    private void rePrintLast(TransTemp transTemp) {
        sigatureLabel.setVisibility(View.GONE);
        if (transTemp.getEmvNameCardHolder() != null)
            nameEmvCardLabel.setText(transTemp.getEmvNameCardHolder().trim());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        nameEmvCardLabel.setLayoutParams(lp);
        nameEmvCardLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        copyLabel.setText("**** ต้นฉบับ ****");
        typeCopyLabel.setText("***** CUSTOMER COPY *****");

        setMeasure();

    }

    private void customDialogReprintConfirmSlip_qr(final QrCode TransTempDB) { //K.GAME 20181019
        final Dialog dialogReprintConfirmSlip = new Dialog(this);
        dialogReprintConfirmSlip.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReprintConfirmSlip.setContentView(R.layout.dialog_custom_reprint_confirm_qr_new);
//        dialogReprintConfirmSlip.setCancelable(false);
        dialogReprintConfirmSlip.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReprintConfirmSlip.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ////
        Button btn_confirm_reprint = dialogReprintConfirmSlip.findViewById(R.id.btn_confirm_reprint);
        TextView msgLabel = dialogReprintConfirmSlip.findViewById(R.id.msgLabel);
        TextView tvLabelName = dialogReprintConfirmSlip.findViewById(R.id.tvLabelName);
        TextView tv_confirm_numberPrice = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_numberPrice);//ยืนยันราคา
        TextView tv_confirm_date = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_date);//date
        TextView tv_confirm_time = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_time);//time
        TextView tv_confirm_traceNo = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_traceNo);//trace no.
        TextView tv_confirm_status = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_status);//status เช่น Success
        TextView tv_confirm_referenceNo = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_referenceNo);
        TextView tv_confirm_ref1 = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_ref1);
        TextView tv_confirm_ref2 = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_ref2);
        TextView tv_confirm_billerId = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_billerId);

        TextView tv_confirm_unit = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_unit);//สีตัวหนังสื่อ คำว่า บาท

        Button btn_confirm_reprint1 = dialogReprintConfirmSlip.findViewById(R.id.btn_confirm_reprint1);//ลูกค้า
        Button btn_confirm_inquire = dialogReprintConfirmSlip.findViewById(R.id.btn_confirm_inquire);//ร้านค้า


        tv_confirm_date.setText(TransTempDB.getDate());

        String HH = TransTempDB.getTime().substring(0, 2);
        String mm = TransTempDB.getTime().substring(2, 4);
        String ss = TransTempDB.getTime().substring(4, 6);
        tv_confirm_time.setText(HH + ":" + mm + ":" + ss);

        tv_confirm_traceNo.setText(TransTempDB.getTrace());

        if (TransTempDB.getStatusSuccess().equals("1")) {
            tv_confirm_status.setText("Success");
            tv_confirm_numberPrice.setText(TransTempDB.getAmount());
            btn_confirm_reprint1.setVisibility(View.VISIBLE);
            btn_confirm_inquire.setVisibility(View.GONE);
        } else if (TransTempDB.getStatusSuccess().equals("0")) {
            tv_confirm_status.setText("Pending");
            tv_confirm_unit.setTextColor(Color.YELLOW);
            tv_confirm_numberPrice.setTextColor(Color.YELLOW);
            tv_confirm_numberPrice.setText(TransTempDB.getAmount());
            btn_confirm_reprint1.setVisibility(View.GONE);
            btn_confirm_inquire.setVisibility(View.VISIBLE);
        } else {
            tv_confirm_status.setText("Void");
            tv_confirm_unit.setTextColor(Color.RED);
            tv_confirm_numberPrice.setTextColor(Color.RED);
            tv_confirm_numberPrice.setText("- " + TransTempDB.getAmount());
            btn_confirm_reprint1.setVisibility(View.VISIBLE);
            btn_confirm_inquire.setVisibility(View.GONE);
        }


        tv_confirm_referenceNo.setText(TransTempDB.getQrTid());
        if (!TransTempDB.getRef1().equals("")) {
            tv_confirm_ref1.setText(TransTempDB.getRef1());
        } else {
            tv_confirm_ref1.setText("-");
        }
        if (!TransTempDB.getRef2().equals("")) {
            tv_confirm_ref2.setText(TransTempDB.getRef2());
        } else {
            tv_confirm_ref2.setText("-");
        }
        tv_confirm_billerId.setText(TransTempDB.getBillerId());


        dialogReprintConfirmSlip.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                dialogReprintConfirmSlip.dismiss();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 20181019 back button
        btn_confirm_reprint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
//                szREF1 = bundle.getString(KEY_INTERFACE_REF1);
                Intent intent = new Intent(ReprintQrListActivity.this, ReprintQRCheckActivity.class);
//                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, TransTempDB.getTrace());
                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        btn_confirm_inquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
//                szREF1 = bundle.getString(KEY_INTERFACE_REF1);
                Intent intent = new Intent(ReprintQrListActivity.this, ReprintQRCheckActivity.class);
//                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER, TransTempDB.getTrace());
                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_REF1, "REPRINT");
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        dialogReprintConfirmSlip.show();
    }


    private void customDialogLoading() {
        //K.GAME 180926
        dialogLoading = new Dialog(this);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(R.layout.dialog_custom_load_process);
        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogLoading.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogLoading);
        //END K.GAME 180831 chang waitting UI
        //END K.GAME 180926
        dialogLoading.setCancelable(false);   // Paul_20181015 Printing Can not cancel button

//        dialogLoading = new Dialog(this);
//        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogLoading.setContentView(R.layout.dialog_custom_alert_loading);
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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
                System.out.printf("utility:: %s doPrinting Befor 036 \n", TAG);
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    private void customDialogReprintConfirmSlip(TransTemp TransTempDB) { //K.GAME 180903 new dialog
        final Dialog dialogReprintConfirmSlip = new Dialog(this);
        dialogReprintConfirmSlip.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReprintConfirmSlip.setContentView(R.layout.dialog_custom_reprint_confirm);
        dialogReprintConfirmSlip.setCancelable(false);
        dialogReprintConfirmSlip.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReprintConfirmSlip.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ////
        Button btn_confirm_reprint = dialogReprintConfirmSlip.findViewById(R.id.btn_confirm_reprint);
        TextView msgLabel = dialogReprintConfirmSlip.findViewById(R.id.msgLabel);
        TextView tvLabelName = dialogReprintConfirmSlip.findViewById(R.id.tvLabelName);
        TextView tv_confirm_numberPrice = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_numberPrice);//ยืนยันราคา
        TextView tv_confirm_idcard = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_idcard);//ยืนยัน id card
        TextView tv_confirm_username = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_username);//usrname
        TextView tv_confirm_date = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_date);//date
        TextView tv_confirm_time = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_time);//time
        TextView tv_confirm_traceNo = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_traceNo);//trace no.
        TextView tv_confirm_status = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_status);//status เช่น Success
        TextView tv_confirm_terminalId = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_terminalId);//Terminal ID
        TextView tv_confirm_apprCode = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_apprCode);//appr code
        TextView tv_confirm_merchantId = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_merchantId);//merchant Id
        TextView tv_confirm_BatchNo = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_BatchNo);//Batch No
        TextView tv_confirm_comCode = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_comCode);//com Code

        ////
        //SINN 20180911 reprint any set void font
        //tv_confirm_numberPrice.setText(TransTempDB.getAmount());
        TextView tv_confirm_unit = dialogReprintConfirmSlip.findViewById(R.id.tv_confirm_unit);
        if (TransTempDB.getVoidFlag().equals("N")) {
            tv_confirm_numberPrice.setText(TransTempDB.getAmount());
            tv_confirm_numberPrice.setTextColor(Color.GREEN);
            tv_confirm_unit.setTextColor(Color.GREEN);
        } else {
            tv_confirm_numberPrice.setText("-" + TransTempDB.getAmount());
            tv_confirm_numberPrice.setTextColor(Color.RED);
            tv_confirm_unit.setTextColor(Color.RED);
        }


//        tv_confirm_idcard.setText(TransTempDB.getCardNo());
//        tv_confirm_idcard.setText(CardPrefix.maskcard(Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_CARDMASK_ID).toString(), TransTempDB.getCardNo()));
        tv_confirm_idcard.setText(CardPrefix.maskviewcard(" ",TransTempDB.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555

        tv_confirm_username.setText(TransTempDB.getThName());
        tv_confirm_date.setText(TransTempDB.getTransDate());
        tv_confirm_time.setText(TransTempDB.getTransTime());
        tv_confirm_traceNo.setText(TransTempDB.getTraceNo());
        tv_confirm_status.setText("");
        tv_confirm_terminalId.setText(TransTempDB.getTid());
        tv_confirm_apprCode.setText(TransTempDB.getApprvCode());
        tv_confirm_merchantId.setText(TransTempDB.getMid());
        tv_confirm_BatchNo.setText(Preference.getInstance(ReprintQrListActivity.this).getValueString(Preference.KEY_SETTLE_BATCH_GHC));
        tv_confirm_comCode.setText(TransTempDB.getComCode());

        typeHost = TransTempDB.getHostTypeCard();

        if (typeHost.equals("GHC") && TransTempDB.getGhcoffFlg().equalsIgnoreCase("Y")) {
            setPrint_off(TransTempDB);

        } else {
            setPrintLastSearch(TransTempDB);
        }

        btn_confirm_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReprintQrListActivity.this, "พิมพ์ซ้ำ", Toast.LENGTH_SHORT).show();
                //K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้ สั่งปริ้น ต้องหาค่า trace มาหยอด
//                Toast.makeText(ReprintAnyActivity.this, " Trace = " + transTemp.getTraceNo(), Toast.LENGTH_SHORT).show();
                // transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", transTemp.getTraceNo() + invoiceEt.getText().toString()).findFirst();
                dialogReprintConfirmSlip.dismiss();

                if (typeHost.equals("GHC")) {
                    System.out.printf("utility:: %s doPrinting Befor 037 \n", TAG);
                    //doPrinting(getBitmapFromView(slip_sale_void_hgc_re));
                } else {
                    if (isStatusPrintLastSlip) {
                        isStatusPrintLastSlip = false;
                        System.out.printf("utility:: %s doPrinting Befor 038 \n", TAG);
                        //doPrinting(getBitmapFromView(slipLinearLayout));
                    }
                }

                //END K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้
            }
        });


//
//        btn_confirm_reprint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ReprintAnyActivity.this, "พิมพ์ซ้ำ", Toast.LENGTH_SHORT).show();
//                //K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้ สั่งปริ้น ต้องหาค่า trace มาหยอด
////                Toast.makeText(ReprintAnyActivity.this, " Trace = " + transTemp.getTraceNo(), Toast.LENGTH_SHORT).show();
//                // transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("ecr", transTemp.getTraceNo() + invoiceEt.getText().toString()).findFirst();
//                if (TransTempDB != null) {
//                    if (typeHost.equals("GHC") && TransTempDB.getGhcoffFlg().equalsIgnoreCase("Y")) {
//                        setPrint_off(TransTempDB);
//                        dialogReprintConfirmSlip.dismiss();
//                    } else {
//                        setPrintLastSearch(TransTempDB);
//                        dialogReprintConfirmSlip.dismiss();
//                    }
//                } else {
//                    Utility.customDialogAlert(ReprintAnyActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                            dialogReprintConfirmSlip.dismiss();
//                        }
//                    });
//                }
//                //END K.GAME 080907 น่าจะเอามาดัดแปลงใช้ได้
//            }
//        });
        dialogReprintConfirmSlip.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVoidList();
        realm = Realm.getDefaultInstance();       // Paul_20180809

    }


    @Override
    public void initBtnExit() {
        super.initBtnExit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }
}