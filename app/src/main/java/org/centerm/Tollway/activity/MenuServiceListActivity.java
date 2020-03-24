package org.centerm.Tollway.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.customerdisplay.AidlCustomerdisplay;
import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.serialport.AidlSerialPort;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.HexUtil;
import com.centerm.smartpos.util.Util;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.jemv.clcommon.EMV_APPLIST;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.device.DeviceManager;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.CustomProgressDialog;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.SettingForUserActivity;
import org.centerm.Tollway.activity.menuvoid.VoidActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.activity.qr.CheckQrActivity;
import org.centerm.Tollway.activity.qr.GenerateQrActivity;
import org.centerm.Tollway.activity.qr.MenuQrActivity;
import org.centerm.Tollway.activity.settlement.MenuSettlementActivity;
import org.centerm.Tollway.adapter.MenuServiceAdapter;
import org.centerm.Tollway.adapter.SlipSummaryReportCardAdapter;
import org.centerm.Tollway.alipay.AliConfig;
import org.centerm.Tollway.alipay.CryptoServices;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.database.ReversalHealthCare;
import org.centerm.Tollway.database.ReversalTemp;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.healthcare.activity.CalculateHelthCareActivityNew;
import org.centerm.Tollway.healthcare.activity.IDActivity;
import org.centerm.Tollway.healthcare.activity.IDActivity2;
import org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity;
import org.centerm.Tollway.healthcare.baseavtivity.BaseHealthCardActivity;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.helper.myCardReaderHelper;
import org.centerm.Tollway.model.Card;
import org.centerm.Tollway.pax.ActionSearchCard;
import org.centerm.Tollway.pax.DeviceImplNeptune;
import org.centerm.Tollway.pax.EUIParamKeys;
import org.centerm.Tollway.service.OtherDetectCard;
import org.centerm.Tollway.service.serviceReadType;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.lang.Math.floor;
import static org.centerm.Tollway.activity.CalculatePriceActivity.readerType;
import static org.centerm.Tollway.activity.MenuServiceActivity.KEY_TYPE_PASSWORD;
import static org.centerm.Tollway.activity.MenuServiceActivity.TYPE_ADMIN_PASSWORD;
import static org.centerm.Tollway.activity.MenuServiceActivity.TYPE_NORMAL_PASSWORD;
import static org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity.KEY_ID_CARD_CD;
import static org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity.KEY_ID_FOREIGNER_NUMBER;

//import android.support.v7.app.AlertDialog;


public class MenuServiceListActivity extends BaseHealthCardActivity {

    private final String TAG = this.getClass().getName();

    private Realm realm = null;

    public static final String MSG_CARD = "MSG_CARD";
    public static final String IC_CARD = "IC_CARD";
    public static final String RF_CARD = "RF_CARD";

    public static final String KEY_CARD = "key_card";
    public static final String KEY_TYPE_CARD = MenuSettlementActivity.class.getName() + "key_type_card";
    public static final String KEY_TYPE_OFFLINE = "key_type_offline";
    /**
     * PosInterface
     */
    public static final String KEY_TYPE_INTERFACE = MenuServiceListActivity.class.getName() + "_key_type_interface";
    public static final String KEY_INTERFACE_AMOUNT = MenuServiceListActivity.class.getName() + "_key_interface_amount";
    public static final String KEY_INTERFACE_CARDHOLDER = MenuServiceListActivity.class.getName() + "_key_interface_cardholder";
    public static final String KEY_INTERFACE_CARD_ID_CHILD = MenuServiceListActivity.class.getName() + "_key_interface_card_id_child";
    public static final String KEY_INTERFACE_CARD_ID_FOREIGNER = MenuServiceListActivity.class.getName() + "_key_interface_card_id_foreigner";
    public static final String KEY_INTERFACE_VOID_INVOICE_NUMBER = MenuServiceListActivity.class.getName() + "_key_interface_void_invoice_number";
    // Paul_20180716
    public static final String KEY_INTERFACE_VOID_APPROVAL_CODE = MenuServiceListActivity.class.getName() + "_key_interface_void_approval_code";

    public static final String KEY_INTERFACE_APP = MenuServiceListActivity.class.getName() + "_key_interface_app";
    public static final String KEY_INTERFACE_TC = MenuServiceListActivity.class.getName() + "_key_interface_tc";
    public static final String KEY_INTERFACE_AID = MenuServiceListActivity.class.getName() + "_key_interface_aid";

    //SINN  rs232 20180705 add interface
    public static final String KEY_INTERFACE_REF1 = MenuServiceListActivity.class.getName() + "_key_interface_ref1";
    public static final String KEY_INTERFACE_REF2 = MenuServiceListActivity.class.getName() + "_key_interface_ref2";
    public static final String KEY_INTERFACE_REF3 = MenuServiceListActivity.class.getName() + "_key_interface_ref3";
    //END rs232 20180705 add interface
    public static final String KEY_INTERFACE_F1_POS_MSG = MenuServiceListActivity.class.getName() + "_key_interface_F1_POS_MSG";  ////SINN 20180710 POS REPRINT

    public static final String KEY_INTERFACE_TYPE = MenuServiceListActivity.class.getName() + "_key_interface_type";    // Paul_20181019

    private RecyclerView recyclerViewMenuList = null;
    private MenuServiceAdapter menuServiceAdapter = null;
    private ArrayList<String> nameMenuList = null;
    private ArrayList<String> nameMenuList_org = null;
    private LinearLayout linearLayoutToolbarBottom = null;
    private Dialog dialogInsertCard = null;
    private Dialog dialogWaiting = null;
    private Dialog DialogSelect = null;
    private CountDownTimer timer = null;

    private CardManager cardManager = null;
    private Card cardNo;
    private Dialog dialogFallBack;
    private TextView msgLabeldialogFallBack;  //// Sinn 20181022 fallback TMS

    private boolean isFallBack = false;
    private TextView msgLabel;
    private Dialog dialogServiceCode;

    private String typeCard = null;
    private String typeClick = null;

    private int numFallBack = 0;
    private TextView msgFallBackLabel;
    private Button closeFallBackImage; //K.GAME 181012 change ImageView > Button
    private Button okBtn;
    private Dialog dialogFallBackCheck;
    private MediaPlayer mp = null;        // Paul_20180801
    private Dialog dialogCheckCard;
    private Dialog dialogCheckCardposinterface;
    private Button closeCardImage;//K.GAME 180924 New dialog ImageView > Button
    private Button okCardBtn;
    private Dialog dialogAlert;
    private TextView errormsgLabel;

    private Dialog dialogAlertAuto;
    private Dialog dialogInsertAmountForSale;
    private Dialog dialogSelectAmountForSale;
    private Dialog dialogContactless;

    private Dialog dialogHost;
    private Button posBtn;
    private Button epsBtn;
    private Button tmsBtn;
    //    private ImageView closeImage;//K.GAME 180828 change UI
    private Button closeImage;//K.GAME 180828 change UI
    private Dialog dialogPassword;
    private Dialog dialogPassword_settingUser;//K.GAME 180925
    private EditText passwordBox;
    private Button cancelBtn;

    private boolean isOffline = false;
    private boolean isSettingForUser = false;
    private String TERMINAL_ID;
    private String MERCHANT_NUMBER;
    private String[] mBlockDataSend;
    private String TPDU;
    private Button ghcBtn;
    private ReversalHealthCare reversalHealthCare;
    private AlertDialog.Builder builder;
    private PosInterfaceActivity posinterface;

    private int inTriger = 0; //sinn

    private String localtypeInterface = null;     // "";      // Paul_20180711

    private int inCntItemno = 0;
    // Paul_20180712 Start
    String amountInterface = null;      // Paul_20180711
    String cardCd = null;
    String idForeigner = null;
    String nocardCd = null;
    String invoiceId = null;

    String ref1 = null;
    String ref2 = null;
    String ref3 = null;
    String F1_POS_MSG = "     ";   ////sinn 20180712 add pos reprint
    String approvalCode = null;      // Paul_20180716
    // Paul_20180712 End

    //K.GAME 180904 Add Calculate ///////////////////////////////////////
    private EditText amountBox_new = null;
    private String typeInterface;

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
    private String amount_new;
    private LinearLayout numberLinearLayout;
    private TextView tv_confirm_amount;
    private String cardhlder = "";
    DecimalFormat decFormat; //20180812 SINN BIG AMOUNT

    private Dialog dialogcustomDialogPin_new;
    private ImageView img_krungthai1;//K.GAME 181016
    private ImageView img_krungthai2;//K.GAME 181016
    private EditText pinBox_new = null;//K.GAME 181017

    //END K.GAME 180904 Add Calculate ///////////////////////////////////////
    private AidlCustomerdisplay displayControlDev = null;
    private int GHCVoidFlg = 0;     // Paul_20180809

    //for multi Application
    private Button btnApp1;
    private Button btnApp2;
    private Button btnApp3;
    private Button btnApp4;
    private Button btnApp5;

    private boolean is_dialogInsertAmountShowing = false;

    //Update
    private Timer update_timer;
    private int timer_update;
    private TextView Title_btn1;
    private Dialog dialogUpdate = null;
    //    private Dialog dialogDownloadKey = null;
    private Button btn_later;
    private Button btn_now;
    //    private Button btn_later_tle;
//    private Button btn_now_tle;
    private CustomProgressDialog customProgressDialog = null;
    private Handler handler = null;
    private int progress_cnt = 0;
    private int progress_max = 100;
    private String step_update = "";

    //Settlement
    private View qrView;
    private LinearLayout settlementLinearLayout;
    private AidlPrinter printDev;
    private String hostflag = "";
    private ArrayList<TransTemp> transTemp_tms = null;
    private ArrayList<TransTemp> transTemp_eps = null;
    private ArrayList<QrCode> qrTemp = null;
    private ArrayList<QrCode> aliTemp = null;
    private ArrayList<QrCode> wecTemp = null;
    private View NormalView;
    private LinearLayout NormalsummaryLinearFeeLayout;
    private RecyclerView recyclerViewCardReportSummary;
    private NestedScrollView slipNestedScrollView = null;
    private LinearLayout settlementNormalLinearLayout;
    private TextView NormaldateLabel;
    private TextView NormaltimeLabel;
    private TextView NormalmidLabel;
    private TextView NormaltidLabel;
    private TextView NormalbatchLabel;
    private TextView NormalhostLabel;
    private TextView NormalmerchantName1NormalLabel;
    private TextView NormalmerchantName2NormalLabel;
    private TextView NormalmerchantName3NormalLabel;
    private TextView NormalsummarysaleCountLabel;
    private TextView NormalsummarysaleTotalLabel;
    private TextView NormalsummaryvoidSaleCountLabel;
    private TextView NormalsummaryvoidSaleAmountLabel;
    private TextView NormalsummarycardCountLabel;
    private TextView NormalsummarycardAmountLabel;
    private TextView NormaldateFeeLabel;
    private TextView NormaltimeFeeLabel;
    private TextView NormalbatchFeeLabel;
    private TextView NormalhostFeeLabel;
    private TextView NormalsaleCountFeeLabel;
    private TextView NormalsaleTotalFeeLabel;
    private TextView NormalvoidSaleCountFeeLabel;
    private TextView NormalvoidSaleAmountFeeLabel;
    private TextView NormalcardCountFeeLabel;
    private TextView NormalcardAmountFeeLabel;
    private TextView NormaltaxIdFeeLabel;
    private TextView NormalmerchantName1FeeLabel;
    private TextView NormalmerchantName2FeeLabel;
    private TextView NormalmerchantName3FeeLabel;
    private View hgcView;
    private LinearLayout settlementHgcLinearLayout;
    private TextView dateHgcLabel;
    private TextView timeHgcLabel;
    private TextView midHgcLabel;
    private TextView tidHgcLabel;
    private TextView batchHgcLabel;
    private TextView hostHgcLabel;
    private TextView saleCountHgcLabel;
    private TextView saleTotalHgcLabel;
    private TextView voidSaleCountHgcLabel;
    private TextView voidSaleAmountHgcLabel;
    private TextView cardCountHgcLabel;
    private TextView cardAmountHgcLabel;
    private TextView merchantName1HgcLabel;
    private TextView merchantName2HgcLabel;
    private TextView merchantName3HgcLabel;
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
    private TextView merchantName1Label;
    private TextView merchantName2Label;
    private TextView merchantName3Label;
    /**
     * TAX Fee
     */
    private View reportSummaryFeeView;
    private LinearLayout summaryLinearFeeLayout;
    private TextView merchantName1FeeLabel;
    private TextView merchantName2FeeLabel;
    private TextView merchantName3FeeLabel;
    private TextView dateFeeLabel;
    private TextView timeFeeLabel;
    private TextView midFeeLabel;
    private TextView tidFeeLabel;
    private TextView batchFeeLabel;
    private TextView hostFeeLabel;
    private TextView saleCountFeeLabel;
    private TextView saleTotalFeeLabel;
    private TextView voidSaleCountFeeLabel;
    private TextView voidSaleAmountFeeLabel;
    private TextView cardCountFeeLabel;
    private TextView cardAmountFeeLabel;
    private TextView taxIdFeeLabel;
    private int countAll;
    private Bitmap oldBitmap;
    private List<TransTemp> CardTypeDB = null;
    private SlipSummaryReportCardAdapter slipSummaryReportCardAdapter;
    private Dialog dialogOutOfPaper;
    private Button okPaperBtn;
    private int status = 0;
    //Body
    private String amt;
    private String token;
    private String deviceid;
    private String merid;
    private String storeid;
    private String record;
    private int endRecord;
    private SettlementLister settlementLister = null;
    private String page = "0";
    private int endPage;
    private String startDate;
    private String endDate;

    private String invoice = "";
    private String type = "";
    private String data = "";
    private String param1 = "";
    private String param2 = "";
    private int cntSale = 0;
    private int cntVoid = 0;
    private Double amountSale = 0.0;
    private Double amountVoid = 0.0;
    private Context context = null;
    private HttpsURLConnection urlConnection;
    private SSLContext sslContext;
    private URL url;

    private JSONObject jsonObject;
    private JSONObject jsonObject2;
    private CryptoServices cryptoServices;

    private Date dateTime;
    private DateFormat dateFormat;
    private DateFormat dateFormat2;
    private DateFormat dateFormat3;

    //Header
    private String reqBy;
    private String reqChannel = "EDC";
    private String reqChannelDtm;
    private String uniqueData;
    private String reqChannelRefId;
    private String service;
    public String ALIPAY_CER_PATH = ""; //20181115JEFF
    private String alipay_http;
    private String ali_status = "";
    private String result = "";

    private LinearLayout lineartranstype1, lineartranstype2;

    private static SoundPool soundPool;
    private static int[] sound;

    private String ratePriceStr[];

    String CARD_NO, NAMECARDHOLDER, AMOUNT;
    public static final String KEY_CALCUATE_ID = CalculatePriceActivity.class.getName() + "key_calcuate_id";
    public static final String KEY_TYPE_SALE_OR_VOID = CalculatePriceActivity.class.getName() + "key_type_sale_or_void";
    public static final String KEY_INTERFACE_CARDHOLDER_2 = CalculatePriceActivity.class.getName() + "_key_interface_cardholder_2";


    Boolean isInsertCard;

    //    Timer timerInsertCard;
    boolean isShowInsertCard;
    private static boolean isDialogShowInsertCardShowing;

    public static EReaderType readerType = null; // 读卡类型
    Intent iDetectCard;
    private byte mode; // 寻卡模式
    public static serviceReadType serReadType = serviceReadType.getInstance();
    String trackData1;
    String trackData2;
    String trackData3;
    private static MenuServiceListActivity instance;
    private static final int READ_CARD_CANCEL = 2; // 取消读卡
    private static final int READ_CARD_ERR = 3; // 读卡失败
    private boolean supportManual = false; // 是否支持手输

    public SearchCardThread searchCardThread;


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
        super.onCreate(savedInstanceState);
        instance = this;

        System.out.printf("utility:: MenuServiceListActivity onCreate \n");
        setContentView(R.layout.activity_menu_service_list);
        localtypeInterface = null;
        GHCVoidFlg = 0;
        is_dialogInsertAmountShowing = false;

//        timerInsertCard = new Timer();

        dateTime = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat3 = new SimpleDateFormat("D");

        String REF1 = Preference.getInstance(context).getValueString(Preference.KEY_BUS_C_STAFF_ID);
        String REF2 = "";
        String REF3 = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS) + makeUniqueData(dateTime);

        Preference.getInstance(context).setValueString(Preference.KEY_REF1, REF1);
        Preference.getInstance(context).setValueString(Preference.KEY_REF2, REF2);
        Preference.getInstance(context).setValueString(Preference.KEY_REF3, REF3);

        cardManager = MainApplication.getCardManager();
        /*try {
            cardManager.initClssTrans.run();
            cardManager.initClssTrans.join();
        } catch(Exception e) {
            e.printStackTrace();
        }*/
        serReadType.setrReadType(EReaderType.DEFAULT.getEReaderType());


        loadParam();

        DeviceManager.getInstance().setIDevice( DeviceImplNeptune.getInstance());
        serReadType.setrReadType(EReaderType.DEFAULT.getEReaderType());

        printDev = cardManager.getInstancesPrint();


        initWidget();

        soundsetting();

        //Update
        customDialogOutOfPaper();
        setViewSettlementNormal();
        setSettlementListener();
//        setCheckUpdate();
//        updatefunction_bmta();
//        cardManager.checkUpdate();

        posinterface = MainApplication.getPosInterfaceActivity();
        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_RS232_Enable_ID).equalsIgnoreCase("1")) {
            PosInterfaceThread();
        }


        iDetectCard = new Intent(this, OtherDetectCard.class);
        iDetectCard.putExtra("readType", readerType.getEReaderType());
        iDetectCard.putExtra("iccSlot", (byte) 0);
        startService(iDetectCard);

        //接收器的动态注册，Action必须与Service中的Action一致

        registerReceiver(br, new IntentFilter("ACTION_DETECT"));
        searchCardThread = new SearchCardThread();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isInsertCard = bundle.getBoolean("InsertCard");


            if (isInsertCard) {
//                startInsertCard();
//                Rf_Handler.sendEmptyMessageDelayed(0, 2000);

                startInsertCard();

/*
                numberPrice = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
                amountInterface = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
*/
                if (checkAllBatch() != 1)    // Paul_20180803
                    CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                //SINN 20181212  check first settlement
//                String valueParameterEnable = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1000);  //para enable
//                if (valueParameterEnable.isEmpty()) {
//                    Utility.customDialogAlert(MenuServiceListActivity.this, "กรุณา First Settlement ก่อนทำรายการ", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                        }
//                    });
//                    break;
//                }
                ////SINN 20181129 Add way2 to UAT6
                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SERVICE_PIN_ID, "0");

//                            numberPrice = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
//                            amountInterface = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
//                            if (checkAllBatch() != 1)    // Paul_20180803
//                                CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                GHCVoidFlg = 0;
                if (checkBatchSettlement() != 1) {       // Paul_20180803
                    System.out.printf("utility:: %s setMenuList 001 \n", TAG);
                    cardManager.setFalseFallbackHappen();
                    System.out.printf("utility:: %s setMenuList 002 \n", TAG);
                    if (checkReversal("SALE")) {

                        System.out.printf("utility:: %s setMenuList 003 \n", TAG);
//                                    if (!dialogSelectAmountForSale) {           // Paul_20181205 K.hong double click wrong
                        is_dialogInsertAmountShowing = true;
//                                        customDialog_InsertAmount();//K.GAME 180914 New dialog
//                                        custoDialog_SelectAmount();


//                                    }//                                    startInsertCard();
                    } else {
                        myCardReaderHelper.getInstance().stopPolling();
                    }
                }


            }
        }


    }

    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180919
        View view = dialogOutOfPaper.getLayoutInflater().inflate(R.layout.dialog_custom_printer, null);//K.GAME 180919
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180919
        dialogOutOfPaper.setContentView(view);//K.GAME 180919
        dialogOutOfPaper.setCancelable(false);//K.GAME 180919

        okPaperBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okPaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //doPrinting(oldBitmap);
                dialogOutOfPaper.dismiss();
            }
        });
    }

    private void setViewSettlementNormal() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NormalView = inflater.inflate(R.layout.view_slip_card_settlement, null);

        NormalsummaryLinearFeeLayout = NormalView.findViewById(R.id.summaryLinearLayout);
        recyclerViewCardReportSummary = NormalView.findViewById(R.id.recyclerViewReportSettlement);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        recyclerViewCardReportSummary.setLayoutManager(layoutManager2);

        slipNestedScrollView = NormalView.findViewById(R.id.slipNestedScrollView);
        settlementNormalLinearLayout = NormalView.findViewById(R.id.settlementLinearLayout);
        NormaldateLabel = NormalView.findViewById(R.id.dateLabel);
        NormaltimeLabel = NormalView.findViewById(R.id.timeLabel);
        NormalmidLabel = NormalView.findViewById(R.id.midLabel);
        NormaltidLabel = NormalView.findViewById(R.id.tidLabel);
        NormalbatchLabel = NormalView.findViewById(R.id.batchLabel);
        NormalhostLabel = NormalView.findViewById(R.id.hostLabel);
        NormalmerchantName1NormalLabel = NormalView.findViewById(R.id.merchantName1Label);
        NormalmerchantName2NormalLabel = NormalView.findViewById(R.id.merchantName2Label);
        NormalmerchantName3NormalLabel = NormalView.findViewById(R.id.merchantName3Label);

        NormalsummarysaleCountLabel = NormalView.findViewById(R.id.saleCountLabel);
        NormalsummarysaleTotalLabel = NormalView.findViewById(R.id.saleTotalLabel);
        NormalsummaryvoidSaleCountLabel = NormalView.findViewById(R.id.voidSaleCountLabel);
        NormalsummaryvoidSaleAmountLabel = NormalView.findViewById(R.id.voidSaleAmountLabel);
        NormalsummarycardCountLabel = NormalView.findViewById(R.id.cardCountLabel);
        NormalsummarycardAmountLabel = NormalView.findViewById(R.id.cardAmountLabel);

        NormalmerchantName1FeeLabel = NormalView.findViewById(R.id.merchantName1TaxLabel);
        NormalmerchantName2FeeLabel = NormalView.findViewById(R.id.merchantName2TaxLabel);
        NormalmerchantName3FeeLabel = NormalView.findViewById(R.id.merchantName3TaxLabel);


        NormaldateFeeLabel = NormalView.findViewById(R.id.dateTaxLabel);
        NormaltimeFeeLabel = NormalView.findViewById(R.id.timeTaxLabel);
        NormalbatchFeeLabel = NormalView.findViewById(R.id.batchTaxLabel);
        NormalhostFeeLabel = NormalView.findViewById(R.id.hostTaxLabel);
        NormalsaleCountFeeLabel = NormalView.findViewById(R.id.saleCountTaxLabel);
        NormalsaleTotalFeeLabel = NormalView.findViewById(R.id.saleTotalTaxLabel);
        NormalvoidSaleCountFeeLabel = NormalView.findViewById(R.id.voidSaleCountTaxLabel);
        NormalvoidSaleAmountFeeLabel = NormalView.findViewById(R.id.voidSaleAmountTaxLabel);
        NormalcardCountFeeLabel = NormalView.findViewById(R.id.cardCountTaxLabel);
        NormalcardAmountFeeLabel = NormalView.findViewById(R.id.cardAmountTaxLabel);
        NormaltaxIdFeeLabel = NormalView.findViewById(R.id.taxIdLabel);
    }

    private void setSettlementListener() {

        settlementLister = new SettlementLister() {
            @Override
            public void onSuccess() {
                deleteDB();
                System.out.printf("utility:: onCreate onSuccess \n");
            }

            @Override
            public void onContinue() {
                System.out.printf("utility:: onCreate onContinue \n");
                if (page.equals(String.valueOf(endPage))) {
                    deleteDB();
                } else {
                    page = String.valueOf(Integer.parseInt(page) + 1);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                if (realm == null)
                                    realm = Realm.getDefaultInstance();

                                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("hostTypeCard", "ALIPAY").equalTo("voidflag", "N").findAll();

                                settlement2(saleTemp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onFail() {
                System.out.printf("utility:: Alipay Settlement  Fail \n");
            }
        };

        cardManager.setSettlementHelperLister(new CardManager.SettlementHelperLister() {
            @Override
            public void onSettlementSuccess() {
                if (dialogWaiting.isShowing())
                    dialogWaiting.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customDialogAlertSuccess();
                    }
                });
            }

            @Override
            public void onCloseSettlementFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        okBtn.setVisibility(View.GONE);
                        Utility.customDialogAlert(MenuServiceListActivity.this, "ทำรายการไม่สำเร็จ 95", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                if (dialogWaiting != null)
                                    dialogWaiting.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }

    public void customDialogAlertSuccess() {
        final Dialog dialogAlert = new Dialog(MenuServiceListActivity.this, R.style.ThemeWithCorners);
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_success_, null);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(view);
        dialogAlert.setCancelable(false);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);

        if (step_update.equals("TMS")) {
            msgLabel.setText("On-Us Settlement Success");
        } else if (step_update.equals("EPS")) {
            msgLabel.setText("Off-Us Settlement Success");
        } else if (step_update.equals("QR")) {
            msgLabel.setText("QR Settlement Success");
        } else if (step_update.equals("ALI")) {
            msgLabel.setText("ALIPAY Settlement Success");
        } else if (step_update.equals("WEC")) {
            msgLabel.setText("WECHATPAY Settlement Success");
        } else if (step_update.equals("TLE")) {
            msgLabel.setText("TLE Download");
        } else if (step_update.equals("FSS")) {
            msgLabel.setText("First Settlement");
        } else {
            msgLabel.setText("อัพเดทข้อมูลสำเร็จ");
        }

        try {
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

        CountDownTimer timer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {

                if (step_update.equals("TMS")) {
                    step_update = "";
                    dialogAlert.dismiss();
                    dialogWaiting.show();
                    setViewNormalSlip("TMS");
                } else if (step_update.equals("EPS")) {
                    step_update = "";
                    dialogAlert.dismiss();
                    dialogWaiting.show();
                    setViewNormalSlip("EPS");
                } else if (step_update.equals("QR")) {
                    step_update = "";
                    dialogAlert.dismiss();
                    dialogWaiting.show();
                    selectSettlementQRAll();
                } else if (step_update.equals("ALI")) {
                    step_update = "";
                    dialogAlert.dismiss();
                } else if (step_update.equals("WEC")) {
                    step_update = "";
                    dialogAlert.dismiss();
                } else if (step_update.equals("TLE")) {
                    step_update = "";
                    dialogAlert.dismiss();
                    if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length() == 8) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogWaiting.show();
                            }
                        });
                        cardManager.setDataFirstSettlement();
                    } else
                        cardManager.updateFile();
                } else if (step_update.equals("FSS")) {
                    step_update = "";
                    dialogAlert.dismiss();
                    cardManager.updateFile();
                } else {
                    step_update = "";
                    dialogAlert.dismiss();
                    if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length() == 8 &&
                            Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length() == 8)
                        cardManager.AutoTLE("323"); // ONUS RKI TLE OFFUS RKI TLE WORKINGKEY
                    else if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length() == 8)
                        cardManager.AutoTLE("112"); // ONUS RKI TLE
                    else
                        cardManager.AutoTLE("213"); // OFFUS RKI TLE WORKINGKEY
//                    customDialogDownloadKey();
                }
            }
        };
        timer.start();
    }

    public void customDialogAlertFail() {
        final Dialog dialogAlert = new Dialog(MenuServiceListActivity.this, R.style.ThemeWithCorners);
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_fail_, null);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(view);
        dialogAlert.setCancelable(false);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        msgLabel.setText("อัปเดตข้อมูลล้มเหลว");

        try {
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

        CountDownTimer timer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                dialogAlert.dismiss();
            }
        };
        timer.start();
    }

    private void setMeasureNormal() {
        NormalView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        NormalView.layout(0, 0, NormalView.getMeasuredWidth(), NormalView.getMeasuredHeight());
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

    private void setCheckUpdate() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //set Task
                TimerTask autoUpdate = new TimerTask() {
                    @Override
                    public void run() {
                        cardManager.checkUpdate();
//                        update_timer.cancel();
                        Log.d("UPDATE_TASK", "::: DO IT! ");
                    }
                };

                timer_update = 10000;

                //connect Task
                update_timer = null;
                update_timer = new Timer();
                update_timer.schedule(autoUpdate, timer_update, 10000);
            }
        });
    }

