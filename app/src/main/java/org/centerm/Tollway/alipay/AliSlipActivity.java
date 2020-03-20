package org.centerm.Tollway.alipay;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.centerm.Tollway.BuildConfig;
import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.utility.Preference;

import java.text.DecimalFormat;

import io.realm.Realm;

//import org.centerm.Tollway.alipay.database.AliTemp;

public class AliSlipActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "AliSlipActivity";

    private Realm realm = null;
    private PrintLister printLister = null;
    private CountDownTimer timer = null;
    private  boolean printFlag = false;

    private CardManager cardManager = null;
    private LinearLayout linearLayoutSuccess = null;
    private LinearLayout linearLayoutFail = null;
    private LinearLayout linearLayoutInquiry = null;

    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private TextView txt_type = null;
    private TextView txt_buyerid = null;
    private TextView txt_amtcny = null;
    private TextView txt_rate = null;
    private TextView tidLabel = null;
    private TextView midLabel = null;
    private TextView traceLabel = null;
    private TextView batchLabel = null;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView amtThbLabel = null;        // Paul_20181006
    private TextView feeThbLabel = null;        // Paul_20181006
    private TextView totThbLabel = null;
    private TextView transidLabel = null;
    private TextView typeCardLabel = null;
    private ImageView qrCode = null;         //Jeff_20181008

    private TextView merchantName1LabelAuto = null;
    private TextView merchantName2LabelAuto = null;
    private TextView merchantName3LabelAuto = null;
    private TextView txt_typeAuto = null;
    private TextView txt_buyeridAuto = null;
    private TextView txt_amtcnyAuto = null;
    private TextView txt_rateAuto = null;
    private TextView tidLabelAuto = null;
    private TextView midLabelAuto = null;
    private TextView traceLabelAuto = null;
    private TextView batchAutoLabel = null;
    private TextView dateLabelAuto = null;
    private TextView timeLabelAuto = null;
    private TextView amtThbLabelAuto = null;        // Paul_20181006
    private TextView feeThbLabelAuto = null;        // Paul_20181006
    private TextView totThbLabelAuto = null;
    private TextView transidLabelAuto = null;
    private TextView typeCardLabelAuto = null;
    private ImageView qrCodeAuto = null;         //Jeff_20181008

    private TextView txt_posid = null;
    private TextView taxIdLabel = null;
    private TextView taxAbbLabel = null;
    private TextView traceTaxLabel = null;
    private TextView batchTaxLabel = null;
    private TextView dateTaxLabel = null;
    private TextView timeTaxLabel = null;
    private TextView feeTaxLabel = null;

    private TextView txt_posidAuto = null;
    private TextView taxIdLabelAuto = null;
    private TextView taxAbbLabelAuto = null;
    private TextView traceTaxLabelAuto = null;
    private TextView batchTaxLabelAuto = null;
    private TextView dateTaxLabelAuto = null;
    private TextView timeTaxLabelAuto = null;
    private TextView feeTaxLabelAuto = null;

    private Button btn_backmain;
    private Button btn_backmain2;
    private Button btn_backmain3;
    private Button btn_inquiry;
    private Button btn_print;

    private TextView txt_comment;
    private TextView txt_comment2;
    private TextView txt_subcomment;

    private AidlPrinter printDev = null;
    private LinearLayout slipLinearLayout; //Customer
    private LinearLayout slipLinearLayoutAuto; //Merchant
    private LinearLayout slipLinearLayoutTax;
    private LinearLayout slipLinearLayoutTaxAuto;

    private String status;
    private String invoice;
    private String tid;
    private String type;
    private String receipt;
    private String comment2;

    private String amt;
    private View printFirst;
    private View printSecond;
    private String batch;

    private Dialog dialogOutOfPaper;

    private Button okBtn;

    private Bitmap bitmapOld = null;
    private TextView msgLabel;

    private TextView name_sw_version;       // Paul_20190205 software version print
    private TextView name_sw_versionAuto;       // Paul_20190205 software version print

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_slip);
        initData();
        initWidget();

        printLister = new PrintLister() {
            @Override
            public void onEnd() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!printFlag) {
                            printFlag = true;
//                            btn_print.setBackgroundColor(getResources().getColor( R.color.color_SkyBlue_C90));
                            btn_print.setEnabled(true);
                            btn_backmain.setEnabled(true);
                        }else{
//                            btn_print.setBackgroundColor(getResources().getColor( R.color.color_gray));
                            btn_backmain.setEnabled(true);
                        }
                    }
                });
            }
        };
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            status = bundle.getString("STATUS");
            if(status.equals( AliConfig.Success)){
                invoice = bundle.getString("INVOICE");
                type = bundle.getString("TYPE");
                amt = bundle.getString("AMOUNT");
            }else if(status.equals( AliConfig.Fail)){
                comment2 = bundle.getString("COMMENT");
            }else{ //Hold
                invoice = bundle.getString("INVOICE");
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void initWidget() {

        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();

        initButton();

        if(status.equals( AliConfig.Success)){

            DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            amt = amt.replace(",", "");

            double doubleamt = 0.00;
            doubleamt = Double.parseDouble(decimalFormat.format(Double.valueOf(amt)));

            linearLayoutSuccess = findViewById( R.id.successLayout);
            linearLayoutSuccess.setVisibility(View.VISIBLE);

            System.out.printf("utility:: %s doubleamt = %f\n ",TAG,(float) (doubleamt));
            if((float) (doubleamt) > 0)         // Paul_20190201 if void success red color
                txt_subcomment.setTextColor(Color.parseColor("#38A1DB")); //ktb color
            else
                txt_subcomment.setTextColor(Color.parseColor("#CC3333")); //red
            txt_subcomment.setText(decimalFormatShow.format((float) (doubleamt)));
            setViewPrintFirst();
            setViewPrintSecond();
            customDialogOutOfPaper();
            btn_print.setEnabled(false);
            btn_backmain.setEnabled(false);

            startPrint();
        }else if(status.equals( AliConfig.Hold)){
            linearLayoutInquiry = findViewById( R.id.inquiryLayout);
            linearLayoutInquiry.setVisibility(View.VISIBLE);
            btn_backmain3 = findViewById( R.id.btn_backmain3);
            btn_inquiry = findViewById( R.id.btn_inquiry);
            btn_backmain3.setOnClickListener(this);
            btn_inquiry.setOnClickListener(this);
        }else {//Fail
            linearLayoutSuccess = findViewById( R.id.successLayout);
            linearLayoutFail = findViewById( R.id.failLayout);
            linearLayoutSuccess.setVisibility(View.GONE);
            linearLayoutFail.setVisibility(View.VISIBLE);
            txt_comment2.setText(comment2);
        }
    }

    private void initButton() {
        txt_subcomment = findViewById( R.id.txt_subcomment);
        txt_comment = findViewById( R.id.txt_comment); //Success Layout
        txt_comment2 = findViewById( R.id.txt_comment2); //Fail Layout
        btn_print = findViewById( R.id.btn_print);
        btn_print.setOnClickListener(this);
        btn_backmain = findViewById( R.id.btn_backmain);
        btn_backmain.setOnClickListener(this);
        btn_backmain2 = findViewById( R.id.btn_backmain2);
        btn_backmain2.setOnClickListener(this);
    }

    private void setViewPrintFirst() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printFirst = inflater.inflate( R.layout.view_alipay, null);

        slipLinearLayoutAuto = printFirst.findViewById( R.id.slipLinearLayout);
        slipLinearLayoutTaxAuto = printFirst.findViewById( R.id.taxLinearLayout);

        merchantName1LabelAuto = printFirst.findViewById( R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1LabelAuto.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1));

        merchantName2LabelAuto = printFirst.findViewById( R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2LabelAuto.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2));

        merchantName3LabelAuto = printFirst.findViewById( R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3LabelAuto.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3));

        txt_typeAuto = printFirst.findViewById( R.id.txt_type);
        txt_buyeridAuto =printFirst.findViewById( R.id.txt_buyerid);
        txt_amtcnyAuto =printFirst.findViewById( R.id.txt_amountcny);
        txt_rateAuto =printFirst.findViewById( R.id.txt_rate);
        transidLabelAuto = printFirst.findViewById(R.id.txt_transid);
        tidLabelAuto = printFirst.findViewById( R.id.tidLabel);
        midLabelAuto = printFirst.findViewById( R.id.midLabel);
        traceLabelAuto = printFirst.findViewById( R.id.traceLabel);
        batchAutoLabel = printFirst.findViewById( R.id.batchLabel);
        dateLabelAuto = printFirst.findViewById( R.id.dateLabel);
        timeLabelAuto = printFirst.findViewById( R.id.timeLabel);
