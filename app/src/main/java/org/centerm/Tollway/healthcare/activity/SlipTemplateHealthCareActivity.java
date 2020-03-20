package org.centerm.Tollway.healthcare.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import org.centerm.Tollway.activity.CalculatePriceActivity;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;

import io.realm.Realm;

// Paul_20180709
public class SlipTemplateHealthCareActivity extends SettingToolbarActivity {

    private final String TAG = "SlipHelthCare";

    private Realm realm = null;

    private CountDownTimer timer = null;

    /**
     * Slip
     */
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
    //    private TextView refNoLabel = null;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView typeLabel = null;
    //    private TextView typeCardLabel = null;
    private TextView nameEngLabel = null;       // Paul_20180711
    private TextView cardNoLabel = null;
    private TextView apprCodeLabel = null;
    private TextView comCodeLabel = null;
    private TextView amtThbLabel = null;

    //    private Button printBtn;
    private AidlPrinter printDev = null;
    private LinearLayout slipLinearLayout;
    private CardManager cardManager = null;

    private int saleId;
    private String typeSlip;
    private View printFirst;

    private boolean statusOutScress = false;

    private AidlPrinterStateChangeListener.Stub callBackPrint = null;
    private Dialog dialogOutOfPaper;
    private Button okBtn;

    private Bitmap bitmapOld = null;
    private TextView msgLabel;
    private View texView;
    private FrameLayout comCodeFragmentAuto;
//    private FrameLayout comCodeFragment;

    /**
     * Interface
     */
    private PosInterfaceActivity posInterfaceActivity;
    private String typeInterface;
    private String szDateOrg = null;
    private String szTimeOrg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_slip_sale_hgc);
        initData();
        initWidget();
        initBtnExit();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
//            saleId = bundle.getInt(CalculateHelthCareActivity.KEY_CALCULATE_ID_HGC);
            saleId = bundle.getInt(CalculateHelthCareActivityNew.KEY_CALCULATE_ID_HGC); //SINN 20181015 SINN GHC UI
            typeSlip = bundle.getString(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID);
            typeInterface = bundle.getString(MenuServiceListActivity.KEY_TYPE_INTERFACE); //20180712 SINN fix rs232 void not print.
        }
        posInterfaceActivity = MainApplication.getPosInterfaceActivity();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        realm = Realm.getDefaultInstance();
        cardManager = MainApplication.getCardManager();
        cardManager.abortPBOCProcess();
        printDev = cardManager.getInstancesPrint();
        customDialogOutOfPaper();

//        printBtn = findViewById(R.id.printBtn);
        slipLinearLayout = findViewById(R.id.settlementLinearLayout);
        bankImage = findViewById(R.id.bankImage);
        bank1Image = findViewById(R.id.bank1Image);
        merchantName1Label = findViewById(R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2Label = findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3Label = findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        tidLabel = findViewById(R.id.tidLabel);
        midLabel = findViewById(R.id.midLabel);
        traceLabel = findViewById(R.id.traceNoLabel);
        systrcLabel = findViewById(R.id.systrcLabel);
        batchLabel = findViewById(R.id.batchLabel);
//        refNoLabel = findViewById(R.id.refNoLabel);
        dateLabel = findViewById(R.id.dateLabel);
        timeLabel = findViewById(R.id.timeLabel);
        typeLabel = findViewById(R.id.typeSaleLabel);
//        typeCardLabel = findViewById(R.id.typeCardLabel);
        nameEngLabel = findViewById(R.id.nameEngLabel); // Paul_20180711
        cardNoLabel = findViewById(R.id.cardNoLabel);
        apprCodeLabel = findViewById(R.id.apprCodeLabel);
        comCodeLabel = findViewById(R.id.comCodeLabel);
        amtThbLabel = findViewById(R.id.amountLabel);
//        comCodeFragment = findViewById(R.id.comCodeFragment);

//        printBtn.setOnClickListener(this);
//        printBtn.setEnabled(true);
        selectHealthCareSALE();
    }

    public void TellToPosMatching() {
        String szMSG = new String();
        String YY = "";
        String MM = "";
        String DD = "";

        posInterfaceActivity.PosInterfaceWriteField("01", apprCodeLabel.getText().toString());   // Approval Code
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("00"));   // Response Message TX NOT FOUND
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));   // Response Message TX NOT FOUND

        szMSG = traceLabel.getText().toString();
        posInterfaceActivity.PosInterfaceWriteField("65", szMSG);   // Invoice Number
        //  posInterfaceActivity.PosInterfaceWriteField("D3",refNoLabel.getText().toString());  //Reference No
        posInterfaceActivity.PosInterfaceWriteField("16", tidLabel.getText().toString());   //tid
        posInterfaceActivity.PosInterfaceWriteField("D1", midLabel.getText().toString());//mid


        //  Log.d(TAG, "TellToPosMatching typeHost :"+typeHost);
        Log.d(TAG, "TellToPosMatching szDateOrg :" + szDateOrg);
        Log.d(TAG, "TellToPosMatching szDateOrg :" + szTimeOrg);


        szMSG = szDateOrg;
        DD = szMSG.substring(6, 8);
        MM = szMSG.substring(4, 6);
        YY = szMSG.substring(2, 4);

        posInterfaceActivity.PosInterfaceWriteField("03", YY + MM + DD);  //yymmdd

        posInterfaceActivity.PosInterfaceWriteField("04", szTimeOrg);  //hhmmss


        //  posInterfaceActivity.PosInterfaceWriteField("F1",szkey_interface_f1_pos);
