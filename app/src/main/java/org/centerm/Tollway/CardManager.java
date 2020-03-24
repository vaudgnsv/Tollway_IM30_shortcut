
package org.centerm.Tollway;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;


//import com.centerm.centermposoversealib.tleservice.tleinterface;
//import com.centerm.centermposoversealib.tleservice.TleLibParamMap;
import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.chock.gtmshelper.AidlGtmsService;
//import com.centerm.smartpos.aidl.sys.AidlManager;
import com.centerm.smartpos.util.CompactUtil;
import com.centerm.smartpos.util.HexUtil;


import org.centerm.Tollway.activity.CalculatePriceActivity;
import org.centerm.Tollway.activity.ConsumeActivity;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.core.ChangeFormat;
import org.centerm.Tollway.core.CustomSocketListener;
import org.centerm.Tollway.core.DataExchanger;
import org.centerm.Tollway.database.BL;
import org.centerm.Tollway.database.ReversalTemp;
import org.centerm.Tollway.database.TCUpload;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.helper.myCardReaderHelper;
import org.centerm.Tollway.jemv.clssDPAS.trans.ClssDPAS;
import org.centerm.Tollway.jemv.clssentrypoint.model.TransResult;
import org.centerm.Tollway.jemv.clssentrypoint.trans.ClssEntryPoint;
import org.centerm.Tollway.jemv.clssexpresspay.trans.ClssExpressPay;
import org.centerm.Tollway.jemv.clssjspeedy.ClssJSpeedy;
import org.centerm.Tollway.jemv.clssjspeedy.model.Clss_JcbAidParam;
import org.centerm.Tollway.jemv.clsspaypass.trans.ClssPayPass;
import org.centerm.Tollway.jemv.clsspaywave.trans.ClssPayWave;
import org.centerm.Tollway.jemv.clsspure.trans.CassPure;
import org.centerm.Tollway.jemv.clsspure.trans.model.Clss_PureAidParam;
import org.centerm.Tollway.jemv.clssquickpass.trans.ClssQuickPass;
import org.centerm.Tollway.model.Card;
import org.centerm.Tollway.service.serviceReadType;
import org.centerm.Tollway.utility.PromptMsg;
import org.centerm.Tollway.utility.FileParse;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import com.centerm.smartpos.util.Util;
import com.pax.dal.IPed;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EReaderType;
import com.pax.jemv.amex.api.ClssAmexApi;
import com.pax.jemv.amex.model.CLSS_AEAIDPARAM;
import com.pax.jemv.amex.model.TransactionMode;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.ClssTmAidList;
import com.pax.jemv.clcommon.Clss_MCAidParam;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.Clss_VisaAidParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.EMV_APPLIST;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.OnlineResult;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.dpas.api.ClssDPASApi;
import com.pax.jemv.entrypoint.api.ClssEntryApi;
import com.pax.jemv.jcb.api.ClssJCBApi;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paywave.api.ClssWaveApi;
import com.pax.jemv.qpboc.api.ClssPbocApi;
import com.pax.jemv.qpboc.model.Clss_PbocAidParam;

import com.tleinterfacelibrary.AidlManager;
import com.tleinterfacelibrary.TleLibParamMap;
import com.tleinterfacelibrary.tleinterface;


import io.realm.Realm;
import io.realm.RealmResults;

import static org.centerm.Tollway.core.ChangeFormat.bcd2Str;
import static org.centerm.Tollway.core.ChangeFormat.bcd2str;
import static org.centerm.Tollway.core.ChangeFormat.str2Bcd;

public class CardManager {

    public static final String TAG = "CardManager";



    public static CardManager instance = null;
    private Context context = null;
    private Realm realm = null;
    private String HOST_CARD = "";
    private String BIN_TYPE = "";
    private int HOST_FLAG = 0 ;
    private String AMOUNT = "";
    private String TERM = "";
    private String SERIAL_NO = "";
    private String PRODUCT_ID = "";
    private String E_VOUCHER = "";
    private String ITEM = "";
    private String PIN = "";
    private String COMCODE = "";
    private String TRANCE_NO = "";
    private String Ecr_NO = "";
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

    private int uploadCreditPosition = 0;
    private int uploadCreditSize = 0;

    private boolean typeCheck = false;

    private int saleId = 0;


    private int insettimewait = 0;



    private AidlManager manager = null;
    private AidlManager managerTle = null;
    private AidlGtmsService gtmsService;
    //private AidlPrinter printDev;
    //private AidlQuickScanZbar aidlQuickScanService = null;

    /**
     * DATA_TO_SEND_TPDU
     */
    //private final static String TPDU =  "6000140000";
    private String TPDU = "6000140000";
    private String PRIMARY_HOST = "172.20.10.3";
    private String PRIMARY_PORT = "3838";
    private String SECONDARY_HOST = "";
    private String SECONDARY_PORT = "";


    public static int OnUsOffUsFlg = 0;
    private String tagPanSnEMV;
    private String AID;
    private String NAMECARDHOLDER="";
    private String NAMECARDHOLDER_FULL="";
    private boolean SIGNATURE = true;
    private String SERVICECODE="";
    private String CARDLABEL="";
    private String PREFEREDNAME="";
    private String TC;
    private String TAX_ABB_NEW;
    private String invoiceGB = "";
    private String stTraceId = "";
    private String dateTimeOnline;

    private String TVR = "";
    private String TSI = "";






    // PAX A920
    private ImplEmv emv;
    private static EReaderType readerType = null; // 读卡类型
    private static final int READ_CARD_CANCEL = 2; // 取消读卡
    private static final int READ_CARD_ERR = 3; // 读卡失败
    public static EReaderType readerMode;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();
    private byte mode; // 寻卡模式
    public static serviceReadType serReadType = serviceReadType.getInstance();
    private static boolean startFlg = false;
    private static boolean statusFlg = false;
    private int magRet;
    ClssTmAidList[] tmAidList;
    Clss_PreProcInfo[] preProcInfo;
    Clss_TransParam transParam;
    boolean rfflag=false;
    private int completeRet;
    private int readedType;
    private String PINBLOCK = "";

    private String TERMINAL_ID = "11000111";
    private String MERCHANT_NUMBER = "030000000011122";

    ////20181218  SINN Void syn date/time
    private String szVoidtime = null;
    private String szVoiddate = null;



    private String expectPinLen = "0,4,5,6,7,8,9,10,11,12";

    private IPed ped = MainApplication.getDal().getPed(EPedType.INTERNAL);





