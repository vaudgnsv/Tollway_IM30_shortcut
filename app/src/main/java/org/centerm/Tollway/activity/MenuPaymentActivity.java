package org.centerm.Tollway.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.qr.MenuQrActivity;
import org.centerm.Tollway.adapter.MenuPaymentAdapter;
import org.centerm.Tollway.helper.CardPrefix;

import java.util.ArrayList;

public class MenuPaymentActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();
    private RecyclerView recyclerViewPayment;
    private AlertDialog.Builder builder;
    private org.centerm.Tollway.adapter.MenuPaymentAdapter MenuPaymentAdapter;
    private ArrayList<String> menuPaymentList = null;

    private int GHCVoidFlg = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_payment);
        initData();
        initWidget();
        initBtnExit();
    }

    private void initData() {
    }

    public void initWidget() {
        // super.initWidget();
        menuPaymentList = new ArrayList<>();
        menuPaymentList.clear();

        menuPaymentList.add("รายการ QR");
        menuPaymentList.add("รายการขาย");


        recyclerViewPayment = findViewById(R.id.recyclerViewPayment);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); //K.GAME Test
        gridLayoutManager.setSpanCount(3);//K.GAME Test
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);//K.GAME Test
        recyclerViewPayment.setLayoutManager(layoutManager);
        setMenuList();
    }

    private int checkAllBatch() {
        return 0;
    }

    private int checkBatchSettlement() {
        return 0;
    }

    private void setMenuList() {
        {
            if (recyclerViewPayment.getAdapter() == null) {

                MenuPaymentAdapter = new MenuPaymentAdapter(this);
                recyclerViewPayment.setAdapter(MenuPaymentAdapter);


                MenuPaymentAdapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        switch (menuPaymentList.get(position)) {
                            case "รายการ QR":
                                if (checkAllBatch() != 1)    // Paul_20180803
                                    CardPrefix.getStringJson(MenuPaymentActivity.this); //20180815 SINN JSON // getStringJsonCAPK();

                                if (checkBatchSettlement() != 1) {       // Paul_20180803
                                    Intent intent = new Intent(MenuPaymentActivity.this, MenuQrActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }
                                break;
                            case "รายการขาย":
                                Toast.makeText(MenuPaymentActivity.this, "รายการขาย ยังไม่เปิดใช้", Toast.LENGTH_SHORT).show();
//                                if (checkAllBatch() != 1)    // Paul_20180803
//                                    CardPrefix.getStringJson(MenuPaymentActivity.this); //20180815 SINN JSON // getStringJsonCAPK();
//
//                                GHCVoidFlg = 0;
//                                if (checkBatchSettlement() != 1) {       // Paul_20180803
//                                    System.out.printf("utility:: %s setMenuList 001 \n", TAG);
//                                    cardManager.setFalseFallbackHappen();
//                                    System.out.printf("utility:: %s setMenuList 002 \n", TAG);
//                                    if (checkReversal("SALE")) {
//                                        System.out.printf("utility:: %s setMenuList 003 \n", TAG);
//                                        startInsertCard();
//                                    }
//                                }
                                break;
                        }

                    }
                });
            } else {
                MenuPaymentAdapter.clear();
            }
            MenuPaymentAdapter.setItem(menuPaymentList);
            MenuPaymentAdapter.notifyDataSetChanged();
        }
    }


    private void setDialog() {
        builder = new AlertDialog.Builder(MenuPaymentActivity.this);
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
    }
}
