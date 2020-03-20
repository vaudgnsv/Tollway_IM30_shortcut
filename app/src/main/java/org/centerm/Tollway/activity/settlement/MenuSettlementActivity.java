package org.centerm.Tollway.activity.settlement;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.LoginActivity;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.adapter.MenuSettlementAdapter;
import org.centerm.Tollway.adapter.SlipSummaryReportCardAdapter;
import org.centerm.Tollway.alipay.AliConfig;
import org.centerm.Tollway.alipay.CryptoServices;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.database.ReversalHealthCare;
import org.centerm.Tollway.database.TCUpload;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.healthcare.baseavtivity.BaseHealthCardActivity;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.lang.Math.floor;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;
import static org.centerm.Tollway.activity.posinterface.PosInterfaceActivity.PosInterfaceTransactionCode;
import static org.centerm.Tollway.utility.Utility.calNumTraceNo;

//import org.centerm.Tollway.healthcare.database.SaleOfflineHealthCare;

public class MenuSettlementActivity extends BaseHealthCardActivity {

    private final String TAG = "MenuSettlementActivity";

    public static final String KEY_TYPE_HOST = MenuSettlementActivity.class.getName() + "key_type_host";

    private String msgSetmenu;

//    private RecyclerView menuSettleRecyclerView;
    private MenuSettlementAdapter menuSettlementAdapter = null;
    private ArrayList<String> menuList = null;
    private CardManager cardManager = null;
    private Realm realm = null;
    private ArrayList<TransTemp> transTemp = null;
    private ArrayList<QrCode> qrTemp = null;
    private ArrayList<QrCode> aliTemp = null;
    private ArrayList<QrCode> wecTemp = null;
    private ArrayList<TransTemp> transTempVoidFlag = null;
    private LinearLayout settlementLinearLayout;
    private RelativeLayout settlementRelativeLayout;
    private AidlPrinter printDev;
    private Dialog dialogWaiting;

    private String typeHost = null;

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
    private int settlementEnd = 1000;
    private Dialog dialogSettlement;
    private Dialog dialogSuccess_GotoMain;//K.GAME 20181018 New dialog Gotomain > finish();

    private String hostflag;
    //K.GAME 181008 New dialog Settlement
    private Dialog dialogSettlement_new;//K.GAME 181008  New dialog Settlement
    //    private TextView checkbox_Settlement_All;//K.GAME 181008  New dialog Settlement
    private TextView tv_KTB_ON_US;//K.GAME 181008  New dialog Settlement
    private TextView tv_BASE24_POS;//K.GAME 181008  New dialog Settlement
    private TextView tv_BASE24_EPS;//K.GAME 181008  New dialog Settlement
    private TextView tv_HC;//K.GAME 181008  New dialog Settlement
    private TextView tv_qr;//K.GAME 181008  New dialog Settlement
    private TextView tv_ali;
    private TextView tv_wec;    // Paul_20181023
    private LinearLayout linear_KTB_ON_US;//K.GAME 181008  New dialog Settlement
    private LinearLayout linear_BASE24_POS;
    private LinearLayout linear_BASE24_EPS;
    private LinearLayout linear_HC;
    private LinearLayout linear_qr;
    private LinearLayout linear_ali;
    private LinearLayout linear_wec;    // Paul_20181023

    private CheckBox checkbox_Settlement_All;
    private CheckBox checkbox_KTB_ON_US;
    private CheckBox checkbox_BASE24_POS;
    private CheckBox checkbox_BASE24_EPS;
    private CheckBox checkbox_Health_Care;
    private CheckBox checkbox_Qr;
    private CheckBox checkbox_Ali;
    private CheckBox checkbox_Wec;  // Paul_20181023

    private LinearLayout linear_checkbox_KTB_ON_US;
    private LinearLayout linear_checkbox_BASE24_POS;
    private LinearLayout linear_checkbox_BASE24_EPS;
    private LinearLayout linear_checkbox_Health_Care;
    private LinearLayout linear_checkbox_Qr;
    private LinearLayout linear_checkbox_Ali;
    private LinearLayout linear_checkbox_Wec;   // Paul_20181023

    private Button btn_transfer_ON_US;
    private Button btn_transfer_pos;
    private Button btn_transfer_EPS;
    private Button btn_transfer_HC;
    private Button btn_transfer_qr;
    private Button btn_transfer_ali;
    private Button btn_transfer_wec;    // Paul_20181023
    private Button btn_ok_settlement;

    int cdtItv = 6000; // K.GAME 181010
    int numsettle = 0;
    boolean checkTimer = true;
    //END K.GAME 18108 New dialog Settlement
    //K.GAME 181011 แก้บัค dialog show msg wrong
    private String SzError = null;
    //END K.GAME 181011 แก้บัค dialog show msg wrong

    private ProgressBar progressBarStatus;
    private TextView statusLabel;
    private Button okBtn;
    private Bitmap oldBitmap;
    private Dialog dialogOutOfPaper;
    private Dialog dialogPin;
    private EditText pinBox_new = null;
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
    private LinearLayout numberLinearLayout;
    private Button okPaperBtn;
    private TextView msgLabel;
    private TextView merchantName1Label;
    private TextView merchantName2Label;
    private TextView merchantName3Label;
    private ImageView closeImage;
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

    private TextView settlementTitleLabel;     // Paul_20181219

    private Double totalSale = 0.0;
    private Double totalVoid = 0.0;
    private int countAll;
    private String HOST_CARD;
    private String MERCHANT_NUMBER;
    private String TERMINAL_ID;
    private String[] mBlockDataSend;
    private String[] mBlockDataReceive;
    private String TPDU;
    private String invoiceNumber;
    private int batchUploadSize;
    private int batchUpload;
    /**
     * SLIP SETTLEMENT
     */
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

    private ImageView bankImage;  //SINN 20181026 change image by project

    //    private SaleOfflineHealthCare saleOfflineHealthCare;  // Paul_20180725_OFF
    private TransTemp databaseTransTemp;        // Paul_20180725_OFF
    private int saleId;
    private int i;
    /**
     * Slid Sale Offline
     */
    private View hgcSaleView;
    private LinearLayout settlementHgcOffLinearLayout;
    private TextView dateHgcOffLabel;
    private TextView timeHgcOffLabel;
    private TextView midHgcOffLabel;
    private TextView tidHgcOffLabel;
    private TextView systrcOffLabel;
    private TextView traceNoOffLabel;
    private TextView typeSaleOffLabel;
    private TextView cardNoOffLabel;
    private TextView nameEngOffLabel;
    private TextView apprCodeOffLabel;
    private TextView comCodeOffLabel;
    private TextView batchHgcOffLabel;
    private TextView amountOffLabel;
    private TextView merchantName1HgcOffLabel;
    private TextView merchantName2HgcOffLabel;
    private TextView merchantName3HgcOffLabel;
    private Bitmap bitmapOld;

    private ProgressDialog dialog = null;

    // Paul_20180708 Start
    /**
     * View Slip Error
     */
    private View hgcSaleViewError;
    private LinearLayout settlementHgcErLinearLayout;
    private TextView dateHgcErLabel;
    private TextView timeHgcErLabel;
    //    private TextView midHgcErLabel;
//    private TextView tidHgcErLabel;
//    private TextView systrcErLabel;
//    private TextView traceNoErLabel;
//    private TextView typeSaleErLabel;
    private TextView cardNoErLabel;
    private TextView comCodeErLabel;
    //    private TextView batchHgcErLabel;
    private TextView merchantName1HgcErLabel;
    private TextView merchantName2HgcErLabel;
    private TextView merchantName3HgcErLabel;
    private TextView footerErrorMsg;
    private TextView errorMsg;
    String deRe39;
    // Paul_20180708 End

    /**
     * ALIPAY
     */
//    public String ALIPAY_CER_PATH = "/data/thaivan/ktb_alipay_uat.cer";
    public String ALIPAY_CER_PATH = ""; //20181115JEFF
    private String alipay_http;

    private SettlementLister settlementLister = null;
    private Context context = null;
    private CountDownTimer timer = null;
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
    //K.GAME
    private Button btn_gotoMain;//K.GAME 20181018
    CountDownTimer cdt;

    private Double Preview_totalSale = 0.0;
    private Double Preview_totalVoid = 0.0;
    private int Preview_saleCount = 0;
    private int Preview_voidCount = 0;

    private ImageView waitingImagegotomain;   //20181021 SINN settlement all
    private TextView msgLabelgotoMain;
    //END K.GAME

    //Body
    private String amt;
    private String token;
    private String deviceid;
    private String merid;
    private String storeid;
    private String record;
    private int endRecord;
    private String page = "0";
    private int endPage;
    private String startDate;
    private String endDate;

    private String invoice = "";
    private String type = "";
    private String data = "";
    private String param1 = "";
    private String param2 = "";

    private AliConfig aliConfig;

    //result
    private String ali_status = "";
    private String result = "";
    private String respcode = "";
    private String reqid = "";
    private String reqdt = "";
    private String transid = "";
    private String walletcode = "";
    private String wallettransid = "";
    private String canceldt = "";
    private String cii = "";
    private String transdt = "";
    private String foramt = "";
    private String buyerid = "";
    private String format = "";
    private String convrate = "";
    private String walletcurr = "";
    private String exchrateunit = "";
    private String fee = "";
    private String amtplusfee = "";
    private String receipttext = "";
    private String feeType = "";
    private String feeRate = "";
    private String merType = "";

    private int cntSale = 0;
    private int cntVoid = 0;
    private Double amountSale = 0.0;
    private Double amountVoid = 0.0;

    private TextView NormaldateLabel;
    private TextView NormaltimeLabel;
    private TextView NormalmidLabel;
    private TextView NormaltidLabel;
    private TextView NormalbatchLabel;
    private TextView NormalhostLabel;
    private TextView NormalmerchantName1NormalLabel;
    private TextView NormalmerchantName2NormalLabel;
    private TextView NormalmerchantName3NormalLabel;
    private TextView NormalDuplicatelLabel;

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

    private TextView NormalDuplicatelLabelfee;


    // Paul_20180706
    /**
     * Interface
     */
    private String typeInterface;
    private PosInterfaceActivity posInterfaceActivity;

    int Step = 0;       // Paul_20180709
    private CountDownTimer countDownTimerSettle;  // Paul_20180709

    private int inTriger = 0;
    private int NormalSettlementFlg = 0;       // Paul_20180713

    private int gDataBaseID = 0;

    private RecyclerView recyclerViewCardReportSummary;      // Paul_20181203
//    private LinearLayout reportCardSummaryLinearLayout = null;   // Paul_20181203
    private List<TransTemp> CardTypeDB = null;      // Paul_20181202
    private SlipSummaryReportCardAdapter slipSummaryReportCardAdapter;    // Paul_20181203 Card Type Add
    private View NormalView;
    private NestedScrollView slipNestedScrollView = null;
    private LinearLayout settlementNormalLinearLayout;
    // Paul_20181204 Can not print label1,2,3
    private TextView merchantName1NormalLabel;
    private TextView merchantName2NormalLabel;
    private TextView merchantName3NormalLabel;
    private LinearLayout NormalsummaryLinearFeeLayout;

    private String logoutInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settlement);
        NormalSettlementFlg = 0;        // Paul_20180713
        initData();
        initWidget();

        msgSetmenu = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).toString();  // Paul_20181020 POSLINK Error

        System.out.printf("utility:: Settlement 0002 typeInterface = %s \n", typeInterface);
        if (typeInterface != null) {     // Paul_20180708
            System.out.printf("utility:: Settlement 0003 typeInterface \n");
            pos_Handler.sendEmptyMessageDelayed(0, 3000); //Paul_20180708
        }

        if (logoutInterface!=null){
            btn_ok_settlement.performClick();
        }else {
            customDialogPin_new();
        }
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
        //        }
    }


    private void customDialogPin_new() { //K.GAME 180905 new dialog
        dialogPin = new Dialog(this);
        dialogPin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPin.setContentView(R.layout.dialog_custom_pin);
        dialogPin.setCancelable(false);
        dialogPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPin.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView dialogTitleLabel = dialogPin.findViewById(R.id.dialogTitleLabel);
        dialogTitleLabel.setText("สรุปยอดประจำวัน");

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
                Toast.makeText(MenuSettlementActivity.this, "PIN ไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
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
                    String keyPin = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_PIN);
                    if (s.toString().equalsIgnoreCase(keyPin)) {

                        numberPrice = "";
                        dialogPin.dismiss();
                        dialogPin = null;
                        if(typeInterface != null)
                        {

                        }
                        else {
                            dialogSettlement_new.show();
                        }


                    } else {
                        inputTextLabel.setVisibility(View.VISIBLE);
                        inputTextLabel.setText("รหัสผ่านไม่ถูกต้อง");
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
        if(dialogPin != null) {
            dialogPin.show();
        }
    }

    private void clickCal(View v) {

        if (v == oneClickFrameLayout) {
            if (numberPrice.length() < 4) //K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            if (numberPrice.length() < 4)//K.GAME 180920 ทำให้กดรหัสผ่าน void ได้แค่ ไม่เกิน 4 ตัว
                numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
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

    // Paul_20180706
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
            logoutInterface = bundle.getString("Logout");
            customDialogWaiting();
            if (!dialogWaiting.isShowing())  // Paul_20181024
                dialogWaiting.show();
        } else {
            typeInterface = null;
        }

        posInterfaceActivity = MainApplication.getPosInterfaceActivity();

        deviceid = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID);
        merid = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID);
        storeid = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_STORE_ID);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat3 = new SimpleDateFormat("D");

        aliConfig = new AliConfig();
        jsonObject = new JSONObject();
        jsonObject2 = new JSONObject();
        cryptoServices = new CryptoServices();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        realm = Realm.getDefaultInstance();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
//        menuSettleRecyclerView = findViewById(R.id.menuSettleRecyclerView);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); //K.GAME 180824 change UI
//        gridLayoutManager.setSpanCount(3);//K.GAME 180824 change UI
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);//K.GAME 180824 change UI
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        menuSettleRecyclerView.setLayoutManager(layoutManager);
//        setMenuList();
        setViewSettlementNormal();      // Paul_20181203
        customDialogWaiting();
        customDialogSettlement();
        customDialogSettlement_New(); //K.GAME 181008 New UI Settlement
        setViewSaleHGCError();      // Paul_20180708
        customDialogOutOfPaper();
        dialogSuccess_GotoMain();//K.GAME 20181018
