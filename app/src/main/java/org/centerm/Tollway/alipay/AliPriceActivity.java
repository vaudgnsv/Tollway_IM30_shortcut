package org.centerm.Tollway.alipay;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;

public class AliPriceActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView txt_name;
    private TextView txt_title;
    private TextView txt_subtitle;

    private Dialog dialogWarning = null;

    private CardManager cardManager = null;     // Paul_20181006

    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private TextView six;
    private TextView seven;
    private TextView eight;
    private TextView nine;
    private TextView zero;
    private ImageView del;
    private Button btn_next;

    private String tmp_amount;
    private String amount;
    private String walletcode;

    private String token;
    private String invoice;

    private String type;
    private boolean flag = false;
    private Intent intent;
    private Intent service;

    private String tmp;

    //Baht Unit
    private DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");
    private EditText edit_amount;
    private String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_price);   // Paul_20190206 xml change
        init();
        initWidget();
        customDialogWarning();

        Intent intent = getIntent();
        type = intent.getExtras().getString("TYPE");
        walletcode = intent.getExtras().getString("WALLET_CODE");

        if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")) {
            txt_name.setVisibility( View.VISIBLE );
            edit_amount.setText("0.00");
            edit_amount.setTextSize(42);
            change_editText(edit_amount);

            if(type.equals( "ALI_SALE" ))
                txt_name.setText( "ALIPAY" );
            else
                txt_name.setText( "WECHAT PAY" );       // Paul_20190323

        }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                txt_name.setVisibility( View.GONE );
                edit_amount.setText("");
                edit_amount.setTextSize(37);
                amount = intent.getExtras().getString("AMOUNT");
                walletcode = intent.getExtras().getString("WALLET_CODE");
                txt_title.setText("Payment Code");
                txt_subtitle.setText("กรุณากรอกหมายเลข payment code.");
        }else { //void
            edit_amount.setTextSize(42);
            txt_name.setVisibility( View.GONE );
            change_editText2(edit_amount);
            txt_title.setText("Invoice");
            txt_subtitle.setText("กรุณากรอก Invoice no. และกดตกลง");
        }
    }