//        feeThbLabel
        amtThbLabelAuto = printFirst.findViewById( R.id.amtThbLabel);    // Paul_20181006
        feeThbLabelAuto = printFirst.findViewById( R.id.feeThbLabel);    // Paul_20181006
        totThbLabelAuto = printFirst.findViewById( R.id.totThbLabel);
        typeCardLabelAuto = printFirst.findViewById( R.id.typeCardLabel);
        qrCodeAuto = printFirst.findViewById( R.id.img_qrCode);

        name_sw_versionAuto = printFirst.findViewById(R.id.name_sw_version);   // Paul_20190205 software version print

        //taxslip
        txt_posidAuto = printFirst.findViewById( R.id.txt_posid);
        taxIdLabelAuto = printFirst.findViewById( R.id.taxIdLabel);
        taxAbbLabelAuto = printFirst.findViewById( R.id.taxAbbLabel);
        traceTaxLabelAuto = printFirst.findViewById( R.id.traceTaxLabel);
        batchTaxLabelAuto = printFirst.findViewById( R.id.batchTaxLabel);
        dateTaxLabelAuto = printFirst.findViewById( R.id.dateTaxLabel);
        timeTaxLabelAuto = printFirst.findViewById( R.id.timeTaxLabel);
        feeTaxLabelAuto = printFirst.findViewById( R.id.feeTaxLabel);
    }

    private void setViewPrintSecond() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printSecond = inflater.inflate( R.layout.view_alipay2, null);

        slipLinearLayout = printSecond.findViewById( R.id.slipLinearLayout);
        slipLinearLayoutTax = printSecond.findViewById( R.id.taxLinearLayout);

        merchantName1Label = printSecond.findViewById( R.id.merchantName1Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_1));

        merchantName2Label = printSecond.findViewById( R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_2));

        merchantName3Label = printSecond.findViewById( R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText( Preference.getInstance(this).getValueString( Preference.KEY_MERCHANT_3));

        txt_type = printSecond.findViewById( R.id.txt_type);
        txt_buyerid =printSecond.findViewById( R.id.txt_buyerid);
        txt_amtcny =printSecond.findViewById( R.id.txt_amountcny);
        txt_rate =printSecond.findViewById( R.id.txt_rate);
        tidLabel = printSecond.findViewById( R.id.tidLabel);
        midLabel = printSecond.findViewById( R.id.midLabel);
        traceLabel = printSecond.findViewById( R.id.traceLabel);
        batchLabel = printSecond.findViewById( R.id.batchLabel);
        dateLabel = printSecond.findViewById( R.id.dateLabel);
        timeLabel = printSecond.findViewById( R.id.timeLabel);
        amtThbLabel = printSecond.findViewById( R.id.amtThbLabel);    // Paul_20181006
        feeThbLabel = printSecond.findViewById( R.id.feeThbLabel);    // Paul_20181006
        totThbLabel = printSecond.findViewById( R.id.totThbLabel);
        transidLabel = printSecond.findViewById(R.id.txt_transid);
        typeCardLabel = printSecond.findViewById( R.id.typeCardLabel);
        qrCode = printSecond.findViewById( R.id.img_qrCode);

        name_sw_version = printSecond.findViewById(R.id.name_sw_version);   // Paul_20190205 software version print

        //taxslip
        txt_posid = printSecond.findViewById( R.id.txt_posid);
        taxIdLabel = printSecond.findViewById( R.id.taxIdLabel);
        taxAbbLabel = printSecond.findViewById( R.id.taxAbbLabel);
        traceTaxLabel = printSecond.findViewById( R.id.traceTaxLabel);
        batchTaxLabel = printSecond.findViewById( R.id.batchTaxLabel);
        dateTaxLabel = printSecond.findViewById( R.id.dateTaxLabel);
        timeTaxLabel = printSecond.findViewById( R.id.timeTaxLabel);
        feeTaxLabel = printSecond.findViewById( R.id.feeTaxLabel);
    }

    private void startPrint() {
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            QrCode transTemp = realm.where(QrCode.class).equalTo("trace", invoice).findFirst();

            setDataView(transTemp); //Customer
            setDataViewAuto(transTemp); //Merchant
            } finally {
                if (realm != null) {
                    realm.close();
            }
            realm = null;
        }
    }

    private void setDataView(QrCode item) {

        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");

        System.out.printf("utility:: setDataView AAAAAAAAAAAAAAAAAAABBBBBBBBBBBB  item.getDeviceId() = %s ,item.getMerid() = %s , item.getTrace() = %s \n",item.getDeviceId(),item.getMerid(),item.getTrace());
        String hostType = item.getHostTypeCard();
        // Paul_20190325
//        txt_type.setText(hostType);
        if(hostType.equals("ALIPAY"))
        {
            txt_type.setText(hostType);
        } else {
            txt_type.setText(hostType + " PAY");
        }
        txt_buyerid.setText(item.getBuyerid());
        txt_amtcny.setText(item.getForamt() +" "+ item.getWalletcurr());
        txt_rate.setText("1 "+ item.getWalletcurr() +" = " + item.getConvrate() + " " + "THB");
        tid = item.getDeviceId();
        tidLabel.setText(tid);
        midLabel.setText(item.getMerid());    // Paul_20181001
        traceLabel.setText(item.getTrace());        // Paul_20181009
        if(hostType.equals("ALIPAY"))
            batch = Preference.getInstance(this).getValueString(Preference.KEY_ALI_BATCH_NUMBER);
        else
            batch = Preference.getInstance(this).getValueString(Preference.KEY_WEC_BATCH_NUMBER);
        batch = checkLength(batch, 6);
        batchLabel.setText(batch);
        transidLabel.setText(item.getWallettransid());
// Paul_20181009 Start
        double doubleamt=0.00;
        double doublefee=0.00;
        double doubleplusfee=0.00;
        System.out.printf("utility:: setDataView 000002 \n");
        doubleamt = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getAmt())));
        System.out.printf("utility:: setDataView 000003 item.getAmt() = %s \n",item.getAmt());
        if(!item.getFee().equals( "null" )) {
            System.out.printf("utility:: setDataView 000004 , item.getFee() = %s \n",item.getFee());
            doublefee = Double.parseDouble( decimalFormat.format( Double.valueOf( item.getFee() ) ) );
        }
        System.out.printf("utility:: setDataView 000005 \n");
        if(!item.getAmtplusfee().equals( "null" )) {
            System.out.printf( "utility:: setDataView 000006 item.getAmtplusfee() = %s \n",item.getAmtplusfee() );
            doubleplusfee = Double.parseDouble( decimalFormat.format( Double.valueOf( item.getAmtplusfee() ) ) );
        }
        else {
            System.out.printf( "utility:: setDataView 000007 \n" );
            doubleplusfee = doubleamt;
        }
