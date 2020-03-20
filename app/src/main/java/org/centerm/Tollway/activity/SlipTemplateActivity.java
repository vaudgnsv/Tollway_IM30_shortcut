package org.centerm.Tollway.activity;

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
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;

import org.centerm.Tollway.BuildConfig;
import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;
import java.util.Date;

import io.realm.Realm;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;

public class SlipTemplateActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "SlipTemplateActivity";

    private Realm realm = null;

    private CountDownTimer timer = null;

    /**
     * Slip
     */
    private ImageView bankImage = null;
    private ImageView bank1Image = null;
    private TextView merchantName1Label = null;
    private TextView merchantName2Label = null;
    private TextView merchantName3Label = null;
    private TextView tidLabel = null;
    private TextView midLabel = null;
    private TextView traceLabel = null;
    //    private TextView systrcLabel = null;
    private TextView batchLabel = null;
    private TextView refNoLabel = null;
    private TextView dateLabel = null;
    private TextView timeLabel = null;
    private TextView typeLabel = null;
    private TextView typeCardLabel = null;
    private TextView cardNoLabel = null;
    private TextView apprCodeLabel = null;
    private TextView comCodeLabel = null;
    private TextView amtThbLabel = null;
    private TextView feeThbLabel = null;
    private TextView totThbLabel = null;
    private TextView ref1Label = null;
    private TextView ref2Label = null;
    private TextView ref3Label = null;
    private RelativeLayout ref1RelativeLayout = null;
    private RelativeLayout ref2RelativeLayout = null;
    private RelativeLayout ref3RelativeLayout = null;

    private TextView taxIdLayout = null;
    private TextView taxAbbLayout = null;
    private TextView traceTaxLayout = null;
    private TextView batchTaxLayout = null;
    private TextView dateTaxLayout = null;
    private TextView timeTaxLayout = null;
    private TextView feeTaxLayout = null;

    //    private TextView appLabel = null;
//    private TextView tcLabel = null;
//    private TextView aidLabel = null;
    private TextView nameEmvCardLabel = null;

    private FrameLayout appFrameLabel = null;
    private FrameLayout tcFrameLayout = null;
    private FrameLayout aidFrameLayout = null;
    private LinearLayout taxLinearLayout = null;
    private TextView copyLabel = null;
    private TextView typeInputCardLabel = null;
    /**
     * Slip Auto
     */
    private ImageView bankImageAuto = null;
    private ImageView bank1ImageAuto = null;
    private TextView merchantName1LabelAuto = null;
    private TextView merchantName2LabelAuto = null;
    private TextView merchantName3LabelAuto = null;
    private TextView tidLabelAuto = null;
    private TextView midLabelAuto = null;
    private TextView traceLabelAuto = null;
    private TextView systrcLabelAuto = null;
    private TextView batchLabelAuto = null;
    private TextView refNoLabelAuto = null;
    private TextView dateLabelAuto = null;
    private TextView timeLabelAuto = null;
    private TextView typeLabelAuto = null;
    private TextView typeCardLabelAuto = null;
    private TextView cardNoLabelAuto = null;
    private TextView apprCodeLabelAuto = null;
    private TextView comCodeLabelAuto = null;
    private TextView amtThbLabelAuto = null;
    private TextView feeThbLabelAuto = null;
    private TextView totThbLabelAuto = null;
    private TextView ref1LabelAuto = null;
    private TextView ref2LabelAuto = null;
    private TextView ref3LabelAuto = null;
    private RelativeLayout ref1RelativeLayoutAuto = null;
    private RelativeLayout ref2RelativeLayoutAuto = null;
    private RelativeLayout ref3RelativeLayoutAuto = null;
    private TextView taxIdLayoutAuto = null;
    private TextView taxAbbLayoutAuto = null;
    private TextView traceTaxLayoutAuto = null;
    private TextView batchTaxLayoutAuto = null;
    private TextView dateTaxLayoutAuto = null;
    private TextView timeTaxLayoutAuto = null;
    private TextView feeTaxLayoutAuto = null;

    private TextView appLabelAuto = null;
    private TextView tcLabelAuto = null;
    private TextView aidLabelAuto = null;
    private TextView nameEmvCardLabelAuto = null;


    private FrameLayout appFrameLabelAuto = null;
    private FrameLayout tcFrameLayoutAuto = null;
    private FrameLayout aidFrameLayoutAuto = null;
    private LinearLayout taxLinearLayoutAuto = null;
    private TextView copyLabelAuto = null;

    private TextView typeInputCardLabelAuto = null;

    private Button printBtn;
    private AidlPrinter printDev = null;
    private AidlDeviceManager manager = null;
    private NestedScrollView slipNestedScrollView;
    private NestedScrollView slipNestedScrollViewAuto;
    private LinearLayout slipLinearLayout;
    private LinearLayout slipLinearLayoutAuto;
    private CardManager cardManager = null;

    private int saleId;
    private String typeSlip;
    private View printFirst;

    private String app, tc, aid;

    private boolean statusOutScress = false;

    private AidlPrinterStateChangeListener.Stub callBackPrint = null;
    private Dialog dialogOutOfPaper;
    private Dialog dialogSuccess_GotoMain;

    private Button okBtn;
    private Button btn_gotoMain;

    private Bitmap bitmapOld = null;
    private TextView msgLabel;
    private View texView;
    private View texViewAuto;
    private FrameLayout comCodeFragmentAuto;
    private FrameLayout comCodeFragment;


    private FrameLayout fee_thb;    //SINN 20181031 merge KTBNORMAL again.
    private FrameLayout tot_thb;     //SINN 20181031 merge KTBNORMAL again.

    /**
     * Interface   RS232 SINN 20180709
     */
    private String typeInterface;
    private PosInterfaceActivity posInterfaceActivity;
    private String szCardForPOS;
    //end RS232 SINN 20180709
//K.GAME 181003 New UI confirm print
    private LinearLayout linear_username;
    private TextView dialogTitleLabel;
    private TextView tv_LabelBaht;
    private TextView priceLabel;
    private TextView tv_cardNumberLabel;
    private TextView tv_confirm_username;
    private TextView tv_confirm_date;
    private TextView tv_confirm_time;
    private TextView tv_confirm_traceNo;
    private TextView tv_confirm_status;
    private TextView tv_confirm_terminalId;
    private TextView tv_confirm_apprCode;
    private TextView tv_confirm_merchantId;
    private TextView tv_confirm_BatchNo;
    //    private TextView tv_confirm_comCode;
    private TextView tv_confirm_comCodetxt;  //20181104 SINN Dialog print
    private TextView tv_confirm_ref1txt;  //20181104 SINN Dialog print
    private TextView tv_confirm_ref2txt;  //20181104 SINN Dialog print
    private TextView tv_confirm_ref3txt;  //20181104 SINN Dialog print

    private TextView name_sw_version;  // Paul_20190125 software version print
    private TextView name_sw_version_Auto;  // Paul_20190125 software version print

    private TextView sigatureLabelAuto;

    private LinearLayout linear_comCode;

    private LinearLayout linear_ref1;
    private LinearLayout linear_ref2;
    private LinearLayout linear_ref3;

    private String printslip = "";  // //20181218  SINN Print slip enable/disable

    private Integer inAutoprint = 0;   //20181218  SINN Print slip enable/disable

    private String Card_holder = "";
    //END K.GAME 181003 New UI confirm print
    //K.GAME 180918
    private Dialog dialogWaiting;
//    private Dialog dialogSuccess_GotoMain;

    //END K.GAME 180918
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip_template);   // activity_slip_template.xml
        inAutoprint = 0;
        printslip = Preference.getInstance(this).getValueString(Preference.KEY_PrintSlip_ID);


        initData();
        initWidget();
//        initBtnExit();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            saleId = bundle.getInt(CalculatePriceActivity.KEY_CALCUATE_ID);
            typeSlip = bundle.getString(CalculatePriceActivity.KEY_TYPE_SALE_OR_VOID);
            Card_holder = bundle.getString(CalculatePriceActivity.KEY_INTERFACE_CARDHOLDER_2);//K.GAME 181003
            //sinn rs232 normal sale 20180709
            Log.d(TAG, "KEY_TYPE_INTERFACE :" + KEY_TYPE_INTERFACE);
            if (bundle.getString(KEY_TYPE_INTERFACE) != null) {
                if (Preference.getInstance(this).getValueString(Preference.KEY_RS232_FLAG).equals(1))
                    typeInterface = bundle.getString(MenuServiceListActivity.KEY_TYPE_INTERFACE);
            }
            //end sinn rs232 normal sale 20180709

            if (bundle.getString(MenuServiceListActivity.KEY_INTERFACE_APP) != null) {
                app = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_APP);
            }

            if (bundle.getString(MenuServiceListActivity.KEY_INTERFACE_TC) != null) {
                tc = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_TC);
            }

            if (bundle.getString(MenuServiceListActivity.KEY_INTERFACE_AID) != null) {
                aid = bundle.getString(MenuServiceListActivity.KEY_INTERFACE_AID);
            }

        }
        posInterfaceActivity = MainApplication.getPosInterfaceActivity();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        cardManager = MainApplication.getCardManager();
        cardManager.abortPBOCProcess();


        printDev = cardManager.getInstancesPrint();

        setViewPrintFirst();
        customDialogOutOfPaper();
        dialogSuccess_GotoMain();


        printBtn = findViewById(R.id.printBtn);
        slipNestedScrollView = findViewById(R.id.slipNestedScrollView);
        slipLinearLayout = findViewById(R.id.slipLinearLayout);   //view_report_sale.xml    //merchant slip
        bankImage = findViewById(R.id.bankImage);
        bank1Image = findViewById(R.id.bank1Image);
        merchantName1Label = findViewById(R.id.merchantName1Label);

        texView = findViewById(R.id.texView);

        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2Label = findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3Label = findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3Label.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        tidLabel = findViewById(R.id.tidLabel);
        midLabel = findViewById(R.id.midLabel);
        traceLabel = findViewById(R.id.traceLabel);
