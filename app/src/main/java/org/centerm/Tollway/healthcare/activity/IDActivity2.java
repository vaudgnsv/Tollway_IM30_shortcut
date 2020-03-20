package org.centerm.Tollway.healthcare.activity;

import android.annotation.SuppressLint;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
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
import org.centerm.Tollway.healthcare.baseavtivity.devBase;
import org.centerm.Tollway.healthcare.model.CardId;
import org.centerm.Tollway.helper.RespCode;

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

public class IDActivity2 extends devBase implements View.OnClickListener {

    public static final String KEY_CARD_ID_DATA = IDActivity2.class.getName() + "_key_card_id_data";

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

    //    private boolean isShowImage = false;
    private boolean isShowImage = true;

    private String statusSale;
    private String cardIdCd;
    private final String TAG = "IDActivity2";
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
    private PosInterfaceActivity posInterfaceActivity;   // Paul_20180717

    private Dialog dialogAlert; // Paul_20180719
    private TextView msgLabel;  // Paul_20180719
    private Dialog dialogWaiting;   // Paul_20180719
    private int TimeOutFlg = 0;       // Paul_20180719

    public Button btnCancelIdCard = null;//K.GAME 180829 change UI insert idcard
    private LinearLayout linearForGeneral;//K.GAME 180910 change UI insert idcaed
    private LinearLayout linearFor7year01;//K.GAME 180910 change UI insert idcaed
    private LinearLayout linearFor7year02;//K.GAME 180910 change UI insert idcaed
    private LinearLayout linearForInserIdcard;//K.GAME 180910 change UI insert idcaed
    TextView tv_insertIdCard_01;//K.GAME 180910 change UI insert idcaed
    private Bitmap _bm;
    private ImageView img_user;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
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
        ////K.GAME 180910 change UI insert idcaed
        tv_insertIdCard_01 = findViewById(R.id.tv_insertIdCard_01);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tv_insertIdCard_01.setText(bundle.getString("tv_insertIdCard_01"));//K.GAME 180910 change UI insert idcaed
        }//END K.GAME 180910 change UI insert idcaed

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

