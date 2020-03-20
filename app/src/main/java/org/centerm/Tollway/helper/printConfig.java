package org.centerm.Tollway.helper;

import android.app.Dialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class printConfig extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "print_config";

    private CardManager cardManager = null;
    private Bitmap bitmapOld;
    private Button okBtn;
    private Dialog dialogOutOfPaper;
    private View slipView;
    private Dialog dialogLoading;
    private AidlPrinter printDev = null;
    private TextView msgLabel;


    private printConfigAdapter printConfigAdapter;
    private RecyclerView recyclerViewReportDetail;

    private List<String> nameList;
    private LinearLayout slipLinearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_reprint);
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        initWidget();
//        initBtnExit();
        customDialogLoading();
        customDialogOutOfPaper();

    }


    @Override
    public void initWidget() {
        printDev = cardManager.getInstancesPrint();
        setViewPrintSlip();
        customDialogOutOfPaper();
        customDialogLoading();

    }


    private void customDialogLoading() {
        dialogLoading = new Dialog(this);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(R.layout.dialog_custom_load_process);
        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogLoading.findViewById(R.id.waitingImage);
        TextView msgLabel = dialogLoading.findViewById(R.id.msgLabel);
        msgLabel.setText("กรุณารอสักครู่...");
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogLoading);
        //END K.GAME 180831 chang waitting UI

