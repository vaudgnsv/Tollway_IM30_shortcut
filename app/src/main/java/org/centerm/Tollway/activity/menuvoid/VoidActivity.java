package org.centerm.Tollway.activity.menuvoid;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.CalculatePriceActivity;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.SlipTemplateActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.adapter.VoidAdapter;
import org.centerm.Tollway.alipay.AliConfig;
import org.centerm.Tollway.alipay.AliVoidActivity;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.database.ReversalHealthCare;
import org.centerm.Tollway.database.ReversalTemp;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.healthcare.activity.CalculateHelthCareActivity;
import org.centerm.Tollway.healthcare.activity.SlipTemplateHealthCareActivity;
import org.centerm.Tollway.healthcare.baseavtivity.BaseHealthCardActivity;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_APPROVAL_CODE;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_VOID_INVOICE_NUMBER;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;
import static org.centerm.Tollway.activity.posinterface.PosInterfaceActivity.PosInterfaceTransactionCode;

public class VoidActivity extends BaseHealthCardActivity {

    private final String TAG = "VoidActivity";

    private boolean flag_check = false;

    private EditText invoiceEt;
    //    private ImageView searchInvoiceImage; //Jeff20181019
    private Dialog dialogInvoice;
    private RecyclerView recyclerViewVoid;
    private VoidAdapter voidAdapter;
    private ArrayList<TransTemp> transTempList;

    private Realm realm;
    private CardManager cardManager;
    private Dialog dialogWaiting;
    private Dialog dialogPin;
    private Dialog dialogTrace;     //SINN 20181107 dialogpin call by to methord will arrange
    private Dialog dialogPinOld;   //SINN 20181107 dialogpin call by to methord will arrange

    private String typeHost = null;
    private Dialog dialogApprCode;

    private TransTemp transTemp = null;
    private QrCode qrCode = null;

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
    private TextView cardNoLabel = null;
    private TextView systrcGHCLabel = null;
    private String[] mBlockDataSend = null;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private AidlPrinter printDev = null;
    private Bitmap bitmapOld;
    private boolean isStatusPrintLastSlip = false;
    private TextView msgLabel;
    private LinearLayout settlementHgcLinearLayout;
    private TextView systrcLabel = null;
    private TextView apprCodeLabel;
    private TextView comCodeLabel;

    /**
     * Interface
     */
    private String typeInterface;
    private String invoiceId;
    private String approvalcode;
    private PosInterfaceActivity posInterfaceActivity;
    //K.GAME
    private EditText pinBox_new = null;//K.GAME 180905 add dialog teace No.
    private EditText traceBox_new = null;//K.GAME 180905 add dialog teace No.

    //END K.GAME
    //K.GAME 180904 Add Calculate
    private FrameLayout oneClickFrameLayout = null;
    private FrameLayout twoClickFrameLayout = null;
    private FrameLayout threeClickFrameLayout = null;
    private FrameLayout fourClickFrameLayout = null;
    private FrameLayout fiveClickFrameLayout = null;
    private FrameLayout sixClickFrameLayout = null;
    private FrameLayout sevenClickFrameLayout = null;
    private FrameLayout eightClickFrameLayout = null;
    private FrameLayout nineClickFrameLayout = null;
    private FrameLayout zeroClickFrameLayout = null;

    private FrameLayout deleteClickFrameLayout = null;
    private String numberPrice = "";
    private String invoice = "";
    private LinearLayout numberLinearLayout;

    //
//    private String numberIdcard_cal;
    //END K.GAME 180904 Add Calculate

    //K.GAME 181017 New Dialog APPR.CODE PIN
    private FrameLayout oneClickFrameLayout_ApprCode = null;
    private FrameLayout twoClickFrameLayout_ApprCode = null;
    private FrameLayout threeClickFrameLayout_ApprCode = null;
    private FrameLayout fourClickFrameLayout_ApprCode = null;
    private FrameLayout fiveClickFrameLayout_ApprCode = null;
    private FrameLayout sixClickFrameLayout_ApprCode = null;
    private FrameLayout sevenClickFrameLayout_ApprCode = null;
    private FrameLayout eightClickFrameLayout_ApprCode = null;
    private FrameLayout nineClickFrameLayout_ApprCode = null;
    private FrameLayout zeroClickFrameLayout_ApprCode = null;
    private FrameLayout deleteClickFrameLayout_ApprCode = null;
    private LinearLayout numberLinearLayout_ApprCode;
    private String number_ApprCode = "";
    private EditText apprcodeBox_new = null;//K.GAME 180905 add dialog teace No.
    private Dialog dialogcustomDialogApprCode_new;//K.GAME 181017 New Dialog APPR.CODE VOID

    //END K.GAME 181017 New Dialog APPR.CODE PIN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.printf("utility:: %s onCreate \n",TAG);
        setContentView(R.layout.activity_void);
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        realm = Realm.getDefaultInstance();
        initData();
        initWidget();
        initBtnExit();
//        setVoidList();

        customDialogPin_new();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
            invoiceId = bundle.getString(KEY_INTERFACE_VOID_INVOICE_NUMBER);
            approvalcode = bundle.getString(KEY_INTERFACE_VOID_APPROVAL_CODE);
            System.out.printf("utility:: %s approvalcode 0004 = %s \n", TAG, approvalcode);
            if (typeInterface != null)    //// Paul_20181015
            {
                customDialogWaiting();
                dialogWaiting.setCancelable(false);
                dialogWaiting.show();
            }
        }

        posInterfaceActivity = MainApplication.getPosInterfaceActivity();
    }

    public void initWidget() {
//        super.initWidget();
        customDialogWaiting();
        customDialogApprCode();
        customDialogApprCode_new();//K.GAME 181024
        customDialogOutOfPaper();
        setViewSettlementHGC();
        invoiceEt = findViewById(R.id.invoiceEt);
//        searchInvoiceImage = findViewById(R.id.searchInvoiceImage);
//Jeff20181019
//        invoiceEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    searchDataTransTemp(invoiceEt.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });
        recyclerViewVoid = findViewById(R.id.recyclerViewVoid);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewVoid.setLayoutManager(layoutManager);

//        searchInvoiceImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
// 돗보기 눌렀을때
//                searchDataTransTemp(invoiceEt.getText().toString());
//            }
//        });

        cardManager.setInsertOrUpdateDatabase(new CardManager.InsertOrUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {
                if (!isFinishing()) {
                    if (dialogPin != null) {
                        if (dialogPin.isShowing()) {
                            dialogPin.dismiss();
                            dialogPin = null;   // Paul_20181106 Read Worning
                        }
                    }
                    if (dialogApprCode != null) {
                        if (dialogApprCode.isShowing()) {
                            dialogApprCode.dismiss();
                        }
                        if (dialogcustomDialogApprCode_new.isShowing())
                            dialogcustomDialogApprCode_new.dismiss();//K.GAME 181024

                    }

//                    if (dialogWaiting != null)
//                        dialogWaiting.dismiss();

                    System.out.printf("utility:: %s onUpdateVoidSuccess 0001 \n", TAG);

                    // Paul_20180708
                    String tmp = cardManager.getHostCard();
                    Intent intent;
                    if (tmp.equals("GHC")) {
                        intent = new Intent(VoidActivity.this, SlipTemplateHealthCareActivity.class);
                        intent.putExtra(CalculateHelthCareActivity.KEY_CALCULATE_ID_HGC, id);
                        intent.putExtra(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID, CalculatePriceActivity.TypeVoid);

                    } else {
                        intent = new Intent(VoidActivity.this, SlipTemplateActivity.class);
                        intent.putExtra(CalculatePriceActivity.KEY_CALCUATE_ID, id);
                        intent.putExtra(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID, CalculatePriceActivity.TypeVoid);
                    }
                    intent.putExtra(CalculatePriceActivity.KEY_CALCUATE_ID, id);
                    intent.putExtra(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID, CalculatePriceActivity.TypeVoid);

                    if (typeInterface != null)
                        intent.putExtra(MenuServiceListActivity.KEY_TYPE_INTERFACE, typeInterface);  ////SINN 20180711 RS232 Link

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    Log.d(TAG, "onUpdateVoidSuccess: ");
                }
            }

            @Override
            public void onInsertSuccess(int nextId) {

            }
        });


