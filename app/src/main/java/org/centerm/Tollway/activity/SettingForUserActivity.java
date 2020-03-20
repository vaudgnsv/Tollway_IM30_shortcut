package org.centerm.Tollway.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.centermposoversealib.tleservice.AidlTleService;
import com.centerm.centermposoversealib.tleservice.TleParamMap;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;

import org.centerm.Tollway.BuildConfig;
import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.GameActivity;
import org.centerm.Tollway.adapter.SettingForUserAdapter;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.core.ChangeFormat;
import org.centerm.Tollway.core.CustomSocketListener;
import org.centerm.Tollway.core.DataExchanger;
import org.centerm.Tollway.database.ReversalHealthCare;
import org.centerm.Tollway.database.ReversalTemp;
import org.centerm.Tollway.database.TCUpload;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.healthcare.activity.MedicalTreatmentActivity;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmResults;

import static org.centerm.Tollway.activity.MenuServiceActivity.KEY_TYPE_PASSWORD;
import static org.centerm.Tollway.activity.MenuServiceActivity.TYPE_ADMIN_PASSWORD;
import static org.centerm.Tollway.activity.MenuServiceActivity.TYPE_NORMAL_PASSWORD;
import static org.centerm.Tollway.core.ChangeFormat.bcd2Str;
import static org.centerm.Tollway.utility.Utility.getLength62;

public class SettingForUserActivity extends AppCompatActivity {
    private final String TAG = "SettingForUserActivity";

    private RecyclerView recyclerViewSettingForUser;
    private LinearLayout linearLayoutToolbarBottom = null;
    private AlertDialog.Builder builder;
    private org.centerm.Tollway.adapter.SettingForUserAdapter SettingForUserAdapter;
    private ArrayList<String> settingForUserList = null;
    private boolean isOffline = false;
    private Button btn_firstSettlement_TMS; //TMS First Settlement
    //GHC First Settlement
    private String[] mBlockDataSend;//GHC First Settlement
    private CustomSocketListener customSocketListener;
    private ExecutorService sFixedThreadPool;
    private String PRIMARY_HOST;
    private String PRIMARY_PORT;
    private String SECONDARY_HOST;
    private String SECONDARY_PORT;
    private AidlTleService tleVersionOne;
    private String[] mBlockDataReceived;
    //END GHC First Settlement
    //Dialog
    private Dialog dialogPassword;
    private Dialog dialog_FirstSettlement;
    private Dialog dialog_ParameterDownload;
    private Dialog dialogWaiting;
    private Dialog dialogAlertSuccess;
    private Dialog dialogVersion;//K.GAME 181025
    private Dialog dialogHost;
    //END dialog
    //dialog host
    private TextView tv_title_host;

    private Button posBtn = null;
    private Button epsBtn = null;
    private Button tmsBtn = null;
    private Button ghcBtn = null;

    private Button closeImage = null;
    private Button btn_ok = null;

    private String typeClick = null;

    private Realm realm = null;
    //end dialog host
    private CardManager cardManager = null;
    private EditText passwordBox;
    private Button cancelBtn;
    private Button okBtn;
    public static final String KEY_TYPE_OFFLINE = "key_type_offline";
    //K.GAME 181017 New Ui dialog PIN
    private ImageView img_krungthai1;//K.GAME 181016
    private ImageView img_krungthai2;//K.GAME 181016
    private Dialog dialogcustomDialogPin_new;//K.GAME 181017 New UI
    private EditText pinBox_new = null;//K.GAME 181017 New UI
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
    private LinearLayout numberLinearLayout;
    private String numberPrice = "";
    private String typeInterface;

    private Dialog dialogAlert;     // Paul_20181029 Add to showMessageResCode
    private TextView msgLabel;      // Paul_20181029 Add to showMessageResCode

