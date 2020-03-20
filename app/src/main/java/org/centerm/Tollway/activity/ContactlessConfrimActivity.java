package org.centerm.Tollway.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;

public class ContactlessConfrimActivity extends AppCompatActivity {

    private final String TAG = "ContactlessConfrim";
    public static final String KEY_CALCUATE_ID = CalculatePriceActivity.class.getName() + "key_calcuate_id";
    public static final String KEY_TYPE_SALE_OR_VOID = CalculatePriceActivity.class.getName() + "key_type_sale_or_void";
    public static final String KEY_INTERFACE_CARDHOLDER_2 = CalculatePriceActivity.class.getName() + "_key_interface_cardholder_2";

    private CardManager cardManager;
    private Dialog dialogWaiting = null;
    private String card;
    private String Card_holder;
    private String amountInterface;

    private String app, tc, aid;

    private String send_code = null;

    private static SoundPool soundPool;
    private static int[] sound;

    private Dialog dialogParaEndble;

    Button btnSaleConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactless_confrim);


        setInit();
        customDialogWaiting();
        setListener();
        soundsetting();
        customDialogParameterEnable();

        btnSaleConfirm.performClick();

    }

    private void setListener() {
        cardManager = MainApplication.getCardManager();
        cardManager.setInsertOrUpdateDatabase(new CardManager.InsertOrUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {
                Log.d("contactless_check: ", "onUpdateVoidSuccess");
            }

            @Override
            public void onInsertSuccess(int id) {
//                playSound(0);

                Log.d("contactless_check: ", "onInsertSuccess");
                cardManager.removeTransResultAbort();
                Intent intent = new Intent(ContactlessConfrimActivity.this, SlipTemplateActivity.class);
                intent.putExtra(KEY_INTERFACE_CARDHOLDER_2, Card_holder);
                intent.putExtra(KEY_CALCUATE_ID, id);
                intent.putExtra(KEY_TYPE_SALE_OR_VOID, "SALE");
                intent.putExtra(MenuServiceListActivity.KEY_TYPE_INTERFACE, "Interface");
                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_APP, app);
                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_TC, tc);
                intent.putExtra(MenuServiceListActivity.KEY_INTERFACE_AID, aid);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);

//                if (dialogWaiting.isShowing()){
//                    dialogWaiting.dismiss();
//                }

            }
        });
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(String response) {
                Log.d("contactless_check: ", "onResponseCode : " + response);
                cardManager.removeTransResultAbort();
                if (dialogWaiting.isShowing())
                    dialogWaiting.dismiss();
            }

            @Override
            public void onResponseCodeandMSG(final String response, final String szCode) {
                Log.d(TAG, "onResponseCodeandMSG" + send_code);
                Log.d("contactless_check: ", "onResponseCodeandMSG : " + response + "\n" + szCode);
                cardManager.removeTransResultAbort();
                if (dialogWaiting.isShowing())
                    dialogWaiting.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.customDialogAlert_gotomain(ContactlessConfrimActivity.this, response, new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponseCodeSuccess() {
                Log.d("contactless_check: ", "onResponseCodeSuccess");
                Log.d(TAG, "onResponseCodeSuccess" + send_code);
                cardManager.removeTransResultAbort();
//                if(dialogWaiting.isShowing())
//                    dialogWaiting.dismiss();
                dialogWaiting.show();
            }

            @Override
            public void onConnectTimeOut() {
                Log.d("contactless_check: ", "onConnectTimeOut");
                cardManager.removeTransResultAbort();
                if (dialogWaiting.isShowing())
                    dialogWaiting.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.customDialogAlert(ContactlessConfrimActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }

            @Override
            public void onTransactionTimeOut() {
                Log.d("contactless_check: ", "onTransactionTimeOut");
                cardManager.removeTransResultAbort();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.customDialogAlert(ContactlessConfrimActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void setInit() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            card = bundle.getString(MenuServiceListActivity.KEY_CARD);
            Card_holder = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_CARDHOLDER);
            amountInterface = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_AMOUNT);

            app = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_APP);
            tc = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_TC);
            aid = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_AID);

            TextView tv_confirm_numberPrice = findViewById(R.id.tv_confirm_numberPrice);
            TextView tv_confirm_idcard = findViewById(R.id.tv_confirm_idcard);
            TextView tv_confirm_username = findViewById(R.id.tv_confirm_username);
            TextView tv_confirm_card_expired = findViewById(R.id.tv_confirm_card_expired);
            btnSaleConfirm = findViewById(R.id.btnSaleConfirm);

            if (Card_holder != null) {
                if (Card_holder.replace(" ", "").equalsIgnoreCase("/")) {
                    Card_holder = "";
                }
            }

            tv_confirm_numberPrice.setText(amountInterface);
            tv_confirm_username.setText(Card_holder);
            tv_confirm_card_expired.setText("XXXX");
            String tmp_cardno = card;
            tmp_cardno = tmp_cardno.substring(0, 4) + " " + tmp_cardno.substring(4, 6) + "XX XXXX " + tmp_cardno.substring(12, card.length());
            tv_confirm_idcard.setText(tmp_cardno);




            btnSaleConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String valueParameterEnable = Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1000);