//    private void updatefunction_bmta() {
//        customProgressDialog = new CustomProgressDialog(MenuServiceListActivity.this);
//        handler = new Handler();
//
//        cardManager.setUpdateLister(new CardManager.UpdateLister() {
//            @Override
//            public void onFindJson() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Title_btn1.setBackgroundResource(R.drawable.alert_update);
//                        Title_btn1.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void onFindApk() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Title_btn1.setBackgroundResource(R.drawable.alert_update2);
//                        Title_btn1.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void onFindJsonandApk() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Title_btn1.setBackgroundResource(R.drawable.alert_update3);
//                        Title_btn1.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void onNone() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Title_btn1.setVisibility(View.INVISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void onRunTHVInstaller(String path, String pw) {
//                if (getPackageList("com.thaivan.install.thvinstaller")) {
//                    Log.d("aaa", "getting name 1 ");
//                    ComponentName component_Name = new ComponentName("com.thaivan.install.thvinstaller", "com.thaivan.install.thvinstaller.MainActivity");
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.putExtra("PACKAGE", "org.centerm.tollway");
//                    intent.putExtra("CLASS", "org.centerm.Tollway.activity.IntroActivity");
//                    intent.putExtra("APK_PATH", path);
//                    intent.putExtra("APK_PW", pw);
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.setComponent(component_Name);
//                    startActivity(intent);
//
//                    finishAffinity();
//                } else {
//                    Log.d("aaa", "getting name 1 fail ");
//                    Toast.makeText(MenuServiceListActivity.this, "Please contact to THAIVAN", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onUpdateJson() {
//                if (dialogWaiting.isShowing())
//                    dialogWaiting.dismiss();
//
//                progress_cnt = 0;
//                if (progress_cnt < progress_max)
//                    customProgressDialog.show();
//                else
//                    Toast.makeText(MenuServiceListActivity.this, "ERROR :: Upate Json ", Toast.LENGTH_SHORT).show();
//
//                new Thread(new Runnable() {
//                    public void run() {
//                        while (progress_cnt < progress_max) {
//                            progress_cnt++;
//                            try {
//                                Thread.sleep(10);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            // Updating the progress bar
//                            handler.post(new Runnable() {
//                                public void run() {
//                                    customProgressDialog.txt_proceed.setText(progress_cnt + " %");
//                                    customProgressDialog.progressBar.setProgress(progress_cnt);
//                                }
//                            });
//
//                            if (progress_cnt == progress_max) {
//                                customProgressDialog.dismiss();
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        CardPrefix.getStringJson(MenuServiceListActivity.this);
//                                        step_update = "FINISH";
//                                        customDialogAlertSuccess();
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onUpdateTle() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        step_update = "TLE";
//                        customDialogAlertSuccess();
//                    }
//                });
//            }
//
//            @Override
//            public void onUpdateFail() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        customDialogAlertFail();
//                    }
//                });
//            }
//        });
//    }


    private void updatefunction_bmta() {
        customProgressDialog = new CustomProgressDialog(MenuServiceListActivity.this);
        handler = new Handler();

        cardManager.setUpdateLister(new CardManager.UpdateLister() {
            @Override
            public void onFindJson() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Title_btn1.setBackgroundResource(R.drawable.alert_update);
                        Title_btn1.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFindApk() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Title_btn1.setBackgroundResource(R.drawable.alert_update2);
                        Title_btn1.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFindJsonandApk() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Title_btn1.setBackgroundResource(R.drawable.alert_update3);
                        Title_btn1.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onNone() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Title_btn1.setVisibility(View.INVISIBLE);
                        Rf_Handler.sendEmptyMessageDelayed(0, 2000);
                    }
                });
            }

            @Override
            public void onRunTHVInstaller(String path, String pw) {
                System.out.printf("utility:: %s updatefunction_bmta onRunTHVInstaller ok \n", TAG);
                if (getPackageList("com.thaivan.install.thvinstaller")) {
                    Log.d("aaa", "getting name 1 ");
                    ComponentName component_Name = new ComponentName("com.thaivan.install.thvinstaller", "com.thaivan.install.thvinstaller.MainActivity");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.putExtra("PACKAGE", "org.centerm.tollway");

                    intent.putExtra("CLASS", "org.centerm.Tollway.activity.IntroActivity");
                    intent.putExtra("APK_PATH", path);
                    intent.putExtra("APK_PW", pw);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(component_Name);
                    startActivity(intent);

                    finishAffinity();
                } else {
                    Log.d("aaa", "getting name 1 fail ");
                    Toast.makeText(MenuServiceListActivity.this, "Please contact to THAIVAN", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUpdateJson() {
                if (dialogWaiting.isShowing())
                    dialogWaiting.dismiss();
                System.out.printf("utility:: %s updatefunction_bmta onUpdateJson ok \n", TAG);
                progress_cnt = 0;
                if (progress_cnt < progress_max)
                    customProgressDialog.show();
                else
                    Toast.makeText(MenuServiceListActivity.this, "ERROR :: Upate Json ", Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    public void run() {
                        while (progress_cnt < progress_max) {
                            progress_cnt++;
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // Updating the progress bar
                            handler.post(new Runnable() {
                                public void run() {
                                    customProgressDialog.txt_proceed.setText(progress_cnt + " %");
                                    customProgressDialog.progressBar.setProgress(progress_cnt);
                                }
                            });

                            if (progress_cnt == progress_max) {
                                customProgressDialog.dismiss();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CardPrefix.getStringJson(MenuServiceListActivity.this);
                                        step_update = "FINISH";
                                        customDialogAlertSuccess();
                                    }
                                });
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onUpdateTle() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        step_update = "TLE";
                        customDialogAlertSuccess();
                    }
                });
            }

            @Override
            public void onUpdateFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customDialogAlertFail();
                    }
                });
            }
        });
    }


    private void setViewNormalSlip(final String typeHost) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
                Realm realm = Realm.getDefaultInstance();
                try {
                    RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll();
                    Double amountSale = 0.0;
                    Double amountVoid = 0.0;
                    for (int i = 0; i < transTemp.size(); i++) {
//                        amountSale += Float.valueOf(transTemp.get(i).getAmount().replaceAll(",", ""));
                        amountSale += Double.valueOf(transTemp.get(i).getAmount().replaceAll(",", "")); // Paul_20190128
                    }
                    RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("hostTypeCard", typeHost).findAll();
                    for (int i = 0; i < transTempVoid.size(); i++) {
//                        amountVoid += Float.valueOf(transTempVoid.get(i).getAmount().replaceAll(",", ""));
                        amountVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",", ""));
                    }
                    Date date = new Date();

                    // Paul_20181204 Can not print label 1,2,3
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        NormalmerchantName1NormalLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        NormalmerchantName2NormalLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        NormalmerchantName3NormalLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3));


                    NormalsummaryvoidSaleCountLabel.setText(transTempVoid.size() + "");
                    NormalsummaryvoidSaleAmountLabel.setText(decimalFormat.format(amountVoid));
                    NormalsummarysaleTotalLabel.setText(decimalFormat.format(amountSale));
                    NormalsummarysaleCountLabel.setText(transTemp.size() + "");
                    NormalsummarycardCountLabel.setText((transTemp.size() + transTempVoid.size()) + "");
                    NormalsummarycardAmountLabel.setText(decimalFormat.format(amountSale));

                    NormaldateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    NormaltimeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));

                    if (typeHost.equalsIgnoreCase("POS")) {
                        NormalhostLabel.setText("KTB OFFUS");     // Paul_20181028 Sinn merge version UAT6_0016
                        int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
                        NormalbatchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                        NormaltidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                        NormalmidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_DATE_POS, NormaldateLabel.getText().toString());
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TIME_POS, NormaltimeLabel.getText().toString());

                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS, decimalFormat.format(amountSale));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_POS, transTemp.size() + "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_POS, transTempVoid.size() + "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));

                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_POS, CardPrefix.calLen(String.valueOf(batch), 6));
                    } else if (typeHost.equalsIgnoreCase("EPS")) {
                        NormalhostLabel.setText("WAY4");
                        int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
                        NormalbatchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                        NormaltidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                        NormalmidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_DATE_EPS, NormaldateLabel.getText().toString());
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TIME_EPS, NormaltimeLabel.getText().toString());
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS, decimalFormat.format(amountSale));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS, transTemp.size() + "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS, transTempVoid.size() + "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_EPS, CardPrefix.calLen(String.valueOf(batch), 6));
                    } else {
                        NormalhostLabel.setText("KTB ONUS");
                        int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)) - 1;
                        NormalbatchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                        NormaltidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                        NormalmidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_DATE_TMS, NormaldateLabel.getText().toString());
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TIME_TMS, NormaltimeLabel.getText().toString());
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS, decimalFormat.format(amountSale));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS, transTemp.size() + "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS, transTempVoid.size() + "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_TMS, CardPrefix.calLen(String.valueOf(batch), 6));
                    }

                    // Paul_20181202
                    if (CardTypeDB == null) {
                        CardTypeDB = new ArrayList<>();
                    } else {
                        CardTypeDB.clear();
                    }
                    recyclerViewCardReportSummary.setAdapter(null);
                    slipSummaryReportCardAdapter = new SlipSummaryReportCardAdapter(MenuServiceListActivity.this);
                    recyclerViewCardReportSummary.setAdapter(slipSummaryReportCardAdapter);
                    if (realm == null)
                        realm = Realm.getDefaultInstance();
                    RealmResults<TransTemp> transTemp10 = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
                    for (int i = 0; i < transTemp10.size(); i++) {
                        String CardTypeHolder = transTemp10.get(i).getCardTypeHolder();
                        System.out.printf("utility:: %s CardTypeHolder = %s \n", TAG, CardTypeHolder);
                        String Amount = decimalFormat.format(Double.valueOf(transTemp10.get(i).getAmount()));
                        System.out.printf("utility:: %s Amount = %s \n", TAG, Amount);
                    }
                    String[] CardTypeTempHolder = new String[100];
//        CardTypeTempHolder = null;
                    int CardTypeTempCnt = 0;

                    for (int k = 0; k < 100; k++) {
                        CardTypeTempHolder[k] = null;
                    }
                    for (int i = 0; i < transTemp10.size(); i++) {
                        String CardTypeHolder = transTemp10.get(i).getCardTypeHolder();
                        System.out.printf("utility:: %s CardTypeHolder 99999999 = %s \n", TAG, CardTypeHolder);

                        int CheckFlg = 0;
                        for (int j = 0; (j < CardTypeTempCnt) && (j < 100); j++) {
                            if (CardTypeTempHolder[j] == null)
                                break;
                            if (CardTypeHolder.equalsIgnoreCase(CardTypeTempHolder[j])) {
                                CheckFlg = 1;
                                break;
                            }
                        }
                        if (CheckFlg == 0) {
                            CardTypeTempHolder[CardTypeTempCnt] = CardTypeHolder;
                            CardTypeTempCnt++;
                            CardTypeDB.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("CardTypeHolder", CardTypeHolder).findAll());
                            System.out.printf("utility:: %s CardTypeDB.size() = %d \n", TAG, CardTypeDB.size());
                            slipSummaryReportCardAdapter.setItem(CardTypeDB);
                        }
                    }
                    slipSummaryReportCardAdapter.notifyDataSetChanged();

                    if ((!typeHost.equalsIgnoreCase("TMS")) && (!typeHost.equalsIgnoreCase("QR"))) {// Paul_20180710
                        {
                            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1")) {
                                NormalsummaryLinearFeeLayout.setVisibility(View.GONE);
                            } else {
                                NormalsummaryLinearFeeLayout.setVisibility(View.VISIBLE);
                                System.out.printf("utility:: %s setViewNormalSlip call to selectSummaryTAXReport\n", TAG);
                                selectSummaryTAXReportNormal(typeHost, realm);  // Paul_20181205
                            }
                        }
                    } else {
                        NormalsummaryLinearFeeLayout.setVisibility(View.GONE);
                    }

                    setMeasureNormal();
                    Utility.SettlementReprintBmpWrite(typeHost, getBitmapFromView(settlementNormalLinearLayout));    // Paul_20181205 settlement reprint modify
                    ////doPrinting(getBitmapFromView(settlementNormalLinearLayout));    // settlementNormalLinearLayout , settlementLinearLayout

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
                            transTemp.deleteAllFromRealm();
                        }
                    });

                } finally {
                    if (realm != null) {
                        realm.close();
                        realm = null;   // Paul_20181026 Some time DB Read error solved
                    }
                }
            }

        }).start();
    }


    private void selectSummaryTAXReportNormal(String typeHost, Realm realm) {
        Double totalVoid = 0.0;
        Double totalSale = 0.0;
//        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "N").findAll();

        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "Y").findAll();
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            totalSale += Double.valueOf(transTempSale.get(i).getFee());
        }


        if (typeHost.equalsIgnoreCase("POS")) {
            NormalhostFeeLabel.setText("KTB OFFUS");
        } else {
            NormalhostFeeLabel.setText("WAY4");
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            totalVoid += Double.valueOf(transTempVoid.get(i).getFee());
        }
        if (typeHost.equalsIgnoreCase("POS")) {
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS, String.valueOf(totalSale));
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_POS, String.valueOf(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
            NormalbatchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        } else {
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS, String.valueOf(totalSale));
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_EPS, String.valueOf(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
            NormalbatchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        }
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            NormalmerchantName1FeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            NormalmerchantName2FeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            NormalmerchantName3FeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        NormaldateFeeLabel.setText(dateFormat.format(date));
        NormaltimeFeeLabel.setText(timeFormat.format(date));
        NormaltaxIdFeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAX_ID));
        NormalsaleCountFeeLabel.setText(String.valueOf(transTempSale.size()));
        NormalsaleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        NormalvoidSaleCountFeeLabel.setText(transTempVoid.size() + "");
        NormalvoidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        NormalcardCountFeeLabel.setText(countAll + "");
        NormalcardAmountFeeLabel.setText(decimalFormat.format(totalSale));
    }


    public void doPrinting(Bitmap slip) {
        Log.d(TAG, "Start doPrinting");
        oldBitmap = slip;
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    //This interface is used for set the gray level of the printing
                    //MIN is 0,Max is 4
                    //The level bigger, the speed of print is smaller
                    printDev.setPrinterGray(2);
                    printDev.printBmpFast(oldBitmap, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "FINISH PRINT");
                                    settlementForupdate();
                                }
                            });
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
//                    Thread.sleep(4000); ///Test 20180707
//                    int ret = printDev.printBarCodeSync("asdasd");
                } catch (RemoteException e) {
                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }
        }.start();
    }

    public boolean getPackageList(String name) {
        boolean isExist = false;

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        try {
            for (int i = 0; i < mApps.size(); i++) {
                if (mApps.get(i).activityInfo.toString().contains(name)) {
                    isExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }

    // Paul_20180705
    private void PosInterfaceThread() {

        try {
            posinterface.PosInterfaceClose();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            posinterface.PosInterfaceOpen();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        posinterface.POSInterfaceInit();

        // Paul_20181127
        AidlSerialPort serialPort1 = null;
        //serialPort1 = cardManager.getInstancesSerial1();
        if (serialPort1 == null) {
            try {
                Thread.sleep(500);        // Paul_20181127
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                int rv;
                boolean retry = true;

                while (retry) {
                    retry = false;
                    rv = posinterface.PosInterfaceDataWait();
                    if (rv != 0) {

                        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(MenuServiceListActivity.this.POWER_SERVICE);
                        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                        wakeLock.acquire();


                        String InputString = null;
                        byte[] data = null;
//                        JavaHexDump(posinterface.RealSerialRecBuf,rv);

//                        System.out.printf("utility:: PosInterfaceDataWait 000001 \n");
                        InputString = HexUtil.bytesToHexString(posinterface.RealSerialRecBuf).substring(0, rv * 2);
//                        posinterface.PosInterfaceSendData( InputString );
                        System.out.printf("utility:: InputString = %s \n", InputString);
//                        System.out.printf("utility:: PosInterfaceDataWait 000004 \n");
                        ProcessPosinterface(InputString);
                    } else
                        retry = true;
                }
            }
        }.start();
    }

    public void initWidget() {

        //Update Method Jeff20190516
        Title_btn1 = findViewById(R.id.toolbar_btn1);
        Title_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogUpdate();
            }
        });

        TextView app_title = findViewById(R.id.app_title);
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            app_title.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));


        decFormat = new DecimalFormat("##,###,##0.00"); //K.GAME 180917

        //20180803 SINN APP_ENABLE=111   1:DOL , 2:QR ,  3:HGC
        nameMenuList = new ArrayList<>();
        nameMenuList.clear();

        nameMenuList_org = new ArrayList<>();
        nameMenuList_org.clear();

        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {
            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1"))
                nameMenuList_org.add("ใช้สิทธิ์\nรักษาพยาบาล");

            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1"))
                nameMenuList_org.add("ทำรายการ\nออฟไลน์");

            nameMenuList_org.add("พิมพ์\nสำเนาสลิป");

            nameMenuList_org.add("สรุปยอด\nประจำวัน");
            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1"))
                nameMenuList_org.add("ตรวจสอบ\nบัตรประชาชน"); //K.GAME 180827 change UI //K.GAME 180907 Reuse

            nameMenuList_org.add("พิมพ์\nรายงาน");
            nameMenuList_org.add("ยกเลิก\nรายการ");
            nameMenuList_org.add("ทดสอบ\nโฮซ์ท");

            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(1, 2).equalsIgnoreCase("1"))
                nameMenuList_org.add("คิวอาร์โค้ด");

            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(0, 1).equalsIgnoreCase("1")) {
                nameMenuList_org.add("ชำระค่าทางด่วน");
            }
            nameMenuList_org.add("ตั้งค่า");
        } else {
            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(0, 1).equalsIgnoreCase("1"))
                nameMenuList_org.add("ชำระค่าทางด่วน");

            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(1, 2).equalsIgnoreCase("1"))
                nameMenuList_org.add("คิวอาร์โค้ด");

            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1"))
                nameMenuList_org.add("ใช้สิทธิ์\nรักษาพยาบาล");

            nameMenuList_org.add("ยกเลิก\nรายการ");
            nameMenuList_org.add("สรุปยอด\nประจำวัน");
            nameMenuList_org.add("พิมพ์\nสำเนาสลิป");
            nameMenuList_org.add("พิมพ์\nรายงาน");
            nameMenuList_org.add("ทดสอบ\nโฮซ์ท");
            nameMenuList_org.add("ตั้งค่า");
        }


        for (int i = nameMenuList_org.size(); i < 18; i++) {//K.GAME ถ้าไม่มี ก็จะให้เพิ่ม (""); เข้าไปจนครบ18
            nameMenuList_org.add("");
        }

        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1") && Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(0, 1).equalsIgnoreCase("1")) {
            for (int i = nameMenuList.size(); i < 18; i++) {//K.GAME ถ้าไม่มี ก็จะให้เพิ่ม (""); เข้าไปจนครบ18
                nameMenuList.add("");
            }
        } else {
            for (int i = nameMenuList.size(); i < 9; i++) {//K.GAME ถ้าไม่มี ก็จะให้เพิ่ม (""); เข้าไปจนครบ18
                nameMenuList.add("");
            }
        }


        nameMenuList.set(0, nameMenuList_org.get(0).toString());
        nameMenuList.set(1, nameMenuList_org.get(3).toString());
        nameMenuList.set(2, nameMenuList_org.get(6).toString());
        nameMenuList.set(3, nameMenuList_org.get(1).toString());
        nameMenuList.set(4, nameMenuList_org.get(4).toString());
        nameMenuList.set(5, nameMenuList_org.get(7).toString());
        nameMenuList.set(6, nameMenuList_org.get(2).toString());
        nameMenuList.set(7, nameMenuList_org.get(5).toString());
        nameMenuList.set(8, nameMenuList_org.get(8).toString());

        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1") && Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(0, 1).equalsIgnoreCase("1")) {
            nameMenuList.set(9, nameMenuList_org.get(9).toString());
            nameMenuList.set(10, nameMenuList_org.get(12).toString());
            nameMenuList.set(11, nameMenuList_org.get(15).toString());
            nameMenuList.set(12, nameMenuList_org.get(10).toString());
            nameMenuList.set(13, nameMenuList_org.get(13).toString());
            nameMenuList.set(14, nameMenuList_org.get(16).toString());
            nameMenuList.set(15, nameMenuList_org.get(11).toString());
            nameMenuList.set(16, nameMenuList_org.get(14).toString());
            nameMenuList.set(17, nameMenuList_org.get(17).toString());


            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            qrView = inflater.inflate(R.layout.view_slip_settlement_and_report, null);
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

            summaryLinearFeeLayout = qrView.findViewById(R.id.summaryLinearLayout);
            merchantName1FeeLabel = qrView.findViewById(R.id.merchantName1TaxLabel);
            merchantName2FeeLabel = qrView.findViewById(R.id.merchantName2TaxLabel);
            merchantName3FeeLabel = qrView.findViewById(R.id.merchantName3TaxLabel);
            dateFeeLabel = qrView.findViewById(R.id.dateTaxLabel);
            timeFeeLabel = qrView.findViewById(R.id.timeTaxLabel);
            batchFeeLabel = qrView.findViewById(R.id.batchTaxLabel);
            hostFeeLabel = qrView.findViewById(R.id.hostTaxLabel);
            saleCountFeeLabel = qrView.findViewById(R.id.saleCountTaxLabel);
            saleTotalFeeLabel = qrView.findViewById(R.id.saleTotalTaxLabel);
            voidSaleCountFeeLabel = qrView.findViewById(R.id.voidSaleCountTaxLabel);
            voidSaleAmountFeeLabel = qrView.findViewById(R.id.voidSaleAmountTaxLabel);
            cardCountFeeLabel = qrView.findViewById(R.id.cardCountTaxLabel);
            cardAmountFeeLabel = qrView.findViewById(R.id.cardAmountTaxLabel);
            taxIdFeeLabel = qrView.findViewById(R.id.taxIdLabel);
        }

        /************
         //20180803 SINN APP_ENABLE=111   1:DOL , 2:QR ,  3:HGC
         if (Preference.getInstance(MenuServiceListActivity.this).getValueString( Preference.KEY_APP_ENABLE).equalsIgnoreCase("111")) {
         nameMenuList = new ArrayList<>();
         nameMenuList.add("รายการ QR");
         nameMenuList.add("รายการขาย");
         nameMenuList.add("ใช้สิทธิ์รักษาพยาบาล");
         nameMenuList.add("สรุปยอด");
         nameMenuList.add("ยกเลิกรายการ");
         nameMenuList.add("ทำรายการออฟไลน์");
         nameMenuList.add("ทดสอบโฮซ์ท");
         nameMenuList.add("พิมพ์รายงาน");
         nameMenuList.add("พิมพ์ซ้ำรายการ");
         nameMenuList.add("ตั้งค่า");
         }else if (Preference.getInstance(MenuServiceListActivity.this).getValueString( Preference.KEY_APP_ENABLE).equalsIgnoreCase("011")) {
         nameMenuList = new ArrayList<>();
         nameMenuList.add("รายการ QR");
         nameMenuList.add("ใช้สิทธิ์รักษาพยาบาล");
         nameMenuList.add("สรุปยอด");
         nameMenuList.add("ยกเลิกรายการ");
         nameMenuList.add("ทำรายการออฟไลน์");
         nameMenuList.add("ทดสอบโฮซ์ท");
         nameMenuList.add("พิมพ์รายงาน");
         nameMenuList.add("พิมพ์ซ้ำรายการ");
         nameMenuList.add("ตั้งค่า");
         }else if (Preference.getInstance(MenuServiceListActivity.this).getValueString( Preference.KEY_APP_ENABLE).equalsIgnoreCase("101")) {
         nameMenuList = new ArrayList<>();
         nameMenuList.add("รายการขาย");
         nameMenuList.add("ใช้สิทธิ์รักษาพยาบาล");
         nameMenuList.add("สรุปยอด");
         nameMenuList.add("ยกเลิกรายการ");
         nameMenuList.add("ทำรายการออฟไลน์");
         nameMenuList.add("ทดสอบโฮซ์ท");
         nameMenuList.add("พิมพ์รายงาน");
         nameMenuList.add("พิมพ์ซ้ำรายการ");
         nameMenuList.add("ตั้งค่า");
         }else if (Preference.getInstance(MenuServiceListActivity.this).getValueString( Preference.KEY_APP_ENABLE).equalsIgnoreCase("100")) {
         nameMenuList = new ArrayList<>();
         nameMenuList.add("รายการขาย");
         nameMenuList.add("สรุปยอด");
         nameMenuList.add("ยกเลิกรายการ");
         nameMenuList.add("ทดสอบโฮซ์ท");
         nameMenuList.add("พิมพ์รายงาน");
         nameMenuList.add("พิมพ์ซ้ำรายการ");
         nameMenuList.add("ตั้งค่า");
         }else if (Preference.getInstance(MenuServiceListActivity.this).getValueString( Preference.KEY_APP_ENABLE).equalsIgnoreCase("110")) {
         nameMenuList = new ArrayList<>();
         nameMenuList.add("รายการ QR");
         nameMenuList.add("รายการขาย");
         nameMenuList.add("สรุปยอด");
         nameMenuList.add("ยกเลิกรายการ");
         nameMenuList.add("ทดสอบโฮซ์ท");
         nameMenuList.add("พิมพ์รายงาน");
         nameMenuList.add("พิมพ์ซ้ำรายการ");
         nameMenuList.add("ตั้งค่า");
         }else if (Preference.getInstance(MenuServiceListActivity.this).getValueString( Preference.KEY_APP_ENABLE).equalsIgnoreCase("001")) {
         nameMenuList = new ArrayList<>();
         nameMenuList.add("ใช้สิทธิ์รักษาพยาบาล");
         nameMenuList.add("สรุปยอด");
         nameMenuList.add("ยกเลิกรายการ");
         nameMenuList.add("ทำรายการออฟไลน์");
         nameMenuList.add("ทดสอบโฮซ์ท");
         nameMenuList.add("พิมพ์รายงาน");
         nameMenuList.add("พิมพ์ซ้ำรายการ");
         nameMenuList.add("ตั้งค่า");
         }else if (Preference.getInstance(MenuServiceListActivity.this).getValueString( Preference.KEY_APP_ENABLE).equalsIgnoreCase("010")) {
         nameMenuList = new ArrayList<>();
         nameMenuList.add("รายการ QR");
         nameMenuList.add("สรุปยอด");
         nameMenuList.add("ยกเลิกรายการ");
         nameMenuList.add("ทดสอบโฮซ์ท");
         nameMenuList.add("พิมพ์รายงาน");
         nameMenuList.add("พิมพ์ซ้ำรายการ");
         nameMenuList.add("ตั้งค่า");
         }
         ************/
        //K.GAME 181016 hard code
        img_krungthai1 = findViewById(R.id.img_krungthai1);
        img_krungthai2 = findViewById(R.id.img_krungthai2);
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {

            img_krungthai1.setVisibility(View.INVISIBLE);
            img_krungthai2.setVisibility(View.VISIBLE);
        }//END K.GAME 181016 hard code
        recyclerViewMenuList = findViewById(R.id.recyclerViewMenuList);
        linearLayoutToolbarBottom = findViewById(R.id.linearLayoutToolbarBottom);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); //K.GAME Test
        gridLayoutManager.setSpanCount(3);//K.GAME Test
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);//K.GAME Test
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMenuList.setLayoutManager(layoutManager);
        setMenuList();
        customDialogFallBack();
        customDialog();
        customDialogWaiting();
        customDialogSelectApp();
        customDialogCheckFallBack();
        customDialogCheckCard();
        customDialogCheckCardPosinterface();
        customDialogAlert();
        customDialogHost();
        setDialog();

        setCheckCallBackCard();

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialoglogout();
            }
        });
    }

    private void setCheckCallBackCard(){
        cardManager.setCardHelperListener(new CardManager.CardHelperListener() {

            @Override
            public void onFindCard() {

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
            public void onMultiApp(int a, EMV_APPLIST[] list) {
                Log.d("cardHelperListener","onMultiApp");
            }
            @Override
            public  void onCardNo(String cardNo) {
                Log.d("cardHelperListener","onCardNo");
            }

            @Override
            public void onGetCardHolderName(String szCardName) {
                Log.d("cardHelperListener","onGetCardHolderName");
                cardhlder = szCardName;
                Log.d(TAG, "cardhlder=" + cardhlder);
            }

            @Override
            public void onMultiApp(final int item, final String[] aid) {
                Log.d("cardHelperListener","onMultiApp");
                Log.d(TAG, "onMultiApp: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogWaiting.dismiss();
                        DialogSelect.show();
                        if (item == 2) {
                            btnApp1.setText(aid[0]);
                            btnApp2.setText(aid[1]);
                        } else if (item == 3) {
                            btnApp3.setVisibility(View.VISIBLE);
                            btnApp1.setText(aid[0]);
                            btnApp2.setText(aid[1]);
                            btnApp3.setText(aid[2]);
                        } else if (item == 4) {
                            btnApp3.setVisibility(View.VISIBLE);
                            btnApp4.setVisibility(View.VISIBLE);
                            btnApp1.setText(aid[0]);
                            btnApp2.setText(aid[1]);
                            btnApp3.setText(aid[2]);
                            btnApp4.setText(aid[3]);
                        } else {
                            btnApp3.setVisibility(View.VISIBLE);
                            btnApp4.setVisibility(View.VISIBLE);
                            btnApp5.setVisibility(View.VISIBLE);
                            btnApp1.setText(aid[0]);
                            btnApp2.setText(aid[1]);
                            btnApp3.setText(aid[2]);
                            btnApp4.setText(aid[3]);
                            btnApp5.setText(aid[4]);
                        }

                    }
                });
            }

            @Override
            public void onDuplicateTrans(final String msg) {
                Log.d("cardHelperListener","onDuplicateTrans");
                playSound(1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogWaiting.dismiss();
                        Utility.customDialogAlert(MenuServiceListActivity.this, msg, new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
//                                dialogInsertCard.dismiss();
//                                dialogContactless.dismiss();
                                Rf_Handler.sendEmptyMessageDelayed(0, 2000);
                            }
                        });


                    }
                });
            }

            @Override
            public void onCardInfoReceived(final Card card) {
                Log.d("cardHelperListener","onCardInfoReceived");
                if (card != null) {
                    cardNo = card;
                    typeCard = IC_CARD;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            dialogWaiting.show();

                            dialogInsertCard.dismiss();
                            dialogContactless.dismiss();
                            myCardReaderHelper.getInstance().stopPolling();
                            isDialogShowInsertCardShowing = false;
                            dialogContactless = null;

                            cardManager.stopTransaction();
                            Intent intent = new Intent(MenuServiceListActivity.this, CalculatePriceActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KEY_CARD, cardNo);
                            bundle.putString(KEY_TYPE_CARD, IC_CARD);
                            bundle.putString(KEY_TYPE_INTERFACE, localtypeInterface);
                            Log.d(TAG, "amountInterface :" + amountInterface);
                            bundle.putString(KEY_INTERFACE_CARDHOLDER, cardhlder);//K.GAME 180916
                            bundle.putString(KEY_INTERFACE_AMOUNT, amountInterface);  ///SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(0, 0);

//                            dismissDialogAll();
                        }
                    });
                    cardManager.stopTransaction();
                    Log.d(TAG, "onCardInfoReceived: " + card.toString());  //MenuServiceListActivity: onCardInfoReceived: No : 5256670000242651, expire date : 1904, service code : 201
                    // MenuServiceListActivity: onCardInfoReceived: No : 6210948000000037, expire date : 3010, service code : 220