    //END K.GAME 181017 New Ui dialog PIN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_setting_for_user);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
        initData();
        initWidget();
        initBtnExit();
    }

    private void initData() {
    }

    public void initWidget() {
        //K.GAME 181016 hard code
        img_krungthai1 = findViewById( R.id.img_krungthai1);
        img_krungthai2 = findViewById( R.id.img_krungthai2);
        if (!Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {

            img_krungthai1.setVisibility(View.INVISIBLE);
            img_krungthai2.setVisibility(View.VISIBLE);
        }//END K.GAME 181016 hard code


        // super.initWidget();
        settingForUserList = new ArrayList<>();
        settingForUserList.clear();

//        settingForUserList.add("เริ่มต้น\nการเชื่อมต่อ");
        settingForUserList.add("สรุปยอด\nครั้งแรก");
//        settingForUserList.add("ตรวจสอบ\nการอัปเดต");
        settingForUserList.add("ดาวน์โหลด\nParameter");
        settingForUserList.add("Clear\nReversal");
        settingForUserList.add("Clear\nBatch");
        settingForUserList.add("เวอร์ชั่น");
        settingForUserList.add("ขั้นสูง");
//        settingForUserList.add("test");


        recyclerViewSettingForUser = findViewById( R.id.recyclerViewSettingForUser);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); //K.GAME Test
        gridLayoutManager.setSpanCount(3);//K.GAME Test
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);//K.GAME Test
        recyclerViewSettingForUser.setLayoutManager(layoutManager);
        setMenuList();
        /////////// Dialog //////////☻☻☻☻☻☻☻☻
        customDialog_FirstSettlement();// K.GAME 180921 New dialog
        customDialog_ParameterDownload();// K.GAME 180921 New dialog
        customDialogWaiting();
        customDialogSuccess();
        customDialogVersion();//K.GAME 181025
        customDialogHost();
        customDialogAlert();    // Paul_20181029 Add to showMessageResCode
        ///////////END Dialog ///////☻☻☻☻☻☻☻☻
    }

    private void customDialog_ParameterDownload() {
        dialog_ParameterDownload = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180828 change UI
        View view = dialog_ParameterDownload.getLayoutInflater().inflate( R.layout.dialog_custom_parameter_download, null);//K.GAME 180828 change UI
        dialog_ParameterDownload.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change UI
        dialog_ParameterDownload.setContentView(view);//K.GAME 180828 change UI
        dialog_ParameterDownload.setCancelable(false);//K.GAME 180828 change UI

//        dialog_FirstSettlement = new Dialog(this);
//        dialog_FirstSettlement.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog_FirstSettlement.setCancelable(false);
//        dialog_FirstSettlement.setContentView(R.layout.dialog_custom_first_settlement);
//        dialog_FirstSettlement.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog_FirstSettlement.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button btn_ParameterDownload_TMS = dialog_ParameterDownload.findViewById( R.id.btn_parameter_TMS);
        Button btn_ParameterDownload_GHC = dialog_ParameterDownload.findViewById( R.id.btn_parameter_GHC);
        Button btn_close = dialog_ParameterDownload.findViewById( R.id.btn_close);
        btn_ParameterDownload_TMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWaiting.show();
                showMessageResCode();       // Paul_20181029 Add to showMessageResCode
                cardManager.setDataParameterDownload();
            }
        });
        btn_ParameterDownload_GHC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWaiting.show();
                setDataParameterDownloadGHC();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_ParameterDownload.dismiss();
            }
        });


    }

    private int checkAllBatch() {
        return 0;
    }

    private void setMenuList() {
        {
            if (recyclerViewSettingForUser.getAdapter() == null) {

                SettingForUserAdapter = new SettingForUserAdapter(this);
                recyclerViewSettingForUser.setAdapter(SettingForUserAdapter);


                SettingForUserAdapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        switch (settingForUserList.get(position)) {
                            case "เริ่มต้น\nการเชื่อมต่อ":
                                Toast.makeText(SettingForUserActivity.this, "ยังไม่เปิดใช้งาน", Toast.LENGTH_SHORT).show();
                                break;
                            case "สรุปยอด\nครั้งแรก":

                                if (!(Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()==8))
                                {
                                    dialogWaiting.show();
                                    cardManager.setDataFirstSettlement();
                                }
                                else
                                    dialog_FirstSettlement.show();

                                break;
                            case "ตรวจสอบ\nการอัปเดต":
                                Toast.makeText(SettingForUserActivity.this, "ยังไม่เปิดใช้งาน", Toast.LENGTH_SHORT).show();
                                break;
                            case "ดาวน์โหลด\nParameter":
                                dialog_ParameterDownload.show();
                                break;
                            case "Clear\nReversal":
                                typeClick = "REVERSAL";
                                if (typeClick.equals("REVERSAL")) {
                                    tv_title_host.setText("Clear Reversal");
                                }
//                                dialogHost.show();
                                //SINN 20181113 reversal no need select host.
                                deleteReversal();
                                break;
                            case "Clear\nBatch":
                                typeClick = "BATCH";
//                                if (typeClick.equals("BATCH")) {
//                                    tv_title_host.setText("Clear Batch");
//                                }
//                                dialogHost.show();

                                if(Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")||
                                        ((Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)&&
                                         (Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)&&
                                         (Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)))
                                {
                                    deleteBatch("EPS");
                                    deleteBatch_TCupload("EPS");  ////20181104 SINN settlement hang
                                }
                                else
                                {
                                    if (typeClick.equals("BATCH")) {
                                    tv_title_host.setText("Clear Batch");
                                }
                                dialogHost.show();
                                }


                                break;
                            case "เวอร์ชั่น":
                                dialogVersion.show();//K.GAME 181025
                                break;
                            case "ขั้นสูง":
                                //K.GAME change Menu setting and UI
                                if (checkAllBatch() != 1)    // Paul_20180803
                                    CardPrefix.getStringJson(SettingForUserActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                                Log.d("SINN", "customDialogPassword()");
                                isOffline = false;
//                                customDialogPassword();//K.GAME 181017 comment
                                customDialogPin_new();//K.GAME 181017 New dialog Admin PIN
                                dialogcustomDialogPin_new.show();//K.GAME 181017 New dialog Admin PIN
                                break;
                            case "test":
                                Intent intent4 = new Intent(SettingForUserActivity.this, GameActivity.class);
                                startActivity(intent4);
                                overridePendingTransition(0, 0);
                                break;
                        }

                    }
                });
            } else {
                SettingForUserAdapter.clear();
            }
            SettingForUserAdapter.setItem(settingForUserList);
            SettingForUserAdapter.notifyDataSetChanged();
        }
    }

    private void dialogResponseSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogAlertSuccess != null) {
                    if (!dialogAlertSuccess.isShowing()) {
                        dialogAlertSuccess.show();
                    }
                }
            }
        });
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        cardManager = MainApplication.getCardManager();
    }

    private void callBackResponseCode() {
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(String response) {
                if (getApplication() != null) {
                    dialogWaiting.dismiss();
                    if (!isFinishing()) {
                        Log.d("1919", "เข้านะ1");
                        dialogResponseError(response);
                    }
                }
            }

            @Override
            public void onResponseCodeandMSG(String response, String szCode) {
                System.out.printf("utility:: TerminalTMSFr onResponseCodeandMSG 000006 \n");
                if (dialogWaiting != null)//K.GAME 180925
                    dialogWaiting.dismiss();
                dialogResponseError(response);//K.GAME 180925
            }

            @Override
            public void onResponseCodeSuccess() {
                System.out.printf("utility:: %s onResponseCodeSuccess \n",TAG);
                if (getApplication() != null) {
                    dialogWaiting.dismiss();
                    if (!isFinishing()) {
                        Log.d("1919", "เข้านะ2");
                        dialogResponseSuccess();
                    }
                }
            }

            @Override
            public void onConnectTimeOut() {
                System.out.printf("utility:: %s onConnectTimeOut \n",TAG);
                if (getApplication() != null) {
                    dialogWaiting.dismiss();
                    if (!isFinishing()) {
                        Log.d("1919", "เข้านะ3");
                        dialogResponseError(null);
                    }
                }
            }

            @Override
            public void onTransactionTimeOut() {
                System.out.printf("utility:: %s onTransactionTimeOut \n",TAG);
                if (getApplication() != null) {
                    dialogWaiting.dismiss();
                    if (!isFinishing()) {
                        Log.d("1919", "เข้านะ4");
                        dialogResponseError(null);
                    }
                }
            }
        });
    }

    private AidlDeviceManager managerTle;
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

    private void tle_initialize(AidlDeviceManager deviceManager) {
        try {
            tleVersionOne = AidlTleService.Stub.asInterface(deviceManager.getDevice(999));
            Log.d(TAG, "TLE Service is " + ((tleVersionOne != null) ? "not null" : "null"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void deleteBatch(final String typeHOst) {
        System.out.printf("utility:: NMXInfoFragment deleteBatch = %s\n", typeHOst);
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                System.out.printf("utility:: NMXInfoFragment deleteBatch 002 = %s\n", typeHOst);
                final RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHOst).findAll();
                if (transTemps != null) {
                    System.out.printf("utility:: NMXInfoFragment transTemps.size() = %d\n", transTemps.size());
                    transTemps.deleteAllFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Utility.customDialogAlertSuccess(SettingForUserActivity.this, "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Utility.customDialogAlert(SettingForUserActivity.this, "ลบข้อมูลล้มเหลว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        realm.close();
        realm = null;
    }


    private void deleteBatch_TCupload(final String typeHOst) {
        System.out.printf("utility:: NMXInfoFragment deleteBatch = %s\n", typeHOst);
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                System.out.printf("utility:: NMXInfoFragment deleteBatch 002 = %s\n", typeHOst);
                final RealmResults<TCUpload> transTemps = realm.where(TCUpload.class).equalTo("hostTypeCard", typeHOst).findAll();
                if (transTemps != null) {
                    System.out.printf("utility:: NMXInfoFragment transTemps.size() = %d\n", transTemps.size());
                    transTemps.deleteAllFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Utility.customDialogAlertSuccess(SettingForUserActivity.this, "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Utility.customDialogAlert(SettingForUserActivity.this, "ลบข้อมูลล้มเหลว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        realm.close();
        realm = null;
    }




    private void removeReversalHealthCareSetup() {
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(ReversalHealthCare.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Utility.customDialogAlertSuccess(SettingForUserActivity.this, "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Utility.customDialogAlert(SettingForUserActivity.this, "ลบข้อมูลล้มเหลว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        realm.close();
        realm = null;
    }

    private void deleteReversal() {
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ReversalTemp> reversalTemp = realm.where(ReversalTemp.class).findAll();
                reversalTemp.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Utility.customDialogAlertSuccess(SettingForUserActivity.this, "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Utility.customDialogAlert(SettingForUserActivity.this, "ลบข้อมูลล้มเหลว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        realm.close();
        realm = null;
    }

    private void customDialogVersion() {
//        dialogVersion = new Dialog(this, R.style.ThemeWithCorners);//K.GAME 180821
//        View view = dialogVersion.getLayoutInflater().inflate(R.layout.dialog_custom_show_version, null);//K.GAME 180821
//        dialogVersion.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
//        dialogVersion.setContentView(view);//K.GAME 180821
//        dialogVersion.setCancelable(false);//K.GAME 180821

        dialogVersion = new Dialog(SettingForUserActivity.this);
        dialogVersion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogVersion.setContentView( R.layout.dialog_custom_show_version);
        dialogVersion.setCancelable(false);
        dialogVersion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogVersion.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tv_softVer = dialogVersion.findViewById( R.id.tv_softVer);
        Button btn_success = dialogVersion.findViewById( R.id.btn_dialog_success);//K.GAME 180821

//        tv_softVer.setText("Software Version Test 0001");
        tv_softVer.setText(BuildConfig.VERSION_NAME);       // Paul_20181028 Sinn merge version UAT6_0016
        btn_success.setOnClickListener(new View.OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                dialogVersion.dismiss();
            }
        });//K.GAME 180821
    }

    private void customDialogSuccess() {
        dialogAlertSuccess = new Dialog(this, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlertSuccess.getLayoutInflater().inflate( R.layout.dialog_custom_success, null);//K.GAME 180821
        dialogAlertSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlertSuccess.setContentView(view);//K.GAME 180821
        dialogAlertSuccess.setCancelable(false);//K.GAME 180821

//        dialogAlertSuccess = new Dialog(getContext());
//        dialogAlertSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlertSuccess.setContentView( R.layout.dialog_custom_success);
//        dialogAlertSuccess.setCancelable(false);
//        dialogAlertSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlertSuccess.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlertSuccess.findViewById( R.id.msgLabel);
        Button btn_success = dialogAlertSuccess.findViewById( R.id.btn_dialog_success);//K.GAME 180821
        btn_success.setOnClickListener(new View.OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                dialogAlertSuccess.dismiss();
            }
        });//K.GAME 180821
    }

    private void customDialogHost() {
        dialogHost = new Dialog(SettingForUserActivity.this, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogHost.getLayoutInflater().inflate( R.layout.dialog_custom_host, null);//K.GAME 180821
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogHost.setContentView(view);//K.GAME 180821
        dialogHost.setCancelable(false);//K.GAME 180821

//        dialogHost = new Dialog(getContext());
//        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogHost.setCancelable(false);
//        dialogHost.setContentView(R.layout.dialog_custom_host);
//        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        tv_title_host = dialogHost.findViewById( R.id.tv_title_host);
        posBtn = dialogHost.findViewById( R.id.posBtn);
        epsBtn = dialogHost.findViewById( R.id.epsBtn);
        tmsBtn = dialogHost.findViewById( R.id.tmsBtn);
        ghcBtn = dialogHost.findViewById( R.id.ghcBtn);
//        if (typeClick.equals("BATCH")) {
//            tv_title_host.setText("Clear Batch");
//        }
//        if (typeClick.equals("REVERSAL")) {
//            tv_title_host.setText("Clear REVERSAL");
//        }

        //SINN 20181119  AXA no need select host

        if (Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length()<8)
            tmsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS).length()<8)
            posBtn.setVisibility(View.GONE);
        if (Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length()<8)
            epsBtn.setVisibility(View.GONE);
        if (Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_TERMINAL_ID_GHC).length()<8)
            ghcBtn.setVisibility(View.GONE);
//END SINN 20181119  AXA no need select host





        closeImage = dialogHost.findViewById( R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.dismiss();
            }
        });
        btn_ok = dialogHost.findViewById( R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.dismiss();
            }
        });
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("REVERSAL")) {
                    deleteReversal();
                } else {
                    deleteBatch("POS");
                    deleteBatch_TCupload("EPS");  ////20181104 SINN settlement hang
                }
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("REVERSAL")) {
                    deleteReversal();
                } else {
                    deleteBatch("EPS");
                    deleteBatch_TCupload("EPS");  ////20181104 SINN settlement hang
                }
            }
        });

        tmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("REVERSAL")) {
                    deleteReversal();
                } else {
                    deleteBatch("TMS");
                }
            }
        });

        ghcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("REVERSAL")) {
                    System.out.printf("utility:: GHC Reversal Clear 001 \n");
                    removeReversalHealthCareSetup();        // Paul_20180706
                    deleteReversal();
//                    deleteReversal();
                } else {
                    System.out.printf("utility:: GHC Reversal Clear 002 \n");
                    deleteBatch("GHC");
//                    deleteBatchGHC("GHC");
                }
            }
        });
    }

    private void setDataFirstSettlementGHC() {
        String terminalVersion = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_TERMINAL_VERSION);
        String messageVersion = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_MESSAGE_GHC_VERSION);
        String transactionCode = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_TRANSACTION_CODE);
        String messageLen = "00000106";
        String terminalSN = "88888888";        // Paul_20180522
        String samId = "5555555555555556";      // Paul_20180522
        String samCsn = "4444444444444445";     // Paul_20180522
        String randomData = "00000000";         // Paul_20180522
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "00000000";           // Paul_20180522
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "920000";
        mBlockDataSend[24 - 1] = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_NII_GHC);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString( CardPrefix.getTerminalId(SettingForUserActivity.this, "GHC"));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString( CardPrefix.getMerchantId(SettingForUserActivity.this, "GHC"));
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((messageLen + terminalVersion + messageVersion + transactionCode + terminalSN + samId + samCsn + randomData + checkSUM).length())) + BlockCalculateUtil.getHexString(messageLen + terminalVersion + messageVersion + transactionCode + terminalSN + samId + samCsn + randomData + checkSUM);
        packageAndSend( Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_TPDU_GHC), "0800", mBlockDataSend);

    }

    protected void packageAndSend(String TPDU, String messageType, String[] mBlockData) {
        //processCallback(PROCESS_REQUEST_INSERT_DB);
        Log.d(TAG, "packageAndSend: " + mBlockData.toString());


        String applicationData = BlockCalculateUtil.calculateApplicationData(mBlockData);
        String dataToSend = "";
        dataToSend = dataToSend + TPDU;
        dataToSend = dataToSend + messageType;
        dataToSend = dataToSend + applicationData;
        dataToSend = dataToSend.trim();

        Log.d(TAG, "Raw packageAndSend => " + dataToSend);

        dataToSend = OnUsEncryptionMsg(dataToSend);
// Paul_20180522 End
        if (dataToSend != null) {
            Log.d(TAG, "Encrypted DATATOSEND => " + dataToSend);
            sendStr(dataToSend);
        } else {
            Log.d(TAG, "Encrypted Data is return NULL!!!");
            //sendStr(plainData);
        }
    }

    private void connect() {
        customSocketListener = new CustomSocketListener() {
            @Override
            public void ConnectTimeOut() {
                Log.d(TAG, "ConnectTimeOut: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectTimeOut();
                    }
                });

            }

            @Override
            public void TransactionTimeOut() {
                Log.d(TAG, "TransactionTimeOut: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        transactionTimeOut();
                    }
                });
            }

            @Override
            public void Received(final byte[] data) {
                System.out.printf("utility:: %s received \n", TAG);
                Log.d(TAG, "RECEIVED DATA:" + bcd2Str(data));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        received(dealWithTheResponse(bcd2Str(data)));
                    }
                });


            }

            @Override
            public void Error(final String error) {
                Log.d(TAG, "Error: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error(error);
                    }
                });

            }

            @Override
            public void Other() {
                Log.d(TAG, "Other: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        other();
                    }
                });

            }
        };
    }

    private String[] dealWithTheResponse(String response) {
        String raw_data;

        Log.d(TAG, "Encrypted Response Data：" + response);

        response = response.substring(4);
        // Paul_20180522 Start

        raw_data = OnUsDecryptionMsg(response); // send to decrypt no need length

// Paul_20180522 End
//        raw_data = decryptMsg(response); // send to decrypt no need length


        //raw_data = raw_data.substring(4); // already cut length
        mBlockDataReceived = BlockCalculateUtil.getReceivedDataBlock(raw_data);

        Log.d(TAG, "Decrypted Response Data：" + raw_data);

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

        return mBlockDataReceived;
    }

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
                        PRIMARY_HOST = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_PRIMARY_IP);
                        PRIMARY_PORT = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_PRIMARY_PORT);
                        SECONDARY_HOST = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_SECONDARY_IP);
                        SECONDARY_PORT = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_SECONDARY_PORT);
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
        } catch (Exception e) {
            e.printStackTrace();
            //showMessage(e.toString());
            Log.d(TAG, e.toString());
        }
    }

    private void customDialog_FirstSettlement() {
        dialog_FirstSettlement = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180828 change UI
        View view = dialog_FirstSettlement.getLayoutInflater().inflate( R.layout.dialog_custom_first_settlement, null);//K.GAME 180828 change UI
        dialog_FirstSettlement.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change UI
        dialog_FirstSettlement.setContentView(view);//K.GAME 180828 change UI
        dialog_FirstSettlement.setCancelable(false);//K.GAME 180828 change UI

//        dialog_FirstSettlement = new Dialog(this);
//        dialog_FirstSettlement.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog_FirstSettlement.setCancelable(false);
//        dialog_FirstSettlement.setContentView(R.layout.dialog_custom_first_settlement);
//        dialog_FirstSettlement.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog_FirstSettlement.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        btn_firstSettlement_TMS = dialog_FirstSettlement.findViewById( R.id.btn_firstSettlement_TMS);
        Button btn_firstSettlement_GHC = dialog_FirstSettlement.findViewById( R.id.btn_firstSettlement_GHC);
        Button btn_close = dialog_FirstSettlement.findViewById( R.id.btn_close);
        btn_firstSettlement_TMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWaiting.show();
                cardManager.setDataFirstSettlement();
            }
        });
        btn_firstSettlement_GHC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWaiting.show();
                setDataFirstSettlementGHC();