//        checkReversal();
    }

    private void setViewSettlementHGC() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hgcSaleView = inflater.inflate(R.layout.view_slip_sale_hgc, null);
        settlementHgcLinearLayout = hgcSaleView.findViewById(R.id.settlementLinearLayout);
        dateHgcLabel = hgcSaleView.findViewById(R.id.dateLabel);
        timeHgcLabel = hgcSaleView.findViewById(R.id.timeLabel);
        midHgcLabel = hgcSaleView.findViewById(R.id.midLabel);
        tidHgcLabel = hgcSaleView.findViewById(R.id.tidLabel);
        systrcLabel = hgcSaleView.findViewById(R.id.systrcLabel);
        traceNoLabel = hgcSaleView.findViewById(R.id.traceNoLabel);
        typeSaleLabel = hgcSaleView.findViewById(R.id.typeSaleLabel);
        cardNoLabel = hgcSaleView.findViewById(R.id.cardNoLabel);
        nameEngLabel = hgcSaleView.findViewById(R.id.nameEngLabel);
        apprCodeLabel = hgcSaleView.findViewById(R.id.apprCodeLabel);
        comCodeLabel = hgcSaleView.findViewById(R.id.comCodeLabel);
        batchHgcLabel = hgcSaleView.findViewById(R.id.batchLabel);
        amountLabel = hgcSaleView.findViewById(R.id.amountLabel);
        merchantName1HgcLabel = hgcSaleView.findViewById(R.id.merchantName1Label);
        merchantName2HgcLabel = hgcSaleView.findViewById(R.id.merchantName2Label);
        merchantName3HgcLabel = hgcSaleView.findViewById(R.id.merchantName3Label);
    }

    private void setResponseCode() {

        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(final String response) {
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(VoidActivity.this, response, new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onResponseCodeandMSG(final String response, final String szCode) {
                System.out.printf("utility:: VoidActivity onResponseCodeandMSG 000004 \n");
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                System.out.printf("utility:: VoidActivity onResponseCodeandMSG 000001 \n");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        dialogAlert.show();
                                        Log.d(TAG, "responseCodeDialog() response: " + szCode);
                                        //TellToPosError(response);
                                        TellToPosError(szCode);
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
//                                                dialogAlert.dismiss();
                                                posInterfaceActivity.PosInterfaceExistFlg = 0;
                                                // finish();
                                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(0, 0);
                                            }
                                        });
                                    }
                                });
                            } else
                                //SINN 20181127 Add dialog error goto main menu.
//                                Utility.customDialogAlert(VoidActivity.this, response, new Utility.OnClickCloseImage() {
                                Utility.customDialogAlert_gotomain(VoidActivity.this, response, new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        }
                    });
                }
            }

            @Override
            public void onResponseCodeSuccess() {
//                if (!isFinishing()) {
//                    if (dialogWaiting != null) {
//                        dialogWaiting.dismiss();
//                    }
//                }
            }

            @Override
            public void onConnectTimeOut() {
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.printf("utility:: %s onConnectTimeOut 001 \n", TAG);
// Paul_20180723
                            if (typeInterface != null) {
//Paul_20181015 Why Remark ?
                                Utility.customDialogAlertAuto(VoidActivity.this, "เชื่อมต่อล้มเหลว");
                                TellToPosError("21");
                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        Utility.customDialogAlertAutoClear();
                                        Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(0, 0);
                                    }
                                });
                            } else
                                Utility.customDialogAlert(VoidActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        }
                    });
                }
            }

            @Override
            public void onTransactionTimeOut() {
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.printf("utility:: %s onTransactionTimeOut 002 \n", TAG);
// Paul_20180723
                            if (typeInterface != null) {
// Paul_20181015 Why Remark ?
                                Utility.customDialogAlertAuto(VoidActivity.this, "เชื่อมต่อล้มเหลว");
                                TellToPosError("21");
                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        Utility.customDialogAlertAutoClear();
                                        Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(0, 0);
                                    }
                                });
                            } else
                                Utility.customDialogAlert(VoidActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        }
                    });
                }
            }
        });
    }

    private void checkReversal() {
        ReversalTemp reversalTemp = realm.where(ReversalTemp.class).equalTo("hostTypeCard", typeHost).findFirst();
        if (reversalTemp != null) {
            dialogWaiting.dismiss();
            cardManager.setDataReversalAndSendHost(reversalTemp);
        }
    }

    private void customDialogInvoice_new(String traceNo, String price, String apprcode, String
            ref1, String ref2, String ref3) {
        dialogInvoice = new Dialog(this);
        dialogInvoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogInvoice.setContentView(R.layout.dialog_custom_invoice_new);//K.GAME 181002 change UI Dialog
        dialogInvoice.setContentView(R.layout.dialog_custom_void_confirm);//K.GAME 181002 change UI Dialog
        dialogInvoice.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogInvoice.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView traceNumberLabel = dialogInvoice.findViewById(R.id.traceNumberLabel);
        TextView priceLabel = dialogInvoice.findViewById(R.id.priceLabel);
        TextView cardNumberLabel = dialogInvoice.findViewById(R.id.cardNumberLabel);
        TextView dialogTitleLabel = dialogInvoice.findViewById(R.id.dialogTitleLabel);
        TextView tv_apprCode = dialogInvoice.findViewById(R.id.tv_apprCode);
        TextView tv_ref_1 = dialogInvoice.findViewById(R.id.tv_ref_1);
        TextView tv_ref_2 = dialogInvoice.findViewById(R.id.tv_ref_2);
        TextView tv_ref_3 = dialogInvoice.findViewById(R.id.tv_ref_3);
        Button saveBtn = dialogInvoice.findViewById(R.id.saveBtn);
        Button cancelBtn = dialogInvoice.findViewById(R.id.cancelBtn);
        LinearLayout linear_ref1 = dialogInvoice.findViewById(R.id.linear_ref1);
        LinearLayout linear_ref2 = dialogInvoice.findViewById(R.id.linear_ref2);
        LinearLayout linear_ref3 = dialogInvoice.findViewById(R.id.linear_ref3);
        DecimalFormat decimalFormatShowGAME = new DecimalFormat("##,###,##0.00");
        TextView minus_symbol = dialogInvoice.findViewById(R.id.minus_symbol);
        minus_symbol.setVisibility(View.GONE);
        TextView currency_symbol = dialogInvoice.findViewById(R.id.currency_symbol);
//        currency_symbol.setTextColor(Color.parseColor("#04BAEE") ); //#04BAEE   olor_SkyBlue_C70
        currency_symbol.setTextColor(Color.parseColor("#00A6E6") ); //  color_SkyBlue_C90
        priceLabel.setTextColor(Color.parseColor("#00A6E6") );

        //SINN 20181113 reference number
        TextView tv_REF_No = dialogInvoice.findViewById(R.id.tv_REF_No);
        tv_REF_No.setText( transTemp.getRefNo());

        //END SINN 20181113


//        traceNumberLabel.setText(getString(R.string.dialog_trace_number, traceNo));//อันเก่า ไม่มีข้อความเพิ่ม
        traceNumberLabel.setText(traceNo);//อันใหม่
//        priceLabel.setText(getString(R.string.dialog_void_amount, price)); //K.GAME 180912 เอาคำว่า จำนวนเงินออก
        tv_apprCode.setText(apprcode);//K.GAME 181002 new
        if (!ref1.isEmpty()) {
            tv_ref_1.setText(ref1);//K.GAME 181002 new
            linear_ref1.setVisibility(View.VISIBLE);
        }
        if (!ref2.isEmpty()) {
            tv_ref_2.setText(ref2);//K.GAME 181002 new
            linear_ref2.setVisibility(View.VISIBLE);
        }
        if (!ref3.isEmpty()) {
            tv_ref_3.setText(ref3);//K.GAME 181002 new
            linear_ref3.setVisibility(View.VISIBLE);
        }
//        priceLabel.setText(price);//K.GAME 180912 เอาคำว่า จำนวนเงินออก
        priceLabel.setText(decimalFormatShowGAME.format(Double.valueOf(price)));//K.GAME 180912 เอาคำว่า จำนวนเงินออก
        dialogTitleLabel.setText("ยกเลิกรายการ");
//        cardNumberLabel.setText(transTemp.getCardNo());//เลขบัตรประชาชน
//        cardNumberLabel.setText(CardPrefix.maskcard(Preference.getInstance(VoidActivity.this).getValueString(Preference.KEY_CARDMASK_ID).toString(), transTemp.getCardNo()));
        cardNumberLabel.setText(CardPrefix.maskviewcard(" ", transTemp.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555


        dialogInvoice.show();
        System.out.printf("utility:: transTemp.getHostTypeCard() 000000000000001 = %s \n", transTemp.getHostTypeCard());
        System.out.printf("utility:: typeHost 000000000000001 = %s \n", typeHost);

        // Paul_20180716 Start
        if (typeInterface != null) {
            if (!typeHost.equalsIgnoreCase("TMS")) {
                dialogWaiting.show();
                dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
                dialogInvoice.dismiss();
                //Paul_20180708
                //                    if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) {
                //                        setDataVoidHealthCare();
                //                    } else {
                setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting
                if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(transTemp);
                } else {
                    cardManager.setDataVoid(transTemp);
                }
                //                    }
                //                    cardManager.insertReversalVoidTransaction(transTemp);
            } else if (typeHost.equalsIgnoreCase("TMS")) {
                System.out.printf("utility:: 0000000000000000000000000000000000002 = %s \n", approvalcode);
// Paul_20180730 Start
                if (approvalcode == null) {
//                    dialogApprCode.show();
                    Utility.customDialogAlertAuto(VoidActivity.this, "Approval code ไม่ถูกต้อง");
                    TellToPosError("13");
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            Utility.customDialogAlertAutoClear();
                            Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                } else
// Paul_20180730 End
//20180724 SINN TMS VOID check Approval code
                    if (!approvalcode.equalsIgnoreCase(transTemp.getApprvCode())) {
                        Utility.customDialogAlertAuto(VoidActivity.this, "Approval code ไม่ถูกต้อง");
                        TellToPosError("13");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else {
                        dialogWaiting.show();
                        dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
                        dialogInvoice.dismiss();
                        setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting
                        cardManager.setDataVoid(transTemp);
                    }
            }
        }
// Paul_20180716 End

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWaiting.show();
                dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
                dialogTrace.dismiss(); ////SINN 20181106 close all previous winnumberPricedows.
                dialogInvoice.dismiss();
                System.out.printf("utility:: transTemp.getHostTypeCard() 000000000000002 = %s \n", transTemp.getHostTypeCard());
                setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting


                if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(transTemp);
                } else {
                    cardManager.setDataVoid(transTemp);

                }
//                if (!typeHost.equalsIgnoreCase("TMS")) {
//                    dialogWaiting.show();
//                    dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
//                    dialogTrace.dismiss(); ////SINN 20181106 close all previous winnumberPricedows.
//                    dialogInvoice.dismiss();
//                    System.out.printf("utility:: transTemp.getHostTypeCard() 000000000000002 = %s \n", transTemp.getHostTypeCard());
//                    setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting
//
//
//                    if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
//                        setPrint_off(transTemp);
//                    } else {
//                        cardManager.setDataVoid(transTemp);
//
//                    }
////                    cardManager.insertReversalVoidTransaction(transTemp);
//                } else if (typeHost.equalsIgnoreCase("TMS")) {
////                    dialogApprCode.show();//K.GAME 181017 Close
//                    dialogcustomDialogApprCode_new.show();////K.GAME 181017 New Dialog Appr.Code PIN
//                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeInterface == null) {
//                    dialogInvoice.dismiss();
                    finish();
                } else {
                    finish();
                }
            }
        });

    }

    private void customDialogInvoice(String traceNo, String price) {
        dialogInvoice = new Dialog(this);
        dialogInvoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInvoice.setContentView(R.layout.dialog_custom_invoice);
        dialogInvoice.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogInvoice.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView traceNumberLabel = dialogInvoice.findViewById(R.id.traceNumberLabel);
        TextView priceLabel = dialogInvoice.findViewById(R.id.priceLabel);
        Button saveBtn = dialogInvoice.findViewById(R.id.saveBtn);
        Button cancelBtn = dialogInvoice.findViewById(R.id.cancelBtn);
        traceNumberLabel.setText(getString(R.string.dialog_trace_number, traceNo));
        priceLabel.setText(getString(R.string.dialog_void_amount, price));
        dialogInvoice.show();
        System.out.printf("utility:: transTemp.getHostTypeCard() 000000000000001 = %s \n", transTemp.getHostTypeCard());
        System.out.printf("utility:: typeHost 000000000000001 = %s \n", typeHost);

        // Paul_20180716 Start
        if (typeInterface != null) {
            if (!typeHost.equalsIgnoreCase("TMS")) {
                dialogWaiting.show();
                dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
                dialogInvoice.dismiss();
                //Paul_20180708
                //                    if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC")) {
                //                        setDataVoidHealthCare();
                //                    } else {
                setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting
                if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(transTemp);
                } else {
                    cardManager.setDataVoid(transTemp);
                }
                //                    }
                //                    cardManager.insertReversalVoidTransaction(transTemp);
            } else if (typeHost.equalsIgnoreCase("TMS")) {
                System.out.printf("utility:: 0000000000000000000000000000000000002 = %s \n", approvalcode);
// Paul_20180730 Start
                if (approvalcode == null) {
//                    dialogApprCode.show();
                    Utility.customDialogAlertAuto(VoidActivity.this, "Approval code ไม่ถูกต้อง");
                    TellToPosError("13");
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            Utility.customDialogAlertAutoClear();
                            Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                } else
// Paul_20180730 End
//20180724 SINN TMS VOID check Approval code
                    if (!approvalcode.equalsIgnoreCase(transTemp.getApprvCode())) {
                        Utility.customDialogAlertAuto(VoidActivity.this, "Approval code ไม่ถูกต้อง");
                        TellToPosError("13");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else {
                        dialogWaiting.show();
                        dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
                        dialogInvoice.dismiss();
                        setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting
                        cardManager.setDataVoid(transTemp);
                    }
            }
        }
// Paul_20180716 End

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Paul_20190226
                dialogWaiting.show();
                dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
                dialogInvoice.dismiss();
                System.out.printf("utility:: transTemp.getHostTypeCard() 000000000000002 = %s \n", transTemp.getHostTypeCard());
                setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting
                if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
                    setPrint_off(transTemp);
                } else {
//                        cardManager.SetVoidtransTemp(transTemp);
                    cardManager.setDataVoid(transTemp);
                }
