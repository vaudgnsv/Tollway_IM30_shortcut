package org.centerm.Tollway.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.CustomProgressDialog;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();

    private CardManager cardManager = null;
    private Dialog dialogWaiting = null;
    private Dialog dialogSetting = null;
    private Dialog dialogUpdate = null;

    private Button btn_later;
    private Button btn_now;
    private Button btn_tle;
    private Button btn_settlement_first;
    private Button closeImage;
    private int cnt_firstsettlement = 0;

    private boolean flag_settlement_first = false;
    private TextView msgLabel;
    private TextView tv_user;
    private TextView txt_verApp;
    private TextView Title_btn1;
    private EditText edit_staffID;
    private Button btn_next;

    private Date dateTime;
    private DateFormat dateFormat;
    private DateFormat dateFormat2;
    private CountDownTimer timer = null;
    private Timer update_timer;
    private int timer_update;
    private LoginoutListener loginoutListener = null;

    //Header_req
    private String reqID;
    private String reqChannel = "POS-BMTA";
    private String reqDtm;
    private String reqBy = "POS-BMTA";
    private String service = "InquiryStaff";

    //InquiryStaff resp
    private String statusCd = null;
    private String citizenID = null;
    private String staffID = null;
    private String name = null;
    private String surname = null;
    private String roleID;
//    private int step;

    private String conductorID = null;

    private static SoundPool soundPool;
    private static int[] sound;

    //Update

    private CustomProgressDialog customProgressDialog = null;
    private Handler handler = null;
    private int progress_cnt = 0;
    private int progress_max = 100;
    private String step_update = "";


    private View NormalView;
    private RecyclerView recyclerViewCardReportSummary;
    private NestedScrollView slipNestedScrollView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.printf("utility:: %s onCreate 000001 \n", TAG);
        setContentView(R.layout.activity_login);
        cardManager = MainApplication.getCardManager();

        CardPrefix.getStringJson(LoginActivity.this);

        InitWidget();
        soundsetting();
        customDialogWaiting();
//        customDialogSettlng();
        setCheckUpdate();
//        responseCodeDialog();

        updatefunction_bmta();
        cardManager.checkUpdate();

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

    private void updatefunction_bmta() {
        customProgressDialog = new CustomProgressDialog(LoginActivity.this);
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
                    Toast.makeText(LoginActivity.this, "Please contact to THAIVAN", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, "ERROR :: Upate Json ", Toast.LENGTH_SHORT).show();

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
                                        CardPrefix.getStringJson(LoginActivity.this);
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


    private void InitWidget() {
        //Toolbar
        TextView app_title = findViewById(R.id.app_title);
//        TextView Title_btn2 = findViewById(R.id.toolbar_btn2);
        Title_btn1 = findViewById(R.id.toolbar_btn1);

        tv_user = findViewById(R.id.tv_user);
        txt_verApp = findViewById(R.id.txt_verApp);
        tv_user.setText("พนักงานขายบัตร");

//        app_title.setText("สิทธิรถโดยสาร");

        btn_next = findViewById(R.id.btn_next);
        edit_staffID = findViewById(R.id.edit_staff);

        Title_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogUpdate();
            }
        });