    public static CardManager init(Context context) {
        if (instance == null) {
            instance = new CardManager();
            instance.context = context;
        }
        //initClssTrans = new initClssTrans();
        startFlg = true;

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
            //manager = AidlManager.Stub.asInterface(service);
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
                System.out.println("before load aid");
                //loadAid();
                System.out.println("after  load aid");

           /*     try {
//                    pboc2 = AidlEMVL2.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));
//                    printDev = AidlPrinter.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));
//                    aidlQuickScanService = AidlQuickScanZbar.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_QUICKSCAN));
                   // settingService = AidlSystemSettingService.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_SYS));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/

                //loadAid();


//                iccard_service_start(1);
//                setTransaction(SALE);
            }
        }
    };

    private ServiceConnection connTle = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            managerTle = AidlManager.Stub.asInterface(service);
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

    private ServiceConnection connGtms = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("gtms","AidlGtmsService onServiceConnected");
            gtmsService = AidlGtmsService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("gtms","AidlGtmsService onServiceDisconnected");
        }
    };


    public void bindService() {
//        Intent intent = new Intent();
//        intent.setPackage("com.centerm.smartposservice");
//        intent.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
//        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);

        Log.d("bind test","바인드 시작");
        Intent intentTle = new Intent();
        intentTle.setPackage("com.thaivan.tle");
        intentTle.setAction("com.thaivan.TleFunction.MANAGER_SERVICE");
        context.bindService(intentTle, connTle, Context.BIND_AUTO_CREATE);
        Log.d("bind test","tle 바인드 끝");
    }

    public void unbindService() {
        context.unbindService(conn);
        context.unbindService(connTle);
    }
    //endregion

    public initClssTrans initClssTrans = new initClssTrans();

    //region - Aid

    private String _id_card;
    private String _thai_name;
    private String _eng_first_name;
    private String _eng_last_name;
    private String _birth_eng;
    private String _birth_th;
    //private String _gender_eng;
    //private String _gender_th;
//    private static AidlEMVL2 pboc2;
//    private static AidlSystemSettingService settingService;
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

    public final static byte READ = 0x00;
    public final static byte SALE = 0x01;
    public final static byte FALLBACK = 0x02;
    public final static byte PREAUTH = 0x20;
    public final static byte VOID_PREAUTH = 0x30;
    public final static byte VOID = 0x12;
    public final static byte SETTLEMENT = 0x13;
    public final static byte UPLOAD = 0x14;
    public final static byte REVERSAL = 0x16;
    public final static byte SETTLEMENT_AFTER_UPLOAD = 0x17;
    public final static byte TC_ADVICE = 0x18;
    public final static byte CHECK_POINT = 0x19;



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

    public byte CARDTYPE = 0x00;
    public final static byte ICCARD = 0x00;
    public final static byte RFCARD = 0x01;
    public final static byte MAGCARD = 0x02;
    public boolean FALLBACK_HAPPEN = false;
    public boolean PRE_AUTH_HAPPEN = false;
    //check card มาจากการรูด
    public boolean MAG_TRX_RECV = false;
    private int isRF = 0;

    /**
     * DATA_TO_SEND_SOME_OF_APPLICATION(CAN NOT BE CHANGE)
     */
    private final static String NETWORK_ID = "0010";
    private final static String POS_COND_CODE = "00";
    private final static String SALE_PROCESSING_CODE = "003000";
    private final static String VOID_PROCESSING_CODE = "023000";
    private final static String VOIDHEALTHCARE_PROCESSING_CODE = "025000"; //Paul_180708
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
    private String POS_ENT_MODE = "0052";
    private String MAG_POS_ENT_MODE = "0021";
    private String CL_POS_ENT_MODE = "0072";
    private boolean PIN_PYPASS = false;

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
    private String TRACK3;;
    private String CARD_NO;
    private String MBLOCK55;
    private String TRACK2_ENC;      // Paul_20180523

    //public ParcelableTrackData MagneticCardData;

    public boolean AUTO_TRANSACTION = false;

    /**
     * FUNCTION_OF_PBOC
     */

    public final static byte SEARCH_CARD = 0x00;
    public final static byte COMSUME = 0x01;
    public final static byte CASH = 0x02;
    public final static byte REFUND = 0x03;
    public final static byte BALANCE_QUERY = 0x04;

    //public CardInfoData ICCardData;

    private String tempSavedAllData = "";
    private String tempSavedTrackData = "";
    private boolean onLineNow = false;

    /**
     * DATA_TO_SEND_MESSAGE_TYPE
     */
    public String MTI;
    private final static String MESSAGE_PREAUTH = "0100";
    private final static String MESSAGE_SALE = "0200";
    private final static String MESSAGE_VOID = "0200";
    private final static String MESSAGE_SETTLEMENT = "0500";
    private final static String MESSAGE_UPLOAD = "0320";
    private final static String MESSAGE_REVERSAL = "0400";
    private final static String MESSAGE_VOID_PREAUTH = "0420";
    private final static String MESSAGE_REFUND = "0200";
    private final static String MESSAGE_TC_ADVICE = "0320";


    //----------
    private TransTemp transTemp; // Database
    private TCUpload tcUploadDb = null;
    private String tcUploadId = null;
    private Card card = null;
    private HashMap<String, byte[]> tagmap = null;


    public boolean getstartFlg() {
        return startFlg;
    }

    public void setstartFlg(boolean flag) {
        this.startFlg = flag;
    }

    /*private void loadAid() {
        try {
            pboc2 = AidlEMVL2.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));



            //printDev = AidlPrinter.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));

        } catch (RemoteException e) {
            e.printStackTrace();
        }


        new Thread() {
            public void run() {
                try {
                    isSuccess = true;
                    Log.d(TAG, "AID Imporing...");

                    //clear AID
                    //pboc2.updateAID(EMVConstant.AidCapkOptFlag.AID_CAPK_OPT_REMOVEALL_FLAG, null);

                    String[] aids = AidParam.getAid();
                    for (int i = 0; i < aids.length; i++) {
                        //pboc2.updateAID(EMVConstant.AidCapkOptFlag.AID_CAPK_OPT_ADDORUPDATE_FLAG, aids[i]);

                    }

                    Log.d(TAG, "CAPK Imporing...");

                    String[] capk_visa = CapkParam.getThaivanCapkVisa();
                    for (int i = 0; i < capk_visa.length; i++) {
                        pboc2.updateCAPK(EMVConstant.AidCapkOptFlag.AID_CAPK_OPT_ADDORUPDATE_FLAG, capk_visa[i]);
                    }

                    String[] capk_mc = CapkParam.getThaivanCapkMc();
                    for (int i = 0; i < capk_mc.length; i++) {
                        pboc2.updateCAPK(EMVConstant.AidCapkOptFlag.AID_CAPK_OPT_ADDORUPDATE_FLAG, capk_mc[i]);
                    }

                    String[] capk_cup = CapkParam.getThaivanCapkCup();
                    for (int i = 0; i < capk_cup.length; i++) {
                        pboc2.updateCAPK(EMVConstant.AidCapkOptFlag.AID_CAPK_OPT_ADDORUPDATE_FLAG, capk_cup[i]);
                    }

                    String[] capk_jcb = CapkParam.getThaivanCapkJcb();
                    for (int i = 0; i < capk_jcb.length; i++) {
                        pboc2.updateCAPK(EMVConstant.AidCapkOptFlag.AID_CAPK_OPT_ADDORUPDATE_FLAG, capk_jcb[i]);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }

                if (isSuccess) {
                    Log.d(TAG, "AID/CAPK Import Completed");
                } else {
                    Log.d(TAG, "AID/CAPK Import Failed");
                }
            }

            ;
        }.start();
    }
*/
    public AidlPrinter getInstancesPrint() {
        /*
        if (printDev != null) {
            return printDev;
        }
        */
        return null;
    }

    public tleinterface getTleVersionOne() {
        if (tleVersionOne != null) {
            return tleVersionOne;
        }
        return null;
    }
//    public AidlSystemSettingService getSettingService() {
//        if (settingService != null) {
//            return settingService;
//        }
//        return null;
//    }

    public void RKIdownload() {
        try {
            tleVersionOne.RKIdownload();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startTransaction(int Type, String amount) {

        Log.d(TAG, "startTransaction: ");
        isRF = 0;
        PIN_PYPASS = false;
        SIGNATURE = true;
        OPERATE_ID = Type;
        AMOUNT = amount;
/*
        try {

            initClssTrans = new initClssTrans();
            initClssTrans.start();
            initClssTrans.join();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
//        try {
//            if (pboc2 != null) {
////                pboc2.cancelCheckCard(); // Edit พี่สิน
//                pboc2.endPBOC();
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        Log.d("kang", "start transaction before switch");
        switch (Type) {
            case READ:
                currentTransactionType = READ;
                //allOperateStart(READ, true, true, true, "Searching the card", "");
                break;
            case SALE:
                if(!PRE_AUTH_HAPPEN){
                    currentTransactionType = SALE;
                    allOperateStart(SALE, true, true, true, "Searching the card", "");
                }else{
                    currentTransactionType = PREAUTH;
                    allOperateStart(PREAUTH, true, true, true, "Searching the card", "");
                }
                break;
            case FALLBACK:
                currentTransactionType = SALE;
                allOperateStart(SALE, true, false, false, "Searching the card", "");
                break;
            case VOID:
                break;
            case SETTLEMENT:
                break;
            case CHECK_POINT:
                currentTransactionType = CHECK_POINT;
                //allOperateStart(CHECK_POINT, true, true, false, "Searching the card", "");
                break;
            default:
                break;
        }
    }

    public void endProcess() {
        Log.d(TAG, "pboc end process!!!");
        try {
            response_code = "";
            //pboc2.endPBOC();
//            pboc2.abortPBOC();
            onLineNow = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abortPBOCProcess() {
        /*
        Log.d(TAG, "pboc end process!!!");
        try {
            Thread.sleep(1000);
            response_code = "";
            pboc2.abortPBOC();
            onLineNow = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
    }

    public void allOperateStart(final byte operateId, final boolean isCheckMag,
                                final boolean isCheckIC, final boolean isChecRF,
                                final String msgPrompt, final String msg) {
        Log.d(TAG, msgPrompt);
        try {

            if (cardHelperListener != null) {
                cardHelperListener.onFindCard();
            }
            if(isCheckIC && !isCheckMag && !isChecRF) {
                readedType = ICCARD;

                if (operateId == SALE) {
                    //  Create message field
                    mBlockDataSend = new String[64];
                    mBlockDataSend[3 - 1] = SALE_PROCESSING_CODE;
                    String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
                    mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(traceIdNo.isEmpty() ? "1" : traceIdNo));

                    PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
                    mBlockDataSend[22 - 1] = POS_ENT_MODE;
                    mBlockDataSend[25 - 1] = "00"; // Insert Chip

                    POSEM = POS_ENT_MODE;
                    Log.d(TAG, "onFindICCard1  HOST_CARD : " + HOST_CARD);
                    //  Find card info
                    allProcess(operateId, ICCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                    Log.d(TAG, "onFindICCard2: \n OperateId = " + operateId + "\n ICCARD = " + ICCARD + "\n MSG = " + msg + "\n isCheckMsg = " + isCheckMag);
                }else{
                    Log.d(TAG, "onFindICCard5  HOST_CARD : " + HOST_CARD);
                    allProcess(operateId, ICCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                }
            }
            else if(!isCheckIC && isCheckMag && !isChecRF) {
                readedType = MAGCARD;
                Log.d("kang","mag all operatestart");
                SERVICECODE = card.getServiceCode();
                NAMECARDHOLDER = "";

                if (FALLBACK_HAPPEN ) {
                    Card cardMsg = new Card(card.getNo());
                    cardMsg.setExpireDate(card.getExpireDate());
                    cardMsg.setServiceCode(card.getServiceCode());
                    card = cardMsg;
                    MAG_TRX_RECV = true;
//                                HOST_CARD = CardPrefix.getTypeCard(card.getNo());
                    BIN_TYPE = CardPrefix.getTypeCard(card.getNo());

                    Log.d(TAG, "onFindMagCard: " + HOST_CARD);
                    TRACK1 = BlockCalculateUtil.get45BlockData(HexUtil.bcd2str(TRACK1.getBytes()));
                    TRACK2 = BlockCalculateUtil.get35Data(TRACK2);

                    Log.d(TAG, "Track 1 = " + TRACK1);
                    Log.d(TAG, "Track 2 = " + TRACK2);
                    String[] name = TRACK1.split("5E");
                    Log.d(TAG, "onFindMagCard " + name.length + " : Name = " + name[0] + " === " + BlockCalculateUtil.hexToString(name[1]));
                    NAMECARDHOLDER = BlockCalculateUtil.hexToString(name[1]);



                    switch (currentTransactionType) {
                        case SALE:
                            processCallback(PROCESS_MAG_REQUEST_AMOUNT);
                            break;
                        default:
                            break;
                    }
                    //allProcess(operateId, MAGCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                }else if(SERVICECODE.substring(0,1).equals("1")){
                    Card cardMsg = new Card(card.getNo());
                    cardMsg.setExpireDate(card.getExpireDate());
                    cardMsg.setServiceCode(card.getServiceCode());
                    card = cardMsg;
                    MAG_TRX_RECV = true;
//                                HOST_CARD = CardPrefix.getTypeCard(card.getNo());
                    BIN_TYPE = CardPrefix.getTypeCard(card.getNo());

                    Log.d(TAG, "onFindMagCard: " + HOST_CARD);
                    TRACK1 = BlockCalculateUtil.get45BlockData(HexUtil.bcd2str(TRACK1.getBytes()));
                    TRACK2 = BlockCalculateUtil.get35Data(TRACK2);

                    Log.d(TAG, "Track 1 = " + TRACK1);
                    Log.d(TAG, "Track 2 = " + TRACK2);
                    String[] name = TRACK1.split("5E");
                    Log.d(TAG, "onFindMagCard " + name.length + " : Name = " + name[0] + " === " + BlockCalculateUtil.hexToString(name[1]));
                    NAMECARDHOLDER = BlockCalculateUtil.hexToString(name[1]);



                    processCallback(PROCESS_MAG_REQUEST_AMOUNT);

                    //allProcess(operateId, MAGCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);

                }else{

                    processCallback(PROCESS_TRANS_RESULT_OTHER);
                    //allProcess(operateId, MAGCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                }
            }
            else if(!isCheckIC && !isCheckMag && isChecRF) {
                readedType = RFCARD;
                CheckCardCallback(CHECKCARD_ONFINDRFCARD);
                MAG_TRX_RECV = true;
                isRF = 1;
                Log.d(TAG, "Middle Transaction !!!!!!!!!!_JEFF");
                if (cardHelperListener != null) {
                    cardHelperListener.onFindCard();
                }

                String invoiceNumber;
                invoiceNumber =  CardPrefix.getInvoice(context, HOST_CARD);

                if(operateId == SALE){

                    //  Create message field
                    mBlockDataSend = new String[64];
                    mBlockDataSend[3 - 1] = SALE_PROCESSING_CODE;
                    mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);
                    String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
                    mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(traceIdNo.isEmpty() ? "1" : traceIdNo));
                    PROCESSING_CODE = mBlockDataSend[3 - 1];
                    mBlockDataSend[22 - 1] = "0072";
                    NII =  Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
                    Log.d("kang","nii:" + NII + ",preference_nii:" + Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS));
                    mBlockDataSend[24 - 1] = NII;
                    mBlockDataSend[25 - 1] = "05";
                    mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                    mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                    mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                    //mBlockDataSend[62 - 1] = "0006303030303635";
                    POSEM = CL_POS_ENT_MODE;
                    //Log.d(TAG, "onFindICCard  HOST_CARD : " + HOST_CARD);
                    //  Find card info
                    long starttime = System.currentTimeMillis();
                    allProcess(operateId, RFCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                    long endtime = System.currentTimeMillis();

                    Log.d("check_time", "allprocess:" + (endtime - starttime));
                    Log.d(TAG, "onFindICCard: \n OperateId = " + operateId + "\n ICCARD = " + ICCARD + "\n MSG = " + msg + "\n isCheckMsg = " + isCheckMag);
                }
            }

            /*
            pboc2.checkCard(isCheckMag, isCheckIC, isChecRF, findCardTimeout,
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

                            if (cardHelperListener != null) {
                                cardHelperListener.onFindCard();
                            }

                            if (operateId == SALE) {
                                //  Create message field
                                mBlockDataSend = new String[64];
                                mBlockDataSend[3 - 1] = SALE_PROCESSING_CODE;
                                PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
                                mBlockDataSend[22 - 1] = POS_ENT_MODE;
                                mBlockDataSend[25 - 1] = "00"; // Insert Chip

                                POSEM = POS_ENT_MODE;
                                Log.d(TAG, "onFindICCard  HOST_CARD : " + HOST_CARD);
                                //  Find card info
                                allProcess(operateId, ICCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                                Log.d(TAG, "onFindICCard: \n OperateId = " + operateId + "\n ICCARD = " + ICCARD + "\n MSG = " + msg + "\n isCheckMsg = " + isCheckMag);
                            }else if(operateId == PREAUTH){
                                //  Create message field
                                mBlockDataSend = new String[64];
                                mBlockDataSend[3 - 1] = PREAUTH_PROCESSING_CODE;
                                PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
                                mBlockDataSend[22 - 1] = POS_ENT_MODE;
                                mBlockDataSend[25 - 1] = "06"; // PRE_AUTH_FIX

                                POSEM = POS_ENT_MODE;
                                Log.d(TAG, "onFindICCard  HOST_CARD : " + HOST_CARD);
                                //  Find card info
                                allProcess(operateId, ICCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                                Log.d(TAG, "onFindICCard: \n OperateId = " + operateId + "\n ICCARD = " + ICCARD + "\n MSG = " + msg + "\n isCheckMsg = " + isCheckMag);
                            }else{
                                allProcess(operateId, ICCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                            }
                        }

                        @Override
                        public void onFindMagCard(ParcelableTrackData arg0)
                                throws RemoteException {
                            System.out.println("TAG:" + arg0.getCardNo());
                            System.out.println("TAG:" + arg0.getServiceCode());

                            SERVICECODE = arg0.getServiceCode();
                            NAMECARDHOLDER = "";

                            if (FALLBACK_HAPPEN ) {
                                Card cardMsg = new Card(arg0.getCardNo());
                                cardMsg.setExpireDate(arg0.getExpireDate());
                                cardMsg.setServiceCode(arg0.getServiceCode());
                                card = cardMsg;
                                MAG_TRX_RECV = true;
//                                HOST_CARD = CardPrefix.getTypeCard(card.getNo());
                                BIN_TYPE = CardPrefix.getTypeCard(card.getNo());

                                Log.d(TAG, "onFindMagCard: " + HOST_CARD);
                                TRACK1 = BlockCalculateUtil.get45BlockData(HexUtil.bcd2str(arg0.getFirstTrackData()));
                                TRACK2 = BlockCalculateUtil.get35Data(arg0);

                                Log.d(TAG, "Track 1 = " + TRACK1);
                                Log.d(TAG, "Track 2 = " + TRACK2);
                                String[] name = TRACK1.split("5E");
                                Log.d(TAG, "onFindMagCard " + name.length + " : Name = " + name[0] + " === " + BlockCalculateUtil.hexToString(name[1]));
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(name[1]);

                                MagneticCardData = arg0;

                                switch (currentTransactionType) {
                                    case SALE:
                                        processCallback(PROCESS_MAG_REQUEST_AMOUNT);
                                        break;
                                    default:
                                        break;
                                }
                            }else if(SERVICECODE.substring(0,1).equals("1")){
                                Card cardMsg = new Card(arg0.getCardNo());
                                cardMsg.setExpireDate(arg0.getExpireDate());
                                cardMsg.setServiceCode(arg0.getServiceCode());
                                card = cardMsg;
                                MAG_TRX_RECV = true;
//                                HOST_CARD = CardPrefix.getTypeCard(card.getNo());
                                BIN_TYPE = CardPrefix.getTypeCard(card.getNo());

                                Log.d(TAG, "onFindMagCard: " + HOST_CARD);
                                TRACK1 = BlockCalculateUtil.get45BlockData(HexUtil.bcd2str(arg0.getFirstTrackData()));
                                TRACK2 = BlockCalculateUtil.get35Data(arg0);

                                Log.d(TAG, "Track 1 = " + TRACK1);
                                Log.d(TAG, "Track 2 = " + TRACK2);
                                String[] name = TRACK1.split("5E");
                                Log.d(TAG, "onFindMagCard " + name.length + " : Name = " + name[0] + " === " + BlockCalculateUtil.hexToString(name[1]));
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(name[1]);

                                MagneticCardData = arg0;

                                processCallback(PROCESS_MAG_REQUEST_AMOUNT);

                            }else{
                                processCallback(PROCESS_TRANS_RESULT_OTHER);
                            }
                        }

                        @Override
                        public void onFindRFCard() throws RemoteException {
                            CheckCardCallback(CHECKCARD_ONFINDRFCARD);
                            MAG_TRX_RECV = true;
                            isRF = 1;
                            Log.d(TAG, "Middle Transaction !!!!!!!!!!_JEFF");
                            if (cardHelperListener != null) {
                                cardHelperListener.onFindCard();
                            }

                            String invoiceNumber;
                            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);

                            if(operateId == SALE){
                                //  Create message field
                                mBlockDataSend = new String[64];
                                mBlockDataSend[3 - 1] = RF_PROCESSING_CODE;
                                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);
                                String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
                                mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(traceIdNo.isEmpty() ? "1" : traceIdNo));
                                PROCESSING_CODE = mBlockDataSend[3 - 1];
                                mBlockDataSend[22 - 1] = "0072";
                                NII = CardPrefix.getNii(context, HOST_CARD);
                                mBlockDataSend[24 - 1] = NII;
                                mBlockDataSend[25 - 1] = "00";
                                mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                                mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                                mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                                POSEM = CL_POS_ENT_MODE;
                                Log.d(TAG, "onFindICCard  HOST_CARD : " + HOST_CARD);
                                //  Find card info
                                allProcess(operateId, RFCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                                Log.d(TAG, "onFindICCard: \n OperateId = " + operateId + "\n ICCARD = " + ICCARD + "\n MSG = " + msg + "\n isCheckMsg = " + isCheckMag);
                            }else if(operateId == PREAUTH){
                                //  Create message field
                                mBlockDataSend = new String[64];
                                mBlockDataSend[3 - 1] = PREAUTH_PROCESSING_CODE;
                                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);
                                String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
                                mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(traceIdNo.isEmpty() ? "1" : traceIdNo));
                                PROCESSING_CODE = mBlockDataSend[3 - 1];
                                mBlockDataSend[22 - 1] = "0072";
                                NII = CardPrefix.getNii(context, HOST_CARD);
                                mBlockDataSend[24 - 1] = NII;
                                mBlockDataSend[25 - 1] = "06";
                                mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                                mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                                mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                                POSEM = CL_POS_ENT_MODE;
                                Log.d(TAG, "onFindICCard  HOST_CARD : " + HOST_CARD);
                                //  Find card info
                                allProcess(operateId, RFCARD, msg, isCheckMag, isCheckIC, isChecRF, msgPrompt);
                                Log.d(TAG, "onFindICCard: \n OperateId = " + operateId + "\n ICCARD = " + ICCARD + "\n MSG = " + msg + "\n isCheckMsg = " + isCheckMag);
                            }
                        }

                        @Override
                        public void onSwipeCardFail() throws RemoteException {
                            MAG_TRX_RECV = false;
                            CheckCardCallback(CHECKCARD_ONSWIPECARDFAIL);
                            Log.d(TAG, "onSwipeCardFail");
                            if (cardHelperListener != null) {
                                cardHelperListener.onSwipeCardFail();
                            }
                        }

                        @Override
                        public void onTimeout() throws RemoteException {
                            MAG_TRX_RECV = false;
                            CheckCardCallback(CHECKCARD_ONTIMEOUT);
                            Log.d(TAG, "onTimeout");
                        }

                    });
            */
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
            tagOf55List.add("82");      // Source from ICC
            tagOf55List.add("84");      // Source from ICC ;UL Recommended
            tagOf55List.add("95");      // Source from Terminal
            tagOf55List.add("9A");      // Source from Terminal
            tagOf55List.add("9C");      // Source from Terminal
            tagOf55List.add("5F2A");    // Currency code
            tagOf55List.add("5F30");    // Currency code
            tagOf55List.add("5F34");    // Source from ICC
            tagOf55List.add("9F02");    // Source from Terminal
            tagOf55List.add("9F03");    // Source from Terminal
            tagOf55List.add("9F09");    // Source from Terminal
            tagOf55List.add("9F10");    // Source from ICC
            tagOf55List.add("9F1A");    // Source from Terminal; Country code
            tagOf55List.add("9F1E");    // Source from Terminal
            tagOf55List.add("9F26");    // Source from ICC
            tagOf55List.add("9F27");    // Source from ICC
            tagOf55List.add("9F33");    // Source from Terminal
            tagOf55List.add("9F34");    // Source from Terminal
            tagOf55List.add("9F35");    // Source from Terminal
            tagOf55List.add("9F36");    // Source from ICC
            tagOf55List.add("9F37");    // Source from Terminal
            tagOf55List.add("9F41");    // Source from Terminal
            tagOf55List.add("9F53"); //MC
            Log.d(TAG, "tagOf55List EPS End: ");
        } else {
            tagOf55List = new ArrayList<String>();
            //tagOf55List.add("5F24"); // Expiry
            tagOf55List.add("82");      // Source from ICC
            tagOf55List.add("84");      // Source from ICC ;UL Recommended
            tagOf55List.add("95");      // Source from Terminal
            tagOf55List.add("9A");      // Source from Terminal
            tagOf55List.add("9C");      // Source from Terminal
            tagOf55List.add("5F2A");    // Currency code
            tagOf55List.add("5F30");    // Currency code
            tagOf55List.add("5F34");    // Source from ICC
            tagOf55List.add("9F02");    // Source from Terminal
            tagOf55List.add("9F03");    // Source from Terminal
            tagOf55List.add("9F09");    // Source from Terminal
            tagOf55List.add("9F10");    // Source from ICC
            tagOf55List.add("9F1A");    // Source from Terminal; Country code
            tagOf55List.add("9F1E");    // Source from Terminal
            tagOf55List.add("9F26");    // Source from ICC
            tagOf55List.add("9F27");    // Source from ICC
            tagOf55List.add("9F33");    // Source from Terminal
            tagOf55List.add("9F34");    // Source from Terminal
            tagOf55List.add("9F35");    // Source from Terminal
            tagOf55List.add("9F36");    // Source from ICC
            tagOf55List.add("9F37");    // Source from Terminal
            tagOf55List.add("9F41");    // Source from Terminal
            tagOf55List.add("9F53"); //MC
        }
    }

    private void setTagOfList55_JCB() {
        Log.d(TAG, "tagOf55List SALE Start jcb: ");
        tagOf55List = new ArrayList<String>();

        tagOf55List.add("82");
        tagOf55List.add("84");
        tagOf55List.add("95");
        tagOf55List.add("9A");
        tagOf55List.add("9C");
        tagOf55List.add("5F2A");
        tagOf55List.add("5F30");
        tagOf55List.add("5F34");
        tagOf55List.add("9F02");
        tagOf55List.add("9F03");
        tagOf55List.add("9F06");
        tagOf55List.add("9F09");

        tagOf55List.add("9F10");
        tagOf55List.add("9F1A");
        tagOf55List.add("9F1E");
        tagOf55List.add("9F26");
        tagOf55List.add("9F27");
        tagOf55List.add("9F33");
        tagOf55List.add("9F34");
        tagOf55List.add("9F35");
        tagOf55List.add("9F36");
        tagOf55List.add("9F37");
        tagOf55List.add("9F41");

        tagOf55List.add("9F5C");
        tagOf55List.add("9F15");
        tagOf55List.add("9F52");

        Log.d(TAG, "tagOf55List SALE End: ");
    }

    private void setTagOfList55_CL() {
        Log.d(TAG, "tagOf55List SALE Start: mc");
        tagOf55List = new ArrayList<String>();

        tagOf55List.add("82");
        tagOf55List.add("84");
        tagOf55List.add("95");
        tagOf55List.add("9A");
        tagOf55List.add("9C");
        tagOf55List.add("5F2A");
        tagOf55List.add("5F30");
        tagOf55List.add("5F34");
        tagOf55List.add("9F02");
        tagOf55List.add("9F03");
        tagOf55List.add("9F06");
        tagOf55List.add("9F09");
        tagOf55List.add("9F10");
        tagOf55List.add("9F1A");
        tagOf55List.add("9F1E");

        tagOf55List.add("9F26");
        tagOf55List.add("9F27");
        tagOf55List.add("9F33");
        tagOf55List.add("9F34");
        tagOf55List.add("9F35");
        tagOf55List.add("9F36");
        tagOf55List.add("9F37");
        tagOf55List.add("9F41");
        tagOf55List.add("9F53");
        Log.d(TAG, "tagOf55List SALE End: ");
    }


    public void allProcess(final byte operateId, final byte cardType,
                           final String msg, final boolean isCheckMag,
                           final boolean isCheckIC, final boolean isChecRF,
                           final String msgPrompt) {
        CARDTYPE = cardType;
        Log.d(TAG, "cardType :" + operateId + "cardType : " + cardType + "msg : " + msg + "isCheckMag :" + isCheckMag + "isCheckIC : " + isCheckIC + " isChecRF: " + isChecRF + " msgPrompt : " + msgPrompt);
        /*
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
            case SEARCH_CARD :
                paramEmvTransData.setTranstype((byte) EMVConstant.TransType.TRANS_TYPE_READCARDNO);
                break;
            case CASH:
                paramEmvTransData.setTranstype((byte) EMVConstant.TransType.TRANSTYPE_CASH);
                break;
            case COMSUME:
            case PREAUTH:
            case VOID_PREAUTH:
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

        if (cardType == ICCARD) {
            paramEmvTransData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);
            paramEmvTransData.setEMVFlow((byte) EMVConstant.EMVFlowSelect.EMV_FLOW_PBOC);
            paramEmvTransData.setIsEcashEnable(false); //del
            paramEmvTransData.setIsSmEnable(false); //del
            paramEmvTransData.setIsForceOnline(false); //false
        }else{
            paramEmvTransData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);
            paramEmvTransData.setEMVFlow((byte) EMVConstant.EMVFlowSelect.EMV_FLOW_PBOC);
            paramEmvTransData.setIsEcashEnable(false);
            paramEmvTransData.setIsForceOnline(false); //UL VISA CONTACTLESS
            paramEmvTransData.setIsSmEnable(false);
        }
*/
        try {

            String RID = "";
            String nameCard = "";
            String cardLabel = "";
            String preferedName = "";

            if(bcd2str(ImplEmv.getTlv(0x57)).equals("")) { // RF
              /*  if(isChecRF) {
                    ByteArray byteArray = new ByteArray();
                    String s;
                    switch(ClssEntryPoint.getInstance().getOutParam().ucKernType) {
                        case KernType.KERNTYPE_MC:
                            ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {0x57}, (byte)1, 19, byteArray);
                            s = bcd2str(byteArray.data);
                            s = "57" + Integer.toHexString(byteArray.length) + s;
                            Log.d("kang","mc 0x57 data : " + s);
                            card = analyse(s);
                            ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {0x5F, 0x34}, (byte)2, 1, byteArray);
                            s = bcd2str(byteArray.data);
                            if(byteArray.length <= 15) {
                                s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                            }
                            else {
                                s = "5F34" + Integer.toHexString(byteArray.length) + s;
                            }
                            Log.d("kang","mc 0x5F34 data : " + s);
                            tagPanSnEMV = s.substring(6,8);
                            ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {0x4F}, (byte)1, 10, byteArray);
                            s = bcd2str(byteArray.data).substring(0,byteArray.length * 2);
                            if(byteArray.length < 10) {
                                AID = "4F" + "0" + Integer.toHexString(byteArray.length) + s;
                            }
                            else {
                                AID = "4F" + Integer.toHexString(byteArray.length) + s;
                            }

                            Log.d("kang","mc 0x4F data : " + s);


                            ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {0x5F, 0x20}, (byte)2, 26, byteArray);
                            nameCard = "5F20" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {0x50}, (byte)1, 10, byteArray);
                            cardLabel = "50" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {(byte)0x9F, 0x12}, (byte)2, 10, byteArray);
                            preferedName = "9F12" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);

                            break;
                        case KernType.KERNTYPE_VIS:
                            ClssWaveApi.Clss_GetTLVData_Wave( (short)0x57, byteArray);
                            s = bcd2str(byteArray.data);
                            s = "57" + Integer.toHexString(byteArray.length) + s;
                            Log.d("kang","visa 0x57 data : " + s);
                            card = analyse(s);
                            ClssWaveApi.Clss_GetTLVData_Wave((short)0x5F34  ,byteArray);
                            Log.d("kang", "wave_5f34 length : " + byteArray.length);
                            Log.d("kang", "wave_5f34 data : " + bcd2Str(byteArray.data));
                            s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            Log.d("kang","before add tag 5F34:" + s);
                            if(byteArray.length <= 15) {
                                s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                            }
                            else {
                                s = "5F34" + Integer.toHexString(byteArray.length) + s;
                            }
                            Log.d("kang","visa 0x5F34 data : " + s);
                            tagPanSnEMV = s.substring(6,8);
                            ClssWaveApi.Clss_GetTLVData_Wave((short)0x4F, byteArray);
                            s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            AID = "4F" + "0" + Integer.toHexString(byteArray.length) + s;
                            Log.d("kang","visa 0x4F data : " + s);

                            ClssWaveApi.Clss_GetTLVData_Wave((short)0x5f20  ,byteArray);
                            Log.d("kang", "wave/5f20/data:" + bcd2Str(byteArray.data) + ", length:" + byteArray.length);
                            nameCard = "5F20" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssWaveApi.Clss_GetTLVData_Wave((short)0x50  ,byteArray);
                            Log.d("kang", "wave/50/data:" + bcd2Str(byteArray.data) + ", length:" + byteArray.length);
                            cardLabel = "50" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssWaveApi.Clss_GetTLVData_Wave((short)0x9f12  ,byteArray);
                            Log.d("kang", "wave/9f12/data:" + bcd2Str(byteArray.data) + ", length:" + byteArray.length);
                            preferedName = "9F12" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);

                            break;
                        case KernType.KERNTYPE_PBOC:
                            ClssPbocApi.Clss_GetTLVData_Pboc((short)0x57, byteArray);
                            s = bcd2str(byteArray.data);
                            s = "57" + Integer.toHexString(byteArray.length) + s;
                            Log.d("kang","pboc 0x57 data : " + s);
                            card = analyse(s);
                            ClssPbocApi.Clss_GetTLVData_Pboc((short)0x5f34 ,byteArray);
                            s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            if(byteArray.length <= 15) {
                                s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                            }
                            else {
                                s = "5F34" + Integer.toHexString(byteArray.length) + s;
                            }
                            Log.d("kang","pboc 0x5F34 data : " + s);
                            tagPanSnEMV = s.substring(6,8);
                            ClssPbocApi.Clss_GetTLVData_Pboc((short)0x4F, byteArray);
                            s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            AID = "4F" + Integer.toHexString(byteArray.length) + s;
                            Log.d("kang","pboc 0x4F data : " + s);

                            ClssPbocApi.Clss_GetTLVData_Pboc((short)0x5f20 ,byteArray);
                            nameCard = "5F20" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssPbocApi.Clss_GetTLVData_Pboc((short)0x50 ,byteArray);
                            cardLabel = "50" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssPbocApi.Clss_GetTLVData_Pboc((short)0x9f12 ,byteArray);
                            preferedName = "9F12" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);

                            break;
                        case KernType.KERNTYPE_JCB:
                            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {0x57}, (byte)1, 60, byteArray);
                            s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            s = "57" + Integer.toHexString(byteArray.length) + s;
                            Log.d("kang","mc 0x57 data : " + s);
                            card = analyse(s);
                            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {0x5F, 0x34}, (byte)2, 10, byteArray);
                            s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            if(byteArray.length <= 15) {
                                s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                            }
                            else {
                                s = "5F34" + Integer.toHexString(byteArray.length) + s;
                            }
                            Log.d("kang","mc 0x5F34 data : " + s);
                            tagPanSnEMV = s.substring(6,8);
                            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {0x4F}, (byte)1, 17, byteArray);
                            s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            AID = "4F" + Integer.toHexString(byteArray.length) + s;
                            Log.d("kang","mc 0x4F data : " + s);

                            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {0x5F, 0x20}, (byte)2, 60, byteArray);
                            nameCard = "5F20" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {0x50}, (byte)1, 10, byteArray);
                            cardLabel = "50" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {(byte)0x9F, 0x12}, (byte)2, 60, byteArray);
                            preferedName = "9F12" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);

                            break;
                    }
                    EXPIRY = card.getExpireDate();
                    BIN_TYPE = CardPrefix.getTypeCard(card.getNo());
                }
                rfflag = true;
                AID = AID.substring(4);
                RID = AID.substring(0, 10);


                if(RID.equals("A000000333")){
                    //UPI for chinese language
                    try {
                        if (nameCard.length() > 6) {
                            byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                            NAMECARDHOLDER = new String(name_array, "gbk");
                        }

                        if (cardLabel.length() > 4) {
                            byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                            CARDLABEL = new String(name_array2, "gbk");
                        }

                        if (preferedName.length() > 0) {
                            byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                            PREFEREDNAME = new String(name_array3, "gbk");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    if (nameCard.length() > 6) {
                        NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                    }

                    if (cardLabel.length() > 4) {
                        CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                    }

                    if (preferedName.length() > 0) {
                        PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                    }
                }*/
            }

            else if(isCheckIC){

                card = analyse(bcd2str(ImplEmv.getTlv(0x57)));
                System.out.println("after analyse : " + card.toString());
                EXPIRY = card.getExpireDate();
                BIN_TYPE = CardPrefix.getTypeCard(card.getNo());
                tagPanSnEMV = bcd2str(ImplEmv.getTlv(0x5F34));
                if(tagPanSnEMV.length() != 0) {
                    tagPanSnEMV = tagPanSnEMV.substring(6, 8);
                }

                AID = bcd2str(ImplEmv.getTlv(0x4F));
                AID = AID.substring(4, AID.length());

                nameCard = bcd2str(ImplEmv.getTlv(0x5F20));
                cardLabel = bcd2str(ImplEmv.getTlv(0x50));
                preferedName = bcd2str(ImplEmv.getTlv(0x9F12));


                RID = AID.substring(0, 10);

                if(RID.equals("A000000333")){
                    //UPI for chinese language
                    try {
                        if (nameCard.length() > 6) {
                            byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                            NAMECARDHOLDER = new String(name_array, "gbk");
                        }

                        if (cardLabel.length() > 4) {
                            byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                            CARDLABEL = new String(name_array2, "gbk");
                        }

                        if (preferedName.length() > 0) {
                            byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                            PREFEREDNAME = new String(name_array3, "gbk");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    if (nameCard.length() > 6) {
                        NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                    }

                    if (cardLabel.length() > 4) {
                        CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                    }

                    if (preferedName.length() > 0) {
                        PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                    }
                }
            }
            else if(isCheckMag) {
                card = analyse(bcd2str(ImplEmv.getTlv(0x57)));
                System.out.println("after analyse : " + card.toString());
                EXPIRY = card.getExpireDate();
                BIN_TYPE = CardPrefix.getTypeCard(card.getNo());

                /*tagPanSnEMV = bcd2str(ImplEmv.getTlv(0x5F34));
                if(tagPanSnEMV.length() != 0) {
                    tagPanSnEMV = tagPanSnEMV.substring(6, 8);
                }*/

            }

            System.out.println("first print");
            System.out.println("card:" + card.toString());
            System.out.println("BIN_TYPE:" + BIN_TYPE);
            System.out.println("tagPanSnEMV:" + tagPanSnEMV);
            System.out.println("AID:" + AID);
            System.out.println("RID:" + RID);
            System.out.println("namecard:" + nameCard);
            System.out.println("preferedName:" + preferedName);
            System.out.println("cardLabel:" + cardLabel);
            System.out.println("NAMECARDHOLDER:" + NAMECARDHOLDER);
            System.out.println("CARDLABEL:" + CARDLABEL);
            System.out.println("PREFEREDNAME:" + PREFEREDNAME);



//            String Tc = bcd2Str(ImplEmv.getTlv(0x9F26));
            String Tag_4f = bcd2Str(ImplEmv.getTlv(0x4F));
            //Log.d(TAG, "EMVTAG_TVR: " + bcd2str(ImplEmv.getTlv(0x95)));
            //Log.d(TAG, "utility::" + "----> CardInfoData ==> Tag_4f " + Tag_4f + " CARD NO = " + card.getNo().substring(0, 1));

            //Log.d(TAG, "----> CardInfoData ==> Tag_4f Cut len:" + Tag_4f.substring(4, 14) + " CARD NO = " + card.getNo().substring(0, 1));
            //Log.d(TAG, "----> CardInfoData ==> PREFEREDNAME " + PREFEREDNAME);
            String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
            invoiceGB = calNumTraceNo(invoiceNumber);
            //VISA CIMB thai 4F07A0000000031010  <-- same inter.

            // Paul_20181107 include A000000333




                invoiceGB = calNumTraceNo(invoiceNumber);
                String mBlockData24 = "";

                mBlockData24 = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);

                setTagOfList55();
                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);

//                    tagPanSnEMV = tagPanSnEMV != null ? tagPanSnEMV.substring(6, 8) : "0001";
                System.out.printf("utility:: YYYYYYYYYYYYY tagPanSnEMV = %s \n", tagPanSnEMV);
                /*
                if (tagPanSnEMV != null) {
                    mBlockDataSend[23 - 1] = CardPrefix.hexadecimalToInt(tagPanSnEMV);
                } else {
                    mBlockDataSend[23 - 1] = "0001";
                }
                */

                System.out.printf("utility:: YYYYYYYYYYYYY mBlockDataSend[23 - 1] = %s \n", mBlockDataSend[23 - 1]);
                Log.d(TAG, "onConfirmCardInfo: " + mBlockDataSend[23 - 1]);

                mBlockDataSend[24 - 1] = mBlockData24;

                System.out.println("terminalid : "+ Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));

                mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                //mBlockDataSend[62 - 1] = "0006303030303635";
                //Log.d(TAG, "EXPIRY = " + EXPIRY);
                
                processCallback(PROCESS_CONFIRM_CARD_INFO);
                onRequestOnline(operateId, cardType);


                //pboc2.importConfirmCardInfoRes(true);

            /*
            pboc2.processPBOC(paramEmvTransData, new PBOCListener.Stub() {
                @Override
                public void onConfirmCardInfo(CardInfoData arg0)
                        throws RemoteException {
                    //ICCardData = arg0;
                    // TODO : Check 4F

                    if(operateId != SEARCH_CARD){
                        card = analyse(readKernelData(EMVTAGS.EMVTAG_TRACK2));
                        EXPIRY = card.getExpireDate();
                        BIN_TYPE = CardPrefix.getTypeCard(card.getNo());
                        Log.d(TAG, "----> CardInfoData ==> arg0 " + arg0.toString());
                        //SINN 20180524 CHECK TH chip card
                        tagPanSnEMV = readKernelData(EMVTAGS.EMVTAG_APP_PAN_SN);
                        AID = readKernelData(EMVTAGS.EMVTAG_AID);
                        AID = AID.substring(4, AID.length());
                        String RID = AID.substring(0, 10);
                        String nameCard = readKernelData(EMVTAGS.combine(0x5F, 0x20));
                        String cardLabel = readKernelData(EMVTAGS.EMVTAG_APP_LABEL);
                        String preferedName = readKernelData(EMVTAGS.combine(0x9F, 0x12));

                        if(RID.equals("A000000333")){
                            //UPI for chinese language
                            try {
                                if (nameCard.length() > 6) {
                                    byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                                    NAMECARDHOLDER = new String(name_array, "gbk");
                                }
                                if (cardLabel.length() > 4) {
                                    byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                                    CARDLABEL = new String(name_array2, "gbk");
                                }
                                if (preferedName.length() > 0) {
                                    byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                                    PREFEREDNAME = new String(name_array3, "gbk");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else{
                            if (nameCard.length() > 6) {
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                            }
                            if (cardLabel.length() > 4) {
                                CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                            }
                            if (preferedName.length() > 0) {
                                PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                            }
                        }

//                        String Tc = readKernelData(EMVTAGS.combine(0x9F, 0x26));
                        String Tag_4f = readKernelData(EMVTAGS.EMVTAG_AID);
                        Log.d(TAG, "EMVTAG_TVR: " + readKernelData(EMVTAGS.EMVTAG_TVR));
                        Log.d(TAG, "----> CardInfoData ==> Tag_4f " + Tag_4f);
                        Log.d(TAG, "----> CardInfoData ==> PREFEREDNAME " + PREFEREDNAME);

                        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
                        invoiceGB = calNumTraceNo(invoiceNumber);
                        String mBlockData24 =  Preference.getInstance(context).getValueString(Preference.KEY_NII_PURCHASE);

                        setTagOfList55();

                        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);

                        mBlockDataSend[23 - 1] = "00" + tagPanSnEMV.substring(6,8);
                        mBlockDataSend[24 - 1] = mBlockData24;
                        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                        Log.d(TAG, "EXPIRY = " + EXPIRY);
                        pboc2.importConfirmCardInfoRes(true);
                    }else{
                        CARD_NO = arg0.getCardno();
                    }
                    processCallback(PROCESS_CONFIRM_CARD_INFO);
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    Log.d(TAG, "onError error: " + arg0);
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
                    if (operateId == SALE || operateId == PREAUTH) {
                        if(operateId == SALE) {
                            MTI = MESSAGE_SALE;
                        }else {
                            MTI = MESSAGE_PREAUTH;
                        }
                        tempSavedTrackData = readKernelData(EMVTAGS.EMVTAG_TRACK2);
                        mBlockDataSend[35 - 1] = BlockCalculateUtil.get35Data(tempSavedTrackData);

                        String DE55;

                        if(cardType == ICCARD) {
                            EXPIRY = card.getExpireDate();
                            TRACK2 = BlockCalculateUtil.get35Data(tempSavedTrackData);
                            DE55 = BlockCalculateUtil.get55Data(readKernelData(EMVTAGS.getF55Taglist()), tagOf55List);
                            String Tag9F06 = readKernelData(EMVTAGS.combine(0x9F, 0x06));
                            String Tag5F30 = readKernelData(EMVTAGS.combine(0x5F, 0x30));
                            String tcHex = readKernelData(EMVTAGS.combine(0x9F, 0x26));
                            if (tcHex.length() > 6) {
                                TC = tcHex.substring(6, tcHex.length());
                            }
                            Log.d(TAG, "----> CardInfoData ==> Tc " + TC);
                            Log.d(TAG, "----> CardInfoData ==> tcHex " + tcHex);

                            Log.d(TAG, "onRequestOnline: " + Tag5F30);
                            DE55 = DE55 + Tag9F06 + Tag5F30;

                            //check pin, online pin or offline pin
                            String Tag9F10 = readKernelData(EMVTAGS.combine(0x9F, 0x10));
                            int FlagPin = Integer.parseInt(Tag9F10.substring(14, 16), 16) & 0x04; //Check CVR Byte2 Bit3
                            if (FlagPin == 0) {
                                if (!PIN.equals(""))
                                    mBlockDataSend[52 - 1] = OffUsEPSPinBlock(CARD_NO, PIN);
                            }
                        }else {

                            AID = readKernelData(EMVTAGS.EMVTAG_AID);
                            AID = AID.substring(4, AID.length());
                            String RID = AID.substring(0, 10);
                            String nameCard = readKernelData(EMVTAGS.combine(0x5F, 0x20));
                            String cardLabel = readKernelData(EMVTAGS.EMVTAG_APP_LABEL);
                            String preferedName = readKernelData(EMVTAGS.combine(0x9F, 0x12));

                            if(RID.equals("A000000333")){
                                //UPI for chinese language
                                try {
                                    if (nameCard.length() > 6) {
                                        byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                                        NAMECARDHOLDER = new String(name_array, "gbk");
                                    }
                                    if (cardLabel.length() > 4) {
                                        byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                                        CARDLABEL = new String(name_array2, "gbk");
                                    }
                                    if (preferedName.length() > 0) {
                                        byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                                        PREFEREDNAME = new String(name_array3, "gbk");
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                if (nameCard.length() > 6) {
                                    NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                                }
                                if (cardLabel.length() > 4) {
                                    CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                                }
                                if (preferedName.length() > 0) {
                                    PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                                }
                            }


                            if(tempSavedTrackData.substring(4,5).equals("4"))
                                setTagOfList55(); //VISA
                            else if(tempSavedTrackData.substring(4,5).equals("5"))
                                setTagOfList55_CL();//MC
                            else if(tempSavedTrackData.substring(4,5).equals("3"))
                                setTagOfList55_JCB(); //JCB
                            else
                                setTagOfList55(); //UPI

                            String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
                            invoiceGB = calNumTraceNo(invoiceNumber);

                            TRACK2 = BlockCalculateUtil.get35Data(tempSavedTrackData);
                            String track2 = TRACK2.substring(2, TRACK2.length());
                            String cardno;
                            cardno = checkCardno(track2);
                            CARD_NO = cardno;
                            BIN_TYPE = CardPrefix.getTypeCard(CARD_NO);
                            DE55 = BlockCalculateUtil.get55Data(readKernelData(EMVTAGS.getF55Taglist()), tagOf55List);
                            String Tag9F06 = readKernelData(EMVTAGS.combine(0x9F, 0x06));
                            String Tag5F30 = readKernelData(EMVTAGS.combine(0x5F, 0x30));
                            String tcHex = readKernelData(EMVTAGS.combine(0x9F, 0x26));
                            if (tcHex.length() > 6) {
                                TC = tcHex.substring(6, tcHex.length());
                            }
                            Log.d(TAG, "----> CardInfoData ==> Tc " + TC);
                            Log.d(TAG, "----> CardInfoData ==> tcHex " + tcHex);

                            Log.d(TAG, "onRequestOnline: " + Tag5F30);
                            DE55 = DE55 + Tag9F06 + tcHex; //Jeff20181029

                            //check pin, online pin or offline pin
                            String Tag9F10 = readKernelData(EMVTAGS.combine(0x9F, 0x10));
                            String CTQ = readKernelData(EMVTAGS.combine(0x9F, 0x6C));

                            if(!CTQ.equals("")){
                                if(CTQ.substring(6, 8).equals("00"))
                                    SIGNATURE = false;
                            }


                            if(!Tag9F10.equals("")){
                                int FlagPin = Integer.parseInt(Tag9F10.substring(14, 16), 16) & 0x04; //Check CVR Byte2 Bit3
                                if (FlagPin == 0) {
                                    if (!PIN.equals("")) {
                                        mBlockDataSend[52 - 1] = OffUsEPSPinBlock(CARD_NO, PIN);
                                        SIGNATURE = false; //In case of support online pin, no need signature (K.Charbaby)
                                    }
                                }
                            }
                        }


                        DE55 = BlockCalculateUtil.checkMessage(DE55);
                        //int length_of_tag55 = BlockCalculateUtil.get55Data(readKernelData(EMVTAGS.getF55Taglist()), tagOf55List).length() / 2;
                        int length_of_tag55 = DE55.length() / 2;


                        mBlockDataSend[55 - 1] = BlockCalculateUtil.get55Length(length_of_tag55) + DE55;
//                        mBlockDataSend[55 - 1] = "016582027D008408A000000333010101950504000480009A031805229C01005F2A0207645F300202205F3401019F02060000000001039F03060000000000009F0608A0000003330101019F090200209F101307000103A0A000010A010000000000098918999F1A0207649F1E0838353130494343009F26083F454A922877F55A9F2701809F3303E0F8C89F34034203009F3501229F360200C99F370430F1E5DD9F410400000054";
                        Log.d(TAG, "block 55 = " + mBlockDataSend[55 - 1]);
                        //EXPIRY = BlockCalculateUtil.getExpireData(mBlockDataSend[35-1]);
                        MBLOCK55 = mBlockDataSend[55 - 1];
                        onLineNow = true;
                        processCallback(PROCESS_TRANSACTION_STARTING);
                        TPDU = CardPrefix.getTPDU(context, "EPS");
//                        if (HOST_CARD.equalsIgnoreCase("EPS")) {
//                            setDataSalePINEPS();
//                        }
                        packageAndSend(TPDU, MTI, mBlockDataSend);

                    }else if (operateId == UPLOAD) {
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

                    //for UL test
                    String Tag_9b = readKernelData(EMVTAGS.EMVTAG_TSI);
                    String Tag_aid = readKernelData(EMVTAGS.EMVTAG_AID);
                    Log.d(TAG, "----> CardInfoData ==> Tag_9b " + Tag_9b);
                    Log.d(TAG, "----> CardInfoData ==> AID" + Tag_aid);

                    switch (arg0) {
                        case EMVConstant.TransResult.TRANS_RESULT_ABORT:
                            Log.d(TAG, "pboc_trans_abort");
                            tempSavedAllData = readKernelData(EMVTAGS.getF55Taglist());
                            processCallback(PROCESS_TRANS_RESULT_ABORT);
                            if (transResultAbortLister != null) {
                                transResultAbortLister.onTransResultAbort();
                            }
                            break;
                        case EMVConstant.TransResult.TRANS_RESULT_APPROVE:
                            Log.d(TAG, "pboc_trans_accept");

                            if (operateId == READ) {
                                readCardNo();
                                if (cardHelperListener != null) {
                                    cardHelperListener.onCardNo(CARD_NO);
                                }
                            } else {
                                //checking online transaction or offline transacction
                                String Tag_9f35 = readKernelData(EMVTAGS.EMVTAG_TERM_TYPE);
                                if(Tag_9f35.substring(6,8).equals("23")){
                                    //offline
                                    tempSavedAllData = readKernelData(EMVTAGS.getF55Taglist());
                                    mBlockDataReceived[37 - 1] = "3030303030303030";
                                    mBlockDataReceived[38 - 1] = "3030303030303030";
                                    mBlockDataReceived[39 - 1] = "3030";
                                    RRN = mBlockDataReceived[37 - 1]; //field37
                                    APPRVCODE = mBlockDataReceived[38 - 1]; //field37
                                    processCallback(PROCESS_TRANS_RESULT_APPROVE);
                                    insertTransaction("C");
                                }else{
                                    //online
                                    tempSavedAllData = readKernelData(EMVTAGS.getF55Taglist());
                                    RRN = mBlockDataReceived[37 - 1]; //field37
                                    APPRVCODE = mBlockDataReceived[38 - 1]; //field37

                                    processCallback(PROCESS_TRANS_RESULT_APPROVE);
                                }
                            }

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
                            if (nfcListener != null)
                                nfcListener.onfindNFC();
                            break;
                        case EMVConstant.TransResult.TRANS_RESULT_REFUSE:
                            Log.d(TAG, "failed :: 交易拒绝");
                            tempSavedAllData = readKernelData(EMVTAGS.getF55Taglist());
                            processCallback(PROCESS_TRANS_RESULT_REFUSE);

                            if(PIN_PYPASS) {
                                if (responsePINListener != null)
                                    responsePINListener.onBYPASSfail();
                            }
//                            else{
//                                if (transResultAbortLister != null) {
//                                    transResultAbortLister.onTransResultAbort();
//                                }
//                            }
                            break;
                        default:
                            Log.d(TAG, "pboc_trans_error , error_code = " + arg0);
                            processCallback(PROCESS_TRANS_RESULT_UNKNOW);
                    }
                    pboc2.endPBOC();
                }

                @Override
                public void requestAidSelect(int arg0, String[] arg1) throws RemoteException {
                    int item = arg1.length;
                    Log.d(TAG, "pboc_request_aidselect");
                    for (String aid : arg1) {
                        Log.d(TAG, "[" + aid + "]");
                    }
                    if (cardHelperListener != null)
                        cardHelperListener.onMultiApp(item, arg1);

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

                    pboc2.importAmount(AMOUNT);

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
                    Log.d(TAG, "pboc_input_pin");

                    if (responsePINListener != null)
                        responsePINListener.onRequirePIN();

                    Log.d(TAG, "arg0 = " + arg0 + "\narg1 = " + arg1 + "\narg2 = " + arg2);
                }

                @Override
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
            });*/

            } catch(Exception e){
                e.printStackTrace();
                String message = e.getMessage();
                Log.d(TAG, "allprocess exception : " + message);
                processCallback(PROCESS_ERROR);
            }

    }

    public void onRequestOnline(byte operateId, byte cardType) {
        //showMessage("Request_Online_Transaction");
        Log.d(TAG, "Request_Online_Transaction/Type:" + cardType);
        Log.d(TAG, "onRequestOnline: " + bcd2Str(ImplEmv.getTlv(0x9F02)));
        // 读取5F34卡序列号的时候不需要在请求联机这个时候读取，
        // If you need  the value from tag "9F26", You should read the Five-five data in onRequestOnline
        // 需要在请求联机交易时读取
        if (operateId == SALE || operateId == PREAUTH) {
            if(operateId == SALE) {
                MTI = MESSAGE_SALE;
            }else {
                MTI = MESSAGE_PREAUTH;
            }
            if(ImplEmv.getTlv(0x57) != null) {
                tempSavedTrackData = bcd2str(ImplEmv.getTlv(0x57));
            }
            else if(cardType == RFCARD) {
                ByteArray tk2 = new ByteArray();
                ByteArray tsi = new ByteArray();
                ByteArray tvr = new ByteArray();
                CL_POS_ENT_MODE = "0072";
                mBlockDataSend[22 - 1] = CL_POS_ENT_MODE;

                int tsi_num = 0, tvr_num = 0;
                String amount="";
                byte[] arr={};
                ByteArray byteArray = new ByteArray();
                switch(ClssEntryPoint.getInstance().getOutParam().ucKernType) {
                    case KernType.KERNTYPE_MC:
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {0x57},(byte)2, 60, tk2);
                        tvr_num = ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {(byte)0x95}, (byte)2, 2, tvr);
                        tsi_num = ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {(byte)0x9B}, (byte)2, 5, tsi);
                        amount = AMOUNT.replace(".", "");
                        ClssPassApi.Clss_ReadVerInfo_MC(byteArray);
                        arr = str2Bcd("9F03" + "0"  + amount.length() + amount +  "9F09" + "02" +
                                 bcd2Str(byteArray.data) + "9F34" + "03" + ClssPayPass.getInstance().getCVMType());
                        ClssPassApi.Clss_SetTLVDataList_MC(arr,3);
                        break;
                    case KernType.KERNTYPE_VIS:
                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x57, tk2);
                        tvr_num = ClssWaveApi.Clss_GetTLVData_Wave( (short)0x95, tvr);
                        tsi_num = ClssWaveApi.Clss_GetTLVData_Wave( (short)0x9B, tsi);
                        amount = AMOUNT.replace(".", "");
                        ClssWaveApi.Clss_ReadVerInfo_Wave(byteArray);
                        ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F03, str2Bcd(amount), 6);
                        ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F09, byteArray.data, 2);
                        ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F34, new byte[]{ClssWaveApi.Clss_GetCvmType_Wave()}, 3);
                        break;
                    case KernType.KERNTYPE_PBOC:
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x57, tk2);
                        tvr_num = ClssPbocApi.Clss_GetTLVData_Pboc( (short)0x95, tvr);
                        tsi_num = ClssPbocApi.Clss_GetTLVData_Pboc( (short)0x9B, tsi);
                        amount = AMOUNT.replace(".", "");
                        CvmType cvmType = new CvmType();
                        ClssPbocApi.Clss_GetCvmType_Pboc(cvmType);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_ReadVerInfo_Pboc(byteArray);

                        ClssPbocApi.Clss_SetTLVData_Pboc((short)0x9F03, str2Bcd(amount), 6);
                        ClssPbocApi.Clss_SetTLVData_Pboc((short)0x9F09, byteArray.data, 2);
                        ClssPbocApi.Clss_SetTLVData_Pboc((short)0x9F34, new byte[]{(byte)cvmType.type}, 3);

                        break;
                    case KernType.KERNTYPE_JCB:
                        ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {0x57},(byte)2, 60, tk2);
                        tvr_num = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {(byte)0x95}, (byte)2, 2, tvr);
                        tsi_num = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {(byte)0x9B}, (byte)2, 5, tsi);
                        byteArray = new ByteArray();
                        ClssJCBApi.Clss_ReadVerInfo_JCB(byteArray);
                        amount = AMOUNT.replace(".", "");

                        arr = str2Bcd("9F03" + "0"  + amount.length() + amount +  "9F09" + "02" +
                                bcd2Str(byteArray.data) + "9F34" + "03" + ClssJCBApi.Clss_CardAuth_JCB());

                        ClssJCBApi.Clss_SetTLVDataList_JCB(arr, 3);
                        break;
                }
                Log.d("kang","tk2:" + bcd2Str(tk2.data));
                tempSavedTrackData = "57" + Integer.toHexString(tk2.length) + bcd2str(tk2.data).substring(0, tk2.length * 2);
                TRACK2 = BlockCalculateUtil.get35Data(tempSavedTrackData);
                String track2 = TRACK2.substring(2);
                String cardno = checkCardno(track2);
                if(CheckBL(cardno)) {
                    Log.d(TAG, "There is card no [" + cardno + "] in BL" );
                    processCallback(PROCESS_TRANS_RESULT_ABORT);
                    MenuServiceListActivity.getinstance().showerr("this card is enrolled at blacklist.");

                    return;
                }
                if(tvr_num == RetCode.EMV_OK) {
                    TVR = "95" + "0" + Integer.toHexString(tvr.length) + bcd2str(tvr.data).substring(0, tvr.length * 2);
                }
                else {
                    TVR = "";
                }
                if(tsi_num == RetCode.EMV_OK) {
                    TSI = "9B" + "0" + Integer.toHexString(tsi.length) + bcd2str(tsi.data).substring(0, tsi.length * 2);
                }
                else {
                    TSI = "";
                }
                Log.d("kang","tvr:" + TVR + ",tsi:" + TSI);
                Log.d("kang","tempsavedata(57):" + tempSavedTrackData);

                String mBlock63 = "RF" + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF1), 20)
                        + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF2), 20)
                        + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF3), 20);

                mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length() + 2), 4) + CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);

            }
            String DE55 = "";

            if(cardType == ICCARD || cardType == RFCARD) {
                mBlockDataSend[35 - 1] = BlockCalculateUtil.get35Data(tempSavedTrackData);


                Log.d("kang", "before set tag55list,tempsavedtrackdata:" + tempSavedTrackData);
                setTagOfList55(); //VISA

            }

            if(cardType == ICCARD) {

                if(tagPanSnEMV.length() != 0) {
                    if(tagPanSnEMV.length() > 4) {
                        tagPanSnEMV = tagPanSnEMV.substring(6, 8);
                    }
                    mBlockDataSend[23 - 1] = "00" + tagPanSnEMV;
                }
                mBlockDataSend[35 - 1] = BlockCalculateUtil.get35Data(tempSavedTrackData);

                EXPIRY = card.getExpireDate();
                CARD_NO = card.getNo();
                TRACK2 = BlockCalculateUtil.get35Data(tempSavedTrackData);
                System.out.println("getF55Taglist");
                byte[] b = ImplEmv.getF55Taglist();

                DE55 = BlockCalculateUtil.get55Data(bcd2str(ImplEmv.getTlv2()), tagOf55List);
                String Tag9F06 = bcd2str(ImplEmv.getTlv(0x9F06));
                String Tag5F30 = bcd2str(ImplEmv.getTlv(0x5F30));
                String tcHex = bcd2str(ImplEmv.getTlv(0x9F26));
                //TC = tcHex;
                if (tcHex.length() > 6) {
                    TC = tcHex.substring(6, tcHex.length());
                }
                Log.d(TAG, "----> CardInfoData ==> DE55 " + DE55);
                Log.d(TAG, "----> CardInfoData ==> Tag9F06 " + Tag9F06);
                Log.d(TAG, "----> CardInfoData ==> Tag5F30 " + Tag5F30);
                Log.d(TAG, "----> CardInfoData ==> Tc " + TC);
                Log.d(TAG, "----> CardInfoData ==> tcHex " + tcHex);
                Log.d(TAG, "onRequestOnline: " + Tag5F30);
                DE55 = DE55 + Tag9F06 + Tag5F30;

                //check pin, online pin or offline pin
                String Tag9F10 = bcd2str(ImplEmv.getTlv(0x9F10));
                Log.d("Tag9F10", Tag9F10);
                int FlagPin = Integer.parseInt(Tag9F10.substring(14, 16 ),16) & 0x04; //Check CVR Byte2 Bit3
                if (FlagPin == 0) {
                        //mBlockDataSend[52 - 1] = OffUsEPSPinBlock(CARD_NO, PIN);
                    if(!PINBLOCK.equals("")) {
                        mBlockDataSend[52 - 1] = PINBLOCK;
                    }
                }
                int flagcvr = Integer.parseInt(Tag9F10.substring(14, 16 ),16) & 0x08; //Check CVR Byte2 Bit4

            }else if(cardType != RFCARD) {


                String nameCard = bcd2str(ImplEmv.getTlv(0x5F20));
                String cardLabel = bcd2str(ImplEmv.getTlv(0x50));
                String preferedName = bcd2str(ImplEmv.getTlv(0x9F12));
                System.out.println("namecard :" + nameCard);
                System.out.println("cardLabel :" + cardLabel);
                System.out.println("preferedName :" + preferedName);

              {
                    if (nameCard.length() > 6) {
                        NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                    }
                    if (cardLabel.length() > 4) {
                        CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                    }
                    if (preferedName.length() > 0) {
                        PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                    }
                }



                String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
                invoiceGB = calNumTraceNo(invoiceNumber);
                /*
                Log.d("onRequestOnline","TRACK2:"+TRACK2);
                String track2 = TRACK2.substring(2, TRACK2.length());
                Log.d("onRequestOnline","track2:"+track2);
                String cardno;
                cardno = checkCardno(track2);
                Log.d("onRequestOnline","CARD_NO:"+cardno);
                CARD_NO = cardno;
                BIN_TYPE = CardPrefix.getTypeCard(CARD_NO);*/

            }
            else if(cardType == RFCARD) {
                ByteArray tk2 = new ByteArray();
                ByteArray byteArray = new ByteArray();
                String s = "";
                String nameCard = "";
                String cardLabel = "";
                String preferedName = "";
                String RID = "";
                String Tag9F06 = "";
                String Tag5F30 = "";
                String tcHex = "";
                String Tag9F10 = "";
                String CTQ = "";



                int code = RetCode.EMV_OK;


                switch(ClssEntryPoint.getInstance().getOutParam().ucKernType) {
                    case KernType.KERNTYPE_MC:
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x57}, (byte) 1, 19, tk2);
                        s = bcd2str(tk2.data, byteArray.length);

                        TRACK2 = "57" + Integer.toHexString(tk2.length) + s;
                        s = "57" + Integer.toHexString(tk2.length) + s;
                        card = analyse(s);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x4F}, (byte) 1, 16, byteArray);
                        s = bcd2str(byteArray.data, byteArray.length);


                        AID = "4F" + "0" + Integer.toHexString(byteArray.length) + s;
                        AID = AID.substring(4,AID.length());
                        RID = AID.substring(0, 10);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[] {0x5F, 0x34}, (byte)2, 1, byteArray);
                        s = bcd2str(byteArray.data);
                        if(byteArray.length <= 15) {
                            s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                        }
                        else {
                            s = "5F34" + Integer.toHexString(byteArray.length) + s;
                        }
                        Log.d("kang","mc 0x5F34 data : " + s);
                        tagPanSnEMV = s.substring(6,8);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x5F, 0x20}, (byte)2, 10, byteArray);
                        if(byteArray.length < 16)
                            nameCard = "5F20" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data, byteArray.length);
                        else
                            nameCard = "5F20" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data, byteArray.length);
                        //nameCard = "5F20" + Integer.toHexString(byteArray.length)  + bcd2str(byteArray.data, byteArray.length);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x50}, (byte)1, 10, byteArray);
                        cardLabel = "50" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data, byteArray.length);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, 0x12}, (byte)2, 10, byteArray);
                        preferedName = "9F12" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data, byteArray.length);
                        byteArray = new ByteArray();

                        byte[] arr;
                        int abc;
                        abc = ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, (byte)0x06}, (byte)2, 10, byteArray);
                        System.out.println("abc : "+abc);

                        System.out.println("9f06_byte : "+bcd2str(byteArray.data));
                        arr = new byte[byteArray.length];
                        System.arraycopy(byteArray.data, 0, arr, 0, byteArray.length);
                        System.out.println("9f06 print : "+bcd2str(byteArray.data));
                        //Tag9F06 = "9F06" + bcd2str(ImplEmv.intToByteArray(byteArray.length)) + bcd2str(byteArray.data, byteArray.length);

                        abc = ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, (byte)0x06}, (byte)2, 16, byteArray);
                        System.out.println("abc : "+abc);

                        System.out.println("9f06_byte : "+bcd2str(byteArray.data));
                        arr = new byte[byteArray.length];
                        System.arraycopy(byteArray.data, 0, arr, 0, byteArray.length);
                        System.out.println("9f06 print : "+bcd2str(byteArray.data));






                        if(byteArray.length >= 16) {
                            Tag9F06 = "9F06" + Integer.toHexString(byteArray.length) + bcd2Str(arr).substring(0, byteArray.length * 2);
                        }
                        else {
                            Tag9F06 = "9F06" + "0" + Integer.toHexString(byteArray.length) + bcd2Str(arr).substring(0, byteArray.length * 2);
                        }
                        System.out.println("9F06 : " + Tag9F06);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x5F, 0x30}, (byte)2, 2, byteArray);
                        Tag5F30 = "5F30" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        System.out.println("5F30 : " + Tag5F30);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, 0x26}, (byte)2, 8, byteArray);
                        tcHex = "9F26" + "0"  + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        System.out.println("9F26 : " + tcHex);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, 0x10}, (byte)2, 32, byteArray);
                        Tag9F10 = "9F10" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        System.out.println("9F10 : " + Tag9F10);
                        byteArray = new ByteArray();
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, 0x6C}, (byte)2, 60, byteArray);
                        CTQ = "9F6C" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        System.out.println("9F6C : " + CTQ);

                        byteArray=new ByteArray();
                        System.out.println("start:"+bcd2str(intToByteArray(0x9F26)));
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, 0x26 }, (byte)2, 60, byteArray);
                        System.out.println(bcd2str(byteArray.data));
                        ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)0x9F, 0x26 }, (byte)2, 60, byteArray);
                        System.out.println(bcd2str(byteArray.data));
                        ArrayList<Integer> list = ImplEmv.getF55Taglist_int();

                        ByteArrayOutputStream bo = new ByteArrayOutputStream();
                        try {
                            for(int i = 0; i < list.size(); i++) {
                                int a = list.get(i);
                                byteArray=new ByteArray();
                                boolean flag = true;
                                System.out.println("list:"+Integer.toHexString(a)+",tobyte:"+bcd2str(intToByteArray(a)));
                                if(Integer.toHexString(a).length()>2) {
                                    System.out.println(Integer.toHexString(a).substring(0,2)+","+Integer.toHexString(a).substring(2));
                                    if(ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)Integer.parseInt(Integer.toHexString(a).substring(0,2), 16),
                                            (byte)Integer.parseInt(Integer.toHexString(a).substring(2), 16)}, (byte)2, 60, byteArray) != RetCode.EMV_OK) {
                                        flag = false;
                                    }
                                }
                                else {
                                    System.out.println(Integer.toHexString(a));
                                    if(ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)Integer.parseInt(Integer.toHexString(a), 16)}, (byte)1, 60, byteArray) != RetCode.EMV_OK) {
                                        flag = false;
                                    }

                                }
                                if(flag) {
                                    byte[] data = Arrays.copyOfRange(byteArray.data, 0, byteArray.length);
                                    bo.write(ImplEmv.intToByteArray(a));
                                    bo.write(ImplEmv.intToByteArray(data.length));
                                    bo.write(data, 0, data.length);
                                    System.out.println("(success)i:"+i+",tag:"+Integer.toHexString(a)+",hexlength:"+Integer.toHexString(byteArray.length)+",data:"+bcd2str(byteArray.data, byteArray.length));
                                }
                                //System.out.println("i:"+i+",tag:"+Integer.toHexString(a)+",hexlength:"+Integer.toHexString(byteArray.length)+",data:"+bcd2str(byteArray.data, byteArray.length));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        System.out.println("getF55Taglist : " + bcd2str(getF55Taglist()));
                        System.out.println("getF55Taglist_size : " + getF55Taglist().length);
                        //ClssPassApi.Clss_GetTLVDataList_MC(bo.toByteArray(), (byte)getF55Taglist().length, 60, byteArray);

                        DE55 = bcd2str(bo.toByteArray());
                        System.out.println("master conless:" + DE55);
                        DE55 = BlockCalculateUtil.get55Data(DE55, tagOf55List);
                        if(RID.equals("A000000333")){
                            //UPI for chinese language
                            try {
                                if (nameCard.length() > 6) {
                                    byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                                    NAMECARDHOLDER = new String(name_array, "gbk");
                                }


                                if (cardLabel.length() > 4) {
                                    byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                                    CARDLABEL = new String(name_array2, "gbk");
                                }

                                if (preferedName.length() > 0) {
                                    byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                                    PREFEREDNAME = new String(name_array3, "gbk");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else{
                            if (nameCard.length() > 6) {
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                            }

                            if (cardLabel.length() > 4) {
                                CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                            }

                            if (preferedName.length() > 0) {
                                PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                            }
                        }
                        break;

                    case KernType.KERNTYPE_VIS:
                         ClssWaveApi.Clss_GetTLVData_Wave((short) 0x57, tk2);

                        s = bcd2str(tk2.data);
                        s = "57" + Integer.toHexString(tk2.length) + s;
                        Log.d("kang","visa 0x57 data : " + s);
                        card = analyse(s);
                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x5F34  ,byteArray);
                        Log.d("kang", "wave_5f34 length : " + byteArray.length);
                        Log.d("kang", "wave_5f34 data : " + bcd2Str(byteArray.data));
                        s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        Log.d("kang","before add tag 5F34:" + s);
                        if(byteArray.length <= 15) {
                            s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                        }
                        else {
                            s = "5F34" + Integer.toHexString(byteArray.length) + s;
                        }
                        Log.d("kang","visa 0x5F34 data : " + s);
                        tagPanSnEMV = s.substring(6,8);
                        byteArray = new ByteArray();

                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x4F,byteArray);

                        AID = "4F" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0,byteArray.length * 2);
                        Log.d("kang","4f/length:" + Integer.toHexString(byteArray.length) + ",data:" + bcd2str(byteArray.data));
                        AID = AID.substring(4,AID.length());
                        RID = AID.substring(0, 10);
                        //byteArray = new ByteArray();
                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x5F20, byteArray);

                        Log.d("kang", "onlineRequest/wave/|bcd2str:" + bcd2str(byteArray.data, byteArray.length));
                        Log.d("kang", "onlineRequest/wave/|bcd2Str:" + bcd2Str(byteArray.data));
                        if(byteArray.length < 16)
                            nameCard = "5F20" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        else
                            nameCard = "5F20" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        //byteArray = new ByteArray();
                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x50, byteArray);
                        Log.d("kang","visa/50/data:" + bcd2str(byteArray.data).substring(0, byteArray.length * 2) + ", length:" + Integer.toHexString(byteArray.length));
                        cardLabel = "50" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        //byteArray = new ByteArray();
                        if(PREFEREDNAME.equals("") || PREFEREDNAME == null) {
                            ClssWaveApi.Clss_GetTLVData_Wave((short)0x9f12, byteArray);
                            Log.d("kang","visa/9f12/data:" + ", length:" + Integer.toHexString(byteArray.length) + ",data:" + bcd2str(byteArray.data).substring(0, byteArray.length * 2));
                            //preferedName = "9F12" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            int b = ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9f12, byteArray);
                            Log.d("kang", "9f12 get data : " + b);
                        }


                        //preferedName = PREFEREDNAME;
                        //byteArray = new ByteArray();
                        //ClssWaveApi.Clss_GetTLVData_Wave((short)0x9f06, byteArray);
                        if((code = ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9f06, byteArray)) != RetCode.EMV_OK) {
                            Log.d("kang", "fail/visa/9F06/code:" + code);
                            Tag9F06 = "9F06A0000000000000000000000000000000";
                        } else {
                            if(byteArray.length >= 16) {
                                Tag9F06 = "9F06" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            }
                            else {
                                Tag9F06 = "9F06" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            }

                        }

                        //
                        Log.d("kang","visa/9f06/data:" + ", length:" + Integer.toHexString(byteArray.length) + ",data:" + bcd2str(byteArray.data).substring(0, byteArray.length * 2));
                        //byteArray = new ByteArray();
                        int c = ClssWaveApi.Clss_GetTLVData_Wave((short) 0x5f30, byteArray);
                        Log.d("kang", "5f30 get data : " + c);


                        if((code = ClssWaveApi.Clss_GetTLVData_Wave((short) 0x5f30, byteArray)) != RetCode.EMV_OK) {
                            Log.d("kang", "fail/visa/5f30/code:" + code);
                            Tag5F30 = "5F30020000";
                        } else {
                            Tag5F30 = "5F30" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }

                        Log.d("kang","visa/5f30/data:" + ", length:" + Integer.toHexString(byteArray.length) + ",data:" + bcd2str(byteArray.data).substring(0, byteArray.length * 2));
                        //
                        //byteArray = new ByteArray();
                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x9f26, byteArray);
                        Log.d("kang","visa/9f26/data:" + ", length:" + Integer.toHexString(byteArray.length) + ",data:" + bcd2str(byteArray.data).substring(0, byteArray.length * 2));
                        tcHex = "9F26" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        //byteArray = new ByteArray();
                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x9f10, byteArray);
                        Log.d("kang","visa/9f10/data:" + ", length:" + Integer.toHexString(byteArray.length) + ",data:" + bcd2str(byteArray.data).substring(0, byteArray.length * 2));
                        Tag9F10 = "9F10" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        //byteArray = new ByteArray();
                        ClssWaveApi.Clss_GetTLVData_Wave((short)0x9f6c, byteArray);
                        Log.d("kang","visa/9f6c/data:" + ", length:" + Integer.toHexString(byteArray.length) + ",data:" + bcd2str(byteArray.data).substring(0, byteArray.length * 2));
                        CTQ = "9F6C" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);

                        ArrayList<Integer> arrayList = ImplEmv.getF55Taglist_int();

                        bo = new ByteArrayOutputStream();
                        try {
                            for(int i = 0; i < arrayList.size(); i++) {
                                int a = arrayList.get(i);
                                byteArray = new ByteArray();
                                byte[] data;
                                boolean flag = true;


                                if((code = (ClssWaveApi.Clss_GetTLVData_Wave((short)a, byteArray))) == RetCode.EMV_OK) {

                                    data = new byte[byteArray.length];
                                    System.arraycopy(byteArray.data, 0, data, 0, byteArray.length);
                                    bo.write(ImplEmv.intToByteArray(a));
                                    bo.write(ImplEmv.intToByteArray(data.length));
                                    bo.write(data, 0, data.length);
                                    Log.d("kang","making tag55list success/tag:" + Integer.toHexString(a) + ",length:" + data.length + ",data:" + bcd2str(data));
                                }
                                else {
                                    Log.d("kang","making tag55list fail/tag:" + Integer.toHexString(a) + ",code:" + code);
                                }

                               /*
                                code = (ClssWaveApi.Clss_GetTLVData_Wave((short)0x9F41, byteArray));
                                byte[] cc = new byte[byteArray.length];
                                System.arraycopy(byteArray.data, 0, cc, 0, byteArray.length);
                                String ss = bcd2Str(cc);

                                Log.d("kang","9f41/length:" + byteArray.length + ",data:" + bcd2str(byteArray.data) + ",code:" + code);
                                Log.d("kang","9f41(ss)/length:" + byteArray.length + ",data:" + ss + ",code:" + code);
                                */

                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        String tag = bcd2str(bo.toByteArray());
                        Log.d("kang","tag:" +  tag);
                        DE55 = BlockCalculateUtil.get55Data(tag, tagOf55List);
                        System.out.println("visa conless:" + DE55);
                        if(RID.equals("A000000333")){
                            //UPI for chinese language
                            try {
                                if (nameCard.length() > 6) {
                                    byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                                    NAMECARDHOLDER = new String(name_array, "gbk");
                                }


                                if (cardLabel.length() > 4) {
                                    byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                                    CARDLABEL = new String(name_array2, "gbk");
                                }

                                if (preferedName.length() > 0) {
                                    byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                                    PREFEREDNAME = new String(name_array3, "gbk");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            if (nameCard.length() > 6) {
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                            }

                            if (cardLabel.length() > 4) {
                                CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                            }

                            if (preferedName.length() > 0) {
                                PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                            }
                        }

                         break;
                    case KernType.KERNTYPE_PBOC:
                        if(ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC) {
                            tk2.data = ImplEmv.getTlv(0x57);
                            tk2.length = tk2.data.length;
                        } else {
                            ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x57, tk2);
                        }
                        s = bcd2str(tk2.data);
                        s = "57" + Integer.toHexString(tk2.length) + s;
                        Log.d("kang","pboc 0x57 data : " + s);
                        card = analyse(s);
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x5f34 ,byteArray);
                        s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        if(byteArray.length <= 15) {
                            s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                        }
                        else {
                            s = "5F34" + Integer.toHexString(byteArray.length) + s;
                        }
                        Log.d("kang","pboc 0x5F34 data : " + s);
                        tagPanSnEMV = s.substring(6,8);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x4F,byteArray);

                        if(byteArray.length <16) {
                            AID = "4F" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }
                        else {
                            AID = "4F" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }
                        AID = AID.substring(4,AID.length());
                        RID = AID.substring(0, 10);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x5f20, byteArray);
                        if(byteArray.length < 16)
                            nameCard = "5F20" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        else
                            nameCard = "5F20" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x50, byteArray);
                        cardLabel = "50" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x9f12, byteArray);
                        if(byteArray.length < 16) {
                            preferedName = "9F12" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }
                        else {
                            preferedName = "9F12" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x9f06, byteArray);
                        if(byteArray.length < 16) {
                            Tag9F06 = "9F06" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }
                        else {
                            Tag9F06 = "9F06" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x5f30, byteArray);
                        Tag5F30 = "5F30" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x9f26, byteArray);
                        tcHex = "9F26" + "0" +  Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x9f10, byteArray);
                        Tag9F10 = "9F10" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        ClssPbocApi.Clss_GetTLVData_Pboc((short)0x9f6c, byteArray);
                        CTQ = "9F6C" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);


                        arrayList = ImplEmv.getF55Taglist_int();
                        bo = new ByteArrayOutputStream();
                        try {
                            for(int i = 0; i < arrayList.size(); i++) {
                                int a = arrayList.get(i);
                                byte[] data;
                                byteArray = new ByteArray();
                                if((code = ClssPbocApi.Clss_GetTLVData_Pboc((short)a, byteArray)) == RetCode.EMV_OK) {
                                    data = new byte[byteArray.length];
                                    System.arraycopy(byteArray.data, 0, data, 0, byteArray.length);
                                    bo.write(ImplEmv.intToByteArray(a));
                                    bo.write(ImplEmv.intToByteArray(data.length));
                                    bo.write(data);
                                }
                                else {
                                    Log.d("kang","making tag55list fail/tag:" + Integer.toHexString(a) + ",code:" + code);
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        tag = bcd2str(bo.toByteArray());
                        DE55 = BlockCalculateUtil.get55Data(tag, tagOf55List);
                        System.out.println("pboc conless:" + DE55);

                        if(RID.equals("A000000333")){
                            //UPI for chinese language
                            try {
                                if (nameCard.length() > 6) {
                                    byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                                    NAMECARDHOLDER = new String(name_array, "gbk");
                                }


                                if (cardLabel.length() > 4) {
                                    byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                                    CARDLABEL = new String(name_array2, "gbk");
                                }

                                if (preferedName.length() > 0) {
                                    byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                                    PREFEREDNAME = new String(name_array3, "gbk");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else{
                            if (nameCard.length() > 6) {
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                            }

                            if (cardLabel.length() > 4) {
                                CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                            }

                            if (preferedName.length() > 0) {
                                PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                            }
                        }
                        break;
                    case KernType.KERNTYPE_JCB:
                        if(ClssJSpeedy.getInstance().getTransPath() == TransactionPath.CLSS_JCB_MAG) {
                            int ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] { (byte)0x9F, (byte)0x6B}, (byte)1, 60, tk2);
                            if(ret != RetCode.EMV_OK) {
                                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x57}, (byte)1, 19, tk2);
                            }
                        }
                        s = bcd2str(tk2.data).substring(0, tk2.length * 2);
                        s = "57" + Integer.toHexString(tk2.length) + s;
                        Log.d("kang","mc 0x57 data : " + s);
                        card = analyse(s);
                        ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[] {0x5F, 0x34}, (byte)2, 10, byteArray);
                        s = bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        if(byteArray.length <= 15) {
                            s = "5F34" + "0" + Integer.toHexString(byteArray.length) + s;
                        }
                        else {
                            s = "5F34" + Integer.toHexString(byteArray.length) + s;
                        }
                        Log.d("kang","mc 0x5F34 data : " + s);
                        tagPanSnEMV = s.substring(6,8);
                        byteArray = new ByteArray();
                        int ret = 0;
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB((new byte[] {(byte)0x57}), (byte)1, 19, byteArray)) == RetCode.EMV_OK) {
                            AID = "4F" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            AID = AID.substring(4,AID.length());
                            RID = AID.substring(0, 10);
                        }


                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x5F, (byte)0x20}, (byte)2, 26, byteArray)) == RetCode.EMV_OK) {
                            if(byteArray.length >=  16)
                                nameCard = "5F20" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            else
                                nameCard = "5F20" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        }
                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x50}, (byte)1, 16, byteArray)) == RetCode.EMV_OK)
                        cardLabel = "50" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x9F, (byte)0x12}, (byte)2, 16, byteArray)) == RetCode.EMV_OK) {
                            if (byteArray.length < 16) {
                                preferedName = "9F12" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            } else {
                                preferedName = "9F12" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            }
                        }
                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x9F, (byte)0x06}, (byte)2, 16, byteArray)) == RetCode.EMV_OK) {
                            if (byteArray.length < 16) {
                                Tag9F06 = "9F06" + "0" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            } else {
                                Tag9F06 = "9F06" + Integer.toHexString(byteArray.length) + bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                            }
                        }
                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x5F, (byte)0x30}, (byte)2, 2, byteArray)) == RetCode.EMV_OK)
                        Tag5F30 = "5F30" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x9F, (byte)0x26}, (byte)2, 8, byteArray)) == RetCode.EMV_OK)
                        tcHex = "9F26" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x9F, (byte)0x10}, (byte)2, 32, byteArray)) == RetCode.EMV_OK)
                        Tag9F10 = "9F10" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);
                        byteArray = new ByteArray();
                        if((ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0x9F, (byte)0x6C}, (byte)2, 60, byteArray)) == RetCode.EMV_OK)
                        CTQ = "9F6C" + "0" + Integer.toHexString(byteArray.length)+ bcd2str(byteArray.data).substring(0, byteArray.length * 2);

                        list = ImplEmv.getF55Taglist_int();

                        bo = new ByteArrayOutputStream();
                        try {
                            for(int i = 0; i < list.size(); i++) {
                                int a = list.get(i);
                                byteArray = new ByteArray();
                                boolean flag = true;
                                byte[] data;

                                if((code = ClssJCBApi.Clss_GetTLVDataList_JCB(ImplEmv.intToByteArray(a), (byte)2, 60, byteArray)) == RetCode.EMV_OK) {
                                    data = new byte[byteArray.length];
                                    System.arraycopy(byteArray.data, 0, data, 0, byteArray.length);
                                    bo.write(ImplEmv.intToByteArray(a));
                                    bo.write(ImplEmv.intToByteArray(data.length));
                                    bo.write(data, 0, data.length);
                                    Log.d("kang","making tag 55 jcb tag:" + Integer.toHexString(a) + ",length:" + data.length + ",data:" + bcd2Str(data));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tag = bcd2str(bo.toByteArray());
                        DE55 = BlockCalculateUtil.get55Data(tag, tagOf55List);
                        System.out.println("pboc conless:" + DE55);

                        if(RID.equals("A000000333")){
                            //UPI for chinese language
                            try {
                                if (nameCard.length() > 6) {
                                    byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                                    NAMECARDHOLDER = new String(name_array, "gbk");
                                }


                                if (cardLabel.length() > 4) {
                                    byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                                    CARDLABEL = new String(name_array2, "gbk");
                                }

                                if (preferedName.length() > 0) {
                                    byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                                    PREFEREDNAME = new String(name_array3, "gbk");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else{
                            if (nameCard.length() > 6) {
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                            }

                            if (cardLabel.length() > 4) {
                                CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                            }

                            if (preferedName.length() > 0) {
                                PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                            }
                        }
                        break;
                    default:
                        break;

                }

                EXPIRY = card.getExpireDate();
                BIN_TYPE = CardPrefix.getTypeCard(card.getNo());

                System.out.println("secont rf print");
                System.out.println("card:" + card.toString());
                System.out.println("BIN_TYPE:" + BIN_TYPE);
                System.out.println("tagPanSnEMV:" + tagPanSnEMV);
                System.out.println("AID:" + AID);
                System.out.println("RID:" + RID);
                System.out.println("namecard:" + nameCard);
                System.out.println("preferedName:" + preferedName);
                System.out.println("cardLabel:" + cardLabel);
                System.out.println("NAMECARDHOLDER:" + NAMECARDHOLDER);
                System.out.println("CARDLABEL:" + CARDLABEL);
                System.out.println("PREFEREDNAME:" + PREFEREDNAME);





                String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
                invoiceGB = calNumTraceNo(invoiceNumber);

                Log.d("onRequestOnline","tempSavedTrackData:"+tempSavedTrackData);
                TRACK2 = BlockCalculateUtil.get35Data(tempSavedTrackData);
                Log.d("onRequestOnline","TRACK2:"+TRACK2);
                String track2 = TRACK2.substring(2, TRACK2.length());
                Log.d("onRequestOnline","track2:"+track2);
                String cardno;
                cardno = checkCardno(track2);
                Log.d("onRequestOnline","CARD_NO:"+cardno);
                CARD_NO = cardno;
                BIN_TYPE = CardPrefix.getTypeCard(CARD_NO);


                if(tagPanSnEMV != null && !tagPanSnEMV.equals("")) {
                    mBlockDataSend[23 - 1] = "00" + tagPanSnEMV;
                }

                if (tcHex.length() > 6) {
                    TC = tcHex.substring(6, tcHex.length());
                }

                Log.d(TAG, "----> CardInfoData ==> Tc " + TC);
                Log.d(TAG, "----> CardInfoData ==> tcHex " + tcHex);

                Log.d(TAG, "onRequestOnline: " + Tag5F30);
                DE55 = DE55 + Tag9F06 + tcHex; //Jeff20181029

                //check pin, online pin or offline pin
                if(!CTQ.equals("")){
                    if(CTQ.equals("00"))
                        SIGNATURE = false;
                }


                if(!Tag9F10.equals("")){
                    int FlagPin = Integer.parseInt(Tag9F10.substring(14, 16), 16) & 0x04; //Check CVR Byte2 Bit3
                    if (FlagPin == 0) {
                        if(!PINBLOCK.equals("")) {
                            mBlockDataSend[52 - 1] = PINBLOCK;
                        }
                        SIGNATURE = false; //In case of support online pin, no need signature (K.Charbaby)
                    }
                }
            }

            if(cardType == ICCARD || cardType == RFCARD) {
                DE55 = BlockCalculateUtil.checkMessage(DE55);
                //int length_of_tag55 = BlockCalculateUtil.get55Data(readKernelData(EMVTAGS.getF55Taglist()), tagOf55List).length() / 2;
                int length_of_tag55 = DE55.length() / 2;


                mBlockDataSend[55 - 1] = BlockCalculateUtil.get55Length(length_of_tag55) + DE55;
//                        mBlockDataSend[55 - 1] = "016582027D008408A000000333010101950504000480009A031805229C01005F2A0207645F300202205F3401019F02060000000001039F03060000000000009F0608A0000003330101019F090200209F101307000103A0A000010A010000000000098918999F1A0207649F1E0838353130494343009F26083F454A922877F55A9F2701809F3303E0F8C89F34034203009F3501229F360200C99F370430F1E5DD9F410400000054";
                Log.d(TAG, "block 55 = " + mBlockDataSend[55 - 1]);
                //EXPIRY = BlockCalculateUtil.getExpireData(mBlockDataSend[35-1]);
                MBLOCK55 = mBlockDataSend[55 - 1];
            }
            onLineNow = true;
            processCallback(PROCESS_TRANSACTION_STARTING);
            TPDU = CardPrefix.getTPDU(context, "EPS");
//                        if (HOST_CARD.equalsIgnoreCase("EPS")) {
//                            setDataSalePINEPS();
//                        }
            packageAndSend(TPDU, MTI, mBlockDataSend);

        }else if (operateId == UPLOAD) {
            mBlockDataSend[55 - 1] = "0101" + BlockCalculateUtil.get55Data(bcd2Str(ImplEmv.getTlv(byteArrayToInt(ImplEmv.getF55Taglist()))), tagOf55List);
            //packageAndSend(TPDU,MESSAGE_UPLOAD,mBlockDataSend);
        } else if (operateId == TC_ADVICE) {
            mBlockDataSend[55 - 1] = "0101" + BlockCalculateUtil.get55Data(tempSavedAllData, tagOf55List);
            //packageAndSend(TPDU,MESSAGE_TC_ADVICE,mBlockDataSend);
        }


        processCallback(PROCESS_REQUEST_ONLINE);

    }


    public String getHostCard() {
        return HOST_CARD;
    }

    public int getHostFlag() {
        return HOST_FLAG;
    }

//    private void readCardNo() {
//        try {
//            /* 此处代码用来读取非接卡卡号,当快速支付交易接受或者拒绝等，或者快速支付要求连接时均可读取到 */
//            byte[] buffer = new byte[512];
//
//            int res = pboc2.readKernelData(
//                    new byte[] { 0x57, 0x00, 0x00, 0x00 }, buffer);
//
//            if (res >= 0) {
//                TlvData data1 = new TlvData(HexUtil.bcd2str(buffer), true);
//
//                String str = data1.getValueByTag("57");
//                CARD_NO = str.substring(0, str.indexOf("D"));
//                EXPIRY =  str.substring(str.indexOf("D")+1, str.indexOf("D")+5);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public String getMagneticCardNumber() {
//        //arg0.getCardno();
//        Log.d(TAG, "get magnetic card number");
//        //MAG_TRX_RECV = true;
//        return (card.getNo());
//    }
//
//    public String getMagneticCardHolderName() {
//        String CardHolderName = BlockCalculateUtil.getTheNameFromFirstTrack(new String(TRACK1));
//        Log.d(TAG, "CardHolderName: " + CardHolderName);
//        return (CardHolderName);
//    }
//
//    public void getMagneticCardType() {
//        String cardtype;
//
//        switch (BlockCalculateUtil.getBankCardType(card.getNo())) {
//            case Config.VISA:
//                Log.d(TAG, "VISA");
//                cardtype = "VISA";
//                break;
//            case Config.UNION:
//                Log.d(TAG, "UNION");
//                cardtype = "UNION";
//                break;
//            case Config.MASTER:
//                Log.d(TAG, "MASTER");
//                cardtype = "MASTER";
//                break;
//            default:
//                Log.d(TAG, "UNKNOW");
//                cardtype = "UNKNOW";
//                break;
//        }
//
//        //return(cardtye);
//    }
//
//
//    public String readKernelData(byte[] tag) {
//        //showMessage("CORE DATA READING ... ...");
//        Log.d(TAG, "CORE DATA READING ... ...");
//        String result = "";
//        try {
//            byte[] outputBuffer = new byte[1024];
//            // 5F34 卡序列号
//            int ret = pboc2.readKernelData(tag, outputBuffer);
//            //showMessage("DATA From kernel :" + " [ " + ret
//            //                + " ]" + getString(R.string.pboc_byte));
//            Log.d(TAG, "DATA From kernel :" + " [ " + ret + " ]");
//            if (ret > 0) {
//                //showMessage(getString(R.string.pboc_read_55tag_success));
//                Log.d(TAG, "pboc_read_55tag_success");
//                outputBuffer = Arrays.copyOfRange(outputBuffer, 0, ret);
//                //showMessage(HexUtil.bcd2str(outputBuffer));
//                result = HexUtil.bcd2str(outputBuffer);
//                outputBuffer = new byte[500];
//            } else {
//                //showMessage(getString(R.string.pboc_read_55tag_failed));
//                Log.d(TAG, "pboc_read_55tag_failed");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            //showMessage(getString(R.string.pboc_read_55tag_exception), Color.RED);
//            Log.d(TAG, "pboc_read_55tag_exception");
//        }
//        return result;
//    }
//
//    public void stopTransaction() {
//        Log.d(TAG, "Cancel checkcard");
//        /*try {
//            pboc2.cancelCheckCard();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }*/
//    }
//
//    public void selectMultiApp(int i) {
//        try {
//            boolean b = pboc2.importAidSelectRes(i);
//
//            if (b) {
//                Log.d(TAG, "pboc_request_adiselect_success");
//            } else {
//                Log.d(TAG, "pboc_request_adiselect_failed");
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void CardEventOccured(int type) {
//
//    }

    private void CheckCardCallback(int type) {

    }

    private void processCallback(int status) {
        switch (status) {
            case PROCESS_CONFIRM_CARD_INFO:
                if (OPERATE_ID == SALE) {
                    PROCESSING_CODE = "003000";
                    POSEM = "0051";
//                    POSOC = "00";
//                    NII = CardPrefix.getNii(card.getNo(), context);
                    NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
                    TRACK1 = "";
                    TRACK2 = "";
                    String RID = "";
                    MTI = MESSAGE_SALE;
                    if(getReadType() == EReaderType.ICC) {
                        CARD_NO = card.getNo();
                        EXPIRY = card.getExpireDate();

                        AID = bcd2Str(ImplEmv.getTlv(0x4F));
                        AID = AID.substring(4, AID.length());
                        RID = AID.substring(0, 10);


                        String nameCard = bcd2Str(ImplEmv.getTlv(0x5F20));
                        String cardLabel = bcd2Str(ImplEmv.getTlv(0x50));
                        String preferedName = bcd2Str(ImplEmv.getTlv(0x9F12));

                        nameCard = bcd2Str(ImplEmv.getTlv(0x5F20));
                        cardLabel = bcd2Str(ImplEmv.getTlv(0x50));
                        preferedName = bcd2Str(ImplEmv.getTlv(0x9F12));

                        if (RID.equals("A000000333")) {
                            //UPI for chinese language
                            try {
                                if (nameCard != null && nameCard.length() > 6) {
                                    byte name_array[] = HexUtil.hexStringToByte(nameCard.substring(6, nameCard.length()));
                                    NAMECARDHOLDER = new String(name_array, "gbk");
                                }
                                if (cardLabel != null && cardLabel.length() > 4) {
                                    byte name_array2[] = HexUtil.hexStringToByte(cardLabel.substring(4, cardLabel.length()));
                                    CARDLABEL = new String(name_array2, "gbk");
                                }
                                if (preferedName != null && preferedName.length() > 0) {
                                    byte name_array3[] = HexUtil.hexStringToByte(preferedName.substring(6, preferedName.length()));
                                    PREFEREDNAME = new String(name_array3, "gbk");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (nameCard != null && nameCard.length() > 6) {
                                NAMECARDHOLDER = BlockCalculateUtil.hexToString(nameCard.substring(6, nameCard.length()));
                            }
                            if (nameCard != null && cardLabel.length() > 4) {
                                CARDLABEL = BlockCalculateUtil.hexToString(cardLabel.substring(4, cardLabel.length()));
                            }
                            if (preferedName != null && preferedName.length() > 0) {
                                PREFEREDNAME = BlockCalculateUtil.hexToString(preferedName.substring(6, preferedName.length()));
                            }
                        }
                        if (CARDTYPE == ICCARD) {
                            tagPanSnEMV = bcd2Str(ImplEmv.getTlv(0x5F34));
                            mBlockDataSend[23 - 1] = "00" + tagPanSnEMV.substring(6, 8);
                        }
                    }
                    String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
                    invoiceGB = calNumTraceNo(invoiceNumber);



                    String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
                    mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(traceIdNo.isEmpty() ? "1" : traceIdNo));
                    if (cardHelperListener != null) {
                        //cardHelperListener.onCardInfoReceived(card);
                    }
                }else{
                    if (cardHelperListener != null) {
                        cardHelperListener.onCardNo(CARD_NO.substring(4));
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
                if (cardNoConnectHost != null) {
                    cardNoConnectHost.onProcessTransResultUnknow();
                }
                break;
            case PROCESS_MAG_REQUEST_AMOUNT:
                if (cardHelperListener != null) {
                    cardHelperListener.onFindMagCard(card);
                }

                break;
            case PROCESS_REQUEST_TRANSACTION_TIMEOUTED:
                break;
            case PROCESS_REQUEST_CONNECTION_FAILED:
                break;
            case PROCESS_TRANS_RESULT_FALLBACK:
                if (cardHelperListener != null) {
                    cardHelperListener.onTransResultFallBack();
                }
                break;
            case PROCESS_TRANS_RESULT_OTHER :
                if (cardHelperListener != null) {
                    cardHelperListener.onTransResulltNone();
                }
                break;
            case PROCESS_TRANS_RESULT_REFUSE:
                if (cardNoConnectHost != null) {
                    cardNoConnectHost.onProcessTransResultRefuse();
                }
//                try {
//                    pboc2.cancelCheckCard();
//                    pboc2.clearKernelICTransLog();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
                break;
            case PROCESS_ERROR:
                if (cardHelperListener != null) {
                    cardHelperListener.onCardInfoFail();
                }
                break;
        }
    }

    public void setFallBackHappen() {
        FALLBACK_HAPPEN = true;
    }

    public boolean getFallBackHappen() {
        return FALLBACK_HAPPEN;
    }

    //region - Operation method

    private int CONNECTION_TIMEOUT = 5000;

    private ExecutorService sFixedThreadPool;
    private tleinterface tleVersionOne;

    private boolean timeOut = false;

    public final static int PROCESS_REQUEST_CONNECTION_FAILED = 70;
    public final static int PROCESS_REQUEST_TRANSACTION_TIMEOUTED = 71;

    private CustomSocketListener customSocketListener = new CustomSocketListener() {
        @Override
        public void ConnectTimeOut() {
            Log.d(TAG, "ConnectTimeOut: ");
            response_code = "93";
//            processCallback(PROCESS_REQUEST_CONNECTION_FAILED);
            if (connectStatusSocket != null) {
                connectStatusSocket.onConnectTimeOut();
            }
            if (responseCodeListener != null) {
                responseCodeListener.onConnectTimeOut();
            }
            if (testHostLister != null) {
                testHostLister.onConnectTimeOut();
            }
        }

        @Override
        public void TransactionTimeOut() {
            Log.d(TAG, "TransactionTimeOut: ");
            response_code = "91";
//            processCallback(PROCESS_REQUEST_TRANSACTION_TIMEOUTED);
            if (connectStatusSocket != null) {
                connectStatusSocket.onTransactionTimeOut();
            }
            if (responseCodeListener != null) {
                responseCodeListener.onTransactionTimeOut();
            }
            if (testHostLister != null) {
                testHostLister.onTransactionTimeOut();
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
                connectStatusSocket.onError(error);
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

    public void setDataDefaultBatchUpload() {
        batchUploadSize = 0;
        batchUpload = 0;
    }

    public void setDataDefaultUploadCradit() {
        uploadCreditPosition = 0;
        uploadCreditSize = 0;
    }

    /**
     * SET DATASEND HOST
     */

    public void setFalseFallbackHappen() {
        FALLBACK_HAPPEN = false;
    }

    public void setPreAuthFlag(boolean flag) {
        PRE_AUTH_HAPPEN = flag;
    }

    public void setHostTrans(String type) {
        switch(type) {

            case "PURCHASE" :  HOST_FLAG = 0;
                HOST_CARD = "EPS";
                break;
            default:     HOST_FLAG = 0;
                HOST_CARD = "";
                break;
        }
    }

    public void setDataSaleFallBack(String ref1) {

        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        HOST_CARD = CardPrefix.getTypeCard(card.getNo());
        NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
        CARD_NO = card.getNo();
        AMOUNT = ref1;
        Log.d("kang", "ref1:" + ref1);
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(AMOUNT);
        EXPIRY = card.getExpireDate();
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "003000";
        Double amountAll = Double.valueOf(ref1);
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        mBlockDataSend[11 - 1] = calNumTraceNo(traceIdNo);
//        mBlockDataSend[14 - 1] = card.getExpireDate();
        mBlockDataSend[22 - 1] = "0801";
        mBlockDataSend[24 - 1] = NII;
        mBlockDataSend[25 - 1] = "05";
        mBlockDataSend[35 - 1] = TRACK2;
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));
//        mBlockDataSend[52 - 1] = keyPin;
        invoiceGB = calNumTraceNo(invoiceNumber);
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        onLineNow = true;
        TPDU = CardPrefix.getTPDU(context, "EPS");
        packageAndSend(TPDU, MESSAGE_SALE, mBlockDataSend);
    }



    public void setDataVoid(TransTemp temp) {
        TRANCE_NO = "";
        transTemp = temp;

        System.out.printf("utility:: setDataVoid transTemp.getId() = %s \n", transTemp.getId());
        Preference.getInstance(context).setValueInt(Preference.KEY_SET_ID, transTemp.getId());  // Paul_20180810
        int ii = Preference.getInstance(context).getValueInt(Preference.KEY_SET_ID);
        System.out.printf("utility:: setDataVoid transTemp.getId() iiiiiiii = %d \n", ii);

        HOST_CARD = transTemp.getHostTypeCard();
//20180907 reversal ecr invoice
        Ecr_NO = transTemp.getEcr();


//        if(transTemp != null)
//            System.out.printf("utility:: setDataVoid transTemp.getTraceNo() = %s \n",transTemp.getTraceNo());


        System.out.printf("utility:: HOST_CARD = %s \n", HOST_CARD);
        setDataVoidEPS();
    }


    private void setDataVoidEPS() {
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
        if ((transTemp.getCardNo().length() % 2) != 0) {
            mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo() + "0";
            CARD_NO = transTemp.getCardNo();
        } else {
            mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo();
            CARD_NO = transTemp.getCardNo();
        }

        mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
        PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
        Double amountAll = Double.valueOf(transTemp.getAmount()) + Double.valueOf(transTemp.getFee());
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        AMOUNT = transTemp.getAmount();
        mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceId(context, "EPS"));
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
        mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(transTemp.getApprvCode());
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        if (transTemp.getIccData() != null) {
            mBlockDataSend[55 - 1] = transTemp.getIccData();
        }
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.getEcr());
//        onLineNow = true;
        onLineNow = false;// SINN VOID CALL PBOC then hang
        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        packageAndSend(TPDU, MESSAGE_VOID, mBlockDataSend);
    }

    public void setCheckTCUpload(String typeHost, boolean type) {
        typeCheck = type;
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        try {
            HOST_CARD = typeHost;
            if (realm == null) {
                Log.d(TAG, "1919_setCheckTCUpload");
                realm = Realm.getDefaultInstance();
            }

            RealmResults<TCUpload> tcUpload = realm.where(TCUpload.class).equalTo("hostTypeCard", HOST_CARD).equalTo("statusTC", "0").findAll();
            Log.d(TAG, "setCheckTCUpload tcUpload: " + tcUpload.size());
            tcUploadPosition = 0;
            Log.d(TAG, "setCheckTCUpload tcUploadPosition: " + tcUploadPosition);


//                        if ((tcUpload.size() > 0)&& ((tcUpload.get(tcUploadPosition).getIccData() != null))) {   //testing not work cannot check null here.
            if (tcUpload.size() > 0) {
                tcUploadSize = tcUpload.size();
                tcUploadDb = tcUpload.get(tcUploadPosition);
                tcUploadId = tcUploadDb.getTraceNo();
                Log.d(TAG, "setCheckTCUpload: " + tcUploadDb.getTraceNo());
                Log.d(TAG, "setTCUpload: " + HOST_CARD);
                MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
                TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
                mBlockDataSend = new String[64];
//                mBlockDataSend[2 - 1] = tcUpload.get(tcUploadPosition).getCardNo().length() + tcUpload.get(tcUploadPosition).getCardNo();
                if ((tcUpload.get(tcUploadPosition).getCardNo().length() % 2) != 0) {
                    mBlockDataSend[2 - 1] = tcUpload.get(tcUploadPosition).getCardNo().length() + tcUpload.get(tcUploadPosition).getCardNo() + "0";
                } else {
                    mBlockDataSend[2 - 1] = tcUpload.get(tcUploadPosition).getCardNo().length() + tcUpload.get(tcUploadPosition).getCardNo();
                }
                mBlockDataSend[3 - 1] = "943000";
//                double fee = Preference.getInstance(context).getValueDouble( Preference.KEY_FEE);
//                double amountFee1 = (Double.valueOf(tcUpload.get(tcUploadPosition).getAmount()) * fee) / 100;
//                double amountAll = Double.valueOf(tcUpload.get(tcUploadPosition).getAmount()) + amountFee1;
//
                double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
                double amountFee1;
                if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
                    amountFee1 = (Double.valueOf(tcUpload.get(tcUploadPosition).getAmount()) * fee);
                else
                    amountFee1 = (Double.valueOf(tcUpload.get(tcUploadPosition).getAmount()) * fee) / 100;
                amountFee1 = (int) (amountFee1 * 100 + 0.5) / 100.0;             // Paul_20190129
                double amountAll = Double.valueOf(tcUpload.get(tcUploadPosition).getAmount()) + amountFee1;


                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
                mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceId(context, HOST_CARD));
                mBlockDataSend[12 - 1] = tcUpload.get(tcUploadPosition).getTransTime().replace(":", "");
                mBlockDataSend[13 - 1] = tcUpload.get(tcUploadPosition).getTransDate().substring(4, 8);
                mBlockDataSend[14 - 1] = tcUpload.get(tcUploadPosition).getExpiry();
                mBlockDataSend[22 - 1] = tcUpload.get(tcUploadPosition).getPointServiceEntryMode();
                mBlockDataSend[23 - 1] = tcUpload.get(tcUploadPosition).getApplicationPAN();
                if (HOST_CARD.equalsIgnoreCase("POS")) {
//                    mBlockDataSend[24 - 1] = CardPrefix.getNii(tcUpload.get(tcUploadPosition).getCardNo(), context);
                    mBlockDataSend[24 - 1] = CardPrefix.getJSONNii(tcUpload.get(tcUploadPosition).getCardNo(), context);  ////20180807 SINN JSON Card range Config
                } else {
                    mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
                }
                if (tcUpload.get(tcUploadPosition).getIccData() != null) {
                    mBlockDataSend[25 - 1] = "05";
                } else {
                    mBlockDataSend[25 - 1] = "00";
//
//                    //SINN 20181102 TC upload for EMV only
//                    if (realm != null) {
//                        realm.close();
//                        realm = null;
//                    }
//
//                    return;
//                    //END SINN 20181102 TC upload for EMV only
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
                    invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
                } else {
                    invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
                }
                String traceNo = String.valueOf(Integer.valueOf(tcUpload.get(tcUploadPosition).getTraceNo()) - 1);
                String f60 = "0200" + CardPrefix.calLen(traceNo, 6) + BlockCalculateUtil.hexToString(tcUpload.get(tcUploadPosition).getRefNo());
                mBlockDataSend[60 - 1] = CardPrefix.calLen(String.valueOf(f60.length()), 4) + BlockCalculateUtil.getHexString(f60);
                mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                onLineNow = true;
                TPDU = CardPrefix.getTPDU(context, HOST_CARD);
                //SINN 20180925 Add WAY4
                if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
//                String mBlock63 = "0062" + "RF"+ CardPrefix.calSpenLen(REF1, 20) + CardPrefix.calSpenLen(REF2, 20) + CardPrefix.calSpenLen(REF3, 20);
                    String mBlock63 = "RF" + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF1), 20)
                            + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF2), 20)
                            + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF3), 20);
                    mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length() + 2), 4) + CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);
                }
                //END SINN 20180925 Add WAY4

                // Paul_20190122
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        packageAndSend(TPDU, "0320", mBlockDataSend);
                    }
                }, insettimewait);

                tcUploadPosition++;
            } else {
                /*switch (HOST_CARD) {
                    case "EPS":
                        setDataSettlementAndSendEPS();
                        break;
                    case "POS":
                        setDataSettlementAndSend("POS");
                        break;KEY_FixRATE_ID
                    default:
                        setDataSettlementAndSendTMS();
                        break;
                }*/
                // Paul_20181103 WAY 4 + AXA
//                if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
//                    if (!Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")) //KTB NORMAL
//                        setUploadCredit();
//                } else {
//                    if (Preference.getInstance( context ).getValueString( Preference.KEY_KTBNORMAL_ID ).equalsIgnoreCase( "1" )) //KTB NORMAL
//                    {
//                        switch (HOST_CARD) {
//                            case "EPS":
//                                setDataSettlementAndSendEPS();
//                                break;
//                            case "POS":
//                                setDataSettlementAndSend( "POS" );
//                                break;
//                            default:
//                                setDataSettlementAndSendTMS();
//                                break;
//                        }
//                    } else
//                        setUploadCredit();
//                }
//SINN 2018112018  settlement no tc upload  hang
                if (Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")) //KTB NORMAL
                {
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
                } else
                    setUploadCredit();
                //END SINN 2018112018  settlement no tc upload  hang


            }

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

//    public void setDataSettlementAndSend(String typeHost) {
//        HOST_CARD = typeHost;
////        setUploadCredit();
//        String traceIdNo;
//        try {
//            if (realm == null) {
//                realm = Realm.getDefaultInstance();
//            }
//            int timeCount = 0;
//            double amountAll = 0;
//
//            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
//            Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
//
//            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
//            RealmResults<TransTemp> transTempVoidFlag = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
//            if (transTempVoidFlag.size() != 0) {
//                timeCount = transTempVoidFlag.size();
//                for (int i = 0; i < transTempVoidFlag.size(); i++) {
//                    amountAll += Float.valueOf(transTempVoidFlag.get(i).getAmount()) + ((Double.valueOf(transTempVoidFlag.get(i).getAmount()) * fee)/ 100);
//                }
//            }
//            String nii = "";
//            if (typeHost.equalsIgnoreCase("POS")) {
//                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
//            } else if (typeHost.equalsIgnoreCase("EMS")) {
//                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
//            } else if (typeHost.equalsIgnoreCase("TMS")) {
//                nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
//            }
//            String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
//            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
//            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
//            mBlockDataSend = new String[64];
//            mBlockDataSend[3 - 1] = SETTLEMENT_PROCESSING_CODE;
//            PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
//            mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
//            mBlockDataSend[24 - 1] = nii;
//            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
//            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
//            mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
//            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, decimalFormat.format(amountAll));
//            settlement63 = mBlockDataSend[63 - 1];
//            onLineNow = false;
//            TPDU = CardPrefix.getTPDU(context, "EPS");
//            packageAndSend(TPDU, MESSAGE_SETTLEMENT, mBlockDataSend);
//
//        } finally {
//            if (realm != null) {
//                realm.close();
//                realm = null;
//            }
//        }
//    }

    public void setDataSettlementAndSendSALE() {
        HOST_CARD = "EPS";
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
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
            if (transTempVoidYFlag.size() != 0) {
                voidCount = transTempVoidYFlag.size();
                for (int i = 0; i < transTempVoidYFlag.size(); i++) {
                    amountVoidAll += Float.valueOf(transTempVoidYFlag.get(i).getAmount());
                }
            }
            String nii = "";
            nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            String batchNumber = CardPrefix.getBatch(context, HOST_CARD);

            int countPayAll = (payCount + voidCount);

            String msgLen = "00000121";
            String terVer = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
            String msgVer = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
            String tranCode = "6012";
            String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
            String transaction = CardPrefix.calLen(String.valueOf((countPayAll + voidCount)), 5);
//            String totalPayCount = CardPrefix.calLen(String.valueOf(payCount), 5);
            String totalPayCount = CardPrefix.calLen(String.valueOf(countPayAll), 5);

//            String amountPayAllToStr = String.format("%.2f", amountAll).replace(".", "");
            String amountPayAllToStr = decimalFormat.format(amountAll + amountVoidAll).replace(".", "");

            String totalPayAmount = CardPrefix.calLen(String.valueOf(amountPayAllToStr), 10);

//            String totalVoidCount = CardPrefix.calLen("", 5);
            String totalVoidCount = CardPrefix.calLen(String.valueOf(voidCount), 5);

            String amountVoidAllToStr = decimalFormat.format(amountVoidAll).replace(".", "");

            String totalVoidAmount = CardPrefix.calLen(String.valueOf(amountVoidAllToStr), 10);

//            String totalVoidAmount = CardPrefix.calLen("", 10);
            String totalRefundCount = CardPrefix.calLen("", 5);
            String totalRefundAmount = CardPrefix.calLen("", 10);
            String randomData = CardPrefix.calSpenLen("", 5);
            String terminalCERT = CardPrefix.calSpenLen("", 14);
            String checkSum = CardPrefix.calSpenLen("", 8);

            mBlockDataSend = new String[64];
            mBlockDataSend[3 - 1] = SETTLEMENT_PROCESSING_CODE;
            mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
            mBlockDataSend[24 - 1] = nii;
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));
            mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
            String mBlock61 = msgLen + terVer + msgVer + tranCode + batchNo +
                    transaction + totalPayCount + totalPayAmount + totalVoidCount +
                    totalVoidAmount + totalRefundCount + totalRefundAmount + randomData +
                    terminalCERT + checkSum;
            mBlockDataSend[61 - 1] = CardPrefix.calLen(mBlock61.length() + "", 4) + BlockCalculateUtil.getHexString(mBlock61);
            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(payCount, amountPayAllToStr);
            Log.d(TAG, "setDataSettlementAndSendTMS: " + mBlockDataSend[63 - 1]);
            settlement61 = mBlockDataSend[61 - 1];
            settlement63 = mBlockDataSend[63 - 1];
            MTI = MESSAGE_SETTLEMENT;
            onLineNow = false;
            TPDU = CardPrefix.getTPDU(context, "EPS");
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
//            mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
            if ((reversalTemp.getCardNo().length() % 2) != 0) {
                mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo() + "0";
            } else {
                mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
            }
            if (reversalTemp.getTransStat().equals("SALE")) {
                mBlockDataSend[3 - 1] = REVERSAL_PROCESSING_CODE;
            } else if (reversalTemp.getTransStat().equals("VOID")) {
                mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
            }
            PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
