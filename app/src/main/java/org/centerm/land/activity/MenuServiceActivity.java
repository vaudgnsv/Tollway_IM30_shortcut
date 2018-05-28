package org.centerm.land.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.centerm.land.fragment.MenuServiceFragment;
import org.centerm.land.R;
import org.centerm.land.utility.Preference;

public class MenuServiceActivity extends AppCompatActivity {

    private ViewPager viewPagerMenu = null;
    private ScreenPagerAdapter pagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_service);
        viewPagerMenu = findViewById(R.id.viewPagerMenu);
        pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());
        viewPagerMenu.setAdapter(pagerAdapter);
        if (Preference.getInstance(this).getValueString(Preference.KEY_PIN).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_PIN,"1111");
        }
    }

    private class ScreenPagerAdapter extends FragmentStatePagerAdapter {
        public ScreenPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MenuServiceFragment.newInstance();
            }
            return MenuServiceFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
