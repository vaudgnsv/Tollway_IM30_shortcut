package org.centerm.Tollway.activity.menuvoid;

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

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.adapter.MenuVoidAdapter;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.ReversalTemp;
import org.centerm.Tollway.utility.Utility;

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
//        callBackConnect();
    }

    private void callBackConnect() {
        cardManager.setConnectStatusSocket(new CardManager.ConnectStatusSocket() {
            @Override
            public void onConnectTimeOut() {

            }

            @Override
            public void onTransactionTimeOut() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onError(String msg) {

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
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }
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
            public void onResponseCodeandMSG(String response, String szCode) {
                System.out.printf("utility:: MenuActivity onResponseCodeandMSG 000003 \n");

            }

            @Override
            public void onResponseCodeSuccess() {

            }

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
        });

    }

    private void setCallBackReversal() {
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

        nameMenuList.add("KTB ONUS"); //2 0         // Paul_20181028 Sinn merge version UAT6_0016
        nameMenuList.add("KTB OFFUS"); //0 1        // Paul_20181028 Sinn merge version UAT6_0016
        nameMenuList.add("WAY4"); //1 2
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
        //K.GAME 180831 chang waitting UI
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        msgLabel = dialogWaiting.findViewById(R.id.msgLabel);
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogWaiting);
        //END K.GAME 180831 chang waitting UI
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
        if (cardManager != null) {
            setCallBackReversal();
            callBackReversal();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }

    @Override
    protected void onPause() {
        super.onPause();
        cardManager.removeReversalListener();
        cardManager.removeResponseCodeListener();
    }
}
