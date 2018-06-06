package org.centerm.land.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.activity.menuvoid.MenuActivity;
import org.centerm.land.activity.settlement.MenuSettlementActivity;
import org.centerm.land.adapter.MenuServiceAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.database.ReversalTemp;
import org.centerm.land.model.Card;
import org.centerm.land.utility.Utility;

import java.util.ArrayList;

import io.realm.Realm;

public class MenuServiceListActivity extends SettingToolbarActivity {

    private final String TAG = this.getClass().getName();

    private Realm realm = null;

    public static final String MSG_CARD = "MSG_CARD";
    public static final String IC_CARD = "IC_CARD";

    public static final String KEY_CARD = "key_card";
    public static final String KEY_TYPE_CARD = MenuSettlementActivity.class.getName() + "key_type_card";

    private RecyclerView recyclerViewMenuList = null;
    private MenuServiceAdapter menuServiceAdapter = null;
    private ArrayList<String> nameMenuList = null;
    private LinearLayout linearLayoutToolbarBottom = null;
    private Dialog dialogInsertCard = null;
    private Dialog dialogWaiting = null;
    private CountDownTimer timer = null;

    private CardManager cardManager = null;
    private Card cardNo;
    private Dialog dialogFallBack;
    private boolean isFallBack = false;
    private TextView msgLabel;
    private Dialog dialogServiceCode;

    private String typeCard = null;
    private String typeClick = null;