//                    if (valueParameterEnable.substring(0, 4).equalsIgnoreCase("2222") || valueParameterEnable.substring(0, 4).equalsIgnoreCase("1111")) { // NAMTAN_20190712
                    if (!(valueParameterEnable.substring(0, 4).contains("3")) && !(valueParameterEnable.substring(0, 4).contains("4"))) {
                        dialogWaiting.show();
                        cardManager.sendMessege();
                    } else {
                        dialogParaEndble.show();
                    }
                }
            });

        }
    }

    private void customDialogParameterEnable() {
        dialogParaEndble = new Dialog(this);
        dialogParaEndble.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogParaEndble.setCancelable(false);
//        dialogParaEndble.setContentView(R.layout.dialog_custom_para_enable);//K.GAME 180914 change XML
        dialogParaEndble.setContentView(R.layout.dialog_custom_insert_ref);//K.GAME 180914 change XML
        dialogParaEndble.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogParaEndble.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogParaEndble.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout comCodeLinearLayout = dialogParaEndble.findViewById(R.id.comCodeLinearLayout);
        final EditText comCodeBox = dialogParaEndble.findViewById(R.id.comCodeBox);
        final EditText ref1Box = dialogParaEndble.findViewById(R.id.ref1Box);
        final EditText ref2Box = dialogParaEndble.findViewById(R.id.ref2Box);
        final EditText ref3Box = dialogParaEndble.findViewById(R.id.ref3Box);
        LinearLayout ref1LinearLayout = dialogParaEndble.findViewById(R.id.ref1LinearLayout);
        LinearLayout ref2LinearLayout = dialogParaEndble.findViewById(R.id.ref2LinearLayout);
        LinearLayout ref3LinearLayout = dialogParaEndble.findViewById(R.id.ref3LinearLayout);
        Button okBtn_dialogParaEndble = dialogParaEndble.findViewById(R.id.okBtn);  //SINN 20181114  support bypass parameter
        Button cancelBtn = dialogParaEndble.findViewById(R.id.cancelBtn);


        //20181213  SINN  KTB CR request set default ref text.
        ref1Box.setHint(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_Ref1text_ID));
        ref2Box.setHint(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_Ref2text_ID));
        ref3Box.setHint(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_Ref3text_ID));

        String valueParameterEnable = Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1000);  //para enable

//20181214 SINN  KTB order parameter countdown 1sec when override parameter.
        if (!(valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) &&  //comm
                !(valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) &&  //ref1
                !(valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) &&    //ref2
                !(valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")))
            dialogParaEndble.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        if (!valueParameterEnable.isEmpty()) {
//ref3Box
            if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
                ref3LinearLayout.setVisibility(View.VISIBLE);
                ref3Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1004));
                ref3Box.setEnabled(false);
            } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
                ref3LinearLayout.setVisibility(View.VISIBLE);
                ref3Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1004));
                //sinn 20180709
//                if(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_RS232_FLAG).equals("1"))
//                if (typeInterface != null) {
//                    ref3Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_REF3));
//                    ref3Box.setEnabled(false);
//                }
                //end 20180709
                ref3Box.setEnabled(true);
                ref3Box.requestFocus();
            } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
                ref3LinearLayout.setVisibility(View.GONE);
                ref3Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1004));
                ref3Box.setEnabled(false);
            }
//ref2Box
            if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
                ref2LinearLayout.setVisibility(View.VISIBLE);
                ref2Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1003));
                ref2Box.setEnabled(false);
            } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
                ref2LinearLayout.setVisibility(View.VISIBLE);
                ref2Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1003));
                //sinn 20180709
                //  if(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_RS232_FLAG).equals("1"))