//            Double fee = Preference.getInstance(context).getValueDouble( Preference.KEY_FEE);
//            Double amountFee1 = (Double.valueOf(reversalTemp.getAmount()) * fee) / 100;
//            Double amountAll = Double.valueOf(reversalTemp.getAmount()) + amountFee1;
//
            Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
            Double amountFee1;
            if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
                amountFee1 = (Double.valueOf(reversalTemp.getAmount()) + fee);
            else
                amountFee1 = (Double.valueOf(reversalTemp.getAmount()) * fee) / 100;
            amountFee1 = (int) (amountFee1 * 100 + 0.5) / 100.0;             // Paul_20190129

            Double amountAll = Double.valueOf(reversalTemp.getAmount()) + amountFee1;

            if (!HOST_CARD.equalsIgnoreCase("TMS")) {
                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
            } else {
                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Double.valueOf(reversalTemp.getAmount())));
            }
            mBlockDataSend[11 - 1] = calNumTraceNo(String.valueOf(Integer.valueOf(reversalTemp.getTraceNo()) - 1));
//            mBlockDataSend[14 - 1] = reversalTemp.getExpiry();
            if (reversalTemp.getHostTypeCard().equalsIgnoreCase("TMS")) {
                mBlockDataSend[22 - 1] = "0022";
                mBlockDataSend[25 - 1] = "00";
            } else if (reversalTemp.getHostTypeCard().equalsIgnoreCase("POS")) {
                mBlockDataSend[22 - 1] = "0051";
                if (reversalTemp.getTransStat().equalsIgnoreCase("I")) {
                    mBlockDataSend[25 - 1] = "05";
                } else {
                    mBlockDataSend[25 - 1] = "00";
                }
            } else {
                mBlockDataSend[22 - 1] = "0052";
                if (reversalTemp.getTransStat().equalsIgnoreCase("I")) {
                    mBlockDataSend[25 - 1] = "05";
                } else {
                    mBlockDataSend[25 - 1] = "00";
                }
            }

            if (reversalTemp.getApplicationPAN() != null) {
                mBlockDataSend[23 - 1] = reversalTemp.getApplicationPAN();
            }
            mBlockDataSend[24 - 1] = reversalTemp.getNii();