//        systrcLabel = findViewById(R.id.systrcLabel);
        batchLabel = findViewById(R.id.batchLabel);
        refNoLabel = findViewById(R.id.refNoLabel);
        dateLabel = findViewById(R.id.dateLabel);
        timeLabel = findViewById(R.id.timeLabel);
        typeLabel = findViewById(R.id.typeLabel);
        typeCardLabel = findViewById(R.id.typeCardLabel);
        cardNoLabel = findViewById(R.id.cardNoLabel);
        apprCodeLabel = findViewById(R.id.apprCodeLabel);
        comCodeLabel = findViewById(R.id.comCodeLabel);
        amtThbLabel = findViewById(R.id.amtThbLabel);
        feeThbLabel = findViewById(R.id.feeThbLabel);
        totThbLabel = findViewById(R.id.totThbLabel);
        ref1Label = findViewById(R.id.ref1Label);
        ref2Label = findViewById(R.id.ref2Label);
        ref3Label = findViewById(R.id.ref3Label);
        ref1RelativeLayout = findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayout = findViewById(R.id.ref2RelativeLayout);
        ref3RelativeLayout = findViewById(R.id.ref3RelativeLayout);

        taxIdLayout = findViewById(R.id.taxIdLabel);
        taxAbbLayout = findViewById(R.id.taxAbbLabel);
        traceTaxLayout = findViewById(R.id.traceTaxLabel);
        batchTaxLayout = findViewById(R.id.batchTaxLabel);
        dateTaxLayout = findViewById(R.id.dateTaxLabel);
        timeTaxLayout = findViewById(R.id.timeTaxLabel);
        feeTaxLayout = findViewById(R.id.feeTaxLabel);

//        appLabel = findViewById(R.id.appLabel);
//        tcLabel = findViewById(R.id.tcLabel);
//        aidLabel = findViewById(R.id.aidLabel);
        nameEmvCardLabel = findViewById(R.id.nameEmvCardLabel);

        appFrameLabel = findViewById(R.id.appFrameLabel);
        tcFrameLayout = findViewById(R.id.tcFrameLayout);
        aidFrameLayout = findViewById(R.id.aidFrameLayout);
        taxLinearLayout = findViewById(R.id.taxLinearLayout);   // <--------------------------- tax
        copyLabel = findViewById(R.id.copyLabel);
        typeInputCardLabel = findViewById(R.id.typeInputCardLabel);
        comCodeFragment = findViewById(R.id.comCodeFragment);


        //K.GAME 181003 New UI confirm print
        linear_username = findViewById(R.id.linear_username);
        dialogTitleLabel = findViewById(R.id.dialogTitleLabel);
        tv_LabelBaht = findViewById(R.id.tv_LabelBaht);
        priceLabel = findViewById(R.id.priceLabel);
        tv_cardNumberLabel = findViewById(R.id.tv_cardNumberLabel);
        tv_confirm_username = findViewById(R.id.tv_confirm_username);
        tv_confirm_date = findViewById(R.id.tv_confirm_date);
        tv_confirm_time = findViewById(R.id.tv_confirm_time);
        tv_confirm_traceNo = findViewById(R.id.tv_confirm_traceNo);
        tv_confirm_status = findViewById(R.id.tv_confirm_status);
        tv_confirm_terminalId = findViewById(R.id.tv_confirm_terminalId);
        tv_confirm_apprCode = findViewById(R.id.tv_confirm_apprCode);
        tv_confirm_merchantId = findViewById(R.id.tv_confirm_merchantId);
        tv_confirm_BatchNo = findViewById(R.id.tv_confirm_BatchNo);
