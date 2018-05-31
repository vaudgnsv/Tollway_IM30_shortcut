package org.centerm.land;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.centerm.centermposoversealib.tleservice.AidlTleService;
import com.centerm.centermposoversealib.tleservice.TleParamMap;
import com.centerm.smartpos.aidl.iccard.AidlICCard;
import com.centerm.smartpos.aidl.pboc.AidlCheckCardListener;
import com.centerm.smartpos.aidl.pboc.AidlEMVL2;
import com.centerm.smartpos.aidl.pboc.CardInfoData;
import com.centerm.smartpos.aidl.pboc.CardLoadLog;
import com.centerm.smartpos.aidl.pboc.CardTransLog;
import com.centerm.smartpos.aidl.pboc.EmvTransData;
import com.centerm.smartpos.aidl.pboc.PBOCListener;
import com.centerm.smartpos.aidl.pboc.ParcelableTrackData;
import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.CompactUtil;
import com.centerm.smartpos.util.EMVConstant;
import com.centerm.smartpos.util.EMVTAGS;
import com.centerm.smartpos.util.HexUtil;

import org.centerm.land.core.AidParam;
import org.centerm.land.core.BlockCalculateUtil;
import org.centerm.land.core.ChangeFormat;
import org.centerm.land.core.Config;
import org.centerm.land.core.CustomSocketListener;
import org.centerm.land.core.DataExchanger;
import org.centerm.land.database.ReversalTemp;
import org.centerm.land.database.TCUpload;
import org.centerm.land.database.TransTemp;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.helper.RespCode;
import org.centerm.land.model.Card;
import org.centerm.land.utility.Preference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

public class CardManager {

    public static final String TAG = "CardManager";

    public static CardManager instance = null;
    private Context context = null;
    private Realm realm = null;
    private String HOST_CARD = "";
    private String REF1 = "";
    private String REF2 = "";
    private String REF3 = "";
    private String COMCODE = "";
    private int batchUpload = 0;
    private int batchUploadSize = 0;
    private String settlement61 = "";
    private String settlement63 = "";
    private Double AMOUNTFEE = 0.0;

    private String F37 = "";
    private String F38 = "";
    private String F39 = "";

    private int tcUploadPosition = 0;
    private int tcUploadSize = 0;

    private boolean typeCheck = false;


    private AidlDeviceManager manager = null;
    private AidlDeviceManager managerTle = null;
    private AidlPrinter printDev;
    private AidlQuickScanZbar aidlQuickScanService = null;

    /**
     * DATA_TO_SEND_TPDU
     */
    //private final static String TPDU =  "6000140000";
    private String TPDU = "6000140000";
    private String PRIMARY_HOST = "172.20.10.3";
    private String PRIMARY_PORT = "3838";
    private String SECONDARY_HOST = "";
    private String SECONDARY_PORT = "";

    public static int OnUsOffUsFlg = 0;   // Paul_20180522
    private String tagPanSnEMV;

    public static CardManager init(Context context) {
        if (instance == null) {
            instance = new CardManager();
            instance.context = context;
        }

        //  Must enter this few code to make
        //  CompactUtil can call context without null
        if (context != null) {
            //context = instance.getBaseContext();
            Log.d(TAG, "getApplicationContext is not null");
            CompactUtil.instance(context);
        } else {
            Log.d(TAG, "getApplicationContext is null");
        }

        return instance;
    }

    //region - Services binding
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
            //LogUtil.print(getResources().getString(R.string.bind_service_fail));
            //LogUtil.print("manager = " + manager);
            Log.d(TAG, "bind service failed");
            Log.d(TAG, "manager = " + manager);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);
            //LogUtil.print(getResources().getString(R.string.bind_service_success));
            Log.d(TAG, "bind service success");
            Log.d(TAG, "mamnager = " + manager);
            if (null != manager) {
                //Log.d(TAG, "on device connected");
                /*try {
                    iccard = AidlICCard.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_ICCARD));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "1. in catch block of main manager");
                    e.printStackTrace();
                }*/

                try {
                    pboc2 = AidlEMVL2.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));
                    printDev = AidlPrinter.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));
//                    aidlQuickScanService = AidlQuickScanZbar.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_QUICKSCAN));

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                //loadAid();


//                iccard_service_start(1);
//                setTransaction(SALE);
            }
        }
    };

    private ServiceConnection connTle = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            managerTle = AidlDeviceManager.Stub.asInterface(service);
            Log.d(TAG, "Tle 服务绑定成功");
            if (null != managerTle) {
                tle_initialize(managerTle);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            managerTle = null;
            Log.d(TAG, "Tle服务绑定失败");
        }
    };

    public void bindService() {
        Intent intent = new Intent();
        intent.setPackage("com.centerm.smartposservice");
        intent.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);

        Intent intentTle = new Intent();
        intentTle.setPackage("com.centerm.smartpostestforandroidstudio");
        intentTle.setAction("com.centerm.TleFunction.MANAGER_SERVICE");
        context.bindService(intentTle, connTle, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        context.unbindService(conn);
        context.unbindService(connTle);
    }
    //endregion

    //region - Aid

    private String _id_card;
    private String _thai_name;
    private String _eng_first_name;
    private String _eng_last_name;
    private String _birth_eng;
    private String _birth_th;
    //private String _gender_eng;
    //private String _gender_th;
    private static AidlEMVL2 pboc2;
    private boolean isSuccess = false;
    private final static byte PROCESS_STARTING = 0x01;
    private final static byte PROCESS_DONE = 0x02;
    private final static byte NO_PROCESS = 0x00;

    public byte some_operation = NO_PROCESS;
    public int card_status_init = 0;
    public int card_action = -1;

    private String _cmd = "00A4040008";
    private String _thai_id_card = "A000000054480001";
    private String _req_cid = "80b0000402000d";
    private String _cardno = "A9EF7B30159C2CFCE9E9AC218945213B";
    private String _req_address = "80b01579020064";
    private String _req_issue_expire = "80b00167020012";
    private String _req_full_name = "80b000110200d1";
    private final Charset _UTF8_CHARSET = Charset.forName("TIS-620");
    private List<String> months_eng = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    private List<String> months_th = Arrays.asList("ม.ค.", "ก.พ.", "มี.ค.", "เม.ษ.", "พ.ค.", "มิ.ย.", "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค.");
    private List<String> religions = Arrays.asList("ไม่นับถือศาสนา", "พุทธ", "อิสลาม", "คริสต์", "พราหมณ์-ฮินดู", "ซิกข์", "ยิว", "เชน", "โซโรอัสเตอร์", "บาไฮ", "ไม่ระบุ");


    private String _address;
    private String _issue_eng;
    private String _issue_th;
    private String _expire_eng;
    private String _expire_th;
    private String _religion;
    private byte[] _photo;


    private List<String> tagOf55List;
    private String[] mBlockDataSend = new String[64];
    private String[] mBlockDataReceived = new String[64];

    /**
     * TRANSACTION_TYPE
     */
    public final static byte SALE = 0x01;
    public final static byte VOID = 0x12;
    public final static byte SETTLEMENT = 0x13;
    public final static byte UPLOAD = 0x14;
    public final static byte REVERSAL = 0x16;
    public final static byte SETTLEMENT_AFTER_UPLOAD = 0x17;
    public final static byte TC_ADVICE = 0x18;

    private int currentTransactionType = 0;

    public int findCardTimeout = 200000;

    public final static int CHECKCARD_ONCANCEL = 1;
    public final static int CHECKCARD_ONERROR = 2;
    public final static int CHECKCARD_ONFINDICCARD = 3;
    public final static int CHECKCARD_ONFINDMAGCARD = 4;
    public final static int CHECKCARD_ONFINDRFCARD = 5;
    public final static int CHECKCARD_ONSWIPECARDFAIL = 6;
    public final static int CHECKCARD_ONTIMEOUT = 7;
    public final static int CHECKCARD_ONNEEDINSERT = 8;

    private final static String ECR = "0006303030303831";

    private String TERMINAL_ID = "11000111";
    private String MERCHANT_NUMBER = "030000000011122";


    /* Interface with C/C++ */
    public final static int PROCESS_CONFIRM_CARD_INFO = 2;
    public final static int PROCESS_ERROR = 3;
    public final static int PROCESS_READ_CARD_LOAD_LOG = 4;
    public final static int PROCESS_READ_CARD_OFFLINE_BALANCE = 5;
    public final static int PROCESS_READ_CARD_TRANS_LOG = 6;
    public final static int PROCESS_REQUEST_ONLINE = 7;
    //public final static int PROCESS_TRANS_RESULT = 8;
    public final static int PROCESS_REQUEST_AID_SELECT = 9;
    public final static int PROCESS_REQUEST_ECASH_TIPS_CONFIRM = 10;
    public final static int PROCESS_REQUEST_IMPORT_AMOUNT = 11;
    public final static int PROCESS_REQUEST_IMPORT_PIN = 12;
    public final static int PROCESS_REQUEST_TIPS_CONFIRM = 13;
    public final static int PROCESS_REQUEST_USER_AUTH = 14;

    public final static int PROCESS_ICCARD_AUTO_TRANS = 61;

    public final static int PROCESS_MAG_REQUEST_AMOUNT = 20;

    public final static int PROCESS_TRANSACTION_STARTING = 99;

    public final static int PROCESS_TRANS_RESULT_ABORT = 81;
    public final static int PROCESS_TRANS_RESULT_APPROVE = 82;
    public final static int PROCESS_TRANS_RESULT_FALLBACK = 83;
    public final static int PROCESS_TRANS_RESULT_OTHER = 84;
    public final static int PROCESS_TRANS_RESULT_OTHERINTERFACES = 85;
    public final static int PROCESS_TRANS_RESULT_REFUSE = 86;
    public final static int PROCESS_TRANS_RESULT_UNKNOW = 87;

    public final static byte ICCARD = 0x00;

    public boolean FALLBACK_HAPPEN = false;
    //check card มาจากการรูด
    public boolean MAG_TRX_RECV = false;

    /**
     * DATA_TO_SEND_SOME_OF_APPLICATION(CAN NOT BE CHANGE)
     */
    private final static String NETWORK_ID = "0010";
    private final static String POS_COND_CODE = "00";
    private final static String SALE_PROCESSING_CODE = "003000";
    private final static String VOID_PROCESSING_CODE = "023000";
    private final static String SETTLEMENT_PROCESSING_CODE = "920000";
    private final static String UPLOAD_PROCESSING_CODE = "000001";
    private final static String REFUND_PROCESSING_CODE = "203000";
    private final static String REVERSAL_PROCESSING_CODE = "003000";
    private final static String SETTLEMENT_AFTER_ULOAD_CODE = "960000";
    private final static String TC_ADVICE_CODE = "943000";
