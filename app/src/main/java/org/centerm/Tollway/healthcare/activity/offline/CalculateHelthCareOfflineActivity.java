package org.centerm.Tollway.healthcare.activity.offline;

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
import android.support.v7.app.AppCompatActivity;
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
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.healthcare.activity.IDActivity;
import org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity;
import org.centerm.Tollway.healthcare.model.CardId;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

import static org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity.KEY_ID_CARD_CD;
import static org.centerm.Tollway.utility.Utility.getLength62;

//import org.centerm.Tollway.healthcare.database.SaleOfflineHealthCare;    // Paul_20180724_OFF

public class CalculateHelthCareOfflineActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "CalculateHelthCareOffln";

//    public static final String KEY_CALCULATE_ID_HGC = CalculateHelthCareOfflineActivity.class.getName() + "_key_calcuate_id_hgc";

    private Realm realm;

    private CardManager cardManager = null;
    private AidlPrinter printer = null;

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
    private Dialog dialogOutOfPaper;
    private Button okBtn;
    private TextView msgLabel;
    private Bitmap bitmapOld;
    private String isOffline;
    private TextView tv_calculate_label01;//K.GAME 180911 intent text title
    private TextView tv_calculate_label02;//K.GAME 180911 intent text title

    int inKeyCounter = 0;   ////20180723 SINNN fixed double click.
    DecimalFormat decFormat; //20180812 SINN BIG AMOUNT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_calculate_helth_offline);
        setContentView(R.layout.activity_calculate_helth_new);   //20180812 SINN BIG AMOUNT
        realm = Realm.getDefaultInstance();
        cardManager = MainApplication.getCardManager();
        initData();
        initWidget();
        ////K.GAME 180910 change UI insert idcaed
        tv_calculate_label02 = findViewById(R.id.tv_calculate_label02);
        tv_calculate_label02 = findViewById(R.id.tv_calculate_label02);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tv_calculate_label02.setText(bundle.getString("tv_insertIdCard_01"));//K.GAME 180910 change UI insert idcaed
            tv_calculate_label02.setText(bundle.getString("tv_insertIdCard_02"));//K.GAME 180910 change UI insert idcaed
        }//END K.GAME 180910 change UI insert idcaed
    }

    private void initData() {
// Paul_20180705
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getParcelable(IDActivity.KEY_CARD_ID_DATA) != null) {
                cardId = bundle.getParcelable(IDActivity.KEY_CARD_ID_DATA);
                String[] name = cardId.getThName().trim().split(" ");

                for (String n : name) {
                    Log.d(TAG, "initData: cardId : " + cardId.getIdCard() + "\n cardId : " + n);
                }
                Log.d(TAG, "initData: Thai = " + BlockCalculateUtil.convertStringToHex(cardId.getThName()));
                System.out.printf("utility:: KKKKKKKK 000 ardId.getIdCard() = %s \n", cardId.getIdCard());
            }
            if (bundle.getString(KEY_ID_CARD_CD) != null) {
                idCardCd = bundle.getString(KEY_ID_CARD_CD);
                System.out.printf("utility:: KKKKKKKKKK 001 idCardCd = %s \n", idCardCd);
            }
            if (bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE) != null) {
                statusSale = bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE);
                Log.d(TAG, "statusSale: " + statusSale);
                if (bundle.getString(MedicalTreatmentActivity.KEY_ID_FOREIGNER_NUMBER) != null)
                    idForeigner = bundle.getString(MedicalTreatmentActivity.KEY_ID_FOREIGNER_NUMBER);
                System.out.printf("utility:: KKKKKKKKKK 002 idForeigner = %s \n", idForeigner);
            }

        }

    }

    public void initWidget() {
        tv_calculate_label01 = findViewById(R.id.tv_calculate_label01);//K.GAME 180911 Intent text title
        idCardLabel = findViewById(R.id.idCardLabel);

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
//        dotClickFrameLayout.setOnClickListener(this);   //20180812 SINN BIG AMOUNT

        deleteClickFrameLayout.setOnClickListener(this);
        sureClickFrameLayout.setOnClickListener(this);
//        exitClickFrameLayout.setOnClickListener(this);   //20180812 SINN BIG AMOUNT
        customDialogWaiting();
        customDialogOutOfPaper();
        setViewSettlementHGC();

        decFormat = new DecimalFormat("##,###,##0.00");

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
        Log.d(TAG, "submitAmount: " + numberPrice);
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
            setDataSlipOffline();
            setMeasureHGC();
//            sendDataSaleTest(priceLabel.getText().toString());
        } else {
            Utility.customDialogAlert(CalculateHelthCareOfflineActivity.this, "กรุณาใส่จำนวนเงิน", new Utility.OnClickCloseImage() {
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
//        if (numberPrice.length() < 8) {
        if (numberPrice.length() < 9) {  //20180812 SINN BIG AMOUNT
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
                cardManager.abortPBOCProcess();
                finish();
            } else if (v == deleteClickFrameLayout) {
                numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                if (numberPrice.length() == 0) {
                    numberPrice = "";
                    priceLabel.setText("0.00");
                }
            } else if (v == sureClickFrameLayout) {
                //20180723 SINNN fixed double click.
                if (!numberPrice.isEmpty())
                    sureClickFrameLayout.setEnabled(false);
                submitAmount();
            }
        }

        if (!numberPrice.isEmpty()) {
//            priceLabel.setText(numberPrice);
            priceLabel.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
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
                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        if (numberPrice.equalsIgnoreCase("") || numberPrice == null) {
                            Log.d(TAG, "clickCal: if");
                            numberPrice = "";
                            priceLabel.setText("0.00");
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
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }

    /**
     * Zone requestData
     */
    private void sendDataSaleFamily(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10);  //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("O1", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);

        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(cardId.getIdCard().trim().replace(" ", ""));
    } // 11

    private void sendDataSaleMinSeven(String amount) {
        Date date = new Date();
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen(cardId.getIdCard().replace(" ", ""), 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("O2", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String[] name = cardId.getThName().trim().split(" ");
        String nameReal = name[1] + "#" + name[3];
        nameReal = BlockCalculateUtil.convertStringToHex(nameReal);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum + nameReal;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idCardCd);
    } // 12

    private void sendDataSaleForeigner(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("O3", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idForeigner);
    } //13

    private void sendDataSaleNoCard(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("B", 50);
        ref3 = CardPrefix.calSpenLen("O4", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idForeigner);
    } //14


    private void sendDataSaleKidneyFamily(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("O1", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(cardId.getIdCard().trim().replace(" ", ""));
    }//21

    private void sendDataSaleKidneyMinSeven(String amount) {
        Date date = new Date();
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen(cardId.getIdCard().replace(" ", ""), 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("O2", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String[] name = cardId.getThName().trim().split(" ");
        String nameReal = name[1] + "#" + name[3];
        nameReal = BlockCalculateUtil.convertStringToHex(nameReal);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum + nameReal;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idCardCd);
    } // 22

    private void sendDataSaleKidneyForeigner(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("O3", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idForeigner);
    } //23

    private void sendDataSaleKidneyNoCard(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("H", 50);
        ref3 = CardPrefix.calSpenLen("O4", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idForeigner);
    } // 24


    private void sendDataSaleCancerFamily(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
//        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("O1", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(cardId.getIdCard().trim().replace(" ", ""));
    }//31

    private void sendDataSaleCancerMinSeven(String amount) {
        Date date = new Date();
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen(cardId.getIdCard().replace(" ", ""), 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("O2", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String[] name = cardId.getThName().trim().split(" ");
        String nameReal = name[1] + "#" + name[3];
        nameReal = BlockCalculateUtil.convertStringToHex(nameReal);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum + nameReal;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idCardCd);
    } // 32

    private void sendDataSaleCancerForeigner(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("O3", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idForeigner);
    } //33

    private void sendDataSaleCancerNoCard(String amount) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TERMINAL_ID = CardPrefix.getTerminalId(CalculateHelthCareOfflineActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(CalculateHelthCareOfflineActivity.this, "GHC");
        invoiceNumber = CardPrefix.getInvoice(CalculateHelthCareOfflineActivity.this, "GHC");
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//        String amountReal = decimalFormat.format(Double.valueOf(amount));
        String amountReal = amount.replaceAll(",", "");  //20180812 SINN BIG AMOUNT

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
        mBlockDataSend[11 - 1] = CardPrefix.calLen(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"), 6);
        Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
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
        String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        msgL = "00000340";
        terVer = "00000001";
        msgVer = "0008";
        terCode = "8015";
        batchNo = CardPrefix.calLen(batch, 8);
        refNo = "00000000";
//        comCode = CardPrefix.calSpenLen("HCG13814", 10);
        comCode = CardPrefix.calSpenLen(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TAG_1001_HC), 10); //20180814 SINN  KEY_TAG_1001_HC for comm with GHC.


        ref1 = CardPrefix.calSpenLen("", 50);
        ref2 = CardPrefix.calSpenLen("S", 50);
        ref3 = CardPrefix.calSpenLen("O4", 50);
        tDate = dateFormat.format(date);
        randomData = CardPrefix.calSpenLen("", 2);
        tCERT = CardPrefix.calSpenLen("", 14);
        cSum = CardPrefix.calSpenLen("", 8);
        String m63 = msgL + terVer + msgVer + terCode + batchNo + refNo + comCode + ref1 + ref2 + ref3 + tDate + randomData + tCERT + cSum;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf(m63.length())) + BlockCalculateUtil.getHexString(m63);
        TPDU = CardPrefix.getTPDU(CalculateHelthCareOfflineActivity.this, "GHC");
        saveDataSale(idForeigner);
    } // 34


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

    private void setDataSlipOffline() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        DateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00"); // Paul_20180711_new
        dateHgcLabel.setText(dateFormat.format(date));
        timeHgcLabel.setText(dateTimeFormat.format(date));
        midHgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        tidHgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        //   systrcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC), 6));
        //20180828 SINN OFFLINE systrc not same upload on settlement
        Integer insystc = Integer.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC));
        systrcLabel.setText(CardPrefix.calLen(String.valueOf(insystc - 1), 6));
        traceNoLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL), 6));
        if (statusSale.substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (statusSale.substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
// Paul_20180713
        String szMSG = null;
//        idCardCd = cardId.getIdCard();
//        System.out.printf("utility:: KKKKKKKKKK 003 idCardCd = %s \n",idCardCd);
//            "3 1011 00015 31 4""
//        szMSG=idCardCd.substring(0,1)+" "+idCardCd.substring(2,5 )+"X"+" "+"XXXX"+idCardCd.substring(11,12 )+" "+idCardCd.substring(13,15 )+" "+idCardCd.substring(16,17 );

        if (statusSale.substring(1, 2).equalsIgnoreCase("1")) {
            idCardCd = cardId.getIdCard();
            if (idCardCd.length() < 17) {
                int i;
                for (i = idCardCd.length(); i < 17; i++) {
                    idCardCd += " ";
                }
            }
            System.out.printf("utility:: KKKKKKKKKK 003 idCardCd = %s \n", idCardCd);
            szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(2, 5) + "X" + " " + "XXXX" + idCardCd.substring(11, 12) + " " + idCardCd.substring(13, 15) + " " + idCardCd.substring(16, 17);
        } else {
//            idCardCd = cardId.getAddress();
            if (idCardCd.length() < 13) {
                int i;
                for (i = idCardCd.length(); i < 13; i++) {
                    idCardCd += " ";
                }
            }
            System.out.printf("utility:: KKKKKKKKKK 005 idCardCd = %s \n", idCardCd);
            szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        }

//        cardId
//        if(idCardCd==null)
//            return;

        ////SINN 20180711 ADD THAI ID  SET MASK
// Paul_20180712 Start
//        cardNoLabel.setText(item.getCardNo());
// Paul_20180712 End
//        szMSG=idCardCd.substring(0,1)+" "+idCardCd.substring(1,4 )+"X"+" "+"XXXX"+idCardCd.substring(9,10 )+" "+idCardCd.substring(10,12 )+" "+idCardCd.substring(12,13 );
//        szMSG=idCardCd.substring(0,1)+" "+idCardCd.substring(1,4 )+"X"+" "+"XXX"+idCardCd.substring(9,13 );
        //END SINN 20180711 ADD THAI ID  SET MASK
// Paul_20180712 Start
// Paul_20180712 End

        if (statusSale.substring(1).equalsIgnoreCase("2")) {
//            cardNoLabel.setText(idCardCd);
            cardNoLabel.setText(szMSG);     // Paul_20180712
//            nameEngLabel.setText(cardId.getEngFName());     // Paul_20180705
            nameEngLabel.setText(null);     // Paul_20180720
        } else if (statusSale.substring(1).equalsIgnoreCase("1")) {
//            cardNoLabel.setText(cardId.getIdCard());
            cardNoLabel.setText(szMSG);     // Paul_20180712
            nameEngLabel.setText(cardId.getEngFName());     // Paul_20180705
        } else {
//            cardNoLabel.setText(idForeigner);
            cardNoLabel.setText(szMSG);     // Paul_20180712
            nameEngLabel.setText(null);     // Paul_20180705
        }
//        nameEngLabel.setText(cardId.getEngFName());   // Paul_20180705


        apprCodeLabel.setText("OFFLINE");
//        comCodeLabel.setText("HCG13814");
        comCodeLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAG_1001_HC));
        batchHgcLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

