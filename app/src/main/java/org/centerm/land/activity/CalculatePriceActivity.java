package org.centerm.land.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.helper.CardPrefix;
import org.centerm.land.model.Card;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.text.DecimalFormat;

public class CalculatePriceActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "CalculatePriceActivity";

    public static String TypeSale = "SALE";
    public static String TypeVoid = "VOID";

    public static final String KEY_CALCUATE_ID = CalculatePriceActivity.class.getName() + "key_calcuate_id";
    public static final String KEY_TYPE_SALE_OR_VOID = CalculatePriceActivity.class.getName() + "key_type_sale_or_void";

    private TextView cardNoLabel = null;
    private FrameLayout sevenClickFrameLayout = null;
    private FrameLayout eightClickFrameLayout = null;
    private FrameLayout nineClickFrameLayout = null;
    private FrameLayout fourClickFrameLayout = null;
    private FrameLayout fiveClickFrameLayout = null;
    private FrameLayout sixClickFrameLayout = null;
    private FrameLayout oneClickFrameLayout = null;
    private FrameLayout twoClickFrameLayout = null;
    private FrameLayout threeClickFrameLayout = null;
    private FrameLayout zeroClickFrameLayout = null;
    private FrameLayout dotClickFrameLayout = null;

    private FrameLayout exitClickFrameLayout = null;
    private FrameLayout deleteClickFrameLayout = null;
    private FrameLayout sureClickFrameLayout = null;
    private TextView priceLabel = null;

    private String numberPrice = "";

    private Card card = null;
    private String typeCard = "";

    private CardManager cardManager;
    private Dialog dialogWaiting;
    private Dialog dialogInputPin;
    private EditText pinBox;
    private Button okBtn;
    private Button cancelBtn;
    private Dialog dialogParaEndble;


    private Dialog dialogAlert = null;
    private TextView msgLabel;

    private boolean stateAbort = false;
    private Dialog dialogCardError;
    private TextView msgCardErrorLabel;
    private ImageView closeCardErrorImage;

    private boolean checkResCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_price);
        cardManager = MainApplication.getCardManager();
        initData();
        initWidget();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            card = (Card) bundle.getSerializable(MenuServiceListActivity.KEY_CARD);
            typeCard = bundle.getString(MenuServiceListActivity.KEY_TYPE_CARD);
            Log.d(TAG, "initData: Card : " + card.getNo() + "\n typeCard : " + typeCard);
        }
    }

    private void initWidget() {
        customDialogAlert();

        cardNoLabel = findViewById(R.id.cardNoLabel);

        oneClickFrameLayout = findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = findViewById(R.id.zeroClickFrameLayout);
        dotClickFrameLayout = findViewById(R.id.dotClickFrameLayout);

        deleteClickFrameLayout = findViewById(R.id.deleteClickFrameLayout);
        sureClickFrameLayout = findViewById(R.id.sureClickFrameLayout);
        exitClickFrameLayout = findViewById(R.id.exitClickFrameLayout);
        priceLabel = findViewById(R.id.priceLabel);
        if (card != null) {
            String cutCardStart = card.getNo().substring(0, 6);
            String cutCardEnd = card.getNo().substring(12, card.getNo().length());
            cardNoLabel.setText(cutCardStart + "XXXXXX" + cutCardEnd);
        }

        oneClickFrameLayout.setOnClickListener(this);
        twoClickFrameLayout.setOnClickListener(this);
        threeClickFrameLayout.setOnClickListener(this);
        fourClickFrameLayout.setOnClickListener(this);
        fiveClickFrameLayout.setOnClickListener(this);
        sixClickFrameLayout.setOnClickListener(this);
        sevenClickFrameLayout.setOnClickListener(this);
        eightClickFrameLayout.setOnClickListener(this);
        nineClickFrameLayout.setOnClickListener(this);
        zeroClickFrameLayout.setOnClickListener(this);
        dotClickFrameLayout.setOnClickListener(this);

        deleteClickFrameLayout.setOnClickListener(this);
        sureClickFrameLayout.setOnClickListener(this);
        exitClickFrameLayout.setOnClickListener(this);
        customDialogWaiting();
        customDialogInputPin();
        customDialogParameterEnable();
        customDialogCardError();
        cardManager.setInsertOrUpdateDatabase(new CardManager.InsertOrUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {

            }

            @Override
            public void onInsertSuccess(int id) {
                Log.d(TAG, "onInsertSuccess: " + id);
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
                cardManager.removeTransResultAbort();
                Intent intent = new Intent(CalculatePriceActivity.this, SlipTemplateActivity.class);
                intent.putExtra(KEY_CALCUATE_ID, id);
                intent.putExtra(KEY_TYPE_SALE_OR_VOID, TypeSale);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });


    }

    private void responseCodeDialog() {
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgLabel.setText(response);
                        if (!isFinishing()) {
                            checkResCode = true;
                            dialogAlert.show();
                        }
                        /*Utility.customDialogAlert(CalculatePriceActivity.this, response, new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });*/
                    }
                });

            }

            @Override
            public void onResponseCodeSuccess() {

            }

            @Override
            public void onConnectTimeOut() {
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            Utility.customDialogAlert(CalculatePriceActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
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
                Log.d(TAG, "TransactionTimeOut: ");
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            Utility.customDialogAlert(CalculatePriceActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
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
        });
    }


    public void customDialogAlert() {
        if (dialogAlert != null) {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        }
        dialogAlert = new Dialog(CalculatePriceActivity.this);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(R.layout.dialog_custom_alert);
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        ImageView closeImage = dialogAlert.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlert.dismiss();
                finish();
                cardManager.endProcess();
            }
        });

    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
    }

    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        }
    }

    private void submitAmount() {
        Log.d(TAG, "submitAmount: " + numberPrice);
        stateAbort = true;
        if (!priceLabel.getText().toString().equalsIgnoreCase("0.00") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0.") &&
                !priceLabel.getText().toString().equalsIgnoreCase("0.0")) {
            if (!cardManager.getHostCard().equalsIgnoreCase("POS")) {
                pinBox.setText("");
                dialogInputPin.show();
                pinBox.requestFocus();
            } else {
                dialogParaEndble.show();
            }
        } else {
            Utility.customDialogAlert(CalculatePriceActivity.this, "กรุณาใส่จำนวนเงิน", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void customDialogInputPin() {
        dialogInputPin = new Dialog(this);
        dialogInputPin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInputPin.setCancelable(false);
        dialogInputPin.setContentView(R.layout.dialog_custom_input_pin);
        dialogInputPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInputPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogInputPin.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        pinBox = dialogInputPin.findViewById(R.id.pinBox);
        okBtn = dialogInputPin.findViewById(R.id.okBtn);
        cancelBtn = dialogInputPin.findViewById(R.id.cancelBtn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInputPin.dismiss();
                dialogParaEndble.show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInputPin.dismiss();
                cardManager.abortPBOCProcess();
                finish();
            }
        });
    }

    private void customDialogParameterEnable() {
        dialogParaEndble = new Dialog(this);
        dialogParaEndble.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogParaEndble.setCancelable(false);
        dialogParaEndble.setContentView(R.layout.dialog_custom_para_enable);
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
        Button okBtn = dialogParaEndble.findViewById(R.id.okBtn);
        Button cancelBtn = dialogParaEndble.findViewById(R.id.cancelBtn);
        String valueParameterEnable = Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1000);
        if (!valueParameterEnable.isEmpty()) {
            if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
                ref3LinearLayout.setVisibility(View.VISIBLE);
                ref3Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1004));
                ref3Box.setEnabled(false);
            } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
                ref3LinearLayout.setVisibility(View.VISIBLE);
                ref3Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1004));
                ref3Box.setEnabled(true);
                ref3Box.requestFocus();
            } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
                ref3LinearLayout.setVisibility(View.GONE);
                ref3Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1004));
                ref3Box.setEnabled(false);
            }
            if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
                ref2LinearLayout.setVisibility(View.VISIBLE);
                ref2Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1003));
                ref2Box.setEnabled(false);
            } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
                ref2LinearLayout.setVisibility(View.VISIBLE);
                ref2Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1003));
                ref2Box.setEnabled(true);
                ref2Box.requestFocus();
            } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
                ref2LinearLayout.setVisibility(View.GONE);
                ref2Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1003));
                ref2Box.setEnabled(false);
            }
            if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1002));
                ref1Box.setEnabled(false);
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
                ref1LinearLayout.setVisibility(View.VISIBLE);
                ref1Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1002));
                ref1Box.setEnabled(true);
                ref1Box.requestFocus();
            } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
                ref1LinearLayout.setVisibility(View.GONE);
                ref1Box.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1002));
                ref1Box.setEnabled(false);
            }
            if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
                comCodeLinearLayout.setVisibility(View.VISIBLE);
                comCodeBox.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1001));
                comCodeBox.setEnabled(false);
            } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
                comCodeLinearLayout.setVisibility(View.VISIBLE);
                comCodeBox.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1001));
                comCodeBox.setEnabled(true);
                comCodeBox.requestFocus();
            } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
                comCodeLinearLayout.setVisibility(View.GONE);
                comCodeBox.setText(Preference.getInstance(CalculatePriceActivity.this).getValueString(Preference.KEY_TAG_1001));
                comCodeBox.setEnabled(false);
            }

        } else {
            Utility.customDialogAlert(CalculatePriceActivity.this, "กรุณา First Settlement ก่อนทำรายการ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    finish();
                    cardManager.abortPBOCProcess();
                }
            });
        }
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ref1Box.getText().toString().isEmpty()) {
                    final DecimalFormat decimalFormat = new DecimalFormat("###0.00");
                    dialogParaEndble.dismiss();
                    dialogWaiting.show();
                    if (cardManager.getHostCard().equalsIgnoreCase("EPS")) {
                        if (typeCard.equalsIgnoreCase(MenuServiceListActivity.IC_CARD)) {
                            cardManager.setImportAmountEPS(decimalFormat.format(Double.valueOf(priceLabel.getText().toString())), pinBox.getText().toString(), ref1Box.getText().toString(), ref2Box.getText().toString(), ref3Box.getText().toString(), comCodeBox.getText().toString());
                        } else {
                            cardManager.setDataSaleFallBackEPS(decimalFormat.format(Double.valueOf(priceLabel.getText().toString())), ref1Box.getText().toString(), ref2Box.getText().toString(), ref3Box.getText().toString(), comCodeBox.getText().toString(), pinBox.getText().toString());
                        }

                    } else if (cardManager.getHostCard().equalsIgnoreCase("TMS")) {
                        cardManager.setDataSalePIN(decimalFormat.format(Double.valueOf(priceLabel.getText().toString())), pinBox.getText().toString(), ref1Box.getText().toString(), ref2Box.getText().toString(), ref3Box.getText().toString(), comCodeBox.getText().toString());
                    } else {
                        if (typeCard.equalsIgnoreCase(MenuServiceListActivity.IC_CARD)) {
                            cardManager.setImportAmount(decimalFormat.format(Double.valueOf(priceLabel.getText().toString())), ref1Box.getText().toString(), ref2Box.getText().toString(), ref3Box.getText().toString(), comCodeBox.getText().toString());
                        } else {
                            cardManager.setDataSaleFallBack(decimalFormat.format(Double.valueOf(priceLabel.getText().toString())), ref1Box.getText().toString(), ref2Box.getText().toString(), ref3Box.getText().toString(), comCodeBox.getText().toString());
                        }
                    }
                } else {
                    ref1Box.setError("กรุณากรอกร Ref1");
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogParaEndble.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Click number: " + numberPrice);
        String[] splitter = null;
        Log.d(TAG, "onClick: " + numberPrice.contains("."));
        if (numberPrice.length() < 8) {
            if (!numberPrice.contains(".")) {
                Log.d(TAG, "if Main : ");
                if (!numberPrice.isEmpty())
                    if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                        numberPrice = "";
                clickCal(v);
            } else {
                Log.d(TAG, "onClick: ");
                Log.d(TAG, "else Main : ");
                splitter = numberPrice.split("\\.");
                if (splitter.length > 1) {
                    Log.d(TAG, "if Sub : ");
                    if (splitter[1].length() > 1) {
                        Log.d(TAG, "splitter[1].length() > 1: ");
                        if (v == exitClickFrameLayout) {
                            cardManager.abortPBOCProcess();
                            finish();
                        } else if (v == deleteClickFrameLayout) {
                            if (!numberPrice.equalsIgnoreCase("0.00")) {
                                Log.d(TAG, "onClick: numberPrice.equalsIgnoreCase(\"0.00\") ");
                                if (numberPrice.length() == 0) {
                                    Log.d(TAG, "onClick: numberPrice.length() If == 0 ");
                                    numberPrice = "0.00";
                                } else {
                                    numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                                    if (numberPrice.length() == 0) {
                                        Log.d(TAG, "onClick: numberPrice.length() Else == 0 ");
                                        numberPrice = "0.00";
                                    }
                                }
                            } else {
                                if (!numberPrice.isEmpty()) {
                                    numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                                }
                            }
                        } else if (v == sureClickFrameLayout) {
                            submitAmount();
                        }
                    } else {
                        if (!numberPrice.isEmpty())
                            /*if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                                numberPrice = "";*/
                            clickCal(v);
                    }
                } else {

                    Log.d(TAG, "splitter[1].length() > 1 Else: ");

                    if (!numberPrice.isEmpty())
                        /*if (numberPrice.substring(0, 1).equalsIgnoreCase("0"))
                            numberPrice = "";*/
                        Log.d(TAG, "else Sub : " + splitter.length);
                    Log.d(TAG, "else Sub : " + splitter[splitter.length - 1]);
                    clickCal(v);
                }
            }
        } else {
            if (v == exitClickFrameLayout) {
                cardManager.abortPBOCProcess();
                finish();
            } else if (v == deleteClickFrameLayout) {
                numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                if (numberPrice.length() == 0) {
                    numberPrice = "";
                    priceLabel.setText("0.00");
                }
            } else if (v == sureClickFrameLayout) {
                submitAmount();
            }
        }

        if (!numberPrice.isEmpty()) {
            priceLabel.setText(numberPrice);
        }
    }

    private void clickCal(View v) {

        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "0";
        } else if (v == dotClickFrameLayout) {
            checkNumberPrice();
            if (!numberPrice.isEmpty()) {
                if (!numberPrice.contains(".")) {
                    numberPrice += ".";
                }
            } else {
                numberPrice += "0.";
            }
        } else if (v == exitClickFrameLayout) {
            cardManager.abortPBOCProcess();
            finish();
        } else if (v == deleteClickFrameLayout) {
            if (!priceLabel.getText().toString().equalsIgnoreCase("0.00")) {
                Log.d(TAG, "clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + numberPrice);
                    numberPrice = "";
                    priceLabel.setText("0.00");
                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        if (numberPrice.equalsIgnoreCase("") || numberPrice == null) {
                            Log.d(TAG, "clickCal: if");
                            numberPrice = "";
                            priceLabel.setText("0.00");
                        } else {
                            Log.d(TAG, "clickCal: else");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } else if (v == sureClickFrameLayout) {
            submitAmount();
        }
    }

    private void setAbortPboc() {
        cardManager.setTransResultAbortLister(new CardManager.TransResultAbortLister() {
            @Override
            public void onTransResultAbort() {
                if (!stateAbort) {
                    Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogCardError.show();
                        }
                    });
                }
            }
        });
    }

    private void customDialogCardError() {
        dialogCardError = new Dialog(this);
        dialogCardError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCardError.setCancelable(false);
        dialogCardError.setContentView(R.layout.dialog_custom_alert_card_error);
        dialogCardError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCardError.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgCardErrorLabel = dialogCardError.findViewById(R.id.msgLabel);
        closeCardErrorImage = dialogCardError.findViewById(R.id.closeImage);
        okBtn = dialogCardError.findViewById(R.id.okBtn);
        closeCardErrorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
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

    private void showOnCardNotConnectHost() {
        cardManager.setCardNoConnectHost(new CardManager.CardNoConnectHost() {
            @Override
            public void onProcessTransResultUnknow() {
                if (!checkResCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlertNotConnect(CalculatePriceActivity.this, "ไม่สามารถทำรายการได้", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
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
            }

            @Override
            public void onProcessTransResultRefuse() {
                if (!checkResCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlertNotConnect(CalculatePriceActivity.this, "ไม่สามารถทำรายการได้", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(CalculatePriceActivity.this, MenuServiceActivity.class);
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cardManager.abortPBOCProcess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateAbort = false;
        if (cardManager != null) {
            setAbortPboc();
            responseCodeDialog();
            showOnCardNotConnectHost();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cardManager != null) {
            cardManager.removeTransResultAbort();
            cardManager.removeResponseCodeListener();
            cardManager.removeCardNoConnectHost();
        }
    }
}