//        Title_btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogSetting.show();
//            }
//        });
//        txt_verApp.setText("Application version : " + getString(R.string.app_version) + "." + Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_BUS_VERJSON_ID) + "");

        txt_verApp.setText("Application version : " + getString(R.string.app_version)+"."+Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_JSON_CONTROL_VERSION) + "");

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conductorID = edit_staffID.getText().toString();

                if (!conductorID.equals("")) {

                    if (conductorID.length()==5){
                        citizenID = edit_staffID.getText().toString();
                        staffID = edit_staffID.getText().toString();
                        surname = edit_staffID.getText().toString();

                        boolean digitsOnly = TextUtils.isDigitsOnly(edit_staffID.getText());

                        if (digitsOnly){
                            Preference.getInstance(LoginActivity.this).setValueString(Preference.KEY_BUS_C_CITIZEN_ID, citizenID);
                            Preference.getInstance(LoginActivity.this).setValueString(Preference.KEY_BUS_C_STAFF_ID, staffID);
                            Preference.getInstance(LoginActivity.this).setValueString(Preference.KEY_BUS_C_NAME, name + " " + surname);

                            Preference.getInstance(LoginActivity.this).setValueString(Preference.KEY_BUS_LOGIN , "ON");

                            Intent ok_intent = new Intent(LoginActivity.this, MenuServiceListActivity.class);
                            ok_intent.putExtra("InsertCard", true);
                            startActivity(ok_intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }else {
                            Utility.customDialogAlert(LoginActivity.this, "กรุณาระบุเฉพาะตัวเลขเท่านั้น", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }


                    }else {
                        Utility.customDialogAlert(LoginActivity.this, "กรุณาระบุพนักงาน 5 หลัก", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }




                } else {
                    Utility.customDialogAlert(LoginActivity.this, "กรุณากรอกรหัสพนักงาน", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void soundsetting() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = new int[2];
        sound[0] = soundPool.load(this, R.raw.success, 1);
        sound[1] = soundPool.load(this, R.raw.fail, 1);
    }

    public static void playSound(int i) {
        soundPool.play(sound[i], 1, 1, 0, 0, 1);
    }

//    private void setTimer(final long time) {
//
//        timer = new CountDownTimer(time, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Log.d("Login Timer ::: ", String.valueOf(millisUntilFinished));
//                if (statusCd != null) {
//                    timer.cancel();
//                    timer = null;
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                timer.cancel();
//                timer = null;
//                dialogWaiting.dismiss();
//                if (statusCd == null)
//                    loginoutListener.onTimeout();
//            }
//        };
//        timer.start();
//    }


//    public boolean isConnectedToServer(String url, int timeout) {
//        try {
//            URL myUrl = new URL(url);
//            URLConnection connection = myUrl.openConnection();
//            connection.setConnectTimeout(timeout);
//            connection.connect();
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

//    private void sendMessage() {
//
//        try {
//            String data = null;
//            URL url = null;
//            HttpURLConnection urlConnection = null;
//            String ip = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_PRIMARY_IP);
////            String port1 = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_BUS_PORT);
////            String port2 = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_BUS_PORT2);
//
//            String https_flag = Preference.getInstance(Contextor.getInstance().getContext()).getValueString(Preference.KEY_BUS_HTTPS_ID);
//
//            String BASE_URL = "";
//
//
////            if (https_flag.equals("0")) {
////                BASE_URL = "http://" + ip + ":" + port1 + "";
//////            } else {
//////                BASE_URL = "https://" + ip + ":" + port2 + "";
//////            }
////            int timeout = Integer.parseInt(Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_BUS_DUPLICATE_DELAY));
////            if (isConnectedToServer(BASE_URL, timeout)) {
//                ip = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_PRIMARY_IP);
////                port = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_PRIMARY_PORT);
////            } else {
////                ip = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_SECONDARY_IP);
//////                port = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_PRIMARY_PORT);
////            }
//
////            if (https_flag.equals("0")) {
////                BASE_URL = "http://" + ip + ":" + port1 + "";
////            } else {
////                BASE_URL = "https://" + ip + ":" + port2 + "";
////            }
//
//            BASE_URL = BASE_URL+"/BMTADG/v1/inquiryStaff/";
////            if (https_flag.equals("0")) {
////                BASE_URL = "http://" + ip + ":" + port + "/BMTADG/v1/inquiryStaff/";
////            } else {
////                BASE_URL = "https://" + ip + ":" + port + "/BMTADG/v1/inquiryStaff/";
////            }
//
//            url = new URL(BASE_URL);
//
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            urlConnection.setDefaultUseCaches(false);
//            urlConnection.setDoInput(true);
//            urlConnection.setDoOutput(true);
//            urlConnection.setRequestMethod("POST");
//
//            urlConnection.setRequestProperty("content-type", "application/json");
//
//            JSONObject jsonObject1 = new JSONObject();
//            JSONObject jsonObject2 = new JSONObject();
//
//            jsonObject1.put("reqID", reqID);
//            jsonObject1.put("reqChannel", reqChannel);
//            jsonObject1.put("reqDtm", reqDtm);
//            jsonObject1.put("reqBy", reqBy);
//            jsonObject1.put("service", service);
//            jsonObject2.put("inputID", conductorID); //021038
////            jsonObject2.put("roleID", roleID);
//            jsonObject2.put("roleID", "D");
//
//            data = "{" +
//                    "headerReq:" + jsonObject1.toString() + "," +
//                    "inquiryStaffReq:" + jsonObject2.toString() + "}";
//
//            if (!data.isEmpty()) { // 웹 서버로 보낼 매개변수가 있는 경우
//                OutputStream os = null; // 서버로 보내기 위한 출력 스트림
//                os = urlConnection.getOutputStream();
//
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
//                bw.write(data); // 매개변수 전송
//                bw.flush();
//                bw.close();
//                os.close();
//            }
//
//            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                getResponseData(in);
//            } else
//                loginoutListener.onFail("การสื่อสารขัดข้อง"); //Netwrok Error
//
//            urlConnection.disconnect();
//        } catch (MalformedURLException e) {
//            loginoutListener.onFail("การสื่อสารขัดข้อง");//Netwrok Error
//            e.printStackTrace();
//        } catch (IOException e) {
//            loginoutListener.onFail("การสื่อสารขัดข้อง");//Netwrok Error
//            e.printStackTrace();
//        } catch (JSONException e) {
//            loginoutListener.onFail("การสื่อสารขัดข้อง");//Netwrok Error
//            e.printStackTrace();
//        }
//    }

//    private void getResponseData(InputStream in) {
//        final String data = readData(in);
//        JSONObject obj;
//        JSONObject obj_header;
//        JSONObject obj_body;
//        try {
//
//            obj = new JSONObject(data);
//            String headerResp = obj.getString("headerResp");
//            obj_header = new JSONObject(headerResp);
//            statusCd = obj_header.getString("statusCd");
//
//            if (statusCd.equals("00000")) {
//                String inquiryStaffResp = obj.getString("inquiryStaffResp");
//                obj_body = new JSONObject(inquiryStaffResp);
//                citizenID = obj_body.getString("citizenID");
//                staffID = obj_body.getString("staffID");
//                roleID = obj_body.getString("roleID");
//                name = obj_body.getString("name");
//                surname = obj_body.getString("surname");
//                loginoutListener.onSuccess();
//
//            } else {
//                String statusDesc = obj_header.getString("statusDesc");
//                loginoutListener.onFail(statusDesc);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String readData(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }

//    private String makeUniqueData(Date data) {
//        dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
//
//        String temp = dateFormat2.format(data);  // yyyyMMddhhmmss
//        String tid = Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_TERMINAL_ID_BMTA);
//
//        return tid + temp;
//
//    }

    public interface LoginoutListener {
        public void onSuccess();

        public void onFail(String msg);

        public void onTimeout();
    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setCancelable(false);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        msgLabel = dialogWaiting.findViewById(R.id.msgLabel);
        animationDrawable.start();

        msgLabel.setText("กรุณารอสักครู่...");
        Utility.animation_Waiting_new(dialogWaiting);
    }

//    private void customDialogSettlng() {
//        dialogSetting = new Dialog(this, R.style.ThemeWithCorners);
//        View view = dialogSetting.getLayoutInflater().inflate(R.layout.dialog_custom_setting_bus, null);
//        dialogSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogSetting.setContentView(view);
//        dialogSetting.setCancelable(false);
//        btn_tle = dialogSetting.findViewById(R.id.btn_tle);
//        btn_settlement_first = dialogSetting.findViewById(R.id.btn_settlement_first);
//        closeImage = dialogSetting.findViewById(R.id.closeImage);
//
//        btn_tle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (dialogSetting.isShowing())
//                    dialogSetting.dismiss();
//
//                Intent intent;
//                intent = new Intent(LoginActivity.this, PasswordActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btn_settlement_first.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cnt_firstsettlement = 0; // Paul_20190418
//                flag_settlement_first = true;
//                cardManager.setBUSstatus_true();
//                dialogWaiting.show();
//                cardManager.setDataFirstSettlement();
//            }
//        });
//
//        closeImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogSetting.dismiss();
//            }
//        });
//    }

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
                cardManager.updateFile();
            }
        });
        dialogUpdate.show();
    }

    public void customDialogAlertSuccess() {
        final Dialog dialogAlert = new Dialog(LoginActivity.this, R.style.ThemeWithCorners);
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

                Log.d("TESTTTT", "step_update: "+step_update);
                dialogAlert.dismiss();
//                if (Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length() == 8 &&
//                        Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS).length() == 8)
//                    cardManager.AutoTLE("323"); // ONUS RKI TLE OFFUS RKI TLE WORKINGKEY
//                else if (Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS).length() == 8)
//                    cardManager.AutoTLE("112"); // ONUS RKI TLE
//                else
                if (step_update.equalsIgnoreCase("FINISH")){
                    cardManager.AutoTLE("213");
                    step_update = "";
                }
                {
                    // OFFUS RKI TLE WORKINGKEY
//                    customDialogDownloadKey();
                    cardManager.updateFile();
                }
            }
        };
        timer.start();
    }

//    public void customDialogAlertFail() {
//        final Dialog dialogAlert = new Dialog(LoginActivity.this, R.style.ThemeWithCorners);
//        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_fail_bmta, null);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(view);
//        dialogAlert.setCancelable(false);
//        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
//        msgLabel.setText("อัปเดตข้อมูลล้มเหลว");
//
//        try {
//            dialogAlert.show();
//        } catch (Exception e) {
//            dialogAlert.dismiss();
//        }
//
//        CountDownTimer timer = new CountDownTimer(2500, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//                dialogAlert.dismiss();
//            }
//        };
//        timer.start();
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        txt_verApp.setText("Application version : " + getString(R.string.app_version) + "." + Preference.getInstance(LoginActivity.this).getValueString(Preference.KEY_BUS_VERJSON_ID) + "");
//        cardManager.checkUpdate();
    }

    @Override
    public void onBackPressed() {
//        if (step == 1) {
//            roleID = "D";
//            InitWidget();
//        }
    }

    public void customDialogAlertFail() {
        final Dialog dialogAlert = new Dialog(LoginActivity.this, R.style.ThemeWithCorners);
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





}