//                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SERVICE_CODE_PIN_ID, "0");

//                    if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_WAY4_ID).equalsIgnoreCase("1"))
//                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SERVICE_CODE_PIN_ID, card.toString().substring(card.toString().length() - 1, card.toString().length()));
//                    Log.d(TAG, "service code pin: " + Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SERVICE_CODE_PIN_ID));

                }
            }

            @Override
            public void onCardInfoReceived_Contactless(String CARD_NO, String NAMECARDHOLDER, String AMOUNT) {
                Log.d("cardHelperListener","onCardInfoReceived_Contactless");
//                cardManager.stopTransaction();


                if (AMOUNT.equalsIgnoreCase("reject")) {
                    playSound(1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogWaiting.dismiss();
                            Utility.customDialogAlert(MenuServiceListActivity.this, "ไม่สามารถใช้งานบัตรนี้ได้", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
//                                    dialogInsertCard.dismiss();
//                                    dialogContactless.dismiss();
                                    Rf_Handler.sendEmptyMessageDelayed(0, 2000);
                                }
                            });

//                            startInsertCard();
                        }
                    });

                } else {
                    playSound(0);

                    MenuServiceListActivity.this.CARD_NO = CARD_NO;
                    MenuServiceListActivity.this.NAMECARDHOLDER = NAMECARDHOLDER;
                    MenuServiceListActivity.this.AMOUNT = AMOUNT;

//                    Intent intent = new Intent(MenuServiceListActivity.this, ContactlessConfrimActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(KEY_CARD, CARD_NO);
//                    bundle.putString(KEY_INTERFACE_CARDHOLDER, NAMECARDHOLDER);
//                    bundle.putString(KEY_INTERFACE_AMOUNT, AMOUNT);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);


//                    dialogInsertCard.dismiss();
//                    dialogContactless.dismiss();

//                    Rf_Handler.sendEmptyMessage(1000);

                    String valueParameterEnable = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1000);
//                    if (valueParameterEnable.substring(0, 4).equalsIgnoreCase("2222") || valueParameterEnable.substring(0, 4).equalsIgnoreCase("1111")) { // NAMTAN_20190712
                    if (!(valueParameterEnable.substring(0, 4).contains("3")) && !(valueParameterEnable.substring(0, 4).contains("4"))) {
                        cardManager.sendMessege();
                    } else {
//                        dialogParaEndble.show();
                    }


                }


            }

            @Override
            public void onCardInfoFail() {
                System.out.printf("utility:: MenuServiceListActivity onCardInfoFail \n");
                Log.d(TAG, "onCardInfoFail: ");
                cardManager.stopTransaction();
                Rf_Handler.sendEmptyMessageDelayed(0, 2000);
            }

            @Override
            public void onTransResultFallBack() {
                System.out.printf("utility:: %s onTransResultFallBack 000000000001 numFallBack = %d\n", TAG, numFallBack);
                if (numFallBack > 2) {      // Paul_20181117 error dialog 3 time display
                    cardManager.setFallBackHappen();
                }
                if (numFallBack > 2) {      // Paul_20181117 error dialog 3 time display
                    isFallBack = true;
                    Log.d(TAG, "onTransResultFallBack: ");
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (timer != null)
                        timer.cancel();
                    if (dialogInsertCard != null)
                        dialogInsertCard.cancel();
                    cardManager.stopTransaction();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (numFallBack > 2) {          // Paul_20181117 error dialog 3 time display
                                dialogFallBack.dismiss();
                                cardManager.stopTransaction();
                            }
                            dialogFallBackCheck.show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ++numFallBack;

                            cardManager.stopTransaction();
                            dialogFallBackCheck.show();

                        }
                    });
                }
            }

            @Override
            public void onCardTransactionUpdate(boolean isApproved, Card card) {
                Log.d(TAG, "onCardTransactionUpdate: " + isApproved + " Card : " + card);
                if (isApproved) {
                    Log.d(TAG, "onCardTransactionUpdate: " + isApproved + " stopTransaction");
//                    cardManager.stopTransaction();

                }

//                else {
//                    Log.d(TAG, "onCardTransactionUpdate: " + isApproved + " runOnUiThread");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            setCheckCallBackCard();
//                        }
//                    });
//                }
            }

            @Override
            public void onFindMagCard(Card card) {
                cardNo = card;
                typeCard = MSG_CARD;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (dialogFallBack != null)
                            dialogFallBack.dismiss();
                        if (dialogInsertCard != null) {
                            dialogInsertCard.dismiss();
                            dialogContactless.dismiss();
                            myCardReaderHelper.getInstance().stopPolling();
                            cardManager.stopTransaction();

                            dialogContactless = null;
                            isDialogShowInsertCardShowing = false;
                        }
                        if (dialogWaiting != null)
                            dialogWaiting.show();
                        setTimer(2000, 3);
                        dialogInsertCard.dismiss();
                        dialogContactless.dismiss();
                        myCardReaderHelper.getInstance().stopPolling();
                        cardManager.stopTransaction();
                        dialogContactless = null;
                        isDialogShowInsertCardShowing = false;
                    }
                });
            }

            @Override
            public void onSwapCardIc() {
                Log.d(TAG, "onSwapCardIc: ");
                dismissDialogAll();
                cardManager.stopTransaction();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customDialogServiceCode();
                    }
                });
            }

            @Override
            public void onSwapCardMag() {
                cardManager.abortPBOCProcess();
                dismissDialogAll();
                cardManager.stopTransaction();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
//                        try {
//                            mp = MediaPlayer.create(MenuServiceListActivity.this, R.raw.beep_02);
//                            mp.setLooping(true);
//                            mp.seekTo(100);
//                            mp.start();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        System.out.printf("utility:: %s onSwapCardMag 003\n", TAG);
// Paul_20181017 Same Normal and POSLINK Function
                        if (posinterface.PosInterfaceExistFlg == 1) {
                            dialogCheckCard.show();
/*
                            dialogCheckCardposinterface.show();
                            TellToPosNoMatching("11");
                            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                @Override
                                public void success() {
                                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                    dialogCheckCardposinterface.dismiss();
                                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            });
*/
                        } else {
                            dialogCheckCard.show();
                        }
                        // Flow not finish Do send error here card will show dialogCheckCard

//                        dialogCheckCard.show();
                    }
                });
            }

            @Override
            public void onSwipeCardFail() {
                System.out.printf("utility:: %s onSwipeCardFail \n", TAG);

                if (posinterface.PosInterfaceExistFlg == 1) {
                    System.out.printf("utility:: %s onSwapCardMag 004\n", TAG);
//                    dialogCheckCardposinterface.show();
                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "สไลด์การ์ดล้มเหลว");
                    TellToPosNoMatching("11");
                    posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                            Utility.customDialogAlertAutoClear();
//                            dialogCheckCardposinterface.dismiss();
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MenuServiceListActivity.this);
                            builder.setMessage("สไลด์การ์ดล้มเหลว")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setCancelable(false)
                                    .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dismissDialogAll();
//                                            cardmanager.startTransaction(CardManager.sale, amountinterface);
                                            //SINN 20181113 read card fail no need start transaction
                                            cardManager.abortPBOCProcess();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
            }

            @Override
            public void onFindICCard() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogCheckCard.dismiss();
                        dialogFallBack.dismiss();
                        dialogWaiting.show();
//                        setTimer(60000, 1);     // Paul_20181019 Chip Mulfunction time out
//                        dialogInsertCard.dismiss();
//                        dialogContactless.dismiss();
                    }
                });
            }

            @Override
            public void onFindContactlessMultiapp() {
                cardManager.startTransaction(CardManager.SALE, amountInterface);
            }

            @Override
            public void onTansAbort() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (dialogInsertCard.isShowing()) {
                            dialogInsertCard.dismiss();
                            dialogContactless.dismiss();
                            myCardReaderHelper.getInstance().stopPolling();
                            cardManager.stopTransaction();

                            dialogContactless = null;
                            isDialogShowInsertCardShowing = false;
                        }

                        if (dialogWaiting.isShowing()) {
                            dialogWaiting.dismiss();
                        }


                        errormsgLabel.setText("อ่านการ์ดล้มเหลว");
                        dialogAlert.show();
                    }
                });
            }
        });
        //Check ว่า Reversal ได้ไหม
        cardManager.setInsertOrUpdateDatabase(new CardManager.InsertOrUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {
                Log.d("contactless_check: ", "UpdateVoidSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting!=null){
                            dialogWaiting.dismiss();
                        }
                        initWidget();
                    }
                });
            }

            @Override
            public void onInsertSuccess(final int nextId) {


//                    Intent intent = new Intent(MenuServiceListActivity.this, ContactlessConfrimActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(KEY_CARD, CARD_NO);
//                    bundle.putString(KEY_INTERFACE_CARDHOLDER, NAMECARDHOLDER);
//                    bundle.putString(KEY_INTERFACE_AMOUNT, AMOUNT);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("contactless_check: ", "onInsertSuccess");
                        cardManager.removeTransResultAbort();
                        Intent intent = new Intent(MenuServiceListActivity.this, SlipTemplateActivity.class);
                        intent.putExtra(KEY_INTERFACE_CARDHOLDER_2, NAMECARDHOLDER);
                        intent.putExtra(KEY_CALCUATE_ID, nextId);
                        intent.putExtra(KEY_TYPE_SALE_OR_VOID, "SALE");
                        intent.putExtra(MenuServiceListActivity.KEY_TYPE_INTERFACE, "Interface");
                        intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_APP, "");
                        intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_TC, "");
                        intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_AID, "");
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            }
        });

        cardManager.setConnectStatusSocket(new CardManager.ConnectStatusSocket() {
            @Override
            public void onConnectTimeOut() {
                if (!isFinishing()) {
                    Log.d(TAG, "onConnectTimeOut");
//                    dismissDialogAll();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if(localtypeInterface != null) {
                            if (posinterface.PosInterfaceExistFlg == 1) {

//                                if (dialogWaiting != null)
//                                    dialogWaiting.dismiss();
//                                if(dialogAlert != null)
//                                    dialogAlert.dismiss();
// Paul_20180731
                                System.out.printf("utility:: cardManager.setConnectStatusSocket 00000000001 onConnectTimeOut\n");
//                                Utility.customDialogAlertAuto(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว");
                                errormsgLabel.setText("เชื่อมต่อล้มเหลว");
                                dialogAlert.show();

                                TellToPosNoMatching("EN");
                                posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                        Utility.customDialogAlertAutoClear();
                                        Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
//                                Utility.customDialogAlert(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });
                                errormsgLabel.setText("เชื่อมต่อล้มเหลว");
                                dialogAlert.show();
                            }
                        }
                    });
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }

            @Override
            public void onTransactionTimeOut() {
                if (!isFinishing()) {
                    dismissDialogAll();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if(localtypeInterface != null) {
                            if (posinterface.PosInterfaceExistFlg == 1) {
                                System.out.printf("utility:: cardManager.setConnectStatusSocket 00000000001 onTransactionTimeOut\n");
//                                Utility.customDialogAlertAuto(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว");

                                errormsgLabel.setText("เชื่อมต่อล้มเหลว");
                                dialogAlert.show();

                                TellToPosNoMatching("21");
                                posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                        Utility.customDialogAlertAutoClear();
                                        Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
//                                Utility.customDialogAlert(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });
                                errormsgLabel.setText("เชื่อมต่อล้มเหลว");
                                dialogAlert.show();
                            }
                        }
                    });
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }

            @Override
            public void onError() {
                if(dialogWaiting != null) {
                    if(dialogWaiting.isShowing()) {
                        dialogWaiting.dismiss();
                    }
                }
                Utility.customDialogAlert(MenuServiceListActivity.this, "connection error");

            }

            @Override
            public void onError(String msg) {

                if(dialogWaiting != null) {
                    if(dialogWaiting.isShowing()) {
                        dialogWaiting.dismiss();
                    }
                }
                Utility.customDialogAlert(MenuServiceListActivity.this, msg);

            }

            @Override
            public void onOther() {

            }

            @Override
            public void onReceived() {

            }
        });

        callReversal();

    }


    private void customDialoglogout() {
        final Dialog dialogLogout = new Dialog(this, R.style.ThemeWithCorners);
        View view = dialogLogout.getLayoutInflater().inflate(R.layout.dialog_custom_logout, null);
        dialogLogout.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLogout.setContentView(view);
        dialogLogout.setCancelable(false);
        Button btn_ok = dialogLogout.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogLogout.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (checkReversal("SETTLEMENT")) {
//                    if (!checkReversalGHC()) {
//                        Intent intent = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
//                        intent.putExtra(KEY_TYPE_INTERFACE,"InterfaceTollway");
//                        startActivity(intent);
//                        overridePendingTransition(0, 0);.
                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_BUS_LOGIN, "OFF");
                Intent intent = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                intent.putExtra("Logout", "Logout");
                startActivity(intent);
                overridePendingTransition(0, 0);
//                    } else {
//                        sendDataReversal(reversalHealthCare);
//                    }
//                }


                //Settlement
//                Intent intent;
//                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_BUS_LOGIN,"OFF");
//                intent = new Intent(MenuServiceListActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogout.dismiss();
            }
        });

        dialogLogout.show();
    }

    private void customDialogUpdate() {
        dialogUpdate = new Dialog(this, R.style.ThemeWithCorners);
        View view = dialogUpdate.getLayoutInflater().inflate(R.layout.dialog_custom_update, null);
        dialogUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdate.setContentView(view);
        dialogUpdate.setCancelable(false);
        btn_later = dialogUpdate.findViewById(R.id.btn_later);
        btn_now = dialogUpdate.findViewById(R.id.btn_now);

        btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUpdate.dismiss();
            }
        });

        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUpdate.dismiss();
                dialogWaiting.show();
                settlementForupdate();
            }
        });
        dialogUpdate.show();
    }

    private void settlementForupdate() {
        //Settlement
        String std_flag = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE);
        String std_ali = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_ID);
        String std_wec = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_WECHATPAY_ID);

        if (realm == null)
            realm = Realm.getDefaultInstance();
        else {
            realm.close();
            realm = Realm.getDefaultInstance();
        }


        if (std_flag.substring(0, 1).equals("1")) {
            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS) != null) {
                if (transTemp_tms == null) {
                    transTemp_tms = new ArrayList<>();
                } else {
                    transTemp_tms.clear();
                    transTemp_tms = new ArrayList<>();
                }
                transTemp_tms.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", "TMS").findAll());
                if (transTemp_tms.size() > 0)
                    hostflag += "TMS";
            }

            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS) != null) {
                if (transTemp_eps == null) {
                    transTemp_eps = new ArrayList<>();
                } else {
                    transTemp_eps.clear();
                    transTemp_eps = new ArrayList<>();
                }
                transTemp_eps.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", "EPS").findAll());
                if (transTemp_eps.size() > 0)
                    hostflag += "EPS";
            }
        }

        if (std_flag.substring(1, 2).equals("1")) {
            if (qrTemp == null) {
                qrTemp = new ArrayList<>();
            } else {
                qrTemp.clear();
                qrTemp = new ArrayList<>();
            }
            qrTemp.addAll(realm.where(QrCode.class).equalTo("hostTypeCard", "QR").findAll());
            if (qrTemp.size() > 0)
                hostflag += " QR";
        }

        if (std_ali.equals("1")) {
            if (aliTemp == null) {
                aliTemp = new ArrayList<>();
            } else {
                aliTemp.clear();
                aliTemp = new ArrayList<>();
            }
            aliTemp.addAll(realm.where(QrCode.class).equalTo("hostTypeCard", "ALIPAY").findAll());
            if (aliTemp.size() > 0)
                hostflag += "ALI";
        }

        if (std_wec.equals("1")) {
            if (wecTemp == null) {
                wecTemp = new ArrayList<>();
            } else {
                wecTemp.clear();
                wecTemp = new ArrayList<>();
            }
            wecTemp.addAll(realm.where(QrCode.class).equalTo("hostTypeCard", "WECHAT").findAll());
            if (wecTemp.size() > 0)
                hostflag += "WEC";
        }

        if (!hostflag.equals("")) {
            if (hostflag.substring(0, 3).equals("TMS")) {
                step_update = "TMS";
                hostflag = hostflag.substring(3, hostflag.length());
                cardManager.setDataDefaultUploadCradit();
                cardManager.setDataSettlementAndSendSALE();
            } else if (hostflag.substring(0, 3).equals("EPS")) {
                step_update = "EPS";
                hostflag = hostflag.substring(3, hostflag.length());
                cardManager.setDataDefaultUploadCradit();
                cardManager.setCheckTCUpload("EPS", true);
            } else if (hostflag.substring(0, 3).equals(" QR")) {
                step_update = "QR";
                hostflag = hostflag.substring(3, hostflag.length());
                selectSettlementQRAll();
            } else if (hostflag.substring(0, 3).equals("ALI")) {
                step_update = "ALI";
                hostflag = hostflag.substring(3, hostflag.length());
                selectSettlementALIPAY();
            } else if (hostflag.substring(0, 3).equals("WEC")) {
                step_update = "WEC";
                selectSettlementWECHAT();
            }
        } else {
            if (dialogWaiting.isShowing())
                dialogWaiting.dismiss();
            step_update = "JSON";
            cardManager.updateFile();
        }

    }

//    private void customDialogDownloadKey() {
//        dialogDownloadKey = new Dialog(this, R.style.ThemeWithCorners);
//        View view = dialogDownloadKey.getLayoutInflater().inflate(R.layout.dialog_custom_tledownload, null);
//        dialogDownloadKey.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogDownloadKey.setContentView(view);
//        dialogDownloadKey.setCancelable(false);
//        btn_later_tle = dialogDownloadKey.findViewById(R.id.btn_later);
//        btn_now_tle = dialogDownloadKey.findViewById(R.id.btn_now);
//
//        btn_later_tle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogDownloadKey.dismiss();
//
//                step_update = "FINISH";
//                cardManager.updateFile();
//            }
//        });
//
//        btn_now_tle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogDownloadKey.dismiss();
//                dialogWaiting.show();
//
//
//            }
//        });
//        dialogDownloadKey.show();
//    }

    private void selectSettlementALIPAY() {
        type = "ALIPAY";
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amountSale = 0.00;
                amountVoid = 0.00;

                dateTime = null;
                dateTime = new Date();
                reqChannelDtm = dateFormat.format(dateTime); // + dateFormat_time.format(dateTime);
                uniqueData = makeUniqueData(dateTime);

                alipay_http = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_URL_ID); //20181114Jeff
                ALIPAY_CER_PATH = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_CERTI_ID); //20181115Jeff

                if (realm == null)
                    realm = Realm.getDefaultInstance();
                else {
                    realm = Realm.getDefaultInstance();
                }

                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
                String tmp, tmp2;

                cntSale = saleTemp.size();
                for (int i = 0; i < cntSale; i++) {
                    tmp = saleTemp.get(i).getAmt();
                    tmp2 = saleTemp.get(i).getAmtplusfee();
//                    if(!tmp2.equals("null"))
//                        tmp = delcomma(tmp2);
//                    else
                    tmp = delcomma(tmp);
                    amountSale += Float.valueOf(tmp);
                    amountSale = floor(amountSale * 100.f + 0.5) / 100.f;
                }

                RealmResults<QrCode> voidTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
                cntVoid = voidTemp.size();
                for (int i = 0; i < cntVoid; i++) {
                    tmp = voidTemp.get(i).getAmt();
                    tmp2 = voidTemp.get(i).getAmtplusfee();
//                    if(!tmp2.equals("null"))
//                        tmp = delcomma(tmp2);
//                    else
                    tmp = delcomma(tmp);
                    amountVoid += Float.valueOf(tmp);
                    amountVoid = floor(amountVoid * 100.f + 0.5) / 100.f;
                }

                DecimalFormat decimalFormat = new DecimalFormat("##,###,###,##0.00");

                voidSaleCountLabel.setText(String.valueOf(cntVoid));
                voidSaleAmountLabel.setText(String.valueOf(decimalFormat.format(amountVoid)));
                saleTotalLabel.setText(String.valueOf(decimalFormat.format(amountSale)));
                saleCountLabel.setText(String.valueOf(cntSale));

                cardCountLabel.setText(String.valueOf(cntSale + cntVoid));
                cardAmountLabel.setText(String.valueOf(decimalFormat.format(amountSale)));
                // Paul_20181219 End


                dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTime));
                timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(dateTime));

                if ((cntSale + cntVoid) > 0) {
                    status = 0;
                    endRecord = saleTemp.size() + voidTemp.size();
                    checkPage(endRecord);

                    if (endRecord < 20) {
                        if (endRecord == 0) {
                            settlementLister.onSuccess();
                        } else {
                            //compare date
                            int comp1, comp2;
                            if (saleTemp.size() > 0)
                                comp1 = Integer.parseInt(saleTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if (voidTemp.size() > 0)
                                comp2 = Integer.parseInt(voidTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if (comp1 < comp2) {
                                if (comp1 != 0 && comp2 != 0)
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            } else {
                                if (comp1 != 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }

                            if (saleTemp.size() > 0)
                                comp1 = Integer.parseInt(saleTemp.get(saleTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if (voidTemp.size() > 0)
                                comp2 = Integer.parseInt(voidTemp.get(voidTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if (comp1 > comp2) {
                                if (comp1 != 0 && comp2 != 0)
                                    endDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            } else {
                                if (comp1 != 0 && comp2 != 0)
                                    endDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }

                            record = String.valueOf(endRecord);

                            page = checkLength(page, 3);
                            record = checkLength(record, 3);

                            try {
                                jsonObject.put("deviceid", deviceid);
                                jsonObject.put("merid", merid);
                                jsonObject.put("storeid", storeid);
                                jsonObject.put("startDate", startDate);
                                jsonObject.put("endDate", endDate);
                                jsonObject.put("currentPage", page);
                                jsonObject.put("recordPerPage", record);

                                System.out.printf("utility:: %s , settlement = %s \n", TAG, jsonObject.toString());

                                param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
                                param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("JSON data :: ", jsonObject.toString());
                            Log.d("param1 :: ", param1);
                            Log.d("param2 :: ", param2);

                            new Thread() {
                                @Override
                                public void run() {
                                    sendMessage();
                                }
                            }.start();
                        }
                    } else {
                        settlement2(saleTemp);
                    }

                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                    hostLabel.setText(type);

                    int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
                    batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                    tidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
                    midLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));

                    selectSummaryAlipayTAXReport(type, realm);
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_DATE_ALI, dateLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TIME_ALI, timeLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_ALI, saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_ALI, saleCountLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_ALI, voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_ALI, voidSaleAmountLabel.getText().toString());

                } else if (cntVoid > 0) {
                    status = 1; //void
                } else {
                    status = 2; //wait
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (status == 0) {
                    qrView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    qrView.layout(0, 0, qrView.getMeasuredWidth(), qrView.getMeasuredHeight());
                    deleteDB();
                    int inV = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_ALI_BATCH_NUMBER_LAST, String.valueOf(inV));
                    inV = inV + 1;
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_ALI_BATCH_NUMBER, String.valueOf(inV));
                    ////doPrinting(getBitmapFromView(settlementLinearLayout));
                } else
                    deleteDB();
            }
        });
    }

    private void selectSummaryAlipayTAXReport(String typeHost, Realm realm) {
        Double totalVoid = 0.00;
        Double totalSale = 0.00;
        System.out.printf("utility:: %s selectSummaryAlipayTAXReport 0001 \n", TAG);
//        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        final RealmResults<QrCode> transTempSale = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();

        final RealmResults<QrCode> transTempVoid = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            if (transTempSale.get(i).getFee().equals("null")) //Add Jeff 20190110
                totalSale += 0.00;
            else
                totalSale += Double.valueOf(transTempSale.get(i).getFee());
        }
        System.out.printf("utility:: %s , totalSale = %f \n", TAG, totalSale);

        if (typeHost.equalsIgnoreCase("ALIPAY")) {
            hostFeeLabel.setText("ALIPAY");
        } else {
            hostFeeLabel.setText("WECHAT PAY");     // Paul_20190324
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            if (transTempVoid.get(i).getFee().equals("null")) //Add Jeff 20190110
                totalVoid += 0.00;
            else
                totalVoid += Double.valueOf(transTempVoid.get(i).getFee());
        }
        System.out.printf("utility:: %s totalVoid = %f \n", TAG, totalVoid);

        if (totalSale == 0.00 && totalVoid == 0.00)
            summaryLinearFeeLayout.setVisibility(View.GONE);
        else
            summaryLinearFeeLayout.setVisibility(View.VISIBLE);

        if (typeHost.equalsIgnoreCase("ALIPAY")) {
            System.out.printf("utility:: %s selectSummaryAlipayTAXReport 0002 \n", TAG);
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_ALI, decimalFormat.format(totalSale));
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_ALI, decimalFormat.format(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        } else {
            System.out.printf("utility:: %s selectSummaryAlipayTAXReport 0003 \n", TAG);
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_WEC, decimalFormat.format(totalSale));
            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_WEC, decimalFormat.format(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        }
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1FeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2FeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3FeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFeeLabel.setText(dateFormat.format(date));
        timeFeeLabel.setText(timeFormat.format(date));
        taxIdFeeLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAX_ID));
        saleCountFeeLabel.setText(String.valueOf(transTempSale.size()));
        saleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountFeeLabel.setText(transTempVoid.size() + "");
        voidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        cardCountFeeLabel.setText(countAll + "");
        cardAmountFeeLabel.setText(decimalFormat.format(totalSale));
    }

    private void checkPage(int endRecord) {
        int value;
        int value2;

        value = endRecord / 20;
        value2 = endRecord % 20;

        if (value == 0) {
            endPage = 1;
            page = "1";
        } else {
            if (value2 > 0)
                endPage = value + 1;
            else
                endPage = value;
            page = "1";
        }
    }

    private void sendMessage() {

        try {
            //set certification
            setCertification(context);

            if (url != null)
                url = null;

            url = new URL(data);

            if (urlConnection != null)
                urlConnection.disconnect();

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            urlConnection.setDefaultUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            setPostHeader();
            setPostBody();
            connetPost();

        } catch (IOException e) {
            e.printStackTrace();
            settlementLister.onFail();
        }
    }

    private void setPostBody() {
        try {
            jsonObject2.put("param1", param1);
            jsonObject2.put("param2", param2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        data = jsonObject2.toString();
    }

    private void connetPost() {
        try {

            if (!data.isEmpty()) { // 웹 서버로 보낼 매개변수가 있는 경우
                OutputStream os = null; // 서버로 보내기 위한 출력 스트림
                os = urlConnection.getOutputStream();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                bw.write(data); // 매개변수 전송
                bw.flush();
                bw.close();
                os.close();
            }

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                getResponseData(in);
                dialogWaiting.dismiss();
            } else {
                ali_status = AliConfig.Fail;
                settlementLister.onFail();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();
    }

    private void setPostHeader() {
        reqBy = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID) + Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID);     // Paul_20181007
        reqChannelRefId = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID) + uniqueData;     // Paul_20181007

        urlConnection.setRequestProperty("content-type", "application/json");
        urlConnection.setRequestProperty("reqBy", reqBy);
        urlConnection.setRequestProperty("reqChannel", reqChannel);
        urlConnection.setRequestProperty("reqChannelDtm", reqChannelDtm);
        urlConnection.setRequestProperty("reqChannelRefId", reqChannelRefId);
        urlConnection.setRequestProperty("service", type);

        Log.d("set_Header :: ", "");
        Log.d("reqBy", reqBy);
        Log.d("reqChannel", reqChannel);
        Log.d("reqChannelDtm", reqChannelDtm);
        Log.d("reqChannelRefId", reqChannelRefId);
        Log.d("service", type);
    }

    private void setCertification(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = null;
            FileInputStream fr;

            System.out.printf("AlipayCertRead Start %s \n", ALIPAY_CER_PATH);

            File file = new File(ALIPAY_CER_PATH);

            if (!file.exists()) {
                System.out.printf("AlipayCert file not found %s \n", ALIPAY_CER_PATH);
            } else {
                fr = new FileInputStream(file);

                ca = cf.generateCertificate(fr);
                System.out.printf("ca =" + ((X509Certificate) ca).getSubjectDN());
                fr.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    private String delcomma(String amount) {
        String result_amount;
        result_amount = amount.toString().replaceAll(",", "");
        return result_amount;
    }

    private String checkLength(String trace, int i) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for (int j = 0; j < (i - tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }

    private String makeUniqueData(Date data) {

        String temp = dateFormat2.format(data);  // yyyyMMddhhmmss
        String julian_date = dateFormat3.format(data);

        String temp_year = temp.substring(0, 4);
        String temp_time = temp.substring(8, 14);
        temp_year = String.valueOf(Integer.parseInt(temp_year) + 483);

        temp = temp_year.substring(3, 4) + julian_date + temp_time;

        return temp;
    }

    private void selectSettlementWECHAT() {
        type = "WECHAT";
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amountSale = 0.00;
                amountVoid = 0.00;

                dateTime = null;
                dateTime = new Date();
                reqChannelDtm = dateFormat.format(dateTime); // + dateFormat_time.format(dateTime);
                uniqueData = makeUniqueData(dateTime);

//                data = aliConfig.getHttps() + "transaction/inquiry";
                alipay_http = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_URL_ID); //20181114Jeff
                ALIPAY_CER_PATH = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_CERTI_ID); //20181115Jeff

                if (realm == null)
                    realm = Realm.getDefaultInstance();
                else {
                    realm = null;
                    realm = Realm.getDefaultInstance();
                }

                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
                String tmp, tmp2;

                cntSale = saleTemp.size();
                for (int i = 0; i < cntSale; i++) {
                    tmp = saleTemp.get(i).getAmt();
                    tmp2 = saleTemp.get(i).getAmtplusfee();
                    if (!tmp2.equals("null"))
                        tmp = delcomma(tmp2);
                    else
                        tmp = delcomma(tmp);
                    amountSale += Float.valueOf(tmp);
                    amountSale = floor(amountSale * 100.f + 0.5) / 100.f;
                }

                RealmResults<QrCode> voidTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
                cntVoid = voidTemp.size();
                for (int i = 0; i < cntVoid; i++) {
                    tmp = voidTemp.get(i).getAmt();
                    tmp2 = voidTemp.get(i).getAmtplusfee();
                    if (!tmp2.equals("null"))
                        tmp = delcomma(tmp2);
                    else
                        tmp = delcomma(tmp);
                    amountVoid += Float.valueOf(tmp);
                    amountVoid = floor(amountVoid * 100.f + 0.5) / 100.f;
                }

                DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

                voidSaleCountLabel.setText(String.valueOf(cntVoid));
                voidSaleAmountLabel.setText(String.valueOf(decimalFormat.format(amountVoid)));
                saleTotalLabel.setText(String.valueOf(decimalFormat.format(amountSale)));
                saleCountLabel.setText(String.valueOf(cntSale));

                cardCountLabel.setText(String.valueOf(cntSale + cntVoid));
                cardAmountLabel.setText(String.valueOf(decimalFormat.format(amountSale)));
                dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTime));
                timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(dateTime));