//        tv_confirm_comCode = findViewById(R.id.tv_confirm_comCode);
        tv_confirm_comCodetxt = findViewById(R.id.tv_confirm_comCodetxt);
        linear_comCode = findViewById(R.id.linear_comCode);
        //END K.GAME 181003 New UI confirm print

        ////20181104 SINN Dialog print
        linear_ref1 = findViewById(R.id.linear_ref1);
        linear_ref2 = findViewById(R.id.linear_ref2);
        linear_ref3 = findViewById(R.id.linear_ref3);
        tv_confirm_ref1txt = findViewById(R.id.tv_confirm_ref1txt);
        tv_confirm_ref2txt = findViewById(R.id.tv_confirm_ref2txt);
        tv_confirm_ref3txt = findViewById(R.id.tv_confirm_ref3txt);
        //END 20181104 SINN Dialog print

        name_sw_version = findViewById(R.id.name_sw_version);   // Paul_20190125 software version print
        printBtn.setOnClickListener(this);
        printBtn.setEnabled(false);
        selectSALE();
    }

    private void customDialogWaiting(String msg) {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        msgLabel = dialogWaiting.findViewById(R.id.msgLabel); //K.GAME 180831 chang waitting UI
//        msgLabel.setText("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");//K.GAME 180831 chang waitting UI
        if (msg != null) {
            msgLabel.setText(msg);
        }
        Utility.animation_Waiting_new(dialogWaiting);//K.GAME 180831 chang waitting UI
        //END K.GAME 180831 chang waitting UI
    }

    private void setViewPrintFirst() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        printFirst = inflater.inflate(R.layout.view_report_sale, null);     //view_report_sale.xml

        slipNestedScrollViewAuto = printFirst.findViewById(R.id.slipNestedScrollView);
        slipLinearLayoutAuto = printFirst.findViewById(R.id.slipLinearLayout);
        bankImageAuto = printFirst.findViewById(R.id.bankImage);

        if (Preference.getInstance(this).getValueString(Preference.KEY_RCPT_LOGO_ID).toString().equalsIgnoreCase("1"))   //20180810 SINN Add multilogo
        {
            int id = getResources().getIdentifier("org.centerm.Tollway.activity:drawable/" + "logo_ktb1", null, null);
            bankImageAuto.setImageResource(id);
        }

        bank1ImageAuto = printFirst.findViewById(R.id.bank1Image);
        merchantName1LabelAuto = printFirst.findViewById(R.id.merchantName1Label);
        texViewAuto = printFirst.findViewById(R.id.texView);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            merchantName1LabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));

        merchantName2LabelAuto = printFirst.findViewById(R.id.merchantName2Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2).isEmpty())
            merchantName2LabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_2));

        merchantName3LabelAuto = printFirst.findViewById(R.id.merchantName3Label);
        if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3).isEmpty())
            merchantName3LabelAuto.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_3));

        tidLabelAuto = printFirst.findViewById(R.id.tidLabel);
        midLabelAuto = printFirst.findViewById(R.id.midLabel);
        traceLabelAuto = printFirst.findViewById(R.id.traceLabel);
        systrcLabelAuto = printFirst.findViewById(R.id.systrcLabel);
        batchLabelAuto = printFirst.findViewById(R.id.batchLabel);
        refNoLabelAuto = printFirst.findViewById(R.id.refNoLabel);
        dateLabelAuto = printFirst.findViewById(R.id.dateLabel);
        timeLabelAuto = printFirst.findViewById(R.id.timeLabel);
        typeLabelAuto = printFirst.findViewById(R.id.typeLabel);
        typeCardLabelAuto = printFirst.findViewById(R.id.typeCardLabel);
        cardNoLabelAuto = printFirst.findViewById(R.id.cardNoLabel);
        apprCodeLabelAuto = printFirst.findViewById(R.id.apprCodeLabel);
        comCodeLabelAuto = printFirst.findViewById(R.id.comCodeLabel);
        amtThbLabelAuto = printFirst.findViewById(R.id.amtThbLabel);
        feeThbLabelAuto = printFirst.findViewById(R.id.feeThbLabel);
        totThbLabelAuto = printFirst.findViewById(R.id.totThbLabel);
        ref1LabelAuto = printFirst.findViewById(R.id.ref1Label);
        ref2LabelAuto = printFirst.findViewById(R.id.ref2Label);
        ref3LabelAuto = printFirst.findViewById(R.id.ref3Label);
        ref1RelativeLayoutAuto = printFirst.findViewById(R.id.ref1RelativeLayout);
        ref2RelativeLayoutAuto = printFirst.findViewById(R.id.ref2RelativeLayout);
        ref3RelativeLayoutAuto = printFirst.findViewById(R.id.ref3RelativeLayout);

        taxIdLayoutAuto = printFirst.findViewById(R.id.taxIdLabel);
        taxAbbLayoutAuto = printFirst.findViewById(R.id.taxAbbLabel);
        traceTaxLayoutAuto = printFirst.findViewById(R.id.traceTaxLabel);
        batchTaxLayoutAuto = printFirst.findViewById(R.id.batchTaxLabel);
        dateTaxLayoutAuto = printFirst.findViewById(R.id.dateTaxLabel);
        timeTaxLayoutAuto = printFirst.findViewById(R.id.timeTaxLabel);
        feeTaxLayoutAuto = printFirst.findViewById(R.id.feeTaxLabel);


        appLabelAuto = printFirst.findViewById(R.id.appLabel);
        tcLabelAuto = printFirst.findViewById(R.id.tcLabel);
        aidLabelAuto = printFirst.findViewById(R.id.aidLabel);
        nameEmvCardLabelAuto = printFirst.findViewById(R.id.nameEmvCardLabel);

        typeInputCardLabelAuto = printFirst.findViewById(R.id.typeInputCardLabel);

        appFrameLabelAuto = printFirst.findViewById(R.id.appFrameLabel);
        tcFrameLayoutAuto = printFirst.findViewById(R.id.tcFrameLayout);
        aidFrameLayoutAuto = printFirst.findViewById(R.id.aidFrameLayout);
        taxLinearLayoutAuto = printFirst.findViewById(R.id.taxLinearLayout);
        copyLabelAuto = printFirst.findViewById(R.id.copyLabel);

        comCodeFragmentAuto = printFirst.findViewById(R.id.comCodeFragment);

        name_sw_version_Auto = printFirst.findViewById(R.id.name_sw_version);   // Paul_20190125 software version print

        sigatureLabelAuto = printFirst.findViewById(R.id.sigatureLabel);

    }

    private void selectSALE() {
        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            TransTemp transTemp = realm.where(TransTemp.class).equalTo("id", saleId).findFirst();
            Log.d(TAG, "selectSALE: " + transTemp.getCardNo());
            setDataView(transTemp);
            setDataViewAuto(transTemp);

            Log.d("Utility::","TransType 3 " + transTemp.getTransType());
        } finally {
            if (realm != null) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }

    }

    private void setDataView(TransTemp item) {
        //END K.GAME 181003 set text on View
        DecimalFormat decimalFormatShowGAME = new DecimalFormat("##,###,##0.00");
        String day_confirm = item.getTransDate().substring(6, 8);
        String mount_confirm = item.getTransDate().substring(4, 6);
        String year_confirm = item.getTransDate().substring(2, 4);
        if (item.getVoidFlag().equals("Y")) {
            dialogTitleLabel.setText("ยกเลิกรายการ");
            tv_LabelBaht.setTextColor(Color.RED);
            priceLabel.setTextColor(Color.RED);
//            priceLabel.setText("- " + item.getAmount());
            priceLabel.setText("- " + decimalFormatShowGAME.format(Double.valueOf(item.getAmount())));
        } else {
            dialogTitleLabel.setText("รายการขาย");
//            priceLabel.setText(item.getAmount());
            priceLabel.setText(decimalFormatShowGAME.format(Double.valueOf(item.getAmount())));
        }
//        tv_cardNumberLabel.setText(item.getCardNo());
//        tv_cardNumberLabel.setText(CardPrefix.maskcard(Preference.getInstance(SlipTemplateActivity.this).getValueString(Preference.KEY_CARDMASK_ID).toString(), item.getCardNo()));
        tv_cardNumberLabel.setText(CardPrefix.maskviewcard(" ", item.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555

//        if (Card_holder.isEmpty()) {
//            linear_username.setVisibility(View.GONE);//if username null > GONE
//        }

        String _name;
        if (item.getEmvNameCardHolder().replace(" ", "").equalsIgnoreCase("/")) {
            _name = "";
        } else {
            _name = item.getEmvNameCardHolder();
        }

        tv_confirm_username.setText(_name);//K.GAME 181010 ชื่อเจ้าของบัตร
        tv_confirm_date.setText(day_confirm + "/" + mount_confirm + "/" + year_confirm);
        tv_confirm_time.setText(item.getTransTime());
        tv_confirm_traceNo.setText(item.getEcr());
        tv_confirm_status.setText("Success");
        tv_confirm_terminalId.setText(item.getTid());
        tv_confirm_apprCode.setText(item.getApprvCode());
        tv_confirm_merchantId.setText(item.getMid());
//        tv_confirm_BatchNo.setText("");//K.GAME 181003 ย้ายไปใส่ด้านล่าง
        if (!item.getComCode().isEmpty() || cardManager.getHostCard().equalsIgnoreCase("TMS")) {//K.GAME 181009 อาจจะเกิดบัค
            linear_comCode.setVisibility(View.VISIBLE);
//            tv_confirm_comCode.setText(item.getComCode());
            tv_confirm_comCodetxt.setText(item.getComCode());
        }
        //END K.GAME 181003 set text on View

        //--------------------------------------------------------------------
//        linear_ref1.setVisibility(View.VISIBLE);
//        linear_ref1.setVisibility(View.VISIBLE);
//        linear_ref1.setVisibility(View.VISIBLE);
//        tv_confirm_ref1txt.setText(item.getRef1());

        //--------------------------------------------------------------------


        if (item.getHostTypeCard().equalsIgnoreCase("POS")) {
            Preference.getInstance(SlipTemplateActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_POS, item.getId());
            Preference.getInstance(SlipTemplateActivity.this).setValueString(Preference.KEY_SETTLE_TYPE_POS, typeSlip);
        } else if (item.getHostTypeCard().equalsIgnoreCase("EPS")) {
            Preference.getInstance(SlipTemplateActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_EPS, item.getId());
            Preference.getInstance(SlipTemplateActivity.this).setValueString(Preference.KEY_SETTLE_TYPE_EPS, typeSlip);
        } else if (item.getHostTypeCard().equalsIgnoreCase("TMS")) {        //20180708 SINN Add healthcare print.
            Preference.getInstance(SlipTemplateActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_TMS, item.getId());
            Preference.getInstance(SlipTemplateActivity.this).setValueString(Preference.KEY_SETTLE_TYPE_TMS, typeSlip);
        } else {
            Preference.getInstance(SlipTemplateActivity.this).setValueInt(Preference.KEY_SALE_VOID_PRINT_ID_TMS, item.getId());
            Preference.getInstance(SlipTemplateActivity.this).setValueString(Preference.KEY_SETTLE_TYPE_TMS, typeSlip);
        }
        DecimalFormat decimalFormatShow = new DecimalFormat("##,###,##0.00");
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabel.setText("TID:" + item.getTid());
        midLabel.setText("MID:" + item.getMid());
        traceLabel.setText("TRACE:" + item.getEcr());
//        systrcLabel.setText(item.getTraceNo());
        Log.d(TAG, "setDataView getTraceNo: " + item.getTraceNo());
        if (item.getHostTypeCard().equalsIgnoreCase("POS")) {
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
            batchTaxLayout.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
        } else if (item.getHostTypeCard().equalsIgnoreCase("EPS")) {
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
            batchTaxLayout.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
        } else if (item.getHostTypeCard().equalsIgnoreCase("TMS")) {
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            batchTaxLayout.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
        }
        //20180708 SINN Add healthcare print.
        else {
            batchLabel.setText("BATCH:" + CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));

        }
//end //20180708 SINN Add healthcare print.


        refNoLabel.setText("REF NO:" + item.getRefNo());

        if (item.getVoidFlag().equals("Y")) {
            typeLabel.setText("VOID");
        } else {
            typeLabel.setText("SALE");
            btn_gotoMain.setText("กลับสู่หน้าจอชำระค่าทางด่วน");
        }
        typeCardLabel.setText(CardPrefix.getJSONTypeCardName(item.getCardNo())); //20180815 SINN JSON // typeCardLabel.setText( CardPrefix.getTypeCardName(item.getCardNo()));
//        typeCardLabel.setText(CardPrefix.getTypeCardName(item.getCardNo()));

//        String cutCardStart = item.getCardNo().substring(0, 6);
//        String cutCardEnd = item.getCardNo().substring(12, item.getCardNo().length());
//        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
//        cardNoLabel.setText(cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16));
        cardNoLabel.setText(CardPrefix.maskviewcard(" ", item.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        apprCodeLabel.setText("APPR.CODE:" + item.getApprvCode());

        if (!item.getHostTypeCard().equals("TMS")) {
            comCodeFragment.setVisibility(View.GONE);
            Date date = new Date();
            String day = item.getTransDate().substring(6, 8);
            String mount = item.getTransDate().substring(4, 6);
            String year = item.getTransDate().substring(2, 4);


////20180719 all host DATE&TIME original sale for receipt.
            dateLabel.setText(day + "/" + mount + "/" + year);
            timeLabel.setText(item.getTransTime());
            dateTaxLayout.setText(day + "/" + mount + "/" + year);
            timeTaxLayout.setText(item.getTransTime());
//END  20180719 all host DATE&TIME original sale for receipt.


//            if (!item.getHostTypeCard().equalsIgnoreCase("POS")) {
//                dateLabel.setText(new SimpleDateFormat("dd/MM/yy").format(date));
//                timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));
//                dateTaxLayout.setText(new SimpleDateFormat("dd/MM/yy").format(date));
//                timeTaxLayout.setText(new SimpleDateFormat("HH:mm:ss").format(date));
//            } else {
//                dateLabel.setText(day + "/" + mount + "/" + year);
//                timeLabel.setText(item.getTransTime());
//                dateTaxLayout.setText(day + "/" + mount + "/" + year);
//                timeTaxLayout.setText(item.getTransTime());
//            }
//


//            timeLabel.setText(item.getTransTime());
/*
            String dayTax = item.getTransDate().substring(6, 8);
            String mountTax = item.getTransDate().substring(4, 6);
            String yearTax = item.getTransDate().substring(2, 4);
            if (!item.getHostTypeCard().equalsIgnoreCase("POS")) {

            } else {

            }*/
            taxIdLayout.setText(Preference.getInstance(SlipTemplateActivity.this).getValueString(Preference.KEY_TAX_ID));
            taxAbbLayout.setText(item.getTaxAbb());
            traceTaxLayout.setText(item.getEcr());

//
//            if (item.getTransType().equals("I")) {
//                if (!item.getEmvAppLabel().isEmpty()) {
//                    appLabel.setText(item.getEmvAppLabel());
//                } else {
//                    // appFrameLabel.setVisibility(View.GONE);
//                }
//
//                if (!item.getEmvTc().isEmpty()) {
//                    tcLabel.setText(item.getEmvTc());
//                } else {
//                    tcFrameLayout.setVisibility(View.GONE);
//                }
//
//                if (!item.getEmvAid().isEmpty()) {
//                    aidLabel.setText(item.getEmvAid());
//                } else {
//                    aidFrameLayout.setVisibility(View.GONE);
//                }
//            }else{
//                // appFrameLabel.setVisibility(View.GONE);
//                tcFrameLayout.setVisibility(View.GONE);
//                aidFrameLayout.setVisibility(View.GONE);
//            }

//            if (item.getEmvAppLabel() != null) {
//                if (!item.getEmvAppLabel().isEmpty()) {
//                    appLabel.setText(item.getEmvAppLabel());
//                } else {
//                    // appFrameLabel.setVisibility(View.GONE);
//                }
//            } else {
//                // appFrameLabel.setVisibility(View.GONE);
//            }
//
//            if (item.getEmvTc() != null) {
//                if (!item.getEmvTc().isEmpty()) {
//                    tcLabel.setText(item.getEmvTc());
//                } else {
//                    tcFrameLayout.setVisibility(View.GONE);
//                }
//            } else {
//                tcFrameLayout.setVisibility(View.GONE);
//            }
//            if (item.getEmvAid() != null) {
//                if (!item.getEmvAid().isEmpty()) {
//                    aidLabel.setText(item.getEmvAid());
//                } else {
//                    aidFrameLayout.setVisibility(View.GONE);
//                }
//            } else {
//                aidFrameLayout.setVisibility(View.GONE);
//            }
            Log.d(TAG, "setDataView: " + item.getEmvAppLabel());
            Log.d(TAG, "setDataView: " + item.getEmvTc());
            Log.d(TAG, "setDataView: " + item.getEmvAid());
            Log.d(TAG, "setDataView: " + item.getEmvNameCardHolder());
        } else {
            comCodeFragment.setVisibility(View.VISIBLE);
            Date date = new Date();
            /*String day = item.getTransDate().substring(6, 8);
            String mount = item.getTransDate().substring(4, 6);
            String year = item.getTransDate().substring(2, 4);

            dateLabel.setText(day + "/" + mount + "/" + year);
            timeLabel.setText(item.getTransTime());*/

            //SINN 20181219 date time from database.
            String day = item.getTransDate().substring(6, 8);
            String mount = item.getTransDate().substring(4, 6);
            String year = item.getTransDate().substring(2, 4);

            dateLabel.setText(day + "/" + mount + "/" + year);
            timeLabel.setText(item.getTransTime());

//            dateLabel.setText(new SimpleDateFormat("dd/MM/yy").format(date));
//            timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(date));

            texView.setVisibility(View.GONE);   //SINN make same 2 copie
            // appFrameLabel.setVisibility(View.GONE);
            tcFrameLayout.setVisibility(View.GONE);
            aidFrameLayout.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

//            lp.setMargins(0, 50, 0, 100);
            lp.setMargins(0, 20, 0, 100);
            copyLabel.setLayoutParams(lp);
            copyLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        if (item.getEmvNameCardHolder() != null) {
            String __name = item.getEmvNameCardHolder();
            if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                nameEmvCardLabel.setText("");
            } else {
                nameEmvCardLabel.setText(item.getEmvNameCardHolder().trim());
            }

        }

        if (item.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabel.setText("C");
        } else if (item.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabel.setText("W");
        } else {
            typeInputCardLabel.setText("S");
        }

        if (typeSlip.equalsIgnoreCase(CalculatePriceActivity.TypeSale)) {
            System.out.printf("utility:: %s setDataView item.getFee() = %s \n", TAG, item.getFee());
//            feeTaxLayout.setText(getString(R.string.slip_pattern_amount, item.getFee()));
            if (item.getFee() != null) {
                feeTaxLayout.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getFee().replaceAll(",", "")))));      // Paul_20190118
            } else {
                feeTaxLayout.setText(getString(R.string.slip_pattern_amount, item.getFee()));
            }
            amtThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getAmount().replaceAll(",", "")))));
            Log.d(TAG, "setDataView if : " + item.getAmount() + " Fee : " + item.getFee());
            if (item.getHostTypeCard().equals("TMS")) {
                if (!item.getEmciFree().isEmpty()) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Float.valueOf(item.getEmciFree()))));
                    double fee = Double.valueOf(item.getEmciFree());         // Paul_20190128
                    double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                    float fee = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getEmciFree())));