//                if (!typeHost.equalsIgnoreCase("TMS")) {
//                    dialogWaiting.show();
//                    dialogWaiting.setCancelable(false);  //20180725 SINN KTB do not cancel void waiting
//                    dialogInvoice.dismiss();
//                    System.out.printf("utility:: transTemp.getHostTypeCard() 000000000000002 = %s \n", transTemp.getHostTypeCard());
//                    setResponseCode();  ////20180725  SINN VOID HOST reject EDC still waiting
//                    if (transTemp.getHostTypeCard().equalsIgnoreCase("GHC") && transTemp.getGhcoffFlg().equalsIgnoreCase("Y")) {
//                        setPrint_off(transTemp);
//                    } else {
////                        cardManager.SetVoidtransTemp(transTemp);
//                        cardManager.setDataVoid(transTemp);
//                    }
////                    cardManager.insertReversalVoidTransaction(transTemp);
//                } else if (typeHost.equalsIgnoreCase("TMS")) {
////                    dialogApprCode.show();//K.GAME 181017 Close
//                    dialogcustomDialogApprCode_new.show();////K.GAME 181017 New Dialog Appr.Code PIN
//                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeInterface == null) {
                    dialogInvoice.dismiss();
                } else {
                    finish();
                }
            }
        });

    }

