package org.centerm.Tollway.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.EPedDesMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedType;
import com.pax.jemv.device.DeviceManager;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.menuvoid.VoidActivity;
import org.centerm.Tollway.core.ChangeFormat;
import org.centerm.Tollway.database.ReversalTemp;
//import org.centerm.Tollway.pax.ActionSearchCard;
import org.centerm.Tollway.pax.Constants;
import org.centerm.Tollway.pax.DeviceImplNeptune;
import org.centerm.Tollway.pax.EUIParamKeys;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import org.centerm.Tollway.pax.ActionSearchCard.SearchMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import io.realm.Realm;

public class MenuServiceActivity extends AppCompatActivity {

    private final String TAG = "MenuServiceActivity";
    private Realm realm = null;
    public static final String KEY_TYPE_PASSWORD = "key_type_password";
    public static String TYPE_NORMAL_PASSWORD = "type_normal_password";
    public static String TYPE_ADMIN_PASSWORD = "type_admin_password";

    private LinearLayout creditLinearLayout = null;
    private LinearLayout qrLinearLayout = null;
    private LinearLayout promptLinearLayout = null;
    private LinearLayout aliLinearLayout = null;
    private LinearLayout wechatLinearLayout = null;
    private LinearLayout voidLinearLayout = null;
    private LinearLayout settleLinearLayout = null;
    private LinearLayout settingLinearLayout = null;
    private LinearLayout functionlinearLayout = null;
    private LinearLayout reprintLinearLayout = null;
    private LinearLayout preauthLinearLayout = null;
    private LinearLayout reportLinearLayout = null;
    private LinearLayout testlinearLayout = null;
    private Dialog dialogPassword;
    private Dialog dialogWaiting;
    private Dialog dialogQr;
    private Dialog dialogFunction;

    private EditText passwordBox;
    private Button okBtn;
    private Button cancelBtn;

    private CardManager cardManager = null;

    private AlertDialog.Builder builder;



    // A920
    private byte mode;
    private boolean supportManual = false; // 是否支持手输

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_service);

        cardManager = MainApplication.getCardManager();
        if (Preference.getInstance(this).getValueString(Preference.KEY_PIN).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_PIN, "1111");
        }

        loadParam();

        DeviceManager.getInstance().setIDevice( DeviceImplNeptune.getInstance());

/*
        MainApplication.getDal().getPed(EPedType.EXTERNAL_TYPEA).setExMode(0x03);

        try {

            MainApplication.getDal().getPed(EPedType.EXTERNAL_TYPEA).writeKey(EPedKeyType.TLK, (byte) 0, EPedKeyType.TMK, Constants.INDEX_TMK, ChangeFormat.str2Bcd("0000000000000000"), ECheckMode.KCV_NONE, null);
            MainApplication.getDal().getPed(EPedType.EXTERNAL_TYPEA).writeKey(EPedKeyType.TLK, (byte) 0, EPedKeyType.TMK, Constants.INDEX_TMK, ChangeFormat.str2Bcd("0000000000000000"), ECheckMode.KCV_NONE, null);
            MainApplication.getDal().getPed(EPedType.EXTERNAL_TYPEA).writeKey(EPedKeyType.TLK, (byte) 0, EPedKeyType.TMK, Constants.INDEX_TMK, ChangeFormat.str2Bcd("0000000000000000"), ECheckMode.KCV_NONE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }*/




        String EPS_pinblock;
        String OffUs_pinblock;
        String OnUs_pinblock;
        String EPS_applicationkey;
        String OffUs_applicationkey;
        String OnUs_applicationkey;
        int len = 0;
        try {
            File file = new File("/sdcard/pinblock.txt");
            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] readBuffer = new byte[fileInputStream.available()];
            fileInputStream.read(readBuffer);

            String workingkey = new String(readBuffer);

            EPS_pinblock = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OffUs_pinblock = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OnUs_pinblock = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            EPS_applicationkey = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OffUs_applicationkey = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OnUs_applicationkey = workingkey.substring(len, len + (17 * 2));

            MainApplication.getDal().getPed(EPedType.INTERNAL).erase();


            MainApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TDK, (byte)0, EPedKeyType.TDK, Constants.INDEX_TDK,
                    ChangeFormat.hexStringToByte(EPS_applicationkey.substring(0,EPS_applicationkey.length() - 2)),ECheckMode.KCV_NONE, null);
            byte[] decryptedkey = MainApplication.getDal().getPed(EPedType.INTERNAL).calcDes(Constants.INDEX_TDK, ChangeFormat.hexStringToByte(EPS_pinblock.substring(0,EPS_pinblock.length() - 2)), EPedDesMode.DECRYPT);

            //MainApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TLK,(byte) 0, EPedKeyType.TMK, Constants.INDEX_TMK, ChangeFormat.hexStringToByte("0000000000000000"), ECheckMode.KCV_NONE, null);
            MainApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TPK,(byte)0, EPedKeyType.TPK, Constants.INDEX_TPK,
                    decryptedkey, ECheckMode.KCV_NONE, null);

        } catch ( Exception e) {
            e.printStackTrace();
        }


        //Device.writeTMK(MainApplication.getConvert().strToBcd("0000000000000000", IConvert.EPaddingPosition.PADDING_LEFT));
        //Device.writeTPK(MainApplication.getConvert().strToBcd("205E61C2299BF7CB405DC420CDB0D552", IConvert.EPaddingPosition.PADDING_LEFT), null);
        //Device.writeTIKFuc(MainApplication.getConvert().strToBcd("0000000000000000", IConvert.EPaddingPosition.PADDING_LEFT), MainApplication.getConvert().strToBcd("0000000000", IConvert.EPaddingPosition.PADDING_LEFT));