//                    float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount().replaceAll(",",""))));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
                Log.d(TAG, "setDataView Else : " + item.getAmount() + " Fee : " + item.getEmciFree());
            } else if (item.getFee() != null) {
//                feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(item.getFee()))));
                feeThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getFee().replaceAll(",", "")))));      // Paul_20190118
                double fee = Double.valueOf(item.getFee());         // Paul_20190128
                double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getFee())));
//                double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getAmount().replaceAll(",",""))));
                totThbLabel.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee))));
            } else {
                feeThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
                totThbLabel.setText(getString(R.string.slip_pattern_amount, "0.00"));
            }
        } else {
//            feeTaxLayout.setText(getString(R.string.slip_pattern_amount_void, item.getFee()));
            if (item.getFee() != null) {
                feeTaxLayout.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getFee().replaceAll(",", "")))));      // Paul_20190118
            } else {
                feeTaxLayout.setText(getString(R.string.slip_pattern_amount_void, item.getFee()));
            }
//            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Float.valueOf(item.getAmount().replaceAll(",","")))));
            amtThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(item.getAmount().replaceAll(",", "")))));   // Paul_20190128

            if (item.getHostTypeCard().equals("TMS")) {
                if (!item.getEmciFree().isEmpty()) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormat.format(Float.valueOf(item.getEmciFree()))));
                    double fee = Double.valueOf(item.getEmciFree());         // Paul_20190128
                    double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                    float fee = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getEmciFree())));
//                    float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount().replaceAll(",",""))));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
                Log.d(TAG, "setDataView Else : " + item.getAmount() + " Fee : " + item.getEmciFree());
            } else {
                if (item.getFee() != null) {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Float.valueOf(item.getFee().replaceAll(",", "")))));       // Paul_20190118
                    double fee = Double.valueOf(item.getFee());         // Paul_20190128
                    double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                    float fee = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getFee())));
//                    float amount = Float.parseFloat(decimalFormat.format(Float.valueOf(item.getAmount().replaceAll(",",""))));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));
                } else {
                    feeThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabel.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }
        String valueParameterEnable = Preference.getInstance(SlipTemplateActivity.this).getValueString(Preference.KEY_TAG_1000);
        if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {     //comm
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(item.getComCode());
            tv_confirm_comCodetxt.setText(item.getComCode());  //20181104 SINN Dialog print
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
            comCodeLabel.setVisibility(View.VISIBLE);
            comCodeLabel.setText(item.getComCode());
            tv_confirm_comCodetxt.setText(item.getComCode());  //20181104 SINN Dialog print
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
            comCodeLabel.setVisibility(View.GONE);
            comCodeLabel.setText(item.getComCode());
        }


        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {      //ref1
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(item.getRef1());

            linear_ref1.setVisibility(View.VISIBLE);
            tv_confirm_ref1txt.setText(item.getRef1());  //20181104 SINN Dialog print
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(item.getRef1());

            linear_ref1.setVisibility(View.VISIBLE);
            tv_confirm_ref1txt.setText(item.getRef1());  //20181104 SINN Dialog print
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
            ref1RelativeLayout.setVisibility(View.VISIBLE);
            ref1Label.setText(item.getRef1());
        }
        if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {    //ref2
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(item.getRef2());

            linear_ref2.setVisibility(View.VISIBLE);
            tv_confirm_ref2txt.setText(item.getRef2());  //20181104 SINN Dialog print
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(item.getRef2());

            linear_ref2.setVisibility(View.VISIBLE);
            tv_confirm_ref2txt.setText(item.getRef2());  //20181104 SINN Dialog print
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
            ref2RelativeLayout.setVisibility(View.VISIBLE);
            ref2Label.setText(item.getRef2());
        }
        if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {   //ref3
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(item.getRef3());

            linear_ref3.setVisibility(View.VISIBLE);
            tv_confirm_ref3txt.setText(item.getRef3());  //20181104 SINN Dialog print
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(item.getRef3());

            linear_ref3.setVisibility(View.VISIBLE);
            tv_confirm_ref3txt.setText(item.getRef3());  //20181104 SINN Dialog print

        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
            ref3RelativeLayout.setVisibility(View.VISIBLE);
            ref3Label.setText(item.getRef3());
        }
        name_sw_version.setVisibility(View.VISIBLE);             // Paul_20190125 software version print
        System.out.printf("utility:: %s BuildConfig.VERSION_NAME = %s\n", TAG, BuildConfig.VERSION_NAME);
        name_sw_version.setText(BuildConfig.VERSION_NAME);      // Paul_20190125 software version print
        //SINN 20181031 merge KTBNORMAL again.