//        reportSummaryFeeView();
        setViewSettlementHGC();
        setViewSettlementHGCOff();

        msgLabelgotoMain.setText("ทำรายการสำเร็จ");  // Paul_20181203   ////20181021 SINN settlement all
        cardManager.setSettlementHelperLister(new CardManager.SettlementHelperLister() {
            @Override
            public void onSettlementSuccess() {
/*
                if (!isSettlementAll) {
                    System.out.printf("utility:: %s  onSettlementSuccess 000000000001 \n",TAG);
                    if (dialogWaiting != null)
                        if (dialogWaiting.isShowing())       // Paul_20181024
                            dialogWaiting.dismiss();
                    Intent intent = new Intent(MenuSettlementActivity.this, SlipSettlementActivity.class);
                    intent.putExtra(KEY_TYPE_HOST, typeHost);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else {
                    System.out.printf("utility:: %s  onSettlementSuccess 000000000002 \n",TAG);
                    if(typeHost.equalsIgnoreCase( "POS" ) || typeHost.equalsIgnoreCase( "EPS" ) || typeHost.equalsIgnoreCase( "TMS" )) {
                        setViewNormalSlip();
                    }
                    else {
                        setViewSlip();
                    }
                }
*/
                deleteTc();
                System.out.printf("utility:: %s  onSettlementSuccess 000000000002 \n",TAG);
                if(typeHost.equalsIgnoreCase( "POS" ) || typeHost.equalsIgnoreCase( "EPS" ) || typeHost.equalsIgnoreCase( "TMS" )) {
                    setViewNormalSlip();
                }
                else {
                    setViewSlip();
                }

            }

            @Override
            public void onCloseSettlementFail() {

                Log.d(TAG, "onCloseSettlementFail()");

                //     RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("statusSuccess", "1").findAll();
                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("statusSuccess", "1").findAll(); // Paul_20181028
                final Integer inChk;
                inChk = selectSettlementQRCHK();

//                Log.d(TAG,"selectSettlementQRCHK()="+String.valueOf(inChk));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        okBtn.setVisibility(View.GONE);
                        Utility.customDialogAlert(MenuSettlementActivity.this, "ทำรายการไม่สำเร็จ 95", new Utility.OnClickCloseImage() {
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
//        midFeeLabel = qrView.findViewById(R.id.midLabel);
//        tidFeeLabel = qrView.findViewById(R.id.tidTaxLabel);
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

    private void customDialogSettlement_New() {

        hostflag = " ";   //TMS|POS|EPS|GHC| QR|ALI

        dialogSettlement_new = new Dialog(MenuSettlementActivity.this);
        dialogSettlement_new.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSettlement_new.setContentView(R.layout.dialog_custom_settlement_new);
        dialogSettlement_new.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSettlement_new.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        checkbox_Settlement_All = dialogSettlement_new.findViewById(R.id.checkbox_Settlement_All);
        checkbox_KTB_ON_US = dialogSettlement_new.findViewById(R.id.checkbox_KTB_ON_US);
        checkbox_BASE24_POS = dialogSettlement_new.findViewById(R.id.checkbox_BASE24_POS);
        checkbox_BASE24_EPS = dialogSettlement_new.findViewById(R.id.checkbox_BASE24_EPS);
        checkbox_Health_Care = dialogSettlement_new.findViewById(R.id.checkbox_Health_Care);
        checkbox_Qr = dialogSettlement_new.findViewById(R.id.checkbox_Qr);
        checkbox_Ali = dialogSettlement_new.findViewById(R.id.checkbox_Ali);
        checkbox_Wec = dialogSettlement_new.findViewById(R.id.checkbox_Wec);

        linear_checkbox_KTB_ON_US = dialogSettlement_new.findViewById(R.id.linear_checkbox_KTB_ON_US);
        linear_checkbox_BASE24_POS = dialogSettlement_new.findViewById(R.id.linear_checkbox_BASE24_POS);
        linear_checkbox_BASE24_EPS = dialogSettlement_new.findViewById(R.id.linear_checkbox_BASE24_EPS);
        linear_checkbox_Health_Care = dialogSettlement_new.findViewById(R.id.linear_checkbox_Health_Care);
        linear_checkbox_Qr = dialogSettlement_new.findViewById(R.id.linear_checkbox_Qr);
        linear_checkbox_Ali = dialogSettlement_new.findViewById(R.id.linear_checkbox_Ali);
        linear_checkbox_Wec = dialogSettlement_new.findViewById(R.id.linear_checkbox_Wec);

        tv_KTB_ON_US = dialogSettlement_new.findViewById(R.id.tv_KTB_ON_US);
        tv_BASE24_POS = dialogSettlement_new.findViewById(R.id.tv_BASE24_POS);
        tv_BASE24_EPS = dialogSettlement_new.findViewById(R.id.tv_BASE24_EPS);
        tv_HC = dialogSettlement_new.findViewById(R.id.tv_HC);
        tv_qr = dialogSettlement_new.findViewById(R.id.tv_qr);
        tv_ali = dialogSettlement_new.findViewById(R.id.tv_ali);
        tv_wec = dialogSettlement_new.findViewById(R.id.tv_wec);

        linear_KTB_ON_US = dialogSettlement_new.findViewById(R.id.linear_KTB_ON_US);
        linear_BASE24_POS = dialogSettlement_new.findViewById(R.id.linear_BASE24_POS);
        linear_BASE24_EPS = dialogSettlement_new.findViewById(R.id.linear_BASE24_EPS);
        linear_HC = dialogSettlement_new.findViewById(R.id.linear_HC);
        linear_qr = dialogSettlement_new.findViewById(R.id.linear_qr);
        linear_ali = dialogSettlement_new.findViewById(R.id.linear_ali);
        linear_wec = dialogSettlement_new.findViewById(R.id.linear_wec);

        btn_transfer_ON_US = dialogSettlement_new.findViewById(R.id.btn_transfer_ON_US);
        btn_transfer_pos = dialogSettlement_new.findViewById(R.id.btn_transfer_pos);
        btn_transfer_EPS = dialogSettlement_new.findViewById(R.id.btn_transfer_EPS);
        btn_transfer_HC = dialogSettlement_new.findViewById(R.id.btn_transfer_HC);
        btn_transfer_qr = dialogSettlement_new.findViewById(R.id.btn_transfer_qr);
        btn_transfer_ali = dialogSettlement_new.findViewById(R.id.btn_transfer_ali);
        btn_transfer_wec = dialogSettlement_new.findViewById(R.id.btn_transfer_wec);
        btn_ok_settlement = dialogSettlement_new.findViewById(R.id.btn_ok_settlement);

        //-------------------------------- SHOW ข้อมูล ด้านใน ---------------------------------------------
        TextView tv_Accquirer_Name_on_us = dialogSettlement_new.findViewById(R.id.tv_Accquirer_Name_on_us);
        TextView tv_Merchant_Name_on_us = dialogSettlement_new.findViewById(R.id.tv_Merchant_Name_on_us);
        TextView tv_Merchant_No_on_us = dialogSettlement_new.findViewById(R.id.tv_Merchant_No_on_us);
        TextView tv_Terminal_No_on_us = dialogSettlement_new.findViewById(R.id.tv_Terminal_No_on_us);
        TextView tv_Batch_No_on_us = dialogSettlement_new.findViewById(R.id.tv_Batch_No_on_us);

        TextView tv_Accquirer_Name_pos = dialogSettlement_new.findViewById(R.id.tv_Accquirer_Name_POS);
        TextView tv_Merchant_Name_pos = dialogSettlement_new.findViewById(R.id.tv_Merchant_Name_POS);
        TextView tv_Merchant_No_pos = dialogSettlement_new.findViewById(R.id.tv_Merchant_No_POS);
        TextView tv_Terminal_No_pos = dialogSettlement_new.findViewById(R.id.tv_Terminal_No_POS);
        TextView tv_Batch_No_pos = dialogSettlement_new.findViewById(R.id.tv_Batch_No_POS);

        TextView tv_Accquirer_Name_eps = dialogSettlement_new.findViewById(R.id.tv_Accquirer_Name_EPS);
        TextView tv_Merchant_Name_eps = dialogSettlement_new.findViewById(R.id.tv_Merchant_Name_EPS);
        TextView tv_Merchant_No_eps = dialogSettlement_new.findViewById(R.id.tv_Merchant_No_EPS);
        TextView tv_Terminal_No_eps = dialogSettlement_new.findViewById(R.id.tv_Terminal_No_EPS);
        TextView tv_Batch_No_eps = dialogSettlement_new.findViewById(R.id.tv_Batch_No_EPS);

        TextView tv_Accquirer_Name_HC = dialogSettlement_new.findViewById(R.id.tv_Accquirer_Name_HC);
        TextView tv_Merchant_Name_HC = dialogSettlement_new.findViewById(R.id.tv_Merchant_Name_HC);
        TextView tv_Merchant_No_HC = dialogSettlement_new.findViewById(R.id.tv_Merchant_No_HC);
        TextView tv_Terminal_No_HC = dialogSettlement_new.findViewById(R.id.tv_Terminal_No_HC);
        TextView tv_Batch_No_HC = dialogSettlement_new.findViewById(R.id.tv_Batch_No_HC);

        TextView tv_Accquirer_Name_QR = dialogSettlement_new.findViewById(R.id.tv_Accquirer_Name_QR);
        TextView tv_Merchant_Name_QR = dialogSettlement_new.findViewById(R.id.tv_Merchant_Name_QR);
        TextView tv_Merchant_No_QR = dialogSettlement_new.findViewById(R.id.tv_Merchant_No_QR);
        TextView tv_Terminal_No_QR = dialogSettlement_new.findViewById(R.id.tv_Terminal_No_QR);
        TextView tv_Batch_No_QR = dialogSettlement_new.findViewById(R.id.tv_Batch_No_QR);

        TextView tv_Accquirer_Name_ALI = dialogSettlement_new.findViewById(R.id.tv_Accquirer_Name_ALI);
        TextView tv_Merchant_Name_ALI = dialogSettlement_new.findViewById(R.id.tv_Merchant_Name_ALI);
        TextView tv_Merchant_No_ALI = dialogSettlement_new.findViewById(R.id.tv_Merchant_No_ALI);
        TextView tv_Terminal_No_ALI = dialogSettlement_new.findViewById(R.id.tv_Terminal_No_ALI);
        TextView tv_Batch_No_ALI = dialogSettlement_new.findViewById(R.id.tv_Batch_No_ALI);

        TextView tv_Accquirer_Name_WEC = dialogSettlement_new.findViewById(R.id.tv_Accquirer_Name_WEC);
        TextView tv_Merchant_Name_WEC = dialogSettlement_new.findViewById(R.id.tv_Merchant_Name_WEC);
        TextView tv_Merchant_No_WEC = dialogSettlement_new.findViewById(R.id.tv_Merchant_No_WEC);
        TextView tv_Terminal_No_WEC = dialogSettlement_new.findViewById(R.id.tv_Terminal_No_WEC);
        TextView tv_Batch_No_WEC = dialogSettlement_new.findViewById(R.id.tv_Batch_No_WEC);

        /////////////findView ด้านล่าง
        final TextView tv_sale_count_ONUS = dialogSettlement_new.findViewById(R.id.tv_sale_count_ONUS);
        final TextView tv_sale_count_POS = dialogSettlement_new.findViewById(R.id.tv_sale_count_POS);
        final TextView tv_sale_count_EPS = dialogSettlement_new.findViewById(R.id.tv_sale_count_EPS);
        final TextView tv_sale_count_HC = dialogSettlement_new.findViewById(R.id.tv_sale_count_HC);
        final TextView tv_sale_count_QR = dialogSettlement_new.findViewById(R.id.tv_sale_count_QR);
        final TextView tv_sale_count_ALI = dialogSettlement_new.findViewById(R.id.tv_sale_count_ALI);
        final TextView tv_sale_count_WEC = dialogSettlement_new.findViewById(R.id.tv_sale_count_WEC);

        final TextView tv_sale_total_ONUS = dialogSettlement_new.findViewById(R.id.tv_sale_total_ONUS);
        final TextView tv_sale_total_POS = dialogSettlement_new.findViewById(R.id.tv_sale_total_POS);
        final TextView tv_sale_total_EPS = dialogSettlement_new.findViewById(R.id.tv_sale_total_EPS);
        final TextView tv_sale_total_HC = dialogSettlement_new.findViewById(R.id.tv_sale_total_HC);
        final TextView tv_sale_total_QR = dialogSettlement_new.findViewById(R.id.tv_sale_total_QR);
        final TextView tv_sale_total_ALI = dialogSettlement_new.findViewById(R.id.tv_sale_total_ALI);
        final TextView tv_sale_total_WEC = dialogSettlement_new.findViewById(R.id.tv_sale_total_WEC);

        final TextView tv_void_count_ONUS = dialogSettlement_new.findViewById(R.id.tv_void_count_ONUS);
        final TextView tv_void_count_POS = dialogSettlement_new.findViewById(R.id.tv_void_count_POS);
        final TextView tv_void_count_EPS = dialogSettlement_new.findViewById(R.id.tv_void_count_EPS);
        final TextView tv_void_count_HC = dialogSettlement_new.findViewById(R.id.tv_void_count_HC);
        final TextView tv_void_count_QR = dialogSettlement_new.findViewById(R.id.tv_void_count_QR);
        final TextView tv_void_count_ALI = dialogSettlement_new.findViewById(R.id.tv_void_count_ALI);
        final TextView tv_void_count_WEC = dialogSettlement_new.findViewById(R.id.tv_void_count_WEC);

        final TextView tv_void_total_ONUS = dialogSettlement_new.findViewById(R.id.tv_void_total_ONUS);
        final TextView tv_void_total_POS = dialogSettlement_new.findViewById(R.id.tv_void_total_POS);
        final TextView tv_void_total_EPS = dialogSettlement_new.findViewById(R.id.tv_void_total_EPS);
        final TextView tv_void_total_HC = dialogSettlement_new.findViewById(R.id.tv_void_total_HC);
        final TextView tv_void_total_QR = dialogSettlement_new.findViewById(R.id.tv_void_total_QR);
        final TextView tv_void_total_ALI = dialogSettlement_new.findViewById(R.id.tv_void_total_ALI);
        final TextView tv_void_total_WEC = dialogSettlement_new.findViewById(R.id.tv_void_total_WEC);

        final TextView tv_totals_count_ONUS = dialogSettlement_new.findViewById(R.id.tv_totals_count_ONUS);
        final TextView tv_totals_count_POS = dialogSettlement_new.findViewById(R.id.tv_totals_count_POS);
        final TextView tv_totals_count_EPS = dialogSettlement_new.findViewById(R.id.tv_totals_count_EPS);
        final TextView tv_totals_count_HC = dialogSettlement_new.findViewById(R.id.tv_totals_count_HC);
        final TextView tv_totals_count_QR = dialogSettlement_new.findViewById(R.id.tv_totals_count_QR);
        final TextView tv_totals_count_ALI = dialogSettlement_new.findViewById(R.id.tv_totals_count_ALI);
        final TextView tv_totals_count_WEC = dialogSettlement_new.findViewById(R.id.tv_totals_count_WEC);

        final TextView tv_totals_total_ONUS = dialogSettlement_new.findViewById(R.id.tv_totals_total_ONUS);
        final TextView tv_totals_total_POS = dialogSettlement_new.findViewById(R.id.tv_totals_total_POS);
        final TextView tv_totals_total_EPS = dialogSettlement_new.findViewById(R.id.tv_totals_total_EPS);
        final TextView tv_totals_total_HC = dialogSettlement_new.findViewById(R.id.tv_totals_total_HC);
        final TextView tv_totals_total_QR = dialogSettlement_new.findViewById(R.id.tv_totals_total_QR);
        final TextView tv_totals_total_ALI = dialogSettlement_new.findViewById(R.id.tv_totals_total_ALI);
        final TextView tv_totals_total_WEC = dialogSettlement_new.findViewById(R.id.tv_totals_total_WEC);

        /////////////END findView ด้านล่าง
        tv_Merchant_Name_on_us.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        tv_Merchant_Name_pos.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        tv_Merchant_Name_eps.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        tv_Merchant_Name_HC.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
        tv_Merchant_Name_QR.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME));
        tv_Merchant_Name_ALI.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME));
        tv_Merchant_Name_WEC.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME));

        tv_Merchant_No_on_us.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
        tv_Merchant_No_pos.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS));
        tv_Merchant_No_eps.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
        tv_Merchant_No_HC.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        tv_Merchant_No_QR.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_ID));
        tv_Merchant_No_ALI.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
        tv_Merchant_No_WEC.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));

        tv_Terminal_No_on_us.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
        tv_Terminal_No_pos.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS));
        tv_Terminal_No_eps.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
        tv_Terminal_No_HC.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        tv_Terminal_No_QR.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID));
        tv_Terminal_No_ALI.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
        tv_Terminal_No_WEC.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));

        tv_Batch_No_on_us.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS));
        tv_Batch_No_pos.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
        tv_Batch_No_eps.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
        tv_Batch_No_HC.setText(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC));
        tv_Batch_No_QR.setText(Preference.getInstance(this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
        tv_Batch_No_ALI.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
        tv_Batch_No_WEC.setText(Preference.getInstance(this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
        //---settext ด้านล่าง--//
        final DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

        final DecimalFormat decimalFormatPreview = new DecimalFormat("##,###,##0.00");

        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll();

        RealmResults<QrCode> aliTemp_sale = realm.where(QrCode.class).equalTo("hostTypeCard", "ALIPAY").equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
        RealmResults<QrCode> aliTemp_void =  realm.where(QrCode.class).equalTo("hostTypeCard", "ALIPAY").equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
        RealmResults<QrCode> wecTemp_sale =  realm.where(QrCode.class).equalTo("hostTypeCard", "WECHAT").equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
        RealmResults<QrCode> wecTemp_void =  realm.where(QrCode.class).equalTo("hostTypeCard", "WECHAT").equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();

        Double amountSale = 0.0;
        Double amountVoid = 0.0;
        Double amountSale_ali = 0.0;
        Double amountVoid_ali = 0.0;
        Double amountSale_wec = 0.0;
        Double amountVoid_wec = 0.0;

        tv_sale_count_ONUS.setText(transTemp.size() + "");
        tv_sale_count_POS.setText(transTemp.size() + "");
        tv_sale_count_EPS.setText(transTemp.size() + "");
        tv_sale_count_HC.setText(transTemp.size() + "");
        tv_sale_count_QR.setText(transTemp.size() + "");
        tv_sale_count_ALI.setText(aliTemp_sale.size() + "");
        tv_sale_count_WEC.setText(wecTemp_sale.size() + "");

        tv_sale_total_ONUS.setText(decimalFormat.format(amountSale));
        tv_sale_total_POS.setText(decimalFormat.format(amountSale));
        tv_sale_total_EPS.setText(decimalFormat.format(amountSale));
        tv_sale_total_HC.setText(decimalFormat.format(amountSale));
        tv_sale_total_QR.setText(decimalFormat.format(amountSale));

        for(int i = 0; i < aliTemp_sale.size(); i ++){
            amountSale_ali = Double.valueOf(aliTemp_sale.get(i).getAmt());
        }
        tv_sale_total_ALI.setText(decimalFormat.format(amountSale_ali));

        for(int i = 0; i < wecTemp_sale.size(); i ++){
            amountSale_wec = Double.valueOf(wecTemp_sale.get(i).getAmt());
        }
        tv_sale_total_WEC.setText(decimalFormat.format(amountSale_wec));

        tv_void_count_ONUS.setText(decimalFormat.format(amountVoid));
        tv_void_count_POS.setText(decimalFormat.format(amountVoid));
        tv_void_count_EPS.setText(decimalFormat.format(amountVoid));
        tv_void_count_HC.setText(decimalFormat.format(amountVoid));
        tv_void_count_QR.setText(decimalFormat.format(amountVoid));
        tv_void_count_ALI.setText(aliTemp_void.size() + "");
        tv_void_count_WEC.setText(wecTemp_void.size() + "");

        for(int i = 0; i < aliTemp_void.size(); i ++){
            amountVoid_ali = Double.valueOf(aliTemp_void.get(i).getAmt());
        }
        tv_void_total_ALI.setText(decimalFormat.format(amountVoid_ali));

        for(int i = 0; i < wecTemp_void.size(); i ++){
            amountVoid_wec = Double.valueOf(wecTemp_void.get(i).getAmt());
        }
        tv_void_total_WEC.setText(decimalFormat.format(amountVoid_wec));

        tv_totals_count_ALI.setText((aliTemp_sale.size() + aliTemp_void.size()) + "");
        tv_totals_count_WEC.setText((wecTemp_sale.size() + wecTemp_void.size()) + "");

        tv_totals_total_ALI.setText(decimalFormat.format(amountSale_ali));
        tv_totals_total_WEC.setText(decimalFormat.format(amountSale_wec));

        String std_flag = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE);
        String std_ali = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_ID);
        String std_wec = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WECHATPAY_ID);

        if (std_flag.substring(0, 1).equals("1")) {
            linear_checkbox_KTB_ON_US.setVisibility(View.VISIBLE);
            linear_checkbox_BASE24_POS.setVisibility(View.VISIBLE);
            linear_checkbox_BASE24_EPS.setVisibility(View.VISIBLE);
        }
        if (std_flag.substring(1, 2).equals("1")) {
            linear_checkbox_Qr.setVisibility(View.VISIBLE);
        }
        if (std_flag.substring(2, 3).equals("1")) {
            linear_checkbox_Health_Care.setVisibility(View.VISIBLE);
        }
        if (std_ali.equals("1")) {
            linear_checkbox_Ali.setVisibility(View.VISIBLE);
        }

        if (std_wec.equals("1")) {
            linear_checkbox_Wec.setVisibility(View.VISIBLE);
        }

        if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)
            linear_checkbox_KTB_ON_US.setVisibility(View.GONE);
        if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
            linear_checkbox_BASE24_POS.setVisibility(View.GONE);
        if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length()<8)
            linear_checkbox_BASE24_EPS.setVisibility(View.GONE);

        dialogSettlement_new.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                finish();
                if (dialogSettlement_new != null)
                    dialogSettlement_new.dismiss();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        btn_ok_settlement.setOnClickListener(new View.OnClickListener() { // OK Button
            @Override
            public void onClick(View v) {
                hostflag = "111222333444555666777";   //TMS|POS|EPS|GHC| QR|ALI
                //Put work
                //GHC|POS|EPS|TMS|QR   //11111

                if (checkbox_KTB_ON_US.isChecked() && (linear_checkbox_KTB_ON_US.getVisibility() == View.VISIBLE)) {
                    Log.d(TAG, "1919_checkbox_ON_US");
//                    settlementPosition = 4;
//                    selectDataTransTempAllGAME("TMS");
//                    hostflag
                    hostflag = hostflag.replaceAll("111", "TMS");//TMS|POS|EPS|GHC| QR|ALI
                }

                if (checkbox_BASE24_POS.isChecked() && (linear_checkbox_BASE24_POS.getVisibility() == View.VISIBLE)) {
                    Log.d(TAG, "1919_checkbox_BASE24_POS");
//                    settlementPosition = 2;
//                    selectDataTransTempAllGAME("POS");
                    hostflag = hostflag.replaceAll("222", "POS");//TMS|POS|EPS|GHC| QR|ALI
                }
                if (checkbox_BASE24_EPS.isChecked() && (linear_checkbox_BASE24_EPS.getVisibility() == View.VISIBLE)) {
                    Log.d(TAG, "1919_checkbox_BASE24_EPS");
//                    settlementPosition = 3;
//                    selectDataTransTempAllGAME("EPS");
                    hostflag = hostflag.replaceAll("333", "EPS");//TMS|POS|EPS|GHC| QR|ALI
                }
                if (checkbox_Health_Care.isChecked() && (linear_checkbox_Health_Care.getVisibility() == View.VISIBLE)) {
                    Log.d(TAG, "1919_checkbox_Health_Care");
//                    settlementPosition = 1;
//                    selectDataTransTempAllGAME("GHC");
                    hostflag = hostflag.replaceAll("444", "GHC");//TMS|POS|EPS|GHC| QR|ALI
                }
                if (checkbox_Qr.isChecked() && (linear_checkbox_Qr.getVisibility() == View.VISIBLE)) {
                    Log.d(TAG, "1919_checkbox_Qr");
//                    selectSettlementQRAll();
                    hostflag = hostflag.replaceAll("555", " QR");//TMS|POS|EPS|GHC| QR|ALI
                }

                if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equals("1")) {
                    if (checkbox_Ali.isChecked() && (linear_checkbox_Ali.getVisibility() == View.VISIBLE)) {
                        hostflag = hostflag.replaceAll("666", "ALI");  //TMS|POS|EPS|GHC| QR|ALI
                    }
                }

                if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equals("1")) {
                    if (checkbox_Wec.isChecked() && (linear_checkbox_Wec.getVisibility() == View.VISIBLE)) {
                        hostflag = hostflag.replaceAll("777", "WEC");  //TMS|POS|EPS|GHC| QR|ALI
                    }
                }

                Log.d("1919_hostflag", hostflag);

                if (hostflag.equalsIgnoreCase("111222333444555666777")) {
                    statusLabel.setText("");
                    new CountDownTimer(1500, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
//                            if (dialogSettlement_new != null)
//                                dialogSettlement_new.dismiss();

                            Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ท่านไม่ได้เลือกรายการ");  // Paul_20181020
                        }

                        @Override
                        public void onFinish() {
                            Utility.customDialogAlertAutoClear();
//                            dialogSettlement_new.show();
                        }
                    }.start();
                }
                else{
                    if (dialogSettlement != null)
                        dialogSettlement.dismiss();

                    if (logoutInterface!=null){
                        statusLabel.setText("SETTLEMENT");
                    }else {
                        statusLabel.setText("โอนยอด");
                    }


                    dialogSettlement.show();

                    settlementPosition = 0;
                    isSettlementAll = true;
                    selectDataTransTempAllGAME("");
                }


//                if(checkbox_Settlement_All.isChecked()){
//                    statusLabel.setText("โอนยอด");
//                    dialogSettlement.show();
//
//                    settlementPosition = 0;
//                    isSettlementAll = true;
//                    selectDataTransTempAllGAME("");
//                }else if(checkbox_KTB_ON_US.isChecked() || checkbox_BASE24_POS.isChecked() || checkbox_BASE24_EPS.isChecked() ||
//                        checkbox_Health_Care.isChecked() || checkbox_Qr.isChecked() || checkbox_Ali.isChecked()){
//                    statusLabel.setText("โอนยอด");
//                    dialogSettlement.show();
//
//                    settlementPosition = 0;
//                    isSettlementAll = false;
//                    selectDataTransTempAllGAME("");
//                }else{
//                    Toast.makeText(MenuSettlementActivity.this, " Please check Settlement type", Toast.LENGTH_SHORT).show();
//                }


            }
        });

        if (!hostflag.equalsIgnoreCase("111222333444555666777")) {


            btn_transfer_ON_US.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Put work
//                Toast.makeText(MenuSettlementActivity.this, "ON US", Toast.LENGTH_SHORT).show();
//                selectDataTransTempAll("TMS");
                    hostflag = "TMS222333444555666777";         // TMSPOSEPSGHC QRALI
                    selectDataTransTempAllGAME(" ");
                }
            });
            btn_transfer_pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Put work
//                Toast.makeText(MenuSettlementActivity.this, "POS", Toast.LENGTH_SHORT).show();
                    hostflag = "111POS333444555666777";         // TMSPOSEPSGHC QRALI
                    selectDataTransTempAllGAME(" ");
//                selectDataTransTempAll("POS");
                }
            });
            btn_transfer_EPS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Put work
//                Toast.makeText(MenuSettlementActivity.this, "EPS", Toast.LENGTH_SHORT).show();
//                selectDataTransTempAll("EPS");
                    hostflag = "111222EPS444555666777";         // TMSPOSEPSGHC QRALI
                    selectDataTransTempAllGAME(" ");
                }
            });
            btn_transfer_HC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Put work
//                Toast.makeText(MenuSettlementActivity.this, "Health Care", Toast.LENGTH_SHORT).show();
                    hostflag = "111222333GHC555666777";         // TMSPOSEPSGHC QRALI
                    selectDataTransTempAllGAME(" ");
//                selectDataTransTempAll("GHC");
                }
            });
            btn_transfer_qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Put work
//                Toast.makeText(MenuSettlementActivity.this, "Qr", Toast.LENGTH_SHORT).show();
                    hostflag = "111222333444 QR666777";         // TMSPOSEPSGHC QRALI
                    selectDataTransTempAllGAME(" ");
                }
            });
            btn_transfer_ali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Put work
//                    Toast.makeText(MenuSettlementActivity.this, "Alipay", Toast.LENGTH_SHORT).show();
                    hostflag = "111222333444555ALI777";         // TMSPOSEPSGHC QRALI
                    selectDataTransTempAllGAME(" ");
            }
            });
            btn_transfer_wec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Put work
//                    Toast.makeText(MenuSettlementActivity.this, "Wechat", Toast.LENGTH_SHORT).show();
                    hostflag = "111222333444555666WEC";         // TMSPOSEPSGHC QRALI
                    selectDataTransTempAllGAME(" ");
            }
            });

            tv_KTB_ON_US.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectSummaryReport("TMS");
                    tv_sale_count_ONUS.setText(String.valueOf(Preview_saleCount));
                    tv_void_count_ONUS.setText(String.valueOf(Preview_voidCount));
                    tv_sale_total_ONUS.setText(decimalFormatPreview.format(Preview_totalSale));
                    tv_void_total_ONUS.setText(decimalFormatPreview.format(Preview_totalVoid));
                    tv_totals_count_ONUS.setText(String.valueOf(Preview_saleCount + Preview_voidCount));
                    tv_totals_total_ONUS.setText(decimalFormatPreview.format(Preview_totalSale));

                    if (linear_KTB_ON_US.getVisibility() == View.GONE) {
                        linear_KTB_ON_US.setVisibility(View.VISIBLE);
                    } else {
                        linear_KTB_ON_US.setVisibility(View.GONE);
                    }
                }
            });
            tv_BASE24_POS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectSummaryReport("POS");
                    tv_sale_count_POS.setText(String.valueOf(Preview_saleCount));
                    tv_void_count_POS.setText(String.valueOf(Preview_voidCount));
                    tv_sale_total_POS.setText(decimalFormatPreview.format(Preview_totalSale));
                    tv_void_total_POS.setText(decimalFormatPreview.format(Preview_totalVoid));
                    tv_totals_count_POS.setText(String.valueOf(Preview_saleCount + Preview_voidCount));
                    tv_totals_total_POS.setText(decimalFormatPreview.format(Preview_totalSale));


                    if (linear_BASE24_POS.getVisibility() == View.GONE) {
                        linear_BASE24_POS.setVisibility(View.VISIBLE);
                    } else {
                        linear_BASE24_POS.setVisibility(View.GONE);
                    }
                }
            });
            tv_BASE24_EPS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectSummaryReport("EPS");
                    tv_sale_count_EPS.setText(String.valueOf(Preview_saleCount));
                    tv_void_count_EPS.setText(String.valueOf(Preview_voidCount));
                    tv_sale_total_EPS.setText(decimalFormatPreview.format(Preview_totalSale));
                    tv_void_total_EPS.setText(decimalFormatPreview.format(Preview_totalVoid));
                    tv_totals_count_EPS.setText(String.valueOf(Preview_saleCount + Preview_voidCount));
                    tv_totals_total_EPS.setText(decimalFormatPreview.format(Preview_totalSale));


                    if (linear_BASE24_EPS.getVisibility() == View.GONE) {
                        linear_BASE24_EPS.setVisibility(View.VISIBLE);
                    } else {
                        linear_BASE24_EPS.setVisibility(View.GONE);
                    }
                }
            });
            tv_HC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectSummaryReport("GHC");
                    tv_sale_count_HC.setText(String.valueOf(Preview_saleCount));
                    tv_void_count_HC.setText(String.valueOf(Preview_voidCount));
                    tv_sale_total_HC.setText(decimalFormatPreview.format(Preview_totalSale));
                    tv_void_total_HC.setText(decimalFormatPreview.format(Preview_totalVoid));
                    tv_totals_count_HC.setText(String.valueOf(Preview_saleCount + Preview_voidCount));
                    tv_totals_total_HC.setText(decimalFormatPreview.format(Preview_totalSale));


                    if (linear_HC.getVisibility() == View.GONE) {
                        linear_HC.setVisibility(View.VISIBLE);
                    } else {
                        linear_HC.setVisibility(View.GONE);
                    }
                }
            });
            tv_qr.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    selectSummaryQrReport("QR");
                    tv_sale_count_QR.setText(String.valueOf(Preview_saleCount));
                    tv_void_count_QR.setText(String.valueOf(Preview_voidCount));
                    tv_sale_total_QR.setText(decimalFormatPreview.format(Preview_totalSale));
                    tv_void_total_QR.setText(decimalFormatPreview.format(Preview_totalVoid));
                    tv_totals_count_QR.setText(String.valueOf(Preview_saleCount + Preview_voidCount));
                    tv_totals_total_QR.setText(decimalFormatPreview.format(Preview_totalSale));
                    if (linear_qr.getVisibility() == View.GONE) {
                        linear_qr.setVisibility(View.VISIBLE);
                    } else {
                        linear_qr.setVisibility(View.GONE);
                    }
                }
            });
            // Paul_20181022 ALIPAY
            tv_ali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectSummaryQrReport("ALIPAY");
                    tv_sale_count_ALI.setText(String.valueOf(Preview_saleCount));
                    tv_void_count_ALI.setText(String.valueOf(Preview_voidCount));
                    tv_sale_total_ALI.setText(decimalFormatPreview.format(Preview_totalSale));
                    tv_void_total_ALI.setText(decimalFormatPreview.format(Preview_totalVoid));
                    tv_totals_count_ALI.setText(String.valueOf(Preview_saleCount + Preview_voidCount));
                    tv_totals_total_ALI.setText(decimalFormatPreview.format(Preview_totalSale));

                    if (linear_ali.getVisibility() == View.GONE) {
                        linear_ali.setVisibility(View.VISIBLE);
                    } else {
                        linear_ali.setVisibility(View.GONE);
                    }
                }
            });
            // Paul_20181023 WECHAT
            tv_wec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectSummaryQrReport("WECHAT");
                    tv_sale_count_WEC.setText(String.valueOf(Preview_saleCount));
                    tv_void_count_WEC.setText(String.valueOf(Preview_voidCount));
                    tv_sale_total_WEC.setText(decimalFormatPreview.format(Preview_totalSale));
                    tv_void_total_WEC.setText(decimalFormatPreview.format(Preview_totalVoid));
                    tv_totals_count_WEC.setText(String.valueOf(Preview_saleCount + Preview_voidCount));
                    tv_totals_total_WEC.setText(decimalFormatPreview.format(Preview_totalSale));

                    if (linear_wec.getVisibility() == View.GONE) {
                        linear_wec.setVisibility(View.VISIBLE);
                    } else {
                        linear_wec.setVisibility(View.GONE);
                    }
                }
            });

        }

    }

    public void CheckboxAll() {//K.GAME Create for checkbox All
        String checkAll = "1";
        if (linear_checkbox_KTB_ON_US.getVisibility() == View.VISIBLE) {
            if (checkbox_KTB_ON_US.isChecked()) {
                checkAll = checkAll + "1";
            } else {
                checkAll = checkAll + "0";
            }
        }
        if (linear_checkbox_BASE24_POS.getVisibility() == View.VISIBLE) {
            if (checkbox_BASE24_POS.isChecked()) {
                checkAll = checkAll + "1";
            } else {
                checkAll = checkAll + "0";
            }
        }
        if (linear_checkbox_BASE24_EPS.getVisibility() == View.VISIBLE) {
            if (checkbox_BASE24_EPS.isChecked()) {
                checkAll = checkAll + "1";
            } else {
                checkAll = checkAll + "0";
            }
        }
        if (linear_checkbox_Health_Care.getVisibility() == View.VISIBLE) {
            if (checkbox_Health_Care.isChecked()) {
                checkAll = checkAll + "1";
            } else {
                checkAll = checkAll + "0";
            }
        }
        if (linear_checkbox_Qr.getVisibility() == View.VISIBLE) {
            if (checkbox_Qr.isChecked()) {
                checkAll = checkAll + "1";
            } else {
                checkAll = checkAll + "0";
            }
        }
        if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
            if (linear_checkbox_Ali.getVisibility() == View.VISIBLE) {
                if (checkbox_Ali.isChecked()) {
                    checkAll = checkAll + "1";
                } else {
                    checkAll = checkAll + "0";
                }
            }
        }
        if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
            if (linear_checkbox_Wec.getVisibility() == View.VISIBLE) {
                if (checkbox_Wec.isChecked()) {
                    checkAll = checkAll + "1";
                } else {
                    checkAll = checkAll + "0";
                }
            }
        }
        if (checkAll.contains("0")) {
        } else {
            checkbox_Settlement_All.setChecked(true);
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_Settlement_All:
                if (checked) {
                    // Put some meat on the sandwich
                    checkbox_KTB_ON_US.setChecked(true);
                    checkbox_BASE24_POS.setChecked(true);
                    checkbox_BASE24_EPS.setChecked(true);
                    checkbox_Health_Care.setChecked(true);
                    checkbox_Qr.setChecked(true);
                    if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
                        checkbox_Ali.setChecked(true);    // Paul_20181022 ALIPAY
                    }
                    if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
                        checkbox_Wec.setChecked(true);  // Paul_20181022 WECHAT
                    }
                } else {
                    // Remove the meat
                    checkbox_KTB_ON_US.setChecked(false);
                    checkbox_BASE24_POS.setChecked(false);
                    checkbox_BASE24_EPS.setChecked(false);
                    checkbox_Health_Care.setChecked(false);
                    checkbox_Qr.setChecked(false);
                    if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
                        checkbox_Ali.setChecked(false);    // Paul_20181022 ALIPAY
                    }
                    if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
                        checkbox_Wec.setChecked(false);  // Paul_20181022 WECHAT
                    }
                }
                break;
            case R.id.checkbox_KTB_ON_US:
            case R.id.checkbox_BASE24_POS:
            case R.id.checkbox_BASE24_EPS:
            case R.id.checkbox_Health_Care:
            case R.id.checkbox_Qr:
            case R.id.checkbox_Ali:
            case R.id.checkbox_Wec:
                if (checked) {
                    CheckboxAll();
                } else {
                    checkbox_Settlement_All.setChecked(false);
                }
                break;
        }
    }

    private void setViewSlip() {
        Log.d(TAG, "setViewSlip()");
        msgLabelgotoMain.setText("ทำรายการสำเร็จ");  ////20181021 SINN settlement all

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
                        amountVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",", ""));  // Paul_20190128
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
                    saleCountLabel.setText(transTemp.size() + "");
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
                        hostLabel.setText("WAY4");
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
                        hostLabel.setText("KTB OFFUS");     // Paul_20181028 Sinn merge version UAT6_0016
                        int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
                        batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