//        if (statusSale.equals("11") || statusSale.equals("21") || statusSale.equals("31")) {//K.GAME 180910 change UI  dialog intser idcard มีบัค
//            linearForGeneral.setVisibility(View.VISIBLE);
//            linearFor7year01.setVisibility(View.GONE);
//            linearFor7year02.setVisibility(View.GONE);
//        } else {
//            linearForGeneral.setVisibility(View.GONE);
//            linearFor7year01.setVisibility(View.VISIBLE);
//            linearFor7year02.setVisibility(View.VISIBLE);
//        }
//        try {
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
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOffline) {
                    finish();
                } else {
                    finish();
                }
            }
        });


    }


    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            statusSale = bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE);
            if (bundle.getString(KEY_ID_CARD_CD) != null) {
                cardIdCd = bundle.getString(KEY_ID_CARD_CD);
            }
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
//                                        mLoading.dismiss();//K.GAME 180910 Change Ui dialog Loading
                                        dialogWaiting.dismiss();//K.GAME 180910 Change Ui dialog Loading
                                        if (statusOut() != 1) {
                                            finish();
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
                                if (statusOut() != 1)
                                    finish();
                            }
                        });
                    }

                }

                @Override
                public void onFindRFCard() throws RemoteException {

                }

                @Override
                public void onTimeout() throws RemoteException {
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "search exception");
        }
    }


    public int statusOut() {
        byte statusVal = -1;
        try {
            statusVal = this.iccard.status();
            if (statusVal == 1)
                return 1;
            else
                return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // showMessage(getResources().getString(R.string.iccard_state_result) + statusVal, Color.RED);
            //Toast.makeText(ICCardActivity.this,getResources().getString(R.string.iccard_state_result) + statusVal,Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    private void setDialogCheckIdcard() {
        Dialog dialogCheckIdcard = new Dialog(this);
        dialogCheckIdcard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCheckIdcard.setCancelable(false);
        dialogCheckIdcard.setContentView(R.layout.activity_check_idcard);
        dialogCheckIdcard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCheckIdcard.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tv_idcard = dialogCheckIdcard.findViewById(R.id.tv_idcard);
        TextView tv_username_Thai = dialogCheckIdcard.findViewById(R.id.tv_username_Thai);
        Button btnCancelIdCard = dialogCheckIdcard.findViewById(R.id.btnCancelIdCard);
        img_user = dialogCheckIdcard.findViewById(R.id.img_user);

/////////////////////////////// K.GAME 180913 //////////////////////////////////////
        dialogWaiting.show();
        tv_idcard.setText(_id_card);
        tv_username_Thai.setText(_thai_name);
        btnCancelIdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialogCheckIdcard.show();
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
                Log.d(TAG, "_photo size = " + String.valueOf(_photo.length));
                byte[] bytephoto = new byte[0];

                byte[] slice1 = Arrays.copyOfRange(_photo, 0, 2000);
                byte[] slice2 = Arrays.copyOfRange(_photo, 2000, 4000);
                byte[] slice3 = Arrays.copyOfRange(_photo, 4000, _photo.length);

                Log.d(TAG, ":" + org.centerm.Tollway.utility.Utility.bytesToHex(slice1));
                Log.d(TAG, ":" + org.centerm.Tollway.utility.Utility.bytesToHex(slice2));
                Log.d(TAG, ":" + org.centerm.Tollway.utility.Utility.bytesToHex(slice3));


                String szPhoto = new String(org.centerm.Tollway.utility.Utility.bytesToHex(_photo));
                String szCheck = szPhoto.substring(szPhoto.length() - 4, szPhoto.length());
                Log.d(TAG, "PHOTO CHECK EOF =" + szCheck);
                if (!szCheck.equalsIgnoreCase("FF9D"))
                    szPhoto = szPhoto + "FF9D";

                byte[] ppp = new byte[0];
                ppp = org.centerm.Tollway.utility.Utility.hexStringToByteArray(szPhoto);

//                Bitmap _bm = BitmapFactory.decodeByteArray(_photo, 0, _photo.length);
                _bm = BitmapFactory.decodeByteArray(ppp, 0, ppp.length);
                img_user.setImageBitmap(_bm);
                cardId.setXphoto(_bm);
            }
        });
        dialogWaiting.show();
        ///////////////////////////////END K.GAME 180913 //////////////////////////////////////
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
                            setDialogCheckIdcard();//K.GAME dialog show check id card


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
                Log.d(TAG, "_photo size = " + String.valueOf(_photo.length));
                byte[] bytephoto = new byte[0];
                //  if (_photo.length > 4538) {
                // int inck = _photo.length - 4538;
                //  bytephoto = new byte[0];
//                    for (int i = 0; i < _photo.length; i++) {
//                      // Log.d(""," "+String.valueOf(_photo[i]));
//                        Log.d("",bytesToHex(_photo[i]);
//                    }
//
                //  byte[] _photo1 = new byte[0];
                //  byte[] _photo2 = new byte[0];;
//
//
//                for (int i = 0; i < 2000; i++) {
//                    _photo1[i] =  _photo[i];
//                }
//                for (int i = 2000; i < _photo.length - 2000; i++) {
//                    _photo2[i] =  _photo[i];
//                }

                // _photo1 = _photo

                byte[] slice1 = Arrays.copyOfRange(_photo, 0, 2000);
                byte[] slice2 = Arrays.copyOfRange(_photo, 2000, 4000);
                byte[] slice3 = Arrays.copyOfRange(_photo, 4000, _photo.length);

                Log.d(TAG, ":" + org.centerm.Tollway.utility.Utility.bytesToHex(slice1));
                Log.d(TAG, ":" + org.centerm.Tollway.utility.Utility.bytesToHex(slice2));
                Log.d(TAG, ":" + org.centerm.Tollway.utility.Utility.bytesToHex(slice3));


                //   Log.d(TAG,":"+bytesToHex(_photo2));

                //  }

                String szPhoto = new String(org.centerm.Tollway.utility.Utility.bytesToHex(_photo));
                String szCheck = szPhoto.substring(szPhoto.length() - 4, szPhoto.length());
                Log.d(TAG, "PHOTO CHECK EOF =" + szCheck);
                if (!szCheck.equalsIgnoreCase("FF9D"))
                    szPhoto = szPhoto + "FF9D";


