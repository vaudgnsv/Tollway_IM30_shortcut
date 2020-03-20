package org.centerm.Tollway.adapter;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.centerm.Tollway.fragment.terminal.TerminalEPSFragment;
import org.centerm.Tollway.fragment.terminal.TerminalHealthCareFragment;
import org.centerm.Tollway.fragment.terminal.TerminalPOSFragment;
import org.centerm.Tollway.fragment.terminal.TerminalTMSFragment;

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
            case 3:
                return TerminalHealthCareFragment.newInstance();
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
        return 4;
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
            case 3:
                return "HC";
            default:
                return "POS";
        }
    }
}