//Jeff20181019
//    private void searchDataTransTemp(String traceNo) {
//        RealmResults<TransTemp> transTemp;
//        String traceNoAddZero = "";//ถ้าพิมพ์น้อยกว่า 6 ตัวจะติด 0 ข้างหน้า
//        if (!traceNo.isEmpty()) {
//            if (traceNo.length() < 6) {
//                for (int i = traceNo.length(); i < 6; i++) {
//                    traceNoAddZero += "0";
//
//                }
//
//            }
//            traceNoAddZero += traceNo;
//            traceBox_new.setText(traceNoAddZero);//K.GAME 180905 Add change EdidText dialog
//            Log.d(TAG, "utility:: searchDataTransTemp: " + traceNoAddZero);
//
//            transTemp = realm.where(TransTemp.class).equalTo("ecr", traceNoAddZero).findAll();
//            Log.d(TAG, "searchDataTransTemp: " + transTemp);
//            if (transTemp.size() > 0) {
//                voidAdapter.clear();
//                if (transTempList == null) {
//                    transTempList = new ArrayList<>();
//                } else {
//                    transTempList.clear();
//                }
//                transTempList.addAll(transTemp);
//                voidAdapter.setItem(transTempList);
//                voidAdapter.notifyDataSetChanged();
//            }
//
//
//        } else {
//            setVoidList();
//        }
//
//    }

    private void setVoidList() {
        if (recyclerViewVoid.getAdapter() == null) {
            voidAdapter = new VoidAdapter(this);
            recyclerViewVoid.setAdapter(voidAdapter);
            voidAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    // 그냥 눌렀을때

                    System.out.printf("utility:: VoidActivity LLLLLLLLLLLLLL position = %d \n", position);
                    transTemp = voidAdapter.getItem(position);
                    System.out.printf("utility:: VoidActivity LLLLLLLLLLLLLL transTemp.getTraceNo() = %s \n", transTemp.getTraceNo());
                    if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
                        typeHost = transTemp.getHostTypeCard();
                        customDialogPin(transTemp.getEcr(), transTemp.getAmount(), transTemp);
                    }
//                    else {
//                        Utility.customDialogAlert(VoidActivity.this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
//                            @Override
//                            public void onClickImage(Dialog dialog) {
//                                dialog.dismiss();
//                            }
//                        });
//                    }
//SINN 20181013  already void but not have db in transactions
                    else if (transTemp.getVoidFlag().equalsIgnoreCase("Y")) {
                        Utility.customDialogAlert(VoidActivity.this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Utility.customDialogAlert(VoidActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
        } else {
            voidAdapter.clear();
        }
        if (transTempList == null) {
            transTempList = new ArrayList<>();
        } else {
            transTempList.clear();
        }
        transTempList.addAll(realm.copyFromRealm(realm.where(TransTemp.class).findAll()));
        voidAdapter.setItem(transTempList);
        voidAdapter.notifyDataSetChanged();

        if (typeInterface != null) {
            transTemp = voidAdapter.getItemWithErcInvoid(invoiceId);
            if (transTemp != null) {
// Paul_20180704
//                System.out.printf("utility:: setVoidList 00003 = %s \n",transTemp.getVoidFlag());
                if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
//                    System.out.printf("utility:: setVoidList 00004 \n");
                    typeHost = transTemp.getHostTypeCard();
                    customDialogInvoice(transTemp.getEcr(), transTemp.getAmount());
                } else {
                    if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                        Utility.customDialogAlertAuto(VoidActivity.this, "รายการนี้ยกเลิกแล้ว");
// SINN 20181013  already void but not have db in transactions
                        if (transTemp.getVoidFlag().equalsIgnoreCase("Y"))
                            Utility.customDialogAlertAuto(VoidActivity.this, "รายการนี้ยกเลิกแล้ว");
                        else
                            Utility.customDialogAlertAuto(VoidActivity.this, "ไม่มีข้อมูล");
// SINN 20181013  already void but not have db in transactions

                        TerToPosNoMatching();
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
                        Utility.customDialogAlert(this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                }
            } else {
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                    Utility.customDialogAlertAuto(VoidActivity.this, "รายการนี้ยกเลิกแล้ว");
                    Utility.customDialogAlertAuto(VoidActivity.this, "ไม่มีข้อมูล");  // SINN 20181013  already void but not have db in transactions
                    TerToPosNoMatching();
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            Utility.customDialogAlertAutoClear();
                            Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                } else
                    Utility.customDialogAlert(this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            }
        }

    }

    public void TerToPosNoMatching() {
        // Paul_20181024 POSLINK Message Change
//        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("47"));   // Response Message
//        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode, "47");
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("12"));   // Response Message
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode, "12");    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogWaiting);
        //END K.GAME 180831 chang waitting UI
    }

    private void customDialogTrace_new() {//K.GAME 180905 new dialog
        dialogTrace = new Dialog(this);
        dialogTrace.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTrace.setContentView(R.layout.dialog_custom_trace_new);
        dialogTrace.setCancelable(true);
        dialogTrace.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogTrace.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTrace.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        traceBox_new = dialogTrace.findViewById(R.id.traceBox_new);
        final TextView inputTextLabel = dialogTrace.findViewById(R.id.inputTextLabel);
        Button okBtn_trace = dialogTrace.findViewById(R.id.okBtn_trace);
        Button cancelBtn_trace = dialogTrace.findViewById(R.id.cancelBtn_trace);

        dialogTrace.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//K.GAME 180905 ปิดคีร์บอร์ด
        traceBox_new.setShowSoftInputOnFocus(false);//K.GAME 180905 ทำให้กดคีร์บอร์ดที่ Edit text ไม่ได้

        //K.GAME ปุ่มกดบน Layout

        oneClickFrameLayout = dialogTrace.findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = dialogTrace.findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = dialogTrace.findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = dialogTrace.findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = dialogTrace.findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = dialogTrace.findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = dialogTrace.findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = dialogTrace.findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = dialogTrace.findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = dialogTrace.findViewById(R.id.zeroClickFrameLayout);
        deleteClickFrameLayout = dialogTrace.findViewById(R.id.deleteClickFrameLayout);
        numberLinearLayout = dialogTrace.findViewById(R.id.numberLinearLayout_test);

//K.GAME 181004 back button กดกลับ dialog
        dialogTrace.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(dialogTrace != null) {
                    dialogTrace.dismiss();
                    dialogTrace = null;   // Paul_20181106 Read Worning
                }
                finish();//K.GAME 180905 แก้จุดนี้เพื่อดูRecyclerView
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 1801004 back button

        if (typeInterface != null) {

        } else {
            View view = null;
            for (int i = 0; i < numberLinearLayout.getChildCount(); i++) {
                view = numberLinearLayout.getChildAt(i);
                view.setEnabled(true);

            }
            //  clickCal_trace(view);
        }


        oneClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        twoClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        threeClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        fourClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        fiveClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        sixClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        sevenClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        eightClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        nineClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        zeroClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });
        deleteClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_trace(v);
            }
        });


//END K.GAME ปุ่มกดบน Layout

        okBtn_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //K.GAME 180905 Big change
//                searchDataTransTemp(traceBox_new.getText().toString());
                Log.d("1919", "Trace: " + traceBox_new.getText().toString());
System.out.printf("utility:: %s customDialogTrace_new 000001 \n",TAG);
                invoice = traceBox_new.getText().toString();
                invoice = checkLength(invoice, 6);

                checkTransdataDB();
                checkAlipaydataDB();

                if(!flag_check){
                    Utility.customDialogAlert(VoidActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {//K.GAME 181011
                        @Override
                        public void onClickImage(Dialog dialog) {
                            Log.d("1919", "ไม่เจอ Trace No. หรือ รายการนี้ถูกยกเลิกไปแล้ว");
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        cancelBtn_trace.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if(dialogTrace != null) {
                    dialogTrace.dismiss();
                    dialogTrace = null;   // Paul_20181106 Read Worning
                }
                finish();//K.GAME 180905 แก้จุดนี้เพื่อดูRecyclerView
            }
        });

        if(dialogTrace != null) {     // Paul_20181106 Read Worning
            dialogTrace.show();
        }
    }//K.GAME 180905 new dialog

    private void checkAlipaydataDB() {
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            // Paul_20190117 k.phadet said void qr none it reprint
            qrCode = realm.where(QrCode.class).equalTo("trace", invoice).notEqualTo("hostTypeCard", "QR").findFirst();  // Paul_20190117

            if (qrCode != null) {
                if (qrCode.getVoidFlag().equalsIgnoreCase("N")) {
                    flag_check = true;
                    Intent service = new Intent(VoidActivity.this, AliVoidActivity.class);
                    service.putExtra("INVOICE", invoice);
                    service.putExtra("TYPE", AliConfig.Void);
                    startActivity(service);
                    finish();
                } else {
                    Utility.customDialogAlert(VoidActivity.this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            Log.d("1919", "กดยกเลิก");
                            dialog.dismiss();
                        }
                    });
                }
            }
        } finally {
            if (realm != null) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
    }

    private void checkTransdataDB() {

        transTemp = voidAdapter.getItemWithErcInvoid(traceBox_new.getText().toString());
        if (transTemp != null) {
            if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
                flag_check = true;
                typeHost = transTemp.getHostTypeCard();
                if(typeHost.equalsIgnoreCase("TMS")) {        // Paul_20190226 TMS befor Approval and than confirm display
//                    dialogApprCode.show();//K.GAME 181017 Close
                    dialogcustomDialogApprCode_new.show();////K.GAME 181017 New Dialog Appr.Code PIN
                }
                else
                {
                    customDialogInvoice_new( transTemp.getEcr(), transTemp.getAmount(), transTemp.getApprvCode(), transTemp.getRef1(), transTemp.getRef2(), transTemp.getRef3() );
                }
                Log.d(TAG, "Success: ");
                Log.d("1919", "Success: ");
                if(dialogPin != null) {
                    dialogPin.dismiss();
                    dialogPin = null;   // Paul_20181106 Read Worning
                }
            } else {
                Utility.customDialogAlert(VoidActivity.this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        Log.d("1919", "กดยกเลิก");
                        dialog.dismiss();
                    }
                });
            }
        }
//        else {
//            //  Utility.customDialogAlert(VoidActivity.this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
//            Utility.customDialogAlert(VoidActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {//K.GAME 181011
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    Log.d("1919", "ไม่เจอ Trace No. หรือ รายการนี้ถูกยกเลิกไปแล้ว");
//                    dialog.dismiss();
//                }
//            });
//        }

//                customDialogInvoice(transTemp.getEcr(), transTemp.getAmount());
//                if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
//                    typeHost = transTemp.getHostTypeCard();
//                    customDialogPin(transTemp.getEcr(), transTemp.getAmount(), transTemp);
//                } else {
//                    Utility.customDialogAlert(VoidActivity.this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                        }
//                    });
//                }