//                amountLabel.setText(getString(R.string.slip_pattern_amount,decimalFormat.format(Double.valueOf(priceLabel.getText().toString()))));

        //20180812 SINN BIG AMOUNT
        amountLabel.setText(priceLabel.getText().toString());

        Log.d(TAG, "setDataSlipOffline()" + amountLabel.getText().toString());
//        Log.d(TAG,"setDataSlipOffline()"+getString(R.string.slip_pattern_amount,decimalFormat.format(Double.valueOf(priceLabel.getText().toString()))));

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
                doPrinter(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

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
//                            Intent intent = new Intent(CalculateHelthCareOfflineActivity.this, MenuServiceListActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                            overridePendingTransition(0, 0);
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

    /**
     * Zone Save Database
     */
    private void saveDataSale(final String cardNumber) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                DateFormat dateTimeFormat = new SimpleDateFormat("HHmmss");

                String tid = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC);
                String mid = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC);
                String batch = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
                String invoice = Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);

                DecimalFormat decimalFormat = new DecimalFormat("###0.00");
                Number currentId = realm.where(TransTemp.class).max("id");  // Paul_20180724_OFF
                int nextId;
                if (currentId == null) {
                    nextId = 1;
                    saleId = nextId;
                } else {
                    nextId = currentId.intValue() + 1;
                    saleId = nextId;
                }
                TransTemp healthCareDB = realm.createObject(TransTemp.class, nextId);   // Paul_20180724_OFF

                healthCareDB.setDe2(mBlockDataSend[2 - 1]);
                healthCareDB.setDe3(mBlockDataSend[3 - 1]);
                healthCareDB.setDe4(mBlockDataSend[4 - 1]);
                healthCareDB.setDe11(mBlockDataSend[11 - 1]);
                healthCareDB.setDe22(mBlockDataSend[22 - 1]);
                healthCareDB.setDe24(mBlockDataSend[24 - 1]);
                healthCareDB.setDe25(mBlockDataSend[25 - 1]);
                healthCareDB.setDe35(mBlockDataSend[35 - 1]);
                healthCareDB.setDe41(mBlockDataSend[41 - 1]);
                healthCareDB.setDe42(mBlockDataSend[42 - 1]);
                healthCareDB.setDe52(mBlockDataSend[52 - 1]);
                healthCareDB.setDe62(mBlockDataSend[62 - 1]);
                healthCareDB.setDe63(mBlockDataSend[63 - 1]);