//    private final static String ECR                        = "0006303030303831";


    /**
     * DATA_TO_SEND_SOME_OF_APPLICATION(CAN BE CHANGE)
     */
    private String POS_ENT_MODE = "0051";
    private String MAG_POS_ENT_MODE = "0021";

    private String response_code;
    private int OPERATE_ID;
    private String APPRVCODE;
    private String RRN;
    private String POSEM;
    private String POSOC;
    private String PROCESSING_CODE;
    private String NII;
    private String EXPIRY;
    private String TRACK1;
    private String TRACK2;
    private String TRACK3;
    private String AMOUNT;
    private String CARD_NO;
    private String MBLOCK55;
    private String TRACK2_ENC;      // Paul_20180523

    public ParcelableTrackData MagneticCardData;

    public boolean AUTO_TRANSACTION = false;

    /**
     * FUNCTION_OF_PBOC
     */

    public final static byte SERACHE_CARD = 0x00;
    public final static byte COMSUME = 0x01;
    public final static byte CASH = 0x02;
    public final static byte REFUND = 0x03;
    public final static byte BALANCE_QUERY = 0x04;

    public CardInfoData ICCardData;

    private String tempSavedAllData = "";
    private String tempSavedTrackData = "";
    private boolean onLineNow = false;

    /**
     * DATA_TO_SEND_MESSAGE_TYPE
     */
    public String MTI;
    private final static String MESSAGE_SALE = "0200";
    private final static String MESSAGE_VOID = "0200";
    private final static String MESSAGE_SETTLEMENT = "0500";
    private final static String MESSAGE_UPLOAD = "0320";
    private final static String MESSAGE_REVERSAL = "0400";
    private final static String MESSAGE_REFUND = "0200";
    private final static String MESSAGE_TC_ADVICE = "0320";


    //----------
    private TransTemp transTemp; // Database
    private TCUpload tcUploadDb = null;
    private String tcUploadId = null;
    private Card card = null;

    private void loadAid() {
        try {
            pboc2 = AidlEMVL2.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));
            printDev = AidlPrinter.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "AID Imporing...");
        new Thread() {
            public void run() {
                try {
                    isSuccess = true;
                    String[] aids = AidParam.getAid();
                    for (int i = 0; i < aids.length; i++) {
                        pboc2.updateAID(
                                EMVConstant.AidCapkOptFlag.AID_CAPK_OPT_ADDORUPDATE_FLAG, aids[i]);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }

                if (isSuccess) {
                    Log.d(TAG, "AID Import Completed");
                } else {
                    Log.d(TAG, "AID Import Failed");
                }
            }

            ;
        }.start();
    }

    public AidlPrinter getInstancesPrint() {
        if (printDev != null) {
            return printDev;
        }
        return null;
    }

    public AidlTleService getTleVersionOne() {
        if (tleVersionOne != null) {
            return tleVersionOne;
        }
        return null;
    }

    public void RKIdownload() {
        try {
            tleVersionOne.RKIdownload();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startTransaction(int Type) {
        OPERATE_ID = Type;
        try {
            if (pboc2 != null) {
                pboc2.cancelCheckCard(); // Edit พี่สิน
                pboc2.endPBOC();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        switch (Type) {
            case SALE:
                currentTransactionType = SALE;
                allOperateStart(SALE, true, true, true, "Searching the card", "");
                break;
            case VOID:
                break;
            case SETTLEMENT:
                break;
            default:
                break;
        }
    }

    public void endProcess() {
        Log.d(TAG, "pboc end process!!!");
        try {
            response_code = "";
            pboc2.endPBOC();
//            pboc2.abortPBOC();
            onLineNow = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void abortPBOCProcess() {
        Log.d(TAG, "pboc end process!!!");
        try {
            response_code = "";
            pboc2.abortPBOC();
            onLineNow = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void allOperateStart(final byte operateId, final boolean isCheckMag,
                                final boolean isCheckIC, final boolean isChecRF,
                                final String msgPrompt, final String msg) {
        //showMessage(msgPrompt);
        Log.d(TAG, msgPrompt);
        try {
            pboc2.checkCard(isCheckMag, isCheckIC, false, findCardTimeout,
                    new AidlCheckCardListener.Stub() {

                        @Override
                        public void onCanceled() throws RemoteException {
                            Log.d(TAG, "pboc2 onCanceled");
//                            CheckCardCallback(CHECKCARD_ONCANCEL);
                        }

                        @Override
                        public void onError(int arg0) throws RemoteException {
                            Log.d(TAG, "pboc2 onError : " + arg0);
                            CheckCardCallback(CHECKCARD_ONERROR);
                        }

                        @Override
                        public void onFindICCard() throws RemoteException {
                            Log.d(TAG, "pboc2 FindICCard");
                            MAG_TRX_RECV = false;
                            CheckCardCallback(CHECKCARD_ONFINDICCARD);
                            if (operateId == SALE) {
                                //  Create message field

                                mBlockDataSend = new String[64];
                                mBlockDataSend[3 - 1] = SALE_PROCESSING_CODE;
                                PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
                                mBlockDataSend[22 - 1] = POS_ENT_MODE;
//                                mBlockDataSend[23 - 1] = "0001";
                                mBlockDataSend[25 - 1] = "00"; // Insert Chip

                                //POSEM = POS_ENT_MODE;
//                                String tag9f06 = readKernelData(EMVTAGS.combine(0x9F, 0x06));
                                Log.d(TAG, "onFindICCard  HOST_CARD : " + HOST_CARD);
//                                HOST_CARD = "EPS";
                                //too long to received for master card
//                                tagOf55List.add("4F");
//                                tagOf55List.add("9F12");
                                //  Find card info
                                allProcess(operateId, ICCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                                Log.d(TAG, "onFindICCard: \n OperateId = " + operateId + "\n ICCARD = " + ICCARD + "\n MSG = " + msg + "\n isCheckMsg = " + isCheckMag);
                            }

                        }

                        @Override
                        public void onFindMagCard(ParcelableTrackData arg0)
                                throws RemoteException {
                            System.out.println("TAG:" + arg0.getCardNo());


                            if (!FALLBACK_HAPPEN) {
                                Log.d(TAG, "found magnetic card number :" + arg0.getCardNo());
                                Card cardMsg = new Card(arg0.getCardNo());
                                cardMsg.setExpireDate(arg0.getExpireDate());
                                cardMsg.setServiceCode(arg0.getServiceCode());
                                card = cardMsg;
                                HOST_CARD = CardPrefix.getTypeCard(card.getNo());
                                MAG_TRX_RECV = true;
                                TRACK1 = BlockCalculateUtil.get45BlockData(HexUtil.bcd2str(arg0.getFirstTrackData()));
                                TRACK2 = get35Data2(arg0);

//                                TRACK2 = "6216541000000398D49121209920009016216";
                                StringBuilder dataStr = new StringBuilder();
//                                dataStr.append("=");
                                dataStr.append(new String(arg0.getSecondTrackData()));
                                String result = dataStr.toString();
                                Log.d(TAG, "Track 2 = : " + result);
                                Log.d(TAG, "Track 1 = " + TRACK1);
                                Log.d(TAG, "Track 2 = " + TRACK2);

                                MagneticCardData = arg0;

                                String[] serviceCode = result.split("=");
                                Log.d(TAG, "onFindMagCard: " + serviceCode[1].toString() + "sub " + serviceCode[1].substring(4, 5));
                                if (serviceCode[1].substring(4, 5).equalsIgnoreCase("6") ||
                                        serviceCode[1].substring(4, 5).equalsIgnoreCase("2") &&
                                                !HOST_CARD.equalsIgnoreCase("TMS")) {
                                    if (cardHelperListener != null) {
                                        cardHelperListener.onSwapCardIc();
                                    }
                                } else {
                                    switch (currentTransactionType) {
                                        case SALE:
                                            processCallback(PROCESS_MAG_REQUEST_AMOUNT);
                                            break;
                                        default:
                                            break;

                                    }
                                }


                            } else {
                                Card cardMsg = new Card(arg0.getCardNo());
                                cardMsg.setExpireDate(arg0.getExpireDate());
                                cardMsg.setServiceCode(arg0.getServiceCode());
                                card = cardMsg;
                                MAG_TRX_RECV = true;
                                HOST_CARD = CardPrefix.getTypeCard(card.getNo());
                                Log.d(TAG, "onFindMagCard: " + HOST_CARD);
                                TRACK1 = BlockCalculateUtil.get45BlockData(HexUtil.bcd2str(arg0.getFirstTrackData()));
                                TRACK2 = BlockCalculateUtil.get35Data(arg0);

                                Log.d(TAG, "Track 1 = " + TRACK1);
                                Log.d(TAG, "Track 2 = " + TRACK2);

                                MagneticCardData = arg0;

                                switch (currentTransactionType) {
                                    case SALE:
                                        processCallback(PROCESS_MAG_REQUEST_AMOUNT);
                                        break;
                                    default:
                                        break;

                                }
                            }
                        }

                        @Override
                        public void onFindRFCard() throws RemoteException {
                            CheckCardCallback(CHECKCARD_ONFINDRFCARD);
                            MAG_TRX_RECV = false;

                        }

                        @Override
                        public void onSwipeCardFail() throws RemoteException {
                            MAG_TRX_RECV = false;
                            CheckCardCallback(CHECKCARD_ONSWIPECARDFAIL);
                            Log.d(TAG, "onSwipeCardFail");
                        }

                        @Override
                        public void onTimeout() throws RemoteException {
                            MAG_TRX_RECV = false;
                            CheckCardCallback(CHECKCARD_ONTIMEOUT);
                            Log.d(TAG, "onTimeout");
                        }

                    });
        } catch (Exception e) {
            MAG_TRX_RECV = false;
            e.printStackTrace();
            Log.d(TAG, "search exception");
        }
    }

    private void setTagOfList55() {
        if (HOST_CARD.equals("EPS")) {
            Log.d(TAG, "tagOf55List EPS Start: ");
            tagOf55List = new ArrayList<String>();
            tagOf55List.add("82");
            tagOf55List.add("84");
            tagOf55List.add("95");
            tagOf55List.add("9A");
            tagOf55List.add("9C");
            tagOf55List.add("5F2A"); // Currency code
//                                    tagOf55List.add("5F30");
            tagOf55List.add("5F34"); // KTB OPTIONAL
            tagOf55List.add("9F02");
            tagOf55List.add("9F03");
//                                    tagOf55List.add(tag9f06);
            tagOf55List.add("9F09");
            tagOf55List.add("9F10");
            tagOf55List.add("9F1A"); // Country code
            tagOf55List.add("9F1E");
            tagOf55List.add("9F26");
            tagOf55List.add("9F27");
            tagOf55List.add("9F33");
            tagOf55List.add("9F34");
            tagOf55List.add("9F35");
            tagOf55List.add("9F36");
            tagOf55List.add("9F37");
            tagOf55List.add("9F41");

            Log.d(TAG, "tagOf55List EPS End: ");
        } else {
            tagOf55List = new ArrayList<String>();
            //tagOf55List.add("5F24"); // Expiry
            tagOf55List.add("5F2A"); // Currency code
            tagOf55List.add("5F34"); // KTB OPTIONAL
            tagOf55List.add("82");
            tagOf55List.add("95");
            tagOf55List.add("9A");
            tagOf55List.add("9C");
            tagOf55List.add("9F02");
            tagOf55List.add("9F03");

            //too long to received for master card
            tagOf55List.add("9F10");
            tagOf55List.add("9F1A"); // Country code
            tagOf55List.add("9F1E");
            tagOf55List.add("9F26");
            tagOf55List.add("9F27");
            tagOf55List.add("9F33");
            tagOf55List.add("9F34");
            tagOf55List.add("9F36");
            tagOf55List.add("9F37");
        }
    }

    public static String get35Data2(ParcelableTrackData arg0) {
        StringBuilder dataStr = new StringBuilder();
        dataStr.append("37");
        dataStr.append(new String(arg0.getSecondTrackData()));
        String result = dataStr.toString();
        result = result.substring(0, result.length());
        result = result;
        result = result.replace("=", "D");
        System.out.println("DATA IN BLOCK 35：" + result);
        return result;
    }


    public void setImportAmount(String amount) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
            Double amountFee = (Double.valueOf(amount) * fee.intValue()) / 100;
            Double amountAll = Double.valueOf(amount) + amountFee;
            pboc2.importAmount(decimalFormat.format(amountAll));
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
            //ส่งค่าที่ยังไม่ได้ คิดว่า Fee เพื่อ จะได้ ไปคิดคำนวณอีกตอนบันทึกลงดาต้าเบส
            AMOUNT = amount;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String OffUsEPSPinblock(String PAN, String PIN) {
        String PinBlock = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("PAN", PAN);
            hashMap.put("PIN", PIN);
            System.out.printf("utility:: OOOOOOOOO PAN = %s \n", PAN);
            System.out.printf("utility:: OOOOOOOOO PIN = %s \n", PIN);
            //Toast.makeText(InterfaceTestActvity.this,hashMap.size()+"弱뷴?",Toast.LENGTH_SHORT).show();
            TleParamMap tleParamMap = new TleParamMap();
            tleParamMap.setParamMap(hashMap);
            PinBlock = tleVersionOne.tleFuncton("OffUsEPSPinBlock", tleParamMap);

            System.out.printf("utility:: OOOOOOOOO PIN After = %s \n", PIN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return PinBlock;
    }

    public String OnUsTMSinblock(String PAN, String PIN) {
        String PinBlock = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("PAN", PAN);
            hashMap.put("PIN", PIN);
            //Toast.makeText(InterfaceTestActvity.this,hashMap.size()+"弱뷴?",Toast.LENGTH_SHORT).show();
            TleParamMap tleParamMap = new TleParamMap();
            tleParamMap.setParamMap(hashMap);
            PinBlock = tleVersionOne.tleFuncton("OnUsTMSPinBlock", tleParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return PinBlock;
    }

    public void allProcess(final byte operateId, final byte cardType,
                           final String msg, final boolean isCheckMag,
                           final boolean isCheckIC, final boolean isChecRF,
                           final String msgPrompt) {
        Log.d(TAG, "cardType :" + operateId + "cardType : " + cardType + "msg : " + msg + "isCheckMag :" + isCheckMag + "isCheckIC : " + isCheckIC + " isChecRF: " + isChecRF + " msgPrompt : " + msgPrompt);
        EmvTransData paramEmvTransData = new EmvTransData();

        if (cardType == ICCARD) {
            Log.d(TAG, "IC CARD Type");
            paramEmvTransData.setSlotType((byte) EMVConstant.SlotType.SLOT_TYPE_IC);
            paramEmvTransData.setTransTypeSimpleFlow(false);
            paramEmvTransData.setConfirmCardNo(false);
        } else {
            Log.d(TAG, "RF Card Type");
            paramEmvTransData.setSlotType((byte) EMVConstant.SlotType.SLOT_TYPE_RF);
        }

        switch (operateId) {
            // 现金
            case CASH:
                paramEmvTransData.setTranstype((byte) EMVConstant.TransType.TRANSTYPE_CASH);
                break;
            //SALE
            case COMSUME:
                paramEmvTransData.setTranstype((byte) EMVConstant.TransType.TRANS_TYPE_CONSUME);
                break;
            // 退款
            case REFUND:
                paramEmvTransData.setTranstype((byte) EMVConstant.TransType.TRANS_TYPE_RETURN);
                break;
            // 余额查询（联机）
            case BALANCE_QUERY:
                paramEmvTransData.setTranstype((byte) EMVConstant.TransType.TRANS_TYPE_BALANCE_QUERY);
                break;
            default:
                break;
        }

        paramEmvTransData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.AFTER_DISPLAY_CARD_NUMBER);
        paramEmvTransData.setEMVFlow((byte) EMVConstant.EMVFlowSelect.EMV_FLOW_PBOC);

//        paramEmvTransData.setIsForceOnline(false);
//        paramEmvTransData.setOnlyOffline(true);

        try {
            pboc2.processPBOC(paramEmvTransData, new PBOCListener.Stub() {
                @Override
                public void onConfirmCardInfo(CardInfoData arg0)
                        throws RemoteException {
                    ICCardData = arg0;
                    // TODO : Check 4F

                    card = analyse(arg0.getCardno());
                    EXPIRY = card.getExpireDate();
                    Log.d(TAG, "----> CardInfoData ==> arg0 " + arg0.toString());
                    //SINN 20180524 CHECK TH chip card
                    tagPanSnEMV = readKernelData(EMVTAGS.EMVTAG_APP_PAN_SN);
                    Log.d(TAG, "----> CardInfoData ==> arg0 " + tagPanSnEMV);
                    String Tag_4f = readKernelData(EMVTAGS.EMVTAG_AID);
                    Log.d(TAG, "----> CardInfoData ==> Tag_4f " + Tag_4f);
                    if (Tag_4f.equalsIgnoreCase("4F08A000000677010100") || Tag_4f.equalsIgnoreCase("4F08A000000677010101") ||
                            Tag_4f.equalsIgnoreCase("4F08A000000333010103") || Tag_4f.equalsIgnoreCase("4F08A000000677010109")) {

                        HOST_CARD = "EPS";/*CardPrefix.getTypeCardTMS(card.getNo()) == null ? "EPS" : CardPrefix.getTypeCardTMS(card.getNo());*/

                    } else {
                        HOST_CARD = CardPrefix.getTypeCard(card.getNo());
                    }

                    Log.d(TAG, "----> CardInfoData ==> Tag_4f " + Tag_4f);
                    String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
                    String mBlockData24 = "";
                    if (HOST_CARD.equalsIgnoreCase("EPS")) {
                        mBlockData24 = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
                    } else {
                        mBlockData24 = CardPrefix.getNii(card.getNo(), context);
                    }

                    setTagOfList55();

//                    tagPanSnEMV = tagPanSnEMV != null ? tagPanSnEMV.substring(6, 8) : "0001";
                    mBlockDataSend[23 - 1] = CardPrefix.hexadecimalToInt(tagPanSnEMV);
                    Log.d(TAG, "onConfirmCardInfo: " + mBlockDataSend[23 - 1]);
                    mBlockDataSend[24 - 1] = mBlockData24;
                    TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
                    MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
                    mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
                    mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
                    mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                    Log.d(TAG, "EXPIRY = " + EXPIRY);
                    processCallback(PROCESS_CONFIRM_CARD_INFO);
                    pboc2.importConfirmCardInfoRes(true);
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    Log.d(TAG, "allprocess error: " + arg0);
                    processCallback(PROCESS_ERROR);
                }

                @Override
                public void onReadCardLoadLog(String arg0, String arg1,
                                              CardLoadLog[] arg2) throws RemoteException {
                    //showMessage(getString(R.string.pboc_read_loadlog_success)
                    //                + "," + "atc：" + arg0 + "  logcheckCode :" + arg1);
                    Log.d(TAG, "loadlog : atc : " + arg0 + "  logcheckCode :" + arg1);
                    processCallback(PROCESS_READ_CARD_LOAD_LOG);
                    for (int i = 0; i < arg2.length; i++) {
                        Log.d(TAG, arg2[i].getTransDate());
                        Log.d(TAG, arg2[i].getTransTime());
                    }
                }

                @Override
                public void onReadCardOffLineBalance(String firstMoneyCode, String firstBalance,
                                                     String secondMoneyCode, String secondBalance) throws RemoteException {
                    Log.d(TAG, "readcardofflinebalance: " + firstBalance);
                    processCallback(PROCESS_READ_CARD_OFFLINE_BALANCE);
                }

                @Override
                public void onReadCardTransLog(CardTransLog[] arg0)
                        throws RemoteException {
                    // 卡片交易日志
                    if (arg0 != null) {
                        Log.d(TAG, "readcardtranslog: " + arg0.length);

                        for (CardTransLog log : arg0) {
                            Log.d(TAG, log.toString());
                        }
                    } else {
                        Log.d(TAG, "pboc_translog_failed_null");
                    }
                    processCallback(PROCESS_READ_CARD_TRANS_LOG);
                }

                @Override
                public void onRequestOnline() throws RemoteException {
                    //showMessage("Request_Online_Transaction");
                    Log.d(TAG, "Request_Online_Transaction");
                    Log.d(TAG, "onRequestOnline: " + readKernelData(EMVTAGS.EMVTAG_AMOUNT));
                    // 读取5F34卡序列号的时候不需要在请求联机这个时候读取，
                    // If you need  the value from tag "9F26", You should read the Five-five data in onRequestOnline
                    // 需要在请求联机交易时读取

                    if (operateId == SALE) {
                        tempSavedTrackData = readKernelData(EMVTAGS.EMVTAG_TRACK2);
                        mBlockDataSend[35 - 1] = BlockCalculateUtil.get35Data(tempSavedTrackData);
                        EXPIRY = card.getExpireDate();
                        TRACK2 = BlockCalculateUtil.get35Data(tempSavedTrackData);

                        String DE55 = BlockCalculateUtil.get55Data(readKernelData(EMVTAGS.getF55Taglist()), tagOf55List);
                        String Tag9F06 = readKernelData(EMVTAGS.combine(0x9F, 0x06));
                        String Tag5F30 = readKernelData(EMVTAGS.combine(0x5F, 0x30));
                        Log.d(TAG, "onRequestOnline: " + Tag5F30);
                        DE55 = DE55 + Tag9F06 + Tag5F30;

                        DE55 = BlockCalculateUtil.checkMessage(DE55);
                        //int length_of_tag55 = BlockCalculateUtil.get55Data(readKernelData(EMVTAGS.getF55Taglist()), tagOf55List).length() / 2;
                        int length_of_tag55 = DE55.length() / 2;


                        mBlockDataSend[55 - 1] = BlockCalculateUtil.get55Length(length_of_tag55) + DE55;
//                        mBlockDataSend[55 - 1] = "016582027D008408A000000333010101950504000480009A031805229C01005F2A0207645F300202205F3401019F02060000000001039F03060000000000009F0608A0000003330101019F090200209F101307000103A0A000010A010000000000098918999F1A0207649F1E0838353130494343009F26083F454A922877F55A9F2701809F3303E0F8C89F34034203009F3501229F360200C99F370430F1E5DD9F410400000054";
                        Log.d(TAG, "block 55 = " + mBlockDataSend[55 - 1]);
                        //EXPIRY = BlockCalculateUtil.getExpireData(mBlockDataSend[35-1]);
                        MBLOCK55 = mBlockDataSend[55 - 1];
                        MTI = MESSAGE_SALE;
                        onLineNow = true;
                        processCallback(PROCESS_TRANSACTION_STARTING);
                        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
                        if (HOST_CARD.equalsIgnoreCase("EPS")) {
                            setDataSalePINEPS();
                        }
                        packageAndSend(TPDU, MESSAGE_SALE, mBlockDataSend);
                    } else if (operateId == UPLOAD) {
                        mBlockDataSend[55 - 1] = "0101" + BlockCalculateUtil.get55Data(readKernelData(EMVTAGS.getF55Taglist()), tagOf55List);
                        //packageAndSend(TPDU,MESSAGE_UPLOAD,mBlockDataSend);
                    } else if (operateId == TC_ADVICE) {
                        mBlockDataSend[55 - 1] = "0101" + BlockCalculateUtil.get55Data(tempSavedAllData, tagOf55List);
                        //packageAndSend(TPDU,MESSAGE_TC_ADVICE,mBlockDataSend);
                    }

                    processCallback(PROCESS_REQUEST_ONLINE);

                }

                @Override
                public void onTransResult(byte arg0) throws RemoteException {
                    switch (arg0) {
                        case EMVConstant.TransResult.TRANS_RESULT_ABORT:
                            Log.d(TAG, "pboc_trans_abort");
                            tempSavedAllData = readKernelData(EMVTAGS.getF55Taglist());
                            processCallback(PROCESS_TRANS_RESULT_ABORT);
                            break;
                        case EMVConstant.TransResult.TRANS_RESULT_APPROVE:
                            Log.d(TAG, "pboc_trans_accept");
                            tempSavedAllData = readKernelData(EMVTAGS.getF55Taglist());
                            RRN = mBlockDataReceived[37 - 1]; //field37
                            APPRVCODE = mBlockDataReceived[38 - 1]; //field37

                            processCallback(PROCESS_TRANS_RESULT_APPROVE);
                            break;
                        case EMVConstant.TransResult.TRANS_RESULT_FALLBACK:
                            Log.d(TAG, "pboc_trans_fallback");
                            processCallback(PROCESS_TRANS_RESULT_FALLBACK);
                            break;
                        case EMVConstant.TransResult.TRANS_RESULT_OTHER:
                            Log.d(TAG, "pboc_trans_other");
                            processCallback(PROCESS_TRANS_RESULT_OTHER);
                            break;
                        case EMVConstant.TransResult.TRANS_RESULT_OTHERINTERFACES:
                            Log.d(TAG, "pboc_trans_other_interface");
                            processCallback(PROCESS_TRANS_RESULT_OTHERINTERFACES);
                            break;
                        case EMVConstant.TransResult.TRANS_RESULT_REFUSE:
                            Log.d(TAG, "failed :: 交易拒绝");
                            tempSavedAllData = readKernelData(EMVTAGS.getF55Taglist());
                            processCallback(PROCESS_TRANS_RESULT_REFUSE);
                            break;
                        default:
                            Log.d(TAG, "pboc_trans_error , error_code = " + arg0);
                            processCallback(PROCESS_TRANS_RESULT_UNKNOW);
                    }
                    pboc2.endPBOC();
                }

                @Override
                public void requestAidSelect(int arg0, String[] arg1) throws RemoteException {
                    Log.d(TAG, "pboc_request_aidselect");
                    for (String aid : arg1) {
                        Log.d(TAG, "[" + aid + "]");
                    }

                    boolean b = pboc2.importAidSelectRes(1);

                    if (b) {
                        Log.d(TAG, "pboc_request_adiselect_success");
                    } else {
                        Log.d(TAG, "pboc_request_adiselect_failed");
                    }
                    processCallback(PROCESS_REQUEST_AID_SELECT);
                }

                @Override
                public void requestEcashTipsConfirm() throws RemoteException {
                    //showMessage(getString(R.string.pboc_request_elecash));
                    Log.d(TAG, "pboc_request_elecash");
                    processCallback(PROCESS_REQUEST_ECASH_TIPS_CONFIRM);
                }

                @Override
                public void requestImportAmount(int arg0)
                        throws RemoteException {
                    Log.d(TAG, "REQUEST IMPORT MONEY HERE");
                    if (AUTO_TRANSACTION) {
                        Log.d(TAG, "iccard do auto transaction");
                        processCallback(PROCESS_ICCARD_AUTO_TRANS);
                    } else {
                        processCallback(PROCESS_REQUEST_IMPORT_AMOUNT);
                    }

                }

                @Override
                // 导入PIN
                public void requestImportPin(int arg0, boolean arg1, String arg2)
                        throws RemoteException {
                    processCallback(PROCESS_REQUEST_IMPORT_PIN);
                    //showMessage(getString(R.string.pboc_input_pin));
                    Log.d(TAG, "pboc_input_pin");
                    Log.d(TAG, "arg0 = " + arg0 + "\narg1 = " + arg1 + "\narg2 = " + arg2);

                    //showMessage(getString(R.string.pboc_importing_pin)+ ":26888888FFFFFFFF" + "");
                    Log.d(TAG, "pboc_importing_pin = 26888888FFFFFFFF");
                    pboc2.importPin("26888888FFFFFFFF");
                }

                @Override
                // 请求提示信息
                public void requestTipsConfirm(String arg0)
                        throws RemoteException {
                    processCallback(PROCESS_REQUEST_TIPS_CONFIRM);
                    Log.d(TAG, "pboc_request_msg_excute");
                    Log.d(TAG, arg0);
                    pboc2.importMsgConfirmRes(true);
                }

                @Override
                public void requestUserAuth(int arg0, String arg1)
                        throws RemoteException {
                    Log.d(TAG, "pboc_request_user");
                    processCallback(PROCESS_REQUEST_USER_AUTH);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            Log.d(TAG, "allprocess exception : " + message);
            processCallback(PROCESS_ERROR);
        }
    }

    public String getHostCard() {
        return HOST_CARD;
    }

    public String getMagneticCardNumber() {
        //arg0.getCardno();
        Log.d(TAG, "get magnetic card number");
        //MAG_TRX_RECV = true;
        return (MagneticCardData.getCardNo());
    }

    public String getMagneticCardHolderName() {
        String CardHolderName = BlockCalculateUtil.getTheNameFromFirstTrack(new String(MagneticCardData.getFirstTrackData()));
        Log.d(TAG, "CardHolderName: " + CardHolderName);
        return (CardHolderName);
    }

    public void getMagneticCardType() {
        String cardtype;

        switch (BlockCalculateUtil.getBankCardType(MagneticCardData.getCardNo())) {
            case Config.VISA:
                Log.d(TAG, "VISA");
                cardtype = "VISA";
                break;
            case Config.UNION:
                Log.d(TAG, "UNION");
                cardtype = "UNION";
                break;
            case Config.MASTER:
                Log.d(TAG, "MASTER");
                cardtype = "MASTER";
                break;
            default:
                Log.d(TAG, "UNKNOW");
                cardtype = "UNKNOW";
                break;
        }

        //return(cardtye);
    }

    public Card analyse(String cardInfo) {
        String fieldData = "";
        if (cardInfo.isEmpty()) {
            Log.e(TAG, "CardInfo is empty");
            return (null);
        } else {
            int i = 0;
            while (i < cardInfo.length()) {
                Log.d(TAG, "come in loop for check cardInfo");
                String fieldId = cardInfo.substring(i, i + 2);
                String fieldLength = "";
                int field_len = 0;
                if (fieldId.equals("5F")) {
                    fieldId = cardInfo.substring(i, i + 4);
                    Log.d(TAG, "field id = " + fieldId);
                    fieldLength = cardInfo.substring(i + 4, i + 6);
                    Log.d(TAG, "field length = " + fieldLength);
                    field_len = (Integer.parseInt(fieldLength, 16)) * 2;
                    fieldData = cardInfo.substring(i + 6, i + 6 + field_len);
                    Log.d(TAG, "field data = " + fieldData);
                    i = i + 6 + field_len;
                } else {
                    Log.d(TAG, "field id = " + fieldId);
                    fieldLength = cardInfo.substring(i + 2, i + 4);
                    Log.d(TAG, "field length = " + fieldLength);
                    field_len = (Integer.parseInt(fieldLength, 16)) * 2;
                    fieldData = cardInfo.substring(i + 4, i + 4 + field_len);
                    Log.d(TAG, "field data = " + fieldData);
                    i = i + 4 + field_len;
                }

                if (fieldId.equals("57")) {
                    break;
                }
            }

            int position = 0;
            if (fieldData.contains("D")) {
                position = fieldData.indexOf("D");
            } else if (fieldData.contains("=")) {
                position = fieldData.indexOf("=");
            } else {
                //Log.e("TAG","Error");
                return (null);
            }

            String cardNo = fieldData.substring(0, position);
            fieldData = fieldData.substring(position + 1);
            String expiredData = fieldData.substring(0, 4);
            String serviceCode = fieldData.substring(4, 4 + 3);

            //  Create instance
            Card card = new Card(cardNo);
            card.setExpireDate(expiredData);
            card.setServiceCode(serviceCode);

            return card;
        }
    }

    public String readKernelData(byte[] tag) {
        //showMessage("CORE DATA READING ... ...");
        Log.d(TAG, "CORE DATA READING ... ...");
        String result = "";
        try {
            byte[] outputBuffer = new byte[1024];
            // 5F34 卡序列号
            int ret = pboc2.readKernelData(tag, outputBuffer);
            //showMessage("DATA From kernel :" + " [ " + ret
            //                + " ]" + getString(R.string.pboc_byte));
            Log.d(TAG, "DATA From kernel :" + " [ " + ret + " ]");
            if (ret > 0) {
                //showMessage(getString(R.string.pboc_read_55tag_success));
                Log.d(TAG, "pboc_read_55tag_success");
                outputBuffer = Arrays.copyOfRange(outputBuffer, 0, ret);
                //showMessage(HexUtil.bcd2str(outputBuffer));
                result = HexUtil.bcd2str(outputBuffer);
                outputBuffer = new byte[500];
            } else {
                //showMessage(getString(R.string.pboc_read_55tag_failed));
                Log.d(TAG, "pboc_read_55tag_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //showMessage(getString(R.string.pboc_read_55tag_exception), Color.RED);
            Log.d(TAG, "pboc_read_55tag_exception");
        }
        return result;
    }

    public void stopTransaction() {
        Log.d(TAG, "Cancel checkcard");
        try {
            pboc2.cancelCheckCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region - ICCard

    private AidlICCard iccard = null;

    public void iccard_service_start(int action_type) {
        card_status_init = 0;
        card_action = action_type;
        Log.d(TAG, "card event monitoring start...");
        try {
            if (iccard != null) {
                Log.d(TAG, "do the card event!!!");
                detectCardEvent();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void detectCardEvent() throws InterruptedException, ExecutionException {
        final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable job = new Runnable() {
            boolean _read = false;

            @Override
            public void run() {
                try {
                    iccard.open();
                    byte card_status = iccard.status();
                    if (card_status == 1) {
                        if (card_status_init == 0) // no card in slot before
                        {
                            Log.d(TAG, "card just inserted");
                            CardEventOccured(1);
                            card_status_init = 1;
                            if (card_action == 2) {  // Citizen Card
                                if (iccard.reset() != null && !_read) {
                                    Log.d(TAG, "start sending command...");
                                    if (iccard.sendAsync(HexUtil.hexStringToByte(_cmd + _thai_id_card)) != null) {
                                        _read = true;
                                        getThaiCitizen_Info(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_cid)), _UTF8_CHARSET), 0);
                                        getThaiCitizen_Info(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_full_name)), _UTF8_CHARSET), 1);
                                        getThaiCitizen_Info(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_address)), _UTF8_CHARSET), 2);
                                        getThaiCitizen_Info(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_issue_expire)), _UTF8_CHARSET), 3);
                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                CitizenInfoUpdate(_id_card, _address, _thai_name, _eng_first_name, _eng_last_name, _birth_th, _birth_eng, _issue_eng, _issue_th, _expire_eng, _expire_th, _religion);
                                            }
                                        });*/
                                        getThaiCitizen_Photo();
                                        CardEventOccured(2);
                                    }
                                }
                            } else //EMV Processing
                            {
                                Log.d(TAG, "Need to EMV Process");
                                if (iccard.reset() != null && !_read) {
                                    //_read = true;
                                    Log.d(TAG, "start sending EMV Process...");
//                                    setTransaction(SALE);
//                                    some_operation = PROCESS_STARTING;
                                }
                                //CardEventOccured(2);
                            }
                        } else // card was in slot, check operation
                        {
                            Log.d(TAG, "card still inserted");
                            if (some_operation == PROCESS_STARTING) {
                                Log.d(TAG, "please waiting...");
                            } else if (some_operation == PROCESS_DONE) {
                                Log.d(TAG, "can removing card");
                                CardEventOccured(2);
                            } else if (some_operation == NO_PROCESS) {
                                Log.d(TAG, "please remove card first");
                                CardEventOccured(2);
                            }
                        }
                    } else if (card_status == 0) {
                        if (card_status_init == 1) {
                            Log.d(TAG, "card removed");
                            CardEventOccured(0);
                            card_status_init = 0;
                            scheduledExecutor.shutdown();
                        } else {
                            Log.d(TAG, "card already empty");
                            if (card_action == -100) {
                                Log.d(TAG, "detectCardEvent got Timeout!!!");
                                scheduledExecutor.shutdown();
                                iccard.close();
                                card_action = -1;
                                card_status_init = 0;
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.d(TAG, "catch block in detectCardEvent " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            private byte[] r(byte[] data) {
                int index = data.length - 1;
                while ((index > 0) && (data[(index - 1)] == 32)) {
                    index--;
                    if (index == 0) {
                        return null;
                    }
                }
                return Arrays.copyOfRange(data, 0, index);
            }

            private void getThaiCitizen_Photo() {
                try {
                    ByteArrayOutputStream _a = new ByteArrayOutputStream();
                    for (int i = 0; i < 20; i++) {
                        int xwd;
                        int xof = i * 254 + 379; //379-381
                        xwd = i == 20 ? 38 : 254;

                        String sp2 = e(xof >> 8 & 0xff);
                        String sp3 = e(xof & 0xff);
                        String sp6 = e(xwd & 0xff);

                        byte[] _xx = r(r(iccard.sendAsync(HexUtil.hexStringToByte("80B0" + sp2 + sp3 + "0200" + sp6)))); //0200 - 0201
                        if (_xx != null)
                            _a.write(_xx, 0, _xx.length);
                    }
                    _a.flush();
                    _photo = _a.toByteArray();
                    String _b = Base64.encodeToString(_photo, Base64.DEFAULT);
                    _a.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    _photo = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    _photo = null;
                }

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Bitmap _bm = BitmapFactory.decodeByteArray(_photo, 0, _photo.length);
                        //xphoto.setImageBitmap(_bm);
                        CitizenPhotoUpdate(_photo);
                    }
                });*/
            }

            private String getThaiCitizen_Info(String _val, int _index) {
                String _xx = _val;
                switch (_index) {
                    case 0:
                        if (_xx != null | _xx.length() != 0) {
                            _xx = _val.replaceAll("#", " ");
                            _xx = _xx.substring(0, _xx.length() - 2);
                            char[] achars = _xx.toUpperCase().toCharArray();
                            _id_card = achars[0] + " " + achars[1] + achars[2] + achars[3] + achars[4] + " " + achars[5] + achars[6] + achars[7] + achars[8] + achars[9] + " " + achars[10] + achars[11] + " " + achars[12];
                        }
                        break;
                    case 1:
                        if (_xx != null | _xx.length() != 0) {
                            int _first_space = _val.indexOf(" ");
                            _thai_name = _xx.substring(0, _first_space).replaceAll("#", " ");
                            _xx = _xx.substring(_first_space, _xx.length() - 2);
                            _xx = _xx.trim();
                            _first_space = _xx.indexOf(" ");
                            String _eng_name = _xx.substring(0, _first_space).replaceAll("#", " ");
                            String[] _eng_name_list = _eng_name.split(" ");
                            _eng_first_name = _eng_name_list[0] + " " + _eng_name_list[1];
                            _eng_last_name = _eng_name_list[3];
                            _xx = _xx.substring(_first_space, _xx.length());
                            _xx = _xx.trim();
                            String _year_th = _xx.substring(0, 4);
                            String _year_eng = "" + (Integer.parseInt(_xx.substring(0, 4)) - 543);
                            String _month_eng = months_eng.get(Integer.parseInt(_xx.substring(4, 6)) - 1);
                            String _month_th = months_th.get(Integer.parseInt(_xx.substring(4, 6)) - 1);
                            String _day = "" + Integer.parseInt(_xx.substring(6, 8));
                            _birth_eng = _day + " " + _month_eng + " " + _year_eng;
                            _birth_th = _day + " " + _month_th + " " + _year_th;
                            /*if(Integer.parseInt(_xx.substring(8, 9)) == 1) {
                                _gender_eng = "Male";
                                _gender_th = "ชาย";
                            }else{
                                _gender_eng = "Female";
                                _gender_th = "หญิง";
                            }*/
                            //_xx = _thai_name + "\n" + _eng_first_name + "\r\n" + _eng_last_name + "\r\n" + _birth_th + "\n" + _birth_eng + "\n" + _gender_eng + "\r\n" + _gender_th;
                        }
                        break;
                    case 2:
                        if (_xx != null | _xx.length() != 0) {
                            _xx = _val.replaceAll("#", " ");
                            _xx = _xx.substring(0, _xx.length() - 2);
                            _xx = _xx.replace("ตำบล", "ต.");
                            _xx = _xx.replace("อำเภอ", "อ.");
                            _xx = _xx.replace("จังหวัด", "จ.");
                            _address = "       " + _xx;
                        }
                        break;
                    case 3:
                        if (_xx != null | _xx.length() != 0) {
                            _xx = _val.replaceAll("#", " ");
                            _xx = _xx.substring(0, _xx.length() - 2);
                            String _year_th = _xx.substring(0, 4);
                            String _year_eng = "" + (Integer.parseInt(_xx.substring(0, 4)) - 543);
                            String _month_eng = months_eng.get(Integer.parseInt(_xx.substring(4, 6)) - 1);
                            String _month_th = months_th.get(Integer.parseInt(_xx.substring(4, 6)) - 1);
                            String _day = "" + Integer.parseInt(_xx.substring(6, 8));
                            _issue_eng = _day + " " + _month_eng + " " + _year_eng;
                            _issue_th = _day + " " + _month_th + " " + _year_th;

                            _year_th = _xx.substring(8, 12);
                            _year_eng = "" + (Integer.parseInt(_xx.substring(8, 12)) - 543);
                            _month_eng = months_eng.get(Integer.parseInt(_xx.substring(12, 14)) - 1);
                            _month_th = months_th.get(Integer.parseInt(_xx.substring(12, 14)) - 1);
                            _day = "" + Integer.parseInt(_xx.substring(14, 16));
                            _expire_eng = _day + " " + _month_eng + " " + _year_eng;
                            _expire_th = _day + " " + _month_th + " " + _year_th;
                            int _in = Integer.parseInt(_xx.substring(16, 18));
                            _religion = religions.get(_in);
                            if (_in == 99) {
                                _religion = religions.get(10);
                            }
                            _xx = _issue_eng + "\r\n" + _issue_th + "\r\n" + _expire_eng + "\r\n" + _expire_th + "\r\n" + _religion;
                        }
                        break;
                    default:
                }
                return _xx;
            }

        };

        scheduledExecutor.scheduleAtFixedRate(job, 300, 300, TimeUnit.MILLISECONDS);
    }

    private String e(int value) {
        String hex = Integer.toHexString(value);
        hex = hex.length() % 2 == 1 ? "0" + hex : hex;
        return hex.toUpperCase();
    }

    //endregion


    private void CardEventOccured(int type) {

    }

    private void CitizenInfoUpdate(String id, String addr, String fullname_th, String fname_eng, String lname_eng, String birth_th, String birth_eng, String issue_eng, String issue_th, String expire_eng, String expire_th, String religion) {

    }

    private void CitizenPhotoUpdate(byte[] photo_data) {

    }

    private void CheckCardCallback(int type) {

    }

    private void processCallback(int status) {
        switch (status) {
            case PROCESS_CONFIRM_CARD_INFO:
                if (OPERATE_ID == SALE) {
                    PROCESSING_CODE = "003000";
                    POSEM = "0051";
                    POSOC = "00";
                    NII = CardPrefix.getNii(card.getNo(), context);
                    TRACK1 = "";
                    TRACK2 = "";
                    MTI = MESSAGE_SALE;
                    CARD_NO = card.getNo();
                }
                String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
                mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(traceIdNo.isEmpty() ? "1" : traceIdNo));
                if (!HOST_CARD.equalsIgnoreCase("TMS")) {
                    if (cardHelperListener != null) {
                        cardHelperListener.onCardInfoReceived(card);
                    }
                } else {
                    try {
                        pboc2.cancelCheckCard();
                        pboc2.clearKernelICTransLog();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (cardHelperListener != null) {
                        cardHelperListener.onSwapCardMag();
                    }
                }
                break;
            case PROCESS_TRANS_RESULT_APPROVE:
                if (cardHelperListener != null) {
                    cardHelperListener.onCardTransactionUpdate(true, card);
                }
                break;
            case PROCESS_TRANS_RESULT_UNKNOW:
                if (cardHelperListener != null) {
                    cardHelperListener.onCardTransactionUpdate(false, card);
                }
                break;
            case PROCESS_MAG_REQUEST_AMOUNT:
                if (cardHelperListener != null) {
                    cardHelperListener.onFindMagCard(card);
                }

                break;
            case PROCESS_REQUEST_TRANSACTION_TIMEOUTED:
                if (insertOrUpdateDatabase != null) {
                    insertOrUpdateDatabase.onTransactionTimeOut();
                }
                break;
            case PROCESS_REQUEST_CONNECTION_FAILED:
                if (insertOrUpdateDatabase != null) {
                    insertOrUpdateDatabase.onConnectTimeOut();
                }
                break;
            case PROCESS_TRANS_RESULT_FALLBACK:
                FALLBACK_HAPPEN = true;
                if (cardHelperListener != null) {
                    cardHelperListener.onTransResultFallBack();
                }
                break;

        }
    }

    //region - Operation method

    private int CONNECTION_TIMEOUT = 5000;

    private ExecutorService sFixedThreadPool;
    private AidlTleService tleVersionOne;

    private boolean timeOut = false;

    public final static int PROCESS_REQUEST_CONNECTION_FAILED = 70;
    public final static int PROCESS_REQUEST_TRANSACTION_TIMEOUTED = 71;

    private CustomSocketListener customSocketListener = new CustomSocketListener() {
        @Override
        public void ConnectTimeOut() {
            Log.d(TAG, "ConnectTimeOut: ");
            response_code = "93";
            processCallback(PROCESS_REQUEST_CONNECTION_FAILED);
            if (connectStatusSocket != null) {
                connectStatusSocket.onConnectTimeOut();
            }
            if (responseCodeListener != null) {
                responseCodeListener.onConnectTimeOut();
            }
        }

        @Override
        public void TransactionTimeOut() {
            Log.d(TAG, "TransactionTimeOut: ");
            response_code = "91";
            processCallback(PROCESS_REQUEST_TRANSACTION_TIMEOUTED);
            if (connectStatusSocket != null) {
                connectStatusSocket.onTransactionTimeOut();
            }
            if (responseCodeListener != null) {
                responseCodeListener.onTransactionTimeOut();
            }
        }

        @Override
        public void Received(byte[] data) {
            Log.d(TAG, "RECEIVED DATA:" + bcd2Str(data));
            dealWithTheResponse(bcd2Str(data));

        }

        @Override
        public void Error(String error) {
            Log.d(TAG, "Error: ");
            if (connectStatusSocket != null) {
                connectStatusSocket.onError();
            }
        }

        @Override
        public void Other() {
            Log.d(TAG, "Other: ");
            if (connectStatusSocket != null) {
                connectStatusSocket.onOther();
            }
        }
    };

    public void setDataDefault() {
        batchUploadSize = 0;
        batchUpload = 0;
    }

    /**
     * SET DATASEND HOST
     */

    public void setDataFirstSettlement() {
        String terminalVersion = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String messageVersion = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionCode = Preference.getInstance(context).getValueString(Preference.KEY_TRANSACTION_CODE);
        String messageLen = "00000106";
        String terminalSN = "000025068";        // Paul_20180522
        String samId = "                ";      // Paul_20180522
        String samCsn = "                ";     // Paul_20180522
        String randomData = "        ";         // Paul_20180522
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "        ";           // Paul_20180522
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "920000";
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getTerminalId(context, "TMS"));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getMerchantId(context, "TMS"));
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((messageLen + terminalVersion + messageVersion + transactionCode + terminalSN + samId + samCsn + randomData + terminalCERT + checkSUM).length())) + BlockCalculateUtil.getHexString(messageLen + terminalVersion + messageVersion + transactionCode + terminalSN + samId + samCsn + randomData + terminalCERT + checkSUM);
        onLineNow = false;  // Paul_20180523
        packageAndSend(Preference.getInstance(context).getValueString(Preference.KEY_TPDU_TMS), "0800", mBlockDataSend);

    }

    public void setDataParameterDownload() {
        String terminalVersion = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String messageVersion = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionCode = Preference.getInstance(context).getValueString(Preference.KEY_TRANSACTION_CODE);
        String parameterVersion = Preference.getInstance(context).getValueString(Preference.KEY_PARAMETER_VERSION);
        String messageLen = "00000106";
        String terminalSN = "000025068";        // Paul_20180522
        String samId = "                ";      // Paul_20180522
        String samCsn = "                ";     // Paul_20180522
        String randomData = "        ";         // Paul_20180522
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "        ";           // Paul_20180522
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "900000";
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180522
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getTerminalId(context, "TMS"));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getMerchantId(context, "TMS"));
        String s63 = messageLen + terminalVersion + messageVersion + transactionCode + parameterVersion + randomData + terminalCERT + checkSUM;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((s63).length())) + BlockCalculateUtil.getHexString(s63);
        onLineNow = false;  // Paul_20180523
        packageAndSend(Preference.getInstance(context).getValueString(Preference.KEY_TPDU_TMS), "0800", mBlockDataSend);

    }

    public void setDataSalePIN(String amount, String pin, String ref1, String ref2, String ref3, String comCode) {
//        HOST_CARD = CardPrefix.getTypeCard(card.getNo());
        Log.d(TAG, "setDataSalePIN HOST_CARD: " + HOST_CARD);
        String keyPin;
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            keyPin = OnUsTMSinblock(card.getNo(), pin);
            Log.d(TAG, "setDataSalePIN keyPin : " + keyPin);
            setDataSalePINTMS(amount, pin, keyPin, ref1, ref2, ref3, comCode);
        } else {
//            Log.d(TAG, "setDataSalePIN HOST_CARD: " + HOST_CARD);
//            keyPin = OffUsEPSPinblock(card.getNo(), pin);
//            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
//            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
//            if (HOST_CARD.equalsIgnoreCase("TMS")) {
//                NII = CardPrefix.getNii(card.getNo(), context);     // Paul_20180523
//            } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
//                NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
//            }
//            String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
//            String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
//            CARD_NO = card.getNo();
//            AMOUNT = amount;
//            EXPIRY = card.getExpireDate();
//            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
//            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
//            mBlockDataSend = new String[64];
//
//            mBlockDataSend[3 - 1] = "003000";
//            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(amount);
//            mBlockDataSend[11 - 1] = calNumTraceNo(traceIdNo);
////            mBlockDataSend[14 - 1] = card.getExpireDate();
//            mBlockDataSend[22 - 1] = "0051";
//            mBlockDataSend[23 - 1] = tagPanSnEMV.substring(4, 8);
//            mBlockDataSend[24 - 1] = NII;
//            Log.d(TAG, "setDataSalePIN TRACK2: " + NII);
//            Log.d(TAG, "setDataSalePIN TRACK2: " + TRACK2);
//            mBlockDataSend[35 - 1] = TRACK2;
//            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
//            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
//            mBlockDataSend[52 - 1] = keyPin;
//            if (HOST_CARD.equalsIgnoreCase("EPS") || HOST_CARD.equalsIgnoreCase("POS")) {
//                mBlockDataSend[55 - 1] = MBLOCK55;
//            }
//            mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
//            onLineNow = true;
//            TPDU = CardPrefix.getTPDU(context, HOST_CARD);
//            packageAndSend(TPDU, MESSAGE_SALE, mBlockDataSend);
        }

    }

    public void setImportAmountEPS(String amount, String pin, String ref1, String ref2, String ref3, String comCode) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
            Double amountFee = (Float.valueOf(amount) * fee) / 100;
            String keyPin = OffUsEPSPinblock(card.getNo(), pin);
            COMCODE = comCode;
            REF1 = ref1;
            REF2 = ref2;
            REF3 = ref3;
            Double amountAll = Double.valueOf(amount) + amountFee;
            pboc2.importAmount(decimalFormat.format(amountAll));
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
            mBlockDataSend[52 - 1] = keyPin;
            AMOUNT = amount;

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setDataSalePINEPS() {
        String tran = Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_EPS);
        mBlockDataSend[11 - 1] = calNumTraceNo(tran);

    }

    private void setDataSalePINTMS(String amount, String pin, String keyPin, String ref1, String ref2, String ref3, String comCode) {

        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        Log.d(TAG, "setDataSalePIN: " + keyPin);
        NII = CardPrefix.getNii(card.getNo(), context);     // Paul_20180523
        String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
        CARD_NO = card.getNo();
        AMOUNT = amount;
        EXPIRY = card.getExpireDate();
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        COMCODE = comCode;
        REF1 = ref1;
        REF2 = ref2;
        REF3 = ref3;
        Date date = new Date();
        String datePatten = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = card.getNo().length() + card.getNo();
        mBlockDataSend[3 - 1] = "003000";
        Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        Double amountFee1 = (Double.valueOf(AMOUNT) * fee) / 100;
        Double amountAll = Double.valueOf(amount) + amountFee1;
        Log.d(TAG, "setDataSalePINTMS amount: " + amount + " fee : " + fee + " amountFee1 : " + amountFee1  + " amountAll : " + amountAll);
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        mBlockDataSend[11 - 1] = calNumTraceNo(traceIdNo);
//        mBlockDataSend[14 - 1] = card.getExpireDate();        // Paul_20180522
        mBlockDataSend[22 - 1] = "0022";
//        mBlockDataSend[24 - 1] = "0346";                        // Paul_20180522 NEXT Change
        mBlockDataSend[24 - 1] = NII;
        mBlockDataSend[25 - 1] = "05";
        Log.d(TAG, "Track2MappingTable 1: " + TRACK2);
        String track2 = TRACK2.substring(2, TRACK2.length());
        Log.d(TAG, "Track2MappingTable 2: " + track2);
        mBlockDataSend[35 - 1] = "37" + Track2MappingTable(track2, datePatten) + "0";
        Log.d(TAG, "Track2MappingTable:  TRACK2 : " + track2 + " \n length : " + track2.length() + "\n datePatten : = " + datePatten + "\n Track2MappingTable " + mBlockDataSend[35 - 1]);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(calNumTraceNo(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_TMS)));
        Log.d(TAG, "Track2MappingTable:  mBlockDataSend[41 - 1]  " + mBlockDataSend[41 - 1]);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(calNumTraceNo(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_TMS)));
        mBlockDataSend[52 - 1] = keyPin;
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String messageV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionC = "8014";
        String batch = CardPrefix.calLen(CardPrefix.getBatch(context, "TMS"), 8);
        String trace = "00000000";
        String mBlock63 = "00000340" + terminalV + messageV +
                transactionC +
                batch +
                trace +
                CardPrefix.calSpenLen(COMCODE, 10) +
                CardPrefix.calSpenLen(REF1, 50) +
                CardPrefix.calSpenLen(REF2, 50) +
                CardPrefix.calSpenLen(REF3, 50) +
                datePatten +
                "00" +
                CardPrefix.calSpenLen("", 14) +
                CardPrefix.calSpenLen("", 8);
        Log.d(TAG, "setDataSalePINTMS: " + mBlock63);
        mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);
        Log.d(TAG, "setDataSalePINTMS: " + mBlockDataSend[63 - 1]);
        // Paul_20180522 Start
        if (MAG_TRX_RECV == true) {
            onLineNow = false;
//            return;
        } else {
            onLineNow = true;
        }
