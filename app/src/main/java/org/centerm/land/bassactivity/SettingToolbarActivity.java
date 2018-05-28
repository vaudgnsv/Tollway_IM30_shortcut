package org.centerm.land.bassactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.centerm.land.R;
import org.centerm.land.activity.SettingActivity;

public class SettingToolbarActivity extends AppCompatActivity {
    private RelativeLayout settingRelativeLayout = null;
    private LinearLayout linearLayoutToolbarBottom = null;

    public void initWidget() {
        settingRelativeLayout = findViewById(R.id.settingRelativeLayout);
        settingRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingToolbarActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    public void initBtnExit() {
        linearLayoutToolbarBottom = findViewById(R.id.linearLayoutToolbarBottom);
        linearLayoutToolbarBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

}