//                customDialogInvoice(transTemp.getEcr(), transTemp.getAmount());
//                Log.d(TAG, "onTextChanged: " + transTemp.getApprvCode());
        //END K.GAME 180905 Big change

    }

    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        }
    }

    private void clickCal_trace(View v) {

        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 6)//K.GAME 181003 6 digit trace
                numberPrice += "0";
        } else if (v == deleteClickFrameLayout) {
            if (!traceBox_new.getText().toString().equalsIgnoreCase("0.00")) {
                Log.d(TAG, "clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + numberPrice);
                    numberPrice = "";
                    traceBox_new.setText("0.00");
                    if (typeInterface != null)
//                        traceBox_new.setText(numberPrice);
                        traceBox_new.setText(Utility.calNumTraceNo(numberPrice));   //20181021 SINN invalid void
//                    if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
//                        priceLabel.setText(amountInterface);
//                        userInputDialogEt.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        if (numberPrice.equalsIgnoreCase("") || numberPrice == null) {
                            Log.d(TAG, "clickCal: if");
                            numberPrice = "";
                            traceBox_new.setText("0.00");
                            if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
//                                traceBox_new.setText(numberPrice);
                                traceBox_new.setText(Utility.calNumTraceNo(numberPrice));   //20181021 SINN invalid void
//                                priceLabel.setText(amountInterface);
//                                priceLabel.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                        } else {
                            Log.d(TAG, "clickCal: else");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        Log.d(TAG, "numberPrice:" + numberPrice);
//        traceBox_new.setText(numberPrice);
        traceBox_new.setText(Utility.calNumTraceNo(numberPrice));   //20181021 SINN invalid void
    }

    private void clickCal(View v) {

        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4) //K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "0";
        } else if (v == deleteClickFrameLayout) {
            if (!pinBox_new.getText().toString().equalsIgnoreCase("0.00")) {
                Log.d(TAG, "clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + numberPrice);
                    numberPrice = "";
                    pinBox_new.setText("0.00");
                    if (typeInterface != null)
                        pinBox_new.setText(numberPrice);
//                    if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
//                        priceLabel.setText(amountInterface);
//                        userInputDialogEt.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        if (numberPrice.equalsIgnoreCase("") || numberPrice == null) {
                            Log.d(TAG, "clickCal: if");
                            numberPrice = "";
                            pinBox_new.setText("0.00");
                            if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                                pinBox_new.setText(numberPrice);
//                                priceLabel.setText(amountInterface);
//                                priceLabel.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                        } else {
                            Log.d(TAG, "clickCal: else");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        Log.d(TAG, "numberPrice:" + numberPrice);
        pinBox_new.setText(numberPrice);
    }

    private void customDialogPin_new() { //K.GAME 180905 new dialog
        dialogPin = new Dialog(this);
        dialogPin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPin.setContentView(R.layout.dialog_custom_pin);
        dialogPin.setCancelable(false);
        dialogPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPin.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        pinBox_new = dialogPin.findViewById(R.id.pinBox);
        final TextView inputTextLabel = dialogPin.findViewById(R.id.inputTextLabel);
        dialogPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//K.GAME 180905 ปิดคีร์บอร์ด
        pinBox_new.setShowSoftInputOnFocus(false);//K.GAME 180905 ทำให้กดคีร์บอร์ดที่ Edit text ไม่ได้
        final ImageView img_pin01 = dialogPin.findViewById(R.id.img_pin01);
        final ImageView img_pin02 = dialogPin.findViewById(R.id.img_pin02);
        final ImageView img_pin03 = dialogPin.findViewById(R.id.img_pin03);
        final ImageView img_pin04 = dialogPin.findViewById(R.id.img_pin04);
        Button cancelBtn = dialogPin.findViewById(R.id.cancelBtn);
        Button okBtn = dialogPin.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VoidActivity.this, "PIN ไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//เปิดเพื่อดูRrcyclerView
            }
        });

        //K.GAME 181022 back button
        dialogPin.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                finish();
                // Paul_20181107
                if(dialogPin != null) {
                    dialogPin.dismiss();
                    dialogPin = null;   // Paul_20181106 Read Worning
                }
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 181022 back button

//K.GAME ปุ่มกดบน Layout
        oneClickFrameLayout = dialogPin.findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = dialogPin.findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = dialogPin.findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = dialogPin.findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = dialogPin.findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = dialogPin.findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = dialogPin.findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = dialogPin.findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = dialogPin.findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = dialogPin.findViewById(R.id.zeroClickFrameLayout);
        deleteClickFrameLayout = dialogPin.findViewById(R.id.deleteClickFrameLayout);
        numberLinearLayout = dialogPin.findViewById(R.id.numberLinearLayout_test);

        if (typeInterface != null) {

        } else {
            View view = null;
            for (int i = 0; i < numberLinearLayout.getChildCount(); i++) {
                view = numberLinearLayout.getChildAt(i);
                view.setEnabled(true);

            }
            //  clickCal(view);
        }


        oneClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        twoClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        threeClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        fourClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        fiveClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        sixClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        sevenClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        eightClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        nineClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        zeroClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        deleteClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });


//END K.GAME ปุ่มกดบน Layout

        System.out.printf("utility:: customDialogPin_new 0001 \n");
        pinBox_new.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.printf("utility:: customDialogPin 0002 \n");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.printf("utility:: customDialogPin 0003 \n");
                if (s.length() == 0) {
                    img_pin01.setVisibility(View.INVISIBLE);
                    img_pin02.setVisibility(View.INVISIBLE);
                    img_pin03.setVisibility(View.INVISIBLE);
                    img_pin04.setVisibility(View.INVISIBLE);
                }
                if (s.length() == 1) {
                    img_pin01.setVisibility(View.VISIBLE);
                    img_pin02.setVisibility(View.INVISIBLE);
                    img_pin03.setVisibility(View.INVISIBLE);
                    img_pin04.setVisibility(View.INVISIBLE);
                }
                if (s.length() == 2) {
                    img_pin01.setVisibility(View.VISIBLE);
                    img_pin02.setVisibility(View.VISIBLE);
                    img_pin03.setVisibility(View.INVISIBLE);
                    img_pin04.setVisibility(View.INVISIBLE);
                }
                if (s.length() == 3) {
                    img_pin01.setVisibility(View.VISIBLE);
                    img_pin02.setVisibility(View.VISIBLE);
                    img_pin03.setVisibility(View.VISIBLE);
                    img_pin04.setVisibility(View.INVISIBLE);
                }
                if (s.length() == 4) {
                    img_pin01.setVisibility(View.VISIBLE);
                    img_pin02.setVisibility(View.VISIBLE);
                    img_pin03.setVisibility(View.VISIBLE);
                    img_pin04.setVisibility(View.VISIBLE);
                }
                if (s.length() == 4) {
                    inputTextLabel.setVisibility(View.INVISIBLE);
                    String keyPin = Preference.getInstance(VoidActivity.this).getValueString(Preference.KEY_PIN);
                    if (s.toString().equalsIgnoreCase(keyPin)) {

                        customDialogTrace_new(); // dialog trace
                        numberPrice = "";//K.GAME 180905 แก้บัค ตัวเลขโผล่มา Trace
                        dialogPin.dismiss();   //SINN 20181106 close all previous windows.
//                        Toast.makeText(VoidActivity.this, "รหัสผ่านถูกต้อง", Toast.LENGTH_SHORT).show();

                        //                        dialogPin.dismiss();
//                        customDialogInvoice(transTemp.getEcr(), transTemp.getAmount());
//                        Log.d(TAG, "onTextChanged: " + transTemp.getApprvCode());


                    } else {
                        inputTextLabel.setVisibility(View.VISIBLE);
//                        inputTextLabel.setText("PIN ไม่ถูกต้อง");
                        inputTextLabel.setText("รหัสผ่านไม่ถูกต้อง");  //20181106 SINN change wording for wrong PIN.
                    }
                } else {
                    inputTextLabel.setVisibility(View.VISIBLE);
                    inputTextLabel.setText("ระบุรหัสผ่าน");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.printf("utility:: customDialogPin 0004 \n");

            }
        });
        if(dialogPin != null) {     // Paul_20181106 Read Worning
            dialogPin.show();
        }
    }

    private void customDialogPin(String traceNo, String amount, final TransTemp transTemp) {
        dialogPinOld = new Dialog(this);
        dialogPinOld.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPinOld.setContentView(R.layout.dialog_custom_pin);
//        dialogPin.setCancelable(false);
        dialogPinOld.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogPinOld.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPinOld.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        EditText pinBox = dialogPinOld.findViewById(R.id.pinBox);
        final TextView inputTextLabel = dialogPinOld.findViewById(R.id.inputTextLabel);
        System.out.printf("utility:: %s customDialogPin 0001 \n",TAG);
        pinBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.printf("utility:: customDialogPin 0002 \n");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.printf("utility:: customDialogPin 0003 \n");
                if (s.length() == 4) {
                    inputTextLabel.setVisibility(View.INVISIBLE);
                    String keyPin = Preference.getInstance(VoidActivity.this).getValueString(Preference.KEY_PIN);
                    if (s.toString().equalsIgnoreCase(keyPin)) {
                        if(dialogPinOld != null) {
                            dialogPinOld.dismiss();
                            dialogPinOld = null;   // Paul_20181106 Read Worning
                        }
                        customDialogInvoice(transTemp.getEcr(), transTemp.getAmount());
                        Log.d(TAG, "onTextChanged: " + transTemp.getApprvCode());
                    } else {
                        inputTextLabel.setVisibility(View.VISIBLE);
//                        inputTextLabel.setText("PIN ไม่ถูกต้อง");
                        inputTextLabel.setText("รหัสผ่านไม่ถูกต้อง");  //20181106 SINN change wording for wrong PIN.
                    }
                } else {
                    inputTextLabel.setVisibility(View.VISIBLE);
                    inputTextLabel.setText("ระบุรหัสผ่าน");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.printf("utility:: customDialogPin 0004 \n");

            }
        });
        if(dialogPinOld != null) {     // Paul_20181106 Read Worning
            dialogPinOld.show();
        }
    }

    private void checkNumberApprCode() {
        if (number_ApprCode.equalsIgnoreCase("")) {
            number_ApprCode = "";
        }
    }

    private void clickCal_ApprCode(View v) {//K.GAME 181017 New Dialog APPR.CODE VOID

        if (v == oneClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "1";
        } else if (v == twoClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "2";
        } else if (v == threeClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "3";
        } else if (v == fourClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "4";
        } else if (v == fiveClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "5";
        } else if (v == sixClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "6";
        } else if (v == sevenClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "7";
        } else if (v == eightClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "8";
        } else if (v == nineClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "9";
        } else if (v == zeroClickFrameLayout_ApprCode) {
            checkNumberApprCode();
            if (number_ApprCode.length() < 9)//K.GAME 181017 9 digit APPR.CODE
                number_ApprCode += "0";
        } else if (v == deleteClickFrameLayout_ApprCode) {
            if (!apprcodeBox_new.getText().toString().equalsIgnoreCase("")) {
                Log.d(TAG, "clickCal y: " + number_ApprCode);
                if (number_ApprCode.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + number_ApprCode);
                    number_ApprCode = "";
                    apprcodeBox_new.setText("");
                    if (typeInterface != null)
                        apprcodeBox_new.setText(number_ApprCode);
//                    if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
//                        priceLabel.setText(amountInterface);
//                        userInputDialogEt.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + number_ApprCode);
                        number_ApprCode = number_ApprCode.substring(0, number_ApprCode.length() - 1);
                        Log.d(TAG, "clickCal 1: " + number_ApprCode);
                        if (number_ApprCode.equalsIgnoreCase("") || number_ApprCode == null) {
                            Log.d(TAG, "clickCal: if");
                            number_ApprCode = "";
                            apprcodeBox_new.setText("");
                            if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                                apprcodeBox_new.setText(number_ApprCode);
//                                priceLabel.setText(amountInterface);
//                                priceLabel.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                        } else {
                            Log.d(TAG, "clickCal: else");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        Log.d(TAG, "number_ApprCode:" + number_ApprCode);
        apprcodeBox_new.setText(number_ApprCode);
    }

    private void customDialogApprCode_new() {//K.GAME 181017 New Dialog
        dialogcustomDialogApprCode_new = new Dialog(this);
        dialogcustomDialogApprCode_new.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogcustomDialogApprCode_new.setContentView(R.layout.dialog_custom_apprcode_new);
        dialogcustomDialogApprCode_new.setCancelable(true);
        dialogcustomDialogApprCode_new.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogcustomDialogApprCode_new.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogcustomDialogApprCode_new.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        apprcodeBox_new = dialogcustomDialogApprCode_new.findViewById(R.id.apprcodeBox_new);
        final TextView inputTextLabel = dialogcustomDialogApprCode_new.findViewById(R.id.inputTextLabel);
        Button okBtn_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.okBtn_ApprCode);
        Button cancelBtn_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.cancelBtn_ApprCode);

        dialogcustomDialogApprCode_new.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//K.GAME 180905 ปิดคีร์บอร์ด
        apprcodeBox_new.setShowSoftInputOnFocus(false);//K.GAME 181017 ทำให้กดคีร์บอร์ดที่ Edit text ไม่ได้

        //K.GAME ปุ่มกดบน Layout

        oneClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.zeroClickFrameLayout);
        deleteClickFrameLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.deleteClickFrameLayout);
        numberLinearLayout_ApprCode = dialogcustomDialogApprCode_new.findViewById(R.id.numberLinearLayout_test);