//                Toast.makeText(SettingForUserActivity.this, "ยังไม่เปิดใช้บริการ", Toast.LENGTH_SHORT).show();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_FirstSettlement.dismiss();
            }
        });


    }

    private void dialogResponseError(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utility.customDialogAlert(SettingForUserActivity.this, response, new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setCancelable(false);
        dialogWaiting.setContentView( R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogWaiting.findViewById( R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogWaiting);
        //END K.GAME 180831 chang waitting UI
    }

    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        } else if (numberPrice.equalsIgnoreCase(null)) {
            numberPrice = "";
        }
    }

    private void clickCal(View v) {
        String keyPin = Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_ADMIN_PIN);
        //SINN 20181021 PIN any digits
//        String keyPin ="11111111";
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
                Log.d(TAG, "utility:: clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "utility:: clickCal u: " + numberPrice);
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
        dialogcustomDialogPin_new.setContentView( R.layout.dialog_custom_pin_admin);
        dialogcustomDialogPin_new.setCancelable(false);
        dialogcustomDialogPin_new.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogcustomDialogPin_new.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogcustomDialogPin_new.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        pinBox_new = dialogcustomDialogPin_new.findViewById( R.id.pinBox);
        final TextView inputTextLabel = dialogcustomDialogPin_new.findViewById( R.id.inputTextLabel);
        dialogcustomDialogPin_new.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//K.GAME 180905 ปิดคีร์บอร์ด
        pinBox_new.setShowSoftInputOnFocus(false);//K.GAME 180905 ทำให้กดคีร์บอร์ดที่ Edit text ไม่ได้
        final ImageView img_pin01 = dialogcustomDialogPin_new.findViewById( R.id.img_pin01);
        final ImageView img_pin02 = dialogcustomDialogPin_new.findViewById( R.id.img_pin02);
        final ImageView img_pin03 = dialogcustomDialogPin_new.findViewById( R.id.img_pin03);
        final ImageView img_pin04 = dialogcustomDialogPin_new.findViewById( R.id.img_pin04);
        final ImageView img_pin05 = dialogcustomDialogPin_new.findViewById( R.id.img_pin05);
        final ImageView img_pin06 = dialogcustomDialogPin_new.findViewById( R.id.img_pin06);

        final ImageView img_pin8_01 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_01);
        final ImageView img_pin8_02 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_02);
        final ImageView img_pin8_03 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_03);
        final ImageView img_pin8_04 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_04);
        final ImageView img_pin8_05 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_05);
        final ImageView img_pin8_06 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_06);
        final ImageView img_pin8_07 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_07);
        final ImageView img_pin8_08 = dialogcustomDialogPin_new.findViewById( R.id.img_pin8_08);

        LinearLayout linear_pin6 = dialogcustomDialogPin_new.findViewById( R.id.linear_pin6);
        LinearLayout linear_pin8 = dialogcustomDialogPin_new.findViewById( R.id.linear_pin8);
        LinearLayout linear_6 = dialogcustomDialogPin_new.findViewById( R.id.linear_6);
        LinearLayout linear_8 = dialogcustomDialogPin_new.findViewById( R.id.linear_8);

        EditText pinBox = dialogcustomDialogPin_new.findViewById( R.id.pinBox);

        String keyPin = Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_ADMIN_PIN);
        //SINN 20181021 PIN any digits
