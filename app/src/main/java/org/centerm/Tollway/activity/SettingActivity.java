package org.centerm.Tollway.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.bassactivity.SettingToolbarActivity;
import org.centerm.Tollway.fragment.MenuServiceFragment;
import org.centerm.Tollway.fragment.setting.AlipaySettingFragment;
import org.centerm.Tollway.fragment.setting.MerchantFragment;
import org.centerm.Tollway.fragment.setting.NMXInfoFragment;
import org.centerm.Tollway.fragment.setting.QrSettingFragment;
import org.centerm.Tollway.fragment.setting.ServerInfoFragment;
import org.centerm.Tollway.fragment.setting.TerminalInfoFragment;
import org.centerm.Tollway.utility.Preference;

public class SettingActivity extends SettingToolbarActivity {
    private ViewPager settingViewPager = null;
    private SettingAdapter settingAdapter = null;
    private static String typePassword = null;

    private int cnt = 4;
    private boolean flag_gr = false;
    private boolean flag_ali = false;
    private boolean flag_wec = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.printf("utility:: SettingActivity onCreate \n");
        setContentView(R.layout.activity_setting);
        initData();
        initWidget();
        initBtnExit();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typePassword = bundle.getString(MenuServiceFragment.KEY_TYPE_PASSWORD);
        }
        if(Preference.getInstance(this).getValueString(Preference.KEY_APP_ENABLE).substring(1,2).equals("1")) {
            flag_gr = true;
            cnt = 5;
            if(Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_ID).equals("1") || Preference.getInstance(this).getValueString(Preference.KEY_WECHATPAY_ID).equals("1")) {
                flag_ali = true;
                cnt ++;
            }
        }
    }

    @Override
    public void initWidget() {
//        super.initWidget();

//SINN 20181102 change title to merchant name
            TextView app_title = findViewById(R.id.app_title);
            if (!Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1).isEmpty())
            app_title.setText(Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_1));
//SINN 20181102 change title to merchant name


        settingViewPager = findViewById(R.id.settingViewPager);
        settingAdapter = new SettingAdapter(getSupportFragmentManager());
        settingViewPager.setAdapter(settingAdapter);
        settingViewPager.setOffscreenPageLimit(5);
    }

    private class SettingAdapter extends FragmentStatePagerAdapter {
        public SettingAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MerchantFragment.newInstance();
                case 1:
                    return TerminalInfoFragment.newInstance();
                case 2:
                    if(flag_gr){
                        return QrSettingFragment.newInstance();
                    }else{
                        return ServerInfoFragment.newInstance();
                    }
                case 3:
                    if(flag_gr){
                        if(flag_ali){
                            return AlipaySettingFragment.newInstance();
                        }else{
                           return  ServerInfoFragment.newInstance();
                        }
                    }else{
                        return NMXInfoFragment.newInstance();
                    }
                case 4:
                    if(flag_gr){
                        if(flag_ali){
                            return  ServerInfoFragment.newInstance();
                        }else{
                            return  NMXInfoFragment.newInstance();
                        }
                    }else{
                        return NMXInfoFragment.newInstance();
                    }
                case 5:
                    return NMXInfoFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return cnt;
        }
    }
}