//            try {

            try{
                String dateTime = reversalTemp.getField63().substring(404, 404 + 28);
                Log.d(TAG, "setDataReversalAndSendHost: " + dateTime + " track 2 " + reversalTemp.getTrack2());


                String track2 = reversalTemp.getTrack2().substring(2, reversalTemp.getTrack2().length());       // Paul_20181101 Eps MSR format error
                System.out.printf("utility:: setDataReversalAndSendHost 000000001 length = %d track2 = %s \n", track2.length(), track2);
                //20190107_Jeff
                if (track2.length() % 2 != 0)
                    mBlockDataSend[35 - 1] = String.valueOf(track2.length()) + Track2MappingTable(track2, BlockCalculateUtil.hexToString(dateTime)) + "0"; // Paul_20190121
//                mBlockDataSend[35 - 1] = String.valueOf(track2.length()) + track2 + "0";
                else
                    mBlockDataSend[35 - 1] = String.valueOf(track2.length() - 1) + Track2MappingTable(track2, BlockCalculateUtil.hexToString(dateTime));
//            mBlockDataSend[35 - 1] = String.valueOf(track2.length()-1) + track2;

                Log.d(TAG, "Reversal : mBlockDataSend[35 - 1] = " + mBlockDataSend[35 - 1] + " \n" +
                        " dateTime = " + BlockCalculateUtil.hexToString(dateTime));
                // Paul_20181025


            }catch (Exception e){
                e.printStackTrace();
            }






//            }catch (Exception e){
//                e.printStackTrace();
//                Log.d(TAG, "setDataReversalAndSendHost: " + e.toString() + " track 2 " + reversalTemp.getTrack2());
//            }

            /*String tr2 =reversalTemp.getTrack2();
            if (reversalTemp.getTransStat().equalsIgnoreCase("SALE")) {
                //mBlockDataSend[35 - 1] = reversalTemp.getTrack2();
                if ((tr2.length() % 2) != 0) {
                    //mBlockDataSend[35 - 1] += "0";
                    tr2+="0";
                }
            }*/


            if (reversalTemp.getTransStat().equals("VOID")) {
                Log.d(TAG, "setDataReversalAndSendHost DE37=" + BlockCalculateUtil.getHexString(reversalTemp.getRefNo()));
                mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(reversalTemp.getRefNo());
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
            Log.d(TAG, "setDataReversalAndSendHost: " + mBlockDataSend[63 - 1]);
        } else {
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);
            onLineNow = false;      // Paul_20181119
            mBlockDataSend = new String[64];
            if (HOST_CARD.equalsIgnoreCase("POS")) {
//                mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
                if ((reversalTemp.getCardNo().length() % 2) != 0) {
                    mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo() + "0";
                } else {
                    mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
                }
            }
            if (reversalTemp.getTransStat().equals("SALE")) {
                mBlockDataSend[3 - 1] = REVERSAL_PROCESSING_CODE;
            } else if (reversalTemp.getTransStat().equals("VOID")) {
                mBlockDataSend[3 - 1] = VOID_PROCESSING_CODE;
                ////20180830 SINN reversal VOID no DE63 and NII 0362
                if (HOST_CARD.equalsIgnoreCase("GHC"))
                    mBlockDataSend[3 - 1] = VOIDHEALTHCARE_PROCESSING_CODE;
            }
            PROCESSING_CODE = mBlockDataSend[3 - 1];    // Paul_20180523
//            Double fee = Preference.getInstance(context).getValueDouble( Preference.KEY_FEE);
//            Double amountFee1 = (Float.valueOf(reversalTemp.getAmount()) * fee) / 100;
//            Double amountAll = Float.valueOf(reversalTemp.getAmount()) + amountFee1;
//
            Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
            Double amountFee1;
            if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
                amountFee1 = (Double.valueOf(reversalTemp.getAmount()) + fee);       // Paul_20190128
            else
                amountFee1 = (Double.valueOf(reversalTemp.getAmount()) * fee) / 100;     // Paul_20190128
            amountFee1 = (int) (amountFee1 * 100 + 0.5) / 100.0;             // Paul_20190129

            Double amountAll = Double.valueOf(reversalTemp.getAmount()) + amountFee1;        // Paul_20190128

            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
//            mBlockDataSend[11 - 1] = reversalTemp.getTraceNo();
            mBlockDataSend[11 - 1] = calNumTraceNo(String.valueOf(Integer.valueOf(reversalTemp.getTraceNo()) - 1));
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
//                mBlockDataSend[35 - 1] = reversalTemp.getTrack2();        // Paul_20181101 Eps MSR format error
                String track2 = reversalTemp.getTrack2().substring(2, reversalTemp.getTrack2().length());       // Paul_20181101 Eps MSR format error
                System.out.printf("utility:: setDataReversalAndSendHost 000000001 length = %d track2 = %s \n", track2.length(), track2);
                //20190107_Jeff
                if (track2.length() % 2 != 0)
                    mBlockDataSend[35 - 1] = String.valueOf(track2.length()) + track2 + "0";
                else
                    mBlockDataSend[35 - 1] = String.valueOf(track2.length() - 1) + track2;

                System.out.printf("utility:: setDataReversalAndSendHost 000000002 mBlockDataSend[35 - 1] = %s \n", mBlockDataSend[35 - 1]);
            }
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);

            //20181011 SINN fix reversal no.de37
//            mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.getRefNo());
            // Paul_20181012
            if (reversalTemp.getTransStat().equals("VOID") && !HOST_CARD.equals("GHC")) {
                Log.d(TAG, "setDataReversalAndSendHost DE37=" + BlockCalculateUtil.getHexString(reversalTemp.getRefNo()));
                mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(reversalTemp.getRefNo());
            }
            if (reversalTemp.getIccData() != null) {
                mBlockDataSend[55 - 1] = reversalTemp.getIccData();
            }
            System.out.printf("utility:: setDataReversalAndSendHost reversalTemp.getEcr().length() = %d\n", reversalTemp.getEcr().length());
            System.out.printf("utility:: setDataReversalAndSendHost %s \n", reversalTemp.getEcr());
            mBlockDataSend[62 - 1] = getLength62(String.valueOf(reversalTemp.getEcr().length())) + BlockCalculateUtil.getHexString(reversalTemp.getEcr());

            //20180830 SINN reversal VOID no DE63 and NII 0362
            if (HOST_CARD.equalsIgnoreCase("GHC")) {
                mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_GHC);
                mBlockDataSend[63 - 1] = reversalTemp.getField63();
                mBlockDataSend[25 - 1] = "05";
                mBlockDataSend[22 - 1] = "0022";
//                mBlockDataSend[11 - 1] = calNumTraceNo(String.valueOf(Integer.valueOf(reversalTemp.getTraceNo())));
                mBlockDataSend[11 - 1] = calNumTraceNo(String.valueOf(Integer.valueOf(reversalTemp.getTraceNo())));
                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Double.valueOf(reversalTemp.getAmount())));

//                String inStr = reversalTemp.getEcr();
//                inStr = String.valueOf(Integer.valueOf(inStr)-1);
//                String szpad ="000000";
//                inStr = szpad.substring(0,6-inStr.length())+inStr;
//                mBlockDataSend[62 - 1] = getLength62(String.valueOf(reversalTemp.getEcr().length())) + BlockCalculateUtil.getHexString(inStr);

                mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(CardPrefix.calLen("0", 12));
                mBlockDataSend[62 - 1] = getLength62(String.valueOf(reversalTemp.getEcr().length())) + BlockCalculateUtil.getHexString(reversalTemp.getEcr());

//                mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
                if ((reversalTemp.getCardNo().length() % 2) != 0) {
                    mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo() + "0";
                } else {
                    mBlockDataSend[2 - 1] = reversalTemp.getCardNo().length() + reversalTemp.getCardNo();
                }
                // mBlockDataSend[2 - 1] ="160003101100015314";

                Log.d(TAG, "mBlockDataSend[2 - 1]=" + mBlockDataSend[2 - 1] + " mBlockDataSend[62 - 1] =" + mBlockDataSend[62 - 1]);

            }
        }

        //-----------------------------------------------------------------------------------------------------
        System.out.printf("utility:: setDataReversalAndSendHost 004 \n");

        TPDU = CardPrefix.getTPDU(context, HOST_CARD);
        System.out.printf("utility:: setDataReversalAndSendHost 005 \n");
// Paul Test
//        deleteReversal();
        onLineNow = false; // Paul_20180601
// Paul_20181108 Start k.hong advice  -- reversal some time success and than errmessage
//        packageAndSend(TPDU, MESSAGE_REVERSAL, mBlockDataSend);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                packageAndSend(TPDU, MESSAGE_REVERSAL, mBlockDataSend);
            }
        }, insettimewait);
// Paul_20181108 End  k.hong advice -- reversal some time success and than errmessage
        System.out.printf("utility:: setDataReversalAndSendHost 006 \n");
    }


    private void setBatchUpload() {
        Log.d(TAG, "setBatchUpload: " + HOST_CARD);
        if (realm == null) {
            Log.d(TAG, "1919_setBatchUpload");
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
        HOST_CARD = "POS";
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
            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
            amountAll += Double.valueOf(transTemp.get(batchUpload).getAmount()); // Paul_20190128
            Log.d(TAG, "batchUploadSize : " + batchUploadSize + " \n batchUpload : " + batchUpload);
            mBlockDataSend = new String[64];
//            mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            if ((transTemp.get(batchUpload).getCardNo().length() % 2) != 0) {
                mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo() + "0";
            } else {
                mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            }
            mBlockDataSend[3 - 1] = "003000";
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Double.valueOf(transTemp.get(batchUpload).getAmount()) + Double.valueOf(transTemp.get(batchUpload).getFee())));
            mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceId(context, HOST_CARD));
            mBlockDataSend[12 - 1] = transTemp.get(batchUpload).getTransTime().replace(":", "");
            mBlockDataSend[13 - 1] = transTemp.get(batchUpload).getTransDate().substring(4, 8);
            mBlockDataSend[14 - 1] = transTemp.get(batchUpload).getExpiry();
            mBlockDataSend[22 - 1] = transTemp.get(batchUpload).getPointServiceEntryMode();
            if (transTemp.get(batchUpload).getApplicationPAN() != null) {
                mBlockDataSend[23 - 1] = transTemp.get(batchUpload).getApplicationPAN();
            }
//            mBlockDataSend[24 - 1] = CardPrefix.getNii(transTemp.get(batchUpload).getCardNo(), context);
            //  mBlockDataSend[24 - 1] = CardPrefix.getJSONNii(transTemp.get(batchUpload).getCardNo(), context);  //20180807 SINN JSON Card range Config

            if (HOST_CARD.equalsIgnoreCase("TMS"))  //20180819  SINN CardPrefix.getJSONNii off
                NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
            else if (HOST_CARD.equalsIgnoreCase("POS"))
                NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
            else if (HOST_CARD.equalsIgnoreCase("EPS"))
                NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            else if (HOST_CARD.equalsIgnoreCase("GHC"))
                NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_GHC);

            mBlockDataSend[24 - 1] = NII;


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

// Paul_20181105 AXA Option
            if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
                //SINN 20180925 Add WAY4
                String REF1 = transTemp.get(batchUpload).getRef1();
                String REF2 = transTemp.get(batchUpload).getRef2();
                String REF3 = transTemp.get(batchUpload).getRef3();
                //SINN 20181019 Add WAY4

//                if (Preference.getInstance( context ).getValueString( Preference.KEY_WAY4_ID ).equalsIgnoreCase( "1" )) {
                if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