//        String keyPin ="11111111";
        numberPrice = "";               // Paul_20181030 PIN set null
        Log.d(TAG, "1919_keyPin = " + keyPin);
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
            Utility.customDialogAlert(SettingForUserActivity.this, "กำหนด PIN ได้แค่ 6 และ 8หลัก", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    dialogcustomDialogPin_new.dismiss();
                }
            });
        }

        pinBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter( // กำหนด length ตาม Json

                Integer.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_ADMIN_PIN).length()) //K.GAME 180925 get length จากใน json เพื่อเอามากำหนดขนาดช่อง password
//SINN 20181021 PIN any digits
//                Integer.valueOf("11111111")
        )});

        Button cancelBtn = dialogcustomDialogPin_new.findViewById( R.id.cancelBtn);
        Button okBtn = dialogcustomDialogPin_new.findViewById( R.id.okBtn);

        //K.GAME 181016 back button
        dialogcustomDialogPin_new.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                numberPrice = "";//K.GAME 181017 แก้บัค
                dialogcustomDialogPin_new.dismiss();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        //END K.GAME 181016 back button


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingForUserActivity.this, "PIN ไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//เปิดเพื่อดูRrcyclerView
            }
        });
//K.GAME ปุ่มกดบน Layout
        oneClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.oneClickFrameLayout);
        twoClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.twoClickFrameLayout);
        threeClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.threeClickFrameLayout);
        fourClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.fourClickFrameLayout);
        fiveClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.fiveClickFrameLayout);
        sixClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.sixClickFrameLayout);
        sevenClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.sevenClickFrameLayout);
        eightClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.eightClickFrameLayout);
        nineClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.nineClickFrameLayout);
        zeroClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.zeroClickFrameLayout);
        deleteClickFrameLayout = dialogcustomDialogPin_new.findViewById( R.id.deleteClickFrameLayout);
        numberLinearLayout = dialogcustomDialogPin_new.findViewById( R.id.numberLinearLayout_test);


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

