package org.centerm.land.activity.qr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.centerm.land.R;
import org.centerm.land.bassactivity.SettingToolbarActivity;

public class CheckQrActivity extends SettingToolbarActivity {

    private EditText traceBox =  null;
    private Button checkBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_qr);
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        traceBox = findViewById(R.id.traceBox);
        checkBtn = findViewById(R.id.checkBtn);
    }
}