//        if(Preference.getInstance(this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))//SINN 20181122  Merchant support rate KEY_MerchantSupportRate_ID
        if (Preference.getInstance(this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1")) {

            fee_thb = findViewById(R.id.fee_thb);    //SINN 20181031 merge KTBNORMAL again.
            tot_thb = findViewById(R.id.tot_thb);
            ;     //SINN 20181031 merge KTBNORMAL again.

            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);
        }
        // Paul_20190312 VOID No TAX Slip
        if (!typeSlip.equalsIgnoreCase(CalculatePriceActivity.TypeSale)) {
//            fee_thb = findViewById(R.id.fee_thb);    //SINN 20181031 merge KTBNORMAL again.
//            tot_thb = findViewById(R.id.tot_thb);;     //SINN 20181031 merge KTBNORMAL again.
//
//            fee_thb.setVisibility(View.GONE);
//            tot_thb.setVisibility(View.GONE);
            taxLinearLayout.setVisibility(View.GONE);
        }
    }

    private void setDataViewAuto(TransTemp item) {

        Log.d("Utility::","TransType 4 " + item.getTransType());

        DecimalFormat decimalFormatShow = new DecimalFormat("#,###,##0.00");    // Paul_20190118
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        tidLabelAuto.setText(item.getTid());
        midLabelAuto.setText(item.getMid());
        traceLabelAuto.setText(item.getEcr());
        systrcLabelAuto.setText(item.getTraceNo());
        if (item.getHostTypeCard().equalsIgnoreCase("POS")) {
            batchLabelAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
            batchTaxLayoutAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS), 6));//K.GAME 181003 New Ui confirm sale
        } else if (item.getHostTypeCard().equalsIgnoreCase("EPS")) {
            batchLabelAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
            batchTaxLayoutAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS), 6));//K.GAME 181003 New Ui confirm sale
        } else if (item.getHostTypeCard().equalsIgnoreCase("TMS")) {
            Preference.getInstance(SlipTemplateActivity.this).setValueString(Preference.KEY_TEMP, "TMS");  //SINN RS232 SALE 20180709;
            batchLabelAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            batchTaxLayoutAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS), 6));//K.GAME 181003 New Ui confirm sale
        }
//20180708 SINN Add healthcare print.
        else if (item.getHostTypeCard().equalsIgnoreCase("GHC")) {
            batchLabelAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
            batchTaxLayoutAuto.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));
            tv_confirm_BatchNo.setText(CardPrefix.calLen(Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC), 6));//K.GAME 181003 New Ui confirm sale
        }
        //end //20180708 SINN Add healthcare print.

        refNoLabelAuto.setText(item.getRefNo());

//        timeLabelAuto.setText(item.getTransTime());

        if (!item.getHostTypeCard().equals("TMS")) {
            comCodeFragmentAuto.setVisibility(View.GONE);


            taxIdLayoutAuto.setText(Preference.getInstance(SlipTemplateActivity.this).getValueString(Preference.KEY_TAX_ID));
            taxAbbLayoutAuto.setText(item.getTaxAbb());
            traceTaxLayoutAuto.setText(item.getEcr());
//            dateTaxLayoutAuto.setText(item.getTransDate());
            String day = item.getTransDate().substring(6, 8);
            String mount = item.getTransDate().substring(4, 6);
            String year = item.getTransDate().substring(2, 4);
            Date date = new Date();
            /*if (!item.getHostTypeCard().equalsIgnoreCase("POS")) {
                dateLabelAuto.setText(new SimpleDateFormat("dd/MM/yy").format(date));
                timeLabelAuto.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                dateTaxLayoutAuto.setText(new SimpleDateFormat("dd/MM/yy").format(date));
                timeTaxLayoutAuto.setText(new SimpleDateFormat("HH:mm:ss").format(date));
            } else {*/
            dateLabelAuto.setText(day + "/" + mount + "/" + year);
            timeLabelAuto.setText(item.getTransTime());
            dateTaxLayoutAuto.setText(day + "/" + mount + "/" + year);
            timeTaxLayoutAuto.setText(item.getTransTime());
//            }

            /*dateTaxLayoutAuto.setText(day + "/" + mount + "/" + year);
            timeTaxLayoutAuto.setText(item.getTransTime());*/

            if (item.getTransType().equalsIgnoreCase("I")) {
                if (!item.getEmvAppLabel().isEmpty()) {
                    appLabelAuto.setText(item.getEmvAppLabel());
                } else {
                    appFrameLabelAuto.setVisibility(View.GONE);
//                    appLabelAuto.setText(app);
                }

                if (!item.getEmvTc().isEmpty()) {
                    tcLabelAuto.setText(item.getEmvTc());
                } else {
                    tcFrameLayoutAuto.setVisibility(View.GONE);
//                    tcLabelAuto.setText(tc);

                }

                if (!item.getEmvAid().isEmpty()) {
                    aidLabelAuto.setText(item.getEmvAid());
                } else {
                    aidFrameLayoutAuto.setVisibility(View.GONE);
//                    aidLabelAuto.setText(aid);
                }
            } else if (item.getTransType().equalsIgnoreCase("W")) {
                sigatureLabelAuto.setVisibility(View.GONE);
                if (item.getEmvAppLabel() != null) {
                    appLabelAuto.setText(item.getEmvAppLabel());
                } else {
//                    appFrameLabelAuto.setVisibility(View.GONE);
                    appLabelAuto.setText("");
                }

                if (!item.getEmvTc().isEmpty()) {
                    tcLabelAuto.setText(item.getEmvTc());
                } else {
                    tcFrameLayoutAuto.setVisibility(View.GONE);
//                    tcLabelAuto.setText(tc);

                }

                if (!item.getEmvAid().isEmpty()) {
                    aidLabelAuto.setText(item.getEmvAid());
                } else {
                    aidFrameLayoutAuto.setVisibility(View.GONE);
//                    aidLabelAuto.setText(aid);
                }


            } else {
//                appFrameLabelAuto.setVisibility(View.GONE);
//                tcFrameLayoutAuto.setVisibility(View.GONE);
//                aidFrameLayoutAuto.setVisibility(View.GONE);

                sigatureLabelAuto.setVisibility(View.GONE);
                if (item.getEmvAppLabel() != null) {
                    appLabelAuto.setText(item.getEmvAppLabel());
                } else {
//                    appFrameLabelAuto.setVisibility(View.GONE);
                    appLabelAuto.setText("");
                }

                if (!item.getEmvTc().isEmpty()) {
                    tcLabelAuto.setText(item.getEmvTc());
                } else {
                    tcFrameLayoutAuto.setVisibility(View.GONE);
//                    tcLabelAuto.setText(tc);

                }

                if (!item.getEmvAid().isEmpty()) {
                    aidLabelAuto.setText(item.getEmvAid());
                } else {
                    aidFrameLayoutAuto.setVisibility(View.GONE);
//                    aidLabelAuto.setText(aid);
                }
            }

//            if (item.getEmvAppLabel() != null) {
//                if (!item.getEmvAppLabel().isEmpty()) {
//                    appLabelAuto.setText(item.getEmvAppLabel());
//                } else {
//                    appFrameLabelAuto.setVisibility(View.GONE);
//                }
//            } else {
//                appFrameLabelAuto.setVisibility(View.GONE);
//            }
//
//            if (item.getEmvTc() != null) {
//                if (!item.getEmvTc().isEmpty()) {
//                    tcLabelAuto.setText(item.getEmvTc());
//                } else {
//                    tcFrameLayoutAuto.setVisibility(View.GONE);
//                }
//            } else {
//                tcFrameLayoutAuto.setVisibility(View.GONE);
//            }
//
//            if (item.getEmvAid() != null) {
//                if (!item.getEmvAid().isEmpty()) {
//                    aidLabelAuto.setText(item.getEmvAid());
//                } else {
//                    aidFrameLayoutAuto.setVisibility(View.GONE);
//                }
//            } else {
//                aidFrameLayoutAuto.setVisibility(View.GONE);
//            }

            Log.d(TAG, "lp.setMargins NOOOOOO");

        } else {
            comCodeFragmentAuto.setVisibility(View.VISIBLE);
            Date date = new Date();
            String day = item.getTransDate().substring(6, 8);
            String mount = item.getTransDate().substring(4, 6);
            String year = item.getTransDate().substring(2, 4);
            dateLabelAuto.setText(day + "/" + mount + "/" + year);
            timeLabelAuto.setText(item.getTransTime());
            /*dateLabelAuto.setText(new SimpleDateFormat("dd/MM/yy").format(date));
            timeLabelAuto.setText(new SimpleDateFormat("HH:mm:ss").format(date));*/

            dateTaxLayoutAuto.setText(day + "/" + mount + "/" + year);
            timeTaxLayoutAuto.setText(item.getTransTime());

            texViewAuto.setVisibility(View.GONE);
//            appFrameLabelAuto.setVisibility(View.GONE);
//            tcFrameLayoutAuto.setVisibility(View.GONE);
//            aidFrameLayoutAuto.setVisibility(View.GONE);
            taxLinearLayoutAuto.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 20, 0, 100);
            copyLabelAuto.setLayoutParams(lp);
            copyLabelAuto.setGravity(Gravity.CENTER_HORIZONTAL);
        }


