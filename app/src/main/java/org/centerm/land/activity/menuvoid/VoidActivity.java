package org.centerm.land.activity.menuvoid;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.activity.CalculatePriceActivity;
import org.centerm.land.activity.MenuServiceListActivity;
import org.centerm.land.activity.SlipTemplateActivity;
import org.centerm.land.adapter.VoidAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.ReversalTemp;
import org.centerm.land.database.TransTemp;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class VoidActivity extends SettingToolbarActivity {

    private final String TAG = "VoidActivity";

    private EditText invoiceEt;
    private ImageView searchInvoiceImage;
    private Dialog dialogInvoice;
    private RecyclerView recyclerViewVoid;
    private VoidAdapter voidAdapter;
    private ArrayList<TransTemp> transTempList;

    private Realm realm;
    private CardManager cardManager;
    private Dialog dialogWaiting;
    private Dialog dialogPin;

    private String typeHost = null;
    private Dialog dialogApprCode;

    private TransTemp transTemp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void);
        cardManager = MainApplication.getCardManager();
        realm = Realm.getDefaultInstance();
        initData();
        initWidget();
        initBtnExit();
//        setVoidList();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typeHost = bundle.getString(MenuActivity.KEY_MENU_HOST);
        }
    }

    public void initWidget() {
//        super.initWidget();
        customDialogWaiting();
        customDialogApprCode();
        invoiceEt = findViewById(R.id.invoiceEt);
        searchInvoiceImage = findViewById(R.id.searchInvoiceImage);
        invoiceEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchDataTransTemp(invoiceEt.getText().toString());
                    return true;
                }
                return false;
            }
        });
        searchInvoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDataTransTemp(invoiceEt.getText().toString());
            }
        });
        recyclerViewVoid = findViewById(R.id.recyclerViewVoid);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewVoid.setLayoutManager(layoutManager);

        cardManager.setInsertOrUpdateDatabase(new CardManager.InsertOrUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {
                if (!isFinishing()) {
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (dialogPin != null) {
                        if (dialogPin.isShowing()) {
                            dialogPin.dismiss();
                        }
                    }
                    if (dialogApprCode != null) {
                        if (dialogApprCode.isShowing()) {
                            dialogApprCode.dismiss();
                        }
                    }
                    Intent intent = new Intent(VoidActivity.this, SlipTemplateActivity.class);
                    intent.putExtra(CalculatePriceActivity.KEY_CALCUATE_ID, id);
                    intent.putExtra(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID, CalculatePriceActivity.TypeVoid);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    Log.d(TAG, "onUpdateVoidSuccess: ");
                }
            }

            @Override
            public void onInsertSuccess(int nextId) {

            }
        });

        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(final String response) {
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(VoidActivity.this, response, new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onResponseCodeSuccess() {
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                }
            }

            @Override
            public void onConnectTimeOut() {
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(VoidActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onTransactionTimeOut() {
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(VoidActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });

        checkReversal();
    }

    private void checkReversal() {
        ReversalTemp reversalTemp = realm.where(ReversalTemp.class).equalTo("hostTypeCard", typeHost).findFirst();
        if (reversalTemp != null) {
            dialogWaiting.dismiss();
            cardManager.setDataReversalAndSendHost(reversalTemp);
        }

    }

    private void customDialogInvoice(String traceNo, String price) {
        dialogInvoice = new Dialog(this);
        dialogInvoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInvoice.setContentView(R.layout.dialog_custom_invoice);
        dialogInvoice.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogInvoice.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView traceNumberLabel = dialogInvoice.findViewById(R.id.traceNumberLabel);
        TextView priceLabel = dialogInvoice.findViewById(R.id.priceLabel);
        Button saveBtn = dialogInvoice.findViewById(R.id.saveBtn);
        Button cancelBtn = dialogInvoice.findViewById(R.id.cancelBtn);
        traceNumberLabel.setText(getString(R.string.dialog_trace_number, traceNo));
        priceLabel.setText(getString(R.string.dialog_void_amount, price));
        dialogInvoice.show();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!typeHost.equalsIgnoreCase("TMS")) {
                    dialogWaiting.show();
                    dialogInvoice.dismiss();
                    cardManager.setDataVoid(transTemp);
//                    cardManager.insertReversalVoidTransaction(transTemp);
                } else if (typeHost.equalsIgnoreCase("TMS")) {
                    dialogApprCode.show();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInvoice.dismiss();
            }
        });

    }

    private void searchDataTransTemp(String traceNo) {
        RealmResults<TransTemp> transTemp;
        String traceNoAddZero = "";//ถ้าพิมพ์น้อยกว่า 6 ตัวจะติด 0 ข้างหน้า
        if (!traceNo.isEmpty()) {
            if (traceNo.length() < 6) {
                for (int i = traceNo.length(); i < 6; i++) {
                    traceNoAddZero += "0";
                }
            }
            traceNoAddZero += traceNo;
            Log.d(TAG, "searchDataTransTemp: " + traceNoAddZero);

            transTemp = realm.where(TransTemp.class).equalTo("ecr", traceNoAddZero).equalTo("hostTypeCard", typeHost).findAll();
            Log.d(TAG, "searchDataTransTemp: " + transTemp);
            if (transTemp.size() > 0) {
                voidAdapter.clear();
                if (transTempList == null) {
                    transTempList = new ArrayList<>();
                } else {
                    transTempList.clear();
                }
                transTempList.addAll(transTemp);
                voidAdapter.setItem(transTempList);
                voidAdapter.notifyDataSetChanged();
            }


        } else {
            setVoidList();
        }

    }

    private void setVoidList() {
        if (recyclerViewVoid.getAdapter() == null) {
            voidAdapter = new VoidAdapter(this);
            recyclerViewVoid.setAdapter(voidAdapter);
            voidAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    transTemp = voidAdapter.getItem(position);
                    if (transTemp.getVoidFlag().equalsIgnoreCase("N")) {
                        customDialogPin(transTemp.getEcr(), transTemp.getAmount(), transTemp);
                    } else {
                        Utility.customDialogAlert(VoidActivity.this, "รายการนี้ยกเลิกแล้ว", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
        } else {
            voidAdapter.clear();
        }
        if (transTempList == null) {
            transTempList = new ArrayList<>();
        } else {
            transTempList.clear();
        }
            transTempList.addAll(realm.copyFromRealm(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll()));
            voidAdapter.setItem(transTempList);
            voidAdapter.notifyDataSetChanged();

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

    private void customDialogPin(String traceNo, String amount, final TransTemp transTemp) {
        dialogPin = new Dialog(this);
        dialogPin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPin.setContentView(R.layout.dialog_custom_pin);
        dialogPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogPin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPin.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        EditText pinBox = dialogPin.findViewById(R.id.pinBox);
        final TextView inputTextLabel = dialogPin.findViewById(R.id.inputTextLabel);
        pinBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    inputTextLabel.setVisibility(View.INVISIBLE);
                    String keyPin = Preference.getInstance(VoidActivity.this).getValueString(Preference.KEY_PIN);
                    if (s.toString().equalsIgnoreCase(keyPin)) {
                        dialogPin.dismiss();
                        customDialogInvoice(transTemp.getEcr(), transTemp.getAmount());
                        Log.d(TAG, "onTextChanged: " + transTemp.getApprvCode());
                    } else {
                        inputTextLabel.setVisibility(View.VISIBLE);
                        inputTextLabel.setText("PIN ไม่ถูกต้อง");
                    }
                } else {
                    inputTextLabel.setVisibility(View.VISIBLE);
                    inputTextLabel.setText("กรุณากรอก 4 หลัก");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialogPin.show();
    }

    private void customDialogApprCode() {
        dialogApprCode = new Dialog(this);
        dialogApprCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogApprCode.setContentView(R.layout.dialog_custom_appr_code);
        dialogApprCode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogApprCode.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogApprCode.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText apprCodeBox = dialogApprCode.findViewById(R.id.apprCodeBox);
        Button okBtn = dialogApprCode.findViewById(R.id.okBtn);
        Button cancelBtn = dialogApprCode.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apprCodeBox.getText().toString().equalsIgnoreCase(transTemp.getApprvCode())) {
                    dialogWaiting.show();
                    dialogInvoice.dismiss();
                    cardManager.setDataVoid(transTemp);
//                    cardManager.insertReversalVoidTransaction(transTemp);
                } else {
                    Utility.customDialogAlert(VoidActivity.this, "ApprCode ไม่ตรงกัน ", new Utility.OnClickCloseImage() {
                        @Override
                        public void onClickImage(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogApprCode.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setVoidList();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