// Paul_20180522 End
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        packageAndSend(TPDU, MESSAGE_SALE, mBlockDataSend);
    }

    public void setFalseFallbackHappen() {
        FALLBACK_HAPPEN = false;
    }

    public void setDataSaleFallBack(String amount) {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        HOST_CARD = CardPrefix.getTypeCard(card.getNo());
        NII = CardPrefix.getNii(card.getNo(), context);     // Paul_20180523
        String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
        CARD_NO = card.getNo();
        AMOUNT = amount;
        EXPIRY = card.getExpireDate();
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "003000";
        Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        Double amountFee1 = (Double.valueOf(AMOUNT) * fee) / 100;
        Double amountAll = Double.valueOf(amount) + amountFee1;
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        mBlockDataSend[11 - 1] = calNumTraceNo(traceIdNo);
        mBlockDataSend[14 - 1] = card.getExpireDate();
        mBlockDataSend[22 - 1] = "0801";
        mBlockDataSend[24 - 1] = NII;
        mBlockDataSend[35 - 1] = TRACK2;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        packageAndSend(TPDU, MESSAGE_SALE, mBlockDataSend);
    }

    public void setDataVoid(TransTemp temp) {
        transTemp = temp;
        HOST_CARD = transTemp.getHostTypeCard();
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            setDataVoidTMS();
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            setDataVoidEPS();
        } else if (HOST_CARD.equalsIgnoreCase("POS")) {
            setDataVoidPOS();
        }
    }

    private void setDataVoidPOS() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        mBlockDataSend = null;
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo();
        CARD_NO = transTemp.getCardNo();
        AMOUNT = transTemp.getAmount();
        TRACK1 = transTemp.getTrack1();
        TRACK2 = transTemp.getTrack2();
        PROCESSING_CODE = transTemp.getProcCode();
        EXPIRY = transTemp.getExpiry();
        mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
        POSEM = transTemp.getPosem();
        POSOC = transTemp.getPosoc();
        NII = transTemp.getNii();
        PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
        Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        Double amountFee1 = (Double.valueOf(transTemp.getAmount()) * fee) / 100;
        Double amountAll = Double.valueOf(transTemp.getAmount()) + amountFee1;
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        AMOUNT = transTemp.getAmount();
        mBlockDataSend[11 - 1] = transTemp.getTraceNo();
        mBlockDataSend[14 - 1] = transTemp.getExpiry();
        mBlockDataSend[22 - 1] = transTemp.getPointServiceEntryMode();
        if (transTemp.getApplicationPAN() != null) {
            mBlockDataSend[23 - 1] = transTemp.getApplicationPAN();
        }
        mBlockDataSend[24 - 1] = transTemp.getNii();
        mBlockDataSend[25 - 1] = "05";
        mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.getRefNo());
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        if (transTemp.getIccData() != null) {
            mBlockDataSend[55 - 1] = transTemp.getIccData();
        }
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.getEcr());
        Log.d(TAG, "mBlockDataSend[2 - 1]: " + transTemp.getCardNo().length() + transTemp.getCardNo() +
                " \n mBlockDataSend[3 - 1]: " + VOID_PROCESSING_CODE +
                " \n mBlockDataSend[4 - 1]" + BlockCalculateUtil.getAmount(transTemp.getAmount()) + "\n" +
                "mBlockDataSend[11 - 1]" + transTemp.getTraceNo() + "\n" +
                "" +
                "mBlockDataSend[14 - 1]" + transTemp.getExpiry() + "\n" +
                "" +
                "mBlockDataSend[22 - 1]" + POS_ENT_MODE + "\n" +
                "" +
                "mBlockDataSend[23 - 1]" + "0000" + "\n" +
                "" +
                "mBlockDataSend[24 - 1]" + transTemp.getNii() + "\n" +
                "" +
                "mBlockDataSend[25 - 1]" + "05" + "\n" +
                "" +
                "mBlockDataSend[37 - 1]" + transTemp.getRefNo() + "\n" +
                "" +
                "mBlockDataSend[41 - 1]" + BlockCalculateUtil.getHexString(TERMINAL_ID) + "\n" +
                "" +
                "mBlockDataSend[42 - 1]" + BlockCalculateUtil.getHexString(MERCHANT_NUMBER) + "\n" +
                "" +
                "mBlockDataSend[55 - 1]" + transTemp.getIccData() + "\n" +
                "" +
                "mBlockDataSend[62 - 1]" + transTemp.getEcr() + "\n" +
                "");
        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        packageAndSend(TPDU, MESSAGE_VOID, mBlockDataSend);
    }

    private void setDataVoidEPS() {
        HOST_CARD = "EPS";
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        mBlockDataSend = null;
        CARD_NO = transTemp.getCardNo();
        AMOUNT = transTemp.getAmount();
        TRACK1 = transTemp.getTrack1();
        TRACK2 = transTemp.getTrack2();
        PROCESSING_CODE = transTemp.getProcCode();
        EXPIRY = transTemp.getExpiry();
        POSEM = transTemp.getPosem();
        POSOC = transTemp.getPosoc();
        NII = transTemp.getNii();
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo();
        mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
        PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
        Double amountAll = Double.valueOf(transTemp.getAmount()) + Double.valueOf(transTemp.getFee());
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        AMOUNT = transTemp.getAmount();
        mBlockDataSend[11 - 1] = transTemp.getTraceNo();
        mBlockDataSend[14 - 1] = transTemp.getExpiry();
        mBlockDataSend[22 - 1] = transTemp.getPointServiceEntryMode();
        mBlockDataSend[23 - 1] = transTemp.getApplicationPAN();
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        if (transTemp.getIccData() != null) {
            mBlockDataSend[25 - 1] = "05";
        } else {
            mBlockDataSend[25 - 1] = "00";
        }
        mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.getRefNo());
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        if (transTemp.getIccData() != null) {
            mBlockDataSend[55 - 1] = transTemp.getIccData();
        }
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.getEcr());
        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        packageAndSend(TPDU, MESSAGE_VOID, mBlockDataSend);
    }

    private void setDataVoidTMS() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
        Date dateTime = new Date();
        String datePatten = new SimpleDateFormat("yyyyMMddHHmmss").format(dateTime);
        mBlockDataSend = null;
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo();
        mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
        CARD_NO = transTemp.getCardNo();
        AMOUNT = transTemp.getAmount();
        TRACK1 = transTemp.getTrack1();
        TRACK2 = transTemp.getTrack2();
        PROCESSING_CODE = transTemp.getProcCode();
        EXPIRY = transTemp.getExpiry();
        POSEM = transTemp.getPosem();
        POSOC = transTemp.getPosoc();
        NII = transTemp.getNii();
        PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
        Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        Double amountFee1 = (Double.valueOf(transTemp.getAmount()) * fee) / 100;
        Double amountAll = Double.valueOf(transTemp.getAmount()) + amountFee1;
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        AMOUNT = transTemp.getAmount();
        mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceIdPlus(context, "TMS"));
        mBlockDataSend[22 - 1] = transTemp.getPointServiceEntryMode();
        if (transTemp.getApplicationPAN() != null) {
            mBlockDataSend[23 - 1] = transTemp.getApplicationPAN();
        }
        mBlockDataSend[24 - 1] = transTemp.getNii();
        mBlockDataSend[25 - 1] = "05";
        mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.getRefNo());
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(calNumTraceNo(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_TMS)));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(calNumTraceNo(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_TMS)));
        if (transTemp.getIccData() != null) {
            mBlockDataSend[55 - 1] = transTemp.getIccData();
        }
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.getEcr());
        String msgLen = "00000340";
        String terVer = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String msgVer = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String tranCode = "8034";//Preference.getInstance(context).getValueString(Preference.KEY_TRANSACTION_CODE);
        String batch = "00" + batchNumber;
        String comCode = CardPrefix.calSpenLen(transTemp.getComCode(), 10);
        String ref1 = CardPrefix.calSpenLen(transTemp.getRef1(), 50);
        String ref2 = CardPrefix.calSpenLen(transTemp.getRef2(), 50);
        String ref3 = CardPrefix.calSpenLen(transTemp.getRef3(), 50);
        String tranNotmp = String.valueOf(Integer.valueOf(transTemp.getTraceNo()) - 1);
        String tranNo = "00000000".substring(tranNotmp.length()) + tranNotmp;
        String date = datePatten;//transTemp.getTransDate() + transTemp.getTransTime().replace(":", "");
