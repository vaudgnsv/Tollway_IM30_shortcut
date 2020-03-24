package org.centerm.Tollway.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.Log;

import org.centerm.Tollway.R;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.database.BL;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

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

        LoadBlData();

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


    private void LoadBlData() {
        Log.d("Intro" , "in LoadBlData");
        Realm realm = Realm.getDefaultInstance();
        final BL BLdatabase = new BL();
        File file = new File("/cache/customer/media/bl_data.txt") ;
        Log.d("INTRO", "is File " + file.isFile());


        RealmResults<BL> BLList = realm.where(BL.class).findAll();
        realm.beginTransaction();
        if(BLList.isEmpty()) {
            try {

                FileReader filereader = new FileReader(file);
                //입력 버퍼 생성
                BufferedReader bufReader = new BufferedReader(filereader);
                String line = "";
                int i = 0;
                while((line = bufReader.readLine()) != null){
                    Log.d("Intro" , "stat [" + line.substring(0,1) + "] pan [" + line.substring(1,20).trim() + "]");
                    BLdatabase.setId(i ++);
                    BLdatabase.setStatus(line.substring(0,1));
                    BLdatabase.setPAN(line.substring(1,20).trim());
                    realm.insert(BLdatabase);
                }
                //.readLine()은 끝에 개행문자를 읽지 않는다.
                bufReader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            realm.commitTransaction();
        }
        realm.close();
        realm = null;

    }


}
