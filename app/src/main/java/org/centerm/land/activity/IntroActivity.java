package org.centerm.land.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.centerm.land.R;
import org.centerm.land.bassactivity.SettingToolbarActivity;

public class IntroActivity extends SettingToolbarActivity {

    private final String TAG = "IntroActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initWidget();
    }

    public void initWidget() {
        super.initWidget();
        ImageView logoKTBImage = findViewById(R.id.logoKTBImage);
        logoKTBImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this,MenuServiceActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
    }
}
