package org.centerm.Tollway.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.jemv.clcommon.EMV_APPLIST;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.device.DeviceManager;

import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.core.ChangeFormat;
import org.centerm.Tollway.helper.myCardReaderHelper;
import org.centerm.Tollway.pax.ActionSearchCard;
import org.centerm.Tollway.pax.DeviceImplNeptune;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.model.Card;
import org.centerm.Tollway.pax.EUIParamKeys;
import org.centerm.Tollway.pax.InputPwdDialog;
import org.centerm.Tollway.service.OtherDetectCard;
import org.centerm.Tollway.service.serviceReadType;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.PromptMsg;
import org.centerm.Tollway.utility.Utility;

import java.net.CacheRequest;

public class CalculatePriceActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "CalculatePriceActivity";

    public static String TypeSale = "SALE";
    public static String TypeVoid = "VOID";
    public static final String MSG_CARD = "MSG_CARD";
    public static final String IC_CARD = "IC_CARD";
    public static final String RF_CARD = "RF_CARD";
    public boolean FALLBACK_HAPPEN = false;
    private Card cardNo;
    private String AMOUNT;
    private int numFallBack = 0;

    private String[] AID_list;

    public SearchCardThread searchCardThread;



    public static final String KEY_CALCUATE_ID = CalculatePriceActivity.class.getName() + "key_calcuate_id";
    public static final String KEY_TYPE_SALE_OR_VOID = CalculatePriceActivity.class.getName() + "key_type_sale_or_void";
    public static final String KEY_INTERFACE_CARDHOLDER_2 = CalculatePriceActivity.class.getName() + "_key_interface_cardholder_2";

    public static boolean pinflag = false;

    private TextView cardNoLabel = null;
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
    private TextView titleLabel = null;

    EditText cardNoBox;
    EditText ref1Box;
    EditText ref2Box;
    EditText ref3Box;
    EditText ref4Box;
    EditText ref5Box;
    EditText ref6Box;
    private String numberPrice = "";

    private Card card = null;
    private String typeCard = "";
    private String typeSale = "";
    private int typeFlag = 0;
    private boolean req_pin = false;
    private CardManager cardManager;
    private Dialog dialogWaiting;
    //private InputPwdDialog dialogPin;
    private Dialog dialogFallBack;
    private boolean PIN = false;
    private EditText pinBox;
    private Button okBtn;
    private Button cancelBtn;
    private Dialog dialogParaEndble;

    //private SearchCardThread searchCardThread;


    private Dialog dialogInsertCard = null;
//    private Dialog dialogAlert = null;
    private Dialog DialogSelect = null;
    private Dialog DialogInform = null;
    private Dialog dialogFallBackCheck;
    private TextView msgLabel;
    private ImageView closeFallBackImage;
    private boolean stateAbort = false;
    private Dialog dialogCardError;
    private TextView msgCardErrorLabel;
    private ImageView closeCardErrorImage;

    private boolean checkResCode = false;

    //for multi Application
    private Button btnApp1;
    private Button btnApp2;
    private Button btnApp3;
    private Button btnApp4;
    private Button btnApp5;



    public static EReaderType readerType = null; // 读卡类型
    Intent iDetectCard;
    private byte mode; // 寻卡模式
    public static serviceReadType serReadType = serviceReadType.getInstance();
    String trackData1;
    String trackData2;
    String trackData3;
    private static CalculatePriceActivity instance;
    private static final int READ_CARD_CANCEL = 2; // 取消读卡
    private static final int READ_CARD_ERR = 3; // 读卡失败
    private boolean supportManual = false; // 是否支持手输

    private InputPwdDialog pindialog = null;

    public static CalculatePriceActivity getinstance() {
        return instance;
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           byte tmpType = intent.getByteExtra("TYPE", (byte) -1);
           Log.i(TAG, "BroadcastReceiver, readType=" + tmpType);
           serReadType.setrReadType(tmpType);
           //System.out.printf("utility:: %s BroadcastReceiver tmpType = [%02X]\n",TAG,tmpType);
           if (tmpType == EReaderType.MAG.getEReaderType()) {
               //Device.beepPromt();
               trackData1 = intent.getStringExtra("TRK1");
               trackData2 = intent.getStringExtra("TRK2");
               trackData3 = intent.getStringExtra("TRK3");
               cardManager.setTRACK1(trackData1);
               cardManager.setTRACK2(trackData2);
               cardManager.setTRACK3(trackData3);

           }
       }

   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_price);
        cardManager = MainApplication.getCardManager();
        initData();
        initWidget();
        cardManager.initClssTrans.run();

        serReadType.setrReadType(EReaderType.DEFAULT.getEReaderType());
        //initInputOfflPwdDialog();

        loadParam();
        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());
        Log.i(TAG, "readerType 1 = " + readerType.getEReaderType());
        //initClssTrans();
        Log.i(TAG, "readerType 2 = " + readerType.getEReaderType());
        iDetectCard = new Intent(this, OtherDetectCard.class);
        iDetectCard.putExtra("readType", readerType.getEReaderType());
        iDetectCard.putExtra("iccSlot", (byte) 0);
        startService(iDetectCard);

        //接收器的动态注册，Action必须与Service中的Action一致

        registerReceiver(br, new IntentFilter("ACTION_DETECT"));

        searchCardThread = new SearchCardThread();
    }

    private void initData() {
//        typeSale = cardManager.getHostCard();
        typeFlag = cardManager.getHostFlag();

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            card = (Card) bundle.getSerializable(MenuServiceListActivity.KEY_CARD);
//            typeCard = bundle.getString(MenuServiceListActivity.KEY_TYPE_CARD);
//            Log.d(TAG, "initData: Card : " + card.getNo() + "\n typeCard : " + typeCard);
//        }
    }

    private void initWidget() {
        customDialog();
        cardNoLabel = findViewById(R.id.cardNoLabel);

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
        titleLabel = findViewById(R.id.titleLabel);

        if (card != null) {
            String cutCardStart = card.getNo().substring(0, 6);
            String cutCardEnd = card.getNo().substring(12, card.getNo().length());
            cardNoLabel.setText(cutCardStart + "XXXXXX" + cutCardEnd);
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
        customDialogSelectApp();
//        customDialogInputPin();
        customDialogFallBack();
        customDialogCheckFallBack();
        //customDialogPin();
        customDialogParameterEnable();
        customDialogCardError();
//        cardManager.setRequiredPINListener(new CardManager.RequiredPINListener() {
//
//            @Override
//            public void onRequiredPIN() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialogInputPin.show();
//                    }
//                });
//            }
//        });
        cardManager.setResponsePINListener(new CardManager.ResponsePINListener() {
            @Override
            public void onRequirePIN() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting != null)
                            dialogWaiting.dismiss();
                        //dialogPin.show();
                        }
                    });
                }

            @Override
            public void onBYPASSfail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting != null)
                            dialogWaiting.dismiss();
                    }
                });