//                cntSale = 0;  // only test
//                cntVoid = 0;  // only test

                if ((cntSale + cntVoid) > 0) {
                    status = 0;
                    endRecord = saleTemp.size() + voidTemp.size();
                    checkPage(endRecord);

                    if (endRecord < 20) {
                        if (endRecord == 0) {
                            settlementLister.onSuccess();
                        } else {
                            //compare date
                            int comp1, comp2;
                            if (saleTemp.size() > 0)
                                comp1 = Integer.parseInt(saleTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if (voidTemp.size() > 0)
                                comp2 = Integer.parseInt(voidTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if (comp1 < comp2) {
                                if (comp1 != 0 && comp2 != 0)
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            } else {
                                if (comp1 != 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }

                            if (saleTemp.size() > 0)
                                comp1 = Integer.parseInt(saleTemp.get(saleTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if (voidTemp.size() > 0)
                                comp2 = Integer.parseInt(voidTemp.get(voidTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if (comp1 > comp2) {
                                if (comp1 != 0 && comp2 != 0)
                                    endDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            } else {
                                if (comp1 != 0 && comp2 != 0)
                                    endDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if (comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }

                            record = String.valueOf(endRecord);

                            page = checkLength(page, 3);
                            record = checkLength(record, 3);

                            try {
                                jsonObject.put("deviceid", deviceid);
                                jsonObject.put("merid", merid);
                                jsonObject.put("storeid", storeid);
                                jsonObject.put("startDate", startDate);
                                jsonObject.put("endDate", endDate);
                                jsonObject.put("currentPage", page);
                                jsonObject.put("recordPerPage", record);

                                System.out.printf("utility:: %s , settlement = %s \n", TAG, jsonObject.toString());

                                param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//                                param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH); //20181114Jeff
                                param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("JSON data :: ", jsonObject.toString());
                            Log.d("param1 :: ", param1);
                            Log.d("param2 :: ", param2);

                            new Thread() {
                                @Override
                                public void run() {
                                    sendMessage();
                                }
                            }.start();
                        }
                    } else {
                        settlement2(saleTemp);
                    }

                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                    hostLabel.setText("WECHAT PAY");        // Paul_20190324

                    int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
                    batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                    tidLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
                    midLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));

                    selectSummaryAlipayTAXReport(type, realm);
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_DATE_WEC, dateLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TIME_WEC, timeLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_WEC, saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_WEC, saleCountLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_WEC, voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_WEC, voidSaleAmountLabel.getText().toString());

                } else if (cntVoid > 0) {
                    status = 1; //void
//                    dialogSettlement.dismiss();
//                        deleteDB_void();
                } else {
                    status = 2; //wait
//                    dialogSettlement.dismiss();
//                        deleteDB_wait();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (status == 0) {
                    deleteDB();
                    qrView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    qrView.layout(0, 0, qrView.getMeasuredWidth(), qrView.getMeasuredHeight());
                    int inV = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_WEC_BATCH_NUMBER_LAST, String.valueOf(inV));
                    inV = inV + 1;
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_WEC_BATCH_NUMBER, String.valueOf(inV));
                    ////doPrinting(getBitmapFromView(settlementLinearLayout));
                } else
                    deleteDB();
            }
        });
    }

    private void deleteDB() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<QrCode> transTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).findAll();
                transTemp.deleteAllFromRealm();
            }
        });
    }

    private void settlement2(RealmResults<QrCode> saleTemp) {

        settlementDataset(saleTemp);

        try {
            jsonObject.put("deviceid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));    // Paul_20181007
            jsonObject.put("merid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));          // Paul_20181007
            jsonObject.put("storeid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_STORE_ID));      // Paul_20181007

            jsonObject.put("startDate", startDate);
            jsonObject.put("endDate", endDate);
            jsonObject.put("currentPage", page);
            jsonObject.put("recordPerPage", record);

            System.out.printf("utility:: %s , settlement2 = %s \n", TAG, jsonObject.toString());

            param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("JSON data :: ", jsonObject.toString());
        Log.d("param1 :: ", param1);
        Log.d("param2 :: ", param2);

        sendMessage2();
    }

    private void sendMessage2() {

        try {
            //set certification
            setCertification(context);

            if (url == null)
                url = new URL(data);

            if (urlConnection != null)
                urlConnection.disconnect();

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            urlConnection.setDefaultUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

//            setTimer(15000);

            setPostHeader();
            setPostBody();
            connetPost2();

        } catch (IOException e) {
            e.printStackTrace();
            settlementLister.onFail();
        }
    }

    private void connetPost2() {
        try {

            if (!data.isEmpty()) { // 웹 서버로 보낼 매개변수가 있는 경우
                OutputStream os = null; // 서버로 보내기 위한 출력 스트림
                os = urlConnection.getOutputStream();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                bw.write(data); // 매개변수 전송
                bw.flush();
                bw.close();
                os.close();
            }

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                getResponseData2(in);
                dialogWaiting.dismiss();
            } else {
                ali_status = AliConfig.Fail;
                settlementLister.onFail();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();
    }

    private void getResponseData(InputStream in) {
        final String data = readData(in);
        String tmp;
        JSONObject obj;
        JSONObject obj2;
        try {
            tmp = cryptoServices.decryptAES(data, cryptoServices.AES_KEY);

            obj = new JSONObject(tmp);
            ali_status = obj.getString("status");
            result = obj.getString("result");
            if (!result.equals("null")) {

                if (ali_status.equals(AliConfig.Success))
                    settlementLister.onSuccess();
                else
                    settlementLister.onFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getResponseData2(InputStream in) {
        final String data = readData(in);
        String tmp;
        JSONObject obj;
        JSONObject obj2;
        try {
            tmp = cryptoServices.decryptAES(data, cryptoServices.AES_KEY);

            obj = new JSONObject(tmp);
            ali_status = obj.getString("status");
            result = obj.getString("result");
            if (!result.equals("null")) {
                if (ali_status.equals(AliConfig.Success))
                    settlementLister.onContinue();
                else
                    settlementLister.onFail();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readData(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }

    interface SettlementLister {
        public void onSuccess();

        public void onContinue();

        public void onFail();
    }

    private void settlementDataset(RealmResults<QrCode> saleTemp) {
        int cnt = Integer.parseInt(page) - 1;

        if (page.equals(String.valueOf(endPage)))
            record = String.valueOf((saleTemp.size() - (20 * cnt)) - 1);
        else
            record = "20";

        if (page.equals(String.valueOf(endPage))) {
            startDate = saleTemp.get(20 * cnt).getReqChannelDtm().substring(0, 10);
            endDate = saleTemp.get(saleTemp.size() - 1).getReqChannelDtm();
        } else {
            startDate = saleTemp.get(20 * cnt).getReqChannelDtm().substring(0, 10);
            endDate = saleTemp.get((20 * (cnt + 1)) - 1).getReqChannelDtm().substring(0, 10);
        }
    }

    private void selectSettlementQRAll() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        } else {
            realm.close();
            realm = Realm.getDefaultInstance();
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("statusSuccess", "1").findAll(); // Paul_20181020
                Double amountSaleQr = 0.0;
                float amountVoidQr = 0;
                if (qrCode.size() > 0) {
                    status = 0;
                    for (int i = 0; i < qrCode.size(); i++) {
                        amountSaleQr += Double.valueOf(qrCode.get(i).getAmount().replaceAll(",", ""));
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");        // Paul_20190117 amount comma

                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1HgcLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2HgcLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3HgcLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_3));


                    Date date = new Date();
                    voidSaleCountHgcLabel.setText("0");
                    voidSaleAmountHgcLabel.setText(String.format("%.2f", 0.0));
                    saleCountHgcLabel.setText(qrCode.size() + "");
                    saleTotalHgcLabel.setText(decimalFormat.format(amountSaleQr));     // Paul_20190117 add to decimalFormat.format
                    cardCountHgcLabel.setText(qrCode.size() + "");
                    cardAmountHgcLabel.setText(decimalFormat.format(amountSaleQr));     // Paul_20190117 add to decimalFormat.format

                    dateHgcLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    timeHgcLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                    hostHgcLabel.setText("KTB QR");
                    int batch = Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
                    batchHgcLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_QR_BATCH_NUMBER, String.valueOf((batch + 1)));
                    tidHgcLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID));
                    midHgcLabel.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_QR_MERCHANT_ID));

                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_DATE_QR, dateHgcLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_TIME_QR, timeHgcLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR, saleTotalHgcLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_QR, saleCountHgcLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_QR, voidSaleCountHgcLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR, voidSaleAmountHgcLabel.getText().toString());
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_QR, CardPrefix.calLen(String.valueOf(batch), 6));    // Paul_20181120 please no remark last settlement reprint problem
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_QR_BATCH_NUMBER, CardPrefix.calLen(String.valueOf(batch + 1), 6));  ////SINN 20181025

                } else {
                    status = 1;
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                deleteQrAll();

                if (status == 0) {
                    hgcView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    hgcView.layout(0, 0, hgcView.getMeasuredWidth(), hgcView.getMeasuredHeight());
                    //(getBitmapFromView(settlementHgcLinearLayout));
                } else {
                    settlementForupdate();
                }
            }
        });

    }

    private void dismissDialogAll() {
// Paul_20181024
        if (dialogWaiting != null && dialogWaiting.isShowing())
            dialogWaiting.dismiss();
        if (timer != null)
            timer.cancel();
//        if (dialogInsertCard != null)
//            dialogInsertCard.cancel();
        if (dialogInsertCard != null && dialogInsertCard.isShowing())   // Paul_20181019
            dialogInsertCard.dismiss();
        if (dialogFallBack != null && dialogFallBack.isShowing())
            dialogFallBack.dismiss();
        if (dialogServiceCode != null && dialogServiceCode.isShowing())
            dialogServiceCode.dismiss();

    }

    //Check ว่า Reversal ได้ไหม
    private void callReversal() {
        cardManager.setReversalListener(new CardManager.ReversalListener() {
            @Override
            public void onReversalSuccess() {
                Log.d(TAG, "onReversalSuccess: ");
                dismissDialogAll();
                cardManager.stopTransaction();
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //                            Utility.customDialogAlertSuccess(MenuServiceListActivity.this, "Reversal สำเร็จ", new Utility.OnClickCloseImage() {
                            Utility.customDialogAlertSuccessAuto(MenuServiceListActivity.this, "Reversal สำเร็จ", new Utility.OnClickCloseImage() {    //20180716 reversal auto close
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    // Paul_20180717 Start
                                    if (posinterface.PosInterfaceExistFlg == 1)      // Paul_20180717
                                    {
                                        System.out.printf("utility:: Reversal RECEIVE OK \n");
                                        final Intent[] intent = {null};
                                        int i;
                                        switch (posinterface.PosInterfaceTransactionCode) {
                                            case "11":      // ผู้ป่วยนอกทั่วไป สิทธิตนเองและครอบครัว
                                            case "21":      // หน่วยไตเทียม สิทธิตนเองและครอบครัว
                                            case "31":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิตนเองและครอบครัว
                                                intent[0] = new Intent(MenuServiceListActivity.this, IDActivity.class);
                                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                                intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                                startActivity(intent[0]);
                                                overridePendingTransition(0, 0);
                                                break;
                                            case "12":      // ผู้ป่วยนอกทั่วไป สิทธิบุตร 0-7 ปี
                                            case "22":      // หน่วยไตเทียม สิทธิบุตร 0-7 ปี
                                            case "32":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิบุตร 0-7 ปี
                                                if (cardCd == null) {
                                                    cardCd = "0000000000000";
                                                }
                                                for (i = cardCd.length(); i < 13; i++) {
                                                    cardCd += " ";
                                                }
                                                if (Long.valueOf(cardCd) == 0) {
                                                    intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                                    intent[0].putExtra(KEY_INTERFACE_CARD_ID_CHILD, cardCd);
                                                    startActivity(intent[0]);
                                                    overridePendingTransition(0, 0);
                                                } else {
                                                    intent[0] = new Intent(MenuServiceListActivity.this, IDActivity.class);
                                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                                    intent[0].putExtra(KEY_ID_CARD_CD, cardCd);
                                                    startActivity(intent[0]);
                                                    overridePendingTransition(0, 0);
                                                }
                                                break;
                                            case "13":      // ผปู้่ายนอกทวั่ไป สิทธิคู่สมรสต่างชาติ
                                            case "23":      // หน่วยไตเทียม สิทธิคู่สมรสต่างชาติ
                                            case "33":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิคู่สมรสต่างชาติ
                                                if (idForeigner == null) {
                                                    idForeigner = "B000000000000";      // Paul_20180705
                                                }
                                                for (i = idForeigner.length(); i < 13; i++) {
                                                    idForeigner += " ";
                                                }
                                                if (Long.valueOf(idForeigner.substring(1, idForeigner.length())) == 0) {
                                                    intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, idForeigner);
                                                    startActivity(intent[0]);
                                                    overridePendingTransition(0, 0);
                                                } else {
//                                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);//
//                 intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);  //SINN 20181015 SINN GHC UI
                                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivityNew.class);
                                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, idForeigner);
                                                    startActivity(intent[0]);
                                                    overridePendingTransition(0, 0);
                                                }
                                                break;
                                            case "14":      // ผู้ป่วยนอกทั่วไป ไม่สามารถใช้บัตรได้
                                            case "24":      // หน่วยไตเทียม ไม่สามารถใช้บัตรได้
                                            case "34":      // หน่วยรังสีผู้เป็นมะเร็ง ไม่สามารถใช้บัตรได้
                                                if (nocardCd == null) {
                                                    nocardCd = "0000000000000";
                                                }
                                                for (i = nocardCd.length(); i < 13; i++) {
                                                    nocardCd += " ";
                                                }
                                                if (Long.valueOf(nocardCd) == 0) {   // if (Long.valueOf(idForeigner.substring(1, nocardCd.length())) == 0) {
                                                    intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, nocardCd);
                                                    startActivity(intent[0]);
                                                    overridePendingTransition(0, 0);
                                                } else {
//                                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);
                                                    //                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);  //SINN 20181015 SINN GHC UI
                                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivityNew.class);
                                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, nocardCd);
                                                    startActivity(intent[0]);
                                                    overridePendingTransition(0, 0);
                                                }
                                                break;
                                            case "20":
                                                localtypeInterface = "Interface";  //set rs232 KEY_TYPE_INTERFACE
                                                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF1, "");
                                                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF2, "");
                                                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF3, "");


//                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "0");  //SINN RS232 SALE 20180709;
                                                cardManager.setFalseFallbackHappen();
                                                if (checkReversal("SALE")) {

                                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF1, ref1);
                                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF2, ref2);
                                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF3, ref3);


                                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "1");  //SINN RS232 SALE 20180709;
                                                    startInsertCard();
                                                }
                                                break;
                                            case "26":      // รายการยกเลกิ     // void
                                                intent[0] = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                intent[0].putExtra(KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                                                intent[0].putExtra(KEY_INTERFACE_VOID_APPROVAL_CODE, approvalCode);     // Paul_20180716
                                                startActivity(intent[0]);
                                                overridePendingTransition(0, 0);
                                                break;
                                            case "50":
                                                intent[0] = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                                startActivity(intent[0]);
                                                overridePendingTransition(0, 0);
                                                break;
                                        }
// Paul_20180717 End
                                    } else if (typeClick.equalsIgnoreCase("SALE")) {


                                        Rf_Handler.sendEmptyMessageDelayed(0, 2000);
                                        setCheckCallBackCard();

                                        Log.d(TAG, "onReversalSuccess: SALE");
//                                        customDialog_InsertAmount();   ////SINN 20180921  reversal and then got null  tv_confirm_amount.setText(amountBox_new.getText().toString());
                                        if (dialogInsertAmountForSale != null && !dialogInsertAmountForSale.isShowing())     // Paul_20181025
                                        {
//                                        if (isShowInsertCard) {
////                                            custoDialog_SelectAmount();
//                                            Rf_Handler.sendEmptyMessageDelayed(0, 2000);
//                                            startInsertCard();

//                                        if (is_dialogInsertAmountShowing){
//                                            cardManager = MainApplication.getCardManager();
//                                            printDev = cardManager.getInstancesPrint();
//
                                            startInsertCard();


                                        }
                                    } else if (typeClick.equalsIgnoreCase("VOID")) {
                                        Log.d(TAG, "onReversalSuccess: VOID");
                                        if (!checkReversalGHC()) {
                                            Intent intent = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(0, 0);
                                        } else {
                                            sendDataReversal(reversalHealthCare);
                                        }
                                    } else if (typeClick.equalsIgnoreCase("SETTLEMENT")) {
                                        Log.d(TAG, "onReversalSuccess: SETTLEMENT");
                                        if (!checkReversalGHC()) {
                                            Intent intent = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(0, 0);
                                        } else {
                                            sendDataReversal(reversalHealthCare);
                                        }
                                    }
                                }
                            });

                        }
                    });
                }

            }
        });
    }

    private void startInsertCard() {
        amountInterface = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_RATE_PRICE);

        is_dialogInsertAmountShowing = false;
        Log.d(TAG, "Start :startInsertCard()");
        cardManager.setAmountforContactless(amountInterface.replace(",", ""));

        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_CONTACTLESS_ID).equalsIgnoreCase("1")) {
            Double __amount = Double.parseDouble(amountInterface.replace(",", ""));
            Double __max_ctl = Double.parseDouble(Preference.getInstance(context).getValueString(Preference.KEY_MAX_CONTACTLESS_ID));

            if (__amount <= __max_ctl) {
                lineartranstype1.setVisibility(View.GONE);
                lineartranstype2.setVisibility(View.VISIBLE);
            } else {
                lineartranstype1.setVisibility(View.VISIBLE);
                lineartranstype2.setVisibility(View.GONE);
            }
        }


        if(!searchCardThread.isAlive()) {
            cardManager.setReaderType(EReaderType.MAG_PICC);
            searchCardThread.start();
        }





        System.out.printf("utility:: %s startInsertCard 001 \n", TAG);

//        cardmanager.startTransaction(CardManager.sale, amountinterface);//เปิดอ่านการ์ด

        Rf_Handler.sendEmptyMessageDelayed(0, 2000);


//        setTimer(60000, 1); //20181013 SINN Amount  // Paul_20181017 time out 60 sec

        System.out.printf("utility:: %s startInsertCard 002 \n", TAG);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                setTimer(15000, 1);
                Log.d("1919_amountInterface", amountInterface);