//                        batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS),6));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_POS, dateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_POS, timeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS, saleTotalLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_POS, saleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_POS, voidSaleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS, voidSaleAmountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_POS, CardPrefix.calLen(String.valueOf(batch), 6));
                    } else if (typeHost.equalsIgnoreCase("EPS")) {
                        hostLabel.setText("WAY4");
                        int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
                        batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
//                        batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS),6));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_EPS, dateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_EPS, timeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS, saleTotalLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS, saleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS, voidSaleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS, voidSaleAmountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_EPS, CardPrefix.calLen(String.valueOf(batch), 6));


////20180720 SINN last reprint settlement GHC
                    } else if (typeHost.equalsIgnoreCase("GHC")) {    //save for last settlement is not pass heres
//                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
//                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_GHC, dateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_GHC, timeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_GHC, saleTotalLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_GHC, saleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_GHC, voidSaleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_GHC, voidSaleAmountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_GHC, CardPrefix.calLen(String.valueOf(batchLabel.getText()), 6));
//////END 20180720 SINN last reprint settlement GHC
                    } else {
                        hostLabel.setText("KTB ONUS");      // Paul_20181028 Sinn merge version UAT6_0016
                        int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)) - 1;
                        batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
//                        batchLabel.setText(CardPrefix.calLen(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS),6));
                        tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                        midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_TMS, dateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_TMS, timeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS, saleTotalLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS, saleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS, voidSaleCountLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS, voidSaleAmountLabel.getText().toString());

                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_TMS, CardPrefix.calLen(String.valueOf(batch), 6));
                    }
// Paul_20180709


                    Log.d(TAG, "typeHost:" + typeHost.toString());

//                    if (!typeHost.equalsIgnoreCase("TMS")) {// Paul_20180710
                    if ((!typeHost.equalsIgnoreCase("TMS")) && (!typeHost.equalsIgnoreCase("QR"))) {// Paul_20180710
                        {
//                            if(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1")) //SINN 20181122  Merchant support rate
                            if(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1"))
                            {
                                summaryLinearFeeLayout.setVisibility(View.GONE);
                            }
                            else {
                                summaryLinearFeeLayout.setVisibility(View.VISIBLE);
                                selectSummaryTAXReport(typeHost, realm);
                            }
                        }

                    } else {
                        summaryLinearFeeLayout.setVisibility(View.GONE);
                    }
                    System.out.printf("utility:: %s          DATABASE Delete 00000000000000000000001111 \n",TAG);
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
                        realm = null;   // Paul_20181026 Some time DB Read error solved
                    }
                }
            }

        }).start();
    }

    // Paul_20181203 POS EPS TMS
    private void setViewNormalSlip() {
        msgLabelgotoMain.setText("ทำรายการสำเร็จ");  ////20181021 SINN settlement all

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
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        NormalmerchantName1NormalLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        NormalmerchantName2NormalLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        NormalmerchantName3NormalLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));


                    NormalsummaryvoidSaleCountLabel.setText(transTempVoid.size() + "");
                    NormalsummaryvoidSaleAmountLabel.setText(decimalFormat.format(amountVoid));
                    NormalsummarysaleTotalLabel.setText(decimalFormat.format(amountSale));
                    NormalsummarysaleCountLabel.setText(transTemp.size() + "");
                    NormalsummarycardCountLabel.setText((transTemp.size() + transTempVoid.size()) + "");
                    NormalsummarycardAmountLabel.setText(decimalFormat.format(amountSale));

                    NormaldateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    NormaltimeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));

                    if (typeHost.equalsIgnoreCase("POS")) {
                        NormalhostLabel.setText("HOST:KTB OFFUS");     // Paul_20181028 Sinn merge version UAT6_0016
                        int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
                        NormalbatchLabel.setText("BATCH:"+CardPrefix.calLen(String.valueOf(batch), 6));
                        NormaltidLabel.setText("TID:"+Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                        NormalmidLabel.setText("MID:"+Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_POS, NormaldateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_POS, NormaltimeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_POS, decimalFormat.format(amountSale));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_POS, transTemp.size() + "");
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_POS, transTempVoid.size() + "");
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_POS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));

                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_POS, CardPrefix.calLen(String.valueOf(batch), 6));
                    } else if (typeHost.equalsIgnoreCase("EPS")) {
                        NormalhostLabel.setText("HOST:WAY4");
                        int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
                        NormalbatchLabel.setText("BATCH:"+CardPrefix.calLen(String.valueOf(batch), 6));
                        NormaltidLabel.setText("TID:"+Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                        NormalmidLabel.setText("MID:"+Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_EPS, NormaldateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_EPS, NormaltimeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_EPS, decimalFormat.format(amountSale));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_EPS, transTemp.size() + "");
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_EPS, transTempVoid.size() + "");
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_EPS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_EPS, CardPrefix.calLen(String.valueOf(batch), 6));

////20180720 SINN last reprint settlement GHC
                    } else {
                        NormalhostLabel.setText("HOST:KTB ONUS");      // Paul_20181028 Sinn merge version UAT6_0016
                        int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)) - 1;
                        NormalbatchLabel.setText("BATCH:"+CardPrefix.calLen(String.valueOf(batch), 6));
                        NormaltidLabel.setText("TID:"+Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                        NormalmidLabel.setText("MID:"+Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_TMS, NormaldateLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_TMS, NormaltimeLabel.getText().toString());
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_TMS, decimalFormat.format(amountSale));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_TMS, transTemp.size() + "");
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_TMS, transTempVoid.size() + "");
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_TMS, getString(R.string.slip_SETTLE_pattern_amount_void, decimalFormat.format(amountVoid)));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_TMS, CardPrefix.calLen(String.valueOf(batch), 6));
                    }
// Paul_20180709

                    // Paul_20181202
                    if (CardTypeDB == null) {
                        CardTypeDB = new ArrayList<>();
                    } else {
                        CardTypeDB.clear();
                    }
                    recyclerViewCardReportSummary.setAdapter(null);
                    slipSummaryReportCardAdapter = new SlipSummaryReportCardAdapter(MenuSettlementActivity.this);
                    recyclerViewCardReportSummary.setAdapter(slipSummaryReportCardAdapter);
                    if(realm == null)
                        realm = Realm.getDefaultInstance();
                    RealmResults<TransTemp> transTemp10 = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
                    for(int i=0;i<transTemp10.size();i++) {
                        String CardTypeHolder =   transTemp10.get(i).getCardTypeHolder();
                        System.out.printf("utility:: %s CardTypeHolder = %s \n",TAG,CardTypeHolder);
                        String Amount = decimalFormat.format(Double.valueOf(transTemp10.get(i).getAmount()));
                        System.out.printf("utility:: %s Amount = %s \n",TAG,Amount);
                    }
                    String[] CardTypeTempHolder = new String[100];
//        CardTypeTempHolder = null;
                    int CardTypeTempCnt=0;

                    for(int k=0;k<100;k++)
                    {
                        CardTypeTempHolder[k] = null;
                    }
                    for(int i=0;i<transTemp10.size();i++) {
                        String CardTypeHolder =   transTemp10.get(i).getCardTypeHolder();
                        System.out.printf("utility:: %s CardTypeHolder 99999999 = %s \n",TAG,CardTypeHolder);

                        int CheckFlg = 0;
                        for(int j=0;(j<CardTypeTempCnt) && (j<100);j++) {
                            if(CardTypeTempHolder[j] == null)
                                break;
                            if(CardTypeHolder.equalsIgnoreCase(CardTypeTempHolder[j]))
                            {
                                CheckFlg = 1;
                                break;
                            }
                        }
                        if(CheckFlg == 0)
                        {
                            CardTypeTempHolder[CardTypeTempCnt] = CardTypeHolder;
                            CardTypeTempCnt++;
                            CardTypeDB.addAll( realm.where( TransTemp.class ).equalTo( "hostTypeCard", typeHost ).equalTo("CardTypeHolder",CardTypeHolder).findAll() );
                            System.out.printf("utility:: %s CardTypeDB.size() = %d \n",TAG,CardTypeDB.size());
                            slipSummaryReportCardAdapter.setItem( CardTypeDB );
                        }
                    }
                    slipSummaryReportCardAdapter.notifyDataSetChanged();
// Paul_20181202 End
                    Log.d(TAG, "typeHost:" + typeHost.toString());

                    if ((!typeHost.equalsIgnoreCase("TMS")) && (!typeHost.equalsIgnoreCase("QR"))) {// Paul_20180710
                        {
                            if(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1"))
                            {
                                NormalsummaryLinearFeeLayout.setVisibility(View.GONE);
                            }
                            else {
                                NormalsummaryLinearFeeLayout.setVisibility(View.VISIBLE);
                                System.out.printf("utility:: %s setViewNormalSlip call to selectSummaryTAXReport\n",TAG);
                                selectSummaryTAXReportNormal(typeHost, realm);  // Paul_20181205
                            }
                        }
                    } else {
                        NormalsummaryLinearFeeLayout.setVisibility(View.GONE);
                    }
//                    System.out.printf("utility:: %s          DATABASE Delete 00000000000000000000001111 \n",TAG);
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll();
//                            transTemp.deleteAllFromRealm();
//                        }
//                    });
                    NormalDuplicatelLabel.setVisibility( View.VISIBLE );
                    NormalDuplicatelLabelfee.setVisibility( View.VISIBLE );
                    setMeasureNormal();
                    Utility.SettlementReprintBmpWrite(typeHost,getBitmapFromView(settlementNormalLinearLayout));    // Paul_20181205 settlement reprint modify
                    NormalDuplicatelLabel.setVisibility( View.GONE );
                    NormalDuplicatelLabelfee.setVisibility( View.GONE );

//                    setMeasureQr();

                    doPrinting(getBitmapFromView(settlementNormalLinearLayout));    // settlementNormalLinearLayout , settlementLinearLayout

// TTTTTTTTTTTTTTTTTTTTT
                    System.out.printf("utility:: %s          DATABASE Delete 00000000000000000000002222 \n",TAG);
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

    private void setViewSettlementHGC() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hgcView = inflater.inflate(R.layout.view_slip_settlement_hgc, null);
        settlementHgcLinearLayout = hgcView.findViewById(R.id.settlementLinearLayout);
        dateHgcLabel = hgcView.findViewById(R.id.dateLabel);
        timeHgcLabel = hgcView.findViewById(R.id.timeLabel);
        midHgcLabel = hgcView.findViewById(R.id.midLabel);
        tidHgcLabel = hgcView.findViewById(R.id.tidLabel);
        batchHgcLabel = hgcView.findViewById(R.id.batchLabel);
        hostHgcLabel = hgcView.findViewById(R.id.hostLabel);
        saleCountHgcLabel = hgcView.findViewById(R.id.saleCountLabel);
        saleTotalHgcLabel = hgcView.findViewById(R.id.saleTotalLabel);
        voidSaleCountHgcLabel = hgcView.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountHgcLabel = hgcView.findViewById(R.id.voidSaleAmountLabel);
        cardCountHgcLabel = hgcView.findViewById(R.id.cardCountLabel);
        cardAmountHgcLabel = hgcView.findViewById(R.id.cardAmountLabel);
        merchantName1HgcLabel = hgcView.findViewById(R.id.merchantName1Label);
        merchantName2HgcLabel = hgcView.findViewById(R.id.merchantName2Label);
        merchantName3HgcLabel = hgcView.findViewById(R.id.merchantName3Label);

        bankImage = hgcView.findViewById(R.id.bankImage);   //SINN 20181026 change image by project
        bankImage.setImageResource(R.drawable.logo_ktb);
        if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1"))
            bankImage.setImageResource(R.drawable.logo_healthcare);

    }

    private void setMeasureHGC() {
        hgcView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcView.layout(0, 0, hgcView.getMeasuredWidth(), hgcView.getMeasuredHeight());
    }

    // Paul_20181203
    private void setViewSettlementNormal() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NormalView = inflater.inflate(R.layout.view_slip_card_settlement, null);

//        qrView = inflater.inflate(R.layout.view_slip_settlement_and_report, null);

        NormalsummaryLinearFeeLayout = NormalView.findViewById(R.id.summaryLinearLayout);

        recyclerViewCardReportSummary = NormalView.findViewById(R.id.recyclerViewReportSettlement);  // Paul_20181203

//        reportCardSummaryLinearLayout = NormalView.findViewById(R.id.summaryLinearLayout);  // Paul_20181203
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);          // Paul_20181202
        recyclerViewCardReportSummary.setLayoutManager(layoutManager2);                              // Paul_20181202

        slipNestedScrollView = NormalView.findViewById(R.id.slipNestedScrollView);
        settlementNormalLinearLayout = NormalView.findViewById(R.id.settlementLinearLayout);        // settlementNormalLinearLayout
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

        NormalDuplicatelLabel = NormalView.findViewById(R.id.duplicateLabel);

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

        NormalDuplicatelLabelfee = NormalView.findViewById(R.id.duplicateLabelfee);


//
    }

    private void setMeasureNormal() {
        NormalView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        NormalView.layout(0, 0, hgcView.getMeasuredWidth(), hgcView.getMeasuredHeight());
    }

//
//    // Paul_20180713 Start
//    private void setMenuList() {
//        if (menuSettleRecyclerView.getAdapter() == null) {
//            menuSettlementAdapter = new MenuSettlementAdapter(this);
//            menuSettleRecyclerView.setAdapter(menuSettlementAdapter);
//            menuSettlementAdapter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    isSettlementAll = false;
//                    cardManager.setDataDefaultBatchUpload();
//                    int position = (int) v.getTag();
//
//                    if (position == 1) {
//                        settlementPosition = 2;
//                        isSettlementAll = true;
//                        NormalSettlementFlg = 1;
//                        statusLabel.setText("KTB offus ไม่มีข้อมูล");
//                        typeHost = "POS";
//                        okBtn.setVisibility(View.GONE);
//                        dialogSettlement.show();
//                        progressBarStatus.setVisibility(View.VISIBLE);
//                        selectDataTransTempAll("POS");
//                    } else if (position == 2) {
//                        selectSettlementQR();
//                    } else if (position == 0) {
//                        settlementPosition = 0;
//                        isSettlementAll = true;
//                        NormalSettlementFlg = 0;
//                        progressBarStatus.setVisibility(View.VISIBLE);
//                        dialogSettlement.show();
//                        i = 0;
//                        selectDatabaseAllSettlement();
//
//                    } else {
//                        isSettlementAll = false;        // Paul_20180706
//                        System.out.printf("utility:: Health Care Process \n");
//                        dialog = ProgressDialog.show(MenuSettlementActivity.this, "","Loading. Please wait...", true);
//                        i = 0;
//                        selectDatabaseSaleOffline();
//                    }

//                    msgSetmenu = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).toString();
//                    Log.d("msgSetmenu_SINN", msgSetmenu + " + " + msgSetmenu.substring(2, 3));
//
//
//                    //20180803  SINN APP_ENABLE=111   1:DOL , 2:QR ,  3:HGC
//                    if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("111")) {
//                        if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 0;
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            dialogSettlement.show();
//                            i = 0;
//                            selectDatabaseAllSettlement();
//                        } else if (position == 1) {
//                            settlementPosition = 2;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 1;
//                            statusLabel.setText("KTB offus ไม่มีข้อมูล");
//                            typeHost = "POS";
//                            okBtn.setVisibility(View.GONE);
//                            dialogSettlement.show();
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            selectDataTransTempAll("POS");
//                        } else if (position == 2) {
//                            selectSettlementQR();
//                        } else if (position == 4) {//K.GAME 181008 New Dialog Settlement
//                            dialogSettlement_new.show();
//                        } else {
//                            isSettlementAll = false;
//                            dialog = ProgressDialog.show(MenuSettlementActivity.this, "", "Loading. Please wait...", true);
//                            i = 0;
//                            selectDatabaseSaleOffline();
//                        }
//                    } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("011")) {
//                        if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 0;
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            dialogSettlement.show();
//                            i = 0;
//                            selectDatabaseAllSettlement();
//                        } else if (position == 1) {
//                            selectSettlementQR();
//                        } else {
//                            isSettlementAll = false;
//                            dialog = ProgressDialog.show(MenuSettlementActivity.this, "", "Loading. Please wait...", true);
//                            i = 0;
//                            selectDatabaseSaleOffline();
//                        }
//                    } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("101")) {
//                        if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 0;
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            dialogSettlement.show();
//                            i = 0;
//                            selectDatabaseAllSettlement();
//                        } else if (position == 1) {
//                            settlementPosition = 2;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 1;
//                            statusLabel.setText("KTB offus ไม่มีข้อมูล");
//                            typeHost = "POS";
//                            okBtn.setVisibility(View.GONE);
//                            dialogSettlement.show();
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            selectDataTransTempAll("POS");
//                        } else {
//                            isSettlementAll = false;
//                            dialog = ProgressDialog.show(MenuSettlementActivity.this, "", "Loading. Please wait...", true);
//                            i = 0;
//                            selectDatabaseSaleOffline();
//                        }
//
//                    } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("100")) {
//                        if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 0;
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            dialogSettlement.show();
//                            i = 0;
//                            selectDatabaseAllSettlement();
//                        } else if (position == 1) {
//                            settlementPosition = 2;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 1;
//                            statusLabel.setText("KTB offus ไม่มีข้อมูล");
//                            typeHost = "POS";
//                            okBtn.setVisibility(View.GONE);
//                            dialogSettlement.show();
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            selectDataTransTempAll("POS");
//                        } else if (position == 2) {//K.GAME 181008 New Dialog Settlement
//                            dialogSettlement_new.show();
//                        }
////===========================================================
//                    } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("110")) {
//                        if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 0;
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            dialogSettlement.show();
//                            i = 0;
//                            selectDatabaseAllSettlement();
//
//
//                        } else if (position == 1) {
//                            settlementPosition = 2;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 1;
//                            // statusLabel.setText("KTB offus ไม่มีข้อมูล");
//                            typeHost = "POS";
//                            okBtn.setVisibility(View.GONE);
//                            dialogSettlement.show();
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            selectDataTransTempAll("POS");
//
//
//                        } else if (position == 2) {
//                            selectSettlementQR();
//                        } else if (position == 3) {//K.GAME 181008 New Dialog Settlement
//                            dialogSettlement_new.show();
//                        }
//                    }
//                    //===========================================================
//                    else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("001")) {
//                        if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 0;
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            dialogSettlement.show();
//                            i = 0;
//                            selectDatabaseAllSettlement();
//                        } else {
//                            isSettlementAll = false;
//                            dialog = ProgressDialog.show(MenuSettlementActivity.this, "", "Loading. Please wait...", true);
//                            i = 0;
//                            selectDatabaseSaleOffline();
//                        }
//                    } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("010")) {
//                        if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            NormalSettlementFlg = 0;
//                            progressBarStatus.setVisibility(View.VISIBLE);
//                            dialogSettlement.show();
//                            i = 0;
//                            selectDatabaseAllSettlement();
//                        } else if (position == 2) {
//                            selectSettlementQR();
//                        }
//                    }
//                }
//            });
//        } else {
//            menuSettlementAdapter.clear();
//        }
//        if (menuList == null) {
//            menuList = new ArrayList<>();
//        } else {
//            menuList.clear();
//        }
////
////        menuList.add("Settlement All"); // 4 0                  // 0
////        menuList.add("Normal Sale");  // Paul_20180713 SIT      // 1
////        menuList.add("QR"); // 3 4                              // 2
////        menuList.add("HEALTH CARE");        // Paul_20180706    // 3
////        menuSettlementAdapter.setItem(menuList);
////        menuSettlementAdapter.notifyDataSetChanged();
//
//        //20180803  SINN APP_ENABLE=111   1:DOL , 2:QR ,  3:HGC
//        if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("111")) {
//            menuList.add("Settlement All");
//            menuList.add("Normal Sale");
//            menuList.add("QR");
//            menuList.add("HEALTH CARE");
//            menuList.add("TEST");
//        } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("011")) {
//            menuList.add("Settlement All");
//            menuList.add("QR");
//            menuList.add("HEALTH CARE");
//            menuList.add("TEST");
//        } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("101")) {
//            menuList.add("Settlement All");
//            menuList.add("Normal Sale");
//            menuList.add("HEALTH CARE");
//            menuList.add("TEST");
//        } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("100")) {
//            menuList.add("Settlement All");
//            menuList.add("Normal Sale");
//            menuList.add("TEST");
//        } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("110")) {
//            menuList.add("Settlement All");
//            menuList.add("Normal Sale");
//            menuList.add("QR");
//            menuList.add("TEST");
//        } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("001")) {
//            menuList.add("Settlement All");
//            menuList.add("HEALTH CARE");
//            menuList.add("TEST");
//        } else if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE).equalsIgnoreCase("010")) {
//            menuList.add("Settlement All");
//            menuList.add("QR");
//            menuList.add("TEST");
//        }
//
//
//        menuSettlementAdapter.setItem(menuList);
//        menuSettlementAdapter.notifyDataSetChanged();
//
//
//    }

// Paul_20180713 End


    //SINN 20180713 Change menu settlement
//    private void setMenuList_Sinn() {
//        if (menuSettleRecyclerView.getAdapter() == null) {
//            menuSettlementAdapter = new MenuSettlementAdapter(this);
//            menuSettleRecyclerView.setAdapter(menuSettlementAdapter);
//            menuSettlementAdapter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    isSettlementAll = false;
//                    cardManager.setDataDefaultBatchUpload();
//                    int position = (int) v.getTag();
//
//
//                    if (inTriger == 0) {
//                        //-------------------------------------------------------
//                        if (position == 1) {
//                            setMenuListNormal();
//                        } else if (position == 2) {
//                            selectSettlementQR();
//                        } else if (position == 0) {
//                            settlementPosition = 0;
//                            isSettlementAll = true;
//                            progressBarStatus.setVisibility( View.VISIBLE );
//                            dialogSettlement.show();
//                            selectDatabaseAllSettlement();
//
//                        } else {
//                            isSettlementAll = false;        // Paul_20180706
//                            System.out.printf( "utility:: Health Care Process \n" );
//                            dialog = ProgressDialog.show( MenuSettlementActivity.this, "",
//                                    "Loading. Please wait...", true );
//                            selectDatabaseSaleOffline();
//                        }
//                    }
//                    //------------------------------------------------
//                    else {
//                        if(position==0)
//                        {
//                            typeHost = "TMS";
//                            selectDataTransTemp("TMS");
//                            if (transTemp.size() > 0) {
//                                if (transTempVoidFlag.size() != 0) {
//                                    cardManager.setDataDefaultUploadCradit();
//                                    cardManager.setDataSettlementAndSendTMS();
//                                } else {
//                                    cardManager.setDataDefaultUploadCradit();
//                                    cardManager.setDataSettlementAndSendTMS();
//                                }
//                                dialogWaiting.show();
//                            } else {
//                                okBtn.setVisibility(View.GONE);
//                                Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                            }
//                        } else if (position == 1) {
//                            typeHost = "POS";
//                            selectDataTransTemp("POS");
//                            if (transTemp.size() > 0) {
//                                if (transTempVoidFlag.size() != 0) {
////                                cardManager.setDataSettlementAndSend("POS");
//                                    cardManager.setDataDefaultUploadCradit();
//                                    cardManager.setCheckTCUpload("POS", true);
//                                } else {
////                                cardManager.setDataSettlementAndSend("POS");
//                                    cardManager.setDataDefaultUploadCradit();
//                                    cardManager.setCheckTCUpload("POS", true);
//                                }
//                                dialogWaiting.show();
//                            } else {
//                                okBtn.setVisibility(View.GONE);
//                                Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                            }
//
//                        } else {
//                            typeHost = "EPS";
//                            selectDataTransTemp("EPS");
//                            if (transTemp.size() > 0) {
//                                if (transTempVoidFlag.size() != 0) {
////                                cardManager.setDataSettlementAndSendEPS();
//                                    cardManager.setDataDefaultUploadCradit();
//                                    cardManager.setCheckTCUpload("EPS", true);
//                                } else {
////                                cardManager.setDataSettlementAndSendEPS();
//                                    cardManager.setDataDefaultUploadCradit();
//                                    cardManager.setCheckTCUpload("EPS", true);
//                                }
//                                dialogWaiting.show();
//                            } else {
//                                okBtn.setVisibility(View.GONE);
//                                Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                    @Override
//                                    public void onClickImage(Dialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                }
//
//            });
//        } else {
//            menuSettlementAdapter.clear();
//        }
//        if (menuList == null) {
//            menuList = new ArrayList<>();
//        } else {
//            menuList.clear();
//        }
//        menuList.add("Settlement All"); // 4 0
//        /*
//        menuList.add("KTB On Us"); // 2 1
//        menuList.add("KTB Off us"); // 0 2
//        menuList.add("BASE24 EPS"); // 1 3
//        */
//        menuList.add("Normal Sale");  ////SINN 20180713 Change menu settlement
//        menuList.add("QR"); // 3 4
//        menuList.add("HEALTH CARE");        // Paul_20180706
//        menuSettlementAdapter.setItem(menuList);
//        menuSettlementAdapter.notifyDataSetChanged();
//
//    }


