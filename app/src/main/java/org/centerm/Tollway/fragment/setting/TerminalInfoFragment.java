package org.centerm.Tollway.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.centerm.Tollway.R;
import org.centerm.Tollway.adapter.TerminalAdapter;

@SuppressWarnings("unused")
public class TerminalInfoFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "TerminalInfoFragment";

    private TabLayout tab = null;
    private ViewPager terminalViewPager = null;
    private TerminalAdapter terminalAdapter = null;

    public TerminalInfoFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static TerminalInfoFragment newInstance() {
        TerminalInfoFragment fragment = new TerminalInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_terminal_info, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        tab = rootView.findViewById(R.id.tab);
        terminalViewPager = rootView.findViewById(R.id.terminalViewPager);

        terminalAdapter = new TerminalAdapter(getFragmentManager());
        terminalViewPager.setAdapter(terminalAdapter);
        terminalViewPager.setOffscreenPageLimit(3);
        tab.setupWithViewPager(terminalViewPager);
        terminalAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


}