////SINNN  20181106 arrange parameter control
                    String mBlock63 = "RF" + CardPrefix.calSpenLen(REF1, 20) + CardPrefix.calSpenLen(REF2, 20) + CardPrefix.calSpenLen(REF3, 20);
                    mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length() + 2), 4) + CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);
                }
                //END SINN 20180925 Add WAY4
            }

            onLineNow = false; // Paul_20180602
//            packageAndSend(TPDU, "0320", mBlockDataSend);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    packageAndSend(TPDU, "0320", mBlockDataSend);
                }
            }, insettimewait);
            batchUpload++;
//            }

        } else {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)));
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
            onLineNow = false; // Paul_20180602
//            packageAndSend(TPDU, "0500", mBlockDataSend);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    packageAndSend(TPDU, "0500", mBlockDataSend);
                }
            }, insettimewait);


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
//        onLineNow = false; // Paul_20180602
//        packageAndSend(TPDU, "0500", mBlockDataSend);
    }

    private void batchOffEPS() {
        HOST_CARD = "EPS";
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
            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_POS, traceIdNo);
            Log.d(TAG, "batchOffEPS batchUpload: " + batchUpload);
            amountAll += Double.valueOf(transTemp.get(batchUpload).getAmount());
            mBlockDataSend = new String[64];
//            mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            if ((transTemp.get(batchUpload).getCardNo().length() % 2) != 0) {
                mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo() + "0";
            } else {
                mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            }
            mBlockDataSend[3 - 1] = "003000";
            Double amount = Double.valueOf(transTemp.get(batchUpload).getAmount());
            Double fee = Double.valueOf(transTemp.get(batchUpload).getFee());
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amount + fee));
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

// Paul_20181105 AXA Option
            if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
                //SINN 20180925 Add WAY4
                String REF1 = transTemp.get(batchUpload).getRef1();
                String REF2 = transTemp.get(batchUpload).getRef2();
                String REF3 = transTemp.get(batchUpload).getRef3();

//                if (Preference.getInstance( context ).getValueString( Preference.KEY_WAY4_ID ).equalsIgnoreCase( "1" )) {
                if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) { //SINNN  20181106 arrange parameter control//                String mBlock63 = "RF" + CardPrefix.calSpenLen(REF1, 20) + CardPrefix.calSpenLen(REF2, 20) + CardPrefix.calSpenLen(REF3, 20);
                    String mBlock63 = "RF" + CardPrefix.calSpenLen(REF1, 20).substring(0, 19) + CardPrefix.calSpenLen(REF2, 20).substring(0, 19) + CardPrefix.calSpenLen(REF3, 20).substring(0, 19);

                    mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length() + 2), 4) + CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);
                }
                //END SINN 20180925 Add WAY4
            }

            onLineNow = false;
            // Paul_20190121
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    packageAndSend(TPDU, "0320", mBlockDataSend);
                }
            }, insettimewait);
            batchUpload++;

        } else {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)));
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
            onLineNow = false; // Paul_20180602
//            packageAndSend(TPDU, "0500", mBlockDataSend);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    packageAndSend(TPDU, "0500", mBlockDataSend);
                }
            }, insettimewait);
        }

    }

    private void batchTMS() {
        HOST_CARD = "TMS";
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
            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            amountAll += Double.valueOf(transTemp.get(batchUpload).getAmount()); // Paul_20190128
            mBlockDataSend = new String[64];
//            mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            if ((transTemp.get(batchUpload).getCardNo().length() % 2) != 0) {
                mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo() + "0";
            } else {
                mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            }
            mBlockDataSend[3 - 1] = "003000";
            Double amount = Double.valueOf(transTemp.get(batchUpload).getAmount());
            String feeIf = transTemp.get(batchUpload).getEmciFree().isEmpty() ? "0.0" : transTemp.get(batchUpload).getEmciFree();
            Double fee = Double.valueOf(feeIf);
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amount + fee));
            mBlockDataSend[11 - 1] = calNumTraceNo(traceIdNo);
            mBlockDataSend[12 - 1] = transTemp.get(batchUpload).getTransTime().replace(":", "");
            mBlockDataSend[13 - 1] = transTemp.get(batchUpload).getTransDate().substring(4, 8);
//                mBlockDataSend[14 - 1] = transTemp.get(i).getExpiry();
            mBlockDataSend[22 - 1] = transTemp.get(batchUpload).getPointServiceEntryMode();

            //sinn 20180912 update json
//            mBlockDataSend[24 - 1] = CardPrefix.getNii(transTemp.get(batchUpload).getCardNo(), context);
            if (HOST_CARD.equalsIgnoreCase("POS"))
                mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
            else if (HOST_CARD.equalsIgnoreCase("EPS"))
                mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            else if (HOST_CARD.equalsIgnoreCase("TMS"))
                mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);


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
            onLineNow = false; // Paul_20180601
//            packageAndSend(TPDU, "0320", mBlockDataSend);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    packageAndSend(TPDU, "0320", mBlockDataSend);
                }
            }, insettimewait);
            batchUpload++;

        } else {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)));
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
            onLineNow = false; // Paul_20180602
//            packageAndSend(TPDU, "0500", mBlockDataSend);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    packageAndSend(TPDU, "0500", mBlockDataSend);
                }
            }, insettimewait);
        }
    }





    public void setDataTestHostPurchase() {
        HOST_CARD = "EPS";
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "990000";
        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_EPS)));
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS));

        TPDU = CardPrefix.getTPDU(context, "EPS");
        packageAndSend(TPDU, "0800", mBlockDataSend);
    }


    private void setUploadCredit() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        try {
            if (realm == null) {
                Log.d(TAG, "1919_setUploadCredit");
                realm = Realm.getDefaultInstance();
            }
            Date date = new Date();
            String invoiceNumber;
            String time = new SimpleDateFormat("HHmmss").format(date);
            String dateTime = new SimpleDateFormat("yyyyMMdd").format(date);
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "TMS");
            TERMINAL_ID = CardPrefix.getTerminalId(context, "TMS");
            RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", HOST_CARD).equalTo("voidFlag", "N").findAll();
            Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);

            uploadCreditSize = transTemp.size();
            if (transTemp.size() > 0) {
//                Double amountFee = (Double.valueOf(transTemp.get(uploadCreditPosition).getAmount()) * fee) / 100;
                Double amountFee;
                if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
                    amountFee = (Double.valueOf(transTemp.get(uploadCreditPosition).getAmount()) + fee);
                else
                    amountFee = (Double.valueOf(transTemp.get(uploadCreditPosition).getAmount()) * fee) / 100;
                amountFee = (int) (amountFee * 100 + 0.5) / 100.0;             // Paul_20190129


                Double amountAll = Double.valueOf(transTemp.get(uploadCreditPosition).getAmount()) + amountFee;
                mBlockDataSend = new String[64];
//                mBlockDataSend[2 - 1] = transTemp.get(uploadCreditPosition).getCardNo().length() + transTemp.get(uploadCreditPosition).getCardNo();
                if ((transTemp.get(uploadCreditPosition).getCardNo().length() % 2) != 0) {
                    mBlockDataSend[2 - 1] = transTemp.get(uploadCreditPosition).getCardNo().length() + transTemp.get(uploadCreditPosition).getCardNo() + "0";
                } else {
                    mBlockDataSend[2 - 1] = transTemp.get(uploadCreditPosition).getCardNo().length() + transTemp.get(uploadCreditPosition).getCardNo();
                }
                mBlockDataSend[3 - 1] = "480000";
                mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
                mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceId(context, "TMS"));
                mBlockDataSend[12 - 1] = time;
                mBlockDataSend[13 - 1] = dateTime.substring(4, 8);
                mBlockDataSend[22 - 1] = transTemp.get(uploadCreditPosition).getPointServiceEntryMode();
                mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
                if (transTemp.get(uploadCreditPosition).getIccData() != null) {
                    mBlockDataSend[25 - 1] = "05";
                } else {
                    mBlockDataSend[25 - 1] = "00";
                }
                /*if (transTemp.get(i) != null && transTemp.get(i).getTrack2() != null) {
                    mBlockDataSend[35 - 1] = Track2MappingTable(transTemp.get(i).getTrack2(),dateTime+time);
                }*/
                mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(transTemp.get(uploadCreditPosition).getRefNo());
                mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(transTemp.get(uploadCreditPosition).getApprvCode());
                mBlockDataSend[39 - 1] = BlockCalculateUtil.getHexString(transTemp.get(uploadCreditPosition).getRespCode());
                mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
                Log.d(TAG, "setUploadCredit: " + MERCHANT_NUMBER);
                mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
                mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
                String msgLen = "00000268";
                String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
                String msgV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
                String transactionC = "8055";
                String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, "TMS"), 8);
                String transactionNo = "00000000";
                String comCode = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1001), 10);
                String ref1 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF1), 50);   //ref1
                String ref2 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF2), 50);   //ref2
                String ref3 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF3), 50);   //ref3
//                String dateTime63 = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
                String dateTime63 = transTemp.get(uploadCreditPosition).getTransDate() + transTemp.get(uploadCreditPosition).getTransTime().replace(":", "");
                Log.d(TAG, "dateTimeOnline: " + dateTime63);
                String random = CardPrefix.calSpenLen("", 2);
                String terminalCERT = CardPrefix.calSpenLen("", 14);
                String checkSUM = CardPrefix.calSpenLen("", 8);

                String f63 = msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 + ref2 + ref3 + dateTime63 + random
                        + terminalCERT + checkSUM;

                mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(f63.length()), 4) + BlockCalculateUtil.getHexString(f63);

                onLineNow = false; //Paul_20180602
                TPDU = CardPrefix.getTPDU(context, "TMS");
                // Paul_20190122
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        packageAndSend(TPDU, "0320", mBlockDataSend);
                    }
                }, insettimewait);
                uploadCreditPosition++;
                Log.d(TAG, "uploadCreditPosition: " + uploadCreditPosition + "\n uploadCreditSize : " + uploadCreditSize);
            } else {
                setDataDefaultUploadCradit();
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
        Log.d(TAG, messageType + " packageAndSend: " + mBlockData.toString());

        OnUsOffUsFlg =0;
        HOST_CARD = "EPS";
        System.out.println("mblockdatasend");
        for(int i = 0; i<64; i++) {
            if(mBlockData[i] != null) {
                System.out.println(i+":"+mBlockData[i]);
            }
        }
        //mBlockDataSend[55 - 1] ="";
        String applicationData = BlockCalculateUtil.calculateApplicationData(mBlockData);
        Log.d(TAG,"applicationdata="+applicationData);
        String dataToSend = "";
        dataToSend = dataToSend + TPDU;
        dataToSend = dataToSend + messageType;
        dataToSend = dataToSend + applicationData;
        dataToSend = dataToSend.trim();

        if (messageType.equals(MESSAGE_SALE) || messageType.equals(MESSAGE_PREAUTH) || messageType.equals(MESSAGE_VOID_PREAUTH)) {
            insertReversalSaleTransaction(HOST_CARD);        // Paul_20180523
        }

        String traceNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_EPS)) + 1);
        Preference.getInstance(context).setValueString(Preference.KEY_TRACE_NO_EPS, traceNo);

        Log.d(TAG, "Raw packageAndSend => " + dataToSend);
        Log.d("utility::", "Raw packageAndSend => " + dataToSend);

//        Jeff 20180622
        String plainData = dataToSend;// Paul_20180522 Start
        Log.d("plaindata",plainData );
        if (OnUsOffUsFlg == 0) {
            dataToSend = encryptMsg(dataToSend);
        } else  if (OnUsOffUsFlg == 1) {
            dataToSend = OnUsEncryptionMsg( dataToSend );
        } else {
            dataToSend = SmartEncryptionMsg( dataToSend );
        }
//// Paul_20180522 End
        if (dataToSend != null) {
            Log.d(TAG, "Encrypted DATATOSEND => " + dataToSend);
            sendStr(dataToSend);
        } else if (OnUsOffUsFlg == 1) {
            Log.d(TAG, "Encrypted Data is return NULL!!!");
            sendStr(plainData);
        }
        rfflag=false;
    }

    private String SmartEncryptionMsg(String InputISOMessage) {
        String OutputISOMessage = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ISOMESSAGE", InputISOMessage);
            TleLibParamMap TleLibParamMap = new TleLibParamMap();
            TleLibParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("SmarttleEncryption", TleLibParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return OutputISOMessage;
    }

    private String SmartDecryptionMsg(String InputISOMessage) {
        String OutputISOMessage = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ISOMESSAGE", InputISOMessage);
            TleLibParamMap TleLibParamMap = new TleLibParamMap();
            TleLibParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("SmarttleDecryption", TleLibParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return OutputISOMessage;
    }

    // Paul_20180522 Start
    private String OnUsEncryptionMsg(String InputISOMessage) {
        String OutputISOMessage = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ISOMESSAGE", InputISOMessage);
            TleLibParamMap TleLibParamMap = new TleLibParamMap();
            TleLibParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("OnUstleEncryption", TleLibParamMap);
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
            TleLibParamMap TleLibParamMap = new TleLibParamMap();
            TleLibParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("OnUstleDecryption", TleLibParamMap);
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
            } catch (Exception e) {
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
                        SECONDARY_HOST = Preference.getInstance(context).getValueString(Preference.KEY_SECONDARY_IP);
                        SECONDARY_PORT = Preference.getInstance(context).getValueString(Preference.KEY_SECONDARY_PORT);
                        Log.d(TAG, "Host => " + PRIMARY_HOST + " [" + PRIMARY_PORT + "]");

                        Log.d(TAG, "Message Length = " + stringss.length());
                        Log.d(TAG, "Message % 2 = " + (stringss.length() % 2));
                        //Log.d(TAG, "TRACK2 length = "+TRACK2.length());

                        DataExchanger dataExchanger = new DataExchanger(1, PRIMARY_HOST, Integer.valueOf(PRIMARY_PORT), SECONDARY_HOST, Integer.valueOf(SECONDARY_PORT));
                        Log.d(TAG, "pass to new DataExchanger");
                        byte[] clientData = ChangeFormat.writeUTFSpecial(stringss);
                        Log.d(TAG, "pass to ChangeFormat");
                        dataExchanger.doExchange(clientData, customSocketListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            sFixedThreadPool.shutdown();

        /*    if(sFixedThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                Log.d("kang","sendstr complete");
            }
            else {
                Log.d("kang","sendstr not complete, just terminate ");
                sFixedThreadPool.shutdown();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            //showMessage(e.toString());
            Log.d(TAG, e.toString());
        }
    }

    private void tle_initialize(AidlManager deviceManager) {
        try {
            tleVersionOne = tleinterface.Stub.asInterface(deviceManager.getDevice(999));
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
        completeRet = 0;
        //showMessage("Response Data："+response);
        String raw_data="";

        Log.d(TAG, "End Transaction !!!!!!!!!!_JEFF");
        Log.d(TAG, "Encrypted Response Data：" + response);

        response = response.substring(4);
        // Paul_20180522 Start
        if (OnUsOffUsFlg == 0) {
            Log.d("kang", "decrypt/onusoffusflg is 0");
            raw_data = decryptMsg(response); // send to decrypt no need length
        } else if (OnUsOffUsFlg == 1){
            Log.d("kang", "decrypt/onusoffusflg is 1");
            raw_data = OnUsDecryptionMsg(response); // send to decrypt no need length
        } else {
            Log.d("kang", "decrypt/onusoffusflg is else");
            raw_data = SmartDecryptionMsg(response); // send to decrypt no need length
        }
// Paul_20180522 End
//        raw_data = decryptMsg(response); // send to decrypt no need length


        //raw_data = raw_data.substring(4); // already cut length
        String receivedTPDU = raw_data.substring(0, 5 * 2);
        String receivedMessageType = raw_data.substring(5 * 2, 5 * 2 + 2 * 2);
        Log.d("kang","received message type:" + receivedMessageType);

        mBlockDataReceived = BlockCalculateUtil.getReceivedDataBlock(raw_data);


        String result = BlockCalculateUtil.checkResult(mBlockDataReceived[39 - 1]);
        Log.d(TAG, "RETURN INFO OF 37:" + mBlockDataReceived[37 - 1]);
        Log.d(TAG, "RETURN INFO OF 38:" + mBlockDataReceived[38 - 1]);
        Log.d(TAG, "RETURN INFO OF 39:" + mBlockDataReceived[39 - 1]);
        Log.d(TAG, "RETURN INFO OF 55:" + mBlockDataReceived[55 - 1]);
        Log.d(TAG, "c:" + mBlockDataReceived[63 - 1]);


        if(receivedMessageType.equals("0210") && emv != null && readedType == ICCARD) {
            completeRet = completeEmvTrans();
        }
        else if(receivedMessageType.equals("0210") && readedType == RFCARD) {
            if(mBlockDataReceived[55 - 1].length() != 0) {
                byte[] script = "".getBytes();
                byte[] issuerAutentication = "".getBytes();
                if(mBlockDataReceived[55 - 1].substring(4).startsWith("91")) {
                    String field55 = mBlockDataReceived[55 - 1].substring(4);
                    int num = Integer.parseInt(field55.substring(2,4), 16);
                    script = str2Bcd(mBlockDataReceived[55 - 1].substring(4 + (num * 2)));
                    issuerAutentication = str2Bcd(field55.substring(0, 4 + (num * 2)));
                }

                switch(ClssEntryPoint.getInstance().getOutParam().ucKernType) {
                    case KernType.KERNTYPE_MC:
                        ClssPassApi.Clss_SetTLVDataList_MC(str2Bcd(mBlockDataReceived[55 - 1]),str2Bcd(mBlockDataReceived[55 - 1]).length);
                        break;
                    case KernType.KERNTYPE_VIS:
                        completeRet  = ClssPayWave.getInstance().waveFlowComplete(issuerAutentication, issuerAutentication.length, script, script.length);
                        break;
                    case KernType.KERNTYPE_JCB:
                        ClssJCBApi.Clss_IssuerUpdateProc_JCB(script, script.length);
                        ClssJCBApi.Clss_SetTLVDataList_JCB(issuerAutentication, issuerAutentication.length);
                        break;
                    case KernType.KERNTYPE_PBOC:


                    default:
                        break;
                }
            }
        }
        if(completeRet == RetCode.EMV_DENIAL && emv.getTransresult() == TransResult.EMV_ONLINE_APPROVED) {
            if(!emv.getReversalflag()) {
                insertReversalSaleTransaction(HOST_CARD);
                ReversalTemp reversalTemp = null;
                Realm.init(this.context);
                realm = Realm.getDefaultInstance();
                reversalTemp = realm.where(ReversalTemp.class).findFirst();
                if(reversalTemp != null) {
                    setDataReversalAndSendHost(reversalTemp);
                }
            }
            //return;
        }

        if(completeRet < 0) {


            TC = "";
            TVR = "";
            TSI = "";
            PIN = "";
            PINBLOCK="";
            PIN_PYPASS = false;
            BIN_TYPE = "";
            isRF = 0;
            NAMECARDHOLDER = "";
            AID = "";
            CARDLABEL = "";
            CARDTYPE = 0;

            return;
        }
        Log.d(TAG, "Decrypted Response Data：" + raw_data);

        for(int i=0; i< mBlockDataReceived.length; i++) {
            System.out.println("decrypted receivedData--" + (i + 1) + ":" + mBlockDataReceived[i]);
        }
        if (mBlockDataReceived[12 - 1].length() == 6 && mBlockDataReceived[13 - 1].length() == 4) {
            Date dateTime = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy");
            int second = Integer.parseInt(mBlockDataReceived[12 - 1].substring(4,6));
            int minute = Integer.parseInt(mBlockDataReceived[12 - 1].substring(2,4));
            int hour = Integer.parseInt(mBlockDataReceived[12 - 1].substring(0,2));
            int mount = Integer.parseInt(mBlockDataReceived[13 - 1].substring(0,2));
            int date = Integer.parseInt(mBlockDataReceived[13 - 1].substring(2,4));
//                if (settingService != null) {
//                    if (settingService.setSystemTime(second, minute, hour, date, mount, Integer.parseInt(dateFormat.format(dateTime)))) {
//                        Log.d(TAG, "onCreate: Success");
//                    } else {
//                        Log.d(TAG, "onCreate: Fail");
//                    }
//                }
        }

//        for (int i = 0; i < mBlockDataReceived.length; i++) {
//            //System.out.println((i+1)+":"+mBlockDataReceived[i]);
//            Log.d(TAG, (i + 1) + ":" + mBlockDataReceived[i]);
//        }
//
//        String result = BlockCalculateUtil.checkResult(mBlockDataReceived[39 - 1]);
//        Log.d(TAG, "RETURN INFO OF 37:" + mBlockDataReceived[37 - 1]);
//        Log.d(TAG, "RETURN INFO OF 38:" + mBlockDataReceived[38 - 1]);
//        Log.d(TAG, "RETURN INFO OF 39:" + mBlockDataReceived[39 - 1]);
//        Log.d(TAG, "RETURN INFO OF 55:" + mBlockDataReceived[55 - 1]);
//        Log.d(TAG, "RETURN INFO OF 63:" + mBlockDataReceived[63 - 1]);


        if (timeOut) // no response
        {
            Log.d(TAG, "RESULT: timeout = " + timeOut);
            timeOut = false;
        } else {

            // Paul_20180522 Start
            String resultToLoadStr = null;
            if (!mBlockDataReceived[39 - 1].isEmpty()) {
                StringBuilder resultToLoad = new StringBuilder();
                String first = mBlockDataReceived[39 - 1].substring(0, 2);
                String second = mBlockDataReceived[39 - 1].substring(2);
                resultToLoad.append(first.substring(1));
                resultToLoad.append(second.substring(1));
                resultToLoadStr = resultToLoad.toString().trim();
                //System.out.println("resultToLoadStr: "+resultToLoadStr);
                Log.d(TAG, "resultToLoadStr:" + resultToLoadStr);
                response_code = resultToLoadStr;
            }

            if (onLineNow) {
                System.out.printf("utility:: dealWithTheResponse 0006 \n");
                try {
                    Log.d(TAG, "SALE WITH PBOC Process");
// Paul_20180522 Question
                    System.out.printf("utility:: dealWithTheResponse KKK001 \n");
                    //pboc2.importOnlineResp(true, resultToLoadStr, mBlockDataReceived[55 - 1]);
                    if (mBlockDataReceived[55 - 1].length() > 0) {

                        String online_data = mBlockDataReceived[55 - 1].substring(4, mBlockDataReceived[55 - 1].length());

/*
                        pboc2.importOnlineResp(true, resultToLoadStr, online_data.trim());
                    } else {
                        pboc2.importOnlineResp(true, resultToLoadStr, "");
                    }
 */
                        System.out.printf("utility:: dealWithTheResponse KKK002 \n");
// Paul_20180522 Question
                    }
                    onLineNow = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    //showMessage("夭寿啦，导入联机数据异常");
                    Log.d(TAG, "import online data is abnormal");
                }
            }
// Paul_20180522 End
            //showMessage("RESULT:"+result);
//            Log.d(TAG, "RESULT:" + result);
//            Log.d(TAG, "RESULT:" + response_code);
            Log.d(TAG, "dealWithTheResponse 3 - 1: " + mBlockDataSend[3 - 1] + " receivedMessageType : " + receivedMessageType);
            int SenondTimeFlg = 0;


            if (receivedMessageType.equals("0810") && mBlockDataSend[3 - 1].equals("990000")) {
                if (testHostLister != null) {
                    testHostLister.onResponseCodeSuccess();
                    SenondTimeFlg = 1;
                }
            }
            if (!receivedMessageType.equals("0410") && !receivedMessageType.equals("0330") && !receivedMessageType.equals("0510")) {
                if (response_code.equals("00")) {
                    Log.d(TAG, "dealWithTheResponse DE03 : " + mBlockDataReceived[3 - 1]);
                    if (mBlockDataSend[3 - 1].equals(SALE_PROCESSING_CODE)) {
                        Log.d(TAG, "dealWithTheResponse DE37 : " + mBlockDataReceived[37 - 1]);
                        F37 = mBlockDataReceived[37 - 1];
                        F38 = mBlockDataReceived[38 - 1];
                        F39 = mBlockDataReceived[39 - 1];
                        if (!MAG_TRX_RECV) {
//                            insertTransaction("I");
                            Log.d(TAG, "insertTransaction(\"I\");");

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    insertTransaction("I");
                                }
                            }, insettimewait);


                        } else {
//                            insertTransaction("M");

                            Log.d(TAG, "insertTransaction(\"M\");");

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRF == 1)
                                        insertTransaction("W");
                                    else {
                                        insertTransaction("M");
//                                    insertTransaction("M");
                                    }
                                    deleteReversal();
                                }
                            }, insettimewait);



                        }
                        if (HOST_CARD.equalsIgnoreCase("POS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL)) + 1));
                        }
//                        if(mBlockDataSend[3 - 1].equals("990000") && receivedMessageType.equals("0810"))  // Paul_20190123 HostTest success and than no reversal clear
//                        {
//                            deleteReversal();
//                        }
                    } else if (mBlockDataSend[3 - 1].equals(VOID_PROCESSING_CODE) || mBlockDataSend[3 - 1].equals(VOIDHEALTHCARE_PROCESSING_CODE)) { // Void  Modify Paul_201480708
                        Log.d(TAG, "dealWithTheResponse Void : ");
                        Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[3 - 1]);
                        System.out.printf("utility:: VOID Received 0000000000001 \n");
                        F37 = mBlockDataReceived[37 - 1];
                        System.out.printf("utility:: VOID Received 0000000000002 \n");
                        F38 = mBlockDataReceived[38 - 1];
                        System.out.printf("utility:: VOID Received 0000000000003 \n");
                        F39 = mBlockDataReceived[39 - 1];
                        System.out.printf("utility:: VOID Received 0000000000004 \n");
                        TRANCE_NO = mBlockDataReceived[11 - 1];
                        System.out.printf("utility:: VOID Received 0000000000005 \n");

                        ////20181218  SINN Void syn date/time
                        if (mBlockDataReceived[12 - 1].length() == 6)
                            szVoidtime = mBlockDataReceived[12 - 1];
                        if (mBlockDataReceived[13 - 1].length() == 4)
                            szVoiddate = mBlockDataReceived[13 - 1];


                        if (HOST_CARD.equalsIgnoreCase("POS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL)) + 1));
                        } else if (HOST_CARD.equalsIgnoreCase("GHC")) {     // Paul_20180708
                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL)) + 1));
                        }
//                        if(transTemp != null)
//                            System.out.printf("utility:: dealWithTheResponse transTemp.getTraceNo() = %s \n",transTemp.getTraceNo());
                        System.out.printf("utility:: VOID Received 0000000000006 \n");

                        if (!Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")) {   //KTB NORMAL
                            if (!HOST_CARD.equals("TMS") && !HOST_CARD.equals("GHC")) { //Paul_20180708


                                //                                setOnlineUploadCreditVoid(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNT);

                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setOnlineUploadCreditVoid(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNT);
                                    }
                                }, insettimewait);

                            }
                        }


                        System.out.printf("utility:: VOID Received 0000000000007 \n");
                        //SINN fix database without late time got error
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTransactionVoid();
                                deleteReversal();
                            }
                        }, 1000);
//                        updateTransactionVoid();
//                        deleteReversal();


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
//                            Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, BlockCalculateUtil.hexToString(transactionNo).substring(2, 8));
//SINN 20181030 KTB K.PHADET let to modify  if current > 1st no need update.
                            Integer IninvoiceNumber, TMSinvoice;
                            IninvoiceNumber = Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL));
                            TMSinvoice = Integer.valueOf(BlockCalculateUtil.hexToString(transactionNo).substring(2, 8));
                            if (IninvoiceNumber < TMSinvoice)
                                Preference.getInstance(context).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, BlockCalculateUtil.hexToString(transactionNo).substring(2, 8));
                            //end  //SINN KTB K.PHADET let to modify  if current > 1st no need update.


                        }
//                        setDataParameterDownload();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setDataParameterDownload();
                            }
                        }, insettimewait);


                    } else if (mBlockDataSend[3 - 1].equals("900000") && receivedMessageType.equals("0810")) {
                        int tagNumber = 54;
                        Log.d(TAG, "dealWithTheResponse: ParameterDownload");
                        Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[63 - 1]);
                        getTag(tagNumber);

// Paul_20181029 Start Add to showMessageResCode
                        System.out.printf("utility:: %s mBlockDataSend[3 - 1] = %s , receivedMessageType = %s , dealWithTheResponse: %s \n", TAG, mBlockDataSend[3 - 1], receivedMessageType, mBlockDataReceived[63 - 1]);
                        if (responseCodeListener != null) {
                            System.out.printf("utility:: %s 00000ABBBAAABBB 00001 \n", TAG);
                            responseCodeListener.onResponseCodeSuccess();
                        }
//                        else
//                        {
//                            System.out.printf("utility:: %s 00000ABBBAAABBB 0002\n",TAG);
////                            setResponseCodeListener(responseCodeListener);
//                        }
                        return;
// Paul_20181029 End Add to showMessageResCode

                    }
                    if (connectStatusSocket != null) {
                        connectStatusSocket.onReceived();
                    }
                } else {
//                    if(mBlockDataSend[3 - 1].equals("990000") && receivedMessageType.equals("0810"))  // Paul_20190123 HostTest success and than no reversal clear
//                    {
//                        deleteReversal();
//                    }
////20180725  SINN VOID HOST reject EDC still waiting
//                   Log.d(TAG,"MSG ID:"+receivedMessageType+"response_code:"+response_code+"onLineNow:"+String.valueOf(onLineNow));
                    //SINN 20180925 First settlement not approve
                    Log.d(TAG, "Call error");
                    if (mBlockDataSend[3 - 1].equals("920000") && receivedMessageType.equals("0810")) {
                        Log.d(TAG, "First Settlement_TEST");
                        if (HOST_CARD.equalsIgnoreCase("TMS"))
                            responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgTMS(response_code), response_code);
                        else
                            responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgGHC(response_code), response_code);

                        processCallback(PROCESS_TRANS_RESULT_UNKNOW);
                        MAG_TRX_RECV = false;
                        MTI = "";


                    }

                    System.out.printf("utility:: CardManager call to onResponseCodeandMSG 000000001 \n");
                    if (SenondTimeFlg == 0)      // Paul_20180731
                    {
                        isRF = 0;
                        if (HOST_CARD.equalsIgnoreCase("TMS"))
                            responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgTMS(response_code), response_code);
                        else
                            responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgPOS(response_code), response_code);
                    }
                    SenondTimeFlg = 1;
////END 20180725  SINN VOID HOST reject EDC still waiting

                }
            } else if (mBlockDataSend[3 - 1].equals(TC_ADVICE_CODE)) { // TCUpload 943000b
                Log.d(TAG, "tcUploadPosition : " + tcUploadPosition + " tcUploadSize : " + tcUploadSize + " MAG_TRX_RECV:" + Boolean.toString(MAG_TRX_RECV));
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
                                //    if (!HOST_CARD.equalsIgnoreCase("TMS")) {
                                if (!HOST_CARD.equalsIgnoreCase("TMS") && (!Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))) {  //KTB NORMAL
                                    Log.d(TAG, "OnlineUploadCredit: ");
//                                    setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNTFEE);

                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNTFEE);
                                        }
                                    }, insettimewait);


//            setUploadCredit(mBlockDataSend[55 - 1]);
                                }
                            } else {
                                if (!Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")) {
//                                    setUploadCredit();
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setUploadCredit();
                                        }
                                    }, insettimewait);
                                }
//SINN 20190111 KTBBORMAL=1 and no more TCUPLOAD.
                                else {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
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
                                    }, insettimewait);
                                }
//END KTBNORMAL =1

                            }
                        }
                    } else {
//                        if (!HOST_CARD.equalsIgnoreCase("TMS")) {
                        if (!HOST_CARD.equalsIgnoreCase("TMS") && (!Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))) {  //KTB NORMAL
                            Log.d(TAG, "OnlineUploadCredit: ");
//                            setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNTFEE);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNTFEE);
                                }
                            }, insettimewait);

//            setUploadCredit(mBlockDataSend[55 - 1]);
                        }
                    }
                }
            } else if (receivedMessageType.equals("0330") && mBlockDataSend[3 - 1].equals("490000")) {
                Log.d(TAG, "updateTransactionDe11Online: ");
                updateTransactionDe11Online();

            } else if (response_code.equals("95") && mBlockDataSend[3 - 1].equals("920000")) {
                Log.d(TAG, "dealWithTheResponse Settlement Open: " + response_code);
                setBatchUpload();
            } else if (response_code.equals("95") && mBlockDataSend[3 - 1].equals("960000")) {
                Log.d(TAG, "dealWithTheResponse Close Sett: " + response_code);
// Paul_20181028 LastSettlement "95" receive    Start
//                if (settlementHelperLister != null) {
//                    settlementHelperLister.onCloseSettlementFail();
//                }
                if (SenondTimeFlg == 0)      // Paul_20180731
                {
                    if (HOST_CARD.equalsIgnoreCase("TMS"))
                        responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgTMS(response_code), response_code);  ////20180719 SINN Display error msg response host is wrong
                    else
                        responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgPOS(response_code), response_code);  ////20180719 SINN Display error msg response host is wrong
                }
                SenondTimeFlg = 1;
// Paul_20181028 LastSettlement "95" receive    End
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
                    String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
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
                    if (HOST_CARD.equals("TMS")) {
                        mBlockDataSend[61 - 1] = settlement61;
                    }
                    mBlockDataSend[63 - 1] = settlement63;
                    MTI = MESSAGE_SETTLEMENT;
                    onLineNow = true;
                    TPDU = CardPrefix.getTPDU(context, HOST_CARD);
//                    packageAndSend(TPDU, "0500", mBlockDataSend);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            packageAndSend(TPDU, "0500", mBlockDataSend);
                        }
                    }, insettimewait);
                }
            }/* else if (response_code.equals("00") && receivedMessageType.equals("0510") && mBlockDataSend[3 - 1].equals("960000")) {

            } */ else if (response_code.equals("00") && mBlockDataReceived[3 - 1].equals("480000")) {

                if (uploadCreditSize > uploadCreditPosition) {
                    if (!Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")) {
//                        setUploadCredit();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setUploadCredit();
                            }
                        }, insettimewait);
                    }
                } else {
                    setDataDefaultUploadCradit();

//                    switch (HOST_CARD) {
//                        case "EPS":
//                            setDataSettlementAndSendEPS();
//                            break;
//                        case "POS":
//                            setDataSettlementAndSend("POS");
//                            break;
//                        default:
//                            setDataSettlementAndSendTMS();
//                            break;
//                    }

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
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
                    }, insettimewait);

                }

            } else if (!response_code.equals("00") && mBlockDataReceived[3 - 1].equals("480000")) {

//                switch (HOST_CARD) {
//                    case "EPS":
//                        setDataSettlementAndSendEPS();
//                        break;
//                    case "POS":
//                        setDataSettlementAndSend("POS");
//                        break;
//                    default:
//                        setDataSettlementAndSendTMS();
//                        break;
//                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                }, insettimewait);


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
            } else if (response_code.equals("00") && receivedMessageType.equals("0510") && mBlockDataReceived[3 - 1].equals("960000")) { // SettlementClose
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
                if (HOST_CARD.equals("TMS")) {
                    setDataFirstSettlement();
                }
                Log.d(TAG, "Close : Settlement");
                Log.d(TAG, "response_code: " + receivedMessageType);
            }