//K.GAME 181004 back button กดกลับ dialog
        dialogcustomDialogApprCode_new.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (dialogcustomDialogApprCode_new.isShowing())
                    dialogcustomDialogApprCode_new.dismiss();
                finish();//K.GAME 180905 แก้จุดนี้เพื่อดูRecyclerView
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 1801004 back button

        if (typeInterface != null) {

        } else {
            View view = null;
            for (int i = 0; i < numberLinearLayout_ApprCode.getChildCount(); i++) {
                view = numberLinearLayout_ApprCode.getChildAt(i);
                view.setEnabled(true);

            }
            //  clickCal_trace(view);
        }


        oneClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        twoClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        threeClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        fourClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        fiveClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        sixClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        sevenClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        eightClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        nineClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        zeroClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });
        deleteClickFrameLayout_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal_ApprCode(v);
            }
        });


//END K.GAME ปุ่มกดบน Layout

        okBtn_ApprCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apprcodeBox_new.getText().toString().equalsIgnoreCase(transTemp.getApprvCode())) {
                    System.out.printf("utility:: %s customDialogApprCode_new onClick\n",TAG);
// Paul_20190226
                    if (dialogcustomDialogApprCode_new.isShowing())
                        dialogcustomDialogApprCode_new.dismiss();
                    if(dialogInvoice != null && dialogInvoice.isShowing())
                        dialogInvoice.dismiss();
                    flag_check = true;
                    typeHost = transTemp.getHostTypeCard();
                    customDialogInvoice_new( transTemp.getEcr(), transTemp.getAmount(), transTemp.getApprvCode(), transTemp.getRef1(), transTemp.getRef2(), transTemp.getRef3() );


//                    dialogWaiting.show();
//                    if(dialogInvoice != null && dialogInvoice.isShowing())
//                        dialogInvoice.dismiss();
//                    cardManager.setDataVoid(transTemp);
//                    cardManager.insertReversalVoidTransaction(transTemp);
                } else {
// Paul_20180730 Start
                    if (typeInterface != null) {
                        Utility.customDialogAlertAuto(VoidActivity.this, "ApprCode ไม่ตรงกัน ");
                        TellToPosError("13");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
// Paul_20180730 end
                        Utility.customDialogAlert(VoidActivity.this, "ApprCode ไม่ตรงกัน ", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                }
            }
        });
        cancelBtn_ApprCode.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                System.out.printf("utility:: %s cancelBtn_ApprCode ok\n", TAG);
                number_ApprCode = "";
                apprcodeBox_new.setText("");
                //SINN 20181214 click Cancel KTB order to
                if (dialogcustomDialogApprCode_new.isShowing())
                    dialogcustomDialogApprCode_new.dismiss();
                cardManager.abortPBOCProcess();
                finish();
            }
        });

    }

    private void customDialogApprCode() {
        dialogApprCode = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180924
        View view = dialogApprCode.getLayoutInflater().inflate(R.layout.dialog_custom_appr_code, null);//K.GAME 180924
        dialogApprCode.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180924
        dialogApprCode.setContentView(view);//K.GAME 180924
        dialogApprCode.setCancelable(false);//K.GAME 180924

//        dialogApprCode = new Dialog(this);
//        dialogApprCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogApprCode.setContentView(R.layout.dialog_custom_appr_code);
//        dialogApprCode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogApprCode.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialogApprCode.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText apprCodeBox = dialogApprCode.findViewById(R.id.apprCodeBox);
        Button okBtn = dialogApprCode.findViewById(R.id.okBtn);
        Button cancelBtn = dialogApprCode.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apprCodeBox.getText().toString().equalsIgnoreCase(transTemp.getApprvCode())) {
                    System.out.printf("utility:: %s customDialogApprCode onClick\n",TAG);
                    dialogWaiting.show();
                    dialogInvoice.dismiss();
                    cardManager.setDataVoid(transTemp);
//                    cardManager.insertReversalVoidTransaction(transTemp);
                } else {
// Paul_20180730 Start
                    if (typeInterface != null) {
                        Utility.customDialogAlertAuto(VoidActivity.this, "ApprCode ไม่ตรงกัน ");
                        TellToPosError("13");
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();
                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
                    } else
// Paul_20180730 end
                        Utility.customDialogAlert(VoidActivity.this, "ApprCode ไม่ตรงกัน ", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogApprCode.dismiss();
                if (dialogcustomDialogApprCode_new.isShowing())
                    dialogcustomDialogApprCode_new.dismiss();//K.GAME 181024
            }
        });

    }

    /**
     * Zone Update Data And Insert DB
     */


    // Paul_20180705
    private void removeReversalHealthCare() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(ReversalHealthCare.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: removeReversal ");
            }
        });

