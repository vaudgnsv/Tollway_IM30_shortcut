package org.centerm.Tollway.healthcare.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.MenuServiceListActivity;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.database.ReversalHealthCare;
import org.centerm.Tollway.healthcare.activity.offline.CalculateHelthCareOfflineActivity;
import org.centerm.Tollway.healthcare.baseavtivity.BaseHealthCardActivity;
import org.centerm.Tollway.healthcare.model.CardId;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.helper.RespCode;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import io.realm.Realm;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_AMOUNT;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_INTERFACE_CARD_ID_CHILD;
import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;

public class MedicalTreatmentActivity extends BaseHealthCardActivity implements View.OnClickListener {

    public static final String KEY_STATUS_SALE = MedicalTreatmentActivity.class.getName() + "_key_status_sale";

    public static final String KEY_ID_FOREIGNER_NUMBER = MedicalTreatmentActivity.class.getName() + "_key_id_foreigner_number";


    //// //SINN 20180911  Add topic msg.
    public static String KEY_GHC_TOPIC_MSG1 = MedicalTreatmentActivity.class.getName() + "key_ghc_topic_msg1";
    public static String KEY_GHC_TOPIC_MSG2 = MedicalTreatmentActivity.class.getName() + "key_ghc_topic_msg2";


    public static final String KEY_TYPE_FAMILY = "11";
    public static final String KEY_TYPE_MIN_SEVEN = "12";
    public static final String KEY_TYPE_FOREIGNER = "13";
    public static final String KEY_TYPE_NO_CARD = "14";

    public static final String KEY_TYPE_KIDNEY_FAMILY = "21";
    public static final String KEY_TYPE_KIDNEY_MIN_SEVEN = "22";
    public static final String KEY_TYPE_KIDNEY_FOREIGNER = "23";
    public static final String KEY_TYPE_KIDNEY_NO_CARD = "24";

    public static final String KEY_TYPE_CANCER_FAMILY = "31";
    public static final String KEY_TYPE_CANCER_MIN_SEVEN = "32";
    public static final String KEY_TYPE_CANCER_FOREIGNER = "33";
    public static final String KEY_TYPE_CANCER_NO_CARD = "34";


    private CardView personOutCardView;
    private CardView kidneyCardView;
    private CardView cancerPatientsCardView;
    private CardView checkIDcardCardView;
    private CardView transactionOfflineCardView;
    /**
     * Dialog Family
     */
    private Dialog dialogMenuFamily;
    //    private Button familyBtn; //K.GAME Big change dialog UI
//    private Button minSevenBtn;//K.GAME Big change dialog UI
//    private Button foreignerBtn;//K.GAME Big change dialog UI
//    private Button noCardBtn;//K.GAME Big change dialog UI
//    private ImageView closeImage;//K.GAME Big change dialog UI
    private CardView familyBtn;//K.GAME Big change dialog UI
    private CardView minSevenBtn;//K.GAME Big change dialog UI
    private CardView foreignerBtn;//K.GAME Big change dialog UI
    private CardView noCardBtn;//K.GAME Big change dialog UI
    private FrameLayout closeImage;//K.GAME Big change dialog UI

    private String statusSale;
    private Dialog dialogForeigner;
    //K.GAME 180827 To use for offline transactions // change UI
    private Dialog dialogPassword;
    private EditText passwordBox;
    public static final String KEY_TYPE_OFFLINE = "key_type_offline";
    //END K.GAME 180827 To use for offline transactions // change UI
    private TextView dialogTitleLabel;
    private TextView dialogTitleLabel2;
    private EditText userInputDialogEt;//K.GAME Edit text
    private TextView titleLabe001; //K.GAME 180911 Intent Text  to title
    private String titleLabel_text; //K.GAME 180911 Intent Text  to title
    private Button okBtn;
    private Button cancelBtn;
    private int pos;
    private InputMethodManager imm;
    private final String TAG = "MedicalTreatment";
    public static final String KEY_ID_CARD_CD = MedicalTreatmentActivity.class.getName() + "_key_id_card_cd";

    //K.GAME 180904 Add Calculate
    private FrameLayout oneClickFrameLayout = null;
    private FrameLayout twoClickFrameLayout = null;
    private FrameLayout threeClickFrameLayout = null;
    private FrameLayout fourClickFrameLayout = null;
    private FrameLayout fiveClickFrameLayout = null;
    private FrameLayout sixClickFrameLayout = null;
    private FrameLayout sevenClickFrameLayout = null;
    private FrameLayout eightClickFrameLayout = null;
    private FrameLayout nineClickFrameLayout = null;
    private FrameLayout zeroClickFrameLayout = null;

    private FrameLayout deleteClickFrameLayout = null;
    private String numberPrice = "";
    private LinearLayout numberLinearLayout;

    //
//    private String numberIdcard_cal;
    //END K.GAME 180904 Add Calculate
    private Realm realm = null;
    private String[] mBlockDataSend;
    private String TPDU;
    private ReversalHealthCare reversalHealthCare;

    private boolean isOffline = false;

    /**
     * InterfacePos
     */
    private PosInterfaceActivity posInterfaceActivity;
    private String typeInterface;
    private String amountInterface;
    private String idcardInterface;//K.GAME 180904 อาจเกิดบัค Add Big dialog
    private String cardIdChild;
    private String foreignerIdInterfacce;

    private int inCntItem;
    private CardId cardId = null;       // Paul_20180704

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_treatment);
        realm = Realm.getDefaultInstance();
        cardId = new CardId();
        initData();
        initWidget();
        initWidgetToolbar();
        setTitleToolbar("ใช้สิทธิ์รักษาพยาบาล");