//    private void setMenuListNormal() {
//        inTriger=1;
//
//        if (menuSettleRecyclerView.getAdapter() == null) {
//            menuSettlementAdapter = new MenuSettlementAdapter(this);
//            menuSettleRecyclerView.setAdapter(menuSettlementAdapter);
//            menuSettlementAdapter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    isSettlementAll = false;
//                    cardManager.setDataDefaultBatchUpload();
//                    int position = (int) v.getTag();
//
//                    if (position == 0) {
//                        typeHost = "TMS";
//                        selectDataTransTemp("TMS");
//                        if (transTemp.size() > 0) {
//                            if (transTempVoidFlag.size() != 0) {
//                                cardManager.setDataDefaultUploadCradit();
//                                cardManager.setDataSettlementAndSendTMS();
//                            } else {
//                                cardManager.setDataDefaultUploadCradit();
//                                cardManager.setDataSettlementAndSendTMS();
//                            }
//                            dialogWaiting.show();
//                        } else {
//                            okBtn.setVisibility(View.GONE);
//                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                @Override
//                                public void onClickImage(Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            });
//                        }
//                    } else if (position == 1) {
//                        typeHost = "POS";
//                        selectDataTransTemp("POS");
//                        if (transTemp.size() > 0) {
//                            if (transTempVoidFlag.size() != 0) {
////                                cardManager.setDataSettlementAndSend("POS");
//                                cardManager.setDataDefaultUploadCradit();
//                                cardManager.setCheckTCUpload("POS", true);
//                            } else {
////                                cardManager.setDataSettlementAndSend("POS");
//                                cardManager.setDataDefaultUploadCradit();
//                                cardManager.setCheckTCUpload("POS", true);
//                            }
//                            dialogWaiting.show();
//                        } else {
//                            okBtn.setVisibility(View.GONE);
//                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                @Override
//                                public void onClickImage(Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            });
//                        }
//
//                    } else {
//                        typeHost = "EPS";
//                        selectDataTransTemp("EPS");
//                        if (transTemp.size() > 0) {
//                            if (transTempVoidFlag.size() != 0) {
////                                cardManager.setDataSettlementAndSendEPS();
//                                cardManager.setDataDefaultUploadCradit();
//                                cardManager.setCheckTCUpload("EPS", true);
//                            } else {
////                                cardManager.setDataSettlementAndSendEPS();
//                                cardManager.setDataDefaultUploadCradit();
//                                cardManager.setCheckTCUpload("EPS", true);
//                            }
//                            dialogWaiting.show();
//                        } else {
//                            okBtn.setVisibility(View.GONE);
//                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                                @Override
//                                public void onClickImage(Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            });
//                        }
//                    }
//
//                }
//            });
//        }
//        else {
//            menuSettlementAdapter.clear();
//        }
//        if (menuList == null) {
//            menuList = new ArrayList<>();
//        } else {
//            menuList.clear();
//        }
//
//
//        menuList.add("KTB On Us"); // 0
//        menuList.add("KTB Off us"); // 1
//        menuList.add("BASE24 EPS"); // 2
//        menuSettlementAdapter.setItem(menuList);
//        menuSettlementAdapter.notifyDataSetChanged();
//
//    }


    //END SINN 20180713 Change menu settlement

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
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private void selectDataTransTempAllGAME(final String typeHost1) {   // Paul_20180706
        typeHost = typeHost1;       // Paul_20180706
        Log.d(TAG, "selectDataTransTempAllGAME hostflag:" + hostflag);

        System.out.printf("utility:: %s selectDataTransTempAllGAME 000000001 hostflag = %s\n", TAG, hostflag);
        if (hostflag.substring(0, 3).equals("TMS")) {
            Log.d("1919", "settlement TMS");
            hostflag = hostflag.replaceAll("TMS", "111");
            typeHost = "TMS";
            settlementPosition = 4;

        } else if (hostflag.substring(3, 6).equals("POS")) {
            Log.d("1919", "settlement POS");
            hostflag = hostflag.replaceAll("POS", "222");
            typeHost = "POS";
            settlementPosition = 2;

        } else if (hostflag.substring(6, 9).equals("EPS")) {
            Log.d("1919", "settlement EPS");
            hostflag = hostflag.replaceAll("EPS", "333");
            typeHost = "EPS";
            settlementPosition = 3;

        } else if (hostflag.substring(9, 12).equals("GHC")) {    //   ||TMS|POS|EPS|GHC|QR
            Log.d("1919", "settlement GHC");
            hostflag = hostflag.replaceAll("GHC", "444");
            typeHost = "GHC";
            settlementPosition = 1;

        } else if (hostflag.substring(12, 15).equals(" QR")) {
            Log.d("1919", "settlement  QR");
            hostflag = hostflag.replaceAll(" QR", "555");
            settlementPosition = 5;
            typeHost = "QR";

        } else if (hostflag.substring(15, 18).equals("ALI")) {
            Log.d("1919", "settlement  ALI");
            hostflag = hostflag.replaceAll("ALI", "666");
            settlementPosition = 6;
            typeHost = "ALI";
        } else if (hostflag.substring(18, 21).equals("WEC")) {
            Log.d("1919", "settlement  WEC");
            hostflag = hostflag.replaceAll("WEC", "777");
            settlementPosition = 7;
            typeHost = "WEC";
        } else {
            settlementPosition = 1000;
        }
        System.out.printf("utility:: %s selectDataTransTempAllGAME 000000002 hostflag = %s\n", TAG, hostflag);

        Log.d(TAG, "selectDataTransTempAllGAME after hostflag:" + hostflag);
//-----------------------------------------------------------------------
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
                    if (qrTemp == null) {
                        qrTemp = new ArrayList<>();
                    } else {
                        qrTemp.clear();
                    }
                    if (aliTemp == null) {
                        aliTemp = new ArrayList<>();
                    } else {
                        aliTemp.clear();
                    }
                    if (wecTemp == null) {
                        wecTemp = new ArrayList<>();
                    } else {
                        wecTemp.clear();
                    }

                    transTemp.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
                    Log.d(TAG, "selectDataTransTemp: " + transTemp.size());

                    qrTemp.addAll(realm.where(QrCode.class).equalTo("hostTypeCard", "QR").findAll());
                    aliTemp.addAll(realm.where(QrCode.class).equalTo("hostTypeCard", "ALIPAY").findAll());
                    wecTemp.addAll(realm.where(QrCode.class).equalTo("hostTypeCard", "WECHAT").findAll());

                    if (transTemp.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (settlementPosition == 0) {      // Paul_20180709
                                } else if (settlementPosition == 1) {
                                    statusLabel.setText("Settlement HeathCare");
                                } else if (settlementPosition == 2) {
                                    statusLabel.setText("Settlement KTB OFFUS");    // Paul_20181028 Sinn merge version UAT6_0016
                                } else if (settlementPosition == 3) {
                                    statusLabel.setText("Settlement WAY4");
                                } else if (settlementPosition == 4) {
                                    statusLabel.setText("Settlement KTB ONUS");
                                }
//                                else if (settlementPosition == 5) {
//                                }
                                //20181021 SINN settlement all
                                else if (settlementPosition == 5) {
                                    statusLabel.setText("Settlement QR");
                                } else if (settlementPosition == 6) {
                                    statusLabel.setText("Settlement ALIPAY");       // Paul_20190324
                                } else if (settlementPosition == 7) {
                                    statusLabel.setText("Settlement WECHAT PAY");   // Paul_20190324
                                }
                                //END 20181021 SINN settlement all

                            }
                        });
                        if (!typeHost.equals("TMS")) {
                            cardManager.setDataDefaultUploadCradit();
                            cardManager.setCheckTCUpload(typeHost, true);
                        } else {
                            cardManager.setDataDefaultUploadCradit();
                            cardManager.setDataSettlementAndSendSALE();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countDownTimerSettle = new CountDownTimer(2000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        System.out.printf("utility:: selectDataTransTempAllGAME onFinish AAAAAAAAAAAA settlementPosition = %d \n", settlementPosition);
                                        statusLabel.setText("โอนยอด");
                                        dialogSettlement.show();

                                        if (settlementPosition == 0) {      // Paul_20180709
                                            countDownTimerSettle.cancel();        // Paul_20180709
                                            selectDatabaseSaleOffline(); ////20181021 SINN settlement all  GHC Tomorow no need
                                        } else if (settlementPosition == 1) {      // Paul_20180709
                                            statusLabel.setText("HeathCare ไม่มีข้อมูล");
                                            countDownTimerSettle.cancel();        // Paul_20180709
                                            selectDataTransTempAllGAME(" ");
                                        } else if (settlementPosition == 2) {
                                            statusLabel.setText("KTB OFFUS ไม่มีข้อมูล");   // Paul_20181028 Sinn merge version UAT6_0016
                                            countDownTimerSettle.cancel();        // Paul_20180709
                                            selectDataTransTempAllGAME(" ");
                                        } else if (settlementPosition == 3) {
                                            statusLabel.setText("WAY4 ไม่มีข้อมูล");
                                            countDownTimerSettle.cancel();        // Paul_20180709
                                            selectDataTransTempAllGAME(" ");
                                        } else if (settlementPosition == 4) {
                                            statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
                                            countDownTimerSettle.cancel();        // Paul_20180709
                                            selectDataTransTempAllGAME(" ");
                                        } else if (settlementPosition == 5) {
                                            countDownTimerSettle.cancel();        //20181021 SINN settlement all
                                            if(qrTemp.size() > 0) {
                                                statusLabel.setText("Settlement Qr");
                                                selectSettlementQRAll();
                                            }else {
                                                statusLabel.setText("QR ไม่มีข้อมูล");
                                                selectDataTransTempAllGAME(" ");
                                            }
                                        } else if (settlementPosition == 6) {
                                            countDownTimerSettle.cancel();
                                            if(aliTemp.size() > 0) {
                                                statusLabel.setText("Settlement ALIPAY");       // Paul_20190324
                                            }else {
                                                statusLabel.setText("ALIPAY ไม่มีข้อมูล");
                                            }
                                            selectSettlementALIPAY();
                                        } else if (settlementPosition == 7) {
                                            statusLabel.setText(" ");
                                            countDownTimerSettle.cancel();
                                            if(wecTemp.size() > 0) {
                                                statusLabel.setText("Settlement WECHAT PAY");
                                            }else{
                                                statusLabel.setText("WECHAT ไม่มีข้อมูล");
                                            }
                                            selectSettlementWECHAT();
                                        } else {
                                            countDownTimerSettle.cancel();        // Paul_20180709
                                            dialogSettlement.dismiss();
                                            Log.d(TAG, "success: " + settlementPosition);
//                                            finish();
                                            System.out.printf("utility:: Settle selectDataTransTempAll Success \n");
                                            if (typeInterface != null) {
                                                Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ยกเลิกการทำรายการ");  // Paul_20181020
                                                TerToPosSettlement();
                                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                    @Override
                                                    public void success() {
                                                        Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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

                                                if (logoutInterface!=null){
                                                    Intent intent = new Intent(MenuSettlementActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                }else {
                                                    dialogSuccess_GotoMain.show();//K.GAME 20181018
                                                    cdt.start();//K.GAME 20181018
                                                }
                                            }
                                        }
                                    }
                                }.start();
                            }
                        });
                    }
                } finally {
                    if (realm != null) {
                        realm.close();
                        realm = null;   // Paul_20181026 Some time DB Read error solved
                    }
                }
            }
        }).start();
        Log.d(TAG, "END selectDataTransTempAllGAME");
    }

    private void setResponsCode() {

        System.out.printf("utility:: setResponsCode AAAAAAAAAAAA 00000005 settlementPosition = %d \n", settlementPosition);
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(final String response) {
                System.out.printf("utility:: MenuSettlementActivity setResponseCode onResponseCode 000001 \n");
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
//                                    settlementPosition++;
//                                    if ((NormalSettlementFlg == 1) && isSettlementAll)       // Paul_20180713
//                                    {
//                                        if (settlementPosition > 4) {
//                                            settlementPosition++;
//                                        }
//                                    }
                                    okBtn.setVisibility(View.GONE);
// Paul_20180706
                                    if (settlementPosition == 1) {
//                                        Log.d("msgSetmenu_SINN", msgSetmenu + " + " + msgSetmenu.substring(2, 3));
//                                        if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
                                        statusLabel.setText("HeathCare ไม่มีข้อมูล");
                                    } else if (settlementPosition == 2) {
                                        statusLabel.setText("KTB OFFUS ไม่มีข้อมูล");
                                    } else if (settlementPosition == 3) {
                                        statusLabel.setText("WAY4 ไม่มีข้อมูล");
                                    } else if (settlementPosition == 4) {
                                        statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
                                    } else if (settlementPosition == 5) {
                                        statusLabel.setText("QR ไม่มีข้อมูล");
                                    } else if (settlementPosition == 6) {
                                        statusLabel.setText("ALIPAY ไม่มีข้อมูล");
                                    } else if (settlementPosition == 7) {
                                        statusLabel.setText("WECHAT ไม่มีข้อมูล");
                                    } else {
                                        statusLabel.setText("Settlement สำเร็จ");
                                    }

                                    if (settlementPosition == 2) {
                                        // selectDataTransTempAll("POS");  hostflag ="TMSPOSEPSGHC QRALI"
                                        hostflag = "111POS333444555666777";
                                        selectDataTransTempAllGAME(" ");

                                    } else if (settlementPosition == 3) {
                                        // selectDataTransTempAll("EPS");   hostflag ="TMSPOSEPSGHC QRALI"
                                        hostflag = "111222EPS444555666777";
                                        selectDataTransTempAllGAME(" ");

                                    } else if (settlementPosition == 4) {
                                        //selectDataTransTempAll("TMS");   hostflag ="TMSPOSEPSGHC QRALI"
                                        hostflag = "TMS222333444555666777";
                                        selectDataTransTempAllGAME(" ");

                                    } else if (settlementPosition == 5) {
                                        hostflag = "111222333444 QR666777";
                                        selectSettlementQR();
                                    } else if (settlementPosition == 6) {
                                        hostflag = "111222333444555ALI777";
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 7) {
                                        hostflag = "111222333444555666WEC";
                                        selectDataTransTempAllGAME(" ");
                                    } else {
                                        dialogSettlement.dismiss();
                                        Log.d(TAG, "success: " + settlementPosition);
                                        fileList();
                                        System.out.printf("utility:: setResponsCode Settle Success \n");
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onResponseCodeandMSG(final String response, final String szCode) {
                System.out.printf("utility:: MenuSettlementActivity setResponseCode onResponseCodeandMSG 000001 \n");
// Paul_20180731
//                System.out.printf("utility:: %s onResponseCodeandMSG 000005 \n",TAG);
//                if(posInterfaceActivity.PosInterfaceExistFlg == 1)
//                {
//                    countDownTimerSettle.cancel();        // Paul_20180801
//
//                    System.out.printf("utility:: %s onResponseCodeandMSG 000006 \n",TAG);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                                        dialogAlert.show();
//                            Log.d(TAG, "responseCodeDialog() response: "+szCode);
//                            //TellToPosError(response);
//                            TellToPosError(szCode);
//                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                @Override
//                                public void success() {
//                                    posInterfaceActivity.POSInterfaceInit();
////                                    posInterfaceActivity.PosInterfaceExistFlg = 0;
//                                    // finish();
//                                    Intent intent = new Intent( MenuSettlementActivity.this, MenuServiceListActivity.class );
//                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
//                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                                    startActivity( intent );
//                                    finish();
//                                    overridePendingTransition( 0, 0 );
//                                }
//                            });
//                        }
//                    });
//                }

// Paul_20181011
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                                System.out.printf("utility:: %s onResponseCodeandMSG 000001 \n", TAG);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//////                                        dialogAlert.show();
//                                        Log.d(TAG, "responseCodeDialog() response: " + szCode);
////                                        //TellToPosError(response);
//                                        TellToPosError(szCode);
//                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                            @Override
//                                            public void success() {
//////                                                dialogAlert.dismiss();
//                                                posInterfaceActivity.PosInterfaceExistFlg = 0;
////                                                // finish();
//                                                Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                startActivity(intent);
//                                                finish();
//                                                overridePendingTransition(0, 0);
//                                            }
//                                        });
//                                    }
//                                });
//                            } else
//                                Utility.customDialogAlert(MenuSettlementActivity.this, response, new Utility.OnClickCloseImage() {

                            HOST_CARD = cardManager.getHostCard();
                            if (HOST_CARD.equalsIgnoreCase("TMS"))
                                SzError = RespCode.ResponseMsgTMS(szCode);
                            else if (HOST_CARD.equalsIgnoreCase("POS"))
                                SzError = RespCode.ResponseMsgPOS(szCode);
                            else if (HOST_CARD.equalsIgnoreCase("EPS"))
                                SzError = RespCode.ResponseMsgPOS(szCode);
                            else if (HOST_CARD.equalsIgnoreCase("GHC"))
                                SzError = RespCode.ResponseMsgGHC(szCode);


//                            Utility.customDialogAlert(MenuSettlementActivity.this, szCode+"\n"+SzError, new Utility.OnClickCloseImage() {
//                                @Override
//                                public void onClickImage(Dialog dialog) {
//                                    dialog.dismiss();
//                                    if(hostflag.equalsIgnoreCase("111222333444555"))
//                                    finish();
//                                }
//                            });

                            dialogSettlement.dismiss();
// Paul_20181019 Start
                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                System.out.printf("utility:: %s onResponseCodeandMSG 000001 \n", TAG);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
////                                        dialogAlert.show();
                                        Log.d(TAG, "responseCodeDialog() response: " + szCode);
//                                        //TellToPosError(response);
                                        Utility.customDialogAlertAuto(MenuSettlementActivity.this, szCode + "\n" + SzError);
                                        TellToPosError(szCode);
                                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                            @Override
                                            public void success() {
////                                                dialogAlert.dismiss();
                                                Utility.customDialogAlertAutoClear();
                                                posInterfaceActivity.PosInterfaceExistFlg = 0;
//                                                // finish();
                                                Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
                            } else {
                                Utility.customDialogAlertAuto(MenuSettlementActivity.this, szCode + "\n" + SzError);
                                new CountDownTimer(3000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        Utility.customDialogAlertAutoClear();
                                        //20181011 Game settlement all
                                        if (!hostflag.equalsIgnoreCase("111222333444555666777")) {
                                            statusLabel.setText("โอนยอด");
                                            dialogSettlement.show();

                                            settlementPosition = 0;
                                            selectDataTransTempAllGAME("");  //20181011 Game settlement all
                                        } else {
                                            dialogSettlement.dismiss();
                                        }
                                    }
                                }.start();
                            }
// Paul_20181019 End
                        }
                    });
                }

            }

            @Override
            public void onResponseCodeSuccess() {

            }

            @Override
            public void onConnectTimeOut() {
                System.out.printf("utility:: MenuSteelement onConnectTimeOut Error \n");
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
                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                System.out.printf("utility:: MenuSettlementActi onConnectTimeOut \n");
                                Utility.customDialogAlertAuto(MenuSettlementActivity.this, "เชื่อมต่อล้มเหลว");
                                TellToPosError("21");
                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        Utility.customDialogAlertAutoClear();
                                        System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                                        Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
//                                Utility.customDialogAlert(MenuSettlementActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
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
                            if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                System.out.printf("utility:: MenuSettlementActi onTransactionTimeOut \n");
                                Utility.customDialogAlertAuto(MenuSettlementActivity.this, "transactionTimeOut");
                                TellToPosError("21");
                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        Utility.customDialogAlertAutoClear();
                                        System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                                        Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
//                                Utility.customDialogAlert(MenuSettlementActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
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
    }

    public void doPrinting(Bitmap slip) {
        Log.d(TAG, "Start doPrinting");
        oldBitmap = slip;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "doPrinting onPrintFinish() hostflag:" + hostflag);
//                                    if (!hostflag.equalsIgnoreCase("111222333444555666777"))
                if(settlementPosition != settlementEnd)
                    selectDataTransTempAllGAME("");  //20181011 Game settlement all
                else {
                    dialogSettlement.dismiss();
                    Log.d(TAG, "success: " + settlementPosition);
//                                        finish();
                    System.out.printf("utility:: Settle onPrintFinish Success 001 \n");
                    if (typeInterface != null) {
                        Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ยกเลิกการทำรายการ");  // Paul_20181020
                        TerToPosSettlement();
                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                            @Override
                            public void success() {
                                Utility.customDialogAlertAutoClear();   // Paul_20181020
                                Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        });
//                                        } else finish(); //K.GAME 20181018
                    } else {

                        if (logoutInterface!=null){
                            Intent intent = new Intent(MenuSettlementActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }else {
                            msgLabelgotoMain.setText("ทำรายการสำเร็จ");  // Paul_20181203   ////20181021 SINN settlement all
                            dialogSuccess_GotoMain.show();//K.GAME 20181018
                            cdt.start();//K.GAME 20181018
                        }


                    }
                }


                /////20181011 Game settlement all

// Paul_20180706
//                                    if (settlementPosition == 1) {
//                                        if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
//                                            statusLabel.setText("Settlement HeathCare");
//                                    } else if (settlementPosition == 2) {
//                                        statusLabel.setText("Settlement KTB OFFUS");
//                                    } else if (settlementPosition == 3) {
//                                        statusLabel.setText("Settlement BASE24 EPS");
//                                    } else if (settlementPosition == 4) {
//                                        statusLabel.setText("Settlement KTB ONUS");
//                                    } else if (settlementPosition == 5) {
//                                        statusLabel.setText("Settlement QR");
//                                    } else {
//                                        statusLabel.setText("Settlement สำเร็จ");
//                                    }
//                                    if (
//                                            settlementPosition == 2) {
//                                        typeHost = "POS";
//                                        selectDataTransTempAll("POS");
//                                    } else if (settlementPosition == 3) {
//                                        typeHost = "EPS";
//                                        selectDataTransTempAll("EPS");
//                                    } else if (settlementPosition == 4) {
//                                        typeHost = "TMS";
//                                        selectDataTransTempAll("TMS");
//                                    } else if (settlementPosition == 5) {
//                                        typeHost = "QR";    //20180720 SINN NO NEED QR TAX
//                                        selectSettlementQRAll();
//                                    } else
// {
//                                        dialogSettlement.dismiss();
//                                        Log.d(TAG, "success: " + settlementPosition);
//                                        System.out.printf("utility:: Settle onPrintFinish Success 001 \n");
//                                        if (typeInterface != null) {
//                                            TerToPosSettlement();
//                                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                                @Override
//                                                public void success() {
//                                                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);
//                                                    finish();
//                                                    overridePendingTransition(0, 0);
//                                                }
//                                            });
//                                        }
//                                    }

                Log.d(TAG, "onSettlementSuccess: " + settlementPosition);
                System.out.printf("utility:: Settle onPrintFinish Success 002 \n");
            }
        });
       /* new Thread() {
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

                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d(TAG, "doPrinting onPrintFinish() hostflag:" + hostflag);
//                                    if (!hostflag.equalsIgnoreCase("111222333444555666777"))
                                    if(settlementPosition != settlementEnd)
                                        selectDataTransTempAllGAME("");  //20181011 Game settlement all
                                    else {
                                        dialogSettlement.dismiss();
                                        Log.d(TAG, "success: " + settlementPosition);
//                                        finish();
                                        System.out.printf("utility:: Settle onPrintFinish Success 001 \n");
                                        if (typeInterface != null) {
                                            Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ยกเลิกการทำรายการ");  // Paul_20181020
                                            TerToPosSettlement();
                                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                @Override
                                                public void success() {
                                                    Utility.customDialogAlertAutoClear();   // Paul_20181020
                                                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                }
                                            });
//                                        } else finish(); //K.GAME 20181018
                                        } else {

                                            if (logoutInterface!=null){
                                                Intent intent = new Intent(MenuSettlementActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(0, 0);
                                            }else {
                                                msgLabelgotoMain.setText("ทำรายการสำเร็จ");  // Paul_20181203   ////20181021 SINN settlement all
                                                dialogSuccess_GotoMain.show();//K.GAME 20181018
                                                cdt.start();//K.GAME 20181018
                                            }


                                        }
                                    }


                                    /////20181011 Game settlement all

// Paul_20180706
//                                    if (settlementPosition == 1) {
//                                        if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
//                                            statusLabel.setText("Settlement HeathCare");
//                                    } else if (settlementPosition == 2) {
//                                        statusLabel.setText("Settlement KTB OFFUS");
//                                    } else if (settlementPosition == 3) {
//                                        statusLabel.setText("Settlement BASE24 EPS");
//                                    } else if (settlementPosition == 4) {
//                                        statusLabel.setText("Settlement KTB ONUS");
//                                    } else if (settlementPosition == 5) {
//                                        statusLabel.setText("Settlement QR");
//                                    } else {
//                                        statusLabel.setText("Settlement สำเร็จ");
//                                    }
//                                    if (
//                                            settlementPosition == 2) {
//                                        typeHost = "POS";
//                                        selectDataTransTempAll("POS");
//                                    } else if (settlementPosition == 3) {
//                                        typeHost = "EPS";
//                                        selectDataTransTempAll("EPS");
//                                    } else if (settlementPosition == 4) {
//                                        typeHost = "TMS";
//                                        selectDataTransTempAll("TMS");
//                                    } else if (settlementPosition == 5) {
//                                        typeHost = "QR";    //20180720 SINN NO NEED QR TAX
//                                        selectSettlementQRAll();
//                                    } else
// {
//                                        dialogSettlement.dismiss();
//                                        Log.d(TAG, "success: " + settlementPosition);
//                                        System.out.printf("utility:: Settle onPrintFinish Success 001 \n");
//                                        if (typeInterface != null) {
//                                            TerToPosSettlement();
//                                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                                @Override
//                                                public void success() {
//                                                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);
//                                                    finish();
//                                                    overridePendingTransition(0, 0);
//                                                }
//                                            });
//                                        }
//                                    }

                                    Log.d(TAG, "onSettlementSuccess: " + settlementPosition);
                                    System.out.printf("utility:: Settle onPrintFinish Success 002 \n");
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
        }.start();*/
    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setCancelable(false);
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

    private Integer selectSettlementQRCHK() {


//        TransTemp transTemp; // Database

        if (realm == null) {
            System.out.printf("utility:: updateTransactionVoid 000000099 \n");
            Log.d(TAG, "1919_updateTransactionVoid");
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            }
        });
        realm.close();
        realm = null;

        realm = Realm.getDefaultInstance();


        //----------------------------
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("statusSuccess", "1").findAll(); // Paul_20181020
                Double amountSaleQr = 0.0;
                if (qrCode.size() > 0) {
                    status = 0;
                    for (int i = 0; i < qrCode.size(); i++) {
                        amountSaleQr += Double.valueOf(qrCode.get(i).getAmount().replaceAll(",", ""));
                    }

                } else {
                    status = 1;
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (status == 0) {
                    status = 1;
                } else {
                    status = 0;
                }
            }
        });


        return status;
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
                        amountSaleQr += Double.valueOf(qrCode.get(i).getAmount().replaceAll(",", ""));
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
                    int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
                    batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