//            else if (response_code.equals("00") && receivedMessageType.equals("0410")) {
            else if (receivedMessageType.equals("0410")) {
                System.out.printf("utility:: CardManager call to onReversalSuccess \n");
                Log.d(TAG, "onReversalSuccess: " + response_code);
                if (reversalListener != null) {
                    reversalListener.onReversalSuccess();
                }
                deleteReversal();
            }
            if (!response_code.equalsIgnoreCase("00") && !response_code.equalsIgnoreCase("95")) {

                Log.d(TAG, "check resp !=00 && !95" + "SenondTimeFlg= " + String.valueOf(SenondTimeFlg) + " HOST_CARD=" + HOST_CARD + " response_code=" + response_code.substring(0, 2));

                if (OnUsOffUsFlg == 0) {
                    if (responseCodeListener != null) {
// SINN_20180719
//                        responseCodeListener.onResponseCode(RespCode.ResponseMsgPOS(response_code));
                        System.out.printf("utility:: CardManager call to onResponseCodeandMSG 000000002 \n");
                        if (SenondTimeFlg == 0)      // Paul_20180731
                        {
                            if (HOST_CARD.equalsIgnoreCase("TMS"))
                                responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgTMS(response_code.substring(0, 2)), response_code.substring(0, 2));  ////20180719 SINN Display error msg response host is wrong
                            else
                                responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgPOS(response_code.substring(0, 2)), response_code.substring(0, 2));  ////20180719 SINN Display error msg response host is wrong
                        }
                        SenondTimeFlg = 1;
                    }
                } else {
                    if (responseCodeListener != null) {
// SINN_20180719
//                        responseCodeListener.onResponseCode(RespCode.ResponseMsgTMS(response_code));
                        System.out.printf("utility:: CardManager call to onResponseCodeandMSG 000000003 \n");
                        if (SenondTimeFlg == 0)      // Paul_20180731
                        {
                            if (HOST_CARD.equalsIgnoreCase("TMS"))
                                responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgTMS(response_code), response_code);  ////20180719 SINN Display error msg response host is wrong
                            else
                                responseCodeListener.onResponseCodeandMSG(RespCode.ResponseMsgPOS(response_code), response_code);  ////20180719 SINN Display error msg response host is wrong
                        }
                        SenondTimeFlg = 1;
                    }
                }
            } else {
                if (responseCodeListener != null) {
                    if (SenondTimeFlg == 0)      // Paul_20180731
                        responseCodeListener.onResponseCodeSuccess();
                    SenondTimeFlg = 1;
                }
            }

            //Start END part.

            Log.d(TAG, "(Start END PART) response_code:" + response_code);
            Log.d(TAG, "cardHelperListener!=null = " + Boolean.valueOf(cardHelperListener != null) + " cardNoConnectHost != null = " + Boolean.valueOf(cardNoConnectHost != null));

            if (currentTransactionType != REVERSAL) {

                Log.d(TAG, "(currentTransactionType != REVERSAL)");
                if (!mBlockDataSend[3 - 1].equals("990000"))  // Paul_20190123 HostTest success and than no reversal clear
                {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deleteReversal();       // Paul_20180530
                        }
                    }, insettimewait);
                }
                if ((MAG_TRX_RECV) && (mBlockDataSend[3 - 1].trim().equals(VOID_PROCESSING_CODE))) {
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

                } else {
                    APPRVCODE = mBlockDataReceived[38 - 1];
                    if (response_code.trim().equals("00")) {
                        processCallback(PROCESS_TRANS_RESULT_APPROVE);
                    } else {
                        processCallback(PROCESS_TRANS_RESULT_UNKNOW);
                    }

                    if (isRF == 0) {
                        MAG_TRX_RECV = false;
                        MTI = "";
                    } else {
                        MTI = "";
                    }


                }

            }   //end   if (currentTransactionType != REVERSAL)
            else {
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
       /* try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.d(TAG, "deleteReversal: ");
        Realm.getDefaultInstance().refresh();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        PIN = "";
                        Log.d(TAG, "execute: " + HOST_CARD);
                        Log.d(TAG, "execute: DeleteReversal");
                        RealmResults<ReversalTemp> reversalTemp = realm.where(ReversalTemp.class).equalTo("hostTypeCard", HOST_CARD).findAll();
                        Log.d(TAG, "execute: " + reversalTemp.size());
                        reversalTemp.deleteAllFromRealm();
                    }
                });
                realm.close();
            }
        }).start();

    }

    private void insertTransaction(String typeCard) {
        double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
//        double amountFee = (Double.valueOf(AMOUNT) * fee) / 100;

        double amountFee;
        if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
            amountFee = (Double.valueOf(AMOUNT) + fee);
        else
            amountFee = (Double.valueOf(AMOUNT) * fee) / 100;
        amountFee = (int) (amountFee * 100 + 0.5) / 100.0;             // Paul_20190129
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");


//        String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
        String traceIdNo = mBlockDataSend[11 - 1];// Pual_20180601
        Log.d(TAG, "insertTransaction: " + traceIdNo);
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
        String EMCI_ID = "";
        String EMCI_Fee = "";
        TAX_ABB_NEW = "";
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            if (!mBlockDataReceived[63 - 1].isEmpty()) {
                EMCI_ID = mBlockDataReceived[63 - 1].substring(44, 44 + 18);
                EMCI_Fee = mBlockDataReceived[63 - 1].substring(62, 62 + 20);
                Log.d(TAG, "EMCI_ID: " + EMCI_ID + " \n EMCI FEE : " + EMCI_Fee);
            }
        }
        if (realm == null) {
            Log.d(TAG, "1919_insertTransaction");
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        Number currentId = realm.where(TransTemp.class).max("id");
        int nextId;
        if (currentId == null) {
            nextId = 1;
            saleId = nextId;
        } else {
            nextId = currentId.intValue() + 1;
            saleId = nextId;
        }
        TransTemp transTemp = realm.createObject(TransTemp.class, nextId);
        transTemp.setAppid("000001");
        transTemp.setTid(CardPrefix.getTerminalId(context, HOST_CARD));
        transTemp.setMid(CardPrefix.getMerchantId(context, HOST_CARD));
        transTemp.setTraceNo(calNumTraceNo(traceIdNo));
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
        // Paul_20190205 date
        if (mBlockDataReceived[13 - 1].isEmpty() || mBlockDataReceived[13 - 1] == null) {
            transTemp.setTransDate(fDate);
        } else {
            transTemp.setTransDate(fDate.substring(0, 4) + mBlockDataReceived[13 - 1]);
        }
        String tTime = new SimpleDateFormat("HH:mm:ss").format(cDate);
        // Paul_20190205 time
        if (mBlockDataReceived[12 - 1].isEmpty() || mBlockDataReceived[12 - 1] == null) {
            dateTimeOnline = new SimpleDateFormat("yyyyMMddHHmmss").format(cDate);
            transTemp.setTransTime(tTime);
        } else {
            dateTimeOnline = fDate + mBlockDataReceived[12 - 1];
            transTemp.setTransTime(mBlockDataReceived[12 - 1].substring(0, 2) + ":" + mBlockDataReceived[12 - 1].substring(2, 4) + ":" + mBlockDataReceived[12 - 1].substring(4, 6));
        }
        String amountAll = String.valueOf(Double.valueOf(AMOUNT) + amountFee);
        Log.d(TAG, "insertTransaction amountAll : " + amountAll + " AMOUNT : " + Float.valueOf(AMOUNT) + " amountFee : " + amountFee);
        transTemp.setAmount(AMOUNT);
        transTemp.setCardNo(CARD_NO);

        Log.d(TAG, "insertTransaction CARD_NO : " + CARD_NO);

        System.out.printf("utility:: %s insertTransaction CardTypeHolder = %s \n", TAG, CardPrefix.getJSONTypeCardName(CARD_NO));
        transTemp.setCardTypeHolderd(CardPrefix.getJSONTypeCardName(CARD_NO));      // Paul_20181202

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
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            transTemp.setRefNo(CardPrefix.calLen(BlockCalculateUtil.hexToString(EMCI_ID), 12));
        } else {
            transTemp.setRefNo(BlockCalculateUtil.hexToString(mBlockDataReceived[37 - 1]));
        }
        transTemp.setIccData(mBlockDataSend[55 - 1]);
        transTemp.setEmvTc(TC);
        transTemp.setEmvAid(AID);
        transTemp.setEmvAppLabel(CARDLABEL);
        transTemp.setEmvNameCardHolder(NAMECARDHOLDER);
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            transTemp.setApprvCode(BlockCalculateUtil.hexToString(EMCI_ID));
            transTemp.setEmciId(BlockCalculateUtil.hexToString(EMCI_ID));
            String hexFee = BlockCalculateUtil.hexToString(EMCI_Fee);
            String emciFeeStart = hexFee.substring(0, hexFee.length() - 2);
            String emciFeeEnd = hexFee.substring(hexFee.length() - 2, hexFee.length());
            Double emciFee = Double.valueOf(emciFeeStart + "." + emciFeeEnd);
            Log.d(TAG, "insertTransaction: " + BlockCalculateUtil.hexToString(EMCI_ID) + " setEmciFree = " + BlockCalculateUtil.hexToString(EMCI_Fee));
            Log.d(TAG, "String.valueOf(emciFee) : " + String.valueOf(emciFee));
            transTemp.setEmciFree(String.valueOf(emciFee));
        } else {
            transTemp.setApprvCode(BlockCalculateUtil.hexToString(mBlockDataReceived[38 - 1]));
        }
        transTemp.setTransType(typeCard);

        transTemp.setRespCode(BlockCalculateUtil.hexToString(mBlockDataReceived[39 - 1]));
        transTemp.setVoidFlag("N");
        transTemp.setCloseFlag("N");
        transTemp.setTransStat("SALE");
        transTemp.setEcr(invoiceGB);
        transTemp.setHostTypeCard(HOST_CARD);
        transTemp.setComCode(COMCODE);
        transTemp.setRef1(Preference.getInstance(context).getValueString(Preference.KEY_REF1));
        transTemp.setRef2(Preference.getInstance(context).getValueString(Preference.KEY_REF2));
        transTemp.setRef3(Preference.getInstance(context).getValueString(Preference.KEY_REF3));
        transTemp.setPin(mBlockDataSend[52 - 1]);
        if (!HOST_CARD.equals("TMS")) {
            transTemp.setFee(decimalFormat.format(amountFee));
        }


        Log.d("Utility::","TransType 1 " + transTemp.getTransType());

        if (!typeCard.equalsIgnoreCase("M") && !typeCard.equalsIgnoreCase("W")) {
            if (HOST_CARD.equalsIgnoreCase("POS")) {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String dateNow = dateFormat.format(date);
                String taxAbbOriginal = Preference.getInstance(context).getValueString(Preference.KEY_TAX_INVOICE_NO_POS);
                String taxDate = taxAbbOriginal.substring(0, 8);
                int numberRunPos = Integer.parseInt(taxAbbOriginal.substring(8, 12));
                if (taxDate.equalsIgnoreCase(dateNow)) {
                    ++numberRunPos;
                    Preference.getInstance(context).setValueString(Preference.KEY_TAX_INVOICE_NO_POS, taxDate + CardPrefix.calLen(String.valueOf(numberRunPos), 4));
                } else {
                    String newTaxInvoice = dateNow + "0001";
                    Preference.getInstance(context).setValueString(Preference.KEY_TAX_INVOICE_NO_POS, newTaxInvoice);
                }
            } else {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String dateNow = dateFormat.format(date);
                String taxAbbOriginal = Preference.getInstance(context).getValueString(Preference.KEY_TAX_INVOICE_NO_EPS);
                String taxDate = taxAbbOriginal.substring(0, 8);
                int numberRunPos = Integer.parseInt(taxAbbOriginal.substring(8, 12));
                if (taxDate.equalsIgnoreCase(dateNow)) {
                    ++numberRunPos;
                    Preference.getInstance(context).setValueString(Preference.KEY_TAX_INVOICE_NO_EPS, taxDate + CardPrefix.calLen(String.valueOf(numberRunPos), 4));
                } else {
                    String newTaxInvoice = dateNow + "0001";
                    Preference.getInstance(context).setValueString(Preference.KEY_TAX_INVOICE_NO_EPS, newTaxInvoice);
                }
            }
            String tid;
            String taxAbb;
            if (HOST_CARD.equalsIgnoreCase("POS")) {
                tid = CardPrefix.getTerminalId(context, HOST_CARD);
                taxAbb = Preference.getInstance(context).getValueString(Preference.KEY_TAX_INVOICE_NO_POS);
            } else {
                tid = CardPrefix.getTerminalId(context, HOST_CARD);
                taxAbb = Preference.getInstance(context).getValueString(Preference.KEY_TAX_INVOICE_NO_EPS);
            }
            TAX_ABB_NEW = tid + taxAbb;
            transTemp.setTaxAbb(TAX_ABB_NEW);
        }
        AMOUNTFEE = amountFee;
        realm.commitTransaction();
        if (insertOrUpdateDatabase != null) {
            insertOrUpdateDatabase.onInsertSuccess(nextId);
        }

        isRF = 0;
        TC = "";
        NAMECARDHOLDER = "";
        NAMECARDHOLDER_FULL = "";  //20180914 Game get card holder name
        AID = "";
        CARDLABEL = "";
        PIN = "";
        realm.close();
        realm = null;
//        if (!MAG_TRX_RECV) {

//        if (!MAG_TRX_RECV && (!HOST_CARD.equalsIgnoreCase("TMS"))) {   //SINN 20181005 fix for TMS never TCupload
        if (!MAG_TRX_RECV && (!HOST_CARD.equalsIgnoreCase("TMS")) && (mBlockDataSend[55 - 1] != null)) {   //SINN 20181005 fix for TMS never TCupload + Magnatic no de55 no need TCupload
            insertTCUploadTransaction(traceIdNo, tTime, fDate, mBlockDataSend[55 - 1],
                    invoiceNumber.length() + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber)),
                    mBlockDataSend[23 - 1], mBlockDataSend[52 - 1], mBlockDataSend[22 - 1], amountFee);
//            setTCUpload(traceIdNo, tTime, fDate, mBlockDataSend[55 - 1],
//                    invoiceNumber.length() + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber)),
//                    mBlockDataSend[23 - 1], mBlockDataSend[52 - 1], mBlockDataSend[22 - 1]);
        } else {
//            if (!HOST_CARD.equalsIgnoreCase("TMS"))
            if ((!HOST_CARD.equalsIgnoreCase("TMS")) && (!Preference.getInstance(context).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")))   //KTB NORMAL
                setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], AMOUNTFEE);

        }
//        if (!HOST_CARD.equalsIgnoreCase("TMS")) {
//            Log.d(TAG, "OnlineUploadCredit: ");
//            setOnlineUploadCredit(mBlockDataSend[55 - 1], mBlockDataSend[22 - 1], amountFee);
////            setUploadCredit(mBlockDataSend[55 - 1]);
//        }
    }

    public void insertReversalSaleTransaction(final String typeCard) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.printf("utility:: insertReversalSaleTransaction 001 \n");
                if (mBlockDataSend[3 - 1] == null) {
                    System.out.printf("utility:: insertReversalSaleTransaction mBlockDataSend[3 - 1] null \n");
                    return;
                }
                //PAUL_20180718
                if ((!mBlockDataSend[3 - 1].equals(SALE_PROCESSING_CODE)) && (!mBlockDataSend[3 - 1].equals(VOID_PROCESSING_CODE) && (!mBlockDataSend[3 - 1].equals(VOIDHEALTHCARE_PROCESSING_CODE)))) {
                    System.out.printf("utility:: insertReversalSaleTransaction None SALE_PROCESSING_CODE VOID_PROCESSING_CODE \n");
                    return;
                }

                Realm realm = Realm.getDefaultInstance();
                //String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);
                //20180831 SINN  DE62 reversal invoice number no original.
                System.out.printf("utility:: %s insertReversalSaleTransaction mBlockDataSend[3 - 1] = %s , HOST_CARD = %s \n", TAG, mBlockDataSend[3 - 1], HOST_CARD);

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

//                if(mBlockDataReceived[12 - 1].length() == 6)
//                    szVoidtime = mBlockDataReceived[12 - 1];
//                if(mBlockDataReceived[13 - 1].length() == 4)
//                    szVoiddate = mBlockDataReceived[13 - 1];
//
//
//                if (Preference.getInstance(context).getValueString(Preference.KEY_SlipSyncTime_ID).equalsIgnoreCase("1") && reversalTemp.getTransDate()!=null && reversalTemp.getTransTime()!=null) {
//                    Log.d(TAG, "szVoidtime:" + szVoidtime + " " + "szVoiddate:" + szVoiddate);
//                    if (szVoidtime.length() == 6) {
//                        final String hour = szVoidtime.substring(0, 2);
//                        final String minute = szVoidtime.substring(2, 4);
//                        final String second = szVoidtime.substring(4, 6);
//                        reversalTemp.setTransTime(hour + ":" + minute + ":" + second);
//
//                    }
//                    if (szVoiddate.length() == 4) {
//                        Date cDate = new Date();
//                        String fDate = new SimpleDateFormat("yyyy").format(cDate);
//
//                        final Date dateTime = new Date();
//                        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//                        reversalTemp.setTransDate(dateFormat.format(dateTime) + fDate+szVoiddate);   //setTransDate getTransDate  //20181224
//                    }
//                    szVoidtime = "";
//                    szVoiddate = "";
//                }else {
                Date cDate = new Date();
                String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
                reversalTemp.setTransDate(fDate);
                String tTime = new SimpleDateFormat("HH:mm:ss").format(cDate);
                reversalTemp.setTransTime(tTime);
//                }

                reversalTemp.setAmount(decimalFormat.format(Double.valueOf(AMOUNT)));    // Paul_20190128
                reversalTemp.setCardNo(CARD_NO);
                reversalTemp.setCardType("0"); //cardType == REFUND ? "1" : "0"
                reversalTemp.setTrack1(TRACK1); //TODO ถ้าไม่มีต้องทำยังไง เซตค่าว่าง ?
                // Paul_20180523 Start
                if (HOST_CARD.equals("TMS")) {

//                    reversalTemp.setTrack2(TRACK2_ENC);
                    reversalTemp.setTrack2(TRACK2);   // Paul_20181106 Reversal TMS Track2
                    reversalTemp.setField63(mBlockDataSend[63 - 1]);
                    reversalTemp.setPinblock(mBlockDataSend[52 - 1]);
                }
                ////20180830 SINN reversal VOID no DE63 and NII 0362
                else if (HOST_CARD.equalsIgnoreCase("GHC")) {
                    reversalTemp.setCardNo(CARD_NO);
                    reversalTemp.setTrack2(TRACK2);
                    reversalTemp.setField63(mBlockDataSend[63 - 1]);
                    NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_GHC);
                    //  Log.d(TAG, "insertReversalSaleTransaction CARD_NO :"+CARD_NO +" transTemp.getEcr() ="+transTemp.getEcr());
                }
                //END 20180830 SINN reversal VOID no DE63 and NII 0362
                else {
                    reversalTemp.setTrack2(TRACK2);
                }
                // Paul_20180523 End
//                reversalTemp.setTrack2(TRACK2);       // Paul_20181101
                reversalTemp.setProcCode(PROCESSING_CODE);
                reversalTemp.setPosem(POSEM);
                reversalTemp.setPosoc(POSOC);
                reversalTemp.setNii(NII);

                Log.d("utility:: " + TAG, "NII =" + NII + " TRACK2 =" + TRACK2 + " HOST_CARD=" + HOST_CARD.toString());

                reversalTemp.setPointService(mBlockDataSend[22 - 1]);
                reversalTemp.setApplicationPAN(mBlockDataSend[23 - 1]);
                reversalTemp.setExpiry(EXPIRY);
                //               Log.d(TAG, "de37 =" + mBlockDataSend[37 - 1] + " de38 =" + mBlockDataSend[38 - 1] + " de39=" + mBlockDataSend[39 - 1]);
//                Log.d(TAG, "de37 hexToString =" + BlockCalculateUtil.hexToString(mBlockDataSend[37 - 1]));
                //               Log.d(TAG, "de37 ASCII2Str =" + BlockCalculateUtil.ASCII2Str(mBlockDataSend[37 - 1]));

//                reversalTemp.setRefNo(mBlockDataReceived[37 - 1] == null ? "" : mBlockDataReceived[37 - 1]);
//transTemp.setRefNo(BlockCalculateUtil.hexToString(mBlockDataReceived[37 - 1]));
                if (mBlockDataSend[37 - 1] == null)
                    reversalTemp.setRefNo("");
                else
                    reversalTemp.setRefNo(BlockCalculateUtil.ASCII2Str(mBlockDataSend[37 - 1]));   //BlockCalculateUtil.hexToString(mBlockDataReceived[37 - 1])


                reversalTemp.setIccData(mBlockDataSend[55 - 1]);
                reversalTemp.setApprvCode(mBlockDataReceived[38 - 1] == null ? "" : mBlockDataSend[38 - 1]);
//                if(mBlockDataSend[38 - 1]==null)
//                    reversalTemp.setApprvCode("");
//                else reversalTemp.setApprvCode(BlockCalculateUtil.hexToString(mBlockDataReceived[38 - 1]));


//                reversalTemp.setTransType(typeCard);
//                reversalTemp.setRespCode(mBlockDataReceived[39 - 1] == null ? "" : mBlockDataReceived[39 - 1]);
                reversalTemp.setRespCode(mBlockDataReceived[39 - 1] == null ? "" : mBlockDataSend[39 - 1]);
//                if(mBlockDataSend[39 - 1]==null)
//                    reversalTemp.setRespCode("");
//                else reversalTemp.setRespCode(mBlockDataSend[39 - 1]);


                reversalTemp.setVoidFlag("N");
                reversalTemp.setCloseFlag("N");
                if (MAG_TRX_RECV) {
//                    reversalTemp.setTransType("M");
                    reversalTemp.setTransType("W");
                } else {
                    reversalTemp.setTransType("I");
                }

                Log.d("Utility::","TransType 2 " + reversalTemp.getTransType());

                if (mBlockDataSend[3 - 1].equals(SALE_PROCESSING_CODE)) {
                    Ecr_NO = calNumTraceNo(CardPrefix.getInvoice(context, HOST_CARD));  // Paul_20190121 Reversal sale fail modify
                    reversalTemp.setTransStat("SALE");
                } else if (mBlockDataSend[3 - 1].equals(VOID_PROCESSING_CODE)) {
                    reversalTemp.setTransStat("VOID");
                } else if (mBlockDataSend[3 - 1].equals(VOIDHEALTHCARE_PROCESSING_CODE)) { //PAUL_20180718
                    reversalTemp.setTransStat("VOID");
                }

                //20180907 reversal ecr invoice
                reversalTemp.setEcr(calNumTraceNo(Ecr_NO));
                reversalTemp.setHostTypeCard(HOST_CARD);
                reversalTemp.setReserved(mBlockDataSend[63 - 1]);
                Log.d(TAG, "insertReversalSaleTransaction: " + calNumTraceNo(Ecr_NO).length() + " " + BlockCalculateUtil.getHexString(calNumTraceNo(Ecr_NO)));
                realm.commitTransaction();
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }).start();
        // Paul_20180523
        // Paul_20180523

    }

    private String Track2MappingTable(String t2, String TRANDATE) {
        String PinBlock = null;
        Log.d(TAG, "Track2MappingTable: " + t2);
        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("TRACK2", t2);
            hashMap.put("TRANDATE", TRANDATE);
            //Toast.makeText(InterfaceTestActvity.this,hashMap.size()+"弱뷴?",Toast.LENGTH_SHORT).show();
            TleLibParamMap TleLibParamMap = new TleLibParamMap();
            TleLibParamMap.setParamMap(hashMap);
            PinBlock = tleVersionOne.tleFuncton("EncryptMappingTable", TleLibParamMap);
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
        reversalTemp.setTransType("C");
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
        String traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
        String invoiceNumber = CardPrefix.getInvoice(context, HOST_CARD);

        try {
            if (realm == null) {
                Log.d(TAG, "1919_insertTCUploadTransaction");
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

// Paul_20181105 AXA Option
            if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
                //SINN 20181019 Add WAY4
                tcUpload.setRef1(Preference.getInstance(context).getValueString(Preference.KEY_REF1));
                tcUpload.setRef2(Preference.getInstance(context).getValueString(Preference.KEY_REF2));
                tcUpload.setRef3(Preference.getInstance(context).getValueString(Preference.KEY_REF3));
                //SINN 20181019 Add WAY4
            }

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

    public void updateTransactionVoid() {

        System.out.printf("utility:: %s updateTransactionVoid 000000000 \n", TAG);
        int ii = Preference.getInstance(context).getValueInt(Preference.KEY_SET_ID);
        System.out.printf("utility:: %s updateTransactionVoid transTemp.getId() iiiiiiii = %d \n", TAG, ii);


        TransTemp VoidtransTemp; // Database

//        if (realm == null) {
//            System.out.printf("utility:: updateTransactionVoid 000000099 \n");
//            realm = Realm.getDefaultInstance();
//        }
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//            }
//        });
//        realm.close();
//        realm = null;
        if (realm == null)       // Paul_20181026
            realm = Realm.getDefaultInstance();
        //  }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            }
        });
        realm.close();
        realm = null;

        realm = Realm.getDefaultInstance();
        System.out.printf("utility:: %s updateTransactionVoid 000000100 \n", TAG);
        VoidtransTemp = realm.where(TransTemp.class).equalTo("id", ii).findFirst();

        System.out.printf("utility:: %s updateTransactionVoid transTemp.getTraceNo() = %s \n", TAG, VoidtransTemp.getTraceNo());
//        Log.d(TAG, "updateTransactionVoid: " + transTemp.getTraceNo());
        final int transTempID = VoidtransTemp.getId();

//        final int transTempID = Preference.getInstance(context).getValueInt( Preference.KEY_SET_ID);
        System.out.printf("utility:: %s updateTransactionVoid transTemp.getId() = %s \n", TAG, transTempID);
        System.out.printf("utility:: %s updateTransactionVoid 000000001 \n", TAG);
//        if (realm == null) {
//            System.out.printf("utility:: updateTransactionVoid 000000002 \n");
//            realm = Realm.getDefaultInstance();
//        }
        System.out.printf("utility:: %s updateTransactionVoid 000000003 \n", TAG);

//        RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).findAll();
//        transTemps.size();
//
//        Log.d(TAG, "insertTransaction: " + transTemps.size() + " base : " + transTemps.toString());
//        System.out.printf("utility:: updateTransactionVoid 000000004 transTemps.toString() = %s \n",transTemps.toString());

        //SINN 20181018 Save void EMCI
        String EMCI_ID = "";
        if (HOST_CARD.equalsIgnoreCase("TMS")) {
            if (!mBlockDataReceived[63 - 1].isEmpty()) {
                EMCI_ID = mBlockDataReceived[63 - 1].substring(44, 44 + 18);
                Log.d(TAG, "EMCI_ID: " + EMCI_ID);
            }
        }
        System.out.printf("utility:: %s updateTransactionVoid EMCI_ID = %s \n", TAG, EMCI_ID);
//END SINN 20181018 Save void EMCI

//        TransTemp trans = realm.where(TransTemp.class).equalTo("id", transTemp.getId()).findFirst();
        TransTemp trans = realm.where(TransTemp.class).equalTo("id", transTempID).findFirst();
        realm.beginTransaction();
        System.out.printf("utility:: %s updateTransactionVoid 000000004 \n", TAG);
        System.out.printf("utility:: %s updateTransactionVoid 000000004 mBlockDataReceived[63 - 1] = %s \n", TAG, mBlockDataReceived[63 - 1]);
        if (trans != null) {
            System.out.printf("utility:: %s updateTransactionVoid 000000005 \n", TAG);
            trans.setVoidFlag("Y");
            if (HOST_CARD.equalsIgnoreCase("TMS"))  // Paul_20181025
            {
                System.out.printf("utility:: %s updateTransactionVoid 000000005 AAAA BlockCalculateUtil.hexToString( EMCI_ID ) = %s\n", TAG, BlockCalculateUtil.hexToString(EMCI_ID));
                trans.setApprvCode(BlockCalculateUtil.hexToString(EMCI_ID));
            }
//            trans.setTraceNo(mBlockDataReceived[11 - 1]);
            if (!TRANCE_NO.isEmpty()) {
                System.out.printf("utility:: %s updateTransactionVoid 000000006 \n", TAG);
                trans.setTraceNo(TRANCE_NO);
                transTemp.setApprvCode(BlockCalculateUtil.hexToString(EMCI_ID));   //SINN 20181018 Save void EMCI
            } else {
                System.out.printf("utility:: %s updateTransactionVoid 000000007 \n", TAG);
                trans.setTraceNo(mBlockDataSend[11 - 1]);
                transTemp.setApprvCode(BlockCalculateUtil.hexToString(EMCI_ID));   //SINN 20181018 Save void EMCI
            }

            ////20181218  SINN Void syn date/time
            if (Preference.getInstance(context).getValueString(Preference.KEY_SlipSyncTime_ID).equalsIgnoreCase("1")) {
                Log.d(TAG, "szVoidtime:" + szVoidtime + " " + "szVoiddate:" + szVoiddate);
                if (szVoidtime.length() == 6) {
                    final String hour = szVoidtime.substring(0, 2);
                    final String minute = szVoidtime.substring(2, 4);
                    final String second = szVoidtime.substring(4, 6);
                    trans.setTransTime(hour + ":" + minute + ":" + second);
                    transTemp.setTransTime(hour + ":" + minute + ":" + second);

                }
                if (szVoiddate.length() == 4) {
                    final Date dateTime = new Date();
                    final DateFormat dateFormat = new SimpleDateFormat("yyyy");

                    trans.setTransDate(dateFormat.format(dateTime) + szVoiddate);   //setTransDate getTransDate  //20181224
                    transTemp.setTransDate(dateFormat.format(dateTime) + szVoidtime);
                }

                szVoidtime = "";
                szVoiddate = "";

            } ////END 20181218  SINN Void syn date/time
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
                    Log.d("kang", "tcupload/hostcard:" + HOST_CARD + ",traceno:" + tcUploadId);
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
                        //insertOrUpdateDatabase.onUpdateVoidSuccess(upload.getId());
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

    private String checkCardno(String track2) {
        char temp[] = track2.toCharArray();

        for(int i = 0; i < track2.length(); i++){
            if(temp[i] == 0x44)
                return track2.substring(0,i);
        }
        return track2; //Error
    }

    //region - Listener

    private CardHelperListener cardHelperListener = null;
    private InsertOrUpdateDatabase insertOrUpdateDatabase = null;
    private ReversalListener reversalListener = null;
    private ConnectStatusSocket connectStatusSocket = null;
    private SettlementHelperLister settlementHelperLister = null;
    private ResponseCodeListener responseCodeListener = null;
    private ResponsePINListener responsePINListener = null;
    private NFCListener nfcListener = null;
    private TestHostLister testHostLister = null;
    private TransResultAbortLister transResultAbortLister = null;
    private CardNoConnectHost cardNoConnectHost = null;
    private UpdateLister updateLister = null;
//    private RequiredPINListener requiredPINListener = null;

    public void setCardHelperListener(CardHelperListener cardHelperListener) {
        this.cardHelperListener = cardHelperListener;
    }
//    public void setRequiredPINListener(RequiredPINListener requiredPINListener) {
//        this.requiredPINListener = requiredPINListener;
//    }

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

    public void setResponsePINListener(ResponsePINListener responsePINListener) {
        this.responsePINListener = responsePINListener;
    }

    public void setNfcListener(NFCListener nfcListener) {
        this.nfcListener = nfcListener;
    }

    public void setTestHostLister(TestHostLister testHostLister) {
        this.testHostLister = testHostLister;
    }

    public void setTransResultAbortLister(TransResultAbortLister transResultAbortLister) {
        this.transResultAbortLister = transResultAbortLister;
    }
    public void setCardNoConnectHost(CardNoConnectHost cardNoConnectHost) {
        this.cardNoConnectHost = cardNoConnectHost;
    }

    public void setUpdateLister(UpdateLister updateLister) {
        this.updateLister = updateLister;
    }

    public void removeCardHelperListener() {
        this.cardHelperListener = null;
    }

    public void removeInsertOrUpdateDatabase() {
        this.insertOrUpdateDatabase = null;
    }

    public void removeReversalListener() {
        this.reversalListener = null;
    }

    public void removeConnectStatusSocket() {
        this.connectStatusSocket = null;
    }

    public void removeSettlementHelperLister() {
        this.settlementHelperLister = null;
    }

    public void removeResponseCodeListener() {
        this.responseCodeListener = null;
    }

    public void removeResponsePINListener() {
        this.responsePINListener = null;
    }

    public void removeNFCListener() {
        this.nfcListener = null;
    }

    public void removeTestHostLister() {
        this.testHostLister = null;
    }

    public void removeTransResultAbort() {
        this.transResultAbortLister = null;
    }

    public void removeCardNoConnectHost() {
        this.cardNoConnectHost = null;
    }

    public void inputofflinePIN(String insertedpin) {
        String lc = String.valueOf(insertedpin.length());
        String pin_lc = checkPinlength(lc);
        String padding = checkPadding(lc);

        Log.d("kang","pin:" + insertedpin);
        if(!insertedpin.equals("TEMP")) {
            PIN = insertedpin;
        }
        if(PIN.equals("")) { //for UPI bypass Pin function
            if(CARDTYPE == RFCARD)
                POS_ENT_MODE = "0072";
            else
                POS_ENT_MODE = "0052";
            mBlockDataSend[22 - 1] = POS_ENT_MODE;
            PIN_PYPASS = true;
//                pboc2.importPin("");
        }else{
            if(CARDTYPE == RFCARD) {
                CL_POS_ENT_MODE = "0071";
                mBlockDataSend[22 - 1] = CL_POS_ENT_MODE;
            }else{
                POS_ENT_MODE = "0051";
                mBlockDataSend[22 - 1] = POS_ENT_MODE;
            }


   //            pboc2.importPin("2" + pin_lc + pin + padding);
        }
        if(insertedpin.equals("TEMP")) {
            POS_ENT_MODE = "0051";
            mBlockDataSend[22 - 1] = POS_ENT_MODE;
        }


    }

    private String checkPadding(String lc) {
        int tmp = 14 - Integer.parseInt(lc);
        String result = "";

        for(int i=0; i<tmp; i++)
            result = result + "F";

        return  result;
    }

    private String checkPinlength(String lc) {
        String result = "";

        switch(lc){
            case "1" :
            case "2" :
            case "3" :
            case "4" :
            case "5" :
            case "6" :
            case "7" :
            case "8" :
            case "9" :
                result = lc;
                break;
            case "10" :
                result = "A";
                break;
            case "11" :
                result = "B";
                break;
            case "12" :
                result = "C";
                break;
            case "13" :
                result = "D";
                break;
            case "14" :
                result = "E";
                break;
        }
        return  result;
    }

    byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    public String OffUsEPSPinBlock(String PAN, String PIN) {
        String PinBlock = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("PAN", PAN);
            hashMap.put("PIN", PIN);
            //Toast.makeText(InterfaceTestActvity.this,hashMap.size()+"弱뷴?",Toast.LENGTH_SHORT).show();
            TleLibParamMap TleLibParamMap = new TleLibParamMap();
            TleLibParamMap.setParamMap(hashMap);
            PinBlock = tleVersionOne.tleFuncton("OffUsEPSPinBlock", TleLibParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return PinBlock;
    }

    public interface SettlementHelperLister {
        public void onSettlementSuccess();

        public void onCloseSettlementFail();

    }

    public interface CardHelperListener {

        public  void onCardNo(String cardNo);

        public void onCardInfoReceived(Card card);

        public void onCardInfoFail();

        public void onTransResultFallBack();

        public void onTransResulltNone();

        public void onCardTransactionUpdate(boolean isApproved, Card card);

        public void onFindMagCard(Card card);

        public void onSwapCardIc();

        public void onSwapCardMag();

        public void onSwipeCardFail();


        public void onFindICCard();


        public void onFindCard();

        public void onMultiApp(int item, EMV_APPLIST[] arg);

        public void onMultiApp(int item, String[] arg);

        public void onDuplicateTrans(String msg);

        public void onGetCardHolderName(String szCardName);

        public void onCardInfoReceived_Contactless(String CARD_NO, String NAMECARDHOLDER, String AMOUNT);

        public void onFindContactlessMultiapp();

        public void onTansAbort();

    }

//    public interface RequiredPINListener {
//        public void onRequiredPIN();
//    }

    public interface InsertOrUpdateDatabase {
        public void onUpdateVoidSuccess(int id);

        public void onInsertSuccess(int id);
    }

    public interface ReversalListener {
        public void onReversalSuccess();
    }

    public interface ConnectStatusSocket {
        public void onConnectTimeOut();

        public void onTransactionTimeOut();

        public void onError(String message);

        public void onError();

        public void onOther();

        public void onReceived();
    }

    public interface ResponseCodeListener {
        public void onResponseCode(String response);

        public void onResponseCodeSuccess();

        public void onConnectTimeOut();

        public void onTransactionTimeOut();

        public void onResponseCodeandMSG(String response, String szCode);
    }

    public interface ResponsePINListener {
        public void onRequirePIN();
        public void onBYPASSfail();
    }

    public interface NFCListener {
        public void onfindNFC();
    }

    public interface TestHostLister {
        public void onResponseCodeSuccess();

        public void onConnectTimeOut();

        public void onTransactionTimeOut();
    }

    public interface TransResultAbortLister {
        public void onTransResultAbort();
    }

    public interface CardNoConnectHost {
        public void onProcessTransResultUnknow();
        public void onProcessTransResultRefuse();
    }

    public static void setReadType(EReaderType type) {
        readerMode = type;
    }

    public static void setReaderType(EReaderType type) {
        readerType = type;
    }

    public static EReaderType getReaderType() {
        System.out.printf("utility:: %s getReaderType\n",TAG);
        return readerType;
    }

    public static EReaderType getReadType() {
        System.out.printf("utility:: %s getReadType\n",TAG);
        return readerMode;
    }

    public static void setStatusFlg(boolean bstatusFlg) {
        statusFlg = bstatusFlg;
    }



    public int completeEmvTrans() {
        int ret = emv.CompleteContactEmvTrans(mBlockDataReceived);

        TVR = bcd2str(ImplEmv.getTlv(0x95));
        TSI = bcd2str(ImplEmv.getTlv(0x9B));
        Log.d("kang","TSI:" + TSI);
        Log.d("kang","TVR:" + TVR);

        return ret;
    }

    public String[] get959B() {
        String[] arr = new String[2];



        if(TVR.length() > 4) {
            arr[0] = TVR.substring(4);
        }
        else {
            arr[0] = "";
        }

        if(TSI.length() > 4) {
            arr[1] = TSI.substring(4);
        }
        else {
            arr[1] = "";
        }

        return arr;
    }
    public void startEmvTrans() {
        System.out.printf("utility:: %s startEmvTrans\n",TAG);
        emv = new ImplEmv(this.context);
        //emv.setdialog(pindialog);
        emv.ulAmntAuth = Long.parseLong(AMOUNT.replace(".", ""));
        emv.amount = AMOUNT;
        Log.i(TAG, "transParam.ulAmntAuth:" + emv.ulAmntAuth);
        emv.ulAmntOther = 0;
        emv.ulTransNo = 1;
        emv.ucTransType = 0x00;
        //emv.setSelectedaid(aid);
        boolean flag_swipe = false;

        int ret = 0;
  /*      if(aid.equals("")) {
            ret = emv.startContactEmvTrans("");
        }
        else {
            ret = emv.startContactEmvTrans(aid);
        }*/

        ret = emv.startContactEmvTrans();

        Log.i(TAG, "startContactEmvTrans ret= " + ret);

        if (ret == TransResult.EMV_ARQC) {
            toOnlineProc();
            byte[] track2 = ImplEmv.getTlv(0x57);
            String strTrack2 = bcd2str(track2);
            Bundle bundle = new Bundle();
            Log.d("strTrack2", strTrack2);

            //Intent intent = new Intent(this, CalculatePriceActivity.class);

            //strTrack2 = strTrack2.split("F")[0];
            card = analyse(strTrack2);
            System.out.println("after analyse : " + card.toString());
            HOST_CARD = CardPrefix.getJsonTypeCARD(card.getNo());
            //Host_card = CardPrefix.getJsonTypeCARD(card.getNo());

            if (Preference.getInstance(this.context).getValueString(Preference.KEY_SWIPE_ID).equalsIgnoreCase("1"))
                flag_swipe = true;



            allOperateStart(CardManager.SALE, flag_swipe, true, false, "Searching the card", "");
/*
            try {
                Thread.sleep(3000);
            } catch(Exception e) {
                e.printStackTrace();
            }
*/

            ret = completeRet;
//
        }

        Log.d("kang","completeRet:" + completeRet);

        if (ret == TransResult.EMV_ONLINE_APPROVED  || ret == TransResult.EMV_ONLINE_CARD_DENIED ) {

            byte[] track2 = ImplEmv.getTlv(0x57);
            String strTrack2 = bcd2str(track2);
            Bundle bundle = new Bundle();
            Log.d("strTrack2", strTrack2);
            card = analyse(strTrack2);
            System.out.println("after analyse : " + card.toString());

            NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            TRACK2 = strTrack2;
            OPERATE_ID = SALE;
            processCallback(PROCESS_CONFIRM_CARD_INFO);
            HOST_CARD = CardPrefix.getJsonTypeCARD(card.getNo());

            BIN_TYPE = CardPrefix.getTypeCard(card.getNo());
            TRACK2 = strTrack2;
            if (Preference.getInstance(this.context).getValueString(Preference.KEY_SWIPE_ID).equalsIgnoreCase("1")) {
                flag_swipe = true;
            }
        }
        else if(ret == TransResult.EMV_OFFLINE_APPROVED) {

            byte[] track2 = ImplEmv.getTlv(0x57);
            String strTrack2 = bcd2str(track2);
            Bundle bundle = new Bundle();
            Log.d("strTrack2", strTrack2);
            card = analyse(strTrack2);
            System.out.println("after analyse : " + card.toString());

            NII = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
            TRACK2 = strTrack2;
            OPERATE_ID = SALE;
            processCallback(PROCESS_CONFIRM_CARD_INFO);
            HOST_CARD = CardPrefix.getJsonTypeCARD(card.getNo());

            BIN_TYPE = CardPrefix.getTypeCard(card.getNo());
            TRACK2 = strTrack2;

            String Tag_9f35 = bcd2Str(ImplEmv.getTlv(0x9F35));

            //offline
            mBlockDataReceived[39 - 1] = "3030";
            tempSavedAllData = bcd2Str(ImplEmv.getTlv2());
            mBlockDataReceived[37 - 1] = "3030303030303030";
            mBlockDataReceived[38 - 1] = "3030303030303030";
            RRN = mBlockDataReceived[37 - 1]; //field37
            APPRVCODE = mBlockDataReceived[38 - 1]; //field37
            processCallback(PROCESS_TRANS_RESULT_APPROVE);
            insertTransaction("C");


        }


        else if(ret < 0){
            showErr(ret);
        }
    }

    public void starMagTrans() {
        System.out.printf("utility:: %s starMagTrans\n",TAG);
        //pan = TrackUtils.getPan(trackData2);
        magRet = 0;
        boolean flag_swipe = false;
        //showPan();
        Log.i(TAG, "magRet = " + magRet);
        magRet = TransResult.EMV_ONLINE_APPROVED;
        if (magRet == TransResult.EMV_ONLINE_APPROVED) {
            int a = TRACK2.indexOf("=");
            //TRACK2 = TRACK2.replace("=","");
            if(!TRACK2.substring(0,2).equals("57")) {
                TRACK2 = "57" + Integer.toHexString(a) + TRACK2;
            }
            card = analyse(TRACK2);
            Log.d("startmagTrans",card.toString());
            /*
            toOnlineProc();
            byte[] track2 = ImplEmv.getTlv(0x57);
            String strTrack2 = bcd2str(track2);
            Bundle bundle = new Bundle();
            Log.d("strTrack2",strTrack2);

            //Intent intent = new Intent(this, CalculatePriceActivity.class);

            //strTrack2 = strTrack2.split("F")[0];
            card = analyse(strTrack2);

            */
            System.out.println("after analyse : " + card.toString());
            HOST_CARD = CardPrefix.getJsonTypeCARD(card.getNo());
            //Host_card = CardPrefix.getJsonTypeCARD(card.getNo());

            //if(Preference.getInstance(this.context).getValueString(Preference.KEY_SWIPE_ID).equalsIgnoreCase( "1" ))
            flag_swipe = true;




            allOperateStart(CardManager.SALE,flag_swipe,false,false,"Searching the card", "");

            String nameCard = bcd2str(ImplEmv.getTlv(0x5F20));
            if (nameCard.length() > 6) {

                Log.d("AAA0", nameCard + " " + nameCard.charAt(0));
                byte name_array[] = ChangeFormat.hexStringToByte(nameCard);
                Log.d("AAA0", nameCard + " " + name_array[0]);
                //if( name_array[0] > 0x7a){
                try {
                    NAMECARDHOLDER = new String(name_array, "gbk");
                    Log.d("AAA1", NAMECARDHOLDER);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    NAMECARDHOLDER = new String(name_array);
                    Log.d("AAA2", NAMECARDHOLDER);
                }
                NAMECARDHOLDER_FULL = NAMECARDHOLDER;
            }
            else {
                NAMECARDHOLDER = "";
                NAMECARDHOLDER_FULL = NAMECARDHOLDER;
            }
            //Intent intent = new Intent(this, TradeResultActivity.class);
            //intent.putExtra("amount", amount);
            //intent.putExtra("pan", pan);
            //startActivity(intent);
        } else {
            endProcess();
        }

    }

    public int starPiccTrans() {
        System.out.printf("utility:: %s starPiccTrans\n",TAG);
        int ret = RetCode.EMV_OK;
        try {
            initClssTrans.getinstance().start();
            initClssTrans.getinstance().join();
        } catch (Exception e ){
            e.printStackTrace();
        }
        while (true) {
            setStatusFlg(false);
            serReadType.setrReadType(EReaderType.DEFAULT.getEReaderType());
            ret = entryPoint.entryProcess();
            if (ret != RetCode.EMV_OK) {
                if (ret == RetCode.CLSS_TRY_AGAIN) {
                    ret = entryPoint.setConfigParam((byte) /*0x37*/0x36, false, tmAidList, preProcInfo);
                    if (ret != RetCode.EMV_OK) {
                        showErr(ret);
                        Log.e(TAG, "setConfigParam ret = " + ret);
                        return ret;
                    }
                    ret = entryPoint.preEntryProcess(transParam);
                    if (ret != RetCode.EMV_OK) {
                        showErr(ret);
                        Log.e(TAG, "preEntryProcess ret = " + ret);
                        return ret;
                    }
                    continue;
                }
                else if(ret == RetCode.ICC_CMD_ERR) {
                    showErr(ret);
                    return ret;
                }
                else {
                    showErr(ret);
                    Log.e(TAG, "entryProcess ret = " + ret);
                    return ret;
                }
            }

            switch (ClssEntryPoint.getInstance().getOutParam().ucKernType) {
                case KernType.KERNTYPE_MC:
                    ret = startMC();
                    break;
                case KernType.KERNTYPE_VIS:
                    ret = startVIS();
                    break;
                case KernType.KERNTYPE_AE:
                    //ret = startAE();
                    break;
                case KernType.KERNTYPE_ZIP:
                    //ret = startDPAS();
                    break;
                case KernType.KERNTYPE_PBOC:
                    ret = startqpboc();
                    break;
                case KernType.KERNTYPE_JCB:
                    ret = startJCB();
                    break;
                case KernType.KERNTYPE_PURE:
                    //ret = startPure();
                    break;
                default:
                    Log.e(TAG, "KernType error, type = " + entryPoint.getOutParam().ucKernType);
                    showErr(PromptMsg.ONLY_PAYPASS_PAYWAVE);
                    break;
            }
            if (ret == RetCode.CLSS_TRY_AGAIN || ret == RetCode.CLSS_REFER_CONSUMER_DEVICE) {
                ret = entryPoint.setConfigParam((byte) /*0x37*/0x36, false, tmAidList, preProcInfo);
                if (ret != RetCode.EMV_OK) {
                    showErr(ret);
                    Log.e(TAG, "setConfigParam ret = " + ret);
                    return ret;
                }
                ret = entryPoint.preEntryProcess(transParam);
                if (ret != RetCode.EMV_OK) {
                    showErr(ret);
                    Log.e(TAG, "preEntryProcess ret = " + ret);
                    return ret;
                }
                continue;
            }
            else if(ret == RetCode.CLSS_FAILED || ret == RetCode.CLSS_DECLINE) {
                myCardReaderHelper.getInstance().stopPolling();
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MenuServiceListActivity.getinstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);

                            MenuServiceListActivity.getinstance().getDialogWaiting().dismiss();

                            MenuServiceListActivity.getinstance().getDialogContactless().show();
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                MenuServiceListActivity.getinstance().searchCardThread.start();


                ret = entryPoint.setConfigParam((byte) /*0x37*/0x36, false, tmAidList, preProcInfo);

                if (ret != RetCode.EMV_OK) {
                    showErr(ret);
                    Log.e(TAG, "setConfigParam ret = " + ret);
                    return ret;
                }
                ret = entryPoint.preEntryProcess(transParam);
                if (ret != RetCode.EMV_OK) {
                    showErr(ret);
                    Log.e(TAG, "preEntryProcess ret = " + ret);
                    return ret;
                }
            }

            else if (ret != 0) {
                showErr(ret);
                return ret;
            }
            break;
        }
        //allOperateStart(SALE, false, false, true, "", "");

        return RetCode.EMV_OK;
    }

    //Gillian end 20170522
    private void showErr(final int ret) {
        System.out.printf("utility:: %s showErr \n",TAG);




        if (getReadType() != null) {
            SystemClock.sleep(300);
            Log.i(TAG, "getReadType=" + getReadType().getEReaderType());
            Log.i(TAG, "readType=" + serReadType.getrReadType());
            if (getReadType().getEReaderType() == EReaderType.PICC.getEReaderType()) {
                if (serReadType.getrReadType() == EReaderType.MAG.getEReaderType()) {
                    setReadType(EReaderType.MAG);
                    Log.i(TAG, " EReaderType.MAG");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            starMagTrans();
                        }
                    }).start();
                    return;
                } else if (serReadType.getrReadType() == EReaderType.ICC.getEReaderType()) {
                    setReadType(EReaderType.ICC);
                    Log.i(TAG, " EReaderType.ICC");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startEmvTrans();
                        }
                    }).start();
                    return;
                }
            }
        }
        if(ret == RetCode.EMV_NO_APP || ret == RetCode.ICC_RESET_ERR) {
            setReaderType(EReaderType.MAG);
            processCallback(PROCESS_TRANS_RESULT_FALLBACK);
        }
        else {
            MenuServiceListActivity.getinstance().showerr(-ret + ":" + PromptMsg.getErrorMsg(ret));
        }
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = PromptMsg.getErrorMsg(ret);
                final CustomAlertDialog dialog = new CustomAlertDialog(//Activity.this, CustomAlertDialog.ERROR_TYPE);
                dialog.setTitleText(msg);
                dialog.show();
                Device.beepErr();
                dialog.showConfirmButton(true);
                //dialog.showCancelButton(true);
                dialog.setConfirmClickListener(new CustomAlertDialog.OnCustomClickListener() {
                    @Override
                    public void onClick(CustomAlertDialog alertDialog) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        });*/
    }

    private int startVIS() {
        int ret = RetCode.EMV_OK;
        Clss_PreProcInfo procInfo = null;
        TransResult transResult = new TransResult();

        ClssPayWave.getInstance().setCallback(new TradeCallback(MenuServiceListActivity.getinstance(), Utility.getDialogAlert()));
        ClssPayWave.getInstance().coreInit();


        byte[] aucCvmReq = new byte[2];
        aucCvmReq[0] = CvmType.RD_CVM_REQ_SIG;
        aucCvmReq[1] = CvmType.RD_CVM_REQ_ONLINE_PIN;
        Clss_VisaAidParam visaAidParam = new Clss_VisaAidParam(100000, (byte) 0, (byte) 2, aucCvmReq, (byte) 0);
        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                procInfo = FileParse.getPreProcInfos()[i];
                break;
            }
        }
        ret = ClssPayWave.getInstance().setConfigParam(visaAidParam, procInfo);
        Log.d("kang","ClssPayWave.setConfigParam ret:" + ret);

        prnTime("startVIS set Param time = ");
        ret = ClssPayWave.getInstance().waveProcess(transResult);
        Log.i(TAG, "waveProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
//        endDate = new Date(System.currentTimeMillis());
//        diff = endDate.getTime() - startDate.getTime();
//        Log.e(TAG, "ClssPayWave setConfigParam diff = " + diff);

        if (ret == 0) {
            successProcess(ClssPayWave.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssPayWave.getInstance().getCVMType());
        }
        return ret;
    }


    private int startMC() {
        int ret = RetCode.EMV_OK;
        Clss_PreProcInfo procInfo = null;
        Clss_MCAidParam aidParam = null;
        TransResult transResult = new TransResult();
        Log.d("startMC",String.valueOf(transResult.result));
        ClssPayPass.getInstance().setCallback(new TradeCallback(MenuServiceListActivity.getinstance(), Utility.getDialogAlert()));
        //ClssPayPass.getInstance().setCallback(TradeCallback.getInstance(this));

        //ClssPayPass.getInstance().coreInit((byte) 1);

        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getMcAidParams()[i];
                break;
            }
        }
        ClssPayPass.getInstance().setConfigParam(aidParam, procInfo);
        ret = ClssPayPass.getInstance().passProcess(transResult);
        Log.i(TAG, "passProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            successProcess(ClssPayPass.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssPayPass.getInstance().getCVMType());
        }
        return ret;
    }

    private int startConlssPBOC(TransResult transResult) {
        emv = new ImplEmv(this.context);
        emv.ulAmntAuth = entryPoint.getTransParam().ulAmntAuth;
        emv.amount = AMOUNT;
        Log.i(TAG, "transParam.ulAmntAuth:" + emv.ulAmntAuth);
        emv.ulAmntOther = entryPoint.getTransParam().ulAmntOther;
        emv.ulTransNo = entryPoint.getTransParam().ulTransNo;
        emv.ucTransType = entryPoint.getTransParam().ucTransType;
        setReadType(EReaderType.ICC);
        int ret = emv.startClssPBOC(transResult);
        Log.i(TAG, "startConlessPBOC ret= " + ret);
        return ret;
    }

    private int startqpboc() {
        String ssAID;
        String listAID;
        int cvmType;
        int ret = RetCode.EMV_OK;

        Clss_PreProcInfo procInfo = null;
        Clss_PbocAidParam aidParam = null;
        TransResult transResult = new TransResult();

        //ClssQuickPass.getInstance().setCallback(new TradeCallback(CalculatePriceActivity.getinstance()));
        ssAID = ChangeFormat.bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen);
        //Log.i(TAG, "sAID  = " + ssAID);
        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            listAID = ChangeFormat.bcd2Str(FileParse.getPreProcInfos()[i].aucAID, FileParse.getPreProcInfos()[i].ucAidLen);
            if (ssAID.indexOf(listAID) != -1) {
                //Log.i(TAG, "ssAID.indexOf(listAID) OK");
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getPbocAidParams()[i];
                break;
            }
        }

        //Log.i(TAG, "aidParam.ucAETermCap  = " + Integer.toHexString(aidParam.ucAETermCap) );
        ret = ClssQuickPass.getInstance().setConfigParam(aidParam, procInfo);

        ret = ClssQuickPass.getInstance().qPbocProcess(transResult);
        Log.i(TAG, "ClssQuickPass ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            cvmType = ClssQuickPass.getInstance().getCVMType();
            if (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC) {   //Contact PBOC
                ret = startConlssPBOC(transResult);
                cvmType = CvmType.RD_CVM_NO;
            }
            if (ret == 0) {
                successProcess(cvmType, transResult.result);
                Log.i(TAG, "cvm = " + ClssQuickPass.getInstance().getCVMType());
            }
        }
        return ret;
    }

    private int startAE() {
        int ret;
        String ssAID;
        String listAID;

        Clss_PreProcInfo procInfo = null;
        CLSS_AEAIDPARAM aidParam = null;
        TransResult transResult = new TransResult();
        ClssExpressPay.getInstance().setCallback(new TradeCallback(MenuServiceListActivity.getinstance(), Utility.getDialogAlert()));

        ssAID = ChangeFormat.bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen);
        //Log.i(TAG, "sAID  = " + ssAID);
        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            listAID = ChangeFormat.bcd2Str(FileParse.getPreProcInfos()[i].aucAID, FileParse.getPreProcInfos()[i].ucAidLen);
            if (ssAID.indexOf(listAID) != -1) {
                //Log.i(TAG, "ssAID.indexOf(listAID) OK");
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getAeAidParams()[i];
                break;
            }
        }

        ClssExpressPay.getInstance().setConfigParam(aidParam, procInfo);

        ret = ClssExpressPay.getInstance().expressProcess(transResult);
        Log.i(TAG, "expressProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            successProcess(ClssExpressPay.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssExpressPay.getInstance().getCVMType());
        }
        return ret;
    }

    private int startDPAS() {
        int ret = RetCode.EMV_OK;
        // Clss_PreProcInfo procInfo = null;
        TransResult transResult = new TransResult();
        ClssDPAS.getInstance().setCallback(new TradeCallback(MenuServiceListActivity.getinstance(), Utility.getDialogAlert()));

    /*    for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                procInfo = FileParse.getPreProcInfos()[i];
                break;
            }
        }*/
        ClssDPAS.getInstance().setConfigParam();
        ret = ClssDPAS.getInstance().DPASProcess(transResult);
        Log.i(TAG, "DPASProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            successProcess(ClssDPAS.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssDPAS.getInstance().getCVMType());
        }
        return ret;
    }

    private int startJCB() {
        int ret = RetCode.EMV_OK;
        Clss_PreProcInfo procInfo = null;
        Clss_JcbAidParam aidParam = null;
        TransResult transResult = new TransResult();
        ClssJSpeedy.getInstance().setCallback(new TradeCallback(MenuServiceListActivity.getinstance(), Utility.getDialogAlert()));
        //ClssPayWave.getInstance().coreInit();


        byte[] aucCvmReq = new byte[2];
        aucCvmReq[0] = CvmType.RD_CVM_REQ_SIG;
        aucCvmReq[1] = CvmType.RD_CVM_REQ_ONLINE_PIN;
        //Clss_VisaAidParam visaAidParam = new Clss_VisaAidParam(100000, (byte) 0, (byte) 2, aucCvmReq, (byte) 0);

        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                Log.i(TAG, "ClssEntryPoint.getInstance().getOutParam().sAID = " + ChangeFormat.bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen));
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getJcbAidParams()[i];
                break;
            }
        }
        ClssJSpeedy.getInstance().setConfigParam(aidParam, procInfo);
        ret = ClssJSpeedy.getInstance().jspeedyProcess(transResult);
        Log.i(TAG, "jspeedyProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);

        if (ret == 0) {
            successProcess(ClssJSpeedy.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssJSpeedy.getInstance().getCVMType());
        }
        return ret;
    }

    private int startPure() {
        int ret = RetCode.EMV_OK;
        Clss_PreProcInfo procInfo = null;
        Clss_PureAidParam aidParam = null;
        TransResult transResult = new TransResult();
        CassPure.getInstance().setCallback(new TradeCallback(MenuServiceListActivity.getinstance(), Utility.getDialogAlert()));
        //ClssPayWave.getInstance().coreInit();


        byte[] aucCvmReq = new byte[2];
        aucCvmReq[0] = CvmType.RD_CVM_REQ_SIG;
        aucCvmReq[1] = CvmType.RD_CVM_REQ_ONLINE_PIN;
        //Clss_VisaAidParam visaAidParam = new Clss_VisaAidParam(100000, (byte) 0, (byte) 2, aucCvmReq, (byte) 0);

        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                //Log.i(TAG, "ClssEntryPoint.getInstance().getOutParam().sAID = " + bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen));
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getPureAidParams()[i];
                break;
            }
        }
        CassPure.getInstance().setConfigParam(aidParam, procInfo);
        ret = CassPure.getInstance().pureProcess(transResult);
        Log.i(TAG, "pureProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);

        if (ret == 0) {
            successProcess(CassPure.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + CassPure.getInstance().getCVMType());
        }
        return ret;
    }

    public class initClssTrans extends Thread {

        public initClssTrans  instance = null;
        public initClssTrans getinstance() {
            if(instance == null) {
                instance = new initClssTrans();
            }
            return instance;
        }

        @Override
        public void run() {
            Log.d(TAG, "start initclssTrans");
            int ret = RetCode.EMV_OK;

            transParam = new Clss_TransParam();
            entryPoint.coreInit();
            if(AMOUNT.equals("")) {
                transParam.ulAmntAuth = 0;
            }
            else {
                transParam.ulAmntAuth = Long.parseLong(AMOUNT.replace(".", ""));
            }
            if(AMOUNT.equals("")) {
                transParam.ulAmntOther = 0;
            }
            else {
                transParam.ulAmntOther = Long.parseLong(AMOUNT.replace(".", ""));
            }

            //transParam.ulAmntOther = 0;
            transParam.ulTransNo = 1;
            transParam.ucTransType = 0x00;

            String transDate = MainApplication.getDal().getSys().getDate();
            System.arraycopy(str2Bcd(transDate.substring(2, 8)), 0, transParam.aucTransDate, 0, 3);
            String transTime = MainApplication.getDal().getSys().getDate();
            System.arraycopy(str2Bcd(transTime.substring(8)), 0, transParam.aucTransTime, 0, 3);
            Log.i(TAG, "transParam.aucTransDate: " + bcd2Str(transParam.aucTransDate));
            Log.i(TAG, "transParam.aucTransTime: " + bcd2Str(transParam.aucTransTime));

            tmAidList = FileParse.getTmAidLists();
            preProcInfo = FileParse.getPreProcInfos();

            ClssPayWave.getInstance().coreInit();
            ClssPayPass.getInstance().coreInit((byte) 1);
            Log.d("kang", "paypass init");
            ClssExpressPay.getInstance().coreInit();
            ClssDPAS.getInstance().coreInit();
            ClssQuickPass.getInstance().coreInit();
            ClssJSpeedy.getInstance().coreInit();
            CassPure.getInstance().coreInit();

            ret = entryPoint.setConfigParam((byte) /*0x37*/0x36, false, tmAidList, preProcInfo);
            if (ret != RetCode.EMV_OK) {
                showErr(ret);
                Log.e(TAG, "setConfigParam ret = " + ret);
                return;
            }

            ret = entryPoint.preEntryProcess(transParam);
            if (ret != RetCode.EMV_OK) {
                showErr(ret);
                Log.e(TAG, "preEntryProcess ret = " + ret);
            }
            Log.d("kang", "initClssTrans end");
        }

    }

    public static void prnTime(String msg) {
        Log.d("prnTime",msg);
        //return;
//        endDate = new Date(System.currentTimeMillis());
//        long diff = endDate.getTime() - startDate.getTime();
//        Log.e(TAG, msg + diff);
//        startDate = new Date(System.currentTimeMillis());

    }

    public Card analyse(String cardInfo) {
        String fieldData = "";
        Log.d("cardanalyse",cardInfo);
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

    private void successProcess(int cvmType, int result) {
        ByteArray tk2 = new ByteArray();
        if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_MC) {
            if (ClssPayPass.getInstance().getTransPath() == TransactionPath.CLSS_MC_MAG) {
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9f, (byte) 0x6B}, (byte) 2, 60, tk2);
            } else if (ClssPayPass.getInstance().getTransPath() == TransactionPath.CLSS_MC_MCHIP) {
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{0x57}, (byte) 1, 60, tk2);
            }
            ByteArray byteArray = new ByteArray();
            ClssPassApi.Clss_GetTLVDataList_MC(getF55Taglist(), (byte)1, 60, byteArray );
            String tag55 = bcd2str(byteArray.data);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_VIS) {
            ClssWaveApi.Clss_GetTLVData_Wave((short) 0x57, tk2);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_AE) {
            if (ClssExpressPay.getInstance().getTransPath() == TransactionMode.AE_MAGMODE) {
                ClssAmexApi.Clss_nGetTrackMapData_AE((byte) 0x02, tk2);
            }
            if (tk2.length == 0)
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x57, tk2);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_ZIP) {
            ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{0x57}, (byte) 1, 60, tk2);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) {
            if (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC) {
                tk2.data = ImplEmv.getTlv(0x57);
                tk2.length = tk2.data.length;
            } else
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x57, tk2);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_JCB) {

            if (ClssJSpeedy.getInstance().getTransPath() == TransactionPath.CLSS_JCB_MAG) {
                int ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x6B}, (byte) 1, 60, tk2);
                if (ret != RetCode.EMV_OK) {
                    ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x57}, (byte) 1, 60, tk2);
                }
            } else
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x57}, (byte) 1, 60, tk2);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PURE) {
            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x57}, (byte) 1, 60, tk2);
            setTagOfList55_JCB();

        }
        //Log.d("successprocess",bcd2str(tk2.data));
        byte[] arr = new byte[19];
        System.arraycopy(tk2.data, 0, arr, 0, 19);

        TRACK2 = bcd2str(arr);
        TRACK2 = "57" + Integer.toHexString(arr.length) + TRACK2;


        System.out.println("track2 : " + TRACK2);

        card = analyse(TRACK2);
        System.out.println("success process : card->" + card.toString());

        //trk
        //pan = TrackUtils.getPan(bcd2Str(tk2.data));
        Log.i(TAG, "cvmType = " + cvmType);

        if (cvmType == CvmType.RD_CVM_ONLINE_PIN) {
            toConsumeActitivy(result, cvmType);
        } else if (cvmType == (CvmType.RD_CVM_ONLINE_PIN + CvmType.RD_CVM_SIG)) {
            toConsumeActitivy(result, cvmType);
            //allOperateStart(SALE, false, false, true, "","");
        } else if (cvmType == CvmType.RD_CVM_NO) {
            if (result == TransResult.EMV_ARQC) {
                allOperateStart(SALE, false, false, true, "","");
            } else if (result == TransResult.EMV_OFFLINE_APPROVED) {
                toTradeResultActivityTc();
            }
        } else {
            if (result == TransResult.EMV_ARQC) {
                allOperateStart(SALE, false, false, true, "","");
            } else if (result == TransResult.EMV_OFFLINE_APPROVED) {
                toTradeResultActivityTc();
            }
        }

    }

    private void toTradeResultActivity() {
        /*if(promptDialog != null) {
            promptDialog.dismiss();
            promptDialog = null;
        }*/
//        while (true) {
//            if (promptDialog == null) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        promptDialog = new CustomAlertDialog(//Activity.this, CustomAlertDialog.PROGRESS_TYPE);
//
//                        promptDialog.show();
//                        promptDialog.setCancelable(false);
//                        promptDialog.setTitleText(getString(R.string.prompt_online));
//
//                    }
//                });
//            } else {
//                Log.i(TAG, "promptDialog dismiss");
//                promptDialog.dismiss();
//                break;
//            }
//            SystemClock.sleep(3000);
//        }
        if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_AE) {
            int result = OnlineResult.ONLINE_APPROVE;
            byte[] aucRspCode = "00".getBytes();
            byte[] aucAuthCode = "123456".getBytes();

            int sgAuthDataLen = 5;
            byte[] sAuthData = str2Bcd("1234567890");
            byte[] sIssuerScript = str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD");
            int sgScriptLen = 18;
            ClssExpressPay.getInstance().amexFlowComplete(result, aucRspCode, aucAuthCode, sAuthData, sgAuthDataLen, sIssuerScript, sgScriptLen);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_JCB) {
            if (ClssJSpeedy.getInstance().getTransPath() == TransactionPath.CLSS_JCB_EMV) {
                byte[] sIssuerScript = str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD");
                int sgScriptLen = 18;
                ClssJSpeedy.getInstance().jcbFlowComplete(sIssuerScript, sgScriptLen);
            }
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) {
            if (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC) {   //Contact PBOC
                emv.CompleteContactEmvTrans(mBlockDataReceived);
                //TradeCallback.getInstance(//Activity.this).removeCardPrompt();
            }
        }
        Log.i(TAG, "Start TradeResultActivity");