//        posInterfaceActivity.PosInterfaceWriteField("F1","GHC");

        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, "00");


    }


    private void selectHealthCareSALE() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        TransTemp healthCareDB = realm.where(TransTemp.class).equalTo("id", saleId).findFirst();
        Log.d(TAG, "selectSALE: " + healthCareDB.getCardNo());
        setHealthDataView(healthCareDB);
//            setHealthDataViewAuto(healthCareDB);
    }

    private void setHealthDataView(TransTemp item) {

//        Preference.getInstance(SlipTemplateHealthCareActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_TMS, item.getId());
//        Preference.getInstance(SlipTemplateHealthCareActivity.this).setValueString(Preference.KEY_SETTLE_TYPE_TMS, typeSlip);
        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText(item.getTid());
        midLabel.setText(item.getMid());
        traceLabel.setText(item.getEcr());
// sinn_20180711 start
        Log.d(TAG, "item.getEcr() :" + item.getEcr());
        Preference.getInstance(this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_GHC, Integer.valueOf(item.getEcr()));

        szDateOrg = item.getTransDate();
        szTimeOrg = item.getTransTime();
// sinn_20180711 end

        String day = item.getTransDate().substring(6, 8);
        String mount = item.getTransDate().substring(4, 6);
        String year = item.getTransDate().substring(2, 4);

        dateLabel.setText(day + "/" + mount + "/" + year);
// Paul_20180720
//        String time = item.getTransTime().substring(4, 6);
//        String min = item.getTransTime().substring(2, 4);
//        String sec = item.getTransTime().substring(0, 2);
//        timeLabel.setText(time + ":" + min + ":" + sec);
        String time = item.getTransTime().substring(0, 2);      // Paul_20180723
        String min = item.getTransTime().substring(2, 4);
        String sec = item.getTransTime().substring(4, 6);       // Paul_20180723
        timeLabel.setText(time + ":" + min + ":" + sec);


        systrcLabel.setText(item.getTraceNo());
        Log.d(TAG, "setDataView getTraceNo: " + item.getTraceNo());
        batchLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

//        refNoLabel.setText(item.getRefNo());

        if (item.getVoidFlag().equals("N")) {
            typeLabel.setText("ผู้ป่วยนอกทั่วไป");    //           typeLabel.setText("SALE");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getAmount()))));
        } else {
            typeLabel.setText("ผู้ป่วยนอกทั่วไป");          //     typeLabel.setText("VOID");
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(item.getAmount()))));
        }
//        typeCardLabel.setText(CardPrefix.getTypeCardName(item.getCardNo()));
//        nameEngLabel.setText(item.getEngFName());       // Paul_20180711
// Paul_20180720
        if (item.getTypeSale().substring(1, 2).equalsIgnoreCase("1")) {
            nameEngLabel.setText(item.getEngFName());
        } else {
            nameEngLabel.setText(null);
        }