/*
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(ReversalHealthCare.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: removeReversal ");
            }
        });
*/
    }

    private void updateTransactionVoidHealthCare(final String[] data) {
        Log.d(TAG, "updateTransactionVoid: " + transTemp.getTraceNo());
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TransTemp transTemps = realm.where(TransTemp.class).equalTo("id", transTemp.getId()).findFirst();
                if (transTemps != null) {
                    transTemps.setVoidFlag("Y");
                    transTemps.setTraceNo(mBlockDataSend[11 - 1]);
                    realm.copyToRealmOrUpdate(transTemps);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
// Paul_20180718
//                Toast.makeText(VoidActivity.this, "Success", Toast.LENGTH_SHORT).show();
                showDataAndSaveDatabaseHealthCare(data);
// Paul_20180705
//                Intent intent = new Intent(VoidActivity.this, SlipTemplateHealthCareActivity.class);
//                intent.putExtra(CalculatePriceActivity.KEY_CALCUATE_ID, id);
//                intent.putExtra(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID, CalculatePriceActivity.TypeVoid);
//                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
// Paul_20180718
//                Toast.makeText(VoidActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Paul_20180705
    String invoiceNumber;
    String TERMINAL_ID;
    String MERCHANT_NUMBER;
    String statusSale;

    // Paul_20180705
    private void showDataAndSaveDatabaseHealthCare(final String[] data) {
        final Date dateTime = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy");
        String year = new SimpleDateFormat("yy").format(dateTime);  // Paul_20180706
        final String second = data[12 - 1].substring(4, 6);
        final String minute = data[12 - 1].substring(2, 4);
        final String hour = data[12 - 1].substring(0, 2);
        final String mount = data[13 - 1].substring(0, 2);
        final String date = data[13 - 1].substring(2, 4);
//        removeReversalHealthCare();
        statusSale = transTemp.getTypeSale();
        if (typeInterface == null) {
            dataSendSuccess(data, dateTime, dateFormat, second, minute, hour, mount, date);
        } else {
            invoiceNumber = CardPrefix.getInvoice(VoidActivity.this, "GHC");
            TERMINAL_ID = CardPrefix.getTerminalId(VoidActivity.this, "GHC");
            MERCHANT_NUMBER = CardPrefix.getMerchantId(VoidActivity.this, "GHC");
            posInterfaceActivity.PosInterfaceWriteField("01", BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9));   // Approval Code
//            posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface( BlockCalculateUtil.hexToString(data[39 - 1])));   // Response Message
            posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(BlockCalculateUtil.hexToString(data[39 - 1])));   // Response Message

            posInterfaceActivity.PosInterfaceWriteField("65", invoiceNumber);   // Invoice Number
            posInterfaceActivity.PosInterfaceWriteField("16", TERMINAL_ID);   // Terminal ID
            posInterfaceActivity.PosInterfaceWriteField("D1", MERCHANT_NUMBER);   // Merchant ID
            posInterfaceActivity.PosInterfaceWriteField("03", year + mount + date);   // Date YYMMDD
            posInterfaceActivity.PosInterfaceWriteField("04", hour + minute + second);   // Time HHMMSS
/*
            if (statusSale.substring(1).equalsIgnoreCase("1")) {
                posInterfaceActivity.PosInterfaceWriteField("30", cardId.getIdCard().replace(" ",""));   // Card No
            } else if (statusSale.substring(1).equalsIgnoreCase("2")) {
                posInterfaceActivity.PosInterfaceWriteField("30", idCardCd);   // Card No
            } else {
                posInterfaceActivity.PosInterfaceWriteField("30", idForeigner);   // Card No
            }
*/


            posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode, BlockCalculateUtil.hexToString(data[39 - 1]));
            System.out.printf("utility:: %s showDataAndSaveDatabaseHealthCare 004 \n", TAG);


            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    dataSendSuccess(data, dateTime, dateFormat, second, minute, hour, mount, date);
                }
            });
        }
    }

    // Paul_20180720
    public void TellToOffLinePosMatching() {
        posInterfaceActivity.PosInterfaceWriteField("01", "000xxxxxx");   // Paul_20180719
        posInterfaceActivity.PosInterfaceWriteField("02", posInterfaceActivity.ResponseMsgPosInterface("00"));
        posInterfaceActivity.PosInterfaceWriteField("65", transTemp.getTraceNo());   // Invoice Number
        posInterfaceActivity.PosInterfaceWriteField("16", transTemp.getTid());   //tid
        posInterfaceActivity.PosInterfaceWriteField("D1", transTemp.getMid());//mid

        posInterfaceActivity.PosInterfaceWriteField("03", transTemp.getTransDate().substring(2, 8));  //yymmdd

        posInterfaceActivity.PosInterfaceWriteField("04", transTemp.getTransTime());

        String CardNo;
        if (transTemp.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            CardNo = transTemp.getCardNo();
        } else if (transTemp.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            CardNo = transTemp.getIdCard();
        } else {
            CardNo = transTemp.getCardNo();
        }
        posInterfaceActivity.PosInterfaceWriteField("30", CardNo);
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, "00");
    }

    private void dataSendSuccess(String[] data, Date dateTime, DateFormat dateFormat, String
            second, String minute, String hour, String mount, String date) {
        String de39Re = BlockCalculateUtil.hexToString(data[39 - 1]);
        System.out.printf("utility:: %s dataSendSuccess 00001 \n", TAG);
        System.out.printf("utility:: statusSale = %s \n", statusSale);
        if (de39Re.equalsIgnoreCase("00")) {
/*
            switch (statusSale) {
                case MedicalTreatmentActivity.KEY_TYPE_FAMILY:
                    if (cardId.getIdCard().trim().replace(" ", "") != null) {
                        saveDataSale(dateFormat.format(dateTime) + mount + date,
                                String.valueOf(hour + minute + second),
                                cardId.getIdCard().trim().replace(" ", ""),
                                BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
                    } else {
                        saveDataSale(dateFormat.format(dateTime) + mount + date,
                                String.valueOf(hour + minute + second),
                                idForeigner,
                                BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
                    }
                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_MIN_SEVEN:
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idCardCd,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));

                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_FOREIGNER:
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idForeigner,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
//                }

                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_NO_CARD:
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idForeigner,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
//                }
                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_FAMILY: // 21
                    if (cardId.getIdCard().trim().replace(" ", "") != null) {
                        saveDataSale(dateFormat.format(dateTime) + mount + date,
                                String.valueOf(hour + minute + second),
                                cardId.getIdCard().trim().replace(" ", ""),
                                BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
                    } else {
                        saveDataSale(dateFormat.format(dateTime) + mount + date,
                                String.valueOf(hour + minute + second),
                                idForeigner,
                                BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
                    }

                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_MIN_SEVEN: // 22
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idCardCd,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));


                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_FOREIGNER: // 23
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idForeigner,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
//                }

                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_NO_CARD: // 24
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idForeigner,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
//                }

                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_FAMILY: // 31
                    if (cardId.getIdCard().trim().replace(" ", "") != null) {
                        saveDataSale(dateFormat.format(dateTime) + mount + date,
                                String.valueOf(hour + minute + second),
                                cardId.getIdCard().trim().replace(" ", ""),
                                BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
                    } else {
                        saveDataSale(dateFormat.format(dateTime) + mount + date,
                                String.valueOf(hour + minute + second),
                                idForeigner,
                                BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
                    }

                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_MIN_SEVEN: // 32
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idCardCd,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));


                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_FOREIGNER: // 33
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idForeigner,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
//                }

                    receivedDataFamily(data[39 - 1]);
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_NO_CARD: // 34
                    saveDataSale(dateFormat.format(dateTime) + mount + date,
                            String.valueOf(hour + minute + second),
                            idForeigner,
                            BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9), CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
//                }

                    receivedDataFamily(data[39 - 1]);
                    break;
            }
*/
        } else {
//            setDataSlipSaleError();
//            receivedDataFamily(data[39 - 1]);
        }
        Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void connectTimeOut() {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        System.out.printf("utility:: %s connectTimeOut 003 \n", TAG);
// Paul_20180723
        if (typeInterface != null) {
            Utility.customDialogAlertAuto(VoidActivity.this, "เชื่อมต่อล้มเหลว");
            TellToPosError("21");
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        } else
            Utility.customDialogAlert(VoidActivity.this, "connectTimeOut", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVoidList();
        System.out.printf("utility:: VoidActivity onResume \n");
        realm = Realm.getDefaultInstance();       // Paul_20180809
        if (cardManager != null) {
            setResponseCode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cardManager.removeResponseCodeListener();
    }

    @Override
    protected void transactionTimeOut() {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        System.out.printf("utility:: %s transactionTimeOut 004 \n", TAG);
// Paul_20180723
        if (typeInterface != null) {
            Utility.customDialogAlertAuto(VoidActivity.this, "เชื่อมต่อล้มเหลว");
            TellToPosError("21");
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        } else
            Utility.customDialogAlert(VoidActivity.this, "transactionTimeOut", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
    }

    @Override
    protected void received(String[] data) {
        System.out.printf("utility:: %s received 00003 \n", TAG);
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        System.out.printf("utility:: %s received 0004 \n", TAG);
        showDataAndSaveDatabaseHealthCare(data);


        removeReversalHealthCare();     // Paul_20180705
        if (BlockCalculateUtil.hexToString(data[39 - 1]).equalsIgnoreCase("00")) {
            updateTransactionVoidHealthCare(data);
//            Utility.customDialogAlertSuccess(VoidActivity.this, "Success :" + data[39 - 1], new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            });
        } else {
//            Utility.customDialogAlert(VoidActivity.this, "error : " + BlockCalculateUtil.hexToString(data[39 - 1]), new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            });
            //end SINN 20180707 fix oidActivity has leaked window
        }
    }

    @Override
    protected void error(String error) {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        Utility.customDialogAlert(VoidActivity.this, "error 005", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void other() {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        Utility.customDialogAlert(VoidActivity.this, "error", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.printf("utility:: VoidActivity onStop \n");
        if (realm != null)     // Paul_20180809
        {
            realm.close();
            realm = null;   // Paul_20181026 Some time DB Read error solved
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
//        Log.d("1919", "onBackPressed()");
//        if (dialogPin != null) { //K.GAME 180906 ยังใช้ไม่ได้
//            Log.d("1919", " if (dialogPin != null)");
//            finish();
//            dialogOutOfPaper.dismiss();
//        }
        finish();
    }

    public void TellToPosError(String szErr) {
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, szErr);
    }

    //PAUL_20180714
    private void setPrint_off(TransTemp transTemp) {
        setDataSlipOffline(transTemp);
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }

//        setContentView(R.layout.view_slip_sale_hgc);
        setContentView(hgcSaleView);

        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                    Utility.customDialogAlertSuccessAutoPaul(VoidActivity.this, "success");
                    TellToOffLinePosMatching();
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            Utility.customDialogAlertAutoClear();
                            //doPrinting(getBitmapFromView(settlementHgcLinearLayout));
                            Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                    autoPrint(); //20180813 SINN Print customer copy
                } else
                    //doPrinting(getBitmapFromView(settlementHgcLinearLayout));
                autoPrint();
            }
        }.start();
    }


    private void autoPrint() {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        Utility.customDialogSelect(VoidActivity.this, "พิมพ์ซ้ำ", new Utility.onTouchoutSide() {

            @Override
            public void onClickImage(Dialog dialog) {
                //doPrinting(getBitmapFromView(settlementHgcLinearLayout));
                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onCancel(Dialog dialog) {
                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
//            }
//        });
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
//                                Intent intent = new Intent(VoidActivity.this, MenuServiceListActivity.class); // Paul_20180704
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                                overridePendingTransition(0, 0);
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

    //PAUL_20180714
    private void setDataSlipOffline(TransTemp healthCareDB) {

        System.out.printf("utility:: %s , setDataSlipOffline healthCareDB.getId()= %d \n", TAG, healthCareDB.getId());
//        transTemp
//        DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");
        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        DateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00"); // Paul_20180711_new


        dateHgcLabel.setText(dateFormat.format(date));
        timeHgcLabel.setText(dateTimeFormat.format(date));

        statusSale = healthCareDB.getTypeSale();


        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
        systrcLabel.setText(healthCareDB.getTraceNo());
//        Log.d("SINN:", "systrcLabel :"+systrcGHCLabel.getText());
//        System.out.printf("utility:: systrcGHCLabel 003 = %s \n",systrcGHCLabel.getText());

        System.out.printf("utility:: healthCareDB.getTypeSale() = %s \n", healthCareDB.getTypeSale());
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
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("2")) {
            System.out.printf("utility:: cardNoLabel 00000000XX001 = %s \n", healthCareDB.getCardNo());
            idCardCd = healthCareDB.getCardNo();
        } else if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            System.out.printf("utility:: cardNoLabel 00000000XX002 = %s \n", healthCareDB.getIdCard());
            idCardCd = healthCareDB.getIdCard();
        } else {
            System.out.printf("utility:: cardNoLabel 00000000XX003 = %s \n", healthCareDB.getCardNo());
            idCardCd = healthCareDB.getCardNo();
        }
        System.out.printf("utility:: cardNoLabel 000001 = %s \n", cardNoLabel.getText());
        String szMSG = null;
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        cardNoLabel.setText(szMSG);
//        nameEngLabel.setText(healthCareDB.getEngFName());
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
        apprCodeLabel.setText("OFFLINE");
        //comCodeLabel.setText("HCG13814");
        comCodeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAG_1001_HC));
        batchHgcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));
        amountLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(healthCareDB.getAmount().replaceAll(",", "")))));