//        date = "     " + date.substring(5, date.length());
        String trackNotmp = Track2MappingTable(transTemp.getTrack2().substring(2, transTemp.getTrack2().length()), date);
        String track2 = trackNotmp + ("                                        ".substring(trackNotmp.length()));
        String pin = transTemp.getPin();
        String randomData = "  ";
        String terminalCERT = CardPrefix.calSpenLen("", 14);
        String checkSum = CardPrefix.calSpenLen("", 8);
        String hex = "";
        String hxxx = "";
        for (int i = 0; i < pin.length(); i++) {
            char ch = pin.charAt(i);
            hex += String.format("%02X", (int) ch);
//            Log.d(TAG, "YII pin : " + pin);
//            Log.d(TAG, "YII ch : " + ch + " Hax : " + String.format("%02X", (int) ch));
//            hxxx += "+" + String.format("%02X", (int) ch);
//            Log.d(TAG, "YII hxxx : " + hxxx);
        }
        Log.d(TAG, "setDataVoidTMS: transTemp.getTrack2() : " + transTemp.getTrack2() +
                "\n msgLen :" + msgLen + " \n terVer : " + terVer +
                "\n msgVer : " + msgVer +
                "\n tranCode : " + tranCode +
                "\n batch : " + batch +
                "\n comCode : " + comCode +
                "\n ref1 : " + ref1 +
                "\n ref2 : " + ref2 +
                "\n ref3 : " + ref3 +
                "\n tranNo : " + tranNo +
                "\n date :" + date +
                "\n track2 : " + track2 +
                "\n pin : " + pin +
                "\n randomData : " + randomData +
                "\n terminalCERT : " + terminalCERT +
                "\n checkSum : " + checkSum +
                "\n hex : " + BlockCalculateUtil.hexToString(hex));
        String mBlock63 = msgLen + terVer + msgVer + tranCode +
                batch + tranNo + comCode + ref1 + ref2 + ref3 + date +
                track2 + BlockCalculateUtil.hexToString(hex) + randomData + terminalCERT + checkSum;

        mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);
        Log.d(TAG, "setDataVoidTMS: " + mBlockDataSend[63 - 1]);
        onLineNow = false;      // Paul_20180522
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        packageAndSend(TPDU, MESSAGE_VOID, mBlockDataSend);
    }

    public void setCheckTCUpload(String typeHost, boolean type) {
        typeCheck = type;
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        try {
            HOST_CARD = typeHost;
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            RealmResults<TCUpload> tcUpload = realm.where(TCUpload.class).equalTo("hostTypeCard", HOST_CARD).equalTo("statusTC", "0").findAll();
            Log.d(TAG, "setCheckTCUpload tcUpload: " + tcUpload.size());
            tcUploadPosition = 0;
            Log.d(TAG, "setCheckTCUpload tcUploadPosition: " + tcUploadPosition);
            if (tcUpload.size() > 0) {
                tcUploadSize = tcUpload.size();
                tcUploadDb = tcUpload.get(tcUploadPosition);
                tcUploadId = tcUploadDb.getTraceNo();
                Log.d(TAG, "setCheckTCUpload: " + tcUploadDb.getTraceNo());
                Log.d(TAG, "setTCUpload: " + HOST_CARD);
                MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
                TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
                mBlockDataSend = new String[64];
                mBlockDataSend[2 - 1] = tcUpload.get(tcUploadPosition).getCardNo().length() + tcUpload.get(tcUploadPosition).getCardNo();
                mBlockDataSend[3 - 1] = "943000";
                double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
                double amountFee1 = (Double.valueOf(AMOUNT) * fee) / 100;
                double amountAll = Double.valueOf(tcUpload.get(tcUploadPosition).getAmount()) + amountFee1;
                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
                mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceId(context, HOST_CARD));
                mBlockDataSend[12 - 1] = tcUpload.get(tcUploadPosition).getTransTime().replace(":", "");
                mBlockDataSend[13 - 1] = tcUpload.get(tcUploadPosition).getTransDate().substring(4, 8);
                mBlockDataSend[14 - 1] = tcUpload.get(tcUploadPosition).getExpiry();
                mBlockDataSend[22 - 1] = tcUpload.get(tcUploadPosition).getPointServiceEntryMode();
                mBlockDataSend[23 - 1] = tcUpload.get(tcUploadPosition).getApplicationPAN();
                if (HOST_CARD.equalsIgnoreCase("POS")) {
                    mBlockDataSend[24 - 1] = CardPrefix.getNii(tcUpload.get(tcUploadPosition).getCardNo(), context);
                } else {
                    mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
                }
                if (tcUpload.get(tcUploadPosition).getIccData() != null) {
                    mBlockDataSend[25 - 1] = "05";
                } else {
                    mBlockDataSend[25 - 1] = "00";
                }
                mBlockDataSend[37 - 1] = tcUpload.get(tcUploadPosition).getRefNo();
                mBlockDataSend[38 - 1] = tcUpload.get(tcUploadPosition).getApprvCode();
                mBlockDataSend[39 - 1] = tcUpload.get(tcUploadPosition).getRespCode();
                mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
                mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        /*if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[52 - 1] = pin;
        }*/
                mBlockDataSend[55 - 1] = tcUpload.get(tcUploadPosition).getIccData();
                String invoiceNumber;
                if (HOST_CARD.equalsIgnoreCase("POS")) {
                    invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS);
                } else {
                    invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS);
                }
                if (HOST_CARD.equalsIgnoreCase("EPS")) {
                    String f60 = "0200" + calNumTraceNo(CardPrefix.geTraceIdPlus(context, HOST_CARD)) + BlockCalculateUtil.hexToString(tcUpload.get(tcUploadPosition).getRefNo());
                    mBlockDataSend[60 - 1] = CardPrefix.calLen(String.valueOf(f60.length()), 4) + BlockCalculateUtil.getHexString(f60);

                }
                mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                onLineNow = true;
                TPDU = CardPrefix.getTPDU(context, HOST_CARD);
                packageAndSend(TPDU, "0320", mBlockDataSend);

                tcUploadPosition++;
            } else {
                switch (HOST_CARD) {
                    case "EPS":
                        setDataSettlementAndSendEPS();
                        break;
                    case "POS":
                        setDataSettlementAndSend("POS");
                        break;
                    default:
                        setDataSettlementAndSendTMS();
                        break;
                }
            }

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    public void setDataSettlementAndSend(String typeHost) {
        HOST_CARD = typeHost;
        setUploadCredit();
        String traceIdNo;
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            int timeCount = 0;
            int amountAll = 0;
            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            RealmResults<TransTemp> transTempVoidFlag = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
            if (transTempVoidFlag.size() != 0) {
                timeCount = transTempVoidFlag.size();
                for (int i = 0; i < transTempVoidFlag.size(); i++) {
                    amountAll += Float.valueOf(transTempVoidFlag.get(i).getAmount());
                }
            }
            String nii = "";
            if (typeHost.equalsIgnoreCase("POS")) {
                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
            } else if (typeHost.equalsIgnoreCase("EMS")) {
                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            } else if (typeHost.equalsIgnoreCase("TMS")) {
                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
            }
            String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
            mBlockDataSend = new String[64];
            mBlockDataSend[3 - 1] = SETTLEMENT_PROCESSING_CODE;
            PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
            mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
            mBlockDataSend[24 - 1] = nii;
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, String.valueOf(amountAll));
            MTI = MESSAGE_SETTLEMENT;
            onLineNow = false;
            TPDU = CardPrefix.getTPDU(context, HOST_CARD);
            packageAndSend(TPDU, MTI, mBlockDataSend);

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    public void setDataSettlementAndSendTMS() {
        HOST_CARD = "TMS";
        String traceIdNo;
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            int payCount = 0;
            float amountAll = 0;
            int voidCount = 0;
            float amountVoidAll = 0;
            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            RealmResults<TransTemp> transTempVoidFlag = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
            if (transTempVoidFlag.size() != 0) {
                payCount = transTempVoidFlag.size();
                for (int i = 0; i < transTempVoidFlag.size(); i++) {
                    amountAll += Float.valueOf(transTempVoidFlag.get(i).getAmount());
                }
            }
            RealmResults<TransTemp> transTempVoidYFlag = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("hostTypeCard", HOST_CARD).findAll();
            if (transTempVoidFlag.size() != 0) {
                voidCount = transTempVoidYFlag.size();
//                for (int i = 0; i < transTempVoidYFlag.size(); i++) {
////                    amountVoidAll += Float.valueOf(transTempVoidYFlag.get(i).getAmount());
//                }
            }
            String nii = "";
            nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
            String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);

            String msgLen = "00000121";
            String terVer = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
            String msgVer = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
            String tranCode = "6012";
            String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
            String transaction = CardPrefix.calLen(String.valueOf((payCount + voidCount)), 5);
            String totalPayCount = CardPrefix.calLen(String.valueOf(payCount), 5);

            String amountPayAllToStr = String.format("%.2f", amountAll).replace(".", "");

            String totalPayAmount = CardPrefix.calLen(String.valueOf(amountPayAllToStr), 10);

            String totalVoidCount = CardPrefix.calLen(String.valueOf(0), 5);

            String amountVoidAllToStr = String.format("%.2f", amountVoidAll).replace(".", "");

            String totalVoidAmount = CardPrefix.calLen(String.valueOf(amountVoidAllToStr), 10);
            String totalRefundCount = CardPrefix.calLen("", 5);
            String totalRefundAmount = CardPrefix.calLen("", 10);
            String randomData = CardPrefix.calSpenLen("", 5);
            String terminalCERT = CardPrefix.calSpenLen("", 14);
            String checkSum = CardPrefix.calSpenLen("", 8);

            mBlockDataSend = new String[64];
            mBlockDataSend[3 - 1] = SETTLEMENT_PROCESSING_CODE;
            mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
            mBlockDataSend[24 - 1] = nii;
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
            String mBlock61 = msgLen + terVer + msgVer + tranCode + batchNo +
                    transaction + totalPayCount + totalPayAmount + totalVoidCount +
                    totalVoidAmount + totalRefundCount + totalRefundAmount + randomData +
                    terminalCERT + checkSum;
            mBlockDataSend[61 - 1] = CardPrefix.calLen(mBlock61.length() + "", 4) + BlockCalculateUtil.getHexString(mBlock61);
            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(payCount, amountPayAllToStr);
            settlement61 = mBlockDataSend[61 - 1];
            settlement63 = mBlockDataSend[63 - 1];
            MTI = MESSAGE_SETTLEMENT;
            onLineNow = false;
            TPDU = CardPrefix.getTPDU(context, HOST_CARD);
            packageAndSend(TPDU, MTI, mBlockDataSend);

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    public void setDataSettlementAndSendEPS() {
        HOST_CARD = "EPS";
        setUploadCredit();
        String traceIdNo;
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            int timeCount = 0;
            int amountAll = 0;
            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            RealmResults<TransTemp> transTempVoidFlag = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
            if (transTempVoidFlag.size() != 0) {
                timeCount = transTempVoidFlag.size();
                for (int i = 0; i < transTempVoidFlag.size(); i++) {
                    amountAll += Float.valueOf(transTempVoidFlag.get(i).getAmount());
                }
            }
            String nii = "";
            nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
            mBlockDataSend = new String[64];
            mBlockDataSend[3 - 1] = SETTLEMENT_PROCESSING_CODE;
            PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
            mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
            mBlockDataSend[24 - 1] = nii;
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, String.valueOf(amountAll));
            MTI = MESSAGE_SETTLEMENT;
            onLineNow = false;
            Log.d(TAG, "mBlockDataSend[3 - 1]: " + SETTLEMENT_PROCESSING_CODE
                    + "\n mBlockDataSend[11 - 1]:" + BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo))
                    + "\n mBlockDataSend[24 - 1]: 0245"
                    + "\n mBlockDataSend[41 - 1]:" + BlockCalculateUtil.getHexString(TERMINAL_ID)
                    + "\n mBlockDataSend[42 - 1]:" + BlockCalculateUtil.getHexString(MERCHANT_NUMBER)
                    + "\n mBlockDataSend[60 - 1]:" + getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber))
                    + "\n mBlockDataSend[63 - 1]:" + BlockCalculateUtil.get63BlockData(timeCount, String.valueOf(amountAll))
            );
            TPDU = CardPrefix.getTPDU(context, HOST_CARD);
            packageAndSend(TPDU, MTI, mBlockDataSend);

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    // Paul_20180523
    public void setDataReversalAndSendHost(ReversalTemp reversalTemp) {
        System.out.printf("utility:: setDataReversalAndSendHost 001 \n");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        HOST_CARD = reversalTemp.getHostTypeCard();
        if (HOST_CARD.equals("TMS")) {
            System.out.printf("utility:: setDataReversalAndSendHost 002 \n");
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
            onLineNow = false;
            mBlockDataSend = new String[64];
            mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
            if (reversalTemp.getTransStat().equals("SALE")) {
                mBlockDataSend[3 - 1] = REVERSAL_PROCESSING_CODE;
            } else if (reversalTemp.getTransStat().equals("VOID")) {
                mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
            }
            PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
            Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
            Double amountFee1 = (Double.valueOf(reversalTemp.getAmount()) * fee) / 100;
            Double amountAll = Float.valueOf(reversalTemp.getAmount()) + amountFee1;
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
            mBlockDataSend[11 - 1] = reversalTemp.getTraceNo();
//            mBlockDataSend[14 - 1] = reversalTemp.getExpiry();
            mBlockDataSend[22 - 1] = POS_ENT_MODE;
            if (reversalTemp.getApplicationPAN() != null) {
                mBlockDataSend[23 - 1] = reversalTemp.getApplicationPAN();
            }
            mBlockDataSend[24 - 1] = reversalTemp.getNii();
            mBlockDataSend[25 - 1] = "05";
            if (reversalTemp.getTransStat().equalsIgnoreCase("SALE")) {
                mBlockDataSend[35 - 1] = reversalTemp.getTrack2();
                if ((reversalTemp.getTrack2().length() % 2) != 0) {
                    mBlockDataSend[35 - 1] += "0";
                }
            }
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);

            mBlockDataSend[52 - 1] = reversalTemp.getPinblock();

            if (reversalTemp.getIccData() != null) {
                mBlockDataSend[55 - 1] = reversalTemp.getIccData();
            }

//            System.out.printf("utility:: setDataReversalAndSendHost mBlockDataSend[37 - 1] = %s \n",mBlockDataSend[37 - 1]);
            System.out.printf("utility:: setDataReversalAndSendHost reversalTemp.getEcr().length() = %d\n", reversalTemp.getEcr().length());
            System.out.printf("utility:: setDataReversalAndSendHost %s \n", reversalTemp.getEcr());

            mBlockDataSend[62 - 1] = getLength62(String.valueOf(reversalTemp.getEcr().length())) + BlockCalculateUtil.getHexString(reversalTemp.getEcr());
            mBlockDataSend[63 - 1] = reversalTemp.getField63();
        } else {
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
            onLineNow = true;
            mBlockDataSend = new String[64];
            if (HOST_CARD.equalsIgnoreCase("POS")) {
                mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
            }
            if (reversalTemp.getTransStat().equals("SALE")) {
                mBlockDataSend[3 - 1] = REVERSAL_PROCESSING_CODE;
            } else if (reversalTemp.getTransStat().equals("VOID")) {
                mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
            }
            PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
            float fee = Preference.getInstance(context).getValueFloat(Preference.KEY_FEE);
            float amountFee1 = (Float.valueOf(reversalTemp.getAmount()) * fee) / 100;
            float amountAll = Float.valueOf(reversalTemp.getAmount()) + amountFee1;
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
            mBlockDataSend[11 - 1] = reversalTemp.getTraceNo();
            if (HOST_CARD.equalsIgnoreCase("POS")) {
                mBlockDataSend[14 - 1] = reversalTemp.getExpiry();
            }
            mBlockDataSend[22 - 1] = POS_ENT_MODE;
            if (reversalTemp.getApplicationPAN() != null) {
                mBlockDataSend[23 - 1] = reversalTemp.getApplicationPAN();
            }
            String nii = "";
            if (HOST_CARD.equalsIgnoreCase("EPS")) {
                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            } else {
                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
            }
            mBlockDataSend[24 - 1] = nii;
            if (reversalTemp.getIccData() != null) {
                mBlockDataSend[25 - 1] = "05";
            } else {
                mBlockDataSend[25 - 1] = "00";
            }
            if (HOST_CARD.equalsIgnoreCase("EPS")) {
                mBlockDataSend[35 - 1] = reversalTemp.getTrack2();
            }
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            if (reversalTemp.getIccData() != null) {
                mBlockDataSend[55 - 1] = reversalTemp.getIccData();
            }
            mBlockDataSend[62 - 1] = getLength62(String.valueOf(reversalTemp.getEcr().length())) + BlockCalculateUtil.getHexString(reversalTemp.getEcr());

        }
        System.out.printf("utility:: setDataReversalAndSendHost 004 \n");

        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        System.out.printf("utility:: setDataReversalAndSendHost 005 \n");