//                if (typeInterface != null) {
//                    ref2Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_REF2));
//                    ref2Box.setEnabled(false);
//                }
                //end 20180709
                ref2Box.setEnabled(true);
                ref2Box.requestFocus();
            } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
                ref2LinearLayout.setVisibility(View.GONE);
                ref2Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1003));
                ref2Box.setEnabled(false);
            }
//ref1Box
            if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1002));
                ref1Box.setEnabled(false);
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1002));
                //sinn 20180709
//                if(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_RS232_FLAG).equals("1"))
//                if (typeInterface != null) {
//                    ref1Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_REF1));
//                    ref1Box.setEnabled(false);
//                }
                //end 20180709
                ref1Box.setEnabled(true);
                ref1Box.requestFocus();
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
                ref1LinearLayout.setVisibility(View.GONE);
                ref1Box.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1002));
                ref1Box.setEnabled(false);
            }
//comCodeBox
            if (cardManager.getHostCard().equalsIgnoreCase("TMS")) {//K.GAME 181009 don't show comcode
                if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
                    comCodeLinearLayout.setVisibility(View.VISIBLE);
                    comCodeBox.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1001));
                    comCodeBox.setEnabled(false);
                } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
                    comCodeLinearLayout.setVisibility(View.VISIBLE);
                    comCodeBox.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1001));
                    comCodeBox.setEnabled(true);
                    comCodeBox.requestFocus();
                } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
                    comCodeLinearLayout.setVisibility(View.GONE);
                    comCodeBox.setText(Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1001));
                    comCodeBox.setEnabled(false);
                }
            } else comCodeLinearLayout.setVisibility(View.GONE);

            Log.d(TAG, "dialogParaEndble.isShowing() :" + String.valueOf(dialogParaEndble.isShowing()));
        } else {
//            if (typeInterface != null) {
//                Utility.customDialogAlertAuto(ContactlessConfrimActivity.this, "กรุณา First Settlement ก่อนทำรายการ");
//                TellToPosError("ND");
//                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                    @Override
//                    public void success() {
//                        Utility.customDialogAlertAutoClear();
//                        cardManager.abortPBOCProcess();
//                        Intent intent = new Intent(ContactlessConfrimActivity.this, MenuServiceListActivity.class); // Paul_20180704
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(0, 0);
//                    }
//                });
//            } else {
            Utility.customDialogAlert(ContactlessConfrimActivity.this, "กรุณา First Settlement ก่อนทำรายการ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    cardManager.abortPBOCProcess();
                    finish();
                }
            });

//            }
        }


        okBtn_dialogParaEndble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//SINN 20181119 check ref1 if disable all can bypasss
                Boolean SetRailwayFlag = false;

                if (!ref1Box.getText().toString().isEmpty()) {
                    SetRailwayFlag = true;
                }

                String valueParameterEnable = Preference.getInstance(ContactlessConfrimActivity.this).getValueString(Preference.KEY_TAG_1000);  //para enable
//                if (valueParameterEnable.substring(0, 4).equalsIgnoreCase("2222") || valueParameterEnable.substring(0, 4).equalsIgnoreCase("1111")) { //NAMTAN_20190712
                if (!(valueParameterEnable.substring(0, 4).contains("3")) && !(valueParameterEnable.substring(0, 4).contains("4"))) {
                    SetRailwayFlag = true;
                }


                if (SetRailwayFlag) {

                    dialogWaiting.show();

                    try {
                        if (cardManager.getHostCard().equalsIgnoreCase("EPS")) {
                            cardManager.setmakeDataEPS(card.toString(), "", ref1Box.getText().toString(), ref2Box.getText().toString(), ref3Box.getText().toString(), comCodeBox.getText().toString());
                            cardManager.sendMessege();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    ref1Box.setError("กรุณากรอก Ref1");   //SINN 20181108 fix THAI wording.
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(typeInterface!=null)      //SINN 20180714 Fix rs232 cancel POS sale.
//                    TellToPosError("EN");
//                if (typeInterface != null) {
//                    TellToPosError("ND");
//                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                        @Override
//                        public void success() {
//                            Intent intent = new Intent(ContactlessConfrimActivity.this, MenuServiceListActivity.class); // Paul_20180704
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                            overridePendingTransition(0, 0);
//                        }
//                    });
//
//                } else {
                finish();
//                }
            }
        });

    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    private void soundsetting() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = new int[2];
        sound[0] = soundPool.load(this, R.raw.success, 1);
        sound[1] = soundPool.load(this, R.raw.fail, 1);
    }

    private void playSound(int i) {
        soundPool.play(sound[i], 1, 1, 0, 0, 1);
    }


}
