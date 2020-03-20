package org.centerm.Tollway.alipay;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centerm.Tollway.R;

public class AliComfirmActivity extends AppCompatActivity {

    private ImageView img_type;
    private ImageView img_type2;
    private Drawable img_ali;
    private Drawable img_ali2;
    private Drawable img_wechat;
    private Drawable img_wechat2;
    private TextView amountLabel;
    private Button cancelBtn;
    private LinearLayout saleLayout;
    private Intent intent;
    private String amount;
    private String walletcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_comfirm);

        init();
        clickListener();
    }


    private void init() {
        intent = getIntent();
        amount = intent.getExtras().getString("AMOUNT");
        walletcode = intent.getExtras().getString("WALLET_CODE");

        img_type = findViewById(R.id.img_type);
        img_type2 = findViewById(R.id.img_type2);
        img_ali =  getResources().getDrawable(R.drawable.ic_alipay);
        img_ali2 =  getResources().getDrawable(R.drawable.alipay_sale);
        img_wechat =  getResources().getDrawable(R.drawable.ic_wechat);
        img_wechat2 =  getResources().getDrawable(R.drawable.wechat_sale);
        amountLabel = findViewById( R.id.amountLabel);
        cancelBtn = findViewById( R.id.btn_cancel);
        saleLayout = findViewById( R.id.saleLayout);
        amountLabel.setText(amount);

        if(walletcode.equals("ALI_SALE")) {
            img_type.setImageDrawable(img_ali);
            img_type2.setImageResource(R.drawable.alipay_sale);
        }else {
            img_type.setImageDrawable(img_wechat);
            img_type2.setImageResource(R.drawable.wechat_sale);
        }
    }

    private void clickListener() {

        saleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent submitsale = new Intent(AliComfirmActivity.this, ScanQrActivity.class);
                submitsale.putExtra("WALLET_CODE", walletcode);
                submitsale.putExtra("TYPE", AliConfig.SubmitSale);
                submitsale.putExtra("AMOUNT", amount);
                startActivity(submitsale);
                finish();
                overridePendingTransition(0, 0);
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
}