//20180824 SINN THAI ID Image some cannnot display
                //ppp = org.centerm.Tollway.utility.Utility.hexStringToByteArray("FFD8FFE000104A46494600010101006000600000FFDB0043000E0A0B0D0B090E0D0C0D100F0E11162417161414162C20211A24342E3736332E32323A4153463A3D4E3E32324862494E56585D5E5D3845666D655A6C535B5D59FFDB0043010F10101613162A17172A593B323B5959595959595959595959595959595959595959595959595959595959595959595959595959595959595959595959595959FFC000110800B2009403012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F00EE314948EDB519B05B682700649C0E83DEB35B582A7074DD44FD20CFF234C469D159275C03FE619A9E07FD3B9FF1A69D740E0E99AA67FEBD8FF8D006C525639D7876D2F553F4B63FE3487C400024E93AA8C73836FF00FD7A00D9A2B10F88403FF209D54F19FF008F7FFEBD27FC2424E31A46ABFF0080FF00FD7A00DBA2B13FE12163D347D53F1831FD68FF0084824C71A3EA7FF7E7FF00AF401B945619F1048073A3EA7FF7E7FF00AF40F11377D1F54FFBF1FF00D7A00DCA2B0C7888E70747D547FDBBFF00F5E81E21C9E748D540FF00AF7FFEBD006E5158A3C40A719D2F541FF6EFFF00D7A70D7D7FE81BA9FF00E031FF001A406C5159235C53D34DD4FF00F018FF008D28D6D7FE81FA88FADB1A00D5A2991CA1E347C326E50DB5810467D476A280253484D23E5918062A48203000907D466B847B9BA8AC7C4A1AE6591E392340E4E0E37953D3A6471C76A607764D26EAE1AD6E3ECDA56B11DBADC4132C51B6C67DC006001653D4120E4F27A8F4E1FE1491E2D6DE0577F25ED125642C48DC55093CFB93F9D0076DBA9A5C5465CF6A6939EF4012F98290C80649E82ABC922C6A598800724D711E20F113CAED6F6AF88C70CC0F53FE1401DACFAADA40ACD24CA36F5C1AE62F7C6E5242B676EACA0FDE909E7E80115C534B24AE49918FA9249A8C82073934AE3B1DD5A78E95980BBB62A3BB467FA1FF001AE8F4FD6AC75151F66994B63250F0C3F035E440E07048FA1A7C52BC6E1D18AB0390CA7041A350B1ED02419E94A1C570FA078AC964B6D4981070166F4FF7BFC6BB256DCA0820823208390681160107A1A5AAE188EF58BE2F69C68ED24572F12A101D14637E480327A8C7A77A607458E68AE135498C97A44CB349143648CBE5C9B4A12572C3D4F24743D7DB226D72537573689E6CCD08D3CCCA19B04B6D6218E3827819A00ED68AA5A233DC68D6924A77398C649C927EB45202E1358B26810BC5A946667C5FB876200F948248C7AF26B649A6134C0C383C3E8905E25C5CBCEF728B1B3EC0BB428C0C004F2303F214CD3FC3E6C5E6945E33CEF00851C201B000002064E48C0FCBDEB749A69A00C36D235227FE43B38FA443FC6ABDC6997F02976D7A7E99C6C03FAD744C42A927B5727AEDF078E590B11126540FEF37F80A18231351D42E10B446FE69C1EA49C0AC5670CC7BD3657691CB124926B674CD277209240727B54B76292B9918600803E53C8A8B273819AEB2EB4E4584EC500E39C771EB5CFCF6E15C8C60F6FAD24D31B8B454033D383DE9471FE14E652A7904534B03D4723BD50872900F620D743A0DD5DDCDC259FF0069CD6CA5711E006048E718278E3A57380638EA2A6B7764752A486520A91D4107822901E8C349D43007F6D4F904E4F9639FD6AD5D698D77A41B29EE5E46241694A8C9C1CF4FC852E8B7E351D3A39891BF1B5C0ECC3AFE7D7F1AD014C931EEF404B9937C772F0968441200A0EE50411D7A1E07E5535EF87E2BAF21A1B8784C507D9F25436E4C11839C60E09E7DEB4C1A911B1C5301B6B6EB6B6B14084ED8D42827A9C7AD1530E68A0089D822B3310154124938000EA6A8B6ADA72F5BEB61FF006D07F8D5F3CD3080739A00CF3ACE9A3ADF5BFF00DFC1FE34D3AD698339BFB7FF00BEC55F6507A807EA2986343D514FE0280322F35CD3CDBB88AF2167C7003673EB5C6EBB7714A218E1915D154124773D4FEB9AEF354891AD18845E0824E3DEBCD75C9849A84A4608DC40C7A03498D11E9F179F74A0F201CD76B0461500031815CC78762124A5C9E14E3F1AEB635E00CE07A9ACE6F53582B217CB0CBC8CFE158BAA69E705E319E092315D12A103A8229AF107073D7D6A15D17A338228581461861D3DEA9C91B2310462BB3BFD196705930AFEA075FAD73F79A7DDC00F99196519C301915A27733946C6628038278FE54F50474EA3A546E082411823B1A45723EA3F9