//                    batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
                    tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID));
//                    midLabel.setText( Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_QR_BILLER_ID));
                    midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_QR_MERCHANT_ID));  //20180814 SINN  use QR Merchant ID instead biller id.

                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_QR, dateLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_QR, timeLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR, saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_QR, saleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_QR, voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR, voidSaleAmountLabel.getText().toString());

                    summaryLinearFeeLayout.setVisibility(View.GONE); ////20180720 SINN NO NEED QR TAX
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
                    deleteQrAll();  //20181112 SINN KTB order QR have or no transaction success but press settlement will clear un success also.

                    okBtn.setVisibility(View.GONE);
                    Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {//K.GAME 181011 พบบัค แสดง msg ผิด //ยังไม่แก้ ถ้าแก้ฝากลบ comment ทิ้งด้วยครับ
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }

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

//                data = aliConfig.getHttps() + "transaction/inquiry";
                alipay_http = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_URL_ID); //20181114Jeff
                ALIPAY_CER_PATH = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_CERTI_ID); //20181115Jeff

                if (realm == null)
                    realm = Realm.getDefaultInstance();
                else{
                    realm = null;
                    realm = Realm.getDefaultInstance();
                }

                System.out.printf("utility:: %s selectSettlementALIPAY 000000001 hostflag = %s\n", TAG, hostflag);
                Log.d(TAG, "selectDataTransTempAllGAME hostflag:" + hostflag);

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

                RealmResults<QrCode> voidTemp = realm.where(QrCode.class).equalTo( "hostTypeCard", type ).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
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

                cardCountLabel.setText(String.valueOf(cntSale+cntVoid));
                cardAmountLabel.setText(String.valueOf(decimalFormat.format(amountSale)));
                // Paul_20181219 End


                dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTime));
                timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(dateTime));

//                cntSale = 0;  // only test
//                cntVoid = 0;  // only test

                if ((cntSale+cntVoid) > 0) {
                    status = 0;
                    endRecord = saleTemp.size()+voidTemp.size();
                    checkPage(endRecord);

                    if (endRecord < 20) {
                        if (endRecord == 0) {
                            settlementLister.onSuccess();
                        } else {
                            //compare date
                            int comp1, comp2;
                            if(saleTemp.size() > 0)
                                comp1 = Integer.parseInt(saleTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if(voidTemp.size()>0)
                                comp2 = Integer.parseInt(voidTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if(comp1 < comp2) {
                                if(comp1 != 0 && comp2 != 0)
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }else {
                                if(comp1 != 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }

                            if(saleTemp.size() >0)
                                comp1 = Integer.parseInt(saleTemp.get(saleTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if(voidTemp.size() > 0)
                                comp2 = Integer.parseInt(voidTemp.get(voidTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if(comp1 > comp2) {
                                if(comp1 != 0 && comp2 != 0)
                                    endDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }else {
                                if(comp1 != 0 && comp2 != 0)
                                    endDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
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
                                param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

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

                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                    hostLabel.setText(type);

                    int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
                    batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                    tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
                    midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));

//                    summaryLinearFeeLayout.setVisibility(View.GONE); ////20180720 SINN NO NEED QR TAX
                    selectSummaryAlipayTAXReport(type,realm);
//                    setMeasureHGC();
                    setMeasureQr();
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_ALI, dateLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_ALI, timeLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_ALI, saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_ALI, saleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_ALI, voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_ALI, voidSaleAmountLabel.getText().toString());

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
                    int inV = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_ALI_BATCH_NUMBER_LAST, String.valueOf(inV));
                    inV = inV + 1;
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_ALI_BATCH_NUMBER, String.valueOf(inV));
//                    doPrinting(getBitmapFromView(settlementHgcLinearLayout));
                    doPrinting(getBitmapFromView(settlementLinearLayout));
                } else {
                    if(status == 1) {
                        deleteqrDB_VOID();
                        int inV = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_ALI_BATCH_NUMBER_LAST, String.valueOf(inV));
                        inV = inV + 1;
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_ALI_BATCH_NUMBER, String.valueOf(inV));
                        doPrinting(getBitmapFromView(settlementLinearLayout));
                    }else{
                        deleteqrDB_WAIT();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countDownTimerSettle = new CountDownTimer(2000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    System.out.printf("utility:: selectSettlementALIPAY onFinish AAAAAAAAAAAA settlementPosition = %d \n", settlementPosition);
                                    if (settlementPosition == 0) {      // Paul_20180709
                                    } else if (settlementPosition == 1) {
                                        statusLabel.setText("HeathCare ไม่มีข้อมูล");
                                    } else if (settlementPosition == 2) {
                                        statusLabel.setText("KTB OFFUS ไม่มีข้อมูล");   // Paul_20181028 Sinn merge version UAT6_0016
                                    } else if (settlementPosition == 3) {
                                        statusLabel.setText("WAY4 ไม่มีข้อมูล");
                                    } else if (settlementPosition == 4) {
                                        statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
                                    } else if (settlementPosition == 5) {
//                                            statusLabel.setText("QR ไม่มีข้อมูล");
                                        statusLabel.setText("โอนยอด");    ////20181025 SINN display wrong status.
                                    } else if (settlementPosition == 6) {
                                        statusLabel.setText("ALIPAY ไม่มีข้อมูล");
                                    } else if (settlementPosition == 7) {
                                        statusLabel.setText("WECHAT ไม่มีข้อมูล");
                                    } else {
                                    }
                                    if (settlementPosition == 0) {      // Paul_20180709
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDatabaseSaleOffline(); ////20181021 SINN settlement all  GHC Tomorow no need
                                    } else if (settlementPosition == 1) {      // Paul_20180709
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 2) {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 3) {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 4) {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 5) {
                                        countDownTimerSettle.cancel();        //20181021 SINN settlement all
                                        selectSettlementQRAll();
                                    } else if (settlementPosition == 6) {
                                        countDownTimerSettle.cancel();        ///20181021 SINN settlement all
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 7) {
                                        countDownTimerSettle.cancel();        ///20181021 SINN settlement all
                                        selectDataTransTempAllGAME(" ");
                                    } else {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        dialogSettlement.dismiss();
                                        Log.d(TAG, "success: " + settlementPosition);
//                                            finish();
                                        System.out.printf("utility:: Settle selectDataTransTempAll Success \n");
                                        if (typeInterface != null) {
                                            Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ยกเลิกการทำรายการ");  // Paul_20181020
                                            TerToPosSettlement();
                                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                @Override
                                                public void success() {
                                                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
                                            dialogSuccess_GotoMain.show();//K.GAME 20181018
                                            cdt.start();//K.GAME 20181018
                                        }
                                    }
                                }
                            }.start();
                        }
                    });
                }
            }
        });
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
                alipay_http = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_URL_ID); //20181114Jeff
                ALIPAY_CER_PATH = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_CERTI_ID); //20181115Jeff

                if (realm == null)
                    realm = Realm.getDefaultInstance();
                else{
                    realm = null;
                    realm = Realm.getDefaultInstance();
                }

                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("hostTypeCard", type).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
                String tmp, tmp2;

                cntSale = saleTemp.size();
                for (int i = 0; i < cntSale; i++) {
                    tmp = saleTemp.get(i).getAmt();
                    tmp2 = saleTemp.get(i).getAmtplusfee();
                    if(!tmp2.equals("null"))
                        tmp = delcomma(tmp2);
                    else
                        tmp = delcomma(tmp);
                    amountSale += Float.valueOf(tmp);
                    amountSale = floor(amountSale * 100.f + 0.5) / 100.f;
                }

                RealmResults<QrCode> voidTemp = realm.where(QrCode.class).equalTo( "hostTypeCard",type ).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
                cntVoid = voidTemp.size();
                for (int i = 0; i < cntVoid; i++) {
                    tmp = voidTemp.get(i).getAmt();
                    tmp2 = voidTemp.get(i).getAmtplusfee();
                    if(!tmp2.equals("null"))
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

                cardCountLabel.setText(String.valueOf(cntSale+cntVoid));
                cardAmountLabel.setText(String.valueOf(decimalFormat.format(amountSale)));
                dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTime));
                timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(dateTime));

//                cntSale = 0;  // only test
//                cntVoid = 0;  // only test

                if ((cntSale+cntVoid) > 0) {
                    status = 0;
                    endRecord = saleTemp.size()+voidTemp.size();
                    checkPage(endRecord);

                    if (endRecord < 20) {
                        if (endRecord == 0) {
                            settlementLister.onSuccess();
                        } else {
                            //compare date
                            int comp1, comp2;
                            if(saleTemp.size() > 0)
                                comp1 = Integer.parseInt(saleTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if(voidTemp.size()>0)
                                comp2 = Integer.parseInt(voidTemp.get(0).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if(comp1 < comp2) {
                                if(comp1 != 0 && comp2 != 0)
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }else {
                                if(comp1 != 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }

                            if(saleTemp.size() >0)
                                comp1 = Integer.parseInt(saleTemp.get(saleTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp1 = 0;

                            if(voidTemp.size() > 0)
                                comp2 = Integer.parseInt(voidTemp.get(voidTemp.size() - 1).getReqChannelDtm().substring(0, 10).replace("-", "").replace(":", "").replace(" ", ""));
                            else
                                comp2 = 0;

                            if(comp1 > comp2) {
                                if(comp1 != 0 && comp2 != 0)
                                    endDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
                                    startDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else
                                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                            }else {
                                if(comp1 != 0 && comp2 != 0)
                                    endDate = voidTemp.get(0).getReqChannelDtm().substring(0, 10);
                                else if(comp1 == 0 && comp2 != 0)
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
                                param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

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

                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3Label.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));

                    hostLabel.setText("WECHAT PAY");        // Paul_20190324

                    int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
                    batchLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                    tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
                    midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));

                    selectSummaryAlipayTAXReport(type,realm);
//                    setMeasureHGC();
                    setMeasureQr();
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_WEC, dateLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_WEC, timeLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_WEC, saleTotalLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_WEC, saleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_WEC, voidSaleCountLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_WEC, voidSaleAmountLabel.getText().toString());

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
                    int inV = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_WEC_BATCH_NUMBER_LAST, String.valueOf(inV));
                    inV = inV + 1;
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_WEC_BATCH_NUMBER, String.valueOf(inV));
                    doPrinting(getBitmapFromView(settlementLinearLayout));
                } else {
                    if(status == 1) {
                        deleteqrDB_VOID();
                        int inV = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_WEC_BATCH_NUMBER_LAST, String.valueOf(inV));
                        inV = inV + 1;
                        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_WEC_BATCH_NUMBER, String.valueOf(inV));
                        doPrinting(getBitmapFromView(settlementLinearLayout));
                    }else{
                        deleteqrDB_WAIT();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countDownTimerSettle = new CountDownTimer(2000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    System.out.printf("utility:: selectSettlementWECHAT onFinish AAAAAAAAAAAA settlementPosition = %d \n", settlementPosition);
                                    if (settlementPosition == 0) {      // Paul_20180709
                                    } else if (settlementPosition == 1) {
                                        statusLabel.setText("HeathCare ไม่มีข้อมูล");
                                    } else if (settlementPosition == 2) {
                                        statusLabel.setText("KTB OFFUS ไม่มีข้อมูล");   // Paul_20181028 Sinn merge version UAT6_0016
                                    } else if (settlementPosition == 3) {
                                        statusLabel.setText("WAY4 ไม่มีข้อมูล");
                                    } else if (settlementPosition == 4) {
                                        statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
                                    } else if (settlementPosition == 5) {
//                                            statusLabel.setText("QR ไม่มีข้อมูล");
                                        statusLabel.setText("โอนยอด");    ////20181025 SINN display wrong status.
                                    } else if (settlementPosition == 6) {
                                        statusLabel.setText("ALIPAY ไม่มีข้อมูล");
                                    } else if (settlementPosition == 7) {
                                        statusLabel.setText("WECHAT ไม่มีข้อมูล");
                                    } else {
                                    }
                                    if (settlementPosition == 0) {      // Paul_20180709
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDatabaseSaleOffline(); ////20181021 SINN settlement all  GHC Tomorow no need
                                    } else if (settlementPosition == 1) {      // Paul_20180709
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 2) {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 3) {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 4) {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 5) {
                                        countDownTimerSettle.cancel();        //20181021 SINN settlement all
                                        selectSettlementQRAll();
                                    } else if (settlementPosition == 6) {
                                        countDownTimerSettle.cancel();        ///20181021 SINN settlement all
                                        selectDataTransTempAllGAME(" ");
                                    } else if (settlementPosition == 7) {
                                        countDownTimerSettle.cancel();        ///20181021 SINN settlement all
                                        selectDataTransTempAllGAME(" ");
                                    } else {
                                        countDownTimerSettle.cancel();        // Paul_20180709
                                        dialogSettlement.dismiss();
                                        Log.d(TAG, "success: " + settlementPosition);
//                                            finish();
                                        System.out.printf("utility:: Settle selectDataTransTempAll Success \n");
                                        if (typeInterface != null) {
                                            Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ยกเลิกการทำรายการ");  // Paul_20181020
                                            TerToPosSettlement();
                                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                @Override
                                                public void success() {
                                                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
                                            dialogSuccess_GotoMain.show();//K.GAME 20181018
                                            cdt.start();//K.GAME 20181018
                                        }
                                    }
                                }
                            }.start();
                        }
                    });
                }
            }
        });
    }

    private void selectSettlementQRAll() {


//20181021 SINN settlement all
        if (realm == null) {
            System.out.printf("utility:: updateTransactionVoid 000000099 \n");
            Log.d(TAG, "1919_updateTransactionVoid");
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            }
        });
        realm.close();
        realm = null;

        realm = Realm.getDefaultInstance();
//END 20181021 SINN settlement all


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                // Paul_20190213
//// Paul_20181021 Start
//                System.out.printf("utility:: %s selectSettlementQRAll 000000001 hostflag = %s\n", TAG, hostflag);
//
//                Log.d(TAG, "selectDataTransTempAllGAME hostflag:" + hostflag);
//                if (hostflag.substring(0, 3).equals("TMS")) {
//                    Log.d("1919", "settlement TMS");
//                    hostflag = hostflag.replaceAll("TMS", "111");
//                    typeHost = "TMS";
//                    settlementPosition = 4;
//
//                } else if (hostflag.substring(3, 6).equals("POS")) {
//                    Log.d("1919", "settlement POS");
//                    hostflag = hostflag.replaceAll("POS", "222");
//                    typeHost = "POS";
//                    settlementPosition = 2;
//
//                } else if (hostflag.substring(6, 9).equals("EPS")) {
//                    Log.d("1919", "settlement EPS");
//                    hostflag = hostflag.replaceAll("EPS", "333");
//                    typeHost = "EPS";
//                    settlementPosition = 3;
//
//                } else if (hostflag.substring(9, 12).equals("GHC")) {    //   ||TMS|POS|EPS|GHC|QR
//                    Log.d("1919", "settlement GHC");
//                    hostflag = hostflag.replaceAll("GHC", "444");
//                    typeHost = "GHC";
//                    settlementPosition = 1;
//                } else if (hostflag.substring(12, 15).equals(" QR")) {
//                    Log.d("1919", "settlement  QR");
//                    hostflag = hostflag.replaceAll(" QR", "555");
//                    settlementPosition = 5;
//                    typeHost = "QR";
//
//                } else if (hostflag.substring(15, 18).equals("ALI")) {
//                    Log.d("1919", "settlement  ALI");
//                    hostflag = hostflag.replaceAll("ALI", "666");
//                    settlementPosition = 6;
//                    typeHost = "ALI";
//                } else if (hostflag.substring(18, 21).equals("WEC")) {
//                    Log.d("1919", "settlement  WEC");
//                    hostflag = hostflag.replaceAll("WEC", "777");
//                    settlementPosition = 7;
//                    typeHost = "WEC";
//                } else {
//                    settlementPosition = 1000;
//                }
//                System.out.printf("utility:: %s selectSettlementQRAll 000000002 hostflag = %s\n", TAG, hostflag);

// Paul_20181021 End

                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").equalTo("statusSuccess", "1").findAll(); // Paul_20181020
                Double amountSaleQr = 0.0;
                float amountVoidQr = 0;
                if (qrCode.size() > 0) {
                    status = 0;
                    for (int i = 0; i < qrCode.size(); i++) {
                        amountSaleQr += Double.valueOf(qrCode.get(i).getAmount().replaceAll(",", ""));
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");        // Paul_20190117 amount comma

//                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_MERCHANT_1).isEmpty())
//                        merchantName1Label.setText( Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_MERCHANT_1));
//                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_MERCHANT_2).isEmpty())
//                        merchantName2Label.setText( Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_MERCHANT_2));
//                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_MERCHANT_3).isEmpty())
//                        merchantName3Label.setText( Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_MERCHANT_3));
//
//
//                    Date date = new Date();
//                    voidSaleCountLabel.setText("0");
//                    voidSaleAmountLabel.setText(String.format("%.2f", 0.0));
//                    saleCountLabel.setText(qrCode.size() + "");
//                    saleTotalLabel.setText(String.format("%.2f", amountSaleQr));
//                    cardCountLabel.setText(qrCode.size() + "");
//                    cardAmountLabel.setText(String.format("%.2f", amountSaleQr));
//                    dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
//                    timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
//                    hostLabel.setText("KTB QR");
////                    batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
//                    int batch = Integer.parseInt( Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_QR_BATCH_NUMBER));
//                    batchLabel.setText( CardPrefix.calLen(String.valueOf(batch), 6));
//                    Preference.getInstance(MenuSettlementActivity.this).setValueString( Preference.KEY_QR_BATCH_NUMBER, String.valueOf((batch + 1)));
//                    tidLabel.setText( Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_TERMINAL_ID_POS));
//                    midLabel.setText( Preference.getInstance(MenuSettlementActivity.this).getValueString( Preference.KEY_MERCHANT_ID_POS));
//---------------------------------------------------------------------------------------------------------------------------------------------------
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
                        merchantName1HgcLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
                        merchantName2HgcLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
                    if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
                        merchantName3HgcLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));


                    Date date = new Date();
                    voidSaleCountHgcLabel.setText("0");
                    voidSaleAmountHgcLabel.setText(String.format("%.2f", 0.0));
                    saleCountHgcLabel.setText(qrCode.size() + "");
//                    saleTotalHgcLabel.setText(String.format("%.2f", amountSaleQr));
                    saleTotalHgcLabel.setText(decimalFormat.format(amountSaleQr));     // Paul_20190117 add to decimalFormat.format
                    cardCountHgcLabel.setText(qrCode.size() + "");
//                    cardAmountHgcLabel.setText(String.format("%.2f", amountSaleQr));
                    cardAmountHgcLabel.setText(decimalFormat.format(amountSaleQr));     // Paul_20190117 add to decimalFormat.format

                    dateHgcLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    timeHgcLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                    hostHgcLabel.setText("KTB QR");
//                    batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
                    int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_QR_BATCH_NUMBER));
                    batchHgcLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_QR_BATCH_NUMBER, String.valueOf((batch + 1)));
//                    tidHgcLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
//                    midHgcLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                    tidHgcLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_QR_TERMINAL_ID));
                    midHgcLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_QR_MERCHANT_ID));

//----------------------------------------------------------------------------------------------------------------------------------------------------

//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_QR, dateLabel.getText().toString());

                    Log.d(TAG, "KEY_SETTLE_DATE_QR 2 =" + dateHgcLabel.getText().toString());
//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_QR, timeLabel.getText().toString());
//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR, saleTotalLabel.getText().toString());
//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_QR, saleCountLabel.getText().toString());
//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_QR, voidSaleCountLabel.getText().toString());
//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR, voidSaleAmountLabel.getText().toString());
//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_QR, CardPrefix.calLen(String.valueOf(batch), 6));

                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_QR, dateHgcLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_QR, timeHgcLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_QR, saleTotalHgcLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_QR, saleCountHgcLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_QR, voidSaleCountHgcLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_QR, voidSaleAmountHgcLabel.getText().toString());
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_QR, CardPrefix.calLen(String.valueOf(batch), 6));    // Paul_20181120 please no remark last settlement reprint problem


