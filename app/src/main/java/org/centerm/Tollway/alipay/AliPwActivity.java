package org.centerm.Tollway.alipay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.Tollway.R;

public class AliPwActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView pw1;
    private ImageView pw2;
    private ImageView pw3;
    private ImageView pw4;

    private TextView titleLabel;
    private TextView titleLabel2;
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

    private String pw = "";
    private String type ="";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_pw);

        init();
    }

    private void init() {
        titleLabel = findViewById( R.id.titleLabel);
        titleLabel2 = findViewById( R.id.titleLabel2);
        pw1 = findViewById( R.id.pw1);
        pw2 = findViewById( R.id.pw2);
        pw3 = findViewById( R.id.pw3);
        pw4 = findViewById( R.id.pw4);

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

        intent = getIntent();
        type = intent.getExtras().getString("TYPE");

        if(type.equals(AliConfig.Void)) {
            titleLabel.setText(" Void");
            titleLabel2.setText(" Please enter void password");
        }else if(type.equals(AliConfig.TransactionInquiry)) {
            titleLabel.setText(" Settlement");
            titleLabel2.setText(" Please enter settlement password");
        }else{
            titleLabel.setText(" Error");
            titleLabel2.setText(" Now Error routine");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.txt_one:
                if(pw.length() < 4)
                pw = pw+1;
                break;
            case R.id.txt_two:
                if(pw.length() < 4)
                    pw = pw+2;
                break;
            case R.id.txt_three:
                if(pw.length() < 4)
                    pw = pw+3;
                break;
            case R.id.txt_four:
                if(pw.length() < 4)
                    pw = pw+4;
                break;
            case R.id.txt_five:
                if(pw.length() < 4)
                    pw = pw+5;
                break;
            case R.id.txt_six:
                if(pw.length() < 4)
                    pw = pw+6;
                break;
            case R.id.txt_seven:
                if(pw.length() < 4)
                    pw = pw+7;
                break;
            case R.id.txt_eight:
                if(pw.length() < 4)
                    pw = pw+8;
                break;
            case R.id.txt_nine:
                if(pw.length() < 4)
                    pw = pw+9;
                break;
            case R.id.txt_zero:
                if(pw.length() < 4)
                    pw = pw+0;
                break;
            case R.id.img_del:
                if(pw.length() > 0 )
                    pw = pw.substring(0,pw.length()-1);
                break;
            case R.id.btn_next:
                if(type.equals(AliConfig.Void)){
                    if(pw.equals(AliConfig.void_pw)) {
                        Intent void_sale = new Intent(AliPwActivity.this, AliPriceActivity.class);
                        void_sale.putExtra("TYPE", AliConfig.Void);
                        startActivity(void_sale);
                        finish();
                        overridePendingTransition(0, 0);
                    }else{
                        Toast.makeText(AliPwActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                        pw = "";
                    }
                }else if(type.equals(AliConfig.TransactionInquiry)){
                    if(pw.equals(AliConfig.settlement_pw)) {
                        Intent void_sale = new Intent(AliPwActivity.this, AliSettlementActivity.class);
                        startActivity(void_sale);
                        finish();
                        overridePendingTransition(0, 0);
                    }else{
                        Toast.makeText(AliPwActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                        pw = "";
                    }
                }
                break;
        }

        check_lc();
    }

    private void check_lc() {
        if(pw.length() ==1) {
            pw1.setImageResource( R.drawable.pw_fill);
            pw2.setImageResource( R.drawable.pw);
            pw3.setImageResource( R.drawable.pw);
            pw4.setImageResource( R.drawable.pw);
        }else if(pw.length() ==2) {
            pw1.setImageResource( R.drawable.pw_fill);
            pw2.setImageResource( R.drawable.pw_fill);
            pw3.setImageResource( R.drawable.pw);
            pw4.setImageResource( R.drawable.pw);
        }else if(pw.length() ==3) {
            pw1.setImageResource( R.drawable.pw_fill);
            pw2.setImageResource( R.drawable.pw_fill);
            pw3.setImageResource( R.drawable.pw_fill);
            pw4.setImageResource( R.drawable.pw);
        }else if(pw.length() ==4) {
            pw1.setImageResource( R.drawable.pw_fill);
            pw2.setImageResource( R.drawable.pw_fill);
            pw3.setImageResource( R.drawable.pw_fill);
            pw4.setImageResource( R.drawable.pw_fill);
        }else{
            pw1.setImageResource( R.drawable.pw);
            pw2.setImageResource( R.drawable.pw);
            pw3.setImageResource( R.drawable.pw);
            pw4.setImageResource( R.drawable.pw);
        }
    }
}