// Paul_20181009 End

        String tmp = item.getReqChannelDtm();
        String date = tmp.substring(8, 10) +"/" + tmp.substring(5,7) +"/" + tmp.substring(2,4);
        String time = tmp.substring(11, tmp.length()-4);

        dateLabel.setText("DATE : " + date);
        timeLabel.setText("TIME : " + time);

        //QR code
        String custcode= item.getToken();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(custcode, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qr_code = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(qr_code);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        name_sw_version.setVisibility(View.VISIBLE);             // Paul_20190205 software version print
        name_sw_version.setText( BuildConfig.VERSION_NAME);      // Paul_20190205 software version print

        if(type.equals(AliConfig.Void)){
            typeCardLabel.setText("VOID");
            amtThbLabel.setText("-" + decimalFormatShow.format((float) (doubleamt)) + " THB");      // Paul_20181006
            feeThbLabel.setText("-"+decimalFormatShow.format((float) (doublefee)) + " THB");          // Paul_20181006
            totThbLabel.setText("-"+decimalFormatShow.format((float) (doubleplusfee)) + " THB");     // Paul_20181006
        }else{
            typeCardLabel.setText("SALE");
            amtThbLabel.setText("*" +decimalFormatShow.format((float) (doubleamt)) + " THB");        // Paul_20181006
            feeThbLabel.setText("*" +decimalFormatShow.format((float) (doublefee)) + " THB");          // Paul_20181006
            totThbLabel.setText("*" +decimalFormatShow.format((float) (doubleplusfee)) + " THB");      // Paul_20181006
        }

        //TAXSLIP
        // Paul_20190313 Void no TAX slip
        if(!item.getFee().equals( "null" ) && !type.equals(AliConfig.Void)) {
            String TAX_ABB_NEW = tid + invoice;
            slipLinearLayoutTax.setVisibility(View.VISIBLE);
            txt_posid.setText("POS ID : " + Preference.getInstance(this).getValueString( Preference.KEY_POS_ID));
            taxIdLabel.setText(Preference.getInstance(this).getValueString( Preference.KEY_TAX_ID));
            taxAbbLabel.setText(TAX_ABB_NEW);
            traceTaxLabel.setText(invoice);
            batchTaxLabel.setText(batch);
            dateTaxLabel.setText(date);
            timeTaxLabel.setText(time);
            if(type.equals( AliConfig.Void))
                feeTaxLabel.setText("-" +decimalFormatShow.format((float) (doublefee)) + " THB");
            else{
                feeTaxLabel.setText("*" +decimalFormatShow.format((float) (doublefee)) + " THB");
            }
        }
    }

    private void setDataViewAuto(QrCode item) {
        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");

        System.out.printf("utility:: setDataViewAuto AAAAAAAAAAAAAAAAAAABBBBBBBBBBBB  item.getDeviceId() = %s ,item.getMerid() = %s , item.getTrace() = %s \n",item.getDeviceId(),item.getMerid(),item.getTrace());

        String hostType = item.getHostTypeCard();
//        txt_typeAuto.setText(hostType);
        if(hostType.equals("ALIPAY"))
        {
            txt_typeAuto.setText(hostType);
        } else {
            txt_typeAuto.setText(hostType + " PAY");
        }
        txt_buyeridAuto.setText(item.getBuyerid());
        txt_amtcnyAuto.setText(item.getForamt() +" "+ item.getWalletcurr());
        txt_rateAuto.setText("1 "+ item.getWalletcurr() +" = " + item.getConvrate() + " " + "THB");
        tidLabelAuto.setText(item.getDeviceId());
        midLabelAuto.setText(item.getMerid());  // Paul_20181001
        traceLabelAuto.setText(item.getTrace());
        if(hostType.equals("ALIPAY"))
            batch = Preference.getInstance(this).getValueString(Preference.KEY_ALI_BATCH_NUMBER);
        else
            batch = Preference.getInstance(this).getValueString(Preference.KEY_WEC_BATCH_NUMBER);
        batch = checkLength(batch, 6);
        batchAutoLabel.setText(batch);
        transidLabelAuto.setText(item.getWallettransid());
// Paul_20181009 Start
        double doubleamt=0.00;
        double doublefee=0.00;
        double doubleplusfee=0.00;
        doubleamt = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getAmt())));
        System.out.printf("utility:: setDataViewAuto 000002 \n");
        if(!item.getFee().equals( "null" )) {
            System.out.printf("utility:: setDataViewAuto 000003 \n");
            doublefee = Double.parseDouble( decimalFormat.format( Double.valueOf( item.getFee() ) ) );
        }
        System.out.printf("utility:: setDataViewAuto 000004 \n");
        if(!item.getAmtplusfee().equals( "null" ))
            doubleplusfee = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getAmtplusfee())));
        else
            doubleplusfee = doubleamt;
