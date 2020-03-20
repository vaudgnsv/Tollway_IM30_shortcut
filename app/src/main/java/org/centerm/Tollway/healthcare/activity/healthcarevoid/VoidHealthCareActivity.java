package org.centerm.Tollway.healthcare.activity.healthcarevoid;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.healthcare.adapter.VoidHealthCareAdapter;
import org.centerm.Tollway.healthcare.baseavtivity.BaseHealthCardActivity;
import org.centerm.Tollway.healthcare.database.HealthCareDB;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

public class VoidHealthCareActivity extends BaseHealthCardActivity {

    private VoidHealthCareAdapter voidHealthCareAdapter;
    private RecyclerView recyclerViewVoid;

    private Realm realm;
    private Dialog dialogInvoice;
    private HealthCareDB healthCareDB;
    private String[] mBlockDataSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_health_care);
        realm = Realm.getDefaultInstance();
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        recyclerViewVoid = findViewById(R.id.recyclerViewVoid);
        recyclerViewVoid.setLayoutManager(new LinearLayoutManager(this));
        setListSale();
    }

    private void setListSale() {
        if (recyclerViewVoid.getAdapter() == null) {
            voidHealthCareAdapter = new VoidHealthCareAdapter(this);
            recyclerViewVoid.setAdapter(voidHealthCareAdapter);
            voidHealthCareAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    healthCareDB = voidHealthCareAdapter.getItem(position);
                    customDialogInvoice(healthCareDB.getTraceNo(),healthCareDB.getAmount());
// Paul_20180718
//                    Toast.makeText(VoidHealthCareActivity.this, "" + healthCareDB.getAmount(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            voidHealthCareAdapter.clear();
        }
        voidHealthCareAdapter.setItem(new ArrayList<>(realm.where(HealthCareDB.class).findAll()));
        voidHealthCareAdapter.notifyDataSetChanged();
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
                setDataVoid();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInvoice.dismiss();
            }
        });

    }
    private void setDataVoid() {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        String MERCHANT_NUMBER = CardPrefix.getMerchantId(this, "GHC");
        String TERMINAL_ID = CardPrefix.getTerminalId(this, "GHC");
        String batchNumber = CardPrefix.getBatch(this, "GHC");
        Date dateTime = new Date();
        String datePatten = new SimpleDateFormat("yyyyMMddHHmmss").format(dateTime);
        mBlockDataSend = new String[64];
        String cardNumber = "000" + healthCareDB.getCardNumber().trim().replace(" ", "");
        if ((cardNumber.length() % 2) != 0) {
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber + "0";
        } else {
            mBlockDataSend[2 - 1] = cardNumber.length() + cardNumber;
        }
        mBlockDataSend[3 - 1] = "025000";

        mBlockDataSend[4 - 1] = BlockCalculateUtil.getAmount(decimalFormat.format(Double.valueOf(healthCareDB.getAmount())));
        mBlockDataSend[11 - 1] = Utility.calNumTraceNo(CardPrefix.geTraceId(this, "GHC"));
        mBlockDataSend[22 - 1] = "0022";
        mBlockDataSend[24 - 1] = Preference.getInstance(this).getValueString(Preference.KEY_NII_GHC);
        mBlockDataSend[25 - 1] = healthCareDB.getConditionCode();
        mBlockDataSend[37 - 1] = BlockCalculateUtil.getHexString(healthCareDB.getRefNo());
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(Utility.calNumTraceNo(Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC)));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(Utility.calNumTraceNo(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC)));
        mBlockDataSend[62 - 1] = getLength62(String.valueOf(healthCareDB.getInvoice().length())) + BlockCalculateUtil.getHexString(healthCareDB.getInvoice());
        mBlockDataSend[63 - 1] = healthCareDB.getDe63Sale();
        String TPDU = CardPrefix.getTPDU(this, "GHC");
        packageAndSend(TPDU, "0200", mBlockDataSend);
    }

    @Override
    protected void connectTimeOut() {
        Utility.customDialogAlert(VoidHealthCareActivity.this, "connectTimeOut", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void transactionTimeOut() {
        Utility.customDialogAlert(VoidHealthCareActivity.this, "transactionTimeOut", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void received(String[] data) {
        System.out.printf("utility:: VoidHealthCareActivity received 001 \n");
        Utility.customDialogAlertSuccess(VoidHealthCareActivity.this, null, new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void error(String error) {
//        Utility.customDialogAlert(VoidHealthCareActivity.this, "error : " + error, new Utility.OnClickCloseImage() {
//            @Override
//            public void onClickImage(Dialog dialog) {
//                dialog.dismiss();
//            }
//        });
        Utility.customDialogAlert(this, "Error 001", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
// Paul_20180809
                Intent intent = new Intent(VoidHealthCareActivity.this, MenuServiceListActivity.class);
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
        Utility.customDialogAlert(VoidHealthCareActivity.this, "error", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }
}