// Paul_20180712 Start
//        cardNoLabel.setText(item.getCardNo());
        String szMSG = null;
        String idCardCd;
        if (item.getTypeSale().substring(1).equalsIgnoreCase("1")) {        // Paul_20180712
            idCardCd = item.getIdCard();
        } else {
            idCardCd = item.getCardNo();
        }
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        cardNoLabel.setText(szMSG);     // Paul_20180712
// Paul_20180712 End

        apprCodeLabel.setText(item.getApprvCode());
        comCodeLabel.setText(item.getComCode());


        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2Label = findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3Label = findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));


//        comCodeFragment.setVisibility(View.GONE);
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //SINN 20180710 send RS232 void.
//                   if(finalInflag ==1)
//                if (typeInterface != null)
//                    TellToPosMatching();
                //END SINN 20180710 send RS232 void.
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                    Utility.customDialogAlertAuto( SlipTemplateActivity.this, "transactionTimeOut" );


                    System.out.printf("utility:: BBBBBBBBBBBBBBBBBBB 00000000000000002 \n");
                    TellToPosMatching();
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            System.out.printf("utility:: %s doPrinting Befor 075 \n",TAG);
                            //doPrinting(getBitmapFromView(slipLinearLayout));
//                            Intent intent = new Intent( SlipTemplateActivity.this, MenuServiceListActivity.class );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                            startActivity( intent );
//                            finish();
//                            overridePendingTransition( 0, 0 );
                        }
                    });
                    autoPrint();
                } else {
                    System.out.printf( "utility:: %s doPrinting Befor 076 \n", TAG );
                    doPrinting( getBitmapFromView( slipLinearLayout ) );
                }
                autoPrint();
            }
        }.start();
    }


    private void autoPrint() {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        Utility.customDialogSelect(SlipTemplateHealthCareActivity.this, "พิมพ์ซ้ำ", new Utility.onTouchoutSide() {

            @Override
            public void onClickImage(Dialog dialog) {
                System.out.printf("utility:: %s doPrinting Befor 073 \n",TAG);
                //doPrinting(getBitmapFromView(slipLinearLayout));
                Intent intent = new Intent(SlipTemplateHealthCareActivity.this, MenuServiceListActivity.class);
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
                Intent intent = new Intent(SlipTemplateHealthCareActivity.this, MenuServiceListActivity.class);
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

    public void doPrinting(Bitmap slip) {
        bitmapOld = slip;
        statusOutScress = true;     // Paul_20180711
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
                            Log.d(TAG, "onPrintFinish: ");
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    printBtn.setEnabled(true);
//                                }
//                            });
                            if (statusOutScress) {
//                                Intent intent = new Intent(SlipTemplateHealthCareActivity.this, MenuServiceListActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                                overridePendingTransition(0, 0);
                            } else {
                                if (timer != null) {
                                    timer.cancel();
                                    timer.start();
                                }
                            }
                        }

                        @Override
                        public void onPrintError(int i) throws RemoteException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onPrintError: ");
                                    msgLabel.setText("เกิดข้อผิดพลาด เช่นแบตเตอรี่อ่อน");
                                    dialogOutOfPaper.show();
                                }
                            });

                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {
                            Log.d(TAG, "onPrintOutOfPaper: ");
                            if (!statusOutScress) {
                                timer.cancel();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    dialogOutOfPaper.show();
                                }
                            });
                        }
                    });
//                    int ret = printDev.printBarCodeSync("asdasd");
//                    Log.d(TAG, "after call printData ret = " + ret);

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

//    @Override
//    public void onClick(View v) {
//        if (v == printBtn) {
//            statusOutScress = true;
//            printBtn.setEnabled(false);
//            //doPrinting(getBitmapFromView(slipLinearLayout));
//            if (timer != null) {
//                timer.cancel();
//            }
//        }
//    }

    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180919
        View view = dialogOutOfPaper.getLayoutInflater().inflate(R.layout.dialog_custom_printer, null);//K.GAME 180919
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180919
        dialogOutOfPaper.setContentView(view);//K.GAME 180919
        dialogOutOfPaper.setCancelable(false);//K.GAME 180919

//        dialogOutOfPaper = new Dialog(this);
//        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogOutOfPaper.setContentView( R.layout.dialog_custom_printer);
//        dialogOutOfPaper.setCancelable(false);
//        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.printf("utility:: %s doPrinting Befor 074 \n",TAG);
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