/*
        Intent intent = new Intent(this, CalculatePriceActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("pan", pan);
        intent.putExtra("hostcard",Host_card);
        startActivity(intent);
*/
    }

    private void toTradeResultActivityTc() {
 /*       while (true) {
            if (promptDialog == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptDialog = new CustomAlertDialog(//Activity.this, CustomAlertDialog.PROGRESS_TYPE);

                        promptDialog.show();
                        promptDialog.setCancelable(false);
                        promptDialog.setTitleText(getString(R.string.prompt_offline));

                    }
                });
            } else {
                Log.i(TAG, "promptDialog dismiss");
                promptDialog.dismiss();
                break;
            }
            SystemClock.sleep(3000);
        }
        */
        /*
        Log.i(TAG, "Start TradeResultActivity");
        Intent intent = new Intent(this, CalculatePriceActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("pan", pan);
        startActivity(intent);
        */
    }


    private void toConsumeActitivy(int result, int cvmtype) {

        System.out.printf("utility:: %s toConsumeActitivy\n",TAG);

        Intent intent = new Intent(context, ConsumeActivity.class);
        intent.putExtra("amount", AMOUNT);
        intent.putExtra("pan", card.getNo());
        intent.putExtra("result", result);
        intent.putExtra("cvmtype", cvmtype);
        context.startActivity(intent);
    }


    private void toOnlineProc() {
        System.out.printf("utility:: %s toOnlineProc\n",TAG);
        /*if(promptDialog != null) {
            promptDialog.dismiss();
            promptDialog = null;
        }*/
//        while (true) {
//            if (promptDialog == null) {
//                Log.i(TAG, "toOnlineProc promptDialog == null");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        promptDialog = new CustomAlertDialog(//Activity.this, CustomAlertDialog.PROGRESS_TYPE);
//
//                        promptDialog.show();
//                        promptDialog.setCancelable(false);
//                        promptDialog.setTitleText(getString(R.string.prompt_online));
//
//                    }
//                });
//            } else {
//                Log.i(TAG, "promptDialog dismiss");
//                promptDialog.dismiss();
//                break;
//            }
//            SystemClock.sleep(4000);
//        }
    }

    private static int byteArrayToInt(byte[] bytes) {
        final int size = Integer.SIZE / 8;
        ByteBuffer buff = ByteBuffer.allocate(size);
        final byte[] newBytes = new byte[size];
        for(int i = 0; i < size; i++) {
            if(i + bytes.length < size) {
                newBytes[i] = (byte) 0x00;
            }
            else {
                newBytes[i] = bytes[i + bytes.length - size];
            }
        }
        buff = ByteBuffer.wrap(newBytes);
        buff.order(ByteOrder.nativeOrder());
        return buff.getInt();
    }

    public static int toint(byte[] source) {
        int result =
                source[0] << 24 |
                        (source[1] & 0xff) << 16 |
                        (source[2] & 0xff) << 8 |
                        (source[3] & 0xff);
        return result;
    }

    public void setTRACK1(String var) {
        TRACK1 = var;
    }

    public void setTRACK2(String var) {
        TRACK2 = var;
    }

    public void setTRACK3(String var) {
        TRACK3 = var;
    }
