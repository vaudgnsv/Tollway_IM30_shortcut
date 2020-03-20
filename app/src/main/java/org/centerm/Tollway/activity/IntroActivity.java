package org.centerm.Tollway.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;

import org.centerm.Tollway.R;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.utility.Preference;

public class IntroActivity extends SettingToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
//        Preference.getInstance(IntroActivity.this).setValueString(Preference.KEY_BUS_LOGIN ,"DONE");
//        if (Preference.getInstance(IntroActivity.this).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {
//            Intent intent = new Intent(IntroActivity.this, MenuServiceListActivity.class);
//            if (Preference.getInstance(IntroActivity.this).getValueString(Preference.KEY_BUS_LOGIN).equals("DONE")) {
//                intent.putExtra("amount", "20.00");
//            } else {
//                intent.putExtra("step", 1);
//            }
//        }
        initWidget();

    }

    public void initWidget() {
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent;
                if( Preference.getInstance(IntroActivity.this).getValueString(Preference.KEY_BUS_LOGIN ).equals("ON")){
                    intent = new Intent(IntroActivity.this, MenuServiceListActivity.class);
                    intent.putExtra("InsertCard", true);
                }else {
                    intent = new Intent(IntroActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