//        dialogLoading = new Dialog(this);
//        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogLoading.setContentView( R.layout.dialog_custom_alert_loading);
//        dialogLoading.setCancelable(false);
//        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogLoading.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }


    private void setMeasureSlip() {
        slipView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        slipView.layout(0, 0, slipView.getMeasuredWidth(), slipView.getMeasuredHeight());
    }




    public void setViewPrintSlip() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        slipView = inflater.inflate( R.layout.view_print_config, null);
            recyclerViewReportDetail = slipView.findViewById( R.id.recyclerViewReportDetail);
        slipLinearLayout = slipView.findViewById( R.id.slipLinearLayout);
            RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
            recyclerViewReportDetail.setLayoutManager(layoutManager1);




        recyclerViewReportDetail.setAdapter(null);
        printConfigAdapter = new printConfigAdapter(this);
        recyclerViewReportDetail.setAdapter(printConfigAdapter);

        if (nameList == null) {
            nameList = new ArrayList<>();
        } else {
            nameList.clear();
        }

        nameList.add("Merchant L1 :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MERCHANT_1));
        nameList.add("Merchant L2 :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MERCHANT_2));
        nameList.add("Merchant L3 :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MERCHANT_3));
        nameList.add("taxId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TAX_ID)+" posId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_POS_ID));
        nameList.add("qrAid :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_QR_AID)+" qrPort :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_QR_PORT));
        nameList.add("qrBilkey :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_BILLER_KEY)+" qrBilId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_QR_BILLER_ID));
        nameList.add("qrMerchantName :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_QR_MERCHANT_NAME));
        nameList.add("qrMerchantNameThai :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_QR_MERCHANT_NAME_THAI));
        nameList.add("qrTerminalId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_QR_TERMINAL_ID));
        nameList.add("qrMerchantId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_QR_MERCHANT_ID));

        nameList.add(" ");

        nameList.add("primaryIp :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_PRIMARY_IP)+" Port :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_PRIMARY_PORT));
        nameList.add("secondaryIp :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_SECONDARY_IP)+" Port :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_SECONDARY_PORT));

        nameList.add(" ");
        nameList.add("App_enable :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_APP_ENABLE)+" MAX_AMT :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MAX_AMT));
        nameList.add("Ali_enable :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_ALIPAY_ID));
        nameList.add("Weci_enable :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_WECHATPAY_ID));
        nameList.add("Rail_enable :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_RAILWAY_ID));
        nameList.add("App_GHC_enable :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_APP_GHC_ENABLE)+" Rs232_enable :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_RS232_Enable_ID)+" FixRate :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_FixRATE_ID));
        nameList.add("rateJCB :"+String.valueOf(Preference.getInstance(printConfig.this).getValueDouble( Preference.KEY_rateJCB_ID))+" rateUPI :"+String.valueOf(Preference.getInstance(printConfig.this).getValueDouble( Preference.KEY_rateUPI_ID))+" rateTPN :"+String.valueOf(Preference.getInstance(printConfig.this).getValueDouble( Preference.KEY_rateTPN_ID)));
        nameList.add("rateMC :"+String.valueOf(Preference.getInstance(printConfig.this).getValueDouble( Preference.KEY_rateMC_ID))+" rateVI :"+String.valueOf(Preference.getInstance(printConfig.this).getValueDouble( Preference.KEY_rateVI_ID))+" rateVILocal :"+String.valueOf(Preference.getInstance(printConfig.this).getValueDouble( Preference.KEY_rateVILocal_ID))+" rateMCLocal :"+String.valueOf(Preference.getInstance(printConfig.this).getValueDouble( Preference.KEY_rateMCLocal_ID)));

        nameList.add(" ");
        nameList.add("posTerminalId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TERMINAL_ID_POS));
        nameList.add("posMerchantId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MERCHANT_ID_POS));
        nameList.add("posTpdu :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TPDU_POS)+" posNii :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_NII_POS));

        nameList.add(" ");
        nameList.add("epsTerminalId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TERMINAL_ID_EPS));
        nameList.add("epsMerchantId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MERCHANT_ID_EPS));
        nameList.add("epsTpdu :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TPDU_EPS)+" epsNii :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_NII_EPS));

        nameList.add(" ");
        nameList.add("tmsTerminalId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TERMINAL_ID_TMS));
        nameList.add("tmsMerchantId :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MERCHANT_ID_TMS));
        nameList.add("tmsTpdu :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TPDU_TMS)+" tmsNii :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_NII_TMS));
        nameList.add("tmsTerminaversion :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TERMINAL_VERSION)+" tmsMsgVersion :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_MESSAGE_VERSION));

        nameList.add(" ");
        nameList.add("para_enable :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TAG_1000)+" para_com :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TAG_1001)+" para_ref1 :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TAG_1002));
        nameList.add("para_ref2 :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TAG_1003)+" para_ref3 :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_TAG_1004));

        //print spac------------------------------------------------------------------------------
        nameList.add(" ");
//        nameList.add("JSONBYPASS :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_JSONBYPASS_ID)+" KTBNORMAL :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_KTBNORMAL_ID)+" KEY_WAY4_ID :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_WAY4_ID)+" KEY_AXA_ID :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_AXA_ID)); // Paul_20181103 Add AXA
        nameList.add("JSONBYPASS :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_JSONBYPASS_ID)+" KTBNORMAL :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_KTBNORMAL_ID)+" KEY_AXA_ID :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_AXA_ID)); // Paul_20181103 Add AXA
        nameList.add("PRINTQR :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_PRINTQR_ID)+" CARDMASK :"+Preference.getInstance(printConfig.this).getValueString( Preference.KEY_CARDMASK_ID));

        //print space------------------------------------------------------------------------------
        nameList.add(" ");

        //Start card info------------------------------------------------------------------------------
        nameList.add("CARD RANGE:");

        File file = new File("/cache/customer/media/print_param.json");   //20180821 Joe centerm chang JSON file location
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;
        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardFee");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
                    String hostIndex = object3.getString("hostIndex");
                    String cardRange = object3.getString("cardRange");
                    String pinReq = object3.getString("pinReq");
                    String CardFee = object3.getString("cardFee");

//                    nameList.add("cardRange:"+cardRange+" cardName:"+cardName+" Host Index:"+hostIndex);
                    nameList.add(cardRange+" :"+cardName+" :"+hostIndex+ " PIN: "+pinReq+" ::"+CardFee);

                    String cardNo="2";
//                    String szCardNO;
//                    szCardNO = cardNo.substring(0, cardRange.length());
                    if (cardRange.equalsIgnoreCase(cardNo)) {   //"2" == "2"
                        break;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //END card info------------------------------------------------------------------------------
        //Start card info----------------------------------------------------------------------------
        nameList.add("\nCARD block:");

        File file1 = new File("/cache/customer/media/print_param.json");   //20180821 Joe centerm chang JSON file location
        String getDirectoryPath1 = String.valueOf(file1.length());
        FileInputStream stream1 = null;
        try {
            String jString = null;
            stream1 = new FileInputStream(file1);
            FileChannel fc = stream1.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardbacklist");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
                    String hostIndex = object3.getString("hostIndex");
                    String cardRange = object3.getString("cardRange");
                    String pinReq = object3.getString("pinReq");

//                    nameList.add("cardRange:"+cardRange+" cardName:"+cardName+" Host Index:"+hostIndex);
                    nameList.add(cardRange+" :"+cardName);

                    String cardNo="2";
//                    String szCardNO;
//                    szCardNO = cardNo.substring(0, cardRange.length());
                    if (cardRange.equalsIgnoreCase(cardNo)) {   //"2" == "2"
                        break;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream1 != null) {
                    stream1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //print space------------------------------------------------------------------------------
        nameList.add(" ");
        nameList.add(" ");


        printConfigAdapter.setItem(nameList);
        printConfigAdapter.notifyDataSetChanged();


        setMeasureSlip();

        System.out.printf("utility:: %s doPrinting Befor 078 \n",TAG);
        //doPrinting(getBitmapFromView(slipLinearLayout));
    }


    public void setItem(List<String> item) {
        if (nameList == null) {
            nameList = new ArrayList<>();
        }
        nameList = item;
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

    public void doPrinting(Bitmap slip) {
        bitmapOld = slip;
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    printDev.setPrinterGray(2);
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() {
//                            finish();
//                            overridePendingTransition(0, 0);
                            Intent intent = new Intent(printConfig.this , MenuServiceListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        @Override
                        public void onPrintError(int i) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    msgLabel.setText("เกิดข้อผิดพลาด เช่นแบตเตอรี่อ่อน");
                                    dialogOutOfPaper.show();
                                }
                            });
                        }

                        @Override
                        public void onPrintOutOfPaper() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogOutOfPaper.show();
                                }
                            });
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180919
        View view = dialogOutOfPaper.getLayoutInflater().inflate(R.layout.dialog_custom_printer, null);//K.GAME 180919
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180919
        dialogOutOfPaper.setContentView(view);//K.GAME 180919
        dialogOutOfPaper.setCancelable(false);//K.GAME 180919

//        dialogOutOfPaper = new Dialog(this);
//        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogOutOfPaper.setContentView( R.layout.dialog_custom_printer);
//        dialogOutOfPaper.setCancelable(false);
//        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById( R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById( R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.printf("utility:: %s doPrinting Befor 077 \n",TAG);
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }


    @Override
    public void onClick(View v) {

    }
}