    private int numFallBack = 0;
    private TextView msgFallBackLabel;
    private ImageView closeFallBackImage;
    private Button okBtn;
    private Dialog dialogFallBackCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_service_list);

        initWidget();
        initBtnExit();
    }

    public void initWidget() {
//        super.initWidget();
        nameMenuList = new ArrayList<>();
        nameMenuList.add("Sale");
        nameMenuList.add("Void");
        nameMenuList.add("Settlement");
//        nameMenuList.add("Batch Upload");
//        nameMenuList.add("TC Upload");
        nameMenuList.add("Detail Report");
        nameMenuList.add("Reprint");
        recyclerViewMenuList = findViewById(R.id.recyclerViewMenuList);
        linearLayoutToolbarBottom = findViewById(R.id.linearLayoutToolbarBottom);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMenuList.setLayoutManager(layoutManager);
        setMenuList();
        customDialogFallBack();
        customDialog();
        customDialogWaiting();
        customDialogCheckFallBack();
        cardManager = MainApplication.getCardManager();
        cardManager.setCardHelperListener(new CardManager.CardHelperListener() {
            @Override
            public void onCardInfoReceived(final Card card) {
                if (card != null) {
                    cardNo = card;
                    typeCard = IC_CARD;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogWaiting.show();
                            setTimer(2000, 2);
                            dialogInsertCard.dismiss();
                        }
                    });
                    cardManager.stopTransaction();
                    Log.d(TAG, "onCardInfoReceived: " + card.toString());
                }
            }

            @Override
            public void onCardInfoFail() {
                Log.d(TAG, "onCardInfoFail: ");
                cardManager.stopTransaction();
            }

            @Override
            public void onTransResultFallBack() {
                if (numFallBack > 1) {
                    cardManager.setFallBackHappen();
                }
                if (numFallBack > 1) {
                    numFallBack = 0;
                    isFallBack = true;
                    Log.d(TAG, "onTransResultFallBack: ");
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (timer != null)
                        timer.cancel();
                    if (dialogInsertCard != null)
                        dialogInsertCard.cancel();
                    cardManager.stopTransaction();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogFallBack.show();
                            cardManager.startTransaction(CardManager.SALE);
                            setTimer(15000, 1);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ++numFallBack;

                            cardManager.stopTransaction();
                            dialogFallBackCheck.show();

                        }
                    });
                }
            }

            @Override
            public void onCardTransactionUpdate(boolean isApproved, Card card) {
                Log.d(TAG, "onCardTransactionUpdate: " + isApproved + " Card : " + card);
                if (isApproved) {
                    cardManager.stopTransaction();
                }
            }

            @Override
            public void onFindMagCard(Card card) {
                cardNo = card;
                typeCard = MSG_CARD;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (dialogFallBack != null)
                            dialogFallBack.dismiss();
                        if (dialogInsertCard != null)
                            dialogInsertCard.dismiss();
                        if (dialogWaiting != null)
                            dialogWaiting.show();
                        setTimer(2000, 3);
                        dialogInsertCard.dismiss();

                        cardManager.stopTransaction();
                    }
                });
            }

            @Override
            public void onSwapCardIc() {
                Log.d(TAG, "onSwapCardIc: ");
                dismissDialogAll();
                cardManager.stopTransaction();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customDialogServiceCode();
                    }
                });
            }

            @Override
            public void onSwapCardMag() {
                cardManager.abortPBOCProcess();
                dismissDialogAll();
                cardManager.stopTransaction();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuServiceListActivity.this);
                        builder.setMessage("กรุณารูดบัตร")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialogFallBack.show();
                                        setTimer(15000, 1);
                                        cardManager.startTransaction(CardManager.SALE);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }

            @Override
            public void onSwipeCardFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuServiceListActivity.this);
                        builder.setMessage("สไลด์กาดล้มเหลว")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dismissDialogAll();
                                        cardManager.startTransaction(CardManager.SALE);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }
        });
        //Check ว่า Reversal ได้ไหม
        cardManager.setInsertOrUpdateDatabase(new CardManager.InsertOrUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {

            }

            @Override
            public void onInsertSuccess(int nextId) {

            }
        });

        cardManager.setConnectStatusSocket(new CardManager.ConnectStatusSocket() {
            @Override
            public void onConnectTimeOut() {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }

            @Override
            public void onTransactionTimeOut() {
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlert(MenuServiceListActivity.this, "เชื่อมต่อล้มเหลว", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    if (dialogWaiting != null)
                        dialogWaiting.dismiss();
                    if (timer != null) {
                        timer.cancel();
                    }
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

        callReversal();
    }

    private void dismissDialogAll() {
        if (dialogWaiting != null)
            dialogWaiting.dismiss();
        if (timer != null)
            timer.cancel();
        if (dialogInsertCard != null)
            dialogInsertCard.cancel();
        if (dialogFallBack != null)
            dialogFallBack.dismiss();
        if (dialogServiceCode != null)
            dialogServiceCode.dismiss();

    }

    //Check ว่า Reversal ได้ไหม
    private void callReversal() {
        cardManager.setReversalListener(new CardManager.ReversalListener() {
            @Override
            public void onReversalSuccess() {
                Log.d(TAG, "onReversalSuccess: ");
                dismissDialogAll();
                cardManager.stopTransaction();
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customDialogAlertSuccess(MenuServiceListActivity.this, "Reversal สำเร็จ", new Utility.OnClickCloseImage() {
                                @Override
                                public void onClickImage(Dialog dialog) {
                                    dialog.dismiss();
                                    if (typeClick.equalsIgnoreCase("SALE")) {
                                        Log.d(TAG, "onReversalSuccess: SALE");
                                        startInsertCard();
                                    } else if (typeClick.equalsIgnoreCase("VOID")) {
                                        Log.d(TAG, "onReversalSuccess: VOID");
                                        Intent intent = new Intent(MenuServiceListActivity.this, MenuActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    } else if (typeClick.equalsIgnoreCase("SETTLEMENT")) {
                                        Log.d(TAG, "onReversalSuccess: SETTLEMENT");
                                        Intent intent = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
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

    private void startInsertCard() {
        cardManager.startTransaction(CardManager.SALE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTimer(15000, 1);
                dialogInsertCard.show();
            }
        });

    }

    private void setMenuList() {
        if (recyclerViewMenuList.getAdapter() == null) {
            menuServiceAdapter = new MenuServiceAdapter(this);
            recyclerViewMenuList.setAdapter(menuServiceAdapter);
            menuServiceAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    numFallBack = 0;
                    if (position == 0) {
                        cardManager.setFalseFallbackHappen();
                        if (checkReversal("SALE")) {
                            startInsertCard();
                        }
                    } else if (position == 1) {
                        if (checkReversal("VOID")) {
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    } else if (position == 2) {
                        if (checkReversal("SETTLEMENT")) {
                            Intent intent = new Intent(MenuServiceListActivity.this, MenuSettlementActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    } else if (position == 3) {
                        Intent intent = new Intent(MenuServiceListActivity.this, MenuDetailReportActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (position == 4) {
                        Intent intent = new Intent(MenuServiceListActivity.this, ReprintActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (position == 5) {

                    } else if (position == 6) {

                    }
                }
            });
        } else {
            menuServiceAdapter.clear();
        }
        menuServiceAdapter.setItem(nameMenuList);
        menuServiceAdapter.notifyDataSetChanged();
    }

    private boolean checkReversal(String typeClick) {
        this.typeClick = typeClick;
        ReversalTemp reversalTemp = null;
        reversalTemp = realm.where(ReversalTemp.class).findFirst();
        if (reversalTemp != null) {
            dialogWaiting.show();
            cardManager.setDataReversalAndSendHost(reversalTemp);
            return false;
        } else {
            dismissDialogAll();
            return true;
        }
    }

    private void customDialog() {
        dialogInsertCard = new Dialog(this);
        dialogInsertCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInsertCard.setCancelable(false);
        dialogInsertCard.setContentView(R.layout.dialog_custom_sale);
        dialogInsertCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInsertCard.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView closeImage = dialogInsertCard.findViewById(R.id.closeImage);
        ImageView cardInsertImage = dialogInsertCard.findViewById(R.id.cardInsertImage);
        final AnimationDrawable animationDrawable = (AnimationDrawable) cardInsertImage.getBackground();
        animationDrawable.start();
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInsertCard.dismiss();
                animationDrawable.stop();
                if (timer != null) {
                    timer.cancel();
                }
                cardManager.stopTransaction();
            }
        });
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

    private void customDialogServiceCode() {
        dialogServiceCode = new Dialog(this);
        dialogServiceCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogServiceCode.setCancelable(false);
        dialogServiceCode.setContentView(R.layout.dialog_custom_service_code);
        dialogServiceCode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogServiceCode.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView closeImage = dialogServiceCode.findViewById(R.id.closeImage);
        ImageView skewerImage = dialogServiceCode.findViewById(R.id.skewerImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) skewerImage.getBackground();
        animationDrawable.start();
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogServiceCode.dismiss();
                cardManager.stopTransaction();
            }
        });
        cardManager.startTransaction(CardManager.SALE);
        setTimer(15000, 1);
        dialogServiceCode.show();
    }

    private void setTimer(final long time, final int typeTimer) { // typeTime 1 = Insert Card 2 = Waiting process

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d(TAG, "onTick: " + millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        if (typeTimer == 1) {
                            dismissDialogAll();
                            cardManager.stopTransaction();
                        } else if (typeTimer == 2) {
                            dismissDialogAll();
                            cardManager.stopTransaction();
                            Intent intent = new Intent(MenuServiceListActivity.this, CalculatePriceActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KEY_CARD, cardNo);
                            bundle.putString(KEY_TYPE_CARD, IC_CARD);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        } else {
                            dismissDialogAll();
                            cardManager.stopTransaction();
                            Intent intent = new Intent(MenuServiceListActivity.this, CalculatePriceActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KEY_CARD, cardNo);
                            bundle.putString(KEY_TYPE_CARD, MSG_CARD);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    }
                };
                timer.start();
            }
        });

    }

    private void customDialogFallBack() {
        dialogFallBack = new Dialog(this);
        dialogFallBack.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFallBack.setCancelable(false);
        dialogFallBack.setContentView(R.layout.dialog_custom_mag);
        dialogFallBack.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFallBack.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogFallBack.findViewById(R.id.msgLabel);
        ImageView closeImage = dialogFallBack.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numFallBack = 0;
                dialogFallBack.dismiss();
                if (timer != null) {
                    timer.cancel();
                }
                cardManager.stopTransaction();
            }
        });
    }

    private void customDialogCheckFallBack() {
        dialogFallBackCheck = new Dialog(this);
        dialogFallBackCheck.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFallBackCheck.setCancelable(false);
        dialogFallBackCheck.setContentView(R.layout.dialog_custom_alert_fall_back);
        dialogFallBackCheck.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFallBackCheck.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgFallBackLabel = dialogFallBackCheck.findViewById(R.id.msgLabel);
        closeFallBackImage = dialogFallBackCheck.findViewById(R.id.closeImage);
        okBtn = dialogFallBackCheck.findViewById(R.id.okBtn);
        closeFallBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*dialogFallBack.dismiss();
                if (timer != null) {
                    timer.cancel();
                }
                cardManager.stopTransaction();*/
                numFallBack = 0;
                dialogFallBack.dismiss();
                if (dialogWaiting != null)
                    dialogWaiting.dismiss();
                if (timer != null)
                    timer.cancel();
                if (dialogInsertCard != null)
                    dialogInsertCard.cancel();
                cardManager.stopTransaction();

            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFallBackCheck.dismiss();
                cardManager.startTransaction(CardManager.SALE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
        numFallBack = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
