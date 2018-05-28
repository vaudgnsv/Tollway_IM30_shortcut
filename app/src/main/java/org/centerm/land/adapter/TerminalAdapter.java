package org.centerm.land.adapter;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.centerm.land.fragment.terminal.TerminalEPSFragment;
import org.centerm.land.fragment.terminal.TerminalPOSFragment;
import org.centerm.land.fragment.terminal.TerminalTMSFragment;

public class TerminalAdapter extends FragmentStatePagerAdapter {
    public TerminalAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TerminalPOSFragment.newInstance();
            case 1:
                return TerminalTMSFragment.newInstance();
            case 2:
                return TerminalEPSFragment.newInstance();
            default:
                return TerminalPOSFragment.newInstance();
        }
    }

    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "POS";
            case 1:
                return "TMS";
            case 2:
                return "EPS";
            default:
                return "POS";
        }
    }
}