//        customDialogMenuFamily(titleLabel_text);
        customDialogKeyInForeigner();
        if (typeInterface != null) {
            if (statusSale.substring(1).equalsIgnoreCase("2")) {
                dialogTitleLabel.setText("สิทธิบุตร 0 - 7 ปี");
                dialogTitleLabel2.setText("ระบุหมายเลขบัตรประชาชน\nบุตร และกดตกลง");
                userInputDialogEt.setText("");
                pos = userInputDialogEt.getText().length();
                userInputDialogEt.setSelection(pos);
                userInputDialogEt.requestFocus();
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                if(dialogForeigner!=null)
//                dialogForeigner.show();
                try {//20180724 SINN  Activity has leaked.
                    dialogForeigner.show();
                } catch (Exception e) {
                    dialogForeigner.dismiss();
                }
            } else if (statusSale.substring(1).equalsIgnoreCase("3")) {
                userInputDialogEt.setText("");
//                userInputDialogEt.setText("B");
                dialogTitleLabel.setText("สิทธิบุคคลต่างชาติ");
                dialogTitleLabel2.setText("ระบุหมายเลขสิทธิ และกดตกลง");//K.GAME 180912 Title02
                pos = userInputDialogEt.getText().length();
                userInputDialogEt.setSelection(pos);
                userInputDialogEt.requestFocus();
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                userInputDialogEt.setText("");
//                dialogTitleLabel.setText("ไม่มีบัตร / บัตรเสีย");
                dialogTitleLabel.setText("ไม่สามารถใช้บัตรได้");    //20180720 SINN Change GHC word no.4
                dialogTitleLabel2.setText("ระบุหมายเลขบัตรประชาชน\nและกดตกลง");//K.GAME 180912 Title02
                pos = userInputDialogEt.getText().length();
                userInputDialogEt.setSelection(pos);
                userInputDialogEt.requestFocus();
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
//            if(dialogForeigner!=null)
//            dialogForeigner.show();
            try {//20180724 SINN  Activity has leaked.
                dialogForeigner.show();
            } catch (Exception e) {
                dialogForeigner.dismiss();
            }
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isOffline = bundle.getBoolean(KEY_TYPE_OFFLINE);
            if (bundle.getString(KEY_TYPE_INTERFACE) != null) {
                typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
                amountInterface = bundle.getString(KEY_INTERFACE_AMOUNT);
                statusSale = bundle.getString(MedicalTreatmentActivity.KEY_STATUS_SALE);
                cardIdChild = bundle.getString(KEY_INTERFACE_CARD_ID_CHILD);
            }
        }
        posInterfaceActivity = MainApplication.getPosInterfaceActivity();
    }

    @Override
    public void initWidget() {
        //K.GAME Add Calculate
//        oneClickFrameLayout = findViewById(R.id.oneClickFrameLayout);
//        twoClickFrameLayout = findViewById(R.id.twoClickFrameLayout);
//        threeClickFrameLayout = findViewById(R.id.threeClickFrameLayout);
//        fourClickFrameLayout = findViewById(R.id.fourClickFrameLayout);
//        fiveClickFrameLayout = findViewById(R.id.fiveClickFrameLayout);
//        sixClickFrameLayout = findViewById(R.id.sixClickFrameLayout);
//        sevenClickFrameLayout = findViewById(R.id.sevenClickFrameLayout);
//        eightClickFrameLayout = findViewById(R.id.eightClickFrameLayout);
//        nineClickFrameLayout = findViewById(R.id.nineClickFrameLayout);
//        zeroClickFrameLayout = findViewById(R.id.zeroClickFrameLayout);
//        dotClickFrameLayout = findViewById(R.id.dotClickFrameLayout);
//
//        deleteClickFrameLayout = findViewById(R.id.deleteClickFrameLayout);
        //END K.GAME Add Calculate


//        super.initWidget();
        personOutCardView = findViewById(R.id.personOutCardView);
        kidneyCardView = findViewById(R.id.kidneyCardView);
        cancerPatientsCardView = findViewById(R.id.cancerPatientsCardView);
        checkIDcardCardView = findViewById(R.id.checkIDcardCardView); //K.GAME 180827 change UI
        transactionOfflineCardView = findViewById(R.id.transactionOfflineCardView); //K.GAME 180827 change UI
//        titleLabe001 = findViewById(R.id.titleLabe001); //K.GAME 180911 intent title

        TextView personOutCardViewTxt = findViewById(R.id.personOutCardViewTxt);
        TextView kidneyCardViewTxt = findViewById(R.id.kidneyCardViewTxt);
        TextView cancerPatientsCardViewTxt = findViewById(R.id.cancerPatientsCardViewTxt);

        inCntItem = 0;
        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_GHC_ENABLE).substring(0, 1).equalsIgnoreCase("1")) {
            inCntItem++;
            personOutCardViewTxt.setText(String.valueOf(inCntItem) + ") " + "ผู้ป่วยนอกทั่วไป");
            // personOutCardView.setVisibility(View.INVISIBLE);
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_GHC_ENABLE).substring(1, 2).equalsIgnoreCase("1")) {
            inCntItem++;
            kidneyCardViewTxt.setText(String.valueOf(inCntItem) + ") " + "หน่วย\nไตเทียม");
            // kidneyCardView.setVisibility(View.INVISIBLE);
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_GHC_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {
            inCntItem++;
            cancerPatientsCardViewTxt.setText(String.valueOf(inCntItem) + ") " + "หน่วย\nรังสีผู้เป็น\nมะเร็ง");
            // cancerPatientsCardView.setVisibility(View.INVISIBLE);
        }


        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_GHC_ENABLE).substring(0, 1).equalsIgnoreCase("0")) {
            personOutCardView.setVisibility(View.GONE);
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_GHC_ENABLE).substring(1, 2).equalsIgnoreCase("0")) {
            kidneyCardView.setVisibility(View.GONE);
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_GHC_ENABLE).substring(2, 3).equalsIgnoreCase("0")) {
            cancerPatientsCardView.setVisibility(View.GONE);
        }


        personOutCardView.setOnClickListener(this);
        kidneyCardView.setOnClickListener(this);
        cancerPatientsCardView.setOnClickListener(this);
        checkIDcardCardView.setOnClickListener(this);//K.GAME 180827 change UI
        transactionOfflineCardView.setOnClickListener(this);//K.GAME 180827 change UI
    }

    private void customDialogMenuFamily(String titleLabel_text) {
        dialogMenuFamily = new Dialog(MedicalTreatmentActivity.this);
        dialogMenuFamily.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMenuFamily.setCancelable(true);
        dialogMenuFamily.setContentView(R.layout.dialog_custom_family);
        dialogMenuFamily.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMenuFamily.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        familyBtn = dialogMenuFamily.findViewById(R.id.familyBtn);
        minSevenBtn = dialogMenuFamily.findViewById(R.id.minSevenBtn);
        foreignerBtn = dialogMenuFamily.findViewById(R.id.foreignerBtn);
        noCardBtn = dialogMenuFamily.findViewById(R.id.noCardBtn);
        closeImage = dialogMenuFamily.findViewById(R.id.closeImage);
        titleLabe001 = dialogMenuFamily.findViewById(R.id.titleLabe001);

//        Log.d("1919",titleLabel_text.toString());
        if (!titleLabel_text.equals(null)) {
//        titleLabel_text = "testtest";
            titleLabe001.setText(titleLabel_text.toString());//K.GAME 180911 Text Title
        }

//        if (statusSale.equals("1")) {
//            titleLabel.setText("1");//K.GAME 180911 Text Title
//        }
//        if (statusSale.equals("2")) {
//            titleLabel.setText("2");//K.GAME 180911 Text Title
//        }
//        if (statusSale.equals("3")) {
//            titleLabel.setText("3");//K.GAME 180911 Text Title
//        }

        familyBtn.setOnClickListener(this);
        minSevenBtn.setOnClickListener(this);
        foreignerBtn.setOnClickListener(this);
        noCardBtn.setOnClickListener(this);
        closeImage.setOnClickListener(this);
    }

    private void customDialogKeyInForeigner() {
        dialogForeigner = new Dialog(MedicalTreatmentActivity.this);
        dialogForeigner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogForeigner.setCancelable(false);
        dialogForeigner.setContentView(R.layout.dialog_key_in_foreigner);
        dialogForeigner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogForeigner.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        dialogTitleLabel = dialogForeigner.findViewById(R.id.dialogTitleLabel);
        dialogTitleLabel2 = dialogForeigner.findViewById(R.id.dialogTitleLabel2);
        userInputDialogEt = dialogForeigner.findViewById(R.id.userInputDialogEt);

        dialogForeigner.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//K.GAME 180905 ปิดคีร์บอร์ด
        userInputDialogEt.setShowSoftInputOnFocus(false);//K.GAME 180905 ทำให้กดคีร์บอร์ดที่ Edit text ไม่ได้
        userInputDialogEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        userInputDialogEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
//                    if (statusSale.substring(1).equalsIgnoreCase("3")) {
//                        userInputDialogEt.setText("B");
//                        int pos = userInputDialogEt.getText().length();
//                        userInputDialogEt.setSelection(pos);
//                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        okBtn = dialogForeigner.findViewById(R.id.okBtn);
        cancelBtn = dialogForeigner.findViewById(R.id.cancelBtn);
        //------------------------------------------------------------------------------------------------
        oneClickFrameLayout = dialogForeigner.findViewById(R.id.oneClickFrameLayout);
        twoClickFrameLayout = dialogForeigner.findViewById(R.id.twoClickFrameLayout);
        threeClickFrameLayout = dialogForeigner.findViewById(R.id.threeClickFrameLayout);
        fourClickFrameLayout = dialogForeigner.findViewById(R.id.fourClickFrameLayout);
        fiveClickFrameLayout = dialogForeigner.findViewById(R.id.fiveClickFrameLayout);
        sixClickFrameLayout = dialogForeigner.findViewById(R.id.sixClickFrameLayout);
        sevenClickFrameLayout = dialogForeigner.findViewById(R.id.sevenClickFrameLayout);
        eightClickFrameLayout = dialogForeigner.findViewById(R.id.eightClickFrameLayout);
        nineClickFrameLayout = dialogForeigner.findViewById(R.id.nineClickFrameLayout);
        zeroClickFrameLayout = dialogForeigner.findViewById(R.id.zeroClickFrameLayout);
        deleteClickFrameLayout = dialogForeigner.findViewById(R.id.deleteClickFrameLayout);

        numberLinearLayout = dialogForeigner.findViewById(R.id.numberLinearLayout_test);

//
//        if (dialogTitleLabel.getText().toString().equals("สิทธิบุคคลต่างชาติ")) {
//            numberPrice = "B";//K.GAME 180907 แก้บัค ไม่มี B ที่บัตรต่างชาติ
//        }
//

        if (typeInterface != null) {

        } else {
            View view = null;
            for (int i = 0; i < numberLinearLayout.getChildCount(); i++) {
                view = numberLinearLayout.getChildAt(i);
                view.setEnabled(true);

            }
            //  clickCal(view);
        }
        oneClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        twoClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        threeClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        fourClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        fiveClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        sixClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        sevenClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        eightClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        nineClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        zeroClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCal(v);
            }
        });
        deleteClickFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!numberPrice.toString().equalsIgnoreCase("B")) {
                    clickCal(v);
                }
            }
        });


        //------------------------------------------------------------------------------------------------
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidingKeyboard(userInputDialogEt);
                System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX01 statusSale = %s \n", statusSale);
                if (typeInterface == null) {
                    if (!statusSale.substring(1).equalsIgnoreCase("2")) {
                        if (!isOffline) {
                            System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX02 statusSale = %s \n", statusSale);
                            Intent intent = new Intent(MedicalTreatmentActivity.this, CalculateHelthCareActivityNew.class);
                            intent.putExtra(KEY_STATUS_SALE, statusSale);
                            intent.putExtra(KEY_ID_FOREIGNER_NUMBER, userInputDialogEt.getText().toString());
                            startActivity(intent);
                        } else {
                            System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX03 statusSale = %s \n", statusSale);
// Paul_20180705
/*
                            Intent intent = new Intent(MedicalTreatmentActivity.this, IDActivity.class);
                            intent.putExtra(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                            intent.putExtra(KEY_STATUS_SALE, statusSale);
                            intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                            startActivity(intent);
*/
                            Intent intent = new Intent(MedicalTreatmentActivity.this, CalculateHelthCareOfflineActivity.class);
                            Bundle bundle = new Bundle();
                            cardId = null;
                            bundle.putParcelable(IDActivity.KEY_CARD_ID_DATA, cardId);
                            bundle.putString(MedicalTreatmentActivity.KEY_STATUS_SALE, statusSale);
                            if (userInputDialogEt.getText().toString() != null)
                                bundle.putString(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                            bundle.putString(MedicalTreatmentActivity.KEY_ID_FOREIGNER_NUMBER, userInputDialogEt.getText().toString());
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }
                    } else {
                        System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX04 statusSale = %s \n", statusSale);
                        Intent intent = new Intent(MedicalTreatmentActivity.this, IDActivity.class);
                        intent.putExtra(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                        intent.putExtra(KEY_STATUS_SALE, statusSale);
                        intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                        // //SINN 20180911  Add topic msg.
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_GHC_TOPIC_MSG1, "สิทธิบุตร 0 - 7 ปี");
                        bundle.putString(KEY_GHC_TOPIC_MSG2, "สอด/รูดบัตรประชาชนผู้ดูแล");
                        intent.putExtras(bundle);


                        startActivity(intent);
                    }
                } else {
                    if (!statusSale.substring(1).equalsIgnoreCase("2")) {
                        System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX05 statusSale = %s \n", statusSale);
                        Intent intent = new Intent(MedicalTreatmentActivity.this, CalculateHelthCareActivityNew.class);
                        intent.putExtra(KEY_TYPE_INTERFACE, "Interface");
                        intent.putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                        intent.putExtra(KEY_STATUS_SALE, statusSale);
                        intent.putExtra(KEY_ID_FOREIGNER_NUMBER, userInputDialogEt.getText().toString());
                        startActivity(intent);
                    } else {
                        System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX06 statusSale = %s \n", statusSale);
                        Intent intent = new Intent(MedicalTreatmentActivity.this, IDActivity.class);
                        intent.putExtra(KEY_TYPE_INTERFACE, "Interface");
                        intent.putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                        intent.putExtra(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                        intent.putExtra(KEY_STATUS_SALE, statusSale);
                        intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                        startActivity(intent);
                    }
                }
                dialogForeigner.dismiss(); //K.GAME 180910 close dialog ปิดไดอะล็อคที่ค้าง ให้กลับไปหน้าแรก
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeInterface == null) {
                    hidingKeyboard(userInputDialogEt);
//                    if(dialogForeigner!=null)
//                    dialogForeigner.dismiss();
                    try {//20180724 SINN  Activity has leaked.
                        dialogForeigner.show();
                    } catch (Exception e) {
                        dialogForeigner.dismiss();
                    }
                    Log.d(TAG, "onClick: " + statusSale);
                    statusSale = statusSale.substring(0, 1);
                    Log.d(TAG, "onClick: " + statusSale);
                    finish();

                } else {
// Paul_20180717
                    TellToPosCancel();
                    posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                        @Override
                        public void success() {
                            hidingKeyboard(userInputDialogEt);
//                                if(dialogForeigner!=null)
//                                dialogForeigner.dismiss();
                            try {//20180724 SINN  Activity has leaked.
                                dialogForeigner.show();
                            } catch (Exception e) {
                                dialogForeigner.dismiss();
                            }
                            statusSale = statusSale.substring(0, 1);
                            Intent intent = new Intent(MedicalTreatmentActivity.this, MenuServiceListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
//                    if (typeInterface != null)
//                        TellToPosCancel();      // Sinn_20180711
                }
                dialogForeigner.dismiss(); //K.GAME 180910 close dialog ปิดไดอะล็อคที่ค้าง ให้กลับไปหน้าแรก
            }
        });


    }


    private void checkNumberPrice() {
        if (numberPrice.equalsIgnoreCase("0.00")) {
            numberPrice = "";
        }
    }

    private void clickCal(View v) {

        if (v == oneClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "1";
        } else if (v == twoClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "2";
        } else if (v == threeClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "3";
        } else if (v == fourClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "4";
        } else if (v == fiveClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "5";
        } else if (v == sixClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "6";
        } else if (v == sevenClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "7";
        } else if (v == eightClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "8";
        } else if (v == nineClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "9";
        } else if (v == zeroClickFrameLayout) {
            checkNumberPrice();
            numberPrice += "0";
        } else if (v == deleteClickFrameLayout) {
            if (!userInputDialogEt.getText().toString().equalsIgnoreCase("0.00")) {
                Log.d(TAG, "clickCal y: " + numberPrice);
                if (numberPrice.isEmpty()) {
                    Log.d(TAG, "clickCal u: " + numberPrice);
                    numberPrice = "";
                    userInputDialogEt.setText("0.00");
                    if (typeInterface != null)
                        userInputDialogEt.setText(numberPrice);
//                    if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
//                        priceLabel.setText(amountInterface);
//                        userInputDialogEt.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                } else {
                    try {
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        numberPrice = numberPrice.substring(0, numberPrice.length() - 1);
                        Log.d(TAG, "clickCal 1: " + numberPrice);
                        if (numberPrice.equalsIgnoreCase("") || numberPrice == null) {
                            Log.d(TAG, "clickCal: if");
                            numberPrice = "";
                            userInputDialogEt.setText("0.00");
                            if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
                                userInputDialogEt.setText(numberPrice);
//                                priceLabel.setText(amountInterface);
//                                priceLabel.setText(decimalFormat.format(Double.valueOf(amountInterface) / 100));
                        } else {
                            Log.d(TAG, "clickCal: else");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        } else if (v == okBtn) {
            //20180723 SINNN fixed double click.
            if (!numberPrice.isEmpty()) {
                okBtn.setEnabled(false);
            } else {
                {//K.GAME 180904 เปลี่ยนที่ว่าง
                    hidingKeyboard(userInputDialogEt);
                    System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX01 clickCal(View v) statusSale = %s \n", statusSale);
                    if (typeInterface == null) {
                        if (!statusSale.substring(1).equalsIgnoreCase("2")) {
                            if (!isOffline) {
                                System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX02 clickCal(View v) statusSale = %s \n", statusSale);
                                Intent intent = new Intent(MedicalTreatmentActivity.this, CalculateHelthCareActivityNew.class);
                                intent.putExtra(KEY_STATUS_SALE, statusSale);
                                intent.putExtra(KEY_ID_FOREIGNER_NUMBER, userInputDialogEt.getText().toString());
                                startActivity(intent);
                            } else {
                                System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX03 clickCal(View v) statusSale = %s \n", statusSale);
// Paul_20180705
/*
                            Intent intent = new Intent(MedicalTreatmentActivity.this, IDActivity.class);
                            intent.putExtra(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                            intent.putExtra(KEY_STATUS_SALE, statusSale);
                            intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                            startActivity(intent);
*/
                                Intent intent = new Intent(MedicalTreatmentActivity.this, CalculateHelthCareOfflineActivity.class);
                                Bundle bundle = new Bundle();
                                cardId = null;
                                bundle.putParcelable(IDActivity.KEY_CARD_ID_DATA, cardId);
                                bundle.putString(MedicalTreatmentActivity.KEY_STATUS_SALE, statusSale);
                                if (userInputDialogEt.getText().toString() != null)
                                    bundle.putString(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                                bundle.putString(MedicalTreatmentActivity.KEY_ID_FOREIGNER_NUMBER, userInputDialogEt.getText().toString());
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }
                        } else {
                            System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX04 clickCal(View v) statusSale = %s \n", statusSale);
                            Intent intent = new Intent(MedicalTreatmentActivity.this, IDActivity.class);
                            intent.putExtra(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                            intent.putExtra(KEY_STATUS_SALE, statusSale);
                            intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                            //// //SINN 20180911  Add topic msg.
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_GHC_TOPIC_MSG1, "สิทธิบุตร 0 - 7 ปี");
                            bundle.putString(KEY_GHC_TOPIC_MSG2, "สอด/รูดบัตรประชาชนผู้ดูแล");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } else {
                        if (!statusSale.substring(1).equalsIgnoreCase("2")) {
                            System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX05 clickCal(View v) statusSale = %s \n", statusSale);
                            Intent intent = new Intent(MedicalTreatmentActivity.this, CalculateHelthCareActivityNew.class);
                            intent.putExtra(KEY_TYPE_INTERFACE, "Interface");
                            intent.putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                            intent.putExtra(KEY_STATUS_SALE, statusSale);
                            intent.putExtra(KEY_ID_FOREIGNER_NUMBER, userInputDialogEt.getText().toString());
                            startActivity(intent);
                        } else {
                            System.out.printf("utility:: MedicalTreatmentActivity 000000000000XX06 clickCal(View v) statusSale = %s \n", statusSale);
                            Intent intent = new Intent(MedicalTreatmentActivity.this, IDActivity.class);
                            intent.putExtra(KEY_TYPE_INTERFACE, "Interface");
                            intent.putExtra(KEY_INTERFACE_AMOUNT, amountInterface);
                            intent.putExtra(KEY_ID_CARD_CD, userInputDialogEt.getText().toString());
                            intent.putExtra(KEY_STATUS_SALE, statusSale);
                            intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                            startActivity(intent);
                        }
                    }
                }
            }

        }

        Log.d(TAG, "numberPrice:" + numberPrice);
        userInputDialogEt.setText(numberPrice);
    }


    private void hidingKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void intentStartActivity() {
        Intent intent = new Intent(MedicalTreatmentActivity.this, IDActivity.class);
        intent.putExtra(KEY_STATUS_SALE, statusSale);
        intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
        //// //SINN 20180911  Add topic msg.
        Bundle bundle = new Bundle();
        bundle.putString(KEY_GHC_TOPIC_MSG1, "ใช้สิทธิจ่ายตรงสิทธิตนเองและครอบครัว");
        bundle.putString(KEY_GHC_TOPIC_MSG2, "สอดบัตรประชาชน");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (!checkReversal()) {
            switch (v.getId()) {
                case R.id.personOutCardView:
                    statusSale = "1";
//                    if(dialogMenuFamily!=null)
//                    dialogMenuFamily.show();
                    //20180724 SINN  Activity has leaked.
                    try {
                        titleLabel_text = "ผู้ป่วยนอกทั่วไป";
                        customDialogMenuFamily(titleLabel_text);
                        dialogMenuFamily.show();
                    } catch (Exception e) {
                        dialogMenuFamily.dismiss();
                    }
                    break;
                case R.id.kidneyCardView:
                    statusSale = "2";
//                    if(dialogMenuFamily!=null)
//                    dialogMenuFamily.show();
                    try {
                        titleLabel_text = "หน่วยไตเทียม";
                        customDialogMenuFamily(titleLabel_text);
                        dialogMenuFamily.show();
                    } catch (Exception e) {
                        dialogMenuFamily.dismiss();
                    }
                    break;
                case R.id.cancerPatientsCardView:
                    statusSale = "3";
//                    if(dialogMenuFamily!=null)
//                    dialogMenuFamily.show();
                    try {
                        titleLabel_text = "หน่วยรังสีผู้เป็นมะเร็ง";
                        customDialogMenuFamily(titleLabel_text);
                        dialogMenuFamily.show();
                    } catch (Exception e) {
                        dialogMenuFamily.dismiss();
                    }
                    break;
                //K.GAME 180827 add Button CardView
                case R.id.checkIDcardCardView:
                    Toast.makeText(this, "ตรวจสอบบัตรประชาชน", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(MedicalTreatmentActivity.this, IDActivity2.class);
                    startActivity(intent2);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.transactionOfflineCardView:
                    Toast.makeText(this, "ทำรายการออฟไลน์", Toast.LENGTH_SHORT).show();
                    customDialogPassword();
                    dialogPassword.show();
                    break;
                //END K.GAME 180827 add Button CardView
                /**
                 * Click ฺButton Dialog
                 */
                case R.id.familyBtn:
                    statusSale += "1";
                    dialogMenuFamily.dismiss(); //K.GAME 180910 close dialog ปิดไดอะล็อคที่ค้าง ให้กลับไปหน้าแรก
                    intentStartActivity();
                    break;
                case R.id.minSevenBtn:
                    statusSale += "2";
                    userInputDialogEt.setText("");
                    dialogTitleLabel.setText("สิทธิบุตร 0 - 7 ปี");
                    dialogTitleLabel2.setText("ระบุหมายเลขบัตรประชาชน\nบุตร และกดตกลง");//K.GAME 180912 Title02
                    pos = userInputDialogEt.getText().length();
                    userInputDialogEt.setSelection(pos);
                    userInputDialogEt.requestFocus();
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                    if(dialogForeigner!=null)
//                    dialogForeigner.show();
                    try {
                        dialogForeigner.show();
                    } catch (Exception e) {
                        dialogForeigner.dismiss();
                    }
                    dialogMenuFamily.dismiss(); //K.GAME 180910 close dialog ปิดไดอะล็อคที่ค้าง ให้กลับไปหน้าแรก
                    break;
                case R.id.foreignerBtn:
                    statusSale += "3";
                    userInputDialogEt.setText("");
                    userInputDialogEt.setText("B");
                    numberPrice = "B";
                    dialogTitleLabel.setText("สิทธิบุคคลต่างชาติ");
                    dialogTitleLabel2.setText("ระบุหมายเลขสิทธิ และกดตกลง");//K.GAME 180912 Title02
                    pos = userInputDialogEt.getText().length();
                    userInputDialogEt.setSelection(pos);
                    userInputDialogEt.requestFocus();
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                    if(dialogForeigner!=null)
//                    dialogForeigner.show();
                    try {
                        if (dialogForeigner != null)
                            dialogForeigner.show();
                    } catch (Exception e) {
                        dialogForeigner.dismiss();
                    }
                    dialogMenuFamily.dismiss(); //K.GAME 180910 close dialog ปิดไดอะล็อคที่ค้าง ให้กลับไปหน้าแรก
                    break;
                case R.id.noCardBtn:
                    statusSale += "4";
                    userInputDialogEt.setText("");
//                    dialogTitleLabel.setText("ไม่มีบัตร / บัตรเสีย");
                    dialogTitleLabel.setText("ไม่สามารถใช้บัตรได้");  //20180720 SINN Change GHC word no.4
                    dialogTitleLabel2.setText("ระบุหมายเลขบัตรประชาชน\nและกดตกลง");//K.GAME 180912 Title02
                    pos = userInputDialogEt.getText().length();
                    userInputDialogEt.setSelection(pos);
                    userInputDialogEt.requestFocus();
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                    if(dialogForeigner!=null)
//                    dialogForeigner.show();
                    try {
                        dialogForeigner.show();
                    } catch (Exception e) {
                        dialogForeigner.dismiss();
                    }
                    dialogMenuFamily.dismiss(); //K.GAME 180910 close dialog ปิดไดอะล็อคที่ค้าง ให้กลับไปหน้าแรก
                    break;
                case R.id.closeImage:
                    dialogMenuFamily.dismiss();
                    break;
            }
        } else {
            sendDataReversal(reversalHealthCare);
        }
    }

    /*
// Paul_20180718
    public void TellToPosCancel() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        DateFormat timeFormat = new SimpleDateFormat("HHmmss");
        posInterfaceActivity.PosInterfaceWriteField("02", posInterfaceActivity.ResponseMsgPosInterface("ND"));   // Response Message
        posInterfaceActivity.PosInterfaceWriteField("D0", "                                                                     ");   //
        posInterfaceActivity.PosInterfaceWriteField("16", Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        posInterfaceActivity.PosInterfaceWriteField("D1", Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        posInterfaceActivity.PosInterfaceWriteField("03", dateFormat.format(date));   // Date YYMMDD
        posInterfaceActivity.PosInterfaceWriteField("04", timeFormat.format(date));   // Time HHMMSS
//        PosInterfaceWriteField("30","");   // Card No
        posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode, "ND");
    }
*/
    public void TellToPosCancel() {
        posInterfaceActivity.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("ND"));   // Response Message
        posInterfaceActivity.PosInterfaceSendMessage(posInterfaceActivity.PosInterfaceTransactionCode, "ND");
    }

    private boolean checkReversal() {

        reversalHealthCare = realm.where(ReversalHealthCare.class).findFirst();
        return reversalHealthCare != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //20180724 SINN  Activity has leaked.


    }

    private void sendDataReversal(ReversalHealthCare reversalHealthCare) {
        mBlockDataSend = new String[64];
        mBlockDataSend[2 - 1] = reversalHealthCare.getDe2();
        if (reversalHealthCare.getType().equalsIgnoreCase("SALE")) {
            mBlockDataSend[3 - 1] = "005000";
        } else {
            mBlockDataSend[3 - 1] = "025000";
        }
        mBlockDataSend[4 - 1] = reversalHealthCare.getDe4();
        mBlockDataSend[11 - 1] = reversalHealthCare.getDe11();
        mBlockDataSend[22 - 1] = reversalHealthCare.getDe22();
        mBlockDataSend[24 - 1] = reversalHealthCare.getDe24();
        mBlockDataSend[25 - 1] = reversalHealthCare.getDe25();
        mBlockDataSend[35 - 1] = reversalHealthCare.getDe35();
        mBlockDataSend[41 - 1] = reversalHealthCare.getDe41();
        mBlockDataSend[42 - 1] = reversalHealthCare.getDe42();
        if (reversalHealthCare.getDe52() != null) {
            mBlockDataSend[52 - 1] = reversalHealthCare.getDe52();
        }
        mBlockDataSend[62 - 1] = reversalHealthCare.getDe62();
        mBlockDataSend[63 - 1] = reversalHealthCare.getDe63();
        TPDU = CardPrefix.getTPDU(MedicalTreatmentActivity.this, "GHC");
        Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC)) + 1));
        packageAndSend(TPDU, "0400", mBlockDataSend);
    }


    private void removeReversal() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(ReversalHealthCare.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: removeReversal ");
            }
        });
    }

    @Override
    protected void connectTimeOut() {
        System.out.printf("utility:: MedicalTreatmentActivity connectTimeOut \n");
//        Utility.customDialogAlert(this, "connectTimeOut", new Utility.OnClickCloseImage() {//K.GAME 180911 dialog alert full screen
        Utility.customDialogAlert_gotomain(this, "connectTimeOut", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(MedicalTreatmentActivity.this, MenuServiceListActivity.class);
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
    protected void transactionTimeOut() {
        System.out.printf("utility:: MedicalTreatmentActivity transactionTimeOut error \n");
//        Utility.customDialogAlert(this, "transactionTimeOut", new Utility.OnClickCloseImage() {//K.GAME 180911 dialog alert full screen
        Utility.customDialogAlert_gotomain(this, "transactionTimeOut", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(MedicalTreatmentActivity.this, MenuServiceListActivity.class);
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
    protected void received(String[] data) {
        System.out.printf("utility:: %s received \n", TAG);
        if (BlockCalculateUtil.hexToString(data[39 - 1]).equalsIgnoreCase("00")) {
            removeReversal();
            Utility.customDialogAlertSuccess(this, "Revesal สำเร็จ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else {
//            Utility.customDialogAlert(this, "error : " + BlockCalculateUtil.hexToString(data[39 - 1]), new Utility.OnClickCloseImage() {

//            Utility.customDialogAlert(this, "TimeOut", new Utility.OnClickCloseImage() {//K.GAME 180911 dialog alert full screen
            Utility.customDialogAlert_gotomain(this, "TimeOut", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
// Paul_20180809
                    Intent intent = new Intent(MedicalTreatmentActivity.this, MenuServiceListActivity.class);
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
    }

    @Override
    protected void error(String error) {
        System.out.printf("utility:: MedicalTreatmentActivity error \n");
//        Utility.customDialogAlert(this, "error 003", new Utility.OnClickCloseImage() {//K.GAME 180911 dialog alert full screen
        Utility.customDialogAlert_gotomain(this, "error 003", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(MedicalTreatmentActivity.this, MenuServiceListActivity.class);
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
        System.out.printf("utility:: MedicalTreatmentActivity other \n");
//        Utility.customDialogAlert(this, "other", new Utility.OnClickCloseImage() {//K.GAME 180911 dialog alert full screen
        Utility.customDialogAlert_gotomain(this, "other", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(MedicalTreatmentActivity.this, MenuServiceListActivity.class);
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
    protected void onResume() {
        super.onResume();
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }


    }

    @Override
    protected void onPause() {
        System.out.printf("utility:: MedicalTreatmentActivity onPause \n");
        super.onPause();
        realm.close();
        realm = null;   // Paul_20181026 Some time DB Read error solved
    }

    @Override
    public void onBackPressed() {
        System.out.printf("utility:: MedicalTreatmentActivity onBackPressed \n");
        finish();
    }

    private void customDialogPassword() { //K.GAME 180827 Add Class // To use for offline transactions
        dialogPassword = new Dialog(MedicalTreatmentActivity.this, R.style.ThemeWithCorners);//K.GAME 180828 change dialog UI
        View view = dialogPassword.getLayoutInflater().inflate(R.layout.dialog_custom_input_password, null);//K.GAME 180828 change dialog UI
        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180828 change dialog UI
        dialogPassword.setContentView(view);//K.GAME 180828 change dialog UI
        dialogPassword.setCancelable(false);//K.GAME 180828 change dialog UI

//        dialogPassword = new Dialog(MedicalTreatmentActivity.this);
//        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogPassword.setContentView(R.layout.dialog_custom_input_password);
//        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialogPassword.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passwordBox = dialogPassword.findViewById(R.id.passwordBox);
        okBtn = dialogPassword.findViewById(R.id.okBtn);
        cancelBtn = dialogPassword.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordBox.getText().toString().isEmpty()) {
                    passwordBox.setError("กรุณาใส่รหัสผ่าน");
                } else {
//                    if (!isOffline) {
//                        if (Preference.getInstance(MedicalTreatmentActivity.this).getValueString(Preference.KEY_ADMIN_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
//                            Intent intent = new Intent(MedicalTreatmentActivity.this, SettingActivity.class);
//                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_ADMIN_PASSWORD);
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
//                            dialogPassword.dismiss();
//                        } else if (Preference.getInstance(MedicalTreatmentActivity.this).getValueString(Preference.KEY_NORMAL_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
//                            Intent intent = new Intent(MedicalTreatmentActivity.this, SettingActivity.class);
//                            intent.putExtra(KEY_TYPE_PASSWORD, TYPE_NORMAL_PASSWORD);
//                            startActivity(intent);
//                            overridePendingTransition(0, 0);
//                            dialogPassword.dismiss();
//                        } else {
//                            passwordBox.setError("รหัสผิดพลาด");
//                        }
//                    } else {
                    if (Preference.getInstance(MedicalTreatmentActivity.this).getValueString(Preference.KEY_OFFLINE_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
// Paul_20180713
                        finish();//K.GAME 180827
                        Toast.makeText(MedicalTreatmentActivity.this, "Offline mode", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MedicalTreatmentActivity.this, MedicalTreatmentActivity.class);
                        intent.putExtra(KEY_TYPE_OFFLINE, isOffline);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        dialogPassword.dismiss();
                    } else {
                        passwordBox.setError("รหัสผิดพลาด");
                    }
                }
            }

//            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPassword.dismiss();
            }
        });

        dialogPassword.show();
    }

//    private void checkNumberPrice() {
//        if (numberIdcard_cal.equalsIgnoreCase("0.00")) {
//            numberIdcard_cal = "";
//        }
//    }

//    private void clickCal(View v) {
//
//        if (v == oneClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "1";
//        } else if (v == twoClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "2";
//        } else if (v == threeClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "3";
//        } else if (v == fourClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "4";
//        } else if (v == fiveClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "5";
//        } else if (v == sixClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "6";
//        } else if (v == sevenClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "7";
//        } else if (v == eightClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "8";
//        } else if (v == nineClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "9";
//        } else if (v == zeroClickFrameLayout) {
//            checkNumberPrice();
//            numberIdcard_cal += "0";
//        } else if (v == dotClickFrameLayout) {
//            checkNumberPrice();
//            if (!numberIdcard_cal.isEmpty()) {
//                if (!numberIdcard_cal.contains(".")) {
//                    numberIdcard_cal += ".";
//                }
//            } else {
//                numberIdcard_cal += "0.";
//            }
//        } else if (v == cancelBtn) {
//            Log.d(TAG, "clickCal exitClickFrameLayout");
////            cardManager.abortPBOCProcess();
////            finish();
//            ////20180724 SINN cannot cancel by manual
//            if (typeInterface != null) {
////                TellToPosError("ND");
//                posInterfaceActivity.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
//                    @Override
//                    public void success() {
////                        dialogCardError.dismiss(); แดง
//                        Intent intent = new Intent(MedicalTreatmentActivity.this, MenuServiceListActivity.class); // Paul_20180704
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(0, 0);
//                    }
//                });
//            } else {
//                finish();
//            }
//
//
//        } else if (v == deleteClickFrameLayout) {
//            if (!userInputDialogEt.getText().toString().equalsIgnoreCase("0.00")) {
//                Log.d(TAG, "clickCal y: " + numberIdcard_cal);
//                if (numberIdcard_cal.isEmpty()) {
//                    Log.d(TAG, "clickCal u: " + numberIdcard_cal);
//                    numberIdcard_cal = "";
//                    userInputDialogEt.setText("0.00");
//                    if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
////                        priceLabel.setText(amountInterface);
////                        priceLabel.setText(decFormat.format(Double.valueOf(amountInterface) / 100));
//                        userInputDialogEt.setText(idcardInterface);
//                    userInputDialogEt.setText(numberIdcard_cal);
//
//                } else {
//                    try {
//                        Log.d(TAG, "clickCal 1: " + numberIdcard_cal);
//                        numberIdcard_cal = numberIdcard_cal.substring(0, numberIdcard_cal.length() - 1);
//                        Log.d(TAG, "clickCal 1: " + numberIdcard_cal);
//                        if (numberIdcard_cal.equalsIgnoreCase("") || numberIdcard_cal == null) {
//                            Log.d(TAG, "clickCal: if");
//                            numberIdcard_cal = "";
//                            userInputDialogEt.setText("0.00");
//                            if (typeInterface != null)//SINN 20180712  SALE RS232 AMT , REF1 ,REF2 , REF3 not fill in box.
////                                priceLabel.setText(amountInterface);
////                                priceLabel.setText(decFormat.format(Double.valueOf(amountInterface) / 100));
//                                userInputDialogEt.setText(idcardInterface);
//                            userInputDialogEt.setText(numberIdcard_cal);
//                        } else {
//                            Log.d(TAG, "clickCal: else");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        } else if (v == okBtn) {
//            //20180723 SINNN fixed double click.
//            if (!numberIdcard_cal.isEmpty())
//                okBtn.setEnabled(false);
////            submitAmount();
//
//        }
//    }
}
