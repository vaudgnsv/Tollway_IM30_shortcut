package org.centerm.land.activity.menuvoid;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.adapter.MenuVoidAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.ReversalTemp;
import org.centerm.land.utility.Utility;

import java.util.ArrayList;

import io.realm.Realm;

public class MenuActivity extends SettingToolbarActivity {

    private final String TAG = "MenuActivity";

    private RecyclerView menuRecyclerView;
    private MenuVoidAdapter menuVoidAdapter;
    private ArrayList<String> nameMenuList;
    public static final String KEY_MENU_HOST = MenuActivity.class.getName() + "key_menu_host";
    private Realm realm = null;
    private Dialog dialogWaiting;
    private CardManager cardManager = null;
    private TextView msgLabel;

    private String typeHost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        cardManager = MainApplication.getCardManager();
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);
        setMenuList();
        customDialogWaiting();
        callBackReversal();
        callBackConnect();
    }

    private void callBackConnect() {
        cardManager.setConnectStatusSocket(new CardManager.ConnectStatusSocket() {
            @Override
            public void onConnectTimeOut() {
                Log.d(TAG, "onConnectTimeOut: ");
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(MenuActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
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
                Log.d(TAG, "onConnectTimeOut: ");
                if (!isFinishing()) {
                    if (dialogWaiting != null) {
                        dialogWaiting.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(MenuActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
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
            public void onError() {

            }

            @Override
            public void onOther() {

            }

            @Override
            public void onReceived() {

            }
        });
    }

    private void callBackReversal() {
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(final String response) {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(MenuActivity.this, response, new Utility.OnClickCloseImage() {
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
            public void onResponseCodeSuccess() {

            }

            @Override
            public void onConnectTimeOut() {
                Log.d(TAG, "onConnectTimeOut: ");
            }

            @Override
            public void onTransactionTimeOut() {
                Log.d(TAG, "onTransactionTimeOut: ");
            }
        });
        cardManager.setReversalListener(new CardManager.ReversalListener() {
            @Override
            public void onReversalSuccess() {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (dialogWaiting != null) {
                                dialogWaiting.dismiss();
                            }
                            Utility.customDialogAlertSuccess(MenuActivity.this, "Revesal สำเร็จ", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    if (typeHost.equals("POS")) {
                                        Intent intent = new Intent(MenuActivity.this, VoidActivity.class);
                                        intent.putExtra(KEY_MENU_HOST, "POS");
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    } else if (typeHost.equals("EPS")) {
                                        Intent intent = new Intent(MenuActivity.this, VoidActivity.class);
                                        intent.putExtra(KEY_MENU_HOST, "EPS");
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    } else if (typeHost.equals("TMS")) {
                                        Intent intent = new Intent(MenuActivity.this, VoidActivity.class);
                                        intent.putExtra(KEY_MENU_HOST, "TMS");
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void setMenuList() {
        if (menuRecyclerView.getAdapter() == null) {
            menuVoidAdapter = new MenuVoidAdapter(this);
            menuRecyclerView.setAdapter(menuVoidAdapter);
            menuVoidAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        if (checkReversal("TMS")) {
                            Intent intent = new Intent(MenuActivity.this, VoidActivity.class);
                            intent.putExtra(KEY_MENU_HOST, "TMS");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }

                    } else if (position == 1) {
                        if (checkReversal("POS")) {
                            Intent intent = new Intent(MenuActivity.this, VoidActivity.class);
                            intent.putExtra(KEY_MENU_HOST, "POS");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    } else if (position == 2) {
                        if (checkReversal("EPS")) {
                            Intent intent = new Intent(MenuActivity.this, VoidActivity.class);
                            intent.putExtra(KEY_MENU_HOST, "EPS");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    }
                }
            });
        } else {
            menuVoidAdapter.clear();
        }
        if (nameMenuList == null) {
            nameMenuList = new ArrayList<>();
        } else {
            nameMenuList.clear();
        }

        nameMenuList.add("KTB On Us"); //2 0
        nameMenuList.add("KTB Off us"); //0 1
        nameMenuList.add("BASE24 EPS"); //1 2
        menuVoidAdapter.setItem(nameMenuList);
        menuVoidAdapter.notifyDataSetChanged();
    }
    private boolean checkReversal(String type) {
        typeHost = type;
            ReversalTemp reversalTemp = null;
            reversalTemp = realm.where(ReversalTemp.class).findFirst();
            if (reversalTemp != null) {
                dialogWaiting.show();
                cardManager.setDataReversalAndSendHost(reversalTemp);
                return false;
            } else {
                return true;
            }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