//                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_QR_BATCH_NUMBER, CardPrefix.calLen(String.valueOf(batch+1), 6));
                    Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_QR_BATCH_NUMBER, CardPrefix.calLen(String.valueOf(batch + 1), 6));  ////SINN 20181025

                    // setMeasureQr();
                    setMeasureHGC();

                    //summaryLinearFeeLayout.setVisibility(View.GONE);   //20180723 Fix QR TAX on settlement all
                    int height = settlementLinearLayout.getHeight();
                    Log.d("SINN", "height=" + String.valueOf(height));
                    if(!isSettlementAll)
                    {
                        statusLabel.setText("Settlement สำเร็จ");
                    }

                } else {
                    status = 1;
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (status == 0) {
                    deleteQrAll();
                    statusLabel.setText("Settlement QR");   //20181025 SINN display wrong status.
                    doPrinting(getBitmapFromView(settlementLinearLayout));

                    doPrinting(getBitmapFromView(settlementHgcLinearLayout));
                } else {

                    deleteQrAll();  //20181112 SINN KTB order QR have or no transaction success but press settlement will clear un success also.
                    okBtn.setVisibility(View.GONE);
//                    progressBarStatus.setVisibility(View.GONE);  //20181119
                    statusLabel.setText("QR ไม่มีข้อมูล");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if ((NormalSettlementFlg == 1) && isSettlementAll)       // Paul_20180713
//                            {
//                                if (settlementPosition > 4) {
//                                    settlementPosition++;
//                                }
//                            }
                            okBtn.setVisibility(View.GONE);
//                            progressBarStatus.setVisibility(View.GONE); //20181119

// Paul_20190213 Start
                            System.out.printf("utility:: %s selectSettlementQRAll 000000001 hostflag = %s\n", TAG, hostflag);

                            Log.d(TAG, "selectDataTransTempAllGAME hostflag:" + hostflag);
                            if (hostflag.substring(0, 3).equals("TMS")) {
                                Log.d("1919", "settlement TMS");
                                hostflag = hostflag.replaceAll("TMS", "111");
                                typeHost = "TMS";
                                settlementPosition = 4;

                            } else if (hostflag.substring(3, 6).equals("POS")) {
                                Log.d("1919", "settlement POS");
                                hostflag = hostflag.replaceAll("POS", "222");
                                typeHost = "POS";
                                settlementPosition = 2;

                            } else if (hostflag.substring(6, 9).equals("EPS")) {
                                Log.d("1919", "settlement EPS");
                                hostflag = hostflag.replaceAll("EPS", "333");
                                typeHost = "EPS";
                                settlementPosition = 3;

                            } else if (hostflag.substring(9, 12).equals("GHC")) {    //   ||TMS|POS|EPS|GHC|QR
                                Log.d("1919", "settlement GHC");
                                hostflag = hostflag.replaceAll("GHC", "444");
                                typeHost = "GHC";
                                settlementPosition = 1;
                            } else if (hostflag.substring(12, 15).equals(" QR")) {
                                Log.d("1919", "settlement  QR");
                                hostflag = hostflag.replaceAll(" QR", "555");
                                settlementPosition = 5;
                                typeHost = "QR";

                            } else if (hostflag.substring(15, 18).equals("ALI")) {
                                Log.d("1919", "settlement  ALI");
                                hostflag = hostflag.replaceAll("ALI", "666");
                                settlementPosition = 6;
                                typeHost = "ALI";
                            } else if (hostflag.substring(18, 21).equals("WEC")) {
                                Log.d("1919", "settlement  WEC");
                                hostflag = hostflag.replaceAll("WEC", "777");
                                settlementPosition = 7;
                                typeHost = "WEC";
                            } else {
                                settlementPosition = 1000;
                            }
                            System.out.printf("utility:: %s selectSettlementQRAll 000000002 hostflag = %s\n", TAG, hostflag);

// Paul_20180706
//                            if (settlementPosition == 1) {
//                                if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
//                                    statusLabel.setText("HeathCare ไม่มีข้อมูล");
//                            } else if (settlementPosition == 2) {
//                                statusLabel.setText("KTB OFFUS ไม่มีข้อมูล");       // Paul_20181028 Sinn merge version UAT6_0016
//                            } else if (settlementPosition == 3) {
//                                statusLabel.setText("BASE24 EPS ไม่มีข้อมูล");
//                            } else if (settlementPosition == 4) {
//                                statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
//                            } else if (settlementPosition == 5) {
//                                statusLabel.setText("QR ไม่มีข้อมูล");
//                            } else if (settlementPosition == 6) {
//                                statusLabel.setText("ALIPAY ไม่มีข้อมูล");
//                            } else if (settlementPosition == 7) {
//                                statusLabel.setText("WECHAT ไม่มีข้อมูล");
//                            } else {
////                                statusLabel.setText("Settlement สำเร็จ");
//                            }
                            new CountDownTimer(2000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {

//                                    settlementPosition++;
// Paul_20181021 Start
//                                    //20181021 SINN settlement all
//                                    if(hostflag.equalsIgnoreCase("111222333444555ALI"))  ////  set for next ali settlement
//                                    settlementPosition++;
//                                    else
//                                        settlementPosition=100;   //  set for select one QR
// Paul_20181021 End
                                    Log.d(TAG, "QRALL settlementPosition=" + String.valueOf(settlementPosition) + " hostflag=" + hostflag);

// Paul_20180706
                                    System.out.printf("utility:: selectSettlementQRAll onFinish AAAAAAAAAAAA settlementPosition = %d \n", settlementPosition);
                                    if (settlementPosition == 2) {
                                        typeHost = "POS";
                                        okBtn.setVisibility(View.GONE);
                                        dialogSettlement.show();
//                                        selectDataTransTempAll("POS");
//                                        hostflag = "111POS3334445555666";
                                        selectDataTransTempAllGAME(" ");

                                    } else if (settlementPosition == 3) {
                                        statusLabel.setText("Settlement WAY4");
//                                        selectDataTransTempAll("EPS");
//                                        hostflag = "111222EPS444555666";
                                        selectDataTransTempAllGAME(" ");

                                    } else if (settlementPosition == 4) {
                                        statusLabel.setText("Settlement KTB ONUS");
//                                        selectDataTransTempAll("TMS");
//                                        hostflag = "111222333TMS555666";
                                        selectDataTransTempAllGAME(" ");

                                    } else if (settlementPosition == 5) {
                                        selectSettlementQRAll();
                                    } else if (settlementPosition == 6) {           // Paul_20190211
//                                        countDownTimerSettle.cancel();
//                                        if(aliTemp.size() > 0) {
//                                            statusLabel.setText("Settlement Alipay");
//                                        }else {
//                                            statusLabel.setText("ALIPAY ไม่มีข้อมูล");
//                                        }
                                        selectSettlementALIPAY();
                                    } else if (settlementPosition == 7) {   // Paul_20190211
//                                        statusLabel.setText(" ");
//                                        countDownTimerSettle.cancel();
//                                        if(wecTemp.size() > 0) {
//                                            statusLabel.setText("Settlement Wechat");
//                                        }else{
//                                            statusLabel.setText("WECHAT ไม่มีข้อมูล");
//                                        }
                                        selectSettlementWECHAT();
                                    }
/*
                                    else if (settlementPosition == 6) {
                                        selectSettlementALIPAY();

//                                        selectDataTransTempAllGAME(" ");
                                    }
                                    else if (settlementPosition == 7) {
                                        selectDataTransTempAllGAME(" ");
                                    }
*/
                                    else {
                                        dialogSettlement.dismiss();
                                        Log.d(TAG, "success: " + settlementPosition);
//                                        finish();//K.GAME 181012 ปิด ของจริง
                                        System.out.printf("utility:: Settle selectSettlementQRAll onSuccess Success 001 \n");
                                        if (typeInterface != null) {
                                            TerToPosSettlement();
                                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                                @Override
                                                public void success() {
                                                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                }
                                            });
//                                        } else finish(); //K.GAME 20181018
                                        } else {
                                            dialogSuccess_GotoMain.show();//K.GAME 20181018
                                            cdt.start();//K.GAME 20181018
                                        }
                                    }
                                }
                            }.start();
                        }
                    });
                }
            }
        });

    }



    private void deleteQrAll() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo("hostTypeCard", "QR").findAll(); // Paul_20181020
                qrCode.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }
        });
    }

    private void deleteqrDB_VOID() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo( "hostTypeCard", type).equalTo("voidflag", "Y").findAll(); // Paul_20181020
                qrCode.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }
        });
    }

    private void deleteqrDB_WAIT() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<QrCode> qrCode = realm.where(QrCode.class).equalTo( "hostTypeCard", type).equalTo("respcode", "1").findAll(); // Paul_20181020
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

    private void dialogSuccess_GotoMain() {
        dialogSuccess_GotoMain = new Dialog(this);
        dialogSuccess_GotoMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess_GotoMain.setContentView(R.layout.dialog_custom_success_gotomain);
        dialogSuccess_GotoMain.setCancelable(false);
        dialogSuccess_GotoMain.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialogSuccess_GotoMain.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

//        waitingImagegotoMain = dialogSuccess_GotoMain.findViewById(R.id.waitingImage);
        msgLabelgotoMain = dialogSuccess_GotoMain.findViewById(R.id.msgLabel);


//        TextView msgLabel = dialogSuccess_GotoMain.findViewById(R.id.msgLabel);
//        Button btn_gotoMain = dialogSuccess_GotoMain.findViewById(R.id.btn_gotoMain);
        cdt = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Tick
            }

            public void onFinish() {
                // Finish

                if (logoutInterface!=null){
                    Intent intent = new Intent(MenuSettlementActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }else {
                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }


            }
        };


        btn_gotoMain = dialogSuccess_GotoMain.findViewById(R.id.btn_gotoMain);
        btn_gotoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdt.cancel();

                if (logoutInterface!=null){
                    Intent intent = new Intent(MenuSettlementActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }else {
                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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

    private void customDialogSettlement() {
        dialogSettlement = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180921 New UI สวยๆ
        View view = dialogSettlement.getLayoutInflater().inflate(R.layout.dialog_custom_settlement, null);//K.GAME 180921 New UI สวยๆ
        dialogSettlement.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180921 New UI สวยๆ
        dialogSettlement.setContentView(view);//K.GAME 180921 New UI สวยๆ
        dialogSettlement.setCancelable(false);//K.GAME 180921 New UI สวยๆ

//        dialogSettlement = new Dialog(this);
//        dialogSettlement.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogSettlement.setCancelable(false);
//        dialogSettlement.setContentView(R.layout.dialog_custom_settlement);
//        dialogSettlement.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogSettlement.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        progressBarStatus = dialogSettlement.findViewById(R.id.progressBarStatus);
        statusLabel = dialogSettlement.findViewById(R.id.statusLabel);
        okBtn = dialogSettlement.findViewById(R.id.okBtn);
        closeImage = dialogSettlement.findViewById(R.id.closeImage);
        okBtn.setVisibility(View.GONE);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "settlementPosition : " + settlementPosition);
//                progressBarStatus.setVisibility(View.VISIBLE); //20181119
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        settlementPosition++;
// Paul_20180706
                        if (settlementPosition == 2) {
                            typeHost = "POS";
                            okBtn.setVisibility(View.GONE);
                            dialogSettlement.show();
                            //selectDataTransTempAll("POS");
                        } else if (settlementPosition == 3) {
                            statusLabel.setText("Settlement WAY4");
                            typeHost = "EPS";
                            //selectDataTransTempAll("EPS");
                        } else if (settlementPosition == 4) {
                            statusLabel.setText("Settlement KTB ONUS");
                            typeHost = "TMS";
                            // selectDataTransTempAll("TMS");
                        } else if (settlementPosition == 5) {
                            typeHost = "QR";   ////20180720 SINN NO NEED QR TAX
                            // selectSettlementQRAll();
                        } else {
                            dialogSettlement.dismiss();
                            Log.d(TAG, "success: " + settlementPosition);
//                            finish();//K.GAME 181012
                            System.out.printf("utility:: Settle onFinish Success \n");
                            if (typeInterface != null) {
                                Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ยกเลิกการทำรายการ");  // Paul_20181020
                                TerToPosSettlement();
                                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                    @Override
                                    public void success() {
                                        Utility.customDialogAlertAutoClear();
                                        Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
                                dialogSuccess_GotoMain.show();//K.GAME 20181018
                                cdt.start();//K.GAME 20181018
                            }
                        }
                    }
                }.start();

            }
        });
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSettlement.dismiss();
            }
        });
    }

    private void setViewSettlementHGCOff() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hgcSaleView = inflater.inflate(R.layout.view_slip_sale_hgc, null);
        settlementHgcOffLinearLayout = hgcSaleView.findViewById(R.id.settlementLinearLayout);
        dateHgcOffLabel = hgcSaleView.findViewById(R.id.dateLabel);
        timeHgcOffLabel = hgcSaleView.findViewById(R.id.timeLabel);
        midHgcOffLabel = hgcSaleView.findViewById(R.id.midLabel);
        tidHgcOffLabel = hgcSaleView.findViewById(R.id.tidLabel);
        systrcOffLabel = hgcSaleView.findViewById(R.id.systrcLabel);
        traceNoOffLabel = hgcSaleView.findViewById(R.id.traceNoLabel);
        typeSaleOffLabel = hgcSaleView.findViewById(R.id.typeSaleLabel);
        cardNoOffLabel = hgcSaleView.findViewById(R.id.cardNoLabel);
        nameEngOffLabel = hgcSaleView.findViewById(R.id.nameEngLabel);
        apprCodeOffLabel = hgcSaleView.findViewById(R.id.apprCodeLabel);
        comCodeOffLabel = hgcSaleView.findViewById(R.id.comCodeLabel);
        batchHgcOffLabel = hgcSaleView.findViewById(R.id.batchLabel);
        amountOffLabel = hgcSaleView.findViewById(R.id.amountLabel);
        merchantName1HgcOffLabel = hgcSaleView.findViewById(R.id.merchantName1Label);
        merchantName2HgcOffLabel = hgcSaleView.findViewById(R.id.merchantName2Label);
        merchantName3HgcOffLabel = hgcSaleView.findViewById(R.id.merchantName3Label);
    }

    private void setMeasureHGCOff() {
        hgcSaleView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleView.layout(0, 0, hgcSaleView.getMeasuredWidth(), hgcSaleView.getMeasuredHeight());
    }

    private void setDataSlipOffline() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        DateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00"); // Paul_20180711_new
        dateHgcOffLabel.setText(dateFormat.format(date));
        timeHgcOffLabel.setText(dateTimeFormat.format(date));
        midHgcOffLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        tidHgcOffLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        // systrcOffLabel.setText( CardPrefix.calLen( Preference.getInstance(this).getValueString( Preference.KEY_TRACE_NO_GHC), 6));
        //20180827  SINN Slip Sele offline on Settlement tansaction systrc is wrong.
        String szTrace = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC);
        int inTrace = Integer.valueOf(szTrace) - 1;
        systrcOffLabel.setText(CardPrefix.calLen(String.valueOf(inTrace), 6));
        //20180827  END SINN Slip Sele offline on Settlement tansaction systrc is wrong.
        traceNoOffLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL), 6));
        if (databaseTransTemp.getTypeSale().substring(0, 1).equalsIgnoreCase("1")) {
            typeSaleOffLabel.setText("ผู้ป่วยนอกทั่วไป");
        } else if (databaseTransTemp.getTypeSale().substring(0, 1).equalsIgnoreCase("2")) {
            typeSaleOffLabel.setText("หน่วยไตเทียม");
        } else {
            typeSaleOffLabel.setText("หน่วยรังสีผู้เป็นมะเร็ง");
        }
        // Paul_20180713
        String szMSG = null;
        String idCardCd = databaseTransTemp.getCardNo();
        if (idCardCd.length() < 13) {
            int i;
            for (i = idCardCd.length(); i < 13; i++) {
                idCardCd += " ";
            }
        }
        szMSG = idCardCd.substring(0, 1) + " " + idCardCd.substring(1, 4) + "X" + " " + "XXXX" + idCardCd.substring(9, 10) + " " + idCardCd.substring(10, 12) + " " + idCardCd.substring(12, 13);
        cardNoOffLabel.setText(szMSG);
/*
        if (databaseTransTemp.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            cardNoOffLabel.setText(databaseTransTemp.getCardNo());
        } else if (databaseTransTemp.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            cardNoOffLabel.setText(databaseTransTemp.getIdCard());
        } else {
            cardNoOffLabel.setText(databaseTransTemp.getCardNo());
        }
*/
        nameEngOffLabel.setText(databaseTransTemp.getEngFName());
//        comCodeOffLabel.setText("HCG13814");
        comCodeOffLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAG_1001_HC));
//        batchHgcLabel.setText( CardPrefix.calLen( Preference.getInstance(this).getValueString( Preference.KEY_BATCH_NUMBER_GHC), 6));

        //20180827  SINN Slip Sele offline on Settlement tansaction batch is wrong.
        batchHgcOffLabel.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcOffLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));   // Paul_20180723

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcOffLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));   // Paul_20180723

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcOffLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));   // Paul_20180723


//        amountOffLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(databaseTransTemp.getAmount().replaceAll(",", "")))));
      // //SINN 20181113  slip_pattern_amount_normal  settlement amount sale no need *
        amountOffLabel.setText(getString(R.string.slip_pattern_amount_normal, decimalFormat.format(Double.valueOf(databaseTransTemp.getAmount().replaceAll(",", "")))));
    }

    private void reportSummaryFeeView() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reportSummaryFeeView = inflater.inflate(R.layout.view_silp_report_fee_settlement, null);
        summaryLinearFeeLayout = reportSummaryFeeView.findViewById(R.id.summaryLinearLayout);
        merchantName1FeeLabel = reportSummaryFeeView.findViewById(R.id.merchantName1Label);
        merchantName2FeeLabel = reportSummaryFeeView.findViewById(R.id.merchantName2Label);
        merchantName3FeeLabel = reportSummaryFeeView.findViewById(R.id.merchantName3Label);
        dateFeeLabel = reportSummaryFeeView.findViewById(R.id.dateLabel);
        timeFeeLabel = reportSummaryFeeView.findViewById(R.id.timeLabel);
        midFeeLabel = reportSummaryFeeView.findViewById(R.id.midLabel);
        tidFeeLabel = reportSummaryFeeView.findViewById(R.id.tidLabel);
        batchFeeLabel = reportSummaryFeeView.findViewById(R.id.batchLabel);
        hostFeeLabel = reportSummaryFeeView.findViewById(R.id.hostLabel);
        saleCountFeeLabel = reportSummaryFeeView.findViewById(R.id.saleCountLabel);
        saleTotalFeeLabel = reportSummaryFeeView.findViewById(R.id.saleTotalLabel);
        voidSaleCountFeeLabel = reportSummaryFeeView.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountFeeLabel = reportSummaryFeeView.findViewById(R.id.voidSaleAmountLabel);
        cardCountFeeLabel = reportSummaryFeeView.findViewById(R.id.cardCountLabel);
        cardAmountFeeLabel = reportSummaryFeeView.findViewById(R.id.cardAmountLabel);
        taxIdFeeLabel = reportSummaryFeeView.findViewById(R.id.taxIdLabel);

    }

    private void setMeasureFeeSummary() {
        reportSummaryFeeView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        reportSummaryFeeView.layout(0, 0, reportSummaryFeeView.getMeasuredWidth(), reportSummaryFeeView.getMeasuredHeight());
    }

    private void selectSummaryTAXReport(String typeHost, Realm realm) {
        Double totalVoid = 0.00;
        Double totalSale = 0.00;
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
            hostFeeLabel.setText("KTB OFFUS");
        } else {
            hostFeeLabel.setText("WAY4");
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            totalVoid += Double.valueOf(transTempVoid.get(i).getFee());
        }
        if (typeHost.equalsIgnoreCase("POS")) {
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS, String.valueOf(totalSale));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_POS, String.valueOf(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        } else {
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS, String.valueOf(totalSale));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_EPS, String.valueOf(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        }
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFeeLabel.setText(dateFormat.format(date));
        timeFeeLabel.setText(timeFormat.format(date));
        /*switch (typeHost) {
            case "POS":
                midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)), 6));
                hostLabel.setText("KTB Off US");
                break;
            case "EPS":
                midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)), 6));
                hostLabel.setText("WAY4");
                break;
            default:
                midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
                hostLabel.setText("KTB ONUS");
                break;
        }*/
        taxIdFeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TAX_ID));
        saleCountFeeLabel.setText(String.valueOf(transTempSale.size()));
        saleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountFeeLabel.setText(transTempVoid.size() + "");
        voidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        cardCountFeeLabel.setText(countAll + "");
        cardAmountFeeLabel.setText(decimalFormat.format(totalSale));
    }

    // Paul_20181219
    private void selectSummaryAlipayTAXReport(String typeHost, Realm realm) {
        Double totalVoid = 0.00;
        Double totalSale = 0.00;
        System.out.printf("utility:: %s selectSummaryAlipayTAXReport 0001 \n",TAG);
//        dialogLoading.show();
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        final RealmResults<QrCode> transTempSale = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();

        final RealmResults<QrCode> transTempVoid = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            if(transTempSale.get(i).getFee().equals("null")) //Add Jeff 20190110
                totalSale += 0.00;
            else
                totalSale += Double.valueOf(transTempSale.get(i).getFee());
        }
        System.out.printf("utility:: %s , totalSale = %f \n",TAG,totalSale);

        if (typeHost.equalsIgnoreCase("ALIPAY")) {
            hostFeeLabel.setText("ALIPAY");
        } else {
            hostFeeLabel.setText("WECHAT PAY");     // Paul_20190324
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            if(transTempVoid.get(i).getFee().equals("null")) //Add Jeff 20190110
                totalVoid += 0.00;
            else
                totalVoid += Double.valueOf(transTempVoid.get(i).getFee());
        }
        System.out.printf("utility:: %s totalVoid = %f \n",TAG,totalVoid);

        if(totalSale == 0.00 && totalVoid == 0.00)
            summaryLinearFeeLayout.setVisibility(View.GONE);
        else
            summaryLinearFeeLayout.setVisibility(View.VISIBLE);

        if (typeHost.equalsIgnoreCase("ALIPAY")) {
            System.out.printf("utility:: %s selectSummaryAlipayTAXReport 0002 \n",TAG);

//            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_ALI, String.valueOf(totalSale));
//            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_ALI, String.valueOf(totalVoid));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_ALI, decimalFormat.format(totalSale));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_ALI, decimalFormat.format(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALI_BATCH_NUMBER));
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        } else {
            System.out.printf("utility:: %s selectSummaryAlipayTAXReport 0003 \n",TAG);
//            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_WEC, String.valueOf(totalSale));
//            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_WEC, String.valueOf(totalVoid));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_SALE_FEE_WEC, decimalFormat.format(totalSale));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_VOID_FEE_WEC, decimalFormat.format(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WEC_BATCH_NUMBER));
            batchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        }
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFeeLabel.setText(dateFormat.format(date));
        timeFeeLabel.setText(timeFormat.format(date));
        taxIdFeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TAX_ID));
        saleCountFeeLabel.setText(String.valueOf(transTempSale.size()));
        saleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        voidSaleCountFeeLabel.setText(transTempVoid.size() + "");
        voidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        cardCountFeeLabel.setText(countAll + "");
        cardAmountFeeLabel.setText(decimalFormat.format(totalSale));
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
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_POS, String.valueOf(totalSale));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_POS, String.valueOf(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)) - 1;
            NormalbatchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        } else {
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_SALE_EPS, String.valueOf(totalSale));
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TAX_FEE_VOID_EPS, String.valueOf(totalVoid));
            int batch = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)) - 1;
            NormalbatchFeeLabel.setText(CardPrefix.calLen(String.valueOf(batch), 6));
        }
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            NormalmerchantName1FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_1));
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            NormalmerchantName2FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_2));
        if (!Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            NormalmerchantName3FeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_3));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        NormaldateFeeLabel.setText(dateFormat.format(date));
        NormaltimeFeeLabel.setText(timeFormat.format(date));
        /*switch (typeHost) {
            case "POS":
                midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
                tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS)), 6));
                hostLabel.setText("KTB Off US");
                break;
            case "EPS":
                midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
                tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS)), 6));
                hostLabel.setText("BASE24 EPS");
                break;
            default:
                midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
                tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
                batchLabel.setText(CardPrefix.calLen(String.valueOf(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS)), 6));
                hostLabel.setText("KTB ONUS");
                break;
        }*/
        NormaltaxIdFeeLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TAX_ID));
        NormalsaleCountFeeLabel.setText(String.valueOf(transTempSale.size()));
        NormalsaleTotalFeeLabel.setText(decimalFormat.format(totalSale));
        NormalvoidSaleCountFeeLabel.setText(transTempVoid.size() + "");
        NormalvoidSaleAmountFeeLabel.setText(decimalFormat.format(totalVoid));
        countAll = transTempSale.size() + transTempVoid.size();
        NormalcardCountFeeLabel.setText(countAll + "");
        NormalcardAmountFeeLabel.setText(decimalFormat.format(totalSale));
    }


    public void setDataSettlementAndSendHGC() {
        if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
            statusLabel.setText("HeathCare ไม่มีข้อมูล");


        // Paul_20180726
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                HOST_CARD = "GHC";
                DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                invoiceNumber = CardPrefix.getInvoice(MenuSettlementActivity.this, "GHC");
                String traceIdNo;
                int payCount = 0;
                float amountAll = 0;
                int voidCount = 0;
                float amountVoidAll = 0;
                traceIdNo = CardPrefix.geTraceId(MenuSettlementActivity.this, HOST_CARD);
                RealmResults<TransTemp> transTempVoidFlag = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("ghcoffFlg", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
                if (transTempVoidFlag.size() != 0) {
                    payCount = transTempVoidFlag.size();
                    for (int i = 0; i < transTempVoidFlag.size(); i++) {
                        amountAll += Double.valueOf(transTempVoidFlag.get(i).getAmount().replaceAll(",", ""));
                    }
                }
                RealmResults<TransTemp> transTempVoidYFlag = realm.where(TransTemp.class).equalTo("voidFlag", "Y").equalTo("ghcoffFlg", "N").equalTo("hostTypeCard", HOST_CARD).findAll();
                if (transTempVoidYFlag.size() != 0) {
                    voidCount = transTempVoidYFlag.size();
                    for (int i = 0; i < transTempVoidYFlag.size(); i++) {
                        amountVoidAll += Double.valueOf(transTempVoidYFlag.get(i).getAmount().replaceAll(",", ""));
                    }
                }


                String nii = "";
                nii = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_NII_GHC);
                String batchNumber = CardPrefix.getBatch(MenuSettlementActivity.this, HOST_CARD);
                MERCHANT_NUMBER = CardPrefix.getMerchantId(MenuSettlementActivity.this, HOST_CARD);
                TERMINAL_ID = CardPrefix.getTerminalId(MenuSettlementActivity.this, HOST_CARD);

                int countPayAll = (payCount + voidCount);

                String msgLen = "00000121";
                String terVer = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_VERSION);
                String msgVer = "0008";
                String tranCode = "6012";
                String batchNo = CardPrefix.calLen(CardPrefix.getBatch(MenuSettlementActivity.this, HOST_CARD), 8);
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
                mBlockDataSend[3 - 1] = "920000";
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
                mBlockDataSend[62 - 1] = getLength62(String.valueOf(Utility.calNumTraceNo(invoiceNumber).length())) + BlockCalculateUtil.getHexString(Utility.calNumTraceNo(invoiceNumber));
                mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(payCount, amountPayAllToStr);
                Log.d(TAG, "setDataSettlementAndSendTMS: " + mBlockDataSend[63 - 1]);
                TPDU = CardPrefix.getTPDU(MenuSettlementActivity.this, HOST_CARD);
                packageAndSend(TPDU, "0500", mBlockDataSend);
                Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
                setDataViewHGC(amountAll, payCount, amountVoidAll, voidCount);
            }
        });
    }

    private void setDataViewHGC(float amountAll, int payCount, float amountVoidAll, int voidCount) {
        System.out.printf("utility:: setDataViewHGC amountAll= %f , payCount = %d , amountVoidAll = %f , voidCount = %d \n", amountAll, payCount, amountVoidAll, voidCount);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        String batchOn = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
// Paul_20180808    Start
//        merchantName1Label = qrView.findViewById( R.id.merchantName1Label);
//        merchantName2Label = qrView.findViewById( R.id.merchantName2Label);
//        merchantName3Label = qrView.findViewById( R.id.merchantName3Label);

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));   // Paul_20180723

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));   // Paul_20180723

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));   // Paul_20180723
// Paul_20180808    End

        dateHgcLabel.setText(dateFormat.format(date));
        timeHgcLabel.setText(dateTimeFormat.format(date));
        midHgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        tidHgcLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        batchHgcLabel.setText(CardPrefix.calLen(batchOn, 6));
        //hostHgcLabel.setText("HGC");
        hostHgcLabel.setText("HEARTH CARE");    // Paul_20180720
        saleCountHgcLabel.setText(payCount + "");
        saleTotalHgcLabel.setText(decimalFormat.format(amountAll));
        voidSaleCountHgcLabel.setText(voidCount + "");
        voidSaleAmountHgcLabel.setText(decimalFormat.format(amountVoidAll));
        cardCountHgcLabel.setText((payCount + voidCount) + "");
        cardAmountHgcLabel.setText(decimalFormat.format(amountAll));
        setMeasureHGC();


        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_DATE_GHC, dateHgcLabel.getText().toString());
        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_TIME_GHC, timeHgcLabel.getText().toString());
        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_TOTAL_GHC, saleTotalHgcLabel.getText().toString());
        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_SALE_COUNT_GHC, saleCountHgcLabel.getText().toString());
        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_COUNT_GHC, voidSaleCountHgcLabel.getText().toString());
        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_VOID_TOTAL_GHC, voidSaleAmountHgcLabel.getText().toString());
        Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_SETTLE_BATCH_GHC, CardPrefix.calLen(String.valueOf(batchHgcLabel.getText()), 6));
//////END 20180720 SINN last reprint settlement GHC


    }
