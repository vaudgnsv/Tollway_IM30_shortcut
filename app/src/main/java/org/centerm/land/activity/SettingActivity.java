package org.centerm.land.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import org.centerm.land.R;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.fragment.MenuServiceFragment;
import org.centerm.land.fragment.setting.MerchantFragment;
import org.centerm.land.fragment.setting.NMXInfoFragment;
import org.centerm.land.fragment.setting.QrSettingFragment;
import org.centerm.land.fragment.setting.ServerInfoFragment;
import org.centerm.land.fragment.setting.TerminalInfoFragment;

public class SettingActivity extends SettingToolbarActivity {
    private ViewPager settingViewPager = null;
    private SettingAdapter settingAdapter = null;
    private static String typePassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        settingViewPager = findViewById(R.id.settingViewPager);
        settingAdapter = new SettingAdapter(getSupportFragmentManager());
        settingViewPager.setAdapter(settingAdapter);
        settingViewPager.setOffscreenPageLimit(4);
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
                    return QrSettingFragment.newInstance();
                case 3:
                    return ServerInfoFragment.newInstance();
                case 4:
                    return NMXInfoFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