//        if(Preference.getInstance(this).getValueString(Preference.KEY_KTBNORMAL_ID).equalsIgnoreCase("1"))//SINN 20181122  Merchant support rate KEY_MerchantSupportRate_ID
        if (Preference.getInstance(this).getValueString(Preference.KEY_MerchantSupportRate_ID).equalsIgnoreCase("1")) {

            fee_thb = printFirst.findViewById(R.id.fee_thb);    //SINN 20181031 merge KTBNORMAL again.
            tot_thb = printFirst.findViewById(R.id.tot_thb);
            ;     //SINN 20181031 merge KTBNORMAL again.

            fee_thb.setVisibility(View.GONE);
            tot_thb.setVisibility(View.GONE);
            taxLinearLayoutAuto.setVisibility(View.GONE);
        }
        // Paul_20190312 VOID No TAX Slip
        if (!typeSlip.equalsIgnoreCase(CalculatePriceActivity.TypeSale)) {
//            fee_thb = printFirst.findViewById(R.id.fee_thb);    //SINN 20181031 merge KTBNORMAL again.
//            tot_thb = printFirst.findViewById(R.id.tot_thb);;     //SINN 20181031 merge KTBNORMAL again.
//
//            fee_thb.setVisibility(View.GONE);
//            tot_thb.setVisibility(View.GONE);
            taxLinearLayoutAuto.setVisibility(View.GONE);
        }

        if (item.getEmvNameCardHolder() != null) {
            String __name = item.getEmvNameCardHolder();
            if (__name.replace(" ", "").equalsIgnoreCase("/")) {
                nameEmvCardLabelAuto.setText("");
            } else {
                nameEmvCardLabelAuto.setText(item.getEmvNameCardHolder().trim());
            }
//            nameEmvCardLabelAuto.setText(item.getEmvNameCardHolder().trim());
        }
        if (item.getTransType().equalsIgnoreCase("I")) {
            typeInputCardLabelAuto.setText("C");
        } else if (item.getTransType().equalsIgnoreCase("W")) {
            typeInputCardLabelAuto.setText("W");
        } else {
            typeInputCardLabelAuto.setText("S");
        }

        Log.d("Utility::","TransType 5 " + item.getTransType());

//        typeLabelAuto.setText(item.getTransStat());
        if (item.getVoidFlag().equals("Y")) {
            typeLabelAuto.setText("VOID");
        } else {
            typeLabelAuto.setText("SALE");

            btn_gotoMain.setText("กลับสู่หน้าจอชำระค่าทางด่วน");
        }
//        typeCardLabelAuto.setText(CardPrefix.getTypeCardName(item.getCardNo()));   //20180815 SINN JSON
        typeCardLabelAuto.setText(CardPrefix.getJSONTypeCardName(item.getCardNo()));
//        String cutCardStart = item.getCardNo().substring(0, 6);
//        String cutCardEnd = item.getCardNo().substring(12, item.getCardNo().length());
//        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
//
//        szCardForPOS = cutCardStart + "xxxxxx" + cutCardEnd;
        szCardForPOS = CardPrefix.maskviewcard(" ", item.getCardNo());  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555

//        cardNoLabelAuto.setText(cardNo.substring(0, 4) + " " + cardNo.substring(4, 8) + " " + cardNo.substring(8, 12) + " " + cardNo.substring(12, 16));
        cardNoLabelAuto.setText(CardPrefix.maskviewcard(" ", item.getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555

        apprCodeLabelAuto.setText(item.getApprvCode());
//        comCodeLabelAuto.setText(item.getComCode());
        if (typeSlip.equalsIgnoreCase(CalculatePriceActivity.TypeSale)) {
//            feeTaxLayoutAuto.setText(getString(R.string.slip_pattern_amount, item.getFee()));
            if (item.getFee() != null) {
                feeTaxLayoutAuto.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getFee().replaceAll(",", "")))));  // Paul_20190118
            } else {
                feeTaxLayoutAuto.setText(getString(R.string.slip_pattern_amount, item.getFee()));
            }
            amtThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getAmount().replaceAll(",", "")))));
            Log.d(TAG, "setDataView if : " + item.getAmount() + " Fee : " + item.getFee());
            if (item.getHostTypeCard().equals("TMS")) {
                if (!item.getEmciFree().isEmpty()) {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Float.valueOf(item.getEmciFree()))));
                    double fee = Double.valueOf(item.getEmciFree());         // Paul_20190128
                    double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                    double fee = Float.parseFloat(decimalFormat.format(Double.valueOf(item.getEmciFree())));
//                    double amount = Float.parseFloat(decimalFormat.format(Double.valueOf(item.getAmount().replaceAll(",",""))));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee))));
                } else {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount, "0.00"));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount, "0.00"));
                }
            } else if (item.getFee() != null) {
//                feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormat.format(Double.valueOf(item.getFee()))));
                feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format(Double.valueOf(item.getFee().replaceAll(",", "")))));   // Paul_20190118
                double fee = Double.valueOf(item.getFee());         // Paul_20190128
                double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                double fee = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getFee())));
//                double amount = Double.parseDouble(decimalFormat.format(Double.valueOf(item.getAmount().replaceAll(",",""))));
                totThbLabelAuto.setText(getString(R.string.slip_pattern_amount, decimalFormatShow.format((amount + fee))));
            } else {
                feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount, "0.00"));
                totThbLabelAuto.setText(getString(R.string.slip_pattern_amount, "0.00"));
            }
        } else {
//            feeTaxLayoutAuto.setText(getString(R.string.slip_pattern_amount_void, item.getFee()));
            if (item.getFee() != null) {
                feeTaxLayoutAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(item.getFee().replaceAll(",", "")))));  // Paul_20190118
            } else {
                feeTaxLayoutAuto.setText(getString(R.string.slip_pattern_amount_void, item.getFee()));
            }
            amtThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(item.getAmount().replaceAll(",", "")))));    // Paul_20190128
            Log.d(TAG, "setDataView Else : " + item.getAmount() + " Fee : " + item.getFee());
            if (item.getHostTypeCard().equals("TMS")) {
                if (!item.getEmciFree().isEmpty()) {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(item.getEmciFree()))));  // Paul_20190128
                    double fee = Double.valueOf(item.getEmciFree());         // Paul_20190128
                    double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                    double fee = Float.parseFloat(decimalFormat.format(Double.valueOf(item.getEmciFree())));
//                    double amount = Float.parseFloat(decimalFormat.format(Double.valueOf(item.getAmount().replaceAll(",",""))));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((amount + fee))));
                } else {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            } else {
                if (item.getFee() != null) {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format(Double.valueOf(item.getFee().replaceAll(",", "")))));   // Paul_20190118  // Paul_20190128
                    double fee = Double.valueOf(item.getFee());         // Paul_20190128
                    double amount = Double.valueOf(item.getAmount());         // Paul_20190128