////                tv_confirm_amount.setText(numberPrice);//K.GAME 180914 set amount dialog
//                tv_confirm_amount.setText(amountBox_new.getText().toString());//K.GAME 180914 set amount dialog// แก้บัค ตัวเลขไม่มีทศนิยม //บัค
////                amountInterface = numberPrice;//K.GAME 180914 set amount dialog
//                amountInterface = amountBox_new.getText().toString();//K.GAME 180914 set amount dialog // แก้บัค ตัวเลขไม่มีทศนิยม
                if (typeInterface != null || (posinterface.PosInterfaceExistFlg == 1)) {
                    customDialog();
                    System.out.printf("utility:: %s startInsertCard amountInterface = %s  \n", TAG, amountInterface);


                    decFormat = new DecimalFormat("##,###,##0.00"); //K.GAME 180917

                    System.out.printf("utility:: %s startInsertCard decFormat.format(Double.valueOf(amountInterface) / 100 = %s  \n", TAG, decFormat.format(Double.valueOf(amountInterface) / 100));
                    tv_confirm_amount.setText(decFormat.format(Double.valueOf(amountInterface) / 100));

//                    amountInterface = amountInterface.replaceFirst("^0*", "");
//                    if (amountInterface.isEmpty()) amountInterface = "0";

//                    System.out.printf("utility:: %s , initData 0001 amt = %s \n",TAG,amountInterface);
//                    amountInterface = decFormat.format(Double.valueOf(amountInterface) / 100);              // Paul_20181019 Amount Display method 0.03
//                    System.out.printf("utility:: %s , initData 0002 amt = %s \n",TAG,amountInterface);
//                    tv_confirm_amount.setText(amountInterface);

                } else {
                    tv_confirm_amount.setText(amountInterface);
                }
                if (dialogInsertCard != null)    // Paul_20181016
                {
                    if (isFinishing() == false)      // Paul_20181016 K.hong advice
                    {
                        System.out.printf("utility:: %s 00000000001 isFinishing() = false \n", TAG);
//                        dialogInsertCard.show();

                        dialogContactless = null;

                        customContactless();
                    } else {
                        System.out.printf("utility:: %s 00000000002 isFinishing() = true \n", TAG);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (isFinishing() == false)      // Paul_20181016 K.hong advice
                        {
                            System.out.printf("utility:: %s 00000000003 isFinishing() = false \n", TAG);
//                            dialogInsertCard.show();

                            dialogContactless = null;

                            customContactless();
                        } else {
                            System.out.printf("utility:: %s 00000000004 isFinishing() = true \n", TAG);
                        }
                    }
                }
            }
        });

    }

    private void setMenuList() {
        if (recyclerViewMenuList.getAdapter() == null) {

            menuServiceAdapter = new MenuServiceAdapter(this);
            recyclerViewMenuList.setAdapter(menuServiceAdapter);


            menuServiceAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
//                    System.out.printf("utility:: menuServiceAdapter.setOnClickListener(new View.OnClickListener() position = %d \n",position);

                    System.out.printf("utility:: nameMenuList.get( position ) = %s \n", nameMenuList.get(position));
                    numFallBack = 0;
                    /***
                     * New
                     * 0 = QR
                     * 1 = Sale
                     * 2 = HealthCare
                     * 3 = Settlement
                     * 4 = Void
                     * 5 = Offline
                     * 6 = TestHost
                     * 7 = Report
                     * 8 = Reprint
                     * 9 = Setting
                     *
                     * Old
                     * 0 = Sale
                     * 1 = Void
                     * 2 = Settlement
                     * 3 = Report
                     * 4 = Reprint
                     *
                     nameMenuList.add("รายการ QR");
                     nameMenuList.add("รายการขาย");
                     nameMenuList.add("ใช้สิทธิ์รักษาพยาบาล");
                     nameMenuList.add("สรุปยอด");
                     nameMenuList.add("ยกเลิกรายการ");
                     nameMenuList.add("ทำรายการออฟไลน์");
                     nameMenuList.add("ทดสอบโฮซ์ท");
                     nameMenuList.add("พิมพ์รายงาน");
                     nameMenuList.add("พิมพ์ซ้ำรายการ");
                     nameMenuList.add("ตั้งค่า");

                     */

//20180723 SINNN power wakeup
//                    PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//                    PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
//                    wakeLock.acquire();
// Paul_20180809 Start
                    switch (nameMenuList.get(position)) {
                        case "คิวอาร์โค้ด":         // QR
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            if (checkAllBatch() != 1)    // Paul_20180803
                                CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                            if (checkBatchSettlement() != 1) {       // Paul_20180803
                                Intent intent = new Intent(MenuServiceListActivity.this, MenuQrActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }
                            break;
//                        case "รายการขาย":
                        case "ชำระค่าทางด่วน":      // SALE

                            numberPrice = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
                            amountInterface = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
                            if (checkAllBatch() != 1)    // Paul_20180803
                                CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                            //SINN 20181212  check first settlement
//                String valueParameterEnable = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1000);  //para enable
//                if (valueParameterEnable.isEmpty()) {
//                    Utility.customDialogAlert(MenuServiceListActivity.this, "กรุณา First Settlement ก่อนทำรายการ", new Utility.OnClickCloseImage() {
//                        @Override
//                        public void onClickImage(Dialog dialog) {
//                            dialog.dismiss();
//                        }
//                    });
//                    break;
//                }
                            ////SINN 20181129 Add way2 to UAT6
                            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SERVICE_PIN_ID, "0");

//                            numberPrice = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
//                            amountInterface = "0.00";//K.GAME 180926 แก้บัค ตัวเลขค้าง
//                            if (checkAllBatch() != 1)    // Paul_20180803
//                                CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                            GHCVoidFlg = 0;
                            if (checkBatchSettlement() != 1) {       // Paul_20180803
                                System.out.printf("utility:: %s setMenuList 001 \n", TAG);
                                cardManager.setFalseFallbackHappen();
                                System.out.printf("utility:: %s setMenuList 002 \n", TAG);
                                if (checkReversal("SALE")) {
                                    System.out.printf("utility:: %s setMenuList 003 \n", TAG);
//                                    if (!dialogSelectAmountForSale) {           // Paul_20181205 K.hong double click wrong
                                    is_dialogInsertAmountShowing = true;
//                                        customDialog_InsertAmount();//K.GAME 180914 New dialog
//                                        custoDialog_SelectAmount();
                                    Rf_Handler.sendEmptyMessageDelayed(0, 2000);
                                    startInsertCard();
//                                    }//                                    startInsertCard();
                                }
                            }
                            break;
                        case "ใช้สิทธิ์\nรักษาพยาบาล":
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            if (checkBatchSettlement() != 1) {       // Paul_20180803
                                isOffline = false;
                                if (checkReversal("GHC")) { //PAUL_20180718
                                    if (!checkReversalGHC()) {
                                        Intent intent = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    } else {
                                        sendDataReversal(reversalHealthCare);
                                    }
                                }
                            }
                            break;
                        case "สรุปยอด\nประจำวัน":
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            if (checkReversal("SETTLEMENT")) {
                                if (!checkReversalGHC()) {
                                    Intent intent = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                } else {
                                    sendDataReversal(reversalHealthCare);
                                }
                            }
                            break;
                        case "ยกเลิก\nรายการ":
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            GHCVoidFlg = 1;
                            if (checkBatchSettlement() != 1) {       // Paul_20180803
                                if (checkReversal("VOID")) {
                                    if (!checkReversalGHC()) {
                                        Intent intent = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    } else {
                                        sendDataReversal(reversalHealthCare);
                                    }
                                }
                            }
                            break;
                        case "ทำรายการ\nออฟไลน์":
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            if (checkBatchSettlement() != 1) {       // Paul_20180803
                                isOffline = true;
//                                customDialogPassword();
                                customDialogPassword_input4();
                                dialogPassword.show();
                            }
                            break;
                        case "ทดสอบ\nโฮซ์ท":
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
//                            dialogHost.show();
//                            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1"))
                            if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1") ||
                                    ((Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length() < 8) &&
                                            (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length() < 8) &&
                                            (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length() < 8)))
                                cardManager.setDataTestHostEPS();
                            else
                                dialogHost.show();

                            break;
                        case "พิมพ์\nรายงาน":
                            /******************
                             Input String "111":			// ONUS RKI
                             "112":			// ONUS RKI TLE
                             "211":			// OFFUS RKI
                             "212":			// OFFUS RKI TLE
                             "213":			// OFFUS RKI TLE WORKINGKEY
                             "311":			// ONUS RKI OFFUS RKI
                             "312":			// ONUS RKI OFFUS RKI TLE
                             "313":			// ONUS RKI OFFUS RKI TLE WORKINGKEY
                             "321":			// ONUS RKI TLE OFFUS RKI
                             "322":			// ONUS RKI TLE OFFUS RKI TLE
                             "323":			// ONUS RKI TLE OFFUS RKI TLE WORKINGKEY
                             String result = cardManager.AutoTLE("323");
                             System.out.printf("utility:: %s cardManager.AutoTLE result = %s \n",TAG,result);
                             try {
                             Thread.sleep( 2000 );
                             } catch (InterruptedException e) {
                             e.printStackTrace();
                             }
                             if(result.equalsIgnoreCase( "100" ))
                             {
                             Utility.customDialogAlertSuccessAuto(MenuServiceListActivity.this, "Success", new Utility.OnClickCloseImage() {
                            @Override public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            }
                            });
                             }
                             else
                             {
                             Utility.customDialogAlertFailAuto(MenuServiceListActivity.this, "Fail", new Utility.OnClickCloseImage() {
                            @Override public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            }
                            });
                             }
                             ********************/
/*

                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuDetailReportActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
*/
                            Utility.customDialogAlert(MenuServiceListActivity.this, "This terminal not support", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                            break;
                        case "พิมพ์\nสำเนาสลิป":
/*
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            Intent intent1 = new Intent(MenuServiceListActivity.this, ReprintActivity.class);
                            startActivity(intent1);
                            overridePendingTransition(0, 0);
*/
                            Utility.customDialogAlert(MenuServiceListActivity.this, "This terminal not support", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                            break;

                        case "ตรวจสอบ\nบัตรประชาชน":
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            Intent intent2 = new Intent(MenuServiceListActivity.this, IDActivity2.class);
                            intent2.putExtra("tv_insertIdCard_01", "ตรวจสอบบัตรประชาชน");
                            startActivity(intent2);
                            overridePendingTransition(0, 0);
                            break;

                        case "ตั้งค่า":
                            if (!isSettingForUser) {
                                numberPrice = "";
                                isSettingForUser = true;
                                customDialogPin_new();
//                                dialogcustomDialogPin_new.show();
                            }
                            break;
                        case "ชำระเงิน":
                            is_dialogInsertAmountShowing = false;       // Paul_20181207
                            //K.GAME change Menu setting and UI
//                            customDialog_InsertAmount();//K.GAME 180914 New dialog
                            Intent intent4 = new Intent(MenuServiceListActivity.this, MenuPaymentActivity.class);
                            startActivity(intent4);
                            overridePendingTransition(0, 0);
                            break;
                        default:
                            is_dialogInsertAmountShowing = false;       // Paul_20181211 K.hong double click wrong
                            break;
                        ///////////////////////////////////
//                        case "Paul Test":
//                            deleteQrAll();
//                            break;
                    }


                }
            });
        } else {
            menuServiceAdapter.clear();
        }
        menuServiceAdapter.setItem(nameMenuList);
        menuServiceAdapter.notifyDataSetChanged();
    }

    private void deleteQrAll() {

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        } else {
            realm.close();
            realm = Realm.getDefaultInstance();
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<org.centerm.Tollway.database.QrCode> qrCode = realm.where(org.centerm.Tollway.database.QrCode.class).equalTo("hostTypeCard", "QR").findAll(); // Paul_20181020
                qrCode.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }
        });
    }

    // Paul_20180705
    private void ProcessPosinterface(String InputString) {

        int rv;
        rv = posinterface.CheckPOSInterface(InputString);

        if (rv == 0) {
//            cardManager = MainApplication.getCardManager();
//            displayControlDev = cardManager.getInstancesDisplay();
//            try {
//                displayControlDev.open();
//                displayControlDev.ledctrl( Constant.LEDTYPE.LED_PRICE );
//                displayControlDev.clear();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }

            posinterface.PosInterfaceExistFlg = 1;
            // Paul_20180716 Start
            amountInterface = null;      // Paul_20180711
            cardCd = null;
            idForeigner = null;
            nocardCd = null;
            invoiceId = null;

            ref1 = null;
            ref2 = null;
            ref3 = null;
            F1_POS_MSG = "     ";   ////sinn 20180712 add pos reprint
            approvalCode = null;      // Paul_20180716
            // Paul_20180716 End
            int i;
            for (i = 0; i < posinterface.PosInterfaceTotalFieldCnt; i++) {
                System.out.printf("utility:: PosInterfaceFieldType = %s \n", posinterface.PosInterfaceFieldType[i]);
                System.out.printf("utility:: PosInterfaceFieldData = %s \n", posinterface.PosInterfaceFieldData[i]);
// Paul_20180716
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("01")) {
                    approvalCode = posinterface.PosInterfaceFieldData[i];
                }
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("40")) {
                    amountInterface = posinterface.PosInterfaceFieldData[i];
                }
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("71")) {
                    cardCd = posinterface.PosInterfaceFieldData[i];
                }
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("72")) {
                    idForeigner = posinterface.PosInterfaceFieldData[i];
                }
// Paul_20180703
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("73")) {
                    nocardCd = posinterface.PosInterfaceFieldData[i];
                }
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("65")) {
                    invoiceId = posinterface.PosInterfaceFieldData[i];
                }

// Paul_20180705
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("A1")) {
                    ref1 = posinterface.PosInterfaceFieldData[i];
                }
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("A2")) {
                    ref2 = posinterface.PosInterfaceFieldData[i];
                }
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("A3")) {
                    ref3 = posinterface.PosInterfaceFieldData[i];
                }
//SINN 20180710 POS REPRINT
                if (posinterface.PosInterfaceFieldType[i].equalsIgnoreCase("F1")) {
                    F1_POS_MSG = posinterface.PosInterfaceFieldData[i];
                }
//END SINN 20180709 POS REPRINT

//
////SINN 20180711 Fix Ref is null app close
//                if(ref1!=null)
//                    ref1 = ref1.trim();
//                if(ref1==null)
//                    ref1=" ";
//
//                if(ref2!=null)
//                    ref2 = ref2.trim();
//                if(ref2==null)
//                    ref2=" ";
//
//                if(ref3!=null)
//                    ref3 = ref3.trim();
//                if(ref3==null)
//                    ref3=" ";
////END SINN 20180711 Fix Ref is null app close
            }

//SINN 20180711 Fix Ref is null app close
            if (ref1 != null)
                ref1 = ref1.trim();
            if (ref1 == null)
                ref1 = " ";
            if (ref2 != null)
                ref2 = ref2.trim();
            if (ref2 == null)
                ref2 = " ";
            if (ref3 != null)
                ref3 = ref3.trim();
            if (ref3 == null)
                ref3 = " ";
//END SINN 20180711 Fix Ref is null app close
//20180723 SINNN power wakeup

            Log.d("SINN", "ProcessPosinterface REF1:" + ref1 + "REF2:" + ref2 + "REF3:" + ref3);

            //-----------------------------------------------------------------------------------------------------
            //Parameter Control here;

            String valueParameterEnable = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1000);  //para enable

            Log.d("SINN", "valueParameterEnable:" + valueParameterEnable);
            if (!valueParameterEnable.isEmpty()) {
                if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {           //use TMS REF3
                    ref3 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1004);
                } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {   // //display & edit  ref3Box
                    //ref3 allowed
                } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {    // hide   ref3Box.
                    ref3 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1004);
                } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("1")) {      //disble
                    ref3 = " ";
                } else
                    ref3 = " ";
//------------------------------------------------------

                if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {         //display cannot edit  ref2Box
                    ref2 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1003);
                } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {   //display & edit  ref2Box
                    //ref2 allowd
                } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {     // hide  ref2Box
                    ref2 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1003);
                } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("1")) {
                    ref2 = " ";
                } else
                    ref2 = " ";
//------------------------------------------------------
                if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {//display cannot edit ref1Box
                    ref1 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1002);
                } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {   //display & edit ref1Box
                    //ref1 allowd
                } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {// hide ref1Box
                    ref1 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TAG_1002);
                } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("1")) {
                    ref1 = " ";
                } else
                    ref1 = " ";
            }


            pos_interface_Handler.sendEmptyMessageDelayed(0, 2000); //Paul_20180719

        } else if (rv == 2) {
            posinterface.PosInterfaceSendData("06");    // ACK SEND Paul_20181020
            posinterface.PosInterfaceExistFlg = 1;  // Paul_20180731
            Intent intent = null;
//            intent = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
            intent = new Intent(MenuServiceListActivity.this, PosLinkSendActivity.class);
            intent.putExtra(KEY_TYPE_INTERFACE, "Interface");
            intent.putExtra(KEY_INTERFACE_TYPE, "02");


//            intent.putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
//            intent.putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
//            intent.putExtra(KEY_ID_FOREIGNER_NUMBER, idForeigner);
            startActivity(intent);
            overridePendingTransition(0, 0);
/*
            posinterface.PosInterfaceExistFlg = 1;  // Paul_20180731
//            posinterface.TerToPosFormatError();
            posinterface.PosInterfaceWriteInitField();
            posinterface.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("02"));
            posinterface.PosInterfaceSendMessage(posinterface.PosInterfaceTransactionCode, "02");
            System.out.printf("utility:: FORMAT ERROR \n");
            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
//                    Utility.customDialogAlertAutoClear();
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
*/


        } else {
            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private boolean checkReversalGHC() {
//        try {
//            Thread.sleep( 200 );
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if (realm == null)       // Paul_20181023
            realm = Realm.getDefaultInstance();     // Paul_20180714
        reversalHealthCare = realm.where(ReversalHealthCare.class).findFirst();
        return reversalHealthCare != null;
    }


    private void sendDataReversal(ReversalHealthCare reversalHealthCare) {
        typeClick = "GHC";      // Paul_20180712
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = reversalHealthCare.getDe2();
        if (reversalHealthCare.getType().equalsIgnoreCase("SALE")) {
            mBlockDataSend[3 - 1] = "005000";
        } else {
            mBlockDataSend[3 - 1] = "025000";
        }
        mBlockDataSend[4 - 1] = reversalHealthCare.getDe4();
        mBlockDataSend[11 - 1] = reversalHealthCare.getDe11();
        mBlockDataSend[22 - 1] = reversalHealthCare.getDe22();
        mBlockDataSend[24 - 1] = reversalHealthCare.getDe24();
        mBlockDataSend[25 - 1] = reversalHealthCare.getDe25();
        mBlockDataSend[35 - 1] = reversalHealthCare.getDe35();
        mBlockDataSend[41 - 1] = reversalHealthCare.getDe41();
        mBlockDataSend[42 - 1] = reversalHealthCare.getDe42();
        if (reversalHealthCare.getDe52() != null) {
            mBlockDataSend[52 - 1] = reversalHealthCare.getDe52();
        }
        mBlockDataSend[62 - 1] = reversalHealthCare.getDe62();
        mBlockDataSend[63 - 1] = reversalHealthCare.getDe63();
        TPDU = CardPrefix.getTPDU(this, "GHC");
        Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        dialogWaiting.show();       // Paul_20180730
        packageAndSend(TPDU, "0400", mBlockDataSend);
    }

    private boolean checkReversal(String typeClick) {
        this.typeClick = typeClick;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ReversalTemp reversalTemp = null;
        if (realm == null) {                    // Paul_20181016
            realm = Realm.getDefaultInstance();
        }
        reversalTemp = realm.where(ReversalTemp.class).findFirst();
        if (reversalTemp != null) {
//            if(dialogWaiting!=null)
//                dialogWaiting.dismiss();   //SINN 20181013  sometime call by rs232 got crash
//            dialogWaiting.show();
            if (dialogWaiting != null)      // Paul_20181015
                dialogWaiting.show();
            cardManager.setDataReversalAndSendHost(reversalTemp);
//            setTimer(60000, 1); //20180107Jeff KTB want to reversal timeout
            return false;
        } else {
            dismissDialogAll();
            return true;
        }
    }

    private void customDialogHost() {
        dialogHost = new Dialog(MenuServiceListActivity.this, R.style.ThemeWithCorners); //K.GAME 180828 change UI
        View view = dialogHost.getLayoutInflater().inflate(R.layout.dialog_custom_host, null);//K.GAME 180828 change UI
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change UI
        dialogHost.setContentView(view);//K.GAME 180828 change UI
        dialogHost.setCancelable(false);//K.GAME 180828 change UI

//        dialogHost = new Dialog(MenuServiceListActivity.this);
//        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogHost.setCancelable(false);
//        dialogHost.setContentView(R.layout.dialog_custom_host);
//        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
        ghcBtn = dialogHost.findViewById(R.id.ghcBtn);
        if ((Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("100")) ||
                (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("110"))) {
            ghcBtn.setVisibility(View.GONE);
        }

        //SINN 20181119  AXA no need select host
        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length() < 8)
            tmsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length() < 8)
            posBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length() < 8)
            epsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length() < 8)
            ghcBtn.setVisibility(View.GONE);
        //END SINN 20181119  AXA no need select host


        closeImage = dialogHost.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.dismiss();
            }
        });
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setDataTestHostPos();
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setDataTestHostEPS();
            }
        });

        tmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setDataTestHostTMS();
            }
        });
        ghcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataTestHostHealthCare();
            }
        });
    }

    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        } else if (numberPrice.equalsIgnoreCase(null)) {
            numberPrice = "";
        }
    }

    private void clickCal_trace(View v) {

        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12) //K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ปป้องกันไม่ให้เกินหลักล้าน // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921ป้องกันไม่ให้เกินหลักล้าน   // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < 12)//K.GAME 180921 ป้องกันไม่ให้เกินหลักล้าน  // Paul_20190130 x,xxx,xxx,xxx.xx
                numberPrice += "0";
        } else if (v == deleteClickFrameLayout) {
            if (!amountBox_new.getText().toString().equalsIgnoreCase("0.00")) {
                Log.d(TAG, "clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + numberPrice);
                    numberPrice = "";
                    amountBox_new.setText("0.00");
                    if (typeInterface != null)
                        amountBox_new.setText(numberPrice);
//                        amountBox_new.setText(decFormat.format(Double.valueOf(amountInterface) / 100));
//                    amountBox_new.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
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
                            amountBox_new.setText("0.00");
                            if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                                amountBox_new.setText(numberPrice);
//                                priceLabel.setText(amountInterface);
//                                amountBox_new.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
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


        if (!numberPrice.isEmpty())
            amountBox_new.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
//        amountBox_new.setText(numberPrice);
//        if (!numberPrice.equals("")) {
//            amountBox_new.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
//        } else {
//            numberPrice = "0.00";
//            amountBox_new.setText("0.00");
//        }
    }

    private void custoDialog_SelectAmount() {
        dialogSelectAmountForSale = new Dialog(this);
        dialogSelectAmountForSale.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSelectAmountForSale.setContentView(R.layout.dialog_custom_select_amount); // Paul_20190206 xml change
        dialogSelectAmountForSale.setCancelable(true);
        dialogSelectAmountForSale.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSelectAmountForSale.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        GridView gridView = (GridView) dialogSelectAmountForSale.findViewById(R.id.gridView);
        Button btnGotoMain = (Button) dialogSelectAmountForSale.findViewById(R.id.btn_gotoMain);

        btnGotoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPrice = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                amountInterface = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                Log.d("1919", "numberPrice1 = " + numberPrice);
                Log.d("1919", "amountInterface1 = " + numberPrice);

                is_dialogInsertAmountShowing = false;       // Paul_20181206 K.hong double click wrong
                ////SINN 20181013 SINN Amount cancel
                cardManager.abortPBOCProcess();     // Sinn 20181014
                dialogSelectAmountForSale.dismiss();   //// Sinn 20181022 cancel go back main menu
            }
        });

        String rateStr = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_RATE_PRICE) + ",ระบุจำนวนเงิน";
        ratePriceStr = rateStr.split(",");
        gridView.setAdapter(new gridAdapter());

        dialogSelectAmountForSale.show();
    }

    private void customDialog_InsertAmount() {//K.GAME 180914 new dialog Insert Amount for sale
        dialogInsertAmountForSale = new Dialog(this);
        dialogInsertAmountForSale.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInsertAmountForSale.setContentView(R.layout.dialog_custom_insert_amount); // Paul_20190206 xml change
        dialogInsertAmountForSale.setCancelable(true);
        dialogInsertAmountForSale.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogInsertAmountForSale.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInsertAmountForSale.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        amountBox_new = dialogInsertAmountForSale.findViewById(R.id.amountBox_new);
        final TextView inputTextLabel = dialogInsertAmountForSale.findViewById(R.id.inputTextLabel);
        Button btn_ok = dialogInsertAmountForSale.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogInsertAmountForSale.findViewById(R.id.btn_cancel);

        dialogInsertAmountForSale.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//K.GAME 180905 ปิดคีร์บอร์ด
        amountBox_new.setShowSoftInputOnFocus(false);//K.GAME 180905 ทำให้กดคีร์บอร์ดที่ Edit text ไม่ได้

        msgLabeldialogFallBack.setText("Chip Malfunction\nกรุณารูดบัตร");

        //K.GAME ปุ่มกดบน Layout
        //K.GAME 180925 back button
        dialogInsertAmountForSale.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {


                // Prevent dialog close on back press button
                numberPrice = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                amountInterface = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                Log.d("1919", "numberPrice0 = " + numberPrice);
                Log.d("1919", "amountInterface0 = " + numberPrice);
                is_dialogInsertAmountShowing = false;       // Paul_20181205 K.hong double click wrong
                dialogInsertAmountForSale.dismiss();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 180925 back button
        oneClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.zeroClickFrameLayout);
        deleteClickFrameLayout = dialogInsertAmountForSale.findViewById(R.id.deleteClickFrameLayout);
        numberLinearLayout = dialogInsertAmountForSale.findViewById(R.id.numberLinearLayout_test);


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

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_dialogInsertAmountShowing = false;       // Paul_20181211 K.hong double click wrong
                Log.d("1919_amountBox_new", amountBox_new.getText().toString());
                if (amountBox_new.getText().toString().trim().isEmpty()) {
                    Utility.customDialogAlert(MenuServiceListActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else if (amountBox_new.getText().toString().equals(": 0.00")) {
                    Utility.customDialogAlert(MenuServiceListActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else if (amountBox_new.getText().toString().equals(" 0.00")) {
                    Utility.customDialogAlert(MenuServiceListActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else if (amountBox_new.getText().toString().equals("0.00")) {
                    Utility.customDialogAlert(MenuServiceListActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else if (Double.parseDouble(amountBox_new.getText().toString().replace(",", "")) > Double.parseDouble(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MAX_CONTACTLESS_ID))) {
                    Utility.customDialogAlert(MenuServiceListActivity.this, "จำนวนเงินไม่ถูกต้อง", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    // Paul_20190130 x,xxx,xxx,xxx.xx
                    System.out.printf("utility:: Double.valueOf(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MAX_AMT) = %s\n", Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MAX_AMT));
                    System.out.printf("utility:: amountBox_new.getText().toString() = %s\n", amountBox_new.getText().toString().replaceAll(",", ""));
                    if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MAX_AMT).equals("0") && (Double.valueOf(amountBox_new.getText().toString().replaceAll(",", "")) > Double.valueOf(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MAX_AMT)))) {
                        Utility.customDialogAlert(MenuServiceListActivity.this, "จำนวนเงินเกินกำหนด", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    } else {

//                        Log.d(TAG, "setImportAmountALL: " + decimalFormat.format(amountAll));

                        is_dialogInsertAmountShowing = false;       // Paul_20181205 K.hong double click wrong
//                amountBox_new.setText(decFormat.format(Double.valueOf(numberPrice) / 100));
                        dialogInsertAmountForSale.dismiss();//K.GAME 180925 เมื่อกด ตกลง ให้ปิด เพื่อป้องกันการย้อนกลับมากด จะ Error + ค้าง
                        //K.GAME ย้ายเข้ามาวางไว้ด้านใน
                        amountInterface = amountBox_new.getText().toString();//K.GAME 180914 set amount dialog // แก้บัค ตัวเลขไม่มีทศนิยม
                        //END K.GAME ย้ายเข้ามาวางไว้ด้านใน
                        Log.d(TAG, "Start :startInsertCard()");
                        cardManager.setAmountforContactless(amountInterface.replace(",", ""));


                        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_CONTACTLESS_ID).equalsIgnoreCase("1")) {
                            Double __amount = Double.parseDouble(amountInterface.replace(",", ""));
                            Double __max_ctl = Double.parseDouble(Preference.getInstance(context).getValueString(Preference.KEY_MAX_CONTACTLESS_ID));

                            if (__amount <= __max_ctl) {
                                lineartranstype1.setVisibility(View.GONE);
                                lineartranstype2.setVisibility(View.VISIBLE);
                            } else {
                                lineartranstype1.setVisibility(View.VISIBLE);
                                lineartranstype2.setVisibility(View.GONE);
                            }
                        }

                        startInsertCard();//K.GAME 180914 ย้ายจากด้านนอกเข้ามาใส่
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPrice = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                amountInterface = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                Log.d("1919", "numberPrice1 = " + numberPrice);
                Log.d("1919", "amountInterface1 = " + numberPrice);

                is_dialogInsertAmountShowing = false;       // Paul_20181206 K.hong double click wrong
                ////SINN 20181013 SINN Amount cancel
                cardManager.abortPBOCProcess();     // Sinn 20181014
                amountBox_new.setText(numberPrice);
                dialogInsertAmountForSale.dismiss();   //// Sinn 20181022 cancel go back main menu
//                //SINN 20180924 Cancel NORMAL SALE
//                cardManager.stopTransaction();
//                if (posinterface.PosInterfaceExistFlg == 1) {    // Paul_20180730
//                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "Waiting POSLINK ACK");
//                    TellToPosNoMatching("  ");
//                    posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                        @Override
//                        public void success() {
//                            posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
//                            Utility.customDialogAlertAutoClear();
//                            System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
//                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                            overridePendingTransition(0, 0);
//                        }
//                    });
//                }
                ////SINN 20181013 SINN Amount cancel

            }
        });
        dialogInsertAmountForSale.show();

//        custoDialog_SelectAmount();
//        if (!dialogInsertAmountForSale.isShowing()) {
//            numberPrice = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
//            amountInterface = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
//        }
    }//K.GAME 180905 new dialog

    private void customContactless() {
        dialogContactless = new Dialog(this);
        dialogContactless.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogContactless.setCancelable(false);
        dialogContactless.setContentView(R.layout.dialog_custom_contactless_card);//K.GAME 180914 change XML
        dialogContactless.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogContactless.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView textViewAmount = (TextView) dialogContactless.findViewById(R.id.textViewAmount);
        textViewAmount.setText(amountInterface);

        ImageView img_qr = (ImageView) dialogContactless.findViewById(R.id.img_qr);
        img_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myCardReaderHelper.getInstance().stopPolling();
                //ref1 ส่งรหัสพนง ไปน่ะ ส่วน ref 2 ไม่ require และ ref3 เป็น tid+date+time
                ref1 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_BUS_C_STAFF_ID);
                ref2 = "";

                dateTime = new Date();
                uniqueData = makeUniqueData(dateTime);

                ref3 = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID) + uniqueData;

//                DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

                Intent intent = new Intent(MenuServiceListActivity.this, GenerateQrActivity.class);
                intent.putExtra(KEY_TYPE_INTERFACE, "InterfaceTollway");
                intent.putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                intent.putExtra(KEY_INTERFACE_REF1, ref1);
                intent.putExtra(KEY_INTERFACE_REF2, ref2);
                intent.putExtra(KEY_INTERFACE_REF3, ref3);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        Button btn_close = (Button) dialogContactless.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPrice = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                amountInterface = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                Log.d("1919", "numberPrice1 = " + numberPrice);
                Log.d("1919", "amountInterface1 = " + numberPrice);

                is_dialogInsertAmountShowing = false;       // Paul_20181206 K.hong double click wrong
                cardManager.abortPBOCProcess();     // Sinn 20181014
//                dialogContactless.dismiss();   //// Sinn 20181022 cancel go back main menu
                myCardReaderHelper.getInstance().stopPolling();

                dialogInsertCard.dismiss();
                dialogContactless.dismiss();

                isDialogShowInsertCardShowing = false;
                dialogContactless = null;

//                timerInsertCard.purge();
//                timerInsertCard.cancel();
                isShowInsertCard = false;
                cardManager.stopTransaction();
//                timerInsertCard = null;
            }
        });

        if (!dialogContactless.isShowing()) {

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            dialogContactless.show();
            /*if(searchCardThread == null) {
                searchCardThread = new SearchCardThread(false, false, true);
            }
            searchCardThread.start();*/
            isDialogShowInsertCardShowing = true;
//                }
//            });

            if (isShowInsertCard) {
//                timerInsertCard.cancel();
//                timerInsertCard.purge();
//                timerInsertCard = null;
//
//                timerInsertCard = new Timer();

                isShowInsertCard = false;
            } else {
                isShowInsertCard = true;
            }

//            if (timerInsertCard!=null){
//                timerInsertCard.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //this will run on UI thread so you can update views here
//                                if (isDialogShowInsertCardShowing) {
            Rf_Handler.sendEmptyMessageDelayed(0, 2000);
//                                }
//                            }
//                        });
//                    }
//                }, 60000, 60000);
//            }


        }

    }

    private void customDialog() {
        dialogInsertCard = new Dialog(this);
        dialogInsertCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInsertCard.setCancelable(false);
//        dialogInsertCard.setContentView(R.layout.dialog_custom_sale);//K.GAME 180914 comment change XML
        dialogInsertCard.setContentView(R.layout.dialog_custom_sale_insert_card);//K.GAME 180914 change XML
        dialogInsertCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInsertCard.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        tv_confirm_amount = dialogInsertCard.findViewById(R.id.tv_confirm_amount);

        Button closeImage = dialogInsertCard.findViewById(R.id.btn_close);
        lineartranstype1 = dialogInsertCard.findViewById(R.id.lineartranstype1);
        lineartranstype2 = dialogInsertCard.findViewById(R.id.lineartranstype2);

        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_CONTACTLESS_ID).equalsIgnoreCase("0")) {
            lineartranstype1.setVisibility(View.VISIBLE);
            lineartranstype2.setVisibility(View.GONE);
        } else {
            lineartranstype1.setVisibility(View.GONE);
            lineartranstype2.setVisibility(View.VISIBLE);
        }

        dialogInsertCard.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                numberPrice = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                amountInterface = "0.00";//K.GAME 180918 แก้บัค ตัวเลขค้าง
                Log.d("1919", "numberPrice2 = " + numberPrice);
                Log.d("1919", "amountInterface2 = " + numberPrice);
                dialogInsertCard.dismiss();
                dialogContactless.dismiss();
                myCardReaderHelper.getInstance().stopPolling();
                cardManager.stopTransaction();
                dialogContactless = null;
                isDialogShowInsertCardShowing = false;
                if (timer != null) {
                    timer.cancel();
                }
                if (posinterface.PosInterfaceExistFlg == 1) {
//                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "ยกเลิกการทำรายการ");

                    errormsgLabel.setText("ยกเลิกการทำรายการ");
                    dialogAlert.show();

                    TellToPosNoMatching("  ");
                    posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                            Utility.customDialogAlertAutoClear();
                            System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                cardManager.stopTransaction();  ////SINN ??
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 180925 back button
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardManager.stopTransaction();
                dialogInsertCard.dismiss();
                dialogContactless.dismiss();
                myCardReaderHelper.getInstance().stopPolling();
                cardManager.stopTransaction();

                isDialogShowInsertCardShowing = false;
                dialogContactless = null;

                numberPrice = "0.00";
                amountInterface = "0.00";
                Log.d("1919", "numberPrice3 = " + numberPrice);
                Log.d("1919", "amountInterface3 = " + numberPrice);
                if (timer != null) {
                    timer.cancel();
                }

                if (posinterface.PosInterfaceExistFlg == 1) {
//                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "ยกเลิกการทำรายการ");  ////SINN 20181013  change wording for cancel transactions

                    errormsgLabel.setText("ยกเลิกการทำรายการ");
                    dialogAlert.show();

                    TellToPosNoMatching("  ");
                    posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                            Utility.customDialogAlertAutoClear();
                            System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                    Utility.customDialogAlertAutoClear();
                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setCancelable(false);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        /////////////////////////K.GAME 180830 Change Dialog Waiting
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        msgLabel = dialogWaiting.findViewById(R.id.msgLabel);
        animationDrawable.start();

        msgLabel.setText("กำลังทำรายการ");
        Utility.animation_Waiting_new(dialogWaiting);//K.GAME 180830 Change Dialog Waiting
        /////////////////////////END 180830 K.GAME Change Dialog Waiting
    }

    private void customDialogServiceCode() {
        dialogServiceCode = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180924
        View view = dialogServiceCode.getLayoutInflater().inflate(R.layout.dialog_custom_service_code_new, null);//K.GAME 180821 //K.GAMe 180924 change XML
        dialogServiceCode.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogServiceCode.setContentView(view);//K.GAME 180924
        dialogServiceCode.setCancelable(false);//K.GAME 180924

//        dialogServiceCode = new Dialog(this);
//        dialogServiceCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogServiceCode.setCancelable(false);
//        dialogServiceCode.setContentView(R.layout.dialog_custom_service_code);//K.GAMe 180924 change XML
//        dialogServiceCode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogServiceCode.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button closeImage = dialogServiceCode.findViewById(R.id.closeImage);//K.GAME 180924 New UI ImageView > Button
        ImageView skewerImage = dialogServiceCode.findViewById(R.id.skewerImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) skewerImage.getBackground();
        animationDrawable.start();
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogServiceCode.dismiss();
                cardManager.stopTransaction();
//                if (localtypeInterface != null) {
                if (posinterface.PosInterfaceExistFlg == 1) {        // Paul_20180730
//                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "Waiting POSLINK ACK");
//                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "ยกเลิกการทำรายการ");  ////SINN 20181013  change wording for cancel transactions

                    errormsgLabel.setText("ยกเลิกการทำรายการ");
                    dialogAlert.show();

                    TellToPosNoMatching("  ");
                    posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                            Utility.customDialogAlertAutoClear();
                            System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
            }
        });
        cardManager.startTransaction(CardManager.SALE, amountInterface);