//                healthCareDB.setType("SALE");             // Paul_20180724_OFF
                healthCareDB.setHostTypeCard("GHC");        // Paul_20180724_OFF
                healthCareDB.setTransStat("SALE");          // Paul_20180724_OFF
                healthCareDB.setVoidFlag("N");            // Paul_20180724_OFF
                healthCareDB.setGhcoffFlg("Y");             // Paul_20180724_OFF

                int traceNo = Integer.parseInt(CardPrefix.geTraceId(CalculateHelthCareOfflineActivity.this, "GHC"));
                healthCareDB.setTraceNo(CardPrefix.calLen(String.valueOf(traceNo - 1), 6));
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
                int inV = Integer.parseInt(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL));
                inV = inV + 1;
                Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
                healthCareDB.setConditionCode(mBlockDataSend[25 - 1]);
                healthCareDB.setDe63Sale(mBlockDataSend[63 - 1]);
                healthCareDB.setTransDate(dateFormat.format(date));
                healthCareDB.setTransTime(dateTimeFormat.format(date));
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
                healthCareDB.setApprvCode("OFFLINE");
                healthCareDB.setRefNo(refNo);
                healthCareDB.setTypeSale(statusSale);
//                healthCareDB.setAmount(decimalFormat.format(Double.valueOf(priceLabel.getText().toString())));

                String amountReal = priceLabel.getText().toString().replaceAll(",", "");  //20180812 SINN BIG AMOUNT
                healthCareDB.setAmount(amountReal);   //20180812 SINN BIG AMOUNT

                realm.insert(healthCareDB);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {


                if (!Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                    merchantName1HgcLabel.setText(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_MERCHANT_1));

                if (!Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                    merchantName2HgcLabel.setText(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_MERCHANT_2));

                if (!Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                    merchantName3HgcLabel.setText(Preference.getInstance(CalculateHelthCareOfflineActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                Preference.getInstance(CalculateHelthCareOfflineActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_GHC, Integer.valueOf(invoiceNumber)); //PAUL_20180714
                doPrinter(getBitmapFromView(settlementHgcLinearLayout));
                autoPrint();
// Paul_20180718
//                Toast.makeText(CalculateHelthCareOfflineActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                saleId = 0;
                Log.d(TAG, "onError: " + error.getMessage());
// Paul_20180718
//                Toast.makeText(CalculateHelthCareOfflineActivity.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void autoPrint() {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        Utility.customDialogSelect(CalculateHelthCareOfflineActivity.this, "พิมพ์ซ้ำ", new Utility.onTouchoutSide() {

            @Override
            public void onClickImage(Dialog dialog) {
                doPrinter(getBitmapFromView(settlementHgcLinearLayout));
                Intent intent = new Intent(CalculateHelthCareOfflineActivity.this, MenuServiceListActivity.class);
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
                Intent intent = new Intent(CalculateHelthCareOfflineActivity.this, MenuServiceListActivity.class);
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

}