//                cardManager.stopTransaction();
                cardManager.abortPBOCProcess();
                cardManager.startTransaction(CardManager.SALE, AMOUNT);
            }
        });

        cardManager.setNfcListener(new CardManager.NFCListener() {
            @Override
            public void onfindNFC() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting != null)
                            dialogWaiting.dismiss();

                        dialogInsertCard.show();
                    }
                });
//                cardManager.stopTransaction();
                cardManager.abortPBOCProcess();
                cardManager.startTransaction(CardManager.SALE, AMOUNT);
            }
        });

        cardManager.setCardHelperListener(new CardManager.CardHelperListener() {

            @Override
            public void onDuplicateTrans(final String msg) {

            }
            @Override
            public void onMultiApp(final int item, final String[] aid) {

            }
            @Override
            public void onGetCardHolderName(String szCardName) {

            }
            @Override
            public void onFindICCard() {

            }


            @Override
            public void onCardInfoReceived_Contactless(String CARD_NO, String NAMECARDHOLDER, String AMOUNT) {
//                cardManager.stopTransaction();

            }
            @Override
            public void onFindContactlessMultiapp() {

            }
            @Override
            public void onTansAbort() {

            }

            @Override
            public void onCardNo(String cardNo) {
                if (cardNo != null) {
                    String tmp = cardNo;
                    cardNoBox.setText(tmp);
                    dialogWaiting.dismiss();
                    typeCard = IC_CARD;
//                    cardManager.stopTransaction();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogParaEndble.show();
//                            cardManager.stopTransaction();
//                            dismissDialogAll();
                        }
                    });
                    Log.d(TAG, "onCardNo: " + cardNo);
//                    finish(); //Jeff20180628
                }else
                    Log.d(TAG, "onCardNo : NULL ");
            }

            @Override
            public void onCardInfoReceived(final Card card) {
                Log.d("CardHelperListener", "onCardInfoReceived");
                if (card != null) {
                    cardNo = card;
                    typeCard = IC_CARD;
                }
            }

            @Override
            public void onCardInfoFail() {
                Log.d(TAG, "onCardInfoFail: ");
                if (dialogWaiting != null)
                    dialogWaiting.dismiss();
                if(dialogInsertCard != null)
                    dialogInsertCard.dismiss();
//                cardManager.stopTransaction();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        cardManager.stopTransaction();
                        dialogFallBackCheck.show();
                    }
                });
            }

            @Override
            public void onTransResultFallBack() {
                Log.d(TAG, "onTransResultFallBack: ");
                if (numFallBack > 0) {
                    Log.d(TAG, "onTransResultFallBack: ");
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (dialogInsertCard != null)
                        dialogInsertCard.cancel();
//                    cardManager.stopTransaction();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogFallBack.dismiss();
//                            cardManager.stopTransaction();
                            dialogFallBackCheck.show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ++numFallBack;
//                            cardManager.stopTransaction();
                            dialogFallBackCheck.show();
                            if(numFallBack >0) {
                                FALLBACK_HAPPEN = true;
                                cardManager.setFallBackHappen();
                            }
                        }
                    });
                }
            }

            @Override
            public void onTransResulltNone() {
                Log.d(TAG, "onTransResulltNone: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        cardManager.stopTransaction();
                        dialogInsertCard.show();
                        dialogFallBackCheck.show();
                    }
                });

                if(searchCardThread.isAlive()) {
                    myCardReaderHelper.getInstance().stopPolling();
                    
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //

                }

            }

            @Override
            public void onCardTransactionUpdate(boolean isApproved, Card card) {
                Log.d(TAG, "onCardTransactionUpdate: " + isApproved + " Card : " + card);
                if (isApproved) {
//                    cardManager.stopTransaction();
                }
            }

            @Override
            public void onFindMagCard(Card card) {
                Log.d("CardHelperListener", "onCardInfoReceived");
                cardNo = card;
//                cardNoBox.setText(cardNo.getNo());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (dialogFallBack.isShowing())
                            dialogFallBack.dismiss();
                        if (dialogInsertCard.isShowing())
                            dialogInsertCard.dismiss();
//                        dialogInsertCard.dismiss();
//                        cardManager.stopTransaction();

                        if(!dialogWaiting.isShowing())
                            dialogWaiting.show();

                        cardManager.setDataSaleFallBack(AMOUNT);
                    }
                });
            }

            @Override
            public void onSwapCardIc() {
                Log.d(TAG, "onSwapCardIc: ");
//                dismissDialogAll();
//                cardManager.stopTransaction();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        customDialogServiceCode();
//                    }
//                });
            }

            @Override
            public void onSwapCardMag() {
                Log.d(TAG, "onSwapCardMag: ");
//                cardManager.abortPBOCProcess();
//                dismissDialogAll();
//                cardManager.stopTransaction();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialogInsertCard.show();
//                        setTimer(15000, 1);
//                        cardmanager.startTransaction(CardManager.sale, amountinterface);
//                    }
//                });
            }

            @Override
            public void onSwipeCardFail() {
                Log.d(TAG, "onSwipeCardFail: ");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuServiceListActivity.this);