//        setTimer(15000, 1);
//        setTimer(60000, 1);     // Paul_20181019
        dialogServiceCode.show();
    }

    private void clickCal(View v) {
//        String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD);
//SINN 20181021 PIN any digits
        String keyPin = "11111111";
        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
                numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            if (numberPrice.length() < keyPin.length()) //K.GAME 181017 Hard code ต้องไปกำหนดขนาด ตาม json
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

    private void customDialogPin_new() { //K.GAME 181017
        dialogcustomDialogPin_new = new Dialog(this);
        dialogcustomDialogPin_new.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogcustomDialogPin_new.setContentView(R.layout.dialog_custom_pin_settingforuser);
        dialogcustomDialogPin_new.setCancelable(false);
        dialogcustomDialogPin_new.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogcustomDialogPin_new.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogcustomDialogPin_new.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        pinBox_new = dialogcustomDialogPin_new.findViewById(R.id.pinBox);
        final TextView inputTextLabel = dialogcustomDialogPin_new.findViewById(R.id.inputTextLabel);
        dialogcustomDialogPin_new.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//K.GAME 180905 ปิดคีร์บอร์ด
        pinBox_new.setShowSoftInputOnFocus(false);//K.GAME 180905 ทำให้กดคีร์บอร์ดที่ Edit text ไม่ได้
        final ImageView img_pin01 = dialogcustomDialogPin_new.findViewById(R.id.img_pin01);
        final ImageView img_pin02 = dialogcustomDialogPin_new.findViewById(R.id.img_pin02);
        final ImageView img_pin03 = dialogcustomDialogPin_new.findViewById(R.id.img_pin03);
        final ImageView img_pin04 = dialogcustomDialogPin_new.findViewById(R.id.img_pin04);
        final ImageView img_pin05 = dialogcustomDialogPin_new.findViewById(R.id.img_pin05);
        final ImageView img_pin06 = dialogcustomDialogPin_new.findViewById(R.id.img_pin06);

        final ImageView img_pin8_01 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_01);
        final ImageView img_pin8_02 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_02);
        final ImageView img_pin8_03 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_03);
        final ImageView img_pin8_04 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_04);
        final ImageView img_pin8_05 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_05);
        final ImageView img_pin8_06 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_06);
        final ImageView img_pin8_07 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_07);
        final ImageView img_pin8_08 = dialogcustomDialogPin_new.findViewById(R.id.img_pin8_08);

        LinearLayout linear_pin6 = dialogcustomDialogPin_new.findViewById(R.id.linear_pin6);
        LinearLayout linear_pin8 = dialogcustomDialogPin_new.findViewById(R.id.linear_pin8);
        LinearLayout linear_6 = dialogcustomDialogPin_new.findViewById(R.id.linear_6);
        LinearLayout linear_8 = dialogcustomDialogPin_new.findViewById(R.id.linear_8);

        EditText pinBox = dialogcustomDialogPin_new.findViewById(R.id.pinBox);

        String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD);

        ////SINN 20181021 PIN any digits
//        String keyPin = "11111111";
        Log.d(TAG, "customDialogPin_new 1919_keyPin = " + keyPin);
        if (keyPin.length() == 6) {
            linear_pin6.setVisibility(View.VISIBLE);
            linear_6.setVisibility(View.VISIBLE);
            linear_pin8.setVisibility(View.GONE);
            linear_8.setVisibility(View.GONE);
        } else if (keyPin.length() == 8) {
            linear_pin6.setVisibility(View.GONE);
            linear_6.setVisibility(View.GONE);
            linear_pin8.setVisibility(View.VISIBLE);
            linear_8.setVisibility(View.VISIBLE);
        } else {
////SINN 20181021 PIN any digits
            linear_pin6.setVisibility(View.GONE);
            linear_6.setVisibility(View.GONE);
            linear_pin8.setVisibility(View.VISIBLE);
            linear_8.setVisibility(View.VISIBLE);
////END SINN 20181021 PIN any digits


//            Utility.customDialogAlert(MenuServiceListActivity.this, "กำหนด PIN ได้แค่ 6 และ 8หลัก", new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                    dialogcustomDialogPin_new.dismiss();
//                }
//            });
        }

        pinBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter( // กำหนด length ตาม Json
//K.GAME 180925 get length จากใน json เพื่อเอามากำหนดขนาดช่อง password  //SINN 20181021 PIN any digits
                Integer.valueOf(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD).length())
//                Integer.valueOf("11111111")

        )});


        Button cancelBtn = dialogcustomDialogPin_new.findViewById(R.id.cancelBtn);
        Button okBtn = dialogcustomDialogPin_new.findViewById(R.id.okBtn);

        //K.GAME 181016 back button
        dialogcustomDialogPin_new.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                numberPrice = "";//K.GAME 181017 แก้บัค
                dialogcustomDialogPin_new.dismiss();
                isSettingForUser = false;
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 181016 back button


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuServiceListActivity.this, "PIN ไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//เปิดเพื่อดูRrcyclerView
            }
        });
//K.GAME ปุ่มกดบน Layout
        oneClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.zeroClickFrameLayout);
        deleteClickFrameLayout = dialogcustomDialogPin_new.findViewById(R.id.deleteClickFrameLayout);
        numberLinearLayout = dialogcustomDialogPin_new.findViewById(R.id.numberLinearLayout_test);

        // Paul_20181120 Start..  Some time gabage number.  I don't understand. I think don't need
//        if (typeInterface != null) {
//
//        } else {
//            View view = null;
//            for (int i = 0; i < numberLinearLayout.getChildCount(); i++) {
//                view = numberLinearLayout.getChildAt(i);
//                view.setEnabled(true);
//
//            }
//            //  clickCal(view);
//        }
        // Paul_20181120 End..   I don't understand. I think don't need


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
//        pinBox_new.getText().clear();       // Paul_20181106 garbage clear
        System.out.printf("utility:: %s customDialogPin_new 0001 \n", TAG);
        pinBox_new.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.printf("utility:: %s customDialogPin_new 0002 s.length() = %s \n", TAG, s.length());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.printf("utility:: %s customDialogPin_new 0003 s.length() = %d \n", TAG, s.length());
//                String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD);
                //SINN 20181021 PIN any digits
//                String keyPin ="11111111";
                String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD);
                Log.d(TAG, "onTextChanged 1919_keyPin = " + keyPin);
                if (keyPin.length() == 6) {
                    if (s.length() == 0) {
                        img_pin01.setVisibility(View.INVISIBLE);
                        img_pin02.setVisibility(View.INVISIBLE);
                        img_pin03.setVisibility(View.INVISIBLE);
                        img_pin04.setVisibility(View.INVISIBLE);
                        img_pin05.setVisibility(View.INVISIBLE);
                        img_pin06.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 1) {
                        img_pin01.setVisibility(View.VISIBLE);
                        img_pin02.setVisibility(View.INVISIBLE);
                        img_pin03.setVisibility(View.INVISIBLE);
                        img_pin04.setVisibility(View.INVISIBLE);
                        img_pin05.setVisibility(View.INVISIBLE);
                        img_pin06.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 2) {
                        img_pin01.setVisibility(View.VISIBLE);
                        img_pin02.setVisibility(View.VISIBLE);
                        img_pin03.setVisibility(View.INVISIBLE);
                        img_pin04.setVisibility(View.INVISIBLE);
                        img_pin05.setVisibility(View.INVISIBLE);
                        img_pin06.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 3) {
                        img_pin01.setVisibility(View.VISIBLE);
                        img_pin02.setVisibility(View.VISIBLE);
                        img_pin03.setVisibility(View.VISIBLE);
                        img_pin04.setVisibility(View.INVISIBLE);
                        img_pin05.setVisibility(View.INVISIBLE);
                        img_pin06.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 4) {
                        img_pin01.setVisibility(View.VISIBLE);
                        img_pin02.setVisibility(View.VISIBLE);
                        img_pin03.setVisibility(View.VISIBLE);
                        img_pin04.setVisibility(View.VISIBLE);
                        img_pin05.setVisibility(View.INVISIBLE);
                        img_pin06.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 5) {
                        img_pin01.setVisibility(View.VISIBLE);
                        img_pin02.setVisibility(View.VISIBLE);
                        img_pin03.setVisibility(View.VISIBLE);
                        img_pin04.setVisibility(View.VISIBLE);
                        img_pin05.setVisibility(View.VISIBLE);
                        img_pin06.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 6) {
                        img_pin01.setVisibility(View.VISIBLE);
                        img_pin02.setVisibility(View.VISIBLE);
                        img_pin03.setVisibility(View.VISIBLE);
                        img_pin04.setVisibility(View.VISIBLE);
                        img_pin05.setVisibility(View.VISIBLE);
                        img_pin06.setVisibility(View.VISIBLE);
                    }
                    if (s.length() == 6) {
                        inputTextLabel.setVisibility(View.INVISIBLE);
//                    String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD);
//                    Log.d(TAG,"1919_keyPin = "+keyPin);
                        if (s.toString().equalsIgnoreCase(keyPin)) {
                            //K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
                            isSettingForUser = false;
                            System.out.printf("utility:: %s Befor SettingForUserActivity Call 00001 \n", TAG);
                            Intent intent3 = new Intent(MenuServiceListActivity.this, SettingForUserActivity.class);
                            startActivity(intent3);
                            overridePendingTransition(0, 0);
                            dialogcustomDialogPin_new.dismiss();
                            //END K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
                            numberPrice = "";//K.GAME 180905 แก้บัค ตัวเลขโผล่มา Trace
                        } else {
                            inputTextLabel.setVisibility(View.VISIBLE);
//                            inputTextLabel.setText("PIN ไม่ถูกต้อง");
                            inputTextLabel.setText("รหัสผ่านไม่ถูกต้อง");  //20181106 SINN change wording for wrong PIN.
                        }
                    } else {
                        inputTextLabel.setVisibility(View.VISIBLE);
                        inputTextLabel.setText("ระบุรหัสผ่านและกดตกลง");
                    }
                } else if (keyPin.length() == 8) {

                    if (s.length() == 0) {
                        img_pin8_01.setVisibility(View.INVISIBLE);
                        img_pin8_02.setVisibility(View.INVISIBLE);
                        img_pin8_03.setVisibility(View.INVISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 1) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.INVISIBLE);
                        img_pin8_03.setVisibility(View.INVISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 2) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.INVISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 3) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 4) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 5) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 6) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.VISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 7) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.VISIBLE);
                        img_pin8_07.setVisibility(View.VISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 8) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.VISIBLE);
                        img_pin8_07.setVisibility(View.VISIBLE);
                        img_pin8_08.setVisibility(View.VISIBLE);
                    }
                    if (s.length() == 8) {
                        inputTextLabel.setVisibility(View.INVISIBLE);
//                    String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD);
//                    Log.d(TAG,"1919_keyPin = "+keyPin);
                        if (s.toString().equalsIgnoreCase(keyPin)) {
                            //K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
                            isSettingForUser = false;
                            System.out.printf("utility:: %s Befor SettingForUserActivity Call 00002 \n", TAG);
                            Intent intent3 = new Intent(MenuServiceListActivity.this, SettingForUserActivity.class);
                            startActivity(intent3);
                            overridePendingTransition(0, 0);
                            dialogcustomDialogPin_new.dismiss();
                            //END K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
                            numberPrice = "";//K.GAME 180905 แก้บัค ตัวเลขโผล่มา Trace
                        } else {
                            inputTextLabel.setVisibility(View.VISIBLE);
//                            inputTextLabel.setText("PIN ไม่ถูกต้อง");
                            inputTextLabel.setText("รหัสผ่านไม่ถูกต้อง");  //20181106 SINN change wording for wrong PIN.
                        }
                    } else {
                        inputTextLabel.setVisibility(View.VISIBLE);
                        inputTextLabel.setText("กรอกรหัสผ่านและกดตกลง");
                    }
                } else {

                    if (s.length() == 0) {
                        img_pin8_01.setVisibility(View.INVISIBLE);
                        img_pin8_02.setVisibility(View.INVISIBLE);
                        img_pin8_03.setVisibility(View.INVISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 1) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.INVISIBLE);
                        img_pin8_03.setVisibility(View.INVISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 2) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.INVISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 3) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.INVISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 4) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.INVISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 5) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.INVISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 6) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.VISIBLE);
                        img_pin8_07.setVisibility(View.INVISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 7) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.VISIBLE);
                        img_pin8_07.setVisibility(View.VISIBLE);
                        img_pin8_08.setVisibility(View.INVISIBLE);
                    }
                    if (s.length() == 8) {
                        img_pin8_01.setVisibility(View.VISIBLE);
                        img_pin8_02.setVisibility(View.VISIBLE);
                        img_pin8_03.setVisibility(View.VISIBLE);
                        img_pin8_04.setVisibility(View.VISIBLE);
                        img_pin8_05.setVisibility(View.VISIBLE);
                        img_pin8_06.setVisibility(View.VISIBLE);
                        img_pin8_07.setVisibility(View.VISIBLE);
                        img_pin8_08.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                System.out.printf("utility:: %s customDialogPin_new 0004 \n",TAG);
//
//                //SINN 20181021 PIN any digits
//                String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD);
////                inputTextLabel.setVisibility(View.INVISIBLE);     // Paul_20181120 remark
//                if (s.toString().equalsIgnoreCase(keyPin)) {
//                    //K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
//                    System.out.printf("utility:: %s Befor SettingForUserActivity Call 00003 \n",TAG);
//                    Intent intent3 = new Intent(MenuServiceListActivity.this, SettingForUserActivity.class);
//                    startActivity(intent3);
//                    overridePendingTransition(0, 0);
//                    dialogcustomDialogPin_new.dismiss();
//                    //END K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
//                    numberPrice = "";//K.GAME 180905 แก้บัค ตัวเลขโผล่มา Trace
//                }
                //SINN 20181021 PIN any digits
            }
        });

        if (typeInterface != null) {  ////SINN 20181014 void rs233 bypass enter pin
            dialogWaiting.show();
        } else {
            dialogcustomDialogPin_new.show();
        }

    }//K.GAME 180905 new dialog

    private void customDialogPassword_settingUser() {
        dialogPassword_settingUser = new Dialog(MenuServiceListActivity.this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogPassword_settingUser.getLayoutInflater().inflate(R.layout.dialog_custom_input_password, null);//K.GAME 180828 change dialog UI
        dialogPassword_settingUser.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogPassword_settingUser.setContentView(view);//K.GAME 180828 change dialog UI
        dialogPassword_settingUser.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogPassword = new Dialog(MenuServiceListActivity.this);
//        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogPassword.setContentView(R.layout.dialog_custom_input_password);
//        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialogPassword.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passwordBox = dialogPassword_settingUser.findViewById(R.id.passwordBox);
        okBtn = dialogPassword_settingUser.findViewById(R.id.okBtn);
        cancelBtn = dialogPassword_settingUser.findViewById(R.id.cancelBtn);

//        passwordBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        passwordBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(

                Integer.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD).toString().length()) //K.GAME 180925 get length จากใน json เพื่อเอามากำหนดขนาดช่อง password

        )});

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1919password", Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD).toString());
                if (passwordBox.getText().toString().isEmpty()) {
                    passwordBox.setError("กรุณาใส่รหัสผ่าน");
                } else {
                    if (isSettingForUser == true)
                        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) { //K.GAME 180925 new Password for SetiingForUser
                            isSettingForUser = false;
                            System.out.printf("utility:: %s Befor SettingForUserActivity Call 00004 \n", TAG);
                            Intent intent3 = new Intent(MenuServiceListActivity.this, SettingForUserActivity.class);
                            startActivity(intent3);
                            overridePendingTransition(0, 0);
                            dialogPassword_settingUser.dismiss();
                        } else {
                            passwordBox.setError("รหัสผิดพลาด");
                        }
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPassword_settingUser.dismiss();
            }
        });

    }

    private void customDialogPassword_input4() {
        dialogPassword = new Dialog(MenuServiceListActivity.this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogPassword.getLayoutInflater().inflate(R.layout.dialog_custom_input_password, null);//K.GAME 180828 change dialog UI
        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogPassword.setContentView(view);//K.GAME 180828 change dialog UI
        dialogPassword.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogPassword = new Dialog(MenuServiceListActivity.this);
//        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogPassword.setContentView(R.layout.dialog_custom_input_password);
//        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialogPassword.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passwordBox = dialogPassword.findViewById(R.id.passwordBox);
        okBtn = dialogPassword.findViewById(R.id.okBtn);
        cancelBtn = dialogPassword.findViewById(R.id.cancelBtn);

        passwordBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});//K.GAME 180925
//        passwordBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Preference.getInstance(this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD).length())});


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordBox.getText().toString().isEmpty()) {
                    passwordBox.setError("กรุณาใส่รหัสผ่าน");
                } else {
                    if (!isOffline) {
                        System.out.printf("utility:: Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD) = %s \n", Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD));
                        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
                            Intent intent = new Intent(MenuServiceListActivity.this, SettingActivity.class);
                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_ADMIN_PASSWORD);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PIN).equalsIgnoreCase(passwordBox.getText().toString())) {
                            Intent intent = new Intent(MenuServiceListActivity.this, SettingActivity.class);
                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_NORMAL_PASSWORD);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else {
                            passwordBox.setError("รหัสผิดพลาด");
                        }
                    } else {
                        System.out.printf("utility:: Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD) = %s \n", Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD));
                        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_OFFLINE_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
