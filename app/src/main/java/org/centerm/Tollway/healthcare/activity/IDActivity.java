package org.centerm.Tollway.healthcare.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.iccard.AidlICCard;
import com.centerm.smartpos.aidl.pboc.AidlCheckCardListener;
import com.centerm.smartpos.aidl.pboc.AidlEMVL2;
import com.centerm.smartpos.aidl.pboc.ParcelableTrackData;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.HexUtil;

import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.healthcare.activity.offline.CalculateHelthCareOfflineActivity;
import org.centerm.Tollway.healthcare.baseavtivity.devBase;
import org.centerm.Tollway.healthcare.model.CardId;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_AMOUNT;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_OFFLINE;
import static org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity.KEY_ID_CARD_CD;


/**
 * Created by KisadaM on 7/13/2017.
 */

public class IDActivity extends devBase implements View.OnClickListener {

    public static final String KEY_CARD_ID_DATA = IDActivity.class.getName() + "_key_card_id_data";

    private AidlICCard iccard = null;
    private AidlEMVL2 pboc2;
    private String _cmd = "00A4040008";
    private String _thai_id_card = "A000000054480001";
    private String _req_cid = "80b0000402000d";
    //private String _req_thai_name = "80b00011020064";
    //private String _req_eng_name = "80b00075020064";
    //private String _req_gender = "80b000E1020001";
    //private String _req_dob = "80b000D9020008";
    private String _cardno = "A9EF7B30159C2CFCE9E9AC218945213B";
    private String _req_address = "80b01579020064";
    private String _req_issue_expire = "80b00167020012";
    private String _req_full_name = "80b000110200d1";
    private final Charset _UTF8_CHARSET = Charset.forName("TIS-620");
    private List<String> months_eng = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    private List<String> months_th = Arrays.asList("ม.ค.", "ก.พ.", "มี.ค.", "เม.ษ.", "พ.ค.", "มิ.ย.", "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค.");
    private List<String> religions = Arrays.asList("ไม่นับถือศาสนา", "พุทธ", "อิสลาม", "คริสต์", "พราหมณ์-ฮินดู", "ซิกข์", "ยิว", "เชน", "โซโรอัสเตอร์", "บาไฮ", "ไม่ระบุ");
    //    public int findCardTimeout = 200000; //Jeff 20180704
    public int findCardTimeout = 200000; // Paul_20180719

    private String _id_card;
    private String _thai_name;
    private String _eng_first_name;
    private String _eng_last_name;
    private String _birth_eng;
    private String _birth_th;
    //private String _gender_eng;
    //private String _gender_th;
    private String _address;
    private String _issue_eng;
    private String _issue_th;
    private String _expire_eng;
    private String _expire_th;
    private String _religion;
    private byte[] _photo;

    private TextView idcard;
    private TextView thname;
    private TextView engfname;
    private TextView englname;
    private TextView engbirth;
    private TextView thbirth;
    private TextView address;
    private TextView engissue;
    private TextView thissue;
    private TextView engexpire;
    private TextView thexpire;
    private TextView religion;
    private ImageView xphoto;
    private ProgressDialog mLoading;
    private Handler mHandler = new Handler();
    ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private boolean isShowImage = false;

    private String statusSale;
    private String cardIdCd;
    private final String TAG = "IDActivity";
    private Button nextBtn = null;
    private CardId cardId = null;
    private Runnable job;
    private String TRACK1;
    private String TRACK2;
    private String TRACK3;

    private boolean isOffline = false;

    /**
     * Type Interface
     */
    String typeInterface;
    String amountInterface;

    //    AidlSerialPort serialPort1 = null;
    private PosInterfaceActivity posInterfaceActivity;    // Paul_20180717

    private Dialog dialogAlert; // Paul_20180719
    private TextView msgLabel;  // Paul_20180719
    private Dialog dialogWaiting;   // Paul_20180719
    private int TimeOutFlg = 0;       // Paul_20180719

    public Button btnCancelIdCard = null;//K.GAME 180829 change UI insert idcard