//                        builder.setMessage("สไลด์การ์ดล้มเหลว")
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setCancelable(false)
//                                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dismissDialogAll();
//                                        dialogInsertCard.show();
//                                        cardmanager.startTransaction(CardManager.sale, amountinterface);
//                                    }
//                                });
//                        AlertDialog alert = builder.create();
//                        alert.show();
//                    }
//                });
            }

            @Override
            public void onFindCard() {
                Log.d(TAG, "onFindICCard: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogInsertCard.dismiss();
                        if(!dialogWaiting.isShowing()) {
                            dialogWaiting.show();
                        }
                    }
                });
            }

            @Override
            public void onMultiApp(final int item, EMV_APPLIST[] Aid) {
                Log.d("kang", "multi item:" + item + ",aid_list_size:" + Aid.length);
                final String [] aid = new String[Aid.length];
                AID_list = new String[Aid.length];
                for(int i = 0; i < aid.length; i++) {
                    AID_list[i] = ChangeFormat.bcd2Str(Aid[i].aid);
                    aid[i] = ChangeFormat.bcd2Str(Aid[i].appName);
                    for(int j = 0; j < aid[i].length() - 4; j++) {
                        if(aid[i].substring(j, j+4).equals("0000")) {
                            aid[i] = aid[i].substring(0, j);
                        }
                    }
                }
                Log.d(TAG, "onMultiApp: " );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        DialogSelect.show();
                        if(item == 1) {
                            DialogSelect.dismiss();
                        }
                        else if(item == 2) {
                            btnApp1.setText(BlockCalculateUtil.hexToString(aid[0]));
                            btnApp2.setText(BlockCalculateUtil.hexToString(aid[1]));
                        }else if(item == 3){
                            btnApp3.setVisibility(View.VISIBLE);
                            btnApp1.setText(BlockCalculateUtil.hexToString(aid[0]));
                            btnApp2.setText(BlockCalculateUtil.hexToString(aid[1]));
                            btnApp3.setText(BlockCalculateUtil.hexToString(aid[2]));
                        }else if(item == 4){
                            btnApp3.setVisibility(View.VISIBLE);
                            btnApp4.setVisibility(View.VISIBLE);
                            btnApp1.setText(BlockCalculateUtil.hexToString(aid[0]));
                            btnApp2.setText(BlockCalculateUtil.hexToString(aid[1]));
                            btnApp3.setText(BlockCalculateUtil.hexToString(aid[2]));
                            btnApp4.setText(BlockCalculateUtil.hexToString(aid[3]));
                        }else{
                            btnApp3.setVisibility(View.VISIBLE);
                            btnApp4.setVisibility(View.VISIBLE);
                            btnApp5.setVisibility(View.VISIBLE);
                            btnApp1.setText(BlockCalculateUtil.hexToString(aid[0]));;
                            btnApp2.setText(BlockCalculateUtil.hexToString(aid[1]));;
                            btnApp3.setText(BlockCalculateUtil.hexToString(aid[2]));
                            btnApp4.setText(BlockCalculateUtil.hexToString(aid[3]));;
                            btnApp5.setText(BlockCalculateUtil.hexToString(aid[4]));
                        }

                    }
                });
            }
        });

        cardManager.setInsertOrUpdateDatabase(new CardManager.InsertOrUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {
                Log.d(TAG, "onUpdateVoidSuccess: " + id);
            }

            @Override
            public void onInsertSuccess(int id) {
                Log.d(TAG, "onInsertSuccess: " + id);
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
                cardManager.removeTransResultAbort();
                Intent intent = new Intent(CalculatePriceActivity.this, SlipTemplateActivity.class);
                intent.putExtra(KEY_CALCUATE_ID, id);
                intent.putExtra(KEY_TYPE_SALE_OR_VOID, TypeSale);




                intent.putExtra("TVR",cardManager.get959B()[0]);
                intent.putExtra("TSI",cardManager.get959B()[1]);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        cardManager.setConnectStatusSocket(new CardManager.ConnectStatusSocket() {
            @Override
            public void onConnectTimeOut() {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(CalculatePriceActivity.this, "onConnectTimeOut");
                        }
                    });
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                /*    if (timer != null) {
                        timer.cancel();

                    }

                 */
                }
            }

            @Override
            public void onTransactionTimeOut() {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(CalculatePriceActivity.this, "onTransactionTimeOut");
                        }
                    });
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                /*    if (timer != null) {
                        timer.cancel();
                    }*/
                }
            }

            @Override
            public void onError(final String message) {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(CalculatePriceActivity.this, message);
                        }
                    });
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                }
            }

            @Override
            public void onOther() {

            }

            @Override
            public void onReceived() {

            }
            @Override
            public void onError() {

            }
        });

    }

    private void responseCodeDialog() {
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(final String response) {
                if(dialogWaiting.isShowing())
                   dialogWaiting.dismiss();

               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            checkResCode = true;
                            Utility.customDialogAlert(CalculatePriceActivity.this, response);
                        }
                    }
                });


            }

            @Override
            public void onResponseCodeSuccess() {

            }

            @Override
            public void onConnectTimeOut() {
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            Utility.customDialogAlert(CalculatePriceActivity.this, "onConnectTimeOut");
                        }
                    }
                });
            }

            @Override
            public void onTransactionTimeOut() {
                Log.d(TAG, "TransactionTimeOut: ");
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            Utility.customDialogAlert(CalculatePriceActivity.this, "onTransactionTimeOut");
                        }
                    }
                });
            }
            @Override
            public void onResponseCodeandMSG(final String response, String szCode) {

                cardManager.removeTransResultAbort();
                if (dialogWaiting.isShowing())
                    dialogWaiting.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.customDialogAlert_gotomain(CalculatePriceActivity.this, response, new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void customDialogCheckFallBack() {
        dialogFallBackCheck = new Dialog(this);
        dialogFallBackCheck.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFallBackCheck.setCancelable(false);
        dialogFallBackCheck.setContentView(R.layout.dialog_custom_alert_fall_back);
        dialogFallBackCheck.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFallBackCheck.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        closeFallBackImage = dialogFallBackCheck.findViewById(R.id.closeImage);
        okBtn = dialogFallBackCheck.findViewById(R.id.okBtn);
        closeFallBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numFallBack = 0;
                dialogFallBack.dismiss();
                if (dialogWaiting != null)
                    dialogWaiting.dismiss();
                if (dialogInsertCard != null)
                    dialogInsertCard.cancel();
                dialogFallBackCheck.dismiss();
//                cardManager.stopTransaction();

            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numFallBack >0) {
                    Log.d(TAG, "onClick IF : " + numFallBack);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogWaiting.dismiss();

                            dialogFallBackCheck.dismiss();
                            dialogFallBack.show();
                            loadParam();

                        }
                    });
                    if(searchCardThread.isAlive()) {
                        myCardReaderHelper.getInstance().stopPolling();
                        try{
                            Thread.sleep(1000);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        /*searchCardThread.setIsic(false);
                        searchCardThread.setIsrf(false);
                        searchCardThread.setIsmag(true);
                        */
                        cardManager.setReaderType(EReaderType.MAG);
                        searchCardThread.start();


                    }
                    else {

                        /*
                        searchCardThread.setIsic(false);
                        searchCardThread.setIsrf(false);
                        searchCardThread.setIsmag(true);
                        */
                        cardManager.setReaderType(EReaderType.MAG);
                        searchCardThread.start();
                    }

                } else {
                    Log.d(TAG, "onClick ELSE : " + numFallBack);
                    dialogWaiting.dismiss();
                    dialogFallBackCheck.dismiss();
                    dialogInsertCard.show();

                    if(!searchCardThread.isAlive()) {
                        cardManager.setReaderType(EReaderType.MAG_PICC);
                        searchCardThread.start();
                    }
                    else {
                        myCardReaderHelper.getInstance().stopPolling();
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                       cardManager.setReaderType(EReaderType.MAG_PICC);
                       searchCardThread.start();
                    }
                }
            }
        });
    }

    private void customDialogFallBack() {
        dialogFallBack = new Dialog(this);
        dialogFallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFallBack.setCancelable(false);
        dialogFallBack.setContentView(R.layout.dialog_custom_mag);
        dialogFallBack.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFallBack.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogFallBack.findViewById(R.id.msgLabel);
        ImageView closeImage = dialogFallBack.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numFallBack = 0;
                dialogFallBack.dismiss();
                myCardReaderHelper.getInstance().stopPolling();
                
//                cardManager.stopTransaction();
            }
        });
    }

    private void customDialogSelectApp() {
        DialogSelect = new Dialog(this);
        DialogSelect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogSelect.setCancelable(false);
        DialogSelect.setContentView(R.layout.dialog_custom_selectapp);
        DialogSelect.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogSelect.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        btnApp1 = DialogSelect.findViewById(R.id.btn_app1);
        btnApp2 = DialogSelect.findViewById(R.id.btn_app2);
        btnApp3 = DialogSelect.findViewById(R.id.btn_app3);
        btnApp4 = DialogSelect.findViewById(R.id.btn_app4);
        btnApp5 = DialogSelect.findViewById(R.id.btn_app5);

        btnApp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                dialogInsertCard.dismiss();
                Log.d("kang","btn1 clicked/aid:" + AID_list[0]);
                cardManager.selectMultiApp(0);

            }
        });
        btnApp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                dialogInsertCard.dismiss();
                Log.d("kang","btn2 clicked/aid:" + AID_list[1]);
                cardManager.selectMultiApp(1);
            }
        });
        btnApp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                dialogInsertCard.dismiss();
                Log.d("kang","btn3 clicked/aid:" + AID_list[2]);
                cardManager.selectMultiApp(2);
            }
        });
        btnApp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                dialogInsertCard.dismiss();
                Log.d("kang","btn4 clicked/aid:" + AID_list[3]);
                cardManager.selectMultiApp(3);
            }
        });
        btnApp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                dialogInsertCard.dismiss();
                Log.d("kang","btn5 clicked/aid:" + AID_list[4]);
                cardManager.selectMultiApp(4);
            }
        });
    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this, R.style.ThemeWithCorners);
        View view = dialogWaiting.getLayoutInflater().inflate(R.layout.dialog_custom_load_process, null);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setContentView(view);
        dialogWaiting.setCancelable(false);
    }

    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        }
    }

    private void submitAmount() {
        Log.d(TAG, "Start Transaction !!!!!!!!!!_JEFF");
        Log.d(TAG, "submitAmount: " + numberPrice);
        stateAbort = true;
        if (!priceLabel.getText().toString().equalsIgnoreCase("0.00") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0.") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0.0")) {

            dialogParaEndble.show();
//            cardManager.startTransaction(CardManager.READ, "0.00");


        } else {
            Utility.customDialogAlert(CalculatePriceActivity.this, "กรุณาใส่จำนวนเงิน");
        }
    }

