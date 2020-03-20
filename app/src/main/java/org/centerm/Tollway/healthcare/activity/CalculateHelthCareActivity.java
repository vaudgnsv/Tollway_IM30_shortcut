package org.centerm.Tollway.healthcare.activity;

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
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.database.ReversalHealthCare;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.healthcare.baseavtivity.BaseHealthCardActivity;
import org.centerm.Tollway.healthcare.model.CardId;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_AMOUNT;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;
import static org.centerm.Tollway.activity.posinterface.PosInterfaceActivity.PosInterfaceTransactionCode;
import static org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity.KEY_ID_CARD_CD;

public class CalculateHelthCareActivity extends BaseHealthCardActivity implements View.OnClickListener {

    private final String TAG = "CalculateHelthCareActivity";

    public static final String KEY_CALCULATE_ID_HGC = CalculateHelthCareActivity.class.getName() + "_key_calcuate_id_hgc";

    private Realm realm;

    private TextView idCardLabel = null;
    private FrameLayout sevenClickFrameLayout = null;
    private FrameLayout eightClickFrameLayout = null;
    private FrameLayout nineClickFrameLayout = null;
    private FrameLayout fourClickFrameLayout = null;
    private FrameLayout fiveClickFrameLayout = null;
    private FrameLayout sixClickFrameLayout = null;
    private FrameLayout oneClickFrameLayout = null;
    private FrameLayout twoClickFrameLayout = null;
    private FrameLayout threeClickFrameLayout = null;
    private FrameLayout zeroClickFrameLayout = null;
    private FrameLayout dotClickFrameLayout = null;

    private FrameLayout exitClickFrameLayout = null;
    private FrameLayout deleteClickFrameLayout = null;
    private FrameLayout sureClickFrameLayout = null;
    private TextView priceLabel = null;

    private String numberPrice = "";

    private CardId cardId = null;

    private CardManager cardManager;
    private Dialog dialogWaiting;
    private String[] mBlockDataSend;
    private String TPDU;
    private String TERMINAL_ID;
    private String MERCHANT_NUMBER;
    private String invoiceNumber;
    private String statusSale;

    private String idForeigner = null;        // Paul_20180622
    private String idCardCd = null;

    private int saleId = 0;
    private String msgL;
    private String terVer;
    private String msgVer;
    private String terCode;
    private String batchNo;
    private String refNo;
    private String comCode;
    private String ref1;
    private String ref2;
    private String ref3;
    private String tDate;
    private String randomData;
    private String tCERT;
    private String cSum;

    /**
     * Slip
     */
    private View hgcSaleView;
    private LinearLayout settlementHgcLinearLayout;
    private TextView dateHgcLabel;
    private TextView timeHgcLabel;
    private TextView midHgcLabel;
    private TextView tidHgcLabel;
    private TextView systrcLabel;
    private TextView traceNoLabel;
    private TextView typeSaleLabel;
    private TextView cardNoLabel;
    private TextView nameEngLabel;
    private TextView apprCodeLabel;
    private TextView comCodeLabel;
    private TextView batchHgcLabel;
    private TextView amountLabel;
    private TextView merchantName1HgcLabel;
    private TextView merchantName2HgcLabel;
    private TextView merchantName3HgcLabel;
    private AidlPrinter printer;
    private Bitmap bitmapOld;
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private CountDownTimer timer = null;
    /**
     * View Slip Error
     */
    private View hgcSaleViewError;
    private LinearLayout settlementHgcErLinearLayout;
    private TextView dateHgcErLabel;
    private TextView timeHgcErLabel;
    private TextView midHgcErLabel;
    private TextView tidHgcErLabel;
    private TextView systrcErLabel;
    private TextView traceNoErLabel;
    private TextView typeSaleErLabel;
    private TextView cardNoErLabel;
    private TextView comCodeErLabel;
    private TextView batchHgcErLabel;
    private TextView merchantName1HgcErLabel;
    private TextView merchantName2HgcErLabel;
    private TextView merchantName3HgcErLabel;


    private TextView footerErrorMsg;   ////20180723 Fix GHC error slip
    private TextView errorMsg;         ////20180723 Fix GHC error slip

    /***
     * Interface
     */
    private LinearLayout numberLinearLayout;
    private String typeInterface;
    private String amountInterface;