    //game 20180903
    public static final String KEY_thainame = IDActivity.class.getName() + "_key_thainame";
    private LinearLayout linearForGeneral;//K.GAME 180910 change UI insert idcaed
    private LinearLayout linearFor7year01;//K.GAME 180910 change UI insert idcaed
    private LinearLayout linearFor7year02;//K.GAME 180910 change UI insert idcaed
    private LinearLayout linearForInserIdcard;//K.GAME 180910 change UI insert idcaed
    private TextView tv_insertIdCard_01;//K.GAME 180911 change UI insert idcaed
    private TextView tv_insertIdCard_02;//K.GAME 180911 change UI insert idcaed

    //SINN 20180911  Add topic msg.
    private String KEY_GHC_TOPIC_MSG1 = "";
    private String KEY_GHC_TOPIC_MSG2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_idcard);
        super.onCreate(savedInstanceState);

        btnCancelIdCard = findViewById(R.id.btnCancelIdCard);
        btnCancelIdCard.setOnClickListener(this);
        TimeOutFlg = 0; // Paul_20180719
        initData();
        cardId = new CardId();
//        customDialogFallBack();
//        customDialog();
        customDialogWaiting("กำลังอ่านบัตร");
//        customDialogCheckFallBack();
//        customDialogCheckCard();
        customDialogAlert();
//        customDialogHost();
//        setDialog();

        //SINN 20180911  Add topic msg.
        tv_insertIdCard_01 = (TextView) findViewById(R.id.tv_insertIdCard_01);
        tv_insertIdCard_02 = (TextView) findViewById(R.id.tv_insertIdCard_02);

//        tv_insertIdCard_01.setText("");
//        tv_insertIdCard_02.setText("");

        idcard = (TextView) findViewById(R.id.tVidcard);
        idcard.setText("");
        thname = (TextView) findViewById(R.id.tVnameTH);
        thname.setText("");
        engfname = (TextView) findViewById(R.id.tVfirstnameENG);
        engfname.setText("");
        englname = (TextView) findViewById(R.id.tVlastnameENG);
        englname.setText("");
        engbirth = (TextView) findViewById(R.id.tVbirthENG);
        engbirth.setText("");
        thbirth = (TextView) findViewById(R.id.tVbirthTH);
        thbirth.setText("");
        address = (TextView) findViewById(R.id.tVaddress);
        address.setText("");
        engissue = (TextView) findViewById(R.id.tVissueENG);
        engissue.setText("");
        thissue = (TextView) findViewById(R.id.tVissueTH);
        thissue.setText("");
        engexpire = (TextView) findViewById(R.id.tVexpireENG);
        engexpire.setText("");
        thexpire = (TextView) findViewById(R.id.tVexpireTH);
        thexpire.setText("");
        religion = (TextView) findViewById(R.id.tVreligion);
        religion.setText("");
        xphoto = (ImageView) findViewById(R.id.iVphoto);
        xphoto.setImageBitmap(null);
        xphoto.destroyDrawingCache();
        nextBtn = findViewById(R.id.nextBtn);

        linearForGeneral = findViewById(R.id.linearForGeneral);//K.GAME 180910
        linearFor7year01 = findViewById(R.id.linearFor7year01);//K.GAME 180910
        linearFor7year02 = findViewById(R.id.linearFor7year02);//K.GAME 180910
        linearForInserIdcard = findViewById(R.id.linearForInserIdcard);//K.GAME 180910
        Log.d("1919", statusSale.toString());

        if (statusSale.equals("11") || statusSale.equals("21") || statusSale.equals("31")) {//K.GAME 180910 change UI  dialog intser idcard มีบัค
            linearForGeneral.setVisibility(View.VISIBLE);
            linearFor7year01.setVisibility(View.GONE);
            linearFor7year02.setVisibility(View.GONE);
            linearForInserIdcard.setVisibility(View.GONE);
        } else if (statusSale.equals("12") || statusSale.equals("22") || statusSale.equals("32")) {
            linearForGeneral.setVisibility(View.GONE);
            linearFor7year01.setVisibility(View.VISIBLE);
            linearFor7year02.setVisibility(View.VISIBLE);
            linearForInserIdcard.setVisibility(View.GONE);
        } else {
        }
//        try {//END K.GAME 180910
//            d();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        mLoading = new ProgressDialog(this);
        mLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoading.setCanceledOnTouchOutside(false);
        mLoading.setMessage("Reading...");

        ////SINN 20180911  Add topic msg.