// Paul_20180713
                            Intent intent = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                            intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else {
                            passwordBox.setError("รหัสผิดพลาด");

                        }
                    }
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPassword.dismiss();
            }
        });

        dialogPassword.show();
    }


    private void customDialogPassword() {
        dialogPassword = new Dialog(MenuServiceListActivity.this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogPassword.getLayoutInflater().inflate(R.layout.dialog_custom_input_password, null);//K.GAME 180828 change dialog UI
        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogPassword.setContentView(view);//K.GAME 180828 change dialog UI
        dialogPassword.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogPassword = new Dialog(MenuServiceListActivity.this);
//        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogPassword.setContentView(R.layout.dialog_custom_input_password);
//        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialogPassword.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passwordBox = dialogPassword.findViewById(R.id.passwordBox);
        okBtn = dialogPassword.findViewById(R.id.okBtn);
        cancelBtn = dialogPassword.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordBox.getText().toString().isEmpty()) {
                    passwordBox.setError("กรุณาใส่รหัสผ่าน");
                } else {
                    if (!isOffline) {
                        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
                            Intent intent = new Intent(MenuServiceListActivity.this, SettingActivity.class);
                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_ADMIN_PASSWORD);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PIN).equalsIgnoreCase(passwordBox.getText().toString())) {
                            Intent intent = new Intent(MenuServiceListActivity.this, SettingActivity.class);
                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_NORMAL_PASSWORD);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else {
                            passwordBox.setError("รหัสผิดพลาด");
                        }
                    } else {
                        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_OFFLINE_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
// Paul_20180713
                            Intent intent = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                            intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else {
                            passwordBox.setError("รหัสผิดพลาด");
                        }
                    }
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPassword.dismiss();
            }
        });

        dialogPassword.show();
    }

    private void setTimer(final long time, final int typeTimer) { // typeTime 1 = Insert Card 2 = Waiting process

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d(TAG, "onTick: " + millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        if (typeTimer == 1) {
//                            dismissDialogAll();
                            cardManager.stopTransaction();
                            //                 if(dialogInsertCard != null && dialogInsertCard.isShowing()) {    // Paul_20181019 Chip Malfunction time out
                            System.out.printf("utility:: Insert Card Timeout \n");
                            if (posinterface.PosInterfaceExistFlg == 1) {
//                                Utility.customDialogAlertAuto(MenuServiceListActivity.this, "ไม่สามารถเชื่อมต่อได้");

                                errormsgLabel.setText("ไม่สามารถเชื่อมต่อได้");
                                dialogAlert.show();

                                TellToPosNoMatching("EN");
                                posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        Utility.customDialogAlertAutoClear();
                                        posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                        dialogInsertCard.dismiss();
                                        dialogContactless.dismiss();
                                        myCardReaderHelper.getInstance().stopPolling();
                                        cardManager.stopTransaction();
                                        dialogInsertCard.cancel();
                                        isDialogShowInsertCardShowing = false;
                                        dialogContactless = null;

                                        Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                                dialogInsertCard.dismiss();
                                dialogContactless.dismiss();
                                myCardReaderHelper.getInstance().stopPolling();
                                cardManager.stopTransaction();
                                dialogInsertCard.cancel();
                                isDialogShowInsertCardShowing = false;
                                dialogContactless = null;

                                Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                            //                 }
                        } else if (typeTimer == 2) {
                            dismissDialogAll();
                            cardManager.stopTransaction();
                            Intent intent = new Intent(MenuServiceListActivity.this, CalculatePriceActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KEY_CARD, cardNo);
                            bundle.putString(KEY_TYPE_CARD, IC_CARD);
                            Log.d(TAG, "amountInterface :" + amountInterface);
                            bundle.putString(KEY_INTERFACE_AMOUNT, amountInterface);
                            bundle.putString(KEY_INTERFACE_CARDHOLDER, cardhlder);//K.GAME 180916
                            bundle.putString(KEY_TYPE_INTERFACE, localtypeInterface);  ////SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        } else {
                            dismissDialogAll();
                            cardManager.stopTransaction();
                            Intent intent = new Intent(MenuServiceListActivity.this, CalculatePriceActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KEY_CARD, cardNo);
                            bundle.putString(KEY_TYPE_CARD, MSG_CARD);
                            Log.d(TAG, "amountInterface :" + amountInterface);
                            bundle.putString(KEY_INTERFACE_AMOUNT, amountInterface);
                            bundle.putString(KEY_INTERFACE_CARDHOLDER, cardhlder);//K.GAME 180916
                            bundle.putString(KEY_TYPE_INTERFACE, localtypeInterface);  ////SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    }
                };
                timer.start();
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
                cardManager.selectMultiApp(1);

            }
        });
        btnApp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                cardManager.selectMultiApp(2);
            }
        });
        btnApp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                cardManager.selectMultiApp(3);
            }
        });
        btnApp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                cardManager.selectMultiApp(4);
            }
        });
        btnApp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelect.dismiss();
                dialogWaiting.show();
                cardManager.selectMultiApp(5);
            }
        });
    }

    private void customDialogFallBack() {
        dialogFallBack = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180924
        View view = dialogFallBack.getLayoutInflater().inflate(R.layout.dialog_custom_mag_new, null);//K.GAME 180924 change XML
        dialogFallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180924
        dialogFallBack.setContentView(view);//K.GAME 180924
        dialogFallBack.setCancelable(false);//K.GAME 180924


//        dialogFallBack = new Dialog(this);
//        dialogFallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogFallBack.setCancelable(false);
//        dialogFallBack.setContentView(R.layout.dialog_custom_mag);
//        dialogFallBack.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogFallBack.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        TextView msgLabel = dialogFallBack.findViewById(R.id.msgLabel);    //// Sinn 20181022 fallback TMS

        msgLabeldialogFallBack = dialogFallBack.findViewById(R.id.msgLabel);   // Sinn 20181022 fallback TMS

        Button closeImage = dialogFallBack.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            private int cancelFlag = 0;

            @Override
            public void onClick(View v) {
                numFallBack = 0;
                dialogFallBack.dismiss();
                if (timer != null) {
                    timer.cancel();
                }
                cardManager.stopTransaction();
// Paul_20181017 TMS Card Full Back Error
                if (posinterface.PosInterfaceExistFlg == 1) {
//                        TellToPosError( "ND" );
                    if (cancelFlag == 0) {
                        cancelFlag = 1;
//                        Utility.customDialogAlertAuto(MenuServiceListActivity.this, "Cancel");   // Paul_20181017 Can

                        errormsgLabel.setText("Cancel");
                        dialogAlert.show();

                        TellToPosNoMatching("ND");
                        posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                if (dialogFallBack != null && dialogFallBack.isShowing())
                                    dialogFallBack.dismiss();
                                if (dialogWaiting != null && dialogWaiting.isShowing())
                                    dialogWaiting.dismiss();
                                if (dialogFallBackCheck != null && dialogFallBackCheck.isShowing())
                                    dialogFallBackCheck.dismiss();
                                posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                dialogInsertCard.cancel();
                                Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                } else {
                    // Paul_20181024
                    dialogInsertCard.cancel();
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
    }
/*
    private void customDialogCheckFallBack() {
        dialogFallBackCheck = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogFallBackCheck.getLayoutInflater().inflate(R.layout.dialog_custom_alert_fall_back, null);//K.GAME 180821
        dialogFallBackCheck.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogFallBackCheck.setContentView(view);//K.GAME 180821
        dialogFallBackCheck.setCancelable(false);//K.GAME 180821


//        dialogFallBackCheck = new Dialog(this);
//        dialogFallBackCheck.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogFallBackCheck.setCancelable(false);
//        dialogFallBackCheck.setContentView(R.layout.dialog_custom_alert_fall_back);
//        dialogFallBackCheck.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogFallBackCheck.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        msgFallBackLabel = dialogFallBackCheck.findViewById(R.id.msgLabel);
        closeFallBackImage = dialogFallBackCheck.findViewById(R.id.closeImage);
        okBtn = dialogFallBackCheck.findViewById(R.id.okBtn);
        closeFallBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFallBack.dismiss();
//                if (timer != null) {
//                    timer.cancel();
//                }
//                cardManager.stopTransaction();
                numFallBack = 0;
                dialogFallBack.dismiss();
                if (dialogWaiting != null)
                    dialogWaiting.dismiss();
                if (timer != null)
                    timer.cancel();
                if (dialogInsertCard != null) {
                    //dialogInsertCard.cancel();

                    //--------------------------------------------------------------------------
                    if (posinterface.PosInterfaceExistFlg == 1) {
//                        TellToPosError( "ND" );
                        TellToPosNoMatching("ND");
                        posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                dialogInsertCard.cancel();
                                Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                        dialogInsertCard.cancel();
                    //---------------------------------------------------------------------------------------
                }
                dialogFallBackCheck.dismiss();
                cardManager.stopTransaction();

            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialogAll();
                if (numFallBack > 1) {
                    Log.d(TAG, "onClick IF : " + numFallBack);
                    dialogFallBackCheck.dismiss();
                    dialogFallBack.show();
                    cardmanager.startTransaction(CardManager.sale, amountinterface);
                    setTimer(15000, 1);
                } else {
                    Log.d(TAG, "onClick ELSE : " + numFallBack);
                    dialogFallBackCheck.dismiss();
                    dialogInsertCard.show();
                    cardmanager.startTransaction(CardManager.sale, amountinterface);
                }
            }
        });
    }
*/

    private void customDialogCheckFallBack() {
        dialogFallBackCheck = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogFallBackCheck.getLayoutInflater().inflate(R.layout.dialog_custom_alert_fall_back, null);//K.GAME 180821
        dialogFallBackCheck.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogFallBackCheck.setContentView(view);//K.GAME 180821
        dialogFallBackCheck.setCancelable(false);//K.GAME 180821


//        dialogFallBackCheck = new Dialog(this);
//        dialogFallBackCheck.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogFallBackCheck.setCancelable(false);
//        dialogFallBackCheck.setContentView(R.layout.dialog_custom_alert_fall_back);
//        dialogFallBackCheck.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogFallBackCheck.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        msgFallBackLabel = dialogFallBackCheck.findViewById(R.id.msgLabel);
//        closeFallBackImage = dialogFallBackCheck.findViewById(R.id.closeImage);
        okBtn = dialogFallBackCheck.findViewById(R.id.okBtn);
//        closeFallBackImage.setOnClickListener(new View.OnClickListener() {
//            private int cancelFlag=0;
//            @Override
//            public void onClick(View v) {
//                /*dialogFallBack.dismiss();
//                if (timer != null) {
//                    timer.cancel();
//                }
//                cardManager.stopTransaction();*/
//                numFallBack = 0;
//                /*
//                dialogFallBack.dismiss();
//                if (dialogWaiting != null)
//                    dialogWaiting.dismiss();
//                    */
//                if (timer != null)
//                    timer.cancel();
//                if (dialogInsertCard != null) {
//                    //dialogInsertCard.cancel();
//
//                    //--------------------------------------------------------------------------
//                    if (posinterface.PosInterfaceExistFlg == 1) {
////                        TellToPosError( "ND" );
//                        if(cancelFlag == 0) {
//                            cancelFlag = 1;
//                            Utility.customDialogAlertAuto(MenuServiceListActivity.this, "Cancel");   // Paul_20181017 Can
//                            TellToPosNoMatching("ND");
//                            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                @Override
//                                public void success() {
//                                    if (dialogFallBack != null && dialogFallBack.isShowing())
//                                        dialogFallBack.dismiss();
//                                    if (dialogWaiting != null && dialogWaiting.isShowing())
//                                        dialogWaiting.dismiss();
//                                    if (dialogFallBackCheck != null && dialogFallBackCheck.isShowing())
//                                        dialogFallBackCheck.dismiss();
//                                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
//                                    dialogInsertCard.cancel();
//                                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                    finish();
//                                    overridePendingTransition(0, 0);
//                                }
//                            });
//                        }
//                    } else
//                        dialogInsertCard.cancel();
//                    //---------------------------------------------------------------------------------------
//                }
//                //dialogFallBackCheck.dismiss();
//                cardManager.stopTransaction();
//
//            }
//        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialogAll();
                if (numFallBack > 2) {      // Paul_20181117 error dialog 3 time display
                    System.out.printf("utility:: %s onTransResultFallBack okBtn 000000000002 \n", TAG);
                    Log.d(TAG, "onClick IF : " + numFallBack);
                    cardManager.setFallBackHappen();        // Paul_20181117
                    dialogFallBackCheck.dismiss();
                    dialogFallBack.show();
                    cardManager.startTransaction(CardManager.SALE, amountInterface);
                    setTimer(15000, 1);
                } else {
                    System.out.printf("utility:: %s onTransResultFallBack okBtn 000000000003 \n", TAG);
                    Log.d(TAG, "onClick ELSE : " + numFallBack);
                    dialogFallBackCheck.dismiss();
//                    dialogInsertCard.show();

                    dialogContactless = null;

                    customContactless();
                    cardManager.startTransaction(CardManager.SALE, amountInterface);
                }
            }
        });
    }

    private void customDialogCheckCard() {
        dialogCheckCard = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180924
        View view = dialogCheckCard.getLayoutInflater().inflate(R.layout.dialog_custom_alert_card, null);//K.GAME 180924
        dialogCheckCard.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180924
        dialogCheckCard.setContentView(view);//K.GAME 180924
        dialogCheckCard.setCancelable(false);//K.GAME 180924

//        dialogCheckCard = new Dialog(this);
//        dialogCheckCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogCheckCard.setCancelable(false);
//        dialogCheckCard.setContentView(R.layout.dialog_custom_alert_card);
//        dialogCheckCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogCheckCard.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        closeCardImage = dialogCheckCard.findViewById(R.id.closeImage);
        okCardBtn = dialogCheckCard.findViewById(R.id.okBtn);
        closeCardImage.setOnClickListener(new View.OnClickListener() {
            private int cancelFlag = 0;

            @Override
            public void onClick(View v) {
                dialogCheckCard.dismiss();
//                setTimer(15000, 1);
                dismissDialogAll();
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
// Paul_20181017 TMS Card Full Back Error
                if (posinterface.PosInterfaceExistFlg == 1) {
//                        TellToPosError( "ND" );
                    if (cancelFlag == 0) {
                        cancelFlag = 1;
//                        Utility.customDialogAlertAuto(MenuServiceListActivity.this, "Cancel");   // Paul_20181017 Can

                        errormsgLabel.setText("Cancel");
                        dialogAlert.show();

                        TellToPosNoMatching("ND");
                        posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                if (dialogFallBack != null && dialogFallBack.isShowing())
                                    dialogFallBack.dismiss();
                                if (dialogWaiting != null && dialogWaiting.isShowing())
                                    dialogWaiting.dismiss();
                                if (dialogFallBackCheck != null && dialogFallBackCheck.isShowing())
                                    dialogFallBackCheck.dismiss();
                                posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                dialogInsertCard.cancel();
                                Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                } else {
                    // Paul_20181024
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
        okCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCheckCard.dismiss();
                msgLabeldialogFallBack.setText("กรุณารูดบัตร");

                dialogFallBack.show();
//                setTimer(15000, 1);
                setTimer(60000, 1);         // Paul_20181019
                cardManager.startTransaction(CardManager.SALE, amountInterface);
//                                        tg.stopTone();
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }
        });
    }

    private void customDialogCheckCardPosinterface() {
        dialogCheckCardposinterface = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180924
        View view = dialogCheckCardposinterface.getLayoutInflater().inflate(R.layout.dialog_custom_alert_card, null);//K.GAME 180924
        dialogCheckCardposinterface.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogCheckCardposinterface.setContentView(view);//K.GAME 180924
        dialogCheckCardposinterface.setCancelable(false);//K.GAME 180924

//        dialogCheckCardposinterface = new Dialog(this);
//        dialogCheckCardposinterface.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogCheckCardposinterface.setCancelable(false);
//        dialogCheckCardposinterface.setContentView(R.layout.dialog_custom_alert_card);
//        dialogCheckCardposinterface.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogCheckCardposinterface.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        closeCardImage = dialogCheckCardposinterface.findViewById(R.id.closeImage);
//        okCardBtn = dialogCheckCardposinterface.findViewById( R.id.okBtn);
    }

    private void showMessageResCode() {
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(String response) {
                System.out.printf("utility:: MenuServiceListActivity showMessageResCode 000002 \n");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting != null) {
                            dialogWaiting.dismiss();
                        }

                        dialogAlert.show();
                        dismissDialogAll();
                    }
                });
            }

            @Override
            public void onResponseCodeandMSG(final String response, String szCode) {
                System.out.printf("utility:: MenuServiceListActivity onResponseCodeandMSG 000002 \n");
                ////20180725  SINN VOID HOST reject EDC still waiting
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting != null) {
                            dialogWaiting.dismiss();
                        }
                        errormsgLabel.setText(response);
                        dialogAlert.show();
                        dismissDialogAll();
                    }
                });
            }

            @Override
            public void onResponseCodeSuccess() {

            }

            @Override
            public void onConnectTimeOut() {
                System.out.printf("utility:: MenuServiceListActivity onConnectTimeOut 00014 \n");
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }

// Paul_20180717
                if (posinterface.PosInterfaceExistFlg == 1) {
                    if (dialogAlert != null && dialogAlert.isShowing()) {
                        dialogAlert.dismiss();      // Paul_20181112 connectTime out Test
                    }
//                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "ไม่สามารถเชื่อมต่อได้");

                    errormsgLabel.setText("ไม่สามารถเชื่อมต่อได้");
                    dialogAlert.show();

//                    TellToPosError( "EN" );
//                    TellToPosNoMatching("21" );
                    TellToPosNoMatching("EN");     // Paul_20180731
                    posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                            Utility.customDialogAlertAutoClear();
                            System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
//                                Utility.customDialogAlert(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                        finish();
//                                    }
//                                });
                                errormsgLabel.setText("เชื่อมต่อล้มเหลว");
                                dialogAlert.show();
                            }
                        }
                    });
            }

            @Override
            public void onTransactionTimeOut() {
                System.out.printf("utility:: MenuServiceListActivity onTransactionTimeOut 00013 \n");
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
// Paul_20180717
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
// Paul_20180718
//                            if (localtypeInterface != null) {
                            if (posinterface.PosInterfaceExistFlg == 1) {
                                if (dialogAlert != null && dialogAlert.isShowing()) {
                                    dialogAlert.dismiss();      // Paul_20181112 connectTime out Test
                                }
//                                Utility.customDialogAlertAuto(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว");

                                errormsgLabel.setText("ไม่สามารถเชื่อมต่อได้");
                                dialogAlert.show();

                                TellToPosNoMatching("  ");
                                posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                        dismissDialogAll();
                                        Utility.customDialogAlertAutoClear();
// Paul_20180718
                                        Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
//                                Utility.customDialogAlert(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                        finish();
//                                    }
//                                });
                                errormsgLabel.setText("เชื่อมต่อล้มเหลว");
                                dialogAlert.show();
                            }
                        }
                    }
                });
            }
        });
    }

    public void customDialogAlert() {
        if (dialogAlert != null) {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        }
        dialogAlert = new Dialog(MenuServiceListActivity.this, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

        errormsgLabel = dialogAlert.findViewById(R.id.msgLabel);

//        dialogAlert = new Dialog(MenuServiceListActivity.this);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView( R.layout.dialog_custom_alert);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821
        btn_close.setOnClickListener(new View.OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                dialogAlert.dismiss();
                cardManager.startTransaction(CardManager.SALE, amountInterface);
                dismissDialogAll();
            }
        });

    }


    public void setDataTestHostHealthCare() {
        System.out.printf("utility:: setDataTestHostHealthCare 001 \n");
        String terminalVersion = "00000001";
        String messageVersion = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MESSAGE_GHC_VERSION);
