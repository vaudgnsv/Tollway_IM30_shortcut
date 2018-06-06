package org.centerm.land.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.activity.qr.MenuQrActivity;
import org.centerm.land.fragment.MenuServiceFragment;
import org.centerm.land.R;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class MenuServiceActivity extends AppCompatActivity {

    private final String TAG = "MenuServiceFragment";

    public static final String KEY_TYPE_PASSWORD = "key_type_password";
    public static String TYPE_NORMAL_PASSWORD = "type_normal_password";
    public static String TYPE_ADMIN_PASSWORD = "type_admin_password";

    private LinearLayout creditLinearLayout = null;
    private LinearLayout qrLinearLayout = null;
    private LinearLayout settingLinearLayout = null;
    private LinearLayout linearLayoutTestHost = null;
    private Dialog dialogPassword;
    private EditText passwordBox;
    private Button okBtn;
    private Button cancelBtn;
    private Dialog dialogHost;
    private Button posBtn;
    private Button epsBtn;
    private Button tmsBtn;
    private ImageView closeImage;

    private CardManager cardManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_service);

//        viewPagerMenu = findViewById(R.id.viewPagerMenu);
//        pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());
//        viewPagerMenu.setAdapter(pagerAdapter);
        if (Preference.getInstance(this).getValueString(Preference.KEY_PIN).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_PIN, "1111");
        }

        creditLinearLayout = findViewById(R.id.creditLinearLayout);
        qrLinearLayout = findViewById(R.id.qrLinearLayout);
        settingLinearLayout = findViewById(R.id.settingLinearLayout);
        creditLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuServiceActivity.this, MenuServiceListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        qrLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuServiceActivity.this, MenuQrActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
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
        linearLayoutTestHost = findViewById(R.id.linearLayoutTestHost);
        linearLayoutTestHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.show();
            }
        });
        customDialogPassword();
        customDialogHost();
    }

    private void getStringJsonCAPK() {
        File file = new File("sdcard/print_param_.json");
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

                String id = jsonObject.getString("id");
                String termSeq = jsonObject.getString("termSeq");    //"termSeq":"D1V0250000022",

                String merchantNameLine1 = jsonObject.getString("merchantNameLine1");
                Preference.getInstance(MenuServiceActivity.this).setValueString(Preference.KEY_MERCHANT_1, merchantNameLine1);

                String merchantNameLine2 = jsonObject.getString("merchantNameLine2");
                String merchantNameLine3 = jsonObject.getString("merchantNameLine3");
                String TAX_ID = jsonObject.getString("TAX_ID");
                String POS_ID = jsonObject.getString("POS_ID");
                Double fee = jsonObject.getDouble("fee");

                String qrAid = jsonObject.getString("qrAid");
                String qrRef2 = jsonObject.getString("qrRef2");
                String qrBillerId = jsonObject.getString("qrBillerId");
                String qrMerchantName = jsonObject.getString("qrMerchantName");
                String qrTerminalId = jsonObject.getString("qrTerminalId");
                String qrMerchantId = jsonObject.getString("qrMerchantId");
                String qrBillerkey = jsonObject.getString("qrBillerkey");

                String primaryIp = jsonObject.getString("primaryIp");
                String primaryPort = jsonObject.getString("primaryPort");
                String secondaryIp = jsonObject.getString("secondaryIp");
                String secondaryPort = jsonObject.getString("secondaryPort");
                String qrPort = jsonObject.getString("qrPort");

                String posTerminalId = jsonObject.getString("posTerminalId");
                String posMerchantId = jsonObject.getString("posMerchantId");
                String posTpdu = jsonObject.getString("posTpdu");
                String posNii = jsonObject.getString("posNii");

                String epsTerminalId = jsonObject.getString("epsTerminalId");
                String epsMerchantId = jsonObject.getString("epsMerchantId");
                String epsTpdu = jsonObject.getString("epsTpdu");
                String epsNii = jsonObject.getString("epsNii");

                String tmsTerminalId = jsonObject.getString("tmsTerminalId");
                String tmsMerchantId = jsonObject.getString("tmsMerchantId");
                String tmsTpdu = jsonObject.getString("tmsTpdu");
                String tmsNii = jsonObject.getString("tmsNii");
                String tmsTerminaversion = jsonObject.getString("tmsTerminaversion");
                String tmsMsgVersion = jsonObject.getString("tmsMsgVersion");

                Log.d(TAG, "getStringJsonCAPK: " + id + " szPara : " + termSeq);

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
                    passwordBox.setError("กรุณาใส่รหัสผ่าน");
                } else {
                    if (Preference.getInstance(MenuServiceActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
                        Intent intent = new Intent(MenuServiceActivity.this, SettingActivity.class);
                        intent.putExtra(KEY_TYPE_PASSWORD, TYPE_ADMIN_PASSWORD);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        dialogPassword.dismiss();
                    } else if (Preference.getInstance(MenuServiceActivity.this).getValueString(Preference.KEY_NORMAL_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
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

    private void customDialogHost() {
        dialogHost = new Dialog(MenuServiceActivity.this);
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHost.setCancelable(false);
        dialogHost.setContentView(R.layout.dialog_custom_host);
        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
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
                                Utility.customDialogAlert(MenuServiceActivity.this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
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
                                Utility.customDialogAlert(MenuServiceActivity.this, "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
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
}