/*
        Device.writeTMK(MainApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT));
        Device.writeTPK("205E61C2299BF7CB405DC420CDB0D55200".getBytes(), null);
        Device.writeTIKFuc(MainApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT), MainApplication.getConvert().strToBcd("0000000001", IConvert.EPaddingPosition.PADDING_LEFT));
*/


        Log.i("writeKey", " load default KEY into PED");


        setInit();
        customDialogPassword();
        customDialogWaiting();
        setDialog();
        DeviceManager.getInstance().setIDevice( DeviceImplNeptune.getInstance());

        setDialogQr();
        setDialogFunction();

        callReversal();
    }

    private void setInit() {
        creditLinearLayout = findViewById(R.id.creditLinearLayout);
        qrLinearLayout = findViewById(R.id.qrLinearLayout);
        voidLinearLayout = findViewById(R.id.voidLinearLayout);
       // settleLinearLayout = findViewById(R.id.settleLinearLayout);
        //functionlinearLayout = findViewById(R.id.functionlinearLayout);
        settingLinearLayout = findViewById(R.id.settingLinearLayout);

        creditLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setFalseFallbackHappen(); //Init
                cardManager.setPreAuthFlag(false);
                if (checkReversal()) {
                    Intent intent = new Intent(MenuServiceActivity.this, CalculatePriceActivity.class);
                    intent.putExtra("amount",0);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        qrLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQr.show();
            }
        });

        voidLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkReversal()) {
                    Intent intent = new Intent(MenuServiceActivity.this, VoidActivity.class);
                    intent.putExtra("amount",0);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        settleLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.customDialogAlert(MenuServiceActivity.this, "Now developing...");

            }
        });

        settingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringJsonCAPK();
                dialogPassword.show();
                passwordBox.setText("");

            }
        });

        functionlinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFunction.show();
            }
        });
    }

    private boolean checkReversal() {
        ReversalTemp reversalTemp = null;
        realm = Realm.getDefaultInstance();
        reversalTemp = realm.where(ReversalTemp.class).findFirst();
        if (reversalTemp != null) {
            //dialogWaiting.show();
            //cardManager.setDataReversalAndSendHost(reversalTemp);
            return false;
        } else {
            return true;
        }
    }


    private void getStringJsonCAPK() {
        File file = new File("sdcard/print_param.json");
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
                JSONObject objParam = jsonObject.getJSONObject("param");

                String merchantNameLine1 = objParam.getString("merchantNameLine1");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_MERCHANT_1, merchantNameLine1);

                String merchantNameLine2 = objParam.getString("merchantNameLine2");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_MERCHANT_2, merchantNameLine2);

                String merchantNameLine3 = objParam.getString("merchantNameLine3");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_MERCHANT_3, merchantNameLine3);