//    private void customDialogInputPin() {
//        dialogInputPin = new Dialog(this);
//        dialogInputPin.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogInputPin.setCancelable(false);
//        dialogInputPin.setContentView(R.layout.dialog_custom_input_pin);
//        dialogInputPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogInputPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialogInputPin.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        pinBox = dialogInputPin.findViewById(R.id.pinBox);
//        okBtn = dialogInputPin.findViewById(R.id.okBtn);
//        cancelBtn = dialogInputPin.findViewById(R.id.cancelBtn);
//
//        okBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String tmp = pinBox.getText().toString();
//                if(tmp.length() == 4 || tmp.length() == 6) {
//                    cardManager.setImportPIN(pinBox.getText().toString());
//                    dialogInputPin.dismiss();
//                    dialogWaiting.show();
//                }else{
//                    Toast.makeText(CalculatePriceActivity.this, "PW length Error.(supported 4, 6 digit)", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogInputPin.dismiss();
//                cardManager.abortPBOCProcess();
//                finish();
//            }
//        });
//    }

    private void customDialog() {
        dialogInsertCard = new Dialog(this);
        dialogInsertCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInsertCard.setCancelable(false);
        dialogInsertCard.setContentView(R.layout.dialog_custom_sale);
        dialogInsertCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInsertCard.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView closeImage = dialogInsertCard.findViewById(R.id.closeImage);
        ImageView cardInsertImage = dialogInsertCard.findViewById(R.id.cardInsertImage);
        final AnimationDrawable animationDrawable = (AnimationDrawable) cardInsertImage.getBackground();
        animationDrawable.start();
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInsertCard.dismiss();
                animationDrawable.stop();

                    if(dialogWaiting.isShowing()) {
                        dialogWaiting.dismiss();
                    }

                cardManager.stopTransaction();
                myCardReaderHelper.getInstance().stopPolling();
                
            }
        });
    }

   /* private void customDialogPin() {
        Handler handler = new Handler(),;
                //dialogPin = new Dialog(this);
        dialogPin = new InputPwdDialog(this, handler, getString(R.string.entry_card_password),getString(R.string.without_password));
        dialogPin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPin.setContentView(R.layout.dialog_custom_offlinepin);
        dialogPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPin.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText pinBox = dialogPin.findViewById(R.id.pinBox);
        Button btn_ok = dialogPin.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogPin.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInsertCard.dismiss();
                dialogPin.dismiss();
                dialogWaiting.show();
                cardManager.inputofflinePIN(pinBox.getText().toString());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    */
    private void customDialogParameterEnable() {
        dialogParaEndble = new Dialog(this);
        dialogParaEndble.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogParaEndble.setCancelable(false);
        dialogParaEndble.setContentView(R.layout.dialog_custom_para_enable);
        dialogParaEndble.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogParaEndble.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        cardNoBox = dialogParaEndble.findViewById(R.id.cardNobox);
        ref1Box = dialogParaEndble.findViewById(R.id.ref1Box); //TH
        LinearLayout ref1LinearLayout = dialogParaEndble.findViewById(R.id.ref1LinearLayout);
        Button okBtn = dialogParaEndble.findViewById(R.id.okBtn);
        Button cancelBtn = dialogParaEndble.findViewById(R.id.cancelBtn);

        typeSale = "SALE";
        ref1LinearLayout.setVisibility(View.VISIBLE);
//        cardNoBox.setEnabled(false);
        ref1Box.setEnabled(false);


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogParaEndble.dismiss();
                AMOUNT = ref1Box.getText().toString();



                if(searchCardThread.isAlive() == false) {
                    searchCardThread = new SearchCardThread();
                    cardManager.setReaderType(EReaderType.MAG_PICC);
                    searchCardThread.start();
                }
                else {
                    myCardReaderHelper.getInstance().stopPolling();

                    cardManager.setReaderType(EReaderType.MAG_PICC);
                    searchCardThread.start();
                }
                //initInputOfflPwdDialog();

                cardManager.startTransaction(CardManager.SALE, AMOUNT);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogInsertCard.show();
                    }
                });
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogParaEndble.dismiss();

                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Click number: " + numberPrice);
        String[] splitter = null;
        Log.d(TAG, "onClick: " + numberPrice.contains("."));
        if (numberPrice.length() < 16) {
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
                            String tmp = priceLabel.getText().toString();
                                ref1Box.setText(tmp);
                                submitAmount();

                        }
                    } else {
                        if (!numberPrice.isEmpty())
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
            String tmp = priceLabel.getText().toString();

                tmp = tmp + ".00";
                ref1Box.setText(tmp);
                submitAmount();

        }
    }

    private void setAbortPboc() {
        cardManager.setTransResultAbortLister(new CardManager.TransResultAbortLister() {
            @Override
            public void onTransResultAbort() {
                if (!stateAbort) {
                    if(searchCardThread.isAlive()) {
                        //SearchCardThread.currentThread().stop();
                        myCardReaderHelper.getInstance().stopPolling();
                        try {
                            Thread.sleep(1000);
                        } catch ( Exception e) {
                            e.printStackTrace();
                        }
                        
                        unregisterReceiver(br);
                        stopService(iDetectCard);
                    }

                    Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogCardError.show();
                        }
                    });
                }
            }
        });
    }

    public void showerr(final int ret) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogWaiting.dismiss();
                dialogInsertCard.dismiss();
                msgCardErrorLabel.setText(PromptMsg.getErrorMsg(ret));
                dialogCardError.show();
            }
        });

    }

    public void customDialogCardError() {
        dialogCardError = new Dialog(this);
        dialogCardError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCardError.setCancelable(false);
        dialogCardError.setContentView(R.layout.dialog_custom_alert_card_error);
        dialogCardError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCardError.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgCardErrorLabel = dialogCardError.findViewById(R.id.msgLabel);
        closeCardErrorImage = dialogCardError.findViewById(R.id.closeImage);
        okBtn = dialogCardError.findViewById(R.id.okBtn);
        closeCardErrorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchCardThread.isAlive()) {
                    myCardReaderHelper.getInstance().stopPolling();
                    try {
                        Thread.sleep(1000);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                    
                    unregisterReceiver(br);
                    stopService(iDetectCard);
                }
                Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchCardThread.isAlive()) {
                    myCardReaderHelper.getInstance().stopPolling();
                    try {
                        Thread.sleep(1000);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                    
                    unregisterReceiver(br);
                    stopService(iDetectCard);
                }
                Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
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

    private void showOnCardNotConnectHost() {
        cardManager.setCardNoConnectHost(new CardManager.CardNoConnectHost() {
            @Override
            public void onProcessTransResultUnknow() {
                if (!checkResCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlertNotConnect(CalculatePriceActivity.this, "ไม่สามารถทำรายการได้", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
                                    if(searchCardThread.isAlive()) {
                                        myCardReaderHelper.getInstance().stopPolling();
                                        try {
                                            Thread.sleep(1000);
                                        } catch ( Exception e) {
                                            e.printStackTrace();
                                        }
                                        
                                        unregisterReceiver(br);
                                        stopService(iDetectCard);
                                    }
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
                }
            }

            @Override
            public void onProcessTransResultRefuse() {
                if (!checkResCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlertNotConnect(CalculatePriceActivity.this, "ไม่สามารถทำรายการได้", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
                                    if(searchCardThread.isAlive()) {
                                        myCardReaderHelper.getInstance().stopPolling();
                                        try {
                                            Thread.sleep(1000);
                                        } catch ( Exception e) {
                                            e.printStackTrace();
                                        }
                                        
                                        unregisterReceiver(br);
                                        stopService(iDetectCard);
                                    }
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
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cardManager.abortPBOCProcess();
        if(dialogFallBackCheck.isShowing()) dialogFallBackCheck.dismiss();
        if(dialogWaiting.isShowing()) dialogWaiting.dismiss();
        if(dialogFallBack.isShowing()) dialogFallBack.dismiss();
        if(dialogInsertCard.isShowing()) {
            if(searchCardThread.isAlive()) {
                myCardReaderHelper.getInstance().stopPolling();
                try {
                    Thread.sleep(1000);
                } catch ( Exception e) {
                    e.printStackTrace();
                }
                
            }
        }
        if(dialogCardError.isShowing()) dialogCardError.dismiss();
        if(Utility.getDialogAlert() != null) {
            if(Utility.getDialogAlert().isShowing()) {
                Utility.getDialogAlert().dismiss();
            }
        }

    }

    @Override
    protected void onResume() {
        if (cardManager.getstartFlg()) {
            System.out.printf("utility:: %s onResume startFlg\n",TAG);

            //cardManager.setstartFlg(false);
        }
        super.onResume();
        stateAbort = false;
        if (cardManager != null) {
            setAbortPboc();

            responseCodeDialog();
//            showOnCardNotConnectHost();
        }
        registerReceiver(br, new IntentFilter("ACTION_DETECT"));
        iDetectCard = new Intent(this, OtherDetectCard.class);
        iDetectCard.putExtra("readType", readerType.getEReaderType());
        iDetectCard.putExtra("iccSlot", (byte) 0);
        startService(iDetectCard);



    }

    @Override
    protected void onPause() {
        super.onPause();
        dialogWaiting.dismiss();
        if (cardManager != null) {
            cardManager.removeTransResultAbort();
            cardManager.removeResponseCodeListener();
            cardManager.removeResponsePINListener();
            cardManager.removeNFCListener();
            cardManager.removeCardNoConnectHost();
        }
    }



    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //PollingResult pollingResult = null;
            switch (msg.what) {
                case READ_CARD_CANCEL:
                    System.out.printf("utility:: %s handler READ_CARD_CANCEL\n",TAG);
                    Log.i("TAG", "SEARCH CARD CANCEL");
                    try {
                        //TradeApplication.dal.getCardReaderHelper().setIsPause(true);
                        myCardReaderHelper.getInstance().setIsPause(true);
                        //TradeApplication.getDal().getCardReaderHelper().stopPolling();
                        myCardReaderHelper.getInstance().stopPolling();
                        MainApplication.getDal().getPicc(EPiccType.INTERNAL).close();
                    } catch (PiccDevException e1) {
                        Log.e(TAG, e1.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    };


    // 寻卡线程
    public class SearchCardThread extends Thread implements  Runnable {

        private boolean stop;
        private int ret;
        private boolean ismag;
        private boolean isic;
        private boolean isrf;
        //private EReaderType eReaderType;

        public SearchCardThread() {
            this.stop = false;
            ismag = true;
            isic = true;
            isrf = true;
        }

        public SearchCardThread(boolean ismag, boolean isic, boolean isrf) {
            this.isrf = isrf;
            this.isic = isic;
            this.ismag = ismag;
        }

    public void setIsmag(boolean flag) {
            ismag = flag;
    }

        public void setIsic(boolean flag) {
            isic = flag;
        }

        public void setIsrf(boolean flag) {
            isrf = flag;
        }


        @Override
        public void run() {
            try {

                    Log.d("searchcardthread","run");
                    //SystemClock.sleep(500);  //waiting for load screen
                    //startDate = new Date(System.currentTimeMillis());
                    //DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());

                    //special for visa certification, the time of un-contactless processing is not more than 100ms
                    //ICardReaderHelper cardReaderHelper = TradeApplication.getDal().getCardReaderHelper();
                    if (cardManager.getReaderType() == null) {
                        Log.d(TAG, "cardManager.readerType is null");
                        return;
                    }
                    //android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    //special for visa certification, the time of un-contactless processing is not more than 100ms
                    //pollingResult = cardReaderHelper.polling(readerType, 60 * 1000);
                    //Log.i(TAG, "readerType = " + readerType);
                    //pollingResult = myCardReaderHelper.getInstance().polling(EReaderType.PICC, 60 * 1000);

                    Log.d("kang", "SearchCardThread/getReaderType:" + cardManager.getReaderType());


                    PollingResult pollingResult = myCardReaderHelper.getInstance().polling(cardManager.getReaderType(), 60 * 1000);
                    //pollingResult = cardReaderHelper.polling(EReaderType.PICC, 60*1000);
                    //cardManager.prnTime("myCardReaderHelper.polling diff = ");

                    if (pollingResult.getOperationType() == PollingResult.EOperationType.CANCEL
                            || pollingResult.getOperationType() == PollingResult.EOperationType.TIMEOUT) {
                        //cardReaderHelper.stopPolling();
                        myCardReaderHelper.getInstance().stopPolling();  //only for cancel read card
                        Log.i("TAG", "CANCEL | TIMEOUT");
                        handler.sendEmptyMessage(READ_CARD_CANCEL);
                    } else {
                        dialogInsertCard.dismiss();
                        //handler.sendEmptyMessage(READ_CARD_OK);
                        if (pollingResult.getReaderType() == EReaderType.MAG && ismag) {
                            cardManager.setReadType(EReaderType.MAG);
                            Log.i(TAG, " EReaderType.MAG");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cardManager.starMagTrans();
                                }
                            }).start();
                        } else if (pollingResult.getReaderType() == EReaderType.ICC && isic) {
                            cardManager.setReadType(EReaderType.ICC);
                            Log.i(TAG, " EReaderType.ICC");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cardManager.startEmvTrans();
                                    //Log.d("kang","ic completetrans return " +a);
                                }
                            }).start();
                        } else if (pollingResult.getReaderType() == EReaderType.PICC && isrf) {
                            cardManager.setReadType(EReaderType.PICC);
                            Log.i(TAG, " EReaderType.PICC");

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cardManager.prnTime("thread call in  time = ");
                                    ret = cardManager.starPiccTrans();
                                }
                            }).start();
                        }
                    }
                if(ret != RetCode.EMV_OK) {
                    if(ret == RetCode.EMV_NO_APP) {
                        if(dialogWaiting.isShowing()) {
                            dialogWaiting.cancel();
                            dialogWaiting.dismiss();
                        }
                        if(dialogInsertCard.isShowing()) {
                            dialogWaiting.cancel();
                            dialogWaiting.dismiss();
                        }
                        dialogFallBack.show();
                        this.isrf = false;
                        this.isic = false;
                    }
                    else {
                        Log.d("kang", "searchcardthread/get error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialogInsertCard.isShowing()) {
                                    dialogInsertCard.cancel();
                                    dialogInsertCard.dismiss();
                                }
                                if (dialogWaiting.isShowing()) {
                                    dialogWaiting.cancel();
                                    dialogWaiting.dismiss();
                                }

                                dialogCardError.show();

                            }
                        });
                    }
                }




            } catch (PiccDevException | IccDevException | MagDevException e) {
                Log.e(TAG, e.getMessage());
                handler.sendEmptyMessage(READ_CARD_ERR);
            }
        }


        public void threadstop() {
            this.stop = true;
        }

        public String toString() {
            return "Searchcardthread status/ic:" + isic + ",mag:" + ismag + ",isrf:" + isrf;
        }
    }

    private EReaderType toReaderType(byte mode) {
        mode &= ~ActionSearchCard.SearchMode.KEYIN;
        EReaderType[] types = EReaderType.values();
        for (EReaderType type : types) {
            if (type.getEReaderType() == mode)
                return type;
        }
        return null;
    }

    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        // 寻卡方式，默认挥卡
        try {
            mode = bundle.getByte(EUIParamKeys.CARD_SEARCH_MODE.toString(), (byte) (ActionSearchCard.SearchMode.INSERT_TAP | ActionSearchCard.SearchMode.SWIPE));
            if ((mode & ActionSearchCard.SearchMode.KEYIN) == ActionSearchCard.SearchMode.KEYIN) { // 是否支持手输卡号
                supportManual = true;
            } else {
                supportManual = false;
            }
            System.out.printf("utility:: %s loadParam mode = [%02X]\n",TAG,mode);

            readerType = toReaderType(mode);
            cardManager.setReaderType(readerType);
        } catch (Exception e) {
            Log.e("loadParam", e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        if(searchCardThread.isAlive()) {
            //SearchCardThread.currentThread().stop();
            myCardReaderHelper.getInstance().stopPolling();
            try {
                Thread.sleep(1000);
            } catch ( Exception e) {
                e.printStackTrace();
            }
            

        }
        try {
            this.getApplicationContext().unregisterReceiver(br);
        } catch (IllegalArgumentException e) {

        } catch (Exception e) {

        } finally {

        }
        //dialogPin.dismiss();
        if (Utility.getDialogAlert() != null ) {
            Utility.getDialogAlert().dismiss();
        }


        dialogCardError.dismiss();

        dialogInsertCard.dismiss();
        dialogWaiting.dismiss();
        dialogFallBack.dismiss();
        dialogFallBackCheck.dismiss();

        stopService(iDetectCard);

        //System.exit(0);
        //finish();
        super.onDestroy();
    }



    /*public void initInputOfflPwdDialog() {
        dialogPin = null;

        Handler handler2 = new Handler();

        Log.i("initInputOfflPwdDialog", "creat  initInputOfflPwdDialog");
        dialogPin = new InputPwdDialog(this, handler2, getString(R.string.entry_card_password),
                getString(R.string.without_password));

        Log.i("InputPwdDialog", "new InputPwdDialog");
        dialogPin.setcvmType(0);
        Log.i("InputPwdDialog", " setcvmType");
        //dialog.setTimeout();
        dialogPin.setCancelable(true);
        Log.i("InputPwdDialog", " setCancelable");
        dialogPin.setPwdListener(new InputPwdDialog.OnPwdListener() {
            @Override
            public void onSucc(String data) {
                Log.i("initInputOfflPwdDialog", "dialog OnSucc");
                Log.d("kang", "OnOffPwdListener/onSucc/data:" + data);
                Log.d("kang","panblock:" + PanUtils.getPanBlock(ChangeFormat.bcd2Str(ImplEmv.getTlv(0x57)), PanUtils.EPanMode.X9_8_WITH_PAN));

                if (data == null)
                    GetPinEmv.getInstance().setPinResult(RetCode.EMV_OK);
                else {
                    GetPinEmv.getInstance().setPinResult(RetCode.EMV_OK);
                    GetPinEmv.getInstance().setPinData(data);
                }
                dialogPin.cancel();
                dialogPin.dismiss();
//                initInputOfflPwdDialog();

*//*                try {
                    dialogPin.getPed().clearScreen();
                } catch (Exception e) {

                }*//*

                //finish();
            }

            @Override
            public void onErr() {
                Log.i("initInputOfflPwdDialog", "dialog onErr");
                dialogPin.dismiss();
                //MainActivity.pinEnterReady();
                //Intent intent1 = new Intent(ConsumeActivity.this, MainActivity.class);
                //startActivity(intent1);
                //finish();
            }
        });
        //dialogPin.show();

        Log.i("InputPwdDialog", " show");
        try {
            dialogPin.inputOfflinePlainPin();
        } catch (PedDevException e) {
            Log.i("inputOfflinePlainPin", "e :" + e.getErrCode());
            if (dialogPin != null)
                dialogPin.dismiss();
            e.printStackTrace();
        }

    }
*/
/*

    private void initInputPwdDialog() {
        Log.i("InputPwdDialog", "creat  initInputPwdDialog");
        pindialog = new InputPwdDialog(this, handler, getString(R.string.entry_card_password),
                getString(R.string.without_password));
        //dialog.setTimeout();
        pindialog.setcvmType(isOnlinePin);
        pindialog.setCancelable(true);
        pindialog.setPwdListener(new InputPwdDialog.OnPwdListener() {
            @Override
            public void onSucc(String data) {
                Log.i("InputPwdDialog", "dialog OnSucc");
                Log.d("kang", "OnPwdListener/onSucc/data:" + data);
                if (data == null)
                    GetPinEmv.getInstance().setPinResult(RetCode.EMV_NO_PASSWORD);
                else {
                    GetPinEmv.getInstance().setPinResult(RetCode.EMV_OK);
                    GetPinEmv.getInstance().setPinData(data);
                }
                if (cardManager.getReadType() == EReaderType.ICC) {
                    pindialog.dismiss();

                    ImplEmv.pinEnterReady();
                    //ShowSuccessDialog();
                    finish();
                    //Intent intent1 = new Intent(ConsumeActivity.this, SwingCardActivity.class);
                    //startActivity(intent1);
                } else {
                    ShowSuccessDialog();
                    pindialog.dismiss();
                }

            }

            @Override
            public void onErr() {
                Log.i("InputPwdDialog", "dialog onErr");
                pindialog.dismiss();
                ImplEmv.pinEnterReady();
                if (cardManager.getReadType() == EReaderType.ICC) {
                    finish();
                } else {
                    Intent intent1 = new Intent(CalculatePriceActivity.this, MainActivity.class);
                    startActivity(intent1);
                }

            }
        });
        pindialog.show();
        pindialog.inputOnlinePin(PanUtils.getPanBlock(getIntent().getStringExtra("pan"), PanUtils.EPanMode.X9_8_WITH_PAN));
    }

*/


    public Dialog getDialogCardError() {
        return this.dialogCardError;
    }

    public Dialog getDialogWaiting() {
        return this.dialogWaiting;
    }

    public Dialog getDialogInsertCard() {
        return this.dialogInsertCard;
    }

    public void setDialogMessage(int textid) {
        msgCardErrorLabel.setText(this.getString(textid));
    }
}