//        if (!KEY_GHC_TOPIC_MSG1.toString().isEmpty())
        if (KEY_GHC_TOPIC_MSG1 != null)                     // Sinn 20181015 Paul Merge
            tv_insertIdCard_01.setText(KEY_GHC_TOPIC_MSG1);
//        if (!KEY_GHC_TOPIC_MSG2.toString().isEmpty())
        if (KEY_GHC_TOPIC_MSG2 != null)                     // Sinn 20181015 Paul Merge
            tv_insertIdCard_02.setText(KEY_GHC_TOPIC_MSG2);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOffline) {
                    Intent intent = new Intent(IDActivity.this, CalculateHelthCareActivityNew.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY_CARD_ID_DATA, cardId);
                    bundle.putString(MedicalTreatmentActivity.KEY_STATUS_SALE, statusSale);
                    if (cardIdCd != null) {
                        bundle.putString(KEY_thainame, _thai_name);
                        bundle.putString(KEY_ID_CARD_CD, cardIdCd);
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    System.out.printf("utility:: %s 000000002 \n", TAG);
                    Intent intent = new Intent(IDActivity.this, CalculateHelthCareActivityNew.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY_CARD_ID_DATA, cardId);
                    bundle.putString(MedicalTreatmentActivity.KEY_STATUS_SALE, statusSale);
                    if (cardIdCd != null) {
                        bundle.putString(KEY_thainame, _thai_name);
                        bundle.putString(KEY_ID_CARD_CD, cardIdCd);
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.setIcon(R.drawable.tsslogo72);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            statusSale = bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE);
            if (bundle.getString(KEY_ID_CARD_CD) != null) {
                cardIdCd = bundle.getString(KEY_ID_CARD_CD);
            }
            ////SINN 20180911  Add topic msg.
            KEY_GHC_TOPIC_MSG1 = bundle.getString(MedicalTreatmentActivity.KEY_GHC_TOPIC_MSG1);
            KEY_GHC_TOPIC_MSG2 = bundle.getString(MedicalTreatmentActivity.KEY_GHC_TOPIC_MSG2);
            Log.d(TAG, "KEY_GHC_TOPIC_MSG1 :" + KEY_GHC_TOPIC_MSG1);
            Log.d(TAG, "KEY_GHC_TOPIC_MSG2 :" + KEY_GHC_TOPIC_MSG2);


            isOffline = bundle.getBoolean(KEY_TYPE_OFFLINE);
            Log.d(TAG, "statusSale: " + statusSale);
            if (bundle.getString(KEY_TYPE_INTERFACE) != null) {
                typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
                statusSale = bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE);

                Log.d(TAG, "KEY_TYPE_INTERFACE : " + bundle.getString(KEY_TYPE_INTERFACE));
                if (bundle.getString(KEY_INTERFACE_AMOUNT) != null) {
                    amountInterface = bundle.getString(KEY_INTERFACE_AMOUNT);
                    Log.d(TAG, "amountInterface : " + amountInterface);
                }
            }
        }
        posInterfaceActivity = MainApplication.getPosInterfaceActivity();      // Paul_20180717
    }


    private void setDialogShowQuestion() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        alertDialog.setTitle("คุณต้องการแสดงรูป ใช่ หรือ ไม่");
        alertDialog.setCancelable(false);


        alertDialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isShowImage = false;
//                if (iccard != null) {
////                        d();
                allOperateStart(true, true, false, "Searching the card", "");