//
//    // Paul_20180706
//    private void selectDatabaseAllSettlement() {
//        Step = 0;
//        selectDataTransTemp("GHC");
//        i++;
//        databaseTransTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").equalTo("voidFlag", "N").equalTo("ghcoffFlg", "Y").findFirst();
//        Log.d(TAG, "selectDatabaseSaleOffline: I = " + i);
//        if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
//            statusLabel.setText("HeathCare ไม่มีข้อมูล");
//        if (databaseTransTemp != null && transTemp.size() >= i) {
//            setDataSlipOffline();
//            sendDataSaleOffline();
//            Step = 1;           // Paul_20180709
//            Log.d(TAG, "selectDatabaseSaleOffline: if = " + i);
//        } else {
//            if (dialog != null)
//                dialog.dismiss();
//            Log.d(TAG, "selectDatabaseSaleOffline: else = " + i);
//            if (transTemp.size() > 0) {
//                dialogWaiting.show();
//                Step = 0;               // Paul_20180709
////                selectDatabaseSaleOffline();
////                settlementPosition++;                                // Paul_20180707 // Paul_20180709
//                setDataSettlementAndSendHGC();                // Paul_20180707
//                Step = 0;       // Paul_20180709
//            } else {
//                if (dialog != null)
//                    dialog.dismiss();
//            }
//        }
//
//        if (!isSettlementAll) {
//            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            });
//        } else {
//
//            countDownTimerSettle = new CountDownTimer(2000, 1000) {     // Paul_20180711
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @Override
//                public void onFinish() {
//                    if (Step != 1) {     // Paul_20180709
//                        settlementPosition++;
//                    }
//                    okBtn.setVisibility(View.GONE);
//                    progressBarStatus.setVisibility(View.GONE);
//                    System.out.printf("utility:: selectDatabaseAllSettlement onFinish BBBBBBBBBBBBBBBBBBBB settlementPosition = %d \n", settlementPosition);
//// Paul_20180706
//                    if (settlementPosition == 0) {      // Paul_20180709
//                    } else if (settlementPosition == 1) {
//                        if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
//                            statusLabel.setText("HeathCare ไม่มีข้อมูล");
//                    } else if (settlementPosition == 2) {
//                        statusLabel.setText("KTB offus ไม่มีข้อมูล");
//                    } else if (settlementPosition == 3) {
//                        statusLabel.setText("BASE24 EPS ไม่มีข้อมูล");
//                    } else if (settlementPosition == 4) {
//                        statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
//                    } else if (settlementPosition == 5) {
//                        statusLabel.setText("QR ไม่มีข้อมูล");
//                    } else {
//                        statusLabel.setText("Settlement สำเร็จ");
//                    }
//                    if (settlementPosition == 0) {      // Paul_20180709
////                        countDownTimerSettle.start();
////                        countDownTimerSettle.cancel();        // Paul_20180709
//                    } else if (settlementPosition == 1) {
//                        okBtn.setVisibility(View.GONE);
//                        dialogSettlement.show();
//                        selectDataTransTempAll("GHC");
//                        countDownTimerSettle.cancel();        // Paul_20180709
//                    } else if (settlementPosition == 2) {
//                        countDownTimerSettle.cancel();        // Paul_20180709
//                        okBtn.setVisibility(View.GONE);
//                        dialogSettlement.show();
//                        selectDataTransTempAll("POS");
//                    } else if (settlementPosition == 3) {
//                        countDownTimerSettle.cancel();        // Paul_20180709
//                        statusLabel.setText("Settlement BASE24 EPS");
//                        selectDataTransTempAll("EPS");
//                    } else if (settlementPosition == 4) {
//                        countDownTimerSettle.cancel();        // Paul_20180709
//                        statusLabel.setText("Settlement KTB ONUS");
//                        selectDataTransTempAll("TMS");
//                    } else if (settlementPosition == 5) {
//                        countDownTimerSettle.cancel();        // Paul_20180709
//                        selectSettlementQRAll();
//                        countDownTimerSettle.cancel();        // Paul_20180709
//                    } else {
//                        countDownTimerSettle.cancel();        // Paul_20180709
//                        dialogSettlement.dismiss();
//                        Log.d(TAG, "success: " + settlementPosition);
//                        finish();//K.GAME 181012
//                        System.out.printf("utility:: Settle selectDatabaseAllSettlement Success \n");
//                        if (typeInterface != null) {
//                            TerToPosSettlement();
//                            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                                @Override
//                                public void success() {
//                                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
//                    }
////                    countDownTimerSettle.start();         // Paul_20180709
//                }
//            }.start();
////            countDownTimerSettle.cancel();        // Paul_20180709
//        }
//
//        Log.d(TAG, "statusLabel.setText(\"โอนยอด\");");  //โอนยอด
//        statusLabel.setText("โอนยอด");
//
//    }

    private void selectDatabaseSaleOffline() {


        selectDataTransTemp("GHC");

        databaseTransTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").equalTo("voidFlag", "N").equalTo("ghcoffFlg", "Y").findFirst();
        i++;
        Log.d(TAG, "selectDatabaseSaleOffline: I = " + i);
        if (databaseTransTemp != null && transTemp.size() >= i) {
            setDataSlipOffline();
            sendDataSaleOffline();
            Log.d(TAG, "selectDatabaseSaleOffline: if = " + i);
        } else {
            if (dialog != null)
                dialog.dismiss();
            Log.d(TAG, "selectDatabaseSaleOffline: else = " + i);
            if (transTemp.size() > 0) {
                dialogWaiting.show();
                if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
                    statusLabel.setText("HeathCare ไม่มีข้อมูล");       // Paul_20180706
                setDataSettlementAndSendHGC();                // Paul_20180706
            } else {
                if (dialog != null)
                    dialog.dismiss();
                if (!isSettlementAll) {
                    Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    // Paul_20180709
                    countDownTimerSettle = new CountDownTimer(2000, 1000) {     // Paul_20180711
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            settlementPosition++;
                            okBtn.setVisibility(View.GONE);
//                            progressBarStatus.setVisibility(View.GONE);  //20181119
// Paul_20180706
                            if (settlementPosition == 1) {
                                if (msgSetmenu.substring(2, 3).equalsIgnoreCase("1"))//K.GAME 181004
                                    statusLabel.setText("HeathCare ไม่มีข้อมูล");
                            } else if (settlementPosition == 2) {
                                statusLabel.setText("KTB OFFUS ไม่มีข้อมูล");   // Paul_20181028 Sinn merge version UAT6_0016
                            } else if (settlementPosition == 3) {
                                statusLabel.setText("WAY4 ไม่มีข้อมูล");
                            } else if (settlementPosition == 4) {
                                statusLabel.setText("KTB ONUS ไม่มีข้อมูล");
                            } else if (settlementPosition == 5) {
                                statusLabel.setText("QR ไม่มีข้อมูล");
                            } else {
                                statusLabel.setText("Settlement สำเร็จ");
                            }

                            if (settlementPosition == 2) {
                                okBtn.setVisibility(View.GONE);
                                dialogSettlement.show();
//                                selectDataTransTempAll("POS");
                                hostflag = "111POS333444555";
                                selectDataTransTempAllGAME(" ");
                            } else if (settlementPosition == 3) {
                                statusLabel.setText("Settlement WAY4");
//                                selectDataTransTempAll("EPS");
                                hostflag = "111222EPS444555";
                                selectDataTransTempAllGAME(" ");
                            } else if (settlementPosition == 4) {
                                statusLabel.setText("Settlement KTB ONUS");
//                                selectDataTransTempAll("TMS");
                                hostflag = "TMS222333444555";
                                selectDataTransTempAllGAME(" ");
                            } else if (settlementPosition == 5) {
                                selectSettlementQRAll();
                            } else {
                                countDownTimerSettle.cancel();        // Paul_20180709
                                dialogSettlement.dismiss();
                                Log.d(TAG, "success: " + settlementPosition);
                                System.out.printf("utility:: selectDatabaseSaleOffline Settle onFinish Success \n");
                                if (typeInterface != null) {
                                    Utility.customDialogAlertAuto(MenuSettlementActivity.this, "ยกเลิกการทำรายการ");  // Paul_20181020
                                    TerToPosSettlement();
                                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                                        @Override
                                        public void success() {
                                            Utility.customDialogAlertAutoClear();
                                            Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
                            countDownTimerSettle.cancel();        // Paul_20180709
                            countDownTimerSettle.start();         // Paul_20180709
                        }
                    }.start();
                }
            }
        }
    }

    private void sendDataSaleOffline() {
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = databaseTransTemp.getDe2();
        mBlockDataSend[3 - 1] = databaseTransTemp.getDe3();
        mBlockDataSend[4 - 1] = databaseTransTemp.getDe4();
        mBlockDataSend[11 - 1] = databaseTransTemp.getDe11();
        mBlockDataSend[22 - 1] = databaseTransTemp.getDe22();
        mBlockDataSend[24 - 1] = databaseTransTemp.getDe24();
        mBlockDataSend[25 - 1] = databaseTransTemp.getDe25();
        mBlockDataSend[35 - 1] = databaseTransTemp.getDe35();
        mBlockDataSend[41 - 1] = databaseTransTemp.getDe41();
        mBlockDataSend[42 - 1] = databaseTransTemp.getDe42();
        mBlockDataSend[52 - 1] = databaseTransTemp.getDe52();
        mBlockDataSend[62 - 1] = databaseTransTemp.getDe62();
        mBlockDataSend[63 - 1] = databaseTransTemp.getDe63();
        TPDU = CardPrefix.getTPDU(MenuSettlementActivity.this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
        Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        saveDataReversalSaleOffline();
    }

    private void batchTMS() {
        HOST_CARD = "TMS";
        TERMINAL_ID = CardPrefix.getTerminalId(this, "TMS");
        MERCHANT_NUMBER = CardPrefix.getMerchantId(this, "TMS");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", "GHC").findAll();
        int timeCount = 0;
        int amountAll = 0;
        timeCount = transTemp.size();
        TPDU = CardPrefix.getTPDU(this, HOST_CARD);
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
            traceIdNo = CardPrefix.geTraceId(this, HOST_CARD);
            mBlockDataSend = new String[64];
            mBlockDataSend[2 - 1] = transTemp.get(batchUpload).getCardNo().length() + transTemp.get(batchUpload).getCardNo();
            mBlockDataSend[3 - 1] = "003000";
            Double amount = Double.valueOf(transTemp.get(batchUpload).getAmount().replaceAll(",", ""));
            mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(amount));
            mBlockDataSend[11 - 1] = calNumTraceNo(traceIdNo);
            mBlockDataSend[12 - 1] = transTemp.get(batchUpload).getTransTime().replace(":", "");
            mBlockDataSend[13 - 1] = transTemp.get(batchUpload).getTransDate().substring(4, 8);
//                mBlockDataSend[14 - 1] = transTemp.get(i).getExpiry();
            mBlockDataSend[22 - 1] = transTemp.get(batchUpload).getPointServiceEntryMode();
            mBlockDataSend[24 - 1] = "0444";
            mBlockDataSend[25 - 1] = "05";
            mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(CardPrefix.calLen("0", 12));
            mBlockDataSend[38 - 1] = BlockCalculateUtil.getHexString(CardPrefix.calLen("0", 6));
            mBlockDataSend[39 - 1] = BlockCalculateUtil.getHexString(CardPrefix.calLen("0", 2));
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[62 - 1] = getLength62(String.valueOf(transTemp.get(batchUpload).getEcr().length())) + BlockCalculateUtil.getHexString(transTemp.get(batchUpload).getEcr());
            terVer = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_VERSION);
            msgVer = "0008";
            tranCode = "6014";
            batchNo = CardPrefix.calLen(CardPrefix.getBatch(this, HOST_CARD), 8);
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
            mBlockDataSend[63 - 1] = CardPrefix.calLen(String.valueOf(mBlock63.length()), 4) + BlockCalculateUtil.getHexString(mBlock63);
            packageAndSend(TPDU, "0320", mBlockDataSend);

            batchUpload++;

        } else {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_POS)));
            String batchNumber = CardPrefix.getBatch(this, HOST_CARD);
            mBlockDataSend = new String[64];
            mBlockDataSend[3 - 1] = "960000";
            mBlockDataSend[11 - 1] = BlockCalculateUtil.getSerialCode(Integer.valueOf(traceIdNo));
            if (HOST_CARD.equalsIgnoreCase("POS")) {
                mBlockDataSend[24 - 1] = Preference.getInstance(this).getValueString(Preference.KEY_NII_POS);
            } else if (HOST_CARD.equalsIgnoreCase("EPS")) {
                mBlockDataSend[24 - 1] = Preference.getInstance(this).getValueString(Preference.KEY_NII_EPS);
            } else if (HOST_CARD.equalsIgnoreCase("TMS")) {
                mBlockDataSend[24 - 1] = Preference.getInstance(this).getValueString(Preference.KEY_NII_TMS);
            }
            mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(TERMINAL_ID);
            mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(MERCHANT_NUMBER);
            mBlockDataSend[60 - 1] = getLength62(String.valueOf(calNumTraceNo(batchNumber).length())) + BlockCalculateUtil.getHexString(calNumTraceNo(batchNumber));
            mBlockDataSend[63 - 1] = BlockCalculateUtil.get63BlockData(timeCount, String.valueOf(amountAll));
            packageAndSend(TPDU, "0500", mBlockDataSend);
        }
    }

    private void removeSettlementHGC() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").findAll();
                Log.d(TAG, "execute: " + transTemp.size());
                transTemp.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: removeReversal ");
            }
        });
    }

    private void saveDataSale(final String appCode, final String refNo) {

        new AsyncTaskSaveDb().execute(appCode, refNo);

    }

    public void doPrintingHgc(Bitmap slip) {
        System.out.printf("utility:: %s doPrintingHgc 0001 \n", TAG);
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
                            if (!isSettlementAll) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.printf("utility:: %s doPrintingHgc 0002 \n", TAG);
                                        Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
                                        System.out.printf("utility:: %s doPrintingHgc 0003 \n", TAG);
                                        typeHost = "GHC";   // "POS"; Paul_20180709
                                        okBtn.setVisibility(View.GONE);
                                        dialogSettlement.show();
//                                        selectDataTransTempAll("GHC");  // "POS" Paul_201807090
                                        if (!hostflag.equalsIgnoreCase("111222333444555666777"))
                                            selectDataTransTempAllGAME("");  //20181011 Game settlement all
                                    }
                                });
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

    private void doPrinter(Bitmap slip, final String appCode, final String refNo) {
        System.out.printf("utility:: %s doPrinter 0001 \n", TAG);
        bitmapOld = slip;

        gDataBaseID = databaseTransTemp.getId();
//        databaseTransTemp.getComCode();
        System.out.printf("utility::  %s doPrinter 0001 gDataBaseID = %d \n", TAG, gDataBaseID);
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
                            saveDataSale(appCode,
                                    refNo);
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

    private void saveDataReversalSaleOffline() {
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
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MenuSettlementActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(MenuSettlementActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        System.out.printf("utility:: %s onDestroy \n", TAG);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        System.out.printf("utility:: %s onResume \n", TAG);
        super.onResume();
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        if (cardManager != null) {
            setResponsCode();
        }
    }

    @Override
    protected void onPause() {
        System.out.printf("utility:: %s onPause \n", TAG);
        super.onPause();
        cardManager.removeResponseCodeListener();
//        realm.close();
    }

    @Override
    protected void connectTimeOut() {
        System.out.printf("utility:: MenuSteelement connectTimeOut Error \n");
        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
            Utility.customDialogAlertAuto(MenuSettlementActivity.this, "transactionTimeOut");
            TellToPosError("21");
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
            Utility.customDialogAlert(this, "connectTimeOut", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
//                countDownTimerSettle.cancel();        // Paul_20180709
                    dialog.dismiss();
                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
        System.out.printf("utility:: MenuSteelement transactionTimeOut Error \n");
        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
            Utility.customDialogAlertAuto(MenuSettlementActivity.this, "transactionTimeOut");
            TellToPosError("21");
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Utility.customDialogAlertAutoClear();
                    System.out.printf("utility:: MenuServiceListActivity connectTimeOut 0000001 \n");
                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
//                countDownTimerSettle.cancel();        // Paul_20180709
                    dialog.dismiss();
                    Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
        System.out.printf("utility:: %s received \n", TAG);

        mBlockDataReceive = data;
        // String deRe39 = BlockCalculateUtil.hexToString(data[39 - 1]);
        deRe39 = BlockCalculateUtil.hexToString(data[39 - 1]);
        if (deRe39.equalsIgnoreCase("00") && mBlockDataSend[3 - 1].equalsIgnoreCase("920000")) {
            removeSettlementHGC();
            dialogWaiting.dismiss();
            int batch = Integer.parseInt(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC));
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_GHC, String.valueOf(batch + 1));
            Log.d(TAG, "received: " + batch);
            if (dialog != null)             // Paul_20180706
                dialog.dismiss();

            // Paul_20180731 Start
            String TempString;
            String TotPayCnt;
            String TotPayAmt;
            String TotVoidPayCnt;
            String TotVoidPayAmt;

            TempString = BlockCalculateUtil.hexToString(data[63 - 1]).substring(43, 43 + 5);
            System.out.printf("utility:: Total Payment Count = %s \n", TempString);
            TotPayCnt = TempString;
            TempString = BlockCalculateUtil.hexToString(data[63 - 1]).substring(48, 48 + 10);
            System.out.printf("utility:: Total Payment Amount = %s \n", TempString);
            TotPayAmt = TempString;
            TempString = BlockCalculateUtil.hexToString(data[63 - 1]).substring(58, 58 + 5);
            System.out.printf("utility:: Total VoidPayment Count = %s \n", TempString);
            TotVoidPayCnt = TempString;
            TempString = BlockCalculateUtil.hexToString(data[63 - 1]).substring(63, 63 + 10);
            System.out.printf("utility:: Total VoidPayment Amount = %s \n", TempString);
            TotVoidPayAmt = TempString;

            int payCount = 0;
            int voidCount = 0;

            DecimalFormat decimalFormatamt = new DecimalFormat("###0.00");

            System.out.printf("utility:: Double.valueOf( TotPayAmt ) = %f \n", Double.valueOf(TotPayAmt));
            System.out.printf("utility:: Double.valueOf( TotVoidPayAmt ) = %f \n", Double.valueOf(TotVoidPayAmt));
            totalSale = Double.valueOf(TotPayAmt) - Double.valueOf(TotVoidPayAmt);
            totalVoid = Double.valueOf(TotVoidPayAmt);
            System.out.printf("utility:: totalSale = %f \n", totalSale);
            System.out.printf("utility:: totalVoid = %f \n", totalVoid);
            totalSale /= 100;
            totalVoid /= 100;
            System.out.printf("utility:: totalSale = %f \n", totalSale);
            System.out.printf("utility:: totalVoid = %f \n", totalVoid);
            payCount = Integer.valueOf(TotPayCnt) - Integer.valueOf(TotVoidPayCnt);
            voidCount = Integer.valueOf(TotVoidPayCnt);
            DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
            saleCountHgcLabel.setText(String.valueOf(payCount));   // Paul_20180724_OFF
            saleTotalHgcLabel.setText(decimalFormat.format(totalSale));
            voidSaleCountHgcLabel.setText(voidCount + "");
            voidSaleAmountHgcLabel.setText(decimalFormat.format(totalVoid));
            countAll = payCount + voidCount;
            cardCountHgcLabel.setText(countAll + "");
            cardAmountHgcLabel.setText(decimalFormat.format(totalSale));

            // Paul_20180731 End

//            saleCountHgcLabel.setText(payCount + "");
//
//            saleTotalHgcLabel.setText(decimalFormat.format(amountAll));
//            voidSaleCountHgcLabel.setText(voidCount + "");
//            voidSaleAmountHgcLabel.setText(decimalFormat.format(amountVoidAll));
//            cardCountHgcLabel.setText((payCount + voidCount) + "");
//            cardAmountHgcLabel.setText(decimalFormat.format(amountAll));

//            mBlockDataReceive
            // Paul_20180731 End

//            private Double totalSale = 0.0;
//            private Double totalVoid = 0.0;

//            saleCountHgcLabel;
//            saleTotalHgcLabel;
//            voidSaleCountHgcLabel;
//            voidSaleAmountHgcLabel;
//            cardCountHgcLabel;
//            cardAmountHgcLabel;

//            Utility.customDialogAlertSuccess(this, null, new Utility.OnClickCloseImage() {
//                @Override
//                public void onClickImage(Dialog dialog) {
//                    dialog.dismiss();
//                }
//            });
            doPrintingHgc(getBitmapFromView(settlementHgcLinearLayout));
        } else if (deRe39.equalsIgnoreCase("00") && mBlockDataSend[3 - 1].equalsIgnoreCase("005000")) {
            removeReversal();
            System.out.printf("utility:: %s responsed 0000000001\n", TAG);
            if (dialog != null) {
                dialog.setTitle("กำลังส่งข้อมูล Sale Offline");
            } else {
                statusLabel.setText("กำลังส่งข้อมูล HeathCare Sale Offline");
            }
            if (BlockCalculateUtil.hexToString(data[63 - 1]) == null) {
                apprCodeOffLabel.setText("");
            } else {
                System.out.printf("utility:: RRRRRRRRRRRRRRRRR BlockCalculateUtil.hexToString( data[63 - 1] ) = %s \n", BlockCalculateUtil.hexToString(data[63 - 1]));
                System.out.printf("utility:: RRRRRRRRRRRRRRRRR apprCodeOffLabel = %s \n", BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9));
//                apprCodeOffLabel.setText( BlockCalculateUtil.hexToString( data[63 - 1] ).substring( 22, 22 + 9 ) );
//                Log.d( TAG, "received: " + BlockCalculateUtil.hexToString( data[63 - 1] ).substring( 22, 22 + 9 ) );
                String TempString = BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9);
                apprCodeOffLabel.setText((CharSequence) TempString);
                Log.d(TAG, "received: " + TempString);
            }
            setMeasureHGCOff();

            ////20180827  SINN Slip Sele offline on Settlement tansaction systrc is wrong.
            systrcOffLabel.setText(mBlockDataSend[11 - 1]);

            doPrinter(getBitmapFromView(settlementHgcOffLinearLayout), BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9),
                    CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));

        } else if (!deRe39.equalsIgnoreCase("00") && mBlockDataSend[3 - 1].equalsIgnoreCase("005000")) {
            System.out.printf("utility:: %s responsed 0000000002\n", TAG);
            // Paul_20180708
            removeReversal();
            System.out.printf("utility:: %s received = %s \n", TAG, deRe39);
            if (dialog != null) {
                dialog.setTitle("กำลังส่งข้อมูล Sale Offline");
            } else {
                statusLabel.setText("กำลังส่งข้อมูล HeathCare Sale Offline");
            }
            apprCodeOffLabel.setText("");
//            apprCodeOffLabel.setText(BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9));
//            Log.d(TAG, "received: " + BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9));
            setDataSlipHGCOfflineSaleError();
            doPrinter(getBitmapFromView(settlementHgcErLinearLayout), "PAUL OFF",
                    CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));
//            doPrinter(getBitmapFromView(settlementHgcErLinearLayout), BlockCalculateUtil.hexToString(data[63 - 1]).substring(22, 22 + 9),
//                    CardPrefix.calLen(BlockCalculateUtil.hexToString(data[37 - 1]), 12));

        } else {
            System.out.printf("utility:: %s responsed 0000000003\n", TAG);
            //----------------------------------------------------------
            // Paul_20180723
            if (typeInterface != null) {
                Utility.customDialogAlertAuto(MenuSettlementActivity.this, "Error : " + deRe39);
                TerToPosSettlement();
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
                //---------------------------------------------------------

                Utility.customDialogAlert(this, "Error : " + deRe39, new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                        System.out.printf("utility:: MenuSteelement receive Error \n");
                        Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
    protected void error(String error) {
        Utility.customDialogAlert(this, "error 004 ", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                System.out.printf("utility:: MenuSteelement error Error \n");
//                countDownTimerSettle.cancel();        // Paul_20180709
                Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
        Utility.customDialogAlert(this, "other", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                System.out.printf("utility:: MenuSteelement other Error \n");
//                countDownTimerSettle.cancel();        // Paul_20180709
                Intent intent = new Intent(MenuSettlementActivity.this, MenuServiceListActivity.class);
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
    protected void onStop() {
//        countDownTimerSettle.cancel();        // Paul_20180709
        System.out.printf("utility:: %s onStop \n", TAG);
        super.onStop();
    }

    private class AsyncTaskSaveDb extends AsyncTask<String, Integer, String> {
        private String appCode;
        private String refNo;
        private int id;

        @Override
        protected String doInBackground(String... strings) {

            appCode = strings[0];
            refNo = strings[1];

//            if(appCode == "PAUL OFF")
//            {
//                databaseRemove(databaseTransTemp.getId());
//            }
//            else {
//                updateTransactionOfflineToSale( databaseTransTemp,appCode,refNo );
//            }


            Realm realm = Realm.getDefaultInstance();
            try {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final Date dateTime = new Date();
                        final DateFormat dateFormat = new SimpleDateFormat("yyyy");
                        String year = new SimpleDateFormat("yy").format(dateTime);  // Paul_20180706

                        final String second = mBlockDataReceive[12 - 1].substring(4, 6);
                        final String minute = mBlockDataReceive[12 - 1].substring(2, 4);
                        final String hour = mBlockDataReceive[12 - 1].substring(0, 2);
                        final String mount = mBlockDataReceive[13 - 1].substring(0, 2);
                        final String date = mBlockDataReceive[13 - 1].substring(2, 4);

                        TransTemp trans = realm.where(TransTemp.class).equalTo("id", gDataBaseID).findFirst();


                        //                        TransTemp trans = realm.where(TransTemp.class).findFirst();
                        if (appCode != "PAUL OFF") {
                            //                        realm.beginTransaction();
                            System.out.printf("utility:: updateTransactionVoid 000000000003 \n");
                            //                        if (trans != null) {
                            trans.setVoidFlag("N");
                            trans.setTransStat("SALE");          // Paul_20180724_OFF

                            trans.setTransDate(dateFormat.format(dateTime) + mount + date);
                            trans.setTransTime(hour + minute + second);
                            trans.setGhcoffFlg("N");
                            trans.setApprvCode(appCode);
                            trans.setRefNo(refNo);
                            //            String batch = Preference.getInstance( MenuSettlementActivity.this ).getValueString( Preference.KEY_BATCH_NUMBER_GHC );
                            String invoice = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
                            trans.setEcr(Utility.calNumTraceNo(invoice));
                            int inV = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL));
                            inV = inV + 1;
                            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
                            trans.setConditionCode(mBlockDataSend[25 - 1]);
                            trans.setDe63Sale(mBlockDataSend[63 - 1]);

                            //                        trans.setComCode( databaseTransTemp.getComCode() );
                            //                        realm.commitTransaction();
                            //                        realm.close();
                            realm.insert(trans);
                            //                        }
                        } else {
                            trans.setVoidFlag("Y");
                            trans.setTransStat("SALE");          // Paul_20180724_OFF

                            trans.setTransDate(dateFormat.format(dateTime) + mount + date);
                            trans.setTransTime(hour + minute + second);
                            trans.setGhcoffFlg("Y");
                            trans.setApprvCode(appCode);
                            trans.setRefNo(refNo);
                            //            String batch = Preference.getInstance( MenuSettlementActivity.this ).getValueString( Preference.KEY_BATCH_NUMBER_GHC );
                            String invoice = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
                            trans.setEcr(Utility.calNumTraceNo(invoice));
                            int inV = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL));
                            inV = inV + 1;
                            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
//                            trans.setConditionCode( mBlockDataSend[25 - 1] );
//                            trans.setDe63Sale( mBlockDataSend[63 - 1] );
                            realm.insert(trans);
                        }
                    }
                });
            } finally {
//                realm.close();
            }

            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: SaveDb Success");
            new AsyncTaskRemoveData().execute(id);
        }
    }

    private class AsyncTaskRemoveData extends AsyncTask<Integer, Integer, String> {

        private int id;

        @Override
        protected String doInBackground(Integer... data) {

/*
            id = data[0];

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TransTemp databaseTransTemp = realm.where(TransTemp.class).equalTo("id", id).findFirst();
                    if (databaseTransTemp != null) {
                        databaseTransTemp.deleteFromRealm();
                    }
                }
            });
*/
            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, "onPostExecute: RemoveDB Success");
            selectDatabaseSaleOffline();
        }
    }

    // Paul_20180708
    private void setViewSaleHGCError() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        hgcSaleViewError = inflater.inflate( R.layout.view_slip_sale_hgc_error_settle, null);