// Paul Test
//        deleteReversal();
        packageAndSend(TPDU, MESSAGE_REVERSAL, mBlockDataSend);
        System.out.printf("utility:: setDataReversalAndSendHost 006 \n");
    }

    public void setTCUpload(String trackNo,
                            String time,
                            String date,
                            String mBlock55,
                            String ecr,
                            String mBlock23,
                            String pin, String f22) {
        Log.d(TAG, "setTCUpload: " + HOST_CARD);
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = card.getNo().length() + card.getNo();
        mBlockDataSend[3 - 1] = "943000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);
        mBlockDataSend[11 - 1] = calNumTraceNo(String.valueOf((Integer.valueOf(trackNo) + 1)));
        mBlockDataSend[12 - 1] = time.replace(":", "");
        mBlockDataSend[13 - 1] = date.substring(4, 8);
        mBlockDataSend[14 - 1] = card.getExpireDate();
        mBlockDataSend[22 - 1] = f22;
        if (mBlock23 != null) {
            mBlockDataSend[23 - 1] = mBlock23;
        }
        if (HOST_CARD.equalsIgnoreCase("POS")) {
            mBlockDataSend[24 - 1] = CardPrefix.getNii(card.getNo(), context);
        } else {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        }
        if (mBlock55 != null) {
            mBlockDataSend[25 - 1] = "05";
        } else {
            mBlockDataSend[25 - 1] = "00";
        }
        mBlockDataSend[37 - 1] = mBlockDataReceived[37 - 1];
        mBlockDataSend[38 - 1] = mBlockDataReceived[38 - 1];
        mBlockDataSend[39 - 1] = mBlockDataReceived[39 - 1];
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        /*if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[52 - 1] = pin;
        }*/
        mBlockDataSend[55 - 1] = mBlock55;
        String invoiceNumber;
        if (HOST_CARD.equalsIgnoreCase("POS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS);
        } else {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS);
        }
        if (HOST_CARD.equalsIgnoreCase("EPS")) {
            String f60 = "0200" + calNumTraceNo(String.valueOf((Integer.valueOf(trackNo) + 1))) + BlockCalculateUtil.hexToString(mBlockDataReceived[37 - 1]);
            mBlockDataSend[60 - 1] = CardPrefix.calLen(String.valueOf(f60.length()), 4) + BlockCalculateUtil.getHexString(f60);

        }
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        packageAndSend(TPDU, "0320", mBlockDataSend);
//        insertTCUploadTransaction(trackNo, time, date, mBlock55, ecr, mBlock23, pin, f22, amountFee);

    }

    private void setBatchUpload() {
        Log.d(TAG, "setBatchUpload: " + HOST_CARD);
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        if (HOST_CARD.equalsIgnoreCase("POS")) {
            batchOff();
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            batchOffEPS();
            Log.d(TAG, "setBatchUpload: " + HOST_CARD);
        } else {
            batchTMS();
        }
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        realm.close();
        realm = null;

    }

    private void batchOff() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        String traceIdNo = null;
        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
        int timeCount = 0;
        int amountAll = 0;
        timeCount = transTemp.size();
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        if (transTemp.size() > 0) {
            batchUploadSize = transTemp.size();
//            for (int i = 0; i < transTemp.size(); i++) {
            traceIdNo = CardPrefix.geTraceIdPlus(context, HOST_CARD);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
            amountAll += Float.valueOf(transTemp.get(batchUpload).getAmount());
            mBlockDataSend = new String[64];
            mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            mBlockDataSend[3 - 1] = "003000";
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Float.valueOf(transTemp.get(batchUpload).getAmount()) + Float.valueOf(transTemp.get(batchUpload).getFee())));
            mBlockDataSend[11 - 1] = calNumTraceNo(transTemp.get(batchUpload).getTraceNo());
            mBlockDataSend[12 - 1] = transTemp.get(batchUpload).getTransTime().replace(":", "");
            mBlockDataSend[13 - 1] = transTemp.get(batchUpload).getTransDate().substring(4, 8);
            mBlockDataSend[14 - 1] = transTemp.get(batchUpload).getExpiry();
            mBlockDataSend[22 - 1] = transTemp.get(batchUpload).getPointServiceEntryMode();
            if (transTemp.get(batchUpload).getApplicationPAN() != null) {
                mBlockDataSend[23 - 1] = transTemp.get(batchUpload).getApplicationPAN();
            }
            mBlockDataSend[24 - 1] = CardPrefix.getNii(transTemp.get(batchUpload).getCardNo(), context);
            mBlockDataSend[25 - 1] = "05";
            mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getRefNo());
            mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getApprvCode());
            mBlockDataSend[39 - 1] = BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getRespCode());
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[55 - 1] = transTemp.get(batchUpload).getIccData();
//                String s60 = "0320" + transTemp.get(batchUpload).getRefNo() + transTemp.get(batchUpload).getTraceNo();
            String s60 = "0200" + transTemp.get(batchUpload).getTraceNo() + transTemp.get(batchUpload).getRefNo();
            mBlockDataSend[60 - 1] = getLength62(String.valueOf(s60.length())) + BlockCalculateUtil.getHexString(s60);
            mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.get(batchUpload).getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getEcr());

            Log.d(TAG, "BatchUpload mBlockDataSend[2 - 1]: " + transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo()
                    + "\n BatchUpload mBlockDataSend[3 - 1]: 003000"
                    + "\n BatchUpload mBlockDataSend[4 - 1]:" + BlockCalculateUtil.getAmount(transTemp.get(batchUpload).getAmount())
                    + "\n BatchUpload mBlockDataSend[11 - 1]:" + calNumTraceNo(transTemp.get(batchUpload).getTraceNo())
                    + "\n BatchUpload mBlockDataSend[12 - 1]:" + transTemp.get(batchUpload).getTransTime().replace(":", "")
                    + "\n BatchUpload mBlockDataSend[13 - 1]:" + transTemp.get(batchUpload).getTransDate().substring(4, 8)
                    + "\n BatchUpload mBlockDataSend[14 - 1]:" + transTemp.get(batchUpload).getExpiry()
                    + "\n BatchUpload mBlockDataSend[22 - 1]:" + transTemp.get(batchUpload).getPointServiceEntryMode()
                    + "\n BatchUpload mBlockDataSend[23 - 1]:" + transTemp.get(batchUpload).getApplicationPAN()
                    + "\n BatchUpload mBlockDataSend[24 - 1]: 0245"
                    + "\n BatchUpload mBlockDataSend[25 - 1]: 05"
                    + "\n BatchUpload mBlockDataSend[37 - 1]:" + transTemp.get(batchUpload).getRefNo()
                    + "\n BatchUpload mBlockDataSend[38 - 1]:" + transTemp.get(batchUpload).getApprvCode()
                    + "\n BatchUpload mBlockDataSend[39 - 1]:" + transTemp.get(batchUpload).getRespCode()
                    + "\n BatchUpload mBlockDataSend[41 - 1]:" + BlockCalculateUtil.getHexString(TERMINAL_ID)
                    + "\n BatchUpload mBlockDataSend[42 - 1]:" + BlockCalculateUtil.getHexString(MERCHANT_NUMBER)
                    + "\n BatchUpload mBlockDataSend[55 - 1]:" + transTemp.get(batchUpload).getIccData()
                    + "\n BatchUpload mBlockDataSend[60 - 1]:" + getLength62(String.valueOf(s60.length())) + BlockCalculateUtil.getHexString(s60)
                    + "\n BatchUpload mBlockDataSend[62 - 1]:" + transTemp.get(batchUpload).getEcr());

            onLineNow = true;
            packageAndSend(TPDU, "0320", mBlockDataSend);
            batchUpload++;
//            }

        } else {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)) + 1);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
        }
        String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "960000";
        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
        if (HOST_CARD.equalsIgnoreCase("POS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        }
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
        mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, String.valueOf(amountAll));
        MTI = MESSAGE_SETTLEMENT;
        onLineNow = true;
        packageAndSend(TPDU, "0500", mBlockDataSend);
    }

    private void batchOffEPS() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        String traceIdNo = null;
        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
        int timeCount = 0;
        Double amountAll = 0.0;
        timeCount = transTemp.size();
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
        if (transTemp.size() > 0) {
            Log.d(TAG, "batchOffEPS batchUpload : " + batchUpload + " transTemp.size() : " + transTemp.size());
            batchUploadSize = transTemp.size();
            traceIdNo = CardPrefix.geTraceIdPlus(context, HOST_CARD);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
            Log.d(TAG, "batchOffEPS batchUpload: " + batchUpload);
            amountAll += Double.valueOf(transTemp.get(batchUpload).getAmount());
            mBlockDataSend = new String[64];
            mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            mBlockDataSend[3 - 1] = "003000";
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Double.valueOf(transTemp.get(batchUpload).getAmount())) + Double.valueOf(transTemp.get(batchUpload).getFee()));
//                mBlockDataSend[11 - 1] = calNumTraceNo(transTemp.get(i).getTraceNo());
            mBlockDataSend[11 - 1] = calNumTraceNo(traceIdNo);
            mBlockDataSend[12 - 1] = transTemp.get(batchUpload).getTransTime().replace(":", "");
            mBlockDataSend[13 - 1] = transTemp.get(batchUpload).getTransDate().substring(4, 8);
            mBlockDataSend[14 - 1] = transTemp.get(batchUpload).getExpiry();
            mBlockDataSend[22 - 1] = transTemp.get(batchUpload).getPointServiceEntryMode();
            if (transTemp.get(batchUpload).getApplicationPAN() != null) {
                mBlockDataSend[23 - 1] = transTemp.get(batchUpload).getApplicationPAN();
            }
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            if (transTemp.get(batchUpload).getIccData() != null) {
                mBlockDataSend[25 - 1] = "05";
            } else {
                mBlockDataSend[25 - 1] = "00";
            }
            mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getRefNo());
            mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getApprvCode());
            mBlockDataSend[39 - 1] = BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getRespCode());
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[55 - 1] = transTemp.get(batchUpload).getIccData();
//                String s60 = "0320" + transTemp.get(i).getRefNo() + transTemp.get(i).getTraceNo();
            String s60 = "0200" + transTemp.get(batchUpload).getTraceNo() + transTemp.get(batchUpload).getRefNo();
            mBlockDataSend[60 - 1] = getLength62(String.valueOf(s60.length())) + BlockCalculateUtil.getHexString(s60);
            mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.get(batchUpload).getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getEcr());

            onLineNow = false;
            packageAndSend(TPDU, "0320", mBlockDataSend);
            batchUpload++;

        } else {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)) + 1);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
        }
        String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "960000";
        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
        if (HOST_CARD.equalsIgnoreCase("POS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        }
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
        mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, String.valueOf(amountAll));
        MTI = MESSAGE_SETTLEMENT;
        onLineNow = true;
        packageAndSend(TPDU, "0500", mBlockDataSend);
    }

    private void batchTMS() {
        TERMINAL_ID = CardPrefix.getTerminalId(context, "TMS");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "TMS");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
        int timeCount = 0;
        int amountAll = 0;
        timeCount = transTemp.size();
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        String traceIdNo = null;
        String msgLen = "00000096";
        String terVer;
        String msgVer;
        String tranCode;
        String batchNo;
        String tranCodeLog;
        String reference;
        String date;
        String transactionStatus;
        String randomData;
        String terminalCERT;
        String checkSUM;
        if (transTemp.size() > 0) {
            Log.d(TAG, "batchTMS: " + batchUploadSize + " batchUpload" + batchUpload);
            batchUploadSize = transTemp.size();
            traceIdNo = CardPrefix.geTraceIdPlus(context, HOST_CARD);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
//            amountAll += Float.valueOf(transTemp.get(batchUpload).getAmount());
            mBlockDataSend = new String[64];
            mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            mBlockDataSend[3 - 1] = "003000";
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Float.valueOf(transTemp.get(batchUpload).getAmount()) + Float.valueOf(transTemp.get(batchUpload).getFee())));
            mBlockDataSend[11 - 1] = calNumTraceNo(transTemp.get(batchUpload).getTraceNo());
            mBlockDataSend[12 - 1] = transTemp.get(batchUpload).getTransTime().replace(":", "");
            mBlockDataSend[13 - 1] = transTemp.get(batchUpload).getTransDate().substring(4, 8);
//                mBlockDataSend[14 - 1] = transTemp.get(i).getExpiry();
            mBlockDataSend[22 - 1] = transTemp.get(batchUpload).getPointServiceEntryMode();
            mBlockDataSend[24 - 1] = CardPrefix.getNii(transTemp.get(batchUpload).getCardNo(), context);
            mBlockDataSend[25 - 1] = "05";
            mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(CardPrefix.calLen("0", 12));
            mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(CardPrefix.calLen("0", 6));
            mBlockDataSend[39 - 1] = BlockCalculateUtil.getHexString(CardPrefix.calLen("0", 2));
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.get(batchUpload).getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getEcr());
            terVer = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
            msgVer = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
            tranCode = "6014";
            batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
            tranCodeLog = "8014";
            reference = CardPrefix.calLen("0", 8);
            date = transTemp.get(batchUpload).getTransDate() + transTemp.get(batchUpload).getTransTime().replace(":", "");
            transactionStatus = "S";
            randomData = "000";
            terminalCERT = CardPrefix.calSpenLen("", 14);
            checkSUM = CardPrefix.calSpenLen("", 8);
            String mBlock63 = msgLen + terVer + msgVer + tranCode + batchNo + tranCodeLog + reference +
                    date + transactionStatus + randomData + terminalCERT + checkSUM;
            String mBlock63Start = msgLen + terVer + msgVer + tranCode + batchNo + tranCodeLog + reference +
                    date;
            String mBlock63End = randomData + terminalCERT + checkSUM;
            mBlock63Start = BlockCalculateUtil.getHexString(mBlock63Start);
            mBlock63End = BlockCalculateUtil.getHexString(mBlock63End);
            String mBlock63All = mBlock63Start + "53" + mBlock63End;
            mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + mBlock63All;
            onLineNow = true;
            packageAndSend(TPDU, "0320", mBlockDataSend);

            batchUpload++;

        } else {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)) + 1);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
        }