//                    scheduledExecutor.scheduleAtFixedRate(job, 1000, 1000, TimeUnit.MILLISECONDS);
//                }

            }
        });
        alertDialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isShowImage = true;
                if (iccard != null) {
                    scheduledExecutor.scheduleAtFixedRate(job, 1000, 1000, TimeUnit.MILLISECONDS);
                }
            }
        });

        // alertDialog.show();
        //Auto say no
        isShowImage = false;
        allOperateStart(true, true, false, "Searching the card", "");
        // Initially disable the button

        //end auto say no


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (iccard != null) {
                scheduledExecutor.shutdown();
                iccard.close();
                iccard = null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeviceConnected(AidlDeviceManager deviceManager) {
        try {
            this.iccard = AidlICCard.Stub.asInterface(deviceManager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_ICCARD));
            this.pboc2 = AidlEMVL2.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            setDialogShowQuestion(); //sinn off prompt
            // isShowImage = false;   //sinn off
            // allOperateStart(true, true, false, "Searching the card", ""); //sinn off

        }
    }

    private static String e(int value) {
        String hex = Integer.toHexString(value);
        hex = hex.length() % 2 == 1 ? "0" + hex : hex;
        return hex.toUpperCase();
    }


    public void allOperateStart(final boolean isCheckMag,
                                final boolean isCheckIC, final boolean isChecRF,
                                final String msgPrompt, final String msg) {
        //showMessage(msgPrompt);
        Log.d(TAG, msgPrompt);
        try {
            pboc2.checkCard(isCheckMag, isCheckIC, false, findCardTimeout, new AidlCheckCardListener.Stub() {

                @Override
                public void onCanceled() throws RemoteException {
                    Log.d(TAG, "pboc2 onCanceled");
//                            CheckCardCallback(CHECKCARD_ONCANCEL);
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    Log.d(TAG, "pboc2 onError : " + arg0);
                }


                @Override
                public void onFindMagCard(ParcelableTrackData arg0)
                        throws RemoteException {
                    System.out.println("TAG:" + arg0.getCardNo());
                    TRACK1 = BlockCalculateUtil.get45BlockData(HexUtil.bcd2str(arg0.getFirstTrackData()));
                    TRACK2 = get35Data2(arg0);

                    TRACK3 = new String(arg0.getThirdTrackData());

                    Log.d(TAG, "TRACK1 :" + TRACK1);
                    Log.d(TAG, "TRACK2 :" + TRACK2);
                    Log.d(TAG, "TRACK3 :" + TRACK3);

                    //String[] name = TRACK1.split("5E");
                    //Log.d(TAG, "NAME  :" + name);

                    _id_card = " ";
                    _thai_name = " ";
                    _eng_first_name = " ";
                    _eng_last_name = " ";
                    _birth_eng = " ";
                    _birth_th = " ";
                    _address = " ";
                    _issue_eng = " ";
                    _issue_th = " ";
                    _expire_eng = " ";
                    _expire_th = " ";
                    _religion = " ";

                    _id_card = TRACK2.substring(2, 2 + 13);

                    idcard.setText(_id_card);
                    cardId.setIdCard(_id_card);

                }

                @Override
                public void onSwipeCardFail() throws RemoteException {

                }

                @Override
                public void onFindICCard() throws RemoteException {
                    boolean _read = false;

                    iccard.open();
                    if (iccard.status() == 1) {
                        if (iccard.reset() != null && !_read) {
                            if (iccard.sendAsync(HexUtil.hexStringToByte(_cmd + _thai_id_card)) != null) {
                                _read = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        mLoading.show();//K.GAME 180910 Change Ui dialog Loading
                                        dialogWaiting.show();//K.GAME 180910 Change Ui dialog Loading
                                    }
                                });
                                a(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_cid)), _UTF8_CHARSET), 0);
                                a(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_full_name)), _UTF8_CHARSET), 1);
                                a(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_address)), _UTF8_CHARSET), 2);
                                a(new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_issue_expire)), _UTF8_CHARSET), 3);
                                Log.d(TAG, "run: " + _req_full_name);
                                if (isShowImage)
                                    m();
                                //iccard.close();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                    mLoading.show();//K.GAME 180910 Change Ui dialog Loading
                                        dialogWaiting.dismiss();//K.GAME 180910 Change Ui dialog Loading
                                        if (!isOffline) {
                                            Intent intent = new Intent(IDActivity.this, CalculateHelthCareActivityNew.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable(KEY_CARD_ID_DATA, cardId);
                                            if (typeInterface != null) {
                                                bundle.putString(KEY_TYPE_INTERFACE, typeInterface);
                                                bundle.putString(KEY_INTERFACE_AMOUNT, amountInterface);
                                                Log.d(TAG, "bundle.putString _thai_name :" + _thai_name);
                                                bundle.putString(KEY_thainame, _thai_name);
                                            }
                                            bundle.putString(MedicalTreatmentActivity.KEY_STATUS_SALE, statusSale);
                                            if (cardIdCd != null) {
                                                bundle.putString(KEY_thainame, _thai_name);
                                                bundle.putString(KEY_ID_CARD_CD, cardIdCd);
                                            }
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(IDActivity.this, CalculateHelthCareOfflineActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable(KEY_CARD_ID_DATA, cardId);
                                            bundle.putString(MedicalTreatmentActivity.KEY_STATUS_SALE, statusSale);
                                            if (cardIdCd != null) {
                                                bundle.putString(KEY_ID_CARD_CD, cardIdCd);
                                                bundle.putString(IDActivity.KEY_thainame, _thai_name);
                                            }
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        _read = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                c();
//                                mLoading.dismiss();//K.GAME 180910 Change Ui dialog Loading
                                dialogWaiting.dismiss();//K.GAME 180910 Change Ui dialog Loading
                            }
                        });
                    }

                }

                @Override
                public void onFindRFCard() throws RemoteException {

                }

                @Override
                public void onTimeout() throws RemoteException {
// Paul_20180719
                    System.out.printf("utility:: IDActivity.java onTimeout \n");
                    TimeOutFlg = 1;
                    finish();

//                    if(typeInterface != null) {
//                        System.out.printf("utility:: IDActivity.java onTimeout 0001 \n");
////                        Utility.customDialogAlertAuto( IDActivity.this, "ไม่สามารถเชื่อมต่อได้" );
//                        TellToPosError( "EN" );
//                        posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                            @Override
//                            public void success() {
//                                Intent intent = new Intent( IDActivity.this, MenuServiceListActivity.class );
//                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
//                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                                startActivity( intent );
//                                finish();
//                                overridePendingTransition( 0, 0 );
//                            }
//                        });
//                    }
//                    else
//                    {
//                        System.out.printf("utility:: IDActivity.java onTimeout 0002 \n");
////                        Utility.customDialogAlert(IDActivity.this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
////                            @Override
////                            public void onClickImage(Dialog dialog) {
////                                dialog.dismiss();
////                                Intent intent = new Intent(IDActivity.this, MenuServiceListActivity.class);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                startActivity(intent);
//                                finish();
////                                overridePendingTransition(0, 0);
////                            }
////                        });
//
//                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "search exception");
        }
    }

    private String a(String _val, int _index) {
        String _xx = _val;
        Log.d(TAG, "a: " + _val);
        Log.d(TAG, "a: " + BlockCalculateUtil.getASCIIString(_val));
        switch (_index) {
            case 0:
                if (_xx != null | _xx.length() != 0) {
                    _xx = _val.replaceAll("#", " ");
                    _xx = _xx.substring(0, _xx.length() - 2);
                    char[] achars = _xx.toUpperCase().toCharArray();
                    _id_card = achars[0] + " " + achars[1] + achars[2] + achars[3] + achars[4] + " " + achars[5] + achars[6] + achars[7] + achars[8] + achars[9] + " " + achars[10] + achars[11] + " " + achars[12];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            idcard.setText(_id_card);

                            cardId.setIdCard(_id_card);
                        }
                    });
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            thname.setText(_thai_name);
                            engfname.setText(_eng_first_name);
                            englname.setText(_eng_last_name);
                            thbirth.setText(_birth_th);
                            engbirth.setText(_birth_eng);

                            /**
                             * Set DataCard Id
                             */
                            cardId.setThName(_thai_name);
                            cardId.setEngFName(_eng_first_name);
                            cardId.setEngLName(_eng_last_name);
                            cardId.setThBirth(_birth_th);
                            cardId.setEngBirth(_birth_eng);

                        }
                    });
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            address.setText(_address);
                            /**
                             * Set DataCard Id
                             */
                            cardId.setAddress(_address);
                        }
                    });
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            engissue.setText(_issue_eng);
                            thissue.setText(_issue_th);
                            engexpire.setText(_expire_eng);
                            thexpire.setText(_expire_th);
                            religion.setText(_religion);
                            /**
                             * Set DataCard Id
                             */
                            cardId.setEngIssue(_issue_eng);
                            cardId.setThIssue(_issue_th);
                            cardId.setEngExpire(_expire_eng);
                            cardId.setThExpire(_expire_th);
                            cardId.setReligion(_religion);
                        }
                    });
                }
                break;
            default:
        }
        return _xx;
    }

    private void c() {
        idcard.setText("");
        thname.setText("");
        engfname.setText("");
        englname.setText("");
        engbirth.setText("");
        thbirth.setText("");
        address.setText("");
        engissue.setText("");
        thissue.setText("");
        engexpire.setText("");
        thexpire.setText("");
        religion.setText("");
        xphoto.setImageBitmap(null);
        xphoto.destroyDrawingCache();
    }

    private void m() {
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap _bm = BitmapFactory.decodeByteArray(_photo, 0, _photo.length);
                xphoto.setImageBitmap(_bm);
                cardId.setXphoto(_bm);
            }
        });
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

    // Paul_20180717
    public void TellToPosCancel() {
/*        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        DateFormat timeFormat = new SimpleDateFormat("HHmmss");
        posInterfaceActivity.PosInterfaceWriteField("02", posInterfaceActivity.ResponseMsgPosInterface("ND"));   // Response Message
        posInterfaceActivity.PosInterfaceWriteField("D0", "                                                                     ");   //
        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        posInterfaceActivity.PosInterfaceWriteField("03", dateFormat.format(date));   // Date YYMMDD
        posInterfaceActivity.PosInterfaceWriteField("04", timeFormat.format(date));   // Time HHMMSS
//        PosInterfaceWriteField("30","");   // Card No
        posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode, "ND");*/
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("ND"));
        posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode, "ND");
    }

    @Override
    public void onBackPressed() {
// Paul_20180717
        System.out.printf("utility:: IDActivity onBackPressed \n");
        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
            TellToPosCancel();
            posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    Intent intent = new Intent(IDActivity.this, MenuServiceListActivity.class);
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
    }

    public void TellToPosError(String szErr) {
//        posInterfaceActivity.PosInterfaceWriteField("01","000xxxxxx");   // Approval Code
//        //posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("12"));   // Response Message TX NOT FOUND
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface(szErr));
//
//        posInterfaceActivity.PosInterfaceWriteField("65","000000");   // Invoice Number
//        posInterfaceActivity.PosInterfaceWriteField("D3","xxxxxxxxxxxx");
//
//        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
//        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
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

        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232(szErr));
        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, szErr);
    }

    // Paul_20180719
    public void customDialogAlert() {
        if (dialogAlert != null) {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        }
        dialogAlert = new Dialog(IDActivity.this, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        dialogAlert = new Dialog(IDActivity.this);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_alert);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821
        btn_close.setOnClickListener(new View.OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                dialogAlert.dismiss();
            }
        });

    }

    private void customDialogWaiting(String textView) {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        TextView msgLabel = dialogWaiting.findViewById(R.id.msgLabel);
        msgLabel.setText(textView);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogWaiting);
        //END K.GAME 180831 chang waitting UI
    }

    // Paul_20180719
    @Override
    protected void onResume() {
        super.onResume();
        System.out.printf("utility:: %s onResume \n", TAG);
//        if (realm.isClosed()) {
//            realm = Realm.getDefaultInstance();
//        }
//        if(typeInterface!=null) {
//            submitAmount();

//            Button okBtn = dialogParaEndble.findViewById(R.id.okBtn);
//            okBtn.performLongClick();
//        }

    }

    // Paul_20180719
    @Override
    protected void onPause() {
        super.onPause();
        System.out.printf("utility:: %s onPause \n", TAG);
        if (TimeOutFlg == 1) {
            TimeOutFlg = 0;
            if (typeInterface != null) {
                System.out.printf("utility:: IDActivity.java onTimeout 0001 \n");
//                Utility.customDialogAlertAuto( IDActivity.this, "ไม่สามารถเชื่อมต่อได้" );
                TellToPosError("21");
                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                    @Override
                    public void success() {
//                        Utility.customDialogAlertAutoClear();
                        Intent intent = new Intent(IDActivity.this, MenuServiceListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            } else {
                System.out.printf("utility:: IDActivity.java onTimeout 0002 \n");
//                Utility.customDialogAlert(IDActivity.this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
//                     @Override
//                     public void onClickImage(Dialog dialog) {
//                        dialog.dismiss();
                Intent intent = new Intent(IDActivity.this, MenuServiceListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                finish();
                overridePendingTransition(0, 0);
//                     }
//                 });
            }
        }
//        realm.close();
//        posInterfaceActivity.removeAckToPrint();
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancelIdCard) {
            finish();
            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_SHORT).show();
        }
    }
}