//                String primaryIp = objParam.getString("primaryIp");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_PRIMARY_IP, "172.31.213.81");

//                String primaryPort = objParam.getString("primaryPort");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_PRIMARY_PORT, "3828");

//                String secondaryIp = objParam.getString("secondaryIp");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_SECONDARY_IP,"172.31.213.81");

//                String secondaryPort = objParam.getString("secondaryPort");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_SECONDARY_PORT, "3828");

                String tmsTerminaversion = objParam.getString("tmsTerminaversion");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_TERMINAL_VERSION, tmsTerminaversion);

                String tmsMsgVersion = objParam.getString("tmsMsgVersion");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_MESSAGE_VERSION, tmsMsgVersion);

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


    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this, R.style.ThemeWithCorners);
        View view = dialogWaiting.getLayoutInflater().inflate(R.layout.dialog_custom_load_process, null);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setContentView(view);
        dialogWaiting.setCancelable(false);
//        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
//        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
//        animationDrawable.start();
    }

    private void customDialogPassword() {
        dialogPassword = new Dialog(MenuServiceActivity.this);
        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPassword.setContentView(R.layout.dialog_custom_input_password);
        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogPassword.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passwordBox = dialogPassword.findViewById(R.id.passwordBox);
        okBtn = dialogPassword.findViewById(R.id.okBtn);
        cancelBtn = dialogPassword.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordBox.getText().toString().isEmpty()) {
                    passwordBox.setError("กรุณาใส่รหัสผ่าน(input pin)");
                } else {
                    if (Preference.getInstance(MenuServiceActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
                        Intent intent = new Intent(MenuServiceActivity.this, SettingActivity.class);
                        intent.putExtra(KEY_TYPE_PASSWORD, TYPE_ADMIN_PASSWORD);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        dialogPassword.dismiss();
                    } else if (Preference.getInstance(MenuServiceActivity.this).getValueString(Preference.KEY_ADMIN_PIN).equalsIgnoreCase(passwordBox.getText().toString())) {
                        Intent intent = new Intent(MenuServiceActivity.this, SettingActivity.class);
                        intent.putExtra(KEY_TYPE_PASSWORD, TYPE_NORMAL_PASSWORD);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        dialogPassword.dismiss();
                    } else {
                        passwordBox.setError("รหัสผิดพลาด");
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
    }

    private void onCheckHost() {
        if (cardManager != null)
            cardManager.setTestHostLister(new CardManager.TestHostLister() {
                @Override
                public void onResponseCodeSuccess() {
                    if (!isFinishing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dialogWaiting.isShowing())
                                    dialogWaiting.dismiss();
                                Utility.customDialogAlertSuccess(MenuServiceActivity.this, null, new Utility.OnClickCloseImage() {
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
                    if (!isFinishing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dialogWaiting.isShowing())
                                    dialogWaiting.dismiss();
                                Utility.customDialogAlert(MenuServiceActivity.this, "ม่สามารถเชื่อมต่อได้\n(connect time out)");
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
                                if(dialogWaiting.isShowing())
                                    dialogWaiting.dismiss();
                                Utility.customDialogAlert(MenuServiceActivity.this, "ม่สามารถเชื่อมต่อได้\n(trans time out)");
                            }
                        });
                    }
                }
            });
    }

    public void setDialogQr() {
        dialogQr = new Dialog(MenuServiceActivity.this, R.style.ThemeWithNormal);
        View view = dialogQr.getLayoutInflater().inflate(R.layout.dialog_total_qr, null);
        dialogQr.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogQr.setContentView(view);

        promptLinearLayout = dialogQr.findViewById(R.id.promptLinearLayout);
        aliLinearLayout = dialogQr.findViewById(R.id.aliLinearLayout);
        wechatLinearLayout = dialogQr.findViewById(R.id.wechatLinearLayout);

        promptLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.customDialogAlert(MenuServiceActivity.this, "Now developing...");
            }
        });

        aliLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.customDialogAlert(MenuServiceActivity.this, "Now developing...");
            }
        });

        wechatLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.customDialogAlert(MenuServiceActivity.this, "Now developing...");
            }
        });
    }

    public void setDialogFunction() {
        dialogFunction = new Dialog(MenuServiceActivity.this, R.style.ThemeWithNormal);
        View view = dialogFunction.getLayoutInflater().inflate(R.layout.dialog_total_function, null);
        dialogFunction.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFunction.setContentView(view);

        preauthLinearLayout = dialogFunction.findViewById(R.id.preauthLinearLayout);
        reprintLinearLayout = dialogFunction.findViewById(R.id.reprintLinearLayout);
        reportLinearLayout = dialogFunction.findViewById(R.id.reportLinearLayout);
        testlinearLayout = dialogFunction.findViewById(R.id.testlinearLayout);

        preauthLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setPreAuthFlag(true);
                if (checkReversal()) {
                    Intent intent = new Intent(MenuServiceActivity.this, CalculatePriceActivity.class);
                    intent.putExtra("amount",0);
                    //intent.putExtra(EUIParamKeys.CARD_SEARCH_MODE.toString(),)

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        reprintLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.customDialogAlert(MenuServiceActivity.this, "Now developing...");
            }
        });

        reportLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(realm == null) {
                    realm = Realm.getDefaultInstance();
                }
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                Utility.customDialogAlert(MenuServiceActivity.this, "Now developing...");
            }
        });

        testlinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFunction.dismiss();
                if(!dialogWaiting.isShowing())
                    dialogWaiting.show();
                cardManager.setDataTestHostPurchase();
            }
        });
    }

    private void callReversal() {
        cardManager.setReversalListener(new CardManager.ReversalListener() {
            @Override
            public void onReversalSuccess() {
                Log.d(TAG, "onReversalSuccess: ");
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }

                if (cardManager != null) {
                    cardManager.removeTransResultAbort();
                    cardManager.removeResponseCodeListener();
                    cardManager.removeCardHelperListener();
                    cardManager.removeCardNoConnectHost();
                }

//                cardManager.stopTransaction();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customDialogAlertReversalSuccess();
                    }
                });
            }
        });
    }

    public void customDialogAlertReversalSuccess() {
        final Dialog dialogAlert = new Dialog(MenuServiceActivity.this, R.style.ThemeWithCorners);
        View view = dialogAlert.getLayoutInflater().inflate( R.layout.dialog_custom_success, null);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(view);
        dialogAlert.setCancelable(false);
        TextView msgLabel = dialogAlert.findViewById( R.id.msgLabel);

        msgLabel.setText("Reversal สำเร็จ");

        try {
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

        CountDownTimer timer = new CountDownTimer(1000, 1000) {
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

    private void setDialog() {
        builder = new AlertDialog.Builder(MenuServiceActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        cardManager = MainApplication.getCardManager();
        onCheckHost();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cardManager.removeTestHostLister();
    }

    @Override
    public void onBackPressed() {
        builder.show();
    }

    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        // 寻卡方式，默认挥卡
        try {
            Log.d("euimode", EUIParamKeys.CARD_SEARCH_MODE.toString());
            System.out.println("searchmode");
            System.out.println((byte) (SearchMode.INSERT_TAP | SearchMode.SWIPE));
            mode = bundle.getByte(EUIParamKeys.CARD_SEARCH_MODE.toString(), (byte) (SearchMode.INSERT_TAP | SearchMode.SWIPE));
            System.out.println("mode");
            System.out.println(mode);
            if ((mode & SearchMode.KEYIN) == SearchMode.KEYIN) { // 是否支持手输卡号
                supportManual = true;
            } else {
                supportManual = false;
            }
            System.out.printf("utility:: %s loadParam mode = [%02X]\n",TAG,mode);

/*
            SwingCardActivity.readerType = toReaderType(mode);
            SwingCardActivity.setReadType(SwingCardActivity.readerType);
*/

        } catch (Exception e) {
            System.out.printf("utility:: %s loadParam Exception\n",TAG);
            Log.e("loadParam", e.getMessage());
        }
    }
}