//        String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
//        mBlockDataSend = new String[64];
//        mBlockDataSend[3 - 1] = "960000";
//        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
//        if (HOST_CARD.equalsIgnoreCase("POS")) {
//            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
//        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
//            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
//        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
//            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
//        }
//        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
//        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
//        mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
//        mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, String.valueOf(amountAll));
//        MTI = MESSAGE_SETTLEMENT;
//        onLineNow = true;
//        packageAndSend(TPDU, "0500", mBlockDataSend);
    }

    private void setOnlineUploadCredit(String mBlock55,
                                       String mBlock22, Double amountFee) {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Log.d(TAG, "setTCUpload: " + HOST_CARD);
        Date date1 = new Date();
        String time = new SimpleDateFormat("HHmmss").format(date1);
        String dateTime = new SimpleDateFormat("MMdd").format(date1);
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "TMS");
        TERMINAL_ID = CardPrefix.getTerminalId(context, "TMS");
        String f22 = "";
        if (HOST_CARD.equalsIgnoreCase("EPS")) {
            f22 = "0052";
        } else if (HOST_CARD.equalsIgnoreCase("POS")) {
            f22 = "0051";
        } else {
            f22 = "0022";
        }
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = card.getNo().length() + card.getNo();
        mBlockDataSend[3 - 1] = "490000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Double.valueOf(AMOUNT) + amountFee));
        mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceIdPlus(context, "TMS"));
        mBlockDataSend[12 - 1] = time;
        mBlockDataSend[13 - 1] = dateTime;
        mBlockDataSend[22 - 1] = f22;
        /*if (HOST_CARD.equalsIgnoreCase("POS")) {
            mBlockDataSend[24 - 1] = CardPrefix.getNii(card.getNo(), context);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        }*/
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        if (mBlock55 != null) {
            mBlockDataSend[25 - 1] = "05";
        } else {
            mBlockDataSend[25 - 1] = "00";
        }
        mBlockDataSend[37 - 1] = F37;
        mBlockDataSend[38 - 1] = F38;
        mBlockDataSend[39 - 1] = F39;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        /*if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[52 - 1] = pin;
        }*/
        String invoiceNumber;
        /*if (HOST_CARD.equalsIgnoreCase("POS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS);
        } else {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
        }*/
        invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        String msgLen = "00000390";
        String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String msgV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionC = "8056";
        String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
        String transactionNo = "00000000";
        String comCode = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1001), 10);
        String ref1 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1002), 50);
        String ref2 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1003), 50);
        String ref3 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1004), 50);
        String dateTime63 = new SimpleDateFormat("yyyyMMddHHmmss").format(date1);
        StringBuilder cardStringBuilder = new StringBuilder(card.getNo());
        cardStringBuilder.replace(7, 12, "X");
        String cardNo = card.getNo();
        String feeAmount = CardPrefix.calLen(decimalFormat.format(amountFee).replace(".", ""), 10);
        Double feeDou = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        String feeRate = CardPrefix.calLen(String.valueOf(decimalFormat.format(feeDou)).replace(".",""), 4);
        Log.d(TAG, "setOnlineUploadCredit feeRate : " + feeRate);
        String feeType = "F";
        String terId = CardPrefix.getTerminalId(context, HOST_CARD);
        String tex = Preference.getInstance(context).getValueString(Preference.KEY_TAX_INVOICE_NO);
        String posID = CardPrefix.calSpenLen("2222", 20);
        String merchantID = CardPrefix.getMerchantId(context, HOST_CARD);
        String texId = Preference.getInstance(context).getValueString(Preference.KEY_TAX_ID);
        String random = CardPrefix.calSpenLen("", 2);
        String terminalCERT = CardPrefix.calSpenLen("", 14);
        String checkSUM = CardPrefix.calSpenLen("", 8);


        String cardStar = card.getNo().substring(0, 6);
        String cardEnd = card.getNo().substring(12, 16);

        Log.d(TAG, "setOnlineUploadCredit: " + card.getNo() + " \n cardStar : " + cardStar + " \n cardEnd : " + cardEnd);

        String f63 = msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 +
                ref2 + ref3 + dateTime63 + cardNo + feeAmount + feeRate + feeType + terId + tex + posID + merchantID + texId + random + terminalCERT + checkSUM;
        String f63Start = BlockCalculateUtil.getHexString(msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 +
                ref2 + ref3 + dateTime63 + cardStar) + "585858585858";
        String f63End1 = BlockCalculateUtil.getHexString(cardEnd + feeAmount + feeRate) + "50";
        String f63End2 = BlockCalculateUtil.getHexString(terId + tex + posID + merchantID + texId + random + terminalCERT + checkSUM);

        String f63All = f63Start + f63End1 + f63End2;
        mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(f63.length()), 4) + f63All;


        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, "TMS");
        packageAndSend(TPDU, "0320", mBlockDataSend);
    }

    private void setOnlineUploadCreditVoid(String mBlock55,
                                           String mBlock22, String amountFee) {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Log.d(TAG, "setTCUpload: " + HOST_CARD);
        Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        Double amountFee1 = (Double.valueOf(AMOUNT) * fee) / 100;
        Double amountAll = Double.valueOf(AMOUNT) + amountFee1;
        Date date1 = new Date();
        String time = new SimpleDateFormat("HHmmss").format(date1);
        String dateTime = new SimpleDateFormat("MMdd").format(date1);
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "TMS");
        TERMINAL_ID = CardPrefix.getTerminalId(context, "TMS");
        String f22 = "";
        if (HOST_CARD.equalsIgnoreCase("EPS")) {
            f22 = "0052";
        } else if (HOST_CARD.equalsIgnoreCase("POS")) {
            f22 = "0051";
        } else {
            f22 = "0022";
        }
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo();
        mBlockDataSend[3 - 1] = "490000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceIdPlus(context, "TMS"));
        mBlockDataSend[12 - 1] = time;
        mBlockDataSend[13 - 1] = dateTime;
        mBlockDataSend[22 - 1] = f22;
        /*if (HOST_CARD.equalsIgnoreCase("POS")) {
            mBlockDataSend[24 - 1] = CardPrefix.getNii(card.getNo(), context);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        }*/
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        if (mBlock55 != null) {
            mBlockDataSend[25 - 1] = "05";
        } else {
            mBlockDataSend[25 - 1] = "00";
        }
        mBlockDataSend[37 - 1] = F37;
        mBlockDataSend[38 - 1] = F38;
        mBlockDataSend[39 - 1] = F39;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        /*if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[52 - 1] = pin;
        }*/
        String invoiceNumber;
        /*if (HOST_CARD.equalsIgnoreCase("POS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS);
        } else {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
        }*/
        invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        String msgLen = "00000390";
        String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String msgV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionC = "8065";
        String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
        String transactionNo = "00000000";
        String comCode = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1001), 10);
        String ref1 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1002), 50);
        String ref2 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1003), 50);
        String ref3 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1004), 50);
        String dateTime63 = new SimpleDateFormat("yyyyMMddHHmmss").format(date1);
        StringBuilder cardStringBuilder = new StringBuilder(transTemp.getCardNo());
        cardStringBuilder.replace(7, 12, "X");
        String cardNo = transTemp.getCardNo();
        String feeAmount = CardPrefix.calLen(decimalFormat.format(Float.valueOf(amountFee)).replace(".", ""), 10);
        String feeRate = CardPrefix.calLen(decimalFormat.format(Preference.getInstance(context).getValueFloat(Preference.KEY_FEE)).replace(".", ""), 4);
        String feeType = "F";
        String terId = CardPrefix.getTerminalId(context, HOST_CARD);
        String tex = Preference.getInstance(context).getValueString(Preference.KEY_TAX_INVOICE_NO);
        String posID = CardPrefix.calSpenLen("2222", 20);
        String merchantID = CardPrefix.getMerchantId(context, HOST_CARD);
        String texId = Preference.getInstance(context).getValueString(Preference.KEY_TAX_ID);
        String random = CardPrefix.calSpenLen("", 2);
        String terminalCERT = CardPrefix.calSpenLen("", 14);
        String checkSUM = CardPrefix.calSpenLen("", 8);


        String cardStar = transTemp.getCardNo().substring(0, 6);
        String cardEnd = transTemp.getCardNo().substring(12, 16);

        Log.d(TAG, "setOnlineUploadCredit: " + transTemp.getCardNo() + " \n cardStar : " + cardStar + " \n cardEnd : " + cardEnd);

        String f63 = msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 +
                ref2 + ref3 + dateTime63 + cardNo + feeAmount + feeRate + feeType + terId + tex + posID + merchantID + texId + random + terminalCERT + checkSUM;
        String f63Start = BlockCalculateUtil.getHexString(msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 +
                ref2 + ref3 + dateTime63 + cardStar) + "585858585858";
        String f63End1 = BlockCalculateUtil.getHexString(cardEnd + feeAmount + feeRate) + "50";
        String f63End2 = BlockCalculateUtil.getHexString(terId + tex + posID + merchantID + texId + random + terminalCERT + checkSUM);

        String f63All = f63Start + f63End1 + f63End2;
        mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(f63.length()), 4) + f63All;


        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, "TMS");
        packageAndSend(TPDU, "0320", mBlockDataSend);
    }

    private void setUploadCredit() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            Date date = new Date();
            String invoiceNumber;
            String time = new SimpleDateFormat("HHmmss").format(date);
            String dateTime = new SimpleDateFormat("yyyyMMdd").format(date);
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "TMS");
            TERMINAL_ID = CardPrefix.getTerminalId(context, "TMS");
            RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", HOST_CARD).equalTo("voidFlag", "N").findAll();
            float fee = Preference.getInstance(context).getValueFloat(Preference.KEY_FEE);
            for (int i = 0; i < transTemp.size(); i++) {
                Double amountFee = (Double.valueOf(transTemp.get(i).getAmount()) * fee) / 100;
                Double amountAll = Double.valueOf(transTemp.get(i).getAmount()) + amountFee;
                mBlockDataSend = new String[64];
                mBlockDataSend[2 - 1] = transTemp.get(i).getCardNo().length() + transTemp.get(i).getCardNo();
                mBlockDataSend[3 - 1] = "480000";
                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
                mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceIdPlus(context, "TMS"));
                mBlockDataSend[12 - 1] = time;
                mBlockDataSend[13 - 1] = dateTime.substring(4, 8);
                mBlockDataSend[22 - 1] = transTemp.get(i).getPointServiceEntryMode();
                mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
                if (transTemp.get(i).getIccData() != null) {
                    mBlockDataSend[25 - 1] = "05";
                } else {
                    mBlockDataSend[25 - 1] = "00";
                }
                /*if (transTemp.get(i) != null && transTemp.get(i).getTrack2() != null) {
                    mBlockDataSend[35 - 1] = Track2MappingTable(transTemp.get(i).getTrack2(),dateTime+time);
                }*/
                mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.get(i).getRefNo());
                mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(transTemp.get(i).getApprvCode());
                mBlockDataSend[39 - 1] = BlockCalculateUtil.getHexString(transTemp.get(i).getRespCode());
                mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
                mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
                mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                String msgLen = "00000268";
                String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
                String msgV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
                String transactionC = "8055";
                String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
                String transactionNo = "00000000";
                String comCode = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1001), 10);
                String ref1 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1002), 50);
                String ref2 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1003), 50);
                String ref3 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1004), 50);
                String dateTime63 = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
                String random = CardPrefix.calSpenLen("", 2);
                String terminalCERT = CardPrefix.calSpenLen("", 14);
                String checkSUM = CardPrefix.calSpenLen("", 8);

                String f63 = msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 + ref2 + ref3 + dateTime63 + random
                        + terminalCERT + checkSUM;

                mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(f63.length()), 4) + BlockCalculateUtil.getHexString(f63);

                onLineNow = true;
                TPDU = CardPrefix.getTPDU(context, "TMS");
                packageAndSend(TPDU, "0320", mBlockDataSend);
            }

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
        /*
        Date date = new Date();
        String time = new SimpleDateFormat("HHmmss").format(date);
        String dateTime = new SimpleDateFormat("MMdd").format(date);
        String f22 = "";

        if (HOST_CARD.equalsIgnoreCase("EPS")) {
            f22 = "0052";
        } else if (HOST_CARD.equalsIgnoreCase("POS")) {
            f22 = "0051";
        } else {
            f22 = "0022";
        }
        *//*if (HOST_CARD.equalsIgnoreCase("POS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS);
        } else {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
        }*//*
        invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "480000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);
        mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceIdPlus(context, "TMS"));
        mBlockDataSend[12 - 1] = time;
        mBlockDataSend[13 - 1] = dateTime;
        mBlockDataSend[22 - 1] = f22;
        *//*if (HOST_CARD.equalsIgnoreCase("POS")) {
            mBlockDataSend[24 - 1] = CardPrefix.getNii(card.getNo(), context);
        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
            mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        }*//*
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        if (mBlock55 != null) {
            mBlockDataSend[25 - 1] = "05";
        } else {
            mBlockDataSend[25 - 1] = "00";
        }
        mBlockDataSend[37 - 1] = mBlockDataReceived[37 - 1];
        mBlockDataSend[38 - 1] = mBlockDataReceived[38 - 1];
        mBlockDataSend[39 - 1] = mBlockDataReceived[39 - 1];
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));

        String msgLen = "00000268";
        String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String msgV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionC = "8055";
        String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
        String transactionNo = "00000000";
        String comCode = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1001), 10);
        String ref1 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1002), 50);
        String ref2 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1003), 50);
        String ref3 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1004), 50);
        String dateTime63 = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String random = CardPrefix.calSpenLen("", 2);
        String terminalCERT = CardPrefix.calSpenLen("", 14);
        String checkSUM = CardPrefix.calSpenLen("", 8);

        String f63 = msgLen + terminalV + msgV + transactionC + batchNo +transactionNo + comCode + ref1 + ref2 + ref3 + dateTime63 + random
                + terminalCERT + checkSUM;

        mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(f63.length()),4) + BlockCalculateUtil.getHexString(f63);

        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, "TMS");
        packageAndSend(TPDU, "0320", mBlockDataSend);*/
    }

    private void packageAndSend(String TPDU, String messageType, String[] mBlockData) {
        //processCallback(PROCESS_REQUEST_INSERT_DB);
        Log.d(TAG, "packageAndSend: " + mBlockData.toString());

        OnUsOffUsFlg = 0;   // Paul_20180522

        String applicationData = BlockCalculateUtil.calculateApplicationData(mBlockData);
        String dataToSend = "";
        dataToSend = dataToSend + TPDU;
        dataToSend = dataToSend + messageType;
        dataToSend = dataToSend + applicationData;
        dataToSend = dataToSend.trim();
        for (int i = 0; i < mBlockData.length; i++) {
            Log.d(TAG, "packageAndSend Yo" + (i + 1) + ": " + mBlockData[i]);
        }

        if (messageType.equals(MESSAGE_SALE)) {
            insertReversalSaleTransaction(HOST_CARD);        // Paul_20180523
        }

        if (mBlockData[24 - 1].equalsIgnoreCase("0245") || mBlockData[24 - 1].equalsIgnoreCase("0345")) {
            String traceNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)) + 1);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceNo);
            OnUsOffUsFlg = 0;   // Paul_20180522
        } else if (mBlockData[24 - 1].equalsIgnoreCase("0242") || mBlockData[24 - 1].equalsIgnoreCase("0362")) {
            String traceNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_EPS)) + 1);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_EPS, traceNo);
            OnUsOffUsFlg = 0;   // Paul_20180522
        } else if (mBlockData[24 - 1].equalsIgnoreCase("0246") || mBlockData[24 - 1].equalsIgnoreCase("0346")) {
            String traceNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_TMS)) + 1);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_TMS, traceNo);
            OnUsOffUsFlg = 1;  // Paul_20180522
        }


        Log.d(TAG, "Raw packageAndSend => " + dataToSend);
        String plainData = dataToSend;// Paul_20180522 Start
        if (OnUsOffUsFlg == 0) {
            dataToSend = encryptMsg(dataToSend);
        } else {
            dataToSend = OnUsEncryptionMsg(dataToSend);
        }