// Paul_20181009 End

        String tmp = item.getReqChannelDtm();
        String date = tmp.substring(8, 10) +"/" + tmp.substring(5,7) +"/" + tmp.substring(2,4);
        String time = tmp.substring(11, tmp.length()-4);

        dateLabelAuto.setText("DATE : " + date);
        timeLabelAuto.setText("TIME : " + time);

        //QR code
        String custcode= item.getToken();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(custcode, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qr_code = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeAuto.setImageBitmap(qr_code);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        name_sw_versionAuto.setVisibility(View.VISIBLE);             // Paul_20190205 software version print
        name_sw_versionAuto.setText( BuildConfig.VERSION_NAME);      // Paul_20190205 software version print

        if(type.equals( AliConfig.Void)){
            typeCardLabelAuto.setText("VOID");
            amtThbLabelAuto.setText("-" + decimalFormatShow.format((float) (doubleamt)) + " THB");     // Paul_20181006
            feeThbLabelAuto.setText("-"+decimalFormatShow.format((float) (doublefee)) + " THB");         // Paul_20181006
            totThbLabelAuto.setText("-"+decimalFormatShow.format((float) (doubleplusfee)) + " THB");     // Paul_20181006
        }else{
            typeCardLabelAuto.setText("SALE");
            amtThbLabelAuto.setText("*" +decimalFormatShow.format((float) (doubleamt)) + " THB");       // Paul_20181006
            feeThbLabelAuto.setText("*" +decimalFormatShow.format((float) (doublefee)) + " THB");         // Paul_20181006
            totThbLabelAuto.setText("*" +decimalFormatShow.format((float) (doubleplusfee)) + " THB");     // Paul_20181006
        }

        //TAXSLIP
        // Paul_20190313 Void no TAX slip
        if(!item.getFee().equals( "null" ) && !type.equals(AliConfig.Void)) {
            String TAX_ABB_NEW = tid + invoice;
            slipLinearLayoutTaxAuto.setVisibility(View.VISIBLE);
            txt_posidAuto.setText("POS ID : " + Preference.getInstance(this).getValueString( Preference.KEY_POS_ID));
            taxIdLabelAuto.setText(Preference.getInstance(this).getValueString( Preference.KEY_TAX_ID));
            taxAbbLabelAuto.setText(TAX_ABB_NEW);
            traceTaxLabelAuto.setText(invoice);
            batchTaxLabelAuto.setText(batch);
            dateTaxLabelAuto.setText(date);
            timeTaxLabelAuto.setText(time);
            if(type.equals( AliConfig.Void))
                feeTaxLabelAuto.setText("-" +decimalFormatShow.format((float) (doublefee)) + " THB");
            else{
                feeTaxLabelAuto.setText("*" +decimalFormatShow.format((float) (doublefee)) + " THB");
            }
        }
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

    private void autoPrint() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                //doPrinting(getBitmapFromView(slipLinearLayout));
//                //doPrinting(getBitmapFromView(slipLinearLayoutAuto));
            }
        };
        timer.start();
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
//                            txt_comment.setText("** Complete **");
                            printLister.onEnd();
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

        printSecond.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printSecond.layout(0, 0, printSecond.getMeasuredWidth(), printSecond.getMeasuredHeight());
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
        if (v == btn_print) {
            autoPrint();
            btn_print.setEnabled(false);
            btn_backmain.setEnabled(false);
            btn_print.setBackgroundResource( R.color.color_gray);
        }else if(v == btn_inquiry){
//            String invoice;
            String walletcode;


            try {
                if (realm == null) {
                    realm = Realm.getDefaultInstance();
                }

                Number maxId  = realm.where(QrCode.class).max("id");

                if(maxId != null){
//                    QrCode aliTemp = realm.where(QrCode.class).equalTo("id", maxId.intValue()).findFirst();
                    invoice = checkLength(invoice, 6);
                    QrCode aliTemp = realm.where(QrCode.class).equalTo("trace", invoice).findFirst();
                    if(aliTemp != null) {
//                        invoice = aliTemp.getTrace();
                        walletcode = aliTemp.getWalletcode();
                        Intent inquiry = new Intent( AliSlipActivity.this, AliServiceActivity.class );
                        inquiry.putExtra( "TYPE", AliConfig.Inquiry );
                        inquiry.putExtra( "INVOICE", invoice );
                        inquiry.putExtra("WALLET_CODE", walletcode);
                        startActivity( inquiry );
                        finish();
                    }
                }else
                    Toast.makeText(AliSlipActivity.this,  "No Transaction . . .", Toast.LENGTH_SHORT).show();


            } finally {
                if (realm != null) {
                    realm.close();
                }
                realm = null;
            }

        }else {
            finish();
        }
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

    interface PrintLister {
        public void onEnd();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(AliSlipActivity.this,  "No Transaction . . .", Toast.LENGTH_SHORT).show();
    }

    private String checkLength(String trace, int i ) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for(int j = 0; j<(i-tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }
}