    private PosInterfaceActivity posInterfaceActivity;
    String PosApprovalCode = null;    // Paul_20180717
    String PosYYMMDD = null;    // Paul_20180717
    String PosHHMMSS = null;    // Paul_20180717
    String PosResponse39 = null;    // Paul_20180717

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_helth);
        realm = Realm.getDefaultInstance();
        cardManager = MainApplication.getCardManager();
        initData();
        initWidget();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getParcelable(IDActivity.KEY_CARD_ID_DATA) != null) {
                cardId = bundle.getParcelable(IDActivity.KEY_CARD_ID_DATA);
                if (cardId != null) {
                    String[] name = cardId.getThName().trim().split(" ");
                    if (bundle.getString(KEY_ID_CARD_CD) != null) {
                        idCardCd = bundle.getString(KEY_ID_CARD_CD);
                    }
                    for (String n : name) {
                        Log.d(TAG, "initData: cardId : " + cardId.getIdCard() + "\n cardId : " + n);
                    }
                    Log.d(TAG, "initData: Thai = " + BlockCalculateUtil.convertStringToHex(cardId.getThName()));
                } else {
                    System.out.printf("utility:: Don't have id card read \n");
                    // back process
                }
            }
            if (bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE) != null) {
                statusSale = bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE);
                Log.d(TAG, "statusSale: " + statusSale);
                if (bundle.getString(MedicalTreatmentActivity.KEY_ID_FOREIGNER_NUMBER) != null)
                    idForeigner = bundle.getString(MedicalTreatmentActivity.KEY_ID_FOREIGNER_NUMBER);
            }
            if (bundle.getString(KEY_TYPE_INTERFACE) != null) {
                typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
                amountInterface = bundle.getString(KEY_INTERFACE_AMOUNT);
                Log.d(TAG, "typeInterface: " + typeInterface);
                Log.d(TAG, "amountInterface: " + amountInterface);
            }
        }
        posInterfaceActivity = MainApplication.getPosInterfaceActivity();
    }

    public void initWidget() {
        idCardLabel = findViewById(R.id.idCardLabel);
        setViewSaleHGC();
        setViewSaleHGCError();
        customDialogOutOfPaper();
        numberLinearLayout = findViewById(R.id.numberLinearLayout);
        oneClickFrameLayout = findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = findViewById(R.id.zeroClickFrameLayout);
        dotClickFrameLayout = findViewById(R.id.dotClickFrameLayout);

        deleteClickFrameLayout = findViewById(R.id.deleteClickFrameLayout);
        sureClickFrameLayout = findViewById(R.id.sureClickFrameLayout);
        exitClickFrameLayout = findViewById(R.id.exitClickFrameLayout);
        priceLabel = findViewById(R.id.priceLabel);
        if (cardId != null) {
            idCardLabel.setText(cardId.getIdCard());
        } else {
            idCardLabel.setText(idForeigner);
        }


        oneClickFrameLayout.setOnClickListener(this);
        twoClickFrameLayout.setOnClickListener(this);
        threeClickFrameLayout.setOnClickListener(this);
        fourClickFrameLayout.setOnClickListener(this);
        fiveClickFrameLayout.setOnClickListener(this);
        sixClickFrameLayout.setOnClickListener(this);
        sevenClickFrameLayout.setOnClickListener(this);
        eightClickFrameLayout.setOnClickListener(this);
        nineClickFrameLayout.setOnClickListener(this);
        zeroClickFrameLayout.setOnClickListener(this);
        dotClickFrameLayout.setOnClickListener(this);

        deleteClickFrameLayout.setOnClickListener(this);
        sureClickFrameLayout.setOnClickListener(this);
        exitClickFrameLayout.setOnClickListener(this);
        customDialogWaiting();

        if (amountInterface != null) {
            setClickable(false);
            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            String amountStart = amountInterface.substring(0, amountInterface.length() - 2);
            String amountLast = amountInterface.substring(amountInterface.length() - 2);
            String amount = amountStart + "." + amountLast;
            Log.d(TAG, "amount: " + amount);
            Log.d(TAG, "amount: " + decimalFormat.format(Double.valueOf(amount)));
            priceLabel.setText(decimalFormat.format(Double.valueOf(amount)));
            //PAUL20180714
            pos_Handler.sendEmptyMessageDelayed(0, 100); //Paul_20180714
//            submitAmount();
        } else {
            for (int i = 0; i < numberLinearLayout.getChildCount(); i++) {
                View view = numberLinearLayout.getChildAt(i);
                view.setEnabled(true);
            }
            setClickable(true);
        }
    }

    private void setClickable(boolean isClick) {
        oneClickFrameLayout.setClickable(isClick);
        twoClickFrameLayout.setClickable(isClick);
        threeClickFrameLayout.setClickable(isClick);
        fourClickFrameLayout.setClickable(isClick);
        fiveClickFrameLayout.setClickable(isClick);
        sixClickFrameLayout.setClickable(isClick);
        sevenClickFrameLayout.setClickable(isClick);
        eightClickFrameLayout.setClickable(isClick);
        nineClickFrameLayout.setClickable(isClick);
        zeroClickFrameLayout.setClickable(isClick);
        dotClickFrameLayout.setClickable(isClick);

        deleteClickFrameLayout.setClickable(isClick);
    }

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

    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        }
    }

    private void submitAmount() {
        Log.d(TAG, ":) CalculateHelthCareActivity.java submitAmount: " + numberPrice);

        dialogWaiting.show();   ////20180725 SINN SHOW WAITING after submit amount.
        dialogWaiting.setCancelable(false);

        if (!priceLabel.getText().toString().equalsIgnoreCase("0.00") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0.") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0.0")) {
            switch (statusSale) {
                case MedicalTreatmentActivity.KEY_TYPE_FAMILY:
                    sendDataSaleFamily(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_MIN_SEVEN:
                    sendDataSaleMinSeven(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_FOREIGNER:
                    Log.d(TAG, "submitAmount: 13 ");
                    sendDataSaleForeigner(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_NO_CARD:
                    sendDataSaleNoCard(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_FAMILY: // 21
                    sendDataSaleKidneyFamily(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_MIN_SEVEN: // 22
                    sendDataSaleKidneyMinSeven(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_FOREIGNER: // 23
                    sendDataSaleKidneyForeigner(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_KIDNEY_NO_CARD: // 24
                    sendDataSaleKidneyNoCard(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_FAMILY: // 31
                    sendDataSaleCancerFamily(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_MIN_SEVEN: // 32
                    sendDataSaleCancerMinSeven(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_FOREIGNER: // 33
                    sendDataSaleCancerForeigner(priceLabel.getText().toString());
                    break;
                case MedicalTreatmentActivity.KEY_TYPE_CANCER_NO_CARD: // 34
                    sendDataSaleCancerNoCard(priceLabel.getText().toString());
                    break;
            }
//            sendDataSaleTest(priceLabel.getText().toString());
        } else {
            Utility.customDialogAlert(CalculateHelthCareActivity.this, "กรุณาใส่จำนวนเงิน", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Click number: " + numberPrice);
        String[] splitter = null;
        Log.d(TAG, "onClick: " + numberPrice.contains("."));
        if (numberPrice.length() < 8) {
            if (!numberPrice.contains(".")) {
                Log.d(TAG, "if Main : ");
                if (!numberPrice.isEmpty())
                    if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                        numberPrice = "";
                clickCal(v);
            } else {
                Log.d(TAG, "onClick: ");
                Log.d(TAG, "else Main : ");
                splitter = numberPrice.split("\\.");
                if (splitter.length > 1) {
                    Log.d(TAG, "if Sub : ");
                    if (splitter[1].length() > 1) {
                        Log.d(TAG, "splitter[1].length() > 1: ");
                        if (v == exitClickFrameLayout) {
                            cardManager.abortPBOCProcess();
                            finish();
                        } else if (v == deleteClickFrameLayout) {
                            if (!numberPrice.equalsIgnoreCase("0.00")) {
                                Log.d(TAG, "onClick: numberPrice.equalsIgnoreCase(\"0.00\") ");
                                if (numberPrice.length() == 0) {
                                    Log.d(TAG, "onClick: numberPrice.length() If == 0 ");
                                    numberPrice = "0.00";
                                } else {
                                    numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                                    if (numberPrice.length() == 0) {
                                        Log.d(TAG, "onClick: numberPrice.length() Else == 0 ");
                                        numberPrice = "0.00";
                                    }
                                }
                            } else {
                                if (!numberPrice.isEmpty()) {
                                    numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                                }
                            }
                        } else if (v == sureClickFrameLayout) {
                            //20180723 SINNN fixed double click.
                            if (!numberPrice.isEmpty())
                                sureClickFrameLayout.setEnabled(false);
                            submitAmount();

                        }
                    } else {
                        if (!numberPrice.isEmpty())
                            /*if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                                numberPrice = "";*/
                            clickCal(v);
                    }
                } else {

                    Log.d(TAG, "splitter[1].length() > 1 Else: ");

                    if (!numberPrice.isEmpty())
                        /*if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                            numberPrice = "";*/
                        Log.d(TAG, "else Sub : " + splitter.length);
                    Log.d(TAG, "else Sub : " + splitter[splitter.length - 1]);
                    clickCal(v);
                }
            }
        } else {
            if (v == exitClickFrameLayout) {
//                if (typeInterface != null) {
//                    TerToPosCancel();
//                }
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                    TerToPosCancel();
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                            Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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
                    finish();
            } else if (v == deleteClickFrameLayout) {
                numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                if (numberPrice.length() == 0) {
                    numberPrice = "";
                    priceLabel.setText("0.00");
                    if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                        priceLabel.setText(amountInterface);
                }
            } else if (v == sureClickFrameLayout) {
                //20180723 SINNN fixed double click.
                if (!numberPrice.isEmpty())
                    sureClickFrameLayout.setEnabled(false);
                submitAmount();

            }
        }

        if (!numberPrice.isEmpty()) {
            priceLabel.setText(numberPrice);
        }
    }

    private void clickCal(View v) {

        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "0";
        } else if (v == dotClickFrameLayout) {
            checkNumberPrice();
            if (!numberPrice.isEmpty()) {
                if (!numberPrice.contains(".")) {
                    numberPrice += ".";
                }
            } else {
                numberPrice += "0.";
            }
        } else if (v == exitClickFrameLayout) {
            cardManager.abortPBOCProcess();
            finish();
        } else if (v == deleteClickFrameLayout) {
            if (!priceLabel.getText().toString().equalsIgnoreCase("0.00")) {
                Log.d(TAG, "clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + numberPrice);
                    numberPrice = "";
                    priceLabel.setText("0.00");
                    if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                        priceLabel.setText(amountInterface);
                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        if (numberPrice.equalsIgnoreCase("") || numberPrice == null) {
                            Log.d(TAG, "clickCal: if");
                            numberPrice = "";
                            priceLabel.setText("0.00");
                            if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                                priceLabel.setText(amountInterface);
                        } else {
                            Log.d(TAG, "clickCal: else");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } else if (v == sureClickFrameLayout) {
            //20180723 SINNN fixed double click.
            if (!numberPrice.isEmpty())
                sureClickFrameLayout.setEnabled(false);
            submitAmount();

//            if(typeInterface != null)
//            {
//                    Button okBtn = dialogParaEndble.findViewById(R.id.okBtn);
//                    okBtn.performLongClick();
//            }
        }
    }

    public void TerToPosCancel() {
/*        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        DateFormat timeFormat = new SimpleDateFormat("HHmmss");
        posInterfaceActivity.PosInterfaceWriteField("02", posInterfaceActivity.ResponseMsgPosInterface("ND"));   // Response Message
        posInterfaceActivity.PosInterfaceWriteField("D0", "                                                                     ");   //
// Paul_20180719
        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_TERMINAL_ID_GHC));
// Paul_20180719
        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_GHC));
        posInterfaceActivity.PosInterfaceWriteField("03", dateFormat.format(date));   // Date YYMMDD
        posInterfaceActivity.PosInterfaceWriteField("04", timeFormat.format(date));   // Time HHMMSS
//        PosInterfaceWriteField("30","");   // Card No
        posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode, "ND");*/

        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("ND"));
        posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode, "ND");

    }

    public void TellToPosError(String szErr) {
        /*posInterfaceActivity.PosInterfaceWriteField("01","000xxxxxx");   // Approval Code
        //posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("12"));   // Response Message TX NOT FOUND
        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(szErr));

        posInterfaceActivity.PosInterfaceWriteField("65","000000");   // Invoice Number
//        posInterfaceActivity.PosInterfaceWriteField("D3","xxxxxxxxxxxx");

        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_TERMINAL_ID_GHC));
        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_GHC));

        Date date = new Date();
        String dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);
        posInterfaceActivity.PosInterfaceWriteField("03",dateFormatDef);  //yymmdd

        String timeFormat = new SimpleDateFormat("HHmmss").format(date);
        posInterfaceActivity.PosInterfaceWriteField("04",timeFormat);  //hhmmss

//        posInterfaceActivity.PosInterfaceWriteField("F1","QR");   // Paul_20180719

        //posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode,"12");
        posInterfaceActivity.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,szErr);*/

        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, szErr);

    }

    @Override
    protected void connectTimeOut() {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
// Paul_20180717
        System.out.printf("utility:: %s connectTimeOut 0000001 \n", TAG);
        if (typeInterface != null) {
            Utility.customDialogAlertAuto(CalculateHelthCareActivity.this, "ไม่สามารถเชื่อมต่อได้");
            System.out.printf("utility:: CalculateHelthCareActivity connectTimeOut \n");
            TellToPosError("21");
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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
            Utility.customDialogAlert(this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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

    @Override
    protected void transactionTimeOut() {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        if (typeInterface != null) {        // Paul_20180723
            System.out.printf("utility:: CalculateHelthCareActivity transactionTimeOut \n");
            Utility.customDialogAlertAuto(CalculateHelthCareActivity.this, "ไม่สามารถเชื่อมต่อได้");
            TellToPosError("21");
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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
            Utility.customDialogAlert(this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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

    @Override
    protected void received(String[] data) {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        System.out.printf("utility:: %s received \n", TAG);
        showDataAndSaveDatabase(data);
    }

    @Override
    protected void error(String error) {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        if (typeInterface != null) {        // Paul_20180712
            TerToPosCancel();
        }
        Utility.customDialogAlert(this, "ไม่สามารถเชื่อมต่อได้ " + error, new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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

    @Override
    protected void other() {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        if (typeInterface != null) {        // Paul_20180712
            TerToPosCancel();
        }
        Utility.customDialogAlert(this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
//        if(typeInterface!=null) {
//            submitAmount();

//            Button okBtn = dialogParaEndble.findViewById(R.id.okBtn);
//            okBtn.performLongClick();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (realm != null)   // Paul_20180720
        {
            realm.close();
            realm = null;   // Paul_20181026 Some time DB Read error solved
        }
//        posInterfaceActivity.removeAckToPrint();         // Paul_20180720
    }

    /**
     * Zone requestData
     */
    private void sendDataSaleFamily(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + cardId.getIdCard().trim().replace(" ", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + cardId.getIdCard().replace(" ", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));

        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("M1", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);

        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } // 11

    private void sendDataSaleMinSeven(String amount) {
        Date date = new Date();
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + idCardCd.trim().replace(" ", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + idCardCd.replace(" ", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));

        msgL = "00000390";
        terVer = "00080001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen(cardId.getIdCard().replace(" ", ""), 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("M2", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String[] name = cardId.getThName().trim().split(" ");
        String nameReal = name[1] + "#" + name[3];
        nameReal = BlockCalculateUtil.convertStringToHex(nameReal);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum + nameReal;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } // 12

    private void sendDataSaleForeigner(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "9000" + idForeigner.replace("B", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "9000" + idForeigner.replace("B", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("M3", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } //13

    private void sendDataSaleNoCard(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + idForeigner;
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + idForeigner + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("M4", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } //14

    private void sendDataSaleKidneyFamily(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + cardId.getIdCard().trim().replace(" ", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + cardId.getIdCard().replace(" ", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("M1", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    }//21

    private void sendDataSaleKidneyMinSeven(String amount) {
        Date date = new Date();
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + idCardCd.trim().replace(" ", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + idCardCd.replace(" ", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));

        msgL = "00000390";
        terVer = "00080001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen(cardId.getIdCard().replace(" ", ""), 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("M2", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String[] name = cardId.getThName().trim().split(" ");
        String nameReal = name[1] + "#" + name[3];
        nameReal = BlockCalculateUtil.convertStringToHex(nameReal);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum + nameReal;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } // 22

    private void sendDataSaleKidneyForeigner(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "9000" + idForeigner.replace("B", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "9000" + idForeigner.replace("B", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("M3", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } //23

    private void sendDataSaleKidneyNoCard(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + idForeigner;
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + idForeigner + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("M4", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } // 24

    private void sendDataSaleCancerFamily(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + cardId.getIdCard().trim().replace(" ", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + cardId.getIdCard().replace(" ", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("M1", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    }//31

    private void sendDataSaleCancerMinSeven(String amount) {
        Date date = new Date();
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + idCardCd.trim().replace(" ", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + idCardCd.replace(" ", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));

        msgL = "00000390";
        terVer = "00080001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen(cardId.getIdCard().replace(" ", ""), 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("M2", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String[] name = cardId.getThName().trim().split(" ");
        String nameReal = name[1] + "#" + name[3];
        nameReal = BlockCalculateUtil.convertStringToHex(nameReal);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum + nameReal;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } // 32

    private void sendDataSaleCancerForeigner(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "9000" + idForeigner.replace("B", "");
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "9000" + idForeigner.replace("B", "") + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("M3", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");

        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } //33

    private void sendDataSaleCancerNoCard(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String amountReal = decimalFormat.format(Double.valueOf(amount));
        mBlockDataSend = new String[64];
        String cardNumber = "000" + idForeigner;
        if ((cardNumber.length() % 2) != 0) {
            Log.d(TAG, "cardNumber: If");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            Log.d(TAG, "cardNumber: Else");
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
//        mBlockDataSend[2 - 1] = "160003101100015314";
        mBlockDataSend[3 - 1] = "005000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amountReal);
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = "0444";
        mBlockDataSend[25 - 1] = "05";
        String cardNo = "000" + idForeigner + "D22102200460000010006";
        if ((cardNo.length() % 2) != 0) {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo + "0";
        } else {
            mBlockDataSend[35 - 1] = cardNo.length() + cardNo;
        }
//        mBlockDataSend[35 - 1] = "37" + cardNo ;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[52 - 1] = "87F1594650284713";
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
        String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);
        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("M4", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = Utility.getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        insertReversalTransaction();
    } // 34

    /**
     * Zone ReceivedData Action
     */

    private void receivedDataFamily(String data) {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        if (typeInterface != null) {        // Paul_20180717
            return;
        }
        String m39 = BlockCalculateUtil.hexToString(data);
        if (m39.equalsIgnoreCase("00")) {
            Utility.customDialogAlertSuccess(this, null, new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else {
            Utility.customDialogAlert(this, "ไม่สามารถทำรายการได้  " + m39, new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    /**
     * Zone Save Database
     */
    private void saveDataSale(final String date,
                              final String time,
                              final String cardNumber,
                              final String appCode,
                              final String refNo) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                System.out.printf("utility:: %s saveDataSale 0001 \n", TAG);
                String tid = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC);
                String mid = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC);
                String batch = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
                String invoice = Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);

                DecimalFormat decimalFormat = new DecimalFormat("###0.00");
                Number currentId = realm.where(TransTemp.class).max("id");
                int nextId;
                if (currentId == null) {
                    nextId = 1;
                    saleId = nextId;
                } else {
                    nextId = currentId.intValue() + 1;
                    saleId = nextId;
                }
                TransTemp healthCareDB = realm.createObject(TransTemp.class, nextId);
                int traceNo = Integer.parseInt(CardPrefix.geTraceId(CalculateHelthCareActivity.this, "GHC"));
                healthCareDB.setTraceNo(CardPrefix.calLen(String.valueOf(traceNo - 1), 6));
                healthCareDB.setHostTypeCard("GHC");
                healthCareDB.setAppid(String.valueOf(nextId));
                healthCareDB.setTid(Utility.calNumTraceNo(tid));
                healthCareDB.setMid(Utility.calNumTraceNo(mid));
//                healthCareDB.setBatch(Utility.calNumTraceNo(batch));
                healthCareDB.setTrack2(mBlockDataSend[35 - 1]);
                healthCareDB.setComCode(comCode);
                healthCareDB.setRef1(ref1);
                healthCareDB.setRef2(ref2);
                healthCareDB.setRef3(ref3);

                healthCareDB.setEcr(Utility.calNumTraceNo(invoice));
                int inV = Integer.parseInt(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL));
                inV = inV + 1;
                Preference.getInstance(CalculateHelthCareActivity.this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
                healthCareDB.setConditionCode(mBlockDataSend[25 - 1]);
                healthCareDB.setDe63Sale(mBlockDataSend[63 - 1]);
                healthCareDB.setTransDate(date);
                healthCareDB.setTransTime(time);
                healthCareDB.setCardNo(cardNumber);
                if (cardId != null) {
                    healthCareDB.setIdCard(cardId.getIdCard().replace(" ", ""));
                    healthCareDB.setThName(cardId.getThName());
                    healthCareDB.setEngFName(cardId.getEngFName());
                    healthCareDB.setEngLName(cardId.getEngLName());
                    healthCareDB.setEngBirth(cardId.getEngBirth());
                    healthCareDB.setThBirth(cardId.getThBirth());
                    healthCareDB.setAddress(cardId.getAddress());
                    healthCareDB.setEngIssue(cardId.getThIssue());
                    healthCareDB.setThIssue(cardId.getThName());
                    healthCareDB.setEngExpire(cardId.getThExpire());
                    healthCareDB.setThExpire(cardId.getThName());
                    healthCareDB.setReligion(cardId.getReligion());
                    healthCareDB.setXphoto(String.valueOf(cardId.getXphoto()));
                }
                healthCareDB.setApprvCode(appCode);
                Log.d(TAG, "execute: " + appCode);
                healthCareDB.setRefNo(refNo);
                healthCareDB.setTypeSale(statusSale);
                healthCareDB.setVoidFlag("N");
                healthCareDB.setGhcoffFlg("N");     // Paul_20180724_OFF
                healthCareDB.setAmount(decimalFormat.format(Double.valueOf(priceLabel.getText().toString())));
                realm.insert(healthCareDB);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
// Paul_20180718
//                Toast.makeText(CalculateHelthCareActivity.this, "Success", Toast.LENGTH_SHORT).show();

                selectHealthCareSALE();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                saleId = 0;
                Log.d(TAG, "onError: " + error.getMessage());
// Paul_20180718
//                Toast.makeText(CalculateHelthCareActivity.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void insertReversalTransaction() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentId = realm.where(ReversalHealthCare.class).max("id");
                int nextId;
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
                ReversalHealthCare reversalHealthCare = realm.createObject(ReversalHealthCare.class, nextId);
                reversalHealthCare.setDe2(mBlockDataSend[2 - 1]);
                reversalHealthCare.setDe3(mBlockDataSend[3 - 1]);
                reversalHealthCare.setDe4(mBlockDataSend[4 - 1]);
                reversalHealthCare.setDe11(mBlockDataSend[11 - 1]);
                reversalHealthCare.setDe22(mBlockDataSend[22 - 1]);
                reversalHealthCare.setDe24(mBlockDataSend[24 - 1]);
                reversalHealthCare.setDe25(mBlockDataSend[25 - 1]);
                reversalHealthCare.setDe35(mBlockDataSend[35 - 1]);
                reversalHealthCare.setDe41(mBlockDataSend[41 - 1]);
                reversalHealthCare.setDe42(mBlockDataSend[42 - 1]);
                reversalHealthCare.setDe52(mBlockDataSend[52 - 1]);
                reversalHealthCare.setDe62(mBlockDataSend[62 - 1]);
                reversalHealthCare.setDe63(mBlockDataSend[63 - 1]);
                reversalHealthCare.setType("SALE");
                realm.insertOrUpdate(reversalHealthCare);
            }
// Paul_20180718
/*
        , new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(CalculateHelthCareActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(CalculateHelthCareActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
*/
        });

    }

    private void removeReversal() {
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
    }

    /**
     * Zone ShowData received Data
     */

    private void showDataAndSaveDatabase(final String[] data) {
        final Date dateTime = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy");
        String year = new SimpleDateFormat("yy").format(dateTime);  // Paul_20180706
        final String second = data[12 - 1].substring(4, 6);
        final String minute = data[12 - 1].substring(2, 4);
        final String hour = data[12 - 1].substring(0, 2);
        final String mount = data[13 - 1].substring(0, 2);
        final String date = data[13 - 1].substring(2, 4);

        removeReversal();
        dataSendSuccess(data, dateTime, dateFormat, second, minute, hour, mount, date);
/*
        PosApprovalCode = BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9);
        PosYYMMDD = year+mount+date;
        PosHHMMSS = hour+minute+second;
        PosResponse39 = BlockCalculateUtil.hexToString(data[39 - 1]);


        if (typeInterface == null) {
            dataSendSuccess(data, dateTime, dateFormat, second, minute, hour, mount, date);
        } else {
            invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
            TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
            MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
System.out.printf("utility:: invoiceNumber = %s ,TERMINAL_ID=%s,MERCHANT_NUMBER=%s \n",invoiceNumber,TERMINAL_ID,MERCHANT_NUMBER);
            posInterfaceActivity.PosInterfaceWriteField("01",BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9));   // Approval Code
            posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(PosResponse39));   // Response Message

            posInterfaceActivity.PosInterfaceWriteField("65",invoiceNumber);   // Invoice Number
            posInterfaceActivity.PosInterfaceWriteField("16",TERMINAL_ID);   // Terminal ID
            posInterfaceActivity.PosInterfaceWriteField("D1",MERCHANT_NUMBER);   // Merchant ID
            posInterfaceActivity.PosInterfaceWriteField("03",year+mount+date);   // Date YYMMDD
            posInterfaceActivity.PosInterfaceWriteField("04",hour+minute+second);   // Time HHMMSS
// Paul_20180719
            String szMSG;
            if (statusSale.substring(1).equalsIgnoreCase("1")) {
//                posInterfaceActivity.PosInterfaceWriteField("30", cardId.getIdCard().replace(" ",""));   // Card No
                szMSG=cardId.getIdCard().replace(" ","").substring(0,1)+" "+cardId.getIdCard().replace(" ","").substring(1,4 )+"X"+" "+"XXXX"+cardId.getIdCard().replace(" ","").substring(9,10 )+" "+cardId.getIdCard().replace(" ","").substring(10,12 )+" "+cardId.getIdCard().replace(" ","").substring(12,13 );
            } else if (statusSale.substring(1).equalsIgnoreCase("2")) {
//                posInterfaceActivity.PosInterfaceWriteField("30", idCardCd);   // Card No
                szMSG=idCardCd.substring(0,1)+" "+idCardCd.substring(1,4 )+"X"+" "+"XXXX"+idCardCd.substring(9,10 )+" "+idCardCd.substring(10,12 )+" "+idCardCd.substring(12,13 );
            } else {
//                posInterfaceActivity.PosInterfaceWriteField("30", idForeigner);   // Card No
                szMSG=idForeigner.substring(0,1)+" "+idForeigner.substring(1,4 )+"X"+" "+"XXXX"+idForeigner.substring(9,10 )+" "+idForeigner.substring(10,12 )+" "+idForeigner.substring(12,13 );
            }
            posInterfaceActivity.PosInterfaceWriteField("30", szMSG);   // Card No
            posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode,PosResponse39);




//            receivedDataFamily(data[39 -1]);

            String m39 = BlockCalculateUtil.hexToString(data[39 - 1]);
            if (m39.equalsIgnoreCase("00")) {
                Utility.customDialogAlertOKAuto(this, null);
            } else {
                Utility.customDialogAlertAuto(this, "ไม่สามารถทำรายการได้  " + m39);
            }
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    dataSendSuccess(data, dateTime, dateFormat, second, minute, hour, mount, date);
                }
            });
        }
*/
    }

    public void TellToPosMatching(TransTemp healthCareDB) {


        posInterfaceActivity.PosInterfaceWriteField("01", healthCareDB.getApprvCode());   // Approval Code
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("00"));   // Response Message
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));   // Response Message

        posInterfaceActivity.PosInterfaceWriteField("65", healthCareDB.getTraceNo());   // Invoice Number
        posInterfaceActivity.PosInterfaceWriteField("16", healthCareDB.getTid());   // Terminal ID
        posInterfaceActivity.PosInterfaceWriteField("D1", healthCareDB.getMid());   // Merchant ID
        posInterfaceActivity.PosInterfaceWriteField("03", healthCareDB.getTransDate().substring(2, 8));   // Date YYMMDD
        posInterfaceActivity.PosInterfaceWriteField("04", healthCareDB.getTransTime());   // Time HHMMSS
        String CardNo;
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            CardNo = healthCareDB.getCardNo();
        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            CardNo = healthCareDB.getIdCard();
        } else {
            CardNo = healthCareDB.getCardNo();
        }
        String szMSG = CardNo.substring(0, 1) + " " + CardNo.substring(1, 4) + "X" + " " + "XXXX" + CardNo.substring(9, 10) + " " + CardNo.substring(10, 12) + " " + CardNo.substring(12, 13);
        posInterfaceActivity.PosInterfaceWriteField("30", szMSG);
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode, "00");
    }

    private void dataSendSuccess(String[] data, Date dateTime, DateFormat dateFormat, String second, String minute, String hour, String mount, String date) {
        String de39Re = BlockCalculateUtil.hexToString(data[39 - 1]);
        if (de39Re.equalsIgnoreCase("00")) {
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
        } else {
            receivedDataFamily(data[39 - 1]);
            setDataSlipSaleError(data);
        }
    }

    /**
     * View Slip
     */
    private void setViewSaleHGC() {
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

        //20180720 SINN Print slip
        dateHgcLabel.setText("");
        timeHgcLabel.setText("");
        midHgcLabel.setText("");
        tidHgcLabel.setText("");
        systrcLabel.setText("");
        traceNoLabel.setText("");
        typeSaleLabel.setText("");
        cardNoLabel.setText("");
        nameEngLabel.setText("");
        apprCodeLabel.setText("");
        comCodeLabel.setText("");
        batchHgcLabel.setText("");
        amountLabel.setText("");
        merchantName1HgcLabel.setText("");
        merchantName2HgcLabel.setText("");
        merchantName3HgcLabel.setText("");
        //END 20180720 SINN Print slip
    }

    private void setViewSaleHGCError() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hgcSaleViewError = inflater.inflate(R.layout.view_slip_sale_hgc_error, null);
//        settlementHgcErLinearLayout = hgcSaleViewError.findViewById(R.id.settlementLinearLayout);
        settlementHgcErLinearLayout = hgcSaleViewError.findViewById(R.id.view_slip_sale_hgc_error_layout);
        dateHgcErLabel = hgcSaleViewError.findViewById(R.id.dateLabel);
        timeHgcErLabel = hgcSaleViewError.findViewById(R.id.timeLabel);
        midHgcErLabel = hgcSaleViewError.findViewById(R.id.midLabel);
        tidHgcErLabel = hgcSaleViewError.findViewById(R.id.tidLabel);
        systrcErLabel = hgcSaleViewError.findViewById(R.id.systrcLabel);
        //  traceNoErLabel = hgcSaleViewError.findViewById(R.id.traceNoLabel);   //20180720 Print slip error no trace number
        //   typeSaleErLabel = hgcSaleViewError.findViewById(R.id.typeSaleLabel);  //20180720 Print slip error no trace number
        cardNoErLabel = hgcSaleViewError.findViewById(R.id.cardNoLabel);
        comCodeErLabel = hgcSaleViewError.findViewById(R.id.comCodeErLabel);
        // batchHgcErLabel = hgcSaleViewError.findViewById(R.id.batchLabel);//20180720 Print slip error no batchdddd number
        merchantName1HgcErLabel = hgcSaleViewError.findViewById(R.id.merchantName1Label);
        merchantName2HgcErLabel = hgcSaleViewError.findViewById(R.id.merchantName2Label);
        merchantName3HgcErLabel = hgcSaleViewError.findViewById(R.id.merchantName3Label);


        footerErrorMsg = hgcSaleViewError.findViewById(R.id.footerErrorMsg);
        errorMsg = hgcSaleViewError.findViewById(R.id.errorMsg);

        footerErrorMsg.setText(" ");   //20180723 HGC slip error following host error response
        errorMsg.setText(" ");     //20180723 HGC slip error following host error response

        //20180720 SINN Print slip
        dateHgcErLabel.setText(" ");
        timeHgcErLabel.setText(" ");
        midHgcErLabel.setText(" ");
        // tidHgcErLabel.setText(" ");  //error slip no tid.
        systrcErLabel.setText(" ");
        // traceNoErLabel.setText(" ");  //20180720 Print slip error no trace number
        // typeSaleErLabel.setText(" ");  //20180720 Print slip error no trace number
        cardNoErLabel.setText(" ");
        comCodeErLabel.setText(" ");
        //  batchHgcErLabel.setText(" ");
//        merchantName1HgcErLabel.setText(" ");
//        merchantName2HgcErLabel.setText(" ");
//        merchantName3HgcErLabel.setText(" ");

        //END 20180720 SINN Print slip
    }

    private void setMeasureHGCEr() {
        hgcSaleViewError.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleViewError.layout(0, 0, hgcSaleViewError.getMeasuredWidth(), hgcSaleViewError.getMeasuredHeight());
    }

    private void selectHealthCareSALE() {
        TransTemp healthCareDB = realm.where(TransTemp.class).equalTo("id", saleId).findFirst();
        Log.d(TAG, "selectSALE: " + healthCareDB.getCardNo());
        setDataSlipSale(healthCareDB);
    }

    private void setDataSlipSale(TransTemp healthCareDB) {
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");    // Paul_20180711
        String year = healthCareDB.getTransDate().substring(0, 4);
        String mount = healthCareDB.getTransDate().substring(4, 6);
        String day = healthCareDB.getTransDate().substring(6, 8);
        String hour = healthCareDB.getTransTime().substring(0, 2);
        String minte = healthCareDB.getTransTime().substring(2, 4);
        String sec = healthCareDB.getTransTime().substring(4, 6);
        dateHgcLabel.setText(day + "/" + mount + "/" + year);
        timeHgcLabel.setText(hour + ":" + minte + ":" + sec);
        midHgcLabel.setText(healthCareDB.getMid());
        tidHgcLabel.setText(healthCareDB.getTid());
        systrcLabel.setText(healthCareDB.getTraceNo());
        traceNoLabel.setText(healthCareDB.getEcr());
        // Sinn_20180711
        Log.d(TAG, "healthCareDB.getEcr() :" + healthCareDB.getEcr());
        // Sinn_20180711
        Preference.getInstance(CalculateHelthCareActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_GHC, Integer.valueOf(healthCareDB.getEcr()));
        if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (healthCareDB.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
//        if(healthCareDB.getCardNo()==null)
//            return;
        ////SINN 20180711 ADD THAI ID  SET MASK
        String szMSG = null;
//        idCardCd = healthCareDB.getCardNo();        // Paul_20180712
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {        // Paul_20180712
            idCardCd = healthCareDB.getIdCard();
        } else {
            idCardCd = healthCareDB.getCardNo();
        }
        if (idCardCd.length() < 13) {
            int i;
            for (i = idCardCd.length(); i < 13; i++) {
                idCardCd += " ";
            }
        }
//        szMSG=healthCareDB.getCardNo().substring(0,1)+" "+healthCareDB.getCardNo().substring(1,4 )+"X"+" "+"XXX"+healthCareDB.getCardNo().substring(9,13 );
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        //END SINN 20180711 ADD THAI ID  SET MASK

        cardNoLabel.setText(szMSG);     // Paul_20180712

/*
        if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("2")) {
//            cardNoLabel.setText(healthCareDB.getCardNo());
            cardNoLabel.setText(szMSG);  ////SINN 20180711 ADD THAI ID  SET MASK

        } else if (healthCareDB.getTypeSale().substring(1).equalsIgnoreCase("1")) {
//            cardNoLabel.setText(healthCareDB.getIdCard());
            cardNoLabel.setText(szMSG);  ////SINN 20180711 ADD THAI ID  SET MASK

        } else {
            cardNoLabel.setText(healthCareDB.getCardNo());
        }
*/
//        nameEngLabel.setText(healthCareDB.getEngFName());
// Paul_20180720
        if (healthCareDB.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(healthCareDB.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
        apprCodeLabel.setText(healthCareDB.getApprvCode());
//        comCodeLabel.setText("HCG13814");
        comCodeLabel.setText(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC));
        batchHgcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));
        amountLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(priceLabel.getText().toString()))));
        setMeasureHGC();
        if (typeInterface != null) {
            Utility.customDialogAlertSuccessAutoPaul(CalculateHelthCareActivity.this, "success");
            TellToPosMatching(healthCareDB);
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    doPrinter(getBitmapFromView(settlementHgcLinearLayout));
                    autoPrint();
//                    Intent intent = new Intent( CalculateHelthCareActivity.this, MenuServiceListActivity.class );
//                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
//                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                    startActivity( intent );
//                    finish();
//                    overridePendingTransition( 0, 0 );
                }
            });
        } else {
            doPrinter(getBitmapFromView(settlementHgcLinearLayout));
            autoPrint();
        }
    }


    private void autoPrint() {

        Utility.customDialogSelect(CalculateHelthCareActivity.this, "พิมพ์ซ้ำ", new Utility.onTouchoutSide() {

            @Override
            public void onClickImage(Dialog dialog) {
                doPrinter(getBitmapFromView(settlementHgcLinearLayout));
                Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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
                Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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


    private void setDataSlipSaleError(String[] data) {
        if (dialogWaiting != null) {
            if (dialogWaiting.isShowing()) {
                dialogWaiting.dismiss();
            }
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
        dateHgcErLabel.setText(dateFormat.format(date));
        timeHgcErLabel.setText(dateTimeFormat.format(date));
//        midHgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));//slip error no tid
        //   tidHgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC)); //slip error no tid
//        systrcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC));//slip error no tid
// Paul_20180713 Start
        String szMSG = null;
        if (statusSale.substring(1).equalsIgnoreCase("1")) {
            idCardCd = cardId.getIdCard();
        } else if (statusSale.substring(1).equalsIgnoreCase("2")) {
            idCardCd = idCardCd;
        } else {
            idCardCd = idForeigner;
        }
        if (idCardCd.length() < 13) {
            int i;
            for (i = idCardCd.length(); i < 13; i++) {
                idCardCd += " ";
            }
        }
//        szMSG=healthCareDB.getCardNo().substring(0,1)+" "+healthCareDB.getCardNo().substring(1,4 )+"X"+" "+"XXX"+healthCareDB.getCardNo().substring(9,13 );
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        cardNoErLabel.setText(szMSG);
//        if (statusSale.substring(1).equalsIgnoreCase("2")) {
//            cardNoErLabel.setText(idCardCd);
//        } else if (statusSale.substring(1).equalsIgnoreCase("1")) {
//            cardNoErLabel.setText(cardId.getIdCard());
//        } else {
//            cardNoErLabel.setText(idForeigner);
//        }
// Paul_20180713 End
//        comCodeErLabel.setText("HCG13814");
        comCodeErLabel.setText(Preference.getInstance(CalculateHelthCareActivity.this).getValueString(Preference.KEY_TAG_1001_HC));
        //   batchHgcErLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));//slip error no batch

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        ////20180723 HGC slip error following host error response
        String PosResponse39 = BlockCalculateUtil.hexToString(data[39 - 1]);

        Log.d(TAG, "statusSale==" + statusSale);
        Log.d("SINN", "PosResponse39==" + PosResponse39);
        ////20180723 HGC slip error following host error response
        if ((statusSale.equalsIgnoreCase("12") || statusSale.equalsIgnoreCase("22") || statusSale.equalsIgnoreCase("32")) && PosResponse39.equalsIgnoreCase("52")) {
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText(" ");
            footerErrorMsg.setText("ผู้ใช้สิทธิ์อายุมากกว่า 7ปี\nกรุณาเลือกเมนูอื่น");
        } else {
            errorMsg.setVisibility(View.VISIBLE);
            if (PosResponse39 != null) {
                errorMsg.setText(RespCode.ResponseMsgGHC(PosResponse39));
                footerErrorMsg.setText("*ติดต่อ Call Center \n กรมบัญชีกลาง 022706400");
            }
        }
////END 20180723 HGC slip error following host error response

        setMeasureHGCEr();

        final Date dateTime = new Date();
//        final DateFormat dateFormat1 = new SimpleDateFormat("yyyy");
        String year = new SimpleDateFormat("yy").format(dateTime);  // Paul_20180706
        final String second = data[12 - 1].substring(4, 6);
        final String minute = data[12 - 1].substring(2, 4);
        final String hour = data[12 - 1].substring(0, 2);
        final String mount = data[13 - 1].substring(0, 2);
        final String date1 = data[13 - 1].substring(2, 4);

//        String PosApprovalCode = BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9);
//        String PosYYMMDD = year+mount+date1;
//        String PosHHMMSS = hour+minute+second;
//        String PosResponse39 = BlockCalculateUtil.hexToString(data[39 - 1]);


        if (typeInterface != null) {
            invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
            TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareActivity.this, "GHC");
            MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareActivity.this, "GHC");
            System.out.printf("utility:: invoiceNumber = %s ,TERMINAL_ID=%s,MERCHANT_NUMBER=%s \n", invoiceNumber, TERMINAL_ID, MERCHANT_NUMBER);
            posInterfaceActivity.PosInterfaceWriteField("01", "000xxxxx");   // Approval Code
//            posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(PosResponse39));   // Response Message
            posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(PosResponse39));   // Response Message

            posInterfaceActivity.PosInterfaceWriteField("65", "");   // Invoice Number
            posInterfaceActivity.PosInterfaceWriteField("16", "");   // Terminal ID
            posInterfaceActivity.PosInterfaceWriteField("D1", "");   // Merchant ID
            posInterfaceActivity.PosInterfaceWriteField("03", year + mount + date1);   // Date YYMMDD
            posInterfaceActivity.PosInterfaceWriteField("04", hour + minute + second);   // Time HHMMSS
/*
// Paul_20180719
//            String szMSG;
            if (statusSale.substring(1).equalsIgnoreCase("1")) {
//                posInterfaceActivity.PosInterfaceWriteField("30", cardId.getIdCard().replace(" ",""));   // Card No
                szMSG=cardId.getIdCard().replace(" ","").substring(0,1)+" "+cardId.getIdCard().replace(" ","").substring(1,4 )+"X"+" "+"XXXX"+cardId.getIdCard().replace(" ","").substring(9,10 )+" "+cardId.getIdCard().replace(" ","").substring(10,12 )+" "+cardId.getIdCard().replace(" ","").substring(12,13 );
            } else if (statusSale.substring(1).equalsIgnoreCase("2")) {
//                posInterfaceActivity.PosInterfaceWriteField("30", idCardCd);   // Card No
                szMSG=idCardCd.substring(0,1)+" "+idCardCd.substring(1,4 )+"X"+" "+"XXXX"+idCardCd.substring(9,10 )+" "+idCardCd.substring(10,12 )+" "+idCardCd.substring(12,13 );
            } else {
//                posInterfaceActivity.PosInterfaceWriteField("30", idForeigner);   // Card No
                szMSG=idForeigner.substring(0,1)+" "+idForeigner.substring(1,4 )+"X"+" "+"XXXX"+idForeigner.substring(9,10 )+" "+idForeigner.substring(10,12 )+" "+idForeigner.substring(12,13 );
            }
*/
            posInterfaceActivity.PosInterfaceWriteField("30", szMSG);   // Card No
            posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode, PosResponse39);


//            receivedDataFamily(data[39 -1]);

            Utility.customDialogAlertAuto(this, "ไม่สามารถทำรายการได้  " + PosResponse39);
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    doPrinter(getBitmapFromView(settlementHgcErLinearLayout));
                }
            });
        } else {
            doPrinter(getBitmapFromView(settlementHgcErLinearLayout));
        }
    }

    private void doPrinter(Bitmap slip) {
        cardManager = MainApplication.getCardManager();
        printer = cardManager.getInstancesPrint();
        bitmapOld = slip;

        new Thread() {
            @Override
            public void run() {
                try {
                    printer.initPrinter();
                    //This interface is used for set the gray level of the printing
                    //MIN is 0,Max is 4
                    //The level bigger, the speed of print is smaller
                    printer.setPrinterGray(2);
                    printer.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Intent intent = new Intent(CalculateHelthCareActivity.this, MenuServiceListActivity.class);
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
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setMeasureHGC() {
        hgcSaleView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleView.layout(0, 0, hgcSaleView.getMeasuredWidth(), hgcSaleView.getMeasuredHeight());
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
                doPrinter(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    //Paul_20180714
    Handler pos_Handler = new Handler() {

        public void handleMessage(Message msg) {
            pos_Handler.removeMessages(0);      // Paul_20180720
            submitAmount();
        }
    };

    @Override
    public void onBackPressed() {
        System.out.printf("utility:: %s onBackPressed \n", TAG);
        finish();
    }
}