// Paul_20180522 End
        if (dataToSend != null) {
            Log.d(TAG, "Encrypted DATATOSEND => " + dataToSend);
            sendStr(dataToSend);
        } else {
            Log.d(TAG, "Encrypted Data is return NULL!!!");
            //sendStr(plainData);
        }
    }

    // Paul_20180522 Start
    private String OnUsEncryptionMsg(String InputISOMessage) {
        String OutputISOMessage = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ISOMESSAGE", InputISOMessage);
            TleParamMap tleParamMap = new TleParamMap();
            tleParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("OnUstleEncryption", tleParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return OutputISOMessage;
    }

    private String OnUsDecryptionMsg(String InputISOMessage) {
        String OutputISOMessage = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ISOMESSAGE", InputISOMessage);
            TleParamMap tleParamMap = new TleParamMap();
            tleParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("OnUstleDecryption", tleParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return OutputISOMessage;
    }
// Paul_20180522 End

    private String encryptMsg(String raw_data) {
        String encrypted_data = "";

        if (null != tleVersionOne) {
            try {
                Log.d(TAG, "encryptMsg:: before encrypting => " + raw_data);
                encrypted_data = tleVersionOne.tleEncryption(raw_data);
                Log.d(TAG, "encryptMsg:: after  encrypting => " + encrypted_data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return (encrypted_data);
    }

    private String decryptMsg(String encrypted_data) {
        String raw_data = "";

        if (null != tleVersionOne) {
            try {
                raw_data = tleVersionOne.tleDecrption(encrypted_data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return (raw_data);
    }

    private void sendStr(final String stringss) {
        if (stringss == null || stringss.isEmpty()) {
            //showMessage("The data to send is null or empty");
            Log.d(TAG, "The data to send is null or empty");
            return;
        }

        try {
            //Log.d(TAG, "DATA TO SEND => "+stringss);
            sFixedThreadPool = Executors.newFixedThreadPool(3);
            sFixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        PRIMARY_HOST = Preference.getInstance(context).getValueString(Preference.KEY_PRIMARY_IP);
                        PRIMARY_PORT = Preference.getInstance(context).getValueString(Preference.KEY_PRIMARY_PORT);
                        Log.d(TAG, "Host => " + PRIMARY_HOST + " [" + PRIMARY_PORT + "]");

                        Log.d(TAG, "Message Length = " + stringss.length());
                        Log.d(TAG, "Message % 2 = " + (stringss.length() % 2));
                        //Log.d(TAG, "TRACK2 length = "+TRACK2.length());

                        DataExchanger dataExchanger = new DataExchanger(1, PRIMARY_HOST, Integer.valueOf(PRIMARY_PORT));
                        Log.d(TAG, "pass to new DataExchanger");
                        byte[] clientData = ChangeFormat.writeUTFSpecial(stringss);
                        Log.d(TAG, "pass to ChangeFormat");
                        dataExchanger.doExchange(clientData, customSocketListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //showMessage(e.toString());
            Log.d(TAG, e.toString());
        }
    }

    private void tle_initialize(AidlDeviceManager deviceManager) {
        try {
            tleVersionOne = AidlTleService.Stub.asInterface(deviceManager.getDevice(999));
            Log.d(TAG, "TLE Service is " + ((tleVersionOne != null) ? "not null" : "null"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static String bcd2Str(byte[] b) {
        if (b == null) {
            return null;
        }
        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }

        return sb.toString();
    }

    private void dealWithTheResponse(String response) {
        //showMessage("Response Data："+response);
        String raw_data;

        Log.d(TAG, "Encrypted Response Data：" + response);

        response = response.substring(4);
        // Paul_20180522 Start
        if (OnUsOffUsFlg == 0) {
            raw_data = decryptMsg(response); // send to decrypt no need length
        } else {
            raw_data = OnUsDecryptionMsg(response); // send to decrypt no need length
        }
// Paul_20180522 End
//        raw_data = decryptMsg(response); // send to decrypt no need length
        Log.d(TAG, "Decrypted Response Data：" + raw_data);

        //raw_data = raw_data.substring(4); // already cut length
        String receivedTPDU = raw_data.substring(0, 5 * 2);
        String receivedMessageType = raw_data.substring(5 * 2, 5 * 2 + 2 * 2);
        mBlockDataReceived = BlockCalculateUtil.getReceivedDataBlock(raw_data);


        for (int i = 0; i < mBlockDataReceived.length; i++) {
            //System.out.println((i+1)+":"+mBlockDataReceived[i]);
            Log.d(TAG, (i + 1) + ":" + mBlockDataReceived[i]);
        }


        String result = BlockCalculateUtil.checkResult(mBlockDataReceived[39 - 1]);
        Log.d(TAG, "RETURN INFO OF 37:" + mBlockDataReceived[37 - 1]);
        Log.d(TAG, "RETURN INFO OF 38:" + mBlockDataReceived[38 - 1]);
        Log.d(TAG, "RETURN INFO OF 39:" + mBlockDataReceived[39 - 1]);
        Log.d(TAG, "RETURN INFO OF 55:" + mBlockDataReceived[55 - 1]);
        Log.d(TAG, "RETURN INFO OF 63:" + mBlockDataReceived[63 - 1]);


// Paul_20180522 Start
/*

        if (onLineNow && (!timeOut)) {
            try {
                Log.d(TAG, "SALE WITH PBOC Process");
                StringBuilder resultToLoad = new StringBuilder();
                String first = mBlockDataReceived[39 - 1].substring(0, 2);
                String second = mBlockDataReceived[39 - 1].substring(2);
                resultToLoad.append(first.substring(1));
                resultToLoad.append(second.substring(1));
                String resultToLoadStr = resultToLoad.toString().trim();
                //System.out.println("resultToLoadStr: "+resultToLoadStr);
                Log.d(TAG, "resultToLoadStr:" + resultToLoadStr);
                response_code = resultToLoadStr;

                pboc2.importOnlineResp(true, resultToLoadStr, mBlockDataReceived[55 - 1]);

                onLineNow = false;
            } catch (RemoteException e) {
                e.printStackTrace();
                //showMessage("夭寿啦，导入联机数据异常");
                Log.d(TAG, "import online data is abnormal");
            }
        }
*/
// Paul_20180522 End

        if (timeOut) // no response
        {
            Log.d(TAG, "RESULT: timeout = " + timeOut);
            timeOut = false;
        } else {

            // Paul_20180522 Start
            StringBuilder resultToLoad = new StringBuilder();
            String first = mBlockDataReceived[39 - 1].substring(0, 2);
            String second = mBlockDataReceived[39 - 1].substring(2);
            resultToLoad.append(first.substring(1));
            resultToLoad.append(second.substring(1));
            String resultToLoadStr = resultToLoad.toString().trim();
            //System.out.println("resultToLoadStr: "+resultToLoadStr);
            Log.d(TAG, "resultToLoadStr:" + resultToLoadStr);
            response_code = resultToLoadStr;

            if (onLineNow) {
                System.out.printf("utility:: dealWithTheResponse 0006 \n");
                try {
                    Log.d(TAG, "SALE WITH PBOC Process");
// Paul_20180522 Question
                    System.out.printf("utility:: dealWithTheResponse KKK001 \n");
                    //pboc2.importOnlineResp(true, resultToLoadStr, mBlockDataReceived[55 - 1]);
                    if (mBlockDataReceived[55 - 1].length() > 0) {

                        String online_data = mBlockDataReceived[55 - 1].substring(4, mBlockDataReceived[55 - 1].length());

                        pboc2.importOnlineResp(true, resultToLoadStr, online_data.trim());
                    } else {
                        pboc2.importOnlineResp(true, resultToLoadStr, "");
                    }
                    System.out.printf("utility:: dealWithTheResponse KKK002 \n");
// Paul_20180522 Question

                    onLineNow = false;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    //showMessage("夭寿啦，导入联机数据异常");
                    Log.d(TAG, "import online data is abnormal");
                }
            }
// Paul_20180522 End
            //showMessage("RESULT:"+result);
            Log.d(TAG, "RESULT:" + result);
            Log.d(TAG, "RESULT:" + response_code);
            Log.d(TAG, "dealWithTheResponse 3 - 1: " + mBlockDataSend[3 - 1] + " receivedMessageType : " + receivedMessageType);
            if (!receivedMessageType.equals("0410") && !receivedMessageType.equals("0330") && !receivedMessageType.equals("0510")) {
                if (response_code.equals("00")) {
                    Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[3 - 1]);
                    if (mBlockDataSend[3 - 1].equals(SALE_PROCESSING_CODE)) {
                        Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[37 - 1]);
                        F37 = mBlockDataReceived[37 - 1];
                        F38 = mBlockDataReceived[38 - 1];
                        F39 = mBlockDataReceived[39 - 1];
                        if (!MAG_TRX_RECV) {
                            insertTransaction("I");
                        } else {
                            insertTransaction("M");
                        }
                        if (HOST_CARD.equalsIgnoreCase("POS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_POS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_EPS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_TMS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS)) + 1));
                        }
                        deleteReversal();
                    } else if (mBlockDataSend[3 - 1].equals(VOID_PROCESSING_CODE)) { // Void
                        Log.d(TAG, "dealWithTheResponse Void : ");
                        Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[3 - 1]);
                        F37 = mBlockDataReceived[37 - 1];
                        F38 = mBlockDataReceived[38 - 1];
                        F39 = mBlockDataReceived[39 - 1];
                        if (HOST_CARD.equalsIgnoreCase("POS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_POS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_EPS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_TMS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS)) + 1));
                        }
                        if (!HOST_CARD.equals("TMS")) {
                            setOnlineUploadCreditVoid(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNT);
                        }
                        updateTransactionVoid();
                        deleteReversal();
                    } else if (mBlockDataSend[3 - 1].equals("920000") && receivedMessageType.equals("0810")) {
                        Log.d(TAG, "dealWithTheResponse: First Settlement");

                        if (mBlockDataReceived[63 - 1] != null) {
                            Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[63 - 1]);
                            String paraVersion = mBlockDataReceived[63 - 1].substring(28, 28 + 16);
                            String batch = mBlockDataReceived[63 - 1].substring(44, 44 + 16);
                            String transactionNo = mBlockDataReceived[63 - 1].substring(60, 60 + 16);
                            Log.d(TAG, "dealWithTheResponse: " + paraVersion + "\n batch : " + batch + " \n transactionNo : " + transactionNo);
                            Log.d(TAG, "dealWithTheResponse: BlockCalculateUtil.changeStringToHexString(paraVersion) : " + BlockCalculateUtil.hexToString(paraVersion));
                            Preference.getInstance(context).setValueString(Preference.KEY_PARAMETER_VERSION, BlockCalculateUtil.hexToString(paraVersion));
                            Preference.getInstance(context).setValueString(Preference.KEY_BATCH_NUMBER_TMS, BlockCalculateUtil.hexToString(batch).substring(2, BlockCalculateUtil.hexToString(batch).length()));
                            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_TMS, BlockCalculateUtil.hexToString(transactionNo));
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_TMS, BlockCalculateUtil.hexToString(transactionNo).substring(2, 8));
                        }
                        setDataParameterDownload();
                    } else if (mBlockDataSend[3 - 1].equals("900000") && receivedMessageType.equals("0810")) {
                        int tagNumber = 54;
                        Log.d(TAG, "dealWithTheResponse: ParameterDownload");
                        Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[63 - 1]);

                        getTag(tagNumber);
                    }
                    if (connectStatusSocket != null) {
                        connectStatusSocket.onReceived();
                    }
                } else {
                    deleteReversal();
                }
            } else if (mBlockDataSend[3 - 1].equals(TC_ADVICE_CODE)) { // TCUpload
                Log.d(TAG, "tcUploadPosition : " + tcUploadPosition + " tcUploadSize : " + tcUploadSize);
                if (!MAG_TRX_RECV) {
                    Log.d(TAG, "tcUploadPosition : " + tcUploadPosition + " tcUploadSize : " + tcUploadSize);
                    if (response_code.equals("00")) {
                        updateTransactionTCUpload();
                        if (tcUploadSize > tcUploadPosition) {
                            Log.d(TAG, "tcUploadPosition if : " + tcUploadPosition + " tcUploadSize : " + tcUploadSize);
                            setCheckTCUpload(HOST_CARD, typeCheck);
                        } else {
                            Log.d(TAG, "tcUploadPosition else : " + tcUploadPosition + " tcUploadSize : " + tcUploadSize);
                            tcUploadSize = 0;
                            tcUploadPosition = 0;
                            if (!typeCheck) {
                                if (!HOST_CARD.equalsIgnoreCase("TMS")) {
                                    Log.d(TAG, "OnlineUploadCredit: ");
                                    setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNTFEE);
//            setUploadCredit(mBlockDataSend[55 - 1]);
                                }
                            } else {
                                switch (HOST_CARD) {
                                    case "EPS":
                                        setDataSettlementAndSendEPS();
                                        break;
                                    case "POS":
                                        setDataSettlementAndSend("POS");
                                        break;
                                    default:
                                        setDataSettlementAndSendTMS();
                                        break;
                                }
                            }
                        }
                    } else {
                        if (!HOST_CARD.equalsIgnoreCase("TMS")) {
                            Log.d(TAG, "OnlineUploadCredit: ");
                            setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNTFEE);
//            setUploadCredit(mBlockDataSend[55 - 1]);
                        }
                    }
                }
            } else if (response_code.equals("95")) {
                Log.d(TAG, "dealWithTheResponse: " + response_code);
                setBatchUpload();
            } else if (response_code.equals("00") && receivedMessageType.equals("0330") && mBlockDataSend[3 - 1].equals("003000")) {
                Log.d(TAG, "Batch Upload 0 : ");
                if (batchUploadSize > batchUpload) {
                    Log.d(TAG, "Batch Upload 1 : ");
                    setBatchUpload();
//                    batchUpload++;
                } else {
                    Log.d(TAG, "Batch Upload 2 : ");
                    batchUpload = 0;
                    batchUploadSize = 0;
                    String traceIdNo = CardPrefix.geTraceIdPlus(context, HOST_CARD);
                    String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
                    mBlockDataSend = new String[64];
                    mBlockDataSend[3 - 1] = "960000";
                    mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
                    if (HOST_CARD.equalsIgnoreCase("POS")) {
                        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
                    } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
                    } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
                    }
                    mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
                    mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
                    mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
                    mBlockDataSend[61 - 1] = settlement61;
                    mBlockDataSend[63 - 1] = settlement63;
                    MTI = MESSAGE_SETTLEMENT;
                    onLineNow = true;
                    TPDU = CardPrefix.getTPDU(context, HOST_CARD);
                    packageAndSend(TPDU, "0500", mBlockDataSend);
                }
            } else if (response_code.equals("00") && mBlockDataReceived[3 - 1].equals("480000")) {

            } else if (response_code.equals("00") && mBlockDataReceived[3 - 1].equals("920000")) {
                Log.d(TAG, "onSettlementSuccess: ");
                if (settlementHelperLister != null) {
                    settlementHelperLister.onSettlementSuccess();
                }
//                deleteTransTemp();
                if (HOST_CARD.equalsIgnoreCase("POS")) {
                    Preference.getInstance(context).setValueString(Preference.KEY_BATCH_NUMBER_POS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_POS)) + 1));
                } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                    Preference.getInstance(context).setValueString(Preference.KEY_BATCH_NUMBER_EPS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) + 1));
                } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                    Preference.getInstance(context).setValueString(Preference.KEY_BATCH_NUMBER_TMS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_TMS)) + 1));
                }
            } else if (response_code.equals("00") && mBlockDataReceived[3 - 1].equals("960000")) { // SettlementClose
                Log.d(TAG, "onSettlementSuccess: ");
                if (settlementHelperLister != null) {
                    settlementHelperLister.onSettlementSuccess();
                }
//                deleteTransTemp();
                if (HOST_CARD.equalsIgnoreCase("POS")) {
                    Preference.getInstance(context).setValueString(Preference.KEY_BATCH_NUMBER_POS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_POS)) + 1));
                } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                    Preference.getInstance(context).setValueString(Preference.KEY_BATCH_NUMBER_EPS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) + 1));
                } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                    Preference.getInstance(context).setValueString(Preference.KEY_BATCH_NUMBER_TMS, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_TMS)) + 1));
                }
                Log.d(TAG, "response_code: " + receivedMessageType);
            } else if (response_code.trim().equals("00") && receivedMessageType.equals("0410")) {
                if (reversalListener != null) {
                    reversalListener.onReversalSuccess();
                }
                deleteReversal();
            }
            if (!response_code.equalsIgnoreCase("00") && !response_code.equalsIgnoreCase("95")) {
                if (OnUsOffUsFlg == 0) {
                    if (responseCodeListener != null) {
                        responseCodeListener.onResponseCode(RespCode.ResponseMsgPOS(response_code));
                    }
                } else {
                    if (responseCodeListener != null) {
                        responseCodeListener.onResponseCode(RespCode.ResponseMsgTMS(response_code));
                    }
                }
            } else {
                if (responseCodeListener != null) {
                    responseCodeListener.onResponseCodeSuccess();
                }
            }

            if (currentTransactionType != REVERSAL) {
                deleteReversal();       // Paul_20180530
                if ((MAG_TRX_RECV) || (mBlockDataSend[3 - 1].trim().equals(VOID_PROCESSING_CODE))) {
// Paul_20180522 Start
/*
                    StringBuilder resultToLoad = new StringBuilder();
                    String first = mBlockDataReceived[39 - 1].substring(0, 2);
                    String second = mBlockDataReceived[39 - 1].substring(2);
                    resultToLoad.append(first.substring(1));
                    resultToLoad.append(second.substring(1));
                    String resultToLoadStr = resultToLoad.toString().trim();
                    //System.out.println("resultToLoadStr: "+resultToLoadStr);
                    //Log.d(TAG, "resultToLoadStr:"+resultToLoadStr);
                    response_code = resultToLoadStr;
                    Log.d(TAG, "response code : = " + response_code);
*/
// Paul_20180522 End
                    APPRVCODE = mBlockDataReceived[38 - 1];
                    if (response_code.trim().equals("00")) {
                        processCallback(PROCESS_TRANS_RESULT_APPROVE);
                    } else {
                        processCallback(PROCESS_TRANS_RESULT_UNKNOW);
                    }
                    MAG_TRX_RECV = false;
                    MTI = "";
                } else if (mBlockDataSend[3 - 1].trim().equals(SETTLEMENT_PROCESSING_CODE)) {
                    Log.d(TAG, "SETTLEMENT PROCESS");
// Paul_20180522 Start
/*
                    StringBuilder resultToLoad = new StringBuilder();
                    String first = mBlockDataReceived[39 - 1].substring(0, 2);
                    String second = mBlockDataReceived[39 - 1].substring(2);
                    resultToLoad.append(first.substring(1));
                    resultToLoad.append(second.substring(1));
                    String resultToLoadStr = resultToLoad.toString().trim();
                    response_code = resultToLoadStr;
                    Log.d(TAG, "response code : = " + response_code);
*/
// Paul_20180522 End
                    RRN = mBlockDataReceived[37 - 1];
                    APPRVCODE = mBlockDataReceived[38 - 1];

                    if (response_code.trim().equals("00")) {
                        processCallback(PROCESS_TRANS_RESULT_APPROVE);
                    } else {
                        processCallback(PROCESS_TRANS_RESULT_UNKNOW);
                    }
                    MTI = "";
                }
            } else {
                Log.d(TAG, "REVERSAL PROCESS");
// Paul_20180522 Start
/*
                StringBuilder resultToLoad = new StringBuilder();
                String first = mBlockDataReceived[39 - 1].substring(0, 2);
                String second = mBlockDataReceived[39 - 1].substring(2);
                resultToLoad.append(first.substring(1));
                resultToLoad.append(second.substring(1));
                String resultToLoadStr = resultToLoad.toString().trim();
                response_code = resultToLoadStr;
                Log.d(TAG, "response code : = " + response_code);
*/
// Paul_20180522 End
                RRN = mBlockDataReceived[37 - 1];
                APPRVCODE = mBlockDataReceived[38 - 1];

                if (response_code.trim().equals("00")) {
                    deleteReversal();
                    processCallback(PROCESS_TRANS_RESULT_APPROVE);
                } else {
                    processCallback(PROCESS_TRANS_RESULT_UNKNOW);
                }
                MTI = "";
            }
        }
    }

    private void getTag(int tagNumber) {
        String tagAll = mBlockDataReceived[63 - 1];

        String tagId0 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen0 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData0 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen0)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen0)) * 2);
        tagNumber = tagNumber + 16;

        String tagId1 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen1 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData1 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen1)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen1)) * 2);
        tagNumber = tagNumber + 16;

        String tagId2 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen2 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData2 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen2)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen2)) * 2);
        tagNumber += 16;

        String tagId3 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen3 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData3 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen3)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen3)) * 2);
        tagNumber += 16;

        String tagId4 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen4 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData4 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen4)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen4)) * 2);
        tagNumber += 16;

        String tagId5 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen5 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData5 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen5)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen5)) * 2);
        tagNumber += 16;

        String tagId6 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen6 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData6 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen6)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen6)) * 2);
        tagNumber += 16;

        String tagId7 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen7 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData7 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen7)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen7)) * 2);
        tagNumber += 16;

        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1000, BlockCalculateUtil.hexToString(tagData0));
        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1001, BlockCalculateUtil.hexToString(tagData1));
        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1002, BlockCalculateUtil.hexToString(tagData2));
        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1003, BlockCalculateUtil.hexToString(tagData3));
        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1004, BlockCalculateUtil.hexToString(tagData4));
        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1005, BlockCalculateUtil.hexToString(tagData5));
        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1006, BlockCalculateUtil.hexToString(tagData6));
        Preference.getInstance(context).setValueString(Preference.KEY_TAG_1007, BlockCalculateUtil.hexToString(tagData7));

        Log.d(TAG, "dealWithTheResponse tagId: " +
                " \n Tag 0 : " + tagId0 +
                " \n tagLen 0 : " + tagLen0 +
                " \n tagData 0 : " + tagData0 +
                " \n =========================================== " +
                " \n Tag 1 : " + tagId1 +
                " \n tagLen 1 : " + tagLen1 +
                " \n tagData 1 : " + tagData1 +
                " \n =========================================== " +
                " \n Tag 2 : " + tagId2 +
                " \n tagLen 2 : " + tagLen2 +
                " \n tagData 2 : " + tagData2 +
                " \n =========================================== " +
                " \n Tag 3 : " + tagId3 +
                " \n tagLen 3 : " + tagLen3 +
                " \n tagData 3 : " + tagData3 +
                " \n =========================================== " +
                " \n Tag 4 : " + tagId4 +
                " \n tagLen 4 : " + tagLen4 +
                " \n tagData 4 : " + tagData4 +
                " \n =========================================== " +
                " \n Tag 5 : " + tagId5 +
                " \n tagLen 5 : " + tagLen5 +
                " \n tagData 5 : " + tagData5 +
                " \n =========================================== " +
                " \n Tag 6 : " + tagId6 +
                " \n tagLen 6 : " + tagLen6 +
                " \n tagData 6 : " + tagData6 +
                " \n =========================================== " +
                " \n Tag 7 : " + tagId7 +
                " \n tagLen 7 : " + tagLen7 +
                " \n tagData 7 : " + tagData7 +
                " \n =========================================== " +
                " \n tagNumber : " + tagNumber);
    }

    //endregion

    private String calNumTraceNo(String trace) {
//        String padded="000000".substring(trace.length()) + trace;
        String traceNo = "";
        for (int i = trace.length(); i < 6; i++) {
            traceNo += "0";
        }
        Log.d(TAG, "calNumTraceNo: " + traceNo + trace);
        return traceNo + trace;
    }

    private String getLength62(String slength62) {
        String length = "";
        Log.d(TAG, "getLength62: " + slength62.length());
        for (int i = slength62.length(); i < 4; i++) {
            length += "0";
        }
        Log.d(TAG, "getLength62: " + length + slength62);
        return length + slength62;
    }


    /***
     * Zone Database
     */

    private void deleteReversal() {
        Log.d(TAG, "deleteReversal: ");
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "execute: " + HOST_CARD);
                Log.d(TAG, "execute: DeleteReversal");
                RealmResults<ReversalTemp> reversalTemp = realm.where(ReversalTemp.class).equalTo("hostTypeCard", HOST_CARD).findAll();
                Log.d(TAG, "execute: " + reversalTemp.size());
                reversalTemp.deleteAllFromRealm();
            }
        });
        realm.close();
        realm = null;
    }

    private void insertTransaction(String typeCard) {
        double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        double amountFee = (Double.valueOf(AMOUNT) * fee) / 100;
        String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
        String EMCI_ID = "";
        String EMCI_Fee = "";
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            if (!mBlockDataReceived[63 - 1].isEmpty()) {
                EMCI_ID = mBlockDataReceived[63 - 1].substring(44, 44 + 18);
                EMCI_Fee = mBlockDataReceived[63 - 1].substring(62, 62 + 20);
                Log.d(TAG, "EMCI_ID: " + EMCI_ID + " \n EMCI FEE : " + EMCI_Fee);
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Number currentId = realm.where(TransTemp.class).max("id");
        int nextId;
        if (currentId == null) {
            nextId = 1;
        } else {
            nextId = currentId.intValue() + 1;
        }
        TransTemp transTemp = realm.createObject(TransTemp.class, nextId);
        transTemp.setAppid("000001");
        transTemp.setTid(CardPrefix.getTerminalId(context, HOST_CARD));
        transTemp.setMid(CardPrefix.getMerchantId(context, HOST_CARD));
        transTemp.setTraceNo(calNumTraceNo(traceIdNo));
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
        transTemp.setTransDate(fDate);
        String tTime = new SimpleDateFormat("HH:mm:ss").format(cDate);
        transTemp.setTransTime(tTime);
        String amountAll = String.valueOf(Double.valueOf(AMOUNT) + amountFee);
        Log.d(TAG, "insertTransaction amountAll : " + amountAll + " AMOUNT : " +Float.valueOf(AMOUNT) + " amountFee : " + amountFee);
        transTemp.setAmount(AMOUNT);
        transTemp.setCardNo(CARD_NO);
        transTemp.setCardType("0"); //cardType == REFUND ? "1" : "0"
        transTemp.setTrack1(TRACK1); //TODO ถ้าไม่มีต้องทำยังไง เซตค่าว่าง ?
        transTemp.setTrack2(TRACK2);
        transTemp.setProcCode(PROCESSING_CODE);
        transTemp.setPosem(POSEM);
        transTemp.setPosoc(POSOC);
        transTemp.setNii(NII);
        transTemp.setPointServiceEntryMode(mBlockDataSend[22 - 1]);
        transTemp.setApplicationPAN(mBlockDataSend[23 - 1]);
        transTemp.setExpiry(EXPIRY);
        transTemp.setRefNo(BlockCalculateUtil.hexToString(mBlockDataReceived[37 - 1]));
        transTemp.setIccData(mBlockDataSend[55 - 1]);
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            transTemp.setApprvCode(BlockCalculateUtil.hexToString(EMCI_ID));
            transTemp.setEmciId(BlockCalculateUtil.hexToString(EMCI_ID));
            transTemp.setEmciFree(BlockCalculateUtil.hexToString(EMCI_Fee));
            Log.d(TAG, "insertTransaction: " + BlockCalculateUtil.hexToString(EMCI_ID) + " setEmciFree = " + BlockCalculateUtil.hexToString(EMCI_Fee));
        } else {
            transTemp.setApprvCode(BlockCalculateUtil.hexToString(mBlockDataReceived[38 - 1]));
        }
        transTemp.setTransType(typeCard);
        transTemp.setRespCode(BlockCalculateUtil.hexToString(mBlockDataReceived[39 - 1]));
        transTemp.setVoidFlag("N");
        transTemp.setCloseFlag("N");
        transTemp.setTransStat("SALE");
        transTemp.setEcr(calNumTraceNo(invoiceNumber));
        transTemp.setHostTypeCard(HOST_CARD);
        transTemp.setComCode(COMCODE);
        transTemp.setRef1(REF1);
        transTemp.setRef2(REF2);
        transTemp.setRef3(REF3);
        transTemp.setPin(mBlockDataSend[52 - 1]);
        transTemp.setFee(decimalFormat.format(amountFee));
        AMOUNTFEE = amountFee;
        realm.commitTransaction();
        if (insertOrUpdateDatabase != null) {
            insertOrUpdateDatabase.onInsertSuccess(nextId);
        }
        realm.close();
        realm = null;
        if (!MAG_TRX_RECV) {
            insertTCUploadTransaction(traceIdNo, tTime, fDate, mBlockDataSend[55 - 1],
                    invoiceNumber.length() + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber)),
                    mBlockDataSend[23 - 1], mBlockDataSend[52 - 1], mBlockDataSend[22 - 1], amountFee);