//    @Override
    public void initWidget() {
        cardManager = MainApplication.getCardManager();
// Paul_20181022 Start   First Settlement Check
        String valueParameterEnable = Preference.getInstance(AliPriceActivity.this).getValueString(Preference.KEY_TAG_1000);   //para enable
        if (valueParameterEnable.isEmpty())
        {
            Utility.customDialogAlert(AliPriceActivity.this, "กรุณา First Settlement ก่อนทำรายการ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.txt_one:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 1);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 1);
                }else{
                        edit_amount.setText(tmp + 1);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_two:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 2);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 2);
                }else{
                        edit_amount.setText(tmp + 2);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_three:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 3);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 3);
                }else{
                        edit_amount.setText(tmp + 3);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_four:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 4);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 4);
                }else{
                        edit_amount.setText(tmp + 4);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_five:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 5);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 5);
                }else{
                        edit_amount.setText(tmp + 5);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_six:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 6);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 6);
                }else{
                        edit_amount.setText(tmp + 6);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_seven:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 7);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 7);
                }else{
                        edit_amount.setText(tmp + 7);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_eight:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 8);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 8);
                }else{
                        edit_amount.setText(tmp + 8);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_nine:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() < 16)
                        edit_amount.setText(tmp + 9);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 9);
                }else{
                        edit_amount.setText(tmp + 9);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.txt_zero:
                tmp = edit_amount.getText().toString();
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    if(tmp.length() > 0 && tmp.length() < 16)
                        edit_amount.setText(tmp + 0);
                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    if(tmp.length() < 18)
                        edit_amount.setText(tmp + 0);
                }else{
                        edit_amount.setText(tmp + 0);
                }
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.img_del:
                tmp = edit_amount.getText().toString();
                if(tmp.length() > 0 ){
                    tmp = tmp.substring(0,tmp.length()-1);
                }
                edit_amount.setText(tmp);
                edit_amount.setSelection(edit_amount.getText().length());
                break;
            case R.id.btn_next:
                if(type.equals("ALI_SALE") || type.equals("WECHAT_SALE")){
                    amount = edit_amount.getText().toString();
//                    tmp_amount = amount.toString().replaceAll(",", "");
//                    if(!tmp_amount.isEmpty()){
//                        Double basic = Double.parseDouble(tmp_amount);
//                    }
// Paul_20181002 Start
/*
                    if(basic < 0.3){
//                        Toast.makeText(AliPriceActivity.this, "Amount Empty !", Toast.LENGTH_SHORT).show();
                        dialogWarning.show();
                    }else{
                        service = new Intent(AliPriceActivity.this, AliComfirmActivity.class);
                        service.putExtra("AMOUNT", amount);
                        startActivity(service);
                        finish();
                        overridePendingTransition(0, 0);
                    }
*/
                    // Paul_20190205 x,xxx,xxx,xxx.xx
                    if (!Preference.getInstance(AliPriceActivity.this).getValueString(Preference.KEY_MAX_AMT).equals("0") && (Double.valueOf(amount.replaceAll(",","")) > Double.valueOf(Preference.getInstance(AliPriceActivity.this).getValueString(Preference.KEY_MAX_AMT)))) {
                        Utility.customDialogAlert(AliPriceActivity.this, "จำนวนเงินเกินกำหนด", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    } else
                    if(!amount.isEmpty() && !amount.equals("0.00")){
                        service = new Intent(AliPriceActivity.this, AliComfirmActivity.class);
                        service.putExtra("WALLET_CODE", type);
                        service.putExtra("AMOUNT", amount);
                        startActivity(service);
                        finish();
                        overridePendingTransition(0, 0);
                    }else{
                        Utility.customDialogAlert(AliPriceActivity.this, "กรุณากรอกจำนวนเงิน", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }

                }else if(type.equals(AliConfig.SubmitSale) || type.equals(AliConfig.Sale)){
                    token = edit_amount.getText().toString();
                        service = new Intent(AliPriceActivity.this, AliServiceActivity.class);
                        service.putExtra("TYPE", type);
                        service.putExtra("WALLET_CODE", walletcode);
                        service.putExtra("TOKEN", token);
                        service.putExtra("AMOUNT", amount);
                        startActivity(service);
                        finish();
                        Toast.makeText(AliPriceActivity.this,  "TOKEN : "+token, Toast.LENGTH_SHORT).show();
                }else{
                    invoice = edit_amount.getText().toString();
                    if(invoice.length() ==6){
                        service = new Intent(AliPriceActivity.this, AliVoidActivity.class);
                        service.putExtra("INVOICE", invoice);
                        startActivity(service);
                        finish();
                    }else
                        Toast.makeText(AliPriceActivity.this, "INVOICE Length Error !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void init() {
        txt_name = findViewById( R.id.txt_name );
        txt_title = findViewById( R.id.txt_title);
        txt_subtitle = findViewById( R.id.txt_subtitle);
        one = findViewById( R.id.txt_one);
        two = findViewById( R.id.txt_two);
        three = findViewById( R.id.txt_three);
        four = findViewById( R.id.txt_four);
        five = findViewById( R.id.txt_five);
        six = findViewById( R.id.txt_six);
        seven = findViewById( R.id.txt_seven);
        eight = findViewById( R.id.txt_eight);
        nine = findViewById( R.id.txt_nine);
        zero = findViewById( R.id.txt_zero);
        del = findViewById( R.id.img_del);

        btn_next = findViewById( R.id.btn_next);
        edit_amount = findViewById( R.id.edit_amount);

        edit_amount.setEnabled(false);
        edit_amount.setFocusable(false);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
        del.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    public static void change_editText(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean mEditing = false;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (!mEditing) {
                    mEditing = true;
                    String digits = s.toString().replaceAll("\\D", "");
                    DecimalFormat nf = new DecimalFormat("#,##0.00");
                    try {
                        String formatted = nf.format(Double.parseDouble(digits) / 100);
                        s.replace(0, s.length(), formatted);
                    } catch (NumberFormatException nfe) {
                        s.clear();
                    }
                    mEditing = false;
                }
            }
        });
    }

    public static void change_editText2(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean mEditing = false;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (!mEditing) {
                    mEditing = true;
                    String digits = s.toString().replaceAll("\\D", "");
                    DecimalFormat nf = new DecimalFormat("000000");
                    try {
                        String formatted = nf.format(Double.parseDouble(digits));
                        s.replace(0, s.length(), formatted);
                    } catch (NumberFormatException nfe) {
                        s.clear();
                    }
                    mEditing = false;
                }
            }
        });
    }

    private void customDialogWarning() {
        dialogWarning = new Dialog(this);
        dialogWarning.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWarning.setCancelable(false);
        dialogWarning.setContentView( R.layout.dialog_custom_aliwarning);
        dialogWarning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWarning.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button btn_ok;
        btn_ok = dialogWarning.findViewById( R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_amount.setText("0.00");
                dialogWarning.dismiss();
            }
        });
    }
}