//                    double fee = Float.parseFloat(decimalFormat.format(Double.valueOf(item.getFee()))); // Paul_20190128
//                    double amount = Float.parseFloat(decimalFormat.format(Double.valueOf(item.getAmount().replaceAll(",",""))));    // Paul_20190128
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, decimalFormatShow.format((Double) (amount + fee))));
                } else {
                    feeThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                    totThbLabelAuto.setText(getString(R.string.slip_pattern_amount_void, "0.00"));
                }
            }
        }
        String valueParameterEnable = Preference.getInstance(SlipTemplateActivity.this).getValueString(Preference.KEY_TAG_1000);
        if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("3")) {
            comCodeLabelAuto.setVisibility(View.VISIBLE);
            comCodeLabelAuto.setText(item.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("4")) {
            comCodeLabelAuto.setVisibility(View.VISIBLE);
            comCodeLabelAuto.setText(item.getComCode());
        } else if (valueParameterEnable.substring(0, 1).equalsIgnoreCase("2")) {
            comCodeLabelAuto.setVisibility(View.GONE);
            comCodeLabelAuto.setText(item.getComCode());
        }
        if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("3")) {
            ref1RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref1LabelAuto.setText(item.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("4")) {
            ref1RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref1LabelAuto.setText(item.getRef1());
        } else if (valueParameterEnable.substring(1, 2).equalsIgnoreCase("2")) {
            ref1RelativeLayoutAuto.setVisibility(View.GONE);
            ref1LabelAuto.setText(item.getRef1());
        }
        if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("3")) {
            ref2RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref2LabelAuto.setText(item.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("4")) {
            ref2RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref2LabelAuto.setText(item.getRef2());
        } else if (valueParameterEnable.substring(2, 3).equalsIgnoreCase("2")) {
            ref2RelativeLayoutAuto.setVisibility(View.GONE);
            ref2LabelAuto.setText(item.getRef2());
        }
        if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("3")) {
            ref3RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref3LabelAuto.setText(item.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("4")) {
            ref3RelativeLayoutAuto.setVisibility(View.VISIBLE);
            ref3LabelAuto.setText(item.getRef3());
        } else if (valueParameterEnable.substring(3, 4).equalsIgnoreCase("2")) {
            ref3RelativeLayoutAuto.setVisibility(View.GONE);
            ref3LabelAuto.setText(item.getRef3());
        }

        name_sw_version_Auto.setVisibility(View.VISIBLE);             // Paul_20190125 software version print
        name_sw_version_Auto.setText(BuildConfig.VERSION_NAME);      // Paul_20190125 software version print


        if (printslip.equalsIgnoreCase("00")) {
            printBtn.setBackgroundColor(getResources().getColor(R.color.color_gray));
            printBtn.setEnabled(false);
            cardManager.abortPBOCProcess();
        }


        setMeasure();

        if (!printslip.substring(0, 1).equalsIgnoreCase("1")) {
            printBtn.setEnabled(true);
            statusOutScress = true;
        }


        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.printf("utility:: %s setDataViewAuto 00000001 \n", TAG);

            }

            @Override
            public void onFinish() {

                System.out.printf("utility:: %s setDataViewAuto 00000002 \n", TAG);

                // if(typeInterface!=null)
//                if(Preference.getInstance(SlipTemplateActivity.this).getValueString(Preference.KEY_RS232_FLAG).equals("1"))
//                if(typeInterface!=null)
//                    TellToPosMatching();//SINN SALE Interface POS call  20180709
// Paul_20180717
//        if(typeInterface != null) {
                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
//                    Utility.customDialogAlertAuto( SlipTemplateActivity.this, "transactionTimeOut" );


                    System.out.printf("utility:: BBBBBBBBBBBBBBBBBBB 00000000000000001 \n");
                    TellToPosMatching();
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            System.out.printf("utility:: %s onFinish success XXXX \n", TAG);
                            // K.GAME เก็บไว้ใช้ง
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new CountDownTimer(2000, 1000) {

                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            printBtn.setEnabled(false);     // Paul_20181015 Next Button many cleck and than many printing. Solved
                                            System.out.printf("utility:: %s setDataViewAuto 00000003 \n", TAG);
//                                            customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");
                                            customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับร้านค้า");  //..SINN 2018102ถ Wording print dialog
                                            dialogWaiting.show();

                                        }

                                        @Override
                                        public void onFinish() {
                                            System.out.printf("utility:: %s setDataViewAuto 00000004 \n", TAG);
                                            dialogWaiting.dismiss();
                                            printBtn.setEnabled(true);      // Paul_20181015 Next Button many cleck and than many printing. Solved
                                        }
                                    }.start();
                                }
                            });
                            //END K.GAME เก็บไว้ใช้
                            System.out.printf("utility:: %s //dpPrinting Befor 044 \n", TAG);
                            if (printslip.substring(0, 1).equalsIgnoreCase("1"))
                                ////dpPrinting(getBitmapFromView(slipLinearLayoutAuto));

                            if (printslip.substring(1, 2).equalsIgnoreCase("1"))
                                autoPrint();
                            else {
                                //K.GAME 180917 New dialog
//                                dialogSuccess_GotoMain();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new CountDownTimer(2000, 1000) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                //SINN 20181013 posinterface autoclose
                                                dialogWaiting.dismiss();
                                                dialogSuccess_GotoMain.show();
                                                //SINN 20181013 posinterface autoclose
                                            }

                                            @Override
                                            public void onFinish() {
                                                //SINN 20181013 posinterface autoclose
//                                                dialogWaiting.dismiss();
//                                                dialogSuccess_GotoMain.show();
                                                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                                    dialogSuccess_GotoMain.dismiss();
                                                    Intent intent = new Intent(SlipTemplateActivity.this, MenuServiceListActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                }
                                                //SINN 20181013 posinterface autoclose
                                            }
                                        }.start();
                                    }
                                });

                            }

//                            Intent intent = new Intent( SlipTemplateActivity.this, MenuServiceListActivity.class );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                            startActivity( intent );
//                            finish();
//                            overridePendingTransition( 0, 0 );
                        }
                    });
                } else {
                    // K.GAME เก็บไว้ใช้ง
                    if (printslip.substring(0, 1).equalsIgnoreCase("1")) {  //merchant
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new CountDownTimer(2000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        printBtn.setEnabled(false);     // Paul_20181015 Next Button many cleck and than many printing. Solved
                                        System.out.printf("utility:: %s setDataViewAuto 00000005 \n", TAG);
//                                    customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");
                                        customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับร้านค้า");  // ***SINN 2018102ถ Wording print dialog  //SINN 20181107  message for dialog print waiting
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(!dialogSuccess_GotoMain.isShowing()) {
                                                    dialogWaiting.show();
                                                }
                                            }
                                        });


                                    }

                                    @Override
                                    public void onFinish() {
                                        System.out.printf("utility:: %s setDataViewAuto 00000006 \n", TAG);
                                        dialogWaiting.dismiss();
                                        printBtn.setEnabled(true);  // Paul_20181015 Next Button many cleck and than many printing. Solved
                                    }
                                }.start();
                            }
                        });
                    }
                    //END K.GAME เก็บไว้ใช้
                    System.out.printf("utility:: %s //dpPrinting Befor 045 \n", TAG);

                    if (printslip.substring(0, 1).equalsIgnoreCase("1"))    //merchant
                        ////dpPrinting(getBitmapFromView(slipLinearLayoutAuto));
                    if (printslip.substring(1, 2).equalsIgnoreCase("1"))   //customer
                    {
                        printBtn.setEnabled(false);
                        autoPrint();
                    } else {

                        //K.GAME 180917 New dialog
//                                dialogSuccess_GotoMain();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new CountDownTimer(2000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        //SINN 20181013 posinterface autoclose
                                        if (dialogWaiting != null) {
                                            dialogWaiting.dismiss();
                                            dialogSuccess_GotoMain.show();
                                        }
                                        //SINN 20181013 posinterface autoclose
                                    }

                                    @Override
                                    public void onFinish() {
                                        //SINN 20181013 posinterface autoclose
//                                                dialogWaiting.dismiss();
//                                                dialogSuccess_GotoMain.show();
                                        if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                            dialogSuccess_GotoMain.dismiss();
                                            Intent intent = new Intent(SlipTemplateActivity.this, MenuServiceListActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(0, 0);
                                        }
                                        //SINN 20181013 posinterface autoclose
                                    }
                                }.start();
                            }
                        });


                    }
                }
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
        if (dialogWaiting != null)
            dialogWaiting.dismiss();

//        timer = new CountDownTimer(5000, 1000) {

//        Integer insettime =5000;
        Integer insettime = 30000;      // Paul_20190311 K.phadet want 30sec
        if (!printslip.substring(0, 1).equalsIgnoreCase("1")) {
            insettime = 500;
            printBtn.setEnabled(false);

            if (inAutoprint == 1)
                return;

        }

        timer = new CountDownTimer(insettime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + millisUntilFinished);
                System.out.printf("utility:: %s autoPrint 00000001 \n", TAG);

            }

            @Override
            public void onFinish() {

                System.out.printf("utility:: %s autoPrint 00000002 \n", TAG);
//                customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับร้านค้า");
                customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");   //SINN 2018102ถ Wording print dialog
                dialogWaiting.show();


                // dialogSuccess_GotoMain.show();

                // dialogWaiting.show();
                // K.GAME เก็บไว้ใช้ง
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        new CountDownTimer(2000, 1000) {
//
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//                                customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับร้านค้า");
//                                dialogWaiting.show();
//
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                dialogWaiting.dismiss();
//                            }
//                        }.start();
//                    }
//                });
                //END K.GAME เก็บไว้ใช้
                statusOutScress = true;
                // //dpPrinting(getBitmapFromView(slipLinearLayout));
                System.out.printf("utility:: %s //dpPrinting Befor 041 \n", TAG);
                //dpPrinting_last(getBitmapFromView(slipLinearLayout));
            }
        };
        timer.start();
