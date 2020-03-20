package org.centerm.Tollway.alipay;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AliSettleSlipActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "SlipTemplateActivity";

    private Context context = null;
    private CountDownTimer timer = null;
    private  boolean printFlag = false;

    private CardManager cardManager = null;
    private LinearLayout linearLayoutSuccess = null;
    private LinearLayout linearLayoutFail = null;
    private LinearLayout linearLayoutInquiry = null;

    private TextView merchantName1LabelAuto = null;
    private TextView merchantName2LabelAuto = null;
    private TextView merchantName3LabelAuto = null;
    private TextView tidLabelAuto = null;
    private TextView midLabelAuto = null;
    private TextView traceLabelAuto = null;
    private TextView dateLabelAuto = null;
    private TextView timeLabelAuto = null;
    private TextView txt_cntsale = null;
    private TextView txt_cntvoid =null;
    private TextView txt_amtsale =null;
    private TextView txt_amtvoid = null;
//    private Button btn_backmain;          // Paul_20181009
//    private Button btn_backmain2;

    private AidlPrinter printDev = null;
    private LinearLayout slipLinearLayoutAuto; //Merchant

    private String status;
    private String invoice;
    private String cntSale;
    private String cntVoid;
    private String amountSale;
    private String amountVoid;
    private String type;
    private String receipt;
    private String amt;
    private View printFirst;
    private View printSecond;

    private Dialog dialogOutOfPaper;
    private Button okBtn;

    private Bitmap bitmapOld = null;
    private TextView msgLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_slip_settlement);
        initData();
        initWidget();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            status = bundle.getString("STATUS");
            if(status.equals(AliConfig.Success)){
                cntSale = bundle.getString("SALECNT");
                cntVoid = bundle.getString("VOIDCNT");
                amountSale = bundle.getString("SALEAMOUNT");
                amountVoid = bundle.getString("VOIDAMOUNT");
            }
        }
    }

    @Override
    public void initWidget() {
        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        invoice = CardPrefix.getInvoice(context, "ALIPAY");
        invoice = checkLength(invoice, 6);
        initButton();

        if(status.equals(AliConfig.Success)){
            invoiceUp();
            linearLayoutSuccess = findViewById( R.id.successLayout);
            linearLayoutSuccess.setVisibility(View.VISIBLE);
            setViewPrintFirst();
            customDialogOutOfPaper();
//            btn_backmain.setEnabled(false);
            startPrint();
        }else{

            linearLayoutFail = findViewById( R.id.failLayout);
            linearLayoutFail.setVisibility(View.VISIBLE);
        }
    }

    private void invoiceUp() {
        int inV = Integer.parseInt( Preference.getInstance(context).getValueString( Preference.KEY_INVOICE_NUMBER_ALL));
        inV = inV + 1;
        Preference.getInstance(context).setValueString( Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
    }

    private void initButton() {
//        btn_backmain = findViewById( R.id.btn_backmain);
//        btn_backmain2 = findViewById( R.id.btn_backmain2);
//        btn_backmain.setOnClickListener(this);
//        btn_backmain2.setOnClickListener(this);
    }


    private void setViewPrintFirst() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printFirst = inflater.inflate( R.layout.view_alipay_settlement, null);

        slipLinearLayoutAuto = printFirst.findViewById( R.id.slipLinearLayout);
        merchantName1LabelAuto = printFirst.findViewById( R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1LabelAuto.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1));

        merchantName2LabelAuto = printFirst.findViewById( R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2LabelAuto.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2));

        merchantName3LabelAuto = printFirst.findViewById( R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3LabelAuto.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3));

        tidLabelAuto = printFirst.findViewById( R.id.tidLabel);
        midLabelAuto = printFirst.findViewById( R.id.midLabel);
        traceLabelAuto = printFirst.findViewById( R.id.traceLabel);
        dateLabelAuto = printFirst.findViewById( R.id.dateLabel);
        timeLabelAuto = printFirst.findViewById( R.id.timeLabel);
        txt_amtsale = printFirst.findViewById( R.id.txt_amtsale);
        txt_amtvoid = printFirst.findViewById( R.id.txt_amtvoid);
        txt_cntsale = printFirst.findViewById( R.id.txt_cntsale);
        txt_cntvoid = printFirst.findViewById( R.id.txt_cntvoid);
    }

    private void startPrint() {
        setDataViewAuto(); //Merchant
    }

    private void setDataViewAuto() {

//        tidLabelAuto.setText(AliConfig.getDeviceId());
//        midLabelAuto.setText(AliConfig.getStoreId());
        tidLabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));  // Paul_20181007
        midLabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_STORE_ID));   // Paul_20181007
        traceLabelAuto.setText(invoice);

        Date date = new Date();
        dateLabelAuto.setText("DATE : " + new SimpleDateFormat("dd/MM/yy").format(date));
        timeLabelAuto.setText("TIME : " + new SimpleDateFormat("HH:mm:ss").format(date));
        txt_cntsale.setText(cntSale);
        txt_cntvoid.setText(cntVoid);

        DecimalFormat nf = new DecimalFormat("#,##0.00");

        txt_amtsale.setText(nf.format(Double.parseDouble(amountSale)));
        txt_amtvoid.setText( nf.format(Double.parseDouble(amountVoid)));

        setMeasure();

        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                //doPrinting(getBitmapFromView(slipLinearLayoutAuto));
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void doPrinting(Bitmap slip) {
        bitmapOld = slip;

        new Thread() {
            @Override
            public void run() {
                try {

                    printDev.initPrinter();
                    printDev.setPrinterGray(3);
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Log.d(TAG, "onPrintFinish: ");
                            finish();   // Paul_20181009
//                            btn_backmain.setEnabled(true);    // Paul_20181009
                        }

                        @Override
                        public void onPrintError(int i) throws RemoteException {
                            Log.d(TAG, "onPrintError: ");
                            msgLabel.setText("เกิดข้อผิดพลาด");
                            dialogOutOfPaper.show();
                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {
                            Log.d(TAG, "onPrintOutOfPaper: ");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogOutOfPaper.show();
                                }
                            });
                        }
                    });

                    int ret = printDev.printBarCodeSync("asdasd");
                    Log.d(TAG, "after call printData ret = " + ret);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setMeasure() {
        printFirst.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printFirst.layout(0, 0, printFirst.getMeasuredWidth(), printFirst.getMeasuredHeight());
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
    public void onClick(View v) {
        finish();
    }

    private String checkLength(String trace, int i ) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for(int j = 0; j<(i-tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }

    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this);
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOutOfPaper.setContentView( R.layout.dialog_custom_printer);
        dialogOutOfPaper.setCancelable(false);
        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById( R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById( R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