//            setTCUpload(traceIdNo, tTime, fDate, mBlockDataSend[55 - 1],
//                    invoiceNumber.length() + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber)),
//                    mBlockDataSend[23 - 1], mBlockDataSend[52 - 1], mBlockDataSend[22 - 1]);
        }
//        if (!HOST_CARD.equalsIgnoreCase("TMS")) {
//            Log.d(TAG, "OnlineUploadCredit: ");
//            setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], amountFee);
////            setUploadCredit(mBlockDataSend[55 - 1]);
//        }
    }

    private void insertReversalSaleTransaction(String typeCard) {
        // Paul_20180523
        System.out.printf("utility:: insertReversalSaleTransaction 001 \n");
        if (mBlockDataSend[3 - 1] == null) {
            System.out.printf("utility:: insertReversalSaleTransaction mBlockDataSend[3 - 1] null \n");
            return;
        }
        if ((!mBlockDataSend[3 - 1].equals(SALE_PROCESSING_CODE)) && (!mBlockDataSend[3 - 1].equals(VOID_PROCESSING_CODE))) {
            System.out.printf("utility:: insertReversalSaleTransaction None SALE_PROCESSING_CODE VOID_PROCESSING_CODE \n");
            return;
        }

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
        String traceId = CardPrefix.geTraceId(context, HOST_CARD);
        realm.beginTransaction();
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Number currentId = realm.where(ReversalTemp.class).max("id");
        int nextId;
        if (currentId == null) {
            nextId = 1;
        } else {
            nextId = currentId.intValue() + 1;
        }
        ReversalTemp reversalTemp = realm.createObject(ReversalTemp.class, nextId);
        reversalTemp.setAppid("000001");
        reversalTemp.setTid(CardPrefix.getTerminalId(context, HOST_CARD));
        reversalTemp.setMid(CardPrefix.getMerchantId(context, HOST_CARD));
        reversalTemp.setTraceNo(calNumTraceNo(traceId));
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
        reversalTemp.setTransDate(fDate);
        String tTime = new SimpleDateFormat("HH:mm:ss").format(cDate);
        reversalTemp.setTransTime(tTime);
        reversalTemp.setAmount(decimalFormat.format(Float.valueOf(AMOUNT)));
        reversalTemp.setCardNo(CARD_NO);
        reversalTemp.setCardType("0"); //cardType == REFUND ? "1" : "0"
        reversalTemp.setTrack1(TRACK1); //TODO ถ้าไม่มีต้องทำยังไง เซตค่าว่าง ?
        // Paul_20180523 Start
        if (HOST_CARD.equals("TMS")) {

            reversalTemp.setTrack2(TRACK2_ENC);
//            reversalTemp.setTrack2( mBlockDataSend[35 - 1] );
            reversalTemp.setField63(mBlockDataSend[63 - 1]);
            reversalTemp.setPinblock(mBlockDataSend[52 - 1]);
        } else {
            reversalTemp.setTrack2(TRACK2);
        }
        // Paul_20180523 End
        reversalTemp.setTrack2(TRACK2);
        reversalTemp.setProcCode(PROCESSING_CODE);
        reversalTemp.setPosem(POSEM);
        reversalTemp.setPosoc(POSOC);
        reversalTemp.setNii(NII);
        reversalTemp.setPointService(mBlockDataSend[22 - 1]);
        reversalTemp.setApplicationPAN(mBlockDataSend[23 - 1]);
        reversalTemp.setExpiry(EXPIRY);
        reversalTemp.setRefNo(mBlockDataReceived[37 - 1] == null ? "" : mBlockDataReceived[37 - 1]);
        reversalTemp.setIccData(mBlockDataSend[55 - 1]);
        reversalTemp.setApprvCode(mBlockDataReceived[38 - 1] == null ? "" : mBlockDataReceived[38 - 1]);
        reversalTemp.setTransType(typeCard);
        reversalTemp.setRespCode(mBlockDataReceived[39 - 1] == null ? "" : mBlockDataReceived[39 - 1]);
        reversalTemp.setVoidFlag("N");
        reversalTemp.setCloseFlag("N");
        if (mBlockDataSend[3 - 1].equals(SALE_PROCESSING_CODE)) {
            reversalTemp.setTransStat("SALE");
        } else if (mBlockDataSend[3 - 1].equals(VOID_PROCESSING_CODE)) {
            reversalTemp.setTransStat("VOID");
        }
        reversalTemp.setEcr(calNumTraceNo(invoiceNumber));
        reversalTemp.setHostTypeCard(HOST_CARD);
        reversalTemp.setReserved(mBlockDataSend[63 - 1]);
        Log.d(TAG, "insertReversalSaleTransaction: " + calNumTraceNo(invoiceNumber).length() + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber)));
        realm.commitTransaction();
        realm.close();
        realm = null;
    }

    private String Track2MappingTable(String t2, String TRANDATE) {
        String PinBlock = null;
        Log.d(TAG, "Track2MappingTable: " + t2);
        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("TRACK2", t2);
            hashMap.put("TRANDATE", TRANDATE);
            //Toast.makeText(InterfaceTestActvity.this,hashMap.size()+"弱뷴?",Toast.LENGTH_SHORT).show();
            TleParamMap tleParamMap = new TleParamMap();
            tleParamMap.setParamMap(hashMap);
            PinBlock = tleVersionOne.tleFuncton("EncryptMappingTable", tleParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return PinBlock;
    }

    /*****************
     public static String TrackEncryptMapping(String Track2String,String TranDate)
     {
     String ReturnString=null;
     int Seed;
     byte[] H = new byte[2+1];
     byte[] TranDateByte = new byte[14+1];
     byte[] MapBuf = new byte[37+100];
     int i;
     int Digt;
     byte[] Track2Byte = new byte[37+100];
     byte[][] MappingTable = {
     {'4','3','1','6','8','9','2','0','7','5','D'},		//"3168920754D",
     {'8','5','2','3','1','7','4','9','6','0','D'},		// "5231749608D",
     {'5','4','6','2','7','3','0','8','1','9','D'},		// "4627308195D",
     {'7','9','0','1','6','8','3','4','5','2','D'},		// "9016834527D",
     {'5','0','3','6','2','4','8','7','9','1','D'},		// "0362487915D",
     {'8','2','6','4','0','1','9','3','7','5','D'},		// "2640193758D",
     {'3','9','1','4','6','5','2','0','8','7','D'},		// "9146520873D",
     {'7','1','4','9','3','0','8','6','5','2','D'},		// "1493086527D",
     {'9','4','8','2','5','7','3','1','6','0','D'},		// "4825731609D",
     {'1','3','9','0','7','6','2','4','8','5','D'},		// "3907624851D",
     {'2','5','3','8','7','1','0','6','9','4','D'},		// "5387106942D",
     {'0','8','9','4','1','7','5','2','3','6','D'},		// "8941752360D",
     {'6','9','0','1','4','2','7','8','3','5','D'},		// "9014278356D",
     {'4','6','7','2','9','8','3','5','0','1','D'},		// "6729835014D",
     {'8','0','1','9','3','6','7','4','2','5','D'},		// "0193674258D",
     {'8','7','5','6','0','2','9','1','4','3','D'},		// "7560291438D",
     {'6','8','2','4','5','3','1','0','7','9','D'},		// "8245310796D",
     {'4','3','5','8','2','9','7','6','1','0','D'},		// "3582976104D",
     {'7','9','4','0','5','1','8','3','6','2','D'}		// "9405183627D"
     };
     TranDateByte = TranDate.getBytes();
     Track2Byte = Track2String.getBytes();

     H[1] = TranDateByte[9];
     H[0] = TranDateByte[8];

     System.out.printf("utility:: TrackEncryptMapping Track2String = %s \n",Track2String);
     System.out.printf("utility:: TrackEncryptMapping TranDate = %s \n",TranDate);
     Seed = 0;

     H[0] = TranDateByte[10];
     H[1] = TranDateByte[11];
     H[2] = 0x00;
     Seed += (int)(((H[0]-0x30)*10 + H[1]) * 60); // (AppUtilInterface.LibAsciiToInt(H) * 60);
     H[0] = TranDateByte[12];
     H[1] = TranDateByte[13];
     H[2] = 0x00;
     Seed += (((H[0]-0x30)*10 + H[1]) );

     System.out.printf("utility:: TrackEncryptMapping Seed = %d \n",Seed);
     Seed = Seed % 19;
     System.out.printf("utility:: TrackEncryptMapping Seed = %d \n",Seed);

     for(i=0;(i<Track2String.length())&&(i<37);i++)
     {
     if((Track2Byte[i] == 'D') || (Track2Byte[i] == 'd') || (Track2Byte[i] == '='))
     {
     Digt = 10;
     MapBuf[i] = Track2Byte[i];
     }
     else
     {
     Digt = Track2Byte[i]-0x30;
     MapBuf[i] = MappingTable[Seed][Digt];
     }
     }
     ReturnString = new String( MapBuf, 0, Track2String.length(), StandardCharsets.UTF_8 );
     System.out.printf("utility:: TrackEncryptMapping ReturnString = %s \n",ReturnString);

     return ReturnString;
     }
     *****************/
    public void insertReversalVoidTransaction(TransTemp transTemp) {
        HOST_CARD = CardPrefix.getTypeCard(transTemp.getCardNo());
        String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Number currentId = realm.where(ReversalTemp.class).max("id");
        int nextId;
        if (currentId == null) {
            nextId = 1;
        } else {
            nextId = currentId.intValue() + 1;
        }
        ReversalTemp reversalTemp = realm.createObject(ReversalTemp.class, nextId);
        reversalTemp.setAppid("000001");
        reversalTemp.setTid(CardPrefix.getTerminalId(context, HOST_CARD));
        reversalTemp.setMid(CardPrefix.getMerchantId(context, HOST_CARD));
        reversalTemp.setTraceNo(calNumTraceNo(String.valueOf(traceIdNo)));
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
        reversalTemp.setTransDate(fDate);
        String tTime = new SimpleDateFormat("HH:mm:ss").format(cDate);
        reversalTemp.setTransTime(tTime);
        reversalTemp.setAmount(decimalFormat.format(Float.valueOf(transTemp.getAmount())));
        reversalTemp.setCardNo(transTemp.getCardNo());
        reversalTemp.setCardType("0"); //cardType == REFUND ? "1" : "0"
        reversalTemp.setTrack1(transTemp.getTrack1()); //TODO ถ้าไม่มีต้องทำยังไง เซตค่าว่าง ?
        reversalTemp.setTrack2(transTemp.getTrack2());
        reversalTemp.setProcCode(transTemp.getProcCode());
        reversalTemp.setPosem(transTemp.getPosem());
        reversalTemp.setPosoc(transTemp.getPosoc());
        reversalTemp.setNii(transTemp.getNii());
        reversalTemp.setExpiry(transTemp.getExpiry());
        reversalTemp.setRefNo(transTemp.getRefNo() == null ? "" : transTemp.getRefNo());
        reversalTemp.setIccData(transTemp.getIccData());
        reversalTemp.setApprvCode(transTemp.getApprvCode() == null ? "" : transTemp.getApprvCode());
        reversalTemp.setTransType("I");
        reversalTemp.setRespCode(transTemp.getRespCode() == null ? "" : transTemp.getRespCode());
        reversalTemp.setVoidFlag("N");
        reversalTemp.setCloseFlag("N");
        reversalTemp.setTransStat("VOID");
        reversalTemp.setEcr(transTemp.getEcr());
        reversalTemp.setHostTypeCard(HOST_CARD);
        realm.commitTransaction();
        RealmResults<ReversalTemp> reversalTemps = realm.where(ReversalTemp.class).findAll();
        reversalTemps.size();
        Log.d(TAG, "insertTransaction: " + reversalTemps.size() + " base : " + reversalTemps.toString());
        realm.close();
        realm = null;
    }

    private void insertTCUploadTransaction(String trackNo,
                                           String time,
                                           String date,
                                           String mBlock55,
                                           String ecr,
                                           String mBlock23,
                                           String pin, String f22, double amountFee) {
        String traceIdNo = CardPrefix.geTraceIdPlus(context, HOST_CARD);
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);

        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            realm.beginTransaction();
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            Number currentId = realm.where(TCUpload.class).max("id");
            int nextId;
            if (currentId == null) {
                nextId = 1;
            } else {
                nextId = currentId.intValue() + 1;
            }
            TCUpload tcUpload = realm.createObject(TCUpload.class, nextId);
            tcUpload.setAppid("000001");
            tcUpload.setTid(CardPrefix.getTerminalId(context, HOST_CARD));
            tcUpload.setMid(CardPrefix.getMerchantId(context, HOST_CARD));
            tcUpload.setTraceNo(calNumTraceNo(String.valueOf(traceIdNo)));
            tcUpload.setTransDate(date);
            tcUpload.setTransTime(time);
            tcUpload.setAmount(decimalFormat.format(Double.valueOf(AMOUNT)));
            tcUpload.setCardNo(CARD_NO);
            tcUpload.setCardType("0"); //cardType == REFUND ? "1" : "0"
            tcUpload.setTrack1(TRACK1); //TODO ถ้าไม่มีต้องทำยังไง เซตค่าว่าง ?
            tcUpload.setTrack2(TRACK2);
            tcUpload.setProcCode(PROCESSING_CODE);
            tcUpload.setPosem(POSEM);
            tcUpload.setPosoc(POSOC);
            tcUpload.setNii(NII);
            tcUpload.setExpiry(EXPIRY);
            tcUpload.setRefNo(mBlockDataReceived[37 - 1]);
            tcUpload.setIccData(mBlockDataSend[55 - 1]);
            tcUpload.setApprvCode(mBlockDataReceived[38 - 1]);
            tcUpload.setTransType("I");
            tcUpload.setRespCode(mBlockDataReceived[39 - 1]);
            tcUpload.setEcr(calNumTraceNo(invoiceNumber));
            tcUpload.setStatusTC("0");
            tcUpload.setHostTypeCard(HOST_CARD);
            tcUpload.setPointServiceEntryMode(f22);
            tcUpload.setApplicationPAN(mBlock23);
            tcUpload.setFee(decimalFormat.format(amountFee));
            realm.commitTransaction();
            RealmResults<TCUpload> tcUploads = realm.where(TCUpload.class).equalTo("hostTypeCard", HOST_CARD).equalTo("statusTC", "0").findAll();
            tcUploads.size();
            tcUploadDb = tcUploads.get(tcUploads.size() - 1);
            Log.d(TAG, "insertTransaction tcUploadDb: " + tcUploads.size() + " base : " + tcUploadDb.toString());
        } finally {
            if (realm != null) {
                realm.isClosed();
                realm = null;
            }
        }

        setCheckTCUpload(HOST_CARD, false);
    }

    private void updateTransactionVoid() {
        Log.d(TAG, "updateTransactionVoid: " + transTemp.getTraceNo());

        final int transTempID = transTemp.getId();
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

        RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).findAll();
        transTemps.size();
        Log.d(TAG, "insertTransaction: " + transTemps.size() + " base : " + transTemps.toString());
        TransTemp trans = realm.where(TransTemp.class).equalTo("traceNo", transTemp.getTraceNo()).findFirst();
        realm.beginTransaction();
        if (trans != null) {
            trans.setVoidFlag("Y");
        }
        realm.commitTransaction();
        if (insertOrUpdateDatabase != null) {
            insertOrUpdateDatabase.onUpdateVoidSuccess(trans.getId());
        }
        realm.close();
        realm = null;
    }

    private void updateTransactionTCUpload() {
        Log.d(TAG, "updateTransactionTCUpload ==============================: ");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    TCUpload upload = realm.where(TCUpload.class).equalTo("hostTypeCard", HOST_CARD).equalTo("traceNo", tcUploadId).findFirst();
                    realm.beginTransaction();
                    Log.d(TAG, "updateTransactionTCUpload: " + tcUploadId);
                    if (upload != null) {
                        upload.setStatusTC("1");
                    }
                    realm.commitTransaction();
                    RealmResults<TCUpload> uploadT = realm.where(TCUpload.class).equalTo("hostTypeCard", HOST_CARD).findAll();
                    Log.d(TAG, "updateTransactionTCUpload size : " + uploadT.size());
                    if (insertOrUpdateDatabase != null) {
                        insertOrUpdateDatabase.onUpdateVoidSuccess(upload.getId());
                    }
                    /*tcUploadPosition++;

                    setCheckTCUpload(HOST_CARD, typeCheck);*/

                } finally {
                    realm.close();
                }
            }
        });

        thread.start();

    }

    public void deleteTransTemp() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "execute: DeleteTransTemp");
                RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard", HOST_CARD).findAll();
                transTemps.deleteAllFromRealm();
            }
        });
        realm.close();
        realm = null;
    }

    //region - Listener

    private CardHelperListener cardHelperListener = null;
    private InsertOrUpdateDatabase insertOrUpdateDatabase = null;
    private ReversalListener reversalListener = null;
    private ConnectStatusSocket connectStatusSocket = null;
    private SettlementHelperLister settlementHelperLister = null;
    private ResponseCodeListener responseCodeListener = null;

    public void setCardHelperListener(CardHelperListener cardHelperListener) {
        this.cardHelperListener = cardHelperListener;
    }

    public void setInsertOrUpdateDatabase(InsertOrUpdateDatabase insertOrUpdateDatabase) {
        this.insertOrUpdateDatabase = insertOrUpdateDatabase;
    }

    public void setReversalListener(ReversalListener reversalListener) {
        this.reversalListener = reversalListener;
    }

    public void setConnectStatusSocket(ConnectStatusSocket connectStatusSocket) {
        this.connectStatusSocket = connectStatusSocket;
    }

    public void setSettlementHelperLister(SettlementHelperLister settlementHelperLister) {
        this.settlementHelperLister = settlementHelperLister;
    }

    public void setResponseCodeListener(ResponseCodeListener responseCodeListener) {
        this.responseCodeListener = responseCodeListener;
    }

    public interface SettlementHelperLister {
        public void onSettlementSuccess();

        public void onConnectTimeOut();

        public void onTransactionTimeOut();
    }

    public interface CardHelperListener {
        public void onCardInfoReceived(Card card);

        public void onCardInfoFail();

        public void onTransResultFallBack();

        public void onCardTransactionUpdate(boolean isApproved, Card card);

        public void onFindMagCard(Card card);

        public void onSwapCardIc();

        public void onSwapCardMag();
    }

    public interface InsertOrUpdateDatabase {
        public void onUpdateVoidSuccess(int id);

        public void onInsertSuccess(int id);

        public void onConnectTimeOut();

        public void onTransactionTimeOut();
    }

    public interface ReversalListener {
        public void onReversalSuccess();
    }

    public interface ConnectStatusSocket {
        public void onConnectTimeOut();

        public void onTransactionTimeOut();

        public void onError();

        public void onOther();

        public void onReceived();
    }

    public interface ResponseCodeListener {
        public void onResponseCode(String response);

        public void onResponseCodeSuccess();

        public void onConnectTimeOut();

        public void onTransactionTimeOut();
    }

//endregion
}
