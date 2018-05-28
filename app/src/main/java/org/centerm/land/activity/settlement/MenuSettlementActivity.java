package org.centerm.land.activity.settlement;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.constant.Constant;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.adapter.MenuSettlementAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.TransTemp;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MenuSettlementActivity extends SettingToolbarActivity {

    private final String TAG = "MenuSettlementActivity";

    public static final String KEY_TYPE_HOST = MenuSettlementActivity.class.getName() + "key_type_host";

    private RecyclerView menuSettleRecyclerView;
    private MenuSettlementAdapter menuSettlementAdapter = null;
    private ArrayList<String> menuList = null;
    private CardManager cardManager = null;
    private Realm realm = null;
    private ArrayList<TransTemp> transTemp = null;
    private ArrayList<TransTemp> transTempVoidFlag = null;
    private LinearLayout settlementLinearLayout;
    private RelativeLayout settlementRelativeLayout;
    private AidlPrinter printDev;
    private Dialog dialogWaiting;

    private String typeHost;

    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView midLabel = null;
    private TextView tidLabel = null;
    private TextView batchLabel = null;
    private TextView hostLabel = null;
    private TextView saleCountLabel = null;
    private TextView saleTotalLabel = null;
    private TextView voidSaleCountLabel = null;
    private TextView voidSaleAmountLabel = null;
    private TextView cardCountLabel = null;
    private TextView cardAmountLabel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settlement);
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        menuSettleRecyclerView = findViewById(R.id.menuSettleRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menuSettleRecyclerView.setLayoutManager(layoutManager);
        setMenuList();
        customDialogWaiting();

        cardManager.setSettlementHelperLister(new CardManager.SettlementHelperLister() {
            @Override
            public void onSettlementSuccess() {
                dialogWaiting.dismiss();
                Intent intent = new Intent(MenuSettlementActivity.this,SlipSettlementActivity.class);
                intent.putExtra(KEY_TYPE_HOST,typeHost);
                startActivity(intent);
                overridePendingTransition(0,0);
            }

            @Override
            public void onConnectTimeOut() {

            }

            @Override
            public void onTransactionTimeOut() {

            }
        });
        if (transTemp == null) {
            transTemp = new ArrayList<>();
        } else {
            transTemp.clear();
        }
        if (transTempVoidFlag == null) {
            transTempVoidFlag = new ArrayList<>();
        } else {
            transTempVoidFlag.clear();
        }

//        LayoutInflater inflater =
//                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View tagView = inflater.inflate(R.layout.view_slip_settlement, null);
//        settlementRelativeLayout = tagView.findViewById(R.id.settlementRelativeLayout);
//        tagView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        tagView.layout(0, 0, tagView.getMeasuredWidth(), tagView.getMeasuredHeight());
        Dialog tagView = new Dialog(this);
        tagView.setContentView(R.layout.view_slip_settlement);
        tagView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tagView.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        settlementLinearLayout = tagView.findViewById(R.id.settlementLinearLayout);
        dateLabel = tagView.findViewById(R.id.dateLabel);
        timeLabel = tagView.findViewById(R.id.timeLabel);
        midLabel = tagView.findViewById(R.id.midLabel);
        tidLabel = tagView.findViewById(R.id.tidLabel);
        batchLabel = tagView.findViewById(R.id.batchLabel);
        hostLabel = tagView.findViewById(R.id.hostLabel);
        saleCountLabel = tagView.findViewById(R.id.saleCountLabel);
        saleTotalLabel = tagView.findViewById(R.id.saleTotalLabel);
        voidSaleCountLabel = tagView.findViewById(R.id.voidSaleCountLabel);
        voidSaleAmountLabel = tagView.findViewById(R.id.voidSaleAmountLabel);
        cardCountLabel = tagView.findViewById(R.id.cardCountLabel);
        cardAmountLabel = tagView.findViewById(R.id.cardAmountLabel);
    }

    private void setViewSlip() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<TransTemp> transTemp = realm.where(TransTemp.class).equalTo("voidFlag","N").equalTo("hostTypeCard",typeHost).findAll();
        float amountSale = 0;
        float amountVoid = 0;
        for (int i = 0; i < transTemp.size(); i++) {
            amountSale += Float.valueOf(transTemp.get(i).getAmount());
        }
        RealmResults<TransTemp> transTempVoid = realm.where(TransTemp.class).equalTo("voidFlag","Y").equalTo("hostTypeCard",typeHost).findAll();
        for (int i = 0; i < transTempVoid.size(); i++) {
            amountVoid += Float.valueOf(transTempVoid.get(i).getAmount());
        }
        Date date = new Date();

        voidSaleCountLabel.setText(transTempVoid.size()+"");
        voidSaleAmountLabel.setText(String.format("%.2f",amountVoid));
        cardCountLabel.setText((transTemp.size() + transTempVoid.size()) + "");
        cardAmountLabel.setText(String.format("%.2f",amountVoid + amountSale));
        dateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        dateLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
        if (typeHost.equalsIgnoreCase("POS")) {
            hostLabel.setText("OFFUS POS");
            batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_POS));
            tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_POS));
            midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_POS));
        } else if (typeHost.equalsIgnoreCase("EPS")) {
            hostLabel.setText("OFFUS EPS");
            batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_EPS));
            tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_EPS));
            midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_EPS));
        } else {
            hostLabel.setText("KTB ONUS");
            batchLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_BATCH_NUMBER_TMS));
            tidLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_TERMINAL_ID_TMS));
            midLabel.setText(Preference.getInstance(MenuSettlementActivity.this).getValueString(Preference.KEY_MERCHANT_ID_TMS));
        }
        realm.close();
        realm = null;
        doPrinting(getBitmapFromView(settlementLinearLayout));
        cardManager.deleteTransTemp();
    }

    private void setMenuList() {
        if (menuSettleRecyclerView.getAdapter() == null) {
            menuSettlementAdapter = new MenuSettlementAdapter(this);
            menuSettleRecyclerView.setAdapter(menuSettlementAdapter);
            menuSettlementAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        typeHost = "POS";
                        selectDataTransTemp("POS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
                                cardManager.setDataSettlementAndSend("POS");
                            } else {
                                cardManager.setDataSettlementAndSend("POS");
                            }
                            dialogWaiting.show();
                        } else {
                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    } else if (position == 1) {
                        typeHost = "EPS";
                        selectDataTransTemp("EPS");if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
                                cardManager.setDataSettlementAndSendEPS();
                            } else {
                                cardManager.setDataSettlementAndSendEPS();
                            }
                            dialogWaiting.show();
                        } else {
                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    } else if (position == 2) {
                        typeHost = "TMS";
                        selectDataTransTemp("TMS");
                        if (transTemp.size() > 0) {
                            if (transTempVoidFlag.size() != 0) {
                                cardManager.setDataSettlementAndSendTMS();
                            } else {
                                cardManager.setDataSettlementAndSendTMS();
                            }
                            dialogWaiting.show();
                        } else {
                            Utility.customDialogAlert(MenuSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                }
            });
        } else {
            menuSettlementAdapter.clear();
        }
        if (menuList == null) {
            menuList = new ArrayList<>();
        } else {
            menuList.clear();
        }
        menuList.add("POS");
        menuList.add("EPS");
        menuList.add("TMS");
        menuSettlementAdapter.setItem(menuList);
        menuSettlementAdapter.notifyDataSetChanged();

    }

    private void selectDataTransTemp(String typeHost) {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        transTemp.addAll(realm.where(TransTemp.class).equalTo("hostTypeCard", typeHost).findAll());
        transTempVoidFlag.addAll(realm.where(TransTemp.class).equalTo("voidFlag", "N").equalTo("hostTypeCard", typeHost).findAll());
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    public void doPrinting(final Bitmap slip) {
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    int ret = printDev.printBmpFastSync(slip, Constant.ALIGN.CENTER);
//                    int ret = printDev.printBarCodeSync("asdasd");
                    Log.d(TAG, "after call printData ret = " + ret);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
        animationDrawable.start();
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }
}