//5510757E13D562B29A58EE2411C3228209CE030E3A7B83FA575635DD33FE7F23FD7FC2BCE348BB16BA843303808E1BF0EFF00A57AB23875041041191421329AEB5A6B74BC87F16C7F3A95755B03822F20E7A7EF07F8D5A078A70E94C092291648D5D0EE561905704514D078E68A0024DC11B6805B07009C0CFB9AE4FF00B575392DF5A3BD44F6CF1AC6B1A0214EE21B19049040EFFA575E466B9B1A35EC675768AE16292EE40F0BA310570C4F24018C838E33D4D311521D6678AC35066B89E4BA86356093C2A9B73D08009C8E41E7D07A9A3C3DA9DE4FA9BDADCCC6746B759949500A921491C0E9F31FC85489A1DDDC2EA325DBC4B35CC4B1AED62C32A0724919E4A8E9EF4DD2746BEB2B99AEA5300945B086250C48240001270303E51D3D4D006D5F10B67396C6021EA7DABC9F54005CB007201C03EBDABBDD4A4D7059C8268EC046400761627938EF5E7F764B3293D4939FCE93DC6B637BC30A7C866F526B666B62E0996E1941EC0E00AA3E1A8B1A786C752707F1AB93D9092E37CB9914020293C03EB8E87F1AC9EACD92D08560646FDD5E311EC73FD6B46D1A55C8924F307638C562E9FA7BC066338C31036153DFD4827A75FCEB62D15C6DF306091C8A18D22CDC6E689803824706B224B48C906594903D4F15B770032051C022B1AEAD0CB14A8C3E6230849381CF7FAD24C6D5D1149A769B728515BE73D086E73F4AE5AFAD9AD2E1A36E704E0FA8AEA2CB4B31D908E554DE1B7065CE4703807AF3D68D5B4C5B8B72C7EF81907D6AAF66438DD5CE4A06C3827BD76EBAADE0F0AC53DA491A987F752B104B0C1006DEDD08C93F8570AB959307A838AEA7408A5D434CD474E8D943164750C481D79E80F602AD199AF77A8DE473A3B4B7496C2D91D9A28D4E58E32492318E6A7D4353B9FB4DB476D70CB135A99C36D19621588CE411D8703D4D2DF691A84A8A96F3218DADD61747760030C7206307A7F3A7DE68B72CD6AF6EF133456C6DD839207DD232300FA938F614C93674A9E4BBD3609D880EEBF371D48E33452E9F68B6B630C0C7718D402474CF538FC68A065E351B8EF4F3FD298DD29888CD30F7A79A61E28032BC40FB34E61EA7F9026BCC2EBA8E7915E99E2220D8919C1019BF00A47F5AF34B81927DA931AD8EB7C2D207D302E795720FF3FEB5B7E5AB93915CB784A401278C1E841C7A76FE95D5472006B2968CDE3AA13C85424E0714D8C8690E29F2B16040E6A98B95B76456462C4F240E07B935299762FCA0281C718A50AB228C804E2AB4F7AB85FDD3382704A0CE07A9E69F0B152C482149E3348691288154138AA97ACB1DBC8C7A2A927E8055E69014F7AC3D7E5F2F4C9CE7195C7E7C7F5A16AC4F4470CA774BB89EA4935D5782DF1ABCA3FBD11FCF20D72A00045745E119026B6B9200284649F607FA56E731E8E39C53C544AC38E47E752020F7CD310F1452A2923345003DD43A3230CAB0208CE3822B324D134F6CE6163FF6D1BFC6B4E46D88CDB4B1504E14649C0E83DEB12C75D178FA8992DDE08ECC0243FDEC00D9C8EC46D3C734C439B40D34F5B6CFD5DBFC698DA0698719B51C7FB6DFE35434FF0012BDD595FDCCD0227D955595549F989CF073D3903F3A9345D6E7BFBD6B5BA8A256310995A3271B4807041EFF0030E7EB4019FAFE9961690B34312C6EAB93F313924800F27D8D7172F25867915DAF8BA5554201E7E553EF8C9FEB5C3BB66538E841FE552CA5B1A3E1AB810EAA518E04AA57F11C8FEB5D91CE4E0F35E6EAED1CCB221C3290411D88AEDB4CD492FADD5C101C70CBE87FC2A26BA9A41F42E4971E41CC990BDC804E3F2A61D4EDD9B0016C7738A9800E0EE008231CD5492C8024AAA907B30E9F4350AC6F0516F564E2FA123201C770702946A10BE1413B9B80319FE555E3B1524E5235CF7009AB51DB471292065B1C9228762A6A09684A8C4A824F04715CFF008A6E00B78E107991B247B0E7F9E2B65E50AA493800735C46A976D797EEE72154ED5F600F5FC688ABBB984DD958A84FCDF81AD8D0238E7D6228E64122364107BF07158F8F9C8F7E6AEE9B72F6B7AB3C6016424807A671DFF3AD8C4F495D0F4C2011691F3F5FF1A90687A682316883E84FF8D65EA7AECD65A7D9C90C71BCD3462470C090A30327820F24814FBFD72E606B58EDE388C925B9B872E0E3014B100039CFCA7BF714C474B6D147040B146BB514600C9A2A3D2EF16F74E82E71B3CC5CEDEB83D0FEB45022D1383C571F1E9979712788A154309B899446F202AAC03313838E460E38F5AEBC9A63F4A6070D69A76A2D1EBB1C9005328C8C2901D83123613C15C67F314FD06DEEA1BF96F25B4B88E38AD163DAC84333055E141EB9DA7F4AEC89F7A8A56088CC480002493401E77E23BF377312629A219C9595769E8074FC2B9D2D8707D0D6A6BB742E6FA460723240FA66B28F2D9F7A9EA31B20DAE476AD6F0F12269403838047EB59920DC063EF01CFB8AD5F0E464CD2BE0E0600E3EB4A5B151DCEA2DE6E42B707B7BD68C6E8570541359C61DEBC0A364A0615C8F7AC6E6E69168C8385008F6AAD2B8009C8E6AB05B83C3313F40053C44E4F23F1CD2BEA32BCE0C8A40CE2B899C6DB9940ECE47EB5DF3445549381C74AE17508DA1BF99581197241F504E6AE9BD4CEA2D0858E189F5152DAA34932A20CB310001D493D2A124320F515674C6C6A1091D430C7D735A991D86A3A45F4B6492C443116D1C2D0EDCB7041383D3A8CFE069751B1BD47B29BECF2CD8B3681C42A58AB1560323D3E61CFB1AEA2260CA083C1008FA54F08F987B531116896F259E8F6B04A80C8A9F374E32738A2AF8FA514001A63F4A59033232AB1524101800483EA33C71599258DF7FD05A6FF00BF31FF0085311749AC7F11DE0B4D32421B6B3
//90AA73F9FE80D4AD617A7FE62F3FE1147FF00C4D71BE2017779A89B28E796F3CA3824AA80188E7A00001C0C9EF9A1BD012BB39D66DEE7B8CE6A3C82D91D00CD74967E149A5602590027F8579FCCD743A7F846CED983489E6B8EEDC8FCAA6E5B56DCE023B59E491408D9430C8246011D3FC6BABD26C85BAED1C12013EF5D45E6931CB1AB44804918C0C0EA3D2B3D60D879043038208C56726CA8242AAF03B1A718F3CE39A701800E29C09C5666A46148E38A5C71EF4E3DB8EB4D2703E9498C8DC641F7ACE3A345A95D849549507248382001EBEE4815A4C72300649E00F535AFA7D99B78CB301BDB19F61E9FCCFE35514EE449A48E36FBC230C38F2E47504E0375E7DC7F862B2BFB06EED6E51E3DB2A06049538207B83FFD7AF4BBC80496EE081903238EE2B0CA83915B26C21152469D9B66DE3CF0768047A1C5684038CFAD73AA590E5588FA1A985D4F8004AC00F438A1313A2D1D18A2B222D4DD2255740EC3AB6464FD78A29DD13ECE46ABB2A2B33B055504924E0003A9359916AF6B746530966588801F6E1589EBB4F7C77A9EEAE3CB52A30588E73D856613D80007603A536C210BEAC9E5BB91890B8507F13550285C9000CE49C0C7269C4E1B1480E54FD6A5BB9D0A296C8B5A6006E483FDDE2B5F6F358DA736DBB504F50456DE33418555A88062ABDD5A24C0B0187C6323BFD6AD638A080450D5CCD368C436D2019D84E3838E6A231329E548C7A8ADB236C9B87DD3C1F63EB530C10410083ED50E25A9BEA60888B8C28248EA00CD3D34E95C8C80A0FA9C9FCAB642AA021554027A018A4762AA49FC00EE6A797B8DCDF42945631C4E303730E4923A7D2AE80718E9491A90093D4F269D5692466DB7B91C8A0A303DC1AE6C8C31E7D6BA498854627B035CE9E493EB9A66F45001861EF4D2712E3B1152601E3BD452E5486F43CD06E38919A2A09250AE413F4E68A076346462CC493934CC8040F534D6623279A8C3169147A1C9A6425A0E918063EC288C1D809EA726A195B748C01EC055951870BE8281F41227F2E5561D8E6BA18D83A060410464115CF48B827D3B559B3BB96DC85642D113C1F4FA506752175736C0E28A6C6EB2286539046453F141CC35802083D0D3532A4A9FC0FA8A7914D9172BC1C10720FA1A180E273D2A3237B83FC2BD3DCD33CE0C4200431241E3A54AA00000E94805C71498A71A86E849E4379470F8E0D00B5762A6A5701233183F330C7D056401923D7148CD2BCA43649C9C927AD3F1B54F1CE3341D918A8A03D88A49143A11D88FD69E0704533381F43FA5051540423F78391C75A2A4923CB934503276E83E951C5FEB9FE868A299088D7FE3E0FF00BC2ADA7FAD6A28A0A1EFD0D310FCA7EB451412F6376D7FD427D2A73D68A2838DEE1437DDA28A181553FE3E9BFDD1FCCD581D45145240C71EB48D451420473EE3F7EFFEF1FF9D");
                byte[] ppp = new byte[0];
                ppp = org.centerm.Tollway.utility.Utility.hexStringToByteArray(szPhoto);

//                Bitmap _bm = BitmapFactory.decodeByteArray(_photo, 0, _photo.length);
                _bm = BitmapFactory.decodeByteArray(ppp, 0, ppp.length);
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
                    Intent intent = new Intent(IDActivity2.this, MenuServiceListActivity.class);
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
        dialogAlert = new Dialog(IDActivity2.this, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        dialogAlert = new Dialog(IDActivity2.this);
//        dialogAlert.requestWindowFeature( Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_alert);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout( WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
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
        org.centerm.Tollway.utility.Utility.animation_Waiting_new(dialogWaiting);
        //END K.GAME 180831 chang waitting UI
    }

    // Paul_20180719
    @Override
    protected void onResume() {
        super.onResume();

    }

    // Paul_20180719
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancelIdCard) {
            finish();
            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_SHORT).show();
        }
    }
}