//        settlementHgcErLinearLayout = hgcSaleViewError.findViewById( R.id.settlementLinearLayout_set);

        //20180827  SINN change Slip Sele offline on Settlement error.
        hgcSaleViewError = inflater.inflate(R.layout.view_slip_sale_hgc_error, null);
        settlementHgcErLinearLayout = hgcSaleViewError.findViewById(R.id.view_slip_sale_hgc_error_layout);

        dateHgcErLabel = hgcSaleViewError.findViewById(R.id.dateLabel);
        timeHgcErLabel = hgcSaleViewError.findViewById(R.id.timeLabel);
//        midHgcErLabel = hgcSaleViewError.findViewById( R.id.midLabel);
//        tidHgcErLabel = hgcSaleViewError.findViewById( R.id.tidLabel);
//        systrcErLabel = hgcSaleViewError.findViewById( R.id.systrcLabel);
//        traceNoErLabel = hgcSaleViewError.findViewById( R.id.traceNoLabel);
//        typeSaleErLabel = hgcSaleViewError.findViewById( R.id.typeSaleLabel);
        cardNoErLabel = hgcSaleViewError.findViewById(R.id.cardNoLabel);
        comCodeErLabel = hgcSaleViewError.findViewById(R.id.comCodeErLabel);
//        batchHgcErLabel = hgcSaleViewError.findViewById( R.id.batchLabel);
        merchantName1HgcErLabel = hgcSaleViewError.findViewById(R.id.merchantName1Label);
        merchantName2HgcErLabel = hgcSaleViewError.findViewById(R.id.merchantName2Label);
        merchantName3HgcErLabel = hgcSaleViewError.findViewById(R.id.merchantName3Label);

        footerErrorMsg = hgcSaleViewError.findViewById(R.id.footerErrorMsg);
        errorMsg = hgcSaleViewError.findViewById(R.id.errorMsg);

    }

    // Paul_20180708
    private void setDataSlipHGCOfflineSaleError() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
        dateHgcErLabel.setText(dateFormat.format(date));
        timeHgcErLabel.setText(dateTimeFormat.format(date));
//        midHgcErLabel.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_GHC));
        //  tidHgcErLabel.setText( Preference.getInstance(this).getValueString( Preference.KEY_TERMINAL_ID_GHC));
//        systrcErLabel.setText( Preference.getInstance(this).getValueString( Preference.KEY_TRACE_NO_GHC));

//        systrcErLabel.setText( CardPrefix.calLen( Preference.getInstance(this).getValueString( Preference.KEY_TRACE_NO_GHC), 6));
//20180827  SINN Slip Sele offline on Settlement tansaction systrc is wrong.
//        String szTrace = Preference.getInstance(this).getValueString( Preference.KEY_TRACE_NO_GHC);
//        int inTrace = Integer.valueOf(szTrace)-1;
//        systrcErLabel.setText( CardPrefix.calLen( String.valueOf(inTrace), 6));
//        systrcErLabel.setText(mBlockDataSend[11 - 1]);
//20180827  END SINN Slip Sele offline on Settlement tansaction systrc is wrong.


// Paul_20180713 Start
        String szMSG = null;
        String idCardCd;
        if (databaseTransTemp.getTypeSale().substring(1).equalsIgnoreCase("2")) {
            idCardCd = databaseTransTemp.getCardNo();
        } else if (databaseTransTemp.getTypeSale().substring(1).equalsIgnoreCase("1")) {
            idCardCd = databaseTransTemp.getIdCard();
        } else {
            idCardCd = databaseTransTemp.getCardNo();
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
//        if (databaseTransTemp.getTypeSale().substring(1).equalsIgnoreCase("2")) {
//            cardNoErLabel.setText(databaseTransTemp.getCardNo());
//        } else if (databaseTransTemp.getTypeSale().substring(1).equalsIgnoreCase("1")) {
//            cardNoErLabel.setText(databaseTransTemp.getIdCard());
//        } else {
//            cardNoErLabel.setText(databaseTransTemp.getCardNo());
//        }
// Paul_20180713 End


//        comCodeErLabel.setText("HCG13814");
        comCodeErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_TAG_1001_HC));
//        batchHgcErLabel.setText( CardPrefix.calLen( Preference.getInstance(this).getValueString( Preference.KEY_BATCH_NUMBER_GHC), 6));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1HgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2HgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3HgcErLabel.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));


        errorMsg.setText(RespCode.ResponseMsgGHC(deRe39));
        footerErrorMsg.setText("*ติดต่อ Call Center \n กรมบัญชีกลาง 022706400");


        setMeasureHGCEr();
//        doPrinter(getBitmapFromView(settlementHgcErLinearLayout));

    }


    // Paul_20180708
    private void setMeasureHGCEr() {
        hgcSaleViewError.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        hgcSaleViewError.layout(0, 0, hgcSaleViewError.getMeasuredWidth(), hgcSaleViewError.getMeasuredHeight());
    }

    //Paul_20180708
    Handler pos_Handler = new Handler() {

        public void handleMessage(Message msg) {

            if (typeInterface != null) {
// Paul_20181106 POSLINK some time Card Insert No
//                if (dialogWaiting != null && !dialogWaiting.isShowing())  // Paul_20181024
//                    dialogWaiting.show();

//
//                hostflag = "TMSPOSEPSGHC QRALI";  //SINN 20181014 settlement all hostflag = "TMSPOSEPSGHC QRALI";
                hostflag = "TMSPOSEPSGHC QR666777";  // Paul_20181022 ALI no follow up  //SINN 20181014 settlement all hostflag = "TMSPOSEPSGHC QRALI";
//                // Paul_20181023 Selete Enable
//                if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALI_WEC_ENABLE).substring(0, 1).equalsIgnoreCase("1")) {
//                    hostflag = "TMSPOSEPSGHC QRALI";
//                }
//                // Paul_20181023 Selete Enable
//                if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALI_WEC_ENABLE).substring(1, 2).equalsIgnoreCase("1")) {
//                    checkbox_Wec.setChecked(true);  // Paul_20181022 WECHAT
//                }

// Paul_20181023 Start : Select Enable
                hostflag = "111222333444555666777";   //TMS|POS|EPS|GHC| QR|ALIWEC
                String std_flag = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_APP_ENABLE);
                if (std_flag.substring(0, 1).equals("1")) {
                    hostflag = hostflag.replaceAll("111", "TMS");//TMS|POS|EPS|GHC| QR|ALI
                    hostflag = hostflag.replaceAll("222", "POS");//TMS|POS|EPS|GHC| QR|ALI
                    hostflag = hostflag.replaceAll("222", "POS");//TMS|POS|EPS|GHC| QR|ALI
                }
                if (std_flag.substring(1, 2).equals("1")) {
                    hostflag = hostflag.replaceAll("555", " QR");//TMS|POS|EPS|GHC| QR|ALI
                    // Paul_20181023 Selete Enable
                    if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_ID).equalsIgnoreCase("1")) {
                        hostflag = hostflag.replaceAll("666", "ALI");  //TMS|POS|EPS|GHC| QR|ALI
                    }
                    if (Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_WECHATPAY_ID).equalsIgnoreCase("1")) {
                        hostflag = hostflag.replaceAll("777", "WEC");  //TMS|POS|EPS|GHC| QR|ALI
                    }
                }
                if (std_flag.substring(2, 3).equals("1")) {
                    hostflag = hostflag.replaceAll("444", "GHC");//TMS|POS|EPS|GHC| QR|ALI
                }
// Paul_20181023 End : Select Enable


                settlementPosition = 0;
                isSettlementAll = true;
                selectDataTransTempAllGAME(" ");
//                cardManager.setDataDefaultBatchUpload();
//                settlementPosition = 0;
//                isSettlementAll = true;
//                progressBarStatus.setVisibility(View.VISIBLE);
//                dialogSettlement.show();
//                selectDatabaseAllSettlement(" ");
//            selectSettlementQR();
/*
            System.out.printf("utility:: Settlement 0003 typeInterface = %s \n",typeInterface);
            settlementPosition = 0;
            isSettlementAll = true;
            progressBarStatus.setVisibility( View.VISIBLE );
            dialogSettlement.show();
            //                        dialog = ProgressDialog.show(MenuSettlementActivity.this, "",
            //                                "Loading. Please wait...", true);
            selectDatabaseSaleOffline();
*/
            }

//            pos_Handler.sendEmptyMessageDelayed(0,3000);        // Paul_20180709

        }
    };

    // Paul_20180710
//    public void TerToPosSettlement()
//    {
//        // Paul_20180704
//        String TERMINAL_ID;
//        String MERCHANT_NUMBER;
//
//        Date dateTime = new Date();
//        String YYMMDD = new SimpleDateFormat("yyMMdd").format(dateTime);
//        String HHMMSS = new SimpleDateFormat("HHmmss").format(dateTime);
//
//        TERMINAL_ID = CardPrefix.getTerminalId(MenuSettlementActivity.this, "GHC");
//        MERCHANT_NUMBER = CardPrefix.getMerchantId(MenuSettlementActivity.this, "GHC");
//
//        String ResponseCode="00";
//        posInterfaceActivity.PosInterfaceWriteField("01","000000000");   // Approval Code
//        posInterfaceActivity.ResponseMsgPosInterface(ResponseCode);
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(ResponseCode));   // Response Message
//
//        posInterfaceActivity.PosInterfaceWriteField("65","      ");   // Invoice Number
//        posInterfaceActivity.PosInterfaceWriteField("16",TERMINAL_ID);   // Terminal ID
//        posInterfaceActivity.PosInterfaceWriteField("D1",MERCHANT_NUMBER);   // Merchant ID
//        posInterfaceActivity.PosInterfaceWriteField("03",YYMMDD);   // Date YYMMDD
//        posInterfaceActivity.PosInterfaceWriteField("04",HHMMSS);   // Time HHMMSS
//        posInterfaceActivity.PosInterfaceWriteField("30","");   // Card No
//        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode,ResponseCode);
//    }
    public void TerToPosSettlement() {
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));   // Response Message
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceTransactionCode, "00");
    }

    //    public void TellToPosError(String szErr)
//    {
//        posInterfaceActivity.PosInterfaceWriteField("01","000xxxxxx");   // Approval Code
//        //posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("12"));   // Response Message TX NOT FOUND
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(szErr));
//
//        posInterfaceActivity.PosInterfaceWriteField("65","000000");   // Invoice Number
//        posInterfaceActivity.PosInterfaceWriteField("D3","xxxxxxxxxxxx");
//
//        if (cardManager.getHostCard().equalsIgnoreCase("EPS")) {
//            posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_TERMINAL_ID_EPS));
//            posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_EPS));
//        }
//        else if (cardManager.getHostCard().equalsIgnoreCase("TMS")) {
//            posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_TERMINAL_ID_TMS));
//            posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
//        }
//        else if (cardManager.getHostCard().equalsIgnoreCase("POS")){
//            posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_TERMINAL_ID_POS));
//            posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_POS));
//        }
//        else
//        {
//            posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString( Preference.KEY_TERMINAL_ID_GHC));
//            posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_ID_GHC));
//        }
//
//        Date date = new Date();
//        String dateFormatDef = new SimpleDateFormat("yyMMdd").format(date);
//        posInterfaceActivity.PosInterfaceWriteField("03",dateFormatDef);  //yymmdd
//
//        String timeFormat = new SimpleDateFormat("HHmmss").format(date);
//        posInterfaceActivity.PosInterfaceWriteField("04",timeFormat);  //hhmmss
//
//        posInterfaceActivity.PosInterfaceWriteField("F1","QR");
//
//        //posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode,"12");
//        posInterfaceActivity.PosInterfaceSendMessage( PosInterfaceActivity.PosInterfaceTransactionCode,szErr);
//    }
    public void TellToPosError(String szErr) {
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, szErr);
    }

    private int iid;

    void databaseRemove(int id) {
        iid = id;
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                System.out.printf("utility:: %s databaseRemove = %d \n", TAG, iid);
                TransTemp databaseTransTemp = realm.where(TransTemp.class).equalTo("id", iid).findFirst();
                if (databaseTransTemp != null) {
                    databaseTransTemp.deleteFromRealm();
                }
            }
        });

    }

    private void updateTransactionOfflineToSale(TransTemp healthCareDB, String gAppCode, String gRefNo) {
        Log.d("utility:: ", "updateTransactionVoid: " + healthCareDB.getTraceNo());
        final Date dateTime = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy");
        String year = new SimpleDateFormat("yy").format(dateTime);  // Paul_20180706
        final String second = mBlockDataReceive[12 - 1].substring(4, 6);
        final String minute = mBlockDataReceive[12 - 1].substring(2, 4);
        final String hour = mBlockDataReceive[12 - 1].substring(0, 2);
        final String mount = mBlockDataReceive[13 - 1].substring(0, 2);
        final String date = mBlockDataReceive[13 - 1].substring(2, 4);

        final int transTempID = healthCareDB.getId();
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
//        else {
//            realm.close();
//            realm = null;   // Paul_20181026 Some time DB Read error solved
//            realm = Realm.getDefaultInstance();
//        }
        RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).findAll();
//        transTemps.size();
        Log.d("utility:: ", "insertTransaction: " + transTemps.size() + " base : " + transTemps.toString());
        System.out.printf("utility:: updateTransactionVoid 000000000001 \n");
        TransTemp trans = realm.where(TransTemp.class).equalTo("id", healthCareDB.getId()).findFirst();
        System.out.printf("utility:: updateTransactionVoid 000000000002 \n");
        realm.beginTransaction();
        System.out.printf("utility:: updateTransactionVoid 000000000003 \n");
        if (trans != null) {
            trans.setVoidFlag("N");
            trans.setTransStat("SALE");          // Paul_20180724_OFF

            trans.setTransDate(dateFormat.format(dateTime) + mount + date);
            trans.setTransTime(hour + minute + second);
            trans.setGhcoffFlg("N");
            trans.setApprvCode(gAppCode);
            trans.setRefNo(gRefNo);
//            String batch = Preference.getInstance( MenuSettlementActivity.this ).getValueString( Preference.KEY_BATCH_NUMBER_GHC );
            String invoice = Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
            trans.setEcr(Utility.calNumTraceNo(invoice));
            int inV = Integer.parseInt(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL));
            inV = inV + 1;
            Preference.getInstance(MenuSettlementActivity.this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
            trans.setConditionCode(mBlockDataSend[25 - 1]);
            trans.setDe63Sale(mBlockDataSend[63 - 1]);
            trans.setComCode(healthCareDB.getComCode());
        }
        realm.commitTransaction();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
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

//            setTimer(15000);

            setPostHeader();
            setPostBody();
            connetPost();

        } catch (IOException e) {
            e.printStackTrace();
            settlementLister.onFail();
        }
    }

    private void setPostHeader() {
//        reqBy = aliConfig.getMerId() + aliConfig.getDeviceId();
//        reqChannelRefId = aliConfig.getDeviceId() + uniqueData;
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
                obj2 = new JSONObject(result);

//                respcode = obj2.getString("respcode");
//                reqid = obj2.getString("origreqid");
//                reqdt = obj2.getString("origreqdt");
//                transid = obj2.getString("transid");
//                wallettransid = obj2.getString("wallettransid");
//                wallettransid = obj2.getString("canceldt");
//                wallettransid = obj2.getString("cii");
//                receipttext = obj2.getString("receipttext");
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

    private String makeUniqueData(Date data) {

        String temp = dateFormat2.format(data);  // yyyyMMddhhmmss
        String julian_date = dateFormat3.format(data);

        String temp_year = temp.substring(0, 4);
        String temp_time = temp.substring(8, 14);
        temp_year = String.valueOf(Integer.parseInt(temp_year) + 483);

        temp = temp_year.substring(3, 4) + julian_date + temp_time;

        return temp;
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

    private void settlement2(RealmResults<QrCode> saleTemp) {

        settlementDataset(saleTemp);

        try {

//            jsonObject.put("deviceid", aliConfig.getDeviceId());
//            jsonObject.put("merid", aliConfig.getMerId());
//            jsonObject.put("storeid", aliConfig.getStoreId());
            jsonObject.put("deviceid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));    // Paul_20181007
            jsonObject.put("merid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));          // Paul_20181007
            jsonObject.put("storeid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_STORE_ID));      // Paul_20181007

            jsonObject.put("startDate", startDate);
            jsonObject.put("endDate", endDate);
            jsonObject.put("currentPage", page);
            jsonObject.put("recordPerPage", record);

            System.out.printf("utility:: %s , settlement2 = %s \n", TAG, jsonObject.toString());

            param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH); //20181114Jeff
            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

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

    interface SettlementLister {
        public void onSuccess();

        public void onContinue();

        public void onFail();
    }

    //20181021 SINN settlement all
//    selectSummaryReport("POS");
//    selectSummaryReport("EPS");
//    selectSummaryReport("TMS");
//     selectSummaryReport("GHC");
//    selectSummaryQrReport();

    private void selectSummaryQrReport(String typeHost) {
        System.out.printf("utility:: %s selectSummaryQrReport \n", TAG);
//        dialogLoading.show();

        //Must Initialize_JEFF
        Preview_totalSale = 0.0;
        Preview_totalVoid = 0.0;

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }else{
           realm = null;
           realm = Realm.getDefaultInstance();
        }
        RealmResults<QrCode> qrCodes_sale = null;
        RealmResults<QrCode> qrCodes_void = null;
        switch(typeHost){
            case "QR" :
                qrCodes_sale = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("statusSuccess", "1").findAll();
//                qrCodes_void = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("statusSuccess", "0").findAll();  //SINN 20181116 QR void is not  "statusSuccess 0"
                qrCodes_void = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("voidflag", "Y").findAll();

                Log.d(TAG, "selectSummaryReport: " + qrCodes_sale.size());
//                for (int i = 0; i < qrCodes_sale.size(); i++) {
//                    Preview_totalSale += Double.valueOf(qrCodes_sale.get(i).getAmt().replaceAll(",", ""));
//                }
                //SINN 20181119 QR preview crash with use getAmt()
                for (int i = 0; i < qrCodes_sale.size(); i++) {
                    try {
                        Preview_totalSale += Double.valueOf(qrCodes_sale.get(i).getAmount().replaceAll(",", ""));
                    }catch (Exception E)
                    {
                        Preview_totalSale +=Double.valueOf(qrCodes_sale.get(i).getAmount());
                    }
                }
                //END  SINN 20181119 QR preview

                Preview_saleCount = Integer.valueOf(qrCodes_sale.size());




                for (int i = 0; i < qrCodes_void.size(); i++) {
//            Preview_totalVoid += Double.valueOf(qrCodes_void.get(i).getAmt().replaceAll(",", ""));   //SINN 20181112 Got null and activity restart.
                    try {
                        Preview_totalVoid += Double.valueOf(qrCodes_void.get(i).getAmt().replaceAll(",", ""));
                    }
                    catch (Exception e) {
                        Preview_totalVoid += 0.0;
                    }
                }
                Preview_voidCount = Integer.valueOf(qrCodes_void.size());

                break;
            case "ALIPAY" :
            case "WECHAT" :
                qrCodes_sale = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
                qrCodes_void = realm.where(QrCode.class).equalTo("hostTypeCard", typeHost).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();

                Log.d(TAG, "selectSummaryReport: " + qrCodes_sale.size());
                for (int i = 0; i < qrCodes_sale.size(); i++) {
                    try {
                        if(!qrCodes_sale.get(i).getAmtplusfee().equals("null"))
                            Preview_totalSale += Double.valueOf(qrCodes_sale.get(i).getAmtplusfee().replaceAll(",", ""));
                        else
                            Preview_totalSale += Double.valueOf(qrCodes_sale.get(i).getAmt().replaceAll(",", ""));
                    } catch (Exception e) {
                        Preview_totalSale += 0.0;
                    }
                }
                Preview_saleCount = Integer.valueOf(qrCodes_sale.size());

                for (int i = 0; i < qrCodes_void.size(); i++) {
                    try {
                        if(!qrCodes_void.get(i).getAmtplusfee().equals("null"))
                            Preview_totalVoid += Double.valueOf(qrCodes_void.get(i).getAmtplusfee().replaceAll(",", ""));
                        else
                            Preview_totalVoid += Double.valueOf(qrCodes_void.get(i).getAmt().replaceAll(",", ""));
                    }catch (Exception e) {
                        Preview_totalSale += 0.0;
                    }
                }
                Preview_voidCount = Integer.valueOf(qrCodes_void.size());
                break;
        }
    }

    //END 20181021 SINN settlement all
    private void selectSummaryReport(String typeHost) {
        System.out.printf("utility:: %s selectSummaryReport = %s \n", TAG, typeHost);

        final RealmResults<TransTemp> transTempSale = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "N").findAll();

        final RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).equalTo("voidFlag", "Y").findAll();
        totalSale = 0.0;
        totalVoid = 0.0;
        int SaleTotalCount = 0;      // Paul_20180724_OFF
        int VoidTotalCount = 0;      // Paul_20180724_OFF
        Log.d(TAG, "selectSummaryReport: " + transTempSale.size());
        Log.d(TAG, "selectSummaryReport: " + transTempVoid.size());
        for (int i = 0; i < transTempSale.size(); i++) {
            if (typeHost.equalsIgnoreCase("GHC")) {
                if (transTempSale.get(i).getGhcoffFlg().equalsIgnoreCase("N")) {
                    totalSale += Double.valueOf(transTempSale.get(i).getAmount().replaceAll(",", ""));
                    SaleTotalCount++;
                }
            } else {
                totalSale += Double.valueOf(transTempSale.get(i).getAmount().replaceAll(",", ""));
                SaleTotalCount++;
            }
        }

        for (int i = 0; i < transTempVoid.size(); i++) {
            if (typeHost.equalsIgnoreCase("GHC")) {
                if (transTempSale.get(i).getGhcoffFlg().equalsIgnoreCase("N")) {
                    totalVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",", ""));
                    VoidTotalCount++;
                }
            } else {
                totalVoid += Double.valueOf(transTempVoid.get(i).getAmount().replaceAll(",", ""));
                VoidTotalCount++;
            }
        }
        System.out.printf("utility:: SaleTotalCount = %d \n", SaleTotalCount);
        System.out.printf("utility:: VoidTotalCount = %d \n", VoidTotalCount);


        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

//        saleCountLabel.setText(String.valueOf(SaleTotalCount));   // Paul_20180724_OFF
        Preview_saleCount = SaleTotalCount;
//        saleTotalLabel.setText(decimalFormat.format(totalSale));
        Preview_totalSale = totalSale;
//        voidSaleCountLabel.setText(VoidTotalCount + "");
        Preview_voidCount = VoidTotalCount;
//        voidSaleAmountLabel.setText(decimalFormat.format(totalVoid));
        Preview_totalVoid = totalVoid;
//        countAll = SaleTotalCount + VoidTotalCount;     // Paul_20180724_OFF
//        cardCountLabel.setText(countAll + "");
//        cardAmountLabel.setText(decimalFormat.format(totalSale));


    }

    private void deleteTc() {
        System.out.printf("utility:: %s deleteReversal 00001 \n",TAG);
       /* try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.d(TAG, "1919_deleteReversal: ");
        Realm.getDefaultInstance().refresh();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<TCUpload> tcUpload = realm.where(TCUpload.class).equalTo("hostTypeCard", HOST_CARD).findAll();
                        if(tcUpload.size() >0)
                            tcUpload.deleteAllFromRealm();
                    }
                });
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }).start();

    }
}