/*
        int nextid = healthCareDB.getId();
        System.out.printf("utility:: healthCareDB.getId = %d \n",nextid);
        TransTemp healthCareDBsave = realm.createObject(TransTemp.class, nextid);
        System.out.printf("utility:: healthCareDB1.getCardNo() = %s \n",healthCareDBsave.getCardNo());
        System.out.printf("utility:: healthCareDB1.getIdCard() = %s \n",healthCareDBsave.getIdCard());
//        healthCareDBsave.setHostTypeCard("GHC");        // Paul_20180724_OFF
//        healthCareDBsave.setTransStat("VOID");          // Paul_20180724_OFF
//        healthCareDBsave.setVoidFlag( "Y" );            // Paul_20180724_OFF
//        healthCareDBsave.setGhcoffFlg("Y");             // Paul_20180724_OFF
        realm.insert(healthCareDBsave);
*/
        updateTransactionVoid(healthCareDB);
        setMeasureHGC();


        /*
        final RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard","GHC").equalTo("id", healthCareDB.getId()).findAll();
        if (transTemps != null) {
            System.out.printf( "utility:: setDataSlipOffline 00000001 \n");
//            transTemps.deleteAllFromRealm();
            realm = Realm.getDefaultInstance();
            System.out.printf( "utility:: setDataSlipOffline 00000002 \n");
            realm.insert(transTemps);
            System.out.printf( "utility:: setDataSlipOffline 00000003 \n");
        }
*/

        /*

//        TransTemp healthCareDBsave = realm.createObject(TransTemp.class, healthCareDB.getId());
        TransTemp healthCareDBsave = realm.where(TransTemp.class).equalTo("id", healthCareDB.getId()).findFirst();

        if (healthCareDBsave.getTypeSale().substring(1,2).equalsIgnoreCase("2")) {
            System.out.printf("utility:: cardNoLabel 00000000XX001 = %s \n",healthCareDBsave.getCardNo());
        } else if (healthCareDBsave.getTypeSale().substring(1,2).equalsIgnoreCase("1")) {
            System.out.printf("utility:: cardNoLabel 00000000XX002 = %s \n",healthCareDBsave.getIdCard());
        } else {
            System.out.printf("utility:: cardNoLabel 00000000XX003 = %s \n",healthCareDBsave.getCardNo());
        }
        healthCareDBsave.setHostTypeCard("GHC");        // Paul_20180724_OFF
        healthCareDBsave.setTransStat("VOID");          // Paul_20180724_OFF
        healthCareDBsave.setVoidFlag( "Y" );            // Paul_20180724_OFF
        healthCareDBsave.setGhcoffFlg("Y");             // Paul_20180724_OFF
        realm.insert(healthCareDBsave);
*/

    }

    private void setMeasureHGC() {
        hgcSaleView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleView.layout(0, 0, hgcSaleView.getMeasuredWidth(), hgcSaleView.getMeasuredHeight());
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

    private void updateTransactionVoid(TransTemp healthCareDB) {
        Log.d("utility:: ", "updateTransactionVoid: " + healthCareDB.getTraceNo());

        final int transTempID = healthCareDB.getId();
        if (realm == null) {
            realm = Realm.getDefaultInstance();
            System.out.printf("utility:: updateTransactionVoid 000000000000 \n");
        }
        RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).findAll();
//        transTemps.size();
        Log.d("utility:: ", "insertTransaction: " + transTemps.size() + " base : " + transTemps.toString());
        System.out.printf("utility:: updateTransactionVoid 000000000001 \n");
        TransTemp trans = realm.where(TransTemp.class).equalTo("id", healthCareDB.getId()).findFirst();
        System.out.printf("utility:: updateTransactionVoid 000000000002 \n");
        realm.beginTransaction();
        System.out.printf("utility:: updateTransactionVoid 000000000003 \n");
        if (trans != null) {
            trans.setVoidFlag("Y");
            trans.setTransStat("VOID");          // Paul_20180724_OFF
        }
        realm.commitTransaction();
        realm.close();        // Paul_20180809
        realm = null;
    }

    private String checkLength(String trace, int i) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for (int j = 0; j < (i - tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }
}