//        if(statusOutScress)
//        dialogSuccess_GotoMain.show();
    }

    public void dpPrinting_last(Bitmap slip) {
        System.out.printf("utility:: %s //dpPrinting_last \n", TAG);
        bitmapOld = slip;
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    //This interface is used for set the gray level of the printing
                    //MIN is 0,Max is 4
                    //The level bigger, the speed of print is smaller
                    printDev.setPrinterGray(2);
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Log.d(TAG, "onPrintFinish: ");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    printBtn.setEnabled(true);
                                }
                            });
                            if (statusOutScress) {
                                //K.GAME 180917 New dialog
//                                dialogSuccess_GotoMain();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new CountDownTimer(2000, 1000) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                //SINN 20181013 posinterface autoclose
                                                dialogWaiting.dismiss();
                                                dialogSuccess_GotoMain.show();
                                                //SINN 20181013 posinterface autoclose
                                            }

                                            @Override
                                            public void onFinish() {
                                                //SINN 20181013 posinterface autoclose
//                                                dialogWaiting.dismiss();
//                                                dialogSuccess_GotoMain.show();
                                                if (posInterfaceActivity.PosInterfaceExistFlg == 1) {
                                                    dialogSuccess_GotoMain.dismiss();
                                                    Intent intent = new Intent(SlipTemplateActivity.this, MenuServiceListActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                }
                                                //SINN 20181013 posinterface autoclose
                                            }
                                        }.start();
                                    }
                                });

                            } else {
                                if (timer != null) {
                                    timer.cancel();
                                    timer.start();
                                }
                            }
                        }

                        @Override
                        public void onPrintError(int i) throws RemoteException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onPrintError: ");
                                    msgLabel.setText("เกิดข้อผิดพลาด เช่นแบตเตอรี่อ่อน");
                                    dialogOutOfPaper.show();
                                }
                            });

                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {
                            Log.d(TAG, "onPrintOutOfPaper: ");
                            if (!statusOutScress) {
                                timer.cancel();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    dialogOutOfPaper.show();
                                }
                            });
                        }
                    });
//                    int ret = printDev.printBarCodeSync("asdasd");
//                    Log.d(TAG, "after call printData ret = " + ret);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void dpPrinting(Bitmap slip) {
        bitmapOld = slip;
        new Thread() {
            @Override
            public void run() {
                try {
                    printDev.initPrinter();
                    //This interface is used for set the gray level of the printing
                    //MIN is 0,Max is 4
                    //The level bigger, the speed of print is smaller
                    printDev.setPrinterGray(2);
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Log.d(TAG, "onPrintFinish: ");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    printBtn.setEnabled(true);
                                }
                            });
                            if (statusOutScress) {
                                //K.GAME 180917 New dialog
//                                dialogSuccess_GotoMain();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new CountDownTimer(2000, 1000) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                //customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับร้านค้า");   ////SINN 20181107  message for dialog print waiting
//                                                customDialogWaiting("กำลังพิมพ์ใบเสร็จ");
                                                dialogWaiting.show();

                                            }

                                            @Override
                                            public void onFinish() {
                                                dialogWaiting.dismiss();
                                                dialogSuccess_GotoMain.show();
//                            dialogSuccess_GotoMain.show();
                                            }
                                        }.start();
                                    }
                                });
                                Log.d("1919", "เข้า statusOutScress");
                                //END K.GAME 180917 New dialog

//                                Intent intent = new Intent(SlipTemplateActivity.this, MenuServiceListActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                                overridePendingTransition(0, 0);
                            } else {
                                if (timer != null) {
                                    timer.cancel();
                                    timer.start();
                                }
                            }
                        }

                        @Override
                        public void onPrintError(int i) throws RemoteException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onPrintError: ");
                                    msgLabel.setText("เกิดข้อผิดพลาด เช่นแบตเตอรี่อ่อน");
                                    dialogOutOfPaper.show();
                                }
                            });

                        }

                        @Override
                        public void onPrintOutOfPaper() throws RemoteException {
                            Log.d(TAG, "onPrintOutOfPaper: ");
                            if (!statusOutScress) {
                                timer.cancel();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    dialogOutOfPaper.show();
                                }
                            });
                        }
                    });
//                    int ret = printDev.printBarCodeSync("asdasd");
//                    Log.d(TAG, "after call printData ret = " + ret);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void dialogSuccess_GotoMain() {
        dialogSuccess_GotoMain = new Dialog(this);
        dialogSuccess_GotoMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess_GotoMain.setContentView(R.layout.dialog_custom_success_gotomain);
        dialogSuccess_GotoMain.setCancelable(false);
        dialogSuccess_GotoMain.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSuccess_GotoMain.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogSuccess_GotoMain.findViewById(R.id.msgLabel);
//        Button btn_gotoMain = dialogSuccess_GotoMain.findViewById(R.id.btn_gotoMain);
        btn_gotoMain = dialogSuccess_GotoMain.findViewById(R.id.btn_gotoMain);
        btn_gotoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlipTemplateActivity.this, MenuServiceListActivity.class);

                if (typeLabel.getText().toString().equalsIgnoreCase("SALE")) {
                    intent.putExtra("InsertCard", true);
                }

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
        if (v == printBtn) {
            statusOutScress = true;
            printBtn.setEnabled(false);

            inAutoprint = 1;

            if (printslip.equalsIgnoreCase("00"))
                return;

            // K.GAME เก็บไว้ใช้ง
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CountDownTimer(2000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
//                            customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับร้านค้า");
                            customDialogWaiting("กำลังพิมพ์ใบเสร็จสำหรับลูกค้า");   //SINN 20181106 modify change 2nd print
                            dialogWaiting.show();

                        }

                        @Override
                        public void onFinish() {
                            // dialogWaiting.dismiss();
                        }
                    }.start();
                }
            });
            //END K.GAME เก็บไว้ใช้
            ////dpPrinting(getBitmapFromView(slipLinearLayout));
            System.out.printf("utility:: %s //dpPrinting Befor 043 \n", TAG);
            //dpPrinting_last(getBitmapFromView(slipLinearLayout));
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    private void customDialogOutOfPaper() {
        dialogOutOfPaper = new Dialog(this, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogOutOfPaper.getLayoutInflater().inflate(R.layout.dialog_custom_printer, null);//K.GAME 180821
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogOutOfPaper.setContentView(view);//K.GAME 180821
        dialogOutOfPaper.setCancelable(false);//K.GAME 180821

//        dialogOutOfPaper = new Dialog(this);
//        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogOutOfPaper.setContentView(R.layout.dialog_custom_printer);
//        dialogOutOfPaper.setCancelable(false);
//        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        okBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        msgLabel = dialogOutOfPaper.findViewById(R.id.msgLabel);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.printf("utility:: %s //dpPrinting Befor 042 \n", TAG);
                ////dpPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //SINN RS232 IQ found  20180705
    public void TellToPosMatching() {
        String szMSG = "";
        String DD = "";
        String MM = "";
        String YY = "";
        posInterfaceActivity.PosInterfaceWriteField("01", apprCodeLabelAuto.getText().toString());   // Approval Code
//        posInterfaceActivity.PosInterfaceWriteField("02",posInterfaceActivity.ResponseMsgPosInterface("00"));   // Response Message TX NOT FOUND
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("00"));

        posInterfaceActivity.PosInterfaceWriteField("65", traceLabelAuto.getText().toString());   // Invoice Number
        posInterfaceActivity.PosInterfaceWriteField("D3", refNoLabelAuto.getText().toString());  //Reference No

//        tidLabelAuto.setText(item.getTid());
//        midLabelAuto.setText(item.getMid());
        posInterfaceActivity.PosInterfaceWriteField("16", tidLabelAuto.getText().toString());  //tid
        posInterfaceActivity.PosInterfaceWriteField("D1", midLabelAuto.getText().toString());  //mid

//        dateLabelAuto.setText(day + "/" + mount + "/" + year);
//        timeLabelAuto.setText(item.getTransTime());

        szMSG = dateLabelAuto.getText().toString();   //  09/07/18
        DD = szMSG.substring(0, 2);
        MM = szMSG.substring(3, 5);
        YY = szMSG.substring(6, 8);


        posInterfaceActivity.PosInterfaceWriteField("03", YY + MM + DD);  //yymmdd

        szMSG = timeLabelAuto.getText().toString();   //15:12:12
        DD = szMSG.substring(0, 2);
        MM = szMSG.substring(3, 5);
        YY = szMSG.substring(6, 8);

        posInterfaceActivity.PosInterfaceWriteField("04", DD + MM + YY);  //hhmmss

        //posInterfaceActivity.PosInterfaceWriteField("30",cardNoLabelAuto.getText().toString());    //484831xxxxxx0150
        posInterfaceActivity.PosInterfaceWriteField("30", szCardForPOS);

        if (Preference.getInstance(SlipTemplateActivity.this).getValueString(Preference.KEY_TEMP).equals("TMS"))
            posInterfaceActivity.PosInterfaceWriteField("F1", "ONUS ");   //ONUS  //OFFUS
        else
            posInterfaceActivity.PosInterfaceWriteField("F1", "OFFUS");

        posInterfaceActivity.PosInterfaceSendMessage(PosInterfaceActivity.PosInterfaceTransactionCode, "00");


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown");

        // return super.onKeyDown( keyCode, event );
        return false;
    }
}