//        System.out.printf("utility:: customDialogPin 0001 \n");
        pinBox_new.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                System.out.printf("utility:: customDialogPin 0002 \n");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                System.out.printf("utility:: customDialogPin 0003 \n");
                String keyPin = Preference.getInstance(SettingForUserActivity.this).getValueString(Preference.KEY_ADMIN_PIN);
                //SINN 20181021 PIN any digits
//                String keyPin ="11111111";
                Log.d(TAG, "1919_keyPin = " + keyPin);
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
//                    String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PIN);
//                    Log.d(TAG,"1919_keyPin = "+keyPin);
                        if (s.toString().equalsIgnoreCase(keyPin)) {
                            //K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
                            Intent intent3 = new Intent(SettingForUserActivity.this, SettingActivity.class);
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
                    } else

                    {
                        inputTextLabel.setVisibility(View.VISIBLE);
                        inputTextLabel.setText("Please enter system administrator\npassword");
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
//                    String keyPin = Preference.getInstance(MenuServiceListActivity.this).getValueString(Preference.KEY_ADMIN_PIN);
//                    Log.d(TAG,"1919_keyPin = "+keyPin);
                        if (s.toString().equalsIgnoreCase(keyPin)) {
                            //K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
                            Intent intent3 = new Intent(SettingForUserActivity.this, SettingActivity.class);
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
                    }
//                    } else
//
//                    {
//                        inputTextLabel.setVisibility(View.VISIBLE);
//                        inputTextLabel.setText("Please enter system administrator\npassword");
//                    }
                }else{

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
////                System.out.printf("utility:: customDialogPin 0004 \n");
//                //SINN 20181021 PIN any digits
//                String keyPin = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_ADMIN_PIN);
//                if (s.toString().equalsIgnoreCase(keyPin)) {
//                    //K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
//                    Intent intent3 = new Intent(SettingForUserActivity.this, SettingActivity.class);
//                    startActivity(intent3);
//                    overridePendingTransition(0, 0);
//                    dialogcustomDialogPin_new.dismiss();
//                    //END K.GAME 181017 ใส่เพื่อทำให้ใส่ครบ แล้วไปต่อ
//                    numberPrice = "";//K.GAME 180905 แก้บัค ตัวเลขโผล่มา Trace
//                }
//                //SINN 20181021 PIN any digits
            }
        });

        if (typeInterface != null)

        {  ////SINN 20181014 void rs233 bypass enter pin
            dialogWaiting.show();
        } else

        {
            dialogcustomDialogPin_new.show();
        }

    }//K.GAME 180905 new dialog

    private void customDialogPassword() {
        dialogPassword = new Dialog(SettingForUserActivity.this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogPassword.getLayoutInflater().inflate( R.layout.dialog_custom_input_password, null);//K.GAME 180828 change dialog UI
        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogPassword.setContentView(view);//K.GAME 180828 change dialog UI
        dialogPassword.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogPassword = new Dialog(MenuServiceListActivity.this);
//        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogPassword.setContentView(R.layout.dialog_custom_input_password);
//        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialogPassword.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passwordBox = dialogPassword.findViewById( R.id.passwordBox);
        okBtn = dialogPassword.findViewById( R.id.okBtn);
        cancelBtn = dialogPassword.findViewById( R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordBox.getText().toString().isEmpty()) {
                    passwordBox.setError("กรุณาใส่รหัสผ่าน");
                } else {
                    if (!isOffline) {
                        if (Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_ADMIN_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
                            Intent intent = new Intent(SettingForUserActivity.this, SettingActivity.class);
                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_ADMIN_PASSWORD);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else if (Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_ADMIN_PIN).equalsIgnoreCase(passwordBox.getText().toString())) {
                            Intent intent = new Intent(SettingForUserActivity.this, SettingActivity.class);
                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_NORMAL_PASSWORD);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            dialogPassword.dismiss();
                        } else {
                            passwordBox.setError("รหัสผิดพลาด");
                        }
                    } else {
                        if (Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_OFFLINE_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
// Paul_20180713
                            Intent intent = new Intent(SettingForUserActivity.this, MedicalTreatmentActivity.class);
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

    public void bindService() {
        Intent intentTle = new Intent();
        intentTle.setPackage("com.centerm.smartpostestforandroidstudio");
        // intentTle.setPackage("com.centerm.tle");
        intentTle.setAction("com.centerm.TleFunction.MANAGER_SERVICE");
        getApplication().bindService(intentTle, connTle, Context.BIND_AUTO_CREATE);//บัค
    }

    private void setDialog() {
        builder = new AlertDialog.Builder(SettingForUserActivity.this);
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

    public void initBtnExit() {
        System.out.printf("utility:: MenuServiceListActivity initBtnExit \n");
//        super.initBtnExit();
//        linearLayoutToolbarBottom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                builder.show();
//            }
//        });
    }

    private void getTag(int tagNumber, String[] data) {
        String tagAll = data[63 - 1];

        String tagId0 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen0 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData0 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen0)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen0)) * 2);
        tagNumber = tagNumber + 16;

        String tagId1 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen1 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData1 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen1)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen1)) * 2);
        tagNumber = tagNumber + 16;

        String tagId2 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen2 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData2 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen2)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen2)) * 2);
        tagNumber += 16;

        String tagId3 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen3 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData3 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen3)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen3)) * 2);
        tagNumber += 16;

        String tagId4 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen4 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData4 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen4)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen4)) * 2);
        tagNumber += 16;

        String tagId5 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen5 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData5 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen5)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen5)) * 2);
        tagNumber += 16;

        String tagId6 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen6 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData6 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen6)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen6)) * 2);
        tagNumber += 16;

        String tagId7 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen7 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData7 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen7)) * 2));
        tagNumber = tagNumber + (Integer.valueOf( BlockCalculateUtil.hexToString(tagLen7)) * 2);
        tagNumber += 16;

        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1000_HC, BlockCalculateUtil.hexToString(tagData0));
        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1001_HC, BlockCalculateUtil.hexToString(tagData1));
        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1002_HC, BlockCalculateUtil.hexToString(tagData2));
        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1003_HC, BlockCalculateUtil.hexToString(tagData3));
        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1004_HC, BlockCalculateUtil.hexToString(tagData4));
        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1005_HC, BlockCalculateUtil.hexToString(tagData5));
        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1006_HC, BlockCalculateUtil.hexToString(tagData6));
        Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TAG_1007_HC, BlockCalculateUtil.hexToString(tagData7));

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

    @Override
    public void onResume() {
        super.onResume();
System.out.printf("utility:: %s onResume \n",TAG);
        bindService();
        connect();
        if (cardManager != null) {
            System.out.printf("utility:: %s onResume cardManager != null \n",TAG);
            showMessageResCode();       // Paul_20181029 Add to showMessageResCode
            //callBackResponseCode();
        }
    }

    protected void connectTimeOut() {
        if (dialogWaiting != null) {
            dialogWaiting.dismiss();
        }
        System.out.printf("utility:: %s connectTimeOut \n",TAG);
//        dialogWaiting.dismiss();
        Utility.customDialogAlert(SettingForUserActivity.this, "ConnectTimeOut", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    protected void transactionTimeOut() {
        if (dialogWaiting != null) {
            dialogWaiting.dismiss();
        }
        System.out.printf("utility:: %s transactionTimeOut \n",TAG);
//        dialogWaiting.dismiss();
        Utility.customDialogAlert(SettingForUserActivity.this, "transactionTimeOut", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    private void setDataParameterDownloadGHC() {
        String terminalVersion = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_TERMINAL_VERSION);
        String messageVersion = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_MESSAGE_GHC_VERSION);
        String transactionCode = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_TRANSACTION_CODE);
        String parameterVersion = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_PARAMETER_VERSION);
        String messageLen = "00000074";
//        String terminalSN = "000025068";        // Paul_20180522
        /*String samId = "                ";      // Paul_20180522
        String samCsn = "                ";     // Paul_20180522*/
        String randomData = "00000000";         // Paul_20180522
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "00000000";           // Paul_20180522
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "900000";
        mBlockDataSend[24 - 1] = Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_NII_GHC);    // Paul_20180522
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString( CardPrefix.getTerminalId(SettingForUserActivity.this, "GHC"));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString( CardPrefix.getMerchantId(SettingForUserActivity.this, "GHC"));
        String s63 = messageLen + terminalVersion + messageVersion + transactionCode + parameterVersion + randomData + terminalCERT + checkSUM;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((s63).length())) + BlockCalculateUtil.getHexString(s63);

        packageAndSend( Preference.getInstance(SettingForUserActivity.this).getValueString( Preference.KEY_TPDU_GHC), "0800", mBlockDataSend);

    }

    private void setDataFS(String[] mBlockDataReceived) {
        if (mBlockDataReceived[63 - 1] != null) {
            Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[63 - 1]);
            String paraVersion = mBlockDataReceived[63 - 1].substring(28, 28 + 16);
            String batch = mBlockDataReceived[63 - 1].substring(44, 44 + 16);
            String transactionNo = mBlockDataReceived[63 - 1].substring(60, 60 + 16);
            Log.d(TAG, "dealWithTheResponse: " + paraVersion + "\n batch : " + batch + " \n transactionNo : " + transactionNo);
            Log.d(TAG, "dealWithTheResponse: BlockCalculateUtil.changeStringToHexString(paraVersion) : " + BlockCalculateUtil.hexToString(paraVersion));
            Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_PARAMETER_VERSION, BlockCalculateUtil.hexToString(paraVersion));
            Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_BATCH_NUMBER_GHC, BlockCalculateUtil.hexToString(batch).substring(2, BlockCalculateUtil.hexToString(batch).length()));
            Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_TRACE_NO_GHC, BlockCalculateUtil.hexToString(transactionNo));
            Preference.getInstance(SettingForUserActivity.this).setValueString( Preference.KEY_INVOICE_NUMBER_ALL, BlockCalculateUtil.hexToString(transactionNo).substring(2, 8));
        }
    }

    protected void received(final String[] data) {
        System.out.printf("utility:: %s received \n", TAG);
        dialogWaiting.dismiss();
        String de39 = BlockCalculateUtil.hexToString(data[39 - 1]);
        String de62 = BlockCalculateUtil.hexToString(data[62 - 1]);
        if (de39.equalsIgnoreCase("00") && mBlockDataSend[3 - 1].equalsIgnoreCase("920000")) {
            setDataFS(data);
            setDataParameterDownloadGHC();
        } else if (de39.equalsIgnoreCase("00")) {
            int tagNumber = 54;
            Log.d(TAG, "dealWithTheResponse: ParameterDownload");
            Log.d(TAG, "dealWithTheResponse: " + data[63 - 1]);

            getTag(tagNumber, data);
            Utility.customDialogAlertSuccess(SettingForUserActivity.this, null, new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
// Paul_20180718
//                    Toast.makeText(getContext(), "ADADA" + data[39 -1 ], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            Utility.customDialogAlert(SettingForUserActivity.this, "ERROR : " + de39, new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
// Paul_20180718
//                    Toast.makeText(getContext(), "Error" + data[39 -1 ], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
    }

    protected void error(String error) {
        dialogWaiting.dismiss();
        Utility.customDialogAlert(SettingForUserActivity.this, "error 002", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    protected void other() {
        dialogWaiting.dismiss();
        Utility.customDialogAlert(SettingForUserActivity.this, "other", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    // Paul_20181029 Add to showMessageResCode
    private void showMessageResCode() {
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(String response) {
                System.out.printf("utility:: MenuServiceListActivity showMessageResCode 000002 \n");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogAlert.show();
                        dismissDialogAll();
                    }
                });
            }

            @Override
            public void onResponseCodeandMSG(String response, String szCode) {
                System.out.printf("utility:: MenuServiceListActivity onResponseCodeandMSG 000002 \n");
                ////20180725  SINN VOID HOST reject EDC still waiting
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogAlert.show();
                        dismissDialogAll();
                    }
                });
            }

            @Override
            public void onResponseCodeSuccess() {
                if (getApplication() != null) {
                    dialogWaiting.dismiss();
                    if (!isFinishing()) {
                        Log.d("1919", "เข้านะ2");
                        dialogResponseSuccess();
                    }
                }
            }

            @Override
            public void onConnectTimeOut() {
                System.out.printf("utility:: %s onConnectTimeOut 00014 \n",TAG);
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                Utility.customDialogAlert(SettingForUserActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                            }
                        }
                    });
            }

            @Override
            public void onTransactionTimeOut() {
                System.out.printf("utility:: %s onTransactionTimeOut 00013 \n",TAG);
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
// Paul_20180717
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            Utility.customDialogAlert( SettingForUserActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    finish();
                                }
                            } );
                        }

                    }
                });
            }
        });
    }

    // Paul_20181029 Add to showMessageResCode
    public void customDialogAlert() {
        if (dialogAlert != null) {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        }
        dialogAlert = new Dialog(SettingForUserActivity.this, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

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
            }
        });

    }

    // Paul_20181029 Add to showMessageResCode
    private void dismissDialogAll() {

        if (dialogWaiting != null && dialogWaiting.isShowing())
            dialogWaiting.dismiss();
//        if (timer != null)
//            timer.cancel();
        if (dialogPassword != null && dialogPassword.isShowing())
            dialogPassword.dismiss();
        if (dialog_FirstSettlement != null && dialog_FirstSettlement.isShowing())
            dialog_FirstSettlement.dismiss();
        if (dialog_ParameterDownload != null && dialog_ParameterDownload.isShowing())
            dialog_ParameterDownload.dismiss();
        if (dialogAlertSuccess != null && dialogAlertSuccess.isShowing())
            dialogAlertSuccess.dismiss();
        if (dialogVersion != null && dialogVersion.isShowing())
            dialogVersion.dismiss();
        if (dialogHost != null && dialogHost.isShowing())
            dialogHost.dismiss();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