//        String messageVersion = "0008";
        String transactionCode = "4017";
        String messageLen = "00000058";
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "        ";           // Paul_20180522
        TERMINAL_ID = CardPrefix.getTerminalId(MenuServiceListActivity.this, "GHC");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(MenuServiceListActivity.this, "GHC");
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "990000";
//        mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.parseInt(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)));
        mBlockDataSend[24 - 1] = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_NII_GHC);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((messageLen + terminalVersion + messageVersion + transactionCode + checkSUM).length())) + BlockCalculateUtil.getHexString(messageLen + terminalVersion + messageVersion + transactionCode + checkSUM);

        TPDU = CardPrefix.getTPDU(MenuServiceListActivity.this, "GHC");
        packageAndSend(TPDU, "0800", mBlockDataSend);
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.printf("utility:: %s onStart \n", TAG);
        if (cardManager == null) {
            System.out.printf("utility:: %s onStart cardManager = null \n", TAG);
        } else {
            System.out.printf("utility:: %s onStart cardManager != null \n", TAG);
        }
    }

    private void getStringJsonCAPK() {
        File file = new File("/cache/customer/media/print_param.json");
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;
        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
//                String Inv = jsonObject.getString("Inv");
                JSONObject ObjParam = jsonObject.getJSONObject("param");
                String id = ObjParam.getString("id");
                String termSeq = ObjParam.getString("termSeq");    //"termSeq":"D1V0250000022",

                String merchantNameLine1 = ObjParam.getString("merchantNameLine1");
                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_MERCHANT_1, merchantNameLine1);

                String merchantNameLine2 = ObjParam.getString("merchantNameLine2");
                String merchantNameLine3 = ObjParam.getString("merchantNameLine3");
                String TAX_ID = ObjParam.getString("TAX_ID");
                String POS_ID = ObjParam.getString("POS_ID");
                Double fee = ObjParam.getDouble("fee");

                String qrAid = ObjParam.getString("qrAid");
                String qrRef2 = ObjParam.getString("qrRef2");
                String qrBillerId = ObjParam.getString("qrBillerId");
                String qrMerchantName = ObjParam.getString("qrMerchantName");
                String qrTerminalId = ObjParam.getString("qrTerminalId");
                String qrMerchantId = ObjParam.getString("qrMerchantId");
                String qrBillerkey = ObjParam.getString("qrBillerkey");

                String primaryIp = ObjParam.getString("primaryIp");
                String primaryPort = ObjParam.getString("primaryPort");
                String secondaryIp = ObjParam.getString("secondaryIp");
                String secondaryPort = ObjParam.getString("secondaryPort");
                String qrPort = ObjParam.getString("qrPort");

                String posTerminalId = ObjParam.getString("posTerminalId");
                String posMerchantId = ObjParam.getString("posMerchantId");
                String posTpdu = ObjParam.getString("posTpdu");
                String posNii = ObjParam.getString("posNii");

                String epsTerminalId = ObjParam.getString("epsTerminalId");
                String epsMerchantId = ObjParam.getString("epsMerchantId");
                String epsTpdu = ObjParam.getString("epsTpdu");
                String epsNii = ObjParam.getString("epsNii");

                String tmsTerminalId = ObjParam.getString("tmsTerminalId");
                String tmsMerchantId = ObjParam.getString("tmsMerchantId");
                String tmsTpdu = ObjParam.getString("tmsTpdu");
                String tmsNii = ObjParam.getString("tmsNii");
                String tmsTerminaversion = ObjParam.getString("tmsTerminaversion");
                String tmsMsgVersion = ObjParam.getString("tmsMsgVersion");

                Log.d(TAG, "getStringJsonCAPK: " + id + " szPara : " + termSeq);

                JSONArray jsonArray = jsonObject.getJSONArray("cardFee");
                Log.d(TAG, "getStringJsonCAPK: " + jsonArray);
                Preference.getInstance(this).setValueString(Preference.KEY_JSON_CARD_FEE, jsonArray.toString());
                Log.d(TAG, "getStringJsonCAPK PF: " + Preference.getInstance(this).getValueString(Preference.KEY_JSON_CARD_FEE));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "onCreate: " + getDirectoryPath);
    }

    private void setDialog() {
        builder = new AlertDialog.Builder(MenuServiceListActivity.this);
        builder.setMessage("คุณต้องการออกจากระบบ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        builder.create();
    }

    private void onCheckHost() {
        if (cardManager != null)
            cardManager.setTestHostLister(new CardManager.TestHostLister() {
                @Override
                public void onResponseCodeSuccess() {
                    System.out.printf("utility:: MenuServiceListActivity onResponseCodeSuccess 00012 \n");
                    if (!isFinishing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.customDialogAlertSuccess(MenuServiceListActivity.this, null, new Utility.OnClickCloseImage() {
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
                public void onConnectTimeOut() {
                    System.out.printf("utility:: MenuServiceListActivity onConnectTimeOut 00011 \n");
                    if (!isFinishing()) {
// Paul_20180717
// Paul_20180717
//        if(localtypeInterface != null) {
                        if (posinterface.PosInterfaceExistFlg == 1) {
//                            Utility.customDialogAlertAuto(MenuServiceListActivity.this, "ไม่สามารถเชื่อมต่อได้");

                            errormsgLabel.setText("ไม่สามารถเชื่อมต่อได้");
                            dialogAlert.show();

                            System.out.printf("utility:: onCheckHost onConnectTimeOut 77777 \n");
                            //                            TellToPosError( "EN" );
                            TellToPosNoMatching("21");
                            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                @Override
                                public void success() {
                                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                    Utility.customDialogAlertAutoClear();
                                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Utility.customDialogAlert(MenuServiceListActivity.this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
//                                        @Override
//                                        public void onClickImage(Dialog dialog) {
//                                            dialog.dismiss();
//                                        }
//                                    });

                                    errormsgLabel.setText("ไม่สามารถเชื่อมต่อได้");
                                    dialogAlert.show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onTransactionTimeOut() {
                    System.out.printf("utility:: MenuServiceListActivity onTransactionTimeOut 00010 \n");
                    if (!isFinishing()) {
                        if (posinterface.PosInterfaceExistFlg == 1) {
//                            Utility.customDialogAlertAuto(MenuServiceListActivity.this, "ไม่สามารถเชื่อมต่อได้");
//                            TellToPosError( "EN" );

                            errormsgLabel.setText("ไม่สามารถเชื่อมต่อได้");
                            dialogAlert.show();

                            System.out.printf("utility:: onCheckHost onConnectTimeOut 8888 \n");
                            TellToPosNoMatching("21");
                            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                @Override
                                public void success() {
                                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                                    Utility.customDialogAlertAutoClear();
                                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Utility.customDialogAlert(MenuServiceListActivity.this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
//                                        @Override
//                                        public void onClickImage(Dialog dialog) {
//                                            dialog.dismiss();
//                                        }
//                                    });

                                    errormsgLabel.setText("ไม่สามารถเชื่อมต่อได้");
                                    dialogAlert.show();
                                }
                            });
                    }
                }
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

    @Override
    protected void onResume() {
        is_dialogInsertAmountShowing = false;               // Paul_20181205 K.hong double click wrong
        System.out.printf("utility:: MenuServiceListActivity onResume \n");
        super.onResume();

        numFallBack = 0;

        if (cardManager != null) {
            showMessageResCode();
            onCheckHost();
        }

        // Paul_20181127
        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_RS232_Enable_ID).equalsIgnoreCase("1")) {
            try {
                if (posinterface.checkRS23open() != null)//SINN 20180716 //reset typeinterface
                    posinterface.PosInterfaceClose();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (posinterface.checkRS23open() != null)//SINN 20180716 //reset typeinterface
                    posinterface.PosInterfaceOpen();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView app_title = findViewById(R.id.app_title);
                if (!Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                    app_title.setText(Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_MERCHANT_1));
            }
        });

        Log.d(TAG, "SINN : " + "inTriger" + String.valueOf(inTriger));


    }

    @Override
    protected void onPause() {
        System.out.printf("utility:: MenuServiceListActivity onPause \n");
        super.onPause();
        if (cardManager != null) {
            cardManager.removeResponseCodeListener();
            cardManager.removeTestHostLister();
            cardManager.stopTransaction();
            cardManager.abortPBOCProcess();
        }
    }

    @Override
    protected void connectTimeOut() {
        System.out.printf("utility:: MenuServiceListActivity connectTimeOut \n");
// Paul_20180717
//        if(localtypeInterface != null) {
        if (posinterface.PosInterfaceExistFlg == 1) {
//            Utility.customDialogAlertAuto(MenuServiceListActivity.this, "connectTimeOut");

            errormsgLabel.setText("Connect Timeout");
            dialogAlert.show();

//            TellToPosError( "EN" );
            TellToPosNoMatching("21");
            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                    Utility.customDialogAlertAutoClear();
                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
            Utility.customDialogAlert(this, "connectTimeOut", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
// Paul_20180809
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
    }

    @Override
    protected void transactionTimeOut() {
        System.out.printf("utility:: MenuServiceListActivity transactionTimeOut \n");
// Paul_20180717
//        if(localtypeInterface != null) {
        if (posinterface.PosInterfaceExistFlg == 1) {
//            Utility.customDialogAlertAuto(MenuServiceListActivity.this, "transactionTimeOut");

            errormsgLabel.setText("Transaction Timeout");
            dialogAlert.show();

//            TellToPosError( "EN" );
            TellToPosNoMatching("21");
            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                    Utility.customDialogAlertAutoClear();
                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
            Utility.customDialogAlert(this, "transactionTimeOut", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
// Paul_20180809
                    Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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

        System.out.printf("utility:: %s received 0000000000001 \n", TAG);
        String de39 = BlockCalculateUtil.hexToString(data[39 - 1]);
        if (mBlockDataSend[3 - 1].equalsIgnoreCase("005000") || mBlockDataSend[3 - 1].equalsIgnoreCase("025000") && de39.equalsIgnoreCase("00")) {
            if (typeClick == "GHC") {
                removeReversal();
            }
            // Reversal Message
//            Utility.customDialogAlertSuccess(MenuServiceListActivity.this, "Reversal สำเร็จ", new Utility.OnClickCloseImage() {
            Utility.customDialogAlertSuccessAuto(MenuServiceListActivity.this, "Reversal สำเร็จ", new Utility.OnClickCloseImage() {    ////SINN  20180716 reversal auto close
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
// Paul_20180712 Start
                    //                   if((posinterface.PosInterfaceExistFlg == 1)&& (typeClick == "GHC"))      // Paul_20180712
                    if (posinterface.PosInterfaceExistFlg == 1)      // Paul_20180712
                    {
                        System.out.printf("utility:: Reversal RECEIVE OK \n");
                        final Intent[] intent = {null};
                        int i;
                        switch (posinterface.PosInterfaceTransactionCode) {
                            case "11":      // ผู้ป่วยนอกทั่วไป สิทธิตนเองและครอบครัว
                            case "21":      // หน่วยไตเทียม สิทธิตนเองและครอบครัว
                            case "31":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิตนเองและครอบครัว
                                intent[0] = new Intent(MenuServiceListActivity.this, IDActivity.class);
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                startActivity(intent[0]);
                                overridePendingTransition(0, 0);
                                break;
                            case "12":      // ผู้ป่วยนอกทั่วไป สิทธิบุตร 0-7 ปี
                            case "22":      // หน่วยไตเทียม สิทธิบุตร 0-7 ปี
                            case "32":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิบุตร 0-7 ปี
                                if (cardCd == null) {
                                    cardCd = "0000000000000";
                                }
                                for (i = cardCd.length(); i < 13; i++) {
                                    cardCd += " ";
                                }
                                if (Long.valueOf(cardCd) == 0) {
                                    intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(KEY_INTERFACE_CARD_ID_CHILD, cardCd);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                } else {
                                    intent[0] = new Intent(MenuServiceListActivity.this, IDActivity.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(KEY_ID_CARD_CD, cardCd);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                }
                                break;
                            case "13":      // ผปู้่ายนอกทวั่ไป สิทธิคู่สมรสต่างชาติ
                            case "23":      // หน่วยไตเทียม สิทธิคู่สมรสต่างชาติ
                            case "33":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิคู่สมรสต่างชาติ
                                if (idForeigner == null) {
                                    idForeigner = "B000000000000";      // Paul_20180705
                                }
                                for (i = idForeigner.length(); i < 13; i++) {
                                    idForeigner += " ";
                                }
                                if (Long.valueOf(idForeigner.substring(1, idForeigner.length())) == 0) {
                                    intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, idForeigner);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                } else {
//                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);
//                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);  //SINN 20181015 SINN GHC UI
                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivityNew.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, idForeigner);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                }
                                break;
                            case "14":      // ผู้ป่วยนอกทั่วไป ไม่สามารถใช้บัตรได้
                            case "24":      // หน่วยไตเทียม ไม่สามารถใช้บัตรได้
                            case "34":      // หน่วยรังสีผู้เป็นมะเร็ง ไม่สามารถใช้บัตรได้
                                if (nocardCd == null) {
                                    nocardCd = "0000000000000";
                                }
                                for (i = nocardCd.length(); i < 13; i++) {
                                    nocardCd += " ";
                                }
                                if (Long.valueOf(nocardCd) == 0) {   // if (Long.valueOf(idForeigner.substring(1, nocardCd.length())) == 0) {
                                    intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, nocardCd);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                } else {
//                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);
//                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);  //SINN 20181015 SINN GHC UI
                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivityNew.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, nocardCd);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                }
                                break;
                            case "20":
                                localtypeInterface = "Interface";  //set rs232 KEY_TYPE_INTERFACE
                                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF1, "");
                                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF2, "");
                                Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF3, "");

                                System.out.printf("utility:: ref1 AAAAAAAAAAAAA 0001  ref1 = %s \n", ref1);
                                System.out.printf("utility:: ref1 AAAAAAAAAAAAA 0001  ref2 = %s \n", ref2);
                                System.out.printf("utility:: ref1 AAAAAAAAAAAAA 0001  ref3 = %s \n", ref3);

//                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "0");  //SINN RS232 SALE 20180709;
                                cardManager.setFalseFallbackHappen();
                                if (checkReversal("SALE")) {
                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF1, ref1);
                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF2, ref2);
                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF3, ref3);


                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "1");  //SINN RS232 SALE 20180709;
                                    startInsertCard();
                                }
                                break;
                            case "26":      // รายการยกเลกิ     // void
                                intent[0] = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                intent[0].putExtra(KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                                intent[0].putExtra(KEY_INTERFACE_VOID_APPROVAL_CODE, approvalCode);     // Paul_20180716
                                startActivity(intent[0]);
                                overridePendingTransition(0, 0);
                                break;
                            case "50":
                                intent[0] = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                startActivity(intent[0]);
                                overridePendingTransition(0, 0);
                                break;
                        }
// Paul_20180712 End
                    } else if (typeClick.equalsIgnoreCase("VOID")) {
                        Intent intent = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (typeClick.equalsIgnoreCase("SETTLEMENT")) {
                        Intent intent = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (typeClick.equalsIgnoreCase("GHC")) {
                        // Paul_20180809
                        if (GHCVoidFlg == 1) {
                            Intent intent = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);

/*
                            Intent intent = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                            intent.putExtra(KEY_TYPE_INTERFACE, "Interface");
                            intent.putExtra(KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                            intent.putExtra(KEY_INTERFACE_VOID_APPROVAL_CODE, approvalCode);     // Paul_20180716
                            startActivity(intent);
                            overridePendingTransition(0, 0);
*/
                        } else {
                            Intent intent = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    }
                }
            });

        } else if (mBlockDataSend[3 - 1].equalsIgnoreCase("005000") || mBlockDataSend[3 - 1].equalsIgnoreCase("025000") && !de39.equalsIgnoreCase("00")) {
            Utility.customDialogAlert(this, "Error : " + de39, new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else {
            Utility.customDialogAlertSuccess(MenuServiceListActivity.this, null, new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void error(String error) {
        System.out.printf("utility:: MenuServiceListActivity error \n");
//        Utility.customDialogAlert(this, "error 006", new Utility.OnClickCloseImage() {
//            @Override
//            public void onClickImage(Dialog dialog) {
//                dialog.dismiss();
//                finish();
//            }
//        });
        Utility.customDialogAlert(this, "Error", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
// Paul_20180809
                Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
        System.out.printf("utility:: MenuServiceListActivity other \n");
        Utility.customDialogAlert(this, "other", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        System.out.printf("utility:: MenuServiceListActivity onStop \n");
        super.onStop();
        if (realm != null)   // Paul_20180803
        {
            realm.close();
            realm = null;   // Paul_20181026 Some time DB Read error solved
        }
        dismissDialogAll();
    }

    // Paul_20181019 IsFinishing() == true
    @Override
    protected void onDestroy() {
        System.out.printf("utility:: MenuServiceListActivity onDestroy \n");
        super.onDestroy();
//        if (realm != null)
//            realm.close();
//        dismissDialogAll();
    }

    @Override
    public void onBackPressed() {
        System.out.printf("utility:: MenuServiceListActivity onBackPressed \n");
//        super.onBackPressed();
        builder.show();
        is_dialogInsertAmountShowing = false;
    }

    //SINN RS232 not found
/*
    public void TellToPosNoMatching(String szErr)
    {
        posinterface.PosInterfaceWriteField("01","000xxxxxx");   // Approval Code
        posinterface.PosInterfaceWriteField("02",posinterface.ResponseMsgPosInterface(szErr));

        posinterface.PosInterfaceWriteField("65","000000");   // Invoice Number
        posinterface.PosInterfaceWriteField("D3","xxxxxxxxxxxx");
        if (cardManager.getHostCard().equalsIgnoreCase("EPS")) {
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
        }
        else if (cardManager.getHostCard().equalsIgnoreCase("TMS")) {
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
        }
        else if (cardManager.getHostCard().equalsIgnoreCase("POS")){
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
        }
        else
        {
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        }
        Date date = new Date();
        String dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);
        posinterface.PosInterfaceWriteField("03",dateFormatDef);  //yymmdd

        String timeFormat = new SimpleDateFormat("HHmmss").format(date);
        posinterface.PosInterfaceWriteField("04",timeFormat);  //hhmmss

//        posinterface.PosInterfaceWriteField("F1","QR");

        posinterface.PosInterfaceSendMessage(posinterface.PosInterfaceTransactionCode,szErr);
    }
    */
    public void TellToPosNoMatching(String szErr) {
        posinterface.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posinterface.PosInterfaceSendMessage(posinterface.PosInterfaceTransactionCode, szErr);
    }


/*
    public void TellToPosError(String szErr)
    {
        posinterface.PosInterfaceWriteField("01","000xxxxxx");   // Approval Code
        //posinterface.PosInterfaceWriteField("02",posinterface.ResponseMsgPosInterface("12"));   // Response Message TX NOT FOUND
        posinterface.PosInterfaceWriteField("02",posinterface.ResponseMsgPosInterface(szErr));

        posinterface.PosInterfaceWriteField("65","000000");   // Invoice Number
        posinterface.PosInterfaceWriteField("D3","xxxxxxxxxxxx");

        if (cardManager.getHostCard().equalsIgnoreCase("EPS")) {
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
        }
        else if (cardManager.getHostCard().equalsIgnoreCase("TMS")) {
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
        }
        else if (cardManager.getHostCard().equalsIgnoreCase("POS")){
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
        }
        else
        {
            posinterface.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
            posinterface.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        }

        Date date = new Date();
        String dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);
        posinterface.PosInterfaceWriteField("03",dateFormatDef);  //yymmdd

        String timeFormat = new SimpleDateFormat("HHmmss").format(date);
        posinterface.PosInterfaceWriteField("04",timeFormat);  //hhmmss

        posinterface.PosInterfaceWriteField("F1","QR");

        //posinterface.PosInterfaceSendMessage(posinterface.PosInterfaceTransactionCode,"12");
        posinterface.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,szErr);
    }
    */

    Handler pos_interface_Handler = new Handler() {
        public void handleMessage(Message msg) {
            final Intent[] intent = {null};
//            Log.d("utility::", "posinterface.PosInterfaceExistFlg=" + String.valueOf(posinterface.PosInterfaceExistFlg));
//            Log.d("utility::", "posinterface.PosInterfaceTransactionCode=" + posinterface.PosInterfaceTransactionCode.toString());
            System.out.printf("utility:: %s , pos_interface_Handler 0001 \n", TAG);
            System.out.printf("utility:: %s posinterface.PosInterfaceTransactionCode 0001 = %s \n", TAG, posinterface.PosInterfaceTransactionCode);

            pos_interface_Handler.removeMessages(0);      // Paul_20180713
            if (posinterface.PosInterfaceExistFlg != 1) {
                return;
            }
            System.out.printf("utility:: %s posinterface.PosInterfaceTransactionCode 0002 = %s \n", TAG, posinterface.PosInterfaceTransactionCode);

            int inChk = 0;

            switch (posinterface.PosInterfaceTransactionCode) {
                case "11":      // ผู้ป่วยนอกทั่วไป สิทธิตนเองและครอบครัว
                case "21":      // หน่วยไตเทียม สิทธิตนเองและครอบครัว
                case "31":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิตนเองและครอบครัว
                    if (checkAllBatch() != 1)    // Paul_20180803
                        CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                    // Paul_20181014 Settlement checking
                    if (checkBatchSettlement() == 1) {
                        break;
                    }
                    //20180724 SINN AMT 0
                    try {
                        inChk = Integer.valueOf(amountInterface);
                    } catch (Exception e) {
                    }

                    if (inChk == 0)
                        ZeroAmtCallBack();
                    else {
                        if (!checkReversalGHC()) {

                            intent[0] = new Intent(MenuServiceListActivity.this, IDActivity.class);
                            intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                            intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                            intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                            startActivity(intent[0]);

                            //                    overridePendingTransition(0, 0);
                        } else {
                            sendDataReversal(reversalHealthCare);
                        }
                        overridePendingTransition(0, 0);
                    }
                    break;
                case "12":      // ผู้ป่วยนอกทั่วไป สิทธิบุตร 0-7 ปี
                case "22":      // หน่วยไตเทียม สิทธิบุตร 0-7 ปี
                case "32":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิบุตร 0-7 ปี
                    if (checkAllBatch() != 1)    // Paul_20180803
                        CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                    // Paul_20181014 Settlement checking
                    if (checkBatchSettlement() == 1) {
                        break;
                    }// Paul_20180703
                    //20180724 SINN AMT 0
                    try {
                        inChk = Integer.valueOf(amountInterface);
                    } catch (Exception e) {
                    }

                    if (inChk == 0)
                        ZeroAmtCallBack();
                    else {
                        if (!checkReversalGHC()) {
                            if (cardCd == null) {
                                cardCd = "0000000000000";
                            }
                            if (Long.valueOf(cardCd) == 0) {
                                //                        System.out.printf("utility:: !!!!!!!!!!!!!!  0003 \n");
                                intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                intent[0].putExtra(KEY_INTERFACE_CARD_ID_CHILD, cardCd);
                                startActivity(intent[0]);
                                overridePendingTransition(0, 0);
                            } else {
                                //                        System.out.printf("utility:: !!!!!!!!!!!!!!  0004 \n");
                                intent[0] = new Intent(MenuServiceListActivity.this, IDActivity.class);
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                intent[0].putExtra(KEY_ID_CARD_CD, cardCd);
                                startActivity(intent[0]);
                                overridePendingTransition(0, 0);
                            }
                        } else {
                            sendDataReversal(reversalHealthCare);
                        }
                    }
                    break;
                case "13":      // ผปู้่ายนอกทวั่ไป สิทธิคู่สมรสต่างชาติ
                case "23":      // หน่วยไตเทียม สิทธิคู่สมรสต่างชาติ
                case "33":      // หน่วยรังสีผู้เป็นมะเร็ง สิทธิคู่สมรสต่างชาติ
                    if (checkAllBatch() != 1)    // Paul_20180803
                        CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                    // Paul_20181014 Settlement checking
                    if (checkBatchSettlement() == 1) {
                        break;
                    }// Paul_20180703
                    //20180724 SINN AMT 0
                    try {
                        inChk = Integer.valueOf(amountInterface);
                    } catch (Exception e) {
                    }

                    if (inChk == 0)
                        ZeroAmtCallBack();
                    else {
                        if (!checkReversalGHC()) {
                            if (idForeigner == null) {
                                idForeigner = "B000000000000";      // Paul_20180705
                            }
                            if (Long.valueOf(idForeigner.substring(1, idForeigner.length())) == 0) {
                                intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, idForeigner);
                                startActivity(intent[0]);
                                overridePendingTransition(0, 0);
                            } else {
//                                intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);
//                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);  //SINN 20181015 SINN GHC UI
                                intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivityNew.class);
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, idForeigner);
                                startActivity(intent[0]);
                                overridePendingTransition(0, 0);
                            }
                        } else {
                            sendDataReversal(reversalHealthCare);
                        }
                        overridePendingTransition(0, 0);
                    }
                    break;
                case "14":      // ผู้ป่วยนอกทั่วไป ไม่สามารถใช้บัตรได้
                case "24":      // หน่วยไตเทียม ไม่สามารถใช้บัตรได้
                case "34":      // หน่วยรังสีผู้เป็นมะเร็ง ไม่สามารถใช้บัตรได้
                    if (checkAllBatch() != 1)    // Paul_20180803
                        CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                    // Paul_20181014 Settlement checking
                    if (checkBatchSettlement() == 1) {
                        break;
                    }
                    //20180724 SINN AMT 0
                    try {
                        inChk = Integer.valueOf(amountInterface);
                    } catch (Exception e) {
                    }

                    if (inChk == 0)
                        ZeroAmtCallBack();
                    else {
                        if (!checkReversalGHC()) {
// Paul_20181014    Json and Settlement checking
                            if (checkBatchSettlement() != 1) {
                                if (nocardCd == null) {
                                    nocardCd = "0000000000000";
                                }
                                if (Long.valueOf(nocardCd) == 0) {   // if (Long.valueOf(idForeigner.substring(1, nocardCd.length())) == 0) {
                                    intent[0] = new Intent(MenuServiceListActivity.this, MedicalTreatmentActivity.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, nocardCd);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                } else {
//                                    intent[0] = new Intent( MenuServiceListActivity.this, CalculateHelthCareActivity.class );
//                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivity.class);  //SINN 20181015 SINN GHC UI
                                    intent[0] = new Intent(MenuServiceListActivity.this, CalculateHelthCareActivityNew.class);
                                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                                    intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                                    intent[0].putExtra(MedicalTreatmentActivity.KEY_STATUS_SALE, posinterface.PosInterfaceTransactionCode);
                                    intent[0].putExtra(KEY_ID_FOREIGNER_NUMBER, nocardCd);
                                    startActivity(intent[0]);
                                    overridePendingTransition(0, 0);
                                }
                            }
                        } else {
                            sendDataReversal(reversalHealthCare);
                        }
                        overridePendingTransition(0, 0);
                    }

                    break;
                case "20":
                    ////SINN 20181129 Add way2 to UAT6
                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_SERVICE_PIN_ID, "0");


                    if (checkAllBatch() != 1)    // Paul_20180803
                        CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                    // Paul_20181014 Settlement checking
                    if (checkBatchSettlement() == 1) {
                        break;
                    }
                    //20180724 SINN AMT 0
                    try {
                        inChk = Integer.valueOf(amountInterface);
                    } catch (Exception e) {
                    }
                    Log.d("Utility::", " case \"20\":" + String.valueOf(inChk));

                    System.out.printf("utility:: ZZZZZZZZZZZZ 001 \n");
                    if (inChk == 0)
                        ZeroAmtCallBack();
                    else {
                        System.out.printf("utility:: ZZZZZZZZZZZZ 002 \n");
                        // รายการขาย Card และ QR Cod
                        pos_interface_Handler.removeMessages(0);      // Paul_20180713
                        final String finalAmountInterface = amountInterface;
                        final String finalRef1 = ref1;
                        final String finalRef2 = ref2;
                        final String finalRef3 = ref3;

                        System.out.printf("utility:: amountInterface = %s \n", amountInterface);
                        System.out.printf("utility:: BBBBBBBBBB ref1 = %s \n", ref1);
                        System.out.printf("utility:: BBBBBBBBBB ref2 = %s \n", ref2);
                        System.out.printf("utility:: BBBBBBBBBB ref3 = %s \n", ref3);
                        // runOnUiThread(new Runnable() {   //20181013 SINN remove  runOnUiThread
//                            @Override
//                            public void run() { //20181013 SINN remove  runOnUiThread
//                                if (dialogWaiting != null) {
//                                    dialogWaiting.dismiss();
//                                }
                        System.out.printf("utility:: ZZZZZZZZZZZZ 003 \n");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "0");  //SINN RS232 SALE 20180709;
                        // dismissDialogAll();
                        System.out.printf("utility:: ZZZZZZZZZZZZ 004 \n");
                        //K.GAME 180926 close dialog เอาไดอะล็อค ออก
//                                Utility.customDialogAlertSelect(MenuServiceListActivity.this, "เชื่อมต่อเครื่องคอมฯ สอด/รูด กด * ทำรายการ QR", new Utility.onTouchoutSide() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//
//                                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "1");  //SINN RS232 SALE 20180709;
//                                        intent[0] = new Intent(MenuServiceListActivity.this, GenerateQrActivity.class);
//                                        intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
//                                        intent[0].putExtra(KEY_INTERFACE_AMOUNT, finalAmountInterface);
//                                        intent[0].putExtra(KEY_INTERFACE_REF1, finalRef1);
//                                        intent[0].putExtra(KEY_INTERFACE_REF2, finalRef2);
//                                        intent[0].putExtra(KEY_INTERFACE_REF3, finalRef3);
//                                        startActivity(intent[0]);
//                                        overridePendingTransition(0, 0);

//-------------------------------------------------------------------------------------------------
//
//                                    }
//
//                                    @Override
//                                    public void onCancel(Dialog dialog) {
                        dismissDialogAll();
//                                        dialog.dismiss();
                        localtypeInterface = "Interface";  //set rs232 KEY_TYPE_INTERFACE


                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF1, "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF2, "");
                        Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF3, "");


//                                    Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "0");  //SINN RS232 SALE 20180709;
                        cardManager.setFalseFallbackHappen();
                        if (checkReversal("SALE")) {

                            System.out.printf("utility:: amountInterface = %s \n", amountInterface);
                            System.out.printf("utility:: CCCCCCCCCC finalRef1 = %s \n", finalRef1);
                            System.out.printf("utility:: CCCCCCCCCC finalRef2 = %s \n", finalRef2);
                            System.out.printf("utility:: CCCCCCCCCC finalRef3 = %s \n", finalRef3);
                            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF1, finalRef1);
                            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF2, finalRef2);
                            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_REF3, finalRef3);


                            Preference.getInstance(MenuServiceListActivity.this).setValueString(Preference.KEY_RS232_FLAG, "1");  //SINN RS232 SALE 20180709;
                            startInsertCard();
                        }
//                                    }
//                                });
                        //END K.GAME 180926 เอาไดอะล็อค ออก
//                            }//run  //20181013 SINN remove  runOnUiThread

//                        });  //20181013 SINN remove  runOnUiThread
                    }
                    break;
                case "26":      // รายการยกเลกิ     // void
                    if (checkReversal("VOID")) {
                        if (!checkReversalGHC()) {
                            if (checkAllBatch() != 1)    // Paul_20180803
                                CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                            // Paul_20181014 Settlement checking
                            if (checkBatchSettlement() == 1) {
                                break;
                            }
                            intent[0] = new Intent(MenuServiceListActivity.this, VoidActivity.class);
                            intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                            intent[0].putExtra(KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                            intent[0].putExtra(KEY_INTERFACE_VOID_APPROVAL_CODE, approvalCode);     // Paul_20180716
                            startActivity(intent[0]);
                        } else {
                            if (checkAllBatch() != 1)    // Paul_20180803
                                CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                            // Paul_20181014 Settlement checking
                            if (checkBatchSettlement() == 1) {
                                break;
                            }
                            sendDataReversal(reversalHealthCare);
                        }
                    }
                    overridePendingTransition(0, 0);
                    break;
                case "50":      // รายการโอนยอด     // Settlement
                    if (checkReversal("SETTLEMENT")) {
                        if (!checkReversalGHC()) {
                            intent[0] = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                            intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                            startActivity(intent[0]);
//                            overridePendingTransition(0, 0);
                        } else {
                            sendDataReversal(reversalHealthCare);
                        }
                    }
                    overridePendingTransition(0, 0);
                    break;
                case "92":      // รายการพิมพ์สลิปซ ้า  // reprint
                    //SINN 20180710 POS REPRINT
                    String szMSG = "";
                    if (!F1_POS_MSG.isEmpty())  //SINN 20180712  reprint pos : hc receive go out program
                        szMSG = F1_POS_MSG.substring(0, 2);
                    Log.d("POS", F1_POS_MSG);
//                    if (szMSG.equals("QR")) {
//                        intent[0] = new Intent(MenuServiceListActivity.this, CheckQrActivity.class);
//                        intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
//                        intent[0].putExtra(KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
//                        startActivity(intent[0]);
//                        overridePendingTransition(0, 0);
//                    }
//                    else
                {
                    Log.d("POS REPRINT", "RS232 PRINT POS");
                    intent[0] = new Intent(MenuServiceListActivity.this, PosReprintActivity.class);
                    intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                    intent[0].putExtra(KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                    intent[0].putExtra(KEY_INTERFACE_VOID_APPROVAL_CODE, approvalCode);     // Paul_20183024
                    intent[0].putExtra(KEY_INTERFACE_F1_POS_MSG, F1_POS_MSG);
                    startActivity(intent[0]);
                    overridePendingTransition(0, 0);
                    //END SINN 20180710 POS REPRINT
                }
                break;
                case "QR":      // รายการขาย QR Code
                    if (checkAllBatch() != 1)    // Paul_20180803
                        CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                    // Paul_20181014 Settlement checking
                    if (checkBatchSettlement() == 1) {
                        break;
                    }
                    //20180724 SINN AMT 0
                    try {
                        inChk = Integer.valueOf(amountInterface);
                    } catch (Exception e) {
                    }

                    if (inChk == 0)
                        ZeroAmtCallBack();
                    else {
                        intent[0] = new Intent(MenuServiceListActivity.this, GenerateQrActivity.class);
                        intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                        intent[0].putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                        intent[0].putExtra(KEY_INTERFACE_REF1, ref1);
                        intent[0].putExtra(KEY_INTERFACE_REF2, ref2);
                        intent[0].putExtra(KEY_INTERFACE_REF3, ref3);
                        startActivity(intent[0]);
                        overridePendingTransition(0, 0);
                    }
                    break;
                case "IQ":      // ตรวจสอบ QR Code
// Paul_20181014    Json and Settlement checking
                    if (checkAllBatch() != 1)    // Paul_20180803
                        CardPrefix.getStringJson(MenuServiceListActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
                    // Paul_20181014 Settlement checking
                    if (checkBatchSettlement() == 1) {
                        break;
                    }

                    if (checkBatchSettlement() != 1) {       // Paul_20180803
                        intent[0] = new Intent(MenuServiceListActivity.this, CheckQrActivity.class);
                        intent[0].putExtra(KEY_TYPE_INTERFACE, "Interface");
                        intent[0].putExtra(KEY_INTERFACE_VOID_INVOICE_NUMBER, invoiceId);
                        startActivity(intent[0]);
                        overridePendingTransition(0, 0);
                    }
                    break;
                default:                // No Matching
//                                    CalculateHelthCareActivity.TerToPosNoMatching();
                    Log.d("Utility::", "default");
                    break;
            }
        }
    };


    public void ZeroAmtCallBack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissDialogAll();
//                Utility.customDialogAlertAuto(MenuServiceListActivity.this, "จำนวนเงินไม่ถูกต้อง");

                errormsgLabel.setText("จำนวนเงินไม่ถูกต้อง");
                dialogAlert.show();

                TellToPosNoMatching("  ");
                posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("1919", "onKeyDown");
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //Include the code here
//            finish();
//        }
//        return true;

        return false;
    }

    // Paul_20180803
    private int checkAllBatch() {
        int Size = 0;
        int rv = 0;

        System.out.printf("utility:: %s checkAllBatch Start\n", TAG);
        Realm.getDefaultInstance().refresh();

//        if (realm == null) {
//            realm = Realm.getDefaultInstance();
//        }

        realm = Realm.getDefaultInstance();////20181024 SINN This Realm instance has already been closed, making it unusable.
        final RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).findAll();

        Size = transTemp.size();
        System.out.printf("utility:: %s checkAllBatch Size = %d \n", TAG, Size);
        if (Size > 0)
            rv = 1;

        return rv;
    }

    // Paul_20180803
    private int checkBatchSettlement() {
        int Size = 0;
        int rv = 0;

        if (rv == 0)           // Paul_20181028 Sinn merge version UAT6_0016
            return 0;       // Paul_20181028 Sinn merge version UAT6_0016


        System.out.printf("utility:: %s checkAllBatch Start\n", TAG);
        Realm.getDefaultInstance().refresh();
//        if (realm != null) {
//            if (!realm.isClosed()) {
//                realm.close();
//            }
//        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
//        final RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).findAll();
//        check settlement for GHC & welfare only     ////SINN 20181025  Check settlement for GHC & welfare only
//        final RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).findAll();
        final RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").findAll();

        Size = transTemp.size();
        System.out.printf("utility:: %s checkAllBatch Size = %d \n", TAG, Size);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        String TodayDate = null;

        TodayDate = dateFormat.format(date);
        System.out.printf("utility:: TodayDate = %s \n", TodayDate);
        if (Size > 0) {
            TransTemp trans = realm.where(TransTemp.class).findFirst();
            String TransDate = trans.getTransDate();
            System.out.printf("utility::  TransDate = %s \n", TransDate);
            if (!TodayDate.equalsIgnoreCase(TransDate)) {
                rv = 1;
                System.out.printf("utility:: TodayDate = %s , TransDate = %s \n", TodayDate, TransDate);
                if (posinterface.PosInterfaceExistFlg == 1) {
                    typeInterface = null;                   // Paul_20181021
//                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20181024
                    dismissDialogAll();
//                    Utility.customDialogAlertAuto(MenuServiceListActivity.this, "Please Settlement");

                    errormsgLabel.setText("Please Settlement");
                    dialogAlert.show();

                    TellToPosNoMatching("ND");
                    posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
                            Utility.customDialogAlertAutoClear();
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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
                    dismissDialogAll();     // Paul_20180723
                    Utility.customDialogAlert(MenuServiceListActivity.this, "Please Settlement", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuServiceListActivity.class);
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

            }
//            trans.getTransDate();
//            transTemp.get
//            Utility.customDialogAlert(MenuServiceListActivity.this, "Please Settlement", new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    Utility.customDialogAlertAutoClear();
//                }
//            });
        }
//        realm.close();
//        realm = null;

        return rv;
    }

    public void TellToPosError(String szErr) {
        posinterface.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posinterface.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, szErr);
    }

    private void soundsetting() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = new int[2];
        sound[0] = soundPool.load(this, R.raw.success, 1);
        sound[1] = soundPool.load(this, R.raw.fail, 1);
    }

    private void playSound(int i) {
        soundPool.play(sound[i], 1, 1, 0, 0, 1);
    }


    public class gridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public gridAdapter() {
            inflater = (LayoutInflater) MenuServiceListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return ratePriceStr.length;    //그리드뷰에 출력할 목록 수
        }

        @Override
        public Object getItem(int position) {
            return ratePriceStr[position];    //아이템을 호출할 때 사용하는 메소드
        }

        @Override
        public long getItemId(int position) {
            return position;    //아이템의 아이디를 구할 때 사용하는 메소드
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_item, parent, false);
            }

            TextView grid_item_tv = convertView.findViewById(R.id.gi_TextView);
            grid_item_tv.setText(ratePriceStr[position]);
            grid_item_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = position + 1;
                    if (pos == ratePriceStr.length) {
                        customDialog_InsertAmount();
                        dialogInsertAmountForSale.show();
                    } else {
                        String Amount = String.valueOf(ratePriceStr[position]);

                        is_dialogInsertAmountShowing = false;
                        dialogSelectAmountForSale.dismiss();
                        amountInterface = Amount;
                        Log.d(TAG, "Start :startInsertCard()");
                        cardManager.setAmountforContactless(amountInterface.replace(",", ""));

                        if (Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_CONTACTLESS_ID).equalsIgnoreCase("1")) {
                            Double __amount = Double.parseDouble(amountInterface.replace(",", ""));
                            Double __max_ctl = Double.parseDouble(Preference.getInstance(context).getValueString(Preference.KEY_MAX_CONTACTLESS_ID));

                            if (__amount <= __max_ctl) {
                                lineartranstype1.setVisibility(View.GONE);
                                lineartranstype2.setVisibility(View.VISIBLE);
                            } else {
                                lineartranstype1.setVisibility(View.VISIBLE);
                                lineartranstype2.setVisibility(View.GONE);
                            }
                        }

                        startInsertCard();


//                        Intent intent;
//                        intent = new Intent(MenuServiceListActivity.this, TransportationMain2Activity.class);
//                        intent.putExtra("Amount", Amount);
//                        startActivity(intent);
//                        finish();
                    }
                }
            });

            return convertView;

        }
    }


    Handler Rf_Handler = new Handler() {

        public void handleMessage(Message msg) {
            Log.d(TAG, "DEBUG ::: Rf_Handler1");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialogContactless != null) {
                        if (isDialogShowInsertCardShowing) {
                            Log.d(TAG, "Utility ::: startTransaction SALE");
                            cardManager.startTransaction(CardManager.SALE, amountInterface);//เปิดอ่านการ์ด
                        } else {
                            isShowInsertCard = false;
                            dialogContactless = null;
//                            timerInsertCard.purge();
//                            timerInsertCard.cancel();
//                            timerInsertCard = null;
                        }
                    }
                }
            });

//            if (!operating) {
//                Log.d(TAG, "DEBUG ::: Rf_Handler2");
//                operating = true;
////                cardManager.SetAllAid();
//                cardManager.SetTpnAid();
//                cardManager.APPROV_State = "0"; // Paul_20190305
//                cardManager.rfstartTransaction(CardManager.BUS, Amount);
//            }
//            Log.d(TAG, "DEBUG ::: Rf_Handler3");
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
                    handler2.sendEmptyMessage(READ_CARD_CANCEL);
                } else {
                    cardManager.setAmountforContactless(amountInterface);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogInsertCard.dismiss();
                            dialogContactless.dismiss();
                            dialogWaiting.show();

                        }
                    });
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
                                long beforeTime = System.currentTimeMillis();
                                
                                ret = cardManager.starPiccTrans();
                                long afterTime = System.currentTimeMillis();
                                long secDiffTime = (afterTime - beforeTime);

                                Log.d("check_time","searchcardthread:" + secDiffTime);
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

                                //dialogCardError.show();

                            }
                        });
                    }
                }




            } catch (PiccDevException | IccDevException | MagDevException e) {
                Log.e(TAG, e.getMessage());
                handler2.sendEmptyMessage(READ_CARD_ERR);
            }
        }


        public void threadstop() {
            this.stop = true;
        }

        public String toString() {
            return "Searchcardthread status/ic:" + isic + ",mag:" + ismag + ",isrf:" + isrf;
        }
    }

    public static MenuServiceListActivity getinstance() {

        return instance;
    }

    public  void showerr(final String msg) {
        myCardReaderHelper.getInstance().stopPolling();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isShowInsertCard) {
                    dialogInsertCard.dismiss();
                }

                Utility.customDialogAlert(MenuServiceListActivity.this, msg, new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                        if(dialogContactless.isShowing()) {
                            dialogContactless.dismiss();
                        }
                        if(dialogWaiting.isShowing()) {
                            dialogWaiting.dismiss();
                        }
                    }
                });

            }
        },1000);
     }

    protected Handler handler2 = new Handler() {
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

    public Dialog getDialogWaiting() {
        return this.dialogWaiting;
    }

    public Dialog getDialogInsertCard() {
        return this.dialogInsertCard;
    }

    public Dialog getDialogContactless() {
        return this.dialogContactless;
    }

    public String getAmountInterface() { return this.amountInterface;  }



}