//endregion

    public static byte[] combine(int... bytes) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(4);

        int i;
        for(i = 0; i < bytes.length; ++i) {
            bout.write(bytes[bytes.length - i - 1]);
        }

        for(i = 0; i < 4 - bytes.length; ++i) {
            bout.write(0);
        }

        return bout.toByteArray();
    }


    public static byte[] getF55Taglist() {
        try {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            ous.write(combine(159, 38));
            ous.write(combine(159, 39));
            ous.write(combine(159, 16));
            ous.write(combine(159, 55));
            ous.write(combine(159, 54));
            ous.write(combine(149));
            ous.write(combine(154));
            ous.write(combine(156));
            ous.write(combine(159, 2));
            ous.write(combine(95, 42));
            ous.write(combine(130));
            ous.write(combine(159, 26));
            ous.write(combine(159, 3));
            ous.write(combine(159, 51));
            ous.write(combine(159, 52));
            ous.write(combine(159, 53));
            ous.write(combine(159, 30));
            ous.write(combine(132));
            ous.write(combine(159, 9));
            ous.write(combine(159, 65));
            ous.write(combine(159, 99));
            ous.write(combine(223, 50));
            ous.write(combine(223, 51));
            ous.write(combine(223, 52));
            ous.write(combine(79));
            ous.write(combine(223, 49));
            ous.write(combine(138));
            ous.write(combine(95, 40));
            ous.write(combine(159, 116));
            ous.write(combine(155));
            ous.write(combine(80));
            ous.write(combine(159, 18));
            ous.write(combine(159, 78));
            ous.write(combine(159, 123));
            ous.write(combine(159, 119));
            ous.write(combine(143));
            ous.write(combine(90));
            ous.write(combine(95, 52));
            ous.write(combine(87));
            ous.write(combine(145));
            ous.write(combine(113));
            ous.write(combine(114));
            return ous.toByteArray();
        } catch (Exception var1) {
            return null;
        }
    }

    private byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[2];
        short a = (short)value;
        byteArray[0] = (byte)(a & 0xff);
        byteArray[1] = (byte)((a >> 8) & 0xff);
        return byteArray;
    }

    private short byteToShort(byte[] bytes) {
        short value = 0;
        value |= (((short)bytes[0]) << 8 ) & 0xFF00;
        value |= (((short)bytes[1])) & 0xFF;

        return value;
    }

    public void stopTransaction() {
        Log.d("kang", "Cancel checkcard");
        try {
            //initClssTrans();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CardHelperListener getCardHelperListener() {
        return this.cardHelperListener;
    }


    public void selectMultiApp(int index) {
        try {
            Log.d("kang", "selectmultiapp/index:" + index);

            //startEmvTrans(null, aid);
            emv.setSelectedinedex(index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPINBLOCK(String pin) {
        Log.d("kang","inputpinblock:" + pin);
        this.PINBLOCK = pin;
        POS_ENT_MODE = "0051";
        mBlockDataSend[22 - 1] = POS_ENT_MODE;
    }

    public void checkUpdate() {
        try {
            String json;
            String path;
            JSONObject jsonObj;
            File file;

            String file_address = "org.centerm.tollway";
            if (isAppExist(context, file_address)) {
                String tarPackage = "com.centerm.chock.gtmshelper";
                if (isAppExist(context, tarPackage)) {
                    json = gtmsService.getLatestJsonVersion();
                    if (json != null && !json.isEmpty()) {
                        //Update Apk
                        jsonObj = new JSONObject(json);

                        path = jsonObj.optString("Path");
                        file = new File(path);

                        if (file.exists()) {
//                                if (Preference.getInstance(context).getValueString(Preference.KEY_NEW_UPDATE).equals("ON")) {
                            if (path.equals("/storage/emulated/0/oversea_ct/gtms/print_param.json"))
                                updateLister.onNone();
                            else {
                                json = gtmsService.getLastestApkVersion("org.centerm.tollway");

                                if (json != null && !json.isEmpty()) {
                                    jsonObj = new JSONObject(json);
                                    path = jsonObj.optString("Path");
                                    file = new File(path);
                                    if (file.exists()) {
                                        updateLister.onFindJsonandApk();
                                    } else {
                                        updateLister.onFindJson();
                                    }
                                } else
                                    updateLister.onFindJson();
                            }
                        } else {
                            json = gtmsService.getLastestApkVersion("org.centerm.tollway");

                            if (json != null && !json.isEmpty()) {
                                jsonObj = new JSONObject(json);
                                path = jsonObj.optString("Path");
                                file = new File(path);
                                if (file.exists()) {
                                    updateLister.onFindApk();
                                } else {
                                    updateLister.onNone();
                                }
                            } else
                                updateLister.onNone();
                        }
                    } else {
                        json = gtmsService.getLastestApkVersion("org.centerm.tollway");

                        if (json != null && !json.isEmpty()) {
                            jsonObj = new JSONObject(json);
                            path = jsonObj.optString("Path");
                            file = new File(path);
                            if (file.exists()) {
                                updateLister.onFindApk();
                            } else {
                                updateLister.onNone();
                            }
                        } else
                            updateLister.onNone();
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface UpdateLister {
        public void onFindJson();

        public void onFindApk();

        public void onFindJsonandApk();

        public void onNone();

        public void onRunTHVInstaller(String path, String pw);

        public void onUpdateJson();

        public void onUpdateTle();

        public void onUpdateFail();
    }

    public boolean isAppExist(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String AutoTLE(String Option) {
        String OutString = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("AUTOTLEINPUT", Option);
            final TleLibParamMap tleParamMap = new TleLibParamMap();
            tleParamMap.setParamMap(hashMap);
            OutString = tleVersionOne.tleFuncton("AutoTle", tleParamMap);

            if (OutString.equals("100"))
                updateLister.onUpdateTle();
            else
                updateLister.onUpdateFail();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return OutString;
    }

    public void updateFile() {
        String json;
        JSONObject jsonObj;
        String path;
        File file;
        try {
            if (gtmsService == null) {
                updateLister.onUpdateFail();
            } else {
                json = gtmsService.getLatestJsonVersion();

                if (json != null && !json.isEmpty()) {
                    jsonObj = new JSONObject(json);
                    path = jsonObj.optString("Path");
                    file = new File(path);
                    if (file.exists()) {
                        if (path.equals("/storage/emulated/0/oversea_ct/gtms/print_param.json"))
                            updateLister.onNone();
                        else {
                            File fileToMove = new File("/storage/emulated/0/oversea_ct/gtms/print_param.json");
                            boolean isMoved = file.renameTo(fileToMove);

                            if (isMoved) {
                                updateLister.onUpdateJson();
                            } else {
                                updateLister.onUpdateFail();
                            }
                        }
                    } else {
                        json = gtmsService.getLastestApkVersion("org.centerm.tollway");
                        if (json != null && !json.isEmpty()) {
                            //Update Apk
                            jsonObj = new JSONObject(json);

                            path = jsonObj.optString("Path");
                            String pws = jsonObj.optString("Psw");
                            file = new File(path);

                            if (file.exists()) {
                                updateLister.onRunTHVInstaller(path, pws);
                            } else {
                                updateLister.onNone();
                            }
                        } else {
                            updateLister.onNone();
                        }
                    }
                } else {
                    json = gtmsService.getLastestApkVersion("org.centerm.tollway");
                    if (json != null && !json.isEmpty()) {
                        //Update Apk
                        jsonObj = new JSONObject(json);

                        path = jsonObj.optString("Path");
                        String pws = jsonObj.optString("Psw");
                        file = new File(path);

                        if (file.exists()) {
                            updateLister.onRunTHVInstaller(path, pws);
                        } else {
                            updateLister.onNone();
                        }
                    } else {
                        updateLister.onNone();
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

        HOST_CARD = "TMS";   //20181002  first settlement fail

        packageAndSend(Preference.getInstance(context).getValueString(Preference.KEY_TPDU_TMS), "0800", mBlockDataSend);

    }

    public void setDataParameterDownload() {

        String terminalVersion = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String messageVersion = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionCode = Preference.getInstance(context).getValueString(Preference.KEY_TRANSACTION_CODE);
        String parameterVersion = Preference.getInstance(context).getValueString(Preference.KEY_PARAMETER_VERSION);
        if (responseCodeListener == null) {
            System.out.printf("utility:: responseCodeListener = null \n");
        }

        System.out.printf("utility:: %s terminalVersion = %s \n", TAG, terminalVersion);
        System.out.printf("utility:: %s messageVersion = %s \n", TAG, messageVersion);
        System.out.printf("utility:: %s transactionCode = %s \n", TAG, transactionCode);
        System.out.printf("utility:: %s parameterVersion = %s \n", TAG, parameterVersion);


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

        HOST_CARD = "TMS";   //20181002  first settlement fail
        packageAndSend(Preference.getInstance(context).getValueString(Preference.KEY_TPDU_TMS), "0800", mBlockDataSend);

    }
    public void setDataTestHostPos() {
        TERMINAL_ID = CardPrefix.getTerminalId(context, "POS");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "POS");
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "990000";
        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)));
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);

        onLineNow = false;      // Paul_20181119
        TPDU = CardPrefix.getTPDU(context, "POS");
        packageAndSend(TPDU, "0800", mBlockDataSend);
    }

    public void setDataTestHostEPS() {
        TERMINAL_ID = CardPrefix.getTerminalId(context, "EPS");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "EPS");
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "990000";
        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_EPS)));
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);

        onLineNow = false;      // Paul_20181119
        TPDU = CardPrefix.getTPDU(context, "EPS");
        packageAndSend(TPDU, "0800", mBlockDataSend);
    }

    public void setDataTestHostTMS() {
        String terminalVersion = "00000001";
        String messageVersion = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionCode = "4017";
        String messageLen = "00000058";
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "        ";           // Paul_20180522
        TERMINAL_ID = CardPrefix.getTerminalId(context, "TMS");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "TMS");
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "990000";
        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_TMS)));
        mBlockDataSend[24 - 1] = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((messageLen + terminalVersion + messageVersion + transactionCode + terminalCERT + checkSUM).length())) + BlockCalculateUtil.getHexString(messageLen + terminalVersion + messageVersion + transactionCode + terminalCERT + checkSUM);

        onLineNow = false;      // Paul_20181119
        TPDU = CardPrefix.getTPDU(context, "TMS");
        packageAndSend(TPDU, "0800", mBlockDataSend);
    }

    public void setAmountforContactless(String amountInterface) {
        AMOUNT = amountInterface;
    }

    public void sendMessege() {
        packageAndSend(TPDU, MESSAGE_SALE, mBlockDataSend);
    }


    public void setmakeDataEPS(String cardNo, String pin, String ref1, String ref2, String ref3, String comCode) {
        if (cardNo.isEmpty()) {
            cardNo = card.getNo();
        }


//        REF1 = ref1;
//        REF2 = ref2;
//        REF3 = ref3;

        Log.d(TAG, "setmakeDataEPS");
// Paul_20181105 AXA Option
//            if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
        // I don't know Paul_20181015 K.Sinn_20181014  <---  way4 ,axa implement KEY_SERVICE_CODE_PIN_ID
        if (Preference.getInstance(context).getValueString(Preference.KEY_SERVICE_PIN_ID).equalsIgnoreCase("1"))
            //mBlockDataSend[52 - 1] = keyPin;


        //SINN 20180925 Add WAY4
//            if (Preference.getInstance(context).getValueString(Preference.KEY_WAY4_ID).equalsIgnoreCase("1")) {
        if (Preference.getInstance(context).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {   //SINNN  20181106 arrange parameter control//                String mBlock63 = "0062" + "RF"+ CardPrefix.calSpenLen(REF1, 20) + CardPrefix.calSpenLen(REF2, 20) + CardPrefix.calSpenLen(REF3, 20);
            String mBlock63 = "RF" + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF1), 20)
                    + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF2), 20)
                    + CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF3), 20);
            mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length() + 2), 4) + CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);
        }

//        //SINN 20180113 railway keyin
//        if (Preference.getInstance( context ).getValueString( Preference.KEY_SERVICE_PIN_ID ).equalsIgnoreCase( "1" )&&
//                Preference.getInstance( context ).getValueString( Preference.KEY_RAILWAY_ID ).equalsIgnoreCase( "1" ))
//        {
//            if(szPIN.length()>=4)
//                packageAndSend(TPDU, MESSAGE_SALE, mBlockDataSend);
//        }
    }

    private void setOnlineUploadCredit(String mBlock55,
                                       String mBlock22, Double amountFee) {

        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Log.d(TAG, "setTCUpload setOnlineUploadCredit: " + HOST_CARD);
        Date date1 = new Date();
        String time = new SimpleDateFormat("HHmmss").format(date1);
        String dateTime = new SimpleDateFormat("MMdd").format(date1);
        MERCHANT_NUMBER = CardPrefix.getMerchantId(context, "TMS");
        TERMINAL_ID = CardPrefix.getTerminalId(context, "TMS");
        String f22 = mBlock22;
        /*if (HOST_CARD.equalsIgnoreCase("EPS")) {
            f22 = "0052";
        } else if (HOST_CARD.equalsIgnoreCase("POS")) {
            f22 = "0051";
        } else {
            f22 = "0022";
        }*/
        mBlockDataSend = new String[64];
//        mBlockDataSend[2 - 1] = card.getNo().length() + card.getNo();
        if ((card.getNo().length() % 2) != 0) {
            mBlockDataSend[2 - 1] = card.getNo().length() + card.getNo() + "0";
        } else {
            mBlockDataSend[2 - 1] = card.getNo().length() + card.getNo();
        }
        mBlockDataSend[3 - 1] = "490000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Double.valueOf(AMOUNT) + amountFee));
        mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceId(context, "TMS"));
        stTraceId = mBlockDataSend[11 - 1];
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
        System.out.printf("utility:: %s setOnlineUploadCredit F38 = %s\n", TAG, F38);
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
        invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        String msgLen = "00000390";
        String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String msgV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionC = "8056";
        String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, "TMS"), 8);
        String transactionNo = "00000000";

        //SINN 20181212  490000 need comcode.
        String comCode = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_TAG_1001), 10);
//        String comCode = CardPrefix.calSpenLen(COMCODE, 10);
        String ref1 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF1), 50);
        String ref2 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF2), 50);
        String ref3 = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_REF3), 50);
        Log.d(TAG, "setOnlineUploadCredit: " + " comCode : " + comCode + " \n ref1 : " + ref1 + " \n ref2 : " + ref2 + "\n ref 3 : " + ref3);
//        String dateTime63 = new SimpleDateFormat("yyyyMMddHHmmss").format(date1);
        String dateTime63 = dateTimeOnline;
        Log.d(TAG, "dateTimeOnline: " + dateTimeOnline);
        StringBuilder cardStringBuilder = new StringBuilder(card.getNo());
        cardStringBuilder.replace(7, 12, "X");
        String cardNo = card.getNo();
        String feeAmount = CardPrefix.calLen(decimalFormat.format(amountFee).replace(".", ""), 10);

//        Double feeDou = Preference.getInstance(context).getValueDouble( Preference.KEY_FEE);  //20180815 SINN FIX FEE

        Double feeDou = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
        if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
            feeDou = 0.0;


        String feeRate = CardPrefix.calLen(String.valueOf(decimalFormat.format(feeDou)).replace(".", ""), 4);
        Log.d(TAG, "setOnlineUploadCredit feeRate : " + feeRate);
        String feeType = "F";
        String terId = CardPrefix.getTerminalId(context, HOST_CARD);
        String tex = TAX_ABB_NEW;
        Log.d(TAG, "setOnlineUploadCredit TAX_ABB_NEW : " + TAX_ABB_NEW);
        String posID = CardPrefix.calSpenLen(Preference.getInstance(context).getValueString(Preference.KEY_POS_ID), 20);
        String merchantID = CardPrefix.getMerchantId(context, HOST_CARD);
        String texId = Preference.getInstance(context).getValueString(Preference.KEY_TAX_ID);
        String random = CardPrefix.calSpenLen("", 2);
        String terminalCERT = CardPrefix.calSpenLen("", 14);
        String checkSUM = CardPrefix.calSpenLen("", 8);


        String cardStar = card.getNo().substring(0, 6);
//        String cardEnd = card.getNo().substring(12, 16);
        String cardEnd;
        if (card.getNo().length() >= 16) {
            cardEnd = card.getNo().substring(12, 16);
        } else {
            cardEnd = card.getNo().substring(12, card.getNo().length());
            for (int i = card.getNo().length(); i < 16; i++) {
                cardEnd += "0";
            }
        }

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

    public void setDataSettlementAndSendEPS() {
        System.out.printf("utility:: %s setDataSettlementAndSendEPS start\n", TAG);
        HOST_CARD = "EPS";
//        setUploadCredit();
        String traceIdNo;
        try {
            if (realm == null) {
                Log.d(TAG, "1919_setDataSettlementAndSendEPS");
                realm = Realm.getDefaultInstance();
            }
            int timeCount = 0;
            double amountAll = 0;
            DecimalFormat decimalFormat = new DecimalFormat("###0.00"); // Paul_20190128
//            Double fee = Preference.getInstance(context).getValueDouble( Preference.KEY_FEE);   //20180815 SINN FIX FEE

            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            RealmResults<TransTemp> transTempVoidFlag = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
            if (transTempVoidFlag.size() != 0) {
                timeCount = transTempVoidFlag.size();
                for (int i = 0; i < transTempVoidFlag.size(); i++) {
                    System.out.printf("utility:: %s YYYYYYYYYYYYYYYYYYY 0000 getFee = %s \n", TAG, transTempVoidFlag.get(i).getFee());
                    System.out.printf("utility:: %s YYYYYYYYYYYYYYYYYYY 0000 getAmount = %s \n", TAG, transTempVoidFlag.get(i).getAmount());
                    Double fee = Double.valueOf(transTempVoidFlag.get(i).getFee());  //20180815 SINN FIX FEE
//                    amountAll += Double.valueOf(transTempVoidFlag.get(i).getAmount()) + ((Double.valueOf(transTempVoidFlag.get(i).getAmount()) * fee) / 100); // Paul_20190128
                    amountAll += Double.valueOf(transTempVoidFlag.get(i).getAmount()) + fee;
                }
            }
            System.out.printf("utility:: YYYYYYYYYYYYYYYYYYY 00001 amountAll = %f\n", amountAll);
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
            System.out.printf("utility:: YYYYYYYYYYYYYYYYYYY 00002 amountAll = %f\n", amountAll);
            System.out.printf("utility:: YYYYYYYYYYYYYYYYYYY 00003 amountAll = %s\n", decimalFormat.format(amountAll));
            System.out.printf("utility:: YYYYYYYYYYYYYYYYYYY 00004 timeCount = %d\n", timeCount);
            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, decimalFormat.format(amountAll));
            System.out.printf("utility:: YYYYYYYYYYYYYYYYYYY 00005 amountAll = %f\n", amountAll);
            settlement63 = mBlockDataSend[63 - 1];
            MTI = MESSAGE_SETTLEMENT;
            onLineNow = false;
            Log.d(TAG, "mBlockDataSend[3 - 1]: " + SETTLEMENT_PROCESSING_CODE
                    + "\n mBlockDataSend[11 - 1]:" + BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo))
                    + "\n mBlockDataSend[24 - 1]: 0245"
                    + "\n mBlockDataSend[41 - 1]:" + BlockCalculateUtil.getHexString(TERMINAL_ID)
                    + "\n mBlockDataSend[42 - 1]:" + BlockCalculateUtil.getHexString(MERCHANT_NUMBER)
                    + "\n mBlockDataSend[60 - 1]:" + getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber))
                    + "\n mBlockDataSend[63 - 1]:" + BlockCalculateUtil.get63BlockData(timeCount, decimalFormat.format(amountAll))
            );
            TPDU = CardPrefix.getTPDU(context, HOST_CARD);
            // Paul_20190122
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    packageAndSend(TPDU, MTI, mBlockDataSend);
                    packageAndSend(TPDU, "0500", mBlockDataSend);   // Paul_20190122
                }
            }, insettimewait);

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    public void setDataSettlementAndSend(String typeHost) {
        System.out.printf("utility:: %s setDataSettlementAndSend Host = %s start\n", TAG, typeHost);
        HOST_CARD = typeHost;
//        setUploadCredit();
        String traceIdNo;
        try {
            if (realm == null) {
                Log.d(TAG, "1919_setDataSettlementAndSend");
                realm = Realm.getDefaultInstance();
            }
            int timeCount = 0;
            double amountAll = 0;

            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
//            Double fee = Preference.getInstance(context).getValueDouble( Preference.KEY_FEE);   //20180815 SINN FIX FEE

            traceIdNo = CardPrefix.geTraceId(context, HOST_CARD);
            RealmResults<TransTemp> transTempVoidFlag = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
            if (transTempVoidFlag.size() != 0) {
                timeCount = transTempVoidFlag.size();
                for (int i = 0; i < transTempVoidFlag.size(); i++) {

                    Double fee = Double.valueOf(transTempVoidFlag.get(i).getFee());  //20180815 SINN FIX FEE
//                    amountAll += Float.valueOf(transTempVoidFlag.get(i).getAmount()) + ((Double.valueOf(transTempVoidFlag.get(i).getAmount()) * fee) / 100);
                    amountAll += Double.valueOf(transTempVoidFlag.get(i).getAmount()) + fee; // Paul_20190128
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
            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, decimalFormat.format(amountAll));
            settlement63 = mBlockDataSend[63 - 1];
            onLineNow = false;
            TPDU = CardPrefix.getTPDU(context, HOST_CARD);
            // Paul_20190122
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    packageAndSend(TPDU, MESSAGE_SETTLEMENT, mBlockDataSend);
                }
            }, insettimewait);

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    public void setDataSettlementAndSendTMS() {
        System.out.printf("utility:: %s setDataSettlementAndSendTMS start\n", TAG);
        HOST_CARD = "TMS";
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String traceIdNo;
        try {
            if (realm == null) {
                Log.d(TAG, "1919_setDataSettlementAndSendTMS");
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
                    amountAll += Double.valueOf(transTempVoidFlag.get(i).getAmount());   // Paul_20190128
                }
            }
            RealmResults<TransTemp> transTempVoidYFlag = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("hostTypeCard", HOST_CARD).findAll();
            if (transTempVoidYFlag.size() != 0) {
                voidCount = transTempVoidYFlag.size();
                for (int i = 0; i < transTempVoidYFlag.size(); i++) {
                    amountVoidAll += Double.valueOf(transTempVoidYFlag.get(i).getAmount());  // Paul_20190128
                }
            }
            String nii = "";
            nii = Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
            String batchNumber = CardPrefix.getBatch(context, HOST_CARD);
            MERCHANT_NUMBER = CardPrefix.getMerchantId(context, HOST_CARD);
            TERMINAL_ID = CardPrefix.getTerminalId(context, HOST_CARD);


            int countPayAll = (payCount + voidCount);

            String msgLen = "00000121";
            String terVer = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
            String msgVer = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
            String tranCode = "6012";
            String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, HOST_CARD), 8);
            String transaction = CardPrefix.calLen(String.valueOf((countPayAll + voidCount)), 5);
//            String totalPayCount = CardPrefix.calLen(String.valueOf(payCount), 5);
            String totalPayCount = CardPrefix.calLen(String.valueOf(countPayAll), 5);

//            String amountPayAllToStr = String.format("%.2f", amountAll).replace(".", "");
            String amountPayAllToStr = decimalFormat.format(amountAll + amountVoidAll).replace(".", "");

            String totalPayAmount = CardPrefix.calLen(String.valueOf(amountPayAllToStr), 10);

//            String totalVoidCount = CardPrefix.calLen("", 5);
            String totalVoidCount = CardPrefix.calLen(String.valueOf(voidCount), 5);

            String amountVoidAllToStr = decimalFormat.format(amountVoidAll).replace(".", "");

            String totalVoidAmount = CardPrefix.calLen(String.valueOf(amountVoidAllToStr), 10);

//            String totalVoidAmount = CardPrefix.calLen("", 10);
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
            Log.d(TAG, "setDataSettlementAndSendTMS: " + mBlockDataSend[63 - 1]);
            settlement61 = mBlockDataSend[61 - 1];
            settlement63 = mBlockDataSend[63 - 1];
            MTI = MESSAGE_SETTLEMENT;
            onLineNow = false;
            TPDU = CardPrefix.getTPDU(context, HOST_CARD);
            // Paul_20190122
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    packageAndSend(TPDU, MTI, mBlockDataSend);
                    packageAndSend(TPDU, "0500", mBlockDataSend);   // Paul_20190122
                }
            }, insettimewait);

        } finally {
            if (realm != null) {
                realm.close();
                realm = null;
            }
        }
    }

    private void updateTransactionDe11Online() {
        Log.d(TAG, "1919_updateTransactionDe11Online");
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TransTemp transTemp = realm.where(TransTemp.class).equalTo("id", saleId).findFirst();
                    if (transTemp != null) {
                        transTemp.setDe11OnlineTMS(stTraceId);
                        realm.copyFromRealm(transTemp);
                    }
                }
            });
        } finally {
            realm.close();
            realm = null;   // Paul_20181026 Some time DB Read error solved
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

    private void setOnlineUploadCreditVoid(String mBlock55,
                                           String mBlock22, String amountFee) {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        Log.d(TAG, "setTCUpload: " + HOST_CARD);
        Double fee = Preference.getInstance(context).getValueDouble(Preference.KEY_FEE);
//        Double amountFee1 = (Double.valueOf(AMOUNT) * fee) / 100;

        Double amountFee1;
        if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
            amountFee1 = (Double.valueOf(AMOUNT) + fee);
        else
            amountFee1 = (Double.valueOf(AMOUNT) * fee) / 100;
        amountFee1 = (int) (amountFee1 * 100 + 0.5) / 100.0;             // Paul_20190129


//   20181003 SINN fix database fail.
        Log.d(TAG, "HOST_CARD:" + HOST_CARD + " Ecr_NO:" + Ecr_NO);

        TransTemp transTemp; // Database
        int ii = Preference.getInstance(context).getValueInt(Preference.KEY_SET_ID);
        System.out.printf("utility:: updateTransactionVoid transTemp.getId() iiiiiiii = %d \n", ii);

        // if (realm == null) {
        System.out.printf("utility:: updateTransactionVoid 000000099 \n");
        Log.d(TAG, "1919_updateTransactionVoid");
        realm = Realm.getDefaultInstance();
        //  }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            }
        });
        realm.close();
        realm = null;

        realm = Realm.getDefaultInstance();

        System.out.printf("utility:: updateTransactionVoid 000000100 \n");
        transTemp = realm.where(TransTemp.class).equalTo("id", ii).findFirst();
        //20181003 SINN fix database fail.


        Log.d(TAG, "usetOnlineUploadCreditVoid TransTemp: " + transTemp);


//        Log.d(TAG, "setOnlineUploadCreditVoid batchUpload : " + batchUpload + " transTemp.size() : " + transTemp1.size()+" (batchUpload).getCardNo()"+transTemp1.get(batchUpload).getCardNo());
//        Log.d(TAG, "setOnlineUploadCreditVoid batchUpload : " + batchUpload +" (batchUpload).getCardNo()"+transTemp.getCardNo());


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
//        mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo();
        if ((transTemp.getCardNo().length() % 2) != 0) {
            mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo() + "0";
        } else {
            mBlockDataSend[2 - 1] = transTemp.getCardNo().length() + transTemp.getCardNo();
        }
        mBlockDataSend[3 - 1] = "490000";
        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amountAll));
        mBlockDataSend[11 - 1] = calNumTraceNo(CardPrefix.geTraceId(context, "TMS"));
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
//        System.out.printf("utility:: %s setOnlineUploadCreditVoid F38 = %s\n",TAG,F38);
//        mBlockDataSend[38 - 1] = F38;
        mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(transTemp.getApprvCode());      // Paul_20190121 field38 receive if null
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
        invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(invoiceNumber));
        String msgLen = "00000390";
        String terminalV = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_VERSION);
        String msgV = Preference.getInstance(context).getValueString(Preference.KEY_MESSAGE_VERSION);
        String transactionC = "8065";
        String batchNo = CardPrefix.calLen(CardPrefix.getBatch(context, "TMS"), 8);
        String transactionNo = CardPrefix.calLen(transTemp.getDe11OnlineTMS(), 8);
        String comCode = CardPrefix.calSpenLen(transTemp.getComCode(), 10);
        String ref1 = CardPrefix.calSpenLen(transTemp.getRef1(), 50);
        String ref2 = CardPrefix.calSpenLen(transTemp.getRef2(), 50);
        String ref3 = CardPrefix.calSpenLen(transTemp.getRef3(), 50);
        Log.d(TAG, "setOnlineUploadCredit: " + " comCode : " + comCode + " \n ref1 : " + ref1 + " \n ref2 : " + ref2 + "\n ref 3 : " + ref3);
//        String dateTime63 = new SimpleDateFormat("yyyyMMddHHmmss").format(date1);
        String dateTime63 = transTemp.getTransDate() + transTemp.getTransTime().replace(":", "");
        StringBuilder cardStringBuilder = new StringBuilder(transTemp.getCardNo());
        cardStringBuilder.replace(7, 12, "X");
        String cardNo = transTemp.getCardNo();
        String feeAmount = CardPrefix.calLen(decimalFormat.format(Double.valueOf(amountFee1)).replace(".", ""), 10);
        Log.d(TAG, "setOnlineUploadCreditVoid Fee Amount : " + feeAmount);
//        String feeRate = CardPrefix.calLen(decimalFormat.format( Preference.getInstance(context).getValueDouble( Preference.KEY_FEE)).replace(".", ""), 4);

        String feeRate;
        if (Preference.getInstance(context).getValueString(Preference.KEY_FixRATE_ID).equalsIgnoreCase("1"))    //20180815 SINN FIX FEE
            feeRate = "0000";
        else
            feeRate = CardPrefix.calLen(decimalFormat.format(Preference.getInstance(context).getValueDouble(Preference.KEY_FEE)).replace(".", ""), 4);


        String feeType = "F";
        String terId = CardPrefix.getTerminalId(context, HOST_CARD);
        String tex = transTemp.getTaxAbb();
        String posID = CardPrefix.calSpenLen("2222", 20);
        String merchantID = CardPrefix.getMerchantId(context, HOST_CARD);
        String texId = Preference.getInstance(context).getValueString(Preference.KEY_TAX_ID);
        String random = CardPrefix.calSpenLen("", 2);
        String terminalCERT = CardPrefix.calSpenLen("", 14);
        String checkSUM = CardPrefix.calSpenLen("", 8);


        String cardStar = transTemp.getCardNo().substring(0, 6);
        String cardEnd;
        if (transTemp.getCardNo().length() >= 16) {
            cardEnd = transTemp.getCardNo().substring(12, 16);
        } else {
            cardEnd = transTemp.getCardNo().substring(12, transTemp.getCardNo().length());
            for (int i = transTemp.getCardNo().length(); i < 16; i++) {
                cardEnd += "0";
            }
        }

        Log.d(TAG, "setOnlineUploadCredit: " + transTemp.getCardNo() + " \n cardStar : " + cardStar + " \n cardEnd : " + cardEnd);

        String f63 = msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 +
                ref2 + ref3 + dateTime63 + cardNo + feeAmount + feeRate + feeType + terId + tex + posID + merchantID + texId + random + terminalCERT + checkSUM;
        String f63Start = BlockCalculateUtil.getHexString(msgLen + terminalV + msgV + transactionC + batchNo + transactionNo + comCode + ref1 +
                ref2 + ref3 + dateTime63 + cardStar) + "585858585858";
        String f63End1 = BlockCalculateUtil.getHexString(cardEnd + feeAmount + feeRate) + "50";
        String f63End2 = BlockCalculateUtil.getHexString(terId + tex + posID + merchantID + texId + random + terminalCERT + checkSUM);

        String f63All = f63Start + f63End1 + f63End2;
        mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(f63.length()), 4) + f63All;


//        onLineNow = true;
        onLineNow = false;  //S20181003  SINN void no need pboc read ICC
        TPDU = CardPrefix.getTPDU(context, "TMS");
        packageAndSend(TPDU, "0320", mBlockDataSend);
    }

    private boolean CheckBL(String Pan){
        boolean is_BL = false;
        Realm realm = Realm.getDefaultInstance();
        BL bl = realm.where(BL.class).equalTo("PAN", Pan).findFirst();
        if (bl != null) {
            is_BL = true;
        }
        realm.close();
        return is_BL;
    }
}
